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
*  File        : CalendarPlanOvwRedirect.java 
*  Modified    :
*    SHFELK  2001-04-10  - Created.
*    ARWILK  031222        Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CalendarPlanOvwRedirect extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CalendarPlanOvwRedirect");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock itemblk;
	private ASPField f;
	private ASPRowSet itemset;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String sMch_code;
	private String sContract;
	private ASPCommand cmd;
	private String fieldVal;
	private ASPBuffer buffer;
	private ASPQuery q;
	private ASPBuffer row; 

	//===============================================================
	// Construction 
	//===============================================================
	public CalendarPlanOvwRedirect(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		trans = mgr.newASPTransactionBuffer();

		if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
		{
			sMch_code = mgr.readValue("MCH_CODE","");
			sContract = mgr.readValue("CONTRACT","");
			trans.clear();

			cmd = trans.addCustomFunction("VALKEE","Equipment_All_Object_API.Get_Obj_Level","OBJ_LEVEL_FUNC");
			cmd.addParameter("CONTRACT",sContract);
			cmd.addParameter("MCH_CODE",sMch_code);

			trans = mgr.perform(trans);

			fieldVal = trans.getValue("VALKEE/DATA/OBJ_LEVEL_FUNC");

			trans.clear();     


			buffer = mgr.newASPBuffer();
			row = buffer.addBuffer("0");
			row.addItem("MCH_CODE",sMch_code);
			row.addItem("CONTRACT",sContract );

			if (!mgr.isEmpty(fieldVal))
			{
				mgr.transferDataTo("../equipw/EquipmentFunctional.page",buffer); 
			}
			else
			{
				mgr.transferDataTo("../equipw/EquipmentSerial.page",buffer);    
			}
		}
		mgr.endResponse(); 
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("MCH_CODE");
		f.setHidden();
		f.setFunction("''");

		f = itemblk.addField("CONTRACT");
		f.setHidden(); 
		f.setFunction("''");  

		f = itemblk.addField("OBJ_LEVEL_FUNC");
		f.setHidden();
		f.setFunction("''");

		itemblk.setView("DUAL");
		itemset = itemblk.getASPRowSet();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCALENDERREIRECT: Redirect Page";
	}

	protected String getTitle()
	{
		return "PCMWCALENDERREIRECT: Redirect Page";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
	}

}
