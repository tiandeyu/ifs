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
*  File        : WorkTimeDayType.java 
*  Modified    : 2001-03-13 - Indra Rodrigo - Java conversion.
*			   : 2001-05-21 - inrolk - Added validation for Period  Call ID 65141.
*    ASP2JAVA Tool  2001-03-08  - Created Using the ASP file WorkTimeDayType.asp
*                   2001-08-21  CHCR Removed depreciated methods.
*    JEWI           2001-09-06  - Modified to show only the title in new mode.
*    VAGU           2001-10-16  - Added Security Handling for Actions
*    Raselk         2004-02-03  - modified to enable multirow select for RBM Period...
*    ThAblk         2004-02-27  - Added addCommandValidConditions to RBM Period... to make it 
*                                 disable in multi row mode when no record is selected.
* ----------------------------------------------------------------------------
 * New Comments:
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimeDayType extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimeDayType");


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
   private String val;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int currrow;
   private ASPBuffer buffer;
   private String titleToShow;
   private String row; 
   private boolean chksec;
   private boolean enable1;

   //===============================================================
   // Construction 
   //===============================================================
   public WorkTimeDayType(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      enable1 = ctx.readFlag("ENABLE1",false);
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else
      {
         startup();
      }
      chkAvailability();
      adjust();

      ctx.writeFlag("ENABLE1",enable1);
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  startup()
   {

      // Insert startup code here 
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
	  String txt="";
	  
      val = mgr.readValue("VALIDATE");
      ASPBuffer buf;
	  buf = mgr.newASPBuffer();
	  
	  trans.clear();
	  
	  if ( "FROM_TIME".equals(val) ) 
	  {
	     buf.addFieldItem("FROM_TIME",mgr.readValue("FROM_TIME") );
	     mgr.responseWrite(buf.getFieldValue("FROM_TIME") +"^" );
	  }
	  if ( "TO_TIME".equals(val) ) 
	  {
	     buf.addFieldItem("TO_TIME",mgr.readValue("TO_TIME") );
	     mgr.responseWrite(buf.getFieldValue("TO_TIME") +"^" );
	  }
	  if ("PERIOD".equals(val))
	  {	
		 cmd = trans.addCustomFunction("PERIODNAME","Work_Time_Period_API.Get_Name","SPERIODNAME");
         cmd.addParameter("PERIOD",mgr.readValue("PERIOD"));
           
         trans = mgr.validate(trans);
   
         String periodName = trans.getValue("PERIODNAME/DATA/SPERIODNAME");
   
         txt = (mgr.isEmpty(periodName)?"":periodName)+"^";
   
         mgr.responseWrite(txt);
	  }	
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR FUNCTIONS  ---------------------------------
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
         mgr.showAlert(mgr.translate("APPSRWWORKTIMEDAYTYPENODATA: No data found."));
         headset.clear();
      }
	  if (  headset.countRows() == 1 ) 
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		 okFindITEM0();     
      }       
   
      eval(headset.syncItemSets());
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","WORK_TIME_DAY_TYPE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("CONNECT_NEXT","FALSE");
      headset.addRow(data);   
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      currrow = headset.getCurrentRowNo();   
   
      q = trans.addQuery(itemblk0);
      q.addWhereCondition("DAY_TYPE = ?");
      q.addParameter("DAY_TYPE", headset.getValue("DAY_TYPE"));
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
      q.addWhereCondition("DAY_TYPE = ?");
      q.addParameter("DAY_TYPE", headset.getValue("DAY_TYPE"));
      q.setOrderByClause("to_date(to_char(FROM_TIME, 'HH24:MI'), 'HH24:MI')");   
      q.includeMeta("ALL");
   
      mgr.submit(trans);
   
      headset.goTo(currrow);
   
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
   
      cmd = trans.addEmptyCommand("ITEM0","WORK_TIME_DAY_TYPE_DESC_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_DAY_TYPE",headset.getRow().getValue("DAY_TYPE"));
      data.setFieldItem("RESERVED_TIME_DB","N");
      itemset0.addRow(data);
   
      headset.goTo(currrow);
   
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  calConnDayType()
   {
      ASPManager mgr = getASPManager();
	
	  if(headlay.isMultirowLayout())
      headset.goTo(headset.getRowSelected());
	  
      mgr.redirectTo("WorkTimeCalendar2Ovw.page?DAY_TYPE="+headset.getRow().getValue("DAY_TYPE"));
   }


   public void  dyaPeriod()
   {
      ASPManager mgr = getASPManager();
      if(itemlay0.isMultirowLayout())  
         itemset0.store();
      else
      {  itemset0.unselectRows();
         itemset0.selectRow();
      }

      mgr.transferDataTo("WorkTimePeriodOvw.page" ,itemset0.getSelectedRows("PERIOD") );

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
   
      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("DAY_TYPE");
      f.setSize(8);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();   
      f.setLabel("APPSRWWORKTIMEDAYTYPEDAY_TYPE: Day Type");
	  f.setUpperCase();
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(40);
      f.setLabel("APPSRWWORKTIMEDAYTYPEDESCRIPTION: Description");
   
      f = headblk.addField("NWORKINGTIME","Number");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("APPSRWWORKTIMEDAYTYPENWORKINGTIME: Working Time");
      f.setFunction("Work_Time_Day_Type_Desc_API.Get_Working_Minutes(:DAY_TYPE)");
   
      f = headblk.addField("NWORKINGPERIODS","Number");
      f.setSize(14);
      f.setReadOnly();   
      f.setLabel("APPSRWWORKTIMEDAYTYPENWORKINGPERIODS: Working Periods");
      f.setFunction("Work_Time_Day_Type_Desc_API.Get_Working_Periods(:DAY_TYPE)");
   
      f = headblk.addField("CONNECT_NEXT");
      f.setSize(5);
      f.setLabel("APPSRWWORKTIMEDAYTYPECONNECT_NEXT: Last Period Belongs to Next Day");
      f.setCheckBox("FALSE,TRUE");
   
      headblk.setView("WORK_TIME_DAY_TYPE");
      headblk.defineCommand("WORK_TIME_DAY_TYPE_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMEDAYTYPEHD: Day Type"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
   
      headbar.addCustomCommand("calConnDayType","APPSRWWORKTIMEDAYTYPECALLCONNDYAT: Calendars Connected to Day type...");
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk0.addField("ITEM0_DAY_TYPE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("DAY_TYPE");
      f.setUpperCase();
   
      f = itemblk0.addField("FROM_TIME","Time");
      f.setSize(11);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMEDAYTYPEFROM_TIME: From Time");
	  f.setCustomValidation("FROM_TIME","FROM_TIME");
   
      f = itemblk0.addField("TO_TIME","Time");
      f.setSize(11);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMEDAYTYPETO_TIME: To Time");
	  f.setCustomValidation("TO_TIME","TO_TIME");
   
      f = itemblk0.addField("PERIOD");
      f.setSize(14);
      f.setDynamicLOV("WORK_TIME_PERIOD",600,445);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMEDAYTYPEPERIOD: Period");
      f.setUpperCase();
	  f.setCustomValidation("PERIOD","SPERIODNAME");
   
      f = itemblk0.addField("SPERIODNAME");
      f.setSize(50);
      f.setReadOnly();
      f.setLabel("APPSRWWORKTIMEDAYTYPESPERIODNAME: Name");
      f.setFunction("Work_Time_Period_API.Get_Name(:PERIOD)");
   
      f = itemblk0.addField("RESERVED_TIME_DB");
      f.setSize(14);
      f.setLabel("APPSRWWORKTIMEDAYTYPERESERVED_TIME_DB: Reserved Time");
      f.setCheckBox("N,Y");
      f.setUpperCase();
   
      itemblk0.setView("WORK_TIME_DAY_TYPE_DESC");
      itemblk0.defineCommand("WORK_TIME_DAY_TYPE_DESC_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
   
      itemset0 = itemblk0.getASPRowSet();
   
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("APPSRWWORKTIMEDAYTYPEITM0: Day Type Desc"));  
      itemtbl0.setWrap();
      itemtbl0.enableRowSelect();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
   
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0"); 
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");   
   
      itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");
      itembar0.enableMultirowAction();
   
      itembar0.addCustomCommand("dyaPeriod","APPSRWWORKTIMEDAYTYPEYADPERIO: Period...");
      itembar0.addCommandValidConditions("dyaPeriod","DAY_TYPE","Disable","null");
   
      itembar0.enableCommand(itembar0.FIND);
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT); 
   }


   public void chkAvailability()
        {
         if(!chksec)
         {

         ASPManager mgr = getASPManager();
         
         ASPBuffer availObj;
         trans.clear();
         
         trans.addSecurityQuery("WORK_TIME_DAY_TYPE");
         trans.addPresentationObjectQuery("APPSRW/WorkTimeCalendar2Ovw.page");

         trans = mgr.perform(trans);

         availObj = trans.getSecurityInfo();
         
         if ( availObj.itemExists("WORK_TIME_DAY_TYPE") && availObj.namedItemExists("APPSRW/WorkTimeCalendar2Ovw.page") )
         enable1 = true; 
                  
         chksec = true;
         }
         if (!enable1)
         headbar.removeCustomCommand("calConnDayType");
        }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

       titleToShow = mgr.translate("APPSRWWORKTIMEDAYTYPEDAYTYTIL: Day Type ");
   
       if (headset.countRows()>0 && (headlay.isSingleLayout() || headlay.isEditLayout()) )
           titleToShow = titleToShow+"- "+headset.getRow().getValue("DAY_TYPE");    
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
      if(itemlay0.isVisible()){
      out.append(itemlay0.show());
      }
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
