package ifs.hzwflw.portlets;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPage;
import ifs.fnd.asp.ASPPoolElement;
import ifs.fnd.asp.ASPPortal;
import ifs.fnd.asp.ASPPortletProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.HzWfUtil;

public class HzwflwTodoCenter extends ASPPortletProvider {

   //==========================================================================
   //  Static constants 
   //==========================================================================

   private final static int size = 15;
   // MDAHSE, 2001-08-16
   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.portlets.HzwflwTodoCenter");

   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================

   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;
   private ASPBuffer data;  //Bug Id 56231.


   //==========================================================================
   //  Mutable attributes
   //==========================================================================


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient int      skip_rows;
   private transient int      db_rows;

   //==========================================================================
   //  Construction
   //==========================================================================
   public HzwflwTodoCenter(ASPPortal portal, String clspath) {
      super(portal, clspath);
   }
   
   public ASPPage construct() throws FndException
   {
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doReset() throws FndException
   {
      super.doReset();
      //   no reset of variables since they should be "remembered".
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      HzwflwTodoCenter page = (HzwflwTodoCenter)(super.clone(mgr));

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());

      return page;
   }


   protected void preDefine() throws FndException
   {
      ctx = getASPContext();
      
      blk = newASPBlock("MAIN");
      
      blk.addField("TASKID")
      .setSize(10)
      .setLabel("TODOWORKBENCHTASKID: Task Id")
      .setHidden();
      blk.addField("USERTASKID").setHidden();
      blk.addField("TITLE")
      .setSize(35)
      .setLabel("TODOWORKBENCHTITLE: Title")
      .setHyperlink(HzConstants.TODOUrl, "ToDo_Workid,TODO_TRACKID,USERTASKID,TODO_REALUSERID","NEWWIN");
      blk.addField("SENDUSERNAME")
      .setSize(10)
      .setLabel("TODOWORKBENCHSENDUSERNAME: Send User Name");
      blk.addField("SENDUSERID")
      .setSize(10)
      .setLabel("TODOWORKBENCHSENDUSERID: Send User Id").setHidden();
      blk.addField("SENDTIME","Date")
      .setSize(20)
      .setLabel("TODOWORKBENCHSENDTIME: Send Time");
      blk.addField("TODO_FLOWSTATUS")
      .setDbName("FLOWSTATUS")
      .setSize(30)
      .setLabel("TODOWORKBENCHFLOWSTATUS: Flow Status")
      .setFontProperty("ÍË»Ø", "red")
      .setFontProperty("Returned", "red");
      blk.addField("URL")
      .setSize(50)
      .setLabel("TODOWORKBENCHURL: Url")
      .setHidden();
      blk.addField("USERID")
      .setSize(50)
      .setLabel("TODOWORKBENCHUSERID: User Id")
      .setHidden()
      .setHidden();
      blk.addField("TODOTYPE")
      .setSize(50)
      .setLabel("TODOWORKBENCHTODOTYPE: Todo Type")
      .setHidden()
      .setHidden();
      blk.addField("MODELNAME")
      .setSize(10)
      .setLabel("TODOWORKBENCHMODELNAME: Model Name");
      
      blk.addField("TOREADNODENAME")
      .setDbName("NODENAME")
      .setSize(10)
      .setLabel("TOREADWORKBENCHNODENAME: Node Name");
      
      blk.addField("IMPORTANCE")
      .setSize(50)
      .setLabel("TODOWORKBENCHIMPORTANCE: Importance")
      .setHidden();
      blk.addField("TIMELIMIT")
      .setSize(50) 
      .setLabel("TODOWORKBENCHTIMELIMIT: Time Limit")
      .setHidden();
      blk.addField("SENDUSERDEPTNAME")
      .setSize(50)
      .setLabel("TODOWORKBENCHSENDUSERDEPTNAME: Send Department")
      .setHidden();
      blk.addField("ToDo_Workid")
      .setDbName("DATAID")
      .setSize(50)
      .setLabel("TODOWORKBENCHDATAID: Data Id")
      .setHidden();
      blk.addField("TODO_REALUSERID")
      .setDbName("REALUSERID")
      .setSize(50)
      .setLabel("TODOWORKBENCHREALUSERID: Real User Id")
      .setHidden();
      blk.addField("STATUS")
      .setSize(50)
      .setLabel("TODOWORKBENCHSTATUS: Status")
      .setHidden();
      blk.addField("DONETIME")
      .setSize(50)
      .setLabel("TODOWORKBENCHDONETIME: Done Time")
      .setHidden();
      blk.addField("TODO_TRACKID")
      .setDbName("TRACKID")
      .setSize(30)
      .setHidden();
      blk.setView("HZ_WF_TASK_VIEW");
     tbl = newASPTable( blk );
     tbl.disableQueryRow();
     tbl.disableQuickEdit();
//   tbl.disableRowCounter();
     tbl.disableRowSelect();
     tbl.disableRowStatus();
     tbl.unsetSortable();

     rowset = blk.getASPRowSet();
     getASPManager().newASPCommandBar(blk);

      appendJavaScript( "function firstCust(obj,id)",
            "{",
            "   getPortletField(id,'CMD').value = 'FIRST';",
            "}\n");
      
      appendJavaScript( "function prevCust(obj,id)",
            "{",
            "   getPortletField(id,'CMD').value = 'PREV';",
            "}\n");
      
      appendJavaScript( "function lastCust(obj,id)",
            "{",
            "   getPortletField(id,'CMD').value = 'LAST';",
            "}\n");
      
      appendJavaScript( "function nextCust(obj,id)",
            "{",
            "   getPortletField(id,'CMD').value = 'NEXT';",
            "}\n");
      
      init();
   }


