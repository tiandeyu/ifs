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
 * File        : RequestHandler.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2001-Feb-.. - Created
 *    Artur K
 *    Piotr Z  2001-Feb-.. - Changed
 *    Jacek P  2001-Mar-29 - Added handling of .page
 *    Piotr Z  2001-May-16 - Added unicode support (UTF8).
 *    Piotr Z  2001-Jun-07 - Added searching for presentation object candidates.
 *    Jacek P  2001-Sep-18 - Improved debugging technique.
 *    Daniel S 2002-Nov-19 - Added support for configfile in context-parameter
 *                           instead of init-parameter. This will give mobile support.
 *    Jacek P  2003-Jan-28 - Added possibilities for searchin JAR files.
 *                           Changed location of config file. The 'config_file'
 *                           parameter points out the directory now. Renamed to 'config_path'.
 *    Jacek P  2003-Feb-12 - Changed handling of debug file.
 *    Jacek P  2003-Feb-24 - 'config_path' parameter syntax independent of OS.
 *                           Use always '/' as a file separator.
 *    Jacek P  2004-May-07 - Better error handling. Adding support for Alert thread.
 *    Rifki R  2004-Jun-24 - Fecthed config parameters from web.xml for Alert thread.
 *    Ramila H 2004-Oct-18 - Implemented JSR168 support
 *    Mangala  2004-Nov-09 - Changes done for SSO for JSR168.
 *    Jacek P  2006-Feb-21 - Added debugging/statistics of the session object.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/04/28 sadhlk Bug 72387, Modified searchClassPath() and searchJarArchive() to correctly enforce PO security.
 * 2008/04/21 madrse Bug id 69076, Added Server functionality needed by new Aurora client
 * 2007/02/19 madrse Bug id 63644, Clear FndContext after performed service()
 * 2006/09/29 gegulk Bug id 58618, Added the method getModuleList() and relevent functionality
 * 2006/08/18 gegulk Bug id 59985, Removed the usages of the word "enum" as variable names
 *
 * 2006/08/01 rahelk Bug id 59663, Removed session handling of USER_ID
 *
 * Revision 1.3  2005/11/01 08:10:13  mapelk
 * Improve cache controling
 *
 * Revision 1.2  2005/10/28 05:42:21  rahelk
 * cache-control handling for supported browser
 *
 * Revision 1.1  2005/09/15 12:38:03  japase
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/13 08:47:26  mapelk
 * Proper handling of HTTP headers for browser caching.
 *
 * Revision 1.4  2005/06/27 10:06:10  riralk
 * Set content expiration for dynamic objects that are streamed to the browser (clustering support)
 *
 * Revision 1.3  2005/04/07 13:57:08  riralk
 * Changes for cluster support in web components and other improvements.
 *
 * Revision 1.2  2005/04/01 13:59:57  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.1  2005/01/28 18:07:27  marese
 * Initial checkin
 *
 * Revision 1.4  2005/01/05 13:09:04  riralk
 * Used response.setCharacterEncoding("UTF-8") when sending javascript to the client when dynamic object caching is enabled
 *
 * Revision 1.3  2004/12/29 09:10:08  japase
 * Added some additional debugging clauses for class loaders
 *
 * Revision 1.2  2004/12/15 11:05:25  riralk
 * Support for clustered environments by caching business graphics and generated javascript files in memory
 *
 * ----------------------------------------------------------------------------
 */



package ifs.fnd.servlets;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.security.*;
import java.lang.reflect.*;
import java.text.*;

import ifs.fnd.base.FndContext;
import ifs.fnd.asp.*;
import ifs.fnd.os.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.portal.GenericIFSPortlet;
import ifs.fnd.sf.storage.FndConnectionManager;

