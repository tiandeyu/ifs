/*  violation of the rights of IFS. Such violations will be reported to the
*  appropriate authorities.
*
*  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
*  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
* ----------------------------------------------------------------------------
*  File        : MpccowNavigator.java
*  Modified    :
*     Imfelk      -  30-Oct-2008 -  Bug 77272: Added Period Template Maintenance, Statistic Periods for Inventory and Distribution,
*                                   Characteristic Templates and node Characteristic Codes with sub items..
* ----------------------------------Defcon Above------------------------------------------
*     Cpeilk      -  26-Jun-2007 -  Added Modify Posting Cost Group window.
*     Cpeilk      -  20-Jun-2007 -  Added Order Cancellation Reasons, Rerun Erroneous Accountings, Codestring Completion windows.
* ---------------------- Wings Merge End -------------------------------------
*     SenSlk	  -  23-Apr-2007 -  Removed PostingEvents.Page from basic data for Inv & Dist.
*     SenSlk      -  20-Apr-2007 -  Added Financial Control & Overview Pages.
*     ChJalk      -  30-Jan-2007 -  Merged Wings Code.
*     ChJalk      -  08-Nov-2006 -  Added System Parameters and Posting Events. 
*     ChJalk      -  30-Oct-2006 -  Created.
* ---------------------- Wings Merge Start -----------------------------------
* ----------------------------------------------------------------------------
*/


package ifs.mpccow;
import ifs.fnd.asp.*;
import ifs.fnd.service.*;

public class MpccowNavigator implements Navigator {
	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.MpccowNavigator");

	public ASPNavigatorNode add(ASPManager mgr)
	{
		if (DEBUG) Util.debug("!!MpccowNavigator!!");

		ASPNavigatorNode nod = mgr.newASPNavigatorNode( "MPCCOWNAVIGATORGENDATAINVDIST: General Data for Inventory and Distribution" );

		nod.addItem((mgr.translate("MPCCOWNAVIGATORSITE: Site")), "mpccow/Site.page", "SITE");
		nod.addItem((mgr.translate("MPCCOWNAVIGATORPERTEMPMAINT: Period Template Maintenance")), "mpccow/PeriodTemplate.page", "PERIOD_TEMPLATE,PERIOD_TEMPLATE_DETAIL");
		nod.addItem((mgr.translate("MPCCOWNAVIGATORSTATPRDFORINVANDDST: Statistic Periods for Inventory and Distribution")), "mpccow/StatisticPeriodOvw.page", "STATISTIC_PERIOD");

		ASPNavigatorNode n = nod.addNode( mgr.translate("MPCCOWNAVIGATORSYSDATAINVDISTSYS: System Data for Inventory and Distribution"));
		n.addItem((mgr.translate("MPCCOWNAVIGATORUSERS: Users")), "mpccow/UsersDefaults.page", "USER_DEFAULT, USER_ALLOWED_SITE");
		n.addItem((mgr.translate("MPCCOWNAVIGATORSYSPARAMS: System Parameters")), "mpccow/SystemParameters.page", "MPCCOM_SYSTEM_PARAMETER_GYN");

		ASPNavigatorNode n1 = nod.addNode( mgr.translate("MPCCOWNAVIGATORBASICDATAINVDIST: Basic Data for Inventory and Distribution"));
		n1.addItem((mgr.translate("MPCCOWNAVIGATORCOORDINATORS: Coordinators")), "mpccow/Coordinators.page", "ORDER_COORDINATOR");
		n1.addItem((mgr.translate("MPCCOWNAVIGATORCOORDINATORGRP: Coordinator Groups")), "mpccow/CoordinatorGroups.page", "ORDER_COORDINATOR_GROUP");
		n1.addItem((mgr.translate("MPCCOWNAVIGATORCANCELREASONS: Order Cancellation Reasons")), "mpccow/CancellationReasonsOvw.page", "ORDER_CANCEL_REASON");
		n1.addItem((mgr.translate("MPCCOWNAVIGATORCHARTEMP: Characteristic Templates")), "mpccow/CharacteristicTemplateOvw.page", "CHARACTERISTIC_TEMPLATE,CHAR_TEMPL_INDISCRETE_CHAR,CHAR_TEMPL_DISCRETE_CHAR");

		ASPNavigatorNode n11 = n1.addNode( mgr.translate("MPCCOWNAVIGATORCHARCODE: Characteristic Codes"));      
		n11.addItem((mgr.translate("MPCCOWNAVIGATORVARIABLECHAR: Variable Characteristics")), "mpccow/IndiscreteCharacteristicOvw.page", "INDISCRETE_CHARACTERISTIC");
		n11.addItem((mgr.translate("MPCCOWNAVIGATORDISCRETECHAR: Discrete Characteristics")), "mpccow/DiscreteCharacteristic.page", "DISCRETE_CHARACTERISTIC,DISCRETE_CHARAC_VALUE");

		ASPNavigatorNode n2 = nod.addNode( mgr.translate("MPCCOWNAVIGATORBASICDATAFINCONT: Financial Control"));
		n2.addItem((mgr.translate("MPCCOWNAVIGATORINVDISTSYSEVENT: Inventory and Distribution System Event")), "mpccow/SystemEvent.page", "MPCCOM_SYSTEM_EVENT,MPCCOM_TRANSACTION_CODE,ACCOUNTING_EVENT,ACC_EVENT_POSTING_TYPE");
		n2.addItem((mgr.translate("MPCCOWNAVIGATORCODESTRINGCOMP: Codestring Completion")), "mpccow/CodestringCompletionDlg.page", "");
		n2.addItem((mgr.translate("MPCCOWNAVIGATORRETURNERRONEOUSACC: Rerun Erroneous Accountings")), "mpccow/RerunErroneousAccountingsDlg.page", "");
		n2.addItem((mgr.translate("MPCCOWNAVIGATORSYSEVENTOVW: Query - Inventory and Distribution System Events")), "mpccow/SystemEventOvw.page", "MPCCOM_SYSTEM_EVENT_ALL");
		n2.addItem((mgr.translate("MPCCOWNAVIGATORPOSTINGTYPSYSEVENTS: Query - Posting Types per System Event")), "mpccow/PostingTypesPerSysEventsOvw.page", "ACC_EVENT_POSTING_TYPE_ALL");

		ASPNavigatorNode n3 = nod.addNode( mgr.translate("MPCCOWNAVIGATORBASICDATACOSTDET: Cost Details"));
		n3.addItem((mgr.translate("MPCCOWNAVIGATORBASICDATAFORCOSTDETAILS: Basic Data For Cost Details")), "mpccow/CostDetailsBasicData.page", "COMPANY_DISTRIBUTION_INFO,COST_SOURCE,COST_SOURCE_OVERHEAD_RATE,COST_TYPE_SOURCE_INDICATOR");
		n3.addItem((mgr.translate("MPCCOWNAVIGATORMODIFYPOSTINGCOSTGROUP: Modify Posting Cost Group")), "mpccow/ModifyPostingCostGroup.page", "SITE,COST_BUCKET");

		return nod;

	}
}