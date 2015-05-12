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
 * File        : DefineGlobalFactory.java
 * Description : Used to define globals variables for standard portlets.
 * Notes       : The class extending this factory class must be defined in a config file
 *               eg:
 *                 <global>
 *                   <company>
 *                    <url>&amp;(APPLICATION/LOCATION/ROOT)enterw/DefineCompany.page</url>
 *                    <class>ifs.enterw.portlets.DefineCompany</class>
 *                   </company>
 *                  </global>
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H 2004-11-08 - Created.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.portal;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;


public abstract class DefineGlobalFactory
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portal.DefineGlobalFactory");
   
   /** Creates a new instance of GlobalVariable */
   public DefineGlobalFactory()
   {
   }
   
   public abstract void defineGlobals(ASPContext ctx, ASPManager mgr) throws FndException;
   
   
   protected void setGlobal( ASPContext ctx, String name, String value )
   {
      if(DEBUG) debug("GlobalVariable.setGlobal("+name+","+value+")");
      ctx.setGlobal(name, value==null ? "" : value);
   }

   protected void debug(String msg)
   {
      Util.debug(msg+"\n");
   }
   
}
