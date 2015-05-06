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
 * File        : DocumentCheckedOut.java
 * Description : Checked out  Documents Portlet
 * Notes       :
 *      KARALK   2006-12-26   Created
 *      BAKALK   2007-05-10   Call Id: 143075.
 *      BAKALK   2007-05-14   Call Id: 140609.
 *      BAKALK   2007-06-04   Call Id: 140610.
 *      SHTHLK   2008-04-17   Bug Id 70286, Enabled the gifs only if the users have rights for the methods
 *------------------------------------------------------------------------------------
 */

package ifs.docmaw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.docmaw.DocmawUtil;
import ifs.docmaw.edm.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;


public class DocumentCheckedOut extends ASPPortletProvider
{

	public static boolean DEBUG = true;
	private final static int size = 10;

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;
	private String     reject;
   private ASPBuffer data;
   private ASPBuffer buff;
	private boolean    bHasAccess = false; //Bug Id 70286

	private transient int skip_rows;
	private transient int db_rows;


	public DocumentCheckedOut(ASPPortal portal, String clspath)
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

                
		addField(blk, "DOC_CLASS").
		setHidden();

		addField(blk, "DOC_NO").
		setHidden();

		addField(blk, "DOC_REV").
		setHidden();

		addField(blk, "DOC_SHEET").
		setHidden();

		addField(blk, "FILE_TYPE").
		setHidden();

		addField(blk, "OBJSTATE").
		setHidden();

                addField(blk, "DOC_KEY").
		setFunction("DOC_CLASS||'-'||DOC_NO||'-'||DOC_SHEET||'-'||DOC_REV").
		setLabel("DOCMAWPORTLETSDOCUMENTCHECKEDOUTDOCKEY: Doc Key");

		addField(blk, "TITLE").
		setFunction("DOC_TITLE_API.Get_Title(:DOC_CLASS,:DOC_NO)").
		setLabel("DOCMAWPORTLETSDOCUMENTCHECKEDOUTTITLE: Title");
                

		addField(blk, "DOC_TYPE").
		setLabel("DOCMAWPORTLETSDOCUMENTDOCUMENTTYPE: Document Type");

		

		addField(blk, "FILE_NAME").
                setLabel("DOCMAWPORTLETSDOCUMENTFILENAME: File Name");

		getASPField("DOC_KEY").setHyperlink("docmaw/DocIssue.page","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

		blk.setView("EDM_FILE");

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
		if (DEBUG) debug(this+": DocumentCheckedOut.run()");

		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

		ASPQuery qry = trans.addQuery(blk);
		qry.setOrderByClause("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		qry.setBufferSize(size);
		qry.skipRows(skip_rows);
		qry.includeMeta("ALL");
                qry.addWhereCondition("OBJSTATE = 'Checked Out' AND checked_out_sign = FND_SESSION_API.Get_Fnd_User");
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
		return "DOCMAWPORTLETSMYDOCUMENTCHECKEDOUT: My Checked Out Documents";
	}


	public String getTitle( int mode )
	{
		if (DEBUG) debug(this+": DocumentCheckedOut.getTitle("+mode+")");

		if (mode == ASPPortal.MINIMIZED)
			return translate(getDescription()) + translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTTXT:  - You have &1 Checked Out document(s) .", Integer.toString(db_rows));
		else
			return translate(getDescription());
	}


	public void printCustomTable() throws FndException
	{
		ASPManager mgr = getASPManager();
		String imageurl;
		String href = "";
		bHasAccess = fileOperationAccess(); //Bug Id 70286

		beginTable();
		beginTableTitleRow();
		printTableCell(translate("DOCMAWPORTLETSDOCUMENCHECKEDOUTDOCUMENT: Document"));
		printTableCell(translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTTITLE: Title"));
		printTableCell(translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTFILETYPE: File Type"));
		printTableCell("");
		
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

         data = mgr.newASPBuffer();
         ASPBuffer data1 = data.addBuffer("DOUCUMENT");
         data1.addItem("DOC_CLASS",rowset.getValue("DOC_CLASS"));
         data1.addItem("DOC_NO",rowset.getValue("DOC_NO"));
         data1.addItem("DOC_SHEET",rowset.getValue("DOC_SHEET"));
         data1.addItem("DOC_REV",rowset.getValue("DOC_REV"));

         data.traceBuffer("qqqq");
         //data.tra
         
         buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "VIEW");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("PROVIDER_PREFIX",""+addProviderPrefix());
         String sFilePath1 = DocumentTransferHandler.getDataTransferUrl(mgr, "docmaw/EdmMacro.page",buff,data);

         buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "CHECKOUT");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));
         buff.addItem("PROVIDER_PREFIX",""+addProviderPrefix());
         
