package ifs.hzwflw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
import ifs.hzwflw.util.HzConstants;

public class HzwflwNavigator  implements Navigator,HzConstants{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzwflwNavigator");
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!HzwflwNavigator!!");
      
      ASPNavigatorNode workbenchNode = mgr.newASPNavigatorNode("HZWFLWNAVIGATORWORKBENCH: Workbench");
      
      if (mgr.getASPConfig().isChangePasswordEnabled())
         workbenchNode.addItem( "FNDSERNAVCHANGEPWD: Change Password", "common/scripts/ChangePassword.page");
      
      workbenchNode.addItem( "FNDSERNAVUSERIPADDRESS: User IP Address", "common/scripts/FndUser.page", "FND_USER,FND_USER_IP_ADDRESS");
      workbenchNode.addItem( "FNDSERNAVIMPORTEXCEL: Import Excel", "genbaw/ImportExcel.page", "GENERAL_IMPORT,GENERAL_IMPORT_INFO,GENERAL_IMPORT_HIST");
      workbenchNode.addItem( "FNDSERNAVGENERALIMPORT: Import Information", "genbaw/GeneralImport.page", "GENERAL_IMPORT,GENERAL_IMPORT_INFO,GENERAL_IMPORT_HIST");
      
      workbenchNode.addItem("HZWFLWNAVIGATORWORKFLOWTEST: Workflow Test","hzwflw/HzBizWfTest.page", "HZ_BIZ_WF_TEST");
      ASPNavigatorNode config = workbenchNode.addNode("HZWFLWNAVIGATORADMINCONSOLE: Admin Console");
      
      config.addItem("HZWFLWNAVIGATORPROCESSDEFINITION: Process Design","hzwflw/HzIntermediatePageForDesign.page?_WF_PAGE_FROM_FLAG_=_FROM_PROCESS_DESIGN_PAGE_", "HZ_WF_ADMIN");
//	      config.addItem("HZWFLWNAVIGATORPROCESSDEFINITION: Process Design","/b2e/horizon/designer/HorizonDesigner.jsp", "HZ_WF_ADMIN");
//	      config.addItem("HZWFLWNAVIGATORPROCESSDEFINITION: Process Design","hzwflw/HzwflwNewWindowOpener.page?"+ FROM_FLAG + "=" + FROM_NAVIGATOR +"&target=designer");
//	      config.addItem("HZWFLWNAVIGATORPROCESSCONFIG: Process Config old","hzwflw/HzBizWfConfig.page");
      config.addItem("HZWFLWNAVIGATORPROCESSCONFIG: Process Config","hzwflw/HzWfProcessDef.page", "HZ_WF_ADMIN");
      config.addItem("HZWFLWNAVIGATORPROCESSMONITOR: Monitor","hzwflw/HzwflwNewWindowOpener.page?"+ FROM_FLAG + "=" + FROM_NAVIGATOR +"&target=monitor", "HZ_WF_ADMIN");
//	      config.addItem("HZWFLWNAVIGATORPROCESSCONFIG: Delegate Admin","hzwflw/HzwflwNewWindowOpener.page?"+ FROM_FLAG + "=" + FROM_NAVIGATOR +"&target=delegateAdmin");
      config.addItem("HZWFLWNAVIGATORPROCESSDELEGATE: Process Delegate","hzwflw/HzDelegateSetNav.page");
      config.addItem("HZWFLWNAVIGATORPROCESSDELEGATENEW: Delegate Batch Set","hzwflw/HzDelegateBatchSet.page");
      config.addItem("HZWFLWNAVIGATORPROCESSNODEFUNCTION: Node Function","hzwflw/HzNodeFuncNav.page","HZ_NODE_FUNC");
      config.addItem("HZWFLWNAVIGATORPROCESSNODESYNC: Node Synchronize","hzwflw/HzNodeSyncNav.page","HZ_NODE_SYNC");
      config.addItem("HZWFLWNAVIGATORPAGETABLEPAIR: Page Table Pair","hzwflw/HzPageTablePair.page","HZ_PAGE_TABLE_PAIR");
      config.addItem("HZWFLWNAVIGATORFLOWINFOSYNCSET: Flow Info Sync Set","hzwflw/HzFlowInfoSyncSet.page","HZ_FLOW_INFO_SYNC_SET");
      config.addItem("HZWFLWNAVIGATORPROCESSADMINISTRATORSET: Administror Set","hzwflw/HzAdministratorSet.page", "HZ_WF_ADMIN");
      config.addItem("HZWFLWNAVIGATORFNDUSERUMMAP: User Map","hzwflw/FndUserUmMap.page", "FND_USER_UM_MAP");      
      config.addItem("HZWFLWNAVIGATORREPORTLIST: Report List","hzwflw/HzReportList.page", "HZ_REPORT_LIST");      
      
      workbenchNode.addItem("HZWFLWNAVIGATORTODOWORKBENCH: Todo Workbench","hzwflw/HzTodoWorkbench.page");
      workbenchNode.addItem("HZWFLWNAVIGATORDONEWORKBENCH: Done Workbench","hzwflw/HzDoneWorkbench.page");
      workbenchNode.addItem("HZWFLWNAVIGATORNOTIFICATIONCENTER: Notification Center", "genbaw/GeneralNotification.page", "GENERAL_NOTIFICATION");
      workbenchNode.addItem("HZWFLWNAVIGATORNOTIFICATIONCENTEROWN: Notification Center Own Query", "genbaw/GeneralNotificationOwn.page", "GENERAL_NOTIFICATION");
      ASPNavigatorNode logon_info_node = workbenchNode.addNode("FNDLWNAVIGATORNODELOGONINFOADMIN: Logon Info Admin");
      logon_info_node.addItem("FNDLWNAVIGATORNODELOGONINFOWEATHER: Weather", "fndlw/LogonWeather.page", "HZ_WF_ADMIN");
      logon_info_node.addItem("FNDLWNAVIGATORNODELOGONINFOCONTENTS: Info", "fndlw/LogonInfo.page", "HZ_WF_ADMIN");
      ASPNavigatorNode ws_node = workbenchNode.addNode("FNDLWNAVIGATORNODEIFSWEBSERVICENODE: Webservice");
      ws_node.addItem("FNDLWNAVIGATORNODEWEBSERVICEIFSTCBASICMAP: Tc Basic Data Map", "ifsww/IfsTcBasicMap.page", "IFS_TC_BASIC_MAP");
      
      return workbenchNode;
   }
}
