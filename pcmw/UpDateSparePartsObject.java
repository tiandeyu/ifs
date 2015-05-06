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
*  File        : UpDateSparePartsObject.java 
*  Created     : AMDILK  042707  Created ( Call Id 142273 )
*  Modifed     :
*  053107   AMDILK   Call Id 145443: Modified printContents(), refreshed the calling form 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class UpDateSparePartsObject extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.UpDateSparePartsObject");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	
        private ASPHTMLFormatter fmt;    
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	
        private ASPTransactionBuffer trans;
	private String cancelFlag;
	private ASPCommand cmd;
	private int rows;
	private ASPQuery q;
	
        String mch_code_contract;
	String mch_code;
	String part_no;
	String part_desc;
	String spare_contract;

	//===============================================================
	// Construction 
	//===============================================================

	public UpDateSparePartsObject(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
	        ASPManager mgr = getASPManager();

		fmt   = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();
		
		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
		{
		        
			part_no           = mgr.getQueryStringValue("PART_NO");
			part_desc         = mgr.getQueryStringValue("PART_DESC");
			mch_code_contract = mgr.getQueryStringValue("MCH_CODE_CONTRACT");
			spare_contract    = mgr.getQueryStringValue("SPARE_CONTRACT");
			mch_code          = mgr.getQueryStringValue("MCH_CODE");
			
			okFind();

			ASPBuffer temp = headset.getRow();
			
			temp.setValue("PART_NO",part_no);
                        temp.setValue("PART_DESC",part_desc);
			temp.setValue("MCH_CODE_CONTRACT",mch_code_contract);
			temp.setValue("SPARE_CONTRACT",spare_contract);
			temp.setValue("MCH_CODE",mch_code);
			temp.setValue("MCH_CODE_CONTRACT",mch_code_contract);
			
			headset.addRow(temp);
		}
		else if (mgr.buttonPressed("FINISH"))
			submit();
		else if (mgr.buttonPressed("CANCEL"))
			cancelFlag = "TRUE";

		adjust();
                

	}


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			headset.clear();
		}
	}


	public void submit()
	{
		ASPManager mgr = getASPManager();

		headset.changeRows();
		trans.clear();

		String note = null ;

		if ( mgr.isEmpty(mgr.readValue("QTY")) ) {
		   mgr.showAlert(mgr.translate("PCMUPDATESPRPARTNOQTY: Please enter the a value for quantity."));
		}
		else if ( mgr.readNumberValue("QTY") < 0 ) {
		   mgr.showAlert(mgr.translate("PCMUPDATESPRPARTINVALIDQTY: Quantity cannot be less than 0."));
		}
		else
		{
                   cmd = trans.addCustomCommand("UPDATEINV", "EQUIPMENT_OBJECT_SPARE_API.Create_Spare_Part");
		   cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
		   cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
		   cmd.addParameter("SPARE_CONTRACT",headset.getRow().getValue("SPARE_CONTRACT"));
		   cmd.addParameter("QTY", mgr.readValue("QTY"));
		   cmd.addParameter("NOTE", note);
		   cmd.addParameter("MCH_PART", mgr.readValue("MCH_PART"));                                     
		   cmd.addParameter("MCH_CODE_CONTRACT", headset.getRow().getValue("SPARE_CONTRACT"));        
    
		   trans = mgr.perform(trans);
                   cancelFlag = "TRUE";   
		}

	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("MCH_CODE");
		f.setSize(100);
                f.setHidden();
		f.setFunction("''");

		
		f = headblk.addField("PART_NO","String");
		f.setLabel("PCMUPDATESPPARTPARTNOLBL: Part No");
		f.setSize(25);
		f.setReadOnly();
		f.setFunction("''");

		f = headblk.addField("MCH_CODE_CONTRACT");
		f.setHidden();
		f.setFunction("''");
	       
		f = headblk.addField("PART_DESC", "String");
		f.setSize(4000);
		f.setLabel("PCMUPDATESPPARTPARTDESCLBL: Part Description");
		f.setReadOnly();
		f.setFunction("''");

		f = headblk.addField("SPARE_CONTRACT", "String");
		f.setSize(5);
		f.setLabel("PCMUPDATESPPARTSPARECONTRLBL: Site");
		f.setReadOnly();
		f.setFunction("''");

		f = headblk.addField("QTY","Number");
		f.setLabel("PCMUPDATESPPARTQTYLBL: Quantity");
		f.setFunction("''");
		
                f = headblk.addField("MCH_PART","String");
		f.setLabel("PCMUPDATESPPARTMCHPARTLBL: Object Part");
		f.setMaxLength(4);
		f.setFunction("''");

		f = headblk.addField("NOTE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("SPARE_EXIST");
		f.setHidden();
		f.setFunction("''");

		
		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.VIEWDETAILS);
                headbar.disableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.SAVENEW);
		headbar.disableCommand(headbar.CANCELNEW);
                headbar.disableCommand(headbar.FORWARD);

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
		headlay.setDialogColumns(2);
		headtbl.disableRowStatus();

                enableConvertGettoPost();
	}

	public void adjust()
	{

		ASPManager mgr = getASPManager();

	}

//===============================================================
//  HTML
//===============================================================

	protected String getDescription()
	{
		return "PCMWUPDATESPRPRTSDESC: Update Object Spare Parts";
	}

	protected String getTitle()
	{
		return "PCMWUPDATESPRPRTSTITLE: Update Object Spare Parts";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("  <table  border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>\n");
		appendToHTML("   <tr>\n");
		 appendToHTML("      <td nowrap height=\"26\" align=\"right\" width = '8%' >");
		appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWAUTHORIZECODINGDLGSMFINISH1: Ok"),"submit"));
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("      <td nowrap height=\"26\" align=\"left\" width = '8%' >");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWAUTHORIZECODINGDLGSMCANCEL: Cancel"),"submit"));
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("   </tr>\n");
		appendToHTML("  </table>\n");
                

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(cancelFlag);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("window.opener.commandSet('HEAD.refreshMaterialTab','');\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

                
	}
}
