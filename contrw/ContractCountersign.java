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

package ifs.contrw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import ifs.docmaw.DocmawConstants;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.genbaw.GenbawConstants;
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ContractCountersign extends HzASPPageProviderWf  
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.contrw.ProjectContract");

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

   private ASPBlock project_contract_item_blk;
   private ASPRowSet project_contract_item_set;
   private ASPCommandBar project_contract_item_bar;
   private ASPTable project_contract_item_tbl;
   private ASPBlockLayout project_contract_item_lay;

   protected DecimalFormat df;
   protected ASPTabContainer tabs;
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ContractCountersign (ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   protected String getDefProj() {
      ASPManager mgr = getASPManager();
      ASPContext ctx =mgr.getASPContext();
      return ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT);
   }    
  
   public void run() throws FndException
   {
      super.run();
      String comnd;  
      ASPManager mgr = getASPManager(); 
      if( mgr.commandBarActivated() ){
         comnd = mgr.readValue("__COMMAND");  
         eval(mgr.commandBarFunction());
         if (( "ITEM1.SaveReturn".equals(comnd) )|| ( "ITEM1.SaveReturn".equals(comnd) )|| ( "ITEM1.Delete".equals(comnd) )|| ( "ITEM2.SaveReturn".equals(comnd) )|| ( "ITEM1.SaveNew".equals(comnd) )|| ( "ITEM1.Delete".equals(comnd) ))
         {
            headset.refreshAllRows();  
         }   
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT_ID")) )
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else 
         okFind();
      tabs.saveActiveTab();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void  performHEAD( String command)
   {
      int currow;
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      

      currow = headset.getCurrentRowNo();
      if(headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
      headset.markSelectedRows( command );
      mgr.submit(trans);
      headset.refreshRow();  
      headset.goTo(currow);
      headset.refreshRow();                  
   }
   public void  check()
   {

      performHEAD( "Check__" );
   }
   public void  close()
   {

      performHEAD( "Close__" );
   }

   
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
         mgr.showAlert("PROJECTCONTRACTNODATA: No data found.");
         headset.clear();
      }else{
         okFindITEM1();
      }
      eval( project_contract_item_set.syncItemSets() );
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
      cmd = trans.addEmptyCommand("HEAD","PROJECT_CONTRACT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE"); 
      cmd.setParameter("PROJ_NO", this.getDefProj());  
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

      q = trans.addQuery(project_contract_item_blk);
      q.addWhereCondition("PROJ_NO = ? AND CONTRACT_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("CONTRACT_ID", headset.getValue("CONTRACT_ID"));
      q.addOrderByClause("FULL_PATH,ITEM_NO");       
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,project_contract_item_blk);
      headset.goTo(headrowno);
   }
   
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","PROJECT_CONTRACT_ITEM_API.New__",project_contract_item_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_CONTRACT_ID", headset.getValue("CONTRACT_ID"));
      cmd.setParameter("ITEM_CURRENCY","CNY");
      cmd.setParameter("ITEM_EXCHANGE_RATE", "1");  
      cmd.setParameter("ITEM_TYPE", "ITEM");  
        
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");    
      project_contract_item_set.addRow(data);
   }
   

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("OBJSTATE").
              setHidden();
      headblk.addField("OBJEVENTS").
              setHidden(); 
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("PROJECTCONTRACTPROJNO: Proj No").
              setSize(20);  
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setReadOnly().
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("PROJECTCONTRACTGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(20);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("CONTRACT_ID"). 
              setUpperCase().  
              setInsertable().  
              setLabel("PROJECTCONTRACTCONTRACTID: Contract Id").
              setSize(20);   
     headblk.addField("PRE_CONTRACT_NO").
             setInsertable().
             setLabel("PROJECTCONTRACTPRECONTRACTNO: Pre Contract No").
             setSize(20);
//add by lhh 20141205
      headblk.addField("VIEW_REF_FILE").
           setReadOnly().
            unsetQueryable().
            setFunction("''").
           setLabel("PROJECTCONTRACTVIEWREFFILE: View File").
           setHyperlink( mgr.getApplicationPath()
              + "/" + mgr.getConfigParameter("DOCMAW/DOC_REF_URL")
              + "?VIEW=" + mgr.URLEncode("PROJECT_CONTRACT"), "OBJID", "NEWWIN").
           setAsImageField();
       headblk.addField("LU_NAME").
             setHidden();
       headblk.addField("KEY_REF").
             setHidden();
       headblk.addField("EXIST_DOC_REF").
             setHidden().
             setCheckBox("FALSE,TRUE").
             setFunction("Doc_Reference_Object_API.Exist_Obj_Reference(:LU_NAME, :KEY_REF)").
             setSize(5);
//add by lhh 20141205  end  
       headblk.addField("CONTRACT_DESC").
               setMandatory().
               setWfProperties(). 
               setInsertable().
               setLabel("PROJECTCONTRACTCONTRACTDESC: Contract Desc").
               setSize(100); 
       headblk.addField("CLASS_NO").
               setInsertable().
               setUpperCase().
               setMandatory().
               setDynamicLOV("CONTRACT_CLASS").
               setLabel("PROJECTCONTRACTCONTRACTCLASS: Contract Class").
               setSize(20);
       headblk.addField("CONTRACT_CLASS_NAME").
               setReadOnly().  
               setFunction("Contract_Class_API.Get_Class_Name(:CLASS_NO)").
               setLabel("PROJECTCONTRACTCONTRACTCLASSNAME: Class Name").
               setSize(20);
      mgr.getASPField("CLASS_NO").setValidation("CONTRACT_CLASS_NAME");
      headblk.addField("PURCH_TYPE_NO").
              setInsertable().
              setDynamicLOV("CONTRACT_PURCH_TYPE").
              setLabel("PROJECTCONTRACTPURCHTYPE: Purch Type").
              setSize(20);
      headblk.addField("PURCH_TYPE_DESC").    
              setReadOnly().
              setFunction("CONTRACT_PURCH_TYPE_API.Get_Description(:PURCH_TYPE_NO)").
              setLabel("PROJECTCONTRACTPURCHTYPEDESC: Purch Type Desc").
              setSize(20);
      mgr.getASPField("PURCH_TYPE_NO").setValidation("PURCH_TYPE_DESC");
      headblk.addField("SING_DATE","Date").
              setInsertable().
              setMandatory().
              setLabel("PROJECTCONTRACTSINGDATE: Sing Date").
              setSize(20);
      headblk.addField("FIRST_SIDE").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_ZONE").
              setLabel("PROJECTCONTRACTFIRSTSIDE: First Side").
              setSize(20); 
      headblk.addField("FIRST_SIDE_NAME").
              setSize(20).
              setMaxLength(140).         
              setReadOnly().
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc(:FIRST_SIDE)").
              setLabel("PROJECTCONTRACTFIRSTSIDENAME: First Side Name");
      mgr.getASPField("FIRST_SIDE").setValidation("FIRST_SIDE_NAME");
      headblk.addField("SECEND_SIDE").
              setInsertable().
              setDefaultNotVisible().
              setMandatory().
              setDynamicLOV("SUPPLIER_INFO").
              setLabel("PROJECTCONTRACTSECENDSIDE: Secend Side").
              setSize(20);
      headblk.addField("SECEND_SIDE_NAME").
              setSize(20).
              setMaxLength(140).
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.GET_NAME(:SECEND_SIDE)").    
              setLabel("PROJECTCONTRACTSECENDSIDENAME: Secend Side Name");
      mgr.getASPField("SECEND_SIDE").setValidation("SECEND_SIDE_NAME");
      headblk.addField("SIGN_PRICE","Number","#0.00").
              setFunction("PROJECT_CONTRACT_API.Cal_Ori_Contract_Total(:PROJ_NO, :CONTRACT_ID)").
              setInsertable().
              setLabel("PROJECTCONTRACTSIGNPRICE: Sign Price").
              setSize(20);
      headblk.addField("FIRST_PERSON").
              setInsertable().
              setDefaultNotVisible().
              setLabel("PROJECTCONTRACTFIRSTPERSON: First Person").
              setDynamicLOV("PERSON_INFO").
              setSize(20);
      headblk.addField("FIRST_PERSON_NAME").
              setReadOnly().
              setLabel("PROJECTCONTRACTFIRSTPERSONNAME: First Person Name").
              setFunction("PERSON_INFO_API.Get_Name(:FIRST_PERSON)").
              setSize(20);  
      mgr.getASPField("FIRST_PERSON").setValidation("FIRST_PERSON_NAME");
      headblk.addField("UNDERTAKER").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("CONTRACTCOUNTERSIGNUNDERTAKER: Undertaker").
              setSize(20);
      headblk.addField("UNDERTAKER_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.Get_Name(:UNDERTAKER)").
              setLabel("CONTRACTCOUNTERSIGNUNDERTAKERNAME: Undertaker Name").
              setSize(20);
      mgr.getASPField("UNDERTAKER").setValidation("UNDERTAKER_NAME");
      headblk.addField("BUDGET_RANGE","Money","#0.00").
              setInsertable().
              setLabel("CONTRACTCOUNTERSIGNBUDGETRANGE: Budget Range").
              setSize(20);
      headblk.addField("UNDERTAKE_DEPT").
              setInsertable().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV", "PROJ_NO").
              setLabel("CONTRACTCOUNTERSIGNUNDERTAKEDEPT: Undertake Dept").
              setSize(20);
      headblk.addField("UNDERTAKE_DEPT_NAME").
              setReadOnly().
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc( :UNDERTAKE_DEPT)").
              setLabel("CONTRACTCOUNTERSIGNUNDERTAKEDEPTNAME: Undertake Dept Name").
              setSize(20);
      mgr.getASPField("UNDERTAKE_DEPT").setValidation("UNDERTAKE_DEPT_NAME");
      headblk.addField("RELEVANT_DEPT").
              setInsertable().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV", "PROJ_NO").
              setLabel("CONTRACTCOUNTERSIGNRELEVANTDEPT: Relevant Dept").
              setSize(20);
      headblk.addField("RELEVANT_DEPT_NAME").
              setReadOnly().
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc( :RELEVANT_DEPT)").
              setLabel("CONTRACTCOUNTERSIGNRELEVANTDEPTNAME: Relevant Dept Name").
              setSize(20);
      mgr.getASPField("RELEVANT_DEPT").setValidation("RELEVANT_DEPT_NAME");
      headblk.addField("SCHEDULE").
              setInsertable().
              setMandatory().
              setCheckBox("FALSE,TRUE").  
              setLabel("PROJECTCONTRACTSCHEDULE: Schedule").
              setSize(5);
      headblk.addField("HAS_DOC").
              setInsertable().
              setLabel("PROJECTCONTRACTHASDOC: Has Doc").
              setSize(5).setCheckBox("F,T");
      headblk.addField("STATE").
              setLabel("PROJECTCONTRACTSTATE: State").
              setReadOnly().
              setSize(20); 
      headblk.addField("UNDERTAKE_INFO").
              setInsertable().
              setHeight(3).
              setLabel("CONTRACTCOUNTERSIGNUNDERTAKEINFO: Undertake Info").
              setSize(120);


      headblk.setView("PROJECT_CONTRACT");
      headblk.defineCommand("PROJECT_CONTRACT_API","New__,Modify__,Remove__,Check__,Close__");
      headset = headblk.getASPRowSet();  
      headbar = mgr.newASPCommandBar(headblk);  
      headbar.enableMultirowAction();          
      headbar.addCustomCommand("printReport", "PROJECTCONTRACTPRINTREPORT: Print Report...");
      headtbl = mgr.newASPTable(headblk);          
      headtbl.setTitle("PROJECTCONTRACTTBLHEAD: Project Contracts");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(3);
      headlay.setDataSpan("CONTRACT_DESC", 12);
      headlay.setDataSpan("RELEVANT_DEPT", 12);
      headlay.setDataSpan("UNDERTAKE_INFO", 12);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CONTRACT_CLASS_NAME");
      headlay.setSimple("PURCH_TYPE_DESC");
      headlay.setSimple("FIRST_SIDE_NAME");
      headlay.setSimple("SECEND_SIDE_NAME");
      headlay.setSimple("FIRST_PERSON_NAME");
      headlay.setSimple("UNDERTAKER_NAME");
      headlay.setSimple("UNDERTAKE_DEPT_NAME");
      headlay.setSimple("RELEVANT_DEPT_NAME");
         

         project_contract_item_blk = mgr.newASPBlock("ITEM1");
         project_contract_item_blk.addField("ITEM0_OBJID").
                                   setHidden().
                                   setDbName("OBJID");
         project_contract_item_blk.addField("ITEM0_OBJVERSION").
                                   setHidden().
                                   setDbName("OBJVERSION");
         project_contract_item_blk.addField("ITEM0_CONTRACT_ID").
                                   setDbName("CONTRACT_ID").
                                   setMandatory().
                                   setInsertable().
                                   setHidden().
                                   setLabel("PROJECTCONTRACTITEMITEM0CONTRACTID: Contract Id").
                                   setSize(20);
         project_contract_item_blk.addField("ITEM0_PROJ_NO").
                                   setDbName("PROJ_NO").
                                   setMandatory().
                                   setInsertable().
                                   setHidden().
                                   setLabel("PROJECTCONTRACTITEMITEM0PROJNO: Proj No").
                                   setSize(20);
         // 2
         
         project_contract_item_blk.addField("ITEM_NO").
                                   setMandatory().
                                   setInsertable().
                                   setReadOnly().
                                   setLabel("PROJECTCONTRACTITEMITEMNO: Item No").
                                   setSize(20);
         
         project_contract_item_blk.addField("PRE_ITEM_NO").
                                   setInsertable().
                                   setReadOnly().
                                   setLabel("PROJECTCONTRACTITEMPREITEMNO: Pre Item No").
                                   setSize(20);
         
         project_contract_item_blk.addField("ITEM_DESC").
                                   setInsertable().
                                   setMandatory().
                                   setLabel("PROJECTCONTRACTITEMITEMDESC: Item Desc").
                                   setSize(30);
         
         // 3
         project_contract_item_blk.addField("ITEM_TYPE").
                                   setInsertable().    
                                   setCheckBox("FOLDER,ITEM").
                                   setCustomValidation("ITEM_TYPE", "ITEM_UNIT,ITEM_CURRENCY,ITEM_EXCHANGE_RATE,PRICE,ORIGINAL_QTY,ORIGINAL_TOTAL").
                                   setLabel("PROJECTCONTRACTITEMITEMTYPE: Item Type").
                                   setSize(10);
         project_contract_item_blk.addField("ITEM_UNIT").
                                   setInsertable().
                                   setDynamicLOV("ISO_UNIT").
                                   setLabel("PROJECTCONTRACTITEMITEMUNIT: Item Unit").
                                   setSize(10);
         project_contract_item_blk.addField("ITEM_UNIT_DESC").
                                   setReadOnly().
                                   setLabel("PROJECTCONTRACTITEMITEMUNITDESC: Item Unit Desc").
                                   setFunction("Iso_Unit_API.Get_Description(:ITEM_UNIT)").
                                   setSize(10);
         mgr.getASPField("ITEM_UNIT").setValidation("ITEM_UNIT_DESC");
         
         // 4
         project_contract_item_blk.addField("ITEM_CURRENCY").
                                   setInsertable().
                                   setLabel("PROJECTCONTRACTITEMITEMCURRENCY: Item Currency").
                                   setDynamicLOV("ISO_CURRENCY").
                                   setSize(10);
         project_contract_item_blk.addField("ITEM_CURRENCY_DESC").
                                   setReadOnly().
                                   setLabel("PROJECTCONTRACTITEMITEMCURRENCYDESC: Item Currency Desc").
                                   setFunction("Iso_Currency_API.Get_Description(:ITEM_CURRENCY)").
                                   setSize(10);
         mgr.getASPField("ITEM_CURRENCY").setValidation("ITEM_CURRENCY_DESC");
         project_contract_item_blk.addField("CONTRACT_ITEM_REP_TYPE").
                                    enumerateValues("Contract_Item_Rep_Type_API").
                                    setSelectBox().  
                                    setHidden().
                                    setInsertable().    
                                    setLabel("PROJECTCONTRACTITEMITEMREPTYPE: Item Rep Type").
                                    setSize(20);  
         project_contract_item_blk.addField("ITEM_EXCHANGE_RATE", "Number").
                                   setDbName("EXCHANGE_RATE").
                                   setInsertable().  
                                   setLabel("PROJECTCONTRACTITEMEXCHANGERATE: Exchange Rate").
                                   setSize(10);
         
         // 5
         project_contract_item_blk.addField("NEW_ITEM").
                                   setReadOnly().
                                   setHidden().
                                   setCheckBox("FALSE,TRUE").
                                   setLabel("PROJECTCONTRACTITEMNEWITEM: New Item").
                                   setSize(5);
         project_contract_item_blk.addField("PRICE", "Money").  
                                   setInsertable().
                                   setLabel("PROJECTCONTRACTITEMPRICE: Price").
                                   setCustomValidation("PRICE,ORIGINAL_QTY","ORIGINAL_TOTAL").
                                   setSize(20);
         // 6
         project_contract_item_blk.addField("ORIGINAL_QTY", "Number","#0.00").
                                   setInsertable().   
                                   setLabel("PROJECTCONTRACTITEMORIGINALQTY: Original Qty").
                                   setCustomValidation("PRICE,ORIGINAL_QTY","ORIGINAL_TOTAL").
                                   setSize(20);
         
         project_contract_item_blk.addField("ORIGINAL_TOTAL", "Money" ,"#0.00").
                                   setInsertable().  
                                   setLabel("PROJECTCONTRACTITEMORIGINALTOTAL: Original Total").
                                   setSize(20);
         
         
         project_contract_item_blk.addField("CHANGED_DIFF_QTY", "Number" ,"#0.00").
                                   setReadOnly().
                                   setFunction("PROJECT_CONTRACT_ITEM_API.Cal_Changed_Diff_Qty(:PROJ_NO,:CONTRACT_ID,:ITEM_NO)").
                                   setLabel("PROJECTCONTRACTITEMCHANGEDDIFFQTY: Changed Diff Qty").
                                   setSize(20);
         project_contract_item_blk.addField("CHANGED_DIFF_TOTAL", "Number" ,"#0.00").
                                   setReadOnly().
                                   setFunction("PROJECT_CONTRACT_ITEM_API.Cal_Changed_Diff_Total(:PROJ_NO,:CONTRACT_ID,:ITEM_NO)").
                                   setLabel("PROJECTCONTRACTITEMCHANGEDDIFFTOTAL: Changed Diff Total").
                                   setSize(20);
         // 7  
         project_contract_item_blk.addField("CHANGED_QTY", "Number" ,"#0.00").
                                   setReadOnly().
                                   setLabel("PROJECTCONTRACTITEMCHANGEDQTY: Changed Qty").
                                   setSize(20);
         project_contract_item_blk.addField("CHANGED_TOTAL", "Money" ,"#0.00").
                                   setReadOnly().   
                                   setLabel("PROJECTCONTRACTITEMCHANGEDTOTAL: Changed Total").
                                   setSize(20);
         //8
         project_contract_item_blk.addField("BUDGET_NO").
                                    setInsertable().
                                    setHidden().
                                    setLabel("PROJECTCONTRACTITEMBUDGETNO: Budget No").
                                    setSize(50);
         project_contract_item_blk.addField("BUDGET_LINE_NO").
                                   setDynamicLOV("PROJECT_BUDGET_LINE","PROJ_NO").
                                   setLOVProperty("WHERE", "STATUS='1'").  
                                   setLOVProperty("ORDER_BY", "SEQ_NO").
                                   setLOVProperty("TREE_PARE_FIELD", "PARENT_NO").
                                   setLOVProperty("TREE_DISP_FIELD", "BUDGET_LINE_NO,BUDGET_NAME").
                                   setInsertable().        
                                   setLabel("PROJECTCONTRACTITEMBUDGETLINENO: Budget Line No").
                                   setSize(50);
         project_contract_item_blk.addField("BUDGET_LINE_NAME").
                                   setReadOnly().        
                                   setFunction("PROJECT_BUDGET_LINE_API.Get_Budget_Name(:PROJ_NO,:BUDGET_LINE_NO)").
                                   setLabel("PROJECTCONTRACTITEMBUDGETLINENAME: Budget Line Name").
                                   setSize(50);
         mgr.getASPField("BUDGET_LINE_NO").setValidation("BUDGET_LINE_NAME");
         // 9    
         project_contract_item_blk.addField("ITEM_NOTE").
                                   setInsertable().
                                   setLabel("PROJECTCONTRACTITEMITEMNOTE: Item Note").
                                   setSize(140).setHeight(3);
        
         // Hidden fields
         project_contract_item_blk.addField("FULL_PATH").
                                   setHidden().
                                   setLabel("PROJECTCONTRACTITEMFULLPATH: Full Path").
                                   setSize(50); 
         
      project_contract_item_blk.setView("PROJECT_CONTRACT_ITEM");
      project_contract_item_blk.defineCommand("PROJECT_CONTRACT_ITEM_API","New__,Modify__,Remove__");
      project_contract_item_blk.setMasterBlock(headblk);
      project_contract_item_set = project_contract_item_blk.getASPRowSet();
      project_contract_item_bar = mgr.newASPCommandBar(project_contract_item_blk);
      project_contract_item_bar.defineCommand(project_contract_item_bar.OKFIND, "okFindITEM1");
      project_contract_item_bar.defineCommand(project_contract_item_bar.NEWROW, "newRowITEM1");
      project_contract_item_tbl = mgr.newASPTable(project_contract_item_blk);
      project_contract_item_tbl.setTitle("PROJECTCONTRACTITEMITEMHEAD1: ProjectContractItem");
      project_contract_item_tbl.enableRowSelect();
      project_contract_item_tbl.setWrap();
      project_contract_item_lay = project_contract_item_blk.getASPBlockLayout();
      project_contract_item_lay.setDefaultLayoutMode(project_contract_item_lay.MULTIROW_LAYOUT);
      project_contract_item_lay.setSimple("ITEM_UNIT_DESC");
      project_contract_item_lay.setSimple("ITEM_CURRENCY_DESC");
      project_contract_item_lay.setSimple("BUDGET_LINE_NAME"); 
      project_contract_item_lay.setDataSpan("ITEM_NOTE", 5);
      project_contract_item_lay.setDataSpan("BUDGET_LINE_NO", 5);   
        
      

      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("CONTRACTLINETAB: Contract Line" ), "javascript:commandSet('MAIN.activateContractLine', '')");
      headbar.addCustomCommand("activateContractLine", "");
   }  

   public void activateContractLine()
   {
      tabs.setActiveTab(1);
      okFindITEM1();
   }
   
   public void adjust() throws FndException
   {
      super.adjust();
      if(headlay.isMultirowLayout()){
         headset.storeSelections();
         ASPBuffer selected_fields=headset.getSelectedRows("OBJSTATE");
         for(int i=0;i<selected_fields.countItems();i++){
             ASPBuffer subBuff = selected_fields.getBufferAt(i);
             String state=subBuff.getValueAt(0);
             if( "Closed".equals(state) || "Checked".equals(state) ){
                 headbar.disableCommand(headbar.DELETE);
                 headbar.disableCommand(headbar.EDITROW);   
            }
         }  
      } else {
         if(headset.countRows()>0){
          String state = headset.getValue("OBJSTATE");
          if( headlay.isSingleLayout() && ("Closed".equals(state) || "Checked".equals(state))){ 
                headbar.disableCommand(headbar.DELETE);
                headbar.disableCommand(headbar.EDITROW);   
              project_contract_item_bar.disableCommand( project_contract_item_bar.DELETE );
              project_contract_item_bar.disableCommand(project_contract_item_bar.NEWROW);
              project_contract_item_bar.disableCommand( project_contract_item_bar.EDITROW );
              
          }  
      }
      }
      // fill function body
   }

   public void validate()
   {
       ASPManager mgr = getASPManager();  
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String val = mgr.readValue("VALIDATE");    
       float tempValue = 0;
       
       if ("PRICE".equals(val)) {         
         tempValue = ((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")))*((mgr.readValue("ORIGINAL_QTY")==null)?0:Float.parseFloat(mgr.readValue("ORIGINAL_QTY")));
         java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00"); 
         String strValue = df.format(tempValue);     
         mgr.responseWrite(String.valueOf(strValue)+"^");
       }
       else if("ORIGINAL_QTY".equals(val)){
         tempValue = ((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")))*((mgr.readValue("ORIGINAL_QTY")==null)?0:Float.parseFloat(mgr.readValue("ORIGINAL_QTY")));
         java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00"); 
         String strValue = df.format(tempValue);  
         mgr.responseWrite(String.valueOf(strValue)+"^");  
       }else if("ITEM_TYPE".equals(val)){
           if("FOLDER".equals(mgr.readValue("ITEM_TYPE"))){
             String txt =  "" + "^" +  "" + "^" +   "" + "^" +   "" + "^" +   "" + "^" +   "" + "^";
             mgr.responseWrite(txt);
           }  
       }        
       else if("DETAIN_NO".equals(val)){    
          String proj_no = mgr.readValue("PROJ_NO"); 
          String contract_id = mgr.readValue("CONTRACT_ID");
          cmd = trans.addCustomFunction("DETAINNAME", 
                "PROJECT_CONTRACT_DETAIN_API.Get_Detain_Name", "DETAIN_NAME");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no); 
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("DETAIN_NO");     
          
          cmd = trans.addCustomFunction("DETAINFLAG", 
                "PROJECT_CONTRACT_DETAIN_API.Get_Detain_Flag", "DETAIN_FLAG");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);    
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("DETAIN_NO");
          
          cmd = trans.addCustomFunction("SCALE", 
                "PROJECT_CONTRACT_DETAIN_API.Get_Scale", "SCALE");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("DETAIN_NO");  
          
          trans = mgr.validate(trans);   
         String detain_name = trans.getValue("DETAINNAME/DATA/DETAIN_NAME");
         String detain_flag = trans.getValue("DETAINFLAG/DATA/DETAIN_FLAG");
         String scale = trans.getValue("SCALE/DATA/SCALE");
         trans.clear();
         cmd.clear();        
         String txt = ((mgr.isEmpty(detain_name)) ? "" : detain_name) + "^" + ((mgr.isEmpty(detain_flag)) ? "FALSE" : detain_flag) + "^" + ((mgr.isEmpty(scale)) ? "1" : scale) + "^";
         mgr.responseWrite(txt);   
      }              
       mgr.endResponse();    
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
             String class_no = headset.getValue("CLASS_NO");
             String proj_no = headset.getValue("PROJ_NO");
             String contract_id = headset.getValue("CONTRACT_ID");
             if("WZ".equals(class_no) || "SB".equals(class_no)){
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptContractContactMat.raq&proj_no="+proj_no+"&contract_id="+contract_id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
             }else{
                appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptProjectContract.raq&proj_no="+proj_no+"&contract_id="+contract_id
                      + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
             }
         }
   }   
   //add by lhh 20141205 
   protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset, int rowNum) throws FndException
   {
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();

      if (rowset.countRows() > 0 && "VIEW_REF_FILE".equals(imageField.getName()))
      {
         if ("TRUE".equals(rowset.getValueAt(rowNum, "EXIST_DOC_REF"))) {
            imgSrc += "folder.gif";
            return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
         }
         else
         {
            return "";
         }
      }
      return "";
   }
   
   protected String getDescription()
   {
      return "CONTRACTCOUNTERSIGNDESC: Project Contract Countersign";
   }


   protected String getTitle()
   {
      return "CONTRACTCOUNTERSIGNTITLE: Project Contract Countersign";
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
      if((headlay.isSingleLayout() || headlay.isCustomLayout()) && headset.countRows() > 0 ){
          appendToHTML(tabs.showTabsInit());      
          if (tabs.getActiveTab() == 1)
              appendToHTML(project_contract_item_lay.show());
          appendToHTML(tabs.showTabsFinish());      
      }    
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}