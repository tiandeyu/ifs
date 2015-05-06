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
*  File        : WorkOrderCodingDlg.java 
*  Created     : ASP2JAVA Tool  010427  Created Using the ASP file WorkOrderCodingDlg.asp
*  Modified    :
*  VAGULK  010502  Corrected some conversion errors.
*  BUNILK  010731  Modified run() method and added new method okFindNew().
*  JEWILK  010821  Modified to refresh the calling form when saved. 
*                    Modified validation of field 'LIST_PRICE'.
*  SHCHLK  010829  Added validation to the field 'QTY_TO_INVOICE'
*  JEWILK  010829  Modified validation of field 'LIST_PRICE' and added validation for field 'DISCOUNT'.
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
* ------------------------------- Edge - SP1 Merge ---------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  NIJALK  060311  Call 136824: Modified getDescription(),getTitle(),printContents().
*  SULILK  060303  Call : Modified validate(), preDefine().
* ----------------------------------------------------------------------------
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060814  Bug 58216, Eliminated SQL errors in web applications. Modified methods okFind(), okFindSave()
*  AMDILK  060904  Merged with the Bug Id 58216
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderCodingDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderCodingDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String wo_No;
	private String row_Nos;
	private String separator_;
	private String getSelectedRows_;
	private String crePlanPath;
	private String fmtdBuff;
	private String creFromPlan;
	private int sentrowNo;
	private String filterCostType;
	private String saFlag;
	private String mform;
	private String mform1;
	private String authFlag;
	private ASPTransactionBuffer trans;
	private ASPTransactionBuffer trans1;
	private String sWoNo;
	private String sRowNo;
	private String singleRowNo;
	private String calling_url;
	private String companyvar;
	private boolean sentNoEmployee;
	private boolean sentEmployee;
	private boolean recordCancel;
	private boolean enableF;
	private boolean backFlag;
	private ASPTransactionBuffer secBuff;
	private String val;
	private ASPCommand cmd;
	private String strCatalogDeac;
	private String strClient3;
	private String strClient4;
	private String strAgreement;
	private double bprice;
	private String strWorkOrderCost;
	private String nQunatity;
	private double nBuyQtyDue;
	private double nQty;
	private int priceflag;
	private String sPriceListNo;
	private double nCustomerAgreement;
	private String sPriceListNo1;
	private double nAmountSales;
	private double nList;
	private ASPQuery q;
	private int currrow;
	private String woNo;
	private boolean closeFlag;
	private String returnForm;
	private String returnWnd;

	//================================================================================
	// ASP2JAVA WARNING: Types of the following variables are not identified by
	// the ASP2JAVA converter. Define them and remove these comments.
	//================================================================================

	public  String isSecure[];
	private String sAgreeId; 
	private int rowToEdit;  
	private double nSalesPriceAmount; 
	private String txt; 
	private double nBaseUnitPrice; 
	private double nSaleUnitPrice; 
	private double nCurrencyRate; 
	private double nDiscount; 
	private double nSalesPartListPrice; 

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderCodingDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		isSecure =  null ;
		sentrowNo =  1;

		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		trans1 = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		fmt = mgr.newASPHTMLFormatter();
		sWoNo = ctx.readValue("CTXWONO","");
		sAgreeId = ctx.readValue("CTXAGREE",sAgreeId);
		sRowNo = ctx.readValue("CTXROWNO","");
		singleRowNo = ctx.readValue("CTXSINGLEROWNO","");
		calling_url = ctx.getGlobal("CALLING_URL");
		companyvar =  ctx.readValue("COMPANYVAR",""); 
		authFlag = ctx.readValue("AUTHFLAG","FALSE");
		crePlanPath = ctx.readValue("CREPLPOS",crePlanPath);
		creFromPlan = ctx.readValue("CREFRMPLPOS","flase");
		saFlag = ctx.readValue("SAFLAG","flase");
		rowToEdit = ctx.readNumber("ROTEDTIPOS",1);
		sentNoEmployee = ctx.readFlag("SENTNOEMPOS",false);
		sentEmployee = ctx.readFlag("SENTEMPPOS",false);
		sentrowNo = ctx.readNumber("SENTRWONPOS",sentrowNo);
		mform1 = ctx.readValue("MFORM1",mform1);
		mform = ctx.readValue("MFORM",mform);
		recordCancel = ctx.readFlag("RECCENRPOS",false);
		enableF = ctx.readFlag("ENABLEF",false);
		backFlag = ctx.readFlag("BACKFLAG",false);
		filterCostType = ctx.readValue("FICOTTYP",filterCostType);

		if (mgr.commandBarActivated())
		{
			eval(mgr.commandBarFunction());
		}

		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			sWoNo = mgr.readValue("WO_NO");
			getcompany();
			okFind();
			mgr.setPageExpiring();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("ROW_NO")))
		{
			sRowNo = mgr.readValue("ROW_NO");
			sWoNo = mgr.getQueryStringValue("WONO");
			singleRowNo = mgr.getQueryStringValue("SINGLE_ROW_NO");

			mform = mgr.getQueryStringValue("BACKFRMNAME");

			getcompany();
			okFindRow();
			mgr.setPageExpiring();
		}
		else if (mgr.dataTransfered())
			okFind();

		adjust();

		ctx.writeValue("CTXWONO",sWoNo);
		ctx.writeValue("CTXAGREE",sAgreeId);
		ctx.writeValue("CTXROWNO",sRowNo);
		ctx.writeValue("CTXSINGLEROWNO",singleRowNo);
		ctx.writeValue("COMPANYVAR",companyvar);
		ctx.writeValue("CREPLPOS",crePlanPath);
		ctx.writeValue("CREFRMPLPOS",creFromPlan);
		ctx.writeValue("SAFLAG",saFlag);
		ctx.writeNumber("ROTEDTIPOS",rowToEdit);
		ctx.writeFlag("SENTNOEMPOS",sentNoEmployee);
		ctx.writeFlag("SENTEMPPOS",sentEmployee);
		ctx.writeNumber("SENTRWONPOS",sentrowNo);
		ctx.writeFlag("RECCENRPOS",recordCancel);
		ctx.writeValue("FICOTTYP",filterCostType);
		ctx.writeValue("MFORM1",mform1);
		ctx.writeFlag("ENABLEF",enableF);
		ctx.writeFlag("BACKFLAG",backFlag);
		ctx.writeValue("MFORM",mform);
		ctx.writeValue("AUTHFLAG",authFlag);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

	public boolean  checksec( String method,int ref,String[]isSecure1)
	{

		ASPManager mgr = getASPManager();

		isSecure1[ref] = "false" ;
		String splitted[] = split(method,".");

		String first = splitted[0].toString();
		String Second = splitted[1].toString();

		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery(first,Second);
		secBuff = mgr.perform(secBuff);
		if (secBuff.getSecurityInfo().itemExists(method))
		{
			isSecure1[ref] = "true";
			return true; 
		}
		else
			return false;
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();
		isSecure = new String[7];
		val = mgr.readValue("VALIDATE");
		double nAmount;


		if ("CATALOG_NO".equals(val))
		{

			if (checksec("Sales_Part_API.Get_Catalog_Desc",1,isSecure))
			{
				cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","LINEDESCRIPTION");
				cmd.addParameter("CONTRACT");
				cmd.addParameter("CATALOG_NO");
			}

			cmd = trans.addCustomFunction("CLIENT3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3");
			cmd = trans.addCustomFunction("CLIENT4","Work_Order_Cost_Type_Api.Get_Client_Value(4)","CLIENTVAL4");

			trans = mgr.validate(trans);

			if (isSecure[1] =="true")
			{
				strCatalogDeac= trans.getValue("CATDESC/DATA/LINEDESCRIPTION");
			}
			else
				strCatalogDeac= "";

			strClient3= trans.getValue("CLIENT3/DATA/CLIENTVAL3");
			strClient4= trans.getValue("CLIENT4/DATA/CLIENTVAL4");

			nAmount = mgr.readNumberValue("AMOUNT");
			strAgreement = "0" ;
			bprice = 0;

			strWorkOrderCost= mgr.readValue("WORK_ORDER_COST_TYPE");


			if (!(strClient4.equals(strWorkOrderCost)))
			{

				if (strClient3.equals(strWorkOrderCost))
				{
					nQunatity = mgr.readValue("QTY");
					if (!mgr.isEmpty(nQunatity))
					{
						trans.clear();
						if (checksec("Sales_Part_API.Get_Cost",2,isSecure))
						{
							cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","AMOUNT_VAR");
							cmd.addParameter("CONTRACT");
							cmd.addParameter("CATALOG_NO");

							trans = mgr.validate(trans);
							nAmount= trans.getNumberValue("COSTING/DATA/AMOUNT_VAR");
						}
						else
							nAmount	= 0.00;
					}
				}
			}
			trans.clear();

			nBuyQtyDue = mgr.readNumberValue("QTY");

			if (isNaN(nBuyQtyDue))
				nBuyQtyDue = 0;

			if (nBuyQtyDue == 0)
				nBuyQtyDue=1;

			cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
			cmd.addParameter("BASE_PRICE");
			cmd.addParameter("SALE_PRICE");
			cmd.addParameter("DISCOUNT");
			cmd.addParameter("CURRENCY_RATE");
			cmd.addParameter("CONTRACT");
			cmd.addParameter("CATALOG_NO");
			cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
			cmd.addParameter("AGREEMENT_ID",mgr.readValue("AGREEMENT_ID"));
			cmd.addParameter("PRICE_LIST_NO");
			cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));

			trans = mgr.validate(trans);

			bprice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
			if (isNaN(bprice))
				bprice = 0;

			double discount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
			if (isNaN(discount))
				discount = 0;

			nQty = mgr.readNumberValue("QTY");

			nSalesPriceAmount = nQty * bprice;
			nSalesPriceAmount = nSalesPriceAmount - ((discount * nSalesPriceAmount) / 100);

			strAgreement = "1" ;
			nAmount =0.00;
			priceflag = 1; 

			String nAmountStr = mgr.formatNumber("AMOUNT",nAmount);
			String nSalesPriceAmountStr =mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);
			String discountStr =mgr.formatNumber("DISCOUNT",discount); 
			String priceflagStr = "1";
			String bpriceStr = mgr.formatNumber("LIST_PRICE",bprice); 

			txt =  (mgr.isEmpty(strCatalogDeac) ? "" :strCatalogDeac) + "^" +  (mgr.isEmpty(strAgreement ) ? "" :strAgreement ) + "^" +  (mgr.isEmpty(nAmountStr) ? "" :nAmountStr) + "^" +  (mgr.isEmpty(bpriceStr) ? "" :bpriceStr) + "^"+ (mgr.isEmpty(nSalesPriceAmountStr) ? "" : (nSalesPriceAmountStr))+ "^" + (mgr.isEmpty(priceflagStr) ? "" :priceflagStr) + "^" + (mgr.isEmpty(discountStr) ? "" :discountStr) + "^";

			mgr.responseWrite(txt);
			mgr.endResponse();
		}

		if ("PRICE_LIST_NO".equals(val))
		{
			nBuyQtyDue= mgr.readNumberValue("QTY_TO_INVOICE");
			if (isNaN(nBuyQtyDue))
				nBuyQtyDue = 0;
			if (nBuyQtyDue == 0)
				nBuyQtyDue=1;

			String nCustomerAgreementStr = "0";

			strWorkOrderCost= mgr.readValue("WORK_ORDER_COST_TYPE");
			String isFixedPrice = mgr.readValue("CLIENTVAL4");

			if (!isFixedPrice.equals(strWorkOrderCost))
			{
				cmd = trans.addCustomCommand("PRICEINFO","fWork_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE");
				cmd.addParameter("SALE_PRICE");
				cmd.addParameter("DISCOUNT");
				cmd.addParameter("CURRENCY_RATE");
				cmd.addParameter("CONTRACT");
				cmd.addParameter("CATALOG_NO");
				cmd.addParameter("CUSTOMER_NO");
				cmd.addParameter("AGREEMENT_ID");
				cmd.addParameter("PRICE_LIST_NO");
				cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));

				trans = mgr.validate(trans);

				nBaseUnitPrice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
				nSaleUnitPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE");
				nCurrencyRate= trans.getNumberValue("PRICEINFO/DATA/CURRENCY_RATE");
				nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
				sPriceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

				nSalesPartListPrice = toDouble(nBaseUnitPrice);

				nQty =mgr.readNumberValue("QTY_TO_INVOICE") ;
				if (isNaN(nQty))
					nAmountSales=0;
				else
				{
					nAmountSales = nSalesPartListPrice * nQty;
					nAmountSales = nAmountSales - ((nDiscount * nAmountSales) / 100) ;
				}

				nCustomerAgreement=1;

				nCustomerAgreementStr = "1";
			}
			else
			{
				nSalesPartListPrice = mgr.readNumberValue("LIST_PRICE");
				nDiscount = mgr.readNumberValue("DISCOUNT");
				nAmountSales = mgr.readNumberValue("SALESPRICEAMOUNT");
				nCustomerAgreementStr = "0";
			}

			String nAmountSalesStr = mgr.formatNumber("SALESPRICEAMOUNT",nAmountSales);
			String nSalesPartListPriceStr = mgr.formatNumber("LIST_PRICE",nSalesPartListPrice);
			String nDiscountStr = mgr.formatNumber("DISCOUNT",nDiscount);


			txt = (mgr.isEmpty(nAmountSalesStr)?"":nAmountSalesStr) + "^" + 
				  (mgr.isEmpty(nCustomerAgreementStr)?"":nCustomerAgreementStr) + "^" +
				  (mgr.isEmpty(nSalesPartListPriceStr)?"":nSalesPartListPriceStr) + "^" +
				  (mgr.isEmpty(nDiscountStr)?"":nDiscountStr) + "^" ;

			mgr.responseWrite(txt);
			mgr.endResponse();

		}

		if ("LIST_PRICE".equals(val))
		{
			nQty = mgr.readNumberValue("QTY_TO_INVOICE");
			if (isNaN(nQty))
				nQty = 0;

			nList =mgr.readNumberValue("LIST_PRICE");  
			if (isNaN(nList))
				nList = 0;

			double discount =mgr.readNumberValue("DISCOUNT");
			if (isNaN(discount))
				discount = 0;

			nSalesPriceAmount = nQty * nList;
			nSalesPriceAmount = nSalesPriceAmount - ((discount * nSalesPriceAmount) / 100) ;

			String nSalesPriceAmountStr = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);

			txt = (mgr.isEmpty(nSalesPriceAmountStr)? "" : (nSalesPriceAmountStr))+ "^0^";

			mgr.responseWrite(txt);
			mgr.endResponse();
		}

		if ("DISCOUNT".equals(val))
		{
			nQty = mgr.readNumberValue("QTY_TO_INVOICE");
			if (isNaN(nQty))
				nQty = 0;

			nList =mgr.readNumberValue("LIST_PRICE");  
			if (isNaN(nList))
				nList = 0;

			double discount =mgr.readNumberValue("DISCOUNT");
			if (isNaN(discount))
				discount = 0;

			nSalesPriceAmount = nQty * nList;
			nSalesPriceAmount = nSalesPriceAmount - ((discount * nSalesPriceAmount) / 100) ;

			String nSalesPriceAmountStr = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);

			txt = (mgr.isEmpty(nSalesPriceAmountStr)? "" : (nSalesPriceAmountStr))+ "^";

			mgr.responseWrite(txt);
			mgr.endResponse();
		}
		if ("QTY_TO_INVOICE".equals(val))
		{
			double nQtyToInv = mgr.readNumberValue("QTY_TO_INVOICE");
			if (isNaN(nQtyToInv))
				nQtyToInv =0;

                        String sList = mgr.readValue("LIST_PRICE");

                        ASPBuffer buf = mgr.newASPBuffer();
                        buf.setFieldItem("LIST_PRICE",sList);

                        double nList = buf.getNumberValue("LIST_PRICE");
                        if (isNaN(nList))
				nList =0;

			//double nList =mgr.readNumberValue("LIST_PRICE");
			//if (isNaN(nQtyToInv))
			//	nQtyToInv =0;
			double nDisc =mgr.readNumberValue("DISCOUNT");
			if (isNaN(nDisc))
				nDisc =0;

			if (nDisc != 0)
			{
				nSalesPriceAmount = nQtyToInv * nList;
				nSalesPriceAmount = nSalesPriceAmount - ((nDisc * nSalesPriceAmount) / 100) ;
			}
			else
				nSalesPriceAmount = nQtyToInv * nList;

			String nSalesPriceAmountStr = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);

			txt = (mgr.isEmpty(nSalesPriceAmountStr)? "" : (nSalesPriceAmountStr))+ "^0^";

			mgr.responseWrite(txt);
			mgr.endResponse();
		}
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ?");
		q.addParameter("WO_NO", sWoNo);
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERCODINGDLGNODATA: No data found."));
			headset.clear();
		}

		if (headset.countRows() == 1)
		{
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}

	}


	public void  okFindRow()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(headblk);
		q.addWhereCondition("ROW_NO = ?");
		q.addParameter("DUMMY", sRowNo);
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERCODINGDLGNODATA: No data found."));
			headset.clear();
		}

		if (headset.countRows() == 1)
		{
			headlay.setLayoutMode(headlay.EDIT_LAYOUT);
		}

	}



	public void  okFindSave()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addEmptyQuery(headblk);
		q.addWhereCondition("WO_NO = ?");
		q.addParameter("WO_NO", woNo);
		q.includeMeta("ALL");
		mgr.submit(trans);
	}


	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");

		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void  saveReturn()
	{
		ASPManager mgr = getASPManager();

		currrow = headset.getCurrentRowNo();
		woNo = headset.getRow().getValue("WO_NO");
		headset.changeRow();
		mgr.submit(trans);

		trans.clear();

		headset.goTo(currrow);

		if (mgr.isEmpty(mform))
		{
			returnForm = "ActiveRound";
			returnWnd = "ActiveRound";
		}
		else
		{
			returnForm = "WorkOrderCoding1";
			returnWnd = "WorkOrCoding1";
		}

		closeFlag = true;
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");


		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();      

		f = headblk.addField("COMPANY");
		f.setHidden();
		f.setFunction("''"); 

		f = headblk.addField("AGREEMENT_ID");
		f.setHidden();
		f.setFunction("ACTIVE_WORK_ORDER_API.Get_Agreement_Id(WO_NO)");

		f = headblk.addField("WORK_ORDER_COST_TYPE");
		f.setSize(11);
		f.setMandatory();
		f.setSelectBox();
		f.enumerateValues("WORK_ORDER_COST_TYPE_API");
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2WORKORDETTYPE: Cost Type");
		f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setSize(11);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2CONTRACT: Site");
		f.setUpperCase();

		f = headblk.addField("CUSTOMER_NO");
		f.setHidden();

		f = headblk.addField("CATALOG_NO");
		f.setSize(17);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2CATALOG_NO: Sales Part Number");
		f.setUpperCase();
		f.setCustomValidation("CATALOG_NO,PRICE_LIST_NO,QTY,WORK_ORDER_COST_TYPE,CONTRACT,CUSTOMER_NO,AGREEMENT_ID,AMOUNT","LINEDESCRIPTION,STRING,AMOUNT,LIST_PRICE,SALESPRICEAMOUNT,AGREEMENT_PRICE_FLAG,DISCOUNT");
		f.setMaxLength(18);
		f.setDynamicLOV("SALES_PART","CONTRACT",600,445);   
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGDLGCATNO: Sales Part Number"));

		f = headblk.addField("LINEDESCRIPTION");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERCODINGDLGLINEDESCRIPTION: Description");
		f.setReadOnly();
		f.setFunction("substr(nvl(LINE_DESCRIPTION, Sales_Part_API.Get_Catalog_Desc(CONTRACT,CATALOG_NO)), 1, 35)");

		f = headblk.addField("PRICE_LIST_NO");
		f.setSize(13);
		f.setDynamicLOV("SALES_PRICE_LIST",600,445);
		f.setCustomValidation("PRICE_LIST_NO,QTY,CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,LIST_PRICE,DISCOUNT,SALESPRICEAMOUNT,WORK_ORDER_COST_TYPE,CLIENTVAL4","SALESPRICEAMOUNT,AGREEMENT_PRICE_FLAG,LIST_PRICE,DISCOUNT");
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2PRICELISTNO: Price List No");
		f.setUpperCase();
		f.setMaxLength(10);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGDLGPRLINO2: Price List No"));

		f = headblk.addField("QTY","Number");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2QTY: Hours/Qty");
		f.setReadOnly();

		f = headblk.addField("QTY_TO_INVOICE","Number");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2QTYTOINV: Qty To Invoice");
		f.setDefaultNotVisible(); 
		f.setCustomValidation("QTY_TO_INVOICE,DISCOUNT,LIST_PRICE","SALESPRICEAMOUNT"); 

		f = headblk.addField("PLAN_LINE_NO","Number");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2PLANLINENO: Plan Line No");
		f.setDynamicLOV("WORK_ORDER_PLANNING",600,445);
		f.setHidden();

		f = headblk.addField("AMOUNT","Money");
		f.setSize(11);
		f.setLabel("PCMWWORKORDERCODINGDLGAMOUNT: Cost Amount");
		f.setReadOnly();
		f.setInsertable();
		f.setMandatory();   
		f.setHidden();

		f = headblk.addField("LIST_PRICE","Money");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2SALEPRICE: Sales Price");
		f.setCustomValidation("LIST_PRICE,QTY_TO_INVOICE,DISCOUNT","SALESPRICEAMOUNT,AGREEMENT_PRICE_FLAG");

		f = headblk.addField("PLAN_SALES_PRICE","Money");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2LISTPRICE: Plan Sales Price");
		f.setFunction("WORK_ORDER_PLANNING_API.Get_Sales_Price(:WO_NO,:PLAN_LINE_NO)");
		f.setHidden();

		f = headblk.addField("WORK_ORDER_ACCOUNT_TYPE");
		f.setSize(12);
		f.setHidden();


		f = headblk.addField("DISCOUNT","Number");
		f.setSize(17);
		f.setLabel("PCMWWORKORDERCODINGDLGDISCOUNT: Discount");
		f.setDefaultNotVisible(); 
		f.setCustomValidation("DISCOUNT,LIST_PRICE,QTY_TO_INVOICE","SALESPRICEAMOUNT");

		f = headblk.addField("PLAN_DISCOUNT","Number");
		f.setSize(17);
		f.setLabel("PCMWWORKORDERCODINGDLGPLANDISCOUNT: Plan Discount%");
		f.setFunction("WORK_ORDER_PLANNING_API.Get_Discount(:WO_NO, :PLAN_LINE_NO)");
		f.setHidden();

		f = headblk.addField("SALESPRICEAMOUNT","Money");
		f.setSize(17);
		f.setLabel("PCMWWORKORDERCODINGDLGSALESPRICEAMOUNT: Sales Price Amount");
		// f.setFunction("(LIST_PRICE*QTY)");
		f.setFunction("((LIST_PRICE - (NVL(DISCOUNT, 0) / 100 * LIST_PRICE))*QTY_TO_INVOICE)"); 
		f.setReadOnly();    

		f = headblk.addField("AMOUNT_VAR","Number");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CONTRACT_VAR1");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("AGREEMENT_PRICE_FLAG");
		f.setSize(22);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2AGREEMENTPRICEFLAG: Use Cust. Agreement");
		f.setCheckBox("0,1");
		f.setDefaultNotVisible(); 
		f.setValidateFunction("disableAgreementPriceFlag");

		f = headblk.addField("KEEP_REVENUE");
		f.setSize(14);
		f.setLabel("PCMWWORKORDERCODINGDLGKEEPREVENUE: Keep Revenue");
		f.setSelectBox();
		f.enumerateValues("KEEP_REVENUE_API");

		f = headblk.addField("WORK_ORDER_BOOK_STATUS");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2WORKORDERBOOKSTATUS: Booking Status");
		f.setReadOnly();
		f.setDefaultNotVisible(); 

		f = headblk.addField("CMNT");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2CMNT: Comment");
		f.setMaxLength(80);

		f = headblk.addField("COST_CENTER");  
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2COSTCENTER: Cost Center");
		f.setMaxLength(10);
		f.setDefaultNotVisible(); 

		f = headblk.addField("PROJECT_NO");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGPROJECT: Project");
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("OBJECT_NO");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERCODINGDLGOBJECT: Object");
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("CODE_C");
		f.setSize(13);
		f.setDynamicLOV("CODE_C","COMPANY COMPANY",600,445);
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("CODE_D");
		f.setSize(13);
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("CODE_E");
		f.setSize(13);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODE_G");
		f.setSize(13);
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("CODE_H");
		f.setSize(13);
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("CODE_I");
		f.setSize(13);
		f.setMaxLength(10);
		f.setHidden();

		f = headblk.addField("CODE_J");
		f.setSize(13);
		f.setDynamicLOV("CODE_J","COMPANY COMPANY",600,445);
		f.setMaxLength(10);
		f.setHidden();


		f = headblk.addField("EMP_NO");
		f.setSize(9);
		f.setHidden();
		f.setLabel("PCMWWORKORDERCODINGDLGITEM2EMPNO: Employee");
		f.setUpperCase();

		f = headblk.addField("WO_NO","Number");
		f.setSize(11);
		f.setMandatory();
		f.setHidden();

		f = headblk.addField("CLIENTVAL1");
		f.setHidden();
		f.setFunction("''");    

		f = headblk.addField("CLIENTVAL2");
		f.setHidden();
		f.setFunction("''");    

		f = headblk.addField("CLIENTVAL3");
		f.setHidden();
		f.setFunction("Work_Order_Cost_Type_Api.Get_Client_Value(3)");

		f = headblk.addField("CLIENTVAL4");
		f.setHidden();
		f.setFunction("Work_Order_Cost_Type_Api.Get_Client_Value(4)");

		f = headblk.addField("STRING");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("COSTCENT");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ACTIVEWORKORDERFIXEDPRICE");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERCODINGDLGACTIVEWORKORDERFIXEDPRICE: Fixed Price");
		f.setFunction("ACTIVE_WORK_ORDER_API.Get_Fixed_Price(:WO_NO)");

		f = headblk.addField("NOTFIXEDPRICE");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERCODINGDLGNOTFIXEDPRICE: Not Fixed Price");
		f.setFunction("Fixed_Price_API.Decode('1')");

		f = headblk.addField("BASE_PRICE","Number");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("SALE_PRICE","Number");
		f.setFunction("''");
		f.setHidden();


		f = headblk.addField("CURRENCY_RATE","Number");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("QTY1","Number");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("DEFCONTRACT");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("SALESPARTCATALOGDESC");
		f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
		f.setHidden(); 

		f = headblk.addField("DUMMY");
		f.setFunction("''");
		f.setHidden();

		headblk.setView("WORK_ORDER_CODING");
		headblk.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);

		headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"","self.close()");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERCODINGDLGHD: Postings"));
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setFieldOrder("WORK_ORDER_COST_TYPE,QTY,QTY_TO_INVOICE,CATALOG_NO,LINEDESCRIPTION,PRICE_LIST_NO,LIST_PRICE,DISCOUNT,SALESPRICEAMOUNT,AGREEMENT_PRICE_FLAG,KEEP_REVENUE,CMNT");
		headlay.setDialogColumns(2);

		headlay.setSimple("LINEDESCRIPTION");

                enableConvertGettoPost();

	}


	public void  getcompany()
	{
		ASPManager mgr = getASPManager();


		trans1.clear();

		cmd = trans1.addCustomFunction("DEFCO","User_Default_API.Get_Contract","DEFCONTRACT");

		cmd = trans1.addCustomFunction("COMPA","Site_API.Get_Company","COMPANY");
		cmd.addReference("DEFCONTRACT", "DEFCO/DATA");

		trans1 = mgr.submit(trans1);

		companyvar = trans1.getValue("COMPA/DATA/COMPANY");   
	}


	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isFindLayout())
		{
			mgr.getASPField("CATALOG_NO").setDynamicLOV("SALES_PART",600,445);
			mgr.getASPField("CATALOG_NO").setLOVProperty("WHERE","CONTRACT IS NOT NULL");

		}
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERCODINGDLGDESC: Manage Sales Revenues";
	}

	protected String getTitle()
	{
            return "PCMWWORKORDERCODINGDLGDESC: Manage Sales Revenues";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("window.name = \"WorkOrCodingDlg\";\n");

		if (closeFlag)
		{
			appendDirtyJavaScript("window.open(\""+returnForm+".page?WO_NO=");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(sWoNo));

			if (!mgr.isEmpty(singleRowNo))
			{
				appendDirtyJavaScript("&SINGLE_ROW_NO=");
				appendDirtyJavaScript(mgr.encodeStringForJavascript(singleRowNo));
			}

			appendDirtyJavaScript("&MANAGEREV=TRUE&FORM=\"+URLClientEncode('");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(mform));
			appendDirtyJavaScript("'),\""+returnWnd+"\");\n");
			appendDirtyJavaScript("self.close();\n\n");
		}

		appendDirtyJavaScript("function validateCatalogNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCatalogNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CATALOG_NO',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('LINEDESCRIPTION',i).value = '';\n");
		appendDirtyJavaScript("		getField_('STRING',i).value = '';\n");
		appendDirtyJavaScript("		getField_('AMOUNT',i).value = '';\n");
		appendDirtyJavaScript("		getField_('LIST_PRICE',i).value = '';\n");
		appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
		appendDirtyJavaScript("		getField_('AGREEMENT_PRICE_FLAG',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
		appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
		appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
		appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
		appendDirtyJavaScript("		+ '&WORK_ORDER_COST_TYPE=' + getField_('WORK_ORDER_COST_TYPE',i)\n");
		appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
		appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
		appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
		appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('LINEDESCRIPTION',i,0);\n");
		appendDirtyJavaScript("		assignValue_('STRING',i,1);\n");
		appendDirtyJavaScript("		assignValue_('AMOUNT',i,2);\n");
		appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,3);\n");
		appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,4);\n");
		appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,5);\n");
		appendDirtyJavaScript("		assignValue_('DISCOUNT',i,6);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	if (getValue_('AGREEMENT_PRICE_FLAG',i) == 1)\n");
		appendDirtyJavaScript("		f.AGREEMENT_PRICE_FLAG.click();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validateListPrice(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkListPrice(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('LIST_PRICE',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
		appendDirtyJavaScript("		getField_('AGREEMENT_PRICE_FLAG',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect('");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=LIST_PRICE'\n");
		appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
		appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
		appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'LIST_PRICE',i,'Sales Price') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,0);\n");
		appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,1);\n");
		appendDirtyJavaScript("        	f.AGREEMENT_PRICE_FLAG.checked = f.AGREEMENT_PRICE_FLAG.defaultChecked = false;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validatePriceListNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkPriceListNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('PRICE_LIST_NO',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('AGREEMENT_PRICE_FLAG',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect('");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
		appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
		appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
		appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
		appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
		appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
		appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
		appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
		appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
		appendDirtyJavaScript("		+ '&SALESPRICEAMOUNT=' + URLClientEncode(getValue_('SALESPRICEAMOUNT',i))\n");
		appendDirtyJavaScript("		+ '&WORK_ORDER_COST_TYPE=' + URLClientEncode(getValue_('WORK_ORDER_COST_TYPE',i))\n");
		appendDirtyJavaScript("		+ '&CLIENTVAL4=' + URLClientEncode(getValue_('CLIENTVAL4',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,0);\n");
		appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,1);\n");
		appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,2);\n");
		appendDirtyJavaScript("		assignValue_('DISCOUNT',i,3);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function disableAgreementPriceFlag()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if (f.AGREEMENT_PRICE_FLAG.checked == false)\n");
		appendDirtyJavaScript("		f.AGREEMENT_PRICE_FLAG.checked = f.AGREEMENT_PRICE_FLAG.defaultChecked = true;\n");
		appendDirtyJavaScript("	else\n");
		appendDirtyJavaScript("		f.AGREEMENT_PRICE_FLAG.checked = f.AGREEMENT_PRICE_FLAG.defaultChecked = false;\n");
		appendDirtyJavaScript("}\n");
	}
}

