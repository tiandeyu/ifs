
package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import ifs.hzwflw.util.BizWfConnVO;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.HzWfUtil;
import ifs.hzwflw.util.URL;
import ifs.hzwflw.util.Util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.horizon.db.Access;
import com.horizon.organization.orgimpl.OrgSelectImpl;
import com.horizon.workflow.flowengine.api.XMLWorkFace;
import com.horizon.workflow.flowengine.api.XMLWorkflow;
import com.horizon.workflow.flowengine.pub.StaticVar;
import com.horizon.workflow.flowengine.pub.XMLWork;
import com.horizon.workflow.workflowservice.IWorkflowCommon;
import com.horizon.workflow.workflowservice.bean.FlowFieldVar;
import com.horizon.workflow.workflowservice.impl.WorkflowCommonImpl;



public abstract class HzASPPageProviderWf extends ASPPageProvider implements HzConstants {

	public HzASPPageProviderWf(ASPManager mgr, String pagePath) {
		super(mgr, pagePath);
	}
	
	
	
	 // -----------------------------------------------------------------------------
	   // ---------- Header Instances created on page creation --------
	   // -----------------------------------------------------------------------------
	   private ASPBlock headblk;
	   private ASPRowSet headset;
	   private ASPCommandBar headbar;
	   private ASPTable headtbl;
	   private ASPBlockLayout headlay;
	   
	   private String resultFlag = null;
	   
	   private String mandatoryHintStr= "";
	   private boolean canSubmitWorkFlow = true;
	 
	   private static final String prefix_wf_ob_dr = "__WF_OB_DR__";
	   
	   protected void run() throws FndException
	   {
	      if ( DEBUG ) debug(this+": HzASPPageProviderWf.run()");
	      headblk = getBizWfBlock();
	      headset = headblk.getASPRowSet();
	      headbar = headblk.getASPCommandBar();
	      headtbl = headblk.getASPTable();
	      headlay = headblk.getASPBlockLayout();
	   }
	   
	   
	   public boolean preApproveStart(){
	      return true;
	   }
	   
