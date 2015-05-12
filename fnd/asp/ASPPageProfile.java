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
 * File        : ASPPageProfile.java
 * Description : User profile for ASPPage
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Artur K  2000-Nov-23 - New class for handling page(global) profile
 *    Piotr Z
 *    Jacek P  2001-Apr-26 - Implemented clone() method.
 *    Mangala  2002-Mar-25 - Changed fromByteString() and toByteStrings()methods to be
 *                           compatible with UTF-8
 *    Suneth M 2002-Sep-12 - Changed clone() method.
 *    Jacek P  2004-Nov-11 - Added API for new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2007/05/04 sadhlk Bug id 64337, Added checkItemFromBase() and markRemoveFlag().
 * 2006/10/18 riralk Bug id 57025, Modified parse() to consider Position of portal views.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 * Revision 1.5  2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 * Revision 1.4  2005/06/09 09:27:25  japase
 * Added parsing of Global Variables
 *
 * Revision 1.3  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.2.2.1  2005/03/08 15:08:19  japase
 * Some changes to the conversion algorithm
 *
 * Revision 1.2  2005/02/02 08:22:18  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.2  2004/12/16 11:56:40  japase
 * First changes for support of the new profile concept.
 *
 * ----------------------------------------------------------------------------
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
 * ASPPage.doFreeze() creates the instance of ASPPageProfile
 * which is stored in the page pool as the mutable attribute 'profile'.
 *
 * During every request ASPPage.?????() fetches the user specific profile
 * by calling ASPProfile.get(this,pre_profile). The returned Object
 * is assigned to the 'profile'-attribute.
 *
 * ASPPage.doReset() re-assigns the value of the 'profile'-attribute.
 *
 * Modified profile are stored in the cache (as Object) and in the database
 * (as String).
 */

