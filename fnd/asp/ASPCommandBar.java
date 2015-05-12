/*
 *
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
 * File        : ASPCommandBar.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Micke A   1998-May-19 - Created
 *    Micke A   1998-Jun-23 - Added the ASPCommandBar.removeCustomCommand(...) method.
 *    Micke A   1998-Jul-01 - Added the visualization of the record status.
 *    Micke A   1998-Jul-08 - Added the "normal" image to the visualization of the record status.
 *    Micke A   1998-Jul-09 - Changed "enableRecordStatus()" to "enableRowStatus()".
 *                            Changed "disableRecordStatus()" to "disableRowStatus()".
 *                            (Now these names correspond to the ASPTable methods.)
 *    Jacek P   1998-Jul-15 - Changed access level for function findItem() to package.
 *    Jacek P   1998-Jul-29 - Introduced FndException concept
 *    Jacek P   1998-Aug-07 - Added try..catch block to each public function
 *                            which can throw exception
 *    Jacek P   1998-Aug-21 - Changes due to redesigned structure of
 *                            the ASPConfig.ifm file (Log id:#2623).
 *    Jacek P   1998-Oct-01 - Added translation of labels (ToDo:#2688). Font
 *                            and color parameters fetched from ASPConfig.ifm.
 *    Jacek P   1998-Oct-13 - Added possibility to show icons instead of labels
 *                            (ToDo:#2757).
 *    Jacek P   1998-Oct-14 - Function removeCommandGroup() does remove only
 *                            for Custom group, only disable otherwise.
 *                            An icon in status placeholder always shown.
 *    Jacek P   1998-Oct-20 - Added new command in the SEARCH group for setting
 *                            of the Favorite Query (Todo: #2811).
 *    Jacek P   1998-Oct-21 - Added hints to command icons.
 *    Micke A   1998-Nov-25 - Improved HTML generation using CSS (ToDo: #2954).
 *    Jacek P   1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P   1999-Feb-17 - Extends ASPPageSubElement instead of ASPObject.
 *                            Class ASPCommandBarItem extends now ASPPageSubElement.
 *    Marek D   1999-Mar-02 - Implementation of ASPPoolElement state diagram
 *    Jacek P   1999-Mar-15 - Added method construct().
 *    Marek D   1999-Mar-19 - Removed calls to getConfigParameter() during request
 *    Marek D   1999-Apr-27 - Added verify() and scan()
 *    Marek D   1999-May-11 - Removed some 'new' during generation of HTML
 *    Jacek P   1999-May-26 - Added buttons 'toggle' and 'save'. Prepared for 'duplicate'.
 *    Jacek P   1999-Jun-09 - Added new boolean flag 'user_server_func'(and
 *                            function isUserDefinedServerFunction() ), which is true
 *                            if user has redefined the server function. Methods
 *                            disableCommand() and enableCommand() automatically
 *                            disables/enables DUPLICATE button with PREPARE.
 *    Jacek P   1999-Jun-18 - Tool tip text always shown.
 *    Jacek P   1999-Jul-16 - Added 'nowrap'.
 *    Johan S   1999-Aug-16 - Added code in getItemData for hover-buttons.
 *                            Also added immutable attributes in ASPCommandBarItems:
 *                            private string icon_enabled_flat, icon_disabled_flat
 *                            Further more, code was added in ASPCommandBarItem constructor
 *                            to set the filenames for the new hover buttons.
 *    Johan S   1999-Aug-18 - Moved hover_enabled check to ASPCommandBar construct
 *                            Added function getHoverEnabled.
 *    Jacek P   1999-Aug-30 - Hover flag changed to default 'N' in construct().
 *                            Should be fetched from ASPConfigFile .....
 *    Jacek P   1999-Sep-06 - Removed word 'record' from hints on browse buttons.
 *    Johan S   1999-Sep-13 - Added another, overloaded, function createImgTag.
 *                            This function adds height and width attributes to the
 *                            toolbar divider image.
 *    Jacek P   2000-Jan-21 - Implementation of portal:
 *                            call to ASPManager.readValue() replaced with ASPPage.readValue()
 *                            in getSelectedCustomCommand().
 *    Johan S   2000-Feb-Gui  Added new buttons for GUImura, added package available copies of the
 *                            Layout Modes and functions to fetch these modes. Added the function
 *                            adjustModes(). Also added the attribute userdisabled on the CommandBarItem, to
 *                            keep track of which function that has been disabled by the user and which has been
 *                            disabled by the Java code (adjustModes())
 *    Johan S   2000-Feb-10   Added attributes and functions in ASPCommandBarItem. Supporting enabling and
 *                            disabling of commmand on a rowlevel. valid_type_condition = inclusive or exclusive;
 *                            valid_values = command is valid when the valid_depended_column assumes or not
 *                            assumes one of these values.
 *    Stefan M  2000-Mar-16 - SINGLE_DESIGNED_LAYOUT changed to CUSTOM_LAYOUT. No buttons visible by default.
 *    Stefan M  2000-Mar-27 - Added conditionsTrue(); checks if row level conditions are true for the current row.
 *    Johan S   2000-Mar-29 - Added speciel browsertext for tablebrowsing.
 *    Johan S   2000-Apr-06 - Fixed some bugs in conditionsTrue() and actionButton(). Added disableCustomCommand().
 *                            removeCustomCommand() now calls disableCustomCommand() in version 3.
 *    Stefan M  2000-Apr-22 - Fixed visual bug when backward and forward are disabled.
 *    Stefan M  2000-May-11 - Added support for remembering state of collapsed block (ASPBlockLayout). Improved
 *                            visual space handling greatly (still problematic if block has a title).
 *    Stefan M  2000-May-14 - Added "Save" commandbaritem. Shown only if it has been enabled.
 *    Stefan M  2000-May-18 - Added enableModeLabel(),disableModeLabel(),isModeLabelEnabled().
 *    Stefan M  2000-May-20 - Moved "Copy" button to Action menu. Added setWidth(), getWidth() -
 *                            modifies the bar_width variable, which was obsolete. Added
 *                            confirmation dialog for "Delete" button.
 *    Stefan M  2000-May-30 - Row level conditions are not applied to the custom commands
 *                            when in multirow, only in single mode.
 *    Stefan M  2000-Jun-02 - client_function access level changed to package. Now immutable.
 *    Johan  S  2000-Jun-02 - Added browseUpdate();
 *    Stefan M  2000-Jul-05 - Added addCustomCommandSeparator().
 *    Stefan M  2000-Sep-26 - Added getAllCustomCommands(), which returns all custom commands, changed
 *                            getCustomCommands() to only return non-separator custom commands.
 *    Stefan M  2000-Oct-02 - conditionsTrue() can now handle number fields. Exceptions now handled.
 *    Stefan M  2000-Nov-01 - Now uses ASPManager.getTranslatedImageLocation() for pictures.
 *    Piotr  Z  2000-Nov-27 - Added move to first and last record functionality.
 *    Artur  K                Added getImageName() function which generate HTML code for
 *                            onmouseover and onmouseout events.
 *    Stefan M  2000-Dec-19 - Added Document Management support in action button.
 *    Jacek P   2000-Feb-05 - Class ASPCommandBarItem moved to its own file.
 *    Jacek P   2000-Mar-02 - Function setCustomCommandGroup() shouldn't throw exception.
 *    Artur K   2001-May-15 - Changes for handling buffer size declared in
 *                            ASPQuery.setBufferSize() function (log id 702)
 *    Mangala P 2001-Jun-09 - Fixed log id #777.
 *    Suneth M  2001-Sep-10 - Changed addContent() for remove actions button when
 *                            there are no rows in the rowset. (Log id #788)
 *    Suneth M  2001-Oct-12 - Undone the changes made by Log id #788.
 *    Ramila H  2001-Dec-31 - Correct PROJ call id 74117.
 *    Jacek P   2002-Feb-12 - Mode names not translatable in addContent(). Introduced new
 *                            translation keys: FNDCBRFINDMODE, FNDCBREDITMODE, FNDCBRNEWMODE
 *    Sampath   2002-Oct-18 - changed  addCommandValidConditions() to allow null values be compared in the condition
 *    ChandanaD 2002-Oct-28 - Made compatible with NE6 and above.
 *    Ramila H  2002-Oct-30 - Log Id 982. implemented code.
 *    Suneth M  2002-Nov-04 - Changed actionButton() to enable action button when custom groups exist.
 *    ChandanaD 2002-Nov-07 - Title Bar border corrected for NE6 and above.
 *    Ramila H  2002-Dec-13 - Log id 933. Added buttons to save and delete queries.
 *    Suneth M  2002-Dec-18 - Log id 1002. Changed addContent() to add a extra menu item for LOV's.
 *    ChandanaD 2002-Dec-18 - Log id 567, Changed showBar() method.
 *    ChandanaD 2002-Dec-30 - Changed actionButton() method. - to correct a  bug in log id 567
 *    Rifki R   2002-Dec-09 - Log id 1010, Added enforceCmdBarSecurity(), getSecurePlSqlMethod()
 *                            and overloaded defineItem() for auto security check.
 *    ChandanaD 2003-Feb-18 - Added new public method addCustomLinkCommand.
 *    ChandanaD 2003-Feb-21 - Overloaded addCustomLinkCommand() method.
 *    ChandanaD 2003-Mar-24 - Addded new public method disableCustomLinkcommand(String commandId).
 *                          - Changed findItem() method to support disabling of custom link commands.
 *    ChandanaD 2003-May-22 - Modified showBar() and addContent() method to format command bar for the new L&F.
 *    Ramila H  2003-Jun-04 - Added functionality to enable/disable IIDOK button.
 *    ChandanaD 2003-Jun-06 - Added onClick event function to the Actions menu to place the actions popup in a proper place.
 *    Rifki R   2003-Jun-26 - Log id 1081, Fixed Bug in Auto Security when ALIAS is used with view name.
 *    Chandana D2003-Jun-30 - Modified actionButton() method to support disabling of menu items in sub menus.
 *    Ramila H  2003-Jul-29 - Log id 1051, added code to remove custom commands from MultirowAction button.
 *    Chandana D2003-Sep-02 - Fixed a bug in disabling submenus.
 *    Ramila H  2003-Sep-04 - Changed = to ^ in addSaveQuery field value separater.
 *    Ramila H  2003-Sep-05 - removed cusmot group commands is isRemovedFromMultirowAction().
 *    Ramila H  2003-Oct-15 - Call id 107696 fixed.
 *    Ramila H  2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    Ramila H  2003-12-10  - Bug 1d 41451 corrected. Added method unsetRemoveFromMultirowAction too.
 *    ChandanaD 2004-Mar-23 - Merged with SP1.
 *    ChandanaD 2004-Apr-01 - Further Improvements for Multirow Actions
 *    ChandanaD 2004-Apr-09 - Added support for multirow delete. Added new public method removeMultirowDeleteButton().
 *    ChandanaD 2004-May-10 - Fixed a bug(table null) in actionButton().
 *    ChandanaD 2004-May-12 - Removed all dependencies on the SCHEME section of the webclientconfig.xml.
 *    ChandanaD 2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Chandana  2004-Jun-28 - Added new command "Row History".
 *    Ramila H  2004-08-03  - Added code to check PO secuirty of all methods given in addSecureCustomCommand.
 *    Chandana  2004-Aug-05 - Proxy support corrections.
 *    Chandana  2004-Sep-10 - Added new menu item "Layout Properties" for the Action button.
 *    Mangala   2004-Sep-13 - Added new method. addSecureCustomLinkCommand().
 *    Chandana  2004-Sep-14 - Reversed changes done for Layout Properties
 *    Chandana  2004-Nov-05 - Added new menu item "Layout Properties" for the Action button.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/03/26 sumelk Bug 89672, Changed addContent() & adjustModes() to enable first & last icons in multirow layout. 
 * 2010/01/21 sumelk Bug 87622, Changed forward() to reload the current rowset when browsing after deleting the records.
 * 2009/10/06 sumelk Bug 86330, Changed addContent() to show the correct row count after deleting in single row mode.
 * 2009/07/17 sumelk Bug 83713, Changed addContent() to enable next and previous icons correctly after deleting 
 *                   records in multirow mode.
 * 2009/05/25 amiklk Bug 83007, Changed addContent() to properly align the images on the top of command bar
 * 2009/04/08 sumelk Bug 82045, Changed addContent() to hide the row navigation icons in single layout when row count is zero.
 * 2009/02/13 buhilk Bug 80265, F1PR454 - Templates IID. Modified enforceCmdBarSecurity()
 * 2008/11/04 buhilk Bug 78215, Modified actionButton() inorder to add sub menu param for sent to submenu.
 * 2008/10/20 sumelk Bug 77802, Changed addContent().
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client.
 * 2008/09/19 sumelk Bug 77207, Changed addContent().
 * 2008/08/28 sumelk Bug 75713, Changed addContent(),first(),last(),forward() & backward(). 
 * 2008/06/26 mapelk - Bug 74852, Programming Model for Activities. 
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support rich command menus/table cells.
 * 2008/04/17 buhilk Bug 73041, Overloaded getAllCustomCommands() and changed the actionButton(), enforceCmdBarSecurity() methid.
 * 2008/04/04 buhilk Bug 72854, Added addCustomRWCLinkCommand() and modified enforceCmdBarSecurity() to support RWC link commands.
 * 2008/01/18 sadhlk Bug 69198, Added Custom Command-Bar Buttons functionality.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/11/30 sadhlk Bug 69691, Modified showBar() to enable Enter key for lov pages.
 * 2007/11/14 sadhlk Bug 69289, Modified actionButton() to correctly display custom commands when history disabled. 
 * 2007/10/08 sadhlk Merged Bug 67690, Changed addContent() to disable multirow delete button when delete method is overwritten.
 *                   Added new method addMultirowDeleteButton().
 * 2007/07/26 sadhlk Merged Bug 66811, Modified actionButton().  
 * 2007/07/16 sadhlk Bug 66470, Modified addContent().
 * 2007/07/03 sadhlk Merged Bug 64669, Modified addContent().  
 * 2007/06/20 buhilk Bug 64777, Modified addContent(), ajustModes() and added forceEnablePropertiesIcon()
 * 2007/05/10 buhilk Bug 65303, modified actionButton() to alert when no rows are selected.
 * 2007/02/27 buhilk Bug 63814, disabling history shows other commands.
 * 2007/01/30 buhilk Bug 63250, Improvements on Themeing.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * Revision 1.10 2006/09/13           gegulk 
 * Bug id 60473, Modified the method generateDialog() to set the proper alignment in RTL mode
 * Revision 1.9  2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 * Revision 1.8  2006/02/14        prralk
 * LCS Bug 55729 Command bar buttons not set for custom layout
 *
 * Revision 1.7  2005/12/21        mapelk
 * Call checkXXXXFields as a script function for SAVERETURN and SAVENEW
 *
 * Revision 1.6  2005/11/21  14:45 rahelk
 * fixed Call id 128794 - selectAll etc functionality to work
 *
 * Revision 1.5  2005/11/15 11:32:47  japase
 * ASPProfile.save() takes 'this' as argument.
 *
 * Revision 1.4  2005/11/10 10:21:06  rahelk
 * changes related to usages
 *
 * Revision 1.3  2005/09/23 08:40:27  mapelk
 * Merged from package 16
 *
 * Revision 1.2  2005/09/22 12:39:22  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.13.2.1  2005/09/21 12:40:15  mapelk
 * Fixed bug: RMBs added just after seperator dissapears.
 *
 * Revision 1.13  2005/08/16 05:11:41  mapelk
 * Remove CSL icon if no PO security granted
 *
 * Revision 1.12  2005/08/08 09:44:03  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.11  2005/07/01 15:26:52  rahelk
 * minor improvements to prepareProfileInfo
 *
 * Revision 1.10  2005/06/23 08:56:30  rahelk
 * CSL 2: disabled default values tab when New not available
 *
 * Revision 1.9  2005/06/09 09:40:35  mapelk
 * Added functionality to "Show pages in default layout mode"
 *
 * Revision 1.8  2005/06/06 07:22:27  rahelk
 * Save all profile objects together from CSL page
 *
 * Revision 1.7  2005/05/17 10:44:12  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.6  2005/05/04 05:31:59  rahelk
 * Layout profile support for groups
 *
 * Revision 1.5  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.4.2.1  2005/03/05 11:28:59  mapelk
 * bug fix: call id 122439
 *
 * Revision 1.4  2005/02/24 08:53:51  mapelk
 * Improved automatic security checks
 *
 * Revision 1.3  2005/02/08 08:21:57  rahelk
 * Fixed bug with dynamic behavior of Actionmenus with multirow select enabled
 *
 * Revision 1.2  2005/02/03 12:40:36  riralk
 * Adapted BlockProfile (saved queries) functionality to new profile changes.
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.5  2005/01/07 10:26:58  marese
 * Merged changes made on the PKG10 branch back to HEAD
 *
 * Revision 1.4  2005/01/03 05:16:05  rahelk
 *
 * Revision 1.3.2.3  2004/12/29 14:08:16  riralk
 * More Fixes to Profile
 *
 * Revision 1.3.2.2  2004/12/29 12:16:41  riralk
 * Fxied Call id 120932
 *
 * Revision 1.3.2.1  2004/12/29 10:13:43  rahelk
 * Call id 120785, Removed "old" code that showed single layout properties in the action popup
 *
 * Revision 1.3  2004/12/14 09:23:44  mapelk
 * Bug fix in Page Properties icon
 *
 * Revision 1.2  2004/12/10 10:09:13  riralk
 * New Block Layout profile GUI changes
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.webfeature.FndDataAdapter;
import java.util.*;

/**
 * A class used for adding one or more command bars to the page.
 * Typically a script will perform the following steps:<br>
 *<pre>
 * step 1 (For example in the "define" server function):
 *   o Create an instance of this class calling ASPBlock's newASPCommandBar().
 *   o Use the different methods to disable/remove specific commands or group of commands.
 *   o If more than one command bar will be used on the same page, the different blocks
 *     have to be named when the command bar is created. The name of the block
 *      will be used to identify the the commands when clicked.
 *   o Add any custom commands.
 * step 2 (Somewhere in the HTML code):
 *   o Use the showBar() method to receive the HTML code that will display
 *     the command bar.
 *</pre>
 */
public class ASPCommandBar extends ASPPageSubElement
{
   //==========================================================================
   //  Immutable attributes
   //==========================================================================

   private ASPRowSet rowset;
   private ASPBlockLayout lay;
   private ASPCommandBarItem[] items;
   private int count;
   private ASPPopup popup;

   private Vector custom_groups = new Vector();
   private Vector custom_labels = new Vector();

   //==========================================================================
   //  Mutable attributes
   //==========================================================================


   private int     bar_width;                   private int     pre_bar_width;
   private int     bar_inner_width;             private int     pre_bar_inner_width;

   private int     bar_height;                  private int     pre_bar_height;

   private boolean cmd_group_layout_enabled;    private boolean pre_cmd_group_layout_enabled;
   private boolean cmd_group_edit_enabled;      private boolean pre_cmd_group_edit_enabled;
   private boolean cmd_group_search_enabled;    private boolean pre_cmd_group_search_enabled;
   private boolean cmd_group_browse_enabled;    private boolean pre_cmd_group_browse_enabled;
   private boolean cmd_group_auto_enabled;     private boolean pre_cmd_group_auto_enabled; //!!!
   private boolean counter_db_mode;             private boolean pre_counter_db_mode;
   private boolean upper_border_line_enabled;   private boolean pre_upper_border_line_enabled;
   private boolean lower_border_line_enabled;   private boolean pre_lower_border_line_enabled;
   private boolean record_status_info_enabled;  private boolean pre_record_status_info_enabled;

   private boolean minimize_enabled;            private boolean pre_minimize_enabled;
   private boolean multirow_action_enabled;     private boolean pre_multirow_action_enabled;
   private boolean page_controls_action_button; private boolean pre_page_controls_action_button;

   private boolean mode_label_enabled;          private boolean pre_mode_label_enabled;

   private String  search_url;                  private String  pre_search_url;
   
   //==========================================================================
   //  Immutable contsants
   //==========================================================================

   // String constants used for the visual text labels on the command bar.
   private boolean multirow_delete_removed;
   private boolean multirow_delete_enabled;
   private String cmd_label_toggle;
   private String cmd_label_new;
   private String cmd_label_duplicate;
   private String cmd_label_edit;
   private String cmd_label_remove;
   private String cmd_label_submit;

   private String cmd_label_clear;
   private String cmd_label_count;
   private String cmd_label_search;
   private String cmd_label_favorite;
   private String cmd_label_record;
   private String cmd_label_perform;

   private String cmd_label_first;
   private String cmd_label_previous;
   private String cmd_label_next;
   private String cmd_label_last;

   private String cmd_label_find; //!!!

   private String cmd_label_of;

   private String cmd_label_save;

   private String cmd_hint_toggle;
   private String cmd_hint_new;
   private String cmd_hint_duplicate;
   private String cmd_hint_edit;
   private String cmd_hint_remove;
   private String cmd_hint_submit;

   private String cmd_hint_clear;
   private String cmd_hint_count;
   private String cmd_hint_search;
   private String cmd_hint_favorite;
   private String cmd_hint_record;
   private String cmd_hint_perform;

   private String cmd_hint_first;
   private String cmd_hint_previous;
   private String cmd_hint_next;
   private String cmd_hint_last;

    private String cmd_hint_find;
    private String cmd_hint_okfind;
    private String cmd_hint_countfind;
    // Added by Terry 20130918
    // Advanced Query
    private String cmd_hint_advancedfind;
    // Added end
    private String cmd_hint_cancelfind;
    private String cmd_hint_delete;
    private String cmd_hint_editrow;
    
    // Added by Terry 20130315
    // Edit command in overview
    private String cmd_hint_overviewedit;
    private String cmd_hint_overviewnew;
    private String cmd_hint_overviewdelete;
    private String cmd_hint_overviewsave;
    private String cmd_hint_overviewcancel;
    // Added end
    
    // Added by Terry 20120816
    // Approve row bar items
    private String cmd_hint_approvestart;
    private String cmd_hint_approverow;
    private String cmd_hint_approvereturn;
    private String cmd_hint_approvecancel;
    private String cmd_hint_approveview;
    private String cmd_hint_approvesendview;
    private String cmd_hint_approvesendviewfin;
    // Added end
    
    // Added by Terry 20131001
    // Command bar in DocEdmControlVue page
    private String cmd_hint_filesave;
    private String cmd_hint_filesaveall;
    private String cmd_hint_fileprint;
    private String cmd_hint_fileprintall;
    // Added end
    
    // Added by Terry 20120929
    // Lov bar in multi-select mode
    private String cmd_hint_oklov;
    // Added end
    
    // Added by Terry 20130902
    // Add document reference bar
    private String cmd_hint_docref;
    // Added end
    
    private String cmd_hint_viewdetails;
    private String cmd_hint_newrow;
    private String cmd_hint_savereturn;
    private String cmd_hint_savenew;
    private String cmd_hint_cancelnew;
    private String cmd_hint_canceledit;
    private String cmd_hint_back;
    private String cmd_hint_duplicaterow;
    private String cmd_hint_backward;
    private String cmd_hint_forward;
    private String cmd_hint_rowhistory;

    private String cmd_hint_save;

    private String cmd_hint_properties;
    private String cmd_hint_notes;

   // Configuration data for icons
   //private String  image_location;
   //private String  base_image_location;

   private boolean icons_enabled;
   private String  icon_alignment;
   private int     icon_height;
   private int     icon_width;
   private String  status_icon;
   private boolean hover_enabled;
   
   // Added by Terry 20131007
   // Show frame space mark
   private boolean cmd_show_frame_space;
   // Added end

   // html names and tags
   private String html_field_goto_name;
   private String html_combo_perform_name;
   private String img_divider_tag;

   int UNDEFINED     = ASPBlockLayout.UNDEFINED; //To disable a block use NONE instead.
   int NEW_LAYOUT    = ASPBlockLayout.NEW_LAYOUT;
   int EDIT_LAYOUT   = ASPBlockLayout.EDIT_LAYOUT;
   int FIND_LAYOUT   = ASPBlockLayout.FIND_LAYOUT;
   int MULTIROW_LAYOUT = ASPBlockLayout.MULTIROW_LAYOUT;
   int SINGLE_LAYOUT   = ASPBlockLayout.SINGLE_LAYOUT;
   int NONE            = ASPBlockLayout.NONE;
   int CUSTOM_LAYOUT   = ASPBlockLayout.CUSTOM_LAYOUT;

   //==========================================================================
   //  Transient temporary variables
   //==========================================================================

   transient CommandBarProfile profile;     transient CommandBarProfile pre_profile;
   private transient boolean    user_profile_prepared;

   private AutoString tmpbuf    = new AutoString();
   private AutoString tmpsubbuf = new AutoString();


   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPCommandBar");

