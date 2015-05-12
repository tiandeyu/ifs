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
 * File        : ProfileUtils.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-Oct-20 - Created.
 *    Jacek P  2005-Jan-28 - Changes in FNDEXT view names:
 *                           - Class name ManageUserProfileParameterViews.AdministrateUserProfiles_LoadUserProfile_Parameters
 *                             changed to ManageUserProfileViews.AdministrateUserProfiles_LoadUserProfile
 *                           - Class name ManageUserProfileParameterViews.AdministrateUserProfiles_SaveUserProfile_Parameters
 *                             changed to ManageUserProfileViews.AdministrateUserProfiles_SaveUserProfile
 *                           Affected methods: loadJAP(), loadRMI(), saveJAP(), saveRMI().
 *    Jacek P  2005-Dec-27 - Write the XML document to debug output on parse error.
 *    Jacek P  2006-Mar-13 - Skipping invalid profiles in loadRMI()
 *    Jacek P  2006-Mar-21 - Corrected problem with site name aliases on conversion
 *    Rifki R  2006-Sep-28 - Bug id 60861, set System property in main() to be used when converting profiles.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/07/14 buhilk Bug 90508, Modified addToProfile() to add missing paranthesis to state "Modified" values.
 * 09/04/2010 rahelk bug 89989, changed XMLStreamReader to support parsing by weblogic
 * 2008/09/09 dusdlk Bug 77095, Updated mergeProfiles() to check for the index of the ENTRY_SEP and assign value for the path variable.
 * 2008/09/02 buhilk Bug 76759, Added functionality to support saving of Theme settings to common location in user profile
 * Bug 67682, Added findOrCreateNestedItem() overloaded method. Modified addToProfile(), profileValuesToBuffer(), bufferToProfileValues()
 *               2007/09/20 sadhlk
 * Bug 67682, Added findOrCreateNestedItem() overloaded method. Modified addToProfile(), profileValuesToBuffer(), bufferToProfileValues()
 *               2007/07/23 sadhlk
 * Merged Bug 65393, Modified bufferToProfileValues() to avoid adding already existing client profile values.
 *               2007/07/03 sadhlk
 * Merged Bug 64669, Modified the ClientPrf inner class constructors to avoid null pointer exception when personal
 *            Profile is diabled.
 *               2007/05/14 sadhlk 
 * Bug 65359, Modified profileValuesToBuffer().
 *               2007/05/04 sadhlk
 * Bug 64337, Modified profileValuesToBuffer(), bufferToProfileValues() to remove portlets and
 *            Portal views of base profile properly.
 *               2007/01/23 buhilk
 * Improvement in code to avoid null pointer exceptions in bug fix 62078.
 *               2007/01/19 buhilk
 * Bug 62078, Modified the addToProfile() method to fic the bug where the MyLinks profile buffer is not
 *            properly displayed inside the solution manager. 
 *
 *               2006/11/16 mapelk
 * Bug 61887, Improved performance on conversion tool. 
 *               2006/11/10 sumelk
 * Bug 61623, Changed bufferToProfileValues() to avoid the error occurs when using base profiles.
 *
 *               2006/10/25 riralk
 * Bug id 57025. Modified correctParsedBuffer() to fix Repository URLs to point to new environment during profile conversion.
 *
 * Revision 1.6  2005/11/17 07:18:55  japase
 * Corrected findElement() for JAP
 *
 * Revision 1.5  2005/11/16 14:38:03  japase
 * Added handling of Regional Settings
 *
 * Revision 1.4  2005/11/08 12:34:11  japase
 * Correction of accidently commit - back to version 1.2
 *
 * Revision 1.2  2005/10/14 09:08:15  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.22  2005/08/18 14:07:43  riralk
 * Fixed Unique contraint error in profiles for JAP. Modified saveJAP() to refresh the record in ClientPrf instance using the record returned by the server invoke.
 *
 * Revision 1.21  2005/08/04 13:41:21  riralk
 * Fixed/Removed profile related Q&D's and removed obsolete code.
 *
 * Revision 1.20  2005/07/14 11:52:45  japase
 * Fixed problrm with null values in CLOB column (binary value) - for JAP
 *
 * Revision 1.19  2005/07/12 10:33:43  japase
 * Correction to the profile handling - manipulated buffers, for example on sort, have been marked as dirty.
 *
 * Revision 1.18  2005/07/12 06:59:58  japase
 * Fixed problrm with null values in CLOB column (binary value)
 *
 * Revision 1.17  2005/06/30 17:31:59  japase
 * Resetting profile buffer after successful saving.
 *
 * Revision 1.16  2005/06/09 11:49:56  japase
 * Reverse the latest change
 *
 * Revision 1.15  2005/06/09 11:30:29  rahelk
 * CSL 2: private settings
 *
 * Revision 1.14  2005/06/09 10:10:39  japase
 * Added parsing of Global Variables
 *
 * Revision 1.13  2005/06/08 11:50:25  japase
 * Fixed trailing slash problem in load functions
 *
 * Revision 1.12  2005/06/08 09:59:42  japase
 * Added support for XML conversion
 *
 * Revision 1.11  2005/06/06 05:42:58  rakolk
 * Fixing the build
 *
 * Revision 1.10  2005/05/20 10:47:04  rakolk
 * Temporally fixes to build errors
 *
 * Revision 1.9  2005/05/17 10:44:13  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.8  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.7  2005/02/28 12:34:48  japase
 * Corrected problems related to conversion tool
 *
 * Revision 1.5.2.5  2005/03/08 15:04:53  japase
 * Some changes to the conversion algorithm
 *
 * Revision 1.5.2.4  2005/03/08 09:15:31  riralk
 * Removed 'Page' level from Global profiles generated by conversion tool. Modified parseProfileBuffer().
 *
 * Revision 1.5.2.3  2005/03/07 03:54:39  riralk
 * Fixed bug which occurs when a Portal view is removed before it has been modified and saved (i.e. Customized). Caught ItemNotFoundException in bufferToProfileValues().
 *
 * Revision 1.5.2.2  2005/03/03 11:48:57  riralk
 * Removed cloning of profile buffer in ASPPortalProfile.clone() and some minor fixes.
 *
 * Revision 1.5.2.1  2005/02/28 12:34:15  japase
 * Corrected problems related to conversion tool
 *
 * Revision 1.5  2005/02/24 13:48:59  riralk
 * Adapted Portal profiles to new profile algorithm. Removed some obsolete code.
 *
 * Revision 1.4  2005/02/16 08:36:27  japase
 * Don't clear Profile Cache in stand-alone mode
 *
 * Revision 1.3  2005/02/08 20:50:24  jehuse
 * Updated ClientProfile model.
 * Added UserProfileAdministration.
 *
 * Revision 1.2  2005/02/02 08:22:19  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.7  2005/01/28 09:40:49  japase
 * Changed class names in FNDEXT API
 *
 * Revision 1.6  2005/01/27 17:24:38  jehuse
 * Attribute ReadOnly changed name to OverrideAllowed (with inverted sematics)
 *
 * Revision 1.5  2005/01/11 04:20:54  riralk
 * Temporary Q&D solution to refresh profile after saving. Called ASPProfileCache.clear() in method save(). Note: This must be replaced later with a better solution.
 *
 * Revision 1.4  2004/12/30 11:57:01  japase
 * Improvments of the conversion algorithm. Now possible to run for all users/sites.
 *
 * Revision 1.3  2004/12/29 08:41:55  japase
 * Splited to two files
 *
 * Revision 1.2  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
 *
 * Revision 1.1  2004/12/16 11:56:40  japase
 * First changes for support of the new profile concept.
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.namespace.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.ap.*;
import ifs.fnd.base.*;
import ifs.fnd.record.*;
import ifs.fnd.websecurity.*;

import ifs.application.manageuserprofile.*;
import ifs.application.clientprofile.*;


/**
 * A utility class for handling of new profiles. Works also in stand-alone mode as
 * a tool converting old profiles to the new format.
 */
public class ProfileUtils implements Serializable  // only for finding profile cache size
{
   //==========================================================================
   // Static variables
   //==========================================================================

   private static final String  USER_PROFILE_PREFIX  = "User/Web/";
   private static final String  USER_COMMON_PROFILE_PREFIX  = "User/General/";
   private static final String  USER_REGIONAL_SETTINGS_PREFIX = "User/General/Regional Settings";
   private static final String  DATA_NOT_COMPRESSED  = "__DATA_NOT_COMPRESSED";
   public  static final char    ENTRY_SEP            = '^';
   private static final String  USER_THEME_SETTINGS_PREFIX = "User/General/Theme";
   public  static final String  SELECTED_THEME             = "Theme"+ProfileUtils.ENTRY_SEP+"SelectedTheme";

   public  static boolean DEBUG   = Util.isDebugEnabled("ifs.fnd.asp.ProfileUtils");
   public  static boolean DEBUGEX = Util.isDebugEnabled("ifs.fnd.asp.ProfileUtils.extended");
   public  static boolean TRACE   = false;
   private static boolean console = false;
   
   //properties used only during profile conversion   
   private static final String BASE_URL = "#BASE_URL#";

   private static ProfileUtils prfutils;


   //==========================================================================
   //  Instance variables
   //==========================================================================

   private Properties properties  = null;
   private boolean    RMI         = true;
   private boolean    testonly    = false;
   private boolean    restrictive = true;
   private String     samples     = null;

   private String  last_site       = null;
   private String  last_site_alias = null;

   private ProfileBuffer common_profile = null;
   private ProfileBuffer theme_profile  = null;


   //==========================================================================
   //  Construction of the only one static instance of ProfileUtils
   //==========================================================================

   /**
    * Private constructor to be called locally from main()
    */
   private ProfileUtils( Properties properties ) throws FndException
   {
      init(properties, null);
   }

   /**
    * Private constructor to be called indirectly from ASPConfigFile
    */
   private ProfileUtils( Map cfgparams ) throws FndException
   {
      init(null, cfgparams);
      TRACE = DEBUG || "Y".equals( getConfigParam(cfgparams, "AUDIT/TRACE/ENABLED","N") );
   }

   /**
    * Initialization of the class. Requires one and only one argument.
    */
   private void init( Properties props,  Map cfg ) throws FndException
   {
      if(DEBUG)
      {
         FndDebug.setDebugAll(true);
         debug("ProfileUtils.init()");
      }
      else if(TRACE)
      {
         FndDebug.setDebugCallSequence(true);
         FndDebug.setDebugStubArguments(true);
         FndDebug.setDebugSkeletonArguments(true);
      }

      this.properties = props;
      this.RMI = !"JAP".equals( cfg==null ? props.getProperty("method") : getConfigParam(cfg, "ADMIN/TRANSACTION_MANAGER","RMI") );

      // needed only for stand alone run, i.e. no FNDWEB configuration, only properties
      if(cfg==null)
      {
         this.testonly    = "true".equals( props.getProperty("testonly") );
         this.restrictive = !"false".equals( props.getProperty("restrictive") );
         this.samples     = props.getProperty("samples");
         SecurityHandler.init(props);
      }
   }

   /**
    * Return named configuration parameter or default value if not found.
    */
   private String getConfigParam( Map cfg, String name, String def_value )
   {
      Object value = cfg.get(name);
      if( !(value instanceof String) )
         return def_value;
      String str = (String)value;
      return Str.isEmpty(str) ? def_value : str;
   }

   //==========================================================================
   //  Static API to be called from the framework
   //==========================================================================

   /**
    * Init method creating the only instance of ProfileUtils.
    * To be called from ASPConfigFile on construction.
    */
   static void initProfileUtils( Map cfgparams ) throws FndException
   {
      prfutils = new ProfileUtils(cfgparams);
   }

   /**
    * Load profile information from the database for given 'path' and 'userid'.
    * The fetched profile is converted to Buffer 'profile'.
    * Return an instance of ClientPrf class that has to be used when saving profile later on.
    */
   static ClientPrf load( ProfileBuffer profile, String path, String userid ) throws FndException
   {
      return prfutils.loadProfile( profile, USER_PROFILE_PREFIX+path, userid);
   }

   /**
    * Save profile information containded in the Buffer 'profile' for user given
    * in instance of ClientPrf. The instance retrieved from load() should be used.
    */
   static void save( ClientPrf clprf, ProfileBuffer profile, String rfc_lang ) throws FndException
   {
      prfutils.saveProfile(clprf, profile, rfc_lang);
   }

   /**
    * Convert class name to more user friendly representation.
    */
   static String convertClassName( ASPPoolElement element )
   {
      if(element instanceof ASPPage)
         return "Page";
      else
         return convertClassName( element.getClass().getName() );
   }

   /**
    * Convert class name to more user friendly representation.
    */
   private static String convertClassName( String classname )
   {
      if( "ifs.fnd.asp.ASPPage".equals(classname) )
         return "Page";
      else if( "ifs.fnd.asp.ASPTable".equals(classname) )
         return "Table";
      else if( "ifs.fnd.asp.ASPBlock".equals(classname) )
         return "Block";
      else if( "ifs.fnd.asp.ASPPortal".equals(classname) )
         return "Portal";
      else if( "ifs.fnd.asp.ASPBlockLayout".equals(classname) )
         return "BlockLayout";
      else if( "ifs.fnd.asp.ASPCommandBar".equals(classname) )
         return "CommandBar";
      else if( "ifs.fnd.asp.ASPTabContainer".equals(classname) )
         return "TabContainers";     

      return classname;
   }

   //==========================================================================
   //  Inner classes as a part of the API
   //==========================================================================

   /**
    * An inner class encapsluating the current instance of ClientProfile
    * and User ID.
    */
   class ClientPrf implements Serializable
   {
      private           ClientProfile  cp;
      private transient Record         rec;
      private           String         userid;

      /**
       * Private constructor called from loadRMI()
       */
      private ClientPrf( ClientProfile cp )
      {
         this.cp     = cp;
         this.userid = cp !=null ? cp.owner.getValue():null;
      }

      /**
       * Private constructor called from loadJAP()
       */
      private ClientPrf( Record rec )
      {
         this.rec    = rec;
         this.userid = rec !=null ? rec.find("OWNER").getString():null;
      }

      private ClientProfile getClientProfile()
      {
         return cp;
      }

      private Record getRecord()
      {
         return rec;
      }

