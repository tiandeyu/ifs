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
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class HseSafetyDeposit extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HseSafetyDeposit");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HseSafetyDeposit (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      super.run();
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
         mgr.showAlert("HSESAFETYDEPOSITNODATA: No data found.");
         headset.clear();
      }
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

      cmd = trans.addEmptyCommand("HEAD","HSE_SAFETY_DEPOSIT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
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
//              setWfProperties().
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT").
              setLabel("HSESAFETYDEPOSITPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setReadOnly().
              setLabel("HSESAFETYDEPOSITGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("ID").
              setHidden().
              setInsertable().
              setLabel("HSESAFETYDEPOSITID: Id").
              setSize(30);

      headblk.addField("CONTRACT_ID").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("HSESAFETYDEPOSITCONTRACTID: Contract Id").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_ID", "CONTRACT_NAME,SECOND_SIDE,SECOND_SIDE_NAME");
      headblk.addField("CONTRACT_NAME").
              setReadOnly().
//              setWfProperties().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSESAFETYDEPOSITCONTRACTNAME: Contract Name").
              setSize(30);

      headblk.addField("SECOND_SIDE").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSESAFETYDEPOSITSECONDSIDE: Constraction Org No").
              setSize(30);

      headblk.addField("SECOND_SIDE_NAME").
              setReadOnly().
//              setWfProperties().
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID))").
              setLabel("HSESAFETYDEPOSITSECONDSIDENAME: Constraction Org Name").
              setSize(30);
//
//      headblk.addField("CONTRACT_ID").
//              setInsertable().
//              setDynamicLOV("PROJECT_CONTRACT").
//              setLabel("HSESAFETYDEPOSITCONTRACTID: Contract Id").
//              setSize(30);
//      headblk.addField("PROJECT_CONTRACT_CONTRACT_DESC").
//              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC ( :PROJ_NO,:CONTRACT_ID)").
//              setReadOnly().
//              setLabel("HSESAFETYDEPOSITPROJECTCONTRACTCONTRACTDESC: Project Contract Contract Desc").
//              setSize(30);
//      mgr.getASPField("CONTRACT_ID").setValidation("PROJECT_CONTRACT_CONTRACT_DESC");
      headblk.addField("APPLY_ORG").
              setInsertable().
              setLabel("HSESAFETYDEPOSITAPPLYORG: Apply Org").
              setSize(30);
      headblk.addField("APPLY_TIME","Date").
              setInsertable().
              setLabel("HSESAFETYDEPOSITAPPLYTIME: Apply Time").
              setSize(30);
      headblk.addField("DEPOSIT_NO").
              setInsertable().
              setLabel("HSESAFETYDEPOSITDEPOSITNO: Deposit No").
              setSize(30);
      headblk.addField("DETAINED_AMOUNT","Number").
              setInsertable().
              setLabel("HSESAFETYDEPOSITDETAINEDAMOUNT: Detained Amount").
              setSize(30);
      headblk.addField("PAID_AMOUNT","Number").
              setInsertable().
              setLabel("HSESAFETYDEPOSITPAIDAMOUNT: Paid Amount").
              setSize(30);
      headblk.addField("PAYMENT_METHOD").
              setInsertable().
              setLabel("HSESAFETYDEPOSITPAYMENTMETHOD: Payment Method").
              setSize(30);
      headblk.addField("APPLY_RETURN_AMOUNT","Number").
              setInsertable().
              setLabel("HSESAFETYDEPOSITAPPLYRETURNAMOUNT: Apply Return Amount").
              setSize(30);
      headblk.addField("APPLY_CONTENT").
              setInsertable().
              setHeight(3).
              setLabel("HSESAFETYDEPOSITAPPLYCONTENT: Apply Content").
              setSize(120);
      headblk.addField("NOTE").
              setInsertable().
              setHeight(3).
              setLabel("HSESAFETYDEPOSITNOTE: Note").
              setSize(120);
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("HSESAFETYDEPOSITCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setLabel("HSESAFETYDEPOSITCREATEPERSONNAME: Create Person Name").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("HSESAFETYDEPOSITCREATETIME: Create Time").
              setSize(30);
      
      headblk.addField("FLOW_TITLE").
      setWfProperties().
      setReadOnly().
      setHidden().
      setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_ID)").
      setLabel("FLOWTITLE: Flow Title").
      setSize(30);
     
      headblk.setView("HSE_SAFETY_DEPOSIT");
      headblk.defineCommand("HSE_SAFETY_DEPOSIT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "HSESAFETYDEPOSITPRINTREPORT: Print Report...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSESAFETYDEPOSITTBLHEAD: Hse Safety Deposits");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
//      headlay.setSimple("PROJECT_CONTRACT_CONTRACT_DESC");
      headlay.setDataSpan("APPLY_RETURN_AMOUNT", 5);
      headlay.setDataSpan("APPLY_CONTENT", 5);
      headlay.setDataSpan("NOTE", 5);
      headlay.setSimple("SECOND_SIDE_NAME");
      headlay.setSimple("CONTRACT_NAME");
 



   }



   public void  adjust() throws FndException
   {
      // fill function body
      super.adjust();
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void  printReport() throws FndException, UnsupportedEncodingException
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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptHseSafetyDeposit.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
    }   
   
   protected String getDescription()
   {
      return "HSESAFETYDEPOSITDESC: Hse Safety Deposit";
   }


   protected String getTitle()
   {
      return "HSESAFETYDEPOSITTITLE: Hse Safety Deposit";
   }


   protected void printContents() throws FndException
   {
      super.printContents();
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
   @Override
   protected ASPBlock getBizWfBlock() {
      // TODO Auto-generated method stub
      return headblk;
   }
}
