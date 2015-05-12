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
 * File        : FndDataSet.java
 * 2010/04/26 buhilk - Bug 88982, Overloaded setValueAt() method to allow non-dirty values.
 * 2010/01/21 sumelk - Bug 87622, Added currentDbSet().
 * 2008/08/21 sumelk - Bug 75713, Added firstDbSet() and lastDbSet().
 * 2008/08/15 buhilk - Bug 76288, Changed getValueAt() to always return a String.
 * 2008/07/08 buhilk - Bug 74852, Changed scope of getDataRow(int) to public.
 * 2008/07/01 buhilk - Bug 74852, Completed most of the incomplete features
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.internal.FndAttributeInternals;
import ifs.fnd.record.*;
import ifs.fnd.record.FndAttribute.Iterator;
import ifs.fnd.record.FndSort.Direction;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import ifs.fnd.webfeature.*;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This will be used by ASPBlock to store populated data after 
 * fetching from the context, when the page is extended from 
 * FndWebFeature.
 * <p>
 * 
 */
public class FndDataSet extends ASPRowSet
{
   
   FndAbstractArray data_array;
   FndAbstractArray db_data_array;
   
   private static final String __CURRENT = "CURRENT";
   static final String  __FNDWEB_INTERNAL_TAG = "FNDWEB_INTERNAL_TAG";
   static final String  __FNDWEB_INTERNAL_TYPE = "FNDWEB_INTERNAL_TYPE";
   
   private FndDataRow first_selected_row;
   
   /** Creates a new instance of FndDataSet */
   public FndDataSet( ASPBlock block )
   {
      super(block);
      if (DEBUG) debug(this+" - constructor");
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
         debug(this+":FndDataSet::resetSubMembers()");
      data_array  = null;
      db_data_array = null;
   }  
   
   /**
    * Overrids the clone function in the super class.
    * @see ifs.fnd.asp.ASPPoolElement#clone
    */   
   protected void doActivate() throws FndException
   {
      ASPContextCache.ContextDataSet ctx_data_set = getASPPage().getASPContext().getCurrentContextDataSet(block_name);
      ASPContextCache.ContextDataSet  db_data_set = getASPPage().getASPContext().getDBContextDataSet(block_name);

      if (ctx_data_set!= null)
      {
         data_array = ctx_data_set.getData();
         if (DEBUG)
            debug(ctx_data_set.toString());          
      }
      
      if (db_data_set!= null)
      {
         db_data_array = db_data_set.getData();
         if (DEBUG)
            debug(db_data_set.toString());          
      }
      
      prev_curr_row = current_row_no;
      filter_enabled = false;
      readInfo(ctx_data_set);
   }
   
   /**
    * Overrids the clone function in the super class.
    * @see ifs.fnd.asp.ASPPoolElement#clone
    */
   protected ASPPoolElement clone( Object block ) throws FndException
   {
      if (DEBUG) ((ASPBlock)block).debug(this+": clone()");
      FndDataSet dataset = new FndDataSet((ASPBlock)block);
      dataset.block_name = this.block_name;
      dataset.setCloned();
      if (DEBUG) ((ASPBlock)block).debug(this+" cloned into "+dataset);
      return dataset;      
   }   

   // ====================================================================
   //           Abstract method implementation I (Allowed methods for FndWebFeatures)
   // ====================================================================

