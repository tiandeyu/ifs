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
 * File        : ASPTransactionBuffer.java
 * Description : An ASPBuffer that represents a Request/Response Buffer
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-May-14 - Created
 *    Marek D  1998-May-26 - Intorduced block commands
 *    Marek D  1998-Jun-16 - Added check for duplicate command name
 *    Marek D  1998-Jun-25 - Added addEmptyQuery()
 *    Marek D  1998-Jul-03 - Added methods addCommand(name,ASPCommand)
 *                           addCommandAt(name,ASPCommand,position)
 *    Marek D  1998-Jul-10 - Better comments for addCustomCommand(...,ASPBlock)
 *    Marek D  1998-Jul-13 - Added method addQuery(command_name,sql_text)
 *    Marek D  1998-Jul-22 - Modified implementation details of SecurityQuery
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Marek D  1998-Aug-18 - Added methods get(clear)StandardInfoMessages()
 *    Marek D  1998-Nov-20 - Introduced APP.ID, APP.PASSWORD (ToDo #2845,2846)
 *    Marek D  1998-Dec-11 - Added commands for trace statistics (ToDo #3014)
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Marek D  1999-Feb-12 - Added type (T=Table, P=Procedure) to Security Query
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Mar-19 - Use methods ASPConfig.getApplicationId/Password()
 *    Marek D  1999-May-10 - Added method addConfigRequestHeader()
 *    Johan S  2000-Jan-03 - Added public method addClearMTSSecurityCache()
 *    Reine A  2000-Mar-29 - Added public method addPresentationObjectQuery()
 *    Jacek P  2001-Jan-19 - Added special handling of Web Kit Presentation Objects
 *                           in addPresentationObjectQuery().
 *    Jacek P  2001-Mar-06 - Changed name of header sub-buffer.
 *    Daniel S 2002-Jan-07 - Added systemname item.
 *    Jacek P  2002-Jan-31 - Added USER_AUTH_METHOD parameter to header in addRequestHeader()
 *    Johan S  2002-Aug-16 - added request header tests.
 *    Chandana 2002-Aug-30 - Added setSystemName method and done changes to SYSTEM_NAME in Header.
 *    Jacek P  2002-Sep-24 - Added clear() method
 *    Jacek P  2003-Feb-28 - Removed getSystemName().
 *    Ramila H 2003-jul-18 - Log id 1119, Authentication by fndext.
 *    Rifki R  2003-Aug-07 - Changed module name from 'WEBKIT' to 'FND' in addPresentationObjectQuery()
 *                           to support new pres. object definitions.
 *    Ramila H 2004-05-26  - implemented code for multi-language support
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2005/09/28 12:00:17  japase
 * Change access level for the HEADER constant 'package'
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import java.util.*;
import java.io.*;

/**
 * An extension of ASPBuffer containing methods for creation of commands
 * and queries. Typically a script will perform the following steps:
 *<pre>
 *   o create a new instance by calling ASPManager's newASPTransactionBuffer()
 *   o use add-methods to fill the buffer with named commands and queries
 *   o pass the buffer to the transaction server and retrieve a response buffer
 *   o use ASPBuffer's get-methods to retrieve the result items
 *</pre>
 */
public class ASPTransactionBuffer extends ASPBuffer
{
   static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPTransactionBuffer");

   static final String HEADER = "__APPHEADER";
   private String sys_name;

   ASPTransactionBuffer( ASPManager manager )
   {
      super(manager);
   }

   /*
   protected ASPBuffer construct()
   {
      return this;
   }*/

   /**
    * Clear the contents of this transaction buffer.
    */
   public void clear()
   {
      super.clear();
      sys_name = null;
   }

   //==========================================================================
   //  Request and Response Headers
   //==========================================================================

   /**
    * Create a new ASPBuffer. Insert it into this buffer as the first item.
    * Insert to this header some system information (like USER, SESSION or LANGUAGE)
    * required by the transaction server.
    * Return the reference to the newly created buffer.
    * @see     ifs.fnd.asp.ASPBuffer#getResponseHeader
    */
   public ASPBuffer addRequestHeader()
   {
      try
      {
         return addRequestHeader(getASPManager().getUserId());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   ASPBuffer addConfigRequestHeader() throws FndException
   {
      return addRequestHeader(getASPManager().getASPConfig().getConfigUser());
   }

   public ASPBuffer addRequestHeader( String user_id )
   {
      ASPManager manager = getASPManager();
      ASPConfig cfg = manager.getASPConfig();
      Buffer header = getBuffer().newInstance();

      String appid = cfg.getApplicationId();
      if( !Str.isEmpty(appid) )
      {
         header.addItem("APP.ID", appid);

         //String apppwd = cfg.getApplicationPassword();
         //if( !Str.isEmpty(apppwd) )
         //   header.addItem("APP.PASSWORD",apppwd);
      }

      header.addItem("USER",     user_id);
      //header.addItem("LANGUAGE", manager.getLanguageCode());
      header.addItem("LANGUAGE", manager.getUserLanguage());
      header.addItem("SESSION",  manager.getSessionId());
      //header.addItem("ACCESS_POINT",  manager.getASPConfig().getAccessPoint());
      // If the current request done under reauthentication we should add the
      // decision buffer with the password.
      if ("Y".equals(manager.readValue("__REAUTHENTICATION")))
         header.addItem("DECISION",manager.getDecisionBuffer());
      Item item = new Item(HEADER,header);
      getBuffer().insertItem(item,0);
      return newASPBuffer(header);
   }

   public void setSystemName(String sn)
   {
      sys_name = sn;
   }

   /**
    * Return the Response Header sub-buffer included in this ASPBuffer.
    * @see     ifs.fnd.asp.ASPBuffer#addRequestHeader
    */
   public ASPBuffer getResponseHeader()
   {
      try
      {
         ASPBuffer header = getBuffer(HEADER);
         if( header==null )
            throw new FndException("FNDTRBNORH: There is no Response Header in this ASPBuffer");
         return header;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   ASPBuffer getRequestHeader()
   {
      return getBuffer(HEADER);
   }

    /**
    * If the HEADER contains the element STATUS, it's
    * probably a response header, not a request header.
    * This method tries to determine whether the header
    * really is a request header. validRequestHeader() checks
    * if an intended request header has any errors.
    *
    */
   boolean hasRequestHeader()
   {
       ASPBuffer header = getBuffer(HEADER);
       if(header.itemExists("STATUS")) return false;
       return true;

   }

    /**
    * A request header should always contain a specific
    * set of elements. See also isRequestHeader().
    */
   boolean hasValidRequestHeader()
   {
       ASPBuffer header = getBuffer(HEADER);

       if(!header.itemExists("APP.ID")) return false;
       //if(!header.itemExists("APP.PASSWORD")) return false;
       if(!header.itemExists("USER")) return false;
       if(!header.itemExists("LANGUAGE")) return false;
       if(!header.itemExists("SESSION")) return false;
       //if(!header.itemExists("ACCESS_POINT")) return false;

       return true;
   }


   //==========================================================================
   //  Security Query
   //==========================================================================

   /**
    * Add the specified view name(s) to the Security Query command attached to
    * this ASPTransactionBuffer. For example:
    *<pre>
    *    buf.addSecurityQuery("DEMO_ORDER,DEMO_CUSTOMER");
    *</pre>
    * @see     ifs.fnd.asp.ASPTransactionBuffer#getSecurityInfo
    */
   public void addSecurityQuery( String view_name )
   {
      try
      {
         addSecurityQuery(view_name,null);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Add the specified method name(s) to the Security Query command attached
    * to this ASPTransactionBuffer. For example:
    *<pre>
    *    buf.addSecurityQuery("DEMO_ORDER_API","New__,Modify__,Exist");
    *</pre>
    * @see     ifs.fnd.asp.ASPTransactionBuffer#getSecurityInfo
    */
   public void addSecurityQuery( String package_name, String method_names )
   {
      try
      {
         ASPBuffer sec = getBuffer("__SECURITY");
         Buffer data = null;
         if( sec==null )
         {
            sec = addBuffer("__SECURITY");
            sec.addItem("METHOD","SecurityQuery");
            data = getBuffer().newInstance();
            sec.getBuffer().addItem("DATA",data);
         }
         else
            data = sec.getBuffer().getBuffer("DATA");

         if( method_names==null )
         {
            StringTokenizer st = new StringTokenizer(package_name,", \n\t\r");
            while( st.hasMoreTokens() )
               data.addItem(new Item(st.nextToken(),"T",null,null));  // T=Table
         }
         else
         {
            package_name = package_name + ".";
            StringTokenizer st = new StringTokenizer(method_names,", \n\t\r");
            while( st.hasMoreTokens() )
               data.addItem(new Item(package_name+st.nextToken(),"P",null,null)); // P=Procedure
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

  /**
    *Add one or more presentation object id(s) to the Security Query command
    *attached to this ASPTransactionBuffer. For example:
    *<pre>
    * buf.addPresentationObjectQuery("TIMREW/Clocking.page,TIMEREW/Timerap.page");
    *</pre>
    * @see     ifs.fnd.asp.ASPTransactionBuffer#getSecurityInfo
    */
   public void addPresentationObjectQuery(String pres_object_id)
   {
      try
      {
         ASPBuffer sec = getBuffer("__SECURITY");
         Buffer data = null;
         if( sec==null )
         {
            sec = addBuffer("__SECURITY");
            sec.addItem("METHOD","SecurityQuery");
            data = getBuffer().newInstance();
            sec.getBuffer().addItem("DATA",data);
         }
         else
            data = sec.getBuffer().getBuffer("DATA");

         StringTokenizer st = new StringTokenizer(pres_object_id,", \n\t\r");
         while(st.hasMoreTokens())
         {
            String nextToken = st.nextToken();
            if( nextToken.startsWith("FND/") || getASPManager().isPresentationObjectInstalled(nextToken))
               data.addItem(new Item(nextToken,"R",null,null)); //R=Presentation Object
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Return the Security Info sub-buffer included in this ASPBuffer.
    * The Security Info buffer contains all views and methods that have been
    * queried via addSecurityQuery() and have been passed all security
    * checks. Use itemExists() method to test the buffer contents, for example:
    *<pre>
    *    sec = buf.getSecurityInfo();
    *    if( sec.itemExists("DEMO_ORDER_API.New__") )
    *       ...
    *</pre>
    * @see     ifs.fnd.asp.ASPBuffer#addSecurityQuery
    * @see     ifs.fnd.asp.ASPBuffer#itemExists
    */
   public ASPBuffer getSecurityInfo()
   {
      try
      {
         ASPBuffer sec = getBuffer("__SECURITY");
         if( sec==null )
            throw new FndException("FNDTRBNOSECI: There is no security info in this transaction buffer");

         return sec.getBuffer("DATA");
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Standard INFO messages
   //==========================================================================

   /**
    * Retrieve all standard messages (items DATA/INFO__)
    * from every sub-buffer of this buffer.
    */
   String getStandardInfoMessages()
   {
      AutoString allinfo = new AutoString();

      BufferIterator trans_iter = getBuffer().iterator();
      while( trans_iter.hasNext() )
      {
         Item trans_item = trans_iter.next();
         if( !trans_item.isCompound() ) continue;

         BufferIterator cmd_iter = trans_item.getBuffer().iterator();
         while( cmd_iter.hasNext() )
         {
            Item cmd_item = cmd_iter.next();
            if( !cmd_item.isCompound() || !"DATA".equals(cmd_item.getName()) )
               continue;
            Buffer data = cmd_item.getBuffer();
            Item info_item = data.getItem("INFO__",null);
            String info = info_item==null ? null : info_item.getString();
            if( !Str.isEmpty(info) )
            {
               StringTokenizer rt = new StringTokenizer(info,""+IfsNames.recordSeparator);
               while( rt.hasMoreTokens() )
               {
                  String r = rt.nextToken();
                  StringTokenizer ft = new StringTokenizer(r,""+IfsNames.fieldSeparator);
                  if( "INFO".equals(ft.nextToken()) )
                  {
                     String f = ft.nextToken();
                     if( !Str.isEmpty(f) )
                     {
                        if( allinfo.length()>0 ) allinfo.append('\n');
                        allinfo.append(f);
                           }
                  }
               }
            }
         }
      }
      return allinfo.length()>0 ? allinfo.toString() : null;
   }


   /**
    * Remove all standard messages (items DATA/INFO__)
    * from every sub-buffer of this buffer.
    */
   void clearStandardInfoMessages()
   {
      BufferIterator trans_iter = getBuffer().iterator();
      while( trans_iter.hasNext() )
      {
         Item trans_item = trans_iter.next();
         if( !trans_item.isCompound() ) continue;

         BufferIterator cmd_iter = trans_item.getBuffer().iterator();
         while( cmd_iter.hasNext() )
         {
            Item cmd_item = cmd_iter.next();
            if( cmd_item.isCompound() && "DATA".equals(cmd_item.getName()) )
            cmd_item.getBuffer().removeItem("INFO__");
         }
      }
   }

   //==========================================================================
   //  Query
   //==========================================================================

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Query command to be performed by the server.
    * The arguments select_list, where_condition, and order_by should have
    * valid SQL syntax.
    */
   public ASPQuery addQuery( String command_name,
                             String view_name,
                             String select_list,
                             String where_condition,
                             String order_by )
   {
      try
      {
         checkCommandName(command_name);
         ASPQuery aspqry = getASPManager().newASPQuery();
         aspqry.define(view_name,select_list,where_condition,order_by);
         getBuffer().addItem(command_name,aspqry.getBuffer());
         return aspqry;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Query command to be performed by the server.
    * The specified SELECT statemenet must have valid SQL syntax.
    */
   public ASPQuery addQuery( String command_name, String sql_text )
   {
      try
      {
         checkCommandName(command_name);
         ASPQuery aspqry = getASPManager().newASPQuery();
         aspqry.define(sql_text);
         getBuffer().addItem(command_name,aspqry.getBuffer());
         return aspqry;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Query command to be performed by the transaction server.
    * The query will be based on the specified ASPBlock.
    * Values for all fields will be fetched. The returned rows will be
    * selected using restrictions build on every field that has a non empty
    * value in the Request object (1. QueryString 2. HTML Form).
    */
   public ASPQuery addQuery( String command_name,
                             String view_name,
                             ASPBlock block )
   {
      try
      {
         checkCommandName(command_name);
         ASPQuery aspqry = getASPManager().newASPQuery();
         aspqry.define(view_name,block);
         getBuffer().addItem(command_name,aspqry.getBuffer());
         return aspqry;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create a standard Query command based on the specified ASPBlock.
    * The block name will become the buffer name, the default view of
    * the block will be used.
    * Values for all fields will be fetched. The returned rows will be
    * selected using restrictions build on every field that has a non empty
    * value in the Request object (1. QueryString 2. HTML Form).
    */
   public ASPQuery addQuery( ASPBlock block )
   {
      try
      {
         checkCommandName(block.getName());
         ASPQuery aspqry = getASPManager().newASPQuery();
         aspqry.define(block.getView(),block);
         getBuffer().addItem(block.getName(),aspqry.getBuffer());
         return aspqry;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create a standard Query command based on the specified ASPBlock,
    * without any WHERE condition.
    * The block name will become the buffer name, the default view of
    * the block will be used, values for all fields will be fetched.
    */
   public ASPQuery addEmptyQuery( ASPBlock block )
   {
      try
      {
         checkCommandName(block.getName());
         ASPQuery aspqry = getASPManager().newASPQuery();
         aspqry.defineEmpty(block.getView(),block);
         getBuffer().addItem(block.getName(),aspqry.getBuffer());
         return aspqry;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Command
   //==========================================================================

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Standard Command to be performed by the transaction server.
    * A Standard Command is one of: New__, Modify__, Remove__ or a State
    * changing command like Invoice__ or Cancel__.
    * The DATA-subbuffer for the command will be created from the HTML fields
    * included in the specified ASPBlock.
    * The value of each field will be converted to the server format using the
    * field specific type and format mask.
    */
   public ASPCommand addCommand( String command_name,
                                 String method_name,
                                 ASPBlock block )
   {
      return addCommand( command_name, method_name, block, null );
   }

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Standard Command to be performed by the transaction server.
    * A Standard Command is one of: New__, Modify__, Remove__ or a State
    * changing command like Invoice__ or Cancel__.
    * The DATA-subbuffer for the command will be created from the HTML fields
    * included in the specified ASPBlock.
    * The argument old_data is a buffer containing old field values.
    * The server may need this information to properly perform the command.
    * The value of each field will be converted to the server format using the
    * field specific type and format mask.
    */
   public ASPCommand addCommand( String command_name,
                                 String method_name,
                                 ASPBlock block,
                                 ASPBuffer old_data )
   {
      try
      {
         checkCommandName(command_name);
         ASPCommand aspcmd = getASPManager().newASPCommand();
         aspcmd.define(method_name,block,old_data);
         getBuffer().addItem(command_name,aspcmd.getBuffer());
         return aspcmd;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Standard Command to be performed by the server.
    * A Standard Command is one of: New__, Modify__, Remove__ or a State
    * changing command like Invoice__ or Cancel__.
    * The DATA-subbuffer will contain empty items corresponding to the
    * specified ASPBlock.
    */
   public ASPCommand addEmptyCommand( String command_name,
                                      String method_name,
                                      ASPBlock block )
   {
      try
      {
         checkCommandName(command_name);
         ASPCommand aspcmd = getASPManager().newASPCommand();
         aspcmd.defineEmpty(method_name,block);
         getBuffer().addItem(command_name,aspcmd.getBuffer());
         return aspcmd;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a non-Standard or Custom Command to be performed by
    * the server. The DATA-subbuffer for the command will be created from the
    * HTML fields included in the specified ASPBlock.
    * The value of each field will be converted to the server format using the
    * field specific type and format mask.
    *<p>
    * The server will obtain, from the database, the meta-information for the
    * specified stored procedure and automatically retrieve all required
    * arguments from the DATA-buffer.
    *<p>
    * Note that you can improve performance
    * of a Custom Command by calling addCustomCommand(String,String) and then
    * explicitly specifying all arguments using addParameter() method.
    */
   public ASPCommand addCustomCommand( String command_name,
                                       String method_name,
                                       ASPBlock block )
   {
      try
      {
         checkCommandName(command_name);
         ASPCommand aspcmd = getASPManager().newASPCommand();
         aspcmd.defineCustom(method_name,block);
         getBuffer().addItem(command_name,aspcmd.getBuffer());
         return aspcmd;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a non-Standard (or Custom) Command to be performed by
    * the server. The DATA-buffer for the command must be created manually
    * by calling addParameter() method.
    */
   public ASPCommand addCustomCommand( String command_name,
                                       String method_name )
   {
      return addCustomCommand(command_name,method_name,null);
   }


   /**
    * Create and append to this buffer a sub-buffer with the specified name,
    * containing a Custom Command to be performed by the server.
    * The specified method must be a function. The specified into-field
    * will be placed as the first item in the DATA-buffer for this command.
    * The type of this ASPField must match the type of the return value from
    * the function.
    * The other parameters of the DATA-buffer must be created manually
    * by calling addParameter() method.
    */
   public ASPCommand addCustomFunction( String command_name,
                                        String method_name,
                                        String into_field_name )
   {
      try
      {
         checkCommandName(command_name);
         ASPCommand aspcmd = getASPManager().newASPCommand();
         aspcmd.defineCustomFunction(method_name,into_field_name);
         getBuffer().addItem(command_name,aspcmd.getBuffer());
         return aspcmd;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Append to this buffer a sub-buffer with the specified name,
    * containing the specified ASPCommand.
    */
   public void addCommand( String command_name, ASPCommand command )
   {
      try
      {
         getBuffer().addItem(command_name,command.getBuffer());
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Insert to this buffer at the specified position, a sub-buffer with
    * the specified name, containing the specified ASPCommand.
    */
   public void addCommandAt( String command_name, ASPCommand command, int position )
   {
      try
      {
         getBuffer().insertItem( new Item(command_name,command.getBuffer()),position );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   //==========================================================================
   //  Block Commands
   //==========================================================================

   /**
    * Append to this ASPTransactionBuffer all commands defined in the specified
    * ASPBlock combined with data from the corresponding ASPRowSet.
    * This method will be automatically invoked by the ASPManager's sumbit() method,
    * so call it manually only if you want to change the order of block commands
    * in the transaction buffer.
    *
    * @see ifs.fnd.asp.ASPBlock#defineCommand
    */
   public void generateBlockCommands( ASPBlock block )
   {
      try
      {
         String blkname = block.getName();
         Buffer def = block.getDefinedCommands();
         if( blkname==null || itemExists(blkname) ) return;
         ASPRowSet set = block.getASPRowSet();

         ASPCommand aspcmd = addCustomCommand( blkname, "PerRow" );
         Buffer cmd = aspcmd.getBuffer();
         cmd.removeItem(ASPRowSet.DATA);
         if( set.countEditedRows()+set.countMarkedRows() > 0 )
            cmd.addItem( "DEFINE", def);

         Buffer data = set.getRows().getBuffer();
         BufferIterator iter = data.iterator();
         while( iter.hasNext() )
         {
            Item item = iter.next();
            if( ASPRowSet.DATA.equals(item.getName()) )
               cmd.addItem(item);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Trace Statistics
   //==========================================================================

   /**
    * Append to this transaction buffer a command that will spool
    * trace event statistics.
    */
   public void addSpoolTraceStatisticsCommand()
   {
      try
      {
         ASPBuffer stat = addBuffer("__SPOOL_TRACE_STAT");
         stat.addItem("METHOD","SpoolTraceStatistics");
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Append to this transaction buffer a command that will clear
    * trace event statistics.
    */
   public void addClearTraceStatisticsCommand()
   {
      try
      {
         ASPBuffer stat = addBuffer("__CLEAR_TRACE_STAT");
         stat.addItem("METHOD","ClearTraceStatistics");
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   void addSetTraceStatisticsPrecision( int prec )
   {
      try
      {
         ASPBuffer stat = addBuffer("__SET_TRACE_STAT_PREC");
         stat.addItem("METHOD","SetTraceStatisticsPrecision");
         stat.addItem("PRECISION",""+prec);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   //==========================================================================
   //
   //==========================================================================

   /**
    * Append a command that will clear the MTS security cache.
    *
    */
   public void addClearMTSSecurityCache()
   {
      try
      {
         ASPBuffer stat = addBuffer("__CLEAR_MTS_SCACHE");
         stat.addItem("METHOD","ClearMTSSecurityCache");
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Throw an Exception, if this ASPTransactionBuffer already contains
    * a command with the specified name.
    */
   protected void checkCommandName( String name ) throws Exception
   {
      if( getBuffer().findItem(name) != null )
         throw new FndException("FNDTRBDUPCMD: Duplicate command name: &1", name);
   }

}
