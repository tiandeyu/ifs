package ifs.hzwflw;

import com.horizon.db.Access;

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
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.HzWfUtil;


public class HzTodoWorkbench extends ASPPageProvider implements HzConstants{

	public HzTodoWorkbench(ASPManager mgr, String pagePath) {
		super(mgr, pagePath);
	}
	
	public static boolean DEBUG = Util.isDebugEnabled(HzTodoWorkbench.class.getCanonicalName());
	private ASPManager mgr ;
	
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	
	private ASPTabContainer tabs;

	// -----------------------------------------------------------------------------
	// ---------- Item Instances created on page creation --------------------------
	// -----------------------------------------------------------------------------

	private ASPBlock todo_blk;
	private ASPRowSet todo_set;
	private ASPCommandBar todo_bar;
	private ASPTable todo_tbl;
	private ASPBlockLayout todo_lay;
	
	private ASPBlock toread_blk;
   private ASPRowSet toread_set;
   private ASPCommandBar toread_bar;
   private ASPTable toread_tbl;
   private ASPBlockLayout toread_lay;
	
	private String topic;
	private String processName;
	private String startDate;
	private String endDate;
	
	
	private String todoSize;
	private String toReadSize;
	
   protected ASPTransactionBuffer trans;
   private ASPBuffer keys;
   private ASPBuffer data;
   protected ASPBuffer bc_Buffer;
   protected ASPBuffer trans_Buffer;
   private ASPCommand cmd;
   protected ASPQuery q;

	public void run()
	{
		mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
		topic = mgr.readValue("SEARCH_TITLE");
		processName=mgr.readValue("SEARCH_PROCESS_NAME");
		startDate=mgr.readValue("START_DATE");
		endDate=mgr.readValue("END_DATE");
		
		setCountValue();
		
		if (mgr.commandBarActivated()){
			eval(mgr.commandBarFunction());
		}else if(mgr.buttonPressed("SEARCH"))
			 setSearchCondition();
		else 
			findAll();
		adjust();
		tabs.saveActiveTab();
	}
	
 
	private void track(String processId, String processName, String processTopic, String bizUrl, String bizObjid, String itemId, String trackNode){
	     ASPManager mgr = getASPManager(); 
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPCommand cmd = trans.addCustomCommand("TRACK", "BIZ_WF_TRACK_API.New_Biz_Wf_Track");
	      cmd.addParameter("PROCESS_ID", "S", "IN", processId);
	      cmd.addParameter("PROCESS_NAME", "S", "IN", processName);
	      cmd.addParameter("PROCESS_TOPIC", "S", "IN", processTopic);
	      cmd.addParameter("TRACK_URL", "S", "IN", bizUrl);
	      cmd.addParameter("BIZ_OBJID", "S", "IN", bizObjid);
	      cmd.addParameter("ITEM_ID", "S", "IN", itemId);
	      cmd.addParameter("TRACK_NODE", "S", "IN", trackNode);
	      trans = mgr.perform(trans);
	}
	

	
	private void setCountValue()
	{
		countFindAll();
	}
	
	private void findAll()
	{
		if(tabs.getActiveTab()==1)
		{
		   okFindTODO();
		}else if(tabs.getActiveTab()==2)
        {
           okFindToRead();
        }
	}
	
	public void setSearchCondition()
	{
		ASPManager mgr = getASPManager();
		topic=mgr.readValue("SEARCH_TITLE");
		processName=mgr.readValue("SEARCH_PROCESS_NAME");
		startDate=mgr.readValue("START_DATE");
		endDate=mgr.readValue("END_DATE");

		if(tabs.getActiveTab()==1)
		{
		   okFindTODO();
		}
		else if(tabs.getActiveTab()==2)
      {
         okFindToRead();
      }
	}
	
	
	private String setSqlCondition(String sql)
	{
		ASPManager mgr = getASPManager();
		if(!mgr.isEmpty(topic)){
			sql=sql+" AND TITLE LIKE '%"+topic+"%' ";
		}
		if(!mgr.isEmpty(processName)){
			sql=sql+" AND MODELNAME LIKE '%"+processName+"%' ";
		}
		if(!mgr.isEmpty(startDate)){
			sql=sql+" AND SENDTIME >= TO_DATE('"+startDate+"','yyyy-MM-dd hh24:mi:ss') ";
		}
		if(!mgr.isEmpty(endDate)){
			sql=sql+" AND SENDTIME <= TO_DATE('"+endDate+"','yyyy-MM-dd hh24:mi:ss') ";
		}
		return sql;
	}
	
	
	public void countFindAll()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		String user_id = HzWfUtil.getOracleUserId(mgr).toUpperCase();
		
