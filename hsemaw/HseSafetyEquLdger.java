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

public class HseSafetyEquLdger extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HseSafetyEquLdger");

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

   private ASPBlock hse_safety_eque_ldger_1_blk;
   private ASPRowSet hse_safety_eque_ldger_1_set;
   private ASPCommandBar hse_safety_eque_ldger_1_bar;
   private ASPTable hse_safety_eque_ldger_1_tbl;
   private ASPBlockLayout hse_safety_eque_ldger_1_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HseSafetyEquLdger (ASPManager mgr, String page_path)
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
         mgr.showAlert("HSESAFETYEQULDGERNODATA: No data found.");
         headset.clear();
      }
      eval( hse_safety_eque_ldger_1_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","HSE_SAFETY_EQU_LDGER_API.New__",headblk);
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

      q = trans.addQuery(hse_safety_eque_ldger_1_blk);
      q.addWhereCondition("PROJ_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,hse_safety_eque_ldger_1_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","HSE_SAFETY_EQUE_LDGER1_API.New__",hse_safety_eque_ldger_1_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      hse_safety_eque_ldger_1_set.addRow(data);
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
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setLabel("HSESAFETYEQULDGERID: Id").
              setSize(20);
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("HSESAFETYEQULDGERPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("HSESAFETYEQULDGERPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");

      headblk.addField("CONTRACT_ID").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("HSESAFETYEQULDGERCONTRACTID: Contract Id").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_ID", "CONTRACT_NAME,SECOND_SIDE,SECOND_SIDE_NAME");
      headblk.addField("CONTRACT_NAME").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSESAFETYEQULDGERCONTRACTNAME: Contract Name").
              setSize(30);

      headblk.addField("SECOND_SIDE").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSESAFETYEQULDGERSECONDSIDE: Constraction Org No").
              setSize(30);

      headblk.addField("SECOND_SIDE_NAME").
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID))").
              setLabel("HSESAFETYEQULDGERSECONDSIDENAME: Constraction Org Name").
              setSize(30);

      headblk.addField("CREATE_PERSON").
              setInsertable().
//              setHidden().
              setDynamicLOV("PERSON_INFO").
              setLabel("HSESAFETYEQULDGERCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
//              setHidden().
              setLabel("HSELARGEEQUREVAPPLCREATEPERSONNAME: Create Person Name").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
//              setHidden().
              setLabel("HSESAFETYEQULDGERCREATETIME: Create Time").
              setSize(20);
      headblk.setView("HSE_SAFETY_EQU_LDGER");
      headblk.defineCommand("HSE_SAFETY_EQU_LDGER_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "HSESAFETYEQULDGERPRINTREPORT: Print Report...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSESAFETYEQULDGERTBLHEAD: Hse Safety Equ Ldgers");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("SECOND_SIDE_NAME");
      headlay.setSimple("CONTRACT_NAME");
 


      hse_safety_eque_ldger_1_blk = mgr.newASPBlock("ITEM1");
      hse_safety_eque_ldger_1_blk.addField("ITEM0_OBJID").
                                  setHidden().
                                  setDbName("OBJID");
      hse_safety_eque_ldger_1_blk.addField("ITEM0_OBJVERSION").
                                  setHidden().
                                  setDbName("OBJVERSION");
      hse_safety_eque_ldger_1_blk.addField("ITEM0_PROJ_NO").
                                  setDbName("PROJ_NO").
                                  setMandatory().
                                  setInsertable().
                                  setHidden().
                                  setLabel("HSESAFETYEQUELDGER1ITEM0PROJNO: Proj No").
                                  setSize(20);
      hse_safety_eque_ldger_1_blk.addField("ITEM0_ID").
                                  setDbName("ID").
                                  setMandatory().
                                  setInsertable().
                                  setHidden().
                                  setLabel("HSESAFETYEQUELDGER1ITEM0ID: Id").
                                  setSize(20);
      hse_safety_eque_ldger_1_blk.addField("LINE_NO").
//                                  setMandatory().
//                                  setInsertable().
                                  setReadOnly().
                                  setLabel("HSESAFETYEQUELDGER1LINENO: Line No").
                                  setSize(20);
      hse_safety_eque_ldger_1_blk.addField("EQUIPMENT_NAME").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1EQUIPMENTNAME: Equipment Name").
                                  setSize(30);
      hse_safety_eque_ldger_1_blk.addField("MODEL").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1MODEL: Model").
                                  setSize(20);
      hse_safety_eque_ldger_1_blk.addField("FACTORY").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1FACTORY: Factory").
                                  setSize(30);
      hse_safety_eque_ldger_1_blk.addField("TEST_CYCLE").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1TESTCYCLE: Test Cycle").
                                  setSize(20);
      hse_safety_eque_ldger_1_blk.addField("USE_DATE","Date").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1USEDATE: Use Date").
                                  setSize(30);
      hse_safety_eque_ldger_1_blk.addField("NOTE").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1NOTE: Note").
                                  setHeight(4).
                                  setSize(100);
      hse_safety_eque_ldger_1_blk.addField("ITEM0_CREATE_PERSON").
                                  setDbName("CREATE_PERSON").
                                  setInsertable().
                                  setDynamicLOV("PERSON_INFO").
                                  setLabel("HSESAFETYEQUELDGER1ITEM0CREATEPERSON: Create Person").
                                  setSize(20);
      hse_safety_eque_ldger_1_blk.addField("ITEM1_CREATE_PERSON_NAME").
                                 setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
                                 setLabel("HSESAFETYEQUELDGER1ITEM0CREATEPERSONNAME: Create Person Name").
                                 setReadOnly().
                                 setSize(30);
      mgr.getASPField("ITEM0_CREATE_PERSON").setValidation("ITEM1_CREATE_PERSON_NAME");
      hse_safety_eque_ldger_1_blk.addField("ITEM0_CREATE_TIME","Date").
                                  setDbName("CREATE_TIME").
                                  setInsertable().
                                  setLabel("HSESAFETYEQUELDGER1ITEM0CREATETIME: Create Time").
                                  setSize(30);
      hse_safety_eque_ldger_1_blk.setView("HSE_SAFETY_EQUE_LDGER1");
      hse_safety_eque_ldger_1_blk.defineCommand("HSE_SAFETY_EQUE_LDGER1_API","New__,Modify__,Remove__");
      hse_safety_eque_ldger_1_blk.setMasterBlock(headblk);
      hse_safety_eque_ldger_1_set = hse_safety_eque_ldger_1_blk.getASPRowSet();
      hse_safety_eque_ldger_1_bar = mgr.newASPCommandBar(hse_safety_eque_ldger_1_blk);
      hse_safety_eque_ldger_1_bar.defineCommand(hse_safety_eque_ldger_1_bar.OKFIND, "okFindITEM1");
      hse_safety_eque_ldger_1_bar.defineCommand(hse_safety_eque_ldger_1_bar.NEWROW, "newRowITEM1");
      hse_safety_eque_ldger_1_tbl = mgr.newASPTable(hse_safety_eque_ldger_1_blk);
      hse_safety_eque_ldger_1_tbl.setTitle("HSESAFETYEQUELDGER1ITEMHEAD1: HseSafetyEqueLdger1");
      hse_safety_eque_ldger_1_tbl.enableRowSelect();
      hse_safety_eque_ldger_1_tbl.setWrap();
      hse_safety_eque_ldger_1_lay = hse_safety_eque_ldger_1_blk.getASPBlockLayout();
      hse_safety_eque_ldger_1_lay.setDefaultLayoutMode(hse_safety_eque_ldger_1_lay.MULTIROW_LAYOUT);
      hse_safety_eque_ldger_1_lay.setSimple("ITEM1_CREATE_PERSON_NAME");


      hse_safety_eque_ldger_1_lay.setDataSpan("NOTE", 5);


   }



   public void  adjust()
   {
      // fill function body
   }
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
            String accept_id = headset.getValue("ID");
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=HseSafetyEquLdger.raq&proj_no="+proj_no+"&id="+accept_id
               + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
        }
  }   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HSESAFETYEQULDGERDESC: Hse Safety Equ Ldger";
   }


   protected String getTitle()
   {
      return "HSESAFETYEQULDGERTITLE: Hse Safety Equ Ldger";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (hse_safety_eque_ldger_1_lay.isVisible())
          appendToHTML(hse_safety_eque_ldger_1_lay.show());

   }
}
