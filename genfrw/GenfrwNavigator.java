package ifs.genfrw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
import ifs.hzwflw.util.HzConstants;

public class GenfrwNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.genfrw.GenfrwNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.genfrw.GenfrwNavigator!!");
      
      ASPNavigatorNode genfrw_nav = mgr.newASPNavigatorNode("GENFRWNAVIGATOR: General Form Management");
      
      genfrw_nav.addItem("GENFRWNAVIGATORTYPE: General Form Type", "genfrw/GenFormType.page", "GEN_FORM_TYPE");
      genfrw_nav.addItem("GENFRWNAVIGATORGROUPS: General Form Groups", "genfrw/GenFormTypeGroups.page", "GEN_FORM_TYPE_GROUPS");
      genfrw_nav.addItem("GENFRWNAVIGATORSMALL: General Form Nav", "genfrw/GenFormNav.page", "GEN_FORM_SMALL");
      
      return genfrw_nav;
   }
}
