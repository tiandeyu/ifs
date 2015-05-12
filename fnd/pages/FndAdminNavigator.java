
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
 * File        : FndAdminNavigator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Buddika H   2006-Dec-11 - Bug id: 61535, Removed Common Messages from the navigator.
 *    Buddika H   2006-Sep-19 - Added the Context Substitution Variables page to navigator
 *    Rifki R     2004-Jun-29 - Created 
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;

public class FndAdminNavigator implements ifs.fnd.asp.Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
             
     ASPNavigatorNode nod = mgr.newASPNavigatorNode( "FNDADMINNAVADMIN: Administration" );
     nod.addItem( "FNDADMINNAVHISTORY: History", "common/scripts/History.page");
     nod.addItem( "FNDADMINNAVWEBADMIN: Web Client Admin", "common/scripts/WebClientAdmin.page");     
     return nod;
   }
}


