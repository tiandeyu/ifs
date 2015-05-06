package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPHTMLFormatter;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.FndRuntimeException;
import ifs.fnd.service.Util;
import ifs.genbaw.GenbawConstants;
import ifs.genbaw.GeneralAppGrp;
import ifs.genbaw.GeneralOrganization;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.horizon.db.Access;
import com.horizon.organization.orginterface.OrgInterfaceTools;
import com.horizon.todo.grdb.GrdbUtil;

public class HzDelegateSet extends ASPPageProvider {

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
   
   boolean editLayout = false;
   String processKey = null;
   String processName = null;
   
   
   
   final String sql = "select t.agentuserid AGENTUSERID, t.agentusername AGENTUSERNAME,t.begindate BEGINDATE,t.enddate ENDDATE from TD_HORIZON_AGENT t where t.status ='1' and t.userid= ? and t.flowid= ? ";
// 
// 
// 
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------
   public HzDelegateSet(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();
      
      processKey = mgr.readValue("PROCESS_KEY");
      processName =  mgr.readValue("PROCESS_NAME");
      
      
      if(mgr.isEmpty(processKey)){
         processKey = headset.getValue("PROCESS_KEY");
         processName =  headset.getValue("PROCESS_NAME");
      }
      
      
      if( mgr.commandBarActivated() ){
         eval(mgr.commandBarFunction());
      }
      
      
      
      
      
      
//      else if(mgr.buttonPressed("EDIT")){
//         headlay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
//      }
//      else if(mgr.buttonPressed("CONFIRM")){
//         headlay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
//         setDelegate();
//      }
      
      
      initDelegateSet();
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
     headset.clear();
     headset.addRow(null);
     headset.setValue("PROCESS_KEY",processKey);
     headset.setValue("PROCESS_NAME", processName);
   }
   
   
   public void initDelegateSet(){
      ASPManager mgr = getASPManager();
      //TO ADD QUERY CODE HERE.
      
      
      
      headset.clear();
      
      
      headset.addRow(null);
      headset.setValue("PROCESS_KEY",processKey);
      headset.setValue("PROCESS_NAME", processName);
      
      
      
    String tempUserId =null;
    String tempBeginDate = null;
    String tempEndDate =null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");;
      
      
      List<String> conditonList = new ArrayList<String>();
      conditonList.add(ifs.hzwflw.util.HzWfUtil.getOracleUserId(mgr).toUpperCase());
      conditonList.add(processKey);
      StringBuffer sb = new StringBuffer();
      sb.append(sql);
      System.out.println("******ifs.hzwflw.HzDelegateSet.initDelegateSet():sql_statment: " + sb);
      System.out.println("******ifs.hzwflw.HzDelegateSet.initDelegateSet():condition_list: " + conditonList);
      List list = Access.getMultiMap(sb.toString(), conditonList, "system");
      
      boolean hasDelegate = false;
      for (Iterator iterator = list.iterator(); iterator.hasNext();) {
         Map map = (Map) iterator.next();
         tempUserId  = (String) map.get("agentuserid");
         tempBeginDate  = (String) map.get("begindate");
         tempEndDate  = (String) map.get("enddate");
         hasDelegate = true;
      }
      if(headlay.isSingleLayout() && !hasDelegate){
         headset.clear();
         return;
      }
      

      
//      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
//      ASPQuery q = trans.addQuery("BPROCESSNAMES", sql);
//      q.addInParameter("PROCESS_KEY", processKey);
//      q.addInParameter("DELEGATOR",  ifs.hzwflw.util.HzWfUtil.getOracleUserId(mgr));//current_userid
//      
//      trans = mgr.perform(trans);
//      
//      ASPBuffer buffer = trans.getBuffer("BPROCESSNAMES");
      
//     String tempUserId = trans.getValue("BPROCESSNAMES/DATA/AGENTUSERID");
//     String tempBeginDate = trans.getValue("BPROCESSNAMES/DATA/BEGINDATE");
//     String tempEndDate = trans.getValue("BPROCESSNAMES/DATA/ENDDATE");
//     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//     
     Date beginDate =null;
     Date endDate = null;
     if(!mgr.isEmpty(tempBeginDate)){
        try {
           beginDate = sdf.parse(tempBeginDate);
      } catch (ParseException e) {
         throw new FndRuntimeException(e);
      }
     }
     if(!mgr.isEmpty(tempEndDate)){
        try {
           endDate = sdf.parse(tempEndDate);
      } catch (ParseException e) {
         throw new FndRuntimeException(e);
      }
     }

     
     if(!mgr.isEmpty(tempUserId)){
        headset.setValue("DELEGATOR", tempUserId);
     }
     if(null != beginDate){
        headset.setDateValue("START_TIME", beginDate);
     }
     if(null != endDate){
        headset.setDateValue("END_TIME", endDate);
     }
     
     
    
     
     
   }
   
