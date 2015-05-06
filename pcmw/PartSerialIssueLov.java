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
*  File        : PartSerialIssueLov.java 
*  Modified    :
*    SHFELK  2001-03-13  - Created.
*    GACOLK  2002-12-04  - Set Max Length of SERIAL_NO to 50
*    ARWILK  031222        Edge Developments - (Removed clone and doReset Methods)
*    VAGULK  040316        Call ID - 112948,Removed Obsolete field "CURRENT_POSITION" and added "LATEST_TRANSACTION".
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PartSerialIssueLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PartSerialIssueLov");

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
	public PartSerialIssueLov(ASPManager mgr, String page_path)
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
		blk.setView("PART_SERIAL_ISSUE");

		f = blk.addField("RET_FIELD");
		f.setFunction("PART_NO"+"||"+"'^'"+"||"+"SERIAL_NO"+"||"+"'^'");
		f.setHidden();
		f.setSize(30);

		f = blk.addField("PART_NO");
		f.setLabel("PCMWPARTSERIALISSUELOVPARTNO: Part No");
		f.setMaxLength(25);
		f.setUpperCase();
		f.setSize(25);

		f = blk.addField("SERIAL_NO");
		f.setLabel("PCMWPARTSERIALISSUELOVSERIALNO: Serial No");
		f.setMaxLength(50);
		f.setSize(15);

		f = blk.addField("ALTERNATE_ID");
		f.setLabel("PCMWPARTSERIALISSUELOVALTID: Alternate Id");
		f.setMaxLength(100);
		f.setSize(15);

	        f = blk.addField("LATEST_TRANSACTION");
		f.setLabel("PCMWPARTSERIALISSUELOVLATESTTRANS: Latest Transaction");
		f.setSize(30);
		f.setMaxLength(200);

		f = blk.addField("ORDER_NO");
		f.setLabel("PCMWPARTSERIALISSUELOVORDNO: Order No");
		f.setSize(25);
		f.setMaxLength(12);

		f = blk.addField("TRANSACTION_DESCRIPTION");
		f.setLabel("PCMWPARTSERIALISSUELOVTRANSDESC: Transaction Description");
		f.setSize(25);
		f.setMaxLength(200);

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle("PCMWPARTSERIALISSUELOVIBT: Part No");
		tbl.setKey("RET_FIELD");
		defineLOV();
	}
}
