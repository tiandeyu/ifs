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
 *  File        : ConfigurableScreenLayout.java
 *    Ramila H  17/05/2005   Created.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/03/16 sumelk Bug 88158, Changed clearprofile() and submit() to save the profile sections separately. 
 * 2009/02/13 buhilk - Bug id 80265, F1PR454 - Templates IID.
 * 2008/07/15 sadhlk Bug 74364, Modified getContents() changed the javascript method called in Apply
 *                              and Apply & Close Buttons to checkMandatoryNonVisible().
 * 2007/01/30 buhilk Bug 63250, Improved theming support in IFS clients.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * Revision 1.3  2006/12/21         buhilk
 * Modified adjust(), createMrsetRows(), run(), submit(), submitclose() and clearprofile()
 * to hide overview layout tab if no ASPTable is found
 * and changed functionality appropietly.
 *               2006/02/22 prralk
 * B133922 fixed default value display for select boxes.
 * Revision      2006/01/27 rahelk
 * Call 131790 corrected. Enabled selection links ONLY when rows > 1
 *
 *               2006/01/25           mapelk
 * Removed errorneous translation keys
 *
 * Revision 1.2  2005/11/15 11:30:56  japase
 * ASPProfile.save() takes 'this' as argument.
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.22  2005/08/19 05:06:37  rahelk
 * Changed warning msg for reset profile
 *
 * Revision 1.21  2005/08/18 11:09:36  mapelk
 * Added warning message before deleting page profile and also change name to Reset Profile.
 *
 * Revision 1.20  2005/08/18 05:17:53  sumelk
 * Merged the corrections for Bug 52503. Changed generateMultirowCustomBox() & generateCustomBox().
 *
 * Revision 1.19  2005/08/17 08:37:54  mapelk
 * Bug fixed: Array out of bound exception when READONLY tab has no PO security
 *
 * Revision 1.18  2005/08/08 09:44:06  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.17  2005/07/18 12:15:44  mapelk
 * More CSL bug fixes
 *
 * Revision 1.16  2005/07/12 10:54:41  mapelk
 * CLS Bug fix
 *
 * Revision 1.15  2005/07/12 04:26:05  rahelk
 * CSL: implemented security check for tabs
 *
 * Revision 1.14  2005/07/11 12:50:39  rahelk
 * CSL: Changed tab order
 *
 * Revision 1.13  2005/07/07 10:40:59  rahelk
 * CSL: Corrected profile_page locking problem when getting profile elements from CSL page
 *
 * Revision 1.12  2005/07/07 08:09:22  rahelk
 * CSL: Corrected profile_page locking problem when getting profile elements from CSL page
 *
 * Revision 1.11  2005/07/06 11:05:31  rahelk
 * CSL: added get defaults functionality to "defaults" tab
 *
 * Revision 1.10  2005/07/04 09:15:58  rahelk
 * CSL: corrected bug with group field list order
 *
 * Revision 1.9  2005/07/01 15:25:12  rahelk
 * CSL: set size and dataSpan for field in detail mode
 *
 * Revision 1.8  2005/06/23 08:56:30  rahelk
 * CSL 2: disabled default values tab when New not available
 *
 * Revision 1.7  2005/06/16 10:38:47  rahelk
 * CSL 2: survive profile setting when new items are added to the page
 *
 * Revision 1.6  2005/06/16 05:20:01  mapelk
 * Remove (instead of disabling) CLS tabs unless have PO Security
 *
 * Revision 1.5  2005/06/15 11:15:04  rahelk
 * CSL 2: bug fix: default values
 *
 * Revision 1.4  2005/06/15 10:19:03  mapelk
 * introduced PO Securities to enable/disable CSL tabs
 *
 * Revision 1.3  2005/06/06 07:22:28  rahelk
 * Save all profile objects together from CSL page
 *
 * Revision 1.2  2005/05/18 10:50:52  rahelk
 * Added support for setting profile height for a field
 *
 * Revision 1.1  2005/05/17 10:46:52  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.asp.ASPBlockLayout.group;

import java.util.*;

