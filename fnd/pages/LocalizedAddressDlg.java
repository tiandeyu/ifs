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
*  File        : LocalizedAddressDlg.java
*  Description : 
*  Modified    : 2002-Jan-13 Mangala P. - Created.
*  Ramila H 2002-Jan-30 Made changes to suport LOV and their in parameters.
*                       Also made changes to get values from window.opener instead of
*                       from the query string.
*  Ramila H 2002-Mar-04 Added support to change edit layout for each country. 
* ----------------------------------------------------------------------------
 * New Comments:
 * 2007/05/16 rahelk bug id 65426, changed javascript method ok to __ok
 * 2007/04/20 rahelk Bug id 63951. Added field label with translation key.
 * 2006 Jun 23 buhilk
 * Bug id 58304 - Improvements for Address Dlg. Added a blank command bar, Header, Help menu and Footer
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.io.*;
import java.util.*;

public class LocalizedAddressDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.LocalizedAddressDlg");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String jsfunc_name;
   private String field_order;
   private int field_size=7;
   private ASPField[] field_list = new ASPField[field_size];

   //===============================================================
   // Construction
   //===============================================================
   public LocalizedAddressDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      jsfunc_name = null;
      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      LocalizedAddressDlg page = (LocalizedAddressDlg)(super.clone(obj));

      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headlay = page.headblk.getASPBlockLayout();

      page.jsfunc_name = null;
      page.field_order = null;
      page.field_list = null;

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      jsfunc_name    = mgr.getQueryStringValue("JSFUNC_NAME");

      adjust();
   }

