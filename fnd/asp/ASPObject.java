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
 * File        : ASPObject.java
 * Description : The common super class for almost all ASP classes.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-Feb-10 - Created
 *    Marek D  1998-May-13 - Added methods for trace and error handling
 *    Marek D  1998-May-20 - Added refresh() method
 *    Marek D  1998-May-30 - Added saveToFile() and loadFromFile() methods
 *    Marek D  1998-Jun-16 - Added trace(title) method
 *    Jacek P  1998-Jul-17 - Added method setASPManager()
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-20 - Better error handling in the error() function
 *    Marek D  1999-Feb-16 - Moved ASPManager based logic to ASPManagedObject
 *    Jacek P  1999-Mar-01 - Abstract class ASPManagedObject replaced with
 *                           an empty interface ASPManageable. All code moved
 *                           back to ASPObject.
 *    Jacek P  1999-Mar-05 - Removed methodes refresh() and close().
 *    Jacek P  1999-Mar-11 - Rewritten methods for error logging and debugging.
 *    Marek D  1999-Mar-18 - Declared trace/debug/error-methods as protected
 *    Jacek P  1999-Jul-08 - Changed access to public for protected methods:
 *                           error(), logError(), log(), trace(), debug(),
 *                           isTraceOn(), isLogOn().
 *    Mangala  2003-Dec-24 - Fixed bug 40929. Remove the word PANIC from error messages when log is missing.
 *    Jacek P  2004-Apr-22 - Adding logging of errors to the Alert file.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2005/09/28 11:36:15  japase
 * Added methods for traceing and debugging of buffer headers only
 *
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

/**
 * The base class for almost all ASP objects.
 * Contains a reference to the creator (an instance of ASPManager).
 */
