package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
/**
 * 
 * @author lu
 *
 */
public class DocTitleDesignExch extends DocTitleDesign {

   public DocTitleDesignExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_DESIGN;
   }
   
   @Override
   protected String getTitle() {
       return "DOCTITLEDESIGNTITLEEXCH: Overview - Exchange Design";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
