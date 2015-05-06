package ifs.docmaw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.DocIssueKey;

public class DocIssueReferenceFilesDlg extends ASPPageProvider {

	// ===============================================================
	// Static constants
	// ===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docctw.DocIssueReferenceFilesDlg");
	public static String docIssueName = "DOCISSUEREFERENCEFILESDLG";
	public static String docRevTitle = docIssueName + "_REV_TITLE";

	// ===============================================================
	// Instances created on page creation (immutable attributes)
	// ===============================================================
	
	private ASPBlock blk;
	private ASPRowSet set;
	private ASPCommandBar bar;
	private ASPTable tbl;
	private ASPBlockLayout lay;
		
	boolean createSuccess;
	String  showMsg;
	
	
	public DocIssueReferenceFilesDlg(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}
	
	public void run() throws FndException 
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		createSuccess = false;
		showMsg = "";
		
		if (mgr.dataTransfered())
			storeParamters();
		
		if (mgr.commandBarActivated())
		{
			storeSelectionToCtx();
			eval(mgr.commandBarFunction());
		}
		else if (mgr.buttonPressed("MAKECONNECTION")) {
			makeDocIssueConnection();
		} else
			okFind();
		adjust();
	}
	
	private void storeSelectionToCtx()
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		int contRows = set.countRows();
		if (contRows > 0)
		{
			set.store();
			HashMap<String, DocIssueKey> docMap = (HashMap) ctx.findGlobalObject(docIssueName);
			if (docMap == null)
			{
				docMap = new HashMap<String, DocIssueKey>(); 
			}
			     
			set.first();
			for(int i = 0; i < contRows; i++)
			{
				String sub_doc_class = set.getRow().getValue("DOC_CLASS");
				String sub_doc_no = set.getRow().getValue("DOC_NO");
				String sub_doc_sheet = set.getRow().getValue("DOC_SHEET");
				String sub_doc_rev = set.getRow().getValue("DOC_REV");
				String keyRef = getDocIssueKeyRef(sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev);
				
				if (set.isRowSelected())
				{
					if (!docMap.containsKey(keyRef))
					{
						DocIssueKey doc_issue_key = new DocIssueKey(sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev);
						docMap.put(keyRef, doc_issue_key);
					}
				}
				else
				{
					if (docMap.containsKey(keyRef))
					{
						docMap.remove(keyRef);
					}
				}
				set.next();
			}
			ctx.setGlobalObject(docIssueName, docMap);
		}
	}
	
	private void clearSelectionToCtx()
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		ctx.setGlobalObject(docIssueName, null);
	}
	
	public void okFind() 
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q = trans.addQuery(blk);
//		String doc_no = ctx.findGlobal("DOC_NO");
//		if (!mgr.isEmpty(doc_no))
		
