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
 *  File        : ScheduledTaskHeader.java 
 *  Modified    :
 *    rahelk  2004-05-18 created.
 *    rahelk  2004-06-04  - Implemented PARAMETER Table functionality
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision x.x  2008/08/18 buhilk
 * Bug id 76424, Modified printContents to resize table for scheduled task header.
 *
 * Revision x.x  2007/02/15 10:08:34  sadhlk
 * Bug Id 63229, Changed printContents()
 * Revision 1.2  2005/10/10 09:39:00  rahelk
 * Added checking parameter values using validation_method
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/08 09:44:06  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.2  2005/02/08 08:12:55  rahelk
 * Merged Call id 121571. Reimplemented scheduled reports to be similar to general tasks.
 *
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.Message;

public class ScheduledTaskHeader extends ASPPageProvider
{
   private ASPBlock blk;
   private ASPRowSet rowset;
   
   public ScheduledTaskHeader(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      
      if( !mgr.isEmpty(mgr.getQueryStringValue("SCHEDULE_ID")) )
         okFind();

   }
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(blk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,blk);

      if (  rowset.countRows() == 0 )
         rowset.clear();
   }
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");
      blk.addField("OBJID").
          setHidden();

      blk.addField("OBJVERSION").
          setHidden();
      
      blk.addField("SCHEDULE_ID");
      blk.addField("SCHEDULE_NAME");
      
      blk.addField("TASK_DESCRIPTION").
          setFunction("BATCH_SCHEDULE_METHOD_API.Get_Description(:SCHEDULE_METHOD_ID)");
      
      blk.addField("USERNAME");
      blk.addField("MODIFIED_DATE");
      blk.addField("LANG_CODE");
      
      blk.addField("LANG_DESC").
          setFunction("LANGUAGE_CODE_API.Get_Description(:LANG_CODE)");
      
      blk.addField("EXECUTIONS");
      blk.addField("USER_DESC").
          setFunction("FND_USER_API.Get_Description(:USERNAME)");
      
      blk.addField("NEXT_EXECUTION_DATE", "Datetime");
      blk.addField("START_DATE", "Datetime");
      blk.addField("STOP_DATE", "Datetime");
      blk.addField("ACTIVE_DB");
      blk.addField("EXECUTION_PLAN");
      blk.addField("SCHEDULE_METHOD_ID");
      
      blk.addField("ARGUMENT_TYPE_DB").
          setFunction("Argument_Type_API.Encode(batch_schedule_method_api.Get_Argument_Type(:SCHEDULE_METHOD_ID))");
      
      blk.addField("METHOD").
          setFunction("batch_schedule_method_api.Get_Method_Name(:SCHEDULE_METHOD_ID)");

      blk.addField("VALIDATION_METHOD").
          setFunction("batch_schedule_method_api.Get_Validation_Method__(:SCHEDULE_METHOD_ID)");
      
      blk.addField("INSTALLATION_ID");
      blk.addField("EXTERNAL_ID");
      
      blk.setView("BATCH_SCHEDULE");
      
      rowset = blk.getASPRowSet();
      
      disableHeader();
      disableFooter();
      disableHomeIcon();
      disableNavigate();
      disableOptions();
      disableHelp();
   }   
   
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return getTitle();
   }

   protected String getTitle()
   {
      return "FNDSCHEDTASKHEADERTITLE: Scheduled Tasks";
   }

   //Remove for treelist embedded iframe sol
   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      //out.append(mgr.startPresentation(getTitle()));

      printContents();

      out.append(mgr.endPresentation());

      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");

      return out;
   }
   
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      String image_location = mgr.getASPConfig().getImagesLocation();
      
      int rows = rowset.countRows();
      
