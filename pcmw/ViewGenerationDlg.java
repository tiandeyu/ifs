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
*  File        : ViewGenerationDlg.java 
*  Modified    :
*  SHCHLK       2001-10-08  - Created.
*  ARWILK       031222        Edge Developments - (Removed clone and doReset Methods)
*  ThWilk      :2004-06-01  - Added PM_REVIISON and and changed method calls under IID AMEC109A, Multiple Standard Jobs on PMs.
*  AMNILK      :Call Id: 127774.Modified the field formats of GEN_ID and PM_NO.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ViewGenerationDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ViewGenerationDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;
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
	private boolean multirow;
	private ASPQuery q;

	private String mWo_no;
	private String mPm_no;
	private String mPm_rev;
	private String mchCode;
	private String mchName;
	private String mSite;
	private String mPlDate;
	private String mPlWeek; 
	private String mGenId;
	private String mGenType;
	private String mRepPmNo;
	private String mRepPmRev;
	private String mRepSNo;
	private String mEvent;
	private String mEvSeq;
	private String mTstP;
	private String mParam;
	private String mVal;
	//===============================================================
	// Construction 
	//===============================================================
	public ViewGenerationDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		// multirow = ctx.readFlag("MULTIROW",true);
		// creates headblk,headset,headbar, headtbl, itemblk,itemset,itembar, tbl
		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
		{
			ASPBuffer buff= mgr.getTransferedData();
			ASPBuffer row = buff.getBufferAt(0);
			mWo_no = row.getValue("WO_NO");
			mPm_no = row.getValue("PM_NO");
			mPm_rev = row.getValue("PM_REVISION");
			mchCode = row.getValue("MCHCODE"); 
			mchName = row.getValue("DESCRIPTION");
			mSite = row.getValue("SITE"); 
			mPlDate = row.getValue("PLANNED_DATE");
			mPlWeek = row.getValue("PLANNED_WEEK"); 
			mGenId = row.getValue("GEN_ID");
			mGenType = row.getValue("GENTYPE");
			mRepPmNo = row.getValue("REPLACED_BY_PM_NO");
			mRepPmRev = row.getValue("REPLACED_BY_PM_REVISION");
			mRepSNo = row.getValue("REPLACED_BY_SEQ_NO");
			mEvent = row.getValue("EVENT");
			mEvSeq = row.getValue("EVENT_SEQ");
			mTstP = row.getValue("TESTPOINT");
			mParam = row.getValue("PARAMETER");
			mVal = row.getValue("VALUE");

			okFind();
			row = headset.getRow();

			row.setValue("WO_NO",mWo_no);
			row.setValue("PM_NO",mPm_no);
			row.setValue("PM_REVISION",mPm_rev);
			row.setValue("MCHCODE",mchCode);
			row.setValue("DESCRIPTION",mchName);
			row.setValue("SITE",mSite);
			row.setValue("PLANNED_DATE",mPlDate);
			row.setValue("PLANNED_WEEK",mPlWeek);
			row.setValue("GEN_ID",mGenId);
			row.setValue("GENTYPE",mGenType);
			row.setValue("REPLACED_BY_PM_NO",mRepPmNo);
			row.setValue("REPLACED_BY_PM_REVISION",mRepPmRev);
			row.setValue("REPLACED_BY_SEQ_NO",mRepSNo);
			row.setValue("EVENT",mEvent);
			row.setValue("EVENT_SEQ",mEvSeq);
			row.setValue("TESTPOINT",mTstP);
			row.setValue("PARAMETER",mParam);
			row.setValue("VALUE",mVal);

			headset.setRow(row);

		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();

		adjust();       
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if ( headset.countRows() == 0 )
		{
			mgr.showAlert(mgr.translate("PCMWVIEWGENERATIONDLGNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("OBJVERSION");
		f.setHidden();
		f.setFunction("''");

                f = headblk.addField("PM_NO");
                f.setSize(8);
		f.setLabel("PCMWVIEWGENERATIONDLGPM_NO: Pm No");
		f.setFunction("''");
		f.setReadOnly();

                f = headblk.addField("PM_REVISION");
		f.setSize(20);
		f.setLabel("PCMWVIEWGENERATIONDLGPMREV: Pm Revision");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("MCHCODE");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGMCHCODE: Object Id");
		f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Code(:PM_NO,:PM_REVISION)");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("DESCRIPTION");
		f.setSize(27);
		f.setLabel("PCMWVIEWGENERATIONDLGDESCRIPTION: Description");
		f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Desc(:PM_NO,:PM_REVISION)");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("SITE");
		f.setSize(6);
		f.setLabel("PCMWVIEWGENERATIONDLGSITE: Site");
		f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Site(:PM_NO,:PM_REVISION)");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("PLANNED_DATE","Date");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGPLANNED_DATE: Planned Date");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("PLANNED_WEEK");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGPLANNED_WEEK: Planned Week");
		f.setFunction("''");
		f.setReadOnly();

                f = headblk.addField("GEN_ID");
		f.setSize(13);
		f.setLOV("PmGenerationLov.asp",600,445);
		f.setLabel("PCMWVIEWGENERATIONDLGGEN_ID: Gen Id");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("GENTYPE");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGGENTYPE: Generation Type");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("REPLACED_BY_PM_NO","Number");
		f.setSize(13);
                f.setLabel("PCMWVIEWGENERATIONDLGREPLACED_BY_PM_NO: Replaced By Pm No");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("REPLACED_BY_PM_REVISION");
	        f.setSize(13);
                f.setLabel("PCMWVIEWGENERATIONDLGREPLACED_BY_PM_REVISION: Replaced By Pm Revision");
	        f.setFunction("''");
	        f.setReadOnly();


		f = headblk.addField("REPLACED_BY_SEQ_NO","Number");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGREPLACED_BY_SEQ_NO: Replaced By Pm Seq No");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("EVENT");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGEVENT: Event");
		f.setUpperCase();
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("EVENT_SEQ","Number");
		f.setSize(13);
		f.setLabel("PCMWVIEWGENERATIONDLGEVENT_SEQ: Event Seq No");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("TESTPOINT");
		f.setSize(8);
		f.setLabel("PCMWVIEWGENERATIONDLGTESTPOINT: Test Point");
		f.setUpperCase();
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("PARAMETER");
		f.setSize(8);
		f.setLabel("PCMWVIEWGENERATIONDLGPARAMETER: Parameter");
		f.setUpperCase();
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("VALUE","Number");
		f.setSize(8);
		f.setLabel("PCMWVIEWGENERATIONDLGVALUE: Value");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("WO_NO","Number");
		f.setSize(8);
		f.setHidden();
		f.setFunction("''");
		f.setReadOnly();

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
	}

	public void  adjust()
	{

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWVIEWGENERATIONDLGTITLE: Generation Details";
	}

	protected String getTitle()
	{
		return "PCMWVIEWGENERATIONDLGTITLE: Generation Details";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}
}
