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

public class HseSpeDevReg extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HseSpeDevReg");

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

   private ASPBlock hse_spe_dev_reg_line_blk;
   private ASPRowSet hse_spe_dev_reg_line_set;
   private ASPCommandBar hse_spe_dev_reg_line_bar;
   private ASPTable hse_spe_dev_reg_line_tbl;
   private ASPBlockLayout hse_spe_dev_reg_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HseSpeDevReg (ASPManager mgr, String page_path)
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
         mgr.showAlert("HSESPEDEVREGNODATA: No data found.");
         headset.clear();
      }
      eval( hse_spe_dev_reg_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","HSE_SPE_DEV_REG_API.New__",headblk);
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

      q = trans.addQuery(hse_spe_dev_reg_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,hse_spe_dev_reg_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","HSE_SPE_DEV_REG_LINE_API.New__",hse_spe_dev_reg_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      hse_spe_dev_reg_line_set.addRow(data);
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
              setLabel("HSESPEDEVREGID: Id").
              setSize(20);
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("HSESPEDEVREGPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("HSESPEDEVREGPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");

      headblk.addField("CONTRACT_ID").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("HSESPEDEVREGPCONTRACTID: Contract Id").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_ID", "CONTRACT_NAME,SECOND_SIDE,SECOND_SIDE_NAME");
      headblk.addField("CONTRACT_NAME").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSESPEDEVREGPCONTRACTNAME: Contract Name").
              setSize(30);

      headblk.addField("SECOND_SIDE").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID)").
              setLabel("HSESPEDEVREGPSECONDSIDE: Constraction Org No").
              setSize(30);

      headblk.addField("SECOND_SIDE_NAME").
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_ID))").
              setLabel("HSESPEDEVREGPSECONDSIDENAME: Constraction Org Name").
              setSize(30);

      headblk.addField("CREATE_PERSON").
              setInsertable().
//              setHidden().
              setDynamicLOV("PERSON_INFO").
              setLabel("HSESPEDEVREGCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
//              setHidden().
              setLabel("HSESPEDEVREGCREATEPERSONNAME: Create Person Name").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
//              setHidden().
              setLabel("HSESPEDEVREGCREATETIME: Create Time").
              setSize(20);
      headblk.setView("HSE_SPE_DEV_REG");
      headblk.defineCommand("HSE_SPE_DEV_REG_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "HSESPEDEVREGRINTREPORT: Print Report...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSESPEDEVREGTBLHEAD: Hse Spe Dev Regs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("SECOND_SIDE_NAME");
      headlay.setSimple("CONTRACT_NAME");
 


      hse_spe_dev_reg_line_blk = mgr.newASPBlock("ITEM1");
      hse_spe_dev_reg_line_blk.addField("ITEM0_OBJID").
                               setHidden().
                               setDbName("OBJID");
      hse_spe_dev_reg_line_blk.addField("ITEM0_OBJVERSION").
                               setHidden().
                               setDbName("OBJVERSION");
      hse_spe_dev_reg_line_blk.addField("ITEM0_PROJ_NO").
                               setDbName("PROJ_NO").
                               setMandatory().
                               setInsertable().
                               setHidden().
                               setLabel("HSESPEDEVREGLINEITEM0PROJNO: Proj No").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("ITEM0_ID").
                               setDbName("ID").
                               setMandatory().
                               setInsertable().
                               setHidden().
                               setLabel("HSESPEDEVREGLINEITEM0ID: Id").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("LINE_NO").
//                               setMandatory().
//                               setInsertable().
                               setReadOnly().
                               setLabel("HSESPEDEVREGLINELINENO: Line No").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("EQUIPMENT_NAME").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEEQUIPMENTNAME: Equipment Name").
                               setSize(30);
      hse_spe_dev_reg_line_blk.addField("MODEL").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEMODEL: Model").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("FACTORY_DATE","Date").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEFACTORYDATE: Factory Date").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("DEVICE_CODE").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEDEVICECODE: Device Code").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("USE_REG_NO").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEUSEREGNO: Use Reg No").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("ADMISSION_DATE","Date").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEADMISSIONDATE: Admission Date").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("EXIT_DATE","Date").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEEXITDATE: Exit Date").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("DETECT_UNIT").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEDETECTUNIT: Detect Unit").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("VALIDITY").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEVALIDITY: Validity").
                               setSize(20);
      hse_spe_dev_reg_line_blk.addField("USE_UNIT").
                               setInsertable().
                               setLabel("HSESPEDEVREGLINEUSEUNIT: Use Unit").
                               setSize(20);
      hse_spe_dev_reg_line_blk.setView("HSE_SPE_DEV_REG_LINE");
      hse_spe_dev_reg_line_blk.defineCommand("HSE_SPE_DEV_REG_LINE_API","New__,Modify__,Remove__");
      hse_spe_dev_reg_line_blk.setMasterBlock(headblk);
      hse_spe_dev_reg_line_set = hse_spe_dev_reg_line_blk.getASPRowSet();
      hse_spe_dev_reg_line_bar = mgr.newASPCommandBar(hse_spe_dev_reg_line_blk);
      hse_spe_dev_reg_line_bar.defineCommand(hse_spe_dev_reg_line_bar.OKFIND, "okFindITEM1");
      hse_spe_dev_reg_line_bar.defineCommand(hse_spe_dev_reg_line_bar.NEWROW, "newRowITEM1");
      hse_spe_dev_reg_line_tbl = mgr.newASPTable(hse_spe_dev_reg_line_blk);
      hse_spe_dev_reg_line_tbl.setTitle("HSESPEDEVREGLINEITEMHEAD1: HseSpeDevRegLine");
      hse_spe_dev_reg_line_tbl.enableRowSelect();
      hse_spe_dev_reg_line_tbl.setWrap();
      hse_spe_dev_reg_line_lay = hse_spe_dev_reg_line_blk.getASPBlockLayout();
      hse_spe_dev_reg_line_lay.setDefaultLayoutMode(hse_spe_dev_reg_line_lay.MULTIROW_LAYOUT);



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
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=HseSpeDevReg.raq&proj_no="+proj_no+"&id="+accept_id
               + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
        }
  }   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HSESPEDEVREGDESC: Hse Spe Dev Reg";
   }


   protected String getTitle()
   {
      return "HSESPEDEVREGTITLE: Hse Spe Dev Reg";
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
      if (hse_spe_dev_reg_line_lay.isVisible())
          appendToHTML(hse_spe_dev_reg_line_lay.show());

   }
}