//=============================================================================
// Command Bar functions
//=============================================================================
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      String defkey = mgr.getDynamicDefKey();
      
      String[] fields = split(mgr.getQueryStringValue("ADDRESS_FIELDS"),"^");
      String[] non_address_fields = split(mgr.getQueryStringValue("NON_ADDRESS_FIELDS"),"^");

      int[] size_arr = new int[field_size];

      String[] labels = split(mgr.getQueryStringValue("LABELS"),"^");
      String[] lov_views = split(mgr.getQueryStringValue("LOV_VIEWS"),"^");
      String[] in_parameters = split(mgr.getQueryStringValue("LOV_IN_PARAMS"),"^");
      
      String[] edit_template_temp = split(mgr.getQueryStringValue("EDIT_TEMPLATE"),"^");

      headblk = mgr.newASPBlock("HEAD");
      
      for (int i=0; i<field_size; i++ )
      {
         ASPField f = headblk.addField(fields[i]);
         field_list[i] = f;
         f.setLabel(labels[i]);
         f.setSize(size_arr[i]);
         if (!mgr.isEmpty(lov_views[i]))
            if (!mgr.isEmpty(in_parameters[i]))
               f.setDynamicLOV(lov_views[i],in_parameters[i]);
            else
               f.setDynamicLOV(lov_views[i]);

      }
      
      if (non_address_fields!=null)
      {
         for (int i=0; i<non_address_fields.length; i++ )
         {
            ASPField f = headblk.addField(non_address_fields[i]);
            f.setHidden();
         }
      }
       
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      mgr.newASPTable(headblk);
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
      
      //disableHelp();
      disableHomeIcon();
      disableOptions();
      disableNavigate();
      disableApplicationSearch();
      //disableHeader();
      //disableFooter();
      //disableBar();
      
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      int max_rows=0, max_cols=0;;
      field_order = "";

      int[] col_arr = new int[field_size];
      int[] row_arr = new int[field_size];
      int[] size_arr = new int[field_size];
      int[] span_arr = new int[field_size];
      boolean[] hidden = new boolean[field_size];
      
      String[] fields = split(mgr.getQueryStringValue("ADDRESS_FIELDS"),"^");
      String[] edit_template_temp = split(mgr.getQueryStringValue("EDIT_TEMPLATE"),"^");
      

      for (int i=0; i<field_size; i++ )
      {
         String[] temp_arr = split(edit_template_temp[i],",");
         
         row_arr[i] = Integer.parseInt(split(temp_arr[0],"=")[1]) ;
         col_arr[i] = Integer.parseInt(split(temp_arr[1],"=")[1]) ;
         size_arr[i]= (int)(Float.parseFloat(split(temp_arr[2],"=")[1]) * 10) ; 

         max_rows = max_rows<row_arr[i]?row_arr[i]:max_rows;
         max_cols = max_cols<col_arr[i]?col_arr[i]:max_cols;
      }
      
      String[][] layout_matrix = new String[max_rows][max_cols];

      for (int i=0; i<field_size; i++)
      {
         int row = row_arr[i];
         int col = col_arr[i];
         int field_size = size_arr[i];

         if (row>0 && col>0 && field_size >0) 
            layout_matrix[row-1][col-1] = fields[i];
         else
         {
             hidden[i] = true;//hide them
         }
      }

      for (int i=0; i<max_rows; i++) 
      {
         String last_field = null;
         int last_span = 0;

         for (int j=0; j<max_cols; j++) 
         {
            if (layout_matrix[i][j]!=null)
            {
               String sep = field_order.length()>0?",":"";
               field_order += sep + layout_matrix[i][j];

               if (last_field!=null && last_span>0)
               {
                  int k=0;
                  while (k<fields.length && !last_field.equals(fields[k]))
                     k++;
                  span_arr[k] = (last_span+1)*2;
               }
               last_field = layout_matrix[i][j]; 
               last_span = 0;
            }
            else{last_span++;}
         }
         if (last_field!=null && last_span>0)
         {
            int k=0;
            while (k<fields.length && !last_field.equals(fields[k]))
               k++;
            span_arr[k] = (last_span+1)*2;
         }
      }
      
      //hide/unhide ASPFields
      for (int i=0;i<hidden.length;i++) {
         if (hidden[i]) 
            field_list[i].setHidden();
         else
         {
            field_list[i].unsetHidden();
            field_list[i].setSize(size_arr[i]);
         }
      }

      headlay.setDialogColumns(max_cols);
      
      headlay.setFieldOrder(field_order);
      for (int i=0; i<field_size; i++) 
      {
         if (span_arr[i]>0) {
            headlay.setDataSpan(fields[i],span_arr[i]*2);
         }
      }

      fields = split(field_order,",");
      mgr.setInitialFocus(fields[0]);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PAGESLOCALIZEADDDESC: Address";
   }

   protected String getTitle()
   {
      return "PAGESLOCALIZEADDTITLE: Address";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      beginDataPresentation();
      drawSimpleCommandBar("");
      endDataPresentation(false);
      appendToHTML(headlay.generateDialog());
      
      beginDataPresentation();
      printButton("OK", "FNDPAGESLOCADDDLGOK: OK","OnClick='javascript:__ok()'");
      this.printSpaces(1);
      printButton("CANCEL", "FNDPAGESLOCADDDLGCANCEL: Cancel","OnClick='javascript:window.close()'");
      endDataPresentation(false);

      appendDirtyJavaScript("assignValues();\n");
      appendDirtyJavaScript("function assignValues()\n{");
      
      String[] fields = split(mgr.getQueryStringValue("ADDRESS_FIELDS"),"^");

      for (int i=0; i<fields.length; i++)
         appendDirtyJavaScript("\nf."+fields[i]+".value = window.opener.document.form."+fields[i]+".value; ");
      
      String[] non_address_fields = split(mgr.getQueryStringValue("NON_ADDRESS_FIELDS"),"^");

      if (non_address_fields!=null)
         for (int i=0; i<non_address_fields.length; i++)
             appendDirtyJavaScript("\nf."+non_address_fields[i]+".value = window.opener.document.form."+non_address_fields[i]+".value; ");
     
      appendDirtyJavaScript("\n}");

      appendDirtyJavaScript("function __ok()\n{");
      appendDirtyJavaScript("\nwindow.opener.", jsfunc_name,"(");
      appendDirtyJavaScript("f.",fields[0],".value,");
      appendDirtyJavaScript("f.",fields[1],".value,");
      appendDirtyJavaScript("f.",fields[2],".value,");
      appendDirtyJavaScript("f.",fields[3],".value,");
      appendDirtyJavaScript("f.",fields[4],".value,");
      appendDirtyJavaScript("f.",fields[5],".value,");
      appendDirtyJavaScript("f.",fields[6],".value);\n");
      appendDirtyJavaScript("window.close();\n}");

   }

}
