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
*  File        : ConnectToolsFacilitiesLov.java 
*  Modified    :
*  CHAMLK  020404  Created.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040720  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  NAMELK  041105  Non Standard Translation Tags Corrected.
*  ASSALK  080818  Bug 75641, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ConnectToolsFacilitiesLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ConnectToolsFacilitiesLov");

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
	public ConnectToolsFacilitiesLov(ASPManager mgr, String page_path)
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
		// Bug 75641 start
		blk.setView("CONNECT_TOOLS_FACILITIES_LOV");
		// Bug 75641 end

		f = blk.addField("RET_FIELD");
		//"TOOL_FACILITY_ID"+"||"+"'^'"+"||"+"CONTRACT"+"||"+"'^'"+"||"+"ORG_CODE"+"||"+"'^'"
		//f.setFunction("TOOL_FACILITY_ID"+"||"+"'^'"+"||"+"CONTRACT"+"||"+"'^'"+"ORG_CODE"+"||"+"'^'");
		f.setFunction("TOOL_FACILITY_ID"+"||"+"'^'"+"||"+"CONTRACT"+"||"+"'^'"+"||"+"ORG_CODE"+"||"+"'^'");
		f.setHidden();
		f.setSize(30);

		f = blk.addField("TOOL_FACILITY_ID");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVTFACID: Tool/Facility ID");
		f.setMaxLength(40);
		f.setUpperCase();
		f.setSize(40);

		f = blk.addField("CONTRACT");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVCONTRACT: Site");
		f.setMaxLength(5);
		f.setUpperCase();
		f.setSize(8);

		f = blk.addField("ORG_CODE");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVORGCODE: Maintenance Organization");
		f.setMaxLength(8);
		f.setSize(8);

		f = blk.addField("TOOL_FACILITY_DESCRIPTION");
		f.setFunction("TOOL_FACILITY_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVTFDESC: Tool/Facility Description");
		f.setSize(40);
		f.setMaxLength(200);

		f = blk.addField("TOOL_FACILITY_TYPE");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVTFTYPE: Tool/Facility Type");
		f.setSize(40);
		f.setMaxLength(40);

		f = blk.addField("TYPE_DESCRIPTION");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVTYPEDESC: Type Description");
		f.setFunction("TOOL_FACILITY_TYPE_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
		f.setSize(40);
		f.setMaxLength(200);

		f = blk.addField("QTY", "Number");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVQTY: Quantity");
		f.setSize(10);

		f = blk.addField("HOUR_COST", "Money");
		f.setFunction("TOOL_FACILITY_API.Get_Hour_Cost(:TOOL_FACILITY_ID)");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVCOST: Cost");
		f.setSize(10);

		f = blk.addField("CURRENCY_CODE");
		f.setFunction("TOOL_FACILITY_API.Get_Currency_Code(:TOOL_FACILITY_ID)");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVCURR: Currency");
		f.setSize(8);
		f.setMaxLength(3);

		f = blk.addField("CATALOG_NO_CONTRACT");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVSPS: Sales Part Site");
		f.setSize(8);
		f.setMaxLength(5);

		f = blk.addField("CATALOG_NO");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVSP: Sales Part");
		f.setSize(20);
		f.setMaxLength(25);

		f = blk.addField("CATALOG_NO_DESC");
		f.setFunction("CONNECT_TOOLS_FACILITIES_API.Get_Sales_Part_Desc(:CATALOG_NO_CONTRACT,:CATALOG_NO)");  
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVSPD: Sales Part Description");
		f.setSize(40);
		f.setMaxLength(35);

		f = blk.addField("CATALOG_PRICE","Money");
		f.setFunction("CONNECT_TOOLS_FACILITIES_API.Get_Sales_Price(:CATALOG_NO_CONTRACT,:CATALOG_NO)");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVSPRICE: Sales Price");
		f.setSize(10);

		f = blk.addField("CATALOG_PRICE_CURR");
		f.setFunction("Company_Finance_API.Get_Currency_Code(Site_API.Get_Company(:CATALOG_NO_CONTRACT))");
		f.setLabel("PCMWCONNETTOOLSFACILITIESLOVSC: Sales Currency");
		f.setSize(8);
		f.setMaxLength(5);

		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle("PCMWCONNETTOOLSFACILITIESLOVCONTF: Connect Tools Facilities");
		tbl.setKey("RET_FIELD");
		defineLOV();
	}
}
