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
 * File        : BufferedRow.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 *  
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.DataFormatter;
import ifs.fnd.buffer.Item;
import ifs.fnd.buffer.ItemNotFoundException;
import ifs.fnd.service.*;

/**
 * Specific implementation for buffered data row
 *
 **/
public class BufferedDataRow extends AbstractDataRow
{
   
   ASPBuffer row;
   boolean is_null;
   String type = "";
   String status = "";
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.BufferedDataRow");
   //String status;
   /** Creates a new instance of BufferedRow */
   BufferedDataRow(ASPBuffer row, ASPPage page)
   {
      this(row,"","",page);
   }
   
   BufferedDataRow(ASPBuffer row,String type, String status, ASPPage page) 
   {
      this.row = row;
      if (row==null)
         is_null=true;
      this.page = page;
      this.type = type;
      this.status = status;
      if (DEBUG)
      {
         debug("Constrcuting BufferedDataRow: isNULL=" + is_null);
         if (row==null)
         {
            debug("rows = ");
            row.traceBuffer("Constracted row ");
         }
                    
      }      
   }
   
   boolean isNULL()
   {
      return is_null;
   }
   
   String getString(ASPField field,String default_value)
   {
      if (row==null) return "";
      String value = row.getBuffer().getString(field.getDbName(),null);
      if (DEBUG)
      {
         debug("BufferedDataRow: getString " + value);
      }
      return value;
   }
   
   String convertToClientString(ASPField field, DataFormatter formatter) throws Exception
   {
      String dbname = field.getDbName();
      if (DEBUG)
         debug("BufferedDataRow: convertToClientString for " + dbname);
      if (row == null)
         return "";

      Item item;
      try
      {
         item = row.getBuffer().getItem(dbname);
      }
      catch( ItemNotFoundException ex )
      {
            item = null;
      }
      
      String value = row.convertToClientString(item, formatter);         
      if (DEBUG)
         debug("value = " + value);      
      return value;
   }
   
   int countColumns()
   {
      return row.countItems();
   }   
   
      
   int getAttributePosition(String name)
   {
      if (DEBUG) 
         debug("BufferedDataRow: getAttributePosition for " + name+ " is " + row.getItemPosition(name));      
      return row.getItemPosition(name);
   }
   
   String getValue(ASPField f) throws ItemNotFoundException
   {
      String value = (String)(row.getBuffer().getItem(f.getDbName()).getValue());
      if (DEBUG) 
         debug("BufferedDataRow: getValue for " + f.toString() + " is " + value);     
      return value;
   }
   
   String getValueAt(int i)  throws ItemNotFoundException 
   {
      String value = (String)(row.getBuffer().getItem(i).getValue());
      if (DEBUG) 
         debug("BufferedDataRow: getValueAt(" + i + ") is " + value);
      return value;
   } 
   
   String getValue(String name) throws ItemNotFoundException
   {
      String value = (String)(row.getBuffer().getItem(name).getValue());
      if (DEBUG) 
         debug("BufferedDataRow: getValue(" + name + ") is " + value);
      return value;      
   }
   
   String getType()
   {
      return type;
   }
   String getStatus()
   {
      return status;
   }

   
}
