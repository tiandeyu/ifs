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

public class HzToReadWorkbench  extends ASPPageProvider implements HzConstants{

   public HzToReadWorkbench(ASPManager mgr, String pagePath) {
       super(mgr, pagePath);
   }
   
   public static boolean DEBUG = Util.isDebugEnabled(HzToReadWorkbench.class.getCanonicalName());
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

   private ASPBlock toRead_blk;
   private ASPRowSet toRead_set;
   private ASPCommandBar toRead_bar;
   private ASPTable toRead_tbl;
   private ASPBlockLayout toRead_lay;
   
  
   private String topic;
   private String processName;
   private String startDate;
   private String endDate;
   
   
   private String TOREADSize;
   private String sendSize;

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
       }else if(mgr.buttonPressed("SEARCH"))
            setSearchCondition();
       else 
           findAll();
       adjust();
       tabs.saveActiveTab();
   }
   

   public void track(String processId, String processName, String processTopic, String bizUrl, String bizObjid, String itemId, String trackNode){
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
          okFindToRead();
       }
   }
   
   private void setSearchCondition()
   {
       ASPManager mgr = getASPManager();
       topic=mgr.readValue("SEARCH_TITLE");
       processName=mgr.readValue("SEARCH_PROCESS_NAME");
       startDate=mgr.readValue("START_DATE");
       endDate=mgr.readValue("END_DATE");

       if(tabs.getActiveTab()==1)
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
           String sql_TOREAD =   "select to_char(count(1)) N from HZ_WF_TASK_VIEW where STATUS in ('2','6') and  upper(REALUSERID)='" + user_id + "'";
           
           if(mgr.buttonPressed("SEARCH"))
           {       
               sql_TOREAD = setSqlCondition(sql_TOREAD);
           }
           q = trans.addQuery("GETCOUNTTOREAD", sql_TOREAD);
           
           trans = mgr.perform(trans);
           TOREADSize = trans.getValue("GETCOUNTTOREAD/DATA/N");
       }
   }
   
   public void okFindToRead()
   {
     ASPManager mgr = getASPManager();
       
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPQuery q;

       q = trans.addQuery(toRead_blk);
       setCondition(q);
       q.addWhereCondition("upper(REALUSERID)='"+HzWfUtil.getOracleUserId(mgr).toUpperCase()+"'");
       q.addWhereCondition("STATUS='1'");
       q.setOrderByClause("SENDTIME DESC");
       q.includeMeta("ALL");
       q.setBufferSize(15);
       trans = mgr.querySubmit(trans, toRead_blk);
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
   
   

   
   public void activateTOREAD()
   {
      tabs.setActiveTab(1);
      okFindToRead();
   }
  
   public void preDefine()
   {
       ASPManager mgr = getASPManager();
       
       headblk = mgr.newASPBlock("MAIN");
       headblk.addField("SEARCH_TITLE").
               setFunction("''").
               setLabel("TOREADWORKBENCHSEARCHTITLE: Title").
               setSize(30);
       headblk.addField("SEARCH_SEND_USER").
               setFunction("''").
               setLabel("TOREADWORKBENCHSEARCHSENDUSER: Sender").
               setHidden();
       headblk.addField("SEARCH_PROCESS_NAME").
               setFunction("''").
               setLabel("TOREADWORKBENCHSEARCHPROCESSNAME: Process Name").
               setSize(20);
       headblk.addField("SEARCH_TASK_NAME").
               setFunction("''").
               setLabel("DAIBANSEARCHTASKNAME: Task Name").
               setHidden();
       headblk.addField("START_DATE","Date","yyyy-MM-dd").
               setFunction("''").
               setLabel("TOREADWORKBENCHSEARCHSTARTDATE: Send Time From ").
               setSize(10);
       headblk.addField("END_DATE","Date","yyyy-MM-dd").
               setFunction("''").
               setLabel("TOREADWORKBENCHSEARCHENDDATE: To").
               setSize(10);
       
       headblk.setView("DUAL");      
       headblk.setTitle("TOREADWORKBENCHHEADBLK: TOREAD Items");
       headset = headblk.getASPRowSet();
       headbar = mgr.newASPCommandBar(headblk);
//     headbar.defineCommand(headbar.OKFIND, "okFind");
       headtbl = mgr.newASPTable(headblk);
       headtbl.setTitle("TOREADWORKBENCHTBLHEAD: TOREAD Items");
       headtbl.enableRowSelect();
       headtbl.setWrap();
       headlay = headblk.getASPBlockLayout();
       headlay.setDefaultLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);
       headlay.setEditable();
       headlay.setDialogColumns(4);
       
       
       this.disableHeader();
//     this.disableBar();
       
       headbar.addCustomCommand("activateTOREAD", "TOREAD");
       
       
       toRead_blk = mgr.newASPBlock("TOREAD");
       toRead_blk.addField("toRead_TASKID")
               .setDbName("TASKID")
               .setSize(50)
               .setLabel("TOREADWORKBENCHTASKID: Task Id")
               .setHidden();
       toRead_blk.addField("toRead_TITLE")
               .setDbName("TITLE")
               .setSize(50)
               .setLabel("TOREADWORKBENCHTITLE: Title")
               .setHyperlink(ToReadUrl, "workid","NEWWIN");
       toRead_blk.addField("toRead_SENDUSERID")
               .setDbName("SENDUSERID")
               .setSize(50)
               .setLabel("TOREADWORKBENCHSENDUSERID: Send User Id");
       toRead_blk.addField("TOREAD_SENDTIME")
               .setDbName("SENDTIME")
               .setSize(50)
               .setLabel("TOREADWORKBENCHSENDTIME: Send Time");
       toRead_blk.addField("TOREAD_URL")
               .setDbName("URL")
               .setSize(50)
               .setLabel("TOREADWORKBENCHURL: Url")
               .setHidden();;
       toRead_blk.addField("toRead_USERID")
               .setDbName("USERID")
               .setSize(50)
               .setLabel("TOREADWORKBENCHUSERID: User Id");
       toRead_blk.addField("toRead_TOREADTYPE")
               .setDbName("TOREADTYPE")
               .setSize(50)
               .setLabel("TOREADWORKBENCHTOREADTYPE: TOREAD Type")
               .setHidden();;
       toRead_blk.addField("toRead_MODELNAME")
               .setDbName("MODELNAME")
               .setSize(50)
               .setLabel("TOREADWORKBENCHMODELNAME: Model Name");
       toRead_blk.addField("toRead_IMPORTANCE")
               .setDbName("IMPORTANCE")
               .setSize(50)
               .setLabel("TOREADWORKBENCHIMPORTANCE: Importance");
       toRead_blk.addField("toRead_TIMELIMIT")
               .setDbName("TIMELIMIT")
               .setSize(50) 
               .setLabel("TOREADWORKBENCHTIMELIMIT: Time Limit");
       toRead_blk.addField("toRead_SENDUSERNAME")
               .setDbName("SENDUSERNAME")
               .setSize(50)
               .setLabel("TOREADWORKBENCHSENDUSERNAME: Send User Name");
       toRead_blk.addField("toRead_SENDUSERDEPTNAME")
               .setDbName("SENDUSERDEPTNAME")
               .setSize(50)
               .setLabel("TOREADWORKBENCHSENDUSERDEPTNAME: Send Department");
       toRead_blk.addField("workid")
                 .setDbName("DATAID")
                 .setSize(50)
                 .setLabel("TOREADWORKBENCHDATAID: Data Id");
       toRead_blk.addField("toRead_REALUSERID")
               .setDbName("REALUSERID")
               .setSize(50)
               .setLabel("TOREADWORKBENCHREALUSERID: Real User Id");
       toRead_blk.addField("toRead_STATUS")
               .setDbName("STATUS")
               .setSize(50)
               .setLabel("TOREADWORKBENCHSTATUS: Status")
               .setHidden();
       toRead_blk.addField("toRead_DONETIME")
               .setDbName("DONETIME")
               .setSize(50)
               .setLabel("TOREADWORKBENCHDONETIME: Done Time")
               .setHidden();;
       toRead_blk.setView("HZ_WF_TASK_VIEW");
       toRead_blk.setMasterBlock(headblk);
       toRead_set = toRead_blk.getASPRowSet();
       toRead_bar = mgr.newASPCommandBar(toRead_blk);
       toRead_bar.defineCommand(toRead_bar.OKFIND, "okFindTOREAD");
       toRead_tbl = mgr.newASPTable(toRead_blk);
       toRead_tbl.enableRowSelect();
       toRead_tbl.setWrap();
       toRead_tbl.disableNoWrap();
       toRead_lay = toRead_blk.getASPBlockLayout();
       toRead_lay.setDefaultLayoutMode(toRead_lay.MULTIROW_LAYOUT);
       
       
       tabs = mgr.newASPTabContainer();
       tabs.addTab(mgr.translate("TOREADWORKBENCHTOREAD: TOREAD"), "javascript:commandSet('MAIN.activateTOREAD','')");

       tabs.setContainerWidth(700);
       tabs.setLeftTabSpace(1);
       tabs.setContainerSpace(5);
       tabs.setTabWidth(100);
   }
   
   
   public void adjust()
   {
       headbar.removeCustomCommand("activateTOREAD");
       
       tabs.setLabel(1, mgr.translate("TOREADWORKBENCHTOREAD: TOREAD") + "(" + TOREADSize + ")");//待办
   }
   
   

   
   protected String getDescription() {
       return  mgr.translate("TOREADWORKBENCHDESC: Workitems");// 工作项
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
         appendDirtyJavaScript("      if(comptime(getValue_('START_DATE',i),getValue_('END_DATE',i))!=1){\n");
         appendDirtyJavaScript("        getField_('END_DATE',i).value='';");
         appendDirtyJavaScript("      }");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");
         
         appendDirtyJavaScript("function validateEndDate(i){\n");
         appendDirtyJavaScript("   setDirty();\n");
         appendDirtyJavaScript("   if( !checkEndDate(i) ) return;\n");
         appendDirtyJavaScript("   if((getValue_('END_DATE',i)!='')&&(getValue_('START_DATE',i)!=''))\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      if(comptime(getValue_('START_DATE',i),getValue_('END_DATE',i))!=1){\n");
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
         appendDirtyJavaScript("           alert('" + mgr.translate("TOREADWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
         appendDirtyJavaScript("          fld.value='';\n");
         appendDirtyJavaScript("          return false;\n");
         appendDirtyJavaScript("      }else{\n");
         appendDirtyJavaScript("          startdate=startdate.replace('-','');\n");
         appendDirtyJavaScript("          startdate=startdate.replace('-','');\n");
         appendDirtyJavaScript("          if(isNaN(parseInt(startdate))){\n");
         appendDirtyJavaScript("             alert('" + mgr.translate("TOREADWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
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
         appendDirtyJavaScript("          alert('" + mgr.translate("TOREADWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
         appendDirtyJavaScript("          fld.value='';\n");
         appendDirtyJavaScript("          return false;\n");
         appendDirtyJavaScript("      }else{\n");
         appendDirtyJavaScript("          enddate=enddate.replace('-','');\n");
         appendDirtyJavaScript("          enddate=enddate.replace('-','');\n");
         appendDirtyJavaScript("          if(isNaN(parseInt(enddate))){\n");
         appendDirtyJavaScript("             alert('" + mgr.translate("TOREADWORKBENCHDATEFORMATINCORRECT: please check the date format.") + "');\n");
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

        printSpaces(2);
        appendToHTML(fmt.drawSubmit("SEARCH", "TOREADWORKBENCHSEARCHBTN: Search", ""));

        appendToHTML(tabs.showTabsInit());
        if (tabs.getActiveTab() == 1) {
           appendToHTML(toRead_lay.show());
           if (toRead_lay.isMultirowLayout() && toRead_set.countRows() > 0) {
              subCurrTableTd(toRead_blk.getName(), 50);
           }
        } 
     }
  }
   
   private void subCurrTableTd(String currTable,int maxLength) throws FndException
   {
       appendDirtyJavaScript("var table=document.getElementById(\"cnt"+currTable+"\");\n");
       appendDirtyJavaScript("for(var i=2;i<table.rows.length;i++){\n");
       appendDirtyJavaScript("   for(var j=2;j<table.rows[i].cells.length;j++){\n");
       appendDirtyJavaScript("          var td=table.rows[i].cells[j];\n");
       appendDirtyJavaScript("          var tdObj;\n");
       appendDirtyJavaScript("          if(td.firstChild.nodeType==1){\n");
       appendDirtyJavaScript("               tdObj=td.firstChild;\n");
       appendDirtyJavaScript("              if('DIV' == tdObj.nodeName){ if(tdObj.firstChild.nodeType == 1  || 'IMG' == tdObj.firstChild.nodeName){continue;} } ;\n");
       appendDirtyJavaScript("          }else if(td.firstChild.nodeType==3){\n");
       appendDirtyJavaScript("               tdObj=td;\n");
       appendDirtyJavaScript("          }\n");
       appendDirtyJavaScript("          var temp=tdObj.innerHTML;\n");
       appendDirtyJavaScript("          if(temp.strLen()>"+maxLength+")\n");
       appendDirtyJavaScript("             tdObj.innerHTML=temp.subCHStr(0,"+(maxLength-1)+")+'...';\n");
       appendDirtyJavaScript("          else\n");
       appendDirtyJavaScript("             tdObj.innerHTML=temp;\n");
       appendDirtyJavaScript("   }\n");
       appendDirtyJavaScript("}\n");
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
