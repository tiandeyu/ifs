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
 * File        : ASPNavigatorItem.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-Jul-31 - Created
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
 *    Mangala,
 *    Chaminda 1999-Jun-10 - Added public methods getLabel(),getView(),getTarget()
 *                                        To get the private attributes - used by the Java
 *                           Script Navigator.
 *    Jacek P  1999-Jun-10 - Changed acces from public to package for functions
 *                           getLabel(), getView() and getTarget().
 *    Jacek P  1999-Jul-02 - Public functions getLabel(), getView() and getTarget()
 *                           removed due to rewritten code. Changes in
 *                           method show().
 *    Reine A  2000-Apr-05 - Added method getTargetList().
 *    Reine A  2000-May-21 - getTarget() looks for targets with parameters.
 *    Chaminda O 2000-Oct-07 - Added method showBookmark().
 *    Jacek P  2001-Jan-19 - Corrected bug #573 - Navigator shows empty folders
 *                           on lack of access grants to contained objects.
 *                           Even corrected bookmark generation - now the algorithm
 *                           uses the same functions as menus. Removed function
 *                           showBookmark().
 *    Sampath 2003-Aug-26 -  HTML encoded the label
 *    Buddika 2006-Nov-17 -  Modified construct() method to store the PL/SQL Method passed with the target name.
 *                           Modified show() method, call to formatItemEntry() includes pl-sql method name and node page rype.
 *    Sasanka 2006-Dec-15 -  Add new show() method to overload existing method to support generate xml file for navigator.
 *    Buddika 2007-Dec-03 -  IID F1PR1472, Added Mini framework functionality for use on PDA.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;

/**
 * Package class representing an item in the navigator.
 * Used by ASPNavigatorNode class.
 */
class ASPNavigatorItem extends ASPPageSubElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPNavigatorItem");


   //==========================================================================
   // Immutable attributes
   //==========================================================================

   private String label;   // translated label showed in navigator
   private String target;  // target URL
   private String image;   // icons file name
   private String view;    // view name for security check
   private String plsql_method;  // PL-SQL Method for security check
   private boolean stw_page;     // Whether the Navigator Item links to SheduledTaskWizard


   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor. Calls constructor within ASPObject.
    * Instances of this class are created by instances of ASPNavigatorNode
    */
   ASPNavigatorItem( ASPNavigatorNode node )
   {
      super(node);
   }

   ASPNavigatorItem construct( String label, String target, String view, String image )
   {
      ASPManager mgr = getASPManager();
      this.label  = mgr.HTMLEncode(mgr.translate(label));
      this.stw_page = false;
      this.plsql_method = "";
      if(!Str.isEmpty(target))
      {
         int pos = target.indexOf("?");
         if(pos>0 && target.substring(0,pos).endsWith("ScheduledTaskWizard.page"))
         {
            String params = target.substring(pos+1);
            pos = params.indexOf("METHOD_NAME");
            if(pos>-1)
            {
               String method_name = params.substring(pos);
               int start = method_name.indexOf("=")+1;
               int end = method_name.indexOf("&");
               this.plsql_method = (end>0) ?method_name.substring(start, end) :method_name.substring(start);
               this.stw_page =  true;
            }
         }
      }  
      this.target = target;
      this.image  = image;
      this.view   = view;

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
   protected ASPPoolElement clone( Object node ) throws FndException
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
   //  Package functions (called in DEFINED state)
   //==========================================================================

   /**
    * Return view name used for security check.
    *
    * @see ifs.fnd.asp.ASPNavigatorNode#getViewList
    */
   void getViewList( AutoString views )
   {
      if ( !Str.isEmpty(this.view) )
      {
         views.append(this.view);
         views.append(",");
      }
   }

   /**
    * Return target name used for security check.
    *
    * @see ifs.fnd.asp.ASPNavigatorNode#getTargetList
    */
   void getTargetList( AutoString targets )
   {
      if ( !Str.isEmpty(this.target) )
      {

         if ( this.target.indexOf("?")!=-1 )
         {
            targets.append(this.target.substring(0, this.target.indexOf("?")));
         }
         else
         {
            // Modified by Terry 20120822
            // Add frameset in IFS
            // Original: targets.append(this.target);
            if (this.target.contains("IFS_MAIN_FRAME"))
               targets.append(this.target.substring(0, this.target.length() - 26));
            else
               targets.append(this.target);
            // Modified end
         }
         targets.append(",");
      }
   }

   /**
    * Show the current item
    */
   int show( int level, ASPNavigator navigator, int parent_pos, int pos_no, boolean bookmark, AutoString out )
   {
      if(DEBUG) debug("ASPNavigatorItem.show(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);

      if(navigator.formatItemEntry(
                      level,
                      parent_pos,
                      pos_no,
                      this.label,
                      this.target,
                      bookmark ? null : Str.nvl(this.image, navigator.getItemImage()),
                      this.view,
                      bookmark,
                      out,
                      this.plsql_method,
                      this.stw_page))
         pos_no++;
      return pos_no;
   }
   
   int show(int level, ASPNavigator navigator, int parent_pos, int pos_no, AutoString out, boolean toXml)
   {
      if(DEBUG) debug("ASPNavigatorItem.show(["+parent_pos+","+pos_no+"]"+this.label+") at level :"+level);

      if(navigator.formatXmlEntry(
                      level,
                      parent_pos,
                      pos_no,
                      this.label,
                      this.target,
                      toXml,
                      this.view,
                      out,
                      this.plsql_method,
                      this.stw_page))
         pos_no++;
      return pos_no;
   }
   
   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   void showMobile(int level, ASPNavigator navigator)
   {
      navigator.formatMobileEntry(level, this.label, this.target);
   }
}
