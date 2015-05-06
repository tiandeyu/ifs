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
 * File        : DefineGlobals.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Suneth M 2001-Jun-13 - Created.
 *    Suneth M 2001-Sep-12 - Changed localization tags according to standard. 
 *    Suneth M 2002-Mar-29 - Fixed minor bugs.
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    Sampath  2003-Feb-27 - made provision to clear the global variable  
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    Mangala  2004-01-14  - Improved with new APIs introduced to portal globals.
 *    Chandana 2004-Mar-23 - Merged with SP1.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.3  2007/02/19 14:59:06  buhilk
 * Bug id: 63433, Added csv support
 *
 *               2006/02/06 prralk
 * B132268 error in next link, db_rows not defines correctly
 * 
 * Revision 1.1  2005/09/15 12:38:02  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/04/20 07:05:39  sumelk
 * Merged the corrections for Bug 50721, Changed run() & printContents().
 * 
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;

import java.util.*;

public class DefineGlobals extends ASPPortletProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.DefineGlobals");

   private final static int size = 15;

   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBuffer buf;
   private transient int    skip_rows;
   private transient int    db_rows;
   private transient String mintitle;
   private transient String maxtitle;
   private transient String field_id;
   private transient String field_value;
   private transient String field_label;
   private transient String desc_value;
   private transient String desc_label;
   private transient String view_name;
   private transient String where_clause;
   //private transient String msg_text;
   private transient String global_value;
   private transient String global_desc;

   public DefineGlobals( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   public static String getDescription()
   {
      return "FNDPORTLETSDEFINEGLOBALSDESC: Define Global Variables";
   }


   public String getTitle(int mode)
   {
      if(DEBUG) debug(this+": DefineGlobals.getTitle()");

      if( mode==MINIMIZED )
         return mintitle;
      else if( mode==MAXIMIZED )
         return maxtitle;
      else
         return translate("FNDPORTLETSDEFINEGLOBALSCUSTTIT: Customize Define Global Variables");
   }


   protected void preDefine() throws FndException
   {
      appendJavaScript("function customizeDefineGlobals(obj,id)",
                       "{",
                          "customizeBox(id);",
                       "}\n");
      appendJavaScript( "function prevValues(obj,id)",
                        "{",
                        "   getPortletField(id,'CMD').value = 'PREV';",
                        "}\n");

      appendJavaScript( "function nextValues(obj,id)",
                        "{",
                        "   getPortletField(id,'CMD').value = 'NEXT';",
                        "}\n");

      init();
   }


   protected void init()
   {
      if(DEBUG) debug(this+": DefineGlobals.init()");

      mintitle = readProfileValue("MINTITLE", translate(getDescription()) );
      maxtitle = readProfileValue("MAXTITLE", translate(getDescription()) );

      field_id     = readProfileValue("FIELD_ID",     "");
      field_value  = readProfileValue("FIELD_VALUE",  "");
      field_label  = readProfileValue("FIELD_LABEL",  "");
      desc_value   = readProfileValue("DESC_VALUE",   "");
      desc_label   = readProfileValue("DESC_LABEL",   "");
      view_name    = readProfileValue("VIEW_NAME",    "");
      where_clause = readProfileValue("WHERE_CLAUSE", "");
      global_value = readProfileValue("GLOBAL_VALUE", "");
      global_desc  = readProfileValue("GLOBAL_DESC",  "");

      ASPContext ctx = getASPContext();
      
      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

      String cmd = readValue("CMD");

      if("PREV".equals(cmd) && skip_rows>=size)
      {
         skip_rows -= size;
      }
      else if("NEXT".equals(cmd) && skip_rows<=db_rows-size)
      {
         skip_rows += size;
      }
      ctx.writeValue("SKIP_ROWS",skip_rows+"");
   }


   protected void run()
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      String sql_text = null;

      if (!Str.isEmpty(field_id))
         field_id = field_id.toUpperCase();

      if (!Str.isEmpty(field_value) && !Str.isEmpty(view_name))
      {
         if (Str.isEmpty(field_label))
         {
            if (field_value.indexOf(".")<0) 
               field_label = field_value;
            else
               field_label = "";
         }
         
         if (Str.isEmpty(desc_label))
         {
            if (desc_value.indexOf(".")<0) 
               desc_label = desc_value;
            else
               desc_label = "";
         }
         if (!Str.isEmpty(where_clause))
            sql_text = "select "+ field_value + " " + field_label + "," + desc_value + " " + desc_label +" from " + view_name + " where " + where_clause;
         else 
            sql_text = "select "+ field_value + " " + field_label + "," + desc_value + " " + desc_label +" from " + view_name;

         ASPQuery             qry   = trans.addQuery("GLOBS",sql_text);
         qry.includeMeta("ALL");
         qry.skipRows(skip_rows);
         qry.setCountDbRows(true);
      
         trans = submit(trans);	 

         buf = trans.getBuffer("GLOBS");
         
         if (buf.countItems() > 0)
            db_rows = Integer.parseInt(buf.getValue("INFO/ROWS"));
         else
            db_rows = 0;

         getASPContext().writeValue("DB_ROWS", db_rows+"" );
      }
      /*if (!Str.isEmpty(field_id))
      {
         if (!getASPManager().getPortalPage().getASPPortal().isGlobalVariableDefined(field_id))
            msg_text       = translate("FNDPORTLETSDEFINEGLOBALSMSG1: Press the arrow icon to choose the Global Variable");
         else
            msg_text = translate("FNDPORTLETSDEFINEGLOBALSMSG2: Current value of the Global Variable (&3): &1 - &2",global_value,global_desc,field_id);
      }*/
   }


   public void printContents() throws FndException
   {
      //String msg_text;

      if (!Str.isEmpty(field_id) && !Str.isEmpty(field_value) && !Str.isEmpty(desc_value) && !Str.isEmpty(view_name))
      { 
         printHiddenField("CMD","");
         
         if (buf.countItems() > 1)
         {
            beginTransparentTable();
            printSpaces(1);
            if (!Str.isEmpty(field_id) && (getASPManager().getPortalPage().getASPPortal().isGlobalVariableDefined(field_id)))
            {
               beginTableBody();
                  beginTableCell();
                  setFontStyle(BOLD);
                  printSpaces(1);
                  printText(translate("FNDPORTLETSDEFINEGLOBALSMSGCURRVAL: Current value of the Global Variable (&1):",field_id));
                  endFont();
                  endTableCell();
               nextTableRow();
                  beginTableCell();
                  printSpaces(6);
                  printText(readProfileValue(field_id) + " -" + getGlobalDescription(field_id));
                  endTableCell();
               endTableBody();
            }
            else
            {
               beginTableTitleRow();
               setFontStyle(BOLD);
               printSpaces(1);
                printText(translate("FNDPORTLETSDEFINEGLOBALSMSG1: Press the arrow icon to choose the Global Variable"));
                endFont();             
                endTableTitleRow();
            }
            endTable();
         
            String labels = field_label + "," + desc_label;
            drawTable(buf,labels);
               
            if(size < db_rows)
            {
               printNewLine();
    
               if(skip_rows>0)
                  printSubmitLink("FNDPORTLETSDEFINEGLOBALSPRV: Previous","prevValues");
               else
                  printText("FNDPORTLETSDEFINEGLOBALSPRV: Previous");
    
               printSpaces(5);
               String rows = translate("FNDPORTLETSDEFINEGLOBALSROWS: (Rows &1 to &2 of &3)",
                                      (skip_rows+1)+"",
                                      (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                       db_rows+"");
               printText( rows );
               printSpaces(5);
    
               if(skip_rows<db_rows-size)
                  printSubmitLink("FNDPORTLETSDEFINEGLOBALSNEXT: Next","nextValues");
               else
                  printText("FNDPORTLETSDEFINEGLOBALSNEXT: Next");
    
               printNewLine();
               printNewLine();
            }
            if(getASPManager().getPortalPage().getASPPortal().isGlobalVariableDefined(field_id))
            {
               printNewLine();
               printRemoveGlobalLink("FNDPORTLETSDEFINEGLOBALSINFCLEAR: &&Clear&& defined globals",field_id );
               printNewLine();
            }
         }
         else
         {
            printNewLine();
            printSpaces(1);
            printText("FNDPORTLETSDEFINEGLOBALSNDF: No Data Found.");  
            printNewLine();
         }
      }
      else
      {
         printNewLine();
         printText("FNDPORTLETSDEFINEGLOBALSINFO1: Enter values for the global variable on the");
         printSpaces(1);
         printScriptLink("FNDPORTLETSDEFINEGLOBALSINFO2: Customization", "customizeDefineGlobals");
         printSpaces(1);
         printText("FNDPORTLETSDEFINEGLOBALSINFO3: page.");
      }
   }


   public void drawTable(ASPBuffer aspbuf, String header) throws FndException
   {
      ASPConfig  cfg = getASPManager().getASPConfig();

      String images_location      = cfg.getParameter("APPLICATION/LOCATION/IMAGES", "/&(APPLICATION/PATH)/common/images/");
      int lov_img_width           = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/RETURN/IMAGE/WIDTH", "11"));
      int lov_img_height          = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/RETURN/IMAGE/HEIGHT","11"));
      int i,j, rows, cols;

      String s = null;
      StringTokenizer st = new StringTokenizer(header,",");

      // Create the table heading
      rows = aspbuf.countItems(); 
         
      beginTable();
         beginTableTitleRow();
            beginTableCell();
            endTableCell();
         
         //unpack and set user defined header items
         while (st.hasMoreTokens())
         {
             beginTableCell();
             printText(st.nextToken());
             endTableCell();
         }
         endTableTitleRow();
         beginTableBody();
         
         // Create the table contents
         for (i=0;i < (rows<size?rows:size); i++)
         {
            ASPBuffer row = aspbuf.getBufferAt(i);
            if ("DATA".equals(aspbuf.getNameAt(i)))
            {
               cols = row.countItems();
               beginTableCell();
               defineGlobalVariable(field_id, field_label, row.getValueAt(0), row.getValueAt(1));
               printImageLink(images_location + "clickindicator.gif", generateGlobalVariableURL());
               //,"FNDPORTLETSDEFINEGLOBALSCLICKIMG: Click to set this value to the global variable");
               endTableCell();
                
               for (j=0;j < cols ; j++ )
               {
                  s = row.getValueAt(j);
                  beginTableCell();
                  if(s==null)
                  {
                     printText("&nbsp");
                  }
                  else
                  {
                     printText(s);
                  }
                  endTableCell();
               }
               nextTableRow();
            }
         }
         endTableBody();
      endTable();
   }


   public boolean canCustomize()
   {
      if(DEBUG) debug(this+": DefineGlobals.canCustomize()");
      return true;
   }


   public void printCustomBody() throws FndException
   {
      if(DEBUG) debug(this+": DefineGlobals.printCustomBody()");

      printNewLine();

      beginTransparentTable();
        beginTableTitleRow();
          printTableCell("FNDPORTLETSDEFINEGLOBALSTITLEMSG: You can choose your own title for this portlet",3,0,LEFT);
        endTableTitleRow();

        beginTableBody();
          beginTableCell();
            printSpaces(5);
            printText("FNDPORTLETSDEFINEGLOBALSMAXTITLE: when maximized:");
          endTableCell();
          beginTableCell(1);
            printField("MAXTITLE",maxtitle,50);
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDPORTLETSDEFINEGLOBALSMINTITLE: and when minimized:");
          endTableCell();
          beginTableCell(1);
            printField("MINTITLE",mintitle,50);
          endTableCell();
        nextTableRow();
          printTableCell(null,3);
        nextTableRow();
          beginTableCell();
            printText("FNDPORTLETSDEFINEGLOBALSFIELDNAME: Global Variable Name:");
          endTableCell();
          beginTableCell(1);
            printField("FIELD_ID",readAbsoluteProfileValue("FIELD_ID", ""),50);
            printText("*");
          endTableCell();
        nextTableRow();
          printTableCell(null,3);
        nextTableRow();
          beginTableTitleRow();
            printTableCell("FNDPORTLETSDEFINEGLOBALSTITLEMSG1: Parameters used to query the possible values of this global variable from the database:",3,0,LEFT);
          endTableTitleRow();
        nextTableRow();
          printTableCell(null,3);
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDPORTLETSDEFINEGLOBALSGLOBVARFIELD: Global Variable Field:");
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(10);
            printText("FNDPORTLETSDEFINEGLOBALSCOLNAME: Column Name:");
          endTableCell();
          beginTableCell(1);
            printField("FIELD_VALUE",readAbsoluteProfileValue("FIELD_VALUE", ""),50);
            printText("*");
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(10);
            printText("FNDPORTLETSDEFINEGLOBALSCOLALIAS: Alias:");
          endTableCell();
          beginTableCell(1);
            printField("FIELD_LABEL",readAbsoluteProfileValue("FIELD_LABEL", ""),50);
          endTableCell();
        nextTableRow();
            printTableCell(null,3);
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDPORTLETSDEFINEGLOBALSVARDESCFIELD: Global Variable Description Field:");
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(10);
            printText("FNDPORTLETSDEFINEGLOBALSCOLNAME: Column Name:");
          endTableCell();
          beginTableCell(1);
            printField("DESC_VALUE",readAbsoluteProfileValue("DESC_VALUE", ""),50);
            printText("*");
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(10);
            printText("FNDPORTLETSDEFINEGLOBALSCOLALIAS: Alias:");
          endTableCell();
          beginTableCell(1);
            printField("DESC_LABEL",readAbsoluteProfileValue("DESC_LABEL", ""),50);
          endTableCell();
        nextTableRow();
            printTableCell(null,3);
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDPORTLETSDEFINEGLOBALSVIEW: View Name:");
          endTableCell();
          beginTableCell(1);
            printField("VIEW_NAME",readAbsoluteProfileValue("VIEW_NAME", ""),50);
            printText("*");
          endTableCell();
          nextTableRow();
            printTableCell(null,3);
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDPORTLETSDEFINEGLOBALSWHERECLAUSE: Where Clause:");
          endTableCell();
          beginTableCell(1);
            printField("WHERE_CLAUSE",readAbsoluteProfileValue("WHERE_CLAUSE", ""),50);
          endTableCell();
        endTableBody();
      endTable();
    }


   public void submitCustomization()
   {
      if(DEBUG) debug(this+": DefineGlobals.submitCustomization()");

      mintitle = readValue("MINTITLE");
      maxtitle = readValue("MAXTITLE");
      writeProfileValue("MINTITLE", mintitle );
      writeProfileValue("MAXTITLE", maxtitle );

      field_id = readValue("FIELD_ID");
      writeProfileValue("FIELD_ID", readAbsoluteValue("FIELD_ID"));
   
      field_value = readValue("FIELD_VALUE");
      writeProfileValue("FIELD_VALUE",readAbsoluteValue("FIELD_VALUE"));
   
      field_label = readValue("FIELD_LABEL");
      writeProfileValue("FIELD_LABEL",readAbsoluteValue("FIELD_LABEL"));

      desc_value = readValue("DESC_VALUE");
      writeProfileValue("DESC_VALUE",readAbsoluteValue("DESC_VALUE"));

      desc_label = readValue("DESC_LABEL");
      writeProfileValue("DESC_LABEL",readAbsoluteValue("DESC_LABEL"));

      view_name = readValue("VIEW_NAME");
      writeProfileValue("VIEW_NAME",readAbsoluteValue("VIEW_NAME"));
   
      where_clause = readValue("WHERE_CLAUSE");
      writeProfileValue("WHERE_CLAUSE",readAbsoluteValue("WHERE_CLAUSE"));

      skip_rows = 0;
      getASPContext().writeValue("SKIP_ROWS","0");
   }

}


