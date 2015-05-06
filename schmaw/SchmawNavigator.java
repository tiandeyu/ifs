package ifs.schmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class SchmawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchmawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.schmaw.SchmawNavigator!!");
      
      ASPNavigatorNode Schmaw_nav = mgr.newASPNavigatorNode("SCHMAWNAVIGATOR: Schedule Manager");

      ASPNavigatorNode m = Schmaw_nav.addNode("SCHEDULENAVIGATORDATA: Genernal Data");
      m.addItem("SCHEDULENAVIGATORPROJECTSORT: Schedule Eps","schmaw/SchEpsNav.page","");
	   m.addItem("SCHEDULENAVIGATORPROJECTTYPE: Schedule Resource","schmaw/SchResource.page","");
	   m.addItem("SCHEDULENAVIGATORPROJECTPHASE: Schedule Base Data","schmaw/SchBaseData.page","");
      
      ASPNavigatorNode n = Schmaw_nav.addNode("SCHEDULENAVIGATORSCHEDULEPLAN: Schedule Plan");
      n.addItem("SCHMAWNAVIGATORSCHPLANWBS: Sch Plan Wbs", "schmaw/SchPlanWbs.page");           
      n.addItem("SCHMAWNAVIGATORSCHPLANWORKNAV: Sch Plan Work Nav", "schmaw/SchPlanWorkNav.page");
      n.addItem("SCHMAWNAVIGATORSCHPLANCHECK: Sch Plan Check", "schmaw/SchPlanCheck.page");
      
      return Schmaw_nav;                                       
   }  
}  
