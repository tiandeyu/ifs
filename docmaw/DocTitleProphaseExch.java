package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocTitleProphaseExch extends DocTitleProphase {

   public DocTitleProphaseExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_PROPHASE;
   }
   
   @Override
   protected String getTitle() {
       return "DOCTITLEPROPHASETITLEEXCH: Overview - Exchange Prophase";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
