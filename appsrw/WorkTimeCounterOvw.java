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
*  File        : WorkTimeCounterOvw.java 
*  Modified    :
*                   2003-10-12  Zahalk - Call ID 106477, Did some changes to show all the records.
*                   2001-08-21  CHCR Removed depreciated methods.
*    ASP2JAVA Tool  2001-03-07  - Created Using the ASP file WorkTimeCounterOvw.asp
*                   08-03-2001  chdelk   Converted to Java
* ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimeCounterOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimeCounterOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private ASPQuery q;
   private String n;
   private String calendarId;
   private String dayType;

   //===============================================================
   // Construction 
   //===============================================================
   public WorkTimeCounterOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      val   = null;
      q   = null;
      n   = null;
      calendarId   = null;
      dayType   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      WorkTimeCounterOvw page = (WorkTimeCounterOvw)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.val   = null;
      page.q   = null;
      page.n   = null;
      page.calendarId   = null;
      page.dayType   = null;
      
      // Cloning immutable attributes
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      trans = mgr.newASPTransactionBuffer();   
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("CALENDAR_ID")))
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
   
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//---------------------------  CMDBAR  FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      n = headset.getRow().getValue("N");
      headlay.setCountValue(toInt(n));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,headblk);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMECOUNTEROVWNODATA: No data found."));
         headset.clear();      
      }
      mgr.createSearchURL(headblk);
   }

//-----------------------------------------------------------------------------
//---------------------------  CUSTOM FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  none()
   {
      ASPManager mgr = getASPManager();

      mgr.showAlert(mgr.translate("APPSRWWORKTIMECOUNTEROVWNONE: No RMB method has been selected."));
   
   }


   public void  calendar()
   {
     ASPManager mgr = getASPManager();

     if (headlay.isMultirowLayout())
        headset.goTo(headset.getRowSelected());
   
     calendarId = headset.getRow().getValue("CALENDAR_ID"); 
     mgr.redirectTo("WorkTimeCalendar.page?CALENDAR_ID="+calendarId);        
   }


   public void  dayType()
   {
     ASPManager mgr = getASPManager();

     if (headlay.isMultirowLayout())
        headset.goTo(headset.getRowSelected());
   
     dayType = headset.getRow().getValue("DAY_TYPE"); 
     mgr.redirectTo("WorkTimeDayType.page?DAY_TYPE="+dayType);      
   }


   public void  genCalDet()
   {
     ASPManager mgr = getASPManager();

     if (headlay.isMultirowLayout())
        headset.goTo(headset.getRowSelected());
   
     calendarId = headset.getRow().getValue("CALENDAR_ID"); 
	 String counter = headset.getRow().getValue("COUNTER"); 
     mgr.redirectTo("WorkTimeCounterDescOvw.page?CALENDAR_ID="+calendarId+"&COUNTER="+counter);      
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("CALENDAR_ID");
      f.setSize(11);
      f.setDynamicLOV("WORK_TIME_CALENDAR",600,445);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMECOUNTEROVWCALENDAR_ID: Calendar Id");
      f.setUpperCase();
   
      f = headblk.addField("SDESCRIPTION");
      f.setSize(39);
      f.setLabel("APPSRWWORKTIMECOUNTEROVWSDESCRIPTION: Description");
      f.setFunction("Work_Time_Calendar_API.Get_Description(:CALENDAR_ID)");
   
      f = headblk.addField("WORK_DAY","Date");
      f.setSize(8);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMECOUNTEROVWWORK_DAY: Work Day");
   
      f = headblk.addField("SWEEKDAY");
      f.setSize(14);
      f.setLabel("APPSRWWORKTIMECOUNTEROVWSWEEKDAY: Weekday");
      f.setFunction("Work_Time_Calendar_API.Get_Week_Day(:WORK_DAY)");
   
      f = headblk.addField("WORKING_TIME","Number");
      f.setSize(11);
      f.setLabel("APPSRWWORKTIMECOUNTEROVWWORKING_TIME: Working Time");
   
      f = headblk.addField("WORKING_PERIODS","Number");
      f.setSize(11);
      f.setLabel("APPSRWWORKTIMECOUNTEROVWWORKING_PERIODS: Working Periods");
   
      f = headblk.addField("DAY_TYPE");
      f.setSize(11);
      f.setDynamicLOV("WORK_TIME_DAY_TYPE",600,445);
      f.setLabel("APPSRWWORKTIMECOUNTEROVWDAY_TYPE: Day Type");
      f.setUpperCase();
   
      f = headblk.addField("SDAYTYPEDESCRIPTION");
      f.setSize(39);
      f.setLabel("APPSRWWORKTIMECOUNTEROVWSDAYTYPEDESCRIPTION: Description");
      f.setFunction("Work_Time_Day_Type_API.Get_Description(:DAY_TYPE)");
      mgr.getASPField("DAY_TYPE").setValidation("SDAYTYPEDESCRIPTION");
   
      f = headblk.addField("COUNTER","Number");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setLabel("APPSRWWORKTIMECOUNTEROVWCOUNTER: Counter");
   
      headblk.setView("WORK_TIME_COUNTER");
      headblk.defineCommand("WORK_TIME_COUNTER_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DELETE);
   
      headbar.addCustomCommand("calendar",mgr.translate("APPSRWWORKTIMECOUNTEROVWCAL: Calendar..."));
      headbar.addCustomCommand("none","-------------------------------------------");   
      headbar.addCustomCommand("genCalDet",mgr.translate("APPSRWWORKTIMECOUNTEROVWGENCALDATE: Overview - Generated Calendar Details..."));   
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMECOUNTEROVWHD: Generated Calendar"));  
      headtbl.setWrap();
   
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWWORKTIMECOUNTEROVWTITLE: Overview - Generated Calendar";
   }

   protected String getTitle()
   {
      return "APPSRWWORKTIMECOUNTEROVWTITLE: Overview - Generated Calendar";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if(headlay.isVisible()){
      appendToHTML(headlay.show());
      }
   }

}