class ASPPageProfile extends ASPPoolElementProfile
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPageProfile");

   private Buffer          profbuf_old;
   private BufferFormatter frmt;   
     
   // profile contents
   private ProfileBuffer profbuf;

   protected ASPPageProfile()
   {
   }

   protected ASPPoolElementProfile newInstance()
   {
      return new ASPPageProfile();
   }

   protected void construct( ASPPoolElement template )
   {
      /*Factory fac = ((ASPPage)template).getASPConfig().getFactory();
      profbuf_old     = fac.getBuffer();
      frmt        = fac.getBufferFormatter();*/
   }

   public Object clone()
   {
      if(DEBUG) debug(this+": ASPPageProfile.clone()");

      ASPPageProfile page_profile = (ASPPageProfile)newInstance();

      synchronized(this)
      {
         /*page_profile.profbuf_old = (Buffer)profbuf_old.clone();
         try
         {
            page_profile.frmt = (BufferFormatter)frmt.getClass().newInstance();
         }
         catch(Exception x)
         {
            throw new RuntimeException("Can not create an instance of BufferFormatter: "+x.getMessage());
         }*/
         page_profile.profbuf = profbuf; // point out the same ProfileBuffer ?
      }
      if(DEBUG) debug(this+": clone(): "+page_profile);
      return page_profile;
   }

   public boolean equals( Object obj )
   {
      if( obj instanceof ASPPageProfile ) return true;
      return false;
   }

   /**
    * Deserialize profile information in a given string and apply to an ASPPoolElement.
    * Called from ASPProfile.findProfile() after fetching from the database.
    *
    * @deprecated
    */
   protected void parse( ASPPoolElement target, String text ) throws FndException
   {
      if(DEBUG) debug(this+"ASPPageProfile.parse():\n\t\t"+text);

      if(profbuf_old == null || frmt == null)
      {
         Factory fac = ((ASPPage)target).getASPConfig().getFactory();
         profbuf_old     = fac.getBuffer();
         frmt        = fac.getBufferFormatter();
      }
      else
      {
         profbuf_old.clear();
      }

      try
      {
         // Temporary solution:
         // in future release parsing should be done by a new class Base64BufferFormatter
         text = toByteString( Util.fromBase64Text( text ) );
         if(DEBUG) debug("  from Base64="+text);
         frmt.parse( text, profbuf_old );

         if(DEBUG) debug("Parsed Page Profile:\n"+Buffers.listToString(profbuf_old));

      }
      catch( Exception any )
      {
         throw new FndException("FNDPAGPRFPRS: Cannot parse profile for ASPPage: '&1'", text)
                   .addCaughtException(any);
      }
   }

   /**
    * Serialize profile information from a given ASPPoolElement to a string.
    * Called from ASPProfile.save() before storing in the database.
    *
    * @deprecated
    */
   protected String format( ASPPoolElement target ) throws FndException
   {
      if(DEBUG) debug(this+"ASPPageProfile.format()");

      try
      {
         // Temporary solution:
         // in future release formatting should be done by a new class Base64BufferFormatter
         if(DEBUG) debug("Page Profile to format:\n"+Buffers.listToString(profbuf_old));
         String s = frmt.format(profbuf_old);
         if(DEBUG) debug("  profbuf_old="+s);
         s = Util.toBase64Text( fromByteString( s ) );
         if(DEBUG) debug("  to Base64="+s);

         return s;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDPAGPRPPRS: Cannot format profile for ASPPortal.")
                   .addCaughtException(any);
      }
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
      if(DEBUG) debug(this+"ASPPageProfile.parse():\n\t\t"+text);      
      
      try
      {
         buffer.clear();

         text = toByteString( Util.fromBase64Text( text ) );
         if(DEBUG) debug("  from Base64="+text);
         fmt.parse( text, buffer );

         if(DEBUG) debug("Parsed Page Profile:\n"+Buffers.listToString(buffer));
                 

         // rename duplicated items: VIEWS/AVAILABLE/<xxx>/LINKS/<yyy>/URL
         // mark all nodes with ProfileEntry
         correctParsedBuffer(buffer, 0, false);
         
         try{
             Buffer views = buffer.getBuffer("PortalViews");
             if (views!=null)
             {
               Buffer avail = views.getBuffer("Available");
               if (avail!=null)
               {             
                 for (int i=0; i<avail.countItems(); i++)
                 {
                   Item it = avail.getItem(i);  
                   if (it!=null && it.getBuffer()!=null)
                     it.getBuffer().addItem(ASPPortal.PORTAL_POSITION, i);
                 }
               }
             }
         }
         catch(ItemNotFoundException inf){
             if(DEBUG) debug("This profile does not seem to contain any portal information");
         }
         if(DEBUG) debug("Parsed and prepared Page Profile:\n"+Buffers.listToString(buffer));
      }
      catch( Throwable any )
      {
         throw new FndException("FNDPAGEPRPARSETOBUF: Cannot parse profile for ASPPage: '&1'",text)
                   .addCaughtException(any);
      }
   }

   private static final String TO_REMOVIAL = "TO_REMOVIAL";

   /**
    * Adapt the profile buffer to the new profile handling by marking all
    * leaves with profile entry "Page Node" and resolving structure of links
    * in track VIEWS/AVAILABLE/<xxx>/LINKS/<yyy>/URL
    * All other names except VIEWS, DEFPRINTER and DEFLANGUAGE on the top level
    * represent Global Variables.
    */
   private void correctParsedBuffer( ProfileBuffer buffer, int level, boolean ontrack ) throws FndException
   {
      // possible items at level 0:
      //  - VIEWS
      //  - DEFPRINTER
      //  - DEFLANGUAGE
      //  - Global Variables

      // the track is: VIEWS/AVAILABLE/<xxx>/LINKS/<yyy>/URL
      for( int i=0; i<buffer.countItems(); i++ )
      {
         ProfileItem item = (ProfileItem)buffer.getItem(i);
         String      name = item.getName();

         if(DEBUG) debug("Found item '"+name+"' at level "+level+", ontrack="+ontrack);

         //------------------------------------------------------------------------
         // Parse the main track: VIEWS/... buffer structure
         if( level==0 && item.isCompound() && "VIEWS".equals(name) )
         {
            item.setName("PortalViews");
            correctParsedBuffer( (ProfileBuffer)item.getBuffer(), level+1, true );
         }
         else if( level==1 && ontrack && item.isCompound() && "AVAILABLE".equals(name) )
         {
            item.setName("Available");
            correctParsedBuffer( (ProfileBuffer)item.getBuffer(), level+1, true );
         }
         else if( level==2 && ontrack && item.isCompound() )
         {
            //TODO: temporary solution (JAPASE)?
            item.setName( name + ProfileUtils.ENTRY_SEP + "Page Node" );
            item.setState(ProfileItem.QUERIED);            
            //JAPASE: end

            correctParsedBuffer( (ProfileBuffer)item.getBuffer(), level+1, true );
         }
         else if( level==3 && ontrack && item.isCompound() && "LINKS".equals(name) )
         {
            item.setName("Links");
            correctParsedBuffer( (ProfileBuffer)item.getBuffer(), level+1, true );
         }
         else if( level==4 && ontrack && item.isCompound() )
            correctParsedBuffer( (ProfileBuffer)item.getBuffer(), level+1, true );
         else if( level==5 && ontrack && !item.isCompound() && "URL".equals(name) )
         {            
            /*
            //TODO: (JAPASE)
            String desc = item.getString();
            int    pos  = desc.indexOf('~');

            String val  = pos>=0 ? desc.substring(pos+1)  : desc;
                   desc = pos>0  ? desc.substring(0, pos)
                                 : "NO_NAME_"+(new NumberFormatter(DataFormatter.INTEGER,"000")).format(new Integer(i));
            item.setName("URL"+ProfileUtils.ENTRY_SEP+desc);
            item.setState(ProfileItem.QUERIED);
            item.setValue(val);
            */
         }
         // The main track ends here
         //------------------------------------------------------------------------

         //------------------------------------------------------------------------
         // Global profile variables start here
         else if( level==0 )
         {
            if( item.isCompound() )
               name = name + ProfileUtils.ENTRY_SEP + "Buffer";
            else
            {
               if     ( "DEFPRINTER" .equals(name) )  name = TO_REMOVIAL;//japa: DefaultPrinter";
               else if( "DEFLANGUAGE".equals(name) )  name = TO_REMOVIAL;//japa: DefaultLanguage";
               else   // Global Values
               {
                  // Can only be strings (created by writeGlobalProfileValue() ) or
                  // booleans (created by writeGlobalProfileFlag() )
                  String val = (String)item.getValue();
                  if( "B".equals(item.getType()) || "TRUE".equals(val) || "FALSE".equals(val) )
                     name = name + ProfileUtils.ENTRY_SEP + "Boolean";
                  else
                     name = name + ProfileUtils.ENTRY_SEP + "String";
               }
            }
            item.setName( name );
         }
         // Global profiles end here
         //------------------------------------------------------------------------

         // sidetracks from the main, VIEWS, track; not at level 0
         else if( item.isCompound() )
            correctParsedBuffer( (ProfileBuffer)item.getBuffer(), level+1, false );
         else
         {
            // Sub items of the main VIEWS track
            if( level==1 && "CURRENT".equals(name) )
            {
               //TODO: (JAPASE):
               name = "Current"+ ProfileUtils.ENTRY_SEP + "Page Node";
               item.setState(ProfileItem.QUERIED);
            }
            else if( level==3 && "DESC".equals(name) )  name = "Description";
            //JAPASE: else if( level==5 && "TITLE"      .equals(name) )  name = "Title";
            item.setName( name );//japa: + ProfileUtils.ENTRY_SEP + "Page Node" );
            //JAPASE: item.setState(ProfileItem.QUERIED);
         }
      }
      while( buffer.findItem(TO_REMOVIAL)!=null )
         buffer.removeItem(TO_REMOVIAL);           
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Store reference to profile sub-buffer containing all profile
    * information corresponding to this instance.
    */
   protected void assign( ASPPoolElement target, ProfileBuffer buffer ) throws FndException
   {
      //throw new FndException("Profile functionality for class '&1' is not implemented yet", getClass().getName());
       profbuf = buffer;
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Return reference to sub-buffer containing profile information
    * corresponding to the current instance
    */
   protected ProfileBuffer extract( ASPPoolElement target ) throws FndException
   {
      //throw new FndException("Profile functionality for class '&1' is not implemented yet", getClass().getName());
      return profbuf;
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Synchronize the internal state with the content of the ProfileBuffer.
    */
   protected void refresh( ASPPoolElement target ) throws FndException
   {
      //throw new FndException("Profile functionality for class '&1' is not implemented yet", getClass().getName());
   }

   //==========================================================================
   //
   //==========================================================================

   private void debug( String text )
   {
      Util.debug(text);
   }

   void showContents( AutoString out ) throws FndException
   {
      out.append("   ",this.toString(),":\n");
      out.append("   ",Buffers.listToString(profbuf),":\n");
   }

   // Temporary solution: functions copied from Util.java

   /**
    * Add 0 as high-byte value to every character
    * Same as new String(data,0), which is depricated.
    */
   private static String toByteString( byte[] data ) throws IOException
   {
      return new String(data,"UTF-8");
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
    * Change item value or create a new one in buffer stored in the class instance.
    * The item_name can contain "/"-operator, for example "A/B/C" points to
    * item "C" in sub-buffer "B" of sub-buffer "A" of this buffer.
    */
   void writeItem(String item_name, Object obj) throws ItemNotFoundException
   {
      if( profbuf==null )
         profbuf = ProfileUtils.newProfileBuffer();

      Item item = profbuf.findItem(item_name);
      if (item != null)
         item.setValue(obj);
      else
      {
         StringTokenizer st = new StringTokenizer( item_name, "/");
         Buffer         buf = profbuf;

         while( st.hasMoreTokens() )
         {
            String t = st.nextToken();
            item = buf.findItem(t);
            if(item == null)
               if (st.hasMoreTokens())
               {
                  buf.addItem(t,buf.newInstance());
                  buf = buf.getBuffer(t);
               }
               else
               {
                  buf.addItem(t,obj);
                  break;
               }
            else
               buf = item.getBuffer();
         }
      }
      if(DEBUG) debug("Set Item in Page Profile:\n"+Buffers.listToString(profbuf_old));
   }

   /**
    * Read item value from buffer stored in the class instance.
    * The item_name can contain "/"-operator, for example "A/B/C" points to
    * item "C" in sub-buffer "B" of sub-buffer "A" of this buffer.
    */
   Object readItem(String item_name)
   {
      if( profbuf==null )
        return null;

      Item item = profbuf.findItem(item_name);
      if (item != null)
      {
         Object value = item.getValue();
         return value;
      }
      else
         return null;
   }

   /**
    * Remove item from buffer stored in the class instance.
    * The item_name can contain "/"-operator, for example "A/B/C" points to
    * item "C" in sub-buffer "B" of sub-buffer "A" of this buffer.
    */
   void removeItem(String item_name)
   {
         int delim   = item_name.lastIndexOf("/");
         if(delim == -1)
         {
            if ( item_name.indexOf(ProfileUtils.ENTRY_SEP)>=0 )
               profbuf.removeItem(item_name);
            //remove item which is not a leaf ("^") node Q&D ?  //for removing the entire "Links" buffer
            // maybe this should be done in ProfileBuffer class??
            else
            {
              try{
               Buffer buf = profbuf.getBuffer(item_name);
               buf.removeItems();
              }
              catch(ItemNotFoundException i){}
            }
         }
         else
         {
            String name = item_name.substring(0,delim);
            String id   = item_name.substring(delim+1);
            Item item   = profbuf.findItem(name);
            if(item==null) return;
            Buffer buf  = item.getBuffer();
            if ( id.indexOf(ProfileUtils.ENTRY_SEP)>= 0 )
              buf.removeItem(id);
            else
            {
              try{
               Buffer tmp = buf.getBuffer(id);
               tmp.removeItems();
              }
              catch(ItemNotFoundException i){}
            }
         }
   }
   
   /**
    * Check whether the the ProfileItem is from base 
    * or not.
    */
   boolean checkItemFromBase(String item_name)
   {
      if( profbuf==null )
        return false;

      ProfileItem item = (ProfileItem)profbuf.findItem(item_name);
      if(item != null)
      {
         return item.isFromBase();
      }
      else
         return false;
   }
   
   /**
    * Set the removeFlag of the given items' to true
    * If the given item is a buffer, set all the items removeFlag
    * in the buffer to true.
    */
   void markRemoveFlag(String item_name)
   {
      int delim = item_name.lastIndexOf("/");
      if(delim == -1)
      {
         if(item_name.indexOf(ProfileUtils.ENTRY_SEP)>=0)
            profbuf.markItem(item_name);
         else
         {
            try
            {
               Buffer buf = profbuf.getBuffer(item_name);
               profbuf.markItems(buf);
            }
              catch(ItemNotFoundException i){}
         }
      }
      else
      {
         String name = item_name.substring(0,delim);
         String id   = item_name.substring(delim+1);
         Item item   = profbuf.findItem(name);
         if(item==null) return;
         Buffer buf  = item.getBuffer();
         if ( id.indexOf(ProfileUtils.ENTRY_SEP)>= 0 )
          profbuf.markItem(buf,id);
         else
         {
           try{
            Buffer tmp = buf.getBuffer(id);
            profbuf.markItems(tmp);
           }
           catch(ItemNotFoundException i){}
         }
      }
   }

}