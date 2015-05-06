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
*  File        : ActiveSeparateDlg3.java 
*  Created     : 
*  Modified    :
*  CHCRLK  080110  Winglet, created.
*  ASSALK  080506  Bug 72214, Winglet merge to app75.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class ActiveSeparateDlg3 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateDlg3");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPBlockLayout headlay;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String woNo;
	private String event;
	private ASPTransactionBuffer trans;
	private String title;
	private boolean checkok;
        private boolean checkCancel;
        private boolean checkStartup;
        private boolean bFirstRequest;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparateDlg3(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		woNo = ctx.readValue("CTX1",woNo);
		event = ctx.readValue("EVENT",event);
		checkok = ctx.readFlag("CHECKOK",false);
                checkCancel = ctx.readFlag("CHECKCANCEL",false);
                checkStartup = ctx.readFlag("CHECKSTARTUP",false);
                bFirstRequest = false;

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer())
                {
                        woNo = mgr.getQueryStringValue("WO_NO");
                        event = mgr.getQueryStringValue("EVENT");
                        startup();
                }
                else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
                        bFirstRequest = true;

                if (!bFirstRequest)
                        adjust();

		ctx.writeFlag("CHECKOK",checkok);
                ctx.writeFlag("CHECKCANCEL",checkCancel);
                ctx.writeFlag("CHECKSTARTUP",checkStartup);
		ctx.writeValue("CTX1",woNo);
		ctx.writeValue("EVENT",event);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
        
	public void startup()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
                ASPQuery q = trans.addEmptyQuery(headblk);
                q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
                q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO", woNo);

                q.includeMeta("ALL");
                mgr.querySubmit(trans, headblk);

		ASPBuffer temp = headset.getRow();

                if (!mgr.isEmpty(temp.getValue("PROJECT_ID")) && !mgr.isEmpty(temp.getValue("TOP_WO_NO")))
                {
                        temp.setValue("TOP_WO_NO","");
                        temp.setValue("TOP_ERR_DESCR","");
                        temp.setValue("TOP_CONTRACT","");
                }
                
                if (!mgr.isEmpty(temp.getValue("PROJECT_ID")))
                        temp.setValue("CBPROJECT","TRUE");
                else if (!mgr.isEmpty(temp.getValue("TOP_WO_NO")))
                        temp.setValue("CBSTRUCTURE","TRUE");
                
		headset.setRow(temp);

                checkStartup = true;
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void ok()
	{
		ASPManager mgr = getASPManager();

		headset.changeRow();

                String attr = "STR_TRANSITION" + (char)31 + "TRUE" + (char)30;

                if ("TRUE".equals(headset.getRow().getValue("CBPROJECT")))
                        attr += "TRANSITION_TYPE" + (char)31 + "PROJECT" + (char)30;   
                else if ("TRUE".equals(headset.getRow().getValue("CBSTRUCTURE")))
                        attr += "TRANSITION_TYPE" + (char)31 + "STRUCTURE" + (char)30;
                else if ("TRUE".equals(headset.getRow().getValue("CBSINGLE")))
                        attr += "TRANSITION_TYPE" + (char)31 + "SINGLE" + (char)30;

		trans.clear();
		ASPCommand cmd = trans.addCustomCommand("CHGSTAT", "Active_Separate_API."+event);
		cmd.addParameter("RESULT","");
		cmd.addParameter("OBJID",mgr.readValue("OBJID"));
		cmd.addParameter("OBJVERSION",mgr.readValue("OBJVERSION"));
		cmd.addParameter("ATTR",attr);
		cmd.addParameter("ACTION","DO");
		trans = mgr.perform(trans);
		trans.clear();

		checkok = true;
	}

	public void cancel()
	{
		checkCancel = true;
	}

