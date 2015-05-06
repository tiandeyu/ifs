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
*  File        : ServiceContractLineSerchDlg.java 
*  Created     : CHANLK	030108
*  Modified    :  
*  CHANLK	030108	Created.
*  HARPLK   090709   Bug 84436, Modified preDefine().
*/
package ifs.pcmw;

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
//Bug 84436, Start
import ifs.fnd.asp.*;
//Bug 84436, End

public class ServiceContractLineSearchDlg extends ASPPageProvider {

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ServiceContractLineSerchDlg");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;

	private boolean refreshMain;
	private boolean bUseAsLov;
	
	//===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private ASPBuffer temp;
   private ASPBuffer tempRow;
   private ASPCommand cmd;
   private String sCustNo;  
   private String sWoNo;  
   private String sContract;  
   private String sMchCode;  
   private String sWorkType; 
   
   private String sContractId;
   private String sLineNo;
   //Bug 84436, Start
    private ASPField f;
   //Bug 84436, End
   //===============================================================
   // Construction 
   //===============================================================
   public ServiceContractLineSearchDlg(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}
   
   public void run() {
      ASPManager mgr = getASPManager();
   	trans = mgr.newASPTransactionBuffer();
   	
      ctx = mgr.getASPContext();
      sCustNo = "";  
      sWoNo = "";  
      sContract = "";  
      sMchCode = "";  
      sWorkType = ""; 

      sCustNo = ctx.readValue("CTXCUSTOMER_NO",sCustNo);
      sWoNo = ctx.readValue("CTXWONO",sWoNo);
      sContract = ctx.readValue("CTXCONTRACT",sContract);
      sMchCode = ctx.readValue("CTXMCH_CODE",sMchCode);
      sWorkType = ctx.readValue("CTXWORK_TYPE",sWorkType);
  	
   	if (mgr.dataTransfered())
      {
   		temp = mgr.getTransferedData();
   		tempRow = temp.getBufferAt(0); 
   		sCustNo = tempRow.getValue("CUSTOMER_NO");
   		sWoNo = tempRow.getValue("WO_NO");
   		sContract = tempRow.getValue("CONTRACT");
   		sMchCode = tempRow.getValue("MCH_CODE");
   		sWorkType = tempRow.getValue("WORK_TYPE_ID");
   		okFind();
      }
   	else if (mgr.commandBarActivated())
      {
          eval(mgr.commandBarFunction());
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_NO")))
      {
      	bUseAsLov = true;
      	sCustNo = mgr.readValue("CUSTOMER_NO");
   		sWoNo = mgr.readValue("WO_NO");
   		sContract = mgr.readValue("CONTRACT");
   		sMchCode = mgr.readValue("MCH_CODE");
   		sWorkType = mgr.readValue("WORK_TYPE_ID");
   		okFind();
      }
   	else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
   		okFind();
      }    
   	adjust();

   	ctx.writeValue("CTXCUSTOMER_NO",sCustNo);
      ctx.writeValue("CTXWONO",sWoNo);
      ctx.writeValue("CTXCONTRACT",sContract);
      ctx.writeValue("CTXMCH_CODE",sMchCode);
      ctx.writeValue("CTXWORK_TYPE",sWorkType);

   }
   
   public void adjust(){
		if (itemset0.countRows()==0)
			itembar0.removeCustomCommand("selectContract");

   }
 //-----------------------------------------------------------------------------
 //------------------------  BUTTON FUNCTIONS  ---------------------------------
 //-----------------------------------------------------------------------------

	public void selectContract() {

		if (itemlay0.isMultirowLayout())
			itemset0.goTo(itemset0.getRowSelected());
		else
			itemset0.selectRow();

		itemset0.goTo(itemset0.getCurrentRowNo());
		refreshMain = true;
		bUseAsLov = Boolean.parseBoolean(headset.getRow().getValue("USEASLOV"));
		sContractId = itemset0.getRow().getValue("CONTRACT_ID");
		sLineNo = itemset0.getRow().getValue("LINE_NO");
 	}

