package ifs.hzwflw.portlets;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPContext;
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

public class HzwflwToReadCenter extends ASPPortletProvider {

   //==========================================================================
   //  Static constants 
   //==========================================================================

   private final static int size = 10;
   // MDAHSE, 2001-08-16
   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.portlets.HzwflwToReadCenter");

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
   public HzwflwToReadCenter(ASPPortal portal, String clspath) {
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
      HzwflwToReadCenter page = (HzwflwToReadCenter)(super.clone(mgr));

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
      
      blk.addField("TOREAD_TASKID").setDbName("TASKID")
      .setSize(10)
      .setLabel("TODOWORKBENCHTASKID: Task Id")
      .setHidden();
      blk.addField("TOREAD_TITLE").setDbName("TITLE")
      .setSize(35)
      .setLabel("TODOWORKBENCHTITLE: Title")
      .setHyperlink(HzConstants.ToReadUrl, "ToRead_Workid,TOREAD_URL,TOREAD_TRACKID,TOREAD_TASKID,TOREAD_REALUSERID,TOREAD_TITLE","NEWWIN");
      blk.addField("SENDUSERNAME")
      .setSize(10)
      .setLabel("TODOWORKBENCHSENDUSERNAME: Send User Name");

      blk.addField("SENDUSERID")
      .setSize(10)
      .setLabel("TODOWORKBENCHSENDUSERID: Send User Id").setHidden();
      blk.addField("SENDTIME","Date")
      .setSize(10)
      .setLabel("TODOWORKBENCHSENDTIME: Send Time");
      blk.addField("TOREAD_URL")
      .setDbName("URL")
      .setSize(50)
      .setLabel("TODOWORKBENCHURL: Url")
      .setHidden();
      blk.addField("USERID")
      .setSize(50)
      .setLabel("TODOWORKBENCHUSERID: User Id")
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
      .setLabel("TODOWORKBENCHNODENAME: Node Name");
      
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
      blk.addField("ToRead_Workid")
      .setDbName("DATAID")
      .setSize(50)
      .setLabel("TODOWORKBENCHDATAID: Data Id")
      .setHidden();
      blk.addField("TOREAD_REALUSERID").setDbName("REALUSERID")
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
      blk.addField("TOREAD_TRACKID")
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
         qry.addWhereCondition("  HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='"+getASPManager().getUserId().toUpperCase()+"'");
         qry.addWhereCondition(" STATUS in ('2','6') ");
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
         return name + translate("HZWFLWREADCENTERTITLE: - You have ") + db_rows + " " + translate("HZWFLWREADCENTERTODOCENTERTOREADMESSAGE: ToRead Messages");
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
               printSubmitLink("HZWFLWREADCENTERFIRST: First", "firstCust");
            else
               printText("HZWFLWREADCENTERFIRST: First");

            printSpaces(5);
            
            if (skip_rows > 0)
               printSubmitLink("HZWFLWREADCENTERPRES: Previous", "prevCust");
            else
               printText("HZWFLWREADCENTERPRES: Previous");

            printSpaces(5);
            String rows = translate("HZWFLWREADCENTERROWS: (Row &1 to &2 total &3)",
                                    (skip_rows+1)+"",
                                    (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                    db_rows+"");
            printText( rows );
            printSpaces(5);

            if (skip_rows<db_rows-size)
               printSubmitLink("HZWFLWREADCENTERNEXT: Next", "nextCust");
            else
               printText("HZWFLWREADCENTERNEXT: Next");
            
            printSpaces(5);
            
            if (skip_rows<db_rows-size)
               printSubmitLink("HZWFLWREADCENTERLAST: Last", "lastCust");
            else
               printText("HZWFLWREADCENTERLAST: Last");

            printNewLine();
         }
      }
      else
      {
         printNewLine();
         printText("HZWFLWREADCENTERNODATA: No data found.");
         printNewLine();
      }
      printNewLine();
   }

   public static String getDescription()
   {
      return "HZWFLWREADCENTERDESC: ToRead Center";
   }
   
   public boolean canCustomize()
   {
      return false;
   }


}
