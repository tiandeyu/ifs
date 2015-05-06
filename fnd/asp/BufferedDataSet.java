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
 * File        : BufferedDataSet.java
 * Description : Buffer specific implementation for data set. They are moved to this class
 *               when ASPRowSet become abstract and general. 
 * Notes       :
 * ----------------------------------------------------------------------------
 * $Log: BufferedDataSet.java,v $
 * 2010/04/26 buhilk Bug 88982, Overloaded setValueAt() method to allow non-dirty values.
 * 2010/01/21 sumelk Bug 87622, Added currentDbSet(). Changed getDbSet() to reload the current rowset after deleting rows.
 * 2009/12/16 sumelk Bug 87873, Changed refreshAllRows() for not to run the query when rowset is empty.
 * 2009/09/02 amiklk Bug 84497, added getSSXMLDataSet_ and getSSXMLHeader to support XML Excel exporting.
 * 2009/07/07 amiklk Bug 84481, Changed skipDbSet() to preserve current row number of any number of parent blocks.
 * 2009/04/06 sumelk Bug 81957, Changed skipDbSet() to reposition the header correctly when switching between row sets.
 * 2008/08/21 sumelk Bug 75713, Added firstDbSet() and lastDbSet(). Changed nextDbSet(), prevDbSet() & getDbSet().
 * 2008/08/11 rahelk Bug id 74484, added _COUNT buffer to next/previous request buffers. Removed duplicate code.
 * 2008/06/26 mapelk Created. Bug 74852, Programming Model for Activities.  
 * 
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.ap.*;


import java.io.*;
import java.util.*;

/**
 * This will be used by ASPBlock to store populated data after 
 * fetching from the context when the page NOT extended from 
 * FndWebFeature.
 * <p>
 * 
 */
