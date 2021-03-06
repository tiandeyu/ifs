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
*  File        : CopyHistWorkOrderDlg.java 
*  Created     : AMDILK 060911
*  Modified    :
*  AMDILK  060912  MTPR904: Request id 21686. Insert a confirmation box to view the generated WO. Modifed printContents()
*  ILSOLK  061207  MTPR904: Request id 21686.Modified preDefine().
*  ILSOLK  070710  Eliminated XSS.
*  ILSOLK  070730  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
* -----------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CopyHistWorkOrderDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CopyHistWorkOrderDlg");

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

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String wo_no;
    private String wo_no1;
    private String flt_rep_flg;
    private String flt_rep_flg1;
    private String comp; 
    private int sValueChecker;
    private String destiWoNo;
    private ASPTransactionBuffer trans;
    private boolean message;
    private boolean newWin;
    private String closeWin;
    private String generated;
    private ASPBuffer buff;
    private ASPBuffer row;
    private ASPCommand cmd;
    private String calling_url;
    private ASPQuery q;
    private ASPBuffer temp;
    private String frmPath;
    //===============================================================
    // Construction 
    //===============================================================
    public CopyHistWorkOrderDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        wo_no = "";
        wo_no1 = "";
        flt_rep_flg = "";
        flt_rep_flg1 = "";
        comp = ""; 
        sValueChecker =  0;
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        fmt = mgr.newASPHTMLFormatter();

        wo_no =ctx.readValue("WO",wo_no);
        wo_no1 =ctx.readValue("WO1",wo_no1);
        message =ctx.readFlag("MESSAGE",false);
        flt_rep_flg =ctx.readValue("FLTREPFLG",flt_rep_flg);
        flt_rep_flg1 =ctx.readValue("FLTREPFLG1",flt_rep_flg1);
        comp =  ctx.readValue("COMP",comp) ;  
        sValueChecker = ctx.readNumber("CTXVALCH",sValueChecker);
        newWin = ctx.readFlag("NEWWIN",false);
        closeWin = ctx.readValue("CLOSEWIN","");
        generated = ctx.readValue("GENERATED","");
        frmPath = ctx.readValue("THIFRMPAT",frmPath);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();
            row = buff.getBufferAt(0); 
            wo_no = row.getValue("WO_NO");
            wo_no1 = wo_no;
            row = buff.getBufferAt(1); 
            flt_rep_flg = row.getValue("FAULT_REP_FLAG");
            flt_rep_flg1 = flt_rep_flg;
            row = buff.getBufferAt(2); 
            comp = row.getValue("COMPANY");
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            newWin = true;
            wo_no = mgr.readValue("WO_NO");
            wo_no1 = wo_no;
            flt_rep_flg = mgr.readValue("FAULT_REP_FLAG");
            flt_rep_flg1 = flt_rep_flg;
            comp = mgr.readValue("COMPANY"); 
            okFind();
        }

        ctx.writeValue("WO",wo_no);
        ctx.writeValue("WO1",wo_no1);
        ctx.writeFlag("MESSAGE",message);
        ctx.writeValue("FLTREPFLG",flt_rep_flg);
        ctx.writeValue("FLTREPFLG1",flt_rep_flg1);
        ctx.writeNumber("CTXVALCH",sValueChecker);
        ctx.writeFlag("NEWWIN",newWin);
        ctx.writeValue("THIFRMPAT",frmPath);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        mgr.showError("VALIDATE not implemented");
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void sub()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();
        wo_no =ctx.readValue("WO",wo_no);

        cmd = trans.addCustomCommand("COPY","HISTORICAL_WORK_ORDER_API.Copy__");
        cmd.addParameter("DESTI_WO_NO",headset.getRow().getValue("DESTI_WO_NO"));
        cmd.addParameter("WO_NO",wo_no);
        cmd.addParameter("CBROLE",headset.getRow().getValue("CBROLE"));
        cmd.addParameter("FAULT_REP_FLAG","1");
        cmd.addParameter("CBPERMIT",headset.getRow().getValue("CBPERMIT"));
        cmd.addParameter("CBCONNECTION",headset.getRow().getValue("CBCONNECTION"));
        cmd.addParameter("CBREQUISITIONS",headset.getRow().getValue("CBREQUISITIONS"));
        cmd.addParameter("CBDOCUMENT",headset.getRow().getValue("CBDOCUMENT"));
        cmd.addParameter("CBSPAREPART",headset.getRow().getValue("CBSPAREPART"));
        cmd.addParameter("CBTOOLFACILITY",headset.getRow().getValue("CBTOOLFACILITY"));
        cmd.addParameter("CBJOB",headset.getRow().getValue("CBJOB"));
        cmd.addParameter("REPORTED_BY",headset.getRow().getValue("REPORTED_BY")); 
        cmd.addParameter("PLAN_S_DATE",headset.getRow().getFieldValue("PLAN_S_DATE")); 
        trans=mgr.perform(trans);    

        destiWoNo = trans.getValue("COPY/DATA/DESTI_WO_NO");
        if (newWin)
            generated = "TRUE";
        else
            message=true;  
    }

    public void cancel()
    {
        ASPManager mgr = getASPManager();

        headtbl.clearQueryRow();
        headset.setFilterOff();
        headset.unselectRows();
        closeWin = "TRUE";
        calling_url=ctx.getGlobal("CALLING_URL");
        frmPath = calling_url+"?WO_NO="+wo_no1;
    }


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData() );

        if (headset.countRows() == 0)
            mgr.setStatusLine("PCMWCOPYHISTWORKORDERDLGNODATA: No data found.");
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addEmptyQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);
        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData() );

        trans.clear();
        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","ISUSER");

        cmd = trans.addCustomFunction("GETREPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("ISUSER","GETUSER/DATA");

        trans = mgr.perform(trans);
        String repby = trans.getValue("GETREPBY/DATA/REPORTED_BY");

        temp = headset.getRow();

        temp.setValue("REPORTED_BY",repby);
        temp.setValue("COMPANY",comp);
        headset.setRow(temp);
       
        if (headset.countRows() == 0)
        {
            mgr.setStatusLine("PCMWCOPYHISTWORKORDERDLGNODATA: No data found.");
            headset.clear();
        }
    }

