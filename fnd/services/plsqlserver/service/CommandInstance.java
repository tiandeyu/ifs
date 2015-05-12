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
 * File        : CommandInstance.java
 * Description : Class that represents input/output for a SQL/PLSQL command
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  2002-Oct-25 - Created
 * ----------------------------------------------------------------------------
 *    Marek D  2006-Aug-31 - Bug id 60208 : Added proxy-user mode
 *    Marek D  2008-Apr-21 - Bug id 69076 : Added Server functionality needed by new Aurora client
 *
 * Revision 1.1  2005/04/20 19:03:12  marese
 * Merged fndext with fndbas
 *
 * Revision 1.1  2005/01/28 17:36:51  marese
 * Initial checkin
 *
 * Revision 1.1  2004/08/17 15:11:21  marese
 * Initial check in
 *
 * Revision 1.2  2004/02/05 11:06:20  marese
 * Added CVS update logging
 *
 */

package ifs.fnd.services.plsqlserver.service;

import ifs.fnd.buffer.*;

/**
 * <B>Framework internal class:</B> Class that represents input/output for a SQL/PLSQL command.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
public abstract class CommandInstance {

   // input
   private String sql;
   private Buffer bindVariables;
   private int maxRows;
   private int skipRows;
   private boolean countRows;
   private String resultRecordType;
   private String cursorId;
   private boolean checkSanity;

   // output
   private Buffer result;

   /**
    * @param checkSanity flag indicating if the security parser (PlsqlAnalyzer) should be used to check sanity of SQL text
    */
   public CommandInstance(boolean checkSanity) {
      this.checkSanity = checkSanity;
   }

   //==========================================================================
   // Define command
   //==========================================================================

   /**
    * Defines this command instance as a PLSQL call
    * @param sql           PLSQL block or procedure call text
    * @param bindVariables buffer with bind variables
    */
   public void defineCall(String sql, Buffer bindVariables) {
      this.sql = sql;
      this.bindVariables = bindVariables;
   }

   /**
    * Defines this command instance as a SQL query
    * @param sql              SQL select statement text
    * @param bindVariables    buffer with bind variables
    * @param maxRows          maximum number of rows to be fetched by this command instance,
    *                         the value of 0 means no limit
    * @param skipRows         the number of rows to be skipped when fetching rows
    * @param countRows        true if this command should count the number of rows matching the
    *                         query condition, false otherwise
    * @param resultRecordType the type of result record which is to be returned by this
    *                         query command
    * @param result           the destination buffer for rows returned by this query command
    */
   public void defineQuery(String sql,
                           Buffer bindVariables,
                           int maxRows,
                           int skipRows,
                           boolean countRows,
                           String resultRecordType,
                           String cursorId,
                           Buffer result) {

      this.sql = sql;
      this.bindVariables = bindVariables;
      this.skipRows = skipRows;
      this.maxRows = maxRows;
      this.countRows = countRows;
      this.resultRecordType = resultRecordType;
      this.cursorId = cursorId;
      this.result = result;
   }

   //==========================================================================
   // Retrieve command input
   //==========================================================================

   /**
    * Returns SQL/PLSQL text for this command instance
    */
   public String getSql() {
      return sql;
   }

   /**
    * Returns an ID of an open cursor to fetch data rows from.
    */
   public String getCursorId() {
      return cursorId;
   }

   /**
    * Returns buffer with bind variables for this command instance.
    * OUT and IN/OUT variables will be modified directly in this buffer.
    */
   public Buffer getBindVariables() {
      return bindVariables;
   }

   /**
    * Returns maximum number of rows to be fetched by this command instance.
    * The value of 0 means no limit.
    */
   public int getMaxRows() {
      return maxRows;
   }

   /**
    * Returns the number of rows to be skipped when fetching rows.
    */
   public int getSkipRows() {
      return skipRows;
   }

   /**
    * Returns true if this command should count the number of rows matching the
    * query condition.
    */
   public boolean getCountRows() {
      return countRows;
   }

   /**
    * Returns the type of result record which is to be returned by this query command.
    */
   public String getResultRecordType() {
      return resultRecordType;
   }

   /**
    * Returns the flag indicating if the security parser (PlsqlAnalyzer) should be used to check sanity of SQL text.
    */
   public boolean checkSanity() {
      return checkSanity;
   }

   /**
    * Returns the destination buffer for/with rows returned by this query command.
    */
   public Buffer getResultBuffer() {
      return result;
   }

   /**
    * Returns meta-data describing type and size of every column in the select list.
    */
   public abstract String getSelectColumnsMetaData();

   //==========================================================================
   // Set command result info
   //==========================================================================

   /**
    * Sets extra information about the result set returned by this command.
    * @param rowCount the number of database rows that matched the query condition
    *                 or -1 if this number is unknown.
    * @param partialResult TRUE if the result set has been truncated because of
    *                      maxRows option defined for this command, FALSE otherwise,
    *                      NULL if this information is not known
    */
   public abstract void setQueryResultInfo(int rowCount, Boolean partialResult);
}
