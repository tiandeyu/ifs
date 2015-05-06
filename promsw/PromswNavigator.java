package ifs.promsw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;

public class PromswNavigator implements Navigator {
	public static boolean DEBUG = Util.isDebugEnabled("ifs.promsw.PromswNavigator");

	   public ASPNavigatorNode add(ASPManager mgr)
	   {
	      if(DEBUG) Util.debug("!!PromswNavigator!!");
 
	      ASPNavigatorNode nod = mgr.newASPNavigatorNode("PROJECTNAVIGATORTITLE: Project Manager Titel");   
	      
	      ASPNavigatorNode info_node = nod.addNode("PROJECTNAVIGATORPROJECTDATA: Project Data");
	      
	      info_node.addItem("PROJECTNAVIGATORGENERALPROJECT: General Project","genbaw/GeneralProject.page","");
	      info_node.addItem("PROJECTNAVIGATORPROJECTSETUP: Project Setup","genbaw/ProjectSetup.page","");
	      info_node.addItem("PROJECTNAVIGATORPROJECTINVESTINFO: Project Invest Info","genbaw/ProjectInvestInfo.page","");
	      info_node.addItem("PROJECTNAVIGATORPROJECTBUILDPREPARE: Project Build Prepare","genbaw/ProjectBuildPrepare.page","");
	      info_node.addItem("PROJECTNAVIGATORPROJECTBUILD: Project Build","genbaw/ProjectBuild.page","");
	      info_node.addItem("PROJECTNAVIGATORPROJECTBUILDSTART: Project Build Start","genbaw/ProjectBuildStart.page","");
	      
	      ASPNavigatorNode data_node = nod.addNode("PROJECTNAVIGATORDATA: Genernal Data");
	      
	      data_node.addItem("PROJECTNAVIGATORPROJECTSORT: Project Sort","genbaw/ProjectSort.page","");
	      data_node.addItem("PROJECTNAVIGATORPROJECTTYPE: Project Type","genbaw/ProjectType.page","");
	      data_node.addItem("PROJECTNAVIGATORPROJECTPHASE: Project Phase","genbaw/ProjectPhase.page","");
	      data_node.addItem("PROJECTNAVIGATORPROJECTINVEST: Project Invest","genbaw/ProjectInvest.page","");
	      data_node.addItem("PROJECTNAVIGATORPROJECTTIMETYPE: Project TimeType","genbaw/ProjectTimeType.page","");
	      data_node.addItem("PROJECTNAVIGATORPROJECTINFOTEMP: Project InfoTemp","genbaw/ProjectInfoTemp.page","");
	      data_node.addItem("PROJECTNAVIGATORPROJECTFILETEMP: Project FileTemp","genbaw/ProjectFileTemp.page","");
	      data_node.addItem("PROJECTNAVIGATORGENERALAREADATA: General Area Data","genbaw/GeneralAreaData.page","");
	      
		  return nod;
	   }
}