         String sFilePath2 = DocumentTransferHandler.getDataTransferUrl(mgr, "docmaw/EdmMacro.page",buff,data);

         buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "CHECKIN");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("PROVIDER_PREFIX",""+addProviderPrefix());
         String sFilePath3 = DocumentTransferHandler.getDataTransferUrl(mgr, "docmaw/EdmMacro.page",buff,data);

			beginTableCell();
			printLink(rowset.getRow().getValue("DOC_KEY"),url);
			endTableCell();

			beginTableCell();
			printText(rowset.getValue("TITLE"));
			endTableCell();

			beginTableCell();
			printText(rowset.getValue("FILE_TYPE"));
			endTableCell();

			/*String sFilePath1  = "docmaw/EdmMacro.page?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"));
			sFilePath1 += "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"));
			sFilePath1 += "&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"));
			sFilePath1 += "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));
		
			sFilePath1 += "&DOC_TYPE="+mgr.URLEncode(rowset.getValue("DOC_TYPE"));
			sFilePath1 += "&FILE_TYPE="+mgr.URLEncode(rowset.getValue("FILE_TYPE"));

			String sFilePath2 = sFilePath1;
			String sFilePath3 = sFilePath1;
			sFilePath1 += "&PROCESS_DB=VIEW";
			sFilePath2 += "&PROCESS_DB=EDIT";
			sFilePath3 += "&PROCESS_DB=CHECKIN";*/

			String objstate = rowset.getValue("OBJSTATE");
			String edmstate = "";


			beginTableCell();
			
			if (objstate.equalsIgnoreCase("Checked Out") && bHasAccess) //Bug Id 70286
			{
				imageurl = "docmaw/images/open.gif";
				href = "javascript:showNewBrowser('"+sFilePath1+"')";
				printImageLink(imageurl,href,"",translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTTVIEW: View"));
				printSpaces(1);

				imageurl = "docmaw/images/edit.gif";
				href = "javascript:showNewBrowser('"+sFilePath2+"')";
				printImageLink(imageurl,href,"",translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTEDIT: Edit"));
				printSpaces(1);

				imageurl = "docmaw/images/checkin.gif";
				href = "javascript:showNewBrowser('"+sFilePath3+"')";
				printImageLink(imageurl,href,"",translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTCHECKIN: Check In"));
				printSpaces(1);
			}

			endTableCell();
			nextTableRow();
			rowset.next();
		}
		rowset.first();

		endTableBody();
		endTable();
		appendToHTML("<script language=javascript> \n function remaninUnchanged(no){eval(\"f.C\"+no).checked=!eval(\"f.C\"+no).checked }</script>");

	}


   public void debug(String str)
   {
     //if (DEBUG) 
         super.debug(str);
   }


	public void printContents() throws FndException
	{
		if (DEBUG)
			debug(this+": DocumentCheckedOut.printContents()");

		// hidden field for next and previous links
		printHiddenField("CMD","");

		printCustomTable();

		if (size < db_rows )
		{
			printNewLine();
			printSpaces(5);

			if (skip_rows > 0)
				printSubmitLink("DOCMAWPORTLETSDOCUMENTCHECKEDOUTDOCMAWACTMYACTPRV: Previous","prevCust");
			else
				printText("DOCMAWPORTLETSDOCUMENTCHECKEDOUTDOCMAWACTMYACTPRV: Previous");

			printSpaces(5);
			String rows = translate("DOCMAWPORTLETSDOCUMENTCHECKEDOUTRACTMYACTROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
			printText( rows );
			printSpaces(5);

			if (skip_rows<db_rows-size)
				printSubmitLink("DOCMAWPORTLETSDOCUMENTCHECKOUTDOCMAWACTMYACTNXT: Next","nextCust");
			else
				printText("DOCMAWPORTLETSDOCUMENTCHECKOUTDOCMAWACTMYACTNXT: Next");

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


      appendDirtyJavaScript("function " + addProviderPrefix() + "openInNewWindow( file_path )\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.open(file_path,'anotherWindow','status,resizable,scrollbars,width=500,height=500,left=100,top=100');\n");
		appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function "+ addProviderPrefix() + "refreshParent()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   submitPortlet('" + getId() + "');\n");
		appendDirtyJavaScript("}\n");


	}
   //Bug Id 70286, Start
   public boolean  fileOperationAccess()  
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf;
      boolean bhasAccess =false;
	    
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	    
      trans.clear();
      trans.addSecurityQuery("DOCUMENT_ISSUE_ACCESS_API","Get_Document_Access");
      trans = mgr.perform(trans);
      buf = trans.getSecurityInfo();    
      
      if( buf.itemExists("DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"))
	bhasAccess = true;
      
      return bhasAccess;
   }
   //Bug Id 70286, End
}

