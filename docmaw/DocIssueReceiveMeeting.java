package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueReceiveMeeting extends DocIssueReceiveAncestor {

   public DocIssueReceiveMeeting(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.PROJ_RECEIVE_MEETING;
   }

   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      mgr.getASPField("INNER_DOC_CODE").setLabel("DOCISSUERECEIVEANCESTORINNERDOCCODE: Receive Trans Id").setHyperlink("../doctrw/DocReceiveTrans.page", "INNER_DOC_CODE RECEIVE_TRANS_ID");
      mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUERECEIVEANCESTORINNERMEETINGCODE: Inner Meeting Code");
      String fieldGroup1 = "REV_TITLE,DOC_CODE,TRANSFER_NO,STATE,MEETING_TYPE,MEETING_TYPE_NAME,HEAD_VIEW_FILE,INNER_DOC_CODE,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,SEND_DATE,RECEIVE_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,SEND_UNIT_NO,SEND_UNIT_NAME,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,RECEIVE_UNIT_NO,RECEIVE_UNIT_NAME,CO_SEND,INNER_SEND,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("SEND_UNIT_NO", 5);
      headlay.setDataSpan("INNER_SEND", 5);

   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
      mgr.getASPField("STATE").unsetHidden();
      mgr.getASPField("MEETING_TYPE").unsetHidden();
      mgr.getASPField("HEAD_VIEW_FILE").unsetHidden();
      mgr.getASPField("INNER_DOC_CODE").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("RECEIVE_DATE").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("ATTACH_PAGES").unsetHidden();
      mgr.getASPField("RECEIPT").unsetHidden();
      mgr.getASPField("RECEIPT_REQUEST").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("RECEIVE_UNIT_NO").unsetHidden();
      mgr.getASPField("CO_SEND").unsetHidden();
      mgr.getASPField("INNER_SEND").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();

      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isCustomLayout()){
         //unsetHidden function field , differ with doc class
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("MEETING_TYPE_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
         mgr.getASPField("RECEIVE_UNIT_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return "DOCISSUERECEIVEMEETINGTITLE: Project Receive - Meeting";
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
