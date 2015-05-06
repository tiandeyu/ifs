package ifs.engmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class EngmawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.engmaw.EngmawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.engmaw.EngmawNavigator!!");
      
      ASPNavigatorNode engmaw_nav = mgr.newASPNavigatorNode("ENGMAWNAVIGATOR: Engineering Management");
      
      
      engmaw_nav.addItem("ENGMAWNAVIGATORENGBASICDATA: Eng Basic Data", "engmaw/EngBasicData.page");
      engmaw_nav.addItem("ENGMAWNAVIGATORPRELIMINARYDESIGN: Preliminary Design", "engmaw/DesgRule.page");
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGLIST: Drawing List","engmaw/DrawingList.page");  
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGREQUIREPLAN: Drawing Require Plan", "engmaw/DrawingRequirePlan.page");
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGARRIVEREPORT: Drawing Arrive Report", "engmaw/DrawingArriveReport.page");
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGARRIVE: Drawing Arrive", "engmaw/DrawingArrive.page");                                      
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGBORROW: Drawing Borrow", "engmaw/DrawingBorrow.page");
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGBACK: Drawing Back", "engmaw/DrawingBack.page");
      engmaw_nav.addItem("ENGMAWNAVIGATORDESGCHANGE: Design Change", "engmaw/DesgChange.page");    
      engmaw_nav.addItem("ENGMAWNAVIGATORCHANGEDESG: Change Design", "engmaw/ChangeDesg.page");  
      engmaw_nav.addItem("ENGMAWNAVIGATORCHANGEDESIGN: Change Design New", "engmaw/ChangeDesign.page");           
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGSENDMAN: Drawing Send Man", "engmaw/DrawingSendMan.page");       
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGSENDENG: Drawing Send Eng", "engmaw/DrawingSendEng.page");       
      engmaw_nav.addItem("ENGMAWNAVIGATORDRAWINGCOUNTERCHECKDESC: Drawing Counter Check", "engmaw/DrawingCounterCheck.page");  
      engmaw_nav.addItem("ENGMAWNAVIGATORDESGCHANGENOTICE: Desg Change Notice", "engmaw/DesgChangeNotice.page");    
      engmaw_nav.addItem("ENGMAWNAVIGATORCHANGEDESGREQ: Change Desg Req", "engmaw/ChangeDesgReq.page");    
      engmaw_nav.addItem("ENGMAWNAVIGATORDESGCHANGEFEEDBACK: Desg Change Feedback", "engmaw/DesgChangeFeedback.page");    
      return engmaw_nav;                                
   }  
}
