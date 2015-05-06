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
 * File        : QuickReports.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    rahelk  2003-Jan-21 - Created.
 *    rahelk  2003-Mar-16 - Opened hyper link in NEWWIN.
 *    chaalk  2003-Sep-03 - New GUI,added images and type column depending on the type
 *    chaalk  2003-Sep-30 - Removed the type column and add tooltips for the image
 *    chaalk  2003-Oct-06 - Removed unnecessary translations
 *    rahelk  2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    Chandana2004-May-13 - Updated for the use of new style sheets.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/09/15 VOHELK - Bug 93000, Introduced new view, inorder to filter out new QR types 
 * 2009/04/24 amiklk, changed the preDefine(), init(), run(), and printContents() to support Next & Prev navigate links.
 * 2007/06/22 sumelk
 * Merged the corrections for Bug 65887, Changed printCustomBody().
 *
 * Revision 1.6  2007/02/19 14:59:06  buhilk
 * Bug 63433, Added csv support
 *
 * Revision 1.5  2006/09/18 04:00:00  chaalk
 * Bug 58703, Reinstate Crystal web integration
 *
 * Revision 1.4  2005/11/11 03:14:03  mapelk
 * Bug fix after removing CR dependencies from Quick Reports
 *
 * Revision 1.3  2005/11/10 10:47:10  mapelk
 * Remove CR dependencies from Quick Reports
 *
 * Revision 1.2  2005/11/10 09:41:36  sumelk
 * Merged the corrections for Bug 53544, Changed printCustomBody(),doReset() & init().
 *
 * Revision 1.1  2005/09/15 12:38:02  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/15 09:24:08  mapelk
 * JAAS related bug fix. Error when use includeJavaScriptFile() in old fashion.
 *
 * Revision 1.2  2005/05/06 09:56:43  mapelk
 * changes required to make standard portlets (JSR 168) to run on WebSphere
 * 
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;

import java.util.*;

/**
 * This example shows how to create a block with a table and how to customize
 * a portlet. Even shows how to debug the class and various others possibilities.
 * Customizing the portlet in several steps. Using of PL/SQL functions.
 */
