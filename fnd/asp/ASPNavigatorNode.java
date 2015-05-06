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
 * File        : ASPNavigatorNode.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-Jul-31 - Created
 *    Jacek P  1998-Aug-10 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-20 - Changes due to redesigned structure of
 *                           the ASPConfig.ifm file (Log id:#2623).
 *    Jacek P  1998-Aug-28 - Corrected bug # 2658: Navigator do not accept
 *                           list of views for security check.
 *    Jacek P  1998-Aug-31 - Changed names of GIF files to not include space
 *                           sign due to problems in Netscape.
 *    Jacek P  1998-Nov-05 - Added label translation on object construction.
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageSubElement instead of ASPObject.
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Mar-08 - Implemented pool concept.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-Jun-10 - Changed access from private to package for vector 'items'.
 *    Jacek P  1999-Jul-02 - Access for 'items' changed back to private due to
 *                           rewritten code for JavaScript generation. Changes
 *                           in method show().
 *    Reine A  2000-Apr-05 - Added method getTargetList().
 *    Reine A  2000-Apr-20 - Added tree new methods for adding reports to the
 *                           Navigator - addReport().
 *    Stefan M 2000-May-30 - Empty nodes are not displayed anymore.
 *    Chaminda O 2000-Oct-07 - Added method showBookmark().
 *    Jacek P  2001-Jan-19 - Corrected bug #573 - Navigator shows empty folders
 *                           on lack of access grants to contained objects.
 *                           Even corrected bookmark generation - now the algorithm
 *                           uses the same functions as menus. Removed function
 *                           showBookmark().
 *    Artur K 2001-Feb-21 -  Added findNodes() function
 *    Artur K 2001-Apr-22 -  Changes for handling node order in the tree navigator. The order
 *                           should be declared in ASPConfig file as value of parameter NAVIGATOR/ORDER.
 *    Chandana D 2001-Jun-29-Improved comments
 *    Jacek P 2003-Feb-04 -  Added support for JAR files. Algorithm for finding nodes re-written.
 *    Mangala 2003-Mar-16 -  Avoid adding fnd item while finding nodes in JAR files. 
 *    Sampath 2003-Aug-26 -  HTML encoded the label 
 *
 * -----------------------------------------------------------------------------------------------------------
 * New Comments:
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/05/22 buhilk Bug id 65519, Improved navigator nodes to be attached to the tree in alphabetical order.
 * 2006/08/18 gegulk Bug id 59985, Removed the usages of the word "enum" as variable names
 * 2006/12/15 sadhlk Bug id 62442,  Add new show() method to overload existing method to support generate xml file for navigator.
 */

package ifs.fnd.asp;

import ifs.fnd.webmobile.web.MobileNavigator;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.os.*;
import java.util.*;
import java.util.jar.*;
import java.io.*;           
import java.lang.reflect.*;

/**
 * Creates a node for the Navigator (ASPNavigator).
 * A node can consist of Items (ASPNavigatorItem) or or/and other nodes.
 *    <b>Example:</b>
 *    <pre>
 *       public class DemorwNavigator implements Navigator
 *       {
 *           public ASPNavigatorNode add(ASPManager mgr)
 *           {
 *              ASPNavigatorNode nod = mgr.newASPNavigatorNode( "DEMORWDEMO: Foundation1/DEMO 2.2.3" );
 *              nod.addItem("SA: Web Kit Admin", "common/scripts/WebkitAdmin.page", "");
 *              ASPNavigatorNode n = nod.addNode( "DEMORWORD: Order" );
 *              n.addItem("DEMORW: Order Entry", "demorw/Order.page", "DEMO_ORDER" );
 *              n.addItem("DEMORWHEAD: Order Headers", "demorw/OrderHeader.page", "DEMO_ORDER" );
 *              n.addItem("DEMORWITEM: Order Items", "demorw/OrderItems.page", "DEMO_ORDER" );
 *              n.addItem("DEMORWPROC: Order Processing", "demorw/OrderProcessing.page", "DEMO_ORDER" );
 *              n = nod.addNode( "DEMORWINV: Invoice" );
 *              n.addItem("DEMORWINV: Invoice","demorw/Invoice.page","DEMO_INVOICE" );
 *              n = nod.addNode( "DEMORWCUST: Customer" );
 *              n.addItem("DEMORWCUST: Customer","demorw/Customer.page","DEMO_CUSTOMER" );
 *              nod.addItem("DEMORWCOMP: Select Company", "demorw/DefineCompany.page","DEMO_COMPANY" );
 *              return nod;
 *           }
 *       }
 *    </pre>
 */
public class ASPNavigatorNode extends ASPPageSubElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPNavigatorNode");


   //==========================================================================
   // Immutable attributes
   //==========================================================================

   private Vector items;            // translated label showed in navigator
   private String label;            // target URL
   private String image;            // icons file name
   private String view;             // view name for security check
   private String target;           // presentation object id for security check
   private String sort_label;       // label used to sort node within tree 
   private SortedMap sortedNodsMap; // collection of nodes in sorted order
   

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor. Calls constructor within ASPObject.
    * A new instance of ASPNavigatorNode is created by function(s)
    * newASPNavigatorNode() within ASPManager class.
   */
   ASPNavigatorNode(ASPNavigator navigator)
   {
      super(navigator);
   }

   ASPNavigatorNode construct( String label, String view, String image )
   {
      ASPManager mgr = getASPManager();
      this.items = new Vector();
      this.sort_label = mgr.translate(label).trim();
      this.label = mgr.HTMLEncode(this.sort_label);
      this.view  = view;
      this.image = image;
      this.target = new String();
      
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
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
   }


   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object navigator ) throws FndException
   {
      // cloning not needed due to navigator does not clone its read only components.
      return null;
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
    * Add a simple item to the current node.
    * @param label Translated label showed in navigator
    * @param target URL of the Navigator Item
    */
   public void addItem( String label, String target )
   {
      int pos = target.indexOf("?");
      String conn_string = "";
      if (pos > 0 && target.substring(0,pos).endsWith("ScheduledTaskWizard.page"))
    	  conn_string = "&";
      // Modified by Terry 20120822
      // Add frameset in IFS
      // Original: addItem( label, target, null, null );
      addItem( label, target + conn_string + "' target='IFS_MAIN_FRAME'\\", null, null );
      // Modified end
   }
   /**
    * Add a simple item to the current node.
    * @param label Translated label showed in navigator
    * @param target URL of the Navigator Item
    * @param view View name for security check
    */
   public void addItem( String label, String target, String view )
   {
	  // Modified by Terry 20120822
	  // Add frameset in IFS
      // Original: addItem( label, target, view, null );
      addItem( label, target + "' target='IFS_MAIN_FRAME'\\", view, null );
      // Modified end
   }
   /**
    * Add a simple item to the current node.
    * @param label Translated label showed in navigator
    * @param target URL of the Navigator Item
    * @param view View name for security check
    * @param image Icons file name
    */
   public void addItem( String label, String target, String view, String image )
   {
      try
      {
         modifyingImmutableAttribute("NAVIGATOR_ITEMS");
         ASPNavigatorItem item = (new ASPNavigatorItem(this)).construct(label,target,view,image);
         if (DEBUG) debug("ASPNavigatorNode.addItem("+label+") to node: "+this.label);
         add( item );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Add a Report item to the current node.
    * @param label Translated label showed in navigator
    * @param target URL of the Navigator Item   
    */
   public void addReport( String label, String target )
   {
      addReport( label, target, null, null );
   }
   /**
    * Add a Report item to the current node.
    * @param label Translated label showed in navigator
    * @param target URL of the Navigator Item
    * @param view View name for security check
    */
   public void addReport( String label, String target, String view )
   {
      addReport( label, target, view, null );
   }
   /**
    * Add a report item to the current node.
    * @param label Translated label showed in navigator
    * @param target URL of the Navigator Item
    * @param view View name for security check
    * @param image Icons file name
    */
   public void addReport( String label, String target, String view, String image )
   {
      try
      {
         modifyingImmutableAttribute("NAVIGATOR_ITEMS");
         ASPNavigatorItem item = (new ASPNavigatorItem(this)).construct(label,target,view,image);
         if (DEBUG) debug("ASPNavigatorNode.addReport("+label+") to node: "+this.label);
         add( item );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Create a new node and add it to the current node.
    * Return the reference to the newly created node.
    * @param label Translated label showed in navigator
    * @return ifs.fnd.asp.ASPNavigator <pre>
    *         Reference to the newly created node</pre>
    */
   public ASPNavigatorNode addNode( String label )
   {
      return addNode( label, null, null );
   }
   /**
    * Create a new node and add it to the current node.
    * @param label Translated label showed in navigator 
    * @param view View name for security check
    * @return ifs.fnd.asp.ASPNavigator <pre>
    *         Reference to the newly created node</pre>
    */
   public ASPNavigatorNode addNode( String label, String view )
   {
      return addNode( label, view, null );
   }
   /**
    * Create a new node and add it to the current node.
    * @param label Translated label showed in navigator 
    * @param view View name for security check
    * @param image Icons file name
    * @return ifs.fnd.asp.ASPNavigator <pre>
    *         Reference to the newly created node</pre>
    * @see ifs.fnd.asp.ASPManager#newASPNavigatorNode
    */
   public ASPNavigatorNode addNode( String label, String view, String image )
   {
      try
      {
         modifyingImmutableAttribute("NAVIGATOR_NODES");
         ASPNavigatorNode item = getASPManager().newASPNavigatorNode( label, view, image );
         if (DEBUG) debug("ASPNavigatorNode.addNode("+label+") to node: "+this.label);
         add( item );
         return item;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Add an already existing item or node to the current node
    * @param item Refernce to the Item to be added
    * @see ifs.fnd.asp.ASPNavigatorItem
    */
   public void add( ASPObject item )
   {
      try
      {
         if ( item instanceof ASPNavigatorNode || item instanceof ASPNavigatorItem )
            items.addElement( item );
         else
            throw new FndException("FNDNODNOTIT: Only nodes and items can be included by a node!");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Package functions called from ASPNavigator.setRoot() (must be UNDEFINED)
   //==========================================================================

   // used only for debugging
   String getLabel()
   {
      return this.label;
   }

   String getSortLabel()
   {
      return this.sort_label;
   }
   
   String getImage()
   {
      return this.image;
   }

   void setImage( String image ) throws FndException
   {
      modifyingImmutableAttribute("IMAGE");
      this.image = image;
   }

   //==========================================================================
   //  Package functions (called in DEFINED state)
   //==========================================================================

   /**
    * Return a comma separated list of all views used for security check
    * by the current node and all other nodes and items included by it.
    */
   void getViewList( AutoString views )
   {
      if ( !Str.isEmpty(this.view) )
      {
         views.append(this.view);
         views.append(",");
      }

      for( int i=0; i<items.size(); i++ )
      {
         if ( items.elementAt(i) instanceof ASPNavigatorItem )
            ((ASPNavigatorItem)(items.elementAt(i))).getViewList(views);
         else if ( items.elementAt(i) instanceof ASPNavigatorNode )
            ((ASPNavigatorNode)(items.elementAt(i))).getViewList(views);
      }
   }

   /**
    * Return a comma separated list of all Presentation Object Id:s used for security check
    * by the current node and all other nodes and items included by it.
    */
   void getTargetList( AutoString targets )
   {
      if ( !Str.isEmpty(this.target) )
      {
         targets.append(this.target);
         targets.append(",");
      }

      for( int i=0; i<items.size(); i++ )
      {
         if ( items.elementAt(i) instanceof ASPNavigatorItem )
            ((ASPNavigatorItem)(items.elementAt(i))).getTargetList(targets);
         else if ( items.elementAt(i) instanceof ASPNavigatorNode )
            ((ASPNavigatorNode)(items.elementAt(i))).getTargetList(targets);
      }
   }

   /**
    * Show the current node and all its items (and nodes)
    */
   int show( int level, ASPNavigator navigator, int parent_pos, int pos_no, boolean bookmark, AutoString out )
   {
      if(DEBUG) debug("ASPNavigatorNode.show(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);

      int last_pos_no = pos_no+1;
      AutoString buf = new AutoString(); // I can not use a class instance of AutoString because
                                         // this class is never cloned

      if( navigator.formatItemEntry(level,
                                    parent_pos,
                                    pos_no,
                                    this.label,
                                    null,
                                    bookmark ? null : Str.nvl(this.image, navigator.getNodeImage()),
                                    this.view,
                                    bookmark,
                                    buf ) )
      {
         for( int i=0; i<items.size(); i++ )
         {
            ASPPageSubElement element = (ASPPageSubElement)(items.elementAt(i));
            if ( element instanceof ASPNavigatorItem )
               last_pos_no = ((ASPNavigatorItem)element).show(level+1,navigator,pos_no,last_pos_no,bookmark,buf);
            else if ( element instanceof ASPNavigatorNode && (((ASPNavigatorNode) element).items.size() > 0) )
               last_pos_no = ((ASPNavigatorNode)element).show(level+1,navigator,pos_no,last_pos_no,bookmark,buf);
         }
         navigator.formatItemEntry2(parent_pos, pos_no, this.label, bookmark, buf);
         if(last_pos_no>pos_no+1)
            out.append(buf);
         else
            last_pos_no--;
      }
      if(DEBUG) debug("  last_pos_no["+this.label+"]="+last_pos_no);
      return last_pos_no;
   }
   
   int show( int level, ASPNavigator navigator, int parent_pos, int pos_no, AutoString out, boolean toXml )
   {
      if(DEBUG) debug("ASPNavigatorNode.show(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);

      int last_pos_no = pos_no+1;
      AutoString buf = new AutoString(); // I can not use a class instance of AutoString because
                                         // this class is never cloned

      if( navigator.formatXmlEntry( level,
                                    parent_pos,
                                    pos_no,
                                    this.label,
                                    null,
                                    toXml,
                                    this.view,
                                    buf,
                                    "",
                                    false) )
      {
         for( int i=0; i<items.size(); i++ )
         {
            ASPPageSubElement element = (ASPPageSubElement)(items.elementAt(i));
            if ( element instanceof ASPNavigatorItem )
               last_pos_no = ((ASPNavigatorItem)element).show(level+1,navigator,pos_no,last_pos_no,buf,toXml);
            else if ( element instanceof ASPNavigatorNode && (((ASPNavigatorNode) element).items.size() > 0) )
               last_pos_no = ((ASPNavigatorNode)element).show(level+1,navigator,pos_no,last_pos_no,buf,toXml);
         }
         navigator.formatXmlEntry2(parent_pos, pos_no, this.label, toXml, buf);
         if(last_pos_no>pos_no+1)
            out.append(buf);
         else
            last_pos_no--;
      }
      if(DEBUG) debug("  last_pos_no["+this.label+"]="+last_pos_no);
      return last_pos_no;
   }


   //==========================================================================
   //  The new implementation of Navigator scanning with support for JAR's
   //==========================================================================

   /**
    * Find and instantiate all Navigator Nodes. A Navigator Node has to be
    * implemented as a Java class matching name ifs.<component_name>.*Navigator
    * and extend the ifs.fnd.asp.Navigator class.
    * Nodes are presented in alphabetical order with exception for nodes that
    * are enumerated in the config parameter NAVIGATOR/ORDER. This is possible
    * to have several Navigator classes per component.
    */
   public void findNodes()
   {
      if(DEBUG) debug(this+"findNodes()");
      ASPManager mgr = getASPManager();
      ASPConfig  cfg = mgr.getASPConfig();
      sortedNodsMap = new TreeMap();
      
      String topdir = cfg.getApplicationContextPhyPath()+"WEB-INF"+OSInfo.OS_SEPARATOR;
      
      // Build class path with both WEB-INF\classes and JAR files in WEB-INF\lib
      AutoString navigatorpath = new AutoString();
      navigatorpath.append(topdir,"classes;");
      File file = new File(topdir+"lib");
      if( file.exists() && file.isDirectory() )
      {
         File[] files = file.listFiles();
         for( int i=0; i<files.length; i++ )
            if( files[i].isFile() && files[i].getName().endsWith(".jar") )
               navigatorpath.append(files[i].getPath(), ";");
      }
      if(DEBUG) debug("  navigatorpath="+navigatorpath);
      
      SortedMap components = new TreeMap();

      // Build up a list of all available Navigator classes in the class path above
      StringTokenizer st = new StringTokenizer(navigatorpath.toString(), ";");
      while( st.hasMoreTokens() )
      {
         file = new File(st.nextToken());
         if( !file.exists() )
            continue;
         
         if( file.isFile() && file.getName().endsWith(".jar") )
            searchJarArchive(file, components);
         else if( file.isDirectory() )
            searchDirectory(file, components);
      }
         
      // Search classes in the requested order
      String nav_order = ","+cfg.getParameter("NAVIGATOR/ORDER", "")+",";
      st = new StringTokenizer(nav_order, ",");
      while( st.hasMoreTokens() )
      {
         String comp = st.nextToken();
         SortedSet list = (SortedSet)components.remove(comp);
         if(list == null) continue;
         instantiateClasses(list, true);
      }
         
      // Search for other classes
      Iterator itr = components.values().iterator();
      while( itr.hasNext() )
      {
         SortedSet list = (SortedSet)itr.next();
         instantiateClasses(list, false);
      }
      
      addSortedNodes();
   }


   private void addSortedNodes()
   {
      Iterator navItr = sortedNodsMap.values().iterator();
      while(navItr.hasNext())
      {
         ASPNavigatorNode node = (ASPNavigatorNode) navItr.next();
         add(node);
      }
   }
   
   private void searchJarArchive( File file, SortedMap components )
   {
      boolean mobile = getASPManager().isMobileVersion();
      if(DEBUG) debug(this+"searchJarArchive():"+file.getAbsolutePath());
      try
      {
         JarFile jar_file = new JarFile(file);
         Enumeration jar_file_list = jar_file.entries();
         while( jar_file_list.hasMoreElements() )
         {
            String name = ((JarEntry)(jar_file_list.nextElement())).getName();
            if (name.startsWith("ifs/fnd/")) continue; //fnd items are already included - should not duplicate.
            String endWithStr = "Navigator.class";
            if(mobile) endWithStr = "MobileNavigator.class";
            if(!mobile && name.endsWith("MobileNavigator.class")) continue;                   
            if( name.startsWith("ifs/") && name.endsWith(endWithStr) && name.indexOf("$")<0 )
            {
               name = Str.replace(name, "/", ".");
               name = name.substring(0,name.length()-6);
               int ix1 = name.indexOf('.');
               int ix2 = name.indexOf('.',ix1+1);
               if( ix1<0 || ix2<0 || ix2-ix1==1 ) continue;
               String comp = name.substring(ix1+1, ix2);
               addComponentClass(comp, name, components);
               if(DEBUG) debug("  Found navigator candidate: "+name);
            }
            else
            {
               if(DEBUG) debug("Jump over file: " + OSInfo.OS_SEPARATOR+name);
            }
         }
      }
      catch( Throwable any )
      {
         if(DEBUG) debug(any+" [path="+file.getPath() );
      }
   }


   private void searchDirectory( File file, SortedMap components )
   {
      boolean mobile = getASPManager().isMobileVersion();
      File ifsdir = new File(file,"ifs");
      if( ifsdir.exists() && ifsdir.isDirectory() )
      {
         String[] dirlist = ifsdir.list();
         for( int i=0; i<dirlist.length; i++ )
         {
            String comp = dirlist[i];
            File compdir = new File(ifsdir,comp);
            if( compdir.exists() && compdir.isDirectory() )
            {
               String[] clslist = compdir.list();
               for( int j=0; j<clslist.length; j++ )
               {
                  String name = clslist[j];
                  File clsfile = new File(compdir,name);
                  String endWithStr = "Navigator.class";
                  if(mobile) endWithStr = "MobileNavigator.class";
                  if(!mobile && name.endsWith("MobileNavigator.class")) continue;                   
                  if( clsfile.isFile() && name.endsWith(endWithStr) && name.indexOf("$")<0 )
                  {
                     name = "ifs."+comp+"."+name.substring(0,name.length()-6);
                     addComponentClass(comp, name, components);
                     if(DEBUG) debug("  Found navigator candidate: "+name);
                  }
               }
            }
         }
      }
   }


   private void addComponentClass( String comp, String clsname, SortedMap components )
   {
      SortedSet list = (SortedSet)components.get(comp);
      if(list==null)
      {
         list = new TreeSet();
         components.put(comp, list);
      }
      list.add(clsname);
   }


   private void instantiateClasses( SortedSet list, boolean ordered )
   {
      Iterator itr = list.iterator();
      while( itr.hasNext() )
         instantiateClass( (String)itr.next(), ordered);
   }


   private void instantiateClass( String clsname, boolean ordered)
   {
      if(DEBUG) debug(this+"classInsanitation("+clsname+")");
      ASPManager mgr = getASPManager();
      ASPPage page   = mgr.getASPPage();
      try
      {
         Class cls = Class.forName(clsname);
         if(cls.isInterface())                        return;
         if(Modifier.isAbstract(cls.getModifiers()))  return;
         Class in  = Class.forName("ifs.fnd.asp.Navigator");
         Class m_in  = Class.forName("ifs.fnd.webmobile.web.MobileNavigator");
         if(!in.isAssignableFrom(cls) && !m_in.isAssignableFrom(cls))return;

         Constructor ctr = cls.getConstructor(new Class[0]);
         ASPNavigatorNode node;
         
         if(mgr.isMobileVersion())
         {
             MobileNavigator navigator = (MobileNavigator)(ctr.newInstance(new Object[0]));
             node = navigator.add(mgr);
         }
         else
         {
             Navigator navigator = (Navigator)(ctr.newInstance(new Object[0]));
             node = navigator.add(mgr);
         }

         if(ordered)
            add(node);
         else
            sortedNodsMap.put(node.getSortLabel(), node);
         if(DEBUG) debug("  Found navigator node: "+clsname);
      }
      catch( ClassNotFoundException e )
      {
         page.logError(e);
      }
      catch( NoSuchMethodException e )
      {
         page.logError(e);
      }
      catch( IllegalAccessException e )
      {
         page.logError(e);
      }
      catch( Throwable any )
      {
         page.error(any);
      }
   }
      
   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   void showMobile(int level, ASPNavigator mobileNavigator)
   {
      mobileNavigator.formatMobileEntry(level, this.label,null);
      for( int i=0; i<items.size(); i++ )
      {
         ASPPageSubElement element = (ASPPageSubElement)(items.elementAt(i));
         if ( element instanceof ASPNavigatorItem )
            ((ASPNavigatorItem)element).showMobile((level+1), mobileNavigator);
         else if ( element instanceof ASPNavigatorNode && (((ASPNavigatorNode) element).items.size() > 0) )
         {
            ((ASPNavigatorNode)element).showMobile((level+1),mobileNavigator);
         }
      }
    }
}

   

