package ifs.matmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
  
public class MatmawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatmawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!ifs.matmaw.MatmawNavigator!!");
      
      ASPNavigatorNode matmaw_nav = mgr.newASPNavigatorNode("MATMAWNAVIGATOR: Mat Management");
      
      ASPNavigatorNode n = matmaw_nav.addNode(mgr.translate( "MATMAWNAVIGATORBASICSETTINGS: Basic Settings" ));
      n.addItem("MATMAWNAVIGATORMATSTORAGETYPE: Mat Storage Type", "matmaw/MatStorageType.page");
      n.addItem("MATMAWNAVIGATORMATSTOWAGE: Mat Stowage", "matmaw/MatStowage.page");
      n.addItem("MATMAWNAVIGATORMATTYPE: Mat Type", "matmaw/MatType.page");
      n.addItem("MATMAWNAVIGATORMATCODENAV: Mat Code Nav", "matmaw/MatCodeNav.page");
      n.addItem("MATMAWNAVIGATORMATSUPEQUIPMENT: Mat Sup Equipment", "matmaw/MatSupEquipment.page");
      
//      ASPNavigatorNode n_two = matmaw_nav.addNode(mgr.translate( "MATMAWNAVIGATORINSTALLREQUIREPLAN: Install Require Plan" ));
//      n_two.addItem("MATMAWNAVIGATORMATINSTALLREQPLAN: Mat Install Req Plan","matmaw/MatInstalReqPlan.page");
      
      ASPNavigatorNode n_sup = matmaw_nav.addNode(mgr.translate( "MATMAWNAVIGATORSUPERVISIONMANAGEMENT: Supervision Management" ));
      n_sup.addItem("MATMAWNAVIGATORMATSUPPROJECT: Mat Sup Project","matmaw/MatSupProject.page");
      n_sup.addItem("MATMAWNAVIGATORMATSUPIMP: Mat Sup Imp","matmaw/MatSupImp.page");
      n_sup.addItem("MATMAWNAVIGATORMATWITNESSINFO: Mat Witness Info","matmaw/MatWitnessInfo.page");
      n_sup.addItem("MATMAWNAVIGATORSUPEXCEPTION: Sup Exception","matmaw/SupException.page");
      n_sup.addItem("MATMAWNAVIGATORMATSUPDOCMAN: Mat Sup Doc Man","matmaw/MatSupDocManNav.page");
      
      ASPNavigatorNode m = matmaw_nav.addNode("MATMAWNAVIGATORSTORAGEMANAGEMENT: Storage Management");

      m.addItem("MATMAWNAVIGATORMATARRIVE: Mat Arrive", "matmaw/MatArrive.page");
      m.addItem("MATMAWNAVIGATORMATACCEPT: Mat Accept", "matmaw/MatAccept.page");
      m.addItem("MATMAWNAVIGATORMATSTENTRY: Mat St Entry", "matmaw/MatStEntry.page");
      m.addItem("MATMAWNAVIGATORMATENTRYLISTTOCONTRACT: Mat Entry List To Contract", "matmaw/MatEntryListToContract.page");
      m.addItem("MATMAWNAVIGATORMATSTCONENTRY: Mat St Con Entry", "matmaw/MatStConEntry.page");
      m.addItem("MATMAWNAVIGATORMATSTCONOUT: Mat St Con Out", "matmaw/MatStConOut.page");
      
      m.addItem("MATMAWNAVIGATORMATSTDELIVERYREQ: Mat St Delivery Req", "matmaw/MatStDeliveryReq.page");
      m.addItem("MATMAWNAVIGATORMATSTDELIVERYREQBILL: Mat St Delivery Req Bill", "matmaw/MatStDeliveryReqBill.page");
      m.addItem("MATMAWNAVIGATORMATSTCANCEL: Mat St Cancel", "matmaw/MatStCancel.page");    
      m.addItem("MATMAWNAVIGATORMATBORROW: Mat Borrow", "matmaw/MatBorrow.page");
      m.addItem("MATMAWNAVIGATORMATBORROWRETURN: Mat Borrow Return", "matmaw/MatBorrowReturn.page");
      
      m.addItem("MATMAWNAVIGATORMATSTORAGE: Mat Storage", "matmaw/MatStorage.page");
      m.addItem("MATMAWNAVIGATORMATMATBILL: Mat Bill", "matmaw/MatBill.page");
      m.addItem("MATMAWNAVIGATORMATMATDEALBILL: Mat Deal Bill", "matmaw/MatDealBill.page");
      m.addItem("MATMAWNAVIGATORMATMATRECEIPT: Mat Receipt", "matmaw/MatReceipt.page");
      return matmaw_nav;                                       
   }  
}
