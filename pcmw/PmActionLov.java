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
*  File        : PmActionLov.java 
*  Created By  : SAPRLK 2004-05-18  For IID AMEC109A, Multiple Standard Jobs on PMs.  
*  Modified    :
*  ARWILK  040722  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  NAMELK  041108  Non Standard and Duplicated Translation Tags Changed.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PmActionLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmActionLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;       

	//===============================================================
	// Construction 
	//===============================================================
	public PmActionLov(ASPManager mgr, String page_path)
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
		blk.setView("PM_ACTION");

		blk.addField("PM_NO").
		setLabel("PMACTIONLOVPMNO: Pm No").
		setUpperCase().              
		setSize(25);

		blk.addField("CONTRACT").
		setLabel("PMACTIONLOVCONTRACT: Site").
		setUpperCase().              
		setSize(20);

		blk.addField("PM_REVISION").
		setLabel("PMACTIONLOVPMREV: Pm Revision").
		setUpperCase().              
		setSize(20);

		blk.addField("PM_TYPE").
		setLabel("PMACTIONLOVPMTYPE: Pm Type").
		setSize(20);    

		blk.addField("MCH_CODE_CONTRACT").
		setHidden();

		blk.addField("MCH_CODE").
		setLabel("PMACTIONLOVMCHCODE: Object").
		setSize(6);

		blk.addField("MCH_NAME").
		setLabel("PMACTIONMCHNAME: Description").
		setFunction("Equipment_Object_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)").
		setSize(20);

		blk.addField("ACTION_CODE_ID").
		setLabel("PMACTIONLOVACTIONCODEID: Action").
		setSize(50);   

		blk.addField("ACTION_DESCR").
		setLabel("PMACTIONLOVACTDESC: Directive").
		setFunction("Maintenance_Action_API.Get_Description(:ACTION_CODE_ID)").    
		setSize(50);    

		blk.addField("ORG_CODE").
		setSize(16).
		setHilite().
		setLabel("PMACTIONLOVORGCODE: Maintenance Organization").
		setUpperCase().
		setMaxLength(8);      

		blk.addField("RET_FIELD").
		setFunction("PM_NO||'~'||PM_REVISION").
		setHidden();


		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PMACTIONPMNO: Pm No"));
		tbl.setKey("RET_FIELD");              
		defineLOV();
	}
}
