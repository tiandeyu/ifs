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

package ifs.engmaw;
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

public class ChangeDesgReq extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.engmaw.ChangeDesgReq");

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

   private ASPBlock change_desg_req_list_blk;
   private ASPRowSet change_desg_req_list_set;
   private ASPCommandBar change_desg_req_list_bar;
   private ASPTable change_desg_req_list_tbl;
   private ASPBlockLayout change_desg_req_list_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ChangeDesgReq (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      super.run();
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();   
      else if(mgr.dataTransfered())
         okFind();
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
         mgr.showAlert("CHANGEDESGREQNODATA: No data found.");
         headset.clear();
      }else okFindITEM1();
      eval( change_desg_req_list_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","CHANGE_DESG_REQ_API.New__",headblk);
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

      q = trans.addQuery(change_desg_req_list_blk);
      q.addWhereCondition("PROJ_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,change_desg_req_list_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","CHANGE_DESG_REQ_LIST_API.New__",change_desg_req_list_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      change_desg_req_list_set.addRow(data);
   }

   

   public void validate()
   {
      ASPManager mgr=getASPManager();
      ASPTransactionBuffer trans=mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String str=mgr.readValue("VALIDATE");
      if ("QUANTITIES".equals(str)||"PRICE".equals(str)) {
         mgr.responseWrite((mgr.isEmpty(mgr.readValue("QUANTITIES"))?0:Float.parseFloat(mgr.readValue("QUANTITIES")))*(mgr.isEmpty(mgr.readValue("PRICE"))?0:Float.parseFloat(mgr.readValue("PRICE")))+ "^");
      }else if ("CONTRACT_ID".equals(str)) {
      
      cmd = trans.addCustomFunction("CONTRACT_DESC", 
            "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_DESC");
      cmd.addParameter("PROJ_NO,CONTRACT_ID");
      
      cmd = trans.addCustomFunction("GETSENDTO", 
            "GENERAL_ORGANIZATION_API.GET_ORG_NO", "SEND_TO");
      cmd.addParameter("PROJ_NO,CONTRACT_ID");
      
      cmd = trans.addCustomFunction("GETCOPYTO", 
            "GENERAL_ORGANIZATION_API.GET_ORG_NO", "COPY_TO");
      cmd.addParameter("PROJ_NO");
      cmd.addParameter("CONTRACT_ID");
      cmd.addParameter("PROJ_DESC","1");
      
      
      trans = mgr.validate(trans);   
      
      String contract_name = trans.getValue("CONTRACT_DESC/DATA/CONTRACT_DESC");
      String send_to = trans.getValue("GETSENDTO/DATA/SEND_TO");
      String copy_to = trans.getValue("GETCOPYTO/DATA/COPY_TO");
      trans.clear();
      
      cmd = trans.addCustomFunction("GETSENDTONAME", 
            "GENERAL_ORGANIZATION_API.GET_ORG_DESC", "SEND_TO_NAME");
      cmd.addParameter("SEND_TO",send_to);
      
      cmd = trans.addCustomFunction("GETCOPYTONAME", 
            "GENERAL_ORGANIZATION_API.GET_ORG_DESC", "COPY_TO_NAME");
      cmd.addParameter("COPY_TO",copy_to);
      
      trans = mgr.validate(trans);  
      
      String send_to_name = trans.getValue("GETSENDTONAME/DATA/SEND_TO_NAME");
      String copy_to_name = trans.getValue("GETCOPYTONAME/DATA/COPY_TO_NAME");
      
      String txt = ((mgr.isEmpty(contract_name)) ? "" : contract_name )+ "^"
         +  ((mgr.isEmpty(send_to)) ? "" : send_to )+ "^"
         +  ((mgr.isEmpty(copy_to)) ? "" : copy_to )+ "^"
         +  ((mgr.isEmpty(send_to_name)) ? "" : send_to_name )+ "^"
         +  ((mgr.isEmpty(copy_to_name)) ? "" : copy_to_name )+ "^";
      
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
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CHANGEDESGREQPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("CHANGEDESGREQPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setHidden().
              setLabel("CHANGEDESGREQID: Id").
              setSize(20);
      headblk.addField("REQ_NO").
              setInsertable().
              setLabel("CHANGEDESGREQREQNO: Req No").
              setSize(20);
      headblk.addField("REQ_NAME").
              setInsertable().
              setWfProperties().
              setMandatory().
              setLabel("CHANGEDESGREQREQNAME: Req Name").
              setSize(20);
      headblk.addField("NOTICE_NO").
              setReadOnly().
              setLabel("CHANGEDESGREQNOTICENO: Notice No").
              setSize(20);
      headblk.addField("NOTICE_NAME").
              setReadOnly().
              setFunction("DESG_CHANGE_NOTICE_API.Get_NOTICE_NAME(:PROJ_NO, :NOTICE_NO)").
              setLabel("CHANGEDESGREQNOTICENAME: Notice Name").
              setSize(30);

      headblk.addField("VOLUME_NO").
              setInsertable().
              setDynamicLOV("DRAWING_LIST_LOV","PROJ_NO").
              setLabel("CHANGEDESGREQVOLUMENO: Volume No").
              setSize(20);
      headblk.addField("VOLUME_DESC").  
              setReadOnly().
              setLabel("CHANGEDESGREQVOLUMEDESC: Volume Desc").
              setFunction("Drawing_List_api.Get_Volume_Desc(:PROJ_NO, :VOLUME_NO)").
              setSize(30);
      mgr.getASPField("VOLUME_NO").setValidation("VOLUME_DESC");

      headblk.addField("CONTRACT_ID").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("CHANGEDESGREQCONTRACTID: Contract Id").
              setCustomValidation("PROJ_NO,CONTRACT_ID", "CONTRACT_DESC,SEND_TO,COPY_TO,SEND_TO_NAME,COPY_TO_NAME").
              setSize(20);
      headblk.addField("CONTRACT_DESC").  
              setReadOnly().
              setLabel("CHANGEDESGREQCONTRACTDESC: Contract Desc").
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC(:PROJ_NO,:CONTRACT_ID)").
              setSize(30);
//      mgr.getASPField("CONTRACT_ID").setValidation("CONTRACT_DESC");

      headblk.addField("SEND_TO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV", "PROJ_NO").
              setLabel("CHANGEDESGREQCONORG: Con Org").
              setSize(20);
      headblk.addField("SEND_TO_NAME").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc( :SEND_TO)").
              setReadOnly().
              setLabel("CHANGEDESGREQCONORGNAME: Con Org Name").
              setSize(30);
      mgr.getASPField("SEND_TO").setValidation("SEND_TO_NAME");
      headblk.addField("COPY_TO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV", "PROJ_NO").
              setLabel("CHANGEDESGREQSUPORG: Sup Org").
              setSize(20);
      headblk.addField("COPY_TO_NAME").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:COPY_TO)").
              setReadOnly().
              setLabel("CHANGEDESGREQSUPORGNAME: Sup Org Name").
              setSize(30);
      mgr.getASPField("COPY_TO").setValidation("COPY_TO_NAME");


      headblk.addField("IF_VALID").
              setInsertable().
              setCheckBox("FALSE,TRUE").
              setLabel("CHANGEDESGREQIFVALID: If Valid").
              setSize(20);
      headblk.addField("IF_DESG_CAHNGE").
//              setInsertable().
              setReadOnly().
              setCheckBox("FALSE,TRUE").
              setLabel("CHANGEDESGREQIFDESGCAHNGE: If Desg Cahnge").
              setSize(20);

      headblk.addField("TOTLE_LIST_PRICE","Money","#0.00").  
              setReadOnly().
              setLabel("CHANGEDESGREQESTIMATETOTALPRICE: Estimate Total Price").
              setFunction("CHANGE_DESG_REQ_API.Get_Totle_List_Price(:PROJ_NO, :ID)").
              setSize(30);
      headblk.addField("STATUS").
              setReadOnly().
              setHidden().
              setLabel("CHANGEDESGREQSTATUS: Status").
              setSize(20);
      headblk.addField("STATUS_DESC").
              setReadOnly().
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("CHANGEDESGREQSTATUS: Status").
              setSize(30);

      headblk.addField("CHANGE_REASON").
              setInsertable().
              setLabel("CHANGEDESGREQCHANGEREASON: Change Reason").
              setHeight(4).
              setSize(100);
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setHidden().
              setDynamicLOV("PERSON_INFO").
              setLabel("CHANGEDESGREQCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setHidden().
              setLabel("CHANGEDESGREQCREATEPERSONNAME: Create Person Name").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setHidden().
              setLabel("CHANGEDESGREQCREATETIME: Create Time").
              setSize(20);



      headblk.setView("CHANGE_DESG_REQ");
      headblk.defineCommand("CHANGE_DESG_REQ_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "CHANGEDESGREQPRINTREPORT: Print Report...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CHANGEDESGREQTBLHEAD: Change Desg Reqs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
    headlay.setDataSpan("CHANGE_REASON", 5);
    headlay.setSimple("PROJ_DESC");
    headlay.setSimple("CONTRACT_DESC");
    headlay.setSimple("VOLUME_DESC");
    headlay.setSimple("CREATE_PERSON_NAME");
    headlay.setSimple("SEND_TO_NAME");
    headlay.setSimple("COPY_TO_NAME");
    headlay.setSimple("NOTICE_NAME");
 


      change_desg_req_list_blk = mgr.newASPBlock("ITEM1");
      change_desg_req_list_blk.addField("ITEM0_OBJID").
                               setHidden().
                               setDbName("OBJID");
      change_desg_req_list_blk.addField("ITEM0_OBJVERSION").
                               setHidden().
                               setDbName("OBJVERSION");
      change_desg_req_list_blk.addField("ITEM0_PROJ_NO").
                               setDbName("PROJ_NO").
                               setMandatory().
                               setInsertable().
                               setHidden().
                               setLabel("CHANGEDESGREQLISTITEM0PROJNO: Proj No").
                               setSize(20);
      change_desg_req_list_blk.addField("ITEM0_ID").
                               setDbName("ID").
                               setMandatory().
                               setHidden().
                               setInsertable().
                               setLabel("CHANGEDESGREQLISTITEM0ID: Id").
                               setSize(20);
      change_desg_req_list_blk.addField("LIST_NO").
//                               setMandatory().
//                               setInsertable().
                               setReadOnly().
                               setHidden().
                               setLabel("CHANGEDESGREQLISTLISTNO: List No").
                               setSize(20);
      change_desg_req_list_blk.addField("LIST_CONTENT").
                               setInsertable().
                               setLabel("CHANGEDESGREQLISTLISTCONTENT: List Content").
                               setSize(20);
      change_desg_req_list_blk.addField("UNIT_NO").
                               setInsertable().
                               setDynamicLOV("ISO_UNIT").
                               setLabel("CHANGEDESGREQLISTUNITNO: Unit No").
                               setSize(20);
      change_desg_req_list_blk.addField("UNIT_DESC").
                                  setReadOnly().
                                  setLabel("CHANGEDESGREQLISTUNITNODESC: Unit Desc").
                                  setFunction("Iso_Unit_API.Get_Description(:UNIT_NO)").
                                  setSize(20);
      mgr.getASPField("UNIT_NO").setValidation("UNIT_DESC");
      change_desg_req_list_blk.addField("QUANTITIES","Money","#0.00").
                               setInsertable().
                               setCustomValidation("QUANTITIES,PRICE","TOTLE_PRICE").
                               setLabel("CHANGEDESGREQLISTQUANTITIES: Quantities").
                               setSize(20);
      change_desg_req_list_blk.addField("PRICE","Money","#0.00").
                               setInsertable().
                               setCustomValidation("QUANTITIES,PRICE","TOTLE_PRICE").
                               setLabel("CHANGEDESGREQLISTPRICE: Price").
                               setSize(20);
      change_desg_req_list_blk.addField("TOTLE_PRICE","Money","#0.00").
//                               setInsertable().
                               setReadOnly().
                               setLabel("CHANGEDESGREQLISTTOTLEPRICE: Totle Price").
                               setSize(20);

      change_desg_req_list_blk.addField("BUDGET_NO").
                                  setInsertable().
//                                setDynamicLOV("PROJECT_BUDGET","PROJ_NO").
                                setDynamicLOV("PROJECT_BUDGET_LINE").
                                  setLabel("CHANGEDESGREQLISTBUDGETNO: Budget No").
                                  setSize(20);
      change_desg_req_list_blk.addField("BUDGET_NAME").
                                  setReadOnly().
//                                setFunction("PROJECT_BUDGET_API.Get_Budget_Name(:PROJ_NO,:BUDGET_NO)").
                                setFunction("PROJECT_BUDGET_LINE_API.Get_Budget_Name (:PROJ_NO , :BUDGET_NO)").
                                  setLabel("CHANGEDESGREQLISTBUDGETNAME: Budget Name").
                                  setSize(30);
      mgr.getASPField("BUDGET_NO").setValidation("BUDGET_NAME");
      change_desg_req_list_blk.setView("CHANGE_DESG_REQ_LIST");
      change_desg_req_list_blk.defineCommand("CHANGE_DESG_REQ_LIST_API","New__,Modify__,Remove__");
      change_desg_req_list_blk.setMasterBlock(headblk);
      change_desg_req_list_set = change_desg_req_list_blk.getASPRowSet();
      change_desg_req_list_bar = mgr.newASPCommandBar(change_desg_req_list_blk);
      change_desg_req_list_bar.defineCommand(change_desg_req_list_bar.OKFIND, "okFindITEM1");
      change_desg_req_list_bar.defineCommand(change_desg_req_list_bar.NEWROW, "newRowITEM1");
      change_desg_req_list_tbl = mgr.newASPTable(change_desg_req_list_blk);
      change_desg_req_list_tbl.setTitle("CHANGEDESGREQLISTITEMHEAD1: ChangeDesgReqList");
      change_desg_req_list_tbl.enableRowSelect();
      change_desg_req_list_tbl.setWrap();
      change_desg_req_list_lay = change_desg_req_list_blk.getASPBlockLayout();
      change_desg_req_list_lay.setDefaultLayoutMode(change_desg_req_list_lay.MULTIROW_LAYOUT);
//      change_desg_req_list_lay.setDataSpan("LIST_CONTENT", 5);
      change_desg_req_list_lay.setSimple("BUDGET_NAME");
      change_desg_req_list_lay.setSimple("UNIT_DESC");



   }



   public void  adjust()
   {
      try {
         super.adjust();
      } catch (FndException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // fill function body
      ASPManager mgr = getASPManager();
      if("ITEM1.OverviewSave".equals(mgr.readValue("__COMMAND"))||"ITEM1.SaveReturn".equals(mgr.readValue("__COMMAND"))||"ITEM1.SaveNew".equals(mgr.readValue("__COMMAND"))) headset.refreshRow();
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
            String delivery_id = headset.getValue("ID");
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=ChangeDesgReq.raq&proj_no="+proj_no+"&id="+delivery_id
               + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
        }
  }  
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CHANGEDESGREQDESC: Change Desg Req";
   }


   protected String getTitle()
   {
      return "CHANGEDESGREQTITLE: Change Desg Req";
   }


   protected void printContents() throws FndException
   {
      super.printContents();
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (change_desg_req_list_lay.isVisible())
          appendToHTML(change_desg_req_list_lay.show());

   }
   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}
