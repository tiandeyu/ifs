package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueReceiveMeetingExch extends DocIssueReceiveMeeting {

   public DocIssueReceiveMeetingExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_RECEIVE;
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.EXCH_RECEIVE_MEETING;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUERECEIVEMEETINGTITLEEXCH: Exchange Receive - Meeting";
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
