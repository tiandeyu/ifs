package ifs.fultxw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fultxw.engine.Constants;
import ifs.fultxw.engine.impl.FullTextServiceImpl;

public class IfsFulltextIndexingPage extends ASPPageProvider implements Constants {

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
	
	
	public IfsFulltextIndexingPage(ASPManager mgr, String pagePath) {
	   super(mgr, pagePath);
   }
	

   
   
   public void preDefine(){
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("Main");
      headblk.addField("DOC_CLASS"  ).setLabel("DOCMAWDOCUMENTCLASSBASICDOCCLASS: Document Class").setSize(20);
      headblk.addField("DOC_NAME").setLabel("DOCMAWDOCUMENTCLASSBASICDESC: Description");
      headblk.setView("DOC_CLASS");
      headset = headblk.getASPRowSet();
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("search result");
      headtbl.setWrap();
      headtbl.enableRowSelect();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.FIND);
      headbar.disableCommand(ASPCommandBar.VIEWDETAILS);
      headbar.disableMinimize();
      headbar.disableMultirowAction();
      headlay = headblk.getASPBlockLayout(); 
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      disableHeader(); 
   }
   
   
   public void run() throws FndException
   {
      super.run();
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("HEAD_ID")) )
         okFind();
      else if (mgr.buttonPressed("CreateIndex"))
      	createIndex();
    	 okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("IFSFULLTEXTINDEXINGPAGENODATA: No data found.");
         headset.clear();
      }
   }
   
   
	public void createIndex() {
		ASPManager mgr = getASPManager();
		headset.store();

		String docClass = "";
		ASPBuffer selected_buf = headset.getSelectedRows("DOC_CLASS");
		int tmpCount = selected_buf.countItems();

		if (tmpCount != 1) {
			mgr.showAlert("you should specify one docClass to index");
			okFind();
			return;
		}
		docClass = selected_buf.getBufferAt(0).getValueAt(0);

		FullTextServiceImpl fullTextService = new FullTextServiceImpl();
		String result = fullTextService.createIndex(docClass);
		
      if(result.equals(ifs.fultxw.engine.Constants.RET_ERROR_INDEX_PATH_NOT_CONFIGURED)){
         mgr.showAlert("DOCISSUEANCESTORINDEXCREATEERRORINDEXPATHNOTCONFIGURED: Index path not configured for Class: " + docClass);
      }else if(result.equals(ifs.fultxw.engine.Constants.RET_ERROR_CLASS_COLUMN_NO_CONFIGURED)){
         mgr.showAlert("DOCISSUEANCESTORINDEXCREATEERRORINDEXPATHNOTCONFIGURED: Index Column not configured for Class: " + docClass);
      }else if(FLAG_CREATEINDEX_SUCCESS.equals(result)){
			mgr.showAlert(mgr.translate("IFSFULLTEXTINDEXINGPAGEIDNEXCREATEDSUCC: index is successfully created"));
		} else if(FLAG_INDEX_IS_CREATING.equals(result)){
			mgr.showAlert(mgr.translate("IFSFULLTEXTINDEXINGPAGEOTHERISCREATING: there is an index being created right now."));
		}else{
			mgr.showAlert(mgr.translate("IFSFULLTEXTINDEXINGPAGEINDEXINGFAILED: index is created failed."));
		}
	}
   
   
   
	
	
   public void adjust(){
   	headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
   }
   
   public String getDescription() {
   	  ASPManager mgr = getASPManager();
      return mgr.translate("IFSFULLTEXTINDEXINGPAGEDESCRIPTION: Fulltext Indexing Page");
   }

   public String getTitle() {
      return getDescription();
   }
   
   
	protected AutoString getContents() throws FndException
	{ 
		return super.getContents();
	}
   
   public void printContents() throws FndException{
      ASPManager mgr = getASPManager();
   		appendToHTML(headlay.show());
   		appendToHTML("<table align = 'right'><tr><td>");
   		printSubmitButton("CreateIndex",  mgr.translate("IFSFULLTEXTINDEXINGPAGECREATEINDEX: Create Index"), "");
   		appendToHTML("</td></tr></table>");
   		  
         appendDirtyJavaScript("  function rowClicked(row_no,table_id,elm,box,i){\n");
         appendDirtyJavaScript("deselectAllRows('__SELECTED1');\n");
         appendDirtyJavaScript("    cca = false;\n");////
         appendDirtyJavaScript(" if (!cca) {\n");
         appendDirtyJavaScript("    if (box != null) {\n");
         appendDirtyJavaScript("       if (box.length > 1)\n");
         appendDirtyJavaScript("          row = box[i];\n");
         appendDirtyJavaScript("       else\n");
         appendDirtyJavaScript("          row = box;\n");
         appendDirtyJavaScript("       row.checked = true;\n");
         appendDirtyJavaScript("       CCA(row, row_no);\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript("    cca = false;\n");
         appendDirtyJavaScript("}\n");	 
   }
	
	
	
	     
	

}
