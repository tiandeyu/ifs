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
 *  File        : PageProperties.java
 *    Dushmantha D  2008/09/24   Created.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/10/15 dusdlk Bug 77769, Updated Run() method and added a adjust() method.
 * 2008/10/10 dusdlk Bug 77095, Updated generateCustomBox(AutoString out) finction.
 * ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class PageProperties extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.PageProperties");

   private static final int BLOCKLAYOUT_TAB  = 1;   
   private static final int TABSETTINGS_TAB  = 2;

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter h;
   private ASPContext ctx;
   private ASPPage profilePage;

   private ASPBlock blk;
   private ASPCommandBar bar;   
   private ASPTabContainer tabs;
   private ASPBlockLayout lay;
   private ASPTable tbl;   
   
   //BLOCK LAYOUT TAB
   private ASPBlock blklayout;
   private ASPRowSet blkset;
   private ASPTable blktbl;
   private ASPCommandBar blkbar;   
   private ASPBlockLayout blklay;
   
   //TABSETTINGS TAB
   private ASPBlock tabsblk;
   private ASPRowSet tabsset;
   private ASPCommandBar tabsbar;
   private ASPTable tabstbl;
   private ASPBlockLayout tabslay;
   
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String url;
   private String name;
   private String title;
   private String frozen_tabs;

   //===============================================================
   // Construction
   //===============================================================
   public PageProperties(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      mgr.setPageExpiring();
      ctx   = mgr.getASPContext();    
      url   = ctx.readValue("URL", mgr.getQueryStringValue("URL"));
      profilePage = mgr.getProfilePage(url);      

      frozen_tabs = ctx.readValue("FROZEN_TABS", mgr.getQueryStringValue("FROZENTABS"));

      if( mgr.commandBarActivated() )
      {
        eval(mgr.commandBarFunction());
        
        int activetab = tabs.getActiveTab();

        if (activetab == BLOCKLAYOUT_TAB)
            blkset.store(); //store changes in current tab 
      }      
      else if( mgr.commandLinkActivated())
      {
         int activetab = tabs.getActiveTab();         
         if(activetab == TABSETTINGS_TAB)
            storeTabset();                  
         eval(mgr.commandLinkFunction());         
      }
      
      adjust();
      
      ctx.writeValue("URL",  url);      
      ctx.writeValue("FROZEN_TABS",frozen_tabs);
      tabs.saveActiveTab();
   }
  
   public void adjust()
   {
      if(!profilePage.hasBlockLayouts())
         tabs.setTabEnabled(BLOCKLAYOUT_TAB, false);
      else if(!profilePage.hasTabContainers())
         tabs.setTabEnabled(TABSETTINGS_TAB, false);
      int c = tabs.getActiveTab();
      if(c==BLOCKLAYOUT_TAB && blkset.countRows()==0)
         createBlockLayoutset();
      if(c==TABSETTINGS_TAB && tabsset.countRows()==0)
         createTabset();
   }
  
  /*
   * Populate the rowset tabsset with the updated values from the page
   * @Populate the rowset with user updated values.
   */
   private void storeTabset()
   {
      ASPManager mgr = getASPManager();
      tabsset.clear();     
      Vector tabvcontainers = profilePage.getAllTabContainer(); 
      Iterator itr = tabvcontainers.iterator();  
      int tabcontainer_count = 1;
      
      while(itr.hasNext())
      {
         ASPTabContainer tabcontainer = (ASPTabContainer)itr.next();
                         
         String visibleTabs;
      
         visibleTabs = mgr.readValue(tabcontainer.getName());
          
         String tabcontainername = "";         
         String tabid = "";
         String tabvisible = "";
         String tabname="";
         int tabpos = 0;
         
         if(visibleTabs==null)
            continue;
         
         StringTokenizer st = new StringTokenizer(visibleTabs,"~");
         StringTokenizer tab_values;
            
         while(st.hasMoreTokens())
         {        
            ASPBuffer buf = mgr.newASPBuffer();
            String hval = st.nextToken();
            tab_values = new StringTokenizer(hval,"^");
 
            tabcontainername = tab_values.nextToken();
            tabid = tab_values.nextToken();
            tabvisible = tab_values.nextToken();
            tabname = tabcontainer.getTabLabelbyId(tabid);
            
            buf.addItem("TAB_CONTAINER_ID",tabcontainer_count+"");
            buf.addItem("TAB_CONTAINER_NAME",tabcontainername);
            buf.addItem("TAB_ID",tabid);
            buf.addItem("TAB_NAME",tabname);
            buf.addItem("ISTABVISIBLE",tabvisible);
            
            if(tabvisible.equals("Y"))
            {
               buf.addItem("TAB_POSITION",tabpos+"");            
               tabpos++;
            }
            else
            {
               buf.addItem("TAB_POSITION","-1");                           
            }
            
            if(isObjectAccessible(profilePage.getComponent().toUpperCase()+"/"+profilePage.getPageName()+".page#"+buf.getValue("TAB_ID")))
               tabsset.addRow(buf); 
         }
         tabcontainer_count++;
      }    
   }
   
   /*
    * Populate the rowset tabsset with updated values from the profile.
    * @Populate rowset with profile values.
    */
   private void createTabset()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = null;
      tabsset.clear(); 
      
      SortedMap sortedtabs; //used to hold the sorted tabs order buffer items
         
      Vector tabvcontainers = profilePage.getAllTabContainer(); 
      Iterator itr = tabvcontainers.iterator();
      int tabcontainer_count = 1;
      int hiddentabpos = -1;
      
      while(itr.hasNext())
      {        
         ASPTabContainer tabcontainer = (ASPTabContainer)itr.next();
         ASPBuffer profile = tabcontainer.getProfile();         
         int tabcount = tabcontainer.getTabCount();
                  
         sortedtabs = new TreeMap();
         
         for(int i=0;i<tabcount;i++)         
         {
            if(profile.getBufferAt(i)==null) continue;
            
            buf = profile.getBufferAt(i);
            
            int pos = Integer.parseInt(buf.getValue("TAB_POSITION"));
            
            buf.addItem("TAB_CONTAINER_ID",tabcontainer_count+"");
            
            if(isObjectAccessible(profilePage.getComponent().toUpperCase()+"/"+profilePage.getPageName()+".page#"+buf.getValue("TAB_ID")))
            {
               if(pos<0)
               {
                  sortedtabs.put(hiddentabpos+"",buf); //sort by the position 
                  hiddentabpos+=-1;
               }
               else
               {
                  sortedtabs.put(pos+"",buf); //sort by the position                   
               }
            }
         }
         
         Iterator sortedItr = sortedtabs.values().iterator();
         
         while(sortedItr.hasNext())
         {
            tabsset.addRow((ASPBuffer)sortedItr.next());
         }         
         tabcontainer_count++;
      }      
      tabsset.first();       
   } 
          
   /*
    * Populate the rowset blkset with values from profile.
    * @Populate rowset with updated values from profile.
    */
   private void createBlockLayoutset()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = null;
      blkset.clear();       
      Vector allblocks = profilePage.getASPBlocks();    
      Iterator itr = allblocks.iterator();
      
      while(itr.hasNext())
      {
         ASPBlock aspblock = (ASPBlock)itr.next();         
         ASPBlockLayout blocklayout = aspblock.getASPBlockLayout();
         
         if(blocklayout!=null && aspblock.getASPBlockLayout().isLayoutRemovable())
         {
            buf = getASPManager().newASPBuffer();
            buf.addFieldItem("BLOCK_NAME",aspblock.getName());
            buf.addFieldItem("ISBLOCKVISIBLE",aspblock.getASPBlockLayout().isVisibleByUserProfile()?"Y":"N");         
            blkset.addRow(buf);
         }
      }      
      blkset.first();       
   } 
   
   private ASPBuffer getTabBuffer(int tab_group)
   {
      boolean add = false;
      ASPBuffer b = getASPManager().newASPBuffer();
      tabsset.first();
      
      for(int i=0; i<tabsset.countRows(); i++)
      {
         String group = tabsset.getValue("TAB_CONTAINER_ID");
         if(getASPManager().isEmpty(group)) 
            add = true;
         else if(group.equals(Integer.toString(tab_group)))
            add = true;
         if(add)
            b.addBuffer("Tab"+i, tabsset.getRow());
         tabsset.forward(1);
         add = false;
      }
      return b;
   }        
      
 //==========================================================
 // Tab functions
 //==========================================================

   /*
    * Activate tabsettings tab and populate the rowset with data of active tab.
    * @Activate Tabsettings tab and populate rowset with profile values.
    */
   public void activateTabSettings()
   {   
      tabs.setActiveTab(TABSETTINGS_TAB);
      createTabset();
   }

   /*
    * Activate blocklayout tab and populate the rowset with data of active tab.
    * @Activate blocklayout tab and populate the rowset.
    */
   public void activateBlockLayoutSettings()
   {   
      tabs.setActiveTab(BLOCKLAYOUT_TAB);
      createBlockLayoutset();
   }
   
   /*
    * Called when Apply buttons are clicked.
    */
   public void submit()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer profile = null;
      int activetab = tabs.getActiveTab();

       if (activetab == BLOCKLAYOUT_TAB) 
        {   
            blkset.store(); //store changes in current tab
            setBlockLayoutProfileProperties(false);
        }
       else if (activetab == TABSETTINGS_TAB)  
        {     
            //TABSETTINGS TAB
            Vector tabcontainers = profilePage.getAllTabContainer();

            for(int i=0;i<tabcontainers.size();i++)
            {
               ASPTabContainer c = (ASPTabContainer) tabcontainers.elementAt(i);
               profile = getTabBuffer(i+1);
               c.updateProfileBuffer(profile);
            }
        }            
      try
      {
         ASPProfile prf = profilePage.getASPProfile();
         prf.save(this);     

         if (activetab == BLOCKLAYOUT_TAB) 
         {
            blkset.clear();
            createBlockLayoutset();
         }  
      }
      catch (Exception e)
      {
         error(e);
      }
   }

   /*
    * Called when Apply and Close button is clicked.
    */
   public void  submitclose()
   {
     ASPManager mgr = getASPManager();
 
     submit();

     mgr.responseWrite("<script language=JavaScript>window.close();</script>");
     mgr.endResponse();
   }
   
   /*
    * Called when reset profile button is clicked.
    * remove the tabcontainers folders in the profile if called from tabsettings tab.
    * remove the blocklayout folders if called from blocklayout settings tab.
    */   
   public void  clearprofile()
   {
      try
      {         
         ASPManager mgr = getASPManager();         
         int activetab = tabs.getActiveTab();
         
         if (activetab == BLOCKLAYOUT_TAB) 
         {
            setBlockLayoutProfileProperties(true);               
         }
         else if (activetab == TABSETTINGS_TAB)  
         {
            Vector tabvcontainers = profilePage.getAllTabContainer();
            Iterator itr = tabvcontainers.iterator();

            while(itr.hasNext())
            {
               ASPTabContainer tabcontainer = (ASPTabContainer)itr.next();
               tabcontainer.removeFromProfileBuffer();               
            }
         }    
         ASPProfile prf = profilePage.getASPProfile();
         prf.save(this);  
         blkset.clear();
         tabsset.clear();            
         createBlockLayoutset();
         createTabset(); 
      }
      catch (Exception e)
      {
         error(e);
      }
   }

   /*
    * Updates the profile buffer with default values when re-setted else with the values of the rowset.
    * The parameter "reset" corresponds to a boolean value to reset the profile buffer or to update
    * blockvisible from value from page.
    */
   public void setBlockLayoutProfileProperties(boolean reset)
   {
      Vector allblocks = profilePage.getASPBlocks();
      int x = 0; //count for the rowset

      for(int i=0;i<allblocks.size();i++)
      {
         ASPBlock block = (ASPBlock)allblocks.elementAt(i);
         ASPBlockLayout blklayout = block.getASPBlockLayout();
         
         if(blklayout==null || !blklayout.isLayoutRemovable())
            continue;
         else
         {
            ASPBuffer buf = getASPManager().newASPBuffer();
            if(reset)
               buf.addItem("BlockVisible","Y");
            else
               buf.addItem("BlockVisible",blkset.getValueAt(x,"ISBLOCKVISIBLE"));
            x++;
            blklayout.updateProfileBuffer(buf, true);         
         }       
      }      
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      disableConfiguration();
      disableNavigate();
      disableValidation();
      disableFooter();
      disableOptions();
      
      blk = mgr.newASPBlock("MAIN");
      blk.addField("NAME").
          setHidden();

      bar = mgr.newASPCommandBar(blk);
      bar.disableMinimize();
      bar.addCustomCommand("activateBlockLayoutSettings","Block Layout Settings");             
      bar.addCustomCommand("activateTabSettings","Tab Settings");      

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      
      tbl = mgr.newASPTable( blk );
      tbl.disableRowStatus();
      tbl.disableRowSelect();
      tbl.disableQueryRow();
      tbl.setEditable();
      tbl.unsetSortable();

      String script_loc = getASPConfig().getScriptsLocation();
      includeJavaScriptFile(script_loc.substring(getASPConfig().getApplicationContext().length()) + "PagePropertiesUtility.js");

      tabs = newASPTabContainer("TAB_SETTINGS_CONTAINER");
      tabs.setDirtyFlagEnabled(false);

      //if changing tab order change setTabEnabled as appropriate.
      tabs.addTab(mgr.translate("Block Layout Settings","FNDPAGESETTINGSBLOCKLAYOUT: Block Layout Settings"),"javascript: commandSet('MAIN.activateBlockLayoutSettings','')");            
      tabs.addTab(mgr.translate("Tab Settings","FNDPAGESETTINGSTAB: Tab Settings"),"javascript: commandSet('MAIN.activateTabSettings','')");      

      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

      bar.removeCustomCommand("activateBlockLayoutSettings");
      bar.removeCustomCommand("activateTabSettings"); 

      //BLOCKLAYOUTSETTINGS TAB
      blklayout = mgr.newASPBlock("BLOCKLAYOUTSETTINGS_BLK");

      blklayout.addField("BLOCK_NAME").
          setLabel("FNDPAGESPAGEPROPBLOCKNAME: Block Name").
          setSize(50).
          setReadOnly();

      blklayout.addField("ISBLOCKVISIBLE").
          setLabel("FNDPAGESPAGEPROPBLOCKREMOVED: Visible").
          setCheckBox("N,Y");
    
      blklay = blklayout.getASPBlockLayout();
      blklay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      blkset = blklayout.getASPRowSet();
      
      blkbar = mgr.newASPCommandBar(blklayout);
      blkbar.disableMinimize();
      blkbar.disableCommand(ASPCommandBar.FIND);
      blkbar.disableCommand(ASPCommandBar.PROPERTIES);

      blktbl = mgr.newASPTable( blklayout );
      blktbl.setEditable();
      blktbl.disableRowStatus();
      blktbl.disableRowSelect();
      blktbl.disableQueryRow();  
           
       //TABSETTINGS TAB
      tabsblk = mgr.newASPBlock("TABSETTINGS_BLK");

      tabsblk.addField("TAB_CONTAINER_ID").
      setLabel("FNDPAGESPAGEPROPTABCONTAINERID: Tab Group").
      setSize(5).
      setReadOnly();            
      
      tabsblk.addField("TAB_ID").
            setHidden();
      
      tabsblk.addField("TAB_POSITION").
            setHidden();      

      tabsblk.addField("TAB_NAME").
          setLabel("FNDPAGESPAGEPROPTABNAME: Tab Name").
          setSize(50).
          setReadOnly();

      tabsblk.addField("ISTABVISIBLE").
          setLabel("FNDPAGESPAGEPROPTABREMOVED: Visible").
          setCheckBox("N,Y");

      tabsblk.addField("TAB_CONTAINER_NAME").
      setHidden();
     
      tabslay = tabsblk.getASPBlockLayout();
      tabslay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      tabsset = tabsblk.getASPRowSet();
      
      tabsbar = mgr.newASPCommandBar(tabsblk);
      tabsbar.disableMinimize();
      tabsbar.disableCommand(ASPCommandBar.FIND);
      tabsbar.disableCommand(ASPCommandBar.PROPERTIES);

      tabstbl = mgr.newASPTable( tabsblk );
      tabstbl.setEditable();
      tabstbl.disableRowStatus();
      tabstbl.disableRowSelect();
      tabstbl.disableQueryRow();
   }

   protected String getDescription()
   {
      return "FNDPAGESPAGEPROPDESC: Page Properties for '" + title + "'";
   }

   protected String getTitle()
   {
      return "FNDPAGESPAGEPROPTITLE: Page Properties";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();

      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDPAGESPAGEPROPTITLE: Page Properties"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");

      out.append(mgr.startPresentation(mgr.translate("FNDPAGESPAGEPROPTITLE1: Page Properties")));

      out.append(tabs.showTabsInit());

      if (tabs.getActiveTab() == BLOCKLAYOUT_TAB)
      {               
         beginDataPresentation();
         drawSimpleCommandBar("");
         endDataPresentation(false);

         out.append("<table>");
          
         if(blkset.countRows()>1)
         {
            out.append("<tr><td "+(mgr.isRTL()?"align=left":"align=right")+">");
            printLink("FNDPAGESPAGEPROPSELALL: Select All","javascript:selectAll('ISBLOCKVISIBLE')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESPAGEPROPDESELALL: Deselect All","javascript:deselectAll('ISBLOCKVISIBLE')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESPAGEPROPTOGGLE: Toggle Selection","javascript:invertselection('ISBLOCKVISIBLE')");
            out.append("</td></tr>");            
         }
         
         out.append("<tr><td>");
         
         printNewLine();
         out.append(blktbl.populate());            
 
         out.append("</td></tr></table>");
      }
      
      else if (tabs.getActiveTab() == TABSETTINGS_TAB)
      { 
          beginDataPresentation();         
          drawSimpleCommandBar("");

          out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
          out.append("<tbody><tr><td>\n");
          out.append("<tr>\n");
          out.append("<td height=100%>&nbsp;&nbsp;&nbsp;</td>");
          out.append("<td style=\"padding-right:10px; padding-bottom:10px;\" valign=top>\n");

          generateCustomBox(out);

          out.append("</td></tr></table>\n");
          out.append("<table>\n");
          out.append("<TBODY></TBODY>\n");

          endDataPresentation(false);         
      }

      endDataPresentation(false);

      out.append(tabs.showTabsFinish());

      out.append("              <table cellspacing=5 border=0>\n");
      out.append("              <tr><td></td><td colspan=4 >\n");
      printText("FNDPAGESPAGEPROPREFRESH: Changes will NOT take effect until the underlying page is refreshed.");
      out.append("              </td></tr>\n");
      out.append("              <tr>\n");
      out.append("                  <td>\n");
      out.append("                  </td>\n");

      if(tabs.getActiveTab() == TABSETTINGS_TAB)
      {
         out.append("              <tr><td></td><td colspan=4 >\n");
         printText("FNDPAGESPAGEPROPACTIVETABINFO: * Hiding active tabs require a page reload from the navigator.");      
         out.append("              </td></tr>\n");
         out.append("              <tr>\n");
         out.append("                  <td>\n");
         out.append("                  </td>\n");     
      }
      
      out.append("                  <td>\n");
      String tag = " class='button'";
      
      if(tabs.getActiveTab() == BLOCKLAYOUT_TAB)
         printButton("APPLY","FNDPAGESPAGEPROPAPPLY: Apply","onClick=\"javascript: commandClick('submit')\" "+tag);
      else
         printButton("APPLY","FNDPAGESPAGEPROPAPPLY: Apply","onClick=\"javascript: updateHiddenFields(); commandClick('submit')\" "+tag);         
      
      out.append("                  </td>\n");
      out.append("                  <td>\n");
      
      if(tabs.getActiveTab() == BLOCKLAYOUT_TAB)      
         printButton("APPLYCLOSE","FNDPAGESPAGEPROPAPPLYCLOSE: Apply & Close","onClick=\"javascript: commandClick('submitclose')\" "+tag);
      else
         printButton("APPLYCLOSE","FNDPAGESPAGEPROPAPPLYCLOSE: Apply & Close","onClick=\"javascript: updateHiddenFields(); commandClick('submitclose')\" "+tag);
      
      out.append("                  </td>\n");
      out.append("                  <td>\n");
      printButton("GETDEFAULT","FNDPAGESPAGEPROPRESETPROFILE: Reset Profile","onClick=\"javascript:{if (confirm('" + mgr.translateJavaScript("FNDPAGESCSLPROFILERESETWARNING: The profile for this page will be reset. Your current page configurations will be lost.") + "')) commandClick('clearprofile');}\" "+tag);
      out.append("                  </td>\n");
      out.append("                  <td>\n");
      printButton("CLOSE","FNDPAGESPAGEPROPCLOSE: Close","onClick=\"window.close()\"");
      out.append("                  </td>\n");
      out.append("              </tr>\n");    
      out.append("              </table>\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
   
   private void generateCustomBox(AutoString out) throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString temp = new AutoString();
      temp.clear();
      String imgloc = mgr.getASPConfig().getImagesLocation();
      String imglocRTL = mgr.getASPConfig().getImagesLocationWithRTL();
      out.append("<BR>");
      out.append("<p>");
      out.append("<UL>");
      out.append("<Li>");
      printText("FNDPAGEPROPERTIESLINE1: Use the left/right arrows to move a selected tab between the visible and non-visible lists.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGEPROPERTIESLINE2: Use the up/down arrows to set the order of the visible tabs.");
      out.append("</Li>\n");
      out.append("</UL>");
      out.append("</p>");

      out.append("<table border=0><tr>\n");
      out.append("<td></td>");
      out.append("<td>");
      printWriteLabel("FNDPAGEPROPERTIESVISIBLELIST: Visible Tab List");
      out.append("</td>");
      out.append("<td></td>");
      out.append("<td>");
      printWriteLabel("FNDPAGEPROPERTIESINVISIBLE: Non-visible Tab List");
      out.append("</td>");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td valign=top>\n");
      out.append("<a href=\"javascript:moveTabItemUp(f.visible_tabs_)\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_up.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVU: Move selected tab up"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVU: Move selected tab up"),"\"");
      out.append("></a><br><br>\n");
      out.append("<a href=\"javascript:moveTabItemDown(f.visible_tabs_)\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_down.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVD: Moves selected tab down"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVD: Moves selected tab down"),"\"");
      out.append("></a>\n</td>\n");

      out.append("<td>\n<select name=visible_tabs_");
      out.append(" size=10 class='selectbox' id='visible_tabs_'>\n");

      Vector visible_tabs = new Vector();
      Vector visible_tabs_values = new Vector();      
      
      Vector invisible_tabs = new Vector();
      Vector invisible_tabs_values = new Vector();
      
      String tab_values = "";
      
      String tab_container_name = "";
      String temp_tab_container_name = "";
      String tab_id = "";
      String tab_name = "";
      String tab_visible = "";
            
      Vector frozen_tabs_vector = new Vector();

      if(frozen_tabs!=null)
      {
         String[] frozen_tabs_arr = frozen_tabs.split(",");

         for(int x=0;x<frozen_tabs_arr.length;x++)
         {
            frozen_tabs_vector.add(frozen_tabs_arr[x]);
         }
      }      
            
      for(int i=0; i<tabsset.countRows(); i++)
      {       
         tab_container_name = tabsset.getValueAt(i,"TAB_CONTAINER_NAME");
         tab_id = tabsset.getValueAt(i,"TAB_ID"); 
         tab_name = tabsset.getValueAt(i,"TAB_NAME"); 
         tab_visible = tabsset.getValueAt(i,"ISTABVISIBLE");
              
         ASPTabContainer tabcontainer = profilePage.getASPTabContainer(tab_container_name);
         
         if(tabcontainer.isActiveTab(tab_id))
            {
               tab_name = tab_name + "*"; //add * if tab is the current active tab
            }                 

         String frtabtemp = tab_container_name+"^"+tab_id;
         
         if(frozen_tabs_vector!=null && frozen_tabs_vector.contains(frtabtemp))
            tab_values = ""+tab_container_name+"^"+tab_id+"^"+tab_visible+"^"+"#";  //tab is frozen              
         else
            tab_values = ""+tab_container_name+"^"+tab_id+"^"+tab_visible;
                 
         if(tab_container_name.equals(temp_tab_container_name))
         {
           if ("Y".equals(tab_visible))
            {
               visible_tabs.add(tab_name);
               visible_tabs_values.add(tab_values);
            }
            else
            {
               invisible_tabs.add(tab_name);   
               invisible_tabs_values.add(tab_values);  
            }              
         }
         else
         {
            visible_tabs.add("------"+tab_container_name);                    
            invisible_tabs.add("------"+tab_container_name); 

            //print hidden field for each tab container for visible tabs values
            temp.append("<input type=\"hidden\" id=\""+tab_container_name+"\" name=\""+tab_container_name+"\" value=\"\">\n");                                 
                       
            if ("Y".equals(tab_visible))
            {
               visible_tabs.add(tab_name);
               visible_tabs_values.add(tab_values); 
            }
            else
            {
               invisible_tabs.add(tab_name); 
               invisible_tabs_values.add(tab_values);                 
            }                
         }         
         temp_tab_container_name = tab_container_name;
      }
        
      int j=0;
     
      for (int i=0; i<visible_tabs.size(); i++)
      {
         if(i==0)
            out.append("<option selected value=");
         else
            out.append("<option value=");

         String tmp = visible_tabs.elementAt(i).toString(); //gets the tabcontainer id/tabid
         
         if(tmp.contains("------"))
         {
            out.append(""+tmp.substring(6)+"*");

            String tmp2 = tmp.substring(6);
            
            if(tmp2.startsWith("__"))
               out.append(">",""+"------"+tmp.substring(8),"</option>\n"); //remove the two underscores "__"
            else
            out.append(">",""+tmp,"</option>\n");
         }
         else
         {
            out.append(""+visible_tabs_values.elementAt(j));
            out.append(">",""+tmp,"</option>\n");  
            j++;
         }
      }
      
      out.append("<option>____________________</option>");
      out.append("</select>\n</td>");

      out.append("<td nowrap "+(mgr.isRTL()?"align=right":"align=left")+" valign=center>\n");

      out.append("<a href=\"javascript:moveItemToSide(f.visible_tabs_,f.invisible_tabs_,'N')\">");
      out.append("<img border=0 src=\"",imglocRTL,"portlet_right.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVR: Move selected field right"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVR: Move selected field right"),"\"");
      out.append("></a>\n");

      out.append("<BR><BR>\n");

      out.append("<a href=\"javascript:moveItemToSide(f.invisible_tabs_,f.visible_tabs_,'Y')\">");
      out.append("<img border=0 src=\"",imglocRTL,"portlet_left.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVL: Move selected field left"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGEPROPERTIESMOVL: Move selected field left"),"\"");
      out.append("></a>\n</td>\n");

      out.append("<td>\n<select name=invisible_tabs_");
      out.append(" size=10 class='selectbox' id = 'invisible_tabs_'>\n");

      j = 0;
      
      for (int i=0; i<invisible_tabs.size(); i++)
      {
         out.append("<option value=");

         String tmp = invisible_tabs.elementAt(i).toString();
         
         if(tmp.contains("------"))
         {
            out.append(""+tmp.substring(6)+"*");

            String tmp2 = tmp.substring(6);
            
            if(tmp2.startsWith("__"))
               out.append(">",""+"------"+tmp.substring(8),"</option>\n"); //remove the two underscores "__"
            else
            out.append(">",""+tmp,"</option>\n");
         }
         else
         {
            out.append(""+invisible_tabs_values.elementAt(j));
            out.append(">",""+tmp,"</option>\n");          
            j++;
         }
      }
      out.append("<option>____________________</option>");
      out.append("</select>\n</td>");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td></td>\n");
      out.append("</tr>\n");
      out.append("</table>\n");
      out.append(temp);
   }   
}