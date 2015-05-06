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
 * File        : StandardPlSqlService.java
 * Description : Service that can perform standard PL/SQL methods and queries
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1997-Nov-24 - Created
 *    Marek D  1998-Feb-04 - Added support for non-standard PL/SQL methods
 *    Marek D  1998-Mar-20 - Added support for PL/SQL functions
 *    Marek D  1998-Mar-25 - Replace chr(31) on return from non std commands
 *    Marek D  1998-Apr-01 - Added nested commands and local references
 *    Marek D  1998-Jun-05 - Removed DATA-rows are removed from result buffer
?                           Remove-Modify-New order of PerRow commands
 *    Marek D  1998-Jun-17 - Corrected execution of Custom Commands per Row
 *                           Many command ids per DATA row
 *    Marek D  1998-Jul-02 - Can execute PL/SQL blocks without bind variables
 *    Marek D  1998-Jul-06 - Do not replace standard separators (ascii 28..31)
 *    Marek D  1998-Jul-13 - Query can contain any SELECT statement
 *                           All SQL statements pass the Security Filter
 *                           Store the RequestContext in an instance variable
 *                           Better trace outputs
 *    Jacek P  1998-Jul-29 - Introduced FndException
 *    Marek D  1998-Aug-17 - Use SQLRecognizer instead of SQLTokenizer
 *    Marek D  1998-Nov-18 - Clear unresolved '&'-references (Bug #2863)
 *    Marek D  1998-Dec-11 - Added commands for trace statistics (ToDo #3014)
 *    Jacek P  1998-Dec-21 - Trace statistics commands changed to support the
 *                           new concept with multiple threads and CPU times.
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Marek D  1999-Feb-12 - Call SecuritySupervisor's checkDbTable() and/or
 *                           chackDbProcedure() instead of checkDbObject()
 *    Jacek P  1999-Apr-28 - New API for trace statistics.
 *    Marek D  1999-Jun-10 - Added method getCurrentRowNo()
 *    Marek D,
 *    Jacek P  1999-Jul-22 - Preserve item type for out parameters
 *                           in function resolveOutReferences().
 *    Jacek P  1999-Aug-13 - Removed filter for _DB columns in functions:
 *                           attributeStringForNew() and attributeStringForModify().
 *    Johan S  2000-Jan-03 - Added support for clearing the MTS security cache in perfomeOne().
 *    Reine A  2000-Apr-13 - Added functionality in performQuery() - GROUP BY.
 *    Reine A  2000-May-10 - Mapped __OBJSTATE to OBJSTATE and __OBJEVENTS to
 *                           OBJEVENTS in performNew().
 *    Jacek P  2000-Jun-13 - Changed handling of __OBJSTATE and __OBJEVENTS.
 *    Mangala  2000-Oct-02 - Added methods checkSqlQueryText, secureModifyPostCall, isProcedureAvailable
 *                           and done some minor changes in methods performPostCommands, secureQuery to
 *                           replace null instead of raising Security Exception for not accessible Get
 *                           methods.
 *    Piotr Z  2000-Nov-01 - Added error handling of Oracle Error ORA-01403: no data found
 *    Artur K                in function performAny().
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    Marek D  2002-Oct-30 - Copied from package ifs.fnd.service
 *                              addInfo() - fixed multiple INFO__ items
 *    Marek D  2003-Feb-04 - Added command CATEGORY "Sql" (ToDo# 3485)
 *    Marek D  2003-Feb-13 - Replace "&AO." prefix in commands of category Sql
 *    Marek D  2003-Sep-03 - Do not set current_row_no to 1 if there is only one
 *                           processed row in performSelectedMethod (ToDo #4587)
 *    Ronalk   2004-Apr-20   bugid=44051 execute PLSQL methods which has no parameters fails.
 * ----------------------------------------------------------------------------
 *    Marek D  2006-Aug-31 - Bug id 60208 : Added proxy-user mode
 *    Marek D  2007-Jan-05 - Bug id 62608 : Use system user connections in proxy-user mode
 *    Marek D  2009-Jan-26 - Bug id 80185 : Support for DataDirect JDBC driver
 *
 * Revision 1.5  2005/11/17 09:20:40  madrse
 * In StandardPlSqlService.performPostCommands() add item "CATEGORY" to a post-command only if such item does not exists.
 * A post-command can be a constant buffer reused by many instances of AspPage.
 *
 * Revision 1.4  2005/10/05 07:53:23  madrse
 * Use typed binding for result from POST-functions in WEB requests (they were bound as VARCHAR before)
 *
 * Revision 1.3  2005/10/04 08:21:43  madrse
 * Use BEGIN END syntax instead of call in generated SQL for WEB requests
 *
 * Revision 1.2  2005/09/21 10:36:28  madrse
 * Replaced with begin end in generated SQL for Web request (bug 52527 correction merged from 2004 release)
 *
 * Revision 1.1  2005/04/20 19:03:12  marese
 * Merged fndext with fndbas
 *
 * Revision 1.2  2005/04/20 06:57:09  madrse
 * Clear security cache in PlsqlGateway by sending an administrative framework message to all deployed applications.
 *
 * Revision 1.1  2005/01/28 17:36:51  marese
 * Initial checkin
 *
 * Revision 1.4  2004/11/20 19:04:43  marese
 * Coding style corrections
 *
 * Revision 1.3  2004/11/19 09:36:02  marese
 * Coding style corrections
 *
 * Revision 1.2  2004/10/06 21:41:46  marese
 * Coding style corrections
 *
 * Revision 1.1  2004/08/17 15:11:21  marese
 * Initial check in
 *
 * 2004/06/16 11:20:31  madrse
 * Corrected 2004 error "execute PLSQL methods which has no parameters fails".
 *
 * Revision 1.2  2004/02/05 11:06:20  marese
 * Added CVS update logging
 *
 */

package ifs.fnd.services.plsqlserver.service;

import ifs.fnd.util.Str;
import ifs.fnd.buffer.*;
import ifs.fnd.service.FndException;
import ifs.fnd.service.IfsNames;
import ifs.fnd.service.SQLRecognizer;
import ifs.fnd.sf.FndServerContext;

import java.util.*;

/**
 * <B>Framework internal class:</B>
 * Class that performs PL/SQL methods and SQL queries included in a standard LU package.
 *<pre>
 *   o Standard private methods: New__, Modify__, Remove__.
 *   o Private State changing methods, like Cancel__ or Invoice__
 *   o Any public PL/SQL procedure or function (with or without "describe")
 *   o Any SELECT statement, with or without bind variables
 *   o Any PL/SQL block without bind variables
 *</pre>
 *<p>
 * Global references "&RESPONSE/..." refer to the first matching item
 * in the whole response buffer. They cannot refer, for example, to
 * a specific row in a query result.
 *<p>
 * Local references refer to the parent's DATA-buffer, so they work
 * fine for many-rows buffers, but they cannot refer to another command.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
public class StandardPlSqlService {
   public static boolean DEBUG = true;

   private WebRequestContext context;
   private boolean trace_on;
   private SecuritySupervisor security;

   private String current_row_no;

   /**
    * The flag that controls PLSQL security checks performed by JSF framework.
    */
   private boolean fndSecurityChecks;

   /**
    * @param fndSecurityChecks boolean flag indicating if PLSQL security checks should be performed by SecuritySupervisor
    */
   StandardPlSqlService(boolean fndSecurityChecks) {
      this.fndSecurityChecks = fndSecurityChecks;
   }

   /**
    * Perform a command using the specified context and placing the result in
    * the specified result-buffer. The command buffer should contain the
    * following items:
    *<pre>
    *   Name     Type      Contents                        Example
    *   -------- --------- ------------------------------- ------------------
    *   METHOD   Simple    full (or symbolic) method name  Customer_API.New__
    *   DATA     Compound  sub-buffer with argumetns
    *</pre>
    * The commnd output is placed in the result-buffer. It may include one
    * or more sub-buffers named "DATA".
    *<p>
    * A command may contain one or more "COMMAND" sub-buffers or nested commands.
    * PRE-commands are executed in the context of the command's DATA-buffer.
    * POST-commands are executed once for each DATA-buffer returned by the
    * parent command.
    *<p>
    * Nested commands can contain local references, which are resolved in the
    * context of the corresponding parent DATA-buffer.
    */
   public boolean perform(Buffer command, Buffer result, WebRequestContext context) throws Exception {
      String method = command.getString("METHOD", null);
      if (method == null)
         return true;

      this.current_row_no = null;
      this.context = context;
      this.trace_on = context.isDebugOn();
      this.security = context.getSecuritySupervisor();

      if (method.equals("PerRow")) {
         performPerRow(command, result);
      }
      else {
         Buffer data = command.getBuffer("DATA", null);
         performOne(command, data, result);
      }
      return true;
   }

   /**
    * Return the number of the currently executed row in a PerRow command
    */
   public String getCurrentRowNo() {
      return current_row_no;
   }

   /**
    * Perform the specified command on the given DATA buffer.
    * Place the result in the the specified result-buffer.
    * The DATA may contain global references, but NO local references.
    * The command may contain PRE/POST sub-commands.
    */
   private void performOne(Buffer command, Buffer data, Buffer result) throws Exception {
      String method = command.getString("METHOD", null);

      replaceGlobalReferences(data);

      performPreCommands(command, data);

      if (method.equals("Query")) {
         performQuery(command, result);
      }
      else if (method.equals("SecurityQuery")) {
         security.performSecurityQuery(command, result);
      }
      else if (method.equals("SpoolTraceStatistics")) {
         //TraceEventType.spoolStatistics("MTSManager.total");
      }
      else if (method.equals("ClearTraceStatistics")) {
         //TraceEventType.clearStatistics();
      }
      else if (method.equals("SetTraceStatisticsPrecision")) {
         //TraceEventType.precision = Integer.parseInt(command.getString("PRECISION","0"));
      }
      else if (method.equals("ClearMTSSecurityCache")) {
         FndServerContext.getCurrentServerContext().clearPlsqlGatewaySecurityCache();
         //security.clearCache();
      }
      else {
         String category = command.getString("CATEGORY", null);

         if ("Standard".equals(category)) {
            if (method.endsWith(".New__"))
               performNew(method, command, data, result);
            else if (method.endsWith(".Modify__"))
               performModify(method, command, data, result);
            else if (method.endsWith(".Remove__"))
               performRemove(method, command, data, result);
            else
               performStateMethod(method, command, data, result);
         }
         else if ("Describe".equals(category))
            describeAndPerform(method, command, data, result);
         else
            performAny(method, command, data, result);
      }

      performPostCommands(command, result);
   }

   /**
    * Perform each PRE-command in the context of the specified DATA buffer
    */
   private void performPreCommands(Buffer command, Buffer data) throws Exception {
      if (data == null)
         return;

      BufferIterator cmditer = command.iterator();
      while (cmditer.hasNext()) {
         Item sub = cmditer.next();
         if (sub.isCompound() && "COMMAND".equals(sub.getName()) && "PRE".equals(sub.getStatus())) {
            if (trace_on)
               context.debug(this, "COMMAND/PRE begin");
            performSubCommand(sub.getBuffer(), data);
            if (trace_on)
               context.debug(this, "COMMAND/PRE end");
         }
      }
   }

   /**
    * Perform each POST-command in the context of each DATA-buffer
    * in the result buffer.
    */
   private void performPostCommands(Buffer command, Buffer result) throws Exception {
      Vector postcommands = new Vector();
      BufferIterator cmditer = command.iterator();
      String method = command.getString("METHOD", null);

      while (cmditer.hasNext()) {
         Item sub = cmditer.next();
         if (sub.isCompound() && "COMMAND".equals(sub.getName()) && "POST".equals(sub.getStatus())) {
            Buffer subbuf = sub.getBuffer();
            if (method.endsWith(".New__") || method.endsWith(".Modify__")) {
               if(subbuf.getItemPosition("CATEGORY") < 0)
                  subbuf.addItem("CATEGORY", "PostModify");
            }
            postcommands.addElement(subbuf);
         }
      }

      if (postcommands.isEmpty())
         return;

      BufferIterator dataiter = result.iterator();
      while (dataiter.hasNext()) {
         Item data = dataiter.next();
         if (data.isCompound() && "DATA".equals(data.getName())) {
            Buffer databuf = data.getBuffer();
            for (int i = 0; i < postcommands.size(); i++) {
               Buffer subcmd = (Buffer) postcommands.elementAt(i);
               if (trace_on)
                  context.debug(this, "COMMAND/POST begin");
               performSubCommand(subcmd, databuf);
               if (trace_on)
                  context.debug(this, "COMMAND/POST end");
            }
         }
      }
   }

   /**
    * o copy type+value to each IN-reference from the parent DATA buffer
    * o Perform the nested command into a new result buffer
    * o copy type+value from each OUT-reference to the parent DATA buffer
    */
   private void performSubCommand(Buffer subcmd, Buffer parentData) throws Exception {
      Buffer subresult = subcmd.newInstance();
      Buffer data = (Buffer) subcmd.getBuffer("DATA").clone();
      int[] outref = new int[data.countItems()];

      boolean inputChanged = resolveInReferences(data, parentData, outref);

      if ("Y".equals(subcmd.getString("FORCE", "N")) || inputChanged) {
         performOne(subcmd, data, subresult);

         resolveOutReferences(outref, subresult.getBuffer("DATA"), parentData);
      }
   }

   //==========================================================================
   //  PerRow Commands
   //==========================================================================

   /**
    * Perform the per row selected command(s) in the specified command buffer.
    * Which means that for each DATA-item contained in the specified
    * Command Buffer:
    *<pre>
    *   o Retrieve the method name (ID) from the item's Status
    *   o Retrieve the named Command Buffer from the DEFINE sub-buffer
    *   o Create an empty Result sub-buffer
    *   o Perform the selected command on the DATA-row and the Result sub-buffer
    *   o Move the output DATA from the Result sub-buffer to the Result Buffer
    *</pre>
    */
   protected void performPerRow(Buffer command, Buffer result) throws Exception {
      BufferIterator cmditer = command.iterator();
      while (cmditer.hasNext()) {
         Item item = cmditer.next();
         if (item.isCompound() && "DATA".equals(item.getName()))
            result.addItem(item);
      }

      Buffer define = command.getBuffer("DEFINE", null);

      for (int i = 0; i < 4; i++)
         performSelectedMethod(ordered_standard_method[i], result, define);
   }

   private static String[] ordered_standard_method = { "Remove__", "Modify__", "New__", null };

   /**
    * Perform the specified method on every row marked with that method.
    * Skip unmarked rows, remove Remove__-d rows.
    * A referred command may have its own DATA buffer. In such a case,
    * resolve its local references against the current DATA row, and pass the local
    * DATA to the command.
    * Otherwise pass the current DATA row to the command.
    */
   protected void performSelectedMethod(String method, Buffer rowset, Buffer define) throws Exception {
      boolean setCurrentRowNo = rowset.countItems() > 1;

      for (int i = 0; i < rowset.countItems();) {
         if (setCurrentRowNo)
            current_row_no = String.valueOf(i);
         Item row = rowset.getItem(i);
         String cmdid = row.getStatus();

         if (Str.isEmpty(cmdid) || (method != null && !cmdid.equals(method))) {
            i++; // skip this row
         }
         else {
            if (define == null)
               throw new FndException("FNDPLSNODEFB: DEFINE buffer is missing in PerRow command");

            Buffer data = row.getBuffer();
            StringTokenizer st = new StringTokenizer(cmdid, ", \t\r\n");
            while (data != null && st.hasMoreTokens()) {
               String id = st.nextToken();
               Buffer cmd = define.getBuffer(id);

               if (cmd.findItem("DATA") == null) {
                  Buffer res = rowset.newInstance();
                  performOne(cmd, data, res);
                  data = res.getBuffer("DATA", null);
               }
               else {
                  performSubCommand(cmd, data);
               }
            }

            if (data == null) {
               rowset.removeItem(i); // remove this row
            }
            else {
               row.setValue(data); // modify this row
               row.setStatus(null);
               i++;
            }
         }
         current_row_no = null;
      }
   }

   //==========================================================================
   //  New
   //==========================================================================

   /**
    * Perform the Standard Procedure New. This includes the following steps:
    *<pre>
    *   o locate the Attribute sub-Buffer in the Command Buffer
    *   o copy attributes from the Attribute Buffer to a new Attribute String,
    *     but skip system attributes and attributes without value
    *   o execute PL/SQL procedure New__
    *   o copy all attributes from the returned Attribute String to the Attribute Buffer
    *   o copy OBJID and OBJVERSION from the OUT-variables to the Attribute Buffer
    *   o append the OUT-variable INFO to the Attribute Buffer
    *</pre>
    */
   protected void performNew(String method, Buffer command, Buffer attrbuf, Buffer result) throws Exception {
      String action, info, objid, objversion, attrstr, pkg, stmt;

      attrstr = attributeStringForNew(attrbuf);

      stmt = "BEGIN " + method + "(?,?,?,?,?); END;";
      action = command.getString("ACTION", "DO");

      Buffer var = command.newInstance();
      var.addItem(new Item("INFO", "S", "OUT", null));
      var.addItem(new Item("OBJID", "S", "OUT", null));
      var.addItem(new Item("OBJVERSION", "S", "OUT", null));
      var.addItem(new Item("ATTR", "S", "IN_OUT", attrstr));
      var.addItem(new Item("ACTION", "S", "IN", action));

      secureCall(stmt, var);

      info = var.getString(0);
      objid = var.getString(1);
      objversion = var.getString(2);
      attrstr = var.getString(3);

      if (!Str.isEmpty(attrstr) && attrstr.indexOf("__OBJSTATE") > -1) {
         //attrstr = attrstr.substring(0, attrstr.indexOf("__OBJSTATE"))+"OBJSTATE"+attrstr.substring(attrstr.indexOf("__OBJSTATE")+10);
         attrstr = Str.replace(attrstr, "__OBJSTATE", "OBJSTATE");
      }

      if (!Str.isEmpty(attrstr) && attrstr.indexOf("__OBJEVENTS") > -1) {
         //attrstr = attrstr.substring(0, attrstr.indexOf("__OBJEVENTS"))+"OBJEVENTS"+attrstr.substring(attrstr.indexOf("__OBJEVENTS")+11);
         attrstr = Str.replace(attrstr, "__OBJEVENTS", "OBJEVENTS");
      }

      clearStatus(attrbuf);
      attrbuf.setItem("OBJID", objid);
      attrbuf.setItem("OBJVERSION", objversion);

      fromAttributeString(attrstr, attrbuf);

      addInfo(attrbuf, info);
      //attrbuf.addItem("INFO__",info);
      result.addItem("DATA", attrbuf);
   }

   /**
    * Retrieve, from the given buffer, all simple items and format them as
    * an attribute string. Skip system attributes (OBJID, OBJVERSION, OBJSTATE),
    * "_DB"-attributes, attributes without value and "-"-marked attributes.
    */
   private String attributeStringForNew(Buffer attrbuf) throws Exception {
      AutoString attrstr = new AutoString();

      int count = attrbuf.countItems();

      for (int i = 0; i < count; i++) {
         Item item = attrbuf.getItem(i);
         if (item.isCompound())
            continue;

         String value = item.getString();
         if (value == null || value.equals(""))
            continue;

         if ("-".equals(item.getStatus()))
            continue;

         String name = item.getName();
         //if( name.endsWith("_DB") ) continue;
         if (name.equals("OBJID"))
            continue;
         if (name.equals("OBJVERSION"))
            continue;
         if (name.equals("OBJSTATE"))
            continue;

         attrstr.append(name);
         attrstr.append(IfsNames.fieldSeparator);
         attrstr.append(value);
         attrstr.append(IfsNames.recordSeparator);
      }
      return attrstr.toString();
   }

   /**
    * Move all attribute values found in the attribute string
    * into the specified buffer.
    */
   private void fromAttributeString(String attrstr, Buffer attrbuf) throws Exception {
      if (Str.isEmpty(attrstr))
         return;
      StringTokenizer st = new StringTokenizer(attrstr, "" + IfsNames.recordSeparator);
      while (st.hasMoreTokens()) {
         String field = st.nextToken();
         int pos = field.indexOf(IfsNames.fieldSeparator);
         if (pos < 0)
            throw new FndException("FNDPLSATTRS: Bad attribute string &1", attrstr);
         String name = field.substring(0, pos);
         pos++;
         String value = pos == field.length() ? null : field.substring(pos);
         attrbuf.setItem(name, value);
      }
   }

   private void clearStatus(Buffer buf) {
      BufferIterator iter = buf.iterator();
      while (iter.hasNext()) {
         Item item = iter.next();
         if (!item.isCompound())
            item.setStatus(null);
         //clearStatus(item.getBuffer());
      }
   }

   private void addInfo(Buffer buf, String info) {
      Item item = buf.getItem("INFO__", null);
      if (item == null)
         buf.addItem("INFO__", info);
      else
         item.setValue(item.getString() + info);
   }

   //==========================================================================
   //  Modify
   //==========================================================================

   /**
    * Perform the Standard Procedure Modify. This includes the following steps:
    *<pre>
    *   o locate the Attribute sub-Buffer in the Command Buffer
    *   o retrieve OBJID and OBJVERSION from the Attribute Buffer
    *   o copy "modified" attributes from the Attribute Buffer to a new
    *     Attribute String (skip system attributes)
    *   o execute PL/SQL procedure Modify__
    *   o copy OBJVERSION from the OUT-variable to the Attribute Buffer
    *   o copy all attributes from the Attribute String to the Attribute Buffer
    *   o append the OUT-variable INFO to the Attribute Buffer
    *</pre>
    */
   protected void performModify(String method, Buffer command, Buffer attrbuf, Buffer result) throws Exception {
      performStateMethod(method, command, attrbuf, result);
   }

   //==========================================================================
   //  State Method
   //==========================================================================

   /**
    * Perform a standard State changing procedure.
    * This includes the following steps:
    *<pre>
    *   o locate the Attribute sub-Buffer in the Command Buffer
    *   o retrieve OBJID and OBJVERSION from the Attribute Buffer
    *   o copy "modified" attributes from the Attribute Buffer to a new
    *     Attribute String (skip system attributes)
    *   o execute the specified PL/SQL State-procedure
    *   o copy OBJVERSION from the OUT-variable to the Attribute Buffer
    *   o copy all attributes from the Attribute String to the Attribute Buffer
    *   o append the OUT-variable INFO to the Attribute Buffer
    *</pre>
    */
   protected void performStateMethod(String method, Buffer command, Buffer attrbuf, Buffer result) throws Exception {
      String action, info, objid, objversion, attrstr, pkg, stmt;

      objid = attrbuf.getString("OBJID");
      objversion = attrbuf.getString("OBJVERSION");

      attrstr = attributeStringForModify(attrbuf);

      stmt = "BEGIN " + method + "(?,?,?,?,?); END;";
      action = command.getString("ACTION", "DO");

      Buffer var = command.newInstance();
      var.addItem(new Item("INFO", "S", "OUT", null));
      var.addItem(new Item("OBJID", "S", "IN", objid));
      var.addItem(new Item("OBJVERSION", "S", "IN_OUT", objversion));
      var.addItem(new Item("ATTR", "S", "IN_OUT", attrstr));
      var.addItem(new Item("ACTION", "S", "IN", action));

      secureCall(stmt, var);

      info = var.getString(0);
      objversion = var.getString(2);
      attrstr = var.getString(3);

      clearStatus(attrbuf);
      attrbuf.setItem("OBJVERSION", objversion);

      fromAttributeString(attrstr, attrbuf);

      addInfo(attrbuf, info);
      //attrbuf.addItem("INFO__",info);
      result.addItem("DATA", attrbuf);
   }

   /**
    * Retrieve, from the given buffer, all simple modified items and format them as
    * an attribute string. Skip system attributes (OBJID, OBJVERSION, OBJSTATE),
    * "_DB"-attributes and "-"-marked attributes.
    */
   private String attributeStringForModify(Buffer attrbuf) throws Exception {
      AutoString attrstr = new AutoString();
      int count = attrbuf.countItems();

      for (int i = 0; i < count; i++) {
         Item item = attrbuf.getItem(i);
         if (item.isCompound())
            continue;

         String status = item.getStatus();
         if (status == null || status.equals("-"))
            continue;

         String name = item.getName();
         //if( name.endsWith("_DB") ) continue;
         if (name.equals("OBJID"))
            continue;
         if (name.equals("OBJVERSION"))
            continue;
         if (name.equals("OBJSTATE"))
            continue;

         String value = item.getString();

         attrstr.append(name);
         attrstr.append(IfsNames.fieldSeparator);
         attrstr.append(value);
         attrstr.append(IfsNames.recordSeparator);
      }
      return attrstr.toString();
   }

   //==========================================================================
   //  Remove
   //==========================================================================

   /**
    * Perform the Standard Procedure Remove. This includes the following steps:
    *<pre>
    *   o locate the Attribute sub-Buffer in the Command Buffer
    *   o retrieve OBJID and OBJVERSION from the Attribute Buffer
    *   o execute PL/SQL procedure Remove__
    *</pre>
    */
   protected void performRemove(String method, Buffer command, Buffer attrbuf, Buffer result) throws Exception {
      String action, info, objid, objversion, pkg, stmt;

      objid = attrbuf.getString("OBJID");
      objversion = attrbuf.getString("OBJVERSION");

      stmt = "BEGIN " + method + "(?,?,?,?); END;";
      action = command.getString("ACTION", "DO");

      Buffer var = command.newInstance();
      var.addItem(new Item("INFO", "S", "OUT", null));
      var.addItem(new Item("OBJID", "S", "IN", objid));
      var.addItem(new Item("OBJVERSION", "S", "IN", objversion));
      var.addItem(new Item("ACTION", "S", "IN", action));

      secureCall(stmt, var);

      //info = var.getString(0);

      //attrbuf.addItem("INFO__",info);
      //result.addItem("DATA",attrbuf);
   }

   //==========================================================================
   //  Query
   //==========================================================================

   protected void performQuery(Buffer command, Buffer result) throws Exception {
      String select = command.getString("SELECT", null);
      String where = command.getString("WHERE", null);
      String group = command.getString("GROUP", null);
      String order = command.getString("ORDER", null);
      Buffer bind = command.getBuffer("DATA", null);
      String view = command.getString("VIEW", null);

      // Added by Terry 20120817
      // Get max_rows and skip_rows
      int max_rows = command.getInt("MAX_ROWS");
      int skip_rows = command.getInt("SKIP_ROWS", 0);
      // Added end
      
      if (Str.isEmpty(select))
         select = "*";

      AutoString stmt = new AutoString();
      if (Str.isEmpty(view))
         stmt.append(select);
      else
         stmt.append("select " + select + "\n" + "  from " + view);

      if (bind == null)
         bind = command.newInstance();

      if (!Str.isEmpty(where))
         stmt.append("\n where " + where);

      if (!Str.isEmpty(group))
         stmt.append("\n group by " + group);

      if (!Str.isEmpty(order))
         stmt.append("\n order by " + order);

      // Added by Terry 20120817
      // Transfer standard select SQL to partial read data.
      String three_stmt = stmt.toString();
      
      if (!"DUAL".equalsIgnoreCase(view) && !Str.isEmpty(view) && max_rows != 0 && select.toLowerCase().indexOf("count(") == -1)
      {
    	  String two_stmt = "SELECT three_stmt.*, rownum three_r_n " +
    	                    "FROM (" + three_stmt + ") three_stmt \n " + 
    	                    "WHERE rownum <= " + String.valueOf((max_rows + skip_rows));
    	  String one_stmt = "SELECT two_stmt.* \n " +
    	                    "FROM ( " + two_stmt + " ) two_stmt \n" +
    	                    "WHERE two_stmt.three_r_n > " + String.valueOf(skip_rows);
    	  three_stmt = one_stmt;
      }
      // Added end
      secureQuery(three_stmt, bind, command, result);
   }

   //==========================================================================
   //  Non-standard methods
   //==========================================================================

   /**
    * Perform a non-Standard procedure with "describe".
    * This includes the following steps:
    *<pre>
    *   o locate the Argument sub-Buffer in the Command Buffer
    *   o execute the specified PL/SQL procedure passing the Argument Buffer
    *     (which must contain all arguments of the specified procedure)
    *</pre>
    * If the specified command is a PL/SQL function then the result value
    * will be placed in the first item of the Argument Buffer.
    */
   protected void describeAndPerform(String method, Buffer command, Buffer argbuf, Buffer result) throws Exception {
      boolean function = "Y".equals(command.getString("FUNCTION", "N"));
      String returnName = null;

      if (function && argbuf.countItems() > 0) {
         Item item = argbuf.getItem(0);
         returnName = item.getName();
         item.setName("RETURN_VALUE");
      }

      secureDescribeAndCall(method, argbuf);

      if (function && argbuf.countItems() > 0) {
         Item item = argbuf.getItem(0);
         item.setName(returnName);
      }
      result.addItem("DATA", argbuf);
   }

   /**
    * Perform a non-Standard procedure without "describe".
    * This includes the following steps:
    *<pre>
    *   o locate the Argument sub-Buffer in the Command Buffer
    *   o execute the specified PL/SQL procedure passing the Argument Buffer
    *     (which must contain all arguments of the specified procedure;
    *      the order and type of items is important)
    *</pre>
    * If the specified command is a PL/SQL function then the result value
    * will be placed in the first item of the Argument Buffer.
    */
   protected void performAny(String method, Buffer command, Buffer argbuf, Buffer result) throws Exception {
      boolean function = "Y".equals(command.getString("FUNCTION", "N"));
      String category;

      String stmt = command.getString("CALL", method);
      int argcount = argbuf.countItems();

      category = command.getString("CATEGORY", null);
      boolean sqlCategory = "Sql".equals(category);

      // ronalk modified bugid=44051
      //      if( !stmt.startsWith("{") && argcount>0 && !sql_category)
      if( !stmt.startsWith("{") && !sqlCategory &&!isValidSQLBlockStart(stmt)) {
         AutoString auto = new AutoString();
         auto.append("BEGIN " + (function?"? := ":"") + method);
         for (int i = (function ? 1 : 0); i < argcount; i++) {
            if (i == (function ? 1 : 0))
               auto.append("(");

            if (i > (function ? 1 : 0))
               auto.append(",");
            auto.append("?");

            if (i == argcount - 1)
               auto.append(")");
         }
         auto.append("; END;");
         stmt = auto.toString();
      }

      // set direction to OUT for the result variable of a function call (required by DataDirect driver)
      if(function)
         argbuf.getItem(0).setStatus("OUT");

      if ("PostModify".equals(category))
         secureModifyPostCall(stmt, argbuf); //for New__ and Modify__
      else
         try {
            if (sqlCategory)
               stmt = PlsqlUtil.replace(stmt, "&AO.", context.getDbPrefix());
            secureCall(stmt, argbuf);
         }
         catch (Exception any) {
            String msg = any.getMessage();
            if (msg == null || msg.indexOf("ORA-01403") == -1)
               throw (any);
            else if (trace_on)
               context.debug(this, "Oracle Error ORA-01403: no data found ignored !!!");
         }

      result.addItem("DATA", argbuf);
   }


   /**
    * This method check the input statement has valid PLSQL block starting word.
    * if vaild return true else false.
    */
   private boolean isValidSQLBlockStart(String stmt) {
      String stmtDup=stmt;
      String[] keyWords = {"DECLARE","BEGIN"};
      stmtDup =stmtDup.replace('\n',' ').replace('\t',' ');
      StringTokenizer tokenizer= new StringTokenizer(stmtDup);
      if(tokenizer.hasMoreTokens()){
         String firstToken =tokenizer.nextToken();
         for(int i=0;i<keyWords.length;i++)
            if(keyWords[i].equalsIgnoreCase(firstToken))
               return true;
      }
      return false;
   }

   //==============================================================================
   //  Resolving references
   //==============================================================================

   /**
    * Replace all global references. The type and value of the referred item
    * is copied into the refering item.
    * If the referred item does not exist, then the type and value
    * of the referred item are set to null.
    */
   private void replaceGlobalReferences(Buffer buf) {
      Buffer globalBuf = context.getGlobalResponse();

      if (buf == null || globalBuf == null)
         return;

      BufferIterator iter = buf.iterator();
      while (iter.hasNext()) {
         Item item = iter.next();
         String value = item.getString();
         if (value == null)
            continue;

         if (value.startsWith("&RESPONSE/")) {
            String ref = value.substring(10);
            Item refitem = globalBuf.findItem(ref);
            if (refitem == null) {
               if (trace_on)
                  context.debug("GLOBAL-REF " + value + " not found.");
               item.setType(null);
               item.setValue(null);
            }
            else {
               if (trace_on)
                  context.debug("GLOBAL-REF " + value + " -> " + refitem);
               item.setType(refitem.getType());
               item.setValue(refitem.getValue());
            }
         }
      }
   }

   private boolean resolveInReferences(Buffer data, Buffer parentData, int[] outref) throws Exception {
      boolean inputChanged = false;

      for (int i = 0; i < outref.length; i++) {
         outref[i] = -1;
         Item item = data.getItem(i);
         String type = item.getType();
         if (!"R".equals(type))
            continue;

         String value = item.getString();
         String localref = Str.nvl(value, "");

         int position = -1;

         try {
            position = Str.toInt(localref);
         }
         catch (NumberFormatException fmt) {
            position = -1;
         }

         int itempos = position >= 0 ? position : parentData.getItemPosition(localref);

         String status = item.getStatus();
         if (status.startsWith("IN")) {
            Item refitem = parentData.getItem(itempos);
            String newType = refitem.getType();
            String newStatus = refitem.getStatus();
            String newValue = refitem.getString();

            item.setType(newType);
            //item.setStatus(new_status);   ????
            item.setValue(newValue);

            if ("*".equals(newStatus))
               inputChanged = true;

            if (trace_on)
               context.debug("[" + i + "] IN " + refitem);
         }

         if (status.endsWith("OUT")) {
            item.setType("S");
            outref[i] = itempos;
         }

         if (status.equals("OUT")) {
            Item refitem = parentData.getItem(itempos);
            String newType = refitem.getType();
            item.setName(value);
            item.setType(newType);
            item.setValue(null);
         }
      }
      return inputChanged;
   }

   private void resolveOutReferences(int[] outPos, Buffer data, Buffer parentData) throws Exception {
      for (int i = 0; i < outPos.length; i++) {
         int pos = outPos[i];
         if (pos < 0)
            continue;

         Item item = data.getItem(i);
         Item refitem = parentData.getItem(pos);

         String oldValue = refitem.getString();
         String newValue = item.getString();

         //         refitem.setType(item.getType());

         if (!Str.nvl(oldValue, "").equals(Str.nvl(newValue, ""))) {
            refitem.setValue(newValue);
            refitem.setStatus("*");
         }
         if (trace_on)
            context.debug("[" + i + "] OUT " + refitem);
      }
   }

   //==============================================================================
   //  Security Filter
   //==============================================================================

   private void secureCall(String plsql, Buffer bindvars) throws Exception {
      plsql = checkSqlText(plsql);
      context.call(plsql, bindvars);
   }

   private void secureDescribeAndCall(String procedure, Buffer args) throws Exception {
      throw new Exception("describeAndCall not implemented");
      //procedure = checkSqlText(procedure);
      //context.describeAndCall(procedure,args);
   }

   private void secureModifyPostCall(String plsql, Buffer bindvars) throws Exception {
      plsql = checkSqlQueryText(plsql);

      if (plsql.indexOf("{ ? = call null") < 0 && plsql.indexOf("BEGIN ? := null") < 0)
         context.call(plsql, bindvars);
      else
         bindvars.getItem(0).setValue(null); //replace null to return value
   }

   private void secureQuery(String sql, Buffer bindVariables, Buffer options, Buffer result) throws Exception {
      //sql = checkSqlText(sql);
      sql = checkSqlQueryText(sql); //Change for secure query
      context.query(sql, bindVariables, options, result);
   }

   private String checkSqlText(String text) throws Exception {
      SQLRecognizer st = new SQLRecognizer(text, context.getDbPrefix());

      text = st.getSQLText();

      //context.debug("\nsql:\n"+st.getSQLText());

      // If fndSecurityChecks is off then skip security checks on tables and procedures
      if(fndSecurityChecks) {
         String[] names = st.getTables();
         for (int i = 0; i < names.length; i++)
            security.checkDbTable(names[i]);

         names = st.getProcedures();
         for (int i = 0; i < names.length; i++)
            security.checkDbProcedure(names[i]);
      }

      return text;
   }

   private String checkSqlQueryText(String text) throws Exception {
      SQLRecognizer st = new SQLRecognizer(text, context.getDbPrefix());
      int removedChars = 0;
      text = st.getSQLText();
      SQLRecognizer.SQLProcedure[] procedures = st.getSQLProcedure();

      // If fndSecurityChecks is off then skip security checks on tables
      if(fndSecurityChecks) {
         String[] names = st.getTables();
         for (int i = 0; i < names.length; i++)
            security.checkDbTable(names[i]);
      }

      // Allways perform security checks on procedures - replace unavailable procedures with NULLs
      for (int i = 0; i < procedures.length; i++) {
         if (procedures[i].isInSelect() && !isProcedureAvailable(procedures[i])) {
            text =
               text.substring(0, procedures[i].getStart() - context.getDbPrefix().length() - removedChars)
                  + "null"
                  + text.substring(procedures[i].getEnd() - removedChars);
            removedChars += procedures[i].getEnd() - procedures[i].getStart() + context.getDbPrefix().length() - 4;
         }
      }
      return text;
   }

   /**
    * This function recursively fired untill all the procedural
    * parameters are availble or one of them is fail.
    **/

   private boolean isProcedureAvailable(SQLRecognizer.SQLProcedure procedure) {
      SQLRecognizer.SQLProcedure[] params;
      if (security.isDbProcedureAvailable(procedure.getName())) {
         params = procedure.getProceduralParams();
         for (int i = 0; i < params.length; i++)
            if (!isProcedureAvailable(params[i]))
               return false;
         return true;
      }
      else
         return false;
   }

   //   private void trace( String head, String[] arr )
   //   {
   //      context.debug(head+":");
   //      for( int i=0; i<arr.length; i++ )
   //         context.debug("   "+arr[i]);
   //
   //   }

}
