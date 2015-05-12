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
 * File        : AbstractDataRow.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2008/06/26 mapelk - Bug 74852, Programming Model for Activities. 
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.DataFormatter;
import ifs.fnd.buffer.ItemNotFoundException;

/**
 * 
 * 
 */
public abstract class AbstractDataRow 
{
   ASPPage page;
   
   String convertToClientString(ASPField field) throws Exception
   {
      return convertToClientString(field,field.getDataFormatter());
   }
   
   void debug(String line)
   {
      page.debug("**************************************************************");
      page.debug(line);
      page.debug("**************************************************************");
   }   
   
   abstract String getString(ASPField field,String default_value);
   abstract String convertToClientString(ASPField field, DataFormatter formatter) throws Exception;
   
   
   abstract int countColumns();
   abstract boolean isNULL();
   
   abstract int getAttributePosition(String name);
   abstract String getValue(ASPField f) throws ItemNotFoundException;
   abstract String getValueAt(int i) throws ItemNotFoundException ;
   abstract String getValue(String name) throws ItemNotFoundException;
   abstract String getType();
   abstract String getStatus();
}
