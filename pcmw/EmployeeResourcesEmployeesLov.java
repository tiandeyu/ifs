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
*  File        : EmployeeResourcesEmployeesLov.java 
*              : 070723  AMDILK  Created.
*  Modified 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class EmployeeResourcesEmployeesLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.EmployeeResourcesEmployeesLov");

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
	public EmployeeResourcesEmployeesLov(ASPManager mgr, String page_path)
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
		blk.setView("EMPLOYEE_LOV");

                              
                f=blk.addField("COMPANY");
                f.setSize(8);
                f.setMaxLength(20);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEECOMPANY: Company");
                f.setUpperCase();

                f = blk.addField("PERSON_ID");
                f.setSize(13);
                f.setMaxLength(20);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEESIG: Signature");
                f.setUpperCase();

                f = blk.addField("EMPLOYEE_ID");
                f.setSize(14);
                f.setMaxLength(11);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEEEMPID: Employee ID");
                f.setUpperCase();

                f = blk.addField("NAME");
                f.setSize(40);
                f.setMaxLength(100);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEENAME: Name");

                f = blk.addField("VENDOR_NO");
                f.setSize(12);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEESUPP: Supplier");
                f.setUpperCase();

                f = blk.addField("ORG_CODE");
                f.setSize(12);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEEMAINTORG: Maint. Org.");
                f.setUpperCase();
                f.setMaxLength(8);


                f = blk.addField("CONTRACT");
                f.setSize(7);
                f.setLabel("PCMWEMPRESOURCESEMPLOYEEORGSITE: Maint. Org. Site");
                f.setUpperCase();
                f.setMaxLength(5);


		blk.addField("RET_FIELD").
                setFunction("PERSON_ID"+"||"+"'^'"+"||"+"EMPLOYEE_ID"+"||"+"'^'"+"||"+"COMPANY"+"||"+"'^'").
		setHidden();
                
		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("PCMWEMPRESOURCESEMPLOYEETITLE: Employee ID"));
		tbl.setKey("RET_FIELD");              
		defineLOV();
	}
}