/*     
      beginDataPresentation();
      appendToHTML("<table border=0 width=100% cellspacing=0 cellpadding=0 >\n");
      appendToHTML("<tr><td colspan=2 >\n");
      drawSimpleCommandBar(mgr.translate(getTitle()));
      appendToHTML("</td></tr>\n");
      
      appendToHTML("<tr>");
      //iframe for TreeList
      appendToHTML("<td valign=top width='25%'>");
      appendToHTML("  <iframe height=400 name=\"navigator\" id=\"navigator\" src = \"ScheduledTaskTreeList.page"+rowset.getValue("METHOD"));
		appendToHTML("\" scrolling=\"auto\"\n");
		appendToHTML("  frameborder=\"0\" class=borders >\n");
      appendToHTML(mgr.translate("FNDSCHEDULETASKNOIFRAME: This page uses iframes, but your browser doesn't support them."));
      appendToHTML("  </iframe>\n");
      appendToHTML("</td>");

      //detail part
      appendToHTML("<td valign=top width=75% height=100%>");
 */
      appendToHTML("<table border=0 class='pageFormWithBorder' cellpadding=0 cellspacing=0 width=100% height=502 >");
      //General
      appendToHTML("<tr><td align=left>&nbsp;");
         printImage(image_location+"/schedule_general.gif");
         appendToHTML("</td><td colspan=3 width=100%>&nbsp;");
         printBoldText("FNDSCHEDTASKHEADGEN: General");
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td></td><td align=left colspan=3>");
         if (rows >0)
            printText(rowset.getValue("SCHEDULE_ID")+" - "+rowset.getValue("SCHEDULE_NAME"));
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td></td><td align=left nowrap>");
         if (rows >0)
         {
            printTextLabel("FNDSCHEDTASKHEADSCHEDTASK: Task:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("TASK_DESCRIPTION"));
         }
      appendToHTML("</td></tr>");         
      appendToHTML("<tr><td></td><td align=left nowrap>");         
         if (rows >0)
         {
            printTextLabel("FNDSCHEDTASKHEADSCHEDAT: Scheduled at:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getClientValue("MODIFIED_DATE"));
         }
      appendToHTML("</td></tr>");         
      appendToHTML("<tr><td></td><td align=left nowrap>");         
         if (rows >0)
         {
            printTextLabel("FNDSCHEDTASKHEADSCHEDBY: Scheduled by:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("USER_DESC") + " ("+ rowset.getValue("USERNAME")+")");
         }
      appendToHTML("</td></tr>");         
      appendToHTML("<tr><td></td><td align=left nowrap>");         
         if (rows >0)
         {
            printTextLabel("FNDSCHEDTASKHEADLANG: Selected language:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("LANG_DESC") +" ("+rowset.getValue("LANG_CODE")+")");
         }
      appendToHTML("</td></tr>");         
      appendToHTML("<tr><td></td><td align=left nowrap>");         
         if (rows >0)
         {
            printTextLabel("FNDSCHEDTASKHEADEXECUTED: Number of executions:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("EXECUTIONS"));
         }
      appendToHTML("</td></tr>");         
      appendToHTML("<tr><td colspan=4 align=right width=100%>");         
         printButton("GENERAL_EDIT", "FNDSCHEDTASKHEADEDIT: Edit","onclick='javascript:openWizard(\"EDIT\");' "+((rows>0)?"":"disabled"));
      appendToHTML("&nbsp;<br></td></tr>");         

      //schedule
      appendToHTML("<tr><td align=left>&nbsp;");
         printImage(image_location+"/schedule_schedule.gif");
         appendToHTML("</td><td colspan=3 width=100%>&nbsp;");
         printBoldText("FNDSCHEDTASKHEADSCHEDULE: Schedule");
      appendToHTML("</td></tr>");
      
      appendToHTML("<tr><td></td><td align=left nowrap ");
         if (rows >0)
         {
           if ("TRUE".equals(rowset.getValue("ACTIVE_DB")))
           {
              appendToHTML(">");
              printTextLabel("FNDSCHEDTASKHEADEXECUPLAN: Execution plan:");
              appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
              printText(rowset.getValue("EXECUTION_PLAN"));
           }
           else
           {
              appendToHTML("colspan=3>");
              printText("FNDSCHEDTASKHEADINACTIVE: The scheduled job is inactive");
           }
         }
         else
            appendToHTML(">");
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td></td><td align=left nowrap>");
         if (rows >0 && "TRUE".equals(rowset.getValue("ACTIVE_DB")))
         {
            printTextLabel("FNDSCHEDTASKHEADSTARTEXE: Start execution:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("START_DATE"));
         }
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td></td><td align=left nowrap>");
         if (rows >0 && "TRUE".equals(rowset.getValue("ACTIVE_DB")))
         {
            printTextLabel("FNDSCHEDTASKHEADSTOPEXE: Stop execution:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("STOP_DATE"));
         }
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td></td><td align=left nowrap>");
         if (rows >0 && "TRUE".equals(rowset.getValue("ACTIVE_DB")))
         {
            printTextLabel("FNDSCHEDTASKHEADNEXTEXE: Next execution:");
            appendToHTML("</td><td>&nbsp;</td><td width=100% align=left>");
            printText(rowset.getValue("NEXT_EXECUTION_DATE"));
         }
      appendToHTML("</td></tr>");
           
      appendToHTML("<tr><td colspan=4 align=right>");
         printButton("SCHEDULE", "FNDSCHEDTASKHEADSCHEDULE: Schedule","onclick='javascript:openWizard(\"SCHEDULE\");' "+((rows>0)?"":"disabled"));
      appendToHTML("&nbsp;<br></td></tr>");
      
      //options
      appendToHTML("<tr><td align=left>&nbsp;");
         printImage(image_location+"/schedule_options.gif");
         appendToHTML("</td><td colspan=3>&nbsp;");
         printBoldText("FNDSCHEDTASKHEADOPTIONS: Options");
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td></td><td colspan=4 align=left nowrap>");
         printText("change the options");
      appendToHTML("</td></tr>");

      appendToHTML("<tr><td colspan=4 align=right>");         
           
           String disable_str = "disabled";
           
           if (rows >0 && (!"NONE".equals(rowset.getValue("ARGUMENT_TYPE_DB"))))
              disable_str = "";
           
           printButton("OPTION", "FNDSCHEDTASKHEADOPTION: Option","onclick='javascript:openWizard(\"OPTION\");' "+disable_str);
      appendToHTML("&nbsp;<br></td></tr>");

      appendToHTML("</table>");

      if (rows > 0)
      {
       appendDirtyJavaScript("\nfunction openWizard(step_){");
       appendDirtyJavaScript("\n\tshowNewBrowser('" + mgr.getASPConfig().getScriptsLocation() + "ScheduledTaskWizard.page?FROM_SCHED_TASK=Y&SCHEDULE_ID="+rowset.getValue("SCHEDULE_ID")+"&METHOD_NAME="+rowset.getValue("METHOD")+"&ARGUMENT_TYPE="+rowset.getValue("ARGUMENT_TYPE_DB")+"&VALIDATION_METHOD="+(!mgr.isEmpty(rowset.getValue("VALIDATION_METHOD"))?rowset.getValue("VALIDATION_METHOD"):"")+"&STEP='+step_);");
       appendDirtyJavaScript("\n}\n");
      }
      //appendToHTML("</td>");
      //appendToHTML("</tr>");
      //appendToHTML("</table>");
      //endDataPresentation();
   }
}
