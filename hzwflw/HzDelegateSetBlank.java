package ifs.hzwflw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

public class HzDelegateSetBlank extends ASPPageProvider {

   // -----------------------------------------------------------------------------
   // ---------- Static constants  ------------------------------------------------
   // -----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssueTobeSignedBlank");
   
   // -----------------------------------------------------------------------------
   // ------------------------ Construction ---------------------------
   // -----------------------------------------------------------------------------
   public HzDelegateSetBlank(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }
   
   public void run() 
   {
      ASPManager mgr = getASPManager();
      adjust();
   }

   public void preDefine() {
      this.disableApplicationSearch();
   }

   public void adjust() {
      // fill function body
   }
   

   // -----------------------------------------------------------------------------
   // ------------------------ Presentation functions ---------------------------
   // -----------------------------------------------------------------------------

   protected String getDescription() {
      return "DOCISSUETOBESIGNEDBLANK: To be signed Blank Page";
   }

   protected String getTitle() {
      return getDescription();
   }

   protected AutoString getContents() throws FndException {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.clear();
      //
      // PAGE HEADER
      //
      
      out.append("<html>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      
      //
      // FORM & FILE CONTENT AREA
      //
      
      out.append("\n");
      out.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
      out.append("   <tr>\n");
      out.append("   <td>\n");
      out.append("   &nbsp;");
      out.append("   </td>\n");
      out.append("   </tr>\n");
      out.append("</table>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      
      return out;
   }

}
