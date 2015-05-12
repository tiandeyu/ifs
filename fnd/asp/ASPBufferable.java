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
 * File        : ASPBufferable.java
 * Description : An interface used for conversion into/from an ASPBuffer
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-May-28 - Created
 *    Jacek P  1998-Aug-07 - Removed 'throws Exception'
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

/**
 * An ASPObject that implements ASPBufferable interface can transform/recreate
 * its internal state into/from an ASPBuffer.
 */
public interface ASPBufferable
{
   /**
    * Store the internal state of this ASPBufferable object
    * in a specified ASPBuffer
    */
   public void save( ASPBuffer into );

   /**
    * Retrieve the internal state of this ASPBufferable object
    * from a specified ASPBuffer
    */
   public void load( ASPBuffer from );
}
