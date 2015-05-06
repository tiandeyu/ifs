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
*  File        : CssPosting.java 
*  Created     : 051209   NIJALK   (AMAD112 - Cost of Sold Services)
*  Modified    : 
*  060126  THWILK   Corrected localization errors.
*  060215  NIJALK   Bug 134216: Changed default Order By.   
*  060308  THWILK   Call ID 136622,Modified method workOrder.
*  060308  NIJALK   Bug 136631: Modified setCodePartLabels(),run(), adjust().
*  060309  NIJALK   Call 136805: Removed method woTransHist() and Hyperlinked the field TRANSACTION_ID.
*  060803  JEWILK   Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  060807  AmNilk   Merged Bug Id: 58214.
*  060815  DIAMLK   Bug 58216, Eliminated SQL Injection security vulnerability.
*  060905  AMDILK   Merged with the Bug Id 58216
*  061110  AMNILK   MEPR604: Original Source info in voucher rows.Modified workOrder() & preDefine().Added field CSS_ID
*  080202  NIJALK   Bug 66456, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class CssPosting extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CssPosting");

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

    private int newWinHeight = 600;
    private int newWinWidth = 900;

    private String newWinHandle;
    private String urlString;
    private String sCompany;

    //===============================================================
    // Construction 
    //===============================================================
    public CssPosting(ASPManager mgr, String page_path)
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

        if (headset.countRows()>0)
        {
            woType = headset.getRow().getValue("WO_TYPE");

            if ("ActiveSeparate".equals(woType))
                newWin = "WorkOrderCoding1";
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
                                "&FRMNAME=CssPosting&IS_POSTING_ACTIVATE=true";
            newWinHandle = newWin;
        }

    }

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

        f = headblk.addField("SEARCH");
        f.setFunction("'Y'");
        f.setHidden();

        f = headblk.addField("SEARCHME");
        f.setFunction("'Y'");
        f.setHidden();

        f = headblk.addField("COST_TYPE");
        f.setLabel("PCMWCSSPOSTINGCOSTTYPEDB: Cost Type Db");
        f.setHidden(); 

	f = headblk.addField("CSS_ID","Number");
	f.setLabel("PCMWCSSPOSTINGCSSID: Css Transaction ID");
	f.setQueryable();
        
        f = headblk.addField("TRANSACTION_ID","Number");
        f.setLabel("PCMWCSSPOSTINGTRANSACTIONID: Source Transaction ID");
        f.setQueryable();

        f = headblk.addField("STR_CODE");
        f.setLabel("PCMWCSSPOSTINGSTRCODE: Posting Type");
        f.setUpperCase();
        f.setDynamicLOV("POSTING_CTRL_POSTING_TYPE");
        f.setQueryable();

        f = headblk.addField("STR_CODE_DESC");
        f.setLabel("PCMWCSSPOSTINGSTRCODEDESC: Description");
        f.setFunction("POSTING_CTRL_POSTING_TYPE_API.Get_Description(:STR_CODE)");
        f.setQueryable();

        f = headblk.addField("WO_NO","Number");
        f.setLabel("PCMWCSSPOSTINGWO_NO: WO No");
        f.setQueryable();

        f = headblk.addField("ROW_NO","Number");
        f.setLabel("PCMWCSSPOSTINGROW_NO: Row No");
        f.setDynamicLOV("MS_WO_CODING","WO_NO");
        f.setQueryable();

        f = headblk.addField("CONTRACT");
        f.setLabel("PCMWCSSPOSTINGCONTRACT: Site");
        f.setUpperCase();
        f.setDynamicLOV("SITE");
        f.setQueryable();

        f = headblk.addField("COMPANY");
        f.setLabel("PCMWCSSPOSTINGCOMPANY: Company");
        f.setFunction("Site_API.Get_Company(:CONTRACT)");
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();

        f = headblk.addField("ACCOUNT_NO");
        f.setLabel("PCMWCSSPOSTINGACCOUNTNO: @A");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_C");
        f.setLabel("PCMWCSSPOSTINGCODEC: @C");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_D");
        f.setLabel("PCMWCSSPOSTINGCODED: @D");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_G");
        f.setLabel("PCMWCSSPOSTINGCODEG: @G");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_H");
        f.setLabel("PCMWCSSPOSTINGCODEH: @H");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_I");
        f.setLabel("PCMWCSSPOSTINGCODEI: @I");
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_J");
        f.setLabel("PCMWCSSPOSTINGCODEJ: @J");
        f.setDefaultNotVisible();

        f = headblk.addField("COST_CENTER");
        f.setLabel("PCMWCSSPOSTINGCOSTCENTER: @B");
        f.setDefaultNotVisible();
        
        f = headblk.addField("OBJECT_NO");
        f.setLabel("PCMWCSSPOSTINGOBJECTNO: @E");
        f.setDefaultNotVisible();

        f = headblk.addField("PROJECT_NO");
        f.setLabel("PCMWCSSPOSTINGPROJECTNO: @F");
        f.setDefaultNotVisible();
        
        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = headblk.addField("ACTIVITY_SEQ","Number");
            f.setLabel("PCMWCSSPOSTINGACTIVITYSEQ: Activity Sequence");
            f.setDynamicLOV("ACTIVITY");
            f.setQueryable();
            f.setDefaultNotVisible();
        }
        //Bug 66456, End

        f = headblk.addField("COST_TYPE_CLIENT");
        f.setLabel("PCMWCSSPOSTINGCOSTTYPE: Cost Type");
        f.setFunction("Work_Order_Cost_Type_API.Decode(:COST_TYPE)");
        f.setQueryable();

        f = headblk.addField("VALUE");
        f.setLabel("PCMWCSSPOSTINGVALUE: Value");
        f.setQueryable();

        f = headblk.addField("CURR_AMOUNT","Number");
        f.setLabel("PCMWCSSPOSTINGCURRAMOUNT: Currency Amount");
        f.setQueryable();

        f = headblk.addField("CURRENCY_CODE");
        f.setLabel("PCMWCSSPOSTINGCURRENCYCODE: Currency Code");
        f.setQueryable();
        
        f = headblk.addField("CURRENCY_RATE");
        f.setLabel("PCMWCSSPOSTINGCURRENCYRATE: Currency Rate");
        f.setQueryable();
        f.setDefaultNotVisible();

        f = headblk.addField("DATE_OF_ORIGINE","Datetime");
        f.setLabel("PCMWCSSPOSTINGDATEOFORIGIN: Date Created");
        f.setQueryable();

        f = headblk.addField("ACCOUNTING_YEAR","Number");
        f.setLabel("PCMWCSSPOSTINGACCYEAR: Accounting Year");
        f.setDefaultNotVisible();
        f.setQueryable();

        f = headblk.addField("ACCOUNTING_PERIOD","Number");
        f.setLabel("PCMWCSSPOSTINGACCPERIOD: Accounting Period");
        f.setDynamicLOV("ACCOUNTING_PERIOD","COMPANY,ACCOUNTING_YEAR");
        f.setQueryable();
        f.setDefaultNotVisible();

        f = headblk.addField("VOUCHER_NO","Number");
        f.setLabel("PCMWCSSPOSTINGVOUCHERNO: Voucher No");
        f.setDynamicLOV("VOUCHER","COMPANY,VOUCHER_TYPE");
        f.setQueryable();

        f = headblk.addField("VOUCHER_TYPE");
        f.setLabel("PCMWCSSPOSTINGVOUCHERTYPE: Voucher Type");
        f.setUpperCase();
        f.setQueryable();
        
        f = headblk.addField("VOUCHER_DATE","Datetime");
        f.setLabel("PCMWCSSPOSTINGVOUCHERDATE: Voucher Date");
        f.setQueryable();
        
        //Hidden fields
        f = headblk.addField("CSS_ROW_NO","Number");
        f.setLabel("PCMWCSSPOSTINGCSSROWNO: Css Row No");
        f.setHidden();

        f = headblk.addField("ACCOUNT_ROW_NO","Number");
        f.setLabel("PCMWCSSPOSTINGACCROWNO: Accounting Row No");
        f.setDynamicLOV("PCM_ACCOUNTING","TRANSACTION_ID");
        f.setHidden();

        f = headblk.addField("WO_TYPE");
        f.setFunction("Active_Work_Order_Api.Get_Work_Order_Type(:WO_NO)");
        f.setHidden(); 

        headblk.setView("CSS_POSTING");
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();
        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("workOrder",mgr.translate("PCMWCSSPOSTINGRMBWO: Work Order..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        //Get User Default Company
        getDefaultCompany();

        // Set the Field Labels for code parts.
        setCodePartLabels();
        
        if (headset.countRows()>0)
        {
            if (headlay.isSingleLayout() && !(mgr.isEmpty(headset.getRow().getValue("TRANSACTION_ID"))))
                mgr.getASPField("TRANSACTION_ID").setHyperlink("WoTimeTransactionHist.page","SEARCHME,TRANSACTION_ID,COST_TYPE","NEWWIN");
            else if (headlay.isMultirowLayout())
                mgr.getASPField("TRANSACTION_ID").setHyperlink("WoTimeTransactionHist.page","SEARCHME,TRANSACTION_ID,COST_TYPE","NEWWIN");
        }
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        ASPManager mgr = getASPManager();
        return (sCompany+ " - " + mgr.translate("PCMWCSSPOSTINGSTITLE: Query - CSS Postings"));
    }

    protected String getTitle()
    {
        ASPManager mgr = getASPManager();
        return (sCompany+ " - " + mgr.translate("PCMWCSSPOSTINGSTITLE: Query - CSS Postings"));
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        appendDirtyJavaScript("window.name = \"CssPosting\"\n");

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
    }
}
