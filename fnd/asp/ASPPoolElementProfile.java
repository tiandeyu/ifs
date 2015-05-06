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
 * File        : ASPPoolElementProfile.java
 * Description : Profile info (properties) for a page element
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Artur K  2000-Nov-23 - New class instead of old ASPPageElementProfile
 *    Piotr Z
 *    Jacek P  2004-Nov-11 - Added new methods due to new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/16 11:56:40  japase
 * First changes for support of the new profile concept.
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import java.io.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;


/**
 * This class encapsulates profile information (properties)
 * for a specific target (ASPPoolElement).
 * There are three types of instances of this class.
 * Default (base) profiles are defined by Java code and are stored in the page pool.
 * They are never changed.
 * User profiles are stored in the profile cache (and in the database). Each user
 * can modify its own instances.
 */
abstract class ASPPoolElementProfile implements Serializable
{
   /**
    * Enable debugging of this class.
    */
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPoolElementProfile");

   /**
    * Create new instance of the same class.
    */
   protected abstract ASPPoolElementProfile newInstance();

   /**
    * Initiate the instance with design-time Meta-data.
    */
   protected abstract void construct( ASPPoolElement template );

   /**
    * Clone this instance.
    */
   public abstract Object clone();

   /**
    * Return 'true' if this instance represents the same Meta-data as the given one,
    * 'false' otherwise.
    */
   public abstract boolean equals( Object obj );

   /**
    * Create a Buffer with profile information from an existing ASPPoolElement.
    */
   protected abstract void save( ASPPoolElement target, Buffer dest ) throws FndException;

   /**
    * Apply profile information stored in the given Buffer on an ASPPoolElement.
    */
   protected abstract void load( ASPPoolElement target, Buffer sorce ) throws FndException;

   //--------------------------------------------------------------------------
   // new profile handling
   //--------------------------------------------------------------------------

   /**
    * Deserialize profile information in a given string to a Buffer
    * without needing to access any run-time objects.
    * Used for conversion between the old and new profile framework.
    * Use ProfileBuffer class as Buffer implementation.
    * Set state of each simple item (instance of ProfileItem to QUERIED).
    */
   protected abstract void parse( ProfileBuffer buffer, BufferFormatter fmt, String text ) throws FndException;

   /**
    * Store reference to profile sub-buffer containing all profile
    * information corresponding to this instance.
    */
   protected abstract void assign( ASPPoolElement target, ProfileBuffer buffer ) throws FndException;

   /**
    * Return reference to sub-buffer containing profile information
    * corresponding to the current instance
    */
   protected abstract ProfileBuffer extract( ASPPoolElement target ) throws FndException;

   /**
    * Synchronize the internal state with the content of the ProfileBuffer.
    */
   protected abstract void refresh( ASPPoolElement target ) throws FndException;

   //--------------------------------------------------------------------------
   // old profile handling - obsolete; should be removed
   //--------------------------------------------------------------------------

   /**
    * Deserialize profile information in a given string and apply to an ASPPoolElement.
    * @deprecated
    */
   protected abstract void parse( ASPPoolElement target, String text ) throws FndException;

   /**
    * Serialize profile information from a given ASPPoolElement to a string.
    * @deprecated
    */
   protected abstract String format( ASPPoolElement target ) throws FndException;

   //--------------------------------------------------------------------------
   // debugging
   //--------------------------------------------------------------------------

   /**
    * Show profile data corresponding to this instance. Used for debugging.
    */
   void showContents( AutoString out ) throws FndException
   {
      out.append("   ",this.toString(),"\n");
   }
}
