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
*  File        : MaintPosting.java 
*  Created     : 060201   NIJALK  (Call ID: 132475) 
*  Modified    : 
*  060215   NIJALK   Bug 134216: Changed default Order By.
*  060308   THWILK   Call ID 136622,Modified method workOrder.
*  060308   NIJALK   Bug 136630: Modified okFind().
*  060308   THWILK   Call ID 136629,Modified setCodePartLabels().
*  060309   NIJALK   Call 136805: Removed method woTransHist() and Hyperlinked the field TRANSACTION_ID.
*  060728   AMDILK   Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  060807   AMNILK   Merged Bug ID: 58214.
*  060815   DIAMLK   Bug 58216, Eliminated SQL Injection security vulnerability.
*  060905   AMDILK   Merged with the Bug Id 58216
*  070730   ILSOLK   Eliminated LocaliztionErrors.
*  071029   SHAFLK   Bug 67390, Added some more columns which retrieve data from postings.
*  080202   NIJALK   Bug 66456, Modified preDefine().
*  100730   ILSOLK   Bug 90329, Modified preDefine(), printContents() and added modifyDate(), refreshRow().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class MaintPosting extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintPosting");

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
    private ASPTabContainer tabs;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================

    private ASPTransactionBuffer trans;
    private ASPQuery q;
    private ASPCommand cmd;
    private boolean bOpenNewWindow;
    private String urlString;
    private int newWinHeight = 600;
    private int newWinWidth = 900;
    private String newWinHandle;
    private String sCompany;

    //===============================================================
    // Construction 
    //===============================================================
    public MaintPosting(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        sCompany = ctx.readValue("SCOMPANY","");

        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
        }
        //Bug 90329,Start
        else if ( !mgr.isEmpty(mgr.readValue("REFRESH_FLAG")))
        {
	    refreshRow();
        }
        //Bug 90329,End

        adjust();

        ctx.writeValue("SCOMPANY",sCompany);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        if (!mgr.isEmpty(sCompany))
        {
            q.addWhereCondition("COMPANY = ?");
            q.addParameter("COMPANY",sCompany);
        }
        if (mgr.dataTransfered())
                q.addOrCondition(mgr.getTransferedData());
        q.setOrderByClause("TRANSACTION_ID DESC");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);
                                              
        if (headset.countRows() == 1)
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        else if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWOTIMETRANSACTIONHISTNODATA: No data found."));
            headset.clear();
        } 
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        if (mgr.dataTransfered())
                q.addOrCondition(mgr.getTransferedData());
        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }     

