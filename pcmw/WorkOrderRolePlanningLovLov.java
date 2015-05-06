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
*  File        : WorkOrderRolePlanningLovLov.java 
*  Modified    :
*  JEJALK  021218  Created.  Lov for the Plan_Line_no in Time reported By Employees.
*  CHAMLK  021223  Added Reference_No to the Lov.
*  JEJALK  030331  Renamed Row No, Refenece Number to Operation  No and Reference No.
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  SHAFLK  060914  Bug 60342, Added new fields TEAM_ID, TEAM_CONTRACT.
*  AMNILK  060926  Merged Bug Id: 60342.
*  SAFALK  090813  Bug 85179, Corrected misspelt page title.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderRolePlanningLovLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderRolePlanningLovLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderRolePlanningLovLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();               
	}        

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		blk = mgr.newASPBlock("HEAD");
		blk.setView("WORK_ORDER_ROLE_PLANNING_LOV");

		blk.addField("WO_NO","Number").
		setSize(15).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONO: WO Number");              

		blk.addField("ROW_NO","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOROWNO: Operation No");             

		blk.addField("DESCRIPTION").
		setSize(40).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONODESCRIPTION: Description");

		blk.addField("ORG_CODE").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOORGCODE: Maintenance Organization").
		setUpperCase();

		blk.addField("ROLE_CODE").
		setSize(10).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOROLECODE: Craft").
		setUpperCase();

		blk.addField("SIGN").
		setSize(20).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOSIGN: Signature").
		setUpperCase();

		blk.addField("DATE_FROM","Date").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONODATEFORM: Date From").
		setSize(20);

		blk.addField("DATE_TO","Date").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONODATETO: Date To").
		setSize(20);

		blk.addField("PLAN_LINE_NO","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOPLANLINNUMB: Plan Line No").
		setSize(20);

		blk.addField("CATALOG_CONTRACT").
		setSize(5).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOCATCONTRA: Site");

		blk.addField("CATALOG_NO").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOCATNUMB: Sales Part No").
		setUpperCase();

		blk.addField("PRICE_LIST_NO").
		setSize(10).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOPRICELISTNO:  Price List No").
		setUpperCase();

		blk.addField("QUANTITY","Number").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOQUANTI:  Quantity");

		blk.addField("SALES_PRICE","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOSALEPRIC: Sales Price").
		setSize(20);

		blk.addField("WORK_ORDER_INVOICE_TYPE").
		setSize(20).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOWOORDINVOTY: Work Order Invoice Type");   

		blk.addField("DISCOUNT").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONODISCOUT: Discount").
		setUpperCase();

		blk.addField("REFERENCE_NUMBER").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVWONOREFERENCENO: Reference No").
		setUpperCase();

		blk.addField("TEAM_CONTRACT").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVTEAMCONTRACT: Team Site").
		setUpperCase();

		blk.addField("TEAM_ID").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVLOVTEAMID: Team Id").
		setUpperCase();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      //Bug 85179 - Corrected misspelt title.
		tbl.setTitle(mgr.translate("PCMWWORKORDERROLEPLANNINGLOVLOVWONOPLANLINENO: Plan Line No"));
		tbl.setKey("PLAN_LINE_NO");              
		defineLOV(); 
	}
}
