package ifs.corpvw;

import java.util.ArrayList;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

public class IFSCorpvView extends ASPPageProvider {

   // ===============================================================
   // Static constants
   // ===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.corpvw.IFSCorpvView");

   // ===============================================================
   // Instances created on page creation (immutable attributes)
   // ===============================================================

   // ===============================================================
   // Transient temporary variables (never cloned)
   // ===============================================================
   private String userID;
   private String userType;
   private String treeType;

   // ===============================================================
   // Construction
   // ===============================================================
   public IFSCorpvView(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   public void run() {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();

   }

   // =============================================================================
   // Command Bar functions
   // =============================================================================

   public void preDefine() {

   }

   protected AutoString getContents() throws FndException {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("PCVSYSTEMHEADTAG: IFS Project Management"));

      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<frameset id=\"frameSet1\" framespacing=\"0\"  frameborder=\"1\" rows=\"60,*\" cols=\"*\">\n");
      out.append("<frame src=\"IFSCorpvTop.page\" name=\"IFS_TOP_FRAME\" scrolling=\"NO\"  marginwidth=\"0\" marginheight=\"0\">\n");

      out.append("<frameset id=\"frameSet2\" cols=\"200,*\" frameborder=1 framespacing=1>\n");
      out.append("<frame src=\"../Navigator.page?__ID=317904&MAINMENU=Y&NEW=Y\" name=\"IFS_NAV_FRAME\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");

      out.append("<frame src=\"/b2e/secured/hzwflw/HzTodoWorkbench.page\" name=\"IFS_MAIN_FRAME\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
      out.append("</frameset>\n");

      out.append("</frameset>\n");
      out.append("<noframes></noframes><body></body>\n");
      out.append("</html>\n");
      return out;
   }

   public void adjust() {

   }

   // ===============================================================
   // HTML
   // ===============================================================
   protected String getDescription() {
      return "CORPVIEWEDESCRIPTION: Corporation View";
   }

   protected String getTitle() {
      return getDescription();
   }

}