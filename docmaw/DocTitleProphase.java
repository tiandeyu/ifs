package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocTitleProphase extends DocTitleAncestor {

   public DocTitleProphase(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_PROPHASE;
   }

   @Override
   protected void customizeHeadlay() {
      ASPManager mgr = getASPManager();
      mgr.getASPField("DOC_CODE").setLabel("DOCTITLEPROPHASEDOCCODE: Doc Code");
      mgr.getASPField("TRANSFER_NO").setLabel("DOCTITLEPROPHASETRANSFERNO: Transfer No");
      
      String fieldGroup1 = "REV_TITLE,TRANSLATE_TITLE,DOC_CODE,SUB_CLASS,SUB_CLASS_NAME,INNER_DOC_CODE,DOC_STATE,ROWSTATE,VIEW_FILE,CHECK_IN_FILE,";
      String fieldGroup2 = "PROJ_NO,PROJ_NAME,MACH_GRP_NO,MACH_GRP_DESC,SPECIALTY_NO,SPECIALTY_DESC,DOC_LOC_NO,WBS_NO,SEC_LEVEL,LEVEL_NAME,PAGES,FIRST_REVISION,SEND_UNIT_NO,SEND_UNIT_NAME,TRANSFER_NO,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,USER_CREATED,USER_CREATED_NAME,DT_CRE,";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("TRANSLATE_TITLE", 5);
      // headlay.setDataSpan("PROJ_NO", 5);
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("SUB_CLASS").unsetHidden();
      mgr.getASPField("INNER_DOC_CODE").unsetHidden();
      mgr.getASPField("DOC_STATE").unsetHidden();
      mgr.getASPField("ROWSTATE").unsetHidden();
      mgr.getASPField("VIEW_FILE").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("FIRST_REVISION").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();
      
      mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
      mgr.getASPField("MACH_GRP_NO").unsetHidden();
      mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
      mgr.getASPField("SPECIALTY_NO").unsetHidden();
      mgr.getASPField("DOC_LOC_NO").unsetHidden();
      mgr.getASPField("WBS_NO").unsetHidden();
      mgr.getASPField("SEC_LEVEL").unsetHidden();

      if(headlay.isFindLayout() ||headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout())
      {
         mgr.getASPField("SUB_CLASS_NAME").unsetHidden();
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
         
         mgr.getASPField("MACH_GRP_DESC").unsetHidden();
         mgr.getASPField("SPECIALTY_DESC").unsetHidden();
         mgr.getASPField("LEVEL_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return "DOCTITLEPROPHASETITLE: Overview - Project Prophase";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
