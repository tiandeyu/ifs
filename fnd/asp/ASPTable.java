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
 * File        : ASPTable.java
 * Description : An ASPObject that represents a multi-row HTML table
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-Apr-14 - Created
 *    Marek D  1998-May-14 - Moved definition of Commands to ASPBlock
 *    Jacek P  1998-May-20 - Moved HTML generation to a private class
 *                           HTMLGenerator. Added new functionality.
 *    Marek D  1998-May-30 - Implemented ASPBufferable interface
 *    Jacek P  1998-Jun-02 - Added generation of hidden fields for hidden
 *                           columns. Added new function getKeyColumnName().
 *    Micke A  1998-Jun-03 - Did some tuning of the HTML presentation.
 *    Jacek P  1998-Jun-04 - Added handling of check boxes and global connections.
 *                           Minor modifications.
 *    Marek D  1998-Jun-15 - Removed unused functions
 *                           New features: Keep state of CheckBoxes, Hyperlinks
 *    Marek D  1998-Jun-25 - LOV in Search Row
 *    Micke A  1998-Jun-26 - Added handling of the images for ASPTable row status
 *    Marek D  1998-Jun-29 - Use DbName while populating data
 *                           New method setKey()
 *    Micke A  1998-Jul-08 - Removed the "green color marking" of selected rows.
 *    Micke A  1998-Jul-08 - "addSystemColumn" will now not create the system column
 *                           if both record checking and status info have been disabled.
 *    Marek D  1998-Jul-13 - Use URLEncode() while generating hyperlinks
 *    Jacek P  1998-Jul-15 - Added Quick Edit functionality (Idea #70).
 *                           Check box column shows now as image. Alignment of column
 *                           according to ASPField definition.
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-17 - Handling of hyperlinks in a new browser (log id #101)
 *    Marek D  1998-Aug-19 - Added closing ">" after LOV in Search Row
 *    Jacek P  1998-Aug-20 - Changes due to redesigned structure of
 *                           the ASPConfig.ifm file (Log id:#2623).
 *    Marek D  1998-Aug-21 - More changes in structure of ASPConfig.ifm
 *    Marek D  1998-Aug-26 - Use mgr.preapareURL() for hyperlinks (Bug #2653)
 *                           Apply UpperCase in Search Row
 *    Jacek P  1998-Sep-29 - Added handling of fields queryable flag (ToDo #2718).
 *                           Methods for enabling/disabling title row and table border.
 *    Jacek P  1998-Sep-30 - BGCOLOR and ALIGN attributes moved away from the TD tag.
 *                           Better formatting of the table.
 *    Jacek P  1998-Oct-01 - COLS=n removed from the TABLE tag.
 *    Jacek P  1998-Oct-21 - Added call to Util.trimLine() in
 *                           generateSearchRow() (Bug: #2816).
 *    Jacek P  1998-Oct-22 - Added call to ASPManager.HTMLEncode() function during
 *                           generation of table title and data (Bug: #2825).
 *    Jacek P  1998-Nov-19 - Added WIDTH and HEIGHT attributes to all <IMG> tags
 *                           (ToDo: #2834).
 *    Micke A  1998-Nov-25 - Improved HTML generation using CSS (ToDo: #2944).
 *    Jacek P  1998-Dec-08 - Function appendColumnValue() generates hyperlink
 *                           only if column value is not empty (Bug: #2997).
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Feb-22 - Added persistent attribute 'name'. New constructors.
 *    Marek D  1999-Mar-05 - Implementation of ASPPoolElement state diagram
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Marek D  1999-Apr-30 - Added mutable attribute 'profile'
 *    Marek D  1999-May-10 - Decreased number of 'new' Objects during populate
 *                           New mutable attribute 'editable'
 *                           New temporary arrays with immutable size:
 *                               column_type, column_pos, column_hyperlink
 *                           Renamed appendColumnValue() to appendTextField()
 *    Marek D  1999-May-28 - Store editable column list in context
 *                           Added Edit-Properties command
 *    Jacek P  1999-Jun-01 - Added method removeProfile()
 *    Marek D  1999-Jun-07 - Generate dummy OnChange handler for not
 *                           queryable hidden fields (Netscape bug)
 *                           getTitle() returns getName() for undefined title
 *                           New immutable attribute 'sort_function'
 *    Marek D  1999-Jun-10 - New mutable attribute 'sortable'
 *    Marek D  1999-Jun-11 - Corrected logic in prepareProfileInfo()
 *                           Removed URLEncode() from Edit-Properties HREF tag
 *                           Do not generate dummy OnChange for CheckBoxes
 *    Jacek P  1999-Jun-15 - Property button should only be shown for 'new' pages.
 *                           QuickEdit function should call the 'toggle()' function
 *                           for 'new' pages. New icon and tool tip for property
 *                           button. Changes in functions: addSystemColumn()
 *                           showNoLovSystemColumnContents() and appendEditPropertiesCommand().
 *    Jacek P  1999-Jul-13 - Added hyperlink icon.
 *    Jacek P  1999-Jul-21 - Added row number in addSystemColumn().
 *                           Hyperlink icon shown only if value is not null.
 *    Jacek P  1999-Aug-02 - Added block name to the '_ROWSTATUS' hidden field.
 *    Jacek P  1999-Aug-09 - Added control of the new row status 'QueryMode__'
 *                           in generateSearchRow().
 *    Chaminda &
 *    Mangala  1999-Jun-18 - Modify addSystemColumn() function to insert Spred-Sheet icon.
 *    Jacek P  1999-Aug-09 - Included C&M:s code with some modifications.
 *                           New function appendOutputChannelsCommand().
 *    Jacek P  1999-Aug-10 - Added tool tips on all icons.
 *    Jacek P  1999-Aug-13 - Added space before the row number in addSystemColumn().
 *    Jacek P  1999-Sep-01 - Changed code generation for system column in method
 *                           addSystemColumn(). Double <A> tag for sorted columns in
 *                           generateTitleRow(). Added generation of JavaScript function
 *                           check<blkname>TableFields() in tableDefinitionEnd().
 *    Jacek P  1999-Sep-01 - New parameter bgcolor2 initialized in fetchConfigParameters()
 *                           for alternative background color (striped background) used in
 *                           populate(). Function appendInputField() can now accept
 *                           background for read only fields as parameter. Corrected bug
 *                           in createHyperlinkColumn(): using getDbName() instead of getName().
 *    Jacek P  1999-Sep-07 - Variable show_border get default value from ASPConfig.ifm.
 *                           Using of title_row_* variables in generateTitleRow() and
 *                           addSystemColumn() instead of hard coding or style sheet.
 *                           New variables cellpad_* initialized in fetchConfigParameters()
 *                           and used in tableDefinitionBegin().
 *                           Added new public functions (un)setStripedBackground() and
 *                           a new variable striped_background initialized from ASPConfig.ifm.
 *    Jacek P  1999-Sep-08 - New functions enable/disableRowCounter(). QuickEdit as hyperlink
 *                           on row counter - changes in showNoLovSystemColumnContents() and
 *                           addSystemColumn().
 *    Jacek P  1999-Sep-09 - Page size divided into two parameters: for editable mode and readonly.
 *    Jacek P  1999-Sep-10 - NOWRAP on all TD cells in function appendTDTag().
 *                           New parameter to addSystemColumn(). Changes in populate() - hidden
 *                           fields placed inside TD tag (except for search row).
 *    Reine A  1999-Dec-20 - Added functionality for context handling of visible columns in
 *                           function populate.
 *    Stefan M 1999-Dec-29 - Added setWrap(), unsetWrap() functions for wrapping cell content
 *                           (non-editable textfields only)- new mutable attribute "wrap".
 *                           Problem with rows shrinking when row checkbox not present fixed using
 *                           a transparent image.
 *    Stefan M 2000-Jan-09 - Added setGridHead(), setGridContent() functions - sets the CSS style
 *                           to use for the table. Functions for letting the user do the HTML
 *                           added.
 *    Stefan M 2000-Jan-14 - Added another user HTML generation function, getNextColumn().
 *    Stefan M 2000-Jan-19 - Added functions for setting column formatting manually. setGridContent()
 *                           bug fixed. setGridHead() and setGridContent() renamed; "Style" was
 *                           appended, for clarification.
 *    Stefan M 2000-Jan-20 - Fixed getNextColumn(); now only retrieves non-hidden columns.
 *    Jacek P  2000-Jan-21 - Implementation of portal:
 *                           call to ASPManager.readValue() replaced with ASPPage.readValue()
 *                           in generateSearchRow().
 *    Johan S  2000-Feb-18 - Disabled the Queryrow for version 3.
 *    Stefan M 2000-Mar-23 - Variable show_border now gets value from AUDIT/SHOW_BORDER.
 *    Stefan M 2000-Mar-24 - Field wrapping improved. Titlerow is now also wrapped if setTitleWrap()
 *                           for the ASPField has been used.
 *    Stefan M 2000-Mar-29 - Table properties icon is now not shown in LOVs.
 *    Stefan M 2000-Apr-27 - Variable show_border now gets value from AUDIT/__DEBUG/SHOW_BORDER.
 *    Stefan M 2000-May-02 - Extra space cells added between all columns.
 *    Stefan M 2000-May-03 - Added disableNoWrap().
 *    Stefan M 2000-May-04 - Added disableTitleNoWrap().
 *    Jacek P  2000-May-08 - Changes in tableDefinitionEnd(). Added enable/disableOutputChannels().
 *    Stefan M 2000-May-11 - Added support for remembering state of collapsed block (ASPBlockLayout).
 *    Jacek P  2000-May-22 - Changes in tableDefinitionEnd(). Support for select-check boxes.
 *    Stefan M 2000-Jul-06 - Support for initially minimized blocks added.
 *    Stefan M 2000-Jul-12 - The url part of hyperlinks are not urlencoded now, since this converted slashes.
 *    Stefan M 2000-Jul-14 - Edit properties and Output channels smacked together into a menu.
 *    Stefan M 2000-Dec-19 - Added Document Management support in row menu.
 *    Stefan M 2001-Mar-14 - The queryrow can now be enabled again.
 *    Jacek P  2001-Mar-29 - Changed .asp to .page
 *    Chandana 2002-Jul-30 - Overloaded appendTDTag() method to facilitate Tooltips
 *    Ramila H 2002-Jul-30 - Log Id 875. Added support for calendat tag generation.
 *    Ramila H 2002-Jul-31 - Log Id 827. Removed HTMLEncode for Label to enable translatable wrapping.
 *    Mangala  2002-Aug-08 - Log Id 971. Added support for textareas in editable tables.
 *    Sampath  2002-Aug-28 - skip tab for readonly fields, modified appendInputField();
 *    Mangala  2002-Sep-04 - Minor improvement for tooltips
 *    Daniel   2002-Sept-05 - Highlight selected row.
 *    Rifki R  2002-Oct-15 - Minor fix in populate() when appending column names to _VISIBLE. Column names
 *                           should be appended only during the first row (similar to col_width).
 *    Ramila H 2002-Oct-30 - Log Id 982. Added method isRowSelectionEnabled.
 *    Suneth M 2002-Nov-04 - Changed showNoLovSystemColumnContents().
 *    Sampath  2002-Nov-07 - made changes to allow selectAll, DeselectAll,invertSelecton menus to be displayed
 *    Suneth M 2002-Dec-11 - Log Id 1002, Changed addSystemColumn() to enable the select-check boxes in LOV's.
 *    ChandanaD2002-Dec-26 - Log Id 967, Changed populate method().
 *    Sampath  2002-Dec-27 - add the mask for editable Date-Time columns.
 *    ChandanaD2003-Jan-08 - Changed tableDefinitionEnd() method.
 *    ChandanaD2003-Jan-10 - Undone the previos changes in tableDefinitionEnd() method.
 *    ChandanaD2003-Feb-18 - Changed tableDefinitionBegin,tableDefinitionEnd,populate & showNoLovSystemColumnContents methods.
 *    ChandanaD2003-Feb-21 - Changed populate() method.
 *    Rifki R  2003-Feb-28 - Log id 1010. Automatic security check for hyperlinks. implemented code.
 *    ChandanaD2003-Mar-19 - Corrected a bug in custom link commands.
 *    ChandanaD2003-Mar-24 - Made changes to custom link commands to support disabling.
 *    Mangala  2003-May-13 - Used getTitle instead of getLable for the table title.
 *                           Related to Log# 1049.
 *    ChandanaD2003-May-22 - Modified populate method for new L&F.
 *    ChandanaD2003-Jun-02 - Added an onClick event function to the table properties & action images to place the popups in a proper place.
 *    ChandanaD2003-Jun-24 - Set 'nowrap' property to the table cell having the actions.
 *    ChandanaD2003-Jun-30 - Modified showNoLovSystemColumnContents() method to support disabling of sub menu items.
 *    Sampath  2003-Aug-21 - new method toSingleLine to allow newline characters in tool tips.
 *    ChandanaD2003-Sep-02 - Fixed a bug in disabling submenus.
 *    Suneth M 2003-Sep-03 - Changed addSystemColumn() to support for select one record in LOV's
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    ChandanaD2004-Feb-16 - Bug 42792 fixed.
 *    Ramila H 2004-02-16  - Bug id 42211, Changed condition and stylesheet class to handle NN7 like IE.
 *    Suneth M 2004-Mar-02 - Bug 43086, Changed addSystemColumn() & showNoLovSystemColumnContents().
 *    ChandanaD2004-Mar-23 - Merged with SP1.
 *    ChandanaD2004-Apr-01 - Further improvements for Multirow Actions.
 *    ChandanaD2004-May-12 - Updated for the new style sheets.
 *    ChandanaD2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Mangala  2004-Jun-17 - Merged with Bug #44656
 *    Chandana 2004-Jun-28 - Added support for viewing history.
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Chandana 2004-Sep-10 - Add a "false" to the popup true-false list to support Layout Properties.
 *    Chandana 2003-Sep-14 - Reversed changes done for Layout Properties.
 *    Jacek P  2004-Nov-11 - Added method newProfile()
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/10/01 buhilk Bug 93333, Improved custom link command functionality.
 * 2010/08/26 sumelk Bug 92641, Changed populate() to avoid a script error.
 * 2010/04/07 sumelk Bug 89777, Changed populate() to support row selection in portlets.
 * 2009/08/11 amiklk Bug 85128, changed mobilePopulate() to use getTitle() instead of getLabel() 
 * 2009/07/30 sumelk Bug 83601, Changed populate() to obtain the client values for history parameters.
 * 2009/07/03 amiklk Bug 83630, changed mobilePopulate() to draw disabled checkboxes for checkbox fields
 * 2009/02/27 buhilk Bug 80960, Overloaded addRowCondition() to manipulate advance row conditions.
 * 2008/11/28 sumelk Bug 78124, Changed addSystemColumn() to fix the sorting errors of LOV's.
 * 2008/07/20 buhilk Bug 74852, Fixed bug in getNextRow(), caused by the new programming model.
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities.
 * 2008/06/13 buhilk Bug 74552, Modified mobilePopulate() to hide fields which are DefaultNotVisible.
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support rich menus/table cells.
 * 2008/04/17 buhilk Bug 73041, Modified showNoLovSystemColumnContents() to append custom group list.
 * 2008/04/04 buhilk Bug 72854, Modified populate() and tableDefinitionBegin() and added rwc variables
 *                              to support RWC links commands.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/03/01 buhilk Bug 63870, Modified generateTitleRow() to improve "whats this" effects.
 * 2007/02/27 buhilk Bug 63814, disabling history shows other commands.
 * 2007/01/30 buhilk Bug 63250, Improved theming support in IFS clients.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/11/22 rahelk Bug 61532 Added functionality for displaying/editing accurate fields
 * Revision      2006/09/05           gegulk 
 * Fixed Bug id 60316, modified addSystemColumn() to replace \ with \\ in the variable safe_value
 *
 * Revision      2006/08/18           gegulk 
 * Fixed Bug id 59985, Removed the usages of the word "enum" as variable names
 *
 * Revision      2006/07/04           buhilk
 * Fixed bug Id: 59007 by prefixing form variable "f" to the "SELECTEBOX" form item in  populate()
 *
 * Revision      2006/05/16           mapelk
 * Improved "What's This?" functionality to show help as a tool tip
 *
 * Revision 1.9  2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 * Revision 1.8  20006/02/14         prralk
 * Mergedd LCS Bug 55545, disable sorting for editable tables.
 *
 * Revision 1.7  2006/01/27          sumelk
 * Corrected LCS Bug 55221,Changed wrapText() to avoid formatting problems.
 *
 * Revision 1.6  2005/11/16 10:04:33  rahelk
 * Call id 128794: fixed bug with selectAll, deseletAll and invertseletion for IIDLOVs
 *
 * Revision 1.5  2005/11/15 11:31:44  japase
 * ASPProfile.save() takes 'this' as argument.
 *
 * Revision 1.4  2005/11/09 05:51:43  sumelk
 * Merged the corrections for Bug 47881, Added javadoc comments.
 *
 * Revision 1.3  2005/10/21 05:11:01  mapelk
 * bugfix: ReadOnly fields show LOVs in editable tables.
 *
 * Revision 1.2  2005/09/22 12:39:22  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.10.2.1  2005/09/21 12:40:15  mapelk
 * Fixed bug: RMBs added just after seperator dissapears.
 *
 * Revision 1.10  2005/07/15 09:17:47  mapelk
 * Profile setting are ignored if the hidden property of the field is dirty
 *
 * Revision 1.9  2005/07/01 15:26:52  rahelk
 * minor improvements to prepareProfileInfo
 *
 * Revision 1.8  2005/06/29 13:29:52  mapelk
 * Bug 125474: fields are not right aligned in Netscape.
 *
 * Revision 1.7  2005/06/09 09:40:36  mapelk
 * Added functionality to "Show pages in default layout mode"
 *
 * Revision 1.6  2005/06/06 07:22:27  rahelk
 * Save all profile objects together from CSL page
 *
 * Revision 1.5  2005/05/17 10:44:13  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.4  2005/02/15 08:45:10  mapelk
 * Bug fix: Project link call 121294: Static LOVs with query string disapear. And removed tabale property icon if not necessory.
 *
 * Revision 1.3  2005/02/11 10:55:40  riralk
 * More changes for new profile. Called the correct ASPProfile.save() method from removeProfile()
 *
 * Revision 1.2  2005/02/01 10:32:59  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.3  2005/01/21 10:40:10  rahelk
 * Bug 48132 Merged, rewrote appendTDTag method to call ASPField.appendTooltipTag.
 *
 * Revision 1.2  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An instance of this class represents a multi-row read-only HTML table.
 * Columns of this table corresponds to ASPField objects,
 * while rows corresponds to database records returned from a Query.
 */
public class ASPTable extends ASPPageElement implements ASPBufferable
{
   //==========================================================================
   //  Persistent immutable attributes
   //==========================================================================

   private String  title;
   private String  column_list;
   private int     column_count;
   private int     table_nr;
   private String  key_name;
   private String  name;
   private String  sort_function;

   private String  STYLESHEET_CLASS_MULTIROW_TH = "multirowTableTH";
   private String  STYLESHEET_CLASS_MULTIROW_TH_EXP = "multirowTableTHExt";
   private String  STYLESHEET_CLASS_MULTIROW_SYS_TH = "multirowTableSysTH";
   private String  STYLESHEET_CLASS_MULTIROW = "multirowTable"; 
   private String  STYLESHEET_CLASS_MULTIROW_TD = "multirowTableTD";
   private String  STYLESHEET_CLASS_MULTIROW_SYS_TD = "multirowTableSysTD";
   private String  STYLESHEET_CLASS_MULTIROW_TH_LINK = "multirowTableTHLink";
   private String  STYLESHEET_CLASS_MULTIROW_TH_TEXT = "multirowTableTHText";
   
   public static final String EQUALS = "==";
   public static final String NOT_EQUALS = "!=";
   public static final String GREATER_THAN_OR_EQUAL = ">=";
   public static final String LESS_THAN_OR_EQUAL = "<=";

   //bug id 42211 start
   //private String  STYLESHEET_CLASS_GRID_CONT_READONLY = "ReadOnlyTextValue";
   //bug id 42211 end

   //==========================================================================
   //  Transient mutable attributes
   //==========================================================================

   transient ASPTableProfile profile;     transient ASPTableProfile pre_profile;

   //==========================================================================
   //  Transient immutable attributes
   //==========================================================================

   private transient ASPField[]    columns;
   private transient HTMLGenerator html;
   private transient ASPPopup      popup;
   private transient ASPPopup      tool;

   //==========================================================================
   //  Cache Attributes
   //
   //  Transient, immutable attributes with late initialization
   //  (just before the first use).
   //==========================================================================


   //==========================================================================
   //  Transient temporary variables
   //==========================================================================

   private transient ASPField[] user_columns;
   private transient int[]      user_column_size;
   private transient boolean    user_profile_prepared;

   // User HTML generation.
   private transient AbstractDataRowCollection  rows;
   private transient AbstractDataRow            row;
   private transient int        row_count;
   private transient int        row_no;
   private transient boolean    initDone;
   private transient int        col_no;

   private String link_command_script;
   private Vector link_command_list;
   private int link_count;
   private String link_function;
   private AutoString sub_menu_calls;
   private AutoString popup_list;
   private AutoString sub_popup_list;
   boolean mulit_action_enable_cond;        boolean pre_mulit_action_enable_cond;

   //History Stuff
   private String lu_name;
   private String lu_keys;
   private Vector history_data;


   //==========================================================================
   //  Static constants
   //==========================================================================

   public        static       boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPTable");

   static final String  SELECTBOX = "__SELECTED";

   // NOW transient
   //private static final String  STYLESHEET_CLASS_GRID_HEAD = "grdHd";
   //private static final String  STYLESHEET_CLASS_GRID_CONT = "grdCnt";


   private static final int     LOV       = 1;
   private static final int     OVERVIEW  = 2;
   private static final String  DATA      = ASPRowSet.DATA;

   private static final int     SEARCHROW = -2;
   private static final int     TITLEROW  = -1;

   private static final int     CLEAR_NOT       = 1;
   private static final int     CLEAR_ALL       = 2;
   private static final int     CLEAR_DISPLAYED = 3;

   private static final double  sign_width = 7.1;

   private static TraceEventType populate_event_type = new TraceEventType("ASPTable.populare");

   //==========================================================================
   //  Constructors
   //==========================================================================

   protected ASPTable( ASPPage page )
   {
      super(page);
      html = new HTMLGenerator();
   }

   ASPTable construct( ASPBlock block, String name ) throws FndException
   {
      ASPPage   page = getASPPage();
      ASPConfig cfg  = page.getASPConfig();

      this.name = block==null ? name : block.getName();
      html.fetchConfigParameters();
      table_nr = page.getTableCount() + 1;

      popup = page.newASPPopup("tbl" + table_nr + "Pop",this);
      tool = page.newASPPopup("tbl" + table_nr + "Tool",this);

      //html.page_size   = Integer.parseInt( cfg.getParameter("TABLE/PAGE_SIZE","20") );
      html.page_size   = 0;
      html.editable_pagesize = Integer.parseInt( cfg.getParameter("TABLE/PAGE_SIZE/EDITABLE","15") );
      html.readonly_pagesize = Integer.parseInt( cfg.getParameter("TABLE/PAGE_SIZE/READONLY","17") );
      html.row_select  = true;
      // Added by Terry 20120921
      // Only one row selection switch
      html.one_row_select = false;
      // Added end
      html.row_counter = true;
      html.row_status  = true;
      html.query_row   = true;
      html.title_row   = true;
      html.detail_link = true;
      html.row_condidtions = new String[0];
      
      //html.show_border = true;
      html.show_border        = "Y".equals( cfg.getParameter("AUDIT/__DEBUG/SHOW_BORDER","N") );
      //html.striped_background = "Y".equals( cfg.getParameter("TABLE/STRIPED_BACKGROUND","N") );
      html.striped_background = null;
      //html.edit_striped_bkg   = "Y".equals( cfg.getParameter("TABLE/STRIPED_BACKGROUND/EDITABLE","N") );
      //html.read_striped_bkg   = "Y".equals( cfg.getParameter("TABLE/STRIPED_BACKGROUND/READONLY","Y") );

      html.quick_edit = null;
      html.clear_query_row = CLEAR_NOT;


      if( block!=null )
      {
         // Modified by Terry 20131027
         // Consider field order
         ASPBlockLayout lay = block.getASPBlockLayout();
         String column_order = lay.getFieldOrder();
         if (!Str.isEmpty(column_order))
         {
            Vector columns_vector = listToFields(block, column_order, true);
            column_count = columns_vector.size();
            columns = new ASPField[column_count];
            for(int i = 0; i < column_count; i++)
               columns[i] = (ASPField) columns_vector.elementAt(i);
            column_list = getFieldList(columns_vector);
         }
         else
         {
            columns = block.getFields();
            column_count = columns.length;
            column_list = block.getFieldList();
         }
         // Modified end
      }
      else
         columns = new ASPField[10];

      // If version 3 or higher, automagically...
      if(getASPPage().getVersion() == 3)
      {
         setPopup();
         unsetEditable();
         disableRowSelect();  // What to do?
         enableRowCounter();
         enableQuickEdit();

         disableQueryRow();
         hideBorder();
         setStripedBackground();
      }


      mulit_action_enable_cond = true;

      return this;
   }

   //==========================================================================
   //  ASPBufferable interface
   //==========================================================================

   /**
    * Store the internal state of this ASPTable in a specified ASPBuffer
    */
   public void save( ASPBuffer intobuf )
   {
      try
      {
         Buffer into = intobuf.getBuffer();
         saveClass(into);
         Buffers.save( into, "NAME"               , name );
         Buffers.save( into, "TITLE"              , title );
         Buffers.save( into, "COLUMN_LIST"        , column_list );
         Buffers.save( into, "COLUMN_COUNT"       , column_count );
         Buffers.save( into, "TABLE_NR"           , table_nr );
         Buffers.save( into, "KEY_NAME"           , key_name );
         Buffers.save( into, "SORT_FUNCTION"      , sort_function    );
         Buffers.save( into, "PAGE_SIZE"          , html.page_size   );
         Buffers.save( into, "ED_PAGE_SIZE"       , html.editable_pagesize );
         Buffers.save( into, "RO_PAGE_SIZE"       , html.readonly_pagesize );
         Buffers.save( into, "ROW_SELECT"         , html.row_select  );
         // Added by Terry 20120921
         // Only one row selection switch
         Buffers.save( into, "ONE_ROW_SELECT"     , html.one_row_select  );
         Buffers.save( into, "SELECTION_OPTION_DISABLED", html.selection_option_disabled  );
         // Added end
         Buffers.save( into, "ROW_COUNTER"        , html.row_counter  );
         Buffers.save( into, "ROW_STATUS"         , html.row_status  );
         Buffers.save( into, "QUERY_ROW"          , html.query_row   );
         Buffers.save( into, "TITLE_ROW"          , html.title_row   );
         Buffers.save( into, "SHOW_BORDER"        , html.show_border );
         //Buffers.save( into, "STRIPED_BACKGROUND" , html.striped_background );
         //Buffers.save( into, "ED_STRIPED_BKG"     , html.edit_striped_bkg );
         //Buffers.save( into, "RO_STRIPED_BKG"     , html.read_striped_bkg );
         Buffers.save( into, "EDITABLE"           , html.editable    );
         Buffers.save( into, "SORTABLE"           , html.sortable    );
         Buffers.save( into, "EDIT_PROPERTIES"    , html.edit_properties );
         Buffers.save( into, "ENABLE_OUTCHANNEL"  , html.enable_outchannel );
         Buffers.save( into, "POP_COMMANDS"    , html.pop_commands );
         Buffers.save( into, "QUERYROW_LINK"    , html.queryrow_link );
         Buffers.save( into, "MULTIORW_ACTION_ENABLE_COND", mulit_action_enable_cond);
         Buffers.save( into, "DETAIL_LINK"        , html.detail_link );
         Buffers.save( into, "ROW_CONSITIONS"     , html.row_condidtions);
         Hashtable ht = new Hashtable();
         for( int i=0; i<column_count; i++ )
         {
            ASPField f = columns[i];
            ht.put(f.getBlock(),"");
         }

         ASPManager aspmgr = getASPManager();
         Enumeration block_list = ht.keys();
         while( block_list.hasMoreElements() )
         {
            ASPBlock b = (ASPBlock)block_list.nextElement();
            Buffer sub = into.newInstance();
            b.save(aspmgr.newASPBuffer(sub));
            into.addItem("BLOCK",sub);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Retrieve the internal state of this ASPTable from a specified ASPBuffer
    */
   public void load( ASPBuffer frombuf )
   {
      try
      {
         setLoaded();
         Buffer from = frombuf.getBuffer();
         BufferIterator iter = from.iterator();
         while( iter.hasNext() )
         {
            Item item = iter.next();
            if( "BLOCK".equals(item.getName()) )
            {
               Buffer sub = item.getBuffer();
               ASPBlock b = new ASPBlock(getASPPage());
               b.load(getASPManager().newASPBuffer(sub));
               b.construct();
               getASPPage().register(b);
            }
         }

         name                    = Buffers.loadString ( from, "NAME"          );
         title                   = Buffers.loadString ( from, "TITLE"         );
         table_nr                = Buffers.loadInt    ( from, "TABLE_NR"      );
         key_name                = Buffers.loadString ( from, "KEY_NAME"      );
         sort_function           = Buffers.loadString ( from, "SORT_FUNCTION" );
         html.page_size          = Buffers.loadInt    ( from, "PAGE_SIZE"     );
         html.editable_pagesize  = Buffers.loadInt    ( from, "ED_PAGE_SIZE"  );
         html.readonly_pagesize  = Buffers.loadInt    ( from, "RO_PAGE_SIZE"  );
         html.row_select         = Buffers.loadBoolean( from, "ROW_SELECT"    );
         // Added by Terry 20120921
         // Only one row selection switch
         html.one_row_select     = Buffers.loadBoolean( from, "ONE_ROW_SELECT");
         html.selection_option_disabled     = Buffers.loadBoolean( from, "SELECTION_OPTION_DISABLED");
         // Added end
         html.row_counter        = Buffers.loadBoolean( from, "ROW_COUNTER"   );
         html.row_status         = Buffers.loadBoolean( from, "ROW_STATUS"    );
         html.query_row          = Buffers.loadBoolean( from, "QUERY_ROW"     );
         html.title_row          = Buffers.loadBoolean( from, "TITLE_ROW"     );
         html.show_border        = Buffers.loadBoolean( from, "SHOW_BORDER"   );
         //html.striped_background = Buffers.loadBoolean( from, "STRIPED_BACKGROUND" );
         //html.edit_striped_bkg   = Buffers.loadBoolean( from, "ED_STRIPED_BKG");
         //html.read_striped_bkg   = Buffers.loadBoolean( from, "RO_STRIPED_BKG");
         html.editable           = Buffers.loadBoolean( from, "EDITABLE"      );
         html.sortable           = Buffers.loadBoolean( from, "SORTABLE"      );
         html.edit_properties    = Buffers.loadBoolean( from, "EDIT_PROPERTIES" );
         html.enable_outchannel  = Buffers.loadBoolean( from, "ENABLE_OUTCHANNEL" );
         html.pop_commands       = Buffers.loadBoolean( from, "POP_COMMANDS" );
         html.queryrow_link       = Buffers.loadBoolean( from, "QUERYROW_LINK" );
         mulit_action_enable_cond = Buffers.loadBoolean( from, "MULTIORW_ACTION_ENABLE_COND");
         html.detail_link        = Buffers.loadBoolean( from, "DETAIL_LINK");
         html.row_condidtions    = Buffers.loadStringArray( from, "ROW_CONSITIONS");

         String clist = Buffers.loadString( from, "COLUMN_LIST"  );
         addColumn(clist);

         if( column_count != Buffers.loadInt(from,"COLUMN_COUNT") )
            throw new FndException("FNDTABINVCC: Cannot load ASPTable. Invalid value for COLUMN_COUNT: &1", ""+column_count);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   //==========================================================================
   //  ASPPoolElement logic
   //==========================================================================

   /**
    * Reset this table and all its attributes.
    * Referenced fields are reset by ASPBlock.
    */
   protected void doReset() throws FndException
   {
      html.page_size          = html.pre_page_size;
      html.textarea_height    = html.pre_textarea_height;
      html.row_select         = html.pre_row_select;
      // Added by Terry 20120921
      // Only one row selection switch
      html.one_row_select     = html.pre_one_row_select;
      html.selection_option_disabled = html.pre_selection_option_disabled;
      // Added end
      html.row_counter        = html.pre_row_counter;
      html.row_status         = html.pre_row_status;
      html.query_row          = html.pre_query_row;
      html.title_row          = html.pre_title_row;
      html.show_border        = html.pre_show_border;
      html.striped_background = html.pre_striped_background;
      html.quick_edit         = html.pre_quick_edit;
      html.clear_query_row    = html.pre_clear_query_row;
      html.editable           = html.pre_editable;
      html.sortable           = html.pre_sortable;
      html.edit_properties    = html.pre_edit_properties;
      html.enable_outchannel  = html.pre_enable_outchannel;
      html.queryrow_link      = html.pre_queryrow_link;
      html.detail_link        = html.pre_detail_link;
      html.pop_commands       = html.pre_pop_commands;
      html.row_condidtions    = html.pre_row_condidtions;
      html.wrap               = html.pre_wrap;

      html.columnFormatting   = html.pre_columnFormatting;
      html.rowFormatting      = html.pre_columnFormatting;

      html.titleLeftColumn    = html.pre_titleLeftColumn;
      html.titleMidColumn     = html.pre_titleMidColumn;
      html.titleRightColumn   = html.pre_titleRightColumn;

      html.upperLeftColumn    = html.pre_upperLeftColumn;
      html.upperMidColumn     = html.pre_upperMidColumn;
      html.upperRightColumn   = html.pre_upperRightColumn;
      html.leftColumn         = html.pre_leftColumn;
      html.rightColumn        = html.pre_rightColumn;
      html.bottomLeftColumn   = html.pre_bottomLeftColumn;
      html.bottomMidColumn    = html.pre_bottomMidColumn;
      html.bottomRightColumn  = html.pre_bottomRightColumn;

      mulit_action_enable_cond = pre_mulit_action_enable_cond;

      user_profile_prepared = false;

      link_command_script   = null;
      link_command_list     = null;
      link_count            = 0;
      link_function         = null;
      sub_menu_calls        = null;

      popup_list            = null;
      sub_popup_list        = null;

      profile = pre_profile;
      
      rows = null;
      row  = null;
   }

   /**
    * Freeze this table and all its attributes.
    * Referenced fields are frozen by ASPBlock.
    * Create the default (pre) instance of ASPTableProfile.
    */
   protected void doFreeze() throws FndException
   {
      createBaseProfile();

      html.pre_page_size          = html.page_size;
      html.pre_row_select         = html.row_select;
      // Added by Terry 20120921
      // Only one row selection switch
      html.pre_one_row_select     = html.one_row_select;
      html.pre_selection_option_disabled = html.selection_option_disabled;
      // Added end
      html.pre_row_counter        = html.row_counter;
      html.pre_row_status         = html.row_status;
      html.pre_query_row          = html.query_row;
      html.pre_title_row          = html.title_row;
      html.pre_show_border        = html.show_border;
      html.pre_striped_background = html.striped_background;
      html.pre_quick_edit         = html.quick_edit;
      html.pre_clear_query_row    = html.clear_query_row;
      html.pre_editable           = html.editable;
      html.pre_sortable           = html.sortable;
      html.pre_edit_properties    = html.edit_properties;
      html.pre_enable_outchannel  = html.enable_outchannel;
      html.pre_queryrow_link      = html.queryrow_link;
      html.pre_textarea_height    = html.textarea_height;
      html.pre_detail_link        = html.detail_link;
      html.pre_pop_commands       = html.pop_commands;
      html.pre_row_condidtions    = html.row_condidtions;
      html.pre_wrap               = html.wrap;

      html.pre_columnFormatting       = html.columnFormatting;
      html.pre_rowFormatting       = html.rowFormatting;

      html.pre_titleLeftColumn    = html.titleLeftColumn;
      html.pre_titleMidColumn     = html.titleMidColumn;
      html.pre_titleRightColumn   = html.titleRightColumn;

      html.pre_upperLeftColumn    = html.upperLeftColumn;
      html.pre_upperMidColumn     = html.upperMidColumn;
      html.pre_upperRightColumn   = html.upperRightColumn;
      html.pre_leftColumn         = html.leftColumn;
      html.pre_rightColumn        = html.rightColumn;
      html.pre_bottomLeftColumn   = html.bottomLeftColumn;
      html.pre_bottomMidColumn    = html.bottomMidColumn;
      html.pre_bottomRightColumn  = html.bottomRightColumn;


      pre_mulit_action_enable_cond = mulit_action_enable_cond;
   }


   /**
    * Clone this table into a new table included in the specified page.
    * The referenced fields (and blocks) must be cloned before the table.
    * The state of the new table is DEFINED.
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPPage p = (ASPPage)page;
      if(DEBUG) p.debug(this+" clone("+page+")");
      ASPTable t = new ASPTable(p);

      t.title         = title;
      t.column_list   = column_list;
      t.table_nr      = table_nr;
      t.key_name      = key_name;
      t.name          = name;
      t.sort_function = sort_function;

      t.columns = new ASPField[column_count];
      t.column_count = 0;
      t.addColumn(t.column_list,p);

      t.html.page_size          = t.html.pre_page_size          = html.pre_page_size;
      t.html.row_select         = t.html.pre_row_select         = html.pre_row_select;
      // Added by Terry 20120921
      // Only one row selection switch
      t.html.one_row_select     = t.html.pre_one_row_select     = html.pre_one_row_select;
      // Add property, user can disable row select opertions, clone
      t.html.selection_option_disabled = t.html.pre_selection_option_disabled = html.pre_selection_option_disabled;
      // Added end
      t.html.row_counter        = t.html.pre_row_counter        = html.pre_row_counter;
      t.html.row_status         = t.html.pre_row_status         = html.pre_row_status;
      t.html.query_row          = t.html.pre_query_row          = html.pre_query_row;
      t.html.title_row          = t.html.pre_title_row          = html.pre_title_row;
      t.html.show_border        = t.html.pre_show_border        = html.pre_show_border;
      t.html.striped_background = t.html.pre_striped_background = html.pre_striped_background;
      t.html.quick_edit         = t.html.pre_quick_edit         = html.pre_quick_edit;
      t.html.clear_query_row    = t.html.pre_clear_query_row    = html.pre_clear_query_row;
      t.html.editable           = t.html.pre_editable           = html.pre_editable;
      t.html.sortable           = t.html.pre_sortable           = html.pre_sortable;
      t.html.edit_properties    = t.html.pre_edit_properties    = html.pre_edit_properties;
      t.html.enable_outchannel  = t.html.pre_enable_outchannel  = html.pre_enable_outchannel;
      t.html.queryrow_link      = t.html.pre_queryrow_link      = html.pre_queryrow_link;
      t.html.textarea_height    = t.html.pre_textarea_height    = html.pre_textarea_height;
      t.html.detail_link        = t.html.pre_detail_link        = html.pre_detail_link;
      t.html.pop_commands       = t.html.pre_pop_commands       = html.pre_pop_commands;
      t.html.row_condidtions    = t.html.pre_row_condidtions    = html.pre_row_condidtions;
      t.html.wrap               = t.html.pre_wrap               = html.pre_wrap;

      t.html.columnFormatting   = t.html.pre_columnFormatting       = html.pre_columnFormatting;
      t.html.rowFormatting      = t.html.pre_rowFormatting       = html.pre_rowFormatting;

      t.html.titleLeftColumn    = t.html.pre_titleLeftColumn    = html.pre_titleLeftColumn;
      t.html.titleMidColumn     = t.html.pre_titleMidColumn     = html.pre_titleMidColumn;
      t.html.titleRightColumn   = t.html.pre_titleRightColumn   = html.pre_titleRightColumn;

      t.html.upperLeftColumn    = t.html.pre_upperLeftColumn    = html.pre_upperLeftColumn;
      t.html.upperMidColumn     = t.html.pre_upperMidColumn     = html.pre_upperMidColumn;
      t.html.upperRightColumn   = t.html.pre_upperRightColumn   = html.pre_upperRightColumn;
      t.html.leftColumn         = t.html.pre_leftColumn         = html.pre_leftColumn;
      t.html.rightColumn        = t.html.pre_rightColumn        = html.pre_rightColumn;
      t.html.bottomLeftColumn   = t.html.pre_bottomLeftColumn   = html.pre_bottomLeftColumn;
      t.html.bottomMidColumn    = t.html.pre_bottomMidColumn    = html.pre_bottomMidColumn;
      t.html.bottomRightColumn  = t.html.pre_bottomRightColumn  = html.pre_bottomRightColumn;


      t.profile                 = t.pre_profile                 = pre_profile;
      t.mulit_action_enable_cond= t.pre_mulit_action_enable_cond= mulit_action_enable_cond;

      t.html.editable_pagesize         = html.editable_pagesize;
      t.html.readonly_pagesize         = html.readonly_pagesize;
      //t.html.edit_striped_bkg          = html.edit_striped_bkg;
      //t.html.read_striped_bkg          = html.read_striped_bkg;

      //t.html.title_row_bgcolor         = html.title_row_bgcolor;
      //t.html.title_row_font_color      = html.title_row_font_color;
      //t.html.title_row_font_face       = html.title_row_font_face;

      //t.html.bgcolor                   = html.bgcolor;
      //t.html.bgcolor2                  = html.bgcolor2;

      t.html.selbox_col_width          = html.selbox_col_width;
      t.html.status_col_width          = html.status_col_width;
      t.html.status_img_width          = html.status_img_width;
      t.html.status_img_height         = html.status_img_height;

      t.html.lov_image                 = html.lov_image;
      t.html.lov_col_width             = html.lov_col_width;
      t.html.lov_img_width             = html.lov_img_width;
      t.html.lov_img_height            = html.lov_img_height;
      t.html.run_lov_img_width         = html.run_lov_img_width;

      t.html.check_box_checked_image   = html.check_box_checked_image;
      t.html.check_box_unchecked_image = html.check_box_unchecked_image;
      t.html.check_box_img_width       = html.check_box_img_width;
      t.html.check_box_img_height      = html.check_box_img_height;

      t.html.hyperlink_img             = html.hyperlink_img;
      t.html.hyperlink_img_width       = html.hyperlink_img_width;
      t.html.hyperlink_img_height      = html.hyperlink_img_height;

      t.html.properties_img            = html.properties_img;
      t.html.allow_properties          = html.allow_properties;

      t.html.out_channel_img           = html.out_channel_img;
      t.html.allow_out_channel         = html.allow_out_channel;

      //t.html.script_location           = html.script_location;
      //t.html.images_location           = html.images_location;
      t.html.alt_text                  = html.alt_text;

      t.html.normal_img                = html.normal_img;
      t.html.new_img                   = html.new_img;
      t.html.modify_img                = html.modify_img;
      t.html.remove_img                = html.remove_img;

      t.html.normal_btn_img            = html.normal_btn_img;
      t.html.new_btn_img               = html.new_btn_img;
      t.html.modify_btn_img            = html.modify_btn_img;
      t.html.remove_btn_img            = html.remove_btn_img;

      t.html.sort_img_asc              = html.sort_img_asc;
      t.html.sort_img_desc             = html.sort_img_desc;
      t.html.sort_img_width            = html.sort_img_width;
      t.html.sort_img_height           = html.sort_img_height;

      //Bug 42792, start
      t.html.nowrap_disabled           = html.nowrap_disabled;
      t.html.title_nowrap_disabled     = html.title_nowrap_disabled;
      //Bug 42792, end
      
      //t.html.cellpad_editable          = html.cellpad_editable;
      //t.html.cellpad_readonly          = html.cellpad_readonly;

      t.popup = p.getASPPopup("tbl" + table_nr + "Pop");
      t.tool = p.getASPPopup("tbl" + table_nr + "Tool");

      t.link_command_script   = link_command_script;
      t.link_command_script   = link_command_script;
      t.link_command_list     = link_command_list;
      t.link_count            = link_count;
      t.link_function         = link_function;
      t.sub_menu_calls        = sub_menu_calls;

      t.popup_list            = popup_list;
      t.sub_popup_list        = sub_popup_list;

      t.lu_name = lu_name;
      t.lu_keys = lu_keys;

      t.rows = null;
      t.row  = null;
      t.setCloned();
      if(DEBUG) p.debug(this+" cloned into "+t);
      return t;
   }


   public String toString()
   {
      return super.toString()+" "+getName();
   }


   /**
    * Inherited interface.
    * Create and return new instance of corresponding profile class.
    * Return null if the class doesn't support profile handling.
    */
   protected ASPPoolElementProfile newProfile()
   {
      return new ASPTableProfile();
   }


   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);

      for( int i=0; i<column_count; i++ )
         columns[i].verifyPage(page);

      getBlock().verifyPage(page);

      if( html.cmd_bar!=null ) html.cmd_bar.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   //==========================================================================
   //  Title
   //==========================================================================

   /**
    * Set the title for this ASPTable. It is used for example by a standard LOV
    * functionality.
    * @see ifs.fnd.asp.ASPManager.showLOV
    */
   public void setTitle( String title )
   {
      try
      {
         modifyingImmutableAttribute("TITLE");
         this.title = getASPManager().translate(title);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Return the title defined for this ASPTable.
    */
   public String getTitle()
   {
      return Str.isEmpty(title) ? getName() : title;
   }

   //==========================================================================
   //  Column
   //==========================================================================

   /**
    * Append one or more columns to this ASPTable. Each column corresponds
    * to an existing ASPField. The specified string should contain a
    * comma-separated list of ASPField names.
    * Return a reference to the last ASPField on the list.
    */
   public ASPField addColumn( String names )
   {
      try
      {
         modifyingImmutableAttribute("COLUMNS");
         return addColumn(names,getASPPage());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   private ASPField addColumn( String names, ASPPage page )
   {
      ASPField field = null;
      StringTokenizer st = new StringTokenizer(names,", \n\r\t");
      while( st.hasMoreTokens() )
      {
         String name = st.nextToken();
         field = page.getASPField(name);
         if( column_count==columns.length )
         {
            ASPField[] newcols = new ASPField[2*column_count];
            System.arraycopy(columns,0,newcols,0,columns.length);
            columns = newcols;
         }
         columns[column_count++] = field;
         column_list = column_list==null ? name : column_list+","+name;
      }
      return field;
   }


   int getColumnCount()
   {
      return column_count;
   }


   ASPField getColumn( int index )
   {
      return columns[index];
   }


   int findColumnIndex( String name )
   {
      for( int c=0; c<column_count; c++ )
      {
         ASPField column = columns[c];
         if( column.getName().equals(name) )
            return c;
      }
      return -1;
   }

   int findColumnIndex( ASPField field )
   {
      for( int c=0; c<column_count; c++ )
         if( columns[c]==field )
            return c;
      return -1;
   }

   //==========================================================================
   //  Key
   //==========================================================================

   /**
    * Define the key column for this ASPTable. The specified string must be a name of an
    * existing ASPField.
    */
   public void setKey( String field_name )
   {
      try
      {
         modifyingImmutableAttribute("KEY");
         getASPPage().getASPField(field_name);
         key_name = field_name;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Return the DbName of the key defined in a call to setKey(),
    * or the DbName of the first visible column, if the key is undefined.
    * The value of this column will
    * be copied back to the caller from a standard LOV form.
    *
    * @see ifs.fnd.asp.ASPTable#setKey
    */
   public String getKeyColumnName()
   {
      try
      {
         return html.getKeyColumnName();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Sort
   //==========================================================================

   /**
    * Enables sorting based on a column.
    */
   public void setSortable()
   {
      try
      {
         modifyingMutableAttribute("SORTABLE");
         html.sortable = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Disables sorting based on a column.
    */
   public void unsetSortable()
   {
      try
      {
         modifyingMutableAttribute("SORTABLE");
         html.sortable = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if sorting is enabled.
    */
   public boolean isSortable()
   {
      return html.sortable;
   }

   /**
    * Define the JScript function which will be called when a user submits
    * a sort command.
    */
   public void setSortFunction( String function_name )
   {
      try
      {
         modifyingImmutableAttribute("SORT_FUNCTION");
         this.sort_function = function_name;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   private String getSortFunction()
   {
      return Str.isEmpty(sort_function) ? "Goto" : sort_function;
   }

   //==========================================================================
   //
   //==========================================================================

   /**
    * Return a reference to ASPBlock which is the base block for this ASPTable.
    */
   public ASPBlock getBlock()
   {
      try
      {
         ASPBlock blk = null;
         for( int i=0; i<column_count; i++ )
         {
            ASPBlock b = columns[i].getBlock();
            if( blk==null )
               blk = b;
            else if( b!=blk )
               throw new FndException("FNDTABDUPCOL: Columns of this ASPTable are included in many blocks");
         }
         return blk;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns the Table No of this ASPTable object.
    * @return Table No
    */
   public int getTableNo()
   {
      return table_nr;
   }

   /**
    * Returnd the Name of this ASPTable object.
    * @return Table Name
    */
   public String getName()
   {
      return name;
   }


   //==========================================================================
   //  Public properties and other public functions for using from script.
   //==========================================================================

   /**
    * Show row counter.This is the defult option.
    */
   public void enableRowCounter()
   {
      try
      {
         modifyingMutableAttribute("ROW_COUNTER");
         html.row_counter = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Hide row counter.
    */
   public void disableRowCounter()
   {
      try
      {
         modifyingMutableAttribute("ROW_COUNTER");
         html.row_counter = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if row counter is enabled.
    */
   public boolean isRowCounterEnabled()
   {
      return html.row_counter;
   }

   public void setMultirowActinEnableCondition(String cond)
   {
      try
      {
         modifyingMutableAttribute("MULTIORW_ACTION_ENABLE_COND");
         if("OR".equalsIgnoreCase(cond))
            mulit_action_enable_cond = false;
         else
            mulit_action_enable_cond = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   public String getMultirowActinEnableCondition()
   {
      return mulit_action_enable_cond?"true":"false";
   }

   /**
    * Enable possibility to mark rows in the table. This option is the default one.
    */
   public void enableRowSelect()
   {
      try
      {
         modifyingMutableAttribute("ROW_SELECT");
         html.row_select = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable possibility to mark rows in the table.
    */
   public void disableRowSelect()
   {
      try
      {
         modifyingMutableAttribute("ROW_SELECT");
         html.row_select = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if the possibility to mark rows is enabled.
    */
   public boolean isRowSelectionEnabled()
   {
      return html.row_select;
   }

   /**
    * Enable possibility to click on detail link for each row.
    */
   public void enableDetailLink()
   {
      try
      {
         modifyingMutableAttribute("DETAIL_LINK");
         html.detail_link = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /**
    * Disable possibility to click on detail link for each row.
    */
   public void disableDetailLink()
   {
      try
      {
         modifyingMutableAttribute("DETAIL_LINK");
         html.detail_link = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if the possibility to click on detail link for each row.
    */
   public boolean isDetailLinkEnabled()
   {
      return html.detail_link;
   }
   
   /**
    * Adds row conditions to the overview table. The field values will be compared with the given condition values
    * during runtime.
    * @param field name of field to compare.
    * @param conditionValue the values to compare with the quried value.
    */
   public void addRowCondition(String field, String conditionValue)
   {
      try
      {
         addRowCondition(field, EQUALS, conditionValue, "boldTextValue");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Adds row conditions to the overview table. The field values will be compared with the given condition values
    * during runtime.
    * @param field name of field to compare.
    * @param condition condition to compare with the quried value.
    * @param value the values to compare with the quried value.
    * @param css_class the style class to apply if the condition is true.
    */
   public void addRowCondition(String field, String condition, String value, String css_class)
   {
      try
      {
         modifyingMutableAttribute("ROW_CONSITIONS");
         html.row_condidtions=(String[]) Arrays.asList(html.row_condidtions).toArray(new String[html.row_condidtions.length+1]);
         html.row_condidtions[html.row_condidtions.length-1] = field.trim()+"^"+value.trim()+"^"+condition.trim()+"^"+css_class.trim();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /**
    * Retrives the list of row conditions that are attached to the table. The conditions are in the "fieldName^value" format.
    * @return a String array of row conditions.
    */
   public String[] getRowConditions()
   {
      return html.row_condidtions;
   }
   
   /**
    * Returns the availability of row condiditons
    * @return true if row condidtions exist otherwise false.
    */
   public boolean hasRowConditions()
   {
      return (html.row_condidtions.length>0);
   }
   
   /**
    * Get the row condidtion status according the values in the rowset.
    * @param rowno index number of the row to be checked.
    * @return true if all the conditions are true, otherwise false.
    */
   public boolean getRowConditionStatus(int rowno)
   {
      return getRowConditionStatus(rowno, true)[1].equals("true");
   }
   
   /**
    * Get the styles classes applied according to the row condidtion status.
    * @param rowno index number of the row to be checked.
    * @return space sperated list of css class names.
    */
   public String getRowConditionStyles(int rowno)
   {
      return getRowConditionStatus(rowno, false)[0];
   }
   
   private String[] getRowConditionStatus(int rowno, boolean condStatus)
   {
      boolean finalCondition = true;
      ASPManager mgr = getASPManager();
      String styles = "";
      String conds[] = getRowConditions();
      if(html.row_condidtions==null) return new String[] {"","false"};
      try
      {
         for(int c=0; c<conds.length; c++)
         {
            boolean _temp = true;
            StringTokenizer st = new StringTokenizer(conds[c],"^");
            if(st.countTokens()<4) continue;
            String fldName    = st.nextToken();
            int typeid        = getBlock().getASPField(fldName).getTypeId();
            String value      = st.nextToken().toUpperCase();
            
            String condition  = st.nextToken();
            String css_class  = st.nextToken();
            String rowValue= getBlock().getASPRowSet().getValueAt(rowno, getBlock().getASPField(fldName).getDbName());
            if(mgr.isEmpty(rowValue) || mgr.isEmpty(value)) continue;
            rowValue = rowValue.toUpperCase();
            
            boolean found_condition_true = false;
            
            StringTokenizer value_st = new StringTokenizer(value, ";,");
            while( value_st.hasMoreTokens() && !found_condition_true )
            {
               _temp = true;
               value = value_st.nextToken();
               if(mgr.isCSVName(value))
                  value = mgr.getCSVValue(value);
               
               if((condition.equals(EQUALS) && !rowValue.equals(value)) ||
                  (condition.equals(NOT_EQUALS) && rowValue.equals(value)))
               {
                  _temp = false;
               }
               else if(condition.equals(GREATER_THAN_OR_EQUAL))
               {
                  if(typeid==DataFormatter.INTEGER || typeid==DataFormatter.NUMBER || typeid==DataFormatter.MONEY)
                  {
                     double rowVal = Double.parseDouble(rowValue);
                     double conVal = Double.parseDouble(value);
                     if(rowVal < conVal) _temp = false;
                  }
                  else if(typeid==DataFormatter.DATE || typeid==DataFormatter.DATETIME)
                  {
                     try
                     {
                        SimpleDateFormat fmt = new SimpleDateFormat(ContextSubstitutionVariables.SERVER_DATE_FORMAT);
                        Date rowVal = fmt.parse(rowValue);
                        Date conVal = fmt.parse(value);
                        if(rowVal!=null && conVal!=null && rowVal.after(conVal)) _temp = false;
                     }
                     catch (ParseException ex)
                     {
                        throw new FndException(ex);
                     }
                  }
               }
               else if(condition.equals(LESS_THAN_OR_EQUAL))
               {
                  if(typeid==DataFormatter.INTEGER || typeid==DataFormatter.NUMBER || typeid==DataFormatter.MONEY)
                  {
                     double rowVal = Double.parseDouble(rowValue);
                     double conVal = Double.parseDouble(value);
                     if(rowVal > conVal) _temp = false;
                  }
                  else if(typeid==DataFormatter.DATE || typeid==DataFormatter.DATETIME)
                  {
                     try
                     {
                        SimpleDateFormat fmt = new SimpleDateFormat(ContextSubstitutionVariables.SERVER_DATE_FORMAT);
                        Date rowVal = fmt.parse(rowValue);
                        Date conVal = fmt.parse(value);
                        if(rowVal!=null && conVal!=null && rowVal.before(conVal)) _temp = false;
                     }
                     catch (ParseException ex)
                     {
                        throw new FndException(ex);
                     }
                  }
               }
               
               found_condition_true = _temp;
            }
            
            if(condStatus && !_temp) return new String[] {"","false"};
            if(!condStatus && _temp)
               // styles = styles + css_class.trim() + " ";
               styles = css_class.trim() + " ";
            finalCondition = finalCondition && _temp;
         }
      }catch(Exception ex){
         error(ex);
      }
      return new String[] {styles.trim(),""+finalCondition};
   }
      
   /**
    * Enable possibility to show the row status in the table. Default option.
    */
   public void enableRowStatus()
   {
      try
      {
         modifyingMutableAttribute("ROW_STATUS");
         html.row_status = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable possibility to show the row status in the table.
    */
   public void disableRowStatus()
   {
      try
      {
         modifyingMutableAttribute("ROW_STATUS");
         html.row_status = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Force to enable the Quick Edit mode. Default is that this mode is synchronized
    * with the Edit function in the corresponding instance of the ASPCommandBar class
    */
   public void enableQuickEdit()
   {
      try
      {
         modifyingMutableAttribute("QUICK_EDIT");
         html.quick_edit = Boolean.TRUE;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Force to disable the Quick Edit mode.
    *
    * @see ifs.fnd.asp.ASPTable#enableQuickEdit()
    */
   public void disableQuickEdit()
   {
      try
      {
         modifyingMutableAttribute("QUICK_EDIT");
         html.quick_edit = Boolean.FALSE;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if quick edit mode is enabled.
    */
   public boolean isQuickEditEnabled()
   {
      return html.quick_edit.booleanValue();
   }

   /**
    * Enable search possibility in the table. This is default.
    */
   public void enableQueryRow()
   {
      try
      {
         modifyingMutableAttribute("QUERY_ROW");
         html.query_row = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable search possibility in the table.
    */
   public void disableQueryRow()
   {
      try
      {
         modifyingMutableAttribute("QUERY_ROW");
         html.query_row = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Enable title row in the table. Default option.
    */
   public void enableTitleRow()
   {
      try
      {
         modifyingMutableAttribute("TITLE_ROW");
         html.title_row = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable title row in the table.
    */
   public void disableTitleRow()
   {
      try
      {
         modifyingMutableAttribute("TITLE_ROW");
         html.title_row = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Show table border. Default option.
    */
   public void showBorder()
   {
      try
      {
         modifyingMutableAttribute("SHOW_BORDER");
         html.show_border = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Hide table border.
    */
   public void hideBorder()
   {
      try
      {
         modifyingMutableAttribute("SHOW_BORDER");
         html.show_border = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Show table with striped background.
    */
   public void setStripedBackground()
   {
      try
      {
         modifyingMutableAttribute("STRIPED_BACKGROUND");
         html.striped_background = Boolean.TRUE;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Show table with one background color. This is default.
    */
   public void unsetStripedBackground()
   {
      try
      {
         modifyingMutableAttribute("STRIPED_BACKGROUND");
         html.striped_background = Boolean.FALSE;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /*
   private boolean isStripedBackground()
   {
      if( html.striped_background!=null )
         return html.striped_background.booleanValue();
      else if(html.editable)
         return html.edit_striped_bkg;
      else
         return html.read_striped_bkg;
   }
    */

   /**
    * Clear the whole search row inclusive hidden fields.
    */
   public void clearQueryRow()
   {
      try
      {
         modifyingMutableAttribute("CLEAR_QUERY_ROW");
         html.clear_query_row = CLEAR_ALL;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Clear only shown fields of the search row.
    */
   public void clearDisplayedQueryRow()
   {
      try
      {
         modifyingMutableAttribute("CLEAR_QUERY_ROW");
         html.clear_query_row = CLEAR_DISPLAYED;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Set the number of rows which will be shown in the table.
    */
   public void setPageSize( int new_size ) throws FndException
   {
      try
      {
         if(getASPPage().getVersion()>=3)
             throw new FndException("PSNVIN: setPageSize is not available in Webkit version 3");
         modifyingMutableAttribute("PAGE_SIZE");
         html.page_size = new_size;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Return the number of rows which will be shown in the table.
    */
   public int getPageSize()
   {
      if( html.page_size>0 )
         return html.page_size;
      else if(html.editable)
         return html.editable_pagesize;
      else
         return html.readonly_pagesize;
   }

   /**
    * Sets height of the textarea field in editable tables.
    *
    * By default the text area height in editable table is '1'. Due to a limitation
    * in Netscape 6.2x, if the text area height in ediable table is less than 3,
    * Netscape 6.2x will print a '32px' height textarea while other browsers drows
    * according to the actual height.
    * But every supporting browsers will work exactly in same way if that height grater
    * than 2.
    * This is a mutable attribute and you can call this method even after defining the
    * page.
    * @param height the height of the textarea in editable tables.
    */

   public void setTextAreaHeight(int height)
   {

      if (height>0)
      {
         try
         {
            modifyingMutableAttribute("TEXTAREA_HEIGHT");
            html.textarea_height = height;
         }
         catch(Throwable any)
         {
            error(any);
         }
      }
   }
   /**
    * Returns the height of textarea fields.
    */

   public int getTextAreaHeight()
   {
      return html.textarea_height;
   }

   /**
    * Change the read only table as a editable table.
    */
   public void setEditable()// throws FndException
   {
      try
      {
         modifyingMutableAttribute("EDITABLE");
         html.editable = true;
         unsetSortable();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Change the editable table as a read only table.
    */
   public void unsetEditable()
   {
      try
      {
         modifyingMutableAttribute("EDITABLE");
         html.editable = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if the table is editable.
    */
   public boolean isEditable()
   {
      return html.editable;
   }


   /**
    * Enable Edit Properties command for this ASPTable
    */
   public void enableEditProperties()
   {
      try
      {
         modifyingMutableAttribute("EDIT_PROPERTIES");
         html.edit_properties = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable Edit Properties command for this ASPTable
    */
   public void disableEditProperties()
   {
      try
      {
         modifyingMutableAttribute("EDIT_PROPERTIES");
         html.edit_properties = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   boolean isEditPropertiesEnabled()
   {
      return html.edit_properties;
   }

   /**
    * Enable Output Channels command for this ASPTable
    */
   public void enableOutputChannels()
   {
      try
      {
         modifyingMutableAttribute("OUTPUT_CHANNELS");
         html.enable_outchannel = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable Output Channels command for this ASPTable
    */
   public void disableOutputChannels()
   {
      try
      {
         modifyingMutableAttribute("OUTPUT_CHANNELS");
         html.enable_outchannel = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   boolean isOutputChannelsEnabled()
   {
      return html.enable_outchannel;
   }

   /**
   * Set this table to show a popup list of commands for each row.
   */
   public void setPopup()
   {
      try
      {
         modifyingMutableAttribute("POP_COMMANDS");
         html.pop_commands = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
   * Set this table to not show a popup list of commands for each row.
   */
   public void unsetPopup()
   {
      try
      {
         modifyingMutableAttribute("POP_COMMANDS");
         html.pop_commands = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
   * Disable the HTML NOWRAP attribute for this table's content.
   */
   public void disableNoWrap()
   {
      try
      {
         modifyingImmutableAttribute("NOWRAP_DISABLED");
         html.nowrap_disabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
   * Enable the HTML NOWRAP attribute for this table's content. On by default.
   */
   public void enableNoWrap()
   {
      try
      {
         modifyingImmutableAttribute("NOWRAP_DISABLED");
         html.nowrap_disabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
   * Disable the HTML NOWRAP attribute for this table's titlerow. This is the default.
   */
   public void disableTitleNoWrap()
   {
      try
      {
         modifyingImmutableAttribute("TITLE_NOWRAP_DISABLED");
         html.title_nowrap_disabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
   * Enable the HTML NOWRAP attribute for this table's titlerow.
   */
   public void enableTitleNoWrap()
   {
      try
      {
         modifyingImmutableAttribute("TITLE_NOWRAP_DISABLED");
         html.title_nowrap_disabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
   * Disable the search link when using the queryrow.
   */
   public void disableQueryRowLink()
   {
      try
      {
         modifyingImmutableAttribute("QUERYROW_LINK");
         html.queryrow_link = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
   * Enable the search link when using the queryrow.
   */
   public void enableQueryRowLink()
   {
      try
      {
         modifyingImmutableAttribute("QUERYROW_LINK");
         html.queryrow_link = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Appearance
   //==========================================================================

   /**
    * Sets formatting to be applied to the leftmost column in the title row.
    */
   public void setTitleLeftColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("titleLeftColumn");
         html.titleLeftColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to middle columns in the title row.
    */
   public void setTitleMidColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("titleMidColumn");
         html.titleMidColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the rightmost column in the title row.
    */
   public void setTitleRightColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("titleRightColumn");
         html.titleRightColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the leftmost column in the first row.
    */
   public void setUpperLeftColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("upperLeftColumn");
         html.upperLeftColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to middle columns in the first row.
    */
   public void setUpperMidColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("upperMidColumn");
         html.upperMidColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the rightmost column in the first row.
    */
   public void setUpperRightColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("upperRightColumn");
         html.upperRightColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the rightmost column in all rows.
    */
   public void setRightColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("rightColumn");
         html.rightColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the leftmost column in all rows.
    */
   public void setLeftColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("leftColumn");
         html.leftColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the leftmost column in the last row.
    */
   public void setBottomLeftColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("bottomLeftColumn");
         html.bottomLeftColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to middle columns in the last row.
    */
   public void setBottomMidColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("bottomMidColumn");
         html.bottomMidColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to the rightmost column in the last row.
    */
   public void setBottomRightColumn( String format )
   {
      try
      {
         modifyingMutableAttribute("bottomRightColumn");
         html.bottomRightColumn = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to all columns in all rows.
    */
   public void setColumnformatting( String format )
   {
      try
      {
         modifyingMutableAttribute("columnFormatting");
         html.columnFormatting = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets formatting to be applied to all rows.
    */
   public void setRowformatting( String format )
   {
      try
      {
         modifyingMutableAttribute("rowFormatting");
         html.rowFormatting = format;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Sets column content to be wrapped at the size specified for the columns.
    */
   public void setWrap()
   {
      try
      {
         modifyingMutableAttribute("WRAP");
         html.wrap = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets column content to not be wrapped (default).
    */
   public void unsetWrap()
   {
      try
      {
         modifyingMutableAttribute("WRAP");
         html.wrap = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets the CSS style to use for the table's title row.
     * @deprecated should not change the styles at development time
    */
   public void setGridHeadStyle( String grdHead )
   {
      STYLESHEET_CLASS_MULTIROW_TH = grdHead;
   }

   /**
    * Sets the CSS style to use for the table's concent.
    * @deprecated should not change the styles at development time
    */
   public void setGridContentStyle( String grdContent )
   {
      STYLESHEET_CLASS_MULTIROW_TD = grdContent;
   }

   //==========================================================================
   //  Moving the current row in the underlying RowSet
   //==========================================================================

   /**
    * Show the next set of rows. Define the first row in the table as current row
    * in the connected row set. Try to show the last page as complete as possible.
    *
    * @see ifs.fnd.asp.ASPTable.fitToPage()
    */
   public boolean nextPage()
   {
      try
      {
         if (DEBUG) debug("ASPTable.nextPage()");

         ASPRowSet rowset = getBlock().getASPRowSet();
         int current_row_no = rowset.getCurrentRowNo();
         if (DEBUG) debug("  [nextPage:old]current_row_no="+current_row_no);
         int max_row_no = rowset.countRows()-1;
         if (DEBUG) debug("  max_row_no="+max_row_no);

         //if (current_row_no + html.page_size <= max_row_no - html.page_size + 1)
         if (current_row_no + getPageSize() <= max_row_no - getPageSize() + 1)
         {
            //return rowset.forward( html.page_size );
            return rowset.forward( getPageSize() );
         }
         //else if (max_row_no - html.page_size - current_row_no + 1 > 0)
         else if (max_row_no - getPageSize() - current_row_no + 1 > 0)
         {
            //return rowset.forward( max_row_no - html.page_size - current_row_no + 1 );
            return rowset.forward( max_row_no - getPageSize() - current_row_no + 1 );
         }
         return false;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Show the previous set of rows. Define the first row in the table as current row
    * in the connected row set.
    */
   public boolean previousPage()
   {
      try
      {
         if (DEBUG) debug("ASPTable.previousPage()");

         ASPRowSet rowset = getBlock().getASPRowSet();
         int current_row_no = rowset.getCurrentRowNo();
         int max_row_no = rowset.countRows()-1;

         if (DEBUG)
         {
            debug("  [previousPage:old]current_row_no="+current_row_no);
            debug("  max_row_no="+max_row_no);
         }

         //if (current_row_no - html.page_size >= 0)
         if (current_row_no - getPageSize() >= 0)
         {
            //return rowset.backward( html.page_size );
            return rowset.backward( getPageSize() );
         }
         else if (current_row_no > 0)
         {
            return rowset.first();
         }
         return false;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Try to define current row in the connected row set so that as many as possible
    * rows will be shown in the table.
    *
    * @see ifs.fnd.asp.ASPTable.nextPage()
    */
   public void fitToPage()
   {
      if(getASPPage().getVersion()>=3)
          return;
      try
      {
         if (DEBUG) debug("ASPTable.fitToPage()");

         ASPRowSet rowset = getBlock().getASPRowSet();
         int current_row_no = rowset.getCurrentRowNo();
         int max_row_no = rowset.countRows()-1;

         if (DEBUG)
         {
            debug("  [fitToPage:old]current_row_no="+current_row_no);
            debug("  max_row_no="+max_row_no);
         }

         //if (max_row_no - current_row_no < html.page_size - 1)
         if (max_row_no - current_row_no < getPageSize() - 1)
         {
            //if (html.page_size > max_row_no)
            if (getPageSize() > max_row_no)
               rowset.first();
            else
               //rowset.backward( html.page_size - max_row_no - 1 + current_row_no);
               rowset.backward( getPageSize() - max_row_no - 1 + current_row_no);
            current_row_no = rowset.getCurrentRowNo();
         }
         if (DEBUG) debug("  [fitToPage:new]current_row_no="+current_row_no);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Generation of HTML
   //==========================================================================

   /**
    * Generate the HTML code for this ASPTable based on the specified buffer.
    * Every sub-buffer named "DATA" will be transformed to one row.
    * Every column in the table will be formatted by the corresponding ASPField.
    *
    * populateLov() functions show an icon defined as a link instead of check box and status.
    */
   public String populateLov( ASPBuffer aspdata, int first_row )
   {
      if (getASPPage() instanceof ifs.fnd.webfeature.FndWebFeature)
         error(new FndException("Method is not allowed call from FndWebFeatures."));
      return html.populate( new BufferedDataRowCollection(aspdata,getASPPage()), first_row, LOV );
   }

   /**
    * Generate the HTML code for this ASPTable based on the defined block.
    */
   public String populateLov()
   {
      return html.populate( LOV );
   }

   /**
    * Generate the HTML code for this ASPTable based on the specified buffer.
    */
   public String populate( ASPBuffer aspdata, int first_row )
   {
      if (getASPPage() instanceof ifs.fnd.webfeature.FndWebFeature)
         error(new FndException("Method is not allowed call from FndWebFeatures."));
      return html.populate( new BufferedDataRowCollection(aspdata,getASPPage()),first_row, OVERVIEW );
   }

   /**
    * Generate the HTML code for this ASPTable based on the defined block.
    */
   public String populate()
   {
      return html.populate( OVERVIEW );
   }

   //==========================================================================
   //  Functions for letting the user (not ASPTable) generate the HTML.
   //==========================================================================

   /**
    * Initializes the rowset
    */
   public void initRowset( )
   {
      ASPRowSet rowset = getBlock().getASPRowSet();
      rows = rowset.getDataRows();
      row_count = rows.countRows();
      if(DEBUG) debug("initRowset: " + row_count + " rows");
      row_no = 0;
      col_no = 0;

      try
      {
         prepareProfileInfo(true);
         html.prepareColumnTypes();
      } catch (Exception e)
      {
         debug("initrowset: exception");
      }

      initDone = true;
   }

    /**
     * The number of rows in the rowset
     */
   public int countRows()
   {
      if( !initDone ) initRowset();
      return row_count;
   }

    /**
     * Fetched the next row. Return true if there is a new row
     */
   public boolean getNextRow( )
   {
      if( !initDone ) initRowset();
      try
      {
         if(row_no <= row_count)
         {
            row = rows.getDataRow(row_no);
            if(row==null) return false;
            if(DEBUG) debug("getNextRow");
            row_no++;
            col_no = 0;
            if(DEBUG) debug("getNextRow");
            return true;
          }
          if(DEBUG) debug("getNextRow: no more rows");
          return false;

      } catch (Exception e) {
            if(DEBUG) debug("getNextRow: exception");
            return false;
      }
    }

    /**
     * Return the number of columns that are not hidden
     */
   public int countColumns( )
   {
      int col_count = row.countColumns();
      
      int visible_columns = 0;
      int i = 0;
      while( i <= col_count )
      {

         if( !( html.column_type[col_no] == null) && !("H".equals(html.column_type[col_no])) )
         {
            visible_columns++;
         }
         i++;

      }
      return visible_columns;
   }
    /**
     * Fetch the next column in the table. Values are converted to client format
     */

    public String getNextColumn( )
    {
      int col_count = row.countColumns();
         try
         {
         while( col_no <= col_count )
            {

               if( !( html.column_type[col_no] == null) && !("H".equals(html.column_type[col_no])) )
               {
               String value = row.convertToClientString( user_columns[col_no]);
                  col_no++;
                  return value;
               }
               col_no++;
               if(DEBUG) debug("getNextColumn");
            }
            return null;
         }
         catch (Exception e)
         {
            if(DEBUG) debug("getNextColumn: exception");
            return null;
         }
    }

    /**
     * Fetches the specified column
     */
    public String getRowColumn( int col )
    {
         int count = row.countColumns();
         int col_no;
         int real_col_no;
         col_no = 0;
         real_col_no = 0;
         try
         {
            while( col_no < col && real_col_no <= count)
            {

               if( !( html.column_type[real_col_no] == null) && !("H".equals(html.column_type[real_col_no])) )
               {
                  col_no++;
               }

               real_col_no++;

            }

            if(DEBUG) debug("getRowColumn: " + real_col_no);
            return row.convertToClientString(user_columns[real_col_no]);
         }
         catch (Exception e)
         {
            if(DEBUG) debug("getRowColumn: exception");
            return null;
         }
    }

   //=============================================================================
   //  Profile
   //=============================================================================

   /**
    * Store the specified profile information in the database.
    * Given buffer must have the same structure as the buffer returned
    * from getProfile().
    *
    * @see ifs.fnd.asp.ASPManager.getProfile
    */
   public void saveProfile( ASPBuffer info )
   {
      saveProfile( info, true );
   }

   public void updateProfileBuffer( ASPBuffer info )
   {
      //used by CSL page to update all profile entries and save at once
      saveProfile( info, false );
   }

   private void saveProfile( ASPBuffer info, boolean save_it )
   {
      if(DEBUG) debug("ASPTable.saveProfile()");
      try
      {
         profile = new ASPTableProfile();
         profile.load(this,info.getBuffer());
         modifyingMutableAttribute("PROFILE");
         user_profile_prepared = false;

         ASPProfile prf = getASPPage().getASPProfile();
         if(DEBUG) debug("  profile="+prf);
         prf.update(this,profile);
         //prf.save(getASPPage());
         if (save_it)
            prf.save(this);
      }
      catch( Throwable any )
      {
         error(any);
      }

   }

   /**
    * Return an ASPBuffer containing profile information corresponding to
    * this ASPTable. The buffer may be modified and then stored by calling
    * the method saveProfile().
    *
    * @see ifs.fnd.asp.ASPManager.saveProfile
    */
   public ASPBuffer getProfile()
   {
      try
      {
         if(DEBUG) debug("ASPTable.getProfile()");
         user_profile_prepared = false; // ????
         prepareProfileInfo(false);
         ASPBuffer info = getASPManager().newASPBuffer();
         profile.save(this,info.getBuffer());
         return info;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }

   }

   /**
    * Remove profile information from database for this ASPTable.
    *
    * @see ifs.fnd.asp.ASPManager.saveProfile
    */
   public void removeProfile()
   {
      removeProfile(true);
   }

   //called by CSL page to remove profile object from profile and save at once
   public void removeFromProfileBuffer()
   {
      removeProfile(false);
   }

   private void removeProfile(boolean save_it)
   {
      if(DEBUG) debug("ASPTable.removeProfile()");
      try
      {
         ASPPage    page    = getASPPage();
         ASPProfile profile = page.getASPProfile();
         profile.remove(this);
         //profile.save(page);
         if (save_it)
            profile.save(this);
      }
      catch( Throwable any )
      {
         error(any);
      }

   }

   /**
    * Read from a text file the profile information corresponding to this ASPTable.
    *
    * @see ifs.fnd.asp.ASPManager.exportProfile
    */
   public void importProfile( String filename )
   {
      try
      {
         ASPBuffer info = getASPManager().newASPBuffer();
         String ifm = Util.readAndTrimFile(filename);
         Buffers.copy(new Message(ifm), info.getBuffer());
         saveProfile(info);
      }
      catch( Throwable any )
      {
         error(any);
      }

   }

   /**
    * Write to a text file the profile information corresponding to this ASPTable.
    *
    * @see ifs.fnd.asp.ASPManager.importProfile
    */
   public void exportProfile( String filename )
   {
      try
      {
         ASPBuffer info = getProfile();
         Message ifm = Buffers.toMessage(info.getBuffer());
         Util.writeFile(filename,ifm.format());
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   private void createBaseProfile() throws FndException
   {
      profile = new ASPTableProfile();
      profile.construct(this);
      user_profile_prepared = false;
      pre_profile = profile;
   }


   void prepareProfileInfo(boolean apply_settings) throws FndException
   {
      if( user_profile_prepared ) return;
      if(DEBUG) debug("ASPTable.prepareProfileInfo():");

      if( pre_profile==null ) createBaseProfile();

      if (!getASPManager().isDefaultLayout())
      {
         ASPProfile aspprf = getASPPage().getASPProfile();
         if(DEBUG) debug("   getASPProfile() = "+aspprf);
         profile = (ASPTableProfile)aspprf.get(ASPTable.this,pre_profile);
         if(DEBUG){
             AutoString out = new AutoString();
             profile.showContents(out);
             debug("prepareProfileInfo():"+
                   "\n\t\t  profile["+profile+"]="+out.toString());
         }
      }
      else
         profile = pre_profile;

      if (!apply_settings) return;

      modifyingMutableAttribute("PROFILE");
      if( user_columns    ==null ) user_columns     = new ASPField[column_count];
      if( user_column_size==null ) user_column_size = new int     [column_count];

      //
      //  Copy all columns[] to user_columns[]
      //
      for( int i=0; i<column_count; i++ )
      {
         user_columns[i] = columns[i];
         user_column_size[i] = -1;
      }
      //
      //  For every position (nr) in user profile
      //
      for( int nr=0; nr<profile.getColumnCount(); nr++ )
      {
         //
         //  Find ASPField that should be at position nr
         //
         ASPField col = columns[profile.getColumnIndex(nr)];
         int user_size = profile.getColumnSize(nr);
         //
         //  Find its position (k) in user_columns[]
         //
         int k = findUserColumnIndex(col,nr);
         //
         //  Swap elements k and nr in user_columns[]
         //
         if( k==nr )
         {
            user_column_size[nr] = user_size;
            continue;
         }
         if(DEBUG) debug("... swaping k="+k+" nr="+nr);
         swapUserColumn(k,nr);
         swapUserSize(k,nr);
         user_column_size[nr] = user_size;
      }

      user_profile_prepared = true;
   }

   private void swapUserColumn( int i, int j )
   {
      ASPField tmp = user_columns[i];
      user_columns[i] = user_columns[j];
      user_columns[j] = tmp;
   }

   private void swapUserSize( int i, int j )
   {
      int tmp = user_column_size[i];
      user_column_size[i] = user_column_size[j];
      user_column_size[j] = tmp;
   }

   private int findUserColumnIndex( ASPField column, int from )
   {
      for( int i=from; i<user_columns.length; i++ )
         if( user_columns[i]==column )
            return i;
      return -1;
   }

   AutoString getExpandableRowContent(int rowno) throws FndException
   {
      return html.generateExpandableRow(getBlock().getASPRowSet(), rowno);
   }
   
   // Added by Terry 20120914
   // Add property, user can disable row select opertions, disable function
   public void disableSelectionOption()
   {
      try
      {
         modifyingMutableAttribute("SELECTION_OPTION_DISABLED");
         html.selection_option_disabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Add property, user can disable row select opertions, enable function
   public void enableSelectionOption()
   {
      try
      {
         modifyingMutableAttribute("SELECTION_OPTION_DISABLED");
         html.selection_option_disabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Add property, user can disable row select opertions, getter
   public boolean isSelectionOptionDisabled()
   {
      return html.selection_option_disabled;
   }
   // Added end
   
   // Added by Terry 20120921
   // Only one row selection switch
   /**
    * Enable possibility to mark one row in the table.
    */
   
   public void enableOneRowSelect()
   {
      try
      {
         modifyingMutableAttribute("ONE_ROW_SELECT");
         if (html.row_select)
         {
            html.one_row_select = true;
            disableSelectionOption();
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disable possibility to mark one row in the table.
    */
   public void disableOneRowSelect()
   {
      try
      {
         modifyingMutableAttribute("ONE_ROW_SELECT");
         html.one_row_select = false;
         if (html.row_select)
            enableSelectionOption();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if the possibility to mark one row is enabled.
    */
   public boolean isOneRowSelectionEnabled()
   {
      return html.one_row_select;
   }
   // Added end
   
   // Added by Terry 20120929
   // Get key for multi-select LOV.
   public String getKey()
   {
      return key_name;
   }
   // Added end
   
   // Added by Terry 20131027
   // String list to ASPField vector
   private Vector listToFields( ASPBlock blk, String fieldlist, boolean addRemaining )
   {
      Vector columns_vector = new Vector();
      StringTokenizer myFields;
      int i;
      String fld;
      ASPField[] allcolumns;
      try
      {
         myFields = new StringTokenizer(fieldlist, ",");
         
         while(myFields.hasMoreTokens())
         {
            fld = myFields.nextToken();
            ASPField temp = blk.getASPField(fld);
            if (temp != null && !columns_vector.contains(temp))
               columns_vector.addElement(temp);
         }
         
         if(addRemaining)
         {
            allcolumns = blk.getFields();
            for(i = 0;i<allcolumns.length;i++)
            {
               if(!columns_vector.contains(allcolumns[i]))
                  columns_vector.addElement(allcolumns[i]);
            }
         }
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
      
      return columns_vector;
   }
   
   private String getFieldList(Vector columns)
   {
      try
      {
         AutoString tmpbuf = new AutoString();
         tmpbuf.clear();
         for( int i=0; i<columns.size(); i++ )
         {
            if( i>0 ) tmpbuf.append(',');
            ASPField fld = (ASPField) columns.elementAt(i);
            tmpbuf.append(fld.getName());
         }
         return tmpbuf.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }
   // Added end

//=============================================================================
//=============================================================================
//  Private class for generation of HTML
//=============================================================================
//=============================================================================


private class HTMLGenerator
{
   //==========================================================================
   //  Persistent mutable attributes
   //==========================================================================

   int     page_size;              int     pre_page_size;
   int     textarea_height =1;     int     pre_textarea_height = 1;
   boolean row_select;             boolean pre_row_select;
   // Added by Terry 20120921
   // Only one row selection switch
   boolean one_row_select;         boolean pre_one_row_select;
   // Added end
   // Added by Terry 20120914
   // Add property, user can disable row select opertions
   boolean selection_option_disabled; boolean pre_selection_option_disabled;
   // Added end
   boolean row_counter;            boolean pre_row_counter;
   boolean row_status;             boolean pre_row_status;
   boolean query_row;              boolean pre_query_row;
   boolean title_row;              boolean pre_title_row;
   boolean show_border;            boolean pre_show_border;
   //boolean striped_background;  boolean pre_striped_background;
   Boolean striped_background;     Boolean pre_striped_background;
   boolean editable;               boolean pre_editable;
   boolean sortable;               boolean pre_sortable;
   boolean edit_properties;        boolean pre_edit_properties;
   boolean enable_outchannel;      boolean pre_enable_outchannel;
   boolean detail_link;            boolean pre_detail_link;
   boolean queryrow_link;          boolean pre_queryrow_link;
   String[] row_condidtions;       String[] pre_row_condidtions;

   // ASPPopup
   boolean pop_commands;        boolean pre_pop_commands;

   boolean wrap;                boolean pre_wrap;

   String  titleLeftColumn = "";      String pre_titleLeftColumn = "";
   String  titleMidColumn = "";       String pre_titleMidColumn = "";
   String  titleRightColumn = "";     String pre_titleRightColumn = "";

   String  upperLeftColumn = "";      String pre_upperLeftColumn = "";
   String  upperMidColumn = "";       String pre_upperMidColumn = "";
   String  upperRightColumn = "";     String pre_upperRightColumn = "";
   String  leftColumn = "";           String pre_leftColumn = "";
   String  rightColumn = "";          String pre_rightColumn = "";
   String  bottomLeftColumn = "";     String pre_bottomLeftColumn = "";
   String  bottomMidColumn = "";      String pre_bottomMidColumn = "";
   String  bottomRightColumn = "";    String pre_bottomRightColumn = "";
   String  columnFormatting = "";     String pre_columnFormatting = "";
   String  rowFormatting = "";        String pre_rowFormatting = "";

   // TODO: add the last variables to pagepool, dofreeze, doreset

   //==========================================================================
   //  Transient mutable attributes
   //==========================================================================

   transient Boolean quick_edit;        transient Boolean pre_quick_edit;
   transient int     clear_query_row;   transient int     pre_clear_query_row;

   //==========================================================================
   //  Transient immutable attributes
   //==========================================================================

   private transient ASPCommandBar cmd_bar;
   private transient AbstractDataRowCollection  data_rows;
   private transient int           populate_type;
   private transient boolean       show_qedit;
   private transient int           key_pos;
   private transient int           col_cnt;
   private transient boolean       nowrap_disabled;
   private transient boolean       title_nowrap_disabled;
   
   private transient int           sys_col_width = 0;
   private transient int           tot_col_size  = 0;

   //==========================================================================
   //  Transient temporary variables
   //==========================================================================

   private transient AutoString out_str          = new AutoString();
   private transient AutoString editable_columns = new AutoString();

   private transient AutoString visible_col        = new AutoString();
   private transient AutoString visible_col_width  = new AutoString();

   //==========================================================================
   //  Immutable contsants - Table definitions, special rows and columns
   //==========================================================================

   private transient int     editable_pagesize;
   private transient int     readonly_pagesize;
   //private transient boolean edit_striped_bkg;
   //private transient boolean read_striped_bkg;

   //private transient String title_row_bgcolor;
   //private transient String title_row_font_color;
   //private transient String title_row_font_face;

   //private transient String bgcolor;
   //private transient String bgcolor2;

   private transient int    selbox_col_width;
   private transient int    status_col_width;
   private transient int    status_img_width;
   private transient int    status_img_height;

   private transient String lov_image;
   private transient int    lov_col_width;
   private transient int    lov_img_width;
   private transient int    lov_img_height;
   private transient int    run_lov_img_width;

   private transient String check_box_checked_image;
   private transient String check_box_unchecked_image;
   private transient int    check_box_img_width;
   private transient int    check_box_img_height;

   private transient String hyperlink_img;
   private transient int    hyperlink_img_width;
   private transient int    hyperlink_img_height;

   private transient String  properties_img;
   private transient boolean allow_properties;

   private transient String  out_channel_img;
   private transient boolean allow_out_channel;

   //private transient String script_location;
   //private transient String images_location;
   private transient String alt_text;

   private transient String normal_img;
   private transient String new_img;
   private transient String modify_img;
   private transient String remove_img;

   private transient String normal_btn_img;
   private transient String new_btn_img;
   private transient String modify_btn_img;
   private transient String remove_btn_img;

   private transient String sort_img_asc;
   private transient String sort_img_desc;
   private transient String sort_img_width;
   private transient String sort_img_height;

   private transient String empty_img;

   //private transient String cellpad_editable;
   //private transient String cellpad_readonly;

   private transient String[]          column_type;
   private transient HyperlinkColumn[] column_hyperlink;

   //==========================================================================
   //  Temporary variables
   //==========================================================================
   private boolean                     cmdNewDefined;
   private boolean                     cmdModifyDefined;
   private boolean                     cmdRemoveDefined;
   private boolean                     rowConditionStatus;
   private String                      rowConditionStyle;

   //==========================================================================
   //  Construction
   //==========================================================================


   HTMLGenerator()
   {
      edit_properties = true;
      enable_outchannel = true;
      queryrow_link = true;
      sortable = true;
      // Modified by Terry 20120821
      // Original: title_nowrap_disabled = true;
      title_nowrap_disabled = false;
      // Modified end
      
      // Added by Terry 20120914
      // Add property, user can disable row select opertions, default set false.
      selection_option_disabled = false;
      // Added end
   }


   //==========================================================================
   //  populate() functions called from ASPTable
   //==========================================================================


   String populate( int populate_type )
   {
      try
      {
         if (DEBUG) debug("ASPTable$HTMLGenerator.populate("+populate_type+")");
         if(getASPPage().getVersion() ==3)
             getBlock().getASPRowSet().first();

         ASPRowSet rowset = getBlock().getASPRowSet();
         return populate( rowset.getDataRows(), rowset.getCurrentRowNo(), populate_type );
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }

   void checkDefinedCommands()
   {
        cmdNewDefined = getBlock().isCommandDefined(ASPRowSet.NEW);
        cmdModifyDefined = getBlock().isCommandDefined(ASPRowSet.MODIFY);
        cmdRemoveDefined = getBlock().isCommandDefined(ASPRowSet.REMOVE);
   }

   String populate( AbstractDataRowCollection data, int first_row, int populate_type )
   {
      if(getASPManager().isMobileVersion())
         return mobilePopulate(data, first_row, populate_type);

      TraceEvent populate_event = populate_event_type.begin();

      try
      {
         if (DEBUG)
         {
            debug("ASPTable$HTMLGenerator.populate("+data+","+first_row+","+populate_type+")");
            if (!data.isNULL()) data.trace("   Aspdata:");
         }

         String block_name = getBlock().getName();
         String state_name = "__"+block_name+"_ROWSTATUS";

         prepareProfileInfo(true);
         prepareColumnTypes();

         checkDefinedCommands(); // check if new, modify, remove commands are defined (needed for popup)

         out_str.clear();
         out_str.append('\n');

         key_pos = getKeyColumnPosition();
         if (key_pos<0) return "";

         this.populate_type = populate_type;

         calculateSystemColumnWidth();
         calculateTotalColumnSize();

         // Generate and get block param filed collection ========================================
         getBlock().generateBlockParams();
         Object blockParamFileds[] = getBlock().getParamFields();
         String blockParamFieldArray = "var "+getBlock().getParamArrayName()+" = new Array(";
         if(blockParamFileds!=null)
         {
            for(int pf=0; pf<blockParamFileds.length; pf++)
            {
               blockParamFieldArray += "\""+((ASPField)blockParamFileds[pf]).getName()+"\"";
               if(pf!=blockParamFileds.length-1)
                  blockParamFieldArray += ",";
            }
            blockParamFieldArray += ");\n";
         }
         // ======================================================================================
         
         tableDefinitionBegin();

         int row_count = 0;

         if( !data.isNULL())
         {
            data_rows = data;
            row_count = data.countDataRows();//Items( DATA );
            //if( row_count > this.page_size + first_row ) row_count = this.page_size + first_row;
         }

         // Extract the first and last visible column
         int first_col = -1;
         int last_col  = -1;

         for( int c=0; c<column_count; c++ )
            if( !( column_type[c] == null) && (! "H".equals(column_type[c])) && first_col == -1) first_col = c;
         for( int c=column_count-1; c>=0; c-- )
            if( !( column_type[c] == null) && (! "H".equals(column_type[c])) && last_col == -1) last_col = c;

         //if(getASPPage().getVersion()<3)
         generateSearchRow(state_name,(row_count==0 || !editable) ? -1 : 0);
         generateTitleRow(first_col,last_col);

         if( data.isNULL())
         {

            tableDefinitionEnd(-1,null);
            return out_str.toString();
         }

         AbstractDataRow firstrow = row_count==0 ? null : data_rows.getDataRow(0);
         if( column_hyperlink==null ) column_hyperlink = new HyperlinkColumn[column_count];
         for( int c=0; c<column_count; c++ )
         {
            ASPField column = user_columns[c];
            column_hyperlink[c] = firstrow==null ? null : createHyperlinkColumn(column,firstrow);
         }

         editable_columns.clear();
         editable_columns.append(',');

         visible_col.clear();
         visible_col.append(',');

         visible_col_width.clear();
         visible_col_width.append(',');


         if(DEBUG) debug("populate: first_col = " + first_col + ", last_col = " + last_col);

         int row_nr = 0;
         //boolean alt_bgcolor = ((row_count-first_row)%2==0 && striped_background) ? true : false;
         boolean alt_bgcolor = false; //((row_count-first_row)%2==0 && isStripedBackground()) ? true : false;

         history_data = new Vector();

         link_command_list = new Vector();
         Vector tmp_items = new Vector();
         link_function ="";
         String link_command = "";
         String field_name = "";
         String page_path = "";
         StringTokenizer fields=null;
         int param_count = 0;

         link_count = 0;
         Vector items = getBlock().getASPCommandBar().getAllCustomCommands();
         ASPCommandBarItem itm = null;
         for(int i=0;i<items.size();i++){
             itm = (ASPCommandBarItem)items.elementAt(i);
             if(itm.getCommandId().startsWith("LINK_CMD")){
                tmp_items.add(itm);
                link_count++;
             }
         }

         for(int j=0;j<link_count;j++)
			{
             itm = (ASPCommandBarItem)tmp_items.elementAt(j);
             boolean rwc_link_cmd = itm.getCommandId().startsWith("LINK_CMD_RWC");
             fields = new StringTokenizer(itm.getLinkFieldList(),",");
             param_count = fields.countTokens()-1;
             page_path = fields.nextToken();
             if(fields.hasMoreTokens())
             {
                while(fields.hasMoreTokens())
                {
                   field_name = fields.nextToken();
                   if(editable)
                   {
                      link_command += "'"+field_name+"='+f."+field_name+"[i+1].value+'&'+"; //hidden fields is editable tables.
						 }
                   else
                   {
                      link_command += "'"+field_name+"='+f."+field_name+"[i].value+'&'+";
                   }
                }
                link_command = (link_command.length()>5)? link_command.substring(0,link_command.length()-5): link_command;
             }
             if(editable)
             {
                if (rwc_link_cmd)
                   link_function +="\nfunction "+getASPPage().addProviderPrefix()+itm.getCommandId()+"_"+getBlock().getName()+"(i){navigateToRWC('"+page_path+"','"+link_command+"');}\n";
                else
                   link_function +="\nfunction "+getASPPage().addProviderPrefix()+itm.getCommandId()+"_"+getBlock().getName()+"(i){"+(itm.isLinkInNewWin()?"showNewBrowser":"location.replace")+"('"+page_path+((param_count > 0)?((page_path.indexOf("?")<0)?"?":"&")+"'+"+link_command:"'")+");}\n";
             }
             else
             {
                if (rwc_link_cmd)
                   link_function +="\nfunction "+getASPPage().addProviderPrefix()+itm.getCommandId()+"_"+getBlock().getName()+"(i){navigateToRWC('"+page_path+"',"+getASPPage().addProviderPrefix()+"link_params_"+getTableNo()+"[(i*"+link_count+")+"+(j)+"]);}\n";
                else
                   link_function +="\nfunction "+getASPPage().addProviderPrefix()+itm.getCommandId()+"_"+getBlock().getName()+"(i){"+(itm.isLinkInNewWin()?"showNewBrowser":"location.replace")+"('"+page_path+((param_count>0)?((page_path.indexOf("?")<0)?"?":"&")+"'+"+getASPPage().addProviderPrefix()+"link_params_"+getTableNo()+"[(i*"+link_count+")+"+(j)+"]":"'")+");}\n";
             }
             link_command = "";
         }

         sub_menu_calls = new AutoString();
         popup_list = new AutoString();
         sub_popup_list = new AutoString();

         //History Stuff
         lu_name = getBlock().getLUName();
         lu_keys = getBlock().getLUKeys();

         StringBuffer sb = null;
         StringTokenizer keys = null;
         String[] lu_key_arr = null;
         int index = 0 ;

         if(!getASPManager().isEmpty(lu_name) && !getASPManager().isEmpty(lu_keys))
         {
            keys = new StringTokenizer(lu_keys, "^");
            lu_key_arr = new String[keys.countTokens()];

            while(keys.hasMoreTokens())
            {
               lu_key_arr[index] = keys.nextToken();
               index++;
            }
         }

         String blockParamValueArray = "var "+getBlock().getParamValueArrayName()+" = new Array(\n";
         
         for( int r=first_row; r<row_count; r++ )
         {
            row_nr = r - first_row + 1;
            AbstractDataRow rowitem = data_rows.getDataRow(r);
            if (rowitem==null) continue; // NOT a data row
            String rowstatus = data_rows.getRowStatus(r);//rowitem.getStatus();

            //History Stuff
            if(!getASPManager().isEmpty(lu_name) && !getASPManager().isEmpty(lu_keys))
            {
               ASPField fld_name = null;
               String fld_value = "";
               sb = new StringBuffer();
               for(int i=0; i<lu_key_arr.length; i++)
               {
                  fld_name = getASPPage().getASPField(lu_key_arr[i]);
                  fld_value = getASPManager().URLEncode(rowitem.convertToClientString(fld_name));
                  sb.append(lu_key_arr[i] + "=" + fld_value + "^");
               }

               history_data.addElement(sb.toString());
            }

            link_command = "";
            for(int i = 0;i<link_count;i++)
            {
               itm  = (ASPCommandBarItem)tmp_items.elementAt(i);
               fields = new StringTokenizer(itm.getLinkFieldList(),",");
               page_path = fields.nextToken();
               if(fields.hasMoreTokens())
               {
                  while(fields.hasMoreTokens())
                  {
                     field_name = fields.nextToken();
                     ASPField f = getASPPage().getASPField(field_name);
                     String val = getASPManager().URLEncode(rowitem.getValue(f));
                     if(!getASPManager().isEmpty(val))
                     {
                        link_command += field_name+"="+val+"&";
                     }
                   }
                   if(itm.getCommandId().startsWith("LINK_CMD"))
                   {
                      if(!getASPManager().isEmpty(link_command))
                      {
                         link_command_list.addElement(link_command.substring(0,link_command.length()-1));
                      }
                      else
                      {
                         link_command_list.addElement("");
                      }
                   }
               }
               else
               {
                  link_command_list.addElement(page_path);
               }
               link_command = "";
            }

            //===============================================================================
            if(blockParamFileds!=null)
            {
               blockParamValueArray += "new Array(";
               for(int pf=0; pf<blockParamFileds.length; pf++)
               {
                  String value = rowitem.convertToClientString((ASPField)blockParamFileds[pf]);
                  blockParamValueArray += "\""+getASPManager().JScriptEncode(value)+"\"";
                  if(pf!=blockParamFileds.length-1)
                     blockParamValueArray += ",";
               }
               blockParamValueArray += ")";
               if(r!=row_count-1)
                  blockParamValueArray += ",\n";
            }
            //===============================================================================
            
            if(hasRowConditions())
            {
               rowConditionStyle = getRowConditionStyles(r);
               if(!getASPManager().isEmpty(rowConditionStyle))
                  rowConditionStatus = true;
               else
                  rowConditionStatus = false;
            }
            
            if ( ! rowFormatting.equals("") )
            {
               out_str.append("<TR ");
               out_str.append(rowFormatting);
            }
            else
            {
               out_str.append("\t<TR ALIGN=LEFT");
               //if(striped_background)
               //if(isStripedBackground())
               alt_bgcolor = !alt_bgcolor;
               if(alt_bgcolor)
                  out_str.append(" class=\"tableRowColor1\"");
               else
                  out_str.append(" class=\"tableRowColor2\"");
                  //out_str.append(" bgcolor=\"",bgcolor2,"\"");
            }

            String table_id = "cnt"+getBlock().getName();

            // Modified by Terry 20120921
            // Original:
            // if(row_select)
            //    out_str.append(editable?"":" onClick=rowClicked("+(r+1)+",'",table_id,"',this,f."+getASPPage().addProviderPrefix()+SELECTBOX+table_nr+","+r+")");
            // Check one row select switch
            if(row_select)
            {
               if (!one_row_select)
                  out_str.append(editable?"":" onClick=rowClicked("+(r+1)+",'",table_id,"',this,f."+getASPPage().addProviderPrefix()+SELECTBOX+table_nr+","+r+")");
               else
                  out_str.append(editable?"":" onClick=rowClicked_("+(r+1)+",'",table_id,"',this,f."+getASPPage().addProviderPrefix()+SELECTBOX+table_nr+","+r+")");
            }
            // Modified end
            else if("Y".equalsIgnoreCase(getASPPage().getASPConfig().getParameter("ADMIN/ENABLE_RULER","Y")))
               out_str.append(editable?"":" onClick=rowClicked("+(r+1)+",'",table_id,"',this)");

            if(getBlock().hasExpandableFields())
               out_str.append(";addExpandRow('",getName(),"',"+(r+1)+",this,"+column_count+","+getBlock().getExpandableTabs()+")");

            ASPField keycolumn = user_columns[key_pos];
            String keyvalue = rowitem.convertToClientString(keycolumn);

            // Added by Terry 20120821
            // When LOV table, double click to select value.
            // When Normal table, double click to view details.
            if(!editable)
            {
               if (this.populate_type==LOV)
               {
                  if (this.row_select)
                     out_str.append(" onDblClick=\"javascript:getAllSelectedValues(",table_nr + ")\"");
                  else
                     out_str.append(" onDblClick=\"javascript:setValue('",keyvalue + "')\"");
               }
               else
               {
                  String cmdname = ASPCommandBar.VIEWDETAILS;

                  String prefix = getASPPage().addProviderPrefix();
                  String cmd_prefix = prefix;
                  if(!Str.isEmpty(prefix))
                     prefix += ".";

                  out_str.append(" onDblClick=\"javascript:",cmd_prefix + "initPop" + table_nr + "(" + r + ");");
                  out_str.append(cmd_prefix + "setTableCommand"+table_nr+"('');commandSet('" + prefix + getBlock().getName() + "." + cmdname + "','')\"");
               }
            }
            //Added end
            out_str.append(" >");
            
            addSystemColumn( r, first_row, row_count, keyvalue, false );

            int nr = 0;
            for( int c=0; c<column_count; c++ )
            {
               ASPField column = user_columns[c];
               if(column.isExpandable() && !column.isHidden()) continue;
               String html_type = column_type[c];
               if( html_type==null ) continue;

               String value;

               DataFormatter formatter = column.getDataFormatter();
               if (column.isAccurateFld())
                  formatter = column.getAccurateClientFormatter();

               value = rowitem.convertToClientString(column,formatter);
               value = (!getASPManager().isEmpty(value))? value: "";
               
               if( "H".equals(html_type) )
               {
                  if(editable)
                     appendHiddenField(column.getName(),value,row_nr,false);
                  continue;
               }

               nr++;

               if(nr==2 && editable)
                  appendHiddenField(state_name,rowstatus,row_nr,false);

                                        if(r == first_row)
                                        {
                                                visible_col_width.appendInt(user_column_size[c]);
                                                visible_col_width.append(',');
                                                visible_col.append(column.getName(), ","); //only for first row
                                        }

                                        out_str.append("</TD>\n"); //--new

                                        /*
                                        if(column.getTooltipField()!=null)
                                        {
                                                ASPManager mgr = getASPManager();
                                                ASPField tip_field = column.getTooltipField ();
                                                String tip = toSingleLine(tip_field.convertToClientString ((String)row.getItem(tip_field.getDbName()).getValue()));
                                                tip = mgr.replace(mgr.HTMLEncode(tip),"\'","\\\'");
                                                appendTDTag(column,r==first_row,r==(row_count-1),c,c==first_col,c==last_col,tip);
                                        }
                                        else
                                           appendTDTag(column,r==first_row,r==(row_count-1),c,c==first_col,c==last_col);
                                         */
                                        appendTDTag(column,r==first_row,r==(row_count-1),c,c==first_col,c==last_col,row_nr);

               if( column.isCheckBox() )
               {
                   if (isEditable())
                       appendHiddenField(column.getName(),
                                         column.getCheckedValue().equals(value)?value:null,
                                         row_nr,
                                         false);
                  if( "I".equals(html_type) && !column.isReadOnly(rowstatus) )
                     appendCheckBoxField(rowitem,column,column_hyperlink[c],value,row_nr);
                  else
                     appendCheckBoxImage(rowitem,column,column_hyperlink[c],value);
               }
               else if(column.isImageField())
                  appendImageField(r,rowitem,column,column_hyperlink[c], getASPPage().getImageFieldTag(column, getBlock().getASPRowSet(), r));
               else if( column.isSelectBox() )
               {
                  if( "I".equals(html_type) )
                  {
                     if( column.isReadOnly(rowstatus) )
                        appendInputField(rowitem,column,column_hyperlink[c],value,row_nr,rowstatus,c);
                     else
                        appendSelectBoxField(rowitem,column,column_hyperlink[c],value,row_nr,rowstatus);
                  }
                  else
                     appendTextField(rowitem,column,column_hyperlink[c],value,c);
               }
               else if (column.getHeight()>1 && "I".equals(html_type) && !column.isReadOnly(rowstatus))
                  appendAreaBoxField(rowitem,column,column_hyperlink[c],value,row_nr,rowstatus,c);
               else if( "T".equals(html_type) )
                  appendTextField(rowitem,column,column_hyperlink[c],value,c);
               else
                  appendInputField(rowitem,column,column_hyperlink[c],value,row_nr,rowstatus,c);

               //out_str.append("</TD>\n");
            }
            out_str.append("</TD>\n"); //--new
            if(getBlock().hasExpandableFields())
            {
               out_str.append("\n\t<TD onclick=\"__onSystemCol=false;\" id=\"__expTemp",getName()+(r+1),"\">&nbsp;");
               out_str.append("<div id=\"__exp",getName()+(r+1),"\" style=\"display:none\">\n");
               out_str.append(getASPPage().drawExpandableRow(getName(),r+1));
               out_str.append("</div></TD>\n");
            }
            
            out_str.append("\t</TR>\n");
            rowConditionStatus = false;
            rowConditionStyle = "";
         }
         tableDefinitionEnd(row_nr, IfsNames.dbToAppName(block_name));

         // append block param filed names and values onto html output
         blockParamValueArray += ");\n";
         if(blockParamFileds!=null && blockParamFileds.length>0)
         {
            out_str.append("<script language=javascript>\n");
            out_str.append(blockParamFieldArray);
            out_str.append(blockParamValueArray);
            out_str.append("</script>\n");
         }
         // ==========================================================
         
         String visible = visible_col.toString();
         String col_width = visible_col_width.toString();
         String editables = editable_columns.toString();
         if(DEBUG) debug("   editable_columns = "+editables);
         getASPPage().getASPContext().writeValue("__"+block_name+"_VISIBLE",visible);
         getASPPage().getASPContext().writeValue("__"+block_name+"_COLWIDTH",col_width);
         getASPPage().getASPContext().writeValue("__"+block_name+"_COLUMNS",editables);
         return out_str.toString();
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
      finally
      {
         user_profile_prepared = false;
         populate_event.end();
      }
   }
   
   private boolean isUserColumnHidden( int c )
   {
      if (user_columns[c].isDirtyHiddenFlag())
         return user_columns[c].isHidden();
      return user_columns[c].isHidden() || user_column_size[c]<0;
   }


   void fetchConfigParameters()
   {
      ASPManager mgr = ASPTable.this.getASPManager();
      ASPConfig  cfg = ASPTable.this.getASPPage().getASPConfig();

      //title_row_bgcolor    = cfg.getParameter("SCHEME/HEADER/BGCOLOR",   "#000000");
      //title_row_font_color = cfg.getParameter("SCHEME/HEADER/FONT/COLOR","#FFFFFF");
      //title_row_font_face  = cfg.getParameter("SCHEME/HEADER/FONT/FACE", "Arial");

      //cfg.getParameter("SCHEME/FORM/BGCOLOR",   "#C0C0C0");
      //bgcolor              = cfg.getFormBgColor();
      //bgcolor2             = cfg.getParameter("SCHEME/FORM/ALT_BGCOLOR", bgcolor);

      selbox_col_width   = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/CHECK_BOX/WIDTH",        "20"));
      status_col_width   = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/ROW_STATUS/WIDTH",       "18"));
      status_img_width   = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/ROW_STATUS/IMAGE/WIDTH", "12"));
      status_img_height  = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/ROW_STATUS/IMAGE/HEIGHT","11"));

      lov_image          = cfg.getParameter("TABLE/COLUMN/STATUS/RETURN/IMAGE/NAME","ClickIndicator.gif");
      lov_col_width      = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/RETURN/WIDTH",       "18"));
      lov_img_width      = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/RETURN/IMAGE/WIDTH", "11"));
      lov_img_height     = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/STATUS/RETURN/IMAGE/HEIGHT","11"));
      run_lov_img_width  = Integer.parseInt(cfg.getParameter("LOV/IMAGE/WIDTH",                        "13"));

      check_box_checked_image   = cfg.getParameter("TABLE/COLUMN/CHECK_BOX/IMAGE/CHECKED",  "check_box_marked.gif");
      check_box_unchecked_image = cfg.getParameter("TABLE/COLUMN/CHECK_BOX/IMAGE/UNCHECKED","check_box_unmarked.gif");
      check_box_img_width       = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/CHECK_BOX/IMAGE/WIDTH", "13"));
      check_box_img_height      = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/CHECK_BOX/IMAGE/HEIGHT","13"));

      hyperlink_img        = cfg.getParameter("TABLE/COLUMN/HYPERLINK/IMAGE/NAME", "table_hyperlink.gif");
      hyperlink_img_width  = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/HYPERLINK/IMAGE/WIDTH", "15"));
      hyperlink_img_height = Integer.parseInt(cfg.getParameter("TABLE/COLUMN/HYPERLINK/IMAGE/HEIGHT","11"));

      properties_img   = cfg.getParameter("TABLE/PROPERTIES/IMAGE","table_properties.gif");
      allow_properties = "Y".equals(cfg.getParameter("TABLE/PROPERTIES/ALLOW_EDIT","Y"));

      out_channel_img   = cfg.getParameter("TABLE/OUTPUT_CHANNELS/IMAGE","table_output_channel.gif");
      allow_out_channel = "Y".equals(cfg.getParameter("TABLE/OUTPUT_CHANNELS/ENABLED","N"));

      //script_location = cfg.getScriptsLocation();
      //images_location = cfg.getImagesLocation();
      alt_text        = mgr.translateJavaText("FNDTABALT: Click to return value");

      normal_img      = cfg.getParameter("ROW_STATUS/IMAGE/NORMAL","row_normal.gif");
      new_img         = cfg.getParameter("ROW_STATUS/IMAGE/NEW"   ,"row_new.gif");
      modify_img      = cfg.getParameter("ROW_STATUS/IMAGE/MODIFY","row_modify.gif");
      remove_img      = cfg.getParameter("ROW_STATUS/IMAGE/REMOVE","row_remove.gif");

      normal_btn_img  = cfg.getParameter("ROW_STATUS/IMAGE/NORMAL_BUTTON","row_normal_btn.gif");
      new_btn_img     = cfg.getParameter("ROW_STATUS/IMAGE/NEW_BUTTON"   ,"row_new_btn.gif");
      modify_btn_img  = cfg.getParameter("ROW_STATUS/IMAGE/MODIFY_BUTTON","row_modify_btn.gif");
      remove_btn_img  = cfg.getParameter("ROW_STATUS/IMAGE/REMOVE_BUTTON","row_remove_btn.gif");

      sort_img_asc    = cfg.getParameter("TABLE/SORT/IMAGE/ASC",   "table_sort_asc.gif");
      sort_img_desc   = cfg.getParameter("TABLE/SORT/IMAGE/DESC",  "table_sort_desc.gif");
      sort_img_width  = cfg.getParameter("TABLE/SORT/IMAGE/WIDTH", "7");
      sort_img_height = cfg.getParameter("TABLE/SORT/IMAGE/HEIGHT","6");

      empty_img       = cfg.getParameter("TABLE/PROPERTIES/EMPTY_IMAGE","table_empty_image.gif");

      //cellpad_editable = cfg.getParameter("TABLE/CELLPADDING_WITHOUT_BORDER/EDITABLE","1");
      //cellpad_readonly = cfg.getParameter("TABLE/CELLPADDING_WITHOUT_BORDER/READONLY","2");
   }


   private void calculateSystemColumnWidth() throws Exception
   {
      if (this.populate_type==LOV)
         sys_col_width = lov_col_width;
      else
      {
         sys_col_width = 0;
         if (this.row_select) sys_col_width += selbox_col_width;
         if (this.row_status) sys_col_width += status_col_width;
      }
   }


   private void calculateTotalColumnSize()
   {
      tot_col_size = (int)(0.5 + sys_col_width / sign_width);
      col_cnt = sys_col_width==0 ? 0 : 1;

      for( int c=0; c<column_count; c++ )
      {
         if( isUserColumnHidden(c) ) continue;
         ASPField column = user_columns[c];

         col_cnt++;
         int size = user_column_size[c];
         if ( size>0 && tot_col_size>=0 )
         {
            tot_col_size += size;
            if (column.hasLOV() && column.isLOVActive())
               tot_col_size += (int)(1.0 + run_lov_img_width / sign_width);
//            if(editable && column.getHyperlinkURL()!=null && !column.isSelectBox())
//               tot_col_size += (int)(1.0 + hyperlink_img_width / sign_width);
         }
         else
            tot_col_size = -1;
      }
   }

   private void tableDefinitionBegin()
   {

/*
      // Optional "frame" borders
      out_str.append("<TABLE width=100%>");
      out_str.append("<tr><td width=10><img src=" + images_location + "top_left.gif></td>");
      out_str.append("<td><img src=" + images_location + "top.gif width=100%></td>");
      out_str.append("<td width=10><img src=" + images_location + "top_right.gif></td></tr>");
      out_str.append("<tr><td><img src=left.gif></td><td>");
*/
      link_command_script = "";

      //page frame start
      out_str.append("<table cellspacing=0 cellpadding=0 border=0 width=100%>");
      out_str.append("<tr>");
      // Added by Terry 20140906
      // Show frame space mark
      if (getBlock().hasCommandBar() && getBlock().getASPCommandBar().isCmdShowFrameSpace())
         out_str.append("<td>&nbsp;&nbsp;</td>");
      // Added end
      out_str.append("<td width=100%>");

      out_str.append("<TABLE id=\"cnt",getBlock().getName(),"\"");

      out_str.append(" CLASS=\"", STYLESHEET_CLASS_MULTIROW, "\"");
      out_str.append( " BORDER=", (this.show_border ? "1" : "0") );

      ASPBlockLayout lay = getBlock().getASPBlockLayout();

//      if(lay.isMinimized(lay.getID()) && getASPManager().isExplorer())

      if(getASPManager().isExplorer() && (lay.isMinimized(lay.getID()) || (!lay.isMaximized(lay.getID()) && lay.isInitiallyMinimized())))
            out_str.append(" style=\"display: none;\"");

      // Modified by Terry 20120717
      // Modify cellspacing from 0 to 1
      // Original: out_str.append(" CELLPADDING=2 CELLSPACING=0>\n");
      out_str.append(" CELLPADDING=2 CELLSPACING=1>\n");
      // Modified end

//      out_str.append(" CELLPADDING=0 CELLSPACING=0 COLS=");
//      out_str.append( col_cnt + ">\n");
   }


   private void tableDefinitionEnd( int cnt_rows, String name )
   {
      out_str.append("</TABLE>\n");

      //page frame end
      out_str.append("</td>");
      
      // Added by Terry 20140906
      // Show frame space mark
      if (getBlock().hasCommandBar() && getBlock().getASPCommandBar().isCmdShowFrameSpace())
         out_str.append("<td>&nbsp;&nbsp</td>");
      // Added end
      
      out_str.append("</tr></table>");

      if( pop_commands )
      {

         String prefix = getASPPage().addProviderPrefix();

//         out_str.append("<input type=hidden name=\"",prefix,"__", getBlock().getName(), "_PERFORM\">");

         ASPBlock blk = getBlock();
         blk.appendPerformField(out_str);

         out_str.append("\n<script language=javascript>\n");

         out_str.append("var ",prefix,"popup_list_"+getTableNo()+" = new Array("+popup_list+"'');\n");

         if(isRowSelectionEnabled())
           out_str.append("var ",prefix,"sub_popup_list_"+getTableNo()+" = new Array("+sub_popup_list+"'');\n");

         if(!editable)
         {
            out_str.append("var ",prefix,"link_params_"+getTableNo()+" = new Array("+link_command_script.substring(0,link_command_script.length())+"'');\n");
         }
         out_str.append(link_function);

         out_str.append("\n");
         out_str.append("var __sub_menu_call"+getTableNo()+" = new Array("+sub_menu_calls+");\n");
         out_str.append("\n");

         //History Stuff
         if(!getASPManager().isEmpty(lu_name) && !getASPManager().isEmpty(lu_keys))
         {
            out_str.append("\n");
            out_str.append("var __lu_name"+getTableNo()+" = \""+lu_name+"\";\n");
            out_str.append("var __history_params"+getTableNo()+" = new Array(");
            for(int i=0; i<history_data.size(); i++)
            {
               if(i==history_data.size()-1)
                  out_str.append("\""+history_data.elementAt(i)+"\"");
               else
                  out_str.append("\""+history_data.elementAt(i)+"\",\n");
            }
            out_str.append(");\n");
            out_str.append("\n");
         }

         out_str.append("function ",prefix,"initPop");
         out_str.appendInt(table_nr);
         out_str.append("(row)\n{\n");
         out_str.append("\t","__customPopupRow = row;\n");
         out_str.append("\t","tblRow");
         out_str.appendInt(table_nr);
         out_str.append(" = row;\n}\n");
         out_str.append("function ",prefix,"setTableCommand");
         out_str.appendInt(table_nr);
         out_str.append("(cmd)\n{\n\t");
         //clear all old settings first!

         out_str.append("\n\tdeselectAllRows('",prefix,"__SELECTED"+getTableNo()+"');\n");

         out_str.append("\tfor(__x=0;__x<");
         out_str.append("f.",prefix,"__SELECTED");
         out_str.appendInt(table_nr);
         out_str.append(".length");
         out_str.append(";__x++)\n\t{\n\t\t");
         out_str.append("f.",prefix,"__SELECTED");
         out_str.appendInt(table_nr);

         if (this.row_select)
         {
            out_str.append("[__x].checked=false;");
         }
         else
         {
            out_str.append("[__x].value=\"\";");
         }
         out_str.append("\n\t}\n\n\t");
         out_str.append("f.",prefix,"__", getBlock().getName(), "_" + ASPCommandBar.PERFORM + ".value = cmd;\n\t");

         out_str.append("f.", prefix, "__SELECTED");
         out_str.appendInt(table_nr);

         if (cnt_rows > 1)
         {
            out_str.append("[tblRow");
            out_str.appendInt(table_nr);
            out_str.append("]");
         }
         if(this.row_select)
         {
            out_str.append(".checked = true;\n");

            //Hilite the selected row
            out_str.append("\t","CCA(");
            out_str.append("f.",prefix,"__SELECTED");
            out_str.appendInt(table_nr);
            if(cnt_rows > 1)
            {
               out_str.append("[");
               out_str.append("tblRow");
               out_str.appendInt(table_nr);
               out_str.append("]");
             }
            out_str.append(",tblRow");
            out_str.appendInt(table_nr);
            out_str.append(");\n");
         }
         else
         {
            out_str.append(".value = tblRow");
            out_str.appendInt(table_nr);
         }
         out_str.append(";\n");
         out_str.append("}\n");

         if(this.row_select)
         {
            out_str.append("__row_select=true;\n");
         }


         out_str.append("</script>\n");
      }

      if( editable )
      {
         out_str.append("<script language=javascript>\n");
         out_str.append("function check",name,"TableFields()\n{\n\t");
         if(cnt_rows>0)
            for(int i=1;i<=cnt_rows;i++)
            {
               if(i==1)
                  out_str.append("return ");
               out_str.append("check",name,"Fields(");
               out_str.appendInt(i);
               out_str.append(")");
               if(i==cnt_rows)
                  out_str.append(";\n");
               else
                  out_str.append(" &&\n\t");
            }
         else
            out_str.append("return true;\n");
         out_str.append("}\n</script>\n");
      }
   }


   private void generateSearchRow( String state_name, int row_nr ) throws Exception
   {
      ASPManager mgr = ASPTable.this.getASPManager();
      out_str.append("\t<TR>\n");
//      appendHiddenField(state_name,null,0,false);
      appendHiddenField(state_name,"QueryMode__",0,false);

      if(this.query_row)
         addSystemColumn( SEARCHROW, 0, 0, null, true );

      for( int c=0; c<column_count; c++ )
      {
         ASPField column = user_columns[c];
         if((column.isImageField() && !column.hasTemplate()) || (column.isExpandable() && !column.isHidden())) continue;         
         String name = column.getName();
         String value;

         if ( column.hasGlobalConnection() )
            value = column.getGlobalValue();
         else if( this.clear_query_row==CLEAR_ALL ||
                  (this.clear_query_row==CLEAR_DISPLAYED &&
                   !isUserColumnHidden(c) &&
                    column.isQueryable()) )
            value = "";
         else
            value = Str.nvl(Util.trimLine(getASPPage().readValue(name)),"");

         if( isUserColumnHidden(c) )
            appendHiddenField(name,value,-1,false);
         else if( !this.query_row )
            appendHiddenField(name,value,-1,!mgr.isExplorer() && !column.isCheckBox());
         else
         {
            if( !column.isQueryable() )
               appendHiddenField(name,value,-1,!mgr.isExplorer());

            out_str.append("\t\t<TD");

            // The browser used is not Internet Explorer
            // ( => a CSS class reference is required on each <TD> tag)
            //if ( ! getASPManager().isExplorer() )
            out_str.append(" CLASS=\"",STYLESHEET_CLASS_MULTIROW_TD, "\"");

            if(!nowrap_disabled)
               out_str.append(" NOWRAP");
            out_str.append(">");

            int size = user_column_size[c]; //column.getSize();

            if( column.isQueryable() )
            {
               out_str.append("<input type=text name=",name," value=\"",value,"\"");

               out_str.append(" class='editableTextField'");

               if( size>0 )
               {
                   out_str.append(" SIZE=");
                   out_str.appendInt(size);
               }
               if( column.isUpperCase() )
               {
                  out_str.append(" OnChange=\"toUpper_('",column.getName(),"',");
                  out_str.appendInt(row_nr);
                  out_str.append(")\"");
               }

               if( column.hasLOV() && column.isLOVActive() )
                  column.appendLOVTag(out_str,row_nr,false);
               out_str.append(">"); // close the INPUT tag
            }
            else
            {
               out_str.append("<FONT class=normalTextValue>");
               for(int i=0; i<size; i++)
                  out_str.append('x');
               out_str.append("</FONT>");
            }

            out_str.append("</TD>\n");
         }
      }

      if(this.query_row && this.queryrow_link)
          out_str.append("\n\n<TD><a href=\"javascript:commandSet('" + getBlock().getName() + "." + ASPCommandBar.OKFIND + "','')\">" + getASPManager().translateJavaText("FNDTABLEQUERYROWSEARCH: Search") + "</a>");

      out_str.append("\t</TD></TR>\n");
      //  ?????   this.clear_query_row = CLEAR_NOT;
   }


   private void generateTitleRow(int first_col, int last_col) throws Exception
   {
      ASPPage page = getASPPage();

      if (this.title_row)
      {
         out_str.append("\t<TR>\n");
         addSystemColumn( TITLEROW, 0, 0, null, true );
         ASPRowSet rowset = getBlock().getASPRowSet();
         String sorted_column = rowset.getSortedColumnName();

         //String space_img = getASPManager().getEmptyImage(1,1);

         for( int c=0; c<column_count; c++ )
         {
            if( isUserColumnHidden(c) ) continue;
            ASPField column = user_columns[c];
            if(column.isExpandable()) continue;
            String   name   = column.getName();

            // Extra space between
//            out_str.append("\t\t<TH>  </TH>");

            if( !html.sortable && (!column.isImageField() || column.hasTemplate()))// to show help on fields when table is not sortable
               out_str.append("\t\t<TH ONCLICK=\"javascript:showHelpTag('"+ column.getUsageID()+ "')\" VALIGN=\"BOTTOM\""); 
            else
               out_str.append("\t\t<TH VALIGN=\"BOTTOM\""); //här börjar det!!
            if(!title_nowrap_disabled)
               out_str.append(" NOWRAP");
            out_str.append(" CLASS=\"",  STYLESHEET_CLASS_MULTIROW_TH, "\"");
//            out_str.append(" BGCOLOR=\"", title_row_bgcolor, "\"");

            if(getASPPage().getVersion() < 3)
               if (!this.query_row)
                  appendColumnWidth(c);

            if( c == first_col && !titleLeftColumn.equals("") ) out_str.append(titleMidColumn);
            if( !(c == first_col) && !(c == last_col) && !titleMidColumn.equals("") ) out_str.append(titleMidColumn);
            if( c == last_col && !titleRightColumn.equals("") ) out_str.append(titleRightColumn);

            out_str.append(">");

//            int size = user_column_size[c]; //column.getSize();
            String label = Str.nvl(column.getTitle(),"&nbsp;");
//            out_str.append( size>0 && label.length()>size ? label.substring(0,size+1) : label );

            String sort_anchor = null;
            if( html.sortable )
            {
               sort_anchor = "<a class="+STYLESHEET_CLASS_MULTIROW_TH_LINK+" tabindex=-1 href=\"javascript:if(helpMode) showHelpTag_('"+ column.getUsageID()+ "');"+((!column.isImageField())?" else sort_('"+getName()+"','"+name+"','"+getSortFunction()+"');":"")+"\">";
               
               out_str.append(sort_anchor);
//               out_str.append("<a href=\"javascript:sort_('",getName(),"','");
//               out_str.append(name,"','",getSortFunction(),"')\">");
            }
            out_str.append("<font onmouseover=\"if(helpMode) changeToHelpModeColor(this, '"+STYLESHEET_CLASS_MULTIROW_TH_TEXT+"','"+ column.getUsageID()+ "')\" onmouseout=\"changeFromHelpModeColor(this, '"+STYLESHEET_CLASS_MULTIROW_TH_TEXT+"')\" class=",STYLESHEET_CLASS_MULTIROW_TH_TEXT,">");
            if(column.getTitleWrap() > 0)
               label = wrapText(label,column.getTitleWrap());
            out_str.append(label);
            if((!column.isReadOnly()) && (isEditable()) &&((column.isDateTime())||(column.isTime())))
            {
               AutoString html = new AutoString();
               column.appendTranslatedMask(html);
               out_str.append("<br>");
               out_str.append(html.toString());
            }
            out_str.append("</font>");

            //out_str.append(label,"</font>");
            if(column.hasAggregateReference())
               name = column.getDbName();
            if(sorted_column!=null && sorted_column.equals(name))
            {
               String images_location =  page.getASPConfig().getImagesLocation() + getASPManager().getUserTheme() + "/";

               out_str.append("</a>&nbsp;",sort_anchor);
               out_str.append("<IMG SRC=\"", images_location);
//               out_str.append("&nbsp;<IMG SRC=\"", images_location);
               if(rowset.isSortedAscending())
               {
                  out_str.append(sort_img_asc, "\" alt=\"");
                  out_str.append(getASPManager().translateJavaText("FNDTABSORTASC: Sorted ascending"));
                  out_str.append("\" title=\"");
                  out_str.append(getASPManager().translateJavaText("FNDTABSORTASC: Sorted ascending"));

               }
               else
               {
                  out_str.append(sort_img_desc, "\" alt=\"");
                  out_str.append(getASPManager().translateJavaText("FNDTABSORTDESC: Sorted descending"));
                  out_str.append("\" title=\"");
                  out_str.append(getASPManager().translateJavaText("FNDTABSORTDESC: Sorted descending"));

               }
               out_str.append("\" BORDER=0 VALIGN=middle>");
            }
            if( html.sortable )
               out_str.append("</a>");
            out_str.append("</TH>\n");
//            out_str.append("</a></TH>\n");
         }
         
         if(getBlock().hasExpandableFields())
            out_str.append("\t\t<TH CLASS=\"",  STYLESHEET_CLASS_MULTIROW_TH_EXP, "\" width=\"100%\">&nbsp;</TH>\n");
         
         out_str.append("\t</TR>\n");
      }
   }


   private void appendColumnWidth( int nr )
   {
      if (tot_col_size>0)
      {
         int size = user_column_size[nr];
         out_str.append(" WIDTH=");
         out_str.appendInt( (int)(100.0*size/tot_col_size+0.5) );
         out_str.append('%');
      }
   }


   private void appendSystemColumnWidth()
   {
      if (tot_col_size>0)
      {
         out_str.append(" WIDTH=");
         out_str.appendInt((int)(100.0*sys_col_width/sign_width/tot_col_size+0.5));
         out_str.append('%');
      }
      else
      {
         out_str.append(" WIDTH=");
         out_str.appendInt(sys_col_width);
      }
   }


   private void addSystemColumn( int      row_no,
                                 int      first_row,
                                 int      row_count,
                                 String   value,
                                 boolean  close_tag ) throws Exception
   {
      if (DEBUG) debug("ASPTable$HTMLGenerator.addSystemColumn("+row_no+","+first_row+","+row_count+","+value+","+close_tag+")");

      // Don't display the system column if neither row selection nor status is enabled.
      if ( this.row_select || this.row_status )
      {
         switch (row_no)
         {
            case SEARCHROW :

               this.show_qedit = this.quick_edit!=null ? this.quick_edit.booleanValue()
                  : ( this.cmd_bar!=null &&
                     !this.cmd_bar.findItem(ASPCommandBar.EDIT).checkRemoved() &&
                     !this.cmd_bar.findItem(ASPCommandBar.EDIT).checkDisabled() ? true : false );

               if(this.show_qedit)
                  out_str.append("<td>&nbsp;</td>");
               // Modified by Terry 20140912
               // Original:
               // out_str.append("\t\t<TD NOWRAP VALIGN=TOP");
               out_str.append("\t\t<TD NOWRAP VALIGN=MIDDLE");
               // Modified end
               out_str.append(" CLASS=\"", STYLESHEET_CLASS_MULTIROW_SYS_TD, "\"");
               if(close_tag)
                  out_str.append("</TD>\n");
               break;
            case TITLEROW :
               ASPPage page = getASPPage();

               this.show_qedit = this.quick_edit!=null ? this.quick_edit.booleanValue()
                  : ( this.cmd_bar!=null &&
                     !this.cmd_bar.findItem(ASPCommandBar.EDIT).checkRemoved() &&
                     !this.cmd_bar.findItem(ASPCommandBar.EDIT).checkDisabled() ? true : false );
               // Modified by Terry 20140912
               // Original:
               // out_str.append("<TH  VALIGN=TOP class=\"", STYLESHEET_CLASS_MULTIROW_SYS_TH,"\" ");
               out_str.append("<TH  VALIGN=MIDDLE class=\"", STYLESHEET_CLASS_MULTIROW_SYS_TH,"\" ");
               // Modified end


               boolean newmode = page.compatibility.newEditMode();
               boolean gen_output_ch = this.enable_outchannel && newmode && this.allow_out_channel;
               boolean gen_edit_prop = false;//this.edit_properties   && newmode && this.allow_properties;
               if(gen_output_ch || gen_edit_prop || isRowSelectionEnabled())
                   {
                       out_str.append(">");

                       out_str.append("&nbsp;</TH><TH class=\"", STYLESHEET_CLASS_MULTIROW_SYS_TH,"\" VALIGN=BOTTOM>");

                       Vector list = new Vector();
                       boolean showPopup = false;
                       // Modified by Terry 20120914
                       // Original: if(isRowSelectionEnabled())
                       if(isRowSelectionEnabled() && !isSelectionOptionDisabled())
                       {
                          list.addElement("true");
                          list.addElement("true");
                          list.addElement("true");
                       }
                       
                       // Original: int done = isRowSelectionEnabled()?3:0;
                       int done = (isRowSelectionEnabled() && !isSelectionOptionDisabled())?3:0;
                       // Modified end
                       if(gen_output_ch)
                       {
                          for(int x=done;x<tool.countItems()-1;x++) //Q&D: enable all, except editprop.
                             list.addElement("true");
                       }
                       else
                       {
                          for(int x=done;x<tool.countItems()-1;x++) //Q&D: disable all, except editprop.
                             list.addElement("false");
                       }

                        if(list.contains("true"))
                           showPopup = true;

                        if(showPopup)
                        {
                           String images_location = page.getASPConfig().getImagesLocation();
                           out_str.append("<a href=\"");

                           out_str.append(tool.generateCall(list));
                           out_str.append("\"><img onclick=\"javascript:menuClicked(this);\" border=0 src=\"" + images_location + getASPPage().getASPConfig().getParameter("TABLE/TABLE_MENU_IMAGE","table_menu.gif") + "\"></a>");
                        }

                       //if(close_tag)
                          out_str.append("&nbsp;</TH>\n");
                   }
               else
                   {
                       out_str.append(">&nbsp;</TH><TH class=\"", STYLESHEET_CLASS_MULTIROW_SYS_TH,"\" VALIGN=BOTTOM >&nbsp;</TH>");

//                       if(close_tag)
//                           out_str.append(">&nbsp;</TH>\n");
                   }
               break;
            default :
               out_str.append("\t\t<TD NOWRAP");
               out_str.append(" CLASS=\"", STYLESHEET_CLASS_MULTIROW_SYS_TD, "\" ");

               if (this.populate_type==LOV)
                  // Modified by Terry 20140912
                  // Original:
                  // out_str.append(" VALIGN=TOP ALIGN=center");
                  out_str.append(" VALIGN=MIDDLE ALIGN=center");
                  // Modified end
               else
                  // Modified by Terry 20140912
                  // Original:
                  // out_str.append(" VALIGN=TOP ALIGN=right");
                  out_str.append(" VALIGN=MIDDLE ALIGN=right");
                  // Modified end

               if (!this.query_row && !this.title_row && row_no==first_row)
                  appendSystemColumnWidth();
               out_str.append(">");

               if (value==null) value = "";

               if (this.populate_type==LOV)
               {

                  // Bug 43086, start
                  // Modified by Terry 20140912
                  // Original:
                  // out_str.append("&nbsp;</TD><TD NOWRAP VALIGN=TOP ALIGN=left ");
                  out_str.append("&nbsp;</TD><TD NOWRAP VALIGN=MIDDLE ALIGN=left ");
                  // Modified end
                  //if ( ! getASPManager().isExplorer() )
                  out_str.append(" CLASS=\"", STYLESHEET_CLASS_MULTIROW_SYS_TD, "\"");
                  out_str.append(">");
                  // Bug 43086, end

                  if(row_counter)
                  {
                     out_str.append("&nbsp;");
                     out_str.appendInt(row_no + getBlock().getASPRowSet().countSkippedDbRows() + 1);
                  }

                  // Convert quotes (otherwise the value can't be placed in the link)
                  String safe_value = Str.replace(Str.replace(value,"\"","<DQ>"),"'","<SQ>");
                  safe_value = Str.replace(safe_value,"\\","\\\\"); //replace \ with \\
                  
                  String images_location = getASPManager().getASPConfig().getImagesLocationWithRTL();

                  out_str.append("<a href=\"javascript:setValue('",safe_value, "')", "\">");
                  out_str.append("<img src=\"", images_location, lov_image, "\"");
                  out_str.append(" border=0");
                  out_str.append(" height=");
                  out_str.appendInt(lov_img_height);
                  out_str.append(" width=");
                  out_str.appendInt(lov_img_width);
                  out_str.append(" alt=\"", alt_text, "\" title=\""+alt_text+ "\" ></a>");

                  if (this.row_select)
                  {
                     out_str.append("<input type=checkbox name=\"__SELECTED"+table_nr+"\"");
                     out_str.append(" value=\"");
                     out_str.appendInt(row_no);
                     out_str.append("\"");
                     if (ASPRowSet.SELECTED.equals( data_rows.getDataRow(row_no).getType()))
                        out_str.append(" CHECKED");
                     out_str.append(" onClick=\"CCA(this,"+(row_no+1)+");\" ");
                     out_str.append("class='checkbox'>");

                     out_str.append("<input type=hidden name=\"__SELECTED_VALUE"+table_nr+"\"");
                     out_str.append(" value=\"");
                     out_str.append(safe_value);
                     out_str.append("\">");
                  }
               }
               else
                  showNoLovSystemColumnContents( row_no, first_row, row_count, value );

               if(close_tag)
                  out_str.append("</TD>\n");
         }
      }
   }

   /*private void appendEditPropertiesCommand() throws FndException
   {
      if(getASPPage().getASPLov() == null)
      {
         ASPPage page = getASPPage();

         String script_location = page.getASPConfig().getScriptsLocation();
         String images_location = page.getASPConfig().getImagesLocation();

         AutoString url = new AutoString();
         url.append(script_location);
         url.append("ASPTableProfile.page?URL=",page.getPagePath(),"&OBJNAME=",getName());

         out_str.append("<a href=\"javascript:showNewBrowser('",url.toString(),"')\">");
         out_str.append("<img src=\"",images_location,properties_img,"\"");
         out_str.append(" alt=\"");
         out_str.append(getASPManager().translateJavaText("FNDTABPROPALT: Edit properties"));
         out_str.append("\" title=\"");
         out_str.append(getASPManager().translateJavaText("FNDTABPROPALT: Edit properties"));
         out_str.append("\" width=15 height=16 border=0>");
         out_str.append("</a>");
      }
   }*/

   private void appendOutputChannelsCommand() throws FndException
   {
      ASPConfig cfg =getASPPage().getASPConfig();
      String images_location = cfg.getImagesLocation();

      out_str.append("<a href=\"javascript:commandSet(\'",getBlock().getName(),".OutputChannel\','')\">");
      out_str.append("<img src=\"",images_location,out_channel_img,"\"");
      out_str.append(" alt=\"");
      out_str.append(getASPManager().translateJavaText("FNDTABCHANALT: Output Channel"));
      out_str.append("\" title=\"");
      out_str.append(getASPManager().translateJavaText("FNDTABCHANALT: Output Channel"));
      out_str.append("\" width=18 height=16 border=0>");
      out_str.append("</a>");
   }

   private void showNoLovSystemColumnContents( int    row_no,
                                               int    first_row,
                                               int    row_count,
                                               String value ) throws Exception
   {
      if (DEBUG) debug("ASPTable$HTMLGenerator.showNoLovSystemColumnContents("+row_no+","+first_row+","+row_count+","+value+")");

      if (row_no==first_row)
      {
         this.cmd_bar = getBlock().getASPCommandBar();
         this.show_qedit = this.quick_edit!=null ? this.quick_edit.booleanValue()
            : ( this.cmd_bar!=null &&
               !this.cmd_bar.findItem(ASPCommandBar.EDIT).checkRemoved() &&
               !this.cmd_bar.findItem(ASPCommandBar.EDIT).checkDisabled() ? true : false );

         if (DEBUG)
         {
            debug("  quick_edit="+quick_edit);
            if (quick_edit!=null) debug("  quick_edit.booleanValue()="+quick_edit.booleanValue());
            if (cmd_bar!=null)
            {
               debug("  cmd_bar.findItem(EDIT).checkRemoved()="+cmd_bar.findItem(ASPCommandBar.EDIT).checkRemoved());
               debug("  cmd_bar.findItem(EDIT).checkDisabled()="+cmd_bar.findItem(ASPCommandBar.EDIT).checkDisabled());
            }
            debug("  show_qedit="+show_qedit);
         }
      }

      if(row_counter)
      {
         if (this.show_qedit)
         {

            String cmdname = ASPCommandBar.VIEWDETAILS;

            String prefix = getASPPage().addProviderPrefix();
            String cmd_prefix = prefix;
            if(!Str.isEmpty(prefix))
               prefix += ".";

            if(getBlock().getASPCommandBar().IsEnabled(ASPCommandBar.VIEWDETAILS) && isDetailLinkEnabled())
            {
               out_str.append("<a href=\"javascript:",cmd_prefix + "initPop" + table_nr + "(" + (row_no - getBlock().getASPRowSet().getCurrentRowNo()) + ");");
               out_str.append(cmd_prefix + "setTableCommand"+table_nr+"('');commandSet('" + prefix + getBlock().getName() + "." + cmdname + "','')\">");
            }
            out_str.appendInt(row_no + getBlock().getASPRowSet().countSkippedDbRows() + 1);
            if(isDetailLinkEnabled())
               out_str.append("</a>&nbsp;");
            // Modified by Terry 20140912
            // Original:
            // out_str.append("</td><td nowrap valign=top class=",STYLESHEET_CLASS_MULTIROW_SYS_TD,">");
            out_str.append("</td><td nowrap VALIGN=MIDDLE class=",STYLESHEET_CLASS_MULTIROW_SYS_TD,">");
            // Modified end
         }
         else
            // Bug 43086, start
            // Modified by Terry 20140912
            // Original:
            // out_str.append("&nbsp;</td><td nowrap valign=top class=",STYLESHEET_CLASS_MULTIROW_SYS_TD,">&nbsp;");
            out_str.append("&nbsp;</td><td nowrap VALIGN=MIDDLE class=",STYLESHEET_CLASS_MULTIROW_SYS_TD,">&nbsp;");
            // Modified by Terry 20140912
            // Bug 43086, end

         if(!editable && link_command_list.size()>0)
            for(int i=0;i<link_count;i++)
               link_command_script += "'"+((String)link_command_list.elementAt((row_no - getBlock().getASPRowSet().getCurrentRowNo())*link_count+i))+"',\n";

      }
      else
         // Bug 43086, start
         // Modified by Terry 20140912
         // Original:
         // out_str.append("&nbsp;</td><td nowrap valign=top class=",STYLESHEET_CLASS_MULTIROW_SYS_TD,">&nbsp;");
         out_str.append("&nbsp;</td><td nowrap VALIGN=MIDDLE class=",STYLESHEET_CLASS_MULTIROW_SYS_TD,">&nbsp;");
         // Modified end
         // Bug 43086, end


      if (!this.row_select && !pop_commands)
      {
         String images_location = getASPManager().getASPConfig().getImagesLocation();
         out_str.append("<img src=\"",images_location,empty_img,"\" height=12 width=1>");
      }

      if (this.row_select)
      {
         out_str.append("<input type=checkbox name=",getASPPage().addProviderPrefix(),SELECTBOX);
         out_str.appendInt(table_nr);
         out_str.append(" value=\"");
         out_str.appendInt(row_no);
         out_str.append("\"");
         if (ASPRowSet.SELECTED.equals( data_rows.getDataRow(row_no).getType()))// data_buffer.getItem(row_no).getType() )) {
             out_str.append(" CHECKED");

         out_str.append(" onClick=\"CCA(this,"+(row_no+1)+");\" ");
         out_str.append("class='checkbox'>");     // close the INPUT tag
      }
      else if ((this.show_qedit && row_no==first_row) || pop_commands)
      {
         out_str.append("<input type=hidden name=",getASPPage().addProviderPrefix(),SELECTBOX);
         out_str.appendInt(table_nr);
         out_str.append(" value=\"","\">");
      }

      // Commands popup
      if( pop_commands ) {

         Vector list = new Vector();
         ASPCommandBar bar = getBlock().getASPCommandBar();
         Vector cmds = bar.getCustomCommands();

         boolean showPopup = false;

         if(bar.IsEnabled(bar.EDITROW) && cmdModifyDefined)
            list.addElement("true");
         else
            list.addElement("false");
         if(bar.IsEnabled(bar.DELETE) && cmdRemoveDefined)
            list.addElement("true");
         else
            list.addElement("false");
         if(bar.IsEnabled(bar.DUPLICATEROW) && cmdNewDefined)
            list.addElement("true");
         else
            list.addElement("false");

         //History Stuff
         if(!getBlock().isHistoryDisabled())
            if(!getASPManager().isEmpty(getBlock().getLUName()) && !getASPManager().isEmpty(getBlock().getLUKeys()))
               list.addElement("true");
            else
               list.addElement("false");

/*
         if(bar.IsEnabled(bar.VIEWDETAILS) && (!isQuickEditEnabled() || !isRowCounterEnabled()))
            list.addElement("true");
         else
            list.addElement("false");
*/

         if(list.contains("true"))
            showPopup = true;

         bar.prepareProfileInfo(true);

         for(int i = 0;i < cmds.size();i++)
         {
            ASPCommandBarItem cmd = (ASPCommandBarItem) cmds.elementAt(i);
            if(cmd.conditionsTrue(row_no) && !cmd.isUserDisabled())
            {
               if(cmd.isRemovedFromRowActions())
                  list.addElement("'false'");
               else
                  list.addElement("true");
               showPopup = true;
            }
            else
               list.addElement("false");
         }

         // Custom Groups
         Vector sub_list = new Vector();
         Vector pop_items = null;
         ASPPopup sub_pop = null;
         String sub_call = "";
         String action = "";
         boolean has_items = false;
         Vector items = bar.getAllCustomCommands();
         String cmd_grp = "";
         int group_index = 0;

         for(int i = 0;i < bar.getCustomCommandGroups().size();i++)
         {
            has_items = false;
            sub_list.removeAllElements();
            cmd_grp = (String)bar.getCustomCommandGroups().elementAt(i);

            for(int j=0; j<items.size(); j++)
            {
               if(((ASPCommandBarItem)items.elementAt(j)).getCustomGroup().equals(cmd_grp) && !((ASPCommandBarItem)items.elementAt(j)).checkRemoved())
               {
                  if(((ASPCommandBarItem)items.elementAt(j)).isUserDisabled() || !((ASPCommandBarItem)items.elementAt(j)).conditionsTrue(row_no))
                     sub_list.addElement("false");
                  else
                  {
                     if(((ASPCommandBarItem)items.elementAt(j)).isRemovedFromRowActions())
                        sub_list.addElement("'false'");
                     else
                        sub_list.addElement("true");
                     has_items = true;
                  }
               }
            }

            if(!has_items)
               list.addElement("s_false");
            else
               list.addElement("s_true");
            sub_popup_list.append(sub_list.toString()+",\n");

            if(getASPPage().isPopupExist("t"+cmd_grp))
               sub_pop = getASPPage().getASPPopup("t"+cmd_grp);

            if(sub_pop != null && has_items)
               sub_call = sub_pop.generateCall(sub_list);
            else
               sub_call = "";

            if(sub_menu_calls.length() > 0)
               sub_menu_calls.append(",\n\""+sub_call+"\"");
            else
               sub_menu_calls.append("\""+sub_call+"\"");

            pop_items = popup.getItems();

            for(int x =0; x <pop_items.size(); x++)
            {
               action = ((ASPPopup.menuItem)pop_items.elementAt(x)).getAction();
               if(action.indexOf(cmd_grp) > 0)
               {
                  ASPPopup.menuItem mi = (ASPPopup.menuItem)pop_items.elementAt(x);
                  mi.setAction("javascript:eval(__sub_menu_call"+table_nr+"[(tblRow"+table_nr+" *"+bar.getCustomCommandGroups().size()+")+"+group_index+"]);");
               }
            }

            if (bar.findGroup(bar.getCustomCommandGroups().elementAt(i).toString()))
            {
               //list.addElement("true");
               showPopup = true;
            }
            /*
            else
               list.addElement("false");
            */
            group_index++;
         }

         if(showPopup)
         {

            String images_location = getASPManager().getASPConfig().getImagesLocation();

            String call = popup.generateCall(getASPPage().addProviderPrefix() + "initPop" + table_nr + "(" + (row_no - getBlock().getASPRowSet().getCurrentRowNo()) + ")",list);
            addToPopupList(call.substring(call.indexOf("["),call.indexOf("]")+1));
            out_str.append("<a href=\"");
            out_str.append(call);
            //out_str.append(popup.generateCall(getASPPage().addProviderPrefix() + "initPop" + table_nr + "(" + (row_no - getBlock().getASPRowSet().getCurrentRowNo()) + ")",list));
            out_str.append("\"><img onclick=\"javascript:menuClicked(this);\" border=0 src=\"" + images_location + "Action.gif\"></a>");
         }
         else
            addToPopupList("[]");

      }


      if (this.row_status && getASPPage().getVersion() < 3)
      {
         String status = data_rows.getDataRow(row_no).getStatus();//data_buffer.getItem( row_no ).getStatus();
/*
         if      ( ASPRowSet.NEW.equals(status) )
            status = this.show_qedit ? new_btn_img    : new_img;
         else if ( ASPRowSet.MODIFY.equals(status) )
            status = this.show_qedit ? modify_btn_img : modify_img;
         else if ( ASPRowSet.REMOVE.equals(status) )
            status = this.show_qedit ? remove_btn_img : remove_img;
         else
            status = this.show_qedit ? normal_btn_img : normal_img;

         AutoString valbuf = DEBUG ? new AutoString() : out_str;
         if (this.show_qedit)
         {
            String cmdname = getASPPage().compatibility.newEditMode()
                              ? ASPCommandBar.TOGGLE
                              : ASPCommandBar.EDIT;

            valbuf.append("<a HREF=\"javascript:quickEdit('");
            valbuf.appendInt(row_no);
            valbuf.append("','");
            valbuf.appendInt(first_row);
            valbuf.append("','",this.cmd_bar.getBlockPrefix(true), cmdname, "','");
            valbuf.append(Str.nvl(this.cmd_bar.findItem(cmdname).getClientFunction(),""), "','");
            valbuf.appendInt(table_nr);
            valbuf.append("','", (this.row_select ? (row_count-first_row>1 ? "M" : "O") : "D"), "')\">");
            valbuf.append("<img src=\"", images_location, status , "\" border=0 ");
            valbuf.append("height=");
            valbuf.appendInt(status_img_height);
            valbuf.append(" width=");
            valbuf.appendInt(status_img_width);
            valbuf.append(" alt=\"");
            valbuf.append(getASPManager().translateJavaText("FNDTABQEDIT: Show/Edit row"));
            valbuf.append("\"></a>&nbsp;");
         }
         else
         {
            valbuf.append("<img src=\"", images_location, status, "\" border=0 ", "height=");
            valbuf.appendInt(status_img_height);
            valbuf.append(" width=");
            valbuf.appendInt(status_img_width);
            valbuf.append('>');
         }

         if (DEBUG)
         {
            debug(" value="+valbuf);
            out_str.append(valbuf);  // copy value buffer to out_str in DEBUG mode
         }
*/
         if      ( ASPRowSet.NEW.equals(status) )     status = new_img;
         else if ( ASPRowSet.MODIFY.equals(status) )  status = modify_img;
         else if ( ASPRowSet.REMOVE.equals(status) )  status = remove_img;
         else                                         status = normal_img;

         String images_location = getASPManager().getASPConfig().getImagesLocation();

         out_str.append("<img src=\"", images_location, status, "\" border=0 ", "height=");
         out_str.appendInt(status_img_height);
         out_str.append(" width=");
         out_str.appendInt(status_img_width);
         out_str.append('>');
      }
   }


   private void addToPopupList(String list)
   {
       popup_list.append(list+",\n");
   }

   //==========================================================================
   //  Keys
   //==========================================================================

   private int getKeyColumnPosition()
   {
      if (DEBUG) debug("ASPTable$HTMLGenerator.getKeyColumnPosition()");

      if( key_name==null ) // no key defined, return first non hidden column
      {
         for( int c=0; c<column_count; c++ )
         {
            if( isUserColumnHidden(c) ) continue;
            if (DEBUG) debug("  c="+c);
            return c;
         }
         return -1;
      }
      else  // key defined, find its position
      {
         for( int c=0; c<column_count; c++ )
         {
            ASPField column = user_columns[c];
            if( column.getName().equals(key_name) )
            {
               if (DEBUG) debug("  c="+c);
               return c;
            }
         }
         return -1;
      }
   }


   String getKeyColumnName() throws FndException
   {
      if (DEBUG) debug("ASPTable$HTMLGenerator.getKeyColumnName()");
      prepareProfileInfo(true);
      int pos = getKeyColumnPosition();
      return pos<0 ? null : user_columns[pos].getDbName();
   }

   //=============================================================================
   //  Hyperlins
   //=============================================================================

   private class HyperlinkColumn
   {
      String     colurl;       // URL for this column
      boolean    pres_obj_ok; // whether user has PO security for the page pointed to by the hyperlink
      // int[]      parposition;  // position in the DATA buffer for each parameter
      ASPField[] parfield;     // ASPField for each parameter
      // Added by Terry 20131028
      // Can accept fields aliases
      String[]   parfieldalias;
      // Added end
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      ASPField   urlfield;
      // Added end
   }

   private HyperlinkColumn createHyperlinkColumn( ASPField field,
                                                  AbstractDataRow firstrow ) throws Exception
   {
      HyperlinkColumn hcol = new HyperlinkColumn();
      
      hcol.colurl = field.getHyperlinkURL();
      
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      if (field.isFieldHyperLinked())
         hcol.urlfield = field.getHyperlinkField();
      // Added end
      

      String hyperlink_presobj = field.getHyperlinkedPresObjectId();

      // Modified by Terry 20121218
      // Original:
      // if( hcol.colurl != null)
      if( hcol.colurl != null || hcol.urlfield != null)
      // Modified end
      {
         // Modified by Terry 20131028
         // Can accept fields aliases
         // Original:
         // ASPField[] params = field.getHyperlinkParameters();
         Vector fields = new Vector();
         Vector aliases = new Vector();
         field.getHyperlinkParameters(fields, aliases);
         ASPField[] params = new ASPField[fields.size()];
         fields.copyInto(params);
         String[] params_aliases = new String[aliases.size()];
         aliases.copyInto(params_aliases);
         hcol.parfieldalias = params_aliases;
         // Modified end
         int param_count    = (params!=null)? params.length: 0;
//         hcol.parposition  = new int[param_count];
         hcol.parfield      = params;

//         for( int p=0; p<param_count; p++ )
//         {
//            ASPField parameter = params[p];
//            hcol.parposition[p] = firstrow.getAttributePosition(parameter.getDbName());
//         }

         if (!Str.isEmpty(hyperlink_presobj))
           hcol.pres_obj_ok = getASPPage().isObjectAccessible(hyperlink_presobj);  // 'secure' hyperlink hence check security
         else
           hcol.pres_obj_ok = true;   //not a 'secure' hyperlink no need for security check
      }

      return hcol;
   }

   //=============================================================================
   //  HTML fields
   //=============================================================================

   private String wrapText( String theText, int wrapWidth )
   {
      ASPManager mgr = getASPManager(); 

      if(theText != null && theText != "")
      {
         String result = "";
         String temp = "";
         String original = theText;
         int pos = 0;
         while(pos < original.length())
         {
//            pos = original.lastIndexOf(" ",wrapWidth);
            pos = original.indexOf(" ",wrapWidth);

//            if( pos == -1 ) pos = wrapWidth;
            if(pos == -1) pos = original.length();
            if(!(pos+1 > original.length()))
            {
               temp = noNull(original.substring(0,pos));
               if(temp != "")
                  temp = mgr.HTMLEncode(temp,true) + "<br>"; 
               original = original.substring(pos+1);
            }
            else
            {
               if(original != null)
                  temp = mgr.HTMLEncode(noNull(original),true); 
               original = "";
            }

            result = result + temp;
         }

         if(original != null)
            result = result + noNull(original);

         return noNull(result);
      }
      else
         return "";
   }

   private String noNull(String str)
   {
      if(str == null)
         return "";
      else
         return str;
   }

   private void appendTextField( AbstractDataRow row,
                                 ASPField column,
                                 HyperlinkColumn hypercolumn,
                                 String value,
                                 int nr ) throws Exception
   {
      AutoString html = out_str;
      if( Str.isEmpty(value) )
         value = "&nbsp;";
      else if ( !column.isCheckBox() )
      {
         int size = user_column_size[nr];
         // Added by Terry 20131108
         // Add table title
         String full_value = getASPManager().HTMLEncode(value, true);
         // Added end
         if ( !wrap )
         {
            if( size>0 && value.length()>size )
               value = (size<3 ? "" : value.substring(0,size-3)) + "...";

            value = getASPManager().HTMLEncode(value,true);
         }
         else
         {
            // WRAP - wrap line at an appropriate place (max: size, but try to keep words whole)
            if( size>0 && value.length()>size )
            {
               value = wrapText(value,size);
            }
         }
         
         
         // Added by Terry 20130326
         // Font color
         if (column.hasFontColor())
            value = column.getFontColorTag(value);
         if (column.hasFontProperty())
            value = column.getFontPropertyTag(value);
         // Added end

         // Added by Terry 20131108
         // Add table title
         value = "<span title=\"" + full_value + "\">" + value + "</span>";
         // Added end
      }
      if (DEBUG) debug("  [appendTextField]value="+value);
      if(rowConditionStatus)
         html.append("<font class=\""+rowConditionStyle+"\">");
      String url = hypercolumn.colurl;
      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Original:
      // if( url != null && !"&nbsp;".equals(value) && hypercolumn.pres_obj_ok )
      if( url != null && !"&nbsp;".equals(value) && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null ) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,value);
      else
         html.append(value);
      if(rowConditionStatus)
         html.append("</font>");
   }


   private void appendAnchorTag( AutoString html,
                                 AbstractDataRow row,
                                 ASPField column,
                                 HyperlinkColumn hypercolumn,
                                 String value ) throws FndException
   {
       beginAnchorTag(html,row,column,hypercolumn);
       if(value==null)
       {
          String images_location = getASPManager().getASPConfig().getImagesLocation();

          html.append("<img src=\"",images_location,hyperlink_img,"\" width=");
          html.appendInt(hyperlink_img_width);
          html.append(" height=");
          html.appendInt(hyperlink_img_height);
          html.append(" border=0 alt=\"");
          html.append(getASPManager().translateJavaText("FNDTABHTMLLINK: HTML Link"));
          html.append("\" title=\"");
          html.append(getASPManager().translateJavaText("FNDTABHTMLLINK: HTML Link"));
          html.append("\">");
       }
       else
          html.append(value);
       html.append("</a>");
   }


   private void beginAnchorTag( AutoString html,
                                AbstractDataRow row,
                                ASPField column,
                                HyperlinkColumn hypercolumn ) throws FndException
   {
      html.append("<a href=\"");
      boolean newwin = column.isHyperlinkInNewBrowser();
      boolean javascript = column.isHyperlinkAsJavascript();
      if(javascript)
      {
         // Modified by Terry 20121218
         // Can use ASPField to set Hyperlink URL.
         // Origianl:
         // if(hypercolumn.colurl.indexOf("javascript:")<0)
         if(!Str.isEmpty(hypercolumn.colurl) && hypercolumn.colurl.indexOf("javascript:")<0) // Modified end
            html.append("javascript:");
         appendHyperlinkURL(html,row,hypercolumn);
      }
      else if( "GET".equals(column.getHyperlinkMethod()) )
      {
         if (newwin) html.append("javascript:showNewBrowser('");
         appendHyperlinkURL(html,row,hypercolumn);
         if (newwin) html.append("')");
      }
      else // POST
      {
         html.append("javascript:submitForm('");
         appendHyperlinkURL(html,row,hypercolumn);
         html.append("')");
      }
      html.append("\">");
   }


   private void appendHyperlinkURL( AutoString html,
                                    AbstractDataRow row,
                                    HyperlinkColumn hypercolumn ) throws FndException
   {
      if(DEBUG)
      {
         debug("ASPTable$HTMLGenerator.appendHyperlinkURL()");
      }
      
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      String url = "";
      if (hypercolumn.urlfield != null)
      {
         url = row.getValue(hypercolumn.urlfield);
      }
      
      if (Str.isEmpty(url))
      {
         if (!Str.isEmpty(hypercolumn.colurl))
         {
            url = hypercolumn.colurl;
//       html.append(getASPManager().prepareURL(url));
         }
      }
      html.append(url);
      // Added end

//      int param_count = hypercolumn.parposition.length;
      int param_count = (hypercolumn.parfield==null)? -1 : hypercolumn.parfield.length;
      for( int p=0; p<param_count; p++ )
      {
         if( p==0 && url.indexOf('?')<0 )
            html.append('?');
         else
            html.append('&');

         if(DEBUG) 
            debug("  hypercolumn.parfield["+p+"]="+hypercolumn.parfield[p]);
         ASPField param = hypercolumn.parfield[p];
         String param_value = "";
         param_value = param.convertToClientString(row.getValue(param));

         // Modified by Terry 20131028
         // Can accept fields aliases
         // Original:
         // html.append(hypercolumn.parfield[p].getName());
         html.append(hypercolumn.parfieldalias[p]);
         // Modified end
         html.append('=');
         html.append(getASPManager().prepareURL(param_value));
      }
   }

   private void appendInputField( AbstractDataRow row,
                                  ASPField column,
                                  HyperlinkColumn hypercolumn,
                                  String value,
                                  int row_nr,
                                  String row_status,
                                  int col_nr) throws FndException
   {
      AutoString html = out_str;
      String name = column.getName();
      if (column.isAccurateFld())
      {
         html.append("<input type=hidden name=",name," value=");
         
         html.append(column.formatNumber(column.parseNumber(row.getValue(column)))+" >");
         name = column.getAccurateDisplayFldName();
      }
      
      html.append("<input type=text name=",name," size=");
      
      // Added by Terry 20140919
      // Control column size in overview edit mode
      int column_size = user_column_size[col_nr];
      if (editable)
      {
         if (user_column_size[col_nr] > column.getSize())
            column_size = column.getSize();
      }
      // Added end
      // Modified by Terry 20140919
      // Original:
      // html.appendInt(user_column_size[col_nr]);
      html.appendInt(column_size);
      // Modified
      
      int max_len = column.getMaxLength();
      if(max_len>0)
      {
         html.append(" maxlength=");
         html.appendInt(max_len);
      }
      String alignment = column.getAlignment();
      if(ASPField.ALIGN_RIGHT.equals(alignment))
         html.append(" style=\"text-align: right\"");
      else if(ASPField.ALIGN_CENTER.equals(alignment))
         html.append(" style=\"text-align: center\"");
      column.appendReadOnlyTag(html,false,row_status);

      // bug id 42211 start.
      if(!getASPManager().isNetscape4x())
      //bug id 42211 end.
      {
         out_str.append(" CLASS=\"");
         if(column.isReadOnly())
            out_str.append("readOnlyTextField");
         else
            out_str.append("editableTextField");

         out_str.append("\"");
      }

       // bug 28939, start.
      if(column.isReadOnly())
      {
          html.append(" tabindex=-1 ");
      }
      //bug 28939, end.


      html.append(" value=\"");
      if( !Str.isEmpty(value) )
         //html.append("&nbsp;");
      //else
         html.append(getASPManager().HTMLEncode(value));
      html.append("\" ");
      column.appendValidationTag(html,row_nr,false);
      if( column.hasLOV() && column.isLOVActive(row_status) && !column.isReadOnly(row_status) )
         column.appendLOVTag(html,row_nr,false);
      if( column.isDateTime() && !column.isReadOnly(row_status))
         column.appendCalendarTag(html,row_nr);

      html.append(">");
      
      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if( column.getHyperlinkURL()!=null && !Str.isEmpty(value) && hypercolumn.pres_obj_ok )
      if( column.getHyperlinkURL()!=null && !Str.isEmpty(value) && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,null);
      if( row_nr==1 ) editable_columns.append(column.getName(),",");
   }

   private void appendImageField(int row_num,
                                 AbstractDataRow row,
                                 ASPField column, 
                                 HyperlinkColumn hypercolumn,
                                 String imgTag) throws FndException
   {
      AutoString html = out_str;
      html.append("<div align=\"center\"");
      if(column.hasPopupMenu())
         html.append(" onclick=\"javascript:initCustomPopup("+row_num+")\"");
      html.append(" >");
      String url = hypercolumn.colurl;
      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if( url!=null && !"&nbsp;".equals(imgTag) && hypercolumn.pres_obj_ok)
      if( url!=null && !"&nbsp;".equals(imgTag) && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,imgTag);
      else
         html.append(imgTag);
      html.append("</div>");
   }
   
 private void appendAreaBoxField( AbstractDataRow row,
                                    ASPField column,
                                    HyperlinkColumn hypercolumn,
                                    String value,
                                    int row_nr,
                                    String row_status,
                                    int col_nr) throws FndException
   {
      AutoString html = out_str;
      html.append("<textarea name=",column.getName()," cols=");
      //html.appendInt(user_column_size[col_nr]);
      html.appendInt(column.getSize());
      html.append(" " + getTBLTextAreaHeight());

      String alignment = column.getAlignment();
      if(ASPField.ALIGN_RIGHT.equals(alignment))
         html.append(" style=\"text-align: right\"");
      else if(ASPField.ALIGN_CENTER.equals(alignment))
         html.append(" style=\"text-align: center\"");
      column.appendReadOnlyTag(html,false,row_status);
      html.append(" wrap='virtual' ");
      if(getASPManager().isExplorer())
      {
         out_str.append(" CLASS=\"");
         if(column.isReadOnly())
            out_str.append("readOnlyTextArea");
         else
            out_str.append("editableTextArea");
         out_str.append("\"");
      }

      html.append(" ");
      column.appendValidationTag(html,row_nr,false);
      html.append(">");
      if( !Str.isEmpty(value) )
         html.append(getASPManager().HTMLEncode(value));
      html.append("</textarea>");

      if( column.hasLOV() && column.isLOVActive(row_status) )
      {
         html.append("<x");
         column.appendLOVTag(html,row_nr,false);
         html.append(">");
      }
      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if( column.getHyperlinkURL()!=null && !Str.isEmpty(value) && hypercolumn.pres_obj_ok)
      if( column.getHyperlinkURL()!=null && !Str.isEmpty(value) && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,null);
      if( row_nr==1 ) editable_columns.append(column.getName(),",");
   }

   private String getTBLTextAreaHeight()
   {
      if (getASPManager().isMozilla() && textarea_height <3)
         return " style={height:32px} ";
      return " rows=" + textarea_height + " ";
   }


   private void appendSelectBoxField( AbstractDataRow row,
                                      ASPField column,
                                      HyperlinkColumn hypercolumn,
                                      String value,
                                      int row_nr,
                                      String row_status ) throws FndException
   {
      AutoString html = out_str;
      html.append("<select name=",column.getName()," size=1 ");
      column.appendValidationTag(html,row_nr,false);

      html.append(" CLASS=\"");
      html.append("selectbox");
      html.append("\"");

      html.append(">");

      getASPPage().getASPHTMLFormatter().populateListBox( html,
                                                          column.getIidClientValues(),
                                                          value,
                                                          column.isMandatory(),
                                                          false );
      html.append("</select>");
      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if(column.getHyperlinkURL()!=null && hypercolumn.pres_obj_ok)
      if(column.getHyperlinkURL()!=null && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,null);
      if( row_nr==1 ) editable_columns.append(column.getName(),",");
   }


   private void appendCheckBoxField( AbstractDataRow row,
                                     ASPField column,
                                     HyperlinkColumn hypercolumn,
                                     String value,
                                     int row_nr ) throws FndException
   {
      AutoString html = out_str;
      String checked_value = column.getCheckedValue();
      html.append("<input type=checkbox name=_",column.getName());

      if( checked_value.equals(value) )
         html.append(" checked");

      html.append(" value=");
      html.appendInt(row_nr);
      html.append(" ");
      column.appendValidationTag(html,row_nr,false); //setCheckBox_() is added in appendValidationTag() function
      /*html.append(" OnClick=\"setCheckBox_('",column.getName(),"',checked,");
      html.appendInt(row_nr);
      html.append(",'",checked_value,"')\"");*/
      html.append("class='checkbox'>");
      
      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if(column.getHyperlinkURL()!=null && hypercolumn.pres_obj_ok)
      if(column.getHyperlinkURL()!=null && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,null);
   }

   private void appendCheckBoxImage( AbstractDataRow row,
                                     ASPField column,
                                     HyperlinkColumn hypercolumn,
                                     String value ) throws FndException
   {
      AutoString html = out_str;

      if( column.getCheckedValue().equals(value) )
      {
         // Added by Terry 20140516
         // Added checked checkbox pic
         html.append("<img src=\"" + getASPManager().getASPConfig().getImagesLocation() + "checkbox_checked.gif\"/>");
         // Added end
         html.append("<input type='checkbox' disabled class='readOnlyCheckbox' checked>");
      }
      else
      {
         // Added by Terry 20140516
         // Added unchecked checkbox pic
         html.append("<img src=\"" + getASPManager().getASPConfig().getImagesLocation() + "checkbox_unchecked.gif\"/>");
         // Added end
         html.append("<input type='checkbox' disabled class='readOnlyCheckbox'>");
      }

      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if(column.getHyperlinkURL()!=null && hypercolumn.pres_obj_ok)
      if(column.getHyperlinkURL()!=null && hypercolumn.pres_obj_ok || hypercolumn.urlfield != null) // Modified end
         appendAnchorTag(html,row,column,hypercolumn,null);
   }


   private void appendHiddenField( String name,
                                   String value,
                                   int row_nr,
                                   boolean dummy_on_change ) throws FndException
   {
      out_str.append("\t<input type=hidden name=",name," value=\"");
      if( !Str.isEmpty(value) )
         out_str.append(getASPManager().HTMLEncode(value));
      out_str.append('"');
      if( dummy_on_change ) out_str.append(" OnChange=\"\"");
      out_str.append(">\n");

      if( row_nr==1 ) editable_columns.append(name,",");
   }

   /*
   private void appendTDTag( ASPField column, boolean first_row, boolean last_row, int col_nr, boolean first_col, boolean last_col) throws FndException
   {
      appendTDTag(column,first_row,last_row,col_nr,first_col,last_col,"");
   }
    */

   private void appendTDTag( ASPField column, boolean first_row, boolean last_row, int col_nr, boolean first_col, boolean last_col, int row_nr ) throws FndException
   {

      String format = "";

      if( first_row && first_col ) format = upperLeftColumn;
      if( first_row && !first_col && !last_col ) format = upperMidColumn;
      if( first_row && last_col ) format = upperRightColumn;
      if( first_col && !first_row && !last_row ) format = leftColumn;
      if( last_col && !first_row && !last_row ) format = rightColumn;
      if( last_row && first_col ) format = bottomLeftColumn;
      if( last_row && !first_col && !last_col ) format = bottomMidColumn;
      if( last_row && last_col ) format = bottomRightColumn;

      if( format.equals("") ) format = columnFormatting;

      // Extra space between
//      out_str.append("\t\t<TD bgcolor=white>  </TD>");
      String expClick = column.isHyperLinked()?"" :"onclick=\"__onSystemCol=false;\" ";

      if( ! format.equals("") )
      {
         // Modified by Terry 20120821
         // Set Background color to column
    	 // Original: out_str.append("\t\t<TD " + expClick + format + ">");
         out_str.append("\t\t<TD " + expClick + format + column.getBgColorTag() + ">");
         // Modified end
      }
      else
      {
         // Modified by Terry 20120821
         // Set Background color to column
         // Original: out_str.append("\t\t<TD " + expClick);
         out_str.append("\t\t<TD " + expClick + column.getBgColorTag());  // BGCOLOR moved to TABLE tag
         // Modified end
         
         column.appendTooltipTag(out_str,row_nr,editable);

         if(getASPPage().getVersion() < 3)
            if (!this.query_row && !this.title_row && first_row && !editable)
               appendColumnWidth(col_nr);

         // Modified by Terry 20120821
         // Set column WRAP property
         // Original: if(!nowrap_disabled)
         //              out_str.append(" NOWRAP");
         int column_wrap = column.getFieldWrap();
         if (column_wrap == column.WITHTABLE)
         {
            if(!nowrap_disabled)
               out_str.append(" NOWRAP ");
         }
         else if (column_wrap == column.SETNOWRAP)
         {
            out_str.append(" NOWRAP ");
         }
         // Modified end

         // wrapping is done manually, by inserting <br> tags
         
         // Modified by Terry 20140912
         // Original:
         // if(column.isCheckBox())
         //     out_str.append(" VALIGN=MIDDLE");
         // else
         //     out_str.append(" VALIGN=TOP");
         out_str.append(" VALIGN=MIDDLE");
         // Added end
         if ( column.isCheckBox() )
         {
               out_str.append(" ALIGN=center CLASS=\"", STYLESHEET_CLASS_MULTIROW_TD,"\">");
         }
         else
         {
            // The browser used is not Internet Explorer
            // ( => a CSS class reference is required on each <TD> tag)
            out_str.append(" CLASS=\"", STYLESHEET_CLASS_MULTIROW_TD,"\" ");
            if(!editable)
            {
               // Only ALIGN different from LEFT on TD tag, otherwise see TR tag
               String align = column.getAlignment();
               if ( !ASPField.ALIGN_LEFT.equals(align) )
                  out_str.append(" ALIGN=",align);
               }

               out_str.append('>');
            }
         }

   }

   public AutoString generateExpandableRow(ASPRowSet rowset, int rowno) throws FndException
   {
      AutoString content = new AutoString();      
      boolean hasContent = false;
      
      if(!(rowset.countRows()>rowno-1))
      {
         content.append("error^",getASPManager().translate("FNDTABLENOEXPANROW: Expandable row &1 not found.",rowno+1+""));
         return content;
      }
      
      content.clear();
      content.append("<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\">\n");
      
      for(int c=0; c<column_count; c++)
      {
         if(!columns[c].isExpandable()) continue;
         // ============= repeat for every expandable field ===============
         String tdContent = "";
         if(columns[c].isCheckBox())
            tdContent = "<input type=\"checkbox\" disabled "+((rowset.getClientValueAt(rowno-1, columns[c].getName()).equalsIgnoreCase("TRUE"))?"checked":"")+">";
         else if(columns[c].isImageField())
            tdContent = getASPPage().getImageFieldTag(columns[c], rowset, rowno-1);
         else
            tdContent = rowset.getClientValueAt(rowno-1, columns[c].getName());
         content.append("  <tr>\n");
         content.append("     <td height=\"20\" class=\"multirowTableTHText\" nowrap valign=\"top\">",columns[c].getLabel(),":&nbsp</td>\n");
         content.append("     <td height=\"20\" class=\"normalTextValue\"nowrap valign=\"top\">",tdContent,"</td>\n");
         content.append("     <td height=\"20\" class=\"normalTextValue\" width=\"100%\" valign=\"top\">&nbsp;</td>\n");
         content.append("  </tr>\n");
         // ===============================================================
         if(!hasContent) hasContent = true;
      }
      content.append("</table>\n");
      
      if(!hasContent)
      {
         content.clear();         
         content.append("error^",getASPManager().translate("FNDTABLENOEXPANCONTENT: No expandable content."));
      }
      
      return content;
   }
   
   //=============================================================================
   //  Prepare
   //=============================================================================

   private boolean isColumnHidden_( int c )
   {
      return user_columns[c].isHidden() || user_column_size[c]<0;
   }

   private void prepareColumnTypes() throws FndException
   {
      if( column_type==null ) column_type = new String[column_count];
      ASPField[] params;

      for( int i=0; i<column_count; i++ )
         user_columns[i].setUsedAsParameter(false);

      for( int i=0; i<column_count; i++ )
      {
         if( isUserColumnHidden(i) ) continue;
         ASPField col = user_columns[i];
         if(col.isImageField() && !col.hasTemplate()) continue;
         
         if( col.hasValidation() )
         {
            params = col.getValidationInParameters();
            for( int j=0; j<params.length; j++ )
               params[j].setUsedAsParameter(true);
            params = col.getValidationOutParameters();
            for( int j=0; j<params.length; j++ )
               params[j].setUsedAsParameter(true);
         }
         if( col.hasLOV() && col.isLOVActive() )
         {
            params = col.getLOVInParameters();
            for( int j=0; j<params.length; j++ )
               params[j].setUsedAsParameter(true);
            col.setUsedAsParameter(true);
         }
      }

      for( int i=0; i<column_count; i++ )
      {
         String type = null;
         ASPField col = user_columns[i];

         if( isUserColumnHidden(i) )
            type = col.isUsedAsParameter() ? "H" : null;
         else
            type = this.editable ? "I" : "T";
         
         type = (col.isImageField() && !col.hasTemplate()) ? "P" : type;

         column_type[i] = type;
      }

      if(DEBUG)
      {
         debug(this+"prepareColumnTypes():");
         debug("   Nr\tIndex\tSize\tType\tName");
         for( int i=0; i<column_count; i++ )
         {
            debug("   "+i+"\t"+findColumnIndex(user_columns[i])+
                          "\t"+user_column_size[i]+
                          "\t"+column_type[i]+
                          "\t"+user_columns[i].getName());
         }
      }
   }

   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================
   
   String mobilePopulate( AbstractDataRowCollection data, int first_row, int populate_type )
   {
      TraceEvent populate_event = populate_event_type.begin();

      try
      {  
         if( user_columns==null ) user_columns  = new ASPField[column_count];
         if( user_column_size==null ) user_column_size = new int[column_count];
         for( int i=0; i<column_count; i++ )
         {
            user_columns[i] = columns[i];
            user_column_size[i] = columns[i].getSize();            
         }
                  
         key_pos = getKeyColumnPosition();
         if (key_pos<0) return "";

         out_str.clear();
         out_str.append("\n");
         
         //Table Definition Begin
         out_str.append("<table class=\"",STYLESHEET_CLASS_MULTIROW,"\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\">\n");
         int rows = data.countRows();

         //Add Table headering columns
         out_str.append("   <tr class=\"",STYLESHEET_CLASS_MULTIROW_TH_TEXT,"\">\n");            
         out_str.append("      <th nowrap class=\"",STYLESHEET_CLASS_MULTIROW_SYS_TH,"\">&nbsp;</th>\n");
         for(int th=0; th<column_count; th++)
         {
            if(isUserColumnHidden(th) || user_columns[th].isDefaultNotVisible()) continue;
            out_str.append("      <th nowrap class=\"",STYLESHEET_CLASS_MULTIROW_TH,"\">",user_columns[th].getTitle().replaceAll(" ","&nbsp;"),"</th>\n");
         }
         out_str.append("   </tr>\n");            
         
         //Add rows of data with alternating colors
         boolean alt_bgcolor = false;
         int row_nr = 0;
         
         for(int i=0; i<rows; i++)
         {
            AbstractDataRow row = data.getDataRow(i);
            row_nr = i+1;
            out_str.append("   <tr");
            alt_bgcolor = !alt_bgcolor;
            if(alt_bgcolor)
               out_str.append(" class=\"tableRowColor1\"");
            else
               out_str.append(" class=\"tableRowColor2\"");
            out_str.append(">\n");

            out_str.append("      <td nowrap class=\"",STYLESHEET_CLASS_MULTIROW_SYS_TD,"\">");

            String cmdname = ASPCommandBar.VIEWDETAILS;
            String prefix = getASPPage().addProviderPrefix();
            String cmd_prefix = prefix;
            
            if(!Str.isEmpty(prefix))
               prefix += ".";

            if(getBlock().getASPCommandBar().IsEnabled(ASPCommandBar.VIEWDETAILS))
            {
               out_str.append("&nbsp;<a class=\"",STYLESHEET_CLASS_MULTIROW_TH_LINK,"\" href=\"javascript:",cmd_prefix + "initPop" + table_nr + "(" + (i) + ");");
               out_str.append(cmd_prefix + "setTableCommand"+table_nr+"('');commandSet('" + prefix + getBlock().getName() + "." + cmdname + "','')\">");
            }
            
            out_str.append(""+(i+1),"</a>&nbsp;");
            out_str.append("<input type=\"hidden\" name=",getASPPage().addProviderPrefix(),SELECTBOX); 
            out_str.appendInt(table_nr);
            out_str.append(" value=\"","\">");            
            out_str.append("</td>\n");

            //if the first row is null or not.. (empty or not)
            AbstractDataRow firstrow = rows==0 ? null : data.getDataRow(0);
            //array of column hyperlinks
            if( column_hyperlink==null ) column_hyperlink = new HyperlinkColumn[column_count];
            for( int c=0; c<column_count; c++ )
            {
               //create hyperlink columns for all columns
               ASPField column = user_columns[c];
               column_hyperlink[c] = firstrow==null ? null : createHyperlinkColumn(column,firstrow);
            }

            for(int c=0; c<column_count; c++)
            {
               ASPField column = user_columns[c];
               
               if(isUserColumnHidden(c) || user_columns[c].isDefaultNotVisible()) continue;
               String value = row.getValue(user_columns[c]);
               
               DataFormatter formatter = column.getDataFormatter();
               if (column.isAccurateFld())
                  formatter = column.getAccurateClientFormatter();

               value = row.convertToClientString(column,formatter);
              
               if(value!=null)
               {
                  if(value.length()>3 && value.length()>user_column_size[c])
                     value = (user_column_size[c]-3<0)? "...": value.substring(0,user_column_size[c]-3);
               }
               out_str.append("      <td nowrap class=\"",STYLESHEET_CLASS_MULTIROW_TD,"\">");

               if ( column.isCheckBox() )
               {
                  out_str.append("<INPUT TYPE=\"checkbox\"");
                  out_str.append( column.getCheckedValue().equals(value)? " CHECKED":"" );
                  out_str.append(" DISABLED>");
               }
               else if(column_hyperlink[c] != null)
                  appendTextField(row, column, column_hyperlink[c], value, c);
               else
                  out_str.append(value.replaceAll(" ","&nbsp;"));
               out_str.append("</td>\n");
            }
            out_str.append("   </tr>\n");
         }
         out_str.append("</table>\n");
         mobileTableDefinitionEnd(row_nr, IfsNames.dbToAppName(getBlock().getName()));
         return out_str.toString();
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
      finally
      {
         populate_event.end();
      }
   }
   
   private void mobileTableDefinitionEnd( int cnt_rows, String name )
   {
      if( pop_commands )
      {

         String prefix = getASPPage().addProviderPrefix();
         ASPBlock blk = getBlock();
         blk.appendPerformField(out_str);

         out_str.append("\n<script language=javascript>\n");

         out_str.append("function ",prefix,"initPop");
         out_str.appendInt(table_nr);
         out_str.append("(row)\n{\n");
         out_str.append("\t","tblRow");
         out_str.appendInt(table_nr);
         out_str.append(" = row;\n}\n");
         
         out_str.append("function ",prefix,"setTableCommand");
         out_str.appendInt(table_nr);
         out_str.append("(cmd)\n{\n");
         //out_str.append("\tdeselectAllRows('",prefix,"__SELECTED"+getTableNo()+"');\n");         
         out_str.append("\tfor(__x=0;__x<");
         out_str.append("f.",prefix,"__SELECTED");
         out_str.appendInt(table_nr);
         out_str.append(".length");
         out_str.append(";__x++)\n\t{\n\t\t");
         out_str.append("f.",prefix,"__SELECTED");
         out_str.appendInt(table_nr);
         out_str.append("[__x].value=\"\";");
         out_str.append("\n\t}\n\n\t");
         out_str.append("f.",prefix,"__", getBlock().getName(), "_" + ASPCommandBar.PERFORM + ".value = cmd;\n\t");
         out_str.append("f.", prefix, "__SELECTED");
         out_str.appendInt(table_nr);
         if (cnt_rows > 1)
         {
            out_str.append("[tblRow");
            out_str.appendInt(table_nr);
            out_str.append("]");
         }
         out_str.append(".value = tblRow");
         out_str.appendInt(table_nr);
         out_str.append(";\n");
         out_str.append("}\n");
         
         out_str.append("</script>\n");
      }

      if( editable )
      {
         out_str.append("<script language=javascript>\n");
         out_str.append("function check",name,"TableFields()\n{\n\t");
         if(cnt_rows>0)
            for(int i=1;i<=cnt_rows;i++)
            {
               if(i==1)
                  out_str.append("return ");
               out_str.append("check",name,"Fields(");
               out_str.appendInt(i);
               out_str.append(")");
               if(i==cnt_rows)
                  out_str.append(";\n");
               else
                  out_str.append(" &&\n\t");
            }
         else
            out_str.append("return true;\n");
         out_str.append("}\n</script>\n");
      }
   }
}



}
