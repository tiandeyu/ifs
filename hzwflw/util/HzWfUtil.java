package ifs.hzwflw.util;

import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPTransactionBuffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;


public class HzWfUtil implements HzConstants{
	
	/**
	 * get process name that's attached to the page. 
	 * @param mgr
	 * @return
	 */
	public static List<String> getPageProcessDefKeys2(ASPManager mgr ,String url) {
		List<String> ls_processKeys = new ArrayList<String>();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		String sql = "SELECT T.PROCESS_KEY PROCESS_KEY FROM HZ_BIZ_WF_CONFIG T WHERE T.PAGE_PATH = '" + url + "'";
		trans.addQuery("BPROCESSNAMES", sql);
		
		trans = mgr.perform(trans);
		
		ASPBuffer buffer = trans.getBuffer("BPROCESSNAMES");
		
		int count = buffer.countItems();
		
		for (int i = 0; i < count; i++)
		{
			if("DATA".equals(buffer.getNameAt(i)))
			{
				ls_processKeys.add(buffer.getBufferAt(i).getValue("PROCESS_KEY"));
			}
		}
		return ls_processKeys;
	}
	
	/**
	 * ger current oracle user id. in lower case.
	 * */
	public static String getOracleUserId(ASPManager mgr) {
		ASPContext aspCtx = mgr.getASPContext();
		
		String oracle_user_id = aspCtx.findGlobal(CURRENT_FND_USER_ID);
		if(false ==  mgr.isEmpty(oracle_user_id)){
			return oracle_user_id;
		}
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
//		String sql = "SELECT PERSON_INFO_API.Get_Id_For_User(Fnd_Session_API.Get_Fnd_User) USERID FROM DUAL";
		String sql = "SELECT Fnd_Session_API.Get_Fnd_User USERID FROM DUAL";
		trans.addQuery("USER", sql);
		trans = mgr.perform(trans);
		oracle_user_id =  trans.getValue("USER/DATA/USERID").toLowerCase();
		aspCtx.setGlobal(CURRENT_FND_USER_ID, oracle_user_id);
		com.horizon.todo.grdb.GrdbUtil d;
		return oracle_user_id;
	}
	
}
