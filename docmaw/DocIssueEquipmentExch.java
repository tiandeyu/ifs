package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.service.FndException;

public class DocIssueEquipmentExch extends DocIssueEquipment {

   public DocIssueEquipmentExch(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   public void adjust() throws FndException
   {
      super.adjust();
      headbar.removeCustomCommand("createDocDistribution");
   }
   
   @Override
   protected String getCurrentPageDocClass() {
      return DocmawConstants.EXCH_EQUIPMENT;
   }

   @Override
   protected String getTitle() {
       return "DOCISSUEEQIUPMENTTITLEEXCH: Exchange Equipments";
   }
   
   @Override
   protected void lastMethod() {
      
   }
}
