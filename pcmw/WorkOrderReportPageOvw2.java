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
*  File        : WorkOrderReportPageOvw2.java 
*  Modified    :
*    ASP2JAVA Tool  2001-04-08  - Created Using the ASP file WorkOrderReportPageOvw2.asp
*    JEWILK         2001-04-08  - Corrected conversion errors and done necessary changes.
*    GACOLK         2002-12-04  - Set Max Length of MCH_CODE to 100
*    JEWILK         2003-11-10  - Changed the display format mask of WO No. Call 110346.
*    SAPRLK         2004-01-05  - Web Alignment - removed methods clone() and doReset().
*    VAGULK         2004-01-29  - Web Alignment - arranged the field order accordingly.
*    SAPRLK         2004-02-12  - Web Alignment - change of conditional code in validate method, 
*                                 remove unnecessary method calls for hidden fields
*    AMDILK         2004-08-17  - Bug 58216, Eliminated SQL errors in web applications. 
*                                 Modified the methods okFind(), okFindITEM0(), okFindITEM0Alert(), countFindITEM0()
*    AMDILK         2004-09-06  - Merged with the Bug Id 58216
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkOrderReportPageOvw2 extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderReportPageOvw2");


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
    private ASPBlock itemblk0;
    private ASPRowSet itemset0;
    private ASPCommandBar itembar0;
    private ASPTable itemtbl0;
    private ASPBlockLayout itemlay0;
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String sWoNo;
    private String callingUrl;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderReportPageOvw2(ASPManager mgr, String page_path)
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
        sWoNo = ctx.readValue("CTXWONO","2");
        callingUrl = ctx.getGlobal("CALLING_URL");

        if ( mgr.commandBarActivated() )
            eval(mgr.commandBarFunction());

        else if ( mgr.dataTransfered() )
            okFind();

        else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
            validate();

        else if ( !mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) )
        {
            sWoNo = mgr.readValue("WO_NO");
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
            okFindITEM0();
        }

        else if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
            okFind();

        ctx.writeValue("CTXWONO",sWoNo);
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
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", sWoNo);

        if (mgr.dataTransfered())
                q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");

        mgr.submit(trans);

        if ( headset.countRows() == 0 )
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERREPORTPAGEOVW2NODATAFOUND: No data found."));
            headset.clear();
        }
    }


    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", sWoNo);
        q.includeMeta("ALL");

        mgr.submit(trans);
    }


    public void  okFindITEM0Alert()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", sWoNo);
        q.includeMeta("ALL");

        mgr.submit(trans);

        if ( itemset0.countRows() == 0 )
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERREPORTPAGEOVW2NODATAFOUND: No data found."));
            itemset0.clear();
        }
    }


    public void  countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", sWoNo);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        String n = itemset0.getRow().getValue("N");
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
    }


    public void  newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        ASPCommand cmd = trans.addEmptyCommand("ITEM0","WORK_ORDER_REPORT_PAGE_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM0/DATA");
        data.setValue("WO_NO", sWoNo);
        itemset0.addRow(data);
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        mgr.beginASPEvent();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("HEAD_WO_NO","Number","#");
        f.setDbName("WO_NO");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2HEADWONO: WO No");
        f.setReadOnly();
        f.setMaxLength(8);

	f = headblk.addField("CONTRACT");
        f.setSize(5);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2CONTRACT: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("MCH_CODE");
        f.setSize(13);
        f.setMaxLength(100);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2MCH_CODE: Object ID");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("DESCRIPTION");
        f.setSize(28);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2DESCRIPTION: Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setMaxLength(2000);

        f = headblk.addField("STATE");
        f.setSize(11);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2STATE: Status");
        f.setReadOnly();
        f.setMaxLength(30);

        headblk.setView("ACTIVE_SEPARATE");

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.FIND);
        headbar.disableCommand(headbar.BACK);

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWWORKORDERREPORTPAGEOVW2HD: Free Notes"));
        headtbl.setWrap();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("OBJID");
        f.setHidden();

        f = itemblk0.addField("OBJVERSION");
        f.setHidden();

        f = itemblk0.addField("PAGE_NO","Number","#");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2PAGE_NO: Page No");
        f.setReadOnly();
        f.setInsertable();

        f = itemblk0.addField("TITLE");
        f.setSize(25);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2TITLE: Title");
        f.setMaxLength(25);

        f = itemblk0.addField("NOTE");
        f.setSize(50);
        f.setLabel("PCMWWORKORDERREPORTPAGEOVW2NOTE: Note");
        f.setMaxLength(2000);
        f.setHeight(6);

        f = itemblk0.addField("WO_NO","Number","#");
        f.setHidden();

        itemblk0.setView("WORK_ORDER_REPORT_PAGE");
        itemblk0.defineCommand("WORK_ORDER_REPORT_PAGE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0Alert");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.enableCommand(itembar0.FIND);

        itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWWORKORDERREPORTPAGEOVW2ITM0: Work Order Report Page"));
        itemtbl0.setWrap();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDERREPORTPAGEOVW2FREENOTES: Free Notes";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDERREPORTPAGEOVW2FREENOTES: Free Notes";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();
        appendToHTML(headbar.showBar());
        appendToHTML("<table cellpadding=0 width=");
        appendToHTML(String.valueOf(frm.getFormWidth()));
        appendToHTML(" border=\"0\">\n");
        appendToHTML("<tr>\n");
        appendToHTML("<td>\n");
        appendToHTML(headlay.generateDataPresentation());
        appendToHTML("</td>			\n");
        appendToHTML("</tr>	\n");
        appendToHTML("</table>	\n");
        if ( itemlay0.isVisible() )
        {
            appendToHTML(itemlay0.show());
        }
        appendToHTML("<br>\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
    }

}
