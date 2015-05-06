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
 * File        : ASPProfile.java
 * Description : Container for ASPPoolElementProfiles owned by an ASPPage
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1999-May-04 - Created
 *    Jacek P  1999-May-05 - Implements Bufferable.
 *    Jacek P  1999-May-31 - Added method update() and compression of profiles.
 *    Jacek P  1999-Jun-07 - Fetching of profile information from the database
 *                           controlled by a new parameter to constructor.
 *    Jacek P  1999-Jun-08 - Variable 'url' initialized by function
 *                           ASPPage.getPagePath() instead of getURL().
 *                           Function save() can now even remove the profile
 *                           information from the database.
 *    Jacek P  1999-Jul-13 - Added method clearSecurity() called from
 *                           ASPProfileCache.clear()
 *    Jacek P  1999-Aug-08 - Added better exception handling in checkSecurity()
 *                           and fetchProfiles().
 *    Jacek P  2000-Mar-28 - Added support for portals.
 *    Jacek P  2000-Apr-14 - Added showContents() function.
 *    Jacek P  2000-Apr-27 - Removed case sensitivity for URL in fetchProfiles() and save().
 *    Jacek P  2000-May-22 - Page url converted to lower case in showContents().
 *    Jacek P  2000-Aug-07 - Added cloning of the default profile before adding
 *                           to the current profile in function get().
 *    Artur K  2000-Nov-23 - Necessary changes for ASPPageProfile(global profile)
 *    Piotr Z                handling.
 *    Jacek P  2000-Dec-14 - Value of constant NO_URL changed from '*' to '#'.
 *    Jacek P  2001-Jan-24 - Corrected bug #587 - global profile is shared among sites.
 *    Artur K  2001-Feb-21 - Necessary changes for handling global profile on the new platform.
 *    Piotr Z  2001-Apr-25 - Method addProfile() from private to package method.
 *    Artuk K  2001-May-07 - Changes for handling .page extension.
 *    Ramila H 2002-Dec-13 - Log id 933. Changed the url key to get the page pool to support
 *                           block profiles for dynamic LOVs
 *    ChandanaD2003-Jan-08 - Overloaded fetchProfiles() and added getCurrentProfile() methohd.
 *    Chandana 2004-Sep-10 - Added support for Block Layout Profiles.
 *    Chandana 2004-Sep-14 - Reversed changes done for Block Layout Profiles.
 *    Ramila H 2004-10-22  - Used method getProfilePoolKey to get profile key without lang suffix
 *    Chandana 2004-Nov-05 - Added support for Block Layout Profiles.
 *    Jacek P  2004-Nov-11 - Re-written due to new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/07/14 buhilk Bug 90508, Modified save() to clear profile cache after saving. Force loading fresh profile from db.
 * 2007/07/03 sadhlk
 * Merged Bug 64669, Modified Constructor and Added isUserProfileDisabled().
 * 2007/05/04 sadhlk
 * Bug 64337, Added markFlag() and Overloaded remove() to set the removeFlag in ProfileItems which 
 *            are in REMOVED state.
 *
 * 2006/11/14 rahelk
 * Bug 61690, Assigned FNDUSER to userid in construct.               
 *
 *               2006/05/11    riralk 
 * Bug 57024, Modified method save() so that the profile cache entries for the particular user is 
 * cleared if an exception occurs. 
 *
 * Revision 1.4  2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 * Revision 1.3  2005/11/16 14:19:12  japase
 * Removed save() without arguments - replaced with save(ASPPoolElement)
 *
 * Revision 1.2  2005/11/15 11:30:06  japase
 * Added version of save() taking ASPPoolElement as argument
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.9  2005/08/04 13:41:21  riralk
 * Fixed/Removed profile related Q&D's and removed obsolete code.
 *
 * Revision 1.8  2005/07/27 12:25:09  riralk
 * Added a get() method which returns a buffer section from the profile of a page for a given item name, used to get saved LOV queries from ASPField.getLOVQueries()
 *
 * Revision 1.7  2005/06/06 07:29:53  rahelk
 * To save all profile objects together from CSL page
 *
 * Revision 1.6  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.5.2.1  2005/03/03 11:48:57  riralk
 * Removed cloning of profile buffer in ASPPortalProfile.clone() and some minor fixes.
 *
 * Revision 1.5  2005/02/24 13:48:59  riralk
 * Adapted Portal profiles to new profile algorithm. Removed some obsolete code.
 *
 * Revision 1.4  2005/02/03 12:40:36  riralk
 * Adapted BlockProfile (saved queries) functionality to new profile changes.
 *
 * Revision 1.3  2005/02/02 15:09:31  riralk
 * Adapted BlockLayoutProfile functionality to new profile changes.
 *
 * Revision 1.2  2005/02/02 08:22:18  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.3  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
 *
 * Revision 1.2  2004/12/10 10:19:32  riralk
 * New Block Layout profile changes
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

