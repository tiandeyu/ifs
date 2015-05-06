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
 * File        : PlsqlProcessor.java
 * Description : Database access to PLSQL
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  2002-Oct-25 - Created from ifs.fnd.service.ADOProcessor.java
 *    Marek D  2003-Feb-25 - Use next() to implement skipRows (ToDo #3515)
 *    Marek D  2003-Mar-12 - Decreased default fetchSize from 1000 to 10
 *                           and maximum fetchSize from 1000 to 100 (Bug #3703)
 *    Marek D  2003-Sep-05 - Added PLSQL security checks (ToDo #4621)
 *    Marek D  2003-Sep-24 - Import FndSqlType/Value from ifs.fnd.record
 *    Marek D  2003-Oct-03 - Restructured framework packages
 * ----------------------------------------------------------------------------
 *    Marek D  2006-Feb-08 - Read default/maxFetchSize from System properties
 *    Marek D  2006-Aug-31 - Bug id 60208 : Added proxy-user mode
 *    Marek D  2006-Oct-05 - IID F1PR493  : Efficient bulk operations (Ref cursor)
 *    Marek D  2007-Feb-05 - Bug id 63331 : New logging API
 *    Marek D  2008-Apr-21 - Bug id 69076 : Added Server functionality needed by new Aurora client
 *    Marek D  2008-Sep-22 - Bug id 77229 : Memory leak in abortable process list
 *    Marek D  2009-Jan-26 - Bug id 80185 : Support for DataDirect JDBC driver
 *    Marek D  2009-Feb-04 - Bug id 80388 : Decimal precision in Plsql Gateway
 *    Marek D  2009-Feb-26 - Bug id 80990 : Accept log level per trace category in FndContext
 *    Marek D  2009-Jun-03 - Bug id 83427 : Workaround (part II) for DataDirect JDBC with unknown parameter directions
 *    Marek D  2009-Aug-13 - Bug id 85299 : New DataDirect JDBC driver
 *
 * Revision 1.3  2005/11/14 09:26:35  madrse
 * FndSqlType.LONG_TEXT type is used to bind text parameters (longer than 2000 characters) only in INSERT and UPDATE statements (not in PLSQL procedure calls).
 *
 * Revision 1.2  2005/07/22 13:00:30  madrse
 * Added support for LOBs in PlsqlServer
 *
 * Revision 1.1  2005/04/20 19:03:12  marese
 * Merged fndext with fndbas
 *
 * Revision 1.1  2005/01/28 17:36:51  marese
 * Initial checkin
 *
 * Revision 1.2  2004/08/23 11:58:52  marese
 * Style corrections
 *
 * Revision 1.1  2004/08/17 15:11:21  marese
 * Initial check in
 *
 * 2004/07/05 10:04:40  madrse
 * Server developer API
 *
 * Revision 1.2  2004/02/05 11:06:20  marese
 * Added CVS update logging
 *
 */

package ifs.fnd.services.plsqlserver.service;

import ifs.fnd.log.*;
import ifs.fnd.buffer.*;
import ifs.fnd.util.*;
import ifs.fnd.base.*;
import ifs.fnd.sf.storage.FndStatement;
import ifs.fnd.sf.storage.FndConnection;
import ifs.fnd.sf.storage.FndProxyCursor;
import ifs.fnd.sf.storage.FndProxyConnection;
import ifs.fnd.sf.storage.FndRefCursor;
import ifs.fnd.record.FndSqlValue;
import ifs.fnd.record.FndSqlType;
import ifs.fnd.services.plsqlserver.service.security.PlsqlAnalyzer;

import java.sql.*;
import java.util.*;

/**
 * <B>Framework internal class:</B>
 * Class that performs SQL statements and stored procedure calls using JDBC
 * interface. It performs data conversion using private instance of
 * {@link PlsqlConverter}
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
public class PlsqlProcessor {
   private FndConnection connection;

   private boolean debug;

   private PlsqlConverter converter;

   private BufferTypeMap typemap;

   private Logger log = LogMgr.getDatabaseLogger();

   /**
    * In proxy-user mode dedicated connections from ProxyConnectionPool are used.
    */
   private boolean proxyUserMode;

   private static final int maxFetchSize = Integer.parseInt(System.getProperty("fndext.maxFetchSize", "30"));

   private static final int defaultFetchSize = Integer.parseInt(System.getProperty("fndext.defaultFetchSize", "10"));

   /**
    * Flag used to activate sanity checks in Security Parser (PlsqlAnalyzer) for non-dedicated sessions.
    * The flag is false by default, so by default the Security Parser is used only for dedicated sessions.
    * The flag must be set to true to activate the sanity checks both for dedicated and non-dedicated sessions.
    */
   private static final boolean useSecurityParserForNonDedicatedSessions = "true".equalsIgnoreCase(System.getProperty("fndext.useSecurityParserForNonDedicatedSessions", "false"));

   /**
    * @param connection
    *           the database connection used to perform statements
    * @param typemap
    *           implementation of BufferTypeMap used to map item types to/from
    *           PLSQL types.
    * @param proxyUserMode
    *           In proxy-user mode dedicated connections from ProxyConnectionPool are used
    *
    */
   public PlsqlProcessor(FndConnection connection, BufferTypeMap typemap, boolean proxyUserMode) {
      this.debug = PlsqlUtil.isDebugOn();
      this.connection = connection;
      this.typemap = typemap;
      this.proxyUserMode = proxyUserMode;
      converter = new PlsqlConverter(typemap);
   }
   
   // Added by Terry 20120807
   // get original select stmt for select
   private String getSelectStmt(String sql)
   {
	   String start_stmt = "SELECT three_stmt.*, rownum three_r_n FROM (";
	   int start_index = sql.indexOf(start_stmt);
	   int end_index = sql.lastIndexOf(") three_stmt");
	   if (start_index != -1 && end_index > start_index)
	   {
		   // SQL stmt is new select operation.
		   String original_stmt = sql.substring(start_index + start_stmt.length(), end_index);
		   return getSelectStmt(original_stmt);
	   }
	   else
		   return sql;
   }
   
   private String removeOrderBy(String sql)
   {
	   // Remove order by
	   String sSearchOrderBy = "\n order by ";
	   int nLengthOrderBy;
	   int order_by_index = sql.indexOf(sSearchOrderBy);
	   if (order_by_index != -1)
	   {
		   nLengthOrderBy = (sql.length() - order_by_index);
		   if (nLengthOrderBy > 0)
		   {
			   return sql.substring(0, order_by_index);
		   }
	   }
	   return sql;
   }
   
   // Must invoke behind removeOrderBy function.
   private String removeGroupBy(String sql)
   {
	   String sSearchGroupBy = "\n group by ";
	   int group_by_index = sql.indexOf(sSearchGroupBy);
	   if (group_by_index != -1)
	   {
		   // Found group by clause
		   int nLengthGroupBy = (sql.length() - group_by_index);
		   if (nLengthGroupBy > 0)
		   {
			   return sql.substring(0, group_by_index);
		   }
	   }
	   return sql;
   }
   
   // Get count SQL of select
   private String getCountStmt(String sql)
   {
	   String select_stmt = getSelectStmt(sql);
	   if (!Str.isEmpty(select_stmt) && !select_stmt.equalsIgnoreCase(sql))
	   {
		   String changed_stmt = removeGroupBy(removeOrderBy(select_stmt));
		   if (!Str.isEmpty(changed_stmt))
		   {
			   int ed_index = changed_stmt.indexOf("  from ");
			   if (ed_index > 0)
			   {
				   return "select COUNT(1) " + changed_stmt.substring(ed_index);
			   }
		   }
	   }
	   return "";
   }
   
   // Get count SQL result of select
   private Long getCountQuery(CommandInstance cmd, String count_stmt) throws Exception
   {
	   FndStatement stmt_count = null;
	   Long sql_count = null;
	   
	   if (!Str.isEmpty(count_stmt))
	   {
		   try
		   {
			   stmt_count = connection.createStatement();
			   Buffer bindvars = cmd.getBindVariables();
			   int bindcount = bindvars == null ? 0 : bindvars.countItems();
			   for (int i = 0; i < bindcount; i++)
				   stmt_count.defineParameter(converter.itemToFndSqlValue(i + 1, bindvars.getItem(i), true, false));
			   stmt_count.prepare(count_stmt);
			   stmt_count.setFetchSize(1);
			   stmt_count.executeQuery();
			   ResultSet rs_count = stmt_count.getResult();
			   if (rs_count.next())
			   {
				   sql_count = Long.valueOf(rs_count.getLong(1));
				   System.out.println("SQL_COUNT:" + sql_count);    			  
			   }
		   }
		   catch (SQLException e)
		   {
		      
		   }
		   finally
		   {
			   if (stmt_count != null)
				   stmt_count.close();
		   }
	   }
	   return sql_count;
   }
   // Added end

   /**
    * Perform a SQL select statement using given CommandInstance as the source
    * (sql statement, command options) and destination (result, result info) of
    * the command.
    */
   public void query(CommandInstance cmd) throws Exception {
      FndStatement stmt = null;
      String cursorId = cmd.getCursorId();

      boolean closeStmt = true; // set to false when stmt is added to FndProxyConnection as a cursor
      
      // Added by Terry 20120807
      // 1. Get count SQL of select
      
      String count_stmt = getCountStmt(cmd.getSql());
      // System.out.println("COUNT_SELECT_STMT: " + count_stmt);
      // 2. Get count SQL result of select
      Long sql_count = getCountQuery(cmd, count_stmt);
      // Added end

      try {
         if(cmd.checkSanity() && (useSecurityParserForNonDedicatedSessions || proxyUserMode))
            PlsqlAnalyzer.analyze(cmd.getSql(), false);

         stmt = connection.createStatement();

         Buffer bindvars = cmd.getBindVariables();
         int bindcount = bindvars == null ? 0 : bindvars.countItems();

         for (int i = 0; i < bindcount; i++)
            stmt.defineParameter(converter.itemToFndSqlValue(i + 1, bindvars.getItem(i), true, false));

         if (debug)
            PlsqlUtil.debug("PlsqlProcessor: preparing query: " + cmd.getSql());
         stmt.prepare(cmd.getSql());
         
         System.out.println("SELECT_STMT: " + cmd.getSql());

         // Set statement limits. Be careful with overflow of maxRows.
         int maxRows = cmd.getMaxRows();
         int skipRows = cmd.getSkipRows();
         if (maxRows == 0)
            maxRows = Integer.MAX_VALUE;

         if (maxRows < Integer.MAX_VALUE)
            stmt.setFetchSize(Math.min(maxRows + skipRows, maxFetchSize));
         else
            stmt.setFetchSize(defaultFetchSize);

         // do not call.setMaxRows() if cursorId != null (client may want to fetch all rows)
         if(cursorId == null && maxRows < Integer.MAX_VALUE && !cmd.getCountRows())
            stmt.setMaxRows(maxRows + skipRows + 1); // one extra fetch to check partialResult

         // parse client-side meta-data for select columns
         ArrayList<FndSqlType> columnTypes = defineColumnTypes(stmt, cmd.getSelectColumnsMetaData());

         stmt.executeQuery();

         FndProxyCursor cursor = null;
         Buffer result = cmd.getResultBuffer();
         if(cursorId != null) {
            cursor = new FndProxyCursor(stmt, columnTypes);
            ((FndProxyConnection)connection).addCursor(cursorId, cursor);
            closeStmt = false;
         }

         // Modified by Terry 20120807
         // original: resultSetToBuffer(stmt, result, cmd, columnTypes);
         resultSetToBuffer(stmt, result, cmd, columnTypes, sql_count);
         // Modified end
      }
      finally {
         if (stmt != null && closeStmt)
            stmt.close();
      }
   }

   /**
    * Fetches data from an open cursor.
    * @param stmt an open SELECT statement
    * @param cmd CommandInstance that acts as the source (command options) and destination (result, result info) of the command.
    */
   public void fetch(FndStatement stmt, CommandInstance cmd) throws Exception {
      Buffer result = cmd.getResultBuffer();

      FndProxyCursor cursor = null;
      if(proxyUserMode) {
         String cursorId = cmd.getCursorId();
         if(cursorId != null) {
            FndProxyConnection proxyConn = (FndProxyConnection) connection;
            cursor = (FndProxyCursor) proxyConn.getCursor(cursorId);
         }
      }

      ArrayList<FndSqlType> columnTypes = cursor == null ? null : cursor.getColumnTypes();
      resultSetToBuffer(stmt, result, cmd, columnTypes, null);
   }

   /**
    * Converts a JDBC result set to a Buffer by transforming every fetched row
    * to a DATA item. The fetching process is controled by command options
    * (maxRows, skipRows, CountRows) retrieved from the specified
    * CommandInstance. Extra information about the result (rowCount,
    * partialResult) is passed to the CommandInstance.setResultInfo().
    */
   private void resultSetToBuffer(FndStatement stmt, Buffer result, CommandInstance cmd, ArrayList<FndSqlType> columnTypes, Long sql_count) throws Exception {
      ResultSet rs = stmt.getResult();
      ResultSetMetaData metadata = rs.getMetaData();

      //if(log.trace)
      //   debugResultSetMetaData(metadata);

      int columnCount = metadata.getColumnCount();

      int maxRows = cmd.getMaxRows();
      int skipRows = cmd.getSkipRows();
      if (maxRows == 0)
         maxRows = Integer.MAX_VALUE;

      if (skipRows > 0 && sql_count == null) {
         if (debug)
            PlsqlUtil.debug("PlsqlProcessor: Skipping " + skipRows + " row(s)");
         for (int i = 0; i < skipRows; i++) {
            if (!rs.next())
               break;
         }
         //rs.absolute(skipRows);
      }

      int fetchCount = 0;

      while (fetchCount < maxRows && rs.next()) {
         fetchCount++;
         Buffer row = result.newInstance();

         result.addItem(new Item("DATA", cmd.getResultRecordType(), null, row));
         for (int i = 0; i < columnCount; i++) {
            FndSqlType columnType = columnTypes == null ? null : columnTypes.get(i);
            Item item = converter.columnToItem(stmt, metadata, i + 1, columnType);
            row.addItem(item);
         }
         if (debug)
            PlsqlUtil.debug("PlsqlProcessor: Row " + fetchCount + " fetched");

         reportWarnings(rs);
      }

      Boolean partialResult = null;
      int rowCount = -1;

      if (fetchCount < maxRows) {
         partialResult = Boolean.FALSE;
         rowCount = fetchCount + skipRows;
         String cursorId = cmd.getCursorId();
         if(cursorId != null) {
            if (debug)
               PlsqlUtil.debug("PlsqlProcessor: Closing proxy cursor ["+cursorId+"] because fetchCount="+fetchCount+" < maxRows="+maxRows);
            ((FndProxyConnection)connection).closeCursor(cursorId);
         }
      }
      else {
         if(cmd.getCursorId() != null) {
            // partialResult and rowCount will be undefined (we cannot call rs.next() to check)
         }
         else if (rs.next()) {
            partialResult = Boolean.TRUE;
            if (cmd.getCountRows()) {
               fetchCount++;
               while (rs.next())
                  fetchCount++;
               rowCount = fetchCount + skipRows;
            }
         }
         else if (sql_count != null && (fetchCount + skipRows < sql_count.intValue()))
         {
            partialResult = Boolean.TRUE;
            rowCount = sql_count.intValue();
         }
         else {
            partialResult = Boolean.FALSE;
            rowCount = fetchCount + skipRows;
         }
      }

      cmd.setQueryResultInfo(rowCount, partialResult);
   }

   /**
    * Execute a stored procedure using given CommandInstance as the source (sql
    * statement, bind variables, command options) and destination (bind
    * variables) of the command. Each bind variable will be transformed into a
    * JDBC parameter. OUT and INOUT variables will be modified directly in the
    * bind variable buffer.
    * If returnRefCursor is true then the the first bind parameter is assumed to
    * be an OUT-parameter of type REF CURSOR, while all other parameters are assumed to
    * be IN-parameters.
    * @param returnRefCursor true if FndRefCursor should be return, false otherwise
    * @return FndRefCursor or null depending on value of parameter returnRefCursor
    */
   public FndRefCursor call(CommandInstance cmd, boolean returnRefCursor) throws Exception {
      FndStatement stmt = null;
      FndRefCursor refCursor = null;

      try {
         String sql = cmd.getSql();

         if(cmd.checkSanity() && (useSecurityParserForNonDedicatedSessions || proxyUserMode))
            PlsqlAnalyzer.analyze(sql, false);

         boolean isUpdateOrInsert = isUpdateOrInsert(sql);

         System.out.println("CALL_STMT: " + sql);
         
         stmt = connection.createStatement();

         Buffer bindvars = cmd.getBindVariables();

         if(returnRefCursor)
            sql = stmt.transformRefCursorCall(sql, bindvars); // DataDirect driver requires special syntax for REF CURSORs

         int bindcount = bindvars == null ? 0 : bindvars.countItems();
         FndSqlValue[] params = new FndSqlValue[bindcount];

         for (int i = 0; i < bindcount; i++) {
            if(i == 0 && returnRefCursor && stmt.defineRefCursorVariable()) {
               stmt.defineOutParameter("CURSOR", FndSqlType.REF_CURSOR);
            }
            else {
               params[i] = converter.itemToFndSqlValue(i + 1, bindvars.getItem(i), false, isUpdateOrInsert);
               stmt.defineParameter(params[i]);
            }
         }

         if (debug)
            PlsqlUtil.debug("PlsqlProcessor: preparing call: " + sql);

         stmt.prepareCall(sql);

         stmt.execute();

         if(returnRefCursor) {
            refCursor = stmt.getRefCursor();
         }
         else {
            for (int i = 0; i < bindcount; i++)
               converter.parameterToItem(stmt, i + 1, params[i], bindvars.getItem(i));
         }

         if (debug)
            PlsqlUtil.debug("PlsqlProcessor: Statement performed.");
         return refCursor;
      }
      finally {
         if (stmt != null && (!returnRefCursor || stmt.closeRefCursorStatement()))
            stmt.close();
      }
   }

   private static boolean isUpdateOrInsert(String sql) {
      StringTokenizer st = new StringTokenizer(sql);
      if(st.hasMoreTokens()) {
         String keyword = st.nextToken();
         if("INSERT".equalsIgnoreCase(keyword) || "UPDATE".equalsIgnoreCase(keyword))
            return true;
      }
      return false;
   }

   private ArrayList<FndSqlType> defineColumnTypes(FndStatement stmt, String selectColumns) throws Exception {
      if(Str.isEmpty(selectColumns))
         return null;

      if(log.trace)
         log.trace("PlsqlProcessor.defineColumnTypes(): selectColumns: &1", selectColumns);

      boolean supportsDefineColumnType = stmt.supportsDefineColumnType();
      ArrayList<FndSqlType> types = new ArrayList<FndSqlType>();

      int colNr = 0;
      StringTokenizer x = new StringTokenizer(selectColumns, ",");
      while(x.hasMoreTokens()) {
         String col = x.nextToken();
         colNr++;
         int delim = col.indexOf('^');
         if(delim < 0)
            throw new SystemException("METADATADELIM:Missing '^'-delimiter in meta-data for select columns: &1", selectColumns);
         String type = col.substring(0, delim);

         if(type.equals("DEC")) {
            types.add(FndSqlType.DECIMAL);
            continue;
         }

         if(type.equals("?")) {
            types.add(null);
            continue;
         }

         if(!supportsDefineColumnType) {
            types.add(null);
            continue;
         }

         int size = Integer.parseInt(col.substring(delim + 1));
         FndSqlType fndType = typemap.itemTypeToFndSqlType(type);
         types.add(fndType);

         if(!needsSize(fndType))
            size = -1;

         if(log.debug)
            log.debug("   [&1] &2 &3", String.valueOf(colNr), fndType.getName(), String.valueOf(size));
         if(size < 0)
            stmt.defineColumn(colNr, fndType);
         else
            stmt.defineColumn(colNr, fndType, size);
      }

      if(log.trace)
         log.trace("PlsqlProcessor.defineColumnTypes(): columnTypes: &1", types.toString());

      return types;
   }

   private boolean needsSize(FndSqlType type) {
      if (type == FndSqlType.BINARY || type == FndSqlType.STRING || type == FndSqlType.TEXT)
         return true;
      else
         return false;
   }

   private void debugResultSetMetaData(ResultSetMetaData meta) throws SQLException {
      log.trace("ResultSet meta data");
      int columnCount = meta.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
         String name  = meta.getColumnName(i);
         String type = meta.getColumnTypeName(i);
         int precision = meta.getPrecision(i);
         int scale = meta.getScale(i);
         log.trace("   &1 &2 precision=&3 scale=&4", name, type, String.valueOf(precision), String.valueOf(scale));
      }
   }

   private void reportWarnings(ResultSet rs) throws SQLException {
      if(!log.trace)
         return;
      SQLWarning warning = rs.getWarnings();
      while (warning != null) {
         log.trace("   SQLWarning: &1 SQLSTATE=&2, ERRORCODE=&3", warning.getMessage(), warning.getSQLState(), Integer.toString(warning.getErrorCode()));
         warning = warning.getNextWarning();
      }
   }
}

