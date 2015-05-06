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
 * File        : CommandBarProfile.java
 * Description : User profile for ASPCommandBar
 * Notes       :
 * ----------------------------------------------------------------------------
 * New Comments:
 *               2008/01/18           sadhlk
 * Bug Id: 69198, Added Custom Command-Bar Buttons functionality.
 *               2006/05/15           riralk
 * Bug Id: 57838, Improve structure in profile. Added method indentBufferOnCmds().
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/15 09:52:40  rahelk
 * added deprecated comment to inherited deprecated methods
 *
 * Revision 1.3  2005/06/28 10:56:10  rahelk
 * Bug corrected with commands removed in adjust
 *
 * Revision 1.2  2005/06/16 10:38:47  rahelk
 * CSL 2: survive profile setting when new items are added to the page
 *
 * Revision 1.1  2005/05/17 10:46:52  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

class CommandBarProfile extends ASPPoolElementProfile
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.CommandBarProfile");
 
   // constances for new profile handling
   private static final String ENABLED   = ProfileUtils.ENTRY_SEP + "Enable";   

   private int cust_cmd_count;       // number of custom commands
   private String  cmd_id[];    
   private boolean cmd_enabled[];    
   
   // profile contents
   private ProfileBuffer profbuf;
   
   
   protected CommandBarProfile()
   {
   }

   protected ASPPoolElementProfile newInstance()
   {
      return new CommandBarProfile();
   }

   protected void construct(ASPPoolElement template)
   {
      ASPCommandBar bar = (ASPCommandBar)template;
      
      //Vector cmds = bar.getCustomCommands();
      Vector cmds = bar.getAllCustomCommands();
      int cmds_size = cmds.size();
      
      cust_cmd_count = 0;
      cmd_id      = new String[cmds_size];
      cmd_enabled = new boolean[cmds_size];
      
      for (int i=0; i<cmds_size; i++)
      {
         ASPCommandBarItem item = (ASPCommandBarItem)cmds.elementAt(i);
         
         if (item.checkRemoved() || item.isUserDisabled() || Str.isEmpty(item.getName()) || item.isCustomCommandBarButton()) continue;
         //if (Str.isEmpty(item.getName())) continue;
         
         cmd_id[cust_cmd_count] = item.getCommandId();
         cmd_enabled[cust_cmd_count] = !item.isUserDisabled();
         
         cust_cmd_count++;
      }
      
   }

   public Object clone()
   {
      CommandBarProfile prf = new CommandBarProfile();
      
      synchronized(this)
      {
         prf.cust_cmd_count = cust_cmd_count;
         prf.cmd_id = new String[cust_cmd_count];
         prf.cmd_enabled = new boolean[cust_cmd_count];
         
         for (int i=0; i<cust_cmd_count; i++)
         {
            prf.cmd_id[i] = cmd_id[i];
            prf.cmd_enabled[i] = cmd_enabled[i];
         }
         prf.profbuf = profbuf; // point out the same ProfileBuffer ?
      }
      return prf;
   }
   
   public boolean equals( Object obj )
   {
      if( obj instanceof CommandBarProfile )
      {
         CommandBarProfile p = (CommandBarProfile)obj;
         
         if (cust_cmd_count != p.cust_cmd_count) return false;
         
         for (int i=0; i<cust_cmd_count; i++)
         {
            if (cmd_id[i] != p.cmd_id[i]) return false;
            if (cmd_enabled[i] != p.cmd_enabled[i]) return false;
         }
         return true;
      }
      return false;
   }
   
   protected void assign(ASPPoolElement target, ProfileBuffer buffer) throws ifs.fnd.service.FndException 
   {
      profbuf = buffer;
      updateMembers(target);
   }
   
   protected ProfileBuffer extract(ASPPoolElement target) throws ifs.fnd.service.FndException
   {
      updateProfileBuffer(target);
      return profbuf;
   }
   
   protected void refresh(ASPPoolElement target) throws ifs.fnd.service.FndException
   {
      updateMembers(target); 
   }

   
   /**
    * Apply profile information stored in the given Buffer on an ASPPoolElement.
    * Called from ASPCommandBar.saveProfile() to save user changes.
    */
   protected void load(ASPPoolElement target, ifs.fnd.buffer.Buffer source) throws ifs.fnd.service.FndException
   {
      try
      {
         ASPCommandBar bar = (ASPCommandBar)target;
         if(DEBUG) bar.getASPManager().newASPBuffer(source).traceBuffer("CommandBarProfile.load()");
         int item_count = source.countItems();
         cust_cmd_count = item_count;
         
         cmd_id = new String[cust_cmd_count];
         cmd_enabled = new boolean[cust_cmd_count];
         
         for( int i=0; i<item_count; i++ )
         {
            Buffer col = source.getBuffer(i);
            String id = col.getString("CMD_ID");
            boolean enabled = "Y".equals(col.getString("CMD_ENABLED"));
            
            cmd_id[i] = id;
            cmd_enabled[i] = enabled;
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDCMDBARPROFLOAD: Cannot load profile for ASPCommandBar: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
      
   }

   /**
    * Create a Buffer with profile information from an existing ASPPoolElement.
    * Called from ASPCommandBar.getProfile() to support user interface.
    */
   protected void save(ASPPoolElement target, ifs.fnd.buffer.Buffer dest) throws ifs.fnd.service.FndException 
   {
      try
      {
         ASPCommandBar bar = (ASPCommandBar)target;
         //Vector cmds = bar.getCustomCommands();
         Vector cmds = bar.getAllCustomCommands();
         
         int all_count = cmds.size();
         
         for (int i=0; i<all_count; i++)
         {
            ASPCommandBarItem item = (ASPCommandBarItem)cmds.elementAt(i);
            
            if (item.checkRemoved() || item.isUserDisabled() || Str.isEmpty(item.getName()) || item.isCustomCommandBarButton()) continue;
            
            Buffer col = dest.newInstance();    
            String cmd_id = item.getCommandId();
            col.addItem("CMD_ID",cmd_id);
            col.addItem("CMD_NAME",item.getName());
            int index = findCommandIndex(cmd_id);
            
            if (index > -1)
               col.addItem("CMD_ENABLED", cmd_enabled[index]? "Y" : "N");
            else
               col.addItem("CMD_ENABLED", "Y" );
            
            dest.addItem("DATA",col);
         }

         RowComparator comp = new RowComparator(bar.getASPManager().getServerFormatter(),"CMD_NAME",true);
         Buffers.sort(dest,comp);

         if(DEBUG)
            bar.getASPManager().newASPBuffer(dest).traceBuffer("CommandBarProfile.save()");
      }
      catch( Throwable any )
      {
         throw new FndException("FNDCMDBARPROFSAV: Cannot save profile for ASPCommandBar: (&1)",any.getMessage())
         .addCaughtException(any);
      }
   }
   
   boolean isCommandEnabled(String cmdId)
   {
      for (int i=0; i<cust_cmd_count; i++)
         if (cmdId.equals(this.cmd_id[i])) return cmd_enabled[i];

      return true;
   }
   
   private int findCommandIndex(String cmd_id)
   {
      for (int i=0; i<cust_cmd_count; i++)
      {
         if (cmd_id.equals(this.cmd_id[i])) return i;
      }
      return -1;
   }
   
   /**
  * This method creates a buffer containing nested items for each field (as shown below) using the buffer received 
  * in the assign() method which has a flat structure. 
  *  
  *   0:$CMD01=:
  *      !
  *      0:$^Enabled=Y  
  *   1:$CMD02=:
  *      !
  *      0:$^Enabled=N    
  *   2:$CMD03=
  *   ...
  */   
   private ProfileBuffer indentBufferOnCmds(ProfileBuffer buffer) throws FndException
   {
       if (buffer==null) 
          return null;
       else
       {           
           ProfileBuffer nestedbuf = ProfileUtils.newProfileBuffer();           
           for (int i=0; i<buffer.countItems(); i++)
           {
               ProfileItem item = (ProfileItem)buffer.getItem(i);               
               String item_name = item.getName();
               Object item_value = item.getValue();
               String field_name = item_name.substring(0,item_name.indexOf(ProfileUtils.ENTRY_SEP));               
               String property_name = item_name.substring(item_name.indexOf(ProfileUtils.ENTRY_SEP));
               ProfileUtils.findOrCreateNestedItem(nestedbuf, field_name+"/"+property_name).setValue(item_value);               
           }
           return nestedbuf;
       }            
   }
   
   private void updateMembers( ASPPoolElement target ) throws FndException
   {
      if(DEBUG) ProfileUtils.debug("CommandBarProfile.updateMembers():",profbuf);

      ASPCommandBar bar = (ASPCommandBar)target;
      Vector cmds = bar.getAllCustomCommands();
      
      int cmds_size = cmds.size();
      
      cust_cmd_count = 0;
      cmd_id      = new String[cmds_size];
      cmd_enabled = new boolean[cmds_size];
      
      for (int i=0; i<cmds_size; i++)
      {
         ASPCommandBarItem item = (ASPCommandBarItem)cmds.elementAt(i);
         
         if (item.checkRemoved() || item.isUserDisabled() || Str.isEmpty(item.getName()) || item.isCustomCommandBarButton()) continue;
         //if (Str.isEmpty(item.getName())) continue;
         
         String cmdId = item.getCommandId();
         
         cmd_id[cust_cmd_count] = cmdId;
         
         //IMPORTANT: must call indentBufferOnCmds() to get the desired buffer structure
         ProfileBuffer nestedbuf = indentBufferOnCmds(profbuf);
         Item prof_item = nestedbuf.findItem(cmdId);
         
         if (prof_item == null)
            cmd_enabled[cust_cmd_count] = true;
         else
         {
            Buffer  buf     = prof_item.getBuffer();
            boolean enabled = "Y".equals(buf.getString(ENABLED)); 
            
            cmd_enabled[cust_cmd_count] = enabled;
         }
         
         cust_cmd_count++;
      }
      
      
      /*
      int prf_col_count = profbuf.countItems();
      cust_cmd_count = prf_col_count;
      
      cmd_id = new String[cust_cmd_count];
      cmd_enabled = new boolean[cust_cmd_count];
            
      for( int i=0; i<prf_col_count; i++ )
      {
         Item    item    = profbuf.getItem(i);// itr.next();
         String  name    = item.getName();
         Buffer  buf     = item.getBuffer();
         boolean enabled = "Y".equals(buf.getString(ENABLED)); 
         
         cmd_id[i] = name;
         cmd_enabled[i] = enabled;
      }
       */
   }

   /**
    * Update profile buffer to reflect the current state of this instance
    */
   private void updateProfileBuffer( ASPPoolElement target )  throws FndException
   {
      if(DEBUG) debug("ASPCommandBarProfile.updateProfileBuffer()");
      if( profbuf==null )
         profbuf = ProfileUtils.newProfileBuffer();

      ASPCommandBar bar = (ASPCommandBar)target;     
      
      for (int index=0; index<cust_cmd_count; index++)
      {
         //Buffer   col = ProfileUtils.findOrCreateNestedBuffer( profbuf, cmd_id[index] );
         
         ProfileUtils.findOrCreateNestedItem(profbuf,cmd_id[index]+ENABLED) .setValue(cmd_enabled[index] ? "Y":"N");
         
      }
   }
   
   private void debug( String text )
   {
      Util.debug(text);
   }

   /**
    * Serialize profile information from a given ASPPoolElement to a string.
    * Called from ASPProfile.save() before storing in the database.
    *
    * @deprecated
    */
   protected String format(ASPPoolElement target) throws ifs.fnd.service.FndException 
   {
      return null;
   }

   /**
    * @deprecated
    */
   protected void parse(ASPPoolElement target, String text) throws ifs.fnd.service.FndException {
   }
   
   protected void parse(ProfileBuffer buffer, ifs.fnd.buffer.BufferFormatter fmt, String text) throws ifs.fnd.service.FndException {
   }
   
}
