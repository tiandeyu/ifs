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
 * File        : BufferCommandInstance.java
 * Description : Web Request specific extension of CommandInstance
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  2002-Oct-25 - Created
 *    Marek D  2002-Dec-12 - Do not create INFO item in an empty result buffer
 * ----------------------------------------------------------------------------
 *   Marek D   2006-Aug-31 - Bug id 60208 : Added proxy-user mode
 *   Marek D   2008-Apr-21 - Bug id 69076 : Added Server functionality needed by new Aurora client
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
 * <B>Framework internal class:</B> Web Request specific extension of CommandInstance.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
class BufferCommandInstance extends CommandInstance {

   /**
    * Creates a buffer based command instance for a PLSQL call.
    * @param checkSanity flag indicating if the security parser (PlsqlAnalyzer) should be used to check sanity of SQL text
    */
   public BufferCommandInstance(String sql, Buffer bindVariables, boolean checkSanity) {
      super(checkSanity);
      defineCall(sql, bindVariables);
   }

   /**
    * Creates a buffer based command instance for a SQL query.
    * The default value for MAX_ROWS is 100.
    * The default value for SKIP_ROWS is 0.
    * The default value for COUNT_ROWS is true.
    * @param checkSanity flag indicating if the security parser (PlsqlAnalyzer) should be used to check sanity of SQL text
    */
   public BufferCommandInstance(String sql, Buffer bindVariables, Buffer options, Buffer result, boolean checkSanity) {
      super(checkSanity);

      defineQuery(sql,
                  bindVariables,
                  options.getInt("MAX_ROWS", 100),
                  options.getInt("SKIP_ROWS", 0),
                  options.getString("COUNT_ROWS","Y").equals("Y"),
                  null, // resultRecordType
                  null, // cursorId
                  result);
   }

   /**
    * Sets extra information about the result set returned by this command.
    * @param rowCount the number of database rows that matched the query condition
    *                 or -1 if this number is unknown.
    * @param partialResult TRUE if the result set has been truncated because of
    *                      maxRows option defined for this command, FALSE otherwise,
    *                      NULL if this information is not known
    */
   public void setQueryResultInfo(int rowCount, Boolean partialResult) {
      Buffer result = getResultBuffer();
      if(result!=null && result.countItems()>0) {
         Buffer info = result.newInstance();
         info.addItem("SKIP_ROWS", getSkipRows());
         if(rowCount>=0)
            info.addItem("ROWS", rowCount);
         if(partialResult != null)
            info.addItem("HAS_MORE_ROWS", partialResult.booleanValue() ? "Y" : "N");
         result.addItem("INFO", info);
      }
   }

   /**
    * Returns meta-data describing type and size of every column in the select list.
    * @return null - meta-data for select columns is not used by Web Client
    */
   public String getSelectColumnsMetaData() {
      return null;
   }

}
