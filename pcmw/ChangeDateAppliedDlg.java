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
*  File        : ChangeDateAppliedDlg.java 
*  Created     : 100730  ILSOLK  Bug 90329,Created.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class ChangeDateAppliedDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ChangeDateAppliedDlg");

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
        private ASPQuery q;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	
        private ASPTransactionBuffer trans;
        private ASPBuffer data;
	private ASPCommand cmd;
        private String val;
        private String transId;
        private boolean closewin;
	private String cancelFlag;
        private String InfoMsg;
        private boolean bFirstRequest;
	//===============================================================
	// Construction 
	//===============================================================
	public ChangeDateAppliedDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                
		ASPManager mgr = getASPManager();
                ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

                transId  = ctx.readValue("TRANSID","");
                closewin = ctx.readFlag("CLOSEWIN",false);
                InfoMsg  = ctx.readValue("INFOMSG","");


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
	        else if (!mgr.isEmpty(mgr.getQueryStringValue("TRANSID")))
                {
                   bFirstRequest=true;
                }
                else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer())
		{
                    transId = mgr.readValue("TRANSID");
		}
                else if (mgr.buttonPressed("OK"))
		    submit();

                ctx.writeValue("TRANSID",transId);
                ctx.writeValue("INFOMSG",InfoMsg);

	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void submit()
	{
		ASPManager mgr = getASPManager();
                String transarr[] = split(transId,"^");

                for (int i = 0; i < transarr.length; i++){
                    if (!mgr.isEmpty(transarr[i])) {
                        cmd = trans.addCustomCommand("MODDATE"+i,"Wo_Time_Transaction_Hist_API.Modify_Date_Applied");
                        cmd.addParameter("INFO");
                        cmd.addParameter("TRANS_ID",transarr[i]);
                        cmd.addParameter("NEW_DATE", mgr.readValue("NEW_DATE"));
                    }
                    
                }
                trans = mgr.perform(trans);

                for (int i = 0; i < transarr.length; i++){
                    if (!mgr.isEmpty(transarr[i])) {
                        String Info = trans.getValue("MODDATE"+i+"/DATA/INFO");
                        if (!mgr.isEmpty(Info)) {
                            InfoMsg = Info.substring(5, Info.length());
                        }
                    }
                }
                closewin = true;
	} 
       
        public void validate()
	{
		ASPManager mgr = getASPManager();
		val = mgr.readValue("VALIDATE");  

		mgr.endResponse();
	}
        
 
	public void  cancel()
	{
		ASPManager mgr = getASPManager();

		cancelFlag = "TRUE";
	}

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

                f = headblk.addField("NEW_DATE","Datetime");
                f.setSize(30);
                f.setLabel("PCMWCHANGEDATEAPPLIEDDLGNEWDATE: New Date Applied");
                f.setFunction("''");
    
                f = headblk.addField("INFO");
                f.setFunction("''");
                f.setHidden();

                f = headblk.addField("TRANS_ID");
                f.setFunction("''");
                f.setHidden();
                
		headblk.setView("");
		headblk.defineCommand("MS_CALL_CODE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCHANGEDATEAPPLIEDDLGTITLE: Change Date Applied";
	}

	protected String getTitle()
	{
		return "PCMWCHANGEDATEAPPLIEDDLGTITLE: Change Date Applied";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

                if (bFirstRequest && mgr.isExplorer())
                {
                     appendToHTML("<html>\n"); 
                     appendToHTML("<head></head>\n"); 
                     appendToHTML("<body>"); 
                     appendToHTML("<form name='form' method='POST'action='"+mgr.getURL()+"?"+mgr.HTMLEncode(mgr.getQueryString())+"'>");
                     appendToHTML(fmt.drawHidden("FIRST_REQUEST", "OK"));
                     appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("TRANSID"))+"\" name=\"TRANSID\" >"); 
        	     appendToHTML("</form></body></html>"); 
                     appendDirtyJavaScript("document.form.submit();"); 
                } 
                appendToHTML(headlay.show());
                appendToHTML("<table id=\"SND\" border=\"0\">\n");
                appendToHTML("<tr>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWCHANGEDATEAPPLIEDDLGOK: OK"),"OK"));
                appendToHTML("</td>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawButton("CANCEL",mgr.translate("PCMWCHANGEDATEAPPLIEDDLGCANCEL: Cancel"),"onClick='window.close();'"));
                appendToHTML("</td>\n");
                appendToHTML("</tr>\n");
                appendToHTML("</table>\n");
                
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
                
               
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(closewin);
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if ('");
                appendDirtyJavaScript(InfoMsg);
                appendDirtyJavaScript("' != '')\n");
                appendDirtyJavaScript("{ \n");
                appendDirtyJavaScript("window.alert('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(InfoMsg));
                appendDirtyJavaScript("');");
                appendDirtyJavaScript("} \n");
                appendDirtyJavaScript("window.opener.refresh();\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");

                if ("TRUE".equals(cancelFlag))
                {
                   appendDirtyJavaScript("window.close();");

                }


	}
}