/**
 * An instance of this class is created for each combination of user and URL (ASPPage)
 * and then cached in the profile cache (ASPProfileCache class).
 * <p>
 * The profile information is stored as a buffer structure, but can be serialized and
 * stored in the database. And the opposite, the serialized profile information stored
 * in the database can be deserialized to a buffer structure.
 * <p>
 * This class supports user profile information but also default profile that can be
 * assign by the system administrator.
 * <p>
 * The profile information is stored as a buffer with items that names correspond to
 * the names of classes instantiated by the corresponding instance of ASPPage.
 * Each item (class name) is a buffer with items that names correspond to object names
 * of a given class. Then the profile information for a particular object can be stored
 * as a string (serialized), but can also be deserialized by the object itself to a proper
 * structure implemented by the object (or its corresponding profile class).
 */
public class ASPProfile implements Bufferable, Serializable
{
   // static variables/constants
   public static boolean DEBUG      = Util.isDebugEnabled("ifs.fnd.asp.ASPProfile");
   public static boolean DEBUGNEW   = false; //TODO: temporary for debugging of the new profile handling
   final  static String  GLOBAL_PRF = "Global";

   // instance variables
   private String  userid;
   private String  apppath;
   private boolean modifiable = true;

   // new variables - need to be serialized
   private ProfileBuffer          profbuf;    // renamed - a buffer containing profile information as a regular nested buffer structure
   private String                 section;    // check usage of it - too many levels!!!
   private ProfileUtils.ClientPrf clientprf;
   private Buffer                 instances;  // a two-level buffer containing instances of ASPPoolElemetProfile; the purpose with this (strange) construction is to minimize the required changes of the code. The primary source of the profile information is the 'profbuf' buffer anyway. Each instance is responsible for updating it.


   //==========================================================================
   //  Construction
   //==========================================================================

   protected ASPProfile()
   {
   }

   /**
    * Protected constructor. Create a new instance of this class.
    * Fetch profile from the database if the user is already logged on.
    */

   protected ASPProfile( ASPPage page, String section, boolean logged_on ) throws FndException
   {
      if(DEBUGNEW) debug(this+": ASPProfile.<init>("+page+","+section+","+logged_on+")");

      this.userid  = page.getASPManager().getFndUser(); 
      //this.userid  = page.getASPManager().getUserId();
      this.apppath = page.getASPConfig().getApplicationPath();
      this.section = section;
      this.profbuf = ProfileUtils.newProfileBuffer();

      if(logged_on)
      {
         if(DEBUGNEW) debug("  ASPProfile: user logged on");
         this.clientprf = ProfileUtils.load( profbuf, section, userid);

         if(clientprf.getUserId() == null)
            this.modifiable = false;
         this.instances = page.getASPConfig().getFactory().getBuffer();//profbuf.newInstance();
      }
      else
      {
         if(DEBUGNEW) debug("  ASPProfile: user NOT logged on - creating empty object");
         this.modifiable = false;
      }
   }

   //==========================================================================
   //  Interface (NEW)
   //==========================================================================


