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
 * File        : ASPPopup.java
 * Description : Implements a popup menu in dynamic HTML, for both IE and Netscape.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Stefan M  2000-Jan-25 - Created
 *    Stefan M  2000-Mar-24 - Complete rewrite.
 *    Stefan M  2000-Apr-28 - Added getWidth(), countItems().
 *    Stefan M  2000-May-14 - System popups are translated.
 *    Stefan M  2000-May-15 - Compability with portlets added.
 *    Stefan M  2000-May-19 - Debug flag added. Separators added.
 *    Stefan M  2000-May-20 - Added "Copy" menuitem to commandbar menu.
 *    Stefan M  2000-May-30 - Menu is no longer too wide in Explorer 4.
 *    Stefan M  2000-Jul-05 - Empty custom commands added with
 *                            ASPCommandBar.addCustomCommandSeparator() are now
 *                            added as separators.
 *    Stefan M  2000-Aug-07 - Items added with addItem() are now translated.
 *    Stefan M  2000-Sep-18 - width is now immutable. Menus can now be nested
 *                            hierarchically. addSubmenu(),setSubmenu() added.
 *    Stefan M  2000-Oct-02 - Exceptions are now handled in generateCall().
 *    Artur  K  2000-Nov-23 - New private method generateMyLinks() called from
 *    Piotr  Z                addContent() in case of defining contents of
 *                            navigate menu.
 *    Stefan M  2000-Dec-19 - Added Document Management items to action button and row menu.
 *    Artur K   2001-Mar-20 - Added generatePortalsMenu() method and made some necessary
 *                            changes for handling mulitple portal pages.
 *    Jacek P   2001-Mar-29 - Changed .asp to .page
 *    Jacek P   2001-Apr-11 - Added value of the LOGOUT query string parameter
 *                            in addContent()
 *    Piotr Z   2001-Apr-25 - Changed generatePortalsMenu() method.
 *    Kingsly P 2001-Apr-27 - Add 'CASESENCETIVE' to querystring of addlink.
 *    Mangala P 2001-Jun-19 - Log #748, Check the OS when generating the "PDF" Output 
 *                            Channel popup item.
 *    Kingsly P 2001-Jun-22 - Log #769, change addContent() method.
 *    Mangala P 2002-Mar-26 - Replace escape() calls with URLClientEncode() for Save Link. 
 *    Kingsly P 2002-Jul-10 - Added translation for 'Delte this row?'.
 *    Ramila H  2002-Jul-17 - Added support for Netscape 6
 *    Chandana  2002-Jul-18 - Used correctURL(url) to append an ID to the URL.
 *    Rifki R   2002-Oct-09 - Enabled PDF output channels popup option regardless of OS in addContent().
 *    ChandanaD 2002-Oct-28 - Made compatible with NE6 and above.
 *    Ramila H  2002-Nov-05 - Made changes to LOGOUT url when BROWSER_CACHE disabled.
 *    Sampath   2002-Nov-06 - add selectall deselectall & invert selection commands to tables   
 *    ChandanaD 2002-Nov-18 - Disables the 'Home' menu item when the 'Home Icon' is disabled.
 *    ChandanaD 2002-Nov-20 - Corrected bug in menu conainer width adjustment in NE6 and above.
 *    ChandanaD 2002-Nov-21 - Uses an image as the menu separator.
 *    Suneth M  2002-Nov-29 - Changed generateMyLinks() to show links in the menu belongs to 
 *                            my links portlets in all portal views.
 *    ChandanaD 2002-Dec-31 - Handled "used_in_request" as a mutable attribute.
 *    ChandanaD 2003-Jan-08 - Added removeAllItems() method.
 *    ChandanaD 2003-Feb-18 - Changed addContent() method.
 *    ChandanaD 2003-Mar-19 - Corrected a bug in custom link commands.
 *    ChandanaD 2003-Mar-24 - Made changes to custom link commands to support disabling.
 *    ChandanaD 2003-May-26 - Modified generateDefinition() method for the new L&F of popups.
 *    ChandanaD 2003-May-27 - Removed 'Home' menu item from the 'Navigate' menu.
 *    ChandanaD 2003-May-29 - New "options" popup handled in the addContents() method.
 *    Ramila H  2003-May-30 - added config_views popup for portal configuration.
 *    Chandana D2003-Jun-05 - Changed width/height of the Table Properties window.
 *    Ramila H  2003-Jun-05 - opened NewPortalViewWizard from create portal view option.
 *    ChandanaD 2003-Jun-24 - Modified generateDefinition() to support NS6.
 *    ChandanaD 2003-Jun-30 - Added getItems, getAction and setAction methods.
 *    Ramila H  2003-Jul-18 - Log id 1119, Authentication by fndext.
 *    Ramila H  2003-Jul-26 - Log id 1080, Save to xml file.
 *    Chandana D2003-Sep-04 - Added 3 new items to the Help menu.(IFS on the web, IFS support, About)
 *    Ramila H  2003-Sep-04 - Changed call in save profile.
 *    Chandana D2003-Sep-18 - Changed generateDefinition to fix a bug in Netscape 4.x.
 *    Chandana D2003-Sep-19 - Added "How to use Help" and "Search" menu items to the Help menu. Changed "This Page" to "Current Page".
 *    Chandana D2003-Dec-10 - Bug 40925, Fixed a bug in popup menus(getting blank lines) for NE7.x
 *    Chandana D2004-Feb-03 - Bug 42495, fixed.
 *    Chandana D2004-Feb-11 - Bug 42692, fixed.
 *    Chandana D2004-Feb-12 - Bug 42727, fixed.
 *    Chandana D2004-Feb-17 - Bug 42801, fixed.
 *    Chandana D2004-May-12 - Updated for the new style sheets.
 *    Chandana D2004-May-19 - Changed generateDefinition() to support Linux Mozilla. Changed Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Ramila H  2004-05-26  - Added link to "options" to change language
 *    Chandana  2004-Jun-28 - Added new popup menu item "History" for both table and cmdbar popups.
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Chandana 2004-Sep-10 - Added new menu item "Layout Properties"
 *    Chandana 2004-Sep-14 - Reversed changes done for "Layout Properties".
 *    Ramila H 2004-11-03  - Changed style clsses to support standard portals.
 *    Chandana 2004-Nov-05 - Added new menu item "Layout Properties"
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/07/01 buhilk Bug 91498, Changed generateMyLinks() to decode CSV's
 * 2010/06/28 buhilk Bug 91645, Changed generateMyLinks()
 * 2010/04/07 sumelk Bug 89777, Changed addContent() to support row selection in portlets.
 * 2009/11/06 amiklk Bug 86918, Changed addContent() to change menu labeling for 'select all rows'
 * 2009/03/11 sumelk Bug 80503, Changed addContent() to enable log off for enternally identified authentication. 
 * 2008/10/15 dusdlk Bug 77769, modified the addContent() added aditional checks before displaying the Page Properties option.
 * 2008/10/10 dusdlk Bug 77095, Updated the window size when opening PageProperties page. 
 * 2008/09/24 dusdlk Bug 77095, Update addContent() to add a new link for PageProperties.page
 * 2008/08/05 buhilk Bug 76154, AEE & web integration changes. Modified portal manage menu creation.
 * 2008/07/09 buhilk Bug 75668, IID F1PR432 - Workflow/My Todo functionality .
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support rich menus/table cells.
 * 2008/04/17 buhilk Bug 73041, Modified generateCall(String, Vector) method to hide secured custom command groups.
 * 2008/04/04 buhilk Bug 72852, Modified addContent() to remove "sign out" menu item from the "Options" popup when viewed from RWC.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/08/10 sadhlk Changed the new window size of the About page.
 * 2007/07/03 sadhlk Merged Bug 64669, Modified addContent() to support disabled personal profile functionality. 
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2007/01/17 sadhlk Bug 61627, Issues with 'Help on this page'.
 * 2006/11/24 mapelk Bug 61536, Ability to rearrange portal views
 * 2006/11/17 rahelk Bug 61910, Called javascript function to deleted WAS cluster cookies
 * 2006/09/24 mapelk Bug 59842, Improved CSV code 
 * 2006/08/07 buhilk Bug 59442, Corrected Translatins in Javascript
 *
 * Revision 1.6  2006/04/27           sumelk 
 * Bug 56316, Changed addContent() to add the view name to the query string when removing a portal view. 
 *
 * 2006/04/26 prralk Bug 57027 issues with 'help on fields'
 *
 * Revision 1.5  2006/01/09           riralk
 * Added menu item "Help on Fields". Removed "Search" menu item.
 *
 * Revision 1.4  2005/12/22 10:21:07  rahelk
 * changes menu option name for make a copy of this page
 *
 * Revision 1.3  2005/11/10 10:21:07  rahelk
 * changes related to usages
 *
 * Revision 1.2  2005/11/08 07:50:55  rahelk
 * core changes for using USAGES in help
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.10  2005/08/08 09:44:05  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.9  2005/06/13 10:12:32  rahelk
 * CSL 2: private settings - bug fix: added sequence control
 *
 * Revision 1.8  2005/06/09 11:30:29  rahelk
 * CSL 2: private settings
 *
 * Revision 1.7  2005/06/09 09:40:36  mapelk
 * Added functionality to "Show pages in default layout mode"
 *
 * Revision 1.6  2005/05/04 03:36:38  sumelk
 * Changed addContent() to enhance the possibility of disabling the History menu item.
 *
 * Revision 1.5  2005/04/08 06:05:37  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.4  2005/02/15 08:45:10  mapelk
 * Bug fix: Project link call 121294: Static LOVs with query string disapear. And removed tabale property icon if not necessory.
 *
 * Revision 1.3  2005/02/02 08:22:18  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.2  2005/02/01 10:32:59  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/10 10:18:07  riralk
 * Removed Layout Properties and Table Properties from popup menu
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.os.OSInfo;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.awt.*;
import java.awt.font.*;
import javax.imageio.ImageIO;
import java.awt.image.*;

