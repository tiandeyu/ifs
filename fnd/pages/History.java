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
*  File        : History.java
* Description :
* Notes       :
* ----------------------------------------------------------------------------
* Modified    :
*    Kingsly P  2001-May-30 - Created
*    Chandana   2004-Jun-28 - Made possible to call from RMB action "History".
*    Suneth M   2004-Aug-04 - Changed duplicate localization tags.
* ----------------------------------------------------------------------------
*
*  New Comments:
*  2006/06/30 buhilk Bug 58216, Fixed SQL Injection threats
*  2007/02/14 sumelk Bug 63530, Changed run() & countFind().
*  2007/11/29 sumelk Bug 69640, Changed run to create the cookie name with the user id instead of fnd user.
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.*;


public class History extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.History");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private int headrowno;
   
   private String lu_name;
   private String lu_keys;
   private boolean has_access;

   //===============================================================
   // Construction
   //===============================================================
   public History(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      q   = null;
      headrowno   = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      History page = (History)(super.clone(obj));

      page.trans   = null;


      page.q   = null;
      page.headrowno   = 0;

      page.blk = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.cmdbar = page.blk.getASPCommandBar();
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();
      page.itemblk = page.getASPBlock(itemblk.getName());
      page.itemset = page.itemblk.getASPRowSet();
      page.itembar = page.itemblk.getASPCommandBar();
      page.itemtbl = page.getASPTable(itemtbl.getName());
      page.itemlay = page.itemblk.getASPBlockLayout();

      return page;
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = getASPContext();
      trans = mgr.newASPTransactionBuffer();
      
      lu_name = ctx.readValue("LU_NAME");
      lu_keys = ctx.readValue("LU_KEYS");
      
      has_access = isObjectAccessible("FND/History.page");
      String cookie_name = "history_"+mgr.getUserId();
      String history_data = ctx.getCookie(cookie_name);
           
      if(!mgr.isEmpty(history_data))
      {
         ctx.removeCookie(cookie_name);
         String[] history_params = Str.split(history_data,(char)30+"");
         lu_name = history_params[0];
         lu_keys = history_params[1];

         if (lu_keys.startsWith("'") && lu_keys.endsWith("'"))
            lu_keys = lu_keys.substring(1,lu_keys.length()-1);     
      }
      
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(lu_name) && !mgr.isEmpty(lu_keys))
         okFind();
      
      if((mgr.isEmpty(lu_name) || mgr.isEmpty(lu_keys)) && !has_access)
         throw new FndException("FNDHISTORYVIEWERROR: You do not have sufficient privileges to access the history data.");   
      
      ctx.writeValue("LU_NAME", lu_name);
      ctx.writeValue("LU_KEYS", lu_keys);
      
      adjust();
   }


//=============================================================================
//  Command Bar functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
     
      mgr.createSearchURL(blk);
      trans.clear();
      q = trans.addQuery(blk);
                 
      if(!mgr.isEmpty(lu_name) && !mgr.isEmpty(lu_keys) || has_access)
      {
         if(!mgr.isEmpty(lu_name) && !mgr.isEmpty(lu_keys))
         {
            q.addWhereCondition("LU_NAME = ? AND KEYS IN (?)");
            q.addParameter("LU_NAME",lu_name);   
            q.addParameter("KEYS",lu_keys);
         }  
         q.setOrderByClause("LOG_ID");
         q.includeMeta("ALL");
         mgr.querySubmit(trans,blk);
         if (  rowset.countRows() == 0 )
         {
            mgr.showAlert("FNDPAGESHISTORYNODATA: No data found.");
            rowset.clear();
         }
         eval(rowset.syncItemSets());
      }        
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      q.setSelectList("to_char(count(*)) N");
      if(!mgr.isEmpty(lu_name) && !mgr.isEmpty(lu_keys))
      {
         q.addWhereCondition("LU_NAME = ? AND KEYS IN (?)");
         q.addParameter("LU_NAME",lu_name);   
         q.addParameter("KEYS",lu_keys);
      }  
      mgr.submit(trans);
      lay.setCountValue(toInt(rowset.getValue("N")));
      rowset.clear();
   }