	   /**
	    * start process
	    */
	   public void approveStart() throws FndException {
	      if(false == preApproveStart()){
	         return;
	      }
	      
	      ASPManager mgr = getASPManager();
	      BizWfConnVO bizWfConnVo = prepareBizConnVo();
	      String userid = HzWfUtil.getOracleUserId(mgr).toUpperCase();

	      URL redirectUrl = new URL(PAGE_URL_START_PROCESS);
	      redirectUrl.addParameters(PARA_BIZ_LU_NAME, bizWfConnVo.getBizLuName());
	      redirectUrl.addParameters(PARA_BIZ_VIEW_NAME, bizWfConnVo.getBizViewName());
	      redirectUrl.addParameters(PARA_BIZ_KEY_REF_PAIR, bizWfConnVo.getLuKeyValuePairs());
	      redirectUrl.addParameters(PARA_BIZ_URL, bizWfConnVo.getBizPagePath());
	      
	      
	      String currentBizProcessId = getCurrentBizProcessId();
	      if(! mgr.isEmpty(currentBizProcessId)){
	         return;//when save during process handling
	      }

	      List<String[]> processList = null;
	      try {
	      	processList =  getPageProcesses( mgr.getURL());
	      } catch (Exception e1) {
	         mgr.showAlert("ABSTRACTASPPAGEPROVIDERWFQRYPROCESSFAIL: Failed to query process definiton.");
	         return;
	      }

	      if (Util.isEmpty(processList)) {
	         mgr.showAlert("ABSTRACTASPPAGEPROVIDERWFNOINITITEMFOUND: No initiate item was found.");
	         return;
	      }
	      String topic = headblk.getWfDescription();
	      // only one process can be started.
         if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug("^^^^^^^^APPROVESTART:processList.size()" +processList.size());
	      if (processList.size() == 1) {
	         if (Util.isEmpty(topic)) {
	            String url = redirectUrl.toString();
	            appendDirtyJavaScript("showNewBrowser_('" + url + "', 800, 600, '" + (url.indexOf("SelectMainFrame.page") > 0 ? "NO" : "YES") + "'); \n");
	         } else {
	            
	            boolean initAuth = validateInitAuth(processList.get(0)[0], userid);
	            if(false == initAuth){
	               mgr.showAlert("ABSTRACTASPPAGEPROVIDERNORIGHTTOINITTHEPROCESS: You do not have enough right to start the process.");
	               return;
	            }
	            String bizUrl = bizWfConnVo.getBizPagePath() + "?" + bizWfConnVo.getLuKeyValuePairs().replaceAll("\\^", "\\&");
	            URL bizKey = new URL(bizUrl);
	            String deptname = "huizheng";// TODO XXXX
	            XMLWorkflow xwork = new XMLWorkflow(userid, userid, deptname);
	            xwork.setDataIdentifier("system");
	            xwork.setFlowIdentifier("system");
	            int result = xwork.createWork(processList.get(0)[0]);
               if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug("^^^^^^^^APPROVESTART:Line:129:======" + result + xwork.getBackMsg());
	            if (StaticVar.Init_Success == result) {
	               XMLWorkFace xmlWorkFace = new XMLWorkFace();
	               xmlWorkFace.setWorkid(xwork.getWork().getWorkID());
	               xmlWorkFace.setMsgSendFlag("Todo");
	               xmlWorkFace.setNodeid(xwork.getWork().getCurNodeID());
	               xmlWorkFace.setTrackid(xwork.getWork().getCurTrackID());
	               xmlWorkFace.setUserid(userid);
	               xmlWorkFace.setUsername(mgr.getASPContext().findGlobal("HZ_SESSION_USER_NAME"));
	               xmlWorkFace.setDataIdentifier("system");
	               xmlWorkFace.setFlowIdentifier("system");
	               LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	               map.put("workid", xwork.getWork().getWorkID());
	               //Modified by luqingwei, 2015-01-29 11:30:03
//	               map.put("dataid", bizKey.toString());
	               map.put("dataid",  xwork.getWork().getWorkID());
	               //Modified end.
	               map.put("FORMID", xwork.getWork().getCurForm()[0].getID());

	               //Modified by luqingwei, 2015-01-29 11:30:03
//	               map.put(xwork.getWork().getCurForm()[0].getID() + "_ID", bizKey.toString());
	               map.put(xwork.getWork().getCurForm()[0].getID() + "_ID",  xwork.getWork().getWorkID());
	               //Modified end.
	               map.put(xwork.getWork().getCurForm()[0].getID() + "_TITLE", topic);//
	               xmlWorkFace.setClassForGetAppSQL("com.horizon.workflow.flowengine.impl.example.AppDataSave");
	               xmlWorkFace.setOtherPara(map);
	               
	               bizWfConnVo.setProcessKey(processList.get(0)[0]);
                  bizWfConnVo.setProcessId(xwork.getWork().getWorkID());
                  bizWfConnVo.setProcessName(xwork.getWork().getFlowName());
	               insertBizProcessId(bizWfConnVo);
	               
	               result = xmlWorkFace.Action_Submit();
	               if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug("^^^^^^^^APPROVESTART:Line150:" + result + xwork.getBackMsg());
	               if (StaticVar.F_STATUS_Success == result) {

	                  try {
//	                     insertBizProcessId(bizWfConnVo);
	                  } finally {
	                     boolean isCLose = XMLWork.exitWindow( xwork.getWork().getWorkID(), xwork.getWork().getCurNodeID(),
	                           xwork.getWork().getCurTrackID(), xwork.getWork().getCurUserID());
	                     if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug(isCLose + "Line:159:===========================EXIT HUIZHENG PROCESS.");
	                  }
	                  URL todoUrl = new URL(TODOUrl);
	                  todoUrl.addParameters("ToDo_Workid", xwork.getWork().getWorkID());
	                  appendDirtyJavaScript("showNewBrowser_('" + todoUrl + "', (window.screen.availWidth-10), (window.screen.availHeight-30), 'NO'); \n");
	               }else{
	                  
	                  deleteBizProcessId(bizWfConnVo);
	                  
	                  mgr.showAlert(xwork.getBackMsg());
	                  try{
	                     boolean isCLose = XMLWork.exitWindow( xwork.getWork().getWorkID(), xwork.getWork().getCurNodeID(),
	                           xwork.getWork().getCurTrackID(), xwork.getWork().getCurUserID());
	                     if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug(isCLose + "Line:169===========================EXIT HUIZHENG PROCESS.");
	                  }catch(Throwable th){
	                     // no-op
	                     th.printStackTrace();
	                  }
	               }
	            }else if (StaticVar.Init_NoRole == result) {
	               mgr.showAlert(xwork.getBackMsg());
	            } else {
	               mgr.showAlert(xwork.getBackMsg());
	               boolean isCLose = XMLWork.exitWindow( xwork.getWork().getWorkID(), xwork.getWork().getCurNodeID(),
	                     xwork.getWork().getCurTrackID(), xwork.getWork().getCurUserID());
	               if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug(isCLose + "===========================EXIT HUIZHENG PROCESS.");
	            }
	         }
	      } else {// has more than one process to start.
	         StringBuffer processSb = new StringBuffer();
	         for (Iterator iterator = processList.iterator(); iterator
                  .hasNext();) {
               String[] processInfo = (String[]) iterator.next();
               processSb.append(processInfo[0] ).append("||");
               
            }
	         if (false == mgr.isEmpty(topic)) { 
               try {
                  redirectUrl.addParameters(PARA_PROCESS_TOPIC, java.net.URLEncoder.encode(topic, "UTF-8"));
                  redirectUrl.addParameters(PARA_PROCESS_KEY_STR, java.net.URLEncoder.encode(processSb.toString(), "UTF-8"));
               } catch (UnsupportedEncodingException e) {
                 //ignore
               }
            }
	         
	         String url = redirectUrl.toString();
	         appendDirtyJavaScript("showNewBrowser_('" + url + "', 800, 600, 'NO'); \n");
	      }
	   }


	   /**
	    * view process.
	    * 
	    * @throws FndException
	    */
	   public void approveView() throws FndException {
	      String processId = getCurrentBizProcessId(); 
	      URL redirectUrl = new URL(PAGE_URL_PROCESS_VIEW);//identifier=system&workid=
	      redirectUrl.addParameters("identifier", "system");
	      redirectUrl.addParameters("workid", processId);
	      appendDirtyJavaScript("showNewBrowser_('" + redirectUrl.toString()+ "', 800, 600, '" + "YES" + "'); \n");
	   }
	   
	   
	   public String getCurrentRealUserDept(){
	        String c = getCurrentRealUser();
	        return OrgSelectImpl.getUserDeptByUserStr(c);
	   }
	   public String getCurrentRealUser(){
	      ASPManager mgr = getASPManager();
         String userTaskId = mgr.readValue("_WF_USERTASKID_");//
         
         if(Str.isEmpty(userTaskId)){
            return null;
         }
         String sql = "select t.userid realuserid from TD_HORIZON_User t where t.id = ?";
         List<String> lstConditions = new ArrayList<String>();
         lstConditions.add(userTaskId);
         
         Map retMap = Access.getSingleMap(sql, lstConditions);
         String realuserid = (String)retMap.get("realuserid");
         
	      return realuserid;
	   }
	  
