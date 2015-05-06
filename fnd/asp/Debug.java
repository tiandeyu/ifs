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
 * File        : ASPPagePool.java
 * Description : Static pool with the instances of ASPPage.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1999-Mar-17 - Created.
 *    Jacek P  1999-Apr-04 - Added support for Debug.exe
 *    Jacek P  1999-May-07 - Added support for profiles.
 *    Jacek P  2000-Apr-11 - Removed functions not used by DEBUG.EXE
 *    Johan S  2001-Mar-25 - 3.5 conversion.
 *    Daniel S 2002-Aug-08 - removed functionality for the old context cache.
 *    Ramila H 2002-Oct-04 - Added support for Debug tool. 
 *    Jacek P  2003-Feb-18 - Debug file name fetched from ifs.fnd.services.DebugInfo
 *    Jacek P  2004-Jan-04 - Bug#40931 changes. autolock -> AUTOLOCK
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import javax.servlet.http.*;

import ifs.fnd.buffer.*;
import ifs.fnd.os.ms.*;
//import ifs.fnd.mts.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.ap.*;


public class Debug extends ASPObject
{
    public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.Debug");

   private ASPManager mgr;
   private HttpServletRequest current_request;
   private HttpServletResponse current_response;
   public Debug()
   {
      super(null);
   }

   public void setASPManager( ASPManager mgr )
   {
      super.setASPManager(mgr);
      this.mgr = mgr;
   }

public void init(ASPManager mgr, HttpServletRequest current_request, HttpServletResponse current_response)
{
    this.mgr = mgr;
    this.current_request = current_request;
    this.current_response = current_response;
}

   //==========================================================================
   //  Utility functions
   //==========================================================================

private String getQueryString()
{
    try{
        return current_request.getQueryString();
    }catch(Throwable any){return null;}
}

private void endResponse()
{
    try{
        current_response.flushBuffer();
    }
    catch(Throwable any){}
}

private void responseWrite( String text )
{
    try
    {
        current_response.getWriter().write( text );
    }
    catch( Throwable any )
    {
    }
}

private String readValue(String name)
{
    return readValue(name, null);
}

private String readValue( String name, String default_value )
{
    try
    {
        String param = current_request.getParameter(name);
        return Str.isEmpty(param) ? default_value : param;
    }
    catch( Throwable any )
    {
        return default_value;
    }
}

   //==========================================================================
   //  Routines used by Debug.exe
   //==========================================================================

   private String qrystr;

   private boolean setExeFlag( String alias )
   {
      boolean on  = qrystr.indexOf(alias) >= 0;
      if(DEBUG) debug("Debug.setExeFlag("+alias+"): "+on);
      return on;
   }