//-----------------------------------------------------------------------------
//------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
    public void workOrder()
    {
        ASPManager mgr = getASPManager();
        String newWin = "";
        String woType = "";

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();        

        if (headset.countRows()>0)
        {
            woType = headset.getRow().getValue("WO_TYPE");

            if ("ActiveSeparate".equals(woType))
                newWin = "ActiveSeperateReportInWorkOrder";
            else if ("ActiveRound".equals(woType))
                newWin = "ActiveRound";
            else if ("HistoricalActiveSeparate".equals(woType))
                newWin = "HistoricalSeparateRMB";
            else if ("HistoricalActiveRound".equals(woType))
                newWin = "HistoricalRound";
        }  

        if (!mgr.isEmpty(newWin))
        {
            bOpenNewWindow = true;
            urlString = newWin+".page?WO_NO="+ mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                                "&FRMNAME=MaintPosting";
            newWinHandle = newWin;
        }

    }

    //Bug 90329,Start
    public void modifyDate()
    {       
	ASPManager mgr = getASPManager();
	int count;
	String transid = "";
	if (headlay.isMultirowLayout())
	{
	   headset.storeSelections();
	   headset.setFilterOn();
	   count = headset.countSelectedRows();
	}
	else
	{
	   headset.unselectRows();
	   count = 1;
	}
			    
	trans.clear();
			       
	for (int i = 0; i < count; i ++)
	{  
	   transid = transid + "^" + headset.getRow().getValue("TRANSACTION_ID");
	   if (headlay.isMultirowLayout())
	      headset.next();
	}

	if (headlay.isMultirowLayout())
		headset.setFilterOff();
	    
	bOpenNewWindow = true;
	urlString = "ChangeDateAppliedDlg.page?TRANSID=" + mgr.URLEncode(transid) +
		    "&FRMNAME=MaintPosting";
	newWinHandle = "ChangeDate";
				   
    }

    public void refreshRow()
    {
       if (headlay.isMultirowLayout())
       {
           headset.refreshAllRows();
       }
       else
       {
          headset.refreshRow();
       }
    }
    //Bug 90329,End

    public void setCodePartLabels()
    {
        ASPManager mgr = getASPManager();
        String company = "";

        if (headset.countRows()>0)
            company = headset.getRow().getValue("COMPANY");
        
        if (mgr.isEmpty(company))
            company = sCompany;

        trans.clear();

        //Fetching names for Code Parts A to J
        cmd = trans.addCustomFunction("GETCODENAMEA","Accounting_Code_Parts_API.Get_Name","ACCOUNT_NO");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("ACCOUNT_NO","A");

        cmd = trans.addCustomFunction("GETCODENAMEB","Accounting_Code_Parts_API.Get_Name","COST_CENTER");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("COST_CENTER","B");

        cmd = trans.addCustomFunction("GETCODENAMEC","Accounting_Code_Parts_API.Get_Name","CODE_C");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_C","C");

        cmd = trans.addCustomFunction("GETCODENAMED","Accounting_Code_Parts_API.Get_Name","CODE_D");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_D","D");

        cmd = trans.addCustomFunction("GETCODENAMEE","Accounting_Code_Parts_API.Get_Name","OBJECT_NO");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("OBJECT_NO","E");

        cmd = trans.addCustomFunction("GETCODENAMEF","Accounting_Code_Parts_API.Get_Name","PROJECT_NO");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("PROJECT_NO","F");

        cmd = trans.addCustomFunction("GETCODENAMEG","Accounting_Code_Parts_API.Get_Name","CODE_G");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_G","G");

        cmd = trans.addCustomFunction("GETCODENAMEH","Accounting_Code_Parts_API.Get_Name","CODE_H");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_H","H");

        cmd = trans.addCustomFunction("GETCODENAMEI","Accounting_Code_Parts_API.Get_Name","CODE_I");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_I","I");

        cmd = trans.addCustomFunction("GETCODENAMEJ","Accounting_Code_Parts_API.Get_Name","CODE_J");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_J","J");

        trans = mgr.perform(trans);

        String sLabelA = trans.getValue("GETCODENAMEA/DATA/ACCOUNT_NO");
        mgr.getASPField("ACCOUNT_NO").setLabel(mgr.isEmpty(sLabelA)?"@A":sLabelA);

        String sLabelB = trans.getValue("GETCODENAMEB/DATA/COST_CENTER");
        mgr.getASPField("COST_CENTER").setLabel(mgr.isEmpty(sLabelB)?"@B":sLabelB);

        String sLabelC = trans.getValue("GETCODENAMEC/DATA/CODE_C");
        mgr.getASPField("CODE_C").setLabel(mgr.isEmpty(sLabelC)?"@C":sLabelC);

        String sLabelD = trans.getValue("GETCODENAMED/DATA/CODE_D");
        mgr.getASPField("CODE_D").setLabel(mgr.isEmpty(sLabelD)?"@D":sLabelD);

        String sLabelE = trans.getValue("GETCODENAMEE/DATA/OBJECT_NO");
        mgr.getASPField("OBJECT_NO").setLabel(mgr.isEmpty(sLabelE)?"@E":sLabelE);

        String sLabelF = trans.getValue("GETCODENAMEF/DATA/PROJECT_NO");
        mgr.getASPField("PROJECT_NO").setLabel(mgr.isEmpty(sLabelF)?"@F":sLabelF);

        String sLabelG = trans.getValue("GETCODENAMEG/DATA/CODE_G");
        mgr.getASPField("CODE_G").setLabel(mgr.isEmpty(sLabelG)?"@G":sLabelG);

        String sLabelH = trans.getValue("GETCODENAMEH/DATA/CODE_H");
        mgr.getASPField("CODE_H").setLabel(mgr.isEmpty(sLabelH)?"@H":sLabelH);

        String sLabelI = trans.getValue("GETCODENAMEI/DATA/CODE_I");
        mgr.getASPField("CODE_I").setLabel(mgr.isEmpty(sLabelI)?"@I":sLabelI);

        String sLabelJ = trans.getValue("GETCODENAMEJ/DATA/CODE_J");
        mgr.getASPField("CODE_J").setLabel(mgr.isEmpty(sLabelJ)?"@J":sLabelJ);
    }

    public void getDefaultCompany()
    {
        ASPManager mgr = getASPManager();
        
        trans.clear();

        cmd = trans.addCustomFunction("GETCON","User_Default_API.Get_Contract","CONTRACT");

        cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT","GETCON/DATA");
        trans = mgr.perform(trans);

        sCompany = trans.getValue("GETCOMP/DATA/COMPANY");
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("SEARCHME");
        f.setFunction("'Y'");
        f.setHidden();

        f = headblk.addField("TRANSACTION_ID","Number");
        f.setLabel("PCMWMAINTPOSTINGTRANSACTIONID: Transaction ID");
        f.setQueryable();
        f.setHyperlink("WoTimeTransactionHist.page","TRANSACTION_ID,SEARCHME","NEWWIN");

        f = headblk.addField("SEQ","Number");
        f.setLabel("PCMWMAINTPOSTINGSEQ: Seq");
        f.setHidden();

        f = headblk.addField("STR_CODE");
        f.setLabel("PCMWMAINTPOSTINGSTRCODE: Posting Type");
        f.setUpperCase();
        f.setDynamicLOV("POSTING_CTRL_POSTING_TYPE");
        f.setQueryable();

        f = headblk.addField("STR_CODE_DESC");
        f.setLabel("PCMWMAINTPOSTINGSTRCODEDESC: Posting Type Description");
        f.setFunction("POSTING_CTRL_POSTING_TYPE_API.Get_Description(:STR_CODE)");
        f.setQueryable();

        f = headblk.addField("CONTRACT");
        f.setLabel("PCMWMAINTPOSTINGCONTRACT: Site");
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
        f.setQueryable();
        
        f = headblk.addField("WO_NO","Number");
        f.setLabel("PCMWMAINTPOSTINGWO_NO: WO No");
        f.setDynamicLOV("ACTIVE_WORK_ORDER2");
        f.setQueryable();

        //Bug 67390, start
        f = headblk.addField("ORG_CODE");
        f.setLabel("PCMWMAINTPOSTINGORG_CODE: Maint. Org.");
        f.setFunction("WORK_ORDER_CODING_API.Get_Org_Code(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV");
        f.setQueryable();
        //Bug 67390, end

        f = headblk.addField("ROW_NO","Number");
        f.setLabel("PCMWMAINTPOSTINGROW_NO: Row No");
        f.setQueryable();

        f = headblk.addField("WORK_ORDER_COST_TYPE");
        f.setLabel("PCMWMAINTPOSTINGCOSTTYPE: Cost Type");
        f.enumerateValues("WORK_ORDER_COST_TYPE_API");
        f.setQueryable();

        f = headblk.addField("TRANS_DATE","Date");
        f.setLabel("PCMWMAINTPOSTINGDATEAPPLIED: Date Applied");
        f.setQueryable();

        //Bug 67390, start
        f = headblk.addField("CRE_DATE","Date");
        f.setLabel("PCMWMAINTPOSTINGTRANS_DATE: Transaction Date");
        f.setFunction("WORK_ORDER_CODING_API.Get_Cre_Date(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");
        f.setQueryable();

        f = headblk.addField("QTY","Number");
        f.setLabel("PCMWMAINTPOSTINGQTY: Hrs/Qty");
        f.setFunction("WORK_ORDER_CODING_API.Get_Qty(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");

        f = headblk.addField("AMOUNT","Number");
        f.setLabel("PCMWMAINTPOSTINGAMOUNT: Cost Amount");
        f.setFunction("WORK_ORDER_CODING_API.Get_Amount(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");

        f = headblk.addField("CMNT");
        f.setLabel("PCMWMAINTPOSTINGCMNT: Comment");
        f.setFunction("WORK_ORDER_CODING_API.Get_Cmnt(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");
        f.setQueryable();

        f = headblk.addField("ROLE_CODE");
        f.setLabel("PCMWMAINTPOSTINGROLE_CODE: Craft Id");
        f.setFunction("WORK_ORDER_CODING_API.Get_Role_Code(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");
        f.setQueryable();

        f = headblk.addField("EMP_NO");
        f.setLabel("PCMWMAINTPOSTINGEMP_NO: Employee No");
        f.setFunction("WORK_ORDER_CODING_API.Get_Emp_NO(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID),WO_TIME_TRANSACTION_HIST_API.Get_Row_No(:TRANSACTION_ID))");
        f.setQueryable();
        //Bug 67390, end

        f = headblk.addField("STATE");
        f.setLabel("PCMWMAINTPOSTINGSTATE: Status");
        f.setQueryable();

        f = headblk.addField("ERROR_DESC");
        f.setLabel("PCMWMAINTPOSTINGERRORDESC: Error Description");
        f.setQueryable();
        f.setSize(50);
        f.setDefaultNotVisible();
        
        f = headblk.addField("ACCOUNT_NO");
        f.setLabel("PCMWMAINTPOSTINGACCOUNTNO: @A");
        f.setDefaultNotVisible();

        f = headblk.addField("COST_CENTER");
        f.setLabel("PCMWMAINTPOSTINGCOSTCENTER: @B");
        f.setDefaultNotVisible();

        f = headblk.addField("PROJECT_NO");
        f.setLabel("PCMWMAINTPOSTINGPROJECTNO: @F");
        f.setDefaultNotVisible();

        f = headblk.addField("OBJECT_NO");
        f.setLabel("PCMWMAINTPOSTINGOBJECTNO: @E");
        f.setDefaultNotVisible();
        
        f = headblk.addField("CODE_C");
        f.setLabel("PCMWMAINTPOSTINGCODEC: @C");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_D");
        f.setLabel("PCMWMAINTPOSTINGCODED: @D");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_G");
        f.setLabel("PCMWMAINTPOSTINGCODEG: @G");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_H");
        f.setLabel("PCMWMAINTPOSTINGCODEH: @H");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_I");
        f.setLabel("PCMWMAINTPOSTINGCODEI: @I");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_J");
        f.setLabel("PCMWMAINTPOSTINGCODEJ: @J");
        f.setDefaultNotVisible();

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = headblk.addField("ACTIVITY_SEQ","Number");
            f.setLabel("PCMWMAINTPOSTINGACTIVITYSEQ: Activity Sequence");
            f.setQueryable();
            f.setDefaultNotVisible();
        }
        //Bug 66456, End

        f = headblk.addField("COST_SOURCE_ID");
        f.setLabel("PCMWMAINTPOSTINGCOSTSOURCEID: Cost Source ID");
        f.setQueryable();
        f.setDefaultNotVisible();

        f = headblk.addField("ADJUSTMENT_ID","Number");
        f.setLabel("PCMWMAINTPOSTINGADJUSTMENTID: Adjustment ID");
        f.setQueryable();
        f.setDefaultNotVisible();

        f = headblk.addField("VOUCHER_NO","Number");
        f.setLabel("PCMWMAINTPOSTINGVOUCHERNO: Voucher No");
        f.setQueryable();

        f = headblk.addField("VOUCHER_TYPE");
        f.setLabel("PCMWMAINTPOSTINGVOUCHERTYPE: Voucher Type");
        f.setUpperCase();
        f.setQueryable();

        f = headblk.addField("ACCOUNTING_YEAR","Number");
        f.setLabel("PCMWMAINTPOSTINGACCYEAR: Accounting Year");
        f.setQueryable();
        f.setDefaultNotVisible();

        f = headblk.addField("ACCOUNTING_PERIOD");
        f.setLabel("PCMWMAINTPOSTINGACCPERIOD: Accounting Period");
        f.setQueryable();
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = headblk.addField("DEBIT_VALUE","Money");
        f.setLabel("PCMWMAINTPOSTINGDEBITAMOUNT: Debit Amount");
        f.setQueryable();

        f = headblk.addField("CREDIT_VALUE","Money");
        f.setLabel("PCMWMAINTPOSTINGCREDITAMOUNT: Credit Amount");
        f.setQueryable();

        f = headblk.addField("DATE_OF_ORIGINE","Datetime");
        f.setLabel("PCMWMAINTPOSTINGDATE: Date");
        f.setQueryable();

        f = headblk.addField("USER_ID");
        f.setLabel("PCMWMAINTPOSTINGUSERID: User");
        f.setFunction("WO_TIME_TRANSACTION_HIST_API.Get_User_Id(:TRANSACTION_ID)");
        f.setQueryable();

        //Hidden fields

        f = headblk.addField("COMPANY");
        f.setLabel("PCMWMAINTPOSTINGCOMPANY: Company");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("WO_TYPE");
        f.setFunction("Active_Work_Order_Api.Get_Work_Order_Type(WO_TIME_TRANSACTION_HIST_API.Get_Wo_No(:TRANSACTION_ID))");
        f.setHidden(); 

        headblk.setView("PCM_ACCOUNTING_POSTING_DATA");
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();
        headbar = mgr.newASPCommandBar(headblk);
        headtbl.enableRowSelect();

        headbar.addCustomCommand("modifyDate",mgr.translate("PCMWMAINTPOSTINGRMBMODATE: Modify Date Applied..."));
        headbar.addCustomCommand("workOrder",mgr.translate("PCMWMAINTPOSTINGRMBWO: Work Order..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("workOrder");
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();
        String sCostType;
        //Get User Default Company
        getDefaultCompany();

        // Set the Field Labels for code parts.
        setCodePartLabels();

      }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWMAINTPOSTINGSTITLE: "+sCompany+" - Query - Postings";
    }

    protected String getTitle()
    {
        return "PCMWMAINTPOSTINGSTITLE: "+sCompany+" - Query - Postings";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();
        printHiddenField("REFRESH_FLAG","");

        appendToHTML(headlay.show());

        appendDirtyJavaScript("window.name = \"MaintPosting\"\n");

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(urlString);
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");      
            appendDirtyJavaScript(newWinWidth);
            appendDirtyJavaScript(",height=");
            appendDirtyJavaScript(newWinHeight);
            appendDirtyJavaScript("\"); \n");  

        }

        //Bug 90329,Start
        appendDirtyJavaScript("function refresh()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
        appendDirtyJavaScript("   f.submit();\n");
        appendDirtyJavaScript("}\n");
       //Bug 90329,End
    }
}
