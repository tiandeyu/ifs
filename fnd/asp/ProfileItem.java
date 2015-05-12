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
 * File        : ProfileItem.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-Dec-29 - Created as a separate file.
 *    Jacek P  2005-Feb-15 - Added itemToProfileItem()
 *    Jacek P  2005-Dec-27 - Added encoding of item values if un-allowed characters found.
 *                           Used the same algorithm as for names.
 *    Jacek P  2006-Mar-21 - Added handling of empty tag names
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/04/29 sumelk - Bug 82235, Changed fromXML() to parse XML events of type ENTITY_REFERENCE.
 *               2007/05/04           sadhlk
 * Bug 64337, Added two instance variables 'frombase'and 'removeFlag'.Added setFromBase(), isFromBase()
 * setRemoveFlag() , isRemoveFlag().  
 *               2006/01/25           mapelk
 * Removed errorneous translation keys
 *
 * Revision 1.4  2005/11/17 08:53:23  japase
 * Improved cloning
 *
 * Revision 1.3  2005/10/14 09:08:14  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.2  2005/09/22 12:39:22  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 * Revision 1.10.2.3  2005/09/05 13:14:04  japase
 * Added encoding of Item names when converting to XML
 *
 * Revision 1.10.2.2  2005/08/23 10:04:52  riralk
 * Fxied bug in ProfilItem.toXML(). Checked if text value is not null before replacing "&" with "&amp;"
 *
 * Revision 1.10.2.1  2005/08/22 10:49:42  riralk
 * Modified ProfilItem.toXML() replaced "&" with "&amp;" for texts.
 *
 * Revision 1.10  2005/07/12 10:33:43  japase
 * Correction to the profile handling - manipulated buffers, for example on sort, have been marked as dirty.
 *
 * Revision 1.9  2005/07/04 12:32:19  japase
 * Added removing of empty bufers.
 *
 * Revision 1.8  2005/06/30 17:31:59  japase
 * Resetting profile buffer after successful saving.
 *
 * Revision 1.7  2005/06/09 09:40:54  japase
 * Improved handling of Boolean while formatting XML
 *
 * Revision 1.6  2005/06/08 09:37:15  japase
 * Added support for XML conversion
 *
 * Revision 1.5  2005/04/06 15:38:27  marese
 * Added an extra boolean argument in call to superclass (Item) constructor
 *
 * Revision 1.4  2005/04/04 14:12:50  marese
 * Added extra Identity argument in call to superclass copy constructor
 *
 * Revision 1.3  2005/02/16 08:51:15  japase
 * Added type cast in the new methods
 *
 * Revision 1.2  2005/02/16 08:37:20  japase
 * Added conversion from standard Buffer/Item implementation to the Profile one.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.2  2005/01/20 09:18:29  japase
 * Improvments in tracking changes
 *
 * Revision 1.1  2004/12/29 14:07:46  japase
 * Splited to separate class files
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import java.util.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.namespace.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import ifs.fnd.base.*;


/**
 * A specialized version of Item for dealing with the new profiles.
 * Handles additional state information.
 * Compound items can only contain instances of ProfileBuffer class.
 */
class ProfileItem extends Item
{
   // TODO: automatically update status of top-level Item on changes ?


   //==========================================================================
   // Static variables
   //==========================================================================

   public  static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ProfileItem");


   //==========================================================================
   //  Constants
   //==========================================================================

   static final char QUERIED   = 'Q';
   static final char NEW       = 'N';
   static final char MODIFIED  = 'M';
   static final char REMOVED   = 'R';
   static final char UNDEFINED = 'U';

   //==========================================================================
   //  Instance variables
   //==========================================================================

   private char     state        = NEW;
   private boolean  dirty        = false;
   private boolean  read_only    = false;
   private boolean  ownprofile   = true;
   private String   category;
   private boolean  frombase     = false;
   private boolean     removeFlag   = false;

   private boolean  allowfmt     = false;
   private boolean  is_leaf      = true;

   //==========================================================================
   //  Constructors
   //==========================================================================

