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
*  File        : NotesDlg.java 
*  Created     : AMDILK  060407  Created ( Call Id 144681 )
*  Modifed     :
*  100330   NIJALK   Bug 87935, Modified submit().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class NotesDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.NotesDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	
        private ASPHTMLFormatter fmt;    
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;
	private ASPContext ctx;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	
        private ASPTransactionBuffer trans;
	private String cancelFlag;
	private ASPCommand cmd;
	private int rows;
	private ASPQuery q;
	
        String sNotetxt;
	String sObjId;
	String sObjVersion;
	String sIdentification;
	String sReqNo;
	String sWoNo;
	String sLineNo;
	String sReleaseNo;

	//===============================================================
	// Construction 
	//===============================================================

	public NotesDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
	        ASPManager mgr = getASPManager();

		fmt   = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();
		ctx   = mgr.getASPContext();
		
		sReqNo           = ctx.readValue("REQNO", "");
		sWoNo            = ctx.readValue("WONO", "");
		sReleaseNo       = ctx.readValue("RELEASENO", "");
		sLineNo          = ctx.readValue("LINENO", "");
                sNotetxt         = ctx.readValue("NOTETXT", ""); 
                sObjId           = ctx.readValue("OBJID", ""); 
	        sObjVersion      = ctx.readValue("OBJVERSION", ""); 
                sIdentification  = ctx.readValue("IDENTIFICATION", ""); 

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("NOTETXT")))
		{
		        
			sReqNo          = mgr.getQueryStringValue("REQNO");
			sWoNo           = mgr.getQueryStringValue("WONO");
			sLineNo         = mgr.getQueryStringValue("LINENO");
			sReleaseNo      = mgr.getQueryStringValue("RELEASENO");
			sNotetxt        = mgr.getQueryStringValue("NOTETXT");
			sObjId          = mgr.getQueryStringValue("OBJID");
			sObjVersion     = mgr.getQueryStringValue("OBJVERSION");
			sIdentification = mgr.getQueryStringValue("IDENTIFICATION");
			
			okFind();

			ASPBuffer temp = headset.getRow();
			
			temp.setValue("NOTETXT",sNotetxt);
                        temp.setValue("OBJID",sObjId);
			temp.setValue("OBJVERSION",sObjVersion);
			
			headset.addRow(temp);
		}
		else if (mgr.buttonPressed("FINISH"))
			submit();
		else if (mgr.buttonPressed("CANCEL"))
			cancelFlag = "TRUE";

		adjust();

		ctx.writeValue("REQNO", sReqNo);
		ctx.writeValue("WONO", sWoNo);
		ctx.writeValue("LINENO", sLineNo);
		ctx.writeValue("RELEASENO", sReleaseNo);
		ctx.writeValue("NOTETXT", sNotetxt);
		ctx.writeValue("OBJID", sObjId);
		ctx.writeValue("OBJVERSION", sObjVersion);
		ctx.writeValue("IDENTIFICATION", sIdentification);

	}


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
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


	public void submit()
	{
		ASPManager mgr     = getASPManager();
		String sAttr       = "";
		String sInfo       = "";
		String sObjversion = "";

		headset.changeRows();
		trans.clear();

		String note = null ;

		if ( ! ( mgr.isEmpty( mgr.readValue("OBJID")) && mgr.isEmpty( mgr.readValue("OBJVERSION")) ))
		{
                   sAttr = "IFSAPP.Purchase_Req_Util_API.Get_Line_Note_Text(REQUISITION_NO, LINE_NO, RELEASE_NO)" ;
		   sAttr = sAttr + (char)31 + mgr.readValue("NOTETXT") + (char)30;
                   
		   trans.clear();
		   //Bug 87935, Start, Rewrite the code to get correct pres objects
                   if ( "PARTS".equals(sIdentification) ) 
		   {
		      cmd = trans.addCustomCommand("MODIFY", "PART_WO_REQUIS_LINE_API.Modify__");
		      cmd.addParameter("INFO");
		      cmd.addParameter("OBJID", sObjId);
		      cmd.addParameter("OBJVERSION", sObjVersion);
		      cmd.addParameter("ATTR", sAttr);
		      cmd.addParameter("ACTION", "DO");       
		   }
		   else if ( "NOPARTS".equals(sIdentification) ) 
		   {
		      cmd = trans.addCustomCommand("MODIFY", "NOPART_WO_REQUIS_LINE_API.Modify__");
		      cmd.addParameter("INFO");
		      cmd.addParameter("OBJID", sObjId);
		      cmd.addParameter("OBJVERSION", sObjVersion);
		      cmd.addParameter("ATTR", sAttr);
		      cmd.addParameter("ACTION", "DO");       
		   }
		   //Bug 87935, End
     
	   	   trans = mgr.perform(trans);
                    
		}

		cancelFlag = "TRUE";  
		trans.clear();

	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("OBJVERSION");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("INFO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ATTR");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ACTION");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("REQUISITION_NO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WO_NO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("LINE_NO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("RELEASE_NO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("NOTETXT");
		f.setSize(40);
		f.setLabel("PCMWNOTESNOTE: Notes");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setMaxLength(2000);
		f.setHeight(4);

		
		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.VIEWDETAILS);
                headbar.disableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.SAVENEW);
		headbar.disableCommand(headbar.CANCELNEW);
                headbar.disableCommand(headbar.FORWARD);

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
		headlay.setDialogColumns(2);
		headtbl.disableRowStatus();

                enableConvertGettoPost();
	}

	public void adjust()
	{

		ASPManager mgr = getASPManager();

	}

//===============================================================
//  HTML
//===============================================================

	protected String getDescription()
	{
		return "PCMWUNOTESDESC: Notes";
	}

	protected String getTitle()
	{
		return "PCMWNOTESTITLE: Notes";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("  <table  border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>\n");
		appendToHTML("   <tr>\n");
		 appendToHTML("      <td nowrap height=\"26\" align=\"right\" width = '8%' >");
		appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWAUTHORIZECODINGDLGSMFINISH1: Ok"),"submit"));
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("      <td nowrap height=\"26\" align=\"left\" width = '8%' >");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWAUTHORIZECODINGDLGSMCANCEL: Cancel"),"submit"));
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
		appendToHTML("</td>   \n");
		appendToHTML("   </tr>\n");
		appendToHTML("  </table>\n");
                

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(cancelFlag);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

                
	}
}
