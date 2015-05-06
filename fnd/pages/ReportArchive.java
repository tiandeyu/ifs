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
*  File        : ReportArchive.java
*  Modified    :
*    ASP2JAVA Tool  2001-Mar-02  - Created Using the ASP file ReportArchive.asp
*    Chaminda O.    2001-Mar-01  - Modifications after converting to Java
*    Jacek P        2001-Mar-29 - Changed .asp to .page
*    Kingsly p.     2001-Jul-03 - Call Id #44217
*    Ramila H.      2001-Jul-05 - Log id 781. Added functionality to 
*                                  show hidden reports.
*    Mangala        2001-Dec-14 - Replace the depricated method archivateIsActiveConfig()
*                                 with archivateIsActive() in preDefine().
*    Rifki R       2002-Apr-08 - Fixed translation problems caused by trailing spaces.
*    Sampath       2002-Nov-18 - Modifications to allow to go to detail mode
*    Sampath       2003-Mar-06 - Log #877 better support for viewing PDF based reports online.
*    Ramila/Chan   2003-Sep-03 - Modified coding for Bayonet.
*    Chandana D    2003-Sep-05 - Opened print Dialog in a new window.
*    Ramila H      2004-04-08  - Changed column names.
*    Suneth M      2004-Aug-04 - Changed duplicate localization tags.
* ----------------------------------------------------------------------------
* New Comments:
*
* 2010/09/15 sumelk Bug 93037, Changed preDefine() to hide View with Excel menu item when layout
*                   type is empty.
* 2009/11/27 sumelk Bug 87417, Changed getPreviewScript() to avoid script errors after translate
*                   the messages to French language.
* 2008/01/29 vohelk
* Bug 69330, Added new option 'View report in Excel', used getPreviewScript() function.
*
* 2006/03/15 mapelk
* Make the feild Created as Datetime
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.2  2005/04/05 05:11:17  sumelk
* Merged the corrections for Bug 50022, Added Result_key to remove method.
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.io.File;

