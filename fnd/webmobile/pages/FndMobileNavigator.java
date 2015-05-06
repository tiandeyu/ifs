
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
 * File        : FndMobileNavigator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * created     : buhilk
 * ----------------------------------------------------------------------------
 * 2007-Dec-20 buhilk Bug ID: 70052 - Error loading window descriptions for miniFW.
 * 2007-Dec-03 buhilk IID F1PR1472 - Improved miniFW functionality.
 */


package ifs.fnd.webmobile.pages;

import ifs.fnd.asp.*;
import ifs.fnd.webmobile.web.*;

public class FndMobileNavigator implements MobileNavigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
     ASPNavigatorNode nod = mgr.newASPNavigatorNode("FNDSERNAVGENERAL: General");
     if(ASPConfigFile.multi_language_enabled)
        nod.addItem( "FNDLNGSELECTDESC: Change Language", "webmobile/MobileLanguageSelector.page");
     return nod;
   }
}


