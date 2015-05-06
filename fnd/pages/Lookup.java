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
*  File        : Lookup.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-05  - Created Using the ASP file Lookup.asp
*    Mangala        2001-03-05  - Convert to WebKit 3.5
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Lookup extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Lookup");

   //===============================================================
   // Construction
   //===============================================================

   public Lookup(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      getASPManager().removePageId();
      populate();
   }

   public void  populate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPHTMLFormatter fmt = mgr.newASPHTMLFormatter();
      ASPBuffer master;

      trans.addQuery("MASTER",
                      mgr.getQueryStringValue("DATA_SOURCE"),
                      mgr.getQueryStringValue("ATTR_LIST"),
                      mgr.getQueryStringValue("RESTRICT_LIST"),
                      mgr.getQueryStringValue("ORDER_BY"));

      master = mgr.validate(trans).getBuffer("MASTER");

      if( master.countItems() > 0 )
         if (!mgr.isEmpty(master.getBuffer("DATA").getValueAt(0)))
         {
            mgr.responseWrite(fmt.populateString(master));
            mgr.endResponse();
            return;
         }
      mgr.responseWrite("No_Data_Found");
      mgr.endResponse();
   }
}
