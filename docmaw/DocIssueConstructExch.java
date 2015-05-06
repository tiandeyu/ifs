package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueConstructExch extends DocIssueConstruct {

   public DocIssueConstructExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_CONSTRUCT;
   }
   
   public void adjust() throws FndException
   {
      super.adjust();
      headbar.removeCustomCommand("createDocDistribution");
   }

   @Override
   protected String getTitle() {
       return "DOCISSUECONSTRUCTTITLEEXCH: Exchange Construct";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
