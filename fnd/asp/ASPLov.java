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
 * File        : ASPLov.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Mar-12 - Created.
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-Sep-01 - Added call to ASPManager.translate() from
 *                           doActivate() method. Changes in generateHTML().
 *    Stefan M 1999-Dec-21 - Added generateDefinition(), for dynamic LOVs.
 *                           Called from Common/Scripts/DynamicLov.page.
 *    Stefan M 2000-Jul-05 - ASPManager.setPageExpiring() is called from generateDefinition().
 *    Kingsly P 2001-May-16 - Add URLDecode in generate().
 *    Artur K  2001-May-23 - Log id 713, 714. Changed doActive() and generate() functions.
 *    Ramila H 2001-Sep-12 - Moved the mutable methods from the default constructor to the
 *                           construct() method to make cloning possible for dynamic and 
 *                           non-dynamic LOVs.
 *    Jacek P  2001-Sep-13 - Changed maximum field length to 50 in generateDefinition().
 *    Suneth M 2002-Jul-24 - Log id 869. Changed generate(),generateHTML() & countFind(). 
 *    Suneth M 2002-Aug-08 - Log id 869. Changed generate() & okFind().
 *    Rifki R  2002-Oct-09 - Disabled Output Channels popup options.
 *    Ramila H 2002-Dec-13 - Log id 933. Added support to save delete queries in LOVs 
 *    Suneth M 2002-Dec-18 - Log id 1002. Changed generate() to enable/disable row select checkbox.
 *    ChandanaD2003-Jun-05 - Added left margin for the msg_txt.
 *    Mangala  2003-Jul-17 - Removed the use of depricated method setTitleWrap. 
 *    Sampath  2003-Jul-31 - Insert the mask of number fields if it's the keyfield in the lov page
 *    Suneth M 2003-Sep-03 - Changed generateHTML() to add a help message.
 *    Suneth M 2003-Dec-11 - Bug 40909, Changed countFind(). 
 *    Suneth M 2003-Dec-19 - Bug 40900, Added new LOV property FORMAT_MASK to handle the format mask 
 *                           of the fields in the dynamic LOV. Changed generateDefinition().
 *    Suneth M 2004-Apr-26 - Bug 43550, Changed cancelFind(),find() & generateHTML(). 
 *    ChandanaD2004-May-12 - Updated for new stylesheets. 
 *    Suneth M 2004-Aug-24 - Changed generate() to fix minor bugs.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/03/26 sumelk - Bug 89672, Changed first() and last() to work correctly in multirow layout. 
 * 2009/05/15  amiklk Bug 82633, added updateColumnTitles() and Modified generateHTML().
 * 2008/06/26  mapelk Bug 74852, Programming Model for Activities. 
 * 2007/04/24  sadhlk
 * Bug Id 64992, Modified generateHTML() to correct translation issue.
 * 2005/11/21  rahelk
 * Fixed Call id 128794: Enabled row select in construct
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/02/14 10:12:12  mapelk
 * Fixed bug related to max data size
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.3  2004/12/10 10:11:55  riralk
 * Disabled Properties button from command bar
 *
 * Revision 1.2  2004/11/25 05:58:01  chdelk
 * Added support for Activity APIs based LOVs.
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;


import ifs.fnd.util.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;
import ifs.fnd.ap.*;

import java.util.*;

import java.lang.reflect.*;

/**
 *
 */
