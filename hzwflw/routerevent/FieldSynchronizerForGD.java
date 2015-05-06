package ifs.hzwflw.routerevent;

import ifs.fnd.util.Str;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.horizon.db.Access;
import com.horizon.organization.orgimpl.OrgSelectImpl;
import com.horizon.workflow.flowengine.impl.XMLEventInterface;
/**
 * 
 * @author luqingwei 2014Äê2ÔÂ13ÈÕ11:06:03
 *
 */
public class FieldSynchronizerForGD  implements XMLEventInterface{
	
	
	/**
	 * 
	 * @param currentWorkId
	 * @param nodeId
	 * @return
	 * 0:workid
	 * 1:nodeid
	 * 2:rowid
	 * 3:tablename
	 * 4:pagePath
	 * 
	 */
	public static String[] getBasicData(final String currentWorkId,final String nodeId){
		String sqlConn = "select biz_objid bizObjid, biz_page_path bizPagePath, biz_view bizView from hz_biz_wf_conn t  where t.process_id= ?";
		List<String> lstConditions = new ArrayList<String>();
		lstConditions.add(currentWorkId);
		Map map = Access.getSingleMap(sqlConn, lstConditions);
		
		String bizObjid = (String)map.get("bizobjid");
		String bizPagePath = (String)map.get("bizpagepath");
		String bizView = (String)map.get("bizview");
		
		String whereCondition  = "";
		String sqlDataType = "select COLUMN_NAME,DATA_TYPE from SYS.USER_TAB_COLUMNS t where t.TABLE_NAME= ? and t.COLUMN_NAME=?";
		String [] tempObjidArray = bizObjid.split("\\^");
		String tempCol = null;
		String tempColValue = null;
		String[] tempKv = null;
		String tempDataType = null;
		for (int i = 0; i < tempObjidArray.length; i++) {
			tempKv = tempObjidArray[i].split("\\=");
			tempCol = tempKv[0];
			tempColValue = tempKv[1];
			lstConditions = new ArrayList<String>();
			lstConditions.add(bizView);
			lstConditions.add(tempCol);
			map = Access.getSingleMap(sqlDataType, lstConditions);
			tempDataType = (String)map.get("data_type");
			
			if("VARCHAR2".equals(tempDataType)){
				whereCondition += "" + tempCol + "=" + "'"  + tempColValue + "' and ";
			}else if("NUMBER".equals(tempDataType)){
				whereCondition += "" + tempCol + "=" + tempColValue + " and ";
			}else{
				whereCondition += "" + tempCol + "=" + tempColValue + " and ";//TODO LQW
			}
		}
		whereCondition += " 1=1";
		
		String rowId = null;
		String sqlRowid = "select objid objid from " + bizView + " where " + whereCondition;
		map = Access.getSingleMap(sqlRowid, null);
		oracle.sql.ROWID ROWID = ((oracle.sql.ROWID)map.get("objid"));
		rowId = ROWID.stringValue();
		
		
		String tableName = "";
		String sqlTableName = "select t.table_name table_name from HZ_PAGE_TABLE_PAIR t where t.page_path= ? ";
		lstConditions = new ArrayList<String>();
		lstConditions.add(bizPagePath);
		map = Access.getSingleMap(sqlTableName, lstConditions);
		
		if(null != map ){
		   tableName = (String)map.get("table_name");
		}
		
		String[] retArray = new String[5];
		retArray[0] = currentWorkId;
		retArray[1] = nodeId;
		retArray[2] = rowId;
		retArray[3] = tableName;
		retArray[4] = bizPagePath;
		return retArray;
	}
	
	
	public static void clearFieldsSync(final String workId,final String[] tempNodeId){
	   if(null == tempNodeId || tempNodeId.length == 0){
	      return;
	   }
	   for (int i = 0; i < tempNodeId.length; i++) {
	      clearFieldSync(workId, tempNodeId[i]);
      }
	}
	
