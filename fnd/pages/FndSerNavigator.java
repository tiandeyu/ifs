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
 * File        : FndSerNavigator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Artur K   2001-Mar-03 - Created
 *    Jacek P   2001-Mar-28 - Removed deprecated functions. Changed .asp to .page
 *                            Logid #599: Default IFS URL changed to ifsworld.com
 *    Kingsly P 2001-May-30 - Add node History.
 *    Ramila H  2001-Jun-11 - changed according to log id 755.
 *    Suneth M  2001-Oct-19 - Added Subscribe Event Actions node.
 *    Mangala   2003-Jan-01 - Added Common Message Admin node.
 *    Suneth M  2003-Jul-29 - Added General Configurations node. 
 *    Ramila H  2003-Jul-30 - Log id 853, added Change Password node.
 *    Chandana D2003-Aug-13 - Reads url_item_img from the config file.
 *    Chandana D2003-Sep-04 - Removed 'About' from the IFS Navigator. 
 *    Chandana D2003-Sep-17 - Relavant nodes renamed to Export Tree Navigator, Subscriptions & Common Messages.
 *    Ramila H  2004-05-18  - Added scheduled task node.
 *    Rifki R   2004-Jun-29 - Moved History.page and IFSCommonMsgAdmin.page to seperate 'Admin' node
 *    Mangala   2006-Sep-25 - Added item Context Substitution Variables
 *    Mangala  2007-01-30   - Bug 63250, Added theming support in IFS clients.
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;

public class FndSerNavigator implements ifs.fnd.asp.Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
      
      String navigator_style = mgr.getASPConfig().getParameter("NAVIGATOR/STYLE","classic");
      String url_item_img    = mgr.getASPConfig().getParameter("NAVIGATOR/"+navigator_style+"/IMAGE/ITEM/URL","navigator_url_classic.gif"); 
       
      ASPNavigatorNode nod = mgr.newASPNavigatorNode( "FNDSERNAVGENERAL: General" );
      // nod.addItem( "FNDSERNAVIIFSONWEB: IFS on the Web", "http://www.ifsworld.com", "", url_item_img);
      // nod.addItem( "FNDSERNAVSUPPORT: IFS Support", "common/scripts/Link.page", "", url_item_img);
      nod.addItem( "FNDSERNAVBACKG: Background Jobs", "common/scripts/DeferredJob.page", "Deferred_Job");
      //nod.addItem( "FNDSERNAVABOUT: About", "common/scripts/About.page" );
      nod.addItem( "FNDSERNAVEXPORT: Export Tree Navigator", "common/scripts/ExportNavigator.page");
      //nod.addItem( "FNDSERNAVHISTORY: History", "common/scripts/History.page");
      nod.addItem( "FNDSERNAVSUBEVEACT: Subscriptions", "common/scripts/SubscribeEventActions.page");
      //nod.addItem( "FNDSERNAVCOMMONMSG: Common Messages", "common/scripts/IFSCommonMsgAdmin.page");
      //nod.addItem( "FNDSERNAVGENCONF: General Configurations", "common/scripts/GeneralConfigurations.page");
      nod.addItem( "FNDSERNAVCSV: Context Substitution Variables", "common/scripts/ContextSubtitutionVars.page");      

      nod.addItem( "FNDSERNAVSCHTASK: Scheduled Tasks", "common/scripts/ScheduledTask.page");
      
      return nod;
   }
}
