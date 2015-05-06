package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocIssueTempExch extends DocIssueTemp {

   public DocIssueTempExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_DESIGN;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUETEMPTITLEEXCH: Exchange Temp";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
