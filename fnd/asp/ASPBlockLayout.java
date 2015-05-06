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
 * File        : ASPBlockLayout.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Johan S  2000-Jan-24 - Created
 *    Stefan M 2000-Feb-03 - Added single-record readonly mode, and multiple row
 *                           input fields (TEXTAREA). Fixed getCurrentRowValue()
 *                           to work in FIND dialogs (ignore rowset).
 *    Stefan M 2000-Feb-14 - Added field span functions, setDialogColumns(),
 *                           setDialogOrder(), and customizable field placement logic.
 *    Stefan M 2000-Mar-10 - Added defineGroup() function. Restructured field generation.
 *    Johan  S 2000-Mar-29 - Recursivly adding hiddens fields of masterblocks when a detail
 *                           block is in edit or new mode.
 *                           Plus a few billions of other undocumentet changes, sorry.
 *    Johan  S 2000-Apr-06 - Find dialogs now generates toUpperCase. (buggfix).
 *    Stefan M 2000-Apr-11 - Added setColumnWidth() and associated internal functions.
 *    Stefan M 2000-Apr-22 - Added setSimple() and logic to handle it;
 *    Stefan M 2000-Apr-28 - All cells now have "valign=top". Background color fix for Netscape;
 *                           using a container table.
 *    Stefan M 2000-May-04 - Select fields now get a stylesheet reference.
 *    Stefan M 2000-May-08 - NOWRAP removed from view details mode.
 *    Stefan M 2000-May-11 - Collapsible blocks (including the main block) now remembers minimized
 *                           state. Added getID().
 *    Stefan M 2000-May-20 - VALIGN=TOP on label added to view details mode.
 *    Stefan M 2000-Jul-04 - Linefeeds are replaced with HTML <BR> for text columns (SINGLE_LAYOUT).
 *    Stefan M 2000-Jul-05 - Immutable attributes are now cloned.
 *    Stefan M 2000-Jul-06 - Support for initially minimized blocks added; setInitiallyMinimized().
 *    Stefan M 2000-Sep-18 - New parameter to defineGroup() - number of columns per row.
 *    Stefan M 2000-Nov-01 - Now uses ASPManager.getTranslatedImageLocation() in default dialog.
 *    Jacek P  2001-Mar-29 - Changed .asp to .page
 *    Piotr Z  2001-Apr-25 - Scaling of input and textarea fields size for Netscape.
 *    Artur K  2001-May-23 - Log id 713, 714. Changed findHiddens() function.
 *    Mangala  2001-May-25 - Log id 730. We have to implicitly set default value for hidden fields
 *                           in Netscape.
 *    Suneth M 2001-Sep-12 - Changed localization tags according to the standards.
 *    Mangala  2001-Sep-19 - Log id 803. Add new method unsetSimple().
 *    Piotr Z  2001-Oct-02 - Changed getURL() method. Returned url has no "?" sign at the end if there are no parameters.
 *    Ramila H 2001-Jan-07 - Added support for localized address fields.
 *    Ramila H 2002-Mar-04 - added support for group box, readOnly and insertable.
 *    Ramila H 2002-Mar-05 - cloned localized_address_vec.
 *    Suneth M 2002-May-14 - Log id 772. Changed beginCell() & endCell() to mark mandatory fields in CUSTOM_LAYOUT mode.
 *    Suneth M 2002-May-22 - Log Id 801. Changed generateDialog().
 *    Rifki R  2002-Jun-13 - Log Id 849. Moved count result display to the top in generateDialog().
 *    Chandana 2002-Jul-30 - Added private method getTooltip(ASPField field,ASPRowSet rowset,String mode).
 *    Ramila   2002-Jul-30 - Log Id 875. Added support for calendat tag generation.
 *    Daniel S 2002-Aug-29 - Added a getName() function.
 *    Chandana 2002-Sep-02 - Overloaded two new defineGroup() methods to show group title when the header is hidden.
 *             2002-Sep-02 - Corrected alignment problems when the group headers are hidden.
 *    Mangala  2002-Sep-04 - Minor improvement for tooltips
 *    Mangala  2002-Oct-09 - Log Id 978. Minor HTML problem in Intro page.
 *    ChandanaD2002-Oct-28 - Made compatible with NE6 and above.
 *    Sampath  2002-Nov-01 - Removed 'help' link from page body and changed the apperence of 'new' and 'find' messages
 *    Mangala  2002-Dec-03 - Fixed Log id 364.
 *    Rifki R  2002-Dec-05 - Log id 990. Changed drawColumns(). Set focus to first editable field as default behaviour.
 *    ChandanaD2002-Dec-18 - Log Id 567, Added setDefaultCommand() and getDefaultCommand() methods
 *    Sampath  2002-Dec-27 - Add Date-Time mask for editable fields ; add the method addDateTimeMask();
 *    Suneth M 2002-Dec-30 - Log id 1002, Added addIidLOV() method. Changed beginCell() & endCell().
 *    ChandanaD2003-Jan-24 - Added Doc Comments to setDefaultCommand() method.
 *    ChandanaD2003-Feb-18 - Added new attribute 'link_functions' and changed show() and drawColumns() methods.
 *    ChandanaD2003-Feb-21 - Changed drawColumns() method.
 *    Rifki R  2003-Feb-28 - Log id 1010. Automatic security check for hyperlinks. implemented code.
 *    ChandanaD2003-Mar-17 - Added support for uploading files.
 *    ChandanaD2003-Mar-19 - Corrected a bug in custom link commands.
 *    ChandanaD2003-Mar-24 - Made changes to custom link commands to support disabling.
 *    ChandanaD2003-Mar-24 - URL encoded query string parameter values in file upload url.
 *    ChandanaD2003-Mar-24 - Moved file upload link generation code to addFileUploadLink(ASPBlock blk) method in ASPPage.
 *    ChandanaD2003-Apr-01 - Set the default command for the SINGLE_LAYOUT to ASPCommandBar.FIND
 *    ChandanaD2003-May-22 - Modified coding for new L&F(show(),introPage(),generateDialog())
 *    Sampath  2003-Jul-28 - Moved redundent code out of if-else block in generateDialog()
 *    Ramila H 2003-Jul-30 - Log id 853, added HTML formatting for Password type ASPfield.
 *    ChandanaD2003-Aug-12 - Added new method showBottomLine.
 *    Sampath  2003-Aug-21 - new method toSingleLine to allow newline characters in tool tips.
 *    ChandanaD2003-Sep-02 - Made dotted_line as a mutable attribute.
 *    Ramila H 2003-Sep-18 - Log id 1044, Implemented code to show query hints images.
 *    Chandana 2003-Oct-18 - Kept the collapsible groups opened (not possible to collapse) for NE6 & 7.
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    ChandanaD2003-12-09  - Bug 40902.Fixed a bug in customLinkCommands when using the DB_NAME.
 *    ChandanaD2004-Apr-23 - Bug 43961 fixed. dialog_columns parameter cloned.
 *    ChandanaD2004-Apr-23 - Bug 43443 fixed.
 *    ChandanaD2004-May-12 - Removed all the dependencies on the SCHEME section of th webclientconfig.xml.
 *    ChandanaD2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Mangala  2004-Jun-17 - Merged with Bug #44656
 *    Chandana 2004-Jun-28 - Added support for viewing the history.
 *    Suneth M 2004-Jul-13 - Changed writeColumn() to format the date & time values according to the
 *                           format mask in find mode.
 *    Mangala  2004-Jul-27 - Call translations in defineGroup
 *    Suneth M 2004-Aug-13 - Merged Bug 45335, Changed beginCell() to add extra new line character for text areas.
 *    Rifki R  2004-Oct-26 - Merged Bug id 47417, modified endCell() avoid showing mandatory "*" for checkboxes.
 *    Chandana 2004-Nov-05 - Page level profile support. (at present limited to pages without field groups)
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/10/01 buhilk Bug 93333, Changed drawColumns() methods to improve custom link command functionality
 * 2010/09/08 amiklk Bug 92919, Changed textColumn() to support POST hyperlinks in Detail layout.
 * 2010/02/08 sumelk Bug 88033, Changed generateDialog() to check the row count before enable the Notes icon.
 * 2010/01/18 amiklk Bug 87749, Changed generateDialog() to write CBLOCK server id to the browser.
 * 2009/07/31 sumelk Bug 83601, Changed drawColumns() to obtain the client values for history parameters.
 * 2009/02/18 buhilk Bug 80667, Blocks with no child blocks did not show any tasks in AEE. Supp Bug for 80266.
 * 2009/02/03 buhilk Bug 80266, Modified show() to add rwc interface values for tasks needed by AEE.
 * 2008/10/15 dusdlk Bug 77769, updated the setDefaultLayoutMode(int mode).
 * 2008/09/24 dusdlk Bug 77095, Added isLayoutVisible(), isLayoutRemovable(), updateProfileBuffer( ASPBuffer info , boolean properties) and getIsPropertiesPage() and updated isVisible() to check for isLayoutVisible().
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client, Modified generateDialog().
 * 2008/08/01 sadhlk Bug 76004, Modified listToFields() to avoid list duplicating fields.
 * 2008/07/09 buhilk Bug 74852, handled Null pointer exception for empty block views in FndWebFeatures
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities. 
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support rich menus/table cells.
 * 2008/04/09 sadhlk Bug 67895, Modified generateDialog() to enable hybrid search functionality.
 * 2008/04/04 buhilk Bug 72854, Added support for RWC link parameters into drawColumns().
 * 2008/03/26 buhilk Bug 72676, Added appending of Ctrl+k function code from within writeCloumn() which was moved from ASPField.appendLOVTag().
 * 2008/02/29 sumelk Bug 71694, Changed beginCell() to append the possibility to set the MAXLENGTH attribute 
 *                   for TEXTAREA fields.
 * 2008/01/25 sadhlk Bug id 70544, Modified generateDialog() to correctly hide defined Groups.
 * 2008/01/21 sumelk Bug 69852, Changed writeColumn() to send the block name as a parameter for formatDate_().
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/09/18 sadhlk Modified generateDialog(), Disabled Application Search functionality temporarily due to Oracle error.
 * 2007/07/24 rahelk Merged Bug id 66487, opened drop-down outside field.
 * 2007/07/18 sumelk Merged the corrections for Bug 66481, Modified generateDialog().
 * 2007/06/13 sadhlk Merged Bug id 64669, Modified generateDialog().
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/03/28 buhilk Imporoved generateDialog() to toggle minimise & maximise of group by clicking on the header bar
 * 2007/03/20 buhilk Modified generateDialog() to minimise the gap between two blocks to 6px
 * 2007/03/01 buhilk Bug id 63870, Modified textColumn(),readColumn() and writeColumn() to improve "whats this" effects
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * 2007/02/09 sadhlk Bug 63260, Moved the code part which provide Find Mode Tips, from show() to generateDialog() 
 *                              to correct script error problem in CUSTOM_LAYOUT modes
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2007/01/10 buhilk Bug 62849, Modified beginCell() method to print validation method for checkbox if available.
 * 2006/11/22 rahelk Bug 61532 Added functionality for displaying/editing accurate fields
 * 2006/11/02 gegulk Bug 61534, Modified the methods writeColumn() & show() to provide Find Mode Tips
 * 2006/09/13 gegulk Bug 60473, Modified the method generateDialog() for proper formatting in RTL Mode
 * 2006/08/29 gegulk Bug 60172, Modified the method prepareProfileInfo() to check whether the
 *                              Size and the Height of the ASPField is Dirty
 * 2006/05/16 mapelk Improved "What's This?" functionality to show help as a tool tip
 *               2006/05/05           sumelk
 * Bug 57271, Changed the placement of Case sensitive search check box.
 *               2006/04/26           prralk
 * Bug 56440, a choice to right align number fields
 *               2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 *               2006/02/09           prralk
 * B133326 - Check for dirty span attribute before applying profile settings
 *               2006/01/31           prralk
 * B131326 - New window with & sign in querry string does not function correctly.
 *
 * Revision 1.11 2006/01/19           prralk
 * Added LOV and date picker for text areas.
 *
 * Revision 1.10 2006/01/17           mapelk
 * Improved alignments among different groups.
 *
 * Revision 1.9  2005/12/27 12:11:20  sumelk
 * Merged the corrections for Bug 54381, Changed getURL().
 * 
 * Revision 1.8  2005/12/23 14:36:40  sumelk
 * Merged the corrections for Bug 54379, Changed generateDialog().
 * 
 * Revision 1.7  2005/11/15 11:32:16  japase
 * ASPProfile.save() takes 'this' as argument.
 *
 * Revision 1.6  2005/11/04 12:28:06  japase
 * Fixed bug in the clone() method
 *
 * Revision 1.5  2005/10/19 10:38:08  mapelk
 * Merged pkg 16 patch 2
 *
 * Revision 1.4  2005/10/14 09:08:13  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.3  2005/09/27 08:41:24  sumelk
 * Merged the corrections for Bug 52960, Changed getURL() to encode the field values.
 *
 * Revision 1.2  2005/09/22 12:39:22  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.18.2.1  2005/09/02 03:35:16  mapelk
 * Fixed Call 126746: Readonly check boxes lost thier state
 *
 * Revision 1.18  2005/08/18 05:14:28  mapelk
 * Bug fixed: Call 124802: Validations fail to assign values to read only check box fields.
 *
 * Revision 1.17  2005/08/15 09:54:22  rahelk
 * profile - removed arrays and introduced Field class structure
 *
 * Revision 1.16  2005/08/08 09:44:03  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.15  2005/07/08 03:51:38  mapelk
 * added missing code during a conflict
 *
 * Revision 1.14  2005/07/04 09:15:52  rahelk
 * CSL: corrected bug with group field list order
 *
 * Revision 1.13  2005/07/04 07:45:07  mapelk
 * Simplified the profile algorithm and bug fix: 125003, 125505
 *
 * Revision 1.12  2005/07/01 15:25:12  rahelk
 * CSL: set size and dataSpan for field in detail mode
 *
 * Revision 1.11  2005/06/09 09:40:35  mapelk
 * Added functionality to "Show pages in default layout mode"
 *
 * Revision 1.10  2005/06/07 07:47:02  mapelk
 * Bugfix in removeing lable from CSL page
 *
 * Revision 1.9  2005/06/06 07:22:27  rahelk
 * Save all profile objects together from CSL page
 *
 * Revision 1.8  2005/05/18 12:29:56  japase
 * Temporary commented line 2958 due to build failure
 *
 * Revision 1.7  2005/05/18 10:50:51  rahelk
 * Added support for setting profile height for a field
 *
 * Revision 1.6  2005/05/17 10:44:12  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.5  2005/05/04 05:31:59  rahelk
 * Layout profile support for groups
 *
 * Revision 1.4  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.3.2.1  2005/03/05 11:28:59  mapelk
 * bug fix: call id 122439
 *
 * Revision 1.3  2005/02/11 10:09:02  riralk
 * More changes for new profile. Called the correct ASPProfile.save() method from removeProfile()
 *
 * Revision 1.2  2005/02/02 15:09:30  riralk
 * Adapted BlockLayoutProfile functionality to new profile changes.
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.5  2005/01/21 10:41:43  rahelk
 * Bug 48132 merged, Rewrote getTooltip method to call ASPField.appendTooltipTag
 *
 * Revision 1.4  2005/01/07 10:26:58  marese
 * Merged changes made on the PKG10 branch back to HEAD
 *
 * Revision 1.3  2005/01/07 05:40:39  sumelk
 * Changed generateDialog() to remove the group boxes in find mode when all fields in the group are not queryable.
 *
 * Revision 1.2.2.1  2004/12/29 12:16:40  riralk
 * Fxied Call id 120932
 *
 * Revision 1.2  2004/12/10 10:05:45  riralk
 * New Block Layout profile GUI changes
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.io.*;
import java.util.*;

import ifs.fnd.asp.ApplicationSearchManager.SearchDomain;

/**
 * This class is responsible for producing HTML layouts that can be automatically
 * generated. It will only handle singlerow modes, the multirow mode is handled by
 * ASPTable.
 */
public class ASPBlockLayout extends ASPPageSubElement
{

   transient BlockLayoutProfile profile;     transient BlockLayoutProfile pre_profile;
   //User specific profile values should NOT be cloned
   private transient boolean    user_profile_prepared;
   private transient ASPField[] user_columns;
   private ASPField[]           columns; // common to all instances
   private int                  column_count; // common to all instances

   // Inmutable attributes
   private transient AutoString html_page          = new AutoString();
   protected ASPBlock  blk;
   //private ASPCommandBar cmdbar;
   private String    dialog_order                  = "";
   private int       dialog_columns                = 2;
   private int       columns_done                  = 0;
   private int       spaceBetween                  = 10;
   private int       spaceAfter                    = 10;
   private Vector    groups                        = new Vector();
   private Vector    widths                        = new Vector();

   private boolean initially_minimized;

   private int       my_id;      // Used as a unique identifier for the collapsible block.


   // Temporary variables
   private int       cells_done;
   private int       active_column;
   private int       counted;
   private boolean   hascounted;
   // Added by Terry 20130918
   // Advanced Query
   private boolean   advanced_find;
   // Added end
   private ASPField  nextField;
   // Added by Terry 20130926
   // Show sort select in findlayout
   private ASPField  presField;
   // Added end

   private boolean isPropertiesPage = false;
   
   // Mutable Attributes.
   private int       default_mode;           private int          pre_default_mode;
   private boolean   editable;               private boolean      pre_editable;
   private int       layout_mode;            private int          pre_layout_mode;
   private int       history_mode;           private int          pre_history_mode;
   private int       last_mode;              private int          pre_last_mode;
   private boolean   layout_select = false;  private boolean      pre_layout_select = false;
   private boolean   dotted_line = true;     private boolean      pre_dotted_line = true;

   // Temporary variables
   private String minblocks = "";

   //==========================================================================
   // Constants and static variables
   //==========================================================================

   // These constants defines the various layout modes.
   public static final int UNDEFINED       = 0; //To disable a block use NONE instead.
   public static final int NEW_LAYOUT      = 1;
   public static final int EDIT_LAYOUT     = 2;
   public static final int FIND_LAYOUT     = 3;
   public static final int MULTIROW_LAYOUT = 4;
   public static final int SINGLE_LAYOUT   = 5;
   public static final int NONE            = 6;
   public static final int CUSTOM_LAYOUT   = 7;

   //   private static String  img_tab_left_grey                  = "GreyLeft20t15.gif";
   //   private static String  img_tab_middle_grey                = "GreyMid20t1.gif";
   //   private static String  img_tab_right_grey                 = "GreyRight20t15.gif";

   // This is incremented for each collapsible block, and used as unique identifier.
   private static int group_id                = 0;

   //rahelk
   private Vector localized_address_vec   = new Vector();

   private String link_functions;
   private String history_params;
   private String lu_name;
   private String lu_keys;
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPBlockLayout");

   private boolean end_table = true;
   private boolean last_group = false;
   private String[] default_commands={"",                       // UNDEFINED
                                      ASPCommandBar.SAVERETURN, // NEW_LAYOUT
                                      ASPCommandBar.SAVERETURN, // EDIT_LAYOUT
                                      ASPCommandBar.OKFIND,     // FIND_LAYOUT
                                      ASPCommandBar.FIND,       // MULTIROW_LAYOUT
                                      ASPCommandBar.FIND,       // SINGLE_LAYOUT
                                      "",                       // NONE
                                      ""};                      // CUSTOM_LAYOUT

   //==========================================================================
   // Constructors
   //==========================================================================

    protected ASPBlockLayout(ASPBlock obj)
    {
        super(obj);
        blk = obj;

        layout_select = pre_layout_select = getASPManager().getASPConfig().getParameter("PAGE/AUTO_LAYOUT_SELECT","N").equals("Y");

        synchronized (this)
        {
           my_id = group_id;
           group_id++;
        }
    };

    protected ASPBlockLayout(ASPBlock obj,boolean clone)
    {
        super(obj);
        blk = obj;

        synchronized (this)
        {
           my_id = group_id;
           group_id++;
        }
    };

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected ASPPoolElement clone( Object block ) throws FndException
   {
       ASPBlockLayout b = new ASPBlockLayout((ASPBlock)block,true);
      return cloneMe(b);
   }
   
   protected ASPPoolElement cloneMe(ASPBlockLayout bl) throws FndException
   {   
      ASPBlockLayout b = bl;
      //ASPBlockLayout b = new ASPBlockLayout((ASPBlock)block,true);
       //b.cmdbar         = (ASPCommandBar) commandbar;
       //b.blk            = ((ASPBlock)block;
       //ASPBlockLayout b = new ASPBlockLayout((ASPBlock)block);
       //b.blk            = (ASPBlock)block;
       //b.cmdbar         = ((ASPBlock)block).getASPCommandBar();

      b.default_commands = this.default_commands;
      b.default_mode   = b.pre_default_mode     = pre_default_mode;
      b.editable       = b.pre_editable         = pre_editable;
      b.layout_select  = b.pre_layout_select    = pre_layout_select;
      //b.layout_mode  = b.pre_layout_mode      = pre_layout_mode;
      //b.history_mode = b.pre_history_mode     = pre_history_mode;
      b.dotted_line    = b.pre_dotted_line      = pre_dotted_line;

      b.profile        = b.pre_profile          = pre_profile;

      b.my_id = my_id; // Not neccessary?

      b.dialog_order = dialog_order;
      b.dialog_columns = dialog_columns;
      b.spaceBetween = spaceBetween;
      b.spaceAfter = spaceAfter;
      b.groups = groups;
      b.widths = widths;

      b.initially_minimized = initially_minimized;

      b.link_functions = link_functions;
      b.history_params = history_params;
      b.lu_name = lu_name;
      b.lu_keys = lu_keys;

      //b.localized_address_vec = localized_address_vec;

      if (localized_address_vec.size()>0)
         for(int i=0; i<localized_address_vec.size(); i++)
            b.localized_address_vec.addElement(((LocalizedAddressField)localized_address_vec.elementAt(i)).clone(b.getBlock()) );


      b.column_count = column_count;
      b.columns = new ASPField[column_count];

      ASPPage newpage = (b.getBlock()).getASPPage();
      for (int i=0; i<column_count; i++)
         //b.columns[i] = columns[i];
         b.columns[i] = newpage.getASPField( columns[i].getName() );

      b.setCloned();
      return b;
   }


