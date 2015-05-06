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
 * File        : FndDataRowCollection.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 *  
 */

package ifs.fnd.asp;

import ifs.fnd.internal.FndAttributeInternals;
import ifs.fnd.record.FndAbstractArray;
import ifs.fnd.service.Util;

/**
 * Specific implementation of FndAbstractRecord collection
 * 
 **/
public class FndDataRowCollection extends AbstractDataRowCollection
{
   
   FndAbstractArray array;
   boolean is_null;
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.FndDataRowCollection");
   
   /* Creates a new instance of BufferedDataRowCollection */
   FndDataRowCollection(FndAbstractArray array) 
   {
      this.array = array;
      is_null = (array==null);
   }
   
   int countDataRows()   
   {
      return countRows();
   }
   
   int countRows()
   {
      return array.size();
   }
   
   boolean isNULL()   
   {
      return is_null;
   }
   
   AbstractDataRow getDataRow(int i)
   {
      if (i>countDataRows()-1) return null;
      return new FndDataRow(FndAttributeInternals.internalGet(array, i));
   }  
   
   String getRowStatus(int i)
   {
      if (DEBUG)
         debug("FndDataRowCollection::getRowStatus at " + i);
      if (i>countDataRows()-1) return null;
      FndDataRow row = (FndDataRow) getDataRow(i);
      String status = row.getStatus();
      if (DEBUG)
         debug("FndDataRowCollection::getRowStatus = " + status);
      return status;
   }   
   
   void trace(String title)
   {
      Util.debug(title);
      Util.debug(ASPManager.printAbstractArray(array,1));
   }
   
}
