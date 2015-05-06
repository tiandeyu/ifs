package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocTitleStandardExchTab extends DocTitleStandardExch {

   public DocTitleStandardExchTab(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   protected void customizeHeadlay()
   {
      super.customizeHeadlay();
      disableHeader();
      headblk.disableFuncFieldsNonSelect();
   }
   
   protected void customizeHeadView(){
      headblk.setView("DOC_TITLE_STRUCTURED");
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("CHECK_IN_FILE").unsetHidden();
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
