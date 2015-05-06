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
*  File        : WorkTimeCalendar2Ovw.java 
*  Modified    : 2001-03-20 - Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool  2001-03-19  - Created Using the ASP file WorkTimeCalendar2Ovw.asp
*                   2001-08-21  - CHCR Removed depreciated methods.
* ----------------------------------------------------------------------------
* New Comments:
* 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
* 2006/09/13 sumelk Bug 59368, Corrected erroneous translation constants.  
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimeCalendar2Ovw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimeCalendar2Ovw");


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
   private String sDayType;
   private String sSchedule;
   private String sException;
   private ASPQuery q;
   private String titleToShow;

   //===============================================================
   // Construction 
   //===============================================================
   public WorkTimeCalendar2Ovw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      sDayType   = null;
      sSchedule   = null;
      sException   = null;
      q   = null;
      titleToShow   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      WorkTimeCalendar2Ovw page = (WorkTimeCalendar2Ovw)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.sDayType   = null;
      page.sSchedule   = null;
      page.sException   = null;
      page.q   = null;
      page.titleToShow   = null;
      
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
   
      sDayType = "";
      sSchedule = "";
      sException = "";
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFindTransfered();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFindTransfered();
      }   
      else if(!mgr.isEmpty(mgr.getQueryStringValue("DAY_TYPE")))
      {
         sDayType = mgr.readValue("DAY_TYPE");
         okFind();
      } 
      else if(!mgr.isEmpty(mgr.getQueryStringValue("EXCEPTION_CODE")))
      {
         sException = mgr.readValue("EXCEPTION_CODE");
         okFindException();
      }   
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SCHEDULE")))
      {
         sSchedule = mgr.readValue("SCHEDULE");
         okFindSchedule();
      }    
      else
      {
         startup();
      }
   
      adjust();
   
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  startup()
   {

      // Insert startup code here 
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

   public void  okFindException()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      q = trans.addEmptyQuery(headblk);
   
      q.addWhereCondition("EXCEPTION_CODE = ?"); 
      q.addParameter("EXCEPTION_CODE", "S", "IN", sException);
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDAR2OVWNODATA: No data found."));
         headset.clear();
      }
   
   }


   public void  okFindSchedule()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      q = trans.addEmptyQuery(headblk);
   
      q.addWhereCondition("CALENDAR_ID in (select distinct CALENDAR_ID from WORK_TIME_CALENDAR_DESC where SCHEDULE = ?)");
      q.addParameter("SCHEDULE", "S", "IN", sSchedule);
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDAR2OVWNODATA: No data found."));
         headset.clear();
      }
   
   }


   public void  okFindTransfered()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      q = trans.addEmptyQuery(headblk);
   
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
   
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDAR2OVWNODATA: No data found."));
         headset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
   
      if (!mgr.isEmpty(sDayType))
      q.addWhereCondition("CALENDAR_ID IN (select distinct CALENDAR_ID from WORK_TIME_CALENDAR_DESC "+
                          "where SCHEDULE in (select distinct SCHEDULE from WORK_TIME_SCHEDULE_DESC "+
                          "where DAY_TYPE in (select distinct DAY_TYPE from WORK_TIME_DAY_TYPE "+
                          "where DAY_TYPE = ?)))");
      q.addParameter("DAY_TYPE", "S", "IN", sDayType);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
   
      if (!mgr.isEmpty(sDayType))
      q.addWhereCondition("CALENDAR_ID IN (select distinct CALENDAR_ID from WORK_TIME_CALENDAR_DESC "+
                          "where SCHEDULE IN (select distinct SCHEDULE from WORK_TIME_SCHEDULE_DESC "+
                          "where DAY_TYPE IN (select distinct DAY_TYPE from WORK_TIME_DAY_TYPE "+
                          "where DAY_TYPE = ?)))");
      q.addParameter("DAY_TYPE", "S", "IN", sDayType);
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
   
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWWORKTIMECALENDAR2OVWNODATA: No data found."));
         headset.clear();
      }
   
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
      f.setSize(10);
      f.setLabel("APPSRWWORKTIMECALENDAR2OVWCALENDARID: Calendar ID");
      f.setReadOnly();
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(40);
      f.setLabel("APPSRWWORKTIMECALENDAR2OVWDESCRIPTION: Description");
      f.setReadOnly();
   
      f = headblk.addField("STATE");
      f.setSize(30);
      f.setLabel("APPSRWWORKTIMECALENDAR2OVWSTATE: State");
      f.setReadOnly();   
   
      headblk.setView("WORK_TIME_CALENDAR");
      headblk.defineCommand("WORK_TIME_CALENDAR_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMECALENDAR2OVWHD: Calendars Connected to Schedule"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headbar.enableCommand(headbar.FIND);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.DELETE);
              
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();

       titleToShow = "";
   
       if (!mgr.isEmpty(sDayType))
       {
          titleToShow = mgr.translate("APPSRWWORKTIMECALENDAR2OVWCALCONDATTY: Calendars Connected to Schedule");
          titleToShow = titleToShow+" - "+sDayType;
       }   
       else if (!mgr.isEmpty(sSchedule))  
       {
          titleToShow = mgr.translate("APPSRWWORKTIMECALENDAR2OVWCALCONDATTY: Calendars Connected to Schedule");
          titleToShow = titleToShow+" - "+sSchedule;    
       }
       else if (!mgr.isEmpty(sException))
       {
          titleToShow = mgr.translate("APPSRWWORKTIMECALENDAR2OVWCALCONDATTYEXEC: Calendars Connected to Schedule Exception");
          titleToShow = titleToShow+" - "+sException;    
       }
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
