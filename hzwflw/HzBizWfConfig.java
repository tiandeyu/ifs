package ifs.hzwflw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class HzBizWfConfig extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzBizWfConfig");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HzBizWfConfig (ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PAGE_PATH")) )
         okFind();else if( !mgr.isEmpty(mgr.getQueryStringValue("PROCESS_KEY")) )
        	 okFind();
         else 
        	 okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("HZBIZWFCONFIGNODATA: No data found.");
         headset.clear();
      }
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }



   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","HZ_BIZ_WF_CONFIG_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PAGE_PATH").
              setMandatory().
              setInsertable().
              setLabel("HZBIZWFCONFIGPAGEPATH: Page Path").
              setSize(50);
      headblk.addField("PROCESS_KEY").
              setInsertable().
              setLabel("HZBIZWFCONFIGPROCESSKEY: Process Key").
              setDynamicLOV("HZ_WF_PROCESS_DEF").
              setSize(50);
      headblk.addField("PROCESS_NAME").
              setInsertable().
              setLabel("HZBIZWFCONFIGPROCESSNAME: Process Name").
              setSize(50);
      headblk.addField("REMARK").
              setInsertable().
              setLabel("HZBIZWFCONFIGREMARK: Remark").
              setSize(50);
      headblk.setView("HZ_BIZ_WF_CONFIG");
      headblk.defineCommand("HZ_BIZ_WF_CONFIG_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HZBIZWFCONFIGTBLHEAD: Hz Biz Wf Configs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(1);
   }


   public void adjust()
   {
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HZBIZWFCONFIGDESC: Hz Biz Wf Config";
   }


   protected String getTitle()
   {
      return "HZBIZWFCONFIGTITLE: Hz Biz Wf Config";
   }


   protected void printContents() throws FndException
   {
      super.printContents();

      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}