public class RequestHandler extends HttpServlet
{
   private static boolean DEBUG = false;
   private static long DEBUG_SES = -1;
   private static boolean STD_PORTAL_MODE_ENABLED = false;
   private static int EXPIRATION_NOT_GIVEN = 0;
   private static int EXPIRATION_GIVEN_AS_DATE = 1;
   private static int EXPIRATION_GIVEN_AS_DAYS = 2;

   private String[] path;
   private String[] clsname;
   private String   cfgpath;
   private long file_expiration;
   private int file_expiration_type;

   private long ie_page_expiration;
   private int ie_page_expiration_type ;
   private long netscape_page_expiration;
   private int netscape_page_expiration_type;
   private long mozilla_page_expiration;
   private int mozilla_page_expiration_type;
   //public static String debuginf_file;

   private static Hashtable pres_objects = new Hashtable(5000);
   private static Vector    portlet_cand = new Vector(500);
   private static SecureRandom    random = new SecureRandom();
   //public         HashMap   shared_data  = new HashMap();
   private static HashSet modules = new HashSet();

   public RequestHandler()
   {
      System.out.println("Starting servlet ifs.fnd.servlets.RequestHandler");
   }

   public void init()
   {
      try
      {
         DEBUG = "Y".equals(getInitParameter("DEBUG"));
         try
         {
            DEBUG_SES = 60000L * Long.parseLong(getInitParameter("DEBUG_SESSION")); // session time-out > 0
         }
         catch( NumberFormatException x )
         {
         }
         STD_PORTAL_MODE_ENABLED = "Y".equals(getInitParameter("std_portal_mode"));
         readCacheControlValues();
         if(DEBUG)
         {
            debug("RequestHandler.init()");
            ClassLoader cl = getClass().getClassLoader();
            if(cl!=null)
               debug("RequestHandler: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
            else
               debug("RequestHandler: current class loader is null");
         }

         if(DEBUG)
            debug("Initializing proxy connection cache by loading class ifs.fnd.sf.storage.FndConnectionManager");
         Util.initClass(FndConnectionManager.class);

         initPresentationObjectsList();

         String appdir = getServletConfig().getServletContext().getRealPath("");
         if(DEBUG)
            debug("RequestHandler: application directory: "+appdir);
         cfgpath = getServletConfig().getServletContext().getInitParameter("config_path");
         if(Str.isEmpty(cfgpath))
            cfgpath = getInitParameter("config_path");
         if(Str.isEmpty(cfgpath))
            cfgpath = appdir + (appdir.endsWith(File.separator) ? "" : File.separator) +
                      "WEB-INF" + File.separator + "config" + File.separator;

         cfgpath = ASPConfig.convertPath(cfgpath, appdir);
         if(DEBUG) debug("RequestHandler.init(): config_path="+cfgpath);

         String alert_file_location = getInitParameter("alert_file_location");
         if (Str.isEmpty(alert_file_location))
            alert_file_location=cfgpath;
         alert_file_location=ASPConfig.convertPath(alert_file_location,appdir);
         String alert_priority = getInitParameter("alert_thread_priority");
         String alert_interval = getInitParameter("alert_thread_interval");
         Alert.init(alert_file_location,alert_interval,alert_priority);

         String debuginf_file = createDebugFilename(cfgpath);
         DebugInfo.init(debuginf_file);
         if(DEBUG)
         {
            ClassLoader cl = DebugInfo.class.getClassLoader();
            if(cl!=null)
               debug("DebugInfo:      current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
            else
               debug("DebugInfo:      current class loader is null");

            cl = this.getClass().getClassLoader();
            if(cl!=null)
               debug("RequestHandler: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
            else
               debug("RequestHandler: current class loader is null");

            cl = Thread.currentThread().getContextClassLoader();
            if(cl!=null)
               debug("Current thread: context class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
            else
               debug("Current thread: context class loader is null");
         }

         String mask = getInitParameter("manager_mask");
         if(Str.isEmpty(mask))
            return;
         else
            mask = mask+";";
         StringTokenizer st = new StringTokenizer(mask, ";");
         int size = st.countTokens();
         path = new String[size];
         clsname = new String[size];

         size = 0;
         while( st.hasMoreTokens() )
         {
             String str = st.nextToken();
             int i = str.indexOf("=");
             path[size] = str.substring(0,i);
             clsname[size] = str.substring(i+1);
             size++;
         }
      }
      catch( RuntimeException any )
      {
         System.err.println("RequestHandler.init(): thrown exception\n"+any);
         throw any;
      }
   }


   public void destroy()
   {
      if(DEBUG) debug("RequestHandler.destroy()");
      Alert.stopTask();
      ASPPagePool.stopCleaner();
   }

   private void readCacheControlValues()
   {
      String file_exp = getInitParameter("file_expiration");
      if (!Str.isEmpty(file_exp))
      {
         try
         {
            file_expiration = Long.parseLong(file_exp)*24L*3600000L;
            file_expiration_type = EXPIRATION_GIVEN_AS_DAYS;
         }
         catch (NumberFormatException e) // expiration value is given as a date
         {
            try
            {
               file_expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(file_exp).getTime();
               file_expiration_type = EXPIRATION_GIVEN_AS_DATE;
            }
            catch (ParseException any)
            {
               System.err.println("RequestHandler.init(): invalid value for file_expiration "+e);
            }
         }
      }
      //read page expiration value for IE
      String page_exp = getInitParameter("ie_page_expiration");
      if (!Str.isEmpty(page_exp))
      {
         try
         {
            ie_page_expiration = Long.parseLong(page_exp)*24L*3600000L;
            ie_page_expiration_type = EXPIRATION_GIVEN_AS_DAYS;
         }
         catch (NumberFormatException e) // expiration value is given as a date
         {
            try
            {
               ie_page_expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(page_exp).getTime();
               ie_page_expiration_type = EXPIRATION_GIVEN_AS_DATE;
            }
            catch (ParseException any)
            {
               System.err.println("RequestHandler.init(): invalid value for ie_page_expiration "+e);
            }
         }
      }

      //read page expiration value for Netscape
      page_exp = getInitParameter("netscape_page_expiration");
      if (!Str.isEmpty(page_exp))
      {
         try
         {
            netscape_page_expiration = Long.parseLong(page_exp)*24L*3600000L;
            netscape_page_expiration_type = EXPIRATION_GIVEN_AS_DAYS;
         }
         catch (NumberFormatException e) // expiration value is given as a date
         {
            try
            {
               netscape_page_expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(page_exp).getTime();
               netscape_page_expiration_type = EXPIRATION_GIVEN_AS_DATE;
            }
            catch (ParseException any)
            {
               System.err.println("RequestHandler.init(): invalid value for netscape_page_expiration "+e);
            }
         }
      }

      //read page expiration value for Mozilla
      page_exp = getInitParameter("mozilla_page_expiration");
      if (!Str.isEmpty(page_exp))
      {
         try
         {
            mozilla_page_expiration = Long.parseLong(page_exp)*24L*3600000L;
            mozilla_page_expiration_type = EXPIRATION_GIVEN_AS_DAYS;
         }
         catch (NumberFormatException e) // expiration value is given as a date
         {
            try
            {
               mozilla_page_expiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(page_exp).getTime();
               mozilla_page_expiration_type = EXPIRATION_GIVEN_AS_DATE;
            }
            catch (ParseException any)
            {
               System.err.println("RequestHandler.init(): invalid value for mozilla_page_expiration "+e);
            }
         }
      }
   }

   private static void searchClassPath(File dir, String pkg)
   {
      if(DEBUG) debug("RequestHandler.searchClassPath()");

      File[] files = dir.listFiles();
      for (int i=0; i < files.length; i++ )
      {
         File file = files[i];

         if (file.isDirectory())
         {
            String temp_module = pkg+file.getName();
            if (temp_module.startsWith("ifs."))
            {
               temp_module = temp_module.substring(4);
               if (temp_module.indexOf(".")>-1)
               temp_module = temp_module.substring(0,temp_module.indexOf("."));
               if (!Str.isEmpty(temp_module))
               modules.add(temp_module);
            }
            searchClassPath(file, pkg+file.getName()+".");
         }
         else
         {
            String name = file.getName();
            if (file.isFile() && name.endsWith(".class") && name.indexOf("$")<0)
            {
               try
               {
                  name = pkg+name.substring(0,name.length()-6);
                  String correctName = name;
                  if(DEBUG) debug("RequestHandler - Found candidate: "+name);
                  if(name.startsWith("ifs.") && name.indexOf(".",4)>-1 && name.indexOf(".",name.indexOf(".",4)+1)>-1 && !name.contains("ifs.fnd.") && !pkg.endsWith(".portlets.")){
                        String pkgName = "ifs."+ name.substring(4,name.indexOf(".",4));//
                        String className = name.substring(name.lastIndexOf("."));
                        correctName = pkgName + className;
                  }
                  pres_objects.put(correctName, "*");
                  if (pkg.endsWith(".portlets."))
                     portlet_cand.add(name);
               }
               catch( Throwable any )
               {
                  System.err.println(any+" [path="+file.getPath()+" clsname="+name+"]");
               }
            }
            else
            {
               if(DEBUG) debug("Jump over file: "+file.getPath()+
                                  ifs.fnd.os.OSInfo.OS_SEPARATOR+name);
            }
         }
      }
   }

   private void searchJarArchive(File file) throws IOException
   {
      if(DEBUG) debug("RequestHandler.searchJarArchive():"+file.getAbsolutePath());

      JarFile jar_file = new JarFile(file);
      Enumeration jar_file_list = jar_file.entries();
      while (jar_file_list.hasMoreElements())
      {
         String name = ((JarEntry)(jar_file_list.nextElement())).getName();
         if( name.endsWith(".class") && name.indexOf("$")<0 )
         {
            try
            {
               name = Str.replace(name, "/", ".");
               name = name.substring(0,name.length()-6);
               String correctName = name;
               if(DEBUG) debug("RequestHandler - Found candidate: "+name);
               if(name.startsWith("ifs.") && name.indexOf(".",4)>-1 && name.indexOf(".",name.indexOf(".",4)+1)>-1 && !name.contains("ifs.fnd.") && !name.contains(".portlets.")){
                        String pkgName = "ifs."+ name.substring(4,name.indexOf(".",4));//
                        String className = name.substring(name.lastIndexOf("."));
                        correctName = pkgName + className;
                  }
               pres_objects.put(correctName, "*");
               String pkg = name.substring(0, name.lastIndexOf('.'));
               if (pkg.endsWith(".portlets"))
                  portlet_cand.add(name);
            }
            catch( Throwable any )
            {
               System.err.println(any+" [path="+file.getPath()+" clsname="+name+"]");
            }
         }
         else
         {
            if(DEBUG) debug("Jump over file: " + ifs.fnd.os.OSInfo.OS_SEPARATOR+name);
         }
      }
   }


   private void initPresentationObjectsList()
   {
      if(DEBUG) debug("RequestHandler.initPresentationObjectsList() - begin");

      String topdir = getServletContext().getRealPath("/") + ifs.fnd.os.OSInfo.OS_SEPARATOR +
                      "WEB-INF" + ifs.fnd.os.OSInfo.OS_SEPARATOR;

      String clspath  = topdir + "classes;";

      File file = new File(topdir+"lib");
      if( file.exists() && file.isDirectory() )
      {
         File[] files = file.listFiles();
         for( int i=0; i<files.length; i++ )
            if( !files[i].isDirectory() && files[i].getName().endsWith(".jar") )
               clspath = files[i].getPath() + ";" + clspath;
      }

      if(DEBUG) debug("  classpath="+clspath);

      StringTokenizer pst = new StringTokenizer(clspath, ";");

      while( pst.hasMoreTokens() )
      {
         file = new File(pst.nextToken());
         if( file.exists() && file.isDirectory() )
            searchClassPath(file, "");
         else if( file.getName().endsWith(".jar") )
         try
         {
            searchJarArchive(file);
         }
         catch( Throwable any )
         {
            System.err.println(any+" [path="+file.getPath()+"]");
         }
      }
      if(DEBUG) debug("===========================================");
      if(DEBUG) debug("Class path:"+clspath+"\nTop directory:"+topdir);
      if(DEBUG) debug("RequestHandler.initPresentationObjectsList() - end");
   }


   public Hashtable getPresentationObjectsList()
   {
      return pres_objects;
   }

   public Vector getPortletCandidatesList()
   {
      return portlet_cand;
   }

   public void resetPortletCandidatesList()
   {
      portlet_cand = null;
   }

   private String createDebugFilename(String cfgpath)
   {
      try
      {
         String debuginf_file =
            cfgpath +
            (cfgpath.charAt(cfgpath.length()-1)==OSInfo.OS_SEPARATOR ? "" : OSInfo.OS_SEPARATOR_STRING) +
            "Debug.inf";
         if(DEBUG) debug("RequestHandler: Debug flag info in file: "+debuginf_file);
         return debuginf_file;
      }
      catch(Throwable e)
      {
         if(DEBUG) debug("RequestHandler: Could not attain debug flag info. No need to panic.");
         return null;
      }
   }

   private void setCacheControls(HttpServletResponse response, boolean page, String userAgent)
   {
      //boolean page_caching = false;
      boolean page_expiration_as_date = false;
      long page_expiration = 0;
      long expires;

      if (page)
      {
         if (userAgent.indexOf("MSIE")>0)
         {
            //page_caching = ie_page_expiration > 0;
            if (ie_page_expiration_type == EXPIRATION_NOT_GIVEN) return;
            page_expiration = ie_page_expiration;
            page_expiration_as_date = (ie_page_expiration_type == EXPIRATION_GIVEN_AS_DATE);
         }
         else if (userAgent.indexOf("Netscape/7.")>0)
         {
            //page_caching = netscape_page_expiration > 0;
            if (netscape_page_expiration_type == EXPIRATION_NOT_GIVEN) return;
            page_expiration = netscape_page_expiration;
            page_expiration_as_date = (netscape_page_expiration_type == EXPIRATION_GIVEN_AS_DATE);
         }
         else if (userAgent.indexOf("Mozilla/5.0")>-1)
         {
            //page_caching = mozilla_page_expiration > 0;
            if (mozilla_page_expiration_type == EXPIRATION_NOT_GIVEN) return;
            page_expiration = mozilla_page_expiration;
            page_expiration_as_date = (mozilla_page_expiration_type == EXPIRATION_GIVEN_AS_DATE);
         }
         else
            return;
         expires = page_expiration + (page_expiration_as_date?0:Util.now());
      }
      else
      {
         if (file_expiration_type == EXPIRATION_NOT_GIVEN) return;
         expires = file_expiration + ((file_expiration_type == EXPIRATION_GIVEN_AS_DATE)?0:Util.now());
      }

      response.setHeader("Cache-Control","private");
      response.setHeader("Pragma", "dummy");
      response.setDateHeader("Expires", expires);
   }

   public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      try
      {
         serviceImpl(request, response);
      }
      finally
      {
         FndContext.setCurrentContext(null);
      }
   }

   private void serviceImpl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String pagepath = request.getRequestURI();
      int pos = pagepath.indexOf(DynamicObjectCache.URL_INDICATOR);
      if (pos>0)
      {
         pos = pos + DynamicObjectCache.URL_INDICATOR.length() + 1;
         String dyna_cache_key = pagepath.substring(pos,pagepath.length());
//////////.........................................................
         if (DEBUG) debug("Trying to fetch object from Dynamic object cache. [ key="+dyna_cache_key+" ]");

         DynamicObject dobj = DynamicObjectCache.get(dyna_cache_key, request.getSession());

         if (dobj != null)
         {
             String mime = dobj.getMime();
             //if mime is not set get it is using the file extension
             if ( "".equals(mime) || mime==null  )
             {
                String file_ext = pagepath.substring( pagepath.lastIndexOf(".") );
                mime = getServletContext().getMimeType(file_ext);
             }
             if (dobj.useStreamer())
             {
                String tmp = (String)dobj.getData(); // contains "class_name^key"
                int i = tmp.indexOf("^");
                String class_name=tmp.substring(0,i);
                String path=tmp.substring(i+1);
                try{

                    Class cls = Class.forName(class_name);
                    Class parent  = Class.forName("ifs.fnd.asp.ObjectStreamer");

                    if(cls.isInterface() || Modifier.isAbstract(cls.getModifiers()) || (!parent.isAssignableFrom(cls)) )
                       throw new ServletException("RequestHandler: "+class_name+" has not been implemented correctly");

                    Constructor ctr = cls.getConstructor(new Class[0]);
                    ObjectStreamer os = (ObjectStreamer)(ctr.newInstance(new Object[0]));
                    InputStream in = os.getFile(dyna_cache_key,path);

                    if (in==null)
                      throw new ServletException("RequestHandler: File denoted by "+pagepath+" does not exist");
                    setCacheControls(response,false, request.getHeader("User-Agent"));
                    response.setContentType(mime);
                    ServletOutputStream out = response.getOutputStream();

                    byte buf[] = new byte[1024*8]; // 8K buffer

                    while (true) {
                        int count = in.read(buf);
                        if (count <= 0)
                            break;
                        out.write(buf, 0, count);
                    }
                    in.close();
                    out.flush();
                    out.close();
                }
                catch( IllegalAccessException e )
                {
                   throw new ServletException("RequestHandler: "+e.getMessage());
                }
                catch( InvocationTargetException e )
                {
                   throw new ServletException("RequestHandler: "+e.getMessage());
                }
                catch( ClassNotFoundException e )
                {
                   throw new ServletException("RequestHandler: "+e.getMessage());
                }
                catch( NoSuchMethodException e )
                {
                   throw new ServletException("RequestHandler: "+e.getMessage());
                }
                catch( InstantiationException e )
                {
                   throw new ServletException("RequestHandler: "+e.getMessage());
                }
             }
             else if (mime.startsWith("text/"))
             {
               String text = (String)dobj.getData();
               setCacheControls(response, false, request.getHeader("User-Agent"));
               response.setContentType(mime+";charset=UTF-8");
               PrintWriter pout = response.getWriter();
               pout.write(text);
               pout.flush();
               pout.close();
             }
             else  //binary data
             {
               setCacheControls(response, false, request.getHeader("User-Agent"));
               response.setContentType(mime);
               ServletOutputStream sout = response.getOutputStream();
               sout.write((byte[])dobj.getData());
               sout.flush();
               sout.close();
             }
         }
         else
            debug("RequestHandler: Object "+pagepath+ " not found in dynamic cache");
      }
      else
      {
          String portlet_name = null;
          int window_state = 0;

          String qs = request.getQueryString();
          String attrname  = "renderURL";
          String attrvalue = (String)request.getAttribute(attrname);
          HttpSession session = request.getSession();

          setCacheControls(response, true, request.getHeader("User-Agent"));
          if (STD_PORTAL_MODE_ENABLED && pagepath.endsWith(GenericIFSPortlet.DEFAULT_URL))
          {
             /*
             if (session.getAttribute(GenericIFSPortlet.USER_REF_KEY) == null)
             {
                String auth_key = getUserAuthKey();
                session.setAttribute(GenericIFSPortlet.USER_REF_KEY,auth_key );
                shared_data.put(auth_key, session.getAttribute(GenericIFSPortlet.USER_ID));
             }
              */

             if(!Str.isEmpty(qs))
             {
                StringTokenizer st = new StringTokenizer(qs, "?&");

                while( st.hasMoreTokens() )
                {
                   String pardef = st.nextToken();
                   int ix = pardef.indexOf("=");
                   if(ix<0)
                      throw new ServletException("RequestHandler: Error in query string definition");

                   String name  = pardef.substring(0,ix);
                   String value = pardef.substring(ix+1);

                   if( GenericIFSPortlet.STD_PORTLET_NAME.equals(name))
                   {
                      portlet_name = value;
                   }
                   else if( GenericIFSPortlet.STD_PORTLET_STATE.equals(name))
                   {
                      try
                      {
                         String MINIMIZED_STATE = javax.portlet.WindowState.MINIMIZED.toString();

                         if((MINIMIZED_STATE).equals(value))
                            window_state = ASPPortal.MINIMIZED;
                         else //for normal and maximised modes
                            window_state = ASPPortal.NORMAL_MODE;
                      }
                      catch (Exception any)
                      {
                         window_state = ASPPortal.NORMAL_MODE;
                      }
                   }
                }
             }
          }
          /*
          else if (STD_PORTAL_MODE_ENABLED && (session.getAttribute(GenericIFSPortlet.USER_ID) == null))
          {
             Cookie[] cookies = request.getCookies();
             if (cookies != null)
                for (int i =0; i< cookies.length; i++)
                {
                   Cookie c = cookies[i];
                   if (GenericIFSPortlet.USER_REF_KEY.equals(c.getName()))
                   {
                      session.setAttribute(GenericIFSPortlet.USER_ID,shared_data.get(c.getValue()));
                      shared_data.remove(c.getValue());
                      break;
                   }
                }
          }
           */

          if(DEBUG)
          {
             debug("RequestHandler.service()");
             debug("Debug flags defined in file: "+DebugInfo.getDebugFileName());
             debug("RequestHandler.service: pagepath="+request.getRequestURI());
             debug("RequestHandler.service: query string="+qs);
             debug("RequestHandler.service: request attribute '"+attrname+"' = '"+attrvalue+"'");
             debug("RequestHandler.service: portlet_name="+portlet_name);

             ClassLoader cl = DebugInfo.class.getClassLoader();
             if(cl!=null)
                debug("DebugInfo: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
             else
                debug("DebugInfo: current class loader is null");
             cl = getClass().getClassLoader();
             if(cl!=null)
                debug("RequestHandler: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
             else
                debug("RequestHandler: current class loader is null");
          }

          response.setContentType("text/html;charset=utf-8");

          if( pagepath.endsWith("/scripts/Default.page") || pagepath.endsWith("/scripts/Navigator.page") ||
              pagepath.endsWith("/scripts/Default.asp")  || pagepath.endsWith("/scripts/Navigator.asp") )
             pagepath = request.getContextPath()+pagepath.substring(pagepath.lastIndexOf("/"));

          if(DEBUG) debug("RequestHandler: instantiating page: "+pagepath);

          String url = pagepath.substring(request.getContextPath().length());
          if(DEBUG) debug("RequestHandler: requested URI="+url);

          ASPManager mgr = null;

          if( path!=null )
          {
             for( int i=0; i<path.length; i++ )
             {
                if( url.startsWith(path[i]))
                {
                   try
                   {
                      mgr = (ASPManager)(Class.forName(clsname[i]).newInstance());
                      mgr.OnStartPage(request,response,this,cfgpath,pagepath);
                   }
                   catch( ClassNotFoundException e )
                   {
                      System.err.println(e.toString()+" - Could not find class: "+clsname[i]);
                      response.sendError(500,getClass().getName()+" - "+e.toString());
                   }
                   catch( InstantiationException e )
                   {
                      System.err.println(e);
                      response.sendError(500,getClass().getName()+" - "+e.toString());
                   }
                   catch( IllegalAccessException e )
                   {
                      System.err.println(e);
                      response.sendError(500,getClass().getName()+" - "+e.toString());
                   }
                   catch( Throwable any )
                   {
                      debug("RequestHandler: Caught an exception while executing subclass ASPManager.OnStartPage():\n"
                            + Str.getStackTrace(any) + "\n");
                   }
                   finally
                   {
                      if(mgr!=null)
                         mgr.OnEndPage();
                      if(DEBUG_SES>0) debugSession(request);
                   }
                   return;
                }
             }
          }

          try
          {
             mgr = new ASPManager();
             mgr.OnStartPage(request,response,this,cfgpath,pagepath,portlet_name,window_state);
          }
          catch( Throwable any )
          {
             //PrintWriter writer = response.getWriter();
             //writer.println(Str.getStackTrace(any));
             debug("RequestHandler: Caught an exception while executing ASPManager.OnStartPage():\n"
                   + Str.getStackTrace(any) + "\n");
          }
          finally
          {
             if(mgr!=null)
                mgr.OnEndPage();
             if(DEBUG_SES>0) debugSession(request);
          }
      }
   }

   private static void debug( String line )
   {
//      Util.debug(line);
	   System.out.println(line);
   }

   private String getUserAuthKey()
   {
      byte[] bytes = new byte[8];
      synchronized(random)
      {
         random.nextBytes(bytes);
      }
      return Util.toHexText(bytes);
   }

   // Debugging of the session object
   private void debugSession( HttpServletRequest request )
   {
      String session_id = null;
      String thred_id   = null;
      try
      {
         HttpSession session    = request.getSession(true);

         session_id = session.getId();
         thred_id   = ""+Thread.currentThread().hashCode();

         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         ObjectOutputStream    out  = new ObjectOutputStream(bout);

         //out.writeObject(session); session in Tomcat, implemented as org.apache.catalina.session.StandardSessionFacade, is not serializable
         Enumeration en = session.getAttributeNames();
         while( en.hasMoreElements() )
            out.writeObject( session.getAttribute( (String)en.nextElement() ) );

         out.close();

         byte[] bytes = bout.toByteArray();

         //Alert.add(thred_id+"; "+session_id+"; "+bytes.length);
         session_stat.put( session_id, new SessionDebugInfo(bytes.length) );
      }
      catch( IOException x )
      {
         String msg = "Exception while debugging session: "+session_id+":\n"+Str.getStackTrace(x);
         Util.debug(msg);
         Alert.add(msg);
      }
   }

   private static Hashtable session_stat = new Hashtable();

   private static class SessionDebugInfo
   {
      private long    timestamp;
      private int     bytes;
      private String  exception = null;

      private SessionDebugInfo( int objsize )
      {
         bytes = objsize;
         timestamp = System.currentTimeMillis() + DEBUG_SES;
      }

      private SessionDebugInfo( Exception ex )
      {
         exception = Str.getStackTrace(ex);
      }
   }

   public static String spoolSessionStatistics()
   {
      long totsize = 0;
      long currtime = System.currentTimeMillis();
      Iterator itr = session_stat.values().iterator();
      while( itr.hasNext() )
      {
         SessionDebugInfo info = (SessionDebugInfo)itr.next();
         if( info.timestamp > currtime )
            totsize += info.bytes;
         else
            itr.remove();
      }
      return "Summary of cache sizes:\r\n"+
                " - Total session size: "+((int)(totsize/1024))+" kB\r\n"+
                " - Total profile size: "+((int)(ASPManager.getProfileCacheSize()/1024))+" kB\r\n"+
                " - Total number of pages in the pool: "+ASPPagePool.getPageCounter();
   }

   public static void scheduleSessionStatisticsSpool()
   {
      Alert.add(spoolSessionStatistics());
   }

   public HashSet getModuleList()
   {
      return modules;
   }
}
