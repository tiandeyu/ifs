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
*  File        : DynamicSel.java
*  Modified    :
*    ASP2JAVA Tool  2000-12-22  - Created Using the ASP file DynamicSel.asp
*    Suneth M       2001-08-31  - Added mgr.setPageExpiring()
* ----------------------------------------------------------------------------
* New Comments:
* 2008/06/26 mapelk Bug 74852, Programming Model for Activities.
* 
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.2  2005/02/11 09:12:11  mapelk
* Remove ClientUtil applet and it's usage from the framework
*
* Revision 1.1  2005/01/28 18:07:26  marese
* Initial checkin
*
* Revision 1.2  2004/11/25 05:58:02  chdelk
* Added support for Activity APIs based LOVs.
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.*;


public class DynamicSel extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.DynamicSel");

   private static final String  VIEW_PARAMS        = "__VIEW_PARAMS";
   private static final String  WHERE              = "__WHERE";
   private static final String  GROUP_BY           = "__GROUP_BY";
   private static final String  ORDER_BY           = "__ORDER_BY";
   private static final String  AUTO               = "__AUTO_SEARCH";
   private static final String  TITLE              = "__TITLE";
   private static final String  FORMAT_MASK        = "__FORMAT_MASK";
   private static final String  PARENT_PARAMS      = "__PARENT_PARAMS";
   private static final String  FIELD_PARAMS       = "__FIELD_PARAMS";
   private static final String  TARGET_PKG         = "__TARGET_PKG";
   private static final String  TARGET_CUSTOM_FUNC = "__TARGET_CUSTOM_FUNC";
   private static final String  TARGET_FIELDS      = "__TARGET_FIELDS";
   // For Additional Where String
   private static final String  ADD_WHERE_SRE_FIES = "__ADD_WHERE_SRE_FIES";
   private static final String  ADD_WHERE_SRE_OPER = "__ADD_WHERE_SRE_OPER";
   private static final String  ADD_WHERE_TAR_VIEW = "__ADD_WHERE_TAR_VIEW";
   private static final String  ADD_WHERE_TAR_FIES = "__ADD_WHERE_TAR_FIES";
   private static final String  ADD_WHERE_TAR_KEYS = "__ADD_WHERE_TAR_KEYS";
   private static final String  ADD_WHERE_CLAUSE   = "__ADD_WHERE_CLAUSE";
   private int max_rows = 0;

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPTable       tbl;
   private ASPBlock       blk;
   private ASPCommandBar  bar;
   private ASPBlockLayout lay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String        name;
   private String        group_by;
   private String        order_by;
   private String        view_params;
   private String        where;
   private String        title;
   private String        format_mask;
   private String        field_params;
   private String        parent_params;
   private String        target_pkg;
   private String        target_custom_func;
   private String        target_fields;
   private String        add_where_sre_fies;
   private String        add_where_sre_oper;
   private String        add_where_tar_view;
   private String        add_where_tar_fies;
   private String        add_where_tar_keys;
   private String        add_where_clause;
   private int           no_of_hits;
   
   private String page_name = getClass().getName().toUpperCase();
   
   private boolean success;
   private String  show_msg;

   //===============================================================
   // Construction
   //===============================================================
   
   public DynamicSel(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      name   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DynamicSel page = (DynamicSel)(super.clone(obj));

      page.name   = null;

      page.blk = page.getASPBlock(blk.getName());
      page.tbl = page.getASPTable(tbl.getName());

      return page;
   }
   
   public void run() throws FndException 
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      success = false;
      show_msg = "";
      
      String auto = "";
      /*String is_multichoice = mgr.readValue("__MULTICHOICE");
      if (Str.isEmpty(is_multichoice))
         is_multichoice = mgr.readValue("MULTICHOICE");
      if (Str.isEmpty(is_multichoice))
         is_multichoice = ctx.readValue("MULTICHOICE");
      
      if (!"true".equals(is_multichoice))
         tbl.enableOneRowSelect();*/
      view_params   = ctx.readValue(VIEW_PARAMS, mgr.URLDecode(mgr.readValue(VIEW_PARAMS)));
      where         = ctx.readValue(WHERE, mgr.readValue(WHERE));
      group_by      = ctx.readValue(GROUP_BY, mgr.readValue(GROUP_BY));
      order_by      = ctx.readValue(ORDER_BY, mgr.readValue(ORDER_BY));
      format_mask   = ctx.readValue(FORMAT_MASK, mgr.readValue(FORMAT_MASK));
      field_params  = ctx.readValue(FIELD_PARAMS, mgr.readValue(FIELD_PARAMS));
      parent_params = ctx.readValue(PARENT_PARAMS, mgr.URLDecode(mgr.readValue(PARENT_PARAMS)));
      target_pkg    = ctx.readValue(TARGET_PKG, mgr.readValue(TARGET_PKG));
      target_custom_func = ctx.readValue(TARGET_CUSTOM_FUNC, mgr.readValue(TARGET_CUSTOM_FUNC));
      target_fields = ctx.readValue(TARGET_FIELDS, mgr.readValue(TARGET_FIELDS));
      add_where_sre_fies = ctx.readValue(ADD_WHERE_SRE_FIES, mgr.readValue(ADD_WHERE_SRE_FIES));
      add_where_sre_oper = ctx.readValue(ADD_WHERE_SRE_OPER, mgr.readValue(ADD_WHERE_SRE_OPER));
      add_where_tar_view = ctx.readValue(ADD_WHERE_TAR_VIEW, mgr.readValue(ADD_WHERE_TAR_VIEW));
      add_where_tar_fies = ctx.readValue(ADD_WHERE_TAR_FIES, mgr.readValue(ADD_WHERE_TAR_FIES));
      add_where_tar_keys = ctx.readValue(ADD_WHERE_TAR_KEYS, mgr.readValue(ADD_WHERE_TAR_KEYS));
      add_where_clause   = ctx.readValue(ADD_WHERE_CLAUSE);
      
      if (Str.isEmpty(add_where_clause) && !Str.isEmpty(add_where_tar_view))
         add_where_clause = getAdditionalWhereString();
      
      title         = ctx.readValue(TITLE, mgr.translate(mgr.readValue(TITLE)));
      
      if (mgr.commandBarActivated())
      {
         ASPRowSet set = blk.getASPRowSet();
         storeSelectionToCtx();
         eval(mgr.commandBarFunction());
      }
      else
      {
         auto = mgr.readValue(AUTO);
         
         if( Str.isEmpty(auto) || "Y".equals(auto) )
            okFind();
         else
            lay.setLayoutMode(lay.FIND_LAYOUT);
      }
      saveLayout();
      
      ASPRowSet set = blk.getASPRowSet();
      
      // ctx.writeValue("MULTICHOICE", is_multichoice);
      
      if( !Str.isEmpty(view_params) )
         ctx.writeValue(VIEW_PARAMS, view_params);
      if( !Str.isEmpty(where) && !where.equals("null"))
         ctx.writeValue(WHERE, where);
      if( !Str.isEmpty(group_by) )
         ctx.writeValue(GROUP_BY, group_by);
      if( !Str.isEmpty(order_by) )
         ctx.writeValue(ORDER_BY, order_by);
      if( !Str.isEmpty(format_mask) )
         ctx.writeValue(FORMAT_MASK, format_mask);
      if( !Str.isEmpty(field_params) )
         ctx.writeValue(FIELD_PARAMS, field_params);
      if( !Str.isEmpty(parent_params) )
         ctx.writeValue(PARENT_PARAMS, parent_params);
      if( !Str.isEmpty(target_pkg) )
         ctx.writeValue(TARGET_PKG, target_pkg);
      if( !Str.isEmpty(target_custom_func) )
         ctx.writeValue(TARGET_CUSTOM_FUNC, target_custom_func);
      if( !Str.isEmpty(target_fields) )
         ctx.writeValue(TARGET_FIELDS, target_fields);
      if( !Str.isEmpty(add_where_sre_fies) )
         ctx.writeValue(ADD_WHERE_SRE_FIES, add_where_sre_fies);
      if( !Str.isEmpty(add_where_sre_oper) )
         ctx.writeValue(ADD_WHERE_SRE_OPER, add_where_sre_oper);
      if( !Str.isEmpty(add_where_tar_view) )
         ctx.writeValue(ADD_WHERE_TAR_VIEW, add_where_tar_view);
      if( !Str.isEmpty(add_where_tar_fies) )
         ctx.writeValue(ADD_WHERE_TAR_FIES, add_where_tar_fies);
      if( !Str.isEmpty(add_where_tar_keys) )
         ctx.writeValue(ADD_WHERE_TAR_KEYS, add_where_tar_keys);
      if( !Str.isEmpty(add_where_clause) )
         ctx.writeValue(ADD_WHERE_CLAUSE, add_where_clause);
      if( !Str.isEmpty(title) )
         ctx.writeValue(TITLE, title);
      
      adjust();
   }
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      if( !mgr.isEmpty(mgr.readValue("__INIT")) && max_rows != 0 )
      {
         ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();
         ASPQuery qry = buf.addQuery(blk);

         qry.setSelectList("to_char(count(*)) N");
         
         if( !Str.isEmpty(view_params) )
         {
            String view_params_string = Str.replace(Str.replace(view_params, "@", "='"), "~", "' AND ");
            view_params_string = view_params_string.substring(0, view_params_string.length() - 5); // remove last AND
            qry.addWhereCondition(view_params_string);
         }
            
         
         if( !Str.isEmpty(where) )
            qry.addWhereCondition(where);
         
         if (!Str.isEmpty(add_where_clause))
            qry.addWhereCondition(add_where_clause);
         
         buf = mgr.perform(buf);

         no_of_hits = Integer.parseInt(buf.getValue(blk.getName()+"/DATA/N"));
      }
      
      if( (max_rows == 0) || (mgr.isEmpty(mgr.readValue("__INIT")) || no_of_hits <= max_rows) )
      {
         lay.setLayoutMode(lay.getHistoryMode());
         ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();
         ASPQuery qry = buf.addQuery(blk);

         if( !Str.isEmpty(view_params) )
         {
            String view_params_string = Str.replace(Str.replace(view_params, "@", "='"), "~", "' AND ");
            view_params_string = view_params_string.substring(0, view_params_string.length() - 5); // remove last AND
            qry.addWhereCondition(view_params_string);
         }
         
         if( !Str.isEmpty(where) )
            qry.addWhereCondition(where);

         if (!Str.isEmpty(add_where_clause))
            qry.addWhereCondition(add_where_clause);
         
         if( !Str.isEmpty(order_by) )
            qry.setOrderByClause(order_by);
         else
         {
            String keys = getGlobalKeys();
            qry.setOrderByClause(keys);
         }

         if( !Str.isEmpty(group_by) )
            qry.setGroupByClause(group_by);

         qry.includeMeta("ALL");
         mgr.querySubmit(buf,blk);
             
         no_of_hits = blk.getASPRowSet().countDbRows();
         if( no_of_hits==0 )
            mgr.showAlert(mgr.translate("FNDSELNODATA: No data found."));
      }
      
      clearSelectionToCtx();
   }

   public void countFind()
   {
      ASPManager mgr = getASPManager();      
      
      lay.setLayoutMode(lay.FIND_LAYOUT);
      ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();
      ASPQuery qry = buf.addQuery(blk);

      qry.setSelectList("to_char(count(*)) N");
      
      if( !Str.isEmpty(view_params) )
      {
         String view_params_string = Str.replace(Str.replace(view_params, "@", "='"), "~", "' AND ");
         view_params_string = view_params_string.substring(0, view_params_string.length() - 5); // remove last AND
         qry.addWhereCondition(view_params_string);
      }
      
      if( !Str.isEmpty(where) )
         qry.addWhereCondition(where);
      
      if (!Str.isEmpty(add_where_clause))
         qry.addWhereCondition(add_where_clause);
      
      buf = mgr.perform(buf);

      lay.setCountValue(Integer.parseInt(buf.getValue(blk.getName()+"/DATA/N")));
      no_of_hits = Integer.parseInt(buf.getValue(blk.getName()+"/DATA/N"));
      
      if (no_of_hits == 0) 
         no_of_hits = -1;
      
      clearSelectionToCtx();
   }
   
   public void okLov()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String prepare_attr_ = "";
      String attr_ = "";
      String source_field_value = "";
      String[] __source_fields;
      String[] __target_fields;
      
      String __msg = Str.isEmpty(title) ? tbl.getTitle() : title;
      
      if (Str.isEmpty(field_params))
      {
         show_msg = mgr.translate("FNDWEBDYNAMICSELNOSOURCEFIELDS: There is no source field parameters.");
         return;
      }
      else
         __source_fields = field_params.split(",");
      
      if (Str.isEmpty(target_fields))
      {
         show_msg = mgr.translate("FNDWEBDYNAMICSELNOTARFIELDS: There is no target field parameters.");
         return;
      }
      else
         __target_fields = target_fields.split(",");
      
      if (__source_fields == null || __target_fields == null || __source_fields.length != __target_fields.length)
      {
         show_msg = mgr.translate("FNDWEBDYNAMICSELNOMATCH: Field parameters does not match.");
         return;
      }
      
      if (Str.isEmpty(target_pkg))
      {
         show_msg = mgr.translate("FNDWEBDYNAMICSELNOTARPKG: There is no target package parameters.");
         return;
      }
      
      ASPBuffer selections = (ASPBuffer)ctx.findGlobalObject(page_name);
      
      if (selections != null && selections.countItems() > 0)
      {
         if (Str.isEmpty(target_custom_func))
         {
            // New record with standard function.
            prepare_attr_ = Str.isEmpty(parent_params) ? "" : Str.replace(Str.replace(parent_params, "@", String.valueOf(IfsNames.fieldSeparator)), "~", String.valueOf(IfsNames.recordSeparator));
            // Invoke Prepare_Insert___
            ASPCommand cmd = trans.addCustomCommand("PREPARENEW", target_pkg + ".New__");
            cmd.addParameter("INFO",       "S", "OUT",    null);
            cmd.addParameter("OBJID",      "S", "OUT",    null);
            cmd.addParameter("OBJVERSION", "S", "OUT",    null);
            cmd.addParameter("ATTR",       "S", "IN_OUT", prepare_attr_);
            cmd.addParameter("ACTION",     "S", "IN",     "PREPARE");
            trans = mgr.perform(trans);
            
            prepare_attr_ = prepare_attr_ + (Str.isEmpty(trans.getValue("PREPARENEW/DATA/ATTR")) ? "" : trans.getValue("PREPARENEW/DATA/ATTR"));
            
            trans.clear();
            for (int i = 0; i < selections.countItems(); i++)
            {
               ASPBuffer one_row = selections.getBufferAt(i);
               attr_ = prepare_attr_;
               
               // Pack parameters for New__
               for (int j = 0; j < __target_fields.length; j ++)
               {
                  if (!Str.isEmpty(__target_fields[j]) && !Str.isEmpty(__source_fields[j]))
                  {
                     __source_fields[j] = __source_fields[j].trim();
                     __target_fields[j] = __target_fields[j].trim().toUpperCase();
                     
                     if (__source_fields[j].indexOf("'") != -1)
                        // Constant value
                        source_field_value = __source_fields[j].substring(1, __source_fields[j].length() - 1);
                     else
                     {
                        // Field value
                        __source_fields[j] = __source_fields[j].toUpperCase();
                        source_field_value = Str.isEmpty(one_row.getValue(__source_fields[j])) ? "" : one_row.getValue(__source_fields[j]);
                     }
                     attr_ = attr_ + __target_fields[j] + IfsNames.fieldSeparator + source_field_value + IfsNames.recordSeparator;
                  }
               }
               
               // Call New__ in target package
               if (!Str.isEmpty(attr_))
               {
                  cmd = trans.addCustomCommand("NEW" + i, target_pkg + ".New__");
                  cmd.addParameter("INFO",       "S", "OUT",    null);
                  cmd.addParameter("OBJID",      "S", "OUT",    null);
                  cmd.addParameter("OBJVERSION", "S", "OUT",    null);
                  cmd.addParameter("ATTR",       "S", "IN_OUT", attr_);
                  cmd.addParameter("ACTION",     "S", "IN",     "DO");
               }
            }
         }
         else
         {
            // New record with user custom function.
            ASPBuffer parent_params_buff = stringParamsToBuffer(parent_params, "@", "~");
            trans.clear();
            ASPCommand cmd;
            for (int i = 0; i < selections.countItems(); i++)
            {
               ASPBuffer one_row = selections.getBufferAt(i);
               cmd = trans.addCustomCommand("NEW" + i, target_pkg + "." + target_custom_func);
               if (parent_params_buff != null)
                  for (int k = 0; k < parent_params_buff.countItems(); k++)
                     cmd.addParameter("PARENT_PARAM" + k, "S", "IN", parent_params_buff.getValueAt(k));
               
               for (int j = 0; j < __target_fields.length; j ++)
               {
                  if (!Str.isEmpty(__target_fields[j]) && !Str.isEmpty(__source_fields[j]))
                  {
                     __source_fields[j] = __source_fields[j].trim();
                     __target_fields[j] = __target_fields[j].trim().toUpperCase();
                     
                     if (__source_fields[j].indexOf("'") != -1)
                        // Constant value
                        source_field_value = __source_fields[j].substring(1, __source_fields[j].length() - 1);
                     else
                     {
                        // Field value
                        __source_fields[j] = __source_fields[j].toUpperCase();
                        source_field_value = Str.isEmpty(one_row.getValue(__source_fields[j])) ? "" : one_row.getValue(__source_fields[j]);
                     }
                     cmd.addParameter("FIELD_PARAM" + j, "S", "IN", source_field_value);
                  }
               }
            }
         }
         
         try
         {
            mgr.perform(trans);
            show_msg = mgr.translate("FNDWEBDYNAMICSELSUCC: Create &1 rows successfully.", __msg);
            success = true;
            clearSelectionToCtx();
         }
         catch (Exception e)
         {
//            ASPLog log = mgr.getASPLog();
//            String msg = log.
//            String msg = e.getLocalizedMessage();
//            FndException fex = (FndException)e;
//            msg = Util.firstLine(fex.getOraErrorMessage());
            show_msg = mgr.translate("FNDWEBDYNAMICSELNEWERROR: Create &1 rows failed.", __msg);
         }
      }
      else
      {
         show_msg = mgr.translate("FNDWEBDYNAMICSELNOSEL: You have to select any rows.");
      }
   }
   
   private ASPBuffer stringParamsToBuffer(String params, String field_sep, String record_sep)
   {
      ASPManager mgr = getASPManager();
      if (!Str.isEmpty(params))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         StringTokenizer st = new StringTokenizer(params, record_sep);
         while(st.hasMoreTokens())
         {
            String field = st.nextToken();
            StringTokenizer st1 = new StringTokenizer(field, field_sep);
            String name=null, value=null;
            if( st1.hasMoreTokens() )
               name = st1.nextToken().trim();
            if( st1.hasMoreTokens() )
               value = st1.nextToken().trim();
            
            if (!Str.isEmpty(name) && !Str.isEmpty(value))
               buff.addItem(name, value);
         }
         return buff;
      }
      return null;
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
   

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      mgr.setPageExpiring();

      disableValidation();
      blk = mgr.newASPBlock("SELECTOR");
      blk.addField("DUMMY").setFunction("NULL").setHidden();

      tbl = mgr.newASPTable(blk);
      
      tbl.enableRowSelect();
      tbl.disableEditProperties();
      tbl.disableOutputChannels();
      tbl.disableRowCounter();
      tbl.disableQuickEdit();
      bar = blk.newASPCommandBar();
      bar.disableCommand(bar.PROPERTIES);
      bar.disableCommand(bar.NEWROW);
      bar.disableMinimize();
      bar.setCounterDbMode();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

      String force_key = mgr.readValue("__FORCE_KEY");
      if (!mgr.isEmpty(force_key))
         generateDefinition(mgr.readValue("__DYNAMIC_LOV_VIEW"), force_key, null, null);
      else
         generateDefinition(mgr.readValue("__DYNAMIC_LOV_VIEW"));
      
      name = Str.isEmpty(mgr.readValue("__FIELD")) ? getViewDesc(mgr.readValue("__DYNAMIC_LOV_VIEW")) : mgr.readValue("__FIELD");
      
      tbl.setTitle(name);
   }
   
   private String getViewDesc(String view)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd  = (new ASPCommand(mgr)).construct();
      cmd.defineCustomFunction("Dictionary_SYS.Get_Logical_Unit");
      cmd.addParameter("LU_NAME", "S", null, null);
      cmd.addParameter("VIEW",    "S", "IN", view);
      cmd.addParameter("TYPE",    "S", "IN", "VIEW");

      trans.addCommand("GETLUNAME", cmd);
      
      if(isUndefined())
         trans = mgr.performConfig(trans);
      else
         trans = mgr.perform(trans);
      
      String lu_name = trans.getValue("GETLUNAME/DATA/LU_NAME");
      
      trans.clear();
      
      cmd  = (new ASPCommand(mgr)).construct();
      cmd.defineCustomFunction("Language_SYS.Translate_Lu_Prompt_");
      cmd.addParameter("LU_PROMPT", "S", null, null);
      cmd.addParameter("LU_NAME",   "S", "IN", lu_name);
      cmd.addParameter("LANG_CODE", "S", "IN", mgr.getLanguageCode());
      
      trans.addCommand("GETLUPROMPT", cmd);
      
      if(isUndefined())
         trans = mgr.performConfig(trans);
      else
         trans = mgr.perform(trans);
      
      return Str.isEmpty(trans.getValue("GETLUPROMPT/DATA/LU_PROMPT")) ? "" : trans.getValue("GETLUPROMPT/DATA/LU_PROMPT");
   }
   
   private String getAdditionalWhereString()
   {
      if (Str.isEmpty(add_where_tar_view))
         return "";
      String source_fields = Str.isEmpty(add_where_sre_fies) ? field_params : add_where_sre_fies;
      if (Str.isEmpty(source_fields))
         return "";
      String target_fields = Str.isEmpty(add_where_tar_fies) ? this.target_fields : add_where_tar_fies;
      String target_keys   = Str.isEmpty(add_where_tar_keys) ? parent_params : add_where_tar_keys;
      String operator = Str.isEmpty(add_where_sre_oper) ? "NOT IN" : add_where_sre_oper;
      
      String add_where_clause = "(";
      
      // 1. Scan source fields, exclude the constant parameters
      String[] __source_fields = source_fields.split(",");
      for (int i = 0; i < __source_fields.length; i++)
      {
         String field = __source_fields[i];
         if (!Str.isEmpty(field) && field.indexOf("'") == -1)
         {
            add_where_clause = add_where_clause + field.trim().toUpperCase() + ",";
         }
      }
      add_where_clause = add_where_clause.substring(0, add_where_clause.length() - 1) + ")" + " " + operator + " (SELECT ";

      // 2. Adding target fields
      if (this.target_fields.equals(target_fields))
      {
         String[] __target_fields = target_fields.split(",");
         for (int i = 0; i < __source_fields.length; i++)
         {
            String field = __source_fields[i];
            if (!Str.isEmpty(field) && field.indexOf("'") == -1)
            {
               add_where_clause = add_where_clause + __target_fields[i].trim().toUpperCase() + " " + field + ",";
            }
         }
         add_where_clause = add_where_clause.substring(0, add_where_clause.length() - 1);
      }
      else
         add_where_clause = add_where_clause + target_fields;
      
      // 3. Adding from view
      add_where_clause =  add_where_clause + " FROM " + add_where_tar_view;
      
      // 4. Adding where clause
      if (!Str.isEmpty(target_keys))
      {
         add_where_clause = add_where_clause + " WHERE ";
         if (target_keys.equals(parent_params))
         {
            add_where_clause = add_where_clause + Str.replace(Str.replace(target_keys, "@", "='"), "~", "' AND ");
         }
         else
         {
            StringTokenizer st_target_keys = new StringTokenizer(target_keys, ",");
            while (st_target_keys.hasMoreTokens())
            {
               String key = st_target_keys.nextToken();
               if (!Str.isEmpty(key))
               {
                  key = key.trim().toUpperCase();
                  String value = getItemValue(key, parent_params, "@", "~");
                  if (!Str.isEmpty(value))
                     add_where_clause = add_where_clause + key + "='" + value + "' AND ";
               }
            }
         }
         add_where_clause = add_where_clause.substring(0, add_where_clause.length() - " AND ".length());
      }
      
      add_where_clause = add_where_clause + ")";
      return add_where_clause;
   }
   
   private String getItemValue(String name, String attr, String field_separator, String record_separator)
   {
      int from_, len_, to_;
      String attr_ = record_separator + attr;
      len_ = name.length();
      from_ = attr_.indexOf(record_separator + name + field_separator);
      if (from_ != -1)
      {
         to_ = attr_.indexOf(record_separator, from_ + 1);
         if (to_ != -1)
            return attr_.substring(from_ + len_ + record_separator.length() + field_separator.length(), to_);
      }
      return "";
   }
   
   /**
    *
    * Creates ASPFields for the specified view.
    * Fields included and formats used on those fields are determined from the view comments.
    */
   public void generateDefinition( String view )
   {
      generateDefinition(view, null, null, null);
   }
   
   /**
    * Used for LOVs created using activity APIs.
    * @param view Name of the entity related to this LOV.
    * @param key Name of the key field.
    * @param handler Name of the activity handler.
    * @param operation Name of the activity operation.
    */
   public void generateDefinition( String view, String key, String handler, String operation)
   {
      ASPManager mgr = getASPManager();

      mgr.setPageExpiring();

      String keys    = "";
      String cols    = "";
      String prompts = "";
      String types   = "";
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand defcmd = trans.addCustomCommand("SELPROPS", "REFERENCE_SYS.Get_Lov_Properties");
      defcmd.addParameter("VIEW",    "S", "IN",  view);
      defcmd.addParameter("KEYS",    "S", "OUT", null);
      defcmd.addParameter("COLS",    "S", "OUT", null);
      defcmd.addParameter("PROMPTS", "S", "OUT", null);
      defcmd.addParameter("TYPES",   "S", "OUT", null);

      trans = mgr.performConfig(trans);

      // Modified by Terry 20120822
      if (mgr.isEmpty(key))
         keys = trans.getValue("SELPROPS/DATA/KEYS");
      else
         keys = key;
      // Modified end
      cols    = trans.getValue("SELPROPS/DATA/COLS");
      prompts = trans.getValue("SELPROPS/DATA/PROMPTS");
      types   = trans.getValue("SELPROPS/DATA/TYPES");

      if(DEBUG)
      {
         debug("view: " + view);
         debug("keys: " + keys);
         debug("cols: " + cols);
         debug("prompts: " + prompts);
         debug("types: " + types);
      }

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
   }

   void addRest(String view)
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand defcmd = trans.addCustomCommand("VIEWPROPS", "REFERENCE_SYS.Get_View_Properties");
      defcmd.addParameter("VIEW_NAME_",   "S", "IN",  view);
      defcmd.addParameter("COL_NAMES_",   "S", "OUT", null);
      defcmd.addParameter("COL_PROMPTS_", "S", "OUT", null);
      defcmd.addParameter("COL_TYPES_",   "S", "OUT", null);

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
   
   protected void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPRowSet set = blk.getASPRowSet();
      String key_values = "";
      
      ASPBuffer selections = (ASPBuffer)ctx.findGlobalObject(page_name);
      String keys = getGlobalKeys();
      
      int setCount = set.countRows();
      
      if (selections != null && selections.countItems() != 0 && setCount > 0 && !Str.isEmpty(keys))
      {
         Vector v_keys = getKeys(keys);
         
         for(int i = 0; i < setCount; i++)
         {
            key_values = "";
            for (int j = 0; j < v_keys.size(); j ++)
            {
               String key_value = set.getValueAt(i, (String)v_keys.get(j));
               key_values = key_values + key_value + "^";
            }
               
            if (selections.itemExists(key_values))
            {
               set.selectRow(i);
            }
         }
      }
   }
   
   private String getKeys()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String view = blk.getView();

      ASPCommand defcmd = trans.addCustomCommand("SELPROPS", "REFERENCE_SYS.Get_Lov_Properties");
      defcmd.addParameter("VIEW",    "S", "IN",  view);
      defcmd.addParameter("KEYS",    "S", "OUT", null);
      defcmd.addParameter("COLS",    "S", "OUT", null);
      defcmd.addParameter("PROMPTS", "S", "OUT", null);
      defcmd.addParameter("TYPES",   "S", "OUT", null);

      trans = mgr.perform(trans);

      return trans.getValue("SELPROPS/DATA/KEYS");
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
            // Key of ASPTable is not part of view's key, then it is not DynamicSel.
            keys = tbl_key;
         }
         ctx.setGlobalObject(blk.getView(), keys);
      }
      return keys;
   }
   
   private void storeSelectionToCtx() throws FndException
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
         
         for(int i = 0; i < contRows; i++)
         {
            key_values = "";
            for (int j = 0; j < v_keys.size(); j ++)
            {
               String key_value = set.getValueAt(i, (String)v_keys.get(j));
               if (!Str.isEmpty(key_value))
                  key_values = key_values + key_value + "^";
            }
            
            if (!Str.isEmpty(key_values))
            {
               if (set.isRowSelected(i))
               {
                  if (!selections.itemExists(key_values))
                     selections.addBuffer(key_values, set.getRow(i));
               }
               else
               {
                  if (selections.itemExists(key_values))
                     selections.removeItem(key_values);
               }
            }
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


   //===============================================================
   //  HTML
   //===============================================================
   
   protected String getDescription()
   {
      return "FNDDYNSELDESC: Values Selector";
   }

   protected String getTitle()
   {
      return "FNDDYNSELTITLE: Values Selector";
   }
   
   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx  = mgr.getASPContext();
      AutoString out = getOutputStream();
      
      String column_titles = mgr.getQueryStringValue("__COLUMN_TITLES");      
      if ( column_titles == null )
         column_titles = ctx.readValue("COLUMN_TITLES");      
      ctx.writeValue("COLUMN_TITLES", column_titles );
      
      if ( column_titles != null )
         updateColumnTitles( column_titles );
      
      out.clear();
      
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDDYNSELWINTIT: IFS/Applications - Values Selector"));
      out.append("</head>\n");
      out.append("<body " + mgr.generateBodyTag());
      out.append("><form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation( Str.isEmpty(title) ? mgr.translate("FNDSELHEADTIT: &1 Selector", tbl.getTitle()) : title ), "\n");
      out.append(lay.show());
      // out.append("<input type=button value='test' onclick='window.history.back();'>\n");
      
      
      if (!Str.isEmpty(show_msg))
      {
         appendDirtyJavaScript("ifsAlert(\"" + show_msg + "\");\n");
      }
      
      if (success)
      {
         appendDirtyJavaScript("Close();\n");
      }
      
      //
      // Client functions
      //
      appendDirtyJavaScript("function Close()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (opener.document.form.REFRESH_CHILD)\n");
      appendDirtyJavaScript("         opener.document.form.REFRESH_CHILD.value = '" + target_pkg.toUpperCase() + "';\n");
      appendDirtyJavaScript("      eval(\"opener.refreshParent()\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err){}\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err){}\n");
      appendDirtyJavaScript("}\n");
      
      // appendDirtyJavaScript("document.getElementById('__REQUEST_METHOD_TYPE').value = 'POST';\n");
      
      out.append(mgr.endPresentation(), "\n");
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
