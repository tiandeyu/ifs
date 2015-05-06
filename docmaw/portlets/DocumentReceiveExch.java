package ifs.docmaw.portlets;

import ifs.docmaw.DocmawConstants;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.genbaw.GenbawConstants;

import java.util.*;
import java.io.*;
import java.lang.*;

/**
 */
public class DocumentReceiveExch extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int size = 20;
	// MDAHSE, 2001-08-16
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.portlets.DocumentReceiveExch");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;
   private ASPBuffer  data;  //Bug Id 56231.


	//==========================================================================
	//  Mutable attributes
	//==========================================================================


	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient int      skip_rows;
	private transient int      db_rows;

	//==========================================================================
	//  Construction
	//==========================================================================

	public DocumentReceiveExch( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
	}

	public ASPPage construct() throws FndException
	{
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	protected void doReset() throws FndException
	{
		super.doReset();
		//   no reset of variables since they should be "remembered".
	}

	public ASPPoolElement clone( Object mgr ) throws FndException
	{
	   DocumentReceiveExch page = (DocumentReceiveExch)(super.clone(mgr));

		page.ctx    = page.getASPContext();
		page.blk    = page.getASPBlock(blk.getName());
		page.rowset = page.blk.getASPRowSet();
		page.tbl    = page.getASPTable(tbl.getName());

		return page;
	}


	protected void preDefine() throws FndException
	{
		ctx = getASPContext();

		blk = newASPBlock("MAIN");

		addField(blk, "DOC_CLASS").
      setHidden().
      setSize(10);
		
		addField(blk, "DOC_NO").
      setHidden().
      setSize(10);
		
		addField(blk, "DOC_SHEET").
      setHidden().
      setSize(10);
		
		addField(blk, "DOC_REV").
      setHidden().
      setSize(10);
		
		addField(blk, "SUB_CLASS").
      setHidden().
      setSize(10);
		
		addField(blk, "SUB_CLASS_NAME").
		setFunction("Doc_Sub_Class_Api.Get_Sub_Class_Name(:DOC_CLASS,:SUB_CLASS)").
		setLabel("DOCUMENTRECEIVEEXCHSUBCLASS: Sub Class").
		setSize(10);
		
		/*addField(blk, "SUB_CLASS_PAGE").
      setFunction("Doc_Sub_Class_Api.Get_Page_Url(:DOC_CLASS,:SUB_CLASS)").
      setHidden().
      setSize(10);*/
		
		addField(blk, "DOC_CODE").
		setLabel("DOCUMENTRECEIVEEXCHDOCCODE: Doc Code").
		setHyperlink("docmaw/DocmawIntermediatePage.page?FROMFLAG=NORMAL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,SUB_CLASS", "NEWWIN").
		setSize(20);
      
		addField(blk, "REV_TITLE").
		setLabel("DOCUMENTRECEIVEEXCHREVTITLE: Rev Title").
		setHyperlink("docmaw/DocmawIntermediatePage.page?FROMFLAG=NORMAL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,SUB_CLASS", "NEWWIN").
		setSize(30);
		
		addField(blk, "SEND_UNIT_NO").
		setHidden();

	   addField(blk, "SEND_UNIT_NAME").
	   setLabel("DOCUMENTRECEIVEEXCHSENDUNITNAME: Send Unit").
      setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:SEND_UNIT_NO)").
      setSize(20);
	   
	   addField(blk, "PURPOSE_NO").
	   setHidden();

	   addField(blk, "PURPOSE_NAME").
	   setLabel("DOCUMENTRECEIVEEXCHPURPOSENAME: Purpose Name").
	   setFunction("DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name(:PURPOSE_NO)").
      setSize(10);

		/*addField(blk, "DOWNLOAD_DOC").
		setFunction("''").
      setLabel("DOCUMENTRECEIVEEXCHDOWNLOAD: Download").
      setAsImageField().
      setSize(5);
		
		addField(blk, "SIGN_DOC").
      setFunction("''").
      setLabel("DOCUMENTRECEIVEEXCHSIGN: Sign").
      setAsImageField().
      setSize(5);*/
		
		blk.setView("DOC_ISSUE_REFERENCE");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();
		getASPManager().newASPCommandBar(blk);

		appendJavaScript( "function firstCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'FIRST';",
		      "}\n");
		
		appendJavaScript( "function prevCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'PREV';",
		      "}\n");
		
		appendJavaScript( "function lastCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'LAST';",
		      "}\n");
		
		appendJavaScript( "function nextCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'NEXT';",
		      "}\n");
		
		init();
	}

	protected void init()
	{
		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());
		ASPContext ctx = getASPContext();

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
		String cmd = readValue("CMD");

		if ("FIRST".equals(cmd))
      {
         skip_rows = 0;
      }
      else if ("PREV".equals(cmd))
      {
         if (skip_rows>=size)
         {
            skip_rows -= size;
         }
         else
         {
            skip_rows = 0;
         }
      }
      else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
      {
         skip_rows += size;
      }
      else if ("LAST".equals(cmd))
      {
         skip_rows = ((db_rows + size - 1)/size - 1) * size;
      }
      else
      {
         skip_rows = 0;
      }

		ctx.writeValue("SKIP_ROWS", skip_rows + "");
	}


	protected void run() throws FndException
	{
		String commd = readValue("CMD");
		ASPCommand cmd;

		if (Str.isEmpty(commd) || "NEXT".equals(commd) || "PREV".equals(commd) || "FIRST".equals(commd) || "LAST".equals(commd))
		{
			ASPManager           mgr    = getASPManager();
			ASPTransactionBuffer trans  = mgr.newASPTransactionBuffer();
			
			ASPQuery             qry    = trans.addQuery(blk);
			qry.addWhereCondition("DOC_CLASS = '" + DocmawConstants.EXCH_RECEIVE + "'");
			qry.addWhereCondition("EXISTS (select 1 from doc_issue_target_org t where t.doc_class = DOC_ISSUE_REFERENCE.doc_class and t.doc_no = DOC_ISSUE_REFERENCE.doc_no and t.doc_sheet = DOC_ISSUE_REFERENCE.doc_sheet and t.doc_rev = DOC_ISSUE_REFERENCE.doc_rev AND t.sign_status = 'FALSE' and t.org_no = '" + getASPContext().findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE) + "' )");
			qry.includeMeta("ALL");
			qry.setBufferSize(size);
			qry.setOrderByClauseCompulsively("SUB_CLASS ASC,DT_CRE DESC");
			
			if (Str.isEmpty(commd))
				qry.skipRows(0);
			else
				qry.skipRows(skip_rows);
			
			submit(trans);
			db_rows = blk.getASPRowSet().countDbRows();
			getASPContext().writeValue("DB_ROWS", db_rows + "" );
		}
	}

	public String getTitle( int mode )
	{
		String name = "";
		
		if (Str.isEmpty(name))
      {
         name = translate(getDescription());
      }
		
      if (mode == ASPPortal.MINIMIZED)
      {
         return name + " " + translate("DOCUMENTRECEIVEEXCHTITLE1: - You have ") + db_rows + " " + translate("DOCUMENTRECEIVEEXCHTITLE2: document need to receive.");
      }
      else
         return name; 
	}
	
	public void printContents() throws FndException
   {
      printHiddenField("CMD","");
      int     nRows;
      nRows = rowset.countRows();
      
      if (nRows > 0)
      {
         printTable(tbl);
         
         if (size < db_rows)
         {
            printNewLine();

            if (skip_rows>0)
               printSubmitLink("DOCUMENTRECEIVEEXCHFIRST: First", "firstCust");
            else
               printText("DOCUMENTRECEIVEEXCHFIRST: First");

            printSpaces(5);
            
            if (skip_rows > 0)
               printSubmitLink("DOCUMENTRECEIVEEXCHPRES: Previous", "prevCust");
            else
               printText("DOCUMENTRECEIVEEXCHPRES: Previous");

            printSpaces(5);
            String rows = translate("DOCUMENTRECEIVEEXCHROWS: (Row &1 to &2 total &3)",
                                    (skip_rows+1)+"",
                                    (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                    db_rows+"");
            printText( rows );
            printSpaces(5);

            if (skip_rows<db_rows-size)
               printSubmitLink("DOCUMENTRECEIVEEXCHNEXT: Next", "nextCust");
            else
               printText("DOCUMENTRECEIVEEXCHNEXT: Next");
            
            printSpaces(5);
            
            if (skip_rows<db_rows-size)
               printSubmitLink("DOCUMENTRECEIVEEXCHLAST: Last", "lastCust");
            else
               printText("DOCUMENTRECEIVEEXCHLAST: Last");

            printNewLine();
         }
      }
      else
      {
         printNewLine();
         printText("DOCUMENTRECEIVEEXCHNODATA: No document found.");
         printNewLine();
      }
      printNewLine();
   }

	public static String getDescription()
	{
		return "DOCUMENTRECEIVEEXCHDESC: Document Receive Center";
	}
	
	public boolean canCustomize()
   {
      return false;
   }

}
