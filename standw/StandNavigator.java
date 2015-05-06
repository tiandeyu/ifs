package ifs.standw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;

public class StandNavigator implements Navigator {

   public ASPNavigatorNode add(ASPManager mgr) {
      ASPNavigatorNode node = mgr.newASPNavigatorNode("STANDNAVIGATORSTANDARD:  Standard");
      node.addItem("STANDNAVIGATORSTANDARDDATA: StandardData", "standw/StandardData.page");
      //node.addItem("STANDNAVIGATORASSESSSTDCLASSIFY: AssessStdClassify", "standw/AssessStdClassify.page");
     // node.addItem("STANDNAVIGATORCHECKSTYLE: CheckStyle", "standw/CheckStyle.page");
//     node.addItem("STANDNAVIGATORPROFESSIONGROUP: ProfessionGroup", "standw/ProfessionGroup.page");
//     node.addItem("STANDNAVIGATORREACHSTDTEMP: ReachStdTemp", "standw/ReachStdTemp.page");
      node.addItem("STANDNAVIGATORREACHSTDPLAN: ReachStdPlan", "standw/ReachStdPlan.page");
     
      ASPNavigatorNode childSelf = node.addNode("SELFCHECK: Selfcheck");
      childSelf.addItem("STANDNAVIGATORSELFNECECOND: SelfNeceCond", "standw/SelfNeceCond.page");
      childSelf.addItem("STANDNAVIGATORSELFCHECKRLT: SelfcheckRlt", "standw/SelfcheckRlt.page");
      ASPNavigatorNode childPre = node.addNode("PRECHECK: Precheck");
      childPre.addItem("STANDNAVIGATORPRENECECOND: PreNeceCond", "standw/PreNeceCond.page");
      childPre.addItem("STANDNAVIGATORPRECHECKRLT: PrecheckRlt", "standw/PrecheckRlt.page");
      ASPNavigatorNode childRe = node.addNode("RECHECK: Recheck");
      childRe.addItem("STANDNAVIGATORRENECECOND: ReNeceCond", "standw/ReNeceCond.page");
      childRe.addItem("STANDNAVIGATORRECHECKRLT: RecheckRlt", "standw/RecheckRlt.page");
      
      node.addItem("STANDNAVIGATORREFROMPLAN: RefromPlan", "standw/RefromPlan.page");
      return node;
   }

}
