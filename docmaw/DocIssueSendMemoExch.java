package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueSendMemoExch extends DocIssueSendMemo {

   public DocIssueSendMemoExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_SEND;
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.EXCH_SEND_MEMO;
   }
   
   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      mgr.getASPField("MAIN_SEND").setHidden();
      mgr.getASPField("CO_SEND").setHidden();
      mgr.getASPField("INNER_SEND").setHidden();

      String fieldGroup1 = "REV_TITLE,DOC_CODE,TRANSFER_NO,STATE,PURPOSE_NO,PURPOSE_NAME,HEAD_VIEW_FILE,ATTACH_TYPE,ATTACH_TYPE_NAME,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,SEND_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,SEND_UNIT_NO,SEND_UNIT_NAME,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,MAIN_SEND,CO_SEND,INNER_SEND,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("SEND_UNIT_NO", 5);
      headlay.setDataSpan("INNER_SEND", 5);

   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("ATTACH_TYPE").unsetHidden();
      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout()){
         //unsetHidden function field , differ with doc class
         mgr.getASPField("ATTACH_TYPE_NAME").unsetHidden();
      }   
   }

   

   @Override
   protected String getTitle() {
       return "DOCISSUESENDMEMOTITLEEXCH: Exchange Send - Memo";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