	public static void clearFieldSync(final String workId,final String tempNodeId){
		String[] basicInfoArray = getBasicData(workId, tempNodeId);
		String currentWorkId = basicInfoArray[0]; 
		String nodeId = basicInfoArray[1]; 
		String rowId = basicInfoArray[2]; 
		String tableName = basicInfoArray[3]; 
		String bizPagePath = basicInfoArray[4];
		
		if(Str.isEmpty(tableName)){
			System.out.println("FieldSynchronizer ERROR: Did not specify the table_name for page: " + bizPagePath);
			return;
		}
		
		List<String> lstConditions = new ArrayList<String>();
		String sqlNodeSync = "select t.sync_item sync_item, t.table_col table_column from hz_node_sync t where t.process_key = (select tt.process_key from HZ_BIZ_WF_CONN tt WHERE tt.process_id= ? ) and t.process_node = ? and t.page_path = ?";
		lstConditions = new ArrayList<String>();
		lstConditions.add(currentWorkId);
		lstConditions.add(nodeId);
		lstConditions.add(bizPagePath);
		List<Map> dataTypeMapList = Access.getMultiMap(sqlNodeSync, lstConditions);
		String setCondition  = "";
		String tempSyncItem = null;
		String tempTableColumn = null;
		lstConditions = new ArrayList<String>();
		for (Iterator iterator = dataTypeMapList.iterator(); iterator.hasNext();) {
			Map tempMap = (Map) iterator.next();
			tempSyncItem = (String)tempMap.get("sync_item");
			tempTableColumn = (String)tempMap.get("table_column");
			if("handler".equals(tempSyncItem)){
				setCondition += tempTableColumn + "=null,";
			}else if("comment".equals(tempSyncItem)){
				setCondition += tempTableColumn + "=null,";
			}else if("handle_time".equals(tempSyncItem)){
				setCondition += tempTableColumn + "=null,";
			}
		}
		
		if(setCondition.length() > 0){
			setCondition = setCondition.substring(0, setCondition.length()-1);
		}else {
			System.out.println("FieldSynchronizer Clearence ERROR: Did not specify the table column for page: " + bizPagePath + " ; for node : " + nodeId);
			return;
		}
		String sqlUpdate = "update " + tableName + " set " + setCondition + " where rowid = ?";
		lstConditions.add(rowId);
		Access.executeUpdate(sqlUpdate, lstConditions);
		
		System.out.println("FieldSynchronizer:" + sqlUpdate);
		System.out.println("FieldSynchronizer:" + lstConditions);
	}
	
	public boolean doAction(LinkedHashMap arg0, LinkedHashMap arg1) {
		String nodeId = (String)arg0.get("NODEID");
		String tempcurrentNodeHandler = (String) arg0.get("USERID");
		String currentNodeHandler = OrgSelectImpl.getCleanUserId(tempcurrentNodeHandler);
      currentNodeHandler = OrgSelectImpl.getCleanUserId(currentNodeHandler);
		System.out.println("handler :" + tempcurrentNodeHandler + "==>" + currentNodeHandler);
		
		String currentNodeComment = (String) arg0.get("COMMENTS");
		String currentWorkId = (String) arg0.get("WORKID");
		
		
		String[] basicInfoArray = getBasicData(currentWorkId, nodeId);
		String rowId = basicInfoArray[2]; 
		String tableName = basicInfoArray[3]; 
		String bizPagePath = basicInfoArray[4];
		

		if(Str.isEmpty(tableName)){
			System.out.println("FieldSynchronizer ERROR: Did not specify the table_name for page: " + bizPagePath);
			return true;
		}
		
		List<String> lstConditions = new ArrayList<String>();
		String sqlNodeSync = "select t.sync_item sync_item, t.table_col table_column from hz_node_sync t where t.process_key = (select tt.process_key from HZ_BIZ_WF_CONN tt WHERE tt.process_id= ? ) and t.process_node = ? and t.page_path = ?";
		lstConditions = new ArrayList<String>();
		lstConditions.add(currentWorkId);
		lstConditions.add(nodeId);
		lstConditions.add(bizPagePath);
		List<Map> dataTypeMapList = Access.getMultiMap(sqlNodeSync, lstConditions);
		String setCondition  = "";
		String tempSyncItem = null;
		String tempTableColumn = null;
		lstConditions = new ArrayList<String>();
		for (Iterator iterator = dataTypeMapList.iterator(); iterator.hasNext();) {
			Map tempMap = (Map) iterator.next();
			tempSyncItem = (String)tempMap.get("sync_item");
			tempTableColumn = (String)tempMap.get("table_column");
			if("handler".equals(tempSyncItem)){
				setCondition += tempTableColumn + "=?,";
				lstConditions.add(currentNodeHandler);
			}else if("comment".equals(tempSyncItem)){
				setCondition += tempTableColumn + "=?,";
				lstConditions.add(currentNodeComment);
			}else if("handle_time".equals(tempSyncItem)){
				setCondition += tempTableColumn + "=sysdate,";
			}
		}
		
		if(setCondition.length() > 0){
			setCondition = setCondition.substring(0, setCondition.length()-1);
		}else {
			System.out.println("FieldSynchronizer ERROR: Did not specify the table column for page: " + bizPagePath + " ; for node : " + nodeId);
			return true;
		}
		String sqlUpdate = "update " + tableName + " set " + setCondition + " where rowid = ?";
		lstConditions.add(rowId);
		Access.executeUpdate(sqlUpdate, lstConditions);
		
		System.out.println("FieldSynchronizer:" + sqlUpdate);
		System.out.println("FieldSynchronizer:" + lstConditions);
		return true;
	}
}