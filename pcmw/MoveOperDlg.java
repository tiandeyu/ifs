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
*  File        : MoveOperDlg.java 
*  Created     : 050722  THWILK  Created.
*  Modified    : 060815  AMNILK  Bug 58216, Eliminated SQL Injection security vulnerability.
*              : 060904  AMNILK  Merged Bug Id: 58216.
*  	       : 070718  AMNILK  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class MoveOperDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MoveOperDlg");

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
        private String woNo;
        private String rowNo;
        private String frameName;
        private String qryStr;
        private String calling_url;
        private boolean closewin;

	//===============================================================
	// Construction 
	//===============================================================
	public MoveOperDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                
		ASPManager mgr = getASPManager();
                ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

                closewin    = ctx.readFlag("CLOSEWIN",false);
                woNo        = ctx.readValue("WO_NO","");
		rowNo       = ctx.readValue ("ROW_NO","");
                qryStr      = ctx.readValue("QRYSTR","");
                frameName   = ctx.readValue("FRMNAME","");
                calling_url = ctx.readValue("CALLING_URL","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
	      
		else if (!mgr.isEmpty(mgr.getQueryStringValue("RES_TYPE")))
		{
                        woNo      = mgr.readValue("WO_NO"); 
			rowNo     = mgr.readValue("ROW_NO"); 
                        qryStr    = mgr.readValue("QRYSTR","");
                        frameName = mgr.readValue("FRMNAME","");
                        startUp();
		}
                 else if (mgr.buttonPressed("OK"))
		    submit();
	       

                ctx.writeValue("WO_NO",woNo);
		ctx.writeValue("ROW_NO",rowNo);
                ctx.writeFlag("CLOSEWIN",closewin);
                ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("FRMNAME",frameName);
                ctx.writeValue("CALLING_URL",calling_url);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void submit()
	{
		ASPManager mgr = getASPManager();   
                trans.clear();         
                cmd = trans.addCustomCommand( "MOVEOP","Work_Order_Role_API.Shift_Operations");
		cmd.addParameter("INFO");
                cmd.addParameter("WO_NO",woNo);
                cmd.addParameter("ROW_NO",rowNo);
                cmd.addParameter("NEW_START_TIME");
                
		trans    = mgr.perform(trans);  
                closewin = true;
	} 
       
        public void validate()
	{
		ASPManager mgr = getASPManager();
		val = mgr.readValue("VALIDATE");  

		mgr.endResponse();
	}
        
        public void  startUp()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		//Bug 58216 Start
                q = trans.addQuery("SYSDAT","SELECT date_from START_TIME,date_to END_TIME from WORK_ORDER_ROLE WHERE WO_NO= ? AND ROW_NO= ?");
		q.addParameter("WO_NO",woNo);
		q.addParameter("ROW_NO",rowNo);
		//Bug 58216 End
		trans = mgr.perform(trans);
                Date startDate = trans.getBuffer("SYSDAT/DATA").getFieldDateValue("START_TIME");
                Date endDate   = trans.getBuffer("SYSDAT/DATA").getFieldDateValue("END_TIME");
                ASPBuffer tempBuff = mgr.newASPBuffer();
		tempBuff.setFieldDateItem("START_TIME",startDate);
                tempBuff.setFieldDateItem("END_TIME",endDate);
                tempBuff.setFieldDateItem("NEW_START_TIME",startDate);

		headset.addRow(tempBuff);   
	}


      public void preDefine()
	{
		ASPManager mgr = getASPManager();        

		headblk = mgr.newASPBlock("HEAD");

                headblk.addField("WO_NO").
                setHidden();
                
                headblk.addField("ROW_NO").
                setHidden();
                                     
                headblk.addField("START_TIME","Datetime").
                setSize(30).
                setReadOnly().
                setLabel("PCMWSHIFTOPERDLGSTARTTIME: Start Time");
                
                headblk.addField("END_TIME","Datetime").
                setSize(30).
                setReadOnly().
                setLabel("PCMWSHIFTOPERDLGENDTIME: End Time");

                headblk.addField("NEW_START_TIME","Datetime").
                setSize(30).
                setLabel("PCMWSHIFTOPERDLGNEWSTARTTIME: New Start Time");

                headblk.addField("INFO").
                setHidden().
		setFunction("''");
                
		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWMOVEDLG: MOVE Operations"));
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
                headlay.setEditable();
		headlay.setDialogColumns(1);   
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWMOVEOPERATIONTITLE: Move Operation";
	}

	protected String getTitle()
	{
		return "PCMWMOVEOPERATIONTITLE: Move Operation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

                appendToHTML(headlay.show());
                appendToHTML("<table id=\"SND\" border=\"0\">\n");
                appendToHTML("<tr>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWMOVEOPERDLGBACK: OK"),"OK"));
                appendToHTML("</td>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawButton("CANCEL",mgr.translate("PCMWMOVEOPERDLGCANCEL: Cancel"),"onClick='window.close();'"));
                appendToHTML("</td>\n");
                appendToHTML("</tr>\n");
                appendToHTML("</table>\n");
                
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
                
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(closewin);		//XSS_Safe AMNILK 20070717	
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(!mgr.isEmpty(qryStr));
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr));		//XSS_Safe AMNILK 20070717
                appendDirtyJavaScript("' + \"&PREDECESSORS=1\",'");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));	//XSS_Safe AMNILK 20070717
                appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));	//XSS_Safe AMNILK 20070717
                appendDirtyJavaScript("','");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));	//XSS_Safe AMNILK 20070717
                appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");


	       	}
}