      private void setRecord(Record rec)
      {
         this.rec=rec;
      }

      /**
       * Return user ID stored in this instance.
       */
      String getUserId()
      {
         return userid;
      }
   }

   //==========================================================================
   //  Other static and help methods of general purpose
   //==========================================================================

   //=======================================================================
   //  - Help routines for Buffers
   //=======================================================================

   /**
    * Find an item in 'buffer' given name 'path'.
    * 'path' can contain slashes ('/') as level separators.
    * If there is no item for the given name, a new one is created.
    * 'buffer' and 'path' cannot be null. 'path' cannot be empty.
    */
   static Item findOrCreateNestedItem( Buffer buffer, String path ) throws FndException
   {
      return findOrCreateNestedItem(buffer, path, false);
   }

   private static Item findOrCreateNestedItem( Buffer buffer, String path, boolean clear ) throws FndException
   {
      if( buffer==null || Str.isEmpty(path) )
         throw new FndException("FNDPROFILEUTILSEMPTYPAR: Buffer and path cannot be null! Path cannot be empty!");

      Buffer buf  = buffer;
      Item   item = null;
      StringTokenizer pst = new StringTokenizer(path, "/");
      while( pst.hasMoreTokens() )
      {
         String name = pst.nextToken();

         if(item!=null)
         {
            if( !item.isCompound() )
               item.setValue( buf.newInstance() );
            buf = item.getBuffer();
         }

         item = buf.findItem(name);

         if(item==null)
         {
            item = buffer.newItem();
            item.setName(name);
            if( buf instanceof ProfileBuffer )
               ((ProfileBuffer)buf).addItem(item, clear);
            else
               buf.addItem(item);
         }
      }
      return item;
   }
 
   /**
   * Find an item in 'buffer' given name 'path'.
   * 'path' can contain slashes ('/') as level separators.
   * If there is no item for the given name, check for the alternate Name 'altName'
   * If there is no item for that too, then a new one is created.
   * 'buffer' and 'path' and 'altName' cannot be null. 'path' cannot be empty.
   */
   static Item findOrCreateNestedItem( Buffer buffer, String path, String altName) throws FndException
   {
      return findOrCreateNestedItem(buffer, path, altName, false);
   }

   private static Item findOrCreateNestedItem( Buffer buffer, String path, String altName, boolean clear ) throws FndException
   {
      if( buffer==null || Str.isEmpty(path) || Str.isEmpty(altName) )
         throw new FndException("FNDPROFILEUTILSEMPTYALTPAR: Buffer, path, altPath cannot be null! Path cannot be empty!");

      Buffer buf  = buffer;
      Item   item = null;
      StringTokenizer pst = new StringTokenizer(path, "/");
      while( pst.hasMoreTokens() )
      {
         String name = pst.nextToken();

         if(item!=null)
         {
            if( !item.isCompound() )
               item.setValue( buf.newInstance() );
            buf = item.getBuffer();
         }

         item = buf.findItem(name);

         if(item==null)
         {
            item = buf.findItem(altName);
            if(item == null){
               item = buffer.newItem();
               item.setName(name);
               if( buf instanceof ProfileBuffer )
                  ((ProfileBuffer)buf).addItem(item, clear);
               else
                  buf.addItem(item);
            }
         }
      }
      return item;
   }   

   /**
    * Find a nested buffer in 'buffer' given name 'path'.
    * 'path' can contain slashes ('/') as level separators.
    * If there is no nested buffer for the given name, a new one is created.
    * 'buffer' and 'path' cannot be null. 'path' cannot be empty.
    */
   static Buffer findOrCreateNestedBuffer( Buffer buffer, String name ) throws FndException
   {
      return findOrCreateNestedBuffer(buffer, name, false);
   }

   private static Buffer findOrCreateNestedBuffer( Buffer buffer, String name, boolean clear ) throws FndException
   {
      Item item = findOrCreateNestedItem(buffer, name, clear);
      if( !item.isCompound() )
         item.setValue( buffer.newInstance() );
      return item.getBuffer();
   }

   /**
    * Create new instance of ProfileBuffer.
    */
   public static ProfileBuffer newProfileBuffer()
   {
      return new ProfileBuffer();
   }

   /**
    * Create new instance of BufferFormatter.
    */
   private static StandardBufferFormatter newBufferFormatter()
   {
      return new StandardBufferFormatter();
   }

   /**
    * Create new instance of Buffer.
    */
   private static Buffer newBuffer()
   {
      return new StandardBuffer();
   }

   //=======================================================================
   //  - Methods for XML parsing
   //=======================================================================

   /**
    * The platform's line separator sequence.
    */
   static final String NL = System.getProperty("line.separator");

