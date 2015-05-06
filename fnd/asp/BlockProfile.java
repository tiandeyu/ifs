
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
 * File        : BlockProfile.java
 * Description : User profile for saved queries
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H  2002-12-04 - Created
 *    ChandanaD 2003-01-08 - Added tokenize() methods and changed parse() methods to call tokenize().
 *    Jacek P  2004-Nov-11 - Introduced API due to new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/03/10 sumelk - Bug 89421, Changed templateSave() & templateLoad() to avoid the errors with
 *                     templates which have no description.
 * 2010/03/08 sumelk - Bug 89142, Changed fieldLoad() to save the template field list correctly.
 * Revision 1.6  2009/02/13 14:14:02  buhilk
 * Bug id 80265, F1PR454 - Templates IID.
 *               2006/05/15           riralk
 * Bug Id: 57838, Improve structure in profile. Added method indentBufferOnFields().
 *
 *               2006/01/25           mapelk
 * Removed errorneous translation keys
 *
 * Revision 1.3  2005/11/17 10:18:02  japase
 * Corrected bug in clone()
 *
 * Revision 1.2  2005/11/17 09:15:30  japase
 * Improved cloning and error handling.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.8  2005/07/06 11:05:31  rahelk
 * CSL: added get defaults functionality to "defaults" tab
 *
 * Revision 1.7  2005/07/05 13:28:33  riralk
 * Fixed deleteQuery problem.
 *
 * Revision 1.6  2005/06/28 11:36:32  japase
 * Corrected conversion of Previous Queries
 *
 * Revision 1.5  2005/06/21 12:39:57  japase
 * Corrected the parse() method used during conversion of profiles
 *
 * Revision 1.4  2005/06/16 10:38:47  rahelk
 * CSL 2: survive profile setting when new items are added to the page
 *
 * Revision 1.3  2005/06/06 07:29:03  rahelk
 * Restructured BlockProfile to handle both queries and default values
 *
 * Revision 1.2  2005/02/03 12:40:37  riralk
 * Adapted BlockProfile (saved queries) functionality to new profile changes.
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

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;

