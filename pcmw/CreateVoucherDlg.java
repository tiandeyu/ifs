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
*  File        : CreateVoucherDlg.java 
*  Modified    :
*    ARWILK  2001-03-01  - Created.
*    JEWILK  2003-09-25  - Corrected overridden javascript function toggleStyleDisplay(). 
*    SAPRLK  2003-12-22  - Web Alignment - removed methods clone() and doReset().
*    NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*    SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*    AMDILK  070330  Merged bug id 64068
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CreateVoucherDlg extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateVoucherDlg");


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
    private String language;
    private String dateformat;
    private int lenofdate;

    //===============================================================
    // Construction 
    //===============================================================
    public CreateVoucherDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();
        dateformat = ctx.readValue("CTX1",dateformat);
        language = mgr.getConfigParameter("APPLICATION/LANGUAGE");
        dateformat = mgr.getFormatMask("Date",true);   

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else
            clear();

        okFind();
        getDefaultValues();

        ctx.writeValue("CTX1",dateformat);

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String val;

        val = mgr.readValue("VALIDATE");
        mgr.showError("VALIDATE not implemented");
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  cancel()
    {
        ASPManager mgr = getASPManager();

        mgr.redirectTo("../Default.page");
        headtbl.clearQueryRow();
        headset.setFilterOff();
    }


    public void  submit()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;

        int isCreated;
        String contract;
        String bookdate;
        String todate;

        trans.clear();

        isCreated = 0;

        contract = mgr.readValue("CONTRACT");
        bookdate = mgr.readValue("BOOKDATE");
        todate =mgr.readValue("TODATE");   

        if (!(( mgr.isEmpty(contract)  ||  ( " ".equals(contract) )) ||  (( mgr.isEmpty(bookdate) ) ||  ( " ".equals(bookdate) ))  ||  (( mgr.isEmpty(todate) ) ||  ( " ".equals(todate) ))))
        {
            isCreated = 1;

            cmd = trans.addCustomCommand("VALUE","Work_Order_Coding_Trans_API.Booking_Wo");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("BOOKDATE");
            cmd.addParameter("TODATE");
            cmd.addParameter("LANGUAGE",language);

            mgr.perform(trans);
        }

        if (isCreated == 0)
            mgr.showAlert(mgr.translate("PCMWCREATEVOUCHERDLGVOUCR: No Voucher has been created"));

        headtbl.clearQueryRow();
        headset.clear();
        mgr.redirectTo("../Default.page");

    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  clear()
    {
        headset.clear();
        headtbl.clearQueryRow();
    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addEmptyQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWCREATEVOUCHERDLGNODATA: No data found."));
            headset.clear();
        }
    }


    public void  getDefaultValues()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;

        String contract;   
        String todate;
        String strTodate;
        ASPBuffer row;

        trans.clear();  
        cmd = trans.addCustomFunction("DEFCON","User_Default_API.Get_Contract","CONTRACT");
        ASPQuery q = trans.addQuery("MASTER1","DUAL","SYSDATE","","");

        trans = mgr.perform(trans);

        contract = trans.getValue("DEFCON/DATA/CONTRACT");
        strTodate = trans.getValue("MASTER1/DATA/SYSDATE");
        lenofdate = dateformat.length() ;
        todate = strTodate.substring(0,lenofdate);

        row = headset.getRow();
        row.setValue("CONTRACT",contract);
        row.setValue("TODATE",strTodate);
        headset.setRow(row);
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden().
        setFunction("''");

        headblk.addField("OBJVERSION").
        setHidden().
        setFunction("''");

        headblk.addField("CONTRACT").
        setSize(30).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setMandatory().
        setLabel("PCMWCREATEVOUCHERDLGCONTRACT: Site").
        setUpperCase().
        setFunction("''");

        headblk.addField("BOOKDATE", "Date").
        setSize(30).
        setMandatory().
        setLabel("PCMWCREATEVOUCHERDLGBOOKDATE: Voucher Date").
        setFunction("''");


        headblk.addField("TODATE", "Date").
        setSize(30).
        setMandatory().
        setLabel("PCMWCREATEVOUCHERDLGTODATE: Until Date").
        setFunction("''");

        headblk.addField("LANGUAGE").
        setHidden().
        setFunction("''");

        headblk.setView("DUAL");
        headblk.defineCommand("","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();
        headbar = headblk.newASPCommandBar();

        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELEDIT,"cancel");

        headtbl = mgr.newASPTable(headblk);
        headlay = headblk.getASPBlockLayout();
        headlay.setDialogColumns(1);
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        headlay.defineGroup(mgr.translate("PCMWCREATEVOUCHERDLGGRPLABEL1: Input Data for Voucher"),"CONTRACT,BOOKDATE,TODATE",true,true);
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCREATEVOUCHERDLGTITLE: Create Voucher";
    }

    protected String getTitle()
    {
        return "PCMWCREATEVOUCHERDLGTITLE: Create Voucher";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML("<table border=\"0\" width= '74%'>\n");
        appendToHTML(" <tr>\n");
        appendToHTML("  <td>\n");
        appendToHTML(headbar.showBar());
        appendToHTML("   <table id=\"cntITEM0\" border=\"0\">\n");
        appendToHTML("    <tr>\n");
        appendToHTML("    <td>\n");
        appendToHTML(fmt.drawReadLabel("PCMWCREATEVOUCHERDLGINFOTEXT1: This will create vouchers and send them to Accounting Rules. Use Until Date to limit the time interval for selected postings."));
        appendToHTML("</td></tr><tr><td>\n");
        appendToHTML(fmt.drawReadLabel("PCMWCREATEVOUCHERDLGINFOTEXT2: All authorized postings on work orders created before Until Date will be sent."));
        appendToHTML("</td></tr>\n");
        appendToHTML("    </table>\n");
        appendToHTML(headlay.generateDataPresentation());
        appendToHTML("  </tr>\n");
        appendToHTML(" </td>\n");
        appendToHTML("</table> \n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  el = document.getElementById(el);\n");
        appendDirtyJavaScript("  if(el.style.display!='none')\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("    el.style.display='none';\n");
        appendDirtyJavaScript("    cntITEM0.style.display='none';\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("    el.style.display='block';\n");
        appendDirtyJavaScript("    cntITEM0.style.display='block';\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("}\n");
    }

}
