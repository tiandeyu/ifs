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
*  File        : WorkTimeSchedule.java       
*  Modified    : 2001-03-13 - Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool  2001-03-08  - Created Using the ASP file WorkTimeSchedule.asp
*    CHCRLK         2001-05-17  - Code Review. 
*    JEWI           2001-09-06  - Modified to show only the title in new mode.
*    Raselk       - 2004-02-03  - Modified to enable multirow select for RMB DayType... & removed clone() ,doReset().
* ----------------------------------------------------------------------------
 * New Comments:
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimeSchedule extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimeSchedule");
   
   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
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
   private String val;
   private ASPCommand cmd;
   private String sDayTypeDesc;
   private String nWorkinTime;
   private ASPQuery q;
   private ASPBuffer data;
   private int currrow;
   private ASPBuffer buffer;
   private String titleToShow;
   private String txt;  
   private String row;  

   //===============================================================
   // Construction 
   //===============================================================
   public WorkTimeSchedule(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }
 
  
   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      trans = mgr.newASPTransactionBuffer();
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SCHEDULE")))
      {
         String shedu =(mgr.getQueryStringValue("SCHEDULE"));
	 okFind2(shedu);
      } 
      adjust();
   }
 
//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
   
      if ( "DAY_TYPE".equals(val) ) 
      {
         cmd = trans.addCustomFunction("SDAYCRION","Work_Time_Day_Type_API.Get_Description","SDAYTYPEDESCRIPTION");
         cmd.addParameter("DAY_TYPE");
   
         cmd = trans.addCustomFunction("NWOIME","Work_Time_Day_Type_Desc_API.Get_Working_Minutes","NWORKINGTIME");
         cmd.addParameter("DAY_TYPE");
   
         trans = mgr.validate(trans);
   
         sDayTypeDesc = trans.getValue("SDAYCRION/DATA/SDAYTYPEDESCRIPTION");
         int nWorkinTime1 = (int)trans.getNumberValue("NWOIME/DATA/NWORKINGTIME");
		 if ( isNaN(nWorkinTime1)) nWorkinTime1=0;
		 nWorkinTime = mgr.getASPField("NWORKINGTIME").formatNumber(nWorkinTime1);
   
         txt = (mgr.isEmpty(sDayTypeDesc) ? "":sDayTypeDesc)+ "^"+(mgr.isEmpty(nWorkinTime) ? "":nWorkinTime)+ "^";
         mgr.responseWrite(txt);
   
      }
   
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
   
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMESCHEDULENODATA: No data found."));
         headset.clear();
      }
      else if ( headset.countRows() == 1 ) 
      {
         okFindITEM0();     
      } 
   
      eval(headset.syncItemSets());
   }

   public void okFind2(String sched)
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.addWhereCondition("SCHEDULE = ?");
      q.addParameter("SCHEDULE", sched);
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMESCHEDULENODATA: No data found."));
         headset.clear();
      }
      else if ( headset.countRows() == 1 ) 
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT); 
	 okFindITEM0();     
      } 
      eval(headset.syncItemSets());
   }

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","WORK_TIME_SCHEDULE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);   
   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      currrow = headset.getCurrentRowNo();   
   
      q = trans.addQuery(itemblk0);
      q.addWhereCondition("SCHEDULE = ?");
      q.addParameter("SCHEDULE", headset.getValue("SCHEDULE"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
   
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
      headset.goTo(currrow);     
   }

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk0);
      q.addWhereCondition("SCHEDULE = ?");
      q.addParameter("SCHEDULE", headset.getValue("SCHEDULE"));
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      headset.goTo(currrow);
   }

   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
   
      cmd = trans.addEmptyCommand("ITEM0","WORK_TIME_SCHEDULE_DESC_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   
      headset.goTo(currrow);
   }

   public void saveReturn()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
	   
      currrow = headset.getCurrentRowNo();
	   
      headset.changeRow();
	   
      mgr.submit(trans);
      headset.goTo(currrow);
	   
      headlay.setLayoutMode(headlay.getHistoryMode()); 
	  
      okFindITEM0();
   }
  
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  calConnSchedule()
   {
      ASPManager mgr = getASPManager();
      if(headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      mgr.redirectTo("WorkTimeCalendar2Ovw.page?SCHEDULE="+headset.getRow().getValue("SCHEDULE"));
   }

   public void  connDayType()
   {
      ASPManager mgr = getASPManager();
      
      if (itemlay0.isMultirowLayout())
         itemset0.store();
      else
      {  itemset0.unselectRows();
         itemset0.selectRow();
      }

      mgr.transferDataTo("WorkTimeDayType.page",itemset0.getSelectedRows("DAY_TYPE") );


   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
   
      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("SCHEDULE");
      f.setSize(14);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("APPSRWWORKTIMESCHEDULESCHEDULE: Schedule");
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(43);
      f.setLabel("APPSRWWORKTIMESCHEDULEDESCRIPTION: Description");
   
      f = headblk.addField("PERIOD_LENGTH","Number");
      f.setSize(14);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMESCHEDULEPERIODLENGTH: Period Length");
   
      f = headblk.addField("PERIOD_START_DAY");
      f.setSize(25);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMESCHEDULEPERIODSTARTDAY: Period Start Day");
      f.setSelectBox();
      f.enumerateValues("WORK_TIME_WEEK_DAY_API");
   
      headblk.setView("WORK_TIME_SCHEDULE");
      headblk.defineCommand("WORK_TIME_SCHEDULE_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMESCHEDULEHD: Schedule"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
   
      headbar.addCustomCommand("calConnSchedule","APPSRWWORKTIMESCHEDULECALLCONNSCHED: Calendars Connected to Schedule...");
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk0.addField("ITEM0_SCHEDULE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("SCHEDULE");
      f.setUpperCase();
   
      f = itemblk0.addField("PERIOD_POS","Number");
      f.setSize(9);
      f.setLabel("APPSRWWORKTIMESCHEDULEPERIODPOS: Day No");
      f.setReadOnly();
   
      f = itemblk0.addField("DAY_TYPE");
      f.setSize(11);
      f.setDynamicLOV("WORK_TIME_DAY_TYPE",600,445);
      f.setLabel("APPSRWWORKTIMESCHEDULEDAYTYPE: Day Type");
      f.setUpperCase();
      f.setCustomValidation("DAY_TYPE","SDAYTYPEDESCRIPTION,NWORKINGTIME");
   
      f = itemblk0.addField("SDAYTYPEDESCRIPTION");
      f.setSize(50);
      f.setReadOnly();   
      f.setLabel("APPSRWWORKTIMESCHEDULESDAYTYPEDESCRIPTION: Description");
      f.setFunction("Work_Time_Day_Type_API.Get_Description(:DAY_TYPE)");
   
      f = itemblk0.addField("NWORKINGTIME","Number");
      f.setSize(14);
      f.setReadOnly();   
      f.setLabel("APPSRWWORKTIMESCHEDULENWORKINGTIME: Working Time");
      f.setFunction("Work_Time_Day_Type_Desc_API.Get_Working_Minutes(:DAY_TYPE)");   
   
      itemblk0.setView("WORK_TIME_SCHEDULE_DESC");
      itemblk0.defineCommand("WORK_TIME_SCHEDULE_DESC_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
   
      itemset0 = itemblk0.getASPRowSet();
   
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("APPSRWWORKTIMESCHEDULEITM0: Schedule Desc"));  
      itemtbl0.setWrap();
      itemtbl0.enableRowSelect();
      
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableMultirowAction();
      itembar0.addCustomCommand("connDayType","APPSRWWORKTIMESCHEDULESDATYPEES: Day Type...");
   
      itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");
   
      itembar0.enableCommand(itembar0.FIND);
      itembar0.disableCommand(itembar0.NEWROW);
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itembar0.disableCommand(itembar0.DELETE);
   
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");  
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      titleToShow = mgr.translate("APPSRWWORKTIMESCHEDULETILSHEDSHOW: Schedule ");
   
      if (headset.countRows()>0 && (headlay.isSingleLayout() || headlay.isEditLayout()))
         titleToShow = titleToShow+" - "+headset.getRow().getValue("SCHEDULE");
   }

//===============================================================
//  HTML
//===============================================================
   
   protected String getDescription()
   {
      return titleToShow;
   }

   protected String getTitle()
   {
      return titleToShow;
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(titleToShow));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation(titleToShow));
      out.append(headlay.show());
      if(itemlay0.isVisible())
         out.append(itemlay0.show());
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
