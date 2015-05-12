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
 * File        : FileRequestHandler.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2001-Sep-17 - Created
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.servlets;

import javax.servlet.http.*;
import java.io.*;

import ifs.fnd.service.*;
import ifs.fnd.os.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;


public class FileRequestHandler extends HttpServlet
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.servlets.FileRequestHandler");

   private static long   expires    = 30;
   private static String pragma     = null;
   private static String cache_ctrl = null;


   public FileRequestHandler()
   {
      System.out.println("Starting servlet ifs.fnd.servlets.FileRequestHandler");
   }

   public void init()
   {
      try
      {
         expires = Long.parseLong(getInitParameter("expires"));
      }
      catch(NumberFormatException e)
      {
         System.err.println("FileRequestHandler: wrong value of 'expires' argument.");
         expires = 30;
      }
      System.out.println("Setting expiration of static files to "+expires+" days.");

      pragma     = getInitParameter("pragma");
      cache_ctrl = getInitParameter("cache_control");
      System.out.println("Setting Pragma to: "+pragma);
      System.out.println("Setting Cache-Control to: "+cache_ctrl);
   }

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      String path = null;
      try
      {
         long now = Util.now();
         response.setDateHeader("Last-Modified", now - 72L*3600000L);   // three days before
         response.setDateHeader("Expires", now + expires*24L*3600000L); // days->msec

         /*
         Pragma: No-cache
         Cache-Control: no-cache

         .css -> Content-Type: text/css
         .js  -> Content-Type: text/javascript
         .png -> Content-Type: image/png
         .jpg ->               image/jpeg
         .gif -> Content-Type: image/gif
         */

         response.setHeader("Cache-Control", cache_ctrl);
         response.setHeader("Pragma", pragma);

         String url = request.getRequestURI().substring(request.getContextPath().length());
         path = getServletContext().getRealPath("/");
         char os_sep  = OSInfo.OS_SEPARATOR;
         int len = path.length();
         if(path.charAt(len-1)==os_sep) path = path.substring(0,len-1);
         path += replace(url,'/',os_sep);
         if(DEBUG)
         {
            debug("FileRequestHandler: requested file:");
            debug(path);
         }

         File file = new File(path);
         byte[] data = new byte[(int)file.length()];
         FileInputStream in = new FileInputStream(file);
         int cnt = in.read(data);

         response.setContentType( getContentType(path) );
         response.setContentLength(cnt);
         response.getOutputStream().write(data);

         if(DEBUG) debug("FileRequestHandler: "+cnt+" bytes written to response.");
      }
      catch( FileNotFoundException any )
      {
         System.err.println("FileRequestHandler: File not found: "+path);
         if(DEBUG) debug("FileRequestHandler: File not found: "+path);
      }
      catch( Throwable any )
      {
         System.err.println("FileRequestHandler:\n"+Str.getStackTrace(any));
         if(DEBUG) debug( Str.getStackTrace(any) );
      }
   }

   private void debug( String line )
   {
      Util.debug(line);
   }

   private String replace( String str, char from, char with )
   {
      if( Str.isEmpty(str) ) return str;

      AutoString buf = new AutoString();
      for(int i=0; i<str.length(); i++)
      {
         char ch = str.charAt(i);
         if(ch==from)
            buf.append(with);
         else
            buf.append(ch);
      }
      return buf.toString();
   }

   private String getContentType( String path )
   {
      String file_ext = path.substring( path.lastIndexOf(".") );
      return getServletContext().getMimeType(file_ext);
   }
}
