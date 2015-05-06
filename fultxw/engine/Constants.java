package ifs.fultxw.engine;

public interface Constants {
	public static final char textSeparator = '^';
	public static final char text_separator = textSeparator;
	public static final char fieldSeparator = (char) 31;
	public static final char field_separator = fieldSeparator;
	public static final char recordSeparator = (char) 30;
	public static final char record_separator = recordSeparator;
	public static final char groupSeparator = (char) 29;
	public static final char group_separator = groupSeparator;
	public static final char fileSeparator = (char) 28;
	
	
	public static final String RET_ERROR_INDEX_PATH_NOT_CONFIGURED = "RET_ERROR_INDEX_PATH_NOT_CONFIGURED";
	public static final String RET_ERROR_CLASS_COLUMN_NO_CONFIGURED = "RET_ERROR_CLASS_COLUMN_NO_CONFIGURED";
	
	String table_doc_issue = "doc_issue";
	String table_edm_file = "edm_file";
	String table_edm_location = "EDM_LOCATION";
	String table_IFS_FTX_COLUMN_TEMPLATE = "IFS_FTX_COLUMN_TEMPLATE";
	
	
	
	
	String uniqueIdentifiers = "uniqueIdentifiers";

	String TEST_FILE_PATH = "D:\\workspace\\lucene\\filepath";
	String TEST_INDEX_PATH = "D:\\workspace\\lucene\\indexpath";
	
	/**
	 * 
	 */
	String PARA_FULLTEXT_INDEX_ROOT_PATH = "FULLTEXT_INDEX_ROOT_PATH";
	String PARA_XPDF_ROOT_PATH = "XPDF_ROOT_PATH";
	String PARA_XPDF_TOOLS_NAME = "XPDF_TOOLS_NAME";
	String PARA_FULLTEXT_LOCAL_TEMP_DIR = "FULLTEXT_LOCAL_TEMP_DIR";
	
	

	String FLAG_CREATEINDEX_SUCCESS = "FLAG_CREATEINDEX_SUCCESS";
	String FLAG_INDEX_IS_CREATING = "FLAG_INDEX_IS_CREATING";
	
	String PK = "PK";
	String DOC_CLASS= "DOC_CLASS";
	String DOC_NO= "DOC_NO";
	String DOC_SHEET= "DOC_SHEET";
	String DOC_REV= "DOC_REV";
	String FILE_CONTENTS= "FILE_CONTENTS";
	
	
	
}