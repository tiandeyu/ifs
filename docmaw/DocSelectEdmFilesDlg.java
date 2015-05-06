package ifs.docmaw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DocSelectEdmFilesDlg extends ASPPageProvider {

	// ===============================================================
	// Static constants
	// ===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocSelectEdmFilesDlg");
	public String dlgName = getClass().getName().toUpperCase();

	// ===============================================================
	// Instances created on page creation (immutable attributes)
	// ===============================================================
	
	protected ASPBlock blk;
	protected ASPRowSet set;
	protected ASPCommandBar bar;
	protected ASPTable tbl;
	protected ASPBlockLayout lay;
		
	protected boolean createSuccess;
	protected String showMsg;
	
	public DocSelectEdmFilesDlg(ASPManager mgr, String page_path) {
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
		else if (mgr.buttonPressed("CREATEFILES")) {
			createFiles();
		} else
			okFind();
		adjust();
	}
	
	protected void storeSelectionToCtx()
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		set.store();
		
		int contRows = set.countRows();
		if (contRows > 0)
		{
			ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
			if (edm_files == null)
			{
			   edm_files = mgr.newASPBuffer();
			}
			
			set.first();
			for(int i = 0; i < contRows; i++)
			{
				String doc_class = set.getRow().getValue("DOC_CLASS");
				String doc_no = set.getRow().getValue("DOC_NO");
				String doc_sheet = set.getRow().getValue("DOC_SHEET");
				String doc_rev = set.getRow().getValue("DOC_REV");
				String doc_type = set.getRow().getValue("DOC_TYPE");
				String file_no = set.getRow().getValue("FILE_NO");
				
				String edm_file_key_ref = getEdmFileKeyRef(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
				
				if (set.isRowSelected())
				{
					if (!edm_files.itemExists(edm_file_key_ref))
					{
						ASPBuffer sel_edm_file = mgr.newASPBuffer();
						sel_edm_file.addItem("DOC_CLASS", doc_class);
						sel_edm_file.addItem("DOC_NO", doc_no);
						sel_edm_file.addItem("DOC_SHEET", doc_sheet);
						sel_edm_file.addItem("DOC_REV", doc_rev);
						sel_edm_file.addItem("DOC_TYPE", doc_type);
						sel_edm_file.addItem("FILE_NO", file_no);
						edm_files.addBuffer(edm_file_key_ref, sel_edm_file);
					}
				}
				else
				{
					if (edm_files.itemExists(edm_file_key_ref))
					{
					   edm_files.removeItem(edm_file_key_ref);
					}
				}
				set.next();
			}
			ctx.setGlobalObject(dlgName, edm_files);
		}
	}
	
	protected String getEdmFileKeyRef(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no)
   {
      return doc_class + IfsNames.textSeparator +
             doc_no + IfsNames.textSeparator +
             doc_sheet + IfsNames.textSeparator +
             doc_rev + IfsNames.textSeparator +
             doc_type + IfsNames.textSeparator +
             file_no + IfsNames.textSeparator;
   }
	
	protected void clearSelectionToCtx()
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		ctx.setGlobalObject(dlgName, null);
	}
	
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(blk);

      q.includeMeta("ALL");
      q.setBufferSize(15);
      mgr.querySubmit(trans, blk);
      if (set.countRows() == 0)
      {
         set.clear();
         mgr.showAlert(mgr.translate("DOCSELECTEDMFILESDLGNODATA: No data found."));
      }
      clearSelectionToCtx();
   }
	
	protected void storeParamters() 
	{
	}
	
	public void createFiles() throws FndException
	{	
	}
	
   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("EDMFILE");

      blk.addField("DOC_CLASS").
      setLabel("DOCSELECTEDMFILESDLGDOCCLASS: Doc Class").
      setSize(10);

      blk.addField("DOC_NAME").
      setLabel("DOCSELECTEDMFILESDLGDOCNAME: Doc Name").
      setSize(15);
      
      blk.addField("DOC_NO").
      setLabel("DOCSELECTEDMFILESDLGDOCNO: Doc No").
      setSize(20);
      
      blk.addField("DOC_SHEET").
      setLabel("DOCSELECTEDMFILESDLGDOCSHEET: Doc Sheet").
      setSize(10);

      blk.addField("DOC_REV").
      setLabel("DOCSELECTEDMFILESDLGDOCREV: Doc Rev").
      setSize(10);

      blk.addField("DOC_TYPE").
      setLabel("DOCSELECTEDMFILESDLGDOCTYPE: Doc Type").
      setSize(10);

      blk.addField("FILE_NO").
      setLabel("DOCSELECTEDMFILESDLGFILENO: File No").
      setSize(10);
      
      blk.addField("USER_FILE_NAME").
      setLabel("DOCSELECTEDMFILESDLGUSERFILENAME: User File Name").
      setSize(30);
      
      blk.addField("STATE").
      setLabel("DOCSELECTEDMFILESDLGSTATE: State").
      setSize(10);

      blk.addField("IN1").
      setFunction("''").
      setHidden();
      
      blk.addField("IN2").
      setFunction("''").
      setHidden();
      
      blk.addField("IN3").
      setFunction("''").
      setHidden();
      
      blk.addField("OUT1").
      setFunction("''").
      setHidden();
      
      blk.addField("OUT2").
      setFunction("''").
      setHidden();

      blk.setView("EDM_FILE");
      set = blk.getASPRowSet();
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCSELECTEDMFILESDLGTBL: Select Documents");
      tbl.disableQuickEdit();
      tbl.enableRowSelect();
      tbl.setWrap();

      bar = mgr.newASPCommandBar(blk);
      bar.disableMultirowAction();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      lay.setEditable();
   }
	
	// ===============================================================
	// HTML
	// ===============================================================
	
	protected String getDescription() {
		return "DOCSELECTEDMFILESDLGDESC: Select Edm Files";
	}

	protected String getTitle() {
		return getDescription();
	}
	
	public void  adjust()
	{
		//
		// Things to adjust on every run of the page
		//
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		
		
		ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
		int setCount = set.countRows();
		
		if (edm_files != null && edm_files.countItems() != 0 && setCount > 0)
		{
			set.first();
			for(int i = 0; i < setCount; i++)
			{
				String edm_file_key_ref = getEdmFileKeyRef (
						set.getValue("DOC_CLASS"),
						set.getValue("DOC_NO"),
						set.getValue("DOC_SHEET"),
						set.getValue("DOC_REV"),
						set.getValue("DOC_TYPE"),
						set.getValue("FILE_NO")); 
					
				if (edm_files.itemExists(edm_file_key_ref))
				{
					set.selectRow();
				}
				set.next();
			}
		}
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
			printSubmitButton("OK", mgr.translate("DOCSELECTEDMFILESDLGOK: Ok"), "");
			printSpaces(1);
			printSubmitButton("CANCEL", mgr.translate("DOCSELECTEDMFILESDLGCANCEL: Cancel"), "");
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
