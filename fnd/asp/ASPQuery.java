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
 * File        : ASPQuery.java
 * Description : An ASPObject that represents a Query in a Request Buffer
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-May-13 - Created
 *    Marek D  1998-Jun-25 - Added defineEmpty() method
 *    Marek D  1998-Jun-26 - Added addOrCondition()
 *    Marek D  1998-Jul-13 - Added method define(sql_text)
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-10 - Static function countRows() changed to be
 *                           an instance function
 *    Marek D  1998-Aug-12 - '%' and '_' in NUMBER and DATE fields
 *                           Use default buffer size from Config.ifm
 *                           Use SQLTokenizer in addWhereCondition()
 *                           Throw ASPField.parseException() on failed parsing
 *    Marek D  1998-Aug-20 - Skip values containing only white-spaces
 *                           in generation of standard WHERE condition
 *    Marek D  1998-Sep-25 - Added method setCountDbRows(boolean): #2722
 *    Marek D  1998-Oct-01 - Get default COUNT_DB_ROWS from ASPConfig.ifm
 *    Jacek P  1998-Oct-21 - Added call to Util.trimLine() in
 *                           createBlockCondition() (Bug: #2816).
 *    Jacek P  1998-Oct-28 - Skip fields marked as not queryable in function
 *                           createBlockCondition() (Bug:#2848).
 *    Marek D  1998-Dec-04 - Use add_months() instead of to_date() in
 *                           generation if standard query (Bug #2992)
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Mar-19 - Removed calls to getConfigParameter()
 *    Marek D  1999-Jun-03 - Removed special logic for RadioButtons from 
 *                           createBlockCondition()
 *                           Added ASPField.searchOnDbColumn() logic
 *    Johan S  1999-Sep-22   Added futher parser/query functionality in
 *                           createBlockCondition and splitIntoOperatorAndValue.
 *    Johan S  1999-Okt-12   Removed Case-insenstive that was introduced by me last time
 *    Jacek P  2000-Jan-21 - Call to ASPManager.readValue() replaced with ASPPage.readValue()
 *                           in function createBlockCondition(). Needed for portal
 *                           implementation.
 *    Reine A  2000-Apr-13 - Added new function setGroupByClause().
 *    Kingsly P 2001-Apr-27 - Added more condition for caseinsens at createBlockCondition().
 *    Suneth M 2001-Sep-12 - Log id #797 :Added more condition to createBlockCondition().
 *    Piotr Z  2001-Sep-12 - Modified createBlockCondition() - global connection is always set.
 *    Suneth M 2002-Aug-13 - Added more conditions to createBlockCondition().
 *    Jacek P  2002-Sep-24 - Added clear() method and case mode constants.
 *                           Call to mainbuf.clear() replaced with the new clear();
 *    Suneth M 2003-Dec-09 - Bug 40924, Changed splitIntoOperatorAndValue().
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/04/20 amiklk Bug id 89905, Modified createBlockCondition() to support date values with SYSDATE
 * 2009/11/16 sumelk Bug 86435, Changed createBlockCondition() to create the where clause correctly for STATE field.
 * 2009/10/08 amiklk Bug id 85955, Modified createBlockCondition() to use ifs.fnd.buffer classes for date intervals
 * 2008/09/23 sadhlk Bug id 77240, Modified createBlockCondition() and splitIntoOperatorAndValue() to provide
 *                                 functionality to query Not Like condition (!=%).
 * 2008/09/09 rahelk Bug id 76831, Reverted to fndext counting the rows if VIEW not explicitely specified in query.
 * 2008/08/11 rahelk Bug id 74484, Set N to request buffer countDbRows always so fndext wont retrieve all rows.
 * 2007/07/01 sumelk Bug 74932, Changed createBlockCondition() to fix the limitation with date intervals. 
 * 2007/04/17 buhilk Bug id 64311, Modified createBlockCondition() to find values from IID Lov windows
 * 2007/03/05 rahelk Bug id 63999, checked if VIEW is in available_view list.
 * 2007/03/05 rahelk Bug id 63948, Avoided adding TEXT_ID$ constraint for detail blocks.
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 *               2006/12/21           sumelk
 * Bug 62620, Moved methods toSqlDateMask() & correctSingleCharFormat() to ASPManager.
 *
 *               2006/05/17 gegulk
 * Bug 57581, Modified createBlockCondition() to change the where clause if it 
 * contains STATE and the state_encode_method is available for the specific LU, 
 * STATE will be replaced by OBJSTATE in such cases.
 *
 *               2005/12/14 07:52:15  mapelk
 * call id 129882: Modified toSqlDateMask() 
 *
 * Revision 1.4  2005/11/11 07:52:15  rahelk
 * call id 127686. Added empty DATA buffer
 *
 * Revision 1.3  2005/10/21 03:42:01  mapelk
 * Project link call 127332: Can not search for data starts with !
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
 * A subclass of ASPCommand that represents a query. It may be defined using
 * SQL syntax, or it can be created automatically basing on an ASPBlock.
 * The result of a query is an ASPBuffer containing database records or rows.
 * Each row is returned as a sub-buffer named "DATA".
 * The result may contain an extra sub-buffer named "INFO" located after the
 * last DATA item.
 */
public class ASPQuery extends ASPCommand
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPQuery");

   protected final static String DATA   = "DATA";
   protected final static String METHOD = "METHOD";
   protected final static String VIEW   = "VIEW";
   protected final static String SELECT = "SELECT";
   protected final static String WHERE  = "WHERE";
   protected final static String ORDER  = "ORDER";
   protected final static String GROUP  = "GROUP";

   private final static int USERDEF     = 0;
   private final static int SENSITIVE   = 1;
   private final static int INSENSITIVE = 2;
   
   private int case_mode = USERDEF;

   
   ASPQuery( ASPManager manager )
   {
      super(manager);
   }

   /*
   protected ASPCommand construct()
   {
      return this;
   }*/

   /**
    * Clear this instance of the ASPQuery class. The same instance can be then resused
    * during subsequent calls. Note however that the same instance cannot be used more
    * then once during creation of one transaction buffer containing several command/query
    * definitions - each command/query has to have it's own instance of this class.
    */
   public void clear()
   {
      super.clear();
      case_mode = USERDEF;
   }

   //==========================================================================
   //
   //==========================================================================

   /**
    * Define this ASPQuery using SQL expressions. All specified arguments
    * must have valid SQL syntax.
    */
   public void define( String view_name,
                       String select_list,
                       String where_condition,
                       String order_by )
   {
      try
      {
         clear();
         mainbuf.addItem( METHOD, "Query" );
         mainbuf.addItem( VIEW,   view_name );
         mainbuf.addItem( SELECT, select_list );
         mainbuf.addItem( WHERE,  where_condition );
         mainbuf.addItem( ORDER,  order_by  );
         mainbuf.addItem( DATA,   mainbuf.newInstance() );
         setDefaultOptions();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPQuery using the specified ASPBlock.
    * Values for all fields will be fetched. The returned rows will be
    * selected using restrictions build on every field that has a non empty
    * value in the Request object in the following order:
    *<pre>
    *   1. QueryString (URL)
    *   2. HTML Form
    *</pre>
    */
   public void define( String view_name,
                       ASPBlock block )
   {
      try
      {
         clear();
         mainbuf.addItem( METHOD, "Query");
         mainbuf.addItem( VIEW,   view_name );
         mainbuf.addItem( SELECT, block.getSelectList() );
         setDefaultOptions();
         
         // Modified by Terry 20130918
         // Advanced Query
         // Original:
         // createBlockCondition(block);
         // Where
         String blk_advanced_query_where = getASPManager().readValue("__" + block.getName() + "_ADVANCED_QUERY_STRING");
         if (!Str.isEmpty(blk_advanced_query_where))
         {
            mainbuf.addItem( WHERE, blk_advanced_query_where );
            mainbuf.addItem( DATA, mainbuf.newInstance() );
         }
         else
            createBlockCondition(block);
         
         // Order By
         String blk_advanced_query_order = getASPManager().readValue("__" + block.getName() + "_ADVANCED_QUERY_ORDER");
         if (!Str.isEmpty(blk_advanced_query_order))
            setOrderByClause(blk_advanced_query_order);
         // Modified end
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPQuery using the specified ASPBlock,
    * without any WHERE condition.
    */
   public void defineEmpty( String view_name,
                            ASPBlock block )
   {
      try
      {
         clear();
         mainbuf.addItem( METHOD, "Query");
         mainbuf.addItem( VIEW,   view_name );
         mainbuf.addItem( SELECT, block.getSelectList() );
         mainbuf.addItem( WHERE,  "" );
         mainbuf.addItem( DATA,   mainbuf.newInstance() );
         setDefaultOptions();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPQuery using the specified SELECT statement, which must
    * have valid SQL syntax.
    */
   public void define( String sql_text )
   {
      try
      {
         clear();
         mainbuf.addItem( METHOD, "Query");
         mainbuf.addItem( SELECT, sql_text );
         mainbuf.addItem( DATA,   mainbuf.newInstance() );
         setDefaultOptions();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   protected void setDefaultOptions()
   {
      ASPManager mgr = getASPManager();
      // Modified by Terry 20120822
      // Allow user input page size to set buffer size property.
      // Original: setBufferSize(mgr.getASPConfig().getDefaultBufferSize());
      int buf_size = Integer.parseInt(mgr.readValue("__PAGESIZE", "-1"));
      if (buf_size == -1)
         buf_size = mgr.getASPConfig().getDefaultBufferSize();
      setBufferSize(buf_size);
      // Modified end
      setCountDbRows(mgr.getASPConfig().countDbRows());
   }

   
   private String convertSearchCriteria(String searchString)
   {
      String criteria = searchString;
      
      try
      {
         ApplicationSearchManager searchManager = ApplicationSearchManager.getInstance(getASPManager().getASPConfig());
         criteria = searchManager.convertSearchCriteria(criteria, getASPManager().getASPConfig());
      }
      catch (Exception any)
      {
         error(any);
      }
      
      return criteria;
   }
   
   private void appendApplicationSearchFields(AutoString where, Buffer bind)
   {
      String[][] applicationSeachFields = getASPManager().getApplicationSearchFields();

      for (int i=0; i<applicationSeachFields.length; i++)
      {
         if( where.length()>0 )
            where.append("\n and ");

         String name  = applicationSeachFields[i][0];
         String value = applicationSeachFields[i][1];
         
         where.append(name+"=?");
         
         Item item = new Item(name);
         item.setValue(value);
         item.setStatus("IN");
         bind.addItem(item);
         
         /*
         //TO-DO try with bind variables
         if (!"S".equals(applicationSeachFields[i][2]))
            where.append(name+"="+value); // primarykey is a NUMBER
         else
            where.append(name+"='"+value+"'");
          */
      }
   }
   

   /**
    * Append to this ASPQuery a WHERE condition based on the specified
    * SQL text. The text may include references to ASPFields defined
    * in the current ASP page, for example
    *<pre>
    *    "COMPANY_ID = &MASTER_COMPANY_ID and DUE_DATE <= :FROM_DATE"
    *</pre>
    * where COMPANY_ID and DUE_DATE are database column names,
    * and MASTER_COMPANY_ID and FROM_DATE are names of ASPFields.
    *<p>
    * The value of each ":"-reference will be fetched from the HTTP request and then
    * converted to the server format using the field specific type and format mask.
    * It is analogous to the ASPCommand.addParameter() method.
    *<p>
    * The type and value of each "&"-reference will be fetched from the
    * response buffer, by the transaction server, just before the execution of this command.
    * It is analogous to the ASPCommand.addReference() method.
    */
   public void addWhereCondition( String sql_text )
   {
      if( Str.isEmpty(sql_text) ) return;

      ASPManager mgr = getASPManager();
      ServerFormatter srvfmt = mgr.getServerFormatter();

      try
      {
         String where = mainbuf.getString(WHERE,null);
         Buffer bind  = mainbuf.getBuffer(DATA,null);
         
         if( where==null || bind==null )
            throw new FndException("FNDNOQRYCOND: This ASPQuery cannot have extra WHERE conditions");

         AutoString stmt = new AutoString();
         stmt.append(where);
         if( stmt.length()>0 ) stmt.append("\n   and ");
         stmt.append("(");

         SQLTokenizer st = new SQLTokenizer(sql_text);

         //debug("Parsing: "+sql_text);
         while(true)
         {
            int tt = st.nextToken();

            //debug("..."+st);
            switch(tt)
            {
               case StreamTokenizer.TT_EOF:
                  mainbuf.setItem(WHERE,stmt.toString()+")");
                  return;

               case ':':
               case '&':
                  int reftype = tt;
                  st.mark();
                  st.nextNonSpaceToken();
                  String name = st.getValue();
                  if( IfsNames.isId(name) )
                  {
                     stmt.append("?");

                     ASPField field = mgr.getASPField(name);
                     String dbname = field.getDbName();
                     String value;
                     DataFormatter fmt;

                     if( reftype==':' )
                     {
                        fmt = field.getDataFormatter();
                        value = field.hasGlobalConnection() ?
                                field.getGlobalValue() :
                                field.readValue();
                     }
                     else
                     {
                        fmt = mgr.getDataFormatter(DataFormatter.STRING,null);
                        String bufname = field.getBlock().getName();
                        value = "&RESPONSE/" + bufname + "/DATA/" + dbname;
                     }


                     Item item;
                     try
                     {
                        item = ASPBuffer.convertToServerItem(dbname, value, srvfmt, fmt, null);
                     }
                     catch( Throwable any )
                     {
                        throw field.parseException(value,any);
                     }   
                     item.setStatus("IN");
                     bind.addItem(item);
                  }
                  else
                  {
                     st.reset();
                     stmt.append((char)tt);
                  }
                  break;

               case '\'':
                  stmt.append(st.getValue());
                  break;

               default:
                  if( tt>0 )
                     stmt.append((char)tt);
                  else
                     stmt.append(st.getValue());
            }
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Append to this ASPQuery a WHERE condition based on the specified ASPBuffer,
    * created by a call to ASPRowSet.getSelectedRows() or equivalent.
    *
    * @see ifs.fnd.asp.ASPRowSet.getSelectedRows
    */
   public void addOrCondition( ASPBuffer keys )
   {
      try
      {
         if( keys==null || keys.countItems()==0 ) return;

         String where = mainbuf.getString(WHERE);
         Buffer bind  = mainbuf.getBuffer(DATA);
         Buffer rows  = keys.getBuffer();

         AutoString stmt = new AutoString();
         stmt.append(where);
         if( stmt.length()>0 ) stmt.append("\n   and ");
         stmt.append("(");

         Buffer firstrow = rows.getBuffer(0);
         int key_count = firstrow.countItems();
         for( int k=0; k<key_count; k++ )
         {
            if( k>0 ) stmt.append(',');
            stmt.append(firstrow.getItem(k).getName());
         }

         stmt.append(") in (");

         int row_count = rows.countItems();
         for( int r=0; r<row_count; r++ )
         {
            Buffer row = rows.getBuffer(r);
            if( r>0 ) stmt.append(',');
            stmt.append('(');
            for( int k=0; k<key_count; k++ )
            {
               if( k>0 ) stmt.append(',');
               stmt.append('?');

               Item item = (Item)row.getItem(k).clone();
               item.setStatus("IN");
               bind.addItem(item);
            }
            stmt.append(')');
         }

         mainbuf.setItem(WHERE,stmt.toString()+")");
      }
      catch(Throwable e)
      {
         error(e);
      }
   }



   /**
    * Replace the select-list of this ASPQuery with the specified SQL text.
    * Note that the name of each column returned from the query will become
    * the item name in the result buffer.
    */
   public void setSelectList( String sql_text )
   {
      try
      {
         mainbuf.setItem( SELECT, sql_text );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Replace the specified name in the select-list of this ASPQuery
    * with the specified SQL text.
    */
   public void setSelectExpression( String name, String sql_text )
   {
      try
      {
         String select = mainbuf.getString(SELECT);
         select = Str.replace(select,name,sql_text+" "+name);
         mainbuf.setItem( SELECT, select );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Append to this ASPQuery (datail) a connection to another ASPQuery (master).
    * The type and value of the detail field will be fetched from the
    * master field in the response buffer, by the transaction server,
    * just before the execution of this command.
    */
   public void addMasterConnection( String master_command_name,
                                    String master_field_name,
                                    String detail_field_name )
   {
      try
      {
         ASPManager mgr = getASPManager();
         ServerFormatter srvfmt = mgr.getServerFormatter();

         String where = mainbuf.getString(WHERE);
         Buffer bind  = mainbuf.getBuffer(DATA);

         ASPField master_field = mgr.getASPField(master_field_name);
         ASPField detail_field = mgr.getASPField(detail_field_name);
         String detail_db_name = detail_field.getDbName();

         where = Str.isEmpty(where) ? "" : where + "\n   and ";
         where = where + detail_db_name+" = ?";
         mainbuf.setItem(WHERE,where);

         DataFormatter fmt = mgr.getDataFormatter(DataFormatter.STRING,null);
         String value = "&RESPONSE/" + master_command_name +
                        "/DATA/" + master_field.getDbName();

         Item item = ASPBuffer.convertToServerItem(detail_db_name, value, srvfmt, fmt, null);
         item.setStatus("IN");
         bind.addItem(item);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Append the specified SQL text to the select-list of this ASPQuery.
    * Note that the name of each column returned from the query will become
    * the item name in the result buffer.
    */
   public void addSelectExpression( String sql_text )
   {
      try
      {
         String select = mainbuf.getString(SELECT);
         mainbuf.setItem( SELECT, select+", "+sql_text );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Set the GROUP BY clause of this ASPQuery to the specified SQL text.
    */
   public void setGroupByClause( String sql_text )
   {
      try
      {
         mainbuf.setItem( GROUP, sql_text );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }  

   /**
    * Set the ORDER BY clause of this ASPQuery to the specified SQL text.
    */
   public void setOrderByClause( String sql_text )
   {
      try
      {
         // Modified by Terry 20131009
         // Always apply first order_by clause
         // Original:
         // mainbuf.setItem( ORDER, sql_text );
         if (Str.isEmpty(mainbuf.getString(ORDER, null)))
            mainbuf.setItem( ORDER, sql_text );
         // Modified end
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   // Added by Terry 20130926
   // Add the ORDER BY clause of this ASPQuery to the specified SQL text.
   public void addOrderByClause( String sql_text )
   {
      try
      {
         String order_by = mainbuf.getString(ORDER, null);
         if (!Str.isEmpty(order_by))
            mainbuf.setItem( ORDER, order_by + ", " + sql_text );
         else
            setOrderByClause(sql_text);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end
   
   // Added by Terry 20131009
   // Set the ORDER BY clause of this ASPQuery compulsively.
   public void setOrderByClauseCompulsively( String sql_text )
   {
      try
      {
         mainbuf.setItem( ORDER, sql_text );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   // Added end

   /**
    * Create two new Items in the mainbuf of this ASPQuery
    * based on all non empty fields in the specified block:
    *   WHERE - String: " ... ? ... ? ... ? ... " (SQL syntax)
    *   BIND  - Buffer: with one bind variable per "?"-placeholder
    */
   private void createBlockCondition( ASPBlock block ) throws Exception
   {
      if(DEBUG)
      {
         debug("ASPQuery.createBlockCondition('"+block.getName()+"')");
         debug("  State of current block is '"+block.getStateName()+"'");
      }
      
      ASPManager mgr = getASPManager();
      // Added by Terry 20140623
      // Get view param fields from CONTEXT
      String __view_param_fields = block.getASPPage().getASPContext().readValue("__VIEW_PARAM_FIELDS");
      // Added end
      DataFormatter strformatter = mgr.getDataFormatter(DataFormatter.STRING,"");
      ServerFormatter srvfmt = mgr.getServerFormatter();
      ASPPage page = block.getASPPage();
      
      boolean caseinsens=false;
      if(case_mode==SENSITIVE)
          caseinsens=false;
      else if (case_mode==INSENSITIVE)
          caseinsens=true;
      else
      {
          if(page.readValue("__CASESS","").equals("1"))
              caseinsens = false;
          else if( block.getASPBlockLayout().wasFindLayout() || block.getASPBlockLayout().isFindLayout())
              caseinsens = true;
          else if("FALSE".equals(mgr.getQueryStringValue("CASESENCETIVE")))
              caseinsens = true;
          else if(("N").equals(page.readValue("__CASESS_VALUE","")))
              caseinsens = true;
      }
      
      // Added by Terry 20131118
      // Check accurate find
      boolean accurate_find = false;
      if (getASPManager().commandBarActivated())
      {
         if("TRUE".equals(getASPManager().readValue("__ACCURATE_FIND", "FALSE")))
            accurate_find = true;
      }
      else
         accurate_find = true;
      // Added end

      AutoString where = new AutoString();
      Buffer bind = mainbuf.newInstance();
      
      // Added by Terry 20130926
      // Sort in Query
      String order_by = "";
      // Added end
      
      if(DEBUG) debug("ASPQuery: isApplicationSearch="+mgr.isApplicationSearch());
      
      //Append where condition to auto populate result page
      if (mgr.isApplicationSearch())
      {
         appendApplicationSearchFields(where, bind);
         mainbuf.addItem( WHERE, where.toString());
         mainbuf.addItem( DATA, bind);
         
         return;
      }
      
      
      ASPField[] fields = block.getFields();
      if(DEBUG) debug("ASPQuery: number of fields="+(fields==null?"null":""+fields.length));
      for( int i=0; i<fields.length; i++ )     //För varje fält:
      {
         ASPField field = fields[i];
         if(DEBUG) debug("ASPQuery: parsing field '"+(field==null?"null":field.getName())+"'");
         boolean  gobal_con = field.hasGlobalConnection();
         if ( !field.isQueryable() && !gobal_con) continue;
         String name = field.getName();
         String dbname = field.getDbName();

         String value;
         if (gobal_con)
            value = field.getGlobalValue();
         else
            value = Util.trimLine( block.getASPPage().readValue(name) );

         // Added by Terry 20120822
         // Check CSV value, and get CSV value.
         if(isUrlCSVName(value))
         {
            value = mgr.getCSVValue(Str.replace(value, "@", "#"), field);
         }
         // Added end
         
         // Added by Terry 20130926
         // Sort in Query
         String field_sort = page.readValue("__SORT_" + name);
         if (!Str.isEmpty(field_sort))
         {
            if (!Str.isEmpty(order_by))
               order_by = order_by + "," + dbname + " " + field_sort;
            else
               order_by = order_by + dbname + " " + field_sort;
         }
         // Added end
         
         /*
         if( field.isCheckBox() )
         {
            if( "+".equals(value) )
               value = field.getCheckedValue();
            else if( "-".equals(value) )
               value = field.getUncheckedValue();
            else if( !Str.isEmpty(value) )
               value = field.getCheckedValue();
         }
         */

         if( value==null ) continue;
         if( Util.trimLine(value).length()==0 ) continue;
         //if( field.isIidDbField() ) continue;

         // Added by Terry 20140623
         // Check field accurate_find
         boolean field_accurate_find = false;
         if (!mgr.isEmpty(__view_param_fields))
         {
            if (__view_param_fields.indexOf("^" + name + "^") >= 0)
               field_accurate_find = true;
         }
         // Added end
         
         DataFormatter formatter = field.getDataFormatter();
         if( where.length()>0 )
            where.append("\n   and ");
         where.append("(");

         String colname;
         if( field.isComputable() )
            colname = field.getWhereExpression();
         else if( field.searchOnDbColumn() ) //field.isRadioButtons() )
            colname = dbname+"_DB";
         else
            colname = dbname;
          
         int type_id = formatter.getTypeId();
         char type_marker = formatter.getBaseTypeMarker(type_id);
         Item item;
         boolean sysdate;
         int orat;
         String expr="NULL";

         value = value+";";
         expr = "NULL";
         while (true)  // OR loop.
             {
                 orat = value.indexOf(';');
                 if (orat==-1) break;
                 if (expr != "NULL") where.append(" OR ");
                 expr = value.substring(0,orat);
                 if (expr.indexOf("SYSDATE")>-1) sysdate=true; else sysdate=false;
                 value = value.substring(orat+1);
        
                 if(field.searchOnDbColumn() && expr.endsWith("%"))
                 {
                    String valueList = field.findValues(field.getIidClientValues(), expr);
                    orat = valueList.indexOf(';');
                    if (orat==-1)
                    {
                       expr = valueList;
                    }
                    else
                    {
                       expr = valueList.substring(0,orat);
                       value = value + valueList.substring(orat+1);
                    }
                    
                 }
                 
                 // Added by Terry 20131118
                 // Check accurate find
                 // if (!accurate_find)
                 //    expr = "%" + expr + "%";
                 // Added end

                 int pos = splitIntoOperatorAndValue(expr);
                 if( pos<=0 )
                     {
                         //
                         //  COL like ?
                         //
                         if( field.searchOnDbColumn() ) expr = field.encode(expr);
                         // Modified by Terry 20131119
                         // Original:
                         // if(expr.startsWith("!=") && (((expr.indexOf('%')>=0 || expr.indexOf('_')>=0)) && expr.indexOf("add_month(")==-1)) // Not like expression
                         if(expr.startsWith("!=") && ((((expr.indexOf('%')>=0 || expr.indexOf('_')>=0)) && expr.indexOf("add_month(")==-1) || (!accurate_find && !field_accurate_find && expr.indexOf("..")==-1)))
                         {  
                            expr = expr.substring(2);
                            
                            // Added by Terry 20131118
                            // Check accurate find
                            if (!accurate_find && !field_accurate_find && expr.indexOf('%') < 0)
                               expr = "%" + expr + "%";
                            // Added end
                            
                            switch(type_marker) {
                               case 'S':
                                  if(caseinsens)
                                     where.append("UPPER("+colname+")"+" not like ?");
                                  else
                                     where.append(colname+" not like ?");
                                  break;
                                  
                               case 'D':
                                  String mask = ((DateFormatter)formatter).getFormatMask();
                                  mask = mgr.toSqlDateMask(mask);
                                  where.append("to_char("+colname+",'"+mask+"') not like ?");
                                  break;
                                  
                               case 'N':
                                  where.append(colname+" not like ?");
                                  break;
                            }
                            if(caseinsens) expr = expr.toUpperCase();
                            item = ASPBuffer.convertToServerItem(dbname, expr, srvfmt, strformatter, null);
                            item.setStatus("IN");
                            bind.addItem(item);
                            
                         }
                         // Modified by Terry 20131119
                         // Original:
                         // else if( ((expr.indexOf('%')>=0 || expr.indexOf('_')>=0)) && expr.indexOf("add_month(")==-1)
                         else if (((expr.indexOf('%')>=0 || expr.indexOf('_')>=0 || (!accurate_find && !field_accurate_find && expr.indexOf("..")==-1))) && expr.indexOf("add_month(")==-1)
                         {
                            // Added by Terry 20131118
                            // Check accurate find
                            if (!accurate_find && !field_accurate_find && expr.indexOf('%') < 0)
                               expr = "%" + expr + "%";
                            // Added end
                            
                            switch(type_marker)
                            {
                            case 'S':
                               if(caseinsens)
                                  where.append("UPPER("+colname+")"+" like ?");
                               else 
                                  where.append(colname+" like ?");
                               break;
                            case 'D':
                               String mask = ((DateFormatter)formatter).getFormatMask();
                               mask = mgr.toSqlDateMask(mask);
                               where.append("to_char("+colname+",'"+mask+"') like ?");
                               break;
                               
                            case 'N':
                               where.append(colname+" like ?");
                               break;
                            }
                            if(caseinsens) expr = expr.toUpperCase();
                            item = ASPBuffer.convertToServerItem(dbname, expr, srvfmt, strformatter, null);
                            item.setStatus("IN");
                            bind.addItem(item);
                         }
                         else if( expr.indexOf("..")>0 )
                             {
                                 boolean part1_added = false;
                                 boolean part2_added = false;
                                 
                                 String partone="NULL";
                                 String parttwo="NULL";
                                 int betweenpos;
                                 switch(type_marker)
                                     {
                                     case 'S':
                                         betweenpos = expr.indexOf("..");
                                         partone = expr.substring(0,betweenpos);
                                         parttwo = expr.substring(betweenpos+2);
                                         if(caseinsens)
                                             where.append("UPPER("+colname+")"+" between ? and ?");
                                         else
                                             where.append(colname+" between ? and ?");            
                                         break;
                     
                                     case 'D': 
                                         String mask = ((DateFormatter)formatter).getFormatMask(); 
                                         betweenpos = expr.indexOf("..");
                                         partone = expr.substring(0,betweenpos).toUpperCase();
                                         parttwo = expr.substring(betweenpos+2).toUpperCase();
                                         //where.append(colname+" between to_date(?,'"+mask+"') and to_date(?,'"+mask+"')+(1-1/(24*60*60))"); 
                                         //where.append(colname+" between ? and ? + (1-1/(24*60*60))");

                                         where.append(colname+" between ");
                                         
                                         if( partone.indexOf("SYSDATE")>-1 && (part1_added=true) )
                                         {
                                             if(mgr.isValidSysdateValue(partone))
                                                where.append(partone);
                                             else
                                                mgr.showError("FNDQRYVALSYSDATE: Invalid date with SYSDATE, enter SYSDATE or SYSDATE +/- Number");
                                         }
                                         else
                                            where.append("?");

                                         where.append(" and ");

                                         if( parttwo.indexOf("SYSDATE")>-1 && (part2_added=true) )
                                         {
                                             if(mgr.isValidSysdateValue(parttwo))
                                                where.append(parttwo.replaceAll("SYSDATE","SYSDATE"));
                                             else
                                                mgr.showError("FNDQRYVALSYSDATE: Invalid date with SYSDATE, enter SYSDATE or SYSDATE +/- Number");
                                         }
                                         else
                                            where.append("? + (1-1/(24*60*60))");
                                         
                                         break;
               
                                     case 'N':
                                         betweenpos = expr.indexOf("..");
                                         partone = expr.substring(0,betweenpos);
                                         parttwo = expr.substring(betweenpos+2);
                                         where.append(colname+" between ? and ?");
                                         break;
                                     }
                                 if(caseinsens)
                                     {
                                         partone=partone.toUpperCase();
                                         parttwo=parttwo.toUpperCase();
                                     }
                                 
                                 if(!part1_added)
                                 {
                                    item = field.convertToServerItem(partone,null);
                                    item.setStatus("IN");
                                    bind.addItem(item);
                                 }
                                 if(!part2_added)
                                 {
                                    item = field.convertToServerItem(parttwo,null);
                                    item.setStatus("IN");
                                    bind.addItem(item);
                                 }
                             }
                         else if( type_id == formatter.DATE )
                             {
               
                                 if (sysdate)
                                 {
                                    String mask = ((DateFormatter)formatter).getFormatMask(); 
                                    mask = mgr.toSqlDateMask(mask);

                                    //where.append("to_char("+colname+",'"+mask+"') = to_char("+expr+",'"+mask+"')"); 
                                    where.append(" "+colname+"=to_date(to_char("+expr+",'"+mask+"'),'"+mask+"')"); 
                                    continue;
                                 }
                                 where.append(colname+" between add_months(?,0) and add_months(?,0)+(1-1/(24*60*60))");
                                 item = field.convertToServerItem(expr,null);
                                 item.setStatus("IN");
                                 bind.addItem(item);
                                 bind.addItem(item); 
                             }
                         else if(type_id == formatter.STRING && caseinsens)
                             {
                                 if(sysdate) {where.append(colname+" ="+expr);continue;}
                                 
                                 where.append("UPPER("+colname+")"+" = ?");
                                 expr = expr.toUpperCase();
                                 item = field.convertToServerItem(expr,null);
                                 item.setStatus("IN");
                                 bind.addItem(item);
                             }   
                         else
                             {
                                 if(sysdate) {where.append(colname+" ="+expr);continue;}
                                 if ("STATE".equalsIgnoreCase(colname) && 
                                      !mgr.isEmpty(block.getStateEncodeMethod()))
                                 {
                                    where.append("OBJSTATE"+" = (select " + block.getStateEncodeMethod() +"( ? ) from dual)");                                                                        
                                    item = field.convertToServerItem(expr,null);
                                    item.setStatus("IN");
                                    bind.addItem(item);
                                    continue;
                                 }
                                 where.append(colname+" = ?");
                                 item = field.convertToServerItem(expr,null);
                                 item.setStatus("IN");
                                 bind.addItem(item);
                             }   
                     }
                 else
                     {
                         //
                         //  COL <operator> ?
                         //
                         if (expr.indexOf("!")>-1 && expr.indexOf("!=")==-1)
                             {
                                 where.append(""+colname+" is null");
                                 continue;
                             }
                         if (sysdate) 
                             {
                                 String mask = ((DateFormatter)formatter).getFormatMask(); 
                                 mask = mgr.toSqlDateMask(mask);
                                 String expr_operator;
                                 expr_operator = expr.substring(0,pos);
                                 expr = expr.substring(pos);
                                 where.append("to_date(to_char("+colname+",'"+mask+"'),'"+mask+"') "+expr_operator+" to_date(to_char("+expr+",'"+mask+"'),'"+mask+"')");
                                 continue;
                             }

                         if ("STATE".equalsIgnoreCase(colname) && 
                             !mgr.isEmpty(block.getStateEncodeMethod()))
                         {
                            where.append("OBJSTATE"+' '); 
                            where.append(expr.substring(0,pos));
                            where.append(" (select " + block.getStateEncodeMethod() +"( ? ) from dual)");
                            expr = expr.substring(pos);
                            if(caseinsens && type_marker==formatter.STRING)
                               expr = expr.toUpperCase();                            
                            item = field.convertToServerItem(expr,null);
                            item.setStatus("IN");
                            bind.addItem(item);
                            continue;
                         }
                         if(type_marker == formatter.STRING && caseinsens) 
                             where.append("UPPER("+colname+")"+' ');
                         else
                             where.append(colname+' ');                             
                         where.append(expr.substring(0,pos));
                         where.append(" ?");
                         expr = expr.substring(pos);
                         if(caseinsens && type_marker==formatter.STRING)
                             expr = expr.toUpperCase();
                         if( field.searchOnDbColumn() ) expr = field.encode(expr);
                         item = field.convertToServerItem(expr,null);
                         item.setStatus("IN");
                         bind.addItem(item);
                     }
         }
         where.append(")");
      }
      
      String textSearch = mgr.readValue("FND_TXT_SEARCH");
      //append application search where condition from find mode
      UserDataCache user_cache = UserDataCache.getInstance();
      Vector available_views = user_cache.getApplicationSearchViews(getASPManager().getSessionId());
      if (!mgr.isEmpty(textSearch) && available_views.contains(block.getView()))
      {
         if( where.length()>0 )
            where.append("\n   and ");

         //Temporary solution. Remove and uncomment the following when Application_Search_SYS.Convert_Search_Criteria__ is working
         where.append("contains(text_id$, Application_Search_SYS.Convert_Search_Criteria__(?), 1) > 0");
         Item item = new Item("APPLICATION_SEARCH");
         item.setValue(textSearch);
         item.setStatus("IN");
         bind.addItem(item);
         
         //where.append(convertSearchCriteria(textSearch));
      }
      
      mainbuf.addItem( WHERE, where.toString());
      mainbuf.addItem( DATA, bind);
      
      // Added by Terry 20130926
      // Sort in Query
      if (!Str.isEmpty(order_by))
         setOrderByClause(order_by);
      // Added end
   }


   /**
    * Return the index of the first character after the operator
    */
   private int splitIntoOperatorAndValue( String value )
   {
      if( Str.isEmpty(value) ) return 0;

      switch(value.charAt(0))
      {
         // Bug 40924, start
         // case '=':
         // Bug 40924, end
         case '<':
         case '>':
         case '!':
            break;

         default:
            return 0;
      }
      
      // Added by Terry 20131118
      // Check accurate find
      boolean accurate_find = false;
      if (getASPManager().commandBarActivated())
      {
         if("TRUE".equals(getASPManager().readValue("__ACCURATE_FIND", "FALSE")))
            accurate_find = true;
      }
      else
         accurate_find = true;
      // Added end
      
      // Bug 40924, start
      //if( value.startsWith("=") )
      //   return 1;
      //else 
      // Bug 40924, end
      if( value.startsWith("<=") )
         return 2;
      else if( value.startsWith(">=") )
         return 2;
      else if( value.startsWith("<>") )
         return 2;
      else if( value.startsWith("!=") )
      {
         // Modified by Terry 20131119
         // Original:
         // if(value.indexOf("%")>0 || value.indexOf("_")>0)
         if(value.indexOf("%")>0 || value.indexOf("_")>0 || !accurate_find)
            return 0;
         return 2;
      }
      else if( value.startsWith("!") && value.trim().length()==1)//Depends on the if statement above.
          return 1;
      else if( value.startsWith("<") )
         return 1;
      else if( value.startsWith(">") )
         return 1;
      else
         return 0;
   }

   /**
    * Define the Include Meta option for this ASPQuery. The option must have one
    * of the following values:
    *<pre>
    *   o ALL   - all returned rows will contain column names
    *   o FIRST - only the first returned row will contain column names (default)
    *   o NONE  - the returned rows will NOT contain column names
    *</pre>
    */
   public void includeMeta( String option )
   {
      try
      {
         if( !"ALL".equals(option) &&
             !"FIRST".equals(option) &&
             !"NONE".equals(option) )
            throw new FndException("FNDQRYMETA: The Meta Option must be one of: ALL, FIRST, ONE.");
         mainbuf.setItem("META",option);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Limit the number of fetched rows to the specified value.
    * Note that if you do not call this method, then the default value,
    * defined in the configuration parameter ADMIN/BUFFER_SIZE, will be used.
    */
   public void setBufferSize( int count )
   {
      try
      {
         mainbuf.setItem("MAX_ROWS",count);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define the number of rows which should be skipped while
    * fetching data from the database.
    */
   public void skipRows( int nr )
   {
      try
      {
         mainbuf.setItem("SKIP_ROWS",nr);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Define the Count-Db-Rows option for this query.
    * This option is relevant only if the buffer size is less than the total number of rows
    * returned by the query. In such a case, if the option is turned on, all queried rows will
    * be fetched from the database, just to perform the counting. The INFO-item in
    * the result buffer will contain this counter.
    *
    * Note! Setting this option to TRUE while querying big database tables
    *       may lead to big overhead.
    *
    * This performance glitch was corrected in Bug id 74484. See bug description for more details
    * @see ifs.fnd.asp.ASPQuery.setBufferSize
    */
   public void setCountDbRows( boolean flag )
   {
      try
      {
         mainbuf.setItem("COUNT_ROWS",flag?"Y":"N");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Return the number of DATA-rows in the specified ASPBuffer.
    * This method does not count the last item, if it contains INFO data.
    */
   public int countRows( ASPBuffer buf )
   {
      try
      {
         if( buf==null ) return 0;
         return ASPRowSet.countRows(buf.getBuffer());
      }
      catch( Throwable any )
      {
         error(any);
         return -1;
      }
   }

    /**
     * Change this query's case sensitive option.
     * This function will override any end-user choice.
     */

   public void caseSensitive(boolean thecase)
   {
      if(thecase)
         case_mode = SENSITIVE;
      else
         case_mode = INSENSITIVE;
   }

   /**
    * Return the current case sensitive mode.
    * This function only returns a valid response
    * if the function caseSensitive has been used.
    */
   public boolean isCaseSensitive()
   {
      return case_mode==SENSITIVE;
   }
   
   // Added by Terry 20120822
   // set View function
   public void setView( String view )
   {
      try
      {
         mainbuf.setItem(VIEW, view);
      }
      catch (Throwable any)
      {
         error(any);
      }
   }
   
   // set Where function
   public void setWhereCondition( String where )
   {
      try
      {
         mainbuf.setItem(WHERE, where);
      }
      catch (Throwable any)
      {
         error(any);
      }
   }
   
   // get Select function
   public String getSelectList()
   {
      try
      {
         return mainbuf.getString(SELECT);
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   
   // get View function
   public String getView()
   {
      try
      {
         return mainbuf.getString(VIEW);
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   
   // get order by function
   public String getOrderByClause()
   {
      try
      {
         return mainbuf.getString(ORDER, null);
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   
   // get group by function
   public String getGroupByClause()
   {
      try
      {
         return mainbuf.getString(GROUP, null);
      }
      catch (Throwable any)
      {
         return null;
      }
   }

   // get where function
   public String getWhereCondition()
   {
      try
      {
         return mainbuf.getString(WHERE, null);
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   
   // Get data binding
   public ASPBuffer getDataBinding()
   {
      try
      {
         Buffer data = mainbuf.getBuffer(DATA, null);
         if (data != null)
            return getASPManager().newASPBuffer(data);
      }
      catch (Throwable any)
      {
         return null;
      }
      return null;
   }
   
   // check CSV name
   private boolean isUrlCSVName(String name)
   {
      if (name == null || name == "") return false;
      name = name.trim();
      if (!name.toUpperCase().equals(name)) return false;
      if (!name.startsWith("@")) return false;
      if (!name.endsWith("@")) return false;
      return true;
   }
   // Added end
}
