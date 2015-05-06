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
 * File        : ASPCommand.java
 * Description : An ASPObject that represents a Command in a Request Buffer
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-May-13 - Created
 *    Marek D  1998-May-26 - Added setOption() method
 *    Marek D  1998-May-30 - Implemented ASPBufferable interface
 *    Marek D  1998-Jun-04 - No nested commands after Remove__
 *    Marek D  1998-Jun-16 - Introduced Local References
 *    Marek D  1998-Jul-10 - Better comments for defineCustom(...,ASPBlock)
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Marek D  1998-Dec-10 - Use Db-Names in POST-commands for computable fields
 *                           by calling getFunctionDbParameters() (Bug #3005)
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPManagedObject instead of ASPObject
 *    Jacek P  1999-Mar-01 - Abstract class ASPManagedObject replaced with
 *                           an empty interface ASPManageable. All code moved
 *                           back to ASPObject.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-May-05 - Added method addParameter(name,type,status,value)
 *    Marek D  1999-Jun-03 - Removed extra decode/encode commands for
 *                           RadioButtons from addStandardNestedCommands()
 *    Jacek P  2000-Feb-01 - Added overloaded versions of functions:
 *                           defineCustomFunction(), addParameter(), setParameter(),
 *                           addReference() and addLocalReference()
 *                           that takes an instance of ASPPage as first parameter.
 *                           Needed for portal implementation.
 *    Jacek P  2000-Apr-19 - Added functions addInParameter()
 *    Jacek P  2000-Dec-20 - Added functions addInReference()
 *    Jacek P  2002-Sep-24 - Added methods clear() and new version of construct().
 *                           Call to mainbuf.clear() replaced with the new clear();
 *   Chandana D2003-Mar-17 - Added setItem(String name, String value) method.
 *   Ramila H  2004-11-09  - implemented setting global variable support for standard portlets.
 *                           Changed private addParameter to public.
 *   Suneth M  2009-May-26 - Bug 83161, Added new method addOutParameter().
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import java.util.*;

/**
 * An instance of this class represents a command in a Transaction Buffer.
 * A command contains the following parts:
 *<pre>
 *   method      - name of the PL/SQL procedure or function to be performed
 *                 by the transaction server
 *   DATA buffer - IN, IN/OUT or OUT arguments (parameters) to the procedure
 *   options     - extra parameters that are not included in the DATA buffer
 *</pre>
 */
public class ASPCommand extends ASPObject implements ASPBufferable, ASPManageable
{
   static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPCommand");

   protected Buffer mainbuf;

   // Modified by Terry 20130913
   // Change visibility
   // Original:
   // ASPCommand( ASPManager manager )
   public ASPCommand( ASPManager manager )
   {
      super(manager);
   }

   // Modified by Terry 20130913
   // Change visibility
   // Original:
   // protected ASPCommand construct()
   public ASPCommand construct()
   {
      mainbuf = getASPManager().getFactory().getBuffer();
      return this;
   }

   protected ASPCommand construct( Buffer buf )
   {
      this.mainbuf = buf;
      return this;
   }

   /**
    * Clear this instance of the ASPCommand class. The same instance can be then resused
    * during subsequent calls. Note however that the same instance cannot be used more
    * then once during creation of one transaction buffer containing several command/query
    * definitions - each command/query has to have it's own instance of this class.
    */
   public void clear()
   {
      mainbuf.clear();
   }
   
   //==========================================================================
   //  ASPBufferable interface
   //==========================================================================

   /**
    * Store the internal state of this ASPCommand in a specified ASPBuffer
    */
   public void save( ASPBuffer intobuf )
   {
      try
      {
         Buffer into = intobuf.getBuffer();
         saveClass(into);
         Buffers.save( into, "MAINBUF", mainbuf );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Retrieve the internal state of this ASPCommand from a specified ASPBuffer
    */
   public void load( ASPBuffer frombuf )
   {
      try
      {
         Buffer from = frombuf.getBuffer();
         mainbuf = Buffers.loadBuffer( from, "MAINBUF" );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Direction of commnad arguments
   //==========================================================================

   /**
    * The constant representing IN direction of a parameter
    */
   public final String IN     = "IN";

   /**
    * The constant representing OUT direction of a parameter
    */
   public final String OUT    = "OUT";

   /**
    * The constant representing IN OUT direction of a parameter
    */
   public final String IN_OUT = "IN_OUT";

   //==========================================================================
   //  Timing of nested commands
   //==========================================================================

   final String PRE    = "PRE";
   final String POST   = "POST";

   //==========================================================================
   //
   //==========================================================================

   protected Buffer getBuffer()
   {
      return mainbuf;
   }

   //==========================================================================
   //
   //==========================================================================

   /**
    * Define this ASPCommand as a Standard Command having the specified name.
    * A Standard Command is one of: New__, Modify__, Remove__ or a State
    * changing command like Invoice__ or Cancel__.
    * The DATA-subbuffer for the command will be created from the HTML fields
    * included in the specified ASPBlock.
    * The value of each field will be converted to the server format using the
    * field specific type and format mask.
    */
   public void define( String method_name, ASPBlock block )
   {
      try
      {
         define( method_name, block, null );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Define this ASPCommand as a Standard Command having the specified name.
    * A Standard Command is one of: New__, Modify__, Remove__ or a State
    * changing command like Invoice__ or Cancel__.
    * The DATA-subbuffer for the command will be created from the HTML fields
    * included in the specified ASPBlock.
    * The argument old_data is a buffer containing old field values.
    * The server may need this information to properly perform the command.
    * The value of each field will be converted to the server format using the
    * field specific type and format mask.
    */
   public void define( String method_name, ASPBlock block, ASPBuffer old_data )
   {
      try
      {
         clear();
         mainbuf.addItem( "METHOD", method_name );
         ASPBuffer aspdata = (new ASPBuffer(getASPManager())).construct(mainbuf.newInstance());

         block.readToBuffer(aspdata,old_data);

         mainbuf.addItem("DATA", aspdata.getBuffer() );
         mainbuf.addItem("CATEGORY","Standard");

         if( !method_name.endsWith(".Remove__") )
         {
            boolean force_nested_commands = old_data==null;
            addStandardNestedCommands(block,force_nested_commands);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPCommand as a Standard Command having the specified name.
    * The DATA-subbuffer will contain null values and datatypes from
    * the specified ASPBlock.
    */
   public void defineEmpty( String method_name, ASPBlock block )
   {
      if(DEBUG) debug(this+".defineEmpty("+method_name+","+block+")");
      try
      {
         clear();
         mainbuf.addItem( "METHOD", method_name );
         ASPBuffer aspdata = (new ASPBuffer(getASPManager())).construct(mainbuf.newInstance());

         block.addToBuffer(aspdata,null);

         mainbuf.addItem("DATA", aspdata.getBuffer() );
         mainbuf.addItem("CATEGORY","Standard");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPCommand as a non-Standard or Custom Command having
    * the specified name.
    * The DATA-subbuffer for the command will be created from the
    * HTML fields included in the specified ASPBlock.
    * The value of each field will be converted to the server format using the
    * field specific type and format mask.
    *<p>
    * The server will obtain from the database the meta-information for the
    * specified stored procedure and automatically retrieve all required
    * arguments from the DATA-buffer.
    *<p>
    * Note that you can improve performance
    * of a Custom Command by calling defineCustom(String) and then
    * explicitly specifying all arguments using addParameter() method.
    */
   public void defineCustom( String method_name, ASPBlock block )
   {
      try
      {
         clear();
         mainbuf.addItem( "METHOD", method_name );
         ASPBuffer aspdata = (new ASPBuffer(getASPManager())).construct(mainbuf.newInstance());

         if( block!=null )
         {
            block.readToBuffer(aspdata,null);
            mainbuf.addItem("CATEGORY","Describe");
         }
         mainbuf.addItem( "DATA", aspdata.getBuffer() );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPCommand as a Custom Command having the specified name.
    * The specified method must be a procedure.
    * The DATA-buffer for the command must be created manually,
    * by calling addParameter(), addReference() or addLocalReference() methods.
    */
   public void defineCustom( String method_name )
   {
      defineCustom(method_name,null);
   }


   /**
    * Define this ASPCommand to be a Custom Function with the specified
    * method name.
    * The DATA-buffer for the command must be created manually,
    * by calling addParameter(), addReference() or addLocalReference() methods.
    * The first item in the DATA-buffer must be an OUT parameter (return value).
    */
   public void defineCustomFunction( String method_name )
   {
      try
      {
         defineCustom(method_name,null);
         mainbuf.addItem("FUNCTION","Y");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define this ASPCommand as a Custom Command having the specified name.
    * The specified method must be a function. The specified into-field
    * will be placed as the first item in the DATA-buffer for this command.
    * The type of this ASPField must match the type of the return value from
    * the function.
    * The other parameters of the DATA-buffer must be created manually
    * by calling addParameter() method.
    */
   public void defineCustomFunction( String method_name, String into_field_name )
   {
      defineCustomFunction( getASPManager().getASPPage(), method_name, into_field_name );
   }


   public void defineCustomFunction( ASPPage page, String method_name, String into_field_name )
   {
      try
      {
         defineCustom(method_name,null);
         mainbuf.addItem("FUNCTION","Y");
         Buffer data = mainbuf.getBuffer("DATA");
         ASPField into = page.getASPField(into_field_name);
         into.readToBuffer( data, null );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Set the specified option for this ASPCommand. For example:
    *<pre>
    *   cmd.addOption("ACTION","PREPARE");
    *</pre>
    */
   public void setOption( String name, String value )
   {
      try
      {
         mainbuf.setItem(name,value);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Append the specified parameter(s) to the DATA-subbuffer of this ASPCommand.
    * Each parameter will be based on  the corresponding ASPField.
    * The value of the parameter will be fetched from the Request object
    * (fitst from the Query String, then from the HTML Form).
    * Note that the order and type of parameters
    * must match the order and type of the arguments for this command's method.
    */
   public void addParameter( String field_names )
   {
      addParameters( getASPManager().getASPPage(), field_names );
   }


   public void addParameters( ASPPage page, String field_names )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");

         StringTokenizer st = new StringTokenizer(field_names,", \n\r\t");
         while( st.hasMoreTokens() )
         {
            String name = st.nextToken();
            ASPField field = page.getASPField(name);
            field.readToBuffer( data, null );
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Append one item to the DATA-subbuffer of this ASPCommand.
    * The item will inherit the name from the specified ASPField's DbName.
    * The specified value will be converted to the server format using the
    * field's type and format mask.
    */
   public void addParameter( String field_name, String value )
   {
      addParameter( getASPManager().getASPPage(), field_name, value );
   }


   /**
    * A version of the addParameter() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.addParameter
    */
   public void addParameter( ASPPage page, String field_name, String value )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");
         ASPField field = page.getASPField(field_name);
         field.convertToBuffer(data,value,null);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Sets a new Item.
    * @param name Name of the item
    * @param value Value of the item
    */
   public void setItem(String name, String value)
   {
      try
      {
         mainbuf.addItem(name,value);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Append one item of type IN to the DATA-subbuffer of this ASPCommand.
    * The item will inherit the name from the specified ASPField's DbName.
    * The specified value will be converted to the server format using the
    * field's type and format mask.
    */
   public void addInParameter( String field_name, String value )
   {
      addInParameter( getASPManager().getASPPage(), field_name, value );
   }


   /**
    * A version of the addInParameter() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.addInParameter
    */
   public void addInParameter( ASPPage page, String field_name, String value )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");
         ASPField field = page.getASPField(field_name);
         field.convertToBuffer(data,value,null);
         data.getItem(field.getDbName()).setStatus("IN");
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /**
    * Append one item of type OUT to the DATA-subbuffer of this ASPCommand.
    * The item will inherit the name from the specified ASPField's DbName.
    * The specified value will be converted to the server format using the
    * field's type and format mask.
    */
   public void addOutParameter( String field_name, String value )
   {
      addOutParameter( getASPManager().getASPPage(), field_name, value );
   }


   /**
    * A version of the addOutParameter() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.addOutParameter
    */
   public void addOutParameter( ASPPage page, String field_name, String value )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");
         ASPField field = page.getASPField(field_name);
         field.convertToBuffer(data,value,null);
         data.getItem(field.getDbName()).setStatus("OUT");
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   public void addParameter( String name, String type, String status, Object server_value )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");
         // type = String.valueOf(DataFormatter.getBaseTypeMarker(DataFormatter.getTypeId(type)));
         data.addItem( new Item(name,type,status,server_value) );
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Set the value of an existing parameter in the DATA-subbuffer of this ASPCommand.
    * The specified value will be converted to the server format using the
    * field's type and format mask.
    */
   public void setParameter( String field_name, String value )
   {
      setParameter( getASPManager().getASPPage(), field_name, value );
   }


   /**
    * A version of the setParameter() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.setParameter
    */
   public void setParameter( ASPPage page, String field_name, String value )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");
         ASPField field = page.getASPField(field_name);
         field.convertInBuffer(data,value);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Append to the DATA-subbuffer of this ASPCommand one or more
    * new items. The type and value of each item will be fetched from the
    * matching item in the response buffer, by the server, just before the
    * execution of this command.
    * For example:
    *<pre>
    *    cmd.addReference("COMPANY_ID,CUSTOMER_ID","REF/DATA");
    *</pre>
    * creates two items "COMPANY_ID" and "CUSTOMER_ID". They will get the type
    * and value from the response buffer's items "REF/DATA/COMPANY_ID" and
    * "REF/DATA/CUSTOMER_ID" respectively.
    */
   public void addReference( String item_names, String refered_buffer_name )
   {
      //addReference(item_names,refered_buffer_name,item_names);
      addReference( getASPManager().getASPPage(),
                    item_names, refered_buffer_name, item_names, null );
   }

   /**
    * Append to the DATA-subbuffer of this ASPCommand one or more
    * new items of IN-direction. The type and value of each item will be fetched from the
    * matching item in the response buffer, by the server, just before the
    * execution of this command.
    * For example:
    *<pre>
    *    cmd.addInReference("COMPANY_ID,CUSTOMER_ID","REF/DATA");
    *</pre>
    * creates two items "COMPANY_ID" and "CUSTOMER_ID". They will get the type
    * and value from the response buffer's items "REF/DATA/COMPANY_ID" and
    * "REF/DATA/CUSTOMER_ID" respectively.
    */
   public void addInReference( String item_names, String refered_buffer_name )
   {
      addReference( getASPManager().getASPPage(),
                    item_names, refered_buffer_name, item_names, "IN" );
   }

   /**
    * Append to the DATA-subbuffer of this ASPCommand one or more
    * new items. The type and value of each item will be fetched from the
    * response buffer, by the server, just before the execution of this command.
    * For example:
    *<pre>
    *    cmd.addReference("ORDER_ID","REF/DATA","REF_ORDER_ID");
    *</pre>
    * creates an item named "ORDER_ID" that will get the type and value
    * from the response buffer's item "MASTER/DATA/ORDER_ID".
    *<p>
    * Note! In the above example the ASPField REF_ORDER_ID in the block REF
    * has the DbName set to ORDER_ID.
    */
   public void addReference( String item_names,
                             String refered_buffer_name,
                             String refered_item_names )
   {
      addReference( getASPManager().getASPPage(),
                    item_names, refered_buffer_name, refered_item_names, null );
   }

   /**
    * Append to the DATA-subbuffer of this ASPCommand one or more
    * new items of IN-direction. The type and value of each item will be fetched from the
    * response buffer, by the server, just before the execution of this command.
    * For example:
    *<pre>
    *    cmd.addInReference("ORDER_ID","REF/DATA","REF_ORDER_ID");
    *</pre>
    * creates an item named "ORDER_ID" that will get the type and value
    * from the response buffer's item "MASTER/DATA/ORDER_ID".
    *<p>
    * Note! In the above example the ASPField REF_ORDER_ID in the block REF
    * has the DbName set to ORDER_ID.
    */
   public void addInReference( String item_names,
                               String refered_buffer_name,
                               String refered_item_names )
   {
      addReference( getASPManager().getASPPage(),
                    item_names, refered_buffer_name, refered_item_names, "IN" );
   }


   /**
    * A version of the addReference() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.addReference
    */
   public void addReference( ASPPage page,
                             String item_names,
                             String refered_buffer_name,
                             String refered_item_names )
   {
      addReference( page, item_names, refered_buffer_name, refered_item_names, null );
   }

   /**
    * A version of the addInReference() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.addInReference
    */
   public void addInReference( ASPPage page,
                               String item_names,
                               String refered_buffer_name,
                               String refered_item_names )
   {
      addReference( page, item_names, refered_buffer_name, refered_item_names, "IN" );
   }

   private void addReference( ASPPage page,
                             String item_names,
                             String refered_buffer_name,
                             String refered_item_names,
                             String direction )
   {
      try
      {
         Buffer data = mainbuf.getBuffer("DATA");

         ASPManager mgr = getASPManager();
         DataFormatter fmt = mgr.getDataFormatter(DataFormatter.STRING,null);
         ServerFormatter srvfmt = mgr.getServerFormatter();

         StringTokenizer items    = new StringTokenizer(item_names," ,\n\t\r");
         StringTokenizer refitems = new StringTokenizer(refered_item_names," ,\n\t\r");
         while( items.hasMoreTokens() )
         {
            String name    = page.getASPField(items.nextToken()).getDbName();    // DB-NAME
            String refname = page.getASPField(refitems.nextToken()).getDbName(); // DB-NAME
            String value = "&RESPONSE/" + refered_buffer_name + "/" + refname;
            Item item = ASPBuffer.convertToServerItem(name, value, srvfmt, fmt, null);
            if(!Str.isEmpty(direction))
               item.setStatus(direction);
            data.addItem(item);
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Append to the DATA-subbuffer of this ASPCommand one or more new arguments.
    * Each argument will be a local reference to the corresponding ASPField from the
    * specified list. All local references will be resolved by the transaction server,
    * just before the execution of the command, in the context of every DATA row
    * marked with an ID, which points to this ASPCommand.
    *
    * The argument direction (common for all fields),
    * must have one of the following values:
    *<pre>
    *    "IN"
    *    "OUT"
    *    "IN_OUT"
    *</pre>
    * For example:
    *<pre>
    *    cmd.addLocalReference("QTY_SUM",cmd.OUT);
    *    cmd.addLocalReference("COMPANY_ID,ORDER_ID",cmd.IN);
    *</pre>
    *
    * @see ifs.fnd.asp.ASPBlock.defineCustomCommand
    */
   public void addLocalReference( String field_names, String direction )
   {
      addLocalReference( getASPManager().getASPPage(), field_names, direction );
   }


   /**
    * A version of the addLocalReference() method dedicated for usage in portlets.
    *
    * @see ifs.fnd.asp.ASPCommand.addLocalReference
    */
   public void addLocalReference( ASPPage page, String field_names, String direction )
   {
      try
      {
         if( !IN.equals(direction) && !OUT.equals(direction) && !IN_OUT.equals(direction) )
            throw new FndException("FNDCMDINVDIR: Invalid argument direction: &1", direction);

         Buffer data = mainbuf.getBuffer("DATA");

         StringTokenizer fields = new StringTokenizer(field_names," ,\n\t\r");
         while( fields.hasMoreTokens() )
         {
            String dbname = page.getASPField(fields.nextToken()).getDbName();
            data.addItem(new Item(null,"R",direction,dbname));
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Nested commands
   //==========================================================================

   /**
    * Attach the specified ASPCommand to this ASPCommand, as a nested command.
    * PRE-commands are executed in the context of the parent DATA-buffer,
    * before the parent command.
    * A nested command may contain local references to the parent DATA-buffer.
    */
   public void addPreCommand( ASPCommand subcmd )
   {
      addSubCommand(subcmd,PRE);
   }

   /**
    * Attach the specified ASPCommand to this ASPCommand, as a nested command.
    * POST-commands are executed in the context of the parent DATA-buffer,
    * after the parent command.
    * A nested command may contain local references to the parent DATA-buffer.
    */
   public void addPostCommand( ASPCommand subcmd )
   {
      addSubCommand(subcmd,POST);
   }

   private void addSubCommand( ASPCommand subcmd, String when )
   {
      try
      {
         mainbuf.addItem(new Item("COMMAND",null,when,subcmd.getBuffer()));
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Append to this ASPCommand all standard sub-commands that implement
    * standard functionality (like setFunction) defined for each
    * ASPField from the specified ASPBlock.
    * If the specified Force flag is TRUE then all defined nested commands
    * will be created on the Client and executed by the server.
    * If the flag is FALSE then the creation (on client) and execution
    * (on server) of nested commands will depend on status of IN-arguments.
    */
   void addStandardNestedCommands( ASPBlock block,
                                   boolean force ) throws Exception
   {
      ASPField[] fields = block.getFields();

      for( int i=0; i<fields.length; i++ )
      {
         ASPField f = fields[i];
         String name = f.getName();
         String in_params;
         if( f.isComputable() && !Str.isEmpty(in_params=f.getFunctionDbParameters()) )
         {
            addNestedFunction( "POST",
                               f.getCallExpression(),
                               name,
                               in_params,
                               force );
         }
         //else if( f.isRadioButtons() )
         //{
         //   String pkg = f.getIidPackage();
         //   String name_db = name + "_DB";
         //
         //   if( addNestedFunction( PRE,
         //                          "{ ? = call "+pkg+".Decode(?) }",
         //                          name,
         //                          name_db,
         //                          force ) )
         //   {
         //      addNestedFunction( POST,
         //                         "{ ? = call "+pkg+".Encode(?) }",
         //                         name_db,
         //                         name,
         //                         true );
         //   }
         //}
      }
   }


   private boolean addNestedFunction( String when,      // "PRE" or "POST"
                                      String method,
                                      String into,
                                      String in_params,
                                      boolean force ) throws Exception
   {
      Buffer parentdata = mainbuf.getBuffer("DATA",null);
      Buffer data = mainbuf.newInstance();
      data.addItem(new Item(null,"R","OUT",into));

      boolean input_modified = false;

      StringTokenizer st = new StringTokenizer(in_params,", \r\n\t");
      while( st.hasMoreTokens() )
      {
         String param = st.nextToken();
         data.addItem(new Item(null,"R","IN",param));
         if( !force && parentdata!=null )
         {
            Item initem = parentdata.getItem(param);
            //debug("in-item="+initem);
            if( initem.getStatus()!=null )
              input_modified = true;
         }
      }

      if( !force && !input_modified ) return false;

      Buffer sub = mainbuf.newInstance();
      sub.addItem("METHOD",method);
      sub.addItem("FUNCTION","Y");
      if( force ) sub.addItem("FORCE","Y");
      sub.addItem( "DATA", data );
      mainbuf.addItem(new Item("COMMAND",null,when,sub));
      return true;
   }
}