   /**
    * Create anonymous (without name) Item
    */
   public ProfileItem()
   {
      if(DEBUG) debug("ProfileItem.<init[1]>: allowfmt="+this.allowfmt);
   }

   /**
    * Create anonymous (without name) Item
    */
   public ProfileItem( boolean allowfmt )
   {
      this.allowfmt = allowfmt;
      if(DEBUG) debug("ProfileItem.<init[2]>: allowfmt="+this.allowfmt);
   }

   /**
    * Create a named Item with the specified value
    * @param name item name
    * @param value item value
    */
   public ProfileItem( String name, Object value )
   {
      super(name,value);
      checkLeaf(name);
      if(DEBUG) debug("ProfileItem["+name+"].<init[3]>: allowfmt="+this.allowfmt+", is_leaf="+this.is_leaf);
   }

   /**
    * Create a named Item with the specified value
    * @param name item name
    * @param value item value
    */
   public ProfileItem( String name, int value )
   {
      super(name,value);
      checkLeaf(name);
      if(DEBUG) debug("ProfileItem["+name+"].<init[4]>: allowfmt="+this.allowfmt+", is_leaf="+this.is_leaf);
   }

   /**
    * Internal "copy" constructor used by clone()
    */
   private ProfileItem( String name, String type, String status, Object value, boolean valueSet )
   {
      super(name, type, status, null, value, valueSet, true);
      checkLeaf(name);
   }

   //==========================================================================
   //  Overriden methods from Item
   //==========================================================================

   /**
    * Set the name of this Item
    */
   public void setName( String name )
   {
      super.setName(name);
      checkLeaf(name);
   }

   /**
    * Set the status of this Item.
    * Not allowed for ProfileItem other then when parsing/formatting.
    */
   public void setStatus( String status ) // the idea is to use the 'status' field for serialization of the additional attributes,
   {                                      // but not sure if it is necessary.
      if(DEBUG) debug("ProfileItem["+getName()+"].setStatus("+status+"): allowfmt="+allowfmt);
      if(allowfmt)                        // if not: setStatus(), getStatus() and handling of 'allowfmt' can be removed.
         super.setStatus(status);         // if yes: correct serialization of the additional fields should be added
      else                                // TODO: decission!
         throw methodNotSupported();
   }

   /**
    * Return the status of this Item
    * Not allowed for ProfileItem other then when parsing/formatting.
    */
   public String getStatus_()
   {
      if(DEBUG) debug("ProfileItem["+getName()+"].getStatus(): allowfmt="+allowfmt);
      if(allowfmt)
         return super.getStatus();
      else
         throw methodNotSupported();
   }

   /**
    * Set the value (String or nested Buffer) of this Item
    */
   public void setValue( Object value )
   {
      // TODO: ? ev. don't allow to change if it is a buffer and the item is not a leaf and not UNDEFINED
      // new flag 'is_leaf' and constructor?
      if( valueChanged(value) )
         super.setValue(value);
   }

   /**
    * Set the int value of this Item
    */
   public void setValue( int value )
   {
      Object newval = new Integer(value);
      setValue(newval);
   }

   /**
    * Clones this Item
    */
   public Object clone()
   {
      Object value  = getValue();
      Object newval = null;
      if( value instanceof Buffer )
         newval = ((Buffer)value).clone();
      else if( value instanceof Integer || value instanceof Double || value instanceof String )
         newval = value;
      else if( value!=null )
         throw new RuntimeException("FNDPRFITEMCLNOTSUP: Not supported cloning of class "+value.getClass().getName());

      ProfileItem item  = new ProfileItem(getName(), getType(), super.getStatus(), newval, isValueSet());

      item.state      = this.state;
      item.dirty      = this.dirty;
      item.read_only  = this.read_only;
      item.ownprofile = this.ownprofile;
      item.frombase   = this.frombase;
      item.removeFlag = this.removeFlag;
      item.category   = this.category;
      item.allowfmt   = this.allowfmt;
      //item.is_leaf    = this.is_leaf; - already set by the constructor

      return item;
   }