//-----------------------------------------------------------------------------
//-------------------------------  PREDEFINE  ---------------------------------
//-----------------------------------------------------------------------------

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("WO_NO","Number");
        f.setSize(26);
        f.setFunction("''");
        f.setHidden();
        f.setLabel("PCMWCOPYHISTWORKORDERDLGWONO: New Wo No");

        f = headblk.addField("DESTI_WO_NO");
        f.setSize(26);
        f.setHidden();
        f.setLabel("PCMWCOPYHISTWORKORDERDLGDESTIWONO: Destination Work Order No");
        f.setFunction("''");

        f = headblk.addField("CBJOB");
        f.setSize(13);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBJOBS: Jobs");
        f.setFunction("''");
        f.setCheckBox("N,Y");

        f = headblk.addField("CBROLE");
        f.setSize(13);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBROLE: Operations");
        f.setFunction("''");
        f.setCheckBox("N,Y");

        f = headblk.addField("CBSPAREPART");
        f.setSize(12);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBSPAREPART: Materials");
        f.setFunction("''");
        f.setCheckBox("N,Y");

        f = headblk.addField("CBTOOLFACILITY");
        f.setSize(12);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBTOOLFACILITY: Tools and Facilities");
        f.setFunction("''");
        f.setCheckBox("N,Y");

        f = headblk.addField("CBCONNECTION");
        f.setSize(13);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBCONNECTION: Connections");
        f.setFunction("''");
	f.setHidden();
      
	f = headblk.addField("CBREQUISITIONS");
        f.setSize(13);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBREQUISI: Requisitions");
        f.setFunction("''");
        f.setCheckBox("N,Y");

        f = headblk.addField("CBPERMIT");
        f.setSize(12);
        f.setLabel("PCMWWCOPYHISTWORKORDERDLGCBPERMIT: Permits");
        f.setFunction("''");
        f.setCheckBox("N,Y");

        f = headblk.addField("CBDOCUMENT");
        f.setSize(12);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGCBDOCUMENT: Documents");
        f.setFunction("''");
        f.setCheckBox("N,Y");
	     //f.setHidden();
        
        f = headblk.addField("ERRDESCR");
        f.setSize(19);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("FAULT_REP_FLAG", "Number");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("ISUSER");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COMPANY");
        f.setHidden();
        f.setFunction("''");

	f = headblk.addField("PLAN_S_DATE","Datetime");
        f.setLabel("PCMWCOPYWOPLANSDATE: Planned Start");
        f.setFunction("''");

        f = headblk.addField("REPORTED_BY");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
        f.setLabel("PCMWCOPYHISTWORKORDERDLGREPORTEDBY: Reported By");
        f.setFunction("''");
        f.setSize(10);
       
        headblk.setView("dual");
        headblk.disableDocMan();
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN, "sub", "checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELEDIT, "cancel");

        headtbl = mgr.newASPTable(headblk);   
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
	headlay.setDialogColumns(2);

	headlay.defineGroup(mgr.translate("PCMWCOPYHISTWORKORDERDLGCOPYLABLE2: Copy Options"),"CBJOB,CBROLE,CBSPAREPART,CBTOOLFACILITY,CBREQUISITIONS,CBPERMIT,CBDOCUMENT",true,true);
        headlay.defineGroup("","REPORTED_BY,PLAN_S_DATE",false,true);
    }

