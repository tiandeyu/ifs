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
 * File        : AbstractDataRowCollection.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2008/06/26 mapelk - Bug 74852, Programming Model for Activities.
 *
 */

package ifs.fnd.asp;

/**
 * Implementation of specific data collections are done in BufferedDataRowCollection
 * and FndDataRowCollection.
 * 
 */
abstract class AbstractDataRowCollection
{
   ASPPage page;
   
   abstract int countRows();
   abstract int countDataRows();
   abstract void trace(String title);
   abstract boolean isNULL();
   abstract AbstractDataRow getDataRow(int i);
   abstract String getRowStatus(int i);
   
   void debug(String line)
   {
      page.debug("**************************************************************");
      page.debug(line);
      page.debug("**************************************************************");
   }   
}
