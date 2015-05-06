package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocTitleEquipment extends DocTitleAncestor {

   public DocTitleEquipment(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_EQUIPMENT;
   }

   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      
      mgr.getASPField("DOC_CODE").setLabel("DOCTITLEEQUIPMENTDOCCODE: Doc Code");
      mgr.getASPField("TRANSFER_NO").setLabel("DOCTITLEEQUIPMENTTRANSFERNO: Transfer No");
      mgr.getASPField("REV_TITLE").setLabel("DOCTITLEEQUIPMENTREVTITLE: Rev Title");
      
      String fieldGroup1 = "REV_TITLE,TRANSLATE_TITLE,DOC_CODE,INNER_DOC_CODE,DOC_STATE,FIRST_REVISION,VIEW_FILE,CHECK_IN_FILE,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,MACH_GRP_NO,MACH_GRP_DESC,SPECIALTY_NO,SPECIALTY_DESC,COMPONENT_TYPE,COMPONENT_TYPE_DESC,DOC_LOC_NO,COMPONENT_NO,LOT_NO,LOT_DESC,WBS_NO,PROFESSION_NO,PROFESSION_NAME,";
      String fieldGroup3 = "COPIES,PAGES,BOOKLET_NO,BOOKLET_NAME,PAGE_SIZE,SIZE_NAME,SUB_CLASS,SUB_CLASS_NAME,SEC_LEVEL,LEVEL_NAME,SEND_UNIT_NO,SEND_UNIT_NAME,TRANSFER_NO,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2 + fieldGroup3);
      
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("TRANSLATE_TITLE", 5);
//      headlay.setDataSpan("SEND_UNIT_NO", 5);
      headlay.setDataSpan("INNER_SEND", 5);
      
//      headlay.defineGroup("Main","OBJID,OBJVERSION,DOC_CLASS,SDOCNAME,DOC_NO,FIRST_SHEET_NO,TITLE,NUM_GEN_TRANSLATED,BOOKING_LIST,ID1,ID2,LANGUAGE_CODE,FORMAT_SIZE," +
//            "REV_TITLE,TRANSLATE_TITLE,DOC_CODE,INNER_DOC_CODE,ROWSTATE,FIRST_REVISION,VIEW_FILE,CHECK_IN_FILE",false,true);
//      headlay.defineGroup(mgr.translate("DOCMAWDOCTITLEMETERIALRELATED: Meterial Related Attributes"),
//            "PROJ_NO,PROJ_NAME,MACH_GRP_NO,MACH_GRP_DESC,SPECIALTY_NO,SPECIALTY_DESC,COMPONENT_TYPE,COMPONENT_TYPE_DESC,DOC_LOC_NO,COMPONENT_NO,LOT_NO,LOT_DESC,WBS_NO",true,false);
//      headlay.defineGroup(mgr.translate("DOCMAWDOCTITLEFILERELATEDATTRIBUTES: File Related Attributes"),
//            "COPIES,PAGES,BOOKLET_NO,BOOKLET_NAME,PAGE_SIZE,SIZE_NAME,SUB_CLASS,SUB_CLASS_NAME,SEC_LEVEL,LEVEL_NAME,SEND_UNIT_NO,SEND_UNIT_NAME,TRANSFER_NO,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,USER_CREATED,USER_CREATED_NAME,DT_CRE," +
//            "RECEIVE_DATE,PURPOSE_NO,PURPOSE_NAME,MAIN_CONTENT,RECEIVE_UNIT_NO,RECEIVE_UNIT_NAME,OBJ_CONN_REQ,VIEW_FILE_REQ,STRUCTURE,MAKE_WASTE_REQ,SAFETY_COPY_REQ,ALTERNATE_DOCUMENT_NUMBER,CONFIDENTIAL,ISO_CLASSIFICATION,INFO,ORIG_DOC_CLASS,ORIG_DOC_NO,ORIG_DOC_SHEET," +
//            "ORIG_DOC_REV,REPL_BY_DOC_CLASS,REPL_BY_DOC_NO,TITLE_REV,TITLE_REV_NOTE,SEND_DATE,EMERGENCY,EMERGENCY_DATE,STATE,PROJECT_NO,ZONE_NO,FROM_DOC_CLASS,FROM_DOC_NO,FROM_DOC_SHEET,FROM_DOC_REV",true,false);
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("INNER_DOC_CODE").unsetHidden();
      mgr.getASPField("DOC_STATE").unsetHidden();
      mgr.getASPField("FIRST_REVISION").unsetHidden();
      mgr.getASPField("VIEW_FILE").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("MACH_GRP_NO").unsetHidden();
      mgr.getASPField("SPECIALTY_NO").unsetHidden();
      mgr.getASPField("COMPONENT_TYPE").unsetHidden();
      mgr.getASPField("DOC_LOC_NO").unsetHidden();
      mgr.getASPField("COMPONENT_NO").unsetHidden();
      mgr.getASPField("LOT_NO").unsetHidden();
      mgr.getASPField("WBS_NO").unsetHidden();
      mgr.getASPField("PROFESSION_NO").unsetHidden();
      mgr.getASPField("COPIES").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("BOOKLET_NO").unsetHidden();
      mgr.getASPField("PAGE_SIZE").unsetHidden();
      mgr.getASPField("SUB_CLASS").unsetHidden();
      mgr.getASPField("SEC_LEVEL").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();

      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout()){
         //unsetHidden function field , differ with doc class
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("MACH_GRP_DESC").unsetHidden();
         mgr.getASPField("SPECIALTY_DESC").unsetHidden();
         mgr.getASPField("COMPONENT_TYPE_DESC").unsetHidden();
         mgr.getASPField("PROFESSION_NAME").unsetHidden();
         mgr.getASPField("LOT_DESC").unsetHidden();
         mgr.getASPField("BOOKLET_NAME").unsetHidden();
         mgr.getASPField("SIZE_NAME").unsetHidden();
         mgr.getASPField("SUB_CLASS_NAME").unsetHidden();
         mgr.getASPField("LEVEL_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return "DOCTITLEEQUIPMENTTITLE: Overview - Project Equipment";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
