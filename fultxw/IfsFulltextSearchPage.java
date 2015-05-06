package ifs.fultxw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fultxw.engine.Constants;
import ifs.fultxw.engine.SearchResult;
import ifs.fultxw.engine.dao.FullTextDao;
import ifs.fultxw.engine.impl.FullTextServiceImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class IfsFulltextSearchPage extends ASPPageProvider implements Constants {

	public IfsFulltextSearchPage(ASPManager mgr, String pagePath) {
	   super(mgr, pagePath);
   }
	
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private boolean show_searche_dialog;
   private boolean show_file_contents;
   SearchResult searchResult ;
   Map<String, Map<String, String>> retMap = null;
   
   String fileContents = null;
   
   
   int pageSize ;
   int currentPage;
	int pageCount ;
	float castTime ;
   
	public void run() throws FndException {
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();

		pageSize = new Integer(mgr.readValue("_page_size_", "10")).intValue();// ;
		currentPage = new Integer(mgr.readValue("_current_page_", "1")).intValue();// ;
		
		String searcheFileContent = mgr.getQueryString("SEARCHFILECONTENTS");

		if (mgr.buttonPressed("Search") || mgr.buttonPressed("Previous") || mgr.buttonPressed("Next")) {
			if (mgr.buttonPressed("Previous")) {
				currentPage -= 1;
			} else if (mgr.buttonPressed("Next")) {
				currentPage += 1;
			}else if (mgr.buttonPressed("Search")) {
				currentPage = 1;
			}

			String keyWordInput = mgr.readValue("keyWordInput");
			if (null == keyWordInput || "".equals(keyWordInput.trim())) {
				mgr.showAlert("key word can not be null.");
				show_searche_dialog = true;
				return;
			}
			String docClass = mgr.readValue("_doc_class_");
			FullTextServiceImpl fullTextService = new FullTextServiceImpl();
			searchResult = fullTextService.searchIndex(keyWordInput, docClass, currentPage, pageSize);
			pageCount = searchResult.getPageCount();
			castTime = searchResult.getCastTime()/1000.0f;
			retMap = searchResult.getResults();

			if (retMap.size() == 0) {
				mgr.showAlert("no document is found!");
				show_searche_dialog = true;
			} else {
				show_searche_dialog = false;
			}

		} else if ("Y".equals(searcheFileContent)){
			show_searche_dialog = false;
			show_file_contents = true;
			
			String temp_doc_class = mgr.getQueryString("DOC_CLASS");
			String temp_doc_no = mgr.getQueryString("DOC_NO");
			String temp_doc_sheet = mgr.getQueryString("DOC_SHEET");
			String temp_doc_rev = mgr.getQueryString("DOC_REV");
			String temp_Search_Keyword = mgr.getQueryString("Search_Keyword");
			
			String keyWordInput = mgr.readValue("keyWordInput");
			
			
			FullTextServiceImpl fullTextService = new FullTextServiceImpl();
			fileContents = fullTextService.getFileContents(temp_doc_class, temp_doc_no, temp_doc_sheet, temp_doc_rev);
			
			System.out.println(fileContents);
			
			
			
			
			
//			searchResult = fullTextService.searchIndex(keyWordInput, docClass, currentPage, pageSize);
			
		}else {
			show_searche_dialog = true;
		}
	}

   
   
   public void preDefine(){
      ASPManager mgr = getASPManager();
      
      headblk = mgr.newASPBlock("Main");
      headblk.addField("PK"  ).setFunction("''").setLabel("PK").setSize(20).setHidden();
      headblk.addField("DOC_CLASS"  ).setFunction("''").setLabel("DOCMAWDOCISSUEHEADDOCCLASS: Doc Class").setSize(20);
      headblk.addField("DOC_NO").setFunction("''").setLabel("DOCMAWDOCISSUEDOCNO: Doc No").setSize(20)
      .setHyperlink("/b2e/secured/fultxw/IfsFulltextIntermediatePage.page?SEARCH=Y", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,SUB_CLASS","NEWWIN");
      headblk.addField("DOC_SHEET").setFunction("''").setLabel("DOCMAWDOCISSUEDOCSHEET: Doc Sheet");
      headblk.addField("DOC_REV").setFunction("''").setLabel("DOCMAWDOCISSUEDOCREV: Doc Rev").setSize(20);
      headblk.addField("SUB_CLASS").setFunction("''").setLabel("DOCMAWDOCISSUEDOCREV: Doc Rev").setSize(20);
      
      headblk.addField("F_1").setFunction("''").setSize(20);
      headblk.addField("F_2").setFunction("''").setSize(20);
      headblk.addField("F_3").setFunction("''").setSize(20);
      headblk.addField("F_4").setFunction("''").setSize(20);
      headblk.addField("F_5").setFunction("''").setSize(20);
      headblk.addField("F_6").setFunction("''").setSize(20);
      headblk.addField("F_7").setFunction("''").setSize(20);
      headblk.addField("F_8").setFunction("''").setSize(20);
      headblk.addField("F_9").setFunction("''").setSize(20);
      headblk.addField("F_10").setFunction("''").setSize(20);
      headblk.addField("F_11").setFunction("''").setSize(20);
      headblk.addField("F_12").setFunction("''").setSize(20);
      headblk.addField("F_13").setFunction("''").setSize(20);
      headblk.addField("F_14").setFunction("''").setSize(20);
      headblk.addField("F_15").setFunction("''").setSize(20);
      headblk.addField("F_16").setFunction("''").setSize(20);
      headblk.addField("F_17").setFunction("''").setSize(20);
      headblk.addField("F_18").setFunction("''").setSize(20);
      headblk.addField("F_19").setFunction("''").setSize(20);
      headblk.addField("F_20").setFunction("''").setSize(20);
      headblk.addField("F_21").setFunction("''").setSize(20);
      headblk.addField("F_22").setFunction("''").setSize(20);
      headblk.addField("F_23").setFunction("''").setSize(20);
      headblk.addField("F_24").setFunction("''").setSize(20);
      headblk.addField("F_25").setFunction("''").setSize(20);
      headblk.addField("F_26").setFunction("''").setSize(20);
      headblk.addField("F_27").setFunction("''").setSize(20);
      headblk.addField("F_28").setFunction("''").setSize(20);
      headblk.addField("F_29").setFunction("''").setSize(20);
      headblk.addField("F_30").setFunction("''").setSize(20);
      headblk.addField("F_31").setFunction("''").setSize(20);
      headblk.addField("F_32").setFunction("''").setSize(20);
      headblk.addField("F_33").setFunction("''").setSize(20);
      headblk.addField("F_34").setFunction("''").setSize(20);
      headblk.addField("F_35").setFunction("''").setSize(20);
      headblk.addField("F_36").setFunction("''").setSize(20);
      headblk.addField("F_37").setFunction("''").setSize(20);
      headblk.addField("F_38").setFunction("''").setSize(20);
      headblk.addField("F_39").setFunction("''").setSize(20);
      headblk.addField("F_40").setFunction("''").setSize(20);
      headblk.addField("F_41").setFunction("''").setSize(20);
      headblk.addField("F_42").setFunction("''").setSize(20);
      headblk.addField("F_43").setFunction("''").setSize(20);
      headblk.addField("F_44").setFunction("''").setSize(20);
      headblk.addField("F_45").setFunction("''").setSize(20);
      headblk.addField("F_46").setFunction("''").setSize(20);
      headblk.addField("F_47").setFunction("''").setSize(20);
      headblk.addField("F_48").setFunction("''").setSize(20);
      headblk.addField("F_49").setFunction("''").setSize(20);
      headblk.addField("F_50").setFunction("''").setSize(20);
      headblk.addField("Search_Keyword").setFunction("''").setSize(20);
      headblk.addField("FILE_CONTENTS").setFunction("''").setLabel("IFSFULLTEXTSEARCHPAGEFILECONTENTS: File Contents").setSize(30).setHyperlink("/b2e/secured/fultxw/IfsFulltextSearchPage.page?SEARCHFILECONTENTS=Y", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,Search_Keyword","NEWWIN");
      headblk.setView("DUAL");
      headset = headblk.getASPRowSet();
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("search result");
      headtbl.setWrap();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.FIND);
      headbar.disableCommand(ASPCommandBar.VIEWDETAILS);
      headbar.disableMinimize();
      headlay = headblk.getASPBlockLayout(); 
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      disableHeader(); 
   }
   
   public void initSearchResult(){
      ASPManager mgr = getASPManager();
   	headset.clear();
   	
   	Map<String,String[]> columnTemplate = FullTextDao.getColumnTemplate();
   	Set<String> allColumnSet = columnTemplate.keySet();
   	
   	Set<Entry<String, Map<String, String>>>  resultSet = retMap.entrySet();
   		for (Iterator iterator = resultSet.iterator(); iterator.hasNext();) {
   	      Entry<String, Map<String, String>> entry = (Entry<String, Map<String, String>>) iterator.next();
   	     String pk =  entry.getKey();
   	     Map<String, String> entryMap = entry.getValue();
   	     headset.addRow(null);
   	     headset.setValue("PK", entryMap.get(PK));
   	     headset.setValue("DOC_CLASS", entryMap.get(DOC_CLASS));
   	     headset.setValue("DOC_NO", entryMap.get(DOC_NO));
   	     headset.setValue("DOC_SHEET", entryMap.get(DOC_SHEET));
   	     headset.setValue("DOC_REV", entryMap.get(DOC_REV));
   	   
   	     int i = 1;
   	     for (Iterator iterator2 = allColumnSet.iterator(); iterator2.hasNext();) {
   		      String columnName = (String) iterator2.next();
   		      headset.setValue("F_" + i++, entryMap.get(columnName));
   	     } 
   	     headset.setValue("FILE_CONTENTS", entryMap.get(FILE_CONTENTS));
   	     headset.setValue("Search_Keyword",  mgr.readValue("keyWordInput"));
         }
   		
   		String tempUserLanguage = mgr.getUserLanguage();
   		int tempIndex = "zh".endsWith(tempUserLanguage) ? 0 : 1;
   	   int j = 1;
 	   	for (Iterator iterator2 = allColumnSet.iterator(); iterator2.hasNext();) {
 		      String columnName = (String) iterator2.next();
 		      String columnDesc = columnTemplate.get(columnName)[tempIndex];
 		      mgr.getASPField("F_" + j++).setLabel(mgr.isEmpty(columnDesc) ? columnName : columnDesc);
 	      }
 	   	
 	   	for (int k = j; k <= 50; k++){
 	   		mgr.getASPField("F_" + k).setHidden();
 	   	}
 	   	mgr.getASPField("Search_Keyword").setHidden();
 	   	
   }
   
   public void adjust(){
   }
   
   public String getDescription() {
      return "IFSFULLTEXTSEARCHPAGEDESCRIPTION: Fulltext Search";
   }

   public String getTitle() {
      return getDescription();
   }
   
	protected AutoString getContents() throws FndException
	{ 
		return super.getContents();
	}
	
	 public ASPBuffer getDocClassBuffer() {
		 ASPManager mgr = getASPManager();
			ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
			String sql = "SELECT DOC_CLASS VALUE,DOC_NAME NAME FROM DOC_CLASS";
			trans.addQuery("DOCCLASS", sql);
			
			trans = mgr.perform(trans);
			
			ASPBuffer buffer = trans.getBuffer("DOCCLASS");
		 return buffer;
	 }
	 
	   public ASPBuffer getPageSizeBuffer() {
	   	 ASPManager mgr = getASPManager();
	      ASPBuffer retBuffer = mgr.newASPTransactionBuffer();
	      ASPBuffer rowBuffer = mgr.newASPTransactionBuffer();
	      rowBuffer = retBuffer.addBuffer("DATA");
	      rowBuffer.addItem("VALUE", "10");
	      rowBuffer.addItem("NAME", "10");
	      rowBuffer = retBuffer.addBuffer("DATA");
	      rowBuffer.addItem("VALUE", "20");
	      rowBuffer.addItem("NAME","20");
	      rowBuffer = retBuffer.addBuffer("DATA");
	      rowBuffer.addItem("VALUE", "50");
	      rowBuffer.addItem("NAME", "50");
	      rowBuffer = retBuffer.addBuffer("DATA");
	      rowBuffer.addItem("VALUE", "100");
	      rowBuffer.addItem("NAME", "100");
	      return retBuffer;
	   }
	
   public void printContents() throws FndException{

   	printHiddenField("_current_page_", "" + currentPage);
   	
      ASPManager mgr = getASPManager();
      if(show_searche_dialog){
         headblk.setHidden();
   		printNewLine();
   		printNewLine();
   		printNewLine();
   		printNewLine();
   		printNewLine();
   		appendToHTML("<table align = 'center'><tr><td>");
   		appendToHTML("<img alt='search log' src='/b2e/secured/common/images/Corporate_Layer_Web_Splash.gif'/>");
   		appendToHTML("</td></tr></table>");
   		appendToHTML("<table align = 'center'><tr><td>");
   		printReadLabel(mgr.translate("DOCMAWDOCISSUEHEADDOCCLASS: Doc Class"));
   		printSelectBox("_doc_class_", getDocClassBuffer(), "");
   		appendToHTML("</td>");
   		appendToHTML("<td>");
   		printField("keyWordInput", "", "", 35);
   		printSubmitButton("Search", mgr.translate("IFSFULLTEXTSEARCHPAGESEARCH: Search"), "");
   		appendToHTML("</td></tr></table>");
   		appendToHTML("<table align = 'right'><tr><td>");
   		printReadLabel(mgr.translate("IFSFULLTEXTSEARCHPAGEPAGESIZE: Page Size"));
   		printSelectBox("_page_size_", getPageSizeBuffer(),""+ pageSize);
   		appendToHTML("</td>");
   		appendToHTML("<td>");
   		appendToHTML("</td></tr></table>");
      } else if(show_file_contents){
      	appendToHTML("<form name='form' >");
      	appendToHTML("<table align = 'center' width='100%' ><tr><td>");
   		appendToHTML(fileContents);
   		appendToHTML("</td></tr></table>");
   		appendToHTML("</form>");
   		
   		appendDirtyJavaScript(getHighlightScript(mgr.getQueryString("Search_Keyword")));
      }else{
   		appendToHTML("<table align = 'center'><tr><td>");
   		printReadLabel(mgr.translate("DOCMAWDOCISSUEHEADDOCCLASS: Doc Class"));
   		printSelectBox("_doc_class_", getDocClassBuffer(), "");
   		appendToHTML("</td>");
   		appendToHTML("<td>");
   		printField("keyWordInput", mgr.readValue("keyWordInput"), "", 35);
   		printSubmitButton("Search",mgr.translate("IFSFULLTEXTSEARCHPAGESEARCH: Search"), "");
   		appendToHTML("</td></tr></table>");
      	initSearchResult();
      	appendToHTML(headlay.show());
   		appendToHTML("<table align = 'center'><tr><td>");
   		printReadLabel(mgr.translate("IFSFULLTEXTSEARCHPAGECASTTIMEIS: Cast time is") + ":");
   		appendToHTML("<font color=\"red\" >");
   		appendToHTML("" + castTime + "s");
   		printReadLabel(",");
   		appendToHTML("</font>");
   		printReadLabel(mgr.translate("IFSFULLTEXTSEARCHPAGETOTALRESULT: Total result") + ":" + searchResult.getResultCount() + "   ");
   		printReadLabel(",");
   		printReadLabel(mgr.translate("IFSFULLTEXTSEARCHPAGETOTALPAGE: Total page")+ ":" + pageCount + "   ");
   		printReadLabel(",");
   		printReadLabel(mgr.translate("IFSFULLTEXTSEARCHPAGECURRENTPAGE: Current page") + ":" + currentPage);
   		printReadLabel("   ");
   		printSubmitButton("Previous", mgr.translate("IFSFULLTEXTSEARCHPAGEPREVIOUSPAGE: Previous Page"), currentPage > 1 ? "" : "disabled");
   		printSubmitButton("Next", mgr.translate("IFSFULLTEXTSEARCHPAGENEXTPAGE: Next Page"), currentPage >= pageCount ? "disabled" : "");
   		appendToHTML("</td>");
   		appendToHTML("<td>");
   		printReadLabel(mgr.translate("IFSFULLTEXTSEARCHPAGEPAGESIZE: Page Size"));
   		printSelectBox("_page_size_", getPageSizeBuffer(), ""+ pageSize);
   		appendToHTML("</td></tr></table>");
      	appendDirtyJavaScript(getHighlightScript(mgr.readValue("keyWordInput")));
      }
   }
   
   private String getHighlightScript(String keywords){
   	ASPManager mgr = getASPManager();
   	StringBuffer sb  = new StringBuffer();
   	sb.append("	function MarkHighLight(obj,hlWords,cssClass){ ").append("\n");
   	sb.append("    	hlWords=AnalyzeHighLightWords(hlWords);").append("\n");
   	sb.append(" 		if(obj==null || hlWords.length==0)").append("\n");
   	sb.append("     		return;").append("\n");
   	sb.append("     	if(cssClass==null)").append("\n");
   	sb.append("     		cssClass=\"highlight\";").append("\n");
   	sb.append("     	MarkHighLightCore(obj,hlWords);").append("\n");
   	sb.append("     	function MarkHighLightCore(obj,keyWords){").append("\n");
   	sb.append("     		var re=new RegExp(keyWords, \"i\"); ").append("\n");
   	sb.append("       	for(var i=0; i<obj.childNodes.length; i++){").append("\n");
   	sb.append("        		var childObj=obj.childNodes[i];").append("\n");
   	sb.append("       		if(childObj.nodeType==3){").append("\n");
   	sb.append("     			if(childObj.data.search(re)==-1)continue; ").append("\n");
   	sb.append("  			var reResult=new RegExp(\"(\"+keyWords+\")\", \"gi\"); ").append("\n");
   	sb.append(" 			var objResult=document.createElement(\"span\");").append("\n");
   	sb.append("				objResult.innerHTML=childObj.data.replace(reResult,\"<FONT COLOR=#ff0000 STYLE=BACKGROUND:#ffff00>$1</FONT>\");       ").append("\n");
//   	sb.append(" 			if(childObj.data==objResult.childNodes[0].innerHTML) continue; ").append("\n");
   	sb.append(" 			obj.replaceChild(objResult,childObj);        	").append("\n");
   	sb.append(" 		}else if(childObj.nodeType==1){").append("\n");
   	sb.append(" 			MarkHighLightCore(childObj,keyWords);").append("\n");
   	sb.append("       		} ").append("\n");
   	sb.append("         	}").append("\n");
   	sb.append("    	} ").append("\n");
   	sb.append("    	function AnalyzeHighLightWords(hlWords)").append("\n");
   	sb.append("    	{").append("\n");
   	sb.append("    		if(hlWords==null) return \"\";").append("\n");
   	sb.append("    		hlWords=hlWords.replace(/\\s+/g,\"|\").replace(/\\|+/g,\"|\");    		").append("\n");
   	sb.append(" 		   hlWords=hlWords.replace(/(^\\|*)|(\\|*$)/g, \"\");").append("\n");
   	sb.append(" 		   if(hlWords.length==0) return \"\";").append("\n");
   	sb.append("   			var wordsArr=hlWords.split(\"|\"); ").append("\n");
   	sb.append("  		if(wordsArr.length>1){").append("\n");
   	sb.append(" 			var resultArr=BubbleSort(wordsArr);").append("\n");
   	sb.append(" 			var result=\"\";").append("\n");
   	sb.append(" 	for(var i=0;i<resultArr.length;i++){").append("\n");
   	sb.append(" 	result=result+\"|\"+resultArr[i];").append("\n");
   	sb.append(" 	}  ").append("\n");
   	sb.append(" 	return result.replace(/(^\\|*)|(\\|*$)/g, \"\");").append("\n");
   	sb.append("  		}else{ ").append("\n");
   	sb.append("   			return hlWords;").append("\n");
   	sb.append("  		} ").append("\n");
   	sb.append("  	}  ").append("\n");
   	sb.append(" 	function BubbleSort(arr){		").append("\n");
   	sb.append(" 	var temp, exchange;	").append("\n");
   	sb.append(" 	for(var i=0;i<arr.length;i++){			").append("\n");
   	sb.append(" 		exchange=false;			").append("\n");
   	sb.append(" 		for(var j=arr.length-2;j>=i;j--){		").append("\n");
   	sb.append(" 		if((arr[j+1].length)>(arr[j]).length){").append("\n");
   	sb.append(" 			temp=arr[j+1]; arr[j+1]=arr[j]; arr[j]=temp;").append("\n");
   	sb.append(" 		exchange=true;").append("\n");
   	sb.append(" 		}").append("\n");
   	sb.append(" 	}	").append("\n");
   	sb.append("	if(!exchange)break; ").append("\n");
   	sb.append("} ").append("\n");
   	sb.append("return arr;	 ").append("\n");
   	sb.append("} ").append("\n");
   	sb.append(" } ").append("\n");
   	sb.append("   function MarkHighLightDemo(){").append("\n");
   	sb.append(" 	var txtObj=document.getElementById(\"txtInput\");").append("\n");
   	sb.append(" var divObj=document.getElementsByName(\"form\");").append("\n");
   	sb.append(" 	MarkHighLight(divObj[0],'"+ keywords +"');").append("\n");
   	sb.append("    } ").append("\n");
   	sb.append(" MarkHighLightDemo(); ").append("\n");
  
   	return sb.toString();
   }
   
   
   
   
}
