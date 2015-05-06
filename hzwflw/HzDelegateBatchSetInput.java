package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPHTMLFormatter;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.genbaw.GenbawConstants;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.horizon.db.Access;
import com.horizon.todo.grdb.GrdbUtil;

public class HzDelegateBatchSetInput  extends ASPPageProvider {

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzDelegateBatchSetInput");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   boolean editLayout = false;
   String processKeys = null;
   
   String currentOralceUserId = null;
   
   boolean submit = false;
   
   final String sql = "select t.agentuserid AGENTUSERID, t.agentusername AGENTUSERNAME,t.begindate BEGINDATE,t.enddate ENDDATE from TD_HORIZON_AGENT t where t.status ='1' and t.userid= ? and t.flowid= ? ";
   
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------
   public HzDelegateBatchSetInput(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();
      currentOralceUserId = ifs.hzwflw.util.HzWfUtil.getOracleUserId(mgr).toUpperCase();
      processKeys = mgr.readValue("PROCESS_KEYS");
      String tempCommand = mgr.readValue("__COMMAND");
      if( mgr.commandBarActivated() ){
         eval(mgr.commandBarFunction());
         if("MAIN.SaveReturn".equals(tempCommand)) {
            submit = true;
         }
      }
      
      if(!submit){
         initDelegateSet();
      }
      
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void initDelegateSet(){
      ASPManager mgr = getASPManager();
      
      headset.clear();
      headset.addRow(null);
      
      
     Date beginDate = new Date();
     Date endDate = new Date();
     
     if(null != beginDate){
        headset.setDateValue("START_TIME", beginDate);
     }
     if(null != endDate){
        headset.setDateValue("END_TIME", endDate);
     }
   }
   
   public void deleteDelegate(){
      ASPManager mgr = getASPManager();
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
   
   public void setDelegate(){
      ASPManager mgr = getASPManager();
      
      headset.store();
      
      int currrow = headset.getCurrentRowNo();

      headset.changeRow();
      headset.goTo(currrow);
      
      String delegator = null;
      java.util.Date startTime = null;
      java.util.Date endTime = null;
      String transfer = null;
      
      String processName = "";
      String[] processKeyArray = processKeys.split("\\^");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < processKeyArray.length; i++) {
         sb.append(processKeyArray[i]).append("~").append(ifs.hzwflw.HzWfProcessDef.getProcessName(processKeyArray[i])).append("|");
      }
      
      delegator = mgr.readValue("DELEGATOR");
      startTime = headset.getDateValue("START_TIME");
      endTime   = headset.getDateValue("END_TIME");
      transfer  = headset.getValue("TRANSFER_EXISTING_TODO");
      
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      
      LinkedHashMap agentMap = new LinkedHashMap(); 
      agentMap.put("userid", currentOralceUserId);
      agentMap.put("username", com.horizon.organization.orgimpl.OrgSelectImpl.getUserNameById(currentOralceUserId));
      String tempDefaultDept =  mgr.getASPContext().findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE);
      if(mgr.isEmpty(tempDefaultDept)){
         agentMap.put("userdeptname", com.horizon.organization.orgimpl.OrgSelectImpl.getDeptNameById(tempDefaultDept));
      }else{
         agentMap.put("userdeptname", "-");
      }
      
      String sql = "select count(1) pcount from person_info_lov where user_id =?";
      List conditionList = new ArrayList();
      conditionList.add(delegator);
      Map retMap = Access.getSingleMap(sql, conditionList);
      BigDecimal bd = (BigDecimal)retMap.get("pcount");
      
      if(bd.intValue() == 0){
         mgr.showAlert("HZDELEGATESETINVALIDEDELEGATOR: Invalid Delegator!");
         return;
      }
      
      agentMap.put("agentName", com.horizon.organization.orgimpl.OrgSelectImpl.getUserNameById(delegator));
      agentMap.put("agentID", "U_" + delegator);
      agentMap.put("notice", "1");
      agentMap.put("noticetype", new String[]{"ReadMsg"});
//      agentMap.put("ids", processKeys + "~" + processName);
      agentMap.put("ids", sb.toString());
      agentMap.put("actionName", "1");
      String moveTodo = "TRUE".equals(transfer) ? "1":"0";
      System.out.println("moveTodo:" + moveTodo);
      agentMap.put("movetodo", moveTodo);
      agentMap.put("begindate", sdf.format(startTime));
      agentMap.put("enddate", sdf.format(endTime));
      
      GrdbUtil grdbUtil = new GrdbUtil();
      grdbUtil.agentUserSet(agentMap);  // save Delegate Info
   }


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");

