package ifs.hzwflw;

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
import ifs.fnd.util.Str;

public class HzNodeSyncSet extends ASPPageProvider
{

	   //-----------------------------------------------------------------------------
	   //---------- Static constants ------------------------------------------------
	   //-----------------------------------------------------------------------------

	   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzNodeSyncSet");

	   //-----------------------------------------------------------------------------
	   //---------- Header Instances created on page creation --------
	   //-----------------------------------------------------------------------------

	   private ASPBlock headblk;
	   private ASPRowSet headset;
	   private ASPCommandBar headbar;
	   private ASPTable headtbl;
	   private ASPBlockLayout headlay;
	   String processKey = "";
	   String processNodeId = "";
	   String pagePath = "";

	   //-----------------------------------------------------------------------------
	   //------------------------  Construction  ---------------------------
	   //-----------------------------------------------------------------------------

	   public  HzNodeSyncSet (ASPManager mgr, String page_path)
	   {
	      super(mgr,page_path);
	   }

	   public void run()
	   {
	      ASPManager mgr = getASPManager();
//	      PROCESS_KEY
//	      NODE_ID
	      processKey = mgr.readValue("P_PROCESS_KEY");
	      processNodeId = mgr.readValue("P_NODE_ID");
	      pagePath = mgr.readValue("P_PAGE_PATH");
	      if( mgr.commandBarActivated() )
	         eval(mgr.commandBarFunction());
	      else if(mgr.dataTransfered())
	         okFind();
	      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
	         okFind();
	      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROCESS_KEY")) )
	         okFind();else if( !mgr.isEmpty(mgr.getQueryStringValue("PROCESS_NODE")) )
	         okFind();else if( !mgr.isEmpty(mgr.getQueryStringValue("COMMAND_ID")) )
	         okFind();else okFind();
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
	      if(!Str.isEmpty(processKey)){
	    	  q.addWhereCondition(" PROCESS_KEY = ?");
	    	  q.setParameter("PROCESS_KEY", processKey);
	      }
	      if(!Str.isEmpty(processNodeId)){
	    	  q.addWhereCondition(" PROCESS_NODE = ?");
	    	  q.setParameter("PROCESS_NODE", processNodeId);
	      }
	      if(!Str.isEmpty(pagePath)){
	    	  q.addWhereCondition(" PAGE_PATH = ?");
	    	  q.setParameter("PAGE_PATH", pagePath);
	      }
	      
	      mgr.querySubmit(trans,headblk);
	      if (  headset.countRows() == 0 )
	      {
//	         mgr.showAlert("HZNODESYNCNODATA: No data found.");
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

	      cmd = trans.addEmptyCommand("HEAD","HZ_NODE_SYNC_API.New__",headblk);
	      cmd.setOption("ACTION","PREPARE");
	      cmd.setParameter("PROCESS_KEY", processKey);
	      cmd.setParameter("PROCESS_NODE", processNodeId);
	      cmd.setParameter("PAGE_PATH", pagePath);
	      trans = mgr.perform(trans);
	      data = trans.getBuffer("HEAD/DATA");
	      headset.addRow(data);
	   }


	   //-----------------------------------------------------------------------------
	   //------------------------  Predefines Head ---------------------------
	   //-----------------------------------------------------------------------------

	   public void  preDefine()
	   {
	      ASPManager mgr = getASPManager();

	      headblk = mgr.newASPBlock("MAIN");
	      headblk.addField("OBJID").
	              setHidden();
	      headblk.addField("OBJVERSION").
	              setHidden();
	      headblk.addField("PROCESS_KEY").
	              setMandatory().
	              setInsertable().
	              setLabel("HZNODESYNCPROCESSKEY: Process Key").setHidden().
	              setSize(20);
	      headblk.addField("PROCESS_NODE").
	              setMandatory().
	              setReadOnly().
	              setLabel("HZNODESYNCPROCESSNODE: Process Node").
	              setSize(20);
	      headblk.addField("PAGE_PATH").
	      setMandatory().
	      setInsertable().
	      setLabel("HZNODESYNCPAGEPATH: Page Path").
	      setSize(20);
	      

	      
	      headblk.addField("SYNC_ITEM").
          setMandatory().
          setInsertable().
          setLabel("HZNODESYNCSYNCITEM: Sync Item").
          setDynamicLOV("HZ_FLOW_INFO_SYNC_SET").
          setSize(20);
	      
	      headblk.addField("ITEM_DESC").
	      setFunction("HZ_FLOW_INFO_SYNC_SET_API.GET_ITEM_DESC(:SYNC_ITEM)").
	      setLabel("HZNODESYNCITEMNAME: Item Name").
	      setReadOnly().
	      setSize(20); 
	      mgr.getASPField("SYNC_ITEM").setValidation("ITEM_DESC");

	      
	      headblk.addField("TABLE_NAME").
	      setFunction("HZ_PAGE_TABLE_PAIR_API.GET_TABLE_NAME(:PAGE_PATH)").
	      setLabel("HZNODESYNCTABLENAME: Table Name").
	      setDynamicLOV("HZ_PAGE_TABLE_PAIR_LOV","PAGE_PATH").
	      setSize(20); 
	      
          headblk.addField("TABLE_COL").
          setMandatory().
          setInsertable().
          setLabel("HZNODESYNCTABLECOL: Table Col").
          setDynamicLOV("USER_TAB_COLS_LOV").
          setSize(20);
          
	      headblk.setView("HZ_NODE_SYNC");
	      headblk.defineCommand("HZ_NODE_SYNC_API","New__,Modify__,Remove__");
	      headset = headblk.getASPRowSet();
	      headbar = mgr.newASPCommandBar(headblk);
	      headtbl = mgr.newASPTable(headblk);
	      headtbl.setTitle("HZNODESYNCTBLHEAD: Hz Node Synchronize");
	      headtbl.enableRowSelect();
	      headtbl.setWrap();
	      headlay = headblk.getASPBlockLayout();
	      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	   }



	   public void  adjust()
	   {
	      // fill function body
	   }

	   //-----------------------------------------------------------------------------
	   //------------------------  Presentation functions  ---------------------------
	   //-----------------------------------------------------------------------------

	   protected String getDescription()
	   {
	      return "HZNODESYNCDESC: Hz Node Sync";
	   }


	   protected String getTitle()
	   {
	      return "HZNODESYNCTITLE: Hz Node Sync" ;
	   }


	   protected void printContents() throws FndException
	   {
	      ASPManager mgr = getASPManager();
	      
	      printHiddenField("P_PROCESS_KEY", processKey);
	      printHiddenField("P_NODE_ID", processNodeId);	
	      printHiddenField("P_PAGE_PATH", pagePath);	
	      
	      
	      
	      if (headlay.isVisible())
	          appendToHTML(headlay.show());

	   }
	}
