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
*  File        : CreateReleaseCODlg.java 
*  Created     : ASP2JAVA Tool  010720
*  Modified    :
*  BUNILK  010720  Fixed conversion errors. 
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  JEWILK  031020  Modified methods run() and getContents() to make the page to properly display server error messages. Call 106549.
*  THWILK  031029  Call ID 106549, Modified Ok() method to include onceLoad=true.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  SESELK  050405  Bug id, 50322, Append additional code inorder to populate the fixed payment plan
*  NIJALK  050624  Merged bug 50322.
*  ILSOLK  070710  Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateReleaseCODlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateReleaseCODlg");

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
    private ASPTransactionBuffer trans;
    private boolean clsFlag;
    private String agreeId;
    private String contract;
    private String mchCode;
    private String workTypeId;
    private String woGenDate;
    private String woNo;
    private ASPCommand cmd;
    private String company;
    private ASPBuffer temp;
    private String val;
    private String authoId;
    private String sName;
    private String txt;
    private boolean onceLoaded;
    private String qrystr;
    private String frmname;

    //===============================================================
    // Construction 
    //===============================================================
    public CreateReleaseCODlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        frmname = ctx.readValue("FRMNAME", "");
        qrystr  = ctx.readValue("QRYSTR", "");

        if ( mgr.commandBarActivated() )
            eval(mgr.commandBarFunction());
        else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
            validate();
        else if ( !mgr.isEmpty(mgr.getQueryStringValue("AGREEMENT_ID")) )
        {
            if ( "TRUE".equals(mgr.readValue("ONCE_LOADED")) || !mgr.isExplorer() )
            {
                agreeId = mgr.readValue("AGREEMENT_ID","");
                contract = mgr.readValue("CONTRACT","");
                mchCode = mgr.readValue("MCH_CODE","");
                workTypeId = mgr.readValue("WORK_TYPE_ID","");
                woGenDate = mgr.readValue("WO_GENERATION_DATE","");
                woNo = mgr.readValue("WO_NO","");
                qrystr = mgr.readValue("QRYSTR");     
                frmname = mgr.readValue("FRMNAME");
                ctx.writeValue("FRMNAME", frmname);
                ctx.writeValue("QRYSTR", qrystr);

                cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY");
                cmd.addParameter("CONTRACT",contract);

                trans = mgr.perform(trans);

                company = trans.getValue("COMP/DATA/COMPANY");   

                headset.addRow(trans.getBuffer("HEAD/DATA"));
                temp = headset.getRow();
                temp.setValue("AGREEMENT_ID",agreeId);
                temp.setValue("CONTRACT",contract);
                temp.setValue("MCH_CODE",mchCode);
                temp.setValue("WORK_TYPE_ID",workTypeId);
                temp.setValue("WO_GENERATION_DATE",woGenDate);
                temp.setNumberValue("WO_NO",mgr.readNumberValue("WO_NO"));
                temp.setValue("COMPANY",company);
                headset.setRow(temp);

                onceLoaded = true;
            }
            else
                onceLoaded = false;
        }
        else
            onceLoaded = true;

        adjust();

        ctx.writeValue("FRMNAME", frmname);
        ctx.writeValue("QRYSTR", qrystr);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ( "AUTHORIZER".equals(val) ||   "NAME".equals(val) )
        {
            cmd = trans.addCustomFunction("MAXEMPID","Company_Emp_API.Get_Max_Employee_Id","AUTHORIZERID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("AUTHORIZER");

            trans = mgr.validate(trans);

            authoId = trans.getValue("MAXEMPID/DATA/AUTHORIZERID");

            txt = (mgr.isEmpty(authoId) ? "" : (authoId))+ "^";
            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  BUTTON FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void ok()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("PERSONNAME","Person_Info_API.Get_Name","NAME");
        cmd.addParameter("AUTHORIZER",mgr.readValue("AUTHORIZER",""));

        trans = mgr.perform(trans);

        sName = trans.getValue("PERSONNAME/DATA/NAME");

        if ( mgr.isEmpty(sName) )
            mgr.showAlert(mgr.translate("PCMWCREATERELEASECODLGNOTAUTHOREG: This Authorizer is not registered"));
        else
        {
            trans.clear();

            cmd = trans.addCustomCommand("CRERELCUSTORD","Cust_Agr_Fixed_Price_Plan_API.Create_Release_Customer_Order");
            cmd.addParameter("AGREEMENT_ID",headset.getRow().getValue("AGREEMENT_ID"));
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
            cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
            cmd.addParameter("WORK_TYPE_ID",headset.getRow().getValue("WORK_TYPE_ID"));
            cmd.addParameter("WO_GENERATION_DATE",headset.getRow().getFieldValue("WO_GENERATION_DATE"));
            cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            cmd.addParameter("SCUSTORDERTYPE",mgr.readValue("SCUSTORDERTYPE",""));
            cmd.addParameter("AUTHORIZERID",mgr.readValue("AUTHORIZERID",""));

            trans = mgr.perform(trans);

            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            onceLoaded = true;
            clsFlag = true;
        } 
    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("SCUSTORDERTYPE");
        f.setSize(11);
        f.setMaxLength(3);
        f.setDynamicLOV("CUST_ORDER_TYPE",600,445);
        f.setLabel("PCMWCREATERELEASECODLGSCUSTORDERTYPE: Customer Order Type");
        f.setFunction("''");
        f.setUpperCase();

        f = headblk.addField("AUTHORIZER");
        f.setSize(11);
        f.setMaxLength(20);
        f.setCustomValidation("AUTHORIZER,COMPANY","AUTHORIZERID");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWCREATERELEASECODLGAUTHORIZER: Authorizer");
        f.setUpperCase();
        f.setMandatory();

        f = headblk.addField("MCH_CODE");
        f.setSize(26);
        f.setMaxLength(100);
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("WO_NO","Number");
        f.setSize(8);
        f.setMaxLength(8);
        f.setHidden();

        f = headblk.addField("WORK_TYPE_ID");
        f.setSize(8);
        f.setMaxLength(20);
        f.setHidden();

        f = headblk.addField("WO_GENERATION_DATE","Datetime");
        f.setSize(13);
        f.setHidden();

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(8);
        f.setMaxLength(10);
        f.setHidden();

        f = headblk.addField("AUTHORIZERID");
        f.setSize(8);
        f.setMaxLength(11);
        f.setHidden();
        f.setFunction("''");
        f.setUpperCase();

        f = headblk.addField("COMPANY");
        f.setSize(8);
        f.setMaxLength(20);
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("NAME");
        f.setSize(11);
        f.setMaxLength(20);
        f.setCustomValidation("AUTHORIZER,COMPANY","AUTHORIZERID");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setHidden();
        f.setFunction("''");
        f.setUpperCase();

        headblk.setView("CUST_AGR_FIXED_PRICE_PLAN");
        headblk.defineCommand("CUST_AGR_FIXED_PRICE_PLAN_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableModeLabel();
        headbar.disableCommand(headbar.FORWARD);
        headbar.disableCommand(headbar.BACKWARD);
        headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELEDIT,null,"closeWnd()");

        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
    }

    public void adjust()
    {
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCREATERELEASECODLGCRERELECUST: Create and Release Customer Order";
    }

    protected String getTitle()
    {
        return "PCMWCREATERELEASECODLGCRERELECUST: Create and Release Customer Order";
    }

    protected AutoString getContents() throws FndException 
    {
        ASPManager mgr = getASPManager();
        AutoString out = getOutputStream();
        out.clear();

        if ( !onceLoaded && mgr.isExplorer() )
        {
            out.append("<html>\n"); 
            out.append("<head></head>\n"); 
            out.append("<body>"); 
            out.append("<form name='form' method='POST' action='"+mgr.getURL()+"?"+mgr.getQueryString()+"'>"); 

            printHiddenField("ONCE_LOADED","");

            out.append("</form></body></html>"); 

            appendDirtyJavaScript("document.form.ONCE_LOADED.value='TRUE'; document.form.submit();"); 

        }
        else
        {
            out.append("<html>\n");
            out.append("<head>");
            out.append(mgr.generateHeadTag("PCMWCREATERELEASECODLGCRERELECUST: Create and Release Customer Order"));
            out.append("<title></title>\n");
            out.append("</head>\n");
            out.append("<body ");
            out.append(mgr.generateBodyTag());
            out.append(">\n");
            out.append("<form ");
            out.append(mgr.generateFormTag());
            out.append(">\n");
            out.append(mgr.startPresentation("PCMWCREATERELEASECODLGCRERELECUST: Create and Release Customer Order"));
            out.append(headlay.show());
            out.append(mgr.endPresentation());
            out.append("</form>\n");
            out.append("</body>\n");
            out.append("</html>");

            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
            appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

            appendDirtyJavaScript("function closeWnd()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  window.close();\n");
            appendDirtyJavaScript("}\n");

            if (clsFlag)
            {
                appendDirtyJavaScript("   jItem1AgreeId = '");
                appendDirtyJavaScript( mgr.encodeStringForJavascript(headset.getRow().getValue("AGREEMENT_ID")) ); // XSS_Safe ILSOLK 20070710
                appendDirtyJavaScript("';\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript( mgr.encodeStringForJavascript(mgr.getASPContext().readValue("QRYSTR", "")) );
                appendDirtyJavaScript("' + \"&CODONE=\"+URLClientEncode(jItem1AgreeId), '");
                appendDirtyJavaScript( mgr.encodeStringForJavascript(mgr.getASPContext().readValue("FRMNAME", "")) );
                appendDirtyJavaScript("',\"\");\n");
                appendDirtyJavaScript("  self.close();\n");
            }
        }

        return out;
    }
}
