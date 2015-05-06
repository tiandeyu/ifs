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
*  File        : EnterCancelCauseDlg.java 
*  Created     : CHAMLK  021218
*  Modified    :
*  CHAMLK  020101  Modified code inorder to return to the work order with 
*                  out modifications after cancel edit.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041001  LCS Merge:46394
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  060227  Modified submit(), run(), printContents() and preDefine(). (Call 135574)
*  NAMELK  070420  Modified run() javascript for okFlag modified.
*  ASSALK  070604  CALL 144849: Modified preDefine().
*  ILSOLK  070612  Modified getContents().(Call ID 146120.)
*  ASSALK  070626  Call ID: 144849. Modified getContents().
*  ILSOLK  070711  Eliminated XSS.
*  ILSOLK  070730  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class EnterCancelCauseDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.EnterCancelCauseDlg");

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
	private ASPBuffer row;
	private ASPBuffer buff;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String sWoNo;
	private String desc;
	private ASPTransactionBuffer trans;
	private String cancelFlag;
	private String okFlag;

        private boolean bFirstRequest;
        private String winName;
        private String qryStr;
        private String sCaller = "";
	//===============================================================
	// Construction 
	//===============================================================
	public EnterCancelCauseDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		sWoNo =  desc ;
		desc =  "";
		ASPManager mgr = getASPManager();

		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		sWoNo = ctx.readValue("SWONO",sWoNo);  
		cancelFlag =  ctx.readValue("CANCELFLAG","FALSE");                         
		okFlag =  ctx.readValue("OKFLAG","FALSE");
                winName = ctx.readValue("WINNAME","");                         
                qryStr = ctx.readValue("QRYSTR","");
                sCaller = ctx.readValue("CALLER","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) ||!mgr.isExplorer()){
                    sWoNo = mgr.readValue("WO_NO");
                    sCaller = mgr.getQueryStringValue("CALLER");
                    winName = mgr.getQueryStringValue("FRMNAME");
                    qryStr = mgr.getQueryStringValue("QRYSTR");
                    startup();
                }
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO"))){
                    sWoNo = mgr.readValue("WO_NO");
                    bFirstRequest = true;
                    winName = mgr.getQueryStringValue("FRMNAME");
                }
		else if (mgr.dataTransfered())
			okFind();
		adjust();

		ctx.writeValue("SWONO",sWoNo);
		ctx.writeValue("CANCELFLAG",cancelFlag);
		ctx.writeValue("OKFLAG",okFlag);
                ctx.writeValue("WINNAME",winName);                         
                ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("CALLER",sCaller);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		String txt;
		ASPManager mgr = getASPManager();
		//Bug 46394, start
		String strCancelDesc ="";
		ASPTransactionBuffer secBuff;
		boolean securityOk = false;
		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("Cancel_Task_Cause_API");
		secBuff = mgr.perform(secBuff);

		if ( secBuff.getSecurityInfo().itemExists("Cancel_Task_Cause_API") )
			securityOk = true;

		String val = mgr.readValue("VALIDATE");

		if ("CANCEL_TASK_CAUSE".equals(val) )
		{
			if (securityOk)
			{
				ASPCommand cmd = trans.addCustomFunction("CANDESC", "Cancel_Task_Cause_API.Get_Cancel_Task_Cause_Desc", "CANCEL_TASK_CAUSE_DESC" );
				cmd.addParameter("CANCEL_TASK_CAUSE");

				trans = mgr.validate(trans);

				strCancelDesc   = trans.getValue("CANDESC/DATA/CANCEL_TASK_CAUSE_DESC");
			}

			txt = (mgr.isEmpty(strCancelDesc)? "" : strCancelDesc) +"^";

			mgr.responseWrite(txt);
			mgr.endResponse();
		}
	}

	public void  startup()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		//     q.includeMeta("ALL");                                                                                  

		mgr.submit(trans);

		trans.clear();
	}

	public void  submit()
	{
		ASPManager mgr = getASPManager();
		//Bug 46394, start
		ASPTransactionBuffer secBuff;
		boolean securityOk = false;
		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("Work_Order_From_Vim_API");
		secBuff = mgr.perform(secBuff);

		if ( secBuff.getSecurityInfo().itemExists("Work_Order_From_Vim_API") )
			securityOk = true;

		// Check whether a value for cancel task cause has been entered by the user.
		if (!mgr.isEmpty("CANCEL_TASK_CAUSE"))
		{
			trans.clear();
			if (securityOk)
			{
				ASPCommand cmd = trans.addCustomFunction("CNCLCAUSE","Work_Order_From_Vim_API.Get_Task_No","TASKNO");
				cmd.addParameter("WO_NO",sWoNo);

				trans = mgr.perform(trans);

				String taskno = trans.getValue("CNCLCAUSE/DATA/TASKNO");

				trans.clear();
				cmd = trans.addCustomCommand("MODIWO","Work_Order_From_Vim_API.Modify_Work_Order");
				cmd.addParameter("WO_NO",sWoNo);
				cmd.addParameter("TASKNO",taskno);
				cmd.addParameter("CANCEL_TASK_CAUSE",mgr.readValue("CANCEL_TASK_CAUSE"));

				trans = mgr.perform(trans);

                                trans.clear();
                                cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE REAL_F_DATE FROM DUAL");
                                trans = mgr.perform(trans);
                                String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("REAL_F_DATE");

                                trans.clear();
                                cmd = trans.addCustomCommand("WOVIMUPREALDATE","Active_Separate_API.Update_Real_F_Date"); 
                                cmd.addParameter("WO_NO",sWoNo);
                                cmd.addParameter("REAL_F_DATE",sysDate);
                                trans = mgr.perform(trans);

                                trans.clear();
                                cmd = trans.addCustomCommand("WOVIMCANCELWO","Active_Separate_API.Cancel_Work_Order"); 
                                cmd.addParameter("WO_NO",sWoNo);
                                trans = mgr.perform(trans);

			}
		}

		okFlag = "TRUE";

	}

	public void  cancel()
	{
		ASPManager mgr = getASPManager();

		cancelFlag = "TRUE";
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("IDENTITY");
		f.setFunction("Fnd_Session_API.Get_Fnd_User");
		f.setSize(30);
		f.setMaxLength(30);
		f.setHidden();

		f = headblk.addField("CANCEL_TASK_CAUSE");
		f.setSize(14);
		f.setFunction("''");
		f.setLabel("PCMWENTERCANCELCAUSEDLGCANCELTSK: Cancel Task Cause");
		f.setDynamicLOV("CANCEL_TASK_CAUSE_ACCESS2","IDENTITY",600,450);
		f.setUpperCase();
		f.setInsertable();
		f.setCustomValidation("CANCEL_TASK_CAUSE","CANCEL_TASK_CAUSE_DESC");

		f = headblk.addField("CANCEL_TASK_CAUSE_DESC");
		f.setFunction("''"); 
		f.setSize(35);
		f.setMaxLength(35);
                f.setReadOnly();
		f.setInsertable();
		f.setLabel("PCMWENTERCANCELCAUSEDLGDESC: Description");

		f = headblk.addField("WO_NO");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TASKNO");
		f.setFunction("''");
		f.setHidden();

                f = headblk.addField("REAL_F_DATE", "Datetime");
                f.setFunction("''");
                f.setHidden();

		headblk.setView("DUAL");
		//headblk.defineCommand("","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.disableCommand(headbar.SAVENEW);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.setDialogColumns(1);

		headtbl = mgr.newASPTable(headblk);
	}


	public void  adjust()
	{
		ASPManager mgr = getASPManager();
	}

