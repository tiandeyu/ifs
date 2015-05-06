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
 * File        : ASPNavigator.java
 * Description : Class to create the IFS Navigator
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    TOWR     1999-Jul-29 - Created
 *    Jacek P  1998-Jul-31 - Rewritten.
 *    Jacek P  1998-Aug-10 - Added try..catch block to setRoot() function
 *    Jacek P  1998-Aug-20 - Changes due to redesigned structure of
 *                           the ASPConfig.ifm file (Log id:#2623).
 *    Jacek P  1998-Aug-28 - Corrected bug # 2658: Navigator do not accept
 *                           list of views for security check.
 *    Jacek P  1998-Aug-31 - Changed names of GIF files to not include space
 *                           sign due to problems in Netscape.
 *    Jacek P  1998-Nov-05 - Label translation on object construction instead
 *                           of in formatItemEntry().
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Feb-19 - Implemented ASPPoolable.
 *    Jacek P  1999-Mar-01 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Mar-19 - Removed calls to getConfigParameter()
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Mangala,
 *    Chaminda 1999-Jun-10 - Added implementation of JavaScript based navigator.
 *    Jacek P  1999-Jun-10 - Only method setMenuType() must be public.
 *                           Changed acces to private for other
 *                           JavaScript-navigator functions.
 *    Jacek P  1999-Jun-14 - Added implementation of 'docking' functionality.
 *    Jacek P  1999-Jul-02 - Rewritten. Added configuration possibility.
 *    Stefan M 2000-Apr-04 - Change in generateCommonJavaScript() for compability with portals:
 *                           "new" querystring parameter added; if present, don't generate base=main.
 *    Reine A  2000-Apr-05 - Added functionality to method generate() regarding
 *                           Presentation Objects. Implemented setDefaultMode().
 *    Reine A  2000-May-21 - Modified the functionality in method accessAllowed().
 *    Reine A  2000-Jun-08 - Added new method accessAllowed2() for Pres Obj.
 *    jacek P  2000-Jun-13 - Rewritten presentation object security check in generate().
 *    Reine A  2000-Jun-13 - Modified formatItemEntry() for asp-fils as in parameter
 *    Chaminda O 2000-Oct-07 - Modified method generate() and added the methods formatBookmarkItemEntry()
 *                             and formatBookmarkItemEntry2() and generateBookmarkFile()
 *                             to support 'import-to-bookmarks' functionality.
 *    Jacek P  2001-Jan-19 - Corrected bug #573 - Navigator shows empty folders
 *                           on lack of access grants to contained objects.
 *                           Even corrected bookmark generation - now the algorithm
 *                           uses the same functions as menus. Removed functions
 *                           formatBookmarkItemEntry*(). Special handling of Web Kit
 *                           Presentation Objects.
 *    Kingsly P 2001-Mar-06 - Change lookup.asp -> Lookup.asp.
 *    Jacek P   2001-Mar-29 - Changed .asp to .page
 *    Mangala   2001-Mar-30 - Corrected bug #668
 *    Chaminda  2001-Jun-01 - Log #745 Export Navigator to Bookmarks/Favorites doesn't work
 *    Chandana D2002-Jun-18 - Appended 'removePageMask();' and return value of mgr.generatePageMaskTag() to 'out' variable.
 *    Ramila    2002-06-19  - Bug 31107 Saved link tree navigator doesn't work.
 *    Ramila    2002-07-17  - Added support for Netscape 6.
 *    ChandanaD 2002-07-18  - Used correctURL(url) to append an ID to the URL
 *    ChandanaD 2002-11-05  - Made compatible with NE6 and above.
 *    ChandanaD 2002-11-06  - Corrected Tree Navigator in Unix environment.
 *    Mangala   2002-12-03  - Remove Applet based navigator support.
 *    ChandanaD 2003-05-22  - Modified body tag attributes to remove page borders.
 *    Rifki R   2003-Aug-07 - Changed module name from 'WEBKIT' to 'FND' in generate() and 
 *                            accessAllowed2() to support new pres. object definitions.
 *    ChandanaD 2003-Aug-13 - Updated for the new L&F.
 *    Suneth M  2004-Apr-26 - Bug 43306, Added new variables left_position & top_position.
 *                            Changed construct() & generateJavaScriptHTML().
 *    Chandana D2004-May-12 - Updated for new style sheets.
 *    Chandana D2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Chandana  2004-Jun-10 - Removed all absolute URLs.
 *    Chandana  2004-Jun-22 - Export Navigator made to use a specific charset for IE and UTF-8 for NN/Mozilla.
 *    Chandana  2004-Aug-05 - Proxy support corrections.
 *    Ramila H 2004-08-24   - added multi language support for stylesheet tag.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/12/08 amiklk Bug id 87591, Changed formatXmlEntry to adjust xml mismatch
 * 2008/07/25 buhilk Bug id 75983, Added "Export Navigator to Favorites" icon to top of webclient tree navigator.
 * 2008/04/04 buhilk Bug id 72852, Modified generateJavaScriptHTML() to move bavigator position heigher when viewd from RWC.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/05/30 sadhlk Bug id 65716, Modified construct() to change the default value of the 'folder_area_width' attribute.
 * 2007/05/04 buhilk Bug id 65098, Broadcast message functionality was improved. Modified generateJavaScriptHTML().
 * 2007/04/25 sadhlk Bug 64985, Modified construct() to change the default value of the 'folder_area_width' attribute.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * Revision 1.6  2006/12/15 sadhlk
 * Bug id 62442, Added new formatXmlEntry(), formatXmlEntry2(), generateXmlFile(), getPresObj()
 *               Methods to support 'extract navigator for Presentation Object security' function.
 *
 * Revision 1.5  2006/11/20 buhilk 
 * Bug id 60473, Added new formatItemEntry() method to overload existing method.
 *               Modified the method formatItemEntry() to validate security grants on pl/sql methods in the target name.
 *
 * Revision x.x  2006/09/13 gegulk 
 * Bug id 60473, Modified the method formatItemEntry() for proper formatting in RTL Mode
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/08 10:00:08  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.3  2005/02/11 09:12:10  mapelk
 * Remove ClientUtil applet and it's usage from the framework
 *
 * Revision 1.2  2005/02/01 10:32:58  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import java.util.*;


/**
 * Creates the IFS Navigator
 */

public class ASPNavigator extends ASPPageElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public      static       boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPNavigator");
   public  static final String  CRLF = "\r\n";
   public  static final String  APPLET    = "APPLET";
   public  static final String  JS_FOLDER = "JAVASCRIPT_FOLDER";
   public  static final String  BOOKMARK = "BOOKMARK";
   public  static final String TOXML = "TOXML";
   public  static final String  MOBILE = "MOBILE";

   //==========================================================================
   // Instance variables
   //==========================================================================

   // immutable attributes
   private ASPNavigatorNode root;
   private AutoString       views;
   private AutoString       presobj;
   private String           background_color;
   private String           def_menu_type = JS_FOLDER;
   private String           folder_image_root;
   private String           folder_image_node_opened;
   private String           folder_image_node_closed;
   private String           folder_image_item;
   private String           folder_image_url;
   private String           folder_area_width;
   private String           folder_area_height;
   private String           folder_font_face;
   private String           folder_font_size;
   private String           folder_font_color;
   private String           folder_link_color_unvisited;
   private String           folder_link_color_visited;
   private String           folder_link_color_selected;
   private String           left_position;
   private String           top_position;
   private String           nav_exp_image;

   // temporary mutable attributes
   private ASPBuffer        security_info;
   private AutoString       out = new AutoString();
   private String           menu_type;
   private boolean          main_menu;
   private String           area_width;
   private String           area_height;
   private String           font_name;
   private String           font_size;
   private String           font_color;
   private String           image_root;
   private String           image_node;
   private String           image_item;
   private String           link_color;
   private String           vlink_color;
   private String           alink_color;
   private String           navigator_style;
   private String           vertical_line;

   //security attributes
   private boolean          default_mode;

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor. Calls constructor within the super class.
    *
    * An instance of ASPNavigator is automatically created by ASMManager.
    * Reference to the creator is passed as input argument.
    * @param page Reference to the creator page.
    */
   ASPNavigator( ASPPage page )
   {
      super(page);
   }

   ASPNavigator construct()
   {
      ASPConfig cfg               = getASPPage().getASPConfig();
      background_color            = "pageBody";
      navigator_style             = cfg.getParameter("NAVIGATOR/STYLE","classic");
      folder_image_root           = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/ROOT",          "navigator_root_classic.gif");
      folder_image_node_opened    = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/NODE/OPENED",   "navigator_node_opened_classic.gif");
      folder_image_node_closed    = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/NODE/CLOSED",   "navigator_node_closed_classic.gif");
      folder_image_item           = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/ITEM/PAGE",     "navigator_web_page_classic.gif");
      folder_image_url            = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/ITEM/URL",      "navigator_url_classic.gif");   
      vertical_line               = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/VER_LINE",      "navigator_ver_line_classic.gif");   
      folder_area_width           = cfg.getParameter("NAVIGATOR/"+navigator_style+"/AREA/WIDTH",          "700");
      folder_area_height          = cfg.getParameter("NAVIGATOR/"+navigator_style+"/AREA/HEIGHT",         "20");
      folder_link_color_unvisited = cfg.getParameter("NAVIGATOR/"+navigator_style+"/LINK/COLOR/UNVISITED","#000000");
      folder_link_color_visited   = cfg.getParameter("NAVIGATOR/"+navigator_style+"/LINK/COLOR/VISITED",  "#000000");
      folder_link_color_selected  = cfg.getParameter("NAVIGATOR/"+navigator_style+"/LINK/COLOR/SELECTED", "#000000");
      left_position               = cfg.getParameter("NAVIGATOR/POSITION/LEFT", "10");
      top_position                = cfg.getParameter("NAVIGATOR/POSITION/TOP",  "80");
      nav_exp_image               = cfg.getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/EXPORT",          "exp_nav.png");
      
      default_mode = false;

      return this;
   }
   
   private String fixColor( String color )
   {
      if(!Str.isEmpty(color) && color.startsWith("#"))
         return color.substring(1);
      else
         return color;
   }

   /**
    * Private constructor used by clone()
    * @param navigator Referance to the Navigator.
    */
   ASPNavigator construct( ASPNavigator navigator )
   {
      this.root                        = navigator.root;
      this.def_menu_type               = navigator.def_menu_type;
      this.background_color            = navigator.background_color;
      this.folder_image_root           = navigator.folder_image_root;
      this.folder_image_node_opened    = navigator.folder_image_node_opened;
      this.folder_image_node_closed    = navigator.folder_image_node_closed;
      this.folder_image_item           = navigator.folder_image_item;
      this.folder_image_url            = navigator.folder_image_url;
      this.folder_area_width           = navigator.folder_area_width;
      this.folder_area_height          = navigator.folder_area_height;
      this.folder_font_face            = navigator.folder_font_face;
      this.folder_font_size            = navigator.folder_font_size;
      this.folder_font_color           = navigator.folder_font_color;
      this.folder_link_color_unvisited = navigator.folder_link_color_unvisited;
      this.folder_link_color_visited   = navigator.folder_link_color_visited;
      this.folder_link_color_selected  = navigator.folder_link_color_selected;
      this.navigator_style             = navigator.navigator_style;
      this.vertical_line               = navigator.vertical_line;
      this.left_position               = navigator.left_position;
      this.top_position                = navigator.top_position;
      this.nav_exp_image               = navigator.nav_exp_image;
      
      if (navigator.views!=null)
         this.views = navigator.views;

      if (navigator.presobj!=null)
         this.presobj = navigator.presobj;

      this.security_info = null;

      this.default_mode = navigator.default_mode;

      return this;
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze() throws FndException
   {
      //root.freeze();
   }

   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
      //root.reset();
   }

   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      security_info = null;
      out.clear();
      menu_type = null;
      main_menu = false;
      area_width   = null;
      area_height  = null;
      font_name = null;
      font_size = null;
      font_color = null;
      image_root = null;
      image_node = null;
      image_item = null;
      link_color  = null;
      vlink_color = null;
      alink_color = null;      
   }

   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      // ?? no cloninig needed on nodes or security info ??
      ASPNavigator nav = (new ASPNavigator((ASPPage)page)).construct(this);
      nav.setCloned();
      return nav;
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
   //  Public interface for using from ASP pages (used only in UNDEFINED state)
   //==========================================================================

   /**
    * Sets a given node as root node in the navigator.
    * @param node Node to be set as the root of the navigator
   */
   public void setRoot( ASPNavigatorNode node )
   {
      try
      {
         modifyingImmutableAttribute("NAVIGATOR_ROOT");
         if(DEBUG) debug("ASPNavigator.setRoot("+node.getLabel()+")");
         if( Str.isEmpty(node.getImage()) )
             node.setImage(folder_image_root);
         this.root = node;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets the Navigator in default mode
    */
   public void setDefaultMode()
   {
      try
      {
         default_mode = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Package functions
   //==========================================================================

   /**
    * Check security for an given Presentation Object
   */
   private boolean accessAllowed2( String pobj )
   {
      if(DEBUG) debug("ASPNavigator.accessAllowed2("+pobj+")");

      if ( Str.isEmpty(pobj) ) return true;

      int pos = pobj.lastIndexOf("/");
      if(pos>=0)
      {
         String module = pobj.substring(0,pos).toUpperCase();
         pobj = pobj.substring(pos+1);
         if(DEBUG) debug("  module='"+module+"', object='"+pobj+"'");
         if( "COMMON/SCRIPTS".equals(module) )
            module = "FND";
         pobj = module+"/"+pobj;
      }

      for(pos=0; pos<security_info.countItems(); pos++)
         if(pobj.equals(security_info.getNameAt(pos))) return true;

      return false;
   }

   /**
    * Check security for a given view list
    */
   private boolean accessAllowed( String viewlist )
   {
      if ( Str.isEmpty(viewlist) ) return true;

      StringTokenizer st = new StringTokenizer( viewlist, ", \t\n\r" );
      while ( st.hasMoreTokens() )
         if ( !security_info.itemExists(st.nextToken()) ) return false;

      return true;
   }

   /**
    * Check security and format suitable output string for Tree 
    */
   boolean formatItemEntry( int    level,
                            int    parentpos,
                            int    posno,
                            String label,
                            String target,
                            String image,
                            String view,
                            boolean    bookmark,
                            AutoString out )
   {
      return formatItemEntry(level, parentpos, posno, label, target, image, view, bookmark, out, "", false );
   }

   
   /**
    * Check security and format suitable output string for Tree 
    */
   boolean formatItemEntry( int    level,
                            int    parentpos,
                            int    posno,
                            String label,
                            String target,
                            String image,
                            String view,
                            boolean    bookmark,
                            AutoString out,
                            String plsql_method,
                            boolean stw_page)
   {
      if(DEBUG) debug("ASPNavigator.formatItemEntry("+level+","+parentpos+","+posno+","
                                                     +label+","+target+","+image+","+view+")");
      if(security_info==null) return false;
      
      // Added by Terry 20120822
      // Add frameset in IFS, Control Nav access
      String target_;
      // Added end

      if(DEBUG) debug("Checking navigator security for '"+label+"' ["+view+"] at level "+level);

      if(target != null && default_mode == true)
      {
         if(stw_page && !Str.isEmpty(plsql_method) && !getASPPage().isObjectAccessible(plsql_method))
            return false;
         
         // Added by Terry 20120822
         // Add frameset in IFS, Control Nav access
         if (target.contains("IFS_MAIN_FRAME"))
            target_ = target.substring(0, target.length() - 26);
         else
            target_ = target;
         // Added end
         
         // Modified by Terry 20120822
         // Add frameset in IFS, Control Nav access
         // Original: String ext = target.substring(target.lastIndexOf(".")+1);
         String ext = target_.substring(target_.lastIndexOf(".") + 1);
         // Modified end
         
         if((ext.equalsIgnoreCase("asp") || ext.equalsIgnoreCase("page")) && target.indexOf("?")==-1)
         {
            // Modified by Terry 20120822
            // Add frameset in IFS, Control Nav access
            // Original: if ( !accessAllowed2(target) || !accessAllowed(view) )
            if ( !accessAllowed2(target_) || !accessAllowed(view) )
               return false;
            // Modified end
         }

         // Modified by Terry 20120822
         // Add frameset in IFS, Control Nav access
         // Original: if(target.indexOf("?")!=-1)
         // Original: {               
         // Original:    if ( !accessAllowed2(target.substring(0, target.indexOf("?"))) || !accessAllowed(view) )
         // Original:       return false;
         // Original: }
         if(target_.indexOf("?") != -1)
         {
            if ( !accessAllowed2(target_.substring(0, target_.indexOf("?"))) || !accessAllowed(view) )
               return false;
         }
         // Modified end
      }

      if(bookmark)
      {
         if(main_menu)
         {
            if(target==null)
            {
               if(level==0)
               {
                   out.append("<TITLE>",label,"</TITLE>",CRLF);
                   out.append("<H1>",label,"</H1>",CRLF);
               }
               else
               {
                   out.append("<DT><H3 FOLDED ADD_DATE=\"0\">",label,"</H3>",CRLF);
                   out.append("<DL><P>",CRLF);
               }
            }
            else if(parentpos>=0)
            {
                if ("http://".equalsIgnoreCase(target.substring(0,7)))
                   out.append("<DT><A HREF=\"",target,"\" ADD_DATE=\"0\" LAST_VISIT=\"0\" LAST_MODIFIED=\"0\">",label);
                else
                {
                    ASPConfig cfg = getASPManager().getASPConfig();
                    String app_root = cfg.getProtocol()+"://"+cfg.getApplicationDomain()+cfg.getRootLocation(false);
                    out.append("<DT><A HREF=\"",app_root,target,"\" ADD_DATE=\"0\" LAST_VISIT=\"0\" LAST_MODIFIED=\"0\">",label);
                }
                out.append("</A>");
                out.append(CRLF);
            }
         }
      }
      else
      {
         if(main_menu)
         {
            out.append("List");
            String image_loc = getASPManager().getASPConfig().getImagesLocationWithRTL();
           
            if(target==null)
            {
               out.appendInt(posno);
               
               if (getASPManager().isRTL())
               {
                  int width = (Str.toInt(area_width)*2); 
                  out.append("=new List(", level==0?"true,":"false,", Integer.toString(width), ",", area_height);               
               }
               else
               out.append("=new List(", level==0?"true,":"false,", area_width, ",", area_height);
               out.append(",\"", background_color, "\",\"", image_loc, "\",\"");
               out.append(menu_type,"\");",CRLF);
               if(level==0)
               {
                  out.append("List0.addItem(\"","","\");",CRLF);
                  out.append("List0.addItem(\"",label,"\");",CRLF);
            	}
			   }
            else if(parentpos>=0)
            {
               out.appendInt(parentpos);
               String alignment = "left";
               if (getASPManager().isRTL())
                  alignment = "right";
               
               out.append(".addItem(\"<table border=0 cellspacing=0 cellpadding=0><tr><td valign=middle align="+alignment+">");
               out.append("<a href='",getASPManager().correctURL(target),"'>");
               out.append("<img src='",image_loc,image,"' border='0'></a></td>");

               out.append("<td valign=middle align= align="+alignment+" width=100%>");
               out.append("<a class='navigatorPageNode' href='",getASPManager().correctURL(target)+"'>&nbsp;",label,"</a></td></tr></table>\");",CRLF);                
            }
         }
         else
         {
            out.appendInt(level);
            out.append(" \"",label,"\" \"",target,"\" ");
            out.append(level==0?"1":"0"," \"",image,"\"",CRLF);
         }
      }
      return true;
   }

   void formatItemEntry2(int parentpos, int posno, String label, boolean bookmark, AutoString out)
   {
      if(DEBUG) debug("ASPNavigator.formatItemEntry2("+parentpos+","+posno+","+label+")");

      if(bookmark && main_menu && (posno>0))
         out.append("</DL><P>",CRLF);
      else if(!bookmark && main_menu)
      {
         out.append("List");
         out.appendInt(posno);
         out.append(".setFont(\"<FONT class='navigatorRootNode'");
         out.append(">\", \"</FONT>\");",CRLF);
         if(posno>0)
         {
            out.append("List");
            out.appendInt(parentpos);
            out.append(".addList(List");
            out.appendInt(posno);
            out.append(",\"<font class=navigatorFolderNode>",label,"</font>\");",CRLF);
         }
      }
   }
   
   boolean formatXmlEntry(int level,
                          int parentpos,
                          int posno,
                          String label,
                          String target,
                          boolean toXml,
                          String view,
                          AutoString out,
                          String plsql_method,
                          boolean stw_page)
   {
      if(DEBUG) debug("ASPNavigator.formatXmlEntry("+level+","+parentpos+","+posno+","
                                                     +label+","+target+","+toXml+","+view+")");
      if(security_info==null) return false;

      if(DEBUG) debug("Checking navigator security for '"+label+"' ["+view+"] at level "+level);

      if(target != null && default_mode == true)
      {
         if(stw_page && !Str.isEmpty(plsql_method) && !getASPPage().isObjectAccessible(plsql_method))
            return false;
            
         String ext = target.substring(target.lastIndexOf(".")+1);
         if((ext.equalsIgnoreCase("asp") || ext.equalsIgnoreCase("page")) && target.indexOf("?")==-1)
         {
            if ( !accessAllowed2(target) || !accessAllowed(view) )
               return false;
         }
         if(target.indexOf("?")!=-1)
         {               
            if ( !accessAllowed2(target.substring(0, target.indexOf("?"))) || !accessAllowed(view) )
               return false;
         }
      }
      if(toXml)
      {
         if(main_menu)
         {
            if(target==null)
            {
               if(level==0)
               {
                   out.append("<Node>");
                   out.append("<Name>",label,"</Name>");
                   out.append("<MDI>webNavigator</MDI>");
               }
               else
               {
                   out.append("<Nodes>");
                   out.append("<Name>",label,"</Name>");
               }
            }
            else if(parentpos>=0)
            {
               
               out.append("<Node>");
               out.append("<Name>",label,"</Name>");
               target = target.replaceAll("&", "&amp;");
               if ("http://".equalsIgnoreCase(target.substring(0,7)))
                  out.append("<Action>",target,"</Action>");
               else
               {
                  out.append("<Action>",target,"</Action>");
               }
               String pres_obj = getPresObj(target);
               if(pres_obj !=null && !pres_obj.equals(""))
               {
                  out.append("<PresentationObject>",pres_obj,"</PresentationObject>");
               }               
               out.append("</Node>");
            }
         }
      }
      return true;
   }
   
   void formatXmlEntry2(  int parentpos,
                          int posno,
                          String label,
                          boolean toXml,
                          AutoString out)
   {
      if(DEBUG) debug("ASPNavigator.formatXmlEntry2("+parentpos+","+posno+","+label+")");

      if(toXml && main_menu && (posno>0))
         out.append("</Nodes>");
   }

   String getNodeImage()
   {
      return image_node;
   }

   String getItemImage()
   {
      return image_item;
   }

   /**
    * Closes log, check security and display navigator.
    * Called from ASPPage.
    */
   void generate() throws FndException
   {
      if (DEBUG) debug("ASPNavigator.generate()");
      ASPManager mgr = getASPManager();

      if(mgr.isMobileVersion())
      {
         generateMobileNav();
         return;
      }

      if (root!=null)
      {
         // Check security
         ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();

         // add VIEW based security query
         if(views==null)
         {
            views = new AutoString();
            synchronized(views)
            {
               root.getViewList(views);
            }
            if(DEBUG) debug("Navigator view list for security check: "+views);
         }
         buf.addSecurityQuery(views.toString());

         // add Presentation Object based security query
         if(presobj==null)
         {
            presobj = new AutoString();
            synchronized(presobj)
            {
               root.getTargetList(presobj);

               String targets = presobj.toString();
               presobj.clear();

               StringTokenizer st = new StringTokenizer(targets, ",");
               while( st.hasMoreTokens() )
               {
                  String pobj = st.nextToken();
                  
                  if( pobj!=null && (pobj.toLowerCase().endsWith("asp") || pobj.toLowerCase().endsWith("page")))
                  {
                     int pos = pobj.lastIndexOf("/");
                     if(pos>=0)
                     {
                        String module = pobj.substring(0,pos).toUpperCase();
                        pobj = pobj.substring(pos+1);
                        if(DEBUG) debug("  module='"+module+"', object='"+pobj+"'");
                        if( "COMMON/SCRIPTS".equals(module) )
                           module = "FND";
                        presobj.append(module,"/");
                     }
                     presobj.append(pobj,",");
                  }
               }
            }
            if(DEBUG) debug("Navigator presentation object list for security check: "+presobj);
         }
         buf.addPresentationObjectQuery(presobj.toString());
         buf = mgr.perform(buf);

         security_info = buf.getSecurityInfo();

         if(DEBUG) security_info.traceBuffer("Navigator [security_info]");

         // generate the output
         out.clear();
         if(main_menu)
         { 
            if(!JS_FOLDER.equals(menu_type) && !BOOKMARK.equals(menu_type) && !TOXML.equals(menu_type) )
               menu_type = def_menu_type;
 
            if(!(getASPManager().isExplorer()||getASPManager().isMozilla()) && !BOOKMARK.equals(menu_type) && !TOXML.equals(menu_type) )
            {
               out.append("<html><head>");
               out.append(mgr.generateHeadTag(getTitleName()));
               //out.append("<link rel=\"STYLESHEET\" href=\"" + mgr.getConfigParameter("APPLICATION/LOCATION/STYLESHEETS") + "Netscape.css\" type=\"text/css\">");
               out.append(getASPManager().getStyleSheetTag());
               out.append("</head>\n");
            }
            else if (!BOOKMARK.equals(menu_type) && !TOXML.equals(menu_type))
            {
               out.append("<html><head>");
               out.append(mgr.generateHeadTag(getTitleName()));
               out.append("</head>");
            }

            if( JS_FOLDER.equals(menu_type) )
            {
               area_width  = folder_area_width;
               area_height = folder_area_height;
               font_name   = folder_font_face;
               font_size   = folder_font_size;
               font_color  = folder_font_color;
               image_root  = folder_image_root;
               image_node  = folder_image_node_closed;
               image_item  = folder_image_item;
               link_color  = folder_link_color_unvisited;
               vlink_color = folder_link_color_visited;
               alink_color = folder_link_color_selected;
               generateJavaScriptHTML();
            }
            else if( BOOKMARK.equals(menu_type) )
            {
                generateBookmarkFile();
            }
            else if(TOXML.equals(menu_type))
            {
               generateXmlFile();
            }
            else if( APPLET.equals(menu_type) )
               throw new FndException("FNDNAVENOAPPLET: Applet based navigator is not supported.");
            else
               throw new FndException("FNDNAVETYPE: Unknown navigator type '&1'.",menu_type);

            String script_loc = getASPManager().getASPConfig().getParameter("APPLICATION/LOCATION/SCRIPTS","/&(APPLICATION/PATH)/common/scripts/");
            String javascript_file_name = getASPManager().getASPConfig().getParameter("NAVIGATOR/JAVASCRIPT_FILE_NAME","navigator.js");

            if(!TOXML.equals(menu_type))
            {
               out.append("<script language=JavaScript src=",script_loc,javascript_file_name,"></script>\n");
               out.append("</body></html>\n");
            }
         }
         else
         {
            font_name  = folder_font_face;
            font_size  = folder_font_size;
            image_node = folder_image_node_opened;
            image_item = folder_image_item;
            root.show(0, this, -1, 0, false, out);
         }
         if(DEBUG) debug("ASPNavigator output:\n\n"+out+"\n\n");

         mgr.writeResponse( out.toString() );
      }


   }

   /**
    * Generates the output for import to browser bookmarks.
    */

   private void generateBookmarkFile()
   {
       ASPManager mgr = getASPManager();
       out.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>",CRLF);
       
       if(mgr.isExplorer())
       {
          String user_lan = mgr.getUserPreferredLanguage();
          String charset = mgr.getASPConfig().getParameter("LANGUAGE/"+user_lan+"/EXPORT_NAVIGATOR_CHARSET","UTF-8");
          out.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=",charset,"\">",CRLF);
       }
       else
          out.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">",CRLF);
       
       root.show(0, this, -1, 0, true, out);
       out.append("</DL>",CRLF);
   }
   
   private void generateXmlFile()
   {
      out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",CRLF);
      root.show(0, this, -1, 0, out, true);
      out.append("</Node>");
      
   }

   private String getTitleName()
   {
      return getASPManager().translateJavaText("FNDNAVTITLE: Tree Navigator");
   }

   //==========================================================================
   //  New methods for Java Script Javigator.
   //==========================================================================
   private void generateJavaScriptHTML()
   {
      ASPManager mgr = getASPManager();

      String rawversion = mgr.getBrowserVersion();
      boolean  nice_Navigator = false;
      if(!mgr.isExplorer() && rawversion.startsWith("Mozilla/4."))
          {
              rawversion = rawversion.substring(10,12);
              rawversion = Str.replace(rawversion," ","0");
              if(Integer.parseInt(rawversion)>=70)
                  nice_Navigator=true;
          }

      out.append("<script language=javascript>\n");
      
      out.append("var VERTICAL_LINE_IMG = \"",vertical_line,"\";\n");
      out.append("var WEB_PAGE_IMG = \"",folder_image_item,"\";\n");
      out.append("var WEB_URL_IMG = \"",folder_image_url,"\";\n");
      out.append("var FOLDER_OPENED_IMG = \"",folder_image_node_opened,"\";\n");
      out.append("var FOLDER_CLOSED_IMG = \"",folder_image_node_closed,"\";\n");
      out.append("var ROOT_IMG          = \"",image_root,              "\";\n");
      out.append("var WRITING_LIST_MSG  = \"");
      out.append( mgr.translateJavaText("FNDNAVLISTMSG: List: Writing list..."), "\";\n");
      out.append("var DEBUG             = false;\n");
      
      String loc = mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR");
      loc = loc.substring(0,loc.indexOf("?"));
      // Modified by Terry 20120822
      // Add frameset in IFS, control exp nav link.
      // Original: out.append("var EXP_NAV_URL = \"common/scripts/ExportNavigator.page\";\n");
      out.append("var EXP_NAV_URL = \"common/scripts/ExportNavigator.page ' target='IFS_MAIN_FRAME'\";\n");
      // Modified end
      out.append("var EXP_NAV_IMG          = \"",nav_exp_image,"\";\n");
      out.append("var EXP_NAV_MSG  = \"");
      out.append( mgr.translateJavaText("FNDNAVLISTEXP: Export Navigator to Favorites"), "\";\n");
      
      out.append("function initJSNavigator()\n");
      out.append("{\n");
      int items_count = root.show(0, this, -1, 0, false, out);
      if(!getASPManager().isRWCHost() && (getASPManager().isExplorer() || nice_Navigator || getASPManager().isMozilla()))
          out.append("List0.build("+left_position+","+top_position+");",CRLF);
      else
          out.append("List0.build(10,20);",CRLF);
      out.append("removePageMask();\n");
      out.append("}\n");
      out.append("</script>\n");

      out.append("<STYLE TYPE=\"text/css\">");
      out.append("\n\t<!-- A:link    {text-decoration: none}");
      out.append("\n\t     A:visited {text-decoration: none}");
      out.append("\n\t     A:active  {text-decoration: none}");
      out.append("\n\t     A:hover   {text-decoration: none} -->\n");

      out.append("\t#spacer {background-color: transparent; position:absolute; height:");
      out.appendInt(Integer.parseInt(top_position)+(items_count * (Integer.parseInt(area_height)-10)));
      out.append("; }",CRLF);
      if( mgr.isExplorer() || mgr.isMozilla())
      {
         for(int i=0; i<=items_count; i++)
         {
            out.append("\t#lItem");
            out.appendInt(i);
            out.append(" { background-color: transparent; position:absolute; }",CRLF);
         }
      }
      out.append("\n</STYLE>\n");
      out.append("<body topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" class=\"", background_color);
      out.append("\" text=\"",       font_color);
      out.append("\" link=\"",       link_color);
      out.append("\" vlink=\"",      vlink_color);
      out.append("\" alink=\"",      alink_color);
      out.append("\" onLoad =\"javascript:initJSNavigator();startBroadcasrMsgs();\">\n");
      
      ASPPage pg = mgr.getASPPage();
      pg.disableConfiguration();

      if(!mgr.isNetscape4x())
         out.append(mgr.generatePageMaskTag());

      if(mgr.isExplorer() || nice_Navigator || mgr.isMozilla())
      {
          out.append("\n<form ");
          out.append(mgr.generateFormTag(),">\n");
          out.append(mgr.startPresentation(getTitleName()));
      }

      out.append("<table border=0 width=100% height="+(Integer.parseInt(top_position)+(items_count * (Integer.parseInt(area_height)-1)))+"><tr><td>\n");
      out.append("<DIV ID=\"spacer\"></DIV>\n");
      out.append("</td></tr></table>\n");

      if( mgr.isExplorer() || mgr.isMozilla())
      {
         for (int i=0; i<=items_count; i++)
         {
            out.append("\t<DIV ID=\"lItem");
            out.appendInt(i);
            out.append("\" NAME=\"lItem");
            out.appendInt(i);
            out.append("\"></DIV>",CRLF);
         }
      }
      if(mgr.isExplorer() || nice_Navigator || mgr.isMozilla())
      {
              out.append(mgr.endPresentationSimple()); //!!!
              out.append("<script language=JavaScript>SEARCHBASE = '"+mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR")+"';</script>");
              out.append("</form>");
      }

   }

   /**
   * Sets the type of the Menu
   * @param type should be one of the following pre-defined constants<pre>
   *        BOOKMARK - Menu will be included in the Bookmarks
   *        TOXML    - Menu will be included in the xml file
   *        JS_FOLDER- Menu will be implemented in Java Script in Folder style</pre>     
   */   
   public void setMenuType( String type )
   {
      this.menu_type = type;
      this.main_menu = true;
   }
   
   private String getPresObj( String target )
   {
      if(DEBUG) debug("ASPNavigator.getPresObj("+target+")");
      
      int pagePos = target.lastIndexOf("?");
      if(pagePos>=0)
         target = target.substring(0,pagePos);

      if ( Str.isEmpty(target) || !target.toLowerCase().endsWith("page"))
         return null;

      int pos = target.lastIndexOf("/");
      if(pos>=0)
      {
         String module = target.substring(0,pos).toUpperCase();
        
         target = target.substring(pos+1);
         if(DEBUG) debug("  module='"+module+"', object='"+target+"'");
         if( "COMMON/SCRIPTS".equals(module) )
            module = "FND";
         target = module+"/"+target;
      }
      return target;
   }
   
   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   void generateMobileNav() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (root!=null)
      {
         // Check security
         ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();

         // add VIEW based security query
         if(views==null)
         {
            views = new AutoString();
            synchronized(views)
            {
               root.getViewList(views);
            }
            if(DEBUG) debug("Navigator view list for security check: "+views);
         }
         
         buf.addSecurityQuery(views.toString());

         // add Presentation Object based security query
         if(presobj==null)
         {
            presobj = new AutoString();
            synchronized(presobj)
            {
               root.getTargetList(presobj);

               String targets = presobj.toString();
               presobj.clear();

               StringTokenizer st = new StringTokenizer(targets, ",");
               while( st.hasMoreTokens() )
               {
                  String pobj = st.nextToken();
                  
                  if( pobj!=null && pobj.toLowerCase().endsWith("page"))
                  {
                     int pos = pobj.lastIndexOf("/");
                     if(pos>=0)
                     {
                        String module = pobj.substring(0,pos).toUpperCase();
                        pobj = pobj.substring(pos+1);
                        if(DEBUG) debug("  module='"+module+"', object='"+pobj+"'");
                        if( "COMMON/MOBILESCRIPTS".equals(module) )
                           module = "FND";
                        presobj.append(module,"/");
                     }
                     presobj.append(pobj,",");
                  }
               }
            }
            
            if(DEBUG) debug("Navigator presentation object list for security check: "+presobj);
         }
         buf.addPresentationObjectQuery(presobj.toString());
         buf = mgr.perform(buf);

         security_info = buf.getSecurityInfo();

         if(DEBUG) security_info.traceBuffer("Navigator [security_info]");

         // generate the output
         out.clear();
         
         out.append("<html>\n");
         out.append("<head>\n");
         out.append(mgr.generateHeadTag(getTitleName()));
         out.append("</head>\n");      
         out.append("<body topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" class=\"", background_color,"\">\n");
         out.append(mgr.startPresentation(getTitleName()));
         if(root!=null)
         {
            root.showMobile(0, this);
         }
         out.append(mgr.endPresentationSimple()); //!!!
         out.append("</body>");
         out.append("</html>\n");
         mgr.writeResponse( out.toString() );
      }
      
   }
   
   void formatMobileEntry(int level, String label, String target)
   {
      ASPManager mgr = getASPManager();
      String imageLoc = mgr.getASPConfig().getMobileImageLocation();
      int nextLineGap = 0;
      
      if(mgr.isEmpty(target))
      {
         if(level==0)
            out.append("<img align=\"absmiddle\" src=\""+imageLoc+"/navigator_root.gif\">",formatMobileEntryGap(1));
         else
         {
            for(int s=0; s<level; s++)
               out.append(formatMobileEntryGap(2));
            
            out.append(formatMobileEntryGap(level-1));
            out.append("<img align=\"absmiddle\" src=\""+imageLoc+"/navigator_node_minus.gif\">",formatMobileEntryGap(1));
            out.append("<img align=\"absmiddle\" src=\""+imageLoc+"/navigator_node_folder.gif\">",formatMobileEntryGap(1));
            
         }
         out.append("<font class=\"normalTextLabel\">",label,"</font><br>\n");
      }
      else
      {
         out.append(formatMobileEntryGap(5));
         for(int s=1; s<level; s++)
            out.append(formatMobileEntryGap(3));
         out.append("<img align=\"absmiddle\" src=\""+imageLoc+"/navigator_web_page.gif\">",formatMobileEntryGap(1));
         out.append("<a href=\"",mgr.getApplicationAbsolutePath()+"/"+target,"\" class=\"normalTextValue\">",label,"</a><br>\n");
      }
   }

   /**
    * This method will write a 5px image on to the html code
    */
   private String formatMobileEntryGap(int count)
   {
      ASPManager mgr = getASPManager();
      String imageLoc = mgr.getASPConfig().getMobileImageLocation();
      String gap = "";
      for(int c=0; c<count; c++)
         gap += "<img align=\"absmiddle\" src=\""+imageLoc+"/navigator_gap.gif\">"; // 5px Image
      return gap;
   }
}
