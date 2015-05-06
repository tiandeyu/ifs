package ifs.pcmw;

import ifs.fnd.asp.*;

public class Pcmw3Navigator implements Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
     ASPNavigatorNode nod = mgr.newASPNavigatorNode("WOEQUIPANAL: Work Order Analysis");

      nod.addItem("WORORDCOAN: Work Order Cost Analysis","pcmw/FrameSetWorkOrderCost.page","WORK_ORDER");
      nod.addItem("WORORDCOREVANSM: Work Order Cost/Revenue Analysis SM","pcmw/WorkOrderCostAnalysisSM.page","WORK_ORDER");   
      nod.addItem("WOANALCOSTREV: Overview - WO Analysis of Cost and Revenue","pcmw/WorkOrder2Ovw.page","WORK_ORDER2");  
      nod.addItem("WORORDPOSTOVW: Overview - Work Order Postings","pcmw/WorkOrderPostingsOvw.page","WORK_ORDER_CODING");

      return nod;
   }

}