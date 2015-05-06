package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueSendMemo extends DocIssueSendAncestor {

   public DocIssueSendMemo(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.PROJ_SEND_MEMO;
   }

   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      String fieldGroup1 = "REV_TITLE,DOC_CODE,TRANSFER_NO,STATE,ATTACH_TYPE,ATTACH_TYPE_NAME,PURPOSE_NO,PURPOSE_NAME,HEAD_VIEW_FILE,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,SEND_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,SEND_UNIT_NO,SEND_UNIT_NAME,SEND_DEPT,SEND_DEPT_DESC,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,MAIN_SEND,CO_SEND,INNER_SEND,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("HEAD_VIEW_FILE", 5);
//      headlay.setDataSpan("SEND_UNIT_NO", 5);
      headlay.setDataSpan("INNER_SEND", 5);

   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
      mgr.getASPField("STATE").unsetHidden();
      mgr.getASPField("HEAD_VIEW_FILE").unsetHidden();
      mgr.getASPField("ATTACH_TYPE").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("SEND_DATE").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("ATTACH_PAGES").unsetHidden();
      mgr.getASPField("RECEIPT").unsetHidden();
      mgr.getASPField("RECEIPT_REQUEST").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("SEND_DEPT").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("MAIN_SEND").unsetHidden();
      mgr.getASPField("CO_SEND").unsetHidden();
      mgr.getASPField("INNER_SEND").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();

      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isCustomLayout()){
         //unsetHidden function field , differ with doc class
         mgr.getASPField("ATTACH_TYPE_NAME").unsetHidden();
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SEND_DEPT_DESC").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return "DOCISSUESENDMEMOTITLE: Project Send - Memo";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
