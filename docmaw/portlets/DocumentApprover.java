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
 * File        : DocumentApprover.java
 * Description : Approve Documents Portlet
 * Notes       :
 * ----------------------------------------------------------------------------
 * Author       : Sandaruwan Wijenayake
 * Date created : 05-Apr-2000
 * Files Called :
 * Modified By :
 *    Date        Sign                 History
 *    14-Apr-2000  Sandaruwan Wijenayake  Add changes to support IFS Webkit 300b2
 *    23-May-2000  Sandaruwan Wijenayake  Translations. Change where condition ( only Approval in progress )
 *    08-Jun-2000  Sandaruwan Wijenayake  Call ID 43368 - Corrected bug in the Reject Step action.
 *    08-Jun-2000  Sandaruwan Wijenayake  Improve efficiency by directly calling View Document and Remark Document from javascript.
 *    12-Jun-2000  Sandaruwan Wijenayake  Call ID 43627 - Commented the Document Remark functionality.
 *    22-Jun-2000  Sandaruwan Wijenayake  Added next/previous functionality.
 *    22-Jun-2000  Sandaruwan Wijenayake  Corrected minimized title to display no fo db rows.
 *    23-Jun-2000  Sandaruwan Wijenayake  Call ID 45333 - Set comment when approve or reject a document step.
 *    13-Jul-2000  Sandaruwan Wijenayake  Call ID 45333 - Set comment when approve or reject a document step.
 *
 *                 Chanaka  Webkit 300A Beta1 change of printTableCell used for other operation than in pure text.
 *    11-Dec-2000  ShDi     Call ID 76741 -  users who haven't administrator rights are also allowed to approve the document
 *    25-May_2001  Baka     Call Id 65259 -  Stoped a document being not visible even after current step rejected.
 *                                           put a checkbox in each row to mark rejection,
 *    01-Jan-2003  Prsalk   Added Doc_Sheet (Sheet Number)
 *    2003-04-30   MDAHSE   Code cleanup.
 *    2003-08-04   DIKALK   Fixed/structured the client-side javascripts functions to
 *                          work properly and did some more code cleanup.
 *    2005-07-29   SHTHLK   Merged bug Id 51404, Get the encoded server value for approval status.
 *    2005-11-09 : AMNALK   Fixed Call 128624.
 *    2006-01-23   RUCSLK   Merged Bug Id 55196
 *    2006-03-18   DIKALK   Cleaned this page, and moved all logic pertaining to approving
 *                          and rejecting steps to DocAppPortCommentDlg.java. This was necessary
 *                          because the Web Client couldn't deal with Security Checkpoints in portlets
 *    2006-03-18   CHODLK   Merged Patch No: 56023 Unable to see document approval step in portlet if no file is attached.
 *    2006-09-12   CHODLK   Bug 59663, Modified method printCustomTable().
 *   2007-08-08    JANSLK   Changed all non-translatable constants.
 *    2007-08-15   ASSALK   Merged Bug 58526, Added dummy column DOCUMENT.
 *------------------------------------------------------------------------------------
 * -----------------------APP7.5 SP1-------------------------------------
 *   071213       ILSOLK    Bug Id 68773, Eliminated XSS.
 *   2008-01-07   AMNALK    Bug Id 70360, Removed the mgr.HTMLEncode() added by the bug 68773 from the check box reject.
 *   2008-04-17   SHTHLK    Bug Id 70286, Enabled the gifs only if the users have rights for the methods
 *   2008-08-04   AMNALK    Bug Id 75783, Modified preDefine() & printCustomTable() to send the note field to the approve/reject page.
 *   2009-10-05   AMNALK    Bug Id 85713, Modified printCustomTable() to support backslash in document keys.
 *   2009-10-16   SHTHLK    Bug Id 86536, Modified printCustomTable() to encod the keys.
 *   2010-06-03   DULOLK    Bug Id 91134, Used mgr.encodeStringForJavascript() to encode string values sent to javascript.
 *                                        Used URLClientEncode() js method to encode client values.
 * -----------------------------------------------------------------------
 */

