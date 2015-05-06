package ifs.matmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;


public class MatSupDocManNav extends ASPPageProvider
{
	public static boolean DEBUG = Util.isDebugEnabled("ifs.genbaw.MatSupDocManNav");

	public MatSupDocManNav(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}

	public void run() throws FndException {
		ASPManager mgr = getASPManager();
	}

	protected String getDescription() {
		return "MATSUPDOCMANNAV: MatSupDocMan Navigator";
	}

	protected String getTitle() {
		return getDescription();
	}

	protected AutoString getContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		AutoString out = getOutputStream();

		out.append("<html>\n");
		out.append("<head>\n");
		out.append(mgr.generateHeadTag(getTitle()));

		out.append("</head>\n");

		out.append("  <frameset rows=\"*\" cols=\"220,*\" framespacing=\"2\" frameborder=\"yes\" border=\"2\">\n");
		out.append("    <frame src=\"MatSupDocManTree.page\" name=\"leftFrame\" scrolling=\"Yes\" id=\"leftFrame\" title=\"leftFrame\" />\n");
		out.append("    <frame src=\"MatSupDocMan.page\" name=\"mainFrame\" id=\"mainFrame\" title=\"mainFrame\" />\n");
		out.append("  </frameset>\n");
		out.append("<noframes>\n");

		out.append("</html>\n");
		return out;
	}

}