//-----------------------------------------------------------------------------
//--------------------------------  PREDEFINE----------------------------------
//-----------------------------------------------------------------------------

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("WO_NO","Number","#");
                f.setLabel("PCMWACTIVESEPARATEDLG3WONO: WO No");
                f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("ERR_DESCR");
                f.setLabel("PCMWACTIVESEPARATEDLG3ERRDESCR: Directive");
                f.setSize(50);
                f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setLabel("PCMWACTIVESEPARATEDLG3CONTRACT: Site");
		f.setUpperCase();
		f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("PROJECT_ID");
		f.setLabel("PCMWACTIVESEPARATEDLG3PROJECTID: Project ID");
		f.setFunction("PCM_CBS_INT_API.Get_Project_Id(:WO_NO)");
		f.setUpperCase();
		f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("DESCRIPTION");
		f.setLabel("PCMWACTIVESEPARATEDLG3DESC: Description");
		f.setFunction("PROJECT_API.Get_Description(PCM_CBS_INT_API.Get_Project_Id(:WO_NO))");
		f.setSize(50);
                f.setReadOnly();
                
                f = headblk.addField("CBPROJECT");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG3CBPROJECT: Set status of entire Project");
                f.setValidateFunction("validateCheckbox");
                f.setFunction("''");
                
		f = headblk.addField("TOP_WO_NO","Number","#");
		f.setLabel("PCMWACTIVESEPARATEDLG3TOPWONO: Top WO No");
		f.setFunction("WORK_ORDER_CONNECTION_API.Get_Top_Parent(:WO_NO)");
		f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("TOP_ERR_DESCR");
		f.setLabel("PCMWACTIVESEPARATEDLG3TOPERRDESCR: Directive");
		f.setFunction("ACTIVE_SEPARATE_API.Get_Err_Descr(WORK_ORDER_CONNECTION_API.Get_Top_Parent(:WO_NO))");
		f.setSize(50);
                f.setReadOnly();

		f = headblk.addField("TOP_CONTRACT");
		f.setLabel("PCMWACTIVESEPARATEDLG3TOPCONTRACT: Site");
                f.setFunction("ACTIVE_SEPARATE_API.Get_Contract(WORK_ORDER_CONNECTION_API.Get_Top_Parent(:WO_NO))");
		f.setUpperCase();
		f.setSize(10);
                f.setReadOnly();
                
                f = headblk.addField("CBSTRUCTURE");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG3CBSTRUCTURE: Set status of entire Structure");
                f.setValidateFunction("validateCheckbox");
                f.setFunction("''");

                f = headblk.addField("CBSINGLE");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG3CBSINGLE: Set status of single Work Order");
                f.setFunction("''");
                
		f = headblk.addField("EVENT");
                f.setFunction("''");
		f.setHidden();

		f = headblk.addField("RESULT");
                f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR");
                f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ACTION");
                f.setFunction("''");
		f.setHidden();

		headblk.setView("ACTIVE_SEPARATE");
		headblk.defineCommand("ACTIVE_SEPARATE_API","Modify__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DELETE);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup("","WO_NO,ERR_DESCR,CONTRACT",true,true);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLG3GRP2: Project"),"PROJECT_ID,DESCRIPTION,CBPROJECT",true,true);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLG3GRP3: Work Order Structure"),"TOP_WO_NO,TOP_ERR_DESCR,TOP_CONTRACT,CBSTRUCTURE",true,true);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLG3GRP4: Single Work Order"),"CBSINGLE",true,true);
                
                headlay.setDialogColumns(1);   
                headlay.setSimple("ERR_DESCR");
                headlay.setSimple("DESCRIPTION");
                headlay.setSimple("TOP_ERR_DESCR");

		headbar.defineCommand(headbar.SAVERETURN,"ok");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");        
	}
        
	public void adjust()
	{
		ASPManager mgr = getASPManager();
		title = mgr.translate("PCMWACTIVESEPARATEDLG3TITLE: Change Work Order Status and Schedule Work Orders");

                if (headset.countRows() > 0)
                {
                        if (!mgr.isEmpty(headset.getRow().getValue("PROJECT_ID")))
                                mgr.getASPField("CBPROJECT").unsetReadOnly();
                        else
                                mgr.getASPField("CBPROJECT").setReadOnly();
                
                        if (!mgr.isEmpty(headset.getRow().getValue("TOP_WO_NO")))
                                mgr.getASPField("CBSTRUCTURE").unsetReadOnly();
                        else
                                mgr.getASPField("CBSTRUCTURE").setReadOnly();
                }
	}


