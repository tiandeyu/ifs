package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocTitleTemp extends DocTitleAncestor {

   public DocTitleTemp(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_TEMP;
   }

   @Override
   protected void customizeHeadlay() { 
      ASPManager mgr = getASPManager();
      
      mgr.getASPField("REV_TITLE").setLabel("DOCTITLETEMPREVTITLE: Rev Title");
      String fieldGroup1 = "REV_TITLE,VIEW_FILE,CHECK_IN_FILE,PROJ_NO,PROJ_NAME,SUB_CLASS,SUB_CLASS_NAME,SEND_UNIT_NO,SEND_UNIT_NAME,SEND_DEPT,SEND_DEPT_DESC,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,USER_CREATED,USER_CREATED_NAME,DT_CRE";
      headlay.setFieldOrder(fieldGroup1);
      
      headlay.setDataSpan("REV_TITLE", 5);
   }
   
   public void adjust() throws FndException{
      ASPManager mgr = getASPManager();
      super.adjust();
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("VIEW_FILE").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("SUB_CLASS").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("SEND_DEPT").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();

      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout()){
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("SUB_CLASS_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SEND_DEPT_DESC").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
      }
   }

   @Override
   protected String getTitle() {
       return "DOCTITLETEMPTITLE: Overview - Project Temp";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
