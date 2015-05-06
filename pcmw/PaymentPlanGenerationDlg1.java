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
*  File        : PaymentPlanGenerationDlg1.java 
*  Modified    :
*    SHFELK       2001-Feb-14  - Created.
*    JEWI         2001-04-03   - Changed file extensions from .asp to .page
*    ARWILK       2001-10-20   - #(70845) Added the javaScript function lovAgreementId;
*    ARWILK       031222       - Edge Developments - (Removed clone and doReset Methods)
*    ThWilk       040430       - Modified run(),close() and ok() methods.
*    SHAFLK       050107       - Bug 46267, Modified title name.
*    Chanlk       050131       - Merged bug 46267.
*    SHAFLK       050307       - Bug 50031, Modified run() and ok().
*    Chanlk       050131       - Merged bug 46267.
*    SHAFLK       050425       - Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*    NIJALK       050617       - Merged bug 50830. 
*    NIJALK       051006       - Modified ok(). Redirected to Navigator page after clicking "Save & Return", if page is opened from navigator.
*    JAPALK       060420       - Removed User_Allowed_Site_API.Authorized method call. 
*    ILSOLK       060720       - Set Customer_No Max Length as 20.     
*    SHAFLK       070312       - Bug 64068, Removed extra mgr.translate().   
*    AMDILK       070330       - Merged bug id 64068 	 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PaymentPlanGenerationDlg1 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PaymentPlanGenerationDlg1");

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
    private boolean closeWin;
    private String qString;
    private String newWndFlag;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPBuffer temp;

    //===============================================================
    // Construction 
    //===============================================================
    public PaymentPlanGenerationDlg1(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager(); 

        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer(); 
        newWndFlag = ctx.readValue("NEWWINFLAG","false");
        qString = ctx.readValue("QSTRING","");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWWNDFLAG")))
            newWndFlag = "true";
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("PAGE")))
            qString=mgr.getQueryStringValue("PAGE");



        ctx.writeValue("QSTRING",qString);

        startUp();
        ctx.writeValue("NEWWINFLAG",newWndFlag);

    }

    public void  startUp()
    {
        getDefaults();
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();
        String val = null;
        String week = null;
        String dateFrom = null;
        String dataTo = null;
        int len = 0;  
        String ye = null; 
        String we = null;  
        int nye = 0;  
        int nwe = 0;  
        String txt = null;

        val = mgr.readValue("VALIDATE");

        if ("WEEK".equals(val))
        {
            week = mgr.readValue("WEEK","");
            len = week.length();        
            ye = week.substring(0,2);
            we = week.substring(2,4);
            nye =toInt(ye)*1;  
            nwe = toInt(we)*1;

            if (( len != 4 ) ||  ( nwe<1 ) ||  ( nwe>53 ) ||  ( nye<0 ) ||  (( nye == 0 ) &&  ( !("00".equals(ye)) )) ||  ( nye>100 ))
            {
                mgr.responseWrite("No_Data_Found" + "Week "+week+" is not a valid week. The format should be YYWW.");
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomFunction("DATEFROM","Pm_Calendar_API.Get_Date","DTDATEFROM");
                cmd.addParameter("WEEK");
                cmd.addParameter("DAY_NUMBER","1");

                cmd = trans.addCustomFunction("DATETO","Pm_Calendar_API.Get_Date","DTDATETO");
                cmd.addParameter("WEEK");
                cmd.addParameter("DAY_NUMBER","7");

                trans = mgr.perform(trans);

                dateFrom = trans.getBuffer("DATEFROM/DATA").getFieldValue("DTDATEFROM");
                dataTo = trans.getBuffer("DATETO/DATA").getFieldValue("DTDATETO");

                txt = (mgr.isEmpty(dateFrom) ? "" : (dateFrom))+ "^" + (mgr.isEmpty(dataTo) ? "" : (dataTo))+ "^";

                mgr.responseWrite(txt);
            }
        }
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//------------------------  BUTTON FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

    public int getDefaults()
    {
        ASPManager mgr = getASPManager();
        String contract = null;

        cmd = trans.addCustomCommand("CON","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");
        trans = mgr.perform(trans);
        contract=trans.getValue("CON/DATA/CONTRACT");
        headset.addRow(trans.getBuffer("HEAD/DATA"));
        temp = headset.getRow();
        temp.setValue("CONTRACT",trans.getValue("CON/DATA/CONTRACT"));

        headset.setRow(temp);
        return 0;
    }


    public void  ok()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomCommand("GENWO","CUST_AGR_FIXED_PRICE_PLAN_API.Generate_Work_Order__");
        cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
        cmd.addParameter("WEEK",mgr.readValue("WEEK",""));
        cmd.addParameter("DTDATEFROM",mgr.readValue("DTDATEFROM",""));
        cmd.addParameter("DTDATETO",mgr.readValue("DTDATETO",""));
        cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO",""));
        cmd.addParameter("AGREEMENT_ID",mgr.readValue("AGREEMENT_ID",""));

        trans = mgr.perform(trans);
        trans.clear();

        if ( "true".equals(newWndFlag) )
        {
            mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
        }
        else if (qString.equals("FixedPricePlan"))
        {
            closeWin = true;
        }
    }


    public void  cancel()
    {
        ASPManager mgr = getASPManager();

        //mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"Default.page");       
        if (qString.equals("FixedPricePlan"))
            closeWin = true;


    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("CONTRACT");
        f.setSize(9);
        f.setDynamicLOV("SITE",600,445);
        f.setMaxLength(5);
        f.setLabel("PCMWPAYMENTPLANGENERATIONDLG1CONTRACT: Site");
        f.setUpperCase();

        f = headblk.addField("WEEK");
        f.setSize(9);
        f.setLabel("PCMWPAYMENTPLANGENERATIONDLG1WEEK: Week");
        f.setCustomValidation("WEEK","DTDATEFROM,DTDATETO");

        f = headblk.addField("DTDATEFROM", "Date");
        f.setSize(13);
        f.setMandatory();
        f.setLabel("PCMWPAYMENTPLANGENERATIONDLG1DTDATEFROM: Date From");
        f.setFunction("''");

        f = headblk.addField("DTDATETO", "Date");
        f.setSize(13);
        f.setMandatory();
        f.setLabel("PCMWPAYMENTPLANGENERATIONDLG1DTDATETO: Date To");
        f.setFunction("''");

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(9);
        f.setDynamicLOV("CUST_ORD_CUST2",600,445);
        f.setLabel("PCMWPAYMENTPLANGENERATIONDLG1CUSTOMER_NO: Customer No");
        f.setUpperCase();
	f.setMaxLength(20);

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(9);
        f.setDynamicLOV("CUSTOMER_AGREEMENT",600,445);
        f.setLabel("PCMWPAYMENTPLANGENERATIONDLG1AGREEMENT_ID: Agreement Id");
        f.setUpperCase();

        f = headblk.addField("TEMP");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DAY_NUMBER");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("DUAL");
        headblk.defineCommand("","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableModeLabel();
        headbar.disableCommand(headbar.FORWARD);
        headbar.disableCommand(headbar.BACKWARD);
        headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
        headbar.enableCommand(headbar.CANCELEDIT); 
        headbar.defineCommand(headbar.CANCELEDIT,"cancel");

        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.defineGroup(mgr.translate("PCMWPAYMENTPLANGENERATIONDLG1GRPLABEL1: Parameters"),"CONTRACT,WEEK,DTDATEFROM,DTDATETO,CUSTOMER_NO,AGREEMENT_ID",true,true);
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWPAYMENTPLANGENERATIONDLG1CRERELECUST1: Generate WO from Payment plan";
    }

    protected String getTitle()
    {
        return "PCMWPAYMENTPLANGENERATIONDLG1CRERELECUST1: Generate WO from Payment plan";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headbar.showBar());
        appendToHTML(fmt.drawReadLabel("PCMWPAYMENTPLANGENERATIONDLG1INFOTEXT1: This job will be started as a background job. Status of the job will be found in the Background Jobs window, in the General folder in the Navigator."));
        appendToHTML(headlay.generateDataPresentation());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        if (closeWin)
        {
            appendDirtyJavaScript("window.close();");
        }

        appendDirtyJavaScript("function lovAgreementId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("openLOVWindow('AGREEMENT_ID',i,\n");
        appendDirtyJavaScript("'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CUSTOMER_AGREEMENT&__FIELD=");
        appendDirtyJavaScript(mgr.translateJavaScript("Agreement Id"));
        appendDirtyJavaScript("&__WHERE=CONTRACT+IN+%28SELECT+CONTRACT+FROM+site_public+WHERE+contract+=+User_Allowed_Site_API.Authorized%28+Contract%29%29'\n");              
        //appendDirtyJavaScript("&__WHERE=CONTRACT+IN+%28Select+CONTRACT+from+USER_ALLOWED_SITE%29'\n"); 
        appendDirtyJavaScript("		,600,450,'validateAgreementId');\n");
        appendDirtyJavaScript("}\n"); 

    }

}
