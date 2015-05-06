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
*  File        : WorkTimePeriodOvw.java    
*  Modified    : 2001-03-13 - Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool- 2001-03-08  - Created Using the ASP file WorkTimePeriodOvw.asp
*                   2001-08-21  CHCR Removed depreciated methods.
*    Raselk       - 2004-02-03  - modified okFind() to make it populate with multiple record when data transferdfrom other pages.
*                                 removed clone(),doReset(). 
* ----------------------------------------------------------------------------
 * New Comments:
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkTimePeriodOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.WorkTimePeriodOvw");

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
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPQuery q;
   private String n;

   //===============================================================
   // Construction 
   //===============================================================
   public WorkTimePeriodOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
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
      else if(!mgr.isEmpty(mgr.getQueryStringValue("PERIOD")))
      {
         String period_=mgr.readValue("PERIOD");
	 okFind1(period_);
      }		  
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
//---------------------------  CMDBAR FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","WORK_TIME_PERIOD_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

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
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());

      mgr.submit(trans);
      if ( headset.countRows() == 0 ) 
         mgr.showAlert(mgr.translate("APPSRWWORKTIMEPERIODOVWNODATA: No data found."));

      mgr.createSearchURL(headblk);
   }

   public void  okFind1(String period_)
   {
      ASPManager mgr = getASPManager();
      q = trans.addQuery(headblk);
      q.addWhereCondition("PERIOD = ?");
      q.addParameter("PERIOD", period_);
      q.includeMeta("ALL");
      mgr.submit(trans);
      if ( headset.countRows() == 0 ) 
         mgr.showAlert(mgr.translate("APPSRWWORKTIMEPERIODOVWNODATA: No data found."));
      mgr.createSearchURL(headblk);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("PERIOD");
      f.setSize(12);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("APPSRWWORKTIMEPERIODOVWPERIOD: Period");
      f.setUpperCase();
      f.setMaxLength(10);  
   
      f = headblk.addField("NAME");
      f.setSize(45);
      f.setMandatory();
      f.setLabel("APPSRWWORKTIMEPERIODOVWNAME: Name");
      f.setMaxLength(40);
   
      headblk.setView("WORK_TIME_PERIOD");
      headblk.defineCommand("WORK_TIME_PERIOD_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWWORKTIMEPERIODOVWHD: Period"));  
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
      return "APPSRWWORKTIMEPERIODOVWTITLE: Period";
   }

   protected String getTitle()
   {
      return "APPSRWWORKTIMEPERIODOVWTITLE: Period";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if(headlay.isVisible())
         appendToHTML(headlay.show());
   }
}