   public void deleteDelegate(){
      ASPManager mgr = getASPManager();
      
      String delegator = null;
      java.util.Date startTime = null;
      java.util.Date endTime = null;
      String transfer = null;
      
      headset.changeRow();
      
//      delegator = mgr.readValue("DELEGATOR");
//      startTime = headset.getDateValue("START_TIME");
//      endTime   = headset.getDateValue("END_TIME");
//      transfer  = headset.getValue("TRANSFER_EXISTING_TODO");
//      
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String currentOracleId = ifs.hzwflw.util.HzWfUtil.getOracleUserId(mgr).toUpperCase();
      
      LinkedHashMap agentMap = new LinkedHashMap(); 
      agentMap.put("userid", currentOracleId);
      agentMap.put("username", com.horizon.organization.orgimpl.OrgSelectImpl.getUserNameById(currentOracleId));
//      String tempDefaultDept =  mgr.getASPContext().findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE);
//      if(mgr.isEmpty(tempDefaultDept)){
//         agentMap.put("userdeptname", com.horizon.organization.orgimpl.OrgSelectImpl.getDeptNameById(tempDefaultDept));
//      }else{
//         agentMap.put("userdeptname", "-");
//      }
      
//      agentMap.put("agentName", com.horizon.organization.orgimpl.OrgSelectImpl.getUserNameById(delegator));
//      agentMap.put("agentID", "U_" + delegator);
      agentMap.put("notice", "1");
      agentMap.put("noticetype", new String[]{"ReadMsg"});
      agentMap.put("ids", processKey + "~" + processName);
      agentMap.put("actionName", "0");
      agentMap.put("movetodo", "0");
//      agentMap.put("begindate", sdf.format(startTime));
//      agentMap.put("enddate", sdf.format(endTime));
      
      GrdbUtil grdbUtil = new GrdbUtil();
      grdbUtil.agentUserSet(agentMap);  // save Delegate Info
   }
   
   public void setDelegate(){
      ASPManager mgr = getASPManager();
      
      String delegator = null;
      java.util.Date startTime = null;
      java.util.Date endTime = null;
      String transfer = null;
      
      headset.changeRow();
      
      delegator = mgr.readValue("DELEGATOR");
      startTime = headset.getDateValue("START_TIME");
      endTime   = headset.getDateValue("END_TIME");
      transfer  = headset.getValue("TRANSFER_EXISTING_TODO");
      
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String currentOracleId = ifs.hzwflw.util.HzWfUtil.getOracleUserId(mgr).toUpperCase();
      
      LinkedHashMap agentMap = new LinkedHashMap(); 
      agentMap.put("userid", currentOracleId);
      agentMap.put("username", com.horizon.organization.orgimpl.OrgSelectImpl.getUserNameById(currentOracleId));
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
      agentMap.put("ids", processKey + "~" + processName);
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
      headblk.addField("PROCESS_KEY").
      setFunction("''").
      setReadOnly().
      setLabel("HZDELEGATESETPROCESSKEY: Process Key").
      setDynamicLOV("HZ_WF_PROCESS_DEF").
      setMandatory().
      setSize(20);
      headblk.addField("PROCESS_NAME").
      setFunction("''").
      setReadOnly().
      setLabel("HZDELEGATESETPROCESSNAME: Process Name").
      setSize(20);
      
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
      setFunction("''").
      setSize(20).
      setMandatory().
      setLabel("HZDELEGATESETSTARTTIME: Start Time");
      headblk.addField("END_TIME", "Date").
      setFunction("''").
      setMandatory().
      setSize(20).
      setLabel("HZDELEGATESETENDTIME: End Time");
      
      headblk.addField("TRANSFER_EXISTING_TODO").
      setFunction("''").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("HZDELEGATESETTRANSFEREXISTINGTODOS: Transfer Existing Todos").setHidden();
      
      headblk.setView("DUAL");
      headblk.defineCommand("HZ_BIZ_WF_CONFIG_API","New__,Modify__,Remove__");
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
      headlay.setSimple("PROCESS_NAME");
      headlay.setSimple("DELEGATOR_NAME");
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
//      headlay.setDialogColumns(1);
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
//         mgr.getASPField("TRANSFER_EXISTING_TODO").unsetHidden();
      }
      
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HZDELEGATESETDESC: Hz Delegate Set";
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
      printHiddenField("PROCESS_KEY", processKey);
      printHiddenField("PROCESS_NAME", processName);
      printSpaces(2);
//      if(headlay.isEditLayout()){
//         appendToHTML(fmt.drawSubmit("CONFIRM", "HZDELEGATESETCONFIRMBUTTON: Confirm", ""));
//      }else{
//         appendToHTML(fmt.drawSubmit("EDIT", "HZDELEGATESETEDITBUTTON: EDIT", ""));
//      }
      
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------
   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}
