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
 * File        : ProfileBuffer.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-Dec-28 - Created as a separate file.
 *    Jacek P  2005-Feb-15 - Added bufferToProfileBuffer()
 *    Jacek P  2005-Mar-08 - Added setItem(Item,int);
 * ----------------------------------------------------------------------------
 * New Comments:
 * Bug 64337, Added markItem(), markItems() and overloaded markItem() to mark remove flag
 *            for removed ProfileItems.
 *
 * Revision 1.3  2005/11/17 08:53:51  japase
 * Improved cloning
 *
 * Revision 1.2  2005/10/14 09:08:14  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.9  2005/07/14 07:02:36  japase
 * New sort algorithm for ProfileBuffers
 *
 * Revision 1.8  2005/07/12 10:33:43  japase
 * Correction to the profile handling - manipulated buffers, for example on sort, have been marked as dirty.
 *
 * Revision 1.7  2005/07/04 12:32:19  japase
 * Added removing of empty bufers.
 *
 * Revision 1.6  2005/06/30 17:31:59  japase
 * Resetting profile buffer after successful saving.
 *
 * Revision 1.5  2005/06/08 09:38:21  japase
 * Added support for XML conversion
 *
 * Revision 1.4  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.3.2.1  2005/03/08 14:57:07  japase
 * Added overloaded setItem(Item,int)
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
 * Revision 1.3  2005/01/20 09:18:29  japase
 * Improvments in tracking changes
 *
 * Revision 1.2  2004/12/29 14:07:46  japase
 * Splited to separate class files
 *
 * Revision 1.1  2004/12/29 08:41:55  japase
 * Splited to two files
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


/**
 * A specialized version of StandardBuffer for dealing with the new profiles.
 * Handles additional state information. Allowes only items of type ProfileItem.
 */
class ProfileBuffer extends StandardBuffer
{
   //==========================================================================
   // Static variables
   //==========================================================================

   public static boolean DEBUG      = Util.isDebugEnabled("ifs.fnd.asp.ProfileBuffer");
   public static boolean DUMP_STACK = Util.isDebugEnabled("ifs.fnd.asp.ProfileBuffer.dump_stack");


   //==========================================================================
   //  Instance variables
   //==========================================================================

   private boolean dirty    = false; // Buffer marks as dirty if the number of Items is changed
   private boolean allowfmt = false;



   /**
    * Return a new instance of ProfileItem
    */
   public Item newItem()
   {
      if(DEBUG) debug("ProfileBuffer.newItem(): allowfmt="+allowfmt);
      return new ProfileItem(allowfmt);
   }

   /**
    *
    */
   public void clear_()
   {
      removeItems();
      if( count==0 )
      {
         header = null;
         frozen = false;
      }
   }

   /**
    * Append a specified Item to the end of the item list in this Buffer,
    * which  may lead to duplicate item names.
    * Return the position of the newly created item (0,1...).
    */
   public int addItem( Item item )
   {
      return addItem(item, false);
   }

   int addItem( Item item, boolean clear )
   {
      if(DEBUG) debug("ProfileBuffer.addItem("+Str.nvl(item.getName(),"")+"): this.allowfmt="+allowfmt
                      + (item instanceof ProfileItem ? ", item.allowfmt="+((ProfileItem)item).isFmtAllowed() : "") );
      super.addItem(item);
      //dirty = true;
      dirty = !clear;
      if(DEBUG && dirty) debugWithDump(" - buffer set to 'dirty' at:");
      return count;
   }

   /**
    * Replace Item at the specified position (0,1,...) in
    * the item list.
    * Return the position of the inserted item.
    */
   public int setItem( Item item, int position )
   {
      if( position < 0 || position >= count )
         addItem(item);
      else
         items[position] = item;
      return position;
   }

   /**
    * Remove the ProfileItem at a specified position (0,1,...) in this Buffer,
    * if the item's state is UNDEFINED; mark as REMOVED otherwise.
    */
   public void removeItem( int position )
   {
      if( position < 0 || position >= count )
         return;

      ProfileItem item = (ProfileItem)items[position];

      if( item.isLeaf() && item.getState()!=ProfileItem.UNDEFINED )
         try
         {
            item.setState( ProfileItem.REMOVED );
         }
         catch( FndException x )
         {
            throw new RuntimeException(x);
         }
      else
      {
         for( int i=position; i<count-1; i++)
            items[i] = items[i + 1];
         items[--count] = null;
         dirty = true;
         if(DEBUG) debugWithDump("ProfileBuffer.removeItem("+Str.nvl(item.getName(),"")+") - buffer set to 'dirty' at:");
      }
   }

