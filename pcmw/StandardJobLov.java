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
*  File        : StandardJobLov.java 
*  Created     : ARWILK  2004-06-03  Created for IID AMEC109A - "Multiple STD Jobs on PM and WO". 
*  Modified    :
*  ARWILK  041022  View was changed from STANDARD_JOB to STANDARD_JOB_LOV. (Spec AMEC111 - Standard Jobs as PM Templates)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class StandardJobLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.StandardJobLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;       

	//===============================================================
	// Construction 
	//===============================================================
	public StandardJobLov(ASPManager mgr, String page_path)
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
		blk.setView("STANDARD_JOB_LOV");

		blk.addField("STD_JOB_ID").
		setLabel("PCMWSTANDARDJOBLOVSTDJOBID: Standard Job ID").
		setUpperCase().              
		setSize(12);

		blk.addField("CONTRACT").
		setLabel("PCMWSTANDARDJOBLOVCONTRACT: Site").
		setUpperCase().              
		setSize(5);

		blk.addField("STD_JOB_REVISION").
		setLabel("PCMWSTANDARDJOBLOVSTDJOBREVISION: Standard Job Revision").
		setUpperCase().              
		setSize(6);

		blk.addField("DEFINITION").
		setLabel("PCMWSTANDARDJOBLOVDEFINITION: Definition").
		setSize(40);    

		blk.addField("STANDARD_JOB_TYPE").
		setLabel("PCMWSTANDARDJOBLOVSTANDARDJOBTYPE: Standard Job Type").
		setSize(20);   

		blk.addField("STANDARD_JOB_TYPE_DB").
		setHidden();  

		blk.addField("STATE").
		setLabel("PCMWSTANDARDJOBLOVSTATE: Status").
		setSize(25);      

		blk.addField("STD_TEXT_ONLY").
		setSize(20).
		setLabel("PCMWSTANDARDJOBLOVSTDTEXTONLY: Used as Standard Text Only").
		setCheckBox("FALSE,TRUE");

		blk.addField("RET_FIELD").
		setFunction("STD_JOB_ID || '~' || CONTRACT || '~' ||STD_JOB_REVISION").
		setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWSTANDARDJOBLOV: Standard Job"));
		tbl.setKey("RET_FIELD");    
		defineLOV();
	}
}
