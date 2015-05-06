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
 * File        : GenInfo.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P   2000-Apr-03 - Created.
 *    Jacek P   2001-Jan-04- Added translation of links (Link #30256)
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    Rahelk   2002-06-14  - Bug 30903. Added if statement to control navigate link
 *                           popup menu.
 *    Chandana 2003-06-04 -  Modified contents for the new L&F.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.1  2007/07/03	sadhlk
 * Merged Bug 64669, Modified printContents()
 * Revision 1.1  2005/09/15 12:38:02  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/06/27 05:01:10  mapelk
 * Bug fixes for std portlets
 * 
 * ----------------------------------------------------------------------------
 */


package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;


public class GenInfo extends ASPPortletProvider
{
   public GenInfo( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   public String getTitle( int mode )
   {
      return translate(getDescription());
   }


   public static String getDescription()
   {
      return "FNDGENINFODESC: General Information";
   }


   public void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      printNewLine();
      if (mgr.isStdPortlet())
      {
         printLink( "FNDGENINFOTXTSTD: Visit &&IFS Navigator&& to navigate to other IFS Pages.", mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR"));
         printNewLine();
         return;
      }
      printText( "FNDGENINFOTXT1: From the toolbar you can select:" );
      if(!mgr.getPortalPage().getASPProfile().isUserProfileDisabled())
      {
         printNewLine();
         printSpaces(1);
         printText( "-" );
         printSpaces(1);

         printLink( translate("FNDGENINFOMANAGE: Manage Portal"), "javascript:showMenu('mconfig_views',[true,true,true,true],false)" );
         printSpaces(1);
         printText( "FNDGENINFOTXT2: to configure portal page." );
      }
      printNewLine();
      printSpaces(1);
      printText( "-" );
      printSpaces(1);
      printLink( translate("FNDGENINFONAV: Navigate"), getASPManager().getConfigParameter("APPLICATION/LOCATION/NAVIGATOR") );
      printSpaces(1);
      printText( "FNDGENINFOTXT3: to navigate to other pages." );
      printNewLine();
      
      printSpaces(1);
      printText( "-" );
      printSpaces(1);
      printLink( translate("FNDGENINFOOPTINS: Options"), "javascript:showMenu('moptions',[true,true,true],false)" );
      printSpaces(1);
      printText( "FNDGENINFOTXT4: to save links, refresh or to log off." );
      printNewLine();
   }
}


