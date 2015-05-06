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
 * File        : TreeListNode.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H    2002-08-26  created.
 *    Ramila H    2002-09-04  Added RMB's to the tree.
 *    Ramila H    2003-08-26  added method addDefinedPopup()
 *    Ramila H    2004-07-12  Deprecated addMenuItem and associated methods.
 *                            Added RMB functionality for root node
 * ----------------------------------------------------------------------------
 */
package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import java.util.*;
import java.io.*;           
import java.lang.reflect.*;

public class TreeListNode
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.TreeListNode");
   
   private Vector items;  
   private String label;  
   private String image;  
   private String target; 
   private String expand_data; 
   private Vector menu_items;
   private String defined_menu_item;
   // Added by Terry 20130819
   // Javascript tag of tree list node
   private String js_tag;
   // Added end
   
   private int menu_width = 120;
   private int font_size = 0;
   
   /**
    * Return a new instance of TreeListNode
    * @param label translated label of the node
    */
   public TreeListNode( String label )
   {
      this.items = new Vector();
      this.label = label;
      this.image = null;
      this.target = null;
      this.expand_data = null;
      this.menu_items = new Vector();
   }

   /**
    * Return a new instance of TreeListNode
    * @param label translated label of the node
    * @param target target url of the node
    */
   public TreeListNode( String label, String target )
   {
      this.items = new Vector();
      this.label = label;
      this.image = null;
      this.target = target;
      this.expand_data = null;
      this.menu_items = new Vector();
   }

   
   /**
    * Return a new instance of TreeListNode
    * @param label translated label of the node
    * @param target target url of the node
    * @param image relative path and image name for the node
    */
   public TreeListNode( String label, String target, String image )
   {
      this.items = new Vector();
      this.label = label;
      this.image = image;
      this.target = target;
      this.expand_data = null;
      this.menu_items = new Vector();
   }

   /**
    * Return a new instance of TreeListNode
    * @param label translated label of the node
    * @param target target url of the node
    * @param image relative path and image name for the node
    * @param expand_data query string name value pairs used to get dynamic nodes.
    */
   public TreeListNode( String label, String target, String image, String expand_data )
   {
      this.items = new Vector();
      this.label = label;
      this.image = image;
      this.target = target;
      this.expand_data = expand_data;
      this.menu_items = new Vector();
   }
   
   /**
    * Return a new instance of TreeListNode
    * @param label translated label of the node
    * @param target target url of the node
    * @param image relative path and image name for the node
    * @param expand_data query string name value pairs used to get dynamic nodes.
    * @param js_tag javascript tag of the node
    */
   // Added by Terry 20130819
   // Javascript tag of tree list node
   public TreeListNode( String label, String target, String image, String expand_data, String js_tag )
   {
      this.items = new Vector();
      this.label = label;
      this.image = image;
      this.target = target;
      this.expand_data = expand_data;
      this.menu_items = new Vector();
      this.js_tag = js_tag;
   }
   // Added end

   /**
    * Add a TreeListNode instance to the current node
    * @param label translated label of the node
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label )
   {
      return addNode( label, null, null, null );
   }
   
   /**
    * Add a TreeListNode instance to the current node
    * @param label translated label of the node
    * @param target target url of the node
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label, String target )
   {
      return addNode( label, target, null, null );
   }

   /**
    * Add a TreeListNode instance to the current node
    * @param label translated label of the node
    * @param target target url of the node
    * @param image relative path and image name for the node
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label, String target, String image )
   {
      return addNode( label, target, image, null );
   }
   
   /**
    * Add a TreeListNode instance to the current node
    * @param label translated label of the node
    * @param target target url of the node
    * @param image relative path and image name for the node
    * @param expand_data query string name value pairs used to get dynamic nodes.
    * @return The TreeListNode reference to the added node.
    */
   public TreeListNode addNode( String label, String target, String image, String expand_data )
   {
      TreeListNode item = new TreeListNode( label, target, image, expand_data);
      if (DEBUG) Util.debug("ASPNavigatorNode.addNode("+label+") to node: "+this.label);
      add( item );
      return item;
   }
   
   /**
    * Add a TreeListNode instance to the current node
    * @param label translated label of the node
    * @param target target url of the node
    * @param image relative path and image name for the node
    * @param expand_data query string name value pairs used to get dynamic nodes.
    * @param js_tag javascript tag of the node
    * @return The TreeListNode reference to the added node.
    */
   // Added by Terry 20130819
   // Javascript tag of tree list node
   public TreeListNode addNode( String label, String target, String image, String expand_data, String js_tag )
   {
      TreeListNode item = new TreeListNode( label, target, image, expand_data, js_tag );
      if (DEBUG) Util.debug("ASPNavigatorNode.addNode("+label+") to node: "+this.label);
      add( item );
      return item;
   }
   // Added end
   
   /**
    * Add a TreeListItem instance to the current node
    * @param label translated label of the item
    * @return The TreeListItem reference to the added item.
    */
   public TreeListItem addItem( String label )
   {
      return addItem( label, null, null );
   }
   
   /**
    * Add a TreeListItem instance to the current node
    * @param label translated label of the item
    * @param target target url of the item
    * @return The TreeListItem reference to the added item.
    */
   public TreeListItem addItem( String label, String target )
   {
      return addItem( label, target, null );
   }
   
   /**
    * Add a TreeListItem instance to the current node
    * @param label translated label of the item
    * @param target target url of the item
    * @param image relative path and image name for the item
    * @return The TreeListItem reference to the added item.
    */
   public TreeListItem addItem( String label, String target, String image )
   {
      TreeListItem item = new TreeListItem(label,target,image);
      if (DEBUG) Util.debug("ASPNavigatorNode.addItem("+label+") to node: "+this.label);
      add( item );
      return item;
   }
   
   /**
    * Add a TreeListItem instance to the current node
    * @param label translated label of the item
    * @param target target url of the item
    * @param image relative path and image name for the item
    * @return The TreeListItem reference to the added item.
    */
   // Added by Terry 20130819
   // Javascript tag of tree list node
   public TreeListItem addItem( String label, String target, String image, String js_tag )
   {
      TreeListItem item = new TreeListItem(label,target,image,js_tag);
      if (DEBUG) Util.debug("ASPNavigatorNode.addItem("+label+") to node: "+this.label);
      add( item );
      return item;
   }
   // Added end
   
   private void add( Object item )
   {
      if ( item instanceof TreeListNode || item instanceof TreeListItem )
         items.addElement( item );
      //else
      //   throw new FndException("FNDNODNOTIT: Only nodes and items can be included by a node!");
   }

   /**
    * Set the label for this TreeListNode
    * @param label translated label
    */
   public void setLabel(String label)
   {
      this.label = label;
   }
   
   /**
    * get the label of this TreeListNode
    * @return this node's label
    */
   public String getLabel()
   {
      return this.label;
   }

   /**
    * Set the target for this TreeListNode
    * @param target target url for this node
    */
   public void setTarget(String target)
   {
      this.target = target;
   }

   /**
    * get the Target of this TreeListNode
    * @return target url of this node
    */
   public String getTarget()
   {
      return this.target;
   }
   
   /**
    * Set the image for this TreeListNode
    * @param image relative path and image name for this node
    */
   public void setImage(String image)
   {
      this.image = image;
   }

   /**
    * get the image of this TreeListNode
    * @return the image name and relative path of this node
    */
   public String getImage()
   {
      return this.image;
   }

   /**
    * Set the expand data for this TreeListNode
    * @param expand_data query string name value pairs used to get dynamic nodes.
    */
   public void setExpandData(String expand_data)
   {
      this.expand_data = expand_data;
   }

   /**
    * get the expand data of this TreeListNode
    * @return the query string name values pairs used to get dynamic nodes
    */
   public String getExpandData()
   {
      return this.expand_data;
   }

   /**
    * Set the font size 
    * @param font_size int font size
    * @see ifs.fnd.asp.TreeListNode.getFontSize
    */
   public void setFontSize(int font_size)
   {
      this.font_size = font_size;
   }

   /**
    * get the Font size
    * @return int value of font size
    * @see ifs.fnd.asp.TreeListNode.setFontSize
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

   /**
    * Show the current node and all its items (and nodes)
    */
   int list_index = 0;
   
   int show( int level, TreeList tree, int parent_pos, int pos_no, AutoString out )
   {
      if(DEBUG) Util.debug("TreeListNode.show(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);

      int last_pos_no = pos_no+1;
      AutoString buf = new AutoString(); 

      // Modified by Terry 20130819
      // Javascript tag of tree list node item
      // Original:
      // tree.formatItemEntry(level,parent_pos,pos_no,this.label,null,this.image,this.expand_data,false,null,0,0,this.defined_menu_item,buf );
      tree.formatItemEntry(level,parent_pos,pos_no,this.label,null,this.image,this.expand_data,false,null,0,0,this.defined_menu_item,this.js_tag,buf );
      // Modified end

      for( int i=0; i<items.size(); i++ )
      {
         Object element = items.elementAt(i);
         if ( element instanceof TreeListItem )
            last_pos_no = ((TreeListItem)element).show(level+1,tree,pos_no,last_pos_no,false,buf);
         //else if ( element instanceof TreeListNode && (((TreeListNode) element).items.size() > 0) )
         else if ( element instanceof TreeListNode )
         {
            last_pos_no = ((TreeListNode)element).show(level+1,tree,pos_no,last_pos_no,buf);
         }
      }
      //list_index++;

      // Modified by Terry 20130819
      // Javascript tag of tree list node item
      // Original:
      // tree.formatItemEntry2(parent_pos, pos_no, this.label, this.target, this.image, false, level+1, this.menu_items, this.menu_width, this.font_size, this.defined_menu_item, buf);
      tree.formatItemEntry2(parent_pos, pos_no, this.label, this.target, this.image, false, level+1, this.menu_items, this.menu_width, this.font_size, this.defined_menu_item, this.js_tag, buf);
      // Modified end
      //if(last_pos_no>pos_no+1)
      out.append(buf);
      //else
      //   last_pos_no--;

      if(DEBUG) Util.debug("  last_pos_no["+this.label+"]="+last_pos_no);
      return last_pos_no;
   }   
   
   int dynamicShow( int level, TreeList tree, int parent_pos, int pos_no, AutoString out )
   {
      if(DEBUG) Util.debug("TreeListNode.dynamicShow(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);

      int last_pos_no = pos_no+1;
      AutoString buf = new AutoString(); 

      for( int i=0; i<items.size(); i++ )
      {
         Object element = items.elementAt(i);
         if ( element instanceof TreeListItem )
            last_pos_no = ((TreeListItem)element).show(level+1,tree,parent_pos,last_pos_no,true,buf);
         //else if ( element instanceof TreeListNode && (((TreeListNode) element).items.size() > 0) )
         else if ( element instanceof TreeListNode )
         {
            // Modified by Terry 20130819
            // Javascript tag of tree list node item
            // Original:
            // tree.formatItemEntry(level,parent_pos,pos_no+i,((TreeListNode)element).label,null,((TreeListNode)element).image,((TreeListNode)element).expand_data,false,null,0,0,null, buf );
            tree.formatItemEntry(level,parent_pos,pos_no+i,((TreeListNode)element).label,null,((TreeListNode)element).image,((TreeListNode)element).expand_data,false,null,0,0,null,((TreeListNode)element).js_tag,buf);
            // Modified end
            last_pos_no = ((TreeListNode)element).dynamicShow(level+1,tree,pos_no+i,last_pos_no,buf);
            // Modified by Terry 20130819
            // Javascript tag of tree list node item
            // Original:
            // tree.formatItemEntry2(parent_pos, pos_no+i, ((TreeListNode)element).label, ((TreeListNode)element).target, ((TreeListNode)element).image, true, level+1, ((TreeListNode)element).menu_items, ((TreeListNode)element).menu_width, ((TreeListNode)element).font_size, ((TreeListNode)element).defined_menu_item,buf);
            tree.formatItemEntry2(parent_pos, pos_no+i, ((TreeListNode)element).label, ((TreeListNode)element).target, ((TreeListNode)element).image, true, level+1, ((TreeListNode)element).menu_items, ((TreeListNode)element).menu_width, ((TreeListNode)element).font_size, ((TreeListNode)element).defined_menu_item,((TreeListNode)element).js_tag,buf);
            // Modified end
         }
      }
      
      //if(last_pos_no>pos_no+1)
      out.append(buf);
      //else
      //   last_pos_no--;

      if(DEBUG) Util.debug("  last_pos_no["+this.label+"]="+last_pos_no);
      return last_pos_no;
   }   

   //Adding RMB's to TreeListNodes
   /**
    *@deprecated use TreeListNode.addDefinedPopup
    */
   public void addMenuItem(String label)
   {
      addMenuItem(label, null);
   }
   /**
    *@deprecated use TreeListNode.addDefinedPopup
    */
   public void addMenuItem(String label, String tag)
   {
      MenuItem item = new MenuItem(label, tag);
      menu_items.addElement(item);
   }
   /**
    *@deprecated use TreeListNode.addDefinedPopup
    */
   public void addMenuSeparator()
   {
      MenuItem item = new MenuItem();
      menu_items.addElement(item);
   }
   /**
    *@deprecated use TreeListNode.addDefinedPopup
    */
   public void setMenuWidth(int width)
   {
      menu_width = width;
   }
   /**
    *@deprecated use TreeListNode.addDefinedPopup
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
