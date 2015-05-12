
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
 *  File        : AdvancedScheduledOptions.java 
 *  Modified    :
 *  rahelk  2004-05-18 created.
 *  Suneth M 2004-Aug-04 - Changed duplicate localization tags. 
 *  Mangala  2007-01-30  - Bug 63250, Added theming support in IFS clients.
 * ----------------------------------------------------------------------------
 * 27/7/2007 rahelk bug id 66785, validated all 3 date fields.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class AdvancedScheduledOptions extends ASPPageProvider
{
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPBlockLayout lay;
   private ASPCommandBar cmdbar;
   
   
   public AdvancedScheduledOptions(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      
      if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();      
      else if (!mgr.isEmpty(mgr.getQueryStringValue("START_DATE")))
      {
         ASPBuffer buf = mgr.newASPBuffer();
         buf.setFieldItem("START_DATE", mgr.getQueryStringValue("START_DATE"));
         
         if (!mgr.isEmpty(mgr.getQueryStringValue("STOP_DATE")))
         {
            buf.setFieldItem("STOP_DATE", mgr.getQueryStringValue("STOP_DATE"));
            buf.setFieldItem("ENABLE_STOP_DATE","TRUE");
         }

         String next_exe = mgr.getQueryStringValue("NEXT_EXECUTION_DATE");
         if (!mgr.isEmpty(next_exe))
            buf.setFieldItem("NEXT_EXECUTION", next_exe);
         else
            buf.setValue("NEXT_EXECUTION",mgr.getQueryStringValue("CAL_NEXT_EXE_DATE"));
            
         rowset.addRow(buf);
         //if (mgr.isEmpty(next_exe))
      }
         
   }
   
   
   private void validate()
   {
      ASPManager mgr = getASPManager();
      String start_date = mgr.readValue("START_DATE");
      String stop_date = mgr.readValue("STOP_DATE");
      String next_date = mgr.readValue("NEXT_EXECUTION_DATE");
      
      ASPBuffer buf = mgr.newASPBuffer();
      
      buf.setFieldItem("START_DATE", start_date);
      buf.setFieldItem("STOP_DATE", stop_date);
      buf.setFieldItem("NEXT_EXECUTION", next_date);
      
      mgr.endResponse();
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
      return "FNDADVSCHEDOPTTITLE: Advanced Schedule Options";
   }
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("EXECUTION").setHidden();
      
      blk.addField("START_DATE","Date").
          setLabel("FNDADVSCHEDSTARTDATE: Start Date");

      blk.addField("ENABLE_STOP_DATE").
          setCheckBox("T,F").
          setCustomValidation("ENABLE_STOP_DATE","STOP_DATE");
          
          
      blk.addField("STOP_DATE","Date").
          setLabel("FNDADVSCHEDENDDATE: End Date");
      
      blk.addField("NEXT_EXECUTION","Datetime").
          setLabel("FNDADVSCHEDNEXTEXEC: Next execution");

      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.EDIT_LAYOUT); 
      
      lay.setSimple("STOP_DATE");
      //lay.setFieldOrder("START_DATE,END_DATE,ENABLE_END_DATE");
      lay.setDialogColumns(1);
      lay.showBottomLine(false);

      //disableHeader();
      disableHomeIcon();
      disableNavigate();
      disableOptions();
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      String image_location = mgr.getASPConfig().getImagesLocation();

      eval(blk.generateAssignments());
      appendToHTML(blk.generateHiddenFields());
      beginDataPresentation();
      appendToHTML("<table class='pageFormWithBorder' cellpadding=0 cellspacing=0 width=100%>");
      appendToHTML("<tr><td colspan=3>");
      drawSimpleCommandBar(mgr.translate(getTitle()));
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td>");
      appendToHTML("</td><td align=left nowrap>");
      printReadLabel(mgr.getASPField("START_DATE").getLabel());
      appendToHTML("&nbsp;</td><td width=100%>");
      printField("START_DATE",rowset.getClientValue("START_DATE"),mgr.getASPField("START_DATE").getTag());
      appendToHTML("</td></tr>");
      

      appendToHTML("<tr><td align=right>");
      printCheckBox("ENABLE_STOP_DATE","ENABLE",("TRUE".equals(rowset.getValue("ENABLE_STOP_DATE")))," onclick='javascript:validateEnableEndDate();'");
      appendToHTML("</td><td align=left nowrap>");
      printReadLabel(mgr.getASPField("STOP_DATE").getLabel());
      appendToHTML("&nbsp;</td><td width=100%>");
      printField("STOP_DATE",rowset.getClientValue("STOP_DATE"),("TRUE".equals(rowset.getValue("ENABLE_STOP_DATE"))?"":"disabled ")+ mgr.getASPField("STOP_DATE").getTag());
      appendToHTML("</td></tr>");
      
      appendToHTML("<tr><td colspan=3 width=100%>");
      appendToHTML("<hr>");
      appendToHTML("</td></tr>");
      
      appendToHTML("<tr><td>");
      appendToHTML("</td><td align=left nowrap>");
      printReadLabel(mgr.getASPField("NEXT_EXECUTION").getLabel());
      appendToHTML("&nbsp;</td><td width=100%>");
      printField("NEXT_EXECUTION",rowset.getClientValue("NEXT_EXECUTION"),mgr.getASPField("NEXT_EXECUTION").getTag());
      appendToHTML("</td></tr>");
      
      appendToHTML("<tr valign=top ><td></td><td colspan=2 align=left>");
      printText("FNDADVSCHEDOPTCALNEXT: Calculate the next execution time, based on the current schedule options");      
      appendToHTML("&nbsp;&nbsp;");
      printButton("CAL_NEXT","FNDADVSCHEDOPTCALCULATE: Calculate","onclick='javascript:calculatedDateValue()'");
      appendToHTML("</td></tr>");
      appendToHTML("<tr><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td></tr>");

      appendToHTML("<tr><td colspan=3 width=100%>");
      appendToHTML("<hr>");
      appendToHTML("</td></tr>");
      
      appendToHTML("<tr><td colspan=3 align=right>");
      printButton("OK","FNDADVSCHEDOPTOK: Ok","onclick='javascript:assignValues()'");
      appendToHTML("&nbsp;&nbsp;");
      printButton("CANCEL","FNDADVSCHEDOPTCANCEL: Cancel","onclick='javascript:window.close();'");
      appendToHTML("</td></tr>");
      appendToHTML("</td></tr></table>");
      endDataPresentation(false);
      

       
      //appendDirtyJavaScript("validateEnableEndDate();\n");
      appendDirtyJavaScript("function validateEnableEndDate(){\n");
      appendDirtyJavaScript("\t if (f.ENABLE_STOP_DATE.checked) \n");
      appendDirtyJavaScript("\t\t  f.STOP_DATE.disabled = false;\n");
      appendDirtyJavaScript("\t else\n");
      appendDirtyJavaScript("\t\t  f.STOP_DATE.disabled = true;\n");
      appendDirtyJavaScript("}\n");
       
      appendDirtyJavaScript("function assignValues(){\n");
      appendDirtyJavaScript(" r = __connect('"+mgr.getURL()+"?VALIDATE=ALLDATES'\n");
      appendDirtyJavaScript("     +'&START_DATE='+ URLClientEncode(getValue_('START_DATE',-1))\n");
      appendDirtyJavaScript("     +'&STOP_DATE='+ (f.ENABLE_STOP_DATE.checked?URLClientEncode(getValue_('STOP_DATE',-1)):'')\n");
      appendDirtyJavaScript("     +'&NEXT_EXECUTION_DATE='+ URLClientEncode(getValue_('NEXT_EXECUTION',-1)));\n");
      appendDirtyJavaScript(" r = r.substring(1);");
      appendDirtyJavaScript(" if(r!='')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("   alert(r);\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("\t window.opener.document.form.START_DATE.value = f.START_DATE.value; \n");
      appendDirtyJavaScript("\t window.opener.document.form.NEXT_EXECUTION_DATE.value = f.NEXT_EXECUTION.value; \n");
      appendDirtyJavaScript("\t if (f.ENABLE_STOP_DATE.checked) \n");
      appendDirtyJavaScript("\t\t  window.opener.document.form.STOP_DATE.value = f.STOP_DATE.value; \n");
      appendDirtyJavaScript("\t window.close(); \n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");

      
      ASPBuffer buf = mgr.newASPBuffer();
      buf.setValue("NEXT_EXECUTION", mgr.getQueryStringValue("CAL_NEXT_EXE_DATE"));
      String default_cal_date = buf.getFieldValue("NEXT_EXECUTION");
      
      appendDirtyJavaScript("function calculatedDateValue(){\n");
      appendDirtyJavaScript("\tf.NEXT_EXECUTION.value ='"+default_cal_date+"'; \n");
      appendDirtyJavaScript("}\n");
      
   }
}