package ifs.docmaw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.docmaw.DocmawUtil;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;


public class DocumentApprover extends ASPPortletProvider
{

	public static boolean DEBUG = true;
	private final static int size = 10;

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;
	private String     reject;
	//Bug Id 70286, Start
	private boolean    bcanApproveRejct =false;
	private boolean    bcanViewDocument =false;
	//Bug Id 70286, End
	private transient int skip_rows;
	private transient int db_rows;


	public DocumentApprover(ASPPortal portal, String clspath)
	{
		super(portal, clspath);
	}


	public ASPPage construct() throws FndException
	{
		return super.construct();
	}


	protected void preDefine()
	{
		ctx = getASPContext();

		blk = newASPBlock("DOCAPP");

		addField(blk, "LU_NAME").
		setHidden();

		addField(blk, "KEY_REF").
		setHidden();

		addField(blk, "LINE_NO","Number").
		setHidden();

		addField(blk, "STEP_NO","Number").
		setHidden();

		addField(blk, "OBJSTATE").
		setHidden();

		addField(blk, "APP_STATUS_DB").
		setHidden().
		setFunction("''");

		addField(blk, "DOC_CLASS").
		setHidden();

		addField(blk, "DOC_NO").
		setHidden();

		addField(blk, "DOC_REV").
		setHidden();

		addField(blk, "DOC_SHEET").
		setHidden();

		addField(blk, "EDM_STATE").
		setHidden();

		addField(blk,"MARKREGECTED","Number").
		setReadOnly().
		setCheckBox("0,1").
		setFunction("NVL(Approval_Status_API.Encode(APPROVAL_ROUTING_API.Get_Approval_Status(:LINE_NO,:STEP_NO,:LU_NAME,:KEY_REF)),'FOR APP')").
		setLabel("DOCMAWPORTLETSDOCUMENTAPPROVERREGECTED: Rejected");

		addField(blk, "DOC_KEY").
		setFunction("DOC_CLASS||'-'||DOC_NO||'-'||DOC_SHEET||'-'||DOC_REV").
		setLabel("DOCMAWPORTLETSDOCUMENTAPPROVERDOCKEY: Doc Key");

		addField(blk, "TITLE").
		setLabel("DOCMAWPORTLETSDOCUMENTAPPROVERTITLE: Title");

		addField(blk, "DESCRIPTION").
		setLabel("DOCMAWPORTLETSDOCUMENTAPPROVERCHECK: Check");

		addField(blk, "EDIT_ACCESS").
		setFunction("DOC_ISSUE_API.Get_Edit_Access_(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
		setHidden();

		addField(blk, "FILE_TYPE").
		setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')").
		setHidden();

                //Bug 58526, Start, This is a dummy column used to connect TERMS.
                addField(blk, "DOCUMENT").
                setLabel("DOCMAWPORTLETSDOCUMENTAPPROVERDOCUMENT: Document").
		setFunction("''").
                setHidden();

		//Bug Id 75783, start
		addField(blk, "NOTE").
		setLabel("DOCMAWPORTLETSDOCUMENTAPPROVERNOTE: Note").
		setHidden();
		//Bug Id 75783, end

		getASPField("DOC_KEY").setHyperlink("docmaw/DocIssue.page","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

		blk.setView("APPROVAL_CURRENT_STEP_DOC");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();
		tbl.unsetEditable();

		rowset = blk.getASPRowSet();
		getASPManager().newASPCommandBar(blk);

		init();
	}


	protected void init()
	{
		String cmd = readValue("CMD");
		String out_row_no   = readValue("ROW_NO");
		String comment = readValue("COMMENTS");

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

		if ( "PREV".equals(cmd) && skip_rows>=size )
		{
			skip_rows -= size;
		}
		else if ( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
		{
			skip_rows += size;
		}

		ctx.writeValue("SKIP_ROWS", skip_rows + "");
	}


	protected void run()
	{
		if (DEBUG) debug(this+": DocumentApprover.run()");

		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

		ASPQuery qry = trans.addQuery(blk);
		qry.setOrderByClause("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		qry.setBufferSize(size);
		qry.skipRows(skip_rows);
		qry.includeMeta("ALL");
		qry.addWhereCondition("ACK_RIGHTS = 'TRUE'");
		qry.addWhereCondition("OBJSTATE = 'Approval In Progress'");
		submit(trans);

		db_rows = blk.getASPRowSet().countDbRows();
		getASPContext().writeValue("DB_ROWS", db_rows+"" );
	}


	public boolean canCustomize()
	{
		return false;
	}


	public static String getDescription()
	{
		return "DOCMAWPORTLETSDOCUMENTAPPROVERDESC: Approve Documents";
	}


	public String getTitle( int mode )
	{
		if (DEBUG) debug(this+": DocumentApprover.getTitle("+mode+")");

		if (mode == ASPPortal.MINIMIZED)
			return translate(getDescription()) + translate("DOCMAWPORTLETSDOCUMENTAPPROVERMINTXT:  - You have &1 document(s) to approve.", Integer.toString(db_rows));
		else
			return translate(getDescription());
	}


	public void printCustomTable() throws FndException
	{
		ASPManager mgr = getASPManager();
		String imageurl;
		String href = "";
	       //Bug Id 70286, Start
		bcanApproveRejct =canViewDocument();
		bcanViewDocument =canApproveRejectDocument();
		//Bug Id 70286, End

		beginTable();
		beginTableTitleRow();
		printTableCell(translate("DOCMAWPORTLETSDOCUMENTAPPROVERDOCUMENT: Document"));
		printTableCell(translate("DOCMAWPORTLETSDOCUMENTAPPROVERTITLE: Title"));
		printTableCell(translate("DOCMAWPORTLETSDOCUMENTAPPROVERAPPROVALSTEPDESCRIPTION: Approval Step Description"));
		printTableCell("");
		printTableCell(translate("DOCMAWPORTLETSDOCUMENTAPPROVERREGECTED: Rejected"));
		endTableTitleRow();
		beginTableBody();

		rowset.first();
		boolean b;
		for (int i=0; i<rowset.countRows(); i++)
		{
			String url =   "docmaw/DocIssue.page"+
								"?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"))+
								"&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"))+
								"&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"))+
								"&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));
			
