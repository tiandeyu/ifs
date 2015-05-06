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
 * File        : BufferedDataRowCollection.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 * 
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.Item;
import ifs.fnd.service.Util;

/**
 * Specific implementation for buffer row collections.
 */
public class BufferedDataRowCollection extends AbstractDataRowCollection{
   
   ASPBuffer rows;
   boolean is_null;
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.BufferedDataRowCollection");
   
   
   /** 
    * Creates a new instance of BufferedDataRowCollection. 
    **/
   BufferedDataRowCollection(ASPBuffer rows, ASPPage page) 
   {
      this.rows = rows;
      is_null = (rows==null);
      this.page = page;
      if (DEBUG)
      {
         debug("Constrcuting BufferedDataRowCollection: isNULL=" + is_null);
         if (rows==null)
         {
            debug("rows = ");
            rows.traceBuffer("Constracted row collection");
         }
                    
      }
   }
   
   int countRows()
   {
      if (DEBUG)
         debug("BufferedDataRowCollection::countRows =" + rows.countItems());      
      return rows.countItems();
   }
   
   int countDataRows()
   {
      if (DEBUG)
         debug("BufferedDataRowCollection::countDataRows =" + rows.getBuffer().countItems(ASPRowSet.DATA));      
      return rows.getBuffer().countItems(ASPRowSet.DATA);
   }   
   
   AbstractDataRow getDataRow(int i)
   {
      if (DEBUG)
         debug("BufferedDataRowCollection::getDataRow at " + i);        
      if (i>countDataRows()-1) return null;
      String name = rows.getBuffer().getItem(i).getName();
      String type = rows.getBuffer().getItem(i).getType();
      String status = rows.getBuffer().getItem(i).getStatus();
       if (DEBUG)
         debug("BufferedDataRowCollection::getDataRow name " + name);     
      
      if ("DATA".equals(name))
      {
         return new BufferedDataRow(rows.getBufferAt(i),type,status,page);
      }

      return null;
   }
   
   void trace(String title)
   {
      rows.traceBuffer(title);
   }
   
   boolean isNULL()   
   {
      return is_null;
   }
   
   String getRowStatus(int i)
   {
      if (DEBUG)
         debug("BufferedDataRowCollection::getRowStatus at " + i);
      if (i>countDataRows()-1) return null;
      Item item = rows.getBuffer().getItem(i);
      if (DEBUG)
         debug("BufferedDataRowCollection::getRowStatus = " + item.getStatus());      
      return item.getStatus();      
   }
   
}