   public void submitFromExe( String qrystr )
   {
      try
      {
         if(DEBUG) debug("Debug.submitFromExe("+qrystr+")");

         if( Str.isEmpty(qrystr) ) return;
         this.qrystr = qrystr;

         //Bug 40931, start
         ASPPagePool.AUTOLOCK = setExeFlag("PAL");
         //Bug 40931, end
         ASPManager .noreset  = setExeFlag("PNR");
         ASPPagePool.VERIFY   = setExeFlag("VPO");
         TraceEvent.statisticsEnabled = setExeFlag("RST");

         // context cache size
         //ASPPagePool    .MAX_SIZE = Integer.parseInt(readValue("PPS"));
         //ASPPagePool    .TIME_OUT = Integer.parseInt(readValue("PPT"))*60000;
         //ASPPagePool    .PATH     =                  readValue("PPF");
         ASPProfileCache.MAX_SIZE = Integer.parseInt(readValue("CPS"));
         ASPProfileCache.TIME_OUT = Integer.parseInt(readValue("CPT"))*60000;
//         ASPProfileCache.PATH     =                  readValue("CPF");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   public void generateExeOutput()
   {
      try
      {
         AutoString buf = new AutoString();

         //Bug 40931, start
         if(ASPPagePool.AUTOLOCK) buf.append(" AutoLockPagePool ");
         //Bug 40931, end
         if(ASPManager.noreset)   buf.append(" NoResetPages     ");
         if(ASPPagePool          .VERIFY) buf.append(" VerifyPooledObjects ");
         if(TraceEvent.statisticsEnabled) buf.append(" TraceStatisticsEnabled ");

         buf.append(" PagePoolSize=\"N.A.\" ");
         buf.append(" PagePoolTimeout=\"N.A.\" ");
         buf.append(" PagePoolFile=\"N.A.\" ");
         //buf.append(" PagePoolSize=\""       + ASPPagePool    .MAX_SIZE       +"\" ");
         //buf.append(" PagePoolTimeout=\""    + ASPPagePool    .TIME_OUT/60000 +"\" ");
         //buf.append(" PagePoolFile=\""       + ASPPagePool    .PATH           +"\" ");
         buf.append(" ProfileCacheSize=\""   + ASPProfileCache.MAX_SIZE       +"\" ");
         buf.append(" ProfileCacheTimeout=\""+ ASPProfileCache.TIME_OUT/60000 +"\" ");
         //buf.append(" ProfileCacheFile=\""   + ASPProfileCache.PATH           +"\" ");

         buf.append(getDebugFlagOutput());

         responseWrite( buf.toString() );
         endResponse();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   public String getDebugFlagOutput()
   {
       try
       {
          AutoString buf = new AutoString();

          buf.append("DEBUG_FLAGS");
          buf.append(DebugInfo.getFlags());

          return buf.toString();
       }
       catch( Throwable any )
       {
          error(any);
          return null;
       }

   }

   //==========================================================================
   //  Methodes called from DebugExe.asp
   //==========================================================================

   /**
    * Shows contents in the page pool.
    */
   public void showPoolContents()
   {
      try
      {
         if(DEBUG) debug(this+" showPoolContents()");
         Runtime r  = Runtime.getRuntime();
         long free  = r.freeMemory();
         long total = r.totalMemory();
         debug("\n\n\tTotal memory:"+Util.lpad(""+total,10)+
               " bytes\n\tFree  memory:"+Util.lpad(""+free,10)+" bytes\n\n");
         ASPPagePool.showContents(readValue("LOAD_GROUP"));
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Clears the page pool.
    */
   public void clearPagePool()
   {
      try
      {
         if(DEBUG) debug(this+" clearPagePool()");
         ASPPagePool.clear();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Locks all objects in the page pool.
    */
   public void lockAll()
   {
      try
      {
         if(DEBUG) debug(this+" lockAll()");
         ASPPagePool.lockAll(mgr);
      }
      catch( Throwable any )
      {
         error(any);
      }  

   }


   /**
    * Unlocks all objects in the page pool.
    */
   public void unlockAll()
   {
      try
      {
         if(DEBUG) debug(this+" unlockAll()");
         ASPPagePool.unlockAll();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Shows contents in the profile cache.
    */
   public void showProfileCacheContents()
   {
      try
      {
         if(DEBUG) debug(this+" showProfileCacheContents()");
         ASPProfileCache.showContents(readValue("LOAD_GROUP"));
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Clears the profile cache.
    */
   public void clearProfileCache()
   {
      try
      {
         if(DEBUG) debug(this+" clearContextCache()");
         ASPProfileCache.clear();
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Runs the gabage collector.
    */
   public void runGC()
   {
      try
      {
         Runtime r  = Runtime.getRuntime();
         long free  = r.freeMemory();
         long total = r.totalMemory();
         debug("\n\n\tTotal memory:"+Util.lpad(""+total,10)+
               " bytes\n\tFree  memory:"+Util.lpad(""+free,10)+" bytes\n\n");
         debug("\n\tRunning Garbage Collector ..............");
         r.gc();
         free  = r.freeMemory();
         total = r.totalMemory();
         debug("\n\n\tTotal memory:"+Util.lpad(""+total,10)+
               " bytes\n\tFree  memory:"+Util.lpad(""+free,10)+" bytes\n\n");
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Shows page contents in the page pool.
    */
   public void showPage()
   {
      try
      {
         String id = readValue("PAGE_ID").toUpperCase();
         if(DEBUG) debug(this+" showPage("+id+")");
         if(!Str.isEmpty(id))
            ASPPagePool.showPage(id);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Shows profile contents.
    */
   public void showProfile()
   {
      try
      {
         String id = readValue("PROFILE_KEY");
         if(DEBUG) debug(this+" showProfile("+id+")");
         if(!Str.isEmpty(id))
            ASPProfileCache.showProfile(id);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Debug the thread.
    */
   public void debugThread()
   {
      try
      {
         String t = readValue("THREAD_ID").toUpperCase();
         int id = 0;
         if(DEBUG) debug(this+" DebugThread("+t+")");
         if(!Str.isEmpty(t) && !"ALL".equals(t) )
            id = Integer.parseInt(t,16);
         if(id<0) id = 0;
         NativeUtilities.THREAD_ID = id;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Sets statistics precision.
    */
   public void setStatisticsPrecision()
   {
      try
      {
         String t = readValue("STAT_PREC").toUpperCase();
         int id = 0;
         if(DEBUG) debug(this+" setStatisticsPrecision("+t+")");
         if( !Str.isEmpty(t) )
            id = Integer.parseInt(t);

         mgr.setTraceStatisticsPrecision(id);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Updates the debug flags.
    */
   public void updateDebugFlags()
   {
      try
      {
          if(DEBUG) debug(this+" reloading Debug.inf file");
          
          updateDebugFlagsFile();
          DebugInfo.reloadFile();
          Enumeration en = DebugInfo.fetchClasses();

          while(en.hasMoreElements())
          {
              String clsname = (String) en.nextElement();
              boolean value = Util.isDebugEnabled(clsname);
              try {
                  Class.forName(clsname).getField("DEBUG").setBoolean(null,value);
                  if(DEBUG) debug("  setting "+clsname+".DEBUG="+value);
              }catch(Throwable any){if(DEBUG) debug("  Could not find "+clsname); }
              
          }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
/*
      try
      {
         if(DEBUG) debug(this+" updateDebugFlags()");
//         Util.updateDebugFlags();

         String classes = Registry.enumKeys("Debug");

         StringTokenizer st = new StringTokenizer(classes,",");
         while( st.hasMoreTokens() )
         {
            String t = st.nextToken();
            int pos  = t.indexOf("=");
            if(pos<0) continue;

            String clsname = t.substring(0,pos);
            String value   = t.substring(pos+1);

            if(DEBUG) debug("  setting "+clsname+".DEBUG="+value);
            Class.forName(clsname).getField("DEBUG").setBoolean(null,"TRUE".equals(value));
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
*/
   //==========================================================================
   //  Other routines
   //==========================================================================

   private void updateDebugFlagsFile()
   {

      if(DEBUG) debug(this+" write to Debug.inf file");

      String flag_values ="";


      ORBDebugConnectionPool.Slot orbcon = ORBDebugConnectionPool.get(ORBDebugConnectionPool.DEBUGGER,readValue("LOAD_GROUP"));

      Record requestRec = new Record("RESULTSTREAM");
      requestRec.add("DEBUG_TYPE","DEBUG_FLAGS");

      try {
         
         Record responseRec = (Record)orbcon.invoke(requestRec);
         orbcon.release();

         flag_values = (String)responseRec.findValue("RESULT");

         File flag_file = new File( DebugInfo.getDebugFileName() );
         BufferedWriter out = new BufferedWriter(new FileWriter(flag_file));
         
         StringTokenizer debug_flags_st  = new StringTokenizer(flag_values,"^");

         while (debug_flags_st.hasMoreTokens()) {
            out.write(debug_flags_st.nextToken());
            out.newLine();
         }

         out.flush();
         out.close();

      } catch (Throwable e) {
         debug("\n"+e.getMessage());
      }
   }


   /**
    * Debug the query string.
    */
   public void debugQueryString()
   {
      if(DEBUG)
      {
         debug(this+" debugQueryString()");
         debug("Query String:\n"+getQueryString());
      }
   }


}
