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
 * File        : Alert.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-Apr-22 - Created.
 *    Rikfi R  2004-Jun-24 - Changes in init() to set priority and interval from web.xml
 *    Ramila H 2004-08-03  - Added code for compliance with Debug.inf
 * ----------------------------------------------------------------------------
 * New Comments:
 * Sasanka D   2007/07/25 - Merged Bug 66847, Modified class constructor to create the alert file name with timestamp appened 
 *                          in each time server starts
 * Revision 1.3  2005/11/15 11:36:16  japase
 * Better formatting of the output. Added timestamp.
 *
 *
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;
import java.text.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


/**
 * Creates a IFS Web Cleient common alert file.
 * This class launches a new thread on the servlet start-up, which in turn opens
 * a common alert log file. By default the file is located in the configuration
 * directory. The existing file is appended on each re-start of the framework.
 *
 * Default thread priority and wait time between iterations can be changed on
 * a running thread by calling proper methods, e.g. after reading proper
 * configuration parameters.
 *
 * Create the thread by calling the static method init() and sending
 * path to the directory.
 * Stop the thread on servlet unload by calling the static method stopTask().
 * Add new messages to the Alert file by calling the static method add().
 */
public class Alert extends Thread
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.Alert");

   private static final String NL            = System.getProperty("line.separator");
   private static final String ALERT_FNAME   = "fndweb-alert-";
   private static final String EXT           = ".log";
   private static Alert            alert;
   private static boolean          active        = true;
   private static long             wait_interval = 30000L;   // 30 sec
   private static SimpleDateFormat formatter     = new SimpleDateFormat("[yyyy.MM.dd-HH:mm:ss:SS] ");
   

   private File       alert_file;
   private LinkedList queue;
   private AutoString tmpbuf;
   private String full_alert_fname;
   private SimpleDateFormat dateFormat;
   private Date currentDate;
   private File parentFile;

   /**
    * Constructor.
    * Creates the Alert file, if not already created, and writes timestamp to it.
    */
   private Alert( String dir_path) throws IOException
   {
      super("FNDWEB-Alert");

      queue  = new LinkedList();
      tmpbuf = new AutoString();
      dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
      currentDate = new Date(System.currentTimeMillis());
      full_alert_fname = ALERT_FNAME+ dateFormat.format(currentDate) + EXT;

      System.out.println("Opening FNDWEB Alert file '"+dir_path+full_alert_fname+"'");
      alert_file = new File(dir_path,full_alert_fname);
      parentFile = alert_file.getParentFile();
      if(parentFile !=null && !parentFile.exists())
         parentFile.mkdirs();

      alert_file.createNewFile();
      if( !alert_file.canWrite() )
         throw new IOException("FNDWEB Alert cannot write to file "+dir_path+full_alert_fname);
      writeString( 3, "Alert thread started at: "+(new Date(System.currentTimeMillis())), 0 );
      writeString( 1, "=========================================================", 3 );
   }

   //==========================================================================
   //  Static methods to be called from other classes
   //==========================================================================

   /**
    * Create and start the Alert thread
    */
   public static void init( String dir_path , String interval, String priority)
   {
      if(alert==null)
      {

         int wait_time,prio;
         try
         {
            wait_time = Integer.parseInt(interval);
         }
         catch(NumberFormatException e)
         {
            wait_time = 30;
         }
         try
         {
            prio = Integer.parseInt(priority);
         }
         catch(NumberFormatException e)
         {
            prio = 4;
         }

         System.out.println("Creating and starting FNDWEB Alert thread");
         System.out.println("  with priority "+priority+"(of "+MIN_PRIORITY+"-"+MAX_PRIORITY+")");
         try
         {
            alert = new Alert(dir_path);
            alert.setThreadPriority(prio);
            alert.setSleepTime(wait_time);
            alert.start();
         }
         catch( Throwable any )
         {
            error("Couldn't start the Alert thread", any);
         }
      }
   }

   /**
    * Stop the Alert thread. Called on servlet unload.
    */
   public static void stopTask()
   {
      if(DEBUG) debug("Alert.stopTask()");
      active = false;
      alert.interrupt();
   }

   /**
    * Set the priority of the Alert thread.
    */
   public static void setThreadPriority( int newprio )
   {
      if(DEBUG) debug("Alert.setThreadPriority("+newprio+")");
      System.out.println("Changing priority of FNDWEB Alert thread");
      try
      {
         if(newprio>MAX_PRIORITY) newprio=MAX_PRIORITY;
         if(newprio<MIN_PRIORITY) newprio=MIN_PRIORITY;
         alert.setPriority(newprio);
      }
      catch( NullPointerException x )
      {
         error("The Alert thread hasn't been initiated!", x);
      }
      catch( IllegalArgumentException x )
      {
         error("The priority value is not allowed for the Alert thread!", x);
      }
      catch( SecurityException x )
      {
         error("The calling thread is not allowed to change priority of the Alert thread!", x);
      }
      catch( Throwable any )
      {
         error("Error while trying to change Alert thread priority", any);
      }
   }

   /**
    * Set the number of second the Alert thread is waiting between iterations.
    */
   public static void setSleepTime( int sec )
   {
      if(DEBUG) debug("Alert.setSleepTime("+sec+")");
      wait_interval = sec * 1000;
   }


   /**
    * Add text to the alert queue
    */
   public static void add( String text )
   {
      try
      {
         if(DEBUG) debug("Alert.add(): Adding message to the queue:\n"+text);
         Date now = new Date();
         synchronized(alert)
         {
            alert.queue.addLast(formatter.format(now) + text + NL);
         }
      }
      catch( Throwable any )
      {
         error("Couldn't add message to the Alert queue", text, any);
      }
   }

   //==========================================================================
   //  Member methods
   //==========================================================================

   /**
    * Start the execution of the task.
    * The task executes as a infinite loop by fetching all the messages
    * from the queue, writting them to the Alert file and waiting between the laps
    */
   public void run()
   {
      if(DEBUG) debug("Alert.run(): starting the loop");
      while(active)
      {
         if(DEBUG) debug("Alert.run(): reading the queue");
         try
         {
            tmpbuf.clear();
            synchronized(alert)
            {
               while(!queue.isEmpty())
                  tmpbuf.append( (String)(queue.removeFirst()) );
            }
            if(tmpbuf.length()>0)
            {
               if(DEBUG) debug("Alert.run(): writting message to the file:\n"+tmpbuf.toString() );
               writeString( 0, tmpbuf.toString(), 1 );
            }
         }
         catch( Throwable any )
         {
            error("Couldn't read the Alert queue", any);
         }

         if(DEBUG) debug("Alert.run(): sleeping "+(wait_interval/1000)+" sec");
         try
         {
            sleep(wait_interval);
         }
         catch(InterruptedException e)
         {
         }
         catch( Throwable any )
         {
            error("Error while waiting", any);
         }
      }
      if(DEBUG) debug("Alert.run(): stopping the task");
      System.out.println("Stopping FNDWEB Alert thread");
   }

   /**
    * Write a string to the Alert file.
    * The string can be preceded and followed by empty lines.
    */
   private void writeString( int lines_before, String str, int lines_after )
   {
      try
      {
         FileOutputStream   fos = new FileOutputStream(alert_file, true);
         OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
         BufferedWriter     wr  = new BufferedWriter(osw);

         for(int i=0; i<lines_before; i++)
            wr.newLine();
         wr.write( str, 0, str.length() );
         for(int i=0; i<lines_after; i++)
            wr.newLine();

         wr.flush();
         wr.close();
      }
      catch( Throwable any )
      {
         error("Couldn't write message to the Alert file", str, any);
      }
   }

   //==========================================================================
   //  Debugging, error
   //==========================================================================

   private static void debug( String line )
   {
      Util.debug(line);
   }

   private static void error( String msg, Throwable x )
   {
      error(msg, null, x);
   }

   private static void error( String msg, String txt, Throwable x )
   {
      debug(msg);
      if(txt!=null)
         debug("The message text\n:"+txt);
      debug("Caught error:\n"+Str.getStackTrace(x));
   }
}
