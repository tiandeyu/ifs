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
*  File        : WorkTimeException.java 
*  Modified    : 2001-03-13 - Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool  2001-03-08  - Created Using the ASP file WorkTimeException.asp
*    CHCRLK         2001-05-17  - Code Review.
*    JEWI           2001-09-06  - Modified to show only the title in new mode.
* ----------------------------------------------------------------------------
 * New Comments:
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimeException extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimeException");

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
   public WorkTimeException(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      val   = null;
      cmd   = null;
      sDayTypeDesc   = null;
      nWorkinTime   = null;
      q   = null;
      data   = null;
      currrow   = 0;
      buffer   = null;
      titleToShow   = null;
      txt   = null;
      row   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      WorkTimeException page = (WorkTimeException)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.val   = null;
      page.cmd   = null;
      page.sDayTypeDesc   = null;
      page.nWorkinTime   = null;
      page.q   = null;
      page.data   = null;
      page.currrow   = 0;
      page.buffer   = null;
      page.titleToShow   = null;
      
      // Cloning immutable attributes
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

      page.txt   = null;
      page.row   = null;

      return page;
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
      else if(!mgr.isEmpty(mgr.getQueryStringValue("EXCEPTION_CODE")))
      {
         String execode =(mgr.getQueryStringValue("EXCEPTION_CODE"));
	 okFind2(execode);
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
   
      if  ( "DAY_TYPE".equals(val) ) 
      {
         cmd = trans.addCustomFunction("SDAYRIPTION","Work_Time_Day_Type_API.Get_Description","SDAYTYPEDESCRIPTION");
         cmd.addParameter("DAY_TYPE");
   
         cmd = trans.addCustomFunction("NWOGTIME","Work_Time_Day_Type_Desc_API.Get_Working_Minutes","NWORKINGTIME");
         cmd.addParameter("DAY_TYPE");
   
         trans = mgr.validate(trans);
   
         sDayTypeDesc = trans.getValue("SDAYRIPTION/DATA/SDAYTYPEDESCRIPTION");
         int nWorkinTime1 = (int)trans.getNumberValue("NWOGTIME/DATA/NWORKINGTIME");
	 if (isNaN(nWorkinTime1)) nWorkinTime1 =0;
	    nWorkinTime = mgr.getASPField("NWORKINGTIME").formatNumber(nWorkinTime1);
   
         txt = (mgr.isEmpty(sDayTypeDesc) ? "" : (sDayTypeDesc))+ "^" + (mgr.isEmpty(nWorkinTime) ? "" : (nWorkinTime))+ "^";
         mgr.responseWrite(txt);
      }   
	  
      if  ( "EXCEPTION_DATE".equals(val) ) 
      {
         cmd = trans.addCustomFunction("WEEKDAY","Work_Time_Calendar_API.Get_Week_Day","SWEEKDAY");
         cmd.addParameter("EXCEPTION_DATE");
	 trans = mgr.validate(trans);
	 String weekday = trans.getValue("WEEKDAY/DATA/SWEEKDAY");
			  
	 txt = (mgr.isEmpty(weekday) ? "" : (weekday))+ "^" ;
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

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
   
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
   
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMEEXCEPTIONNODATA: No data found."));
         headset.clear();
      }
      else if ( headset.countRows() == 1 ) 
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT); 
	 okFindITEM0();     
      } 
   
      eval(headset.syncItemSets());
   }

   public void  okFind2(String execode)
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.addWhereCondition("EXCEPTION_CODE = ?");
      q.addParameter("EXCEPTION_CODE", execode);
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMEEXCEPTIONNODATA: No data found."));
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

      cmd = trans.addEmptyCommand("HEAD","WORK_TIME_EXCEPTION_API.New__",headblk);
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
      q.addWhereCondition("EXCEPTION_CODE = ?");
      q.addParameter("EXCEPTION_CODE", headset.getValue("EXCEPTION_CODE"));
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
      q.addWhereCondition("EXCEPTION_CODE = ?");
      q.addParameter("EXCEPTION_CODE", headset.getValue("EXCEPTION_CODE"));
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
      data.setFieldItem("ITEM0_EXCEPTION_CODE",headset.getRow().getValue("EXCEPTION_CODE"));
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

   public void  calConnException()
   {
      ASPManager mgr = getASPManager();
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      mgr.redirectTo("WorkTimeCalendar2Ovw.page?EXCEPTION_CODE="+headset.getRow().getValue("EXCEPTION_CODE"));
   }

   public void  connDayType()
   {
      ASPManager mgr = getASPManager();
		
      if(itemlay0.isMultirowLayout() )
         itemset0.goTo(itemset0.getRowSelected());
      buffer=mgr.newASPBuffer();
      ASPBuffer row=buffer.addBuffer("0");
      row.addItem("DAY_TYPE",itemset0.getRow().getValue("DAY_TYPE"));
      mgr.transferDataTo("WorkTimeDayType.page",buffer);   
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
   
      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("EXCEPTION_CODE");
      f.setSize(14);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("APPSRWWORKTIMEEXCEPTIONEXCEPTION_CODE: Exception Code");
      f.setUpperCase();
	  
      f = headblk.addField("DESCRIPTION");
      f.setSize(41);
      f.setLabel("APPSRWWORKTIMEEXCEPTIONDESCRIPTION: Description");
   
      headblk.setView("WORK_TIME_EXCEPTION");
      headblk.defineCommand("WORK_TIME_EXCEPTION_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMEEXCEPTIONHD: Report in Route Work Order"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
   
      headbar.addCustomCommand("calConnException","APPSRWWORKTIMEEXCEPTIONCALLCONNSCHED: Calendars Connected to Exception...");
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk0.addField("ITEM0_EXCEPTION_CODE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("EXCEPTION_CODE");
      f.setUpperCase();
   
      f = itemblk0.addField("EXCEPTION_DATE","Date");
      f.setSize(11);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMEEXCEPTIONEXCEPTION_DATE: Date");
      f.setCustomValidation("EXCEPTION_DATE","SWEEKDAY");
   
      f = itemblk0.addField("SWEEKDAY");
      f.setSize(18);
      f.setLabel("APPSRWWORKTIMEEXCEPTIONSWEEKDAY: Week Day");
      f.setFunction("Work_Time_Calendar_API.Get_Week_Day(:EXCEPTION_DATE)");
   
      f = itemblk0.addField("DAY_TYPE");
      f.setSize(11);
      f.setDynamicLOV("WORK_TIME_DAY_TYPE",600,445);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMEEXCEPTIONDAY_TYPE: Day Type");
      f.setUpperCase();
      f.setCustomValidation("DAY_TYPE","SDAYTYPEDESCRIPTION,NWORKINGTIME");
   
      f = itemblk0.addField("SDAYTYPEDESCRIPTION");
      f.setSize(50);
      f.setLabel("APPSRWWORKTIMEEXCEPTIONSDAYTYPEDESCRIPTION: Description");
      f.setFunction("Work_Time_Day_Type_API.Get_Description(DAY_TYPE)");
   
      f = itemblk0.addField("NWORKINGTIME","Number");
      f.setSize(14);
      f.setLabel("APPSRWWORKTIMEEXCEPTIONNWORKINGTIME: Working Time");
      f.setFunction("Work_Time_Day_Type_Desc_API.Get_Working_Minutes(DAY_TYPE)");
   
      itemblk0.setView("WORK_TIME_EXCEPTION_CODE");
      itemblk0.defineCommand("WORK_TIME_EXCEPTION_CODE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
   
      itemset0 = itemblk0.getASPRowSet();
   
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("APPSRWWORKTIMEEXCEPTIONITM0: Schedule Exception Code"));  
      itemtbl0.setWrap();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.addCustomCommand("connDayType","APPSRWWORKTIMEEXCEPTIONSDATYPEES: Day Type...");   
   
      itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");
   
      itembar0.enableCommand(itembar0.FIND);
   
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");  
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);    
   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();
	  
      mgr.getASPField("NWORKINGTIME").setReadOnly();
      mgr.getASPField("SWEEKDAY").setReadOnly(); 
      mgr.getASPField("SDAYTYPEDESCRIPTION").setReadOnly();   

      titleToShow = mgr.translate("APPSRWWORKTIMEEXCEPTIONTILSHEDSHOW: Schedule Exception ");
   
      if (headset.countRows()>0 && (headlay.isSingleLayout() || headlay.isEditLayout()))
         titleToShow = titleToShow+" - "+headset.getRow().getValue("EXCEPTION_CODE");
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
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
