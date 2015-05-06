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
 * File        : TransferCssTransactionsDlg.java
 * Description : Transfer CSS Transactions.
 * Notes       :
 * ----------------------------------------------------------------------------
 *
 * Created     :051209   NIJALK   Created(Relate to spec AMAD112 - Cost of Sold Services). 
 * Modified    :
 * 060309   NIJALK   Bug 136811: Modified preDefine().
 *  AMNILK  070727  Eliminated SQLInjections Security Vulnerability.
 * ----------------------------------------------------------------------------
 */


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class TransferCssTransactionsDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.TransferCssTransactionsDlg");

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

    //===============================================================
    // Construction 
    //===============================================================
    public TransferCssTransactionsDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run()
    {
        ASPManager mgr = getASPManager();
        trans = mgr.newASPTransactionBuffer();

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
        else if (mgr.buttonPressed("DEFAULTS"))
            get_contract();
        else
            okFind();
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void  get_contract()
    {
        /* Used to get the user's default contract*/

        ASPManager mgr = getASPManager();
        String contract;

        trans.clear();
        cmd = trans.addCustomCommand("CONT","USER_DEFAULT_API.GET_USER_CONTRACT");
        cmd.addParameter("CONTRACT");
        trans = mgr.perform(trans);

        contract = trans.getValue("CONT/DATA/CONTRACT");

        headset.setValue("CONTRACT",contract);
    }

    public void  okFind()
    {
        ASPManager mgr = getASPManager();
        ASPQuery q;

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if ( headset.countRows() == 0 )
        {
            mgr.setStatusLine("PCMWTRANSFERMAINTTRANSACTIONDLGNODATA: No data found.");
            headset.clear();
        }
        else
            mgr.createSearchURL(headblk);

        get_contract();
    }

    public void  submit()
    {
        ASPManager mgr = getASPManager();
        String todate = mgr.readValue("DATETO");

        if (mgr.isEmpty(todate))
        {
            mgr.showAlert("Enter data to the date field");
        }
        else
        {
            cmd = trans.addCustomCommand("MM2","CSS_POSTING_API.TRANSFER_TO_FINANCE");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("DATETO",mgr.readValue("DATETO"));
            cmd.addParameter("OFFSET","0");
            trans = mgr.perform(trans);

            mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
        }
    }

    public void  cancel()
    {
        ASPManager mgr = getASPManager();
        mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("DATETO", "Date").
        setFunction("SYSDATE").
        setSize(18).
        setLabel("PCMWTRANSFERCSSTRANSACTIONDLGDATETO: To Date").
        setMandatory();

        headblk.addField("CONTRACT").
        setFunction("''").
        setSize(18).
        setDynamicLOV("USER_ALLOWED_SITE_LOV").
        setLOVProperty("TITLE",mgr.translate("PCMWTRANSFERCSSTRANSACTIONDLGSITES: Sites")).
        setLabel("PCMWTRANSFERCSSTRANSACTIONDLGCONTRACT: Site").
        setUpperCase();

        headblk.addField("OFFSET").
        setFunction("''").
        setHidden();

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setDialogColumns(1);  

        enableConvertGettoPost();
    }

    protected String getDescription()
    {
        ASPManager mgr = getASPManager();

        return mgr.translate("PCMWTRANSFERCSSTRANSACTIONDLGTITLE1: Transfer CSS Transactions");
    }

    protected String getTitle()
    {
        ASPManager mgr = getASPManager();

        return mgr.translate("PCMWTRANSFERCSSTRANSACTIONDLGTITLE1: Transfer CSS Transactions");
    }

    protected void printContents() throws FndException
    { 
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.generateDialog());		//XSS_Safe AMNILK 20070726

        printNewLine();
        printSpaces(1);
        printSubmitButton("SUBMIT",mgr.translate("PCMWTRANSFERCSSTRANSACTIONDLGOKBUTTON:    OK    "), "");
        printSpaces(1);
        printSubmitButton("CANCEL",mgr.translate("PCMWTRANSFERCSSTRANSACTIONDLGCANCELBUTTON:  Cancel "), "");
        printSpaces(1);
        printSubmitButton("DEFAULTS",mgr.translate("PCMWTRANSFERCSSTRANSACTIONDLGDEFAULTSBUTTON:  Get Defaults "), "");
    }
}
