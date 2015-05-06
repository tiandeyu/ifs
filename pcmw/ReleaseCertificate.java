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
*  File        : ReleaseCertificate.java 
*  VAGULK  2004-09-20  - Created.  
*  Modified    :
*  041110  NAMELK Duplicated Translation Tags Corrected. 
*  060815  AMNILK Bug 58216, Eliminated SQL Injection security vulnerability.
*  060904  AMNILK Merged Bug Id: 58216.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ReleaseCertificate extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ReleaseCertificate");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;
	private ASPQuery q;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;


	//===============================================================
	// Construction 
	//===============================================================
	public ReleaseCertificate(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();   

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
	        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
			okFind();

		adjust();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		int currrow = headset.getCurrentRowNo();
		q = trans.addQuery(headblk);

		if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO"))){
			//Bug 58216 Start
		       q.addWhereCondition("WO_NO = ?");
		       q.addParameter("WO_NO_DUMMY",mgr.getQueryStringValue("WO_NO"));
		       //Bug 58216 End
		}

		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWRELEASECERTIFICATENODATA: No data found."));
			headset.clear();
		}
		else if (headset.countRows() == 1)
		{
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}

		headset.goTo(currrow);
		mgr.createSearchURL(headblk);

	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		//====== HEAD BLOCK ======

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("FORM_TRACK_NO","Number","#").
		setMandatory().
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATEFRMTRACKNO: Form Tracking Number").
		setSize(20).
		setReadOnly();

		headblk.addField("STATUS").
		setInsertable().
		setMandatory().
		setLabel("PCMWRELEASECERTIFICATESTATUS: Status/Work").
		setSize(20).
		setReadOnly().
		setMaxLength(200);

		headblk.addField("EMPLOYEE_NAME").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATEAUTHAPPNAME: Authorized Approver Name").
		setSize(20).
		setUpperCase().
		setReadOnly().
		setMaxLength(100);

		headblk.addField("DT_CRE","Datetime").
		setLabel("PCMWRELEASECERTIFICATEDATECREATED: Date Created").
		setSize(20).
		setReadOnly();

                headblk.addField("REMARK1").
		setInsertable().
		setLabel(" ").
		setSize(30).
		setReadOnly().
		setMaxLength(80);

		headblk.addField("REMARK2").
		setInsertable().
		setLabel(" ").
		setSize(30).
		setReadOnly().
		setMaxLength(80);

		headblk.addField("REMARK3").
		setInsertable().
		setLabel(" ").
		setSize(30).
		setReadOnly().
		setMaxLength(80);

		headblk.addField("REMARK4").
		setInsertable().
		setLabel(" ").
		setSize(30).
		setReadOnly().
		setMaxLength(80);

		headblk.addField("REMARK_LONG").
		setInsertable().
		setLabel(" ").
		setSize(60).
		setHeight(4).
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("CONFORMITY_TO_DESIGN1").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATEAPPDESIGN: Approved design data and are in a condition for safe operation").
		setSize(12).
		setCheckBox("N,Y").
		setReadOnly().
		setMaxLength(1);
		
		headblk.addField("CONFORMITY_TO_DESIGN2").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATENONAPPDESIGN: Non Approved design data specified in Block 13").
		setSize(12).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);

		headblk.addField("RETURN_TO_SERVICE1").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATECFRRETSERV: 14 CFR 43.9 Return to Service").
		setSize(20).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);

		headblk.addField("RETURN_TO_SERVICE2").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATEOTHERREGBLK: Other regulation specified in Block 13").
		setSize(20).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);

		headblk.addField("CONFORMITY_TO_DESIGN3").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATEAIRWORHTINS: Airworthiness").
		setSize(20).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);

		headblk.addField("CONFORMITY_TO_DESIGN4").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATECONFONLY: Confirmity Only").
		setSize(20).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);
                
		headblk.addField("RETURN_TO_SERVICE3").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATERETSERV3: JAR 145.50 Release to Service").
		setSize(20).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);

		headblk.addField("RETURN_TO_SERVICE4").
		setInsertable().
		setLabel("PCMWRELEASECERTIFICATEOTHERREGBLK: Other regulation specified in Block 13").
		setSize(20).
		setReadOnly().
		setCheckBox("N,Y").
		setMaxLength(1);

		//Bug 58216 Start
		headblk.addField("WO_NO_DUMMY").
		setHidden().
		setFunction("''");
		//bug 58216 End

		headblk.setView("RELEASE_CERTIFICATE");
		headblk.defineCommand("RELEASE_CERTIFICATE_API"," ");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk); 

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT); 
		headlay.setDialogColumns(2);  

		headlay.defineGroup(mgr.translate("PCMWRELEASECERTIFICATEREPSTATE: Report Status"),"FORM_TRACK_NO,STATUS,EMPLOYEE_NAME,DT_CRE",true,true);
		headlay.defineGroup(mgr.translate("PCMWRELEASECERTIFICATEREPREMARKS: Remarks"),"REMARK1,REMARK2,REMARK3,REMARK4,REMARK_LONG",true,true);
		headlay.defineGroup(mgr.translate("PCMWRELEASECERTIFICATEREOVERHAULEDFAA: OVERHAULED FAA Form 8130-3 - Conformity to Design"),"CONFORMITY_TO_DESIGN1,CONFORMITY_TO_DESIGN2",true,true);
		headlay.defineGroup(mgr.translate("PCMWRELEASECERTIFICATERENOTOVERHAULED: NOT OVERHAULED FAA Form 8130-3 - Return to Service"),"RETURN_TO_SERVICE1,RETURN_TO_SERVICE2",true,true);
		headlay.defineGroup(mgr.translate("PCMWRELEASECERTIFICATEREOVERHAULEDJAA: OVERHAULED EASA Form One - Conformity to Design"),"CONFORMITY_TO_DESIGN3,CONFORMITY_TO_DESIGN4",true,true);
		headlay.defineGroup(mgr.translate("PCMWRELEASECERTIFICATERENOTOVERHAULEDJAA: NOT OVERHAULED EASA Form One - Return to Service"),"RETURN_TO_SERVICE3,RETURN_TO_SERVICE4",true,true);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWRELEASECERTIFICATENAME: Release Certificate"));
		headtbl.setWrap();

	}


	public void  adjust()
	{
	    if (headset.countRows() == 1)
		headbar.disableCommand(headbar.BACK);

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWRELEASECERTIFICATETITLE: Release Certificate";
	}

	protected String getTitle()
	{
		return "PCMWRELEASECERTIFICATETITLE: Release Certificate";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
		{
			appendToHTML(headlay.show());
		}

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}
}
