package ifs.bidmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class BidmawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.bidmaw.BidmawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.bidmaw.BidmawNavigator!!");
      
      ASPNavigatorNode bidmaw_nav = mgr.newASPNavigatorNode("BIDMAWNAVIGATOR: Bid Management");
      
      ASPNavigatorNode n_four = bidmaw_nav.addNode(mgr.translate( "BIDMAWNAVIGATORBASEDATAMANAGEMENT: Base Data Management" ));
      n_four.addItem("BIDMAWNAVIGATORBIDMETHOD: Bid Method", "bidmaw/BidMethod.page");
      
      ASPNavigatorNode n_two = bidmaw_nav.addNode(mgr.translate( "BIDMAWCREATEPROJMANAGEMENT: Create Project Management" ));
      n_two.addItem("BIDMAWNAVIGATORBIDCREPROJAPPLY: Bid Cre Proj Apply", "bidmaw/BidCreProjApply.page");
      
      ASPNavigatorNode n_three = bidmaw_nav.addNode(mgr.translate( "BIDMAWNAVIGATORBIDENQMANAGEMENT: Bid Enq Management" ));
      n_three.addItem("BIDMAWBIDENQDOCMAN: Bid Enq Doc Man", "bidmaw/BidEnqDocMan.page");
      n_three.addItem("BIDMAWBIDPROJPREENQVERIFY: Bid Projpre Enq Verify", "bidmaw/BidProjpreEnqVerify.page");
      n_three.addItem("BIDMAWBIDMATPREENQVERIFY: Bid Matpre Enq Verify", "bidmaw/BidMatpreEnqVerify.page");
      n_three.addItem("BIDMAWBIDPROJENQJUDGE: Bid Proj Enq Judge", "bidmaw/BidProjEnqJudge.page");
      n_three.addItem("BIDMAWBIDMATENQJUDGE: Bid Mat Enq Judge", "bidmaw/BidMatEnqJudge.page");
      
      
      
      ASPNavigatorNode n = bidmaw_nav.addNode(mgr.translate( "BIDMAWNAVIGATORBIDENTRUSTMANAGEMENT: Bid Entrust Management" ));
      n.addItem("BIDMAWNAVIGATORBIDPROJENTRUST: Bid Proj Entrust", "bidmaw/BidProjEntrust.page");
      n.addItem("BIDMAWNAVIGATORBIDPROJENTRUSTREPORT: Bid Proj Entrust Report", "bidmaw/BidProjEntrustReport.page");
      n.addItem("BIDMAWNAVIGATORBIDMATEMERGENCYREQUIRE: Bid Mat Emergency Require", "bidmaw/BidMatEmergencyReq.page");
      n.addItem("BIDMAWNAVIGATORBIDMATEMERESULT: Bid Mat Eme Result", "bidmaw/BidMatEmeResult.page");
      n.addItem("BIDMAWNAVIGATORBIDMATREQ: Bid Mat Req", "bidmaw/BidMatReq.page");
      n.addItem("BIDMAWNAVIGATORBIDMATPURCH: Bid Mat Purch", "bidmaw/BidMatPurch.page");
      n.addItem("BIDMAWNAVIGATORBIDMATRESULT: Bid Mat Result", "bidmaw/BidMatResult.page");
      
      ASPNavigatorNode n_five = bidmaw_nav.addNode(mgr.translate( "BIDMAWNAVIGATORBIDMANAGEMENT: Bid Management" ));
      n_five.addItem("BIDMAWNAVIGATORBIDDOCMANAGEMENT: Bid Doc Management", "bidmaw/BidDocManagement.page");
      n_five.addItem("BIDMAWNAVIGATORBIDPLAN: Bid Plan", "bidmaw/BidPlan.page");
      n_five.addItem("BIDMAWNAVIGATORBIDRESULT: Bid Result", "bidmaw/BidResult.page");
      
      return bidmaw_nav;                                       
   }  
}
