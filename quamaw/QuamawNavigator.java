package ifs.quamaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPNavigatorNode;
import ifs.fnd.asp.Navigator;
import ifs.fnd.service.Util;
import ifs.hzwflw.util.HzConstants;

public class QuamawNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuamawNavigator");
   
   public ASPNavigatorNode add(ASPManager mgr)    
   {
      if(DEBUG) Util.debug("!!ifs.quamaw.QuamawNavigator!!");
      
      ASPNavigatorNode nod = mgr.newASPNavigatorNode("QUAMAWNAVIGATOR: Quality Management");
		   ASPNavigatorNode m = nod.addNode("QUAMAWNAVBASEDATA: Base Data");
		   m.addItem("QUAMAWNAVQUABASEDATA: Quality Base Data","quamaw/QuamanBaseData.page","PROJECT_TYPE");
 	
	   	nod.addItem("QUAMAWNAVQUASTANDARDLIBRARY: Quality Standard Library","quamaw/QuanlityStandard.page", "QUANLITY_STANDARD,QUANLITY_STANDARD_LINE" );
	   	nod.addItem("QUAMAWNAVQUAPLAN: Quality Plan","quamaw/QuanlityPlan.page", "QUANLITY_PLAN,QUANLITY_PLAN_LINE" );
	   	nod.addItem("QUAMAWNAVQUASUPERVISION: Quality Supervision","quamaw/QuaSupervision.page", "QUA_SUPERVISION" );
	   	nod.addItem("QUAMAWNAVQUACHECK: Quality Check","quamaw/QuaCheck.page", "QUA_CHECK" );
	   	
	   	
	   	nod.addItem("QUAPERSONNELMAN: Qua Personnel Man", "quamaw/QuaPersonnelMan.page");
	   	nod.addItem("QUACHECKNOTICE: Qua Check Notice", "quamaw/QuaCheckNotice.page");
	   	nod.addItem("QUAMAWQUANLITYCHECKSUM: Quanlity Check Sum","quamaw/QuanlityCheckSum.page");
	   	nod.addItem("QUAMAWIMPORTEXCELDATA: Quanlity Import Excel Data","quamaw/ImportExcelData.page");
	  ASPNavigatorNode n = nod.addNode("QUAMAWNAVQUAACCIDENT: Quality Accident");
	      n.addItem("QUAMAWNAVQUAACCIDENTREPORT: Quality Accident Report","quamaw/QuaAccident.page");
	      n.addItem("QUAMAWNAVQUAACCIDENTHANDLEPLAN: Quality Accident Handle Plan","quamaw/QuaAccidentHandlePlan.page");
	      n.addItem("QUAMAWNAVQUAACCIDENTHANDLERESULT: Quality Accident Handle Result","quamaw/QuaAccidentHandleResult.page");
      return nod;      
   }
}
