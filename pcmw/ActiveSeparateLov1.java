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
*  File        : ActiveSeparateLov1.java 
*  Created     : INROLK  010323
*  Modified    : 
*  SHAFLK  020801  Bug 31861,Changed HTML code.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041110  Replaced getContents with printContents.
*  Chanlk  050119  Change the metod call to ACTIVE_SEPARATE_API.Enumerate
*  ILSOLK  070713  Eliminated XSS.
*  SHAFLK  080811  Bug 76086,Modified search().
* -----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparateLov1 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateLov1");

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
	public ActiveSeparateLov1(ASPManager mgr, String page_path)
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
		cmd = trans.addCustomCommand("ENUMSTATE","ACTIVE_SEPARATE_API.Enumerate");
		cmd.addParameter("CLIENTVALUES");

		trans = mgr.perform(trans);
		statusBuff = trans.getValue("ENUMSTATE/DATA/CLIENTVALUES");   

		String splitted[] = split(statusBuff, (char)31 + "");
		int len = splitted.length;
		int newLen =len - 1;
		set.clear();
		data = mgr.newASPBuffer();

		for (int i=0;i<newLen;i++)
		{       
                        //Bug 76086, start
                        if (i!= 10)
                        {
                            data.setValue("STATUS",splitted[i]);
                            set.addRow(data);
                        }
                        //Bug 76086, end
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
		setLabel("PCMWACTIVESEPARATELOV1STATUS: Status");

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
		return "PCMWACTIVESEPARATELOV1TITLE: List of Status";
	}

	protected String getTitle()
	{
		return "PCMWACTIVESEPARATELOV1TITLE: List of Status";
	}

//===============================================================
//  HTML
//===============================================================

	//Bug 31861, start
	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(bar.showBar());
		appendToHTML(tbl.populateLov()); // XSS_Safe ILSOLK 20070713
	}
	//Bug 31861, end
}