   protected void verify( ASPPage page ) throws FndException
   {
       /*      this.verifyPage(page);
      for( int i=0; i<count; i++ )
         items[i].verifyPage(page);
      rowset.verifyPage(page);
       */
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
       /*      scanAction(page,level);
      for( int i=0; i<count; i++ )
      items[i].scan(page,level+1); */
   }

   protected void doFreeze() throws FndException
   {
      createBaseProfile();

      Vector tmp_columns = listToFields(dialog_order,true);
      column_count = tmp_columns.size();
      columns = new ASPField[column_count];

      for(int j=0;j<column_count;j++)
          columns[j] = (ASPField)tmp_columns.elementAt(j);

      pre_default_mode = default_mode;
      pre_editable = editable;
      pre_layout_select = getASPManager().getASPConfig().getParameter("PAGE/AUTO_LAYOUT_SELECT","N").equals("Y");;
      pre_dotted_line = dotted_line;
   }

   protected void doReset() throws FndException
    {
      html_page = null;
      default_mode = pre_default_mode;
      editable = pre_editable;
      layout_select = pre_layout_select;
      link_functions = null;
      dotted_line = pre_dotted_line;
      user_profile_prepared = false;
      profile = pre_profile;

      isPropertiesPage = false;      

      int group_count = groups.size();
      for (int i=0; i<group_count; i++)
      {
         group thisGroup = (group) groups.elementAt(i);
         thisGroup.fields = thisGroup.pre_fields;
      }
    }

   protected void doActivate() throws FndException
    {
        ASPContext ctx = getASPPage().getASPContext();

        layout_select = pre_layout_select = getASPManager().getASPConfig().getParameter("PAGE/AUTO_LAYOUT_SELECT","N").equals("Y");

        counted      = 0;
        hascounted   = false;
        // Added by Terry 20130918
        // Advanced Query
        advanced_find = false;
        // Added end
        html_page    = new AutoString();
        layout_mode  = Integer.parseInt(ctx.readValue("__LAYOUTMODE"+getBlock().getName(),"0"));
        history_mode = Integer.parseInt(ctx.readValue("__HISTORYMODE"+getBlock().getName(),"0"));
        last_mode = Integer.parseInt(ctx.readValue("__LASTMODE"+getBlock().getName(),"0"));

        if(layout_mode==0)
            {
                layout_mode=default_mode;
                if(default_mode==FIND_LAYOUT) history_mode=MULTIROW_LAYOUT;
            }

        try
        {
           minblocks    = ctx.getCookie("__CBLOCKS");         // This can throw an exception
        } catch (Throwable e)                                 // for some inexplicable reason
        {
           minblocks = "";
        }

    }

   //==========================================================================
   // Miscellaneous private methods
   //==========================================================================

   private ASPBlock getBlock()
   {
      return blk;
   }

   //==========================================================================
   // Static methods
   //==========================================================================

   //==========================================================================
   // Public methods
   //==========================================================================

   /**
    * Get the name of this ASPBlock.
    */
   public String  getName()
   {
      return getBlock()==null ? "" : getBlock().getName();
   }

   /**
    * Get the identifier of this collapsible block.
    */
   public int getID()
   {
      return my_id;
   }

   public class group
   {
      int             id;
      String          title;        // title for this group
      String          fields;       // elements of this group, comma-separated.
      String          pre_fields;   // comma-separated list as defined in preDefine
      boolean         showHeader;   // show header/box
      boolean         showFirst;    // show content at first
      int             columns;      // number of columns per row
      boolean         showTitle;   // show title for this group

      public int getId()
      {
         return id;
      }

      /**
       * Return the title of this group.
       */
      public String getTitle()
      {
         return title;
      }

      public void setTitle(String title)
      {
         this.title = title;
      }

      /**
       * Return the comma separated list of field names.
       */
      public String getFieldList()
      {
         return fields;
      }

      public void setFieldList(String field_list)
      {
         fields = field_list;
      }

   }

   /**
    * Define a group of ASPFields in generated dialogs.
    * NOTE: If a group has been defined, the field order set with setFieldOrder() is ignored.
    *       Also, only the fields defined in groups are shown.
    * @param  title - Title for the group.
    * @param  fields - Comma separated list of fields to be included in the group.
    * @param  showHeader - Determines whether or not to show the group's header and footer.
    * @param  showFirst - Determines whether or not to show the fields inside the group by default.
    */
   public void defineGroup( String title,String fields,boolean showHeader,boolean showFirst )
   {
      defineGroup(title,fields,showHeader,showFirst,0,false);
   }


   /**
    * Define a group of ASPFields in generated dialogs.
    * @param  title - Title of the group.
    * @param  fields - Comma separated list of fields to be included in the group.
    * @param  showHeader - Determines whether or not to show the group's header and footer.
    * @param  showFirst - Determines whether or not to show the fields inside the group by default.
    * @param  columns - Number of columns used in the group to layout the HTML fields.
    */
   public void defineGroup( String title,String fields,boolean showHeader,boolean showFirst,int columns )
   {
       defineGroup(title,fields,showHeader,showFirst,columns,false);
   }

   /**
    * Define a group of ASPFields in generated dialogs.
    * @param  title - Title of the group.
    * @param  fields - Comma separated list of fields to be included in the group.
    * @param  showHeader - Determines whether or not to show the group's header and footer.
    * @param  showFirst - Determines whether or not to show the fields inside the group by default.
    * @param  showTitle - Determines whether or not to show the group's title even if the header is hidden (showHeader==fasle).
    */
   public void defineGroup( String title,String fields,boolean showHeader,boolean showFirst,boolean showTitle)
   {
      defineGroup(title,fields,showHeader,showFirst,0,showTitle);
   }

   /**
    * Define a group of ASPFields in generated dialogs.
    * @param  title - Title of the group.
    * @param  fields - Comma separated list of fields to be included in the group.
    * @param  showHeader - Determines whether or not to show the group's header and footer.
    * @param  showFirst - Determines whether or not to show the fields inside the group by default.
    * @param  columns - Number of columns used in the group to layout the HTML fields.
    * @param  showTitle - Determines whether or not to show the group's title even if the header is hidden (showHeader==fasle).
    */
   public void defineGroup( String title,String fields,boolean showHeader,boolean showFirst,int columns, boolean showTitle)
   {
      try {
         group newGroup = new group();
         newGroup.title = getASPManager().translate(title);
         newGroup.fields = newGroup.pre_fields = fields;
         newGroup.showHeader = showHeader;
         newGroup.showFirst = showFirst;
         newGroup.columns = columns;
         newGroup.showTitle = showTitle;

         Vector field_vec = listToFields(fields, false);
         int field_count = field_vec.size();

         synchronized (this)
         {
            newGroup.id = group_id;

            for (int i=0; i<field_count; i++)
               ((ASPField)field_vec.elementAt(i)).setGroupId(group_id);

            group_id++;
         }

         groups.addElement(newGroup);
         if ("".equals(dialog_order))
            dialog_order = fields;
         else
            dialog_order +="," + fields;
       }
       catch(Throwable any){error(any);}
   }

