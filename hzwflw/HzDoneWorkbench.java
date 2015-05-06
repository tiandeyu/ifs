package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
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

public class HzDoneWorkbench extends ASPPageProvider implements HzConstants{

   public HzDoneWorkbench(ASPManager mgr, String pagePath) {
       super(mgr, pagePath);
   }
   
   public static boolean DEBUG = Util.isDebugEnabled(HzDoneWorkbench.class.getCanonicalName());
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

   private ASPBlock done_blk;
   private ASPRowSet done_set;
   private ASPCommandBar done_bar;
   private ASPTable done_tbl;
   private ASPBlockLayout done_lay;
   
   private ASPBlock read_blk;
   private ASPRowSet read_set;
   private ASPCommandBar read_bar;
   private ASPTable read_tbl;
   private ASPBlockLayout read_lay;
   
   private String topic;
   private String processName;
   private String startDate;
   private String endDate;
   
   
   private String doneSize;
   private String readSize;
   
   public void run()
   {
       mgr = getASPManager();
       
       topic = mgr.readValue("SEARCH_TITLE");
       processName=mgr.readValue("SEARCH_PROCESS_NAME");
       startDate=mgr.readValue("START_DATE");
       endDate=mgr.readValue("END_DATE");
       
       setCountValue();
       
       if (mgr.commandBarActivated()){
           eval(mgr.commandBarFunction());
       }
//       else if(mgr.buttonPressed("SEARCH"))
//            setSearchCondition();
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
          okFindDONE();
       }
       if(tabs.getActiveTab()==2)
       {
          okFindRead();
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
          okFindDONE();
       }else if(tabs.getActiveTab()==2)
       {
           okFindRead();
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
          
           String sql_done =   "select to_char(count(1)) N from HZ_WF_TASK_HIS_VIEW where STATUS='3' and HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='" + user_id + "'" + " AND TITLE NOT LIKE '%,请关注.'";
           String sql_read =   "select to_char(count(1)) N from HZ_WF_TASK_HIS_VIEW where STATUS='4' and HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='" + user_id + "'";
           if(mgr.buttonPressed("SEARCH"))
           {       
               sql_done = setSqlCondition(sql_done);
               sql_done = setSqlCondition(sql_read);
           }
           q = trans.addQuery("GETCOUNTDONE", sql_done);
           q = trans.addQuery("GETCOUNTREAD", sql_read);
           trans = mgr.perform(trans);
           doneSize = trans.getValue("GETCOUNTDONE/DATA/N");
           readSize = trans.getValue("GETCOUNTREAD/DATA/N");
       }
   }
   
   public void okFindDONE()
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPQuery q;
       q = trans.addQuery(done_blk);
       setCondition(q);
//       q.addWhereCondition(" upper(REALUSERID) like '%"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
       q.addWhereCondition(" HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
       q.addWhereCondition(" STATUS='3'");
       q.addWhereCondition(" TITLE NOT LIKE '%,请关注.'");
       q.setOrderByClause("  DONETIME DESC");
       q.includeMeta("ALL");
       q.setBufferSize(15);
       trans = mgr.querySubmit(trans, done_blk);
   }
   
   public void okFindRead()
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      q = trans.addQuery(read_blk);
      setCondition(q);