public class ReportArchive extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ReportArchive");

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
   private ASPInfoServices info;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private int i;
   private int remvrows;
   private ASPQuery q;
   private boolean showcheckbox;
   private boolean showhidden;

   //===============================================================
   // Construction
   //===============================================================
   public ReportArchive(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      cmd   = null;
      i   = 0;
      remvrows   = 0;
      q   = null;
      showcheckbox = false;
      showhidden = false;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      ReportArchive page = (ReportArchive)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.cmd   = null;
      page.i   = 0;
      page.remvrows   = 0;
      page.q   = null;
      page.showcheckbox = false;
      page.showhidden = false;

      // Cloning immutable attributes
      page.frm = page.getASPForm();
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.info = page.getASPInfoServices();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      showcheckbox = ctx.readFlag("SHOWCHECKBOX", false);
      showhidden = ctx.readFlag("SHOW_HIDDEN", false);

      if(showcheckbox)
         showhidden = (!mgr.isEmpty(mgr.readValue("SHOW_HIDDEN")));

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("OPENWIN")) )
         preview();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();

      adjust();

      ctx.writeFlag("SHOW_HIDDEN",showhidden);
      ctx.writeFlag("SHOWCHECKBOX",showcheckbox);
      
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","ARCHIVE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      headset.addRow(trans.getBuffer("HEAD/DATA"));
   }


   public void  deleteRow()
   {
      ASPManager mgr = getASPManager();

      headset.store();
      headset.setSelectedRowsRemoved();
      headset.unselectRows();
      i=0;
      remvrows = headset.countRows();
      headset.first();
      trans.clear();
      while ( i<remvrows )
      {
         if ( "Remove__".equals(headset.getRowStatus()) )
         {
            cmd=trans.addCustomCommand("REMOVEINST"+i,"ARCHIVE_API.Remove_User_Instance");
	    cmd.addParameter("f0",headset.getRow().getValue("RESULT_KEY"));
            cmd.addParameter("f0",headset.getRow().getValue("OWNER"));
         }
         headset.next();
         i++;
      }
      mgr.perform(trans);
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
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();

   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      
      mgr.createSearchURL(headblk);

      trans.clear();

      q = trans.addQuery(headblk);

      if ( !showhidden )
         q.addWhereCondition("SHOW_STATE = 'V'");

      q.setOrderByClause("EXEC_TIME DESC");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         headset.clear();
         mgr.showAlert("FNDPAGESREPORTARCHIVENODATA: No data found.");
      }
      else if ( headset.countRows() < headset.countDbRows() )
      {
         mgr.showAlert(mgr.translate("FNDPAGESREPORTARCHIVEMOREDATA: You have fetched the first &1 rows out of &2", String.valueOf(headset.countRows()),String.valueOf(headset.countDbRows())));
      }
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  print()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer keys = null;

      headset.store();
      keys = headset.getSelectedRows("RESULT_KEY");
      headset.selectRows();

      if ( keys.countItems() == 0 )
         mgr.showAlert("FNDPAGESREPORTARCHIVENOSELDATA: No rows has been selected.");
      else
         callPrintDlg(keys,true); 
         //mgr.transferDataTo("ReportPrintDlg.page",keys);
   }


   public void  view()
   {
      ASPManager mgr = getASPManager();
      String key = null;

      headset.store();

      headset.setFilterOn();
      if ( headset.countSelectedRows() == 0 )
      {
         mgr.showAlert("FNDPAGESREPORTARCHIVENOSELDATA: No rows has been selected.");
      }
      else if ( headset.countSelectedRows() == 1 )
      {
         key= headset.getRow().getValue("RESULT_KEY");
         if ( info.isReportAvailableAsPdf( key ) )
         {
            info.sendReportAsPdf( key );
         }
         else
         {
            info.convertReportToPdf( key );
            mgr.showAlert("FNDPAGESREPORTARCHIVEPDFCONVERT: A job was created that will convert the report to PDF. Redo the action in about a minute.");
         }
      }
      else
      {
         mgr.showAlert("FNDPAGESREPORTARCHIVEMANYROWSVIEW: Only one row may be selected for operation View...");
      }
      headset.setFilterOff();
   }

   public void populate()
   {
      okFind();
   }
   
   public void  none()
   {
      ASPManager mgr = getASPManager();
      mgr.showAlert("FNDPAGESREPORTARCHIVENORMBMETHOD: No RMB method is selected.");
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("RESULT_KEY","Number","######").
      setSize(10).
      setMandatory().
      setReadOnly().
      setInsertable().
      setLabel("FNDPAGESREPORTARCHIVEREPORTKEY: Report Key");

      headblk.addField("PRINTED","Number").
      setSize(10).
      setReadOnly().
      setCheckBox("0,1").
      setLabel("FNDPAGESREPORTARCHIVEPRINTED: Printed");

      headblk.addField("REPORT_TITLE").
      setSize(30).
      setReadOnly().
      setLabel("FNDPAGESREPORTARCHIVEREPORTTITLE: Report Title");

      headblk.addField("EXEC_TIME", "Datetime").
      setSize(20).
      setMandatory().
      setLabel("FNDPAGESREPORTARCHIVECREATED: Created");

      headblk.addField("EXPIRE_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("FNDPAGESREPORTARCHIVEEXPIREDATE: Expire Date");

      headblk.addField("NOTES").
      setSize(10).
      setMandatory().
      setLabel("FNDPAGESREPORTARCHIVENOTES: Notes");

      headblk.addField("OWNER").
      setHidden();
      
      headblk.addField("OWNER_DESC").
      setSize(20).
      setFunction("Fnd_User_API.Get_Description(:OWNER)").
      setLabel("FNDPAGESREPORTARCHIVEOWNER: Owner");

      headblk.addField("SENDER").
      setHidden();

      headblk.addField("SENDER_DESC").
      setSize(20).
      setFunction("Fnd_User_API.Get_Description(:SENDER)").
      setLabel("FNDPAGESREPORTARCHIVEORDEREDBY: Ordered By");
      
      headblk.addField("SCHEDULE_NAME").
      setSize(20).
      setMandatory().
      setLabel("FNDPAGESREPORTARCHIVECOMMENT: Schedule Name");

      headblk.addField("SCHEDULE_ID","Number").
      setSize(7).
      setMandatory().
      setLabel("FNDPAGESREPORTARCHIVEORDERNO: Schedule Id");

      headblk.addField("SCHEDULE_EXECUTIONS","Number").
      setSize(10).
      setMandatory().
      setLabel("FNDPAGESREPORTARCHIVECOUNTER: Execution");

      headblk.addField("REPORT_ID").
      setHidden();

      headblk.addField("LAYOUT_NAME").
      setHidden();
      
      headblk.addField("LANG_CODE").
      setHidden();

      headblk.addField("SHOW_STATE").
      setHidden();
      
      headblk.addField("LAYOUT_TYPE").
              setHidden().
              setFunction("Report_Layout_Type_API.Encode(Report_Layout_Definition_API.Get_Layout_Type(:REPORT_ID,:LAYOUT_NAME))");
      
      headblk.setView("ARCHIVE");
      headblk.defineCommand("ARCHIVE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.DELETE,"deleteRow");

      headbar.addCustomCommand("print", mgr.translate("FNDPAGESREPORTARCHIVEPRINT: Print"));
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      //headbar.disableCommand(headbar.VIEWDETAILS);
      headbar.addCustomCommand("populate","POP");
      headbar.disableCustomCommand("populate");
      headbar.disableCommand(headbar.DUPLICATEROW);

      info  = mgr.newASPInfoServices();
      info.addFields();

      if ( info.archivateIsActive())
      {
         headbar.addCustomLinkCommand("VIEW","FNDVIEWREPORTSCMDTITLE: View","ViewReports.page","RESULT_KEY,REPORT_ID",true);
         headbar.addCustomCommand("VIEWINEXCEL", mgr.translate("VIEWINEXCEL: View with Excel"));
         headbar.defineCommand("VIEWINEXCEL","","createReport");
         
         headbar.addCommandValidConditions("VIEW","LAYOUT_TYPE","Disable","EXCEL");
         headbar.addCommandValidConditions("VIEWINEXCEL","LAYOUT_TYPE","Disable","BUILDER;CRYSTAL;DESIGNER;STREAMSERVE");
         headbar.addCommandValidConditions("VIEWINEXCEL","LAYOUT_TYPE","Disable","");
      }

      headtbl = mgr.newASPTable(headblk);

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      this.enableConvertGettoPost();
   }
   
   
   private String getPreviewScript()
   {
      AutoString script = new AutoString();
      ASPManager mgr = getASPManager();
      String path = mgr.getPath();
      String time_out = mgr.getConfigParameter("APPLICATION/PDF_PREVIEW_TIME_OUT","25");

      if (path.indexOf("?")>0)
         path += path.endsWith("?")?"":"&";
      else
         path += "?";

      
      script.append("function createReport()\n{\n");
      if (headset.countRows()>0)
      {
         if(headlay.isSingleLayout())
         {
            script.append("result_key ='" + headset.getNumberValue("RESULT_KEY")+"';\n"); 
            script.append("layout ='" + headset.getValue("LAYOUT_NAME")+"';\n"); 
            script.append("lang_code ='" + headset.getValue("LANG_CODE")+"';\n"); 
            script.append("report_id ='" + headset.getValue("REPORT_ID")+"';\n");
         }
         else
            if(headlay.isMultirowLayout())
            {
               script.append("var result_key_arr = new Array("+headset.countRows()+");\n");
               script.append("var layout_arr = new Array("+headset.countRows()+");\n");
               script.append("var lang_code_arr = new Array("+headset.countRows()+");\n");
               script.append("var report_id_arr = new Array("+headset.countRows()+");\n");
               for(i=0;i<headset.countRows();i++)
               {
                  script.append("\t result_key_arr["+i+"] ='" + headset.getValueAt(i,"RESULT_KEY")+"';\n");
                  script.append("\t layout_arr["+i+"] ='" + headset.getValueAt(i,"LAYOUT_NAME")+"';\n");
                  script.append("\t lang_code_arr["+i+"] ='" + headset.getValueAt(i,"LANG_CODE")+"';\n");
                  script.append("\t report_id_arr["+i+"] ='" + headset.getValueAt(i,"REPORT_ID")+"';\n");
               }
               script.append("result_key = result_key_arr[tblRow1];\n");
               script.append("layout = layout_arr[tblRow1];\n");
               script.append("lang_code = lang_code_arr[tblRow1];\n");
               script.append("report_id = report_id_arr[tblRow1];\n");
            }
      }
      else
      {
         script.append("result_key='';\n");
         script.append("layout='';\n");
         script.append("lang_code='';\n");
         script.append("report_id='';\n");
      } 

      script.append("   window.status='", mgr.translateJavaScript("FNDREPORTARCHIVEPRVRPT: Previewing report. Please wait.."),"';\n");

      script.append("   setTimeout('previewReport('+ result_key + ',\"' + layout + '\",\"' + lang_code +'\",0)',2);\n return false;\n}\n\n");
      
      script.append("function previewReport(result_key, layout, lang_code,timeout)\n{\n");
      script.append("   window.status = window.status + '.';\n");
      script.append("   if (timeout*2>",time_out,")\n   {\n");
      String time_out_msg = mgr.translateJavaScript("FNDREPORTARCHIVETIMEOUT: Operation timed out. Report can not be previewed.");
      script.append("      alert('", time_out_msg,"');\n");
      script.append("      window.status='", time_out_msg,"';\n");

      script.append("      return;\n   }\n");
      script.append("   timeout++;\n");
      script.append("      showNewBrowser('",path,"OPENWIN=Y&PREVIEW=SHOW&RESULT_KEY='+URLClientEncode(result_key)+'&LAYOUT='+URLClientEncode(layout)+'&LANG_CODE='+URLClientEncode(lang_code)+'&EXCEL_REPORT=TRUE');\n");
      script.append("}\n\n");
      return script.toString();
   }
   
   
   public void  preview()
   {
      ASPManager mgr       = getASPManager();
      String val           = mgr.readValue("PREVIEW");
      String lang_code     = mgr.readValue("LANG_CODE");
      String layout        = mgr.readValue("LAYOUT");
      String result_key    = mgr.readValue("RESULT_KEY");
      String report_id     = mgr.readValue("REPORT_ID");
      
      if (  "SHOW".equals(val) )
      {
         info.sendPDFContents(result_key, layout, lang_code );
      }
      
      mgr.endResponse();
   }

   public void adjust()
   {
      // to hide checkbox in intro page
      showcheckbox = true;
      if ( headset.countRows()== 0 )
         showcheckbox = false;

      if ( headlay.isFindLayout() )
         showcheckbox = true;
      else if (headlay.isSingleLayout())
         showcheckbox = false; 
      
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESREPORTARCHIVEWINDOWTITLE: Report Archive";
   }

   protected String getTitle()
   {
      return "FNDPAGESREPORTARCHIVEWINDOWTITLE: Report Archive";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      beginDataPresentation();
      if (showcheckbox)
      { 
         printWriteLabel("SHOWHIDDE: Show Hidden Reports");
         if (headlay.isMultirowLayout())
            printCheckBox("SHOW_HIDDEN", "1", showhidden, "onClick=\"javascript:commandSet('HEAD.populate','')\"");
         else 
            printCheckBox("SHOW_HIDDEN", "1", showhidden, "");
      }
      endDataPresentation(false);
      if (headlay.isVisible())
        appendToHTML(headlay.show());
      
      try
      {
         appendDirtyJavaScript(getPreviewScript());
      }
      catch(FndException e)
      {
         error(e);
      }
       
   }
}