	   public void adjust() throws FndException{
	      ASPManager mgr = getASPManager();
	      String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
	      //add by natic May 7th 2015
	      //Make it auto refresh. 
	      String comnd = mgr.readValue("__COMMAND");
         if (  "MAIN.SaveReturn".equals(comnd))
            headset.refreshAllRows();
         else if("MAIN.SaveNew".equals(comnd))
            headset.refreshAllRows();
         //add end
	      if (false == FROM_NAVIGATOR.equals(fromPage)) {
	      	if(!headlay.isEditLayout() && !headlay.isSingleLayout()){
	      		headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
	      	}
	      }else{
	      }
	      // Added by Terry 20140514
	      // Check status of work flow and set VIEW to session.
	      checkFlowStateForDr();
	      // Added end
	      
	      
	      int countRowsNum =  headset.countRows();
	      if( countRowsNum <= 0){
	         return;
	      }

	      try{
	         controlWorkflowButton();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      try{
	         controlIfsButton();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      try{
	         controlIfsCustomButton();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      try{
	         controlIfsField();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      try{
	    	  controlIfsCommand();
	      } catch (Exception e) {
	    	  e.printStackTrace();
	      }
	      try{
	         setWorkflowVariables();;
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }
	   
	   private void controlIfsCustomButton() throws Exception {
	      if (!headlay.isSingleLayout() && !headlay.isEditLayout()) {
	         return;
	      }
	      ASPManager mgr = getASPManager();
	      List<String[]> processNames = getPageProcesses( mgr.getURL());
	      String currentBizProcessId = getCurrentBizProcessId();
	      String fromPage = mgr.readValue(FROM_FLAG, FROM_NAVIGATOR);
	      if (FROM_NAVIGATOR.equals(fromPage)) {
	         if (mgr.isEmpty(currentBizProcessId)) {// has not started a process
	            ctlIfsCustomBtnFromNavWithoutFlow();
	         } else {// has started a process, view process.
	            ctlIfsCustomBtnFromNavWithFlow();
	         }
	      } else if (FROM_TODO.equals(fromPage)) {
	         ctlIfsCustomBtnFromTodo();
	      } else if (FROM_DONE.equals(fromPage)) {
	         ctlIfsCustomBtnFromDone();
	      } else if (FROM_SEND_VIEW.equals(fromPage)) {
	         ctlIfsCustomBtnFromToRead();
	      } else if (FROM_SEND_VIEW_FIN.equals(fromPage)) {
	         ctlIfsCustomBtnFromRead();
	      } 
	   }  
	   
	   protected void ctlIfsCustomBtnFromNavWithFlow() throws Exception {
	      
	   }
	   protected void ctlIfsCustomBtnFromNavWithoutFlow() throws Exception {
	      
	   }
	   protected void ctlIfsCustomBtnFromTodo() throws Exception {
	      
	   }
	   protected void ctlIfsCustomBtnFromDone() throws Exception {
	      
	   }
	   protected void ctlIfsCustomBtnFromToRead() throws Exception {
	      
	   }
	   protected void ctlIfsCustomBtnFromRead() throws Exception {
	      
	   }
	   
   public void controlWorkflowButton() throws Exception {
      if (!headlay.isSingleLayout() && !headlay.isEditLayout()) {
         return;
      }
      ASPManager mgr = getASPManager();
      headbar.disableCommandGroupExtra(ASPCommandBar.CMD_GROUP_APPROVE);// default:close all.
      List<String[]> processNames = getPageProcesses( mgr.getURL());
      String currentBizProcessId = getCurrentBizProcessId();
      String fromPage = mgr.readValue(FROM_FLAG, FROM_NAVIGATOR);
      if (FROM_NAVIGATOR.equals(fromPage)) {
         if (mgr.isEmpty(currentBizProcessId)) {// has not started a process
            if (false == (null == processNames || processNames.isEmpty())) { // has a process to start.
               headbar.enableCommand(ASPCommandBar.APPROVESTART);
            } else {
               // ignore 
            }
         } else {// has started a process, view process.
            headbar.enableCommand(ASPCommandBar.APPROVEVIEW);
         }
      } else if (FROM_TODO.equals(fromPage)) {
      } else if (FROM_DONE.equals(fromPage)) {
      } else if (FROM_TRACK.equals(fromPage)) {
      } else if (FROM_SEND_VIEW.equals(fromPage)) {
      } else if (FROM_SEND_VIEW_FIN.equals(fromPage)) {
      } else if (FROM_DELEGATED_WORKBENCH.equals(fromPage)) {
      } else if (FROM_RETRIEVE_WORKBENCH.equals(fromPage)) {
      }
   }  
   
   

	   
	   public void controlIfsButton() throws Exception {
	      
	    if(headlay.isMultirowLayout()){
          headbar.disableCommand(ASPCommandBar.DELETE);
//          headbar.disableCommand(ASPCommandBar.EDIT);
//          headbar.disableCommand(ASPCommandBar.EDITROW);
//          headbar.disableCommand(ASPCommandBar.OVERVIEWEDIT);
	    }
	      
	    if(!headlay.isSingleLayout() && !headlay.isEditLayout()){
	   		return ;
	   	}
	   	
	      ASPManager mgr = getASPManager();
	      Vector<ASPBlock> subblks = getASPBlocks();

	      String currentBizProcessId = getCurrentBizProcessId();
	      String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
	      if (FROM_NAVIGATOR.equals(fromPage)) {

	         if (headset.countRows() > 0&& (headlay.isSingleLayout() || headlay.isCustomLayout())) {
	            if (mgr.isEmpty(currentBizProcessId)) {
	               // ignore
	            } else {
	               headbar.disableCommand(ASPCommandBar.DELETE);
	               headbar.disableCommand(ASPCommandBar.EDIT);
	               headbar.disableCommand(ASPCommandBar.EDITROW);
	               for (Iterator iterator = subblks.iterator(); iterator.hasNext();) {
	                  ASPBlock subblk = (ASPBlock) iterator.next();
	                  if (subblk != headblk) {
	                     try {
	                        if(subblk.hasCommandBar()){
	                            ASPCommandBar subBar = subblk.getASPCommandBar();
	                            subBar.disableCommand(ASPCommandBar.DELETE);
	                            subBar.disableCommand(ASPCommandBar.EDIT);
	                            subBar.disableCommand(ASPCommandBar.EDITROW);
	                            subBar.disableCommand(ASPCommandBar.NEWROW);
	                            subBar.disableCommand(ASPCommandBar.DUPLICATEROW);
	                        }
	                     } catch (Exception e) {
	                        // ignore
	                     }
	                  }
	               }
	            }
	         } else {
	            headbar.disableCommand(ASPCommandBar.DELETE);
	         }
	      } else if (FROM_TODO.equals(fromPage)) {
	         headbar.disableCommand(ASPCommandBar.FIND);
	         headbar.disableCommand(ASPCommandBar.DELETE);
	         headbar.disableCommand(ASPCommandBar.NEWROW);
	         headbar.disableCommand(ASPCommandBar.BACK);
	         headbar.disableCommand(ASPCommandBar.DUPLICATEROW);
	      } else if (FROM_TRACK.equals(fromPage) || 
	            FROM_DONE.equals(fromPage) || 
	            FROM_SEND_VIEW.equals(fromPage) ||
	            FROM_SEND_VIEW_FIN.equals(fromPage) || 
	            FROM_DELEGATED_WORKBENCH.equals(fromPage) ||
	            FROM_RETRIEVE_WORKBENCH.endsWith(fromPage)) {
	         headbar.disableCommand(ASPCommandBar.FIND);
	         headbar.disableCommand(ASPCommandBar.DELETE);
	         headbar.disableCommand(ASPCommandBar.NEWROW);
	         headbar.disableCommand(ASPCommandBar.BACK);
	         headbar.disableCommand(ASPCommandBar.DUPLICATEROW);
	         headbar.disableCommand(ASPCommandBar.EDIT);
	         headbar.disableCommand(ASPCommandBar.EDITROW);

	         for (Iterator iterator = subblks.iterator(); iterator.hasNext();) {
	            ASPBlock subblk = (ASPBlock) iterator.next();
	            if (subblk != headblk) {
	               try {
	                  if(subblk.hasCommandBar()){
	                      ASPCommandBar subBar = subblk.getASPCommandBar();
	                      subBar.disableCommand(ASPCommandBar.DELETE);
	                      subBar.disableCommand(ASPCommandBar.EDIT);
	                      subBar.disableCommand(ASPCommandBar.EDITROW);
	                      subBar.disableCommand(ASPCommandBar.NEWROW);
	                      subBar.disableCommand(ASPCommandBar.DUPLICATEROW);
	                  }
	               } catch (Exception e) {
	                  // ignore
	               }
	            }
	         }
	      } else {
	         // TODO ...
	      }
	   }
	   
	   private void controlIfsField(){
	      ASPManager mgr = getASPManager();
	      String workId = mgr.readValue("_WF_HZ_WORKID_");
	      String curNodeId = mgr.readValue("_WF_HZ_CURNODEID_");
	      String strUserType = mgr.readValue("_WF_HZ_USER_TYPE_");
	      String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
          if (FROM_NAVIGATOR.equals(fromPage)) {
             return;
          }
          
          
          mandatoryHintStr += mgr.translate("HZASPPAGEPROVIDERWFFIELDCANNOTBENULL: According process definition, the following fields cannot be null:");
          
	      IWorkflowCommon workflowCommon = new  WorkflowCommonImpl();
	      Map<String, Map<String, String>>  allViewFieldControlMap = workflowCommon.getFieldAuth(workId, curNodeId, Integer.valueOf(strUserType).intValue());
	      
		  Vector<ASPBlock> allBlocks = getASPBlocks();
		   
	       for (Iterator<ASPBlock> iterator = allBlocks.iterator(); iterator.hasNext();) {
              ASPBlock blk = iterator.next();
              ASPRowSet set = blk.getASPRowSet();
              ASPBlockLayout layout = blk.getASPBlockLayout();
              String viewName= blk.getView();
              
              if(mgr.isEmpty(viewName)){
                 continue;
              }
              
              Map<String, String> viewFieldControlMap = allViewFieldControlMap.get(viewName.toLowerCase());
              if(viewFieldControlMap == null || viewFieldControlMap.isEmpty()){
                 continue;
              }
              
              ASPField[] aspFields =  blk.getFields();
              ASPField tempAspField = null;
              String tempAspFieldDbName = null;
              String tempAspFieldName = null;
              String tempAspFieldValue = null;
              String[] tempAspFieldValues = null;
              for (int i = 0; i < aspFields.length; i++) {
                 tempAspField = aspFields[i];
                 tempAspFieldDbName = tempAspField.getDbName();//OBJID,OBJVERSION
                 if("OBJID".equals(tempAspFieldDbName) || "OBJVERSION".equals(tempAspFieldDbName)){
                   continue;
                 }
                 tempAspFieldName = tempAspField.getName();
                 String tmpControlFlag = viewFieldControlMap.get(tempAspFieldDbName);
                 
                 if(false == mgr.isEmpty(tmpControlFlag)){
                    if(tmpControlFlag.contains(FIELD_CONTROL_READ)){
                       tempAspField.setReadOnly();
                    }
                    if(tmpControlFlag.contains(FIELD_CONTROL_EDIT)){
                       tempAspField.unsetReadOnly();
                    }
                    if(tmpControlFlag.contains("MUSTINPUT")){
                       if(layout.isEditLayout() || layout.isNewLayout()){
                          tempAspField.unsetReadOnly();
                          tempAspField.setMandatory();// only 
                          canSubmitWorkFlow = false;
                          mandatoryHintStr = mgr.translate("HZASPPAGEPROVIDERWFSAVEBIZCONTENTENTS: Please Save business form first.");
                       }else{
                          if(blk == headblk){
                             tempAspFieldValue = set.getValue(tempAspFieldDbName);
                             if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug("&&&:" + tempAspFieldValues);
                             if(mgr.isEmpty(tempAspFieldValue)){
                                mandatoryHintStr += tempAspField.getLabel() + ";";
                                canSubmitWorkFlow = false;
                             }
                          }else{
                             //no-OP; cause
                          }
                       }
                    }
                    if(tmpControlFlag.contains(FIELD_CONTROL_HIDDEN)){
                       tempAspField.setHidden();
                    }
                    if(tmpControlFlag.contains(FIELD_CONTROL_OTHER)){
                       //NO-OP
                    }
                 } else {
                    continue;
                 }
                 
            }
           }
	   }
	   static final String nodeFunctionSql = "SELECT T.command_id COMMAND_ID, T.enabled ENABLED FROM HZ_NODE_FUNC T WHERE T.PROCESS_KEY in (select b.flowid from tw_horizon_instance b where b.id=?) " +
	   		"AND T.PROCESS_NODE=? AND T.PAGE_PATH IN ( SELECT C.biz_page_path FROM HZ_BIZ_WF_CONN C WHERE C.process_id=?)";
	   
	   private void controlIfsCommand(){
		   ASPManager mgr = getASPManager();
		   String workId = mgr.readValue("_WF_HZ_WORKID_");
		   String curNodeId = mgr.readValue("_WF_HZ_CURNODEID_");
		   String strUserType = mgr.readValue("_WF_HZ_USER_TYPE_");
		   String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
		   if (FROM_NAVIGATOR.equals(fromPage)) {
			   return;
		   }
		   List<String> lstConditions = new ArrayList<String>();
		   lstConditions.add(workId);
		   lstConditions.add(curNodeId);
		   lstConditions.add(workId);
		   List<Map> dataTypeMapList = Access.getMultiMap(nodeFunctionSql, lstConditions);  
		   Vector allCustomCommand = headbar.getAllCustomCommandsName();
		   for (Iterator iterator = dataTypeMapList.iterator(); iterator.hasNext();) {
			   Map tempMap = (Map) iterator.next();
			   String commandId = (String)tempMap.get("command_id");
			   String enabled = (String)tempMap.get("enabled");
			   if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug(commandId + "--------" + enabled);
			   try{
				   if(allCustomCommand.contains(commandId)){
					   if("TRUE".equals(enabled)){
						   headbar.enableCustomCommand(commandId);
					   }else{
						   headbar.disableCustomCommand(commandId);
					   }
				   }else{//standard command
					   if("TRUE".equals(enabled)){
						   headbar.enableCommand(commandId);
					   }else{
						   headbar.disableCommand(commandId);
					   }
				   }
			   }catch(Throwable th){
				   if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug(th.getMessage());
			   }
		   }
	   }
	   
	   
	   protected String sepecifyNextNode() throws FndException{//Add some Js Code Here to specify next nodeId(s).
	      return null;
	   }
	   
	   private void setWorkflowVariables(){
	      ASPManager mgr = getASPManager();
          String workId = mgr.readValue("_WF_HZ_WORKID_");
          String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
          if (FROM_NAVIGATOR.equals(fromPage)) {
             return;
          }
          
          IWorkflowCommon workflowCommon = new  WorkflowCommonImpl();
          List<FlowFieldVar>  flowVarList = workflowCommon.getFlowFieldValue(workId);
          
          if(flowVarList == null){
             return;
          }
          
          for (Iterator iterator = flowVarList.iterator(); iterator.hasNext();) {
           FlowFieldVar flowFieldVar = (FlowFieldVar) iterator.next();
           String tempViewName = flowFieldVar.getTablename() == null || "".equals(flowFieldVar.getTablename()) ? flowFieldVar.getTablename() : headblk.getView();
           
           String varName = flowFieldVar.getArguName();
           Vector<ASPBlock> allBlocks = getASPBlocks();
           for (Iterator<ASPBlock> iterator2 = allBlocks.iterator(); iterator2.hasNext();) {
              ASPBlock blk = iterator2.next();
              ASPRowSet set = blk.getASPRowSet();
              ASPBlockLayout layout = blk.getASPBlockLayout();
              String viewName= blk.getView();
              if(mgr.isEmpty(viewName)){
                 continue;
              }else{
                 if(tempViewName.equals(blk.getView())){
                    String tempValue = set.getValue(flowFieldVar.getFieldName());
                    if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug(tempValue);
                    flowFieldVar.setArguValue(tempValue) ;
                 }else{
                    continue;
                 }
              }
           }
          }
          workflowCommon.setFlowFieldValue(workId, flowVarList);
	   }

	   protected void printContents() throws FndException {
	      if (DEBUG)
	         debug(this + ": AbstractASPPageProviderWf.printContents()");
	      ASPManager mgr = getASPManager();
	     
	      printHiddenField(FROM_FLAG, mgr.readValue(FROM_FLAG));
	      printHiddenField("_WF_HZ_WORKID_", mgr.readValue("_WF_HZ_WORKID_")); 
	      printHiddenField("_WF_HZ_CURNODEID_", mgr.readValue("_WF_HZ_CURNODEID_")); 
	      printHiddenField("_WF_HZ_USER_TYPE_", mgr.readValue("_WF_HZ_USER_TYPE_")); 
	      printHiddenField("_WF_HZ_HEADBLK_NAME_", headblk.getName());
	      printHiddenField("_WF_HZ_FIELD_CONTROL_FLAG_","" + canSubmitWorkFlow);
	      printHiddenField("_WF_HZ_FIELD_HINTSTR_","" + mandatoryHintStr);
	      printHiddenField("_WF_USERTASKID_", mgr.readValue("_WF_USERTASKID_"));
	      
	      
	      
	      if(FROM_SEND_VIEW.equals(mgr.readValue(FROM_FLAG))){
	         printHiddenField(PARA_SEND_OBJID, mgr.readValue(PARA_SEND_OBJID));
	         printHiddenField(PARA_SEND_OBJVERSION, mgr.readValue(PARA_SEND_OBJVERSION));
	      }
	      
	      if("AbortSucceed".equals(resultFlag) || "SendViewFinSucceed".equals(resultFlag)){
	         appendDirtyJavaScript("alert('" + mgr.translate("ABSTRACTASPPAGEPROVIDERWFALERTOPERATESUCCEED: Operate succeed.") + "');");
	         appendDirtyJavaScript("if( parent!=null)\n");
	         appendDirtyJavaScript("{\n");
	         appendDirtyJavaScript("   if(parent.opener!=null)\n");
	         appendDirtyJavaScript("   {\n");
	         appendDirtyJavaScript("    parent.opener.submit();");
	         appendDirtyJavaScript("   }\n");
	         appendDirtyJavaScript("parent.close();");    
	         appendDirtyJavaScript("}\n");
	      }
	      
	      appendDirtyJavaScript("function openFullWindow(url){ \n");
	      appendDirtyJavaScript(" var h=screen.availHeight-35;\n");
	      appendDirtyJavaScript(" var w=screen.availWidth-5;\n");
	      appendDirtyJavaScript(" var vars=\"top=0,left=0,height=\"+h+\",width=\"+w+\",status=no,toolbar=no,menubar=no,location=no,resizable=1,scrollbars=1\";\n");
	      appendDirtyJavaScript(" var win=window.open(url,\"\",vars,true);\n");
	      appendDirtyJavaScript("return win; \n");
	      appendDirtyJavaScript("} \n");
	      
	      
	      appendDirtyJavaScript(" function wf_check_edit_layout(){  \n");
	      if(headlay.isEditLayout()){
	         appendDirtyJavaScript("  alert('"+mgr.translate("HZASPPAGEPROVIDERSAVEBEFORESUBMIT: You must save business page, before submit.")+"'); \n");
	         appendDirtyJavaScript("  return false;  \n");
	      }else{
	         appendDirtyJavaScript("  return true;  \n");
	      }
	      appendDirtyJavaScript("  }  \n");
	      
	      appendDirtyJavaScript(" function wf_check_mandatory_field(){  \n");
	      appendDirtyJavaScript("      var tempField=  eval('f._WF_HZ_FIELD_CONTROL_FLAG_');");
	      appendDirtyJavaScript("      var tempstr = tempField.value;  \n");
	      appendDirtyJavaScript("      if('false' == tempstr){ \n");
	      appendDirtyJavaScript("          var tempFielda=  eval('f._WF_HZ_FIELD_HINTSTR_');");
	      appendDirtyJavaScript("          var tempstra = tempFielda.value;  \n");
	      appendDirtyJavaScript("              alert('' + tempstra);\n");
	      appendDirtyJavaScript("              return false;\n");
	      appendDirtyJavaScript("            } \n");
	      appendDirtyJavaScript("  return true;  \n");
	      appendDirtyJavaScript("  }  \n");
	      
	      jsSpecifyNextNodes(sepecifyNextNode());
	      appendJsWfCheckCustomLogic();
	   }
	   
	   protected void appendJsWfCheckCustomLogic() throws FndException{
	        appendDirtyJavaScript(" function wf_check_custom_logic(){  \n");
	        appendDirtyJavaScript("  return true;  \n");
	        appendDirtyJavaScript("  }  \n");
	   }
	   
	   
	   protected void jsSpecifyNextNodes(String nextNodes) throws FndException{
	      if(Str.isEmpty(nextNodes)){
	         nextNodes = "";
	      }
	      if ( DEBUG ) ifs.fnd.os.NativeUtilities.debug("----ifs.hzwflw.HzASPPageProviderWf.jsSpecifyNextNodes(String): " + nextNodes);
	      appendDirtyJavaScript(" if(parent && parent.document){\n");
	      appendDirtyJavaScript("    var nextNodeFieldCtl_ = parent.document.getElementById('nextNodeID');\n");
	      appendDirtyJavaScript("    var specifiedNextNodeIdCtl_ = parent.document.getElementById('specifiedNextNodeId');\n");
	      appendDirtyJavaScript("    if(nextNodeFieldCtl_ && specifiedNextNodeIdCtl_){\n");
	      appendDirtyJavaScript("       nextNodeFieldCtl_.value = '" + nextNodes + "';\n");//Line2~Node2
	      appendDirtyJavaScript("       specifiedNextNodeIdCtl_.value = '" + nextNodes + "';\n");//Line2~Node2
	      appendDirtyJavaScript("    }\n");
	      appendDirtyJavaScript(" }\n");
	      
	   }
	   

	   private void insertBizProcessId(BizWfConnVO bizWfConnVO){
	        ASPManager mgr = getASPManager();
	        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	        ASPCommand cmd = trans.addCustomCommand("CONFIG","HZ_BIZ_WF_CONN_API.New_BIZ_CONN");
	        cmd.addParameter("BIZ_LU", "S", "IN", bizWfConnVO.getBizLuName());
	        cmd.addParameter("BIZ_VIEW", "S", "IN", bizWfConnVO.getBizViewName());
	        cmd.addParameter("BIZ_OBJID", "S", "IN", bizWfConnVO.getLuKeyValuePairs());
	        cmd.addParameter("BIZ_PAGE_PATH", "S", "IN",bizWfConnVO.getBizPagePath());
	        cmd.addParameter("PROCESS_ID", "S", "IN", bizWfConnVO.getProcessId());
	        cmd.addParameter("PROCESS_NAME", "S", "IN", bizWfConnVO.getProcessName());
	        cmd.addParameter("PROCESS_KEY", "S", "IN", bizWfConnVO.getProcessKey());
	        trans = mgr.validate(trans);
	        trans.clear();
	   }
	   
      private void deleteBizProcessId(BizWfConnVO bizWfConnVO){
         ASPManager mgr = getASPManager();
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPCommand cmd = trans.addCustomCommand("CONFIG","HZ_BIZ_WF_CONN_API.Drop_Biz_Wf_Conn");
         cmd.addParameter("BIZ_LU", "S", "IN", bizWfConnVO.getBizLuName());
         cmd.addParameter("BIZ_VIEW", "S", "IN", bizWfConnVO.getBizViewName());
         cmd.addParameter("BIZ_OBJID", "S", "IN", bizWfConnVO.getLuKeyValuePairs());        
         trans = mgr.validate(trans);
         trans.clear();
    }
	  
	  
	   public String getCurrentBizProcessId() {
	      ASPManager mgr = getASPManager();
	      String luName = headblk.getLUName();//
	      
	      String view = headblk.getView();//
	      String objid  = headset.getValue("OBJID");
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      
	      ASPCommand  cmd = trans.addCustomCommand("KEYREF", " client_sys.get_key_reference");
	      cmd.addParameter("KEYREF", "S", "OUT", null);
	      cmd.addParameter("BIZ_LU", "S", "IN", luName);
	      cmd.addParameter("BIZ_OBJID", "S", "IN", objid);
	      
	      trans = mgr.validate(trans);
	      String keyReference = trans.getValue("KEYREF/DATA/KEYREF");
	      
	      if(keyReference.endsWith("^")){
	      	keyReference = keyReference.substring(0, keyReference.length() - 1);
	      }
	      
	      trans.clear();
	      
	      ASPCommand cmd2 = trans.addCustomFunction("CONFIG","HZ_BIZ_WF_CONN_API.Get_Process_Id", "OBJID");
	      cmd2.addParameter("BIZ_LU", "S", "IN", luName);
	      cmd2.addParameter("BIZ_VIEW", "S", "IN", view);
	      cmd2.addParameter("BIZ_OBJID", "S", "IN", keyReference);
	      
	      trans = mgr.validate(trans);
	      String retvalue = trans.getValue("CONFIG/DATA/OBJID");
	      
	      return retvalue;
	   }
	   
	   	
	//------------util method
	   protected abstract ASPBlock getBizWfBlock();

	   
	   private BizWfConnVO prepareBizConnVo() {
	      ASPManager mgr = getASPManager();
	      String luName = headblk.getLUName();
	      String viewName = headblk.getView();
	      String bizUrl = mgr.getURL();
	      String bizKeyrefPair = null;//mgr.readValue("__LU_KEY_VALUE_PAIR"), this way can not be counted on.
          String objid  = headset.getValue("OBJID");
          
          ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
          
          ASPCommand  cmd = trans.addCustomCommand("KEYREF", " client_sys.get_key_reference");
          cmd.addParameter("KEYREF", "S", "OUT", null);
          cmd.addParameter("BIZ_LU", "S", "IN", luName);
          cmd.addParameter("BIZ_OBJID", "S", "IN", objid);
          
          trans = mgr.validate(trans);
          String keyReference = trans.getValue("KEYREF/DATA/KEYREF");
          
          if(keyReference.endsWith("^")){
            keyReference = keyReference.substring(0, keyReference.length() - 1);
          }
          trans.clear();
          bizKeyrefPair = keyReference;
	      BizWfConnVO bizWfConnVo = new BizWfConnVO(luName, viewName,bizKeyrefPair, bizUrl, null, null);
	      return bizWfConnVo;
	   }
	   
	 private boolean validateInitAuth(String processKey, String userId){
	    ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         
         ASPCommand  cmd = trans.addCustomFunction("KEYREF", " HZ_WF_PROCESS_DEF_API.VALIDATE_INIT_AUTH","OBJID");
         cmd.addParameter("KEYREF", "S", "IN", processKey);
         cmd.addParameter("KEYREF", "S", "IN", userId);
         
         trans = mgr.validate(trans);
         String retvalue = trans.getValue("KEYREF/DATA/OBJID");
         
         if("TRUE".equals(retvalue)){
            return true;
         }
	    return false;
	 }
	 
	 private boolean filterPageProcesses(String conditionStr) {//HEAD_NAME;||
	    
	    if(null == conditionStr || "".endsWith(conditionStr)){
	       return true;
	    }
	    String[] orConditionItems = conditionStr.split("\\|\\|");
	    String orConditionItem = null;
	    boolean orResult = false;
	    for (int i = 0; i < orConditionItems.length; i++) {
	       orConditionItem = orConditionItems[i];
	       String[] andConditionItems = orConditionItem.split("\\&\\&");
	       
	       boolean andResult = true;
	       for (int j = 0; j < andConditionItems.length; j++) {
            String andConditionItem = andConditionItems[j];
            String[] keyValuePair = andConditionItem.split("\\=");
            
            String actualValue = headset.getValue(keyValuePair[0]);
            if(false == keyValuePair[1].equals(actualValue)){
               andResult = false;
               break;
            }
         }
	      
	      if(andResult == true){
	         orResult = true;
	         break;
	      } else{
	         continue;
	      }
      }
	    return orResult;
	 }
	 
	 private  List<String[]> getPageProcesses(String url) {
	    ASPManager mgr = getASPManager();
	      List<String[]> processList = new ArrayList<String[]>();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      String sql = "SELECT T.PROCESS_KEY PROCESS_KEY,T.CONDITION_EXPRESSION CONDITION_EXPRESSION FROM HZ_BIZ_WF_CONFIG T WHERE T.PAGE_PATH = '" + url + "'";
	      trans.addQuery("BPROCESSNAMES", sql);
	      
	      trans = mgr.perform(trans);
	      
	      ASPBuffer buffer = trans.getBuffer("BPROCESSNAMES");
	      
	      int count = buffer.countItems();
	      
	      String[] tempStrArray = null;
	      for (int i = 0; i < count; i++)
	      {
	         ASPBuffer dataBuffer = buffer.getBuffer("DATA");
	         
	         if("DATA".equals(buffer.getNameAt(i)))
	         {
	            tempStrArray = new String[2];
	            tempStrArray[0] = buffer.getBufferAt(i).getValue("PROCESS_KEY");
	            tempStrArray[1] = buffer.getBufferAt(i).getValue("CONDITION_EXPRESSION");
	            if(filterPageProcesses( tempStrArray[1])){
	               processList.add(tempStrArray);
	            }
	         }
	      }
	      return processList;
	   }
	 
	 protected boolean workflowEnded(){
	    boolean ret = false;
	    if (headset.countRows() == 0) return ret;
	    if (Str.isEmpty(getCurrentBizProcessId())) return ret;
	    
	    String sql = "select count(1) pcount from TD_HORIZON_USER t where t.status='1' and t.dataid= ?";
	    List conditionList = new ArrayList();
	    conditionList.add(null);
	    Map retMap = Access.getSingleMap(sql, conditionList);
	    BigDecimal bd = (BigDecimal)retMap.get("pcount");
	    
	    if(bd.intValue() == 0){
	       ret = true;
	    }
	    return ret;
	 }
	 
	 private void checkFlowStateForDr() throws FndException
	 {
	    ASPManager mgr = getASPManager();
	    ASPContext ctx = mgr.getASPContext();
	    String objid = null;
	    if (headset.countRows() > 0 && (headlay.isSingleLayout() || headlay.isCustomLayout()))
	    {
	       if (workflowEnded())
	       {
	          objid = headset.getValue("OBJID");
	       }
	    }
	    ctx.setGlobalObject(prefix_wf_ob_dr + headblk.getView(), objid);
	    checkCldFlowStateForDr(headblk, (mgr.isEmpty(objid) ? true : false));
	 }
	 
	 private void checkCldFlowStateForDr(ASPBlock blk, boolean clear_session)
	 {
	    ASPManager mgr = getASPManager();
	    ASPContext ctx = mgr.getASPContext();
	    Vector blocks = getASPBlocks();
	    for (int i = 0; i < blocks.size(); i++)
	    {
	       ASPBlock child_blk = (ASPBlock)blocks.get(i);
	       if (child_blk.getMasterBlock() == blk)
	       {
	          if (clear_session)
	             ctx.setGlobalObject(prefix_wf_ob_dr + child_blk.getView(), null);
	          else
	             ctx.setGlobalObject(prefix_wf_ob_dr + child_blk.getView(), "ALL");
	          
	          checkCldFlowStateForDr(child_blk, clear_session);
	       }
	    }
	 }
}