   /**
    * Sets this block to be initially minimized.
    */
   public void setInitiallyMinimized()
   {
      try
      {
         modifyingImmutableAttribute("INITIALLY_MINIMIZED");
         initially_minimized = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   boolean isInitiallyMinimized()
   {
      return initially_minimized;
   }

   /* Used by ASPPopup to remove Layout properties... RMB if groups are defined
    * Q&D solution for the profile bug when groups have been defined
    */

   public boolean hasDefinedGroups()
   {
      return (groups.size()>0);
   }

   public String getGroupName(int group_id)
   {
      int group_count = groups.size();
      for (int i=0; i<group_count; i++)
      {
         group thisGroup = (group) groups.elementAt(i);
         if (group_id == thisGroup.id) return thisGroup.title;
      }
      return "";
   }
   
   //
   // Added by Terry 20130611
   // Set group field by title
   //
   public void setGroupFields(String title, String fields)
   {
      ASPManager mgr = getASPManager();
      String trans_title = mgr.translate(title);
      
      int group_count = groups.size();
      for (int i=0; i<group_count; i++)
      {
         group thisGroup = (group) groups.elementAt(i);
         if (trans_title.equals(thisGroup.title))
         {
            thisGroup.fields = thisGroup.pre_fields = fields;
            break;
         }
      }
   }

   /**
    * Return the vector object representing the collection of defined groups for this blocklayout.
    */
   public Vector getDefinedGroups()
   {
      return groups;
   }

   /**
    * Sets the fields of this blocklayout to be editable. For custom designed layout.
    */
   public void setEditable( )
   {
      editable = true;
   }

   /**
    * Sets the fields of this blocklayout to not be editable. For custom designed layout.
    */
   public void unsetEditable( )
   {
      editable = false;
   }

   /**
    * Set the number of columns.
    */
   public void setDialogColumns( int cols )
   {
      dialog_columns = cols;
   }

   /**
    * Returns the number of columns.
    */
   public int getDialogColumns()
   {
      return dialog_columns;
   }

   class columnWidth
   {
      int column;
      int label_width;
      int data_width;
   }

   /**
    * Set the width of a column.
    */
   public void setColumnWidth( int column, int labelWidth, int dataWidth)
   {
      columnWidth c = new columnWidth();
      c.column = column;
      c.label_width = labelWidth;
      c.data_width = dataWidth;
      widths.addElement(c);
   }

    /**
     * Set the field order in which the field of this block will appear in the single row layout. Skipped fields will be displayed
     * last, if they are visible
     */
   public void setFieldOrder( String cols )
   {
      // TODO: check if this order will work, else error.
      dialog_order = cols;
   }
   
   // Added by Terry 20131027
   // Get the field order
   public String getFieldOrder()
   {
      return dialog_order;
   }
   // Added end


   /**
    * Set the address fields to be viewed using the localized address format.
    * @param add1 ADDRESS1
    * @param add2 ADDRESS2
    * @param zip_code ZIP_CODE
    * @param city CITY
    * @param state STATE
    * @param county COUNTY
    * @param country_code COUNTRY_CODE
    * @param country COUNTRY
    * The label shown in SINGLE,EDIT and NEW layouts will be that of ADDRESS1.
    */
   public void setAddressFieldList(ASPField add1,
                                   ASPField add2,
                                   ASPField zip_code,
                                   ASPField city,
                                   ASPField state,
                                   ASPField county,
                                   ASPField country_code,
                                   ASPField country)
   {
      setAddressFieldList(add1,add2,zip_code,city,state,county,country_code,country,null,null);
   }


   /**
    * Set the address fields to be viewed using the localized address format.
    * @param add1 ADDRESS1
    * @param add2 ADDRESS2
    * @param zip_code ZIP_CODE
    * @param city CITY
    * @param state STATE
    * @param county COUNTY
    * @param country_code COUNTRY_CODE
    * @param country COUNTRY
    * @param address_label Translatable label shown in SINGLE,EDIT and NEW layouts.
    * @param class_name name of the extened class.
    */
   public void setAddressFieldList(ASPField add1,
                                   ASPField add2,
                                   ASPField zip_code,
                                   ASPField city,
                                   ASPField state,
                                   ASPField county,
                                   ASPField country_code,
                                   ASPField country,
                                   String address_label,
                                   String class_name)
   {

      try
      {
         modifyingImmutableAttribute("ADDRESS_FIELD_LIST");
         add1.setFirstAddressField();
         LocalizedAddressField laf;

         if(getASPManager().isEmpty(class_name))
            laf = (LocalizedAddressField)Class.forName( getASPManager().getASPConfig().getLocalizedAddressClassName()).newInstance();
         else
            laf = (LocalizedAddressField)Class.forName(class_name).newInstance();

         laf.construct(add1,add2,zip_code,city,state,county,country_code,country,address_label,getASPManager());
         localized_address_vec.addElement(laf);


      }
      catch(ClassNotFoundException e)
      {
         error(e);
      }
      catch(Throwable e)
      {
         error(e);
      }


   }

   //return the index of the address structure starting with field_name.
   private int getAddressElement(String field_name)
   {
      for (int i=0; i<localized_address_vec.size(); i++)
      {
         LocalizedAddressField laf = (LocalizedAddressField)localized_address_vec.elementAt(i);
         if (laf.isFirstAddressField(field_name))
            return i;

      }
      return -1;
   }

   private void addressDisplayColumn(ASPRowSet rowset, int layout_mode, String field_name)
   {

      LocalizedAddressField laf = (LocalizedAddressField)localized_address_vec.elementAt(getAddressElement(field_name));


      html_page.append("\n\t\t<td ",getLabelWidth());
      html_page.append(" nowrap "," class=\"normalTextLabel\"");
      html_page.append(" valign=top");
      html_page.append(">");
      html_page.append(getASPManager().getEmptyImage(1,4),"<br>");
      html_page.append(laf.getAddressLabel(),"</td>");
      html_page.append(appendSpaceBetween());

      html_page.append(laf.getAddressColumn(getCurrentRowValue(rowset,laf.field_list[0].getName(),laf.field_list[0]),
                                            getCurrentRowValue(rowset,laf.field_list[1].getName(),laf.field_list[1]),
                                            getCurrentRowValue(rowset,laf.field_list[2].getName(),laf.field_list[2]),
                                            getCurrentRowValue(rowset,laf.field_list[3].getName(),laf.field_list[3]),
                                            getCurrentRowValue(rowset,laf.field_list[4].getName(),laf.field_list[4]),
                                            getCurrentRowValue(rowset,laf.field_list[5].getName(),laf.field_list[5]),
                                            getCurrentRowValue(rowset,laf.field_list[6].getName(),laf.field_list[6]),
                                            getCurrentRowValue(rowset,laf.field_list[7].getName(),laf.field_list[7]),
                                            layout_mode,getASPManager().isExplorer(),getASPManager()));


      if(nextField == null || !nextField.isSimple())
      {
         html_page.append("</font></td>");
         html_page.append(appendSpaceAfter());
      }
   }

   private void hideAllAdressFields()
   {
      for (int i=0; i<localized_address_vec.size(); i++)
      {
         LocalizedAddressField laf = (LocalizedAddressField)localized_address_vec.elementAt(i);
         laf.hideAll();
      }

   }


   /**
    * Inform the ASPBlockLayout of the value of a count database rows. The result will be displayed in the form, if you're in a
    * Find dialog.
    */
   public void setCountValue(int value)
   {
      counted = value;
      hascounted = true;
   }
   
   public boolean hasCounted()
   {
      return hascounted;
   }
   
   public int getCountedValue()
   {
      return !hascounted? 0: counted;
   }
   
   // Added by Terry 20130918
   // Advanced Query
   public void setAdvancedQuery()
   {
      advanced_find = true;
   }
   
   public boolean hasAdvancedQuery()
   {
      return advanced_find;
   }
   // Added end
   

  /**
   *  Generates an introductory "starting page". If the show() function is used,
   *  this is automatically called if the rowset is empty.
   */
   public String introPage()
   {
      AutoString str = new AutoString();
      ASPManager mgr = getASPManager();
      String image_location = mgr.getASPConfig().getImagesLocation();
      boolean has_contents = blk.getASPCommandBar().IsEnabled("Find") || (blk.getASPCommandBar().IsEnabled("NewRow") && blk.isCommandDefined(ASPRowSet.NEW));
       if(has_contents)
       {
         //page frame start
         str.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr><td>&nbsp;&nbsp;</td><td width=\"100%\">\n" );

         str.append("<table id=cnt",blk.getName());
         if(mgr.isExplorer() && (isMinimized(my_id) || (!isMaximized(my_id) && initially_minimized)))
            str.append(" style=\"display: none;\"");
         str.append(" border=0 cellspacing=0 cellpadding=4 class=",dotted_line?"pageFormWithBorder":"pageFormWithoutBottomLine"," width=100%>\n");

         str.append("\t<tr><td width=100% colspan=3>&nbsp;</td></tr>");
      }

      if(blk.getASPCommandBar().IsEnabled("Find"))
      {
         str.append("\t<tr>");
         str.append("\t<td width=40>");
         str.append("\t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         str.append("\t</td>");
         str.append("\t<td><center>");
         str.append("\t<a href=\"javascript:commandSet('"+blk.getName()+".Find','')\">");
         str.append("\t<img border=0 src=\"",image_location,mgr.getConfigParameter("PAGE/DEFAULT_DIALOG/FIND/NORMAL",     "default_find.gif"),"\">");
         str.append("\t</center></a></td>");
         str.append("\t<td class=introPageText width=100%>");
         str.append(mgr.translateJavaText("FNDLAYPRFIND: &1Find&2 existing records","<a href=\"javascript:commandSet('"+blk.getName()+".Find','')\">","</a>"),"</td></tr>\n");
      }

      if(blk.getASPCommandBar().IsEnabled("NewRow") && blk.isCommandDefined(ASPRowSet.NEW))
      {
         str.append("\t <tr>");
         str.append("\t <td width=40>");
         str.append("\t &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         str.append("\t </td>");
         str.append("\t <td><center>");
         str.append("\t <a href=\"javascript:commandSet('"+blk.getName()+".NewRow','')\">");
         str.append("\t <img border=0 src=\"",image_location,mgr.getConfigParameter("PAGE/DEFAULT_DIALOG/NEWROW/NORMAL",     "default_new.gif"), "\">");
         str.append("\t </a></center>");
         str.append("\t </td>");
         str.append("\t <td class=introPageText width=100%>");
         str.append(mgr.translateJavaText("FNDLAYPRNEW: Enter a &1New&2 record","<a href=\"javascript:commandSet('"+blk.getName()+".NewRow','')\">","</a>"),"</td></tr>\n");
      }

      if(has_contents)
      {

         str.append("\t<tr><td width=100% colspan=3>&nbsp;</td></tr>");

         str.append("</table>\n");

         //page frame end
         str.append( " </td><td>&nbsp;&nbsp;</td></table>\n" );
      }
      return str.toString();
   }


  /**
   *  Generates an HTML presentation of the block.
   */
   public String show() throws FndException
   {

      AutoString presentation = new AutoString();
      ASPManager mgr = getASPManager();
          
      if(isVisibleByUserProfile())
      {
         if(blk.getMasterBlock()==null && (blk.getASPRowSet().countRows() == 1) && (isMultirowLayout() || isSingleLayout()) && isAutoLayoutSelectOn())
         {
                 blk.getASPCommandBar().disableCommand("Back");
                 setLayoutMode(SINGLE_LAYOUT);
                 getASPPage().saveLayout();
         }

         //enable/disable table and layout properties depending on the layout mode
         /*if(blk.getASPRowSet().countRows() == 0 && !isFindLayout() && !isCustomLayout() && blk.getMasterBlock()==null)
         {
            blk.getASPCommandBar().disableCommand(ASPCommandBar.TABLE_PROPERTIES); //don't show in intro page
            blk.getASPCommandBar().disableCommand(ASPCommandBar.LAYOUT_PROPERTIES);
         }
         else
         {
            if( getLayoutMode() == MULTIROW_LAYOUT)
            {
              if (!blk.getASPCommandBar().findItem(ASPCommandBar.TABLE_PROPERTIES).isUserDisabled());
                 blk.getASPCommandBar().enableCommand(ASPCommandBar.TABLE_PROPERTIES);
              blk.getASPCommandBar().disableCommand(ASPCommandBar.LAYOUT_PROPERTIES);
            }
            else
            {
              if (!blk.getASPCommandBar().findItem(ASPCommandBar.LAYOUT_PROPERTIES).isUserDisabled());
                 blk.getASPCommandBar().enableCommand(ASPCommandBar.LAYOUT_PROPERTIES);
              blk.getASPCommandBar().disableCommand(ASPCommandBar.TABLE_PROPERTIES);
            }
         }*/
        //addItem(mgr.translate("FNDBLKLAYPROP: Layout Properties"),"showNewBrowser_('" + mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS") + "BlockLayoutProfile.page?URL="+asppage.getPoolKey() + "&OBJNAME=" + bar.getBlock().getName() + "',600,450)");

         /*
         ASPCommandBarItem prop_item = blk.getASPCommandBar().findItem(ASPCommandBar.PROPERTIES);
         if(blk.getASPRowSet().countRows() == 0 && !isFindLayout() && !isCustomLayout() && blk.getMasterBlock()==null)
         {
            prop_item.userDisable(); //disbale since prepareProfileInfo() has not stillbeing called.
         }
         else
         {
            presentation.append("\n\t\t</td>\n\t</tr>\n");
         }
         */

         // Show commandbar (if available)
         presentation.append(blk.getASPCommandBar().showBar());

         String image_location = mgr.getASPConfig().getImagesLocation();

         presentation.append("\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%");
         presentation.append("\">\n\t<tr>\n\t\t<td>");

         if(blk.getASPRowSet().countRows() == 0 && !isFindLayout() && !isCustomLayout() && blk.getMasterBlock()==null)
         {
            // if we have no rows, show introductory page.
            presentation.append(introPage());
            presentation.append("\n\t\t</td>\n\t</tr>\n");
         }
         else
         {
            // Show table or dialog
            if( getLayoutMode() == MULTIROW_LAYOUT)
               presentation.append(blk.getASPTable().populate());
            else
               presentation.append(generateDialog());

            presentation.append("\n\t\t</td>\n\t</tr>\n");

         }
         
         try {
            if(blk.isMasterBlock() || blk.getMasterBlock()==null){
               mgr.startRWCInterfaceItem("tasks");
               mgr.addRWCInterfaceValue("overview_page", (isMultirowLayout() && blk.getASPRowSet().countRows()>0)?"Y":"N");
               mgr.addRWCInterfaceValue("lu_name", blk.getLUName());
               mgr.endRWCInterfaceItem("tasks");
            }
         } catch (FndException ex) {
            ex.printStackTrace();
         }

         presentation.append("\t</table>\n");

         if(isSingleLayout() && !getASPManager().isEmpty(getBlock().getLUName()) && !getASPManager().isEmpty(getBlock().getLUKeys()))
         {
            if(blk.hasASPTable())
            {
               presentation.append("\n<script language='JavaScript'>\n");
               presentation.append("__lu_name"+blk.getASPTable().getTableNo()+"=\""+lu_name+"\";\n");
               presentation.append("__history_params"+blk.getASPTable().getTableNo()+"= new Array(\""+history_params+"\");</script>\n");
            }
            else
            {
              presentation.append("\n<script language='JavaScript'>\n");
              presentation.append("__lu_name"+blk.getName()+"=\""+lu_name+"\";\n");
              presentation.append("__history_params"+blk.getName()+"= new Array(\""+history_params+"\");</script>\n");
            }
         }

         if(isSingleLayout() && !Str.isEmpty(link_functions))
            presentation.append("\n<script language='JavaScript'>"+link_functions+"</script>\n");

         blk.appendPerformField(presentation);
      }
      return presentation.toString();

   }
   private AutoString getFindAidTag()
   {
      AutoString findAidTag = new AutoString();
      ASPManager mgr = getASPManager();

      findAidTag.append("\n<script language='JavaScript'>");
      findAidTag.append("\nvar ANY_VALUE         = '"+ mgr.translateJavaScript("FNDLAYANYVALUE: (any value)")+"'");
      findAidTag.append("\nvar NO_VALUE          = '"+ mgr.translateJavaScript("FNDLAYNOVALUE: (no value)")+"'");
      findAidTag.append("\nvar LARGER_THAN       = '"+ mgr.translateJavaScript("FNDLAYLARGERTHAN: (larger than)")+"'");
      findAidTag.append("\nvar LESS_THAN         = '"+ mgr.translateJavaScript("FNDLAYLESSTHAN: (less than)")+"'");
      findAidTag.append("\nvar LARGER_OR_EQUAL_TO= '"+ mgr.translateJavaScript("FNDLAYLARGEROREQUAL: (larger or equal to)")+"'");
      findAidTag.append("\nvar LESS_OR_EQUAL_TO  = '"+ mgr.translateJavaScript("FNDLAYLESSOREQUAL: (less or equal to)")+"'");
      findAidTag.append("\nvar NOT_EUQAL_TO      = '"+ mgr.translateJavaScript("FNDLAYNOTEQUAL: (not equal to)")+"'");
      findAidTag.append("\nvar BETWEEN           = '"+ mgr.translateJavaScript("FNDLAYBETWEEN: (between)")+"'"); 
      findAidTag.append("\nvar OR                = '"+ mgr.translateJavaScript("FNDLAYOR: (or)")+"'");
      findAidTag.append("\n</script>");
      findAidTag.append("\n<span id=\"findAid\" style=\"border:1px solid black;position:absolute;visibility:hidden;\">");              
      findAidTag.append("</span>\n");
      return findAidTag;
   }
   int getColumnCount()
   {
      return listToFields(dialog_order,true).size();
   }


   ASPField getColumn( int index )
   {
      return (ASPField)listToFields(dialog_order,true).elementAt(index);
   }


  /**
   *  Generates a HTML presentation of the block, but without enclosing HTML table, and without intro page.
   */
   public String generateDataPresentation()
   {
      try{
      // Show table or dialog
      if( getLayoutMode() == MULTIROW_LAYOUT)
         return blk.getASPTable().populate();
      else
      {

         return generateDialog();
      }
      }
      catch(Throwable any){error(any); return "";}
   }


   private ASPField getField( String fieldName )
   {
           ASPField[] cols = getBlock().getFields();
           for(int i = 0;i<cols.length;i++)
           {
               if(cols[i].getName().equals(fieldName) && (!cols[i].isHidden() || cols[i].isFirstAddressField()))
                  return cols[i];
           }

           return null;
   }

   public Vector listToFields( String fieldlist, boolean addRemaining )
   {
        Vector columns = new Vector();
        StringTokenizer myFields;
        int i;
        String fld;
        ASPField[] allcolumns;

        myFields = new StringTokenizer(fieldlist,",");

        while(myFields.hasMoreTokens())
        {
            fld = myFields.nextToken();
            ASPField temp = getField(fld);
            if (temp != null && !columns.contains(temp))
                columns.addElement(temp);
        }

        if(addRemaining)
        {
           allcolumns = blk.getFields();
           for(i = 0;i<allcolumns.length;i++)
           {
                if(!columns.contains(allcolumns[i]))
                   columns.addElement(allcolumns[i]);
           }
        }

        return columns;

   }

   /**
    *  Checks if this collapsible block is minimized by the user, by looking
    *  for its ID in the __CBLOCKS cookie.
    */
   boolean isMinimized(int id)
   {
      if(minblocks!=null && minblocks.indexOf("N" + id + ",") > -1)
         return true;
      else
         return false;
   }



   /**
    *  Checks if this collapsible block is maximized by the user, by looking
    *  for its ID in the __CBLOCKS cookie.
    */
   boolean isMaximized(int id)
   {
      if(minblocks!=null && minblocks.indexOf("Y" + id + ",") > -1)
         return true;
      else
         return false;
   }


   /**
    *  Generates a HTML presentation of the block, as a single record.
    */
   public String generateDialog()
   {
       try{
           //Vector tmp_columns = new Vector();
           //Vector columns_vec = new Vector();  //no profile support if the programmer has defined groups
           ASPRowSet rowset;
           StringTokenizer myFields;
           int i;
           ASPManager mgr = blk.getASPManager();
           // Added by Terry 20120821
           // Set page size field
           ASPContext ctx = mgr.getASPContext();
           String buffer_size = ctx.readValue("__"+blk.getName()+".MAX_ROWS", getASPManager().getConfigParameter("ADMIN/BUFFER_SIZE","10"));
           // Added end
           boolean show_border = mgr.getConfigParameter("AUDIT/__DEBUG/SHOW_BORDER","N").equals("Y");
           String case_sensitive_flag = mgr.getConfigParameter("ADMIN/CASE_SENSITIVE_SEARCH","Y");
           boolean case_sensitive_warning = mgr.getConfigParameter("ADMIN/SHOW_CASE_SENSITIVE_WARNING","N").equals("Y");
           end_table = true;
           String image_location = mgr.getASPConfig().getImagesLocation();

           rowset = blk.getASPRowSet();

           blk.appendPerformField(html_page);

           //page frame start
           html_page.append("<table cellspacing=0 cellpadding=0 border=0 width=100%>");
           html_page.append("<tr>");
           
           // Added by Terry 20140906
           // Show frame space mark
           if (blk.hasCommandBar() && blk.getASPCommandBar().isCmdShowFrameSpace())
              html_page.append("<td>&nbsp;&nbsp;</td>");
           // Added end
           html_page.append("<td width=100%>");

           html_page.append("\n<table ");
            if((mgr.isExplorer()||mgr.isMozilla()) && (isMinimized(my_id) || (!isMaximized(my_id) && initially_minimized)))
                  html_page.append("style=\"display: none;\" ");
            if(show_border)
               html_page.append("border=1");
            else
               html_page.append("border=0");

            if(groups.size() == 0)
               html_page.append(" width=\"100%\" id=\"cnt",blk.getName(),"\" height=100% class=",dotted_line?"pageFormWithBorder":"pageFormWithoutBottomLine"," cellspacing=0 cellpadding=4>");
            else
               html_page.append(" width=100% id=\"cnt",blk.getName(),"\" class=",dotted_line?"pageFormWithBorder":"pageFormWithoutBottomLine"," cellspacing=0 cellpadding=4>");
            html_page.append("<tr><td width=100%>");

            html_page.append("<table width=\"100%\" height=100% cellspacing=0 cellpadding=0>");

            if(isFindLayout())
            {
               if (!mgr.isEmpty(mgr.readValue("__CASESS_VALUE")))
                  case_sensitive_flag = mgr.readValue("__CASESS_VALUE");

               html_page.append("<tr><td colspan=\""+(dialog_columns*4)+"\">");

               html_page.append("<table border=0 width=100% cellspacing=0 cellpadding=0>");
               
               UserDataCache user_cache = UserDataCache.getInstance();
               Vector available_views = user_cache.getApplicationSearchViews(getASPManager().getSessionId());
               Vector domainList = UserDataCache.getInstance().getSearchDomains(getASPManager().getSessionId());
               
               if(mgr.isRTL())
               {
                  if(!mgr.getASPPage().getASPProfile().isUserProfileDisabled())
               {
                  html_page.append("<tr>");
                  html_page.append("      <td colspan=1 align=right>");               
                  html_page.append("         <table border=0 cellspacing=0 cellpadding=0>");
                  html_page.append("            <tr>");
                  html_page.append("               <td colspan=1 align=right nowrap>");
                  html_page.append("                  <SPAN class=normalTextLabel>");
                  html_page.append(                 getASPManager().translateJavaText("FNDLAYSAVEDQRY: Saved Queries"));               
                  html_page.append(                      getASPManager().getEmptyImage(10,1));               
                  html_page.append(                      blk.getASPCommandBar().addSaveQuery());              
                  html_page.append("                  </SPAN>");
                  html_page.append("               </td>");
                  html_page.append("               <td colspan=1 align=right nowrap>");
                  html_page.append("                  <SPAN class=normalTextLabel>");
                  html_page.append("                     |");               
                  html_page.append("                     <font class=normalTextLabel>"+mgr.translateJavaText("FNDLAYCSENSEAR: Case sensitive search")+"</font>");               
                  html_page.append("                     &nbsp;<input class='checkbox' type=checkbox name=__CASESS value=1 ","N".equals(case_sensitive_flag)?"":"checked"," onClick=\"javascript:document.form.__CASESS_VALUE.value = (document.form.__CASESS.checked)?'Y':'N';");
                  if (case_sensitive_warning)
                     html_page.append("showCaseSensitiveWarning();\") >");   
                  else
                     html_page.append("\") >");
                  
                  // Added by Terry 20131118
                  // Accurate Find
                  String accurate_find = mgr.readValue("__ACCURATE_FIND", "FALSE");
                  html_page.append("                     |");               
                  html_page.append("                     <font class=normalTextLabel>"+mgr.translateJavaText("FNDLAYACCURATEFIND: Accurate Search")+"</font>");               
                  html_page.append("                     &nbsp;<input class='checkbox' type=checkbox name=__ACCURATE_FINDSS value='FALSE'", "TRUE".equals(accurate_find)?"checked":"", " onClick=\"javascript:document.form.__ACCURATE_FIND.value = (document.form.__ACCURATE_FINDSS.checked)?'TRUE':'FALSE';\"", ">\n");
                  html_page.append("                     <input type=hidden name=__ACCURATE_FIND value=\"" + accurate_find + "\">\n");
                  // Added end
                  
                  // Added by Terry 20120821
                  // Set page size field
                  html_page.append("                     |");
                  html_page.append("                     <font class=normalTextLabel>"+mgr.translateJavaText("FNDLAYSETPAGESIZE: Page Size")+"</font>");
                  html_page.append("                     &nbsp;<input class='editableTextField' type=text size=10 name=__PAGESIZE value=\"",buffer_size,"\">");
                  // Added end

                  html_page.append("                  </SPAN>");
                  html_page.append("                  </td>");
                  }
                  else
                  {
                     html_page.append("<tr>");
                     html_page.append("      <td colspan=1 align=right>");               
                     html_page.append("         <table border=0 cellspacing=0 cellpadding=0>");
                     html_page.append("            <tr>");
                  }
                  
                  if(mgr.getASPConfig().isHybridSearchAvailable() && available_views.contains(blk.getView()))
                  {
                     int noOfDomains = domainList.size();
                     String view = blk.getView();
                     String searchDomain = "";
                     
                     for (int j=0; j<noOfDomains; j++)
                     {
                        if (view.equals(((SearchDomain)domainList.elementAt(j)).view))
                        {
                           searchDomain = ((SearchDomain)domainList.elementAt(j)).searchDomain;
                           break;
                        }
                     }

                     html_page.append("<td align=left width=100%><span id=appsearchwhatsthis"+searchDomain+" onmouseover=\"mouseOverSearchHelpSpan=true;\" onmouseout=\"mouseOverSearchHelpSpan=false;\" OnClick=\"if(helpMode) showSearchDomainHelp('"+searchDomain+"')\"><font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','dummyUsageId')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=\"normalTextLabel\">",getASPManager().translateJavaText("FNDLAYAPPSEARCH: Containing Text"),"</span>&nbsp;<input class='editableTextField' name=FND_TXT_SEARCH size=20></font></td>"); 
                  }

                  html_page.append("               </tr>");
                  html_page.append("            </table>");               
                  html_page.append("      </td>");
                  html_page.append("   </tr>"); 
                  if (hascounted)
                     html_page.append("<tr><td colspan=1 align=right><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYCOUNT: This query, unedited, will retrieve &1 rows",Integer.toString(counted)),"</SPAN></td></tr>");
                  
                  // Added by Terry 20130919
                  String advanced_query_string = "";
                  String advanced_query_order = "";
                  
                  if (advanced_find)
                  {
                     advanced_query_string = getAdvancedQueryString();
                     advanced_query_order = getAdvancedQueryOrder();
                  }
                  else
                  {
                     advanced_query_string = mgr.readValue("__" + blk.getName() + "_ADVANCED_QUERY_STRING");
                     advanced_query_order  = mgr.readValue("__" + blk.getName() + "_ADVANCED_QUERY_ORDER");
                  }
                  
                  if (!Str.isEmpty(advanced_query_string) || !Str.isEmpty(advanced_query_order))
                     advanced_find = true;
                  // Added end
                  
                  // Added by Terry 20130917
                  // Advanced Query
                  // To do
                  if (advanced_find)
                  {
                     html_page.append("<tr><td colspan=1 align=right><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYWHERE: Advanced Query - Where"),"</SPAN></td></tr>");
                     html_page.append("<tr><td colspan=1 align=right>", "<textarea class='editableTextArea' style='width:100%' rows=5 name=__" + blk.getName() + "_ADVANCED_QUERY_STRING>",(Str.isEmpty(advanced_query_string) ? getAdvancedQueryString() : advanced_query_string),"</textarea>","</td></tr>");
                     html_page.append("<tr><td colspan=1 align=right><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYORDERBY: Advanced Query - Order By"),"</SPAN></td></tr>");
                     html_page.append("<tr><td colspan=1 align=right>", "<input class='editableTextField' type=text style='width:100%' name=__" + blk.getName() + "_ADVANCED_QUERY_ORDER value=\"",(Str.isEmpty(advanced_query_order) ? getAdvancedQueryOrder() : advanced_query_order),"\">","</td></tr>");
                     
                     html_page.append("<tr><td colspan=1 align=right><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYLUNAME: Lu Name: &1", Str.isEmpty(blk.getBlkLUName()) ? mgr.getLUName(blk.getView()) : blk.getBlkLUName()),"</SPAN></td></tr>");
                     html_page.append("<tr><td colspan=1 align=right><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYVIEW: View: &1", blk.getView()),"</SPAN></td></tr>");
                     html_page.append("<tr><td colspan=1 align=right><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYPACKAGE: Package: &1", Str.isEmpty(blk.getPackage()) ? "" : blk.getPackage()),"</SPAN></td></tr>");
                  }
                  // Added end
               }
               else
               {
                  html_page.append("<tr>");

                  if(!mgr.getASPPage().getASPProfile().isUserProfileDisabled())
                  {
                     html_page.append("<td colspan=1 align=left><SPAN class=normalTextLabel>",getASPManager().translateJavaText("FNDLAYSAVEDQRY: Saved Queries"),getASPManager().getEmptyImage(10,1),blk.getASPCommandBar().addSaveQuery() ,"</SPAN>");
                     html_page.append("&nbsp;&nbsp;|&nbsp;&nbsp;<font class=normalTextLabel>"+mgr.translateJavaText("FNDLAYCSENSEAR: Case sensitive search")+"</font>");
                     html_page.append("&nbsp;<input class='checkbox' type=checkbox name=__CASESS value=1 ","N".equals(case_sensitive_flag)?"":"checked"," onClick=\"javascript:document.form.__CASESS_VALUE.value = (document.form.__CASESS.checked)?'Y':'N';");
                     if (case_sensitive_warning)
                        html_page.append("showCaseSensitiveWarning();\")></td>");   
                     else
                     {
                        // Modified by Terry 20120821
                        // Set page size field 
                        // Original: html_page.append("\")></td>");
                        html_page.append("\")>");
                        // Modified end
                     }
                     
                     // Added by Terry 20131118
                     // Accurate Find
                     String accurate_find = mgr.readValue("__ACCURATE_FIND", "FALSE");
                     html_page.append("&nbsp;&nbsp;|&nbsp;&nbsp;<font class=normalTextLabel>"+mgr.translateJavaText("FNDLAYACCURATEFIND: Accurate Search")+"</font>");               
                     html_page.append("                     &nbsp;<input class='checkbox' type=checkbox name=__ACCURATE_FINDSS value='FALSE'", "TRUE".equals(accurate_find)?"checked":"", " onClick=\"javascript:document.form.__ACCURATE_FIND.value = (document.form.__ACCURATE_FINDSS.checked)?'TRUE':'FALSE';\"", ">\n");
                     html_page.append("                     <input type=hidden name=__ACCURATE_FIND value=\"" + accurate_find + "\">\n");
                     // Added end
                     
                     // Added by Terry 20120821
                     // Set page size field
                     html_page.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
                     html_page.append("<font class=normalTextLabel>"+mgr.translateJavaText("FNDLAYSETPAGESIZE: Page Size")+"</font>");
                     html_page.append("&nbsp;<input class='editableTextField' type=text size=10 name=__PAGESIZE value=\"",buffer_size,"\">");
                     html_page.append("</td>");
                     // Added end
               }
               
               String view = blk.getView();
               if(!mgr.isEmpty(view) && mgr.getASPConfig().isHybridSearchAvailable() && available_views.contains(view))
               {
                  int noOfDomains = domainList.size();
                  String searchDomain = "";

                  for (int j=0; j<noOfDomains; j++)
                  {
                     if (view.equals(((SearchDomain)domainList.elementAt(j)).view))
                     {
                        searchDomain = ((SearchDomain)domainList.elementAt(j)).searchDomain;
                        break;
                     }
                  }

                  html_page.append("<td align=right><span id=appsearchwhatsthis"+searchDomain+" onmouseover=\"mouseOverSearchHelpSpan=true;\" onmouseout=\"mouseOverSearchHelpSpan=false;\" OnClick=\"if(helpMode) showSearchDomainHelp('"+searchDomain+"')\"><font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','dummyUsageId')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=\"normalTextLabel\">",getASPManager().translateJavaText("FNDLAYAPPSEARCH: Containing Text"),"</span>&nbsp;<input class='editableTextField' name=FND_TXT_SEARCH size=20></font></td>"); 
               }

               
               
               html_page.append("</tr>");

               if (hascounted)
                  html_page.append("<tr><td colspan=1 align=left><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYCOUNT: This query, unedited, will retrieve &1 rows",Integer.toString(counted)),"</SPAN></td></tr>");

               // Added by Terry 20130919
               String advanced_query_string = "";
               String advanced_query_order = "";
               
               if (advanced_find)
               {
                  advanced_query_string = getAdvancedQueryString();
                  advanced_query_order = getAdvancedQueryOrder();
               }
               else
               {
                  advanced_query_string = mgr.readValue("__" + blk.getName() + "_ADVANCED_QUERY_STRING");
                  advanced_query_order  = mgr.readValue("__" + blk.getName() + "_ADVANCED_QUERY_ORDER");
               }
               
               if (!Str.isEmpty(advanced_query_string) || !Str.isEmpty(advanced_query_order))
                  advanced_find = true;
               // Added end
               
