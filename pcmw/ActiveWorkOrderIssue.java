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
 *  File        : ActiveWorkOrderIssue.java 
 *  Created     : 030502  JEJALK
 *  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
 *  ARWILK  041005  LCS Merge: 46434.
 *  JaJalk  060303  Made the respective changes as the LCS 54432 was only treated to centura client.
 *  JEWILK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
 *  NAMELK  060906  Merged Bug 58216.
 *  SHAFLK  071101  Bug 68811 Modified preDefine()
 * ----------------------------------------------------------------------------
 */

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveWorkOrderIssue extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveWorkOrderIssue");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPBlockLayout headblklay;
	private ASPTable headtbl;

	//private ASPField fOwnerCode;
	//private ASPField fOwnerName;
	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================

	private String matReqNo;
	private String orderno;
	private String lineno;
	private String dataQueryPresent;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveWorkOrderIssue(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();   
		ctx = mgr.getASPContext();

		orderno = ctx.readValue("ORDERNO",""); 
		lineno = ctx.readValue("LINENO","");  

		if (mgr.commandBarActivated())
		{

			eval(mgr.commandBarFunction());
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("OKFIND")))
		{
			okFind();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("ORDER_NO")))
		{
			orderno =  mgr.readValue("ORDER_NO","") ;
			lineno  =  mgr.readValue("LINE_ITEM_NO","");
			okFind();
		}
		else if (mgr.dataTransfered())
			okFind();

		adjust();   

		ctx.writeValue("ORDERNO",orderno);
		ctx.writeValue("LINENO",lineno);
	}

	//-----------------------------------------------------------------------------
	//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
	//-----------------------------------------------------------------------------

	public void  countFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

                q.addWhereCondition("ORDER_NO = ?");
		q.addWhereCondition("LINE_ITEM_NO = ?");
		q.addWhereCondition("TRANSACTION_CODE = 'WOISS'");
		//Bug 46434, end
		q.addWhereCondition("(QUANTITY - QTY_REVERSED) > 0 ");
		q.addWhereCondition("NVL(DIRECTION,'-')  = '-'");
                q.addParameter("ORDER_NO",orderno);
                q.addParameter("LINE_ITEM_NO",lineno);

		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headblklay.setCountValue(toInt(headset.getValue("N")));
		headset.clear();   
	}

	public void  okFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		if (!mgr.isEmpty(orderno))
                {
                    q.addWhereCondition("ORDER_NO = ?");
                    q.addParameter("ORDER_NO",orderno);
                }

		if (!mgr.isEmpty(lineno))
                {
                    q.addWhereCondition("LINE_ITEM_NO = ?");
                    q.addParameter("LINE_ITEM_NO",lineno);
                }
                        
		q.addWhereCondition("TRANSACTION_CODE = 'WOISS'");
		//Bug 46434, end
		q.addWhereCondition("(QUANTITY - QTY_REVERSED) > 0 ");
		q.addWhereCondition("NVL(DIRECTION,'-')  = '-'");

		q.includeMeta("ALL");

		mgr.querySubmit(trans,headblk);

		if ( headset.countRows() == 0 )
		{
			mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERISSUENODATA: No data found."));     
			headset.clear();
		}
		else
		{
			mgr.createSearchURL(headblk);
		}    
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("QTYISSUED","Number").
		setSize(12).
		setLabel("PCMWACTIVEWORKORDERISSUEQTYISSUED: Quantity Issued").
		setFunction("NVL(quantity,0) - NVL(qty_reversed,0)").
		setReadOnly();  

		headblk.addField("PART_NO").
		setSize(25).
		setMaxLength(25).
		setLOV("../invenw/InventoryPartWoLovLov.page","CONTRACT",600,445).
		setLabel("PCMWACTIVEWORKORDERPART_NO: Part No").
		setUpperCase().
		setReadOnly();

		headblk.addField("PARTDESC").
		setSize(25).
		setLabel("PCMWACTIVEWORKORDERPARTDESC: Part Description").
		setFunction("Inventory_Part_API.Get_Description(:CONTRACT,:PART_NO)").
		setReadOnly();     

		headblk.addField("CONTRACT").
		setSize(5).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
		setLabel("PCMWACTIVEWORKORDERITEM0_CONTRACT: Site").
		setUpperCase().
		setReadOnly();

		headblk.addField("LOCATION_NO").
		setSize(25).
		setMaxLength(35).
		setLabel("PCMWACTIVEWORKORDERLOCATION_NO: Location No").
		setReadOnly();

		headblk.addField("WAREHOUSE").
		setSize(25).
		setMaxLength(25).
		setLabel("PCMWACTIVEWORKORDERWAREHOUSE: Warehouse").
		setFunction("Inventory_Location_API.Get_Warehouse(:CONTRACT,:LOCATION_NO)").
		setReadOnly();

		headblk.addField("BAY").
		setSize(25).
		setMaxLength(25).
		setLabel("PCMWACTIVEWORKORDERBAY: Bay").
		setFunction("Inventory_Location_API.Get_Bay_No(:CONTRACT,:LOCATION_NO)").
		setReadOnly();

		headblk.addField("ROW_NO").
		setSize(25).
		setMaxLength(25).
		setLabel("PCMWACTIVEWORKORDERROW_NO: Row").
		setFunction("Inventory_Location_API.Get_Row_No(:CONTRACT,:LOCATION_NO)").
		setReadOnly();          

		headblk.addField("TIER").
		setSize(25).
		setMaxLength(25).
		setLabel("PCMWACTIVEWORKORDERTIER: Tier").
		setFunction("Inventory_Location_API.Get_Tier_No(:CONTRACT,:LOCATION_NO)").
		setReadOnly();

		headblk.addField("BIN").
		setSize(25).
		setMaxLength(25).
		setLabel("PCMWACTIVEWORKORDERBIN: Bin").
		setFunction("Inventory_Location_API.Get_Bin_No(:CONTRACT,:LOCATION_NO)").
		setReadOnly();

		headblk.addField("LOT_BATCH_NO").
		setSize(25).
		setMaxLength(20).
		setLabel("PCMWACTIVEWORKORDERLOT_BATCH_NO: Lot/Batch No").
		setReadOnly();

		headblk.addField("SERIAL_NO").
		setSize(20).
		setMaxLength(50).
		setLabel("PCMWACTIVEWORKORDERSERIAL_NO: Serial No").
		setUpperCase().
		setReadOnly();

		headblk.addField("CONDITION_CODE").
		setLabel("PCMWACTIVEWORKORDERCONDITIONCODE: Condition Code").
		setSize(15).
		setReadOnly();

		headblk.addField("CONDDESC").
		setLabel("PCMWACTIVEWORKORDERCONDDESC: Condition Code Description").
		setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)").
		setSize(20).
		setMaxLength(50).
		setReadOnly();

		headblk.addField("WAIV_DEV_REJ_NO").
		setSize(12).
		setLabel("PCMWACTIVEWORKORDERWAIV_DEV_REJ_NO: W/D/R No").
		setReadOnly();

		headblk.addField("ENG_CHG_LEVEL").
		setSize(3).
		setMaxLength(2).
		setLabel("PCMWACTIVEWORKORDERENG_CHG_LEVEL: EC").
		setReadOnly();

		headblk.addField("QUANTITY","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERQUANTITY: Quantity").
		setReadOnly();

		headblk.addField("COST","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERCOST: Cost").
		setReadOnly();

		headblk.addField("LINE_ITEM_NO","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERLINE_ITEM_NO: Line Item No").
		setReadOnly();

		headblk.addField("QTY_REVERSED","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERQTY_REVERSED: Qty Reversed").
		setReadOnly();

		headblk.addField("DATE_CREATED","Date").
		setSize(12).
		setLabel("PCMWACTIVEWORKORDERDATECREATED: Created").
		setReadOnly().
		setHidden();

		headblk.addField("ACCOUNTING_ID","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERACCOUNTING_ID: Accounting Id").
		setReadOnly();

		headblk.addField("TRANSACTION_ID","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERTRANSACTION_ID: TransactionId").
		setReadOnly();

		headblk.addField("PARTSHORTAGESEXIST","Number").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERPARTSHORTAGESEXIST: Shortages Exist").
		setFunction("Shortage_Demand_API.Shortage_Exists(:CONTRACT,:PART_NO)").
		setReadOnly();

		headblk.addField("PARTSHORTAGEFLAG").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERPARTSHORTAGEFLAG: PartShortageFlag").
		setFunction("Inventory_Part_API.Get_Shortage_Flag(:CONTRACT,:PART_NO)").
		setReadOnly();

		headblk.addField("SEQUENCE_NO").
		setSize(12).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERSEQUENCE_NO: PartShortageFlag").
		setReadOnly();

		headblk.addField("SOURCE").
		setSize(12).
      setMaxLength(2000).
		setHidden().
		setLabel("PCMWACTIVEWORKORDERSOURCE: Source").
		setReadOnly();

		headblk.addField("ORDER_NO").
		setSize(12).
		setHidden().
		setReadOnly();

		headblk.addField("DIRECTION").
		setSize(12).
		setHidden().
		setReadOnly();

		//Bug 46434, start
		headblk.addField("TRANSACTION_CODE").
		setSize(12).
		setHidden().
		setReadOnly();
		//Bug 46434, end

		headblk.setView("INVENTORY_TRANSACTION_HIST");
		headset = headblk.getASPRowSet();

		headset = headblk.getASPRowSet();
		headblklay = headblk.getASPBlockLayout();
		headblklay.setDialogColumns(3);
		headblklay.setDefaultLayoutMode(headblklay.MULTIROW_LAYOUT);

		headbar = mgr.newASPCommandBar(headblk);
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWACTIVEWORKORDERISSUE: Inventory Transaction Hist"));
		headtbl.setWrap();

	}

	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		if ( headset.countRows() == 0 )
		{
			headbar.disableCommand(headbar.BACK);
			headbar.disableCommand(headbar.BACKWARD);
			headbar.disableCommand(headbar.FORWARD); 
			headbar.disableCommand(headbar.NEWROW);
		}
	}

	//===============================================================
	//  HTML
	//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVEWORKORDERISSUEISSUEDPDETAILS: Issued Part Details";
	}

	protected String getTitle()
	{
		ASPManager mgr = getASPManager();

		return "PCMWACTIVEWORKORDERISSUEISSUEDPDETAILS: Issued Part Details";
	}

	protected void printContents() throws FndException
	{
		appendToHTML(headblklay.show());
	}
}