public class ASPPopup extends ASPPageElement
{
//  implements ASPBufferable

   //==========================================================================
   //  Persistent immutable attributes
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPopup");

   private ASPPage asppage;

   private ASPPoolElement parent;
   private String name;

   private int       width             = 120;
   private int       parent_type;
   private String    parent_name;
   private boolean   is_submenu;

   private String    submenu_arrow;

   private boolean has_param_fields = false;
   private String param_fields;

   //==========================================================================
   //  Transient immutable attributes
   //==========================================================================

   private transient Vector    items         = new Vector();

   //==========================================================================
   //  Temporary attributes
   //==========================================================================

   //private boolean used_in_request;

   private boolean used_in_request;            private boolean pre_used_in_request;
   private boolean is_icon_menu = false;

   //==========================================================================
   //  Constructors
   //==========================================================================

   ASPPopup( ASPPage page, String n )
   {
      super(page);
      asppage = page;
      name = n;
   }

   ASPPopup( ASPPage page, String n, ASPPoolElement content_provider )
   {
      super(page);
      asppage = page;
      parent = content_provider;
      name = n;
   }

   protected void verify( ASPPage page ) throws FndException
   {

   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPPage p = (ASPPage)page;
      if(DEBUG) p.debug(this+" clone("+page+")");
      ASPPopup pop = new ASPPopup(p,name);

      if(parent != null) {
         if(parent instanceof ASPTable)
            pop.parent_type = 1;
         else if(parent instanceof ASPCommandBar)
            pop.parent_type = 2;
         else if(parent instanceof ASPPage)
            pop.parent_type = 3;

         pop.parent_name   = parent.getName();
      }

      pop.name          = name;
      pop.items         = items;
      pop.width         = width;
      pop.is_submenu       = is_submenu;
      
      pop.used_in_request  = pop.pre_used_in_request  = pre_used_in_request;
      pop.is_icon_menu = is_icon_menu;

      pop.param_fields      = param_fields;
      pop.has_param_fields = has_param_fields;
      
      pop.setCloned();
      if(DEBUG) p.debug(this+" cloned into "+pop);
      return pop;
   }


   protected void doActivate() throws FndException
   {
      //used_in_request = false;
   }
   
   protected void doFreeze() throws FndException{
      pre_used_in_request  =   used_in_request;
   }

   protected void doReset() throws FndException{
      used_in_request          = pre_used_in_request;
   }

   //==========================================================================
   //  Package
   //==========================================================================

   boolean usedInRequest()
   {
      return used_in_request;
   }

   // This sets the attribute "used_in_request". ASPPage only writes definitions for
   // ASPPopup instances that have this attribute set. Submenus are also set.
   void setUsedInRequest()
   {
      used_in_request = true;
      for(int i = 0;i < items.size();i++)
      {
         menuItem item = (menuItem)items.elementAt(i);
         if( item.isSubmenu())
           item.submenu.setUsedInRequest();
      }
   }

