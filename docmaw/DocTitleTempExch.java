package ifs.docmaw;

import ifs.fnd.asp.ASPManager;

public class DocTitleTempExch extends DocTitleTemp {

   public DocTitleTempExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_TEMP;
   }

   @Override
   protected String getTitle() {
       return "DOCTITLETEMPTITLEEXCH: Overview - Exchange Temp";
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
