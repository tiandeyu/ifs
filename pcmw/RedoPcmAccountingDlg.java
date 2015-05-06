/*
 *                 IFS Research & Development
 *
 * This program is protected by copyright law and by international
 * conventions. All licensing, renting, lending or copying (including
 * for private use), and all other use of the program, which is not
 * expressively permitted by IFS Research & Development (IFS), is a
 * violation of the rights of IFS. Such violations will be reported to the
 * appropriate authorities.
 *
 * VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 * TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 * File        : RedoPcmAccountingDlg.java
 * Description : Redo Maintenance Accountings.
 * Notes       :
 * ----------------------------------------------------------------------------
 *
 * Created     : NIJALK   Created(Relate to spec AMAD112 - Overhead and Other Rates). 
 * Modified    : AMNILK   070727  Eliminated XSS Security Vulnerability.
 *
 * ----------------------------------------------------------------------------
 */


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class RedoPcmAccountingDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.RedoPcmAccountingDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPBlockLayout headlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================

    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPBuffer buf;
    private ASPContext ctx;
    
    private String val;
    private boolean enOK;

    //===============================================================
    // Construction 
    //===============================================================
    public RedoPcmAccountingDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run()
    {
        ASPManager mgr = getASPManager();
        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        enOK = ctx.readFlag("ENOK",false);

        if (!mgr.isEmpty(mgr.getQueryStringValue("OKFIND")))
            okFind();
        else if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (mgr.buttonPressed("SUBMIT"))
            submit();
        else if ( mgr.buttonPressed("CANCEL") )
            cancel();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();

        checkObjAvailable();

        ctx.writeFlag("ENOK",enOK);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();
        ASPQuery q;

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if ( headset.countRows() == 0 )
        {
            mgr.setStatusLine("PCMWREDOMAINTACCOUNTDLGNODATA: No data found.");
            headset.clear();
        }
        else
            mgr.createSearchURL(headblk);
    }

    public void  submit()
    {
        ASPManager mgr = getASPManager();

        if (mgr.isEmpty(mgr.readValue("CONTRACT")))
        {
            mgr.showAlert("PCMWREDOMAINTACCOUNTDLGSITENOTNULL: Site field should have a value.");
        }
        else
        {
            trans.clear();
            cmd = trans.addCustomCommand("MM2","Pcm_Accounting_API.Redo_Error_Bookings");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            trans = mgr.perform(trans);

            mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
        }
    }

    public void  cancel()
    {
        ASPManager mgr = getASPManager();
        mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
    }

    public void checkObjAvailable()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer availObj;

        trans.clear();
        trans.addSecurityQuery("Pcm_Accounting_API","Redo_Error_Bookings");
        trans = mgr.perform(trans);

        availObj = trans.getSecurityInfo();

        if (availObj.itemExists("Pcm_Accounting_API.Redo_Error_Bookings"))
            enOK = true;
    }

    public void validate()
    {
        ASPManager mgr = getASPManager();
        val = mgr.readValue("VALIDATE");
        trans.clear();

        if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("DESC","Site_API.Get_Description","CONTRACT_DESC");
            cmd.addParameter("CONTRACT");          
            trans = mgr.validate(trans);

            String desc = trans.getValue("DESC/DATA/CONTRACT_DESC");
            String txt = (mgr.isEmpty(desc) ? "" : desc)+ "^";

            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("CONTRACT").
        setFunction("''").
        setSize(5).
        setLabel("PCMWREDOMAINTACCOUNTDLGDLGCONTRACT: Site").
        setDynamicLOV("USER_ALLOWED_SITE_LOV").
        setLOVProperty("TITLE",mgr.translate("PCMWREDOMAINTACCOUNTDLGSITES: Sites")).
        setCustomValidation("CONTRACT","CONTRACT_DESC").
        setUpperCase();

        headblk.addField("CONTRACT_DESC").
        setFunction("Site_API.Get_Description(:CONTRACT)").
        setLabel("PCMWREDOMAINTACCOUNTDLGSITEDESC: Site Description").
        setReadOnly();

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setDialogColumns(1);

        headlay.setSimple("CONTRACT_DESC");
    }

    protected String getDescription()
    {
        ASPManager mgr = getASPManager();

        return mgr.translate("PCMWREDOMAINTACCOUNTDLGTITLE1: Rerun Invalid Accountings");
    }

    protected String getTitle()
    {
        ASPManager mgr = getASPManager();

        return mgr.translate("PCMWREDOMAINTACCOUNTDLGTITLE1: Rerun Invalid Accountings");
    }

    protected void printContents() throws FndException
    { 
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.generateDialog());		//XSS_Safe AMNILK 20070727

        printNewLine();
        printSpaces(1);
        printSubmitButton("SUBMIT",mgr.translate("PCMWREDOMAINTACCOUNTDLGOKBUTTON:    OK    "), "");
        printSpaces(1);
        printSubmitButton("CANCEL",mgr.translate("PCMWREDOMAINTACCOUNTDLGCANCELBUTTON:  Cancel "), "");

        appendDirtyJavaScript("function validateContract(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkContract(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('CONTRACT',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CONTRACT_DESC',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CONTRACT'\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT',i,'Site') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('CONTRACT_DESC',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");  

        if (!enOK)
            appendDirtyJavaScript("f.SUBMIT.disabled = true;\n");
        else
            appendDirtyJavaScript("f.SUBMIT.disabled = false;\n"); 
    }
}