		if (!mgr.isEmpty(user_id))
		{
			String sql_todo =   "select to_char(count(1)) N from HZ_WF_TASK_VIEW where STATUS='1' and   HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='" + user_id + "'";
			String sql_toRead =   "select to_char(count(1)) N from HZ_WF_TASK_VIEW where STATUS in ('2', '6') and  HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='" + user_id + "'";
			
			if(mgr.buttonPressed("SEARCH"))
			{		
				sql_todo = setSqlCondition(sql_todo);
				sql_toRead = setSqlCondition(sql_toRead);
			}
			q = trans.addQuery("GETCOUNTTODO", sql_todo);
			q = trans.addQuery("GETCOUNTTOREAD", sql_toRead);
			
			trans = mgr.perform(trans);
			todoSize = trans.getValue("GETCOUNTTODO/DATA/N");
			toReadSize = trans.getValue("GETCOUNTTOREAD/DATA/N");
		}
	}
	
	public void okFindTODO()
	{
      ASPManager mgr = getASPManager();
		
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(todo_blk);
		setCondition(q);
//		q.addWhereCondition("upper(REALUSERID) like '%"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
	   q.addWhereCondition(" HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
		q.addWhereCondition("STATUS='1'");
		q.setOrderByClause("SENDTIME DESC");
		q.includeMeta("ALL");
		q.setBufferSize(15);
		trans = mgr.querySubmit(trans, todo_blk);
	}
	public void okFindToRead()
	{
	   ASPManager mgr = getASPManager();
	   
	   ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	   ASPQuery q;
	   
	   q = trans.addQuery(toread_blk);
	   setCondition(q);
	   q.addWhereCondition(" HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
//	   q.addWhereCondition("upper(REALUSERID) like '%"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
	   q.addWhereCondition("STATUS in ('2','6') ");
	   q.setOrderByClause("SENDTIME DESC");
	   q.includeMeta("ALL");
	   q.setBufferSize(15);
	   trans = mgr.querySubmit(trans, toread_blk);
	}
	
	private void setCondition(ASPQuery q)
	{
		ASPManager mgr = getASPManager();
		if(!mgr.isEmpty(topic)){
			q.addWhereCondition("TITLE LIKE '%"+topic+"%'");
		}
		if(!mgr.isEmpty(processName)){
			q.addWhereCondition("MODELNAME LIKE '%"+processName+"%'");
		}
		if(!mgr.isEmpty(startDate)){
			q.addWhereCondition("SENDTIME >= TO_DATE('"+startDate+"','yyyy-MM-dd hh24:mi:ss')");
		}
		if(!mgr.isEmpty(endDate)){
			q.addWhereCondition("SENDTIME <= TO_DATE('"+endDate+"','yyyy-MM-dd hh24:mi:ss')");
		}
	}
	
	
	
	public void batchRead(){
	   ASPManager mgr = getASPManager();
	   
	   
	   toread_set.store();
      StringBuilder userTaskIds = new StringBuilder("UPDATE TD_HORIZON_USER T SET STATUS='4' WHERE T.STATUS IN ('2','6') AND T.ID IN  (");
      trans.clear();
      
      ASPBuffer buff = toread_set.getSelectedRows();
      int countRows = toread_set.countSelectedRows();
      if (countRows == 0) {
         mgr.showAlert(mgr.translate("QUABSCHECKFORMOREC: You must select a record."));
         return;
      } else {
         for (int i = 0; i < countRows; i++) {
            ASPBuffer currRow = buff.getBufferAt(i);
            String userTaskId = currRow.getValue("USERTASKID");
            userTaskIds.append("'" ).append(userTaskId).append( "',");
         }
      }
      
      userTaskIds.append("'---')");
      
//      mgr.showAlert(userTaskIds.toString());
      
      Access.executeUpdate(userTaskIds.toString(), null);
      
      okFindToRead();
      countFindAll();
      
//      ASPCommand cmd = trans.addCustomCommand("CREATETRANS", "QUA_BS_CHECK_NOTICE_API.Create_Check_Notice");
//      cmd.addParameter("FORM_CODE",userTaskIds);
//      cmd.addParameter("NOTICE_CODE",null);
//      
    //  trans = mgr.perform(trans);
      
//      ASPBuffer buffer = trans.getBuffer("CREATETRANS");
//      
//      ASPBuffer dataBuffer = buffer.getBuffer("DATA");
//      
//      String noticeCode = dataBuffer.getValue("NOTICE_CODE");
//      String url = "./QuaBsCheckNotice.page?NOTICE_CODE=" + noticeCode;
//      mgr.redirectTo(url);
	}

	
    public void activateTODO()
    {
	   tabs.setActiveTab(1);
	   okFindTODO();
    }
    public void activateToRead()
    {
       tabs.setActiveTab(2);
       okFindToRead();
    }
   
	public void preDefine()
	{
		ASPManager mgr = getASPManager();
		
		headblk = mgr.newASPBlock("MAIN");
		headblk.addField("SEARCH_TITLE").
		        setFunction("''").
		        setLabel("TODOWORKBENCHSEARCHTITLE: Title").
		        setSize(30);
		headblk.addField("SEARCH_SEND_USER").
		        setFunction("''").
		        setLabel("TODOWORKBENCHSEARCHSENDUSER: Sender").
		        setHidden();
		headblk.addField("SEARCH_PROCESS_NAME").
		        setFunction("''").
		        setLabel("TODOWORKBENCHSEARCHPROCESSNAME: Process Name").
		        setSize(20);
		headblk.addField("SEARCH_TASK_NAME").
		        setFunction("''").
		        setLabel("DAIBANSEARCHTASKNAME: Task Name").
		        setHidden();
		headblk.addField("START_DATE","Date","yyyy-MM-dd").
		        setFunction("''").
		        setLabel("TODOWORKBENCHSEARCHSTARTDATE: Send Time From ").
		        setSize(10);
		headblk.addField("END_DATE","Date","yyyy-MM-dd").
		        setFunction("''").
		        setLabel("TODOWORKBENCHSEARCHENDDATE: To").
		        setSize(10);
		
		headblk.setView("DUAL");      
	    headblk.setTitle("TODOWORKBENCHHEADBLK: Todo Items");
	    headset = headblk.getASPRowSet();
	    headbar = mgr.newASPCommandBar(headblk);
	    headbar.defineCommand(headbar.FIND, "setSearchCondition");
	    headtbl = mgr.newASPTable(headblk);
	    headtbl.setTitle("TODOWORKBENCHTBLHEAD: Todo Items");
	    headtbl.enableRowSelect();
	    headtbl.setWrap();
	    headlay = headblk.getASPBlockLayout();
	    headlay.setDefaultLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);
	    headbar.enableCommand(headbar.FIND);
	    headlay.setEditable();
	    headlay.setDialogColumns(4);
	    
		
	    this.disableHeader();
//	    this.disableBar();
		
		headbar.addCustomCommand("activateTODO", "Todo");
		headbar.addCustomCommand("activateToRead", "ToRead");
		
		
		todo_blk = mgr.newASPBlock("TODO");
	    todo_blk.addField("TODO_TASKID")
	            .setDbName("TASKID")
	            .setSize(30)
	            .setLabel("TODOWORKBENCHTASKID: Task Id")
	            .setHidden();
	    todo_blk.addField("USERTASKID").setHidden();
		todo_blk.addField("TODO_TITLE")
		        .setDbName("TITLE")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHTITLE: Title")
		        .setHyperlink(TODOUrl, "ToDo_Workid,TODO_TRACKID,USERTASKID","NEWWIN");
	    todo_blk.addField("TODO_MODELNAME")
       .setDbName("MODELNAME")
       .setSize(30)
       .setLabel("TODOWORKBENCHMODELNAME: Model Name");
	    todo_blk.addField("TODONODENAME")
	    .setDbName("NODENAME")
	    .setSize(10)
	    .setLabel("TODOWORKBENCHNODENAME: Node Name");
		todo_blk.addField("TODO_SENDUSERID")
		        .setDbName("SENDUSERID")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHSENDUSERID: Send User Id").setHidden();
		
		todo_blk.addField("TODO_SENDTIME","Date")
		        .setDbName("SENDTIME")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHSENDTIME: Send Time");
		todo_blk.addField("TODO_URL")
		        .setDbName("URL")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHURL: Url")
		        .setHidden();
		todo_blk.addField("TODO_USERID")
		        .setDbName("USERID")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHUSERID: User Id")
		        .setHidden();
		todo_blk.addField("TODO_TODOTYPE")
		        .setDbName("TODOTYPE")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHTODOTYPE: Todo Type")
		        .setHidden();

		todo_blk.addField("TODO_IMPORTANCE")
		        .setDbName("IMPORTANCE")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHIMPORTANCE: Importance")
		        .setHidden();
		todo_blk.addField("TODO_TIMELIMIT")
		        .setDbName("TIMELIMIT")
		        .setSize(30) 
		        .setLabel("TODOWORKBENCHTIMELIMIT: Time Limit").setHidden();
		todo_blk.addField("TODO_SENDUSERNAME")
		        .setDbName("SENDUSERNAME")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHSENDUSERNAME: Send User Name");
		todo_blk.addField("TODO_SENDUSERDEPTNAME")
		        .setDbName("SENDUSERDEPTNAME")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHSENDUSERDEPTNAME: Send Department")
		        .setHidden();
		todo_blk.addField("ToDo_Workid")
		        .setDbName("DATAID")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHDATAID: Data Id")
		        .setHidden();
		todo_blk.addField("TODO_REALUSERID")
		        .setDbName("REALUSERID")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHREALUSERID: Real User Id")
		        .setHidden();
		todo_blk.addField("TODO_STATUS")
		        .setDbName("STATUS")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHSTATUS: Status")
		        .setHidden();
		
		todo_blk.addField("TODO_FLOWSTATUS")
		.setDbName("FLOWSTATUS")
		.setSize(30)
		.setLabel("TODOWORKBENCHFLOWSTATUS: Flow Status")
		.setFontProperty("ÍË»Ø", "red")
		.setFontProperty("Returned", "red");
		
		todo_blk.addField("TODO_DONETIME")
		        .setDbName("DONETIME")
		        .setSize(30)
		        .setLabel("TODOWORKBENCHDONETIME: Done Time")
		        .setHidden();
		
		todo_blk.addField("TODO_TRACKID")
		.setDbName("TRACKID")
		.setSize(30)
		.setHidden();
		todo_blk.setView("HZ_WF_TASK_VIEW");
		todo_blk.setMasterBlock(headblk);
		todo_set = todo_blk.getASPRowSet();
		todo_bar = mgr.newASPCommandBar(todo_blk);
		todo_bar.defineCommand(ASPCommandBar.OKFIND, "okFindTODO");
		todo_tbl = mgr.newASPTable(todo_blk);
		todo_tbl.enableRowSelect();
//		todo_tbl.setWrap();
//		todo_tbl.disableNoWrap();
		todo_lay = todo_blk.getASPBlockLayout();
		todo_lay.setDefaultLayoutMode(todo_lay.MULTIROW_LAYOUT);
		

      toread_blk = mgr.newASPBlock("TOREAD");
      toread_blk.addField("TOREAD_TASKID")
              .setDbName("TASKID")
              .setSize(30)
              .setLabel("TOREADWORKBENCHTASKID: Task Id")
              .setHidden();
      toread_blk.addField("TOREAD_USERTASKID").setHidden().setDbName("USERTASKID");
     toread_blk.addField("TOREAD_TITLE")
             .setDbName("TITLE")
             .setSize(30)
             .setLabel("TOREADWORKBENCHTITLE: Title")
             .setHyperlink(ToReadUrl, "ToRead_Workid,TOREAD_URL,TOREAD_TRACKID,TOREAD_TASKID,TOREAD_REALUSERID,TOREAD_TITLE","NEWWIN");
     toread_blk.addField("TOREAD_MODELNAME")
     .setDbName("MODELNAME")
     .setSize(30)
     .setLabel("TOREADWORKBENCHMODELNAME: Model Name");
     
     toread_blk.addField("TOREADNODENAME")
     .setDbName("NODENAME")
     .setSize(10)
     .setLabel("TOREADWORKBENCHNODENAME: Node Name");
     
     toread_blk.addField("TOREAD_SENDUSERID")
             .setDbName("SENDUSERID")
             .setSize(30)
             .setLabel("TOREADWORKBENCHSENDUSERID: Send User Id").setHidden();
     toread_blk.addField("TOREAD_SENDTIME","Date")
             .setDbName("SENDTIME")
             .setSize(30)
             .setLabel("TOREADWORKBENCHSENDTIME: Send Time");
     toread_blk.addField("TOREAD_URL")
             .setDbName("URL")
             .setSize(30)
             .setLabel("TOREADWORKBENCHURL: Url")
             .setHidden();
     toread_blk.addField("TOREAD_USERID")
             .setDbName("USERID")
             .setSize(30)
             .setLabel("TOREADWORKBENCHUSERID: User Id")
             .setHidden();
     toread_blk.addField("TOREAD_TODOTYPE")
             .setDbName("TODOTYPE")
             .setSize(30)
             .setLabel("TOREADWORKBENCHTODOTYPE: Todo Type")
             .setHidden();
     toread_blk.addField("TOREAD_IMPORTANCE")
             .setDbName("IMPORTANCE")
             .setSize(30)
             .setLabel("TOREADWORKBENCHIMPORTANCE: Importance")
             .setHidden();
     toread_blk.addField("TOREAD_TIMELIMIT")
             .setDbName("TIMELIMIT")
             .setSize(30)  
             .setLabel("TOREADWORKBENCHTIMELIMIT: Time Limit").setHidden();
     toread_blk.addField("TOREAD_SENDUSERNAME")
             .setDbName("SENDUSERNAME")
             .setSize(30)
             .setLabel("TOREADWORKBENCHSENDUSERNAME: Send User Name");
     toread_blk.addField("TOREAD_SENDUSERDEPTNAME")
             .setDbName("SENDUSERDEPTNAME")
             .setSize(30)
             .setLabel("TOREADWORKBENCHSENDUSERDEPTNAME: Send Department")
             .setHidden();
     toread_blk.addField("ToRead_Workid")
             .setDbName("DATAID")
             .setSize(30)
             .setLabel("TOREADWORKBENCHDATAID: Data Id")
             .setHidden();
     toread_blk.addField("TOREAD_REALUSERID")
             .setDbName("REALUSERID")
             .setSize(30)
             .setLabel("TOREADWORKBENCHREALUSERID: Real User Id").setHidden();
     toread_blk.addField("TOREAD_STATUS")
             .setDbName("STATUS")
             .setSize(30)
             .setLabel("TOREADWORKBENCHSTATUS: Status")
             .setHidden();
     toread_blk.addField("TOREAD_DONETIME")
             .setDbName("DONETIME")
             .setSize(30)
             .setLabel("TOREADWORKBENCHDONETIME: Done Time")
             .setHidden();
     toread_blk.addField("TOREAD_TRACKID")
     .setDbName("TRACKID")
     .setSize(30)
     .setHidden();
     toread_blk.setView("HZ_WF_TASK_VIEW");
     toread_blk.setMasterBlock(headblk);
     toread_set = toread_blk.getASPRowSet();
     toread_bar = mgr.newASPCommandBar(toread_blk);
     toread_bar.defineCommand(ASPCommandBar.OKFIND, "okFindToRead");
     toread_bar.addCustomCommand("batchRead", "TODOWORKBENCHBATCHREAD: Batch Read");
     toread_bar.enableMultirowAction();
     toread_tbl = mgr.newASPTable(toread_blk);
     toread_tbl.enableRowSelect();
//     toread_tbl.setWrap();
//     toread_tbl.disableNoWrap();
     toread_lay = toread_blk.getASPBlockLayout();
     toread_lay.setDefaultLayoutMode(toread_lay.MULTIROW_LAYOUT);
		
		
		tabs = mgr.newASPTabContainer();
		tabs.addTab(mgr.translate("TODOWORKBENCHTODO: Todo"), "javascript:commandSet('MAIN.activateTODO','')");
		tabs.addTab(mgr.translate("TODOWORKBENCHTOREAD: ToRead"), "javascript:commandSet('MAIN.activateToRead','')");

		tabs.setContainerWidth(700);
		tabs.setLeftTabSpace(1);
		tabs.setContainerSpace(5);
		tabs.setTabWidth(100);
	}
	
	
	public void adjust()
	{
		headbar.removeCustomCommand("activateTODO");
		headbar.removeCustomCommand("activateToRead");
		
		tabs.setLabel(1, mgr.translate("TODOWORKBENCHTODO: Todo") + "(" + todoSize + ")");
		tabs.setLabel(2, mgr.translate("TODOWORKBENCHTOREAD: ToRead") + "(" + toReadSize + ")");
	}
	
	

	
	protected String getDescription() {
		return  mgr.translate("TODOWORKBENCHDESC: Workitems");
	}

	protected String getTitle() {
		return getDescription();
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		ASPHTMLFormatter fmt=mgr.newASPHTMLFormatter();
		if (headlay.isVisible())
			appendToHTML(headlay.show());
		
		appendDirtyJavaScript("String.prototype.strLen = function() {\n");
		appendDirtyJavaScript("    var len = 0;\n");
		appendDirtyJavaScript("    for (var i = 0; i < this.length; i++) {\n");
		appendDirtyJavaScript("         if (this.charCodeAt(i) > 255 || this.charCodeAt(i) < 0) len += 2; else len ++;\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("    return len;\n");
		appendDirtyJavaScript("};\n");
		appendDirtyJavaScript("String.prototype.strToChars = function(){\n");
		appendDirtyJavaScript("    var chars = new Array();\n");
		appendDirtyJavaScript("    for (var i = 0; i < this.length; i++){\n");
		appendDirtyJavaScript("       chars[i] = [this.substr(i, 1), this.isCHS(i)];\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("    String.prototype.charsArray = chars;\n");
		appendDirtyJavaScript("    return chars;\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("String.prototype.isCHS = function(i){\n");
		appendDirtyJavaScript("    if (this.charCodeAt(i) > 255 || this.charCodeAt(i) < 0)\n"); 
		appendDirtyJavaScript("        return true;\n");
		appendDirtyJavaScript("    else \n");
		appendDirtyJavaScript("        return false;\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("String.prototype.subCHString = function(start, end){\n");
		appendDirtyJavaScript("    var len = 0; \n");
		appendDirtyJavaScript("    var str =\"\";\n");
		appendDirtyJavaScript("    this.strToChars(); \n");
		appendDirtyJavaScript("    for (var i = 0; i < this.length; i++) {\n");
		appendDirtyJavaScript("        if(this.charsArray[i][1])\n");
		appendDirtyJavaScript("            len += 2;\n");
		appendDirtyJavaScript("        else \n");
		appendDirtyJavaScript("            len++;\n");
		appendDirtyJavaScript("        if (end < len)\n");
		appendDirtyJavaScript("            return str;\n");
		appendDirtyJavaScript("        else if (start < len)\n");
		appendDirtyJavaScript("            str += this.charsArray[i][0];\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("    return str;\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("String.prototype.subCHStr = function(start, length){\n");
		appendDirtyJavaScript("    return this.subCHString(start, start + length);\n");
		appendDirtyJavaScript("}\n");
		
		  appendDirtyJavaScript("function refreshParent()\n");
		  appendDirtyJavaScript("{\n");
		  appendDirtyJavaScript(" submit(); return true;\n");
		  appendDirtyJavaScript("}\n");
	
	
		  appendDirtyJavaScript("function disableEnterKey(e) {\n");
	      appendDirtyJavaScript("   var key;\n");
	      appendDirtyJavaScript("   if (window.event)\n");
	      appendDirtyJavaScript("       key = window.event.keyCode;\n");
	      appendDirtyJavaScript("   else\n");
	      appendDirtyJavaScript("       key = e.which;\n");
	      appendDirtyJavaScript("   return (key != 13);\n");
	      appendDirtyJavaScript("}\n");

		  
		  appendDirtyJavaScript("function comptime(beginTime,endTime){\n");
		  appendDirtyJavaScript("   var beginTimes=beginTime.substring(0,10).split('-');\n");
	      appendDirtyJavaScript("   var endTimes=endTime.substring(0,10).split('-');\n");
	      appendDirtyJavaScript("   beginTime=beginTimes[1]+'-'+beginTimes[2]+'-'+beginTimes[0];\n");
	      appendDirtyJavaScript("   endTime=endTimes[1]+'-'+endTimes[2]+'-'+endTimes[0];\n");
	      appendDirtyJavaScript("   var a =(Date.parse(endTime)-Date.parse(beginTime))/3600/1000;\n");
	      appendDirtyJavaScript("   if(a<0){\n");
	      appendDirtyJavaScript("      return -1;\n");
	      appendDirtyJavaScript("   }else if (a>0){\n");
	      appendDirtyJavaScript("      return 1;\n");
	      appendDirtyJavaScript("   }else if (a==0){\n");
	      appendDirtyJavaScript("     return 0;\n");
	      appendDirtyJavaScript("   }else{\n");
	      appendDirtyJavaScript("    return 'exception';\n");
	      appendDirtyJavaScript("   }\n");
	      appendDirtyJavaScript("}\n");
	      
	      appendDirtyJavaScript("function validateStartDate(i){\n");
	      appendDirtyJavaScript("   setDirty();\n");
	      appendDirtyJavaScript("   if( !checkStartDate(i) ) return;\n");
	      appendDirtyJavaScript("   if((getValue_('END_DATE',i)!='')&&(getValue_('START_DATE',i)!=''))\n");
	      appendDirtyJavaScript("   {\n");
	      appendDirtyJavaScript("      if(comptime(getValue_('START_DATE',i),getValue_('END_DATE',i))==-1){\n");
	      appendDirtyJavaScript("        getField_('END_DATE',i).value='';");
	      appendDirtyJavaScript("      }");
	      appendDirtyJavaScript("   }\n");
	      appendDirtyJavaScript("}\n");
	      
	      appendDirtyJavaScript("function validateEndDate(i){\n");
	      appendDirtyJavaScript("   setDirty();\n");
	      appendDirtyJavaScript("   if( !checkEndDate(i) ) return;\n");
	      appendDirtyJavaScript("   if((getValue_('END_DATE',i)!='')&&(getValue_('START_DATE',i)!=''))\n");
	      appendDirtyJavaScript("   {\n");
	      appendDirtyJavaScript("      if(comptime(getValue_('START_DATE',i),getValue_('END_DATE',i))==-1){\n");
	      appendDirtyJavaScript("        getField_('START_DATE',i).value='';");
	      appendDirtyJavaScript("      }");
	      appendDirtyJavaScript("   }\n");
	      appendDirtyJavaScript("}\n");
	      
	      
	      appendDirtyJavaScript("function checkStartDate(i){\n");
	      appendDirtyJavaScript("   fld = getField_('START_DATE',i);\n");
	      appendDirtyJavaScript("   formatDate_(fld,'yyyy-MM-dd','MAIN');\n");
	      appendDirtyJavaScript("   var startdate=fld.value;\n");
	      appendDirtyJavaScript("   if(startdate!=''){\n");
	      appendDirtyJavaScript("      if(startdate.length!=10){\n");
	      appendDirtyJavaScript("           alert('" + mgr.translate("TODOWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
	      appendDirtyJavaScript("          fld.value='';\n");
	      appendDirtyJavaScript("          return false;\n");
	      appendDirtyJavaScript("      }else{\n");
	      appendDirtyJavaScript("          startdate=startdate.replace('-','');\n");
	      appendDirtyJavaScript("          startdate=startdate.replace('-','');\n");
	      appendDirtyJavaScript("          if(isNaN(parseInt(startdate))){\n");
	      appendDirtyJavaScript("             alert('" + mgr.translate("TODOWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
	      appendDirtyJavaScript("             fld.value='';\n");
	      appendDirtyJavaScript("             return false;\n");
	      appendDirtyJavaScript("          }\n");
	      appendDirtyJavaScript("      }\n");
	      appendDirtyJavaScript("   }\n");
	      appendDirtyJavaScript("   return true;\n");
	      appendDirtyJavaScript("}\n");
	      
	      appendDirtyJavaScript("function checkEndDate(i){\n");
	      appendDirtyJavaScript("   fld = getField_('END_DATE',i);\n");
	      appendDirtyJavaScript("   formatDate_(fld,'yyyy-MM-dd','MAIN');\n");
	      appendDirtyJavaScript("   var enddate=fld.value;\n");
	      appendDirtyJavaScript("   if(enddate!=''){\n");
	      appendDirtyJavaScript("      if(enddate.length!=10){\n");
	      appendDirtyJavaScript("          alert('" + mgr.translate("TODOWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
	      appendDirtyJavaScript("          fld.value='';\n");
	      appendDirtyJavaScript("          return false;\n");
	      appendDirtyJavaScript("      }else{\n");
	      appendDirtyJavaScript("          enddate=enddate.replace('-','');\n");
	      appendDirtyJavaScript("          enddate=enddate.replace('-','');\n");
	      appendDirtyJavaScript("          if(isNaN(parseInt(enddate))){\n");
	      appendDirtyJavaScript("             alert('" + mgr.translate("TODOWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
	      appendDirtyJavaScript("             fld.value='';\n");
	      appendDirtyJavaScript("             return false;\n");
	      appendDirtyJavaScript("          }\n");
	      appendDirtyJavaScript("      }\n");
	      appendDirtyJavaScript("   }\n");
	      appendDirtyJavaScript("   return true;\n");
	      appendDirtyJavaScript("}\n");
		
      if (headlay.isCustomLayout()) {
         appendDirtyJavaScript("function reSetValue()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.SEARCH_TITLE.value = '';\n");
         appendDirtyJavaScript("   document.form.SEARCH_PROCESS_NAME.value = '';\n");
         appendDirtyJavaScript("   document.form.START_DATE.value = '';\n");
         appendDirtyJavaScript("   document.form.END_DATE.value = '';\n");
         appendDirtyJavaScript("}\n");

//         printSpaces(2);
//         appendToHTML(fmt.drawSubmit("SEARCH", "TODOWORKBENCHSEARCHBTN: Search", ""));

         appendToHTML(tabs.showTabsInit());
         if (tabs.getActiveTab() == 1) {
            appendToHTML(todo_lay.show());
            if (todo_lay.isMultirowLayout() && todo_set.countRows() > 0) {
               subCurrTableTd(todo_blk.getName(), 50);
            }
         } else if (tabs.getActiveTab() == 2) {
            appendToHTML(toread_lay.show());
            if (todo_lay.isMultirowLayout() && todo_set.countRows() > 0) {
               subCurrTableTd(todo_blk.getName(), 50);
            }
         } 
      }
   }
	
	private void subCurrTableTd(String currTable,int maxLength) throws FndException
	{
//		appendDirtyJavaScript("var table=document.getElementById(\"cnt"+currTable+"\");\n");
//        appendDirtyJavaScript("for(var i=2;i<table.rows.length;i++){\n");
//        appendDirtyJavaScript("   for(var j=2;j<table.rows[i].cells.length;j++){\n");
//        appendDirtyJavaScript("          var td=table.rows[i].cells[j];\n");
//        appendDirtyJavaScript("          var tdObj;\n");
//        appendDirtyJavaScript("          if(td.firstChild.nodeType==1){\n");
//        appendDirtyJavaScript("               tdObj=td.firstChild;\n");
//        appendDirtyJavaScript("              if('DIV' == tdObj.nodeName){ if(tdObj.firstChild.nodeType == 1  || 'IMG' == tdObj.firstChild.nodeName){continue;} } ;\n");
//        appendDirtyJavaScript("          }else if(td.firstChild.nodeType==3){\n");
//        appendDirtyJavaScript("               tdObj=td;\n");
//        appendDirtyJavaScript("          }\n");
//        appendDirtyJavaScript("          var temp=tdObj.innerHTML;\n");
//        appendDirtyJavaScript("          if(temp.strLen()>"+maxLength+")\n");
//        appendDirtyJavaScript("             tdObj.innerHTML=temp.subCHStr(0,"+(maxLength-1)+")+'...';\n");
//        appendDirtyJavaScript("          else\n");
//        appendDirtyJavaScript("             tdObj.innerHTML=temp;\n");
//        
//        
//        appendDirtyJavaScript("   }\n");
//        appendDirtyJavaScript("}\n");
	}
	
	
	protected AutoString getContents() throws FndException
	   {
	      if ( DEBUG ) debug(this+": ASPPageProvider.getContents()");

	     ASPManager mgr = getASPManager();
		  AutoString out = getOutputStream();
	     out.clear();

	      if ( getASPLov() != null )
	         return out;

	      out.append("<html>\n");
	      out.append("<head>\n");
	      out.append(mgr.generateHeadTag(getDescription()));
	      out.append("<script type=\"text/javascript\">\n");
	      out.append("function disableEnterKey(e) {\n");
	      out.append("   var key;\n");
	      out.append("   if (window.event)\n");
	      out.append("       key = window.event.keyCode;\n");
	      out.append("   else\n");
	      out.append("       key = e.which;\n");
	      out.append("   return (key != 13);\n");
	      out.append("}\n");
	      out.append("</script>\n");
	      out.append("</head>\n");
	      out.append("<body ");
	      String bodyStr=mgr.generateBodyTag();
	      if(mgr.isExplorer()){
	    	  bodyStr=bodyStr.replace("keyPressed()", "return disableEnterKey(event);");
	      }else{
	    	  bodyStr="onkeypress=\"return disableEnterKey(event);\""+bodyStr;
	      }
	      out.append(bodyStr);
	      out.append(" >\n");
	      out.append("<form ");
	      out.append(mgr.generateFormTag());
	      out.append(" >\n");
	      out.append(mgr.startPresentation(getTitle()));

	      printContents();

	      out.append(mgr.endPresentation());

	      out.append("</form>\n");
	      out.append("</body>\n");
	      out.append("</html>\n");
	      return out;
	   }

}
