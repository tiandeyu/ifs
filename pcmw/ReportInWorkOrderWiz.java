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
*  File        : ReportInWorkOrderWiz.java 
*  Created     : INROLK  010308  Java conversion.
*  Modified    : 
*  INROLK  010425  Corrected GUI.
*  ARWILK  010613  Replaced document.ClientUtil.connect with document.applets[0].connect.
*  CHCRLK  010618  Modified HTML section to make text visible in Netscape.
*  ARWILK  010626  Modified some validation functions & Prepareform function so that 
*                  the logon user is automatically filled.
*  SHFELK  010628  Changed the function checkMandato().  
*  INROlK  010629  Changed the form width for netscape.. call ID 66176.
*  INROlK  010912  Changed default layout of header newLayout and  itemlayout also to newlayout when the page is called. call ID 78209. 
*  INROLK  010920  Changed PrepareItem() to add several time reports.
*  ARWILK  011012  Corrected problems associated with Finish,AddTimeReport,TimeReport,NextTimeReport,PreviousTimeReport and Cancel.
*  ARWILK  011026  Changed functionality so that time report is saved when Time Report buttton is pressed.    
* ----------------------------------------------------------------------------
*  SHAFLK  020801  Bug Id 31859, Changed functions nextForm() and previousForm().    
*  BUNILK  020816  Bug Id 31915 Removed some codes from Hour field validation part of Time Report tab so that to
*                  make it possible for enter more than 24 hours.
*  SHAFLK  020902  Bug Id 32489, Changed function finishForm().      
*    ---------------------Generic WO-------------------------------------------
*  INROLK  021115  Added MCH_CODE_CONTRACT and CONNECTION_TYPE.  
*  SHAFLK  021128  Added a new method isModuleInst() to check availability of ORDER module inside preDefine method 
*                  and removed toDouble method in several places. 
*  BUNILK  030121  Codes reviewed.
*  SHAFLK  030326  Changed methods finishForm(), prepareForm() and prepareITEM().     
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay(). 
*  CHCRLK  031023  Set Number mask # to WO_NO [Call ID 108228]. 
*  CHAMLK  031028  Modified function finishForm() to check whether there are any active wo requisitions attached to the work order.
*  CHAMLK  031103  Modified function getContents to close the wizard when the Cancel button is clicked.
*  JEWILK  031111  Modified method finishForm() to check whether a customer exists and if so prompt an error. Call 110620.
*  SAPRLK  031217  Web Alignment - removed methods clone() and doReset().
*  SAPRLK  040225  Web Alignemnt - removed unnecessary global variables,remove unnecessary method calls for hidden fields, 
*                  change of conditional code in validate method.
* -------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  INROLK  040129  Bug Id 40910, changed Sql in finishForm();
*  INROLK  040311  Transefered Customer order lines before finish.Bug Id 40910.
*  THWILK  040324  Merge with SP1.
*  THWILK  040415  Call 114026,Modified prepareForm(). 
*  THWILK  040421  Call 114026,Modified prepareForm() and predefine() in order to give priority to employee-craft relationship. 
*  ARWILK  040723  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  NIJALK  040824  Call 117157, Modified getContent(). Added method CheckAllMaterialIssued().
*  ARWILK  041111  Replaced getContents with printContents.
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info
*  NIJALK  050121  Modified LOVs and validations to ITEM1_ORG_CODE,ROLE_CODE and EMP_NO.
*  NIJALK  060111  Changed DATE format to compatible with data formats in PG19.
*  NIJALK  060117  Call 131053: Modified setFlagVal() and checkMandato(),adjust(),run().Added method CheckAllMaterialIssued1(). 
*  NIJALK  060227  Call 135095: Modified validation to CATALOG_NO.
* ----------------------------------------------------------------------------
*  AMNILK  060727  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070404  Modified Predefine() to remove the extra line shown at runtime
*  ASSALK  070424  Corrected APP_PATH tag to COOKIE_PATH.
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  CHANLK  071112  Bug 69080, Change prepareITEM.
*  SHAFLK  080630  Bug 75395, Modified finishForm().
*  CHARLK  090812  Bug 85103, Removed the duplication of DUMMY_REAL_S_DATE in okFind() and added DUMMY_REAL_F_DATE.
*  SUIJLK  090922  Bug 81023, Modified deleteITEM, prepareITEM, finishForm
*  SHAFLK  100319  Bug 89366, Modified finishForm() and prepareITEM().
*  NIJALK  100719  Bug 89399, Modified printContents().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ReportInWorkOrderWiz extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ReportInWorkOrderWiz");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPForm frm;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPCommandBar itembar;
	private ASPTable itemtbl;
	private ASPBlockLayout itemlay;

	private ASPBlock BlkSympt;
	private ASPRowSet RowsetSympt;

	private ASPBlock BlkCause;
	private ASPRowSet RowsetCause;

	private ASPBlock BlkClass;
	private ASPRowSet RowsetClass;

	private ASPBlock BlkType;
	private ASPRowSet RowsetType;

	private ASPBlock BlkDisc;
	private ASPRowSet RowsetDisc;

	private ASPBlock BlkPerAct;
	private ASPRowSet RowsetPerAct;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String empno;
	private String objid1;
	private String objversion1;
	private String objid;
	private String objversion;
	private String sympt;
	private String caus;
	private String clas;
	private String typ;
	private String disc;
	private String peract;
	private String cancelFlag;
	private String unissue;
	private ASPTransactionBuffer secBuff;
	private ASPCommand cmd;
	private ASPBuffer buf;
	private String securityOk;
	private ASPQuery q;
	private String woNo;
	private boolean errFlag;
	private ASPBuffer data;
	private String horgcode;
	private int introFlag;  
	private int win2Flag;  
	private int win4Flag;  
	private int win4GoFlag;
	private boolean showHtmlPart;
	private String url;
	private boolean toDefaultPage;
	private String calling_url;
	private boolean showAllTimeReports;
	private String sHeaderAttr;
	private String sWoNo;
	private ASPBuffer sTempRowBuff; 
	private ASPBuffer row;
        private double chkAllMat;

	//===============================================================
	// Construction 
	//===============================================================
	public ReportInWorkOrderWiz(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();     

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		frm = mgr.getASPForm();
		ctx = mgr.getASPContext();

		introFlag = ctx.readNumber("INTROFLAG",1);
		win2Flag = ctx.readNumber("WIN2FLAG",0);    
		win4Flag = ctx.readNumber("WIN4FLAG",0);
		win4GoFlag = ctx.readNumber("WIN4GOFLAG",0);    
		empno = ctx.readValue("EMP","");
		objid1 = ctx.readValue("OBJID1","");
		objversion1 = ctx.readValue("OBJVERSION1","");
		sympt = ctx.readValue("SYMPT","");
		caus = ctx.readValue("CAUS","");
		clas = ctx.readValue("CLAS","");
		typ = ctx.readValue("TYP","");
		disc = ctx.readValue("DISC_DISPLAY","");
		peract = ctx.readValue("PERACT",""); 
		cancelFlag =  ctx.readValue("CANCELFLAG","FALSE"); 
		showHtmlPart = ctx.readFlag("CTSSHHTML",true); 
		toDefaultPage = ctx.readFlag("CTXTODEF",false);
		showAllTimeReports = ctx.readFlag("CTXSHTIMREP",false);
		sHeaderAttr = ctx.readValue("CTXHEADER",null);
		sWoNo = ctx.readValue("CTXPREVHEADER",""); 
                chkAllMat = ctx.readNumber("CTXCHKALLMAT");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
		{
			if ("ReportInWorkOrderWiz".equals(mgr.readValue("WNDFLAG","")))
			{
				showHtmlPart = false;
			}
			else if ("ReportInWorkOrderWizFromPortal".equals(mgr.readValue("WNDFLAG","")))
			{
				showHtmlPart = false;
				toDefaultPage = true;

				calling_url = ctx.getGlobal("CALLING_URL");
				ctx.setGlobal("CALLING_URL",calling_url);
			}
		}
		else if (mgr.dataTransfered())
			okFind();
		else if (mgr.buttonPressed("PREVIOUS"))
			previousForm();
		else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
			nextForm();
		else if ("AAAA".equals(mgr.readValue("BUTTONFSH1")))
			finishForm();
		else if ("AAAA".equals(mgr.readValue("BUTTONFSH")))
			finishForm();
		else if (mgr.buttonPressed("CANCEL"))
			cancelForm();
		else if (mgr.buttonPressed("GETTIMEREP"))
			okFindITEM();
		else if (mgr.buttonPressed("ADDTREP"))
			prepareITEM(false);
		else if (mgr.buttonPressed("DELETECURRREP"))
			deleteITEM();
		else if (mgr.buttonPressed("PREVTIMEREP"))
			backwardITEM();
		else if (mgr.buttonPressed("NEXTTIMEREP"))
			forwardITEM();
		else if ("1".equals(mgr.readValue("__GETREC")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else
		{
			prepareForm();
		}

		adjust();

		ctx.writeNumber("INTROFLAG",introFlag);
		ctx.writeNumber("WIN2FLAG",win2Flag);
		ctx.writeNumber("WIN4FLAG",win4Flag);
		ctx.writeNumber("WIN4GOFLAG",win4GoFlag);
		ctx.writeValue("EMP",empno);
		ctx.writeValue("OBJID1",objid1);
		ctx.writeValue("OBJVERSION1",objversion1);
		ctx.writeValue("SYMPT",sympt);
		ctx.writeValue("CAUS",caus);
		ctx.writeValue("CLAS",clas);
		ctx.writeValue("TYP",typ);                
		ctx.writeValue("DISC_DISPLAY",disc);
		ctx.writeValue("PERACT",peract);
		ctx.writeValue("CANCELFLAG",cancelFlag);
		ctx.writeFlag("CTSSHHTML",showHtmlPart);
		ctx.writeFlag("CTXTODEF",toDefaultPage);
		ctx.writeFlag("CTXSHTIMREP",showAllTimeReports);
		ctx.writeValue("CTXHEADER",sHeaderAttr);
		ctx.writeValue("CTXPREVHEADER",sWoNo);
                ctx.writeNumber("CTXSHTIMREP",chkAllMat);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// Parameter types are not recognized and set them to String. Declare type[s] for (method,ref)
	public boolean  checksec( String method,int ref) 
	{

		ASPManager mgr = getASPManager();


		String isSecure[] = new String[7];

		isSecure[ref] = "false" ; 
		String splitted[] = split(method,".");

		String first = splitted[0].toString();
		String Second = splitted[1].toString();

		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery(first,Second);

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists(method))
		{
			isSecure[ref] = "true";
			return true; 
		}
		else
			return false;
	}   

	public void assign()
	{

		eval(headblk.generateAssignments());
		eval(itemblk.generateAssignments());
	}   

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = "";  
		String txt=""; 
		String colAmount1= "";
		String sBuyQtyDue= "";
		double colAmount   = 0;
		double colSalesPartCost = 0;
		String scolSalesPartCost="";
		double colQty   = 0;
		double colAmountSales=0;
		String scolAmountSales="";
		double nSalesPriceAmount=0;
		double colListPrice=0;
		String scolListPrice="";
		double colDiscount=0;
		double numSalesPartCost=0;
		double nqty1=0;
		String roleSecOk=null;
		String colCatalogNo="";
		String colSalesPartDesc="";
		String colSalesPart="";
		String colLineDescription="";
		String colOrgCode="";
		String colCmnt="";
		String salesSecOk="";
		double nBuyQtyDue=0;
		String colPriceListNo="";
		String colCatalogDesc="";
		String strConcatDesc="";

		val = mgr.readValue("VALIDATE");

		if ("CONTRACT".equals(val))
		{
			cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
			cmd.addParameter("CONTRACT");

			trans = mgr.validate(trans);

			String tpcompany = trans.getValue("GETCOMP/DATA/COMPANY");

			txt = (mgr.isEmpty(tpcompany) ? "" :tpcompany) + "^" ;
			mgr.responseWrite(txt);
		}

		else if ("DUMMY_REAL_S_DATE".equals(val))
		{
			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.readValue("DUMMY_REAL_S_DATE"));
			mgr.responseWrite(buf.getFieldValue("DUMMY_REAL_S_DATE") +"^" );   
			mgr.endResponse();
		}

		else if ("DUMMY_REAL_F_DATE".equals(val))
		{
			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.readValue("DUMMY_REAL_F_DATE"));
			mgr.responseWrite(buf.getFieldValue("DUMMY_REAL_F_DATE") +"^" );   
			mgr.endResponse();
		}

		else if ("CRE_DATE".equals(val))
		{
			buf = mgr.newASPBuffer();
			buf.addFieldItem("CRE_DATE",mgr.readValue("CRE_DATE"));
			mgr.responseWrite(buf.getFieldValue("CRE_DATE") +"^" );  
			mgr.endResponse();
		}

		else if ("REPORTED_BY".equals(val))
		{
			cmd = trans.addCustomFunction("GETNAME","PERSON_INFO_API.Get_Name","NAME");
			cmd.addParameter("REPORTED_BY");

			cmd = trans.addCustomFunction("EMPID","COMPANY_EMP_API.Get_Max_Employee_Id","EMPNO");
			cmd.addParameter("COMPANY");
			cmd.addParameter("REPORTED_BY");

			trans = mgr.validate(trans);

			String tpname = trans.getValue("GETNAME/DATA/NAME");
			String tpempno = trans.getValue("EMPID/DATA/EMPNO");
			empno = tpempno;
			String dRegDate = mgr.readValue("REG_DATE");

			txt = (mgr.isEmpty(tpname ) ? "" :tpname ) + "^" + (mgr.isEmpty(tpempno) ? "" :tpempno)+ "^" + (mgr.isEmpty(dRegDate ) ? "" :dRegDate )+ "^";

			mgr.responseWrite(txt); 
		}
		else if ("EMP_NO".equals(val))
		{
			String strCatalogNo = "";
			String strCatalogDesc = "";

                        String reqstr = null;
                        int startpos = 0;
                        int endpos = 0;
                        int i = 0;
                        String ar[] = new String[2];
                        String emp_id = "";
                        String strOrgCode = "";
                        String strRoleCode = "";
                        String sOrgCode = mgr.readValue("ITEM_ORG_CODE");
                        String sRoleCode = mgr.readValue("ROLE_CODE");
			String sOrgContract = mgr.readValue("ITEM_CONTRACT");

			String new_sign_id = mgr.readValue("EMP_NO","");

			if (new_sign_id.indexOf("^",0)>0)
			{
				for (i=0 ; i<2; i++)
				{
					endpos = new_sign_id.indexOf("^",startpos);
					reqstr = new_sign_id.substring(startpos,endpos);
					ar[i] = reqstr;
					startpos= endpos+1;
				}
				emp_id = ar[1];
			}
			else
                            emp_id = new_sign_id;

			securityOk = "";
			secBuff = mgr.newASPTransactionBuffer();
			secBuff.addSecurityQuery("SALES_PART");

                        secBuff = mgr.perform(secBuff);

			if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
				securityOk = "TRUE";
                        trans.clear();

                        cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM_CONTRACT");
                        cmd.addParameter("ITEM_COMPANY");
                        cmd.addParameter("EMP_NO",emp_id);

                        trans = mgr.validate(trans);

                        if (mgr.isEmpty(sOrgContract))
                            sOrgContract = trans.getValue("EMPORGCONT/DATA/CONTRACT");
                        
                        trans.clear();

			cmd = trans.addCustomFunction("PARA1", "Employee_Role_API.Get_Default_Role", "ROLE_CODE" );
			cmd.addParameter("ITEM_COMPANY",mgr.readValue("ITEM_COMPANY"));
			cmd.addParameter("EMP_NO",emp_id);

			cmd = trans.addCustomFunction("PARA2", "Role_API.Get_Description", "CMNT" );
                        if (mgr.isEmpty(sRoleCode))
                            cmd.addReference("ROLE_CODE", "PARA1/DATA");
                        else
                            cmd.addParameter("ROLE_CODE",sRoleCode);

			cmd = trans.addCustomFunction("PARA3", "Employee_API.Get_Organization", "DEPART" );
			cmd.addParameter("ITEM_COMPANY");
			cmd.addParameter("EMP_NO",emp_id);

			cmd = trans.addCustomFunction("PARA4", "Company_Emp_API.Get_Person_Id", "EMP_SIGNATURE" );
			cmd.addParameter("ITEM_COMPANY");
			cmd.addParameter("EMP_NO",emp_id);

			cmd = trans.addCustomFunction("PARA5", "Person_Info_API.Get_Name", "ITEM_NAME" );
			cmd.addReference("EMP_SIGNATURE", "PARA4/DATA");

			if ("TRUE".equals(securityOk))
			{

                                cmd = trans.addCustomFunction("GETWOSITE","Active_Work_Order_API.Get_Contract","WOSITE");
                                cmd.addParameter("ITEM_WO_NO",mgr.readValue("ITEM_WO_NO"));

				cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
                                if (mgr.isEmpty(sRoleCode))
                                    cmd.addReference("ROLE_CODE", "PARA1/DATA");
                                else
                                    cmd.addParameter("ROLE_CODE",sRoleCode);
				cmd.addReference("WOSITE","GETWOSITE/DATA");

				cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
				cmd.addReference("WOSITE","GETWOSITE/DATA");
				cmd.addReference("CATALOG_NO", "CATALO/DATA");
			}

			trans = mgr.validate(trans);

			if ("TRUE".equals(securityOk))
			{
				strCatalogNo = trans.getValue("CATALO/DATA/CATALOG_NO");
				strCatalogDesc= trans.getValue("CATDESC/DATA/SALESPARTCATALOGDESC");
			}
			else
			{
				strCatalogNo = "";
				strCatalogDesc = "";
			}
			String strDesc = trans.getValue("PARA2/DATA/CMNT");

                        if (mgr.isEmpty(sRoleCode))
                            strRoleCode = trans.getValue("PARA1/DATA/ROLE_CODE");        
                        else
                            strRoleCode = sRoleCode;

                        if (mgr.isEmpty(sOrgCode))
                            strOrgCode = trans.getValue("PARA3/DATA/DEPART");
                        else
                            strOrgCode = sOrgCode;

			String strSig = trans.getValue("PARA4/DATA/EMP_SIGNATURE");
			String strName = trans.getValue("PARA5/DATA/NAME");
			strConcatDesc = strName + "," + strOrgCode + "("+ strDesc +")"; 

			txt = (mgr.isEmpty(strRoleCode)? "" : strRoleCode) +"^"+ (mgr.isEmpty(strSig)? "" : strSig) +"^"+ (mgr.isEmpty(strName)? "" : strName) +"^"+ (mgr.isEmpty(strOrgCode)? "" : strOrgCode) +"^"+ (mgr.isEmpty(strConcatDesc)? "" : strConcatDesc) +"^"+ (mgr.isEmpty(strCatalogNo)? "" : strCatalogNo) +"^"+ (mgr.isEmpty(strCatalogDesc)? "" : strCatalogDesc) +"^" +
                              (mgr.isEmpty(emp_id)? "" : emp_id) +"^"+(mgr.isEmpty(sOrgContract)? "" : sOrgContract) +"^";

			mgr.responseWrite(txt);
			mgr.endResponse(  );
		}

		else if ("ITEM_CONTRACT".equals(val))
		{
			cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
			cmd.addParameter("ITEM_CONTRACT", mgr.readValue("ITEM_CONTRACT"));

			trans = mgr.validate(trans);

			String tpitemcompany = trans.getValue("GETCOMP/DATA/COMPANY");

			txt = (mgr.isEmpty(tpitemcompany) ? "" :tpitemcompany);

			mgr.responseWrite(txt);
		}
		else if ("ITEM_ORG_CODE".equals(val))
		{
			securityOk = "";

                        String reqstr = null;
                        int startpos = 0;
                        int endpos = 0;
                        int i = 0;
                        String ar[] = new String[6];
			String colOrgContract = "";
    
                        String new_org_code = mgr.readValue("ITEM_ORG_CODE","");
    
                        if (new_org_code.indexOf("^",0)>0)
                        {
                            if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
                            {
                                for (i=0 ; i<5; i++)
                                {
                                        endpos = new_org_code.indexOf("^",startpos);
                                        reqstr = new_org_code.substring(startpos,endpos);
                                        ar[i] = reqstr;
                                        startpos= endpos+1;
                                }
                                colOrgCode = ar[3];
                                colOrgContract = ar[4];
                            }
                            else
                            {
                                if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
                                {
                                    for (i=0 ; i<4; i++)
                                    {
                                            endpos = new_org_code.indexOf("^",startpos);
                                            reqstr = new_org_code.substring(startpos,endpos);
                                            ar[i] = reqstr;
                                            startpos= endpos+1;
                                    }
                                    colOrgCode = ar[2];
                                    colOrgContract = ar[3];
                                }
                                else
                                {
                                    for (i=0 ; i<2; i++)
                                    {
                                            endpos = new_org_code.indexOf("^",startpos);
                                            reqstr = new_org_code.substring(startpos,endpos);
                                            ar[i] = reqstr;
                                            startpos= endpos+1;
                                    }
                                    colOrgCode = ar[0];
                                    colOrgContract = ar[1];
                                }
                            }
                        }
                        else
                        {
                            colOrgCode = mgr.readValue("ITEM_ORG_CODE");
                            colOrgContract = mgr.readValue("ITEM_CONTRACT"); 
                        }
			secBuff = mgr.newASPTransactionBuffer();
			secBuff.addSecurityQuery("SALES_PART");

			secBuff = mgr.perform(secBuff);

			if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
				securityOk = "TRUE";

			if ("TRUE".equals(securityOk) &&   mgr.isEmpty(mgr.readValue("ROLE_CODE")))
			{
				cmd = trans.addCustomFunction("DEFCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
				cmd.addParameter("ITEM_CONTRACT",colOrgContract);
				cmd.addParameter("ITEM_ORG_CODE",colOrgCode);

				cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
				cmd.addParameter("ITEM_CONTRACT",colOrgContract);
				cmd.addReference("CATALOG_NO","DEFCAT/DATA");
			}
			else
			{
				if (!mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
				{
					cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
					cmd.addParameter("AMOUNT");
					cmd.addParameter("QTY");
					cmd.addParameter("ITEM_ORG_CODE",colOrgCode);
					cmd.addParameter("ROLE_CODE");
					cmd.addParameter("ITEM_CONTRACT",colOrgContract);
                                        cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                                        cmd.addParameter("DUMMY","TRUE");
				}

				if (!mgr.isEmpty(colOrgCode))
				{
					cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
					cmd.addParameter("ROLE_CODE");
				}
				else
				{
					cmd = trans.addCustomFunction("GETORGCODE","Active_Work_Order_API.Get_Org_Code","ORG_CODE");
					cmd.addParameter("ITEM_WO_NO");
				}
			}

			trans = mgr.validate(trans);

			if ("TRUE".equals(securityOk) &&   mgr.isEmpty(mgr.readValue("ROLE_CODE")))
			{
				colSalesPart       = trans.getValue("DEFCAT/DATA/CATALOG_NO");
				colLineDescription = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
				colCmnt            = mgr.readValue("CMNT");

				colAmount       = mgr.readNumberValue("AMOUNT");
				if (isNaN(colAmount))
					colAmount=0;
				colAmount1 = mgr.getASPField("AMOUNT").formatNumber(colAmount);                                   
			}
			else
			{
				colSalesPart       = mgr.readValue("CATALOG_NO");
				colLineDescription = mgr.readValue("SALESPARTCATALOGDESC");

				if (!mgr.isEmpty(colOrgCode) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
					colAmount1 = trans.getBuffer("CALAMOUNT/DATA").getFieldValue("AMOUNT");
				else if (!mgr.isEmpty(mgr.readValue("QTY")))
				{
					colAmount  = mgr.readNumberValue("AMOUNT");
					if (isNaN(colAmount))
						colAmount=0;
					colAmount1  = mgr.getASPField("AMOUNT").formatNumber(colAmount);
				}
				else if (mgr.isEmpty(mgr.readValue("QTY")))
				{
					colAmount = 0;    
					colAmount1 = mgr.getASPField("AMOUNT").formatNumber(colAmount);
				}
				if (!mgr.isEmpty(colOrgCode))
				{
					colCmnt    = mgr.readValue("ITEM_NAME")+ ","+ colOrgCode+"("+ trans.getValue("ROLEDESC/DATA/CMNT")+")";
				}
				else
				{
					colOrgCode = trans.getValue("GETORGCODE/DATA/ORG_CODE");                
					colCmnt    = mgr.readValue("ITEM_NAME")+ "," + colOrgCode;
				}    
			}                                                                                       
			if (mgr.isEmpty(colAmount1))
				colAmount1 = mgr.getASPField("AMOUNT").formatNumber(0);

			txt = (mgr.isEmpty(colOrgCode)?"":colOrgCode)+"^"+ colAmount1 +"^"+ colCmnt +"^"+ (mgr.isEmpty(colSalesPart)?"":colSalesPart) +"^"+ (mgr.isEmpty(colLineDescription)?"":colLineDescription) +"^"+(mgr.isEmpty(colOrgContract)?"":colOrgContract) +"^" ;
			mgr.responseWrite(txt);
		}
		else if ("ROLE_CODE".equals(val))
		{
			securityOk = "";
			salesSecOk = "";
			secBuff = mgr.newASPTransactionBuffer();

                        String reqstr = null;
                        int startpos = 0;
                        int endpos = 0;
                        int i = 0;
                        String ar[] = new String[6];

			String colRoleCode = "";
			String colOrgContract = "";
                        String new_role_code = mgr.readValue("ROLE_CODE","");
    
                        if (new_role_code.indexOf("^",0)>0)
                        {
                            if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
                            {
                                for (i=0 ; i<5; i++)
                                {
                                        endpos = new_role_code.indexOf("^",startpos);
                                        reqstr = new_role_code.substring(startpos,endpos);
                                        ar[i] = reqstr;
                                        startpos= endpos+1;
                                }
                                colRoleCode = ar[2];
                                colOrgContract = ar[4];
                            }
                            else
                            {
                                if (!mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")))
                                {
                                    for (i=0 ; i<5; i++)
                                    {
                                            endpos = new_role_code.indexOf("^",startpos);
                                            reqstr = new_role_code.substring(startpos,endpos);
                                            ar[i] = reqstr;
                                            startpos= endpos+1;
                                    }
                                    colRoleCode = ar[2];
                                    colOrgContract = ar[4];
                                }
                                else
                                {
                                    for (i=0 ; i<2; i++)
                                    {
                                            endpos = new_role_code.indexOf("^",startpos);
                                            reqstr = new_role_code.substring(startpos,endpos);
                                            ar[i] = reqstr;
                                            startpos= endpos+1;
                                    }
                                    colRoleCode = ar[0];
                                    colOrgContract = ar[1];
                                }
                            }
                        }
                        else
                        {
                            colRoleCode = mgr.readValue("ROLE_CODE");
                            colOrgContract = mgr.readValue("ITEM_CONTRACT"); 
                        }

			secBuff.addSecurityQuery("SALES_PART");
			secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
			secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

			secBuff = mgr.perform(secBuff);

			if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
				salesSecOk = "TRUE";

			if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
				securityOk = "TRUE";

			if ("TRUE".equals(securityOk))
			{
				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
					nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
				else
					nBuyQtyDue = mgr.readNumberValue("QTY");
				if (isNaN(nBuyQtyDue))
					nBuyQtyDue = 0;
				if (toDouble(nBuyQtyDue) == 0)
					nBuyQtyDue = 1;
				sBuyQtyDue = mgr.getASPField("QTY").formatNumber(nBuyQtyDue);
				cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE","");
				cmd.addParameter("SALE_PRICE","");
				cmd.addParameter("DISCOUNT","");
				cmd.addParameter("CURRENCY_RATE","");
				cmd.addParameter("ITEM_CONTRACT",colOrgContract);
				cmd.addParameter("CATALOG_NO");
				cmd.addParameter("CUSTOMER_NO");
				cmd.addParameter("ITEM_AGREEMENT_ID");
				cmd.addParameter("PRICE_LIST_NO");
				cmd.addParameter("QTY1",sBuyQtyDue);
			}

			if (!mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(colRoleCode))
			{
				cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
				cmd.addParameter("AMOUNT");
				cmd.addParameter("QTY");
				cmd.addParameter("ITEM_ORG_CODE");
				cmd.addParameter("ROLE_CODE",colRoleCode);
				cmd.addParameter("ITEM_CONTRACT",colOrgContract);
                                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                                cmd.addParameter("DUMMY","TRUE");
			}

			colAmount = mgr.readNumberValue("AMOUNT");
			if (isNaN(colAmount))
				colAmount=0;

			if (!mgr.isEmpty(colRoleCode))
			{
				cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
				cmd.addParameter("ROLE_CODE",colRoleCode);

				if ("TRUE".equals(salesSecOk))
				{
                                        cmd = trans.addCustomFunction("GETWOSITE","Active_Work_Order_API.Get_Contract","WOSITE");
                                        cmd.addParameter("ITEM_WO_NO",mgr.readValue("ITEM_WO_NO"));

					cmd = trans.addCustomFunction("DEFCATNO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
					cmd.addParameter("ROLE_CODE",colRoleCode);
					cmd.addReference("WOSITE","GETWOSITE/DATA");

					cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
					cmd.addReference("WOSITE","GETWOSITE/DATA");
					cmd.addReference("CATALOG_NO","DEFCATNO/DATA");
				}
				else
				{
					if (!mgr.isEmpty(mgr.readValue("CATALOG_NO")))
					{
						if (mgr.isEmpty(mgr.readValue("QTY")))
							colAmount = 0;
						else
						{
							colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");

							colQty = mgr.readNumberValue("QTY");

							colAmount = colSalesPartCost * colQty;
						}
					}
				}        
			}

			colAmount1 = mgr.getASPField("AMOUNT").formatNumber(colAmount);

			trans = mgr.validate(trans);

			if ("TRUE".equals(securityOk))
			{

				colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
				if (isNaN(colListPrice))colListPrice =0;
				colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
				colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
				if (isNaN(colDiscount))colDiscount =0;
				nqty1 =  trans.getNumberValue("PRICEINFO/DATA/QTY1");
				if (isNaN(nqty1))	nqty1=0;

				colAmountSales = colListPrice * nqty1;

				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
				{
					nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY_TO_INVOICE");
					colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
				}
				else
				{
					if (!mgr.isEmpty(mgr.readValue("QTY")))
					{
						nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY");
						colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
					}
					else
						colAmountSales		= 0;
				}

				colListPrice   = trans.getBuffer("PRICEINFO/DATA").getNumberValue("BASE_PRICE");
			}
			else
			{
				colListPrice = mgr.readNumberValue("LIST_PRICE");
				if (isNaN(colListPrice))
					colListPrice=0;

				colAmountSales = mgr.readNumberValue("AMOUNTSALES");
				if (isNaN(colAmountSales))
					colAmountSales=0;

				colPriceListNo = mgr.readValue("PRICE_LIST_NO","");

			}

			if (!mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(colRoleCode))
				colAmount1 = trans.getBuffer("CALAMOUNT/DATA").getFieldValue("AMOUNT");

			if (!mgr.isEmpty(colRoleCode))
			{
				colCmnt = mgr.readValue("ITEM_NAME")+ ","+ mgr.readValue("ITEM_ORG_CODE")+"("+ trans.getValue("ROLEDESC/DATA/CMNT")+")"; 

				if ("TRUE".equals(salesSecOk))
				{
					colCatalogNo   = trans.getValue("DEFCATNO/DATA/CATALOG_NO");
					colCatalogDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
				}
				else
				{
					colCatalogNo   = mgr.readValue("CATALOG_NO");
					colCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC");
				}
			}
			else
			{
				colCatalogNo   = mgr.readValue("CATALOG_NO");
				colCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC");
				colCmnt        = mgr.readValue("ITEM_NAME") +","+ mgr.readValue("ITEM_ORG_CODE");
			} 

                        if (mgr.isEmpty(colCatalogNo))
                        {
                            trans.clear();
                            cmd = trans.addCustomFunction("DEFCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
                            cmd.addParameter("ITEM_CONTRACT",colOrgContract);
                            cmd.addParameter("ITEM_ORG_CODE",mgr.readValue("ITEM_ORG_CODE"));

                            cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                            cmd.addParameter("ITEM_CONTRACT",colOrgContract);
                            cmd.addReference("CATALOG_NO","DEFCAT/DATA");

                            trans = mgr.validate(trans);

                            colCatalogNo = trans.getValue("DEFCAT/DATA/CATALOG_NO");
                            colCatalogDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
                            colCmnt        = mgr.readValue("ITEM_NAME") +","+ mgr.readValue("ITEM_ORG_CODE");
                        }

			scolListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
			scolAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

			txt = scolListPrice +"^"+ (mgr.isEmpty(colPriceListNo) ? "": colPriceListNo)+ "^"+ colAmount1 +"^"+ scolAmountSales +"^"+ colCmnt +"^"+ (mgr.isEmpty(colCatalogNo)?"":colCatalogNo) +"^"+ (mgr.isEmpty(colCatalogDesc)?"":colCatalogDesc) +"^"+
                            (mgr.isEmpty(colRoleCode)?"":colRoleCode) +"^" +(mgr.isEmpty(colOrgContract)?"":colOrgContract) +"^";

			mgr.responseWrite(txt);
		}
		else if ("CATALOG_NO".equals(val))
		{
			securityOk = "";
			salesSecOk = "";
			secBuff = mgr.newASPTransactionBuffer();
			secBuff.addSecurityQuery("SALES_PART");
			secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
			secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

			secBuff = mgr.perform(secBuff);

			if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
				salesSecOk = "TRUE";

			if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
				securityOk = "TRUE";

			if ("TRUE".equals(securityOk))
			{

				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
					nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
				else
					nBuyQtyDue = mgr.readNumberValue("QTY");
				if (isNaN(nBuyQtyDue))
					nBuyQtyDue=0;
				if (toDouble(nBuyQtyDue) == 0)
					nBuyQtyDue = 1;

				sBuyQtyDue = mgr.getASPField("QTY").formatNumber(nBuyQtyDue);

				cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE");
				cmd.addParameter("SALE_PRICE","");
				cmd.addParameter("DISCOUNT");
				cmd.addParameter("CURRENCY_RATE");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("CATALOG_NO");
				cmd.addParameter("CUSTOMER_NO");
				cmd.addParameter("ITEM_AGREEMENT_ID");
				cmd.addParameter("PRICE_LIST_NO");
				cmd.addParameter("QTY1",sBuyQtyDue);
			}

			cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
			cmd.addParameter("ROLE_CODE");

			if ("TRUE".equals(salesSecOk))
			{
				cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("CATALOG_NO");

                                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST1" );
                                cmd.addParameter("ITEM_ORG_CODE");
                                cmd.addParameter("ROLE_CODE");
                                cmd.addParameter("ITEM_CONTRACT");
                                cmd.addParameter("CATALOG_NO");
                                cmd.addParameter("ITEM_CONTRACT");
                                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                                cmd.addParameter("DUMMY","TRUE");
			}
			else
			{
                                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST1" );
                                cmd.addParameter("ITEM_ORG_CODE");
                                cmd.addParameter("ROLE_CODE");
                                cmd.addParameter("ITEM_CONTRACT");
                                cmd.addParameter("DUMMY","");
                                cmd.addParameter("ITEM_CONTRACT");
                                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                                cmd.addParameter("DUMMY","TRUE");
			}

			trans = mgr.validate(trans);

			if ("TRUE".equals(securityOk))
			{
				colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
				if (isNaN(colListPrice)) colListPrice=0;
				colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
				colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
				if (isNaN(colDiscount))	colDiscount=0;
				nqty1 =  trans.getNumberValue("PRICEINFO/DATA/QTY1");
				if (isNaN(nqty1))	nqty1=0;

				colAmountSales = colListPrice * nqty1 ;

				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
				{
					nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY_TO_INVOICE");
					colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
				}
				else
				{
					if (!mgr.isEmpty(mgr.readValue("QTY")))
					{
						nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY");
						colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
					}
					else
						colAmountSales		= 0;
				}

				colListPrice   = trans.getBuffer("PRICEINFO/DATA").getNumberValue("BASE_PRICE");
			}
			else
			{
				if (mgr.isEmpty(mgr.readValue("LIST_PRICE")))
					colListPrice = 0;
				else
					colListPrice = mgr.readNumberValue("LIST_PRICE");

				if (mgr.isEmpty(mgr.readValue("AMOUNTSALES")))
					colAmountSales = 0;
				else
					colAmountSales	= mgr.readNumberValue("AMOUNTSALES");

				colPriceListNo = mgr.readValue("PRICE_LIST_NO","");

			}

			if ("TRUE".equals(salesSecOk))
				colSalesPartDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
			else
				colSalesPartDesc = mgr.readValue("SALESPARTCATALOGDESC");

			colCmnt = mgr.readValue("ITEM_NAME") +","+ mgr.readValue("ITEM_ORG_CODE") +"("+ trans.getValue("ROLEDESC/DATA/CMNT") +")"+ colSalesPartDesc;
                        
                        scolSalesPartCost = trans.getBuffer("CST/DATA").getFieldValue("COST1");
                        numSalesPartCost = trans.getNumberValue("CST/DATA/COST1");
                        if (isNaN(numSalesPartCost))
                            numSalesPartCost = 0;

			if (mgr.isEmpty(mgr.readValue("QTY")))
			{
				colAmount = 0;
				colAmount1 = mgr.getASPField("AMOUNT").formatNumber(colAmount);
			}
			else
				colAmount = numSalesPartCost * mgr.readNumberValue("QTY"); 

			colAmount1 = mgr.getASPField("AMOUNT").formatNumber(colAmount);

			scolListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
			scolAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

			txt = scolListPrice +"^"+ colAmount1 +"^"+ scolAmountSales +"^"+ (mgr.isEmpty(colPriceListNo) ? "": colPriceListNo)+ "^"+ colCmnt +"^"+ (mgr.isEmpty(colSalesPartDesc)?"":colSalesPartDesc) +"^"+ scolSalesPartCost +"^";

			mgr.responseWrite(txt);
		}
		else if ("QTY".equals(val))
		{
			salesSecOk = "";
			roleSecOk  = "";
			securityOk = "";
			secBuff = mgr.newASPTransactionBuffer();
			secBuff.addSecurityQuery("SALES_PART");
			secBuff.addSecurityQuery("ROLE_SALES_PART");
			secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
			secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

			secBuff = mgr.perform(secBuff);

			if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
				salesSecOk = "TRUE";

			if (secBuff.getSecurityInfo().itemExists("ROLE_SALES_PART"))
				roleSecOk = "TRUE";

			if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
				securityOk = "TRUE";

			if ("TRUE".equals(salesSecOk))
			{
				cmd = trans.addCustomFunction("PARTCOST","Sales_Part_API.Get_Cost","COST1");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("CATALOG_NO");

				cmd = trans.addCustomFunction("ROLECOST","Role_API.Get_Role_Cost","COST2");
				cmd.addParameter("ROLE_CODE");

				cmd = trans.addCustomFunction("ORGCOST","Organization_API.Get_Org_Cost","COST3");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("ITEM_ORG_CODE");        
			}
			else
			{
				cmd = trans.addCustomFunction("ROLECOST","Role_API.Get_Role_Cost","COST2");
				cmd.addParameter("ROLE_CODE");

				cmd = trans.addCustomFunction("ORGCOST","Organization_API.Get_Org_Cost","COST3");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("ITEM_ORG_CODE");                
			}

			if (!mgr.isEmpty(mgr.readValue("QTY")))
			{

				if ("TRUE".equals(roleSecOk) &&  ! mgr.isEmpty(mgr.readValue("CATALOG_NO")))
				{
					colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
					if (isNaN(colSalesPartCost)) colSalesPartCost=0;
					colQty = mgr.readNumberValue("QTY");
					if (isNaN(colQty)) colQty =0;

					colAmount = colSalesPartCost * colQty;
					colAmount1 = mgr.getASPField("AMOUNT").formatNumber(colAmount);
				}
				else
				{
					cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
					cmd.addParameter("AMOUNT");
					cmd.addParameter("QTY");
					cmd.addParameter("ITEM_ORG_CODE");
					cmd.addParameter("ROLE_CODE");
					cmd.addParameter("ITEM_CONTRACT");
                                        cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                                        cmd.addParameter("DUMMY","TRUE");
				}
			}
			if ("TRUE".equals(securityOk))
			{
				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
					nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
				else
					nBuyQtyDue = mgr.readNumberValue("QTY");
				if (isNaN(nBuyQtyDue))
					nBuyQtyDue=0;
				if ((int)nBuyQtyDue == 0)
					nBuyQtyDue = 1;
				sBuyQtyDue = mgr.getASPField("QTY").formatNumber(nBuyQtyDue);
				cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE");
				cmd.addParameter("SALE_PRICE","");
				cmd.addParameter("DISCOUNT");
				cmd.addParameter("CURRENCY_RATE");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("CATALOG_NO");
				cmd.addParameter("CUSTOMER_NO");
				cmd.addParameter("ITEM_AGREEMENT_ID");
				cmd.addParameter("PRICE_LIST_NO");
				cmd.addParameter("QTY1",sBuyQtyDue);
			}
			trans = mgr.validate(trans);

			scolSalesPartCost = "";
			if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
				scolSalesPartCost = trans.getBuffer("ROLECOST/DATA").getFieldValue("COST2");
			if (mgr.isEmpty(scolSalesPartCost) && !mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")))
				scolSalesPartCost = trans.getBuffer("ORGCOST/DATA").getFieldValue("COST3");
			if (mgr.isEmpty(scolSalesPartCost)  &&  ! mgr.isEmpty(mgr.readValue("CATALOG_NO"))  &&   "TRUE".equals(salesSecOk))
				scolSalesPartCost = trans.getBuffer("PARTCOST/DATA").getFieldValue("COST1");
			if (! mgr.isEmpty(mgr.readValue("QTY"))  && ("".equals(roleSecOk) ||   mgr.isEmpty(mgr.readValue("CATALOG_NO")) ))
				colAmount1 = trans.getBuffer("CALAMOUNT/DATA").getFieldValue("AMOUNT");


			if ("TRUE".equals(securityOk))
			{
				colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
				if (isNaN(colListPrice)) colListPrice=0;
				colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
				colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
				if (isNaN(colDiscount))	colDiscount=0;


				colAmountSales = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE") * trans.getNumberValue("PRICEINFO/DATA/QTY1");

				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
				{
					nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY_TO_INVOICE");
					colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
				}
				else
				{
					if (!mgr.isEmpty(mgr.readValue("QTY")))
					{
						nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY");
						colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
					}
					else
						colAmountSales		= 0;
				}
				colListPrice   = trans.getBuffer("PRICEINFO/DATA").getNumberValue("BASE_PRICE");
			}
			else
			{
				if (mgr.isEmpty(mgr.readValue("LIST_PRICE")))
					colListPrice = 0;
				else
					colListPrice = mgr.readNumberValue("LIST_PRICE");

				if (mgr.isEmpty(mgr.readValue("AMOUNTSALES")))
					colAmountSales = 0;
				else
					colAmountSales	= mgr.readNumberValue("AMOUNTSALES");

				colPriceListNo = mgr.readValue("PRICE_LIST_NO");
			}
			scolAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
			scolListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
			if (mgr.isEmpty(colAmount1))
				colAmount1 = mgr.getASPField("AMOUNT").formatNumber(0);
			if (mgr.isEmpty(scolSalesPartCost))
				scolSalesPartCost = mgr.getASPField("SALESPARTCOST").formatNumber(0);

			txt = scolListPrice +"^"+ scolAmountSales +"^"+ (mgr.isEmpty(colPriceListNo) ? "": colPriceListNo)+ "^"+ scolSalesPartCost +"^"+(mgr.isEmpty(colAmount1) ? "": colAmount1)+ "^";

			mgr.responseWrite(txt);
		}


		else if ("PRICE_LIST_NO".equals(val))
		{
			securityOk = "";
			secBuff = mgr.newASPTransactionBuffer();
			secBuff.addSecurityQuery("SALES_PART");
			secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
			secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

			secBuff = mgr.perform(secBuff);

			if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
				salesSecOk = "TRUE";

			if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
				securityOk = "TRUE";

			if ("TRUE".equals(securityOk))
			{
				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
					nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
				else
					nBuyQtyDue = mgr.readNumberValue("QTY");
				if (isNaN(nBuyQtyDue)) nBuyQtyDue=0;
				if ((int)nBuyQtyDue == 0) nBuyQtyDue = 1;
				sBuyQtyDue = mgr.getASPField("QTY").formatNumber(nBuyQtyDue);
				cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
				cmd.addParameter("BASE_PRICE");
				cmd.addParameter("SALE_PRICE","");
				cmd.addParameter("DISCOUNT");
				cmd.addParameter("CURRENCY_RATE");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("CATALOG_NO");
				cmd.addParameter("CUSTOMER_NO");
				cmd.addParameter("ITEM_AGREEMENT_ID");
				cmd.addParameter("PRICE_LIST_NO");
				cmd.addParameter("QTY1",sBuyQtyDue);
			}

			trans = mgr.validate(trans);

			if ("TRUE".equals(securityOk))
			{
				colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE"); 
				if (isNaN(colListPrice)) colListPrice=0;
				colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
				colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
				if (isNaN(colDiscount))	colDiscount=0;

				colAmountSales = colListPrice * trans.getNumberValue("PRICEINFO/DATA/QTY1");

				if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
				{
					nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY_TO_INVOICE");
					colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
				}
				else
				{
					if (!mgr.isEmpty(mgr.readValue("QTY")))
					{
						nSalesPriceAmount = colListPrice * mgr.readNumberValue("QTY");
						colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
					}
					else
						colAmountSales		= 0;
				}
				scolAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
				colListPrice   = trans.getBuffer("PRICEINFO/DATA").getNumberValue("BASE_PRICE");
			}
			else
			{
				if (mgr.isEmpty(mgr.readValue("LIST_PRICE")))
					colListPrice = 0;
				else
					colListPrice = mgr.readNumberValue("LIST_PRICE");

				colAmountSales = mgr.readNumberValue("AMOUNTSALES");
				if (isNaN(colAmountSales))
					colAmountSales=0;

				colPriceListNo = mgr.readValue("PRICE_LIST_NO");
			}

			String colcAgreement =  mgr.getASPField("AGREEMENT_PRICE_FLAG").formatNumber(1);

			double listPrice=0;
			double qty = 0;

			listPrice = mgr.readNumberValue("LIST_PRICE");
			if (isNaN(listPrice))
				listPrice =0;

			qty = mgr.readNumberValue("QTY");
			if (isNaN(qty))
				qty=0;

			colAmountSales = listPrice * qty;

			scolAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
			scolListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
			txt = scolListPrice +"^"+ scolAmountSales +"^"+ (mgr.isEmpty(colPriceListNo) ? "": colPriceListNo)+ "^"+ colcAgreement +"^";

			mgr.responseWrite(txt);
			mgr.endResponse();
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		else{
    		    //Bug 58216 Start
		    q.addWhereCondition("WO_NO = ?");
		    q.addParameter("WO_NO",mgr.readValue("WO_NO"));
		    //Bug 58216 End
		}
		q.includeMeta("ALL");

		woNo = mgr.readValue("WO_NO");

		q = trans.addQuery(BlkSympt);
		q.setOrderByClause("ERR_SYMPTOM");

		q = trans.addQuery(BlkCause);
		q.setOrderByClause("ERR_CAUSE");

		q = trans.addQuery(BlkClass);
		q.setOrderByClause("ERR_CLASS");

		q = trans.addQuery(BlkType);
		q.setOrderByClause("ERR_TYPE");         

		q = trans.addQuery(BlkDisc);
		q.setOrderByClause("ERR_DISCOVER_CODE");

		q = trans.addQuery(BlkPerAct);
		q.setOrderByClause("PERFORMED_ACTION_ID");


		mgr.submit(trans);

		if (headset.countRows() == 0)
			mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZNODAT: "+mgr.getASPField("WO_NO").getLabel()+ " " +woNo+ " does not exist"));
		else
		{
			sympt = headset.getRow().getValue("ERR_SYMPTOM");
			caus = headset.getRow().getValue("ERR_CAUSE");
			clas = headset.getRow().getValue("ERR_CLASS");
			typ = headset.getRow().getValue("ERR_TYPE");
			disc = headset.getRow().getValue("ERR_DISCOVER_CODE");
			peract = headset.getRow().getValue("PERFORMED_ACTION_ID");

			sTempRowBuff = headset.getRow();      

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.isEmpty(headset.getRow().getFieldValue("REAL_S_DATE"))?"":headset.getRow().getFieldValue("REAL_S_DATE"));                     
			sTempRowBuff.setValue("DUMMY_REAL_S_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"));                       
			buf = mgr.newASPBuffer();
			//Bug 85103 Start
			buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.isEmpty(headset.getRow().getFieldValue("REAL_F_DATE"))?"":headset.getRow().getFieldValue("REAL_F_DATE")); 
			sTempRowBuff.setValue("DUMMY_REAL_F_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"));
			//Bug 85103 End
			sTempRowBuff.setValue("DUMMY_WORK_DONE",mgr.isEmpty(headset.getRow().getValue("WORK_DONE"))?"":headset.getRow().getValue("WORK_DONE"));
			sTempRowBuff.setValue("DUMMY_PERFORMED_ACTION_LO",mgr.isEmpty(headset.getRow().getValue("PERFORMED_ACTION_LO"))?"":headset.getRow().getValue("PERFORMED_ACTION_LO"));                     
			sTempRowBuff.setValue("DUMMY_ERR_SYMPTOM",mgr.isEmpty(headset.getRow().getValue("ERR_SYMPTOM"))?"":headset.getRow().getValue("ERR_SYMPTOM"));
			sTempRowBuff.setValue("DUMMY_ERR_CAUSE",mgr.isEmpty(headset.getRow().getValue("ERR_CAUSE"))?"":headset.getRow().getValue("ERR_CAUSE"));
			sTempRowBuff.setValue("DUMMY_ERR_CLASS",mgr.isEmpty(headset.getRow().getValue("ERR_CLASS"))?"":headset.getRow().getValue("ERR_CLASS"));
			sTempRowBuff.setValue("DUMMY_ERR_TYPE",mgr.isEmpty(headset.getRow().getValue("ERR_TYPE"))?"":headset.getRow().getValue("ERR_TYPE"));
			sTempRowBuff.setValue("DUMMY_ERR_DISCOVER_CODE",mgr.isEmpty(headset.getRow().getValue("ERR_DISCOVER_CODE"))?"":headset.getRow().getValue("ERR_DISCOVER_CODE"));
			sTempRowBuff.setValue("DUMMY_PERFORMED_ACTION_ID",mgr.isEmpty(headset.getRow().getValue("PERFORMED_ACTION_ID"))?"":headset.getRow().getValue("PERFORMED_ACTION_ID"));

			headset.setRow(sTempRowBuff); 
		}             
	}   

	public void okFindITEM()
	{
		ASPManager mgr = getASPManager();

		errFlag = true;

		prepareITEM(true);

		if (errFlag)
		{
			win4Flag = 1;
			cmd = trans.addCustomFunction("ACCTYPE","Work_Order_Account_Type_API.Get_Client_Value","WORK_ORDER_ACCOUNT_TYPE");
			cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

			cmd = trans.addCustomFunction("COSTTYPE","Work_Order_Cost_Type_API.Get_Client_Value","WORK_ORDER_COST_TYPE");
			cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

			trans = mgr.perform(trans);

			String sAccountType = trans.getValue("ACCTYPE/DATA/WORK_ORDER_ACCOUNT_TYPE");
			String sCostType = trans.getValue("COSTTYPE/DATA/WORK_ORDER_COST_TYPE");    
			trans.clear();

			q = trans.addEmptyQuery(itemblk);
			//Bug 58216 Start
			q.addWhereCondition("WORK_ORDER_COST_TYPE = ?");
			q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = ?");
			q.addWhereCondition("WO_NO = ?");
			q.addParameter("WORK_ORDER_COST_TYPE",sCostType);
			q.addParameter("WORK_ORDER_ACCOUNT_TYPE",sAccountType);
			q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
			//Bug 58216 End
			q.includeMeta("ALL");
			mgr.submit(trans);

			if (itemset.countRows() == 0)
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZNODATA: No time reports available."));
				itemset.clear();
				trans.clear();
				prepareForm(); 
				showAllTimeReports = false;
			}
			else
			{
				itemlay.setLayoutMode(itemlay.CUSTOM_LAYOUT);
				showAllTimeReports = true;
			}
			win2Flag = 1;    
		}
	}


	public void prepare()
	{
		ASPManager mgr = getASPManager();

		itemset.last();
		errFlag = true;

		if (introFlag == 4)
		{
			if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZQTYNULL: The field [Hours] must have a value."));
				errFlag = false;
				itemset.first();
			}

			else if (mgr.isEmpty(itemset.getRow().getValue("ORG_CODE")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZORGNULL: The field [Maintenance Organization] must have a value."));
				errFlag = false;
				itemset.first();
			}
			else if (mgr.isEmpty(itemset.getRow().getValue("CONTRACT")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZSITENULL: The field [Site] must have a value."));
				errFlag = false;
				itemset.first();
			}
			else if (mgr.isEmpty(itemset.getRow().getValue("CRE_DATE")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZCRENULL: The field [Performed Date] must have a value."));
				errFlag = false;
				itemset.first();
			}
		}
		if (errFlag)
		{
			buf = itemset.getRow();

			cmd = trans.addCustomFunction("GETITNAME","Person_Info_API.Get_Name","ITEM_NAME");
			cmd.addParameter("SIGN",itemset.getRow().getValue("SIGN"));

			cmd = trans.addCustomFunction("GETSIG","Employee_API.Get_Signature","SIGN");
			cmd.addParameter("COMPANY",itemset.getRow().getValue("COMPANY"));
			cmd.addParameter("EMP_NO",itemset.getRow().getValue("EMP_NO"));

			trans = mgr.perform(trans);

			String tpname = trans.getValue("GETITNAME/DATA/ITEM_NAME");
			String tpsign = trans.getValue("GETSIG/DATA/SIGN");

			buf.setValue("SIGN",tpsign);
			buf.setValue("ITEM_NAME",tpname);
			buf.setValue("QTY","");
			buf.setValue("AMOUNT","");

			itemset.addRow(buf);
		}
		populateListBoxes();
		assign();
	}


	public void prepareITEM(boolean populateTimeReps)
	{
		ASPManager mgr = getASPManager();          

                cmd = trans.addCustomFunction("GETCUSTOMERNO", "ACTIVE_SEPARATE_API.Get_Customer_No","CUSTOMER_NO"); 
                cmd.addParameter("WO_NO",mgr.readValue("WO_NO"));

                trans = mgr.validate(trans);

                String sCustomerNo = trans.getValue("GETCUSTOMERNO/DATA/CUSTOMER_NO");
                		
                showAllTimeReports = false;

		if (introFlag == 4)
			itemset.changeRow();

		itemset.last();
		errFlag = true;

//		Start, Bug 69080
		if (introFlag == 4 && itemlay.isEditLayout())
		{
		        //Bug 89399, Start
			if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZQTYNULL: The field [Hours] must have a value."));
				errFlag = false;
				//itemset.first();
			}
			else if ("0".equals(itemset.getRow().getValue("QTY")))
			{
				 mgr.showAlert(mgr.translate("PCMWREPINWIZNOTVALIDQTY: Reported hours value for &1 is not allowed to be zero.", sWoNo));
				 errFlag = false;
			}
		        //Bug 89399, End

			else if (mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZORGNULL: The field [Maintenance Organization] must have a value."));
				errFlag = false;
				//itemset.first();
			}
			else if (mgr.isEmpty(mgr.readValue("ITEM_CONTRACT")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZSITENULL: The field [Site] must have a value."));
				errFlag = false;
				//itemset.first();
			}
			else if (mgr.isEmpty(itemset.getRow().getValue("CRE_DATE")))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZCRENULL: The field [Performed Date] must have a value."));
				errFlag = false;
				//itemset.first();
			}
                        else if ((mgr.isEmpty(itemset.getRow().getValue("CATALOG_NO"))) && !(mgr.isEmpty(sCustomerNo))) {

                                 mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZCATNULL: Customer Exist in the Work Order, The field [Sales Part] must have a value."));
                                 errFlag = false;
                        }
//			End, Bug 69080
		}

		if (errFlag)
		{
			// Save the previous record
			if (itemlay.isEditLayout())
			{
				trans.clear();

				cmd = trans.addCustomFunction("ACCTYPE","Work_Order_Account_Type_API.Get_Client_Value","WORK_ORDER_ACCOUNT_TYPE");
				cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

				cmd = trans.addCustomFunction("COSTTYPE","Work_Order_Cost_Type_API.Get_Client_Value","WORK_ORDER_COST_TYPE");
				cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

				cmd = trans.addCustomFunction("AUTHSIG","Maintenance_Configuration_API.Get_Auth_Def_Sign","SIGNATURE");

				cmd = trans.addCustomFunction("BOOKSTATUS","Work_Order_Book_Status_API.Get_Client_Value","BOOK_STATUS");
				cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

				trans = mgr.perform(trans);

				String sAccountType = trans.getValue("ACCTYPE/DATA/WORK_ORDER_ACCOUNT_TYPE");
				String sCostType = trans.getValue("COSTTYPE/DATA/WORK_ORDER_COST_TYPE");
				String sSignature = trans.getValue("AUTHSIG/DATA/SIGNATURE");
				String sBookStatus = trans.getValue("BOOKSTATUS/DATA/BOOK_STATUS");

				String sComment = itemset.getRow().getFieldValue("ITEM_NAME") + ", " + itemset.getRow().getFieldValue("ITEM_ORG_CODE") + " (" + itemset.getRow().getValue("ROLE_CODE") + ")" + ".";
				String sAttr = "";
				String empNo_ = mgr.readValue ("EMP_NO","");
				String orgCode_ = itemset.getRow().getFieldValue("ITEM_ORG_CODE");
				String roleCode_ = mgr.readValue ("ROLE_CODE","");
				String qtyInvoice_ =  mgr.getASPField ("QTY_TO_INVOICE").formatNumber (itemset.getRow ().getNumberValue("QTY_TO_INVOICE") );
				String empSignature_ =   mgr.readValue ("EMP_SIGNATURE","");
				String agreePriceFlag_ = itemset.getValue("AGREEMENT_PRICE_FLAG");
				if (mgr.isEmpty (agreePriceFlag_)) agreePriceFlag_ ="";
				String priceListNo_ = itemset.getRow().getValue("PRICE_LIST_NO");
				if (mgr.isEmpty (priceListNo_))	priceListNo_ ="";
				String listPrice_ =  itemset.getRow().getValue("LIST_PRICE") ;
				if (mgr.isEmpty (listPrice_))	listPrice_ ="";
				String catNo_ =   mgr.readValue ("CATALOG_NO","");

				//Bug id 81023, start - added CHECK_HR_REPORT_STATE
            sAttr = sAttr   + "CMNT" + (char)31 +sComment+ (char)30
						+ "EMP_NO" + (char)31 +empNo_+ (char)30
						+ "EMP_SIGNATURE" + (char)31 +empSignature_+ (char)30
						+ "ORG_CODE" + (char)31 +orgCode_+ (char)30
						+ "WO_NO" + (char)31 +sWoNo+ (char)30
						+ "WORK_ORDER_COST_TYPE" + (char)31 +sCostType+ (char)30
						+ "WORK_ORDER_ACCOUNT_TYPE" + (char)31 +sAccountType+ (char)30
						+ "CATALOG_NO" + (char)31 +catNo_+ (char)30
						+ "SIGNATURE" + (char)31 +sSignature+ (char)30
						+ "SIGNATURE_ID" + (char)31 +sSignature+ (char)30
						+ "WORK_ORDER_BOOK_STATUS" + (char)31 +sBookStatus+ (char)30
						+ "CONTRACT" + (char)31 +itemset.getRow().getValue("CONTRACT")+ (char)30
                                                + "CATALOG_CONTRACT" + (char)31 +itemset.getRow().getValue("CONTRACT")+ (char)30
						+ "COMPANY" + (char)31 +itemset.getRow().getValue("COMPANY")+ (char)30                              
						+ "CRE_DATE" + (char)31 +itemset.getRow().getValue("CRE_DATE")+ (char)30
						+ "LIST_PRICE" + (char)31 +listPrice_+ (char)30
						+ "ROLE_CODE" + (char)31 +roleCode_+ (char)30
						+ "QTY" + (char)31 +itemset.getRow().getValue("QTY")+ (char)30
						+ "QTY_TO_INVOICE" + (char)31+qtyInvoice_+ (char)30
						+ "AGREEMENT_PRICE_FLAG" + (char)31 +agreePriceFlag_+ (char)30
						+ "PRICE_LIST_NO" + (char)31 +priceListNo_+ (char)30                                    
						+ "AMOUNT" + (char)31 +itemset.getRow().getValue("AMOUNT")+ (char)30
                  + "CHECK_HR_REPORT_STATE" + (char)31 + "TRUE"+ (char)30;
            //Bug id 81023, end

				trans.clear();

				cmd = trans.addCustomCommand("HISTSEPNEW","Work_Order_Coding_API.New__");
				cmd.addParameter("INFO","");
				cmd.addParameter("OBJID",objid1);
				cmd.addParameter("OBJVERSION",objversion1);
				cmd.addParameter("ATTR",sAttr);
				cmd.addParameter("ACTION","DO");

				trans = mgr.perform(trans);

				objid1 = trans.getValue("HISTSEPNEW/DATA/OBJID");
				objversion1 = trans.getValue("HISTSEPNEW/DATA/OBJVERSION");
			}
			else
			{
				itemlay.setLayoutMode(itemlay.EDIT_LAYOUT);
			}

			trans.clear();

			if (!populateTimeReps)
			{
				prepareForm();
			}
		}

		itemblk.generateAssignments();    
	}


	public void deleteITEM()
	{

		itemset.changeRow();

		if (( introFlag == 4 ) &&  ( itemset.countRows() > 1 ))
		{
         //Bug id 81023, start
         ASPManager mgr = getASPManager();                  
         trans.clear();
         
         cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
         cmd.addInParameter("COMPANY", itemset.getRow().getValue("COMPANY")); 
         cmd.addInParameter("EMP_NO", itemset.getRow().getValue("EMP_NO")); 
         cmd.addInParameter("CRE_DATE", itemset.getRow().getFieldValue("CRE_DATE")); 
         cmd.addInParameter("CATALOG_NO", "T"); 
         cmd.addInParameter("CONTRACT", itemset.getRow().getValue("CONTRACT"));
         mgr.perform(trans);
         //Bug id 81023, end
         
			itemset.setRemoved();
			itemset.previous();
		}
		else
		{
			itemset.clear();
			previousForm();
		}

		itemblk.generateAssignments();
	}


	public void prepareForm()
	{
		ASPManager mgr = getASPManager();
		String agreeId = "";
		String tporgcode = "";  
		//Bug 58216 Start
	        q = trans.addEmptyQuery(headblk); 
		//Bug 58216 End        

		if (introFlag == 4)
		{
			String wono11 = mgr.readValue("WO_NO");
			agreeId = mgr.readValue("AGREEMENT_ID");

			win4GoFlag = 1;

			cmd = trans.addCustomFunction("GETSITE","Active_Work_Order_API.Get_Contract","ITEM_CONTRACT");
			cmd.addParameter("WO_NO",wono11);

			cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","ITEM_COMPANY");
			cmd.addReference("CONTRACT","GETSITE/DATA");         

			trans.addCustomFunction("LOGONUSERVAL1","FND_SESSION_API.Get_Fnd_User","KEEP_LOGON_USER");

			trans = mgr.perform(trans);

			String itemcont = trans.getValue("GETSITE/DATA/CONTRACT");
			String itemcomp = trans.getValue("GETCOMP/DATA/COMPANY");        
			String logOnUserVal = trans.getValue("LOGONUSERVAL1/DATA/KEEP_LOGON_USER");

			trans.clear();  

			//Bug 58216 Start
			q = trans.addQuery("LOGONPERSON","select PERSON_ID EMP_SIGNATURE from PERSON_INFO where USER_ID= ?"); 
			q.addParameter("KEEP_LOGON_USER",logOnUserVal);
			//Bug 58216 End 

			cmd = trans.addCustomFunction("LOGONPERSONNAME", "Person_Info_API.Get_Name", "ITEM_NAME" );
			cmd.addReference("EMP_SIGNATURE", "LOGONPERSON/DATA");

			trans = mgr.perform(trans);

			String logonPersonId = trans.getValue("LOGONPERSON/DATA/EMP_SIGNATURE");
			String logonPersonName = trans.getValue("LOGONPERSONNAME/DATA/ITEM_NAME");
			trans.clear(); 
			
			//Bug 58216 Start
			q = trans.addQuery("LOGONEMPLOYEE","select EMP_NO EMP_NO from EMPLOYEE where SIGNATURE= ? and COMPANY= ?"); 
			q.addParameter("EMP_SIGNATURE",logonPersonId);
			q.addParameter("COMPANY",itemcomp);
			//Bug 58216 End 

			trans = mgr.perform(trans);

			empno = trans.getValue("LOGONEMPLOYEE/DATA/EMP_NO");
			trans.clear(); 

			cmd = trans.addCustomFunction("GETORG", "Employee_API.Get_Organization", "ITEM_ORG_CODE" );
			cmd.addParameter("COMPANY",itemcomp);
			cmd.addParameter("EMP_NO",empno);           

			cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
			cmd.addParameter("COMPANY");
			cmd.addParameter("EMP_NO",empno);

			cmd = trans.addCustomFunction("GETWOSITE","Active_Work_Order_API.Get_Contract","WOSITE");
			cmd.addParameter("WO_NO",wono11);

			cmd = trans.addCustomFunction("GETCAT","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
			cmd.addReference("ROLE_CODE","GETROLE/DATA");
			cmd.addReference("WOSITE","GETWOSITE/DATA");

			trans = mgr.perform(trans);

			tporgcode = trans.getValue("GETORG/DATA/ORG_CODE");            
			String tprolecode = trans.getValue("GETROLE/DATA/ROLE_CODE");
			String itemCat = trans.getValue("GETCAT/DATA/CATALOG_NO");
			String woSite = trans.getValue("GETWOSITE/DATA/WOSITE");
			trans.clear();

			if (mgr.isEmpty(tporgcode))
			{
				cmd = trans.addCustomFunction("GETORG1","Active_Work_Order_API.Get_Org_Code","ITEM_ORG_CODE");
				cmd.addParameter("WO_NO",wono11);

				trans = mgr.perform(trans);

				tporgcode = trans.getValue("GETORG1/DATA/ORG_CODE");  
				trans.clear();
			}

			if (mgr.isEmpty(itemCat))
			{
				cmd = trans.addCustomFunction("GETCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
				cmd.addParameter("ITEM_CONTRACT");
				cmd.addParameter("ITEM_ORG_CODE",tporgcode);  

				trans = mgr.perform(trans);

				itemCat = trans.getValue("GETCAT/DATA/CATALOG_NO");
			}


			trans.clear();            

			cmd = trans.addEmptyCommand("ITEM","WORK_ORDER_CODING_API.New__",itemblk);
			cmd.setOption("ACTION","PREPARE");

			trans = mgr.perform(trans);

			data = trans.getBuffer("ITEM/DATA");       

			data.setValue("CONTRACT",itemcont);
			data.setValue("ORG_CODE",tporgcode);
			data.setValue("EMP_NO",empno);
			data.setValue("WO_NO",headset.getRow().getValue("WO_NO"));
			data.setValue("SIGN",headset.getRow().getValue("REPORTED_BY"));
			data.setValue("NAME",logonPersonName);
			data.setValue("COMPANY",itemcomp);
			data.setValue("CRE_DATE",headset.getRow().getValue("DUMMY_REAL_F_DATE"));
			data.setValue("ROLE_CODE",tprolecode);      
			data.setValue("CATALOG_NO",itemCat);
			data.setValue("ITEM_AGREEMENT_ID",agreeId);
			data.setValue("COMPANY",headset.getRow().getValue("COMPANY"));
			itemset.addRow(data); 

		}

		if (introFlag == 1)
		{
			data = trans.getBuffer("HEAD/DATA");
			headset.addRow(data);
		}

		if (introFlag == 2)
		{
			horgcode = trans.getValue("HGETORG/DATA/ORG_CODE");
		}

		assign();
	}


	public void previousForm()
	{
		ASPManager mgr = getASPManager();

		if (introFlag == 3)
		{
			sympt = mgr.readValue("ERRSYMPT");

			if (mgr.isEmpty (sympt))  sympt ="";

			caus = mgr.readValue("ERRCAUSE");

			if (mgr.isEmpty (caus))	 caus ="";

			clas = mgr.readValue("ERRCLAS");

			if (mgr.isEmpty (clas))	 clas ="";

			typ = mgr.readValue("TYPE");

			if (mgr.isEmpty (typ))	typ ="";

			disc = mgr.readValue("DISCOVER");

			if (mgr.isEmpty (disc))	 disc ="";

			peract = mgr.readValue("PERACT");

			if (mgr.isEmpty (peract))	peract ="";
		}

		if (introFlag != 1)
			introFlag = (int)introFlag - 1;
		else
			mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZFIRSTPG: This is the first "));

		if (introFlag == 2)
			win2Flag = 1;

		if (introFlag == 1)
		{
			sTempRowBuff = headset.getRow();      

			sTempRowBuff.setValue("ERR_DESCR",mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"));

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.isEmpty(mgr.readValue("DUMMY_REAL_S_DATE"))?"":mgr.readValue("DUMMY_REAL_S_DATE"));      
			sTempRowBuff.setValue("DUMMY_REAL_S_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"));

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.isEmpty(mgr.readValue("DUMMY_REAL_F_DATE"))?"":mgr.readValue("DUMMY_REAL_F_DATE"));
			sTempRowBuff.setValue("DUMMY_REAL_F_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"));

			sTempRowBuff.setValue("DUMMY_WORK_DONE",mgr.isEmpty(mgr.readValue("DUMMY_WORK_DONE"))?"":mgr.readValue("DUMMY_WORK_DONE"));
			sTempRowBuff.setValue("DUMMY_PERFORMED_ACTION_LO",mgr.isEmpty(mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))?"":mgr.readValue("DUMMY_PERFORMED_ACTION_LO"));

			headset.setRow(sTempRowBuff);         
		}

		assign();
	}


	public void nextForm()
	{
		ASPManager mgr = getASPManager();

		errFlag   =   true;
		if (introFlag == 1)
		{
			if (mgr.isEmpty(mgr.readValue("WO_NO")))
			{
				errFlag = false;
			}
			else if (mgr.isEmpty(mgr.readValue("CONTRACT")))
			{
				errFlag = false;
			}
			else if (mgr.isEmpty(mgr.readValue("MCH_CODE")))
			{
				errFlag = false;
			}
			else if (mgr.isEmpty(mgr.readValue("ERR_DESCR")))
			{
				errFlag = false;
			}
			else if (mgr.isEmpty(mgr.readValue("ORG_CODE")))
			{
				errFlag = false;
			}
		}

		if (introFlag == 2)
		{
			if (mgr.isEmpty(mgr.readValue("DUMMY_REAL_F_DATE")))
			{
				errFlag = false;
			}
		}

		if (introFlag == 3)
		{
			sympt = mgr.readValue("ERRSYMPT"); 
			if (mgr.isEmpty (sympt))  sympt ="";

			caus = mgr.readValue("ERRCAUSE");
			if (mgr.isEmpty (caus))	 caus ="";

			clas = mgr.readValue("ERRCLAS");
			if (mgr.isEmpty (clas))	 clas ="";

			typ = mgr.readValue("TYPE");
			if (mgr.isEmpty (typ))	typ ="";

			disc = mgr.readValue("DISCOVER");
			if (mgr.isEmpty (disc))	 disc ="";

			peract = mgr.readValue("PERACT");
			if (mgr.isEmpty (peract))	peract ="";

		}


		if (introFlag == 4 &&  ( mgr.isEmpty(mgr.readValue("DUMMY_REAL_F_DATE")) ))
		{
			errFlag = false;
		}

		if (introFlag == 4)
			mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZFINISHBUT: This is the last "));

		if (( errFlag ) &&  ( introFlag != 4 ))
		{
			introFlag = (int)introFlag + 1;
			if (( introFlag < 4 ) ||  (( introFlag == 4 ) &&  ( itemset.countRows() == 0 )))
			{
				prepareForm();
				itemlay.setLayoutMode(itemlay.EDIT_LAYOUT);
			}
		}
		if (introFlag == 4)
		{
			if (itemset.countRows() == 0)
				itemlay.setLayoutMode(itemlay.EDIT_LAYOUT);
		}

                buf = mgr.newASPBuffer();
                buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.readValue("DUMMY_REAL_S_DATE"));
                buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.readValue("DUMMY_REAL_F_DATE"));

		switch (introFlag)
		{       
		case 2: sWoNo = mgr.readValue("WO_NO");     
			break;
		case 3:

			sHeaderAttr = "ERR_DESCR" +(char)31 +(mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"))+ (char)30
						  + "REAL_S_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"))+ (char)30
						  + "REAL_F_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"))+ (char)30              
						  + "WORK_DONE" + (char)31 +(mgr.isEmpty(mgr.readValue("DUMMY_WORK_DONE"))?"":mgr.readValue("DUMMY_WORK_DONE"))+ (char)30
						  + "PERFORMED_ACTION_LO" + (char)31 +(mgr.isEmpty(mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))?"":mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))+ (char)30;                            

			sTempRowBuff = headset.getRow();      

			sTempRowBuff.setValue("ERR_DESCR",mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"));

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.isEmpty(mgr.readValue("DUMMY_REAL_S_DATE"))?"":mgr.readValue("DUMMY_REAL_S_DATE"));      
			sTempRowBuff.setValue("DUMMY_REAL_S_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"));

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.isEmpty(mgr.readValue("DUMMY_REAL_F_DATE"))?"":mgr.readValue("DUMMY_REAL_F_DATE"));
			sTempRowBuff.setValue("DUMMY_REAL_F_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"));

			sTempRowBuff.setValue("DUMMY_WORK_DONE",mgr.isEmpty(mgr.readValue("DUMMY_WORK_DONE"))?"":mgr.readValue("DUMMY_WORK_DONE"));
			sTempRowBuff.setValue("DUMMY_PERFORMED_ACTION_LO",mgr.isEmpty(mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))?"":mgr.readValue("DUMMY_PERFORMED_ACTION_LO"));

			headset.setRow(sTempRowBuff);

			break;
		case 4:    
			sHeaderAttr = "ERR_DESCR" +(char)31 +(mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"))+ (char)30
						  + "REAL_S_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"))+ (char)30
						  + "REAL_F_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"))+ (char)30
						  + "WORK_DONE" + (char)31 +(mgr.isEmpty(mgr.readValue("DUMMY_WORK_DONE"))?"":mgr.readValue("DUMMY_WORK_DONE"))+ (char)30
						  + "PERFORMED_ACTION_LO" + (char)31 +(mgr.isEmpty(mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))?"":mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))+ (char)30
						  + "ERR_SYMPTOM" + (char)31 +(mgr.isEmpty(mgr.readValue("ERRSYMPT"))?"":mgr.readValue("ERRSYMPT"))+ (char)30
						  + "ERR_CAUSE" + (char)31 +(mgr.isEmpty(mgr.readValue("ERRCAUSE"))?"":mgr.readValue("ERRCAUSE"))+ (char)30
						  + "ERR_CLASS" + (char)31 +(mgr.isEmpty(mgr.readValue("ERRCLAS"))?"":mgr.readValue("ERRCLAS"))+ (char)30
						  + "ERR_TYPE" + (char)31 +(mgr.isEmpty(mgr.readValue("TYPE"))?"":mgr.readValue("TYPE"))+ (char)30
						  + "ERR_DISCOVER_CODE" + (char)31 +(mgr.isEmpty(mgr.readValue("DISCOVER"))?"":mgr.readValue("DISCOVER"))+ (char)30
						  + "PERFORMED_ACTION_ID" + (char)31 +(mgr.isEmpty(mgr.readValue("PERACT"))?"":mgr.readValue("PERACT"))+ (char)30;               

			sTempRowBuff = headset.getRow();

			sTempRowBuff.setValue("ERR_DESCR",mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"));

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.isEmpty(mgr.readValue("DUMMY_REAL_S_DATE"))?"":mgr.readValue("DUMMY_REAL_S_DATE")); 
			sTempRowBuff.setValue("DUMMY_REAL_S_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"));

			buf = mgr.newASPBuffer();
			buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.isEmpty(mgr.readValue("DUMMY_REAL_F_DATE"))?"":mgr.readValue("DUMMY_REAL_F_DATE")); 
			sTempRowBuff.setValue("DUMMY_REAL_F_DATE",mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"));

			sTempRowBuff.setValue("DUMMY_WORK_DONE",mgr.isEmpty(mgr.readValue("DUMMY_WORK_DONE"))?"":mgr.readValue("DUMMY_WORK_DONE"));
			sTempRowBuff.setValue("DUMMY_PERFORMED_ACTION_LO",mgr.isEmpty(mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))?"":mgr.readValue("DUMMY_PERFORMED_ACTION_LO"));
			sTempRowBuff.setValue("DUMMY_ERR_SYMPTOM",mgr.isEmpty(mgr.readValue("ERRSYMPT"))?"":mgr.readValue("ERRSYMPT"));
			sTempRowBuff.setValue("DUMMY_ERR_CAUSE",mgr.isEmpty(mgr.readValue("ERRCAUSE"))?"":mgr.readValue("ERRCAUSE"));
			sTempRowBuff.setValue("DUMMY_ERR_CLASS",mgr.isEmpty(mgr.readValue("ERRCLAS"))?"":mgr.readValue("ERRCLAS"));
			sTempRowBuff.setValue("DUMMY_ERR_TYPE",mgr.isEmpty(mgr.readValue("TYPE"))?"":mgr.readValue("TYPE"));
			sTempRowBuff.setValue("DUMMY_ERR_DISCOVER_CODE",mgr.isEmpty(mgr.readValue("DISCOVER"))?"":mgr.readValue("DISCOVER"));
			sTempRowBuff.setValue("DUMMY_PERFORMED_ACTION_ID",mgr.isEmpty(mgr.readValue("PERACT"))?"":mgr.readValue("PERACT"));

			headset.setRow(sTempRowBuff);   

			break;              
		}     

		assign();
	}


	public boolean CheckAllMaterialIssued()
	{
		ASPManager mgr = getASPManager();
		trans.clear();

		cmd = trans.addCustomFunction("NOTISSUED","Maint_Material_Req_Line_API.Is_All_Qty_Not_Issued","QTY");
		cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

		trans = mgr.perform(trans);

		double issued = trans.getNumberValue("NOTISSUED/DATA/QTY");
		if (issued == 1)
		{
			return true;
		}
		else
			return false;  
	}

	public double CheckAllMaterialIssued1(String woNo)
	{
		ASPManager mgr = getASPManager();
		trans.clear();

		cmd = trans.addCustomFunction("NOTISSUED","Maint_Material_Req_Line_API.Is_All_Qty_Not_Issued","QTY");
		cmd.addParameter("WO_NO",woNo);

		trans = mgr.perform(trans);

		double issued = trans.getNumberValue("NOTISSUED/DATA/QTY");
		if (issued == 1)
		{
			return 1;
		}
		else
			return 0;  
	}

	public void finishForm()
	{
		String sComment="";
		String sActiveWorequis=null;
		int count;
		ASPManager mgr = getASPManager();

		errFlag = true;
		String sAttr = "";
		String sdate="";
                ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();


		assign();

		if ((introFlag == 4)&&(itemlay.isEditLayout()))
		{
			itemset.changeRow();
			itemset.last();

			if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
			{
				errFlag = false;
				itemset.first();
			}
			else if (mgr.isEmpty(mgr.readValue("ITEM_CONTRACT")))
			{
				errFlag = false;
				itemset.first();
			}
			else if (mgr.isEmpty(mgr.readValue("CRE_DATE")))
			{
				errFlag = false;
				itemset.first();
			}


			/*else if (!(mgr.isEmpty(sWoNo))) {
				trans.clear();
				cmd = trans.addCustomFunction("CHKMOVHIST", "WORK_ORDER_REQUIS_HEADER_API.Check_Move_To_History","ACT_WOREQUIS"); 
				cmd.addParameter("WO_NO",sWoNo);
				trans=mgr.perform(trans);
				sActiveWorequis = trans.getValue("CHKMOVHIST/DATA/ACT_WOREQUIS");
				trans.clear();
				if ("FALSE".equals(sActiveWorequis))
				{
				   mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZACTWOREQUIS: Work Order "+sWoNo+" has Active Requisition(s). Remove requisition(s) to continue."));
				   errFlag = false;
				   itemset.first();
				}
			}*/
		}
                if ((introFlag == 4) && (!mgr.isEmpty(sWoNo)))
                {
                        itemset.last();

                                trans1.clear();
                                cmd = trans1.addCustomFunction("GETCUSTOMERNO", "ACTIVE_SEPARATE_API.Get_Customer_No","CUSTOMER_NO"); 
                                cmd.addParameter("WO_NO",sWoNo);
                                trans1 = mgr.validate(trans1);
                                String sCustNo = trans1.getValue("GETCUSTOMERNO/DATA/CUSTOMER_NO");
                                if ( !(mgr.isEmpty(sCustNo)) && (mgr.isEmpty(mgr.readValue("CATALOG_NO")))) {
                                         mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZCATNULL: Customer Exist in the Work Order, The field [Sales Part] must have a value."));
                                         errFlag = false;
                                         itemset.first();
                                }
                }

		if ((introFlag == 4) && (!mgr.isEmpty(sWoNo)))
		{
			itemset.last();
			trans.clear();
			cmd = trans.addCustomFunction("CHKMOVHIST", "WORK_ORDER_REQUIS_HEADER_API.Check_Move_To_History","ACT_WOREQUIS"); 
			cmd.addParameter("WO_NO",sWoNo);
			trans=mgr.perform(trans);
			sActiveWorequis = trans.getValue("CHKMOVHIST/DATA/ACT_WOREQUIS");
			trans.clear();
			if ("FALSE".equals(sActiveWorequis))
			{
				mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZACTWOREQUIS: Work Order &1 has Active Requisition(s). Remove requisition(s) to continue.",sWoNo));
				errFlag = false;
				itemset.first();
			}
		}



		if ((introFlag >= 2) && (errFlag))
		{
                        buf = mgr.newASPBuffer();
                        buf.addFieldItem("DUMMY_REAL_S_DATE",mgr.readValue("DUMMY_REAL_S_DATE"));
                        buf.addFieldItem("DUMMY_REAL_F_DATE",mgr.readValue("DUMMY_REAL_F_DATE"));

			switch (introFlag)
			{
			case 2:

				sHeaderAttr = "ERR_DESCR" +(char)31 +(mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"))+ (char)30
							  + "REAL_S_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"))+ (char)30
							  + "REAL_F_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"))+ (char)30;              

				break;
			case 3:       
				sHeaderAttr = "ERR_DESCR" +(char)31 +(mgr.isEmpty(mgr.readValue("ERR_DESCR"))?"":mgr.readValue("ERR_DESCR"))+ (char)30
							  + "REAL_S_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_S_DATE"))?"":buf.getValue("DUMMY_REAL_S_DATE"))+ (char)30
							  + "REAL_F_DATE" + (char)31 +(mgr.isEmpty(buf.getValue("DUMMY_REAL_F_DATE"))?"":buf.getValue("DUMMY_REAL_F_DATE"))+ (char)30
							  + "WORK_DONE" + (char)31 +(mgr.isEmpty(mgr.readValue("DUMMY_WORK_DONE"))?"":mgr.readValue("DUMMY_WORK_DONE"))+ (char)30
							  + "PERFORMED_ACTION_LO" + (char)31 +(mgr.isEmpty(mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))?"":mgr.readValue("DUMMY_PERFORMED_ACTION_LO"))+ (char)30
							  + "ERR_SYMPTOM" + (char)31 +(mgr.isEmpty(mgr.readValue("ERRSYMPT"))?"":mgr.readValue("ERRSYMPT"))+ (char)30
							  + "ERR_CAUSE" + (char)31 +(mgr.isEmpty(mgr.readValue("ERRCAUSE"))?"":mgr.readValue("ERRCAUSE"))+ (char)30
							  + "ERR_CLASS" + (char)31 +(mgr.isEmpty(mgr.readValue("ERRCLAS"))?"":mgr.readValue("ERRCLAS"))+ (char)30
							  + "ERR_TYPE" + (char)31 +(mgr.isEmpty(mgr.readValue("TYPE"))?"":mgr.readValue("TYPE"))+ (char)30
							  + "ERR_DISCOVER_CODE" + (char)31 +(mgr.isEmpty(mgr.readValue("DISCOVER"))?"":mgr.readValue("DISCOVER"))+ (char)30
							  + "PERFORMED_ACTION_ID" + (char)31 +(mgr.isEmpty(mgr.readValue("PERACT"))?"":mgr.readValue("PERACT"))+ (char)30;               

				break;              
			}     

			if (!(mgr.isEmpty(sWoNo)))
			{      String objver1 = "SELECT OBJVERSION,OBJID FROM ACTIVE_SEPARATE ";
						trans1.clear();
						objver1 += " WHERE  wo_no = ?";
						q = trans1.addQuery("GETOBJVER",objver1);
						q.addParameter("WO_NO",sWoNo);
						trans1 = mgr.validate(trans1);
						objversion = trans1.getValue("GETOBJVER/DATA/OBJVERSION");
                                                objid = trans1.getValue("GETOBJVER/DATA/OBJID");

				cmd = trans.addCustomCommand("MODIFRM", "ACTIVE_SEPARATE_API.Modify__"); 
				cmd.addParameter("INFO");    
				cmd.addParameter("OBJID",objid);
				cmd.addParameter("OBJVERSION",objversion);
				cmd.addParameter("ATTR",sHeaderAttr);
				cmd.addParameter("ACTION","DO");
				trans = mgr.perform(trans);
				objid = trans.getValue("MODIFRM/DATA/OBJID");
				objversion = trans.getValue("MODIFRM/DATA/OBJVERSION");

				if ((introFlag == 4)&&(!mgr.isEmpty(sWoNo))&&(errFlag)&&(itemlay.isEditLayout()))
				{
					trans.clear();
					cmd = trans.addCustomFunction("ACCTYPE","Work_Order_Account_Type_API.Get_Client_Value","WORK_ORDER_ACCOUNT_TYPE");
					cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

					cmd = trans.addCustomFunction("COSTTYPE","Work_Order_Cost_Type_API.Get_Client_Value","WORK_ORDER_COST_TYPE");
					cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

					cmd = trans.addCustomFunction("AUTHSIG","Maintenance_Configuration_API.Get_Auth_Def_Sign","SIGNATURE");

					cmd = trans.addCustomFunction("BOOKSTATUS","Work_Order_Book_Status_API.Get_Client_Value","BOOK_STATUS");
					cmd.addParameter("NUM_ZERO", mgr.getASPField("NUM_ZERO").formatNumber(0));

					trans = mgr.perform(trans);

					String sAccountType = trans.getValue("ACCTYPE/DATA/WORK_ORDER_ACCOUNT_TYPE");
					String sCostType = trans.getValue("COSTTYPE/DATA/WORK_ORDER_COST_TYPE");
					String sSignature = trans.getValue("AUTHSIG/DATA/SIGNATURE");
					String sBookStatus = trans.getValue("BOOKSTATUS/DATA/BOOK_STATUS");

					assign();

					//n = itemset.countRows();
					itemset.last();

					sComment = itemset.getRow().getFieldValue("ITEM_NAME") + ", " + itemset.getRow().getFieldValue("ITEM_ORG_CODE") + " (" + itemset.getRow().getValue("ROLE_CODE") + ")" + ".";
					sAttr = "";
					String empNo_ = mgr.readValue ("EMP_NO","");
					String orgCode_ = itemset.getRow().getFieldValue("ITEM_ORG_CODE");
					String roleCode_ = mgr.readValue ("ROLE_CODE","");
					String qtyInvoice_ =  mgr.getASPField ("QTY_TO_INVOICE").formatNumber (itemset.getRow ().getNumberValue("QTY_TO_INVOICE") );
					String empSignature_ =   mgr.readValue ("EMP_SIGNATURE","");
					String agreePriceFlag_ = itemset.getValue("AGREEMENT_PRICE_FLAG");
					if (mgr.isEmpty (agreePriceFlag_)) agreePriceFlag_ ="";
					String priceListNo_ = itemset.getRow().getValue("PRICE_LIST_NO");
					if (mgr.isEmpty (priceListNo_))	priceListNo_ ="";
					String listPrice_ =  itemset.getRow().getValue("LIST_PRICE") ;
					if (mgr.isEmpty (listPrice_))	listPrice_ ="";
					String catNo_ =   mgr.readValue ("CATALOG_NO","");

					//Bug id 81023, start - added CHECK_HR_REPORT_STATE
               sAttr = sAttr   + "CMNT" + (char)31 +sComment+ (char)30
							+ "EMP_NO" + (char)31 +empNo_+ (char)30
							+ "EMP_SIGNATURE" + (char)31 +empSignature_+ (char)30
							+ "ORG_CODE" + (char)31 +orgCode_+ (char)30
							+ "WO_NO" + (char)31 +sWoNo+ (char)30
							+ "WORK_ORDER_COST_TYPE" + (char)31 +sCostType+ (char)30
							+ "WORK_ORDER_ACCOUNT_TYPE" + (char)31 +sAccountType+ (char)30
							+ "CATALOG_NO" + (char)31 +catNo_+ (char)30
							+ "SIGNATURE" + (char)31 +sSignature+ (char)30
							+ "SIGNATURE_ID" + (char)31 +sSignature+ (char)30
							+ "WORK_ORDER_BOOK_STATUS" + (char)31 +sBookStatus+ (char)30
							+ "CONTRACT" + (char)31 +itemset.getRow().getValue("CONTRACT")+ (char)30
                                                        + "CATALOG_CONTRACT" + (char)31 +itemset.getRow().getValue("CONTRACT")+ (char)30
							+ "COMPANY" + (char)31 +itemset.getRow().getValue("COMPANY")+ (char)30                              
							+ "CRE_DATE" + (char)31 +itemset.getRow().getValue("CRE_DATE")+ (char)30
							+ "LIST_PRICE" + (char)31 +listPrice_+ (char)30
							+ "ROLE_CODE" + (char)31 +roleCode_+ (char)30
							+ "QTY" + (char)31 +itemset.getRow().getValue("QTY")+ (char)30
							+ "QTY_TO_INVOICE" + (char)31+qtyInvoice_+ (char)30
							+ "AGREEMENT_PRICE_FLAG" + (char)31 +agreePriceFlag_+ (char)30
							+ "PRICE_LIST_NO" + (char)31 +priceListNo_+ (char)30                                    
							+ "AMOUNT" + (char)31 +itemset.getRow().getValue("AMOUNT")+ (char)30
                     + "CHECK_HR_REPORT_STATE" + (char)31 + "TRUE" + (char)30  ;
               //Bug id 81023, end

					trans.clear();
					cmd = trans.addCustomCommand("HISTSEPNEW","Work_Order_Coding_API.New__");
					cmd.addParameter("INFO","");
					cmd.addParameter("OBJID",objid1);
					cmd.addParameter("OBJVERSION",objversion1);
					cmd.addParameter("ATTR",sAttr);
					cmd.addParameter("ACTION","DO");

					trans = mgr.perform(trans);           
					objid1 = trans.getValue("HISTSEPNEW/DATA/OBJID");
					objversion1 = trans.getValue("HISTSEPNEW/DATA/OBJVERSION"); 
				}
				trans.clear();

				// Call 110620 Start
				String sqlCheckInv = "SELECT count(1) FROM work_order_coding ";
				//Bug 58216 Start
				sqlCheckInv += " WHERE  wo_no = ?";
				//Bug 58216 End
				sqlCheckInv += " AND objstate not like 'Transferred' ";
				sqlCheckInv += " AND objstate not like 'NotInvoiceable' ";
				sqlCheckInv += " AND work_order_account_type = Work_Order_Account_Type_API.Get_Client_Value(0)";
				q = trans.addQuery("CHECKINVOICEABLE",sqlCheckInv);
                                //Bug 58216 Start
				q.addParameter("WO_NO",sWoNo);    
				//Bug 58216 End

				cmd = trans.addCustomFunction("GETCUSTOMERNO", "ACTIVE_SEPARATE_API.Get_Customer_No","CUSTOMER_NO"); 
				cmd.addParameter("WO_NO",sWoNo);

				trans = mgr.validate(trans);

				String sCustomerNo = trans.getValue("GETCUSTOMERNO/DATA/CUSTOMER_NO");
				boolean bCheckInvOk = ("0".equals(trans.getValue("CHECKINVOICEABLE/DATA/COUNT(1)")));
				boolean bCanFinish = true;

				if (!mgr.isEmpty(sCustomerNo))
				{
					if (!bCheckInvOk)
					{
						trans.clear();
						cmd = trans.addCustomCommand("ADDCORD","Active_Work_Order_API.Modify_Coordinator");
						cmd.addParameter("WO_NO",sWoNo);
						cmd.addParameter("AUTHORIZE_CODE",headset.getRow().getValue("AUTHORIZE_CODE"));
						trans = mgr.perform(trans);

						trans.clear();
						cmd = trans.addCustomCommand("GETCOLINES","WORK_ORDER_CODING_UTILITY_API.Create_Customer_Order_Inv");
						cmd.addParameter("TRANSFERED_LINES");
						cmd.addParameter("UNTRANSFERED_ROWS");
						cmd.addParameter("WO_NO",sWoNo);

						trans = mgr.perform(trans);
						trans.clear();

						String objver = "SELECT OBJVERSION FROM ACTIVE_SEPARATE ";
						
						//Bug 58216 Start
						objver += " WHERE  wo_no = ?";
						q = trans.addQuery("GETOBJVER",objver);
						q.addParameter("WO_NO",sWoNo);
						//Bug 58216 End
						trans = mgr.validate(trans);
						objversion = trans.getValue("GETOBJVER/DATA/OBJVERSION");

					}

				}

				if (bCanFinish)
				{
					trans.clear();
					cmd = trans.addCustomCommand("FINI", "ACTIVE_SEPARATE_API.FINISH__"); 
					cmd.addParameter("INFO");    
					cmd.addParameter("OBJID");
					cmd.addParameter("OBJVERSION",objversion);
					cmd.addParameter("ATTR");
					cmd.addParameter("ACTION","DO"); 

					trans=mgr.perform(trans);
					cancelForm();
				}
				else
					mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZCUSTEXISTS: Customer &1 exists for this Work Order and therefore all lines need to be transferred. Please transfer all lines.",sCustomerNo));

				// Call 110620 End
			}
			if (introFlag != 4)
				cancelForm();
		}

		if (CheckAllMaterialIssued())
		{
			ctx.setCookie( "PageID_my_cookie1", "TRUE" );
			unissue = "TRUE";
		}

		assign();
	}   

	public void cancelForm()
	{
		cancelFlag = "TRUE";
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void clear()
	{

		headset.clear();
		headtbl.clearQueryRow();

		itemset.clear();
		itemtbl.clearQueryRow();

		assign();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void backwardITEM()
	{
		ASPManager mgr = getASPManager();      

		if (!itemset.previous())
		{
			mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZFIRSTROW: This is the first line."));
		}
	}


	public void forwardITEM()
	{
		ASPManager mgr = getASPManager();

		if (!itemset.next())
		{
			mgr.showAlert(mgr.translate("PCMWREPORTINWORKORDERWIZLASTROW: This is the last line."));
		}

		assign();
	}

//-------------------------------------------------------
//  Populate List Boxes
//-------------------------------------------------------

	public void populateListBoxes()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(BlkSympt);
		q.setOrderByClause("ERR_SYMPTOM");

		q = trans.addQuery(BlkCause);
		q.setOrderByClause("ERR_CAUSE");

		q = trans.addQuery(BlkClass);
		q.setOrderByClause("ERR_CLASS");

		q = trans.addQuery(BlkType);
		q.setOrderByClause("ERR_TYPE");         

		q = trans.addQuery(BlkDisc);
		q.setOrderByClause("ERR_DISCOVER_CODE");

		q = trans.addQuery(BlkPerAct);
		q.setOrderByClause("PERFORMED_ACTION_ID");

		mgr.perform(trans);

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

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		boolean orderInst = false; 

		headblk.addField("MODULENAME").
		setHidden().
		setFunction("''");

		if (isModuleInst("ORDER"))
			orderInst = true;
		else
			orderInst = false;        

		f = headblk.addField("INFO");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ACTION");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("REG_DATE","Datetime");
		f.setHidden();

		/////////////////// Report In /////////////////////

		f = headblk.addField("WO_NO","Number","#");
		f.setSize(13);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZWONO: WO No:");

		f = headblk.addField("CONTRACT");
		f.setSize(10);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZCON: WO Site:");
		f.setUpperCase();
		f.setMaxLength(5);
		f.setReadOnly();
		f.setCustomValidation("CONTRACT, MCH_CODE, REG_DATE","COMPANY");

		f = headblk.addField("ERR_DESCR");
		f.setSize(45);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZERRDES: Directive:");
		f.setMaxLength(60);
		f.setReadOnly();

		f = headblk.addField("ORG_CODE");
		f.setSize(10);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZORGCODE: Maintenance Organization:");
		f.setUpperCase();
		f.setMaxLength(8);
		f.setReadOnly();

		f = headblk.addField("MCH_CODE");
		f.setSize(20);
		f.setLabel("PCMWREPORTINWORKORDERWIZMCHCODE: Object ID:");
		f.setUpperCase();
		f.setMaxLength(100);
		f.setMandatory();    
		f.setReadOnly();

		f = headblk.addField("MCH_CODE_DESCRIPTION");
		f.setReadOnly();
		f.setSize(30);
		f.setMaxLength(45);
		f.setLabel("PCMWREPORTINWORKORDERWIZMCHNAME: Object Description:");
		f.setReadOnly();

		f = headblk.addField("STATE");
		f.setSize(18);
		f.setLabel("PCMWREPORTINWORKORDERWIZSTATE: Status:");
		f.setMaxLength(30);
		f.setReadOnly();

		f = headblk.addField("MCH_CODE_CONTRACT");
		f.setSize(20);
		f.setLabel("PCMWREPORTINWORKORDERWIZMCHCODECON: Site:");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setReadOnly();

		f = headblk.addField("CONNECTION_TYPE");
		f.setSize(20);
		f.setLabel("PCMWREPORTINWORKORDERWIZCONTYPE: Connection Type:");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setReadOnly();

		////////////////////////////////////////////////////

		f = headblk.addField("REPORTED_BY");
		f.setSize(8);
		f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
		f.setMandatory();
		f.setUpperCase();
		f.setMaxLength(20);
		f.setCustomValidation("REPORTED_BY,COMPANY,REG_DATE","NAME,EMPNO,SYSDATE");
		f.setHidden();

		f = headblk.addField("EMPNO");
		f.setHidden();
		f.setFunction("COMPANY_EMP_API.Get_Max_Employee_Id(:COMPANY,:REPORTED_BY)");

		f = headblk.addField("NAME");
		f.setHidden();
		f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");

		f = headblk.addField("SYSDATE","Datetime");
		f.setHidden();

		f = headblk.addField("COMPANY");
		f.setHidden();
		f.setUpperCase();

		/////////////////// Directive /////////////////////

		f = headblk.addField("REAL_S_DATE","Datetime");
		f.setHidden();

		f = headblk.addField("DUMMY_REAL_S_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWREPORTINWORKORDERWIZRSDATE: Actual Start:");
		f.setCustomValidation("DUMMY_REAL_S_DATE","DUMMY_REAL_S_DATE");
		f.setFunction("''");

		f = headblk.addField("REAL_F_DATE","Datetime");
		f.setMandatory();
		f.setHidden();

		f = headblk.addField("DUMMY_REAL_F_DATE","Datetime");
		f.setSize(25);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZRFDATE: Actual Completion:");
		f.setCustomValidation("DUMMY_REAL_F_DATE","DUMMY_REAL_F_DATE");
		f.setFunction("''");              

		f = headblk.addField("WORK_DONE");
		f.setHidden();

		f = headblk.addField("DUMMY_WORK_DONE");
		f.setSize(18);
		f.setLabel("PCMWREPORTINWORKORDERWIZWRKDONE: Work Done(short):");
		f.setMaxLength(60);   
		f.setFunction("''");

		f = headblk.addField("PERFORMED_ACTION_LO");
		f.setHidden();

		f = headblk.addField("DUMMY_PERFORMED_ACTION_LO");
		f.setSize(30);
		f.setHeight(5);
		f.setLabel("PCMWREPORTINWORKORDERWIZPERACTLO: Work Done(long):");   
		f.setMaxLength(2000);  
		f.setFunction("''");              

		/////////////////// Classification /////////////////////

		f = headblk.addField("ERR_SYMPTOM");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);

		f = headblk.addField("DUMMY_ERR_SYMPTOM");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZERSYMP: Symptom:");
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);
		f.setFunction("''");

		f = headblk.addField("ERR_CAUSE");
		f.setSize(6);
		f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,450);
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);

		f = headblk.addField("DUMMY_ERR_CAUSE");
		f.setSize(6);
		f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZERCAUSE: Cause:");
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);
		f.setFunction("''");

		f = headblk.addField("ERR_CLASS");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,450);
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);

		f = headblk.addField("DUMMY_ERR_CLASS");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZERCLS: Class:");
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);
		f.setFunction("''");

		f = headblk.addField("ERR_TYPE");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,450);
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);

		f = headblk.addField("DUMMY_ERR_TYPE");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZERTYPE: Type:");
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);
		f.setFunction("''");

		f = headblk.addField("ERR_DISCOVER_CODE");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);

		f = headblk.addField("DUMMY_ERR_DISCOVER_CODE");
		f.setSize(6);
		f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZERDISCODE: Discovery:");
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);
		f.setFunction("''");

		f = headblk.addField("PERFORMED_ACTION_ID");
		f.setSize(6);
		f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,450);
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);

		f = headblk.addField("DUMMY_PERFORMED_ACTION_ID");
		f.setSize(6);
		f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZPAID: Performed Action:");
		f.setUpperCase();
		f.setHidden();
		f.setMaxLength(3);
		f.setFunction("''");

		f = headblk.addField("GET_ORG_CODE");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("AGREEMENT_ID");
		f.setHidden();

		f = headblk.addField("TEMP_EMP_NO");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ACT_WOREQUIS");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TRANSFERED_LINES");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("UNTRANSFERED_ROWS");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("AUTHORIZE_CODE");
		f.setHidden();

 		headblk.setView("ACTIVE_SEPARATE");
		headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,FINISH__");
		headblk.setTitle(mgr.translate("PCMWREPORTINWORKORDERWIZBLKTITLE: Report In Work Order")); 
		headset = headblk.getASPRowSet();
		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.CANCELEDIT);
		headbar.disableCommand(headbar.FORWARD);
		headbar.disableModeLabel();
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
		headlay.setSimple("MCH_CODE_DESCRIPTION");        

		headbar.disableMinimize();

		/////////////////// Time Report /////////////////////

		itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("ITEM_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk.addField("ITEM_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk.addField("ITEM_COMPANY");
		f.setHidden();
		f.setDbName("COMPANY");

		f = itemblk.addField("ITEM_WO_NO","Number","#");
		f.setSize(7);
		f.setHidden();
		f.setDbName("WO_NO");

		f = itemblk.addField("EMP_NO");
		f.setSize(14);
		f.setDynamicLOV("EMPLOYEE_NO","ITEM_COMPANY COMPANY",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZEMPLNO: Employee:");
		f.setUpperCase();
		f.setMaxLength(11);
		f.setCustomValidation("EMP_NO,ITEM_COMPANY,ITEM_CONTRACT,CATALOG_NO,ITEM_ORG_CODE,ROLE_CODE,ITEM_WO_NO","ROLE_CODE,EMP_SIGNATURE,ITEM_NAME,ITEM_ORG_CODE,CMNT,CATALOG_NO,SALESPARTCATALOGDESC,EMP_NO,ITEM_CONTRACT");

		f = itemblk.addField("SIGN");
		f.setSize(7);
		f.setLabel("PCMWREPORTINWORKORDERWIZSIGN: Signature:");
		f.setUpperCase();
		f.setFunction("''");
		f.setMaxLength(20);
		f.setHidden();

		f = itemblk.addField("ITEM_CONTRACT");
		f.setSize(14);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZCONT: Site:");
		f.setUpperCase();
		f.setDbName("CONTRACT");
		f.setMaxLength(5);
		f.setCustomValidation("ITEM_CONTRACT", "ITEM_COMPANY");

		f = itemblk.addField("ITEM_ORG_CODE");
		f.setSize(14);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM_CONTRACT CONTRACT",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZITORGCODE: Maintenance Organization:");
		f.setUpperCase();
		f.setDbName("ORG_CODE");
		f.setMaxLength(8);
		f.setCustomValidation("ITEM_ORG_CODE,ITEM_WO_NO,ITEM_CONTRACT,AMOUNT,QTY,ROLE_CODE,CATALOG_NO,SALESPARTCATALOGDESC,CMNT,ITEM_NAME,EMP_NO","ITEM_ORG_CODE,AMOUNT,CMNT,CATALOG_NO,SALESPARTCATALOGDESC,ITEM_CONTRACT");

		f = itemblk.addField("ROLE_CODE");
		f.setSize(14);
		f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM_CONTRACT CONTRACT",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZROLECODE: Craft:");
		f.setUpperCase();
		f.setMaxLength(10);
		f.setCustomValidation("CUSTOMER_NO,ITEM_CONTRACT,CATALOG_NO,PRICE_LIST_NO,ITEM_AGREEMENT_ID,QTY1,QTY_TO_INVOICE,QTY,ITEM_ORG_CODE,ROLE_CODE,AMOUNT,CMNT,ITEM_NAME,SALESPARTCOST,LIST_PRICE,ITEM_WO_NO,EMP_NO","LIST_PRICE,PRICE_LIST_NO,AMOUNT,AMOUNTSALES,CMNT,CATALOG_NO,SALESPARTCATALOGDESC,ROLE_CODE,ITEM_CONTRACT");

		f = itemblk.addField("CATALOG_NO");
		f.setSize(14);
		if (orderInst)
			f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM_CONTRACT CONTRACT",600,450);
		f.setLabel("PCMWREPORTINWORKORDERWIZCATALOGNO: Sales Part:");
		f.setMaxLength(25);
		f.setUpperCase();
		f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM_CONTRACT,CATALOG_NO,CUSTOMER_NO,ITEM_AGREEMENT_ID,PRICE_LIST_NO,QTY1,SALESPARTCATALOGDESC,ITEM_NAME,ITEM_ORG_CODE,ROLE_CODE,EMP_NO","LIST_PRICE,AMOUNT,AMOUNTSALES,PRICE_LIST_NO,CMNT,SALESPARTCATALOGDESC,SALESPARTCOST");

		f = itemblk.addField("QTY","Number");
		f.setSize(14);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZQTY: Hours:");
		f.setCustomValidation("ITEM_CONTRACT,CATALOG_NO,ROLE_CODE,ITEM_ORG_CODE,QTY,SALESPARTCOST,QTY_TO_INVOICE,CUSTOMER_NO,ITEM_AGREEMENT_ID,PRICE_LIST_NO,LIST_PRICE,EMP_NO","LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,SALESPARTCOST,AMOUNT");

		f = itemblk.addField("AMOUNT","Money");
		f.setSize(14);
		f.setLabel("PCMWREPORTINWORKORDERWIZAMT: Cost Amount:");

		f = itemblk.addField("CRE_DATE","Datetime");
		f.setSize(23);
		f.setMandatory();
		f.setLabel("PCMWREPORTINWORKORDERWIZCREDATE: Performed Date:");
		f.setCustomValidation("CRE_DATE","CRE_DATE");

		f = itemblk.addField("ITEM_NAME");
		f.setHidden();
		f.setDbName("NAME");
		f.setFunction("Person_Info_API.Get_Name(:EMP_SIGNATURE)");

		f = itemblk.addField("WORK_ORDER_ACCOUNT_TYPE");
		f.setHidden();

		f = itemblk.addField("WORK_ORDER_COST_TYPE");
		f.setHidden();

		f = itemblk.addField("SALESPARTCOST","Money");
		f.setFunction("''");
		f.setHidden();

		f = itemblk.addField("SIGNATURE");
		f.setHidden();

		f = itemblk.addField("BOOK_STATUS");
		f.setFunction("''");
		f.setHidden();

		f = itemblk.addField("LIST_PRICE","Money");
		f.setHidden();

		f = itemblk.addField("EMP_SIGNATURE");
		f.setHidden();

		f = itemblk.addField("CMNT");
		f.setHidden();

		f = itemblk.addField("SALESPARTCATALOGDESC");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("DEPART");
		f.setHidden();
		f.setFunction("''");    

		f = itemblk.addField("DEFSITE");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("WOSITE");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("COST1","Money");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("COST2","Money");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("COST3","Money");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("QTY_TO_INVOICE", "Number");
		f.setHidden();

		f = itemblk.addField("BASE_PRICE","Money");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("SALE_PRICE","Money");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("DISCOUNT","Number");
		f.setHidden();

		f = itemblk.addField("CURRENCY_RATE","Number");
		f.setFunction("''");
		f.setHidden();

		f = itemblk.addField("AGREEMENT_PRICE_FLAG","Number");
		f.setHidden();

		f = itemblk.addField("QTY1","Number");
		f.setFunction("''");
		f.setHidden();

		f = itemblk.addField("AMOUNTSALES","Money");
		f.setFunction("(LIST_PRICE*QTY)");
		f.setHidden();

		f = itemblk.addField("PRICE_LIST_NO");
		f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM_CONTRACT,CATALOG_NO,CUSTOMER_NO,ITEM_AGREEMENT_ID,PRICE_LIST_NO,QTY1,LIST_PRICE","LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,AGREEMENT_PRICE_FLAG");
		f.setHidden();

		f = itemblk.addField("CUSTOMER_NO");
		f.setHidden();

		f = itemblk.addField("NUM_ZERO","Number");
		f.setFunction("''");
		f.setHidden();

		f = itemblk.addField("KEEP_LOGON_USER");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("ITEM_AGREEMENT_ID");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("DUMMY");
		f.setHidden();
		f.setFunction("''");

		itemblk.setView("WORK_ORDER_CODING");
		itemblk.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
		itemblk.setTitle(mgr.translate("PCMWREPORTINWORKORDERWIZBLKTITLE: Report In Work Order"));
		itemblk.setMasterBlock(headblk);
		itemset = itemblk.getASPRowSet();

		itembar = mgr.newASPCommandBar(itemblk);
		itembar.disableCommand(itembar.SAVERETURN);
		itembar.disableCommand(itembar.CANCELEDIT);
		itembar.disableCommand(itembar.FORWARD);
		itembar.disableModeLabel();
		itembar.removeCommandGroup(itembar.CMD_GROUP_CUSTOM);

		itemtbl = mgr.newASPTable(itemblk);
		itemtbl.disableRowStatus();
		itemlay = itemblk.getASPBlockLayout();
		itemlay.setDefaultLayoutMode(itemlay.CUSTOM_LAYOUT);

		itemlay.setDialogColumns(1);

		itembar.disableMinimize();

		// ----------------------------------------------------
		// Define block "BlkSympt" for Symptom Combo box
		// ----------------------------------------------------

		BlkSympt = mgr.newASPBlock("SYMPT");

		f = BlkSympt.addField("S3_ERR_SYMPTOM");
		f.setSize(3);
		f.setDbName("ERR_SYMPTOM");
		f.setLabel("PCMWREPORTINWORKORDERWIZERRSYMP: Error Symptom:");

		f = BlkSympt.addField("SYMPT_DISPLAY");
		f.setFunction("ERR_SYMPTOM" + "||" + "' - '" + "||" + "DESCRIPTION");

		BlkSympt.setView("WORK_ORDER_SYMPT_CODE");   
		RowsetSympt = BlkSympt.getASPRowSet();

		// ----------------------------------------------------
		// Define block "BlkCause" for Cause Combo box
		// ----------------------------------------------------

		BlkCause = mgr.newASPBlock("CAUS");

		f = BlkCause.addField("CMB_ERR_CAUSE");
		f.setDbName("ERR_CAUSE");

		f = BlkCause.addField("CAUSE_DISPLAY");
		f.setFunction("ERR_CAUSE" + "||" + "' - '" + "||" + "DESCRIPTION");

		BlkCause.setView("MAINTENANCE_CAUSE_CODE");  
		RowsetCause = BlkCause.getASPRowSet();     

		// ----------------------------------------------------
		// Define block "BlkClass" for Class Combo box
		// ----------------------------------------------------

		BlkClass = mgr.newASPBlock("CLAS");

		f = BlkClass.addField("CMB_ERR_CLASS");
		f.setDbName("ERR_CLASS");

		f = BlkClass.addField("CLASS_DISPLAY");
		f.setFunction("ERR_CLASS" + "||" + "' - '" + "||" + "DESCRIPTION");

		BlkClass.setView("WORK_ORDER_CLASS_CODE");  
		RowsetClass = BlkClass.getASPRowSet();     

		// ----------------------------------------------------
		// Define block "BlkType" for Type Combo box
		// ----------------------------------------------------

		BlkType = mgr.newASPBlock("TYP");

		f = BlkType.addField("CMB_ERR_TYPE");
		f.setDbName("ERR_TYPE");

		f = BlkType.addField("TYPE_DISPLAY");
		f.setFunction("ERR_TYPE" + "||" + "' - '" + "||" + "DESCRIPTION");

		BlkType.setView("WORK_ORDER_TYPE_CODE");  
		RowsetType = BlkType.getASPRowSet();      

		// ----------------------------------------------------
		// Define block "BlkDisc" for Discovery Combo box
		// ----------------------------------------------------

		BlkDisc = mgr.newASPBlock("DISC");

		f = BlkDisc.addField("S3_ERR_DISCOVER_CODE");
		f.setDbName("ERR_DISCOVER_CODE");

		f = BlkDisc.addField("DISC_DISPLAY");
		f.setFunction("ERR_DISCOVER_CODE" + "||" + "' - '" + "||" + "DESCRIPTION");

		BlkDisc.setView("WORK_ORDER_DISC_CODE");  
		RowsetDisc = BlkDisc.getASPRowSet();  

		// ----------------------------------------------------
		// Define block "BlkPerAct" for Performed Action Combo box
		// ----------------------------------------------------

		BlkPerAct = mgr.newASPBlock("PERACT");

		f = BlkPerAct.addField("S3_PERFORMED_ACTION_ID");
		f.setDbName("PERFORMED_ACTION_ID");

		f = BlkPerAct.addField("PERACT_DISPLAY");
		f.setFunction("PERFORMED_ACTION_ID" + "||" + "' - '" + "||" + "DESCRIPTION");

		BlkPerAct.setView("MAINTENANCE_PERF_ACTION");  
		RowsetPerAct = BlkPerAct.getASPRowSet();  

		headlay.showBottomLine(false);
		itemlay.showBottomLine(false);

		disableOptions();
		disableHomeIcon();
		disableNavigate();
	}

	public void setLovWithContracts()
	{
		ASPManager mgr = getASPManager();

		mgr.getASPField("WO_NO").setLOV("ActiveSeparateLov.page",600,450);
		mgr.getASPField("WO_NO").setLOVProperty("WHERE","CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		if (mgr.isExplorer())
			frm.setFormWidth(730);
		else
			frm.setFormWidth(815);

		headbar.setWidth(frm.getFormWidth()); 
		setLovWithContracts();    
		if (introFlag != 1)
		{
			mgr.getASPField("WO_NO").setHidden();
			mgr.getASPField("CONTRACT").setHidden();
			mgr.getASPField("MCH_CODE").setHidden();
			mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden();     
			mgr.getASPField("ORG_CODE").setHidden();
			mgr.getASPField("ERR_DESCR").setHidden();                       
			mgr.getASPField("STATE").setHidden();
			mgr.getASPField("REPORTED_BY").setHidden();
			mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
			mgr.getASPField("CONNECTION_TYPE").setHidden();
		}

		if (introFlag != 2)
		{
			mgr.getASPField("DUMMY_REAL_S_DATE").setHidden();
			mgr.getASPField("DUMMY_REAL_F_DATE").setHidden();
			mgr.getASPField("DUMMY_WORK_DONE").setHidden();
			mgr.getASPField("DUMMY_PERFORMED_ACTION_LO").setHidden();
		}

		if (introFlag != 4)
		{
			mgr.getASPField("EMP_NO").setHidden();
			mgr.getASPField("ITEM_CONTRACT").setHidden();
			mgr.getASPField("ITEM_ORG_CODE").setHidden();
			mgr.getASPField("ROLE_CODE").setHidden();
			mgr.getASPField("CRE_DATE").setHidden();
			mgr.getASPField("QTY").setHidden();
			mgr.getASPField("CATALOG_NO").setHidden();
			mgr.getASPField("AMOUNT").setHidden();
		}

		url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
                chkAllMat = CheckAllMaterialIssued1(sWoNo);
	}

	private String modifiedItemlayGenDlg(ASPManager mgr,String myItemlayGenDlg)
	{
		if (introFlag == 2)
		{
			int defInd1 = myItemlayGenDlg.indexOf("<input type=hidden name=\"DUMMY_REAL_F_DATE\" value=\"\">");
			int defInd2 = myItemlayGenDlg.indexOf("<input type=hidden name=\"DUMMY_REAL_S_DATE\" value=\"\">");
			if (defInd1 != -1)
			{
				myItemlayGenDlg = mgr.replace(myItemlayGenDlg,"<input type=hidden name=\"DUMMY_REAL_F_DATE\" value=\"\">"," ");
			}

			if (defInd2 != -1)
			{
				myItemlayGenDlg = mgr.replace(myItemlayGenDlg,"<input type=hidden name=\"DUMMY_REAL_S_DATE\" value=\"\">"," ");
			}
		}
		return myItemlayGenDlg;
	}

	protected String getDescription()
	{
		return "PCMWREPORTINWORKORDERWIZREPINWIZ: Report in Wizard";
	}

	protected String getTitle()
	{
		return "PCMWREPORTINWORKORDERWIZREPINWIZ: Report in Wizard";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (showHtmlPart)
		{
			printHiddenField("__GETREC","");
			printHiddenField("BUTTONVAL","");
			printHiddenField("BUTTONFSH1","");
			printHiddenField("BUTTONFSH","");

			if (headlay.isVisible() || itemlay.isEditLayout())
			{
				if (introFlag != 4)
					appendToHTML(headbar.showBar());
				else
					appendToHTML(itembar.showBar());

				if (mgr.isExplorer())
					appendToHTML(" <table border=\"0\" class=\"BlockLayoutTable\" cellpadding=\"0\" cellspacing=\"0\" width=730>\n");
				else
					appendToHTML(" <table border=\"0\" class=\"BlockLayoutTable\" cellpadding=\"0\" cellspacing=\"0\" width=815>\n");

				appendToHTML("    <tr>\n");
				appendToHTML("      <td width=\"100\">\n");
				appendToHTML("       <table id=\"picTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width='99.5%'>\n");
				appendToHTML("        <tr>\n");
				appendToHTML("          <td width=\"100%\"><img src=\"images/PcmWizardWorkSM.gif\" WIDTH=\"130\" HEIGHT=\"260\"> </td>\n");
				appendToHTML("        </tr>\n");
				appendToHTML("       </table>\n");
				appendToHTML("      </td>\n");                              
				appendToHTML("      <td width=\"500\">\n");                            
				appendToHTML("       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width='99.5'");
				appendToHTML("        <tr>\n");
				appendToHTML("          <td>\n");
				appendToHTML("           <table id=\"noteTBL\" border=\"0\" width='99.5%'>\n");
				appendToHTML("            <tr>\n");
				appendToHTML("             <td>");

				if (introFlag == 1)
				{
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZHEAD1: Report In Wizard"));
					appendToHTML("<br>\n");
					appendToHTML("			<p>");
					appendToHTML(fmt.drawReadValue(mgr.translate("PCMWREPORTINWORKORDERWIZHEAD1ROW1: In this Wizard you Report in existing Work Orders. The Work Order is after finishing this wizard set to status Finished. Beneath you can choose what Work Order to report.")));
					appendToHTML("</p>\n");
					appendToHTML("		  	<p>");
					appendToHTML(fmt.drawReadValue(mgr.translate("PCMWREPORTINWORKORDERWIZHEAD1ROW2: In the following windows you report the data needed for your report. You can choose to finish after the next window or continue to add more data or report time.")));
					appendToHTML("</p><br>           \n");
				}

				if (introFlag == 2)
				{
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZHEAD2: Work Done"));
					appendToHTML("<br>\n");
					appendToHTML("			<p>");
					appendToHTML(fmt.drawReadValue(mgr.translate("PCMWREPORTINWORKORDERWIZHEAD2ROW1: Register the information for the work done. After this you can choose to continue with additional information and time report, or to finish your registration.")));
					appendToHTML("</p><br>\n");
				}

				if (introFlag == 3)
				{
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZHEAD3: Performed Action and Classification"));
					appendToHTML("<br>\n");
					appendToHTML("			<p>");
					appendToHTML(fmt.drawReadValue(mgr.translate("PCMWREPORTINWORKORDERWIZHEAD3ROW1: Register the additional information for the work done. The classification information could be necessary for follow-up. Choose 'Next' to report time or 'Finish' to end.")));
					appendToHTML("</p><br>\n");
				}

				if (introFlag == 4)
				{
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZHEAD4: Time Report"));
					appendToHTML("<br>\n");
					appendToHTML("			<p>");
					appendToHTML(fmt.drawReadValue(mgr.translate("PCMWREPORTINWORKORDERWIZHEAD4ROW1: Here you can report your time. When ready press Finish.")));
					appendToHTML("</p><br>\n");
				}

				appendToHTML("	     </td>\n");
				appendToHTML("            </tr>\n");
				appendToHTML("       </table>\n");

				appendToHTML(headlay.generateDialog());		//XSS_Safe AMNILK 20070725

				appendToHTML("<table id=\"TST\" border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>\n");

				if (introFlag == 3)
				{
					appendToHTML("       <tr>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZSY: Symptom:"));
					appendToHTML("</td>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
					appendToHTML(fmt.drawSelect("ERRSYMPT", RowsetSympt.getRows(), sympt, ""));
					appendToHTML("</td>\n");
					appendToHTML("       </tr>\n");
					appendToHTML("       <tr>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZCA: Cause:"));
					appendToHTML("</td>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
					appendToHTML(fmt.drawSelect("ERRCAUSE", RowsetCause.getRows(), caus, ""));
					appendToHTML("</td>\n");
					appendToHTML("       </tr>\n");
					appendToHTML("       <tr>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZCL: Class:"));
					appendToHTML("</td>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
					appendToHTML(fmt.drawSelect("ERRCLAS", RowsetClass.getRows(), clas, ""));
					appendToHTML("</td>\n");
					appendToHTML("       </tr>\n");
					appendToHTML("       <tr>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZTY: Type:"));
					appendToHTML("</td>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
					appendToHTML(fmt.drawSelect("TYPE", RowsetType.getRows(), typ, ""));
					appendToHTML("</td>\n");
					appendToHTML("       </tr>\n");
					appendToHTML("       <tr>\n");
					appendToHTML("	   <td nowrap height=\"26\" align=\"left\" >");
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZDI: Discovery:"));
					appendToHTML("</td>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
					appendToHTML(fmt.drawSelect("DISCOVER", RowsetDisc.getRows(), disc, ""));
					appendToHTML("</td>\n");
					appendToHTML("       </tr>\n");
					appendToHTML("       <tr>\n");
					appendToHTML("	   <td nowrap height=\"26\" align=\"left\" >");
					appendToHTML(fmt.drawWriteLabel("PCMWREPORTINWORKORDERWIZPER: Performed Action:"));
					appendToHTML("</td>\n");
					appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
					appendToHTML(fmt.drawSelect("PERACT", RowsetPerAct.getRows(), peract, ""));
					appendToHTML("</td>\n");
					appendToHTML("       </tr>\n");
				}

				appendToHTML("    </table>\n");
			}

			if (introFlag != 0)
			{
				if (showAllTimeReports)
					appendToHTML("           <table id=\"timrepTBL\" border=\"0\" width='270'>\n");

				appendToHTML(modifiedItemlayGenDlg(mgr, itemlay.generateDialog()));	//XSS_Safe AMNILK 20070725

				if (showAllTimeReports)
					appendToHTML("    </table>\n");
			}

			appendToHTML("          </td>\n");
			appendToHTML("        </tr>\n");
			appendToHTML("      </table>\n");
			appendToHTML("      </td>\n");
			appendToHTML("    </tr>\n");
			appendToHTML("   </table>\n");

			if (mgr.isExplorer())
			{
				appendToHTML("  <table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=730>\n");
				appendToHTML("  <tr>\n");
				appendToHTML(fmt.drawReadValue(".............................................................."));
				appendToHTML(fmt.drawReadValue("............................................................."));
				appendToHTML(fmt.drawReadValue("............................................................"));
				appendToHTML("  </tr>\n");
				appendToHTML("  </table> \n");
				appendToHTML("            <table id=\"buttonTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=733>\n");
			}
			else
			{
				appendToHTML("  <table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=815>\n");
				appendToHTML("  <tr>\n");
				appendToHTML(fmt.drawReadValue("..................................................................."));
				appendToHTML(fmt.drawReadValue(".................................................................."));
				appendToHTML(fmt.drawReadValue("................................................................."));
				appendToHTML("  </tr>\n");
				appendToHTML("  </table> \n");
				appendToHTML("            <table id=\"buttonTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=810>\n");
			}   

			if (introFlag == 4)
			{
				appendToHTML("            <tr>\n");
				appendToHTML("              <td align=\"right\">");
				appendToHTML(fmt.drawSubmit("ADDTREP",mgr.translate("PCMWREPORTINWORKORDERWIZADDTRBUT: Add Another Time Report"),"ADDTREP"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("DELETECURRREP",mgr.translate("PCMWREPORTINWORKORDERWIZDELTRBUT: Delete Current Time Report"),"DELETECURRREP"));
				appendToHTML("</td>\n");
				appendToHTML("            </tr>\n");
				appendToHTML("            <tr> </tr>\n");
				appendToHTML("            <tr>\n");
				appendToHTML("              <td align=\"right\">");
				appendToHTML(fmt.drawSubmit("GETTIMEREP",mgr.translate("PCMWREPORTINWORKORDERWIZGETTRBUT: Time Reports"),"GETTIMEREP"));
				appendToHTML("&nbsp;");
				appendToHTML(fmt.drawSubmit("PREVTIMEREP",mgr.translate("PCMWREPORTINWORKORDERWIZPREVIOUSTIMEREPS: <Previous Time Report"),"PREVTIMEREP"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("NEXTTIMEREP",mgr.translate("PCMWREPORTINWORKORDERWIZNEXTTRBUT: Next Time Report>"),"NEXTTIMEREP"));
				appendToHTML("</td><br>\n");
				appendToHTML("            </tr>\n");
				appendToHTML("            <tr> </tr>\n");
			}

			appendToHTML("            <tr>\n");
			appendToHTML("              <td align=\"right\">");

			if (introFlag != 1)
			{
				appendToHTML(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWREPORTINWORKORDERWIZPREVIOUSBUT: <Previous"),"PREVIOUS"));
				appendToHTML("&nbsp;");
			}

			if (introFlag != 4)
			{
				appendToHTML(fmt.drawButton("NEXT",mgr.translate("PCMWREPORTINWORKORDERWIZNEXBUT: Next> "),"onClick=checkMandato()"));                            
				appendToHTML("&nbsp;");
			}

			//appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWREPORTINWORKORDERWIZCANBUT: Cancel"),"Cancel"));
			appendToHTML(fmt.drawButton("CANCEL",mgr.translate("PCMWREPORTINWORKORDERWIZCANBUT: Cancel"),"onClick=window.close()"));
			appendToHTML("&nbsp;\n");

			if (introFlag == 3)
				appendToHTML(fmt.drawButton("FINISH1",mgr.translate("PCMWREPORTINWORKORDERWIZFINBUT1: Finish"),"onClick=setFlagVal()"));

			if (introFlag == 4)
				appendToHTML(fmt.drawButton("FINISH",mgr.translate("PCMWREPORTINWORKORDERWIZFINBUT: Finish"),"onClick=checkMandato()"));

			appendToHTML("              </td>\n");
			appendToHTML("            </tr>\n");
			appendToHTML("          </table> \n");
			appendToHTML("   <p>\n");   

                        appendDirtyJavaScript("var chkAllMatIssued = false;\n");
                        if (chkAllMat == 1)
                            appendDirtyJavaScript("chkAllMatIssued = true;\n");

			appendDirtyJavaScript("var flag = '");
			appendDirtyJavaScript(introFlag);   	//XSS_Safe AMNILK 20070725
			appendDirtyJavaScript("'\n");

                        appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("   el = document.getElementById(el);\n");
			appendDirtyJavaScript("   if(el.style.display!='none')\n");
			appendDirtyJavaScript("   {  \n");
			appendDirtyJavaScript("      el.style.display='none';\n");
			appendDirtyJavaScript("      picTBL.style.display='none';\n");
			appendDirtyJavaScript("      buttonTBL.style.display='none';\n");
			appendDirtyJavaScript("      lineTBL.style.display='none';\n");
			appendDirtyJavaScript("      noteTBL.style.display='none';\n");
			appendDirtyJavaScript("      TST.style.display='none';\n");

			if ((introFlag != 0) && showAllTimeReports)
				appendDirtyJavaScript("      timrepTBL.style.display='none';\n");

			appendDirtyJavaScript("   }  \n");
			appendDirtyJavaScript("   else\n");
			appendDirtyJavaScript("   {  \n");
			appendDirtyJavaScript("      el.style.display='block';\n");
			appendDirtyJavaScript("      picTBL.style.display='block';\n");
			appendDirtyJavaScript("      buttonTBL.style.display='block';\n");
			appendDirtyJavaScript("      lineTBL.style.display='block';\n");
			appendDirtyJavaScript("      noteTBL.style.display='block';\n");
			appendDirtyJavaScript("      TST.style.display='block';\n");

			if ((introFlag != 0) && showAllTimeReports)
				appendDirtyJavaScript("      timrepTBL.style.display='block';\n");

			appendDirtyJavaScript("   }\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function validateEmpNo(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("    if (i == -1)\n");
			appendDirtyJavaScript("    {\n");
			appendDirtyJavaScript("        j = i;\n");
			appendDirtyJavaScript("        i = -1;\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("	if( !checkEmpNo(i) ) return;\n");
                        appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
                        appendDirtyJavaScript("	{\n");
                        appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('EMP_SIGNATURE',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('ITEM_NAME',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('ITEM_ORG_CODE',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('CMNT',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('SALESPARTCATALOGDESC',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('EMP_NO',i).value = '';\n");
                        appendDirtyJavaScript("		getField_('ITEM_CONTRACT',i).value = '';\n");
                        appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("	r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=EMP_NO'\n");
			appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
			appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
                        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
                        appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'EMP_NO',i,'Employee:') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,0);\n");
			appendDirtyJavaScript("		assignValue_('EMP_SIGNATURE',i,1);\n");
			appendDirtyJavaScript("		assignValue_('ITEM_NAME',i,2);\n");
			appendDirtyJavaScript("		assignValue_('ITEM_ORG_CODE',i,3);\n");
			appendDirtyJavaScript("		assignValue_('CMNT',i,4);\n");
			appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,5);\n");
			appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,6);\n");
			appendDirtyJavaScript("		assignValue_('EMP_NO',i,7);\n");
			appendDirtyJavaScript("		assignValue_('ITEM_CONTRACT',i,8);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("    if ( j == -1 )\n");
			appendDirtyJavaScript("    {\n");
                        appendDirtyJavaScript("	       if( getValue_('ROLE_CODE',i)=='' )\n");
			appendDirtyJavaScript("           validateItemOrgCode(1);\n");
                        appendDirtyJavaScript("	       else\n");
			appendDirtyJavaScript("           validateRoleCode(1);\n");
			appendDirtyJavaScript("        validateCatalogNo(1);\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function validateItemOrgCode(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("    if (i == -1)\n");
			appendDirtyJavaScript("    {\n");
			appendDirtyJavaScript("        j = i;\n");
			appendDirtyJavaScript("        i = -1;\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("	if( !checkItemOrgCode(i) ) return;\n");
			appendDirtyJavaScript("	r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=ITEM_ORG_CODE'\n");
			appendDirtyJavaScript("		+ '&ITEM_ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
			appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
			appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
			appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
			appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
			appendDirtyJavaScript("		+ '&SALESPARTCATALOGDESC=' + URLClientEncode(getValue_('SALESPARTCATALOGDESC',i))\n");
			appendDirtyJavaScript("		+ '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_NAME=' + URLClientEncode(getValue_('ITEM_NAME',i))\n");
			appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'ITEM_ORG_CODE',i,'Maintenance Organization:') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('ITEM_ORG_CODE',i,0);\n");
			appendDirtyJavaScript("		assignValue_('AMOUNT',i,1);\n");
			appendDirtyJavaScript("		assignValue_('CMNT',i,2);\n");
			appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,3);\n");
			appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,4);\n");
			appendDirtyJavaScript("		assignValue_('ITEM_CONTRACT',i,5);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("    if (j == -1)\n");
			appendDirtyJavaScript("        validateCatalogNo(1);\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("}\n");  

			appendDirtyJavaScript("function validateRoleCode(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("    if (i == -1)\n");
			appendDirtyJavaScript("    {\n");
			appendDirtyJavaScript("        j = i;\n");
			appendDirtyJavaScript("        i = -1;\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("    if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
			appendDirtyJavaScript("	r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
		       appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
			appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
			appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_AGREEMENT_ID=' + URLClientEncode(getValue_('ITEM_AGREEMENT_ID',i))\n");
			appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
			appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
			appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
			appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
			appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
			appendDirtyJavaScript("		+ '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_NAME=' + URLClientEncode(getValue_('ITEM_NAME',i))\n");
			appendDirtyJavaScript("		+ '&SALESPARTCOST=' + URLClientEncode(getValue_('SALESPARTCOST',i))\n");
			appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
			appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft:') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
			appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,1);\n");
			appendDirtyJavaScript("		assignValue_('AMOUNT',i,2);\n");
			appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,3);\n");
			appendDirtyJavaScript("		assignValue_('CMNT',i,4);\n");
			appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,5);\n");
			appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,6);\n");
			appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,7);\n");
			appendDirtyJavaScript("		assignValue_('ITEM_CONTRACT',i,8);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("    if (j == -1)\n");
			appendDirtyJavaScript("    {\n");
                        appendDirtyJavaScript("	       if( getValue_('ROLE_CODE',i)=='' )\n");
			appendDirtyJavaScript("           validateItemOrgCode(1);\n");
			appendDirtyJavaScript("        validateCatalogNo(1);\n");
			appendDirtyJavaScript("        validatePriceListNo(1);\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function validateCatalogNo(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("    if (i == -1)\n");
			appendDirtyJavaScript("    {\n");
			appendDirtyJavaScript("        j = i;\n");
			appendDirtyJavaScript("        i = -1;\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("	if( !checkCatalogNo(i) ) return;\n");
			appendDirtyJavaScript("	r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
			appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
			appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
			appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
			appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_AGREEMENT_ID=' + URLClientEncode(getValue_('ITEM_AGREEMENT_ID',i))\n");
			appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
			appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
			appendDirtyJavaScript("		+ '&SALESPARTCATALOGDESC=' + URLClientEncode(getValue_('SALESPARTCATALOGDESC',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_NAME=' + URLClientEncode(getValue_('ITEM_NAME',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
			appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
			appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
			appendDirtyJavaScript("		assignValue_('AMOUNT',i,1);\n");
			appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,2);\n");
			appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,3);\n");
			appendDirtyJavaScript("		assignValue_('CMNT',i,4);\n");
			appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,5);\n");
			appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,6);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("    if (j == -1)\n");
			appendDirtyJavaScript("        validatePriceListNo(1);\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function validatePriceListNo(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("    r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
			appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
			appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
			appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
			appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
			appendDirtyJavaScript("		+ '&ITEM_AGREEMENT_ID=' + URLClientEncode(getValue_('ITEM_AGREEMENT_ID',i))\n");
			appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
			appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
			appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
			appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,1);\n");
			appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,2);\n");
			appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,3);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function validateItemContract(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("    if (i == -1)\n");
			appendDirtyJavaScript("    {\n");
			appendDirtyJavaScript("        j = i;\n");
			appendDirtyJavaScript("        i = -1;\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("	if( !checkItemContract(i) ) return;\n");
			appendDirtyJavaScript("	r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=ITEM_CONTRACT'\n");
			appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'ITEM_CONTRACT',i,'Site:') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('ITEM_COMPANY',i,0);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("    if (j == -1)\n");
			appendDirtyJavaScript("        validateCatalogNo(1);\n");
			appendDirtyJavaScript("    j = 0;\n");
			appendDirtyJavaScript("}\n");  

			appendDirtyJavaScript("function validateWoNo(i)\n");
			appendDirtyJavaScript("{   \n");
			appendDirtyJavaScript("    document.form.__GETREC.value = '0';\n");
			appendDirtyJavaScript("    setDirty();\n");
			appendDirtyJavaScript("	if( !checkWoNo(i) ) return;\n");
			appendDirtyJavaScript("        document.form.__GETREC.value = '1';\n");
			appendDirtyJavaScript("    submit();\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function setFlagVal()\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("   var result = true;");
                        appendDirtyJavaScript("   if (chkAllMatIssued == true){\n");
			appendDirtyJavaScript("      if (confirm(\"");
			appendDirtyJavaScript(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERUNISSUE: All required material has not been issued. Do you want to continue?"));
			appendDirtyJavaScript("\"))\n");
			appendDirtyJavaScript("          result = true;\n");
			appendDirtyJavaScript("      else\n");
			appendDirtyJavaScript("          result = false;\n");
			appendDirtyJavaScript("   }\n");
                        appendDirtyJavaScript("   if (result == true)\n");
                        appendDirtyJavaScript("    {\n");
			appendDirtyJavaScript("		document.form.BUTTONFSH1.value = \"AAAA\";\n");
			appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie1\", \"FALSE\", '', COOKIE_PATH); \n");
			appendDirtyJavaScript("		f.submit();\n");
			appendDirtyJavaScript("     }\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function checkMandato()\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript(" if (flag == 1)	\n");
			appendDirtyJavaScript(" {   \n");
			appendDirtyJavaScript("  if(f.WO_NO.value == '')\n");
			appendDirtyJavaScript("            (checkWoNo(-1))\n");
			appendDirtyJavaScript("  else\n");
			appendDirtyJavaScript("  	if(f.MCH_CODE.value == '')\n");
			appendDirtyJavaScript("            checkMchCode(-1);\n");
			appendDirtyJavaScript("  else\n");
			appendDirtyJavaScript("  {\n");
			appendDirtyJavaScript("	  document.form.BUTTONVAL.value = \"AAAA\";\n");
			appendDirtyJavaScript("	  f.submit();\n");
			appendDirtyJavaScript("	 }\n"); 
			appendDirtyJavaScript(" }\n");
			appendDirtyJavaScript(" if(flag == 2)\n");
			appendDirtyJavaScript(" {\n");
			appendDirtyJavaScript("   if( f.DUMMY_REAL_F_DATE.value == '' )  \n");
			appendDirtyJavaScript("            checkDummyRealFDate(-1);\n");
			appendDirtyJavaScript("	  else\n");
			appendDirtyJavaScript("	  {\n");
			appendDirtyJavaScript("	   document.form.BUTTONVAL.value = \"AAAA\";\n");
			appendDirtyJavaScript("	   f.submit();\n");
			appendDirtyJavaScript("	  }\n");
			appendDirtyJavaScript(" }\n");
			appendDirtyJavaScript(" if(flag == 3)\n");
			appendDirtyJavaScript(" {    \n");
			appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"AAAA\";\n");
			appendDirtyJavaScript("		f.submit();\n");
			appendDirtyJavaScript(" }\n"); 
			appendDirtyJavaScript(" if(flag == 4)");      
			appendDirtyJavaScript(" {\n"); 
			appendDirtyJavaScript("   var result = true;");
                        appendDirtyJavaScript("   if (chkAllMatIssued == true){\n");
			appendDirtyJavaScript("      if (confirm(\"");
			appendDirtyJavaScript(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERUNISSUE: All required material has not been issued. Do you want to continue?"));
			appendDirtyJavaScript("\"))\n");
			appendDirtyJavaScript("          result = true;\n");
			appendDirtyJavaScript("      else\n");
			appendDirtyJavaScript("          result = false;\n");
			appendDirtyJavaScript("   }\n");
			//Bug 89399, Start
                        appendDirtyJavaScript("   if (result == true)\n");
                        appendDirtyJavaScript("   {\n");
			appendDirtyJavaScript("   if (!checkZeroQty())  \n");
			appendDirtyJavaScript("      result = false;\n"); 
                        appendDirtyJavaScript("   }\n");
			//Bug 89399, End
                        appendDirtyJavaScript("   if (result == true)\n");
			appendDirtyJavaScript("     {\n");
			appendDirtyJavaScript("        if((");
			appendDirtyJavaScript(itemlay.isEditLayout());			//XSS_Safe AMNILK 20070725
			appendDirtyJavaScript(" == true)) \n");
			appendDirtyJavaScript("		{\n");        
			appendDirtyJavaScript("        if(f.ITEM_CONTRACT.value == '')");
			appendDirtyJavaScript("            checkItemContract(-1);\n");
			appendDirtyJavaScript("        else if(f.QTY.value == '')");      
			appendDirtyJavaScript("            checkQty(-1);\n");
			appendDirtyJavaScript("        else if(f.CRE_DATE.value == '')");
			appendDirtyJavaScript("            checkCreDate(-1);\n");
			appendDirtyJavaScript("	       else\n");
			appendDirtyJavaScript("		{\n");
			appendDirtyJavaScript("		 document.form.BUTTONFSH.value = \"AAAA\";\n");
			appendDirtyJavaScript("		 f.submit();\n");
			appendDirtyJavaScript("		}\n");
			appendDirtyJavaScript("		}\n");
			appendDirtyJavaScript("    else\n");
			appendDirtyJavaScript("		{\n");        
			appendDirtyJavaScript("		 document.form.BUTTONFSH.value = \"AAAA\";\n");
			appendDirtyJavaScript("		 f.submit();\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("    }\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("if ('");

			appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelFlag)); //XSS_Safe AMNILK 20070725
			appendDirtyJavaScript("' == \"TRUE\")\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    window.close();\n");
			appendDirtyJavaScript("}\n");

                        appendDirtyJavaScript("function lovEmpNo(i,params)\n");
                        appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("	if(params) param = params;\n");
                        appendDirtyJavaScript("	else param = '';\n");
                        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
                        appendDirtyJavaScript("	var key_value = (getValue_('EMP_NO',i).indexOf('%') !=-1)? getValue_('EMP_NO',i):'';\n");
                        appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
                        appendDirtyJavaScript("       {\n");
                        appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
                        appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Employee&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
                        appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
                        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
                        appendDirtyJavaScript("       }\n");
                        appendDirtyJavaScript(" else\n");
                        appendDirtyJavaScript("       {\n");        
                        appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
                        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Employee&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
                        appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
                        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
                        appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
                        appendDirtyJavaScript("       }\n");
                        appendDirtyJavaScript("}\n"); 

                        appendDirtyJavaScript("function lovItemOrgCode(i,params)\n");
                        appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("	if(params) param = params;\n");
                        appendDirtyJavaScript("	else param = '';\n");
                        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
                        appendDirtyJavaScript("	var key_value = (getValue_('ITEM_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM_ORG_CODE',i):'';\n");
                        appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
                        appendDirtyJavaScript("       {\n");
                        appendDirtyJavaScript("	            if( getValue_('EMP_NO',i)=='' )\n");
                        appendDirtyJavaScript("             {\n");
                        appendDirtyJavaScript("                  openLOVWindow('ITEM_ORG_CODE',i,\n");
                        appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n"); 
                        appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("       	             ,550,500,'validateItemOrgCode');\n");
                        appendDirtyJavaScript("             }\n");
                        appendDirtyJavaScript("	            else\n");
                        appendDirtyJavaScript("             {\n");
                        appendDirtyJavaScript("                 openLOVWindow('ITEM_ORG_CODE',i,\n");
                        appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n"); 
                        appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('EMP_NO',i))\n");
                        appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("       	            ,550,500,'validateItemOrgCode');\n");
                        appendDirtyJavaScript("             }\n");
                        appendDirtyJavaScript("       }\n");
                        appendDirtyJavaScript(" else\n");
                        appendDirtyJavaScript("       {\n");        
                        appendDirtyJavaScript("              openLOVWindow('ITEM_ORG_CODE',i,\n");
                        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n"); 
                        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
                        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
                        appendDirtyJavaScript("       	   ,550,500,'validateItemOrgCode');\n");
                        appendDirtyJavaScript("       }\n");
                        appendDirtyJavaScript("}\n");

                        appendDirtyJavaScript("function lovRoleCode(i,params)\n");
                        appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("	if(params) param = params;\n");
                        appendDirtyJavaScript("	else param = '';\n");
                        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
                        appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
                        appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
                        appendDirtyJavaScript("       {\n");
                        appendDirtyJavaScript("	            if( getValue_('ITEM_ORG_CODE',i)=='' )\n");
                        appendDirtyJavaScript("             {\n");
                        appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
                        appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
                        appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
                        appendDirtyJavaScript("             }\n");
                        appendDirtyJavaScript("	            else\n");
                        appendDirtyJavaScript("             {\n");
                        appendDirtyJavaScript("                 openLOVWindow('ROLE_CODE',i,\n");
                        appendDirtyJavaScript("                     '../mscomw/EmployeeRoleLov.page?&__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
                        appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
                        appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("       	            ,550,500,'validateRoleCode');\n");
                        appendDirtyJavaScript("             }\n");
                        appendDirtyJavaScript("       }\n");
                        appendDirtyJavaScript(" else\n");
                        appendDirtyJavaScript("       {\n");        
                        appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
                        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
                        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
                        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
                        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
                        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
                        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
                        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
                        appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
                        appendDirtyJavaScript("       }\n");
                        appendDirtyJavaScript("}\n");

			//Bug 89399, Start
                        appendDirtyJavaScript("function checkZeroQty()\n");
                        appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("   if (f.QTY.value != '' && f.QTY.value == 0 )  \n");
			appendDirtyJavaScript("   {\n");
			appendDirtyJavaScript("             alert('");
			appendDirtyJavaScript(                 mgr.translate("PCMWREPINWIZNOTVALIDQTY1: Reported hours value for "));
			appendDirtyJavaScript(sWoNo);
			appendDirtyJavaScript(                 mgr.translate("PCMWREPINWIZNOTVALIDQTY2:  is not allowed to be zero."));
			appendDirtyJavaScript("             ');\n");
			appendDirtyJavaScript("      return false;\n"); 
			appendDirtyJavaScript("   } \n");
			appendDirtyJavaScript("   return true;\n"); 
                        appendDirtyJavaScript("}\n");
			//Bug 89399, End
		}
		else
		{
			appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
			appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
			appendDirtyJavaScript("window.location = '");

			if (toDefaultPage)
			{
				appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));	//XSS_Safe AMNILK 20070725
				appendDirtyJavaScript("';\n");
			}
			else
			{
				appendDirtyJavaScript(url);
				appendDirtyJavaScript("'+\"Navigator.page?MAINMENU=Y&NEW=Y\";\n"); 
			} 

			appendDirtyJavaScript("    if (");
			appendDirtyJavaScript(    mgr.isExplorer());
			appendDirtyJavaScript("    )\n");
			appendDirtyJavaScript("    { \n");
			appendDirtyJavaScript("      window.open('");
			appendDirtyJavaScript(url);
			appendDirtyJavaScript("'+\"pcmw/ReportInWorkOrderWiz.page?RANDOM_VAL=\"+Math.random(),\"frmReportInWorkOrderWiz\",\"resizable=yes,status=yes,menubar=no,height=525,width=760\");      \n");
			appendDirtyJavaScript("    } \n");
			appendDirtyJavaScript("    else \n");
			appendDirtyJavaScript("    { \n");
			appendDirtyJavaScript("      window.open('");
			appendDirtyJavaScript(url);
			appendDirtyJavaScript("'+\"pcmw/ReportInWorkOrderWiz.page?RANDOM_VAL=\"+Math.random(),\"frmReportInWorkOrderWiz\",\"resizable=yes,status=yes,menubar=no,height=550,width=850\");      \n");
			appendDirtyJavaScript("    } \n");
		}
	}

}