   /**
    * Converts an Item to ProfileItem
    */
   static ProfileItem itemToProfileItem( Item item )
   {
      if( item instanceof ProfileItem )
         return (ProfileItem)item;

      Object value  = item.getValue();
      Object newval = null;
      if( value instanceof Buffer )
         newval = ProfileBuffer.bufferToProfileBuffer( (Buffer)value );
      else
         newval = value;

      return new ProfileItem(item.getName(), item.getType(), item.getStatus(), newval, item.isValueSet());
   }


   public String toString()
   {
      String name  = getName();
      Object value = getValue();
      if( name!=null && name.indexOf(ProfileUtils.ENTRY_SEP)>=0 )
         return ( name== null ? "" : "$" + name )
              + ( category== null ? "" : "/" + category )
              + "[" + state
              + ( read_only   ? "r" : "w" )
              + ( ownprofile  ? "+" : "-" )
              + ( frombase    ? "b" : "o" )
              + (removeFlag   ? "y"  : "n")
              + ( dirty       ? "*" : ""  ) + "]"
              + ( value==null ? "*" : "=" + (isCompound() ? "" : value));
      else
         //return super.toString();
         return ( name== null ? "" : "$" + name )
              + ( dirty      ? "[*]" : ""  )
              + ( value==null ? "*" : "=" + (isCompound() ? "" : value));
   }

   /**
    * Return true if the specified ProfileItem has exactly the same
    * profile contents as this one, regardles state and dirty flags.
    */
   public boolean equalsTo( ProfileItem that )
   {
      if( that==null  )
         return false;

      String thisname = this.getName();
      String thatname = that.getName();

      if( (thisname==null && thatname!=null) || (thisname!=null && !thisname.equals(thatname)) )
         return false;

      if( (this.category==null && that.category!=null) || (this.category!=null && !this.category.equals(that.category)) )
         return false;

      if( this.read_only != that.read_only || this.ownprofile != that.ownprofile || this.frombase != that.frombase )
         return false;

      return valuesEqual( this.getValue(), that.getValue() );
   }

   public boolean valueEquals( Object newvalue )
   {
      return valuesEqual( this.getValue(), newvalue );
   }

   private boolean valuesEqual( Object thisvalue, Object thatvalue )
   {
      if( thisvalue instanceof ProfileBuffer && thatvalue instanceof ProfileBuffer )
         return ((ProfileBuffer)thisvalue).equalsTo( (ProfileBuffer)thatvalue );

      if( (thisvalue==null && thatvalue!=null) || (thisvalue!=null && !thisvalue.equals(thatvalue)))
         return false;

      return true;
   }

   //==========================================================================
   //  New methods for control of Item specialization
   //==========================================================================

   /**
    * Due to internal usage of status, all methods allowing manipulation of
    * these attributes are prohibited in ProfileItem.
    */
   private RuntimeException methodNotSupported()
   {
      return new RuntimeException(": Method not supported by ProfileItem.");
   }

   /**
    * Check if the Buffer is of expected class.
    * Used for extended implementation.
    */
   private void checkBuffer( Buffer buffer )
   {
      if( !(buffer instanceof ProfileBuffer) )
         throw new RuntimeException("FNDPRFITEMNOTSUPPBUF: The Buffer implementation is not supported by this Item implementation.");
   }

   //==========================================================================
   //  Additional new methods for profile implementation
   //==========================================================================

   void setState( char ch ) throws FndException
   {
      switch(ch)
      {
         case QUERIED:
            clearDirty();
         case NEW:
         case MODIFIED:
         case REMOVED:
            this.state = ch;
            this.dirty = true;
            break;
         case UNDEFINED:
            throw new FndException("FNDPRFITEMSTATERR1: Unallowed state transition to UNDEFINED state");
         default:
            throw new FndException("FNDPRFITEMSTATERR2: Unrecognized state: '&1'", ch+"");
      }
   }

   void setUndefined() throws FndException // special treatment for items in a nested structure (serialized to binary value); allowed only for NEW
   {
      if( this.state==NEW )
         this.state = UNDEFINED;
      else
         throw new FndException("FNDPRFITEMSTATERR3: Unallowed state transition between '&1' and 'U'",state+"");
   }