// ITEM block functions

   public void  okFindITEM()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk);
      q.addWhereCondition("LOG_ID = ?");
      q.addParameter("LOG_ID", rowset.getValue("LOG_ID"));
      q.includeMeta("ALL");
      headrowno = rowset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk);
      rowset.goTo(headrowno);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      // Master block
      blk = mgr.newASPBlock("MAIN");

      blk.addField("LOG_ID").
          setLabel("FNDPAGESHISTORYLOG_ID: Log Id").
          setSize(10);

      blk.addField("TIME_STAMP","Datetime").
          setLabel("FNDPAGESHISTORYTIME_STAMP: Time").
          setSize(20);

      blk.addField("HISTORY_TYPE").
          setLabel("FNDPAGESHISTORYHISTORY_TYPE: Update Type").
          setSize(30);

      blk.addField("USERNAME").
          setLabel("FNDPAGESHISTORYUSERNAME: User").
          setSize(30);

      blk.addField("MODULE").
          setLabel("FNDPAGESHISTORYMODULE: Component").
          setSize(6);

      blk.addField("TABLE_NAME").
          setLabel("FNDPAGESHISTORYTABLE_NAME: Table Name").
          setSize(30);

      blk.addField("LU_NAME").
          setLabel("FNDPAGESHISTORYLU_NAME: Object Type").
          setSize(30);

      blk.addField("DESCRIPTION").
          setFunction("HISTORY_LOG_API.Get_Description(:LOG_ID)").
          setLabel("FNDPAGESHISTORYDESCRIPTION: Object").
          setSize(60);

      blk.addField("KEYS").
          setLabel("FNDPAGESHISTORYKEYS: Keys").
          setHidden().
          setSize(60);
      
      blk.addField("TRANSACTION_ID").
          setLabel("FNDPAGESHISTORYTRANSACTID: Transaction ID").
          setHidden().
          setSize(20);

      blk.setView("HISTORY_LOG");
      
      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableCommand(cmdbar.NEWROW);
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("FNDPAGESHISTORYMAIN: History Log");
      lay = blk.getASPBlockLayout();
      lay.defineGroup("","LOG_ID,TIME_STAMP,HISTORY_TYPE,USERNAME",false,true,4);
      lay.defineGroup((mgr.translate("FNDPAGESHISTORYOBJINFO: Object Information")),"MODULE,TABLE_NAME,LU_NAME,DESCRIPTION",true,true,2);

      lay.setDialogColumns(4);
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      
      // Item block
      itemblk = mgr.newASPBlock("ITEM");
      itemblk.addField("ITEM_LOG_ID").
              setDbName("LOG_ID").
              setHidden();
      itemblk.addField("COLUMN_NAME").
              setLabel("FNDPAGESHISTORYCOLUMN_NAME: Column Name").
              setSize(30);
      itemblk.addField("OLD_VALUE").
              setLabel("FNDPAGESHISTORYOLD_VALUE: Old Value").
              setSize(50);
      itemblk.addField("NEW_VALUE").
              setLabel("FNDPAGESHISTORYNEW_VALUE: New Value").
              setSize(50);

      itemblk.setView("HISTORY_LOG_ATTRIBUTE");
      itemblk.setMasterBlock(blk);
      itemset = itemblk.getASPRowSet();
      itembar = mgr.newASPCommandBar(itemblk);
      itembar.defineCommand(itembar.OKFIND, "okFindITEM");
      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setTitle("FNDPAGESHISTORYLOGDETAILS: History Log Details");
      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDialogColumns(3);
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      if(mgr.isEmpty(lu_name))
      {
         mgr.getASPField("KEYS").unsetHidden();
         mgr.getASPField("TRANSACTION_ID").unsetHidden();
      }
      lay.adjustLayout();      
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      ASPManager mgr = getASPManager();
      return mgr.translate("FNDPAGESHISTORYTITLE: History Log - &1",(mgr.isEmpty(lu_name)?mgr.translate("FNDPAGESHISTORYDETAILS: Details"):lu_name));
   }

   protected String getTitle()
   {
      return getDescription();
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if(lay.isVisible())
      {
         appendToHTML(lay.show());
      }
      if(itemlay.isVisible())
      {
         appendToHTML(itemlay.show());
      }
   }

}
