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
*    ASP2JAVA Tool  2001-02-19  - Created Using the ASP file navigator.asp
*    Chaminda  2001-Jun-01 - Log #745 Export Navigator to Bookmarks/Favorites 
*                            doesn’t work.
*    Ramila    2002-06-19  - Bug 31056, Add save links doesn't work.
*    Ramila    2002-06-19  - Bug 31107 Saved link tree navigator doesn't work
*                            openned forms in new window.
*    Daniel    2002-08-30  - Added forceClone and forceReset 'cause we manually 
*                            handles cloning and resetting in this page.
*    Chandana  2004-06-22  - Export Navigator made to use a specific charset for IE and UTF-8 for NN/Mozilla.  
*    Rifki R   2004-06-29  - Added 'Admin' node
*    Sasanka   2006-12-15  - Bug 62442, Add functionality to save navigator in a xml file.
*    Buddika H	2008-07-09	- Bug 75668, IID F1PR432 - Workflow/My Todo functionality .
*    Buddika H 2008-08-12  - Bug 76287, Modified preDefine() to show/hide "My Administration" depending on the Aurora global switch
*    Buddika H 2009-02-13  - Bug 80265, F1PR454 - Templates IID.
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.io.*;


public class Navigator extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.ifs.fnd.scripts.navigator");


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

      if (ASPNavigator.BOOKMARK.equals(menutype))
      {
         if(manager.isExplorer())
         {
            String user_lan = manager.getUserPreferredLanguage();
            String charset = getASPConfig().getParameter("LANGUAGE/"+user_lan+"/EXPORT_NAVIGATOR_CHARSET","UTF-8");
            manager.setAspResponsContentType("text/x-ifs-navigator; charset="+charset);         
            manager.setResponseContentFileName("Navigator.html",true);
         }
         else  
         {
            manager.setAspResponsContentType("text/x-ifs-navigator; charset=UTF-8");         
            manager.setResponseContentFileName("Navigator.html",true);
         }
      }   
      
      else if(ASPNavigator.TOXML.equals(menutype))
      {
          if(manager.isExplorer())
         {
            String user_lan = manager.getUserPreferredLanguage();
            String charset = getASPConfig().getParameter("LANGUAGE/"+user_lan+"/EXPORT_NAVIGATOR_CHARSET","UTF-8");
            manager.setAspResponsContentType("text/xml; charset="+charset);
            manager.setResponseContentFileName("Navigator.xml",true);
            
          }
          else
          {
             manager.setAspResponsContentType("text/xml; charset=UTF-8");
             manager.setResponseContentFileName("Navigator.xml",true);
          }
      }

      /*if (!isDefined())
      {
         preDefine();
         setDefined();
      }
      else*/
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
      rootnode = manager.newASPNavigatorNode( "IFS Applications" );
      
      rootnode.add((new FndMyAdminNavigator()).add(manager));
      rootnode.add((new FndAdminNavigator()).add(manager));
      rootnode.add((new FndSerNavigator()).add(manager));
      rootnode.add((new FndInfNavigator()).add(manager));

      rootnode.findNodes();
      nav = manager.getASPNavigator();
      nav.setDefaultMode();
      nav.setRoot(rootnode);
      
      // Added by Terry 20120822
      // Disable nav header and bar
      disableHeader();
      disableBar();
      // Added end
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
      return new AutoString();
   }

}
