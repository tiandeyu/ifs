package ifs.genbaw;
 
import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.URL;

public class GenbawIntermediatePage extends ASPPageProvider
{
   private ASPBlock blk;
   private String prefix;
   
	public GenbawIntermediatePage(ASPManager mgr, String pagePath)
	{
		super(mgr, pagePath);
	}
	
	public void run() throws FndException 
	{
		ASPManager mgr = getASPManager();
		ASPContext ctx = mgr.getASPContext();
		String cmd = ctx.readValue("GENBAW_COMMAND", mgr.readValue("GENBAW_COMMAND"));
		prefix = ctx.readValue("GENBAW_PREFIX", mgr.readValue("GENBAW_PREFIX"));
		String notification_no = ctx.readValue("NOTIFICATION_NO", mgr.readValue("NOTIFICATION_NO"));
		
		if ("EXC_NOTIFICATION".equals(cmd) && !Str.isEmpty(notification_no))
		{
		   createExcNotification(notification_no);
		}
		else
		{
		   String page_url = mgr.getQueryStringValue("CONNECTED_PAGE_URL");
		   String key_ref  = mgr.getQueryStringValue("CONNECTED_KEY_REF");
		   if (!mgr.isEmpty(page_url) && !Str.isEmpty(key_ref))
		   {
		      key_ref = key_ref.substring(0, key_ref.length() - 1);
		      key_ref = Str.replace(key_ref, "^", "&");
		      
		      if (page_url.indexOf('?') < 0) // Modified end
		         page_url = page_url + "?";
		      else
		         page_url = page_url + "&";
		      
		      mgr.redirectTo(page_url + key_ref);  
		   }
		}
		
		if (!Str.isEmpty(cmd))
		   ctx.writeValue("GENBAW_COMMAND", cmd);
		if (!Str.isEmpty(prefix))
		   ctx.writeValue("GENBAW_PREFIX", prefix);
		if (!Str.isEmpty(notification_no))
		   ctx.writeValue("NOTIFICATION_NO", notification_no);
	}
	
	private void createExcNotification(String notification_no) throws FndException
	{
	   ASPManager mgr = getASPManager();
	   ASPContext ctx = mgr.getASPContext();
	   
	   ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	   ASPCommand cmd = trans.addCustomCommand("CREEXC", "GENERAL_NOTIFICATION_EXC_API.Create_Exc_Notification_CP");
	   cmd.addParameter("IN_1", notification_no);
	   mgr.perform(trans);
	   appendDirtyJavaScript("opener." + mgr.encodeStringForJavascript(prefix) + "refreshPortlet();\n");
	   appendDirtyJavaScript("window.close();\n");
	}
	
	public void preDefine() throws FndException
   {
	   ASPManager mgr = getASPManager();
	   blk = mgr.newASPBlock("DUMMY");
	   blk.addField("IN_1");
	   blk.addField("IN_2");
	   blk.addField("OUT_1");
	   blk.addField("OUT_2");
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
		return out;
	}
}
