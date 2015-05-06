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
 * File        : TransferMaintToolTransactionDlg.java
 * Description : Transfer Tool and Facilities Transactions.
 * Notes       :
 * ----------------------------------------------------------------------------
 *
 * Created     : SHAFLK   081119 Bug 77074. 
 * SHAFLK  090824  Bug 85094, Modified lov view for Voucher Type.
 * ----------------------------------------------------------------------------
 */


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class TransferMaintToolTransactionDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.TransferMaintToolTransactionDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPBlockLayout headlay;
    private ASPBlock headblk2;
    private ASPRowSet headset2;
    private ASPBlockLayout headlay2;

    private ASPField f;
    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================

    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPBuffer buf;

    //===============================================================
    // Construction 
    //===============================================================
    public TransferMaintToolTransactionDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run()
    {
        ASPManager mgr = getASPManager();
        ctx = mgr.getASPContext();
	fmt = mgr.newASPHTMLFormatter();
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
        /* Used to get the user's default settings*/

        ASPManager mgr = getASPManager();
        String contract = "%";

        trans.clear();
        cmd = trans.addCustomCommand("GETCONT","USER_DEFAULT_API.GET_USER_CONTRACT");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomFunction("GETCOMPANY","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT","GETCONT/DATA");

        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","ISUSER");
  
        cmd = trans.addCustomFunction("GETUSERGROUP","User_Group_Member_Finance_API.Get_Default_Group","USER_GROUP");
        cmd.addReference("COMPANY","GETCOMPANY/DATA");
        cmd.addReference("ISUSER","GETUSER/DATA");

        cmd = trans.addQuery("SUYSTDATE","SELECT trunc(SYSDATE) CURRENT_DATE FROM DUAL");

        cmd = trans.addCustomCommand("GETVOUTYPE", "Voucher_Type_API.Get_Voucher_Types");
        cmd.addParameter("VOUCHER_TYPES");
        cmd.addParameter("VOUCHER_TYPE");
        cmd.addReference("COMPANY","GETCOMPANY/DATA");
        cmd.addReference("CURRENT_DATE","SUYSTDATE/DATA");
        cmd.addReference("USER_GROUP","GETUSERGROUP/DATA");
        cmd.addParameter("FUNCTION_GROUP","TF");

        trans = mgr.perform(trans);

        String company = trans.getValue("GETCOMPANY/DATA/COMPANY");
        String user_group = trans.getValue("GETUSERGROUP/DATA/USER_GROUP");
        String sysDate = trans.getValue("SUYSTDATE/DATA/CURRENT_DATE");
        String voucher_type = trans.getValue("GETVOUTYPE/DATA/VOUCHER_TYPE");

        headset.setValue("COMPANY",company);
        headset.setValue("CONTRACT",contract);
        headset.setValue("USER_GROUP",user_group);
        headset.setValue("VOUCHER_TYPE",voucher_type);
        headset2.setValue("DATE_APPLIED",sysDate);
    }

    public void  okFind()
    {
        ASPManager mgr = getASPManager();
        ASPQuery q;

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");

        q = trans.addQuery(headblk2);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if ( headset.countRows() == 0 )
        {
            mgr.setStatusLine("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGNODATA: No data found.");
            headset.clear();
        }
        else
            mgr.createSearchURL(headblk);
        get_contract();
    }

    public void  submit()
    {
        ASPManager mgr = getASPManager();
        trans.clear();
        cmd = trans.addCustomCommand("MM2","Pcm_Accounting_API.Transfer_To_Finance_Tf");
        cmd.addParameter("COMPANY",mgr.readValue("COMPANY"));
        cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
        cmd.addParameter("DATE_APPLIED",mgr.readValue("DATE_APPLIED"));
        cmd.addParameter("OFFSET","0");
        cmd.addParameter("USER_GROUP",mgr.readValue("USER_GROUP"));
        cmd.addParameter("VOUCHER_TYPE",mgr.readValue("VOUCHER_TYPE"));
        cmd.addParameter("FROM_DATE",mgr.readValue("FROM_DATE"));
        cmd.addParameter("VOUCHER_DATE",mgr.readValue("VOUCHER_DATE"));
        trans = mgr.perform(trans);

        mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
    }

    public void  cancel()
    {
        ASPManager mgr = getASPManager();
        mgr.redirectTo("../navigator.page?MAINMENU=Y&NEW=Y");
    }

