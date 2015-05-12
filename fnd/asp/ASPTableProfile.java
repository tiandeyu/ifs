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
 * File        : ASPTableProfile.java
 * Description : User profile for ASPTable
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1999-May-03 - Created
 *    Jacek P  1999-May-21 - Added items to method save()
 *    Marek D  1999-Jun-11 - Corrected logic in save()
 *    Jacek P  1999-Aug-08 - Added exception handling in format().
 *    Jacek P  1999-Oct-07 - Profile information about all columns is now
 *                           stored to the database.
 *    Jacek P  2000-May-22 - Added showContents() function.
 *    Jacek P  2000-Aug-07 - Added clone() function.
 *    Artur K  2000-Nov-23 - Changes regarding replacing ASPPageElementProfile
 *    Piotr Z                with ASPPoolElementProfile
 *    Jacek P  2004-Nov-11 - Introduced API due to new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/03/16 sumelk - Bug 88158, Changed updateProfileBuffer() for not to update the hidden fields when saving the profile. 
 *               2008/07/15           sadhlk
 * Bug Id: 74364, Modified   parse() and updateMembers() to allow mandatory field to be set hidden.
 *               2006/05/15           riralk
 * Bug Id: 57838, Improve structure in profile. Added method indentBufferOnFields().
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/07/14 06:56:52  japase
 * New sort algorithm for ProfileBuffers
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.3  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
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
import java.io.*;
import java.lang.Math;

/**
 * ASPTable.doFreeze() creates the default instance of ASPTableProfile
 * which is stored in the page pool as the mutable attribute 'profile'.
 *
 * During every request ASPTable.populate() fetches the user specific profile
 * by calling ASPProfile.getProfile(this,pre_profile). The returned Object
 * is assigned to the 'profile'-attribute, maybe marking the table as DIRTY.
 *
 * ASPTable.doReset() re-assigns the default value of the 'profile'-attribute.
 *
 * Modified profile are stored in the cache (as Object) and in the database
 * (as String), but the user must refresh the page (re-populate the table)
 * to see the changes.
 */