public class ConfigurableScreenLayout extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ConfigurableScreenLayout");

   private static final int LAYOUT_TAB       = 1;
   private static final int MULTIROW_TAB     = 2;
   private static final int COMMANDBAR_TAB   = 3;
   private static final int DEFAULTVALUE_TAB = 4;
   private static final int MANDATORY_TAB    = 5;
   private static final int QUERY_TAB        = 6;
   private static final int READONLY_TAB     = 7;

   
   private static final int DEFAULT_TEMPLATE_VIEW  = 0;
   private static final int VIEW_TEMPLATE          = 1;
   private static final int NEW_TEMPLATE           = 2;
   private static final int EDIT_TEMPLATE          = 3;
   private static final int RENAME_TEMPLATE        = 4;

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   //private ASPForm frm;
   private ASPHTMLFormatter h;
   private ASPContext ctx;
   private ASPPage profilePage;
   private ASPBlockLayout blockLayout; //target;
   //private ASPCommandBar commandBar;
   //private ASPField flab;
   //private ASPField fmand;
   private ASPCommandBar bar;
   private ASPTabContainer tabs;
   //private int activetab;

   //QUERY TAB
   private ASPBlock qryblk;
   private ASPRowSet qryset;
   private ASPCommandBar qrybar;
   private ASPTable qrytbl;
   private ASPBlockLayout qrylay;

   //MANDATORY TAB
   private ASPBlock manblk;
   private ASPRowSet manset;
   private ASPTable mantbl;
   private ASPCommandBar manbar;
   private ASPBlockLayout manlay;

   //READONLY TAB
   private ASPBlock roblk;
   private ASPRowSet roset;
   private ASPTable rotbl;
   private ASPCommandBar robar;
   private ASPBlockLayout rolay;

   //LAYOUT TAB
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   //MULTIROW TAB
   private ASPBlock mrblk;
   private ASPRowSet mrset;
   private ASPTable mrtbl;
   private ASPBlockLayout mrlay;

   //COMMANDBAR TAB
   private ASPBlock cmdblk;
   private ASPRowSet cmdset;
   private ASPTable cmdtbl;
   //private ASPCommandBar cmdbar;
   private ASPBlockLayout cmdlay;

   //MULTIROW TAB
   private ASPBlock dvblk;
   private ASPRowSet dvset;
   private ASPTable dvtbl;
   private ASPBlockLayout dvlay;

   //Templates Tab
   private ASPBlock tmplblk;
   private ASPRowSet tmplset;
   private ASPTable tmpltbl;
   private ASPBlockLayout tmpllay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String url;
   private String name;
   private String title;
   //private ASPBuffer profile;
   private int i;
   private int rowno;
   private boolean sc;
   private boolean atleast_one_column;
   private boolean security_checked;
   private boolean show_overview_layout;
   private int template_status;

   //===============================================================
   // Construction
   //===============================================================
   public ConfigurableScreenLayout(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      mgr.setPageExpiring();
      ctx   = mgr.getASPContext();

      url   = ctx.readValue("URL", mgr.getQueryStringValue("URL"));
      name  = ctx.readValue("NAME", mgr.getQueryStringValue("OBJNAME"));
      title = ctx.readValue("TITLE");
      security_checked = ctx.readFlag("SEC_CHK",false);
      show_overview_layout = ctx.readFlag("SHOW_OVW_LAY", true);
      template_status = ctx.readNumber("TEMPLATE_STATUS",0);
      if (profilePage == null) profilePage = mgr.getProfilePage(url);

      if( mgr.commandBarActivated() )
      {
        store();  //store changes in current tab
        eval(mgr.commandBarFunction());
        //show();
      }
      else if( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());
      else if( !mgr.isEmpty(url) )
         show();
      else
         refresh();

      adjust();

      ctx.writeValue("URL",  url);
      ctx.writeValue("NAME", name);
      ctx.writeValue("TITLE",title);
      ctx.writeFlag("SEC_CHK",security_checked);
      ctx.writeFlag("SHOW_OVW_LAY",show_overview_layout);
      ctx.writeNumber("TEMPLATE_STATUS", template_status);
      tabs.saveActiveTab();
   }

   public void adjust()
   {
      //tab idex starts from 1
      boolean[] enabled_tabs = {false,true,true,true,true,true,true,true};

      //disable menu item tab when no custom commands exist.
      if (!isObjectAccessible("FND/ConfigurableScreenLayout#QUERY"))
      {
         tabs.setTabRemoved(QUERY_TAB,true);
         enabled_tabs[QUERY_TAB] = false;
      }

      if (!isObjectAccessible("FND/ConfigurableScreenLayout#MANDATORY"))
      {
         tabs.setTabRemoved(MANDATORY_TAB,true);
         enabled_tabs[MANDATORY_TAB] = false;
      }

      if (!isObjectAccessible("FND/ConfigurableScreenLayout#READONLY"))
      {
         tabs.setTabRemoved(READONLY_TAB,true);
         enabled_tabs[READONLY_TAB] = false;
      }

      if (!isObjectAccessible("FND/ConfigurableScreenLayout#DETAIL"))
      {
         tabs.setTabRemoved(LAYOUT_TAB,true);
         enabled_tabs[LAYOUT_TAB] = false;
      }

      if (!show_overview_layout || !isObjectAccessible("FND/ConfigurableScreenLayout#OVERVIEW"))
      {
         tabs.setTabRemoved(MULTIROW_TAB,true);
         enabled_tabs[MULTIROW_TAB] = false;
      }

      if (!isObjectAccessible("FND/ConfigurableScreenLayout#MENU") || cmdset.countRows() == 0)
      {
         tabs.setTabRemoved(COMMANDBAR_TAB,true);
         enabled_tabs[COMMANDBAR_TAB] = false;
      }

      ASPManager mgr = getASPManager();
      ASPCommandBar commandBar = null;

      commandBar = mgr.getASPCommandBarFromPage(profilePage,name);

      if (!isObjectAccessible("FND/ConfigurableScreenLayout#DEFAULTVALUES") || !commandBar.IsNewEnabled())
      {
         tabs.setTabRemoved(DEFAULTVALUE_TAB,true);
         enabled_tabs[DEFAULTVALUE_TAB] = false;
      }


      if (!security_checked)
      {
         security_checked = true;

         //tab index starts from 1
         for (int i=1; i<enabled_tabs.length; i++)
         {
            if (enabled_tabs[i])
            {
               tabs.setActiveTab(i);
               return;
            }
         }
      }
   }

   private void show()
   {
      ASPManager mgr = getASPManager();

      name    = mgr.getQueryStringValue("OBJNAME");
      ///
      if (mgr.isEmpty(url))
       url   = ctx.readValue("URL");
      if (mgr.isEmpty(name))
       name  = ctx.readValue("NAME");
      ///
      profilePage = mgr.getProfilePage(url);
      blockLayout  = mgr.getASPBlockLayoutFromPage(profilePage,name);
      title   = blockLayout.getName();

      if (rowset.countRows()==0)
        createRows();
      if (tmplset.countRows()==0)
         createTemplateRows();
   }

  private void store()
  {
    int activetab = tabs.getActiveTab();

    if (activetab == QUERY_TAB)
      qryset.store();
    else if (activetab == MANDATORY_TAB)
      manset.store();
    else if (activetab == READONLY_TAB)
      roset.store();
    else if (activetab == COMMANDBAR_TAB)
      cmdset.store();
    else if (activetab == DEFAULTVALUE_TAB)
    {       
       tmplset.store();
       dvset.store();
    }
    else if (activetab == LAYOUT_TAB)
    {
      String all_field_details =  getASPManager().readValue("FIELD_DETAILS");
      StringTokenizer st = new StringTokenizer(all_field_details,""+(char)30);
      StringTokenizer field_value;
      int fld_order;
      String fld_size = "";
      String fld_height = "";
      String fld_visible = "";
      String fld_showlabel="";
      String fld_span = "";
      String fld_group = "";
      int rowno;
      while (st.hasMoreTokens())
      {
         field_value = new StringTokenizer(st.nextToken(),""+(char)31);
         rowno = Integer.valueOf(field_value.nextToken()).intValue();
         fld_order = Integer.valueOf(field_value.nextToken()).intValue();
         fld_size = field_value.nextToken();
         fld_height = field_value.nextToken();
         fld_visible = field_value.nextToken();
         fld_showlabel = field_value.nextToken();
         fld_span  = getSpan(field_value.nextToken());
         fld_group = field_value.nextToken();

         rowset.setValueAt(rowno,"SIZE",fld_size);
         rowset.setValueAt(rowno,"HEIGHT",fld_height);
         rowset.setValueAt(rowno,"VISIBLE",fld_visible);
         rowset.setValueAt(rowno,"SHOWLABEL",fld_showlabel);
         if( "Y".equals(fld_visible) )
            rowset.setNumberValueAt(rowno,"ORDER",fld_order);
         else
            rowset.setValueAt(rowno,"ORDER","");

         rowset.setValueAt(rowno,"SPAN",fld_span);
         //rowset.setValueAt(rowno,"GROUP",fld_group);
      }
    }
    else if (activetab == MULTIROW_TAB)
    {
      ASPManager mgr = getASPManager();
      String all_field_details = mgr.readValue("MULTIROW_FIELD_DETAILS");

      if (mgr.isEmpty(all_field_details)) return;

      StringTokenizer st = new StringTokenizer(all_field_details,""+(char)30);
      StringTokenizer field_value;
      int fld_order;
      String fld_size = "";
      String fld_visible = "";
      int rowno;

      while (st.hasMoreTokens())
      {
         field_value = new StringTokenizer(st.nextToken(),""+(char)31);
         rowno = Integer.valueOf(field_value.nextToken()).intValue();
         fld_order = Integer.valueOf(field_value.nextToken()).intValue();
         fld_size = field_value.nextToken();
         fld_visible = field_value.nextToken();

         mrset.setValueAt(rowno,"SIZE",fld_size);
         mrset.setValueAt(rowno,"VISIBLE",fld_visible);

         if( "Y".equals(fld_visible) )
            mrset.setNumberValueAt(rowno,"ORDER",fld_order);
         else
            mrset.setValueAt(rowno,"ORDER","");
      }
      mrset.sort("ORDER");
    }

  }

  private void  createRows()
  {
      ASPBuffer buf = null;
      ASPBuffer tmp = null;
      rowset.clear();
      qryset.clear();
      manset.clear();
      roset.clear();
      String name="";
      String label="";
      String visible="";
      String queryable="";
      String mandatory="";
      String readonly="";

      ASPBuffer profile = blockLayout.getProfile();
      int prf_cnt = profile.countItems();
      for(i=0; i<prf_cnt; i++)
      {
         buf = profile.getBufferAt(i);
         rowset.addRow(buf);

         visible = rowset.getValue("VISIBLE");
         if ("N".equals(visible))
           continue;  //don't add non visible fields to the queryable,readonly,mandatory tabs

         name = rowset.getValue("NAME");
         label = rowset.getValue("LABEL");
         queryable = rowset.getValue("QUERYABLE");
         mandatory = rowset.getValue("MANDATORY");
         readonly = rowset.getValue("READONLY");

         if ("Y".equals(rowset.getValue("SYS_QUERYABLE")))
         {
           //add to queryable rowset are setQueryable() in the code
           tmp = getASPManager().newASPBuffer();
           tmp.addFieldItem("QUERY_NAME", name);
           tmp.addFieldItem("QUERY_LABEL", label);
           tmp.addFieldItem("ISQUERYABLE", queryable);
           qryset.addRow(tmp);
         }

         //add to mandatory rowset fields that are NOT setMandatory() in the code
         if ("N".equals(rowset.getValue("SYS_MANDATORY")))
         {
           tmp = getASPManager().newASPBuffer();
           tmp.addFieldItem("MANDATORY_NAME", name);
           tmp.addFieldItem("MANDATORY_LABEL", label);
           tmp.addFieldItem("ISMANDATORY", mandatory);
           manset.addRow(tmp);
         }

         //add to readonly rowset fields that are NOT setReadOnly() in the code
         if ("N".equals(rowset.getValue("SYS_READONLY")))
         {
           tmp = getASPManager().newASPBuffer();
           tmp.addFieldItem("READONLY_NAME", name);
           tmp.addFieldItem("READONLY_LABEL", label);
           tmp.addFieldItem("ISREADONLY", readonly);
           roset.addRow(tmp);
         }
      }
      renumberOrder();
      rowset.first();
      qryset.first();
      manset.first();
      roset.first();

      createMrsetRows();   //TableProfile
      createCmdsetRows();  //CommandBarProfile
   }

   private void  renumberOrder()
   {
      rowno = 1;
      for(i=0; i<rowset.countRows(); i++)
      {
         if( "Y".equals(rowset.getValueAt ( i,"VISIBLE" )) )
         {
            rowset.setNumberValueAt(i,"ORDER",rowno);
            rowno++;
         }
         else
            rowset.setValueAt(i,"ORDER","");
      }
   }

   private void  createMrsetRows()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buf = null;

      ASPTable table = mgr.getASPTableFromPage(profilePage,name);
      if(table!=null)
      {
         ASPBuffer profile = table.getProfile();

         mrset.clear();

         for(int i=0; i<profile.countItems(); i++)
         {
            buf = profile.getBufferAt(i);
            mrset.addRow(buf);
         }
         renumberMultiOrder();
         mrset.first();
         show_overview_layout = true;
      }
      else
         show_overview_layout = false;;
   }

   private void  renumberMultiOrder()
   {
      rowno = 1;
      for(i=0; i<mrset.countRows(); i++)
      {
         if( "Y".equals(mrset.getValueAt ( i,"VISIBLE" )) )
         {
            mrset.setNumberValueAt(i,"ORDER",rowno);
            rowno++;
         }
         else
            mrset.setValueAt(i,"ORDER","");
      }
   }

   private void createCmdsetRows()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buf = null;

      ASPCommandBar commandBar = mgr.getASPCommandBarFromPage(profilePage,name);
      ASPBuffer profile = commandBar.getProfile();

      cmdset.clear();

      for(int i=0; i<profile.countItems(); i++)
      {
         buf = profile.getBufferAt(i);
         cmdset.addRow(buf);
      }
      //renumberOrder();
      cmdset.first();

   }

   private void createDvsetRows()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buf = null;
      ASPBuffer profile = null;

      ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
      String template_id = (tmpllay.isNewLayout())?"__NEW":tmplset.getValueAt(tmplset.getCurrentRowNo(),"TEMPLATE_ID");
      
      if(template_id.equals("__NEW"))
         profile = block.getNewTemplateDetail();
      else
         profile = block.getTemplateDetailProfile(template_id, true);

      dvset.clear();

      for(int i=0; i<profile.countItems(); i++)
      {
         buf = profile.getBufferAt(i);
         dvset.addRow(buf);
      }
      //renumberOrder();
      dvset.first();
   }

   private void createTemplateRows()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = null;
      ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
      ASPBuffer profile = block.getTemplateProfile();
      tmplset.clear();
      for(int i=0; i<profile.countItems(); i++)
      {
         buf = profile.getBufferAt(i);
         tmplset.addRow(buf);
      }
      tmplset.sort("TITLE");
      tmplset.first();
   }
 //==========================================================
 // Tab functions
 //==========================================================

   public void activateQuery()
   {
      tabs.setActiveTab(QUERY_TAB);
   }

   public void activateMandatory()
   {
      tabs.setActiveTab(MANDATORY_TAB);
   }

   public void activateReadOnly()
   {
      tabs.setActiveTab(READONLY_TAB);
   }

   public void activateLayout()
   {
      tabs.setActiveTab(LAYOUT_TAB);
   }

   public void activateMultirow()
   {
      tabs.setActiveTab(MULTIROW_TAB);
   }

   public void activateCommands()
   {
      tabs.setActiveTab(COMMANDBAR_TAB);
   }

   public void activateDefaultValues()
   {
      tabs.setActiveTab(DEFAULTVALUE_TAB);
   }

