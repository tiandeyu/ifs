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
 * File        : SelectStatment.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Daniel S 2001-Jun-15 - Created.
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    Jacek P  2002-Apr-09 - Corrected bug on clone.
 *    Mangala  2003-Feb-14 - Log Id 1027: Improve the SelectStatement portlet in such way that 
 *                           it can allow HTML code within the select part of the SQL statement.
 *    Rifki R  2004-Jun-17 - Merged Bug 43530, Changed printCustomBody(). 
 *    Suneth M 2006-Aug-18 - Bug 59948, Changed run() to restrict the SQL statements.                          
 *    BuddikaH 2006-Sep-21 - Bug 59842, Changef run(), submitCustomization() to allow CSV values inside sql statements
 * ----------------------------------------------------------------------------
 * New Comments:
 *    buhilk   2007-Feb-09 - Bug 63433, Added csv support
 *    sumelk   2010-Oct-26 - Bug 93718, Changed printContents() to hide the portlet content for b2b template.
 *
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


/**
 * This example shows how to create a block with a table and how to customize
 * a portlet. Even shows how to debug the class and various others possibilities.
 * Customizing the portlet in several steps. Using of PL/SQL functions.
 */
public class SelectStatement extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.SelectStatment");


   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================

//   private ASPContext ctx;
//   private ASPBlock   blk;
//   private ASPRowSet  rowset;
//   private ASPTable   tbl;


   //==========================================================================
   //  Mutable attributes
   //==========================================================================


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient String sql_statement;
   private transient String max_rows;
   private transient String skip_rows;
   private transient String maxtitle;
   private transient String mintitle;
   private transient boolean html_format;

   private transient ASPTransactionBuffer trans;


   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * The only mandatory function - constructor
    */
   public SelectStatement( ASPPortal portal, String clspath )
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
      return super.construct();
   }


   /**
    * Called after define() while changing state from UNDEFINED to DEFINED.
    */
   protected void doFreeze() throws FndException
   {
      super.doFreeze();
   }


   /**
    * Very important function. Called after the execution is completed just before
    * releasing (unlock) the page in the page pool. All temporary and user depended
    * data should be cleared here. All mutable attributes should be restored to
    * their original values here.
    */
   protected void doReset() throws FndException
   {

      super.doReset();
      
      trans        = null;
   }


   /**
    * Called as the first function just after fetching the page from the pool.
    * Almost all that should be done here can be done in the init() function.
    * Not called on instance creation (not cloning).
    */
   protected void doActivate() throws FndException
   {
      super.doActivate();
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

      SelectStatement page = (SelectStatement)(super.clone(obj));

      page.sql_statement= null;
      page.trans        = null;

//      page.ctx    = page.getASPContext();
//      page.blk    = page.getASPBlock(blk.getName());
//      page.rowset = page.blk.getASPRowSet();
//      page.tbl    = page.getASPTable(tbl.getName());

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
      return "SQLSTMTDESC: Select Statement";
   }


   /**
    * Creation of all page objects. Runs only once.
    */
   protected void preDefine() throws FndException
   {

/*       ctx = getASPContext();       */
/*                                    */
/*                                    */
/*       tbl = newASPTable( blk );    */
/*       tbl.disableNoWrap();         */
/*       rowset = blk.getASPRowSet(); */
       appendJavaScript( "function customizeSelectStatement(obj,id)",
		  "{",
		  "   customizeBox(id);",
		  "}\n");

      
      init();
   }


   /**
    * Initialization of variables. Fetching profile information.
    */
   protected void init()
   {
      sql_statement = readProfileValue("SQLSTMT",null);
      max_rows      = readProfileValue("SQLSTMTMAX","100");
      skip_rows     = readProfileValue("SQLSTMTSKIP","0");
      mintitle      = readProfileValue("SQLSTMTMINTITLE", translate("FNDPORTSELECTSTMINTITLE: SELECT Statement"));
      maxtitle      = readProfileValue("SQLSTMTMAXTITLE", translate("FNDPORTSELECTSTMAXTITLE: SELECT Statement"));
      html_format   = readProfileFlag("HTML_FORMAT",false);
   }


   /**
    * Run the business logic here. Just query the database in this case.
    * Also example of using a PL/SQL function.
    */
    protected void run()
    {
        if (!Str.isEmpty(sql_statement)) 
        {
            ASPManager mgr = getASPManager();
            ASPBuffer  commandbuffer = mgr.newASPBuffer();
            trans = mgr.newASPTransactionBuffer();
            commandbuffer = trans.addBuffer("SQLSTMT");
            
            //Replacing CSV Variables within query
            sql_statement = mgr.parseCSVQueryString(sql_statement, true);
            
            if (sql_statement.toUpperCase().startsWith("SELECT")) 
            {
                commandbuffer.addItem("METHOD","Query");
                commandbuffer.addItem("SELECT",sql_statement);
                commandbuffer.addItem("MAX_ROWS",max_rows);
                commandbuffer.addItem("SKIP_ROWS",skip_rows);
                commandbuffer.addItem("META","ALL");
                commandbuffer.addBuffer("DATA");
                commandbuffer.addItem("COUNT_ROWS","Y");
            }
            else
            {
               error(new FndException("FNDPORTLETSELECTSTATEMENTNOTALLOW: Only SELECT statements are allowed."));
            }
            trans  = submit(trans);

        }
    }


   /**
    * Create the portlets title for different modes.
    */
   public String getTitle( int mode )
   {

      if(mode==MINIMIZED)
         return mintitle;
      else 
         return maxtitle;
   }


   /**
    * Print the HTML contents of the portlet.
    */
   public void printContents()
   {
      if ("b2b".equals(getASPConfig().getParameter("APPLICATION/APP_TEMPLATE")))
      {
         printNewLine();
         setFontStyle(BOLD);
         printText("FNDNOSELECTSTMENTBTOB: This portlet is not available for B2B template");
         setFontStyle(NONE);
         printNewLine();
      }
      else
      {
         if( !Str.isEmpty(sql_statement))
         {
            ASPBuffer rowset     = trans.getBuffer("SQLSTMT");
            try{
               int rows    = rowset.countItems();
               if (rows<=0) {
                  printText("Nothing returned.");    	    
               }
               else
               { 
                  ASPBuffer currentrow = rowset.getBufferAt(0);
                  int columns = currentrow.countItems();
                  beginTable();
                  beginTableTitleRow();
                  for(int i=0;i<columns;i++)
                  {
                     printTableCell(currentrow.getNameAt(i),"Left");
                  }
                  endTableTitleRow();
                  beginTableBody();
                  int j;
                  for (j=0;j<rows && rowset.getNameAt(j).equals("DATA");j++) 
                  {
                     currentrow = rowset.getBufferAt(j);
                     for(int i=0;i<columns;i++)
                     {
                        if (html_format)
                        {
                           beginTableCell(); 
                           appendToHTML(currentrow.getValueAt(i)); 
                           endTableCell(); 
                        }
                        else
                           printTableCell(currentrow.getValueAt(i),"Left");
                     }
                     nextTableRow();
                  }
                  endTableBody();
                  endTable();
                  printText(j+" rows. "+skip_rows+" skipped.");
	       }
            }
            catch( Throwable any )
            {
               error(any);
            }
         }
         else
         {
            printNewLine();
            printText("SELSTMTCMP1: Enter a SQL Statement in the");
            printSpaces(1);
            printScriptLink("SELSTMTCMP2: Customization", "customizeSelectStatement");
            printSpaces(1);
            printText("SELSTMTCMP3: page.");
         }
      }
   }


   /**
    * If the portlet should be customizable this function must return true.
    */
   public boolean canCustomize()
   {
      return true;
   }


   /**
    * Run the business logic for the customize mode.
    */
   public void runCustom()
   {
   }


   /**
    * Print the HTML code for the customize mode.
    */
   public void printCustomBody() throws FndException //JAPA
   {
        printNewLine();
        printNewLine();
        printSpaces(5);

        printText("SELSTMTCOMPID1: Type your SELECT Statment:");
        printNewLine();
        printSpaces(5);
        printTextArea( "SQLSTMT", readAbsoluteProfileValue("SQLSTMT",""), 5, 130, null );
        printNewLine();
        printSpaces(5);
        printText("Max rows: ");
        printField( "SQLSTMTMAX", max_rows, 10, 10, null );
        printSpaces(5);
        printText("Skip rows: ");
        printField( "SQLSTMTSKIP", skip_rows, 10, 10, null );
        printSpaces(5);
        String html_alert = getASPManager().translateJavaScript("FNDPORTLETSSELECTBHTMLALERT: Please note that HTML syntax errors in your select statement \nor data with HTML syntax might cause problems.");
        printCheckBox("HTML_FORMAT","Y",html_format,"if (this.checked) alert('"+ html_alert +"');");
        printSpaces(1);
        printText("FNDPORTLETSSELECTBHTMLFORMAT: Allow HTML syntax in select statment");
        printNewLine();
        printNewLine();

        beginTransparentTable();
        beginTableTitleRow();
        printTableCell("SQLSTMTTITLEMSG: You can choose your own title for this portlet",3,0,LEFT);
        endTableTitleRow();

        beginTableBody();
        beginTableCell();
        printSpaces(5);
        printText("SQLSTMTMAXTITLE: When maximized:");
        endTableCell();
        beginTableCell(2);
        printField("SQLSTMTMAXTITLE",maxtitle,50);
        endTableCell();
        nextTableRow();
        beginTableCell();
        printSpaces(5);
        printText("SQLSTMTMINTITLE: When minimized:");
        endTableCell();
        beginTableCell(2);
        printField("SQLSTMTMINTITLE",mintitle,50);
        endTableCell();
        endTableBody();
        endTable();

        printNewLine();
   }


   /**
    * Save values of variables to profile if the user press OK button.
    */
   public void submitCustomization()
   {
       //Reading CSV compatible statements
       sql_statement = readValue("SQLSTMT");
       writeProfileValue("SQLSTMT", readAbsoluteValue("SQLSTMT") );
       max_rows = readValue("SQLSTMTMAX");
       writeProfileValue("SQLSTMTMAX", max_rows );
       skip_rows = readValue("SQLSTMTSKIP");
       writeProfileValue("SQLSTMTSKIP", skip_rows );
       maxtitle = readValue("SQLSTMTMAXTITLE");
       writeProfileValue("SQLSTMTMAXTITLE", maxtitle );
       mintitle = readValue("SQLSTMTMINTITLE");
       writeProfileValue("SQLSTMTMINTITLE", mintitle );
       html_format = "Y".equals(readValue("HTML_FORMAT"));
       writeProfileFlag("HTML_FORMAT",html_format);
   }
}