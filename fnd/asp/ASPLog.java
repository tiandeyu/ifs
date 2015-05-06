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
 * File        : ASPLog.java
 * Description : Handling of log and trace print-outs
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-Feb-04 - Created
 *    Jacek P  1998-May-08 - Calls to function findAttribute() in ASPConfig
 *                           replaced with getParameter(). Function error()
 *                           raises now a runtime exception.
 *    Jacek P  1998-Jul-23 - New way of exception handling:
 *                           - redesigned method error().
 *                           - added inner class AbortException
 *    Jacek P  1998-Aug-10 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-14 - Exception AbortException should not be logged in
 *                           logError() procedure.
 *    Jacek P  1998-Aug-20 - Configuration file ASPConfig.ifm restructured
 *                           (Log id:#2623)
 *    Jacek P  1998-Aug-27 - Deleted opening of the log file. Log and trace
 *                           print outs redirect to DBMON console if no path
 *                           defined in the configuration file.
 *    Marek D  1998-Sep-29 - Modified formatting of error messages from PL/SQL
 *    Jacek P  1998-Oct-20 - Added error check while formatting HTML error page.
 *                           Write to browser only on top level exception.
 *    Jacek P  1998-Oct-22 - Added function debug(). Replaced all calls to
 *                           Util.debug() with calls to this new debug().
 *    Jacek P  1999-Feb-09 - Support of double bytes characters: added call to
 *                           HTMLEncode() function for all Oracle error
 *                           messages (Bug #3135).
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *                           Removed calls to Scr class.
 *    Jacek P  1999-Feb-19 - Implemented ASPPoolable.
 *    Jacek P  1999-Mar-01 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement.
 *    Jacek P  1999-Mar-10 - References to the 'config' variable replaced
 *                           with a method call.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Mar-18 - Simplified implementation of put() and trace()
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-May-18 - Added call to ASPContext.rollbackContext() to
 *                           preserve the removed context.
 *    Jacek P  1999-Jun-14 - Better error logging. Added functionality for
 *                           fetching of row number.
 *    Jacek P  2000-May-31 - Added handling of additional error message.
 *    Johan S  2000-Jul-24 - Moved HTMLEncode to ASPHTMLFormatter.
 *    Jacek P  2000-Jul-26 - Added new version of logError() that prints
 *                           output to an AutoString variabel. Added handling
 *                           of portal (id 226).
 *    Jacek P  2000-Aug-08 - Added function dumpStack()
 *    Jacek P  2000-Aug-14 - Added handling of additional message for portlets.
 *    Jacek P  2000-Oct-04 - Added methods disable/enableAdditionalErrorMsg()
 *                           for control of the 'More info' link in portlet (Log id #370).
 *    Piotr Z  2001-Feb-21 - Added handling of ASPManager.ResponseEndException in error() method.
 *    Jacek P  2001-Mar-23 - Log id #672: Added overloaded logError() which takes a buffer
 *                           and fills with info. Added ExtendedAbortException exception.
 *    Jacek P  2001-Mar-28 - Problem with recursive call. Changes in exepction handling.
 *    Jacek P  2001-Apr-11 - Modification of debug print outs.
 *    Jacek P  2001-May-11 - Log id #709: do not write to response if info!=null in logError().
 *    Chaminda 2001-May-11 - Log id #717: Path to log files are converted to lower case.
 *                           Removed toLowerCase in file name line 445 and 483.
 *    Jacek P  2003-Mar-03 - Added call to convertPath() when obtaining paths for log and trace.
 *    Sampath  2003-Jun-16-  made the more-info message size in portlet errors to a limited size and
 *                           also encoded the message to support quotes.
 *    Jacek P  2003-Sep-04 - Corrected handling of the row no indication in error messages.
 *    Mangala  2003-Dec-24 - Fixed bug 40929. Remove the word PANIC from error messages when log is missing.
 *    ChandanaD2004-Apr-23 - Fixed bug 43162. Assigend the error message to __ERR_MSG javascript variable.
 *    Jacek P  2004-Apr-24 - Adding logging of errors to the Alert file.
 *                           This is a temporary solution - all output is sent to Alert.
 *                           Better handling of trace.
 *    Ramila H 2004-11-04  - Changed __ERR_MSG to support portlets in standard portal mode.
 *    Mangala  2005-12-13  - ResponseEndException no longer writes to the log.
 *    prralk   2006-02-16  - B130888 - Remove row number from showing in error messages when multirow delete.
 *
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010-08-20 Bug 92540 Amiklk : Changed formatLogErrMsg() to not to write ManualDecisionException
 * 2006-09-11    gegulk
 * Bug 60225 HTMLEncoded the variables message & addmsg inside the method logError()
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;
import java.text.*;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

// import aspcomp.*;
// import com.ms.com.*;


/**
 * Common class for ASP objects that handles log and trace files.
 *
 * The name of the log and trace file consists of domain id, user id,
 * session id and call counter.
 *
 * The directory path to the log file is defined by AUDIT/LOG/PATH entry in
 * ASP configuration file. If no log path is defined then the log output will
 * be directed to DBMON console.
 *
 * The directory path to the trace file is defined by AUDIT/TRACE/PATH entry
 * in ASP configuration file. If no trace path is defined then the trace output
 * will be directed to DBMON console.
 *
 * The AUDIT/LOG/ENABLED and AUDIT/TRACE/ENABLED entries in the config file are
 * used to enable or disable log and trace print-outs.
 * The valid values for these entries are "Y" and "N".
 */
public class ASPLog extends ASPPageElement
{
   //==========================================================================
   //  Immutable attributes - late initialization
   //==========================================================================

   private static String  log_path;
   private static String  trace_path;

   private static Boolean log_enabled;
   private static Boolean trace_enabled;
   private static Boolean log_user_browser;

   //==========================================================================
   //  Transient attributes - created in activate()
   //==========================================================================

   private boolean closed;

   //==========================================================================
   //  Transient attributes - late initialization
   //==========================================================================

   private PrintWriter log_file;
   private PrintWriter trace_file;

   private final DateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private       Date       date           = new Date();
   private       AutoString out            = new AutoString();

   //==========================================================================
   //  Static variables
   //==========================================================================

   public  static final String CRLF        = "\r\n";
   private static       int    call_count;
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPLog");

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor. Calls constructor within ASPObject.
    *
    * An instance of ASPLog is automatically created by ASPPage. Reference to the creator
    * is passed as input argument.
    */
   ASPLog( ASPPage page )
   {
      super(page);
   }

   ASPLog construct()
   {
      return this;
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze() throws FndException
   {
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
      // Can not reset all attributes here due to files are still open.
      // Explicit call to close() is called by ASPManager in OnEndPage().
      // ASPLog is always in the DEFINED state.
      closed = true;
      resp_write = true;
      show_add_msg = true;
      runpage = null;
   }


   protected void doActivate() throws FndException
   {
      closed = false;
   }

   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPLog l = new ASPLog((ASPPage)page);
      l.log_enabled   = log_enabled;
//      l.trace_enabled = trace_enabled;
      l.log_path      = log_path;
//      l.trace_path    = trace_path;
      l.closed = true;
      l.setCloned();
      return l;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   //==========================================================================
   //  Public interface
   //==========================================================================

   /**
    * Returns true, if the log output is both turned on and initialized.
    */
   public boolean isLogOn()
   {
      if(DEBUG) debug("ASPLog.isLogOn(): "+log_enabled);
      if( log_enabled!=null ) return log_enabled.booleanValue();
      if( closed ) return false;
      try
      {
         ASPConfig cfg = getASPPage().getASPConfig();
         if( cfg!=null )
         {
            if(DEBUG) debug("ASPLog: fetch logging parameters...");
            log_enabled = "Y".equals( cfg.getParameter("AUDIT/LOG/ENABLED","N") ) ?
                          Boolean.TRUE :
                          Boolean.FALSE;
            String temp_path = cfg.getParameter("AUDIT/LOG/PATH",null);
            if (ASPConfigFile.CONSOLE.equalsIgnoreCase(temp_path))
               log_path = temp_path;
            else
               log_path = cfg.convertPath(temp_path);
            if(DEBUG) debug("ASPLog: logging parameters fetched.");

            return log_enabled.booleanValue();
         }
      }
      catch( Throwable any )
      {
         if(DEBUG) debug("ASPLog: can not decide if log is on...");
         error(any);
      }
      return false;
   }


   /**
    * Returns true, if the trace output is both turned on and initialized.
    */
   public boolean isTraceOn()
   {
      if(DEBUG) debug("ASPLog.isTraceOn(): "+trace_enabled);
      if( trace_enabled!=null ) return trace_enabled.booleanValue();
      if( closed ) return false;
      try
      {
         ASPConfig cfg = getASPPage().getASPConfig();
         if( cfg!=null )
         {
            if(DEBUG) debug("ASPLog: fetch traceing parameters...");
            synchronized(getClass())
            {
               if(trace_enabled==null)
                  trace_enabled = "Y".equals( cfg.getParameter("AUDIT/TRACE/ENABLED","N") ) ?
                                  Boolean.TRUE :
                                  Boolean.FALSE;
            }
            String temp_path = cfg.getParameter("AUDIT/TRACE/PATH",null);
            if (ASPConfigFile.CONSOLE.equalsIgnoreCase(temp_path))
               trace_path = temp_path;
            else
               trace_path = cfg.convertPath(temp_path);
            if(DEBUG) debug("ASPLog: traceing parameters fetched.");

            return trace_enabled.booleanValue();
         }
      }
      catch( Throwable any )
      {
         if(DEBUG) debug("ASPLog: can not decide if trace is on...");
         error(any);
      }
      return false;
   }


   /**
    * Returns true, if the trace output is both turned on and initialized.
    */
   public void logUserBrowser( String userid )
   {
      if(DEBUG) debug("ASPLog.logUserBrowser(): "+log_user_browser);
      if( log_user_browser==null )
      {
         try
         {
            ASPConfig cfg = getASPPage().getASPConfig();
            if( cfg!=null )
            {
               if(DEBUG) debug("ASPLog: fetching config parameter...");
               synchronized(getClass())
               {
                  if(log_user_browser==null)
                     log_user_browser = "Y".equals( cfg.getParameter("AUDIT/LOG/LOG_USER_BROWSER","N") ) ?
                                        Boolean.TRUE :
                                        Boolean.FALSE;
               }
            }
         }
         catch( Throwable any )
         {
            if(DEBUG) debug("ASPLog: can not decide if user browser should be logged...");
            error(any);
         }
      }
      if( log_user_browser.booleanValue() )
      {
         ASPManager mgr = getASPManager();
         String browser = "unknown";
         if( mgr.isExplorer() )
            browser = "MS Internet Explorer";
         else if( mgr.isNetscape4x() )
            browser = "Netscape 4.x";
         else if( mgr.isNetscape6() )
            browser = "Netscape 6 or higher";
         else if( mgr.isMozilla() )
            browser = "Mozilla compliant";

         Alert.add("User '"+userid+"' is using "+browser+" browser");
      }
   }


   public void enableTrace( boolean enable )
   {
      trace_enabled = enable ? Boolean.TRUE : Boolean.FALSE;
      ASPConfig cfg = getASPPage().getASPConfig();
      if( cfg!=null )
         trace_path = cfg.convertPath(cfg.getParameter("AUDIT/TRACE/PATH",null));
   }


   /**
    * Writes a string to the log output.
    */
   public void put( String text )
   {
      if(DEBUG) debug("ASPLog.put("+text+")");
      try
      {
         if( !isLogOn() ) return;
         //if(DEBUG) debug("ASPLog.put("+text+")");
         if( !ASPConfigFile.CONSOLE.equalsIgnoreCase(log_path) && log_file==null && log_path!=null )
            openLogOutput();
         if( log_file!=null )
         {
            log_file.println(text);
            log_file.flush();
         }
         else
            debug(text);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Writes a string to the trace output.
    */
   public void trace( String text )
   {
      if(DEBUG) debug("ASPLog.trace("+text+")");
      try
      {
         if( !isTraceOn() ) return;
         //if(DEBUG) debug("ASPLog.trace("+text+")");
         if( !ASPConfigFile.CONSOLE.equalsIgnoreCase(trace_path) && trace_file==null && trace_path!=null )
            openTraceOutput();
         if( trace_file!=null )
         {
            trace_file.println(text);
            trace_file.flush();
         }
         else
            debug(text);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Writes a string to the DBMON console and Alert
    */
   public void debug( String text )
   {
      debug(text, true);
   }

   /**
    * Writes a string to the DBMON console and optionally Alert
    */
   public void debug( String text, boolean alert )
   {
      Util.debug(text);
      if(alert)
         Alert.add(text);
   }


   /**
    * Closes log and trace files.
    * Called from ASPPage.
    */
   void close()
   {
      closeFile(log_file);
      closeFile(trace_file);
      trace_file = null;
      log_file   = null;
      loglevel   = 0;
      closed     = true;
   }

   private void closeFile( PrintWriter file )
   {
      try
      {
         if (file != null)
         {
            file.print("File closed at ");
            file.println(getCurrentDate());
            file.println("");
            file.println("");
            file.close();
         }
      }
      catch( Throwable any )
      {
         debug(Str.getStackTrace(any));
      }
   }


   //==========================================================================
   //  Package methodes
   //==========================================================================

   /**
    * Returns reference to the log file.
    */
   PrintWriter getLogOutput()
   {
      return log_file;
   }


   /**
    * Returns reference to the trace file.
    */
   PrintWriter getTraceOutput()
   {
      return trace_file;
   }


   //==========================================================================
   //  Private methodes
   //==========================================================================

   /**
    * Opens the log file for the current request if not already opened.
    */
   private void openLogOutput() throws Exception
   {
      try
      {
         if( !Str.isEmpty(log_path) )
         {
            ASPManager mgr = getASPManager();
            String user_id    = mgr.getUserId();
            String session_id = mgr.getSessionId();
            if (user_id!=null)
               user_id = user_id.replace('\\','.').replace('/','.');

            String filename = (log_path + ifs.fnd.os.OSInfo.OS_SEPARATOR + user_id + "."
                               + session_id + "." + call_count + ".log");

            if(DEBUG) debug("ASPLog: open log file '"+filename+"' for ASPManager ["+mgr+"]");
            log_file = new PrintWriter(
                       new BufferedOutputStream(
                       new FileOutputStream(filename, true)));

            log_file.println("");
            log_file.print("ASPLog file open at ");
            log_file.print(getCurrentDate());
            log_file.print(" (calls=");
            log_file.println((++call_count)+")");
            log_file.flush();
         }
      }
      catch( Throwable e )
      {
         error(e);
      }
   }


   /**
    * Opens the trace file for the current request if not already opened.
    */
   private void openTraceOutput() throws Exception
   {
      try
      {
         if( !Str.isEmpty(trace_path) )
         {
            ASPManager mgr = getASPManager();
            String user_id = mgr.getUserId();

            String session_id = mgr.getSessionId();
            if (user_id!=null)
               user_id = user_id.replace('\\','.').replace('/','.');

            String filename = (trace_path + ifs.fnd.os.OSInfo.OS_SEPARATOR + user_id + "."
                               + session_id + "." + call_count + ".trc");

            if(DEBUG) debug("ASPLog: open trace file '"+filename+"' for ASPManager ["+mgr+"]");
            trace_file = new PrintWriter(
                         new BufferedOutputStream(
                         new FileOutputStream(filename, true)));

            trace_file.println("");
            trace_file.print("ASPLog trace file open at ");
            trace_file.print(getCurrentDate());
            trace_file.print(" (calls=");
            trace_file.println((++call_count)+")");
            trace_file.flush();
         }
      }
      catch( Throwable e )
      {
         error(e);
      }
   }


   //==========================================================================
   //  Diverse
   //==========================================================================

   private String getCurrentDate()
   {
      date.setTime(System.currentTimeMillis());
      return date_formatter.format(date);
   }

   /**
    * Write the contents of the ASP Request object to the trace output.
    */
   public void traceRequest()
   {
      try
      {
/*
          Request r = getASPManager().getAspRequest();

          trace("Request as Dictionary: "+r);
          trace("   TotalBytes = " + r.getTotalBytes());
          trace( (Dictionary) r.getServerVariables(),   "ServerVariables"   );
          //trace( (Dictionary) r.getCookies(),           "Cookies"           );
          trace( (Dictionary) r.getClientCertificate(), "ClientCertificate" );
          trace( (Dictionary) r.getForm(),              "Form"              );
          trace( (Dictionary) r.getQueryString(),       "QueryString"       );

          trace("Request as Enumerator: "+r);
          trace("   TotalBytes = " + r.getTotalBytes());
          trace( (Enumerator) r.getServerVariables(),   "ServerVariables"   );
          trace( (Enumerator) r.getCookies(),           "Cookies"           );
          trace( (Enumerator) r.getClientCertificate(), "ClientCertificate" );
          trace( (Enumerator) r.getForm(),              "Form"              );
          trace( (Enumerator) r.getQueryString(),       "QueryString"       );
*/
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


/*
    private void trace( Dictionary dic, String name )
    {
       trace("***************"+name+"("+dic+"):");
       Enumeration keys = dic.keys();

       while( keys.hasMoreElements() )
       {
          Object key = keys.nextElement();
          Object val = dic.get(key);

          Variant variant = ((RequestDictionary)dic).getVariant((String)key);
          int type = variant.getvt();

          trace("   "+key+" = \""+val+"\"" + " (vt="+type+")");
       }
       trace("");
    }


    private void trace( Enumerator enum, String name )
    {
       trace("***************"+name+"("+enum+"):");
       while( enum.hasMoreElements() )
       {
          Object elem = enum.nextElement();
          trace("   "+elem);
       }
       trace("");
    }
*/


   //==========================================================================
   //  Error logging
   //==========================================================================

   /**
    * Common error handler routine. Writes error message to log and trace files and also
    * to the screen (browser).
    */
   public void error( Throwable e, boolean format_error_page )
   {
      error(e, format_error_page, false);
   }

   public void error( Throwable e, boolean format_error_page, boolean throw_exception, boolean is_multirow_delete )
   {
      if( e instanceof AbortException )
      {
         debug(sep()+". ASPLog: Caught exception : "+e, false);
         throw (AbortException)e;
      }
      if( e instanceof ASPManager.ResponseEndException )
      {
         //debug(sep()+". ASPLog: Caught exception : "+e, false);
         throw (ASPManager.ResponseEndException)e;
      }

      Buffer info = null;
      if(throw_exception)
      try
      {
         info = getASPManager().getFactory().getBuffer();
      }
      catch( Throwable any )
      {
         debug(sep()+". ASPLog: Error while creating info buffer: "+any);
      }

      logError( e, format_error_page, info, is_multirow_delete );

      debug(sep()+". ASPLog: Throws exception ASPLog.AbortException ...");
      if(throw_exception)
         throw new ExtendedAbortException(info);
      else
         throw new AbortException();
   }
   
   public void error( Throwable e, boolean format_error_page, boolean throw_exception )
   {
      if( e instanceof AbortException )
      {
         debug(sep()+". ASPLog: Caught exception : "+e, false);
         throw (AbortException)e;
      }
      if( e instanceof ASPManager.ResponseEndException )
      {
         //debug(sep()+". ASPLog: Caught exception : "+e, false);
         throw (ASPManager.ResponseEndException)e;
      }

      Buffer info = null;
      if(throw_exception)
      try
      {
         info = getASPManager().getFactory().getBuffer();
      }
      catch( Throwable any )
      {
         debug(sep()+". ASPLog: Error while creating info buffer: "+any);
      }

      logError( e, format_error_page, info );

      debug(sep()+". ASPLog: Throws exception ASPLog.AbortException ...");
      if(throw_exception)
         throw new ExtendedAbortException(info);
      else
         throw new AbortException();
   }


   private int loglevel = 0;

   private String sep()
   {
      String sep = "  ";
      for( int i=0; i<loglevel; i++)
         sep = sep + ". ";
      return sep;
   }


   private String formatErrMsg( String errmsg, boolean translate )
   {
      ASPManager mgr = getASPManager();
      if(translate && !dont_log_error)
      {
         try
         {
            dont_log_error = true;
            if( Str.isEmpty(errmsg) )
               return mgr.translateJavaText("FNDLOGSYSEX1: A system error occurred");
            else
               return mgr.translateJavaText("FNDLOGSYSEX2: A system error '&1' occurred",errmsg);
         }
         catch(Throwable any)
         {
            return "A system error '"+(Str.isEmpty(errmsg)?"unknown":errmsg)+"' occurred";
         }
         finally
         {
            dont_log_error = false;
         }
      }
      else
         return "A system error '"+(Str.isEmpty(errmsg)?"unknown":errmsg)+"' occurred";
   }


   private String formatAddErrMsg( String errclass, String errstack, boolean translate )
   {
      if(translate && !dont_log_error)
      try
      {
         dont_log_error = true;
         return getASPManager().translateJavaText("FNDLOGSYSADEX: Exception class: &1 Exception stack: &2",errclass+CRLF,CRLF+errstack);
      }
      catch(Throwable any)
      {
         return "Can not translate. Exception class: " + errclass + CRLF + "Exception stack: " + CRLF + errstack;
      }
      finally
      {
         dont_log_error = false;
      }
      else
         return "Exception class: " + errclass + CRLF + "Exception stack: " + CRLF + errstack;
   }


   private String formatLogErrMsg( String errclass,
                                   String fndclass,
                                   String oraerr,
                                   String luname,
                                   String fnderr,
                                   String errmsg,
                                   String errstack )
   {
      out.clear();

      if("FndException(ifs.fnd.base.ManualDecisionException)".equals(errclass))
         return out.toString();
      
      out.append("CAUGHT EXCEPTION: [",errclass,"]");
      if( !Str.isEmpty(fndclass) )
         out.append("[",fndclass,"]");
      out.append(CRLF);
      if( !Str.isEmpty(luname) )
         out.append("LU: ",luname,CRLF);
      if( !Str.isEmpty(oraerr) || !Str.isEmpty(fnderr) )
      {
         out.append("ERROR CODE: ");
         if( !Str.isEmpty(oraerr) )
            out.append(oraerr);
         if( !Str.isEmpty(oraerr) && !Str.isEmpty(fnderr) )
            out.append(", ");
         if( !Str.isEmpty(fnderr) )
            out.append(fnderr);
         out.append(CRLF);
      }
      out.append("ERROR TEXT : ",errmsg,CRLF,CRLF);
      out.append("EXCEPTION STACK :",CRLF,errstack);

      return out.toString();
   }


   void logError( Throwable e, AutoString outstr )
   {
      logError(e, true, true, outstr, null,false);
   }


   void logError( Throwable e, boolean format_error_page )
   {
      logError(e, format_error_page, true, null, null,false);
   }


   void logError( Throwable e, boolean format_error_page, Buffer info, boolean is_multirow_delete )
   {
      logError(e, format_error_page, true, null, info, is_multirow_delete);
   }

   void logError( Throwable e, boolean format_error_page, Buffer info )
   {
      logError(e, format_error_page, true, null, info, false);
   }


   void logError( Throwable e, boolean format_error_page, boolean translate )
   {
      logError(e, format_error_page, translate, null, null,false);
   }


   private boolean dont_log_error = false;
   private boolean log_exception = true;

   void logError( Throwable e, boolean format_error_page, boolean translate, AutoString outstr, Buffer info, boolean is_multirow_delete )
   {
      if(DEBUG) dumpStack();
      loglevel++;
      try
      {
         if( e instanceof AbortException )
         {
            debug(sep()+"ASPLog: Logging ASPLog.AbortException exception ...", false);
            return;
         }

         if ( e instanceof FndException )
         {
            FndException fex = (FndException)e;
            log_exception = ! fex.getOriginalClassName().equals("FndException(ifs.fnd.base.ManualDecisionException)");
         }

         if(log_exception)
         debug(sep()+"ASPLog: Logging exception ["+e.getClass().getName()+"] :\r\n"+
               sep()+"'"+e.getMessage()+"'");

         ASPManager       mgr    = getASPManager();
         ASPPage          page   = getASPPage();
         ASPPortal        portal = page.getASPPortal();
         ASPHTMLFormatter fmt    = null;

         String errtype = "UNKNOWN";
         String errclass;
         String errstack;
         String errmsg = null;
         String message;
         String addmsg = null;
         String logmsg;
         boolean formable = false;

         if (loglevel==1)
            try
            {
               fmt = page.getASPHTMLFormatter();
            }
            catch(Throwable any)
            {
               debug(sep()+"Can not format HTML !");
               fmt = null;
            }
         else
            debug(sep()+"RECURSIVE CALL !");

         //if( mgr.getAspResponseBuffered() ) mgr.clearResponse();
         try
         {
            if( mgr.isResponseBuffered() ) mgr.clearResponse();
         }
         catch( ASPManager.AspContextException x )
         {
            String msg = sep()+"Can not clear response while logging error:\n"+Str.getStackTrace(x);
            if( log_file   != null) { log_file.println(msg);    log_file.flush();   }
            if( trace_file != null) { trace_file.println(msg);  trace_file.flush(); }
            debug(msg);
         }

         if ( e instanceof FndException && log_exception )
         {
            FndException fex = (FndException)e;
            errclass = fex.getOriginalClassName();
            errstack = fex.getErrorStack();

            debug(sep()+"Trying to debug portlet ["+runpage+"] in portal ["+portal+"]");
            if(portal!=null && runpage!=null && runpage.getPortletException()==null)
               runpage.setPortletException(fex);
            else if(portal!=null && runpage!=null)
               debug(sep()+"Portlet exception already caught:\r\n"+runpage.getPortletException());

            Throwable addex = fex.getCaughtException();
            if (addex!=null)
               debug(sep()+"ASPLog: Exception caught by this instance of FndException:\r\n\t"+
                     Str.getStackTrace(addex)+"\r\n\r\n");

            String orgerrmsg  = null;
            String param1     = null;
            String param2     = null;
            String param3     = null;
            String oraerrcode = null;
            String luname     = null;
            String ifserrcode = null;
            String oraerrmsg  = null;

            if ( fex.isFndError() )
            {
               errtype = "FND_ERROR";
               orgerrmsg = fex.getMsg();
               param1    = fex.getParam1();
               param2    = fex.getParam2();
               param3    = fex.getParam3();

               if( loglevel==1 && translate )
                  errmsg = mgr.translateJavaText(orgerrmsg,param1,param2,param3);
               else
                  errmsg = Str.replace(Str.replace(Str.replace(orgerrmsg,"&1",""+param1),"&2",""+param2),"&3",""+param3);

               logmsg  = formatLogErrMsg(errclass,errtype,null,null,null,errmsg,errstack);
               message = errmsg;
               addmsg  = formatAddErrMsg(errclass,errstack,translate);

               formable = true;
            }
            else if ( fex.isIfsPlError() )
            {
               errtype    = "IFSPL_ERROR";
               oraerrcode = fex.getOraErrorCode();
               oraerrmsg  = fex.getOraErrorMessage();
               luname     = fex.getLuName();
               ifserrcode = fex.getIfsErrorCode();

               errmsg  = Util.firstLine(fex.getOraErrorMessage());

               logmsg  = formatLogErrMsg(errclass,errtype,oraerrcode,luname,ifserrcode,oraerrmsg,errstack);
               //message = mgr.HTMLEncode(errmsg);
               message = errmsg;
               addmsg  = formatAddErrMsg(errclass,errstack,translate);

               formable = true;
            }
            else if ( fex.isOracleError() )
            {
               errtype    = "ORA_ERROR";
               oraerrcode = fex.getOraErrorCode();
               oraerrmsg  = fex.getOraErrorMessage();

               errmsg  = oraerrmsg;

               logmsg  = formatLogErrMsg(errclass,errtype,oraerrcode,null,null,errmsg,errstack);
               //message = mgr.HTMLEncode(errmsg);
               message = errmsg;
               addmsg  = formatAddErrMsg(errclass,errstack,translate);
            }
            else
            {
               errtype = "JAVA_ERROR";

               errmsg  = fex.getMessage();

               logmsg  = formatLogErrMsg(errclass,errtype,null,null,null,errmsg,errstack);
               message = formatErrMsg(errmsg,translate);
               addmsg  = formatAddErrMsg(errclass,errstack,translate);
            }

            String cmdname = fex.getCommandName();
            String rowno   = fex.getRowNo();
            String custmsg = fex.getAdditionalMessage();

            if(info!=null)
            {
               info.setItem("ERROR_TYPE",               errtype);
               info.setItem("ERROR_MESSAGE",            errmsg);
               info.setItem("ORIGINAL_CLASS_NAME",      errclass);
               info.setItem("ERROR_STACK",              errstack);
               info.setItem("FORMATTED_FULL_INFO",      addmsg);
               info.setItem("CAUGHT_EXCEPTION",         addex!=null?Str.getStackTrace(addex):null);
               info.setItem("ORG_ERROR_MESSAGE",        orgerrmsg);
               info.setItem("ERROR_MESSAGE_PARAM1",     param1);
               info.setItem("ERROR_MESSAGE_PARAM2",     param2);
               info.setItem("ERROR_MESSAGE_PARAM3",     param3);
               info.setItem("ORACLE_ERROR_CODE",        oraerrcode);
               info.setItem("LU_NAME",                  luname);
               info.setItem("IFS_ERROR_CODE",           ifserrcode);
               info.setItem("ORACLE_ERROR_MESSAGE",     oraerrmsg);
               info.setItem("COMMAND_NAME",             cmdname);
               info.setItem("ROW_NUMBER",               rowno);
               info.setItem("ADDITIONAL_MESSAGE",       custmsg);
            }

            if( !Str.isEmpty(cmdname) && !Str.isEmpty(rowno) && !dont_log_error )
            {
               if(DEBUG) debug("ASPLog.logError(): rowno="+rowno);
               try
               {
                  dont_log_error = true;
                  errmsg = Integer.toString((runpage==null?page:runpage).getASPBlock(cmdname).getASPRowSet().translateRowNumber(Integer.parseInt(rowno)));
               }
               catch(Throwable any)
               {
                  errmsg = null;
                  /*
                  if(loglevel==1 && translate)
                     errmsg = mgr.translateJavaText("FNDLOGUNROW: Unknown Row");
                  else
                     errmsg = "Unknown Row";
                  */
               }
               finally
               {
                  dont_log_error = false;
               }

               if(info!=null)
                  info.setItem("ROW_NUMBER",errmsg);

               if(errmsg!=null && !is_multirow_delete )
               {
                  if(loglevel==1 && translate )
                     errmsg = mgr.translateJavaText("FNDLOGROWNO: in row number: &1",errmsg);
                  else
                     errmsg = "in row number: " + errmsg;

                  // Removed by Terry 20130912
                  // message += CRLF + errmsg;
                  // Removed end
                  logmsg  += CRLF + errmsg + " [" + cmdname + "." + rowno + "]";
               }
            }

            if( !Str.isEmpty(custmsg) )
            {
               if(show_add_msg)
                  message += (outstr!=null?"<br>":CRLF) + custmsg;
               logmsg  += CRLF + custmsg;
            }
         }
         else
         {
            if(portal!=null && runpage!=null && runpage.getPortletException()==null)
               runpage.setPortletException(new FndException(e));

            errclass = e.getClass().getName();
            errstack = Str.getStackTrace(e);
            errmsg   = e.getMessage();

            logmsg   = formatLogErrMsg(errclass,null,null,null,null,errmsg,errstack);
            message  = formatErrMsg(errmsg,translate);
            addmsg   = formatAddErrMsg(errclass,errstack,translate);

            if(info!=null)
            {
               info.setItem("ERROR_TYPE",               errtype);
               info.setItem("ERROR_MESSAGE",            errmsg);
               info.setItem("ORIGINAL_CLASS_NAME",      errclass);
               info.setItem("ERROR_STACK",              errstack);
               info.setItem("FORMATTED_FULL_INFO",      addmsg);
               info.setItem("CAUGHT_EXCEPTION",         null);
               info.setItem("ORG_ERROR_MESSAGE",        null);
               info.setItem("ERROR_MESSAGE_PARAM1",     null);
               info.setItem("ERROR_MESSAGE_PARAM2",     null);
               info.setItem("ERROR_MESSAGE_PARAM3",     null);
               info.setItem("ORACLE_ERROR_CODE",        null);
               info.setItem("LU_NAME",                  null);
               info.setItem("IFS_ERROR_CODE",           null);
               info.setItem("ORACLE_ERROR_MESSAGE",     null);
               info.setItem("COMMAND_NAME",             null);
               info.setItem("ROW_NUMBER",               null);
               info.setItem("ADDITIONAL_MESSAGE",       null);
            }
         }

         if(log_exception)
         {
         //AutoString out = new AutoString();
         out.clear();
         out.append("\t",sep(),"**********    ERROR LOG     **********\r\n");
         String s = logmsg;
         int i = s.indexOf(CRLF);
         int l = CRLF.length();
         while (i>=0)
         {
            out.append("\t",sep(),s.substring(0,i),"\r\n");
            if (s.length()>i+l)
               s = s.substring(i+l);
            else
               s = "";
            i = s.indexOf(CRLF);
         }
         out.append("\t",sep(),s,"\r\n\t");
         out.append(sep(),"********** END OF ERROR LOG **********\r\n");
         debug(out.toString());

         logmsg = "**************************************" + CRLF +
                  "ERROR LOG:" + CRLF + logmsg + CRLF +
                  "**************************************";
         if( log_file   != null) { log_file.println(logmsg);    log_file.flush();   }
         if( trace_file != null) { trace_file.println(logmsg);  trace_file.flush(); }
         }
         if(format_error_page)
         {
            if(outstr!=null)
            {
               message = mgr.HTMLEncode(message);
               addmsg  = mgr.HTMLEncode(addmsg);
         
               debug("Appending error message to send output string:\r\n"+message+"\r\n");
               outstr.append("\r\n",message,"\r\n<br><br>\r\n");
               if(show_add_msg && !dont_log_error)
               try
               {
                  dont_log_error = true;

                  String ERR_MSG = "__ERR_MSG";

                  if (portal.isStdPortlet() && runpage!=null)
                     ERR_MSG += "_"+runpage.getId();

                  outstr.append("<script language=javascript>var "+ERR_MSG+"='",mgr.jsEncodeQuotes(mgr.JScriptEncode(addmsg.length()>1500? addmsg.substring(0,1500):addmsg)),"';</script>\r\n");
                  outstr.append("\r\n<a href=\"javascript:alert(HTMLDecode("+ERR_MSG+"))\">",mgr.translateJavaText("FNDLOGADDMSG: More info"),"</a>\r\n");
               }
               finally
               {
                  dont_log_error = false;
               }
            }
            else if(fmt!=null && resp_write)
            {
               debug("Trying to format HTML page.");
               if(!dont_log_error)
               try
               {
                  dont_log_error = true;
                  message = fmt.formatErrorMsg( message, addmsg );
               }
               catch(Throwable any)
               {
                  debug(sep()+"Can not format HTML !");
                  message += "\r\n\r\n" + addmsg;
               }
               finally
               {
                  dont_log_error = false;
               }
               else
                  message += CRLF + CRLF + addmsg;
            }
            else
            {
               debug("Error message without HTML-formatting.");
               message += CRLF + CRLF + addmsg;
            }

            if (loglevel==1)
            {
               debug("First error level.");
               if(outstr==null && resp_write)
               {
                  debug("Write error message to Response.");
                  try
                  {
                     mgr.writeResponse( message );
                  }
                  catch( ASPManager.AspContextException x )
                  {
                     String msg = "Can not write response while logging error:\n"+Str.getStackTrace(x)+"\n"+message;
                     if( log_file   != null) { log_file.println(msg);    log_file.flush();   }
                     if( trace_file != null) { trace_file.println(msg);  trace_file.flush(); }
                     debug(msg);
                  }
               }
               if(!dont_log_error)
               try
               {
                  dont_log_error = true;
                  (runpage==null?page:runpage).getASPContext().rollbackContext();
               }
               catch(Throwable any)
               {
                  debug("Can not rollback context!");
               }
               finally
               {
                  dont_log_error = false;
               }
            }
         }
         else if(info==null && loglevel==1)
         {
            debug("Write formatted error message to Response.");
            try
            {
               mgr.writeResponse( (formable?"No_Data_Found":"MTS_Exception: ")+message );
            }
            catch( ASPManager.AspContextException x )
            {
               String msg = "Can not write response while logging error:\n"+Str.getStackTrace(x)+"\n"+message;
               if( log_file   != null) { log_file.println(msg);    log_file.flush();   }
               if( trace_file != null) { trace_file.println(msg);  trace_file.flush(); }
               debug(sep()+msg);
            }
         }
         else
            debug(sep()+"Error not written to response:\n"+message);
      }
      catch(Throwable any)
      {
         //Bug 40929, start
         //Just remove the PANIC word from the error message.
         debug("\t"+sep()+"Got an error : \r\n\t"+sep()+Str.getStackTrace(any)+
               "\r\n\t"+sep()+"while trying to log exception : \r\n\t"+sep()+Str.getStackTrace(e)+"\r\n\r\n\r\n");
         //Bug 40929, end
      }
      loglevel--;
   }

   private boolean resp_write = true;
   private boolean show_add_msg = true;
   private ASPPortletProvider runpage = null;

   void enableWriteResponse()
   {
      if(DEBUG) debug("Enabling logging to Response.");
      resp_write = true;
      runpage = null;
   }

   void disableWriteResponse( ASPPortletProvider portlet )
   {
      if(DEBUG) debug("Disabling logging to Response. Logging to portlet ["+portlet+"]");
      resp_write = false;
      runpage = portlet;
   }

   /**
    * Disable the link to additional error information.
    * The link will not apear on error inside the portlet area.
    */
   public void disableAdditionalErrorMsg() throws FndException
   {
      modifyingMutableAttribute("ADDITIONAL_MESSAGE");
      show_add_msg = false;
   }

   /**
    * Enable the link to additional error information.
    * The link will apear on error inside the portlet area.
    */
   public void enableAdditionalErrorMsg()
   {
      show_add_msg = true;
   }

   void setPortlet( ASPPortletProvider portlet )
   {
      if(DEBUG) debug("Setting portlet for logging: ["+portlet+"]");
      runpage = portlet;
   }

   public static void dumpStack()
   {
      try
      {
         throw new Exception("Current Stack Dump:");
      }
      catch(Exception e)
      {
         Util.debug( Str.getStackTrace(e) );
      }
   }

   //==========================================================================
   //==========================================================================
   // Inner class for error handling
   //==========================================================================
   //==========================================================================

   public class AbortException extends RuntimeException
   {
      private AbortException()
      {
         super("Aborting execution...");
      }
   }

   public class ExtendedAbortException extends AbortException
   {
      private Buffer info;

      private ExtendedAbortException( Buffer extinfo )
      {
         super();
         this.info = extinfo;
      }

      public Buffer getExtendedInfo()
      {
         return info;
      }
   }
}
