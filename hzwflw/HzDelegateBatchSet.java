package ifs.hzwflw;

import java.util.LinkedHashMap;

import com.horizon.todo.grdb.GrdbUtil;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

public class HzDelegateBatchSet extends ASPPageProvider
{
  
   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzDelegateBatchSet");
   
   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------
   protected ASPTransactionBuffer trans;
   protected ASPCommand cmd;
   
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
 

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------
   
   protected ASPTabContainer tabs;

   
   private boolean refresh = false;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HzDelegateBatchSet (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans   = mgr.newASPTransactionBuffer();
      String tempCommand = mgr.readValue("__COMMAND");
      if( mgr.commandBarActivated() ){
         eval(mgr.commandBarFunction());
         if ("MAIN.Perform".equals(tempCommand))
         {
            String perform_command = mgr.readValue("__MAIN_Perform");
            if("unsetDelegate".equals(perform_command)){
               refresh = true;
            }
         }
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROCESS_KEY")) )
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
         mgr.showAlert("HZWFPROCESSDEFNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","HZ_WF_PROCESS_DEF_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      int length_short = 20;
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("PROCESS_KEY").
      setMandatory().
      setDynamicLOV("HZ_WF_PROCESS_DEF_SRC", "", 400, 600, false).
      
      setCustomValidation("PROCESS_KEY", "PROCESS_NAME").
      setLabel("HZWFPROCESSDEFPROCESSKEY: Process Key").
      setSize(length_short);
      
      headblk.addField("PROCESS_NAME").
      setInsertable().
      setLabel("HZWFPROCESSDEFPROCESSNAME: Process Name").
      setSize(length_short);
      
      headblk.addField("USERID").
      setSize(30).
      setLabel("HZWFPROCESSDEFUSERID: User Id");
      
      headblk.addField("USERNAME").
      setSize(30).
      setLabel("HZWFPROCESSDEFUSERNAME: User Name");
      
      
      headblk.addField("AGENTUSERID").
      setSize(30).
      setLabel("HZWFPROCESSDEFAGENTUSERID: Agent Id");
      
      headblk.addField("AGENTUSERNAME").
      setSize(30).
      setLabel("HZWFPROCESSDEFAGENTUSERNAME: Agent Name");
      
      headblk.addField("BEGINDATE","Date").
      setLabel("HZWFPROCESSDEFBEGINDATE: From ").
      setSize(20);
      
      headblk.addField("ENDDATE","Date").
      setLabel("HZWFPROCESSDEFENDDATE: To").
      setSize(10);
      
      headblk.addField("STATUS").
      setSize(length_short).
      setLabel("HZWFPROCESSDEFSTATUS: Status").
      setCheckBox("0,1");
      
      headblk.setView("HZ_PROCESS_USR_DELEGATE");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      
      headbar.addCustomCommand("setDelegate", "HZWFPROCESSDEFDELEGATESETCMD: Set Delegate...");
      headbar.addCustomCommand("unsetDelegate", "HZWFPROCESSDEFDELEGATEUNSETCMD: Unset Delegate...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HZWFPROCESSDEFTBLHEAD: Hz Wf Process Defs");
      headtbl.enableRowSelect();
      
      headtbl.setWrap();
      headbar.enableMultirowAction();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }
   

   public void  adjust()
   {
      // fill function body
   }
   
   
   public void unsetDelegate() throws FndException{
      ASPManager mgr = getASPManager();
      String currentOralceUserId = ifs.hzwflw.util.HzWfUtil.getOracleUserId(mgr).toUpperCase();
      headset.store();
      String processKeys = "";
      trans.clear();
      
      ASPBuffer buff = headset.getSelectedRows();
      int countRows = headset.countSelectedRows();
      if (countRows == 0) {
         mgr.showAlert(mgr.translate("HZWFPROCESSDEFSELECTAREC: You must select a record."));
      } else {
         for (int i = 0; i < countRows; i++) {
            ASPBuffer currRow = buff.getBufferAt(i);
            String processKey = currRow.getValue("PROCESS_KEY");
            processKeys += processKey + "^";
         }
      }
      
      String processName = "";
      String[] processKeyArray = processKeys.split("\\^");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < processKeyArray.length; i++) {
         sb.append(processKeyArray[i]).append("~").append(ifs.hzwflw.HzWfProcessDef.getProcessName(processKeyArray[i])).append("|");
      }
      
      LinkedHashMap agentMap = new LinkedHashMap(); 
      agentMap.put("userid", currentOralceUserId);
      agentMap.put("username", com.horizon.organization.orgimpl.OrgSelectImpl.getUserNameById(currentOralceUserId));
      agentMap.put("notice", "1");
      agentMap.put("noticetype", new String[]{"ReadMsg"});
//      agentMap.put("ids", processKeys + "~" + processName);
      agentMap.put("ids", sb.toString());
      agentMap.put("actionName", "0");
      agentMap.put("movetodo", "0");
      
      GrdbUtil grdbUtil = new GrdbUtil();
      grdbUtil.agentUserSet(agentMap);  // save Delegate Info
   }
   public void setDelegate() throws FndException{
      ASPManager mgr = getASPManager();
      
      headset.store();
      String processKeys = "";
      trans.clear();
      
      ASPBuffer buff = headset.getSelectedRows();
      int countRows = headset.countSelectedRows();
      if (countRows == 0) {
         mgr.showAlert(mgr.translate("HZWFPROCESSDEFSELECTAREC: You must select a record."));
      } else {
         for (int i = 0; i < countRows; i++) {
            ASPBuffer currRow = buff.getBufferAt(i);
            String processKey = currRow.getValue("PROCESS_KEY");
            processKeys += processKey + "^";
         }
      }
      
      String url = "./HzDelegateBatchSetInput.page?PROCESS_KEYS=" + processKeys;
      appendDirtyJavaScript("showNewBrowser_('" + url + "', 800, 600, 'NO'); \n");
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HZWFPROCESSDEFDESC: Process Delegate Batch Set";
   }


   protected String getTitle()
   {
      return "HZWFPROCESSDEFTITLE: Process Delegate Batch Set";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
      
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" submit(); return true;\n");
      appendDirtyJavaScript("}\n");
      
      if(refresh){
         appendDirtyJavaScript("refreshParent();");
      }
   }
}
