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
*  File        : MaintMaterialReqLineLov.java 
*  Modified    :
*    ASP2JAVA Tool  2001-05-20  - Created Using the ASP file MaintMaterialReqLineLov.asp
*    031222         ARWILK        Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintMaterialReqLineLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintMaterialReqLineLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public MaintMaterialReqLineLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		trans = mgr.newASPTransactionBuffer();
		okFind();
	}

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(blk);
		q.addWhereCondition("OBJSTATE IN ('RELEASED','STARTED','WORKDONE')");

		mgr.perform(trans);
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		blk = mgr.newASPBlock("HEAD");
		blk.setView("MAINT_MATERIAL_REQ_LINE");

		blk.addField("WO_NO").
		setLabel("PCMWMAINTMATERIALREQLINELOVWONO: WO Number");

		blk.addField("MAINT_MATERIAL_ORDER_NO").
		setLabel("PCMWMAINTMATERIALREQLINELOVMAINTMATORDNO: Order No");

		blk.addField("PART_NO").
		setLabel("PCMWMAINTMATERIALREQLINELOVPARTNO: Part No");

		blk.addField("DATE_REQUIRED").
		setLabel("PCMWMAINTMATERIALREQLINELOVDATEREQUIRED: Date Required");

		tbl = mgr.newASPTable(blk);
		tbl.setTitle(mgr.translate("PCMWMAINTMATERIALREQLINELOVTITLE: WO No"));

		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		defineLOV();
	}
}