   char getState()  // state: new item: NEW; changed: NEW -> NEW, QUERIED -> MODIFIED, REMOVED -> REMOVED?
   {                // change state of nested Items for compound Item ??? Maybe not?
      return this.state;
   }

   boolean isChanged()
   {
      if( isCompound() && !dirty )
         return ((ProfileBuffer)getBuffer()).isChanged();
      return dirty;
   }

   void clearDirty()
   {
      this.dirty = false;
      if( isCompound() )
         ((ProfileBuffer)getBuffer()).clearDirty();
   }

   void setReadOnly( boolean readonly )
   {
      this.read_only = readonly;
   }

   boolean isReadOnly()
   {
      return this.read_only;
   }

   void setOwnProfile( boolean ownprofile )
   {
      this.ownprofile = ownprofile;
   }

   boolean isOwnProfile()
   {
      return this.ownprofile;
   }
   
   void setFromBase( boolean frombase )
   {
      this.frombase = frombase;
   }
   
   boolean isFromBase()
   {
      return this.frombase;
   }

   void setRemoveFlag(boolean flag)
   {
      this.removeFlag = flag;
   }
   
   boolean isRemoveFlag()
   {
      return this.removeFlag;
   }

   void setCategory( String str )
   {
      this.category = str;
   }

   String getCategory()
   {
      return this.category;
   }

   private boolean valueChanged( Object value )
   {
      Object oldval = getValue();
      if( oldval==null && value==null )
         return false;

      if( (oldval!=null && value==null) || (oldval==null && value!=null) )
      {
         markDirty();
         return true;
      }

      if( oldval instanceof ProfileBuffer && value instanceof ProfileBuffer )
      {
         if( ((ProfileBuffer)oldval).equalsTo( (ProfileBuffer)value ) )
            return false;
         else
         {
            markDirty();
            return true;
         }
      }

      if( oldval.toString().equals(value.toString()) )
         return false;

      markDirty();
      return true;
   }

   private void markDirty()
   {
      dirty = true;
      switch(state)
      {
         case REMOVED:
         case QUERIED:
            state = MODIFIED;
            break;
         case NEW:
         case MODIFIED:
         case UNDEFINED:
            ;
      }
   }

   void allowFormatting()
   {
      if(DEBUG) debug("ProfileItem["+getName()+"].allowFormatting()");
      this.allowfmt = true;
      if( isCompound() )
         ((ProfileBuffer)getValue()).allowFormatting();
   }

   void formatted()
   {
      if(DEBUG) debug("ProfileItem["+getName()+"].formatted()");
      this.allowfmt = false;
      if( isCompound() )
         ((ProfileBuffer)getValue()).formatted();
   }

   boolean isFmtAllowed()
   {
      return this.allowfmt;
   }

   /**
    * Sets is_leaf to true if the name of this Item contains character '^',
    * i.e. the Item corresponds to a single row in the database.
    * Called from constructors and setName(), i.e. from all palces changing the name.
    */
   private void checkLeaf( String name )
   {
      this.is_leaf = name!=null && name.indexOf(ProfileUtils.ENTRY_SEP)>=0;
   }

   boolean isLeaf()
   {
      return this.is_leaf;
   }

   /**
    * Resets the state and dirty flag of this Item. Throws exception if the Item is marked as REMOVED.
    * Doesn't change the state if it is UNDEFINED.
    * Returns true if the item is not a leaf, is a compound one and the contained buffer is empty.
    */
   boolean reset() throws FndException
   {
      boolean ret = false;
      if( isCompound() )
         ret = ((ProfileBuffer)getBuffer()).reset();

      switch(state)
      {
         case QUERIED:
         case NEW:
         case MODIFIED:
            this.state = QUERIED;
            this.dirty = false;
            break;
         case REMOVED:
            throw new FndException("FNDPRFITEMREMOVED: Item is already removed; Can't reset it!");
         case UNDEFINED:
            break;
         default:
            throw new FndException("FNDPRFITEMSTATERR4: Unrecognized state: '&1'", state+"");
      }
      return ret & !is_leaf;
   }

