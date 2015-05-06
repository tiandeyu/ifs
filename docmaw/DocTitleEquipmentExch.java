package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
/**
 * 
 * @author lu
 *
 */
public class DocTitleEquipmentExch extends DocTitleEquipment {

   public DocTitleEquipmentExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_EQUIPMENT;
   }

   @Override
   protected String getTitle() {
       return "DOCTITLEEQIUPMENTTITLEEXCH: Overview - Exchange Equipments";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
