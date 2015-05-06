package ifs.docmaw;

import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public abstract class DocIssueReceiveAncestor extends DocIssueAncestor {

   public DocIssueReceiveAncestor(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_RECEIVE;
   }

   public void adjust() throws FndException{
      super.adjust();
      headbar.removeCustomCommand("createDocDistribution");
      itembar11.disableCommand(ASPCommandBar.NEWROW);
      itembar11.disableCommand(ASPCommandBar.DELETE);
      itembar11.disableCommand(ASPCommandBar.EDITROW);
      itembar11.disableCommand(ASPCommandBar.EDIT);
      itembar11.disableCommand(ASPCommandBar.DUPLICATE);
      itembar11.disableCommand(ASPCommandBar.DUPLICATEROW);
   }
   
}