public class BufferedDataSet extends ASPRowSet
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.BufferedDataSet");

   //==========================================================================
   // Instance variables
   //==========================================================================


   // private mutable (temporary)
   private Buffer            curr_state_blk;
   private Buffer            db_state_blk;
   private Buffer            first_selected_row;
 
   //==========================================================================
   // Constructors
   //==========================================================================


   /**
    * Package constructor. Calls constructor within super class.
    */
   BufferedDataSet( ASPBlock block )
   {
      super(block);
      if (DEBUG) 
         debug(this+" - constructor");
   }




   //==========================================================================
   //  Methods for implementation of pool.
   //    doFreeze() is implemented in supper class
   //==========================================================================

   /**
    * Called by doReset() from supper class ASPRowSet
    */
   void resetSubMembers() throws FndException
   {
      if (DEBUG) 
         debug(this+":BufferedDataSet::resetSubMembers()");

      curr_state_blk         = null;
      db_state_blk           = null;
      first_selected_row     = null;
   }

   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      if (DEBUG) debug(this+": doActivate()");

      Buffer current_state;
      try
      {
         current_state = getASPPage().getASPContext().getCurrentStateBuffer();
      }
      catch( Exception e )
      {
         throw new FndException(e);
      }
      filter_enabled = false;
      // Fetch the current block from current state buffer.
      // Create it if does not exist yet.
      Item item = current_state.getItem( block_name, null );
      if (item==null)
      {
         item = newCompoundItem( block_name );
         current_state.addItem( item );
      }
      curr_state_blk = item.getBuffer();
      // Fetch current block from db state buffer. Can be null.
      ASPBuffer db_buf = getASPPage().getASPContext().getDbState();
      if (db_buf==null)
         db_state_blk = null;
      else
      {
         item = db_buf.getBuffer().getItem( block_name, null );
         if (item==null)
            db_state_blk = null;
         else
            db_state_blk = item.getBuffer();
      }
      if (DEBUG) debug("  curr_state_blk: "+ curr_state_blk);
      if (DEBUG) debug("  db_state_blk: "  + db_state_blk);

      readInfo();

      prev_curr_row = current_row_no;
      first_selected_row = null;
      duplicate_pressed_flag = false;
      already_sorted         = false;
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object block ) throws FndException
   {
      if (DEBUG) ((ASPBlock)block).debug(this+": clone()");
      BufferedDataSet rs = new BufferedDataSet((ASPBlock)block);
      rs.block_name = this.block_name;
      rs.setCloned();
      if (DEBUG) ((ASPBlock)block).debug(this+" cloned into "+rs);
      return rs;
   }

   /**
    * Callback function that will be called from ASPManager just before
    * generating of client script. Stores private variables in INFO buffer.
    */
   void prepareClientScript()
   {
      if (DEBUG) debug(this+": prepareClientScript()");
      writeInfo();
   }

   /**
    * Reads private variables from INFO buffer.
    */
   private void readInfo()
   {
      Buffer info = curr_state_blk==null ?
                    null :
                    curr_state_blk.getBuffer(INFO,null);

      if( info==null ) info = newBuffer();
      current_row_no   = info.getInt("CURRENT_ROW",0);
      filter_enabled   = info.getInt("FILTER_ENABLED",0) != 0;
      sorted_column    = info.getString("SORTED_COLUMN",null);
      sorted_ascending = info.getInt("SORTED_ASCENDING",1) != 0;
      db_links_created = info.getInt("DB_LINKS_CREATED",0) != 0;

      if (DEBUG) debug("BufferedDataSet.readInfo()\n:" +
                       "\tcurrent_row_no   = "+current_row_no   + "\n" +
                       "\tfilter_enabled   = "+filter_enabled   + "\n" +
                       "\tsorted_column    = "+sorted_column    + "\n" +
                       "\tsorted_ascending = "+sorted_ascending + "\n");
   }


   /**
    * Stores private variables in INFO buffer.
    */
   private void writeInfo()
   {
      if( curr_state_blk==null ) return;
      Buffer info = curr_state_blk.getBuffer(INFO,null);
      if( info==null )
      {
         info = newBuffer();
         curr_state_blk.addItem(INFO,info);
      }
      info.setItem( "CURRENT_ROW",      current_row_no );
      info.setItem( "FILTER_ENABLED",   filter_enabled ? 1 : 0 );
      info.setItem( "SORTED_COLUMN",    sorted_column );
      info.setItem( "SORTED_ASCENDING", sorted_ascending ? 1 : 0 );
      info.setItem( "DB_LINKS_CREATED", db_links_created ? 1 : 0 );
   }

   //==========================================================================
   // Miscellaneous private methods
   //==========================================================================

   /**
    * Returns new, empty instance of Buffer.
    */
   private Buffer newBuffer()
   {
      return getASPManager().getFactory().getBuffer();
   }

   /*
    * Returns new instance of Item with a given name and an empty buffer.
    */
   private Item newCompoundItem( String name )
   {
      return new Item( name, newBuffer() );
   }


   //==========================================================================
   // Private routines for working with rows in the current state context buffer
   //==========================================================================


   /**
    * Returns a reference to a row item from a block buffer within the current
    * state context buffer or null if such block or row does not exist.
    */
   private Item getRowItem( int row_no )
   {
      if (DEBUG) debug("BufferedDataSet.getRowItem("+row_no+")");
      Item row_item = curr_state_blk.getItem( row_no );
      if (row_item==null) return null;
      if (DEBUG) debug("  row_item: "+row_item);
      return DATA.equals( row_item.getName() ) ? row_item : null;
   }


   /**
    * Returns a reference to a row buffer from a block buffer within the current
    * state context buffer or null if such block or row does not exist.
    */
   private Buffer getRowBuffer( int row_no )
   {
      if (DEBUG) debug("BufferedDataSet.getRowBuffer("+row_no+")");
      Item row_item = getRowItem( row_no );
      return row_item ==null ? null : row_item.getBuffer();
   }


   /**
    * Creates or updates a row definition in the block buffer within the current
    * state context buffer. Returns row number.
    * Sets the current row position.
    */
   private int setRowBuffer( ASPBuffer buf, int row_no, int method, String param ) throws FndException
   {
      //  *********************************************************************
      //  *            Parameter:    buf       row_no    method       param   *
      //  * Function:                                                         *
      //  *   setRow()                +         +        SET_ROW      null    *
      //  *   changeRow()            null       +        CHANGE_ROW   null    *
      //  *   markRow()              null       +        MARK_ROW      +      *
      //  *   addRow()                +        <0        ADD_ROW      null    *
      //  *********************************************************************

      if (DEBUG)
      {
         debug("BufferedDataSet.setRowBuffer("+buf+","+row_no+","+method+","+param+")");
         if (buf!=null) buf.traceBuffer("  Input");
      }

      ASPBlock blk = getBlock();

      // create a new, empty buffer for destination
      ASPBuffer dest = getASPManager().newASPBuffer();
      // fetch the old row from db state if exists
      Item     item = getDbRowItem(row_no);
      Buffer    row = item==null || !DATA.equals(item.getName()) ? null : item.getBuffer();
      ASPBuffer old = row==null                                  ? null : dest.newASPBuffer( row );
      if (DEBUG)
         if (old==null)
            debug("  [setRowBuffer]old==null");
         else
            old.traceBuffer("  [setRowBuffer]old-");
      // call a proper method within ASPBlock to get a destination buffer
      boolean changed = false;
      switch( method )
      {
         case SET_ROW :
            changed = blk.addToBuffer( dest, buf, old );
            clearSortedColumn();
            break;
         case CHANGE_ROW :
            changed = blk.readToBuffer( dest, old );
            if( changed ) clearSortedColumn();
            break;
         case MARK_ROW :
            changed = blk.addToBuffer( dest, getRow( row_no ), old );
            break;
         case ADD_ROW :
            clearSortedColumn();
            blk.addToBuffer( dest, buf );
            break;
         default :
            throw new FndException("FNDROSMETNDEF: Method not defined !");
      }
      if (DEBUG)
         if (dest==null)
            debug("  [setRowBuffer]dest==null");
         else
            dest.traceBuffer("  [setRowBuffer]dest-");

      Buffer destbuf = dest.getBuffer();
      // set destination buffer in current state
      if ( method==ADD_ROW )
      {
         item = new Item( DATA );
         if (filter_enabled)
            item.setType( SELECTED );
      }
      else
      {
         // fetch the current row from current state - must exist
         item = curr_state_blk.getItem( row_no );
         if(item==null || !DATA.equals(item.getName()) )
            throw new FndException("FNDROSNOROW3: Row number '&1' does not exist in the current row set",row_no+"");
         Buffer curbuf = item.getBuffer();

         // add other items
         int fldcnt = blk.countFields();
         int itmcnt = curbuf.countItems();
         if( method!=MARK_ROW && itmcnt>fldcnt )
         {
            for(int i=fldcnt; i<itmcnt; i++)
            {
               Item itm = curbuf.getItem(i);
               if(DEBUG) debug("  adding item ["+itm.getName()+"]");
               destbuf.addItem( itm );
            }
         }
      }

      // set new value
      if ( method!=MARK_ROW )
         item.setValue( destbuf );
      // set status and eventually remove the row
      boolean remove_item = setRowStatus( item, method, param, old==null ? true : changed );
      if ( remove_item )
         curr_state_blk.removeItem( row_no-- );
      // return row number
      if ( method==ADD_ROW )
         row_no = curr_state_blk.insertItem( item, countRows(curr_state_blk) );
      if (DEBUG) debug("  row_no="+row_no);

      if( row_no<0 ) row_no = 0;
      return row_no;
   }


   private void setRowBuffer( int row_no, int position ) throws FndException
   {
      //  *************************************************************
      //  *            Parameter:    row_no    method       position  *
      //  * Function:                                                 *
      //  *   changeRowAt()           >= 0     CHANGE_ROW    >= 0     *
      //  *************************************************************

      if (DEBUG) debug("BufferedDataSet.setRowBuffer("+row_no+","+position+")");

      // fetch the old row from db state if exists
      Item   dbitem = getDbRowItem(row_no);
      Buffer dbbuf  = dbitem==null || !DATA.equals(dbitem.getName()) ? null : dbitem.getBuffer();

      // fetch the current row from current state - must exist
      Item curitem = curr_state_blk.getItem(row_no);
      if(curitem==null || !DATA.equals(curitem.getName()) )
         throw new FndException("FNDROSNOROW2: Row number '&1' does not exist in the current row set",row_no+"");
      Buffer curbuf = curitem.getBuffer();

      ASPField[]      fields  = getBlock().getFields();
      ServerFormatter srvfmt  = getASPPage().getASPConfig().getServerFormatter();
      boolean         changed = false;

      // loop over all fields in the current state buffer
      for( int i=0; i<fields.length; i++)
      {
         Item          flditem = curbuf.getItem(i);
         ASPField      fld     = fields[i];
         String        name    = fld.getDbName();
         DataFormatter clifmt  = fld.getDataFormatter();

         if(!name.equals(flditem.getName()))
            throw new FndException("FNDROSNSNAM: Item name '&1' not equals field name '&2'.",flditem.getName(),name);

         if(position==0)
            fld.setUsedAsParameter(generated_fields.indexOf(","+fld.getName()+",")>=0);

         if(!fld.isUsedAsParameter())
         {
            if(DEBUG) debug("     "+name+" column not generated. Skipping...");
            continue;
         }

         String oldval = dbbuf==null ? null : dbbuf.getString(i);
         String newval = fld.prepareClientValue( fld.readValueAt(position+1) );
         if(DEBUG) debug("     "+name+"\t oldval="+oldval+"  newval="+newval);

         ASPBuffer.convertToServerItem(flditem,newval,srvfmt,clifmt,dbbuf!=null,oldval);
         if( fld.isComputable() )
            flditem.setStatus("-");
         if( "*".equals(flditem.getStatus()) )
            changed = true;
         if(DEBUG) debug("     \t\t oldval="+oldval+"  newval="+newval+"  changed="+changed);
      }

      if( changed ) clearSortedColumn();
      // set status and eventually remove the row
      setRowStatus( curitem, CHANGE_ROW, null, dbbuf==null ? true : changed );
   }


   private FndException invalidParamName( String param )
   {
      return new FndException("FNDROSPARNE: Error in parameter name ! [&1]", param);
   }

   private FndException operationNotAllowed( String oper, String state )
   {
      return new FndException("FNDROSOPNEAL: Operation [markRow(&1)] not allowed in state [&2].", oper, state);
   }

   private FndException stateNotDefined( String state )
   {
      return new FndException("FNDROSSTNDEF: State not defined : [&1].", state);
   }

   /**
    * Sets a proper row status according to defined rules. Returns true if the row definition
    * should be removed from the current block buffer. Calls by setRowBuffer().
    *
    * @see ifs.fnd.asp.ASPContext#setRowBuffer
    */
   private boolean setRowStatus( Item row, int method, String param, boolean changed ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.setRowStatus("+row+","+method+","+param+","+changed+")");
      if ( !DATA.equals( row.getName() ) ) throw new FndException("FNDROSNDIT: Not a data item !");
      if ( NEW.equals( param ) )           throw invalidParamName(param);
      if ( MODIFY.equals( param ) )        throw invalidParamName(param);

      boolean remove_item = false;
      String state = row.getStatus();
      switch( method )
      {
         case ADD_ROW :
            // add check here:
            // *  if row exists in current or db -> Exception
            row.setStatus( NEW );
            break;
         case SET_ROW    :
         case CHANGE_ROW :
            if ( NEW.equals(state) )
            {   // same state
            }
            else if ( state==null ) // DB
            {
               if( changed ) row.setStatus( MODIFY );
            }
            else if ( MODIFY.equals(state) )
            {
               if (!changed)
                  row.setStatus( null );
               else
                  ; // same state
            }
            else if ( REMOVE.equals(state) )
            {
               //throw operationNotAllowed( "set/changeRow()", state );
            }
            else
            {
               throw stateNotDefined( state );
            }
            break;
         case MARK_ROW :
            if ( NEW.equals(state) )
            {
               if ( REMOVE.equals(param) )
                  remove_item = true;
               else
                  throw operationNotAllowed( param, state );
            }
            else if ( state==null ) // DB
            {
               if ( param==null )
                  throw operationNotAllowed( "null", "DB" );
               else
                  row.setStatus( param );
            }
            else if ( MODIFY.equals(state) )
            {
               if ( REMOVE.equals(param) )
                  row.setStatus( REMOVE );
               else
                  throw operationNotAllowed( param, state );
            }
            else if ( REMOVE.equals(state) )
            {
               if ( param==null )
               {
                  if (changed)
                     row.setStatus( MODIFY );
                  else
                     row.setStatus( null );
               }
               else
                  throw operationNotAllowed( param, state );
            }
            else
            {
               throw stateNotDefined( state );
            }
            break;
         case SUBMIT :
            if ( NEW.equals(state) )
            {
               row.setStatus( null );
            }
            else if ( state==null ) // DB
            {
               throw operationNotAllowed( "submit()", "DB" );
            }
            else if ( MODIFY.equals(state) )
            {
               row.setStatus( null );
            }
            else if ( REMOVE.equals(state) )
            {
               remove_item = true;
            }
            else
            {
               throw stateNotDefined( state );
            }
            break;
         default :
            throw new FndException("FNDROSEVNDEF: Event not defined !");
      }
      return remove_item;
   }


   private int getDbRowPosition( int row_no )
   {
      if(DEBUG) debug("BufferedDataSet.getDbRowPosition("+row_no+"): db_links_created="+db_links_created);

      if( !db_links_created ) return row_no;
      Item curritem = curr_state_blk.getItem(row_no);
      if(DEBUG) debug("curritem="+curritem);
      return curritem.getBuffer().getInt("__DB_ROW_NO",row_no);
   }

   private Item getDbRowItem( int row_no )
   {
      if( db_state_blk==null || row_no<0 ) return null;
      int db_row_no = getDbRowPosition(row_no);
      return db_state_blk.getItem(db_row_no);
   }


   /**
    * Return the Db State row that corresponds to
    * the specified Current State row.
    */
   private Buffer getDbRow( int row_no )
   {
      Item item = getDbRowItem(row_no);
      if( item==null || !DATA.equals(item.getName()) ) return null;
      return item.getBuffer();
   }

   //==========================================================================
   // Public routines for working with rows in the current state context buffer
   //==========================================================================

   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // protected ASPBuffer getRow( int row_no )
   public ASPBuffer getRow( int row_no )
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.getRow("+row_no+")");
         Buffer buf = getRowBuffer( row_no );
         if (buf==null) return null;
         buf = (Buffer)buf.clone();
         return getASPManager().newASPBuffer(buf);
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }
   
   /**
    * Returns an ASPBuffer containing all rows included in this BufferedDataSet.
    */
   public ASPBuffer getRows()
   {
      try
      {
         Buffer buf = (Buffer)curr_state_blk.clone();
         buf.removeItem(INFO);
         return getASPManager().newASPBuffer(buf);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns an ASPBuffer containing all rows included in this BufferedDataSet.
    * Each row will contain only the specified items.
    */
   public ASPBuffer getRows( String field_names )
   {
      try
      {
         String[] cols = listToArray(field_names, ", \t\r\n");
         if( cols==null ) return getRows();

         Buffer rows = curr_state_blk;
         for( int c=0; c<cols.length; c++ )
//            cols[c] = getASPManager().getASPField(cols[c]).getDbName();
            cols[c] = getASPPage().getASPField(cols[c]).getDbName();

         Buffer buf = rows.newInstance();
         int row_count = rows.countItems();
         for( int i=0; i<row_count; i++ )
         {
            Item item = rows.getItem(i);
            if( DATA.equals(item.getName()) )
            {
               Buffer org = item.getBuffer();
               Buffer row = buf.newInstance();

               for( int c=0; c<cols.length; c++ )
               {
                  Item ii = (Item)org.getItem(cols[c]).clone();
                  if( buf.countItems()>0 ) ii.setName(null);
                  row.addItem(ii);
               }

               buf.addItem(DATA,row);
            }
         }

         return getASPManager().newASPBuffer(buf);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns an ASPBuffer containing all selected rows included in this BufferedDataSet.
    */
   public ASPBuffer getSelectedRows()
   {
      return getSelectedRows(null);
   }

   /**
    * Returns an ASPBuffer containing all selected rows included in this BufferedDataSet.
    * Each row will contain only the specified items.
    */
   public ASPBuffer getSelectedRows( String field_names )
   {
      try
      {
         String[] cols = listToArray(field_names, ", \t\r\n");

         if( cols!=null )
            for( int c=0; c<cols.length; c++ )
//               cols[c] = getASPManager().getASPField(cols[c]).getDbName();
               cols[c] = getASPPage().getASPField(cols[c]).getDbName();

         Buffer buf = curr_state_blk.newInstance();
         int row_count = curr_state_blk.countItems();
         for( int i=0; i<row_count; i++ )
         {
            Item item = curr_state_blk.getItem(i);
            if( SELECTED.equals(item.getType()) )
            {
               Buffer org = item.getBuffer();
               Buffer row;

               if( cols==null )
                  row = (Buffer)org.clone();
               else
               {
                  row = buf.newInstance();
                  for( int c=0; c<cols.length; c++ )
                  {
                     Item ii = (Item)org.getItem(cols[c]).clone();
                     if( buf.countItems()>0 ) ii.setName(null);
                     row.addItem(ii);
                  }
               }
               buf.addItem(DATA,row);
            }
         }

         return getASPManager().newASPBuffer(buf);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Saves a buffer as a row definition in the context buffer for the block.
    * The row must already exist in the block. The buffer must not be null.
    */
   public void setRow( ASPBuffer buf )
   {
      try
      {
         setRow( buf, current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   private void setRow( ASPBuffer buf, int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.setRow("+buf+","+row_no+")");
      setRowBuffer( buf, row_no, SET_ROW, null );
   }

   protected void resetRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.resetRow("+row_no+")");

      ASPBuffer buf = getASPManager().newASPBuffer(getDbRow(row_no));
      setRowBuffer( buf, row_no, SET_ROW, null );
   }
   
   // Added by Terry 20130318
   // Resets all rows of the current row in the Current State from the Db State.
   public void resetAllRows( boolean only_selected ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.resetAllRows()");
      
      for (int i = 0; ;)
      {
         Item item = curr_state_blk.getItem( i );
         
         if (item == null) break;
         
         if ( !DATA.equals( item.getName() ) )
         {
            i = i + 1;
            continue;
         }
         
         if ((only_selected && isRowSelected(i)) || !only_selected)
         {
            if (NEW.equals(item.getStatus()))
               curr_state_blk.removeItem(i);
            else
            {
               resetRow(i);
               i = i + 1;
            }
         }
         else
            i = i + 1;
      }
      
      /* Original invoke
      for (int i = curr_state_blk.countItems() - 1; i >= 0; i--)
      {
         Item item = curr_state_blk.getItem( i );
         if ( !DATA.equals( item.getName() ) ) continue;

         if ((only_selected && isRowSelected(i)) || !only_selected)
         {
            if (NEW.equals(item.getStatus()))
               curr_state_blk.removeItem(i);
            else
               resetRow(i);
         }
      }
      */
   }
   
   // Remove all rows of the current row in the Current State with status
   public void removeAllRows(String status) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.removeAllRows()");
      
      if (Str.isEmpty(status)) return;
      
      for (int i = 0; ;)
      {
         Item item = curr_state_blk.getItem( i );
         
         if (item == null) break;
         
         if ( !DATA.equals( item.getName() ) )
         {
            i = i + 1;
            continue;
         }
         else if (status.equals(item.getStatus()))
            curr_state_blk.removeItem(i);
         else
            i = i + 1;
      }
   }
   // Added end

   /**
    * Adds a buffer as a row to the block definition within the context buffer.
    * The buffer must match the row definition in the block. Creates an empty row
    * if the buffer is null with only meta data definitions. Returns the position
    * of the newly created row.
    */
   public int addRow( ASPBuffer buf )
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.addRow("+buf+")");
         current_row_no = setRowBuffer( buf, -1, ADD_ROW, null );

         if (duplicate_pressed_flag)
         {
            if (DEBUG) debug("  duplicating row...");

            if(filter_enabled)
               throw new FndException("FNDROSDUPFIL: Record duplication is not supported on enabled filter.");

            if(first_selected_row==null)
                throw new FndException("FNDROSNSELR: You must select a row to be duplicated.");

            if( !NEW.equals(getRowStatus()) )
               throw new FndException("FNDROSESTATE: Cannot copy values to an existing row.");

            ASPField[] fields = getBlock().getFields();
            Buffer     newrow = getRowBuffer(current_row_no);

            if( fields.length > newrow.countItems() || fields.length > first_selected_row.countItems() )
               throw new FndException("FNDROSECNT: Not enough items in row.");

            for(int i=0; i<fields.length; i++)
            {
               ASPField fld = fields[i];
               if( fld.isReadOnly() && !fld.isInsertable() ) continue;

               Item   orgitem = first_selected_row.getItem(i);
               Item   newitem = newrow.getItem(i);
               String name    = fld.getDbName();

               if( !name.equals(orgitem.getName()) || !name.equals(newitem.getName()) )
                  throw new FndException("FNDROSENAME: Field name differ from item name.");

               if( name.equals("OBJID")     || name.equals("OBJVERSION") ||
                   name.equals("OBJEVENTS") || name.equals("OBJSTATE") )
                  continue;

               String value = orgitem.getString();
               if(value!=null)
                  newitem.setValue(value);
            }
            unselectRows();
         }
         return getCurrentRowNo();
      }
      catch(Throwable e)
      {
         error(e);
         return -1;
      }
   }
   
   // Added by Terry 20120919
   // Add row with out status change
   public int addRowNoStatus( ASPBuffer buf )
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.addRow("+buf+")");
         current_row_no = setRowBufferNoStatus( buf, -1, ADD_ROW, null );

         if (duplicate_pressed_flag)
         {
            if (DEBUG) debug("  duplicating row...");

            if(filter_enabled)
               throw new FndException("FNDROSDUPFIL: Record duplication is not supported on enabled filter.");

            if(first_selected_row==null)
                throw new FndException("FNDROSNSELR: You must select a row to be duplicated.");

            if( !NEW.equals(getRowStatus()) )
               throw new FndException("FNDROSESTATE: Cannot copy values to an existing row.");

            ASPField[] fields = getBlock().getFields();
            Buffer     newrow = getRowBuffer(current_row_no);

            if( fields.length > newrow.countItems() || fields.length > first_selected_row.countItems() )
               throw new FndException("FNDROSECNT: Not enough items in row.");

            for(int i=0; i<fields.length; i++)
            {
               ASPField fld = fields[i];
               if( fld.isReadOnly() && !fld.isInsertable() ) continue;

               Item   orgitem = first_selected_row.getItem(i);
               Item   newitem = newrow.getItem(i);
               String name    = fld.getDbName();

               if( !name.equals(orgitem.getName()) || !name.equals(newitem.getName()) )
                  throw new FndException("FNDROSENAME: Field name differ from item name.");

               if( name.equals("OBJID")     || name.equals("OBJVERSION") ||
                   name.equals("OBJEVENTS") || name.equals("OBJSTATE") )
                  continue;

               String value = orgitem.getString();
               if(value!=null)
                  newitem.setValue(value);
            }
            unselectRows();
         }
         return getCurrentRowNo();
      }
      catch(Throwable e)
      {
         error(e);
         return -1;
      }
   }
   
   private int setRowBufferNoStatus( ASPBuffer buf, int row_no, int method, String param ) throws FndException
   {
      //  *********************************************************************
      //  *            Parameter:    buf       row_no    method       param   *
      //  * Function:                                                         *
      //  *   setRow()                +         +        SET_ROW      null    *
      //  *   changeRow()            null       +        CHANGE_ROW   null    *
      //  *   markRow()              null       +        MARK_ROW      +      *
      //  *   addRow()                +        <0        ADD_ROW      null    *
      //  *********************************************************************

      if (DEBUG)
      {
         debug("BufferedDataSet.setRowBuffer("+buf+","+row_no+","+method+","+param+")");
         if (buf!=null) buf.traceBuffer("  Input");
      }

      ASPBlock blk = getBlock();

      // create a new, empty buffer for destination
      ASPBuffer dest = getASPManager().newASPBuffer();
      // fetch the old row from db state if exists
      Item     item = getDbRowItem(row_no);
      Buffer    row = item==null || !DATA.equals(item.getName()) ? null : item.getBuffer();
      ASPBuffer old = row==null                                  ? null : dest.newASPBuffer( row );
      if (DEBUG)
         if (old==null)
            debug("  [setRowBuffer]old==null");
         else
            old.traceBuffer("  [setRowBuffer]old-");
      // call a proper method within ASPBlock to get a destination buffer
      boolean changed = false;
      switch( method )
      {
         case SET_ROW :
            changed = blk.addToBuffer( dest, buf, old );
            clearSortedColumn();
            break;
         case CHANGE_ROW :
            changed = blk.readToBuffer( dest, old );
            if( changed ) clearSortedColumn();
            break;
         case MARK_ROW :
            changed = blk.addToBuffer( dest, getRow( row_no ), old );
            break;
         case ADD_ROW :
            clearSortedColumn();
            blk.addToBuffer( dest, buf );
            break;
         default :
            throw new FndException("FNDROSMETNDEF: Method not defined !");
      }
      if (DEBUG)
         if (dest==null)
            debug("  [setRowBuffer]dest==null");
         else
            dest.traceBuffer("  [setRowBuffer]dest-");

      Buffer destbuf = dest.getBuffer();
      // set destination buffer in current state
      if ( method==ADD_ROW )
      {
         item = new Item( DATA );
         if (filter_enabled)
            item.setType( SELECTED );
      }
      else
      {
         // fetch the current row from current state - must exist
         item = curr_state_blk.getItem( row_no );
         if(item==null || !DATA.equals(item.getName()) )
            throw new FndException("FNDROSNOROW3: Row number '&1' does not exist in the current row set",row_no+"");
         Buffer curbuf = item.getBuffer();

         // add other items
         int fldcnt = blk.countFields();
         int itmcnt = curbuf.countItems();
         if( method!=MARK_ROW && itmcnt>fldcnt )
         {
            for(int i=fldcnt; i<itmcnt; i++)
            {
               Item itm = curbuf.getItem(i);
               if(DEBUG) debug("  adding item ["+itm.getName()+"]");
               destbuf.addItem( itm );
            }
         }
      }

      // set new value
      if ( method!=MARK_ROW )
         item.setValue( destbuf );
      // set status and eventually remove the row
      boolean remove_item = false;
      if ( method != ADD_ROW )
         remove_item = setRowStatus( item, method, param, old==null ? true : changed );
      if ( remove_item )
         curr_state_blk.removeItem( row_no-- );
      // return row number
      if ( method==ADD_ROW )
         row_no = curr_state_blk.insertItem( item, countRows(curr_state_blk) );
      if (DEBUG) debug("  row_no="+row_no);

      if( row_no<0 ) row_no = 0;
      return row_no;
   }
   // Added end


   /**
    * Changes row definition within the context buffer according to changes
    * in the Request object from browser.
    */
   public void changeRow()
   {
      try
      {
         if (current_row_no!=prev_curr_row)
         {
            debug("\tcurrent_row_no="+current_row_no+" prev_curr_row="+prev_curr_row);
            throw new FndException("FNDROSROWCH: Cannot perform this operation after the current row has been changed.");
         }
         changeRow( current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Changes the definition of rows within the context buffer according to changes
    * in the Request object from browser.
    */
   public void changeRows()
   {
      if(DEBUG) debug("BufferedDataSet.changeRows()");
      try
      {
         if (current_row_no!=prev_curr_row)
         {
            if(DEBUG) debug("\tcurrent_row_no="+current_row_no+" prev_curr_row="+prev_curr_row);
            throw new FndException("FNDROSROWCH: Cannot perform this operation after the current row has been changed.");
         }
         if(filter_enabled)
            throw new FndException("FNDROSFILEN: This operation is not supported on enabled filter.");

         ASPBlock blk = getBlock();
         generated_fields = getASPPage().getASPContext().readValue("__"+blk.getName()+"_COLUMNS");
         if(DEBUG) debug("  generated_fields="+generated_fields);

         if(!Str.isEmpty(generated_fields))
         {
            int counted_rows = countRows();
            for( int i=0; i<counted_rows; i++ )
            {
               if( i+current_row_no >= countRows() ) break;
               changeRowAt(i, i+current_row_no);
            }
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   private void changeRowAt( int pos, int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.changeRowAt("+pos+","+row_no+")");
      setRowBuffer( row_no, pos );
   }

   private void changeRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.changeRow("+row_no+")");
      setRowBuffer( null, row_no, CHANGE_ROW, null );
   }


   protected void markRow( String method_id, int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.markRow("+method_id+","+row_no+")");
      current_row_no = setRowBuffer( null, row_no, MARK_ROW, method_id );
   }


   /**
    * Mark the current row for removing. The row must already exist in the buffer.
    */
   public void setRemoved()
   {
      try
      {
         if ( REMOVE.equals(getRowStatus()) )
            markRow( null, current_row_no );
         else
            markRow( REMOVE, current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Mark all selected rows for removing. The rows must already exist in the buffer.
    */
   public void setSelectedRowsRemoved()
   {
      try
      {
         int curr_no = current_row_no;

         int i = 0;
         while( i < countRows(curr_state_blk) )
         {
            current_row_no = i;
            if( isRowSelected() ) setRemoved();
            if( current_row_no==i )
               i++;
            else if( curr_no > current_row_no )
               curr_no--;
         }
         current_row_no = curr_no;
         if( current_row_no<0 ) current_row_no = 0;
         if( current_row_no>countRows(curr_state_blk)-1 ) current_row_no = countRows(curr_state_blk)-1;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Remove the current row from the row set without removing it from the database.
    */
   public void clearRow()
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.clearRow()");

         curr_state_blk.removeItem( current_row_no );
         int db_row_no = getDbRowPosition(current_row_no);
         if(curr_state_blk!=db_state_blk)
            db_state_blk.removeItem( db_row_no );
         if( db_links_created ) decreaseLinksToDbState(db_row_no);

         if ( current_row_no >= countRows(curr_state_blk) && current_row_no > 0 )
            current_row_no--;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Mark specified fields (comma separated list of field names) as changed in the current row.
    * The row and the fields must already exist in the buffer.
    */
   public void setEdited( String fields )
   {
      String token = "";
      try
      {
         if (DEBUG) debug("BufferedDataSet.setEdited("+fields+")");

         Item row = getRowItem( current_row_no );
         if (row == null)
            throw new FndException("FNDROSROWNEX: The specified row does not exist in the current row set!");

         boolean row_changed = false;

         StringTokenizer st = new StringTokenizer( fields, ", \t\n\r" );
         while ( st.hasMoreTokens() )
         {
            token = st.nextToken();
            Item item = row.getBuffer().getItem(token);
            item.setStatus("*");
            row_changed = true;
         }

         if (row_changed && Str.isEmpty(row.getStatus()) )
            row.setStatus( MODIFY );
      }
      catch(ItemNotFoundException e)
      {
         error( (new FndException("FNDROSFLDNEX: Field '&1' does not exist in the current row set.",token)).addCaughtException(e) );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   protected void initSelectRows()
   {
      if (initialized_selection) return;
      
/*      for( int i=0; i<page_size; i++ )
      {
         int pos = current_row_no + i;
         if( pos > curr_state_blk.countItems()-1 )
            break;
         Item item = curr_state_blk.getItem(pos);
         if( DATA.equals(item.getName()) )
            item.setType(null);
      }
  */    
      try
      {
         if(DEBUG) debug("BufferedDataSet.selectRows()");
         for (int row_no=0; row_no<countRows(false); row_no++)
               unselectRow( row_no );

         String[] selections = getASPPage().readValues(
                                  ASPTable.SELECTBOX+getASPPage().getASPTable(getBlock().getName()).getTableNo() );

         if (DEBUG) debug("  selections="+selections);
         if (selections==null) return;

         first_selected_row = null;

         for (int i=0; i<selections.length; i++)
         {
            if ( Str.isEmpty(selections[i]) ) continue;
            selectRow( Integer.parseInt( selections[i] ) );

            if(first_selected_row == null)
               first_selected_row = getRowBuffer( Integer.parseInt(selections[i]) );
         }
         initialized_selection = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // void selectRow( int row_no ) throws FndException
   public void selectRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.selectRow("+row_no+")");
      getRowItem( row_no ).setType( SELECTED );
   }

   void unselectRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.unselectRow("+row_no+")");
      getRowItem( row_no ).setType( "" );
   }

   // Modified by Terry 20130910
   // Change visibility
   // Original:
   // protected boolean isRowSelected( int row_no ) throws FndException
   public boolean isRowSelected( int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.isRowSelected("+row_no+")");
      Item item = getRowItem( row_no );
      return SELECTED.equals( item.getType() ) ? true : false;
   }

   /**
    * Returns status of the current row.
    */
   public String getRowStatus()
   {
      try
      {
         return getRowStatus( current_row_no );
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }


   private String getRowStatus( int row_no ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.getRowStatus("+row_no+")");
      Item item = getRowItem( row_no );
      return item==null ? null : item.getStatus();
   }


   /**
    * Unselect all previously selected rows. Returns number of
    * marked rows.
    */
   public int unselectRows()
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.unselectRows()");

         int count = 0;
         for (int i=0; i<curr_state_blk.countItems(); i++)
         {
            Item item = curr_state_blk.getItem( i );
            if ( !DATA.equals( item.getName() ) ) continue;
            item.setType("");
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
    * Mark all selected rows with method_id. Returns a number of
    * marked rows. Clean up previous selection.
    */
   public int markSelectedRows( String method_id )
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.markSelectedRows("+method_id+")");
         int count = 0;

         for (int i=0; i<curr_state_blk.countItems(); i++)
         {
            Item item = curr_state_blk.getItem( i );
            if ( !DATA.equals( item.getName() ) ) continue;

            if ( SELECTED.equals( item.getType() ) )
            {
               item.setStatus( method_id );
               item.setType("");
               count++;
            }
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

   private boolean isStatusOfType( String status, int type, String method_id )
   {
      if (DEBUG) debug("BufferedDataSet.isStatusOfType("+status+","+type+","+method_id+")");
      if ( Str.isEmpty(status) ) return false;
      if ( status.equals( NEW ) || status.equals( MODIFY ) || status.equals( REMOVE ) )
         return type==EDITED ? true : false;
      if ( type==COMMAND && ( method_id==null || method_id.equals( status ) ) )
         return true;
      return false;
   }

   /**
    * Removes all rows from this BufferedDataSet instance
    */
   public void clear()
   {
      try
      {
         if (DEBUG)
         {
            debug("BufferedDataSet.clear()");
            debug("\tcurr_state_blk="+curr_state_blk);
         }
         curr_state_blk.clear();
         if (DEBUG) debug("\tcurr_state_blk="+curr_state_blk);
         current_row_no = 0;
         ASPManager mgr = getASPManager();
         mgr.clearMyChilds(getBlock());
         //getBlock().unsetReadOnly();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   private Item getItemAt( int rowno, String name ) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.getItemAt("+rowno+","+name+")");
      //Item item = curr_state_blk.getItem(rowno);
      //if(item==null || !DATA.equals(item.getName()) )
      //   throw new FndException("FNDROSNOROW: Row number '&1' does not exist in the row set",rowno+"");

      Item item = getRowItem(rowno);
      if( item==null || !DATA.equals(item.getName()) )
         throw new FndException("FNDROSNOROW: Row number '&1' does not exist in the row set",rowno+"");
      return item.getBuffer().getItem(name);
   }

   /**
    * Return a String value of the named item in the given row in current state buffer.
    * The returned value is NOT converted to the client format.
    * <pre>
    *    Example: If you want to get the corresponding server format value for a perticular asp filed
    *       String server_format_date = rowset.getValueAt(2,"FIELD_DB_NAME");
    *</pre> 
    * 
    * @param name item name. Note that if you have a ASPField which has a different db name then you
    * have to give the db name, not the field name.
    * @param rowno Row number
    * @see #getClientValue 
    * @see #getClientValueAt 
    * @see #getValue
    * @see ifs.fnd.asp.ASPField.convertToClientString
    */

   public String getValueAt( int rowno, String name )
   {
      try
      {
         return getItemAt(rowno,name).getString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Safe version of getValueAt(); does not error() - throws exception instead.
    */
   String getSafeValueAt( int rowno, String name ) throws FndException
   {
      try
      {
         return getItemAt(rowno,name).getString();
      }
      catch( Throwable any )
      {
         throw new FndException("FNDNOSUCHITEM: There is no item '&1'.",name);
      }
   }

   /**
    * Return a Number value of the named item in the given row in current state buffer.
    * The returned value is NOT converted to the client format.
    */
   public double getNumberValueAt( int rowno, String name )
   {
      try
      {
         Item item = getItemAt(rowno,name);
         if(item.getType() == null)
            return ASPManager.NOT_A_NUMBER;
         else if( !"N".equals(item.getType()) )
            throw new FndException("FNDROSNOTNUM: The item '&1' is not of the number type",name);

         String text = item.getString();
         if( Str.isEmpty(text) )
            return ASPManager.NOT_A_NUMBER;

         ServerFormatter server_formatter = getASPManager().getServerFormatter();
         Number num = (Number)server_formatter.parse(text, DataFormatter.NUMBER);
         return num.doubleValue();
      }
      catch( Throwable any )
      {
         error(any);
         return ASPManager.NOT_A_NUMBER;
      }
   }

   protected void setValueAt( int rowno, String name, String value, String type) throws FndException {
      setValueAt( rowno, name, value, type, true);
   }
   
   protected void setValueAt( int rowno, String name, String value, String type, boolean make_dirty) throws FndException
   {
      Item item = getItemAt(rowno,name);

      if( type!=null && !type.equals(item.getType()) )
         throw new FndException("FNDROSNEXPT: The item '&1' is not of the expected type '&2'.",name,item.getType());

      if( ASPBuffer.setItemValue(item,value,make_dirty) && make_dirty)
         setRowStatus( curr_state_blk.getItem(rowno), CHANGE_ROW, null, true);
   }

   /**
    * Return a String value of the named item in the given row in current state buffer.
    * The returned value is NOT converted to the client format.
    */
   public String getDbValueAt( int rowno, String name )
   {
      try
      {
         Item item = getDbRowItem(rowno);

         if( item==null || !DATA.equals(item.getName()) )
            throw new FndException("FNDROSNODBROW: Row number '&1' does not exist in the DB row set",rowno+"");

         return item.getBuffer().getItem(name).getString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   // Count rows
   //==========================================================================

   protected int countDirtyRows( int type, String method_id )
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.countDirtyRows("+type+","+method_id+")");
         if( curr_state_blk==null ) return 0;

         int cnt = 0;
         for (int i=0; i<curr_state_blk.countItems(); i++)
         {
            Item item = curr_state_blk.getItem( i );
            if ( !DATA.equals( item.getName() ) ) continue;

            if ( isStatusOfType( item.getStatus(), type, method_id ) )
               cnt++;
         }
         if (DEBUG) debug("  cnt="+cnt);
         return cnt;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Return the number of row buffers in the block buffer in context buffer.
    * The result is depending of the filter mode.
    */
   protected int countRows( boolean only_selected )
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.countRows("+only_selected+")");
         if( curr_state_blk==null ) return 0;

         int count = countRows(curr_state_blk);
         if (DEBUG) debug("  count="+count);
         if (!only_selected) return count;

         int cnt = 0;
         for (int i=0; i<count; i++)
            if ( SELECTED.equals( curr_state_blk.getItem(i).getType() ) ) cnt++;
         if (DEBUG) debug("  cnt="+cnt);
         return cnt;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }



   /**
    * Returns the total number of records in the database.
    */
   public int countDbRows()
   {
      try
      {
         Item item = curr_state_blk==null ? null : curr_state_blk.findItem("INFO/ROWS");
         return item==null ? countRows(curr_state_blk) : item.getInt();
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Returns 1 if there are more rows in the database, 0 otherwise.
    * Returns -1 if the information can not be obtained.
    */
   public int moreRowsExist()
   {
      try
      {
         Item item = curr_state_blk==null ? null : curr_state_blk.findItem("INFO/HAS_MORE_ROWS");
         return item==null ? -1 : ("Y".equals(item.getString()) ? 1 : 0);
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   /**
    * Returns the number of rows that have been skipped while fetching data
    * from the database into this BufferedDataSet.
    */
   public int countSkippedDbRows()
   {
      try
      {
         Item item = curr_state_blk==null ? null : curr_state_blk.findItem("INFO/SKIP_ROWS");
         return item==null ? Integer.parseInt(getASPPage().getASPContext().readValue("__"+getBlock().getName()+".SKIP_ROWS","0")) : item.getInt();
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }



   //==========================================================================
   // Current Row methods
   //==========================================================================

   /**
    * Return position of the current row.
    * The result is depending of the filter mode.
    */
   public int getCurrentRowNo()
   {
      try
      {
         if (DEBUG) debug("BufferedDataSet.getCurrentRowNo()");
         if ( countRows(curr_state_blk)==0 ) return 0;
         if (DEBUG)
         {
            debug("  current_row_no="+current_row_no);
            debug("  filter_enabled="+filter_enabled);
         }
         if (!filter_enabled) return current_row_no;

         int count = -1;
         for (int i=0; i<=current_row_no; i++)
            if ( SELECTED.equals( curr_state_blk.getItem(i).getType() ) ) count++;
         if (DEBUG) debug("  count="+count);
         return count>=0 ? count : 0;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

   //==========================================================================
   // Navigating
   //==========================================================================

   /**
    * Move to the first row.
    * The result is depending of the filter mode.
    */
   public boolean first()
   {
      try
      {
         if (DEBUG)
         {
            debug("BufferedDataSet.first()");
            debug("  [first:old]current_row_no="+current_row_no);
         }
         int max_row_no = countRows(curr_state_blk)-1;
         if (DEBUG) debug("  max_row_no="+max_row_no);
         current_row_no = 0;

         if (DEBUG) debug("  filter_enabled="+filter_enabled);
         if (max_row_no>=0 && filter_enabled)
            while ( !SELECTED.equals( curr_state_blk.getItem(current_row_no).getType() ) )
            {
               current_row_no++;
               if (current_row_no>max_row_no)
               {
                  current_row_no = 0;
                  break;
               }
            }
         if (DEBUG) debug("  [first:new]current_row_no="+current_row_no);
         return max_row_no>=0;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Move to the last row.
    * The result is depending of the filter mode.
    */
   public boolean last()
   {
      try
      {
         if (DEBUG)
         {
            debug("BufferedDataSet.last()");
            debug("  [last:old]current_row_no="+current_row_no);
         }
         int max_row_no = countRows(curr_state_blk)-1;
         if (DEBUG) debug("  max_row_no="+max_row_no);
         current_row_no = max_row_no;

         if (DEBUG) debug("  filter_enabled="+filter_enabled);
         if (max_row_no>=0 && filter_enabled)
            while ( !SELECTED.equals( curr_state_blk.getItem(current_row_no).getType() ) )
            {
               current_row_no--;
               if (current_row_no<0)
               {
                  current_row_no = 0;
                  break;
               }
            }
         if (DEBUG) debug("  [last:new]current_row_no="+current_row_no);
         return max_row_no>=0;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }



   /**
    * Move forward a number of rows.
    * The result is depending of the filter mode.
    */
   public boolean forward( int count )
   {
      try
      {
         if (DEBUG)
         {
            debug("BufferedDataSet.forward("+count+")");
            debug("  [forward:old]current_row_no="+current_row_no);
         }
         int max_row_no = countRows(curr_state_blk)-1;
         if (DEBUG) debug("  max_row_no="+max_row_no);
         if( current_row_no + count > max_row_no ) return false;
         int old_row_no = current_row_no;

         if (DEBUG) debug("  filter_enabled="+filter_enabled);
         if (filter_enabled)
            while ( count>0 )
            {
               if (DEBUG) debug("    count="+count);
               current_row_no++;
               if (current_row_no > max_row_no)
               {
                  current_row_no = old_row_no;
                  return false;
               }
               if ( SELECTED.equals( curr_state_blk.getItem(current_row_no).getType() ) ) count--;
            }
         else
            current_row_no = current_row_no + count;
         if (DEBUG) debug("  [forward:new]current_row_no="+current_row_no);
         return true;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }



   /**
    * Move backward a number of rows.
    * The result is depending of the filter mode.
    */
   public boolean backward( int count )
   {
      try
      {
         if (DEBUG)
         {
            debug("BufferedDataSet.backward("+count+")");
            debug("  [backward:old]current_row_no="+current_row_no);
         }
         int max_row_no = countRows(curr_state_blk)-1;
         if (DEBUG) debug("  max_row_no="+max_row_no);
         if( current_row_no - count < 0 || max_row_no<=0 ) return false;
         int old_row_no = current_row_no;

         if (DEBUG) debug("  filter_enabled="+filter_enabled);
         if (filter_enabled)
            while ( count>0 )
            {
               if (DEBUG) debug("    count="+count);
               current_row_no--;
               if (current_row_no < 0)
               {
                  current_row_no = old_row_no;
                  return false;
               }
               if ( SELECTED.equals( curr_state_blk.getItem(current_row_no).getType() ) ) count--;
            }
         else
            current_row_no = current_row_no - count;
         if (DEBUG) debug("  [backward:new]current_row_no="+current_row_no);
         return true;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }

    /**
     * This function fetches the next chunk of data from the database to the rowset, that did not fit into the rowset previously.
     * The Size of the data chunk depends on the buffer size of the rowset, set in ASPConfig/ADMIN.
     */
    public void nextDbSet()
    {
       getDbSet("next");
    }

    /**
     * The Reverse of nextDbRows()
     */
    public void prevDbSet()
    {
       getDbSet("previous");
    }

    /**
     * This function fetches the first chunk of data from the database to the rowset.
     * The Size of the data chunk depends on the buffer size of the rowset, set in ASPConfig/ADMIN.
     */
    public void firstDbSet()
    {
       getDbSet("first");
    }

    /**
     * This function fetches the last chunk of data from the database to the rowset.
     * The Size of the data chunk depends on the buffer size of the rowset, set in ASPConfig/ADMIN.
     */
    public void lastDbSet()
    {
       getDbSet("last");
    }

    void currentDbSet()
    {
        getDbSet("current");
    }
    
    // Added by Terry 20121120
    // Realize goto page function
    public void gotoPage(int page)
    {
       getDbSet("goto^" + String.valueOf(page));
    }
    // Added end

    private void getDbSet(String position)
    {
       try
       {
          ASPContext ctx = getASPPage().getASPContext();
          ASPBlock blk = getBlock();
          String blk_name = blk.getName();
          ASPManager mgr = getASPManager();
          
          int skipped = Integer.parseInt(ctx.readValue("__"+blk_name+".SKIP_ROWS","0"));
          int dbcount = Integer.parseInt(ctx.readValue("__"+blk_name+".DBCOUNT","0"));
          int delrows = Integer.parseInt(ctx.readValue("__"+blk_name+".DEL_ROWS","0"));
          int buffer_size = Integer.parseInt(ctx.readValue("__"+blk_name+".MAX_ROWS","-1"));
          
          if( buffer_size == -1 )
          {
             if(mgr.isMobileVersion())
                buffer_size = Integer.parseInt(mgr.getConfigParameter("ADMIN/MOBILE_BUFFER_SIZE","10"));
             else
                buffer_size = Integer.parseInt(mgr.getConfigParameter("ADMIN/BUFFER_SIZE","10"));
          }
          int hasmrows = Integer.parseInt(ctx.readValue("__"+blk_name+".HAS_MORE_ROWS","-1"));
          int skipping=0;
          
          if ("next".equals(position))
          {
             if( skipped+buffer_size<dbcount || hasmrows == 1 )
             {
                skipping = skipped+buffer_size-delrows;
                if(skipping>dbcount && hasmrows !=1) skipping = dbcount-skipped;
                
                skipDbSet(blk_name, skipping);
              }
          }
          else if ("previous".equals(position))
          {
             if( skipped>0 )
             {
                skipping = skipped-buffer_size;
                if(skipping<0) skipping = 0;
                
                skipDbSet(blk_name, skipping);
             }
          }
          else if ("first".equals(position))
          {
             skipDbSet(blk_name,0);
          }
          else if ("last".equals(position))
          {
              skipping = ((dbcount/buffer_size) * buffer_size);
              if (skipping == dbcount) skipping = skipping - buffer_size;

              skipDbSet(blk_name, skipping);
          }
          // Added by Terry 20121120
          // From goto page to skipping
          else if (!mgr.isEmpty(position) && position.indexOf("goto") != -1)
          {
             String goto_page = position.substring(position.indexOf("^") + 1);
             skipping = (Integer.parseInt(goto_page) - 1) * buffer_size;
             skipDbSet(blk_name, skipping);
          }
          // Added end
          else
          { 
              // To reload the current DBset after deleting rows
              skipping = skipped;
              skipDbSet(blk_name, skipping);
          }

          dbcount = blk.getASPRowSet().countDbRows();
          hasmrows = blk.getASPRowSet().moreRowsExist();
          
          ctx.writeValue("__"+blk_name+".DBCOUNT",""+dbcount);
          ctx.writeValue("__"+blk_name+".SKIP_ROWS",""+skipping);
          ctx.writeValue("__"+blk_name+".MAX_ROWS",""+buffer_size);
          ctx.writeValue("__"+blk_name+".DEL_ROWS","0");
          ctx.writeValue("__"+blk_name+".HAS_MORE_ROWS",""+hasmrows);
       }
       catch (Throwable any)
       {
          error(any);
       }
    }

    private void skipDbSet(String blk_name, int skipping)
    {
         ASPTransactionBuffer buf = getASPManager().newASPTransactionBuffer();

         ASPBuffer oldbuf = getASPPage().getASPContext().readBuffer("__"+blk_name+".QUERYBUFFER");
         buf.removeItem(blk_name);
         Buffer internal_buffer = buf.getBuffer();
         internal_buffer.addItem(blk_name,oldbuf.getBuffer());
         ASPBuffer mainbuf = buf.getBuffer(blk_name);
         mainbuf.setValue("SKIP_ROWS",""+(skipping));

         ASPBlock master_block = getASPPage().getASPBlock(blk_name).getMasterBlock();
         
         Vector vCurrentRows = new Vector();
         int iBlockIndex=0;
         while(master_block!=null)
         {
            vCurrentRows.add( iBlockIndex++ , new Integer( master_block.getASPRowSet().getCurrentRowNo() ) );
            master_block = master_block.getMasterBlock();
         }
         int current_row = 0;

         getASPManager().callQuery(buf,blk_name);

         master_block = getASPPage().getASPBlock(blk_name).getMasterBlock();
         iBlockIndex = 0;
         while(master_block!=null)
         {
            current_row = ((Integer) vCurrentRows.elementAt(iBlockIndex++)).intValue();
            master_block.getASPRowSet().goTo( current_row );
            master_block = master_block.getMasterBlock();
         }
    }

    /**
     * This functions saves the latest query in the context. For use in the multirow navigation
     *
     */

   void saveQuery()
    {
      ASPContext ctx=getASPPage().getASPContext();
      ASPBuffer oldbuf = ctx.readBuffer("__"+getBlock().getName()+".QUERYBUFFER");
      String dbrows = ctx.readValue("__"+getBlock().getName()+".DBCOUNT","0");
      String skiprows = ctx.readValue("__"+getBlock().getName()+".SKIP_ROWS","0");
      String delrows = ctx.readValue("__"+getBlock().getName()+".DEL_ROWS","0");
      String hasmrows = ctx.readValue("__"+getBlock().getName()+".HAS_MORE_ROWS","-1");

      int buffer_size = Integer.parseInt(ctx.readValue("__"+getBlock().getName()+".MAX_ROWS","-1"));
      
      if(oldbuf!=null)
          {
              ctx.writeBuffer("__"+getBlock().getName()+".QUERYBUFFER",oldbuf);
              ctx.writeValue("__"+getBlock().getName()+".DBCOUNT",dbrows);
              ctx.writeValue("__"+getBlock().getName()+".SKIP_ROWS",skiprows);
              ctx.writeValue("__"+getBlock().getName()+".DEL_ROWS",delrows);
              ctx.writeValue("__"+getBlock().getName()+".MAX_ROWS",""+buffer_size);
              ctx.writeValue("__"+getBlock().getName()+".HAS_MORE_ROWS",""+hasmrows);

          }
    }


   //==========================================================================
   // Sorting
   //==========================================================================

   /**
    * Sorts the rowset.
    * @param key_name Name of the key field.
    * @param ascending Sort order.
    */
   public void sort( String key_name, boolean ascending )
   {
      try
      {
         if (getASPPage().hasASPTable(getBlock().getName()))
            storeSelections();  
         if(DEBUG) debug("Current state buffer for "+this+":\n"+Buffers.listToString(curr_state_blk) );
         RowComparator comp = new RowComparator(getASPManager().getServerFormatter(),key_name,ascending,getASPManager().getLanguageCode());
         Item info = curr_state_blk.getItem("INFO",null);
         if(info!=null)
            curr_state_blk.removeItem("INFO");
         Buffers.sort(curr_state_blk,comp);
         if(info!=null)
            curr_state_blk.addItem(info);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Create a new Item named __DB_ROW_NO in every row (DATA item)
    * in the Current State, containing a link (reference) to the
    * corresponding DbState-row. E
    */
   protected void createLinksToDbState() throws FndException
   {
      Buffer rows = curr_state_blk;

      int row_count = rows.countItems();
      for( int i=0; i<row_count; i++ )
      {
         Item item = rows.getItem(i);
         if( DATA.equals(item.getName()) )
         {
            Buffer row = item.getBuffer();
            //if( row.getItemPosition("__DB_ROW_NO")<0 )
            //   row.addItem("__DB_ROW_NO",i);
            row.setItem("__DB_ROW_NO",i);
         }
      }
      db_links_created = true;
   }

   /**
    * Decrease (by 1) every DbState-row reference that is greater or equal to
    * the specified number.
    */
   private void decreaseLinksToDbState( int from_db_row_no ) throws FndException
   {
      Buffer rows = curr_state_blk;

      int row_count = rows.countItems();
      for( int i=0; i<row_count; i++ )
      {
         Item item = rows.getItem(i);
         if( DATA.equals(item.getName()) )
         {
            Buffer row = item.getBuffer();
            Item link = row.getItem("__DB_ROW_NO",null);
            if( link!=null )
            {
               int db_row_no = link.getInt();
               if( db_row_no>=from_db_row_no )
                  link.setValue( ""+(db_row_no-1) );
            }
         }
      }
   }

   void duplicatePressed3()
   {
      if (DEBUG) debug("BufferedDataSet.duplicatePressed3()"+current_row_no);
      duplicate_pressed_flag = true;
      first_selected_row = getRowBuffer(current_row_no);
   }

   int translateRowNumber(int rowno)
   {
      if(filter_enabled)
      {
         int count = -1;
         for (int i=0; i<=rowno; i++)
            if ( SELECTED.equals( curr_state_blk.getItem(i).getType() ) ) count++;
         return count>=0 ? count+1 : 0;
      }
      else if(rowno<countRows())
         return rowno+1;
      else
         return 0;
   }

   /**
    * Return the string which contains DATA in the curr_state_blk buffer with column names.
    * This string is used to send data to a Spread-Sheet.
    */
   String dataString(ASPField[] fields)
   {
      AutoString out_str = new AutoString();

      // Collect column headings
      for( int i=0; i<fields.length; i++ )
         if( !fields[i].isHidden() )
            out_str.append(fields[i].getLabel(), "\t");
      out_str.append("\n");

      int row_count = curr_state_blk.countItems();
      if (row_count > 0)
      {
         // Data found
         for( int i=0; i<row_count; i++ )
         {
            // for each row
            Item item = curr_state_blk.getItem(i);
            if( DATA.equals(item.getName()) )
            {
               Buffer data = item.getBuffer();
               int col_count = data.countItems();
               for( int j=0; j<col_count; j++ )
               {
                  // for each column
                  if( !fields[j].isHidden() )
                  {
                     Item col_item = data.getItem(j);
                     String item_str = col_item.getString();
                     if(item_str!=null)
                     {
                        item_str = item_str.replace('\n',' '); // filter new line char
                        item_str = item_str.replace('\r',' '); // filter return char
                        out_str.append(item_str + "\t");
                     }
                     else
                        out_str.append("\t");
                  }
               }
            }
            out_str.append("\n");
         }
      }
      else
         out_str.append("No data found");

      //solution for the bug in Explorer 4.x
      if( getASPManager().getBrowserVersion().indexOf("MSIE 4.") > 0)
      while( out_str.length()<4500 )
         out_str.append("      \n");

      return out_str.toString();
   }

   private String getSSXMLHeader()
   {
      StringBuffer xmlheadbuf = new StringBuffer();
      xmlheadbuf.append("<?xml version='1.0'?>\n");
      xmlheadbuf.append("<Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet'\n");     
      xmlheadbuf.append("xmlns:o='urn:schemas-microsoft-com:office:office'\n");
      xmlheadbuf.append("xmlns:x='urn:schemas-microsoft-com:office:excel'\n");
      xmlheadbuf.append("xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet'\n");
      xmlheadbuf.append("xmlns:html='http://www.w3.org/TR/REC-html40'>\n");
      
      return xmlheadbuf.toString();
   }
       
   String getSSXMLDataSet_(ASPField[] fields, String separator, String channel)
                                          throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.xmlExcelDataSet()");
      String remove_group_sep = getASPManager().getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/REMOVE_DIGIT_GROUPING","N");
      AutoString out_str = new AutoString();
      //generated_fields is a list of generated visible columns - names
      generated_fields = getASPPage().getASPContext().readValue("__"+getBlock().getName()+"_VISIBLE");
      if(DEBUG) debug("  generated_fields="+generated_fields);
      
           
      String[] ordered_field_names = Str.split(generated_fields,",");     
      int[] col_pos = new int[ordered_field_names.length];
           
      int cnt=0;
     
      //fetch positions of fields in visible order.
      for (int i=0; i<ordered_field_names.length;i++)     
         for (int j=0; j<fields.length; j++ )         
            if (ordered_field_names[i].equals(fields[j].getName()))
            {                              
                fields[j].setUsedAsParameter(true);
                col_pos[cnt]=j;
                cnt++;                
            }                               
     
      //APPEND XML HEADER
      out_str.append( getSSXMLHeader() );         
      
      //APPEND XML STYLES      
      out_str.append(" <Styles>\n");
      out_str.append("  <Style ss:ID='sFontBold'>\n");
      out_str.append("   <Font ss:Bold='1'/>\n");
      out_str.append("  </Style>\n");
      out_str.append("  <Style ss:ID='sNumber'> <NumberFormat ss:Format='Standard'/></Style>\n");      
      out_str.append("  <Style ss:ID='sString'> <NumberFormat ss:Format='@'/></Style>\n");
      out_str.append(" </Styles>\n");
            
      //START WORKSHEET
      out_str.append(" <Worksheet ss:Name='IFS'>\n");
      out_str.append(" <Table>\n");
      
      out_str.append("  <Row ss:StyleID='sFontBold'>\n");
      
       //get column headings for visible columns in correct order      
       for( int i=0; i<col_pos.length; i++ )
       {
          ASPField fld = fields[col_pos[i]];

          if( fld.isUsedAsParameter() )
          {    
             out_str.append("   <Cell>");
             out_str.append("<Data ss:Type='String'> <![CDATA[" + fields[col_pos[i]].getLabel() + "]]></Data>", separator);
             out_str.append("</Cell>\n");
          }          
       }                     
       out_str.append("  </Row>\n");
       
       //Find and store the data type of the fields---------------------------
      String[] cell_types = new String[fields.length];
       for( int j=0; j<col_pos.length; j++ )
      {
         switch( fields[col_pos[j]].getTypeId() )
         {
            case DataFormatter.NUMBER:  
            case DataFormatter.INTEGER: 
            case DataFormatter.MONEY:   cell_types[col_pos[j]]="Number"; break;
            default: cell_types[col_pos[j]]="String";
         }
      }
      //---------------------------------------------------------------------
   
      int row_count = curr_state_blk.countItems();
      if (row_count > 0)
      {
         // Data found
         for( int i=0; i<row_count; i++ )
         {
            // for each row
            out_str.append("  <Row>\n");
            Item item = curr_state_blk.getItem(i);
            if( DATA.equals(item.getName()) )
            {
               Buffer data = item.getBuffer();
               for( int j=0; j<col_pos.length; j++ )
               {
                  // for each column
                  String val="";
                  if( fields[col_pos[j]].isUsedAsParameter() )
                  {
                     Item col_item = data.getItem(col_pos[j]);
                     String item_str = fields[col_pos[j]].convertToClientString(col_item.getString());                     
                     if(item_str!=null)
                     {
                        item_str = item_str.replace('\n',' '); // filter new line char
                        item_str = item_str.replace('\r',' '); // filter return char
                        item_str = item_str.replace('\t',' '); // filter tab char                       
                         
                        if (col_item.getType() == "N")
                        {
                           StringBuffer tempBuf = new StringBuffer(item_str); 
                           int fld_type = fields[col_pos[j]].getTypeId();
                           char group_sep = ' ';

                           if (fld_type == DataFormatter.NUMBER)                            
                              group_sep = getASPManager().getGroupSeparator(DataFormatter.NUMBER);
                           else if (fld_type == DataFormatter.MONEY)
                              group_sep = getASPManager().getGroupSeparator(DataFormatter.MONEY);

                           for (int n=0; n<tempBuf.length(); n++)
                           {
                              if (tempBuf.charAt(n)==group_sep)
                                 tempBuf.deleteCharAt(n);
                           }
                           item_str = tempBuf .toString();                         
                        } 
                        val = item_str + separator;
                     }
                     else                        
                        val = separator;
                     
                     //Trim the value and format it as non-parsed data
                     val = val.trim();                    
                     val = "<![CDATA[" + val + "]]>";   
                     
                     String cell_style = "s" + cell_types[col_pos[j]];                     
                     out_str.append("   <Cell");                     
                     out_str.append(" ss:StyleID='" + cell_style + "'>");                     
                     out_str.append("<Data ss:Type='" + cell_types[col_pos[j]] + "'>"+ val +"</Data>");
                     out_str.append("</Cell>\n");
                  }
               }
            }
            out_str.append("</Row>\n\n");
         }
      }
      else
         out_str.append("\n");         

            out_str.append(" </Table>\n");
         out_str.append("</Worksheet>\n");
      out_str.append("</Workbook>\n");

      return out_str.toString();
   }   

   String visibleString_(ASPField[] fields, String separator, String channel) throws FndException
   {
      if (DEBUG) debug("BufferedDataSet.dataString()");
      String remove_group_sep = getASPManager().getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/REMOVE_DIGIT_GROUPING","N");
      AutoString out_str = new AutoString();
      //generated_fields is a list of generated visible columns - names
      generated_fields = getASPPage().getASPContext().readValue("__"+getBlock().getName()+"_VISIBLE");
      if(DEBUG) debug("  generated_fields="+generated_fields);
      
           
      String[] ordered_field_names = Str.split(generated_fields,",");     
      int[] col_pos = new int[ordered_field_names.length];
           
      int cnt=0;
     
      //fetch positions of fields in visible order.
      for (int i=0; i<ordered_field_names.length;i++)     
         for (int j=0; j<fields.length; j++ )         
            if (ordered_field_names[i].equals(fields[j].getName()))
            {                              
                fields[j].setUsedAsParameter(true);
                col_pos[cnt]=j;
                cnt++;                
            }                               
     
       //get column headings for visible columns in correct order      
       for( int i=0; i<col_pos.length; i++ )
       {
          ASPField fld = fields[col_pos[i]];
          
          if( fld.isUsedAsParameter() )
          {
             out_str.append(fields[col_pos[i]].getLabel(), separator);
          }          
       }
      out_str.append("\n");

      int row_count = curr_state_blk.countItems();
      if (row_count > 0)
      {
         // Data found
         for( int i=0; i<row_count; i++ )
         {
            // for each row
            Item item = curr_state_blk.getItem(i);
            if( DATA.equals(item.getName()) )
            {
               Buffer data = item.getBuffer();
               //int col_count = data.countItems();
               for( int j=0; j<col_pos.length; j++ )
               //for( int j=0; j<col_count; j++ )
               {
                  // for each column
                  if( fields[col_pos[j]].isUsedAsParameter() )
                  {
                     Item col_item = data.getItem(col_pos[j]);
                     //String item_str = col_item.getString();
                     String item_str = fields[col_pos[j]].convertToClientString(col_item.getString());
                     if(item_str!=null)
                     {
                        item_str = item_str.replace('\n',' '); // filter new line char
                        item_str = item_str.replace('\r',' '); // filter return char
                        item_str = item_str.replace('\t',' '); // filter tab char

                        // A workaround for a limitation in Excel. (When a field value has opening double quote 
                        // as the first character without having a ending double quote, exported data is get concatenated .) 
                        if ((item_str!="") && (item_str.charAt(0) == '\"') && (item_str.lastIndexOf('\"') == 0))
                           item_str = item_str.substring(1);    

                        if ("Y".equals(remove_group_sep))
                        {    
                           if (col_item.getType() == "N")
                           {
                              StringBuffer tempBuf = new StringBuffer(item_str); 
                              int fld_type = fields[col_pos[j]].getTypeId();
                              char group_sep = ' ';
                              
                              if (fld_type == DataFormatter.NUMBER)                            
                                 group_sep = getASPManager().getGroupSeparator(DataFormatter.NUMBER);
                              else if (fld_type == DataFormatter.MONEY)
                                 group_sep = getASPManager().getGroupSeparator(DataFormatter.MONEY);
                           
                              for (int n=0; n<tempBuf.length(); n++)
                              {
                                 if (tempBuf.charAt(n)==group_sep)
                                    tempBuf.deleteCharAt(n);
                              }
                              item_str = tempBuf .toString();                         
                           } 
                        }
                        //out_str.append(item_str + "\t");
                        out_str.append(item_str + separator);
                     }
                     else
                        //out_str.append("\t");
                        out_str.append(separator);
                  }
               }
            }
            out_str.append("\n");
         }
      }
      else
         out_str.append("No data found");

      //solution for the bug in Explorer 4.x
      if( getASPManager().getBrowserVersion().indexOf("MSIE 4.") > 0)
      while( out_str.length()<4500 )
         out_str.append("      \n");
     
      return out_str.toString();
   }


   //==========================================================================
   // Debugging
   //==========================================================================

   private String[] listToArray( String list, String delimiters )
   {
      if( Str.isEmpty(list) ) return null;
      Vector v = new Vector();
      StringTokenizer st = new StringTokenizer(list,delimiters);
      while( st.hasMoreTokens() )
         v.addElement(st.nextToken());

      String[] arr = new String[v.size()];
      v.copyInto(arr);
      return arr;
   }

   /**
    * Write the rowset information to the trace output.
    */
   public void trace()
   {
      try
      {
         if( !isTraceOn() ) return;
         ASPBuffer buf = getASPManager().newASPBuffer(curr_state_blk);
         buf.traceBuffer("BufferedDataSet "+getBlock().getName());
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Get current values from the database and refresh the current row.
    * The row must already exist in the block.
    */
   public void refreshRow()
   {
      try
      {
         ASPManager mgr = getASPManager();
         ASPBlock blk = getBlock();
           
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPQuery q = trans.addEmptyQuery(blk);
         //q.addWhereCondition("OBJID = '"+ getValue("OBJID") + "'");
         q.addWhereCondition("OBJID = ?");
         q.addParameter("OBJID", getValue("OBJID"));

         q.setBufferSize(1);
         if(getASPPage() instanceof ASPPortletProvider)
            trans = ((ASPPortletProvider) getASPPage()).perform(trans);
         else
            trans = mgr.perform(trans);
         ASPBuffer buf = trans.getBuffer(blk.getName()+"/"+DATA);
         setRow(buf);
         Buffer b = buf.getBuffer();
         for(int i=0; i<b.countItems(); i++)
         {
            Item item = b.getItem(i);
            if("*".equals(item.getStatus()))
               item.setStatus(null);
         }
         db_state_blk.getItem(current_row_no).setValue((Buffer)buf.copy().getBuffer());
         setRowStatus( curr_state_blk.getItem(current_row_no), SET_ROW, null, false );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Get current values from the database and refresh all rows in the rowset.
    * Only existing rows will be updated.
    * @param only_selected boolean if true updates only the selected row(multirow)
    *                              else updates all rows in rowset, similar to refreshAllRows 
    * @see #refreshAllRows
    */
   public void refreshAllRows(boolean only_selected)
   {
      try
      {
         ASPManager mgr = getASPManager();
         ASPBlock blk = getBlock();
           
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPQuery q = trans.addEmptyQuery(blk);
         
         ASPBuffer keys = mgr.newASPBuffer();
         HashMap row_no_list = new HashMap();
         String value = "";
         
         if (blk.getASPRowSet().countRows()==0)
            return;

         for (int i=0; i<countRows(false); i++)
         {
            if ((only_selected && isRowSelected(i)) || !only_selected)
            {
               value = getValueAt(i,"OBJID");
               keys.addBuffer("ROW"+i).addItem("OBJID", value);
               row_no_list.put(value,""+i);
            }
         }
         q.addOrCondition(keys);

         if(getASPPage() instanceof ASPPortletProvider)
            trans = ((ASPPortletProvider) getASPPage()).perform(trans);
         else
            trans = mgr.perform(trans);
         
         ASPBuffer buf = trans.getBuffer(blk.getName());
         
         int row_no = 0;
         
         for (int i=0; i<buf.countItems()-1; i++)
         {
            ASPBuffer row_buf = buf.getBufferAt(i);
            // refresh the rows w.r.t sorting/selection
            row_no = Integer.parseInt(""+row_no_list.get(row_buf.getValue("OBJID")));
            setRow(row_buf, row_no);
            
            Buffer b = row_buf.getBuffer();
            
            for(int j=0; j<b.countItems(); j++)
            {
               Item item = b.getItem(j);
               if("*".equals(item.getStatus()))
                  item.setStatus(null);
            }
            
            db_state_blk.getItem(row_no).setValue((Buffer)row_buf.copy().getBuffer());
            setRowStatus( curr_state_blk.getItem(row_no), SET_ROW, null, false );
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
      
   AbstractDataRow getDataRow( int row_no )
   {
      return new BufferedDataRow(getRow(row_no),getASPPage());
   } 
      
   AbstractDataRowCollection getDataRows()   
   {
      return new BufferedDataRowCollection(getRows(),getASPPage());
   }

}

