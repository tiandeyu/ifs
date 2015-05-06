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
*  File        : WOPermitIsoOrdersOvw.java 
*  Created     : NEKOLK  050805 Created.
*  Modified    : NEKOLK  100805 Made changes in Define()..
*                NEKOLK  210905 Call 126829: made changes in PreDefine().
*                NEKOLK  210905 Call 126821: made changes in PreDefine().
*  051024   NIJALK   Bug 128059: Modified okFind() to filter from user allowed site.
*  060126   THWILK   Corrected localization errors.
*  070718   NIJALK   Bug 65987, Modified function calls for WORK_ORDER_STATUS, PERMIT_STATUS,  ISOLATION_STATUS.
*  070808   AMDILK   Merged bug 65987
*  071201   CHANLK   Bug 67819, Change okFind to enable next button.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WOPermitIsoOrdersOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WOPermitIsoOrdersOvw");

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

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String pertyid;
	private String flag1;
	private String val;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;
	private String calling_url;
	private ASPBuffer buffer;
	private ASPBuffer row;  
	private boolean actEna1;
	private boolean again;

	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle; 

	//===============================================================
	// Construction 
	//===============================================================
	public WOPermitIsoOrdersOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		pertyid = ctx.readValue("PERTY", "");
		flag1 = ctx.readValue("FLAG1", "FALSE");

		actEna1 = ctx.readFlag("ACTENA1", false);
		again = ctx.readFlag("AGAIN", false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_TYPE_ID")))
		{
			okFind(); 
		}

		adjust();

		ctx.writeFlag("ACTENA1", actEna1);
		ctx.writeFlag("AGAIN", again);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","PERMIT_TYPE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");

		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
                q.addWhereCondition("CONTRACT IN (SELECT  User_Allowed_Site_API.Authorized(CONTRACT) FROM dual)");
		q.includeMeta("ALL");

      //Bug 67819, Start
      mgr.querySubmit(trans,headblk);
      //Bug 67819, End

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPEOVWNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


   public void preparePermit()
	{
            ASPManager mgr = getASPManager();

                if (headlay.isMultirowLayout())
                    headset.goTo(headset.getRowSelected());
                else
                    headset.selectRow();

                //Web Alignment - simplify code for RMBs
                bOpenNewWindow = true;

                urlString = "Permit.page?PERMIT_SEQ="+mgr.URLEncode(headset.getRow().getValue("PERMIT_SEQ"));
                
                newWinHandle = "frmPermit";

	}


   public void prepareWo()
	{
            ASPManager mgr = getASPManager();

                if (headlay.isMultirowLayout())
                    headset.goTo(headset.getRowSelected());
                else
                    headset.selectRow();

                bOpenNewWindow = true;

                urlString = "ActiveSeparate2.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
                
                newWinHandle = "frmWorkOrder";

	}

   public void prepareIsoOrder()
     {
         ASPManager mgr = getASPManager();

             if (headlay.isMultirowLayout())
                 headset.goTo(headset.getRowSelected());
             else
                 headset.selectRow();

             bOpenNewWindow = true;

             urlString = "DelimitationOrderTab.page?DELIMITATION_ORDER_NO="+mgr.URLEncode(headset.getRow().getValue("DELIMITATION_ORDER_NO"));

             newWinHandle = "frmDelOrder";

     }



	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();


		f = headblk.addField("WO_NO","Number","#");
		f.setSize(12);
		f.setLabel("PCMWWOPERMITISOORDERSOVWWONO: Work Order No");
		f.setReadOnly();
		f.setMaxLength(8);


		f = headblk.addField("WO_SITE");
                f.setSize(12);
		f.setLabel("PCMWWOSITE: Work Order Site");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Contract(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(8);


		f = headblk.addField("WO_TYPE");
                f.setSize(12);
		f.setLabel("PCMWAWOTYPE: Work Order Type");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Work_Type_Id(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("OBJECT_ID");
                f.setSize(12);
		f.setLabel("PCMWOBJECTID: Object ID");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("OBJECT_SITE");
                f.setSize(12);
		f.setLabel("PCMWOBJECTSITE: Object Site");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Mch_Code_Contract(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("ORG_CODE");
                f.setSize(12);
		f.setLabel("WOPERMITORGCODE: Executing Department");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Org_Code(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("PREPARED_BY");
                f.setSize(12);
		f.setLabel("PREPAREDBYWOPERMIT: WO Prepared By");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Prepared_By(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("WORK_LEADER");
                f.setSize(12);
		f.setLabel("PCMWWOPERMITISOORDERSOVWRKLEADER: Work Leader");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_Work_Leader_Sign(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("WORK_ORDER_STATUS");
                f.setSize(12);
		f.setLabel("PCMWWOSTATUS: Work Order Status");
                f.setFunction("ACTIVE_WORK_ORDER_API.Get_State(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("COST_CENTER");
                f.setSize(12);
		f.setLabel("PCMWCOSTCENTER: Cost Center");
                f.setFunction("WORK_ORDER_API.Get_Cost_Cnt(:WO_NO)");
		f.setReadOnly();


		f = headblk.addField("PERMIT_SEQ");
                f.setSize(12);
		f.setLabel("PCMWPERMITNO: Permit No");
		f.setReadOnly();


		f = headblk.addField("PERMIT_TYPE");
                f.setSize(12);
		f.setLabel("PCMWPERMITTYPE: Permit Type");
                f.setFunction("PERMIT_API.Get_Permit_Type_Id(:PERMIT_SEQ)");
		f.setReadOnly();


		f = headblk.addField("PERMIT_STATUS");
                f.setSize(12);
		f.setLabel("PCMWPERMITSTATUS: Permit Status");
                f.setFunction("PERMIT_API.Get_State(:PERMIT_SEQ)");
		f.setReadOnly();


		f = headblk.addField("PERMIT_PREPARED_BY");
                f.setSize(12);
		f.setLabel("PCMWPERMITPREPAREDBY: Permit Prepared By");
                f.setFunction("PERMIT_API.Get_Prepared_By(:PERMIT_SEQ)");
		f.setReadOnly();


		f = headblk.addField("PERMIT_WORK_LEADER");
                f.setSize(12);
		f.setLabel("PCMWWOPERMITISOORDERSOVWPERMITWLEADER: Permit Work Leader");
                f.setFunction("PERMIT_API.Get_Work_Leader_Sign(:PERMIT_SEQ)");
                f.setReadOnly();

                f = headblk.addField("VALID_FROM_DATE","Datetime");
		f.setSize(28);
                f.setFunction("PERMIT_API.Get_Valid_From_Date(:PERMIT_SEQ)");
		f.setLabel("PCMWVALIDFROM: Valid From Date");
                
                f = headblk.addField("VALID_TO_DATE","Datetime");
		f.setSize(28);
                f.setFunction("PERMIT_API.Get_Valid_To_Date(:PERMIT_SEQ)");
		f.setLabel("PCMWVALITODATE: Valid To Date");

		f = headblk.addField("DELIMITATION_ORDER_NO", "Number", "#");
                f.setSize(12);
		f.setLabel("PCMWISOLATIONORDERNO: Isolation Order No");
		f.setReadOnly();
                

		f = headblk.addField("ISOLATION_TYPE");
                f.setSize(12);
		f.setLabel("PCMWISOLATIONTYPE: Isolation Type");
                f.setFunction("ISOLATION_ORDER_PERMIT_API.Get_Isolation_Type(:PERMIT_SEQ,:DELIMITATION_ORDER_NO)");
		f.setReadOnly();


		f = headblk.addField("ISOLATION_STATUS");
                f.setSize(12);
		f.setLabel("PCMWISOLATIONSTATUSE: Isolation Order Status");
                f.setFunction("DELIMITATION_ORDER_API.Get_Decoded_State(:DELIMITATION_ORDER_NO)");
		f.setReadOnly();


		f = headblk.addField("ISO_PREPARED_BY");
                f.setSize(12);
		f.setLabel("PCMWISOPREPAREDBY: Prepared By");
                f.setFunction("DELIMITATION_ORDER_API.Get_Prepared_By(:DELIMITATION_ORDER_NO)");
		f.setReadOnly();


	        f = headblk.addField("PLAN_START_ESTABLISHMENT","Datetime");
		f.setSize(28);
                f.setFunction("DELIMITATION_ORDER_API.Get_Plan_Start_Establishment(:DELIMITATION_ORDER_NO)");
		f.setLabel("PCMWDELIMITATIONORDERTABPLANSTRTEST: Planned Start Establishment");
                
                f = headblk.addField("PLAN_FINISH_ESTABLISHMENT","Datetime");
		f.setSize(28);
                f.setFunction("DELIMITATION_ORDER_API.Get_Plan_Finish_Establishment(:DELIMITATION_ORDER_NO)");
		f.setLabel("PCMWDELIMITATIONORDERTABPLANFINEST: Planned Completion Establishment");

		f = headblk.addField("PLAN_START_REESTABLISHMENT","Datetime");
		f.setSize(28);
                f.setFunction("DELIMITATION_ORDER_API.Get_Plan_Start_Reestablishment(:DELIMITATION_ORDER_NO)");
                f.setLabel("PCMWDELIMITATIONORDERTABPLANSTARTREEST: Planned Start Reestablishment");

		f = headblk.addField("PLAN_FINISH_REESTABLISHMENT","Datetime");
		f.setSize(28);
                f.setFunction("DELIMITATION_ORDER_API.Get_Plan_Finish_Reest(:DELIMITATION_ORDER_NO)");
		f.setLabel("PCMWDELIMITATIONORDERTABPLANFINREEST: Planned Completion Reestablishment"); 

                f = headblk.addField("FLDISSEP");
                f.setFunction("ACTIVE_SEPARATE_API.Is_Separate(:WO_NO)");
                f.setHidden();
                
                headblk.setView("WO_PERMIT_ISO_ORDER");
		headblk.defineCommand("WORK_ORDER_PERMIT_API","");
		headset = headblk.getASPRowSet();

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");   

		headbar.addCustomCommand("attributes",  mgr.translate("PCMWPERMITTYPEOVWSPA: Attributes..."));

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWPERMIISOORDERS: Overview - Work Orders, Permits and Isolation Orders"));
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		headbar.addCustomCommand("prepareWo",mgr.translate("PCMWAPREPAREWO: Prepare Work Order..."));
	        headbar.addCustomCommand("preparePermit", mgr.translate("PCMWWORKORDERPERMITPREPERM: Prepare Permit..."));
	        headbar.addCustomCommand("prepareIsoOrder", mgr.translate("PCMWWORKORDERPERMITISOORDER: Prepare Isolation Order..."));

                headbar.addCommandValidConditions("prepareWo","FLDISSEP","Disable","FALSE");
                headbar.addCommandValidConditions("preparePermit","PERMIT_SEQ","Disable","null");
		headbar.addCommandValidConditions("prepareIsoOrder","DELIMITATION_ORDER_NO","Disable","null");
	}


	public void adjust()
	{
		ASPManager mgr = getASPManager();

		if (!actEna1)
			headbar.removeCustomCommand("attributes");
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
            return "PCMWWOPERMITISOORDERS: Overview - Work Orders, Permits and Isolation Orders";
	}

	protected String getTitle()
	{
            return "PCMWWOPERMITISOORDERS: Overview - Work Orders, Permits and Isolation Orders";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

	 //appendDirtyJavaScript(" window.name = \"frmWoPermit\";\n");

	if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(urlString);
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
     }
}
