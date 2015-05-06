/* 
*                 IFS Research & Development 
*
*  This program is protected by copyright law and by international
*  conventions. All licensing, renting, lending or copying (including
*  for private use), and all other use of the program, which is not
*  expressively permitted by IFS Research & Development (IFS), is a
*  violation of the rights of IFS. Such violations will be reported to the
*  appropriate authorities.
*
*  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
*  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
* ----------------------------------------------------------------------------
*  File        : WorkTimeCalendar.java 
*  Modified    :
*    Zahalk         2003-10-12  - Call ID 106476, Modified the translation TAG.
*                   2001-08-21  - CHCR Removed depreciated methods.
*    ASP2JAVA Tool  2001-03-08  - Created Using the ASP file WorkTimeCalendar.asp
* ----------------------------------------------------------------------------
 * New Comments:
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimeCalendar extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimeCalendar");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String errFlag;
   private String genFlag;
   private String val;
   private ASPBuffer buf;
   private String txt;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int headrowno;
   private String eventVal;
   private String genMsg;
   private String confirmGenMsg;
   private String excepCode;
   private String calendarId;
   private String sche;
   
   private String calId;

   //===============================================================
   // Construction 
   //===============================================================
   public WorkTimeCalendar(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      errFlag   = null;
      genFlag   = "FALSE";
      val   = null;
      buf   = null;
      txt   = null;
      q   = null;
      cmd   = null;
      data   = null;
      headrowno   = 0;
      eventVal   = null;
      genMsg   = null;
      confirmGenMsg   = null;
      excepCode   = null;
      calendarId   = null;
      sche   = null;
    
      calId   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      WorkTimeCalendar page = (WorkTimeCalendar)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.errFlag   = null;
      page.genFlag   = "FALSE";
      page.val   = null;
      page.buf   = null;
      page.txt   = null;
      page.q   = null;
      page.cmd   = null;
      page.data   = null;
      page.headrowno   = 0;
      page.eventVal   = null;
      page.genMsg   = null;
      page.confirmGenMsg   = null;
      page.excepCode   = null;
      page.calendarId   = null;
      page.sche   = null;
      
      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itemlay0 = page.itemblk0.getASPBlockLayout();
      page.f = page.getASPField(f.getName());

      page.calId   = null;

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();   
      calId = ctx.readValue("CALID",calId);
      errFlag = ctx.readValue("ERRFLG","true");
      genFlag = ctx.readValue("GENFLAG","FALSE");
	     
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("CALENDAR_ID")))
      {
         okFind();
         okFindITEM0();
      }
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();

      else if ( "1".equals(mgr.readValue("PERFORMCMD")) ) 
      {
         mgr.setPageExpiring();
         errFlag = "false";
         genFlag = "FALSE";
         performCommand();
      }
	  else if ( "1".equals(mgr.readValue("ERFLG")) ) 
      {
         genFlag = "FALSE";
      }
	 	     
      adjust();
      ctx.writeValue("ERRFLG",errFlag);
      ctx.writeValue("CALID",calId);
      ctx.writeValue("GENFLAG",genFlag);
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      val = mgr.readValue("VALIDATE");

      if ( "START_DATE".equals(val) ) 
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("START_DATE",mgr.readValue("START_DATE"));
   
         txt = mgr.readValue("START_DATE") +"^";
         mgr.responseWrite(txt);		
      }
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//---------------------------  CMDBAR  FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDARNODATA: No data found."));
         headset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
   
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","WORK_TIME_CALENDAR_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR ITEM FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.addWhereCondition("CALENDAR_ID = ?");
      q.addParameter("CALENDAR_ID", headset.getRow().getValue("CALENDAR_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
   
      headset.goTo(headrowno);
      trans.clear();
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("CALENDAR_ID = ?");
      q.addParameter("CALENDAR_ID", headset.getRow().getValue("CALENDAR_ID"));
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
      headset.goTo(headrowno);
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM0","WORK_TIME_CALENDAR_DESC_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_CALENDAR_ID",headset.getRow().getValue("CALENDAR_ID"));
      itemset0.addRow(data);
   }
   
   public void saveReturn()
   {
	   ASPManager mgr = getASPManager();
	   trans.clear();
	   
	   int currrow = headset.getCurrentRowNo();
	   
	   headset.changeRow();
	   
	   mgr.submit(trans);
	   headset.goTo(currrow);
	   
	   headlay.setLayoutMode(headlay.getHistoryMode()); 
	  
	   okFindITEM0();

   }

//-----------------------------------------------------------------------------
//---------------------------  CUSTOM FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  generateCalendar()
   {
     ASPManager mgr = getASPManager();

     if (headlay.isMultirowLayout())
        headset.goTo(headset.getRowSelected());
   
     eventVal = headset.getRow().getValue("OBJEVENTS");
     calId = headset.getRow().getValue("CALENDAR_ID");
     genMsg = mgr.translate("APPSRWWORKTIMECALENDARGENMSG: You are about to start a background job that generates the selected calendar. This process might affect a number of orders and planned operations etc that possibly needs to be replanned or reconsidered etc when this operation has finished. After the generation - be sure to check the consequences before any actions are taken.Start the generating process now?");
     confirmGenMsg = mgr.translate("APPSRWWORKTIMECALENDARCONGENMSG: Are you really sure you want to start the calendar generation now and is this a proper time to do that?, Start now.");

	   
		if  ( eventVal.indexOf("SetGenerated")!=-1) 
		{
			genFlag = "TRUE";     
			
			String xx = "SELECT COUNT(SCHEDULE)  FROM WORK_TIME_CALENDAR_DESC WHERE CALENDAR_ID =?";
		    trans.clear();
	        trans.addQuery("ITEMS",xx).
                   addParameter("CALENDAR_ID", calId);
            trans = mgr.perform(trans);
		
            int yy = (int)trans.getNumberValue("ITEMS/DATA/COUNT(SCHEDULE)");
            if (isNaN(yy)) yy=0;
	        if (yy == 0)
	        {	
		           mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDARCANNOTGEN: Cannot Generate, The Calendar is empty."));  
		           genFlag = "FALSE";
	        }
		}	
		else
		{
        mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDARCANNOT: Cannot Generate Calendar for selected record."));  
		genFlag = "FALSE";
		}	
	
   }


   public void  performCommand()
   {
     ASPManager mgr = getASPManager();

     errFlag = "false";
	 genFlag = "FALSE";
	 cmd = trans.addCustomCommand("CBRL","Work_Time_Calendar_API.Set_Calendar_Generated");
     cmd.addParameter("CALENDAR_ID",calId);
     cmd.addParameter("FORWARD_CHANGES","1");  
     trans = mgr.perform(trans);
   }


   public void  scheduleExcep()
   {
     ASPManager mgr = getASPManager();

     if (headlay.isMultirowLayout())
        headset.goTo(headset.getRowSelected());
   
     if ( mgr.isEmpty(headset.getRow().getValue("EXCEPTION_CODE")) )
        mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDARCANNOTSHEDEX: Cannot perform Schedule Exception for selected record."));  
     else
     {
        excepCode = headset.getRow().getValue("EXCEPTION_CODE");  
        mgr.redirectTo("WorkTimeException.page?EXCEPTION_CODE="+excepCode);  
     }   
   }


   public void  ovwGenCal()
   {
     ASPManager mgr = getASPManager();

     if (headlay.isMultirowLayout())
        headset.goTo(headset.getRowSelected());
   
     calendarId = headset.getRow().getValue("CALENDAR_ID"); 
     mgr.redirectTo("WorkTimeCounterOvw.page?CALENDAR_ID="+calendarId);
   }


   public void  schedule()
   {
     ASPManager mgr = getASPManager();

     if (itemlay0.isMultirowLayout())
        itemset0.goTo(itemset0.getRowSelected());
   
     sche = itemset0.getRow().getValue("SCHEDULE"); 
     mgr.redirectTo("WorkTimeSchedule.page?SCHEDULE="+sche);  
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
   
      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("OBJSTATE");
      f.setHidden();
   
      f = headblk.addField("OBJEVENTS");
      f.setHidden();
   
      f = headblk.addField("CALENDAR_ID");
      f.setSize(14);
      f.setReadOnly();
      f.setInsertable();
      f.setMandatory();
      f.setUpperCase();
      f.setMaxLength(10);   
      f.setLabel("APPSRWWORKTIMECALENDARCALENDAR_ID: Calendar Id");
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(39);
      f.setMandatory();
      f.setMaxLength(40);
      f.setLabel("APPSRWWORKTIMECALENDARDESCRIPTION: Description");
   
      f = headblk.addField("STATE");
      f.setSize(14);
      f.setReadOnly();
      f.setMaxLength(253);  
      f.setLabel("APPSRWWORKTIMECALENDARSTATE: State");
   
      f = headblk.addField("EXCEPTION_CODE");
      f.setSize(14);
      f.setDynamicLOV("WORK_TIME_EXCEPTION",600,445);
      f.setLabel("APPSRWWORKTIMECALENDAREXCEPTION_CODE: Exception Code");
      f.setMaxLength(8);
      f.setUpperCase();
   
      f = headblk.addField("EXCEPTIONDESC");
      f.setSize(54);
      f.setLabel("APPSRWWORKTIMECALENDAREXCEPTIONDESC: Description");
      f.setReadOnly();
      f.setMaxLength(2000);   
      f.setFunction("Work_Time_Exception_API.Get_Description(:EXCEPTION_CODE)");
      mgr.getASPField("EXCEPTION_CODE").setValidation("EXCEPTIONDESC");
   
      f = headblk.addField("FORWARD_CHANGES","Number");
      f.setHidden(); 
      f.setFunction("''");
   
      headblk.setView("WORK_TIME_CALENDAR");
      headblk.defineCommand("WORK_TIME_CALENDAR_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMECALENDARHD: Calendar"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headbar.addCustomCommand("none","");
      headbar.addCustomCommand("generateCalendar",mgr.translate("APPSRWWORKTIMECALENDARGENCAL: Generate Calendar..."));
      headbar.addCustomCommand("scheduleExcep",mgr.translate("APPSRWWORKTIMECALENDARSCHEXC: Schedule Exception..."));
      headbar.addCustomCommand("ovwGenCal",mgr.translate("APPSRWWORKTIMECALENDAROVWGENCAL: Overview - Generated Calendar..."));
   
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields()");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(3);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk0.addField("ITEM0_CALENDAR_ID");
      f.setMandatory();
      f.setHidden();
      f.setDbName("CALENDAR_ID");
      f.setUpperCase();
   
      f = itemblk0.addField("SCHEDULE");
      f.setSize(15);
      f.setDynamicLOV("WORK_TIME_SCHEDULE",600,445);
      f.setMandatory();
      f.setMaxLength(8);
      f.setLabel("APPSRWWORKTIMECALENDARSCHEDULE: Schedule");
      f.setUpperCase();
   
      f = itemblk0.addField("SCHEDULEDESC");
      f.setSize(50);
      f.setMaxLength(2000);
      f.setReadOnly();
      f.setLabel("APPSRWWORKTIMECALENDARSCHEDULEDESC: Description");
      f.setFunction("Work_Time_Schedule_API.Get_Description(:SCHEDULE)");
      mgr.getASPField("SCHEDULE").setValidation("SCHEDULEDESC");
   
      f = itemblk0.addField("START_DATE","Date");
      f.setSize(18);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMECALENDARSTART_DATE: Start Date");
      f.setCustomValidation("START_DATE","START_DATE");
   
      f = itemblk0.addField("END_DATE","Date");
      f.setSize(18);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMECALENDAREND_DATE: End Date");
   
      itemblk0.setView("WORK_TIME_CALENDAR_DESC");
      itemblk0.defineCommand("WORK_TIME_CALENDAR_DESC_API","New__,Modify__,Remove__");
      itemset0 = itemblk0.getASPRowSet();
   
      itemblk0.setMasterBlock(headblk);
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableCommand(itembar0.FIND);
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
   
      itembar0.addCustomCommand("schedule",mgr.translate("APPSRWWORKTIMECALENDARSCH: Schedule..."));  
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("APPSRWWORKTIMECALENDARITM0: Calendar Desc"));  
      itemtbl0.setWrap();
   
      itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields()");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields()");
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "Calendar";
   }

   protected String getTitle()
   {
      return "APPSRWWORKTIMECALENDARTITLE: Calendar";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("APPSRWWORKTIMECALENDARTITLE: Calendar"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append("<input type=\"hidden\" name=\"PERFORMCMD\" value>\n");
      out.append("<input type=\"hidden\" name=\"ERFLG\" value>\n");
      out.append(mgr.startPresentation("Calendar"));
      out.append(headlay.show());
      if(itemlay0.isVisible())
      {
      out.append(itemlay0.show());
      }
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(genFlag);
      appendDirtyJavaScript("' == 'TRUE' && document.form.PERFORMCMD.value != '1')\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if ( confirm('");
      appendDirtyJavaScript(genMsg);
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("   {  \n");
      appendDirtyJavaScript("      if ( confirm('");
      appendDirtyJavaScript(confirmGenMsg);
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         document.form.PERFORMCMD.value = '1';\n");
      appendDirtyJavaScript("         submit();\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         document.form.PERFORMCMD.value = '0';\n");
	  appendDirtyJavaScript("         document.form.ERFLG.value = '1';\n");
	  appendDirtyJavaScript("         submit();\n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("         document.form.ERFLG.value = '1';\n");
	  appendDirtyJavaScript("         submit();\n");
      appendDirtyJavaScript("   }      \n");
      appendDirtyJavaScript("}\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
