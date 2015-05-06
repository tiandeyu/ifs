package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;
/**
 * 
 * @author lu
 *
 */
public class DocTitleSendFaxExch extends DocTitleSendFax {

   public DocTitleSendFaxExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_SEND;
   }

   @Override   
   protected String getCurrentPageSubDocClass(){
      return DocmawConstants.EXCH_SEND_FAX;
   }
   
   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      
      String fieldGroup1 = "REV_TITLE,DOC_CODE,TRANSFER_NO,ROWSTATE,PURPOSE_NO,PURPOSE_NAME,VIEW_FILE,ATTACH_TYPE,ATTACH_TYPE_NAME,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,SEND_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,SEND_UNIT_NO,SEND_UNIT_NAME,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,RECEIVE_UNIT_NO,RECEIVE_UNIT_NAME,CO_SEND,INNER_SEND,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("SEND_UNIT_NO", 5);
      headlay.setDataSpan("INNER_SEND", 5);

   }
   
   
   public void adjust() throws FndException {
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("MAIN_SEND").setHidden();
      mgr.getASPField("CO_SEND").setHidden();
      mgr.getASPField("INNER_SEND").setHidden();
   }

   @Override
   protected String getTitle() {
       return "DOCTITLESENDFAXTITLEEXCH: Overview - Exchange Send - Fax";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
