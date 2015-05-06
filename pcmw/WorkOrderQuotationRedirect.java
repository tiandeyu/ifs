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
*  File        : WorkOrderQuotationRedirect.java 
*  Modified    :
*    ASP2JAVA Tool  2001-04-01  - Created Using the ASP file WorkOrderQuotationRedirect.asp
*    JEWI           2001-04-01  - Correctes conversion errors and done necessary adjustments.
*    INROLK         2002-12-09  - Added HEAD_CONTRACT to Qurey String when called from Pm Action.
*    ARWILK         031223        Edge Developments - (Removed clone and doReset Methods)
*    NIJALK         060210        Call 133579: Changed *.asp url pattern to *.page pattern.
*    AMNILK         060731        Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*    AMDILK         060807        Merged with Bug Id 58214
*    AMDILK         060817        Bug 58216, Eliminated SQL errors in web applications. Modified the method search()
*    AMDILK         060906        Merged with the Bug Id 58216
*    AMNILK         070727        Eliminated XXS Security Vulnerabilities.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderQuotationRedirect extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderQuotationRedirect");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String sMchCode;
	private String sContract;
	private String sCustomerNo;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderQuotationRedirect(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		String sObjLevel;

		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if ((!mgr.isEmpty(mgr.getQueryStringValue("HEAD_MCH_CODE"))) && (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE_CONTRACT")))) //mgr.dataTransfered()
		{
			sMchCode = mgr.readValue("HEAD_MCH_CODE");
			sContract = mgr.readValue("MCH_CODE_CONTRACT");

			search();

			sObjLevel = mgr.getASPField("OBJ_LEVEL").getValue(); 

			if (!mgr.isEmpty(sObjLevel))
				mgr.redirectTo("../equipw/EquipmentFunctional.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
			else
			       mgr.redirectTo("../equipw/EquipmentSerial.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
			mgr.endResponse();
		}
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("ITEM0_MCH_CODE"))) && (!mgr.isEmpty(mgr.getQueryStringValue("ITEM0_MCHCONTRACT"))))	//mgr.dataTransfered()
		{
			sMchCode = mgr.readValue("ITEM0_MCH_CODE");
			sContract = mgr.readValue("ITEM0_MCHCONTRACT");

			search();

			sObjLevel = mgr.getASPField("OBJ_LEVEL").getValue(); 

			if (!mgr.isEmpty(sObjLevel))
				mgr.redirectTo("../equipw/EquipmentFunctional.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
			else
				mgr.redirectTo("../equipw/EquipmentSerial.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
			mgr.endResponse();
		}
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE"))) && (!mgr.isEmpty(mgr.getQueryStringValue("CONTRACT"))))
		{
			sMchCode = mgr.readValue("MCH_CODE");
			sContract = mgr.readValue("CONTRACT");

			search();

			sObjLevel = mgr.getASPField("OBJ_LEVEL").getValue(); 

			if (!mgr.isEmpty(sObjLevel))
				mgr.redirectTo("../equipw/EquipmentFunctional.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
		        else
				mgr.redirectTo("../equipw/EquipmentSerial.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
			mgr.endResponse();
		}
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE"))) && (!mgr.isEmpty(mgr.getQueryStringValue("HEAD_CONTRACT"))))
		{
			sMchCode = mgr.readValue("MCH_CODE");
			sContract = mgr.readValue("HEAD_CONTRACT");

			search();

			sObjLevel = mgr.getASPField("OBJ_LEVEL").getValue(); 

			if (!mgr.isEmpty(sObjLevel))
			       mgr.redirectTo("../equipw/EquipmentFunctional.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
		        else
			       mgr.redirectTo("../equipw/EquipmentSerial.page?MCH_CODE="+mgr.URLEncode(sMchCode)+"&CONTRACT="+mgr.URLEncode(sContract));
				
			mgr.endResponse();
		}

		else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMERAGREEMENTCUSTOMER_NO")))
		{
			sCustomerNo = mgr.readValue("CUSTOMERAGREEMENTCUSTOMER_NO");
			mgr.redirectTo("../enterw/CustomerInfo.page?CUSTOMER_ID="+mgr.URLEncode(sCustomerNo));
			mgr.endResponse();
		}
		else
		{
			mgr.endResponse();
		}
	}

	public void  search()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk);
		q.addWhereCondition("CONTRACT = ? and MCH_CODE = ?");
		q.addParameter("CONTRACT", sContract);
		q.addParameter("MCH_CODE", sMchCode);

		q.includeMeta("ALL");

		mgr.submit(trans);
		eval(itemblk.generateAssignments());   
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		mgr.beginASPEvent();

		itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("ITEM_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk.addField("ITEM_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk.addField("MCH_CODE");                   
		f.setHidden();

		f = itemblk.addField("CONTRACT");
		f.setHidden();

		f = itemblk.addField("OBJ_LEVEL");
		f.setHidden();

		itemblk.setView("EQUIPMENT_ALL_OBJECT");
		itemset = itemblk.getASPRowSet();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "" ;
	}

	protected String getTitle()
	{
		return "";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<html>\n");
		appendToHTML("<head>\n");
		appendToHTML("<title></title>\n");
		appendToHTML("</head>\n");
		appendToHTML(itemblk.generateHiddenFields());	//XSS_Safe AMNILK 20070726
		appendToHTML("<body>\n");
		appendToHTML("</body>\n");
		appendToHTML("</html>\n");
	}

}
