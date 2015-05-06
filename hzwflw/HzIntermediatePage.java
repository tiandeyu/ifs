package ifs.hzwflw;
 
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.URL;

import java.util.ArrayList;
import java.util.List;

import com.horizon.db.Access;
import com.horizon.todo.TodoMsg;
import com.horizon.util.SessionUtil;

public class HzIntermediatePage  extends ASPPageProvider implements HzConstants{
   
   
   boolean emptyPage = false;
   
   String title = null;
   
	public HzIntermediatePage(ASPManager mgr, String pagePath) {
		super(mgr, pagePath);
	}
	
	
	ASPManager mgr;
	
	
	
	private URL targetUrl = null;
	
	public void run() 
	{
	   String toReadUrl = null;
	   
		mgr = getASPManager();
		String fromFlag = mgr.readValue(FROM_FLAG);
		String taskId = null;
		String realUserId = null;
		String userTaskId = null;

		String tempWorkId = null;
		String tempTrackId = null;
      if (FROM_TODO.equals(fromFlag)) {
         tempWorkId = mgr.getQueryStringValue("ToDo_Workid");
         tempTrackId = mgr.getQueryStringValue("TODO_TRACKID");
         userTaskId = mgr.getQueryStringValue("USERTASKID");
         realUserId = mgr.getQueryStringValue("TODO_REALUSERID");
         
         
         
         String temp = mgr.getAspRequest().getQueryString();//.getQueryString();//.getParameter("TODO_REALUSERID");
      } else if (FROM_DONE.equals(fromFlag)) {
         tempWorkId = mgr.getQueryStringValue("Done_Workid");
         tempTrackId = mgr.getQueryStringValue("DONE_TRACKID");
      } else if (FROM_SEND_VIEW.equals(fromFlag)) {
         tempWorkId = mgr.getQueryStringValue("ToRead_Workid");
         toReadUrl =  mgr.getQueryStringValue("TOREAD_URL");
         tempTrackId = mgr.getQueryStringValue("TOREAD_TRACKID");
      } else if (FROM_SEND_VIEW_FIN.equals(fromFlag)) {
         tempWorkId = mgr.getQueryStringValue("Read_Workid");
         tempTrackId = mgr.getQueryStringValue("READ_TRACKID");
      }
      
      
      if(FROM_SEND_VIEW.equals(fromFlag) && mgr.isEmpty(toReadUrl)){
         // TODO LQW NOTICE ONLY ,WITHOUT ANY BIZ PAGE.
         taskId = mgr.getQueryStringValue("TOREAD_TASKID");
         realUserId = mgr.getQueryStringValue("TOREAD_REALUSERID");
         title = mgr.getQueryStringValue("TOREAD_TITLE");
         TodoMsg.setTodoReaded(taskId,realUserId,"system");
         emptyPage = true;
      }else if(FROM_SEND_VIEW_FIN.equals(fromFlag) && mgr.isEmpty(tempWorkId)){
         // TODO LQW NOTICE ONLY ,WITHOUT ANY BIZ PAGE.
//         taskId = mgr.getQueryStringValue("TOREAD_TASKID");
//         realUserId = mgr.getQueryStringValue("TOREAD_REALUSERID");
         title = mgr.getQueryStringValue("READ_TITLE");
//         TodoMsg.setTodoReaded(taskId,realUserId,"system");
         emptyPage = true;
      }else if (FROM_PROCESS_DESIGN_PAGE.equals(fromFlag)){
         ///b2e
         String urlPrefix = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext();
         targetUrl = new URL( urlPrefix + "/horizon/designer/HorizonDesigner.jsp");
      }else if ("TEST_SYSTEM".equals(fromFlag)){
         targetUrl = new URL("http://10.127.127.1/test/index.asp");
      }else{
         String urlPrefix = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext();
         targetUrl = new URL( urlPrefix + "/horizon/workflow/xmlwork.index.jsp");
         targetUrl.addParameters(FROM_FLAG, fromFlag);
         targetUrl.addParameters("workid", tempWorkId);
         
         if (FROM_DONE.equals(fromFlag) || FROM_SEND_VIEW_FIN.equals(fromFlag))  {
            List<String> conditionList = new ArrayList<String>();
            conditionList.add(tempWorkId);
            List<String> result = Access.getSingleList("select trackid from tw_horizon_author where workid= ? ", conditionList, "system");
            if(!(null == result || result.size() == 0)){
               tempTrackId  = result.get(0);
            }
         } 
         
         if(!Str.isEmpty(tempTrackId)){
            targetUrl.addParameters("trackid", tempTrackId);
         }
         if(!Str.isEmpty(userTaskId)){
            targetUrl.addParameters("_WF_USERTASKID_", userTaskId);
         }
         if(!Str.isEmpty(realUserId)){
            targetUrl.addParameters("_REAL_USER_ID_", realUserId);
         }
         targetUrl.addParameters("dbIdentifier", "system");
      }
      
      
      if(!emptyPage){
         mgr.redirectTo(targetUrl.toString());  
      }
	}
	public String getDescription()
	{
		return "" ;
	}

	public String getTitle()
	{
		return  getDescription();
	}
	
	protected AutoString getContents() throws FndException
	{ 
		AutoString out = getOutputStream();
		out.clear();
		if(emptyPage){
		   out.append("\n");
		   out.append("<br/>\n");
		   out.append("<br/>\n");
		   out.append("<br/>\n");
		   out.append("<br/>\n");
		   out.append("<br/>\n");
		   out.append("<table width=80% align=center> <tr align=center><td>\n");
		   out.append("<font >"+ title +"</font>");
		   out.append("</td></tr> <tr align=center><td><input type=button onclick='self.close();' value='"+mgr.translate("HZINTERMEDIATEPAGECLOSE: Close")+"'>");
		   out.append("</td></tr></table>\n");
		   out.append("<br/>\n");
		   out.append("<br/>\n");
		   out.append("<br/>\n");
		   out.append("\n");
		   out.append("\n");
		}
		return out;
	}
}