   /**
    * Retrieve (from the cache or database) a profile info for the
    * current user and the current url. If not found then
    * return the specified default profile.
    */
   protected synchronized ASPPoolElementProfile get( ASPPoolElement element, ASPPoolElementProfile default_profile ) throws FndException
   {

      if(DEBUGNEW)
      {
         debug(this+": ASPProfile.get("+element+","+default_profile+")");
         ProfileUtils.debug("  profile buffer:", profbuf);
      }

      //
      // 1. Try to find (in the internal cache) an instance
      //    of ASPPoolElementProfile for the requested object.
      //
      // 2. If not found, try to find a profile buffer.
      //
      // 3. If buffer not found, just return back 'default_profile'.
      //
      // 4. If buffer found, create a new instance of ASPPoolElementProfile
      //    and assign the buffer to it. Put the created instance in the cache and return it.
      //

      // Obtain the name of the requested class and instance
      String clsname = ProfileUtils.convertClassName(element);
      String objname = element.getName();


      // 1. Try to find an instance of ASPPoolElementProfile for the requested object.
      //--Q&D RIRALK - null pointer exception when reapplying table profile changes
      Item inst = (instances == null)?null:instances.findItem(clsname+"/"+objname); //RIRALK: what abt global profiles?
      //-----
      if(inst!=null)
      {
         // Profile instance found - just return it
         if(DEBUGNEW) debug("  Item for object name '"+objname+"' of class ["+clsname+"] found.");

         Object obj = inst.getValue();
         if( obj instanceof ASPPoolElementProfile )
         {
            if(DEBUGNEW) debug(" - profile information found; refreshing profile buffer.");
            ((ASPPoolElementProfile)obj).refresh(element);
            return (ASPPoolElementProfile)obj;
         }
         else
            throw new FndException("FNDPRFNOTPRF: Profile object for &1 '&2' is not of the expected class.", clsname, objname );
      }


      // 2. Profile instance not found:
      //  - retrieve profile information (sub-buffer)
      if(DEBUGNEW) debug(" - trying to find profile information for ["+userid+"/"+section+"/"+clsname+"/"+objname+"]");

      //---Q&D RIRALK - clsname (Page) and objname (PAGE) not used for gloabl profiles
      ProfileItem item = null;
      if ("PAGE".equals(objname.toUpperCase()) && clsname.toUpperCase().equals(objname.toUpperCase()))
        item = (ProfileItem)profbuf.findItem(userid+"/"+section); //no "Page/Page" path for gloabl profile
      else
        item = (ProfileItem)profbuf.findItem(userid+"/"+section+"/"+clsname+"/"+objname);
      //------

      if(item==null)
      {
         // 3. Profile buffer not found, just return back 'default_profile'.
         if(DEBUGNEW) debug("  Item for class or object ["+clsname+":"+objname+"] not found.");
         return default_profile;
      }


      // 4. Profile buffer found
      //  - create new profile instance (ASPPoolElementProfile)
      ASPPoolElementProfile prof = default_profile!=null ? default_profile.newInstance() : element.newProfile();

      if( prof==null )
         throw new FndException("FNDPRFNPRFSUP: Class '&1' doesn't support profiles.", element.getClass().getName() );

      //  - assign profile information (buffer) to the created profile instance
      if(DEBUGNEW) debug(" - profile information found; assigning profile buffer.");
      if( !item.isCompound() )
         throw syntaxError( clsname, objname );
      prof.assign( element, (ProfileBuffer)item.getBuffer() );     //TODO: !!

      //  - save the profile instance to the cache.
      inst = instances.findItem(clsname);
      if( inst==null )
      {
         // create new compound Item
         inst = new Item(clsname);
         instances.addItem(inst);
         inst.setValue( instances.newInstance() );
      }
      // the Item has to be compound
      if( !inst.isCompound() )
         throw syntaxError( clsname, null );
      // add profile instance for the corresponding object
      inst.getBuffer().addItem(objname,prof);

      return prof;
   }