   // Constants to be used for command identification.
   public  static final String TOGGLE       = "Toggle";
   public  static final String PREPARE      = "Prepare";
   public  static final String DUPLICATE    = "Duplicate";
   public  static final String EDIT         = "Edit";
   public  static final String REMOVE       = "Remove";
   public  static final String SUBMIT       = "Submit";
   public  static final String CLEAR        = "Clear";
   public  static final String COUNT        = "Count";
   public  static final String SEARCH       = "Search";
   public  static final String FAVORITE     = "Favorite";
   public  static final String FIRST        = "First";
   public  static final String PREVIOUS     = "Previous";
   public  static final String NEXT         = "Next";
   public  static final String LAST         = "Last";
   public  static final String GOTO         = "Goto";
   public  static final String PERFORM      = "Perform";
   private static final String FIELDGOTO    = "FieldGoto";
   private static final String FIELDTOTROW  = "Fieldtotrow";
           static final String FIELDPERFORM = "Fieldperform";

   public  static final String FIND         = "Find";
   public  static final String CANCELFIND   = "Cancelfind";
   public  static final String COUNTFIND    = "CountFind";
   // Added by Terry 20130918
   // Advanced Query
   public  static final String ADVANCEDFIND = "AdvancedFind";
   // Added end
   public  static final String OKFIND       = "OkFind";
   public  static final String DELETE       = "Delete";
   public  static final String EDITROW      = "EditRow";
   public  static final String VIEWDETAILS  = "ViewDetails";
   public  static final String NEWROW       = "NewRow";
   public  static final String SAVERETURN   = "SaveReturn";
   public  static final String SAVENEW      = "SaveNew";
   public  static final String CANCELNEW    = "CancelNew";
   public  static final String CANCELEDIT   = "CancelEdit";
   public  static final String BACK         = "Back";
   
   // Added by Terry 20130315
   // Edit command in overview
   public  static final String OVERVIEWEDIT    = "OverviewEdit";
   public  static final String OVERVIEWNEW     = "OverviewNew";
   public  static final String OVERVIEWDELETE  = "OverviewDelete";
   public  static final String OVERVIEWSAVE    = "OverviewSave";
   public  static final String OVERVIEWCANCEL  = "OverviewCancel";
   // Added end
   
   // Added by Terry 20120816
   // Approve row bar item
   public  static final String APPROVESTART    = "ApproveStart";
   public  static final String APPROVEROW      = "ApproveRow";
   public  static final String APPROVERETURN   = "ApproveReturn";
   public  static final String APPROVECANCEL   = "ApproveCancel";
   public  static final String APPROVEVIEW     = "ApproveView";
   public  static final String APPROVESENDVIEW = "ApproveSendView";
   public  static final String APPROVESENDVIEWFIN = "ApproveSendViewFin";
   // Added end
   
   // Added by Terry 20131001
   // Command bar in DocEdmControlVue page
   public  static final String FILESAVE        = "FileSave";
   public  static final String FILESAVEALL     = "FileSaveAll";
   public  static final String FILEPRINT       = "FilePrint";
   public  static final String FILEPRINTALL    = "FilePrintAll";
   // Added end
   
   // Added by Terry 20120929
   // Lov bar in multi-select mode
   public  static final String OKLOV           = "OkLov";
   // Added end
   
   // Added by Terry 20130902
   // Add document reference bar
   public  static final String DOCREF          = "DocRef";
   // Added end
   
   public  static final String DUPLICATEROW = "DuplicateRow";
   public  static final String FORWARD      = "Forward";
   public  static final String BACKWARD     = "Backward";
   public  static final String ROWHISTORY   = "RowHistory";

   public  static final String SAVE         = "Save";  // multirow save

   private static final String NameHtmlFieldGoto    = "GOTO";
   private static final String NameHtmlComboPerform = "PERFORM";

   public static final String OKIID = "OkIID";
   public static final String PROPERTIES    = "Properties";  // page layout properties
   public static final String NOTES    = "Notes";  // page Notes


   // Constants to be used for command groups identification.
   public static final int CMD_GROUP_EDIT    = 1;
   public static final int CMD_GROUP_SEARCH  = 2;
   public static final int CMD_GROUP_BROWSE  = 3;
   public static final int CMD_GROUP_CUSTOM  = 4;
   public static final int CMD_GROUP_LAYOUT  = 5;
   public static final int CMD_GROUP_AUTO    = 6;
   public static final int CMD_GROUP_APPROVE = 7;
   public static final int CMD_GROUP_FILE    = 8;

   private static final String img_divider          = "toolbardivider.gif";
   private static final String img_first            = "first.gif";
   private static final String img_prev             = "previous.gif";
   private static final String img_next             = "next.gif";
   private static final String img_last             = "last.gif";

   // Images used 3-d effects (optional) surrounding the commandbar.
   private static final String img_edge_topleft     = "topleftedge.gif";
   private static final String img_edge_top         = "topedge.gif";
   private static final String img_edge_topright    = "toprightedge.gif";
   private static final String img_edge_right       = "rightedge.gif";
   private static final String img_edge_bottomright = "bottomrightedge.gif";
   private static final String img_edge_bottom      = "bottomedge.gif";
   private static final String img_edge_bottomleft  = "bottomleftedge.gif";
   private static final String img_edge_left        = "leftedge.gif";

   private static final String img_ifs_logo         = "ifssmall.gif";

   private static final String img_delete_query     = "delete.gif";
   private static final String img_save_query       = "save.gif";
   public  static final String DELETEQUERY          = "DeleteQuery";
   public  static final String SAVEQUERY            = "SaveQuery";
   private String cmd_hint_deletequery;
   private String cmd_hint_savequery;

   private static final String  STYLESHEET_CLASS_CMDBAR = "CmdBar";

   public static final String  VALID_CONDITION_ENABLE = "Enable";
   public static final String  VALID_CONDITION_DISABLE = "Disable";

   // Added by Terry 20130916
   // Command bar properties
   public static final String  CMD_PRO_VIEW         = "DYNAMIC_LOV_VIEW";    // Mandatory
   public static final String  CMD_PRO_VIEW_PARAMS  = "VIEW_PARAMS";         // Optional
   public static final String  CMD_PRO_DESC         = "FIELD";               // Optional
   public static final String  CMD_PRO_TITLE        = "TITLE";               // Optional
   public static final String  CMD_PRO_WHERE        = "WHERE";               // Optional
   public static final String  CMD_PRO_GROUP_BY     = "GROUP_BY";            // Optional
   public static final String  CMD_PRO_ORDER_BY     = "ORDER_BY";            // Optional
   public static final String  CMD_PRO_PARPA        = "PARENT_PARAMS";       // Mandatory
   public static final String  CMD_PRO_FIEPA        = "FIELD_PARAMS";        // Mandatory
   public static final String  CMD_PRO_TARG_PKG     = "TARGET_PKG";          // Mandatory
   public static final String  CMD_PRO_TARG_FIE     = "TARGET_FIELDS";       // Mandatory
   public static final String  CMD_PRO_TARG_FUN     = "TARGET_CUSTOM_FUNC";  // Optional
   public static final String  CMD_PRO_ADD_SRE_FIES = "ADD_WHERE_SRE_FIES";  // Optional
   public static final String  CMD_PRO_ADD_SRE_OPER = "ADD_WHERE_SRE_OPER";  // Optional
   public static final String  CMD_PRO_ADD_TAR_VIEW = "ADD_WHERE_TAR_VIEW";  // Optional
   public static final String  CMD_PRO_ADD_TAR_FIES = "ADD_WHERE_TAR_FIES";  // Optional
   public static final String  CMD_PRO_ADD_TAR_KEYS = "ADD_WHERE_TAR_KEYS";  // Optional
   // Added end
   
   //==========================================================================
   //  Constructors
   //==========================================================================

   ASPCommandBar( ASPBlock block )
   {
      super(block);
      rowset = block.getASPRowSet();
      lay = block.getASPBlockLayout();
   }

