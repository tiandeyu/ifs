package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueDesignExch extends DocIssueDesign {

   public DocIssueDesignExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   public void adjust() throws FndException
   {
      super.adjust();
      headbar.removeCustomCommand("createDocDistribution");
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_DESIGN;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUEDESIGNTITLEEXCH: Exchange Design";
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
