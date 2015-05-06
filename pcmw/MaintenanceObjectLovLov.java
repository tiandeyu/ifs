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
*  File        : MaintenanceObjectLovLov.java 
*  Modified    :
*  INROLK      :2002-12-06  - Created.  Lov for the Objetc ID in PM Action.
*  ARWILK       031222        Edge Developments - (Removed clone and doReset Methods)
*  NAMELK       041108        Non Standard Translation Tags Corrected.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceObjectLovLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObjectLovLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenanceObjectLovLov(ASPManager mgr, String page_path)
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
		blk.setView("MAINTENANCE_OBJECT_LOV");

		blk.addField("CONTRACT").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVCONTRACT: Site").
		setUpperCase().              
		setSize(20);

		blk.addField("MCH_CODE").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVMCHCODE: Object ID").
		setUpperCase().              
		setSize(25);

		blk.addField("MCH_NAME").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVMCHNAME: Object Description").
		setSize(20);

		blk.addField("EQUIPMENT_MAIN_POSITION").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVEQUIPMAINPOS: Equipment Main Position").
		setSize(6);

		blk.addField("OBJ_LEVEL").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVOBJLEVEL: Object Level").
		setUpperCase().              
		setSize(20);

		blk.addField("LATEST_TRANSACTION").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVLATESTTRANS: Latest Transaction").
		setUpperCase().              
		setSize(50);

		blk.addField("OPERATIONAL_STATUS").
		setLabel("PCMWMAINTENANCEOBJECTLOVLOVOPERSTATUS: Operational Status").
		setUpperCase().              
		setSize(50);


		blk.addField("MCH_CONTRACT").
		setFunction("MCH_CODE||'~'||MCH_NAME||'~'||CONTRACT")
		.setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWMAINTENANCEOBJECTLOVLOVOBJECTID: Objetc ID"));
		tbl.setKey("MCH_CONTRACT");              
		defineLOV();
	}
}
