package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
/**
 * 
 * @author lu
 *
 */
public class DocTitleReceiveFaxExch extends DocTitleReceiveFax {

   public DocTitleReceiveFaxExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_RECEIVE;
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.EXCH_RECEIVE_FAX;
   }

   @Override
   protected String getTitle() {
       return "DOCTITLERECEIVEFAXTITLEEXCH: Overview - Exchange Receive - Fax";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
