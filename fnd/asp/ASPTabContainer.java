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
 * File        : ASPTabContainer.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Micke A  1998-Mar-25 - Created
 *
 *    WebKit 0.5:
 *    Micke A  1998-Apr-27 - Added the "ASPTabContainer.setTabEnabled" method.
 *    Micke A  1998-May-08 - Added the "dirty flag" handling on each tab's URL.
 *                           ( See "ASPTabContainer.addMiddleLine()". )
 *    Jacek P  1998-Jul-21 - Call to native function OutputDebugString()
 *                           replaced with static function Util.debug()
 *    Jacek P  1998-Aug-07 - Removed 'throw Exception'
 *    Jacek P  1998-Aug-10 - Changed constructor to 'package'
 *    Marek D  1998-Aug-21 - New structure of ASPConfig.ifm
 *    Jacek P  1998-Aug-26 - Added default value to getConfigParameter() call
 *    Jacek P  1998-Oct-16 - Added possibility to send only the name of
 *                           a server function as the url parameter to
 *                           the addTab() function by using
 *                           ASPManager.commandLinkFunction() (Todo: #2627).
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *                           Class ASPTab extends now ASPPageSubElement.
 *    Jacek P  1999-Feb-22 - Added optional attribute 'name'. New constructor.
 *    Marek D  1999-Mar-05 - Implementation of ASPPoolElement state diagram
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-Aug-11 - Colors and fonts are now taken from ASPConfig.ifm.
 *    Jacek P  1999-Aug-25 - Added support for dynamic tabs. New public functions.
 *                           addDynamicTab(), saveActiveTab(), getActiveTab(),
 *    Johan S  2000-Apr-11 - Added showNewTabsInit(). Displays v.3 tabs.
 *    Ramila H 2001-07-07  - deprecate showDynamcTab() and added method showDynamicTab().
 *    ChandanaD2004-Mar-23 - Merged with SP1. 
 *    ChandanaD2004-May-12 - Updated for the new style sheets.
 *    Mangala  2004-Jul-30 - Added new public method to change Tab item label.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/05/10 amiklk bug 90225, Modified showNewTabsInit(), addTabContent() for scrolling customizability.
 * 2010/03/04 amiklk bug 89186, Modified clone()
 * 2010/02/01 amiklk bug 87765, Modified showNewTabsInit(), addTabContent() to implement scrollablility in tab panel.
 * 2009/07/07 amiklk bug 84150, Modified addTabContent() to use hashCode of the tab label as the tab ID
 * 2008/11/05 buhilk bug 78256, Modified ASPTab.construct() method to replace all "." chars with "_".
 * 2008/10/10 dusdlk bug 77095, Added new variable org_active_tab_no and updated the methods saveActiveTab(), getActiveTab(),addTabContent(), createTabPos(), doActivate(),getActiveTab,getInitialActiveTab() and saveActiveTab().
 * 2008/09/24 dusdlk bug 77095, Added getTabId(int index), GetTabCount(), getTabLabel(int index), isTabRemoved(int index), checkTabSecurity(), getActiveTab(), SetActiveTab(), getInitialActiveTab(), isActiveTab(String tabid) and createTabPos().
 *                              Updated saveActiveTab() to set value for is_first_tab_frozen variable.
 * 2007/07/26 buhilk bug 66875, Modified addTabContent() to expand clickable area.
 * 2007/07/02 sadhlk Merged Bug 64254, Modified showTabsInit() and showNewTabsInit() to set current scroll position.
 * 2007/01/30 buhilk Bug 63250, Improved theming support in IFS clients.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/07/29 11:21:01  mapelk
 * Proxy related bug fixed. Read image path each time instead of reading it during the construction.
 * 
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import java.util.*;

import ifs.fnd.util.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;

/**
 * A utility class for ASP scripts.
 * Typically a script will perform the following steps:<br>
 *<pre>
 * step 1:
 *   o create an instance of this class calling ASPManager's newASPTabContainer()
 *   o use the addTab(label,url) method to add a number of tabs
 *   o use the setContainerWidth(width) method to set the width of the content square
 *   o use the setContainerHeight(height) method to set the height of the content square
 *   o use the setActiveTab(no) method to set the top level tab
 *   o use the showTabsInit() method to receive the HTML code that will
 *     display the tabs and the left edged border line of the content square<br>
 * step 2:
 *   o add the content of the tab<br>
 * step 3:
 *   o use the showTabsFinish() method to receive the HTML code that will display
 *     the right and bottom edged border lines of the content square<br>
 *</pre>
 */
public class ASPTabContainer extends ASPPageElement
{
   //==========================================================================
   //  Immutable attributes
   //==========================================================================

   private int tab_id; 
   private String   name;
   private ASPTab[] tabs;
   private int      first_dynamic;
   private int      count;
   private int      tab_height;

   private ASPTab[] sorted_tabs;
   
   //==========================================================================
   //  Mutable attributes
   //==========================================================================

//   private int     active_tab_no;          private int     pre_active_tab_no;
   private boolean dirty_flag_enabled;     private boolean pre_dirty_flag_enabled;
   private int     tab_width;              private int     pre_tab_width;
   //private String  tab_font_face;          private String  pre_tab_font_face;
   //private String  tab_font_color;         private String  pre_tab_font_color;
   //private String  tab_font_color_dis;     private String  pre_tab_font_color_dis;
   private boolean show_table_border;      private boolean pre_show_table_border;
   private int     tab_space_width;        private int     pre_tab_space_width;
   private int     tab_left_space_width;   private int     pre_tab_left_space_width;
   private int     container_width;        private int     pre_container_width;
   private int     container_height;       private int     pre_container_height;
   private int     container_space;        private int     pre_container_space;
   
   //==========================================================================
   //  Transient temporary variables
   //==========================================================================

   private int num_of_spanned_cols;
   private int active_tab_no;
   private int org_active_tab_no;
   private AutoString tmpbuf = new AutoString();

   transient TabContainerProfile profile;     
   transient TabContainerProfile pre_profile;
   private transient boolean    user_profile_prepared;
      
   //==========================================================================
   //  Immutable contsants
   //==========================================================================

   //private String  img_dir;
   private String  table_cell_colour;
   private boolean is_first_tab_frozen = true;

   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPTabContainer");

   private static String  img_tab_left_grey                  = "GreyLeft20t15.gif";
   private static String  img_tab_middle_grey                = "GreyMid20t1.gif";
   private static String  img_tab_right_grey                 = "GreyRight20t15.gif";

   private static String  img_tab_top_left              = "topleft.gif";           
   private static String  img_tab_top                   = "top.gif";           
   private static String  img_tab_top_right             = "topright.gif";
   private static String  img_tab_left                  = "left.gif";
   private static String  img_tab_right                 = "right.gif";
   private static String  img_white_dot                 = "whitedot.gif";
   private static String  img_white_spacer              = "6px_space.gif";
   //private static String  img_red_dot                   = "reddot.gif";
   private static String  img_tab_bottom_left           = "bottomleft.gif";
   private static String  img_tab_bottom_right          = "bottomright.gif";
   private static String  img_square_edge_right         = "rightedge.gif";
   private static String  img_square_edge_bottom        = "bottomedge.gif";
   private static String  img_square_edge_bottom_corner = "corneredge.gif";
//   private static String  table_cell_colour             = " bgcolor=\"#C0C0C0\"";
   private static String  table_cell_colour_transp      = "";
   private static String  empty_table_cell              = "";

   //==========================================================================
   //  Construction
   //==========================================================================

   ASPTabContainer( ASPPage page ) throws FndException
   {
      super(page);
   }

   ASPTabContainer construct( String name )
   {
      this.name = name;
      first_dynamic = -1;
      ASPConfig cfg = getASPPage().getASPConfig();
      try
      {
         tabs = new ASPTab[16];
         sorted_tabs = new ASPTab[16];

         tab_width             = 50;    // Configurable.
         tab_height            = 19;
         tab_left_space_width  = 10;    // Configurable.
         tab_space_width       = 3;     // Configurable.

         container_width       = 500;   // Configurable.
         container_height      = 300;   // Configurable.
         container_space       = 5;     // Configurable.

         show_table_border     = false; // Configurable.
         dirty_flag_enabled    = true;  // Configurable.
         //active_tab_no         = 1;     // Starting at 1. Configurable.
         org_active_tab_no     = 1;
         tab_id = getASPPage().getTabId();
         is_first_tab_frozen = true;
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

   /**
    * Reset this tab container and all its tabs.
    */
   protected void doReset() throws FndException
   {
//      active_tab_no         = pre_active_tab_no;
      dirty_flag_enabled    = pre_dirty_flag_enabled;
      tab_width             = pre_tab_width;
      //tab_font_face         = pre_tab_font_face;
      //tab_font_color        = pre_tab_font_color;
      //tab_font_color_dis    = pre_tab_font_color_dis;
      show_table_border     = pre_show_table_border;
      tab_space_width       = pre_tab_space_width;
      tab_left_space_width  = pre_tab_left_space_width;
      container_width       = pre_container_width;
      container_height      = pre_container_height;
      container_space       = pre_container_space;
      is_first_tab_frozen   = true;

      for( int i=0; i<count; i++ )
      {
         tabs[i].reset();
         sorted_tabs[i].reset();         
      }
   }


   /**
    * Freeze this tab container and all its tabs.
    */
   protected void doFreeze() throws FndException
   {
//      pre_active_tab_no         = active_tab_no;
      pre_dirty_flag_enabled    = dirty_flag_enabled;
      pre_tab_width             = tab_width;
      //pre_tab_font_face         = tab_font_face;
      //pre_tab_font_color        = tab_font_color;
      //pre_tab_font_color_dis    = tab_font_color_dis;
      pre_show_table_border     = show_table_border;
      pre_tab_space_width       = tab_space_width;
      pre_tab_left_space_width  = tab_left_space_width;
      pre_container_width       = container_width;
      pre_container_height      = container_height;
      pre_container_space       = container_space;

      for( int i=0; i<count; i++ )
         tabs[i].freeze();
   }
   

   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      if(DEBUG) debug("ASPTabContainer.doActivate()");
      if(tab_id == 1)
         org_active_tab_no  = Integer.parseInt(getASPPage().getASPContext().readValue("ACTIVETAB","1"));
      else
         org_active_tab_no  = Integer.parseInt(getASPPage().getASPContext().readValue("ACTIVETAB"+tab_id,"1")); 
      
      if(DEBUG) debug("  org_active_tab_no="+org_active_tab_no);
   }
   
   
   /**
    * Clone this tab container into a new tab container included in the specified page.
    * The state of the returned object is DEFINED.
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPTabContainer t = new ASPTabContainer((ASPPage)page);

      t.name          = name;
      t.first_dynamic = first_dynamic;
      t.tab_height    = tab_height;
      t.dirty_flag_enabled    = t.pre_dirty_flag_enabled    = pre_dirty_flag_enabled;
      t.tab_width             = t.pre_tab_width             = pre_tab_width;
      t.show_table_border     = t.pre_show_table_border     = pre_show_table_border;
      t.tab_space_width       = t.pre_tab_space_width       = pre_tab_space_width;
      t.tab_left_space_width  = t.pre_tab_left_space_width  = pre_tab_left_space_width;
      t.container_width       = t.pre_container_width       = pre_container_width;
      t.container_height      = t.pre_container_height      = pre_container_height;
      t.container_space       = t.pre_container_space       = pre_container_space;

      t.tab_id = tab_id;
      //t.active_tab_no = active_tab_no;
      t.org_active_tab_no = org_active_tab_no;
      t.count = count;
      t.tabs = new ASPTab[t.count];
      t.sorted_tabs = new ASPTab[t.count];
      
      t.is_first_tab_frozen = is_first_tab_frozen;
      
      for( int i=0; i<t.count; i++ )
      {
         t.tabs[i] = (ASPTab)tabs[i].clone(t);
         t.sorted_tabs[i] = (sorted_tabs[i]!=null)? (ASPTab)sorted_tabs[i].clone(t): null;
      }

      t.setCloned();
      return t;
   }


   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
      
      for( int i=0; i<count; i++ )
         tabs[i].verifyPage(page);
   }


   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
      for( int i=0; i<count; i++ )
         tabs[i].scan(page,level+1);
   }

   //==========================================================================
   //  addTab
   //==========================================================================

   /**
    * Used for adding a new tab to the container.
    * The "label" parameter is the text that will appear on the tab.
    * The "url" parameter is the url that will be requested when the tab is clicked or
    * the name of the server function called by Command Link in ASPManager class.
    * The first call will create a tab with order no 1,
    * the second tab will have order no 2 and so on.
    *
    * @see ifs.fnd.asp.ASPManager#commandLinkActivated
    * @see ifs.fnd.asp.ASPManager#commandLinkFunction
    */
   public void addTab(String label, String url)
   {
      if(first_dynamic>0)
         error(new FndException("FNDTBCNOSTAT: Not possible to define static tab here!"));
      addTab(label, label, url, false);
   }

   /**
    * Used for adding a new tab to the container.
    * The first call will create a tab with order no 1,
    * the second tab will have order no 2 and so on.
    * @param id Unique id given for a tab.
    * @param label The text that will appear on the tab.
    * @param url The url that will be requested when the tab is clicked or the name of the server function called by Command Link in ASPManager class.
	 *
    * @see ifs.fnd.asp.ASPManager#commandLinkActivated
    * @see ifs.fnd.asp.ASPManager#commandLinkFunction
    */
   public void addTab(String id, String label, String url)
   {
  
      if(first_dynamic>0)
         error(new FndException("FNDTBCNOSTAT: Not possible to define static tab here!"));
      addTab(id, label, url, false);
   }
   
   /**
    * Used for adding a new dynamic tab to the container.
    * Note: Used with the earlier versions of web client.
    */
   public void addDynamicTab(String id, String label, String url)
   {
      addTab(id, label, url, true);
   }

   private void addTab(String id, String label, String url, boolean dynamic)
   {
      try
      {
         modifyingImmutableAttribute("TABS");
         if( !Str.isEmpty(url) && url.indexOf(":")<0 && url.indexOf("/")<0 && url.indexOf(".")<0 )
            url = "javascript:commandClick('"+url+"')";
         defineTab(id, getASPManager().translate(label), url, dynamic);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   private void defineTab(String id, String label, String url, boolean dynamic)
   {
      ASPTab tab = (new ASPTab(this)).construct(id,label,url,dynamic);
      if( count==tabs.length )
      {
         ASPTab[] newtabs = new ASPTab[2*count];
         System.arraycopy(tabs,0,newtabs,0,tabs.length);
         tabs = newtabs;
      }
      if(dynamic && first_dynamic<0)
         first_dynamic = count;
      tabs[count++] = tab;
   }

   /**
    * Used for getting the tab id for a specific index.
    * @param index location of the tab
    * @return String tab id
    */
   public String getTabId(int index)
   {
      return tabs[index].getId();
   }
  
   /**
    * Returns the number of tabs within the container.
    * @return int tab count
    */
   public int getTabCount()
   {
      int i=0;
      
      for(i=0;i<tabs.length;i++)
      {
         if(tabs[i]==null) return i;
      }
      return i;
   }
  
   /**
    * Returns the label of a tab at a specific index.
    * @param index tab index
    * @return String tab label
    */
  	public String getTabLabel(int index)
   {
      return tabs[index].getLabel();
   }
  
     /**
    * Used for getting the label of a tab with specific id.
    */
  public String getTabLabelbyId(String id)
  {
     for(int i =0;i<tabs.length;i++)
     {
        if(id.equals((tabs[i].getId().toUpperCase()))) return tabs[i].getLabel();
     }
     return null;
  }
 
   /**
    * Used for getting the boolean value to check if the removed property for specific tab is set to true.
    */
  public boolean isTabRemoved(int index)
  {
      return tabs[index].isRemoved();
  }
  
  /*
   * Method used to set the name of a tab container.
   */
  void setName(String name)
  {
     this.name = name;
  }
  
  /*
   * Returns false if the getInitialActiveTab() method has been called.
   */
  boolean isFirstTabFrozen()
  {
     return is_first_tab_frozen;
  }
  
   //==========================================================================
   //  Tab activation
   //==========================================================================

   /**
    * Writes the active tab number to the context.
    */
   public void saveActiveTab()
   {
      is_first_tab_frozen = false;
      
      if(DEBUG) debug("ASPTabContainer.saveActiveTab(): "+org_active_tab_no);
      if(tab_id == 1) 
         getASPPage().getASPContext().writeValue("ACTIVETAB",org_active_tab_no+"");
      else
         getASPPage().getASPContext().writeValue("ACTIVETAB"+tab_id,org_active_tab_no+"");  
   }
   
   
   /**
    * @Returns the active tab number.
    */
   public int getActiveTab()
   {
      if(active_tab_no==-1) return getInitialActiveTab();
      for(int i=0;i<this.getTabCount();i++)
      {
		   if(tabs[i]==null) break;
         if(tabs[i].getId().equals(sorted_tabs[active_tab_no-1].getId()) && !tabs[i].isRemoved() && tabs[i].checkEnabled())
         {
            if(DEBUG) debug("ASPTabContainer.getActiveTab(): "+(i+1));
            org_active_tab_no = i+1;
            return org_active_tab_no;
         }
      }                             
      return getInitialActiveTab();
   }
   
   //==========================================================================
   //  Configuration methods.
   //==========================================================================

   /**
    * Used for setting the active tab.
    * The "no" parameter corresponds to the order of calls to the "addTab" method,
    * starting at 1 for the first call to "addTab".
    * @see     ifs.fnd.asp.ASPTabContainer#addTab
    */
   public void setActiveTab(int no)
   {
      try
      {
         modifyingMutableAttribute("ACTIVE_TAB_NO");
         if(no<=tabs.length && tabs[no-1]!=null)
         {
            if(tabs[no-1].isRemoved())
            {
               getInitialActiveTab();
               return;
            }
            String id = tabs[no-1].getId();
            org_active_tab_no = no;
            for(int i=0;i<sorted_tabs.length;i++)
            {
               if(id.equals(sorted_tabs[i].getId()))
               {
                  active_tab_no = i+1;
                  break;
               }
            }
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for disabling/enabling a tab.
    * The "no" parameter corresponds to the order of calls to the "addTab" method,
    * starting at 1 for the first call to "addTab".
    * The "enabled" parameter will disable (value=false) or
    * enable (value=true) the tab. Every tab is enabled by default.
    * @see     ifs.fnd.asp.ASPTabContainer#addTab
    * @see     ifs.fnd.asp.ASPTabContainer#setTabRemoved
    */
   public void setTabEnabled(int no, boolean enabled)
   {
      try
      {
         tabs[no-1].setEnabled(enabled);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /**
    * Used for remove/unremove a tab.
    * The "no" parameter corresponds to the order of calls to the "addTab" method,
    * starting at 1 for the first call to "addTab".
    * The "remove" parameter will remove (value=false) or
    * unremove (value=true) the tab. 
    * @see     ifs.fnd.asp.ASPTabContainer#addTab
    * @see     ifs.fnd.asp.ASPTabContainer#setTabEnabled
    */
   public void setTabRemoved(int no, boolean remove)
   {
      try
      {
         tabs[no-1].setRemoved(remove);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /*
    * @Returns the initial active tab number.
    */
   public int getInitialActiveTab()
   {      
      active_tab_no = -1;
      
      for(int j=0;j<sorted_tabs.length;j++)
      {
         if(sorted_tabs[j]==null) break;
         if(!sorted_tabs[j].isRemoved() && sorted_tabs[j].checkEnabled())
         {
            active_tab_no = j+1;
            break;
         }
      }
   
      if(active_tab_no==-1) return -1;
      
      for(int i=0;i<tabs.length;i++)
      {
         if(tabs[i]==null) break;
         if(sorted_tabs[active_tab_no-1].getId().equals(tabs[i].getId()) && !tabs[i].isRemoved() && tabs[i].checkEnabled())
         {
            org_active_tab_no = i+1;
            break;
         }
      }
      return org_active_tab_no;
   } 
   
   /*
    * Returns true if the tabid is the current active tab of the tab container
    * The "tabid" parameter corresponds to the specified tab id to be checked. 
    * @Returns true if soecified tab is the active tab.
    */
   public boolean isActiveTab(String tabid)
   {
      if(org_active_tab_no==-1) return false;
      
      for(int i=0;i<tabs.length;i++)
      {
         if(tabs[i]==null) break;
         if(tabs[org_active_tab_no-1].getId().equals(tabid)) return true;
      }
      return false;
   }
   
   /**
    * Changes the Tab label after defning the tab. This is a Mutable method.
    */
   public void setLabel(int no, String label)
   {
      try
      {
         tabs[no-1].setLabel(getASPManager().translate(label));
      }
      catch( Throwable any )
      {
         error(any);
      }     
   }

   /**
    * Used for disabling/enabling the dirty flag handling of the form.
    * Calling "setDirtyFlagEnabled(false);" will disable the "dirty-flag" handling.
    * Calling "setDirtyFlagEnabled(true);" will enable it.
    * The "dirty-flag" handling is enabled by default.
    * If the handling is enabled, and the user has done any modification
    * to the record, he will be reminded to save the modifications if he
    * clicks on another tab.
    */
   public void setDirtyFlagEnabled(boolean enabled)
   {
      try
      {
         modifyingMutableAttribute("DIRTY_FLAG_ENABLED");
         dirty_flag_enabled = enabled;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the width of the tabs.
    * The width is measured in pixels.
    * The default value is 50 pixels.
    */
   public void setTabWidth(int width)
   {
      try
      {
         modifyingMutableAttribute("TAB_WIDTH");
         tab_width = width;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the font face of the tabs.
    * For setting the Verdana font, the call should be "setTabFontFace("Verdana");".
    * The default value is "Arial".
    *@deprecate : This method has been deprecated. Tab look & feel should be changed with CSS
    */
   public void setTabFontFace(String font_face)
   {
      /*
      try
      {
         modifyingMutableAttribute("TAB_FONT_FACE");
         tab_font_face = font_face;
      }
      catch( Throwable any )
      {
         error(any);
      }
       */
   }


   /**
    *@deprecate : This method has been deprecated. Tab look & feel should be changed with CSS
    */
   public void setTabFontColor(String font_color)
   {
      /*
      try
      {
         modifyingMutableAttribute("TAB_FONT_COLOR");
         tab_font_color = font_color;
      }
      catch( Throwable any )
      {
         error(any);
      }
       */
   }
   
   /**
    *@deprecate : This method has been deprecated. Tab look & feel should be changed with CSS
    **/

   public void setTabFontColorDisabled(String font_color)
   {
      /*
      try
      {
         modifyingMutableAttribute("TAB_FONT_COLOR_DIS");
         tab_font_color_dis = font_color;
      }
      catch( Throwable any )
      {
         error(any);
      }
       */
   }

   /**
    * Used for displaying the border of the tab container HTML table.
    * This can be useful at design time.
    * To display the table set the "visible" parameter to 1.
    */
   public void setTableVisibility(boolean visible)
   {
      try
      {
         modifyingMutableAttribute("SHOW_TABLE_BORDER");
         show_table_border = visible;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the space between the left edge of the content square and the left most tab.
    * The default value is 10 pixels.
    */
   public void setLeftTabSpace(int left_tab_space)
   {
      try
      {
         modifyingMutableAttribute("TAB_LEFT_SPACE_WIDTH");
         tab_left_space_width = left_tab_space;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the space between the tabs.
    * The default value is 3 pixels.
    */
   public void setTabSpace(int tab_space)
   {
      try
      {
         modifyingMutableAttribute("TAB_SPACE_WIDTH");
         tab_space_width = tab_space;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the width of the content square.
    * The default value is 500 pixels.
    * @see     ifs.fnd.asp.ASPTabContainer#setContainerHeight
    * @see     ifs.fnd.asp.ASPTabContainer#setContainerSpace
    */
   public void setContainerWidth(int width)
   {
      try
      {
         modifyingMutableAttribute("CONTAINER_WIDTH");
         container_width = width;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the height of the content square.
    * The default value is 300 pixels.
    * @see     ifs.fnd.asp.ASPTabContainer#setContainerWidth
    * @see     ifs.fnd.asp.ASPTabContainer#setContainerSpace
    */
   public void setContainerHeight(int height)
   {
      try
      {
         modifyingMutableAttribute("CONTAINER_HEIGHT");
         container_height = height;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Used for setting the distance between the edges and content of the content square.
    * The default value is 5 pixels.
    * @see     ifs.fnd.asp.ASPTabContainer#setContainerHeight
    * @see     ifs.fnd.asp.ASPTabContainer#setContainerWidth
    */
   public void setContainerSpace(int space)
   {
      try
      {
         modifyingMutableAttribute("CONTAINER_SPACE");
         container_space = space;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Returns the name of the tab container.
    */
   public String getName()
   {
      return name;
   }

   //==========================================================================
   //  Tab presentation (complete).
   //==========================================================================

   /**
    * This is the method to call to display the tabs without the edged content square beneath.
    * @see     ifs.fnd.asp.ASPTabContainer#showTabsInit
    * @see     ifs.fnd.asp.ASPTabContainer#showTabsFinish
    */
   public String showTabs()
   {
      try
      {
         tmpbuf.clear();

         initTable(tmpbuf,show_table_border);
         addTopLine(tmpbuf);
         addMiddleLine(tmpbuf);
         addBottomLine(tmpbuf);
         closeTable(tmpbuf);

         return tmpbuf.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Tab presentation call #1.
   //==========================================================================

   /**
    * This is the first method to call to display the tabs with the edged content square beneath.
    * The tabs and the left verctical edged line of the content square will be displayed.
    * A call to this function must be followed by a later call to "showTabsFinish".
    * @see     ifs.fnd.asp.ASPTabContainer#showTabsFinish
    */
   public String showTabsInit()
   {
       if(getASPPage().getVersion()>=3)
           return showNewTabsInit();
      try
      {
         getASPManager().setScrollPosition();
         
         tmpbuf.clear();

         initTable(tmpbuf,show_table_border);
         addTopLine(tmpbuf);
         addMiddleLine(tmpbuf);
         addBottomLine(tmpbuf);
         initSquare(tmpbuf,show_table_border);

         return tmpbuf.toString();
      }
      catch(Throwable any)
      {
         error(any);
         return null;
      }
   }
    
    /**
     * Simpler tabs that fits Webkit 3.
     */

    String showNewTabsInit()
    {
      try{
         getASPManager().setScrollPosition();
         
          tmpbuf.clear();
          
         //Adding space on top of the tabs
         addTableBegin(tmpbuf);
            tmpbuf.append("\t<tr vAlign=\"top\">\n");
            tmpbuf.append("\t<td><img border=0 src=\""+getASPPage().getASPConfig().getImagesLocation()+img_white_spacer+"\"></td>\n");
         finishTable(tmpbuf); 
          
          //create the margin table for page layout and alignment
          tmpbuf.append("<table border=0 cellspacing=0 cellpadding=0 width=100%><tr>");
             tmpbuf.append("<td>&nbsp;&nbsp;</td>");
             tmpbuf.append("<td>");
                //tab container that scrolls
                tmpbuf.append("<div id=\"tab_container_"+ tab_id +"\" style=\"overflow-x:hidden;\">\n");
                   tmpbuf.append("\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
                      tmpbuf.append("\t<tr vAlign=\"top\">\n");
                        addTabContent(tmpbuf);
                      tmpbuf.append("\t</tr>\n");
                   tmpbuf.append("</table>\n");
                tmpbuf.append("</div>\n");
             tmpbuf.append("</td>\n");

             //arrow buttons
             tmpbuf.append("<td width=20 id=\"tab_con_" + tab_id + "_left\"><img id=\"tab_con_"+ tab_id +"_left_image\" style=\"cursor:pointer\" src='" + getASPManager().getASPConfig().getImagesLocation() + getASPManager().getUserTheme() + "/tab_left.gif" + "' onclick=\"TabScroller.moveLeft("+tab_id+")\"></td> \n");
             tmpbuf.append("<td width=20 id=\"tab_con_" + tab_id + "_right\"><img id=\"tab_con_"+ tab_id +"_right_image\" style=\"cursor:pointer\" src='" + getASPManager().getASPConfig().getImagesLocation() + getASPManager().getUserTheme() + "/tab_right.gif" + "' onclick=\"TabScroller.moveRight("+tab_id+")\"></td> \n");

             tmpbuf.append("<td>&nbsp;&nbsp;</td>\n");
          tmpbuf.append("</tr></table>\n");

          //scroll to which tab item
          int tab_at_left=0;
          if(getASPManager().readValue("TABS_"+ tab_id +"_TAB_AT_LEFT")!=null)
             tab_at_left = Integer.parseInt(getASPManager().readValue("TABS_"+ tab_id +"_TAB_AT_LEFT"));

          //make the TabContainer javascript item and hidden field to pass tab item scrolled at
          tmpbuf.append("<script language='JavaScript'> TabScroller.registerTabContainer("+tab_id+", "+tab_at_left+"); </script>\n");
          tmpbuf.append("<input type='hidden' name='TABS_"+tab_id+"_TAB_AT_LEFT' value='"+tab_at_left+"'/>\n");
          
          return tmpbuf.toString();
      }
      catch(Throwable any)
      {
         error(any);
         return null;
      }
    }

   //==========================================================================
   //  Tab presentation call #2.
   //==========================================================================

   /**
    * This is the second method to call to display the tabs with the edged content square beneath.
    * The right and the bottom edged lines of the content square will be displayed.
    * A call to this function must be preceded by a call to "showTabsInit".
    * @see     ifs.fnd.asp.ASPTabContainer#showTabsInit
    */
   public String showTabsFinish()
   {
      try
      {
       if(getASPPage().getVersion()>=3)
           return "";

         tmpbuf.clear();

         closeSquare(tmpbuf);
         closeTable(tmpbuf);

         return tmpbuf.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Private
   //==========================================================================

   private void addTableBegin(AutoString tmpbuf)
    {
        tmpbuf.append("\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
    }


    //This function will add the actual tabs.
    //Learning from past experience with ASPBlockLayout, I don't try to reuse as much code as possible.
    //(modularisation is of course often extremely good, but not in this case. I think.).
    //Each else-statement has it's own full html-implementation, please notice this.

   private void addTabContent(AutoString tmpbuf)
    {      
      String dirtystring;
      String img_dir = getASPPage().getASPConfig().getImagesLocation()+getASPManager().getUserTheme()+"/";
      boolean is_rtl = getASPManager().isRTL();
      if(active_tab_no == -1) getInitialActiveTab();
      for ( int i=0; i < count; i++ )
      {
          if (sorted_tabs[i].isRemoved()) continue;
          String curl = sorted_tabs[i].getUrl();
          String clabel = sorted_tabs[i].getLabel();
          String button_id = tab_id + "_"+i+"_";
          if (i+1==active_tab_no) // The selected tab.
              {
                 //***id is used in tab scrolling position tracking****
                 tmpbuf.append("\t\t<td id=\""+button_id+"L\" align=\"center\" height=\"17\" noWrap valign=\"middle\" width=\"15\" background=",img_dir,(is_rtl)?img_tab_right_grey:img_tab_left_grey,">\n");
                 tmpbuf.append("\t\t\t<p align=\"center\">&nbsp;</p>\n");
                 tmpbuf.append("\t\t</td>\n");

                 tmpbuf.append("\t\t<td id=\""+button_id+"M\" align=\"center\" height=\"17\" noWrap valign=\"middle\" background=",img_dir,img_tab_middle_grey,">\n");
                 tmpbuf.append("\t\t\t<font class=\"activeTabText\">",clabel,"</font>\n");
                 tmpbuf.append("\t\t</td>\n");
          
                 tmpbuf.append("\t\t<td id=\""+button_id+"R\" align=\"center\" height=\"17\" noWrap valign=\"middle\" width=\"15\" background=",img_dir, (is_rtl)?img_tab_left_grey:img_tab_right_grey,">&nbsp;\n");
                 tmpbuf.append("\t\t</td>\n");
              }
          else if ((curl.length()>0)&&(sorted_tabs[i].checkEnabled()==true)) // This is not active, but enabled.
             {
                 String mouse_over = "_hoverTabOnMouseOver('"+button_id+"');this.style.cursor='pointer';";
                 String mouse_out = "_hoverTabOnMouseOut('"+button_id+"');this.style.cursor='default';";

                 if(curl.indexOf("javascript:")!=-1)
                    curl = curl.replaceAll("javascript:","");
                 else if(curl.indexOf(".page")!=-1)
                    curl = "document.location='"+curl+"'";
                 
                 //prepare scrolled tab to be sent to server
                 curl = "TabScroller.saveTabPositions();" + curl;

                 if (dirty_flag_enabled==true)
                    dirtystring = "onClick=\"javascript:if(isNotDirty()) "+curl+"\"";
                 else
                    dirtystring = "onClick=\"javascript:"+curl+"\"";
                 
                 //***id is used in tab scrolling position tracking****
                 tmpbuf.append("\t\t<td ",dirtystring," onmouseover=\""+mouse_over+"\" onmouseout=\""+mouse_out+"\" id=\""+button_id+"L\" align=\"center\" height=\"17\" noWrap valign=\"middle\" width=\"15\" class=\"tabEnabledNormal"+((getASPManager().isRTL())?"Right":"Left")+"\">\n");
                 tmpbuf.append("\t\t\t<p align=\"center\">&nbsp;</p>\n");
                 tmpbuf.append("\t\t</td>\n");

                 tmpbuf.append("\t\t<td ",dirtystring," onmouseover=\""+mouse_over+"\" onmouseout=\""+mouse_out+"\" id=\""+button_id+"M\" align=\"center\" height=\"17\" noWrap valign=\"middle\" class=\"tabEnabledNormalMiddle\">\n");
                 tmpbuf.append("\t\t\t<font class=\"activeTabText\">",clabel,"</font>");
                 tmpbuf.append("\t\t</td>\n");
          
                 tmpbuf.append("\t\t<td ",dirtystring," onmouseover=\""+mouse_over+"\" onmouseout=\""+mouse_out+"\" id=\""+button_id+"R\" align=\"center\" height=\"17\" noWrap valign=\"middle\" width=\"15\" class=\"tabEnabledNormal"+((getASPManager().isRTL())?"Left":"Right")+"\">&nbsp;\n");
                 tmpbuf.append("\t\t</td>\n");
             }
          else // this tab is not active and not enabled.
             {
                 //***id is used in tab scrolling position tracking****
                 tmpbuf.append("\t\t<td id=\""+button_id+"L\" align=\"center\" height=\"17\" noWrap valign=\"middle\" width=\"15\" class=\"tabEnabledNormalLeft\">\n");
                 tmpbuf.append("\t\t\t<p align=\"center\">&nbsp;</p>\n");
                 tmpbuf.append("\t\t</td>\n");

                 tmpbuf.append("\t\t<td id=\""+button_id+"M\" align=\"center\" height=\"17\" noWrap valign=\"middle\" class=\"tabEnabledNormalMiddle\">\n");
                 tmpbuf.append("\t\t\t<font class=\"inactiveTabText\">",clabel,"</font>\n");
                 tmpbuf.append("\t\t</td>\n");
          
                 tmpbuf.append("\t\t<td id=\""+button_id+"R\" align=\"center\" height=\"17\" noWrap valign=\"middle\" width=\"15\" class=\"tabEnabledNormalRight\">&nbsp;\n");
                 tmpbuf.append("\t\t</td>\n");
             }
      }
    }

    private void finishTable(AutoString tmpbuf)
    {
        tmpbuf.append("\t</tr>\n");
        tmpbuf.append("</table>");
    }

   private void addTopLine( AutoString html )
   {
      // Open the table row.
      html.append("\n  <tr>");
      // Empty cell above the left square edge line.
      html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");
      String img_dir = getASPPage().getASPConfig().getImagesLocation();
      for ( int i=0; i <= count-1; i++ )
      {
         //Do not draw tab if isRemoved
          if (sorted_tabs[i].isRemoved()) continue;
          
         // Left space or space between tabs.
         if (( (i==0)&&(tab_left_space_width>0) ) || ((i>0)&&(tab_space_width>0)))
            html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");

         // Draw the left corner, the line and the right corner on the top of each tab.
         html.append("\n    <td",table_cell_colour_transp,">");
         html.append("<img src=\"",img_dir,img_tab_top_left,"\" width=3 height=3></td>");
         html.append("\n    <td>");
         html.append("<img src=\"",img_dir,img_tab_top,"\" width=");
         html.appendInt(tab_width);
         html.append(" height=3></td>");
         html.append("\n    <td",table_cell_colour_transp,">");
         html.append("<img src=\"",img_dir,img_tab_top_right,"\" width=3 height=3></td>");

         // Right space.
         if (i==count-1)
            html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");
      }

      // Empty cell above the right square edge line.
      html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");

      // Close the table row.
      html.append("\n  </tr>");
   }


   private void addMiddleLine( AutoString html )
   {
      String sDirtyFlagEvent;
      String img_dir = getASPPage().getASPConfig().getImagesLocation();
      // Open the table row.
      html.append("\n  <tr>");

      // Empty cell above the left square edge line.
      html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");

      for ( int i=0; i <= count-1; i++ )
      {
         //Do not draw tab if isRemoved
         if (sorted_tabs[i].isRemoved()) continue;
          
         if (( (i==0)&&(tab_left_space_width>0) ) || ((i>0)&&(tab_space_width>0)))
            html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");

         // Draw the left edge line, the text label and the right right edge line of each tab.
         html.append("\n    <td",table_cell_colour_transp," valign=bottom align=left>");
         html.append("<img src=\"",img_dir,img_tab_left,"\" width=3 height=");
         html.appendInt(tab_height);
         html.append("></td>");


         // The "dirty flag handling": When any tab URL is clicked,
         // the client side Javacript function "isNotDirty()" will be called.
         // The user will be asked if he want to save his changes, if they have not been saved.
         if (dirty_flag_enabled==true)
            sDirtyFlagEvent = "onClick=\"return isNotDirty()\" ";
         else
            sDirtyFlagEvent = "";


         // Create a hyperlink label, if a URL is supplied.
         // If no Url is supplied, the tab will be deactivated.
         String sUrl = sorted_tabs[i].getUrl();
         if ((sUrl.length()>0)&&(sorted_tabs[i].checkEnabled()==true))
         {
            // Highlighting label.
            //sHtml = sHtml.concat("\n    <td"+table_cell_colour+"><p align=\"center\">"+
            //                     "<small><font face=\""+tab_font_face+"\"><a CLASS=\"BB\" " + sDirtyFlagEvent +
            //                     "href=\""+tabs[i].getUrl()+"\">"+tabs[i].getLabel()+"</a></font></small></td>");

            // Underlined label.
            html.append("\n    <td><p align=center>");
            html.append("<a class=inactiveTabText",sDirtyFlagEvent,"href=",sUrl,">");
            //html.append("<small><font face=",tab_font_face," color=",tab_font_color,">");
            html.append( sorted_tabs[i].getLabel(),"</a></td>");
         }
         else
         {
            // Not clickable label.
            html.append("\n    <td><p align=center>");
            html.append("<font class=inactiveTabText>");
            html.append( sorted_tabs[i].getLabel(),"</font></td>");
         }


         html.append("\n    <td",table_cell_colour_transp," valign=bottom align=left>");
         html.append("<img src=\"",img_dir,img_tab_right,"\" width=3 height=");
         html.appendInt(tab_height);
         html.append("></td>");

         // Right space.
         if (i==count-1)
            html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");
      }

      // Empty cell above the right square edge line.
      html.append("\n    <td",table_cell_colour_transp,">",empty_table_cell,"</td>");

      // Close the table row.
      html.append("\n  </tr>");
   }

   private void addBottomLine( AutoString html )
   {
      String img_dir = getASPPage().getASPConfig().getImagesLocation();
      // Open the table row.
      html.append("\n  <tr>");

      // The upper left corner of the square.
      html.append("\n    <td>");
      html.append("<img src=\"",img_dir,img_white_dot,"\" width=1 height=1></td>");
      int used_width = 0;
      used_width = used_width + 1;

      for ( int i=0; i <= count-1; i++ )
      {
         //Do not draw tab if isRemoved
         if (sorted_tabs[i].isRemoved()) continue;
          
         // Left space or space between tabs.
         if ((i==0)&&(tab_left_space_width>0))
         {
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_white_dot,"\" width=");
            html.appendInt(tab_left_space_width);
            html.append(" height=1></td>");
            used_width = used_width + tab_left_space_width;
            num_of_spanned_cols = num_of_spanned_cols + 1;
         }
         // ... or space between tabs.
         else if ((i>0)&&(tab_space_width>0))
         {
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_white_dot,"\" width=");
            html.appendInt(tab_space_width);
            html.append(" height=1></td>");
            used_width = used_width + tab_space_width;
            num_of_spanned_cols = num_of_spanned_cols + 1;
         }


         // The active tab.
         if (i+1==active_tab_no)
         {
            // Draw the bottom left corner of the tab.
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_tab_bottom_left,"\" width=3 height=1></td>");
            // Add an empty cell beneath the text label.
            html.append("\n    <td>",empty_table_cell,"</td>");
            // Draw the bottom right corner of the tab.
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_tab_bottom_right,"\" width=3 height=1></td>");
         }
         // All inactive tabs.
         else
         {
            // Draw a white line with the same width as the inactive tab's.
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_white_dot,"\" width=3 height=1></td>");
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_white_dot,"\" width=");
            html.appendInt(tab_width);
            html.append(" height=1></td>");
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_white_dot,"\" width=3 height=1></td>");
         }
         used_width = used_width + 3 + tab_width + 3;
         num_of_spanned_cols = num_of_spanned_cols + 3;

         // Right space.
         if (i+1==count)
         {
            html.append("\n    <td>");
            html.append("<img src=\"",img_dir,img_white_dot,"\" width=");
            html.appendInt( container_width-used_width-2 );
            html.append(" height=1></td>");
            num_of_spanned_cols = num_of_spanned_cols + 1;
            // It's no idea updating the "container_width" variable now,
            // since this is the place where it's used.
         }
      }

      // The upper right corner of the square.
      html.append("\n    <td>");
      html.append("<img src=\"",img_dir,img_white_dot,"\" width=2 height=1></td>");

      // Close the table row.
      html.append("\n  </tr>");
   }

   private void initTable( AutoString html, boolean border )
   {
      //sHtml = "<img src=\""+img_dir+img_red_dot+"\" width=\""+container_width+"\" height=\"1\">";

      // Show the table border.
      if (border==true)
         html.append("\n<table border=1 cellspacing=0 cellpadding=5>");
      // Don't show the table border.
      else
         html.append("\n<table cellspacing=0 cellpadding=0>");
      num_of_spanned_cols = 0;    
   }

   private void closeTable( AutoString html )
   {
      html.append("\n</table>\n");
      //return "\n</table>\n"+"<img src=\""+img_dir+img_red_dot+"\" width=\""+container_width+"\" height=\"1\">\n";
   }

   private void initSquare( AutoString html, boolean border )
   {
      String img_dir = getASPPage().getASPConfig().getImagesLocation();
      // Open a new table row.
      html.append("\n  <tr>");
      // The left vertical line of the square.
      html.append("\n    <td>");
      html.append("<img src=\"",img_dir,img_white_dot,"\" width=1 height=");
      html.appendInt(container_height);
      html.append("></td>");
      // Open the table data cell which holds the main content of the page.
      html.append("\n    <td align=left valign=top colspan=");
      html.appendInt(num_of_spanned_cols);
      html.append(">");

      // Open a table which contains the main content of the tab.
      if (border==true)
      {
          // Show the table border.
          html.append("\n      <table border=1 cellspacing=0 cellpadding=");
          html.appendInt(container_space);
          html.append(">");
      }
      else
      {
          // Don't show the table border.
          html.append("\n      <table cellspacing=0 cellpadding=");
          html.appendInt(container_space);
          html.append(">");
      }
      // Open a single new table row.
      html.append("\n        <tr>");
      // Open a single new table data cell.
      html.append("\n          <td>");
   }

   private void closeSquare( AutoString html )
   {
      String img_dir = getASPPage().getASPConfig().getImagesLocation();
      // Close the single tab content table data cell.
      html.append("\n          </td>");
      // Close the single tab content table row.
      html.append("\n        <tr>");
      // Close the tab content table which holds the main content of the page.
      html.append("\n      </table>\n");

      // Close the table data cell which holds the previous table.
      html.append("</td>");
      // The right vertical line of the square.
      html.append("\n    <td>");
      html.append("<img src=\"",img_dir,img_square_edge_right,"\" width=2 height=");
      html.appendInt(container_height);
      html.append("></td>");
      // Close the content table row.
      html.append("\n  </tr>");
      // Open a new table row.
      html.append("\n  <tr>");
      // The down left corner of the square.
      html.append("\n    <td>");
      html.append("<img src=\"",img_dir,img_square_edge_bottom,"\" width=1 height=2></td>");
      // The bottom line of the square.
      html.append("\n    <td  colspan=");
      html.appendInt(num_of_spanned_cols);
      html.append("><img src=\"",img_dir,img_square_edge_bottom,"\" width=");
      html.appendInt(container_width-3);
      html.append(" height=2></td>");
      // The down right corner of the square.
      html.append("\n    <td>");
      html.append("<img src=\"",img_dir,img_square_edge_bottom_corner,"\" width=2 height=2></td>");
      // Close the table row.
      html.append("\n  </tr>");
   }

  /*
   * This function will check from press object security if the user have rights for specific tabs.
   * @Check if user have access rights for the tabs.
   */ 
   void checkTabSecurity()
   {
      if (tabs[0]==null)  return;
      
      ASPManager mgr = getASPManager();
      ASPPage page = mgr.isPortalPage()?mgr.getPortalPage():mgr.getASPPage();
      
      //Load profile visibility values into hash map
      ASPBuffer profile = this.getProfile();
      HashMap tab_visibility = new HashMap();
      tab_visibility.clear();
      for(int x=0; x<profile.countItems(); x++)
      {
         ASPBuffer buf = profile.getBufferAt(x);
         if(buf==null) continue;
         String id = buf.getValue("TAB_ID");
         String visibility = buf.getValue("ISTABVISIBLE");
         tab_visibility.put(id, visibility);
      }
            
      for( int i=0; i<tabs.length; i++ )
      {
         if (tabs[i]==null) break;
         if(!page.isObjectAccessible(page.getComponent().toUpperCase()+"/"+page.getPageName()+".page#"+tabs[i].getId()))
         {
            setTabRemoved(i+1,true);
         }
         else //check from profile           
         {
            String isVisible = (String) tab_visibility.get(tabs[i].getId());
            if("N".equals(isVisible))
            {
               setTabRemoved(i+1,true);              
            }
         }
       }
    }   
   
   /*
    * Method to re-order the tabs depending on their position value after reading from profile.
    * Hidden tabs will be added at the end of the new tabs array sorted_tabs.
    * @Populate sorted_tabs array with elements of tabs array according to new position.
    */
   public void createTabPos()
   {   
      ASPManager mgr = getASPManager();

      ASPBuffer profile = this.getProfile();
      int tabcount = this.getTabCount();
      
      HashMap tab_pos = new HashMap();
      tab_pos.clear();
      for(int x=0; x<profile.countItems(); x++)
      {
         ASPBuffer buf = profile.getBufferAt(x);
         if(buf==null) continue;
         String id = buf.getValue("TAB_ID");
         String pos = buf.getValue("TAB_POSITION");
         tab_pos.put(id, pos);
      }
      
      int j = tabcount-1;
      
      for(int i=0;i<tabcount;i++)         
      {
         String pos = (String)tab_pos.get(tabs[i].getId());
         int p = Integer.parseInt(pos);
         if(p!=-1)
         {
            if(i==org_active_tab_no-1)
               active_tab_no = p+1;
            sorted_tabs[p] = tabs[i];
         }
         else
         {
            if(i==org_active_tab_no-1)
               active_tab_no = -1;
            sorted_tabs[j] = tabs[i];            
            j--;
         }
      }    
   }

   //==========================================================================
   //  Dynamic tabs
   //==========================================================================

   public String preDefineDynamicTabs( String tabs_list )
   {
      if(DEBUG) debug("ASPTabContainer.preDefineDynamicTabs("+tabs_list+")");

      StringTokenizer st = new StringTokenizer(tabs_list, ",");

      tmpbuf.clear();
      while (st.hasMoreTokens())
      {
         String t = st.nextToken();
         tmpbuf.append("preDefine",t,"();");
      }
      String s = tmpbuf.toString();
      if(DEBUG) debug("  result="+s);
      return s;
   }


   public String defineDynamicTabs()
   {
      if(DEBUG) debug("ASPTabContainer.defineDynamicTabs()");

      tmpbuf.clear();
      for(int i=first_dynamic; i<count; i++)
         tmpbuf.append("define",tabs[i].getId(),"();");

      String s = tmpbuf.toString();
      if(DEBUG) debug("  result="+s);
      return s;
   }


   public String adjustDynamicTabs()
   {
      if(DEBUG) debug("ASPTabContainer.adjustDynamicTabs(): "+active_tab_no);

      tmpbuf.clear();
      for(int i=first_dynamic; i<count; i++)
         tmpbuf.append("adjust",tabs[i].getId(),"(",i+1==active_tab_no?"true":"false",");");

      String s = tmpbuf.toString();
      if(DEBUG) debug("  result="+s);
      return s;
   }


   public String reactivateDynamicTab( String action )
   {
      if(DEBUG) debug("ASPTabContainer.reactivateDynamicTab("+action+"): "+active_tab_no);

      if( active_tab_no <= first_dynamic )
         return ";";
      else
         return "reactivateTab"+tabs[active_tab_no-1].getId()+"('"+action+"');";
   }
   
   
   /**
    * @deprecated
    */
   public String showDynamcTab()
   {
      return showDynamicTab();
   }


   public String showDynamicTab()
   {
      if(DEBUG) debug("ASPTabContainer.showDynamcTab(): "+active_tab_no);
      tmpbuf.clear();
      for(int i=first_dynamic; i<count; i++)
         tmpbuf.append("generateHTML",tabs[i].getId(),"(",i+1==active_tab_no?"true":"false",");");

      String s = tmpbuf.toString();
      if(DEBUG) debug("  result="+s);
      return s;
   }

   public void setActiveDynamicTab( String tabid )
   {
      if(DEBUG) debug("ASPTabContainer.setActiveDynamicTab("+tabid+"): "+active_tab_no);

      active_tab_no = 1;
      for(int i=first_dynamic; i<count; i++)
         if( tabs[i].getId().equals(tabid) )
            active_tab_no = i+1;
   }


   public String validateDynamicTabs( String val )
   {
      if(DEBUG) debug("ASPTabContainer.validateDynamicTabs("+val+")");
      tmpbuf.clear();
      for(int i=first_dynamic; i<count; i++)
         tmpbuf.append("validate",tabs[i].getId(),"('",val,"');");

      String s = tmpbuf.toString();
      if(DEBUG) debug("  result="+s);
      return s;
   }


   public String storeCurrentDynamicTab()
   {
      if(DEBUG) debug("ASPTabContainer.storeDynamicTabs(): "+active_tab_no);

      if( active_tab_no <= first_dynamic )
         return ";";
      else
         return "store"+tabs[active_tab_no-1].getId()+"();";
   }
   
   /**
    * Store the specified profile information in the database.
    * Given buffer must have the same structure as the buffer returned
    * from getProfile().
    *
    * @see ifs.fnd.asp.ASPTabContainer.getProfile
    */
   public void saveProfile( ASPBuffer info )
   {
      saveProfile( info, true );
}

   /*
    * Used to update the profile buffer only and values do not get saved to database.
    */
   public void updateProfileBuffer( ASPBuffer info )
   {
      //called by CSL page to update all profile entries and save at once
      saveProfile( info, false );
}

   private void saveProfile( ASPBuffer info, boolean save_it )
   {
      if(DEBUG) debug("ASPTabContainer.saveProfile()");
      try
      {
         profile = new TabContainerProfile();
         profile.load(this,info.getBuffer());
         modifyingMutableAttribute("PROFILE");
         user_profile_prepared = false;

         ASPProfile prf = getASPPage().getASPProfile();
         if(DEBUG) debug("  profile="+prf);
         prf.update(this,profile);
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
    * this ASPTabContainer The buffer may be modified and then stored by calling
    * the method saveProfile().
    *
    * @see ifs.fnd.asp.ASPTabContainer.saveProfile
    */
   public ASPBuffer getProfile()
   {
      try
      {
         if(DEBUG) debug("ASPTabContainer.getProfile()");
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

   /*
    * called by CSL page to remove profile object from profile.
    */
   public void removeFromProfileBuffer()
   {
      removeProfile(false);
   }

   private void removeProfile(boolean save_it)
   {
      if(DEBUG) debug("ASPTabContainer.removeProfile()");
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
      profile = new TabContainerProfile();
      profile.construct(this);
      user_profile_prepared = false;
      pre_profile = profile;
   }

   /*
    * Method to prepare the profile information of tab container, if there is no profile then base profile created
    */
   void prepareProfileInfo(boolean apply_settings) throws FndException
   {
      if( user_profile_prepared ) return;
      if(DEBUG) debug("ASPTabContainer.prepareProfileInfo():");

      if( pre_profile==null ) createBaseProfile(); //create defualt profile using page design time defintions

         ASPProfile aspprf = getASPPage().getASPProfile();
         if(DEBUG) debug("   getASPProfile() = "+aspprf);
         if(getName()!=null)
            profile = (TabContainerProfile)aspprf.get(this,pre_profile);

      if (!apply_settings) return;

      modifyingMutableAttribute("PROFILE");

      user_profile_prepared = true;
   }      
}

class ASPTab extends ASPPageSubElement
{
   //==========================================================================
   //  Immutable attributes
   //==========================================================================

   private String  id;
   private String  label; private String pre_label;
   private String  url;
   private boolean dynamic;

   //==========================================================================
   //  Mutable attributes
   //==========================================================================

   private boolean enabled;   private boolean pre_enabled;
   private boolean removed;   private boolean pre_removed;

   //==========================================================================
   //  Construction
   //==========================================================================

   ASPTab( ASPTabContainer container )
   {
      super(container);
   }

   ASPTab construct( String id, String label, String url, boolean dynamic )
   {
      String temp = id;
      
      //If the label is passed as ID
      int col = temp.indexOf(":")+1;      
      if(col>0)
         temp = temp.substring(col).trim();
      
      //Replace all restricted chars with an "_" from the ID
      temp = temp.replace('#','_');
      temp = temp.replace(' ','_');
      temp = temp.replace('.','_');
      temp = temp.toUpperCase();
      
      this.id        = temp;
      this.label     = label;
      this.url       = url;
      this.dynamic   = dynamic;
      this.enabled   = true;
      return this;
   }

   //==========================================================================
   //  ASPPoolElement logic
   //==========================================================================

   protected void doReset() throws FndException
   {
      enabled = pre_enabled;
      removed = pre_removed;
      
      label = pre_label;
   }

   protected void doFreeze() throws FndException
   {
      pre_enabled = enabled;
      pre_label = label;
      pre_removed = removed;
   }
   
   protected ASPPoolElement clone( Object tab_container ) throws FndException
   {
      ASPTab t = new ASPTab((ASPTabContainer)tab_container);

      t.enabled   = t.pre_enabled = pre_enabled;
      t.removed   = t.pre_removed = pre_removed;

      t.id        = id;
      t.label     = t.pre_label = pre_label;
      t.url       = url;
      t.dynamic   = dynamic;

      t.setCloned();
      return t;
   }


   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
   }


   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   //==========================================================================
   //  Public methods
   //==========================================================================

   /**
    *
    */
   void setEnabled(boolean enabled)
   {
      try
      {
         modifyingMutableAttribute("ENABLED");
         this.enabled = enabled;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }
   
   void setRemoved(boolean removed)
   {
      try
      {
         modifyingMutableAttribute("REMOVED");
         this.removed = removed; 
      }
      catch(Throwable e)
      {
         error(e);
      }     
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

   boolean checkEnabled()
   {
      return this.enabled;
   }
   
   boolean isRemoved()
   {
      return removed;
   }

   String getId()
   {
      return id;
   }

   boolean isDynamic()
   {
      return dynamic;
   }
   
   /**
    * Return the visible label of the tab.
    */
   String getLabel()
   {
      return label;
   }

   /**
    * Return the url of the tab.
    */
   String getUrl()
   {
      return url;
   }
}

