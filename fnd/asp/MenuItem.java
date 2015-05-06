
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
 * File        : MenuItem.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H    2002-09-02  created.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

public class MenuItem
{
   String label;
   String action;
   boolean separator;
   
   MenuItem()
   {
      // Separator
      label = "<hr>";
      action = "";
      separator = true;
   }

   MenuItem(String l,String a)
   {
      label = l;
      action = a;
   }

   boolean isSeparator()
   {
      return separator;
   }

   
}
