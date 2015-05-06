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
*  File        : RoundStandardJobLov.java 
*  Created By  : SAPRLK 2004-05-18  For IID AMEC109A, Multiple Standard Jobs on PMs.  
*  Modified    :
*  ILSOLK 070731 Eiliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class RoundStandardJobLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.RoundStandardJobLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;      
        private ASPRowSet hset;

	//===============================================================
	// Construction 
	//===============================================================
	public RoundStandardJobLov(ASPManager mgr, String page_path)
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
		blk.setView("ROUND_STANDARD_JOB_LOV");

                blk.addField("STD_JOB_ID").
		setLabel("PCMWROUNDSTANDARDJOBLOVSTDJOBID: Standard Job ID").
		setUpperCase().              
		setSize(25);
                
		blk.addField("CONTRACT").
		setLabel("PCMWROUNDSTANDERDJOBLOVCONTRACT: Site").
		setUpperCase().              
		setSize(20);

                blk.addField("STD_JOB_REVISION").
		setLabel("STDJOBREVISION: Standard Job Revision").
		setUpperCase().              
		setSize(20);

                blk.addField("DEFINITION").
		setLabel("DEFINITION: Definition").
                setSize(20);    

		blk.addField("CREATED_BY").
		setLabel("CREATEDBY: Created By").
		setSize(20);

		blk.addField("LAST_CHANGED").
		setLabel("LASTCHANGED: Last Changed").
		setSize(6);

		blk.addField("WORK_DESCRIPTION").
		setLabel("WORKDESCRIPTION: Work Description").
                setSize(20);

		blk.addField("CREATION_DATE","Date").
		setLabel("CREATIONDATE: Creation Date").
                setSize(50);

		blk.addField("CHANGED_BY").
		setLabel("CHANGEDBY: Changed By").
		setUpperCase().              
		setSize(50);

                blk.addField("STANDARD_JOB_TYPE").
		setLabel("STANDARDJOB_TYPE: Standard Job Type").
                setSize(50);   

                blk.addField("STATE").
		setLabel("STATUS: Status").
                setSize(50);      

		blk.addField("STD_TEXT_ONLY").
		setSize(33).
		setLabel("STDTEXTONLY: Used as Standard Text Only").
                setFunction("STANDARD_JOB_API.Get_Std_Text_Only(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)").
                setCheckBox("FALSE,TRUE").
		setReadOnly().
		setDefaultNotVisible().
		setHidden();     

		blk.addField("RET_FIELD").
		setFunction("STD_JOB_ID||'~'||CONTRACT||'~'||STD_JOB_REVISION").
	        setHidden();

                hset = blk.getASPRowSet();

                tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWROUNDSTANDARDJOBLOVSTDJOBID: Standard Job ID"));
		tbl.setKey("RET_FIELD");              
		defineLOV();
	}
}