   ASPCommandBar construct()
   {
      try
      {
         ASPManager mgr = getASPManager();
         cmd_label_toggle    = mgr.translateJavaText("FNDCBRTOGGLE: Toggle");
         cmd_label_new       = mgr.translateJavaText("FNDCBRNEW: New");
         cmd_label_duplicate = mgr.translateJavaText("FNDCBRDUP: Duplicate");
         cmd_label_edit      = mgr.translateJavaText("FNDCBREDIT: Edit");
         cmd_label_remove    = mgr.translateJavaText("FNDCBRREMOVE: Remove");
         cmd_label_submit    = mgr.translateJavaText("FNDCBRSUBMIT: Save");
         cmd_label_clear     = mgr.translateJavaText("FNDCBRCLEAR: Clear");
         cmd_label_count     = mgr.translateJavaText("FNDCBRCOUNT: Count");
         cmd_label_search    = mgr.translateJavaText("FNDCBRSEARCH: Search");
         cmd_label_favorite  = mgr.translateJavaText("FNDCBRFAVORITE: Favorite");
         cmd_label_record    = mgr.translateJavaText("FNDCBRRECORD: Record");
         cmd_label_perform   = mgr.translateJavaText("FNDCBRPERFORM: Perform");

         cmd_label_first   = mgr.translateJavaText("FNDCBRFIRST: First");
         cmd_label_previous= mgr.translateJavaText("FNDCBRPREV: Previous");
         cmd_label_next    = mgr.translateJavaText("FNDCBRNEXT: Next");
         cmd_label_last    = mgr.translateJavaText("FNDCBRLAST: Last");

         cmd_label_of      = mgr.translateJavaText("FNDCBROF: of");

         cmd_label_save    = mgr.translateJavaText("FNDCBRSAVE: Save");

         cmd_hint_toggle    = mgr.translateJavaText("FNDCBRTOGGLEH: Toggle between modes");
         cmd_hint_new       = mgr.translateJavaText("FNDCBRNEWH: Create new record");
         cmd_hint_duplicate = mgr.translateJavaText("FNDCBRDUPH: Duplicate record");
         cmd_hint_edit      = mgr.translateJavaText("FNDCBREDITH: Edit record/details");
         cmd_hint_remove    = mgr.translateJavaText("FNDCBRREMOVEH: Remove marked records");
         cmd_hint_submit    = mgr.translateJavaText("FNDCBRSUBMITH: Save changes");
         cmd_hint_clear     = mgr.translateJavaText("FNDCBRCLEARH: Clear form");
         cmd_hint_count     = mgr.translateJavaText("FNDCBRCOUNTH: Count rows");
         cmd_hint_search    = mgr.translateJavaText("FNDCBRSEARCHH: Search/query");
         cmd_hint_favorite  = mgr.translateJavaText("FNDCBRFAVORITEH: Prepare for Add to Favorites");
         cmd_hint_record    = mgr.translateJavaText("FNDCBRRECORDH: Go to record");
         cmd_hint_perform   = mgr.translateJavaText("FNDCBRPERFORMH: Perform action");
         cmd_hint_find      = mgr.translateJavaText("FNDCBRFIND: Find"); //!!! %
         cmd_hint_cancelfind  = mgr.translateJavaText("FNDCBRCANCELFIND: Cancel"); //!!! %
         cmd_hint_okfind      = mgr.translateJavaText("FNDCBROKFIND: OK"); //!!! %
         cmd_hint_countfind   = mgr.translateJavaText("FNDCBRCOUNTFIND: Count"); //!!! %
         // Added by Terry 20130918
         // Advanced Query
         cmd_hint_advancedfind= mgr.translateJavaText("FNDCBRADVANCEDFIND: Advanced");
         // Added end
         cmd_hint_delete      = mgr.translateJavaText("FNDCBRDELETE: Delete"); //!!! %
         cmd_hint_editrow     = mgr.translateJavaText("FNDCBREDITROW: Edit"); //!!! %
         cmd_hint_viewdetails = mgr.translateJavaText("FNDCBRVIEWDETAILS: View details of this row"); //!!! %
         cmd_hint_newrow      = mgr.translateJavaText("FNDCBRNEWROW: New"); //!!! %
         cmd_hint_savereturn  = mgr.translateJavaText("FNDCBRSAVERETURN: Save and Return"); //!!! %
         cmd_hint_savenew     = mgr.translateJavaText("FNDCBRSAVENEW: Save and Create a new"); //!!! %
         cmd_hint_cancelnew   = mgr.translateJavaText("FNDCBRCANE: Cancel new"); //!!! %
         cmd_hint_canceledit  = mgr.translateJavaText("FNDCBRCAEDIT: Cancel edit"); //!!! %
         cmd_hint_back        = mgr.translateJavaText("FNDCBRETURN: Overview"); //!!! %//Kipelk Back -> Overview
         cmd_hint_duplicaterow = mgr.translateJavaText("FNDCBRDUPLICATEROW: Duplicate"); //!!! %
         cmd_hint_backward     = mgr.translateJavaText("FNDCBRBACKWARD: Move backward"); //!!! %
         cmd_hint_forward      = mgr.translateJavaText("FNDCBRFORWARD: Move forward"); //!!! %
         cmd_hint_rowhistory  = mgr.translateJavaText("FNDCBRROWHISTORY: Row History"); //!!! %
         
         // Added by Terry 20130315
         // Edit command in overview
         cmd_hint_overviewedit    = mgr.translateJavaText("FNDCBROVERVIEWEDIT: Overview Edit");
         cmd_hint_overviewnew     = mgr.translateJavaText("FNDCBROVERVIEWNEW: Overview New");
         cmd_hint_overviewdelete  = mgr.translateJavaText("FNDCBROVERVIEWDELETE: Overview Delete");
         cmd_hint_overviewsave    = mgr.translateJavaText("FNDCBROVERVIEWSAVE: Overview Save");
         cmd_hint_overviewcancel  = mgr.translateJavaText("FNDCBROVERVIEWCANCEL: Overview Cancel");
         // Added end
         
         // Added by Terry 20120816
         // Approve row bar item
         cmd_hint_approvestart    = mgr.translateJavaText("FNDCBRAPPROVESTART: Start Work Flow");
         cmd_hint_approverow      = mgr.translateJavaText("FNDCBRAPPROVEROW: Approve & Next");
         cmd_hint_approvereturn   = mgr.translateJavaText("FNDCBRAPPROVERETURN: Return Approval");
         cmd_hint_approvecancel   = mgr.translateJavaText("FNDCBRAPPROVECANCEL: Cancel Approval");
         cmd_hint_approveview     = mgr.translateJavaText("FNDCBRAPPROVEVIEW: View Work Flow");
         cmd_hint_approvesendview = mgr.translateJavaText("FNDCBRAPPROVESENDVIEW: Send View");
         cmd_hint_approvesendviewfin = mgr.translateJavaText("FNDCBRAPPROVESENDVIEWFIN: Send View Finished");
         // Added end
         
         // Added by Terry 20131001
         // Command bar in DocEdmControlVue page
         cmd_hint_filesave        = mgr.translateJavaText("FNDCBRFILESAVE: Download File");
         cmd_hint_filesaveall     = mgr.translateJavaText("FNDCBRFILESAVEALL: Download All Files");
         cmd_hint_fileprint       = mgr.translateJavaText("FNDCBRFILEPRINT: Print File");
         cmd_hint_fileprintall    = mgr.translateJavaText("FNDCBRFILEPRINTALL: Print All Files");
         // Added end

         // Added by Terry 20120929
         // Lov bar in multi-select mode
         cmd_hint_oklov           = mgr.translateJavaText("FNDCBRSENDSELECTVAL: Send Selected Values");
         // Added end
         
         // Added by Terry 20130902
         // Add document reference bar
         cmd_hint_docref          = mgr.translateJavaText("FNDCBRDOCREFERENCE: Document Reference");
         // Added end
         
         cmd_hint_first    = mgr.translateJavaText("FNDCBRFIRSTH: Move first");
         cmd_hint_previous = mgr.translateJavaText("FNDCBRPREVH: Previous");
         cmd_hint_next     = mgr.translateJavaText("FNDCBRNEXTH: Next");
         cmd_hint_last     = mgr.translateJavaText("FNDCBRLASTH: Move last");

         cmd_hint_save     = mgr.translateJavaText("FNDCBRSAVEH: Save changes");

         cmd_hint_deletequery = mgr.translateJavaText("FNDCBRDELETEQUERYH: Delete Query");
         cmd_hint_savequery   = mgr.translateJavaText("FNDCBRSAVEQUERYH: Save Query");

         cmd_hint_properties     = mgr.translateJavaText("FNDCBRPROPH: Properties");
         cmd_hint_notes          = mgr.translateJavaText("FNDCBRNOTES: Notes");

//         image_location = mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES","/&(APPLICATION/PATH)/common/images/");
          //image_location = mgr.getTranslatedImageLocation();
          //base_image_location = mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES","/&(APPLICATION/PATH)/common/images/");


         icons_enabled  = "Y".equals(mgr.getConfigParameter("COMMAND_BAR/ICONS/ENABLED","N"));
         hover_enabled  = "Y".equals(mgr.getConfigParameter("COMMAND_BAR/HOVER_ENABLED","N"));
         icon_alignment = mgr.getConfigParameter("COMMAND_BAR/ICONS/ALIGNMENT","absbottom");
         icon_height    = Integer.parseInt( mgr.getConfigParameter("COMMAND_BAR/ICONS/HEIGHT","22") );
         icon_width     = Integer.parseInt( mgr.getConfigParameter("COMMAND_BAR/ICONS/WIDTH", "24") );
         status_icon    = mgr.getConfigParameter("COMMAND_BAR/ICONS/STATUS",mgr.getConfigParameter("ROW_STATUS/IMAGE/NORMAL","row_normal.gif"));

         // Added by Terry 20131007
         // Show frame space mark
         cmd_show_frame_space = true;
         // Added end
         
         items = new ASPCommandBarItem[32];
         /*
         UNDEFINED = ASPBlockLayout.UNDEFINED;
         NEW_LAYOUT = ASPBlockLayout.NEW_LAYOUT;
         EDIT_LAYOUT = ASPBlockLayout.EDIT_LAYOUT;
         FIND_LAYOUT = ASPBlockLayout.FIND_LAYOUT;
         MULTIROW_LAYOUT = ASPBlockLayout.MULTIROW_LAYOUT;
         SINGLE_LAYOUT = ASPBlockLayout.SINGLE_LAYOUT;
         NONE = ASPBlockLayout.NONE;
         CUSTOM_LAYOUT = ASPBlockLayout.CUSTOM_LAYOUT;
         */


         // Was obsolete. In Webkit 3 normally zero - if set to something else,
         // use this as commandbar width.
//         bar_width  = 756; // Configurable.
//         bar_height = 28;

         cmd_group_layout_enabled = true;
         cmd_group_edit_enabled   = true;
         cmd_group_search_enabled = true;
         cmd_group_browse_enabled = true;
         cmd_group_auto_enabled = true; //!!!

         counter_db_mode = false;

         upper_border_line_enabled = false;
         lower_border_line_enabled = false;

         record_status_info_enabled = false;

         minimize_enabled = true;
         mode_label_enabled = true;

         if (icons_enabled)
            initImageBar();
         else
            initStandardBar();

         // Added by Terry 20120820
         // Disable approve bars default.
         if ("N".equals(mgr.getConfigParameter("COMMAND_BAR/APPROVE_DEFAULT_SHOW", "N")))
            disableCommandGroupExtra(CMD_GROUP_APPROVE);
         else
            enableCommandGroupExtra(CMD_GROUP_APPROVE);
         // Added end
         search_url = "";

         String blkprefix = "__" + getBlockPrefix(false).toUpperCase() + "_";
         html_field_goto_name    = blkprefix + NameHtmlFieldGoto;
         html_combo_perform_name = blkprefix + NameHtmlComboPerform;

         img_divider_tag = "&nbsp;"+createImgTag(img_divider,"absbottom",24,4)+"&nbsp;";

         popup = getASPPage().newASPPopup("action" + getBlock().getName(),this);



      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   //==========================================================================
   //  ASPPoolElement logic
   //==========================================================================


    // !!!
   protected void doActivate() throws FndException
   {
      if (DEBUG) debug(this+": doActivate()");
   }


   /**
    * Freeze this command bar and all its items
    */
   protected void doFreeze() throws FndException
   {
      pre_bar_width                  = bar_width;
      pre_bar_inner_width            = bar_inner_width;

      pre_bar_height                 = bar_height;

      pre_cmd_group_layout_enabled   = cmd_group_layout_enabled;
      pre_cmd_group_edit_enabled     = cmd_group_edit_enabled;
      pre_cmd_group_search_enabled   = cmd_group_search_enabled;
      pre_cmd_group_browse_enabled   = cmd_group_browse_enabled;
      pre_cmd_group_auto_enabled     = cmd_group_auto_enabled; //!!!
      pre_counter_db_mode            = counter_db_mode;
      pre_upper_border_line_enabled  = upper_border_line_enabled;
      pre_lower_border_line_enabled  = lower_border_line_enabled;
      pre_record_status_info_enabled = record_status_info_enabled;

      pre_mode_label_enabled        = mode_label_enabled;
      pre_minimize_enabled          = minimize_enabled;
      pre_multirow_action_enabled   = multirow_action_enabled;
      pre_page_controls_action_button = page_controls_action_button;

      pre_search_url                 = search_url;


      for( int i=0; i<count; i++ )
         items[i].freeze();
   }

   /**
    * Reset this command bar and all its items
    */
   protected void doReset() throws FndException
   {
      bar_width                  = pre_bar_width;
      bar_inner_width            = pre_bar_inner_width;

      bar_height                 = pre_bar_height;

      cmd_group_layout_enabled   = pre_cmd_group_layout_enabled;
      cmd_group_edit_enabled     = pre_cmd_group_edit_enabled;
      cmd_group_search_enabled   = pre_cmd_group_search_enabled;
      cmd_group_browse_enabled   = pre_cmd_group_browse_enabled;
      cmd_group_auto_enabled     = pre_cmd_group_auto_enabled; //!!!
      counter_db_mode            = pre_counter_db_mode;
      upper_border_line_enabled  = pre_upper_border_line_enabled;
      lower_border_line_enabled  = pre_lower_border_line_enabled;
      record_status_info_enabled = pre_record_status_info_enabled;

      mode_label_enabled         = pre_mode_label_enabled;
      minimize_enabled          = pre_minimize_enabled;
      multirow_action_enabled    = pre_multirow_action_enabled;

      search_url                 = pre_search_url;

      user_profile_prepared      = false;
      profile                    = pre_profile;

      for( int i=0; i<count; i++ )
         items[i].reset();
   }

   /**
    * Clone this command bar into a new command bar attached to the specified block.
    * The state of the new command bar is DEFINED.
    */
   protected ASPPoolElement clone( Object block ) throws FndException
   {
      ASPCommandBar b = new ASPCommandBar((ASPBlock)block);

      //b.rowset = rowset;  // remove this line!

      b.bar_width  = b.pre_bar_width  = pre_bar_width;
      b.bar_inner_width  = b.pre_bar_inner_width  = pre_bar_inner_width;

      b.bar_height = b.pre_bar_height = pre_bar_height;

      b.cmd_group_layout_enabled   = b.pre_cmd_group_layout_enabled   = pre_cmd_group_layout_enabled;
      b.cmd_group_edit_enabled     = b.pre_cmd_group_edit_enabled     = pre_cmd_group_edit_enabled;
      b.cmd_group_search_enabled   = b.pre_cmd_group_search_enabled   = pre_cmd_group_search_enabled;
      b.cmd_group_browse_enabled   = b.pre_cmd_group_browse_enabled   = pre_cmd_group_browse_enabled;
      b.cmd_group_auto_enabled   = b.pre_cmd_group_auto_enabled   = pre_cmd_group_auto_enabled;  //!!!
      b.counter_db_mode            = b.pre_counter_db_mode            = pre_counter_db_mode;
      b.upper_border_line_enabled  = b.pre_upper_border_line_enabled  = pre_upper_border_line_enabled;
      b.lower_border_line_enabled  = b.pre_lower_border_line_enabled  = pre_lower_border_line_enabled;
      b.record_status_info_enabled = b.pre_record_status_info_enabled = pre_record_status_info_enabled;

      b.mode_label_enabled         = b.pre_mode_label_enabled        = pre_mode_label_enabled;

      b.minimize_enabled          = b.pre_minimize_enabled          = pre_minimize_enabled;
      b.multirow_action_enabled   = b.pre_multirow_action_enabled   = pre_multirow_action_enabled;
      b.page_controls_action_button   = b.pre_page_controls_action_button   = pre_page_controls_action_button;

      b.search_url                 = b.pre_search_url                 = pre_search_url;
      b.profile                    = b.pre_profile                    = pre_profile;

      b.cmd_label_toggle    = cmd_label_toggle;
      b.cmd_label_new       = cmd_label_new;
      b.cmd_label_duplicate = cmd_label_duplicate;
      b.cmd_label_edit      = cmd_label_edit;
      b.cmd_label_remove    = cmd_label_remove;
      b.cmd_label_submit    = cmd_label_submit;
      b.cmd_label_clear     = cmd_label_clear;
      b.cmd_label_count     = cmd_label_count;
      b.cmd_label_search    = cmd_label_search;
      b.cmd_label_favorite  = cmd_label_favorite;
      b.cmd_label_record    = cmd_label_record;
      b.cmd_label_perform   = cmd_label_perform;
      b.cmd_label_find      = cmd_label_find;

      b.cmd_label_first    = cmd_label_first;
      b.cmd_label_previous = cmd_label_previous;
      b.cmd_label_next     = cmd_label_next;
      b.cmd_label_last     = cmd_label_last;

      b.cmd_label_of       = cmd_label_of;

      b.cmd_label_save     = cmd_label_save;

      b.cmd_hint_toggle    = cmd_hint_toggle;
      b.cmd_hint_new       = cmd_hint_new;
      b.cmd_hint_duplicate = cmd_hint_duplicate;
      b.cmd_hint_edit      = cmd_hint_edit;
      b.cmd_hint_remove    = cmd_hint_remove;
      b.cmd_hint_submit    = cmd_hint_submit;
      b.cmd_hint_clear     = cmd_hint_clear;
      b.cmd_hint_count     = cmd_hint_count;
      b.cmd_hint_search    = cmd_hint_search;
      b.cmd_hint_favorite  = cmd_hint_favorite;
      b.cmd_hint_record    = cmd_hint_record;
      b.cmd_hint_perform   = cmd_hint_perform;
      b.cmd_hint_find      = cmd_hint_find;
      b.cmd_hint_okfind      = cmd_hint_okfind;
      b.cmd_hint_cancelfind  = cmd_hint_cancelfind;
      b.cmd_hint_countfind  = cmd_hint_countfind;
      // Added by Terry 20130918
      // Advanced Query
      b.cmd_hint_advancedfind = cmd_hint_advancedfind;
      // Added end
      b.cmd_hint_delete     = cmd_hint_delete;
      b.cmd_hint_editrow    = cmd_hint_editrow;
      
      // Added by Terry 20130315
      // Edit command in overview
      b.cmd_hint_overviewedit    = cmd_hint_overviewedit;
      b.cmd_hint_overviewnew     = cmd_hint_overviewnew;
      b.cmd_hint_overviewdelete  = cmd_hint_overviewdelete;
      b.cmd_hint_overviewsave    = cmd_hint_overviewsave;
      b.cmd_hint_overviewcancel  = cmd_hint_overviewcancel;
      // Added end
      
      // Added by Terry 20120816
      // Approve row bar items
      b.cmd_hint_approvestart    = cmd_hint_approvestart;
      b.cmd_hint_approverow      = cmd_hint_approverow;
      b.cmd_hint_approvereturn   = cmd_hint_approvereturn;
      b.cmd_hint_approvecancel   = cmd_hint_approvecancel;
      b.cmd_hint_approveview     = cmd_hint_approveview;
      b.cmd_hint_approvesendview = cmd_hint_approvesendview;
      b.cmd_hint_approvesendviewfin = cmd_hint_approvesendviewfin;
      // Added end
      
      // Added by Terry 20131001
      // Command bar in DocEdmControlVue page
      b.cmd_hint_filesave        = cmd_hint_filesave;
      b.cmd_hint_filesaveall     = cmd_hint_filesaveall;
      b.cmd_hint_fileprint       = cmd_hint_fileprint;
      b.cmd_hint_fileprintall    = cmd_hint_fileprintall;
      // Added end
      
      // Added by Terry 20120929
      // Lov bar in multi-select mode
      b.cmd_hint_oklov           = cmd_hint_oklov;
      // Added end
      
      // Added by Terry 20130902
      // Add document reference bar
      b.cmd_hint_docref          = cmd_hint_docref;
      // Added end
      
      b.cmd_hint_viewdetails  = cmd_hint_viewdetails;
      b.cmd_hint_newrow     = cmd_hint_newrow;
      b.cmd_hint_savereturn = cmd_hint_savereturn;
      b.cmd_hint_savenew    = cmd_hint_savenew;
      b.cmd_hint_cancelnew  = cmd_hint_cancelnew;
      b.cmd_hint_canceledit = cmd_hint_canceledit;
      b.cmd_hint_back       = cmd_hint_back;
      b.cmd_hint_duplicaterow = cmd_hint_duplicaterow;
      b.cmd_hint_backward       = cmd_hint_backward;
      b.cmd_hint_forward       = cmd_hint_forward;
      b.cmd_hint_rowhistory = cmd_hint_rowhistory;

      b.cmd_hint_first     = cmd_hint_first;
      b.cmd_hint_previous  = cmd_hint_previous;
      b.cmd_hint_next      = cmd_hint_next;
      b.cmd_hint_last      = cmd_hint_last;

      b.cmd_hint_save      = cmd_hint_save;
      b.cmd_hint_deletequery = cmd_hint_deletequery;
      b.cmd_hint_savequery   = cmd_hint_savequery;
      b.cmd_hint_properties   = cmd_hint_properties;
      b.cmd_hint_notes   = cmd_hint_notes;

      //b.image_location     = image_location;
      //b.base_image_location     = base_image_location;

      b.icons_enabled      = icons_enabled;
      b.icon_alignment     = icon_alignment;
      b.icon_height        = icon_height;
      b.icon_width         = icon_width;
      b.status_icon        = status_icon;

      // Added by Terry 20131007
      // Show frame space mark
      b.cmd_show_frame_space = cmd_show_frame_space;
      // Added end
      
      b.html_field_goto_name    = html_field_goto_name;
      b.html_combo_perform_name = html_combo_perform_name;
      b.img_divider_tag         = img_divider_tag;

      b.count = count;
      b.lay = b.getBlock().getASPBlockLayout();
      //b.lay = (ASPBlockLayout)lay.clone((ASPCommandBar)b);

      b.items = new ASPCommandBarItem[b.count];

      b.hover_enabled = this.hover_enabled;
      /*
      UNDEFINED = ASPBlockLayout.UNDEFINED;
      NEW_LAYOUT = ASPBlockLayout.NEW_LAYOUT;
      EDIT_LAYOUT = ASPBlockLayout.EDIT_LAYOUT;
      FIND_LAYOUT = ASPBlockLayout.FIND_LAYOUT;
      MULTIROW_LAYOUT = ASPBlockLayout.MULTIROW_LAYOUT;
      SINGLE_LAYOUT = ASPBlockLayout.SINGLE_LAYOUT;
      NONE = ASPBlockLayout.NONE;
      CUSTOM_LAYOUT = ASPBlockLayout.CUSTOM_LAYOUT;
      */
      b.popup              = b.getASPPage().getASPPopup("action" + getBlock().getName());
      b.multirow_delete_removed = multirow_delete_removed;
      b.multirow_delete_enabled = multirow_delete_enabled;

      for( int i=0; i<b.count; i++ )
         b.items[i] = (ASPCommandBarItem)items[i].clone(b);

      b.custom_groups = custom_groups;
      b.custom_labels = custom_labels;

      b.setCloned();
      return b;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
      for( int i=0; i<count; i++ )
         items[i].verifyPage(page);
      rowset.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
      for( int i=0; i<count; i++ )
         items[i].scan(page,level+1);
   }

   public String  getName()
   {
      return getBlock()==null ? "" : getBlock().getName();
   }

    //disable commands when the corresponding db object is not accessible

   private void enforceCmdBarSecurity()
   {

      Buffer commands = getBlock().getDefinedCommands();

      String method_name="";
      String view_name = getBlock().getDBView();

      if (!Str.isEmpty(view_name))
      {
        if (!getASPPage().isObjectAccessible(view_name))
        {
          disableCommand(ASPCommandBar.FIND);
          disableCommand(ASPCommandBar.OKFIND);
          disableCommand(ASPCommandBar.COUNTFIND);
          // Added by Terry 20130918
          // Advanced Query
          disableCommand(ASPCommandBar.ADVANCEDFIND);
          // Added end
        }
      }

      for(int j=0;j<commands.countItems();j++)
      {
         //get fully qualfied PL/SQL method name from commands buffer

         Item tmp = commands.findItem(commands.getItem(j).getName()+"/METHOD");
         method_name = (tmp==null?"":tmp.getString());

         FndDataAdapter adapter = getBlock().getDataAdapter();

         if ( "New__".equals(commands.getItem(j).getName()) && !Str.isEmpty(method_name) )
         {
            if (!getASPPage().isObjectAccessible(method_name) && !(adapter instanceof ifs.fnd.webfeature.FndSaveDataAdapter) && !getASPPage().isTemplatePages())
            {
               disableCommand(ASPCommandBar.NEWROW);
               disableCommand(ASPCommandBar.DUPLICATEROW);
               // Added by Terry 20130315
               // Edit command in overview
               disableCommand(ASPCommandBar.OVERVIEWNEW);
               // Added end
            }
         }
         else if ( "Modify__".equals(commands.getItem(j).getName()) && !Str.isEmpty(method_name) )
         {
            if (!getASPPage().isObjectAccessible(method_name) && !(adapter instanceof ifs.fnd.webfeature.FndSaveDataAdapter) && !getASPPage().isTemplatePages())
            {
               disableCommand(ASPCommandBar.EDITROW);
               // Added by Terry 20130315
               // Edit command in overview
               disableCommand(ASPCommandBar.OVERVIEWEDIT);
               // Added end
            }
         }
         else if ( "Remove__".equals(commands.getItem(j).getName()) && !Str.isEmpty(method_name) )
         {
            if (!getASPPage().isObjectAccessible(method_name) && !(adapter instanceof ifs.fnd.webfeature.FndSaveDataAdapter) && !getASPPage().isTemplatePages())
            {
               disableCommand(ASPCommandBar.DELETE);
               // Added by Terry 20130315
               // Edit command in overview
               disableCommand(ASPCommandBar.OVERVIEWDELETE);
               // Added end
            }
         }
      }

      //check security for custom commands added using cmdbar.addSecusreCustomCommand()
      Vector cust_cmds = getBlock().getASPCommandBar().getAllCustomCommands(false);
      String cmd_id;
      ASPCommandBarItem cmd;

      for (int i=0; i<cust_cmds.size(); i++)
      {
           cmd = (ASPCommandBarItem) cust_cmds.elementAt(i);
           cmd_id = cmd.getCommandId();
           if (!getASPPage().isObjectAccessible(cmd.getSecurePlSqlMethod()))
              disableCustomCommand(cmd_id);
           if (cmd_id.startsWith("LINK_CMD_RWC") && !cmd_id.startsWith("LINK_CMD_RWC_DUAL") && !getASPManager().isRWCHost() && !getASPManager().showRWCLinksInBrowser())
              disableCustomCommand(cmd_id);
           if(!getASPManager().isRWCHost() && cmd_id.startsWith("LINK_CMD_RWC_DUAL"))
              disableCustomCommand(cmd_id);
           if(getASPManager().isRWCHost() && cmd_id.startsWith("LINK_CMD_DUAL"))
              disableCustomCommand(cmd_id);
      }
      
      // Added by Terry 20130902
      // Add document reference bar, check document reference visiable
      if (!getASPManager().isDocConnectionAware(getBlock().getDocManView()))
         disableCommand(ASPCommandBar.DOCREF);
      // Added end
   }


   //==========================================================================
   //  Public.
   //==========================================================================

   /**
    * Returns the HTML code that will display the command bar.
    */
   public String showBar() //!!!
   {
      if(getASPManager().isMobileVersion())
         return showMobileBar();

     // if(getASPPage().getASPLov()==null)  //added default commad for LOVs
     // {
         getASPPage().increaseBlockCount();
         getASPPage().setDefaultCommand(lay.getDefaultCommand(lay.getLayoutMode()));
         getASPPage().setDefaultBlock(lay.getName());
     // }
      int i;
      try
      {

         if (!( lay.isCustomLayout() || (lay.getLayoutMode() == lay.NONE) ))
           enforceCmdBarSecurity();

         tmpbuf.clear();
//         if(upper_border_line_enabled)
//            tmpbuf.append("\n<hr>");

         //page frame start
         tmpbuf.append("<table cellspacing=0 cellpadding=0 border=0 width=100%>");
         tmpbuf.append("<tr>");
         // Added by Terry 20131007
         // Show frame space mark
         if (cmd_show_frame_space)
            tmpbuf.append("<td>&nbsp;&nbsp;</td>");
         // Added end
         tmpbuf.append("<td width=100%>");
         addContent(tmpbuf);
         //page frame end
         tmpbuf.append("</td>");
         // Added by Terry 20131007
         // Show frame space mark
         if (cmd_show_frame_space)
            tmpbuf.append("<td>&nbsp;&nbsp</td>");
         // Added end
         tmpbuf.append("</tr></table>");

//         if(lower_border_line_enabled)
//            tmpbuf.append("\n<hr>");

         return tmpbuf.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Disables a specific command, given the command identifier string.
    * The corresponding label will appear on the bar, but it will not be hyperlinked.
    * Individual commands may not be visually removed, but only a complete group.
    * @see     ifs.fnd.asp.ASPCommandBar#enableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#removeCommandGroup
    */
   public void disableCommand(String commandId)
   {
      try
      {
         findItem(commandId).disable();
         if(getASPPage().getVersion()>=3)
             findItem(commandId).userDisable();
         if( PREPARE.equals(commandId) )
            findItem(DUPLICATE).disable();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Enables a specific command, given the command identifier string.
    * A call to this method will "undo" a prior call to the "disableCommand" method,
    * given the same command identifier string.
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommand
    */
   public void enableCommand(String commandId)
   {
      try
      {
         findItem(commandId).enable();
         if(getASPPage().getVersion()>=3)
             findItem(commandId).userEnable();
         if( PREPARE.equals(commandId) )
            findItem(DUPLICATE).enable();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disables a whole group of commands, given the command group identifier.
    * The corresponding command labels will appear on the bar, but will not be hyperlinked.
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_LAYOUT
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_EDIT
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_SEARCH
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_BROWSE
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_CUSTOM
    * @see     ifs.fnd.asp.ASPCommandBar#enableCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommand
    */
   public void disableCommandGroup(int commandGroup)
   {
      boolean commandGroupFound = false;
      try
      {
         int i = 0;
         while (i<=count-1)
         {
            if(items[i].getCommandGroup()==commandGroup)
            {
               //disableCommand(items[i].getCommand());
               items[i].disable();
               commandGroupFound = true;
            }
            i++;
         }
         if(!commandGroupFound)
            throw commandGroupNotFound(commandGroup);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Added by Terry 20120820
   // Disable Command Group Extra
   // disable and user disable
   public void disableCommandGroupExtra(int commandGroup)
   {
      boolean commandGroupFound = false;
      try
      {
         int i = 0;
         while (i<=count-1)
         {
            if(items[i].getCommandGroup()==commandGroup)
            {
               //disableCommand(items[i].getCommand());
               items[i].disable();
               items[i].userDisable();
               commandGroupFound = true;
            }
            i++;
         }
         if(!commandGroupFound)
            throw commandGroupNotFound(commandGroup);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end

   /**
    * A call to this method will "undo" a prior call to the "disableCommandGroup" method,
    * given the same command group identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommandGroup
    */
   public void enableCommandGroup(int commandGroup)
   {
      boolean commandGroupFound = false;
      try
      {
         int i = 0;
         while (i<=count-1)
         {
            if(items[i].getCommandGroup()==commandGroup)
            {
               items[i].enable();
               commandGroupFound = true;
            }
            i++;
         }
         if(!commandGroupFound)
            throw commandGroupNotFound(commandGroup);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Added by Terry 20120820
   // Enable Command Group Extra
   // enable and user enable
   public void enableCommandGroupExtra(int commandGroup)
   {
      boolean commandGroupFound = false;
      try
      {
         int i = 0;
         while (i<=count-1)
         {
            if(items[i].getCommandGroup()==commandGroup)
            {
               items[i].enable();
               items[i].userEnable();
               commandGroupFound = true;
            }
            i++;
         }
         if(!commandGroupFound)
            throw commandGroupNotFound(commandGroup);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end

   /**
    * Removes a whole group of commands from the bar, given the command group identifier.
    * The corresponding command labels will not appear on the bar.
    * Only command groups may be removed from the bar.
    * Individual commands may only be disabled (=>visible but not hyperlinked).
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_LAYOUT
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_EDIT
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_SEARCH
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_BROWSE
    * @see     ifs.fnd.asp.ASPCommandBar#CMD_GROUP_CUSTOM
    * @see     ifs.fnd.asp.ASPCommandBar#unRemoveCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommand
    */
   public void removeCommandGroup(int commandGroup)
   {
/* GUIMURA
      if (commandGroup != CMD_GROUP_CUSTOM)
      {
         disableCommandGroup(commandGroup);
         return;
      }
*/
      boolean commandGroupFound = false;
      try
      {
         int i = 0;
         while (i<=count-1)
         {
            if(items[i].getCommandGroup()==commandGroup)
            {
               items[i].remove();
               commandGroupFound = true;
            }
            i++;
         }
         if(!commandGroupFound)
            throw commandGroupNotFound(commandGroup);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * A call to this method will "undo" a prior call to the "removeCommandGroup" method,
    * given the same command group identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#removeCommandGroup
    */
   public void unRemoveCommandGroup(int commandGroup)
   {
      if (commandGroup != CMD_GROUP_CUSTOM)
      {
         enableCommandGroup(commandGroup);
         return;
      }

      boolean commandGroupFound = false;
      try
      {
         int i = 0;
         while (i<=count-1)
         {
            if(items[i].getCommandGroup()==commandGroup)
            {
               items[i].unRemove();
               commandGroupFound = true;
            }
            i++;
         }
         if(!commandGroupFound)
            throw commandGroupNotFound(commandGroup);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to redefine the server function connected to a existing standard or custom command.
    * A client side function can also be connected to the command using this method.
    * The execution of a command with both a client and server function will be as follows:<p>
    *
    * 1. When the user clicks the hyperlink the client side function "commandSet" will be called.
    *    The command identifier and the name of an eventual custom client function is supplied.<br>
    * 2. An eventual custom client function will be called by the "setCommand" function.<br>
    * 3. If the custom client function returns "true", "setCommand" will check if the server function
    *    connected to the command has been redefined. If it has, the redefined name will be
    *    copied to a hidden form variable, otherwise the standard server function name will.
    * 4. The "setCommand" function will then submit the HTML form.
    * 5. The receiving ASP script will look for a function name in the hidden form variable.
    *    If a function name is found, that function will be called.
    * 6. The command has been executed.
    */
   public void defineCommand(String commandId, String serverFunction, String clientFunction)
   {
      ASPCommandBarItem item;
      try
      {
         item = this.findItem(commandId);
         if(serverFunction!=null)
            item.setServerFunction(serverFunction);
         if(clientFunction!=null)
            item.setClientFunction(clientFunction);

//            item.setScriptFunction(clientFunction);

      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to redefine the server function connected to a command,
    * without supplying any client function name.
    */
   public void defineCommand(String commandId, String serverFunction)
   {
      try
      {
         this.defineCommand(commandId,serverFunction, null);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Returns the name of the custom server function defined on the command.
    * If no custom server function has been defined, the standard server function will be returned.
    * @see     ifs.fnd.asp.ASPCommandBar#defineCommand
    */
   public String getServerFunction(String commandId)
   {
      try
      {
         return this.findItem(commandId).getServerFunction();
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }

   /**
    * Use this method to add one or many custom commands.
    * The added commands will appear in a combo box, to the right
    * of the browse command group. When a command has been added,
    * the other command-manipulating methods can be applied.
    * If no custom command is added, the corresponding items
    * on the bar will not be shown.
    * @see     ifs.fnd.asp.ASPCommandBar#defineCommand
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#enableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#removeCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#unRemoveCommandGroup
    * @param commandId command identifier
    * @param lable display name of the command
    */
   public void addCustomCommand(String commandId, String label)
   {
      addCustomCommand(commandId, label, null);
   }
   
   /**
    * Use this method to add one or many custom commands.
    * When a command has been added,the other command-manipulating 
    * methods can be applied. If no custom command is added, 
    * the corresponding items on the bar will not be shown.
    * @see     ifs.fnd.asp.ASPCommandBar#defineCommand
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#enableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#removeCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#unRemoveCommandGroup
    * @param commandId command identifier
    * @param lable display name of the command
    * @param iconImg image path for the command icon
    */   
   public void addCustomCommand(String commandId, String label, String imageUrl)
   {
      try
      {
         label = getASPManager().translate(label, getASPPage());
         defineItem( label, imageUrl, commandId, CMD_GROUP_CUSTOM, false,  false, true, false );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Added by Terry 20140930
   // Adding confirm msg of command bar item
   public void addCustomCommandWithConfirm(String commandId, String label, String confirm_msg)
   {
      addCustomCommand(commandId, label, null, confirm_msg);
   }
   
   // Adding confirm msg of command bar item
   public void addCustomCommand(String commandId, String label, String imageUrl, String confirm_msg)
   {
      try
      {
         label = getASPManager().translate(label, getASPPage());
         defineItem( label, imageUrl, commandId, CMD_GROUP_CUSTOM, false,  false, true, false, null, null, confirm_msg );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end
   
   /**
    * Use this method to add one or many custom commands.
    * The added commands will appear in commnad bar, to the left
    * of the standard commands. When a command has been added,
    * the other command-manipulating methods can be applied.
    * If no custom command is added, the corresponding items
    * on the bar will not be shown.
    * @see     ifs.fnd.asp.ASPCommandBar#defineCommand
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#enableCommand
    * @see     ifs.fnd.asp.ASPCommandBar#disableCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#removeCommandGroup
    * @see     ifs.fnd.asp.ASPCommandBar#unRemoveCommandGroup
    */
   public void addCustomCommand(String commandId, String label, String imageUrl, boolean commandbar_button)
   {
      try
      {
         label = getASPManager().translate(label, getASPPage());
         defineItem( label,imageUrl, commandId, CMD_GROUP_CUSTOM, false,  false, true, commandbar_button );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Added by Terry 20140930
   // Adding confirm msg of command bar item
   public void addCustomCommand(String commandId, String label, String imageUrl, String confirm_msg, boolean commandbar_button)
   {
      try
      {
         label = getASPManager().translate(label, getASPPage());
         defineItem(label, imageUrl, commandId, CMD_GROUP_CUSTOM, false, false, true, commandbar_button, null, null, confirm_msg);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end

   /**
    * Adds a link to another page as a custom command.
    * Page will be opened in a new browser window.
    * No query string paramaters will be added to the URL.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    */
   public void addCustomLinkCommand(String commandId, String label,String url)
   {
      addCustomLinkCommand(commandId, label, url, "", true);
   }
   
   // Added by Terry 20141003
   // Adding confirm msg of command bar item
   public void addCustomLinkCommandWithConfirm(String commandId, String label, String url, String confirm_msg)
   {
      addSecureCustomLinkCommand(commandId, label, url, "", true, null, null, confirm_msg);
   }
   // Added end
   
   /**
    * Adds a link with an icon to another page as a custom command.
    * Page will be opened in a new browser window.
    * No query string paramaters will be added to the URL.
    * @param commandId A unique command identifier.
    * @param imageIncluded availability of the command icon.
    * @param image path to the image file used as the icon.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    */
   public void addCustomLinkCommand(String commandId, boolean imageIncluded, String image, String label,String url)
   {
      if(imageIncluded)
         addCustomLinkCommand(commandId, imageIncluded, image, label, url, true);
      else
         addCustomLinkCommand(commandId, label, url);
   }

   /**
    * Adds a link to another page as a custom command.
    * Page will be opened in a new browser window.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param field_list Fields to be added to the query string.
    */
   public void addCustomLinkCommand(String commandId, String label,String url, String field_list)
   {
      addCustomLinkCommand(commandId, label, url, field_list, true);
   }
   
   // Added by Terry 20141003
   // Adding confirm msg of command bar item
   public void addCustomLinkCommand(String commandId, String label, String url, String field_list, String confirm_msg)
   {
      addSecureCustomLinkCommand(commandId, label, url, field_list, true, null, null, confirm_msg);
   }
   // Added end

   /**
    * Adds a link with an icon to another page as a custom command.
    * Page will be opened in a new browser window.
    * @param commandId A unique command identifier.
    * @param imageIncluded availability of the command icon.
    * @param image path to the image file used as the icon.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param field_list Fields to be added to the query string.
    */
   public void addCustomLinkCommand(String commandId, boolean imageIncluded, String image, String label,String url, String field_list)
   {
      if(imageIncluded)
         addCustomLinkCommand(commandId, imageIncluded, image, label, url, field_list, true);
      else
         addCustomLinkCommand(commandId, label, url, field_list);
   }
   
   /**
    * Adds a link to another page as a custom command.
    * Page will be opened in a new browser window.
    * No query string paramaters will be added to the URL.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param newwin 'true' will cause to open the page in a new browser window and 'false' will open in the same window.
    */
   public void addCustomLinkCommand(String commandId, String label,String url, boolean newwin)
   {
      addCustomLinkCommand(commandId, label, url, "", newwin);
   }

   /**
    * Adds a link with an icon to another page as a custom command.
    * Page will be opened in a new browser window.
    * No query string paramaters will be added to the URL.
    * @param commandId A unique command identifier.
    * @param imageIncluded availability of the command icon.
    * @param image path to the image file used as the icon.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param newwin 'true' will cause to open the page in a new browser window and 'false' will open in the same window.
    */
   public void addCustomLinkCommand(String commandId, boolean imageIncluded, String image, String label,String url, boolean newwin)
   {
      if(imageIncluded)
         addCustomLinkCommand(commandId, imageIncluded, image, label, url, "", newwin);
      else
         addCustomLinkCommand(commandId, label, url, newwin);
   }
   
   /** Adds a link to another page as a custom command.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param field_list Fields to be added to the query string.
    * @param newwin 'true' will cause to open the page in a new browser window and 'false' will open in the same window.
    */
   public void addCustomLinkCommand(String commandId, String label,String url, String field_list,boolean newwin)
   {
      addSecureCustomLinkCommand(commandId, label, url, field_list, newwin, null);
   }

   /** Adds a link to another page as a custom command.
    * @param commandId A unique command identifier.
    * @param imageIncluded availability of the command icon.
    * @param image path to the image file used as the icon.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param field_list Fields to be added to the query string.
    * @param newwin 'true' will cause to open the page in a new browser window and 'false' will open in the same window.
    */
   public void addCustomLinkCommand(String commandId, boolean imageIncluded, String image, String label, String url, String field_list, boolean newwin)
   {
      if(imageIncluded)
         addSecureCustomLinkCommand(commandId, label, url, field_list, newwin, null,image);
      else
         addCustomLinkCommand(commandId, label, url, field_list, newwin);
   }

   /** Similer to addCustomLinkCommand. But you can give a list of security objects
    * here. If the user has lack of rights the command item dissapears.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param field_list Fields to be added to the query string.
    * @param newwin 'true' will cause to open the page in a new browser window and 'false' will open in the same window.
    * @param security_objects comma seperated list of security objects
    */
   public void addSecureCustomLinkCommand(String commandId, String label, String url, String field_list, boolean newwin, String security_objects)
   {
      addSecureCustomLinkCommand(commandId, label, url, field_list, newwin, security_objects, null);
   }
   
   // Added by Terry 20141003
   // Adding confirm msg of command bar item
   public void addSecureCustomLinkCommandWithConfirm(String commandId, String label, String url, String field_list, boolean newwin, String security_objects, String confirm_msg)
   {
      addSecureCustomLinkCommand(commandId, label, url, field_list, newwin, security_objects, null, confirm_msg);
   }
   // Added end

   /** Similer to addCustomLinkCommand. But you can give a list of security objects
    * here. If the user has lack of rights the command item dissapears.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param url page to be called from the link.
    * @param field_list Fields to be added to the query string.
    * @param newwin 'true' will cause to open the page in a new browser window and 'false' will open in the same window.
    * @param security_objects comma seperated list of security objects
    * @param iconImg path to the image file used as the icon.
    */
   
   // Modified by Terry 20141003
   // Change invoke of function
   public void addSecureCustomLinkCommand(String commandId, String label, String url, String field_list, boolean newwin, String security_objects, String iconImg)
   {
      addSecureCustomLinkCommand(commandId, label, url, field_list, newwin, security_objects, iconImg, null);
   }
   // Modified end

   // Added by Terry 20141003
   // Adding confirm msg of command bar item
   public void addSecureCustomLinkCommand(String commandId, String label, String url, String field_list, boolean newwin, String security_objects, String iconImg, String confirm_msg)
   {
      try
      {
         label = getASPManager().translate(label,getASPPage());
         field_list = url+","+field_list;
         defineItem(label, iconImg, "LINK_CMD_"+commandId, CMD_GROUP_CUSTOM, false, false, newwin, false, security_objects, field_list, confirm_msg);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end
   
   /**
    * Depending on the container the link will direct to either
    * a web page or a rich webclient form.
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param web_url Web page to be called from the link.
    * @param rwc_url Rich webclient form url to be called from the link.
    * @param field_list Fields to be added to the query string.
    * @param security_objects Comma seperated list of security objects.
    */
   public void addCustomLinkCommand(String commandId, String label,String web_url, String rwc_url, String field_list, String security_objects)
   {
         addCustomRWCLinkCommand("DUAL_"+commandId, label, rwc_url, field_list, security_objects);
         addSecureCustomLinkCommand("DUAL_"+commandId, label, web_url, field_list, true, security_objects);
   }

   /**
    * Adds a link to a rich webclient form as a custom command
    * @param commandId A unique command identifier.
    * @param label Caption of the command in the command menu.
    * @param rwc_url Rich webclient form url to be called from the link.
    * @param field_list Fields to be added to the query string.
    * @param security_objects Comma seperated list of security objects.
    */
   public void addCustomRWCLinkCommand(String commandId, String label,String rwc_url, String field_list, String security_objects)
   {
      try
      {
         label = getASPManager().translate(label,getASPPage());
         field_list = rwc_url+","+field_list;
         defineItem(label, "LINK_CMD_RWC_"+commandId, CMD_GROUP_CUSTOM,false,false,false,security_objects,field_list);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Similar to addCustomCommand(), but accepts a comma separated list of Database objects and/or PO objects
    * associated to this custom commmand. The command will be enabled by the framework only if the user has
    * security rights to all objects given in the list.
    */
   public void addSecureCustomCommand(String commandId, String label, String po_object)
   {
      try
      {
         label = getASPManager().translate(label,getASPPage());
         defineItem( label, commandId, CMD_GROUP_CUSTOM, false,  false, false, po_object );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Added by Terry 20130923
   // Adding a secure custom command
   public void addSecureCustomCommand(String commandId, String label, String po_object, String imageUrl, boolean commandbar_button)
   {
      try
      {
         label = getASPManager().translate(label,getASPPage());
         defineItem(label, imageUrl, commandId, CMD_GROUP_CUSTOM, false,  false, true, commandbar_button, po_object, null);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end
   
   
   // Added by Terry 20140930
   // Adding confirm msg of command bar item
   public void addSecureCustomCommand(String commandId, String label, String po_object, String confirm_msg)
   {
      try
      {
         label = getASPManager().translate(label,getASPPage());
         defineItem(label, null, commandId, CMD_GROUP_CUSTOM, false, false, false, false, po_object, null, confirm_msg);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Adding a secure custom command
   // Adding confirm msg of command bar item
   public void addSecureCustomCommand(String commandId, String label, String po_object, String imageUrl, String confirm_msg, boolean commandbar_button)
   {
      try
      {
         label = getASPManager().translate(label,getASPPage());
         defineItem(label, imageUrl, commandId, CMD_GROUP_CUSTOM, false, false, true, commandbar_button, po_object, null, confirm_msg);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end

   /**
    * Use this method to add a separator between the custom commands.
    * The separator will be inserted right after the previous custom command
    * added with addCustomCommand().
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
    */
   public void addCustomCommandSeparator()
   {
      try
      {
         defineItem( "", "SEPARATOR", CMD_GROUP_CUSTOM, false,  false, false );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Disables an added custom command from the command bar, given the command identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
    */

   public void disableCustomCommand(String commandId)
   {
      try
      {
         this.findItem(commandId).userDisable();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Disables an added custom link command from the command bar, given the command identifier.
    * @param commandId A unique command identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomLinkCommand
    */
   public void disableCustomLinkCommand(String commandId)
   {
      try
      {
         this.findItem("LINK_CMD_"+commandId).userDisable();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Enables an added custom command from the command bar, given the command identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
    */

   public void enableCustomCommand(String commandId)
   {
      try
      {
         this.findItem(commandId).userEnable();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Removes an added custom command from the command bar, given the command identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
    */

   public void removeCustomCommand(String commandId)// throws FndException
   {
      try
      {
         if(getASPPage().getVersion()>=3)
             disableCustomCommand(commandId);
         this.findItem(commandId).remove();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Removes an added custom command from the action button in multirow mode, given the command identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
    */
   public void removeFromMultirowAction(String commandId)// throws FndException
   {
      try
      {
         this.findItem(commandId).removeFromMultirowAction();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * unset the a remove from action button in multirow mode, given the command identifier.
    * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
    */
   public void unsetRemoveFromMultirowAction(String commandId)// throws FndException
   {
      try
      {
         this.findItem(commandId).unsetRemoveFromMultirowAction();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   public boolean isRemovedFromMultirowAction(String commandId)
   {
      try
      {
         return this.findItem(commandId).isRemovedFromMultirowAction();
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }

   }


   /**
    * Adds a custom command group, used to group custom commands into a hierarchic structure.
    * @param name  A unique name for the command group.
    * @param label A label for the command group which is shown in the menu.
    */
   public void addCustomCommandGroup(String name,String label)
   {
      try
      {
         modifyingImmutableAttribute("CUSTOM_GROUPS");
         custom_groups.addElement(name);
         custom_labels.addElement(getASPManager().translate(label));
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   boolean findGroup(String group)
   {
      try
      {
         for(int x=0;x<custom_groups.size();x++)
            if(((String)custom_groups.elementAt(x)).equals(group))
               return true;

      } catch(Exception e) {error(e);}
      return false;
   }

   /**
    * Adds a custom command to a command group
    * @param commandID Command Identifier
    * @param group Group Identifier
    * @see ifs.fnd.asp.addCustomCommandGroup
    */
   public void setCustomCommandGroup(String commandID, String group)// throws FndException
   {
      try
      {
         this.findItem(commandID).setCustomGroup(group);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   Vector getCustomCommandGroups()
   {
      return custom_groups;
   }
   
   Vector getCustomCommandLabels()
   {
      return custom_labels;
   }

   String getCustomGroup(String commandID) {
      try
      {
         return this.findItem(commandID).getCustomGroup();
      }
      catch( Throwable any )
      {
         error(any);
      }
      return "";
   }

   String getCustomGroupLabel(String group_id)
   {
      try
      {
         for(int x=0;x<custom_groups.size();x++)
            if(((String)custom_groups.elementAt(x)).equals(group_id))
               return (String)custom_labels.elementAt(x);

      } catch(Exception e) {error(e);}
      return "";
   }

   /**
    * Use this method to select the custom command initially shown in the combo box.
    * By default the first added custom command, will also be the command initially
    * shown in the combo box. By calling this method, the initially shown command can be selected.
    */
   public void setDefaultCustomCommand(String commandId)
   {
      ASPCommandBarItem item;
      int i = 0;

      try
      {
         // Try to find the command...
         item = findItem(commandId);
         // ... if it exists, then reset any existing default custom command ...
         while (i<=count-1)
         {
            if(items[i].isCustomCommand())
               items[i].setDefault(false);
            i++;
         }
         // .. and then set the requested command to as default.
         item.setDefault(true);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to get the custom command currently selected by the user,
    * when the "Perform" command was clicked.
    */
   public String getSelectedCustomCommand()
   {
      try
      {
         return getASPPage().readValue( html_combo_perform_name ); //JAPA
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }

   /**
    * When this method has been called the record browsing commands
    * will operate against the database instead of against the buffer.
    * By default the record count displayed will be picked from the buffer.
    * When the user for example has opened a LOV window though,
    * not all database records will be in the buffer.
    * This is when this method should be called, to present
    * the number of records in the database instead of in the buffer.
    */
   public void setCounterDbMode()
   {
      try
      {
         modifyingMutableAttribute("COUNTER_DB_MODE");
         counter_db_mode = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Enable the upper and lower border lines of the command bar.
    */
   public void setBorderLines(boolean upper_border_line_enabled, boolean lower_border_line_enabled)
   {
//      if(getASPPage().getVersion()>=3)
//         error(new FndException("FNDCBRBORDERSOBSOLETE: In webkit 3 you can't use setBorderLines()."));

      try
      {
         modifyingMutableAttribute("BORDER_LINES_ENABLED");
         this.upper_border_line_enabled = upper_border_line_enabled;
         this.lower_border_line_enabled = lower_border_line_enabled;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to show the status ("new","modify","remove") of the current record.
    * An icon will be displayed at the left most postion of the command bar.
    * The icons used are the same as the ones beeing used in ASPTable.
    * @see     ifs.fnd.asp.ASPCommandBar#disableRowStatus
    */
   public void enableRowStatus()
   {
      try
      {
         modifyingMutableAttribute("ROW_STATUS_ENABLED");
         record_status_info_enabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }



   /**
    * Use this method to disable the visualization of the status of the current record.
    * @see     ifs.fnd.asp.ASPCommandBar#enableRowStatus
    */
   public void disableRowStatus()
   {
      try
      {
         modifyingMutableAttribute("ROW_STATUS_ENABLED");
         record_status_info_enabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Use this method to show the minimize button.
    * An icon will be displayed at the right most postion of the command bar.
    * The icons used are the same as the ones beeing used in ASPTable.
    * @see     ifs.fnd.asp.ASPCommandBar#disableMinimize
    */
   public void enableMinimize()
   {
      try
      {
         modifyingMutableAttribute("MINIMIZE_ENABLED");
         minimize_enabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to hide the minimize button.
    * @see     ifs.fnd.asp.ASPCommandBar#enableMinimize
    */
   public void disableMinimize()
   {
      try
      {
         modifyingMutableAttribute("MINIMIZE_ENABLED");
         minimize_enabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Returns true if the minimize button is enabled.
    * @see     ifs.fnd.asp.ASPCommandBar#enableMinimize
    * @see     ifs.fnd.asp.ASPCommandBar#disableMinimize
    */
   public boolean isMinimizeEnabled()
   {
      return minimize_enabled;
   }

   /**
    * Use this method to show the mode label (on by default).
    * @see     ifs.fnd.asp.ASPCommandBar#disableModeLabel
    */
   public void enableModeLabel()
   {
      try
      {
         modifyingMutableAttribute("MODE_LABEL_ENABLED");
         mode_label_enabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to hide the mode label.
    * @see     ifs.fnd.asp.ASPCommandBar#enableModeLabel
    */
   public void disableModeLabel()
   {
      try
      {
         modifyingMutableAttribute("MODE_LABEL_ENABLED");
         mode_label_enabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns true if the mode label is enabled.
    * @see     ifs.fnd.asp.ASPCommandBar#enableModeLabel
    * @see     ifs.fnd.asp.ASPCommandBar#disableModeLabel
    */
   public boolean isModeLabelEnabled()
   {
      return mode_label_enabled;
   }

   /**
    * Sets the width of the commandbar to an arbitrary number of pixels.
    * Note that the commandbar is automatically expanded if the content doesn't fit in.
    * If set to zero (default), the commandbar is 100% of its surrounding area.
    * @see     ifs.fnd.asp.ASPCommandBar#getWidth
    */
   public void setWidth( int width )
   {
      try
      {
         modifyingMutableAttribute("BAR_WIDTH");
         bar_width = width;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns the width of the commandbar.
    * @see     ifs.fnd.asp.ASPCommandBar#setWidth
    */
   public int getWidth()
   {
      return bar_width;
   }

   /**
    * Sets the width of the content of the commandbar to an arbitrary number of pixels.
    * @see     ifs.fnd.asp.ASPCommandBar#setWidth
    * @see     ifs.fnd.asp.ASPCommandBar#getInnerWidth
    */
   public void setInnerWidth( int width )
   {
      try
      {
         modifyingMutableAttribute("BAR_INNER_WIDTH");
         bar_inner_width = width;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns the width of the content of the commandbar.
    * @see     ifs.fnd.asp.ASPCommandBar#setInnerWidth
    */
   public int getInnerWidth()
   {
      return bar_inner_width;
   }


   /**
    * Use this method to show the action button in multirow mode.
    * @see     ifs.fnd.asp.ASPCommandBar#disableMultirowAction
    */
   public void enableMultirowAction()
   {
      try
      {
         modifyingMutableAttribute("MULTIROW_ACTION_ENABLED");
         multirow_action_enabled = true;
         page_controls_action_button = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Use this method to hide the action button in multirow mode. (default)
    * @see     ifs.fnd.asp.ASPCommandBar#enableMultirowAction
    */
   public void disableMultirowAction()
   {
      try
      {
         modifyingMutableAttribute("MULTIROW_ACTION__ENABLED");
         multirow_action_enabled = false;
         page_controls_action_button = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Returns true if the action button is enabled in multirow mode.
    * @see     ifs.fnd.asp.ASPCommandBar#enableMultirowAction
    * @see     ifs.fnd.asp.ASPCommandBar#disableMultirowAction
    */
   public boolean isMultirowActionEnabled()
   {
      return multirow_action_enabled;
   }

   /**
    * Returns true if the action button in multirow mode is controling inside the page.
    * @see     ifs.fnd.asp.ASPCommandBar#enableMultirowAction
    * @see     ifs.fnd.asp.ASPCommandBar#disableMultirowAction
    */
   public boolean isPageControlingActionButton()
   {
      return page_controls_action_button;
   }


  /**
   * This method will sets the Layout mode. The layout mode can decide which
   * HTML layout that will be used in the final webpage. It can also affect the
   * appearance of the Command bar. !!!
   */

   boolean isMultirowLayout()
    {
        return lay.isMultirowLayout();
    }
   boolean isSingleLayout()
    {
        return lay.isSingleLayout();
    }
   boolean isEditLayout()
    {
        return lay.isEditLayout();
    }
   boolean isFindLayout()
    {
        return lay.isFindLayout();
    }
   boolean isNewLayout()
    {
        return lay.isNewLayout();
    }
   boolean isCustomLayout()
    {
        return lay.isCustomLayout();
    }

   void setHistoryMode(int mode)
    {
        lay.setHistoryMode(mode);
    }

   int getHistoryMode()
    {
        return lay.getHistoryMode();
    }

   void setLayoutMode(int mode)
    {
        lay.setLayoutMode(mode);
    }
  /**
   * This method will fetch the current layout mode. !!!
   */
   
   int getLayoutMode()
   {
      return lay.getLayoutMode();
   }
   
   void backward()
   {
      // Added by Terry 20140902
      // Save change to ASPRowSet when it is editlayout
      if(isEditLayout())
         rowset.store();
      // Added end
      
      if(isMultirowLayout())
         getBlock().getASPTable().previousPage();
      else if (rowset.getCurrentRowNo()==0)
      {    
         rowset.prevDbSet();
         rowset.last();
      }  
      else
         rowset.previous();
   }
   
   void forward()
   {
      ASPContext ctx= getASPPage().getASPContext();
      int del_rows = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DEL_ROWS","0"));
      int dbrows = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DBCOUNT","0"));
      
      // Added by Terry 20140902
      // Save change to ASPRowSet when it is editlayout
      if(isEditLayout())
         rowset.store();
      // Added end
      
      if(isMultirowLayout())
         getBlock().getASPTable().nextPage();
      else if (del_rows>0 && dbrows>(rowset.countRows()+rowset.countSkippedDbRows()))
      {  
         int current_row = rowset.getCurrentRowNo(); 
         rowset.currentDbSet(); //To refill the rowset after deleting rows
         rowset.goTo(current_row+1);
      }   
      else if (rowset.getCurrentRowNo()==rowset.countRows()-1 )
         rowset.nextDbSet();
      else
         rowset.next();
   }
   
   void first()
   {
      // Added by Terry 20140902
      // Save change to ASPRowSet when it is editlayout
      if(isEditLayout())
         rowset.store();
      // Added end
      
      // Modified by Terry 20140902
      // Original:
      // rowset.firstDbSet();
      if (isMultirowLayout())
         rowset.firstDbSet();
      // Modified end
      
      rowset.first();
   }
   
   void last()
   {
      // Added by Terry 20140902
      // Save change to ASPRowSet when it is editlayout
      if(isEditLayout())
         rowset.store();
      // Added end
      
      // Modified by Terry 20140902
      // Original:
      // rowset.lastDbSet();
      if (isMultirowLayout())
         rowset.lastDbSet();
      // Modified end
      
      rowset.last();
   }

    /**
     * Refreshes the record browse information after a delete. You need this function if you have
     * defined your own delete function and use querySubmit.
     * The parameter deleted is the number of rows deleted.
     */

    public void browseUpdate(int deleted)
    {
              ASPContext ctx= getASPPage().getASPContext();
              int dbrows = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DBCOUNT","0"));
              int del_rows = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DEL_ROWS","0"));
              if(dbrows>0) dbrows-=deleted;
              del_rows+=deleted;
              ctx.writeValue("__"+getBlock().getName()+".DBCOUNT",""+dbrows);
              ctx.writeValue("__"+getBlock().getName()+".DEL_ROWS",""+del_rows);
    }

 /**
   *    Returns True or False,
   *    This method can be used to Enable or Disable a Custom Command,
   *
   *    @param cmdId        custom command name
   *    @param column       field to be used for validation,
   *    @param type         Enable or Disable the Command if the condition is true, (Only "Enable" or "Disable" is allowed here.)
   *    @param parameters   semicolon separated String.
   *    @param or_condition Relationship between different valid conditions is 'OR' if true. else 'AND'
   *    <pre>
   *    If the field's (given as the second parameter - coloumn) value matches any of the values given in the
   *    semicolon separated list given in the parameter 'parameters' then the command will be either  enabled or
   *    disabled depending on the value given at parameter 'type'.  The fields could even be compared with null values.
   *    To compare with null values, in the parameter 'parameters' either 'null' or '' could be given (in the semicolon separated list).
   *    </pre>
   *
   *    the following example assumes that there is a commandBar object as headbar. Then
   *    it Enables the 'approve' command if  APPROVE_ALLOWED field is either TRUE or null
   *
   *    <pre>
   *    Example:
   *    public void preDefine()
   *    {......
   *    headbar.addCustomCommand("approve","Approve order" );
   *    headbar.addCommandValidConditions("approve","APPROVE_ALLOWED","Enable","TRUE;null");
   *    .............
   *    }
   *    </pre>
   *    @see ASPBCommandBar#addCustomCommand
   */

   public void addCommandValidConditions(String cmdId, String column, String type, String parameters, boolean or_condition)// throws FndException
   {
      try
      {
         ASPCommandBarItem citem = findItem(cmdId);
         String param = "";
         int counter, of;
         String p;

         citem.setValidDependedColumn(column);
         if(or_condition)
            citem.setORCondition(true);

         if(!"".equals(type))
         {
            if(type.equals(VALID_CONDITION_ENABLE) || type.equals(VALID_CONDITION_DISABLE))
               citem.appendValidTypeCondition(type);
            else
               throw new FndException("FNDCBRADCDFHOLA: Type can only be '"+VALID_CONDITION_ENABLE+"' or '"+VALID_CONDITION_DISABLE+"'");
         }

        if(parameters==null || parameters.equals(""))
            parameters = " ";

         parameters += ';';

         StringTokenizer tokens = new StringTokenizer(parameters,";");
         String tempString = "";
         while(tokens.hasMoreTokens())
         {
             tempString= tokens.nextToken();

             if((tempString.equalsIgnoreCase("null"))||(tempString.equals("''")))
                 tempString=" ";

             param += tempString + ";";
          }

         citem.setValidValues(param);

         if(DEBUG)
         {
             debug("params for '" + citem.getCommandId() + "'");
             String blah = citem.fetchValidValues();
             StringTokenizer valid = new StringTokenizer(blah,(char)30+"");
             StringTokenizer vals = null;
             while(valid.hasMoreTokens())
             {
                vals = new StringTokenizer(valid.nextToken(),";");
                while(vals.hasMoreTokens())
                   debug(vals.nextToken());
             }
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   public void addCommandValidConditions(String cmdId, String column, String type, String parameters)
   {
      addCommandValidConditions(cmdId, column, type, parameters, false);
   }

   /**
     *    This method is used to add more columns and valid values for a particular command to extend the valid conditions.
     *    A call to this method should always be followed by a call to an addCommandValidConditions().
     *    Any number of calls can be made to this method for a particular command as desired.
     *
     *    @param cmdId        custom command name
     *    @param column       field to be used for validation,
     *    @param type         'Enable' or 'Disable',
     *    @param parameters   semicolon separated list of valid valaues.
     *
     *    Example:
     *          cmdbar.addCommandValidConditions("myCommnadId", "STATE", "Disable", "Invoiced;Closed", true);
     *          cmdbar.appendCommandValidConditions("myCommnadId", "NAME", "Enable" , "Wild Palms AB");
     *          cmdbar.appendCommandValidConditions("myCommnadId", "DELIVERY_TYPE", "Snail;Mail");
     */
   public void appendCommandValidConditions(String cmdId, String column, String type, String parameters)
   {
       addCommandValidConditions(cmdId, column, type ,parameters, false);
   }

   public void appendCommandValidConditions(String cmdId, String column, String parameters)
   {
       appendCommandValidConditions(cmdId, column, VALID_CONDITION_ENABLE, parameters);
   }

   //==========================================================================
   //  Package.
   //==========================================================================

   boolean isLayoutEnabled()
   {
      return cmd_group_layout_enabled;
   }

   boolean isEditEnabled()
   {
      return cmd_group_edit_enabled;
   }

   boolean isSearchEnabled()
   {
      return cmd_group_search_enabled;
   }

   boolean isBrowseEnabled()
   {
      return cmd_group_browse_enabled;
   }

   public boolean IsNewEnabled()
   {
      if(!(getBlock().isCommandDefined(ASPRowSet.NEW) && IsEnabled(NEWROW)))
         return false;
      else
         return true;
   }
   /**
    * Returns all custom commands that are not separator placeholders,
    * and are not in any custom command group.
    * Used in ASPTable and here.
    */
   Vector getCustomCommands()
   {
      Vector cmds = new Vector();
      for( int i=1; i<count; i++ )
         if(items[i].isCustomCommand() && !Str.isEmpty(items[i].getName()) && Str.isEmpty(items[i].getCustomGroup()))
            cmds.addElement(items[i]);

      return cmds;
   }
   
   /**
    * Returns all custom commands, including separator placeholders.
    * Used in ASPPopup.
    */
   Vector getAllCustomCommands()
   {
      return getAllCustomCommands(true);
   }
   
   Vector getAllCustomCommands(boolean include_sep)
   {
      Vector cmds = new Vector();
      for( int i=1; i<count; i++ )
         if(items[i].isCustomCommand() && (include_sep || !Str.isEmpty(items[i].getName())))
            cmds.addElement(items[i]);

      return cmds;
   }

   ASPBlock getBlock()
   {
      return (ASPBlock)getContainer();
   }

   String getBlockPrefix(boolean withDot)
   {
      String block_prefix;

      block_prefix = getBlock().getName();

      if(block_prefix==null)
         block_prefix = "";
      else if(withDot)
         block_prefix = block_prefix + ".";

      return block_prefix;
   }

   String getSearchURL()
   {
      return search_url;
   }

   void setSearchURL(String url)
   {
      try
      {
         modifyingMutableAttribute("SEARCH_URL");
         search_url = url;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

  public ASPRowSet getASPRowSet()
    {
        return rowset;
    }


  ASPCommandBarItem findItem(String commandId) throws FndException
   {
      int i = 0;
      while (i<=count-1)
      {
         if(items[i].getCommandId().equals(commandId) || items[i].getCommandId().equals("LINK_CMD_"+commandId))
         {
            return items[i];
         }
         i++;
      }
      throw new FndException("FNDCBRCNEX: Command \"&1\" does not exist!", commandId);
   }

   boolean getHoverEnabled()
   {
      return false;// not support for hover from App7 SP3;
   }

   boolean iconsEnabled()
   {
      return icons_enabled;
   }

   String getImageLocation()
   {
      //image_location;
      return getASPManager().getToolBarImageLocation();
   }

   String getImageLocationWithRTL()
   {
      //image_location;
      return getASPManager().getToolBarImageLocationWithRTL();
   }

   /*
   String getBaseImageLocation()
   {
      return base_image_location;
   }
    */

   String getIconAlignment()
   {
      return icon_alignment;
   }

   int getIconHeight()
   {
      return icon_height;
   }

   int getIconWidth()
   {
      return icon_width;
   }

   //==========================================================================
   //  Private
   //==========================================================================

   private void initStandardBar() throws FndException
   {
      // Create a standard command bar...
      defineItem( cmd_label_toggle,                 TOGGLE,      CMD_GROUP_LAYOUT, true,  true,  true );

      defineItem( cmd_label_new,                    PREPARE,     CMD_GROUP_EDIT,   true,  true,  true );
      defineItem( cmd_label_duplicate,              DUPLICATE,   CMD_GROUP_EDIT,   true,  true,  true );
      defineItem( cmd_label_edit,                   EDIT,        CMD_GROUP_EDIT,   true,  true,  true );
      defineItem( cmd_label_remove,                 REMOVE,      CMD_GROUP_EDIT,   true,  true,  true );
      defineItem( cmd_label_submit,                 SUBMIT,      CMD_GROUP_EDIT,   true,  true,  true );


      defineItem( cmd_label_clear,                  CLEAR,       CMD_GROUP_SEARCH, true,  true,  true );
      defineItem( cmd_label_count,                  COUNT,       CMD_GROUP_SEARCH, true,  true,  true );
      defineItem( cmd_label_search,                 SEARCH,      CMD_GROUP_SEARCH, true,  true,  true );
      defineItem( cmd_label_favorite,               FAVORITE,    CMD_GROUP_SEARCH, true,  true,  true );

      defineItem( createImgTag(img_first,"bottom"), FIRST,       CMD_GROUP_BROWSE, true,  false, true );
      defineItem( createImgTag(img_prev, "bottom"), PREVIOUS,    CMD_GROUP_BROWSE, false, false, true );
      defineItem( cmd_label_record,                 GOTO,        CMD_GROUP_BROWSE, true,  false, true );
      defineItem( "",                               FIELDGOTO,   CMD_GROUP_BROWSE, true,  true,  false);
      defineItem( "",                               FIELDTOTROW, CMD_GROUP_BROWSE, false, false, false);
      defineItem( createImgTag(img_next, "bottom"), NEXT,        CMD_GROUP_BROWSE, false, false, true );
      defineItem( createImgTag(img_last, "bottom"), LAST,        CMD_GROUP_BROWSE, false, true,  true );

      defineItem( "",                               FIELDPERFORM,CMD_GROUP_CUSTOM, true,  true,  false);
      defineItem( cmd_label_perform,                PERFORM,     CMD_GROUP_CUSTOM, true,  true,  true );

   }

   private void initImageBar() throws FndException
   {
      // Create a command bar with icons...
      defineItem( cmd_hint_toggle,   TOGGLE,       CMD_GROUP_LAYOUT, true,   true,  true );

      defineItem( cmd_hint_new,      PREPARE,      CMD_GROUP_EDIT,   true,   false, true );
      defineItem( cmd_hint_duplicate,DUPLICATE,    CMD_GROUP_EDIT,   false,  false, true );
      defineItem( cmd_hint_edit,     EDIT,         CMD_GROUP_EDIT,   false,  false, true );
      defineItem( cmd_hint_remove,   REMOVE,       CMD_GROUP_EDIT,   false,  true,  true );
      defineItem( cmd_hint_submit,   SUBMIT,       CMD_GROUP_EDIT,   false,  true,  true );

      defineItem( cmd_hint_clear,    CLEAR,        CMD_GROUP_SEARCH, true,   false, true );
      defineItem( cmd_hint_count,    COUNT,        CMD_GROUP_SEARCH, false,  false, true );
      defineItem( cmd_hint_search,   SEARCH,       CMD_GROUP_SEARCH, false,  true,  true );
      defineItem( cmd_hint_favorite, FAVORITE,     CMD_GROUP_SEARCH, false,  true,  true );

//      defineItem( cmd_hint_first,    FIRST,        CMD_GROUP_BROWSE, true,   false, true );
      defineItem( cmd_hint_previous, PREVIOUS,     CMD_GROUP_BROWSE, false,  false, true );
      defineItem( cmd_hint_record,   GOTO,         CMD_GROUP_BROWSE, true,   true,  true );
//      defineItem( "",                FIELDGOTO,    CMD_GROUP_BROWSE, false,  true,  false);
//      defineItem( "",                FIELDTOTROW,  CMD_GROUP_BROWSE, false,  false, false);
      defineItem( cmd_hint_next,     NEXT,         CMD_GROUP_BROWSE, true,   false, true );
//      defineItem( cmd_hint_last,     LAST,         CMD_GROUP_BROWSE, false,  true,  true );

      defineItem( "",                FIELDPERFORM, CMD_GROUP_CUSTOM, true,   false, false);
      defineItem( cmd_hint_perform,          PERFORM,      CMD_GROUP_CUSTOM, true,   true,  true );
      defineItem( cmd_hint_notes,            NOTES,        CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_find,             FIND,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_okfind,           OKFIND,       CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_cancelfind,       CANCELFIND,   CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_countfind,        COUNTFIND,    CMD_GROUP_AUTO,   false,  true,  true );
      // Added by Terry 20130918
      // Advanced Query
      defineItem( cmd_hint_advancedfind,     ADVANCEDFIND, CMD_GROUP_AUTO,   false,  true,  true );
      // Added end
      defineItem( cmd_hint_delete,           DELETE,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_editrow,          EDITROW,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_viewdetails,      VIEWDETAILS,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_newrow,           NEWROW,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_savereturn,       SAVERETURN,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_savenew,          SAVENEW,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_cancelnew,        CANCELNEW,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_canceledit,       CANCELEDIT,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_back,             BACK,         CMD_GROUP_AUTO,   false,  true,  true );
      
      // Added by Terry 20130315
      // Edit command in overview
      defineItem( cmd_hint_overviewedit,     OVERVIEWEDIT,    CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_overviewnew,      OVERVIEWNEW,     CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_overviewdelete,   OVERVIEWDELETE,  CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_overviewsave,     OVERVIEWSAVE,    CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_overviewcancel,   OVERVIEWCANCEL,  CMD_GROUP_AUTO,   false,  true,  true );
      // Added end
      
      // Added by Terry 20120816
      // Approve row bar items
      defineItem( cmd_hint_approvestart,     APPROVESTART,    CMD_GROUP_APPROVE,   false,  true,  true );
      defineItem( cmd_hint_approverow,       APPROVEROW,      CMD_GROUP_APPROVE,   false,  true,  true );
      defineItem( cmd_hint_approvereturn,    APPROVERETURN,   CMD_GROUP_APPROVE,   false,  true,  true );
      defineItem( cmd_hint_approvecancel,    APPROVECANCEL,   CMD_GROUP_APPROVE,   false,  true,  true );
      defineItem( cmd_hint_approveview,      APPROVEVIEW,     CMD_GROUP_APPROVE,   false,  true,  true );
      defineItem( cmd_hint_approvesendview,  APPROVESENDVIEW, CMD_GROUP_APPROVE,   false,  true,  true );
      defineItem( cmd_hint_approvesendviewfin,  APPROVESENDVIEWFIN, CMD_GROUP_APPROVE,   false,  true,  true );
      // Added end
      
      // Added by Terry 20131001
      // Command bar in DocEdmControlVue page
      defineItem( cmd_hint_filesave,         FILESAVE,        CMD_GROUP_FILE,   false,  true,  true );
      defineItem( cmd_hint_filesaveall,      FILESAVEALL,     CMD_GROUP_FILE,   false,  true,  true );
      defineItem( cmd_hint_fileprint,        FILEPRINT,       CMD_GROUP_FILE,   false,  true,  true );
      defineItem( cmd_hint_fileprintall,     FILEPRINTALL,    CMD_GROUP_FILE,   false,  true,  true );
      
      // Added by Terry 20120929
      // Lov bar in multi-select mode
      defineItem( cmd_hint_oklov,            OKLOV,           CMD_GROUP_AUTO,   false,  true,  true );
      // Added end
      
      // Added by Terry 20130902
      // Add document reference bar
      defineItem( cmd_hint_docref,           DOCREF,          CMD_GROUP_AUTO,   false,  true,  true );
      // Added end
      
      defineItem( cmd_hint_duplicaterow,     DUPLICATEROW,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_first,            FIRST,            CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_backward,         BACKWARD,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_forward,          FORWARD,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_last,             LAST,             CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_rowhistory,     ROWHISTORY,         CMD_GROUP_AUTO,   false,  true,  true );
      defineItem( cmd_hint_properties,     PROPERTIES,         CMD_GROUP_AUTO,   true,  true,  true );

      defineItem( cmd_hint_save,          SAVE,         CMD_GROUP_EDIT,   true,  true,  true );

      defineItem( "",                OKIID, CMD_GROUP_AUTO, true,   false, false);
      disableCommand("Save");
      disableCommand(OKIID);
      // Added by Terry 20120929
      // Lov bar in multi-select mode
      findItem(OKLOV).disable();
      // Added end
   }

   private String createImgTag( String imgName, String vertAlign )
   {
      return "<img align=\"" + vertAlign + "\" border=\"0\" src=\"" + getImageLocation() + imgName + "\">";
   }

   private String createImgTag(String imgName, String vertAlign, int height, int width)
   {
      return "<img align=\"" + vertAlign + "\" border=\"0\" height=\""+height+"\" width=\""+width+"\" src=\"" + getImageLocation() + imgName + "\">";
   }


   private void defineItem(String label, String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked) throws FndException
   {
     defineItem(label, commandId, commandGroup, add_left_space, add_right_space, hyperlinked, null);
   }

   private void defineItem(String label, String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked, String plsql_method) throws FndException
   {
      defineItem(label, null, commandId, commandGroup, add_left_space, add_right_space, hyperlinked, false, plsql_method, null);
   }
   
   private void defineItem(String label,String image_url,String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked, boolean commandbar_button) throws FndException
   {
      defineItem(label, image_url,commandId, commandGroup, add_left_space, add_right_space, hyperlinked, commandbar_button, null);
   }
   
   private void defineItem(String label,String image_url,String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked, boolean commandbar_button, String plsql_method) throws FndException
   {
      defineItem(label, image_url,commandId, commandGroup, add_left_space, add_right_space, hyperlinked, commandbar_button, plsql_method, null);
   }
   
   private void defineItem(String label,String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked,  String plsql_method, String field_list) throws FndException
   {
      defineItem(label, null,commandId, commandGroup, add_left_space, add_right_space, hyperlinked,false,  plsql_method, field_list);
   }

   // Modified by Terry 20140930
   // Adding confirm msg of function
   private void defineItem(String label,String image_url,String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked, boolean commandbar_button, String plsql_method, String field_list) throws FndException
   {
      defineItem(label, image_url, commandId, commandGroup, add_left_space, add_right_space, hyperlinked, commandbar_button, plsql_method, field_list, null);
   }
   // Modified end

   // Added by Terry 20140930
   // Adding confirm msg of command bar item
   private void defineItem(String label,String image_url,String commandId, int commandGroup,
                           boolean add_left_space, boolean add_right_space, boolean hyperlinked, boolean commandbar_button, String plsql_method, String field_list, String confirm_msg) throws FndException
   {
      modifyingImmutableAttribute("ITEMS");
      
      ASPCommandBarItem item;
      
      item = (new ASPCommandBarItem(this)).construct(label,image_url,commandId,commandGroup,false,false,commandId.startsWith("LINK_CMD")?false:hyperlinked, commandbar_button);
      
      if (commandId.startsWith("LINK_CMD"))
      {
         //item.setLinkFieldList(plsql_method);
         item.setLinkFieldList(field_list);
         item.setLinkInNewWin(hyperlinked);
         item.setClientFunction("");
      }
      
      if (!Str.isEmpty(plsql_method))
         item.setSecurePlSqlMethod(plsql_method);
      
      if (!Str.isEmpty(confirm_msg))
         item.setCmdConfirmMsg(confirm_msg);
      
      if( count==items.length )
      {
         ASPCommandBarItem[] newitems = new ASPCommandBarItem[2*count];
         System.arraycopy(items,0,newitems,0,items.length);
         items = newitems;
      }
      
      if (ASPCommandBar.SAVERETURN.equals(commandId) || ASPCommandBar.SAVENEW.equals(commandId) )
         item.setClientFunction("check" + IfsNames.dbToAppName(getBlock().getName()) + "Fields(i)");
      
      items[count++] = item;
   }
   // Added end

   String getClientFunction(String commandId) throws Exception
   {
      try
      {
         return this.findItem(commandId).getClientFunction();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   String getSecurePlSqlMethod(String commandId)
   {
      try
      {
         return this.findItem(commandId).getSecurePlSqlMethod();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   private String createFieldGotoTag() throws Exception
   {
      tmpsubbuf.clear();
      int totrows;

      tmpsubbuf.append("<input TYPE=\"text\" SIZE=\"4\" ");
      tmpsubbuf.append("NAME=\"");

      tmpsubbuf.append(html_field_goto_name);

      tmpsubbuf.append("\" value=\"");

      if(!counter_db_mode)
         totrows = rowset.countRows();
      else
         totrows = rowset.countDbRows();

      if(totrows>0)
      {
         if(!counter_db_mode)
            tmpsubbuf.appendInt(rowset.getCurrentRowNo() + 1);
         else
            tmpsubbuf.appendInt(rowset.getCurrentRowNo() + rowset.countSkippedDbRows() + 1);
      }

      tmpsubbuf.append("\" class='editableTextField'>");
      return tmpsubbuf.toString();
   }

   private String createFieldTotRowTag() throws Exception
   {
      int totrows;

      if(!counter_db_mode)
         totrows = rowset.countRows();
      else
         totrows = rowset.countDbRows();

      if(totrows>0)
         return cmd_label_of + " " + totrows + "&nbsp;";
      else
         return "";
   }

   private String createFieldPerformTag() throws Exception
   {
      tmpsubbuf.clear();
      String tmp;
      int i = 0;

      tmpsubbuf.append("<select name=\"");
      tmpsubbuf.append(html_combo_perform_name);
      tmpsubbuf.append("\" size=\"1\" class='selectbox'>");

      // Add all custom commands to the perform combo box.
      while(i<=count-1)
      {
         if((items[i].isCustomCommand())&&(!items[i].checkDisabled())&&(!items[i].checkRemoved()))
         {
            if(items[i].isDefaultCustomCommand())
               tmp = "selected ";
            else
               tmp = "";

            tmpsubbuf.append("<option ", tmp, "value=\"");
            tmpsubbuf.append(items[i].getCommandId(), "\"> ", items[i].getLabel(), " </option>");
         }
         i++;
      }

      tmpsubbuf.append("</select>");

      return tmpsubbuf.toString();
   }

   private int getNumOfActiveCustomCommands()
   {
      int i=0,commandCounter=0;

      while(i<=count-1)
      {
         if((items[i].isCustomCommand())&&(!items[i].checkDisabled()))
            commandCounter++;
         i++;
      }
      return commandCounter;
   }

   // Added by Terry 20121120
   // Get select box html
   private String printSelectBox(String name, ASPBuffer aspbuf, String key, String tag, boolean mandatory, int size)
   {
      ASPManager mgr = getASPManager();
      AutoString out = new AutoString();
      ASPHTMLFormatter fmt = mgr.newASPHTMLFormatter();
      out.append("<select class='selectbox' size=");
      out.appendInt(size);
      out.append(" name=\"",name,"\" ");
      out.append(" id=\"",name,"\" ");
      
      if ( tag != null )
         out.append(tag);
      out.append(">");
      if ( mandatory )
         out.append(fmt.populateMandatoryListBox(aspbuf, key));
      else
         out.append(fmt.populateListBox(aspbuf, key));
      out.append("</select>");
      
      return out.toString();
   }
   // Added end

   private void addContent( AutoString html ) throws Exception
   {
      ASPManager mgr = getASPManager();

      String imageFileName, status;
      boolean isCustomCommandExists = false;
//      int oldCommandGroup = 0;
      boolean commandIsAdded = false;
      ASPPage page = getASPPage();

      if(getNumOfActiveCustomCommands()==0)
         removeCommandGroup(CMD_GROUP_CUSTOM);

      html.append( "<table ");
      if(mgr.getConfigParameter("AUDIT/__DEBUG/SHOW_BORDER","N").equals("Y"))
          html.append("border=0");
      else if(mgr.isNetscape4x())
          html.append("border=1");

      html.append(" CELLPADDING=\"0\" CELLSPACING=\"0\"");

      if(bar_width == 0)
         html.append(" width=100%");
      else
      {
         html.append(" width=");
         html.appendInt(bar_width);
      }

      html.append(" class='pageCommandBar'>");

      html.append("<tr><td width=100%>");
      html.append("<table cellpadding=0 cellspacing=0 height=22 width=\"");
      html.append("100%\"");
      if(mgr.getConfigParameter("AUDIT/__DEBUG/SHOW_BORDER","N").equals("Y"))
         html.append(" border=1");
      else
         html.append(" border=0");
      html.append("><tr>");

      // Show an icon corresponding to the status of the record,
      // if the status is "New", "Modify" or "Remove".
      // The icon is displayed at the left most position of the command bar,
      // followed by a "divider".

      boolean new_edit_mode = page.compatibility.newEditMode();

         // In Webkit version 3, the Toolbar is dynamically generated depending on the layout mode.
      if(getASPPage().getVersion()>=3)
          adjustModes();

      // add left column - help button, and browse buttons
      html.append("<td align=left width=5>&nbsp;");
      html.append("</td>"); //<td width=5>&nbsp;</td>");
      // add title and the new browse buttons.

      int width;
      if(getASPPage().getASPLov() != null)
         width = 480;
      else
      {
         if(bar_inner_width == 0)
            width = mgr.getASPForm().getFormContentWidth()-20;
         else
            width = bar_inner_width;
      }

      html.append("<td width=100%");
      html.append("><table class=pageCommandBar style=\"border:none;\" ");
      if(mgr.getConfigParameter("AUDIT/__DEBUG/SHOW_BORDER","N").equals("Y"))
            html.append("border=1");
      else
            html.append("border=0");
      html.append(" width=100%");
      html.append(" cellspacing=0 cellpadding=0><tr>");

      //add property button to commandbar
      ASPCommandBarItem props = findItem(PROPERTIES);
      props.setPropertyPageURL(mgr.getASPConfig().getScriptsLocation() + "ConfigurableScreenLayout.page?URL="+mgr.getASPPage().getPoolKey() + "&OBJNAME=" + lay.getName());  //CSL profile page

      if (!props.isUserDisabled() && !Str.isEmpty(props.getPropertyPageURL()) && !mgr.isDefaultLayout() && getASPPage().isObjectAccessible("FND/ConfigurableScreenLayout.page") && !mgr.getASPPage().getASPProfile().isUserProfileDisabled())
      {
        html.append("<td NOWRAP valign=top align=left height="+icon_height+" width=10>");
        props.getItemData(html);
        html.append("</td>");
      }

      html.append("<td NOWRAP valign=middle "+(mgr.isRTL()?"align=right":"align=left")+" height=22>&nbsp;");
      String headmodediv;
      if(getBlock().hasTitle())
          headmodediv=" - ";
      else
          headmodediv="";
      if(isModeLabelEnabled())
      {
         if(isFindLayout())
            html.append(mgr.translateJavaText("FNDCBRFINDMODE: Find")+headmodediv);
         if(isEditLayout())
             html.append(mgr.translateJavaText("FNDCBREDITMODE: Edit")+headmodediv);
         if(isNewLayout())
             html.append(mgr.translateJavaText("FNDCBRNEWMODE: New")+headmodediv);
      }
      if(getBlock().hasTitle())
         html.append(getBlock().getTitle());
      html.append("&nbsp;</td>");

      if(isSingleLayout() || isEditLayout() || isMultirowLayout())
      {
         int y;
         for(y=0;y<count;y++)
            if (items[y].getCommandId().equals(FIRST)) break;

         if(!items[y+1].isUserDisabled() && !items[y+2].isUserDisabled() )
         {
            ASPContext ctx = getASPPage().getASPContext();
            int skipped = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".SKIP_ROWS","0"));
            int dbcount = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DBCOUNT","0"));
            int buffer_size = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".MAX_ROWS","-1"));
            if(buffer_size == -1)
               buffer_size = Integer.parseInt(getASPManager().getConfigParameter("ADMIN/BUFFER_SIZE","10"));
            int hasmrows = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".HAS_MORE_ROWS","-1"));
            ASPBuffer ctxbuf = ctx.readBuffer("__"+getBlock().getName()+".QUERYBUFFER");
            boolean count_db_rows_enabled = ((ctxbuf!=null) && ("Y".equals(ctxbuf.getValue("COUNT_ROWS"))));
            int layout = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".LAYOUT","0"));
            int del_rows = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DEL_ROWS","0"));
            
            if (dbcount>0 && buffer_size>0 && layout == 1) //To increase the dbcount when adding new records
            {    
               if (rowset.countRows()>buffer_size)
                  dbcount = dbcount + 1; 
               else if ((skipped+rowset.countRows())>dbcount)
                  dbcount = dbcount + (skipped+rowset.countRows()-dbcount);
               
               ctx.writeValue("__"+getBlock().getName()+".LAYOUT","0");
               ctx.writeValue("__"+getBlock().getName()+".DBCOUNT",""+dbcount);
            } 

            if(isMultirowLayout())
            {
               int rowcount = rowset.countRows(); 
               html.append("<td valign=top><table class=pageCommandBar style=\"border: none;\"><tr>"); 

               // Added by Terry 20121120
               // Calculate total pages and current page
               int total_pages = (dbcount + buffer_size - 1) / buffer_size;
               int current_page = (skipped + buffer_size) / buffer_size;
               // Added end
               
               if(ctxbuf!=null && rowcount>0 && ((dbcount>buffer_size && rowcount!=dbcount) || (dbcount>rowcount)))
               {
                  html.append("<td NOWRAP width=22>"); 
                  if ((count_db_rows_enabled) && (skipped>0)) 
                     items[y].getItemData(html);
                  html.append("</td>");

                  html.append("<td NOWRAP width=22>");
                  if(skipped>0) items[y+1].getItemData(html);
                  html.append("</td>");
                  skipped++;
                  if (count_db_rows_enabled)
                     html.append("<td NOWRAP valign=middle>",mgr.translateJavaText("FNDCBRRCYRRECNO: &1-&2 of &3",Integer.toString(skipped),Integer.toString(skipped+rowcount-1),Integer.toString(dbcount)),"&nbsp;");
                  else
                     html.append("<td NOWRAP valign=middle>",mgr.translateJavaText("FNDCBRCURRECNO: &1-&2",Integer.toString(skipped),Integer.toString(skipped+rowcount-1)),"&nbsp;");    
                  html.append("</td><td NOWRAP width=22>"); 

                  skipped--;
                  if((skipped+buffer_size)<dbcount || (del_rows>0 && (skipped+rowcount)<dbcount))
                     items[y+2].getItemData(html);
                  html.append("</td>");

                  html.append("<td NOWRAP width=22>");
                  if (count_db_rows_enabled && hasmrows ==1)
                     items[y+3].getItemData(html);
                  html.append("</td>");
                  
                  // Added by Terry 20121120
                  // Generate pages select box in CommandBar
                  if (!getBlock().hasASPTable() || !getBlock().getASPTable().isEditable())
                  {
                     html.append("<td NOWRAP align=\"right\" width=20>");
                     html.append(mgr.translateJavaText("FNDCBRTHECURRENTPAGE1: The"));
                     html.append("</td>");
                     html.append("<td NOWRAP width=22>");
                     ASPBuffer page_buf = mgr.newASPBuffer();
                     for (int i = 0; i < total_pages; i++)
                     {
                        ASPBuffer row = page_buf.addBuffer("DATA");
                        row.addItem("PAGE_NAME", String.valueOf(i + 1));
                        row.addItem("PAGE_VALUE", String.valueOf(i + 1));
                     }
                     html.append(printSelectBox(getBlock().getName() + "_GotoPage", page_buf, String.valueOf(current_page), "onchange=\"javascript:setNoteBookVisible();setGotoPageCommand('" + getBlock().getName() + "', this.options[this.options.selectedIndex].value);\"", true, 1));
                     page_buf.clear();
                     html.append("</td>");
                     
                     html.append("<td NOWRAP width=20>");
                     html.append(mgr.translateJavaText("FNDCBRTHECURRENTPAGE2: Page"));
                     html.append("</td>");
                  }
                  // Added end
               }
               else if(ctxbuf!=null && hasmrows ==1 && rowcount>0)
               {
                  html.append("<td NOWRAP width=22>"); 
                  if ((count_db_rows_enabled) && (skipped>0)) 
                     items[y].getItemData(html);
                  html.append("</td>");

                  html.append("<td NOWRAP width=22>");
                  if(skipped>0) items[y+1].getItemData(html);
                  html.append("</td>");
                  skipped++;
                  html.append("<td NOWRAP valign=middle>",mgr.translateJavaText("FNDCBRCURRECNO: &1-&2",Integer.toString(skipped),Integer.toString(skipped+rowcount-1)),"&nbsp;");
                  html.append("</td><td NOWRAP width=22>"); 

                  skipped--;
                  if(hasmrows==1) items[y+2].getItemData(html);
                  html.append("</td>");

                  html.append("<td NOWRAP width=22>");
                  if (count_db_rows_enabled && hasmrows ==1)
                     items[y+3].getItemData(html);
                  html.append("</td>");
                  
               }
               html.append("</tr></table></td>");
            }
            else
            {
               html.append("<td valign=top><table class=pageCommandBar style=\"border: none;\"><tr>");
               if(rowset.countRows()>0)
               {
                  // Modified by Terry 20131001
                  // Command bar not in DocEdmControlVue page
                  if (!"DocEdmControlVue".equals(getASPPage().getPageName()))
                  {
                     html.append("<td NOWRAP width=22>");
                     // Modified by Terry 20130611
                     // Enable FIRST AND BACKWARD command bar in edit layout
                     // Original:
                     // if ((isSingleLayout()) && ((rowset.countRows()>1 && rowset.getCurrentRowNo()>0) || (rowset.getCurrentRowNo()+skipped)>0))
                     if ((isSingleLayout() || isEditLayout()) && ((rowset.countRows()>1 && rowset.getCurrentRowNo()>0) || (rowset.getCurrentRowNo()+skipped)>0))
                        // Modified end
                     {
                        if (count_db_rows_enabled) 
                           items[y].getItemData(html);
                        html.append("</td>");
                        html.append("<td NOWRAP width=22>");
                        items[y+1].getItemData(html);
                     }
                     else
                     {
                        html.append("</td>");
                        html.append("<td NOWRAP width=22>");
                     }
                     html.append("</td>");
                     
                     int x,total;
                     if (rowset.countRows()==0) x=0;
                     else x=rowset.getCurrentRowNo()+1;
                     
                     if ((dbcount < buffer_size) && (hasmrows==0)) 
                        total = rowset.countRows();
                     else 
                        total = dbcount;
                     
                     //html.append("<td NOWRAP align=center valign=middle>",mgr.translateJavaText("FNDCBRRECNO: &1 of &2",Integer.toString(x),Integer.toString(rowset.countRows())),"&nbsp;");
                     if (count_db_rows_enabled) 
                        html.append("<td NOWRAP align=center valign=middle>",mgr.translateJavaText("FNDCBRRECNO: &1 of &2",Integer.toString(x+skipped),Integer.toString(total)),"&nbsp;");
                     else if (hasmrows ==1)
                        html.append("<td NOWRAP align=center valign=middle>",mgr.translateJavaText("FNDCBRRECNO1: &1",Integer.toString(x+skipped)),"&nbsp;");
                     else
                        html.append("<td NOWRAP align=center valign=middle>",mgr.translateJavaText("FNDCBRRECNO: &1 of &2",Integer.toString(x+skipped),Integer.toString(rowset.countRows())),"&nbsp;"); 
                     
                     html.append("</td><td NOWRAP width=22>");
                     //if(rowset.getCurrentRowNo()<(rowset.countRows()-1))
                     // Modified by Terry 20130611
                     // Enable FORWARD AND LAST command bar in edit layout
                     // Original:
                     // if ((isSingleLayout()) && (rowset.getCurrentRowNo()<(rowset.countRows()-1) ||(hasmrows ==1)))
                     if ((isSingleLayout() || isEditLayout()) && (rowset.getCurrentRowNo()<(rowset.countRows()-1) ||(hasmrows ==1)))
                        // Modified end
                     {
                        items[y+2].getItemData(html);
                        html.append("</td>");
                        html.append("<td NOWRAP width=22>");
                        if (count_db_rows_enabled)
                           items[y+3].getItemData(html);
                     }
                     else
                     {
                        html.append("</td>");
                        html.append("<td NOWRAP width=22>");
                     }
                     html.append("</td>");
                  }
                  else
                  {
                     // Added by Terry 20131001
                     // Command bar in DocEdmControlVue page
                     html.append("<td id=\"CMD_FILE_FIRST\" NOWRAP width=22>");
                     // Add First Command Item
                     html.append("<a href=\"");
                     html.append("javascript:firstFile()");
                     html.append("\">");
                     html.append("<img ");
                     html.append("valign=middle align=center border=\"0\"");
                     html.append(" name=\""+"" + getBlockPrefix(false) + "_"  + items[y].getCommandId()+"\"");
                     html.append(" id=\""+"" + getBlockPrefix(false) + "_"  + items[y].getCommandId()+"\"");
                     html.append(" src=\"");
                     html.append(items[y].getIconEnabled());
                     html.append("\" alt=\"");
                     html.append(items[y].getLabel(),"\"");
                     html.append(" title=\"");
                     html.append(items[y].getLabel(),"\">");
                     html.append("</a>");
                     html.append("</td>");
                     // Add Previous Command Item
                     html.append("<td id=\"CMD_FILE_PRES\" NOWRAP width=22>");
                     html.append("<a href=\"");
                     html.append("javascript:presFile()");
                     html.append("\">");
                     html.append("<img ");
                     html.append("valign=middle align=center border=\"0\"");
                     html.append(" name=\""+"" + getBlockPrefix(false) + "_"  + items[y+1].getCommandId()+"\"");
                     html.append(" id=\""+"" + getBlockPrefix(false) + "_"  + items[y+1].getCommandId()+"\"");
                     html.append(" src=\"");
                     html.append(items[y+1].getIconEnabled());
                     html.append("\" alt=\"");
                     html.append(items[y+1].getLabel(),"\"");
                     html.append(" title=\"");
                     html.append(items[y+1].getLabel(),"\">");
                     html.append("</a>");
                     html.append("</td>");
                     // Add File select box
                     int total_files = rowset.countRows();
                     int current_file = rowset.getCurrentRowNo();
                     // html.append("<td NOWRAP width=80>");
                     // html.append(mgr.translateJavaText("FNDCBRTHECURRENTFILE: File List"));
                     // html.append("</td>");
                     html.append("<td NOWRAP width=22>");
                     ASPBuffer page_buf = mgr.newASPBuffer();
                     for (int i = 0; i < total_files; i++)
                     {
                        ASPBuffer row = page_buf.addBuffer("DATA");
                        row.addItem("FILE_NAME", String.valueOf(i));
                        row.addItem("FILE_VALUE", rowset.getRow(i).getValue("USER_FILE_NAME"));
                     }
                     html.append(printSelectBox("CMD_FILE_GOTO", page_buf, String.valueOf(current_file), "onchange=\"javascript:setFileWithNumber(this.options[this.options.selectedIndex].value,false);\"", true, 1));
                     page_buf.clear();
                     html.append("</td>");
                     // Add Next Command Item
                     html.append("<td id=\"CMD_FILE_NEXT\" NOWRAP width=22>");
                     html.append("<a href=\"");
                     html.append("javascript:nextFile()");
                     html.append("\">");
                     html.append("<img ");
                     html.append("valign=middle align=center border=\"0\"");
                     html.append(" name=\""+"" + getBlockPrefix(false) + "_"  + items[y+2].getCommandId()+"\"");
                     html.append(" id=\""+"" + getBlockPrefix(false) + "_"  + items[y+2].getCommandId()+"\"");
                     html.append(" src=\"");
                     html.append(items[y+2].getIconEnabled());
                     html.append("\" alt=\"");
                     html.append(items[y+2].getLabel(),"\"");
                     html.append(" title=\"");
                     html.append(items[y+2].getLabel(),"\">");
                     html.append("</a>");
                     html.append("</td>");
                     // Add Last Command Item
                     html.append("<td id=\"CMD_FILE_LAST\" NOWRAP width=22>");
                     html.append("<a href=\"");
                     html.append("javascript:lastFile()");
                     html.append("\">");
                     html.append("<img ");
                     html.append("valign=middle align=center border=\"0\"");
                     html.append(" name=\""+"" + getBlockPrefix(false) + "_"  + items[y+3].getCommandId()+"\"");
                     html.append(" id=\""+"" + getBlockPrefix(false) + "_"  + items[y+3].getCommandId()+"\"");
                     html.append(" src=\"");
                     html.append(items[y+3].getIconEnabled());
                     html.append("\" alt=\"");
                     html.append(items[y+3].getLabel(),"\"");
                     html.append(" title=\"");
                     html.append(items[y+3].getLabel(),"\">");
                     html.append("</a>");
                     html.append("</td>");
                     // Added end
                  }
                  // Modified end
               }
               html.append("</tr></table></td>");
            }
         }
      }

      html.append("<td");
      if (mgr.isRTL())      
         html.append(" NOWRAP height=\"20\" align=\"left\">");
      else
         html.append(" NOWRAP height=\"20\" align=\"right\">");

      // if ("DynamicLov".equals(getASPPage().getPageName()) &&
      if ((getASPPage().getASPLov() != null || "DynamicSel".equals(getASPPage().getPageName())) && getLayoutMode() == MULTIROW_LAYOUT && (getBlock().getASPTable().isRowSelectionEnabled() || getASPPage().getASPLov().isTreeListLov()))
      {
         // Modified by Terry 20120929
         // Lov bar in multi-select mode
         /*String id = getBlock().getName() + "_SendSelected";
         
         //added table no to __SELECTED in ASPTable for selectAll feature
         if (getBlock().hasASPTable())
            html.append("<a href=\"javascript:getAllSelectedValues("+getBlock().getASPTable().getTableNo()+")","\" ");
         else
            html.append("<a href=\"javascript:getAllSelectedValues()","\" ");

         if (getHoverEnabled())
         {
            ASPConfig cfg = getASPPage().getASPConfig();
            String imgloc  = getImageLocation();
            String normimg = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.gif");
            String hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/HOVER","ok_hov.gif");

            html.append(" onmouseover=\"javascript:",id ,".src='",imgloc,hovimg);
            html.append("'\"");
            html.append(" onmouseout=\"javascript:",id ,".src='",imgloc,normimg);
            html.append("'\"");
         }

         html.append("><img valign=middle align=center border=0 height=\"");
         html.appendInt(this.getIconHeight());
         html.append("\" name=\"",id,"\" src=\"");
         html.append(getImageLocation(),mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.gif"));
         html.append("\" alt=\"",mgr.translateJavaText("FNDCBRSENDSELECTVAL: Send Selected Values"),"\" title=\""+mgr.translateJavaText("FNDCBRSENDSELECTVAL: Send Selected Values")+"\"></a>");
         html.append("<img valign=middle align=center border=0 src='",getImageLocation(),"6px_space.gif'>");*/
         
         findItem(OKLOV).getItemData(html);
         
         // Modified end
      }
      // Added by Terry 20121009
      // Operate enumerate lov
      else if ("DynamicIIDLov".equals(getASPPage().getPageName()) && IsEnabled(OKIID))
      {
         String id = getBlock().getName() + "_SendSelected";
         
         //added table no to __SELECTED in ASPTable for selectAll feature
         if (getBlock().hasASPTable())
            html.append("<a href=\"javascript:getAllSelectedValues("+getBlock().getASPTable().getTableNo()+")","\" ");
         else
            html.append("<a href=\"javascript:getAllSelectedValues()","\" ");

         if (getHoverEnabled())
         {
            ASPConfig cfg = getASPPage().getASPConfig();
            String imgloc  = getImageLocation();
            String normimg = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.gif");
            String hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/HOVER","ok_hov.gif");

            // Modified by Terry 20130916
            // Multi-language toolbar
            // Original:
            // html.append(" onmouseover=\"javascript:",id ,".src='",imgloc,hovimg);
            html.append(" onmouseover=\"javascript:",id ,".src='",imgloc + getASPManager().getLanguageCode() + "/",hovimg);
            // Modified end
            html.append("'\"");
            // Modified by Terry 20130916
            // Multi-language toolbar
            // Original:
            // html.append(" onmouseout=\"javascript:",id ,".src='",imgloc,normimg);
            html.append(" onmouseout=\"javascript:",id ,".src='",imgloc + getASPManager().getLanguageCode() + "/",normimg);
            // Modified end
            html.append("'\"");
         }

         html.append("><img valign=middle align=center border=0 height=\"");
         html.appendInt(this.getIconHeight());
         html.append("\" name=\"",id,"\" src=\"");
         // Modified by Terry 20130916
         // Multi-language toolbar
         // Original:
         // html.append(getImageLocation(),mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.gif"));
         html.append(getImageLocation() + getASPManager().getLanguageCode() + "/",mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.gif"));
         // Modified end
         html.append("\" alt=\"",mgr.translateJavaText("FNDCBRSENDSELECTVAL: Send Selected Values"),"\" title=\""+mgr.translateJavaText("FNDCBRSENDSELECTVAL: Send Selected Values")+"\"></a>");
         html.append("<img valign=middle align=center border=0 src='",getImageLocation(),"6px_space.gif'>");
      }
      // Added end

      if (isCustomLayout()){
         for ( int i=0; i <= count-1; i++ ){
            ASPCommandBarItem cmditem = items[i];
            if( (!cmditem.checkRemoved()) && (!cmditem.isCustomCommand()) && !cmditem.getCommandId().equals(ASPCommandBar.PROPERTIES)){
                cmditem.getItemData(html);
            }
         }
      }
      
      for ( int i=0; i <= count-1; i++ )
      {
         ASPCommandBarItem item = items[i];
         String cmdid = item.getCommandId();
         if(cmdid.equals(BACKWARD) || cmdid.equals(FORWARD) || cmdid.equals(FIRST) || cmdid.equals(LAST)) continue;
         if(DEBUG) debug("ASPCommandBar.addContent(): cmdid="+cmdid);
         if( (!new_edit_mode && item.isNewItem())      ||
             (new_edit_mode  && item.isObsoleteItem()) )
         {
            if(DEBUG) debug("  skipping item: new_edit_mode="+new_edit_mode+",new_item="+item.isNewItem()+",obsolete_item="+item.isObsoleteItem());
            continue;
         }
         
         // Show custom command with image only.
         if( (!item.checkRemoved() && item.isCustomCommandWithImage() && getLayoutMode()==SINGLE_LAYOUT) || (
              !item.getCommandId().equals(DELETE) && getLayoutMode()!=SINGLE_LAYOUT && !item.getCommandId().equals(DUPLICATEROW) &&
              !item.getCommandId().equals(EDITROW) && !item.getCommandId().equals(VIEWDETAILS) &&
              // Added by Terry 20130315
              // Edit command in overview
              !item.getCommandId().equals(OVERVIEWEDIT) && !item.getCommandId().equals(OVERVIEWNEW) &&
              !item.getCommandId().equals(OVERVIEWDELETE) && !item.getCommandId().equals(OVERVIEWSAVE) &&
              !item.getCommandId().equals(OVERVIEWCANCEL) &&
              // Added end
              // Added by Terry 20120816
              // Approve row bar items
              !item.getCommandId().equals(APPROVESTART) && !item.getCommandId().equals(APPROVEROW) &&
              !item.getCommandId().equals(APPROVERETURN) && !item.getCommandId().equals(APPROVECANCEL) &&
              !item.getCommandId().equals(APPROVEVIEW) && !item.getCommandId().equals(APPROVESENDVIEW) &&
              !item.getCommandId().equals(APPROVESENDVIEWFIN) && !item.getCommandId().equals(OKLOV) &&
              // Added end
              // Added by Terry 20131001
              // Command bar in DocEdmControlVue page
              !item.getCommandId().equals(FILESAVE) && !item.getCommandId().equals(FILESAVEALL) &&
              !item.getCommandId().equals(FILEPRINT) && !item.getCommandId().equals(FILEPRINTALL) &&
              // Added end
              // Added by Terry 20130902
              // Add document reference bar
              !item.getCommandId().equals(DOCREF) &&
              // Added end
              !item.checkRemoved() && item.isCustomCommandWithImage() && getLayoutMode()!=CUSTOM_LAYOUT && !item.getCommandId().equals(PROPERTIES)))
         {
            // Modified by Terry 20130916
            // Original:
            // if(!item.isUserDisabled() && ((getLayoutMode()==SINGLE_LAYOUT && item.conditionsTrue(rowset.getCurrentRowNo())) || (getLayoutMode()==MULTIROW_LAYOUT))){
            if(!item.isUserDisabled() && ((getLayoutMode()==SINGLE_LAYOUT && item.conditionsTrue(rowset.getCurrentRowNo())) || (getLayoutMode()==MULTIROW_LAYOUT && !item.isCmdProperty()))){
            // Modified end
               if (item.isCmdProperty() && rowset.countRows() == 0) continue;
               item.getItemData(html);
               isCustomCommandExists = true;
            }
             
         }
      }

      for ( int i=0; i <= count-1; i++ )
      {
         ASPCommandBarItem item = items[i];
         String cmdid = item.getCommandId();
         if(cmdid.equals(BACKWARD) || cmdid.equals(FORWARD) || cmdid.equals(FIRST) || cmdid.equals(LAST)) continue;
         if(DEBUG) debug("ASPCommandBar.addContent(): cmdid="+cmdid);
         if( (!new_edit_mode && item.isNewItem())      ||
             (new_edit_mode  && item.isObsoleteItem()) )
         {
            if(DEBUG) debug("  skipping item: new_edit_mode="+new_edit_mode+",new_item="+item.isNewItem()+",obsolete_item="+item.isObsoleteItem());
            continue;
         }
         

         // Check whether the command belongs to a group that should be shown.
         // Custom commands should not be displayed,
         // they will put in the corresponding combo box.
         // added extra checks for commands in version 3, that should not be showned in the commandbar
         if( (!item.checkRemoved() && !item.isCustomCommand() && getLayoutMode()==SINGLE_LAYOUT) || (
              !item.getCommandId().equals(DELETE) && getLayoutMode()!=SINGLE_LAYOUT && !item.getCommandId().equals(DUPLICATEROW) &&
              !item.getCommandId().equals(EDITROW) && !item.getCommandId().equals(VIEWDETAILS) &&
              // Added by Terry 20120816
              // Approve row bar items
              !item.getCommandId().equals(APPROVESTART) && !item.getCommandId().equals(APPROVEROW) &&
              !item.getCommandId().equals(APPROVERETURN) && !item.getCommandId().equals(APPROVECANCEL) &&
              !item.getCommandId().equals(APPROVEVIEW) && !item.getCommandId().equals(APPROVESENDVIEW) &&
              !item.getCommandId().equals(APPROVESENDVIEWFIN) && !item.getCommandId().equals(OKLOV) &&
              // Added end
              // Added by Terry 20131001
              // Command bar in DocEdmControlVue page
              !item.getCommandId().equals(FILESAVE) && !item.getCommandId().equals(FILESAVEALL) &&
              !item.getCommandId().equals(FILEPRINT) && !item.getCommandId().equals(FILEPRINTALL) &&
              // Added end
              // Added by Terry 20130902
              // Add document reference bar
              !item.getCommandId().equals(DOCREF) &&
              // Added end
              !item.checkRemoved() && !item.isCustomCommand() && getLayoutMode()!=CUSTOM_LAYOUT && !item.getCommandId().equals(PROPERTIES)))
         {

            // Get current row.
            if(cmdid.equals(FIELDGOTO))
               item.setLabel(createFieldGotoTag());

            // Get the total number of rows.
            if(cmdid.equals(FIELDTOTROW))
               item.setLabel(createFieldTotRowTag());

            // Get the custom commands.
            if(cmdid.equals(FIELDPERFORM))
               item.setLabel(createFieldPerformTag());

            // Copy is shown in Action menu instead.
            if(cmdid.equals(DUPLICATEROW))
               continue;

            //already shown in cmdbar
            if(cmdid.equals(PROPERTIES))
               continue;

            //shown in action menu
            if (cmdid.equals(ROWHISTORY))
               continue;
            
            if(cmdid.equals(BACK) && rowset.countRows()==0)
               continue;

            if(isCustomCommandExists){   
               html.append("<img valign=middle align=center border0 src='",mgr.getASPConfig().getImagesLocation(), mgr.getUserTheme()+"/cmdbar_separator.gif'>");
               html.append("<img valign=middle align=center border=0 src='",getImageLocation(),"6px_space.gif'>");
               isCustomCommandExists =false;
            }
            
            // Add the command.
            item.getItemData(html);
            commandIsAdded = true;
         }
      }

      if(getLayoutMode()==SINGLE_LAYOUT && rowset.countRows() > 0)
      {
         // Show action button
         html.append(actionButton());
      }
      else if (getLayoutMode() == MULTIROW_LAYOUT)
      {
         // Added by Terry 20130316
         boolean has_table = getBlock().hasASPTable();
         if (!has_table || (has_table && !getBlock().getASPTable().isEditable()))
         {
         // Added end
            if((!findItem(ASPCommandBar.DELETE).isUserDefinedServerFunction() || multirow_delete_enabled) && (getBlock().hasASPTable() && getBlock().isCommandDefined(ASPRowSet.REMOVE) &&  IsEnabled(DELETE) && !multirow_delete_removed && getBlock().getASPTable().isRowSelectionEnabled() && rowset.countRows() > 0))
               html.append(multirowDeleteButton());
            
            if(isMultirowActionEnabled())
               html.append(actionButton());
            else if (getBlock().hasASPTable() && getBlock().getASPTable().isRowSelectionEnabled() && !page_controls_action_button && rowset.countRows() > 0)
               html.append(actionButton());
         }
      }

      if((mgr.isExplorer()||mgr.isMozilla()) && isMinimizeEnabled())
          html.append(minimizeButton());

      html.append("         </td></tr>\n");
      html.append("</table></td>");
      html.append("        \n\t\t<td NOWRAP>&nbsp;</td>");
      html.append("        \n\t</tr>\n</table>");
      html.append("</td></tr>");

      html.append("</table>");

      getBlock().appendPerformField(html);

   }

   String multirowDeleteButton()
   {
      AutoString tmp = new AutoString();
      try
      {
         String id = getBlock().getName() + "_MultiDelte";
         ASPManager mgr = getASPManager();
         String image_location = mgr.getToolBarImageLocation();

         tmp.append("<a href=\"javascript:if(multirowDeleteConfMessage('" , mgr.translateJavaText("FNDPOPCONFMULTIDEL: Delete Selected Rows?")+"','" ,mgr.translateJavaText("FNDPOPCONFMULTIDELNOROWS: No Rows Selected."), "','"+getBlock().getASPTable().getTableNo()+"')){commandSet('" + getBlock().getName() + ".Delete','"+Str.nvl(getClientFunction(this.DELETE),"")+"')}\"");

         if (getHoverEnabled())
         {
            ASPConfig cfg = getASPPage().getASPConfig();
            String imgloc  = getImageLocation();
            String normimg = cfg.getParameter("COMMAND_BAR/ICONS/DELETE/NORMAL","delete.png");
            String hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/DELETE/HOVER","delete_hov.png");

            // Modified by Terry 20130916
            // Multi-language toolbar
            // Original:
            // tmp.append(" onmouseover=\"javascript:", id,".src='",imgloc,hovimg);
            tmp.append(" onmouseover=\"javascript:", id,".src='",imgloc + getASPManager().getLanguageCode() + "/",hovimg);
            // Modified end
            tmp.append("';deselectActiveCommand();\"");
            // Modified by Terry 20130916
            // Multi-language toolbar
            // Original:
            // tmp.append(" onmouseout=\"javascript:", id,".src='",imgloc,normimg);
            tmp.append(" onmouseout=\"javascript:", id,".src='",imgloc + getASPManager().getLanguageCode() + "/",normimg);
            // Modified end
            tmp.append("';selectActiveCommand();\"");
         }
         String del_label = mgr.translate("FNDCBRMULTIROWDELETE: Delete Multiple Rows");
         tmp.append("><img onClick=\"menuClicked(this);\" name=\"",id,"\" border=0 src=\"");
         // Modified by Terry 20130916
         // Multi-language toolbar
         // Original:
         // tmp.append(image_location,mgr.getConfigParameter("COMMAND_BAR/ICONS/DELETE/NORMAL","delete.png"));
         tmp.append(image_location + getASPManager().getLanguageCode() + "/",mgr.getConfigParameter("COMMAND_BAR/ICONS/DELETE/NORMAL","delete.png"));
         // Modified end
         tmp.append("\" valign=middle align=center alt=\""+del_label+"\" title=\""+del_label+"\"></a><img valign=middle align=center border=0 src='",image_location,"6px_space.gif'>");
         return tmp.toString();
      }
      catch( Throwable any )
      {
         return tmp.toString();
      }
   }

   /**
    *Add the multirow delete button to the command bar.
    *Should be called in the preDefined().
    **/
   public void addMultirowDeleteButton()
   {
       try
      {
         modifyingImmutableAttribute("MULTIROW_DELETE_ENABLED");
         multirow_delete_enabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    *Removes the multirow delete button from the command bar.
    *Should be called in the preDefined().
    **/
   public void removeMultirowDeleteButton()
   {
       try
      {
         modifyingImmutableAttribute("MULTIROW_DELETE_REMOVED");
         multirow_delete_removed = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String addSaveQuery()
   {
      AutoString tmp = new AutoString();
      ASPManager mgr = getASPManager();

      //tmp.append("<td >");
      tmp.append("&nbsp;");
      tmp.append("<select name=__SAVED_QUERY class='selectbox' onChange='javascript:populateFields()'>");

      ASPField[] flds = getBlock().getFields();
      String value = "";

      String case_sensitive_flag = mgr.getConfigParameter("ADMIN/CASE_SENSITIVE_SEARCH","Y");

      value = "__CASESS_VALUE^" + case_sensitive_flag+ (char)31;

      if ("Y".equals(case_sensitive_flag))
         value += "CASESENCETIVE^TRUE"+ (char)31;
      else
         value += "CASESENCETIVE^FALSE"+ (char)31;


      for (int i=0; i<flds.length; i++)
      {
         if (flds[i].isQueryable()&& !flds[i].isHidden())
            value += flds[i].getName() + "^" + (char)31;
      }

      ASPBuffer aspbuf = getBlock().getQueryProfile();
      tmp.append("<option value=\"",value,"\" ></option>");

      String option_text = "";
      String query_name = mgr.readValue("__QUERY_NAME");

      for (int i=0; i<aspbuf.countItems();i++)
      {
         option_text = aspbuf.getNameAt(i);
         option_text = option_text.substring(option_text.indexOf('^')+1); //remove "Saved query^" from the name
         tmp.append("<option value=\"",aspbuf.getValueAt(i),"\" ",(option_text.equals(query_name)?" selected ":"")," >");
         tmp.append(option_text);
         tmp.append("</option>");
      }

      tmp.append("</select>");
      tmp.append("&nbsp;");
      String image_loc = mgr.getASPConfig().getImagesLocation();

      tmp.append("<a href=\"javascript:deleteQuery('",getBlock().getName(),".",DELETEQUERY,"')\">");
      tmp.append("<input type=hidden name=\"__QUERY_INDEX\" >");
      tmp.append("<img valign=middle align=center border=0 src=\"",image_loc+img_delete_query,"\" alt=\"",cmd_hint_deletequery,"\" title=\""+cmd_hint_deletequery+"\"></a>");
      tmp.append("&nbsp;");
      tmp.append("<a href=\"javascript:openSaveQueryDlg('",getBlock().getName(),".",SAVEQUERY,"','"+mgr.getASPConfig().getScriptsLocation()+"SaveQueryDlg.page')\">");
      tmp.append("<img valign=middle align=center border=0 src=\"",image_loc+img_save_query,"\" alt=\"",cmd_hint_savequery,"\" title=\""+cmd_hint_savequery+"\"></a>");
      tmp.append("<input type=hidden name=\"__QUERY_NAME\" >");
      tmp.append("&nbsp;");

      return tmp.toString();

   }


   private String minimizeButton()
   {
      AutoString tmp = new AutoString();

      String base_image_location = getASPManager().getASPConfig().getImagesLocation();

      String id = getBlock().getName() + "_Minimize";
      ASPManager mgr = getASPManager();

      String state;

      if((mgr.isExplorer() || mgr.isMozilla()) && (lay.isMinimized(lay.getID()) || (!lay.isMaximized(lay.getID()) && lay.isInitiallyMinimized())))
         state = "true";
      else
         state = "false";

      tmp.append("<a href=\"javascript:toggleStyleDisplay('cnt",getBlock().getName(),"','",Integer.toString(lay.getID()),"');");
      tmp.append("setMinimizeImage(document['",id,"'],'cnt",getBlock().getName());
      tmp.append("',",state,")\" ");

      tmp.append("><img src=\"",base_image_location, mgr.getUserTheme()+"/");

      if(state.equals("true"))
         tmp.append(mgr.getConfigParameter("COMMAND_BAR/ICONS/MAXIMIZE/NORMAL","max.gif"));
      else
         tmp.append(mgr.getConfigParameter("COMMAND_BAR/ICONS/MINIMIZE/NORMAL","min.gif"));
      tmp.append("\" border=0 name=\"",id,"\" valign=middle align=center></a></td>");

      return tmp.toString();
   }


   private String actionButton()
   {
      AutoString tmp = new AutoString();
      Vector cmds = getCustomCommands();
      Vector list = new Vector();
      ASPManager mgr = getASPManager();
      boolean showPopup = false;
      int STD_CMD_COUNT = 3; // If no of standard commands changed update this value and
                             // javascript variable STD_CMD_COUNT

      ASPBlockLayout lay = null;
      ASPTable tbl = null;

      if(getBlock().hasASPTable())
      {
          lay = getBlock().getASPBlockLayout();
          tbl = getBlock().getASPTable();

      }

      /*NOTE: If number of standard commands are changed
              update javascript variable STD_CMD_COUNT in showMultiMenu
       */

      // BlockLayout Properties
      /*if(lay.isMultirowLayout())
         list.addElement("false");
      else
         list.addElement("true");
       */

      // Document management
      if(rowset.getBlock().isDocMan() && isSingleLayout() && rowset.countRows() > 0)
      {
         list.addElement("true");
         getBlock().appendDocMan(tmp);
         showPopup = true; //log id #777
      }
      else
         list.addElement("false");

      // Duplicate
      if(getBlock().isCommandDefined(ASPRowSet.NEW) && isSingleLayout() && IsEnabled(DUPLICATEROW))
      {
         list.addElement("true");
         showPopup = true;
      } else
         list.addElement("false");

      //History stuff
      if(!getBlock().isHistoryDisabled()){
         if(getASPPage().getASPLov() == null && getBlock().getASPRowSet().countRows() != 0 && !mgr.isEmpty(getBlock().getLUName()) &&  !mgr.isEmpty(getBlock().getLUKeys()) && (isSingleLayout() || (isMultirowLayout() && tbl != null && tbl.isRowSelectionEnabled())))
         {
            list.addElement("true");
            showPopup = true;
         }
         else
            list.addElement("false");
      }else
         STD_CMD_COUNT--;

      try
      {
         prepareProfileInfo(true);
      }
      catch(Throwable any)
      {
         error(any);
      }

      // Custom commands
      for(int i = 0;i < cmds.size();i++)
      {
         ASPCommandBarItem cmd = (ASPCommandBarItem) cmds.elementAt(i);
         if(!cmd.isUserDisabled() && !cmd.isCustomCommandBarButton() && ( (isSingleLayout() && cmd.conditionsTrue(rowset.getCurrentRowNo()) ) || (isMultirowLayout() && !cmd.isRemovedFromMultirowAction() ) ) )
         {
            if((lay != null && tbl !=null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout() && cmd.hasValidConditions()) || (lay != null && tbl != null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout() && !cmd.isForceEnabledInMultiActionCommand()))
               list.addElement("FALSE");
            else
            {
               // Modified by Terry 20130912
               // Check property of cmd
               // Original:
               // list.addElement("true");
               if (cmd.isCmdProperty() && rowset.countRows() == 0)
                  list.addElement("false");
               else
                  list.addElement("true");
               // Modified end
            }
            showPopup = true;
         }
         else
            list.addElement("false");
      }

      // Custom groups
      Vector sub_list = new Vector();
      Vector pop_items = null;
      ASPPopup sub_pop = null;
      String sub_call = "";
      String action = "";
      boolean has_items = false;
      
      tmp.append("\n<SCRIPT language=JavaScript>");
      tmp.append("\n");
      tmp.append("\t","var __multiMenuNoRowsSelected = '",mgr.translateJavaScript("FNDCOMMULMENUNOROWS: No Rows Selected."),"';\n");
      tmp.append("\t","var __selectOnlyRowsHasMenu = '",mgr.translateJavaScript("FNDCOMONROWSHASMENU: Some Selected Rows cannot perform Selected Action."),"';\n");
      tmp.append("\n</SCRIPT>\n");
         
      for(int i = 0;i < custom_groups.size();i++)
      {
         has_items = false;
         sub_list.removeAllElements();
         String cmd_grp = custom_groups.elementAt(i).toString();
         boolean customGroupFound = false;
         int j = 0;

         while (j<=count-1)
         {
            if(items[j].getCustomGroup().equals(cmd_grp) && !items[j].checkRemoved())
            {
               customGroupFound = true;
               if(items[j].isUserDisabled() || (isSingleLayout() && !items[j].conditionsTrue(rowset.getCurrentRowNo())) || (isMultirowLayout() && items[j].isRemovedFromMultirowAction()))
                  sub_list.addElement("false");
               else
               {
                  if((lay != null && tbl !=null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout() && items[j].hasValidConditions()) || (lay != null && tbl != null && tbl.isRowSelectionEnabled() && isMultirowLayout() && !items[j].isForceEnabledInMultiActionCommand()))
                     sub_list.addElement("FALSE");
                  else
                  {
                     // Modified by Terry 20130912
                     // Check property of cmd
                     // Original:
                     // sub_list.addElement("true");
                     if (items[j].isCmdProperty() && rowset.countRows() == 0)
                        sub_list.addElement("false");
                     else
                        sub_list.addElement("true");
                     // Modified end
                  }
                     
                  has_items = true;

               }
            }
            j++;
         }
         
         if(!has_items)
            list.addElement("s_false");
         else
            list.addElement("s_true");

         if(getASPPage().isPopupExist("c"+cmd_grp))
             sub_pop = getASPPage().getASPPopup("c"+cmd_grp);

         if(sub_pop!= null && has_items)
         {
            if(lay != null && tbl !=null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout())
            {
               sub_call = sub_pop.generateCall(sub_list);
               sub_call = "javascript:__showSubMultiMenu('"+mgr.replace(sub_call,"'", "^")+"',"+custom_groups.size()+","+tbl.getTableNo()+","+i+",'"+tbl.SELECTBOX + tbl.getTableNo()+"',"+tbl.getMultirowActinEnableCondition()+");";
            }
            else
               sub_call = sub_pop.generateCall(sub_list);
         }
         else
            sub_call = "javascript:sbumenuDummy('"+cmd_grp+"')";

         pop_items = popup.getItems();

         for(int x =0; x <pop_items.size(); x++)
         {
             action = ((ASPPopup.menuItem)pop_items.elementAt(x)).getAction();
             if(action.indexOf(cmd_grp) > 0)
             {
                ASPPopup.menuItem mi = (ASPPopup.menuItem)pop_items.elementAt(x);
                mi.setAction(sub_call);
             }
         }

         if (customGroupFound)
         {
            //list.addElement("true");
            showPopup = true;
         }
         /*
         else
            list.addElement("false");
         */
      }

      if(!getBlock().isSendToDisabled())
         list.add(0,"s_true");
      
      if(!showPopup)
         return "";
      
      if(lay != null && tbl != null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout())
         tmp.append("<a href=javascript:showMultiMenu(\"",popup.generateCall(list),"\",'",tbl.SELECTBOX + tbl.getTableNo(),"',"+tbl.getTableNo()+","+tbl.getMultirowActinEnableCondition()+","+ STD_CMD_COUNT+"); ");
      else
         tmp.append("<a href=\"",popup.generateCall(list),"\" ");

      String id = getBlock().getName() + "_Actions";
      if (getHoverEnabled())
      {
         ASPConfig cfg = getASPPage().getASPConfig();
         String imgloc  = getImageLocation();
         String normimg = cfg.getParameter("COMMAND_BAR/ICONS/ACTIONS/NORMAL","actions.gif");
         String hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/ACTIONS/HOVER","actions_hov.gif");

         // Modified by Terry 20130916
         // Multi-language toolbar
         // Original:
         // tmp.append(" onmouseover=\"javascript:", id,".src='",imgloc,hovimg);
         tmp.append(" onmouseover=\"javascript:", id,".src='",imgloc + getASPManager().getLanguageCode() + "/",hovimg);
         // Modified end
         tmp.append("';deselectActiveCommand();\"");
         // Modified by Terry 20130916
         // Multi-language toolbar
         // Original:
         // tmp.append(" onmouseout=\"javascript:", id,".src='",imgloc,normimg);
         tmp.append(" onmouseout=\"javascript:", id,".src='",imgloc + getASPManager().getLanguageCode() + "/",normimg);
         // Modified end
         tmp.append("';selectActiveCommand();\"");
      }
      tmp.append("><img onClick=\"menuClicked(this);\" name=\"",id,"\" border=0 src=\"");
      // Modified by Terry 20130916
      // Multi-language toolbar
      // Original:
      // tmp.append(getImageLocation(),mgr.getConfigParameter("COMMAND_BAR/ICONS/ACTIONS/NORMAL","actions.gif"));
      tmp.append(getImageLocation() + getASPManager().getLanguageCode() + "/",mgr.getConfigParameter("COMMAND_BAR/ICONS/ACTIONS/NORMAL","actions.gif"));
      // Modified end
      tmp.append("\" valign=middle align=center></a>");

      tmp.append("\n<SCRIPT language=JavaScript>");
      tmp.append("function set",getBlock().getName(),"Command(cmd)\n{\n");
      tmp.append("\t","f.__", getBlock().getName(), "_" + ASPCommandBar.PERFORM + ".value = cmd;\n");
      tmp.append("}\n</SCRIPT>\n");

      return tmp.toString();

   }


   private int getCustomCommandIndex(String cmdId)
   {
      Vector cmds = this.getAllCustomCommands();
      int cmds_size = cmds.size();

      for (int i=0; i<cmds_size; i++)
      {
         ASPCommandBarItem item = (ASPCommandBarItem)cmds.elementAt(i);

         if (item.checkRemoved() || item.isUserDisabled() || Str.isEmpty(item.getName()) || item.isCustomCommandBarButton()) continue;

         if (cmdId.equals(item.getCommandId())) return i;
      }

      return -1;
   }


   /**
    * Store the specified profile information in the database.
    * Given buffer must have the same structure as the buffer returned
    * from getProfile().
    *
    * @see ifs.fnd.asp.ASPCommandBar.getProfile
    */
   public void saveProfile( ASPBuffer info )
   {
      saveProfile( info, true );
   }

   public void updateProfileBuffer( ASPBuffer info )
   {
      //called by CSL page to update all profile entries and save at once
      saveProfile( info, false );
   }


   private void saveProfile( ASPBuffer info, boolean save_it )
   {
      if(DEBUG) debug("ASPCommandBar.saveProfile()");
      try
      {
         profile = new CommandBarProfile();
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
    * this ASPCommandBar The buffer may be modified and then stored by calling
    * the method saveProfile().
    *
    * @see ifs.fnd.asp.ASPCommandBar.saveProfile
    */
   public ASPBuffer getProfile()
   {
      try
      {
         if(DEBUG) debug("ASPCommandBar.getProfile()");
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
      if(DEBUG) debug("ASPCommandBar.removeProfile()");
      try
      {
         ASPPage    page    = getASPPage();
         ASPProfile profile = page.getASPProfile();
         profile.remove(this);
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
      profile = new CommandBarProfile();
      profile.construct(this);
      user_profile_prepared = false;
      pre_profile = profile;
   }


   void prepareProfileInfo(boolean apply_settings) throws FndException
   {
      if( user_profile_prepared ) return;
      if(DEBUG) debug("ASPCommandBar.prepareProfileInfo():");

      if( pre_profile==null ) createBaseProfile(); //create defualt profile using page design time defintions

      if (!getASPManager().isDefaultLayout())
      {
         ASPProfile aspprf = getASPPage().getASPProfile();
         if(DEBUG) debug("   getASPProfile() = "+aspprf);
         profile = (CommandBarProfile)aspprf.get(ASPCommandBar.this,pre_profile);
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

      Vector cmds = getAllCustomCommands();
      int cmds_size = cmds.size();

      for (int i=0; i<cmds_size; i++)
      {
         ASPCommandBarItem item = (ASPCommandBarItem)cmds.elementAt(i);

         if (item.checkRemoved() || item.isUserDisabled() || Str.isEmpty(item.getName()) || item.isCustomCommandBarButton()) continue;
         if (!profile.isCommandEnabled(item.getCommandId())) item.userDisable();
      }

      user_profile_prepared = true;
   }


/**
 * Adjust the functions to the current layoutmode. Disable all the commands that shouldn't be visible in the current layout mode and
 * enable the rest. The logic for sorting out custom commands and other Popup commands are handled in addContent()
 */

   private void adjustModes()
   {
      int i;
      
      if(getASPPage().getVersion()<3)
      {
         for(i=14;i<count;i++)
            items[i].disable();
         return;
      }
      
      for (i=0;i<15;i++)
         items[i].disable();
      
      if (getLayoutMode()==MULTIROW_LAYOUT)
      {
         // Added by Terry 20130316
         boolean editrow_enabled = IsEnabled(EDITROW);
         boolean newrow_enabled = IsEnabled(NEWROW);
         boolean delete_enabled = IsEnabled(DELETE);
         // Added end
         for(i=14;i<count;i++)
         {
            if(items[i].getCommandId().equals(FIND)
                  || items[i].getCommandId().equals(NEWROW)
                  || items[i].getCommandId().equals(EDITROW)
                  || items[i].getCommandId().equals(DELETE)
                  || items[i].getCommandId().equals(VIEWDETAILS)
                  // Added by Terry 20130315
                  // Edit command in overview
                  || items[i].getCommandId().equals(OVERVIEWEDIT)
                  || items[i].getCommandId().equals(OVERVIEWNEW)
                  || items[i].getCommandId().equals(OVERVIEWDELETE)
                  || items[i].getCommandId().equals(OVERVIEWSAVE)
                  || items[i].getCommandId().equals(OVERVIEWCANCEL)
                  // Added end
                  // Added by Terry 20120929
                  // Lov bar in multi-select mode
                  || items[i].getCommandId().equals(OKLOV)
                  // Added end
                  || items[i].getCommandId().equals(DUPLICATEROW)
                  || items[i].getCommandId().equals(SAVE)
                  || items[i].getCommandId().equals(FORWARD)
                  || items[i].getCommandId().equals(BACKWARD)
                  || items[i].getCommandId().equals(ROWHISTORY)
                  || items[i].getCommandId().equals(PROPERTIES)
                  || items[i].getCommandId().equals(OKIID)
                  || items[i].getCommandId().equals(FIRST) 
                  || items[i].getCommandId().equals(LAST))    
            {
               // Added by Terry 20121009
               if (items[i].getCommandId().equals(OKLOV))
               {
                  if ((getASPPage().getASPLov() != null || "DynamicSel".equals(getASPPage().getPageName())) && (getBlock().getASPTable().isRowSelectionEnabled() || getASPPage().getASPLov().isTreeListLov()))
                  {
                     if (!items[i].isUserDisabled() && getASPRowSet().countRows() > 0)
                        items[i].enable();
                     else
                        items[i].disable();
                  }
               }
               else if (items[i].getCommandId().equals(OKIID))
               {
                  if ("DynamicIIDLov".equals(getASPPage().getPageName()) && IsEnabled(OKIID))
                  {
                     if (!items[i].isUserDisabled())
                        items[i].enable();
                     else
                        items[i].disable();
                  }
               }
               // Added end
               // Added by Terry 20130315
               // Edit command in overview
               else if(items[i].getCommandId().equals(OVERVIEWEDIT))
               {
                  if(!items[i].isUserDisabled() && editrow_enabled && getASPRowSet().countRows() > 0 && getBlock().hasASPTable() && !getBlock().getASPTable().isEditable())
                     items[i].enable();
                  else
                     items[i].disable();
               }
               else if (items[i].getCommandId().equals(OVERVIEWNEW)    ||
                        items[i].getCommandId().equals(OVERVIEWDELETE) ||
                        items[i].getCommandId().equals(OVERVIEWSAVE)   ||
                        items[i].getCommandId().equals(OVERVIEWCANCEL))
               {
                  if (!items[i].isUserDisabled() && getBlock().hasASPTable() && getBlock().getASPTable().isEditable())
                  {
                     if (items[i].getCommandId().equals(OVERVIEWNEW))
                     {
                        if (!newrow_enabled)
                           items[i].disable();
                        else
                           items[i].enable();
                     }
                     else if (items[i].getCommandId().equals(OVERVIEWDELETE))
                     {
                        if (!delete_enabled || getASPRowSet().countRows() == 0)
                           items[i].disable();
                        else
                           items[i].enable();
                     }
                     else
                        items[i].enable();
                  }
                  else
                     items[i].disable();
               }
               // Added end
               else
               {
                  // Added by Terry 20130316
                  if (getBlock().hasASPTable() && getBlock().getASPTable().isEditable())
                     items[i].disable();
                  else
                  {
                  // Added end
                     if(!items[i].isUserDisabled())
                     {
                        items[i].enable();
                     }
                     else
                        items[i].disable();
                  }
               }
            }
            else if (!items[i].isCustomCommand())
               items[i].disable();
         }
      }
      else if (getLayoutMode()==FIND_LAYOUT)
      {
         for(i=14;i<count;i++)
         {
            if(items[i].getCommandId().equals(OKFIND) 
            || items[i].getCommandId().equals(CANCELFIND)
            || items[i].getCommandId().equals(COUNTFIND)
            // Added by Terry 20130918
            // Advanced Query
            || items[i].getCommandId().equals(ADVANCEDFIND)
            // Added end
            || (items[i].getCommandId().equals(NEWROW) && lay.getDefaultLayoutMode()==FIND_LAYOUT)
            || items[i].getCommandId().equals(SAVE) || items[i].getCommandId().equals(PROPERTIES))
            {
               if(!items[i].isUserDisabled())
                  items[i].enable();
               else items[i].disable();
            }
            else if (!items[i].isCustomCommand())
               items[i].disable();
         }
      }
      else if (getLayoutMode()==EDIT_LAYOUT)
      {
         for(i=14;i<count;i++)
         {
            if(items[i].getCommandId().equals(SAVERETURN) || items[i].getCommandId().equals(CANCELEDIT)
                  || items[i].getCommandId().equals(BACKWARD) || items[i].getCommandId().equals(FORWARD)
                  || items[i].getCommandId().equals(FIRST) || items[i].getCommandId().equals(LAST)
                  || items[i].getCommandId().equals(SAVE) || items[i].getCommandId().equals(PROPERTIES))
            {
               if(!items[i].isUserDisabled())
                  items[i].enable();
               else items[i].disable();
            }
            else if (!items[i].isCustomCommand())
               items[i].disable();
         }
      }
      else if (getLayoutMode()==NEW_LAYOUT)
      {
         for(i=14;i<count;i++)
         {
            if(items[i].getCommandId().equals(SAVERETURN) || items[i].getCommandId().equals(SAVENEW)
                  || items[i].getCommandId().equals(CANCELNEW)
                  || items[i].getCommandId().equals(SAVE) || items[i].getCommandId().equals(PROPERTIES))
            {
               if(!items[i].isUserDisabled())
                  items[i].enable();
               else items[i].disable();
            }
            else if (!items[i].isCustomCommand())
               items[i].disable();
         }
      }
      else if (getLayoutMode()==SINGLE_LAYOUT)
      {
         for(i=14;i<count;i++)
         {
            if(items[i].getCommandId().equals(FIND) || items[i].getCommandId().equals(NEWROW)
                  || items[i].getCommandId().equals(BACK)
                  || items[i].getCommandId().equals(DUPLICATEROW)
                  || items[i].getCommandId().equals(BACKWARD) || items[i].getCommandId().equals(FORWARD)
                  || items[i].getCommandId().equals(FIRST) || items[i].getCommandId().equals(LAST)
                  || items[i].getCommandId().equals(SAVE)
                  || items[i].getCommandId().equals(ROWHISTORY)
                  || items[i].getCommandId().equals(PROPERTIES))
            {
               if(!items[i].isUserDisabled())
               {
                  items[i].enable();
               }
               else items[i].disable();
            }
            else if(items[i].getCommandId().equals(EDITROW)       || items[i].getCommandId().equals(DELETE))
            {
               if(!items[i].isUserDisabled() && getASPRowSet().countRows()>0)
               {
                  items[i].enable();
               }
               else items[i].disable();
            }
            // Added by Terry 20120816
            // Approve row bar items
            else if(items[i].getCommandId().equals(APPROVESTART)  || items[i].getCommandId().equals(APPROVEROW)    ||
                  items[i].getCommandId().equals(APPROVERETURN) || items[i].getCommandId().equals(APPROVECANCEL) ||
                  items[i].getCommandId().equals(APPROVEVIEW)   || items[i].getCommandId().equals(APPROVESENDVIEW) ||
                  items[i].getCommandId().equals(APPROVESENDVIEWFIN))
            {
               if(!items[i].isUserDisabled() && getASPRowSet().countRows()>0 && getBlock().getMasterBlock() == null && !getASPManager().isEmpty(getBlock().getLUName()))
                  items[i].enable();
               else
                  items[i].disable();
            }
            // Added end
            // Added by Terry 20131001
            // Command bar in DocEdmControlVue page
            else if(items[i].getCommandId().equals(FILESAVE)  || items[i].getCommandId().equals(FILESAVEALL)    ||
                    items[i].getCommandId().equals(FILEPRINT) || items[i].getCommandId().equals(FILEPRINTALL))
            {
               if(!items[i].isUserDisabled() && getASPRowSet().countRows() > 0 && "DocEdmControlVue".equals(getASPPage().getPageName()))
                  items[i].enable();
               else
                  items[i].disable();
            }
            // Added end
            // Added by Terry 20130902
            // Add document reference bar
            else if(items[i].getCommandId().equals(DOCREF))
            {
               if(!items[i].isUserDisabled() && getASPRowSet().countRows() > 0) // && !getASPManager().isEmpty(getBlock().getLUName()))
                  items[i].enable();
               else
                  items[i].disable();
            }
            // Added end
            else if(items[i].getCommandId().equals(NOTES))
            {
               if(!items[i].isUserDisabled() && getBlock().getASPRowSet().countRows()>0 && !getASPManager().isRWCHost() && getASPManager().isAuroraFeaturesEnabled() && getBlock().getMasterBlock() == null && !getASPManager().isEmpty(getBlock().getLUName()))
                  items[i].enable();
               else
                  items[i].disable();
            }
            else if (!items[i].isCustomCommand())
               items[i].disable();
         }
      }
      else if (getLayoutMode()==CUSTOM_LAYOUT)
      {
         // Custom layout - enable all except those disabled by the user.
         for(i=14;i<count;i++)
         {
            if(items[i].isUserEnabled() && !items[i].getCommandId().equals(PERFORM))
               items[i].enable();
            else items[i].disable();
         }
      }
      
      else if (getLayoutMode()==NONE)
      {
         // No layout - disable only properties.
         for(i=14;i<count;i++)
         {
            if (items[i].getCommandId().equals(PROPERTIES))
               items[i].disable();
         }
      }
   }

   public boolean IsEnabled(String cmd_id)
   {
      try {
         ASPCommandBarItem item = findItem(cmd_id);
         return !(item.checkDisabled() | item.checkRemoved());
      }
      catch (Throwable e) {error(e);}
      return false;
   }
   
   private FndException commandGroupNotFound(int commandGroup)
   {
      return new FndException("FNDCBRGRNEX: Command group &1 does not exist!", ""+commandGroup);
   }


   /**
   * Removes an added custom command from row actions in the multirow mode.
   * @param commandId Command Identifier.
   * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
   */
   public void removeFromRowActions(String commandId)
   {
      try
      {
         this.findItem(commandId).removeFromRowActions();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
   * Forces to enable an added custom command even when there are no rows selected.
   * @param commandId Command Identifier.
   * @see     ifs.fnd.asp.ASPCommandBar#addCustomCommand
   */
   public void forceEnableMultiActionCommand(String commandId)
   {
      ASPCommandBarItem item;
      try
      {
         item = this.findItem(commandId);
         item.forceEnableMultiActionCommand();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /**
    * Forces to enable the table properties icon. Although forced the icon will only be visible if only
    * an ASPTable is available for the underlying ASPblock.
    **/
   public void forceEnablePropertiesIcon()
   {
      for(int i=0; i<items.length-1; i++)
      {
         if(items[i].getCommandId().equals(PROPERTIES))
         {
            items[i].userEnable();
            break;
         }
      }
   }

   Vector getAllPopupField()
   {
      Vector pops = new Vector();
      ASPField fields[] = getBlock().getFields();
      for(int f=0; f<fields.length; f++)
      {
         if(fields[f].hasPopupMenu())
            pops.add(fields[f]);
      }
      return pops;
   }

   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   private String showMobileBar()
   {
      
      if(getASPPage().getASPLov()==null)
      {
         getASPPage().increaseBlockCount();
         getASPPage().setDefaultCommand(lay.getDefaultCommand(lay.getLayoutMode()));
         getASPPage().setDefaultBlock(lay.getName());
      }
      int i;
      try
      {

         if (!( lay.isCustomLayout() || (lay.getLayoutMode() == lay.NONE) ))
            enforceCmdBarSecurity();

         tmpbuf.clear();

         addMobileContent(tmpbuf);

         return tmpbuf.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }
   
   private void addMobileContent( AutoString html ) throws Exception
   {
      ASPManager mgr = getASPManager();

      String imageFileName, status;

      boolean commandIsAdded = false;
      ASPPage page = getASPPage();

      removeCommandGroup(CMD_GROUP_CUSTOM);

      html.append("<table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
      html.append("   <tr><td><img border=\"0\" src=\""+mgr.getASPConfig().getMobileImageLocation()+"6pxgap.gif\"></td></tr>\n");
      html.append("</table>\n");
      html.append("<table border=\"0\" width=\"100%\" cellpadding=\"1\" class=\"pageCommandBar\">\n");
      html.append("   <tr>\n");

      boolean new_edit_mode = page.compatibility.newEditMode();

      // In Webkit version 3, the Toolbar is dynamically generated depending on the layout mode.
      if(getASPPage().getVersion()>=3)
          adjustModes();

      html.append("      <td nowrap valign=\"middle\" "+(mgr.isRTL()?"align=\"right\"":"align=\"left\"")+">&nbsp;");
      String headmodediv;
      if(getBlock().hasTitle() && !mgr.isMobileVersion())
          headmodediv=" - ";
      else
          headmodediv="";
      
      if(isModeLabelEnabled())
      {
         if(isFindLayout())
            html.append(mgr.translateJavaText("FNDCBRFINDMODE: Find")+headmodediv);
         if(isEditLayout())
             html.append(mgr.translateJavaText("FNDCBREDITMODE: Edit")+headmodediv);
         if(isNewLayout())
             html.append(mgr.translateJavaText("FNDCBRNEWMODE: New")+headmodediv);
      }
      if(getBlock().hasTitle() && !mgr.isMobileVersion())
         html.append(getBlock().getTitle());
      html.append("&nbsp;</td>\n");

      if(isSingleLayout() || isEditLayout() || isMultirowLayout())
      {
         int y;
         for(y=0;y<count;y++)
            if (items[y].getCommandId().equals(FIRST)) break;

         if(!items[y+1].isUserDisabled() && !items[y+2].isUserDisabled() )
         {
            if(isMultirowLayout())
            {
               ASPContext ctx = getASPPage().getASPContext();
               int skipped = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".SKIP_ROWS","0"));
               int dbcount = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".DBCOUNT","0"));
               int buffer_size = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".MAX_ROWS","-1"));
               if(buffer_size == -1)
                  buffer_size = Integer.parseInt(getASPManager().getConfigParameter("ADMIN/MOBILE_BUFFER_SIZE","10"));
               ASPBuffer ctxbuf = ctx.readBuffer("__"+getBlock().getName()+".QUERYBUFFER");
               if(ctxbuf!=null && dbcount>buffer_size && rowset.countRows()>0)
               {
                  html.append("      <td nowrap >");
                  if(skipped>0) items[y+1].getMobileItemData(html);
                  html.append("</td>\n");
                  skipped++;
                  html.append("      <td nowrap align=\"center\" valign=\"middle\">",mgr.translateJavaText("FNDMOBILECBRRCYRRECNO: &1-&2/&3",Integer.toString(skipped),Integer.toString(skipped+rowset.countRows()-1),Integer.toString(dbcount)),"&nbsp;");
                  html.append("</td>\n");
                  html.append("      <td>");

                  skipped--;
                  if((skipped+buffer_size)<dbcount) items[y+2].getMobileItemData(html);
                  html.append("</td>\n");
               }
            }
            else
            {
               html.append("      <td nowrap>");
               if(rowset.countRows()>1 && rowset.getCurrentRowNo()>0)
               {
                  items[y].getMobileItemData(html);
                  items[y+1].getMobileItemData(html);
               }
               html.append("</td>\n");

               int x;
               if (rowset.countRows()==0) x=0;
               else 
               {
                  x=rowset.getCurrentRowNo()+1;
                  html.append("      <td nowrap align=\"center\" valign=\"middle\">",mgr.translateJavaText("FNDMOBILECBRRECNO: &1/&2",Integer.toString(x),Integer.toString(rowset.countRows())),"&nbsp;</td>\n");
               }

               html.append("      <td nowrap>");
               if(rowset.getCurrentRowNo()<(rowset.countRows()-1))
               {
                  items[y+2].getMobileItemData(html);
                  items[y+3].getMobileItemData(html);
               }
               html.append("</td>\n");
            }
         }
      }

      html.append("      <td");
      if (mgr.isRTL())      
         html.append(" nowrap align=\"left\">");
      else
         html.append(" nowrap align=\"right\">");

      for ( int i=0; i <= count-1; i++ )
      {
         ASPCommandBarItem item = items[i];
         String cmdid = item.getCommandId();
         if(cmdid.equals(BACKWARD) || cmdid.equals(FORWARD) || cmdid.equals(FIRST) || cmdid.equals(LAST)) continue;
         if(DEBUG) debug("ASPCommandBar.addMobileContent(): cmdid="+cmdid);
         if( (!new_edit_mode && item.isNewItem())      ||
             (new_edit_mode  && item.isObsoleteItem()) )
         {
            if(DEBUG) debug("  skipping item: new_edit_mode="+new_edit_mode+",new_item="+item.isNewItem()+",obsolete_item="+item.isObsoleteItem());
            continue;
         }
      }
      
      for ( int i=0; i <= count-1; i++ )
      {
         ASPCommandBarItem item = items[i];
         String cmdid = item.getCommandId();
         if(cmdid.equals(BACKWARD) || cmdid.equals(FORWARD) || cmdid.equals(FIRST) || cmdid.equals(LAST)) continue;
         if( (!new_edit_mode && item.isNewItem())||(new_edit_mode  && item.isObsoleteItem()) )
         {
            if(DEBUG) debug("  skipping item: new_edit_mode="+new_edit_mode+",new_item="+item.isNewItem()+",obsolete_item="+item.isObsoleteItem());
            continue;
         }

         // Check whether the command belongs to a group that should be shown.
         // Custom commands should not be displayed,
         // they will put in the corresponding combo box.
         // added extra checks for commands in version 3, that should not be showned in the commandbar
         if( (!item.checkRemoved() && !item.isCustomCommand() && getLayoutMode()==SINGLE_LAYOUT) || (
              !item.getCommandId().equals(DELETE) && getLayoutMode()!=SINGLE_LAYOUT && !item.getCommandId().equals(DUPLICATEROW) &&
              !item.getCommandId().equals(EDITROW) && !item.getCommandId().equals(VIEWDETAILS) &&
              // Added by Terry 20130315
              // Edit command in overview
              !item.getCommandId().equals(OVERVIEWEDIT) && !item.getCommandId().equals(OVERVIEWNEW) &&
              !item.getCommandId().equals(OVERVIEWDELETE) && !item.getCommandId().equals(OVERVIEWSAVE) &&
              !item.getCommandId().equals(OVERVIEWCANCEL) &&
              // Added end
              // Added by Terry 20120816
              // Approve row bar items
              !item.getCommandId().equals(APPROVESTART) && !item.getCommandId().equals(APPROVEROW) &&
              !item.getCommandId().equals(APPROVERETURN) && !item.getCommandId().equals(APPROVECANCEL) &&
              !item.getCommandId().equals(APPROVEVIEW) && !item.getCommandId().equals(APPROVESENDVIEW) &&
              !item.getCommandId().equals(APPROVESENDVIEWFIN) && !item.getCommandId().equals(OKLOV) &&
              // Added end
              // Added by Terry 20131001
              // Command bar in DocEdmControlVue page
              !item.getCommandId().equals(FILESAVE) && !item.getCommandId().equals(FILESAVEALL) &&
              !item.getCommandId().equals(FILEPRINT) && !item.getCommandId().equals(FILEPRINTALL) &&
              // Added end
              // Added by Terry 20130902
              // Add document reference bar
              !item.getCommandId().equals(DOCREF) &&
              // Added end
              !item.checkRemoved() && !item.isCustomCommand() && getLayoutMode()!=CUSTOM_LAYOUT && !item.getCommandId().equals(PROPERTIES)))
         {

            // Get current row.
            if(cmdid.equals(FIELDGOTO))
               item.setLabel(createFieldGotoTag());

            // Get the total number of rows.
            if(cmdid.equals(FIELDTOTROW))
               item.setLabel(createFieldTotRowTag());

            // Get the custom commands.
            if(cmdid.equals(FIELDPERFORM))
               item.setLabel(createFieldPerformTag());

            // Copy is shown in Action menu instead.
            if(cmdid.equals(DUPLICATEROW))
               continue;

            //already shown in cmdbar
            if(cmdid.equals(PROPERTIES))
               continue;

            //shown in action menu
            if (cmdid.equals(ROWHISTORY))
               continue; 

            if(cmdid.equals(BACK) && rowset.countRows()==0)
               continue;
            
            // Add the command.
            item.getMobileItemData(html);
            commandIsAdded = true;


         }

      }
      
      html.append("</td>\n");
      html.append("   </tr>\n");
      html.append("</table>\n");
      
      html.append(ASPPage.BEGIN_SCRIPT_TAG);
      html.append("function set",getBlock().getName(),"Command(cmd)\n{\n");
      html.append("\t","f.__", getBlock().getName(), "_" + ASPCommandBar.PERFORM + ".value = cmd;\n");
      html.append("}\n",ASPPage.END_SCRIPT_TAG,"\n");
      
      if(isSingleLayout() && getBlock().getASPRowSet().countRows()!=0 && (!getBlock().isHistoryDisabled() || getBlock().isDocMan() || getCustomCommands().size()!=0))
      {
         // Show action button
         html.append("<table class=\"actionBar\" width=\"100%\" cellpadding=\"2\">\n");
         html.append("   <tr>\n");
         html.append("      <td align=\"left\" valign=\"middle\" class=\"normalTextValue\">");
         html.append(getActionsForMobile());
         html.append("</td>\n");
         html.append("   </tr>\n");
         html.append("</table>\n");        
      }

      getBlock().appendPerformField(html);

   }
   
   public String addSaveMobileQuery()
   {
      AutoString tmp = new AutoString();
      ASPManager mgr = getASPManager();

      tmp.append("<select name=__SAVED_QUERY class='selectbox' onChange='javascript:populateFields()'>");

      ASPField[] flds = getBlock().getFields();
      String value = "";

      String case_sensitive_flag = mgr.getConfigParameter("ADMIN/CASE_SENSITIVE_SEARCH","Y");

      value = "__CASESS_VALUE^" + case_sensitive_flag+ (char)31;

      if ("Y".equals(case_sensitive_flag))
         value += "CASESENCETIVE^TRUE"+ (char)31;
      else
         value += "CASESENCETIVE^FALSE"+ (char)31;


      for (int i=0; i<flds.length; i++)
      {
         if (flds[i].isQueryable()&& !flds[i].isHidden())
            value += flds[i].getName() + "^" + (char)31;
      }

      ASPBuffer aspbuf = getBlock().getQueryProfile();
      tmp.append("\n\t   ");
      tmp.append("<option value=\"",value,"\" ></option>");

      String option_text = "";
      String query_name = mgr.readValue("__QUERY_NAME");

      for (int i=0; i<aspbuf.countItems();i++)
      {
         option_text = aspbuf.getNameAt(i);
         if(i==0)
            option_text = mgr.translate("FNDBLOCKPROPREQRY: Previous Query");
         else
            option_text = option_text.substring(option_text.indexOf('^')+1); //remove "Saved query^" from the name

         tmp.append("\n\t   ");
         tmp.append("<option value=\"",aspbuf.getValueAt(i),"\" ",(option_text.equals(query_name)?" selected ":"")," >");
         tmp.append(option_text);
         tmp.append("</option>");
      }
      tmp.append("\n\t</select>");
      tmp.append("&nbsp;");      
      String image_loc = mgr.getASPConfig().getImagesLocation();
      tmp.append("<a href=\"javascript:deleteQuery('",getBlock().getName(),".",DELETEQUERY,"')\">");
      tmp.append("<input type=hidden name=\"__QUERY_INDEX\" >");
      tmp.append("<img valign=middle align=center border=0 src=\"",image_loc+img_delete_query,"\" alt=\"",cmd_hint_deletequery,"\" title=\""+cmd_hint_deletequery+"\"></a>");
      tmp.append("&nbsp;");
      
      tmp.append("<input type=\"text\" size=\"5\" class=\"editableTextField\" name=\"__",getBlock().getName(),"_QUERY_NAME\" id=\"__",getBlock().getName(),"_QUERY_NAME\">");      
      tmp.append("&nbsp;");
      tmp.append("<a href=\"javascript:saveQuery('",getBlock().getName(),".",SAVEQUERY,"',f.__"+getBlock().getName()+"_QUERY_NAME.value)\">");
      tmp.append("<img valign=middle align=center border=0 src=\"",image_loc+img_save_query,"\" alt=\"",cmd_hint_savequery,"\" title=\""+cmd_hint_savequery+"\"></a>");
      tmp.append("<input type=hidden name=\"__QUERY_NAME\" >");
      
      return tmp.toString().replace((char)31,'~'); //Limitation due to the face that pda scripting cannot handle char31
   }

   private String getActionsForMobile()
   {
      AutoString tmp = new AutoString();
      Vector cmds = getCustomCommands();
      Vector list = new Vector();
      ASPManager mgr = getASPManager();
      boolean showPopup = false;
      
      ASPBlockLayout lay = null;
      ASPTable tbl = null;

      if(getBlock().hasASPTable())
      {
          lay = getBlock().getASPBlockLayout();
          tbl = getBlock().getASPTable();
      }

      // Document management
      if(rowset.getBlock().isDocMan() && isSingleLayout() && rowset.countRows() > 0)
      {
         list.addElement("true");
         getBlock().appendDocMan(tmp);
         showPopup = true;
      }
      else
         list.addElement("false");
      
      // Duplicate
      if(getBlock().isCommandDefined(ASPRowSet.NEW) && isSingleLayout() && IsEnabled(DUPLICATEROW))
      {
         list.addElement("true");
         showPopup = true;
      } else
         list.addElement("false");

      //History stuff
      if(!getBlock().isHistoryDisabled())
         if(getASPPage().getASPLov() == null && getBlock().getASPRowSet().countRows() != 0 && !mgr.isEmpty(getBlock().getLUName()) &&  !mgr.isEmpty(getBlock().getLUKeys()) && (isSingleLayout() || (isMultirowLayout() && tbl != null && tbl.isRowSelectionEnabled())))
         {
            list.addElement("true");
            showPopup = true;
         }
         else
            list.addElement("false");

      // Custom commands
      for(int i = 0;i < cmds.size();i++)
      {
         ASPCommandBarItem cmd = (ASPCommandBarItem) cmds.elementAt(i);
         if(!cmd.isUserDisabled() && ( (isSingleLayout() && cmd.conditionsTrue(rowset.getCurrentRowNo()) ) || (isMultirowLayout() && !cmd.isRemovedFromMultirowAction() ) ) )
         {
            if((lay != null && tbl !=null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout() && cmd.hasValidConditions()) || (lay != null && tbl != null && tbl.isRowSelectionEnabled() && lay.isMultirowLayout() && !cmd.isForceEnabledInMultiActionCommand()))
               list.addElement("FALSE");
            else
               list.addElement("true");
            showPopup = true;
         }
         else
            list.addElement("false");
      }

      return popup.generateMobileCall(list);
   }
   
   // Added by Terry 20130911
   // Command bar item property
   public void setCmdProperty(String command_id, String name, String value)
   {
      try
      {
         modifyingMutableAttribute("CMD_PROPERTY");
         ASPCommandBarItem item = findItem(command_id);
         if (item != null)
         {
            item.setCmdProperty(name, value);
            removeFromMultirowAction(command_id);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   public boolean isCmdProperty(String command_id) throws FndException
   {
      ASPCommandBarItem item = findItem(command_id);
      if (item != null)
         return item.isCmdProperty();
      return false;
   }
   
   public String getCmdPropertyString(String command_id, ASPRowSet set) throws FndException
   {
      ASPCommandBarItem item = findItem(command_id);
      if (item != null && item.isCmdProperty())
         return item.getCmdPropertyString(set);
      return "";
   }
   // Added end
   
   
   // Added by Terry 20131007
   // Show frame space mark
   public void enableCmdShowFrameSpace()
   {
      try
      {
         modifyingImmutableAttribute("CMD_SHOW_FRAME_SPACE");
         cmd_show_frame_space = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Disable frame space
   public void disableCmdShowFrameSpace()
   {
      try
      {
         modifyingImmutableAttribute("CMD_SHOW_FRAME_SPACE");
         cmd_show_frame_space = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   public boolean isCmdShowFrameSpace()
   {
      return cmd_show_frame_space;
   }
   // Added end
   
   // Added by Terry 20140424
   // Get All Custom Command Names
   public Vector getAllCustomCommandsName()
   {
      Vector cmds = new Vector();
      for( int i=1; i<count; i++ )
         if(items[i].isCustomCommand() && ( !Str.isEmpty(items[i].getName())))
            cmds.addElement(items[i].getCommandId());
      
      return cmds;
   }
   // Added end
   
   // Added by Terry 20140926
   // Set Confirm Msg of command bar item
   public void setCmdConfirmMsg(String command_id, String confirm_msg)
   {
      try
      {
         modifyingMutableAttribute("CMD_CONFIRM_MSG");
         ASPCommandBarItem item = findItem(command_id);
         if (item != null)
         {
            item.setCmdConfirmMsg(confirm_msg);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   public boolean isCmdConfirmMsg(String command_id) throws FndException
   {
      ASPCommandBarItem item = findItem(command_id);
      if (item != null)
         return item.isCmdConfirmMsg();
      return false;
   }
   // Added end
}