   /*
    * Returns a buffer section from profbuf for a given item name without providing pool element
    * this bypasses the normal profile flow, used to get saved LOV queries from ASPField.getLOVQueries()
    */
   protected synchronized Buffer get(String name)  throws FndException
   {
      ProfileItem item = (ProfileItem)profbuf.findItem(userid+"/"+section+"/"+name);
      if (item!=null)
         return (Buffer)item.getBuffer().clone();  //returning a clone since modifications should not be possible/visible
      return null;
   }
   /**
    * Replace the object profile information in the profile cache.
    */
   protected synchronized void update( ASPPoolElement element, ASPPoolElementProfile profile ) throws FndException
   {

      if(DEBUGNEW) debug(this+": ASPProfile.update("+element+","+profile+")");

      // 1. Extract profile buffer from the new instance
      // 2. Merge the extracted buffer with the original one in 'profbuf'
      // 3. Replace the existing instance, if any, in 'instances' with the new one

      // Obtain the name of the requested class and instance
      String clsname = ProfileUtils.convertClassName(element);
      String objname = element.getName();

      // Extract profile buffer from the new instance
      ProfileBuffer newbuf = profile.extract(element);
      if(DEBUGNEW) ProfileUtils.debug("update(): extracted profile buffer for '"+clsname+"/"+objname+"':",newbuf);

      // Merge the extracted buffer with the original one in 'profbuf'
      setProfile( clsname, objname, newbuf );

      // Replace the existing instance, if any, in 'instances' with the new one
      Item inst = instances.findItem(clsname);
      if( inst==null )
      {
         // create new compound Item
         inst = new Item(clsname);
         instances.addItem(inst);
         inst.setValue( instances.newInstance() );
      }
      // the Item has to be compound
      if( !inst.isCompound() )
         throw syntaxError( clsname, null );
      // remove the eventual old instance and add the new one
      Buffer buf = inst.getBuffer();
      buf.removeItem(objname);
      //buf.addItem(objname,profile); //RIRALK: commented this to get ProfileCache working without errors.
   }

   /**
    * Update the database so that it corresponds to the current state
    * of this instance of ASPProfile.
    */
   public synchronized void save( ASPPoolElement obj ) throws FndException
   {
      if(DEBUGNEW)
      {
         debug(this+": ASPProfile.save()");
         ProfileUtils.debug(" - clientprf:", clientprf);
         ProfileUtils.debug(" - profbuf:",   profbuf);
      }

      if(!modifiable)
         throw new FndException("FNDPRFSAVNAL: Current user is not allowed to save profile information.");
      
      try{          
          ProfileUtils.save(clientprf, profbuf, obj.getASPManager().getRFCLanguageCode() );
          ASPProfileCache.clearUser(clientprf.getUserId());
      }
      catch (FndException fe){         
         ASPProfileCache.clearUser(clientprf.getUserId()); //clear cache entries for this user only.
         throw new FndException(fe);
      }    
   }

   /**
    * Remove from this ASPProfile the information corresponding
    * to the specified pool element.
    */
   protected synchronized void remove( ASPPoolElement element ) throws FndException
   {

      if(DEBUGNEW) debug(this+": ASPProfile.remove("+element+")");

      // 1. Find the buffer and mark the whole tree as removed
      // 2. Remove the profile object, if exists

      // Obtain the name of the requested class and instance
      String clsname = ProfileUtils.convertClassName(element);
      String objname = element.getName();

      //RIRALK: added userid/section to path
      Item item = profbuf.findItem(userid+"/"+section+"/"+clsname+"/"+objname);
      if( item==null ) return;

      // the Item has to be compound
      if( !item.isCompound() )
         throw syntaxError( clsname, objname );

      Buffer buf = item.getBuffer();
      mark( buf, ProfileItem.REMOVED );

      // remove the profile instance, if exists
      Item inst = instances.findItem(clsname);
      if(inst==null) return;
      if( !inst.isCompound() )
         throw syntaxError( clsname, null );
      inst.getBuffer().removeItem(objname);
   }
   
