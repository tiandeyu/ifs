
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
 *  File        : ParameterSheet.java
 *  Modified    :
 *    rahelk  2004-06-09 created.
 *    rahelk  2004-06-14 added a scrollable panel
 *    rahelk  2004-07-22 extended ASPPageProvider and implemented a solution with IFRAMES.
 * ----------------------------------------------------------------------------
 * New Comments:
 *
 * Revision 1.5  2006/07/03 buhilk 
 * Bug 58216, Fixed SQL Injection threats
 *
 * Revision x.x  2006/03/09 rahelk
 * Added possibility to pass parameter values from pages
 *
 * Revision 1.4  2006/02/09 rahelk
 * Fixed call id 132599. Added get method for DATA_TYPE in okFind.
 *
 * Revision 1.3  2005/10/11 12:23:26  rahelk
 * Validated scheduled task params DATA_TYPEs
 *
 * Revision 1.2  2005/09/22 12:39:23  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.3.2.2  2005/08/26 10:01:05  rahelk
 * Changed query in okFind
 *
 *
 * ----------------------------------------------------------------------------
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class ParameterSheet extends ASPPageProvider
{
   protected static String CHECK_MANDATORY = "checkMandatoryValue";
   protected static String CHECK_NUMBER_VALUE = "checkNumberValue";
   protected static String CHECK_DATE_VALUE = "checkDateValue";

   protected ASPBlock paramblk;
   protected ASPRowSet paramset;
   protected ASPTable paramtbl;
   protected ASPCommandBar parambar;
   protected ASPBlockLayout paramlay;

   protected boolean modify_task;
   protected String default_script;

   public ParameterSheet(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      initTaskParameters();
      if (!mgr.isEmpty(mgr.getQueryStringValue("SCHEDULE_ID")))
         okFind();
   }

   private void okFind()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      trans.addQuery("PARAM","BATCH_SCHEDULE_PAR","NAME PARAM_NAME, VALUE PARAM_VALUE, MANDATORY_DB PARAM_MANDATORY, BATCH_SCHEDULE_METHOD_PAR_API.Get_Data_Type( BATCH_SCHEDULE_API.Get_Schedule_Method_Id(SCHEDULE_ID), SEQ_NO ) DATA_TYPE","SCHEDULE_ID =?","SEQ_NO").
         addParameter("SCHEDULE_ID","N","IN",mgr.getQueryStringValue("SCHEDULE_ID"));
      //ASPQuery q = trans.addQuery("PARAM","select NAME PARAM_NAME, VALUE PARAM_VALUE, MANDATORY_DB PARAM_MANDATORY FROM BATCH_SCHEDULE_PAR where SCHEDULE_ID ="+mgr.getQueryStringValue("SCHEDULE_ID")+" ORDER BY SEQ_NO");
      mgr.submit(trans);
   }

   private void initTaskParameters()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      trans.addQuery("DEFAULT_PARAMS","select NAME PARAM_NAME, BATCH_SCHEDULE_METHOD_PAR_API.Get_Default_Value__( SCHEDULE_METHOD_ID, SEQ_NO ) PARAM_VALUE, MANDATORY_DB PARAM_MANDATORY, DATA_TYPE FROM BATCH_SCHEDULE_METHOD_PAR where SCHEDULE_METHOD_ID =? ORDER BY SEQ_NO").
         addParameter("SCHEDULE_METHOD_ID","N","IN",mgr.getQueryStringValue("SCHEDULE_METHOD_ID"));
      trans = mgr.perform(trans);

      default_script = "";
      int j = 1; //0th one is the hidden field (old query level)

      HashMap client_param_map = new HashMap();
      String client_params = mgr.getQueryStringValue("CLIENT_PARAMS");

      if (!mgr.isEmpty(client_params))
      {
         StringTokenizer st = new StringTokenizer(client_params,ScheduledTask.CLIENT_PARAM_SEPARATOR );

         while (st.hasMoreTokens())
         {
            String name_value = st.nextToken();
            int index = name_value.indexOf('=');

            if (index != -1)
            {
               String client_param_name  = name_value.substring(0,index);
               String client_param_value = name_value.substring(index+1);

               client_param_map.put(client_param_name, client_param_value);
            }
         }
      }
      
      try
      {
         ASPBuffer buf = trans.getBuffer("DEFAULT_PARAMS");
         int buf_size = buf.countItems()-1;

         for (int i=0; i<buf_size; i++)
         {
            ASPBuffer def_buf = buf.getBufferAt(i);
            if (!modify_task)
            {
               String client_param_value = (String)client_param_map.get(def_buf.getValue("PARAM_NAME"));
               if ( !mgr.isEmpty(client_param_value))
                  def_buf.setValue("PARAM_VALUE", client_param_value);
               
               paramset.addRow(def_buf);
            }
            String def_value = def_buf.getValue("PARAM_VALUE");
            if (mgr.isEmpty(def_value)) def_value = "";

            default_script +="\tf."+def_buf.getValue("PARAM_NAME")+".value='"+def_value+"';\n";
         }
         paramset.first();
      }
      catch (Exception e)
      {
         error(e);
      }
   }


   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      paramblk = mgr.newASPBlock("PARAM");
      paramblk.addField("PARAM_NAME");
      paramblk.addField("PARAM_VALUE").setMandatory();
      paramblk.addField("PARAM_MANDATORY");
      paramblk.addField("DATA_TYPE");
      paramblk.addField("DATE_FIELD","Datetime","yyyy-MM-dd HH:mm:ss").setFunction("''");

      paramset = paramblk.getASPRowSet();
      paramtbl = mgr.newASPTable(paramblk);
      paramtbl.setEditable();

      parambar = mgr.newASPCommandBar(paramblk);
      paramlay = paramblk.getASPBlockLayout();
      paramlay.setDefaultLayoutMode(paramlay.MULTIROW_LAYOUT);

      disableHeader();
      disableFooter();
      disableBar();

      appendJavaScript("\nfunction "+CHECK_MANDATORY+"(fld,lbl)\n");
      appendJavaScript("{\n");
      appendJavaScript("\treturn checkMandatory_(fld,lbl,'');\n");
      appendJavaScript("}\n");

      appendJavaScript("\nfunction "+CHECK_NUMBER_VALUE+"(fld,lbl)\n");
      appendJavaScript("{\n");
      appendJavaScript("\treturn checkNumberValue_(fld,lbl,'');\n");
      appendJavaScript("}\n");
      
      appendToPreDefine();
      //adjustPreDefine();
   }

   protected void appendToPreDefine() throws FndException
   {
   }

   private void adjustPreDefine() throws FndException
   {
      String[] field_names = split(paramblk.getFieldList(),",");
      int count = field_names.length;

      for (int i=0; i<count; i++)
      {
         String name = field_names[i];

         if ("SEQ_NO".equals(name) || "PARAM_NAME".equals(name) || "PARAM_VALUE".equals(name) || "PARAM_MANDATORY".equals(name) || "DATA_TYPE".equals(name) || "DATE_FIELD".equals(name))
            continue;

         ASPField field = paramblk.getASPField(name);
         if (!field.hasSetFunction())
            field.setFunction("''");
      }
   }


   protected void printDefaultButton() throws FndException
   {
      printButton("DEFAULT","FNDSCHEDWIZARDRADDEFBUTT: Default","onclick=\"javascript:setDefaultValues()\"");

      appendDirtyJavaScript("\nfunction setDefaultValues()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(""+default_script);
      appendDirtyJavaScript("}\n");
   }

   protected String getMandatoryTag(String label)
   {
      String tag = "onChange=\"javascript:"+CHECK_MANDATORY+"(this,'"+label+"')\"";
      return tag;
   }

}
