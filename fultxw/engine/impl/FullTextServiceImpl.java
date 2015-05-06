package ifs.fultxw.engine.impl;

import ifs.fultxw.engine.Constants;
import ifs.fultxw.engine.SearchResult;
import ifs.fultxw.engine.api.FullTextService;
import ifs.fultxw.engine.dao.FullTextDao;
import ifs.fultxw.engine.util.FtpUtil;
import ifs.fultxw.engine.util.LuceneUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class FullTextServiceImpl implements FullTextService,Constants{
	private static Analyzer analyzer = null;
	private volatile boolean creatingIndex = false;

	public static void main(String[] args) { 
		FullTextServiceImpl fullTextService = new FullTextServiceImpl();
		
	}
	
	
	/**
	 * just for test
	 */
	public String createIndex() {
		
	    String indexPath = TEST_INDEX_PATH;
	    String filePath = "E:\\fulltexttmp\\doc.doc";
	    boolean create = true;

	    final File file = new File(filePath);
	    if (!file.exists() || !file.canRead()) {
	      System.out.println("Document directory '" +file.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
	      System.exit(1);
	    }
	    
	    Date start = new Date();
	    try {
	      System.out.println("Indexing to directory '" + indexPath + "'...");

	      Directory dir = FSDirectory.open(new File(indexPath));
	      Analyzer analyzer = LuceneUtil.getCreateAnalyzerInstance();
	      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);

	      if (create) {
	        // Create a new index in the directory, removing any
	        // previously indexed documents:
	        iwc.setOpenMode(OpenMode.CREATE);
	      } else {
	        // Add new documents to an existing index:
	        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	      }

	      // Optional: for better indexing performance, if you
	      // are indexing many documents, increase the RAM
	      // buffer.  But if you do this, increase the max heap
	      // size to the JVM (eg add -Xmx512m or -Xmx1g):
	      //
	      // iwc.setRAMBufferSizeMB(256.0);

	      IndexWriter writer = new IndexWriter(dir, iwc);
	     
	      
	      //
	      // make a new, empty document
          Document doc = new Document();  
          
          // Add the path of the file as a field named "path".  Use a
          // field that is indexed (i.e. searchable), but don't tokenize 
          // the field into separate words and don't index term frequency
          // or positional information:
	      Field pathField = new Field("path", file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(pathField);
          
          // Add the last modified date of the file a field named "modified".
          // Use a NumericField that is indexed (i.e. efficiently filterable with
          // NumericRangeFilter).  This indexes to milli-second resolution, which
          // is often too fine.  You could instead create a number based on
          // year/month/day/hour/minutes/seconds, down the resolution you require.
          // For example the long value 2011021714 would mean
          // February 17, 2011, 2-3 PM.
          NumericField modifiedField = new NumericField("modified");
          modifiedField.setLongValue(file.lastModified());
          doc.add(modifiedField);
	      
	      
          // Add the contents of the file to a field named "contents".  Specify a Reader,
          // so that the text of the file is tokenized and indexed, but not stored.
          // Note that FileReader expects the file to be in UTF-8 encoding.
          // If that's not the case searching for special characters will fail.
//          doc.add(new Field("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));
          doc.add(new Field("contents", new FileParserImpl().parseDoc(filePath),Field.Store.NO,Field.Index.ANALYZED));

          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
            // New index, so we just add the document (no old document can be there):
            System.out.println("adding " + file);
            writer.addDocument(doc);
          } else {
            // Existing index (an old copy of this document may have been indexed) so 
            // we use updateDocument instead to replace the old one matching the exact 
            // path, if present:
            System.out.println("updating " + file);
            writer.updateDocument(new Term("path", file.getPath()), doc);
          }
	      //
	      // NOTE: if you want to maximize search performance,
	      // you can optionally call forceMerge here.  This can be
	      // a terribly costly operation, so generally it's only
	      // worth it when your index is relatively static (ie
	      // you're done adding documents to it):
	      //
	      // writer.forceMerge(1);
	      writer.close();
	      Date end = new Date();
	      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	    } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	    }
		return null;
	}

	/**
	 * 
	 * @param docClass
	 * @param docNo
	 * @param docSheet
	 * @param docRev
	 * @return
	 */
	public String createIndex(String doc_class, String doc_no, String doc_sheet, String doc_rev){
		String result = null;
		if(creatingIndex){
			return FLAG_INDEX_IS_CREATING;
		}
		creatingIndex = true;
		
		try{
		   
		    Set<String> configuredColumns = FullTextDao.getConfiguredColumnsFor(doc_class);
		    String indexPath = FullTextDao.getIndexPathByDocClass(doc_class);
		    
		    
		    if(indexPath == null || "".equals(indexPath)){
		       return ifs.fultxw.engine.Constants.RET_ERROR_INDEX_PATH_NOT_CONFIGURED;
		    }
		    
		    if(configuredColumns == null ||  configuredColumns.isEmpty()){
		       return ifs.fultxw.engine.Constants.RET_ERROR_CLASS_COLUMN_NO_CONFIGURED;
		    }
		   
			result = createIndex1(doc_class, doc_no, doc_sheet, doc_rev);
		}finally{
			creatingIndex = false;
		}
		return result;
	}
	
	public String createIndex(String doc_class){
		String result = null;
		if(creatingIndex){
			return FLAG_INDEX_IS_CREATING;
		}
		creatingIndex = true;
		try{
	      Set<String> configuredColumns = FullTextDao.getConfiguredColumnsFor(doc_class);
	      String indexPath = FullTextDao.getIndexPathByDocClass(doc_class);
	      
	      
	      if(indexPath == null || "".equals(indexPath)){
	         return ifs.fultxw.engine.Constants.RET_ERROR_INDEX_PATH_NOT_CONFIGURED;
	      }
	      
	      if(configuredColumns == null ||  configuredColumns.isEmpty()){
	         return ifs.fultxw.engine.Constants.RET_ERROR_CLASS_COLUMN_NO_CONFIGURED;
	      }
			result = createIndex1(doc_class);
		}finally{
			creatingIndex = false;
		}
		return result;
	}
	/**
	 * 
	 * @param docClass
	 * @param docNo
	 * @param docSheet
	 * @param docRev
	 * @return
	 */
	private String createIndex1(String doc_class, String doc_no, String doc_sheet, String doc_rev){
		//get docIssue 
		List<Map<String, String>> docIssueList = FullTextDao.getDocIssueList(doc_class, doc_no, doc_sheet, doc_rev);
		Map<String,Map<String,Map<String,String>>>  emdFileInfoMap = FullTextDao.getEdmFileInfo(doc_class, doc_no, doc_sheet, doc_rev);
		Set<String> configuredColumns = FullTextDao.getConfiguredColumnsFor(doc_class);
		String indexPath = FullTextDao.getIndexPathByDocClass(doc_class);
		


		IndexWriter writer = null;
		 Directory dir = null;
	    Date start = new Date();
	    try {
	      System.out.println("Indexing to directory '" + indexPath + "'...");

	      dir = FSDirectory.open(new File(indexPath));
	      Analyzer analyzer = LuceneUtil.getCreateAnalyzerInstance();
	      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
	      iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);//just update
	      writer = new IndexWriter(dir, iwc);
	      Document doc = null;
	      for (Iterator iterator = docIssueList.iterator(); iterator.hasNext();) {
	    	// make a new, empty document
	    	doc = new Document();
			Map<String, String> map = (Map<String, String>) iterator.next();
			String tmp_doc_class = map.get(DOC_CLASS);
			String tmp_doc_no = map.get(DOC_NO);
			String tmp_doc_sheet = map.get(DOC_SHEET);
			String tmp_doc_rev = map.get(DOC_REV);
			
			String pk = FullTextDao.pk(tmp_doc_class, tmp_doc_no, tmp_doc_sheet, tmp_doc_rev);
			//index field
			
			Field pkField = new Field(Constants.uniqueIdentifiers,pk,Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS);
			pkField.setIndexOptions(IndexOptions.DOCS_ONLY);
			doc.add(pkField);
			
			Set<Entry<String, String>> entrySet  = map.entrySet();
			for (Iterator iterator2 = entrySet.iterator(); iterator2.hasNext();) {
				Entry<String, String> entry = (Entry<String, String>) iterator2.next();
				if(null != entry.getValue()){
					if(configuredColumns.contains(entry.getKey())){
						doc.add(new Field(entry.getKey(),entry.getValue(),Field.Store.YES,Field.Index.ANALYZED));
					}else {//
						doc.add(new Field(entry.getKey(),entry.getValue(),Field.Store.YES,Field.Index.NO));
					}
				}
			}
			
			//index edm_file
			Map<String, Map<String, String>> docIssueFileMap =  emdFileInfoMap.get(pk);
			Map<String, String> edmFileInfo = null;
			StringBuffer fileContentsBuffer = new StringBuffer(); 
			if(docIssueFileMap != null && docIssueFileMap.size() > 0){
				Set<String> fileNoSet = docIssueFileMap.keySet();
				for (Iterator iterator2 = fileNoSet.iterator(); iterator2.hasNext();) {
	            String string = (String) iterator2.next();
	            edmFileInfo = docIssueFileMap.get(string);
	         	String edmFilePath = edmFileInfo.get("path");
					if(null != edmFilePath){// file is not stored on the ftp
						String edmLocationName = edmFileInfo.get("location_name");
						String edmFileName = edmFileInfo.get("file_name");
						String localTempDir = FullTextDao.getLocalTempDir();
						String localTempFileName = localTempDir + "\\" + edmFileName;
						
						//get files from ftp
						downloadFileFromFtp(edmLocationName, edmFileName, localTempFileName);
						fileContentsBuffer.append(parseFile(localTempFileName));
					}
            }
				doc.add(new Field(FILE_CONTENTS,fileContentsBuffer.toString(),Field.Store.YES,Field.Index.ANALYZED));
			}

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            // New index, so we just add the document (no old document can be there):
	            System.out.println("adding " + pk);
	            writer.addDocument(doc);
	          } else {
	            System.out.println("updating " + pk);
	            writer.updateDocument(new Term(Constants.uniqueIdentifiers, pk), doc);
	          }
		}
	      Date end = new Date();
	      System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	      //insert index path infomation
	      FullTextDao.insertIndexPathIfNotExists(indexPath);
	    } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	      throw new RuntimeException(e);
	    }finally{// this can not be deleted 
	   	 try {
	         if(IndexWriter.isLocked(dir)){// 
	               writer.close();
	            	IndexWriter.unlock(dir);  
	            }
         } catch (CorruptIndexException e) {
	         e.printStackTrace();
         } catch (IOException e) {
	         e.printStackTrace();
         }
	    }
		return FLAG_CREATEINDEX_SUCCESS;
	}
	/**
	 * 
	 * @param docClass
	 * @return
	 */
	private String createIndex1(String doc_class){
		List<Map<String, String>> docIssueList = FullTextDao.getDocIssueListByDocClass(doc_class);
		Map<String,Map<String,Map<String,String>>>  emdFileInfoMap = FullTextDao.getEdmFileInfoByDocClass(doc_class);
		Set<String> configuredColumns = FullTextDao.getConfiguredColumnsFor(doc_class);
		
		String indexPath = FullTextDao.getIndexPathByDocClass(doc_class);
		
		boolean create = true;
		IndexWriter writer = null;
		 Directory dir = null;
	    Date start = new Date();
	    try {
	      System.out.println("Indexing to directory '" + indexPath + "'...");

	      dir = FSDirectory.open(new File(indexPath));
	      Analyzer analyzer = LuceneUtil.getCreateAnalyzerInstance();
	      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);

	      if (create) {
	        // Create a new index in the directory, removing any
	        // previously indexed documents:
	        iwc.setOpenMode(OpenMode.CREATE);
	      } else {
	        // Add new documents to an existing index:
	        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	      }

	      // Optional: for better indexing performance, if you
	      // are indexing many documents, increase the RAM
	      // buffer.  But if you do this, increase the max heap
	      // size to the JVM (eg add -Xmx512m or -Xmx1g):
	      //
	      // iwc.setRAMBufferSizeMB(256.0);
	   
	      writer = new IndexWriter(dir, iwc);
	      Document doc = null;
	      for (Iterator iterator = docIssueList.iterator(); iterator.hasNext();) {
	    	// make a new, empty document
	    	doc = new Document();
			Map<String, String> map = (Map<String, String>) iterator.next();
			String tmp_doc_class = map.get(DOC_CLASS);
			String tmp_doc_no = map.get(DOC_NO);
			String tmp_doc_sheet = map.get(DOC_SHEET);
			String tmp_doc_rev = map.get(DOC_REV);
			
			String pk = FullTextDao.pk(tmp_doc_class, tmp_doc_no, tmp_doc_sheet, tmp_doc_rev);
			//index field
			
			Field pkField = new Field(Constants.uniqueIdentifiers,pk,Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS);
			pkField.setIndexOptions(IndexOptions.DOCS_ONLY);
			doc.add(pkField);
			
			Set<Entry<String, String>> entrySet  = map.entrySet();
			for (Iterator iterator2 = entrySet.iterator(); iterator2.hasNext();) {
				Entry<String, String> entry = (Entry<String, String>) iterator2.next();
				if(null != entry.getValue()){
					if(configuredColumns != null && configuredColumns.contains(entry.getKey())){
						doc.add(new Field(entry.getKey(),entry.getValue(),Field.Store.YES,Field.Index.ANALYZED));
					}else {//
						doc.add(new Field(entry.getKey(),entry.getValue(),Field.Store.YES,Field.Index.NO));
					}
				}
			}
			
			//index edm_file
			Map<String, Map<String, String>> docIssueFileMap =  emdFileInfoMap.get(pk);
			Map<String, String> edmFileInfo = null;
			StringBuffer fileContentsBuffer = new StringBuffer(); 
			if(docIssueFileMap != null && docIssueFileMap.size() > 0){
				Set<String> fileNoSet = docIssueFileMap.keySet();
				for (Iterator iterator2 = fileNoSet.iterator(); iterator2.hasNext();) {
	            String string = (String) iterator2.next();
	            edmFileInfo = docIssueFileMap.get(string);
	         	String edmFilePath = edmFileInfo.get("path");
					if(null != edmFilePath){// file is not stored on the ftp
						String edmFileName = edmFileInfo.get("file_name");
						String edmLocationName = edmFileInfo.get("location_name");
						String localTempDir = FullTextDao.getLocalTempDir();
						String localTempFileName = localTempDir + "\\" + edmFileName;
						
						//get files from ftp
						downloadFileFromFtp(edmLocationName, edmFileName, localTempFileName);
						fileContentsBuffer.append(parseFile(localTempFileName));
					}
            }
				doc.add(new Field(FILE_CONTENTS,fileContentsBuffer.toString(),Field.Store.YES,Field.Index.ANALYZED));
			}

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            // New index, so we just add the document (no old document can be there):
	            System.out.println("adding " + pk);
	            writer.addDocument(doc);
	          } else {
	            // Existing index (an old copy of this document may have been indexed) so 
	            // we use updateDocument instead to replace the old one matching the exact 
	            // path, if present:
	            System.out.println("updating " + pk);
	            writer.updateDocument(new Term(Constants.uniqueIdentifiers, pk), doc);
	          }
		}
	      // NOTE: if you want to maximize search performance,
	      // you can optionally call forceMerge here.  This can be
	      // a terribly costly operation, so generally it's only
	      // worth it when your index is relatively static (ie
	      // you're done adding documents to it):
	      //
	      // writer.forceMerge(1);

	      Date end = new Date();
	      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	      //insert index path infomation
	      FullTextDao.insertIndexPathIfNotExists(indexPath);
	    } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	      throw new RuntimeException(e);
	    }finally{// this can not be deleted 
	   	 try {
	         if(IndexWriter.isLocked(dir)){// 
	               writer.close();
	            	IndexWriter.unlock(dir);  
	            }
         } catch (CorruptIndexException e) {
	         e.printStackTrace();
         } catch (IOException e) {
	         e.printStackTrace();
         }
	    }
		
		return FLAG_CREATEINDEX_SUCCESS;
	}
	
	private String parseFile(String filePath) {
		String sHtmlText = "";
		filePath = filePath.toLowerCase();
		System.out.println("filePath="+filePath);
		if (false  == new File(filePath).exists())
			return "";
		if (filePath.toLowerCase().endsWith(".doc")) { 
			return new FileParserImpl().parseDoc(filePath);
		} else if (filePath.toLowerCase().endsWith(".ppt")) { 
			return "";
		} else if (filePath.toLowerCase().endsWith(".pdf")) { 
			return new FileParserImpl().parsePdf(filePath);
		} else if (filePath.toLowerCase().endsWith(".txt")) {
			return "";
		} else if (filePath.toLowerCase().endsWith(".tif")) {
			filePath = filePath.replace(".tif", ".txt");
			return "";
		} else if (filePath.toLowerCase().endsWith(".xls")) {
			return "";
		} else{
			//TODO
			System.out.println("currently can not parse file: " + filePath);
		}
		return "";
	}
	
	public String getIndexPath() {
		
		return null;
	}

	public Analyzer analyzerInstance() {
		if (analyzer == null)
			return  new IKAnalyzer();
		else
			return analyzer;
	}

	public String indexFullName(String docType) {
		
		return null;
	}

	public List<Map<String, String>> getDocumentInfo() {
		
		return null;
	}


	/**
	 * 测试用
	 */
	public void searchIndex() {
		String line = "共和国";
		String field = FILE_CONTENTS;
		IndexReader reader = null;
		try {
			reader = IndexReader.open(FSDirectory.open(new File(TEST_INDEX_PATH+"\\TF")));
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = analyzerInstance();
	    QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);
	    
	      Query query = null;
		try {
			query = parser.parse(line);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	      System.out.println("Searching for: " + query.toString(field));
	    
	    
	    try {
	    	TopDocs topDocs = searcher.search(query, 25);
			
			
	    	ScoreDoc[] hits = topDocs.scoreDocs;
	    	
	    	System.out.println("总共命中" + hits.length);
	    	
	    	
	    	Document doc = null;
	    	for (int i = 0; i < hits.length; i++) {
	    		doc = searcher.doc(hits[i].doc);
	    		
	    		String pk = doc.get(Constants.uniqueIdentifiers);
	    		System.out.println("pk: " + pk);
	    		System.out.println("file_contents: " +  doc.get(FILE_CONTENTS));
			}
	    	
	    	
			
			
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
		
		
		
	      try {
			searcher.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


//	public Map<String, Map<String, String>> searchIndexxx(String keyWord,String docClass) {
//		if (null == keyWord || "".equals(keyWord)) {
//			return null;// TODO
//		}
//		if(docClass == null || "".equals(docClass)){
//			return searchIndex(keyWord);
//		}
//		
//		Map<String, Map<String, String>> retMap = new HashMap<String,Map<String,String>>();
//		
//
//		String line = keyWord;
//		String field = FILE_CONTENTS;
//		IndexReader reader = null;
//		try {
//			reader = IndexReader.open(FSDirectory.open(new File(TEST_INDEX_PATH+ "\\" + docClass)));
//		} catch (CorruptIndexException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		IndexSearcher searcher = new IndexSearcher(reader);
//		Analyzer analyzer = analyzerInstance();
//		QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);
//
//		Query query = null;
//		try {
//			query = parser.parse(line);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Searching for: " + query.toString(field));
//
//		try {
//			TopDocs topDocs = searcher.search(query, 25);
//			ScoreDoc[] hits = topDocs.scoreDocs;
//			System.out.println("总共命中" + hits.length);
//			Document doc = null;
//			
//			Map<String,String> hitMap = null;
//			for (int i = 0; i < hits.length; i++) {
//				hitMap = new HashMap<String,String>();
//				doc = searcher.doc(hits[i].doc);
//				String pk = doc.get(Constants.uniqueIdentifiers);
//				
//				hitMap.put(PK, pk);
//				hitMap.put(DOC_CLASS, doc.get(DOC_CLASS));
//				hitMap.put(DOC_NO, doc.get(DOC_NO));
//				hitMap.put(DOC_SHEET, doc.get(DOC_SHEET));
//				hitMap.put(DOC_REV, doc.get(DOC_REV));
//				hitMap.put(FILE_CONTENTS, doc.get(FILE_CONTENTS).substring(0, doc.get(FILE_CONTENTS).length() > 50 ? 50 :doc.get(FILE_CONTENTS).length()));
//				
////				System.out.println("file_contents: " + doc.get(FILE_CONTENTS));
//				
//				retMap.put(pk, hitMap);
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			searcher.close();
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(retMap);
//		return retMap;
//	}
	
	private void addQuery(String field, Analyzer analyzer, BooleanQuery booleanQuery,String k) throws Exception {

		if (k.contains("OR")) {
			String[] keys = k.split("OR");
			BooleanQuery bool = new BooleanQuery();
			for (String key : keys) {
				if (null == key || key.trim().equals(""))
					continue;
				this.parserKeyword(field, analyzer, bool, key);
				booleanQuery.add(bool, BooleanClause.Occur.SHOULD);
			}
		} else {
			this.parserKeyword(field, analyzer, booleanQuery, k);
		}

	}
	private void parserKeyword(String field, Analyzer analyzer,BooleanQuery booleanQuery, String k) throws Exception {
		if (null == k || k.trim().equals(""))
			return;

		if (k.startsWith("+") || k.startsWith("-")) {
			k = k.substring(1, k.length());
		}
		if (k.length() > 3) {
			QueryParser queryParser = new QueryParser(Version.LUCENE_31,field, analyzer);
			queryParser.setDefaultOperator(Operator.AND);
			booleanQuery.add(queryParser.parse(k), BooleanClause.Occur.SHOULD);
		} else {
			booleanQuery.add(new PrefixQuery(new Term(field, k)),BooleanClause.Occur.SHOULD);
		}
	}
	
	public SearchResult searchIndex(String keyWords, String docClass, int page,int pageper){
		long startTime = System.currentTimeMillis();
		SearchResult ret = new SearchResult();
		if (null == keyWords || "".equals(keyWords)) {
			ret.setCastTime(0);
			ret.setPageCount(0);
			ret.setResultCount(0);
			return ret;
		}
		
		boolean boolSearchAll = docClass == null || "".equals(docClass); // if docClass is not specified, then all indexPath is searched.
		
		Map<String, Map<String, String>> retMap = new HashMap<String,Map<String,String>>();
		ArrayList<String> indexPaths = null;
		if(boolSearchAll){
			indexPaths = getAllIndexPath();
		}else{
			indexPaths = new ArrayList<String>();
			indexPaths.add(FullTextDao.getIndexPathByDocClass(docClass));
		}
		
		ArrayList<String> validIndexPaths = new ArrayList<String>();// in case some index path are deleted
		for (Iterator iterator = indexPaths.iterator(); iterator.hasNext();) {
         String tempPath = (String) iterator.next();
         if(new File(tempPath).exists()){
            validIndexPaths.add(tempPath);
         }
      }
      		
		IndexReader[] indexReaders = new IndexReader[validIndexPaths.size()];
		MultiReader multiReader = null;
		
		try {
			for (int i = 0; i < indexReaders.length; i++) {
			   String tempDir = validIndexPaths.get(i);
			   if(new File(tempDir).exists()){
			      indexReaders[i] = IndexReader.open(FSDirectory.open(new File(tempDir)));
			   }
         }
			multiReader = new MultiReader(indexReaders); 
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IndexSearcher searcher = new IndexSearcher(multiReader);
		Analyzer analyzer = analyzerInstance();
		BooleanQuery booleanQuery = new BooleanQuery();
		List<String> searchWords = new ArrayList<String>();
		for (String configuredField : keyWords.split(" ")) {
			if (configuredField.length() > 0) {
				searchWords.add(configuredField);
			}
		}
		for (String keyword : searchWords) {
			BooleanQuery bool = new BooleanQuery();
			for (String tempField : FullTextDao.getAllColumnSet()) {
				try {
	            this.addQuery(tempField, analyzer, bool, keyword);
            } catch (Exception e) {
	            e.printStackTrace();
            }
			}
			 try {
	         this.addQuery(FILE_CONTENTS, analyzer, bool, keyword);
         } catch (Exception e) {
	         e.printStackTrace();
         }
			if (keyword.startsWith("+")) {
				booleanQuery.add(bool, BooleanClause.Occur.MUST);
			} else if (keyword.startsWith("-")) {
				booleanQuery.add(bool, BooleanClause.Occur.MUST_NOT);
			} else {
				booleanQuery.add(bool, BooleanClause.Occur.SHOULD);
			}
		}
		System.out.println(booleanQuery.toString());
		/////
		try {
			TopDocs topDocs = searcher.search(booleanQuery, 1000);
			ScoreDoc[] hits = topDocs.scoreDocs;
			
			
			int pageCount = getPageCount(hits.length, pageper);
			ret.setCastTime(System.currentTimeMillis() - startTime);
			ret.setResultCount(hits.length);
			ret.setPageCount(pageCount);
			
			Document doc = null;
			Map<String,String> hitMap = null;
			
			
			int indexStart = 0;
			int indexEnd = 0;
			if(page < pageCount){
				indexStart = (page - 1) * pageper;
				indexEnd = page * pageper;
			} else if(page == pageCount){
				indexStart = (page - 1) * pageper;
				indexEnd = hits.length;
			} else {
				//NO-OP
			}
			
			for (int i = indexStart; i < indexEnd; i++) {
				hitMap = new HashMap<String,String>();
				doc = searcher.doc(hits[i].doc);
				String pk = doc.get(Constants.uniqueIdentifiers);
				
				hitMap.put(PK, pk);
				if(null != doc.get(FILE_CONTENTS)){
					hitMap.put(FILE_CONTENTS, doc.get(FILE_CONTENTS).substring(0, doc.get(FILE_CONTENTS).length() > 250 ? 250 :doc.get(FILE_CONTENTS).length()));
				}
				hitMap.put(DOC_CLASS, doc.get(DOC_CLASS));
				hitMap.put(DOC_NO, doc.get(DOC_NO));
				hitMap.put(DOC_SHEET, doc.get(DOC_SHEET));
				hitMap.put(DOC_REV, doc.get(DOC_REV));
				
				for (String tempField : FullTextDao.getAllColumnSet()) {
					hitMap.put(tempField, doc.get(tempField));
				}
				retMap.put(pk, hitMap);
			}
			ret.setResults(retMap);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			searcher.close();
			multiReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(ret);
		return ret;
	}
	
	public String getFileContents(String docClass, String docNo, String docSheet, String docRev){
		String retFileContents = null;
			String line = FullTextDao.pk(docClass, docNo, docSheet, docRev);
			String field = Constants.uniqueIdentifiers;
			IndexReader reader = null;
			try {
				reader = IndexReader.open(FSDirectory.open(new File(FullTextDao.getIndexPathByDocClass(docClass))));
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = analyzerInstance();
			QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);

			Query query = new TermQuery(new Term(field, line));
			
			System.out.println("Searching for: " + query.toString(field));

			try {
				TopDocs topDocs = searcher.search(query, 25);
				ScoreDoc[] hits = topDocs.scoreDocs;
				System.out.println("总共命中" + hits.length);
				Document doc = null;
				
				Map<String,String> hitMap = null;
				for (int i = 0; i < hits.length; i++) {
					hitMap = new HashMap<String,String>();
					doc = searcher.doc(hits[i].doc);
					 retFileContents = doc.get(FILE_CONTENTS);
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				searcher.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return retFileContents;
	}
	
	/**
	 * multiIndex multiField search.
	 */
	public Map<String, Map<String, String>> searchIndex(String keyWords) {
		if (null == keyWords || "".equals(keyWords)) {
			return null;// TODO
		}
		Map<String, Map<String, String>> retMap = new HashMap<String,Map<String,String>>();
		ArrayList<String> indexPaths = getAllIndexPath();
		IndexReader[] indexReaders = new IndexReader[indexPaths.size()];
		MultiReader multiReader = null;
		try {
			for (int i = 0; i < indexReaders.length; i++) {
	         indexReaders[i] = IndexReader.open(FSDirectory.open(new File(indexPaths.get(i))));
         }
			multiReader = new MultiReader(indexReaders); 
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IndexSearcher searcher = new IndexSearcher(multiReader);
		Analyzer analyzer = analyzerInstance();
		BooleanQuery booleanQuery = new BooleanQuery();
		List<String> searchWords = new ArrayList<String>();
		for (String configuredField : keyWords.split(" ")) {
			if (configuredField.length() > 0) {
				searchWords.add(configuredField);
			}
		}
		for (String keyword : searchWords) {
			BooleanQuery bool = new BooleanQuery();
			for (String tempField : FullTextDao.getAllColumnSet()) {
				try {
	            this.addQuery(tempField, analyzer, bool, keyword);
            } catch (Exception e) {
	            e.printStackTrace();
            }
			}
			 try {
	         this.addQuery(FILE_CONTENTS, analyzer, bool, keyword);
         } catch (Exception e) {
	         e.printStackTrace();
         }
			if (keyword.startsWith("+")) {
				booleanQuery.add(bool, BooleanClause.Occur.MUST);
			} else if (keyword.startsWith("-")) {
				booleanQuery.add(bool, BooleanClause.Occur.MUST_NOT);
			} else {
				booleanQuery.add(bool, BooleanClause.Occur.SHOULD);
			}
		}
		System.out.println(booleanQuery.toString());
		/////

		try {
			TopDocs topDocs = searcher.search(booleanQuery, 1000);
			ScoreDoc[] hits = topDocs.scoreDocs;
			System.out.println("****total hit count: " + hits.length);
			Document doc = null;
			
			Map<String,String> hitMap = null;
			for (int i = 0; i < hits.length; i++) {
				hitMap = new HashMap<String,String>();
				doc = searcher.doc(hits[i].doc);
				String pk = doc.get(Constants.uniqueIdentifiers);
				
				hitMap.put(PK, pk);
				if(null != doc.get(FILE_CONTENTS)){
					hitMap.put(FILE_CONTENTS, doc.get(FILE_CONTENTS).substring(0, doc.get(FILE_CONTENTS).length() > 250 ? 250 :doc.get(FILE_CONTENTS).length()));
				}
				hitMap.put(DOC_CLASS, doc.get(DOC_CLASS));
				hitMap.put(DOC_NO, doc.get(DOC_NO));
				hitMap.put(DOC_SHEET, doc.get(DOC_SHEET));
				hitMap.put(DOC_REV, doc.get(DOC_REV));
				
				for (String tempField : FullTextDao.getAllColumnSet()) {
					hitMap.put(tempField, doc.get(tempField));
				}
				retMap.put(pk, hitMap);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			searcher.close();
			multiReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(retMap);
		return retMap;
	}
	
	
	private ArrayList<String> getAllIndexPath(){
		return FullTextDao.getAllIndexPath();
	}
	
	
	/**
	 * establish a ftp connection, download remote file to local disk, then close the connection.
	 * @param location_name
	 * @param edm_file_name
	 * @param local_temp_file_name
	 * @throws SocketException
	 * @throws IOException
	 */
	private void downloadFileFromFtp(String location_name, String edm_file_name,String local_temp_file_name) throws SocketException, IOException {
		FtpUtil util = new FtpUtil();
		Map<String, String> locationMap = FullTextDao.getEdmLocationByLocationName(location_name);

		String location_address = locationMap.get("location_address");
		String location_port = locationMap.get("location_port");
		String path = locationMap.get("path");
		//set default port number as 21.
		int port_number = 21;
		if (null != location_port) {
			port_number = new Integer(location_port).intValue();
		}
//		String ftpUser = FullTextDao.getLocationUser(location_address);
//		String ftpUserPassword = FullTextDao.getFtpPassword(location_address);
		String[] ftpUserPassword = FullTextDao.getLocationUserPassword(location_address);
		util.connectServer(location_address, port_number, ftpUserPassword[0], ftpUserPassword[1],path);
		util.download(edm_file_name, local_temp_file_name);
		util.closeServer();
	}
	
	private int getPageCount(int counts, int pageper) {
		int pageCount = 0;
		if (counts != 0) {

			if (counts % pageper == 0) {
				pageCount = counts / pageper;
			} else {
				pageCount = counts / pageper + 1;
			}
		} else {
			return pageCount;
		}
		return pageCount;
	}
	
}
