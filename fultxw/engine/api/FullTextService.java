package ifs.fultxw.engine.api;

import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;

public interface FullTextService {
	
	String createIndex();
	
	

	/**
	 * 
	 * @param doc_class
	 * @param doc_no
	 * @param doc_sheet
	 * @param doc_rev
	 * @return
	 */
	String createIndex(String doc_class, String doc_no, String doc_sheet, String doc_rev);
	
	
	/**
	 * 
	 * @param docClass
	 * @return
	 */
	String createIndex(String docClass);
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	String getIndexPath();
	
	/**
	 * SINGLETON
	 * @return
	 */
	Analyzer analyzerInstance();
	
	
	String indexFullName(String docType);
	
	
	
	List<Map<String,String>> getDocumentInfo();
	
	/**
	 * just for test
	 */
	void searchIndex();
	
	
//	Map<String,Map<String,String>> searchIndex(String keyWord,String docClass);
	
	Map<String,Map<String,String>> searchIndex(String keyWord);

}
