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
*  File        : ViewReports.java
*  Modified    :
*    Chandana D  2003-09-03  - Created
*    Ramila H    2003-09-30  - checked if PDF available before calling createPdfReport.
*    Chandana D  2003-10-10  - imp code for force rendering.
*    Chandana D  2004-May-12 - Updated for the use of new style sheets.
*    Chandana D  2004-Jun-10 - Removed all absolute URLs.
*    Ramila H    2004-10-20  - Merged Bug 46730, implementation code.
* ----------------------------------------------------------------------------
* New Comments:
* 2006/08/10 buhilk Bug 59442, Corrected Translatins in Javascript
*
* 2006/07/03 buhilk Bug 58216, Fixed SQL Injection threats
*
*               2005/01/02 prralk
* Converts GET to POST to fix preview in PDF issue.
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.3  2005/04/08 06:05:38  riralk
* Merged changes made to PKG12 back to HEAD
*
* Revision 1.2  2005/02/02 08:22:19  riralk
* Adapted global profile functionality to new profile changes
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ViewReports extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pages.ViewReports");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPLog log;
   private ASPBlock blk;
   private ASPBlock tmpblk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPInfoServices info;
   private ASPHTMLFormatter fmt;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;
   
   private boolean other_layout;   
   private String language;
   private String languages;
   private String layout;
   private String layouts;
   
   //===============================================================
   // Construction
   //===============================================================
   public ViewReports(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      trans   = null;
      q   = null;
      super.doReset();
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      fmt = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      other_layout = false;       
      
      if(mgr.commandLinkActivated())
         listOtherLayouts();
      else if(!mgr.isEmpty(mgr.readValue("REPORT_STATE")))
         showPDF();
      else if(!mgr.isEmpty(mgr.readValue("RESULT_KEY")))
         listReports();  
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   private void listOtherLayouts()
   {
       ASPManager mgr = getASPManager();
       other_layout = true; 
       
       rowset.clear();
       trans.clear();
       
       cmd = trans.addCustomCommand("GCLAS", "Report_Definition_API.Get_Class_Info");
       cmd.addParameter("REPORT_ATTR_");
       cmd.addParameter("COLUMN_PROPERTIES_");
       cmd.addParameter("TEXT_PROPERTIES_");
       cmd.addParameter("LAYOUT_PROPERTIES_");
       cmd.addParameter("LANG_PROPERTIES_");
       cmd.addParameter("REPORT_ID", mgr.readValue("REP_ID"));

       trans = mgr.perform(trans);

       languages = info.getLanguageValues(trans.getValue("GCLAS/DATA/LANG_PROPERTIES_"));
       layouts = info.getLayoutValues(trans.getValue("GCLAS/DATA/LAYOUT_PROPERTIES_"));      

       layouts = info.getOptions(info.getTokenAt(layouts, 2), info.getTokenAt(layouts, 1),layout);
       language = mgr.isEmpty(language)?readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false):language;
       languages = info.getOptions(info.getTokenAt(languages, 2), info.getTokenAt(languages, 1),language);      
   }
   
   private void showPDF()
   {
        ASPManager mgr = getASPManager();
        String result_key = mgr.readValue("RESULT_KEY");
        String language_code = mgr.readValue("LANG_CODE");
        String layout_name = mgr.readValue("LAYOUT_NAME");
        String new_archive = mgr.isEmpty(mgr.readValue("NEW_ARCHIVE"))?"":mgr.readValue("NEW_ARCHIVE");
        String report_id = mgr.readValue("REPORT_ID");
        String force_render = mgr.readValue("FORCE_RENDER");
        
        //Bug 46730, start
        String pdf_id = mgr.readValue("PDF_ID");  // used by print agent
               
        if (!mgr.isEmpty(pdf_id))
        {
           info.sendPDFContents("ID=?","ID^S^IN^"+pdf_id);
        }
        //Bug 46730, end
        else if("READY".equals(mgr.readValue("REPORT_STATE")))
        {
            if("TRUE".equals(new_archive))
            {
               //if u chose cancel to force render then print_job_id will be null
               if (!mgr.isEmpty(mgr.readValue("PRINT_JOB_ID")))
                  info.sendPDFContents("PRINT_JOB_ID=?","PRINT_JOB_ID^S^IN^"+mgr.readValue("PRINT_JOB_ID"));
               else
                  info.sendPDFContents(result_key,layout_name,language_code.toLowerCase());
            }
            else
               info.sendReportAsPdf(result_key,language_code.toLowerCase());                   
        }
        else if ("CREATE".equals(mgr.readValue("REPORT_STATE")))
        {
            String owner = "";
            String report_available = "";
            if("TRUE".equals(new_archive))
               owner = "AGENT";
            else if ("FALSE".equals(new_archive))   
               owner = "PRINTSRV";
            else    
               owner = info.getLayoutTypeOwner(report_id,layout_name);

            String print_job_id = "";
            
            if ("PRINTSRV".equals(owner) && !info.isReportAvailableAsPdf(result_key,language_code.toLowerCase()))
            {
               report_available = "N"; 
               info.createPdfReport(result_key,layout_name,language_code.toLowerCase());
            }   
            else if ("AGENT".equals(owner) && !info.isPDFContentAvailable(result_key,layout_name,language_code.toLowerCase()))
            {
               report_available = "N"; 
               print_job_id = info.createPdfReport(result_key,layout_name,language_code.toLowerCase());
            }  
            else
                report_available = "Y"; 
            
            mgr.responseWrite("CREATING^"+owner+"^"+report_available+"^"+print_job_id+"^"); 
        }      
        else if ("TRUE".equals(force_render))
        {
           String print_job_id = "";
           
           if ("FALSE".equals(new_archive))
              info.createPdfReport(result_key,layout_name,language_code.toLowerCase());
           else if ("TRUE".equals(new_archive))
               print_job_id = info.createPdfReport(result_key,layout_name,language_code.toLowerCase());
           
           //Bug 46730 
           mgr.responseWrite("0^1^2^"+print_job_id+"^");   //__getValidateValue(3) to match with above
        }    
        else if ("TRUE".equals(new_archive))
        {
           String print_job_id = mgr.readValue("PRINT_JOB_ID");
           //if u chose cancel to force render then print_job_id will be null
           if (!mgr.isEmpty(print_job_id))
           {
              if (!mgr.isEmpty(info.getPDFContents("PRINT_JOB_ID=?","PRINT_JOB_ID^S^IN^"+mgr.readValue("PRINT_JOB_ID"))))   
                 mgr.responseWrite("READY^AGENT^^"+print_job_id+"^"); 
              else
                 mgr.responseWrite("^^^"+print_job_id+"^"); 
           }
           else
           {
              if(info.isPDFContentAvailable(result_key,layout_name,language_code.toLowerCase()))
                 mgr.responseWrite("READY^AGENT^^^"); 
           }
        }
        else if("FALSE".equals(new_archive))
        {
            if(info.isReportAvailableAsPdf(result_key,language_code.toLowerCase()))
               mgr.responseWrite("READY^PRINTSRV^^^"); 
        }
        
        mgr.endResponse();
   }

   private void listReports()
   {
      ASPManager mgr = getASPManager();
      String result_key = mgr.readValue("RESULT_KEY");
      String report_id = mgr.readValue("REPORT_ID");
      
      q = trans.addQuery(blk);
      q.addWhereCondition("RESULT_KEY=?");
      q.addParameter("RESULT_KEY",result_key);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,blk);
      if (  rowset.countRows() == 0 )
      {
         mgr.showAlert("FNDVIEWREPORTSNODATA: No data found.");
         rowset.clear();
      }
            
      for(int i = 0; i < rowset.countRows(); i++)
         rowset.setValueAt(i, "LAYOUT", info.getLayoutTitle(report_id,rowset.getValueAt(i,"LAYOUT_NAME")));
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
           
      tmpblk = mgr.newASPBlock("HEAD");
      tmpblk.addField("REPORT_ID");
      tmpblk.addField("REPORT_ATTR_");
      tmpblk.addField("COLUMN_PROPERTIES_");
      tmpblk.addField("TEXT_PROPERTIES_");
      tmpblk.addField("LAYOUT_PROPERTIES_");
      tmpblk.addField("LANG_PROPERTIES_");
      tmpblk.addField("CLIENT_VALUES0");
      tmpblk.addField("CLIENT_VALUES1");

      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("KEY_FIELD").
      setHidden().
      setFunction("RESULT_KEY||'^'||LANG_CODE||'^'||NEW_ARCHIVE||'^'||LAYOUT_NAME||'^'||PDF_ID");
      
      blk.addField("RESULT_KEY", "Number", "######")
      .setLabel("FNDVIEWREPTSRESULTKEYFIELD: Report Key");
      
      blk.addField("LAYOUT_NAME").
      setHidden().
      setLabel("FNDVIEWREPTSLAYNAMEFIELD: Layout Name");
                 
      blk.addField("LAYOUT").
      setFunction("''").
      setLabel("FNDVIEWREPTSLAYOUTFIELD: Layout");
      
      blk.addField("LANG_CODE").
      setLabel("FNDVIEWREPTSLANGUAGEFIELD: Language Code");
      
      blk.addField("NEW_ARCHIVE").
      setHidden().
      setLabel("FNDVIEWREPTSLNEWARCIELD: New Archive");
      
      blk.addField("PDF_ID").
      setHidden();
      
      blk.addField("PDF_FILE_NAME").
      setHidden().
      setLabel("FNDVIEWREPTSFILENAMFIELD: File Name");
      
      blk.addField("DATE_CREATED").
      setLabel("FNDVIEWREPTSDATECRTDFIELD: Date Created");
            
      blk.setTitle("FNDVIEWREPORTSTITLE: View Reports");
      blk.setView("FND_COMBINED_PDF_ARCHIVE");

      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      tbl = mgr.newASPTable(blk);
      tbl.setKey("KEY_FIELD");
      tbl.disableEditProperties();
      tbl.unsetSortable();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      
      info  = mgr.newASPInfoServices();
      info.addFields();
      
      disableBar();
      disableHomeIcon();
      disableNavigate();
      disableOptions();
      
      enableConvertGettoPost();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDVIEWREPORTSTITLE: View Reports";
   }

   protected String getTitle()
   {
      return "FNDVIEWREPORTSTITLE: View Reports";
   }

   protected void printContents() throws FndException
   {
       ASPManager mgr = getASPManager();
       boolean is_explorer = mgr.isExplorer();
       
       String timeout_msg = mgr.translateJavaScript("FNDVIEWREPTSTIMOUTMSG: Preview operation timed out. Try again ?");
       String preview_msg = mgr.translateJavaScript("FNDVIEWREPTSPREVIEWMSG: Previewing report. Please wait");
       String available_msg = mgr.translateJavaScript("FNDVIEWREPTSAVAILMSG: Report with the same Layout/Language is already avaialble. Render a new one ?");
       
       
       String timeout = mgr.getConfigParameter("APPLICATION/PDF_PREVIEW_TIME_OUT","20");
       String path = mgr.getPath();
       
       printHiddenField("REP_STAT","");
       printHiddenField("LAN_CODE","");
       printHiddenField("LAY_NAME","");
       printHiddenField("FORCE_RENDER","");
       printHiddenField("PRINT_JOB_ID","");
       printHiddenField("NEW_ARCH",mgr.readValue("NEW_ARCHIVE"));
       printHiddenField("RES_KEY",mgr.readValue("RESULT_KEY"));
             
       printNewLine();
                    
       if(other_layout)
       {   
           printHiddenField("REP_ID",mgr.readValue("REP_ID"));
           beginDataPresentation();
           drawSimpleCommandBar(mgr.translate("FNDWIEWREPORTSOTHERLAYDLG: Select Layout and Language"));
           appendToHTML("<table border=0 class=\"pageForm\" width=100%>\n");
           appendToHTML("<tr>\n");
           appendToHTML("<td nowrap colspan=2>");
           appendToHTML("&nbsp;</td>");
           appendToHTML("</tr>\n");
           appendToHTML("<tr>\n");
           appendToHTML("<td nowrap>");
           printText("FNDPAGESVIEWREPOTSLAYOUT: Layout");
           appendToHTML(":</td>\n");
           appendToHTML(" <td nowrap>");
           appendToHTML(fmt.drawPreparedSelect("LAYOUTS",layouts,"tabindex=3",false));
           appendToHTML(" </td>\n");
           appendToHTML("</tr>\n");
           appendToHTML("<tr>\n");
           appendToHTML(" <td nowrap>");
           printText("FNDPAGESVIEWREPOTSLANGS: Language:");
           appendToHTML(" </td>\n");
           appendToHTML(" <td nowrap>");
           appendToHTML(fmt.drawPreparedSelect("LANGUAGES",languages,"tabindex=4",false));
           appendToHTML(" </td>\n");
           appendToHTML("</tr>\n");
           appendToHTML("<tr>\n");
           appendToHTML(" <td nowrap colspan=2>&nbsp;</td>");
           appendToHTML("</tr>\n");
           appendToHTML("<tr>\n");
           appendToHTML("<td width=100% colspan=2 align=left>"); 
           printButton("VIEW","FNDVIEWREPORTVIEWBTN: View","onClick=\"javascript:viewNewLayout();\"");
           appendToHTML("&nbsp;&nbsp;"); 
           printButton("CANCEL","FNDVIEWREPORTCANCELBTN: Cancel","onClick=\"javascript:cancel()\"");
           appendToHTML("</td></tr>"); 
           appendToHTML("</table>\n");
           printNewLine();
           endDataPresentation(false);
           
           appendDirtyJavaScript("function viewNewLayout(timeout,state){\n");
           appendDirtyJavaScript("   document.form.LAY_NAME.value=document.form.LAYOUTS.options[document.form.LAYOUTS.selectedIndex].value;\n");
           appendDirtyJavaScript("   document.form.LAN_CODE.value=document.form.LANGUAGES.options[document.form.LANGUAGES.selectedIndex].value;\n");
           appendDirtyJavaScript("   window.status='",preview_msg,"...';\n");
           appendDirtyJavaScript("   document.form.NEW_ARCH.value = '';\n");           
           appendDirtyJavaScript("   showPDF(1000,'CREATE');\n");
           appendDirtyJavaScript("}\n");

           appendDirtyJavaScript("function cancel(){\n");
           appendDirtyJavaScript("   window.history.back();\n");
           appendDirtyJavaScript("}\n");
       }
       else
       {
           printHiddenField("REP_ID",mgr.readValue("REPORT_ID"));
           
           beginDataPresentation();
           drawSimpleCommandBar(mgr.translate("FNDWIEWREPORTSLISTAVAILRPTS: Archived Layouts"));
           endDataPresentation(false);
           
           appendToHTML(tbl.populateLov());
           
           beginDataPresentation();
           printNewLine();
           printCommandLink("OTHERLAY","FNDVIEWREPORSOTHERLAYOUT: Other Layout");
           endDataPresentation(false);

           appendDirtyJavaScript("function setValue(val){\n");
           appendDirtyJavaScript("   document.form.RES_KEY.value=val.substring(0,val.indexOf('^'));\n");
           appendDirtyJavaScript("   val = val.substring(val.indexOf('^')+1,val.length);\n");
           appendDirtyJavaScript("   document.form.LAN_CODE.value=val.substring(0,val.indexOf('^'));\n");
           appendDirtyJavaScript("   val = val.substring(val.indexOf('^')+1,val.length);\n");
           appendDirtyJavaScript("   document.form.NEW_ARCH.value=val.substring(0,val.indexOf('^'));\n");
           //Bug 46730, start
           appendDirtyJavaScript("   val = val.substring(val.indexOf('^')+1,val.length);\n");
           appendDirtyJavaScript("   document.form.LAY_NAME.value=val.substring(0,val.indexOf('^'));\n");
           appendDirtyJavaScript("   document.form.PDF_ID.value=val.substring(val.indexOf('^')+1,val.length);\n");
           appendDirtyJavaScript("   window.status='",preview_msg,"...';\n");
           appendDirtyJavaScript("   if (document.form.PDF_ID.value !=''){\n");
           //appendDirtyJavaScript("      showNewBrowser('",path,"?VALIDATE=Y&REPORT_STATE=SHOW");
           appendDirtyJavaScript("      showNewBrowser('",path,"?REPORT_STATE=SHOW");
           appendDirtyJavaScript("&PDF_ID=' + URLClientEncode(document.form.PDF_ID.value));\n");                  
           appendDirtyJavaScript("   }\n");
           appendDirtyJavaScript("   else\n");
           appendDirtyJavaScript("     showPDF(1000,'CREATE');\n");
           //Bug 46730, end
           appendDirtyJavaScript("}\n");
       }
       
       appendDirtyJavaScript("function showPDF(timeout,state){\n");
       appendDirtyJavaScript("window.status=window.status+'.';\n");
       appendDirtyJavaScript("   r = __connect('", path, "?VALIDATE=Y&");
       appendDirtyJavaScript("RESULT_KEY=' + URLClientEncode(document.form.RES_KEY.value)+'&");
       appendDirtyJavaScript("NEW_ARCHIVE=' + URLClientEncode(document.form.NEW_ARCH.value)+'&");           
       appendDirtyJavaScript("REPORT_STATE=' +state+'&");           
       appendDirtyJavaScript("REPORT_ID=' + URLClientEncode(document.form.REP_ID.value)+'&");           
       appendDirtyJavaScript("LAYOUT_NAME=' + URLClientEncode(document.form.LAY_NAME.value)+'&");     
       appendDirtyJavaScript("PRINT_JOB_ID=' + URLClientEncode(document.form.PRINT_JOB_ID.value)+'&");            
       appendDirtyJavaScript("FORCE_RENDER=' + URLClientEncode(document.form.FORCE_RENDER.value)+'&");           
       appendDirtyJavaScript("LANG_CODE=' + URLClientEncode(document.form.LAN_CODE.value)");   
       appendDirtyJavaScript(");\n");
       appendDirtyJavaScript("   if(__getValidateValue(0)=='CREATING'){\n");
       appendDirtyJavaScript("     document.form.PRINT_JOB_ID.value=__getValidateValue(3);\n");
       if (other_layout)
       {
          appendDirtyJavaScript("      if(__getValidateValue(2)=='Y'){\n");
          appendDirtyJavaScript("         if(confirm('",available_msg,"'))\n");
          appendDirtyJavaScript("            document.form.FORCE_RENDER.value='TRUE'; \n");
          appendDirtyJavaScript("         else\n");
          appendDirtyJavaScript("            document.form.FORCE_RENDER.value='FALSE'; \n");
          appendDirtyJavaScript("      }\n");
       }
       
       appendDirtyJavaScript("      if(__getValidateValue(1)=='AGENT')\n");
       appendDirtyJavaScript("          document.form.NEW_ARCH.value='TRUE';\n");
       appendDirtyJavaScript("       else\n");
       appendDirtyJavaScript("          document.form.NEW_ARCH.value='FALSE';\n");
       appendDirtyJavaScript("   }\n");
       appendDirtyJavaScript("      else\n");
       appendDirtyJavaScript("         document.form.FORCE_RENDER.value='FALSE'; \n");
       appendDirtyJavaScript("   if(__getValidateValue(0)=='READY'){\n");
       //appendDirtyJavaScript("      showNewBrowser('",path,"?VALIDATE=Y&");
       appendDirtyJavaScript("      showNewBrowser('",path,"?");
       appendDirtyJavaScript("RESULT_KEY=' + URLClientEncode(document.form.RES_KEY.value)+'&");
       appendDirtyJavaScript("NEW_ARCHIVE=' + URLClientEncode(document.form.NEW_ARCH.value)+'&");           
       appendDirtyJavaScript("REPORT_STATE=READY&");           
       appendDirtyJavaScript("REPORT_ID=' + URLClientEncode(document.form.REP_ID.value)+'&");           
       appendDirtyJavaScript("LAYOUT_NAME=' + URLClientEncode(document.form.LAY_NAME.value)+'&");           
       appendDirtyJavaScript("PRINT_JOB_ID=' + URLClientEncode(document.form.PRINT_JOB_ID.value)+'&");                  
       appendDirtyJavaScript("LANG_CODE=' + URLClientEncode(document.form.LAN_CODE.value)");   
       appendDirtyJavaScript(");\n");
       appendDirtyJavaScript("window.status='';\n");
       appendDirtyJavaScript("   }\n");
       appendDirtyJavaScript("   else{\n");
       appendDirtyJavaScript("      document.form.PRINT_JOB_ID.value=__getValidateValue(3);\n");
       appendDirtyJavaScript("      timeout+=1000;\n");
       appendDirtyJavaScript("      if(timeout > "+timeout+"*1000){\n");
       appendDirtyJavaScript("         if(confirm('",timeout_msg,"')){\n");
       appendDirtyJavaScript("            timeout=1000;\n"); 
       appendDirtyJavaScript("            window.status='",preview_msg,"...';\n");
       appendDirtyJavaScript("         }\n");
       appendDirtyJavaScript("         else{\n");
       appendDirtyJavaScript("            window.status='';\n");
       appendDirtyJavaScript("            return;\n");
       appendDirtyJavaScript("         }\n");
       appendDirtyJavaScript("       }\n");
       appendDirtyJavaScript("       setTimeout('showPDF('+timeout+',\"CREATINGX\")',1000);\n");
       appendDirtyJavaScript("   }\n");
       appendDirtyJavaScript("}\n");
   }
}

