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
 * File        : ASPBuffer.java
 * Description : An ASPObject that represents a Data Buffer
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-Jan-08 - Created
 *    Marek D  1998-Feb-10 - Added conversion between Client and Server format
 *    Marek D  1998-Apr-01 - Added nested commands
 *    Marek D  1998-May-13 - Moved Command/Query logic to ASPCommand/ASPQuery
 *                           Moved Transaction logic to ASPTransactionBuffer
 *    Marek D  1998-May-30 - Implemented ASPBufferable interface
 *    Marek D  1998-Jun-11 - Added sort() method
 *    Marek D  1998-Jun-16 - Changed Diff-algorithm in convertToServerItem()
 *    Marek D  1998-Jul-03 - Added methods addItemAt(), addBufferAt()
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Marek D  1998-Aug-20 - For non-String items remove trailing blanks
 *                           in convertToServerItem()
 *    Marek D  1998-Nov-27 - Added method getNumberValue() (ToDo #2859)
 *    Marek D  1998-Dec-04 - get/setNumberValue() work on "double" values
 *    Marek D  1998-Dec-07 - Use ServerFormatter in setNumberValue()
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPManagedObject instead of ASPObject
 *    Jacek P  1999-Mar-01 - Abstract class ASPManagedObject replaced with
 *                           an empty interface ASPManageable. All code moved
 *                           back to ASPObject.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Jacek P  1999-May-21 - Added static method setItemValue()
 *    Jacek P  1999-May-25 - Added overloaded version of convertToServerItem()
 *    Marek D  1998-Jun-17 - Extra comparison logic for NUMBER items
 *                           in convertToServerItem()
 *    Jacek P  2000-Feb-01 - Added overloaded versions of functions:
 *                           addFieldItem(), setFieldItem() and getFieldValue()
 *                           that takes an instance of ASPPage as first parameter.
 *                           Needed for portal implementation.
 *    Johan S  2000-Jul-26 - Added addFieldDateItem, setFieldDateItem, getFieldDateValue
 *    Piotr Z  2000-Nov-17 - Added namedItemExists
 *    SlawekF  2001-Sep-12 - New overloaded version of addBuffer, that takes String
 *                           name and ASPBuffer parameters
 *    Jacek P  2002-Sep-24 - Added overloaded construct().
 * ----------------------------------------------------------------------------
 * New Comments:
 * 26/04/2010 buhilk BUG 88982 overloaded setItemValue() method to allow non-dirty values in buffer.
 * 18/03/2009 sumelk Bug 81274, Revoked the changes done by bug 79452.
 * 21/01/2009 rahelk Bug id 79452 Called NumberFormatter.parse method without rounding off and new serverFormat() method to round 
 *               2006/12/22           sumelk
 * Bug 62620, Modified convertToClientString() & convertToServerItem() to handle the date fields with null value.
 *
 * Revision 1.3  2005/11/16 14:13:35  japase
 * Added check in setValueAt()
 *
 * Revision 1.2  2005/11/02 12:22:32  mapelk
 * Saving Regional Settings to profile even the Regional Setting profile is missing.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/04/26 05:55:56  riralk
 * Fixed bug in method addBufferAt(). Checked if mainbuf is an instance of ProfileBuffer when adding a new item.
 *
 * ----------------------------------------------------------------------------
 */




package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * An ASPBuffer can contain both simple items (name-value pairs) and compound
 * items (sub-buffers). Items can be accessed by name or by position in the
 * buffer: 0,1,2...etc.
 * The public methods that access the buffer contents have the following pattern:
 *<pre>
 *   public int       addItem      ( String name, String value )
 *<p>
 *   public String    getValue     ( String name     )
 *   public String    getValueAt   ( int    position )
 *<p>
 *   public void      setValue     ( String name,  String value )
 *   public void      setValueAt   ( int position, String value )
 *<p>
 *   public String    getNameAt    ( int position )
 *<p>
 *   public int       addFieldItem ( String name, String value )
 *   public void      setFieldItem ( String name, String value )
 *   public String    getFieldValue( String name )
 *<p>
 *   public ASPBuffer addBuffer    ( String name )
 *   public ASPBuffer getBuffer    ( String name )
 *   public ASPBuffer getBufferAt  ( int    position )
 *<p>
 *   public boolean   isCompound   ( String name )
 *   public boolean   isCompoundAt ( int    position )
 *<p>
 *   public void      removeItem   ( String name )
 *   public void      removeItemAt ( int    position )
 *<p>
 *   public int       countItems()
 *   public void      clear()
 *<p>
 *   public String    format()
 *   public void      parse( String formatted_buffer )
 *</pre>
 */
public class ASPBuffer extends ASPObject implements ASPBufferable, ASPManageable
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPBuffer");

   protected static BufferFormatter buffer_formatter;

   private Buffer mainbuf;

   ASPBuffer( ASPManager manager )
   {
      super(manager);
   }

   protected ASPBuffer construct()
   {
      return construct(getASPManager().getFactory().getBuffer());
   }

   protected ASPBuffer construct( Buffer buffer )
   {
      this.mainbuf = buffer;
      if( buffer_formatter==null )
         buffer_formatter = getASPManager().getFactory().getBufferFormatter();

      return this;
   }

   protected ASPBuffer construct( Buffer buffer, BufferFormatter formatter )
   {
      this.mainbuf = buffer;
      this.buffer_formatter = formatter;

      return this;
   }

   //==========================================================================
   //  ASPBufferable interface
   //==========================================================================

   /**
    * Store the internal state of this ASPBuffer in a specified ASPBuffer
    */
   public void save( ASPBuffer intobuf )
   {
      try
      {
         Buffer into = intobuf.getBuffer();
         saveClass(into);
         Buffers.save( into, "MAINBUF", mainbuf );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Retrieve the internal state of this ASPBuffer from a specified ASPBuffer
    */
   public void load( ASPBuffer frombuf )
   {
      try
      {
         Buffer from = frombuf.getBuffer();
         mainbuf = Buffers.loadBuffer( from, "MAINBUF" );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  addItem
   //==========================================================================

   /**
    * Append a new simple item (name-value pair) to this buffer.
    * Return the item position (0,1,...) in the buffer.
    * Do NOT perform any Client-Server conversion.
    */
   public int addItem( String name, String value )
   {
      try
      {
         return mainbuf.addItem(name,value);
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Insert a a new simple item (name-value pair) to this buffer
    * at the specified position (0,1,...) in the item list.
    * Return the position of the inserted item.
    * Do NOT perform any Client-Server conversion.
    */
   public int addItemAt( String name, String value, int position )
   {
      try
      {
         return mainbuf.insertItem( new Item(name,value), position );
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Append a new simple item to this buffer
    * using the DbName, datatype and format mask of the named ASPField.
    * Return the item position (0,1,...) in the buffer.
    */
   public int addFieldItem( String name, String value )
   {
      return addFieldItem( getASPManager().getASPPage(), name, value );
   }


   public int addFieldItem( ASPPage page, String name, String value )
   {
      try
      {
         ASPManager mgr = getASPManager();
         ASPField field = page.getASPField(name);
         Item item = convertToServerItem( field.getDbName(),
                                          value,
                                          mgr.getServerFormatter(),
                                          field.getDataFormatter(),
                                          null );
         return mainbuf.addItem(item);
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Modify the item that corresponds to the named ASPField.
    */
   public void setFieldItem( String name, String value )
   {
      setFieldItem( getASPManager().getASPPage(), name, value );
   }


   public void setFieldItem( ASPPage page, String name, String value )
   {
      try
      {
         ASPField field = page.getASPField(name);
         field.convertInBuffer(mainbuf,value);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Retuen the value of the item that corresponds to the named ASPField.
    */
   public String getFieldValue( String name )
   {
      return getFieldValue( getASPManager().getASPPage(), name );
   }


   public String getFieldValue( ASPPage page, String name )
   {
      if(DEBUG) debug(this+": ASPBuffer.getFieldValue("+page+","+name+")");

      try
      {
         ASPField field = page.getASPField(name);
//         Item item = mainbuf.getItem(field.getDbName());
//         return field.convertToClientString(item.getString());
         String dbname = field.getDbName();
         Item item = mainbuf.getItem(dbname);
         String result = field.convertToClientString(item.getString());
         if(DEBUG) debug("  field="+field+"\n\t  dbname="+dbname+"\n\t  item="+item+"\n\t  result="+result);
         return result;
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }

   //==========================================================================
   //  Special functions for Dates.
   //==========================================================================

   /**
    * Append a new simple Date item to this buffer
    * using the DbName, datatype and format mask of the named ASPField.
    * Return the item position (0,1,...) in the buffer.
    */
   public int addFieldDateItem( String name, Date value )
   {
      return addFieldDateItem( getASPManager().getASPPage(), name, value );
   }

   public int addFieldDateItem( ASPPage page, String name, Date value )
   {
      try
      {
         String saved_value;
         ASPManager mgr = getASPManager();
         ASPField field = page.getASPField(name);
         saved_value = field.formatDate(value);
         Item item = convertToServerItem( field.getDbName(),
                                          saved_value,
                                          mgr.getServerFormatter(),
                                          field.getDataFormatter(),
                                          null );
         return mainbuf.addItem(item);
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Modify the Date item that corresponds to the named ASPField.
    */
   public void setFieldDateItem( String name, Date value )
   {
      setFieldDateItem( getASPManager().getASPPage(), name, value );
   }


   public void setFieldDateItem( ASPPage page, String name, Date value )
   {
      try
      {
         String saved_value;
         ASPField field = page.getASPField(name);
         saved_value = field.formatDate(value);

         field.convertInBuffer(mainbuf,saved_value);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Retuen the Date value of the item that corresponds to the named ASPField.
    */
   public Date getFieldDateValue( String name )
   {
      return getFieldDateValue( getASPManager().getASPPage(), name );
   }


   public Date getFieldDateValue( ASPPage page, String name )
   {
      if(DEBUG) debug(this+": ASPBuffer.getFieldValue("+page+","+name+")");

      try
      {
         ASPField field = page.getASPField(name);
         String dbname = field.getDbName();
         Item item = mainbuf.getItem(dbname);
         String result = field.convertToClientString(item.getString());
         if(DEBUG) debug("  field="+field+"\n\t  dbname="+dbname+"\n\t  item="+item+"\n\t  result="+result);
         return field.parseDate(result);
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }



   //==========================================================================
   //  addBuffer
   //==========================================================================

   /**
    * Create an empty ASPBuffer. Append it to this buffer as an item
    * with the specified name. Return the reference to the
    * newly created buffer.
    */
   public ASPBuffer addBuffer( String name )
   {
      try
      {
         ASPBuffer aspbuf = newASPBuffer();
         mainbuf.addItem(name,aspbuf.getBuffer());
         return aspbuf;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Append aspbuf to this buffer as an item
    * with the specified name.
    * Return the reference to THIS ASPBuffer instance.
    */
   public ASPBuffer addBuffer( String name, ASPBuffer aspbuf )
   {
      try
      {
         mainbuf.addItem(name,aspbuf.getBuffer());
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Create an empty ASPBuffer. Insert it to this buffer as an item
    * with the specified name at the specified position.
    * Return the reference to the newly created buffer.
    */
   public ASPBuffer addBufferAt( String name, int position )
   {
      try
      {
         ASPBuffer aspbuf = newASPBuffer();
         if (mainbuf instanceof ProfileBuffer)
            mainbuf.insertItem( new ProfileItem(name,aspbuf.getBuffer()), position );
         else
            mainbuf.insertItem( new Item(name,aspbuf.getBuffer()), position );
         return aspbuf;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  setValue
   //==========================================================================

   /**
    * Modify the value of the named item in this ASPBuffer.
    * Do NOT perform Client-Server conversion.
    */
   public void setValue( String name, String value )
   {
      try
      {
         Item item = mainbuf.findItem(name);
         if( item==null )
            mainbuf.addItem(name,value);
         else
            item.setValue(value);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Modify the value of the item at the specified position in
    * this buffer.
    * Do NOT perform Client-Server conversion.
    */
   public void setValueAt( int position, String value )
   {
      try
      {
         Item item = mainbuf.getItem(position);
         if(item==null)
            throw new FndException("FNDBUFPOSE: No item found at position &1 in this buffer.", ""+position);
         item.setValue(value);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   //==========================================================================
   //  getValue
   //==========================================================================

   /**
    * Return a String value of the first occurrence of the named item
    * in this buffer, or null if such item does not exist.
    * The name can contain "/"-operator, for example "A/B/C" points to
    * item "C" in sub-buffer "B" of sub-buffer "A" of this buffer.
    * The returned value is NOT converted to the client format.
    */
   public String getValue( String name )
   {
      try
      {
         Item item = mainbuf.findItem(name);
         if( item==null || item.isCompound() )
            return null;
         else
            return item.getString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the String value of the item at the specified position in
    * this buffer.
    * The returned value is NOT converted to the client format.
    */
   public String getValueAt( int position )
   {
      try
      {
         return mainbuf.getString(position);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  get/setNumberValue
   //==========================================================================

   /**
    * Return a numeric value of the first occurrence of the named item
    * in this buffer, or NaN (Not-a-Number) if such item does not exist.
    * The name can contain "/"-operator, for example "A/B/C" points to
    * item "C" in sub-buffer "B" of sub-buffer "A" of this buffer.
    */
   public double getNumberValue( String name )
   {
      ASPManager mgr = getASPManager();
      try
      {
         Item item = mainbuf.findItem(name);
         if( item==null || item.isCompound() )
            return mgr.NOT_A_NUMBER;
         else
         {
            String text = item.getString();
            if( Str.isEmpty(text) )
               return mgr.NOT_A_NUMBER;

            ServerFormatter server_formatter = mgr.getServerFormatter();
            Number num = (Number)server_formatter.parse(text, DataFormatter.NUMBER);
            return num.doubleValue();
         }
      }
      catch( Throwable any )
      {
         error(any);
         return mgr.NOT_A_NUMBER;
      }
   }


   /**
    * Modify the value of the named item in this ASPBuffer or create a new item,
    * if it does not exist.
    * The item will have the specified numeric value.
    * The item type will be set to "N" (NUMBER).
    */
   public void setNumberValue( String name, double value )
   {
      try
      {
         if( Double.isInfinite(value) )
            throw new FndException("FNDBUFINFNUM: Infinite Number in item &1", name);

         String val = Double.isNaN(value) ?
                      null :
                      getASPManager().getServerFormatter().format(new Double(value),DataFormatter.NUMBER);

         Item item = mainbuf.findItem(name);
         if( item==null )
            mainbuf.addItem(new Item(name,"N",null,val));
         else
         {
            item.setType("N");
            item.setValue(val);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Modify the value of the named item in this ASPBuffer or create a new item,
    * if it does not exist.
    * The item will have the specified Integer object value, not the String value.
    * This method should not called to add Integers to buffers for DB transactions.
    * @param name Item name
    * @param value value to be set
    */
   public void setIntegerItem(String name, int value)
   {
      try
      {
         Item item = mainbuf.findItem(name);
         if( item==null )
            mainbuf.addItem(new Item(name,value));
         else
            item.setValue(value);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  getBuffer
   //==========================================================================

   /**
    * Return the named sub-buffer of this buffer,
    * or null if such buffer does not exist.
    * The name can contain "/"-operator, for example "A/B/C" points to
    * sub-buffer "C" of sub-buffer "B" of sub-buffer "A" of this buffer.
    */
   public ASPBuffer getBuffer( String name )
   {
      try
      {
         Item item = mainbuf.findItem(name);
         Buffer buf = (item!=null && item.isCompound()) ? item.getBuffer() : null;
         return newASPBuffer(buf);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return a reference to the sub-buffer at the specified position in
    * this buffer.
    */
   public ASPBuffer getBufferAt( int position )
   {
      try
      {
         return newASPBuffer(mainbuf.getBuffer(position));
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the underlying Buffer object of this ASPBuffer.
    */
   public Buffer getBuffer()
   {
      return mainbuf;
   }

   //==========================================================================
   //  isCompound
   //==========================================================================

   /**
    * Return true if the named item of this buffer is a nested buffer.
    */
   public boolean isCompound( String name )
   {
      try
      {
         Item item = mainbuf.getItem(name,null);
         return item!=null && item.isCompound();
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Return true if the item at the specified position in this buffer
    * is a nested buffer.
    */
   public boolean isCompoundAt( int position )
   {
      try
      {
         return mainbuf.getItem(position).isCompound();
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   //==========================================================================
   //  countItems
   //==========================================================================

   /**
    * Return the number of items collected in this buffer.
    */
   public int countItems()
   {
      try
      {
         return mainbuf.countItems();
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   //==========================================================================
   //  getNameAt
   //==========================================================================

   /**
    * Return the name of the item at the specified position in
    * this buffer.
    */
   public String getNameAt( int position )
   {
      try
      {
         Item item = mainbuf.getItem(position);
         return item.getName();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  removeItem
   //==========================================================================

   /**
    * Remove the named item from this buffer.
    */
   public void removeItem( String name )
   {
      try
      {
         mainbuf.removeItem(name);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Remove the item at the specified position in this buffer.
    */
   public void removeItemAt( int position )
   {
      try
      {
         mainbuf.removeItem(position);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Clear the contents of this buffer.
    */
   public void clear()
   {
      try
      {
         mainbuf.clear();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //
   //==========================================================================

   /**
    * Return the position (0,1,...) of the named item in this buffer.
    */
   public int getItemPosition( String name )
   {
      try
      {
         return mainbuf.getItemPosition(name);
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Return true if the named item exists in this buffer, false otherwise.
    * The name can contain "/"-operator, for example "A/B/C" points to
    * item "C" in sub-buffer "B" of sub-buffer "A" of this buffer.
    */
   public boolean itemExists( String name )
   {
      try
      {
         return mainbuf.findItem(name) != null;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Return true if the named item exists in this buffer, false otherwise.
    * The name can contain "/" which is not interpreted as sub-buffer operator
    * like in itemExists() method.
    *
    * @see ifs.fnd.asp.ASPBuffer#itemExists
    */
   public boolean namedItemExists( String name )
   {
      return getItemPosition(name)!=-1;
   }

   //==========================================================================
   //  Serialization
   //==========================================================================

   /**
    * Serialize the contents of this buffer into a String
    */
   public String format()
   {
      try
      {
         return buffer_formatter.format(mainbuf);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Parse the specified string into the contents of this buffer.
    * The previous contents of this buffer is removed prior to parsing.
    */
   public void parse( String formatted_buffer )
   {
      try
      {
         mainbuf.clear();
         buffer_formatter.parse(formatted_buffer,mainbuf);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Construction of new ASPBuffers
   //==========================================================================

   /**
    * Construct a new empty ASPBuffer.
    */
   ASPBuffer newASPBuffer()
   {
      return newASPBuffer(mainbuf.newInstance());
   }

   /**
    * Construct a new empty ASPBuffer based on the specified Buffer object.
    */
   ASPBuffer newASPBuffer( Buffer buffer )
   {
      return buffer==null ? null : (new ASPBuffer(getASPManager())).construct(buffer);
   }

   /**
    * Create a new independent ASPBuffer that has exactly the same
    * contents as this buffer.
    */
   public ASPBuffer copy()
   {
      try
      {
         return newASPBuffer((Buffer)mainbuf.clone());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Sorting
   //==========================================================================

   /**
    * Sort all DATA rows in this ASPBuffer according to the named key item.
    */
   public void sort( String key_name, boolean ascending )
   {
      try
      {
         RowComparator comp = new RowComparator(getASPManager().getServerFormatter(),key_name,ascending);
         Buffers.sort(mainbuf,comp);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   //==========================================================================
   //  Client/Server Conversion
   //==========================================================================

   /**
    * Create a new Item without any Client/Server conversion.
    * Mark the new Item as changed (status="*"),
    * if the new value differs from the old one.
    */
   static Item copyToServerItem( String name,
                                 String type_marker,
                                 String value,
                                 Buffer old_data )
   {
      String oldvalue = old_data==null ? null :
                        old_data.getString(name,null);

      String status = null;

      if( old_data!=null )
         status = Str.nvl(value,"").equals(Str.nvl(oldvalue,"")) ? null : "*";

      return new Item(name,type_marker,status,value);
   }


   /**
    * Create a new Item converting the specified value to the server format
    * using the specified client DataFormatter and the current instance of
    * ServerFormatter. Mark the new Item as changed (status="*"),
    * if the new value differs from the old one, which means that:
    *<pre>
    *  1. client values are different, and
    *  2. server values are different
    *</pre>
    */
   static Item convertToServerItem( String          name,
                                    String          value,
                                    ServerFormatter server_formatter,
                                    DataFormatter   client_formatter,
                                    Buffer          old_data ) throws FndException
   {
      String oldsrvvalue = old_data==null ? null : old_data.getString(name,null);
      Item item = new Item(name);
      convertToServerItem(item,value,server_formatter,client_formatter,old_data!=null,oldsrvvalue);
      return item;
   }


   static boolean convertToServerItem( Item            item,
                                       String          value,
                                       ServerFormatter server_formatter,
                                       DataFormatter   client_formatter,
                                       boolean         oldvalexists,
                                       String          oldsrvvalue ) throws FndException
   {
      int  type_id      = client_formatter.getTypeId();
      int  base_type_id = DataFormatter.getBaseTypeId(type_id);
      char type_marker  = DataFormatter.getBaseTypeMarker(type_id);

      if( type_marker!='S' && value!=null )
         value = Util.trimLine(value);

      Object newobj      = client_formatter.parse(value);
      String newsrvvalue = server_formatter.format(newobj,base_type_id);

      String oldclivalue = "";

      if (type_marker=='D' && (oldsrvvalue==null || "".equals(oldsrvvalue.trim())))
      {
         oldclivalue = oldsrvvalue;  
      }
      else
      {    
         Object oldobj      = server_formatter.parse(oldsrvvalue,base_type_id);
         oldclivalue = client_formatter.format(oldobj);
      }
      String newclivalue = value;

      item.setStatus(null);
      boolean changed = false;

      if(DEBUG) Util.debug("ASPBuffer.convertToServerItem():"+
                           "\n\t\t\toldclivalue="+oldclivalue+
                           "\n\t\t\tnewclivalue="+newclivalue+
                           "\n\t\t\toldsrvvalue="+oldsrvvalue+
                           "\n\t\t\tnewsrvvalue="+newsrvvalue);
      if( oldvalexists &&
          !equalValues(oldclivalue,newclivalue) &&
          !equalValues(oldsrvvalue,newsrvvalue) )
      {
         if( base_type_id==DataFormatter.NUMBER && oldsrvvalue!=null && newsrvvalue!=null &&
             only0AndDot(oldsrvvalue) && only0AndDot(newsrvvalue) )
            ; // both items are equal to 0
         else
         {
            item.setStatus("*");
            changed = true;
         }
      }
      item.setType(String.valueOf(type_marker));
      item.setValue(newsrvvalue);
      return changed;
   }

   private static boolean equalValues( String a, String b )
   {
      return Str.nvl(a,"").equals(Str.nvl(b,""));
   }

   private static String rtrim( String value )
   {
      if( value==null ) return null;

      int len = value.length();
      int i = len;
      char ch;

      while( i>0 && ( (ch=value.charAt(i-1))==' ' || ch=='\t' ) )
         i--;

      return i==len ? value : value.substring(0,i);
   }

   private static boolean only0AndDot( String s )
   {
      int len = s.length();
      for( int i=0; i<len; i++ )
      {
         switch(s.charAt(i))
         {
            case '0':
            case '.':
               break;

            default:
               return false;
         }
      }
      return len>0;
   }

   /**
    * Modify the value of the given item.
    * Return true if value has been changed and mark item status.
    * Do NOT perform Client-Server conversion.
    */
   static boolean setItemValue( Item item, String value ){
      return setItemValue(item, value, true);
   }
   
   static boolean setItemValue( Item item, String value, boolean make_dirty )
   {
      String old_value = item.getString();
      if( equalValues(old_value, value) )
         return false;
      item.setValue(value);
      if(make_dirty)
         item.setStatus("*");
      return true;
   }


   /**
    * Convert the specified Item from the server format to the client
    * format using the specified client DataFormatter.
    */
   String convertToClientString( Item item,
                                 DataFormatter client_formatter ) throws Exception
   {
      if( item==null ) return null;
      String value = item.getString();

      if (value == null || getASPManager().isEmpty(value.trim()))
          return value;
      //String stype = item.getType();
      //char server_type_ch = stype==null ? 'S' : stype.charAt(0);
      //char client_type_ch = DataFormatter.getBaseTypeMarker();
      //if( server_type_ch!=client_type_ch )
      //  throw new Exception("Cannot convert Server type "+server_type_ch+
      //                      " to Client type "+client_type_ch);

      ServerFormatter server_formatter = getASPManager().getServerFormatter();

      int server_type_id = DataFormatter.getBaseTypeId(client_formatter.getTypeId());
      Object obj = server_formatter.parse(value, server_type_id);
      return client_formatter.format(obj);
   }


   /**
    * Convert the specified Item from the server format to the client
    * format using the specified client type Id and format mask.
    */
   String convertToClientString( Item item,
                                 int type_id,
                                 String mask ) throws Exception
   {
      if( item==null ) return null;
      DataFormatter formatter = getASPManager().getDataFormatter(type_id,mask);
      return convertToClientString(item,formatter);
   }
}




