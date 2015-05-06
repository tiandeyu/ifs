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
*  File        : ActiveRoundStatusLov.java 
*  Created     : NIJALK	  100717
*  Modified    : 
* -----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveRoundStatusLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveRoundStatusLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPCommandBar bar;
	private ASPBlockLayout lay;
	private ASPRowSet set;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private String statusBuff;
	private ASPBuffer data;
	private int i;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveRoundStatusLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		search();
	}

	public void search()
	{
		ASPManager mgr = getASPManager();

		trans.clear(); 
		cmd = trans.addCustomCommand("ENUMSTATE","Active_Round_API.Enumerate_States_1__");
		cmd.addParameter("CLIENTVALUES");

		trans = mgr.perform(trans);
		statusBuff = trans.getValue("ENUMSTATE/DATA/CLIENTVALUES");   

		String splitted[] = split(statusBuff, (char)31 + "");
		int len = splitted.length;
		int newLen =len - 1;
		set.clear();
		data = mgr.newASPBuffer();

		for (int i = 0;i < newLen; i++)
		{
			data.setValue("STATUS", splitted[i]);
			set.addRow(data);
		}
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		blk = mgr.newASPBlock("HEAD");
		blk.setView("DUAL");

		blk.addField("CLIENTVALUES").
		setHidden().
		setFunction("''"); 

		blk.addField("STATUS","String").
		setSize(20).
		setLabel("PCMWACTIVEROUNDSTATUSLOVSTATUS: Status");

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		set = blk.getASPRowSet();
		bar = mgr.newASPCommandBar(blk);

		bar.disableMinimize();
		tbl.disableEditProperties();
		lay.setDefaultLayoutMode(lay.CUSTOM_LAYOUT);
	}

	protected String getDescription()
	{
		return "PCMWACTIVEROUNDSTATUSLOVTITLE: List of Status";
	}

	protected String getTitle()
	{
		return "PCMWACTIVEROUNDSTATUSLOVTITLE: List of Status";
	}

//===============================================================
//  HTML
//===============================================================

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(bar.showBar());
		appendToHTML(tbl.populateLov());
	}
}

