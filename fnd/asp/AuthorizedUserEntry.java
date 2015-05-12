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
 * File        : AuthorizedUserEntry.java
 * Description : 
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2001-Sep-13 - Created.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import ifs.fnd.service.*;
/**
 *
 */
class AuthorizedUserEntry
{
   private String user_id;
   private String sec_key;
   private long timestamp = Util.now();

   private AuthorizedUserEntry prev;
   private AuthorizedUserEntry next;
   
   
   //==========================================================================
   //  Construction
   //==========================================================================
   
   AuthorizedUserEntry( String user_id, String sec_key)
   {
      this.user_id = user_id;
      this.sec_key = sec_key;
   }

   //==========================================================================
   //  Other routines
   //==========================================================================
   
   String getUserId()
   {
      return user_id;
   }

   String getSecurityKey()
   {
      return sec_key;
   }

   void setTimestamp()
   {
      timestamp = Util.now();
   }
   
   boolean isValid( long timeout )
   {
      return timestamp + timeout*1000 > Util.now();
   }
   
   void setPrevious( AuthorizedUserEntry prev )
   {
      this.prev = prev;
   }
   
   AuthorizedUserEntry getPrevious()
   {
      return prev;
   }
   
   void setNext( AuthorizedUserEntry next )
   {
      this.next = next;
   }
   
   AuthorizedUserEntry getNext()
   {
      return next;
   }
}