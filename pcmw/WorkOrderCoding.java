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
*  File        : WorkOrderCoding.java 
*  Created     : ARWILK  010228  Created.
*  Modified    :
*  ARWILK  010326  Checked if a given sales part is connected to a given role (in ROLE_SALES_PART) 
*                  and did the validation for cost.  
*  ARWILK  010329  Corrected the validation for wo_no (Cost and CostAmount).
*  ARWILK  010424  Corretced the validation function.
*  VAGULK  010713  Changed the OkFind()(added a where clause-77806) 
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  VAGULK  010919  Added the action "show previous records" to the single record mode of Employee(78197)
*  YAWILK  021129  Added MCHCODECONTRACT and modified MCHCODE max length to 100	
*  JEJALK  021218  Synchronizing with vimw (Added PLAN_LINE_NO,CRAFT_REF_NO, CRAFT_REF_NO and CRAFT_DESCRIPTION)
*  SHAFLK  030306  Added a new method isModuleInst() to check availability of 
*                  ORDER module inside preDefine method. 
*  SHAFLK  030306  Made all calls to apis in order module dynamically. 
*  SHAFLK  030310  Added method isModuleInst1() and called it from Validation.
*  JEJALK  030331  Renamed Craft Row No, Reference Number , Craft Description to operation No, Reference No, Description.
* --------------------------------------------------------------------------
*  SHAFLK  031219  Bug ID 41604 Changed method okFindITEM0().
*  SHAFLK  040120  Bug Id 41815 Removed Java dependencies.
*  SHAFLK  040130  Bug ID 42065 Changed Validations for WO_NO and ROLE_CODE.
*  SHAFLK  040310  Bug ID 43248 Changed Validations for plan line no and method preDefine().
*  SHAFLK  040315  Bug ID 43248 added function validatePlanLineNo1().
* ------------------------------- Edge - SP1 Merge -------------------------
*  ARWILK  031217  Edge Developments - (Removed clone and doReset Methods)
*  THWILK  040324  Merge with SP1.
*  THWILK  040407  Fixed the problem of getting numeric or value error and character 
*                  string buffer too small error.Modified method validate().
*  SAPRLK  040415  Web Alignment, changed conditional code in validate method.
*  ARWILK  040723  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning)
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  NIJALK  040826  Call 117295, Modified predefine().
*  ARWILK  041112  Replaced getContents with printContents.
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info
*  Chanlk  040105  Hide the field Maintenance Organization in itemblk0.
*  DiAmlk  050712  Bug ID:125442 - Modified the method validate.
*  NIJALK  050905  Set field AMOUNT Read Only.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  SHAFLK  060529  Bug 58197, Added lov for Site and corrected lov for Maint. Org.
*  AMNILK  060629  Merged with SP1 APP7.
*  AMDILK  060814  Bug 58216, Eliminated SQL errors in web applications. Modified methods validate(),
*                  searchItem(), okFindITEM0(), prevReg(), okFdITEM0(), countFindITEM0()
*  AMDILK  060904  Merged with the Bug Id 58216
*  NIJALK  070717  Bug 66683, Modified searchItem(), okFindITEM0(), prevReg(), okFdITEM0(), countFindITEM0(), getTitle(), newRowITEM0(), preDefine().
*  NIJALK  070725  Bug 66683, Modified okFind(),countFind(),searchItem(), okFindITEM0(), prevReg(), okFdITEM0(), countFindITEM0().
*  ILSOLK  070807  Merged Bug Id 66683.
*  SHAFLK  071203  Bug 69743, Modified preDefine().
*  SHAFLK  080821  Bug 76231, Modified valiation of craft and added lovRoleCode().
*  ILSOLK  080829  Bug 76211, Modified validate(), preDefine().
*  SHAFLK  080922  Bug 77214, Modified preDefine() and Validate().
*  SUIJLK  090922  Bug 81023, Modified preDefine(). Added new method saveReturnITEM0(), saveNewITEM0(), deleteITEM0()
*  NIJALK  100719  Bug 89399, Modified preDefine(), printContents().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkOrderCoding extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderCoding");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock dummyblk1;
	private ASPRowSet dummyset1;

	private ASPBlock itemblk0;
	private ASPRowSet itemset0;
	private ASPCommandBar itembar0;
	private ASPTable itemtbl0;
	private ASPBlockLayout itemlay0;

	private ASPField f;
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String dateMask;
	private String companyvar;
	private String sDefContract;
	private String title_;
	private String xx;
	private String yy;
	private String sCostType;
	private String sAccountType; 
	private boolean[] isSecure;


	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderCoding(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		sCostType =  "" ;
		sAccountType = "";
		sAccountType = "" ;
		isSecure =  new boolean[7] ;

		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		sCostType = ctx.readValue("CTXCOST",sCostType);
		sAccountType = ctx.readValue("CTXACCOUNT",sAccountType);
		dateMask =  ctx.readValue("DATEMASK","");
		companyvar =  ctx.readValue("COMPANYVAR",""); 

		dateMask = mgr.getFormatMask("Datetime",true);

		if (mgr.commandBarActivated())
		{

			eval(mgr.commandBarFunction());
		}
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else
			startup();


		adjust();

		ctx.writeValue("CTXCOST",sCostType);
		ctx.writeValue("CTXACCOUNT",sAccountType);
		ctx.writeValue("DATEMASK",dateMask);
		ctx.writeValue("COMPANYVAR",companyvar);

	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  

	public boolean  checksec(String method,int ref) 
	{
		String[] splitted;
		ASPTransactionBuffer secBuff;
		ASPManager mgr = getASPManager();

		isSecure[ref] = false ; 
		splitted = split(method,"."); 

		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery(splitted[0],splitted[1]);

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists(method))
		{
			isSecure[ref] = true;
			return true; 
		}
		else
			return false;
	}


	public void startup()
	{
		ASPCommand cmd; 
		ASPQuery q; 
		ASPManager mgr = getASPManager();

		q = trans.addQuery(dummyblk1);
		q.includeMeta("ALL");
		mgr.submit(trans); 
		trans.clear();
		eval(dummyblk1.generateAssignments());

		sCostType = dummyset1.getRow().getValue("DEF_COST_TYPE");
		sAccountType = dummyset1.getRow().getValue("DEF_ACCOUNT_TYPE");

		cmd = trans.addCustomFunction("DEFCO","User_Default_API.Get_Contract","DEFCONTRACT");

		cmd = trans.addCustomFunction("COMPA","Site_API.Get_Company","COMPANY");
		cmd.addReference("DEFCONTRACT", "DEFCO/DATA");

		cmd = trans.addCustomCommand("WORK_ORDER_COST_TYPE","Work_Order_Cost_Type_API.Enumerate");
		cmd.addParameter("CLIENT_VALUES0");

		cmd = trans.addCustomCommand("WORK_ORDER_ACCOUNT_TYPE","Work_Order_Account_Type_API.Enumerate");
		cmd.addParameter("CLIENT_VALUES1");

		trans = mgr.submit(trans);

		sDefContract = trans.getValue("DEFCO/DATA/DEFCONTRACT");
		companyvar = trans.getValue("COMPA/DATA/COMPANY");   
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPCommand cmd;

		int ref;
		String txt;
		String val;
		String itemCompany;
		String sContract;
		String sRoleCode;
		String sCostDesc;
		String sSalesPart;
		String sSalesPartDesc;
		String sCatalogDefaultSite;
		String errorDescription;
		String sMchCode;
		String sMchName;
		String sMchCodeCon;
		String sComment;
		String sCmnt0;
		String sCmnt1;
		String sCmnt2;               
		double sSalesCost0 = 0;
		double sSalesCost1 = 0;
		double sSalesCost2 = 0;                
		double nCostAmount = 0;
		double nCost = 0;
		double nListPrice = 0;
		double nListPrice1 = 0;              
		double nRoleCost0 = 0;
		double nRoleCost1 = 0;
		double nRoleCost2 = 0;
		double nFinalRoleCost = 0; 
		double nSalesPartCost = 0;
		double nDiscount = 0;
		double nSalesPriceAmount = 0;
		String craftRowNo;
		String referenceNo;
		String craftDesc;

		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("WO_NO".equals(val))
		{
			nCost = 0.0;
			nListPrice = 0.0;
			nCostAmount = 0.0;

			//Bug 76211, Start
			sRoleCode = mgr.readValue("ROLE_CODE","");

			trans.clear();
			//Bug 76211, End

			cmd = trans.addCustomFunction("ACTWOCONT","Active_Work_Order_API.Get_Contract","CONTRACT");
			cmd.addParameter("WO_NO");

			cmd = trans.addCustomFunction("ITCOMPA","Site_API.Get_Company","ITEM0_COMPANY");
			cmd.addReference("CONTRACT","ACTWOCONT/DATA");

			//Bug 76211, Start
			if (mgr.isEmpty(sRoleCode))
			{
			    cmd = trans.addCustomFunction("ROLCOD","Employee_Role_API.Get_Default_Role","ROLE_CODE");
			    cmd.addReference("ITEM0_COMPANY","ITCOMPA/DATA");
			    cmd.addParameter("ITEM0_EMP_NO"); 

			    cmd = trans.addCustomFunction("COLCMNT","Role_API.Get_Description","CMNT");
			    cmd.addReference("ROLE_CODE","ROLCOD/DATA"); 

			    cmd = trans.addCustomFunction("CATLONO1","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
			    cmd.addReference("ROLE_CODE","ROLCOD/DATA");
			    cmd.addReference("CONTRACT","ACTWOCONT/DATA");

			    cmd = trans.addCustomFunction("ROLECOST1","Role_API.Get_Role_Cost","SALESPARTCOST");
			    cmd.addReference("ROLE_CODE","ROLCOD/DATA");  

			    cmd = trans.addCustomFunction("ROLECOST2","Organization_API.Get_Org_Cost","SALESPARTCOST");
			    cmd.addReference("CONTRACT","ACTWOCONT/DATA");
			    cmd.addReference("ORG_CODE","ROLCOD/DATA"); 
			}
			else
			{
			    cmd = trans.addCustomFunction("COLCMNT","Role_API.Get_Description","CMNT");
			    cmd.addParameter("ROLE_CODE",sRoleCode); 

			    cmd = trans.addCustomFunction("CATLONO1","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
			    cmd.addParameter("ROLE_CODE",sRoleCode); 
			    cmd.addReference("CONTRACT","ACTWOCONT/DATA");

			    cmd = trans.addCustomFunction("ROLECOST1","Role_API.Get_Role_Cost","SALESPARTCOST");
			    cmd.addParameter("ROLE_CODE",sRoleCode); 

			    cmd = trans.addCustomFunction("ROLECOST2","Organization_API.Get_Org_Cost","SALESPARTCOST");
			    cmd.addReference("CONTRACT","ACTWOCONT/DATA");
			    cmd.addParameter("ROLE_CODE",sRoleCode); 
			}
			//Bug 76211, End

			cmd = trans.addCustomFunction("CATLONO2","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
			cmd.addReference("CONTRACT","ACTWOCONT/DATA");
			cmd.addParameter("ORG_CODE");

			cmd = trans.addCustomFunction("ERDESC","WORK_ORDER_API.Get_Err_Descr","ERRDESCR");
			cmd.addParameter("WO_NO");          

			cmd = trans.addCustomFunction("MCHCO","SEPARATE_WORK_ORDER_API.Get_Mch_Code","MCHCODE");
			cmd.addParameter("WO_NO");

			cmd = trans.addCustomFunction("MCHNA","SEPARATE_WORK_ORDER_API.Get_Obj_Description","MCHNAME");
			cmd.addParameter("WO_NO");

			cmd = trans.addCustomFunction("MCHCON","SEPARATE_WORK_ORDER_API.Get_contract","MCHCODECONTRACT");
			cmd.addParameter("WO_NO");

			trans = mgr.validate(trans);

			sContract = trans.getValue("ACTWOCONT/DATA/CONTRACT");
			//Bug 76211, Start
			if (mgr.isEmpty(sRoleCode))
			{
			    sRoleCode = trans.getValue("ROLCOD/DATA/ROLE_CODE");
			}
			//Bug 76211, End
			sCostDesc = trans.getValue("COLCMNT/DATA/CMNT");

			String sSalesPart1 = trans.getValue("CATLONO1/DATA/CATALOG_NO");
			String sSalesPart2 = trans.getValue("CATLONO2/DATA/CATALOG_NO");
			if (mgr.isEmpty(sSalesPart1))
			{
				sSalesPart = sSalesPart2;
			}
			else
			{
				sSalesPart = sSalesPart1;
			}

			nRoleCost0 = trans.getNumberValue("ROLECOST1/DATA/SALESPARTCOST");
			nRoleCost1 = trans.getNumberValue("ROLECOST2/DATA/SALESPARTCOST");
			errorDescription = trans.getValue("ERDESC/DATA/ERRDESCR");   
			sMchCode = trans.getValue("MCHCO/DATA/MCHCODE");
			sMchName = trans.getValue("MCHNA/DATA/MCHNAME");
			sMchCodeCon = trans.getValue("MCHCON/DATA/MCHCODECONTRACT");

			trans.clear();

			if (isModuleInst1("ORDER"))
			{
				cmd = trans.addCustomFunction("ROLECOST3","Sales_Part_API.Get_Cost","SALESPARTCOST");
				cmd.addParameter("CONTRACT",sContract); 
				cmd.addReference("CATALOG_NO","CATLONO/DATA");
			}


			trans = mgr.validate(trans);

			if (isModuleInst1("ORDER"))
				nRoleCost2 = trans.getNumberValue("ROLECOST3/DATA/SALESPARTCOST");
			else
				nRoleCost2 = 0;

			trans.clear();

			ASPQuery q = trans.addQuery("CHECKCATROL","select CONTRACT from ROLE_SALES_PART where ROLE_CODE = ? and CATALOG_NO = ?");
			q.addParameter("ROLE_CODE", sRoleCode);
			q.addParameter("CATALOG_NO", sSalesPart);
			q.includeMeta("ALL");  

			if (isModuleInst1("ORDER") && checksec("Sales_Part_API.Get_Catalog_Desc",1))
			{

				if (!mgr.isEmpty(sSalesPart1))
				{

					cmd = trans.addCustomFunction("SALPATCATLOGDES","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
					cmd.addReference("CONTRACT","CHECKCATROL/DATA"); 
					cmd.addParameter("CATALOG_NO",sSalesPart);
				}
				else
				{
					cmd = trans.addCustomFunction("SALPATCATLOGDES","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
					cmd.addParameter("CONTRACT",sContract);
					cmd.addParameter("CATALOG_NO",sSalesPart);
				}

			}

			if (isModuleInst1("ORDER") && checksec("Sales_Part_API.Get_List_Price",2))
			{
				cmd = trans.addCustomFunction("LISTPRI","Sales_Part_API.Get_List_Price","LIST_PRICE");
				cmd.addReference("CONTRACT","CHECKCATROL/DATA");          
				cmd.addParameter("CATALOG_NO",sSalesPart);
			}

			if (isModuleInst1("ORDER") && (checksec("Company_Finance_API.Get_Currency_Code",3))&&(checksec("Customer_Order_Pricing_API.Get_Valid_Price_List",4)))
			{
				cmd = trans.addCustomFunction("CUSTNO","Active_Separate_API.Get_Customer_No","CUSTOMER_ID");
				cmd.addParameter("WO_NO");  

				cmd = trans.addCustomFunction("CUSTAGRRENO","Active_Separate_API.Get_Agreement_Id","AGREEMENT_ID");
				cmd.addParameter("WO_NO"); 

				cmd = trans.addCustomCommand("PRINFOGET","Work_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE"); 
				cmd.addParameter("UNIT_PRICE"); 
				cmd.addParameter("DEFDISCOUNT"); 
				cmd.addParameter("CURRENCY_RATE"); 
				cmd.addParameter("CONTRACT",sContract); 
				cmd.addParameter("CATALOG_NO",sSalesPart); 
				cmd.addReference("CUSTOMER_ID","CUSTNO/DATA"); 
				cmd.addReference("AGREEMENT_ID","CUSTAGRRENO/DATA"); 
				cmd.addParameter("PRICE_LIST_NO","''"); 
				cmd.addParameter("QTY");                      
			}
			else
			{
				cmd = trans.addCustomCommand("PRINFOGET","Work_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE"); 
				cmd.addParameter("UNIT_PRICE"); 
				cmd.addParameter("DEFDISCOUNT"); 
				cmd.addParameter("CURRENCY_RATE"); 
				cmd.addParameter("CONTRACT",sContract); 
				cmd.addReference("CATALOG_NO",sSalesPart); 
				cmd.addParameter("CUSTOMER_ID","''"); 
				cmd.addParameter("AGREEMENT_ID","''"); 
				cmd.addParameter("PRICE_LIST_NO","''"); 
				cmd.addParameter("QTY");                      
			}                        

			trans = mgr.validate(trans);

			nDiscount  = trans.getNumberValue("PRINFOGET/DATA/DEFDISCOUNT");


			sComment = mgr.readValue("NAME")+","+mgr.readValue("ORG_CODE");                     
			nFinalRoleCost = 0.0;

			ref = 0;      

			if (isModuleInst1("ORDER") && isSecure[ref += 1])
				sSalesPartDesc = (mgr.isEmpty(trans.getValue("SALPATCATLOGDES/DATA/SALESPARTCATALOGDESC"))?"":trans.getValue("SALPATCATLOGDES/DATA/SALESPARTCATALOGDESC"));
			else
				sSalesPartDesc	= ""; 

			if (isModuleInst1("ORDER") && isSecure[ref += 1])
				nSalesPartCost = trans.getNumberValue("LISTPRI/DATA/LIST_PRICE");
			else
				nSalesPartCost	= 0.0;                 


			if (!mgr.isEmpty(sRoleCode))
				nFinalRoleCost =  nRoleCost0;



			if ((nFinalRoleCost == 0)&&(!mgr.isEmpty(mgr.readValue("ORG_CODE"))))
				nFinalRoleCost =  nRoleCost1;


			if ((nFinalRoleCost == 0)&&(!mgr.isEmpty(sSalesPart)))
				nFinalRoleCost =  nRoleCost2;


			if ((!mgr.isEmpty(sRoleCode))||(!mgr.isEmpty(sSalesPart)))
				sComment += "("+sCostDesc+")"+sSalesPartDesc;


			if (nFinalRoleCost !=0)
			{
				if (toInt(mgr.readNumberValue("QTY")) != 0)
				{
					nCostAmount = nFinalRoleCost*toInt(mgr.readNumberValue("QTY"));
					nSalesPriceAmount = nSalesPartCost*toInt(mgr.readValue("QTY"));
				}
				else
				{
					nCostAmount = nFinalRoleCost*1;
					nSalesPriceAmount = nSalesPartCost*1;
				}
			}

			txt =     
                                  (mgr.isEmpty(sMchCode) ? "" : (sMchCode))+ "^" 
				  + (mgr.isEmpty(sMchName) ? "" : (sMchName))+ "^"
				  + (mgr.isEmpty(sMchCodeCon) ? "" : (sMchCodeCon))+ "^"
				  + (mgr.isEmpty(sRoleCode) ? "" : (sRoleCode))+ "^"
				  + (mgr.isEmpty(sSalesPart) ? "" : (sSalesPart))+ "^"
				  + (mgr.isEmpty(sSalesPartDesc) ? "" : (sSalesPartDesc))+ "^"
				  + (mgr.isEmpty(errorDescription) ? "" : (errorDescription))+ "^"
				  + (Double.isNaN(nDiscount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nDiscount)) + "^" 
				  + (Double.isNaN(nFinalRoleCost) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nFinalRoleCost)) + "^" 
				  + (Double.isNaN(nSalesPartCost) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nSalesPartCost)) + "^" 
				  + (Double.isNaN(nCostAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nCostAmount)) + "^" 
				  + (Double.isNaN(nSalesPriceAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nSalesPriceAmount)) + "^" 
				  + (mgr.isEmpty(sComment) ? "" : (sComment))+ "^";


			mgr.responseWrite(txt);
		}
		else if ("ROLE_CODE".equals(val))
		{                   
                        String reqstr = null;
                        int startpos = 0;
                        int endpos = 0;
                        int i = 0;
                        String ar[] = new String[6];
            
                        String sOrgContract = "";
                        String new_role_code = mgr.readValue("ROLE_CODE","");
                        
                        if (new_role_code.indexOf("^",0)>0)
                        {
                                for (i=0 ; i<5; i++)
                                {
                                    endpos = new_role_code.indexOf("^",startpos);
                                    reqstr = new_role_code.substring(startpos,endpos);
                                    ar[i] = reqstr;
                                    startpos= endpos+1;
                                }
                                
                                sRoleCode = ar[2];
                                sOrgContract = ar[4];
                        }
                        else
                        {
                            sRoleCode = mgr.readValue("ROLE_CODE");
                            sOrgContract = mgr.readValue("CONTRACT"); 
                        }

			cmd = trans.addCustomFunction("ITCOMPA","Site_API.Get_Company","ITEM0_COMPANY");
			cmd.addParameter("CONTRACT");

			cmd = trans.addCustomFunction("COLCMNT","Role_API.Get_Description","CMNT");
			cmd.addParameter("ROLE_CODE",sRoleCode);   

			cmd = trans.addCustomFunction("CATLONO1","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
			cmd.addParameter("ROLE_CODE",sRoleCode);
                        cmd.addParameter("CONTRACT",sOrgContract);

			cmd = trans.addCustomFunction("CATLONO2","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
			cmd.addParameter("CONTRACT");
			cmd.addParameter("ORG_CODE"); 

			cmd = trans.addCustomFunction("ROLECOST1","Role_API.Get_Role_Cost","SALESPARTCOST");
			cmd.addParameter("ROLE_CODE",sRoleCode);  

			cmd = trans.addCustomFunction("ROLECOST2","Organization_API.Get_Org_Cost","SALESPARTCOST");
			cmd.addParameter("CONTRACT");
			cmd.addParameter("ORG_CODE"); 

			trans = mgr.validate(trans);

			sCostDesc = trans.getValue("COLCMNT/DATA/CMNT");

			String sSalesPart1 = trans.getValue("CATLONO1/DATA/CATALOG_NO");
			String sSalesPart2 = trans.getValue("CATLONO2/DATA/CATALOG_NO");

			if (mgr.isEmpty(sSalesPart1))
			{
				sSalesPart = sSalesPart2;
			}
			else
			{
				sSalesPart = sSalesPart1;
			}

			nRoleCost0 = trans.getNumberValue("ROLECOST1/DATA/SALESPARTCOST");
			nRoleCost1 = trans.getNumberValue("ROLECOST2/DATA/SALESPARTCOST");

			trans.clear();

			if (isModuleInst1("ORDER"))
			{
				cmd = trans.addCustomFunction("ROLECOST3","Sales_Part_API.Get_Cost","SALESPARTCOST");
				cmd.addParameter("CONTRACT"); 
				cmd.addReference("CATALOG_NO",sSalesPart);
			}

			cmd = trans.addCustomCommand("PRINFOGET","Work_Order_Coding_API.Get_Price_Info");
			cmd.addParameter("BASE_PRICE"); 
			cmd.addParameter("UNIT_PRICE"); 
			cmd.addParameter("DEFDISCOUNT"); 
			cmd.addParameter("CURRENCY_RATE"); 
			cmd.addParameter("CONTRACT"); 
			cmd.addParameter("CATALOG_NO",sSalesPart); 
			cmd.addParameter("CUSTOMER_ID"); 
			cmd.addParameter("AGREEMENT_ID"); 
			cmd.addParameter("PRICE_LIST_NO","''"); 
			cmd.addParameter("QTY"); 

			trans = mgr.validate(trans);
			if (isModuleInst1("ORDER"))
				nRoleCost2 = trans.getNumberValue("ROLECOST3/DATA/SALESPARTCOST");
			else
				nRoleCost2 = 0;                                                     
			nDiscount  = trans.getNumberValue("PRINFOGET/DATA/DEFDISCOUNT");

			trans.clear();

			ASPQuery q = trans.addQuery("CHECKCATROL","select CONTRACT from ROLE_SALES_PART where ROLE_CODE = ? and CATALOG_NO = ?");
			q.addParameter("ROLE_CODE", mgr.readValue("ROLE_CODE"));
			q.addParameter("CATALOG_NO", sSalesPart);
			q.includeMeta("ALL");  

			if (isModuleInst1("ORDER") && checksec("Sales_Part_API.Get_Catalog_Desc",1))
			{
				if (!mgr.isEmpty(sSalesPart1))
				{

					cmd = trans.addCustomFunction("SALPATCATLOGDES","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
					cmd.addReference("CONTRACT","CHECKCATROL/DATA"); 
					cmd.addParameter("CATALOG_NO",sSalesPart);
				}
				else
				{
					cmd = trans.addCustomFunction("SALPATCATLOGDES","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
					cmd.addParameter("CONTRACT"); 
					cmd.addParameter("CATALOG_NO",sSalesPart);
				}

			}

			if (isModuleInst1("ORDER") && checksec("Sales_Part_API.Get_List_Price",2))
			{

				cmd = trans.addCustomFunction("LISTPRI","Sales_Part_API.Get_List_Price","LIST_PRICE");
				cmd.addReference("CONTRACT","CHECKCATROL/DATA");          
				cmd.addParameter("CATALOG_NO",sSalesPart);
			}

			trans = mgr.validate(trans);

			sComment = mgr.readValue("NAME")+","+mgr.readValue("ORG_CODE");                     
			nFinalRoleCost = 0.0;

			ref = 0;      

			if (isModuleInst1("ORDER") && isSecure[ref += 1])
				sSalesPartDesc = (mgr.isEmpty(trans.getValue("SALPATCATLOGDES/DATA/SALESPARTCATALOGDESC"))?"":trans.getValue("SALPATCATLOGDES/DATA/SALESPARTCATALOGDESC"));
			else
				sSalesPartDesc	= ""; 

			if (isModuleInst1("ORDER") && isSecure[ref += 1])
				nSalesPartCost = trans.getNumberValue("LISTPRI/DATA/LIST_PRICE");
			else
				nSalesPartCost	= 0.0;                 

			if (!mgr.isEmpty(sRoleCode))
				nFinalRoleCost =  nRoleCost0;

			if ((nFinalRoleCost == 0)&&(!mgr.isEmpty(mgr.readValue("ORG_CODE"))))
				nFinalRoleCost =  nRoleCost1;

			if ((nFinalRoleCost == 0)&&(!mgr.isEmpty(sSalesPart)))
				nFinalRoleCost =  nRoleCost2;


			sCmnt0 = sComment + "()"+sSalesPartDesc;
			sCmnt1 = sComment + "("+sCostDesc+")"; 
			sCmnt2 = sComment + "()"+sSalesPartDesc; 

			if ((!mgr.isEmpty(sRoleCode))||(!mgr.isEmpty(sSalesPart)))
				sComment += "("+sCostDesc+")"+sSalesPartDesc;


			if (nFinalRoleCost !=0)
			{
				if (toInt(mgr.readNumberValue("QTY")) != 0)
				{
					nCostAmount = nFinalRoleCost*toInt(mgr.readNumberValue("QTY"));
					nSalesPriceAmount = nSalesPartCost*toInt(mgr.readValue("QTY"));
				}
				else
				{
					nCostAmount = nFinalRoleCost*1;
					nSalesPriceAmount = nSalesPartCost*1;
				}
			}

			txt = (   mgr.isEmpty(sRoleCode) ? "" : (sRoleCode))+ "^"
                                  + (mgr.isEmpty(sSalesPart) ? "" : (sSalesPart))+ "^"
				  + (mgr.isEmpty(sSalesPartDesc) ? "" : (sSalesPartDesc))+ "^"
				  + (Double.isNaN(nDiscount) ? mgr.getASPField("DEFDISCOUNT").formatNumber(0.0): mgr.getASPField("DEFDISCOUNT").formatNumber(nDiscount)) + "^" 
				  + (Double.isNaN(nFinalRoleCost) ? mgr.getASPField("SALESPARTCOST").formatNumber(0.0): mgr.getASPField("SALESPARTCOST").formatNumber(nFinalRoleCost)) + "^" 
				  + (Double.isNaN(nSalesPartCost) ? mgr.getASPField("LIST_PRICE").formatNumber(0.0): mgr.getASPField("LIST_PRICE").formatNumber(nSalesPartCost)) + "^" 
				  + (Double.isNaN(nCostAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nCostAmount)) + "^" 
				  + (Double.isNaN(nSalesPriceAmount) ? mgr.getASPField("SALES_AMOUNT").formatNumber(0.0): mgr.getASPField("SALES_AMOUNT").formatNumber(nSalesPriceAmount)) + "^" 
				  + (mgr.isEmpty(sComment) ? "" : (sComment))+ "^"
				  + (Double.isNaN(nRoleCost0) ? mgr.getASPField("SALESCOST0").formatNumber(0.0): mgr.getASPField("SALESCOST0").formatNumber(nRoleCost0)) + "^"
				  + (Double.isNaN(nRoleCost1) ? mgr.getASPField("SALESCOST1").formatNumber(0.0): mgr.getASPField("SALESCOST1").formatNumber(nRoleCost1)) + "^"
				  + (Double.isNaN(nRoleCost2) ? mgr.getASPField("SALESCOST2").formatNumber(0.0): mgr.getASPField("SALESCOST2").formatNumber(nRoleCost2)) + "^"
				  + (mgr.isEmpty(sCmnt0) ? "" : (sCmnt0))+ "^"
				  + (mgr.isEmpty(sCmnt1) ? "" : (sCmnt1))+ "^"
				  + (mgr.isEmpty(sCmnt2) ? "" : (sCmnt2))+ "^";

			mgr.responseWrite(txt);   
		}
		else if ("CATALOG_NO".equals(val))
		{
			cmd = trans.addCustomFunction("COLCMNT","Role_API.Get_Description","CMNT");
			cmd.addParameter("ROLE_CODE");   

			cmd = trans.addCustomFunction("ROLECOST1","Role_API.Get_Role_Cost","SALESPARTCOST");
			cmd.addParameter("ROLE_CODE");  

			cmd = trans.addCustomFunction("ROLECOST2","Organization_API.Get_Org_Cost","SALESPARTCOST");
			cmd.addParameter("CONTRACT"); 
			cmd.addParameter("ORG_CODE"); 

			if (isModuleInst1("ORDER"))
			{
				cmd = trans.addCustomFunction("ROLECOST3","Sales_Part_API.Get_Cost","SALESPARTCOST");
				cmd.addParameter("CONTRACT"); 
				cmd.addParameter("CATALOG_NO");
			}

			cmd = trans.addCustomCommand("PRINFOGET","Work_Order_Coding_API.Get_Price_Info");
			cmd.addParameter("BASE_PRICE"); 
			cmd.addParameter("UNIT_PRICE"); 
			cmd.addParameter("DEFDISCOUNT"); 
			cmd.addParameter("CURRENCY_RATE"); 
			cmd.addParameter("CONTRACT"); 
			cmd.addParameter("CATALOG_NO"); 
			cmd.addParameter("CUSTOMER_ID"); 
			cmd.addParameter("AGREEMENT_ID"); 
			cmd.addParameter("PRICE_LIST_NO","''"); 
			cmd.addParameter("QTY"); 

			trans = mgr.validate(trans);

			sCostDesc = trans.getValue("COLCMNT/DATA/CMNT"); 
			nRoleCost0 = trans.getNumberValue("ROLECOST1/DATA/SALESPARTCOST");
			nRoleCost1 = trans.getNumberValue("ROLECOST2/DATA/SALESPARTCOST");
			if (isModuleInst1("ORDER"))
				nRoleCost2 = trans.getNumberValue("ROLECOST3/DATA/SALESPARTCOST");
			else
				nRoleCost2 = 0;                                
			nDiscount  = trans.getNumberValue("PRINFOGET/DATA/DEFDISCOUNT");

			trans.clear();

			if (isModuleInst1("ORDER") && checksec("Sales_Part_API.Get_Catalog_Desc",1))
			{
				cmd = trans.addCustomFunction("SALPATCATLOGDES","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
				cmd.addParameter("CONTRACT"); 
				cmd.addParameter("CATALOG_NO");
			}

			if (isModuleInst1("ORDER") && checksec("Sales_Part_API.Get_List_Price",2))
			{
				cmd = trans.addCustomFunction("LISTPRI","Sales_Part_API.Get_List_Price","LIST_PRICE");
				cmd.addParameter("CONTRACT");          
				cmd.addParameter("CATALOG_NO");
			}

			trans = mgr.validate(trans);


			sComment = mgr.readValue("NAME")+","+mgr.readValue("ORG_CODE");                     
			nFinalRoleCost = 0.0;

			sRoleCode = mgr.readValue("ROLE_CODE");
			sSalesPart = mgr.readValue("CATALOG_NO");

			ref = 0;   

			if (isModuleInst1("ORDER") && isSecure[ref += 1])
				sSalesPartDesc = (mgr.isEmpty(trans.getValue("SALPATCATLOGDES/DATA/SALESPARTCATALOGDESC"))?"":trans.getValue("SALPATCATLOGDES/DATA/SALESPARTCATALOGDESC"));
			else
				sSalesPartDesc	= ""; 

			if (isModuleInst1("ORDER") && isSecure[ref += 1])
				nSalesPartCost = trans.getNumberValue("LISTPRI/DATA/LIST_PRICE");
			else
				nSalesPartCost	= 0.0;                 

			if (!mgr.isEmpty(sRoleCode))
				nFinalRoleCost =  nRoleCost0;

			if ((nFinalRoleCost == 0)&&(!mgr.isEmpty(mgr.readValue("ORG_CODE"))))
				nFinalRoleCost =  nRoleCost1;


			if ((nFinalRoleCost == 0)&&(!mgr.isEmpty(sSalesPart)))
				nFinalRoleCost =  nRoleCost2;

			sCmnt0 = sComment + "()"+sSalesPartDesc;
			sCmnt1 = sComment + "("+sCostDesc+")"; 
			sCmnt2 = sComment + "()"+sSalesPartDesc;   


			if ((!mgr.isEmpty(sRoleCode))||(!mgr.isEmpty(sSalesPart)))
				sComment += "("+sCostDesc+")"+sSalesPartDesc;



			if (nFinalRoleCost !=0)
			{
				if (toInt(mgr.readNumberValue("QTY")) != 0)
				{
					nCostAmount = nFinalRoleCost*toInt(mgr.readNumberValue("QTY"));
					nSalesPriceAmount = nSalesPartCost*toInt(mgr.readValue("QTY"));
				}
				else
				{
					nCostAmount = nFinalRoleCost*1;
					nSalesPriceAmount = nSalesPartCost*1;
				}
			}

			txt =  (mgr.isEmpty(sSalesPartDesc) ? "" : (sSalesPartDesc))+ "^"
				   + (Double.isNaN(nDiscount) ? mgr.getASPField("DEFDISCOUNT").formatNumber(0.0): mgr.getASPField("DEFDISCOUNT").formatNumber(nDiscount)) + "^" 
				   + (Double.isNaN(nFinalRoleCost) ? mgr.getASPField("SALESPARTCOST").formatNumber(0.0): mgr.getASPField("SALESPARTCOST").formatNumber(nFinalRoleCost)) + "^" 
				   + (Double.isNaN(nSalesPartCost) ? mgr.getASPField("LIST_PRICE").formatNumber(0.0): mgr.getASPField("LIST_PRICE").formatNumber(nSalesPartCost)) + "^" 
				   + (Double.isNaN(nCostAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nCostAmount)) + "^" 
				   + (Double.isNaN(nSalesPriceAmount) ? mgr.getASPField("SALES_AMOUNT").formatNumber(0.0): mgr.getASPField("SALES_AMOUNT").formatNumber(nSalesPriceAmount)) + "^" 
				   + (mgr.isEmpty(sComment) ? "" : (sComment))+ "^"
				   + (Double.isNaN(nRoleCost0) ? mgr.getASPField("SALESCOST0").formatNumber(0.0): mgr.getASPField("SALESCOST0").formatNumber(nRoleCost0)) + "^"
				   + (Double.isNaN(nRoleCost1) ? mgr.getASPField("SALESCOST1").formatNumber(0.0): mgr.getASPField("SALESCOST1").formatNumber(nRoleCost1)) + "^"
				   + (Double.isNaN(nRoleCost2) ? mgr.getASPField("SALESCOST2").formatNumber(0.0): mgr.getASPField("SALESCOST2").formatNumber(nRoleCost2)) + "^"
				   + (mgr.isEmpty(sCmnt0) ? "" : (sCmnt0))+ "^"
				   + (mgr.isEmpty(sCmnt1) ? "" : (sCmnt1))+ "^"
				   + (mgr.isEmpty(sCmnt2) ? "" : (sCmnt2))+ "^";


			mgr.responseWrite(txt);


		}

		else if ("QTY".equals(val))
		{

			cmd = trans.addCustomCommand("PRINFOGET","Work_Order_Coding_API.Get_Price_Info");
			cmd.addParameter("BASE_PRICE"); 
			cmd.addParameter("UNIT_PRICE"); 
			cmd.addParameter("DEFDISCOUNT"); 
			cmd.addParameter("CURRENCY_RATE"); 
			cmd.addParameter("CONTRACT"); 
			cmd.addParameter("CATALOG_NO"); 
			cmd.addParameter("CUSTOMER_ID"); 
			cmd.addParameter("AGREEMENT_ID"); 
			cmd.addParameter("PRICE_LIST_NO","''"); 
			cmd.addParameter("QTY"); 

			trans = mgr.validate(trans);

			nDiscount  = trans.getNumberValue("PRINFOGET/DATA/DEFDISCOUNT");

			trans.clear();

			nFinalRoleCost = 0.0;

			ref = 0;      

			nFinalRoleCost = toInt(mgr.readNumberValue("SALESPARTCOST"));
			nSalesPartCost = toInt(mgr.readNumberValue("LIST_PRICE"));

			if (nFinalRoleCost !=0)
			{
				if (toInt(mgr.readNumberValue("QTY")) != 0)
				{
					nCostAmount = nFinalRoleCost*toInt(mgr.readNumberValue("QTY"));
					nSalesPriceAmount = nSalesPartCost*toInt(mgr.readNumberValue("QTY"));
				}
				else
				{
					nCostAmount = nFinalRoleCost*1;
					nSalesPriceAmount = nSalesPartCost*1;
				}
			}

			txt =       (Double.isNaN(nDiscount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nDiscount)) + "^" 
						+ (Double.isNaN(nCostAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nCostAmount)) + "^" 
						+ (Double.isNaN(nSalesPriceAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nSalesPriceAmount)) + "^";


			mgr.responseWrite(txt);



		}

		else if ("PLAN_LINE_NO1".equals(val))
		{
			String validationAttrAtr1 = "";
			String validationAttrAtr2 = "";
			String planLineNo = "";     
			String craft =  "";
			String dept =  ""; 
			String sales = ""; 
			if (mgr.readValue("PLAN_LINE_NO1").indexOf("~") > -1)
			{
				planLineNo = mgr.readValue("PLAN_LINE_NO1").substring(0,mgr.readValue("PLAN_LINE_NO1").indexOf("~"));   
				validationAttrAtr1 = mgr.readValue("PLAN_LINE_NO1").substring(mgr.readValue("PLAN_LINE_NO1").indexOf("~")+1,mgr.readValue("PLAN_LINE_NO1").length());
				validationAttrAtr2 = validationAttrAtr1.substring(validationAttrAtr1.indexOf("~")+1,validationAttrAtr1.length());
				craft =  validationAttrAtr1.substring(0,validationAttrAtr1.indexOf("~"));
				dept =  validationAttrAtr2.substring(0,validationAttrAtr2.indexOf("~")); 
				sales =   validationAttrAtr2.substring(validationAttrAtr2.indexOf("~")+1,validationAttrAtr2.length()); 
			}
			else
			{
				planLineNo = mgr.readValue("PLAN_LINE_NO1");
				craft =  mgr.readValue("ROLE_CODE");
				dept =  mgr.readValue("ITEM0_ORG_CODE"); 
				sales =   mgr.readValue("CATALOG_NO"); 
			}


			cmd = trans.addCustomFunction("CRAFTRNO","WORK_ORDER_ROLE_API.Get_Plan_Row_No","CRAFTROWNO");
			cmd.addParameter("WO_NO");
			cmd.addParameter("PLAN_LINE_NO",planLineNo);

			cmd = trans.addCustomFunction("REFNO","WORK_ORDER_ROLE_API.Get_Plan_Ref_Number","CRAFTREFNO");
			cmd.addParameter("WO_NO");
			cmd.addParameter("PLAN_LINE_NO",planLineNo);

			cmd = trans.addCustomFunction("CRAFTDESC","WORK_ORDER_ROLE_API.Get_Plan_Description","CRAFTDESCRIPTION");
			cmd.addParameter("WO_NO");
			cmd.addParameter("PLAN_LINE_NO",planLineNo);


			trans = mgr.validate(trans);

			craftRowNo = trans.getValue("CRAFTRNO/DATA/CRAFTROWNO");
			referenceNo = trans.getValue("REFNO/DATA/CRAFTREFNO");
			craftDesc = trans.getValue("CRAFTDESC/DATA/CRAFTDESCRIPTION");

			txt = (mgr.isEmpty(craftRowNo) ? "" : (craftRowNo))+ "^"
				  + (mgr.isEmpty(referenceNo) ? "" : (referenceNo))+ "^"
				  + (mgr.isEmpty(craftDesc) ? "" : (craftDesc))+ "^"
				  + (mgr.isEmpty(planLineNo) ? "" : (planLineNo))+ "^"
				  + (mgr.isEmpty(planLineNo) ? "" : (planLineNo))+ "^"
				  + (mgr.isEmpty(craft) ? "" : (craft))+ "^"
				  + (mgr.isEmpty(dept) ? "" : (dept))+ "^"
				  + (mgr.isEmpty(sales) ? "" : (sales))+ "^"; 
			mgr.responseWrite(txt);
		}

		mgr.endResponse();

	}


//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

	public void okFind()
	{
		ASPQuery q; 
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
		q.includeMeta("ALL");

                mgr.querySubmit(trans,headblk);

		eval(headset.syncItemSets());

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERCODINGNODATA: No data found."));
			headset.clear();
		}
	}


	public void countFind()
	{
		ASPQuery q; 
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void newRow()
	{
		ASPCommand cmd; 
		ASPBuffer data;
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","EMPLOYEE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

	    public void searchItem()
	{
		int currrow;

		ASPQuery q; 
		ASPManager mgr = getASPManager();

		itemset0.clear();

		q = trans.addQuery(itemblk0);
		q.addWhereCondition("EMP_NO = ? AND WORK_ORDER_COST_TYPE = ? AND WORK_ORDER_ACCOUNT_TYPE = ? AND COMPANY = ?"); 
		q.addParameter("EMP_NO", headset.getRow().getValue("EMP_NO"));
		q.addParameter("WORK_ORDER_COST_TYPE", dummyset1.getRow().getValue("DEF_COST_TYPE"));
		q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset1.getRow().getValue("DEF_ACCOUNT_TYPE"));
		q.addParameter("COMPANY", headset.getRow().getValue("COMPANY"));
		q.setOrderByClause("WO_NO,CRE_DATE");
		q.includeMeta("ALL");
		currrow = headset.getCurrentRowNo();

                mgr.querySubmit(trans,itemblk0);

		headset.goTo(currrow);

		if (itemset0.countRows() == 0)
		{
			itemset0.clear();
			mgr.showAlert(mgr.translate("PCMWWORKORDERCODINGNOPREREG: No Previous registration(s) found."));  
		}
	}

//=============
// ITEMS
//=============

	public void okFindITEM0()
	{

		int currrow;

		ASPQuery q; 
		ASPManager mgr = getASPManager();

		itemset0.clear();
		if ("ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
		{
			q = trans.addQuery(itemblk0);
			q.addWhereCondition("EMP_NO = ? AND WORK_ORDER_COST_TYPE = ? AND WORK_ORDER_ACCOUNT_TYPE = ? AND COMPANY = ?"); 
		        q.addParameter("EMP_NO", headset.getRow().getValue("EMP_NO"));
		        q.addParameter("WORK_ORDER_COST_TYPE", dummyset1.getRow().getValue("DEF_COST_TYPE"));
		        q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset1.getRow().getValue("DEF_ACCOUNT_TYPE"));
                        q.addParameter("COMPANY", headset.getRow().getValue("COMPANY"));
			q.setOrderByClause("WO_NO,CRE_DATE");
			q.includeMeta("ALL");
			currrow = headset.getCurrentRowNo();

                        mgr.querySubmit(trans,itemblk0);

			headset.goTo(currrow);

			if (itemset0.countRows() == 0)
			{
				itemset0.clear();
				mgr.showAlert(mgr.translate("PCMWWORKORDERCODINGNODATA: No data found."));
			}
		}
	}

	public void prevReg()
	{

		int headrowno;

		ASPQuery q; 
		ASPManager mgr = getASPManager();

		trans.clear();
		headrowno = headset.getCurrentRowNo();

		q = trans.addQuery(itemblk0);
		q.addWhereCondition("EMP_NO = ? AND WORK_ORDER_COST_TYPE = ? AND WORK_ORDER_ACCOUNT_TYPE = ? AND COMPANY = ?"); 
		q.addParameter("EMP_NO", headset.getRow().getValue("EMP_NO"));
		q.addParameter("WORK_ORDER_COST_TYPE", dummyset1.getRow().getValue("DEF_COST_TYPE"));
		q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset1.getRow().getValue("DEF_ACCOUNT_TYPE"));
                q.addParameter("COMPANY", headset.getRow().getValue("COMPANY"));
		q.setOrderByClause("WO_NO,CRE_DATE");

		q.includeMeta("ALL");

                mgr.querySubmit(trans,itemblk0);

		if ("ITEM0.OkFind".equals(mgr.readValue("__COMMAND"))  &&   itemset0.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERCODINGNODATA: No data found."));
			itemset0.clear();  
		}

		itemtbl0.clearQueryRow();

		headset.goTo(headrowno);  
	}



	public void okFdITEM0()
	{

		int headrowno;

		ASPQuery q;
		ASPManager mgr = getASPManager();

		trans.clear();
		headrowno = headset.getRowSelected();   

		headset.goTo(headset.getRowSelected());

		q = trans.addQuery(itemblk0);
		q.addWhereCondition("EMP_NO = ? AND WORK_ORDER_COST_TYPE = ? AND WORK_ORDER_ACCOUNT_TYPE = ? AND COMPANY = ?"); 
		q.addParameter("EMP_NO", headset.getRow().getValue("EMP_NO"));
		q.addParameter("WORK_ORDER_COST_TYPE", dummyset1.getRow().getValue("DEF_COST_TYPE"));
		q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset1.getRow().getValue("DEF_ACCOUNT_TYPE"));
		q.addParameter("COMPANY", headset.getRow().getValue("COMPANY"));
		q.setOrderByClause("WO_NO,CRE_DATE");
		q.includeMeta("ALL");

                mgr.querySubmit(trans,itemblk0);

		itemtbl0.clearQueryRow();   
		headset.goTo(headrowno);
		headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
	}
   
   //Bug id 81023, start
   public void saveReturnITEM0()
   {
      ASPCommand cmd;
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      int currrowItem1 = itemset0.getCurrentRowNo();

      itemset0.changeRow();      
      
      trans.clear();
      cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
      cmd.addInParameter("COMPANY", itemset0.getRow().getValue("COMPANY")); 
      cmd.addInParameter("EMP_NO", itemset0.getRow().getValue("EMP_NO")); 
      cmd.addInParameter("CRE_DATE", itemset0.getRow().getFieldValue("CRE_DATE")); 
      cmd.addInParameter("CATALOG_NO", "T"); 
      cmd.addInParameter("CONTRACT", itemset0.getRow().getValue("CONTRACT"));      

      mgr.submit(trans);

      headset.goTo(currHead);
      okFdITEM0();
      itemset0.goTo(currrowItem1);
   }   
   
   public void saveNewITEM0()
   {
      ASPCommand cmd;
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      int currrowItem1 = itemset0.getCurrentRowNo();

      itemset0.changeRow();     
      
      trans.clear();
      cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
      cmd.addInParameter("COMPANY", itemset0.getRow().getValue("COMPANY")); 
      cmd.addInParameter("EMP_NO", itemset0.getRow().getValue("EMP_NO")); 
      cmd.addInParameter("CRE_DATE", itemset0.getRow().getFieldValue("CRE_DATE")); 
      cmd.addInParameter("CATALOG_NO", "T"); 
      cmd.addInParameter("CONTRACT", itemset0.getRow().getValue("CONTRACT"));
      
      mgr.submit(trans);
      trans.clear();

      headset.goTo(currHead);
      newRowITEM0();   
   }
   
   public void deleteITEM0()
   {
      ASPCommand cmd;
      ASPManager mgr = getASPManager();

      int currow = headset.getCurrentRowNo();

      itemset0.store();

      if (itemlay0.isMultirowLayout())
      {
         itemset0.setSelectedRowsRemoved();
         itemset0.unselectRows();
      }
      else
      {
         itemset0.setRemoved();      
      }
      
      trans.clear();
      cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
      cmd.addInParameter("COMPANY", itemset0.getRow().getValue("COMPANY")); 
      cmd.addInParameter("EMP_NO", itemset0.getRow().getValue("EMP_NO")); 
      cmd.addInParameter("CRE_DATE", itemset0.getRow().getFieldValue("CRE_DATE")); 
      cmd.addInParameter("CATALOG_NO", "T"); 
      cmd.addInParameter("CONTRACT", itemset0.getRow().getValue("CONTRACT"));      

      mgr.submit(trans);

      headset.goTo(currow);        
   }
   //Bug id 81023, end

	public void countFindITEM0()
	{
		int headrowno;

		ASPQuery q; 
		ASPManager mgr = getASPManager();


		headrowno = headset.getCurrentRowNo();
		q = trans.addQuery(itemblk0);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("EMP_NO = ? AND WORK_ORDER_COST_TYPE = ? AND WORK_ORDER_ACCOUNT_TYPE = ? AND COMPANY = ?"); 
		q.addParameter("EMP_NO", headset.getRow().getValue("EMP_NO"));
		q.addParameter("WORK_ORDER_COST_TYPE", dummyset1.getRow().getValue("DEF_COST_TYPE"));
		q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset1.getRow().getValue("DEF_ACCOUNT_TYPE"));
                q.addParameter("COMPANY", headset.getRow().getValue("COMPANY"));
		mgr.submit(trans);
		itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));

		headset.goTo(headrowno);
		itemset0.clear();
	}


	public void newRowITEM0()
	{
		ASPCommand cmd; 
		ASPBuffer data;
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addEmptyCommand("ITEM0","WORK_ORDER_CODING_API.New__",itemblk0);
		cmd.setOption("ACTION","PREPARE");
                cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","ITEM0_COMPANY");
                cmd.addParameter("CONTRACT",headset.getRow().getFieldValue("CONTRACT"));
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM0/DATA");
		data.setValue("EMP_NO", headset.getRow().getFieldValue("EMP_NO"));
		data.setValue("ORG_CODE",headset.getRow().getFieldValue("ORG_CODE"));
		data.setValue("WORK_ORDER_COST_TYPE",sCostType);
		data.setValue("WORK_ORDER_ACCOUNT_TYPE",sAccountType);
		data.setValue("CONTRACT",headset.getRow().getFieldValue("CONTRACT"));
		data.setValue("COMPANY",trans.getValue("GETCOMP/DATA/COMPANY"));
		itemset0.addRow(data);
	}


	public boolean isModuleInst1(String module_)
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
		ASPCommand cmd = mgr.newASPCommand();
		String modVersion;

		cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
		cmd.addParameter("MODULENAME",module_);

		trans1 = mgr.perform(trans1);
		modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

		if (!mgr.isEmpty(modVersion))
			return true;
		else
			return false;
	}


	public boolean isModuleInst(String module_)
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
		ASPCommand cmd = mgr.newASPCommand();
		String modVersion;

		cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
		cmd.addParameter("MODULENAME",module_);

		trans1 = mgr.performConfig(trans1);
		modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

		if (!mgr.isEmpty(modVersion))
			return true;
		else
			return false;
	}


	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		//----------------------------------------------------------------------------------------------------
		//------------------------------ HEAD BLOCK BEGIN ---------------------------------------------------
		//---------------------------------------------------------------------------------------------------- 

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		boolean orderInst = false;

		f = headblk.addField("MODULENAME");
		f.setHidden();
		f.setFunction("''");

		if (isModuleInst("ORDER"))
			orderInst = true;
		else
			orderInst = false;      

		f = headblk.addField("COMPANY");
		f.setSize(8);
		f.setMaxLength(20);
		f.setHidden();
		f.setUpperCase();

		f = headblk.addField("EMP_NO");
		f.setSize(14);
		f.setMaxLength(11);
		f.setMandatory();
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGEMPNO: Employee");
		f.setUpperCase();

		f = headblk.addField("NAME");
		f.setSize(32);
		f.setReadOnly();
		f.setMaxLength(100);
		f.setLabel("PCMWWORKORDERCODINGNAME: Name");

		f = headblk.addField("ORG_CODE");
		f.setSize(16);
		f.setReadOnly();
		f.setMaxLength(8);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGDEPARTMEN: List of Maintenance Organization"));
		f.setLabel("PCMWWORKORDERCODINGORGCODE: Maintenance Organization");
		f.setUpperCase();
		
                f = headblk.addField("ORG_CONTRACT").setDbName("CONTRACT");
		f.setSize(32);
		f.setReadOnly();
		f.setMaxLength(100);
                f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setLabel("PCMWWORKORDERCODINGORGCONTRACT: Site");

		f = headblk.addField("DEFCONTRACT");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("DEFBASEUNITPRICE","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setHidden();

		f = headblk.addField("DEFSALEUNITPRICE","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setHidden();


		f = headblk.addField("DEFCURRENCYRATE","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setHidden();

		f = headblk.addField("DEFCUSTOMERNO");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("DEFAGREEMENTID");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("DEFPRICELIST");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();

		headblk.setView("EMPLOYEE");
		headblk.defineCommand("EMPLOYEE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERCODINGTRBE: Time Report By Employee"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.addCustomCommand("okFdITEM0",mgr.translate("PCMWWORKORDERCODINGSHOWPREVREGI: Show Previous Registrations..."));
		headbar.disableCommand(headbar.SUBMIT);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.DELETE);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		//----------------------------------------------------------------------------------------------------
		//------------------------------ DUMMY1 BLOCK BEGIN ---------------------------------------------------
		//----------------------------------------------------------------------------------------------------

		dummyblk1 = mgr.newASPBlock("DUMMY1");

		f = dummyblk1.addField("DEF_COST_TYPE");
		f.setSize(11);
		f.setFunction("Work_Order_Cost_Type_API.Get_Client_Value(0)");
		f.setHidden();

		f = dummyblk1.addField("DEF_ACCOUNT_TYPE");
		f.setSize(11);
		f.setFunction("Work_Order_Account_Type_API.Get_Client_Value(0)");
		f.setHidden();

		f = dummyblk1.addField("NUMCATROW","Number");
		f.setFunction("0");
		f.setHidden();

		dummyblk1.setView("DUAL");
		dummyset1 = dummyblk1.getASPRowSet();

		//----------------------------------------------------------------------------------------------------
		//------------------------------ ITEM0 BLOCK BEGIN ---------------------------------------------------
		//---------------------------------------------------------------------------------------------------- 

		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk0.addField("CRE_DATE","Datetime");
		f.setSize(20);
		f.setReadOnly();
		f.setMaxLength(50);
		f.setInsertable();
		f.setLabel("PCMWWORKORDERCODINGCREDATE: Creation Date");

		f = itemblk0.addField("CONTRACT");
		f.setSize(11);
		f.setDynamicLOV("USER_ALLOWED_SITE",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGLISTSITE: List of Site"));
		f.setMandatory();
		f.setMaxLength(5);
		f.setLabel("PCMWWORKORDERCODINGCONTRACT: WO Site");
                f.setReadOnly();
		f.setUpperCase();

		//Bug 89399, Start, Added a mask
		f = itemblk0.addField("WO_NO","Number","#");
		f.setSize(10);
		f.setReadOnly();
		f.setInsertable();
		f.setMaxLength(8);
		f.setDynamicLOV("ACTIVE_WORK_ORDER2","COMPANY",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGWONOLIST: List of WO No"));
		f.setMandatory();
		f.setCustomValidation("WO_NO,NAME,ORG_CODE,QTY,ITEM0_EMP_NO,ROLE_CODE","MCHCODE,MCHNAME,MCHCODECONTRACT,ROLE_CODE,CATALOG_NO,SALESPARTCATALOGDESC,ERRDESCR,DEFDISCOUNT,SALESPARTCOST,LIST_PRICE,AMOUNT,SALES_AMOUNT,CMNT");
		f.setLabel("PCMWWORKORDERCODINGWONO: WO No");
		//Bug 89399, End

		f = itemblk0.addField("PLAN_LINE_NO1");
		f.setSize(15);
		f.setFunction("''");
		f.setLOV("WorkOrderRolePlanningLov1.page","WO_NO",600,450);
		f.setMaxLength(20);
		f.setCustomValidation("WO_NO,PLAN_LINE_NO1,ROLE_CODE,ITEM0_ORG_CODE,CATALOG_NO","CRAFTROWNO,CRAFTREFNO,CRAFTDESCRIPTION,PLAN_LINE_NO1,PLAN_LINE_NO,ROLE_CODE,ITEM0_ORG_CODE,CATALOG_NO");
		f.setLabel("PCMWWORKORDERCODINGPLANLINENO: Plan Line No");

		f = itemblk0.addField("PLAN_LINE_NO","Number");
		f.setSize(15);
		f.setHidden(); 

		f = itemblk0.addField("CRAFTROWNO","Number");
		f.setSize(15);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGCRAFTROWNO: Operation No");
		f.setMaxLength(20);
		f.setFunction("WORK_ORDER_ROLE_API.Get_Plan_Row_No(:WO_NO,:PLAN_LINE_NO)");

		f = itemblk0.addField("CRAFTREFNO");
		f.setSize(15);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGREFNO: Reference No");
		f.setMaxLength(20);
		f.setFunction("WORK_ORDER_ROLE_API.Get_Plan_Ref_Number(:WO_NO,:PLAN_LINE_NO)");

		f = itemblk0.addField("CRAFTDESCRIPTION");
		f.setSize(15);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGCRAFTDESCRIPTION: Description");
		f.setMaxLength(20);
		f.setFunction("WORK_ORDER_ROLE_API.Get_Plan_Description(:WO_NO,:PLAN_LINE_NO)");

		f = itemblk0.addField("ERRDESCR");
		f.setSize(25);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGERRDESCR: Directive");
		f.setFunction("WORK_ORDER_API.Get_Err_Descr(:WO_NO)");

		f = itemblk0.addField("MCHCODE");
		f.setSize(11);
		f.setMaxLength(100);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGMCHCODE: Object ID");
		f.setDynamicLOV("MAINTENANCE_OBJECT","MCHCODECONTRACT CONTRACT",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGOBJID1: List of Object ID"));
		f.setFunction("SEPARATE_WORK_ORDER_API.Get_Mch_Code(:WO_NO)");

		f = itemblk0.addField("MCHNAME");
		f.setSize(25);
		f.setReadOnly();
		f.setDefaultNotVisible ();
		f.setLabel("PCMWWORKORDERCODINGMCHNAME: Object Description");
		f.setFunction("SEPARATE_WORK_ORDER_API.Get_Obj_Description(:WO_NO)"); 

		f = itemblk0.addField("MCHCODECONTRACT");
		f.setSize(11);
		f.setReadOnly();
		f.setMaxLength(5);
		f.setLabel("PCMWWORKORDERCODINGMCHCODECONTRACT: Site");
		f.setUpperCase();
		f.setFunction("SEPARATE_WORK_ORDER_API.Get_Contract(:WO_NO)");

		f = itemblk0.addField("ITEM0_COMPANY");
		f.setSize(11);
		f.setMandatory();
		f.setHidden();
		f.setMaxLength(20);
		f.setLabel("PCMWWORKORDERCODINGITEM0COMPANY: Company");
		f.setDbName("COMPANY");
		f.setUpperCase();

		f = itemblk0.addField("ITEM0_ORG_CODE");
		f.setDbName("ORG_CODE");
		f.setMaxLength(8);
		f.setHidden();
		/*f.setLabel("PCMWWORKORDERCODINGITEM0ITEM0ORGCODE: Maintenance Organization");
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGDEPARTMEN: List of Maintenance Organization"));
		f.setUpperCase();*/

		f = itemblk0.addField("ROLE_CODE");
		f.setSize(11);
		f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGLISTCRAF: List of Craft"));
		f.setLabel("PCMWWORKORDERCODINGROLECODE: Craft");
		f.setMaxLength(10);
		f.setCustomValidation("CONTRACT,ROLE_CODE,WO_NO,ORG_CODE,CUSTOMER_ID,AGREEMENT_ID,NAME,QTY","ROLE_CODE,CATALOG_NO,SALESPARTCATALOGDESC,DEFDISCOUNT,SALESPARTCOST,LIST_PRICE,AMOUNT,SALES_AMOUNT,CMNT,SALESCOST0,SALESCOST1,SALESCOST2,CMNT0,CMNT1,CMNT2");
		f.setUpperCase();

		f = itemblk0.addField("CATALOG_NO");
		f.setSize(18);
		f.setMaxLength(25);
		if (orderInst)
			f.setDynamicLOV("SALES_PART_SERVICE_LOV","CONTRACT",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGLISTSALES: List of Sales Part"));
		f.setLabel("PCMWWORKORDERCODINGCATALOGNO: Sales Part No");
		f.setCustomValidation("CONTRACT,ROLE_CODE,CATALOG_NO,ORG_CODE,CUSTOMER_ID,AGREEMENT_ID,NAME,QTY","SALESPARTCATALOGDESC,DEFDISCOUNT,SALESPARTCOST,LIST_PRICE,AMOUNT,SALES_AMOUNT,CMNT,SALESCOST0,SALESCOST1,SALESCOST2,CMNT0,CMNT1,CMNT2");
		f.setUpperCase();

		f = itemblk0.addField("SALESPARTCATALOGDESC");
		f.setSize(24);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWWORKORDERCODINGSALESPARTCATALOGDESC: Sales Part Description");
		if (orderInst)
			f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
		else
			f.setFunction("''");

		f = itemblk0.addField("QTY","Number");
		f.setSize(9);
		f.setMandatory();
		f.setMaxLength(6);
		f.setCustomValidation("CONTRACT,CATALOG_NO,CUSTOMER_ID,AGREEMENT_ID,QTY,SALESPARTCOST,LIST_PRICE","DEFDISCOUNT,AMOUNT,SALES_AMOUNT");
		f.setLabel("PCMWWORKORDERCODINGQTY: Hours");

		f = itemblk0.addField("SALESPARTCOST","Money");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGSALESPARTCOST: Cost");
		if (orderInst)
			f.setFunction("SALES_PART_API.Get_Cost(:CONTRACT,:CATALOG_NO)");
		else
			f.setFunction("''");                

		f = itemblk0.addField("AMOUNT","Money");
		f.setSize(14);
		f.setMaxLength(52);
		f.setLabel("PCMWWORKORDERCODINGAMOUNT: Cost Amount");
		f.setReadOnly();

		f = itemblk0.addField("LIST_PRICE","Money");
		f.setSize(14);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGLISTPRICE: Sales Price");

		f = itemblk0.addField("DEFDISCOUNT","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setLabel("PCMWWORKORDERCODINGDEFDISCOUNT: Discount");
		f.setReadOnly();

		f = itemblk0.addField("SALES_AMOUNT","Money");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERCODINGSALESAMT: Sales Price Amount");
		f.setMaxLength(52);
		f.setFunction("LIST_PRICE*QTY");

		f = itemblk0.addField("CMNT");
		f.setSize(50);
		f.setMaxLength(80); 
		f.setDefaultNotVisible();
		f.setLabel("PCMWWORKORDERCODINGCMNT: Comment");

		f = itemblk0.addField("ITEM0_EMP_NO");
		f.setSize(9);
		f.setHidden();
		f.setMaxLength(11); 
		f.setLabel("PCMWWORKORDERCODINGITEM0EMPNO: Employee");
		f.setDbName("EMP_NO");
		f.setUpperCase();

		f = itemblk0.addField("WORK_ORDER_COST_TYPE");
		f.setSize(11);
		f.setMandatory();
		f.setMaxLength(20);
		f.setHidden();
		f.setLabel("PCMWWORKORDERCODINGWORKORDERCOSTTYPE: Cost Type");

		f = itemblk0.addField("WORK_ORDER_ACCOUNT_TYPE");
		f.setSize(11);
		f.setMandatory();
		f.setHidden();
		f.setMaxLength(20);
		f.setLabel("PCMWWORKORDERCODINGWORKORDERACCOUNTTYPE: Account Type");

		f = itemblk0.addField("ROW_NO","Number");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERCODINGROWNO: Row No");

		f = itemblk0.addField("SPARE_CONTRACT");
		f.setSize(11);
		f.setHidden();

		f = itemblk0.addField("SALESCOST0","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setHidden();

		f = itemblk0.addField("SALESCOST1","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setHidden();

		f = itemblk0.addField("SALESCOST2","Money");
		f.setSize(11);
		f.setFunction("0");
		f.setHidden();

		f = itemblk0.addField("CMNT0");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();          

		f = itemblk0.addField("CMNT1");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();

		f = itemblk0.addField("CMNT2");
		f.setSize(11);
		f.setFunction("''");
		f.setHidden();                

		f = itemblk0.addField("CUSTOMER_ID");
		f.setFunction("''");
		f.setHidden(); 


		f = itemblk0.addField("AGREEMENT_ID");     
		f.setFunction("''");
		f.setHidden();   

		f = itemblk0.addField("BASE_PRICE","Money");     
		f.setFunction("0.00");
		f.setHidden(); 

		f = itemblk0.addField("UNIT_PRICE","Money");     
		f.setFunction("0.00");
		f.setHidden(); 

		f = itemblk0.addField("CURRENCY_RATE","Money");     
		f.setFunction("0.00");
		f.setHidden(); 

		f = itemblk0.addField("PRICE_LIST_NO");     
		f.setFunction("''");
		f.setHidden();

		itemblk0.setView("WORK_ORDER_CODING");
		itemblk0.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
		itemset0 = itemblk0.getASPRowSet();
		itemblk0.setMasterBlock(headblk);

		itembar0 = mgr.newASPCommandBar(itemblk0);

		itembar0.addCustomCommand("prevReg",mgr.translate("PCMWWORKORDERCODINGITEMSHOWPREVIOUS: Show Previous Registrations..."));
		itembar0.enableMultirowAction();

		itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
		itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      //Bug id 81023, start
		//Bug 89399, Start
		itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0","checkMando()");
		itembar0.defineCommand(itembar0.SAVENEW,"saveNewITEM0","checkMando()");
		//Bug 89399, End
      itembar0.defineCommand(itembar0.DELETE, "deleteITEM0");
      //Bug id 81023, end

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWWORKORDERCODINGITM0: Work Order Coding"));
		itemtbl0.setWrap();

		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
		itemlay0.setDialogColumns(2);
		itemlay0.setDataSpan("CRE_DATE",1);

		b = mgr.newASPBlock("WORK_ORDER_COST_TYPE");

		b.addField("CLIENT_VALUES0");

		b = mgr.newASPBlock("WORK_ORDER_ACCOUNT_TYPE");

		b.addField("CLIENT_VALUES1");
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		mgr.getASPField("EMP_NO").setDynamicLOV("EMPLOYEE_NO",600,445);
		mgr.getASPField("EMP_NO").setLOVProperty("WHERE","COMPANY= '"+companyvar+"'");
		mgr.getASPField("EMP_NO").setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODINGEMPLOO: List of Employee"));

		if (itemlay0.isNewLayout())
		{
			headbar.disableCommand(headbar.FIND);
			headbar.disableCommand(headbar.BACK);
		}

		if (headlay.isSingleLayout() && headset.countRows()>0)
		{
			title_ = mgr.translate("PCMWWORKORDERCODINGTRE: Time Report By Employee - ");
			xx = headset.getRow().getValue("EMP_NO");
			yy = headset.getRow().getValue("NAME");
		}
		else
		{
			title_ = mgr.translate("PCMWWORKORDERCODINGTRBE: Time Report By Employee");
			xx = "";
			yy = "";
		}

		if (itemlay0.isMultirowLayout()|| itemlay0.isSingleLayout())
			itembar0.enableCommand(itembar0.FIND);

		if (itemlay0.isFindLayout())
			headbar.disableCommand(headbar.FIND);

		if (headlay.isSingleLayout())
			headbar.removeCustomCommand("okFdITEM0");

		if (itemset0.countRows() == 0)
			itembar0.disableCommand(itembar0.DUPLICATEROW);

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return title_ + xx + " " + yy;
	}

	protected String getTitle()
	{
                return title_ + xx + " " + yy;
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (headlay.isSingleLayout() && (headset.countRows() > 0))
			appendToHTML(itemlay0.show());

		appendDirtyJavaScript("function validateRoleCode(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i) == \"\" )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = getField_('CATALOG_NO',i).value;\n");
		appendDirtyJavaScript("		getField_('SALESPARTCATALOGDESC',i).value = getField_('SALESPARTCATALOGDESC',i).value;\n");
		appendDirtyJavaScript("		getField_('DEFDISCOUNT',i).value = getField_('DEFDISCOUNT',i).value;\n");
		appendDirtyJavaScript("		if (getField_('LIST_PRICE',i).value != \"\")\n");
		appendDirtyJavaScript("			getField_('SALESPARTCOST',i).value = getField_('SALESCOST2',i).value;	\n");
		appendDirtyJavaScript("		else if (getField_('ORG_CODE',i).value != \"\")	\n");
		appendDirtyJavaScript("			getField_('SALESPARTCOST',i).value = getField_('SALESCOST0',i).value;\n");
		appendDirtyJavaScript("		getField_('LIST_PRICE',i).value = getField_('LIST_PRICE',i).value;\n");
		appendDirtyJavaScript("		if (getField_('LIST_PRICE',i).value != \"\")\n");
		appendDirtyJavaScript("	        {\n");
		appendDirtyJavaScript("			if (getField_('QTY',i).value != \"\")\n");                    
		appendDirtyJavaScript("				getField_('AMOUNT',i).value = parseFloat(getField_('SALESCOST2',i).value)*parseFloat(getField_('QTY',i).value);	\n");
		appendDirtyJavaScript("	        	else\n");
		appendDirtyJavaScript("				getField_('AMOUNT',i).value = getField_('SALESCOST2',i).value;	\n");
		appendDirtyJavaScript("           }\n");         
		appendDirtyJavaScript("		else if (getField_('ORG_CODE',i) != \"\")	\n");
		appendDirtyJavaScript("	        {\n");
		appendDirtyJavaScript("			if (getField_('QTY',i).value != \"\")\n");
		appendDirtyJavaScript("				getField_('AMOUNT',i).value = getField_('SALESCOST0',i).value*getField_('QTY',i).value;	\n");
		appendDirtyJavaScript("	        	else\n");
		appendDirtyJavaScript("				getField_('AMOUNT',i).value = getField_('SALESCOST0',i).value*1;	\n");
		appendDirtyJavaScript("           }\n");       
		appendDirtyJavaScript("		getField_('SALES_AMOUNT',i).value = getField_('SALES_AMOUNT',i).value;\n");
		appendDirtyJavaScript("		if (getField_('LIST_PRICE',i).value == \"\")\n");
		appendDirtyJavaScript("			getField_('CMNT',i).value = getField_('CMNT0',i).value;\n");
		appendDirtyJavaScript("		else\n");
		appendDirtyJavaScript("			getField_('CMNT',i).value = getField_('CMNT2',i).value;	\n");
		appendDirtyJavaScript("		getField_('SALESCOST0',i).value = getField_('SALESCOST0',i).value;\n");
		appendDirtyJavaScript("		getField_('SALESCOST1',i).value = getField_('SALESCOST1',i).value;\n");
		appendDirtyJavaScript("		getField_('SALESCOST2',i).value = getField_('SALESCOST2',i).value;\n");
		appendDirtyJavaScript("		getField_('CMNT0',i).value = getField_('CMNT0',i).value;\n");
		appendDirtyJavaScript("		getField_('CMNT1',i).value = getField_('CMNT1',i).value;\n");
		appendDirtyJavaScript("		getField_('CMNT2',i).value = getField_('CMNT2',i).value;\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
		appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
		appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
		appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
		appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
		appendDirtyJavaScript("		+ '&CUSTOMER_ID=' + URLClientEncode(getValue_('CUSTOMER_ID',i))\n");
		appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
		appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
		appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,0);\n");
		appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,1);\n");
		appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,2);\n");
		appendDirtyJavaScript("		assignValue_('DEFDISCOUNT',i,3);\n");
		appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,4);\n");
		appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,5);\n");
		appendDirtyJavaScript("		assignValue_('AMOUNT',i,6);\n");
		appendDirtyJavaScript("		assignValue_('SALES_AMOUNT',i,7);\n");
		appendDirtyJavaScript("		assignValue_('CMNT',i,8);\n");
		appendDirtyJavaScript("		assignValue_('SALESCOST0',i,9);\n");
		appendDirtyJavaScript("		assignValue_('SALESCOST1',i,10);\n");
		appendDirtyJavaScript("		assignValue_('SALESCOST2',i,11);\n");
		appendDirtyJavaScript("		assignValue_('CMNT0',i,12);\n");
		appendDirtyJavaScript("		assignValue_('CMNT1',i,13);\n");
		appendDirtyJavaScript("		assignValue_('CMNT2',i,14);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validatePlanLineNo1(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("         setDirty();\n");
		appendDirtyJavaScript("	if( !checkPlanLineNo1(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('PLAN_LINE_NO1',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CRAFTROWNO',i).value = '';\n");
		appendDirtyJavaScript("		getField_('CRAFTREFNO',i).value = '';\n");
		appendDirtyJavaScript("       	getField_('CRAFTDESCRIPTION',i).value = '';\n");
		appendDirtyJavaScript("       	getField_('PLAN_LINE_NO1',i).value = '';\n");
		appendDirtyJavaScript("       	getField_('PLAN_LINE_NO',i).value = '';\n");
		appendDirtyJavaScript("       	getField_('ROLE_CODE',i).value = '';\n");
		appendDirtyJavaScript("       	getField_('ITEM0_ORG_CODE',i).value = '';\n");
		appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = '';\n");
		appendDirtyJavaScript("return;\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=PLAN_LINE_NO1'\n");
		appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
		appendDirtyJavaScript("       	+ '&PLAN_LINE_NO1=' + URLClientEncode(getValue_('PLAN_LINE_NO1',i))\n");
		appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
		appendDirtyJavaScript("  	+ '&ITEM0_ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
		appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_LINE_NO1',i,'Plan Line No') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("       	assignValue_('CRAFTROWNO',i,0);\n");
		appendDirtyJavaScript("       	assignValue_('CRAFTREFNO',i,1);\n");
		appendDirtyJavaScript("		assignValue_('CRAFTDESCRIPTION',i,2);\n");
		appendDirtyJavaScript("       	assignValue_('PLAN_LINE_NO1',i,3);\n");
		appendDirtyJavaScript("		assignValue_('PLAN_LINE_NO',i,4);\n");
		appendDirtyJavaScript("       	assignValue_('ROLE_CODE',i,5);\n");
		appendDirtyJavaScript("		assignValue_('ITEM0_ORG_CODE',i,6);\n");
		appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,7);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	validateCatalogNo(i);\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validateCatalogNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i) =='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCatalogNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CATALOG_NO',i).value ==\"\" )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('SALESPARTCATALOGDESC',i).value = '';\n");
		appendDirtyJavaScript("		getField_('DEFDISCOUNT',i).value = '';\n");
		appendDirtyJavaScript("		if (getField_('ROLE_CODE',i).value != \"\")\n");
		appendDirtyJavaScript("			getField_('SALESPARTCOST',i).value = getField_('SALESCOST1',i).value;	\n");
		appendDirtyJavaScript("		else if (getField_('ORG_CODE',i).value != \"\")	\n");
		appendDirtyJavaScript("			getField_('SALESPARTCOST',i).value = getField_('SALESCOST0',i).value;\n");
		appendDirtyJavaScript("		getField_('SALESPARTCOST',i).value = '';\n");
		appendDirtyJavaScript("		getField_('LIST_PRICE',i).value = '';\n");
		appendDirtyJavaScript("		if (getField_('ROLE_CODE',i).value != \"\")\n");
		appendDirtyJavaScript("		{\n");
		appendDirtyJavaScript("			if (getField_('QTY',i).value != \"\")\n");
		appendDirtyJavaScript("				getField_('SALESPARTCOST',i).value = parseFloat(getField_('SALESCOST1',i).value)*parseFloat(getField_('QTY',i).value);\n");
		appendDirtyJavaScript("			else	\n");
		appendDirtyJavaScript("				getField_('SALESPARTCOST',i).value = parseFloat(getField_('SALESCOST1',i).value);\n");
		appendDirtyJavaScript("		}\n");
		appendDirtyJavaScript("		else if (getField_('ORG_CODE',i).value != \"\")\n");
		appendDirtyJavaScript("		{\n");
		appendDirtyJavaScript("			if (getField_('QTY',i).value != \"\")\n");
		appendDirtyJavaScript("				getField_('SALESPARTCOST',i).value = parseFloat(getField_('SALESCOST0',i).value)*parseFloat(getField_('QTY',i).value);\n");
		appendDirtyJavaScript("			else	\n");
		appendDirtyJavaScript("				getField_('SALESPARTCOST',i).value = parseFloat(getField_('SALESCOST0',i).value);\n");
		appendDirtyJavaScript("		}\n");
		appendDirtyJavaScript("		getField_('AMOUNT',i).value = '';\n");
		appendDirtyJavaScript("		getField_('SALES_AMOUNT',i).value = '';\n");
		appendDirtyJavaScript("		if (getField_('ROLE_CODE',i).value != \"\")\n");
		appendDirtyJavaScript("			getField_('CMNT',i).value = getField_('SALESCOST1',i).value;\n");
		appendDirtyJavaScript("		else\n");
		appendDirtyJavaScript("			getField_('CMNT',i).value = getField_('SALESCOST0',i).value;\n");
		appendDirtyJavaScript("		getField_('SALESCOST0',i).value = '';\n");
		appendDirtyJavaScript("		getField_('SALESCOST1',i).value = '';\n");
		appendDirtyJavaScript("		getField_('SALESCOST2',i).value = '';\n");
		appendDirtyJavaScript("		getField_('CMNT0',i).value = '';\n");
		appendDirtyJavaScript("		getField_('CMNT1',i).value = '';\n");
		appendDirtyJavaScript("		getField_('CMNT2',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
		appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
		appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
		appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
		appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
		appendDirtyJavaScript("		+ '&CUSTOMER_ID=' + URLClientEncode(getValue_('CUSTOMER_ID',i))\n");
		appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
		appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
		appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part No') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,0);\n");
		appendDirtyJavaScript("		assignValue_('DEFDISCOUNT',i,1);\n");
		appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,2);\n");
		appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,3);\n");
		appendDirtyJavaScript("		assignValue_('AMOUNT',i,4);\n");
		appendDirtyJavaScript("		assignValue_('SALES_AMOUNT',i,5);\n");
		appendDirtyJavaScript("		assignValue_('CMNT',i,6);\n");
		appendDirtyJavaScript("		assignValue_('SALESCOST0',i,7);\n");
		appendDirtyJavaScript("		assignValue_('SALESCOST1',i,8);\n");
		appendDirtyJavaScript("		assignValue_('SALESCOST2',i,9);\n");
		appendDirtyJavaScript("		assignValue_('CMNT0',i,10);\n");
		appendDirtyJavaScript("		assignValue_('CMNT1',i,11);\n");
		appendDirtyJavaScript("		assignValue_('CMNT2',i,12);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("function lovRoleCode(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript("    if((document.form.ITEM0_ORG_CODE.value == '') && (document.form.CONTRACT.value == ''))\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("    else if((document.form.ITEM0_ORG_CODE.value != '') && (document.form.CONTRACT.value == ''))\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("    else if((document.form.ITEM0_ORG_CODE.value == '') && (document.form.CONTRACT.value != ''))\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("    else if((document.form.ITEM0_ORG_CODE.value != '') && (document.form.CONTRACT.value != ''))\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
                appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
                appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
                appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
                appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
                appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
                appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");       
                appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
                appendDirtyJavaScript("}\n");

		//Bug 89399, Start
                appendDirtyJavaScript("function checkMando()\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   if (f.QTY.value != '' && f.QTY.value == 0 )  \n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("             alert('");
                appendDirtyJavaScript(                 mgr.translate("PCMWWORKORDERCODINGNOTVALIDQTY: Reported hours value for "));
                appendDirtyJavaScript("'+f.WO_NO.value+' ");
                appendDirtyJavaScript(                 mgr.translate("PCMWWORKORDERCODINGNOTVALIDQTY1: is not allowed to be zero."));
                appendDirtyJavaScript("             ');\n");
                appendDirtyJavaScript("   return false;\n"); 
                appendDirtyJavaScript("   } \n");
                appendDirtyJavaScript("  return checkItem0Fields(-1); \n");
                appendDirtyJavaScript("}\n");
		//Bug 89399, End
	}
}