class BlockProfile extends ASPPoolElementProfile
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.BlockProfile");

   static final String TEMPLT_SECTION      = "Templates";
   static final String TEMPLT_TITLE        = ProfileUtils.ENTRY_SEP + "Title";
   static final String TEMPLT_DESC         = ProfileUtils.ENTRY_SEP + "Description";
   static final String TEMPLT_ENTITY       = ProfileUtils.ENTRY_SEP + "Entity";
   static final String TEMPLT_DEFAULT      = ProfileUtils.ENTRY_SEP + "Default";
   static final String TEMPLT_PATH         = ProfileUtils.ENTRY_SEP + "Path";
   static final String TEMPLT_BLOCK        = ProfileUtils.ENTRY_SEP + "Block";
   static final String TEMPLT_ID           = ProfileUtils.ENTRY_SEP + "TemplateID";
   static final String TEMPLT_GLOBAL_ID    = ProfileUtils.ENTRY_SEP + "GlobalID";
   
   private static final String FIELD_SECTION  = "FIELD_SECTION";   
   private static final String DEF_VALUE      = ProfileUtils.ENTRY_SEP + "DefaultValue";
   //private static final String RO_VALUE       = "ReadOnly"+ ProfileUtils.ENTRY_SEP + "Field Node";

   private static final String QUERY_SECTION  = "QUERY_SECTION";
   private static final String SAVED_QUERY    = "Saved Query" + ProfileUtils.ENTRY_SEP;
   private static final String PREVIOUS_QUERY = "Previous Query" + ProfileUtils.ENTRY_SEP;

   //private Buffer field_buffer;
   private int    fld_count;
   private String field_name[];
   private String default_value[];

   private String  global_template_id;
   private String  template_id;
   private boolean template_defailt;
   private String  template_title;
   private String  template_desc;
   private String  template_entity;
   private boolean update_template_only;

   private Buffer query_buffer;
   private Buffer prof_buff; // place holder buffer for profile sturcture query_buffer and field_buffer

   private ProfileBuffer profile_buffer;


   protected BlockProfile()
   {
   }

   protected void construct( ASPPoolElement template )
   {
      ASPBlock block = (ASPBlock) template;
      ASPManager mgr = block.getASPManager();

      profile_buffer = ProfileUtils.newProfileBuffer();

      query_buffer = mgr.newASPBuffer().getBuffer();
      query_buffer.addItem(PREVIOUS_QUERY+mgr.translate("FNDBLOCKPROPREQRY: Previous Query"),"__EMPTY");

      ASPField[] fields = block.getFields();
      field_name    = new String[fields.length];
      default_value = new String[fields.length];
      template_defailt = false;
      fld_count = 0;

      for (int i=0; i<fields.length; i++)
      {
         ASPField fld = fields[i];
         if (fld.isHidden() || (fld.isReadOnly() && !fld.isInsertable())) continue;

         field_name[fld_count]  = fld.getName();
         default_value[fld_count] = "";

         fld_count++;
      }

   }

   protected ASPPoolElementProfile newInstance()
   {
      return new BlockProfile();
   }

   public Object clone()
   {
      if(DEBUG) debug(this+": BlockProfile.clone()");

      BlockProfile prf = new BlockProfile();

      prf.fld_count     = fld_count;

      prf.field_name    = new String[fld_count];
      prf.default_value = new String[fld_count];

      for( int i=0; i<fld_count; i++ )
      {
         prf.field_name[i]    = field_name[i];
         prf.default_value[i] = default_value[i];
      }

      prf.global_template_id = global_template_id;
      prf.template_id = template_id;
      prf.template_defailt = template_defailt;
      prf.template_title = template_title;
      prf.template_desc = template_desc;
      prf.template_entity = template_entity;

      prf.query_buffer   = query_buffer   == null ? null : (Buffer)query_buffer.clone();
      prf.prof_buff      = prof_buff      == null ? null : (Buffer)prof_buff.clone();
      prf.profile_buffer = profile_buffer == null ? null : (ProfileBuffer)profile_buffer.clone();

      return prf;
   }

   protected void load( ASPPoolElement target, Buffer source ) throws FndException
   {
   }

   /**
    * Apply profile information stored in the given Buffer on an ASPPoolElement.
    * Called from ASPBlock.saveProfile() to save user changes.
    */
   protected void queryLoad( ASPPoolElement target, Buffer source ) throws FndException
   {
      ASPBlock block = (ASPBlock) target;
      if(DEBUG) block.getASPManager().newASPBuffer(source).traceBuffer("BlockProfile.queryLoad()");
      query_buffer = block.getASPManager().newASPBuffer().getBuffer();

      for (int i=0; i<source.countItems(); i++)
      {
         Item item = source.getItem(i);
         query_buffer.addItem(item.getName(),item.getString());
      }

      try{
          SimpleComparator comp = new SimpleComparator(true);
          Buffers.sort_(query_buffer,comp);
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILESAVE: Cannot save query profile for ASPBlock: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }

   protected void fieldLoad( ASPPoolElement target, Buffer source ) throws FndException
   {
      try
      {
         ASPBlock block = (ASPBlock) target;
         if(DEBUG) block.getASPManager().newASPBuffer(source).traceBuffer("BlockProfile.fieldLoad()");

         fld_count = source.countItems();

         field_name    = new String[fld_count];
         default_value = new String[fld_count];

         for (int i=0; i<fld_count; i++)
         {
            Buffer col = source.getBuffer(i);

            field_name[i]    = col.getString("NAME");

            ASPField fld = block.getASPField(field_name[i]);
            
            if (!block.getASPManager().isEmpty(col.getString("DEFAULT_VALUE")))
            {    
               Item item = fld.convertToServerItem(col.getString("DEFAULT_VALUE"), null);
               default_value[i] = (String)item.getValue();
            }
            else 
               default_value[i] = "";    
         }
         
         template_defailt = (template_defailt)? true: false;
         if(update_template_only) update_template_only = false;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILELOADFIELD: Cannot load field profile for ASPBlock: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }

   protected void templateLoad( ASPPoolElement target, Buffer source) throws FndException
   {
      try
      {
         ASPBlock block = (ASPBlock) target;
         ASPManager mgr = block.getASPManager();
         if(DEBUG) mgr.newASPBuffer(source).traceBuffer("BlockProfile.templateLoad()");

         Object obj = source.getItem("TEMPLATE_ID").getValue();
         if(obj==null)         
         {
            global_template_id = null;
            template_id = null;
         }
         else
         {
            global_template_id = (String) source.getItem("GLOBAL_TEMPLATE_ID").getValue();
            template_id = (String) source.getItem("TEMPLATE_ID").getValue();
         }
         
         template_defailt  = "true".equals((String) source.getItem("DEFAULT").getValue());
         template_title    = (String) source.getItem("TITLE").getValue();
         template_desc     = (String) source.getItem("DESCRIPTION").getValue();
         if(mgr.isEmpty(template_desc))
            template_desc = "";
         template_entity   = block.getLUName();
         if(mgr.isEmpty(template_entity) && !mgr.isEmpty(block.getView()))
         {
            template_entity = block.searchLUName();
            if(mgr.isEmpty(template_entity))
               template_entity = "";
         }
         update_template_only = true;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILELOADFIELD: Cannot load template profile for ASPBlock: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }
   
   protected void save( ASPPoolElement target, Buffer dest ) throws FndException
   {
   }

   /**
    * Create a Buffer with profile information from an existing ASPPoolElement.
    * Called from ASPBlock.getQueryProfile() to support user interface.
    */
   protected void querySave( ASPPoolElement target, Buffer dest ) throws FndException
   {
      if(DEBUG) debug("BlockProfile.querySave()");

      if(dest==null) throw new FndException("FNDASPBLOCKPROFILEQUERYSAVE1: Destination buffer is null. Cannot save query!");
      try
      {
         for (int i=0; i<query_buffer.countItems(); i++)
         {
            Item item = query_buffer.getItem(i);
            dest.addItem(item.getName(),item.getString());
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILEQUERYSAVE: Cannot save query profile for ASPBlock: (&1)",any.getMessage())
                   .addCaughtException(any);
      }

   }


   protected void fieldSave(ASPPoolElement target, Buffer dest, boolean formatted) throws ifs.fnd.service.FndException
   {
      if(DEBUG) debug("BlockProfile.fieldSave()");
      try
      {
         ASPBlock block = (ASPBlock)target;
         ASPManager mgr = block.getASPManager();

         for (int i=0; i<fld_count; i++)
         {
            Buffer col = dest.newInstance();

            ASPField fld = block.getASPField(field_name[i]);

            col.addItem("NAME",field_name[i]);

            String mask = fld.getTranslatedMask();
            if (!Str.isEmpty(mask))
               mask = " ("+mask+")";
            else
               mask = "";

            col.addItem("LABEL",fld.getLabel()+mask);

            if (!formatted)
               col.addItem("DEFAULT_VALUE", default_value[i]);
            else
               col.addItem("DEFAULT_VALUE", fld.convertToClientString(default_value[i]));

            dest.addItem("DATA",col);
         }

         RowComparator comp = new RowComparator(block.getASPManager().getServerFormatter(),"LABEL",true);
         Buffers.sort(dest,comp);

         if(DEBUG)
            block.getASPManager().newASPBuffer(dest).traceBuffer("BlockProfile.fieldSave()");
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILEFIELDSAVE: Cannot save field profile for ASPBlock: (&1)",any.getMessage())
         .addCaughtException(any);
      }
   }

   protected void templateDetailSave(ASPPoolElement target, Buffer dest, String template, boolean formatted) throws ifs.fnd.service.FndException
   {
      if(DEBUG) debug("BlockProfile.templateDetailSave()");
      try
      {
         ASPBlock block = (ASPBlock)target;
         ASPManager mgr = block.getASPManager();

         Buffer template_ = profile_buffer.findItem(TEMPLT_SECTION+"/"+template).getBuffer();
         if(template_ == null) return;
         template_ = indentBufferOnFields((ProfileBuffer)template_);
         
         for (int i=0; i<fld_count; i++)
         {
            ASPField fld = block.getASPField(field_name[i]);
            String mask = fld.getTranslatedMask();
            mask = (!Str.isEmpty(mask))?" ("+mask+")":"";
            
            Item item = template_.getItem(field_name[i]).getBuffer().findItem("^DefaultValue");
            String value = (item.getValue()==null)?"":(String)item.getValue();
            
            Buffer col = dest.newInstance();
            col.addItem("NAME",field_name[i]);
            col.addItem("LABEL",fld.getLabel()+mask);
            col.addItem("DEFAULT_VALUE", (!formatted)?value:fld.convertToClientString(value));

            dest.addItem("DATA",col);
         }

         RowComparator comp = new RowComparator(block.getASPManager().getServerFormatter(),"LABEL",true);
         Buffers.sort(dest,comp);

         if(DEBUG)
            block.getASPManager().newASPBuffer(dest).traceBuffer("BlockProfile.templateDetailSave()");
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILEFIELDSAVE: Cannot save template field profile for ASPBlock: (&1)",any.getMessage())
         .addCaughtException(any);
      }       
   }
   
   protected void templateDetailNew(ASPPoolElement target, Buffer dest) throws ifs.fnd.service.FndException
      {
      if(DEBUG) debug("BlockProfile.templateDetailNew()");
      try
      {
         ASPBlock block = (ASPBlock)target;
         ASPManager mgr = block.getASPManager();
         
         for (int i=0; i<fld_count; i++)
         {
            ASPField fld = block.getASPField(field_name[i]);
            String mask = fld.getTranslatedMask();
            mask = (!Str.isEmpty(mask))?" ("+mask+")":"";
                        
            Buffer col = dest.newInstance();
            col.addItem("NAME",field_name[i]);
            col.addItem("LABEL",fld.getLabel()+mask);
            col.addItem("DEFAULT_VALUE", "");

            dest.addItem("DATA",col);
         }
         
         RowComparator comp = new RowComparator(block.getASPManager().getServerFormatter(),"LABEL",true);
         Buffers.sort(dest,comp);
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILENEWTEMPLATE: Cannot save new template field profile for ASPBlock: (&1)",any.getMessage())
         .addCaughtException(any);
      }       
   }
   
   protected void templateSave(ASPPoolElement target, Buffer dest) throws ifs.fnd.service.FndException
   {
      if(DEBUG) debug("BlockProfile.templateSave()");
      try
      {
         ASPBlock block = (ASPBlock)target;
         ASPManager mgr = block.getASPManager();
         ASPPage page = block.getASPPage();
         
         Buffer template_section = ProfileUtils.findOrCreateNestedBuffer(profile_buffer, TEMPLT_SECTION);
         if(template_section == null || template_section.countItems()==0) return;
         Buffer tmpl_      = page.readGlobalProfileBuffer(TEMPLT_SECTION,false).getBuffer();
         int count = template_section.countItems();
         for(int t=0; t<count; t++)
         {
            Item tm = template_section.getItem(t);
            if(tm.isCompound())
            {
               String templateId = tm.getName();
               String globalId   = template_section.getItem(templateId+TEMPLT_GLOBAL_ID).getValue().toString();
               String desc       = "";

               String title      = (String) tmpl_.findItem(globalId+TEMPLT_TITLE).getValue();
               Item temp_desc  = tmpl_.findItem(globalId+TEMPLT_DESC);
               if (temp_desc!=null)
                  desc = (String) tmpl_.findItem(globalId+TEMPLT_DESC).getValue();
               String entity     = (String) tmpl_.findItem(globalId+TEMPLT_ENTITY).getValue();
               String _default   = (String) tmpl_.findItem(globalId+TEMPLT_DEFAULT).getValue();
               
               Buffer col = dest.newInstance();
               col.addItem("GLOBAL_TEMPLATE_ID", globalId);
               col.addItem("TEMPLATE_ID", templateId);
               col.addItem("DEFAULT", _default);
               col.addItem("TITLE", title);
               col.addItem("DESCRIPTION", desc);
               col.addItem("ENTITY", entity);               
               
               dest.addItem("DATA",col);
            }
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDASPBLOCKPROFILEFIELDSAVE: Cannot save template profile for ASPBlock: (&1)",any.getMessage())
         .addCaughtException(any);
      }
   }
   
   /**
    * Serialize profile information from a given ASPPoolElement to a string.
    * Called from ASPProfile.save() before storing in the database.
    *
    * @deprecated
    */
   protected String format( ASPPoolElement target ) throws FndException
   {
      /*
      if(DEBUG) debug("BlockProfile.format()");
      String str = "";

      for (int i=0; i<profile_buffer.countItems(); i++)
      {
         Item item = profile_buffer.getItem(i);
         str += item.getName() +(char)30 + item.getString() + (char)29;
      }
      return str;
       */
      return null;
   }

   /**
    * Deserialize profile information in a given string and apply to an ASPPoolElement.
    * Called from ASPProfile.findProfile() after fetching from the database.
    *
    * @deprecated
    */
   protected void parse( ASPPoolElement target, String text ) throws FndException
   {
      if(DEBUG) debug("BlockProfile.parse():\n\t\t"+text);
      ASPBlock block = (ASPBlock) target;
      //profile_buffer = block.getASPManager().newASPBuffer().getBuffer();
      profile_buffer = ProfileUtils.newProfileBuffer();
      tokenize(profile_buffer,text);
   }

   //==========================================================================
   // New profile handling
   //==========================================================================

   /*
      An example of the new profile buffer structure:

      0:$b2e=:
         !
         0:$Pages=:
            !
            1:$demorw.orderheader=:
               !
               0:$Block=:
                  !
                  0:$MAIN=:
                     !
                     0:$FIELD_SECTION=:
                        !
                        0:$COMMENTS_EXISTS=:
                           !
                           0:$DefaultValue^Field Node[Qw+]*
                        1:$DELIVERY_TYPE=:
                           !
                           0:$DefaultValue^Field Node[Qw+]*
                     1:$QUERY_SECTION=:
                        !
                        0:$Previous Query^Previous Query[Qw+]=__CASESS_VALUE^Y?CASESENCETIVE^TRUE?
                        1:$Saved Query^Closed orders[Qw+]=__CASESS_VALUE^Y?CASESENCETIVE^TRUE?STATE^Closed?
                        2:$Saved Query^Open orders[Qw+]=__CASESS_VALUE^Y?CASESENCETIVE^TRUE?STATE^Open?
   */

   /**
    * Inherited interface.
    * Deserialize profile information in a given string to a ProfileBuffer
    * without needing to access any run-time objects.
    * Used for conversion between the old and new profile framework.
    * Set state of each simple item (instance of ProfileItem to QUERIED).
    */
   protected void parse( ProfileBuffer buffer, BufferFormatter fmt, String text ) throws FndException
   {
      if(DEBUG) debug("BlockProfile.parse():\n\t\t"+text);
      buffer.clear();
      //tokenize( buffer, text, "SavedQuery"+ProfileUtils.ENTRY_SEP, true );
      boolean previous_qry = true; //assuming the first query will always be the previous one.
      StringTokenizer st = new StringTokenizer(text,""+(char)29);
      while( st.hasMoreTokens() )
      {
         String token = st.nextToken();
         int pos = token.indexOf(30);
         if(pos<0)
            throw new FndException("FNDASPBLOCKPROFILESYNTAXERR: Syntax error in token '&1'. Expected delimiter (30) not found!",token);
         String name  = token.substring(0,pos);
         String value = token.substring(pos+1);

         Buffer buf = ProfileUtils.findOrCreateNestedBuffer( buffer, QUERY_SECTION);
         ProfileUtils.findOrCreateNestedItem(buf, (previous_qry ? PREVIOUS_QUERY : SAVED_QUERY)+name).setValue(value);
         previous_qry = false;
      }
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Store reference to profile sub-buffer containing all profile
    * information corresponding to this instance.
    */
   protected void assign( ASPPoolElement target, ProfileBuffer buffer ) throws FndException
   {
      // throw new FndException("Profile functionality for class '&1' is not implemented yet", getClass().getName());
      profile_buffer=buffer;
      updateMembers(target);
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Return reference to sub-buffer containing profile information
    * corresponding to the current instance
    */
   protected ProfileBuffer extract( ASPPoolElement target ) throws FndException
   {
      //throw new FndException("Profile functionality for class '&1' is not implemented yet", getClass().getName());
      updateProfileBuffer(target);
      return profile_buffer;
   }

   /**
    * Inherited interface.
    * Synchronize the internal state with the content of the ProfileBuffer.
    */
   protected void refresh( ASPPoolElement target ) throws FndException
   {
      updateMembers(target);
     // throw new FndException("Profile functionality for class '&1' is not implemented yet", getClass().getName());
   }

   //==========================================================================
   //
   //==========================================================================

   /**
    * @deprecated
    */
   public void tokenize( Buffer profile_buffer, String text ) throws FndException
   {
      tokenize(profile_buffer, text, null, false);
   }

   /**
    * @deprecated
    */
   private void tokenize( Buffer profile_buffer, String text, String prefix, boolean prfparse ) throws FndException
   {
      StringTokenizer st = new StringTokenizer(text,""+(char)29);
      StringTokenizer name_value;
      String temp = "";

      if(DEBUG) debug("==> Parsing BlockProfile: "+text);
      while (st.hasMoreTokens())
      {
         temp = st.nextToken();
         name_value = new StringTokenizer(temp,""+(char)30);

         String name  = name_value.nextToken();
         String value = name_value.nextToken();
         if(DEBUG) debug("==>  - Adding Item: '"+name+"', '"+value+"'");
         //profile_buffer.addItem( Str.isEmpty(prefix) ? name : prefix+name, value );
         Item item = profile_buffer.newItem();
         item.setName( Str.isEmpty(prefix) ? name : prefix+name );
         item.setValue(value);
         profile_buffer.addItem(item);
         if( item instanceof ProfileItem && prfparse )
            ((ProfileItem)item).setState(ProfileItem.QUERIED);
      }
   }

   /*protected void parse_( ASPPoolElement target, String text ) throws FndException
   {
      if(DEBUG) debug("BlockProfile.parse():\n\t\t"+text);
      ASPBlock block = (ASPBlock) target;
      ASPBuffer aspbuff = block.getASPManager().newASPBuffer();
      aspbuff.parse(text);
     // profile_buffer = aspbuff.getBuffer();
   }
    */


   public boolean equals( Object obj )
   {
      return false;
   }

   private void debug( String text )
   {
      Util.debug(text);
   }

   /**
    * Update profile buffer to reflect the current state of this instance
    */
   private void updateProfileBuffer( ASPPoolElement target )  throws FndException
   {
      if(DEBUG) debug("BlockProfile.updateProfileBuffer()");
      if( profile_buffer==null )
         profile_buffer = ProfileUtils.newProfileBuffer();

      int count = 0;

      if (query_buffer != null)
      {
         count = query_buffer.countItems();

         for (int i=0; i<count; i++)
         {
            Item item = query_buffer.getItem(i);
            //ProfileUtils.findOrCreateNestedItem(profile_buffer,item.getName()) .setValue(item.getString());

            Buffer buf = ProfileUtils.findOrCreateNestedBuffer( profile_buffer, QUERY_SECTION);
            ProfileUtils.findOrCreateNestedItem(buf,item.getName()) .setValue(item.getString());
         }
      }

      if (update_template_only || field_name != null)
      {
         ASPBlock block = (ASPBlock)target;
         ASPManager mgr = block.getASPManager();
         if(!mgr.isEmpty(template_id))
         {
            ASPPage page = block.getASPPage();            
            ASPBuffer global_tmpls = page.readGlobalProfileBuffer(TEMPLT_SECTION, false);
            boolean first_node = false;
            if(global_tmpls==null) {
               
               global_tmpls = mgr.newASPBuffer();
               Buffer temp = global_tmpls.getBuffer();
               ProfileUtils.findOrCreateNestedItem(temp, global_template_id+TEMPLT_GLOBAL_ID).setValue(global_template_id);
               page.writeGlobalProfileBuffer(TEMPLT_SECTION,global_tmpls,false);
               global_tmpls = page.readGlobalProfileBuffer(TEMPLT_SECTION, false);
               first_node = true;
            }
            
            Buffer template_buf = global_tmpls.getBuffer();
            if(update_template_only)
            {
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_TITLE).setValue(template_title);
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_DESC).setValue(template_desc);
               page.writeGlobalProfileBuffer(TEMPLT_SECTION,global_tmpls,false);
            }
            else
            {
               if(!first_node)
                  ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_GLOBAL_ID).setValue(global_template_id);
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_ID).setValue(template_id);
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_TITLE).setValue(template_title);
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_DESC).setValue(template_desc);
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_ENTITY).setValue(template_entity);
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_PATH).setValue(page.getURL());
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_BLOCK).setValue(block.getName());
               ProfileUtils.findOrCreateNestedItem(template_buf, global_template_id+TEMPLT_DEFAULT).setValue((template_defailt)?"true":"false");
               page.writeGlobalProfileBuffer(TEMPLT_SECTION,global_tmpls,false);

               Buffer buf = ProfileUtils.findOrCreateNestedBuffer( profile_buffer, TEMPLT_SECTION+"/"+template_id);
               ProfileUtils.findOrCreateNestedItem(buf, TEMPLT_GLOBAL_ID).setValue(global_template_id);

               for (int i=0; i<fld_count; i++)
               {
                  ASPField fld = block.getASPField(field_name[i]);
                  ProfileUtils.findOrCreateNestedItem(buf, field_name[i] + DEF_VALUE) .setValue(default_value[i]);
               }
            }
         }
         if(!update_template_only && template_defailt)
         {
            Buffer def_buf = ProfileUtils.findOrCreateNestedBuffer( profile_buffer, FIELD_SECTION);
            for (int i=0; i<fld_count; i++)
            {
               ASPField fld = block.getASPField(field_name[i]);
               ProfileUtils.findOrCreateNestedItem(def_buf, field_name[i] + DEF_VALUE) .setValue(default_value[i]);
            }
            template_defailt = false;
         }
      }
   }
   
 /**
  * This method creates a buffer containing nested items for each field (as shown below) using the buffer received 
  * in the assign() method which has a flat structure. 
  *  
  *   0:$FIELD01=:
  *      !
  *      0:$^DefaultValue=xx  
  *   1:$FIELD02=:
  *      !
  *      0:$^DefaultValue=yy    
  *   2:$FIELD03=
  *   ...
  */   
   private ProfileBuffer indentBufferOnFields(ProfileBuffer buffer) throws FndException
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
      if(DEBUG) debug("CommandBarProfile.updateMembers():");

      //int prf_col_count = profile_buffer.countItems();
      ASPBlock block = (ASPBlock)target;
      ASPManager mgr = block.getASPManager();

      query_buffer = mgr.newASPBuffer().getBuffer();

      Item queryItem = profile_buffer.findItem(QUERY_SECTION);

      if (queryItem != null)
      {

         Buffer queryBuff = queryItem.getBuffer();
         int count = queryBuff.countItems();
         for( int i=0; i<count; i++ )
         {
            Item    item    = queryBuff.getItem(i);
            String  name    = item.getName();
            query_buffer.addItem(name,item.getString());
         }

         try{
             SimpleComparator comp = new SimpleComparator(true);
             Buffers.sort_(query_buffer,comp);
         }
         catch( Throwable any )
         {
           throw new FndException("FNDASPBLOCKPROFILELOADQUERY: Cannot load query profile for ASPBlock: (&1)",any.getMessage())
                   .addCaughtException(any);
         }
      }
      else
      {
         query_buffer = mgr.newASPBuffer().getBuffer();
         query_buffer.addItem(PREVIOUS_QUERY+mgr.translate("FNDBLOCKPROPREQRY: Previous Query"),"__EMPTY");
      }

      Item fieldItem = profile_buffer.findItem(FIELD_SECTION);

      Buffer fieldBuff = null;
      Item item        = null;

      if (fieldItem != null)
      {
         fieldBuff = fieldItem.getBuffer();
         //IMPORTANT: must call indentBufferOnFields() to get the desired buffer structure
         fieldBuff = indentBufferOnFields((ProfileBuffer)fieldBuff);
      }

      ASPField[] fields = block.getFields();
      field_name = new String[fields.length];
      default_value = new String[fields.length];

      fld_count = 0;

      for (int i=0; i<fields.length; i++)
      {
         ASPField fld = fields[i];
         if (fld.isHidden() || (fld.isReadOnly() && !fld.isInsertable())) continue;

         String name = fld.getName();
         field_name[fld_count] = name;

         if (fieldBuff != null)
            item = fieldBuff.findItem(name);

         if (item == null)
            default_value[fld_count] = "";
         else
         {
            Buffer buf = item.getBuffer();
            default_value[fld_count] = buf.getString(DEF_VALUE);
         }

         fld_count++;
      }

   }

   /*
    * mark the given saved query as removed in the profile buffer, also remove the saved query
    * from the internal buffer query_buffer.
    */
   void removeQuery(String query_name) throws FndException
   {
       ProfileItem item = (ProfileItem)profile_buffer.findItem(QUERY_SECTION+"/"+query_name);
       if (item!=null)
         item.setState(ProfileItem.REMOVED);
       query_buffer.removeItem(query_name);
   }

   /*
    * mark the FEILD_SECTION sub-buffer as removed in the profile buffer
    */
   void fieldRemove() throws FndException
   {
      ProfileItem item = (ProfileItem)profile_buffer.findItem(FIELD_SECTION);

      if (item!=null)
         mark(item.getBuffer(), ProfileItem.REMOVED);
   }

   void templateRemove(ASPPoolElement target, String template_id) throws FndException
   {
      ASPBlock block =  (ASPBlock) target;
      boolean end = false;
      ProfileItem item = (ProfileItem)profile_buffer.findItem(TEMPLT_SECTION);
      Buffer buffer = item.getBuffer();
      String globalid = buffer.findItem(template_id+TEMPLT_GLOBAL_ID).getValue().toString();
      for(int i=0; i<buffer.countItems(); i++){
         ProfileItem _item = (ProfileItem) buffer.getItem(i);
         String name  = _item.getName();
         
         if(_item!=null && _item.isCompound() && name.equals(template_id))
         {
            mark(_item.getBuffer(), ProfileItem.REMOVED);
            block.getASPPage().removeTemplateProfileItem(globalid);
            end = true;
         }
         else
         {
            int pos  = name.indexOf(template_id+ProfileUtils.ENTRY_SEP);
            if(pos>=0)
               _item.setState(ProfileItem.REMOVED);
            else if(end)
            {
               break;
            }
         }
      }      
   }

   private void mark( Buffer buffer, char status ) throws FndException
   {
     // TODO: Mark recursive the entire 'buffer' with 'status'
     for(int i=0; i<buffer.countItems(); i++)
     {
         ProfileItem item = (ProfileItem)buffer.getItem(i);
         String name  = item.getName();
         int pos  = name.indexOf(ProfileUtils.ENTRY_SEP);

         if( pos>=0 )
           item.setState(status);
         else if( item.isCompound() )
           mark( item.getBuffer(), status );
     }

   }


}
