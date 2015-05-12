package ifs.fnd.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import ifs.fnd.asp.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;

public class DebugExe extends HttpServlet
{
private HttpServletRequest current_request;
private ASPManager emptymgr;
private Debug dbg;

   public DebugExe()
   {
   }

   public void init()
   {
      emptymgr = new ASPManager();
      dbg = new Debug();
   }



   public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      current_request = request;
      dbg.init(emptymgr,request,response);
      response.setContentType("text/html");
      String x = readValue("DBG",null);

      dbg.debugQueryString();

      if( !Str.isEmpty(x) )
          dbg.submitFromExe(x);
      else if( !Str.isEmpty(readValue("SHOW_POOL") ) )
          dbg.showPoolContents();
      else if( !Str.isEmpty(readValue("CLEAR_POOL") ) )
          dbg.clearPagePool();
      else if( !Str.isEmpty(readValue("LOCK_ALL") ) )
          dbg.lockAll();
      else if( !Str.isEmpty(readValue("UNLOCK_ALL") ) )
          dbg.unlockAll();
// Old Context cache is removed. (Daniel S)
//      else if( !Str.isEmpty(readValue("SHOW_CTX_CACHE") ) )
//          dbg.showContextCacheContents();
//      else if( !Str.isEmpty(readValue("CLEAR_CTX_CACHE") ) )
//          dbg.clearContextCache();
      else if( !Str.isEmpty(readValue("SHOW_PRF_CACHE") ) )
          dbg.showProfileCacheContents();
      else if( !Str.isEmpty(readValue("CLEAR_PRF_CACHE") ) )
          dbg.clearProfileCache();
      else if( !Str.isEmpty(readValue("RUN_GC") ) )
          dbg.runGC();
      else if( !Str.isEmpty(readValue("SHOW_PAGE") ) )
          dbg.showPage();
      else if( !Str.isEmpty(readValue("SHOW_PROFILE") ) )
          dbg.showProfile();
      else if( !Str.isEmpty(readValue("DEBUG_THREAD") ) )
          dbg.debugThread();
      else if( !Str.isEmpty(readValue("SET_STAT_PREC") ) )
          dbg.setStatisticsPrecision();
      else if( !Str.isEmpty(readValue("UPD_DEBUG_FLAGS") ) )
          dbg.updateDebugFlags();
      else if( !Str.isEmpty(readValue("CLEAR_STAT") ) )
      {
          ;//mgr.clearTraceStatistics();
          //log.debug("Trace statistics cleared.");
      }
      else if( !Str.isEmpty(readValue("SPOOL_STAT") ) )
              ;//mgr.spoolTraceStatistics();

   dbg.generateExeOutput();

   }
    ///////////////////
    //Utility Functions
    ///////////////////

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

}
