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
 * File        : ASPRowSet.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-May-18 - Created
 *    Marek D  1998-May-22 - Introduced Current Row concept
 *    Jacek P  1998-May-26 - Introduced Filter Rows concept
 *    Jacek P  1998-Jun-02 - Added new function setSelectedRowsRemoved().
 *    Marek D  1998-Jun-08 - Introduced private DEBUG flag
 *    Marek D  1998-Jun-15 - Corrected goTo(), added storeSelections()
 *    Marek D  1998-Jun-25 - Added resetRow()
 *    Marek D  1998-Jun-26 - Added getSelectedRows()
 *    Marek D  1998-Jul-07 - Added method getRows(field_names)
 *    Jacek P  1998-Jul-15 - Table number added to select box name.
 *                           Remove on removed record equals un-remove.
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-13 - Added function setEdited()
 *    Jacek P  1998-Sep-28 - Added function clearRow() that removes the current
 *                           row from both DB state and current state without
 *                           affecting the database (ToDo #2716).
 *    Marek D  1998-Oct-27 - Bug #2692: Call block.unsetReadOnly() from clear()
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageSubElement instead of ASPObject.
 *                           Removed calls to Scr class.
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Mar-05 - Introduced pool concept.
 *    Marek D  1999-Apr-26 - Verified usage of clone()
 *                           Added verify() and scan()
 *    Jacek P  1999-May-21 - Added public methods of type get/setValue() for
 *                           modification of individual field items.
 *    Jacek P  1999-May-25 - Added new public method changeRows() and
 *                           private methods changeRowAt() and new
 *                           overloaded setRowBuffer()
 *    Marek D  1999-Jun-01 - Removed block.unsetReadOnly() from clear()
 *    Marek D  1999-Jun-07 - New variables sorted_column, sorted_ascending
 *                           and call to sort() from goTo()
 *    Johan S
 *    Jacek P  1999-Jun-09 - Added special handling of DUPLICATE button:
 *                           - new variables first_selected_row and
 *                             duplicate_pressed_flag
 *                           - new function duplicatePressed() called from
 *                             ASPManager
 *                           - changes in methods addRow() and selectRows()
 *    Jacek P  1999-Jun-14 - Added new function translateRowNumber().
 *    Jacek P  1999-Jun-18 - Added completion of new created destination buffer
 *                           with additional items already set in current state
 *                           in function setRowBuffer().
 *    Jacek P  1999-Jul-09 - Corrected bug in setRowBuffer(): updating in multirow
 *                           doesn't work for fields with db names.
 *    Jacek P  1999-Jul-12 - Function goTo() calls sort() only the first time
 *                           during the same request. Fixed bug in the sort()
 *                           function - INFO item is not required.
 *    Jacek P  1999-Jul-14 - Added new package functions getSortedColumnName()
 *                           and isSortedAscending() for using with sort arrows
 *                           in ASPTable.java.
 *    Jacek P  1999-Jul-26 - Added new public functions getDbValue() and getDbValueAt().
 *    Chaminda &
 *    Mangala  1999-Jun-18 - Add new public function dataString() to get data from
 *                           curr_state_blk and return as string for spred sheed
 *                           Functionality.
 *    Jacek P  1999-Aug-09 - Included C&M:s code with some modifications.
 *    Jacek P  1999-Aug-19 - Added new version of getRows().
 *    Marek D,
 *    Jacek P  1999-Aug-19 - Always set value for __DB_ROW_NO item in
 *                           createLinksToDbState(). Added optional versions
 *                           of read/writeInfo() for future use. The new version
 *                           of getRows() not needed. Removed.
 *    Reine A  1999-Dec-20 - Added two new public functions - visible_ColWidth_ to
 *                           get the width of visible columns - visibleString_ to
 *                           get the titles and data of visible columns.
 *    Jacek P  2000-Jan-21 - Implementation of portal:
 *                           call to readValue*() and getASPField*() in ASPManager
 *                           replaced with call to corresponding methods in ASPPage
 *                           in methods getRows(), getSelectedRows(), getRowSelected(),
 *                           selectRows(), switchSelections(), storeSelections().
 *    Johan S  2000-Feb-Gui  Added function duplicatePressed3() and Store().
 *    Johan S  2000-Mar-29   Added functions nextDbSet()/prevDbSet()
 *    Johan S  2000-Apr-12   Added syncItemSets().
 *    Reine A  2000-May-08 - Corrected bug in visibleString_(): output channel doesn't
 *                           work on sorted columns.
 *    Jacek P  2000-May-16 - Do not remove item from db_state in clearRow() if db_state
 *                           points to the same buffer as current_state.
 *                           Corrected bug in goTo(): sorting doesn't work for fields
 *                           with different names and db_names.
 *    Jacek P  2000-Oct-09 - Added method moreRowsExist() (log id #213).
 *    Jacek P  2000-Oct-30 - Handling of variable master_has_rows moved from JScript
 *                           to Java.
 *    Jacek P  2001-Jan-18 - Upgraded to latest version.
 *    Mangala  2001-Mar-30 - Change refreshRow() method to send an EmptyQuery.  
 *    Kingsly  2001-Apr-20 - Change getNumberValueAt() method to handle empty
 *                           values(log id 521)
 *    Artur K  2001-May-15 - Changes for handling buffer size declared in
 *                           ASPQuery.setBufferSize() function (log id 702)
 *    Mangala  2001-Jun-25 - Changed visibleString_() to return data in client format.
 *                           log id #766.
 *    Chandana 2001-Jul-04 - Improved comments.
 *    Suneth M 2002-Aug-14 - Changed sort() to perform the comparison according 
 *                           to the language specified in ASPConfig.ifm.
 *    Mangala  2002-Sep-05 - Added functions getClientValue & getClientValueAt to retun the 
 *                           client format values.
 *    Rifki R  2002-Oct-15 - Changed visibleString_ to return the data string in correct order 
 *                           according to table properties for output channels (log id #363)                                                      
 *    Ramila H 2002-Oct-30 - Log id 982. Implemented code.
 *    ChandanaD2002-Nov-15 - Corrected row numbers in Master-Detail forms.
 *    Ramila H 2002-Nov-29 - Corrected bug in log id 982 when calling rowset store().
 *    ChandanaD2003-Jan-22 - Log id 942. Modified refreshRow() method.
 *    Ramila H 2003-Jan-31 - added functionality to clear child rowsets when master is cleared.
 *    Suneth M 2003-Feb-19 - Modified refreshRow() to change the db state. 
 *    Ramila H 2003-Mar-06 - Used overloaded mehtod clearMyChilds(ASPBlock)
 *    ChandanaD2003-Sep-12 - Fixed a bug in saveQuery() - .MAX_ROWS written to context.
 *    Ramila H 2004-03-31  - Added new methods to refresh all/selected rows.
 *    Ramila H 2004-04-06  - implemented log id 1401
 *    Chandana 2004-Aug-20 - Added public methods to get & set values in different data formats.
 *    Chandana 2004-Nov-10 - Added prepareRecord() to support Activity API calls.
 * ----------------------------------------------------------------------------
 * New Comments:  
 * 2010/04/26 buhilk BUG 88982: overloaded setValue(), setValueAt(), setNumberValue(), setNumberValueAt()
 *                              setDateValue(), setDateValueAt() and abstract method setValueAt()
 *                              method to allow non-dirty values in buffer.
 * 2010/01/21 sumelk Bug 87622, Added currentDbSet().
 * 2008/08/21 sumelk Bug 75713, Added firstDbSet() and lastDbSet().
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities. 
 * 2008/04/22 sumelk
 * Bug 72664, Changed visibleString_() to append the possibility to remove digit grouping symbol. 
 *
 * 2008/03/17 sumelk
 * Bug 72518, Changed visibleString_() to avoid String index out of range error. 
 *
 * 2007/12/03 buhilk
 * IID F1PR1472, Added Mini framework functionality for use on PDA.
 *
 * 2007/10/17 sumelk
 * Bug 67958, Changed visibleString_() to remove the opening double quote in field values 
 * when there is no ending double quote. (To avoid the formatting errors in Excel.)
 *
 * Revision 1.6  2007/07/16 sadhlk
 * Corrections for Bug 66470, Modified nextDbSet(), prevDbSet() and saveQuery()
 * Revision 1.5  2006/06/21 05:39:17  buhilk
 * Corrections for Bug 58187, Added tab char filter to visibleString_().
 *
 * Revision 1.4  2005/11/09 05:39:17  sumelk
 * Merged the corrections for Bug 47881, Added javadoc comments.
 *
 * Revision 1.3  2005/10/14 09:08:14  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.2  2005/09/27 08:05:16  sumelk
 * Merged the corrections for Bug 52959, Changed sort() to store selected rows.
 * 
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.AutoString;
import ifs.fnd.buffer.Buffer;
import ifs.fnd.buffer.DataFormatter;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.webfeature.FndWebFeature;
import java.util.Date;

/**
 * A abstract common class for handling of row sets in the block. This class WAS instantiable
 * before introducing Activity support in the framework. Now it is ABSTRACT and implementation 
 * is done in BufferedDataSet and FndDataSet classes. All the buffer related implementation 
 * are moved to BufferedDataSet.java
 * 
 **/

public abstract class ASPRowSet extends ASPPageSubElement
{
   
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPRowSet");

   static final String NEW      = "New__";
   static final String MODIFY   = "Modify__";
   static final String REMOVE   = "Remove__";
   static final String DATA     = "DATA";
   static final String INFO     = "INFO";
   static final String SELECTED = "*";

   protected static final int ADD_ROW    = 1;
   protected static final int SET_ROW    = 2;
   protected static final int CHANGE_ROW = 3;
   protected static final int MARK_ROW   = 4;
   protected static final int SUBMIT     = 5;
   protected static final int EDITED     = 6;
   protected static final int COMMAND    = 7;

   //==========================================================================
   // Instance variables
   //==========================================================================

   // immutable
   protected String            block_name;

   // private mutable (temporary)
   protected transient int     prev_curr_row;
   protected String            generated_fields;
   protected boolean           duplicate_pressed_flag;
   protected boolean           already_sorted;

   protected int               current_row_no;
   protected boolean           filter_enabled;
   protected String            sorted_column;
   protected boolean           sorted_ascending;
   protected boolean           db_links_created;

   protected boolean           initialized_selection = false;

   //==========================================================================
   // Constructors
   //==========================================================================

   /**
    * Package constructor. Calls constructor within super class.
    */
   ASPRowSet( ASPBlock block )
   {
      super(block);
      if (DEBUG) debug(this+" - constructor");
   }

   ASPRowSet construct() throws FndException
   {
      this.block_name = getBlock().getName();
      if (DEBUG) debug(this+": construct() on block "+block_name);
      if( block_name!=null ) doActivate();
      return this;
   }


   /**
    * Called by ASPPage, after successful execution of the submit() method.
    */
   void refresh() throws FndException
   {
      if (DEBUG) debug(this+": refresh()");
      doActivate();
   }

   ASPBlock getBlock()
   {
      return (ASPBlock)getContainer();
   }

   /**
    * Returns the name of the ASPBlock.
    */
   public String  getName()
   {
      return getBlock()==null ? "" : getBlock().getName();
   }

   //==========================================================================
   //  Methods for implementation of pool.
   //  doActivate() is defined as abstract and should implement in sub class.
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze() throws FndException
   {
      if (DEBUG) 
         debug(this+": doFreeze()");
   }

   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
      if (DEBUG) 
         debug(this+": doReset()");

      duplicate_pressed_flag = false;
      already_sorted         = false;
      initialized_selection  = false;

      clearSortedColumn();
      resetSubMembers();
   }


   void forceDirty() throws FndException
   {
      modifyingMutableAttribute("FORCE_DIRTY");
   }


   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
      getBlock().verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   protected void clearSortedColumn()
   {
      if(DEBUG) debug(this+"clearSortedColumn()");
      sorted_column    = null;
      sorted_ascending = true;
   }

   /**
    * Return the number of DATA-rows in the specified Buffer.
    * This method does not count the last item, if it contains INFO data.
    */
   static int countRows( Buffer buf )
   {
      if( buf==null ) return 0;
      int count = buf.countItems();
      if( count>0 && INFO.equals(buf.getItem(count-1).getName()) )
         count --;
      return count;
   }



   //==========================================================================
   // Public routines for working with rows in the current state context buffer
   //==========================================================================
   /**
    * Marks the current row with method identifier. The row must already exist.
    */
   public void markRow( String method_id )
   {
      try
      {
         markRow( method_id, current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Marks the current row as selected.
    */
   public void selectRow()
   {
      try
      {
         selectRow( current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Removes selection mark from current row.
    */
   public void unselectRow()
   {
      try
      {
         unselectRow( current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   
   /**
    * Returns true if current row is selected.
    */
   public boolean isRowSelected()
   {
      try
      {
         return isRowSelected( current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
         return false;
      }
   }


   /**
    * Returns row number of the selected row.
    */
   public int getRowSelected( )
   {
      try
      {
         initSelectRows();
         
         for (int row_no=0; row_no<countRows(false); row_no++)
         {
            if (isRowSelected(row_no))
               return row_no;
         }
         return -1;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }

   }


   /**
    * Marks all rows marked in the browser as selected. Returns number of
    * marked rows.
    */
   public int selectRows()
   {
      try
      {
         if(DEBUG) debug("ASPRowSet.selectRows()");
         initSelectRows();

         int count = 0;
         for (int row_no=0; row_no<countRows(false); row_no++)
         {
            if (isRowSelected(row_no))
               count++;
         }
         if (DEBUG) debug("  count="+count);
         return count;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }



   /**
    * Switches selections for all rows marked in browser.
    * Returns number of marked rows.
    */
   public int switchSelections()
   {
      try
      {
         if (DEBUG) debug("ASPRowSet.switchSelections()");
         initSelectRows();

         int count = 0;
         for (int row_no=0; row_no<countRows(false); row_no++)
         {
            if ( isRowSelected( row_no ) )
               unselectRow( row_no );
            else
               selectRow( row_no );
            count++;
         }
         if (DEBUG) debug("  count="+count);
         return count;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }



   /**
    * Select/Unselect all checked/unchecked rows on the current page,
    * using the page size defined for the ASPTable based on the ASPBlock
    * corresponding to this ASPRowSet.
    */
   public void storeSelections()
   {
      try
      {
//         storeSelections(getASPManager().getASPTable(getBlock()).getPageSize());
         storeSelections(getASPPage().getASPTable(getBlock().getName()).getPageSize());
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Select/Unselect all checked/unchecked rows on the current page,
    * using the specified page size.
    */
   public void storeSelections( int page_size )
   {
      try
      {
         selectRows();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Returns the client format value corresponding to the given ASPField name from the current row. 
    * RowSet instances always keep value in server format and you must display them in client format if 
    * you directly display them. This method returns the corresponding value in client format using the 
    * ASPField type and it's format mask.
    *
    * <pre>
    *    Example:
    *       String client_format_date = rowset.getClientValue("FIELD_NAME");
    *       mgr.showAlert(mgr. translate("XXXXKEY: Registered at &1", client_format_date));
    *</pre> 
    * @param asp_field_name ASPField name
    * @see #getClientValueAt 
    * @see #getValue
    * @see #getValueAt
    * @see ifs.fnd.asp.ASPField.convertToClientString  
    */


   public String getClientValue(String asp_field_name)
   {
      return getClientValueAt(current_row_no,asp_field_name);
   }

   /**
    * Returns the client format value corresponding to the given ASPField name from the given row number. 
    * RowSet instances always keep value in server format and you must display them in client format if 
    * you directly display them. This method returns the corresponding value in client format using the 
    * ASPField type and it's format mask.
    *
    * <pre>
    *    Example:
    *       String client_format_date = rowset.getClientValueAt("FIELD_NAME",2);
    *       mgr.showAlert(mgr. translate("XXXXKEY: Registered at &1", client_format_date));
    *</pre> 
    * @param asp_field_name ASPField name
    * @see #getClientValue 
    * @see #getValue
    * @see #getValueAt 
    * @see ifs.fnd.asp.ASPField.convertToClientString
    */

      
   public String getClientValueAt(int rowno, String asp_field_name)
   {
      try
      {
         ASPField field = getASPManager().getASPField(asp_field_name);
         return field.convertToClientString(getValueAt(rowno,field.getDbName()));
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   
   }

   /**
     * Returns the rowset date value as a java Date object.
     * @params asp_field_name Name of the ASPField.
     */
   public Date getDateValue(String asp_field_name)
   {
      return getDateValueAt(current_row_no,asp_field_name);
   }
   
   /**
     * Returns the rowset date value as a java Date object.
     * @params rowno Row number.
     * @params asp_field_name Name of the ASPField.
     */
   public Date getDateValueAt(int rowno, String asp_field_name)
   {
      try
      {
         ASPField field = getASPManager().getASPField(asp_field_name);
         return field.convertToJavaDate(getValueAt(rowno,field.getDbName()));
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }   
   }

   /**
    * Returns a String value of the named item in the current row in current state buffer.
    * The returned value is NOT converted to the client format. 
    * <pre>
    *    Example: If you want to get the corresponding server format value for a perticular asp filed
    *       String server_format_date = rowset.getValue("FIELD_DB_NAME");
    *</pre> 
    * @param name item name. Note that if you have a ASPField which has a different db name then you
    * have to give the db name, not the field name.
    * @see #getClientValue 
    * @see #getClientValueAt 
    * @see #getValueAt  
    * @see ifs.fnd.asp.ASPField.convertToClientString
    */
   public String getValue( String name )
   {
      return getValueAt(current_row_no,name);
   }


   /**
    * Return a Number value of the named item in the current row in current state buffer.
    * The returned value is NOT converted to the client format.
    */
   public double getNumberValue( String name )
   {
      return getNumberValueAt(current_row_no,name);
   }


   /**
    * Modify the value of the named item in the current row in current state buffer.
    */
   public void setValue( String name, String value )
   {
      setValueAt(current_row_no,name,value);
   }

   /**
    * Modify the value of the named item in the current row in current state buffer.
    */
   public void setValue( String name, String value, boolean make_dirty ) {
      setValueAt(current_row_no, name, value, make_dirty);
   }
   
   /**
    * Modify the value of the named item in the given row in current state buffer.
    */
   public void setValueAt( int rowno, String name, String value )
   {
      try {
         setValueAt(rowno, name, value, null);
      } catch( Throwable any ) {
         error(any);
      }
   }
   
   /**
    * Modify the value of the named item in the given row in current state buffer.
    */
   public void setValueAt( int rowno, String name, String value, boolean make_dirty){
      try {
         setValueAt(rowno, name, value, null, make_dirty);
      } catch (FndException ex) {
         error(ex);
      }
   }
      
   /**
    * Sets a number value in client format to the row set.
    * @params name Name of the ASPField.
    * @params value Number value in client format.
    */
   public void setNumberValue( String name, String value)
   {
       setNumberValueAt(current_row_no, name, value, true);
   }
   
   public void setNumberValue( String name, String value, boolean make_dirty)
   {
       setNumberValueAt(current_row_no, name, value, true, make_dirty);
   }
   
   /**
    * Sets a number value in client format to the row set.
    * @params rowno Row Number.
    * @params name Name of the ASPField.
    * @params value Number value in client format.
    */
   public void setNumberValueAt( int rowno, String name, String value, boolean is_client_format){
      setNumberValueAt(rowno, name, value, is_client_format, true);
   }
   
   public void setNumberValueAt( int rowno, String name, String value, boolean is_client_format, boolean make_dirty)
   {
      try
      {
         if(is_client_format) 
         {
             double val = getASPManager().parseNumber(name, value);
             setNumberValueAt(rowno,name,val,make_dirty);         
         }
         else
             setValueAt(rowno, name, value,"N", make_dirty);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
      
   /**
    * Sets a java Date object type date value to the row set.
    * @params name Name of the ASPField.
    * @params value Number value as a java Date object.
    */
   public void setDateValue( String name, Date value) {
       setDateValueAt(current_row_no, name, value);
   }
   
   public void setDateValue( String name, Date value, boolean make_dirty) {
       setDateValueAt(current_row_no, name, value, make_dirty);
   }
   
   /**
    * Sets a java Date object type date value to the row set.
    * @params rowno Row number.
    * @params name Name of the ASPField.
    * @params value Number value as a java Date object.
    */
   public void setDateValueAt( int rowno, String name, Date value){
       setDateValueAt(current_row_no, name, value, true);
   }
   
   public void setDateValueAt( int rowno, String name, Date value, boolean make_dirty) {
      try
      {
         String val = getASPManager().getServerFormatter().format(value, DataFormatter.DATETIME);
         setValueAt(rowno,name,val,make_dirty);         
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Sets a date value in client format to the row set.
    * @params name Name of the ASPField.
    * @params value Number value in client format.
    */ 
   public void setDateValue( String name, String value) {
       setDateValueAt(current_row_no, name, value, true);
   }
   
   public void setDateValue( String name, String value, boolean make_dirty) {
       setDateValueAt(current_row_no, name, value, true, make_dirty);
   }
   
   /**
    * Sets a date value in client format to the row set.
    * @params rowno Row number.
    * @params name Name of the ASPField.
    * @params value Number value in client format.
    */
   public void setDateValueAt( int rowno, String name, String value, boolean is_client_value){
      setDateValueAt(current_row_no, name, value, is_client_value, true);
   }
   
   public void setDateValueAt( int rowno, String name, String value, boolean is_client_value, boolean make_dirty)
   {
      try
      {
         String val = value; 
         if(is_client_value)
           val = getASPManager().getServerFormatter().format(this.getASPManager().parseDate(name,value),DataFormatter.DATETIME);
         setValueAt(rowno,name,val,make_dirty);         
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /**
    * Modify the value of the named item in the current row in current state buffer.
    * The item will have the specified numeric value.
    * The item type must already be "N" (NUMBER).
    */
   public void setNumberValue( String name, double value ) {
      setNumberValueAt(current_row_no,name,value);
   }

   public void setNumberValue( String name, double value, boolean make_dirty ) {
      setNumberValueAt(current_row_no,name,value,make_dirty);
   }

   /**
    * Modify the value of the named item in the given row in current state buffer.
    * The item will have the specified numeric value.
    * The item type must already be "N" (NUMBER).
    */
   public void setNumberValueAt( int rowno, String name, double value ){
      setNumberValueAt(rowno, name, value, true);
   }
   
   public void setNumberValueAt( int rowno, String name, double value, boolean make_dirty )
   {
      try
      {
         if( Double.isInfinite(value) )
            throw new FndException("FNDPROSINFNUM: Infinite Number in item &1", name);
         String val = Double.isNaN(value) ?
                      null :
                      getASPManager().getServerFormatter().format(new Double(value),DataFormatter.NUMBER);

         setValueAt(rowno,name,val,"N", make_dirty);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Return a String value of the named item in the current row in current state buffer.
    * The returned value is NOT converted to the client format.
    */
   public String getDbValue( String name )
   {
      return getDbValueAt(current_row_no,name);
   }


   /**
    * Return the number of rows that are marked with a custom command.
    */
   public int countMarkedRows()
   {
      return countDirtyRows( COMMAND, null );
   }

   public int countMarkedRows( String method_id )
   {
      return countDirtyRows( COMMAND, method_id );
   }


   /**
    * Return the number of rows that are marked as modified, new or removed.
    */
   public int countEditedRows()
   {
      return countDirtyRows( EDITED, null );
   }

   /**
    * Return the number of row buffers in the block buffer in context buffer.
    * The result is depending of the filter mode.
    */
   public int countRows()
   {
      return countRows( filter_enabled );
   }

   /**
    * Return the number of selected row buffers in the block buffer in context buffer.
    */
   public int countSelectedRows()
   {
      return countRows( true );
   }


   //==========================================================================
   // Select filter
   //==========================================================================

   /**
    * Enables filtering of selected rows. Affects move methods and current row.
    */
   public void setFilterOn()
   {
      filter_enabled = true;
      first();
   }

   /**
    * Disables filtering of selected rows. Affects move methods and current row.
    */
   public void setFilterOff()
   {
      filter_enabled = false;
   }


   //==========================================================================
   // Navigating
   //==========================================================================


   /**
    * Move to the next row.
    * The result is depending of the filter mode.
    */
   public boolean next()
   {
      return forward(1);
   }

   /**
    * Move to the previous row.
    * The result is depending of the filter mode.
    */
   public boolean previous()
   {
      return backward(1);
   }


    /**
     * Sets the __master_has_rows flag. If a master block okFind action is executed, and the masterblock
     * is not populated, there will no subsequent okFind() calls to item block. It will then also clear
     * the itemblocks from old data.
     */
   public String syncItemSets()
   {
      try
      {
         ASPPage        page   = getASPPage();
         ASPBlock       blk    = getBlock();
         ASPBlockLayout lay    = blk.getASPBlockLayout();
         ASPCommandBar  cmdbar = blk.getASPCommandBar();

         if(lay.isMultirowLayout() &&
            !(blk.getMasterBlock()==null && (blk.getASPRowSet().countRows() == 1)
            && (lay.isMultirowLayout() || lay.isSingleLayout())
            && lay.isAutoLayoutSelectOn()))
            if(page instanceof ASPPageProvider)
               return page.masterHasRows(false);
            else
               return "__master_has_rows=false;";

         if(countRows()>0)
         {
            if(page instanceof ASPPageProvider)
               return page.masterHasRows(true);
            else
               return "__master_has_rows=true;";
         }
         else
         {
            getASPManager().clearMyChilds(cmdbar);
            if(page instanceof ASPPageProvider)
               return page.masterHasRows(false);
            else
               return "__master_has_rows=false;";
         }
      }
      catch(Throwable any)
      {
         error(any);
         return "";
      }
   }


   /**
    * Move current row to a new position.
    * The result is depending of the filter mode.
    */

   public boolean goTo( int count )
   {
      try
      {
         if(DEBUG)
         {
            debug("ASPRowSet.goTo("+count+")");
            debug("  [goTo]current_row_no="+current_row_no);
         }

         if(!already_sorted)
         {
            already_sorted = true;
            String sort = getASPManager().readValue("__SORT");
            if(DEBUG) debug("  __SORT="+sort);
            if( !Str.isEmpty(sort) )
            {
               int pos = sort.indexOf('.');
               String tabname = sort.substring(0,pos);
               //String colname = sort.substring(pos+1);
               String colname = getASPPage().getASPField(sort.substring(pos+1)).getDbName();

               if( tabname.equals(getBlock().getName()) )
               {
                  if( colname.equals(sorted_column) )
                     sorted_ascending = !sorted_ascending;
                  else
                     sorted_ascending = true;
                  if( !db_links_created ) createLinksToDbState();
                  if(DEBUG) debug(this+": Sorting key="+colname+", ascending="+sorted_ascending);
                  sort(colname,sorted_ascending);
                  sorted_column = colname;
               }
            }
         }

         int current_no = getCurrentRowNo();

         if (current_no > count)
         {
            if ( !backward( current_no - count ) )
               return first();
         }
         else if (current_no < count)
         {
            if ( !forward( count - current_no ) )
               return last();
         }
         return true;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   //==========================================================================
   // Sorting
   //==========================================================================

   /**
    * Sorts the rowset.
    * @param name Name of the key field.
    */
   public void sort( String name )
   {
      sort(name, true);
   }

   String getSortedColumnName()
   {
      return sorted_column;
   }

   boolean isSortedAscending()
   {
      return sorted_ascending;
   }

   //==========================================================================
   // Miscellaneous methods
   //==========================================================================

   /**
    *  Store the changes of the rowset.
    */
   public void store()
    {
      if (DEBUG) debug("ASPRowSet.store()");
      if(getBlock().getASPBlockLayout().isSingleLayout())
      {
         unselectRows();
         selectRow();
         return;
      }
      if(getBlock().getASPBlockLayout().isMultirowLayout())
        {
        changeRows();
        storeSelections();
        goTo(getCurrentRowNo()+getRowSelected());
        }
      else
       changeRow();
    }

   void duplicatePressed()
   {
      if (DEBUG) debug("ASPRowSet.duplicatePressed()");
      duplicate_pressed_flag = true;
   }


   String visible_ColWidth_(ASPField[] fields) throws Exception
   {
      if (DEBUG) debug("ASPRowSet.dataString()");
      AutoString out_str = new AutoString();
      generated_fields = getASPPage().getASPContext().readValue("__"+getBlock().getName()+"_COLWIDTH");
      if(DEBUG) debug("  generated_fields="+generated_fields);

      out_str.append(generated_fields);

       return out_str.toString();

   }

   /**
    * Resets the contents of the current row in the Current State to the
    * corresponding row from the Db State.
    *
    * The row must already exist in the block. The buffer must not be null.
    */
   public void resetRow()
   {
      try
      {
         resetRow(current_row_no);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }
   
   // Added by Terry 20130318
   // Resets all rows of the current row in the Current State from the Db State.
   public void resetAllRows()
   {
      try
      {
         resetAllRows(false);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Get current values from the database and refresh all rows in the rowset.
    * Only existing rows will be updated.
    * @see #refreshAllRows(boolean)
    */
   public void refreshAllRows()
   {
      refreshAllRows(false);
   }

   /**
    * Returns the current row from the current state buffer or null
    * if such row does not exist. Not allowed to call from FndWebFeatures.
    */
   public ASPBuffer getRow()
   {
      if (getASPPage() instanceof FndWebFeature)
      {
         error(new FndException("Can not be called from FndAbstractFetures."));
         return null;
      }
      return getRow( current_row_no );
   }

   /**
    * Returns the current row from the current state of data.
    */
   
   AbstractDataRow getDataRow()
   {
      return getDataRow( current_row_no );
   }

   //=============================================================================
   // Abstract methods
   //=============================================================================
   
   //PUBLIC methods
   abstract public String getRowStatus();
   abstract public boolean backward( int count );
   abstract public void changeRow();
   abstract public void changeRows();
   abstract public void clear();
   abstract public void clearRow();
   abstract public int countDbRows();
   abstract public int countSkippedDbRows();
   abstract public boolean first();
   abstract public boolean forward( int count );   
   abstract public String getValueAt( int rowno, String name );
   abstract public double getNumberValueAt( int rowno, String name );   
   abstract public int markSelectedRows( String method_id );
   abstract public int moreRowsExist();  
   abstract public int getCurrentRowNo();
   abstract public void nextDbSet();
   abstract public void prevDbSet();
   abstract public void firstDbSet();
   abstract public void lastDbSet();
   // Added by Terry 20121120
   // Add goto page command function
   abstract public void gotoPage(int page);
   // Added end
   abstract public void refreshAllRows(boolean only_selected);
   abstract public void refreshRow();
   abstract public boolean last(); 
   abstract public void sort( String key_name, boolean ascending );
   abstract public void trace();   
   abstract public void setSelectedRowsRemoved();
   abstract public int unselectRows();  
   abstract public void setRemoved();
   abstract public void setRow( ASPBuffer buf );
   abstract public int addRow( ASPBuffer buf );
   // Added by Terry 20120923
   // Add a row to ASPRowSet, without status change.
   abstract public int addRowNoStatus( ASPBuffer buf );
   // Added end
   abstract public String getDbValueAt( int rowno, String name );
   abstract public void setEdited( String fields );
   
   //protected methods
   abstract protected void doActivate() throws FndException;
   abstract protected int countDirtyRows( int type, String method_id );
   abstract protected int countRows( boolean only_selected );
   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // abstract protected boolean isRowSelected( int row_no ) throws FndException;
   abstract public boolean isRowSelected( int row_no ) throws FndException;
   // Modified end
   abstract protected void markRow( String method_id, int row_no ) throws FndException;
   abstract protected void resetRow( int row_no ) throws FndException;
   // Added by Terry 20130318
   // Resets all rows of the current row in the Current State from the Db State.
   abstract public void resetAllRows( boolean only_selected ) throws FndException;
   // Remove all rows of the current row in the Current State with status
   abstract public void removeAllRows(String status) throws FndException;
   // Added end
   abstract protected void initSelectRows();
   abstract protected void setValueAt( int rowno, String name, String value, String type ) throws FndException;
   abstract protected void setValueAt( int rowno, String name, String value, String type, boolean make_dirty ) throws FndException;
   abstract protected void createLinksToDbState() throws FndException; 
   abstract protected ASPPoolElement clone( Object block ) throws FndException;
   
   //friendly methods
   abstract void resetSubMembers() throws FndException;
   abstract String dataString(ASPField[] fields);
   abstract String getSafeValueAt( int rowno, String name ) throws FndException;
   abstract void saveQuery();
   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // abstract void selectRow( int row_no ) throws FndException;
   abstract public void selectRow( int row_no ) throws FndException;
   // Modified end
   abstract void unselectRow( int row_no ) throws FndException;
   abstract String visibleString_(ASPField[] fields, String separator, String channel) throws FndException;
   abstract void prepareClientScript();
   abstract void duplicatePressed3();
   abstract int translateRowNumber(int rowno);
   abstract void currentDbSet();
   
   // buffer specific existing methods
   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // abstract protected ASPBuffer getRow( int row_no );   
   abstract public ASPBuffer getRow( int row_no );
   // Modified end
   abstract public ASPBuffer getRows();                        
   abstract public ASPBuffer getRows( String field_names );    
   abstract public ASPBuffer getSelectedRows();                
   abstract public ASPBuffer getSelectedRows( String field_names );    
   
   // general mothods to be used for both implementation
   abstract AbstractDataRow getDataRow( int row_no );   
   abstract AbstractDataRowCollection getDataRows();
     
   // abstract AbstractDataRowCollection getDataRows( String field_names );    //todo
   // abstract AbstractDataCollection getSelectedDataRows();                //todo
   // abstract AbstractDataCollection getSelectedDataRows( String field_names );  //todo   
   

   
}