class ASPTableProfile extends ASPPoolElementProfile
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPTableProfile");

   // constances for new profile handling   
   private static final String POSITION = ProfileUtils.ENTRY_SEP + "Position";
   private static final String VISIBLE  = ProfileUtils.ENTRY_SEP + "Visible" ;
   private static final String SIZE     = ProfileUtils.ENTRY_SEP + "Size";

   //==========================================================================
   //  Instance variables
   //==========================================================================

   // profile contents
   private ProfileBuffer profbuf;

   // temporary variables used for better performance
   private int column_count;     // number of user columns
   private int column_size[];    // size of each user column
   private int column_index[];   // index to array ASPTable.columns

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Constructor
    */
   protected ASPTableProfile()
   {
   }

   /**
    * Interface implementation.
    * Create new instance of the same class.
    */
   protected ASPPoolElementProfile newInstance()
   {
      return new ASPTableProfile();
   }

   /**
    * Interface implementation.
    * Initiate the instance with design-time meta data.
    */
   protected void construct( ASPPoolElement template )
   {
      ASPTable table = (ASPTable)template;
      int colcnt = table.getColumnCount();
      column_count = 0;
      column_size  = new int[colcnt];
      column_index = new int[colcnt];

      for( int nr=0; nr<colcnt; nr++ )
      {
         ASPField fld = table.getColumn(nr);
         if(DEBUG) debug(""+fld.getName()+".."+fld.isDefaultNotVisible());
         if(!fld.isDefaultNotVisible())
         {
            column_size [column_count] = fld.getSize();
            column_index[column_count] = nr;//column_count;
            column_count++;
         }
      }
   }

   /**
    * Interface implementation.
    * Clone this instance.
    */
   public Object clone()
   {
      if(DEBUG) debug(this+": ASPTableProfile.clone()");

      ASPTableProfile prf = new ASPTableProfile();

      synchronized(this)
      {
         prf.column_count = column_count;
         prf.column_size  = new int[column_count];
         prf.column_index = new int[column_count];
         for( int i=0; i<column_count; i++ )
         {
            prf.column_size [i] = column_size [i];
            prf.column_index[i] = column_index[i];
         }
         prf.profbuf = profbuf; // point out the same ProfileBuffer ?
      }
      return prf;
   }

   //==========================================================================
   //  Interface implementation
   //==========================================================================

   /**
    * Interface implementation.
    * Return 'true' if this instance represents the same Meta-data as the given one,
    * 'false' otherwise.
    */
   public boolean equals( Object obj )
   {
      if( obj instanceof ASPTableProfile )
      {
         ASPTableProfile p = (ASPTableProfile)obj;
         if( column_count != p.column_count ) return false;
         for( int nr=0; nr<column_count; nr++ )
         {
            if( column_size [nr] != p.column_size [nr] ) return false;
            if( column_index[nr] != p.column_index[nr] ) return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Interface implementation.
    * Create a Buffer with profile information from an existing ASPPoolElement.
    * Called from ASPTable.getProfile() to support user interface.
    */
   protected void save( ASPPoolElement target, Buffer dest ) throws FndException
   {
      try
      {
         ASPTable table = (ASPTable)target;
         int all_count = table.getColumnCount();
         //int col_order = 0;
         for( int index=0; index<all_count; index++ )
         {
            ASPField fld = table.getColumn(index);
            if( fld.isHidden() ) continue;
            //col_order++;
            Buffer col = dest.newInstance();
            col.addItem("NAME",fld.getName());
            col.addItem("LABEL",fld.getLabel());
            int nr = findColumnNr(index);
            col.addItem(new Item("SIZE",  "N", null, nr>=0 ? ""+column_size[nr] : ""+fld.getSize()));
            col.addItem(new Item("ORDER", "N", null, nr>=0 ? ""+(nr+1) : null));
            //col.addItem(new Item("ORDER", "N", null, nr>=0 ? ""+col_order : null));
            col.addItem("VISIBLE", nr>=0 ? "Y" : "N");
            col.addItem("MANDATORY",fld.isMandatory() ? "Y" : "N");
            dest.addItem("DATA",col);
         }

         RowComparator comp = new RowComparator(table.getASPManager().getServerFormatter(),"ORDER",true);
         Buffers.sort(dest,comp);

         if(DEBUG)
            table.getASPManager().newASPBuffer(dest).traceBuffer("ASPTableProfile.save()");
      }
      catch( Throwable any )
      {
         throw new FndException("FNDTABPRFSAV: Cannot save profile for ASPTable: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }

   /**
    * Inherited interface.
    * Apply profile information stored in the given Buffer on an ASPPoolElement.
    * Called from ASPTable.saveProfile() to save user changes.
    */
   protected void load( ASPPoolElement target, Buffer source ) throws FndException
   {
      try
      {
         ASPTable table = (ASPTable)target;
         if(DEBUG)
            table.getASPManager().newASPBuffer(source).traceBuffer("ASPTableProfile.load()");
         int item_count = source.countItems();
         column_count = 0;
         for( int i=0; i<item_count; i++ )
         {
            Buffer col = source.getBuffer(i);
            Item order = col.getItem("ORDER");
            order.setType("N");
            if( !Str.isEmpty(order.getString()) )
               column_count++;
         }

         RowComparator comp = new RowComparator(table.getASPManager().getServerFormatter(),"ORDER",true);
         Buffers.sort(source,comp);

         column_size  = new int[column_count];
         column_index = new int[column_count];

         column_count = 0;
         for( int i=0; i<item_count; i++ )
         {
            Buffer col = source.getBuffer(i);
            Item order = col.getItem("ORDER");
            if( !Str.isEmpty(order.getString()) )
            {
               String name = col.getString("NAME");
               int index = table.findColumnIndex(name);

               String sizestr = col.getString("SIZE",null);
               if( Str.isEmpty(sizestr) ) sizestr = "-1";
               int size = Integer.parseInt(sizestr);
               if( size<0 ) size = table.getColumn(index).getSize();

               column_index[column_count] = index;
               column_size [column_count] = size;
               column_count++;
            }
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDTABPRFLOD: Cannot load profile for ASPTable: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }

   //--------------------------------------------------------------------------
   // old, obsolete interface - should be removed
   //--------------------------------------------------------------------------

   /**
    * Interface implementation.
    * Deserialize profile information in a given string and apply to an ASPPoolElement.
    * Called from ASPProfile.findProfile() after fetching from the database.
    *
    * @deprecated
    */
   protected void parse( ASPPoolElement target, String text ) throws FndException
   {
      if(DEBUG) debug("ASPTableProfile.parse():\n\t\t"+text);
      try
      {
         ASPTable table = (ASPTable)target;
         StringTokenizer st = new StringTokenizer(text," :,");
         int prf_col_count = Integer.parseInt(st.nextToken());
         boolean profile_changed = false;

         column_count = table.getColumnCount();
         column_size  = new int[column_count];
         column_index = new int[column_count];
         int unvisible_index[] = new int[prf_col_count];

         if(prf_col_count != column_count)
         {
            if(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+
                            "\n\t\tprf_col_count = "+prf_col_count );
            profile_changed = true;
         }

         int nr = 0;
         for( int i=0; i<prf_col_count; i++ )
         {
            String  name    = st.nextToken();
            boolean visible = "Y".equals(st.nextToken());
            int     size    = Integer.parseInt(st.nextToken());
            int     index   = table.findColumnIndex(name);

            if(DEBUG) debug("  Fetched column ["+name+"] with index "+index);

            if(index==-1)
            {
               // just jump over the invalid column
               if(DEBUG) debug("  Found invalid column: "+name );
               profile_changed = true;
               continue;
            }

            ASPField fld = table.getColumn(index);

            if( !fld.isHidden() && ( visible /*|| fld.isMandatory()*/ ) )
            {
               column_index[nr] = index;
               column_size [nr] = size;
               nr++;

               if(!visible)
               {
                  if(DEBUG) debug("  Mandatory attribute for column ["+name+"] has been changed (nr="+nr+",index="+index+").");
                  profile_changed = true;
               }
            }
            else
            {
               if(DEBUG) debug("  Unvisible column ["+name+"] (nr="+nr+",index="+index+").");
               unvisible_index[i] = index;
               if(visible)
               {
                  if(DEBUG) debug("  Hidden attribute for column ["+name+"] has been changed.");
                  profile_changed = true;
               }
            }
         }
         // if(prf_col_count!=column_count && )
         int visible_cnt = nr;
         for( int index=0; index<column_count; index++ )
         {
            ASPField fld = table.getColumn(index);
            if(DEBUG) debug("  Check column ["+fld.getName()+"] with index "+index);
            if( fld.isHidden() ) continue;
            boolean new_column = true;

            for( int i=0; i<Math.max(visible_cnt,prf_col_count); i++)
            {
               if( i<visible_cnt   && column_index[i]==index    ||
                   i<prf_col_count && unvisible_index[i]==index )
               {
                  new_column = false;
                  break;
               }
            }
            if(!new_column) continue;

            column_index[nr] = index;
            column_size [nr] = fld.getSize();
            nr++;
         }
         column_count = nr;

         if(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+
                         "\n\t\tcolumn_index[]= "+debugIntArray(column_index,column_count)+
                         "\n\t\tcolumn_size[] = "+debugIntArray(column_size,column_count) );
         if(profile_changed)
            //table.getASPManager().showSystemAlert("FNDTABPRFCH: The profile information for this table has been changed.");
            table.trace("The profile information for table ["+table.getName()+"]has been changed.");
      }
      catch( Exception any )
      {
         throw new FndException("FNDTABPRFPRS: Cannot parse profile for ASPTable: '&1'", text)
                   .addCaughtException(any);
      }
   }

   /**
    * Inherited interface.
    * Serialize profile information from a given ASPPoolElement to a string.
    * Called from ASPProfile.save() before storing in the database.
    *
    * @deprecated
    */
   protected String format( ASPPoolElement target ) throws FndException
   {
      if(DEBUG) debug("ASPTableProfile.format()");
      try
      {
         ASPTable table = (ASPTable)target;
         Buffer   dest  = table.getASPPage().getASPConfig().getFactory().getBuffer();
         int  all_count = table.getColumnCount();
         for( int index=0; index<all_count; index++ )
         {
            ASPField fld = table.getColumn(index);
            Buffer col = dest.newInstance();
            int nr = findColumnNr(index);
            col.addItem("NAME",fld.getName());
            col.addItem("VISIBLE", nr>=0 ? "Y" : "N");
            col.addItem(new Item("SIZE",  "N", null, nr>=0 ? ""+column_size[nr] : ""+fld.getSize()));
            col.addItem(new Item("ORDER", "N", null, nr>=0 ? ""+(nr+1) : null));
            dest.addItem("DATA",col);
         }
         RowComparator comp = new RowComparator(table.getASPManager().getServerFormatter(),"ORDER",true);
         Buffers.sort(dest,comp);

         AutoString out = new AutoString();
         out.appendInt(all_count);
         for( int i=0; i<dest.countItems(); i++)
         {
            Buffer col = dest.getBuffer(i);
            out.append(" ", col.getString("NAME"),    ":" );
            out.append(     col.getString("VISIBLE"), "," );
            out.appendInt(  col.getInt("SIZE") );
         }

         String res = out.toString();
         if(DEBUG) debug("  result="+res);
         return res;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDTABPRPPRS: Cannot format profile for ASPTable.")
                   .addCaughtException(any);
      }
   }

   //--------------------------------------------------------------------------
   // new interface
   //--------------------------------------------------------------------------

   //==========================================================================
   // New profile handling
   //==========================================================================

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Deserialize profile information in a given string to a Buffer
    * without needing to access any run-time objects.
    * Used for conversion between the old and new profile framework.
    * Use ProfileBuffer class as Buffer implementation.
    * Set state of each simple item (instance of ProfileItem to QUERIED).
    */
   protected void parse( ProfileBuffer buffer, BufferFormatter fmt, String text ) throws FndException
   {
      if(DEBUG) debug("ASPTableProfile.parse():\n\t\t"+text);

      // deserialize string in form "<col_count> <col_spec>[ <col_spec>[...]]"
      // where <col_spec> has form: "<col_name>:[Y|N],<width>"
      // and [Y|N] denotes visible attribute
      //
      // convert to a buffer in form (skip <col_count>) for each column:
      //   $<col_name>=:
      //     !
      //     0:$Position=<col_position_in_table>
      //     1:$Visible=[Y|N]
      //     2:$Size=<width>
      //
      // The leaf value is always encapsulated in an instance of ProfileUtils.ProfileValue
      // Profile Entry set to "Table Node"

      try
      {
         buffer.clear();
         int colcnt = 0;
         StringTokenizer pst = new StringTokenizer(text, " ");
         while( pst.hasMoreTokens() )
         {
            String token = pst.nextToken();
            colcnt++;

            if( colcnt==1 )
               continue;

            int pos1 = token.indexOf(":");
            int pos2 = token.indexOf(",");
            if( pos1<0 || pos2<0 )
               throw new FndException("FNDTABPRPARSETOBUF1: Format of column token '&1' is not as expected.",token);

            //ProfileBuffer b = (ProfileBuffer)buffer.newInstance();
            String col_name = token.substring(0,pos1); 
           
            addProfileValue(buffer, col_name+POSITION, colcnt-1 );
            addProfileValue(buffer, col_name+VISIBLE,  token.substring(pos1+1,pos2) );
            addProfileValue(buffer, col_name+SIZE,     Integer.parseInt(token.substring(pos2+1)) );

            //buffer.addItem( token.substring(0,pos1), b );
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDTABPRPARSETOBUF: Cannot parse profile for ASPTable: '&1'",text)
                   .addCaughtException(any);
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
      profbuf = buffer;
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
      updateProfileBuffer(target);
      return profbuf;
   }

   /**
    * Inherited interface.
    * New function for handling of the new profile concept.
    * Synchronize the internal state with the content of the ProfileBuffer.
    */
   protected void refresh( ASPPoolElement target ) throws FndException
   {
      updateMembers(target);
   }

   //==========================================================================
   //  Other methods
   //==========================================================================

   protected int getColumnCount()
   {
      return column_count;
   }

   protected int getColumnSize( int nr )
   {
      return column_size[nr];
   }

   protected int getColumnIndex( int nr )
   {
      return column_index[nr];
   }


   private int findColumnNr( int index )
   {
      for( int nr=0; nr<column_count; nr++ )
         if( index==column_index[nr] )
            return nr;

      return -1;
   }

   //==========================================================================
   //  Private help methods
   //==========================================================================

   /**
    * Add a ProfileItem containing a single int value to a ProfileBuffer.
    */
   private void addProfileValue( ProfileBuffer buffer, String name, int value ) throws FndException
   {
      addProfileValue(buffer, name, new Integer(value) );
   }

   /**
    * Add a ProfileItem containing a single String value to a ProfileBuffer.
    */
   private void addProfileValue( ProfileBuffer buffer, String name, Object value ) throws FndException
   {
      ProfileItem item = (ProfileItem)buffer.newItem();
      item.setName(name);
      item.setValue(value);
      item.setState(ProfileItem.QUERIED);
      buffer.addItem(item);
   }
   
   
 /**
  * This method creates a buffer containing nested items for each field (as shown below) using the buffer received 
  * in the assign() method which has a flat structure. This method should be called before sorting the buffer since
  * sorting is done based on the ^Position item.
  *  
  *   0:$FIELD01=:
  *      !
  *      0:$^Visible=Y
  *      1:$^Size=15  
  *      3:$^Position=2   
  *        ...  
  *   1:$FIELD02=:
  *      !
  *      0:$^Visible=Y  
  *      1:$^Size=10
  *      3:$^Position=1 
  *        ...
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

   /**
    * Update instance variables: column_count, column_size, column_index to reflect
    * the current state of the profile buffer.
    */
   private void updateMembers( ASPPoolElement target ) throws FndException
   {
      // 1. sort the profile buffer according to Position
      PositionComparator comp = new PositionComparator(POSITION);
      //IMPORTANT: must call indentBufferOnFields() to get the desired buffer structure for sorting
      ProfileBuffer nestedbuf = indentBufferOnFields(this.profbuf);   
      nestedbuf.sort(comp);

      if(DEBUG) ProfileUtils.debug("ASPTableProfile.updateMembers():",nestedbuf);

      // 2. Update member variables. Basicaly the same functionality as in the deprecated parse().
      //   - number of user columns:           column_count
      //   - size of each user column:         column_size[]
      //   - index to array ASPTable.columns:  column_index[]
      ASPTable table = (ASPTable)target;
      int prf_col_count = nestedbuf.countItems();
      boolean profile_changed = false;

      column_count = table.getColumnCount();
      column_size  = new int[column_count];
      column_index = new int[column_count];
      int unvisible_index[] = new int[prf_col_count];

      if( prf_col_count!=column_count )
      {
         if(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+ "\n\t\tprf_col_count = "+prf_col_count );
         profile_changed = true;
      }

      int nr = 0;      
      for( int i=0; i<prf_col_count; i++ )
      {
         Item    item    = nestedbuf.getItem(i);// itr.next();
         String  name    = item.getName();
         Buffer  buf     = item.getBuffer();
         boolean visible = "Y".equals( buf.getString(VISIBLE) );  //TODO: better error handling, if the item is missing or have the malformed value. Warning? To Alert?
         int     size    = buf.getInt(SIZE);                      //TODO: same as above
         int     index   = table.findColumnIndex(name);

         if(DEBUG) debug("  Fetched column ["+name+"] with index "+index);
         if(index==-1)
         {
            // just jump over the invalid column
            if(DEBUG) debug("  Found invalid column: "+name );
            profile_changed = true;
            continue;
         }

         ASPField fld = table.getColumn(index);
         if( !fld.isHidden() && ( visible /*|| fld.isMandatory()*/ ) )
         {
            column_index[nr] = index;
            column_size [nr] = size;
            nr++;

            if(!visible)
            {
               if(DEBUG) debug("  Mandatory attribute for column ["+name+"] has been changed (nr="+nr+",index="+index+").");
               profile_changed = true;
            }
         }
         else
         {
            if(DEBUG) debug("  Unvisible column ["+name+"] (nr="+nr+",index="+index+").");
            unvisible_index[i] = index;
            if(visible)
            {
               if(DEBUG) debug("  Hidden attribute for column ["+name+"] has been changed.");
               profile_changed = true;
            }
         }
      }

      int visible_cnt = nr;
      for( int index=0; index<column_count; index++ )
      {
         ASPField fld = table.getColumn(index);
         if(DEBUG) debug("  Check column ["+fld.getName()+"] with index "+index);
         if( fld.isHidden() ) continue;
         boolean new_column = true;

         for( int i=0; i<Math.max(visible_cnt,prf_col_count); i++)
         {
            if( i<visible_cnt   && column_index[i]==index    ||
                i<prf_col_count && unvisible_index[i]==index )
            {
               new_column = false;
               break;
            }
         }
         if(!new_column) continue;

         column_index[nr] = index;
         column_size [nr] = fld.getSize();
         nr++;
      }
      column_count = nr;

      if(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+
                      "\n\t\tcolumn_index[]= "+debugIntArray(column_index,column_count)+
                      "\n\t\tcolumn_size[] = "+debugIntArray(column_size,column_count) );
      if(profile_changed)
         //table.getASPManager().showSystemAlert("FNDTABPRFCH: The profile information for this table has been changed.");
         table.trace("The profile information for table ["+table.getName()+"]has been changed.");
   }

   /**
    * Update profile buffer to reflect the current state of this instance
    */
   private void updateProfileBuffer( ASPPoolElement target )  throws FndException
   {
      if(DEBUG) debug("ASPTableProperties.updateProfileBuffer()");
      if( profbuf==null )
         profbuf = ProfileUtils.newProfileBuffer();

      ASPTable table = (ASPTable)target;
      int  all_count = table.getColumnCount();
      for( int index=0; index<all_count; index++ )
      {
         ASPField fld = table.getColumn(index);
         if( fld.isHidden() ) continue;
         //Buffer   col = ProfileUtils.findOrCreateNestedBuffer( profbuf, fld.getName() );
         String field_name = fld.getName();
         int      nr  = findColumnNr(index);

         ProfileUtils.findOrCreateNestedItem(profbuf,field_name+VISIBLE) .setValue(nr>=0 ? "Y"               : "N"           );
         ProfileUtils.findOrCreateNestedItem(profbuf,field_name+SIZE)    .setValue(nr>=0 ? column_size[nr]   : fld.getSize() );
         ProfileUtils.findOrCreateNestedItem(profbuf,field_name+POSITION).setValue(nr>=0 ? new Integer(nr+1) : null          );
      }
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   private void debug( String text )
   {
      Util.debug(text);
   }

   private String debugIntArray( int[] arr, int size )
   {
      AutoString buf = new AutoString();
      buf.append('[');
      for(int i=0; i<size; i++)
      {
         if(i>0) buf.append(',');
         buf.appendInt(arr[i]);
      }
      buf.append(']');
      return buf.toString();
   }

   /**
    * Inherited interface.
    * Show profile data corresponding to this instance. Used for debugging.
    */
   void showContents( AutoString out ) throws FndException
   {
      out.append("   ",this.toString(),":\n");

      out.append("    column_count  = ");
      out.appendInt(column_count);
      out.append("\n");
      out.append("    column_index[]= ",debugIntArray(column_index,column_count),"\n");
      out.append("    column_size[] = ",debugIntArray(column_size,column_count),"\n");
   }
}
