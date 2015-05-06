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
*  File        : ConnPermitDlg.java 
*  Created     : 050805 NEKOLK AMUT 115:Isolation and permits
*  Modified    : 
*  050812 NEKOLK Changed permit seq lov to permit_lov
*  050824 NEKOLK Added changes to get warning msgs.
*  050907 NIJALK Bug 126388: Modified run(),printContents().
*  060126 THWILK Corrected localization errors.
*  070214 SHAFLK Bug 63500, Changed title of the page.
*  070307 ILSOLK Merged Bug Id 63500.
*  070424 ASSALK Corrected APP_PATH tag to COOKIE_PATH.
*  070710 ILSOLK Eliminated XSS.
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

public class ConnPermitDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ConnPermitDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPField f;

    private String valid;
	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String closeOk;
	private String cancelWin;
	private String CurrPermit;
	private ASPCommand cmd;
	private String strDeliDesc;
	private String mNewDlO;
	private double nNewDlo;
        private String wo_no;
        private String wo_no1;
        private ASPBuffer buff;
	private ASPBuffer row;
        private boolean newWin;
        private ASPQuery q;
	private ASPBuffer temp;	
	private String qrystr;
        private boolean first_request = false;

	//===============================================================
	// Construction 
	//===============================================================
	public ConnPermitDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager(); 
                wo_no = "";
		wo_no1 = "";

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		closeOk = ctx.readValue("CLOSEOK","");
		cancelWin = ctx.readValue("CANCELWIN","");
                wo_no =ctx.readValue("WONO",wo_no);
                wo_no1 =ctx.readValue("WONO1",wo_no1);
                valid =  ctx.readValue("VALID","FALSE");
		qrystr = ctx.readValue("QRYSTR","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
                else if (mgr.dataTransfered())
		{
                    buff = mgr.getTransferedData();
		    row = buff.getBufferAt(0); 
		    wo_no = row.getValue("WO_NO");
		    wo_no1 = wo_no;
 		    okFind();
		}
                else if ("VALIDOK".equals(mgr.readValue("BUTTONVAL")))
                {
                    valid="FALSE";
                    validSubmit();
                } 
                else if ("VALIDCANCEL".equals(mgr.readValue("BUTTONVAL")))
                {
                    cancel();
                }
                else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
                {
                    newWin = true;
		    wo_no = mgr.readValue("WO_NO");
		    wo_no1 = wo_no;
                    qrystr = mgr.readValue("QRYSTR");  
		    okFind();
                }

		ctx.writeValue("QRYSTR",qrystr);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  cancel()
	{
		cancelWin = "TRUE";
	}

	public void  validSubmit()
	{
		ASPManager mgr = getASPManager();

		CurrPermit = mgr.readValue("PERMIT_SEQ");
                if (!mgr.isEmpty(CurrPermit))
		{
                        cmd = trans.addCustomCommand("NEWPERMIT","WORK_ORDER_PERMIT_API.Conn_Permit");
			cmd.addParameter("WO_NO",String.valueOf(mgr.readNumberValue("WO_NO")));
			cmd.addParameter("PERMIT_SEQ",String.valueOf(mgr.readNumberValue("PERMIT_SEQ")));

			trans=mgr.perform(trans);
			closeOk = "TRUE";
		}      
	}

	public void  submit()
	{
		ASPManager mgr = getASPManager();
                headset.changeRow();

		CurrPermit = mgr.readValue("PERMIT_SEQ");

        if (!mgr.isEmpty(CurrPermit))
		{
                    valid="FALSE";    
                    
                    cmd = trans.addCustomFunction("ISVALID","WORK_ORDER_PERMIT_API.Is_Valid_Permit","IS_VALID");
                    cmd.addParameter("WO_NO",String.valueOf(mgr.readNumberValue("WO_NO")));
                    cmd.addParameter("PERMIT_SEQ",String.valueOf(mgr.readNumberValue("PERMIT_SEQ")));
                    
                    trans = mgr.validate(trans);

                    String IsValid = trans.getValue("ISVALID/DATA/IS_VALID");
                    trans.clear();
                    if ("FALSE".equals(IsValid))
                    { 
                        trans.clear();
                        ctx.setCookie( "my_cookie", "TRUE" );
                        valid="TRUE";

                    }
                    else
                    {
                        validSubmit();
                    }
                    
		}      
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
            
            temp = headset.getRow();

            temp.setValue("WO_NO",wo_no);
            headset.setRow(temp);

            if (headset.countRows() == 0)
            {
                mgr.setStatusLine("EQUIPWCOPYWORKORDERDLGNODATA: No data found.");
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

		f = headblk.addField("WO_NO","Number");
		f.setHidden();
		f.setFunction("''");

                f = headblk.addField("PERMIT_SEQ","Number","#");
                f.setLabel("PERMITSEQ: Permit Seq:");
                f.setDynamicLOV("PERMIT_LOV",600,445);
                f.setFunction("''");

		f = headblk.addField("IS_VALID");
                f.setFunction("''");
		f.setHidden();

		headblk.setView("MODULE");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT); 
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWCONNPERMITDLGPERMIT: Permit"),"PERMIT_SEQ",true,true);
	}


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
            return "PCMWCONNPERMITDLGTITLE: Connect Permit";
    }

    protected String getTitle()
    {
            return "PCMWCONNPERMITDLGTITLE: Connect Permit";
    }

    protected void printContents() throws FndException
    {
            ASPManager mgr = getASPManager();
            printHiddenField("BUTTONVAL", "");
            appendToHTML(headlay.show());

            appendDirtyJavaScript("if('");     
            appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelWin)); // Bug Id 68773
            appendDirtyJavaScript("' == 'TRUE')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  self.close();\n");
            appendDirtyJavaScript("} \n");

            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(closeOk)); // Bug Id 68773
            appendDirtyJavaScript("' == 'TRUE')\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("  window.open('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr)); // XSS_Safe ILSOLK 20070710
            appendDirtyJavaScript("','");
            appendDirtyJavaScript("WorkOrderPermit");
            appendDirtyJavaScript("',\"\");\n");
            appendDirtyJavaScript("  self.close();\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(valid)); // Bug Id 68773
            appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"my_cookie\")==\"TRUE\")\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("      if (confirm(\"");
            appendDirtyJavaScript(mgr.translate("VALIDPERMITFORWO: Planned work for the work order is not within validity time for the permit.!"));
            appendDirtyJavaScript("\")) {\n");
            appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"VALIDOK\";\n");
            appendDirtyJavaScript("		writeCookie(\"my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("     } \n");

            appendDirtyJavaScript(" else");
            appendDirtyJavaScript(" {\n");
            appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"VALIDCANCEL\";\n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("     } \n");

            appendDirtyJavaScript("} \n");

    }
}
