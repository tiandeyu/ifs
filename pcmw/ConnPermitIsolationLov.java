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
*  File        : ConnPermitIsolationLov.java   
*  NEKOLK      : 2005-06-09  - Created.  
*  Modified:
*  THWILK  060126  Corrected localization errors.
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ConnPermitIsolationLov extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ConnPermitIsolationLov");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay; 

	//===============================================================
	// Construction 
	//===============================================================
	public ConnPermitIsolationLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		super.doReset();               
	}

	public ASPPoolElement clone(Object obj) throws FndException
	{
		ConnPermitIsolationLov page = (ConnPermitIsolationLov)(super.clone(obj));

		page.blk = page.getASPBlock(blk.getName());
		page.tbl = page.getASPTable(tbl.getName());
		page.lay = page.blk.getASPBlockLayout(); 

		return page;
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();  
	}        

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		blk = mgr.newASPBlock("HEAD");

		blk.setView("CONN_PERMIT_ISOLATION");

		blk.addField("ISOLATION_TYPE").
		setLabel("PCMWCONNPERMITISOLATIONLOVISOTYPE: Isolation Type").
		setUpperCase(). 
		setSize(25);

		blk.addField("PERMIT_TYPE_ID").
		setLabel("PERMITTYPEID: Permit Type").
		setSize(25);

		//blk.addField("NO_OF_CONN","Number","#").
		//setLabel("NOOFCONN: No Of Conn").
		//setSize(20);

                tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWCONNPERMITISOLATIONLOVISOTYPE: Isolation Type"));
		tbl.setKey("ISOLATION_TYPE");              
		defineLOV();
	}

}