   //==========================================================================
   //  Methods for XML parsing
   //==========================================================================

   private final static String RTYPE_TEXT   = "Text",        ITYPE_TEXT = "S";  //or null
   private final static String RTYPE_INT    = "Integer";                        //null and value of type Integer
   private final static String RTYPE_NUM    = "Number",      ITYPE_NUM  = "N";
   private final static String RTYPE_BOOL   = "Boolean",     ITYPE_BOOL = "B";
   private final static String RTYPE_DATE   = "Timestamp",   ITYPE_DATE = "D";
   private final static String RTYPE_AGGREG = "Aggregate";                      //compound Item
   private final static String RTYPE_ENUM   = "Enum",        ITYPE_ENUM = "E";

   private final static String RBOOL_TRUE   = "1";
   private final static String RBOOL_FALSE  = "0";
   private final static String IBOOL_TRUE   = "TRUE";
   private final static String IBOOL_FALSE  = "FALSE";

   /**
    * Convert part of XML document to an instance of ProfileItem. Return null if the XML tag
    * does not represent an Item. The method does not validate the value.
    */
   static ProfileItem fromXML( XMLStreamReader xmlr, ProfileBuffer buf ) throws XMLStreamException, FndException
   {
      if( !xmlr.isStartElement() )
         return null;

      int count = xmlr.getAttributeCount();

      if( !xmlr.hasName() || count==0 )
         throw new FndException("FNDPRFITEMNOITEMNAME: Error while parsing XML: Item has to have a name and type attribute");

      String name = xmlr.getLocalName();

      for(int i=0; i<count; i++)
      {
         if("datatype".equals( xmlr.getAttributeName(i).getLocalPart() ) )
         {
            String type = xmlr.getAttributeValue(i);

            ProfileItem item = null;
            if( RTYPE_AGGREG.equals(type) ) // Aggregate type: START_ELEMENT tag expected
            {
               while( xmlr.hasNext() )
               {
                  int event_type = xmlr.nextTag();
                  ProfileBuffer newbuf = (ProfileBuffer)buf.newInstance();
                  if( ProfileBuffer.fromXML(xmlr, newbuf) )
                  {
                     item = (ProfileItem)buf.newItem();
                     item.setName(decodeTagName(name));
                     item.setValue(newbuf);
                  }
                  else if( xmlr.isEndElement() && xmlr.hasName() && name.equals(xmlr.getLocalName()) )
                  {
                     if( item!=null )
                        return item;
                     else
                        throw new FndException("FNDPRFITEMEMTYAGGR: Error while parsing XML: Empty aggregated item '&1'!",name);
                  }
                  else
                     throw new FndException("FNDPRFITEMNOTEXPTAG: Error while parsing XML: Unexpected tag '&1' for item '&2'!",
                                            ProfileUtils.getXMLEventTypeAsString(event_type), name);
               }
               throw new FndException("FNDPRFITEMUNEXPEOF2: Error while parsing XML: Unexpected end of XML document!");
            }
            else // Simple types: CHARACTERS or/and END_ELEMENT (empty value) expected
            {
               while( xmlr.hasNext() )
               {
                  int event_type = xmlr.next();
                  if( xmlr.isCharacters() )
                  {
                     String text = xmlr.hasText() ? xmlr.getText() : null;
                     if( RTYPE_TEXT.equals(type) )
                     {
                        item = (ProfileItem)buf.newItem();
                        item.setName(decodeTagName(name));
                        item.setValue(decodeTagValue(text));
                     }
                     else if( RTYPE_INT.equals(type) )
                     {
                        try
                        {
                           item = (ProfileItem)buf.newItem();
                           item.setName(decodeTagName(name));
                           item.setValue(Integer.parseInt(text.trim()));
                        }
                        catch( NumberFormatException x )
                        {
                           throw new FndException("FNDPRFITEMNOTVALIDINT: Error while parsing XML: Not valid integer value '&1' for item '&2'!",
                                                  text,name);
                        }
                     }
                     else if( RTYPE_NUM.equals(type) )
                     {
                        item = (ProfileItem)buf.newItem();
                        item.setName(decodeTagName(name));
                        item.setValue(text);
                        item.setType(ITYPE_NUM);
                     }
                     else if( RTYPE_BOOL.equals(type) )
                     {
                        item = (ProfileItem)buf.newItem();
                        item.setName(decodeTagName(name));
                        item.setValue(RBOOL_TRUE.equals(text) ? IBOOL_TRUE : IBOOL_FALSE);
                        item.setType(ITYPE_BOOL);
                     }
                     else if( RTYPE_DATE.equals(type) )
                     {
                        //  0123 56 89 12 45 78
                        // "yyyy-MM-dd-HH.mm.ss" - from IfsNames
                        // "yyyy-MM-ddTHH:mm:ss" - from FndContext
                        item = (ProfileItem)buf.newItem();
                        item.setName(decodeTagName(name));
                        item.setValue(text.substring(0,10)+"-"+text.substring(11));
                        item.setType(ITYPE_DATE);
                     }
                     else if (RTYPE_ENUM.equals(type))
                        continue;
                     else
                        throw new FndException("FNDPRFITEMNOTSUPTYPE: Error while parsing XML: Not supported record type '&1' for item '&2'!",type,name);
                  }
                  else if( xmlr.isEndElement() && xmlr.hasName() && name.equals(xmlr.getLocalName()) )
                  {
                     if( item==null ) //empty tag
                     {
                        item = (ProfileItem)buf.newItem();
                        item.setName(decodeTagName(name));
                        item.setValue(null);
                     }
                     return item;
                  }
                  else if(event_type == XMLEvent.ENTITY_REFERENCE)
                  {
                     continue;
                  }
                  else
                     throw new FndException("FNDPRFITEMNOTEXPTAG: Error while parsing XML: Unexpected tag '&1' for item '&2'!",
                                            ProfileUtils.getXMLEventTypeAsString(event_type), name);
               }
               throw new FndException("FNDPRFITEMUNEXPEOF: Error while parsing XML: Unexpected end of XML document!");
            }
         }
      }
      throw new FndException("FNDPRFITEMRECTYPENFOUND: Error while parsing XML: Record type for item '&1' not found!",name);
   }

