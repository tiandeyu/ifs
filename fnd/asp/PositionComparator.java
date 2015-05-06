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
 * File        : PositionComparator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-Dec-29 - Created as a separate file.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2007/10/17  03:29:32  sadhlk
 * Merged Bug 67682, Modified findKey()
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.4  2005/07/18 06:45:16  japase
 * Removed method compareOrgPositions() due to usage of new sorting algorithm.
 *
 * Revision 1.3  2005/07/14 07:00:25  japase
 * Now implements Comparator due to new sort algorithm for ProfileBuffers
 *
 * Revision 1.2  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.1.2.1  2005/03/02 10:58:48  riralk
 * Bug fixes for Portal profile.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.1  2004/12/29 14:07:46  japase
 * Splited to separate class files
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import java.text.*;
import java.util.*;


/**
 * An ItemComparator for DATA rows in a Buffer. The Buffer must match
 * the following rules:
 *<pre>
 *  o Every item must be a compound item and it must contain a key item
 *  o If sortOnItemName==false the followig rules hold as well.
 *    o The key is a simple typed item (S,D,N,B)
 *    o All keys in a buffer must have the same type
 *  o If sortOnItemName==true (no-arg constructor) the buffer is sorted based on the item names.
 *</pre>
 */
class PositionComparator implements Comparator //extends RowComparator //implements ItemComparator
{
   private String  keyname;
   private int     keypos;
   private boolean sortOnItemName;
   //private ServerFormatter keyformatter;
   private Collator collator; // for String comparsion

   /**
    * Create a PositionComparator based on the specified item name.
    */
   PositionComparator( String keyname )
   {
      this.keyname = keyname;
      this.sortOnItemName = false; //match the complete item name
      this.keypos  = -1;
   }

    /**
    * Create a PositionComparator based on the specified item name which starts with 'keyname'.
    */
   PositionComparator( )
   {
      //this.keyname = keyname;
      this.collator = Collator.getInstance(); // for the current default locale

      this.sortOnItemName=true;
      this.keypos  = -1;
   }

   /**
    * Compare two compund items using the key name specified at construction time.
    * The value is always assumed to be an integer value.
    * Null or malformed value or missing key attribute is always greater that any other value:
    *<pre>
    * compare(null,x)    ->  1
    * compare(null,null) ->  0
    * compare(x,null)    -> -1
    *</pre>
    */
   //public int compare( Item i1, Item i2 ) throws FndException
   public int compare( Object o1, Object o2 ) throws ClassCastException
   {
      Item i1 = (Item)o1;
      Item i2 = (Item)o2;

      if (sortOnItemName)
      {
          String v1 = i1.getName();
          if (Str.isEmpty(v1))
             v1 = null;
          String v2 = i2.getName();
          if (Str.isEmpty(v2))
             v2 = null;
          /*
          if (v1 == null)
             return v2 == null ? compareOrgPositions(i1, i2) : 1;
          if (v2 == null)
             return v1 == null ? compareOrgPositions(i1, i2) : -1;

          int r = compareNonNulls(v1, v2);

          if (r == 0)
             return compareOrgPositions(i1, i2);
          else
             return r;
          */

          if( v1==null && v2==null )
             return 0;
          else if( v1==null || v2==null )
             return v1==null ? 1 : -1;

          return compareNonNulls(v1, v2);
      }
      else
      {
          Buffer buf1 = i1.getBuffer();
          Buffer buf2 = i2.getBuffer();

          Item k1 = findKey(buf1);
          Item k2 = findKey(buf2);

          int v1 = Integer.MAX_VALUE;
          int v2 = Integer.MAX_VALUE;

          if( k1!=null ) try { v1 = k1.getInt(); } catch( NumberFormatException x ) {}
          if( k2!=null ) try { v2 = k2.getInt(); } catch( NumberFormatException x ) {}

          //return v1==v2 ? compareOrgPositions(i1, i2) : ( v1<v2 ? -1 : 1 );
          return v1==v2 ? 0 : ( v1<v2 ? -1 : 1 );
      }
   }

   private int compareNonNulls( String v1, String v2 )
   {
      //String s1 = (String) keyformatter.parse(v1, DataFormatter.STRING);
      //String s2 = (String) keyformatter.parse(v2, DataFormatter.STRING);
      return collator.compare(v1, v2);
   }


   /* Not necessary any longer because Arrays.mergeSort
      doesn't change the order of the equals items
   private int compareOrgPositions( Item i1, Item i2 )
   {
      Buffer b1 = i1.getBuffer();
      Buffer b2 = i2.getBuffer();
      return b1.getInt(b1.countItems() - 1) - b2.getInt(b2.countItems() - 1);
   }
   */

   private Item findKey( Buffer buf )
   {
      Item keyitem = buf.getItem(keypos);
      if( keyitem != null && keyname.equalsIgnoreCase(keyitem.getName()) )
         return keyitem;
      keypos = buf.getItemPosition(keyname);
      if(keypos <0)
         keypos = buf.getItemPosition(keyname.toUpperCase());
      return keypos<0 ? null : buf.getItem(keypos);
   }
}

