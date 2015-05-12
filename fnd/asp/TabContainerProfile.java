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
 * File        : TabContainerProfile.java
 * Description : User profile for ASPTabContainer
 * Notes       : Dushmantha D. 2008/09/24 Created             
 * ----------------------------------------------------------------------------
 * New Comments:
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

import ifs.fnd.service.FndException;

public class TabContainerProfile extends ASPPoolElementProfile
{   
   private static final String ENABLED   = ProfileUtils.ENTRY_SEP + "Enable";
   private ProfileBuffer profbuf;
   private Buffer buffer;
   
   protected TabContainerProfile() {}
   
   protected ASPPoolElementProfile newInstance()
   {
      return new TabContainerProfile();
   }
   
   protected void construct(ASPPoolElement template)
   {
      ASPTabContainer tabc = (ASPTabContainer)template;
   }
   
   public Object clone()
   {
      TabContainerProfile prf = new TabContainerProfile();
      synchronized(this)
      {
         prf.profbuf = profbuf;
      }
      return prf;
   }
   
   public boolean equals( Object obj )
   {
      if( obj instanceof TabContainerProfile )
      {
         TabContainerProfile p = (TabContainerProfile)obj;
         return true;
      }
      return false;
   }
 
   protected void assign(ASPPoolElement target, ProfileBuffer buffer) throws ifs.fnd.service.FndException 
   {
      profbuf = buffer;
   }

   protected ProfileBuffer extract(ASPPoolElement target) throws ifs.fnd.service.FndException
   {
      if( profbuf==null )
         profbuf = ProfileUtils.newProfileBuffer();

      ASPTabContainer tabc = (ASPTabContainer)target;
      
      for(int i=0;i<tabc.getTabCount();i++)
      {
          if(buffer.getItem(i)==null) break; 
          
          Buffer tmp = buffer.getItem(i).getBuffer();        
       
          String containername = tmp.getItem("TAB_CONTAINER_NAME").getValue().toString();
          String tabid = tmp.getItem("TAB_ID").getValue().toString();
          String isvisible = tmp.getItem("ISTABVISIBLE").getValue().toString();
          String tabpos = tmp.getItem("TAB_POSITION").getValue().toString();
          
          ProfileUtils.findOrCreateNestedItem(profbuf,tabid+ProfileUtils.ENTRY_SEP+"Visible").setValue(isvisible);                                            
          ProfileUtils.findOrCreateNestedItem(profbuf,tabid+ProfileUtils.ENTRY_SEP+"Position").setValue(tabpos);                                            
      }                  
      
      return profbuf;
   }
   
   protected void refresh(ASPPoolElement target) throws ifs.fnd.service.FndException
   {
   }
   
   protected void load(ASPPoolElement target, ifs.fnd.buffer.Buffer source) throws ifs.fnd.service.FndException
   {
      try
      {
         ASPTabContainer tabc = (ASPTabContainer)target;
         buffer = source;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDTABCONTAINERPROFLOAD: Cannot load profile for ASPTabContainer: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }
   
   protected void save(ASPPoolElement target, ifs.fnd.buffer.Buffer dest) throws ifs.fnd.service.FndException 
   {
      try
      {
         ASPTabContainer tabc = (ASPTabContainer)target;
         
         int tabcount = tabc.getTabCount();
         int i=0;
         
         for(i=0;i<tabcount;i++)
         {
            Buffer col = dest.newInstance();
            col.addItem("TAB_ID",tabc.getTabId(i));
            col.addItem("TAB_NAME",tabc.getTabLabel(i));

            if(profbuf==null)
            {
               col.addItem("ISTABVISIBLE",tabc.isTabRemoved(i)?"N":"Y");
               col.addItem("TAB_POSITION",i+"");               
            }
            else
            {
               Object tmp = ProfileUtils.findOrCreateNestedItem(profbuf,tabc.getTabId(i)+ProfileUtils.ENTRY_SEP+"Visible").getValue();
               String val = "Y";
             
               if(tmp==null)
                  ProfileUtils.findOrCreateNestedItem(profbuf,tabc.getTabId(i)+ProfileUtils.ENTRY_SEP+"Visible").setValue(val);
               else
                  val = tmp.toString();

               Object tmp2 = ProfileUtils.findOrCreateNestedItem(profbuf,tabc.getTabId(i)+ProfileUtils.ENTRY_SEP+"Position").getValue();                              
               String val2 = i+"";
               
               if(tmp2==null)
                  ProfileUtils.findOrCreateNestedItem(profbuf,tabc.getTabId(i)+ProfileUtils.ENTRY_SEP+"Position").setValue(val2);
               else
                  val2 = tmp2.toString();
               
               
               col.addItem("ISTABVISIBLE",val);
               col.addItem("TAB_POSITION",val2);
            }            
            col.addItem("TAB_CONTAINER_NAME",tabc.getName());             
            dest.addItem("DATA",col);
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDTABCONTAINERPPROFSAV: Cannot save profile for ASPTabContainer: (&1)",any.getMessage())
         .addCaughtException(any);
      }
   }
   
   /**
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