   /**
    * Callback function that will be called from ASPManager just before
    * generating of client script. Stores private variables in INFO buffer.
    */
   void prepareClientScript()
   {
      if (DEBUG) debug(this+": prepareClientScript()");
      writeInfo(getASPPage().getASPContext().getCurrentContextDataSet(block_name));
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
      if (data_array!= null)
      {
         int size = data_array.size();
         for (int i=0; i<size; i++)
         {
            FndAbstractRecord rec = FndAttributeInternals.internalGet(data_array, i);
            for (int j = 0; j < rec.getAttributeCount(); j++)
            {
               FndAttribute attr = rec.getAttribute(j);
               String item_str = attr.toString();
               if(item_str!=null)
               {
                  item_str = item_str.replace('\n',' '); // filter new line char
                  item_str = item_str.replace('\r',' '); // filter return char
                  out_str.append(item_str + "\t");
               }
               else
                  out_str.append("\t");               
            }
            out_str.append("\t");
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
   
   String visibleString_(ASPField[] fields, String separator, String channel) throws FndException 
   {
      if (DEBUG) debug("BufferedDataSet.dataString()");
      AutoString out_str = new AutoString();
      String remove_group_sep = getASPManager().getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/REMOVE_DIGIT_GROUPING","N");
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

      int row_count = data_array.size();
      if (row_count > 0)
      {
         // Data found
         for( int i=0; i<row_count; i++ )
         {
            FndAbstractRecord rec = FndAttributeInternals.internalGet(data_array, i);
            for( int j=0; j<col_pos.length; j++ )
            //for( int j=0; j<col_count; j++ )
            {
               // for each column
               if( fields[col_pos[j]].isUsedAsParameter() )
               {
                  FndAttribute attr = rec.getAttribute(col_pos[j]);
                  //String item_str = col_item.getString();
                  String item_str = fields[col_pos[j]].convertToClientString(attr.toString());
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
                        FndAttributeType attr_type = attr.getType();
                        if(attr_type == FndAttributeType.NUMBER || attr_type == FndAttributeType.DECIMAL || attr_type == FndAttributeType.NUMBER)
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
   
   // ====================================================================
   //     Abstract method implementation II (NOT Allowed methods for 
   //     FndWebFeatures). Just needed to implement to support legacy 
   //     pages through ASPRowSet. 
   // ====================================================================   

   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // protected ASPBuffer getRow( int row_no )
   public ASPBuffer getRow( int row_no )
   {
      notAllowedToUse();
      return null;
   }
   
   public FndAbstractRecord getCurrentRecord()
   {
      if (data_array != null)
         return FndAttributeInternals.internalGet(data_array,getCurrentRowNo());
      return null;
   }
   
   protected void initSelectRows()
   {
      if (initialized_selection) return;
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
               first_selected_row =  (FndDataRow)getDataRow(Integer.parseInt(selections[i]) );
         }
         initialized_selection = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   protected int countDirtyRows( int type, String method_id )
   {
      try
      {
         if (DEBUG) debug("FndDataSet.countDirtyRows("+type+","+method_id+")");
         if( data_array==null ) return 0;

         int cnt = 0;
         for (int i=0; i<data_array.size(); i++)
         {
            if ( isStatusOfType( getDataRow(i).getStatus(), type, method_id ) )
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
   
   // Public methods
   
   public ASPBuffer getRows()
   {
      notAllowedToUse();
      return null;
   }
   public ASPBuffer getRows( String field_names )
   {
      notAllowedToUse();
      return null;
   }
   
   public ASPBuffer getSelectedRows()
   {
      notAllowedToUse();
      return null;
   }
   
   public ASPBuffer getSelectedRows( String field_names )
   {
      notAllowedToUse();
      return null;
   } 
   
   public void setRow( ASPBuffer buf )
   {
      notAllowedToUse();
   }
   
   public int addRow( ASPBuffer buf )
   {
      notAllowedToUse();
      return 0;
   }

   
   

  //=======================================================================
  // Private Methods
  //=======================================================================
   
   private void notAllowedToUse()
   {
      error(new FndException("Can not be called from FndAbstractFetures."));
   }
   
   private void readInfo(ASPContextCache.ContextDataSet ctx_data_set)
   {
      if (ctx_data_set == null) return;
      current_row_no   = ctx_data_set.getCurrentRow();
      filter_enabled   = ctx_data_set.isFilterEnabled();
      sorted_column    = ctx_data_set.getSortedColumn();
      sorted_ascending = ctx_data_set.isSortedAssending();
      db_links_created = ctx_data_set.isDBLinksCreated();
   
   }
   
   void writeInfo()
   {
      writeInfo(getASPPage().getASPContext().getCurrentContextDataSet(block_name));
   }
  
   private void writeInfo(ASPContextCache.ContextDataSet ctx_data_set)
   {
      if (ctx_data_set == null) return;
      ctx_data_set.setCurrentRow(current_row_no);
      ctx_data_set.setFilterEnabled(filter_enabled);
      ctx_data_set.setSortedColumn(sorted_column);
      ctx_data_set.setSortedAssending(sorted_ascending);
      ctx_data_set.setDBLinksCreated(db_links_created);
   }
   
   private void debugText(String text)
   {
      debug(text);
   }

   private boolean isStatusOfType( String status, int type, String method_id )
   {
      if (DEBUG) debug("FndDataSet.isStatusOfType("+status+","+type+","+method_id+")");
      if ( Str.isEmpty(status) ) return false;
      if ( status.equals( NEW ) || status.equals( MODIFY ) || status.equals( REMOVE ) )
         return type==EDITED ? true : false;
      if ( type==COMMAND && ( method_id==null || method_id.equals( status ) ) )
         return true;
      return false;
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
   
   private boolean setRowStatus( FndDataRow row, int method, String param, boolean changed ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.setRowStatus("+row+","+method+","+param+","+changed+")");
      if ( NEW.equals( param ) )           throw invalidParamName(param);
      if ( MODIFY.equals( param ) )        throw invalidParamName(param);

      boolean remove_item = false;
      String state = row.getStatus();
      switch( method )
      {
         case ADD_ROW :
            row.setStatus( NEW );
            break;
         case SET_ROW    :
         case CHANGE_ROW :
            if ( NEW.equals(state) )
            {}
            else if ( state==null )
            {
               if( changed ) row.setStatus( MODIFY );
            }
            else if ( MODIFY.equals(state) )
            {
               if (!changed)
                  row.setStatus( null );
               else
                  ;
            }
            else if ( REMOVE.equals(state) )
            {}
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
            else if ( state==null )
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
            else if ( state==null )
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
   
   private void changeRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.changeRow("+row_no+")");
      boolean changed = getBlock().addToRow((FndDataRow)getDataRow(row_no), (FndDataRow)getDbRow(row_no), true);
      if( changed ) clearSortedColumn();
      setRowStatus((FndDataRow)getDataRow(row_no), CHANGE_ROW, null, changed);
   }
   
  //=======================================================================
  // protected methods
  //=======================================================================
   
   protected void resetRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.resetRow("+row_no+")"); 
      List dbrows = FndAttributeInternals.getInternalRecords(db_data_array);
      List rows   = FndAttributeInternals.getInternalRecords(data_array);
      rows.remove(row_no);
      rows.add(row_no, dbrows.get(row_no));
      FndDataRow dbrow = (FndDataRow)getDbRow(row_no);
      clearSortedColumn();
      setRowStatus(dbrow, SET_ROW, null, false);
   }  
   
   protected void markRow( String method_id, int row_no ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.markRow("+method_id+","+row_no+")");
      boolean changed = getBlock().addToRow((FndDataRow)getDataRow(row_no), (FndDataRow)getDbRow(row_no), false);
      setRowStatus((FndDataRow)getDataRow(row_no), MARK_ROW, method_id, changed);
   }
   
   protected int countRows( boolean only_selected )
   {
      try{
         
         if (DEBUG) 
            debug("FndDataSet.countRows("+only_selected+")");
         if( data_array == null ) return 0;
         if (!only_selected) return data_array.size();
         int cnt = 0;
         if (only_selected)
         {
            for(int i=0; i<data_array.size(); i++)
            {
               if(SELECTED.equals(getDataRow(i).getType()))
                  cnt++;
            }
         }
         return cnt;
      }catch(Throwable any){
         error(any);
         return -1;
      }
   }   
   
   protected void createLinksToDbState() throws FndException   
   {
      FndAbstractArray rows = data_array;
      int row_count = rows.size();
      for( int i=0; i<row_count; i++ )
         ((FndDataRow) getDataRow(i)).setCustomValue("__DB_ROW_NO",i+"");
      db_links_created = true;
   }
   
   // Modified by Terry 20130910
   // Change visibility
   // Orignal:
   // void selectRow( int row_no ) throws FndException
   public void selectRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.selectRow("+row_no+")");
      ((FndDataRow)getDataRow(row_no)).setType( SELECTED );
   }
   
   void unselectRow( int row_no ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.unselectRow("+row_no+")");
      ((FndDataRow)getDataRow(row_no)).setType( "" );
   }
   
  //=======================================================================
  // public methods
  //=======================================================================   
   
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
   public void changeRows()
   {
      //TODO     
   }
   
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
      //TODO
   }

   /**
    * Remove the current row from the row set without removing it from the database.
    */
   public void clearRow()
   {
      try
      {
         if (DEBUG) debug("FndDataSet.clearRow()");

         FndAttributeInternals.internalRemove(data_array, current_row_no);
         int db_row_no = getDbRowPosition(current_row_no);
         if(data_array!=db_data_array)
            FndAttributeInternals.internalRemove(db_data_array, db_row_no);
         if( db_links_created ) decreaseLinksToDbState(db_row_no);

         if ( current_row_no >= data_array.size() && current_row_no > 0 )
            current_row_no--;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }   
   
   /**
    * Decrease (by 1) every DbState-row reference that is greater or equal to
    * the specified number.
    */
   private void decreaseLinksToDbState( int from_db_row_no ) throws FndException
   {
      int row_count = data_array.size();
      for( int i=0; i<row_count; i++ )
      {
         FndDataRow row = (FndDataRow) getDataRow(i);
         String db_row = row.getCustomValue("__DB_ROW_NO");
         if(!Str.isEmpty(db_row))
         {
            int db_row_no = Integer.parseInt(db_row);
            if( db_row_no>=from_db_row_no )
               row.setCustomValue("__DB_ROW_NO", ""+(db_row_no-1) );
         }
      }
   }   
   
   /**
    * Mark specified fields (comma separated list of field names) as changed in the current row.
    * The row and the fields must already exist in the buffer.
    */
   
   public void setEdited( String fields )
   {
      //TODO
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
      if (DEBUG) debug("FndDataSet.getRowStatus("+row_no+")");
      FndDataRow row = (FndDataRow) getDataRow( row_no );
      return row==null ? null : row.getStatus();
   }
   
   public int unselectRows()
   {
      try
      {
         if (DEBUG) debug("FndDataSet.unselectRows()");

         int count = 0;
         for (int i=0; i<data_array.size(); i++)
         {
            FndDataRow row = (FndDataRow) getDataRow(i);
            row.setType("");
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
   
   public int markSelectedRows( String method_id )
   {
      try
      {
         if (DEBUG) debug("FndDataSet.markSelectedRows("+method_id+")");
         int count = 0;

         for (int i=0; i<data_array.size(); i++)
         {
            FndDataRow row = (FndDataRow) getDataRow(i);
            if (SELECTED.equals(row.getType()))
            {
               row.setStatus( method_id );
               row.setType("");
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
   
   /**
    * Removes all rows from this BufferedDataSet instance
    */
   public void clear()
   {
      try
      {
         if (DEBUG)
         {
            debug("FndDataSet.clear()");
            debug("\tdata_array="+data_array);
         }
         data_array.clear();
         if (DEBUG) debug("\tdata_array="+data_array);
         current_row_no = 0;
         ASPManager mgr = getASPManager();
         mgr.clearMyChilds(getBlock());
      }
      catch( Throwable any )
      {
         error(any);
      }
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
   public String getValueAt( int rowno, String name )   {
      FndAbstractRecord rec = FndAttributeInternals.internalGet(data_array,rowno);
      FndAttribute attr;
      if (name.indexOf(".")<0)
         attr = rec.getAttribute(name); 
      else
      {
         StringTokenizer token = new StringTokenizer(name,".");
         String reference = token.nextToken();
         FndAttribute temp = rec.getAttribute(reference);
         try
         {
            FndAbstractRecord aggregate = FndAttributeInternals.internalGetRecord((FndAbstractAggregate)rec.getAttribute(reference));
            attr = aggregate.getAttribute(token.nextToken());
         }catch(Exception e){
            return "";
         }      
      }
      return attr==null?null:attr.toString();
   }
   
   
   
   public FndAttribute getAttributeAt( int rowno, String name )   {
      FndAbstractRecord rec = FndAttributeInternals.internalGet(data_array,rowno);
      return rec.getAttribute(name);
   }   

   /**
    * Safe version of getValueAt(); does not error() - throws exception instead.
    */
   String getSafeValueAt( int rowno, String name ) throws FndException
   {
      try
      {
         return getValueAt(rowno,name);
      }
      catch( Throwable any )
      {
         throw new FndException("FNDNOSUCHITEM: There is no item '&1'.",name);
      }
   }   
   
   int translateRowNumber(int rowno)
   {
      if(filter_enabled)
      {
         int count = -1;
         for (int i=0; i<=rowno; i++)
            if ( SELECTED.equals( getDataRow(i).getType() ) ) count++;
         return count>=0 ? count+1 : 0;
      }
      else if(rowno<countRows())
         return rowno+1;
      else
      return 0;
   }
   
   public double getNumberValueAt( int rowno, String name )
   {
      try
      {
         FndAttribute item = getAttributeAt(rowno,name);
         FndAttributeType type = item.getType();
         if(type == null)
            return ASPManager.NOT_A_NUMBER;
         else if(type!=FndAttributeType.NUMBER && type!=FndAttributeType.DECIMAL && type!=FndAttributeType.INTEGER)
            throw new FndException("FNDROSNOTNUM: The item '&1' is not of the number type",name);

         String text = item.toString();
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
   
   protected void setValueAt(int rowno, String name, String value, String type, boolean make_dirty) throws FndException {
      //TODO
   }
   
   
  public int countDbRows()
  {
      try
      {
         return db_data_array==null ? 0 : db_data_array.size();
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }  
  }   
  
   public int moreRowsExist()
   {
     //TODO
     return 0;
   }   
  
   private int getDbRowPosition( int row_no )
   {
      if(DEBUG) debug("FndDataSet.getDbRowPosition("+row_no+"): db_links_created="+db_links_created);

      if( !db_links_created ) return row_no;
      FndDataRow curritem = (FndDataRow) getDataRow(row_no);
      if(DEBUG) debug("curritem="+curritem);
      String db_row_no = curritem.getCustomValue("__DB_ROW_NO");
      if(!Str.isEmpty(db_row_no))
         return Integer.parseInt(db_row_no);
      return row_no;
   }
   
   private AbstractDataRow getDbRow( int row_no )
   {
      if( db_data_array==null || row_no<0 ) return null;
      int db_row_no = getDbRowPosition(row_no);
      return new FndDataRow(FndAttributeInternals.internalGet(db_data_array, db_row_no));
   }   
   
   public String getDbValueAt( int rowno, String name )
   {
      try
      {
         FndDataRow item = (FndDataRow) getDbRow(rowno);
         if( item==null )
               throw new FndException("FNDROSNODBROW: Row number '&1' does not exist in the DB row set",rowno+"");
         FndAbstractRecord rec = item.rec;
         FndAttribute attr;
         if (name.indexOf(".")<0)
            attr = rec.getAttribute(name);
         else
         {
            StringTokenizer token = new StringTokenizer(name,".");
            String reference = token.nextToken();
            FndAttribute temp = rec.getAttribute(reference);
            FndAbstractRecord aggregate = FndAttributeInternals.internalGetRecord((FndAbstractAggregate)rec.getAttribute(reference));
            attr = aggregate.getAttribute(token.nextToken());
         }

         return attr==null?null:attr.toString();
      }
      catch(Throwable any)
      {
         error(any);
         return null;
      }
   }
   
   public int countSkippedDbRows()
   {
      //TODO
      return 0;
   }   
   
   public int getCurrentRowNo()
   {
      try
      {
         if (DEBUG) debug("FndDataSet.getCurrentRowNo()");
         if ( data_array.size()==0 ) return 0;
         if (DEBUG)
         {
            debug("  current_row_no="+current_row_no);
            debug("  filter_enabled="+filter_enabled);
         }
         if (!filter_enabled) return current_row_no;

         int count = -1;
         for (int i=0; i<=current_row_no; i++)
            if (SELECTED.equals( getDataRow(i).getType())) count++;
         if (DEBUG) debug("  count="+count);
         return count>=0 ? count : 0;
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }   
   
   public void nextDbSet()
   {
      //TODO
      return;
   }   
   
   public void prevDbSet()
   {
      //TODO
      return;
   }   
   
   public void firstDbSet()
   {
      //TODO
      return;
   }   

   public void lastDbSet()
   {
      //TODO
      return;
   }  

   void currentDbSet()
   {
       //TODO
       return;
   }

   void saveQuery()
   {
      //TODO
   }
   
   public void sort( String key_name, boolean ascending )
   {
      try
      {
         if (getASPPage().hasASPTable(getBlock().getName()))
         {
            storeSelections();
            if(DEBUG) debug("Current state buffer for "+this+":\n"+getASPManager().printAbstractArray(data_array,0));
            //data_array.sortOnAttribute(getBlock().getASPField(key_name).getTemplate(), ascending);
            String attr_name = FndSort.formatOrderByClause(getBlock().getDataAdapter().getTemplate(), new FndSortField[] {getBlock().getASPField(key_name).getTemplate().descending()});
            if(ascending)
               attr_name = attr_name.replace("DESC","");
            data_array.sort(attr_name);
         }
      }
      catch(Throwable e)
      {
         error(e);
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
         ASPField fields[] = blk.getFields();
         FndDataAdapter adapter = blk.getDataAdapter();
         FndAbstractRecord record = adapter.getTemplate();
         FndCompoundReference keyRefs = record.getPrimaryKey();
         FndAttribute.Iterator itr = keyRefs.iterator();
         ASPField keyFields[] = new ASPField[keyRefs.getAttributeCount()];
         int counter = 0;
         
         while(itr.hasNext()){
            FndAttribute key = itr.next();
            for(int f=0; f<fields.length; f++){
               if(fields[f].getDbName().equals(key.getName()))
               {
                  keyFields[counter++] = fields[f];
                  break;
               }
            }
         }
         
         // Create search condition according to the primary key fields.
         FndCondition final_cond = null;
         HashMap row_nos = new HashMap();
         boolean _or = false;
         
         for (int i=0; i<countRows(false); i++)
         {
            if ((only_selected && isRowSelected(i)) || !only_selected)
            {
               String keyName = "";
               FndCondition simple_cond = null;
               for(int k=0; k<keyFields.length; k++)
               {
                  String value = getValueAt(i,keyFields[k].getName());
                  keyName += value;
                  if(simple_cond==null)
                     simple_cond = ActivityUtils.addAttributeCondition(keyFields[k],value,true);
                  else
                     simple_cond.and(ActivityUtils.addAttributeCondition(keyFields[k],value,true));
               }
               final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
               row_nos.put(keyName, ""+i);
            }
            _or = true;
         }
         
         //Add search condition to record
         record.addCondition(final_cond);
         
         if(adapter instanceof FndQueryDataAdapter){
            FndAbstractArray array = ((FndQueryDataAdapter)adapter).query(new FndQueryRecord(record), mgr.getASPPage());;
            for(int a=0; a<array.getLength(); a++)
            {
               String keyName = "";
               int rowno = -1;
               record = FndAttributeInternals.internalGet(array,a);
               for(int k=0; k<keyFields.length; k++)
               {
                  String value = record.getAttribute(keyFields[k].getTemplate()).toString();
                  keyName += value;
               }
               rowno = Integer.parseInt(""+row_nos.get(keyName));
               mgr.getASPContext().mergeFndAbstractRecordToArray(record, blk.getName(), rowno);
            }
         }
         else
            throw new FndException("FNDDATASETNOQUERYADAPTERERR: No FndQueryDataAdapter implemented.");
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
         ASPField fields[] = blk.getFields();
         FndDataAdapter adapter = blk.getDataAdapter();
         FndAbstractRecord record = adapter.getTemplate();
         
         // Add search condition according to the primary key fields.
         FndAttribute.Iterator itr = record.getPrimaryKey().iterator();
         while(itr.hasNext()){
            FndAttribute key = itr.next();
            for(int f=0; f<fields.length; f++){
               if(fields[f].getDbName().equals(key.getName()))
               {
                  record.addCondition(ActivityUtils.addAttributeCondition(fields[f],getValue(fields[f].getName()),true));
                  break;
               }
            }
         }
         
         if(adapter instanceof FndQueryDataAdapter){
            FndAbstractArray array = ((FndQueryDataAdapter)adapter).query(new FndQueryRecord(record), mgr.getASPPage());;
            if(array!=null && array.getLength()>0)
            {
               record = FndAttributeInternals.internalGet(array,0);
               mgr.getASPContext().mergeFndAbstractRecordToArray(record, blk.getName(), current_row_no);
            }
         }
         else
            throw new FndException("FNDDATASETNOQUERYADAPTERERR: No FndQueryDataAdapter implemented.");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   //==========================================================================
   // Navigating
   //==========================================================================
   public boolean first()
   {
      try{
         if (DEBUG){
            debug("FndDataSet.first()");
            debug("  [first:old]current_row_no="+current_row_no);
         }
         int max_row_no = data_array.getLength() - 1;
         if (DEBUG) debug("  max_row_no="+max_row_no);
         current_row_no = 0;

         if (DEBUG) debug("  filter_enabled="+filter_enabled);
         if (max_row_no>=0 && filter_enabled)
            while (!SELECTED.equals(((FndDataRow)getDataRow(current_row_no)).getType())){
               current_row_no++;
               if (current_row_no>max_row_no){
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
   
   public boolean forward( int count )
   {
      try
      {
         if (DEBUG)
         {
            debug("FndDataSet.forward("+count+")");
            debug("  [forward:old]current_row_no="+current_row_no);
         }
         int max_row_no = data_array.getLength() - 1;
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
               if ( SELECTED.equals( ((FndDataRow)getDataRow(current_row_no)).getType() ) ) count--;
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
   
   public boolean last()
   {
      try
      {
         if (DEBUG)
         {
            debug("FndDataSet.last()");
            debug("  [last:old]current_row_no="+current_row_no);
         }
         int max_row_no = data_array.getLength()-1;
         if (DEBUG) debug("  max_row_no="+max_row_no);
         current_row_no = max_row_no;

         if (DEBUG) debug("  filter_enabled="+filter_enabled);
         if (max_row_no>=0 && filter_enabled)
            while ( !SELECTED.equals( ((FndDataRow)getDataRow(current_row_no)).getType() ) )
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
   
   public boolean backward( int count )
   {
      try
      {
         if (DEBUG)
         {
            debug("FndDataSet.backward("+count+")");
            debug("  [backward:old]current_row_no="+current_row_no);
         }
         int max_row_no = data_array.getLength()-1;
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
               if ( SELECTED.equals( ((FndDataRow)getDataRow(current_row_no)).getType() ) ) count--;
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
   
   void duplicatePressed3()
   {
      if (DEBUG) debug("FndDataSet.duplicatePressed3()"+current_row_no);
      duplicate_pressed_flag = true;
      first_selected_row = (FndDataRow)getDataRow(current_row_no);
   }    
   
   //=========================================================================
   // Debug
   //=========================================================================
   
   /**
    * Write the rowset information to the trace output.
    */
   public void trace()
   {
      try
      {
         if( !isTraceOn() ) return;
         ASPManager mgr = getASPManager();
         mgr.getASPLog().trace(mgr.printAbstractArray(data_array, 0));
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Modified by Terry 20130910
   // Change visibility
   // Original:
   // protected boolean isRowSelected( int row_no ) throws FndException
   public boolean isRowSelected( int row_no ) throws FndException
   {
      if (DEBUG) debug("FndDataSet.isRowSelected("+row_no+")");
      FndDataRow item = (FndDataRow)getDataRow( row_no );
      return SELECTED.equals( item.getType() ) ? true : false;

   }

   //===========================================================================
   //   Implementation for methods which return DataRows
   //===========================================================================
   
   public AbstractDataRow getDataRow( int row_no )
   {
      return new FndDataRow(FndAttributeInternals.internalGet(data_array, row_no));
   } 
   
   AbstractDataRowCollection getDataRows()   {
      return new FndDataRowCollection(data_array);
   }
//   
//   AbstractDataRow getDataRows( String field_names )   {
//      //todo
//      return new FndDataRow();
//   }
//   
//   AbstractDataRow getSelectedDataRows()   {
//      //todo
//      return new FndDataRow();
//   }
//   
//   AbstractDataRow getSelectedDataRows( String field_names )   {
//      //todo
//      return new FndDataRow();
//   }


   // Added by Terry 20120923
   // Override super(ASPRowSet method), not allow to use in this class.
   public int addRowNoStatus(ASPBuffer buf)
   {
      notAllowedToUse();
      return 0;
   }
   // Added end

   // Added by Terry 20121120
   // Override super(ASPRowSet method)
   public void gotoPage(int page)
   {
      return;
   }
   // Added end
   
   // Added by Terry 20130318
   // Resets all rows of the current row in the Current State from the Db State.
   public void resetAllRows( boolean only_selected ) throws FndException
   {
      return;
   }
   
   // Remove all rows of the current row in the Current State with status
   public void removeAllRows(String status) throws FndException
   {
      return;
   }
   // Added end

}
