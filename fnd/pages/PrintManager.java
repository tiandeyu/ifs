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
*  File        : PrintManager.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-02  - Created Using the ASP file PrintManager.asp
*    Kingsly P      2001-03-02  - Upgarde to WK3.5 Environment
*    Jacek P        2001-Mar-29 - Changed .asp to .page
*    Ramila H       2001-Oct-11 - changed code to open ReportPrintDlg in new window.
*    Ramila H       2004-04-08  - changed column names  
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PrintManager extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.PrintManager");


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
   private ASPBlock paramsblk;
   private ASPRowSet paramset;
   private ASPInfoServices inf;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private String n;
   private ASPBuffer r;

   //===============================================================
   // Construction
   //===============================================================
   public PrintManager(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      q   = null;
      n   = null;
      r   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      PrintManager page = (PrintManager)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.q   = null;
      page.n   = null;
      page.r   = null;

      // Cloning immutable attributes
      page.frm = page.getASPForm();
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.paramsblk = page.getASPBlock(paramsblk.getName());
      page.paramset = page.paramsblk.getASPRowSet();
      page.inf = page.getASPInfoServices();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();

   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  remove()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd = null;
      String job_id = null;
      int curRow = 0;

      curRow = headset.getCurrentRowNo();

      if(headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      headset.setRemoved();
      trans.clear();
      if  ( "Remove__".equals(headset.getRowStatus()) )
      {
         job_id = headset.getValue("PRINT_JOB_ID");
         cmd=trans.addCustomCommand("REMOVEJOB"+job_id,"Print_Job_API.Remove");
         cmd.addParameter("PARAM3",job_id);
      }

      mgr.perform(trans);
      headset.goTo(curRow);
      okFind();
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
      n = headset.getRow().getValue("N");
      headlay.setCountValue(toInt(n));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert("FNDPAGESPRINTMANAGERNODATA: No data found.");
      }
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  print()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer reskey = null;
      String result_keys = null;
      String result_key = null;
      String seq_keys = null;
      String rep_id = null;
      String key = null;
      int record_index = 0;
      int field_index = 0;
      boolean hasMoreKeys = true;

      if(headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      rep_id = headset.getValue("PRINT_JOB_ID");
      result_keys = inf.getResultKey(rep_id);
      seq_keys = result_keys;

      while (hasMoreKeys)
      {
         record_index = result_keys.indexOf((char)(30));
         hasMoreKeys = ((record_index + 1) < result_keys.length());
         key = result_keys.substring(0,record_index);
         field_index = key.indexOf((char)(31));
         result_key = key.substring(0,field_index);
         result_keys = result_keys.substring(record_index + 1, result_keys.length());

         paramset.addRow(mgr.newASPBuffer());
         paramset.setValue("RESULT_KEY",result_key);
      }

      reskey = paramset.getRows("RESULT_KEY");
      reskey.addItem("PRINT_JOB", rep_id);
      reskey.addItem("SEQ_DATA", seq_keys);
      paramset.clear();

      //mgr.transferDataTo("ReportPrintDlg.page",reskey);
      callPrintDlg(reskey,true);

      headset.unselectRows();

   }


   public void  restart()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer selected_row = null;
      String job_status = null;
      int curRow = 0;

      headset.store();

      curRow = headset.getCurrentRowNo();
      selected_row = headset.getSelectedRows("PRINT_JOB_ID, STATUS_DB");

      job_status = (selected_row.getBuffer("DATA")).getFieldValue("STATUS_DB");
      if  ( !("WAITING".equals(job_status)) )
         inf.restart((selected_row.getBuffer("DATA")).getFieldValue("PRINT_JOB_ID"));
      else
         mgr.showAlert("FNDPAGESPRINTMANAGERCANOTRESTART: Cannot Restart Print Jobs in status 'Waiting'");

      headset.goTo(curRow);

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
              setHidden();

      headblk.addField("OBJVERSION").
              setHidden();

      headblk.addField("PRINT_JOB_ID","Number","######").
              setSize(10).
              setReadOnly().
              setLabel("FNDPAGESPRINTMANAGERPRINT_KEY: Print Key");

      headblk.addField("STATUS").
              setReadOnly().
              setSize(10).
              setLabel("FNDPAGESPRINTMANAGERSTATUS: Status");

      headblk.addField("REPORT_TITLE").
              setSize(25).
              setReadOnly().
              setLabel("FNDPAGESPRINTMANAGERREPORT_TITLE: Report Title");

      headblk.addField("PRINTER_ID").
              setSize(25).
              setReadOnly().
              setLabel("FNDPAGESPRINTMANAGERPRINTER_ID: Printer Id");

      headblk.addField("EXPIRE_DATE","Date").
              setReadOnly().
              setSize(12).
              setLabel("FNDPAGESPRINTMANAGEREXPIRE_DATE: Expire Date");

      headblk.addField("USER_NAME").
              setHidden();

      headblk.addField("ORDER_BY").
              setSize(20).
              setReadOnly().
              setFunction("Fnd_User_API.Get_Description(:USER_NAME)").
              setLabel("FNDPAGESPRINTMANAGERORDER_BY: Order By");
      
      headblk.addField("SCHEDULE_NAME").
              setSize(20).
              setReadOnly().
              setLabel("FNDPAGESPRINTMANAGERCOMMENT: Schedule Name");


      headblk.addField("SETTINGS").
              setHidden();

      headblk.addField("SCHEDULE_ID","Number","######").
              setReadOnly().
              setSize(20).
              setLabel("FNDPAGESPRINTMANAGERORDER_NO: Schedule Id");

      headblk.addField("SCHEDULE_EXECUTIONS","Number","######").
              setReadOnly().
              setSize(20).
              setLabel("FNDPAGESPRINTMANAGERCOUNTER: Executions");

      headblk.addField("MESSAGE").
              setReadOnly().
              setSize(20).
              setLabel("FNDPAGESPRINTMANAGERERROR_MESSAGE: Error Message");

      headblk.addField("STATUS_DB").
              setReadOnly().
              setSize(20).
              setHidden();

      headblk.setView("PRINT_JOB");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.NEWROW);
      headbar.addCustomCommand("remove",mgr.translate("FNDPAGESPRINTMANAGERDEL: Remove"));
      headbar.addCustomCommand("print", mgr.translate("FNDPAGESPRINTMANAGERPRINT: Print"));
      headbar.addCustomCommand("restart", mgr.translate("FNDPAGESPRINTMANAGERRESTART: Restart"));
      headtbl = mgr.newASPTable(headblk);
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      paramsblk = mgr.newASPBlock("PARAMS");
      paramsblk.addField("PARAM");
      paramsblk.addField("PARAM2");
      paramsblk.addField("PARAM3");
      paramsblk.addField("RESULT_KEY");
      paramsblk.addField("PRINT_JOB");
      paramset = paramsblk.getASPRowSet();

      inf = mgr.newASPInfoServices();
      inf.addFields();

   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESPRINTMANAGERPMANGHE: Print Manager";
   }

   protected String getTitle()
   {
      return "FNDPAGESPRINTMANAGERPMANGHE: Print Manager";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(headlay.show());
   }

}