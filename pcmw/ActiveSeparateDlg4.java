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
*  File        : ActiveSeparateDlg4.java 
*  Created     : 
*  Modified    :
*  CHCRLK  080118  Winglet, created.
*  ASSALK  080506  Winglet merge in to APP75.
*  ASSALK  080526  Corrections done.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class ActiveSeparateDlg4 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateDlg4");

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
	private ASPTransactionBuffer trans;
	private String title;
	private boolean checkok;
        private boolean checkCancel;
        private boolean bFirstRequest;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparateDlg4(ASPManager mgr, String page_path)
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
		checkok = ctx.readFlag("CHECKOK",false);
                checkCancel = ctx.readFlag("CHECKCANCEL",false);
                bFirstRequest = false;

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer())
                {
                        woNo = mgr.getQueryStringValue("WO_NO");
                        startup();
                }
                else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
                        bFirstRequest = true;

                if (!bFirstRequest)
                        adjust();

		ctx.writeFlag("CHECKOK",checkok);
                ctx.writeFlag("CHECKCANCEL",checkCancel);
		ctx.writeValue("CTX1",woNo);
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
		trans.clear();
                                                                        
                ASPCommand cmd = trans.addCustomCommand("RESCHED","Maint_Scheduling_Int_API.Schedule_Wo_Structure");
		cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
		if (mgr.isEmpty(mgr.readValue("PROJECT_ID")))
		   cmd.addParameter("TOP_WO_NO",mgr.readValue("TOP_WO_NO"));
		else
		   cmd.addParameter("TOP_WO_NO","");
		cmd.addParameter("PROJECT_ID",mgr.readValue("PROJECT_ID"));
		cmd.addParameter("CBRELEASED",mgr.readValue("CBRELEASED"));
		cmd.addParameter("CBREASSIGN",mgr.readValue("CBREASSIGN"));
		cmd.addParameter("CBCOMPRESS",mgr.readValue("CBCOMPRESS"));
		cmd.addParameter("CBJIT",mgr.readValue("CBJIT"));
		cmd.addParameter("CBREFRESHM",mgr.readValue("CBREFRESHM"));
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
                f.setLabel("PCMWACTIVESEPARATEDLG4WONO: WO No");
                f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("ERR_DESCR");
                f.setLabel("PCMWACTIVESEPARATEDLG4ERRDESCR: Directive");
                f.setSize(50);
                f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setLabel("PCMWACTIVESEPARATEDLG4CONTRACT: Site");
		f.setUpperCase();
		f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("PROJECT_ID");
		f.setLabel("PCMWACTIVESEPARATEDLG4PROJECTID: Project ID");
		f.setFunction("PCM_CBS_INT_API.Get_Project_Id(:WO_NO)");
		f.setUpperCase();
		f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("DESCRIPTION");
		f.setLabel("PCMWACTIVESEPARATEDLG4DESC: Description");
		f.setFunction("PROJECT_API.Get_Description(PCM_CBS_INT_API.Get_Project_Id(:WO_NO))");
		f.setSize(50);
                f.setReadOnly();
                
		f = headblk.addField("TOP_WO_NO","Number","#");
		f.setLabel("PCMWACTIVESEPARATEDLG4TOPWONO: Top WO No");
		f.setFunction("WORK_ORDER_CONNECTION_API.Get_Top_Parent(:WO_NO)");
		f.setSize(10);
                f.setReadOnly();

		f = headblk.addField("TOP_ERR_DESCR");
		f.setLabel("PCMWACTIVESEPARATEDLG4TOPERRDESCR: Directive");
		f.setFunction("ACTIVE_SEPARATE_API.Get_Err_Descr(WORK_ORDER_CONNECTION_API.Get_Top_Parent(:WO_NO))");
		f.setSize(50);
                f.setReadOnly();

		f = headblk.addField("TOP_CONTRACT");
		f.setLabel("PCMWACTIVESEPARATEDLG4TOPCONTRACT: Site");
                f.setFunction("ACTIVE_SEPARATE_API.Get_Contract(WORK_ORDER_CONNECTION_API.Get_Top_Parent(:WO_NO))");
		f.setUpperCase();
		f.setSize(10);
                f.setReadOnly();
                
                f = headblk.addField("CBRELEASED");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG4CBRELEASED: Include Released Orders");
                f.setFunction("''");
                
                f = headblk.addField("CBREASSIGN");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG4CBREASSIGN: Re-assign Resources");
                f.setFunction("''");

                f = headblk.addField("CBCOMPRESS");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG4CBCOMPRESS: Compress Downstream from Bottleneck");
                f.setFunction("''");

                f = headblk.addField("CBJIT");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG4CBJIT: As Late as Possible");
                f.setFunction("''");

                f = headblk.addField("CBREFRESHM");
                f.setCheckBox("FALSE,TRUE");       
                f.setLabel("PCMWACTIVESEPARATEDLG4CBREFRESHM: Refresh Materials");
                f.setFunction("''");
                
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
		headlay.defineGroup("","WO_NO,ERR_DESCR,CONTRACT",false,true);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLG4GRP1: Project"),"PROJECT_ID,DESCRIPTION",true,true);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLG4GRP2: Work Order Structure"),"TOP_WO_NO,TOP_ERR_DESCR,TOP_CONTRACT",true,true);
		headlay.defineGroup("","CBREASSIGN,CBRELEASED,CBJIT,CBCOMPRESS,CBREFRESHM",false,true,2);
                
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
		title = mgr.translate("PCMWACTIVESEPARATEDLG4TITLE: Reschedule Work Order Structure/Project");

                if (headset.countRows() == 1) {
		   if (!mgr.isEmpty(headset.getValue("PROJECT_ID")))
		   {
		      headset.setValue("TOP_WO_NO","");
		      headset.setValue("TOP_ERR_DESCR","");
		      headset.setValue("TOP_CONTRACT","");
		   }
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
		return "PCMWACTIVESEPARATEDLG4TITLE: Reschedule Work Order Structure/Project";
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
                out.append(mgr.generateHeadTag(mgr.translate("PCMWACTIVESEPARATEDLG4TITLE: Reschedule Work Order Structure/Project")));
                out.append("</head>\n");
                out.append("<body ");
                out.append(mgr.generateBodyTag());
                out.append(">\n");
                out.append("<form ");
                out.append(mgr.generateFormTag());
                out.append(">\n");
                out.append("<p>\n");
                out.append(mgr.startPresentation(mgr.translate("PCMWACTIVESEPARATEDLG4TITLE: Reschedule Work Order Structure/Project")));
                out.append(headbar.showBar());
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

                out.append(mgr.endPresentation());
                out.append("</form>\n");
                out.append("</body>\n");
                out.append("</html>");
                return out;
        }
}
