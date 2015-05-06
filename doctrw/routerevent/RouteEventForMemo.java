package ifs.doctrw.routerevent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.horizon.db.Access;
import com.horizon.workflow.flowengine.impl.XMLEventInterface;

public class RouteEventForMemo implements XMLEventInterface{
   public boolean doAction(LinkedHashMap arg0, LinkedHashMap arg1) {
      String currentNodeHandler = (String) arg0.get("USERID");
      String currentNodeComment = (String) arg0.get("COMMENTS");
      String currentWorkId = (String) arg0.get("WORKID");
      
      String sendTransId = null;
      
      String sql = "select substr(t.biz_objid,15) send_trans_id  from hz_biz_wf_conn t where t.process_id= ?";
      List<String> lstConditions = new ArrayList<String>();
      lstConditions.add(currentWorkId);
      Map map = Access.getSingleMap(sql, lstConditions);
      
      sendTransId = (String)map.get("send_trans_id");
      
      String nodeId = (String)arg0.get("NODEID");
      
      String updateSql = "" ;
      List<String> lstConditions2 = new ArrayList<String>();
      if("Node2".equals(nodeId)){//±à
          updateSql = "update doc_send_trans_tab t set t.create_date=sysdate,t.create_person=? ,t.create_opinion=? where t.send_trans_id=?";
      }else if("Node3".equals(nodeId) || "Node6".equals(nodeId)){//Éó
         updateSql = "update doc_send_trans_tab t set t.auth_date=sysdate,t.auth_person=? ,t.auth_opinion=? where t.send_trans_id=?";
      }else if("Node4".equals(nodeId) || "Node7".equals(nodeId)){//Åú
         updateSql = "update doc_send_trans_tab t set t.approve_date=sysdate,t.approve_person=? ,t.approve_opinion=? where t.send_trans_id=?";
      } else{
    	  return true;
      }
      lstConditions2.add(currentNodeHandler);
      lstConditions2.add(currentNodeComment);
      lstConditions2.add(sendTransId);
      Access.executeUpdate(updateSql, lstConditions2);
      
      
      System.out.println("RouteEventForMemo:" + sql);
      System.out.println("RouteEventForMemo:" + lstConditions);
      System.out.println("RouteEventForMemo:" + updateSql);
      System.out.println("RouteEventForMemo:" + lstConditions2);
      
      return true;
   }
}