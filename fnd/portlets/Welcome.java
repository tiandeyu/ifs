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
 * File        : Welcome.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P   2000-Apr-01 - Created.
 *    Sasanka D 2007-Aug-15 - Modified printContents()
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;


public class Welcome extends ASPPortletProvider
{
   public Welcome( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   public String getTitle( int mode )
   {
      return translate(getDescription());
   }


   public static String getDescription()
   {
      return "FNDWELCOMEDESC: Welcome to IFS Applications";
   }


   public void printContents() throws FndException
   {
      //printNewLine();
      printImage( getASPConfig().getImagesLocation()+"Corporate_Layer_Web_Splash.gif");
      //printNewLine();
   }
}


