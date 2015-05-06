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
 * File        : BlockLayoutProfile.java
 * Description : User profile for ASPBlockLayout
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    chandana 2004-Aug-26 - Created
 *    Jacek P  2004-Nov-11 - Introduced API due to new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Bug Id: 90508 2010/07/14  buhilk  Modified updateProfileBuffer() to hidden fields.
 *               2008/09/24           dusdlk
 * Bug Id: 77095, Updated Save(), Load() and updateProfileBuffer() to check if the request is from PageProperties page.
 *
 *               2008/07/15           sadhlk
 * Bug Id: 74364, Modified updateMembers() to allow mandatory fields to be hidden.
 *               2006/05/15           riralk
 * Bug Id: 57838, Improve structure in profile. Added method indentBufferOnFields().
 *
 *               2006/05/04           riralk
 * Bug Id: 57725, modified findColumnNr() to use String.equals() instead of ==.
 *
 *               2006/01/25           mapelk
 * Removed errorneous translation keys
 *
 * Revision 1.2  2005/11/16 14:20:18  japase
 * Added default values of parameters in updateMembers()
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.8  2005/08/15 09:54:22  rahelk
 * profile - removed arrays and introduced Field class structure
 *
 * Revision 1.7  2005/07/14 06:57:38  japase
 * New sort algorithm for ProfileBuffers
 *
 * Revision 1.6  2005/07/01 15:25:12  rahelk
 * CSL: set size and dataSpan for field in detail mode
 *
 * Revision 1.5  2005/05/19 04:11:50  rahelk
 * Added support for setting profile height for a field
 *
 * Revision 1.4  2005/05/04 05:39:35  rahelk
 * Code clean up
 *
 * Revision 1.3  2005/05/04 05:32:01  rahelk
 * Layout profile support for groups
 *
 * Revision 1.2  2005/02/02 15:09:31  riralk
 * Adapted BlockLayoutProfile functionality to new profile changes.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.4  2005/01/07 10:26:58  marese
 * Merged changes made on the PKG10 branch back to HEAD
 *
 * Revision 1.3.2.1  2004/12/29 14:08:16  riralk
 * More Fixes to Profile
 *
 * Revision 1.3  2004/12/16 11:56:40  japase
 * First changes for support of the new profile concept.
 *
 * Revision 1.2  2004/12/10 10:20:40  riralk
 * New Block Layout profile changes
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.asp.ASPBlockLayout.group;

import java.util.*;
import java.io.*;
import java.lang.Math;

/**
 * ASPBlockLayout.doFreeze() creates the default instance of BlockLayoutProfile
 * which is stored in the page pool as the mutable attribute 'profile'.
 *
 * During every request ASPBlockLayout.generateDialog() fetches the user specific profile
 * by calling ASPProfile.getProfile(this,pre_profile). The returned Object
 * is assigned to the 'profile'-attribute, maybe marking the table as DIRTY.
 *
 * ASPBlockLayout.doReset() re-assigns the default value of the 'profile'-attribute.
 *
 * Modified profile are stored in the cache (as Object) and in the database
 * (as String), but the user must refresh the page to see the changes.
 */