//      q.addWhereCondition("upper(REALUSERID) like '%"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
      q.addWhereCondition(" HZ_WF_UTIL_API.Get_Clean_User(REALUSERID) ='"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
      q.addWhereCondition("STATUS='4'");
      q.setOrderByClause(" DONETIME DESC");
      q.includeMeta("ALL");
      q.setBufferSize(15);
      trans = mgr.querySubmit(trans, read_blk);
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
   
   public void activateDONE()
   {
      tabs.setActiveTab(1);
      okFindDONE();
   }
   public void activateRead()
   {
      tabs.setActiveTab(2);
      okFindRead();
   }
  
   public void preDefine()
   {
       ASPManager mgr = getASPManager();
       
       headblk = mgr.newASPBlock("MAIN");
       headblk.addField("SEARCH_TITLE").
               setFunction("''").
               setLabel("DONEWORKBENCHSEARCHTITLE: Title").
               setSize(30);
       headblk.addField("SEARCH_SEND_USER").
               setFunction("''").
               setLabel("DONEWORKBENCHSEARCHSENDUSER: Sender").
               setHidden();
       headblk.addField("SEARCH_PROCESS_NAME").
               setFunction("''").
               setLabel("DONEWORKBENCHSEARCHPROCESSNAME: Process Name").
               setSize(20);
       headblk.addField("SEARCH_TASK_NAME").
               setFunction("''").
               setLabel("DAIBANSEARCHTASKNAME: Task Name").
               setHidden();
       headblk.addField("START_DATE","Date","yyyy-MM-dd").
               setFunction("''").
               setLabel("DONEWORKBENCHSEARCHSTARTDATE: Send Time From ").
               setSize(10);
       headblk.addField("END_DATE","Date","yyyy-MM-dd").
               setFunction("''").
               setLabel("DONEWORKBENCHSEARCHENDDATE: To").
               setSize(10);
       
       headblk.setView("DUAL");      
       headblk.setTitle("DONEWORKBENCHHEADBLK: Done Items");
       headset = headblk.getASPRowSet();
       headbar = mgr.newASPCommandBar(headblk);
	   headbar.defineCommand(headbar.FIND, "setSearchCondition");
	   headbar.enableCommand(headbar.FIND);
       headtbl = mgr.newASPTable(headblk);
       headtbl.setTitle("DONEWORKBENCHTBLHEAD: Done Items");// 待处理事项
       headtbl.enableRowSelect();
       headtbl.setWrap();
       headlay = headblk.getASPBlockLayout();
       headlay.setDefaultLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);
       headlay.setEditable();
       headlay.setDialogColumns(4);
       
       
       this.disableHeader();
//     this.disableBar();
       
       headbar.addCustomCommand("activateDONE", "Done");
       headbar.addCustomCommand("activateRead", "Read");
       
       
       done_blk = mgr.newASPBlock("DONE");
       done_blk.addField("DONE_TASKID")
               .setDbName("TASKID")
               .setSize(30)
               .setLabel("DONEWORKBENCHTASKID: Task Id")
               .setHidden();
       done_blk.addField("DONE_TITLE")
               .setDbName("TITLE")
               .setSize(30)
               .setLabel("DONEWORKBENCHTITLE: Title")
               .setHyperlink(DoneUrl, "Done_Workid,DONE_TRACKID","NEWWIN");
       done_blk.addField("DONE_MODELNAME")
       .setDbName("MODELNAME")
       .setSize(30)
       .setLabel("DONEWORKBENCHMODELNAME: Model Name");
       done_blk.addField("DONE_SENDUSERID")
               .setDbName("SENDUSERID")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDUSERID: Send User Id").setHidden();
       done_blk.addField("DONE_SENDTIME","Date")
               .setDbName("SENDTIME")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDTIME: Send Time");
       done_blk.addField("DONE_URL")
               .setDbName("URL")
               .setSize(30)
               .setLabel("DONEWORKBENCHURL: Url")
               .setHidden();
       done_blk.addField("DONE_USERID")
               .setDbName("USERID")
               .setSize(30)
               .setLabel("DONEWORKBENCHUSERID: User Id")
               .setHidden();
       done_blk.addField("DONE_TODOTYPE")
               .setDbName("TODOTYPE")
               .setSize(30)
               .setLabel("DONEWORKBENCHDONETYPE: Todo Type")
               .setHidden();;

       done_blk.addField("DONE_IMPORTANCE")
               .setDbName("IMPORTANCE")
               .setSize(30)
               .setLabel("DONEWORKBENCHIMPORTANCE: Importance")
               .setHidden();
       done_blk.addField("DONE_TIMELIMIT")
               .setDbName("TIMELIMIT")
               .setSize(30) 
               .setLabel("DONEWORKBENCHTIMELIMIT: Time Limit").setHidden();
       done_blk.addField("DONE_SENDUSERNAME")
               .setDbName("SENDUSERNAME")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDUSERNAME: Send User Name");
       done_blk.addField("DONE_SENDUSERDEPTNAME")
               .setDbName("SENDUSERDEPTNAME")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDUSERDEPTNAME: Send Department")
               .setHidden();
       done_blk.addField("Done_Workid")
               .setDbName("DATAID")
               .setSize(30)
               .setLabel("DONEWORKBENCHDATAID: Data Id")
               .setHidden();
       done_blk.addField("DONE_REALUSERID")
               .setDbName("REALUSERID")
               .setSize(30)
               .setLabel("DONEWORKBENCHREALUSERID: Real User Id").setHidden();
       done_blk.addField("DONE_STATUS")
               .setDbName("STATUS")
               .setSize(30)
               .setLabel("DONEWORKBENCHSTATUS: Status")
               .setHidden();
       done_blk.addField("DONE_DONETIME")
               .setDbName("DONETIME")
               .setSize(30)
               .setLabel("DONEWORKBENCHDONETIME: Done Time");
       done_blk.addField("DONE_TRACKID")
       .setDbName("TRACKID")
       .setSize(30)
       .setLabel("DONEWORKBENCHDONETIME: Done Time").setHidden();
       done_blk.setView("HZ_WF_TASK_HIS_VIEW");
       done_blk.setMasterBlock(headblk);
       done_set = done_blk.getASPRowSet();
       done_bar = mgr.newASPCommandBar(done_blk);
       done_bar.defineCommand(done_bar.OKFIND, "okFindDONE");
       done_tbl = mgr.newASPTable(done_blk);
       done_tbl.enableRowSelect();
//       done_tbl.setWrap();
//       done_tbl.disableNoWrap();
       done_lay = done_blk.getASPBlockLayout();
       done_lay.setDefaultLayoutMode(done_lay.MULTIROW_LAYOUT);
       
       
       read_blk = mgr.newASPBlock("READ");
       read_blk.addField("READ_TASKID")
               .setDbName("TASKID")
               .setSize(30)
               .setLabel("DONEWORKBENCHTASKID: Task Id")
               .setHidden();
       read_blk.addField("READ_TITLE")
               .setDbName("TITLE")
               .setSize(30)
               .setLabel("DONEWORKBENCHTITLE: Title")
               .setHyperlink(ReadUrl, "Read_Workid,READ_TRACKID,READ_TITLE","NEWWIN");
       read_blk.addField("READ_MODELNAME")
       .setDbName("MODELNAME")
       .setSize(30)
       .setLabel("DONEWORKBENCHMODELNAME: Model Name");
       read_blk.addField("READ_SENDUSERID")
               .setDbName("SENDUSERID")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDUSERID: Send User Id").setHidden();
       read_blk.addField("READ_SENDTIME","Date")
               .setDbName("SENDTIME")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDTIME: Send Time");
       read_blk.addField("READ_URL")
               .setDbName("URL")
               .setSize(30)
               .setLabel("DONEWORKBENCHURL: Url")
               .setHidden();
       read_blk.addField("READ_USERID")
               .setDbName("USERID")
               .setSize(30)
               .setLabel("DONEWORKBENCHUSERID: User Id")
               .setHidden();
       read_blk.addField("READ_TODOTYPE")
               .setDbName("TODOTYPE")
               .setSize(30)
               .setLabel("DONEWORKBENCHDONETYPE: Todo Type")
               .setHidden();;

       read_blk.addField("READ_IMPORTANCE")
               .setDbName("IMPORTANCE")
               .setSize(30)
               .setLabel("DONEWORKBENCHIMPORTANCE: Importance")
               .setHidden();
       read_blk.addField("READ_TIMELIMIT")
               .setDbName("TIMELIMIT")
               .setSize(30) 
               .setLabel("DONEWORKBENCHTIMELIMIT: Time Limit").setHidden();
       read_blk.addField("READ_SENDUSERNAME")
               .setDbName("SENDUSERNAME")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDUSERNAME: Send User Name");
       read_blk.addField("READ_SENDUSERDEPTNAME")
               .setDbName("SENDUSERDEPTNAME")
               .setSize(30)
               .setLabel("DONEWORKBENCHSENDUSERDEPTNAME: Send Department")
               .setHidden();
       read_blk.addField("Read_Workid")
               .setDbName("DATAID")
               .setSize(30)
               .setLabel("DONEWORKBENCHDATAID: Data Id")
               .setHidden();
       read_blk.addField("READ_REALUSERID")
               .setDbName("REALUSERID")
               .setSize(30)
               .setLabel("DONEWORKBENCHREALUSERID: Real User Id").setHidden();
       read_blk.addField("READ_STATUS")
               .setDbName("STATUS")
               .setSize(30)
               .setLabel("DONEWORKBENCHSTATUS: Status")
               .setHidden();
       read_blk.addField("READ_DONETIME")
               .setDbName("DONETIME")
               .setSize(30)
               .setLabel("DONEWORKBENCHDONETIME: Done Time");
       read_blk.addField("READ_TRACKID")
       .setDbName("TRACKID")
       .setSize(30).setHidden();
       read_blk.setView("HZ_WF_TASK_HIS_VIEW");
       read_blk.setMasterBlock(headblk);
       read_set = read_blk.getASPRowSet();
       read_bar = mgr.newASPCommandBar(read_blk);
       read_bar.defineCommand(read_bar.OKFIND, "okFindRead");
       read_tbl = mgr.newASPTable(read_blk);
       read_tbl.enableRowSelect();
//       read_tbl.setWrap();
//       read_tbl.disableNoWrap();
       read_lay = read_blk.getASPBlockLayout();
       read_lay.setDefaultLayoutMode(read_lay.MULTIROW_LAYOUT);
       
       
       tabs = mgr.newASPTabContainer();
       tabs.addTab(mgr.translate("DONEWORKBENCHDONE: Done"), "javascript:commandSet('MAIN.activateDONE','')");
       tabs.addTab(mgr.translate("DONEWORKBENCHREAD: Read"), "javascript:commandSet('MAIN.activateRead','')");

       tabs.setContainerWidth(700);
       tabs.setLeftTabSpace(1);
       tabs.setContainerSpace(5);
       tabs.setTabWidth(100);
   }
   
   
   public void adjust()
   {
       headbar.removeCustomCommand("activateDONE");
       headbar.removeCustomCommand("activateRead");
       
       tabs.setLabel(1, mgr.translate("DONEWORKBENCHDONE: Done") + "(" + doneSize + ")");
       tabs.setLabel(2, mgr.translate("DONEWORKBENCHREAD: Read") + "(" + readSize + ")");
   }
   
   

   
   protected String getDescription() {
       return  mgr.translate("DONEWORKBENCHDESC: Workitems");
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
         appendDirtyJavaScript(" submit(); \n");
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
         appendDirtyJavaScript("           alert('" + mgr.translate("DONEWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
         appendDirtyJavaScript("          fld.value='';\n");
         appendDirtyJavaScript("          return false;\n");
         appendDirtyJavaScript("      }else{\n");
         appendDirtyJavaScript("          startdate=startdate.replace('-','');\n");
         appendDirtyJavaScript("          startdate=startdate.replace('-','');\n");
         appendDirtyJavaScript("          if(isNaN(parseInt(startdate))){\n");
         appendDirtyJavaScript("             alert('" + mgr.translate("DONEWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
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
         appendDirtyJavaScript("          alert('" + mgr.translate("DONEWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
         appendDirtyJavaScript("          fld.value='';\n");
         appendDirtyJavaScript("          return false;\n");
         appendDirtyJavaScript("      }else{\n");
         appendDirtyJavaScript("          enddate=enddate.replace('-','');\n");
         appendDirtyJavaScript("          enddate=enddate.replace('-','');\n");
         appendDirtyJavaScript("          if(isNaN(parseInt(enddate))){\n");
         appendDirtyJavaScript("             alert('" + mgr.translate("DONEWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
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

//        printSpaces(2);
//        appendToHTML(fmt.drawSubmit("SEARCH", "DONEWORKBENCHSEARCHBTN: Search", ""));

        appendToHTML(tabs.showTabsInit());
        if (tabs.getActiveTab() == 1) {
           appendToHTML(done_lay.show());
           if (done_lay.isMultirowLayout() && done_set.countRows() > 0) {
              subCurrTableTd(done_blk.getName(), 50);
           }
        } 
        if (tabs.getActiveTab() == 2) {
           appendToHTML(read_lay.show());
           if (read_lay.isMultirowLayout() && read_set.countRows() > 0) {
              subCurrTableTd(read_blk.getName(), 50);
           }
        } 
        appendToHTML(tabs.showTabsFinish()); 
        
     }
  }
   
   private void subCurrTableTd(String currTable,int maxLength) throws FndException
   {
//       appendDirtyJavaScript("var table=document.getElementById(\"cnt"+currTable+"\");\n");
//       appendDirtyJavaScript("for(var i=2;i<table.rows.length;i++){\n");
//       appendDirtyJavaScript("   for(var j=2;j<table.rows[i].cells.length;j++){\n");
//       appendDirtyJavaScript("          var td=table.rows[i].cells[j];\n");
//       appendDirtyJavaScript("          var tdObj;\n");
//       appendDirtyJavaScript("          if(td.firstChild.nodeType==1){\n");
//       appendDirtyJavaScript("               tdObj=td.firstChild;\n");
//       appendDirtyJavaScript("              if('DIV' == tdObj.nodeName){ if(tdObj.firstChild.nodeType == 1  || 'IMG' == tdObj.firstChild.nodeName){continue;} } ;\n");
//       appendDirtyJavaScript("          }else if(td.firstChild.nodeType==3){\n");
//       appendDirtyJavaScript("               tdObj=td;\n");
//       appendDirtyJavaScript("          }\n");
//       appendDirtyJavaScript("          var temp=tdObj.innerHTML;\n");
//       appendDirtyJavaScript("          if(temp.strLen()>"+maxLength+")\n");
//       appendDirtyJavaScript("             tdObj.innerHTML=temp.subCHStr(0,"+(maxLength-1)+")+'...';\n");
//       appendDirtyJavaScript("          else\n");
//       appendDirtyJavaScript("             tdObj.innerHTML=temp;\n");
//       
//       
//       appendDirtyJavaScript("   }\n");
//       appendDirtyJavaScript("}\n");
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
