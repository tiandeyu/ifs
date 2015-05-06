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
*  File        : ActiveSeparateLov.java 
*  Modified    :
*  ARWILK  010219  Created.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040716  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparateLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparateLov(ASPManager mgr, String page_path)
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
		blk.setView("ACTIVE_SEPARATE");

		blk.addField("WO_NO").
		setLabel("PCMWACTIVESEPARATELOVWONO: WO Number").
		setSize(12);

		blk.addField("CONTRACT").
		setLabel("PCMWACTIVESEPARATELOVCONTRACT: Contract").
		setSize(12);

		blk.addField("ERR_DESCR").
		setLabel("PCMWACTIVESEPARATELOVERRDESCR: Directive").
		setSize(50).
		setMaxLength(60);

		blk.addField("PLAN_S_DATE","Datetime").
		setLabel("PCMWACTIVESEPARATELOVPLANSDATE: Planned Start").
		setSize(20);

		blk.addField("WORK_DONE").
		setLabel("PCMWACTIVESEPARATELOVWORKDONE: Work Done").
		setSize(30);

		blk.addField("ORG_CODE").
		setLabel("PCMWACTIVESEPARATELOVORGCODE: Maintenance Organization").
		setSize(12).
		setUpperCase();

		blk.addField("ERR_TYPE").
		setLabel("PCMWACTIVESEPARATELOVERRTYPE: Type").
		setSize(12).
		setUpperCase();

		blk.addField("MCH_CODE").
		setLabel("PCMWACTIVESEPARATELOVMCHCODE: Object ID").
		setMaxLength(100).
		setSize(12).
		setUpperCase();

		blk.addField("ERR_CAUSE").
		setLabel("PCMWACTIVESEPARATELOVERRCAUSE: Error Cause").
		setSize(30).
		setUpperCase();

		blk.addField("PLAN_F_DATE","Datetime").
		setLabel("PCMWACTIVESEPARATELOVPLANFDATE: Planned Completion").
		setSize(20);

		blk.addField("REPORT_IN_BY_ID").
		setLabel("PCMWACTIVESEPARATELOVREPORTINBYID: Reported in by ID").
		setSize(12).
		setUpperCase();

		blk.addField("REPORT_IN_BY").
		setSize(12).
		setLabel("PCMWACTIVESEPARATELOVREPORTINBY: Reported in by");

		blk.addField("FIXED_PRICE").
		setSize(12).
		setLabel("PCMWACTIVESEPARATELOVFIXEDPRICE: Fixed Price").
		setSelectBox().
		enumerateValues("FIXED_PRICE_API").
		setReadOnly().
		setMaxLength(20);

		blk.addField("CRITICALITY").
		setSize(12).
		setLabel("PCMWACTIVESEPARATELOVCRITICALITY:  Criticality").
		setUpperCase();

		blk.addField("GROUP_ID").
		setSize(12).
		setLabel("PCMWACTIVESEPARATELOVGROUPID:  Group ID").
		setUpperCase();

		blk.addField("GENERATE_NOTE").
		setSize(12).
		setLabel("PCMWACTIVESEPARATELOVGENERATENOTE: PM Type").
		setUpperCase();

		tbl = mgr.newASPTable(blk);

		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

		tbl.setTitle(mgr.translate("PCMWACTIVESEPARATELOVTITLE: WO No"));
		defineLOV();
	}

}
