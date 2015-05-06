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
*  File        : HistoricalSeparateCOInfo.java 
*  Modified    :
*    ARWILK   2001-02-19  - Created.
*    GACOLK   2002-12-04  - Set Max Length of MCH_CODE to 100
*    SAPRLK   2003-12-23  - Web Alignment - removed methods clone() and doReset().
*    VAGULK   040126      - Made the field order according to the Centura field order.
*    ARWILK   040219      - Edge Developments - (Remove uneccessary global variables, Enhance Performance)
*    SHAFLK   051125   Bug 54622, Added new fields.
*    NIJALK   051208   Merged bug 54622.
*    NEKOLK   060509   Bug 56600, Added contact and phone no flds.
*    AMNILK   060629   Merged with SP1 APP7.
*    BUNILK   070504   Implemented "MTIS907 New Service Contract - Services" changes.
*    AMDILK   070607   Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                      line description and invoice type
*    HARPLK   090708   Bug 84436, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class HistoricalSeparateCOInfo extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.HistoricalSeparateCOInfo");

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
   //Bug 84436, Start
   private ASPField f;
   //Bug 84436,End
   
	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public HistoricalSeparateCOInfo(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
			okFind();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");   

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		q.setOrderByClause("WO_NO");

		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATECOINFONODATA: No data found."));
			headset.clear();
		}
	}

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");      
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

                headblk.addField("WO_NO","Number","#").
                setSize(13).
                setLabel("PCMWHISTORICALSEPARATECOINFOHEADWONO: WO No").
                setReadOnly().
                setDynamicLOV("ACTIVE_WORK_ORDER",600,445).
                setMaxLength(8);

		headblk.addField("MCH_CODE").
		setSize(13).
		setMaxLength(100).
		setLabel("PCMWHISTORICALSEPARATECOINFOMCH_CODE: Object ID").
		setUpperCase().
		setReadOnly();

		headblk.addField("DESCRIPTION").
		setSize(28).
		setLabel("PCMWHISTORICALSEPARATECOINFODESCRIPTION: Description").
		setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("CONTRACT").
		setSize(5).
		setLabel("PCMWHISTORICALSEPARATECOINFOCONTRACT: Site").
		setUpperCase().
		setReadOnly();

		headblk.addField("WO_STATUS_ID").
		setHidden();

		headblk.addField("STATUS").
		setSize(5).
		setLabel("PCMWHISTORICALSEPARATECOINFOSTATE: Status").
		setFunction("Active_Separate_API.Finite_State_Decode__(WO_STATUS_ID)").
		setReadOnly();    

		//Bug 84436, Start
      f = headblk.addField("CUSTOMER_NO");
		f.setSize(11);
		f.setDynamicLOV("CUSTOMER_INFO",600,445);
		f.setLabel("PCMWHISTORICALSEPARATECOINFOCUSTOMER_NO: Customer No");      
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
      else
            f.setFunction("''");      
		f.setUpperCase();

		f = headblk.addField("CUSTOMERDESCRIPTION");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATECOINFOCUSTOMER_NO_DESC: Customer Description");
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("CUSTOMER_INFO_API.Get_Name(Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID))");
       else
            f.setFunction("''");
      //Bug 84436, End 
		mgr.getASPField("CUSTOMER_NO").setValidation("CUSTOMERDESCRIPTION");

                headblk.addField("CONTRACT_ID").                 
                setDynamicLOV("PSC_CONTR_PRODUCT_LOV").           
                setUpperCase().
                setMaxLength(15).
                setLabel("PCMWHISTORICALSEPARATECOINFOCONTRACTID: Contract ID").
                setSize(15);

      //Bug 84436, Start
      f = headblk.addField("CONTRACT_NAME");                    
		f.setDefaultNotVisible();      
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
       else
         f.setFunction("''");      
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATECOINFOCONTRACNAME: Contract Name");
		f.setSize(15);
      //Bug 84436, End 

                headblk.addField("LINE_NO","Number").
                setDynamicLOV("PSC_CONTR_PRODUCT_LOV").
                setLabel("PCMWHISTORICALSEPARATECOINFOLINENO: Line No").
                setSize(10); 

      //Bug 84436, Start
      f = headblk.addField("LINE_DESC");                     
		f.setDefaultNotVisible();
      if (mgr.isModuleInstalled("PCMSCI"))
         f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
       else
         f.setFunction("''"); 
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATECOINFOLINEDESC: Description");
		f.setSize(15);	
		
      f =headblk.addField("CONTRACT_TYPE");                     
		f.setDefaultNotVisible();     
      if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
       else
            f.setFunction("''");      
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATECOINFOCONTRTYPE: Contract Type");
		f.setSize(15);
      
	
		f = headblk.addField("INVOICE_TYPE");                   
		f.setDefaultNotVisible();
      if (mgr.isModuleInstalled("PCMSCI"))
         f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
      else
            f.setFunction("''");   
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATECOINFOINVTYPE: Invoice Type");
		f.setSize(15);
      //Bug 84436, End 

                headblk.addField("CUST_ORDER_TYPE").
		setSize(11).
		setDynamicLOV("CUST_ORDER_TYPE",600,445).
		setLabel("PCMWHISTORICALSEPARATECOINFOCUST_ORDER_TYPE: Cust Order Type");

		headblk.addField("CUSTORDERTYPEDESCRIPTION").
		setSize(20).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUSTOMER_ORD_DESC: Customer Order Type Description").
		setFunction("CUST_ORDER_TYPE_API.Get_Description(:CUST_ORDER_TYPE)");
		mgr.getASPField("CUST_ORDER_TYPE").setValidation("CUSTORDERTYPEDESCRIPTION");

		headblk.addField("COMPANY").
		setSize(6).
		setHidden().
		setUpperCase(); 

                headblk.addField("DUMMY").
                setSize(20).
                setReadOnly().
                setLabel("PCMWHISTORICALSEPARATECOINFODUMMY:           ").
                setFunction("''");
        
                headblk.addField("WARRANTY_ROW_NO").
                setHidden();
        
                headblk.addField("WARRANTYWO").
                setLabel("PCMWHISTORICALSEPARATECOINFOWARRANTYWO: Warranty Work Order").
                setFunction("HISTORICAL_SEPARATE_API.Has_Cust_Warr_Type(:WO_NO)").
                setReadOnly().
                setCheckBox("FALSE,TRUE");
        
                headblk.addField("REFERENCE_NO").
                setSize(25).
                setReadOnly().
                setLabel("PCMWHISTORICALSEPARATECOINFOEFERENCE_NO: Reference No"); 

		headblk.addField("CURRENCY_CODE").
		setSize(11).
		setDynamicLOV("CURRENCY_CODE","COMPANY",600,445).
		setLabel("PCMWHISTORICALSEPARATECOINFOCURRENCY_CODE: Currency");

		//Bug 84436, Start
      f = headblk.addField("AUTHORIZE_CODE");
		f.setSize(16);
		f.setReadOnly();      
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
       else
            f.setFunction("''");      
		f.setLabel("PCMWHISTORICALSEPARATECOINFOAUTHCODE: Coordinator");
      //Bug 84436, End 

		headblk.addField("CUST_ORDER_NO").
		setSize(16).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUST_ORDER_NO: Customer Order Reference"); 

		headblk.addField("DELIVERYDATE","Datetime").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFODELIVERYDATE: Planned Delivery Date").
		setFunction("Customer_Order_Line_API.Get_Planned_Delivery_Date(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)"); 

		headblk.addField("CUST_ORDER_LINE_NO").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUST_ORDER_LINE_NO: Line No");

		headblk.addField("CUST_ORDER_REL_NO").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUST_ORDER_REL_NO: Delivery No");

		headblk.addField("CUST_ORDER_LINE_ITEM_NO","Number").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUST_ORDER_LINE_ITEM_NO: Line Item No"); 

		headblk.addField("CUSTLINESALESPARTNO").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUSTLINESALESPARTNO: Sales Part No").
		setFunction("CUSTOMER_ORDER_LINE_API.GET_CATALOG_NO(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

		headblk.addField("CUSTLINESALESPARTDESC").
		setSize(20).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUSTLINESALESPARTNODESC: Sales Part Description").
		setFunction("CUSTOMER_ORDER_LINE_API.GET_CATALOG_DESC(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)"); 

		headblk.addField("CUSTLINEINVPART").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUSTLINEINVPART: Inventory Part").
		setFunction("CUSTOMER_ORDER_LINE_API.GET_PART_NO(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

		headblk.addField("CUSTLINEINVPARTDESC").
		setSize(20).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOCUSTLINEINVPARTDESC: Inventory Part Description").
		setFunction("CUSTOMER_ORDER_LINE_API.GET_PART_DESCRIPTION(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

		headblk.addField("BELONGSTOSITEAFTERDELIVERY").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOBELONGSTOSITEAFTERDELIVERY: Site").
		setFunction("CUSTOMER_ORDER_LINE_API.GET_SUP_SM_CONTRACT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

		headblk.addField("BELONGSTOOBJECTAFTERDELIVERY").
		setSize(11).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOBELONGSTOOBJECTAFTERDELIVERY: Object").
		setFunction("CUSTOMER_ORDER_LINE_API.GET_SUP_SM_OBJECT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");  

		headblk.addField("OBJECTAFTERDELIVERYDESC").
		setSize(20).
		setReadOnly().
		setLabel("PCMWHISTORICALSEPARATECOINFOOBJECTAFTERDELIVERYDESCRIPTION: Object Description").
		setDbName("NULL");   

                headblk.addField("LINE_STATUS").
                setSize(20).
                setReadOnly().
                setLabel("PCMWHISTORICALSEPARATECOINFOLINESTA: Line Status").
                setFunction("substr(Customer_Order_Line_API.Get_Objstate(CUST_ORDER_NO, CUST_ORDER_LINE_NO, CUST_ORDER_REL_NO, CUST_ORDER_LINE_ITEM_NO), 1, 35)");   
                
                headblk.addField("ORDER_STATUS").
                setSize(20).
                setReadOnly().
                setLabel("PCMWHISTORICALSEPARATECOINFOORDERSTA: Order Status").
                setFunction("substr(Customer_Order_API.Get_Objstate(CUST_ORDER_NO), 1, 35)");  
                
                headblk.addField("OBJ_CUST_WARRANTY").
                setHidden(); 
                 
                headblk.addField("CUST_WARRANTY").
                setHidden();  
                
                headblk.addField("CUST_WARR_TYPE").
                setSize(20).
                setLabel("PCMWHISTORICALSEPARATECOINFOCUSTWARRTYPE: Warranty Type").
                setReadOnly(). 
                setUpperCase(); 
                        
                headblk.addField("WARRANTY_DESCRIPTION").
                setSize(50).
                setReadOnly().
                setLabel("PCMWHISTORICALSEPARATECOINFOWARRANTYDESCRIPTION: Warranty Description").
                setFunction("Cust_Warranty_Type_API.Get_Warranty_Description(:CUST_WARRANTY,:CUST_WARR_TYPE)"); 
                
                headblk.addField("MUL_CSUT_EXIST").
                setLabel("PCMWHISTORICALSEPARATECOINFOMULCSUTEXIST: Multiple Customer Exist").
                setFunction("WORK_ORDER_CODING_UTILITY_API.Multiple_Customer_Exist(:WO_NO)").
                setReadOnly().
                setCheckBox("FALSE,TRUE"); 
                
                headblk.addField("PHONE_NO").
                setSize(25).
                setLabel("PCMWHISTORICALSEPARATEPHONENO: Phone No:").
                setMaxLength(20);

                headblk.addField("CONTACT").
                setSize(30).
                setLabel("PCMWHISTORICALSEPARATECONTACT: Contact:").
                setMaxLength(30);

		headblk.setView("HISTORICAL_SEPARATE");

		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWHISTORICALSEPARATECOINFOHD: CO Information"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.BACK);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
                headlay.setDialogColumns(2);
		headlay.defineGroup("", "WO_NO,CONTRACT,STATUS,MCH_CODE,DESCRIPTION", false, true);
                headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATECOINFOGRPLABEL0: Contact Information"),"CONTACT,PHONE_NO",true,true);
                headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATECOINFOGRPLABEL1: Order/Invoice Information"), "CUSTOMER_NO,CUSTOMERDESCRIPTION,AUTHORIZE_CODE,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,CUST_ORDER_TYPE,CUSTORDERTYPEDESCRIPTION,REFERENCE_NO,CURRENCY_CODE,CUST_WARR_TYPE,WARRANTY_DESCRIPTION,MUL_CSUT_EXIST,DUMMY,WARRANTYWO", true, true);
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATECOINFOGRPLABEL2: After Sales Information"), "CUST_ORDER_NO,DELIVERYDATE,CUST_ORDER_LINE_NO,CUST_ORDER_REL_NO,CUST_ORDER_LINE_ITEM_NO,CUSTLINESALESPARTNO,CUSTLINESALESPARTDESC,CUSTLINEINVPART,CUSTLINEINVPARTDESC,ORDER_STATUS,LINE_STATUS", true, true);
                headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATECOINFOGRPLABEL3: Belongs to After Delivery"), "BELONGSTOOBJECTAFTERDELIVERY,OBJECTAFTERDELIVERYDESC,BELONGSTOSITEAFTERDELIVERY", true, true);

		headlay.setSimple("CUSTOMERDESCRIPTION");
                headlay.setSimple("CUSTORDERTYPEDESCRIPTION");
		headlay.setSimple("CUSTLINESALESPARTDESC");
		headlay.setSimple("CUSTLINEINVPARTDESC");
		headlay.setSimple("OBJECTAFTERDELIVERYDESC");
                headlay.setSimple("WARRANTY_DESCRIPTION");
                headlay.setSimple("CONTRACT_NAME");
		headlay.setSimple("LINE_DESC");
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWHISTORICALSEPARATECOINFOTITLE: CO Information";
	}

	protected String getTitle()
	{
		return "PCMWHISTORICALSEPARATECOINFOTITLE: CO Information";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
			appendToHTML(headlay.show());
	}
}