//===============================================================
//  HTML
//===============================================================

    protected String getDescription()
    {
        return "PCMWCOPYHISTWORKORDERDLGDESC: Copy Historical Work Order";
    }

    protected String getTitle()
    {
        return "PCMWCOPYHISTWORKORDERDLGTITLE: Copy Historical Work Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (!("TRUE".equals(closeWin)) && !("TRUE".equals(generated)))
        {
            if (headlay.isVisible())
            {
                if (!message)
                {
                    appendToHTML("     <table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1>\n");
                    appendToHTML("       <tr>	\n");
                    appendToHTML("	 <td>\n");
                    appendToHTML(headbar.showBar());
                    appendToHTML(fmt.drawReadLabel(mgr.translate("PCMWCOPYHISTWORKORDERDLGMESSAGELABEL1: If no options are entered, only the general historical work order data will be copied.")));
                    appendToHTML("	   </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("	 </td>			   \n");
                    appendToHTML("       </tr>				\n");
                    appendToHTML("     </table>\n");
                }
                if (message)
                {
                    appendToHTML("     <table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1>\n");
                    appendToHTML("       <tr>	\n");
                    appendToHTML("	 <td>\n");
                    appendToHTML("	   </br>\n");
                    appendToHTML(fmt.drawReadLabel(mgr.translate("PCMWCOPYHISTWORKORDERDLGMESSAGELABEL3: The historical work order is now copied. New generated WO No is ")+destiWoNo));
                    appendToHTML("	   </br></br>\n");
                    appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWCOPYHISTWORKORDERDLGOKBUTTON:   OK  "),""));
                    appendToHTML("	 </td>			   \n");
                    appendToHTML("	</tr>				\n");
                    appendToHTML("      </table>\n");
                }
            }
        }

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("if('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(closeWin)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        //Web Alignment - focus to the url in the called window
        appendDirtyJavaScript("  window.opener.location = '" + mgr.encodeStringForJavascript(frmPath) + "';\n"); // XSS_Safe ILSOLK 20070710
        //
        appendDirtyJavaScript("  self.close();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(generated)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	newWoNo = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(destiWoNo)); // XSS_Safe ILSOLK 20070710
        appendDirtyJavaScript("';\n");
	appendDirtyJavaScript("if (confirm(\" ");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWCOPYHISTWORKORDERDLGNEWWONO: The historical work order is now copied. New generated WO No is &1 . \n Do you want to view the generated work order?", destiWoNo));
        appendDirtyJavaScript("\" )) \n");
	appendDirtyJavaScript("{ \n");
	appendDirtyJavaScript("window.open(\"ActiveSeparate2.page?WO_NO=");
	appendDirtyJavaScript(mgr.encodeStringForJavascript(destiWoNo)); // XSS_Safe ILSOLK 20070710
	appendDirtyJavaScript(" \", \"prepareWorkOrd\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\"); \n");
        appendDirtyJavaScript("self.close();\n");
	appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("else \n");
	appendDirtyJavaScript("{ \n");
	appendDirtyJavaScript("self.close();\n");
	appendDirtyJavaScript("} \n");
        appendDirtyJavaScript("}\n");
    }
}

