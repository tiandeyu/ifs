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
 * File        : ASPPortalProfile.java
 * Description : User profile for ASPPortal
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2000-Mar-21 - Created
 *    Jacek P  2000-Apr-17 - Added forceDirty() and showContents().
 *                           removed-flag moved from ASPPortletProvider.
 *    Jacek P  2000-Apr-18 - Added caching of HTML code. Changes in activate()
 *    Jacek P  2000-May-08 - Beta 4 release of Web Kit 3.0.0.
 *    Jacek P  2000-May-22 - Added handling of ClassNotFoundException
 *                           in ASPPortletHandle.activate()
 *    Jacek P  2000-May-31 - Changed default values for column configuration in construct()
 *    Jacek P  2000-Aug-02 - Renamed and reimplemented functions to- and fromByteString().
 *    Jacek P  2000-Aug-04 - Do not check security in findPortletHandle() (Log id: 300)
 *    Jacek P  2000-Aug-08 - Changes in clone() and rearrangeColumns()
 *    Jacek P  2000-Aug-14 - Added handling of total width of dynamic columns.
 *    Jacek P  2000-Oct-17 - Implementation of the automatic refresh (log id #427).
 *    Jacek P  2000-Nov-16 - Instantiate portlets if the HTML-cache is empty. Changed
 *                           interface of the activate() methods (Log id #331).
 *    Artur K  2000-Nov-23 - Changes regarding replacing ASPPoolElementProfile
 *    Piotr Z                with ASPPoolElementProfile
 *                           Removed MAXTITLE attribute and its asscociated functions.
 *    Jacek P  2000-Dec-11 - Added back MAX- & MINTITLE functionality.
 *    Jacek P  2000-Dec-21 - Improved implementation of the clone() methods.
 *                           Improved error handling in activate() and reset().
 *    Jacek P  2000-Feb-05 - Classes ASPPortalColumn and ASPPortletHandle moved
 *                           to their own files.
 *    Piotr Z  2001-Mar-12 - Clear method global profile info written within portlets which are
 *                           going to be removed from portal in rearrangeColumns() method.
 *    Chandana 2002-Feb-12 - Changed fromByteString() and toByteStrings()methods to be compatible with UTF-8
 *    Sampath  2003-Feb-28 - fixed problems in portal copy introduced new method getNewProfileInstance( )
 *    Ramila H 2003-May-30 - Added functionality for multi-column portlet.
 *    Ramila H 2003-Jun-19 - Log id 1078 implemented.
 *    Ramila H 2003-Juy-26 - Log id 1080, import profile from xml file implemented.
 *    Jacek P  2004-Jan-04 - Bug#40931. Added handling of session related data from ASPPortletHandle.
 *    Suneth M 2004-Jun-17 - Merged correction for Bug#45369. Changed rearrangeColumns().
 *    Suneth M 2004-Aug-04 - Changed duplicate localization tags.
 *    Ramila H 2004-10-18  - Implemented JSR168 support.
 *    Jacek P  2004-Nov-11 - Introduced API due to new profile handling.
 * ------------------------------------------------------------------------------------------------------
 * New Comments:
 * 2008/07/09 sadhlk Bug 73745, Added code to check DEBUG condition before calling debug() method.
 *               2007/10/17 sadhlk
 * Bug id 67682, Modified updateMembers(), updateProfileBuffer().
 *
 *               2007/05/04 buhilk
 * Bug id 64889. Modified updateProfileBuffer() to remove any Mylinks menu items from the navigator menu that doesn't belong to any MyLinks portlet.
 *
 *               2007/05/04 sadhlk
 * Bug id 64337, Added constant 'PORTLET_ISOWN', Modified updateMembers() and updateProfileBuffer().
 *               2006/10/25 riralk
 * Bug id 57025. Modified parse() and correctParsedBuffer() to fix MyLinks URL to point to new environment during profile conversion.
 *
 * Revision 1.2  2006/01/09 11:50:58  rahelk
 * Fixed called id 130710 - Merged bug id 50447. Bug when copying portal views and configuring.
 *
 * Revision 1.2  2005/09/28 11:50:58  japase
 * Added method isReset() to make it possible to check if portlets have been reset.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/05 11:42:59  riralk
 * Gave package level access to constants to support  XML-export and  XML-import in new profile framework. Used by ASPPortal.setViewName()
 *
 * Revision 1.6  2005/07/14 06:54:40  japase
 * New sort algorithm for ProfileBuffers
 *
 * Revision 1.5  2005/07/12 10:33:43  japase
 * Correction to the profile handling - manipulated buffers, for example on sort, have been marked as dirty.
 *
 * Revision 1.4  2005/05/06 09:56:42  mapelk
 * changes required to make standard portlets (JSR 168) to run on WebSphere
 *
 * Revision 1.3  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.2.2.3  2005/03/08 14:35:51  riralk
 * Fixed Call id 122420. Pressing Default button in a portlet now clears the portlets profile in the DB. Modified updateProfileBuffer().
 *
 * Revision 1.2.2.2  2005/03/03 11:48:56  riralk
 * Removed cloning of profile buffer in ASPPortalProfile.clone() and some minor fixes.
 *
 * Revision 1.2.2.1  2005/03/02 10:58:48  riralk
 * Bug fixes for Portal profile.
 *
 * Revision 1.2  2005/02/24 13:48:59  riralk
 * Adapted Portal profiles to new profile algorithm. Removed some obsolete code.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.3  2004/12/16 11:56:40  japase
 * First changes for support of the new profile concept.
 *
 * Revision 1.2  2004/12/14 08:53:23  sumelk
 * Changed rearrangeColumns() to avoid errors when trying to reset the undefined portlets.
 *
 * ------------------------------------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;
import java.lang.Math;
import java.lang.reflect.*;

/**
 * ASPPortal.doFreeze() creates the default instance of ASPPortalProfile
 * which is stored in the page pool as the mutable attribute 'profile'.
 *
 * During every request ASPPortal.?????() fetches the user specific profile
 * by calling ASPProfile.getProfile(this,pre_profile). The returned Object
 * is assigned to the 'profile'-attribute, maybe marking the portal as DIRTY.
 *
 * ASPPortal.doReset() re-assigns the default value of the 'profile'-attribute.
 *
 * Modified profile are stored in the cache (as Object) and in the database
 * (as String).
 */
class ASPPortalProfile extends ASPPoolElementProfile
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortalProfile");

  //constants for new profile handling
   static final String PORTAL_DEF      = ProfileUtils.ENTRY_SEP + "Portal Definition";
   static final String MIN_PORTLETS    = "MinimizedPortlets"+PORTAL_DEF;
   static final String NEXT_ID         = "NextID"+PORTAL_DEF;
   static final String REFRESH_TIME    = "RefreshTime"+PORTAL_DEF;
   static final String WIDE_COL_EXISTS = "WideColumnExists"+PORTAL_DEF;

   //constants for column data
   static final String COLUMN          = "Column_";
   static final String COLUMN_DEF      = "Definition"+ProfileUtils.ENTRY_SEP+"Column Node";
   static final String COLUMN_WIDTH    = "Width";
   static final String COLUMN_APPEAR   = "Appearance";

   //constants for portlet data
   static final String PORTLET_DEF     = "Portlet"+ProfileUtils.ENTRY_SEP;
   static final String PORTLET_POS     = "Position";
   static final String PORTLET_ISOWN    = "IsOwn";
   static final String PORTLET_CLS     = "ClassName";
   static final String PORTLET_PRF     = "Profile";
   
   //constants for column data(UPPERCASE)
   static final String COLUMN_WIDTH_CAP = "WIDTH";
   static final String COLUMN_APPEAR_CAP = "APPEARANCE";
   
   //constants for portlet date(UPPERCASE)
   static final String PORTLET_POS_CAP      = "POSITION";
   static final String PORTLET_ISOWN_CAP    = "IS_OWN";
   static final String PORTLET_CLS_CAP      = "CLASS_NAME";
   static final String PORTLET_PRF_CAP      = "PROFILE";

   // profile contents
   private ProfileBuffer profbuf;

   private Vector columns = new Vector();
   private int    colcnt;
   private int    dyncolcnt;
   private int    dyncolwidth;
   private int    nextid;
   private String minboxes;
   private Buffer user_portlets;
   private int    refresh_time;
   private int    wide_column;

   private final int MULTI_INDEX = 4;
   final int MULTI_WIDTH = 500;

   //properties used only during profile conversion
   private static final String FROM_SITES = System.getProperty("from_sites");
   private static String[] FROM_SITES_ARR; 
   private static final String BASE_URL = "#BASE_URL#";

   //==========================================================================
   //  Construction
   //==========================================================================

   protected ASPPortalProfile()
   {
   }


   /**
    * Inherited interface. Creates a new instance of this class.
    * Called from ASPProfile class.
    */
   protected ASPPoolElementProfile newInstance()
   {
      return new ASPPortalProfile();
   }


   public Object clone()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.clone()");

      ASPPortalProfile prf = new ASPPortalProfile();

      synchronized(this)
      {
         prf.colcnt       = colcnt;
         prf.dyncolcnt    = dyncolcnt;
         prf.dyncolwidth  = dyncolwidth;
         prf.nextid       = nextid;
         prf.minboxes     = null;
         prf.refresh_time = refresh_time;
         prf.wide_column  = wide_column;

         for(int c=0; c<columns.size(); c++)
         {
            ASPPortalColumn col = getColumn(c);
            ASPPortalColumn newcol = new ASPPortalColumn( col.getWidth(), col.isDynamic() );
            prf.columns.addElement(newcol);

            for(int p=0; p<col.countPortlets(); p++)
               newcol.addPortletHandle((ASPPortletHandle)(col.getPortletHandle(p).clone()));
         }

         //prf.profbuf = profbuf; // point out the same ProfileBuffer ?

      }
      if(DEBUG) debug(this+": clone(): "+prf);
      return prf;
   }


   /**
    * Inherited interface. Construct the default profile information according
    * to ASPConfig.ifm file.
    * Called from ASPPortal.createBaseProfile() function (an instance of ASPPortal
    * class is send as input parameter 'template').
    * Fill vector 'columns' with instances of the inner class ASPPortalColumn.
    * Each entry in this vector coRresponds to a column in the portal.
    * Call private method initPortlet() to create instances of portlets and put them
    * into an internal vector in the ASPPortalColumn class.
    */
   protected void construct( ASPPoolElement template )
   {
      if(DEBUG) debug(this+": ASPPortalProfile.construct()");
      ASPPortal portal = (ASPPortal)template;

      if (portal.isStdPortlet()) return; //not necessary to initiate portlets for standard (JSR168) portlets

      try
      {
         ASPConfig cfg = portal.getASPPage().getASPConfig();
         String coldefs = cfg.getParameter("PORTAL/COLUMNS", "288:S,288:S,144:S"); //"320:D,320:D,160:S");
         if(DEBUG) debug("  coldefs="+coldefs);

         ASPPortalColumn col;
         int c = 0;
         int p = 0;
         StringTokenizer st = new StringTokenizer( coldefs, ", \t");
         while( st.hasMoreTokens() )
         {
            String t = st.nextToken();

            int pos = t.indexOf(':');
            int cwidth = Integer.parseInt(t.substring(0,pos));
            boolean dynw = "D".equalsIgnoreCase(t.substring(pos+1));
            colcnt++;
            if(dynw)
            {
               dyncolcnt++;
               dyncolwidth = dyncolwidth + cwidth;
            }
            col = new ASPPortalColumn(cwidth,dynw);
            columns.addElement(col);
            c++;
            String pdefs = cfg.getParameter("PORTAL/PORTLETS/DEFAULT/COLUMN_"+c, "");
            if(DEBUG) debug("  pdefs="+pdefs);
            StringTokenizer pst = new StringTokenizer( pdefs, ", \t");
            while( pst.hasMoreTokens() )
            {
               String pt = pst.nextToken();
               initPortlet(col,pt,""+p);
               p++;
            }
            nextid = p;
         }
      }
      catch( Throwable e )
      {
         portal.error(e);
      }
   }


   Buffer getUserPortlets()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.getUserPortlets()");
      return user_portlets;
   }


   void setUserPortlets( Buffer user_portlets )
   {
      if(DEBUG) debug(this+": ASPPortalProfile.setUserPortlets()");
      this.user_portlets = user_portlets;
   }


   int countColumns()
   {
      return columns.size();
   }


   int countDynamicColumns()
   {
      return dyncolcnt;
   }


   int getDynamicColTotWidth()
   {
      return dyncolwidth;
   }


   ASPPortalColumn getColumn( int index )
   {
      return (ASPPortalColumn)columns.elementAt(index);
   }


   String getMinimizedBoxes()
   {
      return minboxes;
   }


   void setMinimizedBoxes( String minboxes )
   {
      this.minboxes = minboxes;
   }


   int getRefreshTime()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.getRefreshTime():"+refresh_time);
      return refresh_time;
   }


   void setRefreshTime( int refresh_time )
   {
      if(DEBUG) debug(this+": ASPPortalProfile.setRefreshTime("+refresh_time+")");
      this.refresh_time = refresh_time;
   }

   boolean isWideColumnAvailable()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.isWideColumnAvailable():"+wide_column);
      return (wide_column == 1);
   }

   void enableWideColumn()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.enableWideColumn()");
      this.wide_column = 1;
   }

   void disableWideColumn()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.disableWideColumn()");
      this.wide_column = 0;
   }

