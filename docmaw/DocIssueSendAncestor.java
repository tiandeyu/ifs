package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public abstract class DocIssueSendAncestor extends DocIssueAncestor {

   public DocIssueSendAncestor(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   public void adjust() throws FndException
   {
      super.adjust();
      headbar.removeCustomCommand("createDocDistribution");
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_SEND;
   }
   
}
