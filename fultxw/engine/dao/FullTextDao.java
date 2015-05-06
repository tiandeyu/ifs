package ifs.fultxw.engine.dao;

import ifs.fultxw.engine.Constants;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;



public class FullTextDao implements Constants {
	public static final int REFRESHTIMEINTERVAL = 1000*60;//1min
	public static final Map<String, Long> cacheMapTimer = new HashMap<String,Long>();
	public static final Map<String, Object> cacheMap = new HashMap<String,Object>();
	
	public static final String CACHE_KEY_FULLTEXT_CONFIG = "CACHE_KEY_FULLTEXT_CONFIG";
	public static final String CACHE_KEY_COLUMN_TEMPLATE = "CACHE_KEY_COLUMN_TEMPLATE";
	public static final String CACHE_KEY_CONFIGURED_COLUMN = "CACHE_KEY_CONFIGURED_COLUMN";
	
	public static final String strEdmFileQryStr = "doc_class,doc_no,doc_sheet,doc_rev," +
			"doc_type,file_no,file_name,local_path,checked_in_sign,checked_in_date," +
			"checked_out_sign,checked_out_date,location_name,file_type,path,local_filename," +
			"user_file_name";
	public static final String strEdmLocationQryStr = "location_name,doc_class,description," +
			"current_location,current_location_db,location_type,location_type_db," +
			"location_address,location_port,path";
	public static final Set<String> setEDMFILEColumn = new HashSet<String>();
	public static final String[] allEdmFileColumn = strEdmFileQryStr.split(",");
	public static final String[] allEdmLocationColumn = strEdmLocationQryStr.split(",");
	public static final Map<String, Set<String>> columnNameSetMap = new  HashMap<String, Set<String>>();//cachepub
	public static final Map<String, String> fullTextConfig = new HashMap<String, String>();
	
	static {
		System.currentTimeMillis() ;
		getFullTextConfig();
	}
	
	public static Set<String> getAllColumnSet(){
		return getColumnTemplate().keySet();
	}
	
	
	public static String getAllColumnQryStr(){
		Set<String> allColumnSet  = getAllColumnSet();
		String tmp = allColumnSet.toString();
		return tmp.substring(1, tmp.length()-1);
	}
	
	public static Set<String> getColumnSetByDocClass(String doc_class){
		//TODO 
		return getAllColumnSet();
	}
	
//	public static String getColumnQryStrByDocClass(String doc_class){
//		//TODO 
//		return getAllColumnQryStr();
//	}
	
	
	public static Set<String> getConfiguredColumnsFor(String doc_class){
		 Map<String ,Set<String>> allConfiguredColumns =  getAllConfiguredColumns();
		 return allConfiguredColumns.get(doc_class);
	}
	
