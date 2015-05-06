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
*  File        : MaintObjectLov.java 
*  Modified    :
*  SHAFLK      080325  Bug 72539, Added new Lov.
*  CHANLK      100212  Bug 88985, increase field size of MCH_CODE, MCH_NAME.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintObjectLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintObjectLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public MaintObjectLov(ASPManager mgr, String page_path)
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
		blk.setView("MAINT_OBJECT_LOV");

	        blk.addField("CONTRACT").
		setLabel("PCMWMAINTOBJECTLOVCONTRACT: Site").
		setUpperCase().              
		setSize(20);

 		//Bug 88985,Start 
		blk.addField("MCH_CODE").
		setLabel("PCMWMAINTOBJECTLOVOBJID: Object ID").
		setUpperCase(). 
		setMaxLength(100).
		setSize(100);

		blk.addField("MCH_NAME").
		setLabel("PCMWMAINTOBJECTLOVOBJDESC: Description").
		setSize(150);
		//Bug 88985,End 

		blk.addField("OBJ_LEVEL").
		setLabel("PCMWMAINTOBJECTLOVOBJLEVEL: Object Level").
		setUpperCase().              
		setSize(20);

		blk.addField("LATEST_TRANSACTION").
		setLabel("PCMWMAINTOBJECTLOVLATESTTRANSACTION: Latest Transaction").
		setUpperCase().              
		setSize(50);

		blk.addField("OPERATIONAL_STATUS").
		setLabel("PCMWMAINTOBJECTLOVOPESTATE: Operational Status").
		setSize(50);

		blk.addField("MCH_CODE_CONT").
		setFunction("MCH_CODE||'~'||MCH_NAME||'~'||CONTRACT").
		setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWMAINTOBJECTLOVOBJID: Object ID"));
		tbl.setKey("MCH_CODE_CONT");              
		defineLOV();
	}
}
