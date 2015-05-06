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
 * File        : IFSCommonMessages.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Mangala P  2003-Jan-01 - Created
 *    Geethika G 2006-Oct-31 - Modified to read the data from the Database
 *    Buddika H  2006-Dec-08 - Bug id: 61535, Modified load() to expire messages after a specific time.
 *                             Added method resetTimeout() to cleat cache time.
 *    Buddika H  2006-Dec-18 - Improvements to common message cache management code.
 *    Buddika H  2007-Jan-05 - Changed common messages name to bradcast messages.
 *		Buddika H  2007-Apr-11 - Bug id 64118, Modified load() to create a new message set everytime cache is cleared.
 *		Buddika H  2007-May-04 - Bug id 65098, Modified MAX_LENGTH, getValidMessages() to improve broadcast messages.
 *    Buddika H  2007-Jul-04 - Bug id 66454, Improved performance by Modifying Load() and all calls to load() method.
 * ------------------------------------------------------------------------------------------------------
 * New Comments:
 * 2008/07/09 sadhlk Bug 73745, Added code to check DEBUG condition before calling debug() method.
 *
 * ------------------------------------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import java.io.*;
import java.util.*;
import java.text.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

public class IFSCommonMessages 
{
   public static final int MAX_LENGTH       = 120;

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.IFSCommonMessages");
   
   public static HashSet messages;
   public static boolean loaded;
   
   private static Calendar expire_time = null;
   
   //=====================================================================
   // Private utility methods.
   //=====================================================================

   private static synchronized boolean load(ASPManager mgr)
   {
      Calendar now = new GregorianCalendar();
      if (!loaded || expire_time.getTimeInMillis()<now.getTimeInMillis())
      {
         try 
         {
            String SELECT_STMT =   "SELECT MESSAGE,to_char(EFFECTIVE_FROM,'YYYY.MM.DD HH24:MI:SS') as EFFECTIVE_FROM, "+
                                   "to_char(EXPIRES_ON,'YYYY.MM.DD HH24:MI:SS') as EXPIRES_ON "+
                                   "FROM COMMON_MESSAGES "+
                                   "WHERE COMMON_MESSAGES_API.Is_Message_Expired(MESSAGE_ID)='FALSE'";
            ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();                
            ASPQuery q = trans.addQuery("GET_COMMON_MESSAGES",SELECT_STMT);
            trans = mgr.perform(trans);

            ASPBuffer buf = trans.getBuffer("GET_COMMON_MESSAGES");
            int count=0;
            messages = new HashSet();
            if (buf!=null)
            {
               try
               {
                  count = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
                  if(DEBUG) Util.debug(" Number records collected " + count);
               }
               catch(Exception e)
               {
                     //Util.debug("IFSCommonMessages: No Broadcast Messages were found");
                  Alert.add("IFSCommonMessages: No Broadcast Messages were found");
                  count = 0;
               }
            }     

            for (int i=0; i<count; i++)
            {  
               ASPBuffer b = buf.getBufferAt(i);                    
               String message =  b.getValue("MESSAGE");
               String start_date_str = b.getValue("EFFECTIVE_FROM");
               String end_date_str = b.getValue("EXPIRES_ON");
               SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
               Date start_date = df.parse(start_date_str);
               Date end_date = df.parse(end_date_str);
            
               IFSMessage msg = new IFSMessage(message,start_date,end_date,"","");
               messages.add(msg);
            }
            loaded = true;
            expire_time = new GregorianCalendar();
            if(expire_time!=null)
               expire_time.add(Calendar.MINUTE, Integer.parseInt(mgr.getConfigParameter("IFS_BROADCAST_MESSAGES/MESSAGE_TIMEOUT","30")));
            return messages!=null;
         } 
         //for the safe. Catch any exception and return false.
         catch (Exception any) 
         {
            return false;
         }
      }
      return true;
   }

   //=====================================================================
   // Public APIs
   //=====================================================================

   /*
    * Reset Cache timeout
    */
   public static void resetTimeout()
   {
      loaded = false;
   }
   
   public static String getValidMessages(ASPManager mgr)
   {
      //String filename = mgr.getASPConfig().getCommonMsgFilePath();
      Calendar now = new GregorianCalendar();
      if (!loaded || expire_time.getTimeInMillis()<now.getTimeInMillis())
      {
         load(mgr);
      }
      
      if (!loaded)
      { 
         mgr.getASPLog().debug("Error loading messages!"); //No need to traslate. Just to debug if error occurs.
         return "";
      }
      if (messages == null) return "";

      try
      {
         AutoString valid_messages = new AutoString();
         AutoString alert_messages = new AutoString();
         boolean msg_too_long = false;
         
         Iterator itr = messages.iterator();
         while (itr.hasNext())
         {
            IFSMessage msg = ((IFSMessage)itr.next());
            if(msg.isActive())
            {
               String valid_msg = mgr.HTMLEncode (msg.getMessage());
               valid_messages.append(valid_msg,"^");
            }
            else if (msg.isExpired())
               itr.remove();
         }
         
         String message_str = valid_messages.toString();
            return message_str;
      }
      catch( Exception e)
      {
         mgr.getASPLog().debug("Error: " + e);
         return "";
      }
   }

   public static HashSet getMessageStructure(ASPManager mgr)
   {
      Calendar now = new GregorianCalendar();
      if (!loaded || expire_time.getTimeInMillis()<now.getTimeInMillis())
      {
         load(mgr);
      }
      if (!loaded) return null;
      return messages;
   }

   public static boolean hasValidMessages(ASPManager mgr)
   {
      try
      {
         Calendar now = new GregorianCalendar();
         if (!loaded || expire_time.getTimeInMillis()<now.getTimeInMillis())
         {
            load(mgr);
         }
         if (!loaded && messages == null) return false;
         Iterator itr = messages.iterator();
         while (itr.hasNext())
         {
               IFSMessage msg = ((IFSMessage)itr.next());
               if(msg.isActive())
                  return true;
         }
         return false;
      }
      catch (RuntimeException e)
      {
         return false;
      }

   }

   //=====================================================================
   // Inner class IFSMessage
   //=====================================================================


   static public class IFSMessage
   {
   
      private String message;
      private Date start;
      private Date expire;
      private String owner;
      private String target_group;


      IFSMessage(String message, Date start, Date expire, String owner, String group)
      {
         this.message = message;
         this.start = start;
         this.expire = expire;
         this.owner = owner;
         this.target_group = group;
      }

      public boolean isActive()
      {
         Date now = new Date();
         return (now.compareTo(start)>0  && now.compareTo(expire)<0 );
      }

      public boolean isExpired()
      {
         return (new Date().compareTo(expire)>0);
      }

      public String getMessage()
      {
         return message;
      }

      public Date getStartTime()
      {
         return this.start; 
      }

      public Date getEndTime()
      {
         return this.expire; 
      }

   }
}