			beginTableCell();
			printLink(rowset.getRow().getValue("DOC_KEY"),url);
			endTableCell();

			beginTableCell();
			printText(rowset.getValue("TITLE"));
			endTableCell();

			beginTableCell();
			printText(rowset.getValue("DESCRIPTION"));
			endTableCell();

         // Bug Id 91134, Start
			String objstate = rowset.getValue("OBJSTATE");
			String edmstate = rowset.getValue("EDM_STATE");	 //Bug Id 56023

			beginTableCell();
			//Bug 59663, Start, replaced hardcoded images with the methods of ASPPortletProvider
			if (edmstate != null && bcanViewDocument) //Bug Id 70286
			{
            //Bug Id 85713, Added replaceAll() to avoid problems with backslash			
            String sEdmMacroPath  = "'docmaw/EdmMacro.page?";
            sEdmMacroPath += mgr.encodeStringForJavascript("DOC_CLASS=") + "' + URLClientEncode('"+mgr.encodeStringForJavascript(rowset.getValue("DOC_CLASS").replaceAll("\\\\","\\\\\\\\")) + "')";
            sEdmMacroPath += " + '" + mgr.encodeStringForJavascript("&DOC_NO=") + "' + URLClientEncode('"+mgr.encodeStringForJavascript(rowset.getValue("DOC_NO").replaceAll("\\\\","\\\\\\\\")) + "')";
            sEdmMacroPath += " + '" + mgr.encodeStringForJavascript("&DOC_SHEET=") + "' + URLClientEncode('"+mgr.encodeStringForJavascript(rowset.getValue("DOC_SHEET").replaceAll("\\\\","\\\\\\\\")) + "')";
            sEdmMacroPath += " + '" + mgr.encodeStringForJavascript("&DOC_REV=") + "' + URLClientEncode('"+mgr.encodeStringForJavascript(rowset.getValue("DOC_REV").replaceAll("\\\\","\\\\\\\\")) + "')";
            sEdmMacroPath += " + '" + mgr.encodeStringForJavascript("&DOC_TYPE=ORIGINAL") + "'";
            sEdmMacroPath += " + '" + mgr.encodeStringForJavascript("&FILE_TYPE=") + "' + URLClientEncode('"+mgr.encodeStringForJavascript(rowset.getValue("FILE_TYPE")) + "')";
            sEdmMacroPath += " + '" + mgr.encodeStringForJavascript("&PROCESS_DB=VIEW") + "'";

            imageurl = "docmaw/images/open.gif";
            href = "javascript:showNewBrowser("+ sEdmMacroPath +")"; //Bug Id 85713, encodeStringForJavascript
            printImageLink(imageurl,href,"",translate("DOCMAWPORTLETSDOCUMENTAPPROVERALTVIEW: View"));
            printSpaces(1);
			}
			else
			{
            imageurl = getASPConfig().getImagesLocation() + "table_empty_image.gif";
            printImage(imageurl);
			   printSpaces(1);
			} 