class BlockLayoutProfile extends ASPPoolElementProfile
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.BlockLayoutProfile");

   // constants for new profile handling
   private static final String POSITION  = ProfileUtils.ENTRY_SEP + "Position";
   private static final String VISIBLE   = ProfileUtils.ENTRY_SEP + "Visible";
   private static final String SIZE      = ProfileUtils.ENTRY_SEP + "Size";
   private static final String QUERYABLE = ProfileUtils.ENTRY_SEP + "Queryable";
   private static final String READONLY  = ProfileUtils.ENTRY_SEP + "Readonly";
   private static final String MANDATORY = ProfileUtils.ENTRY_SEP + "Mandatory";
   private static final String LABEL     = ProfileUtils.ENTRY_SEP + "Label";
   //private static final String GROUP     = "Group"     + ProfileUtils.ENTRY_SEP + "Layout Node";
   private static final String HEIGHT    = ProfileUtils.ENTRY_SEP + "Height";
   private static final String SPAN      = ProfileUtils.ENTRY_SEP + "Span";
   private static final String ISBLOCKVISIBLE = ProfileUtils.ENTRY_SEP + "BlockVisible";

   private HashMap columns;
   private String[] column_order;

   private int column_count;     // number of user columns

   private int group_count;
   private int groups[];               //defined group_ids
   private String group_fields[];      //comma separate list of ordered field names
   //private int group_column_count[];   //user columns per group
   //private boolean group_showheader[];
   //private boolean group_firstpopulate[];

   private String isBlockVisible;

   // profile contents
   private ProfileBuffer profbuf;

   private int dialog_column_count;
   private boolean apply_lay_profile;   //apply profile changes only for layout (not for queryable, mandatory, readonly profile setting)

   protected BlockLayoutProfile()
   {
   }

   protected ASPPoolElementProfile newInstance()
   {
      return new BlockLayoutProfile();
   }

   protected void construct( ASPPoolElement template )
   {
      ASPBlockLayout lay = (ASPBlockLayout)template;
      int colcnt = lay.getColumnCount();
      column_count = 0;
      dialog_column_count = 0;
      apply_lay_profile = false;

      columns = new HashMap();
      column_order = new String[colcnt];

      for( int nr=0; nr<colcnt; nr++ )
      {
         ASPField fld = lay.getColumn(nr);
         String fld_name = fld.getName();
         column_order[nr] = fld_name;
         columns.put(fld_name, new Field(fld.getSize(),
                                         fld.getHeight(),
                                         fld.getDataSpan(),
                                         fld.isQueryable(),
                                         fld.isReadOnly(),
                                         fld.isMandatory(),
                                         !fld.isSimple()) );

         column_count++;
      }

      if (lay.hasDefinedGroups())
      {
         Vector groupVec = lay.getDefinedGroups();
         group_count = groupVec.size();
         groups = new int[group_count];
         group_fields = new String[group_count];

         for (int nr=0; nr<group_count; nr++)
         {
            ASPBlockLayout.group group = (ASPBlockLayout.group)groupVec.elementAt(nr);
            groups[nr] = group.id;
            group_fields[nr] = group.fields;
         }
      }
   }

   public Object clone()
   {
      //if(DEBUG) debug(this+": BlockLayoutProfile.clone()");

      BlockLayoutProfile prf = new BlockLayoutProfile();

      synchronized(this)
      {
         prf.dialog_column_count = dialog_column_count;
         prf.column_count = column_count;
         prf.apply_lay_profile = apply_lay_profile;

         prf.group_count = group_count;
         prf.groups = new int[group_count];
         prf.group_fields = new String[group_count];
         for (int i=0; i<group_count; i++)
         {
            prf.groups[i] = groups[i];
            prf.group_fields[i] = group_fields[i];
         }
         prf.profbuf = profbuf; // point out the same ProfileBuffer ?
      }
      return prf;
   }

   public boolean equals( Object obj )
   {
      if( obj instanceof BlockLayoutProfile )
      {
         BlockLayoutProfile p = (BlockLayoutProfile)obj;
         if( dialog_column_count != p.dialog_column_count ) return false;
         if( apply_lay_profile != p.apply_lay_profile ) return false;
         if( column_count != p.column_count ) return false;

         for( int nr=0; nr<column_count; nr++ )
            if (column_order[nr] != p.column_order[nr]) return false;

         if (!columns.equals(p.columns)) return false;

         if (group_count != p.group_count) return false;
         for (int nr=0; nr<group_count; nr++)
         {
            if (groups[nr] != p.groups[nr]) return false;
            if (group_fields[nr] != p.group_fields[nr]) return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Deserialize profile information in a given string and apply to an ASPPoolElement.
    * Called from ASPProfile.findProfile() after fetching from the database.
    *
    * @deprecated
    */
   protected void parse( ASPPoolElement target, String text ) throws FndException
   {
      /*
      //if(DEBUG) debug("ASPTableProfile.parse():\n\t\t"+text);
      try
      {
         ASPBlockLayout lay = (ASPBlockLayout)target;
         StringTokenizer st = new StringTokenizer(text," :,");

         int prf_col_count = Integer.parseInt(st.nextToken());
         String apply_prf = st.nextToken();
         apply_lay_profile = "Y".equals(apply_prf);

         boolean profile_changed = false;

         dialog_column_count = lay.getDialogColumns();
         column_count = lay.getColumnCount();
         column_size  = new int[column_count];
         column_index = new int[column_count];

         column_queryable = new boolean[column_count];
         column_mandatory = new boolean[column_count];
         column_readonly  = new boolean[column_count];
         column_showlabel  = new boolean[column_count];

         int unvisible_index[] = new int[prf_col_count];

         if(prf_col_count != column_count)
         {
            //(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+
            //"\n\t\tprf_col_count = "+prf_col_count );
            profile_changed = true;
         }

         int nr = 0;
         for( int i=0; i<prf_col_count; i++ )
         {
            String  name    = st.nextToken();
            boolean visible = "Y".equals(st.nextToken());
            int     size    = Integer.parseInt(st.nextToken());
            int     index   = lay.findColumnIndex(name);

            boolean queryable = "Y".equals(st.nextToken());
            boolean mandatory = "Y".equals(st.nextToken());
            boolean readonly = "Y".equals(st.nextToken());
            boolean show_label = "Y".equals(st.nextToken());
            //if(DEBUG) debug("  Fetched column ["+name+"] with index "+index);
            if(index==-1)
            {
               // just jump over the invalid column
               //if(DEBUG) debug("  Found invalid column: "+name );
               profile_changed = true;
               continue;
            }

            ASPField fld = lay.getColumn(index);

            if( !fld.isHidden() && ( visible || fld.isMandatory() ) )
            {
               column_index[nr] = index;
               column_size[nr] = size;
               //make sure not to override settings done in the code
               column_queryable[nr] = fld.isQueryable()?queryable:false;
               column_mandatory[nr] = fld.isMandatory()?true:mandatory;
               column_readonly[nr] = fld.isReadOnly()?true:readonly;
               column_showlabel[nr] = fld.isSimple()?false:show_label;
               nr++;

               if(!visible)
               {
                  //if(DEBUG) debug("  Mandatory attribute for column ["+name+"] has been changed (nr="+nr+",index="+index+").");
                  profile_changed = true;
               }
            }
            else
            {
               //if(DEBUG) debug("  Unvisible column ["+name+"] (nr="+nr+",index="+index+").");
               unvisible_index[i] = index;
               if(visible)
               {
                  //if(DEBUG) debug("  Hidden attribute for column ["+name+"] has been changed.");
                  profile_changed = true;
               }
            }
         }
         // if(prf_col_count!=column_count && )
         int visible_cnt = nr;
         for( int index=0; index<column_count; index++ )
         {
            ASPField fld = lay.getColumn(index);
            //if(DEBUG) debug("  Check column ["+fld.getName()+"] with index "+index);
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
            column_queryable[nr] = fld.isQueryable();
            column_mandatory[nr] = fld.isMandatory();
            column_readonly[nr] = fld.isReadOnly();
            column_showlabel[nr] = (!fld.isSimple());
            nr++;
         }
         column_count = nr;

         //if(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+
         //                "\n\t\tcolumn_index[]= "+debugIntArray(column_index,column_count)+
         //                "\n\t\tcolumn_size[] = "+debugIntArray(column_size,column_count) );
         //if(profile_changed)
         //table.getASPManager().showSystemAlert("FNDTABPRFCH: The profile information for this table has been changed.");
         //lay.trace("The profile information for table ["+table.getName()+"]has been changed.");
      }
      catch( Exception any )
      {
         throw new FndException("FNDBLKPRFPRS: Cannot parse profile for BlockLayoutProfile: '&1'", text)
                   .addCaughtException(any);
      }
       */
   }

   /**
    * Serialize profile information from a given ASPPoolElement to a string.
    * Called from ASPProfile.save() before storing in the database.
    *
    * @deprecated
    */
   protected String format( ASPPoolElement target ) throws FndException
   {
      //called from ASPBlockLayout for debugging.
      try
      {
         ASPBlockLayout lay = (ASPBlockLayout)target;
         Buffer   dest  = lay.getASPPage().getASPConfig().getFactory().getBuffer();
         int  all_count = lay.getColumnCount();
         for( int index=0; index<all_count; index++ )
         {
            ASPField fld = lay.getColumn(index);
            String fld_name = fld.getName();
            Buffer col = dest.newInstance();

            col.addItem("NAME",fld_name);

            if (columns.containsKey(fld_name))
            {
               Field prof_fld = (Field) columns.get(fld_name);

               int nr = findColumnNr(fld_name);

               col.addItem("VISIBLE", "Y" );
               col.addItem(new Item("SIZE",  "N", null, ""+prof_fld.getSize()));
               col.addItem(new Item("ORDER", "N", null, ""+(nr+1)));

               col.addItem(new Item("QUERYABLE", fld.isQueryable()?((prof_fld.isQueryable())?"Y":"N"):"N" ));
               col.addItem(new Item("MANDATORY", fld.isMandatory()?"Y":(prof_fld.isMandatory())?"Y":"N" ));
               col.addItem(new Item("READONLY",  fld.isReadOnly()?"Y":(prof_fld.isReadonly())?"Y":"N" ));
               col.addItem(new Item("SHOWLABEL", prof_fld.showlabel()?"Y":"N" ));
            }
            else //non visible columns are set according to default definitions
            {
               col.addItem("VISIBLE", "N");
               col.addItem(new Item("SIZE",  "N", null, ""+fld.getSize()));
               col.addItem(new Item("ORDER", "N", null, ""));

               col.addItem(new Item("QUERYABLE", fld.isQueryable()?"Y":"N" ));
               col.addItem(new Item("MANDATORY", fld.isMandatory()? "Y":"N" ));
               col.addItem(new Item("READONLY",  fld.isReadOnly()? "Y":"N" ));
               col.addItem(new Item("SHOWLABEL", fld.isSimple()? "N":"Y" ));
            }

            dest.addItem("DATA",col);
         }
         RowComparator comp = new RowComparator(lay.getASPManager().getServerFormatter(),"ORDER",true);
         Buffers.sort(dest,comp);

         AutoString out = new AutoString();
         out.appendInt(all_count);
         out.append(" ",apply_lay_profile?"Y":"N");
         for( int i=0; i<dest.countItems(); i++)
         {
            Buffer col = dest.getBuffer(i);
            out.append(" ", col.getString("NAME"),    ":" );
            out.append(     col.getString("VISIBLE"), "," );
            out.appendInt(  col.getInt("SIZE") );
            out.append( "," , col.getString("QUERYABLE"), "," );
            out.append(     col.getString("MANDATORY"), "," );
            out.append(     col.getString("READONLY") ,",");
            out.append(     col.getString("SHOWLABEL") );
         }

         String res = out.toString();
         //if(DEBUG) debug("  result="+res);
         return res;
      }
      catch( Throwable any )
      {
         throw new FndException("FNDBLKPRFFMTERR: Cannot format profile for ASPBlockLayout.")
                   .addCaughtException(any);
      }
   }


   /**
    * Create a Buffer with profile information from an existing ASPPoolElement.
    * Called from ASPBlockLayout.getProfile() to support user interface.
    */
   protected void save( ASPPoolElement target, Buffer dest ) throws FndException
   {
      try
      {
         ASPBlockLayout lay = (ASPBlockLayout)target;
             
         if(!lay.getIsPropertiesPage())
         {
         int all_count = lay.getColumnCount();
         //int col_order = 0;
         for( int index=0; index<all_count; index++ )
         {
            ASPField fld = lay.getColumn(index);
            if( fld.isHidden() ) continue;
            //col_order++;
            Buffer col = dest.newInstance();
            String fld_name = fld.getName();
            col.addItem("NAME",fld_name);
            col.addItem("LABEL",fld.getLabel());

            if (columns.containsKey(fld_name))
            {
               Field prof_fld = (Field) columns.get(fld_name);

               int nr = findColumnNr(fld_name);

               col.addItem(new Item("SIZE",  "N", null, ""+prof_fld.getSize()));
               col.addItem(new Item("HEIGHT","N", null, ""+prof_fld.getHeight()));
               col.addItem(new Item("SPAN",  "N", null, ""+prof_fld.getSpan()));
               col.addItem(new Item("ORDER", "N", null, ""+(nr+1)));
               col.addItem("VISIBLE", "Y");

               col.addItem(new Item("QUERYABLE", fld.isQueryable()?((prof_fld.isQueryable())?"Y":"N"):"N" ));
               col.addItem(new Item("MANDATORY", fld.isMandatory()? "Y":(prof_fld.isMandatory())?"Y":"N" ));
               col.addItem(new Item("READONLY",  fld.isReadOnly()? "Y":(prof_fld.isReadonly())?"Y":"N" ));
               col.addItem(new Item("SHOWLABEL", fld.isSimple()? "N":(prof_fld.showlabel()?"Y":"N") ));
            }
            else //non visible columns should be set to default
            {
               col.addItem(new Item("SIZE",  "N", null, ""+fld.getSize()));
               col.addItem(new Item("HEIGHT","N", null, ""+fld.getHeight()));
               col.addItem(new Item("SPAN",  "N", null, ""+fld.getDataSpan()));
               col.addItem(new Item("ORDER", "N", null, ""));
               col.addItem("VISIBLE", "N");

               col.addItem(new Item("QUERYABLE", fld.isQueryable()? "Y":"N" ));
               col.addItem(new Item("MANDATORY", fld.isMandatory()? "Y":"N" ));
               col.addItem(new Item("READONLY",  fld.isReadOnly()? "Y":"N" ));
               col.addItem(new Item("SHOWLABEL", fld.isSimple()?"N":"Y" ));

            }

            //necessary when generating the user interface to show relevant rows in each tab
            col.addItem(new Item("SYS_QUERYABLE", fld.isQueryable()? "Y":"N" ));
            col.addItem(new Item("SYS_MANDATORY", fld.isMandatory()? "Y":"N" ));
            col.addItem(new Item("SYS_READONLY",  fld.isReadOnly()? "Y":"N" ));
            //col.addItem(new Item("APPLY_LAY_PROFILE", apply_lay_profile? "Y":"N" ));  //uncessary duplication maybe a bit ugly

            dest.addItem("DATA",col);
         }

         RowComparator comp = new RowComparator(lay.getASPManager().getServerFormatter(),"ORDER",true);
         Buffers.sort(dest,comp);          

         if(DEBUG)
            lay.getASPManager().newASPBuffer(dest).traceBuffer("BlockLayoutProfile.save()");
      }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDBLOCKPRFSAVEERR: Cannot save profile for ASPTBlocklayout: (&1)",any.getMessage())
         .addCaughtException(any);
      }
   }

   /**
    * Apply profile information stored in the given Buffer on an ASPPoolElement.
    * Called from ASPBlockLayout.saveProfile() to save user changes.
    */
   protected void load( ASPPoolElement target, Buffer source ) throws FndException
   {
      try
      {
         ASPBlockLayout lay = (ASPBlockLayout)target;
         
         if(lay.getIsPropertiesPage())
         {
            isBlockVisible = source.getItem("BlockVisible").getValue().toString();
         }
         else
         {
            if(DEBUG) lay.getASPManager().newASPBuffer(source).traceBuffer("BlockLayoutProfile.load()");
            int item_count = source.countItems();
            dialog_column_count = 0;
            column_count = 0;
            for( int i=0; i<item_count; i++ )
            {
               Buffer col = source.getBuffer(i);
               Item order = col.getItem("ORDER");
               order.setType("N");
               if( !Str.isEmpty(order.getString()) )
                  column_count++;
            }

            RowComparator comp = new RowComparator(lay.getASPManager().getServerFormatter(),"ORDER",true);
            Buffers.sort(source,comp);

            column_order = new String[column_count];
            columns      = new HashMap();
            column_count = 0;

            //apply_lay_profile = "Y".equals(lay.getASPManager().readValue("APPLY_LAY_PROFILE")); //set from the checkbox in the Layout UI
            if (lay.hasDefinedGroups())
            {
               Vector groupVec = lay.getDefinedGroups();
               group_count = groupVec.size();
               groups = new int[group_count];
               group_fields = new String[group_count];

               for (int nr=0; nr<group_count; nr++)
               {
                  ASPBlockLayout.group group = (ASPBlockLayout.group)groupVec.elementAt(nr);
                  groups[nr] = group.id;
                  group_fields[nr] = "";
               }
            }


            for( int i=0; i<item_count; i++ )
            {
               Buffer col = source.getBuffer(i);
               Item order = col.getItem("ORDER");
               if( !Str.isEmpty(order.getString()) )
               {
                  String name = col.getString("NAME");
                  column_order[column_count] = name;

                  String sizestr = col.getString("SIZE",null);
                  if( Str.isEmpty(sizestr) ) sizestr = "-1";
                  int size = Integer.parseInt(sizestr);
                  if( size<0 ) size = (lay.findColumn(name)).getSize();

                  String heightstr = col.getString("HEIGHT","1");
                  int height = Integer.parseInt(heightstr);
                  //if( height<0 ) height = (lay.findColumn(name)).getHeight();

                  String spanstr = col.getString("SPAN","1");
                  int span = Integer.parseInt(spanstr);
                  //if( span<0 ) span = (lay.findColumn(name)).getDataSpan();


                  boolean queryable = "Y".equals(col.getString("QUERYABLE"));
                  boolean readonly = "Y".equals(col.getString("READONLY"));
                  boolean mandatory = "Y".equals(col.getString("MANDATORY"));
                  boolean show_label = "Y".equals(col.getString("SHOWLABEL"));

                  columns.put(name, new Field(size, height, span,
                                              queryable, readonly,
                                              mandatory, show_label));

                  int group_id = lay.findColumnGroup(name);
                  if (group_id > -1)
                     appendFieldList(name, findGroupIndex(group_id));

                  column_count++;
               }
            }
         }
      }
      catch( Throwable any )
      {
         throw new FndException("FNDBLKPRFLOD: Cannot load profile for ASPBloackLayout: (&1)",any.getMessage())
                   .addCaughtException(any);
      }
   }

   private int findGroupIndex(int group_id)
   {
      for (int i=0; i<groups.length; i++)
         if (groups[i] == group_id) return i;

      return -1;
   }

   private void appendFieldList(String name, int index)
   {
      group_fields[index] += Str.isEmpty(group_fields[index])?name:","+name;
   }

   //==========================================================================
   // New profile handling
   //==========================================================================

   /**
    * Inherited interface.
    * Deserialize profile information in a given string to a ProfileBuffer
    * without needing to access any run-time objects.
    * Used for conversion between the old and new profile framework.
    * Set state of each simple item (instance of ProfileItem to QUERIED).
    */
   protected void parse( ProfileBuffer buffer, BufferFormatter fmt, String text ) throws FndException
   {
      //throw new FndException("Profile parsing for class '&1' is not implemented.", getClass().getName());
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
   //
   //==========================================================================

   protected int getColumnCount()
   {
      return column_count;
   }

   protected String getColumnName(int nr)
   {
      return column_order[nr];
   }

   private int findColumnNr( String name )
   {
      for( int nr=0; nr<column_count; nr++ )
        if( name != null && name.equals(column_order[nr]) ) 
            return nr;

      return -1;
   }

   protected int getColumnSize( String name )
   {
      return ((Field)columns.get(name)).getSize();
   }

   protected int getColumnHeight( String name )
   {
      return ((Field)columns.get(name)).getHeight();
   }

   protected int getColumnSpan( String name )
   {
      return ((Field)columns.get(name)).getSpan();
   }

   protected boolean isColumnQueryable( String name )
   {
      return ((Field)columns.get(name)).isQueryable();
   }

   protected boolean isColumnMandatory( String name )
   {
      return ((Field)columns.get(name)).isMandatory();
   }

   protected boolean isColumnReadOnly( String name )
   {
      return ((Field)columns.get(name)).isReadonly();
   }

   protected boolean isLabelVisible( String name )
   {
      return ((Field)columns.get(name)).showlabel();
   }

   protected boolean applyLayProfile( )
   {
      return apply_lay_profile;
   }

   protected String getGroupField(int group_id)
   {
      int index = 0;
      for (index=0; index<group_count; index++)
      {
         if (groups[index] == group_id) break;
      }
      return group_fields[index];
   }
   
 /**
  * This method creates a buffer containing nested items for each field (as shown below) using the buffer received 
  * in the assign() method which has a flat structure. This method should be called before sorting the buffer since
  * sorting is done based on the ^Position item.
  *  
  *   0:$FIELD01=:
  *      !
  *      0:$^Visible=Y
  *      1:$^Span=1
  *      2:$^Size=21
  *      3:$^Position=2   
  *        ...  
  *   1:$FIELD02=:
  *      !
  *      0:$^Visible=Y
  *      1:$^Span=1
  *      2:$^Size=10
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
             
      if(DEBUG) ProfileUtils.debug("BlockLayoutProfile.updateMembers():", nestedbuf);

      // 2. Update member variables. Basicaly the same functionality as in the deprecated parse().
      //   - number of user columns:           column_count
      //   - size of each user column:         column_size[]
      //   - index to array ASPBlockLayout.columns:  column_index[]

      ASPBlockLayout layout = (ASPBlockLayout)target;
      int prf_col_count = nestedbuf.countItems();
      boolean profile_changed = false;

      column_count = layout.getColumnCount();
      column_order = new String[column_count];
      columns      = new HashMap();
      String[] nonvisible_list = new String[prf_col_count];

      if( prf_col_count!=column_count )
      {
         if(DEBUG) debug("\n\t\tcolumn_count  = "+column_count+ "\n\t\tprf_col_count = "+prf_col_count );
         profile_changed = true;
      }

      if (layout.hasDefinedGroups())
      {
         Vector groupVec = layout.getDefinedGroups();
         group_count = groupVec.size();
         groups = new int[group_count];
         group_fields = new String[group_count];

         for (int nr=0; nr<group_count; nr++)
         {
            ASPBlockLayout.group group = (ASPBlockLayout.group)groupVec.elementAt(nr);
            groups[nr] = group.id;
            group_fields[nr] = "";
         }
      }

      int nr = 0;      
      for( int i=0; i<prf_col_count; i++ )
      {
         Item    item    = nestedbuf.getItem(i);// itr.next();
         String  name    = item.getName();
         Buffer  buf     = item.getBuffer();
         int     size    = buf.getInt(SIZE   , -1 );  //TODO: better error handling, if the item is missing or have the malformed value. Warning? To Alert?
         int     height  = buf.getInt(HEIGHT , -1 );  //TODO: same as above
         int     span    = buf.getInt(SPAN   , -1 );

         boolean visible   = "Y".equals( buf.getString(VISIBLE   , "Y") );
         boolean readonly  = "Y".equals( buf.getString(READONLY  , "N") );
         boolean queryable = "Y".equals( buf.getString(QUERYABLE , "Y") );
         boolean mandatory = "Y".equals( buf.getString(MANDATORY , "N") );
         boolean label     = "Y".equals( buf.getString(LABEL     , "Y") );

         ASPField fld = layout.findColumn(name);
         if(DEBUG) debug("  Fetched column ["+name+"]");

         if(fld == null)
         {
            // just jump over the invalid column
            if(DEBUG) debug("  Found invalid column: "+name );
            profile_changed = true;
            continue;
         }

         if( !fld.isHidden() && ( visible /*|| fld.isMandatory()*/ ) )
         {
            column_order[nr] = name;
            //make sure not to override settings done in the code..but what abt changes done in code other than preDefine()
            columns.put(name, new Field( size  <0 ? fld.getSize()     : size,
                                         height<0 ? fld.getHeight()   : height,
                                         span  <0 ? fld.getDataSpan() : span,
                                         fld.isQueryable() ? queryable : false,
                                         fld.isReadOnly()  ? true      : readonly,
                                         fld.isMandatory() ? true      : mandatory,
                                         fld.isSimple()    ? false     : label ));
            nr++;

            if(!visible)
            {
               if(DEBUG) debug("  Mandatory attribute for column ["+name+"] has been changed (nr="+nr+").");
               profile_changed = true;
            }
         }
         else
         {
            if(DEBUG) debug("  Unvisible column ["+name+"] (nr="+nr+").");
            nonvisible_list[i] = name;
            if(visible)
            {
               if(DEBUG) debug("  Hidden attribute for column ["+name+"] has been changed.");
               profile_changed = true;
            }
         }

         int group_id = layout.findColumnGroup(name);
         if (group_id > -1)
            appendFieldList(name, findGroupIndex(group_id));

      }

      int visible_cnt = nr;
      for( int index=0; index<column_count; index++ )
      {
         ASPField fld = layout.getColumn(index);
         String fld_name = fld.getName();

         if(DEBUG) debug("  Check column ["+fld_name+"] with index "+index);
         if( fld.isHidden() ) continue;
         boolean new_column = true;

         for( int i=0; i<Math.max(visible_cnt,prf_col_count); i++)
         {
            if( i<visible_cnt   && fld_name.equals(column_order[i])    ||
                i<prf_col_count && fld_name.equals(nonvisible_list[i]) )
            {
               new_column = false;
               break;
            }
         }
         if(!new_column) continue;

         column_order[nr] = fld_name;
         columns.put(fld_name, new Field(fld.getSize(),
                                         fld.getHeight(),
                                         fld.getDataSpan(),
                                         fld.isQueryable(),
                                         fld.isReadOnly(),
                                         fld.isMandatory(),
                                         !fld.isSimple()) );

         int group_id = layout.findColumnGroup(fld_name);
         if (group_id > -1)
            appendFieldList(fld_name, findGroupIndex(group_id));

         nr++;
      }
      column_count = nr;


      if(DEBUG)
      {
         debug("\n\t\tcolumn_count  = "+column_count);

         for (int i=0; i<column_count; i++)
         {
            Field fld = (Field)columns.get(column_order[i]);
            debug("\n\t\tcolumn name= "+column_order[i]+
                  "\n\t\tcolumn size= "+fld.getSize()+
                  "\n\t\tcolumn height= "+fld.getHeight()+
                  "\n\t\tcolumn span= "+fld.getSpan()+
                  "\n\t\tcolumn queriable= "+fld.isQueryable()+
                  "\n\t\tcolumn mandatory= "+fld.isMandatory()+
                  "\n\t\tcolumn readonly= "+fld.isReadonly()+
                  "\n\t\tcolumn showlabel= "+fld.showlabel());
         }
      }

      if(profile_changed)
         layout.trace("The profile information for table ["+layout.getName()+"]has been changed.");
   }

   /**
    * Update profile buffer to reflect the current state of this instance
    */
   private void updateProfileBuffer( ASPPoolElement target )  throws FndException
   {       
      if(DEBUG) debug("ASPTableProperties.updateProfileBuffer()");
      
      if( profbuf==null )
         profbuf = ProfileUtils.newProfileBuffer();

      ASPBlockLayout layout = (ASPBlockLayout)target;
      
      if(layout.getIsPropertiesPage())
      {
         ProfileUtils.findOrCreateNestedItem(profbuf,ISBLOCKVISIBLE).setValue(isBlockVisible);
      }
      else
      {
      int  all_count = layout.getColumnCount();
      for( int index=0; index<all_count; index++ )
      {
         ASPField fld = layout.getColumn(index);
         if( fld.isHidden() ) continue;
         String name = fld.getName();         

         if (columns.containsKey(name))
         {
            Field prof_fld = (Field) columns.get(name);
            int nr = findColumnNr(name);
        
            ProfileUtils.findOrCreateNestedItem(profbuf, name+VISIBLE) .setValue("Y");            
            ProfileUtils.findOrCreateNestedItem(profbuf, name+SIZE)    .setValue(prof_fld.getSize());
            ProfileUtils.findOrCreateNestedItem(profbuf, name+HEIGHT)  .setValue(prof_fld.getHeight());
            ProfileUtils.findOrCreateNestedItem(profbuf, name+SPAN)    .setValue(prof_fld.getSpan());
            ProfileUtils.findOrCreateNestedItem(profbuf, name+POSITION).setValue(new Integer(nr+1));

            ProfileUtils.findOrCreateNestedItem(profbuf, name+QUERYABLE).setValue(prof_fld.isQueryable()?"Y":"N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+READONLY).setValue(prof_fld.isReadonly()?"Y":"N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+MANDATORY).setValue(prof_fld.isMandatory()?"Y":"N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+LABEL).setValue(prof_fld.showlabel()?"Y":"N");
         }
         else
         {
            ProfileUtils.findOrCreateNestedItem(profbuf, name+VISIBLE) .setValue("N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+SIZE)    .setValue(fld.getSize() );
            ProfileUtils.findOrCreateNestedItem(profbuf, name+HEIGHT)  .setValue(fld.getHeight() );
            ProfileUtils.findOrCreateNestedItem(profbuf, name+SPAN)    .setValue(fld.getDataSpan() );
            ProfileUtils.findOrCreateNestedItem(profbuf, name+POSITION).setValue( null );

            ProfileUtils.findOrCreateNestedItem(profbuf, name+QUERYABLE).setValue(fld.isQueryable()?"Y":"N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+READONLY).setValue(fld.isReadOnly()?"Y":"N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+MANDATORY).setValue(fld.isMandatory()?"Y":"N");
            ProfileUtils.findOrCreateNestedItem(profbuf, name+LABEL).setValue(fld.isSimple()?"N":"Y");
         }
      }     
      }      
   }

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

   /*void showContents( AutoString out ) throws FndException
   {
      out.append("   ",this.toString(),":\n");

      out.append("    column_count  = ");
      out.appendInt(column_count);
      out.append("\n");
      out.append("    column_index[]= ",debugIntArray(column_index,column_count),"\n");
      out.append("    column_size[] = ",debugIntArray(column_size,column_count),"\n");
   }
   */

   private class Field implements Serializable
   {
      private int size;             // size of user column
      private int height;           // height of text area column, 1 for normal fields
      //private int index;            // index (field order)
      private int span;             //data column span
      private boolean queryable;
      private boolean readonly;
      private boolean mandatory;
      private boolean showlabel;    // show/hide label

      Field(int s, int h, int sp, boolean q, boolean r, boolean m, boolean show)
      {
         size = s;
         height = h;
         span = sp;
         queryable = q;
         readonly = r;
         mandatory = m;
         showlabel = show;
      }

      int getSize()
      {
         return size;
      }

      int getHeight()
      {
         return height;
      }

      int getSpan()
      {
         return span;
      }

      boolean isQueryable()
      {
         return queryable;
      }

      boolean isReadonly()
      {
         return readonly;
      }

      boolean isMandatory()
      {
         return mandatory;
      }

      boolean showlabel()
      {
         return showlabel;
      }
   }

}


