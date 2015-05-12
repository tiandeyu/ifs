
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
 * File        : TreeListItem.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H    2002-08-26  created.
 *    Ramila H    2002-09-04  Added RMB's to the tree.
 *    Ramila H    2003-08-26  added method addDefinedPopup()
 *    Ramila H    2004-05-11  Bug id 38505 corrected. changed target to "" if null
 *    Ramila H    2004-07-12  Deprecated addMenuItem and associated methods
 * ----------------------------------------------------------------------------
 */
package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import java.util.*;
import java.io.*;           
import java.lang.reflect.*;



public class TreeListItem
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.TreeListItem");
   
   private String label;   // translated label showed in navigator
   private String target;  // target URL
   private String image;   // icons file name
   // Added by Terry 20130819
   // Javascript tag of tree list item
   private String js_tag;
   // Added end
   
   private Vector menu_items;
   private int menu_width = 120;
   private int font_size = 0;
   private String defined_menu_item;

   /**
    * Return a new instance of TreeListItem
    * @param label translated label of the item
    * @param target target url of the item
    * @param image relative path and image name for the item
    */
   public TreeListItem( String label, String target, String image )
   {
      if (target == null) target = "";
      this.label  = label;
      this.target = target;
      this.image  = image;
      this.menu_items = new Vector();
   }
   
   /**
    * Return a new instance of TreeListItem
    * @param label translated label of the item
    * @param target target url of the item
    * @param image relative path and image name for the item
    * @param js_tag javascript tag of the item
    */
   // Added by Terry 20130819
   // Javascript tag of tree list item
   public TreeListItem( String label, String target, String image, String js_tag )
   {
      if (target == null) target = "";
      this.label  = label;
      this.target = target;
      this.image  = image;
      this.menu_items = new Vector();
      this.js_tag = js_tag;
   }
   // Added end
   
   /**
    * Show the current item
    */
   int show( int level, TreeList tree, int parent_pos, int pos_no, boolean dynamic, AutoString out )
   {
      if(DEBUG) Util.debug("TreeListItem.show(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);
      // Modified by Terry 20130819
      // Javascript tag of tree list node item
      // Original:
      // tree.formatItemEntry(level,parent_pos,pos_no,this.label,this.target,this.image,null,dynamic,this.menu_items,this.menu_width,this.font_size,this.defined_menu_item,out );
      tree.formatItemEntry(level,parent_pos,pos_no,this.label,this.target,this.image,null,dynamic,this.menu_items,this.menu_width,this.font_size,this.defined_menu_item,this.js_tag,out );
      // Modified end
      pos_no++;
      return pos_no;
   }
   
   /**
    * Set label for this item
    * @param label translated label
    */
   public void setLabel(String label)
   {
      this.label  = label;
   }

   /**
    * Get label of this item
    * @return this item's label
    */
   public String getLabel()
   {
      return this.label;
   }

   /**
    * Set target for this item
    * @param target target url for this item
    */
   public void setTarget(String target)
   {
      if (target == null) target ="";
      this.target  = target;
   }

   /**
    * get target url of this item
    * @return target url of this item
    */
   public String getTarget()
   {
      return this.target;
   }

   /**
    * Set image icon for this item
    * @param image relative path and image name of this item
    */
   public void setImage(String image)
   {
      this.image  = image;
   }

   /**
    * get image of this item
    * @return image name with relative path of this item
    */
   public String getImage()
   {
      return this.image;
   }

   
   /**
    * Set the font size 
    * @param font_size int font size
    * @see ifs.fnd.asp.TreeListItem.getFontSize
    */
   public void setFontSize(int font_size)
   {
      this.font_size = font_size;
   }

   /**
    * get the Font size
    * @return int value of font size
    * @see ifs.fnd.asp.TreeListItem.setFontSize
    */
   public int getFontSize()
   {
      return font_size;
   }
   
   /**
    * Set the Javascript tag 
    * @param js_tag String Javascript tag
    * @see ifs.fnd.asp.TreeListNode.getJsTag
    */
   // Added by Terry 20130819
   // Javascript tag of tree list node
   public void setJsTag(String js_tag)
   {
      this.js_tag = js_tag;
   }
   // Added end
   
   /**
    * get the Javascript tag
    * @return String value of Javascript tag
    * @see ifs.fnd.asp.TreeListNode.setJsTag
    */
   // Added by Terry 20130819
   // Javascript tag of tree list node
   public String getJsTag()
   {
      return this.js_tag;
   }
   // Added end
   
   
   //Adding RMB's to TreeListNodes

   /**
    *@deprecated use TreeListItem.addDefinedPopup
    */
   public void addMenuItem(String label)
   {
      addMenuItem(label, null);
   }

   /**
    *@deprecated use TreeListItem.addDefinedPopup
    */
   public void addMenuItem(String label, String tag)
   {
      MenuItem item = new MenuItem(label, tag);
      menu_items.addElement(item);
   }

   /**
    *@deprecated use TreeListItem.addDefinedPopup
    */
   public void addMenuSeparator()
   {
      MenuItem item = new MenuItem();
      menu_items.addElement(item);
   }

   /**
    *@deprecated use TreeListItem.addDefinedPopup
    */
   public void setMenuWidth(int width)
   {
      menu_width = width;
   }

   /**
    *@deprecated use TreeListItem.addDefinedPopup
    */
   public int getMenuWidth()
   {
      return menu_width;
   }
   
   /**
    * Use a previously defined ASPPopup instance. This method overrides all other menu items.
    * @param menu_item_status_str String returned by ASPPopup.generateCall()
    * @see ASPPopup.generateCall
    * @see addMenuItem
    */
   public void addDefinedPopup(String menu_item_status_str)
   {
      this.defined_menu_item = menu_item_status_str;
   }
   
   
}
