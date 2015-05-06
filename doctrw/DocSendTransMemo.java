package ifs.doctrw;

import ifs.docmaw.DocmawConstants;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocSendTransMemo extends DocSendTransAncestor {

   public DocSendTransMemo(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      
      String fieldGroup1 = "REV_TITLE,DOC_CODE,TRANSFER_NO,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,SEND_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,SEND_UNIT_NO,SEND_UNIT_NAME,DRAFT_DEPT_NO,DRAFT_DEPT_NAME,";
      String fieldGroup3 = "CREATE_PERSON,CREATE_PERSON_NAME,CREATE_DATE,CREATE_OPINION," +
      "AUTH_PERSON,AUTH_PERSON_NAME,AUTH_DATE,AUTH_OPINION,APPROVE_PERSON,APPROVE_PERSON_NAME,APPROVE_DATE,APPROVE_OPINION,";
      String fieldGroup4 = "MAIN_SEND_UNIT_NO,MAIN_SEND_UNIT_NAME,CO_SEND_UNIT_NO,INNER_SEND,INNER_SEND_NAME,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2 + fieldGroup3 + fieldGroup4);
      
      
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("INNER_SEND", 5);
      headlay.setDataSpan("CREATE_OPINION", 5);
      headlay.setDataSpan("AUTH_OPINION", 5);
      headlay.setDataSpan("APPROVE_OPINION", 5);
      
//      headlay.defineGroup("Main","OBJID,OBJVERSION,OBJSTATE,OBJEVENTS,SEND_TRANS_ID,ZONE_NO,GOLD_GRID_RECORD_ID,GOLD_GRID_TEMP_ID,GOLD_GRID_SIGN,STATE,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV," +
//            "REV_TITLE,DOC_CODE,TRANSFER_NO",false,true);
//      headlay.defineGroup(mgr.translate("DOCSENDTRANSSENDTRANSIDGROUPFILEATTRIBUTES: Document Title Attributes"),
//            "PROJ_NO,PROJ_NAME,SEND_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,SEND_UNIT_NO,SEND_UNIT_NAME,DRAFT_DEPT_NO,DRAFT_DEPT_NAME," +
//            "CREATE_PERSON,CREATE_PERSON_NAME,CREATE_DATE,AUTH_PERSON,AUTH_PERSON_NAME,AUTH_DATE,APPROVE_PERSON,APPROVE_PERSON_NAME,APPROVE_DATE," +
//            "MAIN_SEND_UNIT_NO,MAIN_SEND_UNIT_NAME,CO_SEND_UNIT_NO,INNER_SEND,INNER_SEND_NAME"
//            +"PRESENTER,MEETING_DATE,MEETING_LOC,HOST_UNIT,ATTEND_UNIT,SUB_CLASS,ATTACH_TYPE,ATTACH_TYPE_NAME,MEETING_TYPE,MEETING_TYPE_NAME",true,false);
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
//      mgr.getASPField("PURPOSE_NO").unsetHidden();
//      mgr.getASPField("VIEW_FILE").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("SEND_DATE").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("ATTACH_PAGES").unsetHidden();
      mgr.getASPField("RECEIPT").unsetHidden();
      mgr.getASPField("RECEIPT_REQUEST").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("DRAFT_DEPT_NO").unsetHidden();
      mgr.getASPField("MAIN_SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("CO_SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("INNER_SEND").unsetHidden();
      
      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout()){
//         mgr.getASPField("PURPOSE_NAME").unsetHidden();
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
//       mgr.getASPField("MAIN_SEND_UNIT_NAME").unsetHidden();
//       mgr.getASPField("CO_SEND_UNIT_NAME").unsetHidden();
//       mgr.getASPField("INNER_SEND_NAME").unsetHidden();
      }
      
      if (!(headlay.isNewLayout() || headlay.isEditLayout()))
      {
         mgr.getASPField("CREATE_PERSON").unsetHidden();
         mgr.getASPField("CREATE_DATE").unsetHidden();
         mgr.getASPField("CREATE_OPINION").unsetHidden();
         mgr.getASPField("AUTH_PERSON").unsetHidden();
         mgr.getASPField("AUTH_DATE").unsetHidden();
         mgr.getASPField("AUTH_OPINION").unsetHidden();
         mgr.getASPField("APPROVE_PERSON").unsetHidden();
         mgr.getASPField("APPROVE_DATE").unsetHidden();
         mgr.getASPField("APPROVE_OPINION").unsetHidden();
         mgr.getASPField("CREATE_PERSON_NAME").unsetHidden();
         mgr.getASPField("AUTH_PERSON_NAME").unsetHidden(); 
         mgr.getASPField("APPROVE_PERSON_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return  "DOCSENDTRANSTITLEMEMO: Doc Send Trans Memo";
   }

   @Override
   protected String getCurrentPageSubDocClass() {
      return DocmawConstants.PROJ_SEND_MEMO;
   }
}
