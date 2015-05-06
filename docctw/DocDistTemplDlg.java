package ifs.docctw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DocDistTemplDlg extends ASPPageProvider {

	// ===============================================================
	// Static constants
	// ===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docctw.DocDistTemplDlg");

	// ===============================================================
	// Instances created on page creation (immutable attributes)
	// ===============================================================
	
	// -----------------------------------------------------------------------------
	// ---------- Header Instances created on page creation --------
	// -----------------------------------------------------------------------------

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPTabContainer tabs;

	// -----------------------------------------------------------------------------
	// ---------- Item Instances created on page creation --------
	// -----------------------------------------------------------------------------

	private ASPBlock doc_dist_templ_depts_blk;
	private ASPRowSet doc_dist_templ_depts_set;
	private ASPCommandBar doc_dist_templ_depts_bar;
	private ASPTable doc_dist_templ_depts_tbl;
	private ASPBlockLayout doc_dist_templ_depts_lay;

	private ASPBlock doc_dist_templ_units_blk;
	private ASPRowSet doc_dist_templ_units_set;
	private ASPCommandBar doc_dist_templ_units_bar;
	private ASPTable doc_dist_templ_units_tbl;
	private ASPBlockLayout doc_dist_templ_units_lay;
		
	boolean createSuccess;
	String  showMsg;
	
	
	public DocDistTemplDlg(ASPManager mgr, String page_path) {
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
		
		String dist_no = ctx.readValue("DOC_DIST_NO");
		String dist_type = ctx.readValue("DOC_DIST_TYPE");
		
		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("REFTEMPLH") || mgr.buttonPressed("REFTEMPLB")) {
			refTemplate();
		} else
			okFind();
		adjust();
		ctx.writeValue("DOC_DIST_NO", dist_no);
		ctx.writeValue("DOC_DIST_TYPE", dist_type);
		
	}
	
	public void storeParamters() 
	{
		ASPManager mgr = getASPManager();

		if (mgr.dataTransfered())
		{
			ASPContext ctx = mgr.getASPContext();
			ASPBuffer buf = mgr.getTransferedData();
			String dist_no = buf.getValue("DATA/DIST_NO");
			String dist_type = buf.getValue("DATA/DIST_TYPE");
			if (!mgr.isEmpty(dist_no))
				ctx.writeValue("DOC_DIST_NO", dist_no);
			if (!mgr.isEmpty(dist_type))
				ctx.writeValue("DOC_DIST_TYPE", dist_type);
		}
	}
	
	public void refTemplate() throws FndException
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPContext ctx = mgr.getASPContext();
		String dist_no = ctx.readValue("DOC_DIST_NO");
		if (!mgr.isEmpty(dist_no))
		{
			headset.store();
			int sel_count = headset.countSelectedRows();
			if (sel_count < 1)
			{
				mgr.showAlert(mgr.translate("DOCDISTTEMPLDLGNOREC: You must select a template record."));
			}
			else
			{
				String templ_dist_no = headset.getValue("DIST_NO");
				ASPCommand cmd = trans.addCustomCommand("REFTEMPL", "DOC_DISTRIBUTION_CTL_API.Ref_Template");
				cmd.addParameter("DIST_NO", dist_no);
				cmd.addParameter("TEMPL_DIST_NO", templ_dist_no);
				trans = mgr.perform(trans);
				createSuccess = true;
				showMsg = mgr.translate("DOCDISTTEMPLDLGSUCC: Document distribution &1 reference from template &2 successfully.", dist_no, templ_dist_no);
			}
		}
	}
	
	
	public void okFind() {
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPContext ctx = mgr.getASPContext();
		ASPQuery q;

		mgr.createSearchURL(headblk);
		q = trans.addQuery(headblk);
		
		String doc_dist_type = ctx.readValue("DOC_DIST_TYPE");
		if (!mgr.isEmpty(doc_dist_type))
			q.addWhereCondition("DIST_TYPE = '" + doc_dist_type + "'");
		
		q.includeMeta("ALL");
		/*if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());*/
		mgr.querySubmit(trans, headblk);
		if (headset.countRows() == 0) {
			headset.clear();
		}
		okFindITEM1();
	}

	public void countFind() {
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getValue("N")));
		headset.clear();
	}

	// -----------------------------------------------------------------------------
	// ------------------------ Item block cmd bar functions  ----------------------
	// -----------------------------------------------------------------------------

	public void okFindITEM1() {
		ASPManager mgr = getASPManager();
		
		if (headset.countRows() == 0)
		   return;
		
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		int headrowno;

		q = trans.addQuery(doc_dist_templ_depts_blk);
		q.addWhereCondition("DIST_NO = ?");
		q.addParameter("DIST_NO", headset.getValue("DIST_NO"));
		q.includeMeta("ALL");
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans, doc_dist_templ_depts_blk);
		headset.goTo(headrowno);
	}


	public void okFindITEM2() {
		ASPManager mgr = getASPManager();
		
		if (headset.countRows() == 0)
         return;
		
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		int headrowno;

		q = trans.addQuery(doc_dist_templ_units_blk);
		q.addWhereCondition("DIST_NO = ?");
		q.addParameter("DIST_NO", headset.getValue("DIST_NO"));
		q.includeMeta("ALL");
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans, doc_dist_templ_units_blk);
		headset.goTo(headrowno);
	}
	
	public void preDefine() 
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("MAIN");
		headblk.addField("OBJID").
				setHidden();
		headblk.addField("OBJVERSION").
				setHidden();
		headblk.addField("DIST_NO").
				setMandatory().
				setInsertable().
				setLabel("DOCDISTRIBUTIONTEMPLDISTNO: Dist No").
				setSize(10);
		headblk.addField("DIST_SEQ").
				setInsertable().
				setLabel("DOCDISTRIBUTIONTEMPLDISTSEQ: Dist Seq").
				setSize(20);
		headblk.addField("DIST_NAME").
				setInsertable().
				setLabel("DOCDISTRIBUTIONTEMPLDISTNAME: Dist Name").
				setSize(50);
		headblk.addField("DIST_TYPE").
				setInsertable().
				setDynamicLOV("DOC_CLASS").
            setLOVProperty("WHERE", "COMP_DOC='TRUE'").
				setLabel("DOCDISTRIBUTIONTEMPLDISTTYPE: Dist Type").
				setSize(10);
		headblk.addField("DIST_TYPE_NAME").
				setFunction("DOC_CLASS_API.GET_DOC_NAME(:DIST_TYPE)").
				setLabel("DOCDISTRIBUTIONTEMPLDISTTYPENAME: Dist Type Name").
				setSize(10);
		mgr.getASPField("DIST_TYPE").setValidation("DIST_TYPE_NAME");
		
		headblk.addField("DIST_SUB_TYPE").
            setInsertable().
            setLabel("DOCDISTRIBUTIONTEMPLDISTSUBTYPE: Dist Sub Type").
            setDynamicLOV("DOC_SUB_CLASS", "DIST_TYPE DOC_CLASS").
            setSize(10);
      
      headblk.addField("DIST_SUB_TYPE_NAME").
            setLabel("DOCDISTRIBUTIONTEMPLSUBTYPENAME: Dist Sub Type Name").
            setFunction("DOC_SUB_CLASS_API.Get_Sub_Class_Name(:DIST_TYPE,:DIST_SUB_TYPE)").
            setReadOnly().
            setSize(10);
      mgr.getASPField("DIST_SUB_TYPE").setValidation("DIST_SUB_TYPE_NAME");

		headblk.addField("NOTE").
				setInsertable().
				setLabel("DOCDISTRIBUTIONTEMPLNOTE: Note").
				setHeight(3).
				setSize(80);
		
		headblk.addField("TEMPL_DIST_NO").
				setFunction("''").
				setHidden();
		
		headblk.setView("DOC_DISTRIBUTION_TEMPL");
		headblk.defineCommand("DOC_DISTRIBUTION_TEMPL_API", "");
		
		headblk.disableHistory();
		headblk.disableDocMan();
		
		headset = headblk.getASPRowSet();
		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableMultirowAction();
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("DOCDISTRIBUTIONTEMPLTBLHEAD: Doc Distribution Templs");
		headtbl.enableRowSelect();
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setSimple("DIST_TYPE_NAME");
		headlay.setSimple("DIST_SUB_TYPE_NAME");
		headlay.setDataSpan("NOTE", 5);
		
		// Tab commands
		headbar.addCustomCommand("activateDepts", "Depts");
		headbar.addCustomCommand("activateUnits", "Units");
		
		//
		// Departments
		//
		
		doc_dist_templ_depts_blk = mgr.newASPBlock("ITEM1");
		doc_dist_templ_depts_blk.addField("ITEM0_OBJID").
				setHidden().
				setDbName("OBJID");
		doc_dist_templ_depts_blk.addField("ITEM0_OBJVERSION").
				setHidden().
				setDbName("OBJVERSION");
		doc_dist_templ_depts_blk.addField("ITEM0_DIST_NO").
				setDbName("DIST_NO").
				setMandatory().
				setInsertable().
				setHidden().
				setLabel("DOCDISTTEMPLDEPTSITEM0DISTNO: Dist No").
				setSize(10);
		
		doc_dist_templ_depts_blk.addField("SEND_DEPT").
				setMandatory().
				setInsertable().
				setDynamicLOV("GENERAL_DEPARTMENT_CU_LOV").
				setLabel("DOCDISTTEMPLDEPTSSENDDEPT: Send Dept").
				setSize(10);
		doc_dist_templ_depts_blk.addField("SEND_DEPT_NAME").
				setReadOnly().
				setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:SEND_DEPT)").
				setLabel("DOCDISTTEMPLDEPTSSENDDEPTNAME: Dept Name").
				setSize(20);
		mgr.getASPField("SEND_DEPT").setValidation("SEND_DEPT_NAME");
		
		doc_dist_templ_depts_blk.addField("PAPER_QTY", "Number").
				setInsertable().
				setLabel("DOCDISTTEMPLDEPTSPAPERQTY: Paper Qty").
				setSize(10);
		
		doc_dist_templ_depts_blk.addField("ELEC_QTY", "Number").
				setInsertable().
				setLabel("DOCDISTTEMPLDEPTSELECQTY: Elec Qty").
				setSize(10);
		
		doc_dist_templ_depts_blk.addField("BLUEPRINT_QTY", "Number").
            setInsertable().
            setLabel("DOCDISTTEMPLDEPTSBLUEPRINTQTY: Blueprint Qty").
            setSize(10);
      
      doc_dist_templ_depts_blk.addField("WHITEPRINT_QTY", "Number").
            setInsertable().
            setLabel("DOCDISTTEMPLDEPTSWHITEPRINTQTY: Whiteprint Qty").
            setSize(10);
            
      doc_dist_templ_depts_blk.addField("CONTROLLED_NO").
            setInsertable().
            setLabel("DOCDISTTEMPLDEPTSCONTROLLEDNO: Controlled No").
            setSize(20);

		doc_dist_templ_depts_blk.addField("MAIN_SEND").
				setInsertable().
				setCheckBox("FALSE,TRUE").
				setLabel("DOCDISTTEMPLDEPTSMAINSEND: Main Send").
				setSize(5);
		doc_dist_templ_depts_blk.addField("ITEM0_NOTE").
				setDbName("NOTE").
				setInsertable().
				setLabel("DOCDISTTEMPLDEPTSITEM0NOTE: Note").
				setSize(50);
		
		doc_dist_templ_depts_blk.setView("DOC_DIST_TEMPL_DEPTS");
		doc_dist_templ_depts_blk.defineCommand("DOC_DIST_TEMPL_DEPTS_API", "");
		doc_dist_templ_depts_blk.setMasterBlock(headblk);
		doc_dist_templ_depts_set = doc_dist_templ_depts_blk.getASPRowSet();
		doc_dist_templ_depts_bar = mgr.newASPCommandBar(doc_dist_templ_depts_blk);
		doc_dist_templ_depts_bar.defineCommand(doc_dist_templ_depts_bar.OKFIND, "okFindITEM1");
		doc_dist_templ_depts_tbl = mgr.newASPTable(doc_dist_templ_depts_blk);
		doc_dist_templ_depts_tbl.setTitle("DOCDISTTEMPLDEPTSITEMHEAD1: Depts");
		doc_dist_templ_depts_tbl.enableRowSelect();
		doc_dist_templ_depts_tbl.setWrap();
		doc_dist_templ_depts_lay = doc_dist_templ_depts_blk.getASPBlockLayout();
		doc_dist_templ_depts_lay.setDefaultLayoutMode(doc_dist_templ_depts_lay.MULTIROW_LAYOUT);
		doc_dist_templ_depts_lay.setSimple("SEND_DEPT_NAME");

		//
		// Units
		//
		
		doc_dist_templ_units_blk = mgr.newASPBlock("ITEM2");
		doc_dist_templ_units_blk.addField("ITEM1_OBJID").
				setHidden().
				setDbName("OBJID");
		doc_dist_templ_units_blk.addField("ITEM1_OBJVERSION").
				setHidden().
				setDbName("OBJVERSION");
		doc_dist_templ_units_blk.addField("ITEM1_DIST_NO").
				setDbName("DIST_NO").
				setMandatory().
				setInsertable().
				setHidden().
				setLabel("DOCDISTTEMPLUNITSITEM1DISTNO: Dist No").
				setSize(10);
		
		doc_dist_templ_units_blk.addField("SEND_UNIT").
				setMandatory().
				setInsertable().
				setLabel("DOCDISTTEMPLUNITSSENDUNIT: Send Unit").
				setDynamicLOV("GENERAL_ZONE_LOV").
				setSize(10);
		doc_dist_templ_units_blk.addField("SEND_UNIT_NAME").
				setReadOnly().
				setFunction("GENERAL_ZONE_API.Get_Zone_Desc(:SEND_UNIT)").
				setLabel("DOCDISTTEMPLUNITSSENDUNITNAME: Send Unit Name").
				setSize(30);
		mgr.getASPField("SEND_UNIT").setValidation("SEND_UNIT_NAME");
		
		doc_dist_templ_units_blk.addField("ITEM1_PAPER_QTY", "Number").
				setDbName("PAPER_QTY").
				setInsertable().
				setLabel("DOCDISTTEMPLUNITSPAPERQTY: Paper Qty").
				setSize(10);
		
		doc_dist_templ_units_blk.addField("ITEM1_ELEC_QTY", "Number").
				setDbName("ELEC_QTY").
				setInsertable().
				setLabel("DOCDISTTEMPLUNITSELECQTY: Elec Qty").
				setSize(10);
		
		doc_dist_templ_units_blk.addField("ITEM1_BLUEPRINT_QTY", "Number").
            setDbName("BLUEPRINT_QTY").
            setInsertable().
            setLabel("DOCDISTTEMPLUNITSBLUEPRINTQTY: Blueprint Qty").
            setSize(10);
      
      doc_dist_templ_units_blk.addField("ITEM1_WHITEPRINT_QTY", "Number").
            setDbName("WHITEPRINT_QTY").
            setInsertable().
            setLabel("DOCDISTTEMPLUNITSWHITEPRINTQTY: Whiteprint Qty").
            setSize(10);
      
      doc_dist_templ_units_blk.addField("ITEM1_CONTROLLED_NO").
            setDbName("CONTROLLED_NO").
            setInsertable().
            setLabel("DOCDISTTEMPLUNITSCONTROLLEDNO: Controlled No").
            setSize(20);

		doc_dist_templ_units_blk.addField("ITEM1_MAIN_SEND").
				setDbName("MAIN_SEND").
				setInsertable().
				setCheckBox("FALSE,TRUE").
				setLabel("DOCDISTTEMPLUNITSITEM1MAINSEND: Main Send").
				setSize(5);
		doc_dist_templ_units_blk.addField("ITEM1_NOTE").
				setDbName("NOTE").
				setInsertable().
				setLabel("DOCDISTTEMPLUNITSITEM1NOTE: Note").
				setSize(50);
		
		doc_dist_templ_units_blk.setView("DOC_DIST_TEMPL_UNITS");
		doc_dist_templ_units_blk.defineCommand("DOC_DIST_TEMPL_UNITS_API", "");
		doc_dist_templ_units_blk.setMasterBlock(headblk);
		doc_dist_templ_units_set = doc_dist_templ_units_blk.getASPRowSet();
		doc_dist_templ_units_bar = mgr.newASPCommandBar(doc_dist_templ_units_blk);
		doc_dist_templ_units_bar.defineCommand(doc_dist_templ_units_bar.OKFIND, "okFindITEM2");
		doc_dist_templ_units_tbl = mgr.newASPTable(doc_dist_templ_units_blk);
		doc_dist_templ_units_tbl.setTitle("DOCDISTTEMPLUNITSITEMHEAD2: Units");
		doc_dist_templ_units_tbl.enableRowSelect();
		doc_dist_templ_units_tbl.setWrap();
		doc_dist_templ_units_lay = doc_dist_templ_units_blk.getASPBlockLayout();
		doc_dist_templ_units_lay.setDefaultLayoutMode(doc_dist_templ_units_lay.MULTIROW_LAYOUT);
		doc_dist_templ_units_lay.setSimple("SEND_UNIT_NAME");
		
		//
		// Tab definitions
		//

		tabs = mgr.newASPTabContainer();
		tabs.addTab(mgr.translate("DOCDISTRIBUTIONTEMPLDEPTS: Depts"), "javascript:commandSet('MAIN.activateDepts','')");
		tabs.addTab(mgr.translate("DOCDISTRIBUTIONTEMPLUNITS: Units"), "javascript:commandSet('MAIN.activateUnits','')");
		
		tabs.setContainerWidth(700);
		tabs.setLeftTabSpace(1);
		tabs.setContainerSpace(5);
		tabs.setTabWidth(100);
	}
	
	public void activateDepts()
	{
		tabs.setActiveTab(1);
		okFindITEM1();
	}

	public void activateUnits()
	{
		tabs.setActiveTab(2);
		okFindITEM2();
	}

	public void adjust()
	{
		// fill function body
		headbar.removeCustomCommand("activateDepts");
		headbar.removeCustomCommand("activateUnits");
	}
	
	// ===============================================================
	// HTML
	// ===============================================================
	
	protected String getDescription() {
		return "DOCDISTRIBUTIONTEMPLDESC: Doc Distribution Templ";
	}

	protected String getTitle() {
		return getDescription();
	}
		
	protected void printContents() throws FndException 
	{
		ASPManager mgr = getASPManager();
		
		if (!headlay.isFindLayout() && headset.countRows() > 0)
		{
			beginDataPresentation();
			printSubmitButton("REFTEMPLH", mgr.translate("DOCDISTRIBUTIONTEMPLREF: Ref Template"), "");
			printSpaces(1);
			printSubmitButton("CANCELH", mgr.translate("DOCDISTRIBUTIONTEMPLCANCEL: Cancel"), "OnClick='javascript:window.close();'");
			endDataPresentation();
		}
		
		if (headlay.isVisible())
			appendToHTML(headlay.show());
		else 
		{
			headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
			appendToHTML(headlay.show());
		}
		
		if ((headlay.isSingleLayout() || headlay.isCustomLayout()) && headset.countRows() > 0)
		{
			appendToHTML(tabs.showTabsInit());
			if (tabs.getActiveTab() == 1)
			{
				appendToHTML(doc_dist_templ_depts_lay.show());
			}
			else if (tabs.getActiveTab() == 2)
			{
				appendToHTML(doc_dist_templ_units_lay.show());
			}
		}

		if (createSuccess && !mgr.isEmpty(showMsg))
		{
			appendDirtyJavaScript("ifsAlert(\"" + showMsg + "\");\n");
			appendDirtyJavaScript("Close();\n");
		}
		
		if (!headlay.isFindLayout() && headset.countRows() > 0)
		{
			beginDataPresentation();
			printSubmitButton("REFTEMPLB", mgr.translate("DOCDISTRIBUTIONTEMPLREF: Ref Template"), "");
			printSpaces(1);
			printSubmitButton("CANCELB", mgr.translate("DOCDISTRIBUTIONTEMPLCANCEL: Cancel"), "OnClick='javascript:window.close();'");
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
		
		appendDirtyJavaScript("function rowClicked(row_no,table_id,elm,box,i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if(!cca)\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      if(box!=null)\n");
		appendDirtyJavaScript("      {\n");
		appendDirtyJavaScript("         table_elm = elm.parentNode;\n");
		appendDirtyJavaScript("         if(document.getElementById && table_elm)\n");
		appendDirtyJavaScript("         {\n");
		appendDirtyJavaScript("            var table = table_elm;\n");
		appendDirtyJavaScript("            var rows = table.rows;\n");
		appendDirtyJavaScript("            var odd_row = true;\n");
		appendDirtyJavaScript("            for(i=0; i<rows.length; i++)\n");
		appendDirtyJavaScript("            {\n");
		appendDirtyJavaScript("               if(rows[i].id.indexOf('expand')!=-1) continue;\n");
		appendDirtyJavaScript("               if(odd_row && rows[i].className)\n");
		appendDirtyJavaScript("                  rows[i].className='tableRowColor1';\n");
		appendDirtyJavaScript("               else if(rows[i].className) \n");
		appendDirtyJavaScript("                  rows[i].className='tableRowColor2';\n");
		appendDirtyJavaScript("               odd_row=!odd_row;\n");
		appendDirtyJavaScript("            }\n");
		appendDirtyJavaScript("            box.checked = false;\n");
		appendDirtyJavaScript("         }\n");
		appendDirtyJavaScript("         \n");
		appendDirtyJavaScript("         if(box.length>1)\n");
		appendDirtyJavaScript("           row = box[i];\n");
		appendDirtyJavaScript("         else\n");
		appendDirtyJavaScript("           row = box;\n");
		appendDirtyJavaScript("         if(row.checked)\n");
		appendDirtyJavaScript("         {\n");
		appendDirtyJavaScript("            row.checked=false;\n");
		appendDirtyJavaScript("            CCA(row,row_no);\n");
		appendDirtyJavaScript("         }\n");
		appendDirtyJavaScript("         else\n");
		appendDirtyJavaScript("         {\n");
		appendDirtyJavaScript("            row.checked=true;\n");
		appendDirtyJavaScript("            CCA(row,row_no);\n");
		appendDirtyJavaScript("         }\n");
		appendDirtyJavaScript("      }\n");
		appendDirtyJavaScript("      else\n");
		appendDirtyJavaScript("      {\n");
		appendDirtyJavaScript("         var toggle = true;\n");
		appendDirtyJavaScript("         if(elm.className=='tableRowHiliteColor')\n");
		appendDirtyJavaScript("             toggle = false;\n");
		appendDirtyJavaScript("         clearAllRows(elm.parentNode,elm);\n");
		appendDirtyJavaScript("         if(toggle)\n");
		appendDirtyJavaScript("            elm.className='tableRowHiliteColor';\n");
		appendDirtyJavaScript("      }\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("  cca = false;\n");
		appendDirtyJavaScript("}\n");
		
	}

}
