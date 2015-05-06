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
*  File        : navigator.java
*  Modified    :
*  BUHILK      2007-Jul-20    - Created.
* ----------------------------------------------------------------------------
* New Comments:
* 2007-Dec-03 buhilk IID F1PR1472 - Improved miniFW functionality.
*/


package ifs.fnd.webmobile.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.webmobile.web.*;
import ifs.fnd.*;

import java.io.*;


public class Navigator extends MobilePageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.webmobile.pages.navigator");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPNavigator nav;
   private ASPNavigatorNode rootnode;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String menutype;

   //===============================================================
   // Construction
   //===============================================================
   public Navigator(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      menutype   = null;
      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      Navigator page = (Navigator)(super.clone(obj));

      // Initializing mutable attributes
      page.menutype   = null;

      // Cloning immutable attributes
      page.nav = this.nav;
      page.rootnode = this.rootnode;
      return page;
   }

   public void run()
   {
      ASPManager manager = getASPManager();
      menutype = manager.getQueryStringValue("MAINMENU");
      
      nav = getASPNavigator();
     
      if( !manager.isEmpty(menutype) )
         nav.setMenuType(menutype);
      else
         manager.removePageId();
   }


   public void  preDefine()
   {
      ASPManager manager = getASPManager();


      manager.getASPPage().disableConfiguration();
      rootnode = manager.newASPNavigatorNode("FNDPAGESABOUTWINDOWIFSAPPLICATIONALT: IFS Applications");
      
      rootnode.add((new FndMobileNavigator()).add(manager));

      rootnode.findNodes();
      nav = manager.getASPNavigator();
      nav.setDefaultMode();
      nav.setRoot(rootnode);
   }

 /**
  * This page deals with cloning and reseting manually.
  */
   protected boolean forceReset()
   {
      return true;
   }

   protected boolean forceClone()
   {
      return true;
   }
   


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return null;
   }

   protected String getTitle()
   {
      return null;
   }

   protected AutoString getContents() throws FndException
   {
      return getOutputStream();
   }

}
