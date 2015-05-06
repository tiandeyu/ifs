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
*  File        : ReleaseCertAuthIdLov.java 
*  Modified    :
*  VAGULK      :2004-09-06  - Created. 
*  BUNILK      :2005-07-11  - Added new RETURN_FIELD and set it as key field. 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ReleaseCertAuthIdLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ReleaseCertAuthIdLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public ReleaseCertAuthIdLov(ASPManager mgr, String page_path)
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
		blk.setView("RELEASE_CERTIFICATE_AUTH_LOV");

		blk.addField("AUTHORIZATION_NO").
		setLabel("PCMWRELEASECERTAUTHIDLOVAUTHORIZATIONNO: Authorization NO").
		setUpperCase().
		setMaxLength(40).              
		setSize(20);

		blk.addField("COMPANY").
		setLabel("PCMWRELEASECERTAUTHIDLOVCOMPANY: Company").
		setUpperCase(). 
		setMaxLength(6).
		setSize(20);

		blk.addField("EMPLOYEE_ID").
		setLabel("PCMWRELEASECERTAUTHIDLOVEMPLOYEEID: Employee ID").
		setSize(20);

		blk.addField("EMPLOYEE_NAME").
		setLabel("PCMWRELEASECERTAUTHIDLOVEMPLOYEENAME: Employee Name").
		setSize(20);

                blk.addField("RETURN_FIELD").
                setFunction("AUTHORIZATION_NO"+"||"+"'~'"+"||"+"EMPLOYEE_NAME"+"||"+"'~'"+"||"+"EMPLOYEE_ID").
                setHidden();
                
		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWRELEASECERTAUTHIDLOVAUTHNO: Authorization NO"));
		tbl.setKey("RETURN_FIELD");              
		defineLOV();
	}
}
