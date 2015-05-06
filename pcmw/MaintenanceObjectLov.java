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
*  File        : MaintenanceObjectLov.java 
*  Modified    : 2001-02-26 Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool  2001-02-25  - Created Using the ASP file MaintenanceObjectLov.asp
*                   2002-11-28 YAWILK : Changed the field CURRENT_POSITION to LATEST_TRANSACTION	
*    GACOLK         2002-12-04  - Set Max Length of MCH_CODE to 100
*    ARWILK         031222        Edge Developments - (Removed clone and doReset Methods)
*    CHANLK         100212  	  Bug 88985, increase field size of MCH_CODE, MCH_NAME.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceObjectLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObjectLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;
	private ASPField f;

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenanceObjectLov(ASPManager mgr, String page_path)
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
		blk.setView("MAINTENANCE_OBJECT");

		//Bug 88985, Start.
		f = blk.addField("MCH_CODE");
		f.setUpperCase();
		f.setLabel("PCMWMAINTENANCEOBJECTLOVMCHCODE: Object ID");
		f.setMaxLength(100);
		f.setSize(100);

		f = blk.addField("MCH_NAME");
		f.setLabel("PCMWMAINTENANCEOBJECTLOVMCHNAME: Description");
		f.setSize(150);
		//Bug 88985, End.

		f = blk.addField("CONTRACT");
		f.setLabel("PCMWMAINTENANCEOBJECTLOVCONT: Site");
		f.setSize(8);

		f = blk.addField("OBJ_LEVEL");
		f.setLabel("PCMWMAINTENANCEOBJECTLOVDESCRIP: Object Level");
		f.setSize(30);

		f = blk.addField("EQUIPMENT_MAIN_POSITION");
		f.setLabel("PCMWMAINTENANCEOBJECTLOVDELIVERYA: Equipment Main Position");
		f.setSize(23);

		f = blk.addField("LATEST_TRANSACTION");
		f.setLabel("PCMWMAINTENANCEOBJECTLOVDESCRIPTION: Latest Transaction");
		f.setSize(40);

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWMAINTENANCEOBJECTLOVMAINTOBJECT: Object ID:"));

		defineLOV();
	}
}
