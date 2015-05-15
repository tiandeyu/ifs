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
* File                          :
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    : Automatically generated by IFS/Design
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.hsemaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import java.io.UnsupportedEncodingException;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class HseMonthlyReport extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HseMonthlyReport");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock hse_monthly_report_line_blk;
   private ASPRowSet hse_monthly_report_line_set;
   private ASPCommandBar hse_monthly_report_line_bar;
   private ASPTable hse_monthly_report_line_tbl;
   private ASPBlockLayout hse_monthly_report_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HseMonthlyReport (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ID")) )
         okFind();
      else 
         okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("HSEMONTHLYREPORTNODATA: No data found.");
         headset.clear();
      }
      eval( hse_monthly_report_line_set.syncItemSets() );
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }



   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","HSE_MONTHLY_REPORT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------


   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(hse_monthly_report_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,hse_monthly_report_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","HSE_MONTHLY_REPORT_LINE_API.New__",hse_monthly_report_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      hse_monthly_report_line_set.addRow(data);
   }

   public void validate() {
      // TODO Auto-generated method stub
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String val = mgr.readValue("VALIDATE");
//      String PRICE = "";
      
      
      if ("CONTRACT_ID".equals(val)) {
         cmd = trans.addCustomFunction("GETCONTRACTNAME", 
               "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_NAME");
         cmd.addParameter("PROJ_NO,CONTRACT_ID");
         
         cmd = trans.addCustomFunction("GETSECONDSIDE", 
               "PROJECT_CONTRACT_API.Get_Secend_Side", "SECOND_SIDE");
         cmd.addParameter("PROJ_NO,CONTRACT_ID");
   
         trans = mgr.validate(trans);
         String contractName = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         String SECONDSIDE = trans.getValue("GETSECONDSIDE/DATA/SECOND_SIDE");
         trans.clear();
         cmd = trans.addCustomFunction("GETSECONDSIDENAME", 
               "SUPPLIER_INFO_API.GET_NAME", "SECOND_SIDE_NAME");
         cmd.addParameter("SECOND_SIDE",SECONDSIDE);
         
         trans = mgr.validate(trans);
         
         String SECONDSIDENAME = trans.getValue("GETSECONDSIDENAME/DATA/SECOND_SIDE_NAME");

         String txt = ((mgr.isEmpty(contractName)) ? "" : contractName ) + "^"
            +  ((mgr.isEmpty(SECONDSIDE)) ? "" : SECONDSIDE ) + "^" 
            +  ((mgr.isEmpty(SECONDSIDENAME)) ? "" : SECONDSIDENAME ) + "^";
         
         mgr.responseWrite(txt);
      }
      mgr.endResponse();
   }


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setLabel("HSEMONTHLYREPORTPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC( :PROJ_NO)").
              setLabel("HSEMONTHLYREPORTGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setLabel("HSEMONTHLYREPORTID: Id").
              setSize(30);

      headblk.addField("CONTRACT_ID").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("HSEMONTHLYREPORTCONTRACTID: Contract Id").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_ID", "CONTRACT_NAME,SECOND_SIDE,SECOND_SIDE_NAME");
      headblk.addField("CONTRACT_NAME").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSEMONTHLYREPORTCONTRACTNAME: Contract Name").
              setSize(30);

      headblk.addField("SECOND_SIDE").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSEMONTHLYREPORTSECONDSIDE: Constraction Org No").
              setSize(30);

      headblk.addField("SECOND_SIDE_NAME").
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID))").
              setLabel("HSEMONTHLYREPORTSECONDSIDENAME: Constraction Org Name").
              setSize(30);

      headblk.addField("DEPT_PERSON").
              setInsertable().
              setLabel("HSEMONTHLYREPORTDEPTPERSON: Dept Person").
              setSize(30);

      headblk.addField("PM_PERSON").
              setInsertable().
              setLabel("HSEMONTHLYREPORTPMPERSON: Pm Person").
              setSize(30);

      headblk.addField("CREATE_PERSON").
              setInsertable().
              setLabel("HSEMONTHLYREPORTCREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setLabel("HSEMONTHLYREPORTCREATEPERSONNAME: Create Person Name").
              setSize(30).
              setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("HSEMONTHLYREPORTCREATETIME: Create Time").
              setSize(30);
      
      headblk.setView("HSE_MONTHLY_REPORT");
      headblk.defineCommand("HSE_MONTHLY_REPORT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printElectricityMonthReport", "HSEELECTRICITYMONTHREPORT: Print Electricity Month Report...");//TODO
      headbar.addCustomCommand("printElectricityYearReport", "HSEELECTRICITYYEARREPORT: Print Electricity Year Report...");//TODO
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSEMONTHLYREPORTTBLHEAD: Hse Monthly Reports");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("SECOND_SIDE_NAME");
      headlay.setSimple("CONTRACT_NAME");


      hse_monthly_report_line_blk = mgr.newASPBlock("ITEM1");
      hse_monthly_report_line_blk.addField("ITEM0_OBJID").
                                  setHidden().
                                  setDbName("OBJID");
      hse_monthly_report_line_blk.addField("ITEM0_OBJVERSION").
                                  setHidden().
                                  setDbName("OBJVERSION");
      hse_monthly_report_line_blk.addField("ITEM0_PROJ_NO").
                                  setDbName("PROJ_NO").
                                  setMandatory().
                                  setHidden().
                                  setLabel("HSEMONTHLYREPORTLINEITEM0PROJNO: Proj No").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("ITEM0_ID").
                                  setDbName("ID").
                                  setMandatory().
                                  setHidden().
                                  setLabel("HSEMONTHLYREPORTLINEITEM0ID: Id").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("LINE_NO").
                                  setHidden().
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINELINENO: Line No").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("ACCIDENT_TYPE").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEACCIDENTTYPE: Accident Type").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("NO_SERIOUS_DAYS").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINENOSERIOUSDAYS: No Serious Days").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("NO_DEATH_DAYS").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINENODEATHDAYS: No Death Days").
                                  setSize(30);
      //add start
      hse_monthly_report_line_blk.addField("SLIGHT_INJURY_NUM","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESLIGHTINJURYNUM: Slight Injury Num").
                                  setSize(30);

      hse_monthly_report_line_blk.addField("SLIGHT_ADD","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESLIGHTADD: Slight Add").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("SERIOUS_INJURY_NUM","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESERIOUSINJURYNUM: Serious Injury Num").
                                  setSize(30);

      hse_monthly_report_line_blk.addField("SERIOUS_ADD","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESERIOUSADD: Serious Add").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("DEATH_NUM","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEDEATHNUM: Death Num").
                                  setSize(30);

      hse_monthly_report_line_blk.addField("DEATH_ADD","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEDEATHADD: Death Add").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("SUB_CONTRACTOR_SLIGHT","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESUBCONTRACTORSLIGHT: Sub Contractor Slight").
                                  setSize(30);

      hse_monthly_report_line_blk.addField("SUB_SLIGHT_ADD","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESUBSLIGHTADD: Sub Slight Add").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("SUB_CONTRACTOR_SERIOUS","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESUBCONTRACTORSERIOUS: Sub Contractor Serious").
                                  setSize(30);

      hse_monthly_report_line_blk.addField("SUB_SERIOUS_ADD","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESUBSERIOUSADD: Sub Serious Add").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("SUB_CONTRACTOR_DEATH","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESUBCONTRACTORDEATH: Sub Contractor Death").
                                  setSize(30);

      hse_monthly_report_line_blk.addField("SUB_DEATH_ADD","Number").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINESUBDEATHADD: Sub Death Add").
                                  setSize(30);
      //add end
      hse_monthly_report_line_blk.addField("LAST_YEAR_SLIGHT").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINELASTYEARSLIGHT: Last Year Slight").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("LAST_YEAR_SERIOUS").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINELASTYEARSERIOUS: Last Year Serious").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("LAST_YEAR_DEATH").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINELASTYEARDEATH: Last Year Death").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("DIRECT_ECONOMIC_LOSSES").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEDIRECTECONOMICLOSSES: Direct Economic Losses").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("LOST_WORK_HOURS").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINELOSTWORKHOURS: Lost Work Hours").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("MONTH_INJURY_RATE").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEMONTHINJURYRATE: Month Injury Rate").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("YEAR_INJURY_RATE").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEYEARINJURYRATE: Year Injury Rate").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("YEAR","Number", "####").
                                  setInsertable().
                                  setLabel("HSEMONTHLYREPORTLINEYEAR: Year").
                                  setSize(30);
      hse_monthly_report_line_blk.addField("MONTH").
                                  setInsertable().
                                  enumerateValues("Budget_Month_API").
                                  setSelectBox().
                                  setLabel("HSEMONTHLYREPORTLINEMONTH: Month").
                                  setSize(30);
      hse_monthly_report_line_blk.setView("HSE_MONTHLY_REPORT_LINE");
      hse_monthly_report_line_blk.defineCommand("HSE_MONTHLY_REPORT_LINE_API","New__,Modify__,Remove__");
      hse_monthly_report_line_blk.setMasterBlock(headblk);
      hse_monthly_report_line_set = hse_monthly_report_line_blk.getASPRowSet();
      hse_monthly_report_line_bar = mgr.newASPCommandBar(hse_monthly_report_line_blk);
      hse_monthly_report_line_bar.defineCommand(hse_monthly_report_line_bar.OKFIND, "okFindITEM1");
      hse_monthly_report_line_bar.defineCommand(hse_monthly_report_line_bar.NEWROW, "newRowITEM1");
      hse_monthly_report_line_tbl = mgr.newASPTable(hse_monthly_report_line_blk);
      hse_monthly_report_line_tbl.setTitle("HSEMONTHLYREPORTLINEITEMHEAD1: HseMonthlyReportLine");
      hse_monthly_report_line_tbl.enableRowSelect();
      hse_monthly_report_line_tbl.setWrap();
      hse_monthly_report_line_lay = hse_monthly_report_line_blk.getASPBlockLayout();
      hse_monthly_report_line_lay.setDefaultLayoutMode(hse_monthly_report_line_lay.MULTIROW_LAYOUT);



   }



   public void  adjust()
   {
      // fill function body
   }

   
   //Report  Function
   public void  printElectricityMonthReport() throws FndException, UnsupportedEncodingException//TODO
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String id = headset.getValue("ID");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptElectricityMonthlyReport.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }
   
   
   public void  printElectricityYearReport() throws FndException, UnsupportedEncodingException//TODO
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String id = headset.getValue("ID");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptElectricityYearReport.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }
   
   
   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HSEMONTHLYREPORTDESC: Hse Monthly Report";
   }


   protected String getTitle()
   {
      return "HSEMONTHLYREPORTTITLE: Hse Monthly Report";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible()) {
          appendToHTML(headlay.show());
      }
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (hse_monthly_report_line_lay.isVisible())
          appendToHTML(hse_monthly_report_line_lay.show());

   }
}
