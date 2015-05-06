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
*  File        : ConnectTnFDlg.java
* ---------------------------------------------------------------------------- 
*  050318  JAPALK  Change the Dlg title.
*  050214  NIJALK  Created.
*  060818  JEWILK  Bug 58216, Eliminated SQL Injection security vulnerability.
*  060906  NAMELK  Merged Bug 58216.
*  070710  ILSOLK    Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ConnectTnFDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ConnectTnFDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================

    private ASPContext ctx;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPBlockLayout headlay;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPField f;
    private ASPHTMLFormatter fmt;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String wo_no;

    private boolean win_cls;
    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPQuery q;

    //===============================================================
    // Construction 
    //===============================================================
    public ConnectTnFDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        wo_no = "";

        ASPManager mgr = getASPManager();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();

        wo_no = ctx.readValue("WO_NO","");
        win_cls = ctx.readFlag("WINCLS",win_cls);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTXT")) && !mgr.isEmpty(mgr.getQueryStringValue("CONURL")) && !mgr.isEmpty(mgr.getQueryStringValue("CONWIN")))
        {
            wo_no = mgr.readValue("WO_NO");
            ctx.setGlobal("CONTXT",mgr.readValue("CONTXT"));
            ctx.setGlobal("CONURL",mgr.readValue("CONURL"));
            ctx.setGlobal("CONWIN",mgr.readValue("CONWIN"));

            okFind();
        }
        else if (mgr.buttonPressed("OK"))
            ok();

        ctx.writeValue("WO_NO",wo_no);
        ctx.writeFlag("WINCLS",win_cls);
        win_cls = ctx.readFlag("WINCLS",win_cls);
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  submit()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();
        mgr.submit(trans);
        headset.refreshAllRows();
    }

    public void  ok()
    {
        if (headlay.isEditLayout())
            submit();
        win_cls = true;
    }
    
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",wo_no);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWCONNECTTNFDLGNODATA: No data found."));
            headset.clear();
        }
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("WO_NO");
        f.setSize(8);
        f.setLabel("PCMWCONNECTTNFDLGWONO: WO No");
        f.setReadOnly();
        f.setHidden();

        f = headblk.addField("ROW_NO");
        f.setSize(5);
        f.setLabel("PCMWCONNECTTNFDLGROWNO: Row No");
        f.setReadOnly();

        f = headblk.addField("TOOL_FACILITY_ID");
        f.setLabel("PCMWCONNECTTNFDLGTOOLFACILITYID: Tool/Facility ID");
        f.setReadOnly();

        f = headblk.addField("TOOL_FACILITY_DESC");
        f.setLabel("PCMWCONNECTTNFDLGTOOLFACILITYDESC: Description");
        f.setReadOnly();

        f = headblk.addField("CRAFT_LINE_NO");
        f.setSize(8);
        f.setLabel("PCMWCONNECTTNFDLGCRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","WO_NO",600,450);

        f = headblk.addField("ORG_CODE");
        f.setSize(8);
        f.setLabel("PCMWCONNECTTNFDLGORGCODE: Maint.Org.");
        f.setReadOnly();

        f = headblk.addField("CONTRACT");
        f.setSize(5);
        f.setLabel("PCMWCONNECTTNFDLGCONTRACT: Site");
        f.setReadOnly();

        f = headblk.addField("QTY");
        f.setLabel("PCMWCONNECTTNFDLGQTY: Quantity");
        f.setReadOnly();

        f = headblk.addField("PLANNED_HOUR");
        f.setLabel("PCMWCONNECTTNFDLGPLANNEDHOUR: Planned Hours");
        f.setReadOnly();

        f = headblk.addField("FROM_DATE_TIME","Datetime");
        f.setLabel("PCMWCONNECTTNFDLGFROMDATETIME: From Date");
        f.setReadOnly();

        f = headblk.addField("TO_DATE_TIME","Datetime");
        f.setLabel("PCMWCONNECTTNFDLGTODATETIME: To Date");
        f.setReadOnly();

        headblk.setView("WO_TOOL_FACILITY");
        headblk.defineCommand("WORK_ORDER_TOOL_FACILITY_API","Modify__");
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headtbl = mgr.newASPTable(headblk);
        headlay = headblk.getASPBlockLayout();

        headbar.disableCommand(headbar.FIND);
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
        headbar.disableMultirowAction();

        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2); 
    }
    

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCONNECTTNFDLG: Connect Tools and Facilities to Operations";
    }

    protected String getTitle()
    {
        return "PCMWCONNECTTNFDLG: Connect Tools and Facilities to Operations";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());
        appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
        appendToHTML("<tr align=\"right\">\n");
        appendToHTML("<td width=\"100%\" align=\"right\">\n");
        appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWCONNECTTNFDLGOK:     Ok    "),"OK"));
        appendToHTML("&nbsp;&nbsp;");
        appendToHTML("</td>\n</tr>\n</table>\n");

        appendDirtyJavaScript("if (");
        appendDirtyJavaScript(win_cls); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript(")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("setInfoMsg();\n"); 
        appendDirtyJavaScript("self.close();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function setInfoMsg()");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if (");
        appendDirtyJavaScript(!mgr.isEmpty(ctx.findGlobal("CONTXT","")) && !mgr.isEmpty(ctx.findGlobal("CONURL","")) && !mgr.isEmpty(ctx.findGlobal("CONWIN","")));
        appendDirtyJavaScript(")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if (confirm('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(ctx.findGlobal("CONTXT",""))); // XSS_Safe ILSOLK 20070710
        appendDirtyJavaScript("'))\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("window.open(\"");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(ctx.findGlobal("CONURL",""))); // XSS_Safe ILSOLK 20070710
        appendDirtyJavaScript("\", \"");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(ctx.findGlobal("CONWIN",""))); // XSS_Safe ILSOLK 20070710
        appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\");\n");      
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");

    }
}