//=============================================================================
//  Button functions
//=============================================================================
   public void  refresh_()
   {
      url   = ctx.readValue("URL");
      name  = ctx.readValue("NAME");
      title = ctx.readValue("TITLE");

      rowset.changeRows();
      rowset.sort("ORDER");
      renumberOrder();
      rowset.sort("ORDER");
      rowset.first();
   }


   private void  refresh() //save the data to the main rowset
   {
      ASPManager mgr = getASPManager();
      store();

      //transfer the data from other tabs (query,mandatory,readonly) to the main rowset
      for (int i=0; i<rowset.countRows(); i++)
      {
         String name = rowset.getValueAt(i, "NAME");
         for (int j=0; j<qryset.countRows(); j++)
         {
             if (name.equals(qryset.getValueAt(j,"QUERY_NAME")))
             {
               rowset.setValueAt(i,"QUERYABLE",qryset.getValueAt(j,"ISQUERYABLE"));
               break;
             }
         }

         for (int j=0; j<manset.countRows(); j++)
         {
             if (name.equals(manset.getValueAt(j,"MANDATORY_NAME")))
             {
               rowset.setValueAt(i,"MANDATORY",manset.getValueAt(j,"ISMANDATORY"));
               break;
             }
         }

         for (int j=0; j<roset.countRows(); j++)
         {
             if (name.equals(roset.getValueAt(j,"READONLY_NAME")))
             {
               rowset.setValueAt(i,"READONLY", roset.getValueAt(j,"ISREADONLY"));
               break;
             }
         }
      }

      rowset.sort("ORDER");
   }

   public void submit()
   {
      try
      {
         ASPManager mgr = getASPManager();
         ASPBuffer profile = null;
         refresh();

         rowno = rowset.countRows();
         atleast_one_column=false;
         i=0;
         while ( (i<rowno) && (!atleast_one_column) )
         {
            atleast_one_column = "Y".equals(rowset.getValueAt( i,"VISIBLE"));
            i++;
         }

         profilePage = mgr.getProfilePage(url);
         ASPProfile prf = profilePage.getASPProfile();

         //Save command bar settings
         ASPCommandBar commandBar = mgr.getASPCommandBarFromPage(profilePage,name);
         profile = cmdset.getRows();
         commandBar.updateProfileBuffer(profile);
         prf.save(this);

         ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);

         //block layout settings
         if (atleast_one_column)
         {
            blockLayout  = mgr.getASPBlockLayoutFromPage(profilePage,name);
            profile = rowset.getRows();
            blockLayout.updateProfileBuffer(profile);
            prf.save(this);
         }
         else
            mgr.showAlert("FNDPAGESCONFIGSCRLAYOUTATLEASEONESINGLE: Atleast one column should be visible.");


         //save multirow settings
         if(show_overview_layout)
         {
            rowno = mrset.countRows();
            atleast_one_column=false;
            i=0;
            while ( (i<rowno) && (!atleast_one_column) )
            {
               atleast_one_column = "Y".equals(mrset.getValueAt( i,"VISIBLE"));
               i++;
            }

            if (atleast_one_column)
            {
               ASPTable table  = mgr.getASPTableFromPage(profilePage,name);
               profile = mrset.getRows();
               table.updateProfileBuffer(profile);
               prf.save(this);
            }
            else
               mgr.showAlert("FNDPAGESCONFIGSCRLAYOUTATLEASEONEMULTI: Atleast one table column should be visible.");
         }

         if (!sc)
         {
            rowset.clear();
            createRows();
         }
      }
      catch (Exception e)
      {
         error(e);
      }

   }

   public void  submitclose()
   {
      ASPManager mgr = getASPManager();

      sc = true;
      submit();

      sc = false;
      if (!show_overview_layout || atleast_one_column)
      {
         mgr.responseWrite("<script language=JavaScript>window.close();</script>");
         sc = true;
      }
   }

   public void  clearprofile()
   {
      try
      {
         ASPManager mgr = getASPManager();

         profilePage = mgr.getProfilePage(url);
         ASPProfile prf = profilePage.getASPProfile();

         blockLayout = mgr.getASPBlockLayoutFromPage(profilePage,name);
         blockLayout.removeFromProfileBuffer();
         prf.save(this);

         if(show_overview_layout)
         {
            ASPTable table = mgr.getASPTableFromPage(profilePage, name);
            table.removeFromProfileBuffer();
            prf.save(this);
         }

         ASPCommandBar commandbar = mgr.getASPCommandBarFromPage(profilePage, name);
         commandbar.removeFromProfileBuffer();
         prf.save(this);

         ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
         block.removeFieldProfile();
         prf.save(this);

         rowset.clear();
         roset.clear();
         manset.clear();
         qryset.clear();
         mrset.clear();
         cmdset.clear();
         dvset.clear();
         createRows();

      }
      catch (Exception e)
      {
         error(e);
      }
   }

   public void  preDefine() throws FndException
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

      //blk.addField("GROUP","Integer");

      blk.addField("ORDER","Integer").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFORD: Order").
          setSize(4);

      //flab =
      blk.addField("LABEL").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFNAM: Column Name").
          setSize(50).
          setReadOnly();

      blk.addField("SIZE","Integer").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFSIZ: Size").
          setSize(6);

      blk.addField("HEIGHT","Integer").setSize(6);
      blk.addField("SPAN","Integer").setSize(2);

      blk.addField("VISIBLE").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFVIS: Visible").
          setCheckBox("N,Y");

      blk.addField("SHOWLABEL").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFLBL: Show label");
          //setCheckBox("N,Y");

     // fmand =
      blk.addField("MANDATORY"); //.setReadOnly();
      blk.addField("SYS_MANDATORY");

      blk.addField("QUERYABLE"); //.setReadOnly();
      blk.addField("SYS_QUERYABLE");

      blk.addField("READONLY"); //.setReadOnly();
      blk.addField("SYS_READONLY");

      //blk.addField("APPLY_LAY_PROFILE");

      rowset = blk.getASPRowSet();
      bar = mgr.newASPCommandBar(blk);
      bar.disableMinimize();
      bar.addCustomCommand("activateQuery","Query");
      bar.addCustomCommand("activateMandatory","Mandatory");
      bar.addCustomCommand("activateReadOnly","Read Only");
      bar.addCustomCommand("activateLayout","Layout");
      bar.addCustomCommand("activateMultirow","Multirow");
      bar.addCustomCommand("activateCommands","Menu Items");
      bar.addCustomCommand("activateDefaultValues","Default Values");

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      tbl = mgr.newASPTable( blk );
   //   tbl.setPageSize(100);
      tbl.disableRowStatus();
      tbl.disableRowSelect();
      tbl.disableQueryRow();
      tbl.setEditable();
      tbl.unsetSortable();

      String script_loc = getASPConfig().getScriptsLocation();
      includeJavaScriptFile(script_loc.substring(getASPConfig().getApplicationContext().length()) + "BlockLayoutProfileUtility.js");

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);

      //if changing tab order change setTabEnabled as appropriate.
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTLAYOUTTAB: Detail Layout"),"javascript:multiStore();javascript:commandSet('MAIN.activateLayout','')");
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTTABLETAB: Overview Layout"),"javascript:store();javascript:commandSet('MAIN.activateMultirow','')");
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTCMDTAB: Menu Items"),"javascript:store();javascript:commandSet('MAIN.activateCommands','')");
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTTEMPLATETAB: Templates"),"javascript:store();javascript:commandSet('MAIN.activateDefaultValues','')");
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTMANDATORYTAB: Mandatory"),"javascript:store();commandSet('MAIN.activateMandatory','')");
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTQUERYTAB: Query"),"javascript:store();commandSet('MAIN.activateQuery','')");
      tabs.addTab(mgr.translate("FNDPAGESCONFIGSCRLAYOUTREADONLYTAB: Read Only"),"javascript:store();commandSet('MAIN.activateReadOnly','')");

      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

      bar.removeCustomCommand("activateQuery");
      bar.removeCustomCommand("activateMandatory");
      bar.removeCustomCommand("activateReadOnly");
      bar.removeCustomCommand("activateLayout");
      bar.removeCustomCommand("activateMultirow");
      bar.removeCustomCommand("activateCommands");
      bar.removeCustomCommand("activateDefaultValues");


      //QUERY TAB
      qryblk = mgr.newASPBlock("QUERY_BLK");

      qryblk.addField("QUERY_NAME").
            setHidden();

      qryblk.addField("QUERY_LABEL").
          setLabel("FNDPAGESCONFIGSCRLAYOUTQUERYCOLNAME: Column Name").
          setSize(50).
          setReadOnly();

      qryblk.addField("ISQUERYABLE").
          setLabel("FNDPAGESCONFIGSCRLAYOUTQUERYABLE: Queryable").
          setCheckBox("N,Y");

      qrylay = qryblk.getASPBlockLayout();
      qrylay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      qryset = qryblk.getASPRowSet();

      qrybar = mgr.newASPCommandBar(qryblk);
      qrybar.disableMinimize();
      qrybar.disableCommand(ASPCommandBar.FIND);
      qrybar.disableCommand(ASPCommandBar.PROPERTIES);

      qrytbl = mgr.newASPTable( qryblk );
      qrytbl.setEditable();
      qrytbl.disableRowStatus();
      qrytbl.disableRowSelect();
      qrytbl.disableQueryRow();

      //MANDATORY TAB
      manblk = mgr.newASPBlock("MANDATORY_BLK");

      manblk.addField("MANDATORY_NAME").
            setHidden();

      manblk.addField("MANDATORY_LABEL").
          setLabel("FNDPAGESCONFIGSCRLAYOUTMANDATORYCOLNAME: Column Name").
          setSize(50).
          setReadOnly();

      manblk.addField("ISMANDATORY").
          setLabel("FNDPAGESCONFIGSCRLAYOUTMANDATORY: Mandatory").
          setCheckBox("N,Y");

      manlay = manblk.getASPBlockLayout();
      manlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      manset = manblk.getASPRowSet();

      manbar = mgr.newASPCommandBar(manblk);
      manbar.disableMinimize();
      manbar.disableCommand(ASPCommandBar.FIND);
      manbar.disableCommand(ASPCommandBar.PROPERTIES);

      mantbl = mgr.newASPTable( manblk );
      mantbl.setEditable();
      mantbl.disableRowStatus();
      mantbl.disableRowSelect();
      mantbl.disableQueryRow();

      //READONLY TAB
      roblk = mgr.newASPBlock("READONLY_BLK");

      roblk.addField("READONLY_NAME").
            setHidden();

      roblk.addField("READONLY_LABEL").
          setLabel("FNDPAGESCONFIGSCRLAYOUTREADONLYCOLNAME: Column Name").
          setSize(50).
          setReadOnly();

      roblk.addField("ISREADONLY").
          setLabel("FNDPAGESCONFIGSCRLAYOUTREADONLY: Read Only").
          setCheckBox("N,Y");

      rolay = roblk.getASPBlockLayout();
      rolay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      roset = roblk.getASPRowSet();

      robar = mgr.newASPCommandBar(roblk);
      robar.disableMinimize();
      robar.disableCommand(ASPCommandBar.FIND);
      robar.disableCommand(ASPCommandBar.PROPERTIES);

      rotbl = mgr.newASPTable( roblk );
      rotbl.setEditable();
      rotbl.disableRowStatus();
      rotbl.disableRowSelect();
      rotbl.disableQueryRow();

      //MULTIROW TAB
      mrblk = mgr.newASPBlock("MR_BLK");

      mrblk.addField("MR_NAME").setDbName("NAME").
          setHidden();

      mrblk.addField("MR_ORDER","Integer").setDbName("ORDER").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFORD: Order").
          setSize(4);

      mrblk.addField("MR_LABEL").setDbName("LABEL").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFNAM: Column Name").
          setSize(50).
          setReadOnly();

      mrblk.addField("MR_SIZE","Integer").setDbName("SIZE").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFSIZ: Size").
          setSize(6);

      mrblk.addField("MR_VISIBLE").setDbName("VISIBLE").
          setLabel("FNDPAGESASPTABLEPROFILETABPRFVIS: Visible").
          setCheckBox("N,Y");

      mrblk.addField("MR_MANDATORY").setDbName("MANDATORY").
          setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFMAN: Mandatory").
          setCheckBox("N,Y").
          setReadOnly();

      mrset = mrblk.getASPRowSet();
      //mrbar = mgr.newASPCommandBar(mrblk);

      //mrtbl = mgr.newASPTable( mrblk );

      //String script_loc = getASPConfig().getParameter("APPLICATION/LOCATION/SCRIPTS","/&(APPLICATION/PATH)/common/scripts/");
      //includeJavaScriptFile(script_loc.substring(getASPConfig().getParameter("APPLICATION/LOCATION/ROOT").length()-1) + "TableProfileUtility.js");


      //COMMANDBAR TAB
      cmdblk = mgr.newASPBlock("CMD_BLK");

      cmdblk.addField("CMD_ID").
            setHidden();

      cmdblk.addField("CMD_NAME").
          setLabel("FNDPAGESCONFIGSCRLAYOUTMENUITEMNAME: Menu Item").
          setSize(50).
          setReadOnly();

      cmdblk.addField("CMD_ENABLED").
          setLabel("FNDPAGESCONFIGSCRLAYOUTENABLED: Enabled").
          setCheckBox("N,Y");

      cmdlay = cmdblk.getASPBlockLayout();
      cmdlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      cmdset = cmdblk.getASPRowSet();

      ASPCommandBar cmdbar = mgr.newASPCommandBar(cmdblk);
      cmdbar.disableMinimize();
      cmdbar.disableCommand(ASPCommandBar.FIND);
      cmdbar.disableCommand(ASPCommandBar.PROPERTIES);

      cmdtbl = mgr.newASPTable( cmdblk );
      cmdtbl.setEditable();
      cmdtbl.disableRowStatus();
      cmdtbl.disableRowSelect();
      cmdtbl.disableQueryRow();

      //DEFAULTVALUES TAB
      dvblk = mgr.newASPBlock("DEFAULTS");

      dvblk.addField("DV_NAME").setDbName("NAME").
            setHidden();

      dvblk.addField("DV_LABEL").setDbName("LABEL").
            setLabel("FNDPAGESCONFIGSCRLAYOUTTABPRFNAM: Column Name").
            setSize(50).
            setReadOnly();

      dvblk.addField("DEFAULT_VALUE").
            setLabel("FNDPAGESCONFIGSCRLAYOUTDEFVALUE: Default Value").
            setSize(20);

      dvlay = dvblk.getASPBlockLayout();
      dvlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      dvset = dvblk.getASPRowSet();

      ASPCommandBar dvbar = mgr.newASPCommandBar(dvblk);
      dvbar.disableMinimize();
      dvbar.disableCommand(ASPCommandBar.FIND);
      dvbar.disableCommand(ASPCommandBar.PROPERTIES);

      dvtbl = mgr.newASPTable( dvblk );
      dvtbl.disableRowStatus();
      dvtbl.disableRowSelect();
      dvtbl.disableQueryRow();

      //Templates Tab
      tmplblk = mgr.newASPBlock("TEMPLATES");
      tmplblk.addField("GLOBAL_TEMPLATE_ID").
              setHidden();
      tmplblk.addField("TEMPLATE_ID").
              setHidden();
      tmplblk.addField("DEFAULT").
              setLabel("FNDPAGESCONFIGSCRTEMPLATETABDEFAULT: Default").
              setHidden().
              setAsImageField();
      tmplblk.addField("TITLE").
              setLabel("FNDPAGESCONFIGSCRTEMPLATETABTITLE: Title").
              setSize(20).
              setMandatory();
      tmplblk.addField("DESCRIPTION").
              setLabel("FNDPAGESCONFIGSCRTEMPLATETABDESC: Description").
              setSize(50);
      tmplblk.addField("ENTITY").
              setLabel("FNDPAGESCONFIGSCRTEMPLATETABENTITY: Entity").
              setHidden().
              setSize(20);
      tmpllay = tmplblk.getASPBlockLayout();
      tmpllay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      tmplset = tmplblk.getASPRowSet();
      tmplblk.enableStandardCommands("New,Modify,Remove");
      ASPCommandBar tmplbar = mgr.newASPCommandBar(tmplblk);
      tmplbar.defineCommand(tmplbar.VIEWDETAILS, "viewTemplate");
      tmplbar.defineCommand(tmplbar.NEWROW, "newTemplate");
      tmplbar.defineCommand(tmplbar.SAVERETURN, "saveTemplate");
      tmplbar.defineCommand(tmplbar.SAVENEW, "saveNewTemplate");
      tmplbar.defineCommand(tmplbar.CANCELNEW, "cancelNewTemplate");
      tmplbar.defineCommand(tmplbar.EDITROW, "editTemplate");
      tmplbar.defineCommand(tmplbar.CANCELEDIT, "cancelEditTemplate");
      tmplbar.defineCommand(tmplbar.DELETE, "deleteTemplate");
      tmplbar.disableMinimize();
      tmplbar.disableCommand(ASPCommandBar.FIND);
      tmplbar.disableCommand(ASPCommandBar.PROPERTIES);
      tmplbar.disableCommand(ASPCommandBar.DUPLICATEROW);
      tmplbar.addCustomCommand("setAsDefault","FNDPAGESCONFIGSCRTEMPLATETABSETDEFAULT: Set As Default");
      tmplbar.addCustomCommand("renameTemplate","FNDPAGESCONFIGSCRTEMPLATETABRENAME: Rename Template");
      tmpltbl = mgr.newASPTable(tmplblk);
      
   }


