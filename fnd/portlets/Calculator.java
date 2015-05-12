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
 * File        : Calculator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Dec-06 - Created.
 *    Jacek P  2000-Apr-01 - Changed package to fnd.portlets
 *    Jacek P  2000-May-05 - Added method predefine()
 *    Jacek P  2000-Aug-07 - Added getMinWidth().
 *    Jacek P  2000-Aug-11 - New implementation compatible with Netscape (Log id #122).
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;


/**
 */
public class Calculator extends ASPPortletProvider
{
   public Calculator( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   public String getTitle( int mode )
   {
      return translate( getDescription() );
   }

   
   public static int getMinWidth()
   {
      return 144;
   }


   public static String getDescription()
   {
      return "FNDCALCDESC: Calculator";
   }

   
   public void preDefine() throws FndException
   {
      String scrloc = getScriptsLocation().substring(getASPConfig().getApplicationContext().length());
      includeJavaScriptFile(scrloc + "CalculatorScript.js");
   }
   
   
   public void printContents() throws FndException
   {
      String id = getId();
      String imgloc = getImagesLocation();
      
      printField("THEVALUE", "", 16);
      printNewLine();

      beginTable("calctab cellpadding=0",true,false);
      beginTableBody();

      //row 1
      beginTableCell();
      printScriptImageLink(imgloc + "calc_7.gif", "setVal('7');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_8.gif", "setVal('8');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_9.gif", "setVal('9');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_div.gif", "setVal('divide');doOp");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_clr.gif", "setVal('clear');doOp");
      endTableCell();
      
      nextTableRow();
      //row 2
      beginTableCell();
      printScriptImageLink(imgloc + "calc_4.gif", "setVal('4');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_5.gif", "setVal('5');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_6.gif", "setVal('6');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_mlt.gif", "setVal('times');doOp");
      endTableCell();

      beginTableCell();
      printImage(imgloc + "calc_emp.gif",36,17);
      endTableCell();

      nextTableRow();
      //row 3
      beginTableCell();
      printScriptImageLink(imgloc + "calc_1.gif", "setVal('1');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_2.gif", "setVal('2');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_3.gif", "setVal('3');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_sub.gif", "setVal('minus');doOp");
      endTableCell();

      beginTableCell();
      printImage(imgloc + "calc_emp.gif",36,17);
      endTableCell();

      nextTableRow();
      //row 4
      beginTableCell();
      printScriptImageLink(imgloc + "calc_0.gif", "setVal('0');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_dot.gif", "setVal('.');eN");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_inv.gif", "setVal('invert');doOp");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_add.gif", "setVal('plus');doOp");
      endTableCell();

      beginTableCell();
      printScriptImageLink(imgloc + "calc_eql.gif", "setVal('equals');doOp");
      endTableCell();

      endTableBody();
      endTable();

      appendDirtyJavaScript("var calculator",id," = new Object;\nsetVal('clear');\ndoOp(null,'",id,"');\n");
   }
}