   protected void init()
   {
      blk    = getASPBlock("MAIN");
      rowset = blk.getASPRowSet();
      tbl    = getASPTable(blk.getName());
      ASPContext ctx = getASPContext();

      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
      String cmd = readValue("CMD");

      if ("FIRST".equals(cmd))
      {
         skip_rows = 0;
      }
      else if ("PREV".equals(cmd))
      {
         if (skip_rows>=size)
         {
            skip_rows -= size;
         }
         else
         {
            skip_rows = 0;
         }
      }
      else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
      {
         skip_rows += size;
      }
      else if ("LAST".equals(cmd))
      {
         skip_rows = ((db_rows + size - 1)/size - 1) * size;
      }
      else
      {
         skip_rows = 0;
      }

      ctx.writeValue("SKIP_ROWS", skip_rows + "");
   }


   protected void run()
   {
      String commd = readValue("CMD");

      if (Str.isEmpty(commd) || "NEXT".equals(commd) || "PREV".equals(commd) || "FIRST".equals(commd) || "LAST".equals(commd))
      {
         ASPManager           mgr    = getASPManager();
         ASPTransactionBuffer trans  = mgr.newASPTransactionBuffer();
         ASPQuery             qry    = trans.addQuery(blk);
         ASPCommand           cmd    = mgr.newASPCommand();
         
//         qry.addWhereCondition(" upper(REALUSERID) like '%"+getASPManager().getUserId().toUpperCase()+"'");
         qry.addWhereCondition("  HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) = '"+getASPManager().getUserId().toUpperCase()+"'");
         qry.addWhereCondition(" STATUS='1'");
         qry.setOrderByClause(" SENDTIME DESC");
         qry.includeMeta("ALL");
         qry.setBufferSize(size);
         
         if (Str.isEmpty(commd))
            qry.skipRows(0);
         else
            qry.skipRows(skip_rows);
         
         submit(trans);
         db_rows = blk.getASPRowSet().countDbRows();
         getASPContext().writeValue("DB_ROWS", db_rows + "" );
         // getASPContext().writeValue("SKIP_ROWS", skip_rows + "");
      }
   }

   public String getTitle( int mode )
   {
      String name = "";
      
      if (Str.isEmpty(name))
      {
         name = translate(getDescription());
      }
      
      if (mode == ASPPortal.MINIMIZED)
      {
         return name + translate("HZWFLWTODOCENTERTITLE: - You have ") + db_rows + " " + translate("HZWFLWTODOCENTERTODOCENTERTODOMESSAGE: Todo Messages");
      }
      else
         return name; 
   }

   public void printContents() throws FndException
   {
      printHiddenField("CMD","");
      int     nRows;
      nRows = rowset.countRows();
      
      if (nRows > 0)
      {
         printTable(tbl);
         
         if (size < db_rows)
         {
            printNewLine();

            if (skip_rows>0)
               printSubmitLink("HZWFLWTODOCENTERFIRST: First", "firstCust");
            else
               printText("HZWFLWTODOCENTERFIRST: First");

            printSpaces(5);
            
            if (skip_rows > 0)
               printSubmitLink("HZWFLWTODOCENTERPRES: Previous", "prevCust");
            else
               printText("HZWFLWTODOCENTERPRES: Previous");

            printSpaces(5);
            String rows = translate("HZWFLWTODOCENTERROWS: (Row &1 to &2 total &3)",
                                    (skip_rows+1)+"",
                                    (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                    db_rows+"");
            printText( rows );
            printSpaces(5);

            if (skip_rows<db_rows-size)
               printSubmitLink("HZWFLWTODOCENTERNEXT: Next", "nextCust");
            else
               printText("HZWFLWTODOCENTERNEXT: Next");
            
            printSpaces(5);
            
            if (skip_rows<db_rows-size)
               printSubmitLink("HZWFLWTODOCENTERLAST: Last", "lastCust");
            else
               printText("HZWFLWTODOCENTERLAST: Last");

            printNewLine();
         }
      }
      else
      {
         printNewLine();
         printText("HZWFLWTODOCENTERNODATA: No data found.");
         printNewLine();
      }
      printNewLine();
   }

   public static String getDescription()
   {
      return "HZWFLWTODOCENTERDESC: Todo Center";
   }
   
   public boolean canCustomize()
   {
      return false;
   }

}
