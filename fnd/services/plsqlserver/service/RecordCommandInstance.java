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
 * File        : RecordCommandInstance.java
 * Description : Record based extension of CommandInstance
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  2002-Oct-25 - Created
 *    Marek D  2003-Feb-26 - Method processInfo() moved to PlsqlUtil
 *    Marek D  2003-Apr-03 - NullPointerException in findItemIgnoreCase (Bug #3877)
 *    Marek D  2003-Sep-05 - Added PLSQL security checks (ToDo #4621)
 *    Marek D  2003-Oct-03 - Restructured framework packages
 * ----------------------------------------------------------------------------
 *    Marek D  2006-Aug-31 - Bug id 60208 : Added proxy-user mode
 *    Marek D  2008-Apr-21 - Bug id 69076 : Added Server functionality needed by new Aurora client
 *    Marek D  2009-Jan-26 - Bug id 80185 : Support for DataDirect JDBC driver
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
 * 2004/05/12 08:29:29  madrse
 * NullPointerException in formatBindVariables
 *
 * 2004/04/21 23:02:45  marese
 * Introduced IFS Buffer character set encoding constant
 *
 * Revision 1.3  2004/02/05 11:06:20  marese
 * Added CVS update logging
 *
 */

package ifs.fnd.services.plsqlserver.service;

import ifs.fnd.log.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.SQLRecognizer;

import ifs.fnd.services.plsqlserver.*;
import ifs.fnd.base.*;
import ifs.fnd.record.*;
import ifs.fnd.record.serialization.FndSerializeConstants;

import java.io.*;

/**
 * <B>Framework internal class:</B>
 * Extension of CommandInstance used by PLSQL invocation.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
public class RecordCommandInstance extends CommandInstance {

   private Command cmd;
   private Command parent;
   private BufferFormatter formatter;
   private boolean isSelectStmt;
   private boolean isFetchStmt;
   private Buffer bindVariables;

   /**
    * Creates new RecordCommandInstance.
    * If the specified SecuritySupervisor is null then no PLSQL security
    * checks will be performed.
    * If this command represents a select statement then the following
    * default values are applied.
    * The default value for maxRows is 0, which means no limit.
    * The default value for skipRows is 0.
    * The default value for countRows is false.
    */
   public RecordCommandInstance(Command cmd,
                                Command parent,
                                String appOwner,
                                SecuritySupervisor security,
                                boolean returnRefCursor) throws Exception {

      super(security != null);

      this.cmd = cmd;
      this.parent = parent;
      this.formatter = new StandardBufferFormatter();

      String sql0 = cmd.statement.getValue();
      String sql  = PlsqlUtil.replace(sql0, "&AO", appOwner);

      SQLRecognizer r;

      if(sql.equals(sql0))
         r = new SQLRecognizer(sql, appOwner+"."); // Prefix &AO not used. Append db prefix automatically
      else
         r = new SQLRecognizer(sql, null); // Prefix &AO used. No automatic prefixing.

      sql = r.getSQLText();

      if(security!=null) {
         String[] tables = r.getTables();
         for(int i=0; i<tables.length; i++)
            security.checkDbTable(tables[i]);

         String[] procedures = r.getProcedures();
         for(int i=0; i<procedures.length; i++)
            security.checkDbProcedure(procedures[i]);
      }

      bindVariables = parseBindVariables(cmd.bindVariables);

      getPropagatedVariables(bindVariables, parent);

      String[] stmtVariables = r.getVariables();
      int stmtType = getStatementType(sql);
      isSelectStmt = stmtType == SELECT;
      boolean generateOuterBlock = !returnRefCursor && stmtType == BLOCK && cmd.generateOuterBlock.booleanValue(false);
      //
      // When a PLSQL block contains named bind variables and the attribute Command.generateOuterBlock is true then
      // binding by-name is implemented by generating an outer PLSQL block,
      // in which variables are bound by-position. The new PLSQL block contains three parts:
      //
      //   (1) IN bind variables are converted into PLSQL local variables
      //   (2) the original PLSQL block where "?" placeholders are replaced with PLSQL local variables
      //   (3) PLSQL local variables are converted back to OUT bind variables
      //
      // In cases where the above algorithm is not necessary the old algorithm is used:
      // If there are no named variables in the statement then bind-by-pos is assumed.
      // Otherwise the specified bind variables are bind-by-name variables that must
      // be converted into bind-by-pos variables before execution by JDBC driver.
      //
      Buffer byPosVars;

      if(generateOuterBlock) {
         byPosVars = PlsqlUtil.createNewBuffer();
         sql = generateOuterBlock(sql, bindVariables, stmtVariables, r, byPosVars);
         Logger log = LogMgr.getClassLogger(getClass());
         if(log.trace) {
            log.trace("Original PLSQL block:\n&1", sql0);
            log.trace("Generated outer PLSQL block:\n&1", sql);
            log.trace("Generated outer bind variables:\n&1", Buffers.listToString(byPosVars));
         }
      }
      else if(stmtVariables.length == 0)
         byPosVars = bindVariables;
      else
         byPosVars = convertToBindByPos(bindVariables, stmtVariables);

      if(isSelectStmt)
         defineQuery(sql,
                     byPosVars,
                     PlsqlUtil.nvl(cmd.maxRows,0),
                     PlsqlUtil.nvl(cmd.skipRows,0),
                     PlsqlUtil.nvl(cmd.countRows,false),
                     PlsqlUtil.nvl(cmd.resultRecordType,null),
                     cmd.cursorId.getValue(),
                     PlsqlUtil.createNewBuffer());
      else
         defineCall(sql, byPosVars);
   }

   /**
    * Creates new RecordCommandInstance used only by FETCH statement.
    */
   public RecordCommandInstance(Command cmd) throws Exception {

      super(false);

      this.isFetchStmt = true;
      this.cmd = cmd;
      this.parent = parent;
      this.formatter = new StandardBufferFormatter();

      defineQuery(null,
                  null,
                  PlsqlUtil.nvl(cmd.maxRows,0),
                  PlsqlUtil.nvl(cmd.skipRows,0),
                  PlsqlUtil.nvl(cmd.countRows,false),
                  PlsqlUtil.nvl(cmd.resultRecordType,null),
                  cmd.cursorId.getValue(),
                  PlsqlUtil.createNewBuffer());
   }

   /**
    * Parse binary attribute bindVariables into a Buffer
    */
   private Buffer parseBindVariables(FndBinary bindVariables) throws Exception {
      byte[] arr = bindVariables.getValue();
      if(arr==null) return null;
      Buffer buf = PlsqlUtil.createNewBuffer();
      formatter.parse(new ByteArrayInputStream(arr), buf);
      return buf;
   }

   /**
    * Convert "bind-by-name" variables into "bind-by-pos" variables using
    * given array of variable names encountered in the sql statement.
    * Perform no cloning of included items (bind variables), this way
    * OUT variables in the source buffer will be updated directly
    * by PlsqlProcesoor.
    */
   private Buffer convertToBindByPos(Buffer byNameVars,
                                     String[] stmtVariables) throws Exception {

      Buffer byPosVars = PlsqlUtil.createNewBuffer();

      for(int i=0; i<stmtVariables.length; i++) {
         String name = stmtVariables[i];
         Item item = findItemIgnoreCase(byNameVars,name);
         if(item==null)
            throw new SystemException("BINDVARUNDEF:Undefined bind variable '&1'", name);
         byPosVars.addItem(item); // No cloning. OUT variables will work automatically.
      }

      return byPosVars;
   }


   /**
    * Returns named item (ignoring case) or null if such item does nit exist.
    */
   private Item findItemIgnoreCase(Buffer buf, String name) {

      if(buf==null) return null;

      BufferIterator iter = buf.iterator();

      while(iter.hasNext()) {
         Item item = iter.next();
         if(name.equalsIgnoreCase(item.getName()))
            return item;
      }
      return null;
   }


   /**
    * Returns true if this command instance represents a select statement,
    * false if its a PLSQL call.
    */
   public boolean isSelectStatement() {
      return isSelectStmt;
   }

   /**
    * Formats the result of this command (the result set for a query and
    * bindVariables for a call) into a binary attribute of the Command
    * view specified at construction time.
    */
   public void formatResult() throws Exception {
      if(isSelectStmt || isFetchStmt)
         formatQueryResult();
      else
         formatBindVariables();
   }



   /**
    * Formats the result set buffer into the result attribute
    */
   private void formatQueryResult() throws Exception {
      byte[] arr = formatter.format(getResultBuffer()).getBytes(FndSerializeConstants.BUFFER_CHARSET);
      cmd.result.setValue(arr);
   }

   /**
    * Formats the bind variables buffer into bindVariables attribute.
    * Note! The user view of bind variables is used (bind-by-name), not
    * the JDBC driver view (bind-by-pos).
    */
   private void formatBindVariables() throws Exception {
      Buffer vars = bindVariables;
      if(vars != null)
         processInfoAndSetPropagatedVariables(vars, parent);
      byte[] arr = vars == null ? null : formatter.format(vars).getBytes(FndSerializeConstants.BUFFER_CHARSET);
      cmd.bindVariables.setValue(arr);
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
      if(rowCount>=0)
         cmd.rowCount.setValue(rowCount);
      else
         cmd.rowCount.setNull();

      if(partialResult != null)
         cmd.partialResult.setValue(partialResult.booleanValue());
   }

   /**
    * Returns meta-data describing type and size of every column in the select list.
    */
   public String getSelectColumnsMetaData() {
      return cmd.selectColumns.getValue();
   }

   //==========================================================================
   // Propagated variables
   //==========================================================================

   /**
    * Updates values of those bind variables that match propagated variables
    * owned by given parent Command.
    */
   private void getPropagatedVariables(Buffer vars, Command parent) throws IfsException {
      if(parent==null || vars==null) return;
      BufferIterator iter = vars.iterator();
      while(iter.hasNext())
         getPropagatedVariable(iter.next(), parent);
   }

   /**
    * Sets the value of a bind variable to the value of the corresponding propagated
    * variable in the parent command.
    * If the bind variable is an IFSAPP attribute string then many propagated
    * variables may have matching parameter name and then all their values
    * will be included.
    */
   private void getPropagatedVariable(Item bindvar, Command parent) throws IfsException {

      String varName = bindvar.getName();
      AttributeString attrstr = null;

      for(int i=0; i<parent.propagatedVariables.size(); i++) {

         PropagatedVariable v = parent.propagatedVariables.get(i);
         v.parameterName.checkValuePresent();

         if(v.parameterName.getValue().equalsIgnoreCase(varName)) {

            v.variableType.checkValuePresent();
            VariableTypeEnumeration.Enum typ = v.variableType.getValue();
            String value = PlsqlUtil.nvl(v.variableValue, null);

            if(typ==VariableTypeEnumeration.ATTRIBUTE_NAME) {

               v.attributeName.checkValuePresent();
               String name = v.attributeName.getValue().toUpperCase();

               if(attrstr==null) {
                  String varValue = bindvar.getString();
                  attrstr = varValue==null ? new AttributeString() : new AttributeString(varValue);
               }

               if(attrstr.getItemValue(name)!=null)
                  attrstr.setItemValue(name, value);
            }
            else if(typ==VariableTypeEnumeration.BIND_VARIABLE) {
               bindvar.setValue(value);
               break;
            }
         }
      }
      if(attrstr!=null)
         bindvar.setValue(attrstr.toString());
   }

   /**
    * Updates values of those propagated variables in a Command that match
    * bind variables in given buffer.
    * If a bind variable correspons to an IFSAPP attribute string then many propagated
    * variables may have matching parameter name and then all of them will be updated.
    * Also performs processing of INFO_ parameters by updating the current FndContext
    * with INFO and WARNING attributes found in a variable named "INFO_".
    */
   private void processInfoAndSetPropagatedVariables(Buffer vars,
                                                     Command parent) throws IfsException {

      BufferIterator iter = vars.iterator();

      while(iter.hasNext()) {

         Item variable = iter.next();
         String varName = variable.getName();
         String varValue = variable.getString();
         if("INFO_".equals(varName))
            PlsqlUtil.processInfo(varValue);
         if(parent==null) continue;
         AttributeString attrstr = null;

         for(int i=0; i<parent.propagatedVariables.size(); i++) {

            PropagatedVariable v = parent.propagatedVariables.get(i);
            v.parameterName.checkValuePresent();

            if(v.parameterName.getValue().equalsIgnoreCase(varName)) {

               v.variableType.checkValuePresent();
               VariableTypeEnumeration.Enum typ = v.variableType.getValue();

               if(typ==VariableTypeEnumeration.ATTRIBUTE_NAME) {
                  v.attributeName.checkValuePresent();
                  String name = v.attributeName.getValue().toUpperCase();

                  if(attrstr==null)
                     attrstr = varValue==null ? new AttributeString() : new AttributeString(varValue);

                  String value = attrstr.getItemValue(name); // returned null means not found

                  if(value!=null) {
                     if(value.equals(""))
                        v.variableValue.setNull();
                     else
                        v.variableValue.setValue(value);
                  }
               }
               else if(typ==VariableTypeEnumeration.BIND_VARIABLE) {
                  v.variableValue.setValue(varValue);
                  break;
               }
            }
         }
      }
   }

   //==========================================================================
   // Bind By-Name - Outer PLSQL block
   //==========================================================================

   // statement types
   private static final int SELECT = 1;
   private static final int BLOCK  = 2;
   private static final int OTHER  = 3;

   private static int getStatementType(String sql) {
      int len = sql.length();
      int pos;

      // skip whitespace
      for(pos = 0; pos < len; pos++) {
         if(sql.charAt(pos) > ' ')
            break;
      }

      if(sql.regionMatches(true, pos, "SELECT", 0, 6))
         return SELECT;
      else if(sql.regionMatches(true, pos, "DECLARE", 0, 7))
         return BLOCK;
      else if(sql.regionMatches(true, pos, "BEGIN", 0, 5))
         return BLOCK;
      else
         return OTHER;
   }

   private static String generateOuterBlock(String sqlStr,
                                            Buffer bindVariables,
                                            String[] stmtVariables,
                                            SQLRecognizer r,
                                            Buffer byPosVars) throws SystemException {
      char[] sql = sqlStr.toCharArray();
      BufferTypeMap typemap = new PlsqlInvocationBufferTypeMap();
      int count = bindVariables.countItems();
      AutoString newsql = new AutoString(sql.length + 50 * count);

      // (1) declare IN and OUT local variables, bind IN variables
      newsql.append("\ndeclare\n");
      for(int i = 0; i < count; i++) {
         Item item = bindVariables.getItem(i);
         newsql.append(" ");
         newsql.append(itemNameToVariableName(item.getName()));
         newsql.append(" ");
         newsql.append(itemTypeToVariableType(typemap, item.getType()));
         newsql.append(" := ");
         String status = item.getStatus();
         if(status == null || status.startsWith("IN")) { // null status defaults to IN_OUT
            newsql.append("?");
            Item inItem = (Item) item.clone();
            inItem.setStatus("IN");  // set direction to IN (required by DataDirect driver)
            byPosVars.addItem(inItem);
         }
         else
            newsql.append("null");
         newsql.append(";\n");
      }
      newsql.append("begin\n");

      // (2) add original PLSQL block replacing "?" placeholders with PLSQL local variables
      int p1 = 0;
      for(int i = 0; i < stmtVariables.length; i++) {
         int p2 = r.getVariablePosition(i);
         newsql.append(sql, p1, p2 - p1);
         newsql.append(itemNameToVariableName(stmtVariables[i]));
         p1 = p2 + 1;
      }
      if(p1 < sql.length)
         newsql.append(sql, p1, sql.length - p1);

      // (3) convert PLSQL local variables back to OUT bind variables
      newsql.append("\n");
      for(int i = 0; i < count; i++) {
         Item item = bindVariables.getItem(i);
         String status = item.getStatus();
         if(status == null || status.endsWith("OUT")) { // null status defaults to IN_OUT
            newsql.append(" ? := ");
            newsql.append(itemNameToVariableName(item.getName()));
            newsql.append(";\n");
            item.setStatus("OUT");  // set direction to OUT (required by DataDirect driver)
            byPosVars.addItem(item);
         }
      }
      newsql.append("end;\n");

      return newsql.toString();
   }

   private static String itemNameToVariableName(String itemName) {
      return itemName.toLowerCase() + "$_";
   }

   private static String itemTypeToVariableType(BufferTypeMap typemap , String itemType) throws SystemException {
      PlsqlType pltype = typemap.itemTypeToPlsqlType(itemType);
      if(pltype==PlsqlType.TEXT)
         return "varchar2(32767)";
      else if(pltype==PlsqlType.LONG_TEXT)
         return "clob";
      else if(pltype==PlsqlType.NUMBER)
         return "number";
      else if(pltype==PlsqlType.TIMESTAMP)
         return "date";
      else if(pltype==PlsqlType.BINARY)
         return "blob";
      else
         throw new SystemException("BINDBYNAME:Bind by-name not supported for item type: &1", itemType);
   }
}