/*   void setWideColumnIndex(int wide_index)
   {
      if(DEBUG) debug(this+": ASPPortalProfile.setWideColumn:"+wide_index);
      this.wide_column = wide_index;
   }
*/
   int getWideColumnIndex()
   {
      if(DEBUG) debug(this+": ASPPortalProfile.getWideColumn()");

      if (!isWideColumnAvailable()) return -1;

      for(int c=0; c<columns.size(); c++)
      {
         ASPPortalColumn col = getColumn(c);
         if (col.getWidth() == MULTI_WIDTH)
            return c;
      }
      //return -1;
      return countColumns()-1;
   }

   int getWideColumn()
   {
      return wide_column;
   }

   int getNextId()
   {
      return nextid;
   }


   ASPPortletHandle findPortletHandle( ASPPortal portal, String id ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalProfile.findPortletHandle("+id+")");

      if( Str.isEmpty(id) ) return null;

      for(int c=0; c<columns.size(); c++)
      {
         if(DEBUG) debug("  column "+c);

         ASPPortalColumn col = getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            if(DEBUG) debug("  provider id="+ph.getId());
            //if(!ph.isUserAvailable()) continue;
            if( id.equals(ph.getId()) )
            {
               if(DEBUG) debug("  portlet handle found.");
               return ph;
            }
         }
      }

      if( !portal.isStdPortlet() )
         return null;

      ASPPortalColumn col;
      if( columns.size()==0 )
      {
         col = new ASPPortalColumn(288,true);
         columns.addElement(col);
      }
      else
         col = getColumn(0);

      return initPortlet( col, portal.getASPPage().getStdPortletName(), id );
   }

   //Bug 40931, start
   void rearrangeColumns( ASPPortal portal, String custcols, ASPPage toppage ) throws FndException
   //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortalProfile.rearrangeColumns("+portal+","+custcols+")");

      ASPManager mgr = portal.getASPManager();
      Vector newcols = new Vector();
      int size = columns.size();

      for(int c=0; c<size; c++)
      {
         ASPPortalColumn col = getColumn(c);
         ASPPortalColumn newcol = new ASPPortalColumn( Integer.parseInt(mgr.isEmpty(mgr.readValue("col"+c+"width")) ? "0" : mgr.readValue("col"+c+"width")),
                                                       "TRUE".equals(mgr.readValue("col"+c+"type")) );

         newcols.addElement(newcol);

         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            ph.setRemoved(true);
         }
      }

      ASPPortalColumn newcol = null;
      int c = -1;
      StringTokenizer st = new StringTokenizer( custcols, ", \t");
      while( st.hasMoreTokens() )
      {
         String t = st.nextToken();
         if(DEBUG) debug("  token="+t);

         if(t.startsWith("C="))
         {
            c = Integer.parseInt(t.substring(2));
            newcol = (ASPPortalColumn)(newcols.elementAt(c));
         }
         else if(t.startsWith("P="))
         {
            initPortlet( newcol, t.substring(2), nextid+"" );
            nextid++;
         }
         else
         {
            ASPPortletHandle ph = findPortletHandle(portal, t);
            newcol.addPortletHandle(ph);
            ph.setRemoved(false);
         }
      }

      // reset removed portlets
      for(c=0; c<size; c++)
      {
         ASPPortalColumn col = getColumn(c);

         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            if( ph.isRemoved() )
            {
               //Bug 40931, start
               ASPPortletProvider portlet = ph.getPortlet(portal);
               //Bug 40931, end
               if( DEBUG) debug("Portlet: "+portlet+"!!!");
               if (portlet == null)
               {
                  //Bug 40931, start
                  ph.activate(portal, true, ph.getId(), null, toppage);
                  portlet = ph.getPortlet(portal);
                  //Bug 40931, end
               }
               if (portlet != null && portlet.isDefined())
               {
                  portlet.clearGlobalProfile();
                  portlet.doReset();
               }

               //Bug 40931, start
               ph.reset(portal,mgr,toppage);
               //Bug 40931, end
            }
         }
      }

      columns = newcols;

      // new size: remove empty columns or add columns
      int newsize = Integer.parseInt(mgr.readValue("colnum"));
      if(newsize>size)
      {
         for(c=size; c<newsize; c++)
            columns.addElement(new ASPPortalColumn(144,false));
      }
      else if(newsize<size)
      {
         int delta = size - newsize;
         if(DEBUG) debug("  newsize="+newsize+", size="+size+", delta="+delta);
         newcols = new Vector();
         for(c=0; c<size; c++)
         {
            if(DEBUG) debug("    Fetching old column "+c);
            ASPPortalColumn col = getColumn(c);
            int pcnt = col.countPortlets();
            if(DEBUG) debug("    num of portlets="+pcnt+", delta="+delta);
            if(pcnt>0 || delta<=0)
               newcols.addElement(col);
            else
               delta--;
         }
         if(DEBUG) debug("  newcols.size()="+newcols.size());
         columns = newcols;
      }

      // update column counters
      colcnt      = columns.size();
      dyncolcnt   = 0;
      dyncolwidth = 0;

      for(c=0; c<colcnt; c++)
      {
         ASPPortalColumn col = getColumn(c);
         if(col.isDynamic())
         {
            dyncolcnt++;
            dyncolwidth = dyncolwidth + col.getWidth();
         }
      }

      //activate(portal,null,null);
      //Bug 40931, start
      activate(portal,true,null,null,toppage);
      //Bug 40931, end
   }


   /**
    * Private method called from construct().
    * Dynamically create an instance of a portlet class specified by the name.
    */
   private ASPPortletHandle initPortlet( ASPPortalColumn col, String clsname, String id ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalProfile.initPortlet("+col+","+clsname+","+id+")");

      //Bug 40931, start
      //ASPPortletHandle colport = new ASPPortletHandle(clsname, id, null);
      ASPPortletHandle colport = new ASPPortletHandle(clsname, id);
      //Bug 40931, end
      col.addPortletHandle(colport);
      return colport;
   }


   /**
    * Inherited interface.
    * Deserialize profile information in a given string and apply to an ASPPoolElement.
    * Called from ASPProfile.findProfile() after fetching from the database.
    *
    * @deprecated
    */
   protected void parse( ASPPoolElement target, String text ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalProfile.parse():\n\t\t"+text);

      // if(columns.size()>0) throw new FndException(...);

      try
      {
         ASPPortal       portal = (ASPPortal)target;
         ASPPage         page   = portal.getASPPage();
         ASPConfig       cfg    = page.getASPConfig();
         Buffer          buf    = cfg.getFactory().getBuffer();
         BufferFormatter frmt   = cfg.getFactory().getBufferFormatter();


         // Temporary solution:
         // in future release parsing should be done by a new class Base64BufferFormatter
         text = toByteString( Util.fromBase64Text( text ) );
         if(DEBUG) debug("  from Base64="+text);
         frmt.parse( text, buf );
         if(DEBUG) debug("Parsed Portal Profile:\n"+Buffers.listToString(buf));

         minboxes     = buf.getString( "MIN_BOXES", null);
         nextid       = buf.getInt(    "NEXTID"         );
         colcnt       = buf.getInt(    "COLCOUNT"       );
         refresh_time = buf.getInt(    "REFRESH_TIME",0 );
         wide_column  = buf.getInt(    "WIDE_COLUMN",0  );

         dyncolcnt   = 0;
         dyncolwidth = 0;

         for(int c=0; c<colcnt; c++)
         {
            Buffer b = buf.getBuffer( "COLUMN_"+c );

            int cwidth = b.getInt( "WIDTH" );
            boolean dynw = "D".equalsIgnoreCase( b.getString("APPEARANCE") );
            if(dynw)
            {
               dyncolcnt++;
               dyncolwidth = dyncolwidth + cwidth;
            }

            ASPPortalColumn col = new ASPPortalColumn(cwidth,dynw);
            columns.addElement(col);

            int portcnt = b.getInt("PORTCOUNT");

            for(int p=0; p<portcnt; p++)
            {
               ASPPortletHandle colport = new ASPPortletHandle();
               colport.load( page, b.getBuffer("PORTLET_"+p) );
               col.addPortletHandle(colport);
            }
         }
      }
      catch( Exception any )
      {
         throw new FndException("FNDPORPRFPRSPORT: Cannot parse profile for ASPPortal: '&1'", text)
                   .addCaughtException(any);
      }
   }

  /*
   * Called when importing proflie from XML from ASPPortal, not necessary now?
   * calling assign() instead
   *
   * @deprecated
   */
   protected void parse( ASPPoolElement target, Buffer buf ) throws FndException
   {
      try
      {
         ASPPortal       portal = (ASPPortal)target;
         ASPPage         page   = portal.getASPPage();
         ASPConfig       cfg    = page.getASPConfig();
         BufferFormatter frmt   = cfg.getFactory().getBufferFormatter();


         // Temporary solution:
         // in future release parsing should be done by a new class Base64BufferFormatter

         minboxes     = buf.getString( "MIN_BOXES", null);
         nextid       = buf.getInt(    "NEXTID"         );
         colcnt       = buf.getInt(    "COLCOUNT"       );
         refresh_time = buf.getInt(    "REFRESH_TIME",0 );
         wide_column  = buf.getInt(    "WIDE_COLUMN",0  );

         dyncolcnt   = 0;
         dyncolwidth = 0;

         for(int c=0; c<colcnt; c++)
         {
            Buffer b = buf.getBuffer( "COLUMN_"+c );

            int cwidth = b.getInt( "WIDTH" );
            boolean dynw = "D".equalsIgnoreCase( b.getString("APPEARANCE") );
            if(dynw)
            {
               dyncolcnt++;
               dyncolwidth = dyncolwidth + cwidth;
            }

            ASPPortalColumn col = new ASPPortalColumn(cwidth,dynw);
            columns.addElement(col);

            int portcnt = b.getInt("PORTCOUNT");

            for(int p=0; p<portcnt; p++)
            {
               ASPPortletHandle colport = new ASPPortletHandle();
               colport.load( page, b.getBuffer("PORTLET_"+p) );
               col.addPortletHandle(colport);
            }
         }
      }
      catch( Exception any )
      {
         throw new FndException("FNDPORPRFPRS: Cannot parse profile")
                   .addCaughtException(any);
      }
   }


   /**
    * Inherited interface.
    * Serialize profile information from a given ASPPoolElement to a string.
    * Called from ASPProfile.save() before storing in the database.
    *
    * @deprecated
    */
   protected String format( ASPPoolElement target ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalProfile.format("+target+")");
      try
      {
         ASPPage         page = ((ASPPortal)target).getASPPage();
         ASPConfig       cfg  = page.getASPConfig();
         Buffer          buf  = cfg.getFactory().getBuffer();
         BufferFormatter frmt = cfg.getFactory().getBufferFormatter();

         int csize = columns.size();

         buf.addItem( "MIN_BOXES",    minboxes     );
         buf.addItem( "NEXTID",       nextid       );
         buf.addItem( "COLCOUNT",     csize        );
         buf.addItem( "REFRESH_TIME", refresh_time );
         buf.addItem( "WIDE_COLUMN",  wide_column  );

         for(int c=0; c<csize; c++)
         {
            if(DEBUG) debug("  formatting column "+c);

            Buffer bc = buf.newInstance();

            ASPPortalColumn col = (ASPPortalColumn)(columns.elementAt(c));
            int psize = col.countPortlets();

            bc.addItem( "WIDTH",      col.getWidth() );
            bc.addItem( "APPEARANCE", col.isDynamic() ? "D" : "S");
            bc.addItem( "PORTCOUNT",  psize );

            for(int p=0; p<psize; p++)
            {
               if(DEBUG) debug("    formatting portlet "+p);

               ASPPortletHandle cp = col.getPortletHandle(p);
               bc.addItem( "PORTLET_"+p, cp.save(page) );
            }

            buf.addItem( "COLUMN_"+c, bc );
         }

         // Temporary solution:
         // in future release formatting should be done by a new class Base64BufferFormatter
         if(DEBUG) debug("Portal Profile to format:\n"+Buffers.listToString(buf));
         String s = frmt.format(buf);
         if(DEBUG) debug("  buf="+s);
         s = Util.toBase64Text( fromByteString( s ) );
         if(DEBUG) debug("  to Base64="+s);

         return s;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDPORPRPPRS: Cannot format profile for ASPPortal.")
                   .addCaughtException(any);
      }
   }

   //==========================================================================
   // New profile handling
   //==========================================================================

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Deserialize profile information in a given string to a Buffer
    * without needing to access any run-time objects.
    * Used for conversion between the old and new profile framework.
    * Use ProfileBuffer class as Buffer implementation.
    * Set state of each simple item (instance of ProfileItem to QUERIED).
    */
   protected void parse( ProfileBuffer buffer, BufferFormatter fmt, String text ) throws FndException
   {
      /*
      With exception of portlet definitions, in all cases Profile Entry is set to 'Portal Definition'
      For portlets Portal Entry is equal portlet ID


      Single Portal View definition:     (ok)
      ==============================

      Old:
      ----

      $<view_id>=:                                        // Portal View ID unique within the protal
         !
         0:$MIN_BOXES=?                                   // minimized portlets
         1:$NEXTID=<next_id>                              // next portlet ID
         2:$COLCOUNT=<n>                                  // number of columns within the view; OBSOLETE!
         3:$REFRESH_TIME=<x>                              // portal refresh time
         4:$WIDE_COLUMN=[0|1]                             // flag detecting existence of wide column
         5:$<single_column_definition>                    // column definitions according to the specification below
         ...

      New:
      ----

      $<view_id>=:                                        // Portal View ID unique within the protal
         !
         0:$MinimizedPortlets^Portal Definition=?         // minimized portlets
         1:$NextID^Portal Definition=<next_id>            // next portlet ID; just suggestion - generation should always check duplicates
         3:$RefreshTime^Portal Definition=<x>             // portal refresh time
         4:$WideColumnExists^Portal Definition=[0|1]      // flag detecting existence of wide column
         5:$<single_column_definition>                    // column definitions according to the specification below
         ...


      Single column definition:    (not ok - duplicates!!!)
      =========================

      Old:
      ----

      $COLUMN_<n>=:                                       // always has a unique order number within a view
         !
         0:$WIDTH=<width>                                 // column width; typically 144 or 288; 500 for wide column
         1:$APPEARANCE=[S|D]                              // (S)tatic or (D)ynamic
         2:$PORTCOUNT=<portlet_count>                     // number of portlets within column; OBSOLETE!
         3:$<single_portlet_definition>                   // portlet definitions according to the specification below
         ...

      New:
      ----

      $Column=:                                           // order number removed
         !
         0:$Definition^<no>=:                             // order number as 'profile entry' of a new attribute               ??????
            !
            0:$Width=<width>                              // column width; typically 144 or 288                                  |
            1:$Appearance=[S|D]                           // (S)tatic or (D)ynamic                                               \/
         1:$<single_portlet_definition>                   // portlet definitions according to the specification below
         ...


      Single portlet definition:    (ok)
      ==========================

      Old:
      ----

      $PORTLET_<n>=:                                      // always has a unique order number within a column
         !
         0:$CLSNAME=<qualified_portlet_class_name>
         1:$ID=<portlet_id>                               // unique portlet ID; has to be unique within the entire portal
         2:$PROFILE=:                                     // arbitrary; exists if a portlet has profile
            !
            <portlet_profile_buffer>

      New:
      ----

      $Portlet^<portlet_id>=:                             // portlet ID as 'profile entry'
         !
         0:$Position=<no>                                 // order number as attribute
         1:$ClassName=<qualified_portlet_class_name>
         2:$Profile=:                                     // arbitrary; exists if a portlet has profile
            !
            <portlet_profile_buffer>

      */

      if(DEBUG) debug(this+": ASPPortalProfile.parse():\n\t\t"+text);
      try
      {
         buffer.clear();

         text = toByteString( Util.fromBase64Text( text ) );
         if(DEBUG) debug("  from Base64="+text);
         fmt.parse( text, buffer );
         if(DEBUG) debug("Parsed Portal Profile:\n"+Buffers.listToString(buffer));
         if (!Str.isEmpty(FROM_SITES))
           FROM_SITES_ARR = FROM_SITES.split(",");         
         correctParsedBuffer(buffer, 0);
         if(DEBUG) debug("Portal Profile after restructure:\n"+Buffers.listToString(buffer));
      }
      catch( Throwable any )
      {
         if(DEBUG) debug("Cought exception:\n"+Str.getStackTrace(any) );
         throw new FndException("FNDPORTALPRPARSETOBUF: Cannot parse profile for ASPPortal: '&1'",text)
                   .addCaughtException(any);
      }
   }

   /**
    * Adapt the ProfileBuffer to the new profile handling by marking all
    * leaves with profile entry "Portal Definition", except portlets that are marked with portlert ID.
    */
   private void correctParsedBuffer( ProfileBuffer buffer, int level ) throws FndException
   {
      for( int i=buffer.countItems()-1; i>=0; i-- )
      {
         ProfileItem item   = (ProfileItem)buffer.getItem(i);
         String      name   = item.getName();
         boolean     remove = false;

         if( item.isCompound() )
         {
            // if name==COLUMN_<n>, change to 'Column' and create compound item 'Definition^...' AFTER recursive function call.
            // but fetch items WIDTH and APPEARANCE BEFORE and change names.

            // if name==PORTLET_<n>, change to 'Portlet^ID' and create item 'Position' AFTER recursive function call.
            // but ID has to be fetched BEFORE running the function!

            ProfileBuffer buf        = (ProfileBuffer)item.getBuffer();
            boolean       columndef  = level==0 && name!=null && name.startsWith("COLUMN_");
            boolean       portletdef = level==1 && name!=null && name.startsWith("PORTLET_");
            String        id         = null;
            ProfileItem   i1         = null;
            ProfileItem   i2         = null;

            if(columndef)
            {
               i1 = (ProfileItem)buf.getItem("WIDTH");
               i2 = (ProfileItem)buf.getItem("APPEARANCE");
            }
            else if(portletdef)
               id = buf.getString("ID");

            if( level<2 )
               correctParsedBuffer(buf, level+1);

            if(columndef)
            {
               item.setName("Column_"+Integer.parseInt( name.substring("COLUMN_".length()) ));
               //Item it = new Item( "Position"+ProfileUtils.ENTRY_SEP+"Portal Definition",
               //                    Integer.parseInt( name.substring("COLUMN_".length()) ) );
               //Item it = new Item( "Definition"+ProfileUtils.ENTRY_SEP+Integer.parseInt( name.substring("COLUMN_".length()) ) );
               ProfileItem it = (ProfileItem)buffer.newItem();
               //it.setName("Definition"+ProfileUtils.ENTRY_SEP+Integer.parseInt( name.substring("COLUMN_".length()) ) );
               it.setName("Definition"+ProfileUtils.ENTRY_SEP+ "Column Node" );
               it.setState(ProfileItem.QUERIED);

               ProfileBuffer b = (ProfileBuffer)buf.newInstance();
               i1.setName("Width");
               i2.setName("Appearance");
               b.addItem(i1);
               b.addItem(i2);
               it.setValue(b);

               buf.insertItem(it, 0);
            }
            else if(portletdef)
            {
               item.setName( "Portlet" + ProfileUtils.ENTRY_SEP + id );
               item.setState(ProfileItem.QUERIED);
               //Item it = new Item( "Position", Integer.parseInt(name.substring("PORTLET_".length())) );
               ProfileItem it = (ProfileItem)buffer.newItem();
               it.setName("Position");
               it.setValue( Integer.parseInt(name.substring("PORTLET_".length())) );
               buf.insertItem(it, 0);
            }
            else if( level==2 && "PROFILE".equals(name) )
            {
               item.setName("Profile");
               //try to fix MyLinks URLs..only if from_sites is given in ifs.properties
               if (FROM_SITES_ARR!=null && FROM_SITES_ARR.length>0){
                   Buffer portlet_prf = item.getBuffer();
                   if (portlet_prf != null)
                   {
                     try{
                         Buffer mylinks = portlet_prf.getBuffer("MYLINKSBUF"); //check to see if this is a MyLinks portlet
                         //debug("MyLinks Profile buffer before fixing URLs:\n"+Buffers.listToString(mylinks)); 
                         if (mylinks!=null)
                         {
                           for (int k=0; k< mylinks.countItems(); k++)
                           {
                             Buffer name_val_buf = mylinks.getItem(k).getBuffer();  
                             String old_entry = name_val_buf.getString("VALUE");
                             int start = old_entry.indexOf("~");
                             for (int j=0; j<FROM_SITES_ARR.length; j++)
                             {                                    
                               String from_site = FROM_SITES_ARR[j];
                               int end   = old_entry.indexOf("/"+from_site+"/");                         
                               if (start > 0 && end > 0 && end > start)
                               {
                                 end=end+from_site.length()+1;
                                 if (end<old_entry.length()) //make sure to avoid IndexOutOfBoundsException
                                 {
                                   String fixed_entry = old_entry.substring(0,start+1) + BASE_URL + old_entry.substring(end,old_entry.length());                           
                                   name_val_buf.setItem("VALUE",fixed_entry);
                                   break;  //there can be only one match per URL string
                                 }
                               }
                             }
                           }
                         }
                         //debug("MyLinks Profile buffer after fixing URLs:\n"+Buffers.listToString(mylinks)); 
                     }catch(ItemNotFoundException e){
                         //debug("Current portlet is not a MyLinks portlet: "+e.getMessage());                     
                     }
                     catch(Exception e){
                         if (DEBUG) debug("Encountered error when fixing MyLinks URLs: "+e.getMessage());                     
                     }
                   }
              }
            }
         }
         else
         {
            // leaf names at level 0
            if     ( level==0 && "MIN_BOXES"   .equals(name) )  name   = "MinimizedPortlets";
            else if( level==0 && "NEXTID"      .equals(name) )  name   = "NextID";
            else if( level==0 && "COLCOUNT"    .equals(name) )  remove = true;
            else if( level==0 && "REFRESH_TIME".equals(name) )  name   = "RefreshTime";
            else if( level==0 && "WIDE_COLUMN" .equals(name) )  name   = "WideColumnExists";
            // leaf names at level 1
            else if( level==1 && "WIDTH"       .equals(name) )  remove = true;
            else if( level==1 && "APPEARANCE"  .equals(name) )  remove = true;
            else if( level==1 && "PORTCOUNT"   .equals(name) )  remove = true;
            // leaf names at level 2
            else if( level==2 && "CLSNAME"     .equals(name) )  name   = "ClassName";
            else if( level==2 && "ID"          .equals(name) )  remove = true;

            if(remove)
               buffer.removeItem(i);
            else if( level<2 )
            {
               item.setName( name + ProfileUtils.ENTRY_SEP + "Portal Definition");
               item.setState(ProfileItem.QUERIED);
            }
            else
               item.setName(name);
         }
      }
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Store reference to profile sub-buffer containing all profile
    * information corresponding to this instance.
    */
   protected void assign( ASPPoolElement target, ProfileBuffer buffer ) throws FndException
   {
      profbuf = buffer;
      updateMembers(target);
   }

   /**
    * Update instance variables to reflectthe current state of the profile buffer.
    */
   private void updateMembers( ASPPoolElement target) throws FndException
   {
      if(DEBUG) debug("ASPPortalProfile.updateMembers() - profile buffer:\n"+Buffers.listToString(profbuf));

      ASPPortal portal = (ASPPortal)target;
      ASPPage   page   = portal.getASPPage();

      minboxes = profbuf.getString(MIN_PORTLETS,null);
      nextid = profbuf.getInt(NEXT_ID);
      //colcnt = profbuf.countItems(COLUMN); //not part of the profile stored in DB
      refresh_time = profbuf.getInt(REFRESH_TIME,0);
      wide_column  = profbuf.getInt(WIDE_COL_EXISTS,0);

      dyncolcnt   = 0;
      dyncolwidth = 0;

      int buf_count = profbuf.countItems();

      //use this buffer to sort columns, make sure that only compound items are in the buffer
      ProfileBuffer columns_buf = ProfileUtils.newProfileBuffer();

      for(int c=0; c<buf_count; c++)
      {
         Item i = profbuf.getItem(c);
         if( i!=null && i.isCompound() && i.getName().startsWith(COLUMN))
            columns_buf.addItem(i);
      }

      // 1. sort the columns buffer according to order number Column_n
      PositionComparator comp = new PositionComparator();
      /*
      try
      {
         Buffers.sort(columns_buf, comp);
      }
      catch( Exception x )
      {
         throw new FndException(x);
      }
      columns_buf.clearDirty();
      */
      columns_buf.sort(comp);

      colcnt = (columns_buf!=null) ? columns_buf.countItems() : 0;
      columns.clear();

      if(DEBUG) debug(" - columns_buf:\n"+Buffers.listToString(columns_buf));
      for(int c=0; c<colcnt; c++)
      {
         Buffer  col_buf     = columns_buf.getBuffer( c );
         Buffer  col_def_buf = col_buf.getBuffer( COLUMN_DEF );
         int     cwidth      = (col_def_buf.findItem(COLUMN_WIDTH) !=null)?col_def_buf.getItem( COLUMN_WIDTH ).getInt():col_def_buf.getItem( COLUMN_WIDTH_CAP).getInt();
         boolean dynw        = "D".equalsIgnoreCase( (col_def_buf.findItem(COLUMN_APPEAR) !=null )?col_def_buf.getItem( COLUMN_APPEAR ).getString():col_def_buf.getItem( COLUMN_APPEAR_CAP ).getString() );

         if(dynw)
         {
            dyncolcnt++;
            dyncolwidth = dyncolwidth + cwidth;
         }

         ASPPortalColumn col = new ASPPortalColumn(cwidth,dynw);
         columns.addElement(col);

         //use this buffer to sort portlet buffers within a column buffer,
         //Buffer portlets_buf = page.getASPConfig().getFactory().getBuffer();//ProfileUtils.newProfileBuffer();
         ProfileBuffer portlets_buf = ProfileUtils.newProfileBuffer();
         int col_buf_count = col_buf.countItems();
         for(int k=0; k<col_buf_count; k++)
         {
            Item i = col_buf.getItem(k);
            if( i!=null && i.isCompound() && i.getName().startsWith(PORTLET_DEF) )
               portlets_buf.addItem(i);
         }

         if(DEBUG) debug("  - portlets_buf (befor sort):\n"+Buffers.listToString(portlets_buf));
         //sort the portlets buffer according to 'Position' item
         PositionComparator pcomp = new PositionComparator(PORTLET_POS);
         /*
         try
         {
            Buffers.sort(portlets_buf, pcomp);
         }
         catch( Exception x )
         {
            throw new FndException(x);
         }
         portlets_buf.clearDirty();
         */
         portlets_buf.sort(pcomp);

         int portlet_count = portlets_buf.countItems();
         if(DEBUG) debug("  - portlets_buf (after sort):\n"+Buffers.listToString(portlets_buf));
         for(int p=0; p<portlet_count; p++)
         {
            Item pitem = portlets_buf.getItem(p);
            String portlet_item_name = pitem.getName();

            Buffer port_buf= pitem.getBuffer();
            //create buffer with profile info to store in portlet handle
            Buffer b = page.getASPConfig().getFactory().getBuffer();
            //set the portlet ID from Portlet^ID
            String  portlet_id = portlet_item_name.substring(portlet_item_name.indexOf(ProfileUtils.ENTRY_SEP)+1);
            b.addItem("ID", portlet_id);
            //set the ClassName from Portlet^ID/ClassName
            b.addItem("CLSNAME", port_buf.findItem(PORTLET_CLS) !=null? port_buf.getString(PORTLET_CLS):port_buf.getString(PORTLET_CLS_CAP));

            //set the Profile buffer from Portlet^ID/Profile
            if(port_buf.findItem(PORTLET_PRF)!=null)
               b.addItem("PROFILE", port_buf.getBuffer(PORTLET_PRF));
            else if(port_buf.findItem(PORTLET_PRF_CAP) !=null)
               b.addItem("PROFILE", port_buf.getBuffer(PORTLET_PRF_CAP));
            
            int portletPos = port_buf.findItem(PORTLET_POS) !=null ?port_buf.getInt(PORTLET_POS):port_buf.getInt(PORTLET_POS_CAP);
            
            if(portletPos < 0) 
               continue;

            ASPPortletHandle colport = new ASPPortletHandle();
            colport.load( page, b );
            col.addPortletHandle(colport);
         }
      }
      if(DEBUG) debugProfile();
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Return reference to sub-buffer containing profile information
    * corresponding to the current instance
    */
   protected ProfileBuffer extract( ASPPoolElement target ) throws FndException
   {
      updateProfileBuffer(target);
      return profbuf;
   }

   private void updateProfileBuffer( ASPPoolElement target )  throws FndException
   {
      if(DEBUG) debug("ASPPortalProfile.updateProfileBuffer()");

      ASPPage         page = ((ASPPortal)target).getASPPage();
      ASPConfig       cfg  = page.getASPConfig();
      Buffer          buf  = cfg.getFactory().getBuffer();

      if( profbuf==null )
         profbuf = ProfileUtils.newProfileBuffer();


      if(DEBUG) debug("ASPPortalProfile.updateProfileBuffer() - Current profile buffer:\n"+Buffers.listToString(profbuf));

      ProfileUtils.findOrCreateNestedItem(profbuf,MIN_PORTLETS).setValue(minboxes);
      ProfileUtils.findOrCreateNestedItem(profbuf,NEXT_ID).setValue(nextid);
      ProfileUtils.findOrCreateNestedItem(profbuf,REFRESH_TIME).setValue(refresh_time);
      ProfileUtils.findOrCreateNestedItem(profbuf,WIDE_COL_EXISTS).setValue(wide_column);

      int csize = columns.size();
      ArrayList existing_portlets = new ArrayList(10); //used to find removed portlets so that they can be removed from the profile buffer
      ArrayList existing_mylinks_portlets = new ArrayList(10); //used to find removed MyLinks portlets
      for(int c=0; c<csize; c++)
      {
         if(DEBUG) debug("  formatting column "+c);

         ASPPortalColumn col = (ASPPortalColumn)(columns.elementAt(c));

         Buffer col_buf = ProfileUtils.findOrCreateNestedBuffer(profbuf,COLUMN+c); // order no for columns ?
         ProfileUtils.findOrCreateNestedItem(col_buf,COLUMN_DEF+"/"+COLUMN_WIDTH_CAP,COLUMN_WIDTH).setValue(col.getWidth() );
         ProfileUtils.findOrCreateNestedItem(col_buf,COLUMN_DEF+"/"+COLUMN_APPEAR_CAP,COLUMN_APPEAR).setValue(col.isDynamic() ? "D" : "S");

         int psize = col.countPortlets();
         for(int p=0; p<psize; p++)
         {
            if(DEBUG) debug("    formatting portlet "+p);

            ASPPortletHandle cp = col.getPortletHandle(p);
            Buffer cpbuf = cp.save(page);

            existing_portlets.add(c+"^"+cpbuf.getString("ID"));

            //find or create buffer "Portlet^ID"
            Buffer portlet_buf = ProfileUtils.findOrCreateNestedBuffer(col_buf,PORTLET_DEF+cpbuf.getString("ID"));

            //set position "Portlet^ID/Position"
            ProfileUtils.findOrCreateNestedItem(portlet_buf,PORTLET_POS_CAP,PORTLET_POS).setValue(p);

            //set class name "Portlet^ID/ClassName"
            ProfileUtils.findOrCreateNestedItem(portlet_buf,PORTLET_CLS_CAP,PORTLET_CLS).setValue(cpbuf.getString("CLSNAME"));
                        
            //set visibility "Portlet^ID/IsOwn"
            Item isOwnItem = portlet_buf.findItem(PORTLET_ISOWN_CAP);
            String isOwnVal = null;
            if(isOwnItem !=null)
               isOwnVal = isOwnItem.getString();
            else
            {
               isOwnItem = portlet_buf.findItem(PORTLET_ISOWN);
               if(isOwnItem !=null)
                  isOwnVal = isOwnItem.getString();
            }
            
            if(isOwnVal != null)
            ProfileUtils.findOrCreateNestedItem(portlet_buf,PORTLET_ISOWN_CAP,PORTLET_ISOWN).setValue(isOwnVal);
            else
            ProfileUtils.findOrCreateNestedItem(portlet_buf,PORTLET_ISOWN_CAP,PORTLET_ISOWN).setValue("TRUE");  

            if(cpbuf.getString("CLSNAME").equals("ifs.fnd.portlets.MyLinks"))
               existing_mylinks_portlets.add("ID_"+cpbuf.getString("ID"));                                 

            //set/remove profile buffer "Portlet^ID/Profile"
            Item prf_it = cpbuf.findItem("PROFILE");
            if(prf_it!=null)
            {
               Item prf_item = portlet_buf.findItem("Profile");
               if(prf_item!=null)
                  prf_item.setValue(prf_it.getBuffer());
               else{
                  prf_item = portlet_buf.findItem("PROFILE");
                  if(prf_item !=null)
                     prf_item.setValue(prf_it.getBuffer());
                  else
                  portlet_buf.addItem(new ProfileItem("PROFILE",prf_it.getBuffer()));
               }
            }
            else
            {
               Item prf_item = portlet_buf.findItem("Profile"); //clear portlet profile
               if(prf_item!=null)
                  portlet_buf.removeItem("Profile");
            }
         }
      }

      //update profile buffer by removing non-existing columns and portlets

      //first extract columns and sort, make sure that only compound items are in the buffer
      ProfileBuffer columns_buf = ProfileUtils.newProfileBuffer();
      int buf_count = profbuf.countItems();
      for(int c=0; c<buf_count; c++)
      {
         Item i = profbuf.getItem(c);
         if( i!=null && i.isCompound() && i.getName().startsWith(COLUMN)) //required for sorting
            columns_buf.addItem(i);
      }

      // sort the columns buffer according to order number 'Column_n'
      PositionComparator comp = new PositionComparator();
      /*
      try
      {
         Buffers.sort(columns_buf, comp);
      }
      catch( Exception x )
      {
         throw new FndException(x);
      }
      columns_buf.clearDirty();
      */
      columns_buf.sort(comp);

      int col_count = columns_buf.countItems();

      for(int c=0; c<col_count; c++)
      {
         Buffer col_buf = null;
         Item col_item = columns_buf.getItem(c);
         col_buf = col_item.getBuffer();

         if(c>=csize) //the whole column has been removed
            col_buf.removeItems();
         else
         {
            int col_buf_count= col_buf.countItems();
            for(int p=0; p<col_buf_count; p++)  //find portlet buffers within column buffer
            {
               String port_item_name = col_buf.getItem(p).getName();
               String portlet_id="";
               if(port_item_name.indexOf(PORTLET_DEF)>=0)  // i.e: Portlet^n
                  portlet_id = port_item_name.substring(port_item_name.indexOf("^")+1);
               else
                  continue; //avoid non portlet buffers

               if(!existing_portlets.contains(c+"^"+portlet_id))  //remove portlets from buffer which do not exist in columns
               {
                  //debug("!!!!removing portlet="+port_item_name+"["+c+"^"+portlet_id+"]");
                  col_buf.removeItem(port_item_name);
               }
            }
         }
      }

      String name = page.getASPPortal().getName();
      if(!Str.isEmpty(name))
      {         
         ASPBuffer portal_buf = page.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE,false);
         if(portal_buf!=null)
         {
            ASPBuffer links_buf=portal_buf.getBuffer("Links");
            if(links_buf!=null)
            {
               for(int m=0; m<links_buf.countItems(); m++)
               {
                  String id = links_buf.getNameAt(m);
                  if(!existing_mylinks_portlets.contains(id))
                     links_buf.removeItem(id);
               }
               if(links_buf.countItems()==0)
                  portal_buf.removeItem("Links");
               page.removeGlobalProfileItem(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE);
               page.writeGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS+"/"+name+ASPPortal.PAGE_NODE,portal_buf,false);
            }
         }
      }
      
      if(DEBUG) debug("ASPPortalProfile.updateProfileBuffer() - Updated profile buffer:\n"+Buffers.listToString(profbuf));
   }


   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Synchronize the internal state with the content of the ProfileBuffer.
    */
   protected void refresh( ASPPoolElement target ) throws FndException
   {
      updateMembers(target);
   }

   //==========================================================================
   //
   //==========================================================================

   // Temporary solution: functions copied from Util.java

   /**
    * Add 0 as high-byte value to every character
    * Same as new String(data,0), which is depricated.
    */
   private static String toByteString( byte[] data ) throws IOException
   {
      return new String(data, "UTF-8");
   }

   /**
    * Ignorie high-byte value of every character.
    * Same as text.getBytes(0,size,data,0), which is depricated.
    */
   private static byte[] fromByteString( String text ) throws IOException
   {
      return text.getBytes("UTF-8");
   }


   /**
    * Inherited interface.
    * Create a Buffer with profile information from an existing ASPPoolElement.
    */
   protected void save( ASPPoolElement target, Buffer dest ) throws FndException
   {
   }


   /**
    * Inherited interface.
    * Apply profile information stored in the given Buffer on an ASPPoolElement.
    */
   protected void load( ASPPoolElement target, Buffer source ) throws FndException
   {
   }



   /**
    * Inherited interface.
    */
   public boolean equals( Object obj )
   {
      if(DEBUG) debug(this+": ASPPortalProfile.equals("+obj+")");

      if( obj==null ) return false;
      if( obj instanceof ASPPortalProfile )
      {
         ASPPortalProfile p = (ASPPortalProfile)obj;

         if( colcnt       != p.colcnt       ||
             dyncolcnt    != p.dyncolcnt    ||
             dyncolwidth  != p.dyncolwidth  ||
             nextid       != p.nextid       ||
             refresh_time != p.refresh_time ||
             wide_column  != p.wide_column   ) return false;
         if( minboxes!=null && !minboxes.equals(p.minboxes) ) return false;
         if( minboxes==null && p.minboxes!=null ) return false;

         int csize = columns.size();
         if( csize != p.columns.size() ) return false;

         for(int c=0; c<csize; c++)
         {
            ASPPortalColumn col = (ASPPortalColumn)(columns.elementAt(c));
            if(!col.equals( (ASPPortalColumn)(p.columns.elementAt(c)) ) ) return false;
         }
         return true;
      }
      return false;
   }


   /**
    *
    */
   void activate( ASPPortal portal,
                  boolean   instantiate,
                  String    portletid,
                  Buffer    user_portlets,
                  ASPPage toppage ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalProfile.activate("+portal+","+instantiate+","+portletid+","+user_portlets+")");
      for(int c=0; c<columns.size(); c++)
       //Bug 40931, start
         //((ASPPortalColumn)(columns.elementAt(c))).activate(portal,instantiate,portletid,user_portlets);
         ((ASPPortalColumn)(columns.elementAt(c))).activate(portal,instantiate,portletid,user_portlets,toppage);
       //Bug 40931, end
   }

  //Bug 40931, start
   void forceDirty( ASPPortal portal ) throws FndException
  //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortalProfile.forceDirty()");
      for(int c=0; c<columns.size(); c++)
        //Bug 40931, start
         ((ASPPortalColumn)(columns.elementAt(c))).forceDirty(portal);
        //Bug 40931, end
   }


   //Bug 40931, start
   //void reset( ASPManager mgr )
   void reset( ASPPortal portal, ASPManager mgr, ASPPage toppage )
   //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortalProfile.reset("+mgr+")");
      for(int c=0; c<columns.size(); c++)
         //Bug 40931, start
         //((ASPPortalColumn)(columns.elementAt(c))).reset(mgr);
         ((ASPPortalColumn)(columns.elementAt(c))).reset(portal,mgr,toppage);
         //Bug 40931, end
   }

   boolean isReset( ASPPortal portal )
   {
      if(DEBUG) debug(this+": ASPPortalProfile.isReset("+portal+")");
      for(int c=0; c<columns.size(); c++)
         if( !((ASPPortalColumn)(columns.elementAt(c))).isReset(portal) ) return false;
      return true;
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   /**
    * Debug printout to the DBMON console.
    */
   private void debug( String text )
   {
      Util.debug(text);
   }


   private void showContents( AutoString out, String indent ) throws FndException
   {
      out.append(indent,"   ","MIN_BOXES=",    minboxes,"\n");
      out.append(indent,"   ","NEXTID=",       nextid+"\n");
      out.append(indent,"   ","REFRESH_TIME=", refresh_time+"\n");
      out.append(indent,"   ","WIDE_COLUMN=",  wide_column+"\n");

      for(int c=0; c<columns.size(); c++)
      {
         ASPPortalColumn col = (ASPPortalColumn)(columns.elementAt(c));

         out.append(indent,"   ","Column ");
         out.appendInt(c);
         out.append(" (",col.isDynamic()?"DYN":"STAT",","+col.getWidth(),")\n");

         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle cp = col.getPortletHandle(p);
            out.append(indent,"   "," Portlet: ",cp.getClassName() );
            out.append("(",cp.getId(),")\n");
            out.append(indent,"     ");
            out.append( Str.replace(Buffers.listToString(cp.getProfile()),"\n","\n"+indent+"     ") );
            out.append("\n");
         }
      }
   }


   void showContents( AutoString out ) throws FndException
   {
      showContents(out,"");
   }


   String showContents()
   {
      AutoString out = new AutoString();
      try
      {
         showContents(out,"");//"\t\t");
      }
      catch( Throwable any )
      {
         out.append(Str.getStackTrace(any));
      }
      return toString() + ":\n" + out.toString();
   }

   ASPPortalProfile getNewProfileInstance(ASPPortalProfile prof)
   {
      if(prof!=null)
      {
         ASPPortalProfile ret_prof  = (ASPPortalProfile)newInstance();
         ret_prof.colcnt            = prof.colcnt;
         ret_prof.dyncolcnt         = prof.dyncolcnt;
         ret_prof.dyncolwidth       = prof.dyncolwidth;
         ret_prof.nextid            = prof.nextid;
         ret_prof.refresh_time      = prof.refresh_time;
         ret_prof.wide_column       = prof.wide_column;
         ret_prof.user_portlets     = prof.user_portlets;
         ret_prof.minboxes          = prof.minboxes;
         //ret_prof.columns           = prof.columns;

         int cols = prof.columns.size();
         for(int c=0; c<cols; c++)
         {
            ASPPortalColumn col = (ASPPortalColumn)prof.columns.elementAt(c);
            ASPPortalColumn newcol = new ASPPortalColumn( col.getWidth(), col.isDynamic() );
            ret_prof.columns.addElement(newcol);

            for(int p=0; p<col.countPortlets(); p++)
               newcol.addPortletHandle((ASPPortletHandle)(col.getPortletHandle(p).clone()));
         }

         return ret_prof;
      }
      else
         return null;
   }

   void debugProfile()
   {
      debug("ASPPortalProfile.debugProfBuf() - profile buffer:\n"+Buffers.listToString(profbuf));
      for(int c=0; c<columns.size(); c++)
      {
         ASPPortalColumn col = (ASPPortalColumn)columns.elementAt(c);
         debug(" - Column ["+c+"]: "+(col.isDynamic()?"D":"S")+", width="+col.getWidth());

         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);

            debug(" -- Portlet ["+p+"]: name="+ph.getClassName()+", ID="+ph.getId());

            Buffer buf = ph.getProfile();
            if(buf!=null)
              debug(" --- Portlet profile ("+buf.getClass().getName()+"):\n"+Buffers.listToString(buf));
         }
      }
   }
}

