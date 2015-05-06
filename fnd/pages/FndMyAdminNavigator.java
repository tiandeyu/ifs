
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
 * File        : FndMyAdminNavigator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    buhilk0  15-Feb-2008    Created.
 * ----------------------------------------------------------------------------
 * $Log: FndMyAdminNavigator.java,v $
 *
 * New Comments:
 * 13-02-2009 buhilk Bug id 80265, F1PR454 - Templates IID.
 * 09-07-2008 buhilk Created navigator notde for MyTodo Task pages
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;

public class FndMyAdminNavigator implements ifs.fnd.asp.Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
             
     ASPNavigatorNode nod = mgr.newASPNavigatorNode( "FNDMYADMINNAVADMIN: My Administration" );
     if(mgr.isAuroraFeaturesEnabled())
     {
        nod.addItem( "FNDMYADMINTASKS: Tasks", "fnd/web/features/managemytodo/MyToDoTask.page?MyTodoFolderType=Todo&SEARCH=Y", "HZ_WF_ADMIN");
        nod.addItem( "FNDMYADMINSENTTASKS: Sent Tasks", "fnd/web/features/managemytodo/MyToDoTask.page?MyTodoFolderType=SentItems&SEARCH=Y", "HZ_WF_ADMIN");
     }
     nod.addItem( "FNDMYADMINTEMPLATES: Templates", "common/scripts/Templates.page");
     return nod;
   }
}