public class QuickReports extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.QuickReports");

   private final static int default_size = 15;
   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================

   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;
   private ASPCommandBar cmdbar;
   private String    imgloc;

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================
   private transient ASPBuffer portlet_categories;
   private transient ASPTransactionBuffer trans;
   private transient int      skip_rows;
   private transient int      db_rows;
   private transient String   view_size;
   private transient int      size;
   
   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================
   private transient ASPBuffer all_categories;
   private transient String selected_categories;
   private transient String title;
   private transient String attname1;
   private transient String attvalue1;
   private transient String attname2;
   private transient String attvalue2;
   private transient String attname3;
   private transient String attvalue3;
   private transient String attname4;
   private transient String attvalue4;
   private transient String attrib_url;
   private transient String max_rows;
   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * The only mandatory function - constructor
    */
   public QuickReports( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * The first method to be called in this class after construction.
    * Not called on cloning.
    */
   public ASPPage construct() throws FndException
   {
      if(DEBUG) debug(this+": QuickReports.construct()");
      return super.construct();
   }



   /**
    * Very important function. Called after the execution is completed just before
    * releasing (unlock) the page in the page pool. All temporary and user depended
    * data should be cleared here. All mutable attributes should be restored to
    * their original values here.
    */
   protected void doReset() throws FndException
   {
      if(DEBUG) debug(this+": QuickReports.doReset()");

      super.doReset();
      trans = null;
      title = null;
      all_categories = null;
      selected_categories = null;
      portlet_categories = null;
      attname1 = null;
      attvalue1 = null;
      attname2 = null;
      attvalue2 = null;
      attname3 = null;
      attvalue3 = null;
      attname4 = null;
      attvalue4 = null;
      attrib_url = null;
      max_rows = null;
      view_size = null;
   }

   /**
    * Create a new instance if all existing instances of the same page are already
    * locked in the pool. Cloning is done by copying and cloning values and objects
    * from another (locked) instance. All temporary variables and mutable attributes
    * should be initialized here. All page components that inherits from the
    * ASPPoolElement class are already cloned by the super class. Just initialize
    * corresponding variables.
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if(DEBUG) debug(this+": QuickReports.clone("+obj+")");

      QuickReports page = (QuickReports)(super.clone(obj));

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());
      page.cmdbar = page.blk.getASPCommandBar();

      return page;
   }

   //==========================================================================
   //  Other methods
   //==========================================================================

   /**
    * Description that will be shown on the 'Customize Portal' page.
    */
   public static String getDescription()
   {
      return "FNDPORTQUICKRPTDESC: Quick Reports";
   }


   /**
    * Creation of all page objects. Runs only once.
    */
   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": QuickReports.preDefine()");

      imgloc    = getImagesLocation();

      ctx = getASPContext();

      blk = newASPBlock("MAIN");

      blk.setView("QUICK_REPORT_NON_BI");
      
      rowset = blk.getASPRowSet();

      cmdbar = blk.newASPCommandBar();

      includeJavaScriptFile(getScriptsLocation().substring(getASPConfig().getApplicationContext().length()) + "QuickReportUtility.js");
      
      AutoString javaScript = new AutoString();

      javaScript.append("function prevQuickReports(obj,id) \n","{ \n","   getPortletField(id,'CMD').value = 'PREV'; \n","}\n");
      javaScript.append("function nextQuickReports(obj,id) \n","{ \n","   getPortletField(id,'CMD').value = 'NEXT'; \n","}\n");
      appendJavaScript(javaScript.toString());
      
      init();
   }


   /**
    * Initialization of variables. Fetching profile information.
    */
   protected void init()
   {
      ASPManager mgr = getASPManager();  
      title = readProfileValue("TITLE", translate(getDescription()) );
      selected_categories = readProfileValue("CATEGORIES","");
      
      attname1  = readProfileValue("ATTNAME1","");
      if (!mgr.isEmpty(attname1))
         attvalue1 = readProfileValue(attname1);
      
      attname2  = readProfileValue("ATTNAME2","");
      if (!mgr.isEmpty(attname2))
         attvalue2 = readProfileValue(attname2);
      
      attname3  = readProfileValue("ATTNAME3","");
      if (!mgr.isEmpty(attname3))
         attvalue3 = readProfileValue(attname3);
      
      attname4  = readProfileValue("ATTNAME4","");
      if (!mgr.isEmpty(attname4))
         attvalue4 = readProfileValue(attname4);

      max_rows = readProfileValue("MAXROWS", mgr.getConfigParameter("ADMIN/BUFFER_SIZE"));
      
      attrib_url = "";
      if (!mgr.isEmpty(attname1) && !mgr.isEmpty(attvalue1))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname1) + "=" + mgr.URLEncode(attvalue1);
      if (!mgr.isEmpty(attname2) && !mgr.isEmpty(attvalue2))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname2) + "=" + mgr.URLEncode(attvalue2);
      if (!mgr.isEmpty(attname3) && !mgr.isEmpty(attvalue3))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname3) + "=" + mgr.URLEncode(attvalue3);
      if (!mgr.isEmpty(attname4) && !mgr.isEmpty(attvalue4))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname4) + "=" + mgr.URLEncode(attvalue4);
      if (!mgr.isEmpty(attrib_url))
         attrib_url = attrib_url + "&BYPASS_PAGE=TRUE";
      if (!mgr.isEmpty(max_rows))
         attrib_url = attrib_url + "&MAXROWS=" + max_rows;

      view_size = readProfileValue("VIEW_SIZE", default_size+"");
      size = Integer.parseInt(view_size);
      
      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

      String cmd = readValue("CMD");

      if ("PREV".equals(cmd) && skip_rows>=size)
         skip_rows -= size;
      else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
         skip_rows += size;
     
      ctx.writeValue("SKIP_ROWS",skip_rows+"");
   }


   /**
    * Run the business logic here. Just query the database in this case.
    * Also example of using a PL/SQL function.
    */
   protected void run()
   {
      view_size = readProfileValue("VIEW_SIZE", default_size+"");
      size = Integer.parseInt(view_size);      
      
      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      
      ASPQuery q = trans.addQuery(blk);

      q.setBufferSize( size );
      q.skipRows(skip_rows);
      
      q.includeMeta("ALL");
      //String where ="QR_TYPE_DB='SQL'";
      if (!mgr.isEmpty(selected_categories))
         q.addWhereCondition("CATEGORY_ID in ("+getWhereCondition()+")");
         //where+=" and CATEGORY_ID in ("+getWhereCondition()+")";
      //else
      
      q.setOrderByClause("DESCRIPTION");
      //q.addWhereCondition(where);
      trans = submit(trans);
      
      db_rows = blk.getASPRowSet().countDbRows();
      getASPContext().writeValue("DB_ROWS", db_rows+"" );
   }
   
   private String getWhereCondition()
   {
      ASPManager mgr = getASPManager();

      String where_condition = selected_categories.substring(0,selected_categories.length()-1);
      String[] category_id_array = split(where_condition,",");
      
      //reverse string to get records in customized order
      where_condition ="";
      for (int i=category_id_array.length-1; i>=0; i--)
         where_condition += category_id_array[i] + ",";

      where_condition = where_condition.substring(0,where_condition.length()-1);
      return where_condition;
   }


   /**
    * Create the portlets title for different modes.
    */
   public String getTitle( int mode )
   {
      if(mode==MINIMIZED || mode==MAXIMIZED)
         return title;
      else
         return translate("FNDPORTQUICKRPTCUSTTIT: Customize ")+title;
   }


   /**
    * Print the HTML contents of the portlet.
    */
   public void printContents() throws FndException
   {
      printHiddenField("CMD","");

      ASPManager mgr = getASPManager();

      if (rowset.countRows() > 0)
      {
         String quickReportType,categoryDescription,description,quickReportId;
         
         rowset.goTo(0);

         beginTable();
            beginTableTitleRow();
               beginTableCell(0,0,CENTER,null,true);
                     setFontStyle(BOLD);
                     printText("FNDPORTQUICKRPTCATEGORYTITLE: Category");
                     setFontStyle(NONE);
               endTableCell();
               beginTableCell(0,0,CENTER,null,true);
                     printText("");
               endTableCell();
               beginTableCell(0,0,CENTER,null,true);
                     setFontStyle(BOLD);
                     printText("FNDPORTQUICKRPTDESCRIPTIONTITLE: Description");
                     setFontStyle(NONE);
               endTableCell();
            endTableTitleRow();
            beginTableBody();
               for(int rowCount=0;rowCount<rowset.countRows();rowCount++)
               {
                  quickReportType     = rowset.getValue("QR_TYPE_DB");
                  categoryDescription = rowset.getValue("CATEGORY_DESCRIPTION");
                  description         = rowset.getValue("DESCRIPTION");
                  quickReportId       = rowset.getValue("QUICK_REPORT_ID");
                  rowset.next();
   
                  beginTableCell(0,0,LEFT,null,true);
                     printText(categoryDescription);
                  endTableCell();
   
                  if (quickReportType.equals("SQL"))
                  {
                     beginTableCell(0,0,LEFT,null,true);
                        appendToHTML("<img src=\""+imgloc+"sql.gif"+"\" border=0 alt=\""+ mgr.translate("FNDPORTQUICKRPTTYPESQL: SQL Statement") +"\" title=\""+ mgr.translate("FNDPORTQUICKRPTTYPESQL: SQL Statement") +"\">");
                     endTableCell();
                  }
                  else if (quickReportType.equals("CR"))
                  {
                     beginTableCell(0,0,LEFT,null,true);
                        appendToHTML("<img src=\""+imgloc+"cr.gif"+"\" border=0 alt=\""+ mgr.translate("FNDPORTQUICKRPTTYPECR: Crystal Reports") +"\" title=\""+ mgr.translate("FNDPORTQUICKRPTTYPECR: Crystal Reports") +"\">");
                     endTableCell();
                  }
                  else
                  {
                     beginTableCell(0,0,LEFT,null,true);
                        appendToHTML("<img src=\""+imgloc+"ca.gif"+"\" border=0 alt=\""+ mgr.translate("FNDPORTQUICKRPTTYPECA: Crystal Analysis") +"\" title=\""+ mgr.translate("FNDPORTQUICKRPTTYPECA: Crystal Analysis") +"\">");
                     endTableCell();
                  }
   
                  beginTableCell(0,0,LEFT,null,true);
                  if (quickReportType.equals("SQL"))
                     appendToHTML("<a class=hyperLink href= javascript:showNewBrowser('QuickReportQuery.page?QUICK_REPORT_ID="+quickReportId+attrib_url+"')>" + description + "</a>");
                  else
                     appendToHTML("<a class=hyperLink href= javascript:showNewBrowser('QuickReportQuery.page?QUICK_REPORT_ID="+quickReportId+"')>" + description + "</a>");
                  endTableCell();
                  nextTableRow();
               }
            
            endTableBody();
            endTable(); 
            
            if (size < db_rows)
            {
               printNewLine();
               if (skip_rows>0)
                  printSubmitLink("FNDPORTQUICKRPTPRV: Previous","prevQuickReports");
               else
                  printText("FNDPORTQUICKRPTPRV: Previous");

               printSpaces(5);
               String rows = translate("FNDPORTQUICKRPTROWS: (Rows &1 to &2 of &3)",
                                       (skip_rows+1)+"",
                                       (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                       db_rows+"");
               printText( rows );
               printSpaces(5);

               if (skip_rows<db_rows-size)
                  printSubmitLink("FNDPORTQUICKRPTNEXT: Next","nextQuickReports");
               else
                  printText("FNDPORTQUICKRPTNEXT: Next");

               printNewLine();
               printNewLine();
            }           
      }
      else
         printText("FNDPORTQUICKRPTNODATA: No data found");
   }


   /**
    * If the portlet should be customizable this function must return true.
    */
   public boolean canCustomize()
   {
      if(DEBUG) debug(this+": QuickReports.canCustomize()");
      return true;
      //return false;
   }

 
   /**
    * Run the business logic for the customize mode.
    */
   public void runCustom()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      trans.clear();
      ASPQuery qNumRows = trans.addQuery("CATEGORYCOUNT","SELECT COUNT(*) CT FROM REPORT_CATEGORY");
      trans = submit(trans);
      ASPBuffer bufNumCat = trans.getBuffer("CATEGORYCOUNT");
      String sNumRows = bufNumCat.getBufferAt(0).getValue("CT");     
      
      trans.clear();
      ASPQuery q = trans.addQuery("CATEGORYLIST","REPORT_CATEGORY","CATEGORY_ID, DESCRIPTION","","DESCRIPTION");
      q.includeMeta("ALL");

      try{
         int numRows = Integer.parseInt(sNumRows);
         q.setBufferSize(numRows);
      }catch(Exception e) {}

      trans = submit(trans);
      all_categories = trans.getBuffer("CATEGORYLIST");

      formatBuffers();
   }

   private void formatBuffers()
   {
      ASPManager mgr = getASPManager();
      portlet_categories = mgr.newASPBuffer();
      
      ASPBuffer row;
      String category_id;
         
      if (!mgr.isEmpty(selected_categories))
      {
         selected_categories = selected_categories.substring(0,selected_categories.length()-1);
         StringTokenizer st_category_ids = new StringTokenizer(selected_categories,",");
         
         selected_categories ="";
         while( st_category_ids.hasMoreTokens() )
         {
            category_id = st_category_ids.nextToken();
            for (int i=0;i<all_categories.countItems();i++)
            {
               row = all_categories.getBufferAt(i);
               if (category_id.equals(row.getValue("CATEGORY_ID")))
               {
                  portlet_categories.addBuffer("DATA",row);
                  selected_categories += category_id +",";
                  all_categories.removeItemAt(i);
               }
            }
         }
         //writeProfileValue("CATEGORIES",selected_categories);
      }
   }
   
   /**
    * Print the HTML code for the customize mode.
    */
   public void printCustomBody() throws FndException //JAPA
   {
      ASPManager mgr = getASPManager();
      boolean is_explorer = mgr.isExplorer();
      
      printNewLine();
      setFontStyle(BOLD);
      
      printText("FNDPORTQUICKRPTMYTITLE: You can choose your own title for this portlet:");
      printNewLine();
      printField("TITLE",title,50);
      
      printNewLine();
      printNewLine();
   
      beginTable("showCategoryTable",true,false);
        beginTableBody();

        beginTableCell(3);
            setFontStyle(BOLD);
            printText("FNDPORTQUICKRPTREPORTCATS: Report Categories");
        endTableCell();

        beginTableCell();
        endTableCell();
        
        beginTableCell(3);
            setFontStyle(BOLD);
            printText("FNDPORTQUICKRPTSELECTEDCATS: Selected Categories");
        endTableCell();

        beginTableCell();
        endTableCell();

        nextTableRow();
        
        beginTableCell(3);
        appendToHTML("<select multiple name=col_0 size=10 class='selectbox'>\n");
        
        ASPBuffer row;
        for (int i=0; i<all_categories.countItems()-1;i++)
        {
           row = all_categories.getBufferAt(i);
           appendToHTML("<option value=");
           appendToHTML(row.getValue("CATEGORY_ID"));
           appendToHTML(" >"+row.getValue("DESCRIPTION")+"</option>\n");
        }

        appendToHTML("<option>______________________</option>\n");
        appendToHTML("</select>\n");
        endTableCell();

        beginTableCell();
        printImageLink(getImagesLocation()+"portlet_right.gif","javascript:moveItemsSide('col_0','col_1')");
        
        appendToHTML("<BR><BR>");
   
        printImageLink(getImagesLocation()+"portlet_left.gif","javascript:moveItemsSide('col_1','col_0')");        
        endTableCell();

        beginTableCell(3);
        
        appendToHTML("<select multiple name=col_1 size=10 class='selectbox'>\n");

        for (int i=0; i<portlet_categories.countItems();i++)
        {
           row = portlet_categories.getBufferAt(i);
           appendToHTML("<option value=");
           appendToHTML(row.getValue("CATEGORY_ID"));
           appendToHTML(" >"+row.getValue("DESCRIPTION")+"</option>\n");
        }

        appendToHTML("<option>______________________</option>\n");
        appendToHTML("</select>\n");
        endTableCell();
        beginTableCell();
        printImageLink(getImagesLocation()+"portlet_up.gif","javascript:moveItemUp('col_1')");
        appendToHTML("<BR><BR>");        
        printImageLink(getImagesLocation()+"portlet_down.gif","javascript:moveItemDown('col_1')");    
        endTableCell();
        endTableBody();
      endTable();

      printNewLine();
      beginTable("showNumberOfReportsTable", true, false);
         beginTableBody();
            beginTableCell();
               setFontStyle(BOLD);
               printText("FNDPORTQUICKRPTVIEWSIZE: Max Rows to Display : ");
               endFont();
            endTableCell();
            beginTableCell();
               printField("VIEW_SIZE",view_size,10);
            endTableCell();
         endTableBody();
      endTable();      
      
      printNewLine();      
      beginTable("showAttributeTable",true,false);
        beginTableBody();
        beginTableCell(3);
            setFontStyle(BOLD);
            printText("FNDPORTQUICKRPTATTRIBUTES: Attributes");
            endFont();
        endTableCell();
        nextTableRow();
        beginTableCell();
            printText("FNDPORTQUICKRPTATTRIBUTENAME: Attribute");
        endTableCell();
        beginTableCell();
            printText("FNDPORTQUICKRPTATTRIBUTEVALUE: Value");
        endTableCell();
        nextTableRow();
        beginTableCell();
            printField("ATTNAME1",attname1,20);
        endTableCell();
        beginTableCell();
            printField("ATTVALUE1",attname1!=null ? readAbsoluteProfileValue(attname1): null,10);
        endTableCell();
        nextTableRow();
        beginTableCell();
            printField("ATTNAME2",attname2,20);
        endTableCell();
        beginTableCell();
            printField("ATTVALUE2",attname2!=null ? readAbsoluteProfileValue(attname2): null,10);
        endTableCell();
        nextTableRow();
        beginTableCell();
            printField("ATTNAME3",attname3,20);
        endTableCell();
        beginTableCell();
            printField("ATTVALUE3",attname3!=null ? readAbsoluteProfileValue(attname3): null,10);
        endTableCell();
        nextTableRow();
        beginTableCell();
            printField("ATTNAME4",attname4,20);
        endTableCell();
        beginTableCell();
            printField("ATTVALUE4",attname4!=null ? readAbsoluteProfileValue(attname4): null,10);
        endTableCell();
        endTableBody();
      endTable();
      printNewLine();      
      printText("FNDPORTQUICKRPTMAXNOOFROWSTOSHOW: Maximum rows to show in SQL reports");
      printSpaces(3);
      printField("MAXROWS",max_rows,10);
      
      printHiddenField("SELECTED_CATEGORIES",selected_categories);
   }


   /**
    * Save values of variables to profile if the user press OK button.
    */
   public void submitCustomization()
   {
      ASPManager mgr = getASPManager();

      title      = readValue("TITLE");
      writeProfileValue("TITLE", title);  
      
      selected_categories = readValue("SELECTED_CATEGORIES");
      writeProfileValue("CATEGORIES",selected_categories);
      
      view_size     = readValue("VIEW_SIZE", default_size+"");
      
      try{
         size = Integer.parseInt(view_size);
      } catch(Exception ne) { }      
      
      if(size<1) size = 15;
      writeProfileValue("VIEW_SIZE", size+"");  
      
      ctx.writeValue("SKIP_ROWS","0");
      
      attname1      = readValue("ATTNAME1","");
      writeProfileValue("ATTNAME1", attname1);  
      attvalue1      = readValue("ATTVALUE1","");
      if (!mgr.isEmpty(attname1))
         writeProfileValue(attname1, readAbsoluteValue("ATTVALUE1",""));  
      
      attname2      = readValue("ATTNAME2","");
      writeProfileValue("ATTNAME2", attname2);  
      attvalue2      = readValue("ATTVALUE2","");
      if (!mgr.isEmpty(attname2))
         writeProfileValue(attname2, readAbsoluteValue("ATTVALUE2",""));  
      
      attname3      = readValue("ATTNAME3","");
      writeProfileValue("ATTNAME3", attname3);  
      attvalue3      = readValue("ATTVALUE3","");
      if (!mgr.isEmpty(attname3))
         writeProfileValue(attname3, readAbsoluteValue("ATTVALUE3",""));  
      
      attname4      = readValue("ATTNAME4","");
      writeProfileValue("ATTNAME4", attname4);  
      attvalue4      = readValue("ATTVALUE4","");
      if (!mgr.isEmpty(attname4))
         writeProfileValue(attname4, readAbsoluteValue("ATTVALUE4",""));  

      max_rows      = readValue("MAXROWS",mgr.getConfigParameter("ADMIN/BUFFER_SIZE"));
      
      attrib_url = "";
      if (!mgr.isEmpty(attname1) && !mgr.isEmpty(attvalue1))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname1) + "=" + mgr.URLEncode(attvalue1);
      if (!mgr.isEmpty(attname2) && !mgr.isEmpty(attvalue2))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname2) + "=" + mgr.URLEncode(attvalue2);
      if (!mgr.isEmpty(attname3) && !mgr.isEmpty(attvalue3))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname3) + "=" + mgr.URLEncode(attvalue3);
      if (!mgr.isEmpty(attname4) && !mgr.isEmpty(attvalue4))
         attrib_url = attrib_url + "&" + mgr.URLEncode(attname4) + "=" + mgr.URLEncode(attvalue4); 
      if (!mgr.isEmpty(attrib_url))
         attrib_url = attrib_url + "&BYPASS_PAGE=TRUE";
      if (!mgr.isEmpty(max_rows))
         attrib_url = attrib_url + "&MAXROWS=" + max_rows;

      writeProfileValue("MAXROWS", max_rows);  
   }
}