//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "FNDPAGESCONFIGSCRLAYOUTDESC: Configurable Screen Layout for '" + title + "'";
   }

   protected String getTitle()
   {
      return "FNDPAGESCONFIGSCRLAYOUTTITLE: Configurable Screen Layout";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      if (sc)
      {
         return out;
      }

      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDPAGESCONFIGSCRLAYOUTTITLE: Configurable Screen Layout"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");

      out.append(mgr.startPresentation(mgr.translate("FNDPAGESCONFIGSCRLAYOUTTITLE1: Configurable Screen Layout")));

      out.append(tabs.showTabsInit());

      if (tabs.getActiveTab() == LAYOUT_TAB)
      {
          beginDataPresentation();

          //drawSimpleCommandBar(mgr.translate("LAYPROPTITLE: Properties for &1",title));
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
      else if (tabs.getActiveTab() == READONLY_TAB)
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         endDataPresentation(false);

         out.append("<table>");
         
         if (roset.countRows() > 1)
         {
            out.append("<tr><td "+(mgr.isRTL()?"align=left":"align=right")+">");
            printLink("FNDPAGESCONFIGSCRLAYOUTSELALL: Select All","javascript:selectAll('ISREADONLY')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTDESELALL: Deselect All","javascript:deselectAll('ISREADONLY')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTTOGGLE: Toggle Selection","javascript:invertselection('ISREADONLY')");
            out.append("</td></tr>");
         }

         out.append("<tr><td>");
         out.append(rotbl.populate());

         out.append("</td></tr></table>");

      }
      else if (tabs.getActiveTab() == MANDATORY_TAB)
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         endDataPresentation(false);

         out.append("<table>");
         
         if (manset.countRows() > 1)
         {
            out.append("<tr><td "+(mgr.isRTL()?"align=left":"align=right")+">");
            printLink("FNDPAGESCONFIGSCRLAYOUTSELALL: Select All","javascript:selectAll('ISMANDATORY')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTDESELALL: Deselect All","javascript:deselectAll('ISMANDATORY')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTTOGGLE: Toggle Selection","javascript:invertselection('ISMANDATORY')");
            out.append("</td></tr>");
         }

         out.append("<tr><td>");
         out.append(mantbl.populate());

         out.append("</td></tr></table>");


         //out.append(manlay.show());
      }
      else if (tabs.getActiveTab() == QUERY_TAB)
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         endDataPresentation(false);

         out.append("<table>");
         
         if (qryset.countRows() > 1)
         {
            out.append("<tr><td "+(mgr.isRTL()?"align=left":"align=right")+">");
            printLink("FNDPAGESCONFIGSCRLAYOUTSELALL: Select All","javascript:selectAll('ISQUERYABLE')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTDESELALL: Deselect All","javascript:deselectAll('ISQUERYABLE')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTTOGGLE: Toggle Selection","javascript:invertselection('ISQUERYABLE')");
            out.append("</td></tr>");
         }

         out.append("<tr><td>");
         out.append(qrytbl.populate());

         out.append("</td></tr></table>");

         //out.append(qrylay.show());
      }
      else if (tabs.getActiveTab() == MULTIROW_TAB)
      {
         beginDataPresentation();

         drawSimpleCommandBar("");

         out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
         out.append("<tbody><tr><td>\n");
         out.append("<tr>\n");
         out.append("<td height=100%>&nbsp;&nbsp;&nbsp;</td>");
         out.append("<td style=\"padding-right:10px; padding-bottom:10px;\" valign=top>\n");

         generateMultirowCustomBox(out);

         out.append("</td></tr></table>\n");
         out.append("<table>\n");
         out.append("<TBODY></TBODY>\n");

         endDataPresentation(false);

      }
      else if (tabs.getActiveTab() == COMMANDBAR_TAB)
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         endDataPresentation(false);

         out.append("<table>");
         
         if (cmdset.countRows() > 1)
         {
            out.append("<tr><td "+(mgr.isRTL()?"align=left":"align=right")+">");
            printLink("FNDPAGESCONFIGSCRLAYOUTSELALL: Select All","javascript:selectAll('CMD_ENABLED')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTDESELALL: Deselect All","javascript:deselectAll('CMD_ENABLED')");
            out.append("&nbsp;&nbsp;");
            printLink("FNDPAGESCONFIGSCRLAYOUTTOGGLE: Toggle Selection","javascript:invertselection('CMD_ENABLED')");
            out.append("</td></tr>");
         }

         out.append("<tr><td>");
         out.append(cmdtbl.populate());

         out.append("</td></tr></table>");

         //out.append(cmdlay.show());
      }
      else if (tabs.getActiveTab() == DEFAULTVALUE_TAB){
          if(tmpllay.isMultirowLayout())
                   tmplblk.getASPField("DEFAULT").unsetHidden();
          appendToHTML(tmpllay.show());          
          if(tmpllay.isSingleLayout() || tmpllay.isNewLayout() || tmpllay.isEditLayout()){
             createDvsetRows();
             beginDataPresentation();
             drawSimpleCommandBar("FNDPAGESCONFIGSCRLAYOUTTEMPLATETABTMPLVALUES: Template Values");
             if(template_status==VIEW_TEMPLATE || template_status == RENAME_TEMPLATE)
                out.append(dvtbl.populate());
             else if(template_status==EDIT_TEMPLATE || template_status==NEW_TEMPLATE)
             {
                dvtbl.setEditable();
                generateDefaultRows(out);
             }
             endDataPresentation(false);
          }
      }


      endDataPresentation(false);

      printHiddenField("BLKLAY_ACTIVETAB", tabs.getActiveTab()+"");

      AutoString details = new AutoString();

      boolean hasGroups = blockLayout.hasDefinedGroups();
      int group_id = -1;

      if (!hasGroups)
      {
         for(int i=0; i<rowset.countRows(); i++)
         {
            details.append(i+"");
            details.append((char)31);
            details.append(""+(int)rowset.getNumberValueAt(i,"ORDER"));
            details.append((char)31);
            details.append(rowset.getValueAt(i,"SIZE") );
            details.append((char)31);
            details.append(rowset.getValueAt(i,"HEIGHT") );
            details.append((char)31);
            details.append(rowset.getValueAt(i,"VISIBLE"));
            details.append((char)31);
            details.append(rowset.getValueAt(i,"SHOWLABEL"));
            details.append((char)31);
            details.append(getSpan(rowset.getValueAt(i,"SPAN")) );
            details.append((char)31);
            details.append(""+group_id);
            details.append((char)30);
         }
      }
      else
      {
         Vector groups = blockLayout.getDefinedGroups();
         int no_of_groups = groups.size();

         for (int i=0; i<no_of_groups; i++)
         {
            ASPBlockLayout.group group = (ASPBlockLayout.group)groups.elementAt(i);

            group_id = group.getId();

            //add fields in group
            Vector columns = blockLayout.listToFields(group.getFieldList(),false);
            int fields = columns.size();

            for (int j=0; j<fields; j++)
            {
               ASPField tmpCol = (ASPField) columns.elementAt(j);

               int rowno = findRowNo(tmpCol.getName());

               details.append(rowno+"");
               details.append((char)31);
               details.append(""+(int)rowset.getNumberValueAt(rowno,"ORDER"));
               details.append((char)31);
               details.append(rowset.getValueAt(rowno,"SIZE") );
               details.append((char)31);
               details.append(rowset.getValueAt(rowno,"HEIGHT") );
               details.append((char)31);
               details.append(rowset.getValueAt(rowno,"VISIBLE"));
               details.append((char)31);
               details.append(rowset.getValueAt(rowno,"SHOWLABEL"));
               details.append((char)31);
               details.append(getSpan(rowset.getValueAt(rowno,"SPAN")) );
               details.append((char)31);
               details.append(""+group_id);
               details.append((char)30);
            }
         }
      }

      printHiddenField("FIELD_DETAILS",details.toString());

      /*String app_prf=mgr.readValue("APPLY_LAY_PROFILE");
      if (mgr.isEmpty(app_prf))
          app_prf=rowset.getValueAt(0,"APPLY_LAY_PROFILE");

      printHiddenField("APPLY_LAY_PROFILE", app_prf);*/
      out.append(tabs.showTabsFinish());
      int activetab = tabs.getActiveTab();
      out.append("              <table cellspacing=5 border=0>\n");
      out.append("              <tr><td></td>\n");
      out.append("              <td "+((activetab!=DEFAULTVALUE_TAB)?"colspan=4 >":"")+"\n");
      printText("FNDPAGESCONFIGSCRLAYOUTREFRESH: Changes will NOT take effect until the underlying page is refreshed.");
      out.append("              </td></tr>\n");
      out.append("              <tr>\n");
      out.append("                  <td>\n");
      out.append("                  </td>\n");
      if(activetab!=DEFAULTVALUE_TAB)
      {
         out.append("                  <td>\n");
         String tag = " class='button'";
         printButton("APPLY","FNDPAGESCONFIGSCRLAYOUTAPPLY: Apply","onClick=\"javascript:checkMandatoryNonVisible('submit')\" "+tag);
         out.append("                  </td>\n");
         out.append("                  <td>\n");
         printButton("APPLYCLOSE","FNDPAGESCONFIGSCRLAYOUTAPPLYCLOSE: Apply & Close","onClick=\"javascript:checkMandatoryNonVisible('submitclose')\" "+tag);
         out.append("                  </td>\n");
         out.append("                  <td>\n");
         printButton("GETDEFAULT","FNDPAGESCONFIGSCRLAYOUTRESETPROFILE: Reset Profile","onClick=\"javascript:{if (confirm('" + mgr.translateJavaScript("FNDPAGESCSLPROFILERESETWARNING: The profile for this page will be reset. Your current page configurations will be lost.") + "')) commandClick('clearprofile');}\" "+tag);
         out.append("                  </td>\n");
      }
      out.append("                  <td>\n");
      printButton("CLOSE","FNDPAGESCONFIGSCRLAYOUTCLOSE: Close","onClick=\"window.close()\"");
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
      int pos = 0;
      String imgloc = mgr.getASPConfig().getImagesLocation();
      String imglocRTL = mgr.getASPConfig().getImagesLocationWithRTL();
      out.append("<BR>");
      out.append("<p>");
      out.append("<UL>");
      out.append("<Li>");
      printText("FFNDPAGESCONFIGSCRLAYOUTLINE1: Use the left/right arrows to move a selected field between the visible and non-visible lists.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE2: Use the up/down arrows to set the order of the visible fields.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE3: Use the text fields below to set the length and height of the selected visible field.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE4: An '*' indicates a mandatory field.");
      out.append("</Li>\n");
      out.append("</UL>");
      out.append("</p>");

      out.append("<table border=0><tr>\n");
      out.append("<td></td>");
      out.append("<td>");
      printWriteLabel("FNDPAGESCONFIGSCRLAYOUTVISIBLELIST: Visible Field List");
      out.append("</td>");
      out.append("<td></td>");
      out.append("<td>");
      printWriteLabel("FNDPAGESCONFIGSCRLAYOUTINVISIBLE: Non-visible Field List");
      out.append("</td>");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td valign=top>\n");
      out.append("<a href=\"javascript:moveItemUp('");
      out.appendInt(pos);
      out.append("')\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_up.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVU: Move selected field up"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVU: Move selected field up"),"\"");
      out.append("></a><br><br>\n");
      out.append("<a href=\"javascript:moveItemDown('");
      out.appendInt(pos);
      out.append("')\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_down.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVD: Moves selected field down"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVD: Moves selected field down"),"\"");
      out.append("></a>\n</td>\n");

      out.append("<td>\n<select name=col_");
      out.appendInt(pos);
      out.append(" size=10 class='selectbox' onChange=\"javascript:getFieldProps(this)\">\n");


      Vector visible_fields_values = new Vector();
      Vector visible_fields_text = new Vector();

      Vector invisible_fields_values = new Vector();
      Vector invisible_fields_text = new Vector();

      String field_value = "";
      String fld_label = "";
      boolean hasGroups = blockLayout.hasDefinedGroups();
      int group_id = -1;

      if (!hasGroups)
      {
         for(int i=0; i<rowset.countRows(); i++)
         {
            field_value = ""+ i + (char)31 +
                          ""+(int)rowset.getNumberValueAt(i,"ORDER") + (char)31 +
                          rowset.getValueAt(i,"SIZE")          + (char)31 +
                          rowset.getValueAt(i,"HEIGHT")        + (char)31 +
                          rowset.getValueAt(i,"VISIBLE")       + (char)31 +
                          rowset.getValueAt(i,"SHOWLABEL")     + (char)31+
                          getSpan(rowset.getValueAt(i,"SPAN")) + (char)31+
                          group_id + (char)31;


            fld_label = rowset.getValueAt(i,"LABEL");

            if ("Y".equals(rowset.getValueAt(i,"MANDATORY")))
               fld_label += " *";

            if ("Y".equals(rowset.getValueAt(i,"VISIBLE")))
            {
               visible_fields_text.add(fld_label);
               visible_fields_values.add(field_value);
            }
            else
            {
               invisible_fields_text.add(fld_label);
               invisible_fields_values.add(field_value);
            }
         }
      }
      else
      {
         Vector groups = blockLayout.getDefinedGroups();
         int no_of_groups = groups.size();

         for (int i=0; i<no_of_groups; i++)
         {
            ASPBlockLayout.group group = (ASPBlockLayout.group)groups.elementAt(i);

            group_id = group.getId();
            String group_title = "------";

            //add field separator
            try
            {
               group_title += blockLayout.getGroupName(group_id);
            }
            catch (NumberFormatException e)
            {
               group_title += mgr.translate("FNDPAGESCONFIGSCRLAYOUTGROUPTITLE: Group");

            }
            group_title += "------";

            visible_fields_text.add(group_title);
            visible_fields_values.add("GROUP_ID="+group_id);

            invisible_fields_text.add(group_title);
            invisible_fields_values.add("GROUP_ID="+group_id);

            //add fields in the group in the correct order of preference
            //Vector columns = blockLayout.listToFields(group.getFieldList(),false);
            Vector columns = blockLayout.listToFields(blockLayout.getProfileGroupFieldList(group_id),false);

            int fields = columns.size();

            for (int j=0; j<fields; j++)
            {
               ASPField tmpCol = (ASPField) columns.elementAt(j);

               int rowno = findRowNo(tmpCol.getName());

               field_value = ""+ rowno + (char)31 +
                             ""+(int)rowset.getNumberValueAt(rowno,"ORDER") + (char)31 +
                             rowset.getValueAt(rowno,"SIZE")          + (char)31 +
                             rowset.getValueAt(rowno,"HEIGHT")        + (char)31 +
                             rowset.getValueAt(rowno,"VISIBLE")       + (char)31 +
                             rowset.getValueAt(rowno,"SHOWLABEL")     + (char)31+
                             getSpan(rowset.getValueAt(rowno,"SPAN")) + (char)31 +
                             group_id + (char)31;

               fld_label = rowset.getValueAt(rowno,"LABEL");

               if ("Y".equals(rowset.getValueAt(rowno,"MANDATORY")))
                  fld_label += " *";

               if ("Y".equals(rowset.getValueAt(rowno,"VISIBLE")))
               {
                  visible_fields_text.add(fld_label);
                  visible_fields_values.add(field_value);
               }
               else
               {
                  invisible_fields_text.add(fld_label);
                  invisible_fields_values.add(field_value);
               }
            }
         }
      }

      for (int i=0; i<visible_fields_text.size(); i++)
      {
         if(i==0)
            out.append("<option selected value=");
         else
            out.append("<option value=");

         out.append(""+visible_fields_values.elementAt(i));
         out.append(">",""+visible_fields_text.elementAt(i),"</option>\n");
      }

      out.append("<option>____________________</option>\n");
      out.append("</select>\n</td>");

      out.append("<td nowrap "+(mgr.isRTL()?"align=right":"align=left")+" valign=center>\n");

      out.append("<a href=\"javascript:moveItemSide('");
      out.appendInt(pos);
      out.append("','");
      out.appendInt( pos+1);
      out.append("')\">");
      out.append("<img border=0 src=\"",imglocRTL,"portlet_right.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVR: Move selected field right"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVR: Move selected field right"),"\"");
      out.append("></a>\n");

      out.append("<BR><BR>\n");

      out.append("<a href=\"javascript:moveItemSide('");
      out.appendInt(pos+1);
      out.append("','");
      out.appendInt( pos );
      out.append("')\">");
      out.append("<img border=0 src=\"",imglocRTL,"portlet_left.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVL: Move selected field left"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVL: Move selected field left"),"\"");
      out.append("></a>\n</td>\n");

      out.append("<td>\n<select name=col_");
      out.appendInt(pos+1);
      out.append(" size=10 class='selectbox'>\n");

      for (int i=0; i<invisible_fields_text.size(); i++)
      {
         out.append("<option value=");
         out.append(""+invisible_fields_values.elementAt(i));
         out.append(">",""+invisible_fields_text.elementAt(i),"</option>\n");
      }

      out.append("<option>____________________</option>\n");
      out.append("</select>\n</td>");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td></td>\n");
      out.append("<td colspan=4>\n");
      printTextLabel("FNDPAGESCONFIGSCRLAYOUTFIELDSIZE: Field width: ");

      String def_width = "";
      String def_height = "";
      String show_lbl = "";
      String data_span = "";
      int span = 1;

      if (visible_fields_values.size() > 0)
      {
          String[] temp = split((String)visible_fields_values.elementAt(0), ""+(char)31);

          try
          {
             def_width = temp[2];
             def_height = temp[3];
             show_lbl = temp[5];
             data_span = getSpan(temp[6]);
          }
          catch (ArrayIndexOutOfBoundsException e)
          {
             //exception when its a group separator
          }
      }

      printField("FIELD_SIZE",def_width,"onChange='javascript:setFieldSize()'",5,4);

      //out.append("</td>\n");
      out.append("&nbsp;&nbsp;");

      //out.append("<td colspan=2>\n");
      printTextLabel("FNDPAGESCONFIGSCRLAYOUTFLDHEIGHT: Field Height: ");
      printField("FIELD_HEIGHT",def_height,"onChange='javascript:setFieldHeight()'",5,4);
      //out.append("</td>\n");
      out.append("&nbsp;&nbsp;");
      //out.append("<td>\n");
      printCheckBox("FNDPAGESCONFIGSCRLAYOUTSHOWLBL: Show label","SHOW_LABEL","Y","Y".equals(show_lbl),"onClick='javascript:setShowLabel()'");

      out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
      printTextLabel("FNDPAGESCONFIGSCRLAYOUTFLDSPAN: Data Span : ");
      int dlg_col_count = blockLayout.getDialogColumns();
      ASPBuffer buf = mgr.newASPBuffer();

      for (int i=0; i<dlg_col_count; i++)
      {
         ASPBuffer row = buf.addBuffer("DATA");
         row.addItem("VALUE",i==0?"1":""+(i*5));
         row.addItem("TEXT",mgr.translate("FNDPAGESCONFIGSCRLAYOUTCOLTEXT: &1 column",""+(i+1)));
      }

      printSelectBox("FIELD_SPAN",buf,data_span,"onChange='javascript:setFieldSpan()'");

      out.append("</td>\n");

      out.append("<td></td>\n");

      out.append("<td>\n");
      //printTextLabel("FNDLAYPROFCOLUMNSIZE: Number of columns: ");
      //printField("DIALOG_COLUMN_COUNT",target.getDialogColumns()+"","",5,4);
      out.append("</td>\n");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td></td>\n");
      out.append("<td>\n");
      /*String app_prf=mgr.readValue("APPLY_LAY_PROFILE");
      if (mgr.isEmpty(app_prf))
          app_prf=rowset.getValueAt(0,"APPLY_LAY_PROFILE");
      printCheckBox("FNDLAYPROFAPPLYPRF: Apply layout profile settings","CHKBOX_APPLPRF","Y", "Y".equals(app_prf),"onClick='javascript:setApplyLayProfile()'");*/
      out.append("</td>\n");
      out.append("</tr>\n");
      out.append("</table>\n");
      //printHiddenField("FIELD_DETAILS","");
   }

   private int findRowNo(String field_name)
   {
      int rows = rowset.countRows();
      for (int i=0; i<rows; i++)
      {
         if (field_name.equals(rowset.getValueAt(i, "NAME"))) return i;
      }
      return -1;
   }

   private void generateMultirowCustomBox(AutoString out) throws FndException
   {

      ASPManager mgr = getASPManager();
      int pos = 0;
      String imgloc = mgr.getASPConfig().getImagesLocation();

      out.append("<BR>");
      out.append("<p>");
      out.append("<UL>");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE1: Use the left/right arrows to move a selected field between the visible and non-visible lists.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE2: Use the up/down arrows to set the order of the visible fields.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE5: Use the text field below to set the size of the selected visible field.");
      out.append("</Li>\n");
      out.append("<Li>");
      printText("FNDPAGESCONFIGSCRLAYOUTLINE4: An '*' indicates a mandatory field.");
      out.append("</Li>\n");
      out.append("</UL>");
      out.append("</p>");


      out.append("<table><tr>\n");
      out.append("<td></td>");
      out.append("<td>");
      printWriteLabel("FNDPAGESCONFIGSCRLAYOUTVISIBLEMRLIST: Visible Field List");
      out.append("</td>");
      out.append("<td></td>");
      out.append("<td>");
      printWriteLabel("FNDPAGESCONFIGSCRLAYOUTINVISIBLEMRLIST: Non-visible Field List");
      out.append("</td>");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td valign=top>\n");
      out.append("<a href=\"javascript:multiMoveItemUp('");
      out.appendInt(pos);
      out.append("')\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_up.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVU: Move selected field up"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVU: Move selected field up"),"\"");
      out.append("></a><br><br>\n");
      out.append("<a href=\"javascript:multiMoveItemDown('");
      out.appendInt(pos);
      out.append("')\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_down.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVD: Moves selected field down"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPAGESCONFIGSCRLAYOUTMOVD: Moves selected field down"),"\"");
      out.append("></a>\n</td>\n");

      out.append("<td>\n<select name=col_");
      out.appendInt(pos);
      out.append(" size=10 class='selectbox' onChange=\"javascript:multiGetFieldSize(this)\">\n");


      Vector visible_fields_values = new Vector();
      Vector visible_fields_text = new Vector();

      Vector invisible_fields_values = new Vector();
      Vector invisible_fields_text = new Vector();

      String field_value = "";
      String fld_label = "";

      for(int i=0; i<mrset.countRows(); i++)
      {

         field_value = ""+ i + (char)31 +
                       ""+(int)mrset.getNumberValueAt(i,"ORDER") + (char)31 +
                       mrset.getValueAt(i,"SIZE") + (char)31 +
                       mrset.getValueAt(i,"VISIBLE") + (char)31;

         fld_label = mrset.getValueAt(i,"LABEL");

         if ("Y".equals(mrset.getValueAt(i,"MANDATORY")))
            fld_label += " *";

         if ("Y".equals(mrset.getValueAt(i,"VISIBLE")))
         {
            visible_fields_text.add(fld_label);
            visible_fields_values.add(field_value);
         }
         else
         {
            invisible_fields_text.add(fld_label);
            invisible_fields_values.add(field_value);
         }

      }

      for (int i=0; i<visible_fields_text.size(); i++)
      {
         if(i==0)
            out.append("<option selected value=");
         else
            out.append("<option value=");

         out.append(""+visible_fields_values.elementAt(i));
         out.append(">",""+visible_fields_text.elementAt(i),"</option>\n");
      }

      out.append("<option>____________________</option>\n");
      out.append("</select>\n</td>");

      out.append("<td nowrap "+(mgr.isRTL()?"align=right":"align=left")+" valign=center>\n");

      out.append("<a href=\"javascript:multiMoveItemSide('");
      out.appendInt(pos);
      out.append("','");
      out.appendInt( pos+1);
      out.append("')\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_right.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDPORALTMOVR: Move selected field right"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDPORALTMOVR: Move selected field right"),"\"");
      out.append("></a>\n");

      out.append("<BR><BR>\n");

      out.append("<a href=\"javascript:multiMoveItemSide('");
      out.appendInt(pos+1);
      out.append("','");
      out.appendInt( pos );
      out.append("')\">");
      out.append("<img border=0 src=\"",imgloc,"portlet_left.gif\"");
      out.append(" alt=\"",mgr.translateJavaText("FNDTABLEPROFMOVL: Move selected field left"),"\"");
      out.append(" title=\"",mgr.translateJavaText("FNDTABLEPROFMOVL: Move selected field left"),"\"");
      out.append("></a>\n</td>\n");


      out.append("<td>\n<select name=col_");
      out.appendInt(pos+1);
      out.append(" size=10 class='selectbox'>\n");

      for (int i=0; i<invisible_fields_text.size(); i++)
      {
         out.append("<option value=");
         out.append(""+invisible_fields_values.elementAt(i));
         out.append(">",""+invisible_fields_text.elementAt(i),"</option>\n");
      }

      out.append("<option>____________________</option>\n");
      out.append("</select>\n</td>");
      out.append("</tr>\n");
      out.append("<tr>\n");
      out.append("<td></td>\n");
      out.append("<td>\n");
      printTextLabel("FNDTABLEPROFFIELDSIZE: Field width: ");

      String def_width = "";
      if (visible_fields_values.size() > 0)
      {
          String[] temp = split((String)visible_fields_values.elementAt(0), ""+(char)31);
          def_width = temp[2];
      }

      printField("FIELD_SIZE",def_width,"onChange='javascript:multiSetFieldSize()'",5,4);
      out.append("</td>\n");
      out.append("</tr>\n");
      out.append("</table>\n");

      printHiddenField("MULTIROW_FIELD_DETAILS","");

   }

   //return data span as increments of 5 for proper formatting
   private String getSpan(String data_span)
   {
      try
      {
         int span = Integer.parseInt(data_span);
         span = (span/5);

          if (span == 0)
             span = 1;
          else
             span = span*5;

          return data_span = ""+span;
      }
      catch (NumberFormatException e)
      {
         return "1";
      }
   }

   private void generateDefaultRows(AutoString out) throws FndException{
      ASPManager mgr = getASPManager();

      if (profilePage == null) mgr.getProfilePage(url);
      ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
      beginTable("cntDEFAULTS",false,true," class=\"pageFormWithBorder\" ");

      beginTableTitleRow();
      beginTableCell(true);
      printBoldText("FNDPAGESCONFIGSCRLAYOUTTABPRFNAM: Column Name");
      endTableCell();
      beginTableCell(true);
      printBoldText("FNDPAGESCONFIGSCRLAYOUTDEFVALUE: Default Value");
      endTableCell();
      endTableTitleRow();

      beginTableBody();
      printHiddenField("__DEFAULTS_ROWSTATUS","QueryMode__");
      printHiddenField("DV_NAME","");
      printHiddenField("DV_LABEL","");
      printHiddenField("DEFAULT_VALUE","");

      int rows = dvset.countRows();

      for (int i=0; i<rows; i++)
      {
         ASPField fld = block.getASPField(dvset.getValueAt(i, "NAME"));
         beginTableCell(true);
         printReadOnlyField("DV_LABEL",dvset.getValueAt(i,"LABEL"),"", 50, 50,false);
         endTableCell();

         beginTableCell(true);

         String value = dvset.getValueAt(i,"DEFAULT_VALUE");
         if (value == null) value = "";
         if (fld.isSelectBox())
         {
            String[] client_values = fld.getIidClientValues();
            String[] db_values = fld.getIidDbValues();

            printSelectStart("DEFAULT_VALUE", "");
            printSelectOption("", "", false);

            for (int j=0; j<client_values.length; j++)
               printSelectOption(client_values[j],db_values[j],value.equals(db_values[j]) );

            printSelectEnd();
         }
         else if (fld.isCheckBox())
         {
            printCheckBox("DEFAULT_VALUE_CHKBOX","TRUE","TRUE".equals(value), "onClick=\"{if (this.checked) f.DEFAULT_VALUE["+(i+1)+"].value='TRUE'; else f.DEFAULT_VALUE["+(i+1)+"].value=''}\"");
            printHiddenField("DEFAULT_VALUE", value);
         }
         else
            printField("DEFAULT_VALUE",value,"",20);

         endTableCell();

         printHiddenField("__DEFAULTS_ROWSTATUS", "New__");

         nextTableRow();
      }
      endTableBody();

      endTable();

      String editables = ",__DEFAULTS_ROWSTATUS,DEFAULT_VALUE,";
      mgr.getASPPage().getASPContext().writeValue("__DEFAULTS_COLUMNS",editables);

   }

   public void viewTemplate(){
      template_status = VIEW_TEMPLATE;
      tmplset.goTo(tmplset.getRowSelected());
      tmplset.selectRow();
      tmpllay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
   }
   
   public void newTemplate(){
      template_status = NEW_TEMPLATE;
      ASPBuffer data = getASPManager().newASPBuffer();
      data.addBuffer("DATA");
      tmplset.clear();
      tmplset.addRow(data);
      tmpllay.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
   }
   
   private String createTemplateID(ASPBuffer templates, boolean global){
      String field = (global)?"GLOBAL_TEMPLATE_ID":"TEMPLATE_ID";
      int count = templates==null?0:templates.countItems();
      String template_id = "0";
      if(count>0)
      {
         templates.sort(field,false);
         template_id = templates.getBufferAt(0).getValue(field);
         template_id = ""+(Integer.parseInt(template_id.substring(template_id.lastIndexOf("_")+1))+1);
      }
      return template_id;
   }
   
   public void saveTemplate() throws FndException{
      ASPManager mgr = getASPManager();
      ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
      
      if(template_status==NEW_TEMPLATE){
         ASPBuffer templates = block.getTemplateProfile();
         String template_id = createTemplateID(templates, false);
         tmplset.setValue("TEMPLATE_ID","Template_"+template_id);
         
         String prefix = profilePage.getComponent().toLowerCase()+"_"
                       + profilePage.getPageName().toLowerCase()+"_"
                       + block.getName().toLowerCase()+"_";
         
         tmplset.setValue("GLOBAL_TEMPLATE_ID",prefix+template_id);
      }
      
      block.updateTemplateProfileBuffer(tmplset.getRow(), dvset.getRows());
      profilePage.getASPProfile().save(this);
      template_status = DEFAULT_TEMPLATE_VIEW;
      createTemplateRows();
      tmpllay.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
   }
   
   public void saveNewTemplate() throws FndException{
      saveTemplate();
      newTemplate();
   }
   
   public void cancelNewTemplate(){
      template_status = DEFAULT_TEMPLATE_VIEW;
      tmpllay.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      createTemplateRows();
   }
   
   public void setAsDefault() throws FndException{
      ASPManager mgr = getASPManager();
      ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
      
      int defaultrow = -1;
      if(template_status==DEFAULT_TEMPLATE_VIEW)
         defaultrow = tmplset.getRowSelected();
      else
         defaultrow = tmplset.getCurrentRowNo();
      
      boolean def_exist = false;
      for(int x=0; x<tmplset.countRows(); x++)
      {
         tmplset.goTo(x);
         if("true".equals(tmplset.getValue("DEFAULT")))
         {
            tmplset.setValue("DEFAULT","false");
            def_exist = true;
            break;
         }
      }
      if(def_exist)
      {
         createDvsetRows();
         block.updateTemplateProfileBuffer(tmplset.getRow(), dvset.getRows());
         profilePage.getASPProfile().save(this);
      }
      
      tmplset.goTo(defaultrow);
      tmplset.setValue("DEFAULT","true");
      createDvsetRows();
      ASPBuffer profile = dvset.getRows();
      block.updateFieldProfileBuffer(profile);
      block.updateTemplateProfileBuffer(tmplset.getRow(), profile);
      profilePage.getASPProfile().save(this);
      
      template_status = DEFAULT_TEMPLATE_VIEW;
      createTemplateRows();
      tmpllay.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
   }
   
   public void editTemplate() throws FndException{
      template_status = EDIT_TEMPLATE;
      if(template_status==DEFAULT_TEMPLATE_VIEW)
         tmplset.goTo(tmplset.getRowSelected());
      tmpllay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
      tmplblk.getASPField("TITLE").setReadOnly();
      tmplblk.getASPField("DESCRIPTION").setReadOnly();
   }
   
   public void cancelEditTemplate(){
      cancelNewTemplate();
   }
   
   public void deleteTemplate() throws FndException{
      ASPManager mgr = getASPManager();
      ASPBlock block = mgr.getASPBlockFromPage(profilePage,name);
      
      if(template_status==DEFAULT_TEMPLATE_VIEW)
         tmplset.goTo(tmplset.getRowSelected());
      
      boolean is_default = "true".equals(tmplset.getValue("DEFAULT"));
      String template_id = tmplset.getValue("TEMPLATE_ID");
      
      block.removeTemplate(template_id,is_default);
      profilePage.getASPProfile().save(this);
      
      template_status = DEFAULT_TEMPLATE_VIEW;
      createTemplateRows();
      tmpllay.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
   }
   
   public void renameTemplate() throws FndException{
      if(template_status==DEFAULT_TEMPLATE_VIEW)
         tmplset.goTo(tmplset.getRowSelected());
      template_status = RENAME_TEMPLATE;
      tmpllay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
   }
   
   protected String getImageFieldTag(ASPField field, ASPRowSet rowset, int rowNum) throws FndException {
      if(field.getName().equals("DEFAULT"))
      {
         boolean val = rowset.getValueAt(rowNum, "DEFAULT").equals("true");
         String loc = getASPManager().getASPConfig().getImagesLocation();
         if(val)
            return "<img src=\""+loc+"check_box_marked.gif\" align=\"middle\" border-\"0\">";
         else
            return "<img src=\""+loc+"check_box_unmarked.gif\" align=\"middle\" border-\"0\">";
      }
      return "&nbsp;";
   }
}

