/*
 *
 *                 IFS Research & Development
 *
 *  This program is protected by copyright law and by international
 *  conventions. All licensing, renting, lending or copying (including
 *  for private use), and all other use removeof the program, which is not
 *  expressively permitted by IFS Research & Development (IFS), is a
 *  violation of the rights of IFS. Such violations will be reported to the
 *  appropriate authorities.
 *
 *  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 *  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 * File        : ASPCommandBarItem.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Micke A  1998-May-19 - Created
 *    Micke A  1998-Jun-23 - Added the ASPCommandBar.removeCustomCommand(...) method.
 *    Micke A  1998-Jul-01 - Added the visualization of the record status.
 *    Micke A  1998-Jul-08 - Added the "normal" image to the visualization of the record status.
 *    Micke A  1998-Jul-09 - Changed "enableRecordStatus()" to "enableRowStatus()".
 *                           Changed "disableRecordStatus()" to "disableRowStatus()".
 *                           (Now these names correspond to the ASPTable methods.)
 *    Jacek P  1998-Jul-15 - Changed access level for function findItem() to package.
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-21 - Changes due to redesigned structure of
 *                           the ASPConfig.ifm file (Log id:#2623).
 *    Jacek P  1998-Oct-01 - Added translation of labels (ToDo:#2688). Font
 *                           and color parameters fetched from ASPConfig.ifm.
 *    Jacek P  1998-Oct-13 - Added possibility to show icons instead of labels
 *                           (ToDo:#2757).
 *    Jacek P  1998-Oct-14 - Function removeCommandGroup() does remove only
 *                           for Custom group, only disable otherwise.
 *                           An icon in status placeholder always shown.
 *    Jacek P  1998-Oct-20 - Added new command in the SEARCH group for setting
 *                           of the Favorite Query (Todo: #2811).
 *    Jacek P  1998-Oct-21 - Added hints to command icons.
 *    Micke A  1998-Nov-25 - Improved HTML generation using CSS (ToDo: #2954).
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageSubElement instead of ASPObject.
 *                           Class ASPCommandBarItem extends now ASPPageSubElement.
 *    Marek D  1999-Mar-02 - Implementation of ASPPoolElement state diagram
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Mar-19 - Removed calls to getConfigParameter() during request
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Marek D  1999-May-11 - Removed some 'new' during generation of HTML
 *    Jacek P  1999-May-26 - Added buttons 'toggle' and 'save'. Prepared for 'duplicate'.
 *    Jacek P  1999-Jun-09 - Added new boolean flag 'user_server_func'(and
 *                           function isUserDefinedServerFunction() ), which is true
 *                           if user has redefined the server function. Methods
 *                           disableCommand() and enableCommand() automatically
 *                           disables/enables DUPLICATE button with PREPARE.
 *    Jacek P  1999-Jun-18 - Tool tip text always shown.
 *    Jacek P  1999-Jul-16 - Added 'nowrap'.
 *    Johan S  1999-Aug-16 - Added code in getItemData for hover-buttons.
 *                           Also added immutable attributes in ASPCommandBarItems:
 *                           private string icon_enabled_flat, icon_disabled_flat
 *                           Further more, code was added in ASPCommandBarItem constructor
 *                           to set the filenames for the new hover buttons.
 *    Johan S  1999-Aug-18 - Moved hover_enabled check to ASPCommandBar construct
 *                           Added function getHoverEnabled.
 *    Jacek P  1999-Aug-30 - Hover flag changed to default 'N' in construct().
 *                           Should be fetched from ASPConfigFile .....
 *    Jacek P  1999-Sep-06 - Removed word 'record' from hints on browse buttons.
 *    Johan S  1999-Sep-13 - Added another, overloaded, function createImgTag.
 *                           This function adds height and width attributes to the
 *                           toolbar divider image.
 *    Jacek P  2000-Jan-21 - Implementation of portal:
 *                           call to ASPManager.readValue() replaced with ASPPage.readValue()
 *                           in getSelectedCustomCommand().
 *    Johan S  2000-Feb-Gui  Added new buttons for GUImura, added package available copies of the
 *                           Layout Modes and functions to fetch these modes. Added the function
 *                           adjustModes(). Also added the attribute userdisabled on the CommandBarItem, to
 *                           keep track of which function that has been disabled by the user and which has been
 *                           disabled by the Java code (adjustModes())
 *    Johan S  2000-Feb-10   Added attributes and functions in ASPCommandBarItem. Supporting enabling and
 *                           disabling of commmand on a rowlevel. valid_type_condition = inclusive or exclusive;
 *                           valid_values = command is valid when the valid_depended_column assumes or not
 *                           assumes one of these values.
 *    Stefan M 2000-Mar-16 - SINGLE_DESIGNED_LAYOUT changed to CUSTOM_LAYOUT. No buttons visible by default.
 *    Stefan M 2000-Mar-27 - Added conditionsTrue(); checks if row level conditions are true for the current row.
 *    Johan S  2000-Mar-29 - Added speciel browsertext for tablebrowsing.
 *    Johan S  2000-Apr-06 - Fixed some bugs in conditionsTrue() and actionButton(). Added disableCustomCommand().
 *                           removeCustomCommand() now calls disableCustomCommand() in version 3.
 *    Stefan M 2000-Apr-22 - Fixed visual bug when backward and forward are disabled.
 *    Stefan M 2000-May-11 - Added support for remembering state of collapsed block (ASPBlockLayout). Improved
 *                           visual space handling greatly (still problematic if block has a title).
 *    Stefan M 2000-May-14 - Added "Save" commandbaritem. Shown only if it has been enabled.
 *    Stefan M 2000-May-18 - Added enableModeLabel(),disableModeLabel(),isModeLabelEnabled().
 *    Stefan M 2000-May-20 - Moved "Copy" button to Action menu. Added setWidth(), getWidth() -
 *                           modifies the bar_width variable, which was obsolete. Added
 *                           confirmation dialog for "Delete" button.
 *    Stefan M 2000-May-30 - Row level conditions are not applied to the custom commands
 *                           when in multirow, only in single mode.
 *    Stefan M 2000-Jun-02 - client_function access level changed to package. Now immutable.
 *    Johan  S 2000-Jun-02 - Added browseUpdate();
 *    Stefan M 2000-Jul-05 - Added addCustomCommandSeparator().
 *    Stefan M 2000-Sep-26 - Added getAllCustomCommands(), which returns all custom commands, changed
 *                           getCustomCommands() to only return non-separator custom commands.
 *    Stefan M 2000-Oct-02 - conditionsTrue() can now handle number fields. Exceptions now handled.
 *    Stefan M 2000-Nov-01 - Now uses ASPManager.getTranslatedImageLocation() for pictures.
 *    Piotr  Z 2000-Nov-27 - Added move to first and last record functionality.
 *    Artur  K               Added getImageName() function which generate HTML code for
 *                           onmouseover and onmouseout events.
 *    Stefan M 2000-Dec-19 - Added Document Management support in action button.
 *    Jacek P  2000-Feb-05 - Class ASPCommandBarItem moved to its own file.
 *    Kingsly P 2002-Jul-10 - Added translation for 'Delte this row?'.
 *    Sampath  2002-oct-10 - Changed conditionsTrue() to allow null values to be used with addCommandValidConditions() in ASPCommandBar 
 *    ChandanaD2002-Dec-18 - Log Id 567, Changed getImageName() and getItemData() methods. Added getActiveLayout() method.
 *    ChandanaD2002-Dec-30 - Changed getItemData() method - to correct a bug in log id  567.
 *    ChandanaD2003-Jan-02 - Bug corrected in log Id 567.
 *    ChandanaD2003-Jan-08 - Bug corrected in log Id 567.
 *    Rifki R  2003-Jan-09 - Log id 1010, added setSecurePlSqlMethod() and getSecurePlSqlMethod().
 *    ChandanaD2003-Feb-18 - Added new methods setLinkFieldList,setLinkInNewWin,isLinkInNewWin & getLinkFieldList.
 *    ChandanaD2003-Mar-27 - Changed some codes related to active commands.
 *    Ramila H 2003-Jul-29 - Log id 1051, added code to remove custom commands from MultirowAction button.
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    Ramila H 2003-12-10  - Bug id 41451 corrected. Added method unsetRemoveFromMultirowAction too.
 *    ChandanaD2004-Mar-23 - Merged with SP1.
 *    ChandanaD2004-Apr-01 - Further Improvements for Multirow Actions 
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/07/17 amiklk Bug 84844, getUrl() changed to call setNoteBookVisible() iff not mobile version
 * 2009/02/13 buhilk Bug 80265, F1PR454 - Templates IID. Modified getItemData().
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client, Modified construct(), getImageName() and getImageData().
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support rich menus with icons.
 * 2008/01/18 sadhlk Bug 69198, Added Custom Command-Bar Buttons functionality.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.5  2005/05/18 10:48:59  rahelk
 * Changed property page height and width
 *
 * Revision 1.4  2005/05/17 10:44:12  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.3  2005/02/08 08:25:08  rahelk
 * Merged call id 121740: considered both object null and string null in method conditionsTrue
 *
 * Revision 1.2  2005/02/01 10:32:58  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/10 10:10:44  riralk
 * New Block Layout profile GUI changes
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.util.*;


class ASPCommandBarItem extends ASPPageSubElement
{
   //==========================================================================
   //  Immutable attributes
   //==========================================================================

   private ASPCommandBar commandBar;

   private boolean add_left_space;
   private boolean add_right_space;
   private boolean hyperlinked;
   private String  command_id;
   private int     command_group;

   private String  client_function;  // Now immutable.
   private String  secure_plsql_method; // for secure custom commands      
   
   private String  link_field_list;
   private boolean link_newwin;

   private String  custom_group     = "";
   private String image_url;
   private boolean commandbar_button;

   private String   icon_enabled;
   private String   icon_disabled;
   private String   icon_enabled_flat;
   private String   icon_disabled_flat;

   // backward compatibility attributes
   private boolean new_item;//      = false;
   private boolean obsolete_item;// = false;
   
   private boolean remove_from_row_actions;
   private boolean force_enable_multi_action;
   private String valid_type_conditions;
   private boolean or_condition;
   private boolean theme_specific;
      
   private boolean has_icon;
   //==========================================================================
   //  Mutable attributes
   //==========================================================================

   private String  server_function;       private String  pre_server_function;
   private boolean user_server_func;      private boolean pre_user_server_func;
   private String  label;                 private String  pre_label;
   private boolean disabled;              private boolean pre_disabled;
   private boolean userdisabled;          private boolean pre_userdisabled;
   private boolean userenabled;           private boolean pre_userenabled;
   private boolean removed;               private boolean pre_removed;
   private boolean default_command;       private boolean pre_default_command;
   private String  valid_values;          private String  pre_valid_values;
   private boolean valid_type_condition;  private boolean pre_valid_type_condition;
   private String valid_depended_column;  private String  pre_valid_depended_column;
   private boolean remove_from_action_button; private boolean pre_remove_from_action_button;
   private String  property_page_url;     private String  pre_property_page_url;
   
   private String  cmd_property_page_url;     private String  pre_cmd_property_page_url;
   // Added by Terry 20130911
   // Command bar item property
   private Buffer  cmd_property;          private Buffer pre_cmd_property;
   // Added end
   
   // Added by Terry 20140926
   // Confirm msg of command bar
   private String  cmd_confirm_msg;       private String pre_cmd_confirm_msg;
   // Added end
   

   //==========================================================================
   //  Static constants
   //==========================================================================

   //private static final boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPCommandBarItem");

   private final static String JavaScriptFunction = "commandSet";

   //==========================================================================
   //  Constructors
   //==========================================================================

   ASPCommandBarItem( ASPCommandBar bar )
   {
      super(bar);
      this.commandBar = bar;
   }

   ASPCommandBarItem construct( String label,String image_url, String command_id, int command_group,
                                boolean add_left_space, boolean add_right_space, boolean hyperlinked, boolean commandbar_button)
   {
      try
      {
         this.disabled              = false;
         this.removed               = false;
         this.label                 = label;
         this.command_id            = command_id;
         this.server_function       = command_id.substring(0,1).toLowerCase();
         if(command_id.length()>1)
            this.server_function = this.server_function + command_id.substring(1);
         this.client_function       = null;
         this.secure_plsql_method   = null;
         this.link_field_list       = null;
         this.link_newwin           = false;
         this.command_group         = command_group;
         this.add_left_space        = add_left_space;
         this.add_right_space       = add_right_space;
         this.default_command       = false;
         this.hyperlinked           = hyperlinked;
         this.image_url             = image_url;
         this.commandbar_button     = commandbar_button;
         this.remove_from_action_button = false;

         if ( commandBar.FAVORITE.equals(command_id) && Str.isEmpty(commandBar.getSearchURL()) )
            this.disabled = true;         

         String base_img_loc = getASPManager().getASPConfig().getDefaultImagesLocation();
         
         ASPManager mgr = commandBar.getASPManager();

         this.has_icon               = !mgr.isEmpty(image_url);
         
         if (commandBar.iconsEnabled())
         {
            if (commandBar.FIND.equals(command_id)) //!!!
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/FIND/NORMAL",     "find.gif");
               // Commented by Terry 20130923
               // if(commandBar.getBlock().getMasterBlock()!=null)
               //     userdisabled = true;
               // else
               //     userdisabled = false;
               // Commented end
            }
            else if (commandBar.OKFIND.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL",        "ok.gif");
             }
            else if (commandBar.CANCELFIND.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELFIND/NORMAL",    "cancel.gif");
            }
            else if (commandBar.COUNTFIND.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/COUNTFIND/NORMAL",     "count.gif");
            }
            // Added by Terry 20130918
            // Advanced Query
            else if (commandBar.ADVANCEDFIND.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/ADVANCEDFIND/NORMAL",  "findAdvanced.gif");
            }
            // Added end
            else if (commandBar.DELETE.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/DELETE/NORMAL",     "delete.gif");
            }
            else if (commandBar.EDITROW.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/EDITROW/NORMAL",     "editrow.gif");
            }
            else if (commandBar.VIEWDETAILS.equals(command_id))
            {
               icon_enabled       =mgr.getConfigParameter("COMMAND_BAR/ICONS/VIEWDETAILS/NORMAL",     "viewdetails.gif");
            }
            else if (commandBar.NEWROW.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/NEWROW/NORMAL",     "new.gif");
            }
            else if (commandBar.SAVERETURN.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVERETURN/NORMAL",     "savenreturn.gif");
            }
            else if (commandBar.SAVENEW.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVENEW/NORMAL",     "savennew.gif");
            }
            else if (commandBar.CANCELNEW.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELNEW/NORMAL",     "cancelnew.gif");
            }
            else if (commandBar.CANCELEDIT.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELEDIT/NORMAL",     "canceledit.gif");
            }
            else if (commandBar.BACK.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/BACK/NORMAL",     "return.gif");
            }
            // Added by Terry 20130315
            // Edit command in overview
            else if (commandBar.OVERVIEWEDIT.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/OVERVIEWEDIT/NORMAL",     "overviewedit_16x16.gif");
            }
            else if (commandBar.OVERVIEWNEW.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/NEWROW/NORMAL",     "new.gif");
            }
            else if (commandBar.OVERVIEWDELETE.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/DELETE/NORMAL",     "delete.gif");
            }
            else if (commandBar.OVERVIEWSAVE.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVERETURN/NORMAL",     "savenreturn.gif");
            }
            else if (commandBar.OVERVIEWCANCEL.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELEDIT/NORMAL",     "canceledit.gif");
            }
            // Added end
            // Added by Terry 20120816
            // Approve row bar items
            else if (commandBar.APPROVESTART.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVESTART/NORMAL",     "approvestart.gif");
            }
            else if (commandBar.APPROVEROW.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVEROW/NORMAL",     "approverow.gif");
            }
            else if (commandBar.APPROVERETURN.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVERETURN/NORMAL",     "approvereturn.gif");
            }
            else if (commandBar.APPROVECANCEL.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVECANCEL/NORMAL",     "approvecancel.gif");
            }
            else if (commandBar.APPROVEVIEW.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVEVIEW/NORMAL",     "approveview.gif");
            }
            else if (commandBar.APPROVESENDVIEW.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVESENDVIEW/NORMAL",     "approvesendview.gif");
            }
            else if (commandBar.APPROVESENDVIEWFIN.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/APPROVESENDVIEWFIN/NORMAL",     "approvesendviewfin.gif");
            }
            // Added end
            // Added by Terry 20131001
            // Command bar in DocEdmControlVue page
            else if (commandBar.FILESAVE.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/FILESAVE/NORMAL",        "saveDoc_16x16.gif");
            }
            else if (commandBar.FILESAVEALL.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/FILESAVEALL/NORMAL",     "saveAllDoc_16x16.gif");
            }
            else if (commandBar.FILEPRINT.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/FILEPRINT/NORMAL",       "printDoc_16x16.gif");
            }
            else if (commandBar.FILEPRINTALL.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/FILEPRINTALL/NORMAL",    "printAllDoc_16x16.gif");
            }
            // Added end
            // Added by Terry 20120929
            // Lov bar in multi-select mode
            else if (commandBar.OKLOV.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL",     "ok.gif");
            }
            // Added end
            // Added by Terry 20130902
            // Add document reference bar
            else if (commandBar.DOCREF.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/DOCREF/NORMAL",     "docref.gif");
            }
            // Added end
            else if (commandBar.DUPLICATEROW.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/DUPLICATEROW/NORMAL",     "copy.gif");
            }
            else if (commandBar.SAVE.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVE/NORMAL","save.gif");
            }
            else if (commandBar.FIRST.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/FIRST/NORMAL",        "first.gif");
            }
            else if (commandBar.BACKWARD.equals(command_id))
            {
               icon_enabled       = mgr.getConfigParameter("COMMAND_BAR/ICONS/BACKWARD/NORMAL",     "backward.gif");
            }
            else if (commandBar.FORWARD.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/FORWARD/NORMAL",     "forward.gif");
            }
            else if (commandBar.LAST.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/LAST/NORMAL",     "last.gif");
            }
            else if (commandBar.PROPERTIES.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/PROPERTIES/NORMAL",     "properties.gif"); 
               theme_specific = true;
            }           
            else if (commandBar.NOTES.equals(command_id))
            {
               icon_enabled       =  mgr.getConfigParameter("COMMAND_BAR/ICONS/NOTES/NORMAL",     "notes.gif");
            }           
            else
            {
               icon_enabled  = null;
               icon_disabled = null;
            }
         }
      }
      catch( Throwable any )
      {
         this.commandBar.error(any);
      }
      return this;
   }
   
   // Modified by Terry 20131001
   // Change visibility
   // Original:
   // private String getIconEnabled()
   protected String getIconEnabled()
   // Modified end
   {
      if (icon_enabled==null) return null;
      ASPManager mgr = commandBar.getASPManager();
      if (theme_specific) 
         return mgr.getASPConfig().getImagesLocation() + mgr.getUserTheme() + "/" + icon_enabled;         
      else
      {
         if(command_id==commandBar.FIRST || command_id==commandBar.FORWARD || command_id==commandBar.LAST || command_id==commandBar.BACKWARD)
            return commandBar.getImageLocationWithRTL() + icon_enabled;
         else
         {
            // Modified by Terry 20130916
            // Original:
            // return commandBar.getImageLocation() + icon_enabled;
            return commandBar.getImageLocation() + mgr.getLanguageCode() + "/" + icon_enabled;
            // Modified end
         }
      }
   }
   
   private String getIconDisabled()
   {
       return getIconDisabled();        
   }
   
   private String getIconEnabledFlat()
   {
       return icon_enabled_flat;        
   }
   
   private String getIconDisabledFlat()
   {
       return icon_disabled_flat;        
   }
   
   //==========================================================================
   //  ASPPoolElement logic
   //==========================================================================

   /**
    * Copy the default value of every mutable attribute (pre-variable) to
    * its current value.
    */
   protected void doReset()
   {
      server_function  = pre_server_function;
      user_server_func = pre_user_server_func;
      
      label            = pre_label;
      disabled         = pre_disabled;
      userdisabled     = pre_userdisabled;
      userenabled      = pre_userenabled;
      removed          = pre_removed;
      default_command  = pre_default_command;
      valid_values     = pre_valid_values;
      valid_type_condition = pre_valid_type_condition;
      valid_depended_column = pre_valid_depended_column;
      remove_from_action_button = pre_remove_from_action_button;           
      property_page_url = pre_property_page_url;
      cmd_property_page_url = pre_cmd_property_page_url;
      // Added by Terry 20130911
      // Command bar item property
      cmd_property     = pre_cmd_property;
      // Added end
      
      // Added by Terry 20140926
      // Confirm msg of command bar
      cmd_confirm_msg = pre_cmd_confirm_msg;
      // Added end
   }

   /**
    * Copy the current value of every mutable attribute to
    * its default value (pre-variable).
    */
   protected void doFreeze()
   {
      pre_server_function  = server_function;
      pre_user_server_func = user_server_func;

      pre_label            = label;
      pre_disabled         = disabled;
      pre_userdisabled     = userdisabled;
      pre_userenabled      = userenabled;
      pre_removed          = removed;
      pre_default_command  = default_command;
      pre_valid_values         = valid_values;
      pre_valid_type_condition = valid_type_condition;
      pre_valid_depended_column = valid_depended_column;
      pre_remove_from_action_button = remove_from_action_button;
      pre_property_page_url = property_page_url;
      pre_cmd_property_page_url = cmd_property_page_url;
      // Added by Terry 20130911
      // Command bar item property
      pre_cmd_property     = cmd_property;
      // Added end
      
      // Added by Terry 20140926
      // Confirm msg of command bar
      pre_cmd_confirm_msg  = cmd_confirm_msg;
      // Added end
   }

   /**
    * Clone this bar item into a new item included in the specified command bar.
    * The state of the new item is DEFINED.
    */
   protected ASPPoolElement clone( Object bar ) throws FndException
   {
      ASPCommandBarItem i = new ASPCommandBarItem((ASPCommandBar)bar);

      i.add_left_space  = add_left_space;
      i.add_right_space = add_right_space;
      i.hyperlinked     = hyperlinked;
      i.image_url       = image_url;
      i.commandbar_button = commandbar_button;
      i.command_id      = command_id;
      i.command_group   = command_group;

      i.custom_group    = custom_group;

      i.icon_enabled    = icon_enabled;
      i.icon_disabled   = icon_disabled;
      i.theme_specific  = theme_specific;
      
      i.force_enable_multi_action = force_enable_multi_action;
      i.remove_from_row_actions = remove_from_row_actions;
      i.valid_type_conditions = valid_type_conditions;
      i.or_condition = or_condition;
      
      i.client_function = client_function;
      i.secure_plsql_method = secure_plsql_method;
      i.link_field_list = link_field_list;
      i.link_newwin     = link_newwin;
      i.remove_from_action_button = i.pre_remove_from_action_button = pre_remove_from_action_button;
      i.has_icon         = has_icon;

      i.server_function  = i.pre_server_function  = pre_server_function;
      i.user_server_func = i.pre_user_server_func = pre_user_server_func;
      i.label            = i.pre_label            = pre_label;
      i.disabled         = i.pre_disabled         = pre_disabled;
      i.userdisabled         = i.pre_userdisabled         = pre_userdisabled;
      i.userenabled         = i.pre_userenabled         = pre_userenabled;
      i.removed          = i.pre_removed          = pre_removed;
      i.default_command  = i.pre_default_command  = pre_default_command;
      i.valid_values     = i.pre_valid_values     = pre_valid_values;
      i.valid_depended_column = i.pre_valid_depended_column = pre_valid_depended_column;
      i.valid_type_condition = i.pre_valid_type_condition = pre_valid_type_condition;
      i.property_page_url  = i.pre_property_page_url  = pre_property_page_url;
      i.cmd_property_page_url  = i.pre_cmd_property_page_url  = pre_cmd_property_page_url;
      // Added by Terry 20130911
      // Command bar item property
      i.cmd_property     = i.pre_cmd_property     = pre_cmd_property;
      // Added end
      
      // Added by Terry 20140926
      // Confirm msg of command bar
      i.cmd_confirm_msg  = i.pre_cmd_confirm_msg  = pre_cmd_confirm_msg;
      // Added end
      
      i.setCloned();
      return i;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
      commandBar.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   public String  getName()
   {
      return label;
   }

   //==========================================================================
   //
   //==========================================================================


   boolean isUserDisabled()
    {
        return userdisabled;
    }
   boolean isUserEnabled()
    {
        return userenabled;
    }

   void userEnable()
   {
      try
      {
         modifyingMutableAttribute("USERDISABLED");
         modifyingMutableAttribute("USERENABLED");
         userdisabled = false;
         userenabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void userDisable()
   {
      try
      {
         modifyingMutableAttribute("USERDISABLED");
         modifyingMutableAttribute("USERENABLED");
         userdisabled = true;
         userenabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void enable()
   {
      try
      {
         modifyingMutableAttribute("DISABLED");
         disabled = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void disable()
   {
      try
      {
         modifyingMutableAttribute("DISABLED");
         disabled = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   boolean checkDisabled()
   {
      return disabled;
   }

   void remove()
   {
      try
      {
         modifyingMutableAttribute("REMOVED");
         removed = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void unRemove()
   {
      try
      {
         modifyingMutableAttribute("REMOVED");
         removed = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   boolean checkRemoved()
   {
      return removed;
   }

   void setLabel(String label)
   {
      try
      {
         modifyingMutableAttribute("LABEL");
         this.label = label;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String getLabel()
   {
      return label;
   }

   String getCommandId()
   {
      return command_id;
   }
   
   String getImageUrl()
   {
      return image_url;
   }

   int getCommandGroup()
   {
      return command_group;
   }

   String getCustomGroup()
   {
      return this.custom_group;
   }

   void setCustomGroup(String group)
   {
      try
      {
         modifyingImmutableAttribute("CUSTOM_GROUP");
         this.custom_group = group;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void setServerFunction(String server_function)
   {
      try
      {
         modifyingMutableAttribute("SERVER_FUNCTION");
         this.server_function = server_function;
         this.user_server_func = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String getServerFunction()
   {
      return server_function;
   }

   boolean isUserDefinedServerFunction()
   {
      return user_server_func;
   }

   void setDefault(boolean isDefault)
   {
      try
      {
         if(isCustomCommand())
         {
            modifyingMutableAttribute("DEFAULT_COMMAND");
            default_command = isDefault;
         }
         else
            throw new FndException("FNDCBRNCC: Only custom commands might be set default!");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void setClientFunction(String client_function)
   {
      try
      {
         modifyingImmutableAttribute("CLIENT_FUNCTION");
         this.client_function = client_function;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String getClientFunction()
   {
      return client_function;
   }
   
   void setIconImage(String iconImage)
   {
      try
      {
         modifyingImmutableAttribute("ICON_IMAGE");
         this.image_url = iconImage;
         this.has_icon   = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String getIconImage()
   {
      return getImageUrl();
   }
   
   boolean hasIcon()
   {
      return this.has_icon;
   }
      
   //used by addSecureCustomCommand()
   void setSecurePlSqlMethod(String plsql_method)
   {
      try
      {
        modifyingImmutableAttribute("SECURE_PLSQL_METHOD");
        this.secure_plsql_method = plsql_method;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }  

   void setLinkFieldList(String field_list)
   {
      try
      {
        modifyingImmutableAttribute("LINK_FIELD_LIST");
        this.link_field_list = field_list;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   void setLinkInNewWin(boolean newwin)
   {
      try{
         modifyingImmutableAttribute("LINK_NEWWIN");
         link_newwin = newwin;    
      }
      catch( Throwable any )
      {
         error(any);
      }
   }  
   
   boolean isLinkInNewWin()
   {
      return link_newwin;   
   }    
   
   String getLinkFieldList()
   {
      return link_field_list;   
   }
   
   String getSecurePlSqlMethod()
   {
     return secure_plsql_method;
   }

   boolean isCustomCommand()
   {
      if( ( getCommandGroup()==commandBar.CMD_GROUP_CUSTOM) &&
          (!getCommandId().equals(commandBar.FIELDPERFORM)) &&
          (!getCommandId().equals(commandBar.PERFORM)) )
         return true;
      else
         return false;
   }
   
   boolean isCustomCommandWithImage()
   {
      if( ( getCommandGroup()==commandBar.CMD_GROUP_CUSTOM) && isCustomCommandBarButton() &&
          (!getCommandId().equals(commandBar.FIELDPERFORM)) &&
          (!getCommandId().equals(commandBar.PERFORM)) )
         return true;
      else
         return false;
   }
   
   boolean isCustomCommandBarButton()
   {
      return commandbar_button;
   }

   boolean isDefaultCustomCommand()
   {
      return default_command;
   }

   void setPropertyPageURL(String url)
   {
      try
      {
         modifyingMutableAttribute("PROPERTY_PAGE_URL");
         this.property_page_url = url;         
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String getPropertyPageURL()
   {
      return property_page_url;
   } 
   
   void setCmdPropertyPageURL(String url)
   {
      try
      {
         modifyingMutableAttribute("CMD_PROPERTY_PAGE_URL");
         this.cmd_property_page_url = url;         
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   String getCmdPropertyPageURL()
   {
      return cmd_property_page_url;
   } 

   boolean conditionsTrue()
   {
     ASPRowSet rowset = commandBar.getASPRowSet();
     return conditionsTrue(rowset.getCurrentRowNo());
   }

   boolean conditionsTrue(int row)
   {
      boolean conditionMatch = false; 
      boolean commandValid = true; 
             
      try
      {
          if(hasValidConditions())
          {
             if(commandBar.getASPRowSet().countRows()<1)
                 return false;
             
             if(isORCondition())
                commandValid = false;
             
             StringTokenizer colms = new StringTokenizer(fetchValidDependedColumn(),""+(char)30);
             StringTokenizer vals = new StringTokenizer(fetchValidValues(),""+(char)30);
             StringTokenizer conds = new StringTokenizer(fetchValidtypeConditions(),""+(char)30);
                          
             while(colms.hasMoreTokens() && vals.hasMoreTokens() &&  conds.hasMoreTokens())
             {
                String value = commandBar.getASPRowSet().getValueAt(row,colms.nextToken());
                if(value == null || "".equals(value)) //call id 121740.
                   value = " "; 
                
                String values = vals.nextToken();
                String cond = conds.nextToken();
                conditionMatch = false;

                StringTokenizer lst = new StringTokenizer(values,";");
                while(lst.hasMoreTokens())
                {
                   if(value.equals(lst.nextToken()))
                      conditionMatch = true;
                }
                 
                if(!isORCondition()) //AND
                {
                   if("Disable".equals(cond) && conditionMatch || "Enable".equals(cond) && !conditionMatch)
                      commandValid = false;                  
                }
                else                 // OR
                {
                   if("Enable".equals(cond) && conditionMatch)
                      commandValid = true;
                }
             }  

             return commandValid;
          }
          else
          {
             return true;
          }
      } catch (Exception e){ error(e); return false;}
   }

   boolean hasValidConditions()
    {
        if (valid_values==null) return false;
        else return true;
    }

   boolean fetchValidTypeCondition()
    {
        return valid_type_condition;
    }

   void setValidTypeCondition(boolean in)
    {
        valid_type_condition = in;
    }

   String fetchValidValues()
    {
        return valid_values;
    }

   void setValidValues(String in)
    {
      try
      {
        modifyingMutableAttribute("VALID_VALUES");

        if(valid_values==null)
           valid_values = ""+(char)30;
        valid_values += in + (""+(char)30);
      }
      catch( Throwable any )
      {
         error(any);
      }
    }

   String fetchValidDependedColumn()
    {
        return valid_depended_column;
    }

   void setValidDependedColumn(String in)
    {
        if(valid_depended_column==null)
           valid_depended_column = ""+(char)30; 
        valid_depended_column += in +(""+(char)30);
    }

   String getUrl()
   {
      String url, client_function;
      ASPManager mgr = commandBar.getASPManager();

      if(this.hyperlinked)
      {
         if (commandBar.FAVORITE.equals(command_id))
            url = "javascript:loadFavoriteUrl('"+commandBar.getSearchURL()+"')";
         else
         {
            client_function = getClientFunction();
            if(client_function==null)
               client_function = "";

            url = "javascript:";
            
            
            // Confirmation dialog for Delete.
            if(command_id.equals(commandBar.DELETE))
               url += "if(confirm('" + commandBar.getASPManager().translateJavaText("FNDCMDBARITEMCONFMDEL: Delete this Row?") + "'))";
            
            if((command_id==commandBar.FIRST || command_id==commandBar.FORWARD 
                    || command_id==commandBar.LAST || command_id==commandBar.BACKWARD 
                    || command_id==commandBar.BACK || command_id==commandBar.EDITROW 
                    || command_id==commandBar.NEWROW || command_id==commandBar.FIND
                    // Added by Terry 20130315
                    // Edit command in overview
                    || command_id==commandBar.OVERVIEWEDIT
                    // Added end
                    // Added by Terry 20120816
                    // Approve row bar items
                    || command_id==commandBar.APPROVESTART || command_id==commandBar.APPROVEROW
                    || command_id==commandBar.APPROVERETURN || command_id==commandBar.APPROVECANCEL
                    || command_id==commandBar.APPROVEVIEW || command_id==commandBar.APPROVESENDVIEW
                    || command_id==commandBar.APPROVESENDVIEWFIN
                    // Added end
                    // Added by Terry 20130902
                    // Add document reference bar
                    || command_id==commandBar.DOCREF
                    // Added end
                    ) 
                    && commandBar.getBlock().getMasterBlock() == null 
                    && !commandBar.getASPManager().isEmpty(commandBar.getBlock().getLUName()) 
                    && !mgr.isMobileVersion() )
               url += "setNoteBookVisible();";
            
            // Added by Terry 20120820
            // Confirmation dialog for Approve Return.
            // Confirmation dialog for Approve Cancel.
            if (command_id.equals(commandBar.APPROVERETURN))
               url += "if(confirm('" + commandBar.getASPManager().translateJavaText("FNDCMDBARITEMCONFMAPPROVERETURN: Confirm return this approval?") + "'))";
            else if (command_id.equals(commandBar.APPROVECANCEL))
               url += "if(confirm('" + commandBar.getASPManager().translateJavaText("FNDCMDBARITEMCONFMAPPROVECANCEL: Confirm cancel this approval?") + "'))";
            // Added end
            
            // Added by Terry 20140926
            // Adding confirm msg of command bar item
            if (isCmdConfirmMsg())
            {
               url += "if(confirm('" + commandBar.getASPManager().translateJavaText(this.cmd_confirm_msg) + "'))";
            }
            // Added end
            
            url += JavaScriptFunction +
                  "('" + commandBar.getBlockPrefix(true) + command_id + "','" + client_function + "')";
         }
      }
      else
         url = "";

      return url;
   }

   private void getImageName(String command_id, AutoString html, String item_id)
   {
      String normimg;
      String hovimg;
      ASPConfig cfg = getASPPage().getASPConfig();
      String imgloc = commandBar.getImageLocation();

      ASPBlockLayout lay = getASPPage().getASPBlock(getASPPage().getDefaultBlock()).getASPBlockLayout();      
      boolean active_cmd = command_id.equals(lay.getDefaultCommand(lay.getLayoutMode()));

      if(command_id.equals("Find"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FIND/NORMAL","find.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FIND/HOVER","find_hov.gif");
      }
      else if(command_id.equals("OkFind"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/HOVER","ok_hov.gif");
      }
      else if(command_id.equals("Cancelfind"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/CANCELFIND/NORMAL","cancel.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/CANCELFIND/HOVER","cancel_hov.gif");
      }
      else if(command_id.equals("CountFind"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/COUNTFIND/NORMAL","count.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/COUNTFIND/HOVER","count_hov.gif");
      }
      // Added by Terry 20130918
      // Advanced Query
      else if(command_id.equals("AdvancedFind"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/ADVANCEDFIND/NORMAL","findAdvanced.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/ADVANCEDFIND/HOVER","findAdvanced_hov.gif");
      }
      // Added end
      else if(command_id.equals("Delete"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/DELETE/NORMAL","delete.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/DELETE/HOVER","delete_hov.gif");
      }
      else if(command_id.equals("EditRow"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/EDITROW/NORMAL","edit.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/EDITROW/HOVER","edit_hov.gif");
      }
      else if(command_id.equals("ViewDetails"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/VIEWDETAILS/NORMAL","viewdetails.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/VIEWDETAILS/HOVER","viewdetails_hov.gif");
      }
      else if(command_id.equals("NewRow"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/NEWROW/NORMAL","new.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/NEWROW/HOVER","new_hov.gif");
      }
      else if(command_id.equals("SaveReturn"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/SAVERETURN/NORMAL","savenreturn.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/SAVERETURN/HOVER","savenreturn_hov.gif");
      }
      else if(command_id.equals("SaveNew"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/SAVENEW/NORMAL","savennew.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/SAVENEW/HOVER","savennew_hov.gif");
      }
      else if(command_id.equals("CancelNew"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/CANCELNEW/NORMAL","cancel.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/CANCELNEW/HOVER","cancel_hov.gif");
      }
      else if(command_id.equals("CancelEdit"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/CANCELEDIT/NORMAL","cancel.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/CANCELEDIT/HOVER","cancel_hov.gif");
      }
      else if(command_id.equals("Back"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/BACK/NORMAL","back.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/BACK/HOVER","back_hov.gif");
      }
      // Added by Terry 20130315
      // Edit command in overview
      else if(command_id.equals("OverviewEdit"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/OVERVIEWEDIT/NORMAL","overviewedit.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/OVERVIEWEDIT/HOVER","overviewedit_hov.gif");
      }
      
      else if(command_id.equals("OverviewNew"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/NEWROW/NORMAL","new.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/NEWROW/HOVER","new_hov.gif");
      }
      else if(command_id.equals("OverviewDelete"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/DELETE/NORMAL","delete.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/DELETE/HOVER","delete_hov.gif");
      }
      else if(command_id.equals("OverviewSave"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/SAVERETURN/NORMAL","savenreturn.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/SAVERETURN/HOVER","savenreturn_hov.gif");
      }
      else if(command_id.equals("OverviewCancel"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/CANCELEDIT/NORMAL","cancel.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/CANCELEDIT/HOVER","cancel_hov.gif");
      }
      // Added end
      // Added by Terry 20120816
      // Approve row bar items
      else if(command_id.equals("ApproveStart"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVESTART/NORMAL", "approvestart.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVESTART/HOVER", "approvestart_hov.gif");
      }
      else if(command_id.equals("ApproveRow"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVEROW/NORMAL", "approve.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVEROW/HOVER", "approve_hov.gif");
      }
      else if(command_id.equals("ApproveReturn"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVERETURN/NORMAL", "approvereturn.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVERETURN/HOVER", "approvereturn_hov.gif");
      }
      else if(command_id.equals("ApproveCancel"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVECANCEL/NORMAL", "approvecancel.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVECANCEL/HOVER", "approvecancel_hov.gif");
      }
      else if(command_id.equals("ApproveView"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVEVIEW/NORMAL", "approveview.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVEVIEW/HOVER", "approveview_hov.gif");
      }
      else if(command_id.equals("ApproveSendView"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVESENDVIEW/NORMAL", "approvesendview.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVESENDVIEW/HOVER", "approvesendview_hov.gif");
      }
      else if(command_id.equals("ApproveSendViewFin"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/APPROVESENDVIEWFIN/NORMAL", "approvesendviewfin.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/APPROVESENDVIEWFIN/HOVER", "approvesendviewfin_hov.gif");
      }
      // Added end
      // Added by Terry 20131001
      // Command bar in DocEdmControlVue page
      else if(command_id.equals("FileSave"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FILESAVE/NORMAL", "saveDoc_16x16.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FILESAVE/HOVER",  "saveDoc_hov_16x16.gif");
      }
      else if(command_id.equals("FileSaveAll"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FILESAVEALL/NORMAL", "saveAllDoc_16x16.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FILESAVEALL/HOVER",  "saveAllDoc_hov_16x16.gif");
      }
      else if(command_id.equals("FilePrint"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FILEPRINT/NORMAL", "printDoc_16x16.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FILEPRINT/HOVER",  "printDoc_hov_16x16.gif");
      }
      else if(command_id.equals("FilePrintAll"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FILEPRINTALL/NORMAL", "printAllDoc_16x16.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FILEPRINTALL/HOVER",  "printAllDoc_hov_16x16.gif");
      }
      // Added end
      // Added by Terry 20120929
      // Lov bar in multi-select mode
      else if(command_id.equals("OkLov"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL", "ok.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/OKFIND/HOVER", "ok_hov.gif");
      }
      // Added end
      // Added by Terry 20130902
      // Add document reference bar
      else if(command_id.equals("DocRef"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/DOCREF/NORMAL", "docref.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/DOCREF/HOVER",  "docref_hov.gif");
      }
      // Added end
      else if(command_id.equals("DuplicateRow"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/DUPLICATEROW/NORMAL","copy.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/DUPLICATEROW/HOVER","copy_hov.gif");
      }
      else if(command_id.equals("First"))
      {
         //imgloc  = commandBar.getBaseImageLocation();
         imgloc  = cfg.getImagesLocation();
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FIRST/NORMAL","first.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FIRST/HOVER","first_hov.gif");
      }
      else if(command_id.equals("Backward"))
      {
         //imgloc  = commandBar.getBaseImageLocation();
         imgloc  = cfg.getImagesLocation(); 
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/BACKWARD/NORMAL","backward.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/BACKWARD/HOVER","backward_hov.gif");
      }
      else if(command_id.equals("Forward"))
      {
         //imgloc  = commandBar.getBaseImageLocation();
         imgloc  = cfg.getImagesLocation(); 
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/FORWARD/NORMAL","forward.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/FORWARD/HOVER","forward_hov.gif");
      }
      else if(command_id.equals("Last"))
      {
         //imgloc  = commandBar.getBaseImageLocation();
         imgloc  = cfg.getImagesLocation(); 
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/LAST/NORMAL","last.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/LAST/HOVER","last_hov.gif");
      }
/*      else if(command_id.equals("Minimize"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/MINIMIZE/NORMAL","kn_min.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/MINIMIZE/HOVER","kn_min_hov.gif");
      }
      else if(command_id.equals("Maximize"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/MAXIMIZE/NORMAL","kn_max.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/MAXIMIZE/HOVER","kn_max_hov.gif");
      }*/
      else if(command_id.equals("Save"))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/SAVE/NORMAL","save.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/SAVE/HOVER","save_hov.gif");
      }
      else if(command_id.equals(ASPCommandBar.PROPERTIES)) 
      {
         imgloc  = cfg.getImagesLocation();  //no need for translation
         normimg = getASPManager().getUserTheme()+"/" + cfg.getParameter("COMMAND_BAR/ICONS/PROPERTIES/NORMAL","properties.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/PROPERTIES/HOVER","properties_hov.gif");
      }
      else if(command_id.equals(ASPCommandBar.NOTES))
      {
         normimg = cfg.getParameter("COMMAND_BAR/ICONS/NOTES/NORMAL","notes.gif");
         hovimg  = cfg.getParameter("COMMAND_BAR/ICONS/NOTES/HOVER","notes_hov.gif");
      }
      else
         return;
     
      if(command_id.equals(lay.getDefaultCommand(lay.getLayoutMode())))
      {
          if (commandBar.getHoverEnabled())
          {
             // Modified by Terry 20130916
             // Original:
             // html.append(" onmouseover=\"javascript:",item_id,".src='",imgloc,hovimg);
             html.append(" onmouseover=\"javascript:",item_id,".src='",imgloc + getASPManager().getLanguageCode() + "/",hovimg);
             // Modified end
             html.append("';\"");
             // Modified by Terry 20130916
             // Original:
             // html.append(" onmouseout=\"javascript:",item_id,".src='",imgloc,normimg);
             html.append(" onmouseout=\"javascript:",item_id,".src='",imgloc + getASPManager().getLanguageCode() + "/",normimg);
             // Modified end
             html.append("';outActiveCommand()\"");
          }
          // Modified by Terry 20130916
          // Original:
          // getASPPage().setActiveCommandHoverImage(imgloc + hovimg);
          // getASPPage().setActiveCommandNormalImage(imgloc + normimg);
          getASPPage().setActiveCommandHoverImage(imgloc + getASPManager().getLanguageCode() + "/" + hovimg);
          getASPPage().setActiveCommandNormalImage(imgloc + getASPManager().getLanguageCode() + "/" + normimg);
          // Modified end
      }
      else
      {
          if (commandBar.getHoverEnabled())
          {
             // Modified by Terry 20130916
             // Original:
             // html.append(" onmouseover=\"javascript:",item_id,".src='",imgloc,hovimg);
             html.append(" onmouseover=\"javascript:",item_id,".src='",imgloc + getASPManager().getLanguageCode() + "/",hovimg);
             // Modified end
             html.append("';deselectActiveCommand();\"");
             // Modified by Terry 20130916
             // Original:
             // html.append(" onmouseout=\"javascript:",item_id,".src='",imgloc,normimg);
             html.append(" onmouseout=\"javascript:",item_id,".src='",imgloc + getASPManager().getLanguageCode() + "/",normimg);
             // Modifed end
             html.append("';selectActiveCommand()\"");
          }
      }    
   }

   void getItemData( AutoString html )
   {
      String url = getUrl();
     
      String this_item_id = "" + commandBar.getBlockPrefix(false) + "_"  + command_id;

      // Modified by Terry 20130315
      // Original:
      // if(command_id == commandBar.NEWROW || command_id == commandBar.DUPLICATE)
      if(command_id == commandBar.NEWROW || command_id == commandBar.DUPLICATE || command_id == commandBar.OVERVIEWNEW)
      // Modified end
         if(!commandBar.getBlock().isCommandDefined(ASPRowSet.NEW))
            return;
      // Modified by Terry 20130315
      // Original:
      // if(command_id == commandBar.EDITROW)
      if(command_id == commandBar.EDITROW || command_id == commandBar.OVERVIEWEDIT)
      // Modified end
         if(!commandBar.getBlock().isCommandDefined(ASPRowSet.MODIFY))
            return;
      // Modified by Terry 20130315
      // Original:
      // if(command_id == commandBar.DELETE)
      if(command_id == commandBar.DELETE || command_id == commandBar.OVERVIEWDELETE)
      // Modified end
         if(!commandBar.getBlock().isCommandDefined(ASPRowSet.REMOVE))
            return;


      boolean hover_bool = commandBar.getHoverEnabled();

      // Add some "space" to the left ...
      if(this.add_left_space)
         html.append("&nbsp;");

      // start the hyperlink ...
      if((!checkDisabled()) && (url.length()>0 && !command_id.equals(commandBar.NOTES) || command_id.equals(commandBar.PROPERTIES)))
      {
         html.append("<a href=\"");
         if (command_id.equals(commandBar.PROPERTIES))
            html.append("javascript:showNewBrowser_('"+property_page_url+"',750,550)");
         // Added by Terry 20130903
         // Add document reference bar
         else if (command_id.equals(commandBar.DOCREF))
         {
            ASPRowSet rowset = commandBar.getASPRowSet();
            ASPConfig  cfg = getASPManager().getASPConfig();
            
            AutoString returnstr = new AutoString();
            try
            {
               String obj_id = rowset.getSafeValueAt(rowset.getCurrentRowNo(), "OBJID");
               returnstr.append("showNewBrowser('", cfg.getApplicationPath());
               returnstr.append("/", cfg.getParameter("DOCMAW/DOC_REF_URL"));
               returnstr.append("?view=",  getASPManager().URLEncode(commandBar.getBlock().getDocManView()));
               returnstr.append("&objid=", getASPManager().URLEncode(obj_id));
               returnstr.append("');\n");
               html.append("javascript:", returnstr.toString());
            }
            catch (FndException e)
            {
               html.append(url);
            }
         }
         // Added end
         // Added by Terry 20130911
         // Check command bar item property
         else if (isCmdProperty())
         {
            ASPRowSet rowset = commandBar.getASPRowSet();
            
            if (rowset.countRows() == 0)
            {
               disable();
               html.append(url);
               // return;
            }
            else
            {
               String command_bar_string = getCmdPropertyString(rowset);
               if (!Str.isEmpty(command_bar_string))
               {
                  AutoString returnstr = new AutoString();
                  returnstr.append("showNewBrowser('", command_bar_string);
                  returnstr.append("');\n");
                  html.append("javascript:", returnstr.toString());
               }
               else
                  html.append(url);
            }
         }
         // Added end
         // Added by Terry 20131001
         // Command bar in DocEdmControlVue page Command Items scripts
         else if (command_id.equals(commandBar.FILESAVE))
         {
            html.append("javascript:saveFile();");
         }
         else if (command_id.equals(commandBar.FILESAVEALL))
         {
            html.append("javascript:saveAllFile();");
         }
         else if (command_id.equals(commandBar.FILEPRINT))
         {
            html.append("javascript:printFile();");
         }
         else if (command_id.equals(commandBar.FILEPRINTALL))
         {
            html.append("javascript:printAllFiles();");
         }
         // Added end
         else    
            html.append(url);
         html.append("\"");
         getImageName(command_id, html, this_item_id);

         html.append(">");
      }

      //Note hyperlink---------------------------------------------------------------------
      
      if(!checkDisabled() && command_id.equals(commandBar.NOTES) )
      {
         html.append("<a href=\"");
         html.append("javascript:initNoteBook()\"");
         getImageName(command_id, html, this_item_id);
         html.append(">");
//         if(!getASPPage().isPopupExist("NOTES_POPUP"))
//         {
//            ASPPopup notes_popup = getASPPage().newASPPopup("NOTES_POPUP");
//            ASPConfig cfg = getASPPage().getASPConfig();
//            String image_loc = cfg.getImagesLocation();
//
//            notes_popup.addItem(image_loc+"notes_icon.gif", "Notes","initNoteBook()");
//            notes_popup.addItem(image_loc+"archivenote_icon.gif", "Archive Notes","initArchiveNoteBook()");
//
//            html.append("<a href=\""+notes_popup.generateCall()+"\">");
//         }
//         else
//            disable();
      }
      
      //------------------------------------------------------------------------------------
      
      
      ASPBlockLayout active_lay = getASPPage().getASPBlock(getASPPage().getDefaultBlock()).getASPBlockLayout();     
      if(active_lay != null && command_id.equals(active_lay.getDefaultCommand(active_lay.getLayoutMode())) && checkDisabled())
         getASPPage().setDefaultCommandDisabled(true);
      if(active_lay != null && command_id.equals(active_lay.getDefaultCommand(active_lay.getLayoutMode())) && !checkDisabled())
         getASPPage().setClientFunction(client_function);  
         
      // add the label or icon
      if (this.commandBar.iconsEnabled() && !Str.isEmpty(getIconEnabled())
         && (!checkDisabled()) && (url.length()>0))
      {                                                           // && !Str.isEmpty(icon_disabled)
         html.append("<img ");
         html.append("valign=middle align=center border=\"0\"");
         html.append(" name=\""+this_item_id+"\"");
         html.append(" id=\""+this_item_id+"\"");
         html.append(" src=\"");
         html.append(getIconEnabled());
         html.append("\" alt=\"");
         html.append(label,"\"");
         html.append(" title=\"");
         html.append(label,"\">");
      }
      
      if(this.image_url != null && isCustomCommandBarButton())
      { 
         html.append("<img ");
         html.append("valign=middle align=center border=0");
         html.append("\" name=\""+this_item_id);
         html.append("\" src=\"");
         html.append(this.image_url);
         html.append("\" alt=\"");
         html.append(label,"\"");
         html.append(" title=\"");
         html.append(label,"\">");
      }

      // finish the hyperlink ...
      if((!checkDisabled()) && (url.length()>0))
      {
         html.append("</a>");

         if(command_id.equals(commandBar.NEWROW))
         {
            if(command_id==commandBar.NEWROW)
            {
               ASPBlock blk = commandBar.getBlock();
               ASPBuffer buf = blk.getTemplateProfile();
               String menu_name = blk.getName()+"_NewMenu";
               int count = buf.countItems();
               if(count>=1)
               {
                  buf.sort("TITLE", true);
                  ASPPopup new_popup = getASPPage().isPopupExist(menu_name)?getASPPage().getASPPopup(menu_name):null;
                  if(new_popup==null)
                  {
                     new_popup = getASPPage().newASPPopup(menu_name);
                     new_popup.addItem(label,url);
                     new_popup.addSeparator();
                     ASPPopup new_template = getASPPage().newASPPopup(menu_name+"_Templates");
                     for(int i=0; i<count; i++){
                        String template_id   = buf.getBufferAt(i).getValue("TEMPLATE_ID");
                        String template_name = buf.getBufferAt(i).getValue("TITLE");
                        String url1 = "setTemplate('"+template_id+"');";
                        new_template.addItem(template_name,"javascript:"+url1+(url.substring(url.indexOf(":")+1)));
                     }
                     new_template.addSeparator();
                     new_template.addItem("FNDCBRITEMNEWFRMTEMPLATEADMIN: Templates", "javascript:showNewBrowser_(APP_PATH+'/common/scripts/Templates.page?TEMPLATE_PAGE_URL="+getASPPage().getURL()+"',750,500);");
                     new_popup.addSubmenu(getASPManager().translate("FNDCBRITEMNEWFRMTEMPLATE: New From Template"), new_template);
                  }
                  
                  html.append("<a href=\"");
                  html.append(new_popup.generateCall());
                  html.append("\">");
                  html.append("<img ");
                  html.append("valign=middle align=center border=\"0\"");
                  html.append("onclick=\"javascript:menuClicked(this);\" ");
                  html.append(" src=\"");
                  html.append(getASPManager().getASPConfig().getImagesLocation()+"search2_24x24.gif");
                  html.append("\" alt=\"");
                  html.append(label,"\"");
                  html.append(" title=\"");
                  html.append(label,"\">");
                  html.append("</a>");
               }
            }            
         }
         
         if ( !command_id.equals(commandBar.PROPERTIES))
             html.append("<img valign=middle align=center border=0 src='",commandBar.getImageLocation(),"6px_space.gif'>");
      }

      // add some "space" to the right ...
      //if(this.add_right_space)
       


   }


   boolean isNewItem()
   {
      return new_item;
   }

   boolean isObsoleteItem()
   {
      return obsolete_item;
   }
   
   void removeFromMultirowAction()
   {
      try
      {
         modifyingMutableAttribute("REMOVEFROMMULTIACTION");
         remove_from_action_button = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   void unsetRemoveFromMultirowAction()
   {
      try
      {
         modifyingMutableAttribute("REMOVEFROMMULTIACTION");
         remove_from_action_button = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   boolean isRemovedFromMultirowAction()
   {
      return remove_from_action_button;
   }
   
   void removeFromRowActions()
   {
      remove_from_row_actions = true;
   }
   
   boolean isRemovedFromRowActions()
   {
      return remove_from_row_actions;
   }
   
   void forceEnableMultiActionCommand()
   {
      force_enable_multi_action = true;
   }
   
   boolean isForceEnabledInMultiActionCommand()
   {
      return force_enable_multi_action;
   }
   
   void appendValidTypeCondition(String cond)
   {
      if(valid_type_conditions==null)
           valid_type_conditions = ""+(char)30;
      valid_type_conditions += cond +(char)30;
   }
   
   String fetchValidtypeConditions()
   {
      return valid_type_conditions;
   }
   
   void setORCondition(boolean or)
   {
      or_condition = or;
   }
   
   boolean isORCondition()
   {
      return or_condition;
   }
   
   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   void getMobileItemData( AutoString html )
   {
      String url = getUrl();
     
      String this_item_id = "" + commandBar.getBlockPrefix(false) + "_"  + command_id;

      if(command_id == commandBar.NEWROW || command_id == commandBar.DUPLICATE)
         if(!commandBar.getBlock().isCommandDefined(ASPRowSet.NEW))
            return;
      if(command_id == commandBar.EDITROW)
         if(!commandBar.getBlock().isCommandDefined(ASPRowSet.MODIFY))
            return;
      if(command_id == commandBar.DELETE)
         if(!commandBar.getBlock().isCommandDefined(ASPRowSet.REMOVE))
            return;

      boolean hover_bool = commandBar.getHoverEnabled();

      // Add some "space" to the left ...
      if(this.add_left_space)
         html.append("&nbsp;");

      // start the hyperlink ...
      if((!checkDisabled()) && (url.length()>0 || command_id.equals(commandBar.PROPERTIES)))
      {
         html.append("<a href=\"");
         html.append(url);
         html.append("\"");
         getImageName(command_id, html, this_item_id);
         html.append(">");
      }

      ASPBlockLayout active_lay = getASPPage().getASPBlock(getASPPage().getDefaultBlock()).getASPBlockLayout();     
      if(active_lay != null && command_id.equals(active_lay.getDefaultCommand(active_lay.getLayoutMode())) && checkDisabled())
         getASPPage().setDefaultCommandDisabled(true);
      if(active_lay != null && command_id.equals(active_lay.getDefaultCommand(active_lay.getLayoutMode())) && !checkDisabled())
         getASPPage().setClientFunction(client_function);  
         
      // add the label or icon
      if (this.commandBar.iconsEnabled() && !Str.isEmpty(getIconEnabled())
         && (!checkDisabled()) && (url.length()>0))
      {                                                           // && !Str.isEmpty(icon_disabled)

         html.append("<img ");
         html.append("valign=middle align=center border=\"0");
         html.append("\" name=\""+this_item_id);
         html.append("\" src=\"");
         html.append(getIconEnabled());
         html.append("\" alt=\"");
         html.append(label,"\"");
         html.append(" title=\"");
         html.append(label,"\">");
            
      }
      // finish the hyperlink ...
      if((!checkDisabled()) && (url.length()>0))
      {
         html.append("</a>");

         if ( !command_id.equals(commandBar.PROPERTIES))
             html.append("<img valign=middle align=center border=0 src='",commandBar.getImageLocation(),"6px_space.gif'>");
      }
   }
   
   // Added by Terry 20130911
   // Command bar item property
   ASPCommandBarItem setCmdProperty( String name, String value )
   {
      try
      {
         modifyingMutableAttribute("CMD_PROPERTY");
         if( cmd_property==null )
            cmd_property = getASPManager().getFactory().getBuffer();
         cmd_property.setItem(name,value);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   String getCmdProperty( String name )
   {
      try
      {
         return cmd_property==null ? null : cmd_property.getString(name,null);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }
   
   String getCmdPropertyString(ASPRowSet set)
   {
      AutoString tmpbuf = new AutoString();
      ASPManager mgr = getASPManager();
      String page_url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/scripts/DynamicSel.page";
      tmpbuf.append(page_url);
      if( cmd_property != null )
      {
         for( int i=0; i< cmd_property.countItems(); i++ )
         {
            Item item = cmd_property.getItem(i);
            String name = item.getName();
            String value = item.getString();
            if (commandBar.CMD_PRO_PARPA.equals(name) && !Str.isEmpty(value))
            {
               // Get parent keys and value from set and transfer to DynamicSel
               String[] __parent_params = value.split(",");
               String   parent_params = "";
               for (int j = 0; j < __parent_params.length; j++)
               {
                  __parent_params[j] = __parent_params[j].trim().toUpperCase();
                  parent_params = parent_params + mgr.URLEncode(__parent_params[j] + "@" + set.getValue(__parent_params[j]) + "~");
               }
               value = parent_params;
            }
            else if (commandBar.CMD_PRO_VIEW_PARAMS.equals(name) && !Str.isEmpty(value))
            {
               String view_params = "";
               StringTokenizer __all_params = new StringTokenizer(value, ",");
               while( __all_params.hasMoreTokens() )
               {
                  String argname, alias;
                  StringTokenizer __one_param = new StringTokenizer(__all_params.nextToken());
                  switch(__one_param.countTokens())
                  {
                     case 1:
                        argname = __one_param.nextToken();
                        alias = argname;
                        break;
                     case 2:
                        argname = __one_param.nextToken();
                        alias = __one_param.nextToken();
                        break;
                     default:
                        argname = "";
                        alias = "";
                        break;
                  }
                  if (!Str.isEmpty(argname) && !Str.isEmpty(alias))
                  {
                     String arg_value = set.getValue(argname);
                     if (!Str.isEmpty(arg_value))
                        view_params = view_params + mgr.URLEncode(alias.toUpperCase() + "@" + arg_value + "~");
                  }
               }
               value = view_params;
            }
            
            if(i == 0)
               tmpbuf.append('?');
            else
               tmpbuf.append('&');
            
            tmpbuf.append("__", mgr.URLEncode(name));
            tmpbuf.append('=');
            tmpbuf.append(mgr.URLEncode(value));
         }
      }
      return tmpbuf.toString();
   }
   
   boolean isCmdProperty()
   {
      return cmd_property!=null;
   }
   // Added end
   
   // Added by Terry 20140926
   // Confirm msg of command bar
   ASPCommandBarItem setCmdConfirmMsg( String confirm_msg )
   {
      try
      {
         modifyingMutableAttribute("CMD_CONFIRM_MSG");
         this.cmd_confirm_msg = confirm_msg;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   String getCmdConfirmMsg()
   {
      return this.cmd_confirm_msg;
   }
   
   boolean isCmdConfirmMsg()
   {
      return !Str.isEmpty(this.cmd_confirm_msg);
   }
   // Added end
}