   	//-----------------------------------------------------------------------------
   	//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
   	//-----------------------------------------------------------------------------

   public void  okFind()
   {
       ASPManager mgr = getASPManager();

       trans.clear();

       q = trans.addEmptyQuery(headblk);
       q.includeMeta("ALL");
       mgr.submit(trans);

       temp = headset.getRow();
       
       temp.setValue("CUSTOMER_NO",sCustNo);
       temp.setValue("WO_NO",sWoNo);
       temp.setValue("CONTRACT",sContract);
       temp.setValue("MCH_CODE",sMchCode);
       temp.setValue("WORK_TYPE_ID",sWorkType);
       temp.setValue("USEASLOV",Boolean.toString(bUseAsLov));
       
       trans.clear();

       cmd = trans.addCustomCommand("GETPARTDET", "VIM_SERIAL_API.Fetch_Object_Details");
       cmd.addParameter("PART_NO");
       cmd.addParameter("SERIAL_NO");
       cmd.addParameter("MCH_CODE",sMchCode);

       cmd = trans.addCustomFunction("GETPARTDESC", "PART_CATALOG_API.Get_Description", "PART_DESCRIPTION");
       cmd.addReference("PART_NO","GETPARTDET/DATA");
       
       cmd = trans.addCustomFunction("GETPARTREV", "VIM_SERIAL_API.Get_Part_Rev", "PART_REVISION" );
       cmd.addReference("PART_NO","GETPARTDET/DATA");
       cmd.addReference("SERIAL_NO","GETPARTDET/DATA");

       cmd = trans.addCustomFunction("GETCUSTNAME", "CUSTOMER_INFO_API.Get_Name", "CUSTOMERDESCRIPTION");
       cmd.addParameter("CUSTOMER_NO",sCustNo);

       cmd = trans.addCustomFunction("GETWRKTYPEDESC", "WORK_TYPE_API.Get_Description", "WORKTYPEDESCRIPTION" );
       cmd.addParameter("WORK_TYPE_ID",sWorkType);

       trans = mgr.perform(trans);
       
       temp.setValue("PART_NO",trans.getValue("GETPARTDET/DATA/PART_NO"));
       temp.setValue("SERIAL_NO",trans.getValue("GETPARTDET/DATA/SERIAL_NO"));
       temp.setValue("PART_DESCRIPTION",trans.getValue("GETPARTDESC/DATA/PART_DESCRIPTION"));
       temp.setValue("PART_REVISION",trans.getValue("GETPARTREV/DATA/PART_REVISION"));
       temp.setValue("CUSTOMERDESCRIPTION",trans.getValue("GETCUSTNAME/DATA/CUSTOMERDESCRIPTION"));
       temp.setValue("WORKTYPEDESCRIPTION",trans.getValue("GETWRKTYPEDESC/DATA/WORKTYPEDESCRIPTION"));

       headset.setRow(temp);
       okFindITEM0();
   }
   