   protected synchronized void remove( ASPPoolElement element, boolean setFlag ) throws FndException
   {

      if(DEBUGNEW) debug(this+": ASPProfile.setFlag("+element+")");

      // 1. Find the buffer and mark the whole tree not to be set positioned
      // 2. Remove the profile object, if exists

      // Obtain the name of the requested class and instance
      String clsname = ProfileUtils.convertClassName(element);
      String objname = element.getName();

      //RIRALK: added userid/section to path
      Item item = profbuf.findItem(userid+"/"+section+"/"+clsname+"/"+objname);
      if( item==null ) return;

      // the Item has to be compound
      if( !item.isCompound() )
         throw syntaxError( clsname, objname );

      Buffer buf = item.getBuffer();
      mark( buf, ProfileItem.REMOVED );
      markFlag(buf, setFlag);

      // remove the profile instance, if exists
      Item inst = instances.findItem(clsname);
      if(inst==null) return;
      if( !inst.isCompound() )
         throw syntaxError( clsname, null );
      inst.getBuffer().removeItem(objname);
   }

   //==========================================================================
   //  Private help routines
   //==========================================================================

   /**
    *
    */
   private FndException syntaxError( String classname, String objname )
   {
      if( Str.isEmpty(objname) )
         return new FndException("FNDPRFPRFSYNTAX1: Syntax error in profile object for class '&1'.", classname );
      else
         return new FndException("FNDPRFPRFSYNTAX2: Syntax error in profile object '&1' of class '&2'.", objname, classname );
   }

   /**
    * Merge sub-buffer 'newbuf' to the existing profile buffer the given class and object name
    * with consideration of the item state.
    */
   private void setProfile( String clsname, String objname, ProfileBuffer newbuf ) throws FndException
   {
      if(DEBUGNEW) debug("ASPProfile.setProfile(): merging profile for ["+section+"/"+clsname+"/"+objname+"]");
      if ("PAGE".equals(objname.toUpperCase()) && clsname.toUpperCase().equals(objname.toUpperCase()))
         ProfileUtils.mergeProfiles( profbuf, newbuf, userid +"/"+section ); //global profile "Page/Page" is overkill
      else
         ProfileUtils.mergeProfiles( profbuf, newbuf, userid +"/"+section+"/"+clsname+"/"+objname );
   }

   /**
    *
    */
   private void mark( Buffer buffer, char status ) throws FndException
   {
     // TODO: Mark recursive the entire 'buffer' with 'status'
     for(int i=0; i<buffer.countItems(); i++)
     {
         ProfileItem item = (ProfileItem)buffer.getItem(i);
         String name  = item.getName();
         int pos  = name.indexOf(ProfileUtils.ENTRY_SEP);

         if( pos>=0 )
           item.setState(status);
         else if( item.isCompound() )
           mark( item.getBuffer(), status );
     }

   }
   
   /**
    * Set the flag value to all the items in the buffer.
    */
   private void markFlag(Buffer buffer, boolean flag) throws FndException
   {
      for(int i=0; i<buffer.countItems(); i++)
      {
         ProfileItem item = (ProfileItem)buffer.getItem(i);
         String name  = item.getName();
         int pos  = name.indexOf(ProfileUtils.ENTRY_SEP);

         if( pos>=0 )
           item.setRemoveFlag(flag);
         else if( item.isCompound() )
           markFlag( item.getBuffer(), flag );
     }
   }

   //==========================================================================
   //==========================================================================
   //==========================================================================
   //  General purpose functiones - should be kept,
   //   but updated with new member variables
   //==========================================================================

   //==========================================================================
   //  Bufferable interface
   //==========================================================================

   /**
    * Store the internal state of this ASPProfile in a specified Buffer
    */
   public void save( Buffer into )
   {
      if(DEBUG) debug(this+": ASPProfile.save("+into+")");

      Buffers.save( into, "CLASS"     , getClass().getName() );
      Buffers.save( into, "USERID"    , userid               );
      Buffers.save( into, "APP_PATH"  , apppath              );
      //Buffers.save( into, "URL"       , url                  );
      Buffers.save( into, "MODIFIABLE", modifiable           );
      //Buffers.save( into, "DEFAULT"   , defprofbuf           );
      //Buffers.save( into, "CURRENT"   , currprofbuf          );
   }


