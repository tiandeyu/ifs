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

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.genbaw.GenbawConstants;
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ContractCounterclaimMat extends HzASPPageProviderWf
{
  
   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.contrw.ContractCounterclaimMat");

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

   private ASPBlock contract_var_item_blk;
   private ASPRowSet contract_var_item_set;
   private ASPCommandBar contract_var_item_bar;
   private ASPTable contract_var_item_tbl;
   private ASPBlockLayout contract_var_item_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ContractCounterclaimMat (ASPManager mgr, String page_path)
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

      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT_VAR_NO")) )
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();  
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
      q.addWhereCondition("Project_Contract_API.Get_Schedule(PROJ_NO, CONTRACT_ID) = 'FALSE'");
      q.addWhereCondition("VAR_TYPE = 'Counterclaim' ");
      q.addWhereCondition("Project_Contract_API.Get_Is_Mat(PROJ_NO, CONTRACT_ID) = 'TRUE'");
      q.includeMeta("ALL");  
      if(mgr.dataTransfered())  
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("CONTRACTVARIATIONNODATA: No data found.");
         headset.clear();
      }
      eval( contract_var_item_set.syncItemSets() );
   }    



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.addWhereCondition("Project_Contract_API.Get_Schedule(PROJ_NO, CONTRACT_ID) = 'FALSE'");
      q.addWhereCondition("VAR_TYPE = 'Counterclaim' ");
      q.addWhereCondition("Project_Contract_API.Get_Is_Mat(PROJ_NO, CONTRACT_ID) = 'TRUE'");
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

      cmd = trans.addEmptyCommand("HEAD","CONTRACT_VARIATION_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("VAR_TYPE", "Counterclaim");  
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

      q = trans.addQuery(contract_var_item_blk);
      q.addWhereCondition("PROJ_NO = ? AND CONTRACT_ID = ? AND CONTRACT_VAR_NO = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("CONTRACT_ID", headset.getValue("CONTRACT_ID"));
      q.addParameter("CONTRACT_VAR_NO", headset.getValue("CONTRACT_VAR_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,contract_var_item_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","CONTRACT_VAR_ITEM_API.New__",contract_var_item_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_CONTRACT_ID", headset.getValue("CONTRACT_ID"));
      cmd.setParameter("ITEM0_CONTRACT_VAR_NO", headset.getValue("CONTRACT_VAR_NO"));
      cmd.setParameter("ITEM_CURRENCY","CNY");
      cmd.setParameter("EXCHANGE_RATE", "1");  
      cmd.setParameter("ITEM_TYPE", "ITEM");     
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      contract_var_item_set.addRow(data);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Perform Header and Item functions  ---------------------------
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
      headset.goTo(currow);
   }
   public void  release()
   {

      performHEAD( "Release__" );
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
              setDynamicLOV("GENERAL_PROJECT").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONTRACTVARIATIONPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setReadOnly().
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("CONTRACTVARIATIONGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("CONTRACT_ID").
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO"). 
              setLOVProperty("WHERE", "CLASS_NO IN ('SB','WX','WZ')").
              setLOVProperty("ORDER_BY", "CONTRACT_ID").  
              setMandatory().
              setInsertable().
              setLabel("CONTRACTVARIATIONCONTRACTID: Contract Id").
              setSize(30);    
      headblk.addField("CONTRACT_DESC").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC(:PROJ_NO,:CONTRACT_ID)").
              setLabel("CONTRACTVARIATIONCONTRACTDESC: Contract Desc").
              setSize(30); 
      mgr.getASPField("CONTRACT_ID").setValidation("CONTRACT_DESC");
      headblk.addField("CONTRACT_VAR_NO","Number").
              setHidden().  
              setInsertable().
              setLabel("CONTRACTVARIATIONCONTRACTVARNO: Contract Var No").
              setSize(30);
      headblk.addField("CONTRACT_VAR_DESC").
              setInsertable().
              setLabel("CONTRACTVARIATIONCONTRACTVARDESC: Contract Var Desc").
              setSize(30);
      headblk.addField("CONTRACT_VARIATION_TYPE").
              enumerateValues("Contract_Variation_Type_API").
              setReadOnly().
              setHidden().  
              setSelectBox().    
              setLabel("CONTRACTVARIATIONCONTRACTVARIATIONTYPE: Contract Variation Type").
              setSize(30);
      headblk.addField("CONTRACT_VARIATION_TYPE_DB").
              setReadOnly().
              setHidden().
              setLabel("CONTRACTVARIATIONCONTRACTVARIATIONTYPEDB: Contract Variation Type Db").
              setSize(30);
      headblk.addField("CONTRACT_VAR_DATE","Date").
              setInsertable().
              setLabel("CONTRACTVARIATIONCONTRACTVARDATE: Contract Var Date").
              setSize(30);
      headblk.addField("REPORT_TOTAL","Number","#0.00").
              setInsertable().
              setLabel("CONTRACTVARIATIONREPORTTOTAL: Report Total").
              setSize(30);
      headblk.addField("INSPECT_TOTAL","Number","#0.00").
              setInsertable().
              setLabel("CONTRACTVARIATIONINSPECTTOTAL: Inspect Total").
              setSize(30);
      // 9.1
      headblk.addField("SIGN_PRICE", "Money","#0.00").
            unsetInsertable().
            setReadOnly().
            setFunction("PROJECT_CONTRACT_API.Cal_Ori_Contract_Total(:PROJ_NO, :CONTRACT_ID)").
            setLabel("CONTRACTVARIATIONREQSIGNPRICE: Sign Price").
            setSize(20);
      headblk.addField("CHANGING_PRICE", "Money","#0.00").
            unsetInsertable().
            setReadOnly().
            setFunction("CONTRACT_VARIATION_API.Cal_VR_Total(:PROJ_NO, :CONTRACT_ID,:CONTRACT_VAR_NO)").
            setLabel("CONTRACTVARIATIONREQCHANGINGPRICE: Changing Price").
            setSize(20);
      
      // 9.2
      headblk.addField("CHANGED_PRICE", "Money","#0.00").
            unsetInsertable().
            setReadOnly().
            setFunction("CONTRACT_VARIATION_API.Cal_All_VR_Total(:PROJ_NO, :CONTRACT_ID,:CONTRACT_VAR_NO)").
            setLabel("CONTRACTVARIATIONREQCHANGEDPRICE: Changed Price").
            setSize(20);
      headblk.addField("CHANGED_PRICE_PRO", "String").
            unsetInsertable().
            setReadOnly().
            setFunction("CONTRACT_VARIATION_API.Cal_All_VR_Pro(:PROJ_NO, :CONTRACT_ID,:CONTRACT_VAR_NO)").
            setLabel("CONTRACTVARIATIONREQCHANGEDPRICEPRO: Changed Price Pro").
            setSize(20);
      headblk.addField("STATE").
               setLabel("CONTRACTVARIATIONSTATE: State").
               setSize(30).
               setReadOnly();   
      
      headblk.addField("GIST_CONTENT").
              setInsertable().  
              setLabel("CONTRACTVARIATIONGISTCONTENT: Gist Content").
              setSize(140).
              setDefaultNotVisible().
              setHeight(4);
      headblk.addField("FILES").
              setInsertable().
              setLabel("CONTRACTVARIATIONFILES: Files").
              setSize(140).
              setDefaultNotVisible().
              setHeight(4);
      headblk.addField("COST_EFFECT").
              setInsertable().
              setLabel("CONTRACTVARIATIONCOSTEFFECT: Cost Effect").
              setSize(140).
              setDefaultNotVisible().
              setHeight(4);
      headblk.addField("SCHEDULE_EFFECT").
              setInsertable().
              setLabel("CONTRACTVARIATIONSCHEDULEEFFECT: Schedule Effect").
              setSize(140).
              setDefaultNotVisible().
              setHeight(4);
      headblk.addField("ITEM_EFFECT").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONTRACTVARIATIONITEMEFFECT: Item Effect").
              setSize(140).
              setHeight(4);
      headblk.addField("OTHER_EFFECT").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONTRACTVARIATIONOTHEREFFECT: Other Effect").
              setSize(140).
              setHeight(4);
      headblk.addField("VAR_NEED").
              setInsertable().
              setLabel("CONTRACTVARIATIONVARNEED: Var Need").
              setSize(140).
              setDefaultNotVisible().
              setHeight(4);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("CONTRACTVARIATIONNOTE: Note").
              setSize(140).
              setDefaultNotVisible().
              setHeight(4);
      headblk.addField("VAR_TYPE").
              setInsertable().
              setLabel("CONTRACTVARIATIONVARTYPE: Var Type").
              setHidden();
      headblk.addField("FLOW_TITLE").
              setWfProperties().
              setReadOnly().
              setHidden().
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC(:PROJ_NO,:CONTRACT_ID)").
              setLabel("FLOWTITLE: Flow Title");
      
      headblk.setView("CONTRACT_VARIATION");
      headblk.defineCommand("CONTRACT_VARIATION_API","New__,Modify__,Remove__,Release__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addSecureCustomCommand("Release","CONTRACTCOUNTERCLAIMOTHRELEASE: Release Contract Counterclaim", "CONTRACT_VARIATION_API.Release__");
      headbar.addCommandValidConditions("Release",     "OBJSTATE",    "Enable",      "Initialization");  

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONTRACTVARIATIONTBLHEAD: Contract Variations");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDataSpan("GIST_CONTENT", 6);
      headlay.setDataSpan("FILES", 6);
      headlay.setDataSpan("COST_EFFECT", 6);
      headlay.setDataSpan("SCHEDULE_EFFECT", 6);
      headlay.setDataSpan("ITEM_EFFECT", 6);
      headlay.setDataSpan("OTHER_EFFECT", 6);
      headlay.setDataSpan("NOTE", 6);
      headlay.setDataSpan("VAR_NEED", 6);    
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CONTRACT_DESC");    
      


      contract_var_item_blk = mgr.newASPBlock("ITEM1");
      contract_var_item_blk.addField("ITEM0_OBJID").
                            setHidden().
                            setDbName("OBJID");
      contract_var_item_blk.addField("ITEM0_OBJVERSION").
                            setHidden().
                            setDbName("OBJVERSION");
      contract_var_item_blk.addField("ITEM0_PROJ_NO").
                            setDbName("PROJ_NO").
                            setMandatory().
                            setHidden().
                            setInsertable().
                            setLabel("CONTRACTVARITEMITEM0PROJNO: Proj No").
                            setSize(50);
      contract_var_item_blk.addField("ITEM0_CONTRACT_ID").
                            setDbName("CONTRACT_ID").
                            setMandatory().
                            setInsertable().
                            setHidden().
                            setLabel("CONTRACTVARITEMITEM0CONTRACTID: Contract Id").
                            setSize(100);
      contract_var_item_blk.addField("ITEM0_CONTRACT_VAR_NO","Number").
                            setDbName("CONTRACT_VAR_NO").
                            setHidden().    
                            setInsertable().
                            setLabel("CONTRACTVARITEMITEM0CONTRACTVARNO: Contract Var No").
                            setSize(30);
      contract_var_item_blk.addField("ITEM_NO").
                            setMandatory().
                            setDynamicLOV("PROJECT_CONTRACT_ITEM", "PROJ_NO,CONTRACT_ID").
                            setLOVProperty("WHERE", "ITEM_TYPE = 'ITEM'").
                            setLOVProperty("ORDER_BY", "FULL_PATH").   
                            setCustomValidation("PROJ_NO,CONTRACT_ID,ITEM_NO", "ITEM_DESC,ITEM_TYPE,ITEM_CURRENCY,ITEM_UNIT,EXCHANGE_RATE,PRICE,MAT_NO,BUDGET_LINE_NO").
                            setInsertable().                    
                            setLabel("CONTRACTVARITEMITEMNO: No").
                            setSize(30);  
      contract_var_item_blk.addField("ITEM_DESC").
                            setInsertable().
                            setLabel("CONTRACTVARITEMITEMDESC: Desc").
                            setSize(30);
      contract_var_item_blk.addField("ITEM_TYPE").
                            setInsertable().
                            setCustomValidation("ITEM_TYPE", "ITEM_UNIT,ITEM_CURRENCY,EXCHANGE_RATE,PRICE,QTY,TOTAL").
                            setCheckBox("FOLDER,ITEM").
                            setLabel("CONTRACTVARITEMITEMTYPE: Type").
                            setSize(30);
      contract_var_item_blk.addField("ITEM_CURRENCY").
                            setInsertable().
                            setDynamicLOV("ISO_CURRENCY").
                            setLabel("CONTRACTVARITEMITEMCURRENCY: Currency").
                            setSize(3);
      contract_var_item_blk.addField("ITEM_CURRENCY_DESC").
                            setReadOnly().
                            setLabel("CONTRACTVARITEMITEMCURRENCYDESC: Item Currency Desc").
                            setFunction("Iso_Currency_API.Get_Description(:ITEM_CURRENCY)").
                            setSize(10);
      mgr.getASPField("ITEM_CURRENCY").setValidation("ITEM_CURRENCY_DESC");
      contract_var_item_blk.addField("ITEM_UNIT").
                            setInsertable().
                            setDynamicLOV("ISO_UNIT").
                            setLabel("CONTRACTVARITEMITEMUNIT: Unit").
                            setSize(30);
      contract_var_item_blk.addField("ITEM_UNIT_DESC").
                            setReadOnly().
                            setLabel("CONTRACTVARITEMITEMUNITDESC: Item Unit Desc").
                            setFunction("Iso_Unit_API.Get_Description(:ITEM_UNIT)").
                            setSize(30);
      mgr.getASPField("ITEM_UNIT").setValidation("ITEM_UNIT_DESC");
      
      contract_var_item_blk.addField("EXCHANGE_RATE","Number").
                            setInsertable().
                            setLabel("CONTRACTVARITEMEXCHANGERATE: Exchange Rate").
                            setSize(30);
      
      contract_var_item_blk.addField("PRICE","Number","#0.00").
                            setInsertable().
                            setCustomValidation("PRICE,QTY", "TOTAL").
                            setLabel("CONTRACTVARITEMPRICE: Price").
                            setSize(30);
      contract_var_item_blk.addField("QTY","Number","#0.00").
                            setInsertable().
                            setCustomValidation("PRICE,QTY", "TOTAL").
                            setLabel("CONTRACTVARITEMQTY: Qty").  
                            setSize(30);
      contract_var_item_blk.addField("TOTAL","Number","#0.00").
                            setInsertable().
                            setLabel("CONTRACTVARITEMTOTAL: Total").
                            setSize(30);
      contract_var_item_blk.addField("CONTRACT_ITEM_REP_TYPE").
                            enumerateValues("Contract_Item_Rep_Type_API").
                            setSelectBox().
                            setHidden().  
                            setInsertable().
                            setLabel("CONTRACTVARITEMCONTRACTITEMREPTYPE: Contract Item Rep Type").
                            setSize(200);
      contract_var_item_blk.addField("NEW_ITEM").
                            setReadOnly().
                            setCheckBox("FALSE,TRUE").
                            setLabel("CONTRACTVARITEMNEWITEM: New Item").
                            setSize(5);
      contract_var_item_blk.addField("BUDGET_NO").
                                 setInsertable().
                                 setHidden().
                                 setLabel("CONTRACTVARITEMBUDGETNO: Budget No").
                                 setSize(30);
      contract_var_item_blk.addField("BUDGET_LINE_NO").
                                setDynamicLOV("PROJECT_BUDGET_LINE","PROJ_NO").
                                setLOVProperty("WHERE", "STATUS='1'").  
                                setLOVProperty("ORDER_BY", "SEQ_NO").
                                setLOVProperty("TREE_PARE_FIELD", "PARENT_NO").
                                setLOVProperty("TREE_DISP_FIELD", "BUDGET_LINE_NO,BUDGET_NAME").
                                setInsertable().        
                                setLabel("CONTRACTVARITEMBUDGETLINENO: Budget Line No").
                                setSize(30);      
      contract_var_item_blk.addField("BUDGET_LINE_NAME").
                                setReadOnly().        
                                setFunction("PROJECT_BUDGET_LINE_API.Get_Budget_Name(:PROJ_NO,:BUDGET_LINE_NO)").
                                setLabel("CONTRACTVARITEMBUDGETLINENAME: Budget Line Name").
                                setSize(30);
      mgr.getASPField("BUDGET_LINE_NO").setValidation("BUDGET_LINE_NAME");
      contract_var_item_blk.addField("MAT_NO").  
                            setInsertable().  
                            setDynamicLOV("MAT_CODE"). 
                            setLabel("CONTRACTVARITEMMATNO: Mat No").
                            setSize(5);
      contract_var_item_blk.addField("MAT_NAME").
                            setReadOnly(). 
                            setFunction("MAT_CODE_API.Get_Mat_Name(:ITEM0_PROJ_NO,:MAT_NO)").
                            setLabel("CONTRACTVARITEMMATNAME: Mat Name").
                            setSize(30);
      mgr.getASPField("MAT_NO").setValidation("MAT_NAME");  
      contract_var_item_blk.addField("PRICE_CODE").
                            setInsertable().
                            setHidden(). 
                            setLabel("CONTRACTVARITEMPRICECODE: Price Code").
                            setSize(30);
      contract_var_item_blk.addField("FULL_PATH").
                            setInsertable().
                            setHidden().
                            setLabel("CONTRACTVARITEMFULLPATH: Full Path").
                            setSize(140);
      contract_var_item_blk.addField("ITEM_NOTE").
                            setInsertable().
                            setLabel("CONTRACTVARITEMITEMNOTE: Note").
                            setHeight(3).
                            setSize(140);
      contract_var_item_blk.setView("CONTRACT_VAR_ITEM");
      contract_var_item_blk.defineCommand("CONTRACT_VAR_ITEM_API","New__,Modify__,Remove__");
      contract_var_item_blk.setMasterBlock(headblk);
      contract_var_item_set = contract_var_item_blk.getASPRowSet();
      contract_var_item_bar = mgr.newASPCommandBar(contract_var_item_blk);
      contract_var_item_bar.defineCommand(contract_var_item_bar.OKFIND, "okFindITEM1");
      contract_var_item_bar.defineCommand(contract_var_item_bar.NEWROW, "newRowITEM1");
      contract_var_item_tbl = mgr.newASPTable(contract_var_item_blk);
      contract_var_item_tbl.setTitle("CONTRACTVARITEMITEMHEAD1: ContractVarItem");
      contract_var_item_tbl.enableRowSelect();    
      contract_var_item_tbl.setWrap();
      contract_var_item_lay = contract_var_item_blk.getASPBlockLayout();
      contract_var_item_lay.setDefaultLayoutMode(contract_var_item_lay.MULTIROW_LAYOUT);
      contract_var_item_lay.setSimple("ITEM_UNIT_DESC");
      contract_var_item_lay.setSimple("ITEM_CURRENCY_DESC");
      contract_var_item_lay.setSimple("MAT_NAME"); 
      contract_var_item_lay.setSimple("BUDGET_LINE_NAME");     
      contract_var_item_lay.setDataSpan("ITEM_NOTE", 5);   
      
      
      //changeLable
      mgr.getASPField("CONTRACT_VAR_NO").
      setLabel("CONTRACTCOUNTERCLAIMCOUNTERCLAIMNO: Counterclaim No");
      
      mgr.getASPField("CONTRACT_VAR_DESC").
            setLabel("CONTRACTCOUNTERCLAIMCOUNTERCLAIMDESC: Counterclaim Desc").
            setMandatory();
      
      mgr.getASPField("GIST_CONTENT").
            setLabel("CONTRACTCOUNTERCLAIMGISTCONTENT: Gist Content");
        
      mgr.getASPField("FILES").
            setLabel("CONTRACTCOUNTERCLAIMFILES: Files");
      
      mgr.getASPField("COST_EFFECT").
            setLabel("CONTRACTCOUNTERCLAIMCOSTEFFECT: Cost Effect");
      
      mgr.getASPField("SCHEDULE_EFFECT").
            setLabel("CONTRACTCOUNTERCLAIMSCHEDULEEFFECT: Schedule Effect");
      
      mgr.getASPField("ITEM_EFFECT").
            setLabel("CONTRACTCOUNTERCLAIMITEMEFFECT: Item Effect");
      
      mgr.getASPField("OTHER_EFFECT").
            setLabel("CONTRACTCOUNTERCLAIMOTHEREFFECT: Other Effect");
      
      mgr.getASPField("VAR_NEED").
            setLabel("CONTRACTCOUNTERCLAIMVARNEED: Var Need");
      
      mgr.getASPField("NOTE").
            setLabel("CONTRACTCOUNTERCLAIMNOTE: Note");
      
      
      // Hidden fields
      mgr.getASPField("REPORT_TOTAL").
            setHidden();
      mgr.getASPField("INSPECT_TOTAL").
            setHidden();
      mgr.getASPField("CONTRACT_VAR_DATE").     
            setHidden();  
      mgr.getASPField("VAR_NEED").    
            setHidden();    

   }    


   public void adjust() throws FndException
   {
      super.adjust();
      // fill function body
      if(headlay.isMultirowLayout()){
         headset.storeSelections();  
         ASPBuffer selected_fields=headset.getSelectedRows("OBJSTATE");
         for(int i=0;i<selected_fields.countItems();i++){
             ASPBuffer subBuff = selected_fields.getBufferAt(i);
             String state=subBuff.getValueAt(0);
             if( "Released".equals(state)){
                 headbar.disableCommand(headbar.DELETE);
                 headbar.disableCommand(headbar.EDITROW);   
            }
         }   
      } else {
         if(headset!=null&&headset.countRows()>0){
          String state = headset.getValue("OBJSTATE");      
          if( headlay.isSingleLayout() && ("Released".equals(state))){ 
             headbar.disableCommand(headbar.DELETE);
             headbar.disableCommand(headbar.EDITROW);       
             contract_var_item_bar.disableCommand( contract_var_item_bar.DELETE );
             contract_var_item_bar.disableCommand(contract_var_item_bar.NEWROW);
             contract_var_item_bar.disableCommand( contract_var_item_bar.EDITROW );
      }
      }  
      }  
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   public void validate()
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String txt = "";  
       String itemDesc = "";
       String itemType = "";
       String itemCurrency = "";
       String itemUnit = "";
       String exchangeRate = "";
       String price = "";       
       String mat_no = "";  
       String budget_line_no = "";  
       String val = mgr.readValue("VALIDATE");    
       float tempValue = 0;
       if ("PRICE".equals(val)) {         
         tempValue = ((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")))*((mgr.readValue("QTY")==null)?0:Float.parseFloat(mgr.readValue("QTY")));
         java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00"); 
         String strValue = df.format(tempValue);  
         mgr.responseWrite(String.valueOf(strValue)+"^");
       }      
       else if("QTY".equals(val)){
         tempValue = ((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")))*((mgr.readValue("QTY")==null)?0:Float.parseFloat(mgr.readValue("QTY")));
         java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00"); 
         String strValue = df.format(tempValue);  
         mgr.responseWrite(String.valueOf(strValue)+"^");  
       }
       if("ITEM_NO".equals(val)){    
          String proj_no = mgr.readValue("PROJ_NO");
          String contract_id = mgr.readValue("CONTRACT_ID");
          cmd = trans.addCustomFunction("ITEMDESC", 
                "PROJECT_CONTRACT_ITEM_API.Get_Item_Desc", "ITEM_DESC");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");             
            
          cmd = trans.addCustomFunction("ITEMTYPE", 
                "PROJECT_CONTRACT_ITEM_API.Get_Item_Type", "ITEM_TYPE");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");             
          
          cmd = trans.addCustomFunction("ITEMCURRENCY", 
                "PROJECT_CONTRACT_ITEM_API.Get_Item_Currency", "ITEM_CURRENCY");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");              
          
          cmd = trans.addCustomFunction("ITEMUNIT", 
                "PROJECT_CONTRACT_ITEM_API.Get_Item_Unit", "ITEM_UNIT");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");            
          
          cmd = trans.addCustomFunction("EXCHANGERATE", 
                "PROJECT_CONTRACT_ITEM_API.Get_Exchange_Rate", "EXCHANGE_RATE");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");            
          
          cmd = trans.addCustomFunction("PRICE", 
                "PROJECT_CONTRACT_ITEM_API.Get_Price", "PRICE");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");        
          
          cmd = trans.addCustomFunction("MAT_NO", 
                "PROJECT_CONTRACT_ITEM_API.Get_Mat_No", "MAT_NO");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");     
          
          cmd = trans.addCustomFunction("BUDGET_LINE_NO", 
                "PROJECT_CONTRACT_ITEM_API.Get_Budget_Line_No", "BUDGET_LINE_NO");
          cmd.addParameter("ITEM0_PROJ_NO",proj_no);
          cmd.addParameter("ITEM0_CONTRACT_ID",contract_id);
          cmd.addParameter("ITEM_NO");  
          
          trans = mgr.validate(trans);   
          itemDesc = trans.getValue("ITEMDESC/DATA/ITEM_DESC");
          itemType = trans.getValue("ITEMTYPE/DATA/ITEM_TYPE");
          itemCurrency = trans.getValue("ITEMCURRENCY/DATA/ITEM_CURRENCY");
          itemUnit = trans.getValue("ITEMUNIT/DATA/ITEM_UNIT");
          exchangeRate = trans.getValue("EXCHANGERATE/DATA/EXCHANGE_RATE");
          price = trans.getValue("PRICE/DATA/PRICE");   
          mat_no = trans.getValue("MAT_NO/DATA/MAT_NO");   
          budget_line_no = trans.getValue("BUDGET_LINE_NO/DATA/BUDGET_LINE_NO"); 
          trans.clear();
          cmd.clear();        
          txt = ((mgr.isEmpty(itemDesc)) ? "" : itemDesc) + "^" + ((mgr.isEmpty(itemType)) ? "ITEM" : itemType) + "^" + ((mgr.isEmpty(itemCurrency)) ? "CNY" : itemCurrency) + "^" + ((mgr.isEmpty(itemUnit)) ? "" : itemUnit) + "^" + ((mgr.isEmpty(exchangeRate)) ? "1" : exchangeRate) + "^" + ((mgr.isEmpty(price)) ? "" : price) + "^" + ((mgr.isEmpty(mat_no)) ? "" : mat_no) + "^" + ((mgr.isEmpty(budget_line_no)) ? "" : budget_line_no) + "^";
          mgr.responseWrite(txt);             
       }else if("ITEM_TYPE".equals(val)){
           if("FOLDER".equals(mgr.readValue("ITEM_TYPE"))){
             txt =  "" + "^" +  "" + "^" +   "" + "^" +   "" + "^" +   "" + "^" +   "" + "^";
             mgr.responseWrite(txt);
           }         
       }                  
       mgr.endResponse();     
   }  
   
   protected String getDescription()
   {
      return "CONTRACTCOUNTERCLAIMMATDESC: Contract Counterclaim Mat";
   }
  

   protected String getTitle()
   {
      return "CONTRACTCOUNTERCLAIMMATTITLE: Contract Counterclaim Mat";
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
      if (contract_var_item_lay.isVisible())
          appendToHTML(contract_var_item_lay.show());
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}
  