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
*  File        : MaintenanceObjectLov1.java 
*  Modified    :
*  BUNILK      :2002-12-06  - Created.  Lov page for the Object ID in Prepare Work Order forms.
*  ARWILK       031222        Edge Developments - (Removed clone and doReset Methods)
*  NAMELK       041105        Non Standard Translation Tags Corrected.
*  CHANLK       100212  		Bug 88985, increase field size of MCH_CODE, MCH_NAME.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceObjectLov1 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObjectLov1");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenanceObjectLov1(ASPManager mgr, String page_path)
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
//		blk.setView("MAINTENANCE_OBJECT");
		blk.setView("MAINT_OBJECT2_LOV");
		blk.addField("CONTRACT").
		setLabel("PCMWMAINTENANCEOBJECTLOV1CONTRACT: Site").
		setUpperCase().              
		setSize(20);

		//Bug 88985, Start.
		blk.addField("MCH_CODE").
		setLabel("PCMWMAINTENANCEOBJECTLOV1OBJID: Object ID").
		setUpperCase(). 
		setMaxLength(100).
		setSize(100);

		blk.addField("MCH_NAME").
		setLabel("PCMWMAINTENANCEOBJECTLOV1OBJDESC: Description").
		setSize(150);
		//Bug 88985, End.

		blk.addField("EQUIPMENT_MAIN_POSITION").
		setLabel("PCMWMAINTENANCEOBJECTLOV1MAINPOS: Equipment Main Position").
		setSize(20);

		blk.addField("OBJ_LEVEL").
		setLabel("PCMWMAINTENANCEOBJECTLOV1OBJLEVEL: Object Level").
		setUpperCase().              
		setSize(20);

		blk.addField("LATEST_TRANSACTION").
		setLabel("PCMWMAINTENANCEOBJECTLOV1LATESTTRANSACTION: Latest Transaction").
		setUpperCase().              
		setSize(50);

		blk.addField("OPERATIONAL_STATUS").
		setLabel("PCMWMAINTENANCEOBJECTLOV1OPESTATE: Operational Status").
		setSize(50);

		blk.addField("MCH_CODE_CONT").
		setFunction("MCH_CODE||'~'||MCH_NAME||'~'||CONTRACT").
		setHidden();

//		Call 144425 Start
		blk.addField("CONNECTION_TYPE").
		setLabel("CONNECTIONTYPE: Connection Type").
		setSize(30).
		setHidden();
//		Call 144425 End

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWMAINTENANCEOBJECTLOV1OBJID: Object ID"));
		tbl.setKey("MCH_CODE_CONT");              
		defineLOV();
	}
}
