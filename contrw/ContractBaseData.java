package ifs.contrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ContractBaseData extends ASPPageProvider {
	
	private ASPBlock paymethodblk;
	private ASPRowSet paymethodset;
	private ASPCommandBar paymethodbar;
	private ASPTable paymethodtbl;
	private ASPBlockLayout paymethodlay;
	
	private ASPBlock paytypeblk;
	private ASPRowSet paytypeset;
	private ASPCommandBar paytypebar;
	private ASPTable paytypetbl;
	private ASPBlockLayout paytypelay;
	
	private ASPBlock contract_purch_type_blk;
	private ASPRowSet contract_purch_type_set;
	private ASPCommandBar contract_purch_type_bar;
	private ASPTable contract_purch_type_tbl;
	private ASPBlockLayout contract_purch_type_lay;
	
	private ASPBlock contract_class_blk;
	private ASPRowSet contract_class_set;
	private ASPCommandBar contract_class_bar;
	private ASPTable contract_class_tbl;
	private ASPBlockLayout contract_class_lay;
	
	private ASPBlock contract_pay_min_type_blk;
	private ASPRowSet contract_pay_min_type_set;
	private ASPCommandBar contract_pay_min_type_bar;
	private ASPTable contract_pay_min_type_tbl;
	private ASPBlockLayout contract_pay_min_type_lay;
	
	private ASPBlock contract_pay_support_files_blk;
	private ASPRowSet contract_pay_support_files_set;
	private ASPCommandBar contract_pay_support_files_bar;
	private ASPTable contract_pay_support_files_tbl;
	private ASPBlockLayout contract_pay_support_files_lay;
	
	 private ASPBlock contract_detain_class_blk;
    private ASPRowSet contract_detain_class_set;
    private ASPCommandBar contract_detain_class_bar;
    private ASPTable contract_detain_class_tbl;
    private ASPBlockLayout contract_detain_class_lay;
    
    private ASPBlock contract_contact_class_blk;
    private ASPRowSet contract_contact_class_set;
    private ASPCommandBar contract_contact_class_bar;
    private ASPTable contract_contact_class_tbl;
    private ASPBlockLayout contract_contact_class_lay;
	   
	private ASPTabContainer tabs;
	
	public ContractBaseData(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}
	
	public void run()
	{
		ASPManager mgr = getASPManager();
		if( mgr.commandBarActivated() )
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
			okFind();
		else if( !mgr.isEmpty(mgr.getQueryStringValue("ONPAGEOPEN")) )
			okFind();
      else 
         okFind();
		tabs .saveActiveTab();
		adjust();
	}
	
	public void okFind()
	{
		if(paymethodset      .countRows() == 0) okFindPayMethod      ();
		if(paytypeset        .countRows() == 0) okFindPayType        ();
		if(contract_purch_type_set.countRows() == 0) okFindPurchType ();
		if(contract_class_set.countRows() == 0) okFindClass ();
		if(contract_pay_min_type_set.countRows() == 0) okFindPayMinType ();
		if(contract_pay_support_files_set.countRows() == 0) okFindPaySuppFiles ();  
		if(contract_detain_class_set.countRows() == 0) okFindDetainClass(); 
		if(contract_contact_class_set.countRows() == 0) okFindContactClass(); 
	}
	  
	
	public void okFindPayMinType()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		
		mgr.createSearchURL(contract_pay_min_type_blk);
		q = trans.addQuery(contract_pay_min_type_blk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		
		q.setOrderByClause("TYPE_NO");
		
		mgr.querySubmit(trans, contract_pay_min_type_blk);
		if (contract_pay_min_type_set.countRows() == 0)
		{
			contract_pay_min_type_set.clear();
		}
	}
	
	public void newRowPayMinType()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;
		
		cmd = trans.addEmptyCommand("PAYMINTYPE", "CONTRACT_PAY_MIN_TYPE_API.New__", contract_pay_min_type_blk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("PAYMINTYPE/DATA");
		contract_pay_min_type_set.addRow(data);
	}
	
	public void okFindPaySuppFiles()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		
		mgr.createSearchURL(contract_pay_support_files_blk);
		q = trans.addQuery(contract_pay_support_files_blk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		
		q.setOrderByClause("FILE_NO");
		
		mgr.querySubmit(trans, contract_pay_support_files_blk);
		if (contract_pay_support_files_set.countRows() == 0)
		{
			contract_pay_support_files_set.clear();
		}
	}
	
	public void newRowPaySuppFiles()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;
		
		cmd = trans.addEmptyCommand("PAYSUPPFILES", "CONTRACT_PAY_SUPPORT_FILES_API.New__", contract_pay_support_files_blk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("PAYSUPPFILES/DATA");
		contract_pay_support_files_set.addRow(data);
		
	}
	   
	public void okFindPurchType()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		mgr.createSearchURL(contract_purch_type_blk);
		q = trans.addQuery(contract_purch_type_blk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		
		q.setOrderByClause("PURCH_TYPE_NO");
		
		mgr.querySubmit(trans, contract_purch_type_blk);
		if (contract_purch_type_set.countRows() == 0)
		{
			contract_purch_type_set.clear();
		}
	}
	
	public void countFindPurchType()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(contract_purch_type_blk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		contract_purch_type_lay.setCountValue(toInt(contract_purch_type_set.getValue("N")));
		contract_purch_type_set.clear();
	}
	
	public void newRowPurchType()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPBuffer data;
		ASPCommand cmd;

		cmd = trans.addEmptyCommand("PURCH_TYPE", "CONTRACT_PURCH_TYPE_API.New__", contract_purch_type_blk);
		cmd.setOption("ACTION", "PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("PURCH_TYPE/DATA");
		contract_purch_type_set.addRow(data);
	}
	
	public void okFindClass()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		
		mgr.createSearchURL(contract_class_blk);
		q = trans.addQuery(contract_class_blk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		
		q.setOrderByClause("CLASS_NO");
		
		mgr.querySubmit(trans, contract_class_blk);
		if (contract_class_set.countRows() == 0)
		{
			contract_class_set.clear();
		}
	}

	public void newRowClass()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;

		cmd = trans.addEmptyCommand("CLASS", "CONTRACT_CLASS_API.New__", contract_class_blk);
		cmd.setOption("ACTION", "PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("CLASS/DATA");
		contract_class_set.addRow(data);
	}

	   public void okFindPayMethod()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery q;

	      mgr.createSearchURL(paymethodblk);
	      q = trans.addQuery(paymethodblk);
	      q.includeMeta("ALL");
	      if(mgr.dataTransfered())
	         q.addOrCondition(mgr.getTransferedData());
	      mgr.querySubmit(trans,paymethodblk);
	      if (  paymethodset.countRows() == 0 )
	      {
	         paymethodset.clear();
	      }
	   }



	   public void countFindPayMethod()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery q;

	      q = trans.addQuery(paymethodblk);
	      q.setSelectList("to_char(count(*)) N");
	      mgr.submit(trans);
	      paymethodlay.setCountValue(toInt(paymethodset.getValue("N")));
	      paymethodset.clear();
	   }



	   public void newRowPayMethod()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPBuffer data;
	      ASPCommand cmd;

	      cmd = trans.addEmptyCommand("HEAD","PAY_METHOD_API.New__",paymethodblk);
	      cmd.setOption("ACTION","PREPARE");
	      trans = mgr.perform(trans);
	      data = trans.getBuffer("HEAD/DATA");
	      paymethodset.addRow(data);
	   }
	
	   public void okFindPayType()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery q;

	      mgr.createSearchURL(paytypeblk);
	      q = trans.addQuery(paytypeblk);
	      q.includeMeta("ALL");
	      if(mgr.dataTransfered())
	         q.addOrCondition(mgr.getTransferedData());
	      mgr.querySubmit(trans,paytypeblk);
	      if (  paytypeset.countRows() == 0 )
	      {
	         paytypeset.clear();
	      }
	   }
	   
	   public void countFindPayType()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery q;

	      q = trans.addQuery(paytypeblk);
	      q.setSelectList("to_char(count(*)) N");
	      mgr.submit(trans);
	      paytypelay.setCountValue(toInt(paytypeset.getValue("N")));
	      paytypeset.clear();
	   }
	   
	   public void newRowPayType()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPBuffer data;
	      ASPCommand cmd;

	      cmd = trans.addEmptyCommand("PAYTYPE","CONTRACT_PAYMENT_TYPE_API.New__",paytypeblk);
	      cmd.setOption("ACTION","PREPARE");
	      trans = mgr.perform(trans);  
	      data = trans.getBuffer("PAYTYPE/DATA");
	      paytypeset.addRow(data);
	   }
	   
	   
	   public void okFindDetainClass()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery q;

	      mgr.createSearchURL(contract_detain_class_blk);
	      q = trans.addQuery(contract_detain_class_blk);
	      q.includeMeta("ALL");
	      if(mgr.dataTransfered())
	         q.addOrCondition(mgr.getTransferedData());
	      mgr.querySubmit(trans,contract_detain_class_blk);
	      if (  contract_detain_class_set.countRows() == 0 )
	      {
	         mgr.showAlert("CONTRACTDETAINCLASSNODATA: No data found.");
	         contract_detain_class_set.clear();
	      }
	   }



	   public void countFindDetainClass()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery q;

	      q = trans.addQuery(contract_detain_class_blk);
	      q.setSelectList("to_char(count(*)) N");
	      mgr.submit(trans);
	      contract_detain_class_lay.setCountValue(toInt(contract_detain_class_set.getValue("N")));
	      contract_detain_class_set.clear();
	   }
	   
	   public void newRowDetainClass()
	   {
	      ASPManager mgr = getASPManager();
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPBuffer data;
	      ASPCommand cmd;

	      cmd = trans.addEmptyCommand("DETAINCLASS","CONTRACT_DETAIN_CLASS_API.New__",contract_detain_class_blk);
	      cmd.setOption("ACTION","PREPARE");  
	      trans = mgr.perform(trans);
	      data = trans.getBuffer("DETAINCLASS/DATA");
	      contract_detain_class_set.addRow(data);
	   }   
	   
	     public void okFindContactClass()
	      {
	        
	         ASPManager mgr = getASPManager();
	         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	         ASPQuery q;
	         trans.clear();  
	         mgr.createSearchURL(contract_contact_class_blk);
	         q = trans.addQuery(contract_contact_class_blk);
	         q.includeMeta("ALL");   
	         if(mgr.dataTransfered())
	            q.addOrCondition(mgr.getTransferedData());
	         mgr.querySubmit(trans,contract_contact_class_blk);
	         if (  contract_contact_class_set.countRows() == 0 )
	         {
	            mgr.showAlert("CONTRACTCONTACTCLASSNODATA: No data found.");
	            contract_contact_class_set.clear();
	         }
	      }



	      public void countFindContactClass()
	      {
	         ASPManager mgr = getASPManager();
	         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	         ASPQuery q;

	         q = trans.addQuery(contract_contact_class_blk);
	         q.setSelectList("to_char(count(*)) N");
	         mgr.submit(trans);
	         contract_contact_class_lay.setCountValue(toInt(contract_contact_class_set.getValue("N")));
	         contract_contact_class_set.clear();
	      }
	      
	      public void newRowContactClass()
	      {
	         ASPManager mgr = getASPManager();
	         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	         ASPBuffer data;
	         ASPCommand cmd;

	         cmd = trans.addEmptyCommand("CONTACTCLASS","CONTRACT_CONTRACT_TYPE_API.New__",contract_contact_class_blk);
	         cmd.setOption("ACTION","PREPARE");    
	         trans = mgr.perform(trans);
	         data = trans.getBuffer("CONTACTCLASS/DATA");    
	         contract_contact_class_set.addRow(data);
	      }  
	   
	public void  preDefine()
	{
		ASPManager mgr = getASPManager();
		
		paymethodblk = mgr.newASPBlock("PAYMETHOD");
		paymethodblk.addField("PAYMETHOD_OBJID").
		             setDbName("OBJID").
		             setHidden();
		paymethodblk.addField("PAYMETHOD_OBJVERSION").
		             setDbName("OBJVERSION").
		             setHidden();
		paymethodblk.addField("PAYMETHOD_METHOD_ID").
		             setDbName("METHOD_ID").
		             setInsertable().  
		             setLabel("PAYMETHODMETHODID: Method Id").
		             setSize(20);  
		paymethodblk.addField("PAYMETHOD_METHOD_NAME").
		             setDbName("METHOD_NAME").
		             setMandatory().
		             setInsertable().
		             setLabel("PAYMETHODMETHODNAME: Method Name").
		             setSize(20);
		paymethodblk.addField("PAYMEYHOD_REMARK").
		             setDbName("REMARK").
		             setInsertable().
		             setLabel("PAYMETHODREMARK: Remark").
		             setSize(50).    
		             setHeight(4);
		paymethodblk.setView("PAY_METHOD");
		paymethodblk.defineCommand("PAY_METHOD_API","New__,Modify__,Remove__");
		paymethodset = paymethodblk.getASPRowSet();
		paymethodbar = mgr.newASPCommandBar(paymethodblk);
		paymethodbar.defineCommand(paymethodbar.OKFIND   , "okFindPayMethod"    );
		paymethodbar.defineCommand(paymethodbar.NEWROW   , "newRowPayMethod"    );
		paymethodbar.defineCommand(paymethodbar.COUNTFIND, "countFindPayMethod" );
		paymethodtbl = mgr.newASPTable(paymethodblk);
		paymethodtbl.setTitle("PAYMETHODTBLHEAD: Pay Methods");
		paymethodtbl.enableRowSelect();
		paymethodtbl.setWrap();
		paymethodlay = paymethodblk.getASPBlockLayout();
		paymethodlay.setDefaultLayoutMode(paymethodlay.MULTIROW_LAYOUT);
		paymethodlay.setDataSpan("PAYMETHOD_METHOD_ID"  , 10);
		paymethodlay.setDataSpan("PAYMETHOD_METHOD_NAME", 10);
		paymethodlay.setDataSpan("PAYMEYHOD_REMARK"     , 10);
		
		paytypeblk = mgr.newASPBlock("PAYTYPE");
		paytypeblk.addField("PAYTYPE_OBJID").
				     setDbName("OBJID").
				     setHidden();
		paytypeblk.addField("PAYTYPE_OBJVERSION").
				     setDbName("OBJVERSION").
				     setHidden();
		paytypeblk.addField("PAYTYPE_TYPE_NO").
				     setDbName("TYPE_NO").
				     setMandatory().
				     setInsertable().
				     setLabel("PAYTYPESEQNO: Seq No").
				     setSize(20);
		paytypeblk.addField("PAYTYPE_TYPE_DESC").
				     setDbName("TYPE_DESC").
				     setMandatory().  
				     setInsertable().
				     setLabel("PAYTYPETYPENAME: Type Name").
				     setSize(20);
		paytypeblk.addField("PAYTYPE_NOTE").
				     setDbName("NOTE").
				     setInsertable().
				     setLabel("PAYTYPEREMARK: Remark").
				     setSize(80). 
				     setHeight(4); 
		paytypeblk.setView("CONTRACT_PAYMENT_TYPE");
		paytypeblk.defineCommand("CONTRACT_PAYMENT_TYPE_API","New__,Modify__,Remove__");
		paytypeset = paytypeblk.getASPRowSet();  
		paytypebar = mgr.newASPCommandBar(paytypeblk);
		paytypebar.defineCommand(paytypebar.OKFIND   , "okFindPayType"    );
		paytypebar.defineCommand(paytypebar.NEWROW   , "newRowPayType"    );
		paytypebar.defineCommand(paytypebar.COUNTFIND, "countFindPayType" );
		paytypetbl = mgr.newASPTable(paytypeblk);
		paytypetbl.setTitle("PAYTYPETBLHEAD: Pay Types");
		paytypetbl.enableRowSelect();
		paytypetbl.setWrap();
		paytypelay = paytypeblk.getASPBlockLayout();
		paytypelay.setDefaultLayoutMode(paytypelay.MULTIROW_LAYOUT);
		paytypelay.setDataSpan("PAYTYPE_NOTE", 10);
		
		//  
		// Contract Purchase Type
		//
		
		contract_purch_type_blk = mgr.newASPBlock("PURCH_TYPE");
		contract_purch_type_blk.addField("PURCH_TYPE_OBJID").
				                  setDbName("OBJID").
				                  setHidden();
		contract_purch_type_blk.addField("PURCH_TYPE_OBJVERSION").
				                  setDbName("OBJVERSION").
				                  setHidden();
		contract_purch_type_blk.addField("PURCH_TYPE_NO").
				                  setMandatory().
				                  setInsertable().
				                  setReadOnly().
				                  setLabel("CONTRACTPURCHTYPEPURCHTYPENO: Purch Type No").
				                  setSize(20);
		contract_purch_type_blk.addField("DESCRIPTION").
				                  setInsertable().
				                  setLabel("CONTRACTPURCHTYPEDESCRIPTION: Description").
				                  setSize(50);
		contract_purch_type_blk.addField("NOTE").
				                  setInsertable().
				                  setLabel("CONTRACTPURCHTYPENOTE: Note").
				                  setSize(50).
				                  setHeight(5);
		contract_purch_type_blk.setView("CONTRACT_PURCH_TYPE");
		contract_purch_type_blk.defineCommand("CONTRACT_PURCH_TYPE_API", "New__,Modify__,Remove__");
		contract_purch_type_set = contract_purch_type_blk.getASPRowSet();
		contract_purch_type_bar = mgr.newASPCommandBar(contract_purch_type_blk);
		
		contract_purch_type_bar.defineCommand(contract_purch_type_bar.OKFIND   , "okFindPurchType"    );
		contract_purch_type_bar.defineCommand(contract_purch_type_bar.NEWROW   , "newRowPurchType"    );
		contract_purch_type_bar.defineCommand(contract_purch_type_bar.COUNTFIND, "countFindPurchType" );
		
		contract_purch_type_tbl = mgr.newASPTable(contract_purch_type_blk);
		contract_purch_type_tbl.setTitle("CONTRACTPURCHTYPETBLHEAD: Contract Purch Types");
		contract_purch_type_tbl.enableRowSelect();
		contract_purch_type_tbl.setWrap();
		contract_purch_type_lay = contract_purch_type_blk.getASPBlockLayout();
		contract_purch_type_lay.setDefaultLayoutMode(contract_purch_type_lay.MULTIROW_LAYOUT);
		
		//
		// Contract Class
		//
		
		contract_class_blk = mgr.newASPBlock("CLASS");
		contract_class_blk.addField("CLASS_OBJID").
				             setHidden().
				             setDbName("OBJID");
		contract_class_blk.addField("CLASS_OBJVERSION").
				             setHidden().
				             setDbName("OBJVERSION");
		contract_class_blk.addField("CLASS_NO").
				             setMandatory().
				             setInsertable().
				             setReadOnly().
				             setUpperCase().
				             setLabel("CONTRACTCLASSCLASSNO: Class No").
				             setSize(10);
		contract_class_blk.addField("CLASS_NAME").
				             setInsertable().
				             setLabel("CONTRACTCLASSCLASSNAME: Class Name").
				             setSize(20);
		contract_class_blk.addField("CLASS_NOTE").
				             setDbName("NOTE").
				             setInsertable().
				             setLabel("CONTRACTCLASSNOTE: Note").
				             setSize(50).
				             setHeight(3);
		contract_class_blk.setView("CONTRACT_CLASS");
		contract_class_blk.defineCommand("CONTRACT_CLASS_API", "New__,Modify__,Remove__");
		contract_class_set = contract_class_blk.getASPRowSet();
		contract_class_bar = mgr.newASPCommandBar(contract_class_blk);
		contract_class_bar.defineCommand(contract_class_bar.OKFIND, "okFindClass");
		contract_class_bar.defineCommand(contract_class_bar.NEWROW, "newRowClass");
		contract_class_tbl = mgr.newASPTable(contract_class_blk);
		contract_class_tbl.setTitle("CONTRACTCLASSITEMHEAD1: ContractClass");
		contract_class_tbl.enableRowSelect();
		contract_class_tbl.setWrap();
		contract_class_lay = contract_class_blk.getASPBlockLayout();
		contract_class_lay.setDefaultLayoutMode(contract_class_lay.MULTIROW_LAYOUT);
		
		//
		// Payment Min Type
		//
		
		contract_pay_min_type_blk = mgr.newASPBlock("PAYMINTYPE");
		contract_pay_min_type_blk.addField("PAYMINTYPE_OBJID").
		                          setHidden().
		                          setDbName("OBJID");
		contract_pay_min_type_blk.addField("PAYMINTYPE_OBJVERSION").
		                          setHidden().
		                          setDbName("OBJVERSION");
		contract_pay_min_type_blk.addField("PAYMINTYPE_TYPE_NO").
		                          setDbName("TYPE_NO").
		                          setMandatory().
		                          setInsertable().
		                          setLabel("CONTRACTPAYMINTYPETYPENO: Type No").
		                          setSize(20);
		contract_pay_min_type_blk.addField("PAYMINTYPE_TYPE_DESC").
		                          setDbName("TYPE_DESC").
		                          setInsertable().
		                          setLabel("CONTRACTPAYMINTYPETYPEDESC: Type Desc").
		                          setSize(30);
		contract_pay_min_type_blk.addField("PAYMINTYPE_NOTE").
		                          setDbName("NOTE").
		                          setInsertable().
		                          setLabel("CONTRACTPAYMINTYPENOTE: Note").
		                          setSize(50);
		contract_pay_min_type_blk.setView("CONTRACT_PAY_MIN_TYPE");
		contract_pay_min_type_blk.defineCommand("CONTRACT_PAY_MIN_TYPE_API","New__,Modify__,Remove__");
		contract_pay_min_type_set = contract_pay_min_type_blk.getASPRowSet();
		contract_pay_min_type_bar = mgr.newASPCommandBar(contract_pay_min_type_blk);
		contract_pay_min_type_bar.defineCommand(contract_pay_min_type_bar.OKFIND, "okFindPayMinType");
		contract_pay_min_type_bar.defineCommand(contract_pay_min_type_bar.NEWROW, "newRowPayMinType");
		contract_pay_min_type_tbl = mgr.newASPTable(contract_pay_min_type_blk);
		contract_pay_min_type_tbl.setTitle("CONTRACTPAYMINTYPEITEMHEAD1: ContractPayMinType");
		contract_pay_min_type_tbl.enableRowSelect();
		contract_pay_min_type_tbl.setWrap();
		contract_pay_min_type_lay = contract_pay_min_type_blk.getASPBlockLayout();
		contract_pay_min_type_lay.setDefaultLayoutMode(contract_pay_min_type_lay.MULTIROW_LAYOUT);
		
		//
		// Payment Support Files
		//
		
		contract_pay_support_files_blk = mgr.newASPBlock("PAYSUPPFILES");
		contract_pay_support_files_blk.addField("PAYSUPPFILES_OBJID").
		                               setHidden().
		                               setDbName("OBJID");
		contract_pay_support_files_blk.addField("PAYSUPPFILES_OBJVERSION").
		                               setHidden().
		                               setDbName("OBJVERSION");
		contract_pay_support_files_blk.addField("FILE_NO").
		                               setMandatory().
		                               setInsertable().
		                               setLabel("CONTRACTPAYSUPPORTFILESFILENO: File No").
		                               setSize(20);
		contract_pay_support_files_blk.addField("FILE_DESC").
		                               setInsertable().
		                               setLabel("CONTRACTPAYSUPPORTFILESFILEDESC: File Desc").
		                               setSize(30);
		contract_pay_support_files_blk.addField("PAYSUPPFILES_NOTE").
		                               setDbName("NOTE").
		                               setInsertable().
		                               setLabel("CONTRACTPAYSUPPORTFILESITEM1NOTE: Note").
		                               setSize(50);
		contract_pay_support_files_blk.setView("CONTRACT_PAY_SUPPORT_FILES");
		contract_pay_support_files_blk.defineCommand("CONTRACT_PAY_SUPPORT_FILES_API","New__,Modify__,Remove__");
		contract_pay_support_files_set = contract_pay_support_files_blk.getASPRowSet();
		contract_pay_support_files_bar = mgr.newASPCommandBar(contract_pay_support_files_blk);
		contract_pay_support_files_bar.defineCommand(contract_pay_support_files_bar.OKFIND, "okFindPaySuppFiles");
		contract_pay_support_files_bar.defineCommand(contract_pay_support_files_bar.NEWROW, "newRowPaySuppFiles");
		contract_pay_support_files_tbl = mgr.newASPTable(contract_pay_support_files_blk);
		contract_pay_support_files_tbl.setTitle("CONTRACTPAYSUPPORTFILESITEMHEAD2: ContractPaySupportFiles");
		contract_pay_support_files_tbl.enableRowSelect();
		contract_pay_support_files_tbl.setWrap();
		contract_pay_support_files_lay = contract_pay_support_files_blk.getASPBlockLayout();
		contract_pay_support_files_lay.setDefaultLayoutMode(contract_pay_support_files_lay.MULTIROW_LAYOUT);
		
		
		contract_detain_class_blk = mgr.newASPBlock("DETAINCLASS");
      contract_detain_class_blk.addField("DETAINCLASS_OBJID").
                                setDbName("OBJID").
                                setHidden();
      contract_detain_class_blk.addField("DETAINCLASS_OBJVERSION").
                                setDbName("OBJVERSION").
                                setHidden();
      contract_detain_class_blk.addField("DETAIN_NO").
                                setMandatory().
                                setInsertable().
                                setLabel("CONTRACTDETAINCLASSDETAINNO: Detain No").
                                setSize(30);
      contract_detain_class_blk.addField("DETAIN_NAME").
                                setInsertable().
                                setLabel("CONTRACTDETAINCLASSDETAINNAME: Detain Name").
                                setSize(30);
      contract_detain_class_blk.addField("DETAIN_FLAG").
                                setInsertable().
                                setCheckBox("FALSE,TRUE").  
                                setLabel("CONTRACTDETAINCLASSDETAINFLAG: Detain Flag").
                                setSize(30);
      contract_detain_class_blk.addField("SCALE").
                                setInsertable().
                                setLabel("CONTRACTDETAINCLASSSCALE: Scale").
                                setSize(30);
      contract_detain_class_blk.addField("PAGE_NAME").
                                setInsertable().
                                setLabel("CONTRACTDETAINCLASSPAGENAME: Page Name").
                                setSize(30);
      contract_detain_class_blk.addField("LU_NAME").
                                setInsertable().
                                setLabel("CONTRACTDETAINCLASSLUNAME: Lu Name").
                                setSize(30);
      contract_detain_class_blk.addField("VIEW_NAME").
                                setInsertable().
                                setLabel("CONTRACTDETAINCLASSVIEWNAME: View Name").
                                setSize(30);
      contract_detain_class_blk.setView("CONTRACT_DETAIN_CLASS");
      contract_detain_class_blk.defineCommand("CONTRACT_DETAIN_CLASS_API","New__,Modify__,Remove__");
      contract_detain_class_set = contract_detain_class_blk.getASPRowSet();
      contract_detain_class_bar = mgr.newASPCommandBar(contract_detain_class_blk);
      contract_detain_class_bar.defineCommand(contract_detain_class_bar.OKFIND, "okFindDetainClass");
      contract_detain_class_bar.defineCommand(contract_detain_class_bar.NEWROW, "newRowDetainClass");
      contract_detain_class_bar.defineCommand(contract_detain_class_bar.COUNTFIND, "countFindDetainClass");
      contract_detain_class_tbl = mgr.newASPTable(contract_detain_class_blk);
      contract_detain_class_tbl.setTitle("CONTRACTDETAINCLASSTBLcontract_detain_class_: Contract Detain Classs");
      contract_detain_class_tbl.enableRowSelect();
      contract_detain_class_tbl.setWrap();
      contract_detain_class_lay = contract_detain_class_blk.getASPBlockLayout();
      contract_detain_class_lay.setDefaultLayoutMode(contract_detain_class_lay.MULTIROW_LAYOUT);
      
      contract_contact_class_blk = mgr.newASPBlock("CONTACTCLASS");
      contract_contact_class_blk.addField("CONTACTCLASS_OBJID").
                                setDbName("OBJID").
                                setHidden();
      contract_contact_class_blk.addField("CONTACTCLASS_OBJVERSION").
                                setDbName("OBJVERSION").
                                setHidden();
      contract_contact_class_blk.addField("CONTACT_CLASS_NO").
                                setMandatory().
                                setInsertable().
                                setLabel("CONTRACTCONTACTCLASSCONTACTCLASSNO: Contact Class No").
                                setSize(30);
      contract_contact_class_blk.addField("CONTACT_CLASS_NAME").
                                setInsertable().
                                setMandatory().
                                setLabel("CONTRACTCONTACTCLASSCONTACTCLASSNAME: Contact Class Name").
                                setSize(30);
      contract_contact_class_blk.setView("CONTRACT_CONTRACT_TYPE");
      contract_contact_class_blk.defineCommand("CONTRACT_CONTRACT_TYPE_API","New__,Modify__,Remove__");
      contract_contact_class_set = contract_contact_class_blk.getASPRowSet();
      contract_contact_class_bar = mgr.newASPCommandBar(contract_contact_class_blk);
      contract_contact_class_bar.defineCommand(contract_contact_class_bar.OKFIND, "okFindContactClass");
      contract_contact_class_bar.defineCommand(contract_contact_class_bar.NEWROW, "newRowContactClass");
      contract_contact_class_bar.defineCommand(contract_contact_class_bar.COUNTFIND, "countFindContactClass");
      contract_contact_class_tbl = mgr.newASPTable(contract_contact_class_blk);
      contract_contact_class_tbl.setTitle("CONTRACTCONTACTCLASSTBL: Contract Contact Classs");
      contract_contact_class_tbl.enableRowSelect();
      contract_contact_class_tbl.setWrap();  
      contract_contact_class_lay = contract_contact_class_blk.getASPBlockLayout();
      contract_contact_class_lay.setDefaultLayoutMode(contract_contact_class_lay.MULTIROW_LAYOUT);
      
		tabs  = mgr.newASPTabContainer();
		tabs .addTab(mgr.translate("CONTRACTBASEDATAPAYMETHODTAB: Pay Method"                 ), "javascript:commandSet('CLASS.activateITEM1', '')"  );
		tabs .addTab(mgr.translate("CONTRACTBASEDATAPAYTYPETAB: Pay Type"                     ), "javascript:commandSet('CLASS.activateITEM2', '')" );
		tabs .addTab(mgr.translate("CONTRACTBASEDATAPURCHTYPETAB: Purch Type"                 ), "javascript:commandSet('CLASS.activateITEM3', '')" );
		tabs .addTab(mgr.translate("CONTRACTBASEDATACLASSTAB: Contract Class"                 ), "javascript:commandSet('CLASS.activateITEM4', '')" );
		tabs .addTab(mgr.translate("CONTRACTBASEDATAMINTYPETAB: Min Type"              		  ), "javascript:commandSet('CLASS.activateITEM5', '')" );
		tabs .addTab(mgr.translate("CONTRACTBASEDATASUPPFILESTAB: Support Files"           	  ), "javascript:commandSet('CLASS.activateITEM6', '')" );
      tabs .addTab(mgr.translate("CONTRACTBASEDATADETAINCLASSSTAB: Detain Classs"           ), "javascript:commandSet('CLASS.activateITEM7', '')" );
      tabs .addTab(mgr.translate("CONTRACTBASEDATACONTACTCLASSSTAB: Contact Classs"           ), "javascript:commandSet('CLASS.activateITEM8', '')" );
      contract_class_bar.addCustomCommand("activateITEM1" , "");
		contract_class_bar.addCustomCommand("activateITEM2" , "");
		contract_class_bar.addCustomCommand("activateITEM3" , "");
		contract_class_bar.addCustomCommand("activateITEM4" , "");
		contract_class_bar.addCustomCommand("activateITEM5" , "");
		contract_class_bar.addCustomCommand("activateITEM6" , "");
		contract_class_bar.addCustomCommand("activateITEM7" , "");  
		contract_class_bar.addCustomCommand("activateITEM8" , "");  
	} 
	
	public void  adjust()
	{    
		// fill function body
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
	}
	
	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------
	
	protected String getDescription()
	{
		return "CONTRACTBASEDATADESC: Contract Base Data";
	}
	
	
	protected String getTitle()
	{
		return "CONTRACTBASEDATATITLE: Contract Base Data";
	}
	
	public void activateITEM1()
	{
		tabs.setActiveTab(1);
	}
	
	public void activateITEM2()
	{
		tabs.setActiveTab(2);
	}
	
	public void activateITEM3()
	{
		tabs.setActiveTab(3);
	}
	
	public void activateITEM4()
	{
		tabs.setActiveTab(4);
	}
	
	public void activateITEM5()
	{
		tabs.setActiveTab(5);
	}
	
	public void activateITEM6()
	{
		tabs.setActiveTab(6);
	}
	
	  public void activateITEM7()
	  {
	      tabs.setActiveTab(7); 
	  }
	  
     public void activateITEM8()
     {
         tabs.setActiveTab(8); 
     }  
	
	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		appendToHTML(tabs.showTabsInit());
		 
		if (tabs.getActiveTab() == 1 )
	       appendToHTML(paymethodlay.show());
	   else if (tabs.getActiveTab() == 2 )  
	       appendToHTML(paytypelay.show());    
	   else if (tabs.getActiveTab() == 3 )      
	       appendToHTML(contract_purch_type_lay.show());  
	   else if (tabs.getActiveTab() == 4 )
          appendToHTML(contract_class_lay.show());  
      else if (tabs.getActiveTab() == 5 )  
          appendToHTML(contract_pay_min_type_lay.show());  
      else if (tabs.getActiveTab() == 6 )
          appendToHTML(contract_pay_support_files_lay.show());
      else if (tabs.getActiveTab() == 7 )  
          appendToHTML(contract_detain_class_lay.show());
      else if (tabs.getActiveTab() == 8 )  
         appendToHTML(contract_contact_class_lay.show());
	    appendToHTML(tabs.showTabsFinish());            
	}  
}