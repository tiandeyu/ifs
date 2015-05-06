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
*  File        : SimulatedPmActionLov.java 
*  Created     : NIJALK  050329  Created. (AMEC112 Job Program) 
*  Modified    :
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SimulatedPmActionLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SimulatedPmActionLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;       

	//===============================================================
	// Construction 
	//===============================================================
	public SimulatedPmActionLov(ASPManager mgr, String page_path)
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
		blk.setView("SIMULATED_PM_ACTION_LOV");

		blk.addField("SIMULATED_PM_NO").
		setLabel("PCMWSIMPMACT: PM No").
		setUpperCase().              
		setSize(10);

		blk.addField("SIMULATED_PM_REVISION").
                setLabel("PCMWSIMPMREV: Revision").
		setUpperCase().              
		setSize(6);

		blk.addField("PM_TYPE").
                setLabel("PCMWSIMTYPE: State").
		setUpperCase(). 
		setSize(30);

		blk.addField("SIMULATED_WO_SITE").
		setLabel("PCMWSIMWOSITE: WO Site").
		setSize(5);    

		blk.addField("SIMULATED_ORG_CODE").
		setLabel("PCMWSIMORGCODE: Maint.Org.").
		setSize(8);   

		blk.addField("SIMULATED_ACTION_ID").
		setLabel("PCMWSIMACTION: Action").
		setSize(10);   

		blk.addField("SIMULATED_WORK_TYPE").
		setLabel("PCMWSIMWORKTYPE: Work Type").
                setSize(10);  

		blk.addField("SIMULATED_PRIORITY").
		setLabel("PCMWSIMPRIORITY: Priority ID").
                setSize(2);   

		blk.addField("SIMULATED_OPER_STATUS").
		setLabel("PCMWSIMOPSTAT: Operational Status").
                setSize(4);   

		blk.addField("SIMULATED_INTERVAL").
		setLabel("PCMWSIMINTERVAL: Interval").
                setSize(50);   

		blk.addField("PM_INTER_UNIT").
		setLabel("PCMWSIMINTUNIT: Interval Unit").
                setSize(50);   

		blk.addField("SIMULATED_PM_TYPE").
		setLabel("PCMWSIMPMTYPE: Simulated PM Type").
                setHidden();   

		blk.addField("PM_SIMULATION_ID","Number").
		setLabel("PCMWSIMID: Simulation ID").
                setHidden();   

		blk.addField("RET_FIELD").
		setFunction("SIMULATED_PM_NO || '~' || SIMULATED_PM_REVISION").
		setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWSIMULATEDPMACT: Simulated PM Action"));
		tbl.setKey("RET_FIELD");    
		defineLOV();
	}
}
