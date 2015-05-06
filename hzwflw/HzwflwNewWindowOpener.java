package ifs.hzwflw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.hzwflw.util.HzConstants;
import ifs.hzwflw.util.HzWfUtil;
import ifs.hzwflw.util.URL;

import java.util.ArrayList;
import java.util.List;

import com.horizon.db.Access;
import com.horizon.util.encrypt.DESEDE;

public class HzwflwNewWindowOpener extends ASPPageProvider implements
		HzConstants {
	// ===============================================================
	// Static constants
	// ===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.enterwNewWindowOpener");

	// ===============================================================
	// Transient temporary variables (never cloned)
	// ===============================================================
	private int width;
	private int height;
	private int left;
	private int top;

	private URL targetUrl = null;

	// ===============================================================
	// Construction
	// ===============================================================
	public HzwflwNewWindowOpener(ASPManager mgr, String pagePath) {
		super(mgr, pagePath);
	}

	public void run() {
		ASPManager mgr = getASPManager();
		String huizhengAdminBaseUrl = mgr.getASPConfig().getParameter( "APPLICATION/HUIZHENG_ADMIN");
		String fromFlag = mgr.readValue(FROM_FLAG);
		width = 1366;
		height = 768;
		left = 0;
		top = 0;
		if (FROM_NAVIGATOR.equals(fromFlag)) {
			String target = mgr.getQueryStringValue("target");
			if ("monitor".equals(target)) {
				String tempUserId = HzWfUtil.getOracleUserId(getASPManager())
						.toUpperCase();
				ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
				ASPQuery q = trans.addQuery("USERLOGINNAME",
						"select t.login_name LOGINNAME from to_horizon_user  t where t.id= '"
								+ tempUserId + "'");

				List conditionList = new ArrayList();
				if ("IFSAPP".equals(tempUserId)) {
					conditionList.add("*");
				} else {
					conditionList.add(tempUserId);
				}

				List userList = Access
						.getSingleList("select t.login_name LOGINNAME from to_horizon_user  t where t.id=?",
								conditionList);
				if (null == userList || userList.size() == 0) {
					targetUrl = null;
					return;
				}
				targetUrl = new URL(huizhengAdminBaseUrl + "/horizon/formview/view/workview.jsp?menuidLevel1=HZ28e7f91eb8f987011eb917a12b0017&menuCategory=2&os=D667935EA06C1A49");
			} else if ("delegateAdmin".equals(target)) {
				targetUrl = new URL( "http://lqw-pc:8080/hzsp2/horizon/formview/view/viewtemplate/view.template.normal.jsp?viewid=HZkCinRSDoo7Qhh8pkQ9mdRVTn2WFKsV&loginName=SUNYING");
			} else if ("designer".equals(target)) {
				String urlPrefix = mgr.getASPConfig().getProtocol() + "://"
						+ mgr.getASPConfig().getApplicationDomain() + ""
						+ mgr.getASPConfig().getApplicationContext();
				targetUrl = new URL(urlPrefix + "/horizon/designer/HorizonDesigner.jsp");
			}
		}

	}

	// ===============================================================
	// HTML
	// ===============================================================
	protected String getDescription() {
		return "Dummy Window Opener";
	}

	protected String getTitle() {
		return "Dummy Window Opener";
	}

	protected void printContents() throws FndException {

		ASPManager mgr = getASPManager();
		appendToHTML("<html>\n");
		appendToHTML("<head>\n");
		appendToHTML("<title>Dummy Window Opener</title>\n");
		appendToHTML("</head>\n");
		appendToHTML("<body>\n");
		appendToHTML("<form>\n");
		appendToHTML("</form>\n");
		appendToHTML("</body>\n");
		appendToHTML("</html>\n");

		if (targetUrl == null) {
			appendDirtyJavaScript("   if (history.length == 0)\n");
			appendDirtyJavaScript("      window.close();\n");
			appendDirtyJavaScript("   else \n");
			appendDirtyJavaScript("      history.back();\n");
			appendDirtyJavaScript("  alert('"
					+ mgr
							.translate("YOUDONOTHAVEENOUGHRIGHTTOVIEWPROCESSMONITOR: You do not have enough right to monitor processes.")
					+ "');");
			return;
		}

		// -----------------------------------------------------------------------------
		// ---------------------------- CLIENT FUNCTIONS
		// -----------------------------
		// -----------------------------------------------------------------------------

		appendDirtyJavaScript(" var h=screen.availHeight-35;\n");
		appendDirtyJavaScript(" var w=screen.availWidth-5;\n");
		appendDirtyJavaScript(" var vars=\"top=0,left=0,height=\"+h+\",width=\"+w+\",status=no,toolbar=no,menubar=no,location=no,resizable=1,scrollbars=1\";\n");
		appendDirtyJavaScript(" window.open(\""
				+ mgr.encodeStringForJavascript(targetUrl.toString())
				+ "\",\"\",vars);\n");

		appendDirtyJavaScript("   if (history.length == 0)\n");
		appendDirtyJavaScript("      window.close();\n");
		appendDirtyJavaScript("   else \n");
		appendDirtyJavaScript("      history.back();\n");
	}

	public static void main(String[] args) {
		String tempUser = DESEDE.encryptIt("admin");
		System.out.println(tempUser);
		System.out.println(DESEDE.decryptIt(tempUser));
	}

}