public class ASPLov extends ASPPageElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public  static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPLov");

   private static final String  WHERE = "__WHERE";

   private static final String  GROUP_BY = "__GROUP_BY";
   private static final String  ORDER_BY = "__ORDER_BY";

   private static final String  AUTO  = "__AUTO_SEARCH";
   private static final String  TITLE = "__TITLE";
   
   // Added by Terry 20130813
   // Tree list lov
   // Fields display in tree
   private static final String  TREE_DISP_FIELD = "__TREE_DISP_FIELD";
   // Parent field in tree dataset
   private static final String  TREE_PARE_FIELD = "__TREE_PARE_FIELD";
   // Field type in tree
   private static final String  TREE_TYPE_FIELD = "__TREE_TYPE_FIELD";
   // Only select one type in tree
   private static final String  TREE_ONLSEL_TYPE = "__TREE_ONLSEL_TYPE";
   // Selected value in tree
   private static final String  TREE_SELE_VALUE = "__TREE_SELE_VALUE";
   // Added end
   
   // Bug 40900, start
   private static final String  FORMAT_MASK = "__FORMAT_MASK";   
   // Bug 40900, end
   private int max_rows = 0;

   //==========================================================================
   // Instance variables
   //==========================================================================

   // immutable
   private ASPTable       tbl;
   private ASPBlock       blk;
   private ASPCommandBar  bar;
   private ASPBlockLayout lay;

   // mutable - sets for each request in doActivate()
   private String        group_by;
   private String        order_by;

   private String        where;
   
   // Added by Terry 20120822
   // Set first where property
   private String        first_where;
   // Added end
   
   // Added by Terry 20130813
   // Tree list lov
   // Fields display in tree
   private String        tree_disp_field;
   // Parent field in tree dataset
   private String        tree_pare_field;
   // Field type in tree
   private String        tree_type_field;
   // Only select one type in tree
   private String        tree_onlsel_type;
   
   private String        tree_key;
   private String        tree_view;
   private String        tree_selected_value;
   private String img_loc;
   private TreeList lov_tree;
   // Added end
   
   private String        title;
   // Bug 40900, start
   private String        format_mask;
   // Bug 40900, end
   private int           skipped_rows;
   private int           db_pos;
   private int           db_rows;

   private AutoString    out = new AutoString();

   private String msg_txt;
   private int no_of_hits;
   
   // Added by Terry 20120929
   // Save user selection in buffer at multi-select mode.
   private String page_name = getClass().getName().toUpperCase();
   private String client_function = "";
   // Added end
   
   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor.
    */
   ASPLov( ASPPage page )
   {
      super(page);
      if(DEBUG) debug("ASPLov.constructor");

      tbl = page.getASPTable();
      //blk = tbl.getBlock();
      blk = page.getASPBlock();
      lay = blk.getASPBlockLayout();
      max_rows = Integer.parseInt(page.getASPConfig().getParameter("ADMIN/MAX_LOV_ROWS","0"));
   }

   /**
    * Package construct() called by defineLOV() function within ASPPage.
    */
   ASPLov construct( int width, int height )
   {
      if(DEBUG) debug("ASPLov.construct("+width+","+height+")");
      
     
      ASPForm frm = getASPPage().getASPForm();
      frm.setFormWidth  ( width  - 45 );
      frm.setFormHeight ( height - 30 );

      //tbl.enableQueryRow();
      tbl.enableRowSelect();
      tbl.disableEditProperties();
      tbl.disableOutputChannels();
      tbl.disableRowCounter();
      tbl.disableQuickEdit();

      bar = blk.newASPCommandBar();
      //bar.removeCommandGroup(bar.CMD_GROUP_EDIT);
      //bar.removeCommandGroup(bar.CMD_GROUP_CUSTOM);
      bar.disableCommand(bar.PROPERTIES);
      bar.setCounterDbMode();

      doActivate();
      return this;
   }

   /**
    * Private construct() - called by clone()
    */
   private ASPLov construct( ASPLov lov )
   {
      this.bar = this.blk.getASPCommandBar();
      return this;
   }

   /**
    * Called from Common/scripts/DynamicLov.page
    *
    * Creates ASPFields for the specified view.
    * Fields included and formats used on those fields are determined from the view comments.
    */
   public ASPLov generateDefinition( String view )
   {
       return generateDefinition(view, null, null, null);
   }
   
   /**
    * Called from Common/scripts/DynamicLov.page
    * Used for LOVs created using activity APIs.
    * @param view Name of the entity related to this LOV.
    * @param key Name of the key field.
    * @param handler Name of the activity handler.
    * @param operation Name of the activity operation.
    */
   public ASPLov generateDefinition( String view, String key, String handler, String operation)
   {
      ASPManager mgr = getASPManager();

      mgr.setPageExpiring();

      String keys    = "";
      String cols    = "";
      String prompts = "";
      String types   = "";
      
      ASPCommand defcmd = (new ASPCommand(mgr)).construct();
      defcmd.defineCustom("REFERENCE_SYS.Get_Lov_Properties");
      defcmd.addParameter("VIEW", "S", "IN", view);
      defcmd.addParameter("KEYS","S", "OUT", null);
      defcmd.addParameter("COLS","S", "OUT", null);
      defcmd.addParameter("PROMPTS","S", "OUT", null);
      defcmd.addParameter("TYPES","S", "OUT", null);

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      trans.addCommand("LOVPROPS", defcmd);
      trans = mgr.performConfig(trans);

      // Modified by Terry 20120822
      // Original: keys    = trans.getValue("LOVPROPS/DATA/KEYS");
      if (mgr.isEmpty(key))
         keys = trans.getValue("LOVPROPS/DATA/KEYS");
      else
         keys = key;
      // Modified end
      cols    = trans.getValue("LOVPROPS/DATA/COLS");
      prompts = trans.getValue("LOVPROPS/DATA/PROMPTS");
      types   = trans.getValue("LOVPROPS/DATA/TYPES");

      if(DEBUG)
      {
         debug("view: " + view);
         debug("keys: " + keys);
         debug("cols: " + cols);
         debug("prompts: " + prompts);
         debug("types: " + types);
      }

      //ASPBlock blk = getASPPage().getASPBlock("LOV");
      blk.setView(view);
      
      ASPField f;

      StringTokenizer st_cols    = new StringTokenizer(cols,",");
      StringTokenizer st_prompts = new StringTokenizer(prompts,"^");
      StringTokenizer st_types   = new StringTokenizer(types,"^");

      StringTokenizer str_toz = new StringTokenizer(keys,",");
      String tempkey = "";

      // Bug 40900, start
      HashMap fields = new HashMap();
      if (!Str.isEmpty(format_mask))
      {
         StringTokenizer st_masks = new StringTokenizer(format_mask,"^"); 
         String fieldName = "";
         String fieldMask = "";
         String value = "";         
         
         while (st_masks.hasMoreTokens())
         {
            value = st_masks.nextToken(); 
            fieldName = value.substring(0,value.indexOf("="));
            fieldMask = value.substring(value.indexOf("=") + 1,value.length());
            fields.put(fieldName, fieldMask);
         }
      }
      // Bug 40900, end

      while(str_toz.hasMoreTokens())
         tempkey= str_toz.nextToken();
      
      while( st_cols.hasMoreTokens() )
      {
         String col = st_cols.nextToken();
         String type = "";
         String fieldFormat = "";
         try
         {
            StringTokenizer st_coltype = new StringTokenizer(st_types.nextToken(),"/");
            type = st_coltype.nextToken();
            fieldFormat = st_coltype.nextToken();
         }
         catch (NoSuchElementException e)
         {
            // no format
         }

         String fieldType;
         int fieldSize;
         if(type.indexOf("(") > -1)
         {
            fieldType = type.substring(0,type.indexOf("("));
            fieldSize = Integer.parseInt(type.substring(type.indexOf("(") + 1,type.length()-1));
         }
         else
         {
            fieldType = type;
            fieldSize = 0;
         }
         if(DEBUG) debug("Adding field:" + col + ". Type: " + fieldType + " (" + fieldSize + "). fieldFormat = " + fieldFormat);

         if (fieldType.compareTo("STRING") == 0)
         {
            f = blk.addField(col);
            if(fieldFormat.compareTo("UPPERCASE") == 0) f.setUpperCase();
         }
         else if (fieldType.compareTo("NUMBER") == 0)
         {
            if(fieldFormat.compareTo("DECIMAL") == 0) f = blk.addField(col,"Number",".00");
            else if(fieldFormat.compareTo("MONEY") == 0) // f = blk.addField(col,"Money");
            {
               if(!mgr.isEmpty(tempkey))
               {
                  String mask = mgr.URLDecode(mgr.getQueryStringValue("__MASK"));
                  if(!mgr.isEmpty(mask) && tempkey.equals(col))
                     f = blk.addField(col,"Money",mask);
                  // Bug 40900, start
                  else if(!Str.isEmpty(format_mask) && fields.containsKey(col))
                      f = blk.addField(col,"Money",fields.get(col).toString());
                  // Bug 40900, end
                  else
                     f = blk.addField(col,"Money");
               }
               else
                  f = blk.addField(col,"Money");
               
            }
            else
            {
               if(!mgr.isEmpty(tempkey))
               {
                  String mask = mgr.URLDecode(mgr.getQueryStringValue("__MASK"));
                  if(!mgr.isEmpty(mask) && tempkey.equals(col))
                     f = blk.addField(col,"Number",mask);
                  // Bug 40900, start
                  else if(!Str.isEmpty(format_mask) && fields.containsKey(col))
                      f = blk.addField(col,"Number",fields.get(col).toString());
                  // Bug 40900, end
                  else
                     f = blk.addField(col,"Number");
               }
               else
                  f = blk.addField(col,"Number");
            }
            if(fieldFormat.compareTo("PERCENTAGE") == 0) f.setLabel(f.getLabel() + " %");
         }
         else if (fieldType.compareTo("DATE") == 0)
         {
            if(fieldFormat.compareTo("DATE") == 0) f = blk.addField(col,"Date");
            else if(fieldFormat.compareTo("TIME") == 0) f = blk.addField(col,"Time");
            else if(fieldFormat.compareTo("DATETIME") == 0) f = blk.addField(col,"Datetime");
            else f = blk.addField(col,"Date");
         }
         else if (fieldType.compareTo("BOOLEAN") == 0)
         {
            f = blk.addField(col,"Boolean");
            f.setCheckBox("FALSE,TRUE");
         }
         else
         {
            // other (?); string
           f = blk.addField(col);
           if(DEBUG) debug("Other added.");

         }
         if(fieldFormat.compareTo("INVISIBLE") == 0) f.setHidden();

         if(fieldSize > 0) f.setSize(fieldSize);
         //if(fieldSize > 0) f.setTitleWrap(fieldSize);
         //if(fieldSize > 100) f.setSize(100);
         if(fieldSize > 50) f.setSize(50);
         f.setLabel(st_prompts.nextToken());

         tbl.addColumn(col);
      }

      // Add keys, and set the last key (normally only one key) to be er, the Key.
      addFields(keys,true);
      
      // Fetch all fields in this view
      // Add those that are not already defined as LOV fields.

      addRest(view);

      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

      return this;
   }

   void addRest(String view)
   {
      ASPManager mgr = getASPManager();

      ASPCommand defcmd = (new ASPCommand(mgr)).construct();
      defcmd.defineCustom("REFERENCE_SYS.Get_View_Properties");
      defcmd.addParameter("VIEW_NAME_", "S", "IN", view);
      defcmd.addParameter("COL_NAMES_","S", "OUT", null);
      defcmd.addParameter("COL_PROMPTS_","S", "OUT", null);
      defcmd.addParameter("COL_TYPES_","S", "OUT", null);

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      trans.addCommand("VIEWPROPS", defcmd);
      trans = mgr.performConfig(trans);

      String cols    = trans.getValue("VIEWPROPS/DATA/COL_NAMES_");

      if(DEBUG)
      {
         debug("all cols: " + cols);
      }

      addFields(cols,false);
   }

   void addFields( String field_list, boolean setKey )
   {
      StringTokenizer st_keys = new StringTokenizer(field_list,",");

      String this_field;
      ASPField f;
      boolean found = false;
      if(DEBUG) debug("********* addFields");
      while( st_keys.hasMoreTokens() )
      {
         this_field = st_keys.nextToken();
         if(DEBUG) debug("adding field: " + this_field);

         found = false;
         StringTokenizer st_fields = new StringTokenizer(blk.getFieldList(),",");
         while( st_fields.hasMoreTokens() )
            if( st_fields.nextToken().compareTo(this_field) == 0 )
               found = true;

         if( !found )
         {
            if(DEBUG) debug("Not found - adding " + this_field + "!");

            f = blk.addField(this_field);
            f.setHidden();
         }

         if( !st_keys.hasMoreTokens() && setKey )
            tbl.setKey(this_field);
      }

   }
   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze() throws FndException
   {
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
      msg_txt = null;
      no_of_hits = 0;
   }


   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() //throws FndException
   {
      if(DEBUG) debug("ASPLov.doActivate()");
      ASPRowSet  set = blk.getASPRowSet();
      ASPContext ctx = getASPPage().getASPContext();
      ASPManager mgr = getASPManager();

      skipped_rows = set.countSkippedDbRows();
      db_pos       = skipped_rows + set.getCurrentRowNo();
      db_rows      = set.countDbRows();
      where        = ctx.readValue(WHERE, mgr.readValue(WHERE) );
      
      // Added by Terry 20120822
      first_where  = ctx.readValue("__FWHERE", mgr.readValue("__FWHERE") );
      // Added end
      
      group_by        = ctx.readValue(GROUP_BY, mgr.readValue(GROUP_BY) );
      order_by        = ctx.readValue(ORDER_BY, mgr.readValue(ORDER_BY) );
      // Bug 40900, start
      format_mask  = ctx.readValue(FORMAT_MASK, mgr.readValue(FORMAT_MASK) );
      // Bug 40900, end
      title        = ctx.readValue(TITLE, mgr.translate(mgr.readValue(TITLE)) );
      no_of_hits   = db_rows;
      
      // Added by Terry 20130813
      // Tree list lov
      tree_disp_field = ctx.readValue(TREE_DISP_FIELD, mgr.readValue(TREE_DISP_FIELD) );
      tree_pare_field = ctx.readValue(TREE_PARE_FIELD, mgr.readValue(TREE_PARE_FIELD) );
      tree_type_field = ctx.readValue(TREE_TYPE_FIELD, mgr.readValue(TREE_TYPE_FIELD) );
      tree_onlsel_type = ctx.readValue(TREE_ONLSEL_TYPE, mgr.readValue(TREE_ONLSEL_TYPE) );
      // Added end

      if(DEBUG)
      {
         debug("   skipped_rows = " + skipped_rows );
         debug("   db_pos       = " + db_pos       );
         debug("   db_rows      = " + db_rows      );
         debug("   where        = " + where        );
         debug("   group_by     = " + group_by     );
         debug("   order_by     = " + order_by     );
         debug("   format_mask  = " + format_mask  );
         debug("   title        = " + title        );
         debug("   no_of_hits   = " + no_of_hits   );
         debug("   tree_disp_field = " + tree_disp_field  );
         debug("   tree_pare_field = " + tree_pare_field  );
         debug("   tree_type_field = " + tree_type_field  );
         debug("   tree_onlsel_type = " + tree_onlsel_type  );
      }
      
      ASPField[] fields = blk.getFields();
      for( int i=0;i<fields.length; i++ )
      {
         if( !fields[i].isHidden() ) continue;
         String value = ctx.readValue(fields[i].getName());
         if( !Str.isEmpty(value) )
            ctx.writeValue(fields[i].getName(),value);
      }

      if( !Str.isEmpty(where) && !where.equals("null"))
         ctx.writeValue(WHERE, where);
      if( !Str.isEmpty(group_by) )
         ctx.writeValue(GROUP_BY, group_by);
      if( !Str.isEmpty(order_by) )
         ctx.writeValue(ORDER_BY, order_by);
      // Bug 40900, start
      if( !Str.isEmpty(format_mask) )
         ctx.writeValue(FORMAT_MASK, format_mask);
      // Bug 40900, end

      if( !Str.isEmpty(title) )
         ctx.writeValue(TITLE, title);
      
      // Added by Terry 20130813
      // Tree list lov
      if( !Str.isEmpty(tree_disp_field) )
         ctx.writeValue(TREE_DISP_FIELD, tree_disp_field);
      if( !Str.isEmpty(tree_pare_field) )
         ctx.writeValue(TREE_PARE_FIELD, tree_pare_field);
      if( !Str.isEmpty(tree_type_field) )
         ctx.writeValue(TREE_TYPE_FIELD, tree_type_field);
      if( !Str.isEmpty(tree_onlsel_type) )
         ctx.writeValue(TREE_ONLSEL_TYPE, tree_onlsel_type);
      // Added end
   }

   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPLov lov = new ASPLov((ASPPage)page);
      lov.construct(this).setCloned();
      lov.msg_txt = null;
      lov.no_of_hits = 0;
      return lov;
   }


   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
      if( tbl!=null ) tbl.verifyPage(page);
      if( blk!=null ) blk.verifyPage(page);
      if( bar!=null ) bar.verifyPage(page);
   }


   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }


   void generate() throws FndException
   {
      if(DEBUG) debug("ASPLov.generate()");
      
      ASPContext ctx = getASPManager().getASPContext();
      
      String auto = "";
      tree_key = getASPManager().readValue("TREE_KEY");
      if (Str.isEmpty(tree_key))
         tree_key = tbl.getKey();
      tree_view = getASPManager().readValue("__DYNAMIC_LOV_VIEW");
      if (Str.isEmpty(tree_view))
         tree_view = blk.getView();
      tree_selected_value = getASPManager().readValue(TREE_SELE_VALUE);
      
      // Added by Terry 20130813
      // Tree list lov
      img_loc = getASPManager().getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      // Added end
      
      // Modified by Terry 20120822
      // Get MUTICHOICE property
      // Original: String is_multichoice = ctx.readValue("MULTICHOICE");
      String is_multichoice = getASPManager().readValue("__MULTICHOICE");
      if (Str.isEmpty(is_multichoice))
         is_multichoice = getASPManager().readValue("MULTICHOICE");
      if (Str.isEmpty(is_multichoice))
         is_multichoice = ctx.readValue("MULTICHOICE");
      // Modified end

      // Modified by Terry 20121010
      // Original:
      // if (("true".equals(is_multichoice)) && (blk.getASPRowSet().countRows() != 1))
      if ("true".equals(is_multichoice)) 
         tbl.enableRowSelect();
      else
         tbl.disableRowSelect();
      // Modified end
      
      // Added by Terry 20140623
      // Get view param fields from CONTEXT
      String view_param_fields = ctx.readValue("__VIEW_PARAM_FIELDS");
      // Added end

      // Added by Terry 20130814
      // Tree list lov
      if (commandBarActivated())
         commandBarFunction();
      else if(!Str.isEmpty(getASPManager().getQueryStringValue("VALIDATE")))
         validate();
      else if (isTreeListLov())
      {
         okFind(false);
         searchTree();
      }
      // Added end
      else
      {
         auto = getASPManager().readValue(AUTO);

         if( Str.isEmpty(auto) || "Y".equals(auto) )
            okFind(true);
         else
            bar.setLayoutMode(bar.FIND_LAYOUT);
      }
      getASPPage().saveLayout();

      //if(getASPManager().readValue("__INIT") != null)
      //{
         // First request; we need to put all passed field values in the context.
         String qs = getASPManager().getQueryString();
         StringTokenizer params = new StringTokenizer(qs,"&");
         String field = "";
         
         boolean first_save = true;
         while(params.hasMoreTokens())
         {
            field = params.nextToken();
            if(!field.startsWith("__"))
            {
               //debug("************* ADDING HIDDEN VALUE TO CONTEXT");
               //debug(field.substring(0,field.indexOf("=")) + "!");
               //debug(field.substring(field.indexOf("=")+1,field.length()) + "!");
               // Added by Terry 20140623
               // Save view params to context
               if (first_save)
               {
                  view_param_fields = "^";
                  first_save = false;
               }
               String __qs_field_name = field.substring(0,field.indexOf("="));
               String __qs_field_value = getASPManager().URLDecode(field.substring(field.indexOf("=")+1,field.length()));
               if (!Str.isEmpty(__qs_field_value) && !__qs_field_name.equals(tbl.getKey()) && !"MULTICHOICE".equals(__qs_field_name) && !"LOV".equals(__qs_field_name) && !"WIDTH".equals(__qs_field_name) && !"HEIGHT".equals(__qs_field_name))
                  view_param_fields = view_param_fields + __qs_field_name + "^";
               ctx.writeValue(__qs_field_name, __qs_field_value);
               // Added end
            }
         }
      //}
      
      // Added by Terry 20140623
      // Save view params to context
      ctx.writeValue("__VIEW_PARAM_FIELDS", view_param_fields);
      // Added end
      ctx.writeValue("MULTICHOICE",is_multichoice);

      if( no_of_hits==0 )
      {
         bar.setLayoutMode(bar.FIND_LAYOUT);
         if (getASPManager().isEmpty(auto))
            msg_txt = getASPManager().translateJavaText("FNDLOVZEROHITS: 0 hits for this query condition. Please modify and try again.");
         else
            msg_txt = "";
      }
      else if((blk.getASPRowSet().countRows() == 1) && (blk.getASPRowSet().getValue(tbl.getKeyColumnName())).equals(getASPManager().readValue("__KEY_VALUE")))
      {
         // Added by Terry 20130816
         if (!isTreeListLov())
         {
            // Added end
            bar.setLayoutMode(bar.FIND_LAYOUT);
            msg_txt = getASPManager().translateJavaText("FNDLOVALREADYREGISTERED: The value found is already registerd in the form. Please requery to see alternatives.");
         }
      }
      else if( !getASPManager().isEmpty(getASPManager().readValue("__INIT")) && (max_rows != 0) && (no_of_hits > max_rows))
      {
         // Added by Terry 20130816
         if (!isTreeListLov())
         {
            // Added end
            bar.setLayoutMode(bar.FIND_LAYOUT);
            msg_txt = getASPManager().translateJavaText("FNDLOVMORETHANMAXROWS: The query will retrieve more than maximum number of rows. Modify the query condition or press 'Ok' to continue.");
         }
      }
      else
         msg_txt = "";
      
      // Added by Terry 20120929
      // Save user selection in buffer at multi-select mode.
      if (tbl.isRowSelectionEnabled())
         adjustLov();
      // Added end
      generateHTML();
   }

   //==========================================================================
   // Command Bar functions
   //==========================================================================

   // Added by Terry 20130815
   public boolean commandBarActivated()
   {
      String cmd = getASPManager().readValue("__COMMAND");
      return !Str.isEmpty(cmd);
   }
   
   private boolean showTree()
   {
      if (isTreeListLov())
      {
         if (no_of_hits <= 0)
            return false;
         
         if (lay.isFindLayout())
            return false;
         
         String cmd = getASPManager().readValue("__COMMAND");
         if (Str.isEmpty(cmd))
            return true;
         else
         {
            String id = cmd.substring(cmd.indexOf('.')+1);
            if( ASPCommandBar.FIND.equals(id) )
               return false;
            else if( ASPCommandBar.OKFIND.equals(id) )
               return true;
            else if( ASPCommandBar.COUNTFIND.equals(id) )
               return false;
            else if( ASPCommandBar.ADVANCEDFIND.equals(id) )
               return false;
            else if( ASPCommandBar.CANCELFIND.equals(id) )
               return true;
            else if( ASPCommandBar.BACKWARD.equals(id) )
               return true;
            else if( ASPCommandBar.FORWARD.equals(id) )
               return true;
            else if (ASPCommandBar.SAVEQUERY.equals(id))
               return false;
            else if ( ASPCommandBar.DELETEQUERY.equals(id) )      
               return false;
            else if (ASPCommandBar.OKLOV.equals(id))
               return true;
            else if ( id.indexOf("GotoPage") != -1 )
               return true;
         }
      }
      return false;
   }
   // Added end

   private boolean commandBarFunction() throws FndException
   {
      String cmd = getASPManager().readValue("__COMMAND");
      if( Str.isEmpty(cmd) ) return false;

      if(DEBUG) debug("ASPLov.commandBarActivated(): "+cmd);

      String id = cmd.substring(cmd.indexOf('.')+1);

      // Added by Terry 20120929
      // Save user selection in buffer at multi-select mode.
      if (tbl.isRowSelectionEnabled())
      {
         if (ASPCommandBar.FIRST.equals(id) || ASPCommandBar.BACKWARD.equals(id) ||
             ASPCommandBar.LAST.equals(id)  || ASPCommandBar.FORWARD.equals(id)  ||
             ASPCommandBar.OKLOV.equals(id))
            storeSelectionToCtx();
         else
            clearSelectionToCtx();
      }
      // Added end
      
      if( ASPCommandBar.CLEAR.equals(id) )
         clear();
      else if( ASPCommandBar.COUNT.equals(id) )
         count();
      else if( ASPCommandBar.SEARCH.equals(id) )
         search(0);
      else if( ASPCommandBar.FIRST.equals(id) )
         first();
      else if( ASPCommandBar.PREVIOUS.equals(id) )
         previous();
      else if( ASPCommandBar.NEXT.equals(id) )
         next();
      else if( ASPCommandBar.LAST.equals(id) )
         last();
      else if( ASPCommandBar.GOTO.equals(id) )
      {
         String val = getASPManager().readValue("__"+blk.getName()+"_"+ASPCommandBar.GOTO);
         int nr;
         try
         {
            nr = Str.isEmpty(val) ? 0 : Integer.parseInt(val);
         }
         catch( NumberFormatException f )
         {
            nr = 0;
         }
         nr--;
         goTo(nr);
      }
      // new GUI
      else if( ASPCommandBar.FIND.equals(id) )
         find();
      else if( ASPCommandBar.OKFIND.equals(id) )
      {
         getASPManager().saveQuery(blk.getName(),true);
         okFind(true);
         // Added by Terry 20130815
         // Tree Lov
         if (isTreeListLov())
            searchTree();
         // Added end
      }
      else if( ASPCommandBar.COUNTFIND.equals(id) )
         countFind(true);
      // Added by Terry 20130922
      // Advanced Query
      else if( ASPCommandBar.ADVANCEDFIND.equals(id) )
         bar.getBlock().getASPBlockLayout().setAdvancedQuery();
      // Added end
      else if( ASPCommandBar.CANCELFIND.equals(id) )
      {
         cancelFind();
         // Added by Terry 20130815
         // Tree Lov
         okFind(false);
         if (isTreeListLov())
            searchTree();
         // Added end
      }
      else if( ASPCommandBar.BACKWARD.equals(id) )
      {
          int[] saved = {0,0,0,0,0,0,0,0,0,0,0,00,0,0,0};
          getASPManager().saveRowNos(bar,saved);
          bar.getBlock().getASPRowSet().prevDbSet();
          getASPManager().restoreRowNos(bar,saved);
          // Added by Terry 20130815
          // Tree Lov
          if (isTreeListLov())
             searchTree();
          // Added end
//          return "while(false){}";
      }
      else if( ASPCommandBar.FORWARD.equals(id) )
      {
          int[] saved = {0,0,0,0,0,0,0,0,0,0,0,00,0,0,0};
          getASPManager().saveRowNos(bar,saved);
          bar.getBlock().getASPRowSet().nextDbSet();
          getASPManager().restoreRowNos(bar,saved);
          // Added by Terry 20130815
          // Tree Lov
          if (isTreeListLov())
             searchTree();
          // Added end
      }
      else if (ASPCommandBar.SAVEQUERY.equals(id))
      {
         getASPManager().saveQuery(blk.getName(),false);
         find();
      }
      else if ( ASPCommandBar.DELETEQUERY.equals(id) )      
      {
         getASPManager().deleteQuery(blk.getName());
         find();
      }
      // Added by Terry 20120929
      // Lov bar in multi-select mode
      else if (ASPCommandBar.OKLOV.equals(id))
         okLov();
      // Added end
      // Added by Terry 20121120
      // Control goto page command
      else if ( id.indexOf("GotoPage") != -1 )
      {
         if ( bar.isMultirowLayout() )
         {
            int[] saved = {0,0,0,0,0,0,0,0,0,0,0,00,0,0,0};
            getASPManager().saveRowNos(bar,saved);
            int goto_page = Integer.parseInt(id.substring(id.indexOf("(") + 1, id.indexOf(")")));
            bar.getBlock().getASPRowSet().gotoPage(goto_page);
            getASPManager().restoreRowNos(bar,saved);
         }
         // Added by Terry 20130815
         // Tree Lov
         if (isTreeListLov())
            searchTree();
         // Added end
      }
      // Added end
      // end new GUI
      else
         throw new FndException("FNDLOVUNDEFCMD: Undefined command bar function!");

      return true;
   }

   //=============================================================================
   //  Command Bar Search Group functions
   //=============================================================================

   // new functions
   private void find()
   {
      bar.setLayoutMode(bar.FIND_LAYOUT);
      no_of_hits = -1;
   }

   private void okFind(boolean not_from_root)
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      if( !mgr.isEmpty(mgr.readValue("__INIT")) && max_rows != 0 )
      {
         ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();
         ASPQuery qry = buf.addQuery(blk);

         qry.setSelectList("to_char(count(*)) N");
         if( !Str.isEmpty(where) )
            qry.addWhereCondition(where);
         
         // Added by Terry 20120822
         // Set first where property
         if( !Str.isEmpty(first_where) )
         {
            qry.addWhereCondition(first_where);
            first_where = null;
         }
         // Added end
         
         // Added by Terry 20130813
         // Tree list lov
         if (!not_from_root && !Str.isEmpty(tree_pare_field))
            qry.addWhereCondition(tree_pare_field + " IS NULL");
         // Added end

         buf = mgr.perform(buf);

         no_of_hits = Integer.parseInt(buf.getValue(blk.getName()+"/DATA/N"));
      }
      
      if( (max_rows == 0) || (mgr.isEmpty(mgr.readValue("__INIT")) || no_of_hits <= max_rows) )
      {
         bar.setLayoutMode(bar.getHistoryMode());
         ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();
         ASPQuery qry = buf.addQuery(blk);

         if( !Str.isEmpty(where) )
            qry.addWhereCondition(where);

         // Added by Terry 20120822
         // Set first where property
         if( !Str.isEmpty(first_where) )
         {
            qry.addWhereCondition(first_where);
            first_where = null;
         }
         // Added end
         
         // Added by Terry 20130813
         // Tree list lov
         if (!not_from_root && !Str.isEmpty(tree_pare_field))
            qry.addWhereCondition(tree_pare_field + " IS NULL");
         // Added end
         
         if( !Str.isEmpty(order_by) )
            qry.setOrderByClause(order_by);
         // Added by Terry 20140220
         // Check order by of view
         else if (!checkViewOrderby())
         // Added end
         {
            // Modified by Terry 20121010
            // Original:
            // qry.setOrderByClause(tbl.getKeyColumnName());
            // Avoid single key order by confuse
            String keys = getGlobalKeys();
            qry.setOrderByClause(keys);
            // Modified end
         }
            

         if( !Str.isEmpty(group_by) )
            qry.setGroupByClause(group_by);

         qry.includeMeta("ALL");
         mgr.querySubmit(buf,blk);
             
         no_of_hits = blk.getASPRowSet().countDbRows();
         if( no_of_hits==0 )
            msg_txt = mgr.translateJavaText("FNDLOVNODATA: No data found.");
      }
   }

   private void countFind(boolean command_invoke)
   {
      ASPManager mgr = getASPManager();      
      
      bar.setLayoutMode(bar.FIND_LAYOUT);
      ASPTransactionBuffer buf = getASPManager().newASPTransactionBuffer();
      ASPQuery qry = buf.addQuery(blk);

      qry.setSelectList("to_char(count(*)) N");
      if( !Str.isEmpty(where) )
         qry.addWhereCondition(where);
      
      // Added by Terry 20130813
      // Tree list lov
      if (!command_invoke && !Str.isEmpty(tree_pare_field))
         qry.addWhereCondition(tree_pare_field + " IS NULL");
      // Added end

      buf = getASPManager().perform(buf);

      lay.setCountValue(Integer.parseInt(buf.getValue(blk.getName()+"/DATA/N")));
      // Bug 40909, start
      no_of_hits = Integer.parseInt(buf.getValue(blk.getName()+"/DATA/N"));
      
      if (no_of_hits ==0) 
         no_of_hits = -1;
      // Bug 40909, end
   }

   private void cancelFind()
   {
      bar.setLayoutMode(bar.getHistoryMode());
      if(bar.getASPRowSet().countRows()==0)
         bar.getASPRowSet().clear();
      no_of_hits = -1;
   }


   // old functions - remove ?

   private void search( int skip_rows )
   {
      if(DEBUG) debug("ASPLov.search()");
/*
      ASPTransactionBuffer buf = getASPManager().newASPTransactionBuffer();
      ASPQuery qry = buf.addQuery(blk);

      if( !Str.isEmpty(where) )
         qry.addWhereCondition(where);
      qry.setOrderByClause(tbl.getKeyColumnName());
      qry.includeMeta("ALL");
      int buffer_size = 6*tbl.getPageSize();
      qry.setBufferSize(buffer_size);
      qry.skipRows(skip_rows);

      getASPManager().submit(buf);

      if( blk.getASPRowSet().countRows()==0 )
         getASPManager().setStatusLine(getASPManager().translateJavaText("FNDLOVNODATA: No data found."));
*/
   }

   private void count()
   {
      if(DEBUG) debug("ASPLov.count()");
      
      ASPManager mgr = getASPManager();
      String n = "";
      
      ASPTransactionBuffer buf = getASPManager().newASPTransactionBuffer();
      ASPQuery qry = buf.addQuery(blk);

      qry.setSelectList("to_char(count(*)) N");
      if( !Str.isEmpty(where) )
         qry.addWhereCondition(where);

      mgr.submit(buf);

      n = blk.getASPRowSet().getRow().getValue("N");
      
      getASPManager().setStatusLine(getASPManager().translateJavaText("FNDLOVCNTRES: Query will retrieve &1 rows",n));
      blk.getASPRowSet().clear();
   }

   private void clear()
   {
      if(DEBUG) debug("ASPLov.clear()");

      blk.getASPRowSet().clear();
      tbl.clearDisplayedQueryRow();
   }

   //=============================================================================
   //  Command Bar Browse Group functions
   //=============================================================================

   private void next()
   {
      if(DEBUG) debug("ASPLov.next()");

      if( !goTo(db_pos+tbl.getPageSize()) )
         getASPManager().setStatusLine(getASPManager().translateJavaText("FNDLOVLASTPG: It is the last page."));
   }

   private void previous()
   {
      if(DEBUG) debug("ASPLov.previous()");

      if( !goTo(db_pos-tbl.getPageSize()) )
         getASPManager().setStatusLine(getASPManager().translateJavaText("FNDLOVFIRSTPG: It is the first page."));
   }

   private void first()
   {
      if(DEBUG) debug("ASPLov.first()");
      if (lay.isMultirowLayout())
         getASPManager().getASPPage().getASPBlock().getASPRowSet().firstDbSet(); 
      else
         goTo(0);
   }

   private void last()
   {
      if(DEBUG) debug("ASPLov.last()");
      if (lay.isMultirowLayout())
         getASPManager().getASPPage().getASPBlock().getASPRowSet().lastDbSet(); 
      else
         goTo(db_rows-1);
   }

   private boolean goTo( int new_db_pos )
   {
      if(DEBUG) debug("ASPLov.goTo("+new_db_pos+")");

      int page_size = tbl.getPageSize();
      if(DEBUG) debug("  page_size="+page_size);
      int max_db_pos = db_rows - page_size;
      if(DEBUG) debug("  max_db_pos="+max_db_pos);
      if( new_db_pos > max_db_pos ) new_db_pos = max_db_pos;
      if( new_db_pos < 0 ) new_db_pos = 0;
      if(DEBUG) debug("  new_db_pos="+new_db_pos);

      if(DEBUG) debug("  skipped_rows="+skipped_rows);
      int new_buf_pos = new_db_pos - skipped_rows;
      if(DEBUG) debug("  new_buf_pos="+new_buf_pos);
      int buf_rows     = blk.getASPRowSet().countRows();
      if(DEBUG) debug("  buf_rows="+buf_rows);
      if( new_buf_pos >= 0 && new_buf_pos+page_size <= buf_rows )
      {
         blk.getASPRowSet().goTo(new_buf_pos);
      }
      else
      {
         int buffer_left = 2*page_size;
         new_buf_pos = buffer_left;
         if( new_buf_pos > new_db_pos ) new_buf_pos = new_db_pos;
         if(DEBUG) debug("  new_db_pos="+new_db_pos+", new_buf_pos="+new_buf_pos);
         search(new_db_pos - new_buf_pos);
         blk.getASPRowSet().goTo(new_buf_pos);
      }
      if(DEBUG) debug("  new_db_pos="+new_db_pos+", db_pos="+db_pos);

      return new_db_pos != db_pos;
   }

   /**
    * Updates the column titles if the COLUMN_TITLES LOVProperty 
    * has been set by using ASPField.setLOVProperty().
    *   @see ifs.fnd.asp.ASPField#setLOVProperty()
    */
   private void updateColumnTitles(String column_titles)
   {      
      if(column_titles==null) return;
      
      StringTokenizer stk = new StringTokenizer(column_titles, "^");      
      HashMap lov_titles = new HashMap();
      ASPField[] fields = blk.getFields();
      
      while(stk.hasMoreTokens())
      {
         String pair = stk.nextToken();
         StringTokenizer stk2 = new StringTokenizer( pair, "=" );
         String id=null, value=null;
         if( stk2.hasMoreTokens() )
            id = stk2.nextToken().trim();
         if( stk2.hasMoreTokens() )
            value = stk2.nextToken().trim();
         if(id!=null && value!=null)
            lov_titles.put( id, value );        
      }     
      
      for( int i=0; i<fields.length; i++ )
      {
         ASPField f = fields[i];
         String col = f.getName();         
         if( lov_titles.containsKey(col) && lov_titles.get(col)!=null )
            f.setLabel( lov_titles.get(col).toString() );
      }
   }
   
   //=============================================================================
   //  HTML
   //=============================================================================
   private void generateHTML() throws FndException
   {
      if(DEBUG) debug("ASPLov.generateHTML()");

      ASPManager mgr  = getASPManager();
      ASPPage    page = getASPPage();
      ASPContext ctx  = mgr.getASPContext();
      ASPConfig  cfg  = page.getASPConfig();
      //ASPForm    frm  = page.getASPForm();
      String[]   html = cfg.getLOVHTML();

      String column_titles = this.getASPManager().getQueryStringValue("__COLUMN_TITLES");      
      if ( column_titles == null )
         column_titles = ctx.readValue("COLUMN_TITLES");      
      ctx.writeValue( "COLUMN_TITLES", column_titles );
      
      if ( column_titles != null )
         updateColumnTitles( column_titles );

      // Added by Terry 20130816
      // Tree lov
      boolean show_tree = showTree();
      // Added end
      
      out.clear();
      out.append( html[0] );    // <html><head>
      out.append( mgr.generateHeadTag("FNDCFFWINTIT: IFS/Applications - List of values") );
      //out.append( mgr.generateHTMLHead("FNDCFFWINTIT: IFS/Applications - List of values") );
      //out.append( html[1] );
      out.append( "</head>\n");
      
      // Added by Terry 20130814
      // Tree list lov
      if (show_tree)
      {
         out.append("<body topmargin=0 leftmargin=0 marginheight=0 marginwidth=0 ");
         out.append(" onLoad =\"javascript:initJSNavigator();\"");
      }
      // Added end
      else
      {
         out.append( "<body " );
         out.append( mgr.generateBodyTag() );
      }
      
      out.append( html[2] );    //  >\n<form
      // Added by Terry 20130814
      // Tree list lov
      if (show_tree)
         out.append(" TARGET='_self' ");
      // Added end
      out.append( mgr.generateFormTag() );
      out.append( ">\n" );
      
      // Added by Terry 20130814
      // Tree list lov
      if (show_tree)
         out.append(lov_tree.generateTreeHeader_());
      // Added end
      
      //out.append( html[3] );
      // Modified by Terry 20130816
      // Original:
      // out.append( mgr.startPresentation( Str.isEmpty(title) ? mgr.translate("FNDLOVHEADTIT: List of &1",tbl.getTitle()) : title ), "\n" );
      if (show_tree)
      {
         String lov_title = Str.isEmpty(title) ? mgr.translate("FNDLOVHEADTIT: List of &1", tbl.getTitle()) : title;
         out.append("\n");
         out.append("<table ");
         if ( cfg.getParameter("AUDIT/SHOW_BORDER","N").equals("Y") )
            out.append("border=\"1\"");
         else
            out.append("border=\"0\"");
         out.append(" cellpadding=\"0\" cellspacing=\"0\"");
         out.append(" width=\"100%\">\n");
         out.append("<tr> <td "+(!mgr.isRTL()?"align=\"left\"":"")+" valign=\"middle\" height=\"20\"><font class=lovTitle>&nbsp;&nbsp;", mgr.translate(lov_title),"</font></td>\n");
         out.append("<td align=right>\n");
         out.append("<font class=lovTitle>" + mgr.translate("FNDLOVTREESELECTEDVALUE: Selected value:") + "</font>");
         out.append("<input class='readOnlyTextField' type=text size=20 name=" + TREE_SELE_VALUE + " value=\"" + (mgr.isEmpty(tree_selected_value) ? "" : tree_selected_value) + "\" readonly tabindex=-1 style=\"text-align=right\"");
         out.append("<font class=lovTitle>&nbsp;&nbsp;</font></td>\n");
         out.append("</tr>\n");
         out.append("</table>\n");
      }
      else
         out.append( mgr.startPresentation( Str.isEmpty(title) ? mgr.translate("FNDLOVHEADTIT: List of &1",tbl.getTitle()) : title ), "\n" );
      // Modified end

      ASPCommandBar bar = blk.getASPCommandBar();
      bar.disableCommand(ASPCommandBar.NEWROW);
      bar.disableMinimize();

      out.append( bar.showBar(), "\n" );
      //out.append( lay.generateDataPresentation(), "\n" );
      // Modified by Terry 20121010
      // Original:
      // if ((("true".equals(ctx.readValue("MULTICHOICE"))) && (lay.isMultirowLayout()) && (blk.getASPRowSet().countRows() != 1) && (blk.getASPRowSet().countRows() != 0)))
      if (!show_tree && ("true".equals(ctx.readValue("MULTICHOICE"))) && (lay.isMultirowLayout()) && (blk.getASPRowSet().countRows() != 0))
      {
         out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr><td>&nbsp;&nbsp;</td><td width=\"100%\">\n" );  
         out.append( "<SPAN class=normalTextValue>" );
         out.append( "<UL><Li>");
         out.append(mgr.translate("FNDLOVSELECTPICKONEVALUE: Use the arrow to pick up a single value. "));
         out.append( "</Li><Li>");
         out.append(mgr.translate("FNDLOVSELECTPICKMULTIVALUE: Select check boxes and press 'OK' to choose multiple values. "));
         out.append( "</Li></UL>");
         out.append( "</SPAN>" );
         out.append( "</td><td>&nbsp;&nbsp;</td></table>\n" );   
      }
      // Modified end
      
      // Added by Terry 20130814
      // Tree list lov
      if (show_tree)
      {
         out.append(lov_tree.generateTreeBody_());
         out.append(lov_tree.generateTreeScripts());
         out.append(lov_tree.generateHiddenFields());
      }
      // Added end
      else if( lay.getLayoutMode() == lay.MULTIROW_LAYOUT)
         out.append( tbl.populateLov() );