//===============================================================
//  HTML
//===============================================================

	protected String getDescription()
	{
		return "ENTERCANCELCAUSEDLGCNCLTITLE: Cancellation";
	}

	protected String getTitle()
	{
		return "ENTERCANCELCAUSEDLGCNCLTITLE: Cancellation";
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
            out.append(mgr.generateHeadTag(mgr.translate("ENTERCANCELCAUSEDLGCNCLTITLE: Cancellation")));
            out.append("</head>\n");
            out.append("<body ");
            out.append(mgr.generateBodyTag());
            out.append(">\n");
            out.append("<form ");
            out.append(mgr.generateFormTag());
            out.append(">\n");
        
            out.append(mgr.startPresentation(mgr.translate("ENTERCANCELCAUSEDLGCNCLTITLE: Cancellation")));
        
            out.append(headlay.show());  
            
            if ("ActiveSeprepInWO".equals(winName))
            {
                appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr)); // XSS_Safe ILSOLK 20070711
		appendDirtyJavaScript("' + \"&CANCELCAUSEDLG=1\",'ReportInWO'");
                appendDirtyJavaScript(",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
            }
            else if("ActiveSeprepInWOSM".equals(winName))
            {
                appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr)); // XSS_Safe ILSOLK 20070711
		appendDirtyJavaScript("' + \"&CANCELCAUSEDLG=1\",'ReportInWOSM'");
                appendDirtyJavaScript(",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
            }
            /*else
	    {
                appendDirtyJavaScript("   window.opener.refreshMaint();\n");
	    }*/
	    
            
            if ("TRUE".equals(okFlag))
	    {
               if ("ActiveSeparate2".equals(sCaller))
                  appendDirtyJavaScript("window.opener.refreshWorkOrder();\n");
	       appendDirtyJavaScript("self.close();");
		    
	    }

	    if ("TRUE".equals(cancelFlag))
	    {
	       appendDirtyJavaScript("window.close();");
		    
	    }

            
            out.append(mgr.endPresentation());
            out.append("</form>\n");
            out.append("</body>\n");
            out.append("</html>");
            return out;
         }     
        
        
        }

