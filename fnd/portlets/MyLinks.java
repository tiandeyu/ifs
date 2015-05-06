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
 * File        : MyLinks.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2000-May-19 - Created.
 *    Jacek P  2000-May-31 - Corrected bug: error if there are no links stored in DB.
 *    Jacek P  2000-Aug-11 - Added better configuration possibilities (Log id: 160)
 *    Artur K  2000-Nov-23 - Added possibility to have links shown in navigate
 *    Piotr Z                menu.
 *    Piotr Z  2001-Mar-12 - Removing global profile for MyLinks in clearGlobalProfile().
 *    Artur K  2001-Mar-20 - Necessary changes for handling multiple portal pages.
 *    Artur K 2001-Jun-19  - Check box 'SHOWLINKSCB' will be automatically checked after any 
 *                           changes in the select boxes on the configuration page 
 *                           (when using Netscape, log id 550)
 *    Sampath 2003-May-28  - Changes in the customize mode  for build 4
 *    Chandana2004-Apr-23  - Made link boxes stretchable.
 *    Chandana2004-May-13  - Updated for the use of new style sheets.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/07/02 buhilk Bug Id 91498, Changed printContents(), fetchDBLinks(), submitCustomization()
 * 2010/06/28 buhilk Bug Id 91645, Changed printContents() & Moved groupParams to ASPPage
 * 2010/06/22 buhilk Bug Id 91494, changed printContents() to split params and check for encodubg.
 *                                 Also added groupParams to perform specific escape fix for bug 89518
 * 2010/06/02 amiklk Bug Id 89518, changed printContents() to use &amp; instead of & for IE
 * 2007/02/07 buhilk Bug Id 63250, added rtl support for new themes
 * 2006/09/24 mapelk Bug Id 59842, Improved CSV code 
 * 2006/08/10 buhilk Bug 59442, Corrected Translatins in Javascript
 *
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;

import java.util.*;


/**
 */