   /**
    * Convert given XML document to an instance of ProfileBuffer. This method does not validate values.
    */
   private static void fromXML( String xml_text, ProfileBuffer to_buf ) throws FndException
   {
      if(TRACE) debug(" - Converting XML to ProfileBuffer:",xml_text);

      XMLInputFactory xmlif = XMLInputFactory.newInstance();                                // Default value

      xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE ,             Boolean.TRUE);     //  true
      xmlif.setProperty(XMLInputFactory.IS_COALESCING ,                  Boolean.TRUE);     //  false
      xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,  Boolean.TRUE);     //  true
      xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);    //   -
      /*
      xmlif.setProperty(XMLInputFactory.IS_VALIDATING ,                  Boolean.TRUE);     //  false
      xmlif.setProperty(XMLInputFactory.SUPPORT_DTD,                     Boolean.FALSE);    //  true
      xmlif.setProperty(XMLInputFactory.REPORTER,                        Boolean.FALSE);    //   -
      xmlif.setProperty(XMLInputFactory.RESOLVER,                        Boolean.FALSE);    //   -
      xmlif.setProperty(XMLInputFactory.ALLOCATOR,                       Boolean.FALSE);    //   -
      */

      try
      {
         //XMLStreamReader xmlr = xmlif.createXMLStreamReader(new StringReader(xml_text));

         StringReader sr = new StringReader(xml_text);
         XMLStreamReader xmlr = xmlif.createFilteredReader(xmlif.createXMLStreamReader(sr),
                                                           new StreamFilter(){
                                                              public boolean accept(XMLStreamReader reader) {
                                                                  if (reader.isWhiteSpace())
                                                                      return false;
                                                                  else
                                                                      return true;
                                                              }
         });


         while( xmlr.hasNext() )
         {
            int event_type = xmlr.next();

            if(DEBUG) debugXMLEventType(xmlr, event_type);

            //if (xmlr.isWhiteSpace() ) continue; // if filter doesn't work on all App servers

            if( ProfileBuffer.fromXML(xmlr, to_buf) )
               ;
            else if( event_type!= XMLStreamConstants.END_DOCUMENT )
               throw new FndException("FNDPROFILEUTILSUNRECENTRY: Error while parsing XML: Unrecognized XML entry '&1'.",xmlr.hasName() ? xmlr.getLocalName() : "");
         }
         xmlr.close();
      }
      catch( XMLStreamException x )
      {
         debug("Error while parsing XML document:", xml_text);
         throw new FndException(x);
      }
   }

   /**
    * Convert given ProfileBuffer to XML. This method does not validate values.
    */
   private static String toXML( ProfileBuffer buf ) throws FndException
   {
      if(DEBUG) debug(" - Converting ProfileBuffer to XML:",buf);
      AutoString out = new AutoString();
      out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",NL);
      buf.toXML(out,0);
      String xml = out.toString();
      if(DEBUG) debug(" - The resulting XML document:",xml);
      return xml;
   }

   /**
    * Help method used from toXML() for better formatting of the resulting XML document.
    */
   static void indent( AutoString buf, int cnt )
   {
      for( int i=0; i<cnt; i++ )
         buf.append(' ');
   }

   //  - Debugging of XML
   /**
    * Returns description of an event type.
    * @param eventType
    * @return
    */
   static String getXMLEventTypeAsString( int event_type )
   {
      switch(event_type)
      {
         case XMLEvent.START_ELEMENT:
            return "START_ELEMENT";
         case XMLEvent.END_ELEMENT:
            return "END_ELEMENT";
         case XMLEvent.PROCESSING_INSTRUCTION:
            return "PROCESSING_INSTRUCTION";
         case XMLEvent.CHARACTERS:
            return "CHARACTERS";
         case XMLEvent.COMMENT:
            return "COMMENT";
         case XMLEvent.START_DOCUMENT:
            return "START_DOCUMENT";
         case XMLEvent.END_DOCUMENT:
            return "END_DOCUMENT";
         case XMLEvent.ENTITY_REFERENCE:
            return "ENTITY_REFERENCE";
         case XMLEvent.ATTRIBUTE:
            return "ATTRIBUTE";
         case XMLEvent.DTD:
            return "DTD";
         case XMLEvent.CDATA:
            return "CDATA";
      }
      return "UNKNOWN_EVENT_TYPE";
   }

   static void debugXMLEventType( XMLStreamReader xmlr, int event_type )
   {
      debug("Event " + getXMLEventTypeAsString(event_type) + "[" + event_type + "]/"
            + ( xmlr.hasName() ? xmlr.getLocalName() : "-" ) + ":"
            + ( xmlr.hasText() ? xmlr.getText()      : "-" ) );
      debugXMLAttributes(xmlr);
   }

   private static void debugXMLAttributes( XMLStreamReader xmlr )
   {
      if( xmlr.isStartElement() )
      {
         int count = xmlr.getAttributeCount();
         for(int i=0; i<count; i++)
         {
            QName  attrname  = xmlr.getAttributeName(i) ;
            String prefix    = xmlr.getAttributePrefix(i) ;
            String namespace = xmlr.getAttributeNamespace(i) ;
            String type      = xmlr.getAttributeType(i) ;
            String value     = xmlr.getAttributeValue(i) ;
            debugXMLAttribute(attrname,prefix,namespace,type,value);
         }
      }
   }

   private static void debugXMLAttribute( QName name, String prefix, String namespace, String type, String value )
   {
      debug("  Attribute: "+name.getLocalPart()+" ["+name.toString()+"]" );
      debug("    ["+prefix+"/"+namespace+"/"+type+"]: "+value);
   }

   //=======================================================================
   //  - Debugging and output
   //=======================================================================

   /**
    * Print a text line to the standard output.
    */
   private static void out( String line )
   {
      System.out.println(line);
   }

   /**
    * Print a text line.
    */
   static void debug( String line )
   {
      debugString(line);
   }

   /**
    * Print a text preceded with a message.
    */
   static void debug( String message, String line )
   {
      debugString(message);
      debugString(line);
   }

   /**
    * Print contents of a buffer.
    */
   static void debug( Buffer buffer )
   {
      debugString("===================== Debugging buffer: ====================");
      debugBuffer(buffer);
      debugString("============================================================");
   }

   /**
    * Print contents of a buffer preceded with a message.
    */
   static void debug( String message, Buffer buffer )
   {
      debugString("===================== Debugging buffer: ====================");
      debugString(message);
      debugString("------------------------------------------------------------");
      debugBuffer(buffer);
      debugString("============================================================");
   }

   /**
    * Print contents of a AP record.
    */
   static void debug( Record record )
   {
      debugString("===================== Debugging record: ====================");
      debugRecord(record);
      debugString("============================================================");
   }

   /**
    * Print contents of a AP record preceded with a message.
    */
   static void debug( String message, Record record )
   {
      debugString("===================== Debugging record: ====================");
      debugString(message);
      debugString("------------------------------------------------------------");
      debugRecord(record);
      debugString("============================================================");
   }

   /**
    * Print contents of a record.
    */
   static void debug( FndAbstractRecord record )
   {
      debugString("===================== Debugging record: ====================");
      debugRecord(record);
      debugString("============================================================");
   }

   /**
    * Print contents of a record preceded with a message.
    */
   static void debug( String message, FndAbstractRecord record )
   {
      debugString("===================== Debugging record: ====================");
      debugString(message);
      debugString("------------------------------------------------------------");
      debugRecord(record);
      debugString("============================================================");
   }

   /**
    * Print contents of an array.
    */
   static void debug( FndAbstractArray array )
   {
      debugString("===================== Debugging array: =====================");
      debugArrayRecord(array);
      debugString("============================================================");
   }

   /**
    * Print contents of an array preceded with a message.
    */
   static void debug( String message, FndAbstractArray array )
   {
      debugString("===================== Debugging array: =====================");
      debugString(message);
      debugString("------------------------------------------------------------");
      debugArrayRecord(array);
      debugString("============================================================");
   }

   /**
    * Print contents of ClientPrf instance.
    */
   static void debug( ClientPrf clprf )
   {
      debugString("================== Debugging ClientPrf: ====================");
      debugClientPrf(clprf);
      debugString("============================================================");
   }

   /**
    * Print contents of ClientPrf instance preceded with a message.
    */
   static void debug( String message, ClientPrf clprf )
   {
      debugString("================== Debugging ClientPrf: ====================");
      debugString(message);
      debugString("------------------------------------------------------------");
      debugClientPrf(clprf);
      debugString("============================================================");
   }

   /**
    * Print contents of a Map.
    */
   static void debug( Map map )
   {
      debugString("==================== Debugging Map: ========================");
      debugMap(map);
      debugString("============================================================");
   }

   /**
    * Print contents of a Map instance preceded with a message.
    */
   static void debug( String message, Map map )
   {
      debugString("==================== Debugging Map: ========================");
      debugString(message);
      debugString("------------------------------------------------------------");
      debugMap(map);
      debugString("============================================================");
   }

   //-----------------------------------------------------------------------
   //  - atomic debug functions
   //-----------------------------------------------------------------------

   /**
    * Print a text line.
    */
   private static void debugString( String line )
   {
      if(console && DEBUG)
         FndDebug.debug(line);
      else if(console)
         out(line);
      else
         Util.debug(line);
   }

   /**
    * Print a Buffer.
    */
   private static void debugBuffer( Buffer buffer )
   {
      debugString( Buffers.listToString(buffer) );
      //debugString( listBufferToString(buffer) );
   }

   /**
    * Print an AP Record.
    */
   private static void debugRecord( Record record )
   {
      try
      {
         debugBuffer( record.formatToBuffer() );
      }
      catch( APException x )
      {
         debugString("Unable to convert Record to Buffer:\n"+Str.getStackTrace(x) );
      }
   }

   /**
    * Print a SF Record.
    */
   private static void debugRecord( FndAbstractRecord record )
   {
      if(console)
         FndDebug.debugRecord(record);
      else
         FndDebugUtil.debugRecord(record);
   }

   /**
    * Print a SF Record Array.
    */
   private static void debugArrayRecord( FndAbstractArray array )
   {
      FndDebug.debugArrayRecord(array);
      if(console)
         FndDebug.debugArrayRecord(array);
      else
         FndDebugUtil.debugArrayRecord(array);
   }

   /**
    * Print a ClientPrf instance.
    */
   private static void debugClientPrf( ClientPrf clprf )
   {
      debugString(" - USERID: "+clprf.userid);
      if( clprf.cp!=null )
      {
         debugString(" - CLIENT_PROFILE:");
         debugRecord(clprf.cp);
      }
      if( clprf.rec!=null )
      {
         debugString(" - RECORD:");
         debugRecord(clprf.rec);
      }
   }

   /**
    * Print a Map instance.
    */
   private static void debugMap( Map map )
   {
      debugString("\tkey -\tvalue");
      debugString("\t-----\t-----");
      Set set = map.entrySet();
      Iterator itr = set.iterator();
      while( itr.hasNext() )
      {
         Map.Entry entry = (Map.Entry)itr.next();
         Object key   = entry.getKey();
         Object value = entry.getValue();
         debugString("\t"+key+"-\t"+value);
      }
   }

   //==========================================================================
   //  Member methods
   //==========================================================================

   //=======================================================================
   //  Database transaction: the perform() method (stand-alone mode)
   //=======================================================================

   /**
    * Perform the database transaction defined in the 'requestbuf' and return the result in the same form
    */
   private ASPTransactionBuffer perform( ASPTransactionBuffer requestbuf ) throws FndException
   {
      requestbuf.clearStandardInfoMessages();

      addRequestHeader(requestbuf);

      if(TRACE) debug(" - Request buffer contents:", requestbuf.getBuffer() );

      ASPTransactionBuffer responsebuf = new ASPTransactionBuffer(null);
      responsebuf.construct( newBuffer(), newBufferFormatter() );

      String requeststr;
      String respstr;

      Buffer rbuf = requestbuf.getBuffer();
      Buffer buf;

      ConnectionPool.Slot con;
      if(RMI)
         con = EJBConnectionPool.get(ConnectionPool.PLSQLGTW, properties);
      else
         con = JAPConnectionPool.get(ConnectionPool.PLSQLGTW, properties);
      buf = con.perform(rbuf);
      con.release();

      responsebuf.construct(buf);

      if(TRACE) debug(" - Response buffer contents:", responsebuf.getBuffer() );

      ASPBuffer header = responsebuf.getResponseHeader();
      String status = header.getValue("STATUS");
      if ( "DONE".equals(status) )
         return responsebuf;
      else
      {
         FndException e = new FndException();

         Buffer fndbuf = header.getBuffer().getBuffer("FND_EXCEPTION",null);
         if( fndbuf == null )
            throw new FndException("FNDPROFILEUTILSEXC: Item 'FND_EXCEPTION' missing in the response buffer.");
         else
            try
            {
               e.load( fndbuf );
            }
            catch( Exception x )
            {
               throw new FndException(x);
            }
         return null;
      }
   }

   /**
    * Add a proper request header to transaction buffer
    */
   private void addRequestHeader( ASPTransactionBuffer requestbuf )
   {
      Buffer header = requestbuf.getBuffer().newInstance();

      header.addItem("APP.ID",        "id");
      header.addItem("APP.PASSWORD",  properties.getProperty("syssecret"));
      header.addItem("USER",          properties.getProperty("user_id"));
      header.addItem("LANGUAGE",      properties.getProperty("language"));
      header.addItem("ACCESS_POINT",  properties.getProperty("access_point"));
      header.addItem("SESSION",       "123456");

      Item item = header.newItem();
      item.setName("__APPHEADER");
      item.setValue(header);
      requestbuf.getBuffer().insertItem(item,0);
   }

   //=======================================================================
   //  Conversion of old profile format to the new one (stand-alone mode)
   //=======================================================================

   /**
    * Fill the 'map' with pairs key-value from 'keys' and 'aliases'.
    * Both 'keys' and 'aliases' are comma separate lists of values.
    * 'aliases' must not contain more values then 'keys', but can contain empty values.
    * 'keys' must not contain empty values.
    * Return true if 'keys' is empty or the first or last key is a star ('*'),
    * false otherwise.
    * 'aliases' must be empty if 'keys' is empty.
    */
   private boolean prepareMap( Map map, String keys, String aliases, boolean users ) throws FndException
   {
      if( Str.isEmpty(keys) )
      {
         if( !Str.isEmpty(aliases) )
            throw new FndException("FNDPROFILEUTILSALIASNOTEMPTY: The list '&1' must not be defined if '&2' is not defined.",users?"to_users":"to_sites",users?"from_users":"from_sites");
         return true;
      }

      boolean allkeys = false;
      String[] karr = (users ? keys.toUpperCase() : keys.toLowerCase() ).split(",",-1);
      String[] aarr = null;
      int lena = 0;
      int lenk = karr.length;
      int offset = 0;

      if( "*".equals(karr[0]) )
      {
         allkeys = true;
         offset  = 1;
         lenk--;
      }
      else if( "*".equals(karr[lenk-1] ) )
      {
         allkeys = true;
         lenk--;
      }

      if( !Str.isEmpty(aliases) )
      {
         aarr = (users ? aliases.toUpperCase() : aliases.toLowerCase() ).split(",",-1);
         lena = aarr.length;
      }

      if( lenk < lena )
         throw new FndException("FNDPROFILEUTILSTOOMANYALIASES: The list '&1' must not contain more values then '&2'.",users?"to_users":"to_sites",users?"from_users":"from_sites");

      for( int i=0; i<lenk; i++ )
      {
         String key = karr[i+offset];
         if( Str.isEmpty(key) )
            throw new FndException("FNDPROFILEUTILSEMPTYKEY: The list '&1' must not contain empty values.",users?"from_users":"from_sites");
         if( key.startsWith("/") || key.endsWith("/") )
            throw new FndException("FNDPROFILEUTILSINVALIDVAL: The value '&1' is invalid.",key);

         String alias = i<lena ? aarr[i] : key;
         if( Str.isEmpty(alias) )
            alias = key;
         map.put(key,alias);
      }
      return allkeys;
   }

   /**
    * Transform old profiles to a buffer structure suitable for the new profile concept and storage.
    * If 'all_users' is true, conversion will be done for all users having old profiles,
    * otherwise only users specified by the Map 'users' will be handled.
    * If 'all_sites' is true, conversion will be done for all sites defined in old profiles,
    * otherwise only sites specified by the Map 'sites' will be handled.
    * Maps 'users' and 'sites' can optionaly define mappings to diferent values.
    * If 'can_contain_slash' is true, a more complex algorithm requireing full scanning of 'sites'
    * is used for finding sites. All sites eventually containing slashes must be defined in 'sites'.
    * If 'all_users'/'all_sites' is true, the routine will complete the corresponding Map
    * with the missing values.
    */
   private ProfileBuffer fetchOldProfiles( Map users, boolean all_users, Map sites, boolean all_sites, boolean can_contain_slash ) throws FndException
   {
      if(DEBUG) debug("ProfileUtils.fetchOldProfiles()");//+userid+","+site+","+newuser+")");
      out("Fetching old profiles...");
      if(DEBUG) debug("Users:",users);
      if(DEBUG) debug("Sites:",sites);

      // prepate the SQL statement
      String sqlstmt = "SELECT FND_USER,URL,PROFILE FROM WEB_PROFILE";

      if( !all_users )
         sqlstmt = sqlstmt + " WHERE FND_USER IN ("+getInClause(users)+")";

      if( !all_sites )
         sqlstmt = sqlstmt + (all_users ? " WHERE" : " AND") +" SUBSTR(URL,2,INSTR(URL,'/',2)-2) IN ("+getInClause(sites)+")";

      sqlstmt = sqlstmt + " ORDER BY URL, FND_USER"; //for trimmed performance while parsing URL:s, because those can contain slashes

      // prepare and execute the command buffer
      BufferFormatter buf_frmt = newBufferFormatter();
      ASPCommand      curcmd   = (new ASPCommand(null)).construct(newBuffer());

      curcmd.mainbuf.clear();
      curcmd.mainbuf.addItem( "METHOD",     "Query"  );
      curcmd.mainbuf.addItem( "SELECT",     sqlstmt  );
      curcmd.mainbuf.addItem( "MAX_ROWS",   "1000000");
      curcmd.mainbuf.addItem( "SKIP_ROWS",  "0"      );
      curcmd.mainbuf.addItem( "META",       "ALL"    );
      curcmd.mainbuf.addItem( "COUNT_ROWS", "N"      );
      curcmd.mainbuf.addItem( "DATA",       curcmd.mainbuf.newInstance() );

      ASPTransactionBuffer trans = new ASPTransactionBuffer(null);
      trans.construct(newBuffer(), buf_frmt);
      trans.addCommand("CURRENT", curcmd);

      if(DEBUG) debug("ProfileUtils.fetchOldProfiles(): Retrive profiles from the database.");

      trans = perform(trans);

      // fetch the result and transform to new profile structure
      out("\nTransforming old profiles to the new structure...");
      ProfileBuffer oldprof = newProfileBuffer();

      Buffer resp = trans.getBuffer().getBuffer("CURRENT");
      for( int i=0; i<resp.countItems(); i++ )
      {
         Item item = resp.getItem(i);

         if( !"DATA".equals(item.getName()) )
            continue;

         if( !item.isCompound() )
            if(restrictive)
               throw new FndException("FNDPROFILEUTILSUNEXPITOPRF: Found unexpected or not compound item '&1'!", item.getName());
            else
            {
               if(TRACE) debug("Found unexpected or not compound item "+item.getName()+". Skipping...");
               continue;
            }

         Buffer b   = item.getBuffer();
         if(TRACE) debug("Parsing old profile sub-buffer #"+i+":", b);
         String usr = b.getString( "FND_USER" );
         String url = b.getString( "URL"      );
         String prf = b.getString( "PROFILE"  );
         if(DEBUG) debug("Sub-bufer #"+i+": FND_USER="+usr+", URL='"+url+"'");

         if( Str.isEmpty(usr) || Str.isEmpty(url) || Str.isEmpty(prf) )
            if(restrictive)
               throw new FndException("FNDPROFILEUTILSDATANULL: Missing profile data");
            else
            {
               if(TRACE) debug("Missing profile data. Skipping...");
               continue;
            }

         // extract site and page name from the URL, change if necessary
         AppUrl appurl = extractSite( url, sites, all_sites, can_contain_slash );
         String ctxroot = appurl.site_root;
         if(DEBUG) debug("   ctxroot='"+ctxroot+"'");
         if( Str.isEmpty(ctxroot) )
         {
            if(TRACE) debug("Cannot find context root for "+url+". Skipping...");
            continue;
         }
         url = appurl.rel_url;//url.substring( ctxroot.length()+2 );
         if(DEBUG) debug("   url='"+url+"'");

         // change user ID, if necessary
         usr = setUserId( usr, users, all_users );
         if(DEBUG) debug("   usr='"+usr+"'");
         if( Str.isEmpty(usr) )
         {
            if(TRACE) debug("Cannot find user ID. Skipping...");
            continue;
         }

         // skip the two top levels: user ID and site
         ProfileBuffer buf = (ProfileBuffer)findOrCreateNestedBuffer( oldprof,  usr     );
                       buf = (ProfileBuffer)findOrCreateNestedBuffer( buf,      ctxroot );

         // find a proper buffer level for known profile URL:s.
         // possible URL:s: #, My.Defaults, My.Links, xxx.page[defkey]
         int pos = url.indexOf(".page");

         if( "#".equals(url) ) // the global page profile
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Global");
         else if( "My.Links".equals(url) ) // special treatment of saved links
         {
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Global");
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Links");
         }
         else if( "My.Defaults".equals(url) )  // special treatment of default printers
         {
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Global");
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Defaults");
         }
         else if( pos>0 )  // "normal" pages, e.g. URL's like Xxx.page[key]
         {
            String key = url.substring(pos+5); // eventual 'def key'
            url = url.substring(0, pos);
            url = Str.replace(url, "/", ".");
            url = Str.replace(url, "common.scripts.", "fndweb.");
            if( url.indexOf(".")<0 )
               url = "fndweb."+url;
            if( !Str.isEmpty(key) )
               url = url + ":" + key;

            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Pages");
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, url);
         }
         else  // not recognized URL:s
            buf = (ProfileBuffer)findOrCreateNestedBuffer( buf, "Undefined");

         // Uncompress the content of the profile and
         // set to the found buffer. Set the resulting string
         // if not possible to uncompress.
         buf.allowFormatting();
         try
         {
            String str = Util.uncompress(prf);
            if(DEBUG) debug("The uncompressed serialized buffer:",str);
            buf_frmt.parse( str, buf );
            if(DEBUG) debug("The parsed buffer:",buf);
         }
         catch(IOException x)
         {
            buf.addItem(DATA_NOT_COMPRESSED, prf);
         }
         buf.formatted();
         // parse the profile
         parseProfileBuffer(url, buf);
         if(TRACE) debug("Old profile sub-buffer #"+i+"parsed.");
      }

      if(TRACE) debug(" - The parsed old profile buffer:", oldprof);

      return oldprof;
   }

   /**
    * Create a comma separated list of all keys in the 'map',
    * each value enclosed within single quotes.
    */
   private String getInClause( Map map )
   {
      AutoString out = new AutoString();
      boolean first = true;
      Iterator itr = map.keySet().iterator();
      while( itr.hasNext() )
      {
         if(first)
            first = false;
         else
            out.append(',');
         out.append("'",(String)itr.next(),"'");
      }
      return out.toString();
   }

   /**
    * Internal function used by extractSite() only
    *
    * Find site alias in the Map 'sites'. Return the alias if found or null
    * if 'mandatory' is false, throw an exception otherwise.
    */
   private String findSiteAlias( Map sites, String site, boolean mandatory ) throws FndException
   {
      String newsite = (String)sites.get(site);
      if( !Str.isEmpty(newsite) )
      {
         last_site       = site;     // performance trimming
         last_site_alias = newsite;
         return newsite;
      }
      if(mandatory)
         throw new FndException("FNDPROFILEUTILSNSITENULL: Found null value as alias for site '&1'",site);
      else
         return null;
   }

   /**
    * Internal function used by extractSite() only
    *
    * Extract site name from 'url' assuming that site name can not contain
    * slashes and the url is on form '/<site_name/xxx'
    */
   //private String extractSite( String url )
   private AppUrl extractSite( String url )
   {
      int pos = url.indexOf('/',1);
      if( url.charAt(0)!='/' || pos<2 )
         return null;
      AppUrl appurl = new AppUrl();
      appurl.site_root = url.substring(1,pos);
      appurl.rel_url   = url.substring(pos+1);
      return appurl;
   }

   private class AppUrl
   {
      private String site_root;
      private String rel_url;
   }

   /**
    * Return site name for given 'url' or null if the site name can not be found or is not allowed.
    * 'url' is matched against 'sites' and site alias is returned if match is found.
    * If no match is found and 'all_sites' is false, null is returned, otherwise the site
    * is extracted from the 'url' and added to the 'sites' Map.
    * If 'can_contain_slash' is true, a more performance consuming algorithm is choosen
    * which require all eventual sites containing slashes to be defined within 'sites' even if
    * 'all_sites' is true.
    * For better performance the implementation assumes sorting by URL during subsequent method calls.
    */
   //private String extractSite( String url, Map sites, boolean all_sites, boolean can_contain_slash ) throws FndException
   private AppUrl extractSite( String url, Map sites, boolean all_sites, boolean can_contain_slash ) throws FndException
   {
      if( Str.isEmpty(url) ) // 'url' can not be empty
         return null;

      // 1. Trimming: if the site doesn't change return it's alias
      //
      // - if site CAN contain slashes:
      //  2. Full scan the 'sites'
      //  3. Compare each site (='from_value') against 'url'
      //  4. Return the alias if found
      //  5. If not found and 'all_sites' is 'false' return null
      //  6. Extract site name (=context root) from 'url' assuming site doesn't contain slashes
      //
      // - if site can not contain slashes:
      //  2. Extract site name (=context root) from 'url'
      //  3. Search after alias (='to_value') for given site (='from_value')
      //  4. Return the alias if found
      //  5. If not found and 'all_sites' is 'false' return null
      //  6. (empty)
      //
      // 7. Add a new entry to 'sites' with 'from_value'='to_value'='site' and return 'site'

      AppUrl appurl = null;

      if( last_site!=null && url.startsWith("/"+last_site+"/") )
      {
         appurl = new AppUrl();
         appurl.site_root = last_site_alias;
         appurl.rel_url   = url.substring( last_site.length()+2 );
         return appurl;                                    // 1. Check if the site doesn't change and return it's alias if so
      }

      //String site = null;
      if(can_contain_slash)                                // - if site CAN contain slashes
      {
         Iterator itr = sites.keySet().iterator();
         while( itr.hasNext() )                            // 2. Full scan the 'sites'
         {
            String site = (String)itr.next();
            if( Str.isEmpty(site) )
               throw new FndException("FNDPROFILEUTILSSITENULL: Found empty value as site name");

            if( url.startsWith("/"+site+"/") )             // 3. Compare each site (='from_value') against 'url'
            {
               appurl = new AppUrl();
               appurl.site_root = findSiteAlias(sites,site,true);
               appurl.rel_url = url.substring( site.length()+2 );
               return appurl;                               // 4. Return the alias if found
            }
         }
         if(!all_sites)                                    // 5. If not found and 'all_sites' is 'false'...
            return null;                                   //    ...return null
         appurl = extractSite(url);                        // 6. Extract site name (=context root) from 'url' asuming it doesn't contain slashes
         if( appurl.site_root==null )                      //    -  'site' can not be empty
            return null;
      }
      else                                                 // - if site can not contain slashes
      {
         appurl = extractSite(url);                        // 2. Extract site name (=context root) from 'url'
         if( appurl.site_root==null )                      //    -  'site' can not be empty
            return null;

         String newsite = findSiteAlias(sites,appurl.site_root,false); // 3. Search after alias (='to_value') for given site (='from_value')
         if( newsite!=null )
         {
            appurl.site_root = newsite;
            return appurl;                                 // 4. Return the alias if found
         }
         if(!all_sites)                                    // 5. If not found and 'all_sites' is 'false'...
            return null;                                   //    ...return null
      }                                                    // 6. (empty)

      sites.put(appurl.site_root, appurl.site_root);       // 7. Add a new entry to 'sites' ...
      last_site       = appurl.site_root;                  //    - (performance trimming)
      last_site_alias = appurl.site_root;
      return appurl;                                       //    ...and return 'site'
   }

   /**
    * Return user ID or null if invalid user.
    * If user found in 'users' the user alias is returned.
    * If 'all_users' is true, all users are valid, otherwise only those specified
    * in 'users' as Map keys..
    * If 'all_users' is true and the user is not found in 'users', the user is added
    * to 'users' with both values equal (from_value==to_value).
    */
   private String setUserId( String user, Map users, boolean all_users )
   {
      // 1. Try to find 'to_value' for given 'user' as 'from_value' and return it if found
      // 2. If not found and 'all_users' is 'false' return null
      // 3. Add a new entry to 'users' with 'from_value'='to_value'='user' and return 'user'

      if( Str.isEmpty(user) ) // 'user' can not be empty
         return null;

      String newuser = (String)users.get(user); // 1. Try to find 'to_value' for given 'user'...
      if( !Str.isEmpty(newuser) )
         return newuser;                        //    ...and return it if found
      else if(!all_users)                       // 2. If not found and 'all_users' is 'false'...
         return null;                           //    ...return null

      users.put(user, user);                    // 3. Add a new entry to 'users' ...
      return user;                              //    ...and return 'user'
   }

   //-----------------------------------------------------------------------
   //  parsing a single profiles (stand-alone mode)
   //-----------------------------------------------------------------------

   /**
    *
    */
   private void parseProfileBuffer( String url, ProfileBuffer buffer ) throws FndException
   {
      if(DEBUG)
      {
         debug("ProfileUtils.parseProfileBuffer("+url+"):\n - Before:");
         debug(buffer);
      }           
      
      
      if( url.equals("My.Links") )
      {
         //get the from_sites to fix URLs in the repository
         String from_sites_ = properties.getProperty("from_sites"); 
         String [] from_sites_arr = null;
         if (!Str.isEmpty(from_sites_))
           from_sites_arr = from_sites_.split(","); 
         // special treatment of saved links:
         // Use link description as ProfileEntry
         String data = buffer.getString(DATA_NOT_COMPRESSED);
         buffer.clear();

         //TODO: A temporary solution (JAPASE)?
         // Added Link number (variable 'j') to the 'Link' item.
         // Should be at least formatted - otherwise sorting doesn't work with number >9
         int j = 0;
         StringTokenizer pst = new StringTokenizer(data, "^");
         while( pst.hasMoreTokens() )
         {
            String link = pst.nextToken();
            int pos = link.indexOf('~');

            String desc = pos>0 ? link.substring(0,pos) : link;
            String val  = pos>0 ? link.substring(pos+1) : link;           
                      
            if (from_sites_arr!=null && from_sites_arr.length>0){ 
             for (int k=0; k<from_sites_arr.length; k++)
             {  
               String from_site = from_sites_arr[k];
               
               int sitepos   = val.indexOf("/"+from_site+"/");
               if (sitepos > 0 )
               {
                  sitepos=sitepos+from_site.length()+1;
                  if (sitepos<val.length()) //make sure to avoid IndexOutOfBoundsException
                  {    
                    val = BASE_URL + val.substring(sitepos,val.length());                                                                        
                    break;  //there can be only one match per URL string
                  }
               }               
             }
            }                    

            // find or create item resolving duplicates
            ProfileItem item;
            int i = 0;
            do
            {
               String newdesc = i>0 ? desc + "_" + (new NumberFormatter(DataFormatter.INTEGER,"000")).format(new Integer(i)) : desc;               
               item = (ProfileItem)findOrCreateNestedItem(buffer, "Link_"+j+ENTRY_SEP+newdesc);
               i++;
            }
            while( item.getValue()!=null );
            item.setValue(val);
            item.setState(ProfileItem.QUERIED);
            j++;
         }
      }
      else if( url.equals("My.Defaults") )
      {
         // special treatment of default printers
         String data = buffer.getString(DATA_NOT_COMPRESSED);
         buffer.clear();

         int cnt = 0;
         StringTokenizer pst = new StringTokenizer(data, "^");
         while( pst.hasMoreTokens() )
         {
            String def = pst.nextToken();
            int    pos = def.indexOf('~');

            String desc = pos>0 ? def.substring(0,pos) : "UNDEFINED";
            String val  = pos>0 ? def.substring(pos+1) : def;

            ProfileItem item;
            if( "DEFPRINTER".equals(desc) )
               item = (ProfileItem)findOrCreateNestedItem(buffer, "Printer"+ENTRY_SEP+"Default");
            else if( "DEFLANGUAGE".equals(desc) )
               item = (ProfileItem)findOrCreateNestedItem(buffer, "Language"+ENTRY_SEP+"Default");
            else
               item = (ProfileItem)findOrCreateNestedItem(buffer, desc+ENTRY_SEP+(new NumberFormatter(DataFormatter.INTEGER,"000")).format(new Integer(++cnt)) );

            item.setValue(val);
            item.setState(ProfileItem.QUERIED);
         }
      }
      else
      {
         // parsing "normal" profiles. Should start with two compound levels:
         // one for class name and the second one for the instance name
         int bufsize = buffer.countItems(); // size can be changed during execution; CustomValues can be added
         for( int i=0; i<bufsize; i++ )
         {
            Item item = buffer.getItem(i);
            if( !item.isCompound() )
            if(restrictive)
               throw new FndException("FNDPROFILEUTILSUNEXPNCOMPIT: Found unexpected non-compound item '&1'!", item.getName());
            else
            {
               if(TRACE) debug("Found unexpected non-compound item '"+item.getName()+"'. Skipping...");
               continue;
            }

            String clsname = item.getName();
            if( "Defaults".equals(clsname) || "Links".equals(clsname) )
            {
               if(TRACE) debug("Found Already converted item '"+clsname+"'. Skipping...");
               continue;
            }

            item.setName( convertClassName(clsname) );
            Buffer buf = item.getBuffer();

            Item it = null;
            int size = buf.countItems();
            for( int j=0; j<size; j++ )
            {
               it = buf.getItem(j);
               if( !it.isCompound() )
               {
                  deserializeData(clsname, it);
                  if(DEBUG)
                  {
                     debug("ProfileUtils.parseProfileBuffer() - After deserialization of "+clsname+":");
                     debug(buffer);
                  }
               }
               else
                  if(DEBUG) debug("Found compound item '"+it.getName()+"'. De-serialization not necessary...");
            }

            if( size==1 && "ifs.fnd.asp.ASPPage".equals(clsname) )  // skip one level for ASPPage
            {
               //TODO: Comments (JAPASE):
               // Dangerous to remove an Item in a 'for' loop.
               // Introduced new version of ProfileBuffer.setItem()

               if( !it.isCompound() )
                  throw new FndException("FNDPROFILEUTILSUNEXPNCOMPPAGIT: Found unexpected non-compound item '&1' for ASPPage!'",it.getName());

               Buffer buf2 = it.getBuffer();
               Buffer globalbuf = null;
               for(int k=0; k<buf2.countItems(); k++)
               {
                  Item it2 = buf2.getItem(k);
                  String name = it2.getName();
                  if( "PortalViews".equals(name) )
                     buffer.setItem( it2, i); // move the item 2 levels up in the structure
                  else // found global variables
                  {
                     if(globalbuf==null)
                        globalbuf = findOrCreateNestedBuffer(buffer, "CustomValues");
                     globalbuf.addItem(it2);
                  }
               }
             }
         }
      }
      if(DEBUG)
      {
         debug("ProfileUtils.parseProfileBuffer() - After:");
         debug(buffer);
      }
   }

   /**
    *
    */
   private void deserializeData( String classname, Item item ) throws FndException
   {
      if(DEBUG) debug("ProfileUtils.deserializeData("+classname+")");

      ASPPoolElementProfile prof = null;

      if( "ifs.fnd.asp.ASPPage".equals(classname) )
         prof = new ASPPageProfile();
      else if( "ifs.fnd.asp.ASPTable".equals(classname) )
         prof = new ASPTableProfile();
      else if( "ifs.fnd.asp.ASPBlock".equals(classname) )
         prof = new BlockProfile();
      else if( "ifs.fnd.asp.ASPPortal".equals(classname) )
         prof = new ASPPortalProfile();
      else if( "ifs.fnd.asp.ASPBlockLayout".equals(classname) )
         prof = new BlockLayoutProfile();

      if( prof==null )
         throw new FndException("FNDPROFILEUTILSUNKNCLS: Unknown class '&1'. Don't know how to deserialize it.",classname);

      ProfileBuffer   buf  = newProfileBuffer();
      BufferFormatter frmt = newBufferFormatter();

      prof.parse( buf, frmt, item.getString() ); // set state to QUERIED for each nested Item
      if(DEBUG) debug("ProfileUtils.deserializeData()",buf);

      item.setValue(buf);
   }

   //=======================================================================
   //  Merging of profiles. Even used from the framework.
   //=======================================================================

   /**
    * Merge entries from a new sub ProfileBuffer to an existing ProfileBuffer.
    * Used both in stand-alone mode and from the framework.
    */
   static void mergeProfiles( ProfileBuffer profile, ProfileBuffer newprf, String name ) throws FndException
   {
      for( int i=0; i<newprf.countItems(); i++)
      {
         ProfileItem item  = (ProfileItem)newprf.getItem(i);
         String      iname = item.getName();
         String path;
         
         if(iname.indexOf(ENTRY_SEP) == 0)
            path  = Str.isEmpty(name) ? iname : name+iname;            
         else
            path  = Str.isEmpty(name) ? iname : name+"/"+iname;                    
         
         if( iname.indexOf(ENTRY_SEP) >= 0 )
            prfutils.addToProfile(profile, path, item);
         else if( item.isCompound() )
            mergeProfiles(profile, (ProfileBuffer)item.getBuffer(), path );
         else
            throw new FndException("FNDPROFILEUTILSLEAFERR: Unrecognized buffer item '&1'",path);
      }
      if(TRACE && Str.isEmpty(name)) debug(" - The merged profile buffer:", profile);
   }

   /**
    * Set or add a single ProfileItem (can be compound) to a specified ProfileBuffer.
    * The state of the ProfileItem has to be set or updated, if necessary.
    * Following combinations are supported:
    *
    * 1. The item doesn't exist in the buffer:
    *     - add the item and set state to NEW
    *     - item can be single or compound
    *
    * 2. The item already exixts and has state QUERIED:
    *     - both items have to be of the same type: simple or compound
    *     - if the new item has state REMOVED, marke the item as REMOVED
    *     - otherwise if values differ (buffers are not equal, if compound), update the value and set state to MODIFIED
    *
    * 3. The item already exixts and has state NEW:
    *     - both items have to be of the same type: simple or compound
    *     - if the new item has state REMOVED, remove the item
    *     - otherwise update the value if values are different (buffers are not equal, if compound)
    *
    * 4. The item already exixts and has state MODIFIED:
    *     - both items have to be of the same type: simple or compound
    *     - if the new item has state REMOVED, marke the item as REMOVED
    *     - otherwise update the value if values are different (buffers are not equal, if compound)
    *
    * 5. The item already exixts and has state REMOVED:
    *     - both items have to be of the same type: simple or compound
    *     - if values differ (buffers are not equal, if compound) and the new item is not REMOVED,
    *       update the value and set state to MODIFIED
    *
    */
   private void addToProfile( ProfileBuffer profile, String key, ProfileItem newitem ) throws FndException
   {
      ProfileItem olditem  = (ProfileItem)findOrCreateNestedItem(profile, key);

      Object  newval   = newitem.getValue();
      Object  oldval   = olditem.getValue();
      boolean newcomp  = newitem.isCompound();
      boolean oldcomp  = olditem.isCompound();
      char    newstate = newitem.getState();
      char    oldstate = olditem.getState();

      if( !olditem.isValueSet() )   // combination 1: create a new item with state NEW
         olditem.setValue(newval);  // the state is already NEW
      else                          // all other combinantions: item exists
      {
         // both items have to be of the same type: simple or compound
         if( newcomp!=oldcomp )
            throw new FndException("FNDPROFILEUTILSITEMSTYPE: A compound Profile Item cannot be merged with a simple one for key '&1'",key);

         switch(oldstate)
         {
            case ProfileItem.QUERIED:   // combinantion 2
            case ProfileItem.MODIFIED:  // combinantion 4
               if( newstate==ProfileItem.REMOVED )
                  olditem.setState(ProfileItem.REMOVED);
               else if( !olditem.valueEquals(newval) ){
                  olditem.setValue(newval);  // the state is already MODIFIED
                  olditem.setState(ProfileItem.MODIFIED); //RIRALK: Q&D to avoid UNIQUE_CONTSRAINT error in db.
               }
               break;
            case ProfileItem.NEW:       // combinantion 3
               if( newstate==ProfileItem.REMOVED )
                  profile.removeItem(key);
               else if( !olditem.valueEquals(newval) )
                  olditem.setValue(newval);  // the state is already NEW
               break;
            case ProfileItem.REMOVED:   // combinantion 5
               if( newstate!=ProfileItem.REMOVED && !olditem.valueEquals(newval) )
                  olditem.setValue(newval);  // the state is already MODIFIED
               break;
            default:
               throw new FndException("FNDPROFILEUTILSNOTSUP: The merge combination is not supported for key '&1'",key);
         }
      }
      
      if(oldval!=null)
      {
         if(oldval instanceof ProfileBuffer)
         {
            String classname = olditem.getBuffer().getString("ClassName","");
            if (classname.equals(""))
               classname = olditem.getBuffer().getString("CLASS_NAME","");
            
            //Renaming links inside the My Links portlet
            if(classname.equals("ifs.fnd.portlets.MyLinks"))
            {
               Item mylinkbuf = olditem.getBuffer().findItem("Profile/MYLINKSBUF");
               if(mylinkbuf ==null)
                  mylinkbuf = olditem.getBuffer().findItem("PROFILE/MYLINKSBUF");
                              
               if(mylinkbuf!=null)
               {
                  Buffer links = mylinkbuf.getBuffer();
                  for(int i=0; i<links.countItems(); i++)
                  {
                     links.getItem(i).setName("LINK_"+i);
                  }
               }             
            }
            
            //Renaming links inside the navigator menu (Links applied from My Links)
            Item linkItem = olditem.getBuffer().getItem("Links",null);
            
            if(linkItem!=null)
            {
               for(int j=0; j<linkItem.getBuffer().countItems(); j++)
               {
                  Buffer id_n = linkItem.getBuffer().getItem(j).getBuffer();
                  for(int k=0; k<id_n.countItems(); k++)
                  {
                     if("URL".equals(id_n.getItem(k).getName()))
                        id_n.getItem(k).setName("URL_"+k);
                  }
               }
            }
         }
      }
   }

   //=======================================================================
   //  Help classes for conversions between records and buffers
   //=======================================================================

   /**
    * An interface for uniform handling of typed records ClientProfileValueArray with ClientProfileValue
    * and un-typed RecordCollection with Record.
    */
   private interface CommonRecordWrapper
   {
      // entire array level
      public void    init( Object arr ) throws FndException;
      public void    reset();
      public boolean hasMoreElements();
      // element level
      public void    nextElement();
      public void    addElement();
      public void    remove();
      public boolean findElement( String section, String entry );
      // record status
      public void    setNew();
      public void    setQueried();
      public void    setRemoved();
      public boolean isChanged();
      // key attributes
      public String  getProfileSectionValue();
      public void    setProfileSectionValue( String section )  throws FndException;
      public String  getEntry();
      public void    setEntry( String entry ) throws FndException;
      // other attributes
      public String  getValue();
      public void    setValue( String value ) throws FndException;

      // changed storage format of binary date to XML
      public boolean binaryValueExists();
      public String  getBinaryValue();
      public void    setBinaryValue( String value ) throws FndException;

      public String  getCategory();
      public void    setCategory( String category ) throws FndException;
      public boolean isReadOnly();
      public void    setReadOnly( boolean readonly );
   }

   /**
    * Implementation of CommonRecordWrapper for handling of Java Access Providers
    * un-typed records: RecordCollection with Record.
    */
   private class APRecordWrapper implements CommonRecordWrapper
   {
      private RecordCollection array;
      private RecordIterator   iterator;
      private Record           current;

      //--------------------------------------------------------------------
      // entire array level
      //--------------------------------------------------------------------

      public void init( Object arr ) throws FndException
      {
         if( arr instanceof RecordCollection )
         {
            array    = (RecordCollection)arr;
            iterator = array.recordIterator();
         }
         else
            throw new FndException("FNDPROFILEUTILSUNSUPCLS1: Class '&1' is not supported by the JAP wrapper.", arr.getClass().getName());
      }

      public void reset()
      {
         array.clear();
      }

      public boolean hasMoreElements()
      {
         return iterator.hasNext();
      }

      //--------------------------------------------------------------------
      // element level
      //--------------------------------------------------------------------

      public void nextElement()
      {
         current = iterator.nextRecord();
      }

      public void addElement()
      {
         current = array.add();
         current.add("PROFILE_ID");
         //current.setQueried();
      }

      public void remove()
      {
         // not necessary to implement for AP
      }

      public boolean findElement( String section, String entry )
      {
         RecordIterator it = array.recordIterator();
         while( it.hasNext() )
         {
            Record rec = it.nextRecord();
            if( section.equals( rec.find("PROFILE_SECTION").getString() ) && entry.equals( rec.find("PROFILE_ENTRY").getString() ) )
            {
               current = rec;
               return true;
            }
         }
         return false;
      }

      //--------------------------------------------------------------------
      // record status
      //--------------------------------------------------------------------

      public void setNew()
      {
         current.setNew();
      }

      public void setQueried()
      {
         current.setQueried();
      }

      public void setRemoved()
      {
         current.setRemoved();
      }

      public boolean isChanged()
      {
         return current.getStatus() != RecordStatus.QUERIED;
      }

      //--------------------------------------------------------------------
      // key attributes
      //--------------------------------------------------------------------

      public String getProfileSectionValue()
      {
         return current.find("PROFILE_SECTION").getString();
      }

      public void setProfileSectionValue( String section )  throws FndException
      {
         current.add("PROFILE_SECTION", section);
      }

      public String getEntry()
      {
         return current.find("PROFILE_ENTRY").getString();
      }

      public void setEntry( String entry ) throws FndException
      {
         current.add("PROFILE_ENTRY", entry);
      }

      //--------------------------------------------------------------------
      // other attributes
      //--------------------------------------------------------------------

      public String getValue()
      {
         return current.find("PROFILE_VALUE").getString();
      }

      public void setValue( String value ) throws FndException
      {
         current.add("PROFILE_VALUE", value);
         current.add("PROFILE_BINARY_VALUE", (String)null);
      }

      public boolean binaryValueExists()
      {
         return current.find("PROFILE_BINARY_VALUE").hasValue();
      }

      public String getBinaryValue()
      {
         return current.find("PROFILE_BINARY_VALUE").getString();
      }

      public void setBinaryValue( String value ) throws FndException
      {
         current.add("PROFILE_BINARY_VALUE", value);
      }

      public String getCategory()
      {
         return current.find("CATEGORY").getString();
      }

      public void setCategory( String category ) throws FndException
      {
         current.add("CATEGORY", category);
      }

      public boolean isReadOnly()
      {
         try
         {
            RecordAttribute read_only = current.find("OVERRIDE_ALLOWED");
            if (read_only==null)
               return false;
            else
            return !read_only.getBoolean();
         }
         catch(Exception e)
         {
            return false;
         }
      }

      public void setReadOnly( boolean readonly )
      {
         current.add("OVERRIDE_ALLOWED", !readonly);
      }
   }

   /**
    * Implementation of CommonRecordWrapper for handling of Java Server Frameworks
    * typed records: ClientProfileValueArray with ClientProfileValue.
    */
   private class SFRecordWrapper implements CommonRecordWrapper
   {
      private ClientProfileValueArray array;
      private int                     position;
      private int                     size;
      private ClientProfileValue      current;

      //--------------------------------------------------------------------
      // entire array level
      //--------------------------------------------------------------------

      public void init( Object arr ) throws FndException
      {
         if( arr instanceof ClientProfileValueArray )
         {
            array    = (ClientProfileValueArray)arr;
            size     = array.size();

            // loop in opposite direction if using the remove() method
            //position = 0;
            position = size-1;
         }
         else
            throw new FndException("FNDPROFILEUTILSUNSUPCLS2: Class '&1' is not supported by the JSF wrapper.", arr.getClass().getName());
      }

      public void reset()
      {
         array.reset();
      }

      public boolean hasMoreElements()
      {
         //return position < size;
         return position >= 0;
      }

      //--------------------------------------------------------------------
      // element level
      //--------------------------------------------------------------------

      public void nextElement()
      {
         current = array.get(position);
         //position++;
         position--;
      }

      public void addElement()
      {
         current = new ClientProfileValue();
         array.add(current);
      }

      public void remove()
      {
         array.remove(position);
      }

      public boolean findElement( String section, String entry )
      {
         int len = array.size();
         for( int i=0; i<len; i++ )
         {
            ClientProfileValue pv = array.get(i);
            if( pv.profileSection.getValue().equals(section) && pv.profileEntry.getValue().equals(entry) )
            {
               current = pv;
               return true;
            }
         }
         return false;
      }

      //--------------------------------------------------------------------
      // record status
      //--------------------------------------------------------------------

      public void setNew()
      {
         current.setState(FndRecordState.NEW_RECORD);
      }

      public void setQueried()
      {
         current.setState(FndRecordState.QUERY_RECORD);
      }

      public void setRemoved()
      {
         current.setState(FndRecordState.REMOVED_RECORD );
      }

      public boolean isChanged()
      {
         return current.getState() != FndRecordState.QUERY_RECORD;
      }

      //--------------------------------------------------------------------
      // key attributes
      //--------------------------------------------------------------------

      public String getProfileSectionValue()
      {
         return current.profileSection.getValue();
      }

      public void setProfileSectionValue( String section ) throws FndException
      {
         try
         {
            current.profileSection.setValue(section);
         }
         catch( ApplicationException x )
         {
            throw new FndException(x);
         }
      }

      public String getEntry()
      {
         return current.profileEntry.getValue();
      }

      public void setEntry( String entry ) throws FndException
      {
         try
         {
            current.profileEntry.setValue(entry);
         }
         catch( ApplicationException x )
         {
            throw new FndException(x);
         }
      }

      //--------------------------------------------------------------------
      // other attributes
      //--------------------------------------------------------------------

      public String getValue()
      {
         return current.profileValue.getValue();
      }

      public void setValue( String value ) throws FndException
      {
         try
         {
            current.profileValue.setValue(value);
            current.profileBinaryValue.setNull();
         }
         catch( ApplicationException x )
         {
            throw new FndException(x);
         }
      }

      public boolean binaryValueExists()
      {
         return !( current.profileBinaryValue.isNull() );
      }

      public String getBinaryValue()
      {
         return current.profileBinaryValue.getValue();
      }

      public void setBinaryValue( String value ) throws FndException
      {
         try
         {
            current.profileBinaryValue.setValue(value);
         }
         catch( ApplicationException x )
         {
            throw new FndException(x);
         }
      }

      public String getCategory()
      {
         return current.category.getValue();
      }

      public void setCategory( String category ) throws FndException
      {
         try
         {
            current.category.setValue(category);
         }
         catch( ApplicationException x )
         {
            throw new FndException(x);
         }
      }

      public boolean isReadOnly()
      {
         Boolean bool = current.overrideAllowed.getValue();
         return bool!=null && !bool.booleanValue();
      }

      public void setReadOnly( boolean readonly )
      {
         current.overrideAllowed.setValue(!readonly);
      }
   }

   //=======================================================================
   //  Conversion from record array to buffer; used by load()
   //=======================================================================

   /**
    * Converts an array with profile values to a buffer structure for a given user.
    */
   private void profileValuesToBuffer( Object inarr, ProfileBuffer top, String userid, String prefix, boolean ownprf, String alt_context ) throws FndException
   {
      if(DEBUG) debug("ProfileUtils.profileValuesToBuffer()");
      CommonRecordWrapper recwrapper;

      if( inarr instanceof ClientProfileValueArray )
         recwrapper = new SFRecordWrapper();
      else if( inarr instanceof RecordCollection )
         recwrapper = new APRecordWrapper();
      else
         throw new FndException("FNDPROFILEUTILSARRTYPE: Unknown array type: &1", inarr.getClass().getName() );
      recwrapper.init(inarr);

      top = (ProfileBuffer)findOrCreateNestedBuffer(top, userid, true);

      while( recwrapper.hasMoreElements() )
      {
         recwrapper.nextElement();

         String key = recwrapper.getProfileSectionValue();
         if( !key.startsWith(prefix) && !key.startsWith(USER_COMMON_PROFILE_PREFIX) )
            throw new FndException("FNDPROFILEUTILSWREFIX: Profile section name '&1' doesn't start with expected prefix '&2'",key,prefix);
         if( key.indexOf(ENTRY_SEP)>=0 )
            throw new FndException("FNDPROFILEUTILSUNCHAR: Unallowed character '&1' in Profile Section name '&2'",""+ENTRY_SEP,key);

         // only nodes (=corresponding to array elements) can contain the ENTRY_SEP character.
         // String      path = key.substring( prefix.length() ) + ENTRY_SEP + recwrapper.getEntry();
         String path;
         if (key.startsWith(USER_COMMON_PROFILE_PREFIX))
            path =  alt_context + "/Global/" + key.substring(USER_COMMON_PROFILE_PREFIX.length()) + ENTRY_SEP + recwrapper.getEntry();
         else
            path = key.substring(USER_PROFILE_PREFIX.length()) + ENTRY_SEP + recwrapper.getEntry();

         ProfileItem item = (ProfileItem)findOrCreateNestedItem( top, path, true );

         if( recwrapper.binaryValueExists() )
         {
            ProfileBuffer buf = (ProfileBuffer)top.newInstance();
            if(DEBUG) debug("ProfileUtils.profileValuesToBuffer(): allowing formatting; key="+key+", path="+path);
            buf.allowFormatting();
            fromXML( recwrapper.getBinaryValue(), buf);
            buf.formatted();
            if(DEBUG) debug("ProfileUtils.profileValuesToBuffer(): formatted.");
            item.setValue(buf);
            
            if(ownprf)
            {
               int isOwnPos = buf.getItemPosition("IsOwn");
               if(isOwnPos <0)
                  isOwnPos = buf.getItemPosition("IS_OWN");
                              
               Object isOwnVal;
               if(isOwnPos !=-1){
                  isOwnVal = buf.getItem(isOwnPos).getValue();
                  if(isOwnVal != null && isOwnVal.equals("FALSE"))
                     item.setFromBase(true);
               }
               else
                  item.setFromBase(true);
            }
            else
            {
               item.setFromBase(true);
            }
         }
         else
            item.setValue( recwrapper.getValue() );

         item.setCategory( recwrapper.getCategory() );
         item.setReadOnly( recwrapper.isReadOnly() );
         item.setOwnProfile( ownprf );
         item.setState( ProfileItem.QUERIED );

         //recwrapper.remove(); //requires to loop in opposite direction
      }
      recwrapper.reset(); // alternative
   }

   //=======================================================================
   //  Conversion from buffer to record array; used by save()
   //=======================================================================

   /**
    * Version used by common profiles only (regional settings)
    */
   private boolean bufferToProfileValues( Object inarr, ProfileBuffer buffer, String userid, String rfc_lang ) throws FndException //returns true if there are differencies
   {
      String prefix = "";
      if(buffer.getItem(0).getName().equals(SELECTED_THEME))
         prefix = USER_COMMON_PROFILE_PREFIX;
      else
         prefix = USER_REGIONAL_SETTINGS_PREFIX+"/";
      
      return bufferToProfileValues(inarr, buffer, userid, prefix, null, true, rfc_lang);
   }

   /**
    * Converts profile buffer to an array of profile values for a given user.
    */
   private void bufferToProfileValues( Object inarr, ProfileBuffer buffer, String userid, String prefix, String path ) throws FndException
   {
      bufferToProfileValues(inarr, buffer, userid, prefix, path, false, null);
   }

   /**
    * Implementation.
    */
   private boolean bufferToProfileValues( Object inarr, ProfileBuffer buffer, String userid, String prefix, String path, boolean only_common, String rfc_lang ) throws FndException
   {
      CommonRecordWrapper recwrapper;

      if( inarr instanceof ClientProfileValueArray )
         recwrapper = new SFRecordWrapper();
      else if( inarr instanceof RecordCollection )
         recwrapper = new APRecordWrapper();
      else
         throw new FndException("FNDPROFILEUTILSARRTYPE2: Unknown array type: &1", inarr.getClass().getName() );
      recwrapper.init(inarr);

      if( userid!=null && !only_common )
      {
         Item item;
         try
         {
            item = buffer.getItem(userid);
         }
         catch( ItemNotFoundException ie )
         {
            item = null;
         }
         if( item==null || !item.isCompound() )
            return false;
         buffer = (ProfileBuffer)item.getBuffer();
         if( buffer==null )
            return false;
      }

      // Special handling of 'Culture'
      if(only_common && rfc_lang!=null)
      {
         if( !recwrapper.findElement(USER_REGIONAL_SETTINGS_PREFIX, "Culture") )
         {
            if(DEBUG) debug("Culture not found. Adding...");
            recwrapper.addElement();

            recwrapper.setProfileSectionValue( USER_REGIONAL_SETTINGS_PREFIX );
            recwrapper.setEntry( "Culture" );

            recwrapper.setReadOnly( false );
            recwrapper.setNew();
            recwrapper.setValue( rfc_lang );
         }
         else if(DEBUG) debug("Found Culture");
      }

      return bufferToProfileValues( recwrapper, buffer, userid, prefix, path, only_common );
   }

   /**
    * Converts profile buffer to an array of profile values for a given user using wrapper class.
    * Returns true if there are differencies and only_common is true
    */
   private boolean bufferToProfileValues( CommonRecordWrapper recwrapper, ProfileBuffer buffer, String userid, String prefix, String path, boolean only_common ) throws FndException
   {
      if(DEBUGEX) debug("ProfileUtils.bufferToProfileValues()");
      boolean ret = false;

      for(int i=0; i<buffer.countItems(); i++)
      {
         ProfileItem item = (ProfileItem)buffer.getItem(i);
         String      name = item.getName();
         int         pos  = name.indexOf(ENTRY_SEP);

         if( "Regional Settings".equals(name) && !only_common )
         {
            if( item.isCompound() )
               common_profile = (ProfileBuffer)item.getBuffer();
            else
               common_profile = null;
            continue;
         }
         else if( SELECTED_THEME.equals(name) && !only_common )
         {
            if( item.isCompound() )
               theme_profile = (ProfileBuffer)item.getBuffer();
            else
            {
               theme_profile = new ProfileBuffer();
               theme_profile.addItem(item);
            }
            continue;
         }

         if(DEBUGEX) debug("Found Item '"+name+"' at path="+path);
         // only nodes (=corresponding to array elements) can contain the ENTRY_SEP character.
         if( pos>=0 )
         {
            if(DEBUGEX) debug(" - as a node");
            // the node
            if( item.isChanged() )
            {
               if( item.isReadOnly() )
               {
                  // TODO: read-only item, cannot be changed - notify the user,
                  throw new FndException("FNDPROFILEUTILSRO: The attribute '&1' is read-only and cannot be modified",prefix+(path==null?"":path));
               }
               else
               {
                  String section = (Str.isEmpty(path) ? prefix : prefix+path+"/") + name.substring(0, pos);
                  String entry   = name.substring(pos+1);
                  char   state   = item.getState();

                  if(only_common)
                  {
                     boolean found = recwrapper.findElement(section, entry);
                     if( !found && state==ProfileItem.REMOVED )
                        continue;
                     else if( !found )
                     {
                        recwrapper.addElement();

                        recwrapper.setProfileSectionValue( section );
                        recwrapper.setEntry( entry );

                        recwrapper.setCategory( item.getCategory() );
                        recwrapper.setReadOnly( false );
                        recwrapper.setNew();

                        if( item.isCompound() )
                        {
                           ProfileBuffer buf = (ProfileBuffer)item.getBuffer();
                           buf.allowFormatting();
                           recwrapper.setBinaryValue( toXML(buf) );
                           buf.formatted();
                        }
                        else
                           recwrapper.setValue(item.getString());

                        ret = true;
                     }
                     else if( state==ProfileItem.REMOVED )
                     {
                        recwrapper.setRemoved();
                        ret = true;
                     }
                     else
                     {
                        recwrapper.setQueried();

                        if( item.isCompound() )
                        {
                           ProfileBuffer buf = (ProfileBuffer)item.getBuffer();
                           buf.allowFormatting();
                           recwrapper.setBinaryValue( toXML(buf) );
                           buf.formatted();
                        }
                        else
                           recwrapper.setValue(item.getString());

                        ret = recwrapper.isChanged();
                     }
                  }
                  else
                  {
                     boolean found = recwrapper.findElement(section, entry);

                     if( !found )
                     {
                        recwrapper.addElement();

                        recwrapper.setProfileSectionValue( section );
                        recwrapper.setEntry( entry );

                        recwrapper.setCategory( item.getCategory() );
                        recwrapper.setReadOnly( false );


                        if( !item.isOwnProfile() )
                        {
                           if(state == ProfileItem.REMOVED && !item.isRemoveFlag())
                           { 
                              int itemPos = item.getBuffer().getItemPosition("Position");
                              boolean is_cap= false; //To determine whether item name is uppercase or not.
                              if(itemPos == -1){
                                 is_cap= true;
                                 itemPos = item.getBuffer().getItemPosition("POSITION");
                              }

                              if(itemPos != -1){
                                 if(is_cap)
                                 itemPos = item.getBuffer().setItem("POSITION", -2);
                                 else
                                    itemPos = item.getBuffer().setItem("Position", -2);                                 
                              }
                              
                              if(item.getBuffer().findItem("IsOwn") !=null)
                                 item.getBuffer().setItem("IsOwn","FALSE");
                              else
                                 item.getBuffer().setItem("IS_OWN","FALSE");
                              
                           }

                           if(item.isCompound())
                           {
                              int isOwnPos = item.getBuffer().getItemPosition("IsOwn");
                              boolean is_cap = false;
                              if(isOwnPos ==-1){
                                 is_cap =true;
                                 isOwnPos = item.getBuffer().getItemPosition("IS_OWN");
                              }
                              if(isOwnPos != -1){
                                 if(is_cap)
                                 isOwnPos = item.getBuffer().setItem("IS_OWN","FALSE");
                                 else
                                 isOwnPos = item.getBuffer().setItem("IsOwn","FALSE");    
                              }
                           }

                           recwrapper.setNew();
                           state = ProfileItem.NEW;
                           // Added to correct Bug 61623
                           item.setOwnProfile(true);
                           item.setFromBase(true);
                        }

                        else if( state==ProfileItem.MODIFIED )
                           recwrapper.setQueried();
                        else if( state==ProfileItem.REMOVED ){
                           if(item.isFromBase() && !item.isRemoveFlag())
                           {
                              int itemPos = item.getBuffer().getItemPosition("Position");
                              boolean is_cap= false;
                              if(itemPos == -1){
                                 is_cap= true;
                                 itemPos = item.getBuffer().getItemPosition("POSITION");
                              }
                              if(itemPos != -1){
                                 if(is_cap)
                                 itemPos = item.getBuffer().setItem("POSITION", -2);
                                 else
                                    itemPos = item.getBuffer().setItem("Position", -2);                                 
                              }
                              recwrapper.setQueried();
                           }
                           else   
                              recwrapper.setRemoved();
                        }
                     }
                     else if( state==ProfileItem.MODIFIED )
                        recwrapper.setQueried();
                     else if( state==ProfileItem.REMOVED ){
                        if(item.isFromBase() && !item.isRemoveFlag())
                        {
                           int itemPos = item.getBuffer().getItemPosition("Position");
                           boolean is_cap= false;
                           if(itemPos == -1){
                              is_cap= true;
                              itemPos = item.getBuffer().getItemPosition("POSITION");
                           }
                           if(itemPos != -1){
                              if(is_cap)
                              itemPos = item.getBuffer().setItem("POSITION", -2);
                              else
                                 itemPos = item.getBuffer().setItem("Position", -2);                                 
                           }
                           recwrapper.setQueried();
                        }
                        else   
                           recwrapper.setRemoved();
                     }

                     if( item.isCompound() )
                     {
                        ProfileBuffer buf = (ProfileBuffer)item.getBuffer();
                        buf.allowFormatting();
                        recwrapper.setBinaryValue( toXML(buf) );
                        buf.formatted();
                     }
                     else
                        recwrapper.setValue(item.getString());
                  }
                  if(TRACE) debug(" - "+state+":"+section+ENTRY_SEP+entry);
               }
            }
         }
         else if( item.isCompound() )  // otherwise the item HAS to be compound
         {
            if(DEBUGEX) debug(" - a compound Item: nested call");
            String key = Str.isEmpty(path) ? name : path+"/"+name;
            boolean changed = bufferToProfileValues( recwrapper, (ProfileBuffer)item.getBuffer(), null, prefix, key, only_common );
            ret = changed ? changed : ret;
         }
         else
            throw new FndException("FNDPROFILEUTILSNONORE: Item '&1' is not a node!", (Str.isEmpty(path) ? prefix : prefix+path+"/")+name );
      }
      return ret;
   }

   /**
    *
    */
   private void resetBuffer( ProfileBuffer buffer )
   {
      buffer.allowFormatting();
      for(int i=0; i<buffer.countItems(); i++)
      {
         ProfileItem item = (ProfileItem)buffer.getItem(i);
      }
      buffer.formatted();
   }

   //=======================================================================
   //  Load profiles from the database
   //=======================================================================

   /**
    * Loads a profile sub tree from the database for the chosen user using RMI
    */
   private ClientPrf loadRMI( ProfileBuffer prf, String path, String userid ) throws FndException
   {
      return loadRMI(prf, path, userid, false);
   }

   /**
    * Loads only common profile, i.e. Regional Settings, for the chosen user using RMI
    */
   private ClientPrf loadCommonProfileRMI( String userid ) throws FndException
   {
      return loadRMI(null, USER_REGIONAL_SETTINGS_PREFIX+"%", userid, true); // Even 'Culture' has to be fetched, not only nested records
   }

   private ClientPrf loadThemeProfileRMI( String userid ) throws FndException
   {
      return loadRMI(null, USER_THEME_SETTINGS_PREFIX, userid, true);
   }

   /**
    * Implementation of loadRMI() and loadCommonProfileRMI()
    */
   private ClientPrf loadRMI( ProfileBuffer prf, String path, String userid, boolean only_common ) throws FndException
   {
      ClientProfileValue value = new ClientProfileValue();
      String alt_context = null;//Only required when reading Regional Setting from the profile.

      if( only_common )
         value.setCondition(value.profileSection.createLikeCondition(path));
      else
      {
         value.setCondition(value.profileSection.createLikeCondition( path + (path.charAt(path.length()-1)=='/'?"%":"/%") ));
         if( path.indexOf("/Global")>0 )
         {
           value.addCondition(value.profileSection.createLikeCondition(USER_REGIONAL_SETTINGS_PREFIX+"/%" ),FndConditionOperator.OR);
           value.addCondition(value.profileSection.createLikeCondition(USER_THEME_SETTINGS_PREFIX), FndConditionOperator.OR);
           alt_context = path.substring(path.indexOf(USER_PROFILE_PREFIX)+USER_PROFILE_PREFIX.length(),path.indexOf("/Global"));
         }
      }
      if(DEBUG) debug("ProfileUtils.loadRMI(): condition:", value);

      UserProfile uprof = null;

      ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.PROFILE_LOAD, properties );
      ManageUserProfileViews.V11 param =
         new ManageUserProfileViews.V11();

      try
      {
         param.userId.setValue(userid);
         param.useEncoding.setValue(false); //Set this to false when pure XML string handling is implemented.
      }
      catch( ApplicationException x )
      {
         throw new FndException(x);
      }
      param.clientProfileValueCondition.setRecord(value);
      UserProfileArray arr = (UserProfileArray)con.invoke(param);

      int size = arr.size();
      if( size!=1 ) throw new FndException("FNDPROFILEUTILSTOOMANYUPRF: Unexpected number of User Profiles: &1",size+"");
      uprof = arr.get(0);
      con.release();

      if(DEBUG) debug("ProfileUtils.loadRMI(): The fetched UserProfile instance:", uprof);

      ClientProfile cp = null;

      for(int i=0; i<uprof.clientProfiles.size(); i++)
      {
         ClientProfile c = uprof.clientProfiles.get(i);
         if(DEBUG) debug("ProfileUtils.loadRMI(): The fetched ClientProfile instance:", c);

         String uid = c.owner.getValue();
         if( userid.equals(uid) )
            cp = c;
         else if( !Str.isEmpty(uid) )
            if(restrictive)
               throw new FndException("FNDPROFILEUTILSWRONGUID: Unexpected User ID: &1",uid);
            else
            {
               debug("  - found unexpected User ID: "+uid+". Skipping...");
               continue;
            }

         // Convert the record array to buffer
         if( !only_common )
            profileValuesToBuffer(c.values, prf, userid, path, cp==c, alt_context);
      }
      return new ClientPrf(cp);
   }

   /**
    * Loads a profile sub tree from the database for the chosen user using JAP.
    */
   private ClientPrf loadJAP( ProfileBuffer prf, String path, String userid ) throws FndException
   {
      return loadJAP(prf, path, userid, false);
   }

   /**
    * Loads only common profile, i.e. Regional Settings, for the chosen user using JAP.
    */
   private ClientPrf loadCommonProfileJAP( String userid ) throws FndException
   {
      return loadJAP(null, USER_REGIONAL_SETTINGS_PREFIX+"%", userid, true);
   }

   private ClientPrf loadThemeProfileJAP( String userid ) throws FndException
   {
      return loadJAP(null, USER_THEME_SETTINGS_PREFIX, userid, true);
   }

   /**
    * Implementation of loadJAP() and loadCommonProfileJAP()
    */
   private ClientPrf loadJAP( ProfileBuffer prf, String path, String userid, boolean only_common ) throws FndException
   {
      
      Record request_rec1,  request_rec2, request_rec3;
      Record response_rec1, response_rec2, response_rec3;
      String alt_context = null;//Only required when reading Regional Setting from the profile.

      response_rec2 = null;
      response_rec3 = null;

      // Fetch user profile OR Regional Settings only if 'only_common'
      request_rec1 = new Record("MANAGEUSERPROFILE_LOADUSERPROFILE");
      request_rec1.add("USER_ID",userid);
      request_rec1.add("USE_ENCODING", false); //Set this to false when pure XML string handling is implemented.
      Record valcond1 = request_rec1.addAggregate("CLIENT_PROFILE_VALUE_CONDITION","CLIENT_PROFILE_VALUE");
      if( only_common )
         valcond1.setLikeCondition("PROFILE_SECTION" ,path); // Even 'Culture' has to be fetched, not only nested records
      else
         valcond1.setLikeCondition("PROFILE_SECTION" ,path + (path.charAt(path.length()-1)=='/'?"%":"/%") );
      if(DEBUG) debug("ProfileUtils.loadJAP(): 1 st request record:", request_rec1 );

      if(DEBUG) debug("ProfileUtils.loadJAP(): retrieving connection...");
      ConnectionPool.Slot con = JAPConnectionPool.get( ConnectionPool.PROFILE_LOAD, properties );
      if(DEBUG) debug("ProfileUtils.loadJAP(): invoking 1:st request...");
      response_rec1 = (Record)con.invoke(request_rec1);

      if( !only_common && path.indexOf("/Global")>0 )
      {  // Fetch also Regional Settings if fetching global profile
         request_rec2 = new Record("MANAGEUSERPROFILE_LOADUSERPROFILE");
         request_rec2.add("USER_ID",userid);
         request_rec2.add("USE_ENCODING", false); //Set this to false when pure XML string handling is implemented.
         Record valcond2 = request_rec2.addAggregate("CLIENT_PROFILE_VALUE_CONDITION","CLIENT_PROFILE_VALUE");
         valcond2.setLikeCondition("PROFILE_SECTION" ,USER_REGIONAL_SETTINGS_PREFIX+"/%");
         
         if(DEBUG) debug("ProfileUtils.loadJAP(): 2 nd request record:", request_rec2 );
         if(DEBUG) debug("ProfileUtils.loadJAP(): invoking 2:nd request...");
         response_rec2 = (Record)con.invoke(request_rec2);
         
         request_rec3 = new Record("MANAGEUSERPROFILE_LOADUSERPROFILE");
         request_rec3.add("USER_ID",userid);
         request_rec3.add("USE_ENCODING", false); //Set this to false when pure XML string handling is implemented.
         Record valcond3 = request_rec3.addAggregate("CLIENT_PROFILE_VALUE_CONDITION","CLIENT_PROFILE_VALUE");
         valcond3.setLikeCondition("PROFILE_SECTION" ,USER_THEME_SETTINGS_PREFIX);
         response_rec3 = (Record)con.invoke(request_rec3);
         
         alt_context = path.substring(path.indexOf(USER_PROFILE_PREFIX)+USER_PROFILE_PREFIX.length(),path.indexOf("/Global"));
      }
      con.release();

      RecordAttribute  ra, ra2, ra3 = null;
      RecordCollection rc, rc2, rc3 = null;
      if(DEBUG) debug("ProfileUtils.loadJAP(): result record(1):", response_rec1 );
      rc2 = null;
      rc3 = null;

      ra = response_rec1.find("__RESULT");
      rc = ra.getArray();
      int size = rc.size();
      if( size!=1 ) throw new FndException("FNDPROFILEUTILSTOOMANYREC: Unexpected number of Records: &1",size+"");
      response_rec1 = rc.get(0);

      if(TRACE) debug("ProfileUtils.loadJAP(): result record:", response_rec1 );

      ra = response_rec1.find("CLIENT_PROFILES");
      rc = ra.getArray();
      if( response_rec2!= null )
      {
         ra2 = response_rec2.find("__RESULT");
         rc2 = ra2.getArray();
         if( rc2.size()!=1 ) throw new FndException("FNDPROFILEUTILSTOOMANYREC: Unexpected number of Records: &1",size+"");
         response_rec2 = rc2.get(0);

         ra2 = response_rec2.find("CLIENT_PROFILES");
         rc2 = ra2.getArray();
      }

      if( response_rec3!= null )
      {
         ra3 = response_rec3.find("__RESULT");
         rc3 = ra3.getArray();
         if( rc3.size()!=1 ) throw new FndException("FNDPROFILEUTILSTOOMANYREC: Unexpected number of Records: &1",size+"");
         response_rec3 = rc3.get(0);

         ra3 = response_rec3.find("CLIENT_PROFILES");
         rc3 = ra3.getArray();
      }

      // Merge together both results
      if( rc2!=null )
      {
         size = rc2.size();
         for( int k=0; k<size; k++ )
            rc.add(rc2.get(k));
      }

      if( rc3!=null )
      {
         size = rc3.size();
         for( int k=0; k<size; k++ )
            rc.add(rc3.get(k));
      }
      
      Record cp = null;
      size = rc.size();
      for( int i=0; i<size; i++ )
      {
         Record c = rc.get(i);
         if(DEBUG) debug("ProfileUtils.loadJAP(): The fetched ClientProfile instance:", c);

         String uid = c.find("OWNER").getString();
         if( userid.equals(uid) )
            cp = c;
         else if( !Str.isEmpty(uid) )
            throw new FndException("FNDPROFILEUTILSWRONGUID2: Unexpected User ID: &1",uid);

         // Convert the record array to buffer
         if( !only_common )
         {
            RecordAttribute values = c.find("VALUES");
            if( values!=null )
               profileValuesToBuffer(values.getArray(), prf, userid, path, cp==c, alt_context);
         }
      }
      return new ClientPrf(cp);
   }

   /**
    * Loads a profile sub tree from the database and converst to a Buffer structure
    * Parameters: prf - an empty buffer, path - points out the sub tree; has to be trailed with a slash,
    * userid - mandatory, the used ID for whom the profile has to be exported,
    * myprofile - if true, the profile for the current user is fetched; userid must be the same as the current user then.
    * Return: an instance of of ClientProfile to be reused on save
    * Calls ManageMyUserProfile.LoadMyUserProfile if the argument 'myprofile' is set
    * or AdministrateUserProfiles.LoadUserProfile otherwise
    *
    */
   private ClientPrf loadProfile( ProfileBuffer prf, String path, String userid ) throws FndException
   {
      if( Str.isEmpty(userid) )
         throw new FndException("FNDPROFILEUTILSEMPTYUSER: User ID is mandatory!");

      //if (!path.endsWith("/")) path = path +"/";

      ClientPrf cp;
      if(RMI)
         cp = loadRMI(prf, path, userid );
      else
         cp = loadJAP(prf, path, userid );
      //Q&D RIRALK
      //prf.clearDirty();
      prf.reset();
      //----------
      if(TRACE) debug(" - Fetched profile buffer:", prf);
      return cp;
   }

   //=======================================================================
   //  Save profiles to the database
   //=======================================================================

   /**
    * Saves profile changes to the database for the chosen user using RMI.
    * User ID stored in the instance of ClientPrf.
    */
   private void saveRMI( ClientPrf clprf, ProfileBuffer profbuf ) throws FndException
   {
      saveRMI(clprf, profbuf, false, null);
   }

   /**
    * Saves changes done to the common profile, i.e. Regional Settings, using RMI.
   * User ID stored in the instance of ClientPrf.
    */
   private void saveCommonProfileRMI( ClientPrf clprf, ProfileBuffer profbuf, String rfc_lang ) throws FndException
   {
      saveRMI(clprf, profbuf, true, rfc_lang);
   }

   private void saveThemeProfileRMI( ClientPrf clprf, ProfileBuffer profbuf ) throws FndException
   {
      saveRMI(clprf, profbuf, true, null);
   }
   
   /**
    * Implementation of saveRMI() and saveCommonProfileRMI()
    */
   private void saveRMI( ClientPrf clprf, ProfileBuffer profbuf, boolean only_common, String rfc_lang ) throws FndException
   {
      ClientProfile cp = clprf.getClientProfile();
      if( cp==null )
      {
         if(DEBUG) debug("ProfileUtils.saveRMI(): ClientProfile instance is null! Aborting.");
         throw new FndException("FNDPROFILEUTILSCPNULL: The instance of ClientProfile is null while saving profile!");
      }
      UserProfile profile = new UserProfile();

      String userid = clprf.getUserId();

      // convert buffer to a record array
      if(TRACE) debug("ProfileUtils.saveRMI(): rows to be saved:");
      if( only_common )
      {
         if( !bufferToProfileValues(cp.values, profbuf, userid, rfc_lang) ) //returns true if there are differencies
            return;
      }
      else
         bufferToProfileValues(cp.values, profbuf, userid, USER_PROFILE_PREFIX, null);

      profile.clientProfiles.add(cp);
      ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.PROFILE_SAVE, properties );
      ManageUserProfileViews.V12 param =
            new ManageUserProfileViews.V12();
      try
      {
         param.userId.setValue(userid);
         param.useEncoding.setValue(false); //Set this to false when pure XML string handling is implemented.
      }
      catch( ApplicationException x )
      {
         throw new FndException(x);
      }
      param.userProfile.setRecord(profile);
      if(TRACE) debug(" - Ready to save UserProfile instance:", param);
      if(testonly)
         debug("   Warning: 'testonly' enabled: changes not saved!");
      else
         con.invoke(param);

      con.release();
   }

   /**
    * Saves profile changes to the database for the chosen user using JAP.
    * User ID stored in the instance of ClientPrf.
    */
   private void saveJAP( ClientPrf clprf, ProfileBuffer profbuf ) throws FndException
   {
      saveJAP(clprf, profbuf, false, null);
   }

   /**
    * Saves changes done to the common profile, i.e. Regional Settings, using JAP.
    * User ID stored in the instance of ClientPrf.
    */
   private void saveCommonProfileJAP( ClientPrf clprf, ProfileBuffer profbuf, String rfc_lang ) throws FndException
   {
      saveJAP(clprf, profbuf, true, rfc_lang);
   }

   private void saveThemeProfileJAP( ClientPrf clprf, ProfileBuffer profbuf ) throws FndException
   {
      saveJAP(clprf, profbuf, true, null);
   }
   
   /**
    * Implementation of saveJAP() and saveCommonProfileJAP()
    */
   private void saveJAP( ClientPrf clprf, ProfileBuffer profbuf, boolean only_common, String rfc_lang ) throws FndException
   {
      Record cp = clprf.getRecord();
      Record request_rec = new Record("MANAGEUSERPROFILE.USER_PROFILE");
      request_rec.addArray("CLIENT_PROFILES").add(cp);
      RecordAttribute  ra = cp.find("VALUES");
      RecordCollection rc = ra!=null ? ra.getArray() : cp.addArray("VALUES");

      String userid = clprf.getUserId();

      // convert buffer to a record array
      if(DEBUG) debug("ProfileUtils.saveJAP(): rows to be saved:");
      if( only_common )
      {
         if( !bufferToProfileValues( rc, profbuf, userid, rfc_lang) ) //returns true if there are differencies
            return;
      }
      else
         bufferToProfileValues( rc, profbuf, userid, USER_PROFILE_PREFIX, null );

      Record rec = new Record("MANAGEUSERPROFILE_SAVEUSERPROFILE");
      rec.addAggregate("USER_PROFILE", request_rec);
      rec.add("USER_ID", userid);
      rec.add("USE_ENCODING", false); //Set this to false when pure XML string handling is implemented.
      request_rec = rec;

      ConnectionPool.Slot con = JAPConnectionPool.get( ConnectionPool.PROFILE_SAVE, properties );

      Record response_rec = null;
      if(TRACE) debug(" - Ready to save UserProfile instance:", request_rec);
      if(testonly)
         debug("   Warning: 'testonly' enabled: changes not saved!");
      else
        response_rec = (Record)con.invoke(request_rec);
      con.release();

      //refresh the record stored in instance of ClientPrf
      if( !only_common )
      {
         ra = response_rec.find("USER_PROFILE");
         ra = ra.getAggregate().find("CLIENT_PROFILES");
         rc = ra.getArray();

         int size = rc.size();
         for( int i=0; i<size; i++ )
         {
            Record c = rc.get(i);
            String uid = c.find("OWNER").getString();
            if( userid.equals(uid) )
               cp = c;
            else if( !Str.isEmpty(uid) )
               throw new FndException("FNDPROFILEUTILSWRONGUID2: Unexpected User ID: &1",uid);
         }
         clprf.setRecord(cp);
      }
   }

   /**
    * Saves a profile tree, given as a Buffer 'profbuf', to database.
    * Saves only the changed attributes and always to the personal profile.
    * Requires an unmodified instance of ClientProfile fetched by load() containing user ID.
    * myprofile - if true, the profile for the current user is stored; userid must be the same as the current user then.      --remove this line
    * Calls ManageMyUserProfile.SaveMyUserProfile if the argument 'myprofile' is set
    * or AdministrateUserProfiles.SaveUserProfile otherwise
    */
   private void saveProfile( ClientPrf cp, ProfileBuffer profbuf, String rfc_lang ) throws FndException
   {
      if(DEBUG) debug("ProfileUtils.saveProfile(): saving profile:",profbuf);
      if( cp==null )
      {
         if(DEBUG) debug("ProfileUtils.save(): ClientPrf instance is null! Aborting.");
         throw new FndException("FNDPROFILEUTILSCPNULL: The instance of ClientProfile is null while saving profile!");
      }

      common_profile = null;
      theme_profile  = null;

      if(RMI)
         saveRMI(cp, profbuf);
      else
         saveJAP(cp, profbuf);

      if(DEBUG) debug("Common profile:",common_profile);
      if( common_profile!=null && common_profile.isChanged() )
         saveCommonProfile(cp, rfc_lang);

      if(DEBUG) debug("Theme Profile:",theme_profile);
      if(theme_profile!=null && theme_profile.isChanged())
         saveThemeProfile(cp);
      
      // TODO: clear the cache here ?
      // JAPA: even remove marked items
      profbuf.reset();
      if(DEBUGEX) debug("Saved ProfileBuffer after resetting:",profbuf);
     // if( !console )
     //    ASPProfileCache.clear();
      //------
   }

   private void saveCommonProfile( ClientPrf clprf, String rfc_lang ) throws FndException
   {
      String    userid = clprf.getUserId();
      ClientPrf cp     = null;

      if(RMI)
      {
         cp = loadCommonProfileRMI(userid);
         saveCommonProfileRMI(cp, common_profile, rfc_lang); // saves only if differ
      }
      else
      {
         cp = loadCommonProfileJAP(userid);
         saveCommonProfileJAP(cp, common_profile, rfc_lang); // saves only if differ
      }
   }

   private void saveThemeProfile( ClientPrf clprf ) throws FndException
   {
      String userid = clprf.getUserId();
      ClientPrf cp = null;
      if(RMI)
      {
         cp = loadThemeProfileRMI(userid);
         saveThemeProfileRMI(cp, theme_profile);
      }
      else
      {
         cp = loadThemeProfileJAP(userid);
         saveThemeProfileJAP(cp, theme_profile);
      }
   }
   
   //=======================================================================
   //  Add samples - for test purpose (stand-alone mode)
   //=======================================================================

   private void addSamples( Buffer profile, String userid ) throws FndException
   {
      /*
      b2exx/Pages/Default/Portal/IfsPersonalPortal/NextId^Test=50,
      b2exx/Global/Page/PortalViews/Current^Test=IfsPersonalPortal,
      b2exx/Global/Page/PortalViews/Available/IfsPersonalPortal/Desc^Test=IFS Personal Portal,
      b2exy/Global/Page/PortalViews/Available^Test=VIEW1=IfsPersonalPortal;VIEW2=View 2
      */

      StringTokenizer st = new StringTokenizer(samples, ",");
      while( st.hasMoreTokens() )
      {
         String element = st.nextToken();
         int pos1 = element.indexOf(ENTRY_SEP);
         int pos2 = element.indexOf('=');

         if( pos1==-1 || pos1>pos2 )
            throw new FndException("FNDPROFILEUTILSSAMPLE1: Wrong sample format for: '&1'",element);

         String value = element.substring(pos2+1);

         Item item = findOrCreateNestedItem( profile, userid+"/"+element.substring(0,pos2) );
         if( value.indexOf('=')==-1 )
         {
            // simple item
            if( item.isCompound() )
               throw new FndException("FNDPROFILEUTILSSAMPLE4: Compound item already set for: '&1'",element);
            item.setValue(value);
         }
         else
         {
            Buffer buf;
            if( item.isCompound() )
               buf = item.getBuffer();
            else if( item.getValue()==null )
            {
               buf = profile.newInstance();
               item.setValue(buf);
            }
            else
               throw new FndException("FNDPROFILEUTILSSAMPLE2: Simple value already set for: '&1'",element);

            // compound item
            StringTokenizer st2 = new StringTokenizer(value, ";");
            while( st2.hasMoreTokens() )
            {
               String el = st2.nextToken();
               int    ix = el.indexOf('=');
               if( ix==-1 )
                  throw new FndException("FNDPROFILEUTILSSAMPLE3: Wrong sample format for nested item: '&1', nested: '&2'",element,el);

               Item it = findOrCreateNestedItem( buf, el.substring(0,ix) );
               it.setValue( el.substring(ix+1) );
            }
         }
      }
      if(DEBUG) debug("ProfileUtils.addSamples(): the resulting profile buffer:", profile);
   }

   //=======================================================================
   //  main (stand-alone mode)
   //=======================================================================

   /**
    * Run the class in stand-alone mode.
    * In this mode the class converts profils from the old format to the new one.
    * To controll the process a number of properties have to be defined in file 'ifs.properties'.
    *
    * TODO: document the tool
    *
    * Description of some more important properties:
    *
    * property 'from_users' is a list of users to export. If not specified all users are assumed.
    * property 'to_users'   is a list of users to import to. If not specified, the same as 'from_users'.
    * If specified, 'from_users' is required.
    * property 'from_sites' is a list of sites to export. If not specified all sites are exported.
    * property 'to_sites'   is a list of sites to import to. If not specified, the same as 'from_sites'.
    * If specified, 'from_sites' is required.
    * last values in 'to_*' can be omitted. Empty values are allowed.
    * A star character ('*') as a first or last value of a 'from_*' list means that all users/sites will be exported,
    * but other values will be mapped against the 'to_*' list in the same order (after ignorring the '*')
    */
   public static void main( String[] arg ) throws FndException, FileNotFoundException, IOException
   {
      console = true;
      
      System.setProperty("fndweb.profile.conversion.tool.running","true"); //used by EJBConnectionPool.getFndContext()
      
      Properties properties = System.getProperties();
      properties.load( new FileInputStream("ifs.properties") );

      DEBUG = "true".equals( properties.getProperty("debug") );
      TRACE = DEBUG || "true".equals( properties.getProperty("trace") );

      long t1 = System.currentTimeMillis();
      prfutils = new ProfileUtils(properties);
      prfutils.run();
      long t2 = System.currentTimeMillis();
      out("Conversion done in " + (t2-t1)/1000 + " seconds ( "+(t2-t1)/60000+" minutes).");
   }

   /**
    * Called from main() directly after instantiation.
    */
   private void run() throws FndException
   {
      out("\n\nUtility for conversion of IFS Web Client profiles.");
      out("\n==================================================\n");

      out("The communication method is set to "+(RMI ? "RMI" : "JAP") );
      if(DEBUG)
      {
         ProfileBuffer.DEBUG = true;
         ProfileItem.DEBUG   = true;
      }

      if(testonly)
         out(">>> Warning: Test-only mode. The changes will not be saved!");
      out("\n");

      String from_users = properties.getProperty("from_users");
      String to_users   = properties.getProperty("to_users");
      String from_sites = properties.getProperty("from_sites");
      if (Str.isEmpty(from_sites))
          out(">>> Warning: \"from_sites\" are not given. Old saved links will not be converted properly.");
      String to_sites   = properties.getProperty("to_sites");
      String buffer_size = properties.getProperty("max_users_per_cycle");
      
      if (Str.isEmpty(from_users)) // to avoid populating profiles for all useres once 
                                   // we should generate user lists with given size and conver in a loop.
      {
          if (!Str.isEmpty(to_users))
              throw new FndException("The list '&1' must not be defined if '&2' is not defined.","to_users","from_users");
          if (Str.isEmpty(buffer_size))
              throw new FndException("\"max_users_per_cycle\" should be given when converting all users.");
          String[] user_list = collectUserList(buffer_size);
          for (int i=0; i<user_list.length; i++)
              if (!Str.isEmpty(user_list[i]))
                convert(user_list[i], "", from_sites, to_sites);
      }
      else
          convert(from_users, to_users, from_sites, to_sites);
      out("\n");
   }
   
   private String[] collectUserList(String buffer_size) throws FndException
   {
      String sqlstmt = "SELECT distinct FND_USER FROM WEB_PROFILE";

      // prepare and execute the command buffer
      BufferFormatter buf_frmt = newBufferFormatter();
      ASPCommand      curcmd   = (new ASPCommand(null)).construct(newBuffer());

      curcmd.mainbuf.clear();
      curcmd.mainbuf.addItem( "METHOD",     "Query"  );
      curcmd.mainbuf.addItem( "SELECT",     sqlstmt  );
      curcmd.mainbuf.addItem( "MAX_ROWS",   "1000000");
      curcmd.mainbuf.addItem( "SKIP_ROWS",  "0"      );
      curcmd.mainbuf.addItem( "META",       "ALL"    );
      curcmd.mainbuf.addItem( "COUNT_ROWS", "N"      );
      curcmd.mainbuf.addItem( "DATA",       curcmd.mainbuf.newInstance() );

      ASPTransactionBuffer trans = new ASPTransactionBuffer(null);
      long t1 = System.currentTimeMillis();
      trans.construct(newBuffer(), buf_frmt);
      trans.addCommand("CURRENT", curcmd);
      out("Collecting users to convert...");

      ASPBuffer buf = perform(trans).getBuffer("CURRENT");
      
      long t2 = System.currentTimeMillis();
      out("Users are collected in " + (t2-t1)/1000 + "sec");
      
      int count = buf.countItems();
      int bufer_limit = Integer.parseInt(buffer_size);
      int size = (count/bufer_limit)+1;
      int i =0;
      int step = 0;
      String[] user_list = new String[size];
      while(i<(count-1))
      {
          StringBuffer list = new StringBuffer();
          for (int j=0; (j<bufer_limit && i<count); j++)
          {
              if (j!=0)list.append(",");
              list.append(buf.getBufferAt(i++).getValue("FND_USER"));
          }
          user_list[step++] = list.toString();
      }
      return user_list;
   }
   
   private void convert(String from_users, String to_users, String from_sites, String to_sites) throws FndException
   {
      Map users = new HashMap();
      Map sites = new HashMap();

      boolean all_sites = prepareMap(sites, from_sites, to_sites, false);
      boolean all_users = prepareMap(users, from_users, to_users, true);

      boolean can_contain_slash = from_sites!=null && from_sites.indexOf('/')>0;

      ProfileBuffer oldprf = fetchOldProfiles(users, all_users, sites, all_sites, can_contain_slash );

      Iterator itr = users.values().iterator();
      while( itr.hasNext() )
      {
         String user_id = (String)itr.next();
         out(" - Converting profiles for user: "+user_id);
         ProfileBuffer prf  = newProfileBuffer();
         ClientPrf cprf = loadProfile( prf, USER_PROFILE_PREFIX, user_id); //load the "new" profile for a given user
         if( !Str.isEmpty(samples) ) addSamples(prf, user_id);             //add some samples
         mergeProfiles(prf, oldprf, null);                                 //merge the "old" profiles to the fetched "new" ones
         saveProfile(cprf, prf, null);                                     //save the resulting merged profile for a given user
      }
   }
}

