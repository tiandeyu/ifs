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
*  File        : ActiveSeparateWizB2ELov.java 
*  Modified    : 2001-03-05 Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool  2001-02-28  - Created Using the ASP file ActiveSeparateWizB2ELov.asp
*    ARWILK         031222        Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparateWizB2ELov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateWizB2ELov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparateWizB2ELov(ASPManager mgr, String page_path)
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
		blk.setView("EQUIPMENT_OBJECT_PARTY");

		blk.addField("MCH_CODE").
		setHidden();

		blk.addField("IDENTITY").
		setUpperCase().
		setLabel("PCMWACTIVESEPARATEWIZB2ELOVIDENTITY: Identity").
		setSize(20);

		blk.addField("PARTY_TYPE").
		setLabel("PCMWACTIVESEPARATEWIZB2ELOVPARTY_TYPE: Party Type").
		setSize(20);

		blk.addField("CONTRACT").
		setHidden();

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWACTIVESEPARATEWIZB2ELOVIBT:  List of Customer Id: "));
		tbl.setKey("IDENTITY"); 
		defineLOV();    
	}  
}
