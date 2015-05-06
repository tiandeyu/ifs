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
 * File        : TreeList.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H    2002-08-26  created.
 *    Ramila H    2002-09-04  Added RMB's to the tree.
 *    Ramila H    2003-06-19  Added target to root, and changed popup style.
 *    Suneth M    2003-07-22  Log Id 1122, Changed show() to support Netscape in LINUX.
 *    Ramila H    2003-08-12  Log Id 1079, added target to root.
 *    Ramila H    2003-08-12  added method to display header, commandbar and
 *                            enable/disable root node.  
 *    Ramila H    2003-08-14  split show into four methods for more customizing.  
 *                            added call to onLoad in clientscipt.js
 *                            fixed the size of the scrollbars.
 *                            added a method to initially expand a single node.    
 *    Ramila H    2003-08-28  Fixed problem with Netscape in Linux.
 *    Ramila H    2003-10-16  Called id 108000. Add method get/setHeadTitle. set header_title to HEAD tag.
 *    Chandana D  2004-02-13  Bug 42727, fixed.
 *    Chandana D  2004-05-12  Updated for the use of new style sheets.
 *    Ramila H    2004-05-18  Added generation of hidden fields
 *    Chandana D  2004-05-19  Modified generateDefinition() to support Linux-Mozilla. Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Chandana D  2004-06-10  Removed all absolute URLs.
 *    Ramila H    2004-07-12  Added RMB functionality to root.
 *    Prabha R    2005-20-12  Changed the List.addList(..) to take in two strings. This is done to improve the appearance of the tree
 *    Prabha R    2006-04-20  Bug 57019 Extra apostrophe appearing in the tree 
 *    Buddika H   2006-12-08  Bug 62316, Modified generateTreeScripts() to replace deprecated captureEvent() js method.
 *    Buddika H   2007-01-30  Bug 62316, Bug 63250, Omproved theming support in IFS clients.
 *    Sasanka D   2007-02-19  Bug 63561, Added theming support to change background color according to the selected theme.
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.io.*;
import java.util.*;
//Bug 42727, start
import java.awt.*;
//Bug 42727, end

