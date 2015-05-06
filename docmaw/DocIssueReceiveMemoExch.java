package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueReceiveMemoExch extends DocIssueReceiveMemo {
   public DocIssueReceiveMemoExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_RECEIVE;
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.EXCH_RECEIVE_MEMO;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUERECEIVEMEMOTITLEEXCH: Exchange Receive - Memo";
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("MAIN_SEND").setHidden();
      mgr.getASPField("CO_SEND").setHidden();
      mgr.getASPField("INNER_SEND").setHidden();
      mgr.getASPField("RECEIVE_DATE").setHidden();
      mgr.getASPField("SEND_DATE").unsetHidden();
   }

   public void okFind()
   {
      okFindForExch();
   }
   
   public void runQuery()
   {
      runQueryForExch();
   }
   
   @Override
   protected void lastMethod() {
      
   }
   
}
