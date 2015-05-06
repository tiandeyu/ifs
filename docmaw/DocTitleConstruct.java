package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocTitleConstruct extends DocTitleAncestor {

   public DocTitleConstruct(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_CONSTRUCT;
   }

   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      
      mgr.getASPField("DOC_CODE").setLabel("DOCTITLECONSTRUCTDOCCODE: Doc Code");
      mgr.getASPField("TRANSFER_NO").setLabel("DOCTITLECONSTRUCTTRANSFERNO: Transfer No");
      mgr.getASPField("DOC_LOC_NO").setLabel("DOCTITLECONSTRUCTDOCLOCNO: Doc Loc No");
      
      String fieldGroup1 = "REV_TITLE,DOC_CODE,TRANSFER_NO,SUB_CLASS,SUB_CLASS_NAME,ROWSTATE,VIEW_FILE,CHECK_IN_FILE,DOC_STATE,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,MACH_GRP_NO,MACH_GRP_DESC,DOC_LOC_NO,SPECIALTY_NO,SPECIALTY_DESC,ROOM_NO,ROOM_DESC,LOT_NO,LOT_DESC,GRADE_NO,GRADE_DESC,";
      String fieldGroup3 = "PAGES,FIRST_REVISION,SEND_UNIT_NO,SEND_UNIT_NAME,SEND_DEPT,SEND_DEPT_DESC,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,SEND_DATE,RECEIVE_UNIT_NO,RECEIVE_UNIT_NAME,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2 + fieldGroup3);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("GRADE_NO", 5);
      
//      headlay.defineGroup("Main","OBJID,OBJVERSION,DOC_CLASS,SDOCNAME,DOC_NO,FIRST_SHEET_NO,TITLE,NUM_GEN_TRANSLATED,BOOKING_LIST,ID1,ID2,LANGUAGE_CODE,FORMAT_SIZE," +
//            "REV_TITLE,DOC_CODE,TRANSFER_NO,SUB_CLASS,SUB_CLASS_NAME,ROWSTATE,VIEW_FILE,CHECK_IN_FILE,DOC_STATE",false,true);
//      headlay.defineGroup(mgr.translate("DOCMAWDOCTITLDOCUMENTSATTRIBUTES: Documents Attributes"),
//            "PROJ_NO,PROJ_NAME,MACH_GRP_NO,MACH_GRP_DESC,DOC_LOC_NO,SPECIALTY_NO,SPECIALTY_DESC,ROOM_NO,ROOM_DESC,LOT_NO,LOT_DESC,GRADE_NO,GRADE_DESC,PAGES,FIRST_REVISION,SEND_UNIT_NO,SEND_UNIT_NAME,SEND_DEPT,SEND_DEPT_DESC,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,SEND_DATE,RECEIVE_UNIT_NO,RECEIVE_UNIT_NAME,USER_CREATED,USER_CREATED_NAME,DT_CRE",true,false);
//
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
      mgr.getASPField("SUB_CLASS").unsetHidden();
      mgr.getASPField("ROWSTATE").unsetHidden();
      mgr.getASPField("VIEW_FILE").unsetHidden();
      mgr.getASPField("DOC_STATE").unsetHidden();
      
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("MACH_GRP_NO").unsetHidden();
      mgr.getASPField("DOC_LOC_NO").unsetHidden();
      mgr.getASPField("SPECIALTY_NO").unsetHidden();
      mgr.getASPField("ROOM_NO").unsetHidden();
      mgr.getASPField("LOT_NO").unsetHidden();
      mgr.getASPField("GRADE_NO").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("FIRST_REVISION").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("SEND_DEPT").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("SEND_DATE").unsetHidden();
      mgr.getASPField("RECEIVE_UNIT_NO").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();

      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout()){
         //unsetHidden function field , differ with doc class
         mgr.getASPField("SUB_CLASS_NAME").unsetHidden();
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("MACH_GRP_DESC").unsetHidden();
         mgr.getASPField("SPECIALTY_DESC").unsetHidden();
         mgr.getASPField("ROOM_DESC").unsetHidden();
         mgr.getASPField("LOT_DESC").unsetHidden();
         mgr.getASPField("GRADE_DESC").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SEND_DEPT_DESC").unsetHidden();
         mgr.getASPField("RECEIVE_UNIT_NAME").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return "DOCTITLECONSTRUCTIONTITLE: Overview - Project Construction";
   }
   
   @Override
   protected void lastMethod() {
      
   }

}
