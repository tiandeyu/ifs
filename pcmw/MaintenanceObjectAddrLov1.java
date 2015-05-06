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
*  File        : MaintenanceObjectAddrLov1.java 
*  Modified    :
*  INROLK      :2002-11-22  - Created.  Lov for the Objetc ID in Service Reequest.
*  GACOLK      :2002-12-04  - Set Max Length of MCH_CODE to 100
*  ARWILK      031222   Edge Developments - (Removed clone and doReset Methods)
*  NAMELK      041105   Duplicated and Non Standard Translation Tags Corrected. 
*  ILSOLK      060817  	Set the MaxLength of Address1 as 100.
*  CHANLK      070516 	Call 144425 Added hidden field Connection Type.
*  ILSOLK      070730 	Eliminated LocalizationErrors. 
*  CHANLK      100212  	Bug 88985, increase field size of MCH_CODE, MCH_NAME.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceObjectAddrLov1 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObjectAddrLov1");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenanceObjectAddrLov1(ASPManager mgr, String page_path)
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
		blk.setView("MAINTENANCE_OBJECT_ADDR_LOV1");

 		//Bug 88985,Start 
		blk.addField("MCH_CODE").
		setLabel("PCMWMAINTENANCEOBJECTADDRLOV1OBJID: Object ID").
		setUpperCase(). 
		setMaxLength(100).
		setSize(100);

		blk.addField("MCH_NAME").
		setLabel("PCMWMAINTENANCEOBJECTADDRLOV1OBJDESC: Object Description").
		setSize(150);
 		//Bug 88985,End 

		blk.addField("MCH_CODE_CONTRACT").
		setLabel("PCMWMAINTENANCEOBJECTADDRLOV1CONTRACT: Site").
		setUpperCase().              
		setSize(20);

		blk.addField("OBJ_LEVEL").
		setLabel("PCMWMAINTENANCEOBJECTADDRLOV1OBJLEVEL: Object Level").
		setUpperCase().              
		setSize(20);

		blk.addField("LATEST_TRANSACTION").
		setLabel("LATESTTRANSACTION: Latest Transaction").
		setUpperCase().              
		setSize(50);

		blk.addField("DEF_ADDRESS").
		setLabel("DEFADDRESS: Default Address").
		setSize(25);

		blk.addField("ADDRESS1").
		setLabel("ADDRESS1: Address1").
		setSize(25).
                setMaxLength(100);

		blk.addField("ADDRESS2").
		setLabel("ADDRESS2: Address2").
		setSize(30);

		blk.addField("ADDRESS3").
		setLabel("ADDRESS3: Address3").
		setSize(30);

		blk.addField("ADDRESS4").
		setLabel("ADDRESS43: Address4").
		setSize(30);

		blk.addField("ADDRESS5").
		setLabel("ADDRESS5: Address5").
		setSize(30);

		blk.addField("ADDRESS6").
		setLabel("ADDRESS6: Address6").
		setSize(30);

		blk.addField("ADDRESS_ID").
		setLabel("ADDRESSID: Address ID").
		setSize(30);

//		Call 144425 Start
		blk.addField("CONNECTION_TYPE").
		setLabel("CONNECTIONTYPE: Connection Type").
		setSize(30).
		setHidden();
//		Call 144425 End

		blk.addField("MCH_CONTRACT").
		setFunction("MCH_CODE||'~'||MCH_NAME||'~'||MCH_CODE_CONTRACT")
		.setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWMAINTENANCEOBJECTADDRLOV1OBJID: Object ID"));
		tbl.setKey("MCH_CONTRACT");              
		defineLOV();
	}
}
