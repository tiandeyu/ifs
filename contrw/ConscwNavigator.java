/*
 *                 IFS Research & Development
 *
 *  This program is protected by copyright law and by international
 *  conventions. All licensing, renting, lending or copying (including
 *  for private use), and all other use of the program, which is not
 *  expressively permitted by IFS Research & Development (IFS), is a
 *  violation of the rights of IFS. Such violations will be reported to the
 *  appropriate authorities.
 *
 *  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 *  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 *  File        : ConscwNavigator.java
 *  Created     :
 *
 *
 *----------------------------------------------------------------------------
 */

package ifs.contrw;

import ifs.fnd.asp.*; 

public class ConscwNavigator implements Navigator
{
	public ASPNavigatorNode add(ASPManager mgr)
	{

		ASPNavigatorNode contract = mgr.newASPNavigatorNode("CONTRAWNAVIGATOR: Project Contract");	
		contract.addItem("NAV_CONTRACTCOUNTERSIGN: Project Contract Countersign", "contrw/ContractCountersign.page", "PROJECT_CONTRACT");
		contract.addItem("NAV_CONTRACTACCOUNT: Project Contract Account", "contrw/ProjectContract.page", "PROJECT_CONTRACT");
		contract.addItem("NAV_CONTRACTREPORTQTY: Contract Report Qty", "contrw/ContractReportQty.page", "PROJECT_CONTRACT"); 
		contract.addItem("NAV_CONTRACTQTYVISA: Contract Qty Visa", "contrw/ContractQtyVisa.page", "PROJECT_CONTRACT"); 
		contract.addItem("NAV_CONTRACTCONTACTREQ: Contract Contact Req", "contrw/ContractContactReq.page", "PROJECT_CONTRACT"); 
		//oth contract                 
		ASPNavigatorNode oth_contract = contract.addNode("OTHERCONTRACTNAVIGATOR: Other Contract");
		oth_contract.addItem("NAV_OTHERCONTRACTITEM: Other Contract Items", "contrw/ProjectContractOth.page", "PROJECT_CONTRACT");
      oth_contract.addItem("NAV_OTHERCONTRACTPAYMENTOTHER:  Other Contract Payment", "contrw/ContractPaymentOth.page", "PROJECT_CONTRACT");		
      oth_contract.addItem("NAV_OTHERCONTRACTVARIATIONOTHER:  Other Contract Variation", "contrw/ContractVariationOth.page", "PROJECT_CONTRACT");  	
      oth_contract.addItem("NAV_OTHERCONTRACTCOUNTERCLAIMOTHER:  Other Contract Counterclaim", "contrw/ContractCounterclaimOth.page", "PROJECT_CONTRACT");   
      //mat contract 
      ASPNavigatorNode mat_contract = contract.addNode("MATCONTRACTNAVIGATOR: Mat Contract");
      mat_contract.addItem("NAV_OTHERCONTRACTITEMMAT: Mat Contract Items", "contrw/ProjectContractMat.page", "PROJECT_CONTRACT");
      mat_contract.addItem("NAV_OTHERCONTRACTPAYMENTMAT:  Mat Contract Payment", "contrw/ContractPaymentMat.page", "PROJECT_CONTRACT");     
      mat_contract.addItem("NAV_OTHERCONTRACTVARIATIONMAT:  Mat Contract Variation", "contrw/ContractVariationMat.page", "PROJECT_CONTRACT");     
      mat_contract.addItem("NAV_OTHERCONTRACTCOUNTERCLAIMMAT:  Mat Contract Counterclaim", "contrw/ContractCounterclaimMat.page", "PROJECT_CONTRACT");  
                      
      //Schedule Contract
		ASPNavigatorNode con_contract = contract.addNode("CONSCWNAVIGATOR: Construction Contract");
		con_contract.addItem("NAV_SCHCONTRACTITEMS: Schedule Contract Items", "contrw/ProjectContractSch.page", "PROJECT_CONTRACT");
      con_contract.addItem("NAV_SCHCONTRACTPAYMENT: Schedule Contract Payment", "contrw/ContractPaymentSch.page", "PROJECT_CONTRACT");      
      con_contract.addItem("NAV_SCHCONTRACTVARIATION: Schedule Contract Variation", "contrw/ContractVariationSch.page", "PROJECT_CONTRACT");    
      con_contract.addItem("NAV_SCHCONTRACTCOUNTERCLAIM: Schedule Contract Counterclaim", "contrw/ContractCounterclaimSch.page", "PROJECT_CONTRACT");   
      //contract basic data           
	   ASPNavigatorNode contract_base = contract.addNode("CONTRWNAVIGATORBASE: Contract Base Data");
	   contract_base.addItem("NAV_CONTRACTBASE: Contract Base Data", "contrw/ContractBaseData.page", "CONTRACT_PURCH_TYPE");
	   contract_base.addItem("NAV_CONTRACTITEMIMP: Contract Item Import", "contrw/ImportExcelData.page?IMPTITLE=" + mgr.URLEncode(mgr.translate("NAV_CONTRACTITEMIMP: Contract Item Import")), "CONTRACT_PURCH_TYPE");
	   contract_base.addItem("NAV_CONTRACTACCESS: Contract Access", "conscw/ProjectContractAccess.page", "PROJECT_CONTRACT_ACCESS");
		return contract;    
	}
}    