               // Added by Terry 20130917
               // Advanced Query
               // To do
               if (advanced_find)
               {
                  html_page.append("<tr><td colspan=1 align=left><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYWHERE: Advanced Query - Where"),"</SPAN></td></tr>");
                  html_page.append("<tr><td colspan=1 align=left>", "<textarea class='editableTextArea' style='width:100%' rows=5 name=__" + blk.getName() + "_ADVANCED_QUERY_STRING>",(Str.isEmpty(advanced_query_string) ? getAdvancedQueryString() : advanced_query_string),"</textarea>","</td></tr>");
                  html_page.append("<tr><td colspan=1 align=left><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYORDERBY: Advanced Query - Order By"),"</SPAN></td></tr>");
                  html_page.append("<tr><td colspan=1 align=left>", "<input class='editableTextField' type=text style='width:100%' name=__" + blk.getName() + "_ADVANCED_QUERY_ORDER value=\"",(Str.isEmpty(advanced_query_order) ? getAdvancedQueryOrder() : advanced_query_order),"\">","</td></tr>");
                  
                  html_page.append("<tr><td colspan=1 align=left><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYLUNAME: Lu Name: &1", Str.isEmpty(blk.getBlkLUName()) ? mgr.getLUName(blk.getView()) : blk.getBlkLUName()),"</SPAN></td></tr>");
                  html_page.append("<tr><td colspan=1 align=left><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYVIEW: View: &1", blk.getView()),"</SPAN></td></tr>");
                  html_page.append("<tr><td colspan=1 align=left><SPAN class=boldTextValue>", getASPManager().translateJavaText("FNDLAYADVANCEDQUERYPACKAGE: Package: &1", Str.isEmpty(blk.getPackage()) ? "" : blk.getPackage()),"</SPAN></td></tr>");
               }
               // Added end
               }
               
               html_page.append("<tr><td colspan=2><hr width=100% size=1 noshade></td></tr>");
               html_page.append("</table></td></tr>");

               html_page.append("</td></tr>");

            }

            if(blk.getMasterBlock()==null && isSingleLayout() && rowset.countRows()>0)
            { 
               html_page.append("\n<input type=hidden id=\"__LU_MASTER_BLOCK\" name=\"__LU_MASTER_BLOCK\" value=\""+blk.getName()+"\">");
               html_page.append("\n<input type=hidden id=\"__LU_NAME\" name=\"__LU_NAME\" value=\""+blk.getLUName()+"\">");
               String key_values = "";
               String key_value_pairs = "";
               String key_name = "";
               String sorted_lu_keys = blk.getSortedLUKeys();
               if(!Str.isEmpty(sorted_lu_keys))
               {
                  StringTokenizer st = new StringTokenizer(sorted_lu_keys, "^");
                  while(st.hasMoreTokens())
                  {
                     key_name = st.nextToken();
                     key_values = key_values + rowset.getValue(key_name) + "^";
                     key_value_pairs = key_value_pairs + key_name + "=" + rowset.getValue(key_name) + "^";
                  }
               }
               if(key_values.endsWith("^"))
                  key_values = key_values.substring(0, key_values.length()-1);
               if(key_value_pairs.endsWith("^"))
                  key_value_pairs = key_value_pairs.substring(0,key_value_pairs.length()-1);
               
               html_page.append("\n<input type=hidden id=\"__LU_KEYS\" name=\"__LU_KEYS\" value=\""+key_values+"\">");
               html_page.append("\n<input type=hidden id=\"__LU_KEY_VALUE_PAIR\" name=\"__LU_KEY_VALUE_PAIR\" value=\""+key_value_pairs+"\">");

               mgr.startRWCInterfaceItem("notes");
                  mgr.addRWCInterfaceValue("lu_name", blk.getLUName());
                  mgr.addRWCInterfaceValue("lu_key_value_pairs", key_value_pairs);
                  mgr.addRWCInterfaceValue("view_name", blk.getView());
               mgr.endRWCInterfaceItem("notes");
            }
            
            // Generate and get block param filed collection ========================================
            if(rowset.countRows()>0)
            {
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

               String blockParamValueArray = "var "+getBlock().getParamValueArrayName()+" = new Array(\n";
               if(blockParamFileds!=null)
               {
                  blockParamValueArray += "new Array(";
                  for(int pf=0; pf<blockParamFileds.length; pf++)
                  {
                     String value = rowset.getDataRow().convertToClientString((ASPField)blockParamFileds[pf]);
                     blockParamValueArray += "\""+value+"\"";
                     if(pf!=blockParamFileds.length-1)
                        blockParamValueArray += ",";
                  }
                  blockParamValueArray += ")\n";
               }

               // append block param filed names and values onto html output
               blockParamValueArray += ");\n";
               if(blockParamFileds!=null && blockParamFileds.length>0)
               {
                  html_page.append("<script language=javascript>\n");
                  html_page.append("initCustomPopup(0);");
                  html_page.append(blockParamFieldArray);
                  html_page.append(blockParamValueArray);
                  html_page.append("</script>\n");
               }
            }
            // ====================================================================================== 

           if(groups.size() == 0)
           {
              // User has not used the defineGroup() function.
              // Draw the default group.

              /*
               tmp_columns = listToFields(dialog_order,true);
               column_count = tmp_columns.size();
               columns = new ASPField[column_count];
               //column_size = new int[column_count];

               for(int j=0;j<tmp_columns.size();j++)
               {
                   columns[j] = (ASPField)tmp_columns.elementAt(j);
                   //column_size[j] = columns[j].getSize();
               }
               */

               prepareProfileInfo(true);

               //drawColumns(columns,rowset);
               drawColumns(rowset);

               if(blk.hasFileUpload())
               {
                  int mode = getLayoutMode();
                  if (mode == NEW_LAYOUT || mode == EDIT_LAYOUT)
                     html_page.append(getASPPage().addFileUploadLink(blk));
               }


               html_page.append("</table>");

               html_page.append("</td></tr>");

               html_page.append("</table>");

               html_page.append(mgr.getEmptyImage(1,5),"<br>");
           }
           else
           {
              // User has used the defineGroup() function. Draw each group as a separate table.

               html_page.append("<tr><td");
               html_page.append(">");

               if(!(mgr.isExplorer()||mgr.isMozilla()))
                  html_page.append("<table cellspacing=0 cellpadding=0><tr><td>");

               boolean minimized;
               int no_of_groups = groups.size();

               /*
               tmp_columns = listToFields(dialog_order,true);
               column_count = tmp_columns.size();
               columns = new ASPField[column_count];
               //column_size = new int[column_count];

               for(int j=0;j<column_count;j++)
               {
                   columns[j] = (ASPField)tmp_columns.elementAt(j);
                   //column_size[j] = columns[j].getSize();
               }
               */
               prepareProfileInfo(true);

               for(i=0;i<no_of_groups;i++)
               {
                  group thisGroup = (group) groups.elementAt(i);

                  Vector tmp_columns = listToFields(thisGroup.fields,false);

                  if(isFindLayout())
                  {
                        boolean has_quariables = false;
                        int tmp_col_size = tmp_columns.size();
                        for(int j=0;j<tmp_col_size;j++)
                        {
                           ASPField tmpCol = (ASPField) tmp_columns.elementAt(j);
                           if (tmpCol.isQueryable())
                              has_quariables = true;
                        }
                        if (!has_quariables)
                           continue;
                  }
                  else{
                       boolean is_visible =false;
                       int tmp_col_size = tmp_columns.size();
                       for(int j=0;j<tmp_col_size;j++)
                       {
                          ASPField tmpCol = (ASPField) tmp_columns.elementAt(j);
                          if(!tmpCol.isHidden())
                             is_visible =true;
                       }
                       if(!is_visible)
                          continue;                       
                  }

                  group nextGroup = (group) groups.elementAt(i);
                  if(i+1<groups.size())
                     nextGroup = (group) groups.elementAt(i+1);
                  if(i==groups.size()-1)
                     last_group=true;
                  else
                     last_group=false;


                  String id = "group" + blk.getName() + i;
                  String target_id = "cnt" + blk.getName() + Integer.toString(i);


                  if(isMinimized(thisGroup.id) || (!thisGroup.showFirst && !isMaximized(thisGroup.id)))
                     minimized = true;
                  else
                     minimized = false;

                  if(thisGroup.showHeader)// && mgr.isExplorer())
                  {

                     html_page.append("\n\t<table width=100% cellspacing=0 cellpadding=0");
                     html_page.append(">\n\t\t<tr>\n\t\t\t");
                     html_page.append("<td ");
                     html_page.append("onmouseover=\"this.style.cursor='pointer'\" onmouseout=\"this.style.cursor='default'\" ");
                     html_page.append("onclick=\"javascript:");
                     html_page.append("toggleStyleDisplay('",target_id,"','",Integer.toString(thisGroup.id),"');");
                     html_page.append("setMinimizeGroupImage(document['", id,"'],'",target_id,"',false)\"");
                     html_page.append("\" ");
                     html_page.append("class=pageGroupCel noWrap>&nbsp;&nbsp;");
                     html_page.append("<img src=\"",image_location);
                     if(minimized)
                        html_page.append(getASPManager().getConfigParameter("COMMAND_BAR/ICONS/MAXIMIZE/NORMAL","max.gif"));
                     else
                        html_page.append(getASPManager().getConfigParameter("COMMAND_BAR/ICONS/MINIMIZE/NORMAL","max.gif"));
                     html_page.append("\" border=0 name=\"",id,"\">&nbsp;&nbsp;");
                     html_page.append("<font class=groupBoxTitleText>",thisGroup.title,"</font>");
                     html_page.append("\n\t\t\t</td>\n\t\t</tr>");
                     if (mgr.isExplorer()) // to keep some distance from bootm
                        html_page.append("<tr><td width=\"5\"></td></tr>");
                     html_page.append("\n\t</table>");

                     html_page.append("\n\t<INPUT TYPE='hidden' NAME='CBLOCK_SERVER_ID_"+ Integer.toString(thisGroup.id) +"' VALUE='"+ target_id +"'/>");
                  }


                  if(end_table)
                  {
                     html_page.append("\n<div ");
                     if(minimized)
                        html_page.append("style=\"display: none;\"");
                     html_page.append(" id=\"cnt",blk.getName(),Integer.toString(i),"\" >\n");
                     html_page.append("\n<table width=100% ");
                     if(show_border)
                        html_page.append("border=1 ");
                     else
                        html_page.append("border=0 ");

                     html_page.append("cellspacing=0 cellpadding=0");
                     html_page.append(">");
                  }

                  if(thisGroup.showTitle && !thisGroup.showHeader)
                     html_page.append("<tr><td><font class=\"normalTextLabel\">",thisGroup.title,"</font></td></tr>");

                  drawColumns(tmp_columns,rowset,thisGroup.columns);

                  if(!thisGroup.showHeader && !nextGroup.showHeader)
                     end_table=false;
                  else
                     end_table=true;

                  if(!end_table && !thisGroup.showHeader && !last_group)
                     html_page.append("<tr><td height=9 width=1>&nbsp;</td></tr>\n");

                  if(i!=groups.size())
                  {
                     html_page.append("<tr>");
                     html_page.append("<td><img src='",mgr.getASPConfig().getImagesLocation(),"6px_space.gif'></td>");
                  }
                  
                  
                  if(end_table || last_group){
                     html_page.append("\n</table>\n");
                     html_page.append("\n</div>");

                  }

               }


               if(!(mgr.isExplorer()||mgr.isMozilla()))
                  html_page.append("\n</td></tr></table>");

               html_page.append("\n\t</td></tr>");
               html_page.append("\n</table>\n");

               html_page.append("</td></tr>");

               html_page.append("</table>");
           }

           //page frame end
           html_page.append("</td>");
           
           // Added by Terry 20140906
           // Show frame space mark
           if (blk.hasCommandBar() && blk.getASPCommandBar().isCmdShowFrameSpace())
              html_page.append("<td>&nbsp;&nbsp;</td>");
           // Added end
           
           html_page.append("</tr></table>");

           ASPField[] allcolumns = blk.getFields();
           if(isFindLayout())
           {
               findHiddens(allcolumns);
               html_page.append("<input type=hidden name=__",blk.getName(),"_ROWSTATUS value=QueryMode__>");
               html_page.append("\n<span id=\"findAidBtn\" style=\"position:absolute;visibility:hidden;\"></span>\n");
               html_page.append(getFindAidTag());
           }
           else if (!isSingleLayout())
               html_page.append(blk.generateHiddenFields());

           if(isSingleLayout() && !Str.isEmpty(link_functions))
               html_page.append("\n<script language='JavaScript'>"+link_functions+"</script>\n");

           if(isEditLayout() || isNewLayout())
               generateMasterHiddens();

           //if(groups.size() == 0) //user profile only when groups are not defined
           /*{
               for(i=0;i<user_columns.length;i++)
               {
                  ASPField thisCol = user_columns[i];

                   if(user_force_hidden[i]==true)
                   {
                      thisCol.unsetHidden();
                   }
               }
           }*/

           return html_page.toString();
       }
       catch(Throwable any){error(any); return "";}
   }

   //drawColumns() with vector as parameter is a fallback solution for the profile problem with defineGroup()
   private void drawColumns( Vector columns,ASPRowSet rowset )
   {
      drawColumns(columns,rowset,0);
   }

   private void drawColumns( Vector columns,ASPRowSet rowset,int row_columns )
   {
       int columns_done = 0;
       int i;
       // Added by Terry 20120821
       // Change line color in SINGLE_LAYOUT
       boolean alt_bgcolor = false;
       // Added end
       cells_done = 0;
       active_column = 0;
       int col_limit = dialog_columns;
       if(row_columns > 0)
           col_limit = row_columns;
       String default_width = 90.0/(col_limit*2) + "%";

       //set default focus only if focus is not already set in page.
       if (!getASPManager().isInitialFocusSet())
       {
         for(int j=0;j<columns.size();j++)
         {
           ASPField tmpCol = (ASPField) columns.elementAt(j);
           if (!tmpCol.isHidden())
           {
             if ( (getLayoutMode()==FIND_LAYOUT && tmpCol.isQueryable()) ||
                  (getLayoutMode()==EDIT_LAYOUT && !tmpCol.isReadOnly()) ||
                  (getLayoutMode()==NEW_LAYOUT  && (tmpCol.isInsertable() || !tmpCol.isReadOnly())) )
             {
                getASPManager().setInitialFocus((tmpCol.getName()));
                break;
             }
           }
         }
       }

       try {
          if (getLayoutMode()==SINGLE_LAYOUT || getLayoutMode()==NEW_LAYOUT || getLayoutMode()==EDIT_LAYOUT)
             hideAllAdressFields();

          for(i=0;i<columns.size();i++)
          {
             ASPField thisCol = (ASPField) columns.elementAt(i);


             int index = findUserColumnIndex(thisCol,0);

                   if (!thisCol.isHidden())
                   {
                       String name = thisCol.getName();
                       if(!thisCol.isSimple())
                       {
                          if(((cells_done + 4) > (col_limit * 4)) || i==0)
                          {
                             // Added by Terry 20120821
                             // Change line color in SINGLE_LAYOUT
                             if (getLayoutMode() == SINGLE_LAYOUT || (getLayoutMode() == CUSTOM_LAYOUT && !editable))
                             {
                                html_page.append("\n\t<tr");
                                alt_bgcolor = !alt_bgcolor;
                                if(alt_bgcolor)
                                   html_page.append(" class=\"tableRowColor1\">\n");
                                else
                                   html_page.append(" class=\"tableRowColor2\">\n");
                        	  }
                        	  else
                                 html_page.append("\n\t<tr>\n");
                              // Added end
                              cells_done = 0;
                              active_column = 0;
                          }

                          active_column++;
                          columns_done++;

                       }

                       if((i+1) < columns.size())
                          nextField = (ASPField) columns.elementAt(i+1);
                       else
                          nextField = null;
                       
                       // Added by Terry 20130926
                       // Show sort select in findlayout
                       if ((i - 1) >= 0)
                          presField = (ASPField) columns.elementAt(i - 1);
                       else
                          presField = null;
                       // Added end

       //Boolean mu = new Boolean(thisCol.isSimple());
       //debug(thisCol.getName() + ": " + mu.toString());

                       // Modified by Terry 20131028
                       // Original:
                       // if ( thisCol.isQueryable() && getLayoutMode()==FIND_LAYOUT)
                       if (getLayoutMode()==FIND_LAYOUT)
                       // Modified end
                       //Find && Queryable.
                           {

                               if (thisCol.isQueryable() || !thisCol.isReadOnly()){
                                       writeColumn(rowset,name, thisCol,default_width);
                               }
                               else readColumn(rowset, name, thisCol,default_width);
                           }

                       else if (getLayoutMode()==NEW_LAYOUT) //New
                           {
                               if (thisCol.isInsertable() || !thisCol.isReadOnly()) writeColumn(rowset, name, thisCol,default_width);
                               else readColumn(rowset, name, thisCol,default_width);
                           }
                       else if (getLayoutMode()==EDIT_LAYOUT) // Edit
                           {
                               if (!thisCol.isReadOnly()) writeColumn(rowset, name, thisCol,default_width); //Read only, ljusare text.
                               else readColumn(rowset, name, thisCol,default_width);
                           }
                       else if (getLayoutMode()==SINGLE_LAYOUT) // Single-record, readonly
                           {
                                  textColumn(rowset,name,thisCol,default_width);
                           }
                       else if (getLayoutMode()==CUSTOM_LAYOUT) // Custom - readonly or editable
                           {                                    // determined by flag.

                               if (editable)
                               {
                                   if(!thisCol.isReadOnly())
                                      writeColumn(rowset, name, thisCol,default_width);
                                   else
                                      readColumn(rowset, name, thisCol,default_width);
                               }
                               else textColumn(rowset,name,thisCol,default_width);
                           }


                       }
                   else if (thisCol.isFirstAddressField())
                   {
                      if(((cells_done + 4) > (col_limit * 4)) || i==0)
                      {
                         // Added by Terry 20120821
                         // Change line color in SINGLE_LAYOUT
                         if (getLayoutMode() == SINGLE_LAYOUT || (getLayoutMode() == CUSTOM_LAYOUT && !editable))
                         {
                            html_page.append("\n\t<tr");
                            alt_bgcolor = !alt_bgcolor;
                            if(alt_bgcolor)
                               html_page.append(" class=\"tableRowColor1\">\n");
                            else
                               html_page.append(" class=\"tableRowColor2\">\n");
                         }
                     	 else
                            html_page.append("\n\t<tr>\n");
                         // Added end
                         cells_done = 0;
                         active_column = 0;
                      }

                      active_column++;
                      columns_done++;

                      if (getLayoutMode()==SINGLE_LAYOUT) // Single-record, readonly
                      {
                         addressDisplayColumn(rowset,SINGLE_LAYOUT,thisCol.getName());
                         cells_done = cells_done + 2;
                      }
                      else if (getLayoutMode()==EDIT_LAYOUT)
                      {
                         addressDisplayColumn(rowset,EDIT_LAYOUT,thisCol.getName());
                         cells_done = cells_done + 2;
                      }
                      else if (getLayoutMode()==NEW_LAYOUT)
                      {
                         addressDisplayColumn(rowset,NEW_LAYOUT,thisCol.getName());
                         cells_done = cells_done + 2;
                      }

                   }
          }

          if (rowset != null && rowset.countRows()>0 && isSingleLayout()){

              //History Stuff
              lu_name = getBlock().getLUName();
              lu_keys = getBlock().getLUKeys();

              StringBuffer sb = null;
              StringTokenizer keys = null;
              String key = "";

              if(!getASPManager().isEmpty(lu_name) && !getASPManager().isEmpty(lu_keys))
              {
                 ASPField fld = null;
                 String fld_value = "";
                 sb = new StringBuffer();

                 keys = new StringTokenizer(lu_keys, "^");

                 while(keys.hasMoreTokens())
                 {
                    key = keys.nextToken();
                    fld = getASPManager().getASPField(key);
                    fld_value = getASPManager().URLEncode(rowset.getClientValue(fld.getDbName()));
                    sb.append(key + "=" + fld_value + "^");
                 }

                 history_params = sb.toString();
              }

              Vector items = blk.getASPCommandBar().getAllCustomCommands();
              ASPCommandBarItem itm = null;
              StringTokenizer fields = null;
              String field = "";
              String link_params = "";
              String page_path = "";
              link_functions = "";
              int param_count = 0;
              //Bug 40902, start
              ASPField f = null;
              //Bug 40902, end
              for(int j=0;j<items.size();j++)
              {
                  itm = (ASPCommandBarItem)items.elementAt(j);
                  if(itm.getCommandId().startsWith("LINK_CMD"))
                  {
                      fields = new StringTokenizer(itm.getLinkFieldList(),",");
                      param_count = fields.countTokens()-1;
                      page_path = fields.nextToken();
                      while(fields.hasMoreTokens())
                      {
                          field = fields.nextToken();
                          //Bug 40902, start
                          f = getASPManager().getASPField(field);
                          if (rowset.countRows()>0 && isSingleLayout() && rowset.getValue(f.getDbName())!=null && !rowset.getValue(f.getDbName()).equals("null"))
                                link_params += field+"="+ getASPManager().URLEncode(rowset.getValue(f.getDbName()))+"&";
                          //Bug 40902,end
                      }
                      field = "";
                  }
                  String cmb_item_id = itm.getCommandId();
                  if(cmb_item_id.startsWith("LINK_CMD"))
                  {
                     if (cmb_item_id.startsWith("LINK_CMD_RWC"))
                        link_functions += "\nfunction "+itm.getCommandId()+"_"+getBlock().getName() + "(){navigateToRWC('" + page_path + "','" + link_params.substring(0,link_params.length()-1) + "');}\n";
                     else
                        link_functions += "\nfunction "+itm.getCommandId()+"_"+getBlock().getName()+"(){"+(itm.isLinkInNewWin()?"showNewBrowser":"location.replace")+"('"+page_path+((param_count > 0 && !getASPManager().isEmpty(link_params))?((page_path.indexOf("?")<0)?"?":"&")+"'+'"+link_params.substring(0,link_params.length()-1):"")+"');}\n";
                    link_params = "";
                  }
              }
          }
       }
       catch (Throwable e)
       {
          error(e);
       }

   }
   private void drawColumns(ASPRowSet rowset )
   {
      drawColumns(rowset,0);
   }

   private void drawColumns(ASPRowSet rowset,int row_columns )
   {
           int columns_done = 0;
           int i;
           // Added by Terry 20120821
           // Change line color in SINGLE_LAYOUT
           boolean alt_bgcolor = false;
           // Added end
           cells_done = 0;
           active_column = 0;
           int col_limit = dialog_columns;
           if(row_columns > 0)
               col_limit = row_columns;
           String default_width = 90.0/(col_limit*2) + "%";
           
           //set default focus only if focus is not already set in page.
           if (!getASPManager().isInitialFocusSet())
           {
             for(int j=0;j<user_columns.length;j++)
             {
               //if(user_column_size[j]==-1) continue;

               ASPField tmpCol = user_columns[j];
               if (!tmpCol.isHidden() && (!tmpCol.isImageField() || tmpCol.hasTemplate()))
               {
                 if ( (getLayoutMode()==FIND_LAYOUT && tmpCol.isQueryable()) ||
                      (getLayoutMode()==EDIT_LAYOUT && !tmpCol.isReadOnly()) ||
                      (getLayoutMode()==NEW_LAYOUT  && (tmpCol.isInsertable() || !tmpCol.isReadOnly())) )
                 {
                    getASPManager().setInitialFocus((tmpCol.getName()));
                    break;
                 }
               }
             }
           }

           try {
              if (getLayoutMode()==SINGLE_LAYOUT || getLayoutMode()==NEW_LAYOUT || getLayoutMode()==EDIT_LAYOUT)
                 hideAllAdressFields();

              for(i=0;i<user_columns.length;i++)
              {

                       ASPField thisCol = user_columns[i];
                       if(thisCol.isImageField() && !thisCol.hasTemplate()) continue;
                       if (!thisCol.isHidden())
                       {
                           String name = thisCol.getName();
                           if(!thisCol.isSimple())
                           {
                              if(((cells_done + 4) > (col_limit * 4)) || i==0)
                              {
                                 // Added by Terry 20120821
                                 // Change line color in SINGLE_LAYOUT
                                 if (getLayoutMode() == SINGLE_LAYOUT || (getLayoutMode() == CUSTOM_LAYOUT && !editable))
                                 {
                                    html_page.append("\n\t<tr");
                                    alt_bgcolor = !alt_bgcolor;
                                    if(alt_bgcolor)
                                       html_page.append(" class=\"tableRowColor1\">\n");
                                    else
                                       html_page.append(" class=\"tableRowColor2\">\n");
                             	 }
                             	 else
                                    html_page.append("\n\t<tr>\n");
                                 // Added end
                                 cells_done = 0;
                                 active_column = 0;
                              }

                              active_column++;
                              columns_done++;

                           }

                           if((i+1) < user_columns.length)
                              nextField = user_columns[i+1];
                           else
                              nextField = null;
                           
                           // Added by Terry 20130926
                           // Show sort select in findlayout
                           if ((i - 1) >= 0)
                              presField = user_columns[i - 1];
                           else
                              presField = null;
                           // Added end

   //Boolean mu = new Boolean(thisCol.isSimple());
   //debug(thisCol.getName() + ": " + mu.toString());

                           // Modified by Terry 20131028
                           // Original:
                           // if ( thisCol.isQueryable() && getLayoutMode()==FIND_LAYOUT)
                           if (getLayoutMode()==FIND_LAYOUT)
                           // Modified end
                               {

                                   if (thisCol.isQueryable() || !thisCol.isReadOnly()){
                                           writeColumn(rowset,name, thisCol,default_width);
                                   }
                                   else readColumn(rowset, name, thisCol,default_width);
                               }

                           else if (getLayoutMode()==NEW_LAYOUT) //New
                               {
                                   if (thisCol.isInsertable() || !thisCol.isReadOnly())
                                      writeColumn(rowset, name, thisCol,default_width);
                                   else
                                      readColumn(rowset, name, thisCol,default_width);
                               }
                           else if (getLayoutMode()==EDIT_LAYOUT) // Edit
                               {
                                   if (!thisCol.isReadOnly())
                                      writeColumn(rowset, name, thisCol,default_width); //Read only, ljusare text.
                                   else
                                      readColumn(rowset, name, thisCol,default_width);
                               }
                           else if (getLayoutMode()==SINGLE_LAYOUT) // Single-record, readonly
                               {
                                      textColumn(rowset,name,thisCol,default_width);
                               }
                           else if (getLayoutMode()==CUSTOM_LAYOUT) // Custom - readonly or editable
                               {                                    // determined by flag.

                                   if (editable)
                                   {
                                       if(!thisCol.isReadOnly())
                                          writeColumn(rowset, name, thisCol,default_width);
                                       else
                                          readColumn(rowset, name, thisCol,default_width);
                                   }
                                   else textColumn(rowset,name,thisCol,default_width);
                               }


                           }
                       else if (thisCol.isFirstAddressField())
                       {
                          if(((cells_done + 4) > (col_limit * 4)) || i==0)
                          {
                             // Added by Terry 20120821
                             // Change line color in SINGLE_LAYOUT
                             if (getLayoutMode() == SINGLE_LAYOUT || (getLayoutMode() == CUSTOM_LAYOUT && !editable))
                             {
                                html_page.append("\n\t<tr");
                                alt_bgcolor = !alt_bgcolor;
                                if(alt_bgcolor)
                                   html_page.append(" class=\"tableRowColor1\">\n");
                                else
                                   html_page.append(" class=\"tableRowColor2\">\n");
                          	 }
                          	 else
                                html_page.append("\n\t<tr>\n");
                             // Added end
                             cells_done = 0;
                             active_column = 0;
                          }

                          active_column++;
                          columns_done++;

                          if (getLayoutMode()==SINGLE_LAYOUT) // Single-record, readonly
                          {
                             addressDisplayColumn(rowset,SINGLE_LAYOUT,thisCol.getName());
                             cells_done = cells_done + 2;
                          }
                          else if (getLayoutMode()==EDIT_LAYOUT)
                          {
                             addressDisplayColumn(rowset,EDIT_LAYOUT,thisCol.getName());
                             cells_done = cells_done + 2;
                          }
                          else if (getLayoutMode()==NEW_LAYOUT)
                          {
                             addressDisplayColumn(rowset,NEW_LAYOUT,thisCol.getName());
                             cells_done = cells_done + 2;
                          }

                       }
                   }

              if (rowset != null && rowset.countRows()>0 && isSingleLayout()){

                  //History Stuff
                  lu_name = getBlock().getLUName();
                  lu_keys = getBlock().getLUKeys();

                  StringBuffer sb = null;
                  StringTokenizer keys = null;
                  String key = "";

                  if(!getASPManager().isEmpty(lu_name) && !getASPManager().isEmpty(lu_keys))
                  {
                     ASPField fld = null;
                     String fld_value = "";
                     sb = new StringBuffer();

                     keys = new StringTokenizer(lu_keys, "^");

                     while(keys.hasMoreTokens())
                     {
                        key = keys.nextToken();
                        fld = getASPManager().getASPField(key);
                        fld_value = getASPManager().URLEncode(rowset.getClientValue(fld.getDbName()));
                        sb.append(key + "=" + fld_value + "^");
                     }

                     history_params = sb.toString();
                  }

                  Vector items = blk.getASPCommandBar().getAllCustomCommands();
                  ASPCommandBarItem itm = null;
                  StringTokenizer fields = null;
                  String field = "";
                  String link_params = "";
                  String page_path = "";
                  link_functions = "";
                  int param_count = 0;
                  //Bug 40902, start
                  ASPField f = null;
                  //Bug 40902, end
                  for(int j=0;j<items.size();j++)
                  {
                      itm = (ASPCommandBarItem)items.elementAt(j);
                      if(itm.getCommandId().startsWith("LINK_CMD"))
                      {
                          fields = new StringTokenizer(itm.getLinkFieldList(),",");
                          param_count = fields.countTokens()-1;
                          page_path = fields.nextToken();
                          while(fields.hasMoreTokens())
                          {
                              field = fields.nextToken();
                              //Bug 40902, start
                              f = getASPManager().getASPField(field);
                              if (rowset.countRows()>0 && isSingleLayout() && rowset.getValue(f.getDbName())!=null && !rowset.getValue(f.getDbName()).equals("null"))
                                     link_params += field+"="+ getASPManager().URLEncode(rowset.getValue(f.getDbName()))+"&";
                              //Bug 40902,end
                          }
                          field = "";
                      }
                      String cmb_item_id = itm.getCommandId();
                      if(cmb_item_id.startsWith("LINK_CMD"))
                      {
                          if (cmb_item_id.startsWith("LINK_CMD_RWC"))
                             link_functions += "\nfunction "+itm.getCommandId()+"_"+getBlock().getName() + "(){navigateToRWC('" + page_path + "','" + link_params.substring(0,link_params.length()-1) + "');}\n";
                          else
                             link_functions += "\nfunction "+itm.getCommandId()+"_"+getBlock().getName()+"(){"+(itm.isLinkInNewWin()?"showNewBrowser":"location.replace")+"('"+page_path+((param_count > 0 && !getASPManager().isEmpty(link_params))?((page_path.indexOf("?")<0)?"?":"&")+"'+'"+link_params.substring(0,link_params.length()-1):"")+"');}\n";
                        link_params = "";
                      }
                  }
              }
           }
           catch (Throwable e)
               {
                   error(e);
               }

   }

   private String getCurrentRowValue(ASPRowSet rowset, String name, ASPField field)
   {
      return getCurrentRowValue(rowset, name, field, false, false);
   }

   private String getCurrentRowValue(ASPRowSet rowset, String name, ASPField field, boolean encode_additional_spaces, boolean encode_url)
   {
        String dbname = field.getDbName();
        String value = "";
        ASPManager mgr = blk.getASPManager();
        if (getLayoutMode()==FIND_LAYOUT)
        {
            if(getASPPage().getASPLov() != null)
            {
               if(mgr.readValue(name) != null)
                  value = mgr.readValue(name);
            } else
            {
               String key = mgr.readValue("__COMMAND");
               if(key != null)
               {
                  int pos = key.indexOf('.');
                  String id = key.substring(pos+1);

                  if( ! id.equals(ASPCommandBar.FIND))
                  {
                     if(mgr.readValue(name) != null)
                        value = mgr.readValue(name);
                  }
               }
            }
        }
        else
        {
           if (rowset.countRows()>0)
               if (rowset.getValue(dbname)!=null && !rowset.getValue(dbname).equals("null"))
               {
                  try {
                     value = field.convertToClientString(rowset.getDataRow(rowset.getCurrentRowNo()).getValue(field));
                  } catch (ItemNotFoundException ex) {
                     value = "";
                  } catch (FndException ex) {
                     value = "";
                  }
               }
        }

         if(Str.isEmpty(value) && isSingleLayout())
            return "&nbsp;";
         else
            if(Str.isEmpty(value))
               return "";
            else if (encode_url)
               return value;
            else
               return mgr.HTMLEncode(value,encode_additional_spaces);
    }

   private void addLOV(ASPField field) throws FndException
   {
     try{

         if (field.hasLOV() && field.isLOVActive())
             field.appendLOVTag(html_page,-1,false);
     }
     catch(Throwable e)
         {error(e);}
   }

   private void addIidLOV(ASPField field) throws FndException
   {
      try
      {
         field.appendIidLOVTag(html_page,-1,false);
      }
      catch(Throwable e)
         {error(e);}
   }

   private String appendSpaceBetween()
   {
      cells_done++;
      return "<td width=" + spaceBetween + ">&nbsp</td>";
   }

   private String appendSpaceAfter()
   {
      cells_done++;
      return "<td width=" + spaceAfter + ">&nbsp</td>";
   }
   
   // Added by Terry 20130926
   // Show sort select in findlayout
   private String appendSortAndSpaceAfter(ASPField field)
   {
      ASPManager mgr = getASPManager();
      cells_done++;
      AutoString sort_string = new AutoString();
      sort_string.append("<td width=" + spaceAfter + ">");
      if(isFindLayout())
      {
         String name = "";
         if (!field.isSimple() || presField == null)
            name = "__SORT_" + field.getName();
         else if (presField != null)
            name = "__SORT_" + presField.getName();
         
         if (!Str.isEmpty(name))
         {
            String value = mgr.readValue(name);
            String asc_selected = "";
            String desc_selected = "";
            if ("ASC".equals(value))
               asc_selected = " selected ";
            else if ("DESC".equals(value))
               desc_selected = " selected ";
            
            sort_string.append("<select class='selectbox' name=" + name + ">");
            sort_string.append("<option value=''></option>");
            sort_string.append("<option value='" + "ASC" + "'" + asc_selected + ">"+mgr.translateJavaText("FNDLAYSORTASC: Ascending")+"</option>");
            sort_string.append("<option value='" + "DESC" + "'" + desc_selected + ">"+mgr.translateJavaText("FNDLAYSORTDESC: Descending")+"</option>");
            sort_string.append("</select>");
         }
      }
      sort_string.append("&nbsp</td>");
      return sort_string.toString();
   }
   // Added end

   private String getLabelWidth()
   {
      try
      {
         // Note: inefficient
         for(int i=0;i<widths.size();i++)
            if(((columnWidth)widths.elementAt(i)).column==active_column)
               return "width=" + ((columnWidth)widths.elementAt(i)).label_width;
         return "";
      }
      catch (Throwable e) {error(e); return "";}
   }

   private String getDataWidth()
   {
      try
      {
         // Note: inefficient
         for(int i=0;i<widths.size();i++)
            if(((columnWidth)widths.elementAt(i)).column==active_column)
               return "width=" + ((columnWidth)widths.elementAt(i)).data_width;
         return "";
      }
      catch (Throwable e) {error(e); return "";}
   }

   private String getStyle(ASPField field)
   {
      if(field.isHilite())
         return "hiliteTextValue";
      else if(field.isBold())
         return "boldTextValue";
      else
         return "normalTextValue";
   }

   private void textColumn(ASPRowSet rowset, String name, ASPField field, String width) throws FndException
   {

      String urllink = field.getHyperlinkURL();
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      String fieldurl = field.getHyperlinkFieldURL();
      // Added end
      String hyperlink_presobj = field.getHyperlinkedPresObjectId();
      ASPManager mgr = getASPManager();
      String lable_width=getLabelWidth();
      String data_width=getDataWidth();
      if (hasDefinedGroups() && "".equals(lable_width) && "".equals(data_width))
         lable_width = data_width = "width="+ width;
      
      // Added by Jack Zhang,20100916 23:44:41
      if (field.getLabelSpan() > 1)
         lable_width = "";
      if (field.getDataSpan() > 1)
         data_width = "";
      // Added end
      
      boolean hyperlink_ok;

      if (!Str.isEmpty(hyperlink_presobj))
        hyperlink_ok = getASPPage().isObjectAccessible(hyperlink_presobj);  // 'secure' hyperlink hence check security
      else
        hyperlink_ok = true;   //not a 'secure' hyperlink no need for security check


      try{
         if(!field.isSimple())
         {
            html_page.append("\n\t\t<td VALIGN=TOP ",lable_width);
            html_page.append(" nowrap ",writeLabelSpan(field));
            String usageId = field.getUsageID();
            html_page.append(" OnClick=\"showHelpTag('",usageId,"')\">");
            html_page.append("<font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+usageId+"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=\"normalTextLabel\" face=\"Arial\">",field.getLabel(),"</td>");
            html_page.append(appendSpaceBetween());
         }
         else
            html_page.append(getASPManager().getEmptyImage(10,1));

         //if(!field.isSimple())
         //   html_page.append("\n\t\t<td valign=top ",data_width," ",writeDataSpan(field),">");
         
         if(!field.isSimple())
         {
            // Modified by Terry 20120821
            // Set Background color to field.
            // Original: html_page.append("\n\t\t<td valign=top ");
            html_page.append("\n\t\t<td valign=top " + field.getBgColorTag());
            // Modified end
            if(blk.isAlignNumbersToRight()){
               if(field.getAlignment().length()>0)
                  html_page.append(" align="+field.getAlignment()+" ");
            }
            html_page.append(data_width," ",writeDataSpan(field),">");
        }

         html_page.append("<font class=\"",getStyle(field),"\" "+getTooltip(field,"TEXT_LABEL")+">");

         if(field.isCheckBox())
         {
            appendReadCheckbox(html_page,rowset,field,name);
            // Modified by Terry 20121218
            // Can use ASPField to set Hyperlink URL.
            // Origianl:
            // if(urllink != null && hyperlink_ok)
            if(urllink != null && hyperlink_ok || !Str.isEmpty(fieldurl)) // Modified end
            {
               html_page.append("&nbsp;<a href=\"",getURL(rowset,field),"\"><img border=0 src=\"");
               html_page.append(mgr.getASPConfig().getImagesLocation(),mgr.getConfigParameter("TABLE/COLUMN/HYPERLINK/IMAGE/NAME","table_hyperlink.gif"),"\"></a>");
            }
         }
         else
         {
            // Modified by Terry 20121218
            // Can use ASPField to set Hyperlink URL.
            // Origianl:
            // if(urllink != null && hyperlink_ok)
            if(urllink != null && hyperlink_ok || !Str.isEmpty(fieldurl)) // Modified end
            {
               html_page.append("<a class=\"",getStyle(field),"\" href=\"");
               if("POST".equals(field.getHyperlinkMethod()) )
                  html_page.append("javascript:submitForm('",getURL(rowset,field),"');\">");
               else
                  html_page.append( getURL(rowset,field), "\">");
            }
            String val = getCurrentRowValue(rowset, name, field,true,false);
            if (field.isAccurateFld())
            {
               String dbname = field.getDbName();
               if (rowset.countRows()>0)
                  if (rowset.getValue(dbname)!=null && !rowset.getValue(dbname).equals("null"))
                  {
                     val = field.getAccurateClientFormatter().format(new Double(rowset.getValue(dbname)));
                  }
            }
            else if(field.isImageField())
               val = getASPPage().getImageFieldTag(field,rowset,rowset.getCurrentRowNo());
            
            // Added by Terry 20130326
            // Set Font color to field.
            if (field.hasFontColor())
               val = field.getFontColorTag(val);
            if (field.hasFontProperty())
               val = field.getFontPropertyTag(val);
            // Added end
            
            html_page.append(Str.replace(val,"\n","<br>"));
            // Modified by Terry 20121218
            // Can use ASPField to set Hyperlink URL.
            // Origianl:
            // if(urllink != null && hyperlink_ok)
            if(urllink != null && hyperlink_ok || !Str.isEmpty(fieldurl)) // Modified end
               html_page.append("</a>");
         }

         html_page.append("</font>");

         if(nextField == null || !nextField.isSimple())
            html_page.append("</td>",appendSpaceAfter());

      }
      catch (Throwable e) {error(e);}
   }

   private void appendReadCheckbox( AutoString html_page, ASPRowSet rowset, ASPField field, String name )
   {
       ASPConfig cfg = getASPPage().getASPConfig();
       if(getCurrentRowValue(rowset, name, field).equals(field.getCheckedValue()))
       {
          // Added by Terry 20140516
          // Added checked checkbox pic
          html_page.append("<img src=\"" + getASPManager().getASPConfig().getImagesLocation() + "checkbox_checked.gif\"/>");
          // Added end
          html_page.append("<input type='checkbox' disabled checked class='readOnlyCheckbox'>");
       }
       else
       {
          // Added by Terry 20140516
          // Added unchecked checkbox pic
          html_page.append("<img src=\"" + getASPManager().getASPConfig().getImagesLocation() + "checkbox_unchecked.gif\"/>");
          // Added end
          html_page.append("<input type='checkbox' disabled class='readOnlyCheckbox'>");
       }
   }

   private String getURL(ASPRowSet rowset, ASPField field) throws Exception
   {
      String urllink = "";
      try
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
         // Modified end
         
         int param_count = (params != null) ? params.length : 0;
         String name;

         for (int i = 0; i < param_count; i++)
         {
            if (i > 0)
               urllink += "&";
            // Modified by Terry 20131028
            // Can accept fields aliases
            // Original:
            // name = params[i].getName();
            // urllink += ("" + name + "=" + getASPManager().URLEncode(getCurrentRowValue(rowset, name, params[i], false, true)));
            name = params_aliases[i];
            urllink += ("" + name + "=" + getASPManager().URLEncode(getCurrentRowValue(rowset, params[i].getName(), params[i], false, true)));
            // Modified end
         }
      }
      catch (Exception e)
      {
         throw new FndException(e);
      }

      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      String url = "";
      String field_url = field.getHyperlinkFieldURL();
      if (!Str.isEmpty(field_url))
         url = getCurrentRowValue(rowset, field_url, getASPPage().getASPField(field_url), false, true);

      if (Str.isEmpty(url) || "&nbsp;".equals(url))
         url = field.getHyperlinkURL();
      // Added end
      
      if (!Str.isEmpty(urllink))
      {
         // Modified by Terry 20121218
         // Can use ASPField to set Hyperlink URL.
         // Origianl:
         // if (field.getHyperlinkURL().indexOf('?') < 0)
         if (url.indexOf('?') < 0) // Modified end
            urllink = "?" + urllink;
         else
            urllink = "&" + urllink;
      }

      // Modified by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      // Origianl:
      // if (field.isHyperlinkInNewBrowser())
      //    urllink = "javascript:showNewBrowser('" + field.getHyperlinkURL() + urllink + "');";
      // else
      //    urllink = field.getHyperlinkURL() + urllink;
      if (field.isHyperlinkInNewBrowser())
         urllink = "javascript:showNewBrowser('" + url + urllink + "');";
      else
         urllink = url + urllink;
      // Modified end
      return urllink;
   }

   private void readColumn(ASPRowSet rowset, String name, ASPField field, String width) throws FndException
   {
        ASPManager mgr = getASPManager();
        String lable_width=getLabelWidth();
        String data_width=getDataWidth();
        if (hasDefinedGroups() && "".equals(lable_width) && "".equals(data_width))
           lable_width = data_width = "width="+ width;
        
        // Added by Jack Zhang,20100916 23:33:34
        if (field.getLabelSpan() > 1)
           lable_width = "";
        if (field.getDataSpan() > 1)
           data_width = "";
        // Added end

        if(!field.isSimple())
        {
           String usageId = field.getUsageID();
           // Modified by Terry 20140418
           // Change layout in editlayout and findlayout
           // Original:
           // html_page.append("\n\t\t<td ",lable_width);
           if (hasDefinedGroups())
           {
              String field_lable_width = getLabelWidth();
              if (!mgr.isEmpty(field_lable_width))
                 html_page.append("\n\t\t<td ",field_lable_width," nowrap ");
              else
                 html_page.append("\n\t\t<td width=120 ");
           }
           else
              html_page.append("\n\t\t<td ",lable_width);
           // Modified end
           html_page.append(" nowrap ",writeLabelSpan(field)," onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+usageId+"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=\"normalTextLabel\"");
           html_page.append(" valign=top");
           html_page.append(" OnClick=\"showHelpTag('",usageId,"')\">");
           html_page.append(mgr.getEmptyImage(1,4),"<br>");
           html_page.append(field.getLabel(),"</td>");
           html_page.append(appendSpaceBetween());
           
           // Modified by Terry 20140418
           // Change layout in editlayout and findlayout
           // Original:
           // html_page.append("\n\t\t<td valign=top ",data_width," ",writeDataSpan(field),">");
           if (hasDefinedGroups())
           {
              if (field.getDataSpan() > 1)
                 html_page.append("\n\t\t<td valign=top ",data_width," ",writeDataSpan(field),">");
              else
              {
                 String field_width = getDataWidth();
                 if (!mgr.isEmpty(field_width))
                    html_page.append("\n\t\t<td valign=top ",field_width," ",writeDataSpan(field),">");
                 else
                    html_page.append("\n\t\t<td valign=top width=325 ",writeDataSpan(field),">");
              }
           }
           else
              html_page.append("\n\t\t<td valign=top ",data_width," ",writeDataSpan(field),">");
           // Modified end
        }
        else
        {
           // Modified by Terry 20140823
           // Conditional Mandatory property of field
           // Original:
           // html_page.append(mgr.getEmptyImage(10,1));
           if (presField != null && presField.hasConditionalMandatory() && !isFindLayout())
              html_page.append("");
           else
              html_page.append(mgr.getEmptyImage(10,1));
           // Modified end
        }
           

        try {
            if(field.isCheckBox())
            {
               ASPConfig cfg = getASPPage().getASPConfig();
               String row_value = getCurrentRowValue(rowset, name, field);
               // Added by Terry 20140516
               // Added checked or unchecked checkbox pic
               if (row_value.equals(field.getCheckedValue()))
                  html_page.append("<img src=\"" + getASPManager().getASPConfig().getImagesLocation() + "checkbox_checked.gif\"/>");
               else
                  html_page.append("<img src=\"" + getASPManager().getASPConfig().getImagesLocation() + "checkbox_unchecked.gif\"/>");
               // Added end
               html_page.append("<input type=checkbox name=\"",name,"_CHECK\" value=\"",field.getCheckedValue(),"\" disabled class=readOnlyCheckbox ");
               html_page.append(row_value.equals(field.getCheckedValue())?"checked":"unchecked");
               html_page.append("><input type=hidden name=\"",name,"\" value=\"");
               html_page.append(row_value,"\"");

            }
            else if(field.getHeight()>1)
            {
               html_page.append("<textarea tabindex=-1 readonly class='editableTextArea' name=\"" + name +
                               "\"  cols=" + ((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6)) + " rows=" + field.getHeight()+" wrap=virtual");
               if (!mgr.isExplorer())
                  html_page.append(" OnChange=\"document.form."+name+".value=document.form."+name+".defaultValue\"");
               html_page.append(">"+getCurrentRowValue(rowset,name,field));
               html_page.append("</textarea");
            }
            else
            {
               String val = getCurrentRowValue(rowset,name, field);
               if (field.isAccurateFld())
               {
                  html_page.append("<input type=hidden name="+name+" value=\""+val+"\" >");
                  
                  String dbname = field.getDbName();
                  if (rowset.countRows()>0)
                     if (rowset.getValue(dbname)!=null && !rowset.getValue(dbname).equals("null"))
                     {
                        val = field.getAccurateClientFormatter().format(new Double(rowset.getValue(dbname)));
                     }
                  
               }
               html_page.append("<input "+getTooltip(field,"TEXT_INPUT")+" class='readOnlyTextField' type=text size=\""+((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6))+"\" name=\"",name,"\" value=\"");
               html_page.append(val+"\" readonly tabindex=-1");
               if(blk.isAlignNumbersToRight())
               {
                   if(field.getAlignment().length()>0)
                       html_page.append(" style=\"text-align="+field.getAlignment()+"\" ");
               }
               if (!mgr.isExplorer())
                  html_page.append(" OnChange=\"document.form."+name+".value=document.form."+name+".defaultValue\"");
            }

            // Added by Terry 20120821
            // Set SV Tag
            if (field.isForceSV())
               addSV(field);
            // Added end
            
            html_page.append(">");

            if(nextField == null || !nextField.isSimple())
            {
               html_page.append("</td>");
               // Modified by Terry 20130926
               // Show sort select in findlayout
               // Original:
               // html_page.append(appendSpaceAfter());
               html_page.append(appendSortAndSpaceAfter(field));
               // Modified end
            }

        }
        catch (Throwable e) {error(e);}
    }

    private void writeColumn(ASPRowSet rowset, String name, ASPField field, String width) throws FndException
    {
        ASPManager mgr = getASPManager();
        String lable_width=getLabelWidth();
        if (hasDefinedGroups() && "".equals(lable_width))
           lable_width = "width="+ width;
       
        if(!field.isSimple())
        {
           // Modified by Terry 20140418
           // Change layout in editlayout and findlayout
           // Original:
           // html_page.append("\n\t\t<td ",lable_width," nowrap ");
           if (hasDefinedGroups())
           {
              String field_lable_width = getLabelWidth();
              if (!mgr.isEmpty(field_lable_width))
                 html_page.append("\n\t\t<td ",field_lable_width," nowrap ");
              else
                 html_page.append("\n\t\t<td width=120 nowrap ");
           }
           else
              html_page.append("\n\t\t<td ",lable_width," nowrap ");
           // Modified end
           html_page.append(writeLabelSpan(field));
           html_page.append(" valign=top");
           String usageId = field.getUsageID();
           html_page.append(" OnClick=\"showHelpTag('",usageId,"')\">");
           html_page.append(mgr.getEmptyImage(1,4),"<br>");
           html_page.append("<font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+usageId+"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=\"normalTextLabel\">",field.getLabel(),"</font></td>");
           html_page.append(appendSpaceBetween());
        }
        else
        {
           // Modified by Terry 20140823
           // Conditional Mandatory property of field
           // Original:
           // html_page.append(mgr.getEmptyImage(10,1));
           if (presField != null && presField.hasConditionalMandatory() && !isFindLayout())
              html_page.append("");
           else
              html_page.append(mgr.getEmptyImage(10,1));
           // Modified end
        }

/*
        if(field.isRadioButtons() || (!field.isSelectBox() && field.getHeight() > 1))  // textareas and radiobuttons
            html_page.append("valign=\"top\" ");
*/

        try {
            if (beginCell(field, name, rowset))
                {
                    //html_page.append(" name=\"",name,"\" ");
                    if(isFindLayout())
                        {
                            html_page.append(" OnFocus =\"showSearchSupportButton('"+field.getName()+"','"+field.getSize()+"','"+field.getHeight()+"')\"");
                            html_page.append(" OnBlur =\"hideSearchSupportButton('"+field.getName()+"')\"");
                            if (field.isDateTime() || field.isTime())
                                html_page.append(" OnChange=\"formatDate_(getField_('"+field.getName()+"',-1),'"+field.getMask()+"','"+getBlock().getName()+"')\"");
                            else if(field.isUpperCase())
                                html_page.append(" OnChange=\"toUpper_('",field.getName(),"',-1)\"");
                        }
                    else
                        field.appendValidationTag(html_page,-1, false);
                    
                    if (field.getHeight() > 1)
                       html_page.append(">\r\n",getCurrentRowValue(rowset,name, field)+"</textarea");
                    
                    if (field.hasLOV() && field.isLOVActive()) // Ctrl+k functionality
                    {
                        html_page.append(" onkeydown=\"if(catchKeyCombination(event)){ preLov" + field.getJavaScriptName() + "(-1); return false;}\" ");
                    }
                    
                    if(isFindLayout())
                    {
                       html_page.append("><img onmouseover=\"SearchSupportHideButton=false\" onmouseout=\"SearchSupportHideButton=true\" id=\"findAidBtn_"+field.getName()+"\" src=\""+mgr.getASPConfig().getImagesLocation()+"table_empty_image.gif\" ");
                    }
                    
                    // Added by Terry 20120821
                    // Add SV Tag
                    addSV(field);
                    // Added end
                    addLOV(field);
                    addCalendar(field);
                    html_page.append(">");
                    //endCell(field, name, rowset);
            }
                    endCell(field, name, rowset);
        }
        catch (Throwable e) {error(e);}
    }


   private boolean beginCell(ASPField field, String name, ASPRowSet rowset) throws  FndException
   {
      int i;
      ASPManager mgr = getASPManager();
      boolean query_hints_on = mgr.getASPConfig().isQueryHintsOn();

      // Added by Terry 20120109
      // Control sv field, can not input value.
      String field_control_sv;
      if (field.hasShortcutValue())
         field_control_sv = " style=\"ime-mode:disabled\" onkeypress=\"event.returnValue = false;\" onpaste=\"return false;\" ";
      else
      {
         if (field.getHeight() > 1)
            field_control_sv = " onkeypress=\"return checkMaxLength(this);\" ";
         else
            field_control_sv = "";
      }
      // Added end
      
      // Added by Terry 20131108 Fix client func bug
      boolean client_function_keypress = false;
      if (field.hasClientFunc() && field.getClientFunc().indexOf("onkeypress") != -1)
         client_function_keypress = true;
      // Added end
      
      try
      {
         if(!field.isSimple())
            // Modified by Terry 20140418
            // Change layout in editlayout and findlayout
            // Original:
            // html_page.append("<td valign=top ",getDataWidth()," nowrap ",writeDataSpan(field),"><font class=\"normalTextLabel\">");
            if (hasDefinedGroups())
            {
               if (field.getDataSpan() > 1)
                  html_page.append("<td valign=top ",getDataWidth()," nowrap ",writeDataSpan(field),"><font class=\"normalTextLabel\">");
               else
               {
                  String field_width = getDataWidth();
                  if (!mgr.isEmpty(field_width))
                     html_page.append("<td valign=top ",field_width," nowrap ",writeDataSpan(field),"><font class=\"normalTextLabel\">");
                  else
                     html_page.append("<td valign=top width=325 nowrap ",writeDataSpan(field),"><font class=\"normalTextLabel\">");
               }
            }
            else
               html_page.append("<td valign=top ",getDataWidth()," nowrap ",writeDataSpan(field),"><font class=\"normalTextLabel\">");
            // Modified end
         if(isFindLayout() && query_hints_on && !field.isPasswordField())
         {
            if (blk.isSuitableForQuerying(field.getDbName()))
               html_page.append("<img src=\"",mgr.getASPConfig().getImagesLocation(),"item_indexed.gif\" width=16 height=16 alt=\""+mgr.translate("FNDLAYTOOLTIPSUITABLE: This field is suitable for searching")+"\" title=\""+mgr.translate("FNDLAYTOOLTIPSUITABLE: This field is suitable for searching")+"\" >");
            else if (blk.isNotSuitableForQuerying(field.getDbName()) || field.hasSetFunction())
               html_page.append("<img src=\"",mgr.getASPConfig().getImagesLocation(),"item_function_bound.gif\" width=16 height=16 alt=\""+mgr.translate("FNDLAYTOOLTIPNOTSUITABLE: This field is not suitable for searching")+"\" title=\""+mgr.translate("FNDLAYTOOLTIPNOTSUITABLE: This field is not suitable for searching")+"\" >");
            else
               html_page.append("<img src=\"",mgr.getASPConfig().getImagesLocation(),"empty.gif\" width=16 height=16>");
         }
         

         if(field.isSelectBox())
         {
            if(isFindLayout() && field.isFindModeIidToLov())
            {
               html_page.append("<input "+getTooltip(field,"TEXT_INPUT")+" class='editableTextField' type=text size="+((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6)) +
                                " value=\""+getCurrentRowValue(rowset,name,field)+"\" ");
               html_page.append(" name=\"",name,"\" ");
               try
               {
                  addIidLOV(field);
                  // Added by Terry 20120821
                  // Add client functions
                  addClientFunc(field);
                  // Added end
               }
               catch(Throwable e)
                  {error(e);
               }
            }
            else
            {
               html_page.append("<select class='selectbox' size=1 ");
               html_page.append(" name=\"",name,"\" ");
               html_page.append(" ");
               field.appendValidationTag(html_page,-1, false);
               // Added by Terry 20120821
               // Add client functions
               addClientFunc(field);
               // Added end
               html_page.append(">");
               getASPPage().getASPHTMLFormatter().populateListBox(html_page,field.getIidClientValues(),getCurrentRowValue(rowset, name,field), false, false);
               html_page.append("</select>");
            }

            return false;

         }
         else if(field.isCheckBox())
         {
            if(isFindLayout())
            {
                html_page.append("<select class='selectbox' name="+field.getName()+">");
                if (getCurrentRowValue(rowset, name, field).equals(field.getCheckedValue()))
                    {
                        html_page.append("<option value='"+field.getCheckedValue()+"'>"+mgr.translateJavaText("FNDLAYCHE: Checked")+"</option>");
                        html_page.append("<option value='"+field.getUncheckedValue()+"'>"+mgr.translateJavaText("FNDLAYUNCHE: Unchecked")+"</option>");
                        html_page.append("<option value=''></option>");
                    }
                else if (getCurrentRowValue(rowset, name, field).equals(field.getUncheckedValue()))
                    {
                        html_page.append("<option value='"+field.getUncheckedValue()+"'>"+mgr.translateJavaText("FNDLAYUNCHE: Unchecked")+"</option>");
                        html_page.append("<option value='"+field.getCheckedValue()+"'>"+mgr.translateJavaText("FNDLAYCHE: Checked")+"</option>");
                        html_page.append("<option value=''></option>");
                    }
                else
                    {
                        html_page.append("<option value=''></option>");
                        html_page.append("<option value='"+field.getCheckedValue()+"'>"+mgr.translateJavaText("FNDLAYCHE: Checked")+"</option>");
                        html_page.append("<option value='"+field.getUncheckedValue()+"'>"+mgr.translateJavaText("FNDLAYUNCHE: Unchecked")+"</option>");
                    }

                html_page.append("</select>");
            }
            else
            {
               html_page.append("<input "+getTooltip(field,"TEXT_INPUT")+" class='checkbox' type=checkbox value=\""+field.getCheckedValue()+"\" ");
               if (getCurrentRowValue(rowset, name, field).equals(field.getCheckedValue())) //Behövs ev.  inte
                  html_page.append("checked");
               html_page.append(" ");
               field.appendValidationTag(html_page,-1, false);
               html_page.append(" name=\"",name,"\" >");
               
               //html_page.append(field.getCheckedValue() + "=" + getCurrentRowValue(rowset, name));
            }
            return false;
         }
         else if (field.isRadioButtons())
         {
            for(i=0;i<field.countValues();i++)
            {
               html_page.append("<input class=\"radioButton\" type=\"radio\"  name=\""+name+"\" value=\""+field.getIidClientValues()[i]+"\" ");
               if (field.getIidClientValues()[i].equals(getCurrentRowValue(rowset,name,field)))
                  html_page.append(" checked ");
               if(!isFindLayout())
                   field.appendValidationTag(html_page,-1, false);
               html_page.append(">"+field.getIidClientValues()[i]+" <br>");
            }

            return false;
         }
         else if (field.isPasswordField())
         {
            html_page.append("<input class='passwordTextField' type=password size="+((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6)) +
                             " value=\""+getCurrentRowValue(rowset,name,field)+"\" " +
                             "name=\"",name,"\" ");
            // Added by Terry 20120821
            // Add client functions
            addClientFunc(field);
            html_page.append(" >");
            // Added end
            return false;
         }

         if (field.getHeight() > 1)
         {
        	 // Added by Terry 20120821
        	 // Control SV, user can only click it, can not input value.
        	 if (field.hasShortcutValue() && isEditLayout())
        	 {
        		 html_page.append("<textarea  onFocus=\"javascript:if(default_command!=null)default_command=false;\"");
        		 html_page.append(field_control_sv + " onkeyup=\"return checkMaxLength(this);\" class='editableTextArea' name=\"" + name +
        				 "\"  cols=" + ((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6)) + " rows=" + field.getHeight() + " wrap='virtual' ");
        	 }
        	 else
        	 {
        	    html_page.append("<textarea  onFocus=\"javascript:if(default_command!=null)default_command=false;\" onBlur=\"javascript:if(default_command!=null)default_command=true;\" " + (client_function_keypress ? "" : "onkeypress=\"return checkMaxLength(this);\"") + " onkeyup=\"return checkMaxLength(this);\" class='editableTextArea' name=\"" + name +
        	          "\"  cols=" + ((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6)) + " rows=" + field.getHeight() + " wrap='virtual' ");
        	 }
        	 addClientFunc(field);
        	 // Modified end
             return true;
         }
         else
         {
            //html_page.append("<input "+getTooltip(field,"TEXT_INPUT")+" class='editableTextField' type=text size="+((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6)) +
             //                            " value=\""+getCurrentRowValue(rowset,name,field)+"\" ");
            
            String eleVal = getCurrentRowValue(rowset,name,field);
            String eleName = name;

            if (field.isAccurateFld() && !isFindLayout())
            {
               html_page.append("<input type=hidden name="+name+" value=\""+eleVal+"\" >");

               String dbname = field.getDbName();
               if (rowset.countRows()>0)
                  if (rowset.getValue(dbname)!=null && !rowset.getValue(dbname).equals("null"))
                  {
                     //html_page.append(""+rowset.getValue(dbname));
                     eleVal = field.getAccurateClientFormatter().format(new Double(rowset.getValue(dbname)));
                  }

               eleName = field.getAccurateDisplayFldName();
            }
            
            html_page.append("<input "+getTooltip(field,"TEXT_INPUT")+" class=\'editableTextField\' ");
            if(blk.isAlignNumbersToRight())
            {   
              if(field.getAlignment().length()>0)
              html_page.append("style=\"text-align=", field.getAlignment(),"\" ");
            }
            html_page.append("type=text size="+((mgr.isExplorer()||mgr.isMozilla()) ? field.getSize() : Math.round(field.getSize()*.6))); 
            
            html_page.append(" value=\""+eleVal+"\" ");
            html_page.append(" name=\"",eleName,"\" ");
            
            // Added by Terry 20120109
            // Control sv field, can not input value.
            html_page.append(field_control_sv);
            // Added end
            
            // Added by Terry 20140410
            // Calculate numberic field value
            // int type_id = field.getTypeId();
            // if (!field.isAccurateFld() && (isEditLayout() || isNewLayout()) && (type_id == DataFormatter.NUMBER || type_id == DataFormatter.INTEGER || type_id == DataFormatter.MONEY))
            //    html_page.append(" onBlur=\"javascript: if (this.value.indexOf('=') == 0) this.value = eval(this.value.substring(1));\" ");
            // Added end
            
            if (field.isAccurateFld() && !isFindLayout())
            {
               String mask = field.getMask();
               mask = mask.substring(mask.indexOf(".")+1);
               
               html_page.append(" onFocus=\"this.value=f."+name+".value\" ");
               html_page.append(" onBlur=\"javascript:roundOff(this,f."+name+","+mask.length()+")\" ");
            }
            
            // Added by Terry 20120821
            // Add client functions
            addClientFunc(field);
            // Added end
         }
      }
      catch (Throwable e) {error(e);}
      return true;

   }

   private void endCell(ASPField field, String name, ASPRowSet rowset)
   {
      // Modified by Terry 20140822
      // Conditional Mandatory property of field
      // Original:
      // if(field.isMandatory() && !field.isCheckBox() && !field.isRadioButtons() && (isNewLayout() || isEditLayout() || isCustomLayout()))
      //    html_page.append("*");
      ASPManager mgr = getASPManager();
      if((field.isMandatory() || field.hasConditionalMandatory()) && !field.isCheckBox() && !field.isRadioButtons() && (isNewLayout() || isEditLayout() || isCustomLayout()))
      {
         if (field.isMandatory())
            html_page.append("*");
         else
         {
            // Field has conditional mandatory setting
            // Add a readonly text field to show *
            String mandatory_field_name = "";
            if (!field.isSimple())
               mandatory_field_name = "__CONDITIONAL_MANDATORY_" + field.getName();
            
            if (!Str.isEmpty(mandatory_field_name))
            {
               String value = mgr.readValue(mandatory_field_name);
               value = mgr.isEmpty(value) ? "" : value;
               if (Str.isEmpty(value))
               {
                  Buffer condition_buff = field.getConditionalMandatory();
                  if (conditionsTrue(rowset, condition_buff))
                     value = "*";
               }
               html_page.append("<input style=\"width:10px\" class='readOnlyTextField' type=text size=1 name=\"",mandatory_field_name,"\" value=\"");
               html_page.append(value+"\" readonly tabindex=-1");
               html_page.append(">");
            }
         }
      }
      // Modified end
      
      addDateTimeMask(field);
      
      if(nextField == null || !nextField.isSimple())
      {
         html_page.append("</font></td>");
         // Modified by Terry 20130926
         // Show sort select in findlayout
         // Original:
         // html_page.append(appendSpaceAfter());
         html_page.append(appendSortAndSpaceAfter(field));
         // Modified end
         
      }
   }
   
   // Added by Terry 20141008
   // Field has conditional mandatory setting
   // Initial value of __CONDITIONAL_MANDATORY_ field
   boolean conditionsTrue(ASPRowSet rowset, Buffer condition_buff)
   {
      return conditionsTrue(rowset, rowset.getCurrentRowNo(), condition_buff);
   }

   boolean conditionsTrue(ASPRowSet rowset, int row, Buffer condition_buff)
   {
      boolean conditionMatch = false;
      try
      {
         if(rowset.countRows() < 1)
            return false;

         if (condition_buff != null && condition_buff.countItems() > 0)
         {
            for (int i = 0; i < condition_buff.countItems(); i++)
            {
               Item validate_fld = condition_buff.getItem(i);
               String validate_fld_name = validate_fld.getName();
               String validate_fld_value = validate_fld.getString();
               if (!Str.isEmpty(validate_fld_name) && !Str.isEmpty(validate_fld_value))
               {
                  String fld_value = rowset.getValueAt(row, blk.getASPField(validate_fld_name).getDbName());
                  if (Str.isEmpty(fld_value))
                     fld_value = "null";
                  StringTokenizer value_st = new StringTokenizer(validate_fld_value, ";");
                  conditionMatch = false;
                  while(value_st.hasMoreTokens())
                  {
                     String value = value_st.nextToken();
                     if(fld_value.equals(value))
                        conditionMatch = true;
                  }
                  
                  if (!conditionMatch)
                     return false;
               }
            }
            return conditionMatch;
         }
         return false;
      }
      catch(Exception e)
      {
         error(e);
         return false;
      }
   }
   // Added end

   //RIRALK: commented this method as a Q&D fix for profile + defineGroup() bug and replaced with original method
   /*private void findHiddens(ASPField[] columns)
   {
      ASPField currcol;
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      for(int i=0;i<user_columns.length;i++)
          {
              currcol = user_columns[i];
              if(!currcol.isHidden()) continue;
              if(getASPPage().getASPLov() != null && !Str.isEmpty(ctx.readValue(currcol.getName())))
                  html_page.append("<input type=hidden name=\""+currcol.getName()+"\" value=\""+ctx.readValue(currcol.getName())+"\">");
              else if(currcol.hasGlobalConnection())
                  html_page.append("<input type=hidden name=\""+currcol.getName()+"\" value=\""+currcol.getGlobalValue()+"\">");
          }
   }*/

   private void findHiddens(ASPField[] columns)
   {
      ASPField currcol;
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      for(int i=0;i<columns.length;i++)
          {
              currcol = columns[i];
              if(!currcol.isHidden()) continue;
              if(getASPPage().getASPLov() != null && !Str.isEmpty(ctx.readValue(currcol.getName())))
                  html_page.append("<input type=hidden name=\""+currcol.getName()+"\" value=\""+ctx.readValue(currcol.getName())+"\">");
              else if(currcol.hasGlobalConnection())
                  html_page.append("<input type=hidden name=\""+currcol.getName()+"\" value=\""+currcol.getGlobalValue()+"\">");
          }
   }

   private void generateMasterHiddens()
   {
       blk = getBlock();
       ASPBlock mblk = blk.getMasterBlock();
       if(mblk==null) return;
       recHiddens(mblk);
   }

   private void recHiddens(ASPBlock blk)
   {
       ASPField[] fields = blk.getFields();
       ASPRowSet rowset = blk.getASPRowSet();
       for(int i=0;i<fields.length;i++)
           {
               html_page.append("<input type=hidden name=\""+fields[i].getName()+"\" value=\""+getCurrentRowValue(rowset,fields[i].getName(),fields[i])+"\">");
           }
       html_page.append("\n");
       ASPBlock mblk = blk.getMasterBlock();
       if(mblk!=null)
           recHiddens(mblk);

   }

   //==========================================================================
   //  Field span & width functions
   //==========================================================================

   private String writeLabelSpan(ASPField field)
   {
        cells_done = cells_done + field.getLabelSpan();

        if(field.getLabelSpan() > 1)
            return "colspan=" + field.getLabelSpan();
        else
            return "";

   }

   private String writeDataSpan(ASPField field)
   {
        cells_done = cells_done + field.getDataSpan();

        if(field.getDataSpan() > 1)
            return "colspan=" + field.getDataSpan();
        else
            return "";

   }


   /**
    * Sets this ASPField to be simple, meaning that no
    * label is shown, and the field is outside the layout logic;
    * no HTML TD cell is generated.
    */
   public void setSimple(String field)
   {
      getBlock().getASPManager().getASPField(field).setSimple();
   }


   /**
    * Unset the simple property.
    */

   public void unsetSimple(String field)
   {
      getBlock().getASPManager().getASPField(field).unsetSimple();
   }


   /**
    * Set the labelspan, dataspan, and number of extra columns to add for this ASPField.
    */
  /* public void setFieldSpan(String field,int lSpan, int dSpan, int skipped)
   {
      getBlock().getASPManager().getASPField(field).setSpan(lSpan,dSpan,skipped);
   }
   */
   /**
    * Set the labelspan, dataspan for this ASPField.
    */
   public void setFieldSpan(String field, int lSpan, int dSpan)
   {
      getBlock().getASPManager().getASPField(field).setSpan(lSpan,dSpan);
   }

   /**
    * Set the labelspan for this ASPField.
    */
   public void setLabelSpan(String field, int lSpan)
   {
      getBlock().getASPManager().getASPField(field).setLabelSpan(lSpan);
   }

   /**
    * Set the dataspan for this ASPField.
    */
   public void setDataSpan(String field, int dSpan)
   {
       try {
           getBlock().getASPManager().getASPField(field).setDataSpan(dSpan);
       }
       catch(Throwable any){error(any);}
   }

   /**
    * Set the number of extra columns to add for this ASPField.
    */