public class MyLinks extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.MyLinks");

   //==========================================================================
   //  Instances created on page creation (immutable attributes)
   //==========================================================================


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient ASPBuffer             dblinks;
   private transient ASPTransactionBuffer  trans;
   private transient boolean               dblinks_fetched;


   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private transient ASPBuffer  prflinks;
   private transient String     title;
   private transient String     navtitle;
   private transient boolean    mylinks;
   private transient boolean    navlinks;

   /**
    * Constructor
    */ 
   public MyLinks( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
      if(DEBUG) debug(this+": MyLinks.<init>");
   }


   /**
    * Clear temporary variables.
    */
   protected void doReset() throws FndException
   {
      if(DEBUG) debug(this+": MyLinks.doReset()");

      super.doReset();

      trans           = null;
      dblinks_fetched = false;
      dblinks         = null;
      prflinks        = null;
      title           = null;
      navtitle        = null;
      mylinks         = false;
      navlinks        = false;
   }


   /**
    * Create a new instance if all existing instances of the same page are already
    * locked in the pool.
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if(DEBUG) debug(this+": MyLinks.clone("+obj+")");

      MyLinks page = (MyLinks)(super.clone(obj));

      page.trans           = null;
      page.dblinks_fetched = false;
      page.dblinks         = null;
      page.prflinks        = null;
      page.title           = null;
      page.navtitle        = null;
      page.mylinks         = false;
      page.navlinks        = false;
      return page;
   }


   /**
    * Description that will be shown on the 'Customize Portal' page.
    */ 
   public static String getDescription()
   {
      if(DEBUG) Util.debug("MyLinks.getDescription()");
      return "FNDMYLINKSDESC: My Links";
   }


   /**
    * Creation of all page objects. Runs only the first time.
    */ 
   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": MyLinks.preDefine()");

      // temporary block for type definition of variables used in commands.
      ASPBlock blk = newASPBlock("DUMMY");
      addField(blk, "PROFILE");
      addField(blk, "URL");
      ASPForm frm = getASPManager().getASPForm();
      frm.setFormWidth(100);
      frm.setFormHeight(100);
      String scrloc = getScriptsLocation().substring(getASPConfig().getApplicationContext().length());
      includeJavaScriptFile(scrloc + "MyLinksScript.js");
      appendJavaScript("function customizeMyLinks(obj,id)",
                       "{",
                          "customizeBox(id);",
                       "}\n");
      init();
   }


   /**
    * Initialization of variables.
    */
   protected void init()
   {
      if(DEBUG) debug(this+": MyLinks.init()");

      title      = readProfileValue("MINTITLE", translate(getDescription()) );
      navtitle   = readProfileValue("NAVTITLE", translate(getDescription()) );
      mylinks    = readProfileFlag("MYLINKS",   false);
      navlinks   = readProfileFlag("NAVLINKS",  false);
      prflinks   = readProfileBuffer("MYLINKSBUF");
      
      if(DEBUG) debug("  init:\n\t\t\t title="+title+
                             "\n\t\t\t navtitle="+navtitle+
                             "\n\t\t\t mylinks="+mylinks+
                             "\n\t\t\t navlinks="+navlinks+
                             "\n\t\t\t prflinks:\n"+(prflinks==null?"null":prflinks.format())+"\n\n");
   }
  
   private void fetchDBLinks()
   {
      if(DEBUG) debug(this+": MyLinks.fetchDBLinks()");

      if(dblinks_fetched) return;

      dblinks_fetched = true;
      ASPManager mgr = getASPManager();
     
      ASPBuffer buf = mgr.getPortalPage().readGlobalProfileBuffer("Links",false);
      dblinks = mgr.newASPBuffer();
      
      if(buf!=null)
      {         
                    
         for (int i=0; i<buf.countItems();i++)
         {
            
            String name = buf.getNameAt(i);
            name= name.substring(name.indexOf(ProfileUtils.ENTRY_SEP)+1,name.length());
            String value = buf.getValueAt(i);
            if(value.split("~").length==1) value = "NULL~"+value;
            ASPBuffer row = dblinks.addBuffer("DATA");
            
            row.addItem("VALUE",value);
            
            if(mgr.isEmpty(name))
               row.addItem("DESC",value);
            else
               row.addItem("DESC",name);
         }
      }      
   }     


   /**
    * Run the business logic.
    */
   protected void run()
   {
      if(DEBUG) debug(this+": MyLinks.run()");

      if(!mylinks)
         fetchDBLinks();
   }


   /**
    * Create the portlets title for different modes.
    */
   public String getTitle( int mode )
   {
      if(DEBUG) debug(this+": MyLinks.getTitle("+mode+")");

      if(mode==MINIMIZED || mode==MAXIMIZED)
         return title;
      else
         return translate("FNDMYLINKSCUSTTIT: Customize My Links");
   }


   /**
    * Print the HTML contents of the portlet.
    */
   public void printContents()
   {
      if(DEBUG) debug(this+": MyLinks.printContents()");
      ASPManager mgr = getASPManager();
      printNewLine();

      if( !mylinks && (dblinks==null || dblinks.countItems()==0) )
      {
         printText("FNDMYLINKSNOLINKS: You have no saved links.");
      }
    /*  else if(!mylinks)
      {
         for(int i=0; i<dblinks.countItems(); i++)
         {
            ASPBuffer row = dblinks.getBufferAt(i);
            printSpaces(3);
            String link = row.getValue("VALUE");
            String desc = row.getValue("DESC");
            int pos = link.indexOf('~');
            printLink(desc,pos>0?link.substring(pos+1):link);
            printNewLine();
         }
      } */
      else if(prflinks!=null && prflinks.countItems()>0)
      {
         for(int i=0; i<prflinks.countItems(); i++)
         {
            ASPBuffer row = prflinks.getBufferAt(i);
            printSpaces(3);
            String link = row.getValue("VALUE");
            String desc = row.getValue("DESC");
            boolean decode = link.startsWith("DECODE");
            if(link.indexOf("?")>0){
                String[] _params = link.split("\\&");
                link = "";
                for(int x=0; x<_params.length; x++){
                    if(_params[x].indexOf("=")>0){
                        String[] pair = _params[x].split("\\=");
                        if(pair.length==2){
                            if(pair[1].equals(mgr.URLDecode(pair[1])))
                                pair[1] = mgr.URLEncode(pair[1]);
                            link += groupParams(pair[0], pair[1], x!=0, decode);
                        } else
                            link += groupParams(pair[0], "", x!=0, false);
                    } else
                        link += _params[x];
                }
            }
            int pos = link.indexOf('~');
            printLink(desc,mgr.decodedURLWithCSV(pos>0?link.substring(pos+1):link));
            printNewLine();
         }
      }
      else
      {  
         printText("FNDMYLINKSNOTCONFIGUREDYET01: This portlet is not configured yet. Click");
         printSpaces(1);
         printScriptLink("FNDMYLINKSNOTCONFIGUREDYET02: here","customizeMyLinks");
         printSpaces(1);
         printText("FNDMYLINKSNOTCONFIGUREDYET03: to configure.");
      }


      printNewLine();
   }

   /**
    * If the portlet should be customizable this function must return true.
    */
   public boolean canCustomize()
   {
      if(DEBUG) debug(this+": MyLinks.canCustomize()");
      return true;
   }


   /**
    * Run the business logic for the customize mode.
    */
   public void runCustom()
   {
      if(DEBUG) debug(this+": MyLinks.runCustom()");

      fetchDBLinks();
      if(prflinks==null)
         prflinks = getASPManager().newASPBuffer();

      ASPBuffer row = dblinks.addBuffer("DATA");
      row.addItem("VALUE","");
      row.addItem("DESC","____________________________");

      row = prflinks.addBuffer("DATA");
      row.addItem("VALUE","");
      row.addItem("DESC","____________________________");
   }

   private String printCustomizedMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String func, boolean mandatory, int size )
   {
      AutoString out = new AutoString(); 
      out.append("<select class='selectbox' size=");
      out.append(size+"");
      out.append(" name=",addProviderPrefix(),name);
      if(!Str.isEmpty(func))
         out.append(" onChange=\"javascript:",func,"(this,'",getId(),"')\"");
      out.append(">");
      if(mandatory)
         out.append( getASPHTMLFormatter().populateMandatoryListBox(aspbuf, key) );
      else
         out.append( getASPHTMLFormatter().populateListBox(aspbuf, key) );
      out.append("</select>");
      return out.toString();
   }

   /**
    * Print the HTML code for the customize mode.
    */
 public void printCustomBody() throws FndException
   {
      boolean is_explorer = getASPManager().isExplorer();
      String align = getASPManager().isRTL()?RIGHT:LEFT;
      printHiddenField("PRFLINKSLIST","");
      printHiddenField("SHOWLINKSCB","TRUE");
      
      
      beginTransparentTable("customise my links");
       beginTableBody();

            beginTableCell();
               printSpaces(1);
            endTableCell();
          nextTableRow();
            beginTableCell();
               printText("FNDMYLINKURLDESC1: Configure this portlet by adding links from your link Repository");
            endTableCell();
         nextTableRow();
            beginTableCell();
               printSpaces(1);
            endTableCell();
         nextTableRow();
            beginTableCell();
               beginTransparentTable();
                  beginTableBody();
                  beginTableCell();
                     beginTransparentTable();
                        beginTableBody();
                        beginTableCell(align);
                           setFontStyle(1);
                           printText("FNDMYLINKLINKREPOSITORY: Link Repository");
                        endTableCell();
                        nextTableRow();
                        beginTableCell(align);
                            appendToHTML( printCustomizedMandatorySelectBox("DBLINKS",dblinks,null,"setMyLinksFlag",true,10));
                        endTableCell();
                        endTableBody();
                     endTable();
                  endTableCell();
                  beginTableCell(CENTER);
                     printScriptImageLink( getImagesLocationWithRTL()+"portlet_right.gif", "moveLink");
                  endTableCell();
                  beginTableCell(CENTER);
                     beginCustomTable("",true,"class=\"borders\" width=50%");
                     beginTableBody();
                     beginTableCell();
                     beginTransparentTable();
                        beginTableBody();
                        beginTableCell(align);
                           setFontStyle(1);
                           printText("FNDMYLINKLINKTITLE: Title");
                        endTableCell();
                        beginTableCell();
                           printSpaces(3);
                        endTableCell();
                        nextTableRow();
                        beginTableCell(align);
                            printField("TITLE",title,33);
                        endTableCell();
                        beginTableCell();
                           printSpaces(3);
                        endTableCell();
                        nextTableRow();
                        beginTableCell(align);
                             appendToHTML( printCustomizedMandatorySelectBox("PRFLINKS",prflinks,null,"setMyLinksFlag",true,10));
                        endTableCell();
                        beginTableCell();
                           beginTransparentTable();
                              beginTableBody();
                                 beginTableCell(align);
                                    printScriptImageLink( getImagesLocation()+"portlet_up.gif", "moveItemUp");
                                 endTableCell();
                               nextTableRow();
                                 beginTableCell(); 
                                    printSpaces(1);
                                 endTableCell();
                               nextTableRow();
                                 beginTableCell(align);
                                    printScriptImageLink( getImagesLocation()+"portlet_down.gif", "moveItemDown");
                                 endTableCell();
                               endTableBody();
                            endTable();  
                         endTableCell();
                         nextTableRow();
                            beginTableCell(CENTER);
                               printScriptLink("FNDMYLINKADDNEW: Add new","addPrfLink");
                               printSpaces(2);
                               printScriptLink("FNDMYLINKMODIFY: Modify","openEditMyLinks");
                               printSpaces(2);
                               printScriptLink("FNDMYLINKDELETEPRFILE: Delete","deleteMyLinks");
                            endTableCell();
                            beginTableCell(); 
                               printSpaces(3);
                            endTableCell();
                       endTableBody();
                       endTable();
                       endTableCell();
                       endTableBody(); 
                       endTable();
                       endTableCell();
                       nextTableRow();
                       beginTableCell(1);
                       endTableCell();
                       
                       beginTableCell(3,1,CENTER);
                           printCheckBox("NAVLINKSCB",navlinks);
                           printText("FNDSHOWINNAVLINKS: Show contents of this portlet in the navigate menu.");
                       endTableCell();
                       nextTableRow();
                       beginTableCell(align);
                           printScriptLink("FNDMODIFYREPOSITORY: Modify Repository","modifyRepository");
                       endTableCell();
                       beginTableCell();
                          printSpaces(3);
                       endTableCell();
                       beginTableCell();
                          printSpaces(3);
                       endTableCell();
                       endTableBody();
                       endTable();
                    endTableCell();
                    nextTableRow();
                      beginTableCell();
                          printSpaces(3);
                      endTableCell();
   
                    endTableBody();
                    endTable();
                    
                    ASPManager mgr = getASPManager();
                    appendDirtyJavaScript("saveLinksChanges('",getId(),"');\n");
                    appendDirtyJavaScript("__SAVE_QUERY_URL='"+mgr.getASPConfig().getParameter("APPLICATION/LOCATION/SCRIPTS")+"EditMyLinksDlg.page';\n");
                    appendDirtyJavaScript("__MODIFY_REP_URL='"+mgr.getASPConfig().getParameter("APPLICATION/LOCATION/SCRIPTS")+"ModifyLinkRepository.page';\n");
                    appendDirtyJavaScript("__SELECT_A_LINK_TO_EDIT_MSG = '"+mgr.translateJavaScript("FNDMYLINKSSELECTALINKTOEDIT: Select a link to Modify")+"'\n");
                    appendDirtyJavaScript("__SELECT_A_LINK_TO_DELETE_MSG = '"+mgr.translateJavaScript("FNDMYLINKSSELECTALINKTODELETE: Select a link to Delete")+"'\n");
                    appendDirtyJavaScript("__MYLINK_DELETE_CONFERM_MSG  = '"+mgr.translateJavaScript("FNDMYLINKSDELETECONFERM: The Link will be Deleted")+"'\n");

   }

 /**
    * Save values of variables to profile and context if the user press OK button.
    */
   public void submitCustomization()
   {
      if(DEBUG) debug(this+": MyLinks.submitCustomization()");

      fetchDBLinks();
      ASPManager mgr = getASPManager();

      title      = readValue("TITLE");
      navtitle   = title;
      mylinks    = "TRUE".equals(readValue("SHOWLINKSCB"));
      navlinks   = "TRUE".equals(readValue("NAVLINKSCB"));

      if(DEBUG) debug("  submitCustomization():"+
                      "\n\t  title      = "+title+
                      "\n\t  navtitle   = "+navtitle+
                      "\n\t  mylinks    = "+mylinks+
                      "\n\t  navlinks   = "+navlinks);

      writeProfileValue("MINTITLE",    title );
      writeProfileValue("NAVTITLE", navtitle );
      writeProfileFlag("MYLINKS",   mylinks);
      writeProfileFlag("NAVLINKS",  navlinks);

      if(!mylinks) return;

      String links = readValue("PRFLINKSLIST");
      if(DEBUG) debug("  [submitCustomization] PRFLINKSLIST: links="+links);

      prflinks = mgr.newASPBuffer();
      
      ASPBuffer nav =  mgr.newASPBuffer(ProfileUtils.newProfileBuffer());
      if(!Str.isEmpty(links))
      {
         if(navlinks) nav.addItem("TITLE",navtitle);

         StringTokenizer pst = new StringTokenizer(links, "^");
         while( pst.hasMoreTokens() )
         {
            String[] link = pst.nextToken().split("~");
            String url = "";

            if(link.length>=2)
                url = link[link.length-2]+"~"+link[link.length-1];
            else
                url = "NULL~"+link[0];
            
            ASPBuffer row = prflinks.addBuffer("DATA");
            row.addItem("VALUE",url);
            row.addItem("DESC",link[0]);

            if(navlinks) nav.addItem("URL",link[0]+"~"+url);
         }
      }
      writeProfileBuffer("MYLINKSBUF",prflinks);

      ASPPage page = mgr.getPortalPage();
      String name = page.getASPPortal().getName();
      String id = "ID_"+getId();
      
      
      if(navlinks)
      {
        ASPBuffer portal_buf = page.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE,false);
        ASPBuffer links_buf=portal_buf.getBuffer("Links");
                
        if (links_buf==null)
          links_buf=portal_buf.addBuffer("Links");         
        else
          links_buf.removeItem(id);           
        links_buf.addBuffer(id,nav);
        page.removeGlobalProfileItem(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE);
        page.writeGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE,portal_buf,false);
      }
      else
      {   
        ASPBuffer portal_buf = page.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE,false);        
        ASPBuffer links_buf=portal_buf.getBuffer("Links");                
        if (links_buf!=null && links_buf.itemExists(id))    
        {
          links_buf.removeItem(id);          
          page.removeGlobalProfileItem(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE); 
          page.writeGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE,portal_buf,false);
        }
      }
  
   }

   protected void clearGlobalProfile()
   {
      if(DEBUG) debug(this+": MyLinks.clearGlobalProfile()");
      ASPPage page = getASPManager().getPortalPage();
      String name  = page.getASPPortal().getName();
      page.removeGlobalProfileItem(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE+"/Links/ID_"+getId());
   }

   public static int getMinWidth()
   {
      if(DEBUG) Util.debug("MyLinks.getMinWidth()");
      return 144;
   }
}