//		q.addWhereCondition("Document_Issue_Access_API.User_Get_Access(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,Fnd_Session_API.Get_Fnd_User) IN ('VIEW', 'EDIT')");
		
		int buf_size = Integer.parseInt(mgr.readValue("__PAGESIZE", "-1")) == -1 ? 
	    		Integer.parseInt(mgr.getConfigParameter("ADMIN/BUFFER_SIZE", "50")) : 
	    		Integer.parseInt(mgr.readValue("__PAGESIZE", "-1"));
        
	    String populate_record_count = mgr.getConfigParameter("DOCMAW/POPULATE_RECORD_COUNT", "500");
	    
	    String pop_rec_cnt;
	    
	    if (buf_size > Integer.parseInt(populate_record_count))
	    	pop_rec_cnt = String.valueOf(buf_size);
	    else
	    	pop_rec_cnt = populate_record_count;
	    
	    q.addWhereCondition("ROWNUM <= " + pop_rec_cnt);
		
		q.includeMeta("ALL");
		q.setBufferSize(20);
		mgr.querySubmit(trans, blk);
		if (set.countRows() == 0) 
		{
			set.clear();
			mgr.showAlert(mgr.translate("DOCMAWDOCISSUEREFERENCEFILESDLGNODATA: No data found."));
		}
		clearSelectionToCtx();
	}
	
	public void storeParamters() 
	{
		ASPManager mgr = getASPManager();

		if (mgr.dataTransfered())
		{
			ASPContext ctx = mgr.getASPContext();
			ASPBuffer buf = mgr.getTransferedData();
			String rev_title = buf.getValue("DATA/REV_TITLE");
			String doc_no = buf.getValue("DATA/DOC_NO");
			String doc_class = buf.getValue("DATA/DOC_CLASS");
			String doc_sheet = buf.getValue("DATA/DOC_SHEET");
			String doc_rev = buf.getValue("DATA/DOC_REV");
			if (!mgr.isEmpty(rev_title))
				ctx.setGlobal(docRevTitle, rev_title);
			if (!mgr.isEmpty(doc_no))
				ctx.setGlobal("DOC_NO", doc_no);
			if (!mgr.isEmpty(doc_class))
				ctx.setGlobal("DOC_CLASS", doc_class);
			if (!mgr.isEmpty(doc_sheet))
				ctx.setGlobal("DOC_SHEET", doc_sheet);
			if (!mgr.isEmpty(doc_rev))
				ctx.setGlobal("DOC_REV", doc_rev);
		}
	}
	
	public void makeDocIssueConnection() throws FndException
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPContext ctx = mgr.getASPContext();
		String rev_title = ctx.findGlobal(docRevTitle);
		String doc_class = ctx.findGlobal("DOC_CLASS");
		String doc_no = ctx.findGlobal("DOC_NO");
		String doc_sheet = ctx.findGlobal("DOC_SHEET");
		String doc_rev = ctx.findGlobal("DOC_REV");
		
		if (!mgr.isEmpty(rev_title))
		{
			storeSelectionToCtx();
			
			HashMap<String, DocIssueKey> docMap = (HashMap) ctx.findGlobalObject(docIssueName);
			if (docMap == null || docMap.isEmpty())
			{
				mgr.showAlert(mgr.translate("DOCMAWDOCISSUEREFERENCEFILESDLGMSGERR: You must select some documents."));
			}
			else
			{
				Iterator docKeyIterator = docMap.keySet().iterator();
				
				for (int j = 0; docKeyIterator.hasNext(); j++)
				{
					DocIssueKey doc_issue_key = docMap.get(docKeyIterator.next());
					if (!mgr.isEmpty(doc_issue_key.doc_class) && 
						!mgr.isEmpty(doc_issue_key.doc_no) && 
						!mgr.isEmpty(doc_issue_key.doc_sheet) && 
						!mgr.isEmpty(doc_issue_key.doc_rev))
					{
						ASPCommand cmd = trans.addCustomCommand("MAKECONNECTION" + j, "DOC_STRUCTURE_API.Make_Connection_");					
						
						cmd.addParameter("DOC_CLASS", doc_class);
						cmd.addParameter("DOC_NO", doc_no);
						cmd.addParameter("DOC_SHEET", doc_sheet);
						cmd.addParameter("DOC_REV", doc_rev);
						cmd.addParameter("SUB_DOC_CLASS", doc_issue_key.doc_class);
						cmd.addParameter("SUB_DOC_NO", doc_issue_key.doc_no);
						cmd.addParameter("SUB_DOC_SHEET", doc_issue_key.doc_sheet);
						cmd.addParameter("SUB_DOC_REV", doc_issue_key.doc_rev);					
						cmd.addParameter("RELATIVE_PATH", "null");
					}
				}
				trans = mgr.perform(trans);
				createSuccess = true;
				showMsg = mgr.translate("DOCMAWDOCISSUEREFERENCEFILESDLGMSGSUCC: Make files connection in document info &1 successfully.", rev_title);
				clearSelectionToCtx();
			}
		}
	}
	
	public void preDefine() 
	{
		ASPManager mgr = getASPManager();

		blk = mgr.newASPBlock("DOCISSUE");

		blk.addField("DOC_CLASS").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCCLASS: Doc Class").
			setSize(5);
		
		blk.addField("DOC_NAME").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCNAME: Doc Name").
			setSize(10);
		
		blk.addField("FILE_NO").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGFILENO: File No").
			setFunction("Doc_Issue_API.Get_Doc_Numbers(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
			setSize(30);
		
		blk.addField("DOCUMENT_SEQ").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCUMENTSEQ: Document Seq").
			setSize(10);
		
		blk.addField("DOC_TITLE").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCTITLE: Doc Title").
			setSize(30);
		
		blk.addField("DOC_DEPT").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCDEPT: Document Department").
			setFunction("Doc_Issue_API.Get_Document_Dept(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
			setSize(10);

		/*blk.addField("DOC_QTY").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCQTY: Document Qty").
			setFunction("Doc_Invent_Location_API.Cal_Doc_Qty(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
			setSize(5);*/
		
		blk.addField("DOC_REV").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCREV: Doc Rev").
			setSize(6);
		
		blk.addField("TRANFER_NO").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGTRANFERNO: Tranfer No").
			setSize(20);
	
		blk.addField("USER_CREATED").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGUSERCRE: Created By").
			setSize(10);
		
		blk.addField("DT_CRE", "Datetime").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDATECRE: Date Created").
			setSize(30);
		
		blk.addField("IS_ELE_DOC").
		    setFunction("EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESISELEDOC: Is Ele Doc").
			setCheckBox("FALSE,TRUE").
			setSize(5);
		
		// Hidden fields
		blk.addField("DOC_NO").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCNO: Doc No").
			setSize(20).
			setHidden();
		
		blk.addField("DOC_SHEET").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGDOCSHEET: Doc Sheet").
			setSize(10).
			setHidden();
		
		blk.addField("REV_TITLE").
			setFunction("''").
			setHidden();
	
		blk.addField("LANGUAGE_CODE").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGLANCODE: Language Code").
			setSize(13).
			setHidden();
		
		blk.addField("SUB_DOC_CLASS").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGSUBDOCCLASS: Doc Class").
			setFunction("''").
			setSize(12).
			setHidden();
		
		blk.addField("SUB_DOC_NO").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGSUBDOCNO: Doc No").
			setFunction("''").
			setSize(20).
			setHidden();
		
		blk.addField("SUB_DOC_SHEET").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGSUBDOCSHEET: Doc Sheet").
			setFunction("''").
			setSize(10).
			setHidden();
		
		blk.addField("SUB_DOC_REV").
			setLabel("DOCMAWDOCISSUEREFERENCEFILESDLGSUBDOCREV: Doc Rev").
			setFunction("''").
			setSize(6).
			setHidden();
	
		blk.addField("USER_ID").
			setFunction("''").
			setHidden();

		blk.addField("RELATIVE_PATH").
			setFunction("''").
			setHidden();
		
		blk.setView("DOC_ISSUE_LOV");
		set = blk.getASPRowSet();
		tbl = mgr.newASPTable(blk);
		tbl.setTitle("DOCMAWDOCISSUEREFERENCEFILESDLGTBL: Select Documents");
		tbl.disableQuickEdit();
		tbl.enableRowSelect();
		tbl.setWrap();

		bar = mgr.newASPCommandBar(blk);
		bar.disableMultirowAction();
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		lay.setDataSpan("FILE_NO", 5);
		lay.setDataSpan("DOC_TITLE", 5);
		lay.setEditable();
	}
	
	// ===============================================================
	// HTML
	// ===============================================================
	
	protected String getDescription() {
		return "DOCMAWDOCISSUEREFERENCEFILESDLGDESC: Select Documents";
	}

	protected String getTitle() {
		return "DOCMAWDOCISSUEREFERENCEFILESDLGTITLE: Select Documents";
	}
	
	public void  adjust()
	{
		//
		// Things to adjust on every run of the page
		//
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		
		HashMap<String, DocIssueKey> docMap = (HashMap) ctx.findGlobalObject(docIssueName);
		int setCount = set.countRows();
		
		if (docMap != null && !docMap.isEmpty() && setCount > 0)
		{
			set.first();
			for(int i = 0; i < setCount; i++)
			{
				String keyRef = getDocIssueKeyRef(
						set.getValue("DOC_CLASS"),
						set.getValue("DOC_NO"),
						set.getValue("DOC_SHEET"),
						set.getValue("DOC_REV")); 
					
				if (docMap.containsKey(keyRef))
				{
					set.selectRow();
				}
				set.next();
			}
		}
	}
	
	private String getDocIssueKeyRef(String doc_class, String doc_no, String doc_sheet, String doc_rev)
	{
		return 	"DOC_CLASS=" + doc_class + IfsNames.textSeparator +
				"DOC_NO=" + doc_no + IfsNames.textSeparator +
				"DOC_SHEET=" + doc_sheet + IfsNames.textSeparator +
				"DOC_REV=" + doc_rev + IfsNames.textSeparator;
	}
	
	protected void printContents() throws FndException 
	{
		ASPManager mgr = getASPManager();

		appendToHTML(lay.show());

		if (createSuccess && !mgr.isEmpty(showMsg))
		{
			appendDirtyJavaScript("ifsAlert(\"" + showMsg + "\");\n");
			appendDirtyJavaScript("Close();\n");
		}
		else if (!lay.isFindLayout() && set.countRows() > 0)
		{
			beginDataPresentation();
			printSubmitButton("MAKECONNECTION", mgr.translate("DOCMAWDOCISSUEREFERENCEFILESDLGCREATE: Make Connection"), "");
			printSpaces(1);
			printSubmitButton("CANCEL", mgr.translate("DOCMAWDOCISSUEREFERENCEFILESDLGCANCEL: Cancel"), "OnClick='javascript:window.close();'");
			endDataPresentation();
		}
		
		//
		// Client functions
		//
		
		appendDirtyJavaScript("function Close()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   try\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      eval(\"opener.refreshParent()\");\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   catch(err){}\n");
		appendDirtyJavaScript("   try\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      window.close();\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   catch(err){}\n");
		appendDirtyJavaScript("}\n");
	}

}