/*   public void setSkip(String field, int skipped)
   {
      getBlock().getASPManager().getASPField(field).setSkip(skipped);
   }
*/
   /**
    * Get the label span for this field.
    */
   public int getLabelSpan(String field)
   {
      try {
      return getBlock().getASPManager().getASPField(field).getLabelSpan();
      }
      catch(Throwable any){error(any);return -1;}
   }

   /**
    * Get the data span for this field.
    */
   public int getDataSpan(String field)
   {
      try {
      return getBlock().getASPManager().getASPField(field).getDataSpan();
      }
      catch(Throwable any){error(any); return -1;}
   }


   int getSpaceBetween()
   {

      return spaceBetween;

   }

   int getSpaceAfter()
   {

      return spaceAfter;

   }

/**
 * Sets the space between the label and the data field, in the single row mode.
 */
   public void setSpaceBetween(int space)
   {

      spaceBetween = space;

   }
   /**
    * Space added after the datafield, in single row mode.
    */
   public void setSpaceAfter(int space)
   {

      spaceAfter = space;

   }
   /**
    * Get the number of extra columns for this field.
    */
/*   public int getSkip(String field)
   {
      return getBlock().getASPManager().getASPField(field).getSkip();
   }
*/

  /**
   * These methods will sets the Layout mode. The layout mode can decide which
   * HTML layout that will be used in the final webpage. It can also affect the
   * appearance of the Command bar.
   */

    /**
    * Return true when the current layout mode is a multirow layout.
    */
    public   boolean isMultirowLayout()
    {
        return getLayoutMode()==MULTIROW_LAYOUT;
    }
 
    /**
     * Return true when the current layout mode is a single layout.
     */
    public   boolean isSingleLayout()
    {
        return getLayoutMode()==SINGLE_LAYOUT;
    }

    /**
     * Return true when the current layout mode is a edit layout.
     */
    public   boolean isEditLayout()
    {
        return getLayoutMode()==EDIT_LAYOUT;
    }

    /**
     * Return true when the current layout mode is a find layout.
     */
    public   boolean isFindLayout()
    {
        return getLayoutMode()==FIND_LAYOUT;
    }

    /**
     * Return true when the current layout mode is a new layout.
     */
    public   boolean isNewLayout()
    {
        return getLayoutMode()==NEW_LAYOUT;
    }
  
    /**
     * Return true when the current layout mode is a custom layout.
     */
    public   boolean isCustomLayout()
    {
        return getLayoutMode()==CUSTOM_LAYOUT;
    }

   /**
    * Sets the current History Layout Mode. Used to determine which layout mode (table or single row mode) to return too after a dialog.
    */
    public   void setHistoryMode(int mode)
    {
        history_mode= mode;
    }

   /**
    * Fetches the current History Layout Mode
    */
    public   int getHistoryMode()
    {
        return history_mode;
    }

   /**
    * Sets the Default Layoutmode of a block. Should be used in preDefine().
    */
    public void setDefaultLayoutMode(int mode)
    {
      try
      {
        modifyingMutableAttribute("DEFAULTLAYOUTMODE");
        default_mode = mode;
        layout_mode = mode;
        if(mode==FIND_LAYOUT) history_mode=MULTIROW_LAYOUT;
        if(default_mode!=UNDEFINED && default_mode!=NONE)
           getASPPage().setBlockLayoutAvailability();
      }
      catch( Throwable any)
          {
              error(any);
          }
    }

   int getDefaultLayoutMode()
   {
       return default_mode;
   }

   /*
    * Returns true if the layout is visible
    */
   public boolean isVisibleByUserProfile()
   {      
      ASPProfile aspprf = getASPPage().getASPProfile();
      ASPBlock block = this.getBlock();
      
      if(block.isMasterBlock())
         return true;
      else if(aspprf!=null)
      {
         try 
         {
            Buffer tmpbuf = aspprf.get("BlockLayout");
            
            if(tmpbuf!=null)
            {
               Object tmp = tmpbuf.getItem(block.getName()+ProfileUtils.ENTRY_SEP+"BlockVisible").getValue();
               if(tmp!=null && tmp.toString().equals("N"))
                     return false;            
            }
         } 
         catch (Exception ex) {} 
      }
      return true;
   }
   
   /*
    * Returns true if the block in this layout is not a master block.
    */ 
   public boolean isLayoutRemovable()
   {
      if(this.getBlock().isMasterBlock() || getDefaultLayoutMode()== UNDEFINED || getDefaultLayoutMode()== NONE)
         return false;
      return true;
   }
  
   /**
    * Sets the current Layoutmode for this block. Should only be used when the Layout mode changes.
    */
    public   void setLayoutMode(int mode)
    {
        if (mode==FIND_LAYOUT || mode==EDIT_LAYOUT || mode==NEW_LAYOUT)
            if(layout_mode!=FIND_LAYOUT)    setHistoryMode(layout_mode);
        setLastMode(layout_mode);
        layout_mode = mode;
    }
  /**
   * This method will fetch the current layout mode. !!!
   */
    public   int getLayoutMode()
    {
        return layout_mode;
    }

   void setLastMode(int m)
   {
       last_mode = m;
   }

   int getLastMode()
   {
       return last_mode;
   }

   boolean wasFindLayout()
   {
       if(last_mode==FIND_LAYOUT) return true;
       return false;
   }

    void saveLayout()
    {
      if(getLayoutMode()==UNDEFINED)
      {
          getASPPage().getASPContext().writeValue("__LAYOUTMODE"+getBlock().getName(),""+default_mode);
          layout_mode = default_mode;
      }
      else
         getASPPage().getASPContext().writeValue("__LAYOUTMODE"+getBlock().getName(),""+getLayoutMode());

      getASPPage().getASPContext().writeValue("__HISTORYMODE"+getBlock().getName(),""+getHistoryMode());
      getASPPage().getASPContext().writeValue("__LASTMODE"+getBlock().getName(),""+getLastMode());

    }

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

   /*
    * Used when update profile buffer is called from Pageproperties page, 
    * sets the boolean variable isPropertiesPage to true if its called from PageProperties page.
    */
   public void updateProfileBuffer( ASPBuffer info , boolean properties)
   {
      isPropertiesPage = properties;
      saveProfile( info, false );
   }
   
   public void updateProfileBuffer( ASPBuffer info )
   {
      //called by CSL page to update all profile entries and save at once
      updateProfileBuffer( info, false );
   }
   
   /*
    * returns true if current execution is triggered from PageProperties
    */
   boolean getIsPropertiesPage()
   {
      return isPropertiesPage;
   }

   private void saveProfile( ASPBuffer info, boolean save_profile )
   {
      if(DEBUG) debug("ASPBlockLayout.saveProfile()");
      try
      {
         profile = new BlockLayoutProfile();
         profile.load(this,info.getBuffer());
         modifyingMutableAttribute("PROFILE");
         user_profile_prepared = false;

         ASPProfile prf = getASPPage().getASPProfile();
         if(DEBUG) debug("  profile="+prf);
         prf.update(this,profile);
         //prf.save(getASPPage());
         if (save_profile)
            prf.save(this);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Return an ASPBuffer containing profile information corresponding to
    * this ASPBlockLayouot. The buffer may be modified and then stored by calling
    * the method saveProfile().
    *
    * @see ifs.fnd.asp.ASPManager.saveProfile
    */
   public ASPBuffer getProfile()
   {
      try
      {
         if(DEBUG) debug("ASPBlockLayou.getProfile()");
         user_profile_prepared = false;
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

   //Caled by CSL page to get the reordered field list for the given group
   public String getProfileGroupFieldList(int group_id)
   {
      try
      {
         if(DEBUG) debug("ASPBlockLayou.getProfile()");
         user_profile_prepared = false;
         prepareProfileInfo(false);

         return profile.getGroupField(group_id);
      }
      catch (Throwable any)
      {
         error(any);
         return "";
      }
   }

   /**
    * Remove profile information from database for this ASPBlockLayout.
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
      if(DEBUG) debug("ASPBlockLayout.removeProfile()");
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

   private void createBaseProfile() throws FndException
   {
      profile = new BlockLayoutProfile();
      profile.construct(this);
      user_profile_prepared = false;
      pre_profile = profile;
   }


   void prepareProfileInfo(boolean apply_settings) throws FndException
   {
      if( user_profile_prepared ) return;
      if(DEBUG) debug("ASPTable.prepareProfileInfo():");

      if( pre_profile==null ) createBaseProfile(); //create defualt profile using page design time defintions

      if (!getASPManager().isDefaultLayout())
      {
        ASPProfile aspprf = getASPPage().getASPProfile();
        if(DEBUG) debug("   getASPProfile() = "+aspprf);
        profile = (BlockLayoutProfile)aspprf.get(ASPBlockLayout.this,pre_profile);
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
      
      // Added by Terry 20131030
      // Hidden fields move to last position
      ASPField[] hidden_columns = new ASPField[column_count];
      int hidden_columns_count = 0;
      int unhidden_columns_count = 0;
      // Added end
      
      //  Copy all columns[] to user_columns[]
      for( int i=0; i<column_count; i++ )
      {
         // Modified by Terry 20131030
         // Hidden fields move to last position
         // Original:
         // user_columns[i] = columns[i];
         if (columns[i].isHidden())
         {
            hidden_columns[hidden_columns_count] = columns[i];
            hidden_columns_count = hidden_columns_count + 1;
         }
         else
         {
            user_columns[unhidden_columns_count] = columns[i];
            unhidden_columns_count = unhidden_columns_count + 1;
         }
         // Modified end
      }
      
      // Added by Terry 20131030
      // Hidden fields move to last position
      for (int i = 0; i < hidden_columns_count; i++)
      {
         user_columns[unhidden_columns_count] = hidden_columns[i];
         unhidden_columns_count = unhidden_columns_count + 1;
      }
      // Added end

      //  For every position (nr) in user profile
      int prof_col_count = profile.getColumnCount();
      for( int nr=0; nr<prof_col_count; nr++ )
      {
         //  Find ASPField that should be at position nr
         String name = profile.getColumnName(nr);
         ASPField col = findUserColumn(name);
         // Added by Terry 20131030
         // Hidden fields move to last position
         if (col.isHidden()) continue;
         // Added end
         int user_size = profile.getColumnSize(name);
         if (user_size<0)
         {
            if (!col.isDirtyHiddenFlag())
               col.setHidden();
         }
         else
         {  
            if(!col.isDirtySizeFlag())
            col.setSize(user_size);
         }
         
         if(!col.isDirtyDataSpanFlag())
         	col.setDataSpan(profile.getColumnSpan(name));

         if(!col.isDirtyHeightFlag())
            col.setHeight(profile.getColumnHeight(name));
         
         if (!profile.isLabelVisible(name))
            col.setSimple();
         if (profile.isColumnMandatory(name) && !col.isDirtyMandatoryFlag())
            col.setMandatory();
         if (!profile.isColumnQueryable(name) && !col.isDirtyQueryableFlag())
            col.unsetQueryable();
         if (profile.isColumnReadOnly(name) &&  !col.isDirtyReadOnlyFlag())
            col.setReadOnly();

         //  Find its position (k) in user_columns[]
         int k = findUserColumnIndex(name);
         if(DEBUG) debug("... swaping k="+k+" nr="+nr);

         if( k==nr ) continue; //correct position no need to swap

         swapUserColumn(k,nr);
      }

      for (int i=prof_col_count; i<user_columns.length; i++)
      {
         ASPField fld = user_columns[i];
         if (!fld.isDirtyHiddenFlag())
            user_columns[i].setHidden();
      }

      if (hasDefinedGroups())
      {
         int group_count = groups.size();

         for (int i=0; i<group_count; i++)
         {
            group group = (group)groups.elementAt(i);
            String original_list = group.getFieldList();

            String prof_list = profile.getGroupField(group.id);

            Vector prof_columns = new Vector();
            StringTokenizer myFields = new StringTokenizer(prof_list,",");

            while(myFields.hasMoreTokens())
            {
               prof_columns.addElement(myFields.nextToken());
            }

            //add fields not in profile
            myFields = new StringTokenizer(original_list,",");

            while(myFields.hasMoreTokens())
            {
               String field_name = myFields.nextToken();
               if (!prof_columns.contains(field_name))
                  prof_list += ","+field_name;
            }

            group.setFieldList(prof_list);
         }
      }

      user_profile_prepared = true;
   }


   private void swapUserColumn( int i, int j )
   {
      ASPField tmp = user_columns[i];
      user_columns[i] = user_columns[j];
      user_columns[j] = tmp;
   }

   private int findUserColumnIndex( ASPField column, int from )
   {
      for( int i=from; i<user_columns.length; i++ )
         if( user_columns[i]==column )
            return i;
      return -1;
   }

   private ASPField findUserColumn( String name )
   {
      for( int i=0; i<user_columns.length; i++ )
      {
         ASPField column = user_columns[i];
         if( name.equals(column.getName()) )
            return column;
      }
      return null;
   }

   private int findUserColumnIndex( String name )
   {
      for( int i=0; i<user_columns.length; i++ )
      {
         ASPField column = user_columns[i];
         if( name.equals(column.getName()) )
            return i;
      }
      return -1;
   }



   /**
    * Auto layout select switched on. If ASPBlockLayout.show() is used and a query only return with on row it will
    * be displayed in SingleRowMode, no matter what you have set the Layout mode too.
    * Use this one with care. You might end up in trouble with master/detail forms, where detailparts might not be populated
    * depending on where you call this function in your script. Make sure you call it before any commandbar functions.
    */

    public void setAutoLayoutSelect()
    {
      try
      {
        modifyingMutableAttribute("AUTOSELECTLAYOUT");
        layout_select = true;
      }
      catch( Throwable any)
          {
              error(any);
          }
    }

   /**
    * Auto layout select switched off.
    */
   public void unsetAutoLayoutSelect()
    {
      try
      {
        modifyingMutableAttribute("AUTOSELECTLAYOUT");
        layout_select = false;
      }
      catch( Throwable any)
          {
              error(any);
          }
    }

   /**
    * Returns the Auto layout select flag
    */
   public boolean isAutoLayoutSelectOn()
    {
        return layout_select;
    }

   /**
    * This function will adjust the layout mode before the ASPBlockLayout.show() function.
    * When auto layout select is on you might end up in a different layout mode after show() than
    * before show() was executed.
    * This function should only be used in adjust.
    */
   public void adjustLayout()
    {
      try
      {
      if((blk.getASPRowSet().countRows() == 1) && (isMultirowLayout() || isSingleLayout()) && isAutoLayoutSelectOn() )      {
              blk.getASPCommandBar().disableCommand("Back");
              setLayoutMode(SINGLE_LAYOUT);
          }
      }
      catch( Throwable any)
          {
              error(any);
          }
    }


   /**
    * Returns true if this block should be displayed. Depended on the LayoutMode of this block and all other blocks.
    */
   public boolean isVisible()
    {
        ASPBlock mblk = blk.getMasterBlock();
        ASPBlock cblk;
        ASPBlockLayout clay;
        String name = blk.getName();
        Vector blocks = getASPPage().getASPBlocks();

        if (mblk!=null && mblk.getASPBlockLayout().isMultirowLayout())
            return false;
        for( int i=0; i<blocks.size(); i++ )
         {
            cblk = (ASPBlock) blocks.elementAt(i);
            clay = cblk.getASPBlockLayout();
            if (!name.equals(cblk.getName()))
                if (clay.isEditLayout() || clay.isNewLayout() || clay.isFindLayout())
                   return false;
         }
        if (getASPPage().isLogonPage()) return true;
        return isVisibleByUserProfile();
    }

   private String getTooltip(ASPField field,String mode) throws FndException
   {
      if (isFindLayout()) return " ";

      AutoString html = new AutoString();
      field.appendTooltipTag(html,-1,"TEXT_INPUT".equals(mode));

      return html.toString();

/*
      if(!mgr.isNetscape4x() && field.hasTooltip()){
         if(mode.equals("TEXT_LABEL"))
         {
            String tip = toSingleLine(field.getTooltipLabel() + getCurrentRowValue(rowset,field.getTooltipField().getName(),field));
            tip = mgr.replace(mgr.HTMLEncode(tip),"\'","\\\'");

            return "onMouseover=\"showtip(this,event,'"+ tip +
                   "')\" onMouseOut=\"hidetip()\"";
         }
         else if (mode.equals("TEXT_INPUT"))
            return "onMouseover=\"javascript:if(f."+
                   field.getTooltipField().getName()+" && f."+
                   field.getTooltipField().getName()+".value!='')showtip(this,event,'"+
                   field.getTooltipLabel()+"'+f."+
                   field.getTooltipField().getName()+".value)\" onMouseOut=\"javascript:if(f."+
                   field.getTooltipField().getName()+" && f."+
                   field.getTooltipField().getName()+".value!='') hidetip()\"";
         else
            return "";
     }
     else
        return "";
 */
   }


   private void addCalendar(ASPField field) throws FndException
   {
      try{
         if (field.isDateTime())
                field.appendCalendarTag(html_page,-1);
      }
      catch(Throwable e)
         {error(e);}
   }

   /**
    * Sets Default Command to a particular Layout Mode
    * @param layout The Layout Mode in which you want to change the Default command.
    * @param command The command itself which you want to make as default.
    * <pre>NOTE: Pass an empty String ("") as the second parameter to disable a predefine Default Command.</pre>
    */
   public void setDefaultCommand(int layout,String command){
       default_commands[layout] = command;
   }

   /**
    * Returns the Default Command for a given Layout Mode
    * @param layout The Layout Mode in which you want to get the Default command.
    */
   public String getDefaultCommand(int layout){
       return default_commands[layout];
   }

   private void addDateTimeMask(ASPField field)
   {
      try
      {
         if ((field.isDateTime()||field.isTime()))
                field.appendTranslatedMask(html_page);
      }
      catch(Throwable e)
         {error(e);}
   }

   /**
    * Makes the dotted line at the bottom of the block layout visible/invisible.
    * @param line value 'false' will make the bottom line invisible where as 'true' will make it visible.
    */
   public void showBottomLine(boolean line)
   {
       try
       {
          modifyingMutableAttribute("DOTTED_LINE");
          dotted_line = line;
       }
       catch( Throwable any )
       {
          error(any);
       }
   }

   private String toSingleLine(String tip)
   {
      ASPManager mgr = getASPManager();
      String tip_line = mgr.translate("ASPTOOLTIPNOTAVAILABLE: Not Available");
      if(!mgr.isEmpty(tip))
      {
         StringReader temp_String_Reader = new StringReader(tip);
         tip_line = "";
         int temp_String = 0;
         try
         {
            while((temp_String = temp_String_Reader.read())!=-1)
            {
               if(temp_String==13)
               {
                  tip_line += " <BR>";
                  temp_String_Reader.skip(1);
               }
               else
                  tip_line += (char)temp_String;
            }
         }
         catch(Exception e){}
       }
       return tip_line;
   }

   int findColumnIndex( String name )
   {
      for( int c=0; c<columns.length; c++ )
      {
         ASPField column = columns[c];
         if( column.getName().equals(name) )
            return c;
      }
      return -1;
   }

   ASPField findColumn( String name )
   {
      for( int c=0; c<columns.length; c++ )
      {
         ASPField column = columns[c];
         if( column.getName().equals(name) )
            return column;
      }
      return null;
   }

   int findColumnGroup( String name )
   {
      for( int c=0; c<columns.length; c++ )
      {
         ASPField column = columns[c];
         if( column.getName().equals(name) )
            return column.getGroupId();
      }
      return -1;
   }
   
   // Added by Terry 20120821
   // append SV Tag
   private void addSV(ASPField field) throws FndException
   {
      try
      {
         if (field.hasShortcutValue())
            field.appendSVTag(html_page);
      }
      catch(Throwable e)
      {
    	 error(e);
      }
   }
   
   // append client Tag
   private void addClientFunc(ASPField field) throws FndException
   {
      try
      {
         if (field.hasClientFunc())
            field.appendClientFuncTag(html_page);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }
   // Added end

   // Added by Terry 20130917
   // check CSV name
   private boolean isUrlCSVName(String name)
   {
      if (name == null || name == "") return false;
      name = name.trim();
      if (!name.toUpperCase().equals(name)) return false;
      if (!name.startsWith("@")) return false;
      if (!name.endsWith("@")) return false;
      return true;
   }
   
   private int splitIntoOperatorAndValue( String value )
   {
      if( Str.isEmpty(value) ) return 0;

      switch(value.charAt(0))
      {
         // Bug 40924, start
         // case '=':
         // Bug 40924, end
         case '<':
         case '>':
         case '!':
            break;

         default:
            return 0;
      }
      
      // Added by Terry 20131118
      // Check accurate find
      boolean accurate_find = false;
      if (getASPManager().commandBarActivated())
      {
         if("TRUE".equals(getASPManager().readValue("__ACCURATE_FIND", "FALSE")))
            accurate_find = true;
      }
      else
         accurate_find = true;
      // Added end
      
      if( value.startsWith("<=") )
         return 2;
      else if( value.startsWith(">=") )
         return 2;
      else if( value.startsWith("<>") )
         return 2;
      else if( value.startsWith("!=") )
      {
         // Modified by Terry 20131119
         // Original:
         // if(value.indexOf("%")>0 || value.indexOf("_")>0)
         if(value.indexOf("%")>0 || value.indexOf("_")>0 || !accurate_find)
            return 0;
         return 2;
      }
      else if( value.startsWith("!") && value.trim().length()==1)//Depends on the if statement above.
          return 1;
      else if( value.startsWith("<") )
         return 1;
      else if( value.startsWith(">") )
         return 1;
      else
         return 0;
   }
   
   // Advanced Query
   // Get Advanced Query Where Clause
   private String getAdvancedQueryString() throws Exception
   {
      ASPManager mgr = getASPManager();
      // DataFormatter strformatter = mgr.getDataFormatter(DataFormatter.STRING,"");
      // ServerFormatter srvfmt = mgr.getServerFormatter();
      
      // Added by Terry 20140623
      // Get view param fields from CONTEXT
      ASPContext ctx = mgr.getASPContext();
      String __view_param_fields = ctx.readValue("__VIEW_PARAM_FIELDS");
      // Added end
      
      ASPPage page = blk.getASPPage();
      
      boolean caseinsens=false;
      if(page.readValue("__CASESS","").equals("1"))
         caseinsens = false;
      else if(wasFindLayout() || isFindLayout())
         caseinsens = true;
      else if("FALSE".equals(mgr.getQueryStringValue("CASESENCETIVE")))
         caseinsens = true;
      else if(("N").equals(page.readValue("__CASESS_VALUE","")))
         caseinsens = true;
      
      // Added by Terry 20131118
      // Check accurate find
      boolean accurate_find = false;
      if (getASPManager().commandBarActivated())
      {
         if("TRUE".equals(getASPManager().readValue("__ACCURATE_FIND", "FALSE")))
            accurate_find = true;
      }
      else
         accurate_find = true;
      // Added end
      
      AutoString where = new AutoString();
      
      ASPField[] fields = blk.getFields();
      if(DEBUG) debug("ASPBlockLayout - getAdvancedQueryString(): number of fields="+(fields==null?"null":""+fields.length));
      for( int i=0; i<fields.length; i++ )
      {
         ASPField field = fields[i];
         if(DEBUG) debug("ASPBlockLayout - getAdvancedQueryString(): parsing field '"+(field==null?"null":field.getName())+"'");
         boolean gobal_con = field.hasGlobalConnection();
         if (!field.isQueryable() && !gobal_con) continue;
         String name = field.getName();
         String dbname = field.getDbName();
         
         String value;
         if (gobal_con)
            value = field.getGlobalValue();
         else
            value = Util.trimLine(page.readValue(name));
         
         // Added by Terry 20120822
         // Check CSV value, and get CSV value.
         if(isUrlCSVName(value))
         {
            value = mgr.getCSVValue(Str.replace(value, "@", "#"), field);
         }
         // Added end
         
         if( value==null ) continue;
         if( Util.trimLine(value).length()==0 ) continue;
         
         // Added by Terry 20140623
         // Check field accurate_find
         boolean field_accurate_find = false;
         if (!mgr.isEmpty(__view_param_fields))
         {
            if (__view_param_fields.indexOf("^" + name + "^") >= 0)
               field_accurate_find = true;
         }
         // Added end
         
         DataFormatter formatter = field.getDataFormatter();
         if( where.length() > 0 )
            where.append("\n   and ");
         where.append("(");
         
         String colname;
         if( field.isComputable() )
            colname = field.getWhereExpression();
         else if( field.searchOnDbColumn() )
            colname = dbname+"_DB";
         else
            colname = dbname;
         
         int type_id = formatter.getTypeId();
         char type_marker = formatter.getBaseTypeMarker(type_id);
         Item item;
         boolean sysdate;
         int orat;
         String expr="NULL";
         
         value = value+";";
         expr = "NULL";
         while (true)  // OR loop.
         {
            orat = value.indexOf(';');
            if (orat == -1) break;
            
            String pre_expr = value.substring(0, orat);
            if (Str.isEmpty(pre_expr) || Str.isEmpty(pre_expr.trim()))
            {
               value = value.substring(orat + 1);
               continue;
            }
            
            if (expr != "NULL") where.append(" OR ");
            expr = pre_expr;
            if (expr.toUpperCase().indexOf("SYSDATE") > -1)
               sysdate=true;
            else
               sysdate=false;
            value = value.substring(orat + 1);
            
            if(field.searchOnDbColumn() && expr.endsWith("%"))
            {
               String valueList = field.findValues(field.getIidClientValues(), expr);
               orat = valueList.indexOf(';');
               if (orat== -1)
               {
                  expr = valueList;
               }
               else
               {
                  expr = valueList.substring(0, orat);
                  value = value + valueList.substring(orat + 1);
               }
            }
            
            // Added by Terry 20131118
            // Check accurate find
            // if (!accurate_find)
            //    expr = "%" + expr + "%";
            // Added end
            
            int pos = splitIntoOperatorAndValue(expr);
            if( pos <= 0 )
            {
               //
               //  COL like ?
               //
               if( field.searchOnDbColumn() )
                  expr = field.encode(expr);
               
               if(expr.startsWith("!=") && ((((expr.indexOf('%')>=0 || expr.indexOf('_')>=0)) && expr.indexOf("add_month(")==-1) || (!accurate_find && !field_accurate_find && expr.indexOf("..")==-1))) // Not like expression
               {
                  expr = expr.substring(2);
                  if(caseinsens)
                     expr = expr.toUpperCase();
                  // Added by Terry 20131118
                  // Check accurate find
                  if (!accurate_find && !field_accurate_find && expr.indexOf('%') < 0)
                     expr = "%" + expr + "%";
                  // Added end
                  // item = ASPBuffer.convertToServerItem(dbname, expr, srvfmt, strformatter, null);
                  // expr = item.getString();
                  
                  switch(type_marker)
                  {
                     case 'S':
                        if(caseinsens)
                           where.append("UPPER("+colname+")"+" not like '" + expr + "'");
                        else
                           where.append(colname+" not like '" + expr + "'");
                        break;
                        
                     case 'D':
                        String mask = ((DateFormatter)formatter).getFormatMask();
                        mask = mgr.toSqlDateMask(mask);
                        where.append("to_char("+colname+",'"+mask+"') not like '" + expr + "'");
                        break;
                        
                     case 'N':
                        where.append(colname+" not like '" + expr + "'");
                        break;
                  }
               }
               else if (((expr.indexOf('%')>=0 || expr.indexOf('_')>=0 || (!accurate_find && !field_accurate_find && expr.indexOf("..")==-1))) && expr.indexOf("add_month(")==-1)
               {
                  if(caseinsens)
                     expr = expr.toUpperCase();
                  // Added by Terry 20131118
                  // Check accurate find
                  if (!accurate_find && !field_accurate_find && expr.indexOf('%') < 0)
                     expr = "%" + expr + "%";
                  // Added end
                  // item = ASPBuffer.convertToServerItem(dbname, expr, srvfmt, strformatter, null);
                  // expr = item.getString();
                  switch(type_marker)
                  {
                     case 'S':
                        if(caseinsens)
                           where.append("UPPER("+colname+")"+" like '" + expr + "'");
                        else 
                           where.append(colname+" like '" + expr + "'");
                        break;
                           
                     case 'D':
                        String mask = ((DateFormatter)formatter).getFormatMask();
                        mask = mgr.toSqlDateMask(mask);
                        where.append("to_char("+colname+",'"+mask+"') like '" + expr + "'");
                        break;
                        
                     case 'N':
                        where.append(colname+" like '" + expr + "'");
                        break;
                  }
               }
               else if( expr.indexOf("..")>0 )
               {
                  String partone="NULL";
                  String parttwo="NULL";
                  int betweenpos;
                  switch(type_marker)
                  {
                     case 'S':
                        betweenpos = expr.indexOf("..");
                        partone = expr.substring(0,betweenpos);
                        parttwo = expr.substring(betweenpos+2);
                        
                        if(caseinsens)
                        {
                           partone = partone.toUpperCase();
                           parttwo = parttwo.toUpperCase();
                        }
                        
                        // item = field.convertToServerItem(partone,null);
                        // partone = item.getString();
                        // item = field.convertToServerItem(parttwo,null);
                        // parttwo = item.getString();
                        
                        if(caseinsens)
                           where.append("UPPER("+colname+")"+" between '" + partone + "' and '" + parttwo + "'");
                        else
                           where.append(colname+" between '" + partone + "' and '" + parttwo + "'");            
                        break;
                        
                     case 'D': 
                        String mask = ((DateFormatter)formatter).getFormatMask();
                        mask = mgr.toSqlDateMask(mask);
                        betweenpos = expr.indexOf("..");
                        partone = expr.substring(0,betweenpos).toUpperCase();
                        parttwo = expr.substring(betweenpos+2).toUpperCase();
                        
                        // item = field.convertToServerItem(partone,null);
                        // partone = item.getString();
                        // item = field.convertToServerItem(parttwo,null);
                        // parttwo = item.getString();
                        
                        where.append(colname+" between ");
                        
                        if( partone.indexOf("SYSDATE") > -1)
                        {
                           if(mgr.isValidSysdateValue(partone))
                              where.append(partone);
                           else
                              mgr.showError("FNDQRYVALSYSDATE: Invalid date with SYSDATE, enter SYSDATE or SYSDATE +/- Number");
                        }
                        else
                           where.append("to_date('" + partone + "', '" + mask + "')");
                        
                        where.append(" and ");
                        
                        if( parttwo.indexOf("SYSDATE") > -1)
                        {
                           if(mgr.isValidSysdateValue(parttwo))
                              where.append(parttwo.replaceAll("SYSDATE","SYSDATE"));
                           else
                              mgr.showError("FNDQRYVALSYSDATE: Invalid date with SYSDATE, enter SYSDATE or SYSDATE +/- Number");
                        }
                        else
                           where.append("to_date('" + parttwo + "', '" + mask + "') + (1-1/(24*60*60))");
                        
                        break;
                        
                     case 'N':
                        betweenpos = expr.indexOf("..");
                        partone = expr.substring(0,betweenpos);
                        parttwo = expr.substring(betweenpos+2);
                        
                        if(caseinsens)
                        {
                           partone = partone.toUpperCase();
                           parttwo = parttwo.toUpperCase();
                        }
                        
                        // item = field.convertToServerItem(partone,null);
                        // partone = item.getString();
                        // item = field.convertToServerItem(parttwo,null);
                        // parttwo = item.getString();
                        
                        where.append(colname+" between " + partone + " and " + parttwo + "");
                        break;
                  }
               }
               else if( type_id == formatter.DATE )
               {
                  String mask = ((DateFormatter)formatter).getFormatMask(); 
                  mask = mgr.toSqlDateMask(mask);
                  if (sysdate)
                  {
                     where.append(colname+"=to_date(to_char("+expr+",'"+mask+"'),'"+mask+"')"); 
                     continue;
                  }
                  where.append(colname+" between add_months(to_date('" + expr + "', '" + mask + "'),0) and add_months(to_date('" + expr + "', '" + mask + "'),0)+(1-1/(24*60*60))");
               }
               else if(type_id == formatter.STRING && caseinsens)
               {
                  if(sysdate)
                  {
                     where.append(colname+" ="+expr);
                     continue;
                  }
                  expr = expr.toUpperCase();
                  // item = field.convertToServerItem(expr,null);
                  // expr = item.getString();
                  
                  where.append("UPPER("+colname+")"+" = '" + expr + "'");
               }
               else
               {
                  if(sysdate)
                  {
                     where.append(colname+" ="+expr);
                     continue;
                  }
                  if ("STATE".equalsIgnoreCase(colname) && !mgr.isEmpty(blk.getStateEncodeMethod()))
                  {
                     // item = field.convertToServerItem(expr,null);
                     // expr = item.getString();
                     
                     where.append("OBJSTATE"+" = (select " + blk.getStateEncodeMethod() +"( '" + expr + "' ) from dual)");                                                                        
                     continue;
                  }
                  // item = field.convertToServerItem(expr,null);
                  // expr = item.getString();
                  
                  if (type_marker == 'N')
                     where.append(colname+" = " + expr + "");
                  else
                     where.append(colname+" = '" + expr + "'");
               }
            }
            else
            {
               //
               //  COL <operator> ?
               //
               if (expr.indexOf("!")>-1 && expr.indexOf("!=")==-1)
               {
                  where.append(""+colname+" is null");
                  continue;
               }
               if (sysdate) 
               {
                  String mask = ((DateFormatter)formatter).getFormatMask(); 
                  mask = mgr.toSqlDateMask(mask);
                  String expr_operator;
                  expr_operator = expr.substring(0,pos);
                  expr = expr.substring(pos);
                  where.append("to_date(to_char("+colname+",'"+mask+"'),'"+mask+"') "+expr_operator+" to_date(to_char("+expr+",'"+mask+"'),'"+mask+"')");
                  continue;
               }
               
               if ("STATE".equalsIgnoreCase(colname) && !mgr.isEmpty(blk.getStateEncodeMethod()))
               {
                  where.append("OBJSTATE"+' '); 
                  where.append(expr.substring(0,pos));
                  expr = expr.substring(pos);
                  if(caseinsens && type_marker==formatter.STRING)
                     expr = expr.toUpperCase();                            
                  // item = field.convertToServerItem(expr,null);
                  // expr = item.getString();
                  where.append(" (select " + blk.getStateEncodeMethod() +"( '" + expr + "' ) from dual)");
                  continue;
               }
               
               if(type_marker == formatter.STRING && caseinsens) 
                  where.append("UPPER("+colname+")"+' ');
               else
                  where.append(colname+' ');                             
               where.append(expr.substring(0,pos));
               
               expr = expr.substring(pos);
               if(caseinsens && type_marker==formatter.STRING)
                  expr = expr.toUpperCase();
               if( field.searchOnDbColumn() ) expr = field.encode(expr);
               
               // item = field.convertToServerItem(expr,null);
               // expr = item.getString();

               if (type_marker == 'D')
               {
                  String mask = ((DateFormatter)formatter).getFormatMask(); 
                  mask = mgr.toSqlDateMask(mask);
                  where.append(" to_date('" + expr + "', '" + mask + "')");
               }
               else if (type_marker == 'N')
                  where.append(" " + expr + "");
               else
                  where.append(" '" + expr + "'");
            }
         }
         where.append(")");
      }
      
      String textSearch = mgr.readValue("FND_TXT_SEARCH");
      //append application search where condition from find mode
      UserDataCache user_cache = UserDataCache.getInstance();
      Vector available_views = user_cache.getApplicationSearchViews(getASPManager().getSessionId());
      if (!mgr.isEmpty(textSearch) && available_views.contains(blk.getView()))
      {
         if( where.length()>0 )
            where.append("\n   and ");
         
         //Temporary solution. Remove and uncomment the following when Application_Search_SYS.Convert_Search_Criteria__ is working
         where.append("contains(text_id$, Application_Search_SYS.Convert_Search_Criteria__('" + textSearch + "'), 1) > 0");
      }
      return where.toString();
   }
   // Added end
   
   // Added by Terry 20130926
   // Get Advanced Query Order by Clause
   private String getAdvancedQueryOrder() throws Exception
   {
      ASPManager mgr = getASPManager();
      ASPPage page = blk.getASPPage();
      
      String order_by = "";
      
      ASPField[] fields = blk.getFields();
      if(DEBUG) debug("ASPBlockLayout - getAdvancedQueryOrderby(): number of fields="+(fields==null?"null":""+fields.length));
      for( int i=0; i<fields.length; i++ )
      {
         ASPField field = fields[i];
         if(DEBUG) debug("ASPBlockLayout - getAdvancedQueryOrderby(): parsing field '"+(field==null?"null":field.getName())+"'");
         boolean gobal_con = field.hasGlobalConnection();
         if (!field.isQueryable() && !gobal_con) continue;
         String name = field.getName();
         String dbname = field.getDbName();
         
         String field_sort = page.readValue("__SORT_" + name);
         if (!Str.isEmpty(field_sort))
         {
            if (!Str.isEmpty(order_by))
               order_by = order_by + "," + dbname + " " + field_sort;
            else
               order_by = order_by + dbname + " " + field_sort;
         }
      }
      return order_by;
   }
   // Added end
}

