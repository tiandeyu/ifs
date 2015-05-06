package ifs.conmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class ConmawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.conmawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.conmaw.conmawNavigator!!");
      
      ASPNavigatorNode conmaw_nav = mgr.newASPNavigatorNode("CONMAWNAVIGATOR: Construction Manager");
      
      conmaw_nav.addItem("CONMAWNAVIGATORCONBASICDATA: Con Basic Data", "conmaw/ConBasicData.page");
      conmaw_nav.addItem("CONMAWNAVIGATORCONMAJORDATA: Con Major Data", "conmaw/ConMajorData.page");
      conmaw_nav.addItem("CONMAWNAVIGATORCONDOCTYPEDATA: Con Doc Type Data", "conmaw/ConDocTypeData.page");
      conmaw_nav.addItem("CONMAWNAVIGATORCONGENSETDATA: Con Genset Data", "conmaw/ConGensetData.page");
      ASPNavigatorNode n = conmaw_nav.addNode(mgr.translate( "CONMAWNAVIGATORSTARTPREPARE: Start Prepare" ));
      n.addItem("CONMAWNAVIGATORCONCONSTRUCTIONDESIGN: Con Construction Design", "conmaw/ConConstructionDesign.page");      
      n.addItem("CONMAWNAVIGATORCONPLANSORTCHECK: Con Plan Sort Check", "conmaw/ConPlanSortCheck.page");
      n.addItem("CONMAWNAVIGATORCONPLANCHECK: Con Plan Check", "conmaw/ConPlanCheck.page");
      n.addItem("CONMAWNAVIGATORCONDESIGNTECHNOTIFY: Con Design Tech Notify", "conmaw/ConDesignTechNotify.page");
      n.addItem("CONMAWNAVIGATORCONPROJMEASURECHECK: Con Proj Measure Check", "conmaw/ConProjMeasureCheck.page");
      n.addItem("CONMAWNAVIGATORCONCONTRACTORAPPRAISE: Con Contractor Appraise", "conmaw/ConContractorAppraise.page");
      n.addItem("CONMAWNAVIGATORCONSPECIALPERSONCHECK: Con Special Person Check", "conmaw/ConSpecialPersonCheck.page");
      n.addItem("CONMAWNAVIGATORCONMAINEQUCHECK: Con Main Equ Check", "conmaw/ConMainEquCheck.page");
      conmaw_nav.addItem("CONMAWNAVIGATORCONSTARTREPORT: Con Start Report", "conmaw/ConStartReport.page");
      
      ASPNavigatorNode t = conmaw_nav.addNode(mgr.translate( "CONMAWNAVIGATORCONSTRUCTIONPROCESSMANAGER: Construction Process Manager" ));
      t.addItem("CONMAWNAVIGATORADJUSTNOTICE: Adjust Notice", "conmaw/ConAdjustNotice.page");
      t.addItem("CONMAWNAVIGATORADJUSTREPLY: Adjust Reply", "conmaw/ConAdjustReply.page");
      t.addItem("CONMAWNAVIGATORENGINEERNOTICE: Engineer Notice", "conmaw/ConEngineerNotice.page");
      t.addItem("CONMAWNAVIGATORENGINEERNOTICEREPLY: Engineer Notice Reply", "conmaw/ConEngineerNoticeReply.page");
      t.addItem("CONMAWNAVIGATORCHECKNOTICE: Check Notice", "conmaw/ConCheckNotice.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTCOSTOWNER: Proj Connection List Cost Owner", "conmaw/ConProjConnectionListCostOwner.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTCOSTORG: Proj Connection List Cost Org", "conmaw/ConProjConnectionListCostOrg.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTCOSTREPLY: Proj Connection List Cost Reply", "conmaw/ConProjConnectionListCostReply.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTFREENOREPLY: Proj Connection List Free No Reply", "conmaw/ConProjConnectionListFreeNoReply.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTFREEREPLY: Proj Connection List Free Reply", "conmaw/ConProjConnectionListFreeReply.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTINNERREPLY: Proj Connection List Inner Reply", "conmaw/ConProjConnectionListInnerReply.page");
      t.addItem("CONMAWNAVIGATORPROJCONNECTIONLISTINNERNOREPLY: Proj Connection List Inner No Reply", "conmaw/ConProjConnectionListInnerNoReply.page");
      t.addItem("CONMAWNAVIGATORSUPERVISORPROJCONNECTIONLIST: Supervisor Proj Connection List", "conmaw/ConSupervisorProjConnectionList.page");
      t.addItem("CONMAWNAVIGATORCONTRACTQUANTITIESVISA: Contract Quantities Visa", "conmaw/ConContractQuantitiesVisa.page");
      t.addItem("CONMAWNAVIGATORENTRUSTQUANTITIESVISA: Entrust Quantities Visa", "conmaw/ConEntrustQuantitiesVisa.page");

      conmaw_nav.addItem("CONMAWNAVIGATORCONPROJCONSTRUCTIONMAN: Con Proj Construction Man", "conmaw/ConProjConstructionManNav.page");
      conmaw_nav.addItem("CONMAWNAVIGATORCONPROJCONSTRUCTIONMANALL: Con Proj Construction Man All", "conmaw/ConProjConstructionManAllNav.page");
      conmaw_nav.addItem("CONMAWNAVIGATORSPECIALPROJMAN: Special Proj Sort", "conmaw/ConSpecialProjSort.page");
      
      ASPNavigatorNode m = conmaw_nav.addNode(mgr.translate( "CONMAWNAVIGATORPROJECTACCEPT: Contract Project Complete Check And Accept" ));
      
      m.addItem("CONMAWNAVIGATORSINGLEPROJACCEPT: Single Proj Accept", "conmaw/ConSingleProjAccept.page");
      m.addItem("CONMAWNAVIGATORORGPROJACCEPT: Org Proj Accept", "conmaw/ConOrgProjAccept.page");
      m.addItem("CONMAWNAVIGATORSPORADICPROJACCEPT: Sporadic Proj Accept", "conmaw/ConSporadicProjAccept.page");

      conmaw_nav.addItem("CONMAWNAVIGATORCCONMEETINGSUMMARY: Con Meeting Summary", "conmaw/ConMeetingSummary.page");
      
      return conmaw_nav;                                       
   }  
}