   /**
    * Remove all items from this Buffer
    */
   public void removeItems()  // TODO: ??? Maybe this method shouldn't override the super??? Introduce new API instead, if necessary.
   //public void markItemsRemoved()
   {
      //int newcnt=0;
      for( int i=0; i<count; i++ )
      {
         ProfileItem item = (ProfileItem)items[i];
         if( item==null )
         {
            count = i;
            break;
         }

         if( !item.isLeaf() || item.getState()==ProfileItem.UNDEFINED )
         {  // remove
            /*
            if(i<count-1)
               items[i] = items[i+1];
            else
               items[i] = null;
            */
            for( int j=i; j<count-1; j++ )
               items[j] = items[j+1];

            items[count-1] = null;
            i--;
            dirty = true;
            if(DEBUG) debugWithDump("ProfileBuffer.removeItems("+Str.nvl(item.getName(),"")+") - buffer set to 'dirty' at:");
         }
         else
         {  // only mark as REMOVED
            try
            {
               item.setState( ProfileItem.REMOVED );
            }
            catch( FndException x )
            {
               throw new RuntimeException(x);
            }
            //newcnt++;
         }
      }
      //count = newcnt;
   }

   /**
    * Resets the state and dirty flags of Items contained by this Buffer.
    * Removes Items marked as REMOVED.
    * Doesn't change the state of UNDEFINED Items.
    * Clear the dirty flag.
    * Returns true if there is no more items.
    */
   boolean reset() throws FndException
   {
      for( int i=0; i<count; i++ )
      {
         ProfileItem item = (ProfileItem)items[i];
         if( item==null )
         {
            count = i;
            break;
         }

         if( item.getState()==ProfileItem.REMOVED || item.reset() )
         {  // remove it
            for( int j=i; j<count-1; j++ )
               items[j] = items[j+1];

            items[--count] = null;
            i--;
         }
      }
      dirty = false;
      return count==0;
   }
   
   /**
    * If the ProfileItems' state is REMOVED,
    * set the items' removeFlag to true.
    */
   public void markItem(String item_name)
   {
      int position = getItemPosition(item_name);
      
       if( position < 0 || position >= count )
         return;
      
       ProfileItem item = (ProfileItem)items[position];
       if(item.getState() == ProfileItem.REMOVED)
          item.setRemoveFlag(true);
   }
   
   /**
    * Set the removeFlag of all the items in the
    * buffer to true.
    */
   public void markItems(Buffer buf)
   {
      for(int i=0; i<buf.countItems(); i++)
      {
         ProfileItem item = (ProfileItem)buf.getItem(i);
         String name  = item.getName();
         int pos  = name.indexOf(ProfileUtils.ENTRY_SEP);

         if( pos>=0 )
           markItem(name);
         else if( item.isCompound() )
           markItems( item.getBuffer());
     }
   }
   
   public void markItem(Buffer buf, String id)
   {
      int position = buf.getItemPosition(id);
      
      if( position < 0 || position >buf.countItems())
         return;
      
       ProfileItem item = (ProfileItem)buf.getItem(position);
       if(item.getState() == ProfileItem.REMOVED)
          item.setRemoveFlag(true);
   }


   /**
    * Return true if a specified ProfileBuffer has exactly the same
    * profile contents as this ProfileBuffer
    */
   public boolean equalsTo( ProfileBuffer that )
   {
      if( that==null )
         return false;

      if( count != that.count )
         return false;

      for( int i=0; i<count; i++ )
      {
         ProfileItem thisitem = (ProfileItem)this.items[i];
         ProfileItem thatitem = (ProfileItem)that.items[i];

         if( !thisitem.equalsTo(thatitem) )
            return false;
      }
      return true;
   }

   /**
    * Clones this Buffer
    */
   public Object clone()
   {
      ProfileBuffer newbuffer = new ProfileBuffer();
      //newbuffer.setHeader(getHeader());
      newbuffer.setHeader(this.header);
      newbuffer.dirty    = dirty;
      newbuffer.allowfmt = allowfmt;

      for( int i=0; i<count; i++ )
      {
         Item item = items[i];
         if( item!=null )
            item = (Item)item.clone();
         newbuffer.addItem(item);
      }
      return newbuffer;
   }

   /**
    * Converts a Buffer to ProfileBuffer
    */
   static ProfileBuffer bufferToProfileBuffer( Buffer buffer )
   {
      if( buffer instanceof ProfileBuffer )
         return (ProfileBuffer)buffer;

      ProfileBuffer newbuffer = new ProfileBuffer();
      newbuffer.setHeader(buffer.getHeader());
      for( int i=0; i<buffer.countItems(); i++ )
      {
         ProfileItem item = ProfileItem.itemToProfileItem( buffer.getItem(i) );
         newbuffer.addItem(item);
      }
      return newbuffer;
   }

   /**
    * Return a new (empty) instance of the same class as this Buffer
    */
   public Buffer newInstance()
   {
      ProfileBuffer b = new ProfileBuffer();
      b.allowfmt = this.allowfmt;
      return b;
   }

