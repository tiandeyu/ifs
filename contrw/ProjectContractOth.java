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
 * File                          :
 * Description                   :
 * Notes                         :
 * Other Programs Called :
 * ----------------------------------------------------------------------------
 * Modified    : Automatically generated by IFS/Design
 * ----------------------------------------------------------------------------
 */

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.contrw;

//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ProjectContractOth extends ProjectContractBase {

	// -----------------------------------------------------------------------------
	// ---------- Static constants ------------------------------------------------
	// -----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.contrw.ProjectContractOth");

	// -----------------------------------------------------------------------------
	// ---------- Header Instances created on page creation --------
	// -----------------------------------------------------------------------------

	
	// -----------------------------------------------------------------------------
	// ---------- Item Instances created on page creation --------
	// -----------------------------------------------------------------------------

	

	// -----------------------------------------------------------------------------
	// ------------------------ Construction ---------------------------
	// -----------------------------------------------------------------------------

	public ProjectContractOth(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}


	// -----------------------------------------------------------------------------
	// ------------------------ Command Bar functions ---------------------------
	// -----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		mgr.createSearchURL(headblk);
		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.addWhereCondition("SCHEDULE = 'FALSE'");
		q.addWhereCondition("Project_Contract_API.Get_Is_Mat(PROJ_NO, CONTRACT_ID) = 'FALSE'");
		q.setOrderByClause("CONTRACT_ID");
		mgr.querySubmit(trans, headblk);
		if (headset.countRows() == 0) {
			mgr.showAlert("PROJECTCONTRACTNODATA: No data found.");
			headset.clear();
		}
		eval(project_contract_item_set.syncItemSets());
		okFindITEM1();
	}

	public void countFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("SCHEDULE = 'FALSE'");
		q.addWhereCondition("Project_Contract_API.Get_Is_Mat(PROJ_NO, CONTRACT_ID) = 'FALSE'");
		mgr.submit(trans);  
		headlay.setCountValue(toInt(headset.getValue("N")));
		headset.clear();
	}

	public void newRow()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPBuffer data;
		ASPCommand cmd;

		cmd = trans.addEmptyCommand("HEAD", "PROJECT_CONTRACT_API.New__", headblk);
		cmd.setOption("ACTION", "PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		
		data.setFieldItem("SCHEDULE", "FALSE");
		
		headset.addRow(data);
	}

	// -----------------------------------------------------------------------------
	// ---------------------- Item block cmd bar functions -------------------------
	// -----------------------------------------------------------------------------
   public void adjust()
   {
      // fill function body
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("MAT_NO").unsetHidden();
      mgr.getASPField("MAT_NAME").unsetHidden();
      mgr.getASPField("ITEM_NOTE").setSize(140);  
   }  

	// -----------------------------------------------------------------------------
	// ------------------------ Predefines Head ---------------------------
	// -----------------------------------------------------------------------------


	// -----------------------------------------------------------------------------
	// ------------------------ Presentation functions ---------------------------
	// -----------------------------------------------------------------------------

	protected String getDescription() {
		return "PROJECTCONTRACTOTHDESC: Other Project Contract";
	}

	protected String getTitle() {
		return getDescription();
	}
}