//===============================================================
//  HTML
//===============================================================
	
        protected String getDescription()
	{
		return title;
	}

	protected String getTitle()
	{
		return "PCMWACTIVESEPARATEDLG3TITLE: Change Work Order Status and Schedule Work Orders";
	}

        protected AutoString getContents() throws FndException
        { 
                ASPManager mgr = getASPManager();
                AutoString out = getOutputStream();
                out.clear();

                if (bFirstRequest && mgr.isExplorer())
                {
                        out.append("<html>\n"); 
                        out.append("<head></head>\n"); 
                        out.append("<body>"); 
                        out.append("<form name='form' method='POST'action='"+mgr.getURL()+"?"+mgr.getQueryString()+"'>"); 
                        out.append(fmt.drawHidden("FIRST_REQUEST", "OK"));            
                        out.append("</form></body></html>"); 
                        appendDirtyJavaScript("document.form.submit();"); 
                        return out;
                }
                out.append("<html>\n");
                out.append("<head>\n");
                out.append(mgr.generateHeadTag(mgr.translate("PCMWACTIVESEPARATEDLG3TITLE: Change Work Order Status and Schedule Work Orders")));
                out.append("</head>\n");
                out.append("<body ");
                out.append(mgr.generateBodyTag());
                out.append(">\n");
                out.append("<form ");
                out.append(mgr.generateFormTag());
                out.append(">\n");
                out.append("<p>\n");
                out.append(mgr.startPresentation(mgr.translate("PCMWACTIVESEPARATEDLG3TITLE: Change Work Order Status and Schedule Work Orders")));
                out.append(headbar.showBar());

                appendToHTML("<table cellspacing=0 cellpadding=0 border=0 width=100%>\n");
                appendToHTML("<tr><td>&nbsp;&nbsp;</td>\n");
                appendToHTML("<td width=100%>\n");
                appendToHTML("<table border=0 width=100% id=\"cntHEAD\" class=\"BlockLayoutTable\" cellspacing=0 cellpadding=0>\n");
                appendToHTML("<tr><td align=\"left\" valign=\"top\" width=100%>\n");
                appendToHTML(fmt.drawReadLabel(mgr.translate("PCMWACTIVESEPARATEDLG3TXT1: This work order is part of a work order structure or is part of a project or both. Please tick the relevant checkboxes if you want to change the status of all the work orders in the structure and/or project. The work orders will also be automatically scheduled using the IFS Constraint Based Scheduling server. This might take a while.")));
		appendToHTML("</td></tr>\n");
		appendToHTML(" </table>\n");
		appendToHTML("</td></tr>\n");
		appendToHTML(" </table>\n");

                out.append(headlay.generateDataPresentation());

                if (checkok)
                {  			
                        appendDirtyJavaScript("  window.opener.commandSet('HEAD.refreshForm','');\n");
                        appendDirtyJavaScript("  window.close();\n");
                }

                if (checkCancel)
                {
                        appendDirtyJavaScript("  window.close();\n");
                }
                
                if (checkStartup)
                {
                    appendDirtyJavaScript("    if(f.CBSTRUCTURE.checked || f.CBPROJECT.checked)\n");
                    appendDirtyJavaScript("{\n");
                    appendDirtyJavaScript("          f.CBSINGLE.checked = false;\n");
                    appendDirtyJavaScript("          f.CBSINGLE.disabled = true;\n");
                    appendDirtyJavaScript("}\n");
                    appendDirtyJavaScript("    else\n");
                    appendDirtyJavaScript("          f.CBSINGLE.disabled = false;\n");
                }

                appendDirtyJavaScript("function validateCheckbox()");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("    if(f.CBSTRUCTURE.checked || f.CBPROJECT.checked)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("          f.CBSINGLE.checked = false;\n");
                appendDirtyJavaScript("          f.CBSINGLE.disabled = true;\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("    else\n");
                appendDirtyJavaScript("          f.CBSINGLE.disabled = false;\n");
                appendDirtyJavaScript("}\n");

                out.append(mgr.endPresentation());
                out.append("</form>\n");
                out.append("</body>\n");
                out.append("</html>");
                return out;
        }
}