public abstract class ASPObject
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPObject");
   public static boolean TRACEFILE_DEBUG = Util.isTraceFileDebugEnabled();

   private transient ASPManager mgr;
   private transient ASPLog     log;

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Stores the reference to the creator instance (of ASPManager class).
    * Must be called by constructor within the actual instance.
    */
   protected ASPObject( ASPManager manager )
   {
      mgr = manager;
   }


   /**
    * Returns reference to the creator object (an instance of ASPManager class).
    */
   public ASPManager getASPManager()
   {
      return mgr;
   }


   /**
    * Set the reference to the actual instance of ASPManager on an already existing object.
    */
   void setASPManager(ASPManager manager)
   {
      mgr = manager;
   }

   //==========================================================================
   //  ASPBufferable
   //==========================================================================

   /**
    * Stores the class name of this instance in the specified buffer.
    * Should be called from the save() method of every class that implements
    * the ASPBufferable interface.
    */
   protected void saveClass( Buffer into )
   {
      Buffers.save( into, "CLASS", getClass().getName() );
   }


   /**
    * Serializes the contents of this ASPObject to the specified file.
    * Throws an exception if this ASPObject does not implement
    * the ASPBufferable interface.
    */
   public void saveToFile( String filename )
   {
      try
      {
         verifyASPBufferable();
         ASPBuffer aspbuf = getASPManager().newASPBuffer();
         ((ASPBufferable)this).save(aspbuf);
         Buffers.save(aspbuf.getBuffer(),filename);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Recreates the contents of this ASPObject from a file
    * that has been created by saveToFile() method.
    * Throws an exception if this ASPObject does not implement
    * the ASPBufferable interface.
    */
   public void loadFromFile( String filename )
   {
      try
      {
         verifyASPBufferable();
         Buffer buf = Buffers.load(filename);
         ((ASPBufferable)this).load(getASPManager().newASPBuffer(buf));
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   protected void verifyASPBufferable() throws FndException
   {
      if( !(this instanceof ASPBufferable) )
         throw new FndException("FNDOBJNOBUF: &1 does not implement ASPBufferable interface", getClass().getName() );
   }


   //==========================================================================
   //  Log, trace, debug, error
   //
   //  These methods delegates job to ASPLog, if it exists, but otherwise:
   //     isLogOn(),isTraceOn()   return false
   //     trace(),log()           ignore the request
   //     debug(),error()         write to DBMON
   //==========================================================================

   private boolean logExists()
   {
      if( log==null && getASPManager()!=null )
         log = getASPManager().getASPLog();
      return log!=null;
   }


   /**
    * Return true if the log output is turned on
    */
   public boolean isLogOn()
   {
      return logExists() ? log.isLogOn() : false;
   }


   /**
    * Return true if the trace output is turned on
    */
   public boolean isTraceOn()
   {
      return logExists() ? log.isTraceOn() : false;
   }


   /**
    * Send the debug string to the DBMon console or, if initialized
    * and defined in Registry, to the trace file.
    */
   public void debug( String line )
   {
//       if( TRACEFILE_DEBUG && logExists() )
//          log.trace(line);
//       else
         Util.debug(line);
   }


   /**
    * Calls the ASPLog's trace() method to write the trace string
    * to the trace file.
    *
    * @see ifs.fnd.asp.ASPLog#trace
    */
   public void trace( String line )
   {
      if( logExists() )
         log.trace(line);
   }


   private void listBuffer( Buffer buf, String indent )
   {
      log.trace(indent+"!"+Str.nvl(buf.getHeader(),""));
      BufferIterator iter = buf.iterator();
      int i=0;

      while( iter.hasNext() )
      {
         Item item = iter.next();
         if( item.isCompound() )
         {
            log.trace(indent+i+":"+item.toString());
            listBuffer(item.getBuffer(),indent+"   ");
         }
         else
            log.trace(indent+i+":"+item.toString());
         i++;
      }
   }

   /**
    * Prints the contents of this ASPObject to the trace output.
    * Throws an exception if the current instance does not implement
    * the ASPBufferable interface.
    */
   public final void traceBuffer( String title )
   {
      if( !logExists() ) return;
      try
      {
         verifyASPBufferable();

         if( !log.isTraceOn() ) return;

         log.trace(title+" buffer contents:");
         ASPBuffer aspbuf;

         if (this instanceof ASPBuffer)
            aspbuf = (ASPBuffer)this;
         else
         {
            aspbuf = getASPManager().newASPBuffer();
            ((ASPBufferable)this).save(aspbuf);
         }
         listBuffer(aspbuf.getBuffer(),"");

         log.trace("");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Prints the contents of this ASPObject header to the trace output.
    * Throws an exception if the current instance does not implement
    * the ASPBufferable interface.
    */
   public final void traceBufferHeader( String title )
   {
      if( !logExists() ) return;
      try
      {
         verifyASPBufferable();

         if( !log.isTraceOn() ) return;

         log.trace(title+" buffer contents:");
         ASPBuffer aspbuf;

         if (this instanceof ASPBuffer)
            aspbuf = (ASPBuffer)this;
         else
         {
            aspbuf = getASPManager().newASPBuffer();
            ((ASPBufferable)this).save(aspbuf);
         }
         Buffer buf = aspbuf.getBuffer();
         buf = buf.getBuffer(ASPTransactionBuffer.HEADER);
         listBuffer(buf,"");

         log.trace("");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Prints the contents of this ASPObject header to the trace output.
    * Throws an exception if the current instance does not implement
    * the ASPBufferable interface.
    */
   public final void debugBufferHeader( String title )
   {
      try
      {
         verifyASPBufferable();

         String out = title+" buffer header '"+ASPTransactionBuffer.HEADER+"' contents:\n";
         ASPBuffer aspbuf;

         if (this instanceof ASPBuffer)
            aspbuf = (ASPBuffer)this;
         else
         {
            aspbuf = getASPManager().newASPBuffer();
            ((ASPBufferable)this).save(aspbuf);
         }
         Buffer buf = aspbuf.getBuffer();
         buf = buf.getBuffer(ASPTransactionBuffer.HEADER);
         //listBuffer(buf,"");
         Util.debug(out + Buffers.listToString(buf));
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Calls the ASPLog's put() method to write the log string
    * to the log file.
    *
    * @see ifs.fnd.asp.ASPLog#put
    */
   public final void log( String line )
   {
      if( logExists() )
         log.put(line);
   }


   /**
    * Calls the ASPLog's error() method. Throws a runtime exception if
    * no instance of ASPLog is accessible.
    *
    * @see ifs.fnd.asp.ASPLog#error
    */
   public void error( Throwable any )
   {
      if( logExists() )
         log.error(any,true);
      else
      {
         String msg =
           "\tThe instance of ASPLog is missing in class ASPObject while throwing error:\n\n\t"+
           Str.getStackTrace(any)+"\n\n"+
          "\tASPObject: Throws RuntimeException ...\n";
         //Bug 40929, start
         //Just remove the PANIC word from the error message.
         /*
          Util.debug(
            "\tThe instance of ASPLog is missing in class ASPObject while throwing error:\n\n\t"+
            Str.getStackTrace(any)+"\n\n"+
           "\tASPObject: Throws RuntimeException ...\n");
          */
         Util.debug(msg);
         Alert.add(msg);
          //Bug 40929, end
         throw new RuntimeException("Aborting execution...\n");
      }
   }


   /**
    * Calls the ASPLog's logError() method.
    *
    * @see ifs.fnd.asp.ASPLog#logError
    */
   public void logError( Throwable any )
   {
      if( logExists() )
         log.logError(any,true);
      else
      {
         String msg = "\tThe instance of ASPLog is missing in class ASPObject while logging error:\n\n\t"+
                      Str.getStackTrace(any)+"\n\n";
         //Bug 40929, start
         //Just remove the PANIC word from the error message.
         /*
         Util.debug(
            "\tThe instance of ASPLog is missing in class ASPObject while logging error:\n\n\t"+
            Str.getStackTrace(any)+"\n\n");
         */
         Util.debug(msg);
         Alert.add(msg);
         //Bug 40929, end
      }
   }
}