   /**
    * Convert this ProfileItem to XML. Called from ProfileBuffer.toXML().
    * The method does not validate the value.
    */
   void toXML( AutoString out, int level ) throws FndException
   {
      // Available methods for profile handling:
      //  - write(Global)ProfileBuffer( ASPBuffer )
      //  - write(Global)ProfileValue( String )
      //  - write(Global)ProfileFlag( boolean ) -- writes a String: TRUE or FALSE
      //
      // Available item types in ASPBuffer (always serialized to strings)
      //  - Date
      //  - Number (double)

      if(DEBUG) debug("ProfileItem["+getName()+"].toXML("+level+")");

      if( !isValueSet() )  return;

      String rectype  = null;
      String recvalue = null;

      if( !isCompound() )
      {
         String type  = getType(); //type must be one of ('S','N','B','D') or null
         Object value = getValue();

         if( ( type==null || ITYPE_BOOL.equals(type) ) && ( IBOOL_TRUE.equals(value) || IBOOL_FALSE.equals(value) ) )
         {
            // Boolean
            rectype  = RTYPE_BOOL;
            recvalue = IBOOL_TRUE.equals(value) ? RBOOL_TRUE : RBOOL_FALSE;
         }
         else if( (type==null || ITYPE_TEXT.equals(type)) && (value==null || value instanceof String) )
         {
            // String
            rectype  = RTYPE_TEXT;
            recvalue = value!=null ? (String)value : null;
            if (recvalue!=null)
               recvalue = encodeTagValue(recvalue);
         }

         else if( type==null && value instanceof Integer )
         {
            // int
            rectype  = RTYPE_INT;
            recvalue = value.toString();
         }
         else if( type==null || (value!=null && !(value instanceof String)) )
            throw new FndException("FNDPRFITEMNOTSUPTYPE1: Error while formatting XML: Data type '&1' is not supported in compound profile items.",
                                   value.getClass().getName());
         else if( ITYPE_NUM.equals(type) )
         {
            // Number
            rectype  = RTYPE_NUM;
            recvalue = value!=null ? (String)value : null;
         }
         else if( ITYPE_DATE.equals(type) )
         {
            // DateTime
            rectype  = RTYPE_DATE;
            recvalue = ((String)value).substring(0,10) + "T" + ((String)value).substring(11);
         }
         else
            throw new FndException("FNDPRFITEMNOTSUPTYPE2: Error while formatting XML: Data type marker '&1' is not supported in compound profile items.", type);
      }
      else
         rectype = RTYPE_AGGREG;

      ProfileUtils.indent(out, level);
      String tagname = encodeTagName(getName());
      out.append("<",tagname," ifsrecord:datatype=\"",rectype,"\">");

      if(isCompound())
      {
         out.append(ProfileUtils.NL);
         ((ProfileBuffer)getBuffer()).toXML(out,level+1);
         ProfileUtils.indent(out, level);
      }
      else
         out.append(recvalue);
      out.append("</",tagname,">",ProfileUtils.NL);
   }