	public static Map<String ,Set<String>> getAllConfiguredColumns(){
		Map<String ,Set<String>> result = (Map<String ,Set<String>>) cacheMap.get(CACHE_KEY_CONFIGURED_COLUMN);
		
		if(result != null ){
			Long cachedTime = cacheMapTimer.get(CACHE_KEY_CONFIGURED_COLUMN);
			if(null != cachedTime && (System.currentTimeMillis() - cachedTime ) < REFRESHTIMEINTERVAL){
				return result;
			}
		}
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("SELECT DOC_CLASS,COLUMN_NAME FROM IFS_FULLTEXT_COLUMNS");
		 
		ResultSetHandler<Map<String ,Set<String>>> h = new ResultSetHandler<Map<String ,Set<String>>>() {
			public Map<String ,Set<String>> handle(ResultSet rs) throws SQLException {
				Map<String ,Set<String>> rtMap = new HashMap<String ,Set<String>>();
				Set<String> tmpSet;
				while(rs.next()){
					String DOC_CLASS = rs.getString("DOC_CLASS");
					tmpSet = rtMap.get(DOC_CLASS);
					tmpSet = tmpSet == null ?  new HashSet<String>() : tmpSet;
					String COLUMN_NAME = rs.getString("COLUMN_NAME");
					tmpSet.add(COLUMN_NAME);
					rtMap.put(DOC_CLASS, tmpSet);
				}
				System.out.println("ifs.fultxw.engine.dao.FullTextDao.getAllConfiguredColumns():" + rtMap);
				return rtMap;
			}
		};
		QueryRunner run = new QueryRunner();
		try {
			result = run.query(conn, sb.toString(), h);
			cacheMap.put(CACHE_KEY_CONFIGURED_COLUMN, result);
			cacheMapTimer.put(CACHE_KEY_CONFIGURED_COLUMN, System.currentTimeMillis());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	
	public static Map<String,String[]> getColumnTemplate(){
		Map<String,String[]> result = (Map<String,String[]>) cacheMap.get(CACHE_KEY_COLUMN_TEMPLATE);
		
		if(result != null ){
			Long cachedTime = cacheMapTimer.get(CACHE_KEY_COLUMN_TEMPLATE);
			if(null != cachedTime && (System.currentTimeMillis() - cachedTime ) < REFRESHTIMEINTERVAL){
				return result;
			}
		}
		
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("SELECT COLUMN_NAME,COLUMN_DESC_ZH,COLUMN_DESC_EN FROM IFS_FTX_COLUMN_TEMPLATE ORDER BY ORDER_NUM ASC ");
		 
		ResultSetHandler<Map<String,String[]>> h = new ResultSetHandler<Map<String,String[]>>() {
			public Map<String,String[]> handle(ResultSet rs) throws SQLException {
				Map<String,String[]> rtMap = new LinkedHashMap<String, String[]>(); 
				String[] tempStrArray ;
				while(rs.next()){
					String COLUMN_NAME = rs.getString("COLUMN_NAME");
					String COLUMN_DESC_ZH = rs.getString("COLUMN_DESC_ZH");
					String COLUMN_DESC_EN = rs.getString("COLUMN_DESC_EN");
					tempStrArray = new String[2];
					tempStrArray[0] = COLUMN_DESC_ZH == null ? "" : COLUMN_DESC_ZH;
					tempStrArray[1] = COLUMN_DESC_EN == null ? "" : COLUMN_DESC_EN;
					rtMap.put(COLUMN_NAME, tempStrArray);
				}
				System.out.println("docIssueMap" + rtMap);
				return rtMap;
			}
		};
		QueryRunner run = new QueryRunner();
		try {
			result = run.query(conn, sb.toString(), h);
			cacheMap.put(CACHE_KEY_COLUMN_TEMPLATE, result);
			cacheMapTimer.put(CACHE_KEY_COLUMN_TEMPLATE, System.currentTimeMillis());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	
	
	public static Map<String, String> getDocIssueMap(String doc_class, String doc_no, String doc_sheet, String doc_rev){
		
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("select ");
		sb.append(getAllColumnQryStr());
		sb.append(" from ");
		sb.append(table_doc_issue);
		sb.append(" where ");
		sb.append("doc_class = ? and doc_no = ? and doc_sheet = ? and doc_rev = ?");
		
		 
		ResultSetHandler<Map<String, String>> h = new ResultSetHandler<Map<String, String>>() {
			public Map<String, String> handle(ResultSet rs) throws SQLException {
				Map<String, String> docIssueMap = null; 
				while(rs.next()){
					docIssueMap = new HashMap<String,String>();
					for (Iterator<String> iterator = getAllColumnSet().iterator(); iterator.hasNext();) {
						String colunmName = iterator.next();
						Object value = rs.getObject(colunmName);
						docIssueMap.put(colunmName, value == null ? null : value.toString() );
					}
					System.out.println("docIssueMap" + docIssueMap);
				}
				return docIssueMap;
			}
		};
		QueryRunner run = new QueryRunner();
		Map<String, String> result = null;
		try {
			result = run.query(conn, sb.toString(), h, new String[]{doc_class,doc_no,doc_sheet,doc_rev});
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	
	
	public static List<Map<String, String>> getDocIssueList(String doc_class, String doc_no, String doc_sheet, String doc_rev){
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("select ");
		sb.append(getAllColumnQryStr());
		sb.append(" from ");
		sb.append(table_doc_issue);
		sb.append(" where ");
		sb.append("doc_class = ? and doc_no = ? and doc_sheet = ? and doc_rev = ?");
		
		ResultSetHandler<List<Map<String, String>>> h = new ResultSetHandler<List<Map<String, String>>>() {
			public List<Map<String, String>> handle(ResultSet rs) throws SQLException {
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
				Map<String, String> docIssueMap = null; 
				while(rs.next()){
					docIssueMap = new HashMap<String,String>();
					for (Iterator<String> iterator = getAllColumnSet().iterator(); iterator
					.hasNext();) {
						String colunmName = iterator.next();
						Object value = rs.getObject(colunmName);
						docIssueMap.put(colunmName, value == null ? null : value.toString() );
					}
					System.out.println("docIssueMap" + docIssueMap);
					retList.add(docIssueMap);
				}
				return retList;
			}
		};
		QueryRunner run = new QueryRunner();
		List<Map<String, String>> result = null;
		try {
			result = run.query(conn, sb.toString(), h, new String[]{doc_class,doc_no,doc_sheet,doc_rev});
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	public static List<Map<String, String>> getDocIssueListByDocClass(String doc_class)  {
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("select ");
		sb.append(getAllColumnQryStr());
		sb.append(" from ");
		sb.append(table_doc_issue);
		sb.append(" where ");
		sb.append("doc_class = ? ");
	
		ResultSetHandler<List<Map<String, String>>> h = new ResultSetHandler<List<Map<String, String>>>() {
			public List<Map<String, String>> handle(ResultSet rs) throws SQLException {
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
				Map<String, String> docIssueMap = null; 
				while(rs.next()){
					docIssueMap = new HashMap<String,String>();
					for (Iterator<String> iterator = getAllColumnSet().iterator(); iterator
							.hasNext();) {
						String colunmName = iterator.next();
						Object value = rs.getObject(colunmName);
						docIssueMap.put(colunmName, value == null ? null : value.toString() );
					}
					System.out.println("docIssueMap" + docIssueMap);
					retList.add(docIssueMap);
				}
				return retList;
			}
		};
		QueryRunner run = new QueryRunner();
		List<Map<String, String>> result = null;
		try {
			result = run.query(conn, sb.toString(), h, doc_class);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	/**
	 * get edm_file storage information --THERE ONLY ONE EDM FILE FOR ONE DOC ISSUE.
	 * @param doc_class
	 * @return
	 */
//	public static Map<String, Map<String, String>> getEdmFileInfoByDocClass(String doc_class)  {
//		Connection conn = ConnectionManager.getConnection();
//
//		StringBuffer sb = new StringBuffer("select ");
//		sb.append(strEdmFileQryStr);
//		sb.append(" from ");
//		sb.append(table_edm_file);
//		sb.append(" where ");
//		sb.append("doc_class = ? ");
//		sb.append(" and doc_type='ORIGINAL' AND FILE_NO='1'");
//
//
//		ResultSetHandler<Map<String, Map<String, String>>> h = new ResultSetHandler<Map<String, Map<String, String>>>() {
//			public Map<String, Map<String, String>> handle(ResultSet rs) throws SQLException {
//				Map<String, Map<String, String>> retMap = new HashMap<String, Map<String, String>>();
//				Map<String, String> edmFileMap = null; 
//				String tmp_doc_class = null;
//				String tmp_doc_no = null;
//				String tmp_doc_sheet = null;
//				String tmp_doc_rev = null;
//				while(rs.next()){
//					tmp_doc_class = rs.getObject("doc_class").toString();
//					tmp_doc_no = rs.getObject("doc_no").toString();
//					tmp_doc_sheet = rs.getObject("doc_sheet").toString();
//					tmp_doc_rev = rs.getObject("doc_rev").toString();
//					
//					edmFileMap = new HashMap<String,String>();
//					for (int i = 0; i < allEdmFileColumn.length; i++) {
//						String colunmName = (String) allEdmFileColumn[i];
//						Object value = rs.getObject(colunmName);
//						edmFileMap.put(colunmName, value == null ? null : value.toString() );
//					}
//					System.out.println("edmFileMap" + edmFileMap);
//					retMap.put(pk(tmp_doc_class,tmp_doc_no,tmp_doc_sheet,tmp_doc_rev),edmFileMap);
//				}
//				return retMap;
//			}
//		};
//		QueryRunner run = new QueryRunner();
//		Map<String, Map<String, String>> result = null;
//		try {
//			result = run.query(conn, sb.toString(), h, doc_class);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			DbUtils.closeQuietly(conn);
//		}
//		
//		return result;
//	}
	
	
	public static Map<String, Map<String,  Map<String, String>>> getEdmFileInfo(String doc_class, String doc_no, String doc_sheet, String doc_rev)  {
		Connection conn = ConnectionManager.getConnection();
		
		StringBuffer sb = new StringBuffer("select ");
		sb.append(strEdmFileQryStr);
		sb.append(" from ");
		sb.append(table_edm_file);
		sb.append(" where ");
		sb.append("doc_class = ? and doc_no = ? and doc_sheet = ? and doc_rev = ?");
		sb.append(" and doc_type='ORIGINAL'");// AND FILE_NO='1'
		
		
		ResultSetHandler<Map<String, Map<String,  Map<String, String>>>> h = new ResultSetHandler<Map<String, Map<String,  Map<String, String>>>>() {
			public Map<String, Map<String,  Map<String, String>>> handle(ResultSet rs) throws SQLException {
				Map<String, Map<String,  Map<String, String>>> retMap = new HashMap<String, Map<String,  Map<String, String>>>();
				Map<String, Map<String, String>> docIssueFileMap = null;
				Map<String, String> edmFileMap = null; 
				String tmp_doc_class = null;
				String tmp_doc_no = null;
				String tmp_doc_sheet = null;
				String tmp_doc_rev = null;
				String tmp_file_no = null;
				String pk = null;
				while(rs.next()){
					tmp_doc_class = rs.getObject("doc_class").toString();
					tmp_doc_no = rs.getObject("doc_no").toString();
					tmp_doc_sheet = rs.getObject("doc_sheet").toString();
					tmp_doc_rev = rs.getObject("doc_rev").toString();
					tmp_file_no = rs.getObject("file_no").toString();
					pk = pk(tmp_doc_class,tmp_doc_no,tmp_doc_sheet,tmp_doc_rev);
					
					edmFileMap = new HashMap<String,String>();
					for (int i = 0; i < allEdmFileColumn.length; i++) {
						String colunmName = (String) allEdmFileColumn[i];
						Object value = rs.getObject(colunmName);
						edmFileMap.put(colunmName, value == null ? null : value.toString() );
					}
					
					docIssueFileMap = retMap.get(pk);
					
					if(null == docIssueFileMap){
						docIssueFileMap = new HashMap<String,Map<String, String>>();
					}
					docIssueFileMap.put(tmp_file_no, edmFileMap);
					retMap.put(pk,docIssueFileMap);
				}
				return retMap;
			}
		};
		QueryRunner run = new QueryRunner();
		Map<String, Map<String,  Map<String, String>>> result = null;
		try {
			result = run.query(conn, sb.toString(), h, new String[]{doc_class,doc_no,doc_sheet,doc_rev});
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param location_name
	 * @return
	 */
	public static  Map<String,String> getEdmLocationByLocationName(String location_name){
		Connection conn = ConnectionManager.getConnection();
		
		StringBuffer sb = new StringBuffer("select ");
		sb.append(strEdmLocationQryStr);
		sb.append(" from ");
		sb.append(table_edm_location);
		sb.append(" where ");
		sb.append("location_name = ? ");
		
		ResultSetHandler<Map<String,String>> h = new ResultSetHandler<Map<String,String>>() {
			public Map<String,String> handle(ResultSet rs) throws SQLException {
				Map<String, String> edmLocationMap = new HashMap<String, String>(); 
				while(rs.next()){
					for (int i = 0; i < allEdmLocationColumn.length; i++) {
						String colunmName = (String) allEdmLocationColumn[i];
						Object value = rs.getObject(colunmName);
						edmLocationMap.put(colunmName, value == null ? null : value.toString() );
					}
				}
				return edmLocationMap;
			}
		};
		
		QueryRunner run = new QueryRunner();
		Map<String,String> result = null;
		try {
			result = run.query(conn, sb.toString(), h, location_name);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	
	
	/**
	 * get edm_file storage information --THERE ARE NOT ONLY ONE EDM FILE FOR ONE DOC ISSUE.
	 * @param doc_class
	 * @return
	 */
	public static Map<String, Map<String,  Map<String, String>>> getEdmFileInfoByDocClass(String doc_class)  {
		Connection conn = ConnectionManager.getConnection();
		
		StringBuffer sb = new StringBuffer("select ");
		sb.append(strEdmFileQryStr);
		sb.append(" from ");
		sb.append(table_edm_file);
		sb.append(" where ");
		sb.append("doc_class = ? ");
		sb.append(" and doc_type='ORIGINAL'");// AND FILE_NO='1'
		
		
		ResultSetHandler<Map<String, Map<String,  Map<String, String>>>> h = new ResultSetHandler<Map<String, Map<String,  Map<String, String>>>>() {
			public Map<String, Map<String,  Map<String, String>>> handle(ResultSet rs) throws SQLException {
				Map<String, Map<String,  Map<String, String>>> retMap = new HashMap<String, Map<String,  Map<String, String>>>();
				Map<String, Map<String, String>> docIssueFileMap = null;
				Map<String, String> edmFileMap = null; 
				String tmp_doc_class = null;
				String tmp_doc_no = null;
				String tmp_doc_sheet = null;
				String tmp_doc_rev = null;
				String tmp_file_no = null;
				String pk = null;
				while(rs.next()){
					tmp_doc_class = rs.getObject("doc_class").toString();
					tmp_doc_no = rs.getObject("doc_no").toString();
					tmp_doc_sheet = rs.getObject("doc_sheet").toString();
					tmp_doc_rev = rs.getObject("doc_rev").toString();
					tmp_file_no = rs.getObject("file_no").toString();
					pk = pk(tmp_doc_class,tmp_doc_no,tmp_doc_sheet,tmp_doc_rev);
					
					edmFileMap = new HashMap<String,String>();
					for (int i = 0; i < allEdmFileColumn.length; i++) {
						String colunmName = (String) allEdmFileColumn[i];
						Object value = rs.getObject(colunmName);
						edmFileMap.put(colunmName, value == null ? null : value.toString() );
					}
					
					docIssueFileMap = retMap.get(pk);
					
					if(null == docIssueFileMap){
						docIssueFileMap = new HashMap<String,Map<String, String>>();
					}
					docIssueFileMap.put(tmp_file_no, edmFileMap);
					retMap.put(pk,docIssueFileMap);
				}
				return retMap;
			}
		};
		QueryRunner run = new QueryRunner();
		Map<String, Map<String,  Map<String, String>>> result = null;
		try {
			result = run.query(conn, sb.toString(), h, doc_class);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		
		return result;
	}
	
	
	public static String getIndexPathByDocClass(String doc_class){
		return fullTextConfig.get(Constants.PARA_FULLTEXT_INDEX_ROOT_PATH) + "\\" + doc_class.toUpperCase();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Map<String,String> getFullTextConfig(){
		Map<String,String> result = (Map<String, String>) cacheMap.get(CACHE_KEY_FULLTEXT_CONFIG);
		
		if(result != null ){
			Long cachedTime = cacheMapTimer.get(CACHE_KEY_FULLTEXT_CONFIG);
			if(null != cachedTime && (System.currentTimeMillis() - cachedTime ) < REFRESHTIMEINTERVAL){
				return result;
			}
		}
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("select t.para_key para_key ,t.para_value para_value, t.para_desc para_desc from ifs_fulltext_config t");
		ResultSetHandler<Map<String,String>> h = new ResultSetHandler<Map<String,String>>() {
			public Map<String,String> handle(ResultSet rs) throws SQLException {
				//fullTextConfig
				fullTextConfig.clear();
				Map<String,String> retMap = new HashMap<String,String>();
				String para_key = null;
				String para_value = null;
				while(rs.next()){
					para_key = rs.getObject("para_key").toString();
					para_value = rs.getObject("para_value").toString();
					System.out.println( para_key + ":" + para_value);
					fullTextConfig.put(para_key,para_value);
				}
				retMap = fullTextConfig;
				return retMap;
			}
		};
		QueryRunner run = new QueryRunner();
		
		try {
			result = run.query(conn, sb.toString(), h);
			cacheMap.put(CACHE_KEY_FULLTEXT_CONFIG, result);
			cacheMapTimer.put(CACHE_KEY_FULLTEXT_CONFIG, System.currentTimeMillis());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	
	
	
	public static String getLocalTempDir(){
		return fullTextConfig.get(Constants.PARA_FULLTEXT_LOCAL_TEMP_DIR);
	}
	
	
	/**
	 * 
	 * @param docClass
	 * @param docNo
	 * @param docSheet
	 * @param docRev
	 * @return
	 */
	public static String pk(String docClass, String docNo, String docSheet, String docRev){
		String ret = docClass + text_separator + docNo + text_separator + docSheet  + text_separator + docRev;
		return ret;
	}
	
	
	public static void insertIndexPathIfNotExists(String indexPath){
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("insert into ifs_ftx_index_path_tab (path ,rowversion)values(?,sysdate)");
		
		QueryRunner run = new QueryRunner();
		try {
			run.update(conn, sb.toString(), indexPath);
			System.out.println("indexPath added:" + indexPath);
		} catch (SQLException e) {
			// ignore.
			System.out.println("indexPath already exists:" + indexPath);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	
	public static String[] getLocationUserPassword(String location_address){
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("select location_user,location_password from edm_location_user where location_address= ?");
		
		ResultSetHandler<String[]> h = new ResultSetHandler<String[]>() {
			public String[] handle(ResultSet rs) throws SQLException {
				String[] result = new String[2];
				Map<String, Map<String, String>> docIssueFileMap = null;
				Map<String, String> edmFileMap = null; 
				String tmp_location_user = null;
				String tmp_location_password = null;
				String pk = null;
				while(rs.next()){
					tmp_location_user = rs.getObject("location_user").toString();
					tmp_location_password = rs.getObject("location_password").toString();
					result[0] = tmp_location_user;
					result[1] = tmp_location_password;
				}
				return result;
			}
		};
		QueryRunner run = new QueryRunner();
		String[] result = null;
		try {
			result = run.query(conn, sb.toString(), h, location_address);
			String encryptedPassword = result[1];
			String descryptedPassword = decryptFtpPassword(encryptedPassword);
			result[1] = descryptedPassword;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
	public static String getLocationUser(String location_address){
		Connection conn = ConnectionManager.getConnection();
		CallableStatement clst = null;
		String result = null;

		try {
			clst = conn.prepareCall("{? = call edm_location_user_api.get_location_user( ? )}");
			clst.registerOutParameter(1, Types.NVARCHAR);
			clst.setString(2, location_address);
			clst.execute();
			result = clst.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn, clst, null);
		}
		return result;
	}
	
	
	public static String getFtpPassword(String location_address) {
		String encrytedPassword = getEncrytedWebPassword(location_address);
		return decryptPassword(encrytedPassword);
	}
	private static String getEncrytedWebPassword(String location_address) {
		Connection conn = ConnectionManager.getConnection();
		CallableStatement clst = null;
		String result = null;

		try {
			clst = conn.prepareCall("{? = call edm_location_user_api.get_web_password( ? )}");
			clst.registerOutParameter(1, Types.NVARCHAR);
			clst.setString(2, location_address);
			clst.execute();
			result = clst.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn, clst, null);
		}
		return result;
	}
	
	
	  private static String decryptPassword( String password )
	   {
	      String dePass = new String();
	      int nLen;
	      int nTemp;
	      int nAsciiVal;
	      String str = new String();
	      char charStr;
	      int conLen;
	      int x;
	      String ascStr = new String();
	      Integer intObj= new Integer(0);

	      //decode ascii string

	      nLen=password.length();


	      x = 0;

	      while (x<nLen)
	      {
	         ascStr = password.substring(x, x + 5); 
	         nAsciiVal = intObj.parseInt(ascStr);
	         dePass = dePass + dePass.valueOf((char)nAsciiVal);
	         x = x + 5; 
	      }  


	      password = dePass;

	      dePass="";

	      nLen=password.length();    
	      conLen=nLen;


	      while (nLen>0)
	      {
	         charStr = password.charAt(nLen-1);
	         nAsciiVal=(int)(charStr);

	         if (nAsciiVal==1)
	            nAsciiVal=94;

	         if (nAsciiVal==2)
	            nAsciiVal=61;

	         //subtract if even else add
	         if (( (conLen - nLen + 1) % 2)==0)
	            nAsciiVal = nAsciiVal - (conLen - nLen + 1);
	         else
	            nAsciiVal = nAsciiVal + (conLen - nLen + 1);


	         str = str.valueOf((char)nAsciiVal);       
	         dePass = dePass + str; 
	         nLen--;

	      }

	      return dePass;
	   }
	  
	   private static String decryptFtpPassword(String crypt_pwd)
	   {
	      StringBuffer uncrypt_pwd = new StringBuffer();

	      for (int i = crypt_pwd.length()-1; i>=0; i--)
	      {
	         char ch = crypt_pwd.charAt(i);
	         switch (ch)
	         {
	         case 1: ch = 94;
	            break;
	         case 2: ch = 61;
	            break;
	         }

	         if ((crypt_pwd.length() - i + 1) % 2 == 0)
	         {
	            ch = (char) (ch + (crypt_pwd.length() - i));
	         }
	         else
	         {
	            ch = (char) (ch - (crypt_pwd.length() - i));
	         }
	         uncrypt_pwd.append(ch);
	      }
	      return uncrypt_pwd.toString();
	   }
	
	
	
	
	public static void main(String[] args) {
//		getDocIssueListByDocClass("TF");
//		System.out.println(getEdmFileInfoByDocClass("TF"));;
//		getFullTextConfig();
//		String allColumn = strAllColumn.toUpperCase();
//		
//		String[] colunms = allColumn.split(",");
//		
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < colunms.length; i++) {
//	      sb.append("'").append(colunms[i]).append("',");
//      }
//		
//		System.out.println(sb.toString());
		
		
//		getDocIssueMap("TF", "1000005", "1", "A1");
//		getEdmFileInfo("TF", "1000005", "1", "A1");
//		getDocIssueList("TF", "1000005", "1", "A1");
		
		
//		System.out.println(getEdmLocationByLocationName("TX"));
//		System.out.println(getFtpPassword("127.0.0.1"));
//		System.out.println(getLocationUser("127.0.0.1"));
//		System.out.println(getLocationUserPassword("127.0.0.1"));
//		insertIndexPathIfNotExists("D:\\workspace\\lucene\\indexpath\\TF");
//		temp();
//		getColumnTemplate();
//		System.out.println(getAllColumnSet().toString());
//		System.out.println(getAllColumnQryStr());
//		getAllConfiguredColumns();
//		getAllConfiguredColumns();
//		System.out.println(getConfiguredColumnsFor("TF"));
	}
	
	static void temp(){
		for (Iterator iterator = getAllColumnSet().iterator(); iterator.hasNext();) {
	      String type = (String) iterator.next();
	      System.out.println("INSERT INTO IFS_FTX_COLUMN_TEMPLATE_TAB T (COLUMN_NAME,ROWVERSION) VALUES('" + type.toUpperCase() + "',SYSDATE);");
      }
	}
	
	
	public static ArrayList<String> getAllIndexPath(){
		Connection conn = ConnectionManager.getConnection();
		StringBuffer sb = new StringBuffer("select path from ifs_ftx_index_path_tab ");
		ResultSetHandler<ArrayList<String>> h = new ResultSetHandler<ArrayList<String>>() {
			public ArrayList<String> handle(ResultSet rs) throws SQLException {
				ArrayList<String> ret = new ArrayList<String>(); 
				while(rs.next()){
					ret.add(rs.getString("path"));
				}
				return ret;
			}
		};
		QueryRunner run = new QueryRunner();
		ArrayList<String> result = null;
		try {
			result = run.query(conn, sb.toString(), h);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return result;
	}
}
