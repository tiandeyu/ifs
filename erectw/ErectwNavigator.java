package ifs.erectw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;

public class ErectwNavigator implements Navigator {

   public ASPNavigatorNode add(ASPManager mgr) {
      ASPNavigatorNode node = mgr.newASPNavigatorNode("ERECTTESTSNAVIGATORTITLE: ErectTests");
      node.addItem("ERECTWNAVIGATORETETDATA: EtData", "erectw/ErectData.page");
      node.addItem("ERECTWNAVIGATORETSTART: EtStart", "erectw/EtStart.page");
      node.addItem("ERECTWNAVIGATORETREPORT: EtReport", "erectw/EtReport.page");
      return node;
   }

}