   public void okFindITEM0(){
      ASPManager mgr = getASPManager();
      int currrow = headset.getCurrentRowNo();
      if (headset.countRows()>0)
      {            
      	 
          trans.clear();
          q = trans.addEmptyQuery(itemblk0);
          q.addParameter("TOP_PART_NO",headset.getRow().getValue("PART_NO"));
          q.addParameter("SERIAL_NO",headset.getRow().getValue("SERIAL_NO"));
          q.addParameter("TOP_PART_NO",headset.getRow().getValue("PART_NO"));
          q.addParameter("SERIAL_NO",headset.getRow().getValue("SERIAL_NO"));
          q.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
          q.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
          q.addParameter("WORK_TYPE_ID",headset.getRow().getValue("WORK_TYPE_ID"));
          q.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
          q.includeMeta("ALL");

          mgr.querySubmit(trans, itemblk0);
          
      }
      headset.goTo(currrow);
   	
   }

   
   public void  preDefine()
   {
   	ASPManager mgr = getASPManager();

   	headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden().
      setFunction("''");
      
      headblk.addField("OBJVERSION").
      setHidden().
      setFunction("''");
      
      headblk.addField("CUSTOMER_NO").
      setSize(12).
      setLabel("PCMWSRVCONLINESERCHDLGCUSTOMERNO: Customer No").
      setUpperCase(). 
      setFunction("''").
      setMaxLength(20);
      
      headblk.addField("CUSTOMERDESCRIPTION").
      setSize(30).
      setLabel("PCMWSRVCONLINESERCHDLGCUSTOMERDESC: Customer Name").
      setFunction("''").
      setReadOnly().
      setDefaultNotVisible();
      
      headblk.addField("WO_NO","Number","#").
      setSize(13).
      setLabel("PCMWSRVCONLINESERCHDLGWO_NO: WO No").
      setFunction("''").
      setReadOnly();
      
      headblk.addField("CONTRACT").
      setSize(5).
      setLabel("PCMWSRVCONLINESERCHDLGWOCONTRACT: WO Site").
      setUpperCase().
      setReadOnly().
      setFunction("''").
      setMaxLength(5);
      
      headblk.addField("MCH_CODE").
      setHidden().
      setFunction("''");
      
      headblk.addField("PART_NO").
      setSize(14).
      setLabel("PCMWSRVCONLINESERCHDLGPARTNO: Part No").
      setUpperCase().
      setReadOnly().
      setFunction("''");
      
      headblk.addField("PART_DESCRIPTION").
      setSize(28).
      setLabel("PCMWSRVCONLINESERCHDLGDESCRIPTION: Part Description").
      setReadOnly().
      setFunction("''");
      
      headblk.addField("PART_REVISION").
      setLabel("PCMWSRVCONLINESERCHDLGPARTREVISION: Part Revision").
      setUpperCase().
      setFunction("''").
      setReadOnly();
      
      headblk.addField("SERIAL_NO").
      setSize(10).
      setMaxLength(50).
      setLabel("PCMWSRVCONLINESERCHDLGSERIALNO: Serial No").
      setUpperCase().
      setFunction("''").
      setReadOnly();
      
      headblk.addField("WORK_TYPE_ID").
      setLabel("PCMWSRVCONLINESERCHDLGWORKTYPEID: Work Type").
      setUpperCase().
      setSize(18).
      setFunction("''").
      setMaxLength(20);
      
      headblk.addField("WORKTYPEDESCRIPTION").
      setSize(30).
      setFunction("''").
      setReadOnly().
      setLabel("PCMWSRVCONLINESERCHDLGWORKTYPEDESC: Work Type Description");
 
      headblk.addField("USEASLOV").
      setFunction("''").
      setHidden();

      headblk.setView("DUAL");
      headblk.disableDocMan();
      headset = headblk.getASPRowSet();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("PCMWSRVCONLINESERCHDLGTITLE: Service Contract Search");
      
      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.BACK,"cancel");
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW); 
      headbar.disableCommand(headbar.DELETE); 
      headbar.disableCommand(headbar.FIND); 
      headbar.disableCommand(headbar.BACK);
      
      itemblk0 = mgr.newASPBlock("ITEM0");
      
      itemblk0.addField("ITEM0_OBJID").
      setHidden().
      setDbName("OBJID");
      
      itemblk0.addField("ITEM0_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");
      
      itemblk0.addField("TOP_PART_NO").
      setLabel("PCMWSRVCONLINESERCHDLGTOPPARTNO: Top Part No").
      setReadOnly();
      
      itemblk0.addField("TOP_PART_REV").
      setLabel("PCMWSRVCONLINESERCHDLGTOPPARTREV: Top Part Rev").
      setReadOnly();
      
      itemblk0.addField("CONTRACT_ID").
      setLabel("PCMWSRVCONLINESERCHDLGCONTRACTID: Contract ID").
      setReadOnly();
      
      itemblk0.addField("CONTRACT_NAME").
      setLabel("PCMWSRVCONLINESERCHDLGCONREACTNAME: Contract Name").
      setReadOnly();
      
      itemblk0.addField("LINE_NO").
      setLabel("PCMWSRVCONLINESERCHDLGLINENO: Line No").
      setReadOnly();
      
      itemblk0.addField("DESCRIPTION").
      setLabel("PCMWSRVCONLINESERCHDLGLINEDESCRIPTION: Description").
      setReadOnly();
      
      itemblk0.addField("WORK_SHOP_CODE").
      setLabel("PCMWSRVCONLINESERCHDLGWORKSHOPCODE: Work Shop Code").
      setReadOnly();
      
      itemblk0.addField("WORK_SHOP_DESC").
      setLabel("PCMWSRVCONLINESERCHDLGWORKSHOPDESC: Work Shop Description").
      setFunction("MAINT_WORKSHOP_API.Get_Workshop_Desc(:WORK_SHOP_CODE)").
      setReadOnly();
      
      itemblk0.addField("NODE_STRUCTURE").
      setLabel("PCMWSRVCONLINESERCHDLGNODESTRUCTURE: Node Structure").
      setReadOnly();
      
      itemblk0.addField("PARENT_LINE_NO").
      setLabel("PCMWSRVCONLINESERCHDLGPARENTLINENO: Parent Line No").
      setReadOnly();
      
      itemblk0.addField("DATE_FROM").
      setLabel("PCMWSRVCONLINESERCHDLGDATEFROM: Date From").
      setReadOnly();
      
      itemblk0.addField("EXPIRY_DATE").
      setLabel("PCMWSRVCONLINESERCHDLGEXPIRYDATE: Expiry Date").
      setReadOnly();
      
      itemblk0.addField("INVOICE_RULE_ID").
      setLabel("PCMWSRVCONLINESERCHDLGINVOICERULEID: Invoice Rule ID").
      setReadOnly();
      
      itemblk0.addField("INVOICE_TYPE").
      setLabel("PCMWSRVCONLINESERCHDLGINVOICETYPE: Invoice Type").
      setReadOnly();
       
      itemblk0.addField("INVOICE_TYPE_DB").
      setLabel("PCMWSRVCONLINESERCHDLGINVOICETYPEDB: Invoice Type db").
      setReadOnly();
       
      itemblk0.addField("CAP_PRICE").
      setLabel("PCMWSRVCONLINESERCHDLGCAPPRICE: Cap Price").
      setReadOnly();
       
      itemblk0.addField("FIXED_CATALOG_NO").
      setLabel("PCMWSRVCONLINESERCHDLGFIXEDCATALOGNO: Fixed Price Execution / Sales Part No").
      setReadOnly();
      
      itemblk0.addField("FIXED_CATALOG_DESC").
      setLabel("PCMWSRVCONLINESERCHDLGFIXEDCATALOGDESC: Fixed Price Execution / Sales Part Description").
      setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:FIXED_CATALOG_NO)").
      setReadOnly();
       
      //Bug 84436, Start
      f = itemblk0.addField("FIXED_CATALOG_CONTR");
      f.setLabel("PCMWSRVCONLINESERCHDLGFIXEDCATALOGDESC: Fixed Price Execution / Sales Part Site");
      if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract(:CONTRACT_ID)");
       else
            f.setFunction("''");       
      f.setReadOnly();
      //Bug 84436, End
      
      itemblk0.addField("PRICE").
      setLabel("PCMWSRVCONLINESERCHDLGPRICE: Price").
      setReadOnly();
       
      itemblk0.addField("DISCOUNT").
      setLabel("PCMWSRVCONLINESERCHDLGDISCOUNT: Discount").
      setReadOnly();
       
      itemblk0.addField("INVOICE_CATALOG_NO").
      setLabel("PCMWSRVCONLINESERCHDLGINVOICECATALOGNO: Periodic Invoicing / Sales Part No").
      setReadOnly();
       
      itemblk0.addField("INVOICE_CATALOG_DESC").
      setLabel("PCMWSRVCONLINESERCHDLGINVOICECATALOGDESC: Periodic Invoicing / Sales Part Description").
      setFunction("SALES_PART_API.Get_Catalog_Desc(CONTRACT,INVOICE_CATALOG_NO)").
      setReadOnly();
       
      //Bug 84436, Start
      f = itemblk0.addField("INVOICE_CATALOG_CONTR");
      f.setLabel("PCMWSRVCONLINESERCHDLGINVOICECATALOGCONTR: Periodic Invoicing / Sales Part Site");
      if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract(CONTRACT_ID)");
       else
            f.setFunction("''");      
      f.setReadOnly();
      //Bug 84436, End 
      
      itemblk0.addField("ALT_CUSTOMER_ID").
      setLabel("PCMWSRVCONLINESERCHDLGALTCUSTOMERID: Periodic Invoicing / Alternative Customer").
      setReadOnly();
       
      itemblk0.addField("SLA_ID").
      setLabel("PCMWSRVCONLINESERCHDLGSLAID: SLA ID").
      setReadOnly();
       
      itemblk0.addField("SLA_CALENDAR_ID").
      setLabel("PCMWSRVCONLINESERCHDLGSLACALENDARID: SLA Calendar").
      setReadOnly();
       
      itemblk0.addField("SETTING_TIME").
      setLabel("PCMWSRVCONLINESERCHDLGSETTINGTIME: Setting Time").
      setReadOnly();
      
      itemblk0.addField("MAX_RESOLUTION_TIME").
      setLabel("PCMWSRVCONLINESERCHDLGMAXRESOLUTIONTIME : Max Resolution Time").
      setReadOnly();
      
      itemblk0.addField("CUT_OFF_TIME").
      setLabel("PCMWSRVCONLINESERCHDLGCUTOFFTIME: Cut Off Time").
      setReadOnly();
      
      itemblk0.addField("RESPONSE_TIME1").
      setLabel("PCMWSRVCONLINESERCHDLGRESPONSETIME1: Severity Weight1").
      setReadOnly();
      
      itemblk0.addField("RESPONSE_TIME2").
      setLabel("PCMWSRVCONLINESERCHDLGRESPONSETIME2: Severity Weight2").
      setReadOnly();
      
      itemblk0.addField("RESPONSE_TIME3").
      setLabel("PCMWSRVCONLINESERCHDLGRESPONSETIME3: Severity Weight3").
      setReadOnly();
      
      itemblk0.addField("RESPONSE_TIME4").
      setLabel("PCMWSRVCONLINESERCHDLGRESPONSETIME4: Severity Weight4").
      setReadOnly();
      
      //Bug 84436, Start
      f = itemblk0.addField("EXECUTION_CAL");
      f.setLabel("PCMWSRVCONLINESERCHDLGEXECUTIONCAL: Execution Calendar");      
      if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Calendar_Id(CONTRACT_ID)");
      else
            f.setFunction("''");      
      f.setReadOnly();
      //Bug 84436, End 
      
      itemblk0.addField("STD_JOB_ID").
      setLabel("PCMWSRVCONLINESERCHDLGSTDJOBID: Std Job ID").
      setReadOnly();
      
      itemblk0.addField("STD_JOB_DESC").
      setLabel("PCMWSRVCONLINESERCHDLGSTDJOBDESC: Std Job Description").
      setFunction("STANDARD_JOB_API.Get_Definition(std_job_id,CONTRACT_SITE,STD_JOB_REVISION)").
      setReadOnly();
      
      itemblk0.addField("STD_JOB_REVISION").
      setLabel("PCMWSRVCONLINESERCHDLGSTDJOBREVISION: Std Job Revision").
      setReadOnly();
      
      itemblk0.addField("FAULT_REPORT_AGREEMENT").
      setLabel("PCMWSRVCONLINESERCHDLGFAULTREPORTAGREEMENT: Fault Report Agreement").
      setReadOnly();
      
      itemblk0.addField("FAULT_REPORT_AGREEMENT_DB").
      setLabel("PCMWSRVCONLINESERCHDLGFAULTREPORTAGREEMENT: Fault Report Agreement db").
      setHidden().
      setReadOnly();

      
      itemblk0.setView("PSC_CONTR_PRODUCT a, " +
                     "(SELECT PART_NO, PART_REV, SERIAL_NO FROM VIM_SERIAL_STRUCTURE " +
                     "START WITH part_no = ? AND serial_no = ? " +
                     "CONNECT BY PRIOR parent_seq_no = seq_no) b " +
                     "WHERE a.top_part_no = b.part_no " +
                     "AND a.top_part_rev = b.part_rev " +
                     "AND a.fault_report_agreement_db = NVL('2', a.fault_report_agreement_db) " +
                     "AND (a.node_structure = 'Y' OR (b.part_no = ? AND b.serial_no = ?)) " +
                     "AND (Sc_Contract_Customer_API.Check_Exist(a.contract_id, ?) = 'TRUE' " +
                     "OR Sc_Service_Contract_API.Get_Customer_Id(a.contract_id) = ?) " +
                     "AND (a.work_type_id = nvl( ?, a.work_type_id) OR a.work_type_id = '%') " +
                     "AND Sc_Service_Contract_API.Get_Contract(a.contract_id) = nvl( ? , Sc_Service_Contract_API.Get_Contract(a.contract_id)) " +
                     "AND Sc_Service_Contract_API.Get_State_Db(a.contract_id) IN ('Active') ");
       
       
       
      itemblk0.defineCommand("PSC_CONTR_PRODUCT_API","");
      itemblk0.setMasterBlock(headblk);
      itemblk0.disableDocMan();
      itemset0 = itemblk0.getASPRowSet();
      
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle("PCMWSRVCONLINESERCHDLGCONTRLINES: Vehicle Service Line");
      itemtbl0.setWrap();
      
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableCommand(itembar0.FIND);
      
      itembar0.addCustomCommand("selectContract",mgr.translate("PCMWSRVCONLINESERCHDLGITEMSET0SELECT: Select"));
      
      //itembar0.enableRowStatus();
      
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
//      itemlay0.setDialogColumns(2);
   }

 //===============================================================
