package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.hzwflw.util.BizWfConnVO;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.HzWfUtil;
import ifs.hzwflw.util.URL;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;

import com.horizon.workflow.flowengine.api.XMLWorkFace;
import com.horizon.workflow.flowengine.api.XMLWorkflow;
import com.horizon.workflow.flowengine.pub.StaticVar;
import com.horizon.workflow.flowengine.pub.XMLWork;

public class StartProcess extends ASPPageProvider implements HzConstants {
   public StartProcess(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPManager mgr;
   private ASPContext ctx;
   private ASPTransactionBuffer trans;

   private String bizLuName = null;
   private String bizViewName = null;
   private String bizObjid = null;
   private String bizPagePath = null;
   private String processTopic = null;
   
   private String processKeyStr = null;
   
   BizWfConnVO bizWfConnVO = null;
   
   String user_id = null;
   
   String workId = null;
   boolean startSuccessFlag = false;

   List<String> pageProcessNameList = null;

   public void run() throws FndException {
      mgr = getASPManager();
      
      ctx = mgr.getASPContext();
      
      bizLuName = mgr.readValue(PARA_BIZ_LU_NAME);
      bizViewName = mgr.readValue(PARA_BIZ_VIEW_NAME);
      bizObjid = mgr.readValue(PARA_BIZ_KEY_REF_PAIR);
     
      bizPagePath = mgr.readValue(PARA_BIZ_URL);
      
      try {
         String tmpTopic = mgr.getQueryStringValue(PARA_PROCESS_TOPIC);
         String tmpProcessKeys  = mgr.getQueryStringValue(PARA_PROCESS_KEY_STR);
         if(tmpTopic != null){
            processTopic = java.net.URLDecoder.decode(tmpTopic, "UTF-8");
            processKeyStr = java.net.URLDecoder.decode(tmpProcessKeys, "UTF-8");
         }
      } catch (UnsupportedEncodingException e1) {
        // ignore
      }
      
      if(null == mgr.getQueryStringValue(PARA_BIZ_LU_NAME)){
         processTopic = mgr.readValue("hiddenProcessTopic"); 
      }
      if(null == mgr.getQueryStringValue(PARA_PROCESS_KEY_STR)){
         processKeyStr = mgr.readValue("hiddenProcessKeyStr"); 
      }
      
      bizWfConnVO = new BizWfConnVO(bizLuName, bizViewName, bizObjid, bizPagePath, null, null);
      
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PAGE_PATH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROCESS_NAME")) )
         okFind();
      else  if (mgr.buttonPressed("CONFIRMSTART"))
         startProcess();
      else 
         okFind();
      adjust();
   }
   
   
   

   
   
	public void startProcess() {
      ASPManager mgr = getASPManager();
      headset.store();
      ASPBuffer selected_buf = headset.getSelectedRows("PROCESS_KEY");
      int tmpCount = selected_buf.countItems();
      if (tmpCount == 0) {
         mgr.showAlert("STARTPROCESSNOITEMCANBESTARTED: No Process can be started.");
         okFind();
         return;
      }
      if (tmpCount > 1) {
         mgr.showAlert("STARTPROCESSCANONLYCHOOSEONE: Only one process can be choosen.");
         okFind();
         return;
      }
      String processKey = selected_buf.getBufferAt(0).getValueAt(0);
      String userid = HzWfUtil.getOracleUserId(mgr).toUpperCase();
      String topic = processTopic;
      
      boolean initAuth = validateInitAuth(processKey, userid);
      if(false == initAuth){ 
         mgr.showAlert("ABSTRACTASPPAGEPROVIDERNORIGHTTOINITTHEPROCESS: You do not have enough right to start the process!");
         return;
      }
      //
      String bizUrl = bizWfConnVO.getBizPagePath() + "?" + bizWfConnVO.getLuKeyValuePairs().replaceAll("\\^", "\\&");
      URL bizKey = new URL(bizUrl);
      String deptname = "huizheng";// TODO XXXX
      XMLWorkflow xwork = new XMLWorkflow(userid, userid, deptname);
      xwork.setDataIdentifier("system");
      xwork.setFlowIdentifier("system");
      int result = xwork.createWork(processKey);
      if (StaticVar.Init_Success == result) {
         XMLWorkFace xmlWorkFace = new XMLWorkFace();
         xmlWorkFace.setWorkid(xwork.getWork().getWorkID());
         xmlWorkFace.setMsgSendFlag("Todo");
         xmlWorkFace.setNodeid(xwork.getWork().getCurNodeID());
         xmlWorkFace.setTrackid(xwork.getWork().getCurTrackID());
         xmlWorkFace.setUserid(userid);
         xmlWorkFace.setUsername(ctx.findGlobal("HZ_SESSION_USER_NAME"));
         xmlWorkFace.setDataIdentifier("system");
         xmlWorkFace.setFlowIdentifier("system");
         LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
         map.put("workid", xwork.getWork().getWorkID());
         map.put("dataid", bizKey.toString());
         map.put("FORMID", xwork.getWork().getCurForm()[0].getID());

         map.put(xwork.getWork().getCurForm()[0].getID() + "_ID", bizKey.toString());
         map.put(xwork.getWork().getCurForm()[0].getID() + "_TITLE", topic);//
         xmlWorkFace.setClassForGetAppSQL("com.horizon.workflow.flowengine.impl.example.AppDataSave");
         xmlWorkFace.setOtherPara(map);
         result = xmlWorkFace.Action_Submit();
         if (StaticVar.F_STATUS_Success == result) {
            bizWfConnVO.setProcessId(xwork.getWork().getWorkID());
            bizWfConnVO.setProcessName(xwork.getWork().getFlowName());
            try {
               insertBizProcessId(bizWfConnVO);
            } finally {
               xwork.getWork();
               boolean isCLose = XMLWork.exitWindow( xwork.getWork().getWorkID(), xwork.getWork().getCurNodeID(),
                     xwork.getWork().getCurTrackID(), xwork.getWork().getCurUserID());
               System.out.println(isCLose + "===========================EXIT HUIZHENG PROCESS.");
            }
         }
         
//         URL todoUrl = new URL(TODOUrl);
//         todoUrl.addParameters("ToDo_Workid", xwork.getWork().getWorkID());
//         appendDirtyJavaScript("showNewBrowser_('" + todoUrl + "', (window.screen.availWidth-10), (window.screen.availHeight-30), 'NO'); \n");
//     
//         
         
         startSuccessFlag = true;
         workId = xwork.getWork().getWorkID();
      } else if (StaticVar.Init_NoRole == result) {
         mgr.showAlert(xwork.getBackMsg());
      } else {
         mgr.showAlert(xwork.getBackMsg());
         boolean isCLose = XMLWork.exitWindow( xwork.getWork().getWorkID(), xwork.getWork().getCurNodeID(),
               xwork.getWork().getCurTrackID(), xwork.getWork().getCurUserID());
         System.out.println(isCLose + "===========================EXIT HUIZHENG PROCESS.");
      }
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
        trans = mgr.validate(trans);
        trans.clear();
   }
   
   public String getProcessName(){
      return mgr.readValue("SELECTED_PROCESS");
   }
   
   public String getProcessTopic(){
      return mgr.readValue("PROCESSTOPIC");
   }
   
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
      
      q.addWhereCondition("page_path= '" + bizPagePath + "'");
      
      setProcessKeyCondition(q);
      
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("BIZWFCONFIGNODATA: No data found.");
         headset.clear();
      }
   }
   
   
   public void setProcessKeyCondition(ASPQuery q) {
      if (mgr.isEmpty(processKeyStr)) {
         return;
      }
      String[] processKeys = processKeyStr.split("\\|\\|");
      if (null == processKeys || processKeys.length == 0) {
         return;
      } else {
         StringBuilder sb = new StringBuilder("PROCESS_KEY IN (");
         for (int i = 0; i < processKeys.length; i++) {

            sb.append(" '").append(processKeys[i]).append("' ,");
         }

         sb.append(" ''");
         sb.append(")");
         q.addWhereCondition(sb.toString());
      }
   }
   
   public void preDefine() {
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PAGE_PATH").
              setMandatory().
              setInsertable().
              setLabel("STARTPROCESSPAGEPATH: Page Path").
              setHidden().
              setSize(50);
      headblk.addField("PROCESS_KEY").
			      setInsertable().
			      setLabel("STARTPROCESSPROCESSNAME: Process Key").
			      setSize(50);
      headblk.addField("PROCESS_NAME").
              setInsertable().
              setLabel("STARTPROCESSPROCESSNAME: Process Name").
              setSize(50);
      headblk.addField("REMARK").
              setInsertable().
              setLabel("STARTPROCESSREMARK: Remark").
              setHidden().
              setSize(50);
      headblk.setView("HZ_BIZ_WF_CONFIG");
      headblk.setTitle("STARTPROCESSHEADBLKTITLE: Initiate Items");
//      headblk.defineCommand("BIZ_WF_CONFIG_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("STARTPROCESSTBLHEAD: Biz Wf Configs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headtbl.disableSelectionOption();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(1);
   }

   protected String getDescription() {
      return "STARTPROCESSDESC: Start Process";
   }

   protected String getTitle() {
      return getDescription();
   }

   protected void printContents() throws FndException {
      printHiddenField(PARA_BIZ_LU_NAME, bizLuName);
      printHiddenField(PARA_BIZ_VIEW_NAME, bizViewName);
      printHiddenField(PARA_BIZ_KEY_REF_PAIR, bizObjid);
      printHiddenField(PARA_BIZ_URL, bizPagePath);
      
      printHiddenField("hiddenProcessTopic", processTopic);
      printHiddenField("hiddenProcessKeyStr", processKeyStr);
      
      ASPManager mgr = getASPManager();
      if (startSuccessFlag) {
         appendDirtyJavaScript("if(opener != null){\n");
//         appendDirtyJavaScript("opener.commandSet('MAIN.xxx','');\n");
//         appendDirtyJavaScript("opener.history.go(-1);\n");
//         appendDirtyJavaScript("opener.location.reload();\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("window.close();\n");
         
         URL todoUrl = new URL(TODOUrl);
         todoUrl.addParameters("ToDo_Workid", workId);
         appendDirtyJavaScript("showNewBrowser_('" + todoUrl + "', (window.screen.availWidth-10), (window.screen.availHeight-30), 'NO'); \n");
         return;
      }

      beginDataPresentation();
      appendToHTML(headlay.show());
      endDataPresentation();
      beginDataPresentation();
      if(ASPBlockLayout.MULTIROW_LAYOUT == headlay.getLayoutMode()){
         printNewLine();
         printSpaces(2);
         printReadLabel("STARTPROCESSPROCESSTOPIC: ProcessTopic");
         printTextArea("processTopic", processTopic, " onchange='setHiddenProcessTopic(this.value);' ",4, 50);
      }
 
      endDataPresentation();
      beginDataPresentation();
      if(ASPBlockLayout.MULTIROW_LAYOUT == headlay.getLayoutMode()){
         printSpaces(2);
         printSubmitButton("CONFIRMSTART", mgr.translate("STARTPROCESSCONFIRMSTART: Confirm"), "");
         printSpaces(4);
         printSubmitButton("CANCEL", mgr.translate("STARTPROCESSWFCANCEL: Cancel"),
               "OnClick='javascript:window.close();'"); 
      }

      endDataPresentation();

      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  window.close();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("document.getElementsByName('form')[0].firstChild.onclick=function(){\n");
      appendDirtyJavaScript(" var frameset = window.parent.document.getElementsByName('main')[0];\n");
      appendDirtyJavaScript(" if(window.parent.col_state == 0) {\n");
      appendDirtyJavaScript("    frameset.cols = '90%,*';\n");
      appendDirtyJavaScript("    window.parent.col_state = 1;\n");
      appendDirtyJavaScript(" } else  if (window.parent.col_state == 1) {\n");
      appendDirtyJavaScript("    frameset.cols = '60%,*';\n");
      appendDirtyJavaScript("    window.parent.col_state = 0;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("};\n");
      
    
      
      //select only one row
      appendDirtyJavaScript("  function rowClicked(row_no,table_id,elm,box,i){\n");
      appendDirtyJavaScript("deselectAllRows('__SELECTED1');\n");
      appendDirtyJavaScript("    cca = false;\n");////
      appendDirtyJavaScript(" if (!cca) {\n");
      appendDirtyJavaScript("    if (box != null) {\n");
      appendDirtyJavaScript("       if (box.length > 1)\n");
      appendDirtyJavaScript("          row = box[i];\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("          row = box;\n");
      appendDirtyJavaScript("       row.checked = true;\n");
      appendDirtyJavaScript("       CCA(row, row_no);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("    cca = false;\n");
      appendDirtyJavaScript("}\n");
      
      //save topic to hidden field.
      appendDirtyJavaScript("  function setHiddenProcessTopic(obj){\n");
      appendDirtyJavaScript("window.document.form.hiddenProcessTopic.value = obj;\n");
      appendDirtyJavaScript("}\n");
   }
   private boolean validateInitAuth(String processKey, String userId){
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
   
   public void  adjust()
   {
      headbar.disableCommand(ASPCommandBar.VIEWDETAILS);
   }
}
