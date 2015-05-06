package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
/**
 * 
 * @author lu
 *
 */
public class DocTitleReceiveMeetingExch extends DocTitleReceiveMeeting {

   public DocTitleReceiveMeetingExch(ASPManager mgr, String page_path) {
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
       return "DOCTITLERECEIVEMEETINGTITLEEXCH: Overview - Exchange Receive - Meeting";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
