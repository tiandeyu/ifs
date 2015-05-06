package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocIssueProphaseExch extends DocIssueProphase {

   public DocIssueProphaseExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_PROPHASE;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUEPROPHASETITLEEXCH: Exchange Prophase";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
