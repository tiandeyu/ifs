package ifs.pcmw;

import ifs.fnd.asp.*;

public class Pcmw2Navigator implements Navigator
{
	public ASPNavigatorNode add(ASPManager mgr)
	{
		ASPNavigatorNode nod = mgr.newASPNavigatorNode("WORKORDERMAN: Work Order Management");

		ASPNavigatorNode a = nod.addNode( "WOORDQUO: Work Order Quotation");

		a.addItem("WORKORDERQUOT: Prepare Work Order Quotation","pcmw/WorkOrderQuotation.page","WORK_ORDER_QUOTATION");
		a.addItem("FLWUPONWRKORDQUO: Follow up on Work Order Quotation","pcmw/WorkOrderQuotationWiz.page?WNDFLAG=WorkOrderQuotationWiz","WORK_ORDER_QUOTATION");
		a.addItem("WORORDQUO: Overview - Work Order Quotation","pcmw/WorkOrderQuotationOvw.page","WORK_ORDER_QUOTATION_OVERVIEW");

		ASPNavigatorNode p = nod.addNode( "FAUREPSERVICEREQ: Fault Report/Service Request");
		p.addItem("CRTFAULTREP: Create Fault Report","pcmw/ActiveSeparateWiz.page?WNDFLAG=ActiveSeparateWiz","ACTIVE_SEPARATE");
		p.addItem("FAULTREP: Fault Report","pcmw/ActiveSeparate.page","ACTIVE_SEPARATE");
		p.addItem("CRTSERREQ: Create Service Request","pcmw/ActiveSeparateWizB2E.page?WNDFLAG=ActiveSeparateWizB2E","ACTIVE_SEPARATE");
		p.addItem("SERREQ: Service Request ","pcmw/ActiveSeparate3.page","ACTIVE_SEPARATE");
		p.addItem("ACTWORKREQ: Overview - Active Work Requests","pcmw/ActiveSeparateOvw.page","ACTIVE_SEPARATE");

		ASPNavigatorNode q = nod.addNode("ACTWORKORD: Active Work Order");

		ASPNavigatorNode r = q.addNode("QCKREPTWRK: Quick Report Work");
		r.addItem("QCKREPHIST: Quick Report in Work Wizard","pcmw/QuickReportToHistory.page?WNDFLAG=QuickReportToHistory","HISTORICAL_SEPARATE");
		r.addItem("QCKREPINWSM: Quick Report in Work Wizard for SM","pcmw/QuickReportInIntroSM.page?WNDFLAG=QuickReportInIntroSM","HISTORICAL_SEPARATE");
		r.addItem("QCKREPINW: Quick Report in Work","pcmw/QuickReportInWorkDlg.page","HISTORICAL_SEPARATE");

		q.addItem("WOINFO: Work Order Information","pcmw/ActiveSeparate2light.page","ACTIVE_SEPARATE");
		q.addItem("WOINFOSM: Work Order Information SM","pcmw/ActiveSeparate2lightSM.page","ACTIVE_SEPARATE");
		q.addItem("PRPWRKORD: Prepare Work Order","pcmw/ActiveSeparate2.page","ACTIVE_SEPARATE");
		q.addItem("PRPWRKORDSERVMGT: Prepare Work Order for SM","pcmw/ActiveSeparate2ServiceManagement.page","ACTIVE_SEPARATE");
		q.addItem("REPAIRWORKORDER: Repair Work Orders","pcmw/ActiveSeparateRepairWorkOrders.page","ACTIVE_SEPARATE_REPAIR");
		q.addItem("CREREPWRKORD: Create Repair Work Order for Serial Object","pcmw/CreateRepairWorkOrderDlg.page?NEWWNDFLAG=TRUE","ACTIVE_SEPARATE");
		q.addItem("CREREPWRKORDFNONSEPART: Create Repair Work Order for Non Serial Part","pcmw/CreateRepairWorkOrderForNonSerialParts.page?NEWWNDFLAG=TRUE","ACTIVE_SEPARATE");
		q.addItem("REPORTWRKORD: Report In Work Order","pcmw/ActiveSeperateReportInWorkOrder.page","ACTIVE_SEPARATE");
		q.addItem("REPWRKORDSERVMGT: Report In Work Order for SM","pcmw/ActiveSeperateReportInWorkOrderSM.page","ACTIVE_SEPARATE");
		q.addItem("REPORTWRKORDWIZ: Report In Work Order Wizard","pcmw/ReportInWorkOrderWiz.page?WNDFLAG=ReportInWorkOrderWiz","ACTIVE_SEPARATE");
		q.addItem("WOTOCUS: Transfer To Customer Order","pcmw/ActiveWorkOrder3.page","ACTIVE_WORK_ORDER");
		q.addItem("WOORDSTRUCT: Work Order Structure","pcmw/NavigatorFrameSet.page","SEPARATE_WORK_ORDER");
		q.addItem("WOPERMITISO: Overview - Work Orders, Permits and Isolation Orders","pcmw/WOPermitIsoOrdersOvw.page","WO_PERMIT_ISO_ORDER");

		ASPNavigatorNode d = nod.addNode("ACTIVEROUND: Active Route");
		d.addItem("REPORTINROUND: Report In Route","pcmw/ActiveRound.page","ACTIVE_ROUND");

		ASPNavigatorNode v = nod.addNode("WORKORDSCH: Work Order Scheduling");
		v.addItem("DEPTRES: Maintenance Organization Resources","pcmw/DepartmentResources.page");
		v.addItem("EMPRES: Employee Resources","pcmw/EmployeeResources.page");
		v.addItem("OVWCRFT: Overview - Crafts","pcmw/WorkOrderRoleOvw.page","WORK_ORDER_ROLE_ACTIVE"); 
		v.addItem("SEARCHENG: Search Engine","pcmw/FreeTimeSearchEngine.page");

		ASPNavigatorNode y = nod.addNode("TIMEREP: Time Report");
		y.addItem("TIMEREPBYEMP: Time Report By Employee","pcmw/WorkOrderCoding.page","EMPLOYEE");

		ASPNavigatorNode s = nod.addNode( "INVTRANS: Inventory Transaction");
                s.addItem("MAINTMATREQOVW: Overview - Maintenance Material Requisition Lines","pcmw/MaintMaterialReqLineOvw.page","MAINT_MATERIAL_REQ_LINE");
		s.addItem("UNISSWO: Unissue of Inventory Part for Maint - Material Requisition","pcmw/ActiveWorkOrder.page","MAINT_MATERIAL_REQUISITION");
		s.addItem("ISSUEINVPART: Maint Material Requisition","pcmw/MaintMaterialRequisition.page","MAINT_MATERIAL_REQUISITION");

		ASPNavigatorNode x = nod.addNode( "WCLRPERM: Isolation Orders and Permits");
		x.addItem("DELORD: Isolation Order","pcmw/DelimitationOrderTab.page","DELIMITATION_ORDER");
		x.addItem("PERMI: Permit","pcmw/Permit.page","PERMIT"); 

		ASPNavigatorNode g = nod.addNode( "HISTWORKORD: Historical Work Order");
		g.addItem("HISTSEP: Historical Work Order","pcmw/HistoricalSeparateRMB.page","HISTORICAL_SEPARATE"); 
		g.addItem("HISTROUND: Historical Route Work Order","pcmw/HistoricalRound.page","HISTORICAL_ROUND"); 

                //Voucher folder, Start
		ASPNavigatorNode e = nod.addNode("VOUCH: Voucher");

		ASPNavigatorNode e1 = e.addNode("MAINTTRANS: Maintenance Transactions");
                e1.addItem("WOTRANSACTIONHIST: Work Order Transaction History","pcmw/WoTimeTransactionHist.page");
                e1.addItem("MAINTPOSTINGS: Query - Postings","pcmw/MaintPosting.page");
                e1.addItem("RERUNERRACC: Rerun Invalid Accountings ","pcmw/RedoPcmAccountingDlg.page");
                e1.addItem("TRANSFERMAINTTIMETRANS: Transfer Time Transactions","pcmw/TransferMaintTransactionDlg.page");
                e1.addItem("TRANSFERMAINTTOOLTRANS: Transfer Tool and Facilities Transactions","pcmw/TransferMaintToolTransactionDlg.page");

		ASPNavigatorNode e2 = e.addNode("CSS: Cost of Sold Services");
                e2.addItem("CRTCSSPOSTING: Create CSS Postings","pcmw/CreateCssPostingsDlg.page");
                e2.addItem("QRYCSSPOSTING: Query - CSS Postings","pcmw/CssPosting.page");
                e2.addItem("TRANSCSSTRANS: Transfer CSS Transactions","pcmw/TransferCssTransactionsDlg.page");
                //Voucher folder, End

		return nod;
	}
}
