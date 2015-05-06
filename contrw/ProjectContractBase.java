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

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.genbaw.GenbawConstants;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ProjectContractBase extends ASPPageProvider {

	// -----------------------------------------------------------------------------
	// ---------- Static constants ------------------------------------------------
	// -----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.contrw.ProjectContractBase");

	// -----------------------------------------------------------------------------
	// ---------- Header Instances created on page creation --------
	// -----------------------------------------------------------------------------

	protected ASPBlock headblk;
	protected ASPRowSet headset;
	protected ASPCommandBar headbar;
	protected ASPTable headtbl;
	protected ASPBlockLayout headlay;

	// -----------------------------------------------------------------------------
	// ---------- Item Instances created on page creation --------
	// -----------------------------------------------------------------------------

	protected ASPBlock project_contract_item_blk;
	protected ASPRowSet project_contract_item_set;
	protected ASPCommandBar project_contract_item_bar;
	protected ASPTable project_contract_item_tbl;
	protected ASPBlockLayout project_contract_item_lay;
	ASPTransactionBuffer trans;  
	// -----------------------------------------------------------------------------
	// ------------------------ Construction ---------------------------
	// -----------------------------------------------------------------------------

	public ProjectContractBase(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}

   protected String getDefProj() {
      ASPManager mgr = getASPManager();
      ASPContext ctx =mgr.getASPContext();
      return ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT);
   }    
   
	public void run() {
		ASPManager mgr = getASPManager();
      if( mgr.commandBarActivated() )    
         eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("CONTRACT_ID")))
			okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();  
      else 
         okFind();
		adjust();
	}

	// -----------------------------------------------------------------------------
	// ------------------------ Command Bar functions ---------------------------
	// -----------------------------------------------------------------------------
	public void okFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		mgr.createSearchURL(headblk);
		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		
		q.setOrderByClause("CONTRACT_ID");
		
		mgr.querySubmit(trans, headblk);
		if (headset.countRows() == 0) {
			mgr.showAlert("PROJECTCONTRACTNODATA: No data found.");
			headset.clear();
		}
		eval(project_contract_item_set.syncItemSets());
		okFindITEM1();
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

	public void newRow() {
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPBuffer data;
		ASPCommand cmd;

		cmd = trans.addEmptyCommand("HEAD", "PROJECT_CONTRACT_API.New__", headblk);
		cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("PROJ_NO", this.getDefProj());  
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

	// -----------------------------------------------------------------------------
	// ---------------------- Item block cmd bar functions -------------------------
	// -----------------------------------------------------------------------------

	public void okFindITEM1()
	{
		ASPManager mgr = getASPManager();
		
		if (headset.countRows() == 0)
			return;
		
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		int headrowno;

		q = trans.addQuery(project_contract_item_blk);
		q.addWhereCondition("PROJ_NO = ? AND CONTRACT_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("CONTRACT_ID", headset.getValue("CONTRACT_ID"));
		q.includeMeta("ALL");    
		    
		q.setOrderByClause("FULL_PATH");
		
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans, project_contract_item_blk);
		headset.goTo(headrowno); 
	}

	public void newRowITEM1()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;

		cmd = trans.addEmptyCommand("ITEM1", "PROJECT_CONTRACT_ITEM_API.New__", project_contract_item_blk);
		cmd.setOption("ACTION", "PREPARE");
		cmd.setParameter("ITEM0_CONTRACT_ID", headset.getValue("CONTRACT_ID"));
		cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM_CURRENCY","CNY");
      cmd.setParameter("ITEM_EXCHANGE_RATE", "1");  
      cmd.setParameter("ITEM_TYPE", "ITEM");     
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM1/DATA");
		project_contract_item_set.addRow(data);
	}
	
	public void refreshITEM1()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addEmptyQuery(project_contract_item_blk);
      q.addWhereCondition("PROJ_NO = ? AND CONTRACT_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("CONTRACT_ID", headset.getValue("CONTRACT_ID"));  
      q.includeMeta("ALL");  
      headrowno = headset.getCurrentRowNo();
      
      q.setOrderByClause("FULL_PATH");
      
      mgr.querySubmit(trans, project_contract_item_blk);
      headset.goTo(headrowno);
   }  
	// -----------------------------------------------------------------------------
	// ------------------------ Predefines Head ---------------------------
	// -----------------------------------------------------------------------------

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
		// 1
		headblk.addField("CONTRACT_ID").
				setMandatory().
				setInsertable().
				setUpperCase().
				setLabel("PROJECTCONTRACTCONTRACTID: Contract Id").
				setSize(20);
		headblk.addField("PRE_CONTRACT_NO").
              setInsertable().
              setLabel("PROJECTCONTRACTPRECONTRACTNO: Pre Contract No").
              setSize(20);
		headblk.addField("CONTRACT_DESC").
				setInsertable().
				setLabel("PROJECTCONTRACTCONTRACTDESC: Contract Desc").
				setSize(30);
		
		// 2
		headblk.addField("CLASS_NO").
				setInsertable().
				setHidden().
				setLabel("PROJECTCONTRACTCONTRACTCLASS: Contract Class").
				setSize(10);    
		headblk.addField("SCHEDULE").
				setInsertable().
				setHidden().
				setCheckBox("FALSE,TRUE").
				setLabel("PROJECTCONTRACTSCHEDULE: Schedule").
				setSize(5);
		    
		headblk.addField("STATE").
				setReadOnly().
				setLabel("PROJECTCONTRACTSTATTE: State").
				setSize(20);    
		headblk.setView("PROJECT_CONTRACT");
		headblk.defineCommand("PROJECT_CONTRACT_API", "New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();
		headbar = mgr.newASPCommandBar(headblk);      
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("PROJECTCONTRACTTBLHEAD: Project Contracts");
		headtbl.enableRowSelect();
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);      
		headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
  
		//
		// Project Contract Items  
		//
		    
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
                                 setSize(30);
       
       project_contract_item_blk.addField("PRE_ITEM_NO").
                                 setInsertable().
                                 setReadOnly().
                                 setLabel("PROJECTCONTRACTITEMPREITEMNO: Pre Item No").
                                 setSize(30);
       
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
                                 setSize(30);
       project_contract_item_blk.addField("ITEM_UNIT").
                                 setInsertable().
                                 setDynamicLOV("ISO_UNIT").
                                 setLabel("PROJECTCONTRACTITEMITEMUNIT: Item Unit").
                                 setSize(30);
       project_contract_item_blk.addField("ITEM_UNIT_DESC").
                                 setReadOnly().
                                 setLabel("PROJECTCONTRACTITEMITEMUNITDESC: Item Unit Desc").
                                 setFunction("Iso_Unit_API.Get_Description(:ITEM_UNIT)").
                                 setSize(30);
       mgr.getASPField("ITEM_UNIT").setValidation("ITEM_UNIT_DESC");
       
       // 4
       project_contract_item_blk.addField("ITEM_CURRENCY").
                                 setInsertable().
                                 setLabel("PROJECTCONTRACTITEMITEMCURRENCY: Item Currency").
                                 setDynamicLOV("ISO_CURRENCY").
                                 setSize(30);
       project_contract_item_blk.addField("ITEM_CURRENCY_DESC").
                                 setReadOnly().
                                 setLabel("PROJECTCONTRACTITEMITEMCURRENCYDESC: Item Currency Desc").
                                 setFunction("Iso_Currency_API.Get_Description(:ITEM_CURRENCY)").
                                 setSize(30);
       mgr.getASPField("ITEM_CURRENCY").setValidation("ITEM_CURRENCY_DESC");
       project_contract_item_blk.addField("CONTRACT_ITEM_REP_TYPE").
                                  enumerateValues("Contract_Item_Rep_Type_API").
                                  setSelectBox().  
                                  setHidden().
                                  setInsertable().    
                                  setLabel("PROJECTCONTRACTITEMITEMREPTYPE: Item Rep Type").
                                  setSize(30);  
       project_contract_item_blk.addField("ITEM_EXCHANGE_RATE", "Number").
                                 setDbName("EXCHANGE_RATE").
                                 setInsertable().  
                                 setLabel("PROJECTCONTRACTITEMEXCHANGERATE: Exchange Rate").
                                 setSize(30);
       
       // 5
       project_contract_item_blk.addField("NEW_ITEM").
                                 setReadOnly().
                                 setHidden().
                                 setCheckBox("FALSE,TRUE").
                                 setLabel("PROJECTCONTRACTITEMNEWITEM: New Item").
                                 setSize(5);
       project_contract_item_blk.addField("PRICE", "Money" ,"#0.00").
                                 setInsertable().
                                 setLabel("PROJECTCONTRACTITEMPRICE: Price").
                                 setCustomValidation("PRICE,ORIGINAL_QTY","ORIGINAL_TOTAL").
                                 setSize(30);
       // 6
       project_contract_item_blk.addField("ORIGINAL_QTY", "Number","#0.00").
                                 setInsertable().   
                                 setLabel("PROJECTCONTRACTITEMORIGINALQTY: Original Qty").
                                 setCustomValidation("PRICE,ORIGINAL_QTY","ORIGINAL_TOTAL").
                                 setSize(30);
       
       project_contract_item_blk.addField("ORIGINAL_TOTAL", "Money" ,"#0.00").
                                 setInsertable().  
                                 setLabel("PROJECTCONTRACTITEMORIGINALTOTAL: Original Total").
                                 setSize(30);
       
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
                                 setSize(30);
       project_contract_item_blk.addField("CHANGED_TOTAL", "Money" ,"#0.00").
                                 setReadOnly().   
                                 setLabel("PROJECTCONTRACTITEMCHANGEDTOTAL: Changed Total").
                                 setSize(30);
       //8
       project_contract_item_blk.addField("BUDGET_NO").
                                  setInsertable().
                                  setHidden().
                                  setLabel("PROJECTCONTRACTITEMBUDGETNO: Budget No").
                                  setSize(30);
       project_contract_item_blk.addField("BUDGET_LINE_NO").
                                 setDynamicLOV("PROJECT_BUDGET_LINE","PROJ_NO").
                                 setLOVProperty("WHERE", "STATUS='1'").  
                                 setLOVProperty("ORDER_BY", "SEQ_NO").
                                 setLOVProperty("TREE_PARE_FIELD", "PARENT_NO").
                                 setLOVProperty("TREE_DISP_FIELD", "BUDGET_LINE_NO,BUDGET_NAME").
                                 setInsertable().        
                                 setLabel("PROJECTCONTRACTITEMBUDGETLINENO: Budget Line No").
                                 setSize(30);
       project_contract_item_blk.addField("BUDGET_LINE_NAME").
                                 setReadOnly().        
                                 setFunction("PROJECT_BUDGET_LINE_API.Get_Budget_Name(:PROJ_NO,:BUDGET_LINE_NO)").
                                 setLabel("PROJECTCONTRACTITEMBUDGETLINENAME: Budget Line Name").
                                 setSize(30);
       mgr.getASPField("BUDGET_LINE_NO").setValidation("BUDGET_LINE_NAME");
       
       project_contract_item_blk.addField("MAT_NO").
                                 setHidden().
                                 setDynamicLOV("MAT_CODE"). 
                                 setLabel("PROJECTCONTRACTITEMMATNO: Mat No").
                                 setSize(30);  

       project_contract_item_blk.addField("MAT_NAME").
                                 setHidden().
                                 setReadOnly().
                                 setFunction("MAT_CODE_API.Get_Mat_Name(:ITEM0_PROJ_NO,:MAT_NO)").
                                 setLabel("PROJECTCONTRACTITEMMATNAME: Mat Name").
                                 setSize(30);
       project_contract_item_blk.addField("PRODUCT_MODEL").
                                  setHidden().
                                  setInsertable().
                                  setLabel("PROJECTCONTRACTITEMPRODUCTMODEL: Product Model").
                                  setSize(30);
         
       // 9    
       project_contract_item_blk.addField("ITEM_NOTE").
                                 setInsertable().
                                 setLabel("PROJECTCONTRACTITEMITEMNOTE: Item Note").
                                 setHeight(3);  
       // Hidden fields  
       project_contract_item_blk.addField("FULL_PATH").
                                 setHidden().
                                 setLabel("PROJECTCONTRACTITEMFULLPATH: Full Path").
                                 setSize(50); 
         
		project_contract_item_blk.setView("PROJECT_CONTRACT_ITEM");
		project_contract_item_blk.defineCommand("PROJECT_CONTRACT_ITEM_API", "New__,Modify__,Remove__");
		project_contract_item_blk.setMasterBlock(headblk);
		project_contract_item_set = project_contract_item_blk.getASPRowSet();
		project_contract_item_bar = mgr.newASPCommandBar(project_contract_item_blk);
		project_contract_item_bar.enableMultirowAction();
		project_contract_item_bar.defineCommand(project_contract_item_bar.OKFIND, "okFindITEM1");
		project_contract_item_bar.defineCommand(project_contract_item_bar.NEWROW, "newRowITEM1");
		project_contract_item_tbl = mgr.newASPTable(project_contract_item_blk);
		project_contract_item_tbl.setTitle("PROJECTCONTRACTITEMTBLHEAD: Project Contract Items");
		project_contract_item_tbl.enableRowSelect();
		project_contract_item_tbl.setWrap();
		project_contract_item_lay = project_contract_item_blk.getASPBlockLayout();
		project_contract_item_lay.setDefaultLayoutMode(project_contract_item_lay.MULTIROW_LAYOUT);
		project_contract_item_lay.setSimple("ITEM_UNIT_DESC");
		project_contract_item_lay.setSimple("ITEM_CURRENCY_DESC");
      project_contract_item_lay.setSimple("ITEM_UNIT_DESC");
      project_contract_item_lay.setSimple("ITEM_CURRENCY_DESC");
      project_contract_item_lay.setSimple("BUDGET_LINE_NAME"); 
      project_contract_item_lay.setSimple("MAT_NAME"); 
      project_contract_item_lay.setDataSpan("ITEM_NOTE", 5);  

	}

	public void adjust()
	{
		// fill function body
	   if(headlay.isMultirowLayout()){
	      headbar.disableCommand(headbar.NEWROW);
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
          if(headset!=null&&headset.countRows()>0){   
           String state = headset.getValue("STATE");
            headbar.disableCommand(headbar.NEWROW);  
          if( headlay.isSingleLayout() && ("Closed".equals(state) || "Checked".equals(state))){ 
             headbar.disableCommand(headbar.DELETE);
             headbar.disableCommand(headbar.EDITROW);   
              project_contract_item_bar.disableCommand( project_contract_item_bar.DELETE );
              project_contract_item_bar.disableCommand(project_contract_item_bar.NEWROW);
              project_contract_item_bar.disableCommand( project_contract_item_bar.EDITROW );
      }
      }  
      }
	}

	 public void validate()
	   {
       ASPManager mgr = getASPManager();
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
       mgr.endResponse();          
	   }
	// -----------------------------------------------------------------------------
	// ------------------------ Presentation functions ---------------------------
	// -----------------------------------------------------------------------------

	protected String getDescription() {
		return "PROJECTCONTRACTDESC: Project Contract";
	}

	protected String getTitle() {
		return getDescription();
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
		
		if ((headlay.isSingleLayout() || headlay.isCustomLayout()) && headset.countRows() > 0)
		{
			appendToHTML(project_contract_item_lay.show());
		}
	}
}