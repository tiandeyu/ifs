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
*  File        : CopyPmActionsDlg.java 
*  Modified    :
    SHFELK  2001-Feb-14  - Created.
    SAPRLK  2003-Dec-22  - Web Alignment - removed methods clone() and doReset().
    NAMELK  2004-Nov-05  - Non Standard Translation Tags Corrected. 
    NIJALK  060221   Renamed description and Title to "Copy PM Actions between Agreements". 
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CopyPmActionsDlg extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CopyPmActionsDlg");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPQuery q;
    private ASPCommand cmd;

    //===============================================================
    // Construction 
    //===============================================================
    public CopyPmActionsDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }


    public void run() 
    {
        ASPManager mgr = getASPManager();


        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();


        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();

    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWCOPYPMACTIONSDLGNODATA: No data found."));
            headset.clear();
        }
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void  ok()
    {
        String oldagree = null;
        String newagree = null;
        ASPManager mgr = getASPManager();

        oldagree = mgr.readValue("OLDAGREEMENT","");
        newagree = mgr.readValue("NEWAGREEMENT","");

        if (!(( "".equals(oldagree) ) ||  ( "".equals(newagree) )))
        {
            cmd = trans.addCustomCommand("COPYACT","Pm_Action_Util_API.Copy_Service_Agreement__");
            cmd.addParameter("OLDAGREEMENT");
            cmd.addParameter("NEWAGREEMENT");
            trans=mgr.perform(trans);
        }
        headtbl.clearQueryRow();
        headset.setFilterOff();
        headset.unselectRows();
        mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"Default.page");       
    }

    public void  cancel()
    {
        ASPManager mgr = getASPManager();

        mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"Default.page");       
        headtbl.clearQueryRow();
        headset.setFilterOff();
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        mgr.endResponse();
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden().
        setFunction("''");

        headblk.addField("OBJVERSION").
        setFunction("''").
        setHidden();

        headblk.addField("OLDAGREEMENT").
        setSize(12).
        setDynamicLOV("CUSTOMER_AGREEMENT",600,445).
        setLOVProperty("TITLE", mgr.translate("PCMWCOPYPMACTIONSDLGOLDAG: List of Agreement ID")).
        setMandatory().
        setLabel("PCMWCOPYPMACTIONSDLGOLDAGR: Agreement ID").
        setUpperCase().
        setFunction("''").
        setMaxLength(10);

        headblk.addField("NEWAGREEMENT").
        setSize(12).
        setDynamicLOV("CUSTOMER_AGREEMENT",600,445).
        setLOVProperty("TITLE", mgr.translate("PCMWCOPYPMACTIONSDLGNEAGID: List of New Agreement ID")).
        setMandatory().
        setLabel("PCMWCOPYPMACTIONSDLGNEWAGR: New  Agreement ID").
        setUpperCase().
        setFunction("''").
        setMaxLength(10);

        headblk.setView("DUAL");
        headblk.defineCommand("","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELEDIT,"cancel");

        headlay = headblk.getASPBlockLayout();
        headlay.setDialogColumns(1);
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        headlay.defineGroup(mgr.translate("PCMWCOPYPMACTIONSDLGNEWPMACT: New PM Actions"),"OLDAGREEMENT,NEWAGREEMENT",true,true);

        headtbl = mgr.newASPTable(headblk);

    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCOPYPMACTIONSDLGPMACTBTWCUSTAGR: Copy PM Actions between Agreements";
    }

    protected String getTitle()
    {
        return "PCMWCOPYPMACTIONSDLGPMACTBTWCUSTAGR: Copy PM Actions between Agreements";
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
