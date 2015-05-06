package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;
/**
 * 
 * @author lu
 *
 */
public class DocTitleSendMeetingExch extends DocTitleSendMeeting {

   public DocTitleSendMeetingExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_SEND;
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.EXCH_SEND_MEETING;
   }

   @Override
   protected String getTitle() {
       return "DOCTITLESENDMEETINGTITLEEXCH: Overview - Exchange Send - Meeting";
   }
   
   public void adjust() throws FndException {
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("MAIN_SEND").setHidden();
      mgr.getASPField("CO_SEND").setHidden();
      mgr.getASPField("INNER_SEND").setHidden();
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