			if (objstate.equalsIgnoreCase("Approval In Progress") && bcanApproveRejct) //Bug Id 70286
			{      
            String keys = "'" + mgr.encodeStringForJavascript("&LU_NAME=") + "' + URLClientEncode('" + mgr.encodeStringForJavascript(rowset.getValue("LU_NAME")) + "')";
            keys += " + '" + mgr.encodeStringForJavascript("&KEY_REF=") + "' + URLClientEncode('" + mgr.encodeStringForJavascript(rowset.getValue("KEY_REF").replaceAll("\\\\","\\\\\\\\")) + "')";//Bug Id 85713
            keys += " + '" + mgr.encodeStringForJavascript("&LINE_NO=") + "' + URLClientEncode('" + mgr.encodeStringForJavascript(rowset.getValue("LINE_NO")) + "')";
            keys += " + '" + mgr.encodeStringForJavascript("&STEP_NO=") + "' + URLClientEncode('" + mgr.encodeStringForJavascript(rowset.getValue("STEP_NO")) + "')";            
            keys += " + '" + mgr.encodeStringForJavascript("&NOTE=") + "' + URLClientEncode('" + mgr.encodeStringForJavascript(rowset.getValue("NOTE")) + "')"; //Bug Id 86536 //Bug Id 75783

            imageurl = "docmaw/images/ok.gif";
            href = "javascript:"+addProviderPrefix()+"approveCheck(" + keys + ")"; //Bug Id 85713, encodeStringForJavascript
            printImageLink(imageurl,href,"",translate("DOCMAWPORTLETSDOCUMENTAPPROVERALTAPP: Approve"));
            printSpaces(1);

            imageurl = "docmaw/images/cncl.gif";
            href = "javascript:"+addProviderPrefix()+"rejectCheck(" + keys + ")"; //Bug Id 85713, encodeStringForJavascript
            printImageLink(imageurl,href,"",translate("DOCMAWPORTLETSDOCUMENTAPPROVERALTREJ: Reject"));            
			}
			//Bug 59663,End
                        // Bug Id 91134, End

			endTableCell();

			reject = rowset.getValue("MARKREGECTED");

			if (reject.indexOf("REJ") > -1)
				reject="<input type=\"checkbox\" name=\"C"+i+"\" value=\"ON\" checked onClick=\"javascript:remaninUnchanged("+i+")\">";
			else
				reject="<input type=\"checkbox\" name=\"C"+i+"\" value=\"ON\"  onClick=\"javascript:remaninUnchanged("+i+")\">";

			beginTableCell();
			appendToHTML(reject);//Bug Id 68773 //Bug Id 70360 : Removed the change done by bug 68773
			endTableCell();
			nextTableRow();

