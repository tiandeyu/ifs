package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocTitleStandardExch extends DocTitleStandard {
   
   public DocTitleStandardExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_STANDARD;
   }
   
   @Override
   protected String getTitle() {
       return "DOCTITLESTANDARDTITLEEXCH: Overview - Exchange Standard";
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
