package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

public class DocStructureTreeFrame extends ASPPageProvider {

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocStructureTreeFrame");
   public DocStructureTreeFrame(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }

   public void run() throws FndException {
      ASPManager mgr = getASPManager();
   }

   protected String getDescription() {
      return "DOCISSUETOBESIGNEDNAV: Contractor Receive Navigator";
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
      out.append("    <frame src=\"DocStructureTreeNav.page\" name=\"leftFrame\" scrolling=\"Yes\" id=\"leftFrame\" title=\"leftFrame\" />\n");
      out.append("    <frame src=\"DocStructureTreeBlank.page\" name=\"mainFrame\" id=\"mainFrame\" title=\"mainFrame\" />\n");
      out.append("  </frameset>\n");
      out.append("<noframes>\n");

      out.append("</html>\n");
      return out;
   }
}