   void addContent()
   {
      // Called by ASPPage just before freeze.
      // Custom popups must not set "parent" as instances of ASPTable, ASPCommandbar or ASPPage, or scary things will occur.
      // Note: a better solution would be nice.

      if(parent == null && parent_type > 0) {
         // Page was cloned, and we need to find the correct parent for this new instance.

         if(parent_type == 1)
            parent = asppage.getASPTable(parent_name);
         else if(parent_type == 2) {
            // Parent is a commandbar. Same name as its block, so no need to loop through all blocks.
            parent = asppage.getASPBlock(parent_name).getASPCommandBar();
         }
         else if(parent_type == 3)
            parent = asppage;
      }

      ASPManager mgr = asppage.getASPManager();
      String prefix = "__PORTLET__";
      String cmd_prefix = "__PORTLETCMD__";
      String chkbox_name = "";
//      if(!prefix.equals(""))
//         prefix += ".";
      
      try
      {

      if(parent instanceof ASPTable)
      {
         ASPTable tbl = (ASPTable)parent;
         ASPCommandBar bar = tbl.getBlock().getASPCommandBar();
         
         if(name.equals("tbl" + tbl.getTableNo() + "Tool"))
         {
            //addItem(mgr.translateJavaText("FNDPOPPROPS: Table Properties","commandSet('" + tbl.getBlock().getName() + ".OutputChannel','')"),"showNewBrowser_('" + mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS") + "ASPTableProfile.page?URL="+asppage.getPoolKey() + "&OBJNAME=" + tbl.getName() + "',600,450)");
            // Modified by Terry 20120914
            // Add selection option disable check.
            // Original: if(tbl.isRowSelectionEnabled())
            if(tbl.isRowSelectionEnabled() && !tbl.isSelectionOptionDisabled())
            {
               if (mgr.isEmpty(getASPPage().addProviderPrefix()))
                  chkbox_name = tbl.SELECTBOX + tbl.getTableNo();
               else
                  chkbox_name = cmd_prefix + tbl.SELECTBOX + tbl.getTableNo(); 
                     
               addItem(mgr.translateJavaText("FNDPOPSELALL: Select All Rows in Pane"),"selectAllRows('"+chkbox_name+"')");   
               addItem(mgr.translateJavaText("FNDPOPDESELALL: Deselect All Rows"),"deselectAllRows('"+chkbox_name+"')");   
               addItem(mgr.translateJavaText("FNDPOPINVSEL: Invert Selection"),"invertselection('"+chkbox_name+"')");   
            }
            // Modified end
            int channr=1;
            while(!mgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channr+"/NAME","__BREAK").equals("__BREAK"))
            {
               if((mgr.isExplorer())&&(channr==1))
                  addSeparator();
               else if((mgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channr+"/ENABLED","N").equals("Y"))&&(channr==1))
                  addSeparator(); 
               String response_type = mgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channr+"/CONTENT_TYPE","application/vnd.ms-excel");
               if(mgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channr+"/ENABLED","N").equals("Y"))                       
                  addItem(mgr.translateJavaText("FNDPOPCHAN: "+mgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channr+"/NAME","Output"+channr)),"commandSet('" + tbl.getBlock().getName() + ".OutputChannel-"+channr+"','')");
               channr++;
            }
         }
         else
         {
            addItem(mgr.translateJavaText("FNDPOPEDIT: Edit"),cmd_prefix + "setTableCommand"+tbl.getTableNo()+"('');commandSet('" + prefix + tbl.getBlock().getName() + ".EditRow','"+Str.nvl(bar.getClientFunction(bar.EDITROW),"")+"')");

            addItem(mgr.translateJavaText("FNDPOPDEL: Delete"),"if(confirm('" + mgr.translateJavaText("FNDPOPCONFMDEL: Delete this Row?") + "')){"+cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('');commandSet('" + prefix + tbl.getBlock().getName() + ".Delete','"+Str.nvl(bar.getClientFunction(bar.DELETE),"")+"')}");

            addItem(mgr.translateJavaText("FNDPOPDUP: Duplicate"),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('');commandSet('" + prefix + tbl.getBlock().getName() + ".DuplicateRow','"+Str.nvl(bar.getClientFunction(bar.DUPLICATEROW),"")+"')");
            
            if (!bar.getBlock().isHistoryDisabled())
               addItem(mgr.translateJavaText("FNDPOPHISTORY: History"),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('');commandSet('" + prefix + tbl.getBlock().getName() + ".RowHistory','__showHistory("+tbl.getTableNo()+")')");
            
            if(!bar.getBlock().isSendToDisabled())
            {
               Object STmenu[] = bar.getBlock().getSendToMenu();
               if(STmenu!=null)
                  addSubmenu((String)STmenu[0], (ASPPopup)STmenu[1]);
            }
            
            Vector cmds = bar.getAllCustomCommands();

            if(cmds.size() > 0)
               addSeparator();

            // First create new popup instances for every custom command group
            Vector groups = bar.getCustomCommandGroups();
            Vector labels = bar.getCustomCommandLabels();

            // Create all child popups and add the custom commands to those first
            String g_label;
            String g_id;
            for(int i=0;i<groups.size();i++)
            {
//               if(asppage.existsASPPopup((String)groups.elementAt(i)))
//                  continue;

               ASPPopup p = asppage.newASPPopup("t" + (String)groups.elementAt(i));
               p.setSubmenu();
               g_label = (String)labels.elementAt(i);
               g_id    = (String)groups.elementAt(i);
               if(cmds.size() > 0)
               {
                  for(int j = 0;j <= cmds.size()-1;j++) {
                     ASPCommandBarItem item = (ASPCommandBarItem) cmds.elementAt(j);
                     //debug(item.getLabel() + ": " + item.getCustomGroup());
                     if(item.getCustomGroup().equals(g_id))
                     {
                        if(!Str.isEmpty(item.getName()))
                        {
                           // Added by Terry 20140926
                           // Adding command bar confirm msg of ASPTable row
                           String confirm_msg = item.getCmdConfirmMsg();
                           // Added end
                           if(!item.getCommandId().startsWith("LINK_CMD"))   
                              if(item.hasIcon())
                                 // Modified by Terry 20140926
                                 // Confirm msg of command bar item
                                 // Original:
                                 // p.addItem(item.getIconImage(),item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                                 if (Str.isEmpty(confirm_msg))
                                    p.addItem(item.getIconImage(),item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                                 else
                                    p.addItem(item.getIconImage(),item.getName(),
                                          "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                          cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                                 // Modified end
                              else
                                 // Modified by Terry 20140926
                                 // Confirm msg of command bar item
                                 // Original:
                                 // p.addItem(item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                                 if (Str.isEmpty(confirm_msg))
                                    p.addItem(item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                                 else
                                    p.addItem(item.getName(),
                                          "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                          cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                                 // Modified end
                           else if(item.getCommandId().startsWith("LINK_CMD"))
                           {
                              if(item.hasIcon())
                                 // Modified by Terry 20141003
                                 // Confirm msg of command bar item
                                 // Original:
                                 // p.addItem(item.getIconImage(), item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                                 if (Str.isEmpty(confirm_msg))
                                    p.addItem(item.getIconImage(), item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                                 else
                                    p.addItem(item.getIconImage(), item.getName(),"javascript:"+
                                          "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                          cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");}");
                                 // Modified end
                              else
                                 // Modified by Terry 20141003
                                 // Confirm msg of command bar item
                                 // Original:
                                 // p.addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                                 if (Str.isEmpty(confirm_msg))
                                    p.addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                                 else
                                    p.addItem(item.getName(),"javascript:"+
                                          "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                          cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");}");
                                 // Modified end
                           }
                        }
                        else
                           p.addSeparator();
                     }
                  }
               }
            }

            if(cmds.size() > 0)
            {
               Vector subs_done = new Vector();
               int j = 0;
               for(int i = 0;i <= cmds.size()-1;i++) {
                  ASPCommandBarItem item = (ASPCommandBarItem) cmds.elementAt(i);
                  if(item.getCustomGroup().equals(""))
                  {
                     if(!Str.isEmpty(item.getName()))
                     {
                        // Added by Terry 20140926
                        // Adding command bar confirm msg of ASPTable row
                        String confirm_msg = item.getCmdConfirmMsg();
                        // Added end
                        if(!item.getCommandId().startsWith("LINK_CMD")) 
                        {
                           if(item.hasIcon())
                              // Modified by Terry 20140926
                              // Confirm msg of command bar item
                              // Original:
                              // addItem(item.getIconImage(),item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              if (Str.isEmpty(confirm_msg))
                                 addItem(item.getIconImage(),item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              else
                                 addItem(item.getIconImage(),item.getName(),
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                              // Modified end
                           else
                              // Modified by Terry 20140926
                              // Confirm msg of command bar item
                              // Original:
                              // addItem(item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              if (Str.isEmpty(confirm_msg))
                                 addItem(item.getName(),cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              else
                                 addItem(item.getName(),
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       cmd_prefix+"setTableCommand"+tbl.getTableNo()+"('" + item.getCommandId() + "');commandSet('" + prefix + tbl.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                              // Modified end
                        }
                        else if(item.getCommandId().startsWith("LINK_CMD"))
                        {
                           if(item.hasIcon())
                              // Modified by Terry 20141003
                              // Confirm msg of command bar item
                              // Original:
                              // addItem(item.getIconImage(),item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                              if (Str.isEmpty(confirm_msg))
                                 addItem(item.getIconImage(),item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                              else
                                 addItem(item.getIconImage(),item.getName(),"javascript:"+
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");}");
                              // Modified end
                           else
                              // Modified by Terry 20141003
                              // Confirm msg of command bar item
                              // Original:
                              // addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                              if (Str.isEmpty(confirm_msg))
                                 addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");");
                              else
                                 addItem(item.getName(),"javascript:"+
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       cmd_prefix+item.getCommandId()+"_"+tbl.getBlock().getName()+"(tblRow"+tbl.getTableNo()+");}");
                              // Modified end
                        }
                     }
                     else
                        addSeparator();
                  }
                  else
                  {
                     if (! subs_done.contains(item.getCustomGroup())) {
                        addSubmenu(bar.getCustomGroupLabel(item.getCustomGroup()),asppage.getASPPopup("t" + item.getCustomGroup()));
                        subs_done.addElement(item.getCustomGroup());
                     }
                  }
               }
            }
            
            Vector popflds = bar.getAllPopupField();
            if(popflds!=null & popflds.size()>0)
            {
               Iterator itr = popflds.iterator();
               addSeparator();
               while(itr.hasNext())
               {
                  ASPField f = (ASPField) itr.next();
                  addSubmenu(f.getCustomPopupLabel(), f.getCustomPopupMenu());
               }
            }
            
            Object blkPopMenus[]      = bar.getBlock().getPopupMenus();
            Object blkPopMenuLabels[] = bar.getBlock().getPopupMenuLabels();
            if(blkPopMenus!=null & blkPopMenus.length>0)
            {
               addSeparator();
               for(int p=0; p<blkPopMenus.length; p++)
               {
                  addSubmenu((String) blkPopMenuLabels[p],(ASPPopup) blkPopMenus[p]);
               }
            }
         }

      }
      else if(parent instanceof ASPCommandBar)
      {
         ASPCommandBar bar = (ASPCommandBar)parent;
         Vector cmds = bar.getAllCustomCommands();

         // First create new popup instances for every custom command group
         Vector groups = bar.getCustomCommandGroups();
         Vector labels = bar.getCustomCommandLabels();

         // Create all child popups and add the custom commands to those first
         String g_label;
         String g_id;
         for(int i=0;i<groups.size();i++)
         {
//            if(asppage.existsASPPopup((String)groups.elementAt(i)))
//               continue;

            ASPPopup p = asppage.newASPPopup("c" + (String)groups.elementAt(i));
            p.setSubmenu();
            g_label = (String)labels.elementAt(i);
            g_id    = (String)groups.elementAt(i);
            if(cmds.size() > 0)
            {
               for(int j = 0;j <= cmds.size()-1;j++) {
                  ASPCommandBarItem item = (ASPCommandBarItem) cmds.elementAt(j);
                  if(item.getCustomGroup().equals(g_id))
                  {
                     if(!Str.isEmpty(item.getName()))
                     {
                        // Added by Terry 20140926
                        // Adding command bar confirm msg of ASPTable row
                        String confirm_msg = item.getCmdConfirmMsg();
                        // Added end
                        if(!item.getCommandId().startsWith("LINK_CMD"))  
                        {
                           if(item.hasIcon())
                              // Modified by Terry 20140926
                              // Confirm msg of command bar item
                              // Original:
                              // p.addItem(item.getIconImage(),item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              if (Str.isEmpty(confirm_msg))
                                 p.addItem(item.getIconImage(),item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              else
                                 p.addItem(item.getIconImage(),item.getName(),
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       "set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                              // Modified end
                           else
                              // Modified by Terry 20140926
                              // Confirm msg of command bar item
                              // Original:
                              // p.addItem(item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              if (Str.isEmpty(confirm_msg))
                                 p.addItem(item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                              else
                                 p.addItem(item.getName(),
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       "set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                              // Modified end
                        }
                        else if(item.getCommandId().startsWith("LINK_CMD"))
                        {
                           if(item.hasIcon())
                              // Modified by Terry 20141003
                              // Confirm msg of command bar item
                              // Original:
                              // p.addItem(item.getIconImage(),item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                              if (Str.isEmpty(confirm_msg))
                                 p.addItem(item.getIconImage(),item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                              else
                                 p.addItem(item.getIconImage(),item.getName(),"javascript:"+
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();}");
                              // Modified end
                           else
                              // Modified by Terry 20141003
                              // Confirm msg of command bar item
                              // Original:
                              // p.addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                              if (Str.isEmpty(confirm_msg))
                                 p.addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                              else
                                 p.addItem(item.getName(),"javascript:"+
                                       "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                       cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();}");
                              // Modified end
                         }   
                     }   
                     else
                        p.addSeparator();
                  }
               }
            }
         }
         
         addItem(mgr.translateJavaText("FNDCMDDOCMAN: Documents"),bar.getBlock().getName() + "DocMan()");
         addItem(mgr.translateJavaText("FNDCMDDUPLICATE: Duplicate"),"commandSet('" + bar.getBlock().getName() + ".DuplicateRow','"+Str.nvl(bar.getClientFunction(bar.DUPLICATEROW),"")+"')");
         
         if (!bar.getBlock().isHistoryDisabled())
         {   
            if(bar.getBlock().hasASPTable())
               addItem(mgr.translateJavaText("FNDPOPHISTORY: History"),"commandSet('" + bar.getBlock().getName() + ".RowHistory','__showHistory("+bar.getBlock().getASPTable().getTableNo()+")')");
            else
               addItem(mgr.translateJavaText("FNDPOPHISTORY: History"),"commandSet('" + bar.getBlock().getName() + ".RowHistory','__showHistory(\\'"+bar.getBlock().getName()+"\\')')");
         }
         
         if(!bar.getBlock().isSendToDisabled())
         {
            Object STmenu[] = bar.getBlock().getSendToMenu();
            if(STmenu!=null)
               addSubmenu((String)STmenu[0], (ASPPopup)STmenu[1]);
         }
         
         addSeparator();

         if(cmds.size() > 0)
         {
            Vector subs_done = new Vector();
            int j = 0;
            for(int i = 0;i <= cmds.size()-1;i++) {
               ASPCommandBarItem item = (ASPCommandBarItem) cmds.elementAt(i);
               if(item.getCustomGroup().equals(""))
               {
                  // Added by Terry 20140926
                  // Adding command bar confirm msg of ASPTable row
                  String confirm_msg = item.getCmdConfirmMsg();
                  // Added end
                  if(!Str.isEmpty(item.getName()) && !item.getCommandId().startsWith("LINK_CMD"))
                  {
                     if(item.hasIcon())
                        // Modified by Terry 20140926
                        // Confirm msg of command bar item
                        // Original:
                        // addItem(item.getIconImage(),item.getCustomGroup() + item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                        if (Str.isEmpty(confirm_msg))
                           addItem(item.getIconImage(),item.getCustomGroup() + item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                        else
                           addItem(item.getIconImage(),item.getCustomGroup() + item.getName(),
                                 "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                 "set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                        // Modified end
                     else
                        // Modified by Terry 20140926
                        // Confirm msg of command bar item
                        // Original:
                        // addItem(item.getCustomGroup() + item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                        if (Str.isEmpty(confirm_msg))
                           addItem(item.getCustomGroup() + item.getName(),"set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')");
                        else
                           addItem(item.getCustomGroup() + item.getName(),
                                 "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                 "set"+((ASPCommandBar)parent).getBlock().getName()+"Command('" + item.getCommandId() + "');commandSet('" + bar.getBlock().getName() + ".Perform','"+Str.nvl(bar.getClientFunction(item.getCommandId()),"")+"')}");
                        // Modified end
                  }
                  else if  (item.getCommandId().startsWith("LINK_CMD"))
                  {
                     if(item.hasIcon())
                        // Modified by Terry 20141003
                        // Confirm msg of command bar item
                        // Original:
                        // addItem(item.getIconImage(),item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                        if (Str.isEmpty(confirm_msg))
                           addItem(item.getIconImage(),item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                        else
                           addItem(item.getIconImage(),item.getName(),"javascript:"+
                                 "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                 cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();}");
                        // Modified end
                     else
                        // Modified by Terry 20141003
                        // Confirm msg of command bar item
                        // Original:
                        // addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                        if (Str.isEmpty(confirm_msg))
                           addItem(item.getName(),"javascript:"+cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();");
                        else
                           addItem(item.getName(),"javascript:"+
                                 "if(confirm('" + mgr.translateJavaText(confirm_msg) + "')){" + 
                                 cmd_prefix+item.getCommandId()+"_"+((ASPCommandBar)parent).getBlock().getName()+"();}");
                        // Modified end
                  }
                  else
                     addSeparator();
               }
               else
               {
                  if (! subs_done.contains(item.getCustomGroup())) {
                     addSubmenu(bar.getCustomGroupLabel(item.getCustomGroup()),asppage.getASPPopup("c" + item.getCustomGroup()));
                     subs_done.addElement(item.getCustomGroup());
                  }
               }
            }
         }

         Vector popflds = bar.getAllPopupField();
         if(popflds!=null & popflds.size()>0)
         {
            Iterator itr = popflds.iterator();
            addSeparator();
            while(itr.hasNext())
            {
               ASPField f = (ASPField) itr.next();
               addSubmenu(f.getCustomPopupLabel(), f.getCustomPopupMenu());
            }
         }
         
         Object blkPopMenus[]      = bar.getBlock().getPopupMenus();
         Object blkPopMenuLabels[] = bar.getBlock().getPopupMenuLabels();
         if(blkPopMenus!=null & blkPopMenus.length>0)
         {
            addSeparator();
            for(int p=0; p<blkPopMenus.length; p++)
            {
               addSubmenu((String) blkPopMenuLabels[p],(ASPPopup) blkPopMenus[p]);
            }
         }

      }
      else if(parent instanceof ASPPage )
      {
         if(name.equals("views"))
         {
            generatePortalsMenu();
         }
         else if(name.equals("navigate"))
         {  
            addItem(mgr.translateJavaText("FNDNAVTREE: Tree Navigator"),"document.location='" +mgr.correctURL(asppage.getASPManager().getConfigParameter("APPLICATION/LOCATION/NAVIGATOR"))+"'");
            generateMyLinks(); //generate links saved in page profile from MyLinks portlets
         }
         else if(name.equals("help"))
         {
            //Bug 42801, start            
            
            //String pageurl = mgr.getURL()+"&USAGEID="+mgr.getUsageKey();
            ASPPortal portal = ((ASPPage)parent).getASPPortal();
            if (portal == null)
            {
               ASPPage parentPage = (ASPPage)parent;
               if(parentPage.isChildFrameHelpReference())
               {
                  String path = mgr.getASPConfig().getApplicationPath();
                  String child_help_url = parentPage.getChildFrameHelpReference();
                  addItem(mgr.translateJavaText("FNDHELPONFIELDS: Help on fields"),"showFieldHelp('" + path + child_help_url + "','"+mgr.getUsageKey()+"')");
                  addItem(mgr.translateJavaText("FNDHELPONTHISPAGE: Help on this page"),"showHelp('" + path + child_help_url + "')");
               }
               else
               {
                  addItem(mgr.translateJavaText("FNDHELPONFIELDS: Help on fields"),"showFieldHelp('" + mgr.getURL() + "','"+mgr.getUsageKey()+"')");
                  addItem(mgr.translateJavaText("FNDHELPONTHISPAGE: Help on this page"),"showHelp('" + mgr.getURL() + "')");
               }
            }
            else
               addItem(mgr.translateJavaText("FNDHELPONTHISPAGE: Help on this page"),"showHelp('" + mgr.getURL() + "')");
                        
            addItem(mgr.translateJavaText("FNDHOWTOUSEHELP: How to use Help"),"showHelp('General/UsingIFSHelp')");
            addItem(mgr.translateJavaText("FNDHOWTOUSEWEBCLIENT: How to use IFS Web Client"),"showHelp('')");
            //addItem(mgr.translateJavaText("FNDSEARCHHELP: Search"),"showHelp('General/Search')");
            //Bug 42801, end
            this.addSeparator();
            //Bug 42692, start
            addItem(mgr.translateJavaText("FNDSERNAVIIFSONWEB: IFS on the Web"),"showNewBrowser('http://www.ifsworld.com')");
            addItem(mgr.translateJavaText("FNDSERNAVSUPPORT: IFS Support"),"showNewBrowser('" + mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS") + "Link.page" + "')");
            this.addSeparator();
            addItem(mgr.translateJavaText("FNDSERNAVABOUT: About"),"showNewBrowser_('" + mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS") + "About.page" + "',520,375,'YES')");
            //Bug 42692, end
            //Bug 42495, end
         }
         else if(name.equals("options"))
         {
            //Options menu
            ASPPage parentPage = (ASPPage)parent;
            if(!parentPage.isLogonPage())
            {
               if(!parentPage.getASPProfile().isUserProfileDisabled())
               {

               addItem(mgr.translateJavaText("FNDNAVLINK: Save Link"), "showNewBrowser_('"+mgr.correctURL(mgr.getConfigParameter("APPLICATION/LOCATION/ADDLINK"))+"&'+'SEARCHBASE='+URLClientEncode(SEARCHBASE)+'&SEARCHPARAM='+URLClientEncode(SEARCHPARAM)+'&HEADTITLE='+URLClientEncode(HEADTITLE)+'&CASESENCETIVE='+URLClientEncode(CASESENCETIVE),790,450)");

               }
            }
            else
               addItem(mgr.translateJavaText("FNDNAVLINK: Save Link"), "showNewBrowser_('"+mgr.correctURL(mgr.getConfigParameter("APPLICATION/LOCATION/ADDLINK"))+"&'+'SEARCHBASE='+URLClientEncode(SEARCHBASE)+'&SEARCHPARAM='+URLClientEncode(SEARCHPARAM)+'&HEADTITLE='+URLClientEncode(HEADTITLE)+'&CASESENCETIVE='+URLClientEncode(CASESENCETIVE),790,450)");

            ASPPortal portal = ((ASPPage)parent).getASPPortal();
            if(portal!=null && !portal.isCustomizeMode())
               addItem(mgr.translateJavaText("FNDPORTALREFRESH: Refresh"), "refreshPortal()");
            
            if (mgr.getASPConfig().isMultiLanguageEnabled())
               addItem( "FNDSERNAVSELECTLANG: Change Language", "document.location='"+mgr.correctURL(mgr.getASPConfig().getLanguageURL(false)+"?CURRENT_URL='+URLClientEncode(CURRENT_URL)"));
            
            if (portal == null)
            {
               addItem("FNDSERDEFAULTLAY: Show page in Default Layout", "showNewBrowser_(CURRENT_URL + '&"+ ASPManager.DEFAULT_LAYOUT +"=Y')");
               addItem(mgr.translateJavaText("FNDNAVMAKEPAGECOPY: Make a copy with default settings"), "showNewBrowser_('"+mgr.correctURL(mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS")+"CopyPage.page?COPY=Y")+"'+'&ORIGINAL='+URLClientEncode(SEARCHBASE)+'&HEADTITLE='+URLClientEncode(HEADTITLE)+'&DEF_KEY='+URLClientEncode(f.__DYNAMIC_DEF_KEY.value),790,450)");
            }
            
            if(!parentPage.isPagePropertiesDisabled() && (parentPage.hasBlockLayouts() || parentPage.hasTabContainers()))
            {
               Vector tabc = asppage.getAllTabContainer();
               Iterator tabc_itr = tabc.iterator();

               String frozentabs = "&FROZENTABS=";
            
               while(tabc_itr.hasNext())
               {
                  ASPTabContainer cont = (ASPTabContainer) tabc_itr.next();
                  if(cont.isFirstTabFrozen())
                  {
                     frozentabs+=cont.getName()+"^"+cont.getTabId(0)+",";
                  }
               }

               if(frozentabs.length()>0)
               {
                  frozentabs = frozentabs.substring(0,frozentabs.length()-1);
               }            

               if((!parentPage.isLogonPage()) && (portal==null))
                      addItem(mgr.translateJavaText("FNDNAVPAGEPROP: Page Properties"),"showNewBrowser_('" +mgr.correctURL(mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS")+ "PageProperties.page?URL="+mgr.getASPPage().getPoolKey()) +frozentabs+"',790,500)");
            }
            
            if(mgr.getASPConfig().isFormBasedAuth() && !mgr.isRWCHost())               
               addItem(mgr.translateJavaText("FNDNAVLOGOFFLA: Sign out"),"javascript:removeClusterCookie();document.location='" +mgr.correctURL(mgr.getASPConfig().getLogonURL()+"?LOGOUT=YES")+"'");
            else if(!mgr.isRWCHost() && mgr.getASPConfig().isUserExternallyIdentified() && mgr.getASPConfig().isLogoffEnabled())               
               addItem(mgr.translateJavaText("FNDNAVLOGOFFLA: Sign out"),"javascript:removeClusterCookie();document.location='" +mgr.correctURL(mgr.getASPConfig().getLogonURL()+"?LOGOUT=YES&EXTERNAL=YES")+"'");
            
         }
         else if(name.equals("config_views"))
         {  
            //portal manage menu            
            addItem(mgr.translateJavaText("FNDCONTHISVIEW: Configure this view"),"customizePage()");
            addItem(mgr.translateJavaText("FNDCONREMVIEW: Remove this view"), "removePortalView()");
            addItem(mgr.translateJavaText("FNDCONNEWVIEW: Create new view"), "createPortalView()");
            addItem(mgr.translateJavaText("FNDCONSAVEVIEW: Save a copy of this view"), "savePortalView()");
            addSeparator();
            addItem(mgr.translateJavaText("FNDCONFIGUREPORTAL: Rearrange portal views"),"rearrangePortalViews()");
            
         }

      }

      }
      catch( Throwable any )
      {
         error(any);
      }

   }

   //==========================================================================
   //  Public properties and other public functions for using from script.
   //==========================================================================

  /**
   * Returns the name of the popup.
   */
   public String getName()
   {
      return name;
   }

  /**
   * Returns the width of the popup.
   */
   public int getWidth()
   {
      return width;
   }

  /**
   * Returns the number of items of the popup.
   */
   public int countItems()
   {
      return items.size();
   }


  /**
   * Adds an item to the popup.
   */
   public ASPPopup addItem( String n )
   {
      items.addElement(new menuItem(asppage.getASPManager().translate(n,getASPPage()),""));
      return this;
   }

  /**
   * Adds an item with an associated javascript action.
   */
   public ASPPopup addItem( String n, String action )
   {
      items.addElement(new menuItem(asppage.getASPManager().translate(n,getASPPage()),action));
      return this;
   }

   /**
   * Adds an item with an associated javascript action and an icon.
   */
   public ASPPopup addItem( String icon, String n, String action )
   {
      items.addElement(new menuItem(icon, asppage.getASPManager().translate(n,getASPPage()),action));
      this.is_icon_menu = true;
      return this;
   }

   /**
    * Adds a separator to the popup.
    */
   public ASPPopup addSeparator()
   {
      items.addElement(new menuItem());
      return this;
   }

   /**
    * Adds a submenu to the popup.
    */
   public ASPPopup addSubmenu(String name,ASPPopup submenu)
   {
      try {
         items.addElement(new menuItem(name,submenu.generateCall(),submenu));
      } catch (Throwable e) {error(e);}

      return this;
   }

   /**
    * Adds a submenu to the popup.
    */
   public ASPPopup addSubmenu(String name,String action,ASPPopup submenu)
   {
      try {
         items.addElement(new menuItem(name,action,submenu));
      } catch (Throwable e) {error(e);}

      return this;
   }

   class menuItem
   {

      String icon;
      String label;
      String action;
      boolean has_icon = false;
      boolean separator;
      ASPPopup submenu;
      String action_param_fields;
      boolean has_action_params = false;

      menuItem()
      {
         // Separator
         label = "<hr>";
         action = "";
         separator = true;
      }

      menuItem(String l)
      {
         label = l;
         action = "";
      }

      menuItem(String l,String a,ASPPopup pop)
      {
         label = l;
         action = a;
         submenu = pop;
      }

      menuItem(String l,String a)
      {
         label = l;
         action = a;
      }

      menuItem(String i, String l,String a)
      {
         icon = i;
         label = l;
         action = a;
         has_icon = true;
      }

      boolean isSeparator()
      {
         return separator;
      }
      
      boolean hasIcon()
      {
         return has_icon;
      }

      boolean isSubmenu()
      {
         return submenu != null;
      }
      
      boolean hasActionParams()
      {
         return has_action_params;
      }
      
      String getAction()
      {
          return action;
      }
      
      void setAction(String act)
      {
          action = act;
      }
      
      void setActionParamFields(String actionParamFields)
      {
         if(!getASPManager().isEmpty(actionParamFields))
         {
            this.action_param_fields = actionParamFields;
            this.has_action_params = true;
         }
      }
      
      String getActionParamFields()
      {
         return (has_action_params)? this.action_param_fields: getParameterFields();
      }
   }

   //==========================================================================
   //  Appearance
   //==========================================================================

  /**
   * Sets the width of the popup. Default is 120 pixels.
   */
   public ASPPopup setWidth( int new_width )
   {
      try
      {
         modifyingImmutableAttribute("WIDTH");
         width = new_width;
      }
      catch( Throwable any )
      {
         error(any);
      }

      return this;
   }

   /**
    * Set a submenu to the popup.
    */
   public ASPPopup setSubmenu()
   {
      try
      {
         modifyingImmutableAttribute("IS_SUBMENU");
         is_submenu = true;
      }
      catch( Throwable any )
      {
         error(any);
      }

      return this;
   }

   //==========================================================================
   //  Generation of Javascript and HTML
   //==========================================================================

   // The Netscape version needs to hide each element in the popup individually.
   void generateNetscapeHideScript(AutoString head)
   {
      String prefix = getASPPage().addProviderPrefix();
      head.append("\t\tdocument.layers[\"m",prefix,name,"Start","\"].visibility = \"hidden\";\n");
      for(int row = 0;row < items.size();row++)
         head.append("\t\tdocument.layers[\"m",prefix,name,Integer.toString(row),"\"].visibility = \"hidden\";\n");
      head.append("\t\tdocument.layers[\"m",prefix,name,"End","\"].visibility = \"hidden\";\n");
   }

  /**
   * Returns a javascript call that, when executed in the browser, displays the popup.
   */
   public String generateCall( )
   {
 //      return "javascript:showMenu(" + name + ")";

         return generateCall("",null);

   }

  /**
   * Returns a javascript call that, when executed in the browser, first executes the javascript
   * in the argument passed, and then displays the popup.
   */
   public String generateCall( String s )
   {
//      return "javascript:" + s + ";javascript:showMenu(" + name + ")";

      return generateCall(s,null);
   }

  /**
   * Returns a javascript call that, when executed in the browser, displays the popup.
   * The Vector passed is a list of strings, with values "true" or "false", one for each item in the popup.
   */
   public String generateCall( Vector list )
   {
      return generateCall("",list);
   }

   public String generateCall( String s, Vector list )
   {
//      boolean first = true;
      AutoString params = new AutoString();
      Vector sub_list = new Vector();
      
      if(list == null)
      {
         list = new Vector();
         for(int i = 0;i < items.size();i++)
            list.addElement("true");
      }
      else if(list!=null && (list.contains("s_false") || list.contains("s_true")))
      {
         int size = list.size();
         for(int i=0; i<size; i++)
         {
            String val = (String)list.elementAt(i);
            if(val.indexOf("s_")!=-1)
            {
               sub_list.addElement(val);
               list.removeElementAt(i);
               size = list.size();
               i--;
            }
         }
      }


      // For each menu item, check if it is a separator or not. If it is,
      // add it to parameter list, but don't increment counter (separators
      // are handled internally and can not be turned on/off).

      try
      {
         //debug("generateCall for " + this.name);

          int j = 0;
          int sl = 0;
          boolean lastWasSeparator = true;
          boolean lineRemains;
          for(int i = 0;i < items.size();i++)
          {
             if(DEBUG)
                debug(((menuItem)items.elementAt(i)).label);

             //debug(((menuItem)items.elementAt(i)).label);
             if(((menuItem)items.elementAt(i)).isSeparator())
             {
                if(!lastWasSeparator)
                {
                   // Check if any lines remain - if so, draw the separator.
                   lineRemains = false;
                   for(int r = j;r < list.size();r++)
                      if(((String)list.elementAt(r)).equals("true"))
                      {
                         lineRemains = true;
                         break;
                      }
                   if(!lineRemains)
                      for(int r = i+1;r < items.size();r++)
                        if(((menuItem)items.elementAt(r)).isSubmenu())
                        {
                           if(sub_list.size()>sl)
                           {
                              if(((String)sub_list.elementAt(sl)).equals("s_true"))
                              {
                                 lineRemains = true;
                                 break;
                              }                                 
                           }
                           else
                           {
                              lineRemains = true;
                              break;
                           }
                        }

                   if(lineRemains)
                   {
                      params.append("true,");
                      lastWasSeparator = true;
                      if(DEBUG) debug("TRUE (separator)");
                   }
                   else
                      params.append("false,");
                }
                else
                   params.append("false,");
             }
             else
             {
               if(((menuItem)items.elementAt(i)).isSubmenu())
               {
                  if(sub_list.size()>sl)
                  {
                     if(((String)sub_list.elementAt(sl)).equals("s_true"))
                     {
                        lastWasSeparator = false;
                        params.append("true,");
                     }
                     else
                        params.append("false,");
                     sl++;
                  }
                  else
                  {
                     lastWasSeparator = false;
                     params.append("true,");
                  }
               }
               else
               {


                   if(DEBUG) debug(Integer.toString(j) + " - " + list.size());

                   if(!(j < list.size()))
                   {
                      if(DEBUG) debug("FALSE (not enough params)");
                   }
                   else
                   {
                      if(((String)list.elementAt(j)).equals("true"))
                         lastWasSeparator = false;
                      params.append((String)list.elementAt(j),",");
                      if(DEBUG) debug((String)list.elementAt(j));
                      j++;
                   }

               }

             }
          }
          if(DEBUG) debug(params.toString());
          if(s != null && !s.equals(""))
             s = "javascript:" + s + ";";

          String paramlist = params.toString();
          if(paramlist.length() > 0)
             paramlist = paramlist.substring(0,paramlist.length()-1);

          // Set instance and all submenu instances to be used in request
          setUsedInRequest();

          if(is_submenu)
             return s + "javascript:showMenu('m" + getASPPage().addProviderPrefix() + name + "',[" + paramlist + "],true)";
          else
             return s + "javascript:showMenu('m" + getASPPage().addProviderPrefix() + name + "',[" + paramlist + "],false)";

      } catch (Exception e) {error(e);return "";}

   }

  /**
   * Returns the HTLM definition of the popup.
   */
   public String generateDefinition()
   {
      ASPManager mgr = asppage.getASPManager();
      AutoString def = new AutoString();
//      def.append("\n<!-- Menu definition, ");
      String prefix = getASPPage().addProviderPrefix();
      String action;
      String prefix2 = prefix;
      if(!Str.isEmpty(prefix2))
         prefix2 = prefix+'.';

      String id = "m" + prefix + name;
      menuItem item;
      submenu_arrow = mgr.getConfigParameter("POPUP/IMAGES/SUBMENU_ARROW","menu_arrow.gif");
      String images_location = mgr.getASPConfig().getImagesLocation();
      
      if(mgr.isMozilla()||mgr.isExplorer())
      {
         // Netscape 7 solution
         int sep_length = width;
         int label_length = 0;
         for(int row=0;row<items.size();row++)
         {
            item = (menuItem) items.elementAt(row); 
            if(item.label.length()>label_length) 
               label_length = item.label.length();                 
         }
         label_length = label_length * 10;
         if (sep_length < label_length)
            sep_length = label_length;
        
         def.append("<div style=\"z-index:1000;width:",Integer.toString(width));
         def.append("px; position: absolute; visibility:hidden; top: 0px; left: 0px;\" id=\"",id,"\">");
         def.append("<table class=portlet-menu width=",Integer.toString(width),"px border=0 cellpadding=0px cellspacing=0px><tr width=100%><td width=100%>\n");
         def.append("<table width=100% cellpadding=",mgr.isExplorer()?"2":"1","px cellspacing=0px><tr><td width=100%>\n");
         def.append("<table width=100% border=0 cellpadding=2px cellspacing=0px>\n");        

         String onmouseover="";
         String onmouseout="";

         for(int row=0;row<items.size();row++)
         {
            item = (menuItem) items.elementAt(row);

            action = Str.replace(item.action,"__PORTLET__",prefix2);
            action = Str.replace(action,"__PORTLETCMD__",prefix);

            if( !item.isSeparator() )
            {
               onmouseover = "this.className='portlet-menu-item-selected';";
               onmouseout = "this.className='portlet-menu-item';";
            }
            else{
               onmouseover="";
               onmouseout="";
            }
                         
            if( item.isSubmenu() )
               def.append("\n<tr class=portlet-menu-item style=\"display:block;\" onClick=\"_menu('", id ,"');",action,"\"");
            else
               def.append("\n<tr class=portlet-menu-item style=\"display:block;\" onClick=\"_menu(0);setFormReference('"+getASPPage().getId()+"');",action,"\"");
            def.append(" id=\"m",prefix,name,Integer.toString(row), "\"");
            def.append(" onmouseover=\"",onmouseover,"\" onmouseout=\"",onmouseout,"\" ");
            def.append(">\n");

            if( item.isSeparator() )
               def.append("\t<td colspan=2 width="+sep_length+"><img width=100% height=2 src=\""+mgr.getASPConfig().getImagesLocation()+"menu_separator.gif\"></td>\n");
            else            
            {
               String icon_str = "";
               // Modified by Terry 20140404
               // Change image width from 16 to real width
               int img_with = 0;
               if(is_icon_menu)
                  if(item.hasIcon())
                  {
                     // Original:
                     // icon_str = "<img align=\"absmiddle\" height=16 width=16 border=0 src=\""+item.icon+"\">&nbsp;";
                     img_with = getImageWith(item.icon);
                     icon_str = "<img align=\"absmiddle\" height=16 width=" + img_with + " border=0 src=\""+item.icon+"\">&nbsp;";
                     // Modified end
                  }
                  else
                  {
                     // Original:
                     // icon_str = "<img align=\"absmiddle\" height=16 width=16 border=0 src=\""+mgr.getASPConfig().getImagesLocation()+"table_empty_image.gif\">&nbsp;";
                     img_with = 16;
                     icon_str = "<img align=\"absmiddle\" height=16 width=" + img_with + " border=0 src=\""+mgr.getASPConfig().getImagesLocation()+"table_empty_image.gif\">&nbsp;";
                     // Modified end
                  }
               
               // Origianl:
               // def.append("\t<td ",item.isSubmenu()?"":"colspan=2"," width=100% NOWRAP>",icon_str+item.label,"</td>\n");
               if (img_with > 16)
                  def.append("\t<td ",item.isSubmenu()?"":"colspan=2"," width=100% NOWRAP>",icon_str,"</td>\n");
               else
                  def.append("\t<td ",item.isSubmenu()?"":"colspan=2"," width=100% NOWRAP>",icon_str+item.label,"</td>\n");
               // Modified end
            }
          
            if( item.isSubmenu() )
               def.append("<td align=right valign=middle><img src=\"",images_location,submenu_arrow,"\"></td>");
           
            def.append("</tr>");
         }
     
         def.append("</table>\n");
         def.append("</tr></td></table>\n");
         def.append("</td></tr></table>\n");
         def.append("</div>\n");
      }  
      //Bug 42727, end
      else
      {
         // Netscape solution - each row is a layer.
         // def.append(" Netscape version -->\n");
          
         String label;         
         String font_name = "Verdana";
         int font_size = 8;
         int temp_width = width ;
         
         Frame frame = new Frame();
         frame.addNotify();
         MediaTracker trk = new MediaTracker(frame);
         Image dummy_image = frame.createImage(100,100);
         Graphics dummy_g = dummy_image.getGraphics();
         Font font = new Font(font_name,Font.PLAIN,font_size+1); 
         FontMetrics f_metrics = dummy_g.getFontMetrics(font);
         
         for(int row=0;row<items.size();row++)
         {
             item = (menuItem) items.elementAt(row);
             label = item.label;
             if(temp_width < f_metrics.stringWidth(label))
                temp_width = f_metrics.stringWidth(label);            
         }          
         
         temp_width = (int)(temp_width+(temp_width * 0.15));
        
         def.append("\n<script>\nvar ",id,"Width=");
         def.appendInt(temp_width+10);
         def.append(";\n</script>");

         def.append("\n\n<DIV class=menuRow id=\"",id,"Start\">");
         def.append("\n\t<table border=0 cellpadding=0 cellspacing=0><tr>\n\t\t<td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td>");
         def.append("\n\t\t<td colspan=2 bgcolor=black width=");
         def.appendInt(temp_width+10);

         def.append(">",mgr.getEmptyImage(1,1),"</td>");
         def.append("\n\t\t<td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td>\n\t</tr></table>");
         def.append("\n</DIV>\n");
         
         for(int row=0;row<items.size();row++)
         {
            item = (menuItem) items.elementAt(row);

            label = item.label;
            action = item.action;

            action = Str.replace(action,"__PORTLET__",prefix2);
            action = Str.replace(action,"__PORTLETCMD__",prefix);

            def.append("\n<DIV class=menuRow id=\"",id);
            def.appendInt(row);                         
            def.append("\">\n");

            def.append("<table border=0 cellpadding=0 cellspacing=0>");

            def.append("\t<tr>\n\t\t<td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td>");
            def.append("\n\t\t<td height=20pt width=");
            if( item.isSubmenu() )
               def.appendInt(temp_width);
            else
               def.appendInt(temp_width+10);
            def.append(" class=menuContent NOWRAP>");
            def.append(item.isSeparator()?"":"&nbsp;","<a class=menuLink href=\"javascript:");

            if( item.isSubmenu() )
               def.append("_menu('",id,"');");
            else
               def.append("_menu(0);");

            def.append(action,"\">",label,"</a></td>");

            if( item.isSubmenu() )
               def.append("<td width=10 class=menuContent NOWRAP align=right valign=middle><img src=\"",images_location,submenu_arrow,"\">&nbsp;</td>");

            def.append("\n\t\t<td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td>");

            def.append("</tr>");
            
            def.append("\n\t</table>\n");
            def.append("</DIV>\n");

         }
         
         def.append("<DIV class=menuRow id=\"",id,"End\">");
         def.append("<table border=0 cellpadding=0 cellspacing=0><tr><td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td>");
         def.append("<td colspan=2 bgcolor=black width=");
         def.appendInt(temp_width+10);
         def.append(">",mgr.getEmptyImage(1,1),"</td>");
         def.append("<td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td></tr></table>");
         def.append("</DIV>");

      }

      return def.toString();
   }

   private void generateMyLinks() throws FndException
   {
      if(DEBUG) debug("ASPPopup.generateMyLinks");
      ASPManager mgr   = asppage.getASPManager();
      //RIRALK
      ASPBuffer buffer = mgr.newASPBuffer();
      //RIRALK
      ASPBuffer aspbuf = asppage.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS,false);
      ASPBuffer avail_links  = mgr.newASPBuffer();
      int no_of_links = 0;
      
      if (aspbuf!=null) 
      {
         for( int p=0; p<aspbuf.countItems(); p++)
         {
            String view_name = aspbuf.getNameAt(p);
            //RIRALK
            avail_links = asppage.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+view_name+"/Links",false);
            if(avail_links!=null)
            {
               buffer = buffer.addBuffer("VIEWS", avail_links);
               no_of_links = no_of_links + avail_links.countItems();
            }
          }
      }

      if(buffer == null) return; //no links saved in page profile
      
      Buffer views = buffer.getBuffer();
      int views_count = views.countItems();
      ASPPopup submenu = this;

      if(no_of_links>1)
      {
         submenu = asppage.newASPPopup("MyLinks",this);
         submenu.setSubmenu();
      }

      for(int p=0; p<views_count; p++)
      {
         Buffer links = views.getItem(p).getBuffer();
         int count = links.countItems();
         
         for(int i=0; i<count; i++)
         {
            Buffer buf = links.getItem(i).getBuffer();
            String name = buf.getString("TITLE","");
            if (Str.isEmpty(name)) continue;
            ASPPopup linkmenu;
            linkmenu = asppage.newASPPopup("MyLink_" + p + i,submenu);
            linkmenu.setSubmenu();

            for(int j=1; j<buf.countItems(); j++)
            {
               String desc = "";
               String url  = "";
               boolean decode = false;
               String[] link = buf.getString(j).split("~");
               try{
                   desc   = link[0];
                   decode = "DECODE".equals(link[1]);
                   url    = link[2];
               }catch(ArrayIndexOutOfBoundsException ae){
                   desc   = link[0];
                   url    = link[1];
               }
               
                if(url.indexOf("?")>0){
                    String[] _params = url.split("\\&");
                    url = "";
                    for(int x=0; x<_params.length; x++){
                        if(_params[x].indexOf("=")>0){
                            String[] pair = _params[x].split("\\=");
                            if(pair.length==2){
                                if(pair[1].equals(mgr.URLDecode(pair[1])))
                                    pair[1] = mgr.URLEncode(pair[1]);
                                url += getASPPage().groupParams(pair[0], pair[1], x!=0, decode);
                            } else
                                url += getASPPage().groupParams(pair[0], "", x!=0, false);
                        } else
                            url += _params[x];
                    }
                }

               linkmenu.addItem(desc,"document.location='" + mgr.decodedURLWithCSV(url) + "'");
            }
            submenu.addSubmenu(name, linkmenu);
         }
      }

      if(no_of_links>1)
         addSubmenu(mgr.translateJavaText("FNDNAVMYLINKS: My Links"), submenu);
   }

   private void generatePortalsMenu()
   {
      ASPManager mgr = asppage.getASPManager();
      String home = mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL");
      Buffer views = asppage.getPortalViews();
      if(views!=null && views.countItems()>1)
      {
         BufferIterator itr = views.iterator();
         while( itr.hasNext() )
         {
            Item item = itr.next();
            String name = mgr.URLEncode(item.getName());
            String desc = item.getString();
            addItem(desc,"document.location='"+home+"?__VIEW_NAME="+name+"'");
         }
      }
   }

   /**
    * Removes all items from the popup.
    */
   public void removeAllItems()
   {
      items.removeAllElements();
   }
   
   Vector getItems()
   {
       return items;
   }

   /**
    * Addes parameter fields to an ASPPopup object. These param values can be used inside
    * functions used for popup items.
    * @param param_fields Comma seperated list of parameter field names.
    * @return The ASPPopup object with parameter fileds.
    */
   public ASPPopup setParameterFields(String paramFields) {
      try
      {
         if(paramFields==null) return this;
         modifyingImmutableAttribute("PARAM_FIELDS");
         this.param_fields = paramFields;
         has_param_fields = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public String getParameterFields()
   {
      return (has_param_fields)? this.param_fields: null;
   }

   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================
   
   public String generateMobileCall(Vector list)
   {
      AutoString params = new AutoString();

      if(list == null)
      {
         list = new Vector();
         for(int i = 0;i < items.size();i++)
            list.addElement("true");
      }

      try
      {
         int j = 0;
         boolean lastWasSeparator = true;
         boolean lineRemains;
         for(int i = 0;i < items.size();i++)
         {
            if(((menuItem)items.elementAt(i)).isSeparator())
            {
               if(!lastWasSeparator)
               {
                  // Check if any lines remain - if so, draw the separator.
                  lineRemains = false;
                  for(int r = j;r < list.size();r++)
                  if(((String)list.elementAt(r)).equals("true"))
                  {
                     lineRemains = true;
                     break;
                  }
                  if(!lineRemains)
                     for(int r = i+1;r < items.size();r++)
                        if(((menuItem)items.elementAt(r)).isSubmenu())
                        {
                           lineRemains = true;
                           break;
                        }

                  if(lineRemains)
                  {
                     params.append("true,");
                     lastWasSeparator = true;
                  }
                  else
                     params.append("false,");
               }
               else
                  params.append("false,");
            }
            else
            {
               if(((menuItem)items.elementAt(i)).isSubmenu())
               {
                  lastWasSeparator = false;
                  params.append("false,");
               }
               else
               {
                  if(!(j < list.size()))
                  {
                     if(DEBUG) debug("FALSE (not enough params)");
                  }
                  else
                  {
                     if(((String)list.elementAt(j)).equals("true"))
                        lastWasSeparator = false;
                     params.append((String)list.elementAt(j),",");
                     if(DEBUG) debug((String)list.elementAt(j));
                     j++;
                  }
               }
            }
         }

         String paramlist = params.toString();
         if(paramlist.length() > 0)
            paramlist = paramlist.substring(0,paramlist.length()-1);      

         String[] para = paramlist.split(",");
         menuItem item;
         AutoString def = new AutoString();
         boolean devider = false;
         
         for(int row=0;row<items.size();row++)
         {
            if(para[row].equalsIgnoreCase("false")) continue;
            
            item = (menuItem) items.elementAt(row);
                        
            if(!item.isSeparator() )
            {
               if(devider)
                  def.append(" | ");
               def.append("<a href=\"javascript:",item.action,"\">",item.label,"</a>");
            }
            
            devider = true;
         }
         
         return def.toString();
      }
      catch (Exception ec)
      {
         error(ec);
      }

      return "";
   }

   // Added by Terry 20140404
   public int getImageWith(String imgUrl)
   {
      ASPManager mgr = asppage.getASPManager();
      int imageWith;
      URL url = null;
      InputStream is = null; 
      BufferedImage img = null;
      try
      {
         String urlPrefix = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext() + "/secured/";
         url = new URL(urlPrefix + imgUrl.substring(3));
         is = url.openStream();
         img = ImageIO.read(is);
         imageWith = img.getWidth();
      }
      catch (Exception e)
      {
         imageWith = 16;
      }
      finally
      {
         try
         {
            is.close();
         }
         catch (Exception e)
         {
            imageWith = 16;
         }
      }
      return imageWith;
   }
   // Added end
}
