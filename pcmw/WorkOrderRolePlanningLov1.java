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
*  File        : WorkOrderRolePlanningLov1.java
* --------------------------------- Edge - SP1 Merge -------------------------
*  THWILK  040325  Merge with SP1.
*  SHAFLK  040309  Bug id 43248, Created.
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
* ----------------------------------------------------------------------------
*  SAFALK  090813  Bug 85179, Corrected misspelt page title.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkOrderRolePlanningLov1 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderRolePlanningLov1");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderRolePlanningLov1(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		super.doReset();               
	}

	public ASPPoolElement clone(Object obj) throws FndException
	{
		WorkOrderRolePlanningLov1 page = (WorkOrderRolePlanningLov1)(super.clone(obj));

		// Cloning immutable attributes
		page.blk = page.getASPBlock(blk.getName());
		page.tbl = page.getASPTable(tbl.getName());
		page.lay = page.blk.getASPBlockLayout();              

		return page;
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
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1WONO: WO Number");              

		blk.addField("ROW_NO","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1ROWNO: Operation No");             

		blk.addField("DESCRIPTION").
		setSize(40).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1DESCRIPTION: Description");

		blk.addField("ORG_CODE").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1ORGCODE: Maintenance Organization").
		setUpperCase();

		blk.addField("ROLE_CODE").
		setSize(10).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1ROLECODE: Craft").
		setUpperCase();

		blk.addField("SIGN").
		setSize(20).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1SIGN: Signature").
		setUpperCase();

		blk.addField("DATE_FROM","Date").
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1DATEFORM: Date From").
		setSize(20);

		blk.addField("DATE_TO","Date").
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1DATETO: Date To").
		setSize(20);

		blk.addField("PLAN_LINE_NO","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1PLANLINNUMB: Plan Line No").
		setSize(20);

		blk.addField("CATALOG_CONTRACT").
		setSize(5).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1CATCONTRA: Site");

		blk.addField("CATALOG_NO").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1CATNUMB: Sales Part No").
		setUpperCase();

		blk.addField("PRICE_LIST_NO").
		setSize(10).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1PRICELISTNO:  Price List No").
		setUpperCase();

		blk.addField("QUANTITY","Number").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1QUANTI:  Quantity");

		blk.addField("SALES_PRICE","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1SALEPRIC: Sales Price").
		setSize(20);

		blk.addField("WORK_ORDER_INVOICE_TYPE").
		setSize(20).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1WOORDINVOTY: Work Order Invoice Type");   

		blk.addField("DISCOUNT").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1DISCOUT: Discount").
		setUpperCase();

		blk.addField("REFERENCE_NUMBER").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOV1REFERENCENO: Reference No").
		setUpperCase();

		blk.addField("RETURNVAL").
		setFunction("PLAN_LINE_NO||'~'||ROLE_CODE||'~'||ORG_CODE||'~'||CATALOG_NO").
		setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      //Bug 85179 - Corrected misspelt title.
		tbl.setTitle(mgr.translate("PCMWWORKORDERROLEPLANNINGLOV1PLANLINENO: Plan Line No"));
		tbl.setKey("RETURNVAL");              
		defineLOV(); 



	}

}
