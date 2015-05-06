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
*  File        : JobProgramLov.java 
*  Created     : NIJALK  050324  Created. (AMEC112 Job Program) 
*  Modified    :
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class JobProgramLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.JobProgramLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;       

	//===============================================================
	// Construction 
	//===============================================================
	public JobProgramLov(ASPManager mgr, String page_path)
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
		blk.setView("JOB_PROGRAM");

		blk.addField("JOB_PROGRAM_ID").
		setLabel("PCMWJOBPROGRAMLOVJOBPROGRAMID: Job Program ID").
		setUpperCase().              
		setSize(12);

		blk.addField("JOB_PROGRAM_REVISION").
                setLabel("PCMWJOBPROGRAMLOVJOBPROGRAMREV: Job Program Revision").
		setUpperCase().              
		setSize(6);

		blk.addField("JOB_PROGRAM_CONTRACT").
                setLabel("PCMWJOBPROGRAMLOVJOBPROGRAMCONT: Job Program Site").
		setUpperCase(). 
                setHidden().
		setSize(6);

		blk.addField("DESCRIPTION").
		setLabel("PCMWJOBPROGRAMLOVDESCR: Description").
		setSize(40);    

		blk.addField("DATE_CREATED","Date").
		setLabel("PCMWJOBPROGRAMLOVDATECREATED: Date Created").
		setSize(20);   

		blk.addField("DATE_MODIFIED","Date").
		setLabel("PCMWJOBPROGRAMLOVDATEMODIFIED: Date Modified").
		setSize(20);   

		blk.addField("OBJSTATE").
		setLabel("PCMWJOBPROGRAMLOVSTATE: State").
                setHidden();

		blk.addField("RET_FIELD").
		setFunction("JOB_PROGRAM_ID || '~' || JOB_PROGRAM_REVISION").
		setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWJOBPROGRAMLOV: Job Program"));
		tbl.setKey("RET_FIELD");    
		defineLOV();
	}
}
