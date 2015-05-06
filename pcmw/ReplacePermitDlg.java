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
*  File        : ReplacePermitDlg.java 
*  ASP2JAVA Tool  2001-03-22  - Created Using the ASP file ReplacePermitDlg.asp
*  Modified    :  2001-03-23   BUNI Corrected some conversion errors.
*  031222      ARWILK   Edge Developments - (Removed clone and doReset Methods)
*  040824      ThWilk   Call 116923, modified run(),submit() and added javascripts.
*  040831      NIJALK   Call 117419, modified preDefine(), submit().
*  040921      NIJALK   Modified submit().
*  040929      NIJALK   Modified preDefine().
*  050921      NEKOLK   Call 126826:Modified submit().
*  070523      CHANLK   Call 144621 Error msg does not popup in IE Browser.
*  070725      AMNILK   Eliminated XSS Security Vulnerability.
*  070803      AMDILK   refreshed the parent after submit
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ReplacePermitDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ReplacePermitDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPBlockLayout headlay;
   private ASPHTMLFormatter fmt;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String wo_no;
	private String start;
        private boolean state;
	private String per_seq;
	private boolean win_cls;
	private boolean ok_sub;
	private ASPTransactionBuffer trans;
        private String primary_key;
	private String fname;
	private ASPBuffer buff;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;
	private ASPBuffer row; 
        private boolean refreshParent ;

   private boolean bFirstRequest;
	//===============================================================
	// Construction 
	//===============================================================
	public ReplacePermitDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		wo_no =  "";
		per_seq =  "";
                refreshParent = false;

		ASPManager mgr = getASPManager();
                fmt = mgr.newASPHTMLFormatter();   

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
                primary_key = ctx.getGlobal("PRIMARY_KEY");
		wo_no = ctx.readValue("WO_NO","");
		per_seq = ctx.readValue("PERMIT_SEQ",""); 
		fname= ctx.readValue("FORM_NAME","");
		start = ctx.readValue("START", "");
		win_cls = ctx.readFlag("WINCLS",win_cls);
		ok_sub = ctx.readFlag("OKSUB",ok_sub);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
	        else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) ||!mgr.isExplorer())
		{
		       per_seq = mgr.readValue("PERMIT_SEQ");
		       start = mgr.readValue("START");
		       wo_no = mgr.readValue("WO_NO");
    
		       okFind();
    
		       ASPBuffer temp = headset.getRow();
		       temp.setValue("OLDPERMITSEQ",per_seq);
		       headset.addRow(temp);
		}
	        else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_SEQ")) && !mgr.isEmpty(mgr.getQueryStringValue("START")))
	               bFirstRequest = true;
		else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_SEQ")))
		{
			per_seq = mgr.readValue("PERMIT_SEQ");
			wo_no = mgr.readValue("WO_NO");
			fname = mgr.readValue("FRMNAME");

			okFind();

			ASPBuffer temp = headset.getRow();
			temp.setValue("OLDPERMITSEQ",per_seq);
			headset.addRow(temp);
		}

		ctx.writeValue("WO_NO",wo_no);
		ctx.writeValue("PERMIT_SEQ",per_seq); 
		ctx.writeValue("FORM_NAME",fname);
		ctx.writeFlag("WINCLS",win_cls);
		ctx.writeValue("START", start);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		if (!mgr.isEmpty(mgr.readValue("NEWPERMITSEQ")))
		{
                    cmd = trans.addCustomFunction("NTYPE","Permit_API.Get_Permit_Type_Id","NTYP");
                    cmd.addParameter("NEWPERMITSEQ",mgr.readValue("NEWPERMITSEQ"));
                    
                    trans = mgr.perform(trans);

                    String ntyp = trans.getValue("NTYPE/DATA/NTYP");

                    trans.clear();
                    
                        cmd = trans.addCustomCommand("VAL1","Work_Order_Permit_API.Replace_Permit__");
			cmd.addParameter("PERMIT_SEQ",mgr.readValue("OLDPERMITSEQ"));
			cmd.addParameter("WO_NO",primary_key);
			cmd.addParameter("NEWPERMITSEQ",mgr.readValue("NEWPERMITSEQ"));
                         
                        trans=mgr.perform(trans);

                        refreshParent = true;

//                        if (!mgr.isEmpty(start))
//			    state = true;
//			else
//			    ok_sub = true;
//
	       	}
		else
		{
			mgr.showAlert(mgr.translate("PCMWREPLACEPERMITDLGERROR: New Permit Seq should have a value"));

		}
	}

	public void  cancel()
	{
		win_cls = true;
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
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

		f = headblk.addField("OLDPERMITSEQ", "Number","#");
		f.setSize(20);
                f.setMaxLength(6);
		f.setDynamicLOV("PERMIT_LOV",600,450);
		f.setLabel("PCMWREPLACEPERMITDLGOLDPERMITSEQ: Old Permit Seq");
		f.setLOVProperty("TITLE",mgr.translate("PCMWREPLACEPERMITDLGOLDPS: Old Permit Seq"));
		f.setMandatory();
		f.setFunction("''");

		f = headblk.addField("NEWPERMITSEQ", "Number","#");
		f.setSize(20);
                f.setMaxLength(6);
		f.setDynamicLOV("PERMIT_LOV",600,450);
		f.setLabel("PCMWREPLACEPERMITDLGNEWPERMITSEQ: New Permit Seq");
		f.setLOVProperty("TITLE",mgr.translate("PCMWREPLACEPERMITDLGNEWPS: New Permit Seq"));
		f.setMandatory();
		f.setFunction("''");

		f = headblk.addField("WO_NO", "Number");
		f.setSize(9);
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("PERMIT_SEQ", "Number");
		f.setSize(9);
		f.setFunction("''");
		f.setHidden();
                
                f = headblk.addField("NTYP");
		f.setFunction("''");
		f.setHidden();

                f = headblk.addField("DUMMY", "Number");
                f.setFunction("''");
                f.setHidden();
                
		headblk.setView("MODULE");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.disableCommand(headbar.FORWARD);

		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
		headlay.setDialogColumns(1);
		headtbl.disableRowStatus();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWREPLACEPERMITTITLE: Replace Permit";
	}

	protected String getTitle()
	{
		return "PCMWREPLACEPERMITTITLE: Replace Permit";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

      if (bFirstRequest && mgr.isExplorer())
      {
             appendToHTML("<html>\n"); 
             appendToHTML("<head></head>\n"); 
             appendToHTML("<body>"); 
             appendToHTML("<form name='form' method='POST'action='"+mgr.getURL()+"?"+mgr.HTMLEncode(mgr.getQueryString())+"'>"); //XSS_Safe AMNILK 20070725
             appendToHTML(fmt.drawHidden("FIRST_REQUEST", "OK"));  
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("PERMIT_SEQ"))+"\" name=\"PERMIT_SEQ\" >"); 
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("START"))+"\" name=\"START\" >"); 
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("WO_NO"))+"\" name=\"WO_NO\" >"); 
	     appendToHTML("</form></body></html>"); 
             appendDirtyJavaScript("document.form.submit();"); 
      } 
      appendToHTML(headlay.show());

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(win_cls);		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(state);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.opener.location = \"ActiveRound.page?WO_NO=\" + URLClientEncode(");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(primary_key));	//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript(") + \"\";\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("else if (");
		appendDirtyJavaScript(ok_sub); 		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.open(\"WorkOrderPermit.page?WO_NO=\"+URLClientEncode(");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(primary_key));	//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("),'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(fname));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("',\"\");\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");

                //Refresh the parent window 
                appendDirtyJavaScript("if (");
		appendDirtyJavaScript(refreshParent);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.opener.refreshPage();\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