public void preDefine()
{
        ASPManager mgr = getASPManager();  

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("COMPANY");                                                  
        f.setSize(25);
        f.setFunction("''");
        f.setDynamicLOV("USER_ALLOWED_COMPANY_LOV",600,445);
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGCOMPANY: Company");                                                      
        f.setInsertable(); 
        f.setMandatory();                                                                         

        f = headblk.addField("CONTRACT");                                                  
        f.setSize(25); 
        f.setMandatory();                                                                         
        f.setUpperCase();
        f.setFunction("''");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGCONTRACT: Site");                                                     
        f.setInsertable();  

        f = headblk.addField("USER_GROUP");                                                  
        f.setSize(25);
        f.setFunction("''");
        f.setDynamicLOV("USER_GROUP_FINANCE","COMPANY",600,445);
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGUSER_GROUP: User Group");                                                      
        f.setInsertable(); 
        f.setMandatory();                                                                         

        f = headblk.addField("VOUCHER_TYPE");                                                  
        f.setSize(25); 
        f.setMandatory();                                                                         
        f.setUpperCase();
        f.setFunction("''");
        f.setDynamicLOV("VOUCHER_TYPE_USER_VOV",600,445);
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGVOUCHER_TYPE: Voucher Type");                                                     
        f.setInsertable(); 

        f = headblk.addField("ISUSER");                                                  
        f.setHidden();                                                                         
        f.setFunction("''");

        f = headblk.addField("CURRENT_DATE","Date");
	f.setHidden();
        f.setFunction("''");

        f = headblk.addField("VOUCHER_TYPES");
	f.setHidden();
        f.setFunction("''");

        f = headblk.addField("FUNCTION_GROUP");
	f.setHidden();
        f.setFunction("'TF'");
     
        f = headblk.addField("OFFSET");
        f.setHidden();
        f.setFunction("''");
        
        f = headblk.addField("CURRENT_YEAR");
        f.setHidden();
        f.setFunction("TO_CHAR(sysdate,'yyyy')");


        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setDialogColumns(2);
        headlay.defineGroup(mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGGRPLABEL1: Parameters"),"COMPANY,CONTRACT,USER_GROUP,VOUCHER_TYPE",true,true);
        headlay.showBottomLine(false);

        headblk2 = mgr.newASPBlock("HEAD2");

        f = headblk2.addField("FROM_DATE", "Date");                                                 
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGDATEFROM: From Date");                                                     
        f.setFunction("''");                                                       
        f.setInsertable();                                                                 
        f.setSize(25);   

        f = headblk2.addField("DATE_APPLIED", "Date");                                                 
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGDATETO: To Date");                                                     
        f.setFunction("''");                                                       
        f.setInsertable();                                                                 
        f.setSize(25);

        f = headblk2.addField("VOUCHER_DATE", "Date");                                                 
        f.setLabel("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGVOUCHER_DATE: Voucher Date");                                                     
        f.setFunction("''");                                                       
        f.setInsertable();                                                                 
        f.setSize(25);

        headblk2.setView("DUAL");
        headset2 = headblk2.getASPRowSet();
        headlay2 = headblk2.getASPBlockLayout();
        headlay2.setDefaultLayoutMode(headlay2.EDIT_LAYOUT);
        headlay2.setDialogColumns(1);
        headlay2.defineGroup(mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGGRPLABEL2: Date Selection"),"FROM_DATE,DATE_APPLIED,VOUCHER_DATE",true,true);
        headlay2.showBottomLine(false);

}

//===============================================================
//  HTML
//===============================================================
        protected String getDescription()
        {
            ASPManager mgr = getASPManager();
    
            return mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGTITLE1: Transfer Tool and Facilities Transactions");
        }
    
        protected String getTitle()
        {
            ASPManager mgr = getASPManager();
    
            return mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGTITLE1: Transfer Tool and Facilities Transactions");
        }

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<table bgcolor=\"white\" width=\"727\" id=\"cntHEAD\" class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("<tr><td>\n");
		appendToHTML(headlay.generateDataPresentation());
		appendToHTML("</td></tr>\n");
		appendToHTML("<!-- table cell for radio buttons    -->\n");
		appendToHTML("<tr><td>\n");
		appendToHTML("<table border=0 width=100%  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("	<tr><td>\n");
		appendToHTML("<table width=100% border=0  class=\"SubBlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("<tr>\n");
		appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
		appendToHTML(fmt.drawRadio("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGRADIO1: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;One Voucher Per Date","OPTION","0",true," OnClick='validateOption(0)'"));
		appendToHTML("</td><td width=10>&nbsp</td>\n");
		appendToHTML("	<tr>\n");
		appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
		appendToHTML(fmt.drawRadio("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGRADIO2: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;One Voucher for All Dates", "OPTION","1",false,"OnClick='validateOption(1)'"));
		appendToHTML("</td><td width=10>&nbsp</td>\n");
		appendToHTML("</table>	\n");
		appendToHTML("<!-- end of table cell for radio buttons    -->\n");
		appendToHTML("<tr><td>\n");
		appendToHTML(headlay2.generateDataPresentation());
		appendToHTML("</td></tr>\n");
		appendToHTML("<tr>		\n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

                printNewLine();
                printSpaces(1);
                printSubmitButton("SUBMIT",mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGOKBUTTON:    OK    "), "");
                printSpaces(1);
                printSubmitButton("CANCEL",mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGCANCELBUTTON:  Cancel "), "");
                printSpaces(1);
                printSubmitButton("DEFAULTS",mgr.translate("PCMWTRANSFERMAINTTOOLTRANSACTIONDLGDEFAULTSBUTTON:  Get Defaults "), "");

                appendDirtyJavaScript("	document.form.VOUCHER_DATE.disabled = true;\n");
		appendDirtyJavaScript("function validateOption(i)\n");
		appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("		if (i == '0')\n");
                appendDirtyJavaScript("		       	document.form.VOUCHER_DATE.disabled = true;\n");
                appendDirtyJavaScript("		else\n");
		appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("		       	document.form.VOUCHER_DATE.disabled = false;\n");
                appendDirtyJavaScript("		       	document.form.VOUCHER_DATE.value = document.form.DATE_APPLIED.value;\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovContract(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript(" if (document.form.COMPANY.value != '') \n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT',i).indexOf('%') !=-1)? getValue_('CONTRACT',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('CONTRACT',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('CONTRACT',i))\n");
                appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript(",550,500,'');\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovVoucherType(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript(" if (document.form.COMPANY.value != '') \n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript("	      whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND USER_GROUP = '\" +URLClientEncode(document.form.USER_GROUP.value)+ \"' AND FUNCTION_GROUP = '\" +URLClientEncode(document.form.FUNCTION_GROUP.value)+ \"' AND ACCOUNTING_YEAR = '\" +URLClientEncode(document.form.CURRENT_YEAR.value)+\"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('VOUCHER_TYPE',i).indexOf('%') !=-1)? getValue_('VOUCHER_TYPE',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('VOUCHER_TYPE',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=VOUCHER_TYPE_USER_VOV&__FIELD=Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('VOUCHER_TYPE',i))\n");
                appendDirtyJavaScript("+ '&VOUCHER_TYPE=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript(",550,500,'');\n");
                appendDirtyJavaScript("}\n");


	}
}