   /**
    * Return 'true' if a string contains only allowed characters,
    * which are letters 'a'-'z' and 'A'-'Z', digits '0'-'9' and '_'.
    */
   private static boolean isIdPart( String name )
   {
      if( name==null )
         return false;

      for( int i=0; i<name.length(); i++ )
         if( !IfsNames.isIdPart(name.charAt(i)) )
            return false;
      return true;
   }

   private static final String ENCODED_ITEM_NAME      = "_EncodedItem_";
   private static final String ENCODED_ITEM_NAME_NULL = "_EncodedItemNull_";
   private static final char   CODE_SEPARATOR         = '-';

   /**
    * Encode Item name to be a valid XML tag name. If the name doesn't contain
    * un-allowed characters and doesn't start with '_', the name is not encoded.
    * Otherwise a string '_EncodedItem_' is added and all eventual un-allowed
    * characters are replaced with sequences '-<ascii>-', where <ascii> is
    * a decimal representation of the character.
    * Allowed characters are letters 'a'-'z' and 'A'-'Z', digits '0'-'9',
    * underscore character '_', dot '.' and minus '-'.
    * Colon ':' is also allowed, but due to its special meaning as name space
    * separator, it will be treated as an un-allowed character.
    */
   private static String encodeTagName( String str )
   {
      if( isXMLName(str) && str.charAt(0)!=ENCODED_ITEM_NAME.charAt(0) )
         return str;

      AutoString out = new AutoString();
      return encodeTag(out, str);
   }

   /**
    * Encode Item string value to be a valid XML tag value. If the value doesn't contain
    * un-allowed characters and doesn't start with '_', the value is not encoded.
    * Otherwise a string '_EncodedItem_' is added and all eventual un-allowed
    * characters are replaced with sequences '-<ascii>-', where <ascii> is
    * a decimal representation of the character.
    * In XML 1.0 characters 0 .. 31 are illegal (with the exception for HT, LF, CR)
    */
   private static String encodeTagValue( String str )
   {
      if( Str.isEmpty(str) )
         return str;

      AutoString out = new AutoString();
      boolean xml_text = str.charAt(0)!=ENCODED_ITEM_NAME.charAt(0);
      int size = str.length();
      for( int i=0; i<size; i++ )
      {
         char ch = str.charAt(i);
         switch(ch)
         {
            case '&' :
               out.append("&amp;");
               break;
            case '\'' :
               out.append("&apos;");
               break;
            case '>' :
               out.append("&gt;");
               break;
            case '<' :
               out.append("&lt;");
               break;
            case '"' :
               out.append("&quot;");
               break;
            case '\t':
            case '\n':
            case '\r':
               out.append(ch);
               break;
            default :
               out.append(ch);
               if( ch<' ' )
                  xml_text = false;
         }
      }
      str = out.toString();
      if(xml_text)
         return str;

      out.clear();
      return encodeTag(out, str);
   }