// HTML
//===============================================================
   protected String getDescription()
   {
       return "PCMWSRVCONLINESERCHDLGTITLE: Service Contract Search";
   }

   protected String getTitle()
   {
       return "PCMWSRVCONLINESERCHDLGTITLE: Service Contract Search";
   }

   protected void printContents() throws FndException {
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());
		if (headlay.isSingleLayout() && (headset.countRows() > 0))
			appendToHTML(itemlay0.show());
		if (refreshMain) {
			if (bUseAsLov) {
				appendDirtyJavaScript("  window.opener.setContractId('");
				appendDirtyJavaScript(mgr.encodeStringForJavascript(sContractId));
				appendDirtyJavaScript("', '");
				appendDirtyJavaScript(mgr.encodeStringForJavascript(sLineNo));
				appendDirtyJavaScript("', true);\n");
				appendDirtyJavaScript("  window.close();\n");
			}
			else {
				appendDirtyJavaScript("  window.opener.setContractId('");
				appendDirtyJavaScript(mgr.encodeStringForJavascript(sContractId));
				appendDirtyJavaScript("', '");
				appendDirtyJavaScript(mgr.encodeStringForJavascript(sLineNo));
				appendDirtyJavaScript("', false);\n");
				appendDirtyJavaScript("  window.close();\n");
			}
		}
	}
}
