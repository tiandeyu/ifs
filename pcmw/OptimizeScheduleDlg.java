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
*  File        : OptimizeScheduleDlg.java 
*  Created     : NIJALK  080820  Created
*  Modified    :
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class OptimizeScheduleDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.OptimizeScheduleDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    
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

    private ASPBuffer transferedDataBuffer;
    private ASPTransactionBuffer trans;
    private String sOptOpr;
    private String sOptJobs;
    private String sOptTools;
    private String sInfo;
    private String sOprSelected;
    private String sJobsSelected;
    private String sToolsSelected;
    private boolean sOptOprReadOnly;
    private boolean sOptJobsReadOnly;
    private boolean sOptToolsReadOnly;
    private boolean returnFlag;

    //===============================================================
    // Construction 
    //===============================================================
    public OptimizeScheduleDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        transferedDataBuffer = ctx.readBuffer("CTXTRANSBUFF");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.buttonPressed("OK"))
            submit();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
        {
            transferedDataBuffer = mgr.getTransferedData();
        }

        adjust();

        ctx.writeBuffer("CTXTRANSBUFF", transferedDataBuffer);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        mgr.showError("VALIDATE not implemented");
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void submit()
    {
        ASPManager mgr = getASPManager();

        int nItems = transferedDataBuffer.countItems();
        int i = 0;

        sOprSelected = "OPTOPR".equals(mgr.readValue("OPTIONS"))? "1" : "0";
        sJobsSelected = "OPTJOBS".equals(mgr.readValue("OPTIONS"))? "1" : "0";
        sToolsSelected = "OPTTOOLS".equals(mgr.readValue("OPTIONS"))? "1" : "0";

        while (i < nItems)
        {
            String sWoNo = transferedDataBuffer.getBufferAt(i).getValueAt(0);

            trans.clear();

            ASPCommand cmd = trans.addCustomCommand("OPTIMIZEWO","Active_Work_Order_API.Optimize_Planning_Schedule");
            cmd.addParameter("INFO","");
            cmd.addParameter("DUMMY", sWoNo);
            cmd.addParameter("DUMMY", sOprSelected);
            cmd.addParameter("DUMMY", sJobsSelected);
            cmd.addParameter("DUMMY", sToolsSelected);

            trans = mgr.perform(trans);

            sInfo = trans.getValue("OPTIMIZEWO/DATA/INFO");
            if (!mgr.isEmpty(sInfo))
                sInfo = sInfo.substring(5);

            i = i+1;
        }

        returnFlag = true;
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("DUMMY");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("INFO");
        f.setFunction("''");
        f.setHidden();

        headblk.setView("DUAL");
        headblk.setTitle("OPTPLANSCHGROUP: Optimize Start Dates/Times");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableMinimize();

        headbar.disableCommand(headbar.FORWARD);
        headbar.disableCommand(headbar.BACKWARD);
        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);

        enableConvertGettoPost();
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();
        sOptOprReadOnly = false;
        sOptJobsReadOnly = false;
        sOptToolsReadOnly = false;
        sOptOpr = "0";
        sOptJobs = "0";
        sOptTools = "0";

        int nItems = transferedDataBuffer.countItems();

        if (nItems > 1)
        {
            sOptOprReadOnly = true;
            sOptJobsReadOnly = true;
        }
        else
        {
            ASPBuffer buff = mgr.newASPBuffer();
            ASPCommand cmd;
            String sWoNo = transferedDataBuffer.getBufferAt(0).getValueAt(0);

            trans.clear();

            cmd = trans.addCustomFunction("HASROLE","Active_Separate_API.Has_Role","DUMMY");
            cmd.addParameter("DUMMY",sWoNo);

            cmd = trans.addCustomFunction("HASJOB","Active_Work_Order_API.Has_Job","DUMMY");
            cmd.addParameter("DUMMY",sWoNo);

            cmd = trans.addCustomFunction("HASTOOLS","Active_Separate_API.Has_Facility","DUMMY");
            cmd.addParameter("DUMMY",sWoNo);

            trans = mgr.perform(trans);

            String sHasRole  = trans.getValue("HASROLE/DATA/DUMMY");
            String sHasJob   = trans.getValue("HASJOB/DATA/DUMMY");
            String sHasTools = trans.getValue("HASTOOLS/DATA/DUMMY");

            if ("FALSE".equals(sHasRole) || ("TRUE".equals(sHasRole) && "TRUE".equals(sHasTools)))
                sOptOprReadOnly = true;

            if ("FALSE".equals(sHasJob))
                sOptJobsReadOnly = true;

            if ("FALSE".equals(sHasTools))
                sOptToolsReadOnly = true;
        }

        if (!sOptOprReadOnly)
            sOptOpr = "1";
        else if (!sOptJobsReadOnly)
            sOptJobs = "1";
        else if (!sOptToolsReadOnly)
            sOptTools = "1";
        else
            sOptOpr = "1";
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "OPTPLANSCHTITLE: Optimize Planning Schedule";
    }

    protected String getTitle()
    {
        return "OPTPLANSCHTITLE: Optimize Planning Schedule";
    }

    public String drawRadio(String label, String name, String value, boolean checked, String tag)
    {
        ASPManager mgr = getASPManager();
        String usage_id ="";
        int i = 0;

        if (!mgr.isEmpty(label))
            i = label.indexOf(":");

        if (i>0)
        {
            String tr_constant = label.substring(0,i);         
            usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
        }

        return "<input class=radioButton type=radio name=\"" + name + "\" value=\"" + value + "\"" + (checked ? " CHECKED " : "") + " " + (tag == null ? "" : tag) + ">&nbsp;<span OnClick=\"showHelpTag('"+usage_id+"')\"><font class=normalTextValue>" + mgr.translate(label) + "</font></span>";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headbar.showBar());
        appendToHTML("<table id=\"SND\" border=\"0\">\n");
        appendToHTML("<tr><td>");
        appendToHTML("</td></tr>");
        appendToHTML("<tr><td>");
        appendToHTML(drawRadio("OPTPLANSCHOPTOPR: Optimize though fixed Operations", "OPTIONS", "OPTOPR", true, ""));
        appendToHTML("</td></tr>");
        appendToHTML("<tr><td>");
        appendToHTML(drawRadio("OPTPLANSCHOPTJOBS: Optimize through fixed Jobs", "OPTIONS", "OPTJOBS", false, ""));
        appendToHTML("</td></tr>");
        appendToHTML("<tr><td>");
        appendToHTML(drawRadio("OPTPLANSCHOPTTOOLS: Optimize through fixed Operations and T&F", "OPTIONS", "OPTTOOLS", false, ""));
        appendToHTML("</td></tr>");
        appendToHTML("<tr>\n");
        appendToHTML("<td><br>\n");
        appendToHTML(fmt.drawSubmit("OK",mgr.translate("OPTPLANSCHOK:  OK "),"OK"));
        appendToHTML("&nbsp;&nbsp;\n");
        appendToHTML(fmt.drawButton("CANCEL",mgr.translate("OPTPLANSCHCANCEL: Cancel"),"onClick='window.close();'"));
        appendToHTML("</td>\n");
        appendToHTML("</tr>\n");
        appendToHTML("</table>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"optimizeSchedule\"\n");

        //Enable/Disable radio buttons
        if (sOptOprReadOnly)
            appendDirtyJavaScript("document.form.OPTIONS[0].disabled = true;\n");

        if (sOptJobsReadOnly)
            appendDirtyJavaScript("document.form.OPTIONS[1].disabled = true;\n");

        if (sOptToolsReadOnly)
            appendDirtyJavaScript("document.form.OPTIONS[2].disabled = true;\n");

        if (returnFlag)
        {
            if ("1".equals(sOprSelected))
                appendDirtyJavaScript("document.form.OPTIONS[0].checked = \"checked\";\n");

            if ("1".equals(sJobsSelected))
                appendDirtyJavaScript("document.form.OPTIONS[1].checked = \"checked\";\n");

            if ("1".equals(sToolsSelected))
                appendDirtyJavaScript("document.form.OPTIONS[2].checked = \"checked\";\n");

            if (!mgr.isEmpty(sInfo))
            {
                appendDirtyJavaScript("alert('"+sInfo+"');");
            }

            appendDirtyJavaScript("opener.commandSet('HEAD.refresh','');");
            appendDirtyJavaScript("self.close();\n");
        }
        else
        {
            if ("1".equals(sOptOpr))
                appendDirtyJavaScript("document.form.OPTIONS[0].checked = \"checked\";\n");

            if ("1".equals(sOptJobs))
                appendDirtyJavaScript("document.form.OPTIONS[1].checked = \"checked\";\n");

            if ("1".equals(sOptTools))
                appendDirtyJavaScript("document.form.OPTIONS[2].checked = \"checked\";\n");
        }

    }
}