			rowset.next();
		}
		rowset.first();

		endTableBody();
		endTable();
		appendToHTML("<script language=javascript> \n function remaninUnchanged(no){eval(\"f.C\"+no).checked=!eval(\"f.C\"+no).checked }</script>");

	}


	public void printContents() throws FndException
	{

		if (DEBUG)
			debug(this+": DocumentApprover.printContents()");

		// hidden field for next and previous links
		printHiddenField("CMD","");

		printCustomTable();

		if (size < db_rows )
		{
			printNewLine();
			printSpaces(5);

			if (skip_rows > 0)
				printSubmitLink("DOCMAWPORTLETSDOCUMENTAPPROVERDOCMAWACTMYACTPRV: Previous","prevCust");
			else
				printText("DOCMAWPORTLETSDOCUMENTAPPROVERDOCMAWACTMYACTPRV: Previous");

			printSpaces(5);
			String rows = translate("DOCMAWPORTLETSDOCUMENTAPPROVERACTMYACTROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
			printText( rows );
			printSpaces(5);

			if (skip_rows<db_rows-size)
				printSubmitLink("DOCMAWPORTLETSDOCUMENTAPPROVERDOCMAWACTMYACTNXT: Next","nextCust");
			else
				printText("DOCMAWPORTLETSDOCUMENTAPPROVERDOCMAWACTMYACTNXT: Next");

			printNewLine();
			printNewLine();

			appendDirtyJavaScript("function prevCust(obj,id)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("   getPortletField(id,'CMD').value = 'PREV';\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function nextCust(obj,id)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("   getPortletField(id,'CMD').value = 'NEXT';\n");
			appendDirtyJavaScript("}\n");

		}
		else
			printNewLine();

		// hidden field for approve and reject links
		printHiddenField("ROW_NO","");
		printHiddenField("COMMENTS","");


		appendDirtyJavaScript("function " + addProviderPrefix() + "refreshPortlet()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   submitPortlet('" + getId() + "');\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function " + addProviderPrefix() + "setRowNo( row_no )\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   getPortletField('" + getId() + "','ROW_NO').value = row_no;\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function " + addProviderPrefix() + "approveCheck(keys)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    window.open('docmaw/DocAppPortCommentDlg.page?PREFIX=" + getASPManager().URLEncode(addProviderPrefix()) + "&COMMAND=APPROVESTEP' + keys,'anotherWindow','toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,dependent=yes,width=500,height=225,screenX=150,screenY=250,top=150,left=250');\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function " + addProviderPrefix() + "rejectCheck(keys)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    window.open('docmaw/DocAppPortCommentDlg.page?PREFIX=" + getASPManager().URLEncode(addProviderPrefix()) + "&COMMAND=REJECTSTEP' + keys,'anotherWindow','toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,dependent=yes,width=500,height=225,screenX=150,screenY=250,top=150,left=250');\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function " + addProviderPrefix() + "openInNewWindow( file_path )\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.open(file_path,'anotherWindow','status,resizable,scrollbars,width=500,height=500,left=100,top=100');\n");
		appendDirtyJavaScript("}\n");
	}
   //Bug Id 70286, Start
   public boolean  canViewDocument()  
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf;
      boolean bcanView =false;
	    
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	    
      trans.clear();
      trans.addSecurityQuery("DOCUMENT_ISSUE_ACCESS_API","Get_Document_Access");
      trans = mgr.perform(trans);
      buf = trans.getSecurityInfo();    
      
      if( buf.itemExists("DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"))
	bcanView = true;
      
      return bcanView;
   }
   public boolean  canApproveRejectDocument()  
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf;
      boolean bHasrights =false;
	    
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	    
      trans.clear();
      trans.addSecurityQuery("Approval_Routing_API","Set_Next_App_Step");
      trans = mgr.perform(trans);
      buf = trans.getSecurityInfo();    
      
      if( buf.itemExists("Approval_Routing_API.Set_Next_App_Step"))
	bHasrights = true;
      
      return bHasrights;
   }
   //Bug Id 70286, End

}
