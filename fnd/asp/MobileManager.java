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
 * File        : MobileManager.java
 * Description : A class that provides outside access to ifs.fnd.asp Objects 
 *               methods through accessers.
 * Notes       : Can only be instantiated by a decendant of MobilePageProvider
 * ----------------------------------------------------------------------------
 * Modified    : 
 *       2009/07/09 amiklk Bug 84659, created.
 * ----------------------------------------------------------------------------
 **/

package ifs.fnd.asp;

import ifs.fnd.webmobile.web.*;
import ifs.fnd.service.FndException;

public class MobileManager {
   
   private Object sourceObject;

   public MobileManager(Object sourceObject) throws FndException {
      this.sourceObject = sourceObject;
      
      if( !(sourceObject instanceof MobilePageProvider) )
         throw new FndException("FNDWEBMMGRNOAUTH: Object unauthorized to instantiate the MobileManager");    
   }
   
   public ASPField[] getHyperlinkParameters(ASPField field) throws Exception
   {
         return field.getHyperlinkParameters();
   }
   
   public String getHyperlinkedPresObjectId(ASPField field)
   {
         return field.getHyperlinkedPresObjectId();
   }
   
   public boolean isObjectAccessible(ASPPage page, String object_names)
   {
         return page.isObjectAccessible(object_names);
   }
}