   /**
    * Check if the Item is of expected class.
    * Used for extended implementation.
    * ProfileBuffer allowes only items of type ProfileItem.
    */
   public void checkItem( Item item )
   {
      if( !(item instanceof ProfileItem) )
         throw new RuntimeException("FNDPRFBUFNOTSUPPITEM: The Item class is not supported by this Buffer implementation.");
   }

   boolean isChanged()
   {
      if(dirty)
         return true;

      for( int i=0; i<count; i++ )
         if( ((ProfileItem)items[i]).isChanged() )
            return true;
      return false;
   }

   void clearDirty()
   {
      dirty = false;
      for( int i=0; i<count; i++ )
         ((ProfileItem)items[i]).clearDirty();
   }

   void allowFormatting()
   {
      if(DEBUG) debug("ProfileBuffer.allowFormatting(): count="+count);
      this.allowfmt = true;
      for( int i=0; i<count; i++ )
         ((ProfileItem)items[i]).allowFormatting();
   }

   void formatted()
   {
      if(DEBUG) debug("ProfileBuffer.formatted()");
      this.allowfmt = false;
      for( int i=0; i<count; i++ )
         ((ProfileItem)items[i]).formatted();
   }

   //==========================================================================
   //  Methods for XML parsing
   //==========================================================================

   private static final String XML_NAME = "ProfileBuffer";
   private static final String XML_NAME_RS1 = "RegionalSettings.DateTimeFormat";
   private static final String XML_NAME_RS2 = "RegionalSettings.PercentFormat";
   private static final String XML_NAME_RS3 = "RegionalSettings.NumberFormat";
   private static final String XML_NAME_RS4 = "RegionalSettings.CurrencyFormat";

   /**
    * Convert part of XML document to an instance of ProfileBuffer. Return false if the XML tag
    * does not represent a Buffer.
    */
   static boolean fromXML( XMLStreamReader xmlr, ProfileBuffer buf ) throws XMLStreamException, FndException
   {
      String name = xmlr.hasName() ? xmlr.getLocalName() : null;

      if( xmlr.isStartElement() && ( XML_NAME.equals(name) || XML_NAME_RS1.equals(name) || XML_NAME_RS2.equals(name) || XML_NAME_RS3.equals(name) || XML_NAME_RS4.equals(name)))
      {
         while( xmlr.hasNext() )
         {
            int event_type = xmlr.nextTag();
            ProfileItem item = ProfileItem.fromXML(xmlr, buf);
            if( item!=null )
               buf.addItem(item);
            else if( xmlr.isEndElement() && ( XML_NAME.equals(name) || XML_NAME_RS1.equals(name) || XML_NAME_RS2.equals(name) || XML_NAME_RS3.equals(name) || XML_NAME_RS4.equals(name)))
               return true;
            else
               return false; //unexpected content of the buffer tag
         }
         throw new FndException("FNDPRFBUFUNEXPENDOFDOC: Error while parsing XML: Unexpected end of XML document - didn't find buffer end tag.");
      }
      else
         return false;
   }

   /**
    * Convert this ProfileBuffer to XML. Called from ProfileUtils.toXML().
    */
   void toXML( AutoString out, int level ) throws FndException
   {
      if(DEBUG) debug("ProfileBuffer.toXML("+level+")");

      ProfileUtils.indent(out, level);
      out.append("<",XML_NAME);
      if(level==0)
      {
         out.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",ProfileUtils.NL);
         out.append("   xmlns:ifsrecord=\"urn:ifsworld-com:ifsrecord\"",ProfileUtils.NL);
         out.append("   xmlns=\"urn:ifsworld-com:schemas:profile_buffer\"");
      }
      out.append(">",ProfileUtils.NL);

      for(int i=0; i<countItems(); i++)
      {
         ProfileItem item = (ProfileItem)getItem(i);
         //out.append( item.toXML(out, level+1) );
         item.toXML(out, level+1);
      }

      ProfileUtils.indent(out, level);
      out.append("</",XML_NAME,">",ProfileUtils.NL);
   }

   //==========================================================================
   //  Sorting
   //==========================================================================

   public void sort( Comparator comparator ) throws FndException
   {
      try
      {
         Arrays.sort(items, 0, count, comparator);
      }
      catch( ClassCastException x )
      {
         throw new FndException(x);
      }
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   private static void debug( String line )
   {
      ProfileUtils.debug(line);
   }

   private static void debugWithDump( String line )
   {
      ProfileUtils.debug(line);
      if(DUMP_STACK)
      {
         ProfileUtils.debug("\n");
         ASPLog.dumpStack();
      }
   }

   /**
    * Return the header of this Buffer.
    * Header is normally not used - overriden here for better debugging only.
    */
   public String getHeader()
   {
      return Str.nvl(super.getHeader(), "") + (dirty ? "[*]" : "");
   }
}