   /**
    * Retrieve the internal state of this ASPProfile from a specified Buffer
    */
   public void load( Buffer from )
   {
      if(DEBUG) debug(this+": ASPProfile.load("+from+")");

      userid      = Buffers.loadString ( from, "USERID"     );
      apppath     = Buffers.loadString ( from, "APP_PATH"   );
      //url         = Buffers.loadString ( from, "URL"        );
      modifiable  = Buffers.loadBoolean( from, "MODIFIABLE" );
      //defprofbuf  = Buffers.loadBuffer ( from, "DEFAULT"    );
      //currprofbuf = Buffers.loadBuffer ( from, "CURRENT"    );
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   /**
    * Debug printout to the DBMON console.
    */
   private void debug( String line )
   {
      Util.debug(line);
   }

   /**
    *
    */
   private void showContents( AutoString out, ASPPage page, Buffer profbuf ) throws FndException
   {
      for( int i=0; i<profbuf.countItems(); i++ )
      {
         Item   item    = profbuf.getItem(i);
         String clsname = item.getName();
         Buffer objbuf  = item.getBuffer();

         out.append(" CLASS [",clsname+"]\n");

         for( int j=0; j<objbuf.countItems(); j++ )
         {
                   item    = objbuf.getItem(j);
            String objname = item.getName();
            Object obj     = item.getValue();

            out.append("  OBJECT NAME: '",objname+"'[",obj+"]\n");

            ASPPoolElement element = null;
            if( "ifs.fnd.asp.ASPTable".equals(clsname) )
               element = page.getASPTable(objname);
            else if( "ifs.fnd.asp.ASPPortal".equals(clsname) )
               element = page.getASPPortal();
            else if( "ifs.fnd.asp.ASPPage".equals(clsname) )
               element = page;
            else
            {
               out.append("  This class is not supported by profile.\n");
               continue;
            }

            if( obj instanceof String )
            {
               ASPPoolElementProfile prof = null;

               if( element instanceof ASPTable )
                  prof = new ASPTableProfile();
               else if( element instanceof ASPPortal )
                  prof = new ASPPortalProfile();
               else if( element instanceof ASPPage )
                  prof = new ASPPageProfile();
               else
               {
                  out.append("   This class is not supported by profile.\n");
                  continue;
               }
               BufferFormatter frmt = page.getASPConfig().getFactory().getBufferFormatter();
               prof.parse((ProfileBuffer)profbuf,frmt,(String)obj);
               obj = prof;
            }


            if( obj instanceof ASPPoolElementProfile )
               ((ASPPoolElementProfile)obj).showContents(out);
            else
               debug("   Profile object is not of the expected class.");
         }
      }
   }

   /**
    *
    */
   void showContents() throws FndException
   {
      AutoString tmpbuf = new AutoString();
      tmpbuf.clear();
      tmpbuf.append("\n\n\n\tContents of profile [",this.toString(),"] '");
      //tmpbuf.append(userid, ": ", url );
      tmpbuf.append("':\n\t===================================================================================\n\n");

      //ASPPage page = ASPPagePool.getPage(url.toLowerCase());

      //tmpbuf.append("CURRENT PROFILE [",currprofbuf+"]:\n");
     // showContents( tmpbuf, page, currprofbuf );
      //tmpbuf.append("\nDEFAULT PROFILE [",defprofbuf+"]:\n");
      //showContents( tmpbuf, page, defprofbuf );

      tmpbuf.append("\n\n\n");
      debug(tmpbuf.toString());
   }

   /**
    *
    * @deprecated
    */
   public Buffer getCurrentProfile()
   {
       //return currprofbuf;
       return null;
   }
   
   public boolean isUserProfileDisabled()
   {
      return !this.modifiable;
   }

}