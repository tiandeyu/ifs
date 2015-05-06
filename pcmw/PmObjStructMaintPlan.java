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
*  File        : PmObjStructMaintPlan.java 
*  Created     : 041222  NIJALK (AMEC112 - Job Program)
*  Modified    :
*  050117   NIJALK   Removed unused variables.
*  060126   THWILK   Corrected localization errors.
*  060815   AMNILK   Bug 58216, Eliminated SQL Injection security vulnerability.
*  060904   AMNILK   Merged Bug Id: 58216.
*  060906   ILSOLK   Merged Bug 60074.
*  070725   AMNILK   Eliminated XSS Security Vulnerability.
*  091111   SHAFLK   Bug 86600, Modified preDefine(), printContents(), countFind() and okFind().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PmObjStructMaintPlan extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmObjStructMaintPlan");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPBuffer SecBuff;
    private boolean varSec;
    private boolean secGen;
    private int currrow;
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPField f;

    private boolean bOpenNewWindow;
    private String newWinHandle;
    private int newWinHeight = 600;
    private int newWinWidth = 900;

    private String calling_url;
    private String urlString;

    private String sMchCode;
    private String sContract;

    //===============================================================
    // Construction 
    //===============================================================
    public PmObjStructMaintPlan(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        sMchCode = "";
        sContract = "";
        calling_url = "";

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        sMchCode = ctx.readValue("MCH_CODE",sMchCode);
        sContract = ctx.readValue("MCH_CODE_CONTRACT",sContract);
        varSec = ctx.readFlag("VARSEC",false);
        secGen = ctx.readFlag("SECGEN",false);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.readValue("MCH_CODE")) && !mgr.isEmpty(mgr.readValue("CONTRACT")))
        {
            sMchCode =  mgr.readValue("MCH_CODE");
            sContract = mgr.readValue("CONTRACT");
            okFind();
        }
        else
            okFind();

        securityChk();
        adjust();

        ctx.writeValue("MCH_CODE",sMchCode);
        ctx.writeValue("MCH_CODE_CONTRACT",sContract);
        ctx.writeFlag("VARSEC",varSec);
        ctx.writeFlag("SECGEN",secGen);
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
            ASPManager mgr = getASPManager();

            q = trans.addQuery(headblk);
	    //Bug 58216 Start
            q.addWhereCondition("Pm_Action_Calendar_Plan_API.Get_Mch_Code(PM_NO,PM_REVISION)= ?");
            q.addWhereCondition("Pm_Action_API.Get_Mch_Code_Contract(PM_NO,PM_REVISION) = ?");
	    q.addParameter("MCH_CODE",sMchCode);
	    q.addParameter("CONTRACT",sContract);
	    //Bug 58216 End

            if (mgr.dataTransfered())
                    q.addOrCondition(mgr.getTransferedData());

            q.includeMeta("ALL");
            q.setOrderByClause("PLANNED_DATE");
            mgr.querySubmit(trans, headblk);

            if (headset.countRows() == 0)
            {
                headset.clear();
            }  
    }

    public void countFind()
    {
            ASPManager mgr = getASPManager();

            q = trans.addQuery(headblk);
            q.setSelectList("to_char(count(*)) N");
            q.addWhereCondition("Pm_Action_Calendar_Plan_API.Get_Mch_Code(PM_NO,PM_REVISION)= ?");
            q.addWhereCondition("Pm_Action_API.Get_Mch_Code_Contract(PM_NO,PM_REVISION) = ?");
	    q.addParameter("MCH_CODE",sMchCode);
	    q.addParameter("CONTRACT",sContract);
            mgr.submit(trans);
            headlay.setCountValue(toInt(headset.getRow().getValue("N")));
            headset.clear();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    private String createTransferUrl(String url, ASPBuffer object)
    {
            ASPManager mgr = getASPManager();

            try

            
            {
                    String pkg = mgr.pack(object,1900 - url.length());
                    char sep = url.indexOf('?') > 0 ? '&' : '?';
                    urlString = url + sep + "__TRANSFER=" + pkg ;
                    return urlString;
            }

            catch (Throwable any)
            {
		   return null;
                    
            }

    }

    public void openNewPmWindow(ASPBlockLayout lay1, ASPRowSet set1, String winHandle)
    {
        ASPManager mgr = getASPManager();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);
            
        if (lay1.isMultirowLayout())
                set1.store();
        else
        {

                 set1.unselectRows();
                 set1.selectRow();
        }
        bOpenNewWindow = true;
        urlString = createTransferUrl(winHandle+".page", set1.getSelectedRows("WO_NO"));
        newWinHandle = winHandle;
    }

            
    public void prepWorkOrder()
    {
        openNewPmWindow(headlay, headset, "ActiveSeparate2");
    }

    public void reportInRouteWO()
    {
        openNewPmWindow(headlay, headset, "ActiveRound");
    }
            
    

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("PM_NO","Number");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMNO: PM No");
        f.setSize(6);
        f.setReadOnly();

        f = headblk.addField("PM_REVISION");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMREVISION: Revision");
        f.setSize(6);
        f.setReadOnly();

        f = headblk.addField("SEQ_NO","Number");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANSEQNO: Seq No");
        f.setSize(10);
        f.setHidden();

        f = headblk.addField("MCH_CODE");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANMCHCODE: Object ID");
        f.setSize(12);
        f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Code(:PM_NO,:PM_REVISION)");
        f.setReadOnly();

        f = headblk.addField("PLANNED_DATE","Datetime");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPLANNEDDATE: Planned Date");
        f.setReadOnly();

        f = headblk.addField("PLANNED_WEEK");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPLANNEDWEEK: Planned Week");
        f.setSize(4);
        f.setReadOnly();

        f = headblk.addField("GENERATION_DATE","Datetime");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANGENERATIONDATE: Generation Date");
        f.setReadOnly();

        f = headblk.addField("COMPLETION_DATE","Datetime");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANCOMPLETIONDATE: Completion Date");
        f.setReadOnly();

        f = headblk.addField("PM_ADJUSTED_DATE");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMADJUSTEDDATE: Adjusted");
        f.setSize(20);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("WO_NO","Number");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANWONO: WO Number");
        f.setSize(8);
        f.setReadOnly();

        f = headblk.addField("CONTRACT");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANCONTRACT: Site");
        f.setSize(5);
        f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Site(:PM_NO,:PM_REVISION)");
        f.setReadOnly();

        f = headblk.addField("WO_STATUS");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANWOSTATUS: Status");
        f.setFunction("WORK_ORDER_API.Get_Wo_Status_Id_Cl(:WO_NO)");
        f.setSize(10);
        f.setReadOnly();

        f = headblk.addField("NOTE");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANNOTE: Inspection Note");
        f.setFunction("WORK_ORDER_API.Get_Note(:WO_NO,:PM_NO,:PM_REVISION)");
        f.setSize(20);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_ADJUSTED_DATE_DB");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMADJUSTEDDATEDB: Pm Adjusted Date");
        f.setHidden();

        f = headblk.addField("PM_GENERATEABLE");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMGENERATEABLE: Generatable");
        f.setSize(5);
        f.setReadOnly();

        f = headblk.addField("PM_GENERATEABLE_DB");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMGENERATEABLEDB: Pm Generateable");
        f.setHidden();

        f = headblk.addField("SIGNATURE");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANSIGNATURE: Signature");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REMARK");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANREMARK: Remark");
        f.setSize(20);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_TYPE");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPMTYPE: PM Type");
        f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_PM_TYPE(:PM_NO,:PM_REVISION)");
        f.setHidden();

        f = headblk.addField("PLAN_HRS","Number");
        f.setLabel("PCMWPMOBJSTRUCTMAINTPLANPLANHRS: Plan Hours");
        f.setFunction("PM_ACTION_API.Get_Plan_Hrs(:PM_NO,:PM_REVISION)");
        f.setHidden();

        f = headblk.addField("S_PM_STATE");
        f.setFunction("Pm_Action_API.Get_Pm_State(:PM_NO,:PM_REVISION)");
        f.setHidden();

        f = headblk.addField("S_VALID_PM");
        f.setFunction("Pm_Action_API.Is_Valid_Pm(:PM_NO,:PM_REVISION)");
        f.setHidden();

        f = headblk.addField("IS_CONNECTED");
        f.setFunction("Pm_Action_Connection_API.Is_Connected(:PM_NO,:PM_REVISION)");
        f.setHidden();

        f = headblk.addField("IS_GENERATABLE");
        f.setFunction("Pm_Action_Calendar_Plan_API.Is_Generateable(:PM_NO,:PM_REVISION)");
        f.setHidden();

        f = headblk.addField("GENABLE1");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PSDATE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PFDATE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ISGENTYPE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("GNTYPE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SNULL");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("WO_NO1");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("GEN_ID1");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("IS_ACTIVE");
        f.setFunction("WORK_ORDER_API.Is_Active(:WO_NO)");
        f.setHidden();

        f = headblk.addField("IS_SEPARATE");
        f.setFunction("ACTIVE_SEPARATE_API.Is_Separate(:WO_NO)");
        f.setHidden();

        headblk.setView("PM_ACTION_CALENDAR_PLAN");
        headblk.setTitle(mgr.translate("PCMWPMOBJSTRUCTMAINTPLANSELECTOBJ: Selected Object:"));
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableModeLabel();
        headbar.enableMultirowAction();

        headbar.defineCommand(headbar.COUNTFIND,"countFind");
        headbar.enableCommand(headbar.FIND);
        headbar.defineCommand(headbar.OKFIND,"okFind");

        headbar.addCustomCommand("prepWorkOrder",mgr.translate("PREPAREWO: Prepare Work Order..."));
        headbar.addCustomCommand("reportInRouteWO",mgr.translate("PCMWREPINROUWO: Report in Route Work Order...")); 
       
        headbar.appendCommandValidConditions("prepWorkOrder","IS_ACTIVE","Enable","TRUE");
        headbar.appendCommandValidConditions("prepWorkOrder","IS_SEPARATE","Enable","TRUE");
        headbar.appendCommandValidConditions("prepWorkOrder","WO_NO","Disable","");
        headbar.appendCommandValidConditions("reportInRouteWO","IS_ACTIVE","Enable","TRUE");
        headbar.appendCommandValidConditions("reportInRouteWO","IS_SEPARATE","Enable","FALSE");
        headbar.appendCommandValidConditions("reportInRouteWO","WO_NO","Disable","");
        

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();
        headtbl.enableRowSelect();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2);

        this.disableFooter();
        this.disableHeader();
        this.disableHelp();
        this.disableHomeIcon();
        this.disableNavigate();
        this.disableOptions();
    }

    public void securityChk()
    {
            ASPManager mgr = getASPManager();

            if (!varSec)
            {
                    trans.clear();

                    trans.addSecurityQuery("Pm_Action_API","Generate__");

                    trans = mgr.perform(trans);

                    SecBuff = trans.getSecurityInfo();

                    if (SecBuff.itemExists("Pm_Action_API.Generate__"))
                            secGen = true;

                    varSec = true;
            }
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (!secGen)
                headbar.removeCustomCommand("generateWO");

    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
            return "PCMWPMOBJSTRUCTMAINTPLANTITLE: Maintenance Plan";
    }

    protected String getTitle()
    {
            return "PCMWPMOBJSTRUCTMAINTPLANTITLE: Maintenance Plan";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());
        appendToHTML("<br>");

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));   //XSS_Safe AMNILK 20070725
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");
            appendDirtyJavaScript(newWinWidth);
            appendDirtyJavaScript(",height=");
            appendDirtyJavaScript(newWinHeight);
            appendDirtyJavaScript("\"); \n");  
            
        }
       
    }
}