   private static String encodeTag( AutoString out, String str )
   {
      if(str==null)
         out.append(ENCODED_ITEM_NAME_NULL);
      else
      {
         out.append(ENCODED_ITEM_NAME);
         int size = str.length();
         for( int i=0; i<size; i++ )
         {
            char ch = str.charAt(i);
            if( ch!=CODE_SEPARATOR && isXMLNamePart(ch) )
               out.append(ch);
            else
            {
               out.append( CODE_SEPARATOR );
               out.append( "" + ( (int)ch ) );
               out.append( CODE_SEPARATOR );
            }
         }
      }
      return out.toString();
   }

   /**
    * Convert the coded XML tag name to Item name.
    * If the name doesn't start with '_' then it is not encoded - simply return the value.
    * If the name starts with string '_EncodedItem_', some characters could be
    * encoded - find sequences of type '-<ascii>-' and convert them to corresponding characters.
    * Other start sequences are not allowed for encoded Items.
    */
   private static String decodeTagName( String name )  throws FndException
   {
      return decodeTag(name);
   }

   /**
    * Convert the coded XML tag value to Item string value.
    * If the value doesn't start with '_' then it is not encoded - simply return the value.
    * If the value starts with string '_EncodedItem_', some characters could be
    * encoded - find sequences of type '-<ascii>-' and convert them to corresponding characters.
    * Other start sequences are not allowed for encoded Items.
    */
   private static String decodeTagValue( String value )  throws FndException
   {
      return decodeTag(value);
   }

   /**
    * Common implementation of both decode*() functions
    */
   private static String decodeTag( String str )  throws FndException
   {
      if( Str.isEmpty(str) )
         return str;

      if( str.charAt(0)!=ENCODED_ITEM_NAME.charAt(0) )
         return str;

      if( str.equals(ENCODED_ITEM_NAME_NULL) )
         return null;

      if( !str.startsWith(ENCODED_ITEM_NAME) )
         throw new FndException("FNDPRFITEMNOTVALIDNAMEVALUE: The item name or value '&1' is not valid", str);

      // the name is encoded - decode it
      AutoString out = new AutoString();
      int from = ENCODED_ITEM_NAME.length();

      while( from<str.length() )
      {
         int pos1 = str.indexOf(CODE_SEPARATOR, from);
         int pos2;

         if( pos1<0 )
         {
            out.append( str.substring(from) );
            break;
         }
         else
         {
            pos2 = str.indexOf(CODE_SEPARATOR, pos1+1);
            if( pos2<0 )
               throw new FndException("FNDPRFITEMNOTVALIDNAMEVALUE2: The item name or value '&1' is wrong encoded", str);

            out.append( str.substring(from, pos1) );
            try
            {
               out.append( (char)Integer.parseInt( str.substring(pos1+1, pos2) ) );
            }
            catch( NumberFormatException x )
            {
               throw new FndException("FNDPRFITEMNOTVALIDNAME3: The sequence '&1' in item '&2' doesn't represent any valid encoded character.",
                                       str.substring(pos1+1, pos2), str);
            }
            from = pos2 + 1;
         }
      }
      return out.toString();
   }

   /**
    * Check if a string is a valid XML tag name.
    */
   private static boolean isXMLName( String str )
   {
      if( Str.isEmpty(str) )
         return false;

      if( !isXMLNameFirst(str.charAt(0)) )
         return false;

      for( int i=1; i<str.length(); i++ )
         if( !isXMLNamePart(str.charAt(i)) )
            return false;

      return true;
   }

   /**
    * Check if a character is valid as a first character in an XML tag name.
    */
   private static boolean isXMLNameFirst( char ch )
   {
      // colon ':' is also allowed but due to its special meaning as name space separator, we don't want to allow it
      return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '_';
   }

   /**
    * Check if a character is a valid character in an XML tag name,
    * but not at the first position.
    */
   private static boolean isXMLNamePart( char ch )
   {
      // colon ':' is also allowed but due to its special meaning as name space separator, we don't want to allow it
      return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '.';
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   private static void debug( String line )
   {
      ProfileUtils.debug(line);
   }
}

