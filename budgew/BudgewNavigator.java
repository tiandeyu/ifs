package ifs.budgew;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class BudgewNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.BudgewNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.engmaw.EngmawNavigator!!");
        
      ASPNavigatorNode budgew_nav = mgr.newASPNavigatorNode("BUDGEWNAVIGATOR: Budget Management");
      
      budgew_nav.addItem("BUDGEWNAVIGATORBUDGETBASICDATA: Basic Data", "budgew/BudgetBasicData.page");
      budgew_nav.addItem("BUDGEWNAVIGATORPROJECTBUSGETTEMP: Project Budget Temp","budgew/ProjectBudgetTemp.page"); 
      budgew_nav.addItem("BUDGEWNAVIGATORPROJECTBUSGET: Project Budget","budgew/ProjectBudget.page");  
      return budgew_nav;                                               
   }  
   
}    
