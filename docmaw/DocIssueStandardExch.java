package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocIssueStandardExch extends DocIssueStandard {

   public DocIssueStandardExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_STANDARD;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUESTANDARDTITLEEXCH: Exchange Standard";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
