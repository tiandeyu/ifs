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
*  File        : PscContrProductLov.java 
*  Created     : BUNILK    2007-03-30
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PscContrProductLov extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PscContrProductLov");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;
	private ASPRowSet set;

	//===============================================================
	// Construction 
	//===============================================================
	public PscContrProductLov(ASPManager mgr, String page_path)
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
		blk.setView("PSC_CONTR_PRODUCT_LOV");

                blk.addField("CONTRACT_ID").
		setLabel("PCMWPSCCONTRPRODUCTLOVCONTRACT_ID: Contract ID");
                
		blk.addField("LINE_NO").
		setLabel("PCMWPSCCONTRPRODUCTLOVLINENO: Line No");

                blk.addField("DESCRIPTION").
		setLabel("PCMWPSCCONTRPRODUCTLOVDESCRIPTION: Description");

                blk.addField("DATE_FROM").
		setLabel("PCMWPSCCONTRPRODUCTLOVDATEFROM: Date From");
                
		blk.addField("EXPIRY_DATE").
		setLabel("PCMWPSCCONTRPRODUCTLOVEXPIRYDATE: Expiry Date");

		blk.addField("SLA_ID").
		setLabel("PCMWPSCCONTRPRODUCTLOVSLAID: Sla ID");

		blk.addField("CONTRACT").
		setLabel("PCMWPSCCONTRPRODUCTLOVCONTRACT: Maint. Org. Site");

                blk.addField("MAINT_ORG").
		setLabel("PCMWPSCCONTRPRODUCTLOVMAINTORG: Maint Org");

                blk.addField("MCH_CONTRACT").
		setLabel("PCMWPSCCONTRPRODUCTLOVMCHCONTRACT: Object Site");

                blk.addField("MCH_CODE").
		setLabel("PCMWPSCCONTRPRODUCTLOVMCHCODE: Object ID");

                blk.addField("WORK_TYPE_ID").
		setLabel("PCMWPSCCONTRPRODUCTLOVWORKTYPEID: Work Type");

                blk.addField("FIXED_CATALOG_NO").
		setLabel("PCMWPSCCONTRPRODUCTLOVFIXEDCATALOGNO: Fixed Catalog No");

                blk.addField("SLA_CALENDAR_ID").
		setLabel("PCMWPSCCONTRPRODUCTLOVSLACALENDARID: Sla Calendar ID");

                blk.addField("CUSTOMER_NO").
		setLabel("PCMWPSCCONTRPRODUCTLOVCUSTOMERNO: Customer No");

                blk.addField("RET_FIELD").
                setFunction("CONTRACT_ID"+"||"+"'^'"+"||"+"LINE_NO").
                setHidden().
                setSize(30);
                
                tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		set = blk.getASPRowSet();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

		tbl.setTitle(mgr.translate("PCMWPSCCONTRPRODUCTLOVTITILE: Service Contract Lines"));
		tbl.setKey("RET_FIELD");       

		defineLOV();
	}
}