//         out.append( tbl.populate() );
      else
         out.append( lay.generateDialog() );

      // 
      /*out.append("<input type=hidden name=TREE_KEY");
      out.append(" value=\"", tree_key,"\">");
      out.append("<input type=hidden name=TREE_VIEW");
      out.append(" value=\"", tree_view,"\">");*/
      // 
      
      // Added by Terry 20130816
      // Tree lov
      out.append("<script>\n");
      out.append("function selectKeyNode(key_value)\n");
      out.append("{\n");
      out.append("   window.document.getElementById('" + TREE_SELE_VALUE + "').value = key_value;\n");
      out.append("}\n");
      out.append("</script>\n");
      // Added end
      
      out.append("<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr>");
      out.append("<td>&nbsp;&nbsp;</td>");
      out.append("<td colspan=1 align=left><SPAN class=boldTextValue>", msg_txt,"</SPAN></td>");
      out.append("<td>&nbsp;&nbsp;</td></tr></table>");
//      out.append( tbl.populateLov() );
      out.append( mgr.endPresentation(), "\n" );
      out.append( "</form>\n</body>\n</html>\n" );

      // Added by Terry 20120822
      // Set LOV first selected by value.
      out.append("<script language=javascript>\n");
      out.append("boxs = document.getElementsByName('__SELECTED1');\n");
      out.append("var myValue = '"+ mgr.readValue("myValue") +"';\n");
      out.append("var valuesArray = new Array();\n");
      out.append("valuesArray = myValue.split(';');\n");
      out.append("for(var i=0;i<valuesArray.length;i++){\n");
      out.append("\tfor(var j=0;j<boxs.length;j++){\n");
      out.append("\t\tif(boxs[j].value == valuesArray[i]){\n");
      out.append("\t\t\tboxs[j].checked = true;\n");
      out.append("\t\t}\n");
      out.append("\t}\n");
      out.append("}\n");
      out.append("</script>\n");
      // Added end
      
      // Added by Terry 20120929
      // Lov bar in multi-select mode
      if (!mgr.isEmpty(client_function))
      {
         out.append(client_function);
         client_function = "";
      }
      // Added end
      
      mgr.responseWrite( out.toString() );
   }
   
   // Added by Terry 20120929
   // Save user selection in buffer at multi-select mode.
   private void okLov()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      AutoString cf_out = new AutoString();
      String selected_values = "";
      
      if (isTreeListLov())
      {
         cf_out.append("<script language=javascript>\n");
         cf_out.append("var selected_value = window.document.getElementById('" + TREE_SELE_VALUE + "').value;\n");
         cf_out.append("setValue(selected_value);\n");
         cf_out.append("</script>\n");
         client_function = cf_out.toString();
      }
      else if ((("true".equals(ctx.readValue("MULTICHOICE"))) && (lay.isMultirowLayout()) && (blk.getASPRowSet().countRows() != 0)))
      {
         client_function = "";
         
         ASPBuffer selections = (ASPBuffer)ctx.findGlobalObject(page_name);
         String key = tbl.getKey();
         
         if (selections != null && selections.countItems() > 0 && !mgr.isEmpty(key))
         {
            for (int i = 0; i < selections.countItems(); i++)
            {
               if (mgr.isEmpty(selected_values))
                  selected_values = selections.getBufferAt(i).getValue(key);
               else
                  selected_values = selected_values + ";" + selections.getBufferAt(i).getValue(key);
            }
         }
         
         cf_out.append("<script language=javascript>\n");
         cf_out.append("function getAllSelectedValues(i)\n");
         cf_out.append("{\n");
         cf_out.append("   str = \"" + selected_values + "\";\n");
         cf_out.append("   setValue(str);\n");
         cf_out.append("}\n");
         cf_out.append("getAllSelectedValues(0);\n");
         cf_out.append("</script>\n");
         
         clearSelectionToCtx();
         client_function = cf_out.toString();
      }
   }
   
   private void adjustLov()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPRowSet set = blk.getASPRowSet();
      String key_values = "";
      
      ASPBuffer selections = (ASPBuffer)ctx.findGlobalObject(page_name);
      String keys = getGlobalKeys();
      
      int setCount = set.countRows();
      
      if (selections != null && selections.countItems() != 0 && setCount > 0 && !mgr.isEmpty(keys))
      {
         Vector v_keys = getKeys(keys);
         
         set.first();
         for(int i = 0; i < setCount; i++)
         {
            key_values = "";
            for (int j = 0; j < v_keys.size(); j ++)
            {
               String key_value = set.getRow().getValue((String)v_keys.get(j));
               key_values = key_values + key_value + "^";
            }
               
            if (selections.itemExists(key_values))
            {
               set.selectRow();
            }
            set.next();
         }
      }
   }
   
   private String getKeys()
   {
      ASPManager mgr = getASPManager();
      String view = blk.getView();

      ASPCommand defcmd = (new ASPCommand(mgr)).construct();
      defcmd.defineCustom("REFERENCE_SYS.Get_Lov_Properties");
      defcmd.addParameter("VIEW", "S", "IN", view);
      defcmd.addParameter("KEYS","S", "OUT", null);
      defcmd.addParameter("COLS","S", "OUT", null);
      defcmd.addParameter("PROMPTS","S", "OUT", null);
      defcmd.addParameter("TYPES","S", "OUT", null);

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      trans.addCommand("LOVPROPS", defcmd);
      trans = mgr.perform(trans);

      return trans.getValue("LOVPROPS/DATA/KEYS");
   }
   
   private Vector getKeys(String sKeys)
   {
      StringTokenizer st_keys = new StringTokenizer(sKeys, ",");
      Vector keys = new Vector();
      while(st_keys.hasMoreTokens())
      {
         String key_field = st_keys.nextToken();
         keys.add(key_field);
      }
      return keys;
   }
   
   private String getGlobalKeys()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      String keys = (String) ctx.findGlobalObject(blk.getView());
      if (mgr.isEmpty(keys))
      {
         keys = getKeys();
         String tbl_key = tbl.getKey();
         if (keys.indexOf(tbl_key) == -1)
         {
            // Key of ASPTable is not part of view's key, then it is not DynamicLov.
            keys = tbl_key;
         }
         ctx.setGlobalObject(blk.getView(), keys);
      }
      return keys;
   }
   
   private boolean checkViewOrderby()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      String view_order_by = (String) ctx.findGlobalObject(blk.getView() + "_ORDER_BY");
      if (mgr.isEmpty(view_order_by))
      {
         String view = blk.getView();

         ASPCommand defcmd = (new ASPCommand(mgr)).construct();
         defcmd.defineCustom("Database_SYS.Check_View_Orderby");
         defcmd.addParameter("VIEW",    "S", "IN",  view);
         defcmd.addParameter("ORDER_BY","S", "OUT", null);

         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         trans.addCommand("VIEW_ORDER_BY", defcmd);
         trans = mgr.perform(trans);

         view_order_by = Str.isEmpty(trans.getValue("VIEW_ORDER_BY/DATA/ORDER_BY")) ? "FALSE" : trans.getValue("VIEW_ORDER_BY/DATA/ORDER_BY");
         ctx.setGlobalObject(blk.getView() + "_ORDER_BY", view_order_by);
      }
      if ("TRUE".equals(view_order_by))
         return true;
      
      return false;
   }
   
   private void storeSelectionToCtx()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPRowSet set = blk.getASPRowSet();
      String key_values = "";
      try
      {
         if (lay.isSingleLayout() || lay.isMultirowLayout())
            set.store();
      }
      catch(Exception e)
      {
         
      }
      
      String keys = getGlobalKeys();
      
      int contRows = set.countRows();
      
      if (contRows > 0 && !mgr.isEmpty(keys))
      {
         ASPBuffer selections = (ASPBuffer)ctx.findGlobalObject(page_name);
         if (selections == null)
         {
            selections = mgr.newASPBuffer();
         }
         
         Vector v_keys = getKeys(keys);
         
         set.first();
         for(int i = 0; i < contRows; i++)
         {
            key_values = "";
            for (int j = 0; j < v_keys.size(); j ++)
            {
               String key_value = set.getRow().getValue((String)v_keys.get(j));
               key_values = key_values + key_value + "^";
            }
            
            if (set.isRowSelected())
            {
               if (!selections.itemExists(key_values))
               {
                  ASPBuffer selection = mgr.newASPBuffer();
                  for (int j = 0; j < v_keys.size(); j ++)
                  {
                     String key_value = set.getRow().getValue((String)v_keys.get(j));
                     selection.addItem((String)v_keys.get(j), key_value);
                  }
                  selections.addBuffer(key_values, selection);
               }
            }
            else
            {
               if (selections.itemExists(key_values))
               {
                  selections.removeItem(key_values);
               }
            }
            set.next();
         }
         ctx.setGlobalObject(page_name, selections);
      }
   }
   
   private void clearSelectionToCtx()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      ASPBuffer selections = (ASPBuffer)ctx.findGlobalObject(page_name);
      
      if (selections != null)
      {
         selections.clear();
      }
      ctx.setGlobalObject(page_name, selections);
   }
   // Added end
   
   // Added by Terry 20130813
   // Tree list lov
   private void searchTree()
   {
      ASPRowSet set = blk.getASPRowSet();
      try 
      {
         if(set.countRows() == 0)
         {
            createTreeRoot();
            set.clear();
         }
         else
            createTreeFirst();
      }
      catch (Exception e) 
      {
         createTreeRoot();
         set.clear();
      }
   }
   
   // Create lov tree root node
   private void createTreeRoot() 
   {
      ASPManager mgr = getASPManager();
      
      String root_name = mgr.translate(Str.isEmpty(title) ? mgr.translate("FNDLOVHEADTIT: List of &1",tbl.getTitle()) : title );
      String target = ""; //getTargetScript("");
      
      lov_tree = new TreeList(mgr);
      
      lov_tree.setLabel(root_name);
      lov_tree.setImage(img_loc.concat("Object_Root.gif"));
      lov_tree.setTarget(target);
      lov_tree.setTreePosition(6, 50);
      lov_tree.setTreeAreaWidth(500);
   }
   
   // Get display fields in tree
   private ArrayList<String> getDisplayFields()
   {
      if (!Str.isEmpty(tree_disp_field))
      {
         ArrayList<String> display_fields = new ArrayList<String>();
         StringTokenizer st = new StringTokenizer(tree_disp_field, ",");
         while(st.hasMoreTokens())
            display_fields.add(st.nextToken());
         return display_fields;
      }
      return null;
   }
   
   // Get type for tree node
   private ArrayList<String> getTypeValues()
   {
      if (!Str.isEmpty(tree_type_field))
      {
         ArrayList<String> type_values = new ArrayList<String>();
         StringTokenizer st = new StringTokenizer(tree_type_field, ",");
         while(st.hasMoreTokens())
            type_values.add(st.nextToken());
         return type_values;
      }
      return null;
   }
   
   // Get target script for tree node
   private String getTargetScript(String key_value)
   {
      return "javascript:selectKeyNode(\\\"" + key_value + "\\\")' target='_self";
   }
   
   // Create first level tree
   private void createTreeFirst() 
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      createTreeRoot();
      
      ASPRowSet set = blk.getASPRowSet();
      
      int size = set.countRows();
      set.first();
      // Get display fields
      ArrayList<String> display_fields = getDisplayFields();
      // Get type and values setting
      ArrayList<String> type_values = getTypeValues();
      
      String qs = mgr.getQueryString();
      StringTokenizer params = new StringTokenizer(qs, "&");
      String field = "";
      String __parent_params = "";
      String __parent_keys = "";
      
      while(params.hasMoreTokens())
      {
         field = params.nextToken();
         if(!field.startsWith("__"))
         {
            String __qs_field_name = field.substring(0,field.indexOf("="));
            String __qs_field_value = getASPManager().URLDecode(field.substring(field.indexOf("=")+1,field.length()));
            if (!Str.isEmpty(__qs_field_name) && !__qs_field_name.equals(tbl.getKey()) && !"MULTICHOICE".equals(__qs_field_name) && !"LOV".equals(__qs_field_name) && !"WIDTH".equals(__qs_field_name) && !"HEIGHT".equals(__qs_field_name) && mgr.hasASPField(__qs_field_name) && !Str.isEmpty(__qs_field_value))
            {
               __parent_params = __parent_params + "&" + __qs_field_name + "=" + mgr.URLEncode(__qs_field_value);
               __parent_keys = __parent_keys + __qs_field_name + "^";
            }
         }
      }
      
      if (Str.isEmpty(__parent_keys) && Str.isEmpty(__parent_params))
      {
         String view_param_fields = ctx.readValue("__VIEW_PARAM_FIELDS");
         if (!Str.isEmpty(view_param_fields))
         {
            StringTokenizer param_fields = new StringTokenizer(view_param_fields, "^");
            while(param_fields.hasMoreTokens())
            {
               field = param_fields.nextToken();
               if (!Str.isEmpty(field))
               {
                  String value = ctx.readValue(field);
                  if (!Str.isEmpty(value))
                  {
                     __parent_params = __parent_params + "&" + field + "=" + mgr.URLEncode(value);
                     __parent_keys = __parent_keys + field + "^";
                  }
               }
            }
         }
      }
            
      for (int i = 0; i < size; i++) 
      {
         String node_key = tbl.getKey();
         String node_key_value = set.getValue(node_key);
         String display_string = "";
         if (display_fields != null)
         {
            for (int j = 0; j < display_fields.size(); j++)
            {
               String field_value = Str.isEmpty(set.getValue(display_fields.get(j))) ? "" : set.getValue(display_fields.get(j));
               display_string = display_string + field_value + " ";
            }
            display_string = display_string.trim();
         }
         else
            display_string = node_key_value;
         
         String expand_data;
         if (!Str.isEmpty(__parent_keys) && !Str.isEmpty(__parent_params))
         {
            expand_data = "&__DYNAMIC_LOV_VIEW=" + tree_view +
                          "&TREE_KEY=" + mgr.URLEncode(tree_key) + 
                          "&" + node_key + "=" + mgr.URLEncode(node_key_value) +
                          "&TREE_PARENT_KEY=" + mgr.URLEncode(__parent_keys) +
                          __parent_params +
                          "&" + TREE_PARE_FIELD + "=" + mgr.URLEncode(tree_pare_field);
         }
         else
         {
            expand_data = "&__DYNAMIC_LOV_VIEW=" + tree_view +
                          "&TREE_KEY=" + mgr.URLEncode(tree_key) + 
                          "&" + node_key + "=" + mgr.URLEncode(node_key_value) +
                          "&" + TREE_PARE_FIELD + "=" + mgr.URLEncode(tree_pare_field);
         }

         if (!Str.isEmpty(where))
            expand_data = expand_data + "&" + WHERE + "=" + mgr.URLEncode(where);
         
         if (!Str.isEmpty(order_by))
            expand_data = expand_data + "&" + ORDER_BY + "=" + mgr.URLEncode(order_by);
         
         if (!Str.isEmpty(tree_disp_field))
            expand_data = expand_data + "&" + TREE_DISP_FIELD + "=" + mgr.URLEncode(tree_disp_field);
         
         if (!Str.isEmpty(tree_type_field))
            expand_data = expand_data + "&" + TREE_TYPE_FIELD + "=" + mgr.URLEncode(tree_type_field);
         
         if (!Str.isEmpty(tree_onlsel_type))
            expand_data = expand_data + "&" + TREE_ONLSEL_TYPE + "=" + mgr.URLEncode(tree_onlsel_type);
         
         // Convert quotes (otherwise the value can't be placed in the link)
         node_key_value = Str.replace(Str.replace(node_key_value,"\"","<DQ>"),"'","<SQ>");
         node_key_value = Str.replace(node_key_value,"\\","\\\\"); //replace \ with \\
         
         String target = "";
         if (!Str.isEmpty(tree_onlsel_type))
         {
            if (type_values != null && type_values.size() == 3 && tree_onlsel_type.equals(set.getValue(type_values.get(0))))
               target = getTargetScript(node_key_value);
         }
         else
            target = getTargetScript(node_key_value);
         
         // Check type setting
         // 0: Type field name, 1: Folder type value, 2: Item type value
         if (type_values != null && type_values.size() == 3 && !Str.isEmpty(type_values.get(2)) && type_values.get(2).equals(set.getValue(type_values.get(0))))
         {
            // Items
            createTreeItem(lov_tree, display_string, target, node_key_value);
         }
         else
         {
            // Folders
            createTreeNode(lov_tree, display_string, target, expand_data, node_key_value);
         }
         set.next();
      }
   }
   
   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      if("EXPAND_TREE".equals(val))
      {
         TreeList temp_node = new TreeList(mgr, "DUMMY");
         tree_key = mgr.readValue("TREE_KEY");
         String node_key = mgr.readValue(tree_key);
         where = mgr.readValue(WHERE);
         order_by = mgr.readValue(ORDER_BY);
         tree_disp_field = mgr.readValue(TREE_DISP_FIELD);
         tree_pare_field = mgr.readValue(TREE_PARE_FIELD);
         tree_type_field = mgr.readValue(TREE_TYPE_FIELD);
         tree_onlsel_type = mgr.readValue(TREE_ONLSEL_TYPE);
         
         buildSubNode(temp_node, node_key);
         String dynamicData = temp_node.getDynamicNodeString();
         
         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
      }
      mgr.endResponse();
   }
   
   private void buildSubNode(TreeList parent_tree, String parent_node_no)
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPQuery qry = trans.addEmptyQuery(blk);
      
      if( !Str.isEmpty(where) )
         qry.addWhereCondition(where);

      // Added by Terry 20130813
      // Tree list lov
      // qry.addWhereCondition(tree_pare_field + " = '" + parent_node_no + "'");
      qry.addWhereCondition(tree_pare_field + " = ?");
      qry.addParameter(tree_pare_field, "S", "IN", parent_node_no);
      // Added end
      
      String __parent_keys = mgr.readValue("TREE_PARENT_KEY");
      String __parent_params = "";
      if (!Str.isEmpty(__parent_keys))
      {
         StringTokenizer st = new StringTokenizer(__parent_keys, "^");
         while(st.hasMoreTokens())
         {
            String parent_key_name = st.nextToken();
            if (!Str.isEmpty(parent_key_name))
            {
               String parent_key_value = mgr.readValue(parent_key_name);
               if (!Str.isEmpty(parent_key_value))
               {
                  __parent_params = __parent_params + "&" + parent_key_name + "=" + mgr.URLEncode(parent_key_value);
                  // qry.addWhereCondition(parent_key_name + " = '" + parent_key_value + "'");
                  qry.addWhereCondition(parent_key_name + " = ?");
                  qry.addParameter(parent_key_name, "S", "IN", parent_key_value);
               }
            }
         }
      }
      
      if( !Str.isEmpty(order_by) )
         qry.setOrderByClause(order_by);
      
      qry.includeMeta("ALL");
      mgr.querySubmit(trans, blk);
      
      ASPRowSet set = blk.getASPRowSet();
      
      if (set.countRows() == 0) 
      {
         set.clear();
      }
      else
      {
         int countRows = set.countRows();
         set.first();
         
         // Get display fields
         ArrayList<String> display_fields = getDisplayFields();
         // Get type and values setting
         ArrayList<String> type_values = getTypeValues();
         
         for(int i = 0; i < countRows; i++)
         {
            String node_key = tree_key;
            String node_key_value = set.getValue(node_key);
            String display_string = "";
            String display_type   = "";
            if (display_fields != null)
            {
               for (int j = 0; j < display_fields.size(); j++)
               {
                  String field_value = Str.isEmpty(set.getValue(display_fields.get(j))) ? "" : set.getValue(display_fields.get(j));
                  display_string = display_string + field_value + " ";
               }
               display_string = display_string.trim();
            }
            else
               display_string = node_key_value;
            
            String expand_data;
            if (!Str.isEmpty(__parent_keys) && !Str.isEmpty(__parent_params))
            {
               expand_data = "&__DYNAMIC_LOV_VIEW=" + tree_view +
                             "&TREE_KEY=" + mgr.URLEncode(tree_key) + 
                             "&" + node_key + "=" + mgr.URLEncode(node_key_value) +
                             "&TREE_PARENT_KEY=" + mgr.URLEncode(__parent_keys) +
                             __parent_params +
                             "&" + TREE_PARE_FIELD + "=" + mgr.URLEncode(tree_pare_field);
            }
            else
            {
               expand_data= "&__DYNAMIC_LOV_VIEW=" + tree_view +
                            "&TREE_KEY=" + mgr.URLEncode(tree_key) + 
                            "&" + node_key + "=" + mgr.URLEncode(node_key_value) +
                            "&" + TREE_PARE_FIELD + "=" + mgr.URLEncode(tree_pare_field);
            }
            
            if (!Str.isEmpty(where))
               expand_data = expand_data + "&" + WHERE + "=" + mgr.URLEncode(where);
            
            if (!Str.isEmpty(order_by))
               expand_data = expand_data + "&" + ORDER_BY + "=" + mgr.URLEncode(order_by);
            
            if (!Str.isEmpty(tree_disp_field))
               expand_data = expand_data + "&" + TREE_DISP_FIELD + "=" + mgr.URLEncode(tree_disp_field);
            
            if (!Str.isEmpty(tree_type_field))
               expand_data = expand_data + "&" + TREE_TYPE_FIELD + "=" + mgr.URLEncode(tree_type_field);
            
            if (!Str.isEmpty(tree_onlsel_type))
               expand_data = expand_data + "&" + TREE_ONLSEL_TYPE + "=" + mgr.URLEncode(tree_onlsel_type);
            
            // Convert quotes (otherwise the value can't be placed in the link)
            node_key_value = Str.replace(Str.replace(node_key_value,"\"","<DQ>"),"'","<SQ>");
            node_key_value = Str.replace(node_key_value,"\\","\\\\"); //replace \ with \\
            
            String target = "";
            if (!Str.isEmpty(tree_onlsel_type))
            {
               if (type_values != null && type_values.size() == 3 && tree_onlsel_type.equals(set.getValue(type_values.get(0))))
                  target = getTargetScript(node_key_value);
            }
            else
               target = getTargetScript(node_key_value);
            
            // Check type setting
            // 0: Type field name, 1: Folder type value, 2: Item type value
            if (type_values != null && type_values.size() == 3 && !Str.isEmpty(type_values.get(2)) && type_values.get(2).equals(set.getValue(type_values.get(0))))
            {
               // Items
               createTreeItem(parent_tree, display_string, target, node_key_value);
            }
            else
            {
               // Folders
               createTreeNode(parent_tree, display_string, target, expand_data, node_key_value);
            }
            set.next();
         }
      }
   }
   
   // Create tree node
   private void createTreeNode(TreeList parent_tree, String display_string, String target, String expand_data, String node_key_value)
   {
      // Folders
      TreeListNode node = parent_tree.addNode(display_string);
      node.setTarget(target);
      node.setExpandData(expand_data);
      node.setImage(img_loc.concat("Object_Sub_Project.gif"));
      if (!Str.isEmpty(target))
         node.setJsTag("ondblclick=javascript:setValue('" + node_key_value + "')");
   }
   
   // Create tree item
   private void createTreeItem(TreeList parent_tree, String display_string, String target, String node_key_value)
   {
      // Items
      TreeListItem item = parent_tree.addItem(display_string);
      item.setTarget(target);
      item.setImage(img_loc.concat("Object_Activity.gif"));
      if (!Str.isEmpty(target))
         item.setJsTag("ondblclick=javascript:setValue('" + node_key_value + "')");
   }
   
   // Return true, if the lov is tree list, otherwise false
   public boolean isTreeListLov()
   {
      return (!Str.isEmpty(tree_disp_field) && !Str.isEmpty(tree_pare_field));
   }
   // Added end
}
