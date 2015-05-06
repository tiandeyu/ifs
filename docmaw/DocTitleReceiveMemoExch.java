package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
/**
 * 
 * @author lu
 *
 */
public class DocTitleReceiveMemoExch extends DocTitleReceiveMemo {

   public DocTitleReceiveMemoExch(ASPManager mgr, String page_path) {
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
       return "DOCTITLERECEIVEMEMOTITLEEXCH: Overview - Exchange Receive - Memo";
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