public class TreeList
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.TreeList");
   public static final String  CRLF = "\r\n";
   public static final String  JS_ARROW  = "JAVASCRIPT_ARROW";
   public static final String  JS_FOLDER = "JAVASCRIPT_FOLDER";
   public static final String THEME_BGCOLOR = "THEME_BGCOLOR";

   private ASPPage page;
   private TreeListNode root;

   private String javascript_filename = "list.js";
   private String script_loc;
   private String image_loc;
   private String menu_type;
   private String folder_image_root;
   private String folder_image_node_opened;
   private String folder_image_node_closed;
   private String body_background_color;
   private String background_color;
   private String folder_font_color;
   private String folder_area_width;
   private String folder_area_height;
   private String folder_font_face;
   private String folder_font_size;
   private String folder_link_color_unvisited;
   private String folder_link_color_visited;
   private String folder_link_color_selected;

   private int xPos = 10;
   private int yPos = 10;
   
   private String base_target = "main";

   private String menu_image;
   private String onmouseover;
   private String onmouseout;
   //private MenuItem menu_items;
   private AutoString menu_str = new AutoString();
   private AutoString hide_str = new AutoString();
   
   private boolean show_header;
   private boolean show_cmdbar;
   private boolean custom_tree_positioned;
   private String header_title;
   private String cmdbar_title;
   
   private boolean disable_root;
   private int items_count;
   private int expanded_node;
   
   private Vector hidden_field;

   //private int items_count;
   //private AutoString out;

   /**
    * Return a reference to a new instance of TreeList
    * @param manager the current ASPManager object.
    * The label of the root node will be 'Root'.
    * @see ifs.fnd.asp.TreeList.setLabel
    * @see ifs.fnd.asp.TreeList.setTarget
    * @see ifs.fnd.asp.TreeList.setImage
    */
   public TreeList(ASPManager manager)
   {
      page = manager.getASPPage();
      initValues(manager);
      root = new TreeListNode(manager.translate("FNDTREELISTROOT: Root"),null,folder_image_root);
   }

   /**
    * Return a reference to a new instance of TreeList
    * @param manager the current ASPManager object.
    * @param label translated string for root node's label.
    * @see ifs.fnd.asp.TreeList.setLabel
    * @see ifs.fnd.asp.TreeList.setTarget
    * @see ifs.fnd.asp.TreeList.setImage
    */
   public TreeList(ASPManager manager, String label)
   {
      page = manager.getASPPage();
      initValues(manager);      
      root = new TreeListNode(label,null,folder_image_root);
   }
   
   /**
    * Return a reference to a new instance of TreeList
    * @param manager the current ASPManager object.
    * @param label translated string for root node's label.
    * @param target url string denoting the target of the root link.
    * @see ifs.fnd.asp.TreeList.setLabel
    * @see ifs.fnd.asp.TreeList.setTarget
    * @see ifs.fnd.asp.TreeList.setImage
    */
   public TreeList(ASPManager manager, String label, String target )
   {
      page = manager.getASPPage();
      initValues(manager);      
      root = new TreeListNode(label,target,folder_image_root);
   }

   /**
    * Return a reference to a new instance of TreeList
    * @param manager the current ASPManager object.
    * @param label translated string for root node's label.
    * @param target url string denoting the target of the root link.
    * @see ifs.fnd.asp.TreeList.setLabel
    * @see ifs.fnd.asp.TreeList.setTarget
    * @see ifs.fnd.asp.TreeList.setImage
    */
   public TreeList(ASPManager manager, String label, String target, String image )
   {
      page = manager.getASPPage();
      initValues(manager);  
      folder_image_root = image;
      root = new TreeListNode(label,target,folder_image_root);
   }
   
   private void initValues(ASPManager mgr)
   {
      ASPConfig cfg  = mgr.getASPConfig();
      script_loc     = cfg.getScriptsLocation();
      image_loc      = cfg.getImagesLocationWithRTL();
      menu_type      = JS_FOLDER;
      folder_image_root           = image_loc + cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/IMAGE/ROOT",          "navigator_root.gif");
      folder_image_node_opened    = image_loc + cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/IMAGE/NODE/OPENED",   "tree_opened_line.gif");
      folder_image_node_closed    = image_loc + cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/IMAGE/NODE/CLOSED",   "tree_closed_line.gif");
      folder_area_width           = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/AREA/WIDTH",          "350");
      folder_area_height          = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/AREA/HEIGHT",         "20");
      folder_font_face            = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/FONT/FACE",           "Helvetica");
      folder_font_size            = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/FONT/SIZE",           "2");
      folder_font_color           = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/FONT/COLOR",          "#000000");
      folder_link_color_unvisited = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/LINK/COLOR/UNVISITED","#400040");
      folder_link_color_visited   = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/LINK/COLOR/VISITED",  "#400040");
      folder_link_color_selected  = cfg.getParameter("TREE_STRUCTURE/JAVASCRIPT_FOLDER/LINK/COLOR/SELECTED", "#FF0000");
      body_background_color = background_color = cfg.getParameter("TREE_STRUCTURE/BACKGROUND/COLOR", "FFFFFF");
      menu_image = image_loc + "Action.gif";
      onmouseover = mgr.getConfigParameter("POPUP/EXPLORER/ONMOUSEOVER","this.style.border='1px solid #989898'; this.style.background='#CACACA';");
      onmouseout = mgr.getConfigParameter("POPUP/EXPLORER/ONMOUSEOUT","this.style.border='1px solid #F0F0F0'; this.style.background='#F0F0F0';");
      
      hidden_field = new Vector();
   }

   /**
    * Set the label for the root node.
    * @param label translated string 
    * @see ifs.fnd.asp.TreeList.getLabel
    */
   public void setLabel(String label)
   {
     root.setLabel(label);
   }

   /**
    * get the label of the root node
    * @return the root node's label
    * @see ifs.fnd.asp.TreeList.setLabel
    */
   public String getLabel()
   {
      return root.getLabel();
   }
   
   /**
    * Set the target for the root node.
    * @param target target for the root node 
    * @see ifs.fnd.asp.TreeList#getTarget
    */
   public void setTarget(String target)
   {
     root.setTarget(target);
   }

   /**
    * Get the target of the root node.
    * @return the root node's target
    * @see ifs.fnd.asp.TreeList#setTarget
    */
   public String getTarget()
   {
     return root.getTarget();
   }

   /**
    * Set the image for the root node.
    * @param image image name, with path of root node.
    * @see ifs.fnd.asp.TreeList.getImage
    */
   public void setImage(String image)
   {
     folder_image_root = image;
   }

   /**
    * get the image of the root node
    * @return the image name and relative path of the root node
    * @see ifs.fnd.asp.TreeList.setImage
    */
   public String getImage()
   {
      return root.getImage();
   }
   
   /**
    * Set the image for folder opened state
    * @param image relative path image name
    * @see ifs.fnd.asp.TreeList.setFolderClosedImage
    * @see ifs.fnd.asp.TreeList.getFolderOpenedImage
    * @see ifs.fnd.asp.TreeList.getFolderClosedImage
    */
   public void setFolderOpenedImage(String image)
   {
      folder_image_node_opened = image;
   }

   /**
    * get folder opened state image
    * @return folder opened state image relative path image name
    * @see ifs.fnd.asp.TreeList.setFolderOpenedImage
    * @see ifs.fnd.asp.TreeList.setFolderClosedImage
    * @see ifs.fnd.asp.TreeList.getFolderClosedImage
    */
   public String getFolderOpenedImage()
   {
      return folder_image_node_opened;
   }
   
   /**
    * Set the image for folder closed state
    * @param image relative path image name
    * @see ifs.fnd.asp.TreeList.setFolderOpenedImage
    * @see ifs.fnd.asp.TreeList.getFolderOpenedImage
    * @see ifs.fnd.asp.TreeList.getFolderClosedImage
    */
   public void setFolderClosedImage(String image)
   {
      folder_image_node_closed = image;
   }

   /**
    * get folder closed state image
    * @return folder closed state image relative path image name
    * @see ifs.fnd.asp.TreeList.setFolderOpenedImage
    * @see ifs.fnd.asp.TreeList.setFolderClosedImage
    * @see ifs.fnd.asp.TreeList.getFolderOpenedImage
    */
   public String getFolderClosedImage()
   {
      return folder_image_node_closed;
   }
   
   
   /**
    * Set the x,y coordinate for the tree root. Default values (10,10)
    * @param x int value representing the root x-coordinate
    * @param y int value representing the root y-coordinate
    */
   public void setTreePosition(int x, int y)
   {
      xPos = x;
      yPos = y;
      
      custom_tree_positioned = true;
   }

   /**
    * Set the .js file to be used instead of the default list.js file found 
    * in common/scripts. Used in conjunction with setScriptLocation.
    * @param file_name .js file name to be used
    * @see ifs.fns.asp.setScriptLocation
    * @see ifs.fns.asp.getScriptFile
    * @see ifs.fns.asp.getScriptLocation
    */
   public void setScriptFile(String file_name)
   {
      javascript_filename = file_name;
   }

   /**
    * get the .js file name
    * @return .js file name used
    * @see ifs.fns.asp.setScriptFile
    * @see ifs.fns.asp.setScriptLocation
    * @see ifs.fns.asp.getScriptLocation
    */
   public String getScriptFile()
   {
      return javascript_filename;
   }
   
   /**
    * Set the location of the .js file to be used instead of the default list.js
    * file found in common/scripts. Used in conjunction with setScriptFile.
    * @param location locations of the .js file name to be used
    * @see ifs.fns.asp.setScriptFile
    * @see ifs.fns.asp.getScriptFile
    * @see ifs.fns.asp.getScriptLocation
    */
   public void setScriptLocation(String location)
   {
      script_loc = location;
   }

   /**
    * get the location of the .js file used 
    * @return location of the .js file used
    * @see ifs.fns.asp.setScriptFile
    * @see ifs.fns.asp.getScriptFile
    * @see ifs.fns.asp.setScriptLocation
    */
   public String getScriptLocation()
   {
      return script_loc;
   }

   /**
    * Set the area height of the tree
    * @param area_height int value of the tree area height
    * @see ifs.fnd.asp.getTreeAreaHeight
    * @see ifs.fnd.asp.setTreeAreaWidth
    * @see ifs.fnd.asp.getTreeAreaWidth
    */
   public void setTreeAreaHeight(int area_height)
   {
      folder_area_height = ""+area_height;
   }

   /**
    * get the area height of the tree
    * @return area height of the tree
    * @see ifs.fnd.asp.setTreeAreaHeight
    * @see ifs.fnd.asp.setTreeAreaWidth
    * @see ifs.fnd.asp.getTreeAreaWidth
    */
   public int getTreeAreaHeight()
   {
      return Integer.valueOf(folder_area_height).intValue();
   }
   
   /**
    * Set the area width of the tree
    * @param area_width int value of the tree area width
    * @see ifs.fnd.asp.setTreeAreaHeight
    */
   public void setTreeAreaWidth(int area_width)
   {
      folder_area_width = ""+area_width;
   }

   /**
    * get the area width of the tree
    * @return area width of the tree
    * @see ifs.fnd.asp.setTreeAreaHeight
    * @see ifs.fnd.asp.getTreeAreaHeight
    * @see ifs.fnd.asp.setTreeAreaWidth
    */
   public int getTreeAreaWidth()
   {
      return Integer.valueOf(folder_area_width).intValue();
   }

   /**
    * Set the base target for all links in the tree
    * @param target_form string value of the target HTML form name
    * @see ifs.fnd.asp.TreeList.getBaseTarget
    */
   public void setBaseTarget(String target_form)
   {
      base_target = target_form;
   }

   /**
    * get the base target for all links in the tree
    * @return the base target form name
    * @see ifs.fnd.asp.TreeList.setBaseTarget
    */
   public String getBaseTarget()
   {
      return base_target;
   }
   
   /**
    * Set the background color 
    * @param bgcolor color for the background
    * @see ifs.fnd.asp.TreeList.getBgColor
    */
   public void setBgColor(String bgcolor)
   {
      background_color = bgcolor;
   }

   /**
    * get the background color
    * @return the background color
    * @see ifs.fnd.asp.TreeList.setBgColor
    */
   public String getBgColor()
   {
      return background_color;
   }

   /**
    * Set the body background color 
    * @param body_background_color color for the background
    * @see ifs.fnd.asp.TreeList.getBgColor
    */
   public void setBodyBgColor(String body_background_color)
   {
      this.body_background_color = body_background_color;
   }

   /**
    * get the body background color
    * @return the background color
    * @see ifs.fnd.asp.TreeList.setBgColor
    */
   public String getBodyBgColor()
   {
      return body_background_color;
   }
   
   
   /**
    * Set the font size 
    * @param font_size int font size
    * @see ifs.fnd.asp.TreeList.getFontSize
    */
   public void setFontSize(int font_size)
   {
      folder_font_size = ""+font_size;
   }

   /**
    * get the Font size
    * @return int value of font size
    * @see ifs.fnd.asp.TreeList.setFontSize
    */
   public int getFontSize()
   {
      return Integer.valueOf(folder_font_size).intValue();
   }
   
   public void addDefinedPopup(String menu_item_status_str)
   {
      root.addDefinedPopup(menu_item_status_str);
   }

   /**
    * Enable header gif
    * @param title String translated title name
    * @see ifs.fnd.asp.TreeList.disableHeader
    * @see ifs.fnd.asp.TreeList.isHeaderEnabled
    * @see ifs.fnd.asp.TreeList.setHeadTitle
    * @see ifs.fnd.asp.TreeList.getHeadTitle
    */
   public void enableHeader(String title)
   {
      show_header = true;
      this.header_title = title;
   }

   /**
    * Disable header gif (default)
    * @see ifs.fnd.asp.TreeList.enableHeader
    * @see ifs.fnd.asp.TreeList.isHeaderEnabled
    */
   public void disableHeader()
   {
      show_header = false;
   }

   /**
    * Set the broswer title.
    * @param title String translated title name
    * @see ifs.fnd.asp.TreeList.getHeadTitle
    */
   public void setHeadTitle(String title)
   {
      this.header_title = title;
   }

   /**
    * Get the broswer title.
    * @see ifs.fnd.asp.TreeList.setHeadTitle
    */
   public String getHeadTitle()
   {
      return header_title;
   }
   
   
   /**
    * get the status of the header gif
    * @return boolean value of whether header gif is enable
    * @see ifs.fnd.asp.TreeList.enableHeader
    * @see ifs.fnd.asp.TreeList.disableHeader
    */
   public boolean isHeaderEnabled()
   {
      return show_header;
   }
   
   /**
    * Enable simple command bar
    * @param cmdbar_title String translatable title for simple command bar
    * @see ifs.fnd.asp.TreeList.disableSimpleCommandBar
    * @see ifs.fnd.asp.TreeList.isSimpleCommandBarEnabled
    */
   public void enableSimpleCommandBar(String cmdbar_title)
   {
      show_cmdbar = true;
      this.cmdbar_title = cmdbar_title;
   }

   /**
    * Disable simple command bar (default)
    * @see ifs.fnd.asp.TreeList.enableSimpleCommandBar
    * @see ifs.fnd.asp.TreeList.isSimpleCommandBarEnabled
    */
   public void disableSimpleCommandBar()
   {
      show_cmdbar = false;
   }
   
   /**
    * get the status of the simple command bar
    * @return boolean value of whether commandbar gif is enable
    * @see ifs.fnd.asp.TreeList.enableSimpleCommandBar
    * @see ifs.fnd.asp.TreeList.disableSimpleCommandBar
    */
   public boolean isSimpleCommandBarEnabled()
   {
      return show_cmdbar;
   }

   /**
    * Enable root node (default)
    * @see ifs.fnd.asp.TreeList.disableRoot
    * @see ifs.fnd.asp.TreeList.isRootEnabled
    */
   public void enableRoot()
   {
      disable_root = false;
   }

   /**
    * Disable root node 
    * @see ifs.fnd.asp.TreeList.enableRoot
    * @see ifs.fnd.asp.TreeList.isRootEnabled
    */
   public void disableRoot()
   {
      disable_root = true;
   }
   
   /**
    * get statuts of root 
    * @return boolean value of whether root node is enable
    * @see ifs.fnd.asp.TreeList.enableRoot
    * @see ifs.fnd.asp.TreeList.disableRoot
    */
   public boolean isRootEnabled()
   {
      return !disable_root;
   }

   /**
    * To have a single node expanded initially.
    * @param node_id int node id greater than zero.
    */
   public void initiallyExpandedNode(int node_id)
   {
      expanded_node = node_id;
   }
   
   /**
    * Add an instance of TreeListNode to the root
    * @param label translated label of node
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label )
   {
      return root.addNode(label);
   }

   /**
    * Add an instance of TreeListNode to the root
    * @param label translated label of node
    * @param target target url of node
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label, String target )
   {
      return root.addNode(label, target );
   }

   /**
    * Add an instance of TreeListNode to the root
    * @param label translated label of node
    * @param target target url of node
    * @param image relative path and image name of node
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label, String target, String image )
   {
      return root.addNode(label, target, image );
   }

   /**
    * Add an instance of TreeListNode to the root
    * @param label translated label of node
    * @param target target url of node
    * @param image relative path and image name of node
    * @param expand_data query string name value pairs used to get dynamic nodes.
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label, String target, String image, String expand_data )
   {
      return root.addNode(label, target, image, expand_data );
   }

   /**
    * Add an instance of TreeListItem to the root
    * @param label translated label of the item
    * @return The TreeListItem reference to the added item.
    */
   public TreeListItem addItem( String label)
   {
      return root.addItem(label);
   }

   /**
    * Add an instance of TreeListItem to the root
    * @param label translated label of the item
    * @param target target url of item
    * @return The TreeListItem reference to the added item.
    */
   public TreeListItem addItem( String label, String target )
   {
      return root.addItem(label, target);
   }

   /**
    * Add an instance of TreeListItem to the root
    * @param label translated label of the item
    * @param target target url of item
    * @param image relative path and image name of item
    * @return The TreeListItem reference to the added item.
    */
   public TreeListItem addItem( String label, String target, String image )
   {
      return root.addItem(label, target, image );
   }

   /**
    * Add a HTML hidden field 
    * @param name name of HTML hidden field.
    * @param value value of that hidden field.
    */
   public void addHiddenField(String name, String value)
   {
      hidden_field.addElement(new HiddenField(name,value));
   }

   /**
    * Returns the HTML code for the tree structure
    * show calls the following methods
    *       generateTreeHeader
    *       generateTreeBody
    *       generateTreeScripts
    *       generateHiddenFields
    *       generateTreeFooter
    * If customizing you MUST call these methods in this order.
    * @see ifs.fnd.asp.TreeList.generateTreeHeader
    * @see ifs.fnd.asp.TreeList.generateTreeBody
    * @see ifs.fnd.asp.TreeList.generateTreeScripts
    * @see ifs.fnd.asp.TreeList.generateHiddenFields
    * @see ifs.fnd.asp.TreeList.generateTreeFooter
    */
   public String show()
   {
      ASPManager mgr = page.getASPManager();
      AutoString html = new AutoString();
      
      html.append(generateTreeHeader());
      html.append(generateTreeBody());
      html.append(generateTreeScripts());
      html.append(generateHiddenFields());
      html.append(generateTreeFooter());

      return html.toString();
   }

   /**
    * Returns the Tree header HTML code
    * @see ifs.fnd.asp.TreeList.show
    * @see ifs.fnd.asp.TreeList.generateTreeBody
    * @see ifs.fnd.asp.TreeList.generateTreeScripts
    * @see ifs.fnd.asp.TreeList.generateHiddenFields
    * @see ifs.fnd.asp.TreeList.generateTreeFooter
    */
   public String generateTreeHeader()
   {
      ASPManager mgr = page.getASPManager();
      
      AutoString out = new AutoString();
      boolean div_enabled = (mgr.isExplorer() || mgr.isMozilla());
      menu_str.clear();

      if (show_header && !custom_tree_positioned)
      {
         if (mgr.isNetscape4x())
            yPos += 75;
         else
            yPos += 85;
      }
      
      if (show_cmdbar && !custom_tree_positioned)
      {
         if (mgr.isNetscape4x())
            yPos += 25;
         else
            yPos += 35;
      }
      
      if (disable_root && !custom_tree_positioned)
            yPos -= Integer.parseInt(folder_area_height);

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag((mgr.isEmpty(header_title)? mgr.translate("FNDTREELISTHEAD: Tree"):header_title)));
      out.append("</head>\n");

      out.append("<STYLE TYPE=\"text/css\">");
      out.append("\n\t<!-- A:link    {text-decoration: none}");
      out.append("\n\t     A:visited {text-decoration: none;COLOR:#009999}");
      out.append("\n\t     A:active  {text-decoration: none}");
      out.append("\n\t     A:hover   {text-decoration: none} -->\n");

      out.append("\t#spacer { position:absolute; height:");
      if (!mgr.isNetscape4x())
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)-5));
      else
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)));
      out.append("; }",CRLF);

      //out.append("\t#spacer_table { position:absolute;}",CRLF);
      out.append("\t#spacer_table { position:absolute; height:");
      if (!mgr.isNetscape4x())
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)-5));
      else
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)));
      out.append("; }",CRLF);
      
      
      out.append("\n</STYLE>\n");
      
      out.append("<script language=javascript>\n");
      
      out.append("var FOLDER_OPENED_IMG = \"",folder_image_node_opened,"\"\n");
      out.append("var FOLDER_CLOSED_IMG = \"",folder_image_node_closed,"\"\n");

      out.append("var ROOT_IMG          = \"",folder_image_root,"\"\n");
      out.append("var WRITING_LIST_MSG  = \"");
      out.append( mgr.translateJavaText("FNDNAVLISTMSG: List: Writing list..."), "\"\n");
      out.append("var DEBUG             = false\n");
      
      if (disable_root)
         out.append("var START_INDEX  = 1;\n");
      else
         out.append("var START_INDEX  = 0;\n");
      
      out.append("function initJSNavigator()\n");
      out.append("{\n");
      items_count = root.show(0, this, -1, 0, out);      
      if(div_enabled)
          out.append("List0.build("+xPos+","+yPos+");",CRLF);
      else
          out.append("List0.build("+xPos+","+(yPos+10)+");",CRLF);
      out.append("\ndocument.form.INDEX.value="+items_count+";");
  
      if (expanded_node > 0)
         out.append("\nexpand("+expanded_node+");");
 
      out.append("\n onLoad();\n");
      out.append("}\n");
      out.append("</script>\n");
      
      out.append("<STYLE TYPE=\"text/css\">");
      out.append("\n\t<!-- A:link    {text-decoration: none}");
      out.append("\n\t     A:visited {text-decoration: none;COLOR:#009999}");
      out.append("\n\t     A:active  {text-decoration: none}");
      out.append("\n\t     A:hover   {text-decoration: none} -->\n");

      out.append("\t#spacer { position:absolute; height:");
      if (!mgr.isNetscape4x())
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)-5));
      else
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)));
      out.append("; }",CRLF);

      //out.append("\t#spacer_table { position:absolute;}",CRLF);
      out.append("\t#spacer_table { position:absolute; height:");
      if (!mgr.isNetscape4x())
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)-5));
      else
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)));
      out.append("; }",CRLF);
      
      
      out.append("\n</STYLE>\n");

      if (!mgr.isEmpty(base_target))
         out.append("<base target=\"",base_target,"\">\n");

      out.append("<body topmargin=0 leftmargin=0 marginheight=0 marginwidth=0 ");
      if(body_background_color.equals(THEME_BGCOLOR))
         out.append(" class=pageFormWithoutBottomLine");
      else
         out.append(" bgcolor=\"", body_background_color, "\"");         
      out.append(" text=\"", folder_font_color, "\"");
      out.append(" link=\"", folder_link_color_unvisited,"\"");
      out.append(" vlink=\"", folder_link_color_visited,"\"");
      out.append(" alink=\"", folder_link_color_visited,"\"");
      out.append(" onLoad =\"javascript:initJSNavigator();\">\n");
      
      out.append("\n<form TARGET='_self' ");
      out.append(mgr.generateFormTag(),">\n");
      
      if (show_header)
         out.append(mgr.startPresentation(header_title));
      
      if (show_cmdbar)
      {
         out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr><td>&nbsp;&nbsp;</td><td width=\"100%\">\n" );   
         out.append( "<table cellpadding=0 cellspacing=0 width=100% class=\"pageCommandBar\" height=22>"+
             "<tr><td height=22 width=100%>&nbsp;&nbsp;"+cmdbar_title+"</td></tr></table>\n");
         out.append( " </td><td>&nbsp;&nbsp;</td></table>\n" );   
      }

      if (mgr.isNetscape4x())
      {
         int dummy_items_count = (items_count > 10?items_count:10);
         out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\" height=\""+(dummy_items_count * (Integer.parseInt(folder_area_height))*50)+"\"><tr><td>&nbsp;</td></tr></table>\n" );   
      }

      return out.toString();
   }      

   // Added by Terry 20130814
   // Generate tree header without html and head
   public String generateTreeHeader_()
   {
      ASPManager mgr = page.getASPManager();
      
      AutoString out = new AutoString();
      boolean div_enabled = (mgr.isExplorer() || mgr.isMozilla());
      menu_str.clear();

      if (show_header && !custom_tree_positioned)
      {
         if (mgr.isNetscape4x())
            yPos += 75;
         else
            yPos += 85;
      }
      
      if (show_cmdbar && !custom_tree_positioned)
      {
         if (mgr.isNetscape4x())
            yPos += 25;
         else
            yPos += 35;
      }
      
      if (disable_root && !custom_tree_positioned)
            yPos -= Integer.parseInt(folder_area_height);

      // out.append("<html>\n");
      // out.append("<head>\n");
      // out.append(mgr.generateHeadTag((mgr.isEmpty(header_title)? mgr.translate("FNDTREELISTHEAD: Tree"):header_title)));
      // out.append("</head>\n");

      out.append("<script language=javascript>\n");
      
      out.append("var FOLDER_OPENED_IMG = \"",folder_image_node_opened,"\"\n");
      out.append("var FOLDER_CLOSED_IMG = \"",folder_image_node_closed,"\"\n");

      out.append("var ROOT_IMG          = \"",folder_image_root,"\"\n");
      out.append("var WRITING_LIST_MSG  = \"");
      out.append( mgr.translateJavaText("FNDNAVLISTMSG: List: Writing list..."), "\"\n");
      out.append("var DEBUG             = false\n");
      
      if (disable_root)
         out.append("var START_INDEX  = 1;\n");
      else
         out.append("var START_INDEX  = 0;\n");
      
      out.append("function initJSNavigator()\n");
      out.append("{\n");
      items_count = root.show(0, this, -1, 0, out);      
      if(div_enabled)
          out.append("List0.build("+xPos+","+yPos+");",CRLF);
      else
          out.append("List0.build("+xPos+","+(yPos+10)+");",CRLF);
      out.append("\ndocument.form.INDEX.value="+items_count+";");
  
      if (expanded_node > 0)
         out.append("\nexpand("+expanded_node+");");
 
      out.append("\n onLoad();\n");
      out.append("}\n");
      out.append("</script>\n");
      
      out.append("<STYLE TYPE=\"text/css\">");
      out.append("\n\t<!-- A:link    {text-decoration: none}");
      out.append("\n\t     A:visited {text-decoration: none;COLOR:#009999}");
      out.append("\n\t     A:active  {text-decoration: none}");
      out.append("\n\t     A:hover   {text-decoration: none} -->\n");

      out.append("\t#spacer { position:absolute; height:");
      if (!mgr.isNetscape4x())
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)-5));
      else
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)));
      out.append("; }",CRLF);

      //out.append("\t#spacer_table { position:absolute;}",CRLF);
      out.append("\t#spacer_table { position:absolute; height:");
      if (!mgr.isNetscape4x())
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)-5));
      else
         out.appendInt(items_count * (Integer.parseInt(folder_area_height)));
      out.append("; }",CRLF);
      
      
      out.append("\n</STYLE>\n");

      if (!mgr.isEmpty(base_target))
         out.append("<base target=\"",base_target,"\">\n");

      /*out.append("<body topmargin=0 leftmargin=0 marginheight=0 marginwidth=0 ");
      if(body_background_color.equals(THEME_BGCOLOR))
         out.append(" class=pageFormWithoutBottomLine");
      else
         out.append(" bgcolor=\"", body_background_color, "\"");         
      out.append(" text=\"", folder_font_color, "\"");
      out.append(" link=\"", folder_link_color_unvisited,"\"");
      out.append(" vlink=\"", folder_link_color_visited,"\"");
      out.append(" alink=\"", folder_link_color_visited,"\"");
      out.append(" onLoad =\"javascript:initJSNavigator();\">\n");*/
      
      // out.append("\n<form TARGET='_self' ");
      // out.append(mgr.generateFormTag(),">\n");
      
      if (show_header)
         out.append(mgr.startPresentation(header_title));
      
      if (show_cmdbar)
      {
         out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr><td>&nbsp;&nbsp;</td><td width=\"100%\">\n" );   
         out.append( "<table cellpadding=0 cellspacing=0 width=100% class=\"pageCommandBar\" height=22>"+
             "<tr><td height=22 width=100%>&nbsp;&nbsp;"+cmdbar_title+"</td></tr></table>\n");
         out.append( " </td><td>&nbsp;&nbsp;</td></table>\n" );   
      }

      if (mgr.isNetscape4x())
      {
         int dummy_items_count = (items_count > 10?items_count:10);
         out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\" height=\""+(dummy_items_count * (Integer.parseInt(folder_area_height))*50)+"\"><tr><td>&nbsp;</td></tr></table>\n" );   
      }

      return out.toString();
   }
   // Added end
   
   /**
    * Returns the HTML code for the tree body
    * @see ifs.fnd.asp.TreeList.show
    * @see ifs.fnd.asp.TreeList.generateTreeHeader
    * @see ifs.fnd.asp.TreeList.generateTreeScripts
    * @see ifs.fnd.asp.TreeList.generateHiddenFields
    * @see ifs.fnd.asp.TreeList.generateTreeFooter
    */
   public String generateTreeBody()
   {
      ASPManager mgr = page.getASPManager();
      AutoString out = new AutoString();
            
      out.append("<input type=\"hidden\" name=\"INDEX\" value=\"",""+items_count,"\">\n");
      out.append("<input type=\"hidden\" name=\"MENU_NAME\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"MENU_LINES\" value=\"\">\n");
      
      out.append(mgr.endPresentationSimple()); 

      //out.append("<table border=0 width=100% height="+(items_count * (Integer.parseInt(folder_area_height)-8))+"><tr><td>\n");
      out.append("<table border=0 width=100%><tr><td>\n");
      //out.append("<DIV ID=\"spacer_table\">\n");
      out.append("<DIV ID=\"spacer\"></DIV>\n");
      //out.append("</DIV>\n");
      out.append("</td></tr></table>\n");

      if (mgr.isExplorer() || mgr.isMozilla())
      {
         for (int i=0; i<items_count; i++)
         {
            out.append("\t<DIV ID=\"lItem");
            out.appendInt(i);
            out.append("\" style='top:0;left:0;position:absolute;'");
            out.append("></DIV>",CRLF);
         }
         out.append("<DIV style='top:0;left:0;position:absolute;' ID=\"expand_div_"+items_count+"\">");
         out.append("</DIV>\n");

      }
      else
      {
         out.append("<DIV ID=\"expand_div_"+items_count+"\" style='top:0;left:0;position:absolute;'>");
         out.append("</DIV>\n");
      }
      
      return out.toString();
   }
   
   // Added by Terry 20130815
   // Generate tree body without command hidden field
   public String generateTreeBody_()
   {
      ASPManager mgr = page.getASPManager();
      AutoString out = new AutoString();
            
      out.append("<input type=\"hidden\" name=\"INDEX\" value=\"",""+items_count,"\">\n");
      out.append("<input type=\"hidden\" name=\"MENU_NAME\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"MENU_LINES\" value=\"\">\n");
      
      // out.append(mgr.endPresentationSimple()); 

      //out.append("<table border=0 width=100% height="+(items_count * (Integer.parseInt(folder_area_height)-8))+"><tr><td>\n");
      out.append("<table border=0 width=100%><tr><td>\n");
      //out.append("<DIV ID=\"spacer_table\">\n");
      out.append("<DIV ID=\"spacer\"></DIV>\n");
      //out.append("</DIV>\n");
      out.append("</td></tr></table>\n");

      if (mgr.isExplorer() || mgr.isMozilla())
      {
         for (int i=0; i<items_count; i++)
         {
            out.append("\t<DIV ID=\"lItem");
            out.appendInt(i);
            out.append("\" style='top:0;left:0;position:absolute;'");
            out.append("></DIV>",CRLF);
         }
         out.append("<DIV style='top:0;left:0;position:absolute;' ID=\"expand_div_"+items_count+"\">");
         out.append("</DIV>\n");

      }
      else
      {
         out.append("<DIV ID=\"expand_div_"+items_count+"\" style='top:0;left:0;position:absolute;'>");
         out.append("</DIV>\n");
      }
      
      return out.toString();
   }
   // Added end

   /**
    * Returns the HTML code for the scripts
    * @see ifs.fnd.asp.TreeList.show
    * @see ifs.fnd.asp.TreeList.generateTreeHeader
    * @see ifs.fnd.asp.TreeList.generateTreeBody
    * @see ifs.fnd.asp.TreeList.generateHiddenFields
    * @see ifs.fnd.asp.TreeList.generateTreeFooter
    */
   public String generateTreeScripts()
   {
      ASPManager mgr = page.getASPManager();
      AutoString out = new AutoString();
      
      out.append("<script language=javascript src=",script_loc,javascript_filename,"></script>\n");
      out.append(menu_str.toString());
      out.append("<script language=javascript>\n");
     
      if (mgr.isExplorer() || mgr.isMozilla())
      {
         out.append("var _handle_menus = false;\n");
         out.append("document.onclick = hideMenus;\n");
      }
      else
      {
         out.append("var mouseX = 0;\n");
         out.append("var mouseY = 0;\n");
         out.append("var _handle_menus = false;\n");
         out.append("var _hide_menus = true;\n");
         out.append("var _submenus = new Array();\n");
         out.append("var _menu_clicked = false;\n");
         out.append("var _last_menu;\n");

         out.append("if (window.addEventListener)\n{\n");
         out.append("    document.addEventListener('mousemove', updateMouse, true);\n");
         out.append("    document.addEventListener('mouseup', mousePressed, true);\n");
         out.append("} \nelse if (window.attachEvent)\n{\n");
         out.append("    document.attachEvent('onmousemove', updateMouse);\n");
         out.append("    document.attachEvent('onmouseup', mousePressed);\n}\n");

         //out.append("document.captureEvents(Event.MOUSEMOVE);\n");
         //out.append("document.captureEvents(Event.MOUSEUP);\n");
         //out.append("document.onmousemove = updateMouse;\n");
         //out.append("document.onmouseup = mousePressed;\n\n");

         out.append("function updateMouse(e)\n");
         out.append("{\n");
         out.append("\tmouseX = e.pageX;\n");
         out.append("\tmouseY = e.pageY;\n");
         out.append("}\n\n");

         out.append("function mousePressed(e)\n");
         out.append("{\n");
         out.append("\tsetTimeout(\"updateMenus()\",100);\n");
         out.append("}\n\n");

         out.append("function updateMenus()\n");
         out.append("{\n");
         out.append("\tif(_hide_menus)\n");
         out.append("\t\thideMenus();\n");
         out.append("\t_hide_menus = true;\n");
         out.append("}\n\n");
      }
      
      out.append("USER_AGENT=\"",mgr.getUserAgent(),"\"\n");
      out.append("</script>\n");
      
      return out.toString();
   }      
   
   /**
    * Returns the HTML code for the tree body
    * @see ifs.fnd.asp.TreeList.show
    * @see ifs.fnd.asp.TreeList.generateTreeHeader
    * @see ifs.fnd.asp.TreeList.generateTreeBody
    * @see ifs.fnd.asp.TreeList.generateTreeScripts
    * @see ifs.fnd.asp.TreeList.generateTreeFooter
    */
   public String generateHiddenFields()
   {
      AutoString out = new AutoString();
      
      for (int i=0; i<hidden_field.size(); i++)
      {
         out.append("<input type=hidden name="+((HiddenField)hidden_field.get(i)).name);
         out.append(" value='"+((HiddenField)hidden_field.get(i)).value+"'>\n");
      }
         
      return out.toString();
   }
      
   
   /**
    * Returns the Tree footer HTML code
    * @see ifs.fnd.asp.TreeList.show
    * @see ifs.fnd.asp.TreeList.generateTreeHeader
    * @see ifs.fnd.asp.TreeList.generateTreeBody
    * @see ifs.fnd.asp.TreeList.generateTreeScripts
    * @see ifs.fnd.asp.TreeList.generateHiddenFields
    */
   public String generateTreeFooter()
   {
      AutoString out = new AutoString();

      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");

      return out.toString();
   }
   
   
   // Modified by Terry 20130819
   // Javascript tag of tree list item
   // Original:
   // void formatItemEntry( int    level,
   //      int    parentpos,
   //      int    posno,
   //      String label,
   //      String target,
   //      String image,
   //      String expand_data,
   //      boolean dynamic,
   //      Vector menu_items,
   //      int menu_width,
   //      int font_size,
   //      String defined_menu,
   //      AutoString out )
   
   void formatItemEntry( int    level,
                         int    parentpos,
                         int    posno,
                         String label,
                         String target,
                         String image,
                         String expand_data,
                         boolean dynamic,
                         Vector menu_items,
                         int menu_width,
                         int font_size,
                         String defined_menu,
                         String js_tag,
                         AutoString out )
   {
      if(DEBUG) Util.debug("TreeList.formatItemEntry("+level+","+parentpos+","+posno+","
                                                     +label+","+target+","+image+")");   
    
      ASPManager mgr = page.getASPManager();
      
      // Added by Terry 20140915
      // Tree font color
      String tree_font_color = "black";
      // Added end
      
      label = mgr.HTMLEncode(label);
      if (dynamic && level ==2)
         out.append("_mLists[");
      else
         out.append("List");
         
      if(target==null)
      {
         out.appendInt(posno);
         out.append("=new List(", level==0?"true,":"false,", folder_area_width, ",", folder_area_height);
         out.append(",\"", background_color, "\"");  // ,\"", image_loc, "\",\"");
         out.append(",\"",menu_type,"\"");
         out.append(",\"",expand_data,"\");",CRLF);
         if(level==0)
         {
            out.append("List0.addItem(\"<TD valign=top NOWRAP>");
            if (!mgr.isEmpty(defined_menu))
            {
               // Modified by Terry 20130819
               // Javascript tag of tree list item
               // Original:
               // out.append("<a TARGET=_self href=\\\"",defined_menu,"\\\">");
               out.append("<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",defined_menu,"\\\">");
               // Modified end
               out.append("<img src='"+folder_image_root+"' width='16' height='16' border='0'></a>");
            }
            else
               out.append("<img src='"+folder_image_root+"' width='16' height='16' border='0'>");
            out.append("&nbsp;</TD>");

            out.append("<td WIDTH=100% align=left NOWRAP>");
            
            // Added by Terry 20140915
            // Tree font color
            if (mgr.isEmpty(root.getTarget()))
               tree_font_color = "gray";
            // Added end
            
            // Modified by Terry 20140915
            // Tree font color
            // Original:
            // out.append("<FONT FACE='",folder_font_face,"' SIZE=",folder_font_size,">");
            out.append("<FONT COLOR='" + tree_font_color + "' FACE='",folder_font_face,"' SIZE=",folder_font_size,">");
            // Modified end
            
            if (mgr.isEmpty(root.getTarget()))
            {
               //out.append("List0.addItem(\"<TD valign=top NOWRAP><img src='"+folder_image_root+"' width='16' height='16' border='0'></TD>");
               out.append(label);
            }
            else
            {
               // Modified by Terry 20130819
               // Javascript tag of tree list item
               // Original:
               // out.append("<a href='",mgr.correctURL(root.getTarget()),"'>",label,"</a>");
               out.append("<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(root.getTarget()),"'>",label,"</a>");
               // Modified end
               //out.append(label,"</a>\");",CRLF);
            }
            out.append("</FONT>\");",CRLF);
         }
      }
      else if(parentpos>=0)
      {
         // Added by Terry 20140915
         // Tree font color
         if (mgr.isEmpty(target))
            tree_font_color = "gray";
         // Added end
         
         if (font_size > 0)
            // Modified by Terry 20140915
            // Tree font color
            // Original:
            // label = "<FONT SIZE="+font_size+">"+label+"</FONT>";
            label = "<FONT COLOR='" + tree_font_color + "' SIZE="+font_size+">"+label+"</FONT>";
            // Modified end
         
         out.appendInt(parentpos);
         if (dynamic && level ==2)
            out.append("]");

         if (!mgr.isEmpty(defined_menu))
         {
            if (mgr.isEmpty(image))
               image = menu_image;

            // Modified by Terry 20130819
            // Javascript tag of tree list item
            // Original:
            // out.append(".addItem(\"<a TARGET=_self href=\\\"",defined_menu,"\\\">");
            out.append(".addItem(\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",defined_menu,"\\\">");
            // Modified end
            out.append("<img src='",image,"' width='16' height='16' border='0'></a>&nbsp;\"");
            
            if (!mgr.isEmpty(target))
            {
               // Modified by Terry 20130819
               // Javascript tag of tree list item
               // Original:
               // out.append(",\"<a href='",mgr.correctURL(target),"'>");
               out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
               // Modified end
               out.append(label,"</a>\");",CRLF);
            }
            else
            {
               out.append(",\"",label,"\");",CRLF);
            }
         }
         else if (menu_items.size()>0)
         {
            if (mgr.isEmpty(image))
               image = menu_image;

            // Modified by Terry 20130819
            // Javascript tag of tree list item
            // Original:
            // out.append(".addItem(\"<a TARGET=_self href=\\\"",generateCall(menu_items,posno),"\\\">");
            out.append(".addItem(\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",generateCall(menu_items,posno),"\\\">");
            // Modified end
            out.append("<img src='",image,"' width='16' height='16' border='0'></a>&nbsp;\"");
            
            if (!mgr.isEmpty(target))
            {
               // Modified by Terry 20130819
               // Javascript tag of tree list item
               // Original:
               // out.append(",\"<a href='",mgr.correctURL(target),"'>");
               out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
               // Modified end
               out.append(label,"</a>\");",CRLF);
            }
            else
            {
               out.append(",\"",label,"\");",CRLF);
            }
            
            generateDefinition(menu_items,posno,menu_width,menu_str);   
         }
         else
         {   
            if (mgr.isEmpty(image))
            {
               image = image_loc+"empty.gif";

               //out.append(".addItem(\"<img src='",image,"' width='16' height='16' border='0'>&nbsp;");
               //out.append(".addItem(\"<img src='",image,"' border='0'>&nbsp;");
               out.append(".addItem(\"");
               
               if (!mgr.isEmpty(target))
               {
                  // Modified by Terry 20130819
                  // Javascript tag of tree list item
                  // Original:
                  // out.append("<a href='",mgr.correctURL(target),"'>");
                  out.append("<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                  // Modified end
                  out.append(label,"</a>\")\"");
                  out.append(",\"\");",CRLF);
               }
               else{
                  out.append(label,"\"");
                  out.append(",\"\");",CRLF);
               }
            }
            else
            {
               if (!mgr.isEmpty(target))
               {
                  // Modified by Terry 20130819
                  // Javascript tag of tree list item
                  // Original:
                  // out.append(".addItem(\"<a href='",mgr.correctURL(target),"'>");
                  out.append(".addItem(\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                  // Modified end
                  out.append("<img src='",image,"' width='16' height='16' border='0'>&nbsp;</a>\"");
                  // Modified by Terry 20130819
                  // Javascript tag of tree list item
                  // Original:
                  // out.append(",\"<a href='",mgr.correctURL(target),"'>");
                  out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                  // Modified end
                  out.append(label,"</a>\");",CRLF);
               }
               else
               {
                  out.append(".addItem(\"");
                  //out.append("<img src='",image,"' width='16' height='16' border='1'>&nbsp;");
                  out.append("<img src='",image,"' border='0'>&nbsp;\"");
                  out.append(",\"",label,"\");",CRLF);
                  
               }
            }
         }
      }
   }
   // Modified end
   
   
   // Modified by Terry 20130819
   // Javascript tag of tree list item
   // Original:
   // void formatItemEntry2(int parentpos,
   //      int posno, 
   //      String label, 
   //      String target,
   //      String image,
   //      boolean dynamic,
   //      int level,
   //      Vector menu_items,
   //      int menu_width,
   //      int font_size,
   //      String defined_menu,
   //      AutoString out)
   
   void formatItemEntry2(int parentpos,
                         int posno, 
                         String label, 
                         String target,
                         String image,
                         boolean dynamic,
                         int level,
                         Vector menu_items,
                         int menu_width,
                         int font_size,
                         String defined_menu,
                         String js_tag,
                         AutoString out)
   {
      if(DEBUG) Util.debug("TreeList.formatItemEntry2("+parentpos+","+posno+","+label+")");
      
      ASPManager mgr = page.getASPManager();
      
      // Added by Terry 20140915
      // Tree font color
      String tree_font_color = "black";
      // Added end
      
      label = mgr.HTMLEncode(label);

      // Added by Terry 20140915
      // Tree font color
      if (mgr.isEmpty(target))
         tree_font_color = "gray";
      // Added end

      if (font_size > 0)
         // Added by Terry 20140915
         // Tree font color
         // Original:
         // label = "<FONT SIZE="+font_size+">"+label+"</FONT>";
         label = "<FONT COLOR='" + tree_font_color + "' SIZE="+font_size+">"+label+"</FONT>";
         // Modified end
    
      out.append("List");
      out.appendInt(posno);
      
      
      // Modified by Terry 20140915
      // Original:
      // out.append(".setFont(\"<FONT FACE='",folder_font_face,"' SIZE=",folder_font_size);
      out.append(".setFont(\"<FONT COLOR='" + tree_font_color + "' FACE='",folder_font_face,"' SIZE=",folder_font_size);
      // Modified end
      
      out.append(">\", \"</FONT>\");",CRLF);
      if(posno>0)
      {
          if (dynamic && level ==2)
          {
             out.append("_mLists[");
             out.appendInt(parentpos);
             out.append("].addList(List");
             out.appendInt(posno);
          }
          else
          {
             out.append("List");
             out.appendInt(parentpos);
             out.append(".addList(List");
             out.appendInt(posno);
          }
            
          if (!mgr.isEmpty(target))
          {
             if (!mgr.isEmpty(defined_menu))
             {
                if (mgr.isEmpty(image))
                   image = menu_image;
                
                // Modified by Terry 20130819
                // Javascript tag of tree list item
                // Original:
                // out.append(",\"<a TARGET=_self href=\\\"",defined_menu,"\\\">");
                out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",defined_menu,"\\\">");
                // Modified end
                out.append("<img src='",image,"' width='16' height='16' border='0'></a>\"");
                // Modified by Terry 20130819
                // Javascript tag of tree list item
                // Original:
                // out.append(",\"&nbsp;<a href='",mgr.correctURL(target),"'>");
                out.append(",\"&nbsp;<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                // Modified end
                out.append(label,"</a>\");");
                out.append(CRLF);
                  
             }
             else if (menu_items.size() > 0)
             {
                if (mgr.isEmpty(image))
                   image = menu_image;
                
                // Modified by Terry 20130819
                // Javascript tag of tree list item
                // Original:
                // out.append(",\"<a TARGET=_self href=\\\"",generateCall(menu_items,posno),"\\\">");
                out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",generateCall(menu_items,posno),"\\\">");
                // Modified end
                out.append("<img src='",image,"' width='16' height='16' border='0'></a>\"");
                // Modified by Terry 20130819
                // Javascript tag of tree list item
                // Original:
                // out.append(",\"&nbsp;<a href='",mgr.correctURL(target),"'>");
                out.append(",\"&nbsp;<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                // Modified end
                out.append(label,"</a>\");");
                out.append(CRLF);  
              }
              else
              {
                 // Modified by Terry 20130819
                 // Javascript tag of tree list item
                 // Original:
                 // out.append(",\"<a href='",mgr.correctURL(target),"'>");
                 out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                 // Modified end
                 if (!mgr.isEmpty(image)){
                    out.append("<img src='",image,"' width='16' height='16' border='0'></a>\"");
                    // Modified by Terry 20130819
                    // Javascript tag of tree list item
                    // Original:
                    // out.append(",\"&nbsp;<a href='",mgr.correctURL(target),"'>");
                    out.append(",\"&nbsp;<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " href='",mgr.correctURL(target),"'>");
                    // Modified end
                    out.append(label,"</a>\");");
                    out.append(CRLF);
                 }
                 else {
                    out.append(label,"</a>\"");
                    out.append(",\"\"");
                    out.append(CRLF);
                 }
               }
          }
          else
          {
             // Added by Terry 20140915
             // Tree font color
             if (label.indexOf("<FONT") == -1)
                label = "<FONT COLOR='" + tree_font_color + "' FACE='" + folder_font_face + "' SIZE=" + folder_font_size + ">" + label + "</FONT>";
             // Added end
             if (!mgr.isEmpty(defined_menu))
             {
                if (mgr.isEmpty(image))
                   image = menu_image;
                
                // Modified by Terry 20130819
                // Javascript tag of tree list item
                // Original:
                // out.append(",\"<a TARGET=_self href=\\\"",defined_menu,"\\\">");
                out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",defined_menu,"\\\">");
                // Modified end
                out.append("<img src='",image,"' width='16' height='16' border='0'></a>\"");
                //out.append("<A TARGET='_self' HREF=\\\"javascript:expand("+posno+");\\\">");
                out.append(",\"&nbsp;",label,"\");");
                //out.append(label,"</a>\");",CRLF);
                out.append(CRLF);
             }
             else if (menu_items.size() > 0)
             {
                if (mgr.isEmpty(image))
                   image = menu_image;
                
                // Modified by Terry 20130819
                // Javascript tag of tree list item
                // Original:
                // out.append(",\"<a TARGET=_self href=\\\"",generateCall(menu_items,posno),"\\\">");
                out.append(",\"<a " + (mgr.isEmpty(js_tag) ? "" : js_tag) + " TARGET=_self href=\\\"",generateCall(menu_items,posno),"\\\">");
                // Modified end
                out.append("<img src='",image,"' width='16' height='16' border='0'></a>\"");
                //out.append("<A TARGET='_self' HREF=\\\"javascript:expand("+posno+");\\\">");
                out.append(",\"&nbsp;",label,"\");");
                //out.append(label,"</a>\");",CRLF);
                out.append(CRLF);
             }
             else
             {
                if (!mgr.isEmpty(image))
                {
                   out.append(",\"","<img src='",image,"' width='16' height='16' border='0'>\"");
                   out.append(",\"&nbsp;",label,"\");",CRLF);
                }
                else{
                   out.append(",\"\"");
                   out.append(",\"&nbsp;",label,"\");",CRLF);
                }
             }
          }
          
          if (menu_items.size() > 0)
          {
             generateDefinition(menu_items,posno,menu_width,menu_str);
             //generatePopupClickScript(menu_items,posno,hide_str);
          }
      }
   }
   // Modified end

   /**
    * Returns the HTML coding of the dynamic nodes to be added 
    * to the current expanding node location
    */
   public String getDynamicNodeString()
   {
      ASPManager mgr = page.getASPManager();
      
      int parent_pos = Integer.valueOf(mgr.readValue("LIST_POS")).intValue();
      int pos_no = Integer.valueOf(mgr.readValue("INDEX")).intValue();

      return getDynamicNodeString(parent_pos, pos_no);
   }
   
   /**
    * Returns the HTML coding of the dynamic nodes to be added 
    * to the parent_pos, starting from pos_no
    * @param parent_pos int value of the parent node's INDEX LIST_POS
    * @param pos_no int value of the starting INDEX
    */
   public String getDynamicNodeString(int parent_pos, int pos_no)
   {
      AutoString out = new AutoString();
      int items = root.dynamicShow(1, this, parent_pos, pos_no, out)-1;
      
      String return_str = out.toString()+ "j=" +items+";^"
                          +items+"^"
                          +menu_str.toString()+"^";
      return return_str;
   }
   

   private String generateCall(Vector items, int posno )
   {
      AutoString params = new AutoString();
      boolean lastWasSeparator = true;
      for(int i=0;i<items.size();i++)
      {
            params.append("true",",");
      }
         
      String paramlist = params.toString();
         
      if(paramlist.length() > 0)
         paramlist = paramlist.substring(0,paramlist.length()-1);

      return "javascript:showMenu('mtreelist"+posno+"_',[" + paramlist + "],false);";
   }

   //Bug 42727, start
   private void generateDefinition(Vector items, int posno, int width, AutoString def)
   //Bug 42727, end
   {
      String action;

      ASPManager mgr = page.getASPManager();
      
      String id = "mtreelist" + posno +"_";
      //Bug 42727, start
      MenuItem item;
      
      if(mgr.isMozilla() || mgr.isExplorer())
      {
         int sep_length = width;
         int label_length = 0;
         for(int row=0;row<items.size();row++)
         {
            item = (MenuItem) items.elementAt(row); 
            if(item.label.length()>label_length) 
               label_length = item.label.length();                 
         }
         label_length = label_length * 10;
         if (sep_length < label_length)
            sep_length = label_length;
          
         def.append("<span style=\"z-index:1000;width:",Integer.toString(width));
         def.append("px;position:absolute;visibility:hidden;\" id=\"",id,"\">");
         def.append("<table class=menu width=",Integer.toString(width),"px border=0 cellpadding=0px cellspacing=0px><tr width=100%><td width=100%>\n");
         def.append("<table width=100% cellpadding=",mgr.isExplorer()?"2":"1","px cellspacing=0px><tr><td width=100%>\n");
         def.append("<table width=100% border=0 cellpadding=2px cellspacing=0px>\n");        

         String onmouseover="";
         String onmouseout="";

         for(int row=0;row<items.size();row++)
         {
            item = (MenuItem) items.elementAt(row);

            action = item.action;

            if( !item.isSeparator() )
            {
               onmouseover = "this.className='menuRowMouseOver';";
               onmouseout = "this.className='menuRowMouseOut';";
            }
            else{
               onmouseover="";
               onmouseout="";
            }
                         
            def.append("\n<tr class=menuRowMouseOut style=\"display:block;\" onClick=\"_menu(0);",action,"\"");
            def.append(" id=\"",id,Integer.toString(row), "\"");
            def.append(" onmouseover=\"",onmouseover,"\" onmouseout=\"",onmouseout,"\" ");
            def.append(">\n");

            if( item.isSeparator() )
               def.append("\t<td width="+sep_length+"><img width=100% height=2 src=\""+mgr.getASPConfig().getImagesLocation() +"menu_separator.gif\"></td>\n");
            else            
               def.append("\t<td width=100% class=menuRow NOWRAP>",item.label,"</td>\n");
          
            def.append("</tr>");
         }
          
         def.append("</table>\n");
         def.append("</tr></td></table>\n");
         def.append("</td></tr></table>\n");
         def.append("</span>\n");
      }  
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
             item = (MenuItem) items.elementAt(row);
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
            item = (MenuItem) items.elementAt(row);

            label = item.label;
            action = item.action;
            
            def.append("\n<DIV class=menuRow id=\"",id);
            def.appendInt(row);                         
            def.append("\">\n");

            def.append("<table border=0 cellpadding=0 cellspacing=0>");

            def.append("\t<tr>\n\t\t<td bgcolor=black width=1>",mgr.getEmptyImage(1,1),"</td>");
            def.append("\n\t\t<td height=20pt width=");
            def.appendInt(temp_width+10);
            def.append(" class=menuContent NOWRAP>");
            def.append(item.isSeparator()?"":"&nbsp;","<a class=menuLink href=\"javascript:");
            def.append("_menu(0);");
            def.append(action,"\">",label,"</a></td>");
 
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
      //Bug 42727, end
   }

   private void generatePopupClickScript(Vector menu_items, int posno, AutoString str )
   {
      ASPManager mgr = page.getASPManager();
      
      String id = "mtreelist" + posno +"_";
      
      if(mgr.isExplorer())
      {
         //for( int i=0; i<menu_items.size(); i++ )
         //{
            str.append("\tdocument.all[\"",id,"\"].style.visibility = \"hidden\";\n");
         //}
      }
      else
      {
         generateNetscapeHideScript(menu_items,id,str);
      }
   }
  
   private void generateNetscapeHideScript(Vector menu_items, String name, AutoString head)
   {
      head.append("\t\tif(!isNav6) {\n");
      head.append("\t\t\tdocument.layers[\"",name,"Start","\"].visibility = \"hidden\";\n");
      for(int row = 0;row < menu_items.size();row++)
         head.append("\t\t\tdocument.layers[\"",name,Integer.toString(row),"\"].visibility = \"hidden\";\n");
      head.append("\t\t\tdocument.layers[\"",name,"End","\"].visibility = \"hidden\";\n");
      head.append("\t\t}else{\n");
      head.append("\t\t\tdocument.getElementById(\"",name,"Start","\").style.visibility = \"hidden\";\n");
      for(int row = 0;row < menu_items.size();row++)
         head.append("\t\t\tdocument.getElementById(\"",name,Integer.toString(row),"\").style.visibility = \"hidden\";\n");
      head.append("\t\t\tdocument.getElementById(\"",name,"End","\").style.visibility = \"hidden\";\n");
      head.append("\t\t}\n");
   }

   
   class HiddenField
   {
      String name;
      String value;
      
      HiddenField(String name, String value)
      {
         this.name = name;
         this.value = value;
      }
   }
}