      headblk.addField("DELEGATOR").
      setFunction("''").
      setInsertable().
      setLabel("HZDELEGATESETDELEGATOR: Delegator").
      setMandatory().
      setLOVProperty("WHERE", "USER_ID IS NOT NULL").
      setSize(20).
      setDynamicLOV("PERSON_INFO_USER");
      
      headblk.addField("DELEGATOR_NAME").
      setFunction("Fnd_User_Api.Get_Description(:DELEGATOR)").
      setReadOnly().
      setLabel("HZDELEGATESETDELEGATOR: Delegator Name").
      setSize(20);
      mgr.getASPField("DELEGATOR").setValidation("DELEGATOR_NAME");
      
      headblk.addField("START_TIME", "Date").
      setSize(20).
      setMandatory().
      setLabel("HZDELEGATESETSTARTTIME: Start Time");
      
      headblk.addField("END_TIME", "Date").
      setMandatory().
      setSize(20).
      setLabel("HZDELEGATESETENDTIME: End Time");
      
      headblk.addField("TRANSFER_EXISTING_TODO").
      setFunction("''").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("HZDELEGATESETTRANSFEREXISTINGTODOS: Transfer Existing Todos");
      
      headblk.setView("DUAL");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
//      headbar.defineCommand(headbar.EDITROW,"editRow");
      headbar.defineCommand(headbar.DELETE,"deleteRow");
      headbar.defineCommand(headbar.SAVERETURN,"saveRow");
      headbar.disableCommand(ASPCommandBar.EDIT);
      headbar.disableCommand(ASPCommandBar.EDITROW);
//      headbar.disableCommand(ASPCommandBar.DELETE);
//      headbar.disableCommand(ASPCommandBar.CANCELEDIT);
      headbar.disableCommand(ASPCommandBar.SAVENEW);
//      headbar.disableCommand(ASPCommandBar.NEWROW);
      headbar.disableCommand(ASPCommandBar.FIND);
      headbar.disableCommand(ASPCommandBar.PROPERTIES);
      headbar.disableCommand(ASPCommandBar.DUPLICATEROW);
      headbar.disableCommand(ASPCommandBar.DUPLICATE);
      headbar.disableCommand(ASPCommandBar.BACK);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HZBIZWFCONFIGTBLHEAD: Hz Biz Wf Configs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      
      headlay.setDataSpan("DELEGATOR", 5);
      headlay.setDataSpan("TRANSFER_EXISTING_TODO", 5);
      headlay.setSimple("DELEGATOR_NAME");
      headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
   }
   
   public void editRow(){
      headlay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
      initDelegateSet();
   }
   
   public void deleteRow(){
      headlay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      deleteDelegate();
      headset.clear();
   }
   public void addRow(){
      headlay.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
      setDelegate();
      initDelegateSet();
   }
   
   
   
   public void addDelegate(){
      
   }
   
   public void saveRow(){
      headlay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      setDelegate();
   }


   public void adjust()
   {
      ASPManager mgr = getASPManager();
      if(null != headset && headset.countRows() > 0){
         headbar.disableCommand(ASPCommandBar.NEWROW);
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HZDELEGATESETDESC: Delegate Set";
   }


   protected String getTitle()
   {
      return getDescription();
   }


   protected void printContents() throws FndException
   {
      super.printContents();

      ASPManager mgr = getASPManager();
      ASPHTMLFormatter fmt=mgr.newASPHTMLFormatter();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      appendToHTML("");
      printHiddenField("PROCESS_KEYS", processKeys);
      printSpaces(2);
      
      if(submit){
         appendDirtyJavaScript(" window.opener.refreshParent();");
         appendDirtyJavaScript(" window.close();");
      }
      
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------
   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}