package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocTitleConstructExch extends DocTitleConstruct {

   public DocTitleConstructExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_CONSTRUCT;
   }

   @Override
   protected String getTitle() {
       return "DOCTITLECONSTRUCTTITLEEXCH: Overview - Exchange Construct";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
